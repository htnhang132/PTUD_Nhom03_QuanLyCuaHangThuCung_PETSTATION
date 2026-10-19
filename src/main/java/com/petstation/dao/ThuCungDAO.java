package com.petstation.dao;

import com.petstation.model.ThuCung;

import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của thú cưng DAO. */
public interface ThuCungDAO {

    /**
     * Tạo trong ThuCungDAO.
     *
     * @param thuCung thú cưng
     * @return kết quả tạo
     */
    ThuCung tao(ThuCung thuCung);

    /**
     * Tìm theo id trong ThuCungDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<ThuCung> timTheoId(Long id);

    /**
     * Tìm theo mã thú cưng trong ThuCungDAO.
     *
     * @param maThuCung mã thú cưng
     * @return kết quả tìm theo mã thú cưng
     */
    Optional<ThuCung> timTheoMaThuCung(String maThuCung);

    /**
     * Tìm theo id khách hàng trong ThuCungDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    List<ThuCung> timTheoIdKhachHang(Long idKhachHang);

    /**
     * Tìm theo loài trong ThuCungDAO.
     *
     * @param loai loài
     * @return kết quả tìm theo loài
     */
    List<ThuCung> timTheoLoai(String loai);

    /**
     * Tìm kiếm theo tên trong ThuCungDAO.
     *
     * @param ten tên
     * @return kết quả tìm kiếm theo tên
     */
    List<ThuCung> timKiemTheoTen(String ten);

    /**
     * Tìm tất cả trong ThuCungDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<ThuCung> timTatCa();

    /**
     * Cập nhật trong ThuCungDAO.
     *
     * @param thuCung thú cưng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(ThuCung thuCung);

    /**
     * Cập nhật cân nặng trong ThuCungDAO.
     *
     * @param idThuCung id thú cưng
     * @param canNang cân nặng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatCanNang(Long idThuCung, java.math.BigDecimal canNang);

    /**
     * Xóa trong ThuCungDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Đếm theo id khách hàng trong ThuCungDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả đếm theo id khách hàng
     */
    long demTheoIdKhachHang(Long idKhachHang);

    /**
     * Đếm trong ThuCungDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
