package com.petstation.dao;

import com.petstation.model.BangGiaHotel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của Hotel bảng giá DAO. */
public interface BangGiaHotelDAO {

    /**
     * Tạo trong BangGiaHotelDAO.
     *
     * @param bangGia bảng giá
     * @return kết quả tạo
     */
    BangGiaHotel tao(BangGiaHotel bangGia);

    /**
     * Tìm theo id trong BangGiaHotelDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<BangGiaHotel> timTheoId(Long id);

    /**
     * Tìm theo id phòng trong BangGiaHotelDAO.
     *
     * @param idPhong id phòng
     * @return kết quả tìm theo id phòng
     */
    List<BangGiaHotel> timTheoIdPhong(Long idPhong);

    /**
     * Tìm theo khung thời gian trong BangGiaHotelDAO.
     *
     * @param khungThoiGian khung thời gian
     * @return kết quả tìm theo khung thời gian
     */
    List<BangGiaHotel> timTheoKhungThoiGian(String khungThoiGian);

    /**
     * Tìm theo phòng khung thời gian và cân nặng trong BangGiaHotelDAO.
     *
     * @param idPhong id phòng
     * @param khungThoiGian khung thời gian
     * @param canNang cân nặng
     * @return kết quả tìm theo phòng khung thời gian và cân nặng
     */
    Optional<BangGiaHotel> timTheoPhongKhungThoiGianVaCanNang(
            Long idPhong, String khungThoiGian, BigDecimal canNang);

    /**
     * Tìm tất cả hoạt động trong BangGiaHotelDAO.
     *
     * @return kết quả tìm tất cả hoạt động
     */
    List<BangGiaHotel> timTatCaHoatDong();

    /**
     * Tìm tất cả trong BangGiaHotelDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<BangGiaHotel> timTatCa();

    /**
     * Cập nhật trong BangGiaHotelDAO.
     *
     * @param bangGia bảng giá
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(BangGiaHotel bangGia);

    /**
     * Cập nhật giá trong BangGiaHotelDAO.
     *
     * @param idBangGia id bảng giá
     * @param giaMoi giá mới
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatGia(Long idBangGia, BigDecimal giaMoi);

    /**
     * Xóa trong BangGiaHotelDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Xóa theo id phòng trong BangGiaHotelDAO.
     *
     * @param idPhong id phòng
     * @return kết quả xóa theo id phòng
     */
    int xoaTheoIdPhong(Long idPhong);

    /**
     * Đếm trong BangGiaHotelDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
