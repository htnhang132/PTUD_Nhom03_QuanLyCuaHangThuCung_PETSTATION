package com.petstation.gui.luutru;

import static com.petstation.gui.chung.TruongNghiepVu.docSoTien;
import static com.petstation.gui.chung.TruongNghiepVu.layTruongVanBan;

import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.gui.banhang.ThuTienDialog;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.luutru.LuuTruService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class NghiepVuHotelPanel extends BangNghiepVuPanel {
    // 1. THUỘC TÍNH & HÀM KHỞI TẠO

    protected final LuuTruService luuTruService = new LuuTruService();
    private final BanHangService banHangService = new BanHangService();

    protected NghiepVuHotelPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    // 2. THAO TÁC PHÒNG, ĐẶT PHÒNG & HỦY ĐẶT

    protected void lichSuCoc() {
        lichSuCocChoHopDong(layDongChon());
    }

    public void lichSuCocChoHopDong(String ma) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layCoc(ma),
                dsCoc -> {
                    List<Object[]> dong = new ArrayList<>();
                    for (HotelDTO.DongLichSuCoc c : dsCoc) {
                        dong.add(
                                new Object[] {
                                    c.getPhuongThuc(),
                                    c.getSoTien(),
                                    c.getKhachDua(),
                                    c.getTienThua(),
                                    c.getTaoLuc(),
                                    c.getCa()
                                });
                    }
                    hienThiChiTiet(
                            "Lịch sử thu cọc",
                            new String[] {
                                "Phương thức",
                                "Thực thu",
                                "Khách đưa",
                                "Tiền thừa",
                                "Lúc nhận",
                                "Ca"
                            },
                            dong);
                });
    }

    public void moDonPhong(String maPhong) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                luuTruService::layPhongDangDon,
                dsPhong -> hienThiHopThoaiDonPhong(maPhong, dsPhong));
    }

    public void moDatPhongTheoPhong(String maPhong) {
        if (maPhong != null && !maPhong.isBlank()) {
            layBanHang().getQuyTrinh().chonPhongHotel(maPhong);
        }
        datPhong();
    }

    private void hienThiHopThoaiDonPhong(String maPhong, List<PhongHotel> dsPhong) {
        if (dsPhong.isEmpty()) {
            HopThoai.hienThiThongTin(
                    this, "Hiện không có phòng nào đang ở trạng thái chờ dọn vệ sinh.");
            return;
        }
        List<Object[]> cacPhongDangDon = new ArrayList<>();
        for (PhongHotel p : dsPhong) {
            cacPhongDangDon.add(new Object[] {p.getMaPhong(), p.getMaPhong()});
        }
        var field =
                BieuMauTruong.TruongDuLieu.layLuaChon("room", "Phòng đang dọn", cacPhongDangDon);
        if (maPhong != null && !maPhong.isBlank())
            field =
                    new BieuMauTruong.TruongDuLieu(
                            field.khoa(), field.nhan(), maPhong, field.cacLuaChon());
        var values = BieuMauTruong.layYeuCauNhap(this, "Hoàn thành dọn phòng", field);
        if (values == null) return;
        String phongChon = values.get("room");
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.hoanTatDonPhong(phongChon),
                () -> {
                    taiLaiNen();
                    HopThoai.hienThiThanhCong(
                            this,
                            "Đã xác nhận dọn phòng xong, phòng đã chuyển sang trạng thái Trống.");
                });
    }

    protected long cocConThieu() {
        if (layBangDuLieu().getSelectedRow() < 0 || layMoHinh().getColumnCount() < 11)
            return Long.MAX_VALUE;
        int row = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        return ((Number) layMoHinh().getValueAt(row, 10)).longValue()
                - ((Number) layMoHinh().getValueAt(row, 7)).longValue();
    }

    protected long cocDaThu() {
        if (layBangDuLieu().getSelectedRow() < 0 || layMoHinh().getColumnCount() < 8)
            return Long.MAX_VALUE;
        int row = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        Object value = layMoHinh().getValueAt(row, 7);
        return value instanceof Number n ? n.longValue() : 0;
    }

    protected boolean datPhong() {
        String maPhong = layBanHang().getQuyTrinh().getMaPhongHotel();
        String maHopDong = DatPhongHotelDialog.mo(this, null, maPhong);
        if (maHopDong != null) {
            layBanHang().getQuyTrinh().chonHotel(maHopDong);
            return true;
        }
        return false;
    }

    protected void suaDatPhong() {
        String maHopDong = layDongChon();
        String result = DatPhongHotelDialog.mo(this, maHopDong, null);
        if (result != null) layBanHang().getQuyTrinh().chonHotel(result);
    }

    protected void huyDatPhong() {
        huyDatPhongChoHopDong(layDongChon(), this::taiLaiNen);
    }

    public void huyDatPhongChoHopDong(String maHopDong, Runnable sauKhiHuy) {
        String reason =
                HopThoai.nhapChuoiThuan(
                        this,
                        "Nhập lý do hủy hợp đồng đặt phòng #"
                                + maHopDong
                                + ":\n(Chỉ có thể hủy trực tiếp khi hợp đồng chưa thu cọc.)",
                        "Hủy đặt phòng #" + maHopDong);
        if (reason == null) return;
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.huyDatPhong(maHopDong, reason),
                () -> {
                    layBanHang().getQuyTrinh().chonHotel("");
                    if (sauKhiHuy != null) sauKhiHuy.run();
                });
    }

    // 3. THU CỌC & ĐỐI SOÁT THANH TOÁN CỌC

    protected void thuCoc() {
        thuCocChoHopDong(layDongChon(), layNoiDungDongChon(1), null);
    }

    public void thuCocChoHopDong(String ma, String maHopDongHienThi, Runnable sauKhiXong) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> CaThuNganService.caDangMo() ? luuTruService.layMucCoc(ma) : null,
                muc -> {
                    if (muc == null) {
                        hienThiCanhBaoMoCa(
                                "Ca thu ngân hiện chưa mở. Cần mở ca để ghi nhận phiếu thu tiền"
                                        + " cọc.");
                        return;
                    }
                    nhapVaThuCoc(ma, maHopDongHienThi, muc, sauKhiXong);
                });
    }

    private void hienThiCanhBaoMoCa(String thongDiep) {
        if (HopThoai.xacNhanDongY(
                this,
                thongDiep
                        + "\n"
                        + "Bạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay bây"
                        + " giờ không?",
                "Chưa mở ca làm việc")) {
            layDieuHuong().accept(MaManHinh.CA_THU_NGAN);
        }
    }

    private void nhapVaThuCoc(String ma, String maHopDong, long[] muc, Runnable sauKhiXong) {
        long thieu = muc[0] - muc[1];
        if (thieu <= 0) {
            HopThoai.hienThiThongTin(this, "Hợp đồng đã thu đủ tiền cọc yêu cầu.");
            return;
        }
        ThuTienDialog.KetQuaThu khoanThu = ThuTienDialog.nhap(this, thieu, maHopDong);
        if (khoanThu == null) return;
        String khoa = "COC-" + UUID.randomUUID();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () ->
                        luuTruService.thuCoc(
                                ma,
                                khoanThu.phuongThuc(),
                                khoanThu.soTien(),
                                khoanThu.khachDua(),
                                khoa),
                () -> {
                    lamMoi();
                    if (sauKhiXong != null) sauKhiXong.run();
                    HopThoai.hienThiThanhCong(this, "Đã ghi nhận tiền cọc.");
                });
    }

    protected void doiCoc() {
        doiCocChoHopDong(layDongChon(), this::taiLaiNen);
    }

    public void doiCocChoHopDong(String ma, Runnable sauKhiDoi) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layMucCoc(ma)[0],
                mucHienTai -> {
                    Map<String, String> v =
                            BieuMauTruong.layYeuCauNhap(
                                    this,
                                    "Điều chỉnh cọc",
                                    layTruongVanBan(
                                            "muc",
                                            "Mức yêu cầu mới (đồng)",
                                            String.valueOf(mucHienTai)),
                                    layTruongVanBan("lydo", "Lý do", ""));
                    if (v == null) return;
                    long mucMoi = docSoTien(v, "muc");
                    String lyDo = v.get("lydo");
                    TacVuNen.chayCoPhien(
                            this,
                            () -> true,
                            () -> luuTruService.dieuChinhCoc(ma, mucMoi, lyDo),
                            () -> {
                                if (sauKhiDoi != null) sauKhiDoi.run();
                            });
                });
    }

    protected void layNhatKyChiTiet() {
        layNhatKyChiTietChoHopDong(layDongChon());
    }

    public void layNhatKyChiTietChoHopDong(String ma) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layNhatKyChiTiet(ma),
                dsNhatKy -> {
                    List<Object[]> dong = new ArrayList<>();
                    for (HotelDTO.DongNhatKyChiTiet n : dsNhatKy) {
                        dong.add(
                                new Object[] {
                                    n.getThoiGian(),
                                    n.getNhanVien() != null ? n.getNhanVien().getHoTen() : "",
                                    n.getAnUong(),
                                    n.getDaThucHien(),
                                    n.getTinhTrang()
                                });
                    }
                    hienThiChiTiet(
                            "Nhật ký lưu trú",
                            new String[] {
                                "Thời gian", "Nhân viên", "Ăn uống", "Đã thực hiện", "Tình trạng"
                            },
                            dong);
                });
    }

    protected void quyetToan(String loai) {
        quyetToanChoHopDong(loai, layDongChon());
    }

    public void quyetToanChoHopDong(String loai, String ma) {
        if (!layBanHang().kiemTraTrong()) {
            if (HopThoai.xacNhanDongY(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\n"
                            + "Bạn có muốn làm trống giỏ hàng để chuyển sang quyết toán lưu trú"
                            + " không?",
                    "Giỏ hàng đang có sản phẩm")) {
                layBanHang().xoa();
            } else {
                return;
            }
        }
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> banHangService.quyetToan(loai, ma, layBanHang()),
                () -> layDieuHuong().accept(MaManHinh.THANH_TOAN));
    }
}
