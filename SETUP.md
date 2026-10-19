# Chạy Pet Station với SQL Server

Dự án dùng JDK 17+, Maven và SQL Server 2019+. VS Code dùng Extension Pack for Java, mở thư mục chứa `pom.xml`.

Cấu trúc package và nơi sửa từng chức năng được mô tả trong [ARCHITECTURE.md](docs/ARCHITECTURE.md). Đợt sắp xếp package không thay đổi schema hoặc dữ liệu; không cần chạy lại hai file SQL vì thay đổi này.

## 1. Tạo hoặc nâng cấp database

Trong PowerShell tại thư mục dự án:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/setup-database.ps1
```

Script dùng quyền Windows hiện tại để kết nối `localhost,1433` và chuẩn bị `PetStationDemo2`. Có thể chỉ định `-Server` và `-Database`.

Thư mục `database` chỉ có hai file, chạy theo thứ tự:

1. `001_schema.sql`: bảng, khóa chính, khóa ngoại, ràng buộc, chỉ mục và các bổ sung cho database cũ.
2. `002_sample_data.sql`: quyền, ca làm việc, cài đặt và dữ liệu mẫu cho danh mục, lịch hẹn, Hotel, nhập kho, hóa đơn, điểm khách hàng.

Nếu dùng SSMS, chọn database đích rồi chạy hai file trên. Các script có thể chạy lại, không xóa bảng và không ghi đè bản ghi đã có. Phiên bản `005_schema_v2.sql` cũ đã được hợp nhất; ứng dụng thống nhất sử dụng khóa `id`.

## 2. Cấu hình kết nối ứng dụng

Sao chép `src/main/resources/db.properties.example` thành `db.properties` rồi đặt URL, tài khoản và mật khẩu SQL Server phù hợp. Cấu hình hiện có của máy này được giữ nguyên. Có thể dùng các biến môi trường để ghi đè:

```powershell
$env:PETSTATION_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=PetStationDemo2;encrypt=true;trustServerCertificate=true'
$env:PETSTATION_DB_USER = 'your_sql_login'
$env:PETSTATION_DB_PASSWORD = 'your_sql_password'
```

Tài khoản ứng dụng hiện có vẫn dùng được. Các tài khoản `demo.admin`, `demo.sales`, `demo.stock`, `demo.spa` được tạo ở trạng thái **khóa, chưa có mật khẩu đăng nhập**. Quản lý vào **Hệ thống → Tài khoản**, đặt mật khẩu riêng rồi mở khóa nếu cần sử dụng.

Với database hoàn toàn mới chưa có quản lý đang hoạt động, tạo quản lý đầu tiên bằng tiện ích tương tác:

```powershell
mvn -B -ntp package
java -cp target/petstation.jar com.petstation.util.KhoiTaoQuanTri
```

Tiện ích hỏi tên đăng nhập, họ tên, số điện thoại và mật khẩu trong terminal. Không có mật khẩu chung được kích hoạt tự động.

## 3. Chạy ứng dụng

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/run-java.ps1
```

Hoặc nhấn **F5 → Pet Station**, hoặc **Run Code** khi mở `App.java` trong VS Code. Chạy bản đóng gói bằng `java -jar target/petstation.jar`.

Mở ca trước khi bán hàng hoặc thu tiền dịch vụ. Các màn danh mục, lịch, phê duyệt, báo cáo và lịch sử truy vấn SQL Server; tuần hoặc kỳ chưa có dữ liệu sẽ hiển thị trống/0. Giỏ đang soạn được giữ trong phiên; **Treo đơn** lưu giỏ vào SQL Server để có thể mở lại sau khi khởi động lại.

Spa: đặt lịch → chọn lịch và tiếp nhận → thêm dịch vụ theo loài/cân nặng → bắt đầu → hoàn thành → thanh toán.

Hotel: đặt phòng → nhận phòng → ghi nhật ký chăm sóc → quyết toán → thanh toán → dọn phòng xong. Giá phòng được chốt khi đặt, tiền cọc được trừ khi quyết toán.

Nhập/xuất/kiểm kê thao tác theo lô. Xuất hủy vượt ngưỡng, kiểm kê có chênh lệch, đổi trả quá hạn/vượt ngưỡng, nghỉ phép và sửa/hủy hóa đơn được lưu vào phê duyệt. Phê duyệt vẫn xử lý được sau khi khởi động lại. Yêu cầu sửa hóa đơn chỉ chỉnh ghi chú; thay đổi tài chính dùng hủy hoặc đổi trả để giữ lịch sử.

## 4. Kiểm thử

```powershell
mvn -B -ntp test
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-sqlserver.ps1
```

Lệnh đầu chạy unit test; kiểm thử SQL được bỏ qua nếu chưa bật biến môi trường. Lệnh thứ hai tạo/nâng cấp database riêng `PetStationIntegrationTest`, bật kiểm thử tích hợp và chạy toàn bộ bộ test. Từng tình huống tích hợp được rollback, không dùng database bán hàng của ứng dụng.

Các tình huống gồm CRUD danh mục, lịch sử giá, FEFO, thiếu tồn và rollback, chống thanh toán lặp, voucher/điểm, phê duyệt, lịch/nghỉ phép, Spa, Hotel, nhập kho, đổi trả, phân quyền báo cáo và khởi tạo toàn bộ màn hình bằng dữ liệu SQL thật.
