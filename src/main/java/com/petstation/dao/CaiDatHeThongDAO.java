package com.petstation.dao;

import com.petstation.model.CaiDatHeThong;

import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của hệ thống cài đặt DAO. */
public interface CaiDatHeThongDAO {

    /**
     * Tạo trong CaiDatHeThongDAO.
     *
     * @param caiDat cài đặt
     * @return kết quả tạo
     */
    CaiDatHeThong tao(CaiDatHeThong caiDat);

    /**
     * Tìm theo id trong CaiDatHeThongDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<CaiDatHeThong> timTheoId(Long id);

    /**
     * Tìm theo khóa trong CaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả tìm theo khóa
     */
    Optional<CaiDatHeThong> timTheoKhoa(String khoa);

    /**
     * Lấy giá trị trong CaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả lấy giá trị
     */
    String layGiaTri(String khoa);

    /**
     * Lấy giá trị hoặc mặc định trong CaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @param giaTriMacDinh giá trị mặc định
     * @return kết quả lấy giá trị hoặc mặc định
     */
    String layGiaTriHoacMacDinh(String khoa, String giaTriMacDinh);

    /**
     * Tìm theo nhóm trong CaiDatHeThongDAO.
     *
     * @param nhom nhóm
     * @return kết quả tìm theo nhóm
     */
    List<CaiDatHeThong> timTheoNhom(String nhom);

    /**
     * Tìm tất cả trong CaiDatHeThongDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<CaiDatHeThong> timTatCa();

    /**
     * Cập nhật trong CaiDatHeThongDAO.
     *
     * @param caiDat cài đặt
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(CaiDatHeThong caiDat);

    /**
     * Cập nhật giá trị trong CaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @param giaTri giá trị
     * @param daCapNhatTheo đã cập nhật theo
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatGiaTri(String khoa, String giaTri, Long daCapNhatTheo);

    /**
     * Xóa trong CaiDatHeThongDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Xóa theo khóa trong CaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoaTheoKhoa(String khoa);

    /**
     * Lấy tồn tại theo khóa trong CaiDatHeThongDAO.
     *
     * @param khoa khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean layTonTaiTheoKhoa(String khoa);

    /**
     * Đếm trong CaiDatHeThongDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
