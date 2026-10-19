package com.petstation.dao;

import com.petstation.model.LoHang;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của lô hàng DAO. */
public interface LoHangDAO {

    /**
     * Tạo trong LoHangDAO.
     *
     * @param loHang lô hàng
     * @return kết quả tạo
     */
    LoHang tao(LoHang loHang);

    /**
     * Tìm theo id trong LoHangDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<LoHang> timTheoId(Long id);

    /**
     * Tìm theo mã lô hàng trong LoHangDAO.
     *
     * @param maLoHang mã lô hàng
     * @return kết quả tìm theo mã lô hàng
     */
    Optional<LoHang> timTheoMaLoHang(String maLoHang);

    /**
     * Tìm theo id sản phẩm trong LoHangDAO.
     *
     * @param idSanPham id sản phẩm
     * @return kết quả tìm theo id sản phẩm
     */
    List<LoHang> timTheoIdSanPham(Long idSanPham);

    /**
     * Tìm theo id nhà cung cấp trong LoHangDAO.
     *
     * @param idNhaCungCap id nhà cung cấp
     * @return kết quả tìm theo id nhà cung cấp
     */
    List<LoHang> timTheoIdNhaCungCap(Long idNhaCungCap);

    /**
     * Tìm theo trạng thái trong LoHangDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    List<LoHang> timTheoTrangThai(String trangThai);

    /**
     * Tìm sắp hết hạn trước trong LoHangDAO.
     *
     * @param ngay ngày
     * @return kết quả tìm sắp hết hạn trước
     */
    List<LoHang> timSapHetHanTruoc(LocalDate ngay);

    /**
     * Tìm hết hạn các lô hàng trong LoHangDAO.
     *
     * @return kết quả tìm hết hạn các lô hàng
     */
    List<LoHang> timHetHanCacLoHang();

    /**
     * Tìm thấp tồn kho các lô hàng trong LoHangDAO.
     *
     * @param nguong ngưỡng
     * @return kết quả tìm thấp tồn kho các lô hàng
     */
    List<LoHang> timThapTonKhoCacLoHang(Integer nguong);

    /**
     * Tìm tất cả trong LoHangDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<LoHang> timTatCa();

    /**
     * Cập nhật trong LoHangDAO.
     *
     * @param loHang lô hàng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(LoHang loHang);

    /**
     * Cập nhật số lượng trong LoHangDAO.
     *
     * @param idLoHang id lô hàng
     * @param soLuong số lượng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatSoLuong(Long idLoHang, Integer soLuong);

    /**
     * Cập nhật trạng thái trong LoHangDAO.
     *
     * @param idLoHang id lô hàng
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idLoHang, String trangThai);

    /**
     * Xóa trong LoHangDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Đếm trong LoHangDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
