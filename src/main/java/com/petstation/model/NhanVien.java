package com.petstation.model;

import java.time.LocalDate;

/**
 * Biểu diễn dữ liệu ánh xạ với bảng nhan_vien trong SQL Server.
 *
 * @param id id
 * @param maNhanVien mã nhân viên
 * @param hoTen họ tên
 * @param ngaySinh ngày sinh
 * @param soDienThoai số điện thoại
 * @param diaChi địa chỉ
 * @param viTri vị trí
 * @param ngayBatDau ngày bắt đầu
 * @param hoatDong hoạt động
 */
public record NhanVien(
        long id,
        String maNhanVien,
        String hoTen,
        LocalDate ngaySinh,
        String soDienThoai,
        String diaChi,
        String viTri,
        LocalDate ngayBatDau,
        boolean hoatDong) {}
