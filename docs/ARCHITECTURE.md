# Rà soát khôi phục nhánh develop

> **Trạng thái:** Đã hợp nhất vào `develop` ngày 28/10/2026.  
> **PR:** [#43 — Restore missing sources and build setup](https://github.com/htnhang132/PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/pull/43)  
> **CI:** [Verify Java — success](https://github.com/htnhang132/PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/actions/runs/40689136445)

Tài liệu này ghi lại nội dung khôi phục và phạm vi kiểm tra. Cây thư mục/ranh giới tầng đầy đủ nằm ở [`../ARCHITECTURE.md`](../ARCHITECTURE.md).

## 1. Nguyên nhân chính

Khi đối chiếu `develop` với `vi`, `develop` đã thiếu nhiều thành phần mà các lớp nghiệp vụ còn lại cần để build và chạy ứng dụng: entry point `com.petstation.App`, các DAO interface, package kết nối/giao dịch `com.petstation.database`, model và màn hình giao diện, cùng cấu hình Maven và tài nguyên.

Nhánh `develop` cũng có hai lớp trong package `com.petstore` thuộc một skeleton cũ. `MainApplication` chỉ có TODO, còn `DatabaseConnection` chứa thông tin SQL mặc định trong code và thay đổi cấu hình TLS ở phạm vi toàn JVM. Hai lớp này không phải điểm khởi chạy của Pet Station và đã được loại khỏi bản khôi phục.

## 2. Các phần đã khôi phục

- Source Java còn thiếu trong các nhóm DAO, database, GUI, model, service và util, sử dụng các bản có trong nhánh `vi` khi đường dẫn chưa tồn tại ở `develop`.
- Các DAO interface cần cho các implementation hiện dùng hậu tố `*DAOImpl`; không thêm lại các file `*Impl` cũ song song.
- `pom.xml` và Maven Wrapper; dependencies cho giao diện, SQL Server, thao tác Excel/mã QR, logging và test.
- `database/001_schema.sql`, `database/002_sample_data.sql`, script thiết lập/kiểm tra SQL Server và `SETUP.md`.
- Tài nguyên GUI: logo, ảnh sản phẩm, cấu hình mẫu `db.properties.example` và logging.
- Tài liệu và cấu hình nhóm phù hợp với quy trình build/kiểm tra hiện có.
- `.gitignore` được bổ sung quy tắc loại trừ cấu hình database local; các file `target/classes` đã commit trước đó được loại bỏ khỏi source tree.

Các thay đổi được gom trong PR #43 và squash-merge vào `develop` tại commit `9e4d330c7bb8250222d3eb9564e85a6f614b78e8`.

## 3. Điểm khởi chạy và luồng giao diện

Điểm khởi chạy hiện là `com.petstation.App`.

```text
com.petstation.App
  -> thiết lập FlatLaf
  -> khởi tạo ngữ cảnh ứng dụng
  -> nạp cấu hình và xác minh SQL Server
  -> mở DangNhapFrame
  -> xác thực tài khoản
  -> mở ChinhFrame
  -> điều hướng các màn hình theo quyền
```

Vì ứng dụng xác minh kết nối SQL trước khi mở cửa sổ đăng nhập, máy chạy cần có SQL Server hoạt động và file cấu hình hợp lệ. Nếu database chưa sẵn sàng, ứng dụng sẽ báo lỗi khởi động chứ không chuyển sang một chế độ giả lập không có dữ liệu.

## 4. Kết quả CI

Workflow [Verify Java](https://github.com/htnhang132/PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/actions/runs/40689136445) đã hoàn tất **success** với bốn job:

| Hệ điều hành | JDK | Kết quả |
| --- | --- | --- |
| Ubuntu | 17 | Thành công |
| Ubuntu | 21 | Thành công |
| Windows | 17 | Thành công |
| Windows | 21 | Thành công |

Mỗi job vượt qua kiểm tra định dạng, build và các test không cần SQL Server. Đây là bằng chứng source biên dịch và test CI đã thành công trong bốn cấu hình trên.

**Giới hạn:** CI không chạy bộ tích hợp SQL Server vì cần database kiểm thử riêng, cũng không tự mở cửa sổ Swing để kiểm tra bằng mắt. Vì vậy, chưa thể khẳng định đăng nhập và mọi luồng nghiệp vụ đã hoạt động với cấu hình SQL Server trên máy cá nhân của người dùng.

## 5. Cách kiểm tra tại máy Windows

1. Cài JDK 17 trở lên và SQL Server 2019 trở lên.
2. Theo mục cài đặt trong [`SETUP.md`](../SETUP.md), chạy script database với database demo/test phù hợp.
3. Tạo cấu hình local từ mẫu:

```powershell
New-Item -ItemType Directory -Force config | Out-Null
Copy-Item src/main/resources/db.properties.example config/db.properties
```

4. Sửa `config/db.properties` bằng server, database, SQL login và mật khẩu của chính máy. Không commit tệp này.
5. Chạy build/test và khởi động:

```powershell
./mvnw.cmd -B -ntp verify
./mvnw.cmd -B -ntp package
java -jar target/petstation.jar
```

Hoặc chạy lớp `com.petstation.App` từ IDE với working directory là thư mục gốc dự án.

Để kiểm tra các ca SQL tích hợp, dùng `scripts/test-sqlserver.ps1` với database riêng `PetStationIntegrationTest`. Không dùng seed/reset trên database thật: `002_sample_data.sql` thay dữ liệu ứng dụng trong database đích.

## 6. Việc còn cần làm thủ công

- Xác nhận ứng dụng mở được cửa sổ đăng nhập trên máy có SQL Server cấu hình đúng.
- Thử đăng nhập bằng tài khoản demo trên database mẫu rồi kiểm tra phân quyền và điều hướng màn hình.
- Chạy script kiểm thử SQL tích hợp trên database test riêng.
- Ghi lại lỗi thực tế nếu có; không đưa mật khẩu hoặc file cấu hình thật lên GitHub.

