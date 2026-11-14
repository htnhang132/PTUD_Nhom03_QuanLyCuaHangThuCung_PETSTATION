package com.petstation.service.banhang;

import com.petstation.dao.banhang.PhieuDoiTraDAO;
import com.petstation.dao.banhang.impl.PhieuDoiTraDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.GioHang;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DoiTraService {
    private static final Logger LOG = LoggerFactory.getLogger(DoiTraService.class);
    private final PhieuDoiTraDAO doiTraDAO;

    public DoiTraService() {
        this(new PhieuDoiTraDAOImpl());
    }

    public DoiTraService(PhieuDoiTraDAO doiTraDAO) {
        this.doiTraDAO = Objects.requireNonNull(doiTraDAO, "doiTraDAO không được null");
    }

    public void apDungDoiTra(String maPhieu) {
        throw new IllegalArgumentException(
                "Phiếu hoàn tiền lịch sử chỉ được tra cứu; không áp dụng thành giao dịch mới.");
    }

    static void kiemTraHanDoi(LocalDateTime ngayLap, LocalDateTime hienTai) {
        int soGio = 72;
        try {
            soGio = Math.max(1, CaiDatService.macDinh().laySoNgayDuocDoiTra()) * 24;
        } catch (RuntimeException ex) {
            LOG.warn("Không đọc được cấu hình số ngày đổi trả, dùng mặc định 72 giờ.", ex);
        }
        if (ngayLap == null
                || hienTai.isBefore(ngayLap)
                || hienTai.isAfter(ngayLap.plusHours(soGio))) {
            throw new IllegalArgumentException(
                    "Chỉ được đổi trong " + soGio + " giờ kể từ ngày lập hóa đơn.");
        }
    }

    private static final class DuLieuGoc {
        final ChiTietHoaDon goc;
        final long giaTri;

        private DuLieuGoc(ChiTietHoaDon goc, long giaTri) {
            this.goc = goc;
            this.giaTri = giaTri;
        }
    }

    private static long phanBo(long soLuong, ChiTietHoaDon goc) {
        HoaDon hd = goc.getHoaDon();
        if (hd == null || hd.getTamTinh() <= 0)
            throw new IllegalArgumentException("Hóa đơn gốc thiếu tạm tính để phân bổ giảm giá.");
        return BigDecimal.valueOf(soLuong)
                .multiply(BigDecimal.valueOf(goc.getDonGia()))
                .multiply(BigDecimal.valueOf(hd.getTongTien()))
                .divide(BigDecimal.valueOf(hd.getTamTinh()), 0, RoundingMode.DOWN)
                .longValueExact();
    }

    private static String[] tachMaDong(String maDong) {
        if (maDong == null || maDong.isBlank()) {
            throw new IllegalArgumentException("Chưa chọn dòng hàng hợp lệ.");
        }
        int sep = maDong.indexOf(':');
        if (sep <= 0 || sep >= maDong.length() - 1) {
            throw new IllegalArgumentException("Mã dòng hàng không hợp lệ.");
        }
        return new String[] {maDong.substring(0, sep), maDong.substring(sep + 1)};
    }

    private DuLieuGoc kiemTra(String maDong, int soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng đổi phải dương.");
        String[] phan = tachMaDong(maDong);
        Optional<ChiTietHoaDon> dongTim = doiTraDAO.timDongGoc(phan[0], phan[1]);
        if (dongTim.isEmpty()
                || dongTim.get().getHoaDon() == null
                || dongTim.get().getHoaDon().getTrangThaiEnum() == null
                || !dongTim.get().getHoaDon().getTrangThaiEnum().daHoanTat())
            throw new IllegalArgumentException("Hóa đơn gốc không hợp lệ.");
        ChiTietHoaDon goc = dongTim.get();
        HoaDon hd = goc.getHoaDon();
        if (goc.getSanPham() == null || !"Phụ kiện".equals(goc.getSanPham().getNhomHang()))
            throw new IllegalArgumentException("Chỉ được đổi hàng thuộc nhóm Phụ kiện.");
        if (doiTraDAO.laHoaDonThayThe(hd.getMaHoaDon()))
            throw new IllegalArgumentException("Hàng thay thế đã đổi không được đổi tiếp.");
        LocalDateTime ngayLap = hd.getThanhToanLuc();
        kiemTraHanDoi(ngayLap, LocalDateTime.now());
        long daDoi = doiTraDAO.demDoiTraMuc(phan[0], phan[1]);
        if (daDoi + soLuong > goc.getSoLuong())
            throw new IllegalArgumentException("Số lượng đổi vượt số đã mua chưa đổi.");
        return new DuLieuGoc(goc, phanBo(daDoi + soLuong, goc) - phanBo(daDoi, goc));
    }

    public List<Object[]> timHoaDon(String tuKhoa) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        if (tuKhoa == null || tuKhoa.isBlank()) {
            throw new IllegalArgumentException("Nhập mã hóa đơn hoặc SĐT.");
        }
        return doiTraDAO.timHoaDon(tuKhoa.trim()).stream()
                .map(
                        hd ->
                                new Object[] {
                                    hd.getMaHoaDon(),
                                    hd.getMaHoaDon(),
                                    hd.getKhachHang() != null
                                            ? hd.getKhachHang().getHoTen()
                                            : "Khách lẻ",
                                    hd.getTaoLuc(),
                                    hd.getTongTien()
                                })
                .toList();
    }

    public List<Object[]> layDongHoaDon(String maHoaDon) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        return doiTraDAO.layDongHoaDon(maHoaDon).stream()
                .map(
                        ctDt -> {
                            ChiTietHoaDon ctHd = ctDt.getChiTietHoaDon();
                            SanPham sp = ctHd == null ? null : ctHd.getSanPham();
                            String maSp =
                                    sp == null || sp.getMaSanPham() == null
                                            ? ""
                                            : sp.getMaSanPham();
                            String maDong = maHoaDon + ":" + maSp;
                            return new Object[] {
                                maDong,
                                sp != null ? sp.getTenSanPham() : null,
                                sp != null ? sp.getNhomHang() : null,
                                ctHd != null ? ctHd.getSoLuong() : 0,
                                ctDt.getSoLuong(),
                                ctHd != null ? ctHd.getDonGia() : 0L
                            };
                        })
                .toList();
    }

    public List<Object[]> timPhuKien(String tuKhoa) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        return doiTraDAO.timPhuKien(tuKhoa == null ? "" : tuKhoa.trim()).stream()
                .map(
                        sp ->
                                new Object[] {
                                    sp.getMaSanPham(),
                                    sp.getTenSanPham(),
                                    sp.getGia(),
                                    sp.tinhTongTonKhoKhaDung()
                                })
                .toList();
    }

    public long layGiaTriDuocDoi(String maDong, int soLuong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        return QuanLyGiaoDich.macDinh().thucThi(c -> kiemTra(maDong, soLuong).giaTri);
    }

    public void lapDoiHang(
            GioHang gio,
            String maDong,
            int soLuong,
            String maThayThe,
            int soLuongMoi,
            String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        if (!gio.kiemTraTrong())
            throw new IllegalArgumentException("Hãy thanh toán hoặc treo giỏ hiện tại trước.");
        if (soLuongMoi <= 0 || lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Cần số lượng thay thế và lý do đổi.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            DuLieuGoc dl = kiemTra(maDong, soLuong);
                            Optional<SanPham> sp = doiTraDAO.timThayThe(maThayThe);
                            if (sp.isEmpty() || !"Phụ kiện".equals(sp.get().getNhomHang()))
                                throw new IllegalArgumentException(
                                        "Hàng thay thế phải là phụ kiện đang kinh doanh.");
                            long gia = sp.get().getGia();
                            if (Math.multiplyExact(gia, soLuongMoi) < dl.giaTri)
                                throw new IllegalArgumentException(
                                        "Giá trị hàng thay thế không được thấp hơn giá trị được"
                                                + " đổi.");
                            gio.xoa();
                            gio.setCacDong(
                                    List.of(
                                            new GioHang.DongHang(
                                                    maThayThe,
                                                    sp.get().getTenSanPham(),
                                                    gia,
                                                    soLuongMoi)));
                            KhachHang kh = dl.goc.getHoaDon().getKhachHang();
                            String maKh =
                                    kh == null || kh.getMaKhachHang() == null
                                            ? ""
                                            : kh.getMaKhachHang();
                            String hoTen = kh == null || kh.getHoTen() == null ? "" : kh.getHoTen();
                            long diem =
                                    kh == null || kh.getDiemTichLuy() == null
                                            ? 0L
                                            : kh.getDiemTichLuy();
                            gio.setKhachHang(maKh, hoTen, diem);
                            gio.setNguon(LoaiPhieuDoiTra.DOI_HANG.maDB(), maDong);
                            gio.setDoiHang(
                                    new GioHang.YeuCauDoiHang(maDong, soLuong, lyDo, dl.giaTri));
                            return null;
                        });
    }

    String chuanBi(GioHang gio) {
        var yc = gio.getDoiHang();
        if (yc == null || yc.getLyDo().isBlank())
            throw new IllegalArgumentException("Thiếu thông tin đổi hàng.");
        String[] phan = tachMaDong(yc.getDongGoc());
        DuLieuGoc dl = kiemTra(yc.getDongGoc(), yc.getSoLuong());
        KhachHang kh = dl.goc.getHoaDon().getKhachHang();
        String maKh = kh == null || kh.getMaKhachHang() == null ? "" : kh.getMaKhachHang();
        if (dl.giaTri != yc.getGiaTriDuocDoi()
                || !maKh.equals(gio.getMaKhachHang())
                || gio.tinhTong() < 0
                || !gio.getMaVoucher().isBlank()
                || gio.getGiamGiaPhanTram() != 0)
            throw new IllegalArgumentException(
                    "Thông tin đổi hàng đã thay đổi; hãy lập lại phiếu.");
        for (var dong : gio.getCacDong()) {
            Optional<SanPham> sp = doiTraDAO.timThayThe(dong.getMa());
            if (sp.isEmpty() || !"Phụ kiện".equals(sp.get().getNhomHang()))
                throw new IllegalArgumentException("Chỉ chọn phụ kiện thay thế.");
        }
        String maPhieu =
                doiTraDAO.taoPhieu(
                        dl.goc.getHoaDon().getMaHoaDon(),
                        PhienDangNhapService.macDinh().layMaNhanVien(),
                        gio.tinhTong(),
                        yc.getLyDo(),
                        dl.giaTri,
                        gio.getKhoaThanhToan());
        int con = yc.getSoLuong();
        long tienCon = dl.giaTri;
        for (LoHang lo : doiTraDAO.laySoLuongLoConDuocDoi(phan[0], phan[1])) {
            int luong = Math.min(con, lo.getSoLuongTon());
            if (luong <= 0) continue;
            long tien = luong == con ? tienCon : dl.giaTri * luong / yc.getSoLuong();
            doiTraDAO.capNhatDoiTraMuc(maPhieu, phan[0], phan[1], lo.getMaLo(), luong, tien);
            doiTraDAO.capNhatApDungDoiTra(luong, lo.getMaLo());
            con -= luong;
            tienCon -= tien;
            if (con == 0) break;
        }
        if (con != 0) throw new IllegalArgumentException("Thiếu phân bổ lô hàng gốc.");
        return maPhieu;
    }

    public static long tinhDiemChenhLech(long tienThucTra) {
        if (tienThucTra < 0) {
            throw new IllegalArgumentException("Tiền chênh lệch không được âm.");
        }
        long tyLe = 10_000L;
        try {
            tyLe = Math.max(1L, CaiDatService.macDinh().laySoTienChoMoiDiemTichLuy());
        } catch (RuntimeException ex) {
            LOG.warn(
                    "Không đọc được cấu hình quy đổi điểm khi đổi trả, dùng mặc định 10.000₫.", ex);
        }
        return tienThucTra / tyLe;
    }

    static long tinhDiemCong(GioHang gio) {
        return tinhDiemChenhLech(gio.tinhTong());
    }

    public void hoanTat(String maPhieu, String maHoaDon) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.THANH_TOAN);
        doiTraDAO.hoanTat(maPhieu, maHoaDon);
        NhatKyHeThongService.macDinh()
                .ghiNhatKy("Đổi phụ kiện", maPhieu + " / hóa đơn chênh lệch " + maHoaDon);
    }
}
