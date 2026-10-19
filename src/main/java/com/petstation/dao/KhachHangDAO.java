package com.petstation.dao;

import com.petstation.model.KhachHang;

import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của khách hàng DAO. */
public interface KhachHangDAO {

    /**
     * Tạo trong KhachHangDAO.
     *
     * @param khachHang khách hàng
     * @return kết quả tạo
     */
    KhachHang tao(KhachHang khachHang);

    /**
     * Tìm theo id trong KhachHangDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<KhachHang> timTheoId(Long id);

    /**
     * Tìm theo mã khách hàng trong KhachHangDAO.
     *
     * @param maKhachHang mã khách hàng
     * @return kết quả tìm theo mã khách hàng
     */
    Optional<KhachHang> timTheoMaKhachHang(String maKhachHang);

    /**
     * Tìm theo số điện thoại trong KhachHangDAO.
     *
     * @param soDienThoai số điện thoại
     * @return kết quả tìm theo số điện thoại
     */
    Optional<KhachHang> timTheoSoDienThoai(String soDienThoai);

    /**
     * Tìm kiếm theo tên trong KhachHangDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    List<KhachHang> timKiemTheoTen(String ten);

    /**
     * Tìm tất cả trong KhachHangDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<KhachHang> timTatCa();

    /**
     * Tìm theo hạng thành viên trong KhachHangDAO.
     *
     * @param hang hạng
     * @return kết quả tìm theo hạng thành viên
     */
    List<KhachHang> timTheoHangThanhVien(String hang);

    /**
     * Cập nhật trong KhachHangDAO.
     *
     * @param khachHang khách hàng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(KhachHang khachHang);

    /**
     * Cập nhật điểm tích lũy trong KhachHangDAO.
     *
     * @param idKhachHang id khách hàng
     * @param diem điểm
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatDiemTichLuy(Long idKhachHang, Integer diem);

    /**
     * Cập nhật hạng thành viên trong KhachHangDAO.
     *
     * @param idKhachHang id khách hàng
     * @param hang hạng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatHangThanhVien(Long idKhachHang, String hang);

    /**
     * Xóa trong KhachHangDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Lấy tồn tại theo số điện thoại trong KhachHangDAO.
     *
     * @param soDienThoai số điện thoại
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean layTonTaiTheoSoDienThoai(String soDienThoai);

    /**
     * Đếm trong KhachHangDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
