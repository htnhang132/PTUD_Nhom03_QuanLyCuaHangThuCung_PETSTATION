package com.petstation.dao;

import com.petstation.model.NhaCungCap;

import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của nhà cung cấp DAO. */
public interface NhaCungCapDAO {

    /**
     * Tạo trong NhaCungCapDAO.
     *
     * @param nhaCungCap nhà cung cấp
     * @return kết quả tạo
     */
    NhaCungCap tao(NhaCungCap nhaCungCap);

    /**
     * Tìm theo id trong NhaCungCapDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<NhaCungCap> timTheoId(Long id);

    /**
     * Tìm theo mã nhà cung cấp trong NhaCungCapDAO.
     *
     * @param maNhaCungCap mã nhà cung cấp
     * @return kết quả tìm theo mã nhà cung cấp
     */
    Optional<NhaCungCap> timTheoMaNhaCungCap(String maNhaCungCap);

    /**
     * Tìm theo số điện thoại trong NhaCungCapDAO.
     *
     * @param soDienThoai số điện thoại
     * @return kết quả tìm theo số điện thoại
     */
    Optional<NhaCungCap> timTheoSoDienThoai(String soDienThoai);

    /**
     * Tìm kiếm theo tên trong NhaCungCapDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    List<NhaCungCap> timKiemTheoTen(String ten);

    /**
     * Tìm tất cả hoạt động trong NhaCungCapDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    List<NhaCungCap> timTatCaHoatDong();

    /**
     * Tìm tất cả trong NhaCungCapDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<NhaCungCap> timTatCa();

    /**
     * Cập nhật trong NhaCungCapDAO.
     *
     * @param nhaCungCap nhà cung cấp
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(NhaCungCap nhaCungCap);

    /**
     * Cập nhật trạng thái hoạt động trong NhaCungCapDAO.
     *
     * @param idNhaCungCap id nhà cung cấp
     * @param dangHoatDong đang hoạt động
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThaiHoatDong(Long idNhaCungCap, Boolean dangHoatDong);

    /**
     * Xóa trong NhaCungCapDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Lấy mã tồn tại theo nhà cung cấp trong NhaCungCapDAO.
     *
     * @param maNhaCungCap mã nhà cung cấp
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean layMaTonTaiTheoNhaCungCap(String maNhaCungCap);

    /**
     * Đếm trong NhaCungCapDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
