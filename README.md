# Pet Station — Quản lý cửa hàng thú cưng

Đồ án Phát triển ứng dụng, Nhóm 03. Ứng dụng Java Swing dành cho nhân viên, quản lý bán hàng, kho, Spa và Hotel thú cưng.

GitHub: [PTUD Nhóm 03 — Pet Station](https://github.com/htnhang132/PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION).

# PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION
Hệ thống Quản lý Cửa hàng Thú cưng - Đồ án môn Phát triển Ứng dụng (10/08/2026 - 27/10/2026).
### 📊 Sơ đồ Phân cấp Chức năng Hệ thống
![Sơ đồ phân cấp](./docs/diagrams/so-do-phan-cap-chuc-nang-ver2.png)

## Chạy dự án

Cần **JDK 17 trở lên** và **SQL Server 2019 trở lên**. Dự án có Maven Wrapper 3.9.11; không cần chép thư viện JAR hoặc cấu hình riêng theo IDE.

```powershell
./mvnw.cmd -v
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/setup-database.ps1
New-Item -ItemType Directory -Force config | Out-Null
Copy-Item src/main/resources/db.properties.example config/db.properties
# Sửa tài khoản SQL Server trong config/db.properties trước khi chạy.
./mvnw.cmd -B -ntp package
java -jar target/petstation.jar
```

Hướng dẫn đầy đủ cho VS Code, IntelliJ, Eclipse, SQL Authentication và các lỗi thường gặp: **[SETUP.md](SETUP.md)**. Nếu đã có database cũ, xem mục reset trước khi chạy; lệnh mặc định không tự xóa database cũ.

Tài khoản ứng dụng mẫu sau khi nạp SQL mới:

| Tài khoản | Vai trò |
| --- | --- |
| `demo.admin` | Quản lý |
| `demo.sales` | Bán hàng |
| `demo.stock` | Kho |
| `demo.spa` | Spa–Hotel |

Mật khẩu mẫu: **`PetStation@123`**; phải đổi khi đăng nhập lần đầu. Đây là tài khoản ứng dụng, khác tài khoản đăng nhập SQL Server. Bộ mẫu cũng giữ tài khoản quản lý `admin` với cùng mật khẩu ban đầu.

## Tài liệu

- [Cấu trúc và luồng xử lý](docs/ARCHITECTURE.md)
- [Kịch bản kiểm thử và demo](docs/KIEM_THU_VA_YEU_CAU.md)
- [Quy ước code và làm việc nhóm](CONTRIBUTING.md)
- [Nhật ký sử dụng AI](AI_USAGE_LOG.md)
- [Nguồn sản phẩm và ảnh](docs/NGUON_SAN_PHAM.md)

Database chỉ có `001_schema.sql` tạo bảng/khóa/chỉ mục và `002_sample_data.sql` nạp mẫu. File dữ liệu mẫu xóa dữ liệu ứng dụng trước khi nạp lại; chỉ dùng cho demo/test. Không dùng tài khoản và mật khẩu mẫu để vận hành thật.
