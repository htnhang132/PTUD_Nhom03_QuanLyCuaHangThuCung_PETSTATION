# Mẫu SQL bảo mật cho Pet Station (chỉ tham khảo)

> Các đoạn SQL dưới đây là ví dụ học tập, không được gọi tự động bởi 001_schema.sql hay 002_sample_data.sql. Đọc, chỉnh sửa và review quyền trước khi chạy. Không chạy trên production.

## 1. RBAC / quyền tối thiểu (C02A, C02C, bài thực hành tuần 1-2)

Ví dụ chỉ cấp quyền đọc audit log cho một role báo cáo. Người triển khai phải tạo/mapping user đúng môi trường trước khi thêm thành viên.

    USE [PetStationDev20261029];
    GO

    IF DATABASE_PRINCIPAL_ID(N'petstation_audit_reader') IS NULL
        CREATE ROLE [petstation_audit_reader] AUTHORIZATION [dbo];
    GO

    GRANT SELECT ON OBJECT::dbo.nhat_ky_he_thong TO [petstation_audit_reader];
    GO

Gán một database user đã được tạo và được phê duyệt vào role (thay tên placeholder, không dùng nguyên trạng):

    -- ALTER ROLE [petstation_audit_reader] ADD MEMBER [approved_audit_user];
    -- GO

Nguyên tắc: không cấp db_owner cho tài khoản ứng dụng chỉ để dễ chạy. Phân quyền database không thay thế kiểm tra quyền theo người dùng trong ứng dụng. DAC là quyền cấp trên đối tượng; RBAC gom các quyền vào role. MAC theo nhãn bảo mật không được triển khai bởi đoạn ví dụ này.

## 2. Tránh SQL Injection bằng tham số hóa (C04, bài thực hành tuần 4)

Ví dụ stored procedure không nối chuỗi đầu vào thành mã SQL động:

    CREATE OR ALTER PROCEDURE dbo.usp_PetStation_TimSanPham
        @TuKhoa NVARCHAR(100)
    AS
    BEGIN
        SET NOCOUNT ON;

        SELECT p.ma_san_pham, p.ten, p.barcode, p.gia_ban
        FROM dbo.san_pham AS p
        WHERE p.ma_san_pham = @TuKhoa
           OR p.barcode = @TuKhoa
           OR p.ten LIKE N'%' + @TuKhoa + N'%';
    END;
    GO

Cách này giữ input ở vai trò dữ liệu, không phải cú pháp SQL. Trong source Java của Pet Station, khi truy vấn trực tiếp từ DAO, ưu tiên `PreparedStatement` với placeholder `?`. Không tạo SQL bằng cách cộng chuỗi từ dữ liệu người dùng.

## 3. Giao dịch và rollback (C03, C05)

    SET XACT_ABORT ON;
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- Đặt các lệnh DML có liên quan vào cùng giao dịch.
        -- Thay đoạn minh họa bằng các thao tác đã được review.
        
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
    GO

`XACT_ABORT ON` giúp các lỗi runtime thích hợp làm transaction bị hủy; `XACT_STATE()` kiểm tra trạng thái giao dịch trước khi rollback. Không để giao dịch mở khi lỗi.

## 4. Kiểm tra nhật ký nghiệp vụ hiện có (C06)

    SELECT TOP (100)
        thoi_gian,
        hanh_dong,
        loai_doi_tuong,
        ma_doi_tuong,
        ghi_chu
    FROM dbo.nhat_ky_he_thong
    ORDER BY thoi_gian DESC;
    GO

Audit trả lời các câu hỏi: ai thực hiện, thao tác gì, đối tượng nào và thời điểm nào. Pet Station đã có bảng nhật ký và ghi log ở tầng Java; cần kiểm kê các nghiệp vụ còn thiếu sự kiện trước khi thêm trigger. Tránh tự động ghi trùng một thao tác bởi cả service và trigger.

## 5. Mã hóa và hash (C07, bài thực hành tuần 7)

Không thay hash BCrypt đang được ứng dụng dùng bằng mật khẩu rõ hoặc `HASHBYTES('SHA2_256', ...)` rồi kỳ vọng đăng nhập tiếp tục hoạt động. SHA-256 là hàm băm nhanh và không tương thích với bộ kiểm tra BCrypt trong ứng dụng. Cột `tai_khoan.mat_khau_hash` chứa hash đã được tạo ở tầng ứng dụng.

Kiểm tra trạng thái TDE hiện tại (chỉ đọc, không bật TDE):

    SELECT
        DB_NAME(database_id) AS database_name,
        encryption_state,
        encryptor_type
    FROM sys.dm_database_encryption_keys
    WHERE database_id = DB_ID();
    GO

TDE chỉ bảo vệ dữ liệu lưu trữ ở cấp database files/backup; không ngăn một truy vấn được cấp quyền đọc dữ liệu. Always Encrypted yêu cầu thiết kế cột, khóa và client phù hợp. Không tạo master key, certificate hoặc mật khẩu khóa tự động trong hai file setup.

## 6. Data masking, backup và SQL Server Audit

Các nội dung này được học trong tài liệu bài tập tuần 6, 8 và 9 nhưng không nên bật bằng một câu lệnh mặc định trong schema/seed:

- SQL Server Audit cần quyền server-level và đường dẫn lưu audit hợp lệ trên máy chủ.
- Dynamic Data Masking làm thay đổi kết quả người dùng không đủ quyền nhìn thấy; cần chọn cột và kiểm tra hành vi ứng dụng.
- Backup/restore cần vị trí lưu, retention, quyền truy cập và kiểm tra khôi phục.
- Replication/mirroring/Always On là cấu hình hạ tầng, không phải code khởi tạo bảng.

Các nội dung trên cần một kế hoạch triển khai riêng, review quyền và kiểm thử trước khi áp dụng vào môi trường thực tế.
