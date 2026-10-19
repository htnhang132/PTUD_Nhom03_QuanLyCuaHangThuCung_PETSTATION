package com.petstation.dao;

import com.petstation.model.HopDongLuuTru;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của hợp đồng lưu trú DAO. */
public interface HopDongLuuTruDAO {

    /**
     * Tạo trong HopDongLuuTruDAO.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @return kết quả tạo
     */
    HopDongLuuTru tao(HopDongLuuTru hopDongLuuTru);

    /**
     * Tìm theo id trong HopDongLuuTruDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<HopDongLuuTru> timTheoId(Long id);

    /**
     * Tìm theo mã hợp đồng trong HopDongLuuTruDAO.
     *
     * @param maHopDong mã hợp đồng
     * @return kết quả tìm theo mã hợp đồng
     */
    Optional<HopDongLuuTru> timTheoMaHopDong(String maHopDong);

    /**
     * Tìm theo id thú cưng trong HopDongLuuTruDAO.
     *
     * @param idThuCung id thú cưng
     * @return kết quả tìm theo id thú cưng
     */
    List<HopDongLuuTru> timTheoIdThuCung(Long idThuCung);

    /**
     * Tìm theo id khách hàng trong HopDongLuuTruDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    List<HopDongLuuTru> timTheoIdKhachHang(Long idKhachHang);

    /**
     * Tìm theo id phòng trong HopDongLuuTruDAO.
     *
     * @param idPhong id phòng
     * @return kết quả tìm theo id phòng
     */
    List<HopDongLuuTru> timTheoIdPhong(Long idPhong);

    /**
     * Tìm theo trạng thái trong HopDongLuuTruDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    List<HopDongLuuTru> timTheoTrangThai(String trangThai);

    /**
     * Tìm theo khoảng ngày trong HopDongLuuTruDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    List<HopDongLuuTru> timTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    /**
     * Tìm hoạt động các hợp đồng lưu trú trong HopDongLuuTruDAO.
     *
     * @return kết quả tìm hoạt động các hợp đồng lưu trú
     */
    List<HopDongLuuTru> timHoatDongCacHopDongLuuTru();

    /**
     * Tìm quá hạn các hợp đồng lưu trú trong HopDongLuuTruDAO.
     *
     * @return kết quả tìm quá hạn các hợp đồng lưu trú
     */
    List<HopDongLuuTru> timQuaHanCacHopDongLuuTru();

    /**
     * Tìm đã kiểm tra vào các hợp đồng lưu trú trong HopDongLuuTruDAO.
     *
     * @return kết quả tìm đã kiểm tra vào các hợp đồng lưu trú
     */
    List<HopDongLuuTru> timDaKiemTraVaoCacHopDongLuuTru();

    /**
     * Tìm tất cả trong HopDongLuuTruDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<HopDongLuuTru> timTatCa();

    /**
     * Cập nhật trong HopDongLuuTruDAO.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(HopDongLuuTru hopDongLuuTru);

    /**
     * Cập nhật trạng thái trong HopDongLuuTruDAO.
     *
     * @param idHopDongLuuTru id hợp đồng lưu trú
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idHopDongLuuTru, String trangThai);

    /**
     * Cập nhật thời gian trả phòng thực tế trong HopDongLuuTruDAO.
     *
     * @param idHopDongLuuTru id hợp đồng lưu trú
     * @param thoiGianTraPhong thời gian trả phòng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatThoiGianTraPhongThucTe(Long idHopDongLuuTru, LocalDateTime thoiGianTraPhong);

    /**
     * Xóa trong HopDongLuuTruDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Kiểm tra phòng khả dụng trong HopDongLuuTruDAO.
     *
     * @param idPhong id phòng
     * @param thoiGianBatDau thời gian bắt đầu
     * @param thoiGianKetThuc thời gian kết thúc
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean kiemTraPhongKhaDung(
            Long idPhong, LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc);

    /**
     * Đếm trong HopDongLuuTruDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
