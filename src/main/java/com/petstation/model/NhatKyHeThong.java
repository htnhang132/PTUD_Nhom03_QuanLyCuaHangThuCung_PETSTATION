package com.petstation.model;

import java.time.LocalDateTime;

/**
 * Biểu diễn dữ liệu ánh xạ với bảng nhat_ky_he_thong trong SQL Server.
 *
 * @param id id
 * @param idNguoiThucHienTaiKhoan id người thực hiện tài khoản
 * @param thaoTac thao tác
 * @param kieuThucThe kiểu thực thể
 * @param idThucThe id thực thể
 * @param giaTriCu giá trị cũ
 * @param giaTriMoi giá trị mới
 * @param ghiChu ghi chú
 * @param thoiDiemXayRa thời điểm xảy ra
 */
public record NhatKyHeThong(
        long id,
        Long idNguoiThucHienTaiKhoan,
        String thaoTac,
        String kieuThucThe,
        String idThucThe,
        String giaTriCu,
        String giaTriMoi,
        String ghiChu,
        LocalDateTime thoiDiemXayRa) {}
