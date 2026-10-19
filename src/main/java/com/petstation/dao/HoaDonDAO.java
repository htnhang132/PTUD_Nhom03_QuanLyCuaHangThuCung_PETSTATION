package com.petstation.dao;

import com.petstation.model.HoaDon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của hóa đơn DAO. */
public interface HoaDonDAO {

    /**
     * Tạo trong HoaDonDAO.
     *
     * @param hoaDon hóa đơn
     * @return kết quả tạo
     */
    HoaDon tao(HoaDon hoaDon);

    /**
     * Tìm theo id trong HoaDonDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<HoaDon> timTheoId(Long id);

    /**
     * Tìm theo mã hóa đơn trong HoaDonDAO.
     *
     * @param maHoaDon mã hóa đơn
     * @return kết quả tìm theo mã hóa đơn
     */
    Optional<HoaDon> timTheoMaHoaDon(String maHoaDon);

    /**
     * Tìm theo id khách hàng trong HoaDonDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    List<HoaDon> timTheoIdKhachHang(Long idKhachHang);

    /**
     * Tìm theo id nhân viên trong HoaDonDAO.
     *
     * @param idNhanVien id nhân viên
     * @return kết quả tìm theo id nhân viên
     */
    List<HoaDon> timTheoIdNhanVien(Long idNhanVien);

    /**
     * Tìm theo id phiên ca trong HoaDonDAO.
     *
     * @param idPhienCa id phiên ca
     * @return kết quả tìm theo id phiên ca
     */
    List<HoaDon> timTheoIdPhienCa(Long idPhienCa);

    /**
     * Tìm theo khoảng ngày trong HoaDonDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    List<HoaDon> timTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    /**
     * Tìm theo trạng thái trong HoaDonDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    List<HoaDon> timTheoTrangThai(String trangThai);

    /**
     * Tìm theo loại hóa đơn trong HoaDonDAO.
     *
     * @param loaiHoaDon loại hóa đơn
     * @return kết quả tìm theo loại hóa đơn
     */
    List<HoaDon> timTheoLoaiHoaDon(String loaiHoaDon);

    /**
     * Tìm theo phương thức thanh toán trong HoaDonDAO.
     *
     * @param phuongThucThanhToan phương thức thanh toán
     * @return kết quả tìm theo phương thức thanh toán
     */
    List<HoaDon> timTheoPhuongThucThanhToan(String phuongThucThanhToan);

    /**
     * Tìm tất cả trong HoaDonDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<HoaDon> timTatCa();

    /**
     * Cập nhật trong HoaDonDAO.
     *
     * @param hoaDon hóa đơn
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(HoaDon hoaDon);

    /**
     * Cập nhật trạng thái trong HoaDonDAO.
     *
     * @param idHoaDon id hóa đơn
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idHoaDon, String trangThai);

    /**
     * Xóa trong HoaDonDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Lấy khoảng tổng doanh thu theo ngày trong HoaDonDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả lấy khoảng tổng doanh thu theo ngày
     */
    BigDecimal layKhoangTongDoanhThuTheoNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    /**
     * Lấy tổng doanh thu theo phiên ca trong HoaDonDAO.
     *
     * @param idPhienCa id phiên ca
     * @return kết quả lấy tổng doanh thu theo phiên ca
     */
    BigDecimal layTongDoanhThuTheoPhienCa(Long idPhienCa);

    /**
     * Đếm theo khoảng ngày trong HoaDonDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả đếm theo khoảng ngày
     */
    long demTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    /**
     * Đếm trong HoaDonDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
