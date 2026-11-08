package com.petstation.dao;

import java.util.List;

// Giao diện DAO phục vụ nghiệp vụ Tích điểm thành viên & Quản lý/Áp dụng Voucher.
public interface TichDiemDAO {

    // 1. KIỂM TRA RÀNG BUỘC & TRA CỨU VOUCHER

    // Lấy thông tin số lượt đã dùng, khóa đổi điểm và chủ sở hữu của mã voucher
    List<Object[]> layRangBuocVoucher(String ma);

    // Tìm thông tin chi tiết của một mã voucher để kiểm tra điều kiện áp dụng khi thanh toán
    List<Object[]> timVoucher(String ma);

    // Lấy danh sách tất cả voucher thuộc sở hữu của một khách hàng
    List<Object[]> layCuaKhach(String ma);

    // 2. ĐỔI ĐIỂM TÍCH LŨY LẤY VOUCHER SPA

    // Tìm thông tin điểm tích lũy và trạng thái hoạt động của khách hàng (có khóa dòng UPDLOCK)
    List<Object[]> timKhachHang(String ma);

    // Tìm voucher đã phát hành theo khóa đổi điểm (tránh phát hành trùng lặp)
    List<Object[]> timTheoKhoa(String khoa);

    // Tạo mới voucher Spa 150.000đ và trừ 200 điểm tích lũy của khách hàng
    String taoVoucher(long khachHang, String khoa);
}
