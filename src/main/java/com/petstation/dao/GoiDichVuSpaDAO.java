package com.petstation.dao;

import com.petstation.model.GoiDichVuSpa;

import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của gói dịch vụ Spa DAO. */
public interface GoiDichVuSpaDAO {

    /**
     * Tạo trong GoiDichVuSpaDAO.
     *
     * @param goiDichVuSpa gói dịch vụ Spa
     * @return kết quả tạo
     */
    GoiDichVuSpa tao(GoiDichVuSpa goiDichVuSpa);

    /**
     * Tìm theo id trong GoiDichVuSpaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<GoiDichVuSpa> timTheoId(Long id);

    /**
     * Tìm theo mã gói dịch vụ trong GoiDichVuSpaDAO.
     *
     * @param maGoiDichVu mã gói dịch vụ
     * @return kết quả tìm theo mã gói dịch vụ
     */
    Optional<GoiDichVuSpa> timTheoMaGoiDichVu(String maGoiDichVu);

    /**
     * Tìm theo loại gói dịch vụ trong GoiDichVuSpaDAO.
     *
     * @param loaiGoiDichVu loại gói dịch vụ
     * @return kết quả tìm theo loại gói dịch vụ
     */
    List<GoiDichVuSpa> timTheoLoaiGoiDichVu(String loaiGoiDichVu);

    /**
     * Tìm tất cả hoạt động trong GoiDichVuSpaDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    List<GoiDichVuSpa> timTatCaHoatDong();

    /**
     * Tìm tất cả trong GoiDichVuSpaDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<GoiDichVuSpa> timTatCa();

    /**
     * Cập nhật trong GoiDichVuSpaDAO.
     *
     * @param goiDichVuSpa gói dịch vụ Spa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(GoiDichVuSpa goiDichVuSpa);

    /**
     * Cập nhật trạng thái trong GoiDichVuSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idGoiDichVu, String trangThai);

    /**
     * Xóa trong GoiDichVuSpaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Lấy mã tồn tại theo gói dịch vụ trong GoiDichVuSpaDAO.
     *
     * @param maGoiDichVu mã gói dịch vụ
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean layMaTonTaiTheoGoiDichVu(String maGoiDichVu);

    /**
     * Đếm trong GoiDichVuSpaDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
