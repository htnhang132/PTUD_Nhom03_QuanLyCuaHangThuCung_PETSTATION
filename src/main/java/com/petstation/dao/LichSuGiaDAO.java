package com.petstation.dao;

import com.petstation.model.LichSuGia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của lịch sử giá DAO. */
public interface LichSuGiaDAO {

    /**
     * Tạo trong LichSuGiaDAO.
     *
     * @param lichSuGia lịch sử giá
     * @return kết quả tạo
     */
    LichSuGia tao(LichSuGia lichSuGia);

    /**
     * Tìm theo id trong LichSuGiaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<LichSuGia> timTheoId(Long id);

    /**
     * Tìm theo id sản phẩm trong LichSuGiaDAO.
     *
     * @param idSanPham id sản phẩm
     * @return kết quả tìm theo id sản phẩm
     */
    List<LichSuGia> timTheoIdSanPham(Long idSanPham);

    /**
     * Tìm theo đã thay đổi theo trong LichSuGiaDAO.
     *
     * @param idNguoiDung id người dùng
     * @return kết quả tìm theo đã thay đổi theo
     */
    List<LichSuGia> timTheoDaThayDoiTheo(Long idNguoiDung);

    /**
     * Tìm theo kiểu thay đổi trong LichSuGiaDAO.
     *
     * @param kieuThayDoi kiểu thay đổi
     * @return kết quả tìm theo kiểu thay đổi
     */
    List<LichSuGia> timTheoKieuThayDoi(String kieuThayDoi);

    /**
     * Tìm theo khoảng ngày trong LichSuGiaDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    List<LichSuGia> timTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    /**
     * Tìm id gần nhất theo sản phẩm trong LichSuGiaDAO.
     *
     * @param idSanPham id sản phẩm
     * @return kết quả tìm id gần nhất theo sản phẩm
     */
    Optional<LichSuGia> timIdGanNhatTheoSanPham(Long idSanPham);

    /**
     * Tìm tất cả trong LichSuGiaDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<LichSuGia> timTatCa();

    /**
     * Xóa trong LichSuGiaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Đếm trong LichSuGiaDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
