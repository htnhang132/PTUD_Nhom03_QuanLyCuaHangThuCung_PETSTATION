# Mẫu triển khai DAO JDBC

Các interface nằm trực tiếp trong `com.petstation.dao`; lớp triển khai nằm trong `dao.impl`. Tên nghiệp vụ thống nhất với SQL: `KhachHangDAO`, `JdbcKhachHangDAO`, `HopDongLuuTruDAO`, `JdbcHopDongLuuTruDAO`. Xem [bảng đối chiếu đầy đủ](TEN_MA_NGUON.md).

## Phân chia trách nhiệm

- Model biểu diễn dữ liệu; DAO chứa câu SQL, tham số JDBC và ánh xạ kết quả.
- Service kiểm tra quyền, dữ liệu đầu vào và phối hợp transaction; GUI gọi service.
- Không ghép trực tiếp dữ liệu người dùng vào SQL. Dùng PreparedStatement hoặc tiện ích `ThaoTacSQL` có tham số.
- Giữ tên bảng/cột và mã trạng thái của schema. Không thêm danh sách giả khi SQL thất bại.

## Ví dụ truy vấn khách hàng

Đoạn sau minh họa một phương thức thuộc lớp triển khai `JdbcKhachHangDAO` có trường kết nối `ketNoi`; không phải lớp thay thế toàn bộ interface.

```java
/**
 * Tìm khách hàng theo khóa chính trong bảng khach_hang.
 * @param id khóa chính khách hàng
 * @return khách hàng nếu tìm thấy; Optional.empty() nếu không tồn tại
 * @throws TruyCapDuLieuException nếu truy vấn SQL Server thất bại
 */
@Override
public Optional<KhachHang> timTheoId(Long id) {
    String cauLenhSQL = "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai "
            + "FROM khach_hang WHERE id = ?";
    try (PreparedStatement cauLenh = ketNoi.prepareStatement(cauLenhSQL)) {
        cauLenh.setLong(1, id);
        try (ResultSet ketQua = cauLenh.executeQuery()) {
            if (!ketQua.next()) return Optional.empty();
            return Optional.of(new KhachHang(
                    ketQua.getLong("id"), ketQua.getString("ma_khach_hang"),
                    ketQua.getString("ho_ten"), ketQua.getString("so_dien_thoai")));
        }
    } catch (SQLException loi) {
        throw new TruyCapDuLieuException("Không thể tìm khách hàng.", loi);
    }
}
```

Với truy vấn nghiệp vụ qua `ThaoTacSQL`, service mở `thucHienGiaoDich` để các DAO dùng chung kết nối hiện tại. `QuanLyGiaoDich` phục vụ các service nhận DAO qua constructor. Duy trì cách phối hợp hiện có khi sửa nghiệp vụ; không mở một kết nối khác giữa giao dịch cần nguyên tử.

## Kiểm tra

Chạy `mvn clean package`. Kiểm thử SQL chỉ chạy với `PetStationIntegrationTest` và hoàn tác dữ liệu từng tình huống. Không chạy file tạo bảng hay dữ liệu mẫu trên database ứng dụng khi chỉ đổi tên hoặc sắp xếp mã.
