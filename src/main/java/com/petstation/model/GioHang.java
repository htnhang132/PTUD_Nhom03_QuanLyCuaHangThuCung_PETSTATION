package com.petstation.model;

import com.petstation.service.CaThuNganService;
import com.petstation.service.DanhMucService;

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
    /**
     * Biểu diễn dữ liệu dòng dùng trong nghiệp vụ cửa hàng.
     *
     * @param ma mã
     * @param ten tên
     * @param donGia đơn giá
     * @param soLuong số lượng
     */
    public record DongHang(String ma, String ten, long donGia, int soLuong) {
        /**
         * Khởi tạo DongHang với các giá trị được cung cấp.
         *
         * @param ma mã
         * @param ten tên
         * @param donGia đơn giá
         * @param soLuong số lượng
         */
        public DongHang {
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (ma == null || ma.isBlank() || ten == null || donGia < 0 || soLuong <= 0)
                throw new IllegalArgumentException("Dòng hàng không hợp lệ.");
        }

        /**
         * Đọc số tiền trong DongHang.
         *
         * @return kết quả đọc số tiền
         */
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
            CaThuNganService.PhuongThuc phuongThuc,
            long daNhan,
            long thayDoi,
            String khachHang,
            String voucher,
            String thuNgan) {}

    /** Các dòng. */
    private final NguCanhQuyTrinh quyTrinh = new NguCanhQuyTrinh();

    public NguCanhQuyTrinh layQuyTrinh() {
        return quyTrinh;
    }

    private final List<DongHang> cacDong = new ArrayList<>();

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

    private String taoKhoaNhanTien(CaThuNganService.PhuongThuc pt) {
        return pt + "|" + khoaThanhToan + "|" + tinhTong() + "|" + maVoucher;
    }

    public void xacNhanDaNhanTien(CaThuNganService.PhuongThuc pt, boolean daNhan) {
        xacNhanNhanTien = daNhan ? taoKhoaNhanTien(pt) : "";
    }

    public boolean kiemTraDaNhanTien(CaThuNganService.PhuongThuc pt) {
        return pt == CaThuNganService.PhuongThuc.CASH
                || xacNhanNhanTien.equals(taoKhoaNhanTien(pt));
    }

    public YeuCauDoiHang layDoiHang() {
        return doiHang;
    }

    public void datDoiHang(YeuCauDoiHang giaTri) {
        doiHang = giaTri;
    }

    /**
     * Lấy các dòng trong GioHang.
     *
     * @return kết quả lấy các dòng
     */
    public List<DongHang> layCacDong() {
        return List.copyOf(cacDong);
    }

    /**
     * Kiểm tra trống trong GioHang.
     *
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public boolean kiemTraTrong() {
        return cacDong.isEmpty();
    }

    /**
     * Lấy khách hàng số điện thoại trong GioHang.
     *
     * @return kết quả lấy khách hàng số điện thoại
     */
    public String layKhachHangSoDienThoai() {
        return khachHangSoDienThoai;
    }

    /**
     * Lấy giảm giá phần trăm trong GioHang.
     *
     * @return kết quả lấy giảm giá phần trăm
     */
    public int layGiamGiaPhanTram() {
        return giamGiaPhanTram;
    }

    /**
     * Lấy mã voucher trong GioHang.
     *
     * @return kết quả lấy mã voucher
     */
    public String layMaVoucher() {
        return maVoucher;
    }

    /**
     * Lấy biên lai cuối trong GioHang.
     *
     * @return kết quả lấy biên lai cuối
     */
    public BienLai layBienLaiCuoi() {
        return bienLaiCuoi;
    }

    /**
     * Đặt biên lai cuối trong GioHang.
     *
     * @param bienLai biên lai
     */
    public void datBienLaiCuoi(BienLai bienLai) {
        bienLaiCuoi = bienLai;
    }

    /**
     * Lấy khóa thanh toán trong GioHang.
     *
     * @return kết quả lấy khóa thanh toán
     */
    public String layKhoaThanhToan() {
        return khoaThanhToan;
    }

    /**
     * Lấy kiểu nguồn trong GioHang.
     *
     * @return kết quả lấy kiểu nguồn
     */
    public String layKieuNguon() {
        return kieuNguon;
    }

    /**
     * Lấy id nguồn trong GioHang.
     *
     * @return kết quả lấy id nguồn
     */
    public long layIdNguon() {
        return idNguon;
    }

    /**
     * Đặt nguồn trong GioHang.
     *
     * @param kieu kiểu
     * @param id id
     */
    public void datNguon(String kieu, long id) {
        kieuNguon = kieu;
        idNguon = id;
    }

    /**
     * Đặt các dòng trong GioHang.
     *
     * @param moiCacDong mới các dòng
     */
    public void datCacDong(List<DongHang> moiCacDong) {
        cacDong.clear();
        cacDong.addAll(moiCacDong);
    }

    /**
     * Lấy mã khách hàng trong GioHang.
     *
     * @return kết quả lấy mã khách hàng
     */
    public String layMaKhachHang() {
        return maKhachHang;
    }

    /**
     * Lấy tên khách hàng trong GioHang.
     *
     * @return kết quả lấy tên khách hàng
     */
    public String layTenKhachHang() {
        return tenKhachHang;
    }

    /**
     * Lấy khách hàng điểm trong GioHang.
     *
     * @return kết quả lấy khách hàng điểm
     */
    public long layKhachHangDiem() {
        return khachHangDiem;
    }

    /**
     * Lấy điểm đã dùng trong GioHang.
     *
     * @return kết quả lấy điểm đã dùng
     */
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

    /**
     * Lấy giá trị điểm trong GioHang.
     *
     * @return kết quả lấy giá trị điểm
     */
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
        // Phần phụ.
        long phanPhu = tinhTamTinh();
        for (Object[] dongDuLieu : DanhMucService.layKhuyenMai()) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if ("Ngừng áp dụng".equals(dongDuLieu[8])
                    || !kiemTraTrongKhoangNgay(dongDuLieu[6], dongDuLieu[7])) continue;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (phanPhu < com.petstation.util.TienTe.doc(String.valueOf(dongDuLieu[5]))) continue;
            // Cơ sở.
            long coSo = 0;
            // Phạm vi.
            String phamVi = String.valueOf(dongDuLieu[3]);
            for (DongHang dongDangXet : cacDong) {
                // Là dịch vụ.
                boolean laDichVu = "SPA".equals(kieuNguon) || "HOTEL".equals(kieuNguon);
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (phamVi.equals("Cả đơn")
                        || (phamVi.equals("Sản phẩm") && !laDichVu)
                        || (phamVi.equals("Dịch vụ") && laDichVu)) coSo += dongDangXet.docSoTien();
            }
            // Giá trị.
            long giaTri = com.petstation.util.TienTe.doc(String.valueOf(dongDuLieu[4]));
            // Số tiền.
            long soTien =
                    switch (String.valueOf(dongDuLieu[2])) {
                        case "Giảm %" -> Math.round(coSo * giaTri / 100.0);
                        case "Giảm tiền" -> coSo > 0 ? Math.min(giaTri, coSo) : 0;
                        default -> 0;
                    };
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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
        // Trường đang xét.
        java.time.format.DateTimeFormatter truongDangXet =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        // Hôm nay.
        java.time.LocalDate homNay = java.time.LocalDate.now();
        try {
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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

    /**
     * Lấy khuyến mãi giảm giá trong GioHang.
     *
     * @return kết quả lấy khuyến mãi giảm giá
     */
    public long layKhuyenMaiGiamGia() {
        // Vùng đang xét.
        KhuyenMai vungDangXet = layKhuyenMaiTotNhat();
        return vungDangXet == null ? 0 : vungDangXet.soTien();
    }

    /**
     * Đặt khách hàng số điện thoại trong GioHang.
     *
     * @param soDienThoai số điện thoại
     */
    public void datKhachHangSoDienThoai(String soDienThoai) {
        this.khachHangSoDienThoai = soDienThoai == null ? "" : soDienThoai.trim();
    }

    /**
     * Đặt giảm giá phần trăm trong GioHang.
     *
     * @param phanTram phần trăm
     */
    public void datGiamGiaPhanTram(int phanTram) {
        this.giamGiaPhanTram = Math.max(0, Math.min(phanTram, 100));
    }

    /**
     * Tính tạm tính trong GioHang.
     *
     * @return kết quả tính tạm tính
     */
    public long tinhTamTinh() {
        // Tổng.
        long tong = 0;
        for (DongHang dongDangXet : cacDong) tong += dongDangXet.docSoTien();
        return tong;
    }

    /**
     * Tính giảm giá trong GioHang.
     *
     * @return kết quả tính giảm giá
     */
    public long tinhGiamGia() {
        if ("EXCHANGE".equals(kieuNguon)) return doiHang == null ? 0 : doiHang.giaTriDuocDoi();
        // Theo phần trăm.
        long theoPhanTram = Math.round(tinhTamTinh() * giamGiaPhanTram / 100.0);
        return Math.min(
                tinhTamTinh(),
                theoPhanTram + layKhuyenMaiGiamGia() + laySoTienVoucher() + layGiaTriDiem());
    }

    /**
     * Tính tổng trong GioHang.
     *
     * @return kết quả tính tổng
     */
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
            long soTien =
                    com.petstation.service.TichDiemService.kiemTraVoucher(
                            giaTri, maKhachHang, kieuNguon, tinhTamTinh());
            maVoucher = giaTri;
            giaTriVoucher = soTien;
            return null;
        } catch (IllegalArgumentException loi) {
            return loi.getMessage();
        }
    }

    /**
     * Lấy số tiền voucher trong GioHang.
     *
     * @return kết quả lấy số tiền voucher
     */
    public long laySoTienVoucher() {
        return maVoucher.isEmpty()
                ? 0
                : Math.min(giaTriVoucher, Math.max(0, tinhTamTinh() - layKhuyenMaiGiamGia()));
    }

    /**
     * Hoàn tất trong GioHang.
     *
     * @param phuongThuc phương thức
     * @param daNhan đã nhận
     * @param thuNgan thu ngân
     * @return kết quả hoàn tất
     */
    public BienLai hoanTat(CaThuNganService.PhuongThuc phuongThuc, long daNhan, String thuNgan) {
        bienLaiCuoi =
                com.petstation.service.BanHangService.thanhToan(this, phuongThuc, daNhan, thuNgan);
        quyTrinh.ketThuc(kieuNguon);
        return bienLaiCuoi;
    }

    /** Xóa trong GioHang. */
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
