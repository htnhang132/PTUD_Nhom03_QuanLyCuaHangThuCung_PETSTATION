package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng phieu_tiep_nhan_spa trong SQL Server. */
public class PhieuTiepNhanSpa {
    /** Id. */
    private Long id;

    /** Mã phiếu Spa. */
    private String maPhieuSpa;

    /** Id thú cưng. */
    private Long idThuCung;

    /** Id khách hàng. */
    private Long idKhachHang;

    /** Id gói dịch vụ. */
    private Long idGoiDichVu;

    /** Id nhân viên. */
    private Long idNhanVien;

    /** Thời gian nhận phòng. */
    private LocalDateTime thoiGianNhanPhong;

    /** Thời gian trả phòng. */
    private LocalDateTime thoiGianTraPhong;

    /** Giá cơ sở. */
    private BigDecimal giaCoSo;

    /** Tiền phụ thu. */
    private BigDecimal tienPhuThu;

    /** Tổng tiền. */
    private BigDecimal tongTien;

    /** Trạng thái. */
    private String trangThai; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED

    /** Ghi chú. */
    private String ghiChu;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo PhieuTiepNhanSpa với các giá trị được cung cấp. */
    public PhieuTiepNhanSpa() {
        this.trangThai = "PENDING";
        this.tienPhuThu = BigDecimal.ZERO;
    }

    /**
     * Khởi tạo PhieuTiepNhanSpa với các giá trị được cung cấp.
     *
     * @param id id
     * @param maPhieuSpa mã phiếu Spa
     * @param idThuCung id thú cưng
     * @param idKhachHang id khách hàng
     * @param idGoiDichVu id gói dịch vụ
     */
    public PhieuTiepNhanSpa(
            Long id, String maPhieuSpa, Long idThuCung, Long idKhachHang, Long idGoiDichVu) {
        this();
        this.id = id;
        this.maPhieuSpa = maPhieuSpa;
        this.idThuCung = idThuCung;
        this.idKhachHang = idKhachHang;
        this.idGoiDichVu = idGoiDichVu;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong PhieuTiepNhanSpa.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã phiếu Spa trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy mã phiếu Spa
     */
    public String layMaPhieuSpa() {
        return maPhieuSpa;
    }

    /**
     * Đặt mã phiếu Spa trong PhieuTiepNhanSpa.
     *
     * @param maPhieuSpa mã phiếu Spa
     */
    public void datMaPhieuSpa(String maPhieuSpa) {
        this.maPhieuSpa = maPhieuSpa;
    }

    /**
     * Lấy id thú cưng trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy id thú cưng
     */
    public Long layIdThuCung() {
        return idThuCung;
    }

    /**
     * Đặt id thú cưng trong PhieuTiepNhanSpa.
     *
     * @param idThuCung id thú cưng
     */
    public void datIdThuCung(Long idThuCung) {
        this.idThuCung = idThuCung;
    }

    /**
     * Lấy id khách hàng trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy id khách hàng
     */
    public Long layIdKhachHang() {
        return idKhachHang;
    }

    /**
     * Đặt id khách hàng trong PhieuTiepNhanSpa.
     *
     * @param idKhachHang id khách hàng
     */
    public void datIdKhachHang(Long idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    /**
     * Lấy id gói dịch vụ trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy id gói dịch vụ
     */
    public Long layIdGoiDichVu() {
        return idGoiDichVu;
    }

    /**
     * Đặt id gói dịch vụ trong PhieuTiepNhanSpa.
     *
     * @param idGoiDichVu id gói dịch vụ
     */
    public void datIdGoiDichVu(Long idGoiDichVu) {
        this.idGoiDichVu = idGoiDichVu;
    }

    /**
     * Lấy id nhân viên trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy id nhân viên
     */
    public Long layIdNhanVien() {
        return idNhanVien;
    }

    /**
     * Đặt id nhân viên trong PhieuTiepNhanSpa.
     *
     * @param idNhanVien id nhân viên
     */
    public void datIdNhanVien(Long idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    /**
     * Lấy thời gian nhận phòng trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy thời gian nhận phòng
     */
    public LocalDateTime layThoiGianNhanPhong() {
        return thoiGianNhanPhong;
    }

    /**
     * Đặt thời gian nhận phòng trong PhieuTiepNhanSpa.
     *
     * @param thoiGianNhanPhong thời gian nhận phòng
     */
    public void datThoiGianNhanPhong(LocalDateTime thoiGianNhanPhong) {
        this.thoiGianNhanPhong = thoiGianNhanPhong;
    }

    /**
     * Lấy thời gian trả phòng trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy thời gian trả phòng
     */
    public LocalDateTime layThoiGianTraPhong() {
        return thoiGianTraPhong;
    }

    /**
     * Đặt thời gian trả phòng trong PhieuTiepNhanSpa.
     *
     * @param thoiGianTraPhong thời gian trả phòng
     */
    public void datThoiGianTraPhong(LocalDateTime thoiGianTraPhong) {
        this.thoiGianTraPhong = thoiGianTraPhong;
    }

    /**
     * Lấy giá cơ sở trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy giá cơ sở
     */
    public BigDecimal layGiaCoSo() {
        return giaCoSo;
    }

    /**
     * Đặt giá cơ sở trong PhieuTiepNhanSpa.
     *
     * @param giaCoSo giá cơ sở
     */
    public void datGiaCoSo(BigDecimal giaCoSo) {
        this.giaCoSo = giaCoSo;
    }

    /**
     * Lấy tiền phụ thu trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy tiền phụ thu
     */
    public BigDecimal layTienPhuThu() {
        return tienPhuThu;
    }

    /**
     * Đặt tiền phụ thu trong PhieuTiepNhanSpa.
     *
     * @param tienPhuThu tiền phụ thu
     */
    public void datTienPhuThu(BigDecimal tienPhuThu) {
        this.tienPhuThu = tienPhuThu;
    }

    /**
     * Lấy tổng tiền trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy tổng tiền
     */
    public BigDecimal layTongTien() {
        return tongTien;
    }

    /**
     * Đặt tổng tiền trong PhieuTiepNhanSpa.
     *
     * @param tongTien tổng tiền
     */
    public void datTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    /**
     * Lấy trạng thái trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong PhieuTiepNhanSpa.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy ghi chú trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong PhieuTiepNhanSpa.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy thời điểm tạo trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong PhieuTiepNhanSpa.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong PhieuTiepNhanSpa.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong PhieuTiepNhanSpa.
     *
     * @param phienBanBanGhi phiên bản bản ghi
     */
    public void datPhienBanBanGhi(byte[] phienBanBanGhi) {
        this.phienBanBanGhi = phienBanBanGhi;
    }

    /**
     * Biểu diễn đối tượng dưới dạng chuỗi.
     *
     * @return chuỗi mô tả đối tượng
     */
    @Override
    public String toString() {
        return "SpaTicket{"
                + "id="
                + id
                + ", ticketCode='"
                + maPhieuSpa
                + '\''
                + ", petId="
                + idThuCung
                + ", totalAmount="
                + tongTien
                + ", status='"
                + trangThai
                + '\''
                + '}';
    }
}
