package com.petstation.dao;

import com.petstation.model.ChotNgay;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của ngày khóa DAO. */
public interface ChotNgayDAO {

    /**
     * Tạo trong ChotNgayDAO.
     *
     * @param ngayKhoa ngày khóa
     * @return kết quả tạo
     */
    ChotNgay tao(ChotNgay ngayKhoa);

    /**
     * Tìm theo id trong ChotNgayDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<ChotNgay> timTheoId(Long id);

    /**
     * Tìm theo ngày chốt trong ChotNgayDAO.
     *
     * @param ngayChot ngày chốt
     * @return kết quả tìm theo ngày chốt
     */
    Optional<ChotNgay> timTheoNgayChot(LocalDate ngayChot);

    /**
     * Tìm theo người khóa trong ChotNgayDAO.
     *
     * @param idNguoiDung id người dùng
     * @return kết quả tìm theo người khóa
     */
    List<ChotNgay> timTheoNguoiKhoa(Long idNguoiDung);

    /**
     * Tìm theo trạng thái trong ChotNgayDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    List<ChotNgay> timTheoTrangThai(String trangThai);

    /**
     * Tìm theo khoảng ngày trong ChotNgayDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    List<ChotNgay> timTheoKhoangNgay(LocalDate ngayBatDau, LocalDate ngayKetThuc);

    /**
     * Tìm đã khóa số ngày trong ChotNgayDAO.
     *
     * @return kết quả tìm đã khóa số ngày
     */
    List<ChotNgay> timDaKhoaSoNgay();

    /**
     * Tìm tất cả trong ChotNgayDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<ChotNgay> timTatCa();

    /**
     * Cập nhật trong ChotNgayDAO.
     *
     * @param ngayKhoa ngày khóa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(ChotNgay ngayKhoa);

    /**
     * Cập nhật trạng thái trong ChotNgayDAO.
     *
     * @param idNgayKhoa id ngày khóa
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idNgayKhoa, String trangThai);

    /**
     * Xóa trong ChotNgayDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Kiểm tra ngày đã khóa trong ChotNgayDAO.
     *
     * @param ngay ngày
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean kiemTraNgayDaKhoa(LocalDate ngay);

    /**
     * Đếm trong ChotNgayDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
