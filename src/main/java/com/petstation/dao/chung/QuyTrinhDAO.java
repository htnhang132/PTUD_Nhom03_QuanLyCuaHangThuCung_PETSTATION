package com.petstation.dao.chung;

import com.petstation.dto.HotelDTO;
import com.petstation.dto.SpaDTO;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.model.spa.LichHen;

import java.util.List;
import java.util.Optional;

// Giao diện DAO phục vụ kiểm tra bước quy trình Spa/Lưu trú và tải dữ liệu Sơ đồ phòng lưu trú.
public interface QuyTrinhDAO {

    // 1. KIỂM TRA TRẠNG THÁI QUY TRÌNH SPA & LỊCH HẸN

    // Lấy trạng thái phiếu tiếp nhận Spa và kiểm tra phiếu đã chọn gói dịch vụ hay chưa
    Optional<SpaDTO.ThongTinBuocSpa> laySpa(String maPhieu);

    // Lấy thông tin lịch hẹn đã xác nhận theo mã lịch hẹn
    Optional<LichHen> layLichHen(String maLichHen);

    // 2. KIỂM TRA TRẠNG THÁI LƯU TRÚ & TẢI SƠ ĐỒ PHÒNG HOTEL

    // Lấy trạng thái hiện tại của hợp đồng lưu trú (DA_DAT, DANG_O, DA_TRA_PHONG...)
    String layTrangThaiHotel(String maHopDong);

    // Lấy danh sách phòng kèm số lượng đặt trước và mã hợp đồng đang hoạt động
    List<HotelDTO.TomTatPhong> layPhong();

    // Lấy danh sách chi tiết tất cả các phòng trong bảng phong_hotel
    List<PhongHotel> layPhongChiTiet();

    // Lấy danh sách các hợp đồng lưu trú đang ở (DANG_O) hoặc đã đặt trước (DA_DAT)
    List<HotelDTO.ChiTietLichPhong> layLichPhong();
}
