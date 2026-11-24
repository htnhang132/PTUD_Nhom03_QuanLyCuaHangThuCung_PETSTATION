package com.petstation.service.banhang;

import com.petstation.dao.banhang.PhienLamViecDAO;
import com.petstation.dao.banhang.impl.PhienLamViecDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.TomTatCaLamViec;
import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.chung.PhatSuKien;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public final class CaThuNganService {
    // 1. INSTANCE MẶC ĐỊNH, PHỤ THUỘC & HÀM KHỞI TẠO

    private static final CaThuNganService MAC_DINH = new CaThuNganService();

    public static CaThuNganService macDinh() {
        return MAC_DINH;
    }

    private final PhienLamViecDAO caThuNganDAO;

    public CaThuNganService() {
        this(new PhienLamViecDAOImpl());
    }

    public CaThuNganService(PhienLamViecDAO caThuNganDAO) {
        this.caThuNganDAO = Objects.requireNonNull(caThuNganDAO);
    }

    private static final long THOI_HAN_DEM_MS = 1500L;

    private final PhatSuKien<CaThuNganService> phatSuKien = new PhatSuKien<>();

    private String tieuDeDangCho = "", cuoiCungBanGiao = "";
    private volatile String maNhanVienDem;
    private volatile PhienLamViec phienDem;
    private volatile long thoiDiemDemMs;

    public void xoaBoDem() {
        maNhanVienDem = null;
        phienDem = null;
        thoiDiemDemMs = 0L;
    }

    private PhienLamViec layHienTai() {
        if (PhienDangNhapService.macDinh().layPhienHienTai() == null) {
            xoaBoDem();
            return null;
        }
        String maNv = PhienDangNhapService.macDinh().layMaNhanVien();
        if (QuanLyGiaoDich.dangTrongGiaoDich()) {
            return caThuNganDAO.timHienTai(maNv).orElse(null);
        }
        long bayGio = System.currentTimeMillis();
        if (Objects.equals(maNv, maNhanVienDem) && bayGio - thoiDiemDemMs < THOI_HAN_DEM_MS) {
            return phienDem;
        }
        PhienLamViec kq = caThuNganDAO.timHienTai(maNv).orElse(null);
        maNhanVienDem = maNv;
        phienDem = kq;
        thoiDiemDemMs = System.currentTimeMillis();
        return kq;
    }

    // 2. TRUY VẤN CA HIỆN TẠI & DOANH THU

    public String layMaPhien() {
        PhienLamViec phien = layHienTai();
        return phien == null || phien.getMaPhien() == null ? "" : phien.getMaPhien();
    }

    public boolean kiemTraMo() {
        return layHienTai() != null;
    }

    public static boolean caDangMo() {
        return MAC_DINH.kiemTraMo();
    }

    public String layTenCa() {
        PhienLamViec phien = layHienTai();
        return phien == null || phien.getTenCa() == null ? "" : phien.getTenCa();
    }

    public long layTienDauCa() {
        PhienLamViec phien = layHienTai();
        return phien == null ? 0 : phien.getTienDauCa();
    }

    private long layDoanhThu(PhuongThuc phuongThuc) {
        return caThuNganDAO.demBanHang(layMaPhien(), phuongThuc);
    }

    public long layDoanhThuTienMat() {
        return layDoanhThu(PhuongThuc.TIEN_MAT);
    }

    public long layDoanhThuChuyenKhoan() {
        return layDoanhThu(PhuongThuc.CHUYEN_KHOAN);
    }

    public long layDoanhThuThe() {
        return layDoanhThu(PhuongThuc.THE_NGAN_HANG);
    }

    public int demHoaDon() {
        return (int) caThuNganDAO.demSoLuongHoaDon(layMaPhien());
    }

    public long layCocHotel(PhuongThuc pt) {
        return caThuNganDAO.layCocHotel(layMaPhien(), pt.name());
    }

    public long layTongCocHotel() {
        return layCocHotel(PhuongThuc.TIEN_MAT)
                + layCocHotel(PhuongThuc.CHUYEN_KHOAN)
                + layCocHotel(PhuongThuc.THE_NGAN_HANG);
    }

    public long layTienMatDuKien() {
        return layTienDauCa()
                + layDoanhThuTienMat()
                + layTienCocHotel()
                - caThuNganDAO.demDuKienTienMat(layMaPhien());
    }

    public long layTienCocHotel() {
        return caThuNganDAO.layTongCocDaNhan(layMaPhien());
    }

    public String layNhan() {
        return kiemTraMo() ? "Đang mở – " + layTenCa() : "Chưa mở ca";
    }

    public void datTieuDeDangCho(String tieuDe) {
        tieuDeDangCho = tieuDe == null ? "" : tieuDe;
    }

    // 3. MỞ CA, ĐÓNG CA & BÀN GIAO THU NGÂN

    public void mo(String ten, long soTien) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_CA_THU_NGAN);
        if (ten == null || ten.isBlank()) throw new IllegalArgumentException("Chưa chọn tên ca.");
        if (soTien < 0) throw new IllegalArgumentException("Tiền đầu ca phải không âm.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (kiemTraMo())
                                throw new IllegalStateException("Tài khoản đã có ca đang mở.");
                            caThuNganDAO.capNhatMo(
                                    PhienDangNhapService.macDinh().layMaNhanVien(), ten, soTien);
                            NhatKyHeThongService.macDinh().ghiNhatKy("Mở ca", ten);
                            return null;
                        });
        thongBao();
    }

    public void ghiNhanBanHang(PhuongThuc phuongThuc, long soTien) {
        xoaBoDem();
        thongBao();
    }

    public void dong(long daDem, String ghiChu) {
        dong(daDem, ghiChu, HinhThucBanGiao.NOP_QUY, null);
    }

    public enum HinhThucBanGiao {
        NOP_QUY,
        BAN_GIAO
    }

    public static final class NguoiNhan {
        private final NhanVien nhanVien;

        public NguoiNhan(NhanVien nhanVien) {
            this.nhanVien = nhanVien;
        }

        public NguoiNhan(String hoTen, String maNhanVien) {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(maNhanVien);
            nv.setHoTen(hoTen);
            this.nhanVien = nv;
        }

        public String maNhanVien() {
            return nhanVien != null && nhanVien.getMaNhanVien() != null
                    ? nhanVien.getMaNhanVien()
                    : "";
        }

        @Override
        public String toString() {
            if (nhanVien == null) return "";
            String hoTen = nhanVien.getHoTen() != null ? nhanVien.getHoTen() : "";
            String ma = maNhanVien();
            return ma.isBlank() ? hoTen : hoTen + " (" + ma + ")";
        }
    }

    public TomTatCaLamViec layTomTatVaNguoiNhan() {
        xoaBoDem();
        PhienLamViec phien = layHienTai();
        if (phien == null) {
            return new TomTatCaLamViec(
                    false, "", null, 0L, 0L, 0L, 0L, 0L, cuoiCungBanGiao, tieuDeDangCho, List.of());
        }
        String maPhien = phien.getMaPhien() == null ? "" : phien.getMaPhien();
        long tienDauCa = phien.getTienDauCa();
        long doanhThuTm = caThuNganDAO.demBanHang(maPhien, PhuongThuc.TIEN_MAT);
        long doanhThuCkVaThe =
                caThuNganDAO.demBanHang(maPhien, PhuongThuc.CHUYEN_KHOAN)
                        + caThuNganDAO.demBanHang(maPhien, PhuongThuc.THE_NGAN_HANG)
                        + caThuNganDAO.layCocHotel(maPhien, PhuongThuc.CHUYEN_KHOAN.name())
                        + caThuNganDAO.layCocHotel(maPhien, PhuongThuc.THE_NGAN_HANG.name());
        long cocHotelTm = caThuNganDAO.layTongCocDaNhan(maPhien);
        long duKien = tienDauCa + doanhThuTm + cocHotelTm - caThuNganDAO.demDuKienTienMat(maPhien);
        List<NhanVien> nguoiNhan = List.of();
        if (PhienDangNhapService.macDinh().layPhienHienTai() != null
                && PhienDangNhapService.macDinh().layPhien().kiemTraCo(Quyen.QUAN_LY_CA_THU_NGAN)) {
            nguoiNhan = caThuNganDAO.layNguoiNhan(PhienDangNhapService.macDinh().layMaNhanVien());
        }
        LocalTime gioMo =
                phien.getMoLuc() == null ? null : phien.getMoLuc().toLocalTime().withNano(0);
        return new TomTatCaLamViec(
                true,
                phien.getTenCa(),
                gioMo,
                tienDauCa,
                doanhThuTm,
                doanhThuCkVaThe,
                cocHotelTm,
                duKien,
                cuoiCungBanGiao,
                tieuDeDangCho,
                nguoiNhan);
    }

    public List<NguoiNhan> layNguoiNhan() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_CA_THU_NGAN);
        return caThuNganDAO.layNguoiNhan(PhienDangNhapService.macDinh().layMaNhanVien()).stream()
                .map(NguoiNhan::new)
                .toList();
    }

    public void dong(long daDem, String ghiChu, HinhThucBanGiao hinhThuc, String nguoiNhan) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_CA_THU_NGAN);
        Objects.requireNonNull(hinhThuc, "Chưa chọn hình thức bàn giao.");
        if (hinhThuc == HinhThucBanGiao.NOP_QUY && nguoiNhan != null && !nguoiNhan.isBlank())
            throw new IllegalArgumentException("Nộp quỹ không có người nhận ca.");
        if (daDem < 0) throw new IllegalArgumentException("Tiền thực đếm phải không âm.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            String maPhienHienTai = layMaPhien();
                            if (maPhienHienTai.isBlank())
                                throw new IllegalStateException("Chưa mở ca.");
                            caThuNganDAO.demDong(maPhienHienTai);
                            long duKien = layTienMatDuKien();
                            if (daDem != duKien && (ghiChu == null || ghiChu.isBlank()))
                                throw new IllegalArgumentException(
                                        "Cần nhập lý do chênh lệch tiền mặt.");
                            String maNguoiNhan = null;
                            if (hinhThuc == HinhThucBanGiao.BAN_GIAO) {
                                var opt =
                                        nguoiNhan == null || nguoiNhan.isBlank()
                                                ? null
                                                : layNguoiNhan().stream()
                                                        .filter(
                                                                n ->
                                                                        n.maNhanVien()
                                                                                .equals(nguoiNhan))
                                                        .findFirst()
                                                        .orElse(null);
                                if (opt == null) {
                                    throw new IllegalArgumentException(
                                            "Chọn quản lý hoặc thu ngân đang hoạt động để nhận"
                                                    + " ca.");
                                }
                                maNguoiNhan = opt.maNhanVien();
                            }
                            if (caThuNganDAO.dongCoBanGiao(
                                            daDem,
                                            daDem - duKien,
                                            ghiChu,
                                            hinhThuc.name(),
                                            maNguoiNhan,
                                            maPhienHienTai)
                                    != 1) throw new IllegalStateException("Ca đã đóng.");
                            cuoiCungBanGiao =
                                    "Thực đếm "
                                            + daDem
                                            + " / dự kiến "
                                            + duKien
                                            + " (chênh lệch "
                                            + (daDem - duKien)
                                            + ")";
                            NhatKyHeThongService.macDinh().ghiNhatKy("Kết ca", cuoiCungBanGiao);
                            return null;
                        });
        thongBao();
    }

    // 4. ĐĂNG KÝ & PHÁT SỰ KIỆN CA

    public void themBoLangNghe(BoLangNgheSuKien<CaThuNganService> boLangNghe) {
        phatSuKien.them(boLangNghe);
    }

    public void xoaBoLangNghe(BoLangNgheSuKien<CaThuNganService> boLangNghe) {
        phatSuKien.xoa(boLangNghe);
    }

    public void thongBaoThayDoi() {
        thongBao();
    }

    private void thongBao() {
        xoaBoDem();
        phatSuKien.phat(this);
    }
}
