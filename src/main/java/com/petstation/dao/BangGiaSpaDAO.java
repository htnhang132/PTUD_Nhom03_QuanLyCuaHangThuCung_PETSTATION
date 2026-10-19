package com.petstation.dao;

import com.petstation.model.BangGiaSpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của Spa giá ma trận DAO. */
public interface BangGiaSpaDAO {

    /**
     * Tạo trong BangGiaSpaDAO.
     *
     * @param giaMaTran giá ma trận
     * @return kết quả tạo
     */
    BangGiaSpa tao(BangGiaSpa giaMaTran);

    /**
     * Tìm theo id trong BangGiaSpaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<BangGiaSpa> timTheoId(Long id);

    /**
     * Tìm theo id gói dịch vụ trong BangGiaSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @return kết quả tìm theo id gói dịch vụ
     */
    List<BangGiaSpa> timTheoIdGoiDichVu(Long idGoiDichVu);

    /**
     * Tìm theo gói dịch vụ loài và cân nặng trong BangGiaSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @param loai loài
     * @param canNang cân nặng
     * @return kết quả tìm theo gói dịch vụ loài và cân nặng
     */
    Optional<BangGiaSpa> timTheoGoiDichVuLoaiVaCanNang(
            Long idGoiDichVu, String loai, BigDecimal canNang);

    /**
     * Tìm tất cả hoạt động trong BangGiaSpaDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    List<BangGiaSpa> timTatCaHoatDong();

    /**
     * Tìm theo loài trong BangGiaSpaDAO.
     *
     * @param loai loài
     * @return kết quả tìm theo loài
     */
    List<BangGiaSpa> timTheoLoai(String loai);

    /**
     * Tìm tất cả trong BangGiaSpaDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<BangGiaSpa> timTatCa();

    /**
     * Cập nhật trong BangGiaSpaDAO.
     *
     * @param giaMaTran giá ma trận
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(BangGiaSpa giaMaTran);

    /**
     * Cập nhật giá trong BangGiaSpaDAO.
     *
     * @param idGiaMaTran id giá ma trận
     * @param giaMoi giá mới
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatGia(Long idGiaMaTran, BigDecimal giaMoi);

    /**
     * Xóa trong BangGiaSpaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Xóa theo id gói dịch vụ trong BangGiaSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @return kết quả xóa theo id gói dịch vụ
     */
    int xoaTheoIdGoiDichVu(Long idGoiDichVu);

    /**
     * Đếm trong BangGiaSpaDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
