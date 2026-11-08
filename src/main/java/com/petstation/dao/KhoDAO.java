package com.petstation.dao;

import java.util.List;

// Giao diện truy xuất dữ liệu (DAO) cho các nghiệp vụ Kho
public interface KhoDAO {

    // 1. NGHIỆP VỤ NHẬP KHO & CÔNG NỢ NHÀ CUNG CẤP

    // Kiểm tra sản phẩm có bắt buộc theo dõi hạn sử dụng (HSD) hay không
    long demNhapKho(Object... cacThamSo);

    // Kiểm tra nhà cung cấp có đang ở trạng thái hợp tác hay không
    long demNhaCungCapDangHopTac(Object... cacThamSo);

    // Thêm mới phiếu nhập kho vào bảng phieu_nhap_kho
    long themNhapKho(Object... cacThamSo);

    // Thêm dòng chi tiết mặt hàng nhập vào bảng chi_tiet_phieu_nhap
    int themChiTietNhapKho(Object... cacThamSo);

    // Tìm kiếm lô hàng hiện có của sản phẩm theo số lô hoặc mã lô
    List<Object[]> timNhapKho(Object... cacThamSo);

    // Tạo lô hàng mới vào bảng lo_hang khi nhập lô chưa từng tồn tại
    int themLoHang(Object... cacThamSo);

    // Cộng thêm số lượng tồn và số lượng nhập khi nhập trùng vào lô hàng đã có
    int capNhatNhapKho(Object... cacThamSo);

    // Cập nhật giá nhập gần nhất cho sản phẩm trong bảng san_pham
    int capNhatGiaNhapGanNhat(Object... cacThamSo);

    // Lấy danh sách các dòng sản phẩm chi tiết của một phiếu nhập kho
    List<Object[]> layNhapKhoChiTiet(Object... cacThamSo);

    // Truy vấn số tiền công nợ còn lại (tong_tien - da_tra) của phiếu nhập kho
    long layCongNoConLai(Object... cacThamSo);

    // Cập nhật cộng thêm số tiền đã trả cho nhà cung cấp trên phiếu nhập kho
    int themThanhToanNhaCungCap(Object... cacThamSo);

    // 2. NGHIỆP VỤ XUẤT HỦY HÀNG HÓA

    // Tra cứu số lượng tồn hiện tại và đơn giá vốn của lô hàng cần xuất hủy
    List<Object[]> timXuatHuy(Object... cacThamSo);

    // Tạo mới phiếu xuất hủy vào bảng phieu_xuat_kho (trạng thái CHO_XUAT)
    long themXuatHuy(Object... cacThamSo);

    // Thêm dòng chi tiết lô hàng xuất hủy vào bảng chi_tiet_phieu_xuat
    int capNhatXuatHuy(Object... cacThamSo);

    // Kiểm tra phiếu xuất hủy có đang ở trạng thái chờ xuất (CHO_XUAT) để áp dụng hay không
    long demApDungXuatHuy(Object... cacThamSo);

    // Lấy danh sách các lô hàng và số lượng cần trừ tồn của phiếu xuất hủy
    List<Object[]> timApDungXuatHuy(Object... cacThamSo);

    // Trừ số lượng tồn thực tế trong bảng lo_hang khi duyệt phiếu xuất hủy
    int truTonXuatHuy(Object... cacThamSo);

    // Đánh dấu phiếu xuất hủy sang trạng thái đã xuất (DA_XUAT)
    int capNhatApDungXuatHuy(Object... cacThamSo);

    // 3. NGHIỆP VỤ KIỂM KÊ TỒN KHO

    // Lấy danh sách các lô hàng đang bán để hiển thị lên bảng kiểm kê định kỳ
    List<Object[]> layDanhSachLoHangKiemKe(Object... cacThamSo);

    // Tra cứu số lượng tồn hệ thống hiện tại của một lô hàng cụ thể
    List<Object[]> timKiemKe(Object... cacThamSo);

    // Tạo mới biên bản kiểm kê vào bảng bien_ban_kiem_ke
    long themKiemKe(Object... cacThamSo);

    // Thêm dòng chi tiết chênh lệch kiểm kê vào bảng chi_tiet_kiem_ke
    int capNhatKiemKe(Object... cacThamSo);

    // Lấy danh sách chi tiết các lô hàng trong một biên bản kiểm kê
    List<Object[]> layChiTietKiemKe(Object... cacThamSo);

    // Kiểm tra biên bản kiểm kê có đang ở trạng thái chờ duyệt (CHO_DUYET) hay không
    long demApDungKiemKe(Object... cacThamSo);

    // Lấy danh sách mã lô, tồn hệ thống và tồn thực tế của biên bản kiểm kê chờ duyệt
    List<Object[]> timApDungKiemKe(Object... cacThamSo);

    // Cập nhật cân bằng số lượng tồn của lô hàng theo tồn thực tế sau khi duyệt kiểm kê
    int capNhatApDungKiemKe(Object... cacThamSo);

    // Đánh dấu biên bản kiểm kê sang trạng thái đã duyệt (DA_DUYET)
    int danhDauKiemKeDaDuyet(Object... cacThamSo);
}
