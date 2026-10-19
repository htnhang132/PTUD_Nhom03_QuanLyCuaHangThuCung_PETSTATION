package com.petstation.dao;

import com.petstation.model.PhieuTiepNhanSpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Khai báo thao tác truy cập dữ liệu của phiếu tiếp nhận Spa DAO. */
public interface PhieuTiepNhanSpaDAO {

    /**
     * Tạo trong PhieuTiepNhanSpaDAO.
     *
     * @param phieuTiepNhanSpa phiếu tiếp nhận Spa
     * @return kết quả tạo
     */
    PhieuTiepNhanSpa tao(PhieuTiepNhanSpa phieuTiepNhanSpa);

    /**
     * Tìm theo id trong PhieuTiepNhanSpaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    Optional<PhieuTiepNhanSpa> timTheoId(Long id);

    /**
     * Tìm theo mã phiếu Spa trong PhieuTiepNhanSpaDAO.
     *
     * @param maPhieuSpa mã phiếu Spa
     * @return kết quả tìm theo mã phiếu Spa
     */
    Optional<PhieuTiepNhanSpa> timTheoMaPhieuSpa(String maPhieuSpa);

    /**
     * Tìm theo id thú cưng trong PhieuTiepNhanSpaDAO.
     *
     * @param idThuCung id thú cưng
     * @return kết quả tìm theo id thú cưng
     */
    List<PhieuTiepNhanSpa> timTheoIdThuCung(Long idThuCung);

    /**
     * Tìm theo id khách hàng trong PhieuTiepNhanSpaDAO.
     *
     * @param idKhachHang id khách hàng
     * @return kết quả tìm theo id khách hàng
     */
    List<PhieuTiepNhanSpa> timTheoIdKhachHang(Long idKhachHang);

    /**
     * Tìm theo id gói dịch vụ trong PhieuTiepNhanSpaDAO.
     *
     * @param idGoiDichVu id gói dịch vụ
     * @return kết quả tìm theo id gói dịch vụ
     */
    List<PhieuTiepNhanSpa> timTheoIdGoiDichVu(Long idGoiDichVu);

    /**
     * Tìm theo id nhân viên trong PhieuTiepNhanSpaDAO.
     *
     * @param idNhanVien id nhân viên
     * @return kết quả tìm theo id nhân viên
     */
    List<PhieuTiepNhanSpa> timTheoIdNhanVien(Long idNhanVien);

    /**
     * Tìm theo trạng thái trong PhieuTiepNhanSpaDAO.
     *
     * @param trangThai trạng thái
     * @return kết quả tìm theo trạng thái
     */
    List<PhieuTiepNhanSpa> timTheoTrangThai(String trangThai);

    /**
     * Tìm theo khoảng ngày trong PhieuTiepNhanSpaDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    List<PhieuTiepNhanSpa> timTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc);

    /**
     * Tìm đang chờ các phiếu Spa trong PhieuTiepNhanSpaDAO.
     *
     * @return kết quả tìm đang chờ các phiếu Spa
     */
    List<PhieuTiepNhanSpa> timDangChoCacPhieuSpa();

    /**
     * Tìm vào tiến độ các phiếu Spa trong PhieuTiepNhanSpaDAO.
     *
     * @return kết quả tìm vào tiến độ các phiếu Spa
     */
    List<PhieuTiepNhanSpa> timVaoTienDoCacPhieuSpa();

    /**
     * Tìm tất cả trong PhieuTiepNhanSpaDAO.
     *
     * @return kết quả tìm tất cả
     */
    List<PhieuTiepNhanSpa> timTatCa();

    /**
     * Cập nhật trong PhieuTiepNhanSpaDAO.
     *
     * @param phieuTiepNhanSpa phiếu tiếp nhận Spa
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhat(PhieuTiepNhanSpa phieuTiepNhanSpa);

    /**
     * Cập nhật trạng thái trong PhieuTiepNhanSpaDAO.
     *
     * @param idPhieuSpa id phiếu Spa
     * @param trangThai trạng thái
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatTrangThai(Long idPhieuSpa, String trangThai);

    /**
     * Cập nhật thời gian trả phòng trong PhieuTiepNhanSpaDAO.
     *
     * @param idPhieuSpa id phiếu Spa
     * @param thoiGianTraPhong thời gian trả phòng
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean capNhatThoiGianTraPhong(Long idPhieuSpa, LocalDateTime thoiGianTraPhong);

    /**
     * Xóa trong PhieuTiepNhanSpaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    boolean xoa(Long id);

    /**
     * Đếm trong PhieuTiepNhanSpaDAO.
     *
     * @return kết quả đếm
     */
    long dem();
}
