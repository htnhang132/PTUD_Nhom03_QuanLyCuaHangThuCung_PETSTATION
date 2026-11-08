package com.petstation.dao;

import java.util.List;

// Giao diện DAO phục vụ kiểm tra bước quy trình Spa/Lưu trú và tải dữ liệu Sơ đồ phòng lưu trú.
public interface QuyTrinhDAO {

    // 1. KIỂM TRA TRẠNG THÁI QUY TRÌNH SPA & LỊCH HẸN

    // Lấy trạng thái phiếu tiếp nhận Spa và kiểm tra phiếu đã chọn gói dịch vụ hay chưa
    List<Object[]> laySpa(long id);

    // Lấy thông tin lịch hẹn đã xác nhận theo mã/ID lịch hẹn
    List<Object[]> layLichHen(long id);

    // 2. KIỂM TRA TRẠNG THÁI LƯU TRÚ & TẢI SƠ ĐỒ PHÒNG HOTEL

    // Lấy trạng thái hiện tại của hợp đồng lưu trú (DA_DAT, DANG_O, DA_TRA_PHONG...)
    String layTrangThaiHotel(long id);

    // Lấy danh sách phòng kèm số lượng đặt trước và mã hợp đồng đang hoạt động
    List<Object[]> layPhong();

    // Lấy danh sách chi tiết tất cả các phòng trong bảng phong_hotel
    java.util.List<Object[]> layPhongChiTiet();

    // Lấy danh sách các hợp đồng lưu trú đang ở (DANG_O) hoặc đã đặt trước (DA_DAT)
    java.util.List<Object[]> layLichPhong();
}
