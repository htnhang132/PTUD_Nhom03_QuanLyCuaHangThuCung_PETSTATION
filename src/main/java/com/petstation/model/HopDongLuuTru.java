package com.petstation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Biểu diễn dữ liệu ánh xạ với bảng hop_dong_luu_tru trong SQL Server. */
public class HopDongLuuTru {
    /** Id. */
    private Long id;

    /** Mã hợp đồng. */
    private String maHopDong;

    /** Id thú cưng. */
    private Long idThuCung;

    /** Id khách hàng. */
    private Long idKhachHang;

    /** Id phòng. */
    private Long idPhong;

    /** Thời gian nhận phòng. */
    private LocalDateTime thoiGianNhanPhong;

    /** Thời gian trả phòng dự kiến. */
    private LocalDateTime thoiGianTraPhongDuKien;

    /** Thời gian trả phòng thực tế. */
    private LocalDateTime thoiGianTraPhongThucTe;

    /** Khung thời gian. */
    private String khungThoiGian; // HOURLY, DAILY, OVERNIGHT

    /** Giá cơ sở. */
    private BigDecimal giaCoSo;

    /** Tiền cọc. */
    private BigDecimal tienCoc;

    /** Phí trễ. */
    private BigDecimal phiTre;

    /** Tổng tiền. */
    private BigDecimal tongTien;

    /** Trạng thái. */
    private String trangThai; // RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED, OVERDUE

    /** Ghi chú. */
    private String ghiChu;

    /** Id nhân viên. */
    private Long idNhanVien;

    /** Thời điểm tạo. */
    private LocalDateTime thoiDiemTao;

    /** Phiên bản bản ghi. */
    private byte[] phienBanBanGhi;

    // Hàm khởi tạo
    /** Khởi tạo HopDongLuuTru với các giá trị được cung cấp. */
    public HopDongLuuTru() {
        this.trangThai = "RESERVED";
        this.phiTre = BigDecimal.ZERO;
    }

    /**
     * Khởi tạo HopDongLuuTru với các giá trị được cung cấp.
     *
     * @param id id
     * @param maHopDong mã hợp đồng
     * @param idThuCung id thú cưng
     * @param idKhachHang id khách hàng
     * @param idPhong id phòng
     */
    public HopDongLuuTru(
            Long id, String maHopDong, Long idThuCung, Long idKhachHang, Long idPhong) {
        this();
        this.id = id;
        this.maHopDong = maHopDong;
        this.idThuCung = idThuCung;
        this.idKhachHang = idKhachHang;
        this.idPhong = idPhong;
    }

    // Truy xuất và cập nhật thuộc tính
    /**
     * Lấy id trong HopDongLuuTru.
     *
     * @return kết quả lấy id
     */
    public Long layId() {
        return id;
    }

    /**
     * Đặt id trong HopDongLuuTru.
     *
     * @param id id
     */
    public void datId(Long id) {
        this.id = id;
    }

    /**
     * Lấy mã hợp đồng trong HopDongLuuTru.
     *
     * @return kết quả lấy mã hợp đồng
     */
    public String layMaHopDong() {
        return maHopDong;
    }

    /**
     * Đặt mã hợp đồng trong HopDongLuuTru.
     *
     * @param maHopDong mã hợp đồng
     */
    public void datMaHopDong(String maHopDong) {
        this.maHopDong = maHopDong;
    }

    /**
     * Lấy id thú cưng trong HopDongLuuTru.
     *
     * @return kết quả lấy id thú cưng
     */
    public Long layIdThuCung() {
        return idThuCung;
    }

    /**
     * Đặt id thú cưng trong HopDongLuuTru.
     *
     * @param idThuCung id thú cưng
     */
    public void datIdThuCung(Long idThuCung) {
        this.idThuCung = idThuCung;
    }

    /**
     * Lấy id khách hàng trong HopDongLuuTru.
     *
     * @return kết quả lấy id khách hàng
     */
    public Long layIdKhachHang() {
        return idKhachHang;
    }

    /**
     * Đặt id khách hàng trong HopDongLuuTru.
     *
     * @param idKhachHang id khách hàng
     */
    public void datIdKhachHang(Long idKhachHang) {
        this.idKhachHang = idKhachHang;
    }

    /**
     * Lấy id phòng trong HopDongLuuTru.
     *
     * @return kết quả lấy id phòng
     */
    public Long layIdPhong() {
        return idPhong;
    }

    /**
     * Đặt id phòng trong HopDongLuuTru.
     *
     * @param idPhong id phòng
     */
    public void datIdPhong(Long idPhong) {
        this.idPhong = idPhong;
    }

    /**
     * Lấy thời gian nhận phòng trong HopDongLuuTru.
     *
     * @return kết quả lấy thời gian nhận phòng
     */
    public LocalDateTime layThoiGianNhanPhong() {
        return thoiGianNhanPhong;
    }

    /**
     * Đặt thời gian nhận phòng trong HopDongLuuTru.
     *
     * @param thoiGianNhanPhong thời gian nhận phòng
     */
    public void datThoiGianNhanPhong(LocalDateTime thoiGianNhanPhong) {
        this.thoiGianNhanPhong = thoiGianNhanPhong;
    }

    /**
     * Lấy thời gian trả phòng dự kiến trong HopDongLuuTru.
     *
     * @return kết quả lấy thời gian trả phòng dự kiến
     */
    public LocalDateTime layThoiGianTraPhongDuKien() {
        return thoiGianTraPhongDuKien;
    }

    /**
     * Đặt thời gian trả phòng dự kiến trong HopDongLuuTru.
     *
     * @param thoiGianTraPhongDuKien thời gian trả phòng dự kiến
     */
    public void datThoiGianTraPhongDuKien(LocalDateTime thoiGianTraPhongDuKien) {
        this.thoiGianTraPhongDuKien = thoiGianTraPhongDuKien;
    }

    /**
     * Lấy thời gian trả phòng thực tế trong HopDongLuuTru.
     *
     * @return kết quả lấy thời gian trả phòng thực tế
     */
    public LocalDateTime layThoiGianTraPhongThucTe() {
        return thoiGianTraPhongThucTe;
    }

    /**
     * Đặt thời gian trả phòng thực tế trong HopDongLuuTru.
     *
     * @param thoiGianTraPhongThucTe thời gian trả phòng thực tế
     */
    public void datThoiGianTraPhongThucTe(LocalDateTime thoiGianTraPhongThucTe) {
        this.thoiGianTraPhongThucTe = thoiGianTraPhongThucTe;
    }

    /**
     * Lấy khung thời gian trong HopDongLuuTru.
     *
     * @return kết quả lấy khung thời gian
     */
    public String layKhungThoiGian() {
        return khungThoiGian;
    }

    /**
     * Đặt khung thời gian trong HopDongLuuTru.
     *
     * @param khungThoiGian khung thời gian
     */
    public void datKhungThoiGian(String khungThoiGian) {
        this.khungThoiGian = khungThoiGian;
    }

    /**
     * Lấy giá cơ sở trong HopDongLuuTru.
     *
     * @return kết quả lấy giá cơ sở
     */
    public BigDecimal layGiaCoSo() {
        return giaCoSo;
    }

    /**
     * Đặt giá cơ sở trong HopDongLuuTru.
     *
     * @param giaCoSo giá cơ sở
     */
    public void datGiaCoSo(BigDecimal giaCoSo) {
        this.giaCoSo = giaCoSo;
    }

    /**
     * Lấy tiền cọc trong HopDongLuuTru.
     *
     * @return kết quả lấy tiền cọc
     */
    public BigDecimal layTienCoc() {
        return tienCoc;
    }

    /**
     * Đặt tiền cọc trong HopDongLuuTru.
     *
     * @param tienCoc tiền cọc
     */
    public void datTienCoc(BigDecimal tienCoc) {
        this.tienCoc = tienCoc;
    }

    /**
     * Lấy phí trễ trong HopDongLuuTru.
     *
     * @return kết quả lấy phí trễ
     */
    public BigDecimal layPhiTre() {
        return phiTre;
    }

    /**
     * Đặt phí trễ trong HopDongLuuTru.
     *
     * @param phiTre phí trễ
     */
    public void datPhiTre(BigDecimal phiTre) {
        this.phiTre = phiTre;
    }

    /**
     * Lấy tổng tiền trong HopDongLuuTru.
     *
     * @return kết quả lấy tổng tiền
     */
    public BigDecimal layTongTien() {
        return tongTien;
    }

    /**
     * Đặt tổng tiền trong HopDongLuuTru.
     *
     * @param tongTien tổng tiền
     */
    public void datTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    /**
     * Lấy trạng thái trong HopDongLuuTru.
     *
     * @return kết quả lấy trạng thái
     */
    public String layTrangThai() {
        return trangThai;
    }

    /**
     * Đặt trạng thái trong HopDongLuuTru.
     *
     * @param trangThai trạng thái
     */
    public void datTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * Lấy ghi chú trong HopDongLuuTru.
     *
     * @return kết quả lấy ghi chú
     */
    public String layGhiChu() {
        return ghiChu;
    }

    /**
     * Đặt ghi chú trong HopDongLuuTru.
     *
     * @param ghiChu ghi chú
     */
    public void datGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    /**
     * Lấy id nhân viên trong HopDongLuuTru.
     *
     * @return kết quả lấy id nhân viên
     */
    public Long layIdNhanVien() {
        return idNhanVien;
    }

    /**
     * Đặt id nhân viên trong HopDongLuuTru.
     *
     * @param idNhanVien id nhân viên
     */
    public void datIdNhanVien(Long idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    /**
     * Lấy thời điểm tạo trong HopDongLuuTru.
     *
     * @return kết quả lấy thời điểm tạo
     */
    public LocalDateTime layThoiDiemTao() {
        return thoiDiemTao;
    }

    /**
     * Đặt thời điểm tạo trong HopDongLuuTru.
     *
     * @param thoiDiemTao thời điểm tạo
     */
    public void datThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    /**
     * Lấy phiên bản bản ghi trong HopDongLuuTru.
     *
     * @return kết quả lấy phiên bản bản ghi
     */
    public byte[] layPhienBanBanGhi() {
        return phienBanBanGhi;
    }

    /**
     * Đặt phiên bản bản ghi trong HopDongLuuTru.
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
        return "HotelBooking{"
                + "id="
                + id
                + ", bookingCode='"
                + maHopDong
                + '\''
                + ", petId="
                + idThuCung
                + ", roomId="
                + idPhong
                + ", status='"
                + trangThai
                + '\''
                + ", totalAmount="
                + tongTien
                + '}';
    }
}
