# Chuẩn hóa SQL Pet Station theo học phần Bảo mật Cơ sở dữ liệu

## 1. Phạm vi thay đổi

Hai file schema và sample data đã được chuẩn hóa trên nhánh riêng để review, không cập nhật trực tiếp vào main hoặc develop.

- `database/001_schema.sql`: thêm `SET NOCOUNT ON`, kiểm tra database đích và trạng thái database trống; bỏ các dấu phân tách batch `GO` trong phần DDL để các lệnh tạo bảng/khóa/chỉ mục nằm trong một `TRY/CATCH` và một transaction; nếu phát sinh lỗi khi thực thi thì rollback toàn bộ DDL.
- `database/002_sample_data.sql`: thêm kiểm tra database và schema; dừng nếu các bảng dbo đã có dữ liệu; giữ nguyên phần từ khai báo biến `@homNay` trở xuống, gồm toàn bộ giá trị mẫu và transaction hiện tại.
- `README.md`, `SETUP.md`: sửa mô tả không chính xác nói rằng file seed xóa dữ liệu cũ. Script seed hiện tại không có `DELETE` hay `TRUNCATE`.

Không thay đổi giá trị dữ liệu mẫu, danh sách cột trong INSERT, ID mẫu, giá/đơn vị, ngày tương đối, các quy tắc nghiệp vụ hay nội dung các định nghĩa bảng/khóa/chỉ mục. Phần DDL trong file 001 chỉ được đặt trong cơ chế transaction và bỏ batch separator `GO` đang ngăn TRY/CATCH bao trọn script.

## 2. Nội dung chuẩn hóa và lý do

### File 001_schema.sql

1. **Chạy đúng database**: script từ chối các database hệ thống `master`, `model`, `msdb`, `tempdb`.
2. **Fail-fast trên database đã có bảng**: yêu cầu database ứng dụng trống, không tự động drop bảng hoặc ghi đè schema cũ.
3. **Tính nguyên tử của DDL**: các lệnh tạo bảng, ràng buộc và index chạy trong một transaction. `XACT_STATE()` được dùng để rollback nếu transaction ở trạng thái không thể commit.
4. **Tính toàn vẹn**: các khóa chính/ngoại, UNIQUE, CHECK và index trong schema gốc được giữ lại. Chúng giúp ràng buộc tính hợp lệ ở tầng cơ sở dữ liệu, không chỉ dựa vào giao diện.

### File 002_sample_data.sql

1. **Kiểm tra trước khi nạp**: cần có bảng lõi `dbo.san_pham` và `dbo.cai_dat`, thể hiện schema đã được tạo.
2. **Tránh chạy seed lặp**: kiểm tra số dòng trong các bảng dbo trước khi INSERT. Nếu đã có dữ liệu, script dừng và thông báo; nó không xóa dữ liệu cũ.
3. **Transaction và xử lý lỗi**: giữ nguyên `SET XACT_ABORT ON`, `BEGIN TRY/BEGIN TRANSACTION`, `COMMIT`, `BEGIN CATCH/ROLLBACK/THROW`. Nếu một INSERT thất bại, transaction bị rollback thay vì để lại một phần bộ dữ liệu mẫu.
4. **Giữ nguyên dữ liệu**: mọi câu lệnh seed từ `DECLARE @homNay date=CAST(GETDATE() AS date);` đến hết file được giữ nguyên trong bản chuẩn hóa.

## 3. Đối chiếu các PDF đã cung cấp

| Tài liệu | Nội dung lý thuyết/thực hành liên quan | Áp dụng vào file | Trạng thái |
|---|---|---|---|
| `C01_TongQuan.pdf` | CIA, bảo vệ tính toàn vẹn, phân quyền, giám sát và lập trình an toàn | Guard database, ràng buộc và index schema; không tự động phá hủy dữ liệu | Áp dụng một phần |
| `C02A_DirectionAccessControl.pdf` | DAC và quyền trên các đối tượng cơ sở dữ liệu | Schema tạo các đối tượng cần được bảo vệ; GRANT/REVOKE nên được quản lý riêng theo login/user của môi trường | Giải thích riêng, không nhúng cấp quyền vào seed |
| `C02B_MandatoryAccessControlModels.pdf` | MAC, nhãn bảo mật và chính sách đọc/ghi bắt buộc | Schema hiện không định nghĩa nhãn bảo mật hoặc chính sách phân loại dữ liệu đa mức | Chưa triển khai; không nên tuyên bố hai script đã có MAC |
| `C02C_RolebasedAccessControlModels.pdf` | RBAC, user-role-permission, least privilege | Giải thích trong tài liệu mẫu quyền bên ngoài; ứng dụng hiện có vai trò và kiểm tra quyền ở tầng Java | Một phần ở ứng dụng; cần cấu hình DB riêng nếu triển khai RBAC tại SQL Server |
| `C03_LapTrinhVoiT_SQL_Ham_ThuTuc.pdf` | Batch, biến, TRY/CATCH, THROW/RAISERROR, hàm và thủ tục | Cấu trúc script có guard, TRY/CATCH và THROW; không thêm thủ tục nghiệp vụ vào schema khi ứng dụng hiện truy cập dữ liệu bằng DAO | Áp dụng TRY/CATCH; ví dụ thủ tục ở file hướng dẫn |
| `C04_KhaiThacPhongChongLoHongSQL Injection.pdf` | SQL injection do nối chuỗi; tham số hóa và validation | Hai file SQL là script tĩnh, không nhận đầu vào từ người dùng. Khi viết truy vấn cho ứng dụng phải dùng tham số JDBC/PreparedStatement hoặc stored procedure có tham số, không nối input vào SQL | Giải thích và ví dụ riêng |
| `C05_KiemSoatGiaoDichTransaction_Chinh.pdf` | ACID, BEGIN/COMMIT/ROLLBACK, TRY/CATCH, XACT_STATE | File 001 gom DDL vào một transaction; file 002 giữ transaction seed và rollback khi lỗi | Áp dụng |
| `C06_AuditDatabase.pdf` | Ai làm gì, đối tượng nào, thời gian nào; audit trail, trigger và SQL Server Audit | Giữ nguyên bảng `nhat_ky_he_thong` và index hiện có. Việc ghi log nghiệp vụ đang có ở tầng ứng dụng; không tự thêm trigger trùng lặp | Một phần; cần kiểm tra độ phủ sự kiện |
| `C07_MaHoaEncryption_XML_security_Chinh.pdf` | Mã hóa đối xứng, TDE, Always Encrypted, hash | Giữ nguyên cột `tai_khoan.mat_khau_hash` và các hash mẫu. Dự án dùng BCrypt ở tầng Java; không thay bằng mật khẩu rõ hoặc SHA-256 nhanh trong SQL. TDE/Always Encrypted cần khóa/chứng chỉ và cấu hình triển khai | Không bật mã hóa mới trong hai file |
| `2026_07_31_BTTHBMCSDL_SQL_DaiHocKTPM_V1.pdf` | Phân quyền, T-SQL, SQLi, transaction, audit, encryption/hash, backup/restore, masking, replication | Hai script thực hành chủ yếu về schema và seed; các nội dung phụ thuộc môi trường như login, audit SQL Server, TDE, masking, backup và replication không được bật tự động | Phân loại trong file hướng dẫn |

Phần số trang/tuần của file bài tập thực hành được đối chiếu theo mục lục của tài liệu: phân quyền (tuần 1-2), lập trình T-SQL (tuần 3), SQL injection (tuần 4), transaction (tuần 5), trigger/auditing (tuần 6), mã hóa/hash (tuần 7), backup/restore (tuần 8), masking (tuần 9), replication/mirroring (tuần 10).

## 4. Các nội dung bảo mật không nên nhúng trực tiếp vào hai file setup

- **GRANT/REVOKE/CREATE LOGIN/CREATE USER** phụ thuộc tên login SQL thực tế và môi trường. Không đưa mật khẩu thật hay login cá nhân vào repository.
- **MAC** cần chính sách nhãn bảo mật, đối tượng và chủ thể rõ ràng; không thể coi chỉ có bảng/role là đã triển khai MAC.
- **SQL Server Audit** cần quyền server-level và đường dẫn lưu audit trên máy SQL Server. Audit Trigger cũng cần xác định bảng/cột và quy tắc giữ lịch sử trước khi bật.
- **TDE/Always Encrypted** cần quản lý certificate/key và backup khóa. Không nên đưa mật khẩu master key hoặc khóa thật vào Git.
- **Dynamic Data Masking** làm thay đổi dữ liệu người dùng thấy qua query; phải kiểm thử quyền và yêu cầu hiển thị trước khi bật.
- **Backup/restore, replication, mirroring/Always On** là cấu hình vận hành, không phải thay đổi seed data.

Các mẫu mã không tự chạy được đặt trong `docs/SQL_SECURITY_EXAMPLES.md` để đọc và triển khai có chủ đích sau khi review.

## 5. Cách chạy sau khi review

1. Tạo database demo/test mới và chọn database đó trong SSMS.
2. Chạy toàn bộ `database/001_schema.sql`.
3. Chỉ khi bước 2 thành công, chạy `database/002_sample_data.sql`.
4. Nếu seed báo bảng đã có dữ liệu, dừng lại. Không xóa dữ liệu để vượt qua guard; tạo database demo mới nếu cần nạp bộ mẫu.
5. Kiểm tra bảng, FK, index và số dòng mẫu sau khi chạy.

Hai file này chỉ thiết lập schema và dữ liệu mẫu. Chúng không tự động thiết lập mọi biện pháp bảo mật được dạy trong các PDF.
