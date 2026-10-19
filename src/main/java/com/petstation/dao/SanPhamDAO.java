package com.petstation.dao;

import com.petstation.model.SanPham;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của sản phẩm DAO. */
public interface SanPhamDAO {

    /**
     * Tạo trong SanPhamDAO.
     *
     * @param sanPham sản phẩm
     * @return kết quả tạo
     */
    SanPham tao(SanPham sanPham);

    /**
     * Tìm theo id trong SanPhamDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<SanPham> timTheoId(Long id);

    /**
     * Tìm theo mã sản phẩm trong SanPhamDAO.
     *
     * @param maSanPham mã sản phẩm
     * @return kết quả tìm theo mã sản phẩm
     */
    Optional<SanPham> timTheoMaSanPham(String maSanPham);

    /**
     * Tìm theo id nhóm hàng trong SanPhamDAO.
     *
     * @param idNhomHang id nhóm hàng
     * @return kết quả tìm theo id nhóm hàng
     */
    List<SanPham> timTheoIdNhomHang(Long idNhomHang);

    /**
     * Tìm kiếm theo tên trong SanPhamDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    List<SanPham> timKiemTheoTen(String ten);

    /**
     * Tìm tất cả hoạt động trong SanPhamDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    List<SanPham> timTatCaHoatDong();

    /**
     * Tìm yêu cầu đơn thuốc trong SanPhamDAO.
     *
     * @return kết quả tìm yêu cầu đơn thuốc
     */
    List<SanPham> timYeuCauDonThuoc();

    /**
     * Tìm tất cả trong SanPhamDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<SanPham> timTatCa();

    /**
     * Cập nhật trong SanPhamDAO.
     *
     * @param sanPham sản phẩm
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(SanPham sanPham);

    /**
     * Cập nhật giá bán trong SanPhamDAO.
     *
     * @param idSanPham id sản phẩm
     * @param giaMoi giá mới
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatGiaBan(Long idSanPham, BigDecimal giaMoi);

    /**
     * Cập nhật trạng thái hoạt động trong SanPhamDAO.
     *
     * @param idSanPham id sản phẩm
     * @param dangHoatDong đang hoạt động
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThaiHoatDong(Long idSanPham, Boolean dangHoatDong);

    /**
     * Xóa trong SanPhamDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Lấy mã tồn tại theo sản phẩm trong SanPhamDAO.
     *
     * @param maSanPham mã sản phẩm
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean layMaTonTaiTheoSanPham(String maSanPham);

    /**
     * Đếm trong SanPhamDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
