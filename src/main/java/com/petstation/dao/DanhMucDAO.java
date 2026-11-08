package com.petstation.dao;

import com.petstation.model.LoaiDanhMuc;

import java.util.*;

// Giao diện DAO dùng chung cho các màn hình Quản lý Danh mục
public interface DanhMucDAO {

    // 1. NẠP DỮ LIỆU, LƯU BẢN GHI DANH MỤC & TIỆN ÍCH TRA CỨU MÃ / ID

    // Nạp danh sách dòng dữ liệu hiển thị lên bảng theo từng LoaiDanhMuc
    List<Object[]> nap(LoaiDanhMuc loai);

    // Thêm mới hoặc cập nhật bản ghi của một LoaiDanhMuc vào CSDL
    void luu(
            LoaiDanhMuc loai,
            Object[] truoc,
            Object[] giaTri,
            long idNguoiThucHien,
            String nguoiThucHienVaiTro);

    // Tách lấy mã định danh từ chuỗi lựa chọn dạng "MA - Tên hiển thị"
    String layMaTuLuaChon(Object luaChon);

    // Kiểm tra sự tồn tại của mã trong bảng và trả về ID cầu nối tương ứng
    long layId(String bang, String khoa, String ma);

    // 2. NGHIỆP VỤ BỔ TRỢ SẢN PHẨM & LỊCH SỬ GIÁ

    // Lấy danh sách lịch sử thay đổi giá bán của các sản phẩm
    List<Object[]> layLichSuGia();

    // Cập nhật mã vạch (Barcode) cho sản phẩm
    int capNhatBarcode(long sanPhamId, String barcode);

    // Kiểm tra sản phẩm đã từng phát sinh giao dịch bán hàng hay chưa
    boolean kiemTraSanPhamCoGiaoDich(long sanPhamId);

    // Chuyển trạng thái sản phẩm sang ngừng kinh doanh (NGUNG_BAN)
    int ngungKinhDoanhSanPham(long sanPhamId);

    // 3. NGHIỆP VỤ BỔ TRỢ TÀI KHOẢN & NHÂN VIÊN

    // Khóa tất cả tài khoản đăng nhập của nhân viên khi chuyển sang trạng thái nghỉ việc
    int khoaCacTaiKhoanCua(String maNhanVien);

    // Đặt lại mật khẩu mới (đã băm BCrypt) và mở khóa đăng nhập cho tài khoản
    void datLaiMatKhau(String tenDangNhap, String matKhau);

    // Tìm tên đăng nhập tương ứng với ID nhân viên
    String layTenDangNhapTheoNhanVien(long nhanVienId);

    // Tạo mới tài khoản đăng nhập cho nhân viên
    void taoTaiKhoanNhanVien(
            long nhanVienId, String tenDangNhap, String matKhauHash, String vaiTro);

    // 4. NGHIỆP VỤ BỔ TRỢ NHÀ CUNG CẤP (KIỂM TRA TRÙNG & NHẬT KÝ JSON)

    // Kiểm tra tên nhà cung cấp có bị trùng với nhà cung cấp khác hay không
    boolean tonTaiTenNcc(String ten, String maLoaiTru);

    // Kiểm tra số điện thoại nhà cung cấp có bị trùng với nhà cung cấp khác hay không
    boolean tonTaiSdtNcc(String soDienThoai, String maLoaiTru);

    // Xuất thông tin bản ghi nhà cung cấp ra chuỗi JSON để lưu lịch sử thay đổi
    String layAnhChupNcc(String maNcc);
}
