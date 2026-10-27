package com.petstation.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Biểu diễn dữ liệu bán hàng ngữ cảnh dùng trong nghiệp vụ cửa hàng.
 *
 * <p>Giỏ hàng/hóa đơn đang lập, dùng chung giữa Lập đơn → Thanh toán → In hóa đơn.
 */
public final class GioHang {
    /** Cổng nghiệp vụ: model không biết JDBC hay lớp triển khai service. */
    public interface ChinhSach {
        List<Object[]> layKhuyenMai();

        long kiemTraVoucher(String ma, String khachHang, String nguon, long tamTinh);

        BienLai thanhToan(GioHang gio, PhuongThuc phuongThuc, long daNhan, String thuNgan);

        BienLai thanhToan(GioHang gio, List<DongThuTien> dong, String thuNgan);
    }

    private final ChinhSach chinhSach;

    public GioHang(ChinhSach chinhSach) {
        this.chinhSach = java.util.Objects.requireNonNull(chinhSach);
    }

    /**
     * Biểu diễn dữ liệu dòng dùng trong nghiệp vụ cửa hàng.
     *
     * @param ma mã
     * @param ten tên
     * @param donGia đơn giá
     * @param soLuong số lượng
     */
    public record DongHang(String ma, String ten, long donGia, int soLuong) {
        public DongHang {
            if (ma == null || ma.isBlank() || ten == null || donGia < 0 || soLuong <= 0)
                throw new IllegalArgumentException("Dòng hàng không hợp lệ.");
        }

        public long docSoTien() {
            return donGia * soLuong;
        }
    }

    /**
     * Biểu diễn dữ liệu biên lai dùng trong nghiệp vụ cửa hàng.
     *
     * @param soHoaDon số hóa đơn
     * @param thoiGian thời gian
     * @param cacDong các dòng
     * @param tamTinh tạm tính
     * @param giamGia giảm giá
     * @param tong tổng
     * @param phuongThuc phương thức
     * @param daNhan đã nhận
     * @param thayDoi thay đổi
     * @param khachHang khách hàng
     * @param voucher voucher
     * @param thuNgan thu ngân
     */
    public record BienLai(
            String soHoaDon,
            LocalDateTime thoiGian,
            List<DongHang> cacDong,
            long tamTinh,
            long giamGia,
            long tong,
            PhuongThuc phuongThuc,
            long daNhan,
            long thayDoi,
            String khachHang,
            String voucher,
            String thuNgan,
            List<DongThuTien> cacKhoanThu) {
        public BienLai(
                String soHoaDon,
                LocalDateTime thoiGian,
                List<DongHang> cacDong,
                long tamTinh,
                long giamGia,
                long tong,
                PhuongThuc phuongThuc,
                long daNhan,
                long thayDoi,
                String khachHang,
                String voucher,
                String thuNgan) {
            this(
                    soHoaDon,
                    thoiGian,
                    cacDong,
                    tamTinh,
                    giamGia,
                    tong,
                    phuongThuc,
                    daNhan,
                    thayDoi,
                    khachHang,
                    voucher,
                    thuNgan,
                    List.of());
        }
    }

    /** Các dòng. */
    private final NguCanhQuyTrinh quyTrinh = new NguCanhQuyTrinh();

    public NguCanhQuyTrinh layQuyTrinh() {
        return quyTrinh;
    }

    private final List<DongHang> cacDong = new ArrayList<>();
    private volatile boolean dangGhiThanhToan;

    public boolean dangGhiThanhToan() {
        return dangGhiThanhToan;
    }

    public void datDangGhiThanhToan(boolean value) {
        dangGhiThanhToan = value;
    }

    /** Khách hàng số điện thoại. */
    private String khachHangSoDienThoai = "";

    /** Giảm giá phần trăm. */
    private int giamGiaPhanTram;

    /** Mã voucher. */
    private String maVoucher = "";

    /** Mã khách hàng. */
    private String maKhachHang = "";

    /** Tên khách hàng. */
    private String tenKhachHang = "";

    /** Khách hàng điểm. */
    private long khachHangDiem;

    /** Điểm đã dùng. */
    private long diemDaDung;

    /** Giá trị voucher. */
    private long giaTriVoucher;

    /** Biên lai cuối. */
    private BienLai bienLaiCuoi;

    /** Khóa thanh toán. */
    private String khoaThanhToan =
            "PAY-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);

    /** Kiểu nguồn. */
    private String kieuNguon = "";

    /** Id nguồn. */
    private long idNguon;

    public record YeuCauDoiHang(
            long dongGoc, int soLuong, String lyDo, long giaTriDuocDoi, String maKhachHang) {}

    private YeuCauDoiHang doiHang;
    private String xacNhanNhanTien = "";

    private String taoKhoaNhanTien(PhuongThuc pt) {
        return pt + "|" + khoaThanhToan + "|" + tinhTong() + "|" + maVoucher;
    }

    public void xacNhanDaNhanTien(PhuongThuc pt, boolean daNhan) {
        xacNhanNhanTien = daNhan ? taoKhoaNhanTien(pt) : "";
    }

    public boolean kiemTraDaNhanTien(PhuongThuc pt) {
        return pt == PhuongThuc.CASH || xacNhanNhanTien.equals(taoKhoaNhanTien(pt));
    }

    public YeuCauDoiHang layDoiHang() {
        return doiHang;
    }

    public void datDoiHang(YeuCauDoiHang giaTri) {
        doiHang = giaTri;
    }

    public List<DongHang> layCacDong() {
        return List.copyOf(cacDong);
    }

    public boolean kiemTraTrong() {
        return cacDong.isEmpty();
    }

    public String layKhachHangSoDienThoai() {
        return khachHangSoDienThoai;
    }

    public int layGiamGiaPhanTram() {
        return giamGiaPhanTram;
    }

    public String layMaVoucher() {
        return maVoucher;
    }

    public BienLai layBienLaiCuoi() {
        return bienLaiCuoi;
    }

    public void datBienLaiCuoi(BienLai bienLai) {
        bienLaiCuoi = bienLai;
    }

    public String layKhoaThanhToan() {
        return khoaThanhToan;
    }

    public String layKieuNguon() {
        return kieuNguon;
    }

    public long layIdNguon() {
        return idNguon;
    }

    public void datNguon(String kieu, long id) {
        kieuNguon = kieu;
        idNguon = id;
    }

    public void datCacDong(List<DongHang> moiCacDong) {
        cacDong.clear();
        cacDong.addAll(moiCacDong);
    }

    public String layMaKhachHang() {
        return maKhachHang;
    }

    public String layTenKhachHang() {
        return tenKhachHang;
    }

    public long layKhachHangDiem() {
        return khachHangDiem;
    }

    public long layDiemDaDung() {
        return diemDaDung;
    }

    /**
     * Đặt khách hàng trong GioHang.
     *
     * <p>Chọn khách thành viên cho hóa đơn (code rỗng = khách lẻ).
     *
     * @param ma mã
     * @param ten tên
     * @param diem điểm
     */
    public void datKhachHang(String ma, String ten, long diem) {
        maKhachHang = ma == null ? "" : ma;
        tenKhachHang = ten == null ? "" : ten;
        khachHangDiem = Math.max(0, diem);
        maVoucher = "";
        giaTriVoucher = 0;
        diemDaDung = 0;
    }

    /**
     * Đặt điểm đã dùng trong GioHang.
     *
     * <p>Dùng điểm: không vượt số điểm của khách và không vượt số tiền còn phải trả.
     *
     * @param diem điểm
     */
    public void datDiemDaDung(long diem) {
        if (diem != 0)
            throw new IllegalArgumentException(
                    "Điểm chỉ dùng để đổi voucher Spa; không trừ trực tiếp vào hóa đơn.");
        diemDaDung = 0;
    }

    public long layGiaTriDiem() {
        return 0;
    }

    /**
     * Lấy khuyến mãi tốt nhất trong GioHang.
     *
     * <p>Khuyến mãi tự áp: chọn chương trình đang áp dụng cho giá giảm lớn nhất.
     *
     * @return kết quả lấy khuyến mãi tốt nhất
     */
    public KhuyenMai layKhuyenMaiTotNhat() {
        // Tốt nhất.
        KhuyenMai totNhat = null;
        long tamTinh = tinhTamTinh();
        for (Object[] dongDuLieu : chinhSach.layKhuyenMai()) {
            if ("Ngừng áp dụng".equals(dongDuLieu[8])
                    || !kiemTraTrongKhoangNgay(dongDuLieu[6], dongDuLieu[7])) continue;
            if (tamTinh < com.petstation.util.TienTe.doc(String.valueOf(dongDuLieu[5]))) continue;
            long coSo = 0;
            // Phạm vi.
            String phamVi = String.valueOf(dongDuLieu[3]);
            for (DongHang dongDangXet : cacDong) {
                // Là dịch vụ.
                boolean laDichVu = "SPA".equals(kieuNguon) || "HOTEL".equals(kieuNguon);
                if (phamVi.equals("Cả đơn")
                        || (phamVi.equals("Sản phẩm") && !laDichVu)
                        || (phamVi.equals("Dịch vụ") && laDichVu)) coSo += dongDangXet.docSoTien();
            }
            long giaTri = com.petstation.util.TienTe.doc(String.valueOf(dongDuLieu[4]));
            // Số tiền.
            long soTien =
                    switch (String.valueOf(dongDuLieu[2])) {
                        case "Giảm %" -> Math.round(coSo * giaTri / 100.0);
                        case "Giảm tiền" -> coSo > 0 ? Math.min(giaTri, coSo) : 0;
                        default -> 0;
                    };
            if (soTien > 0 && (totNhat == null || soTien > totNhat.soTien()))
                totNhat =
                        new KhuyenMai(
                                String.valueOf(dongDuLieu[0]),
                                String.valueOf(dongDuLieu[1]),
                                soTien);
        }
        return totNhat;
    }

    /**
     * Kiểm tra trong khoảng ngày trong GioHang.
     *
     * <p>Ngày hôm nay nằm trong [from, to] (định dạng dd/MM/yyyy); ngày sai định dạng không áp
     * dụng.
     *
     * @param tu từ
     * @param den đến
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private static boolean kiemTraTrongKhoangNgay(Object tu, Object den) {
        java.time.format.DateTimeFormatter truongDangXet =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        // Hôm nay.
        java.time.LocalDate homNay = java.time.LocalDate.now();
        try {
            if (java.time.LocalDate.parse(String.valueOf(tu), truongDangXet).isAfter(homNay))
                return false;
            return !java.time.LocalDate.parse(String.valueOf(den), truongDangXet).isBefore(homNay);
        } catch (java.time.format.DateTimeParseException loi) {
            return false;
        }
    }

    /**
     * Biểu diễn dữ liệu khuyến mãi dùng trong nghiệp vụ cửa hàng.
     *
     * @param ma mã
     * @param ten tên
     * @param soTien số tiền
     */
    public record KhuyenMai(String ma, String ten, long soTien) {}

    public long layKhuyenMaiGiamGia() {
        KhuyenMai vungDangXet = layKhuyenMaiTotNhat();
        return vungDangXet == null ? 0 : vungDangXet.soTien();
    }

    public void datKhachHangSoDienThoai(String soDienThoai) {
        this.khachHangSoDienThoai = soDienThoai == null ? "" : soDienThoai.trim();
    }

    public void datGiamGiaPhanTram(int phanTram) {
        this.giamGiaPhanTram = Math.max(0, Math.min(phanTram, 100));
    }

    public long tinhTamTinh() {
        // Tổng.
        long tong = 0;
        for (DongHang dongDangXet : cacDong) tong += dongDangXet.docSoTien();
        return tong;
    }

    public long tinhGiamGia() {
        if ("EXCHANGE".equals(kieuNguon)) return doiHang == null ? 0 : doiHang.giaTriDuocDoi();
        // Theo phần trăm.
        long theoPhanTram = Math.round(tinhTamTinh() * giamGiaPhanTram / 100.0);
        return Math.min(
                tinhTamTinh(),
                theoPhanTram + layKhuyenMaiGiamGia() + laySoTienVoucher() + layGiaTriDiem());
    }

    public long tinhTong() {
        return tinhTamTinh() - tinhGiamGia();
    }

    /**
     * Áp dụng voucher trong GioHang.
     *
     * <p>Áp mã voucher trong Danh mục. Trả về thông báo lỗi, hoặc null nếu hợp lệ (mã rỗng = bỏ
     * voucher).
     *
     * @param ma mã
     * @return kết quả áp dụng voucher
     */
    public String apDungVoucher(String ma) {
        if ("EXCHANGE".equals(kieuNguon) && ma != null && !ma.isBlank())
            return "Đổi hàng không áp dụng thêm voucher.";
        String giaTri = ma == null ? "" : ma.trim().toUpperCase(Locale.ROOT);
        if (giaTri.isEmpty()) {
            maVoucher = "";
            giaTriVoucher = 0;
            return null;
        }
        try {
            long soTien = chinhSach.kiemTraVoucher(giaTri, maKhachHang, kieuNguon, tinhTamTinh());
            maVoucher = giaTri;
            giaTriVoucher = soTien;
            return null;
        } catch (IllegalArgumentException loi) {
            return loi.getMessage();
        }
    }

    public long laySoTienVoucher() {
        return maVoucher.isEmpty()
                ? 0
                : Math.min(giaTriVoucher, Math.max(0, tinhTamTinh() - layKhuyenMaiGiamGia()));
    }

    public BienLai hoanTat(PhuongThuc phuongThuc, long daNhan, String thuNgan) {
        bienLaiCuoi = chinhSach.thanhToan(this, phuongThuc, daNhan, thuNgan);
        quyTrinh.ketThuc(kieuNguon);
        return bienLaiCuoi;
    }

    public BienLai hoanTat(List<DongThuTien> cacKhoanThu, String thuNgan) {
        bienLaiCuoi = chinhSach.thanhToan(this, cacKhoanThu, thuNgan);
        quyTrinh.ketThuc(kieuNguon);
        return bienLaiCuoi;
    }

    public void xoa() {
        khoaThanhToan =
                "PAY-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        kieuNguon = "";
        idNguon = 0;
        doiHang = null;
        xacNhanNhanTien = "";
        cacDong.clear();
        khachHangSoDienThoai = "";
        giamGiaPhanTram = 0;
        maVoucher = "";
        giaTriVoucher = 0;
        maKhachHang = "";
        tenKhachHang = "";
        khachHangDiem = 0;
        diemDaDung = 0;
    }
}
