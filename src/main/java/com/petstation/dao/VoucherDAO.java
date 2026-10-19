package com.petstation.dao;

import com.petstation.model.Voucher;

import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của voucher DAO. */
public interface VoucherDAO {

    /**
     * Tạo trong VoucherDAO.
     *
     * @param voucher voucher
     * @return kết quả tạo
     */
    Voucher tao(Voucher voucher);

    /**
     * Tìm theo id trong VoucherDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<Voucher> timTheoId(Long id);

    /**
     * Tìm theo mã voucher trong VoucherDAO.
     *
     * @param maVoucher mã voucher
     * @return kết quả tìm theo mã voucher
     */
    Optional<Voucher> timTheoMaVoucher(String maVoucher);

    /**
     * Tìm theo id khách hàng trong VoucherDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    List<Voucher> timTheoIdKhachHang(Long idKhachHang);

    /**
     * Tìm theo loại voucher trong VoucherDAO.
     *
     * @param loaiVoucher loại voucher
     * @return kết quả tìm theo loại voucher
     */
    List<Voucher> timTheoLoaiVoucher(String loaiVoucher);

    /**
     * Tìm theo trạng thái trong VoucherDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    List<Voucher> timTheoTrangThai(String trangThai);

    /**
     * Tìm hoạt động các voucher trong VoucherDAO.
     *
     * @return kết quả tìm hoạt động các voucher
     */
    List<Voucher> timHoatDongCacVoucher();

    /**
     * Tìm hết hạn các voucher trong VoucherDAO.
     *
     * @return kết quả tìm hết hạn các voucher
     */
    List<Voucher> timHetHanCacVoucher();

    /**
     * Tìm Spa chỉ các voucher trong VoucherDAO.
     *
     * @return kết quả tìm Spa chỉ các voucher
     */
    List<Voucher> timSpaChiCacVoucher();

    /**
     * Tìm tất cả trong VoucherDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<Voucher> timTatCa();

    /**
     * Cập nhật trong VoucherDAO.
     *
     * @param voucher voucher
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(Voucher voucher);

    /**
     * Cập nhật trạng thái trong VoucherDAO.
     *
     * @param idVoucher id voucher
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idVoucher, String trangThai);

    /**
     * Tăng số lần sử dụng trong VoucherDAO.
     *
     * @param idVoucher id voucher
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean tangSoLanSuDung(Long idVoucher);

    /**
     * Xóa trong VoucherDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Lấy mã tồn tại theo voucher trong VoucherDAO.
     *
     * @param maVoucher mã voucher
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean layMaTonTaiTheoVoucher(String maVoucher);

    /**
     * Đếm trong VoucherDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
