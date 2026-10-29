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
    public static class DongHang implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String ma;
        private String ten;
        private long donGia;
        private int soLuong;

        public DongHang() {}

        public DongHang(String ma, String ten, long donGia, int soLuong) {
            if (ma == null || ma.isBlank() || ten == null || donGia < 0 || soLuong <= 0)
                throw new IllegalArgumentException("Dòng hàng không hợp lệ.");
            this.ma = ma;
            this.ten = ten;
            this.donGia = donGia;
            this.soLuong = soLuong;
        }

        public String getMa() { return ma; }
        public String layMa() { return ma; }
        public String ma() { return ma; }
        public void setMa(String ma) { this.ma = ma; }
        public void datMa(String ma) { this.ma = ma; }

        public String getTen() { return ten; }
        public String layTen() { return ten; }
        public String ten() { return ten; }
        public void setTen(String ten) { this.ten = ten; }
        public void datTen(String ten) { this.ten = ten; }

        public long getDonGia() { return donGia; }
        public long layDonGia() { return donGia; }
        public long donGia() { return donGia; }
        public void setDonGia(long donGia) { this.donGia = donGia; }
        public void datDonGia(long donGia) { this.donGia = donGia; }

        public int getSoLuong() { return soLuong; }
        public int laySoLuong() { return soLuong; }
        public int soLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
        public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

        public long docSoTien() {
            return donGia * soLuong;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DongHang dongHang = (DongHang) o;
            return donGia == dongHang.donGia && soLuong == dongHang.soLuong && java.util.Objects.equals(ma, dongHang.ma) && java.util.Objects.equals(ten, dongHang.ten);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(ma, ten, donGia, soLuong);
        }

        @Override
        public String toString() {
            return "DongHang{" + "ma='" + ma + '\'' + ", ten='" + ten + '\'' + ", donGia=" + donGia + ", soLuong=" + soLuong + '}';
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
    public static class BienLai implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String soHoaDon;
        private LocalDateTime thoiGian;
        private List<DongHang> cacDong;
        private long tamTinh;
        private long giamGia;
        private long tong;
        private PhuongThuc phuongThuc;
        private long daNhan;
        private long thayDoi;
        private String khachHang;
        private String voucher;
        private String thuNgan;
        private List<DongThuTien> cacKhoanThu;

        public BienLai() {}

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
                String thuNgan,
                List<DongThuTien> cacKhoanThu) {
            this.soHoaDon = soHoaDon;
            this.thoiGian = thoiGian;
            this.cacDong = cacDong;
            this.tamTinh = tamTinh;
            this.giamGia = giamGia;
            this.tong = tong;
            this.phuongThuc = phuongThuc;
            this.daNhan = daNhan;
            this.thayDoi = thayDoi;
            this.khachHang = khachHang;
            this.voucher = voucher;
            this.thuNgan = thuNgan;
            this.cacKhoanThu = cacKhoanThu != null ? cacKhoanThu : List.of();
        }

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
            this(soHoaDon, thoiGian, cacDong, tamTinh, giamGia, tong, phuongThuc, daNhan, thayDoi, khachHang, voucher, thuNgan, List.of());
        }

        public String getSoHoaDon() { return soHoaDon; }
        public String laySoHoaDon() { return soHoaDon; }
        public String soHoaDon() { return soHoaDon; }
        public void setSoHoaDon(String soHoaDon) { this.soHoaDon = soHoaDon; }
        public void datSoHoaDon(String soHoaDon) { this.soHoaDon = soHoaDon; }

        public LocalDateTime getThoiGian() { return thoiGian; }
        public LocalDateTime layThoiGian() { return thoiGian; }
        public LocalDateTime thoiGian() { return thoiGian; }
        public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
        public void datThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

        public List<DongHang> getCacDong() { return cacDong; }
        public List<DongHang> layCacDong() { return cacDong; }
        public List<DongHang> cacDong() { return cacDong; }
        public void setCacDong(List<DongHang> cacDong) { this.cacDong = cacDong; }
        public void datCacDong(List<DongHang> cacDong) { this.cacDong = cacDong; }

        public long getTamTinh() { return tamTinh; }
        public long layTamTinh() { return tamTinh; }
        public long tamTinh() { return tamTinh; }
        public void setTamTinh(long tamTinh) { this.tamTinh = tamTinh; }
        public void datTamTinh(long tamTinh) { this.tamTinh = tamTinh; }

        public long getGiamGia() { return giamGia; }
        public long layGiamGia() { return giamGia; }
        public long giamGia() { return giamGia; }
        public void setGiamGia(long giamGia) { this.giamGia = giamGia; }
        public void datGiamGia(long giamGia) { this.giamGia = giamGia; }

        public long getTong() { return tong; }
        public long layTong() { return tong; }
        public long tong() { return tong; }
        public void setTong(long tong) { this.tong = tong; }
        public void datTong(long tong) { this.tong = tong; }

        public PhuongThuc getPhuongThuc() { return phuongThuc; }
        public PhuongThuc layPhuongThuc() { return phuongThuc; }
        public PhuongThuc phuongThuc() { return phuongThuc; }
        public void setPhuongThuc(PhuongThuc phuongThuc) { this.phuongThuc = phuongThuc; }
        public void datPhuongThuc(PhuongThuc phuongThuc) { this.phuongThuc = phuongThuc; }

        public long getDaNhan() { return daNhan; }
        public long layDaNhan() { return daNhan; }
        public long daNhan() { return daNhan; }
        public void setDaNhan(long daNhan) { this.daNhan = daNhan; }
        public void datDaNhan(long daNhan) { this.daNhan = daNhan; }

        public long getThayDoi() { return thayDoi; }
        public long layThayDoi() { return thayDoi; }
        public long thayDoi() { return thayDoi; }
        public void setThayDoi(long thayDoi) { this.thayDoi = thayDoi; }
        public void datThayDoi(long thayDoi) { this.thayDoi = thayDoi; }

        public String getKhachHang() { return khachHang; }
        public String layKhachHang() { return khachHang; }
        public String khachHang() { return khachHang; }
        public void setKhachHang(String khachHang) { this.khachHang = khachHang; }
        public void datKhachHang(String khachHang) { this.khachHang = khachHang; }

        public String getVoucher() { return voucher; }
        public String layVoucher() { return voucher; }
        public String voucher() { return voucher; }
        public void setVoucher(String voucher) { this.voucher = voucher; }
        public void datVoucher(String voucher) { this.voucher = voucher; }

        public String getThuNgan() { return thuNgan; }
        public String layThuNgan() { return thuNgan; }
        public String thuNgan() { return thuNgan; }
        public void setThuNgan(String thuNgan) { this.thuNgan = thuNgan; }
        public void datThuNgan(String thuNgan) { this.thuNgan = thuNgan; }

        public List<DongThuTien> getCacKhoanThu() { return cacKhoanThu; }
        public List<DongThuTien> layCacKhoanThu() { return cacKhoanThu; }
        public List<DongThuTien> cacKhoanThu() { return cacKhoanThu; }
        public void setCacKhoanThu(List<DongThuTien> cacKhoanThu) { this.cacKhoanThu = cacKhoanThu; }
        public void datCacKhoanThu(List<DongThuTien> cacKhoanThu) { this.cacKhoanThu = cacKhoanThu; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BienLai bienLai = (BienLai) o;
            return tamTinh == bienLai.tamTinh && giamGia == bienLai.giamGia && tong == bienLai.tong && daNhan == bienLai.daNhan && thayDoi == bienLai.thayDoi && java.util.Objects.equals(soHoaDon, bienLai.soHoaDon) && java.util.Objects.equals(thoiGian, bienLai.thoiGian) && java.util.Objects.equals(cacDong, bienLai.cacDong) && phuongThuc == bienLai.phuongThuc && java.util.Objects.equals(khachHang, bienLai.khachHang) && java.util.Objects.equals(voucher, bienLai.voucher) && java.util.Objects.equals(thuNgan, bienLai.thuNgan) && java.util.Objects.equals(cacKhoanThu, bienLai.cacKhoanThu);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(soHoaDon, thoiGian, cacDong, tamTinh, giamGia, tong, phuongThuc, daNhan, thayDoi, khachHang, voucher, thuNgan, cacKhoanThu);
        }

        @Override
        public String toString() {
            return "BienLai{" + "soHoaDon='" + soHoaDon + '\'' + ", tong=" + tong + '}';
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

    public static class YeuCauDoiHang implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private long dongGoc;
        private int soLuong;
        private String lyDo;
        private long giaTriDuocDoi;
        private String maKhachHang;

        public YeuCauDoiHang() {}

        public YeuCauDoiHang(long dongGoc, int soLuong, String lyDo, long giaTriDuocDoi, String maKhachHang) {
            this.dongGoc = dongGoc;
            this.soLuong = soLuong;
            this.lyDo = lyDo;
            this.giaTriDuocDoi = giaTriDuocDoi;
            this.maKhachHang = maKhachHang;
        }

        public long getDongGoc() { return dongGoc; }
        public long layDongGoc() { return dongGoc; }
        public long dongGoc() { return dongGoc; }
        public void setDongGoc(long dongGoc) { this.dongGoc = dongGoc; }
        public void datDongGoc(long dongGoc) { this.dongGoc = dongGoc; }

        public int getSoLuong() { return soLuong; }
        public int laySoLuong() { return soLuong; }
        public int soLuong() { return soLuong; }
        public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
        public void datSoLuong(int soLuong) { this.soLuong = soLuong; }

        public String getLyDo() { return lyDo; }
        public String layLyDo() { return lyDo; }
        public String lyDo() { return lyDo; }
        public void setLyDo(String lyDo) { this.lyDo = lyDo; }
        public void datLyDo(String lyDo) { this.lyDo = lyDo; }

        public long getGiaTriDuocDoi() { return giaTriDuocDoi; }
        public long layGiaTriDuocDoi() { return giaTriDuocDoi; }
        public long giaTriDuocDoi() { return giaTriDuocDoi; }
        public void setGiaTriDuocDoi(long giaTriDuocDoi) { this.giaTriDuocDoi = giaTriDuocDoi; }
        public void datGiaTriDuocDoi(long giaTriDuocDoi) { this.giaTriDuocDoi = giaTriDuocDoi; }

        public String getMaKhachHang() { return maKhachHang; }
        public String layMaKhachHang() { return maKhachHang; }
        public String maKhachHang() { return maKhachHang; }
        public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
        public void datMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    }

    private YeuCauDoiHang doiHang;
    private String xacNhanNhanTien = "";

    private String taoKhoaNhanTien(PhuongThuc pt) {
        return pt + "|" + khoaThanhToan + "|" + tinhTong() + "|" + maVoucher;
    }

    public void xacNhanDaNhanTien(PhuongThuc pt, boolean daNhan) {
        xacNhanNhanTien = daNhan ? taoKhoaNhanTien(pt) : "";
    }

    public boolean kiemTraDaNhanTien(PhuongThuc pt) {
        return pt == PhuongThuc.TIEN_MAT || xacNhanNhanTien.equals(taoKhoaNhanTien(pt));
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
    public static class KhuyenMai implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String ma;
        private String ten;
        private long soTien;

        public KhuyenMai() {}

        public KhuyenMai(String ma, String ten, long soTien) {
            this.ma = ma;
            this.ten = ten;
            this.soTien = soTien;
        }

        public String getMa() { return ma; }
        public String layMa() { return ma; }
        public String ma() { return ma; }
        public void setMa(String ma) { this.ma = ma; }
        public void datMa(String ma) { this.ma = ma; }

        public String getTen() { return ten; }
        public String layTen() { return ten; }
        public String ten() { return ten; }
        public void setTen(String ten) { this.ten = ten; }
        public void datTen(String ten) { this.ten = ten; }

        public long getSoTien() { return soTien; }
        public long laySoTien() { return soTien; }
        public long soTien() { return soTien; }
        public void setSoTien(long soTien) { this.soTien = soTien; }
        public void datSoTien(long soTien) { this.soTien = soTien; }
    }

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
