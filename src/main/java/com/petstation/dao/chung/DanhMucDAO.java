package com.petstation.dao.chung;

import com.petstation.dto.DongNghiepVu;
import com.petstation.model.chung.LoaiDanhMuc;

import java.util.List;
import java.util.Optional;

// Giao diện DAO dùng chung cho các màn hình Quản lý Danh mục
public interface DanhMucDAO {
    final class RangBuocVoucher {
        public final long daDung;
        public final String khoaDoiDiem;
        public final String maKhachHang;

        public RangBuocVoucher(long daDung, String khoaDoiDiem, String maKhachHang) {
            this.daDung = daDung;
            this.khoaDoiDiem = khoaDoiDiem;
            this.maKhachHang = maKhachHang;
        }
    }

    // 1. NẠP DỮ LIỆU, LƯU BẢN GHI DANH MỤC & TIỆN ÍCH TRA CỨU MÃ / ID

    // Nạp danh sách dòng dữ liệu hiển thị lên bảng theo từng LoaiDanhMuc
    List<DongNghiepVu> nap(LoaiDanhMuc loai);

    // Thêm mới hoặc cập nhật bản ghi của một LoaiDanhMuc vào CSDL
    void luu(
            LoaiDanhMuc loai,
            DongNghiepVu truoc,
            DongNghiepVu giaTri,
            String nguoiThucHien,
            String nguoiThucHienVaiTro);

    // Tách lấy mã định danh từ chuỗi lựa chọn dạng "MA - Tên hiển thị"
    String layMaTuLuaChon(String luaChon);

    // Kiểm tra bản ghi có tồn tại theo bảng và cột khóa hợp lệ hay không
    boolean tonTai(String bang, String khoa, String ma);

    // Xác nhận bản ghi tồn tại theo bảng và cột khóa hợp lệ, ném ngoại lệ nếu không thấy
    void xacNhanTonTai(String bang, String khoa, String ma);

    // 2. NGHIỆP VỤ BỔ TRỢ SẢN PHẨM

    // Cập nhật mã vạch (Barcode) cho sản phẩm
    int capNhatBarcode(String maSanPham, String barcode);

    // Kiểm tra sản phẩm đã từng phát sinh giao dịch bán hàng hay chưa
    boolean kiemTraSanPhamCoGiaoDich(String maSanPham);

    // Chuyển trạng thái sản phẩm sang ngừng kinh doanh (NGUNG_BAN)
    int ngungKinhDoanhSanPham(String maSanPham);

    // 3. NGHIỆP VỤ BỔ TRỢ TÀI KHOẢN & NHÂN VIÊN

    // Khóa tất cả tài khoản đăng nhập của nhân viên khi chuyển sang trạng thái nghỉ việc
    int khoaCacTaiKhoanCua(String maNhanVien);

    // Mở khóa tài khoản của nhân viên khi chuyển sang trạng thái đang làm việc
    int moKhoaTaiKhoanNhanVien(String maNhanVien);

    // Đặt lại mật khẩu mới (đã băm BCrypt) và mở khóa đăng nhập cho tài khoản
    void datLaiMatKhau(String tenDangNhap, String matKhau);

    // Tìm tên đăng nhập tương ứng với mã nhân viên
    String layTenDangNhapTheoNhanVien(String maNhanVien);

    // Tạo mới tài khoản đăng nhập cho nhân viên
    void taoTaiKhoanNhanVien(
            String maNhanVien, String tenDangNhap, String matKhauHash, String vaiTro);

    // 4. NGHIỆP VỤ BỔ TRỢ KHÁCH HÀNG & NHÀ CUNG CẤP

    // Kiểm tra khách hàng có thú cưng hoạt động / phiếu Spa / hợp đồng lưu trú đang mở hay không
    String kiemTraKhachHangDangGiaoDich(String maKhachHang);

    // Kiểm tra tên nhà cung cấp có bị trùng với nhà cung cấp khác hay không
    boolean tonTaiTenNcc(String ten, String maLoaiTru);

    // Kiểm tra số điện thoại nhà cung cấp có bị trùng với nhà cung cấp khác hay không
    boolean tonTaiSdtNcc(String soDienThoai, String maLoaiTru);

    // Xuất thông tin bản ghi nhà cung cấp ra chuỗi JSON để lưu lịch sử thay đổi
    String layAnhChupNcc(String maNcc);

    // Kiểm tra ràng buộc voucher đổi điểm & chủ sở hữu khi lưu từ danh mục
    RangBuocVoucher layRangBuocVoucher(String maVoucher);

    // Lấy giá bán hiện tại của sản phẩm (nếu đã tồn tại trong CSDL)
    Optional<Long> layGiaBanSanPham(String maSanPham);
}
