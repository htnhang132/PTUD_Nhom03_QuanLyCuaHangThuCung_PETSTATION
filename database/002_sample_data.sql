/*
  Pet Station - 002_sample_data.sql
  Purpose: insert the repository's sample dataset into an empty Pet Station schema.

  Important:
    - This file preserves the existing sample INSERT values and IDs.
    - It does not clear, truncate, or delete existing application rows.
    - Preflight checks run before the transaction to prevent loading into populated tables.
    - All seed INSERT statements execute inside the existing TRY/CATCH transaction.
  Security-course mapping:
    - C03: T-SQL script structure and error handling;
    - C05: SET XACT_ABORT, BEGIN TRANSACTION, COMMIT, ROLLBACK, THROW;
    - C06: sample audit records are data, while audit policy is documented separately.
*/

SET NOCOUNT ON;
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET ANSI_PADDING ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET NUMERIC_ROUNDABORT OFF;

-- Preflight: the seed contains fixed IDs and INSERT statements; it does not delete old rows.
-- Refuse ambiguous targets and avoid partial duplicate-key failures.
IF DB_NAME() IN (N'master', N'model', N'msdb', N'tempdb')
BEGIN
    ;THROW 51100, 'Run 002_sample_data.sql only inside a dedicated application database.', 1;
END;

IF OBJECT_ID(N'dbo.san_pham', N'U') IS NULL
   OR OBJECT_ID(N'dbo.cai_dat', N'U') IS NULL
BEGIN
    ;THROW 51101, 'The Pet Station schema is missing. Run 001_schema.sql successfully first.', 1;
END;

IF EXISTS (
    SELECT 1
    FROM sys.dm_db_partition_stats AS ps
    INNER JOIN sys.tables AS t
        ON t.object_id = ps.object_id
    INNER JOIN sys.schemas AS s
        ON s.schema_id = t.schema_id
    WHERE s.name = N'dbo'
      AND t.is_ms_shipped = 0
      AND t.name <> N'sysdiagrams'
      AND ps.index_id IN (0, 1)
      AND ps.row_count > 0
)
BEGIN
    ;THROW 51102, '002_sample_data.sql requires empty dbo tables. Existing rows were not deleted or changed.', 1;
END;

DECLARE @homNay date=CAST(GETDATE() AS date);
BEGIN TRY
BEGIN TRANSACTION;

-- ca_lam_viec (2 dòng)
DBCC CHECKIDENT ('dbo.ca_lam_viec', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[ca_lam_viec] ON;
INSERT INTO dbo.[ca_lam_viec] ([id], [ma_ca], [ten_ca], [gio_bat_dau], [gio_ket_thuc], [so_nhan_su_toi_thieu], [he_so_luong])
VALUES
    (1, N'CA01', N'Ca sáng', CONVERT(time,'07:30:00'), CONVERT(time,'14:30:00'), 2, 1.00),
    (2, N'CA02', N'Ca chiều', CONVERT(time,'14:30:00'), CONVERT(time,'21:30:00'), 2, 1.00);
SET IDENTITY_INSERT dbo.[ca_lam_viec] OFF;

-- chuong_trinh_khuyen_mai (1 dòng)
DBCC CHECKIDENT ('dbo.chuong_trinh_khuyen_mai', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chuong_trinh_khuyen_mai] ON;
INSERT INTO dbo.[chuong_trinh_khuyen_mai] ([id], [ma_chuong_trinh], [ten], [loai_giam], [gia_tri], [bat_dau], [ket_thuc], [nguon], [trang_thai], [don_toi_thieu])
VALUES
    (1, N'KM000001', N'Giảm 10% sản phẩm trong tháng', N'PERCENT', 10.00, DATEADD(second,-2160000,CAST(@homNay AS datetime2)), DATEADD(second,518400,CAST(@homNay AS datetime2)), N'PRODUCT', N'ACTIVE', 0.00);
SET IDENTITY_INSERT dbo.[chuong_trinh_khuyen_mai] OFF;

-- goi_dich_vu_spa (10 dòng)
DBCC CHECKIDENT ('dbo.goi_dich_vu_spa', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[goi_dich_vu_spa] ON;
INSERT INTO dbo.[goi_dich_vu_spa] ([id], [ma_goi], [ten_goi], [mo_ta], [thoi_gian_du_kien], [chung_loai_ap_dung], [don_gia_goc], [bang_gia], [trang_thai])
VALUES
    (1, N'SPA00000001', N'Tắm và sấy khô', N'Tắm, sấy, chải lông', 60, N'Cả hai', 150000.00, N'DOG|< 5kg|0.00|5.00|150000.00;DOG|5 - 10kg|5.00|10.00|200000.00;DOG|> 10kg|10.00|NULL|280000.00;CAT|Mọi cân nặng|0.00|NULL|180000.00', N'ACTIVE'),
    (2, N'SPA00000002', N'Cắt tỉa tạo kiểu', N'Cắt tỉa theo giống', 90, N'Cả hai', 230000.00, N'DOG|< 5kg|0.00|5.00|230000.00;DOG|5 - 10kg|5.00|10.00|300000.00;DOG|> 10kg|10.00|NULL|400000.00;CAT|Mọi cân nặng|0.00|NULL|250000.00', N'ACTIVE'),
    (3, N'SPA00000003', N'Cắt móng, vệ sinh tai', N'Cắt và dũa móng, lau tai', 20, N'Cả hai', 80000.00, N'DOG|Mọi cân nặng|0.00|NULL|80000.00;CAT|Mọi cân nặng|0.00|NULL|80000.00', N'ACTIVE'),
    (4, N'SPA00000004', N'Tắm và chăm sóc', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 60, N'Cả hai', 200000.00, N'DOG|Dưới 5kg|0.00|5.00|200000.00', N'ACTIVE'),
    (5, N'SPA00000005', N'Tắm cơ bản', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 45, N'Cả hai', 125000.00, N'DOG|< 5kg|0.00|5.00|125000.00;DOG|5 - 10kg|5.00|10.00|250000.00;DOG|>= 10kg|10.00|NULL|375000.00;CAT|< 5kg|0.00|5.00|125000.00;CAT|5 - 10kg|5.00|10.00|250000.00;CAT|>= 10kg|10.00|NULL|375000.00', N'ACTIVE'),
    (6, N'SPA00000006', N'Tắm dưỡng lông', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 60, N'Cả hai', 150000.00, N'DOG|< 5kg|0.00|5.00|150000.00;DOG|5 - 10kg|5.00|10.00|300000.00;DOG|>= 10kg|10.00|NULL|450000.00;CAT|< 5kg|0.00|5.00|150000.00;CAT|5 - 10kg|5.00|10.00|300000.00;CAT|>= 10kg|10.00|NULL|450000.00', N'ACTIVE'),
    (7, N'SPA00000007', N'Cắt tỉa tạo kiểu (DV000007)', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 75, N'Cả hai', 175000.00, N'DOG|< 5kg|0.00|5.00|175000.00;DOG|5 - 10kg|5.00|10.00|350000.00;DOG|>= 10kg|10.00|NULL|525000.00;CAT|< 5kg|0.00|5.00|175000.00;CAT|5 - 10kg|5.00|10.00|350000.00;CAT|>= 10kg|10.00|NULL|525000.00', N'ACTIVE'),
    (8, N'SPA00000008', N'Vệ sinh tai và móng', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 90, N'Cả hai', 200000.00, N'DOG|< 5kg|0.00|5.00|200000.00;DOG|5 - 10kg|5.00|10.00|400000.00;DOG|>= 10kg|10.00|NULL|600000.00;CAT|< 5kg|0.00|5.00|200000.00;CAT|5 - 10kg|5.00|10.00|400000.00;CAT|>= 10kg|10.00|NULL|600000.00', N'ACTIVE'),
    (9, N'SPA00000009', N'Chải lông và gỡ rối', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 105, N'Cả hai', 225000.00, N'DOG|< 5kg|0.00|5.00|225000.00;DOG|5 - 10kg|5.00|10.00|450000.00;DOG|>= 10kg|10.00|NULL|675000.00;CAT|< 5kg|0.00|5.00|225000.00;CAT|5 - 10kg|5.00|10.00|450000.00;CAT|>= 10kg|10.00|NULL|675000.00', N'ACTIVE'),
    (10, N'SPA00000010', N'Chăm sóc toàn diện', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 120, N'Cả hai', 250000.00, N'DOG|< 5kg|0.00|5.00|250000.00;DOG|5 - 10kg|5.00|10.00|500000.00;DOG|>= 10kg|10.00|NULL|750000.00;CAT|< 5kg|0.00|5.00|250000.00;CAT|5 - 10kg|5.00|10.00|500000.00;CAT|>= 10kg|10.00|NULL|750000.00', N'ACTIVE');
SET IDENTITY_INSERT dbo.[goi_dich_vu_spa] OFF;

-- khach_hang (29 dòng)
DBCC CHECKIDENT ('dbo.khach_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[khach_hang] ON;
INSERT INTO dbo.[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [dia_chi], [ngay_sinh], [diem_tich_luy], [hang_thanh_vien], [dang_hoat_dong], [email], [ghi_chu], [ngay_tao], [lich_su_diem])
VALUES
    (7, N'KH000001', N'Nguyễn Văn An', N'0901234567', N'12 Nguyễn Trãi, Hà Nội', NULL, 1268, N'Vàng', 1, N'an.nguyen@email.com', N'Thích giao hàng buổi tối', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|28|1268|Hóa đơn dữ liệu mẫu|2026-10-19 08:00:00;NULL|1240|1240|Số dư đầu kỳ mẫu|2026-10-19 08:00:00'),
    (8, N'KH000002', N'Trần Thị Bình', N'0912345678', N'45 Lê Lợi, TP.HCM', NULL, 4860, N'Kim Cương', 1, N'binh.tran@email.com', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'NULL|4860|4860|Số dư đầu kỳ mẫu|2026-10-19 08:00:00'),
    (9, N'KH000003', N'Lê Hoàng Cường', N'0923456789', N'8 Hùng Vương, Đà Nẵng', NULL, 3210, N'Bạch Kim', 1, N'', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'NULL|3210|3210|Số dư đầu kỳ mẫu|2026-10-19 08:00:00'),
    (10, N'KH000004', N'Phạm Minh Đức', N'0934567890', N'21 Lạch Tray, Hải Phòng', NULL, 840, N'Bạc', 1, N'duc.pham@email.com', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'NULL|840|840|Số dư đầu kỳ mẫu|2026-10-19 08:00:00'),
    (11, N'KH000005', N'Hoàng Thị Em', N'0945678901', N'5 Ninh Kiều, Cần Thơ', NULL, 230, N'Đồng', 1, N'', N'Dị ứng mùi nước hoa mạnh', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'NULL|230|230|Số dư đầu kỳ mẫu|2026-10-19 08:00:00'),
    (12, N'KH000006', N'Vũ Quang Hải', N'0956789012', N'77 Cầu Giấy, Hà Nội', NULL, 1980, N'Vàng', 1, N'hai.vu@email.com', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'NULL|1980|1980|Số dư đầu kỳ mẫu|2026-10-19 08:00:00'),
    (13, N'KH000007', N'Lê Minh Châu', N'0999199199', NULL, NULL, 199, N'REGULAR', 1, N'', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-527699,CAST(@homNay AS datetime2)), N'NULL|199|199|Số dư đầu|2026-10-19 08:00:00'),
    (14, N'KH000008', N'Nguyễn Gia Hân', N'0999200200', NULL, NULL, 200, N'REGULAR', 1, N'', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-527699,CAST(@homNay AS datetime2)), N'NULL|200|200|Số dư đầu|2026-10-19 08:00:00'),
    (15, N'KH000009', N'Võ Anh Thư', N'0999111222', NULL, NULL, 0, N'REGULAR', 1, N'', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-527699,CAST(@homNay AS datetime2)), NULL),
    (16, N'KH000010', N'Nguyễn Minh Anh', N'0009000001', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 249, N'Đồng', 1, N'khachhang16@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'2|32|32|tích điểm giao dịch|2026-10-19 08:00:00;22|17|49|tích điểm giao dịch|2026-10-19 08:00:00;NULL|400|449|điểm mở đầu|2026-10-19 08:00:00;NULL|-200|249|đổi voucher Spa 150000|2026-10-19 08:00:00'),
    (17, N'KH000011', N'Trần Gia Hân', N'0009000002', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 23, N'Đồng', 1, N'khachhang17@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'3|3|3|tích điểm giao dịch|2026-10-19 08:00:00;23|20|23|tích điểm giao dịch|2026-10-19 08:00:00'),
    (18, N'KH000012', N'Lê Hoàng Nam', N'0009000003', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 44, N'Đồng', 1, N'khachhang18@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'4|22|22|tích điểm giao dịch|2026-10-19 08:00:00;24|22|44|tích điểm giao dịch|2026-10-19 08:00:00'),
    (19, N'KH000013', N'Phạm Ngọc Linh', N'0009000004', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 55, N'Đồng', 1, N'khachhang19@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'5|30|30|tích điểm giao dịch|2026-10-19 08:00:00;25|25|55|tích điểm giao dịch|2026-10-19 08:00:00'),
    (20, N'KH000014', N'Võ Khánh An', N'0009000005', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 18, N'Đồng', 1, N'khachhang20@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'6|9|9|tích điểm giao dịch|2026-10-19 08:00:00;26|9|18|tích điểm giao dịch|2026-10-19 08:00:00'),
    (21, N'KH000015', N'Đặng Thanh Tâm', N'0009000006', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 22, N'Đồng', 1, N'khachhang21@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'7|13|13|tích điểm giao dịch|2026-10-19 08:00:00;27|9|22|tích điểm giao dịch|2026-10-19 08:00:00'),
    (22, N'KH000016', N'Bùi Quang Huy', N'0009000007', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 14, N'Đồng', 1, N'khachhang22@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'8|5|5|tích điểm giao dịch|2026-10-19 08:00:00;28|9|14|tích điểm giao dịch|2026-10-19 08:00:00'),
    (23, N'KH000017', N'Đỗ Bảo Ngọc', N'0009000008', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 57, N'Đồng', 1, N'khachhang23@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'9|48|48|tích điểm giao dịch|2026-10-19 08:00:00;29|9|57|tích điểm giao dịch|2026-10-19 08:00:00'),
    (24, N'KH000018', N'Hồ Minh Khang', N'0009000009', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 12, N'Đồng', 1, N'khachhang24@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'10|12|12|tích điểm giao dịch|2026-10-19 08:00:00'),
    (25, N'KH000019', N'Dương Tuệ Nhi', N'0009000010', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 5, N'Đồng', 1, N'khachhang25@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'11|5|5|tích điểm giao dịch|2026-10-19 08:00:00'),
    (26, N'KH000020', N'Nguyễn Minh Anh', N'0009000011', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 1, N'Đồng', 1, N'khachhang26@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'12|1|1|tích điểm giao dịch|2026-10-19 08:00:00'),
    (27, N'KH000021', N'Trần Gia Hân', N'0009000012', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 2, N'Đồng', 1, N'khachhang27@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'13|2|2|tích điểm giao dịch|2026-10-19 08:00:00'),
    (28, N'KH000022', N'Lê Hoàng Nam', N'0009000013', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 7, N'Đồng', 1, N'khachhang28@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'14|7|7|tích điểm giao dịch|2026-10-19 08:00:00'),
    (29, N'KH000023', N'Phạm Ngọc Linh', N'0009000014', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 7, N'Đồng', 1, N'khachhang29@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'15|7|7|tích điểm giao dịch|2026-10-19 08:00:00'),
    (30, N'KH000024', N'Võ Khánh An', N'0009000015', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 18, N'Đồng', 1, N'khachhang30@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'16|18|18|tích điểm giao dịch|2026-10-19 08:00:00'),
    (31, N'KH000025', N'Đặng Thanh Tâm', N'0009000016', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 5, N'Đồng', 1, N'khachhang31@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'17|5|5|tích điểm giao dịch|2026-10-19 08:00:00'),
    (32, N'KH000026', N'Bùi Quang Huy', N'0009000017', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 6, N'Đồng', 1, N'khachhang32@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'18|6|6|tích điểm giao dịch|2026-10-19 08:00:00;30|0|6|tích điểm giao dịch|2026-10-19 08:00:00'),
    (33, N'KH000027', N'Đỗ Bảo Ngọc', N'0009000018', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 6, N'Đồng', 1, N'khachhang33@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'19|6|6|tích điểm giao dịch|2026-10-19 08:00:00;31|0|6|tích điểm giao dịch|2026-10-19 08:00:00'),
    (34, N'KH000028', N'Hồ Minh Khang', N'0009000019', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 12, N'Đồng', 1, N'khachhang34@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'20|12|12|tích điểm giao dịch|2026-10-19 08:00:00'),
    (35, N'KH000029', N'Dương Tuệ Nhi', N'0009000020', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 15, N'Đồng', 1, N'khachhang35@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)), N'21|15|15|tích điểm giao dịch|2026-10-19 08:00:00');
SET IDENTITY_INSERT dbo.[khach_hang] OFF;

-- nha_cung_cap (10 dòng)
DBCC CHECKIDENT ('dbo.nha_cung_cap', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[nha_cung_cap] ON;
INSERT INTO dbo.[nha_cung_cap] ([id], [ma_ncc], [ten], [so_dien_thoai], [dia_chi], [nguoi_dai_dien], [nhom_mat_hang], [ghi_chu], [trang_thai], [ma_so_thue], [dieu_khoan])
VALUES
    (1, N'NCC0001', N'Royal Canin VN', N'0243825678', N'Hà Nội', N'Nguyễn Minh Tâm', NULL, NULL, N'COOPERATING', N'0101234567', N'30 ngày'),
    (2, N'NCC0002', N'Mars Petcare VN', N'0283826789', N'TP.HCM', N'Trần Thành Long', NULL, NULL, N'COOPERATING', N'0309876543', N'15 ngày'),
    (3, N'NCC0003', N'Orijen Import', N'0243827890', N'Hà Nội', N'Lê Đức Anh', NULL, NULL, N'COOPERATING', N'0105551234', N'Trả ngay'),
    (4, N'NCC0004', N'PetShop Thú Y', N'0911222333', N'TP.HCM', N'Phạm Quang Vinh', NULL, NULL, N'COOPERATING', N'0312223344', N'30 ngày'),
    (5, N'NCC0005', N'Công ty Thực phẩm thú cưng An Phú', N'0008000001', N'TP. Hồ Chí Minh', N'Nguyễn Thanh Hà', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (6, N'NCC0006', N'Nhà phân phối Minh Khang', N'0008000002', N'TP. Hồ Chí Minh', N'Trần Quốc Huy', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (7, N'NCC0007', N'Công ty Phụ kiện thú cưng Hưng Thịnh', N'0008000003', N'TP. Hồ Chí Minh', N'Lê Bảo Trâm', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (8, N'NCC0008', N'Nhà phân phối Happy Pet', N'0008000004', N'TP. Hồ Chí Minh', N'Phạm Minh Tuấn', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (9, N'NCC0009', N'Công ty Chăm sóc thú cưng Đại Việt', N'0008000005', N'TP. Hồ Chí Minh', N'Võ Thu Ngân', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (10, N'NCC0010', N'Nhà phân phối Thiên Phúc', N'0008000006', N'TP. Hồ Chí Minh', N'Đỗ Hoàng Long', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO');
SET IDENTITY_INSERT dbo.[nha_cung_cap] OFF;

-- nhan_vien (11 dòng)
DBCC CHECKIDENT ('dbo.nhan_vien', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[nhan_vien] ON;
INSERT INTO dbo.[nhan_vien] ([id], [ma_nhan_vien], [ho_ten], [ngay_sinh], [so_dien_thoai], [dia_chi], [chuc_vu], [ngay_vao_lam], [anh_dai_dien], [dang_lam_viec], [cccd])
VALUES
    (1, N'NV0001', N'Vi', NULL, N'0123456789', NULL, N'Cửa hàng trưởng', CONVERT(date,'2026-10-03',23), NULL, 1, NULL),
    (8, N'NV0002', N'Nguyễn Văn An', NULL, N'0901111222', NULL, N'Cửa hàng trưởng', CONVERT(date,'2020-03-01',23), NULL, 1, N'001090012345'),
    (9, N'NV0003', N'Phạm Thu Lan', NULL, N'0902222333', NULL, N'Nhân viên bán hàng', CONVERT(date,'2021-06-15',23), NULL, 1, N'001092045678'),
    (10, N'NV0004', N'Hoàng Văn Nam', NULL, N'0903333444', NULL, N'Nhân viên kho', CONVERT(date,'2022-01-10',23), NULL, 1, N'001088078901'),
    (11, N'NV0005', N'Trần Thị Hoa', NULL, N'0904444555', NULL, N'Nhân viên Spa/Hotel', CONVERT(date,'2022-09-05',23), NULL, 1, N'001095011223'),
    (12, N'NV0006', N'Lê Văn Hùng', NULL, N'0905555666', NULL, N'Nhân viên Spa/Hotel', CONVERT(date,'2023-02-20',23), NULL, 1, N'001093099887'),
    (13, N'NV0007', N'Đặng Minh Tú', NULL, N'0906666777', NULL, N'Nhân viên bán hàng', CONVERT(date,'2024-08-01',23), NULL, 1, N'001096055443'),
    (14, N'NV0008', N'Nguyễn Minh Quân', NULL, N'0999000001', NULL, N'Cửa hàng trưởng', CONVERT(date,'2026-10-19',23), NULL, 1, NULL),
    (15, N'NV0009', N'Trần Ngọc Anh', NULL, N'0999000002', NULL, N'Nhân viên bán hàng', CONVERT(date,'2026-10-19',23), NULL, 1, NULL),
    (16, N'NV0010', N'Lê Hoàng Phúc', NULL, N'0999000003', NULL, N'Nhân viên kho', CONVERT(date,'2026-10-19',23), NULL, 1, NULL),
    (17, N'NV0011', N'Phạm Thảo Nhi', NULL, N'0999000004', NULL, N'Nhân viên Spa/Hotel', CONVERT(date,'2026-10-19',23), NULL, 1, NULL);
SET IDENTITY_INSERT dbo.[nhan_vien] OFF;

-- lich_lam_viec (32 dòng)
DBCC CHECKIDENT ('dbo.lich_lam_viec', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lich_lam_viec] ON;
INSERT INTO dbo.[lich_lam_viec] ([id], [nhan_vien_id], [ca_lam_viec_id], [ngay_lam], [trang_thai], [loai])
VALUES
    (1, 14, 1, DATEADD(day,-7,@homNay), N'OPEN', N'WORK'),
    (2, 14, 1, DATEADD(day,-6,@homNay), N'OPEN', N'WORK'),
    (3, 14, 1, DATEADD(day,-5,@homNay), N'OPEN', N'WORK'),
    (4, 14, 1, DATEADD(day,-4,@homNay), N'OPEN', N'WORK'),
    (5, 14, 1, DATEADD(day,-3,@homNay), N'OPEN', N'WORK'),
    (6, 15, 1, DATEADD(day,-7,@homNay), N'OPEN', N'WORK'),
    (7, 15, 1, DATEADD(day,-6,@homNay), N'OPEN', N'WORK'),
    (8, 15, 1, DATEADD(day,-5,@homNay), N'OPEN', N'WORK'),
    (9, 15, 1, DATEADD(day,-4,@homNay), N'OPEN', N'WORK'),
    (10, 15, 1, DATEADD(day,-3,@homNay), N'OPEN', N'WORK'),
    (11, 16, 1, DATEADD(day,-7,@homNay), N'OPEN', N'WORK'),
    (12, 16, 1, DATEADD(day,-6,@homNay), N'OPEN', N'WORK'),
    (13, 16, 1, DATEADD(day,-5,@homNay), N'OPEN', N'WORK'),
    (14, 16, 1, DATEADD(day,-4,@homNay), N'OPEN', N'WORK'),
    (15, 16, 1, DATEADD(day,-3,@homNay), N'OPEN', N'WORK'),
    (16, 17, 1, DATEADD(day,-7,@homNay), N'OPEN', N'WORK'),
    (17, 17, 1, DATEADD(day,-6,@homNay), N'OPEN', N'WORK'),
    (18, 17, 1, DATEADD(day,-5,@homNay), N'OPEN', N'WORK'),
    (19, 17, 1, DATEADD(day,-4,@homNay), N'OPEN', N'WORK'),
    (20, 17, 1, DATEADD(day,-3,@homNay), N'OPEN', N'WORK'),
    (21, 14, 1, DATEADD(day,-2,@homNay), N'OPEN', N'WORK'),
    (22, 14, 1, DATEADD(day,-1,@homNay), N'OPEN', N'WORK'),
    (23, 14, 1, DATEADD(day,0,@homNay), N'OPEN', N'WORK'),
    (24, 15, 1, DATEADD(day,-2,@homNay), N'OPEN', N'WORK'),
    (25, 15, 1, DATEADD(day,-1,@homNay), N'OPEN', N'WORK'),
    (26, 15, 1, DATEADD(day,0,@homNay), N'OPEN', N'WORK'),
    (27, 16, 1, DATEADD(day,-2,@homNay), N'OPEN', N'WORK'),
    (28, 16, 1, DATEADD(day,-1,@homNay), N'OPEN', N'WORK'),
    (29, 16, 1, DATEADD(day,0,@homNay), N'OPEN', N'WORK'),
    (30, 17, 1, DATEADD(day,-2,@homNay), N'OPEN', N'WORK'),
    (31, 17, 1, DATEADD(day,-1,@homNay), N'OPEN', N'WORK'),
    (32, 17, 1, DATEADD(day,0,@homNay), N'OPEN', N'WORK');
SET IDENTITY_INSERT dbo.[lich_lam_viec] OFF;

-- phong_hotel (17 dòng)
DBCC CHECKIDENT ('dbo.phong_hotel', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phong_hotel] ON;
INSERT INTO dbo.[phong_hotel] ([id], [ma_phong], [ten_phong], [loai_phong], [gia_dem], [trang_thai], [suc_chua], [ghi_chu], [bang_gia])
VALUES
    (1, N'PH00000001', N'Phòng 01', N'Tiêu chuẩn', 150000.00, N'AVAILABLE', 1, N'', N'DAILY|0.00|999.00|150000.00'),
    (2, N'PH00000002', N'Phòng 02', N'Tiêu chuẩn', 150000.00, N'AVAILABLE', 1, N'', N'DAILY|0.00|999.00|150000.00'),
    (3, N'PH00000003', N'Phòng 03', N'Mèo', 120000.00, N'CLEANING', 1, N'Có cây leo', N'DAILY|0.00|999.00|120000.00'),
    (4, N'PH00000004', N'Phòng 04', N'VIP', 300000.00, N'AVAILABLE', 2, N'Điều hòa riêng', N'DAILY|0.00|999.00|300000.00'),
    (5, N'PH00000005', N'Phòng 05', N'VIP', 300000.00, N'AVAILABLE', 2, N'Điều hòa riêng', N'DAILY|0.00|999.00|300000.00'),
    (6, N'PH00000006', N'Phòng 06', N'Tiêu chuẩn', 150000.00, N'MAINTENANCE', 1, N'Sửa quạt thông gió', N'DAILY|0.00|999.00|150000.00'),
    (7, N'PH00000007', N'Phòng 07', N'Tiêu chuẩn', 200000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|200000.00'),
    (8, N'PH00000008', N'Phòng 08', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|180000.00'),
    (9, N'PH00000009', N'Phòng 09', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|180000.00'),
    (10, N'PH00000010', N'Phòng 10', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|180000.00'),
    (11, N'PH00000011', N'Phòng 11', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|180000.00'),
    (12, N'PH00000012', N'Phòng 12', N'Tiêu chuẩn', 180000.00, N'OCCUPIED', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|180000.00'),
    (13, N'PH00000013', N'Phòng 13', N'VIP', 250000.00, N'OCCUPIED', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|250000.00'),
    (14, N'PH00000014', N'Phòng 14', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|250000.00'),
    (15, N'PH00000015', N'Phòng 15', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|250000.00'),
    (16, N'PH00000016', N'Phòng 16', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|250000.00'),
    (17, N'PH00000017', N'Phòng 17', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.', N'DAILY|0.00|999.00|250000.00');
SET IDENTITY_INSERT dbo.[phong_hotel] OFF;

-- san_pham (74 dòng)
DBCC CHECKIDENT ('dbo.san_pham', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[san_pham] ON;
INSERT INTO dbo.[san_pham] ([id], [ma_san_pham], [barcode], [ten], [nhom_hang], [don_vi_tinh], [gia_ban], [gia_nhap_gan_nhat], [ton_toi_thieu], [theo_doi_hsd], [anh], [trang_thai], [thuong_hieu], [quy_cach], [doi_tuong_su_dung], [mo_ta], [nguon_url], [ngay_thu_thap], [lich_su_gia], [de_xuat_gia])
VALUES
    (3, N'SP000001', N'8938505000015', N'Royal Canin Adult 2kg', N'Thức ăn', N'Túi', 285000.00, 240000.00, 10, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (4, N'SP000002', N'8938505000022', N'Pate Whiskas 400g', N'Thức ăn', N'Lon', 45000.00, 36000.00, 30, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (5, N'SP000003', N'8938505000039', N'Vòng cổ da size M', N'Phụ kiện', N'Cái', 120000.00, 80000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (6, N'SP000004', N'8938505000046', N'Sữa tắm Spa Dogs 500ml', N'Grooming', N'Chai', 89000.00, 62000.00, 10, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (7, N'SP000005', N'8938505000053', N'Lồng vận chuyển size L', N'Phụ kiện', N'Cái', 450000.00, 330000.00, 3, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (8, N'SP000006', N'8938505000060', N'Đồ chơi bóng cao su', N'Phụ kiện', N'Cái', 35000.00, 20000.00, 10, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (9, N'SP000007', N'8938505000077', N'Thức ăn hạt Orijen 1.8kg', N'Thức ăn', N'Túi', 520000.00, 430000.00, 5, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (10, N'SP000008', N'8938505000084', N'Cát vệ sinh mèo 5L', N'Vệ sinh', N'Túi', 75000.00, 55000.00, 15, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (11, N'SP000009', N'8938505000091', N'Bàn chải lông chó', N'Grooming', N'Cái', 55000.00, 40000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (12, N'SP000010', N'8938505000107', N'Thuốc nhỏ gáy Frontline', N'Thuốc', N'Hộp', 180000.00, 130000.00, 5, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (13, N'SP000011', N'8938505000114', N'Snack xương gặm răng', N'Thức ăn', N'Gói', 42000.00, 30000.00, 10, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (14, N'SP000012', N'8938505000121', N'Áo mưa chó size S', N'Phụ kiện', N'Cái', 95000.00, 65000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (15, N'SP000013', N'8938505000138', N'Vòng cổ cỡ S', N'Phụ kiện', N'Cái', 100000.00, 50000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (16, N'SP000014', N'8938505000145', N'Vòng cổ cỡ M', N'Phụ kiện', N'Cái', 150000.00, 50000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    (17, N'SP000015', N'8938505000152', N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 lít; Đóng gói: 1 túi', N'Vệ sinh', N'Túi', 160000.00, 104000.00, 5, 0, N'images/products/SP-VS-001.jpg', N'SELLING', N'IRIS OHYAMA', N'Dung tích: 10 lít; Đóng gói: 1 túi', N'Mèo', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 10 lít; Đóng gói: 1 túi.', N'https://www.petmart.vn/cat-ve-sinh-cho-meo-sieu-von-iris-ohyama-kfag-100', CONVERT(date,'2026-10-19',23), N'0.00|160000.00|2|2026-09-15 08:00:00', NULL),
    (18, N'SP000016', N'8938505000169', N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói', N'Thức ăn', N'Gói', 10000.00, 6500.00, 5, 1, N'images/products/SP-TA-001.jpg', N'SELLING', N'VEGEBRAND', N'Đóng gói: 1 gói', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: Đóng gói: 1 gói.', N'https://www.petmart.vn/xuong-cho-cho-gam-sach-rang-vegebrand-360-bone-prevent-tartar', CONVERT(date,'2026-10-19',23), N'0.00|10000.00|2|2026-09-15 08:00:00', NULL),
    (19, N'SP000017', N'8938505000176', N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800g', N'Thức ăn', N'Gói', 225000.00, 146250.00, 5, 1, N'images/products/SP-TA-002.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 800g', N'Chó', N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 800g.', N'https://www.petmart.vn/thuc-an-cho-cho-con-co-nho-royal-canin-mini-puppy', CONVERT(date,'2026-10-19',23), N'0.00|225000.00|2|2026-09-15 08:00:00', NULL),
    (20, N'SP000018', N'8938505000183', N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150ml', N'Vệ sinh', N'Chai', 150000.00, 97500.00, 5, 0, N'images/products/SP-VS-002.jpg', N'SELLING', N'JOYCE & DOLLS', N'Dung tích: 150ml', N'Chó và mèo', N'Vệ sinh thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 150ml.', N'https://www.petmart.vn/chai-xit-cho-meo-di-ve-sinh-dung-cho-joyce-dolls-trainer-puppy', CONVERT(date,'2026-10-19',23), N'0.00|150000.00|2|2026-09-15 08:00:00', NULL),
    (21, N'SP000019', N'8938505000190', N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-003.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-nguyen-chat-cat-sea-fish-pure-tuna-meat', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', NULL),
    (22, N'SP000020', N'8938505000206', N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400g', N'Thức ăn', N'Gói', 130000.00, 84500.00, 5, 1, N'images/products/SP-TA-004.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-con-royal-canin-kitten', CONVERT(date,'2026-10-19',23), N'0.00|130000.00|2|2026-09-15 08:00:00', NULL),
    (23, N'SP000021', N'8938505000213', N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-005.jpg', N'SELLING', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-ca-ngu-whiskas-tuna-flavour-sauce', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', NULL),
    (24, N'SP000022', N'8938505000220', N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 lít', N'Vệ sinh', N'Gói', 160000.00, 104000.00, 5, 0, N'images/products/SP-VS-003.jpg', N'SELLING', N'TRIXIE', N'Dung tích: 5 lít', N'Mèo', N'Vệ sinh thương hiệu TRIXIE, dành cho mèo. Quy cách: Dung tích: 5 lít.', N'https://www.petmart.vn/cat-ve-sinh-thuy-tinh-cho-meo-trixie-silikatstreu', CONVERT(date,'2026-10-19',23), N'0.00|160000.00|2|2026-09-15 08:00:00', NULL),
    (25, N'SP000023', N'8938505000237', N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400g', N'Thức ăn', N'Gói', 125000.00, 81250.00, 5, 1, N'images/products/SP-TA-006.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-indoor-27', CONVERT(date,'2026-10-19',23), N'0.00|125000.00|2|2026-09-15 08:00:00', NULL),
    (26, N'SP000024', N'8938505000244', N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-007.jpg', N'SELLING', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-thit-bo-whiskas-beef-flavour-sauce', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', NULL),
    (27, N'SP000025', N'8938505000251', N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ', N'Vệ sinh', N'Cái', 5000.00, 3250.00, 5, 0, N'images/products/SP-VS-004.jpg', N'SELLING', N'IRIS OHYAMA', N'Đóng gói: 1 miếng lẻ', N'Chó', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.', N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-88', CONVERT(date,'2026-10-19',23), N'0.00|5000.00|2|2026-09-15 08:00:00', NULL),
    (28, N'SP000026', N'8938505000268', N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-008.jpg', N'SELLING', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/banh-thuong-cho-cho-vi-thit-bo-vegebrand-orgo-freshening-biscuit-bacon-beef', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', NULL),
    (29, N'SP000027', N'8938505000275', N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 35000.00, 22750.00, 5, 1, N'images/products/SP-TA-009.jpg', N'SELLING', N'IRIS OHYAMA', N'Trọng lượng: 100g; Đóng gói: 1 lon', N'Chó', N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-cho-vi-thit-bo-iris-ohyama-one-care-beef', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', NULL),
    (30, N'SP000028', N'8938505000282', N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-010.jpg', N'SELLING', N'PEDIGREE', N'Trọng lượng: 100g', N'Chó', N'Thức ăn thương hiệu PEDIGREE, dành cho chó. Quy cách: Trọng lượng: 100g.', N'https://www.petmart.vn/pate-cho-cho-nuoc-sot-vi-thit-bo-pedigree-pouch-beef', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', NULL),
    (31, N'SP000029', N'8938505000299', N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500g', N'Thức ăn', N'Gói', 185000.00, 120250.00, 5, 1, N'images/products/SP-TA-011.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 500g', N'Chó', N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 500g.', N'https://www.petmart.vn/thuc-an-cho-cho-poodle-con-royal-canin-poodle-puppy', CONVERT(date,'2026-10-19',23), N'0.00|185000.00|2|2026-09-15 08:00:00', NULL),
    (32, N'SP000030', N'8938505000305', N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-012.jpg', N'SELLING', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/nuoc-sot-cho-meo-vi-thit-ga-whiskas-chicken-flavour-in-sauce', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', NULL),
    (33, N'SP000031', N'8938505000312', N'Pate cho mèo mẹ và mèo con CATIDEA Mother & Baby Cat — Trọng lượng: 85g', N'Thức ăn', N'Gói', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-013.jpg', N'SELLING', N'CATIDEA', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu CATIDEA, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-me-va-meo-con-catidea-mother-baby-cat', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', NULL),
    (34, N'SP000032', N'8938505000329', N'Pate cho mèo vị cá ngừ thanh cua CAT SEA FISH Tuna Surimi — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-014.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-thanh-cua-cat-sea-fish-tuna-surimi', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', NULL),
    (35, N'SP000033', N'8938505000336', N'Pate cho mèo CIAO Tuna & Whitebait vị cá ngừ và cá chạch trắng — Trọng lượng: 60g', N'Thức ăn', N'Gói', 20000.00, 13000.00, 5, 1, N'images/products/SP-TA-015.jpg', N'SELLING', N'CIAO', N'Trọng lượng: 60g', N'Mèo', N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.', N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-whitebait-vi-ca-ngu-va-ca-chach-trang', CONVERT(date,'2026-10-19',23), N'0.00|20000.00|2|2026-09-15 08:00:00', NULL),
    (36, N'SP000034', N'8938505000343', N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1kg', N'Vệ sinh', N'Gói', 140000.00, 91000.00, 5, 0, N'images/products/SP-VS-005.jpg', N'SELLING', N'CATS BEST', N'Trọng lượng: 2.1kg', N'Mèo', N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.1kg.', N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-original', CONVERT(date,'2026-10-19',23), N'0.00|140000.00|2|2026-09-15 08:00:00', NULL),
    (37, N'SP000035', N'8938505000350', N'Xương cho chó vị thịt bò VEGEBRAND Orgo Nutrients Beef', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-016.jpg', N'SELLING', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/xuong-cho-cho-vi-thit-bo-vegebrand-orgo-nutrients-beef', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', NULL),
    (38, N'SP000036', N'8938505000367', N'Thức ăn cho mèo con và mèo mẹ ROYAL CANIN Mother & Babycat — Trọng lượng: 400g', N'Thức ăn', N'Gói', 130000.00, 84500.00, 5, 1, N'images/products/SP-TA-017.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-con-va-meo-me-royal-canin-mother-babycat', CONVERT(date,'2026-10-19',23), N'0.00|130000.00|2|2026-09-15 08:00:00', NULL),
    (39, N'SP000037', N'8938505000374', N'Pate cho mèo vị cá ngừ cá thu CAT SEA FISH Tuna Mackerel — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-018.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-thu-cat-sea-fish-tuna-mackerel', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', NULL),
    (40, N'SP000038', N'8938505000381', N'Thức ăn cho mèo trưởng thành ROYAL CANIN Regular Fit 32 — Trọng lượng: 400g', N'Thức ăn', N'Gói', 115000.00, 74750.00, 5, 1, N'images/products/SP-TA-019.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-regular-fit-32', CONVERT(date,'2026-10-19',23), N'0.00|115000.00|2|2026-09-15 08:00:00', NULL),
    (41, N'SP000039', N'8938505000398', N'Thức ăn cho chó Poodle MKB All Life Stages Formula Nutrition — Trọng lượng: 1kg', N'Thức ăn', N'Gói', 315000.00, 204750.00, 5, 1, N'images/products/SP-TA-020.jpg', N'SELLING', N'MKB', N'Trọng lượng: 1kg', N'Chó', N'Thức ăn thương hiệu MKB, dành cho chó. Quy cách: Trọng lượng: 1kg.', N'https://www.petmart.vn/thuc-an-cho-cho-poodle-mkb-all-life-stages-formula-nutrition', CONVERT(date,'2026-10-19',23), N'0.00|315000.00|2|2026-09-15 08:00:00', NULL),
    (42, N'SP000040', N'8938505000404', N'Pate cho chó hỗ trợ chức năng gan và sáng mắt IRIS OHYAMA Benefit For Eyes & Liver — Trọng lượng: 100g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 35000.00, 22750.00, 5, 1, N'images/products/SP-TA-021.jpg', N'SELLING', N'IRIS OHYAMA', N'Trọng lượng: 100g; Đóng gói: 1 lon', N'Chó', N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-cho-ho-tro-chuc-nang-gan-va-sang-mat-iris-ohyama-benefit-for-eyes-liver', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', NULL),
    (43, N'SP000041', N'8938505000411', N'Pate cho mèo CIAO Tuna & Salmon vị cá ngừ và cá hồi — Trọng lượng: 60g', N'Thức ăn', N'Gói', 20000.00, 13000.00, 5, 1, N'images/products/SP-TA-022.jpg', N'SELLING', N'CIAO', N'Trọng lượng: 60g', N'Mèo', N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.', N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-salmon-vi-ca-ngu-va-ca-hoi', CONVERT(date,'2026-10-19',23), N'0.00|20000.00|2|2026-09-15 08:00:00', NULL),
    (44, N'SP000042', N'8938505000428', N'Đồ chơi cho chó mèo dạng bóng thừng BOBO', N'Phụ kiện', N'Cái', 30000.00, 19500.00, 5, 0, N'images/products/SP-PK-001.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-cho-cho-meo-dang-bong-thung-bobo', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'PENDING|4|30000.00|35000.00|đề xuất giá chưa áp dụng|2026-10-19 08:00:00'),
    (45, N'SP000043', N'8938505000435', N'Pate cho mèo vị cá ngừ cá cơm CAT SEA FISH Tuna Anchovy — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-023.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-com-cat-sea-fish-tuna-anchovy', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', NULL),
    (46, N'SP000044', N'8938505000442', N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone', N'Thức ăn', N'Gói', 50000.00, 32500.00, 5, 1, N'images/products/SP-TA-024.jpg', N'SELLING', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/xuong-cho-cho-vi-thit-vit-bone-for-poodles', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', NULL),
    (47, N'SP000045', N'8938505000459', N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam', N'Grooming', N'Chai', 180000.00, 117000.00, 5, 1, N'images/products/SP-GR-001.jpg', N'SELLING', N'JOYCE & DOLLS', N'', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo.', N'https://www.petmart.vn/sua-tam-kho-cho-cho-meo-joyce-dolls-waterless-cleansing-foam', CONVERT(date,'2026-10-19',23), N'0.00|180000.00|2|2026-09-15 08:00:00', NULL),
    (48, N'SP000046', N'8938505000466', N'Khay vệ sinh cho mèo MAKAR Cat Litter Station', N'Vệ sinh', N'Cái', 250000.00, 162500.00, 5, 0, N'images/products/SP-VS-006.jpg', N'SELLING', N'MAKAR', N'', N'Mèo', N'Vệ sinh thương hiệu MAKAR, dành cho mèo.', N'https://www.petmart.vn/khay-ve-sinh-cho-meo-makar-cat-litter-station', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', NULL),
    (49, N'SP000047', N'8938505000473', N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-42 — Đóng gói: 1 miếng lẻ', N'Vệ sinh', N'Cái', 10000.00, 6500.00, 5, 0, N'images/products/SP-VS-007.jpg', N'SELLING', N'IRIS OHYAMA', N'Đóng gói: 1 miếng lẻ', N'Chó', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.', N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-42', CONVERT(date,'2026-10-19',23), N'0.00|10000.00|2|2026-09-15 08:00:00', NULL),
    (50, N'SP000048', N'8938505000480', N'Cát vệ sinh cho mèo than hoạt tính IRIS OHYAMA Charcoal SNS-70 — Dung tích: 7 lít; Đóng gói: 1 túi', N'Vệ sinh', N'Túi', 160000.00, 104000.00, 5, 0, N'images/products/SP-VS-008.jpg', N'SELLING', N'IRIS OHYAMA', N'Dung tích: 7 lít; Đóng gói: 1 túi', N'Mèo', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 7 lít; Đóng gói: 1 túi.', N'https://www.petmart.vn/cat-ve-sinh-cho-meo-than-hoat-tinh-iris-ohyama-charcoal-sns-70', CONVERT(date,'2026-10-19',23), N'0.00|160000.00|2|2026-09-15 08:00:00', NULL),
    (51, N'SP000049', N'8938505000497', N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb', N'Grooming', N'Cái', 85000.00, 55250.00, 5, 1, N'images/products/SP-GR-002.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/luoc-chai-long-cho-meo-dau-luoi-mem-bobo-pet-comb', CONVERT(date,'2026-10-19',23), N'0.00|85000.00|2|2026-09-15 08:00:00', NULL),
    (52, N'SP000050', N'8938505000503', N'Cát vệ sinh hữu cơ cho mèo CATS BEST Smart Peppets — Trọng lượng: 2.5kg', N'Vệ sinh', N'Gói', 155000.00, 100750.00, 5, 0, N'images/products/SP-VS-009.jpg', N'SELLING', N'CATS BEST', N'Trọng lượng: 2.5kg', N'Mèo', N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.5kg.', N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-smart-peppets', CONVERT(date,'2026-10-19',23), N'0.00|155000.00|2|2026-09-15 08:00:00', NULL),
    (53, N'SP000051', N'8938505000510', N'Vòng cổ chó mèo kèm dây dắt cỡ mini HAND IN HAND', N'Phụ kiện', N'Cái', 70000.00, 45500.00, 5, 0, N'images/products/SP-PK-002.jpg', N'SELLING', N'HAND IN HAND', N'', N'Chó và mèo', N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-mini-hand-in-hand', CONVERT(date,'2026-10-19',23), N'0.00|70000.00|2|2026-09-15 08:00:00', N'PENDING|4|70000.00|75000.00|đề xuất giá chưa áp dụng|2026-10-19 08:00:00'),
    (54, N'SP000052', N'8938505000527', N'Bóng đồ chơi cho chó mèo có chuông nhựa BOBO', N'Phụ kiện', N'Cái', 45000.00, 29250.00, 5, 0, N'images/products/SP-PK-003.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/bong-do-choi-cho-cho-meo-co-chuong-nhua-bobo', CONVERT(date,'2026-10-19',23), N'0.00|45000.00|2|2026-09-15 08:00:00', NULL),
    (55, N'SP000053', N'8938505000534', N'Đồ chơi dây thừng cho chó BOBO', N'Phụ kiện', N'Cái', 35000.00, 22750.00, 5, 0, N'images/products/SP-PK-004.jpg', N'SELLING', N'BOBO', N'', N'Chó', N'Phụ kiện thương hiệu BOBO, dành cho chó.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-bobo', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', NULL),
    (56, N'SP000054', N'8938505000541', N'Vòng cổ chống ve chó BIOLINE Flea and Tick Collar For Dogs', N'Phụ kiện', N'Cái', 60000.00, 39000.00, 5, 0, N'images/products/SP-PK-005.jpg', N'SELLING', N'BIOLINE', N'', N'Chó', N'Phụ kiện thương hiệu BIOLINE, dành cho chó.', N'https://www.petmart.vn/vong-co-chong-ve-cho-bioline-flea-and-tick-collar-for-dogs', CONVERT(date,'2026-10-19',23), N'0.00|60000.00|2|2026-09-15 08:00:00', NULL),
    (57, N'SP000055', N'8938505000558', N'Khay vệ sinh cho chó MAKAR Dog Toilet Trays Small — Kích thước: S', N'Vệ sinh', N'Cái', 220000.00, 143000.00, 5, 0, N'images/products/SP-VS-010.jpg', N'SELLING', N'MAKAR', N'Kích thước: S', N'Chó', N'Vệ sinh thương hiệu MAKAR, dành cho chó. Quy cách: Kích thước: S.', N'https://www.petmart.vn/khay-ve-sinh-cho-cho-makar-dog-toilet-trays-small', CONVERT(date,'2026-10-19',23), N'0.00|220000.00|2|2026-09-15 08:00:00', NULL),
    (58, N'SP000056', N'8938505000565', N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush', N'Grooming', N'Cái', 30000.00, 19500.00, 5, 1, N'images/products/SP-GR-003.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/ban-chai-tam-cho-cho-meo-bobo-pet-washing-brush', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', NULL),
    (59, N'SP000057', N'8938505000572', N'Đồ chơi cần câu cho mèo PAW vui nhộn', N'Phụ kiện', N'Cái', 75000.00, 48750.00, 5, 0, N'images/products/SP-PK-006.jpg', N'SELLING', N'PAW', N'', N'Mèo', N'Phụ kiện thương hiệu PAW, dành cho mèo.', N'https://www.petmart.vn/do-choi-can-cau-cho-meo-paw-vui-nhon', CONVERT(date,'2026-10-19',23), N'0.00|75000.00|2|2026-09-15 08:00:00', NULL),
    (60, N'SP000058', N'8938505000589', N'Sữa tắm cho chó mèo hương hoa hồng JOYCE & DOLLS Rose Dew — Dung tích: 400ml', N'Grooming', N'Chai', 250000.00, 162500.00, 5, 1, N'images/products/SP-GR-004.jpg', N'SELLING', N'JOYCE & DOLLS', N'Dung tích: 400ml', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.', N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-hong-joyce-dolls-rose-dew', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', NULL),
    (61, N'SP000059', N'8938505000596', N'Đồ chơi cho chó mèo Paw hình con gà', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP-PK-007.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-cho-cho-meo-paw-hinh-con-ga', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', NULL),
    (62, N'SP000060', N'8938505000602', N'Bình uống nước cho chó mèo gài chuồng BOBO', N'Phụ kiện', N'Cái', 80000.00, 52000.00, 5, 0, N'images/products/SP-PK-008.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/binh-uong-nuoc-cho-cho-meo-gai-chuong-bobo', CONVERT(date,'2026-10-19',23), N'0.00|80000.00|2|2026-09-15 08:00:00', NULL),
    (63, N'SP000061', N'8938505000619', N'Vòng cổ chống liếm cho chó mèo PAW Pet Elizabethan Collar', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP-PK-009.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-chong-liem-cho-cho-meo-paw-pet-elizabethan-collar', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', NULL),
    (64, N'SP000062', N'8938505000626', N'Sữa tắm cho chó diệt khuẩn BBN Bactericidal Shampoo Lemon', N'Grooming', N'Chai', 150000.00, 97500.00, 5, 1, N'images/products/SP-GR-005.jpg', N'SELLING', N'BBN', N'', N'Chó', N'Grooming thương hiệu BBN, dành cho chó.', N'https://www.petmart.vn/sua-tam-cho-cho-diet-khuan-bbn-bactericidal-shampoo-lemon', CONVERT(date,'2026-10-19',23), N'0.00|150000.00|2|2026-09-15 08:00:00', NULL),
    (65, N'SP000063', N'8938505000633', N'Găng tay tắm cho chó mèo BOBO Pet Grooming Brush Gloves', N'Grooming', N'Cái', 40000.00, 26000.00, 5, 1, N'images/products/SP-GR-006.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/gang-tay-tam-cho-cho-meo-bobo-pet-grooming-brush-gloves', CONVERT(date,'2026-10-19',23), N'0.00|40000.00|2|2026-09-15 08:00:00', NULL),
    (66, N'SP000064', N'8938505000640', N'Kềm cắt bấm móng chó mèo PAW Safety Guard Clipper', N'Grooming', N'Cái', 70000.00, 45500.00, 5, 1, N'images/products/SP-GR-007.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Grooming thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/kem-cat-bam-mong-cho-meo-paw-safety-guard-clipper', CONVERT(date,'2026-10-19',23), N'0.00|70000.00|2|2026-09-15 08:00:00', NULL),
    (67, N'SP000065', N'8938505000657', N'Sữa tắm cho chó lông trắng BBN Whitening Shampoo', N'Grooming', N'Chai', 150000.00, 97500.00, 5, 1, N'images/products/SP-GR-008.jpg', N'SELLING', N'BBN', N'', N'Chó', N'Grooming thương hiệu BBN, dành cho chó.', N'https://www.petmart.vn/sua-tam-cho-cho-long-trang-bbn-whitening-shampoo', CONVERT(date,'2026-10-19',23), N'0.00|150000.00|2|2026-09-15 08:00:00', NULL),
    (68, N'SP000066', N'8938505000664', N'Đồ chơi dây thừng cho chó mèo đan chéo BOBO', N'Phụ kiện', N'Cái', 55000.00, 35750.00, 5, 0, N'images/products/SP-PK-010.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-dan-cheo-bobo', CONVERT(date,'2026-10-19',23), N'0.00|55000.00|2|2026-09-15 08:00:00', NULL),
    (69, N'SP000067', N'8938505000671', N'Bát ăn cho chó mèo nhựa mềm SAWYER PET', N'Phụ kiện', N'Cái', 40000.00, 26000.00, 5, 0, N'images/products/SP-PK-011.jpg', N'SELLING', N'SAWYER PET', N'', N'Chó và mèo', N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-cho-cho-meo-nhua-mem-sawyer-pet', CONVERT(date,'2026-10-19',23), N'0.00|40000.00|2|2026-09-15 08:00:00', NULL),
    (70, N'SP000068', N'8938505000688', N'Sữa tắm cho chó mèo hương hoa nhài JOYCE & DOLLS Moder Jasmine — Dung tích: 400ml', N'Grooming', N'Chai', 250000.00, 162500.00, 5, 1, N'images/products/SP-GR-009.jpg', N'SELLING', N'JOYCE & DOLLS', N'Dung tích: 400ml', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.', N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-nhai-joyce-dolls-moder-jasmine', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', NULL),
    (71, N'SP000069', N'8938505000695', N'Lược chải lông chó mèo nút bấm KUDI Pet Grooming Slicker Brushes', N'Grooming', N'Cái', 170000.00, 110500.00, 5, 1, N'images/products/SP-GR-010.jpg', N'SELLING', N'KUDI', N'', N'Chó và mèo', N'Grooming thương hiệu KUDI, dành cho chó và mèo.', N'https://www.petmart.vn/luoc-chai-long-cho-meo-nut-bam-kudi-pet-grooming-slicker-brushes', CONVERT(date,'2026-10-19',23), N'0.00|170000.00|2|2026-09-15 08:00:00', NULL),
    (72, N'SP000070', N'8938505000701', N'Thảm mát giải nhiệt cho chó mèo PAW Pet Cool Mat', N'Phụ kiện', N'Cái', 195000.00, 126750.00, 5, 0, N'images/products/SP-PK-012.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/tham-mat-giai-nhiet-cho-cho-meo-paw-pet-cool-mat', CONVERT(date,'2026-10-19',23), N'0.00|195000.00|2|2026-09-15 08:00:00', NULL),
    (73, N'SP000071', N'8938505000718', N'Vòng cổ chó mèo kèm dây dắt cỡ bé HAND IN HAND', N'Phụ kiện', N'Cái', 80000.00, 52000.00, 5, 0, N'images/products/SP-PK-013.jpg', N'SELLING', N'HAND IN HAND', N'', N'Chó và mèo', N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-be-hand-in-hand', CONVERT(date,'2026-10-19',23), N'0.00|80000.00|2|2026-09-15 08:00:00', NULL),
    (74, N'SP000072', N'8938505000725', N'Bát ăn đôi cho chó mèo bằng nhựa SAWYER PET', N'Phụ kiện', N'Cái', 60000.00, 39000.00, 5, 0, N'images/products/SP-PK-014.jpg', N'SELLING', N'SAWYER PET', N'', N'Chó và mèo', N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-doi-cho-cho-meo-bang-nhua-sawyer-pet', CONVERT(date,'2026-10-19',23), N'0.00|60000.00|2|2026-09-15 08:00:00', NULL),
    (75, N'SP000073', N'8938505000732', N'Đồ chơi dây thừng cho chó mèo PAW loại dài', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP-PK-015.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-paw-loai-dai', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', NULL),
    (76, N'SP000074', N'8938505000749', N'Bát ăn cho chó mèo bằng nhựa BOBO Plastic Bowl 3038', N'Phụ kiện', N'Cái', 35000.00, 22750.00, 5, 0, N'images/products/SP-PK-016.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-cho-cho-meo-bang-nhua-bobo-plastic-bowl-3038', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', NULL);
SET IDENTITY_INSERT dbo.[san_pham] OFF;

-- lo_hang (75 dòng)
DBCC CHECKIDENT ('dbo.lo_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lo_hang] ON;
INSERT INTO dbo.[lo_hang] ([id], [ma_lo], [san_pham_id], [so_lo], [ngay_san_xuat], [ngay_nhap], [han_su_dung], [gia_nhap], [so_luong_nhap], [so_luong_ton], [ngay_kiem_ke], [trang_thai])
VALUES
    (2, N'LO00000001', 3, N'LO00000001', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 240000.00, 47, 47, @homNay, N'AVAILABLE'),
    (3, N'LO00000002', 4, N'LO00000002', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 36000.00, 82, 82, @homNay, N'AVAILABLE'),
    (4, N'LO00000003', 5, N'LO00000003', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 80000.00, 10, 3, @homNay, N'AVAILABLE'),
    (5, N'LO00000004', 6, N'LO00000004', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 62000.00, 28, 28, @homNay, N'AVAILABLE'),
    (6, N'LO00000005', 7, N'LO00000005', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 330000.00, 10, 7, @homNay, N'AVAILABLE'),
    (7, N'LO00000006', 8, N'LO00000006', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 20000.00, 15, 15, @homNay, N'AVAILABLE'),
    (8, N'LO00000007', 9, N'LO00000007', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 430000.00, 10, 2, @homNay, N'AVAILABLE'),
    (9, N'LO00000008', 10, N'LO00000008', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 55000.00, 31, 31, @homNay, N'AVAILABLE'),
    (10, N'LO00000009', 11, N'LO00000009', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 40000.00, 12, 12, @homNay, N'AVAILABLE'),
    (11, N'LO00000010', 12, N'LO00000010', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 130000.00, 10, 0, @homNay, N'AVAILABLE'),
    (12, N'LO00000011', 13, N'LO00000011', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 30000.00, 40, 40, @homNay, N'AVAILABLE'),
    (13, N'LO00000012', 14, N'LO00000012', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 65000.00, 10, 9, @homNay, N'AVAILABLE'),
    (14, N'LO00000013', 3, N'LO00000013', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 240000.00, 10, 2, @homNay, N'AVAILABLE'),
    (15, N'LO00000014', 15, N'LO00000014', DATEADD(day,-30,@homNay), @homNay, NULL, 50000.00, 20, 20, @homNay, N'AVAILABLE'),
    (16, N'LO00000015', 16, N'LO00000015', DATEADD(day,-30,@homNay), @homNay, NULL, 50000.00, 20, 20, @homNay, N'AVAILABLE'),
    (17, N'LO00000016', 17, N'LO00000016', DATEADD(day,-30,@homNay), @homNay, NULL, 104000.00, 10, 9, @homNay, N'AVAILABLE'),
    (18, N'LO00000017', 18, N'LO00000017', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 6500.00, 10, 9, @homNay, N'AVAILABLE'),
    (19, N'LO00000018', 19, N'LO00000018', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 146250.00, 12, 12, @homNay, N'AVAILABLE'),
    (20, N'LO00000019', 20, N'LO00000019', DATEADD(day,-30,@homNay), @homNay, NULL, 97500.00, 12, 12, @homNay, N'AVAILABLE'),
    (21, N'LO00000020', 21, N'LO00000020', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 12, 12, @homNay, N'AVAILABLE'),
    (22, N'LO00000021', 22, N'LO00000021', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 84500.00, 15, 15, @homNay, N'AVAILABLE'),
    (23, N'LO00000022', 23, N'LO00000022', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 15, 15, @homNay, N'AVAILABLE'),
    (24, N'LO00000023', 24, N'LO00000023', DATEADD(day,-30,@homNay), @homNay, NULL, 104000.00, 15, 15, @homNay, N'AVAILABLE'),
    (25, N'LO00000024', 25, N'LO00000024', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 81250.00, 18, 18, @homNay, N'AVAILABLE'),
    (26, N'LO00000025', 26, N'LO00000025', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 18, 18, @homNay, N'AVAILABLE'),
    (27, N'LO00000026', 27, N'LO00000026', DATEADD(day,-30,@homNay), @homNay, NULL, 3250.00, 18, 18, @homNay, N'AVAILABLE'),
    (28, N'LO00000027', 28, N'LO00000027', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 21, 21, @homNay, N'AVAILABLE'),
    (29, N'LO00000028', 29, N'LO00000028', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 22750.00, 21, 21, @homNay, N'AVAILABLE'),
    (30, N'LO00000029', 30, N'LO00000029', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 21, 21, @homNay, N'AVAILABLE'),
    (31, N'LO00000030', 31, N'LO00000030', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 120250.00, 24, 24, @homNay, N'AVAILABLE'),
    (32, N'LO00000031', 32, N'LO00000031', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 24, 24, @homNay, N'AVAILABLE'),
    (33, N'LO00000032', 33, N'LO00000032', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 27, 27, @homNay, N'AVAILABLE'),
    (34, N'LO00000033', 34, N'LO00000033', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 19500.00, 28, 28, @homNay, N'AVAILABLE'),
    (35, N'LO00000034', 35, N'LO00000034', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 13000.00, 29, 29, @homNay, N'AVAILABLE'),
    (36, N'LO00000035', 36, N'LO00000035', DATEADD(day,-30,@homNay), @homNay, NULL, 91000.00, 29, 29, @homNay, N'AVAILABLE'),
    (37, N'LO00000036', 37, N'LO00000036', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 10, 10, @homNay, N'AVAILABLE'),
    (38, N'LO00000037', 38, N'LO00000037', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 84500.00, 11, 11, @homNay, N'AVAILABLE'),
    (39, N'LO00000038', 39, N'LO00000038', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 12, 12, @homNay, N'AVAILABLE'),
    (40, N'LO00000039', 40, N'LO00000039', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 74750.00, 13, 13, @homNay, N'AVAILABLE'),
    (41, N'LO00000040', 41, N'LO00000040', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 204750.00, 14, 14, @homNay, N'AVAILABLE'),
    (42, N'LO00000041', 42, N'LO00000041', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 22750.00, 15, 15, @homNay, N'AVAILABLE'),
    (43, N'LO00000042', 43, N'LO00000042', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 13000.00, 16, 16, @homNay, N'AVAILABLE'),
    (44, N'LO00000043', 44, N'LO00000043', DATEADD(day,-30,@homNay), @homNay, NULL, 19500.00, 15, 15, @homNay, N'AVAILABLE'),
    (45, N'LO00000044', 45, N'LO00000044', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 18, 18, @homNay, N'AVAILABLE'),
    (46, N'LO00000045', 46, N'LO00000045', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 32500.00, 17, 17, @homNay, N'AVAILABLE'),
    (47, N'LO00000046', 47, N'LO00000046', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 117000.00, 20, 20, @homNay, N'AVAILABLE'),
    (48, N'LO00000047', 48, N'LO00000047', DATEADD(day,-30,@homNay), @homNay, NULL, 162500.00, 21, 21, @homNay, N'AVAILABLE'),
    (49, N'LO00000048', 49, N'LO00000048', DATEADD(day,-30,@homNay), @homNay, NULL, 6500.00, 22, 22, @homNay, N'AVAILABLE'),
    (50, N'LO00000049', 50, N'LO00000049', DATEADD(day,-30,@homNay), @homNay, NULL, 104000.00, 23, 23, @homNay, N'AVAILABLE'),
    (51, N'LO00000050', 51, N'LO00000050', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 55250.00, 22, 22, @homNay, N'AVAILABLE'),
    (52, N'LO00000051', 52, N'LO00000051', DATEADD(day,-30,@homNay), @homNay, NULL, 100750.00, 25, 25, @homNay, N'AVAILABLE'),
    (53, N'LO00000052', 53, N'LO00000052', DATEADD(day,-30,@homNay), @homNay, NULL, 45500.00, 26, 26, @homNay, N'AVAILABLE'),
    (54, N'LO00000053', 54, N'LO00000053', DATEADD(day,-30,@homNay), @homNay, NULL, 29250.00, 27, 27, @homNay, N'AVAILABLE'),
    (55, N'LO00000054', 55, N'LO00000054', DATEADD(day,-30,@homNay), @homNay, NULL, 22750.00, 26, 26, @homNay, N'AVAILABLE'),
    (56, N'LO00000055', 56, N'LO00000055', DATEADD(day,-30,@homNay), @homNay, NULL, 39000.00, 29, 29, @homNay, N'AVAILABLE'),
    (57, N'LO00000056', 57, N'LO00000056', DATEADD(day,-30,@homNay), @homNay, NULL, 143000.00, 10, 3, @homNay, N'AVAILABLE'),
    (58, N'LO00000057', 58, N'LO00000057', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 10, 9, @homNay, N'AVAILABLE'),
    (59, N'LO00000058', 59, N'LO00000058', DATEADD(day,-30,@homNay), @homNay, NULL, 48750.00, 11, 11, @homNay, N'AVAILABLE'),
    (60, N'LO00000059', 60, N'LO00000059', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 162500.00, 12, 12, @homNay, N'AVAILABLE'),
    (61, N'LO00000060', 61, N'LO00000060', DATEADD(day,-30,@homNay), @homNay, NULL, 32500.00, 13, 13, @homNay, N'AVAILABLE'),
    (62, N'LO00000061', 62, N'LO00000061', DATEADD(day,-30,@homNay), @homNay, NULL, 52000.00, 14, 14, @homNay, N'AVAILABLE'),
    (63, N'LO00000062', 63, N'LO00000062', DATEADD(day,-30,@homNay), @homNay, NULL, 32500.00, 15, 15, @homNay, N'AVAILABLE'),
    (64, N'LO00000063', 64, N'LO00000063', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 97500.00, 16, 16, @homNay, N'AVAILABLE'),
    (65, N'LO00000064', 65, N'LO00000064', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 26000.00, 17, 17, @homNay, N'AVAILABLE'),
    (66, N'LO00000065', 66, N'LO00000065', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 45500.00, 18, 18, @homNay, N'AVAILABLE'),
    (67, N'LO00000066', 67, N'LO00000066', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 97500.00, 19, 19, @homNay, N'AVAILABLE'),
    (68, N'LO00000067', 68, N'LO00000067', DATEADD(day,-30,@homNay), @homNay, NULL, 35750.00, 20, 20, @homNay, N'AVAILABLE'),
    (69, N'LO00000068', 69, N'LO00000068', DATEADD(day,-30,@homNay), @homNay, NULL, 26000.00, 21, 21, @homNay, N'AVAILABLE'),
    (70, N'LO00000069', 70, N'LO00000069', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 162500.00, 22, 22, @homNay, N'AVAILABLE'),
    (71, N'LO00000070', 71, N'LO00000070', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 110500.00, 23, 23, @homNay, N'AVAILABLE'),
    (72, N'LO00000071', 72, N'LO00000071', DATEADD(day,-30,@homNay), @homNay, NULL, 126750.00, 24, 24, @homNay, N'AVAILABLE'),
    (73, N'LO00000072', 73, N'LO00000072', DATEADD(day,-30,@homNay), @homNay, NULL, 52000.00, 25, 25, @homNay, N'AVAILABLE'),
    (74, N'LO00000073', 74, N'LO00000073', DATEADD(day,-30,@homNay), @homNay, NULL, 39000.00, 26, 26, @homNay, N'AVAILABLE'),
    (75, N'LO00000074', 75, N'LO00000074', DATEADD(day,-30,@homNay), @homNay, NULL, 32500.00, 27, 27, @homNay, N'AVAILABLE'),
    (76, N'LO00000075', 76, N'LO00000075', DATEADD(day,-30,@homNay), @homNay, NULL, 22750.00, 28, 28, @homNay, N'AVAILABLE');
SET IDENTITY_INSERT dbo.[lo_hang] OFF;

-- tai_khoan (5 dòng)
DBCC CHECKIDENT ('dbo.tai_khoan', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[tai_khoan] ON;
INSERT INTO dbo.[tai_khoan] ([id], [nhan_vien_id], [ten_dang_nhap], [mat_khau_hash], [vai_tro], [quyen], [trang_thai], [so_lan_dang_nhap_sai], [khoa_den], [bat_buoc_doi_mat_khau], [dang_nhap_cuoi])
VALUES
    (1, 1, N'admin', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'QUAN_LY_CUA_HANG', N'XEM_TONG_QUAN;QUAN_LY_TAI_KHOAN;QUAN_LY_LICH_LAM_VIEC;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA;DUYET_GIA;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_KHUYEN_MAI;QUAN_LY_SPA;QUAN_LY_LUU_TRU;XU_LY_PHE_DUYET;XEM_NHAT_KY;XEM_BAO_CAO_CHUNG;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, NULL, 1, NULL),
    (2, 14, N'demo.admin', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'QUAN_LY_CUA_HANG', N'XEM_TONG_QUAN;QUAN_LY_TAI_KHOAN;QUAN_LY_LICH_LAM_VIEC;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA;DUYET_GIA;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_KHUYEN_MAI;QUAN_LY_SPA;QUAN_LY_LUU_TRU;XU_LY_PHE_DUYET;XEM_NHAT_KY;XEM_BAO_CAO_CHUNG;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, NULL, 1, NULL),
    (3, 15, N'demo.sales', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_BAN_HANG', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, NULL, 1, NULL),
    (4, 16, N'demo.stock', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_KHO', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA', N'HOAT_DONG', 0, NULL, 1, NULL),
    (5, 17, N'demo.spa', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_SPA_HOTEL', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_SPA;QUAN_LY_LUU_TRU', N'HOAT_DONG', 0, NULL, 1, NULL);
SET IDENTITY_INSERT dbo.[tai_khoan] OFF;

-- bien_ban_kiem_ke (1 dòng)
DBCC CHECKIDENT ('dbo.bien_ban_kiem_ke', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[bien_ban_kiem_ke] ON;
INSERT INTO dbo.[bien_ban_kiem_ke] ([id], [so_bien_ban], [nguoi_lap_id], [ky_kiem_ke], [trang_thai], [tao_luc])
VALUES
    (1, N'KK00000001', 4, N'2026-10-19', N'PENDING', DATEADD(second,-549878,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[bien_ban_kiem_ke] OFF;

-- cai_dat (34 dòng)
INSERT INTO dbo.[cai_dat] ([khoa], [gia_tri], [nguoi_cap_nhat_id], [ngay_cap_nhat])
VALUES
    (N'CANH_BAO_TON_THAP', N'true', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'CANH_BAO_DON_MOI', N'true', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'CANH_BAO_SPA', N'true', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'SO_TAI_KHOAN_NGAN_HANG', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TEN_CHU_TAI_KHOAN', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MA_BIN_NGAN_HANG', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TIEN_TE', N'VND', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'BAO_CAO_NGAY', N'false', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'SO_NGAY_CANH_BAO_HET_HAN', N'30', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'NGON_NGU', N'Tiếng Việt', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'PHI_TRA_PHONG_TRE', N'50000', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'NGUONG_TON_KHO_THAP', N'10', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'KHO_GIAY', N'80mm', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'MAY_IN', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'GIO_BAO_CAO', N'21:00', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'SO_NGAY_DOI_TRA', N'3', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'DIA_CHI_CUA_HANG', N'123 Phan Văn Trị, Gò Vấp, TP.HCM', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'EMAIL_CUA_HANG', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TEN_CUA_HANG', N'Pet Station', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_DIEN_THOAI_CUA_HANG', N'0243825678', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MA_SO_THUE', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'WEBSITE_CUA_HANG', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MUC_CHI_TIEU_HANG', N'2000000,10000000,25000000,50000000', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'MUI_GIO', N'Asia/Ho_Chi_Minh', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'THUE_VAT_PHAN_TRAM', N'8', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_TIEN_MOI_DIEM_TICH', N'10000', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'SO_TIEN_MOI_DIEM_DOI', N'1000', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'HAN_MUC_XUAT_HUY_CAN_DUYET', N'500000', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_NGAY_TON_DONG', N'45', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MAC_DINH_KIEM_KE_MU', N'false', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_GIO_TOI_DA_MOI_TUAN', N'40', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_GIO_BAO_TRUOC_DOI_CA', N'12', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'THU_MUC_ANH_CHAM_SOC', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'PHIEN_BAN_CSDL', N'20261029', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2)));

-- chi_tiet_kiem_ke (1 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_kiem_ke', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_kiem_ke] ON;
INSERT INTO dbo.[chi_tiet_kiem_ke] ([id], [bien_ban_id], [lo_hang_id], [ton_he_thong], [ton_thuc_te], [nguyen_nhan])
VALUES
    (1, 1, 57, 3, 2, N'lệch kiểm kê , chưa áp dụng');
SET IDENTITY_INSERT dbo.[chi_tiet_kiem_ke] OFF;

-- nhat_ky_he_thong (0 dòng)
DBCC CHECKIDENT ('dbo.nhat_ky_he_thong', RESEED, 1) WITH NO_INFOMSGS;

-- phien_lam_viec (33 dòng)
DBCC CHECKIDENT ('dbo.phien_lam_viec', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phien_lam_viec] ON;
INSERT INTO dbo.[phien_lam_viec] ([id], [ma_phien], [lich_lam_viec_id], [tai_khoan_id], [mo_luc], [dong_luc], [tien_dau_ca], [tien_thuc_te], [chenh_lech], [giai_trinh], [trang_thai], [ten_ca], [nguoi_nhan_ban_giao_id], [hinh_thuc_ban_giao])
VALUES
    (1, N'MPI00000001', NULL, 1, DATEADD(second,-554336,CAST(@homNay AS datetime2)), DATEADD(second,-517635,CAST(@homNay AS datetime2)), 22000000.00, 22180000.00, 0.00, N'', N'HANDED_OVER', N'Ca chiều', 2, N'BAN_GIAO'),
    (2, N'MPI00000002', NULL, 2, DATEADD(second,-560772,CAST(@homNay AS datetime2)), DATEADD(second,-553572,CAST(@homNay AS datetime2)), 1000000.00, 1285000.00, 0.00, NULL, N'HANDED_OVER', N'Ca dữ liệu mẫu', NULL, NULL),
    (3, N'MPI00000003', NULL, 3, DATEADD(second,-3081600,CAST(@homNay AS datetime2)), DATEADD(second,-3038400,CAST(@homNay AS datetime2)), 500000.00, 820000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (4, N'MPI00000004', NULL, 3, DATEADD(second,-2995200,CAST(@homNay AS datetime2)), DATEADD(second,-2952000,CAST(@homNay AS datetime2)), 500000.00, 530000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (5, N'MPI00000005', NULL, 3, DATEADD(second,-2908800,CAST(@homNay AS datetime2)), DATEADD(second,-2865600,CAST(@homNay AS datetime2)), 500000.00, 725000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (6, N'MPI00000006', NULL, 3, DATEADD(second,-2822400,CAST(@homNay AS datetime2)), DATEADD(second,-2779200,CAST(@homNay AS datetime2)), 500000.00, 800000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (7, N'MPI00000007', NULL, 3, DATEADD(second,-2736000,CAST(@homNay AS datetime2)), DATEADD(second,-2692800,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (8, N'MPI00000008', NULL, 3, DATEADD(second,-2649600,CAST(@homNay AS datetime2)), DATEADD(second,-2606400,CAST(@homNay AS datetime2)), 500000.00, 630000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (9, N'MPI00000009', NULL, 3, DATEADD(second,-2563200,CAST(@homNay AS datetime2)), DATEADD(second,-2520000,CAST(@homNay AS datetime2)), 500000.00, 550000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (10, N'MPI00000010', NULL, 3, DATEADD(second,-2476800,CAST(@homNay AS datetime2)), DATEADD(second,-2433600,CAST(@homNay AS datetime2)), 500000.00, 980000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (11, N'MPI00000011', NULL, 3, DATEADD(second,-2390400,CAST(@homNay AS datetime2)), DATEADD(second,-2347200,CAST(@homNay AS datetime2)), 500000.00, 625000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (12, N'MPI00000012', NULL, 3, DATEADD(second,-2304000,CAST(@homNay AS datetime2)), DATEADD(second,-2260800,CAST(@homNay AS datetime2)), 500000.00, 550000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (13, N'MPI00000013', NULL, 3, DATEADD(second,-2217600,CAST(@homNay AS datetime2)), DATEADD(second,-2174400,CAST(@homNay AS datetime2)), 500000.00, 515000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (14, N'MPI00000014', NULL, 3, DATEADD(second,-2131200,CAST(@homNay AS datetime2)), DATEADD(second,-2088000,CAST(@homNay AS datetime2)), 500000.00, 525000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (15, N'MPI00000015', NULL, 3, DATEADD(second,-2044800,CAST(@homNay AS datetime2)), DATEADD(second,-2001600,CAST(@homNay AS datetime2)), 500000.00, 570000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (16, N'MPI00000016', NULL, 3, DATEADD(second,-1958400,CAST(@homNay AS datetime2)), DATEADD(second,-1915200,CAST(@homNay AS datetime2)), 500000.00, 575000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (17, N'MPI00000017', NULL, 3, DATEADD(second,-1872000,CAST(@homNay AS datetime2)), DATEADD(second,-1828800,CAST(@homNay AS datetime2)), 500000.00, 685000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (18, N'MPI00000018', NULL, 3, DATEADD(second,-1785600,CAST(@homNay AS datetime2)), DATEADD(second,-1742400,CAST(@homNay AS datetime2)), 500000.00, 550000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (19, N'MPI00000019', NULL, 3, DATEADD(second,-1699200,CAST(@homNay AS datetime2)), DATEADD(second,-1656000,CAST(@homNay AS datetime2)), 500000.00, 500000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (20, N'MPI00000020', NULL, 3, DATEADD(second,-1612800,CAST(@homNay AS datetime2)), DATEADD(second,-1569600,CAST(@homNay AS datetime2)), 500000.00, 500000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (21, N'MPI00000021', NULL, 3, DATEADD(second,-1526400,CAST(@homNay AS datetime2)), DATEADD(second,-1483200,CAST(@homNay AS datetime2)), 500000.00, 625000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (22, N'MPI00000022', NULL, 3, DATEADD(second,-1440000,CAST(@homNay AS datetime2)), DATEADD(second,-1396800,CAST(@homNay AS datetime2)), 500000.00, 740000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (23, N'MPI00000023', NULL, 3, DATEADD(second,-1353600,CAST(@homNay AS datetime2)), DATEADD(second,-1310400,CAST(@homNay AS datetime2)), 500000.00, 765000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (24, N'MPI00000024', NULL, 3, DATEADD(second,-1267200,CAST(@homNay AS datetime2)), DATEADD(second,-1224000,CAST(@homNay AS datetime2)), 500000.00, 790000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (25, N'MPI00000025', NULL, 3, DATEADD(second,-1180800,CAST(@homNay AS datetime2)), DATEADD(second,-1137600,CAST(@homNay AS datetime2)), 500000.00, 815000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (26, N'MPI00000026', NULL, 3, DATEADD(second,-1094400,CAST(@homNay AS datetime2)), DATEADD(second,-1051200,CAST(@homNay AS datetime2)), 500000.00, 840000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (27, N'MPI00000027', NULL, 3, DATEADD(second,-1008000,CAST(@homNay AS datetime2)), DATEADD(second,-964800,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (28, N'MPI00000028', NULL, 3, DATEADD(second,-921600,CAST(@homNay AS datetime2)), DATEADD(second,-878400,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (29, N'MPI00000029', NULL, 3, DATEADD(second,-835200,CAST(@homNay AS datetime2)), DATEADD(second,-792000,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (30, N'MPI00000030', NULL, 3, DATEADD(second,-748800,CAST(@homNay AS datetime2)), DATEADD(second,-705600,CAST(@homNay AS datetime2)), 500000.00, 620000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (31, N'MPI00000031', NULL, 3, DATEADD(second,-662400,CAST(@homNay AS datetime2)), DATEADD(second,-619200,CAST(@homNay AS datetime2)), 500000.00, 1155000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (32, N'MPI00000032', NULL, 3, DATEADD(second,-576000,CAST(@homNay AS datetime2)), DATEADD(second,-532800,CAST(@homNay AS datetime2)), 500000.00, 1250000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (33, N'MPI00000033', NULL, 1, DATEADD(second,-517618,CAST(@homNay AS datetime2)), NULL, 22180000.00, NULL, NULL, NULL, N'OPEN', N'Ca sáng', NULL, NULL);
SET IDENTITY_INSERT dbo.[phien_lam_viec] OFF;

-- hoa_don (37 dòng)
DBCC CHECKIDENT ('dbo.hoa_don', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[hoa_don] ON;
INSERT INTO dbo.[hoa_don] ([id], [so_hoa_don], [phien_lam_viec_id], [tai_khoan_id], [khach_hang_id], [tong_tien], [hinh_thuc_thanh_toan], [tien_khach_dua], [tien_thoi], [diem_cong], [trang_thai], [tao_luc], [thanh_toan_luc], [idempotency_key], [tam_tinh], [giam_gia], [diem_dung], [ma_voucher], [ghi_chu], [chi_tiet_thanh_toan])
VALUES
    (1, N'HD2610190001', 2, 2, 7, 285000.00, N'TIEN_MAT', 285000.00, 0.00, 28, N'PAID', DATEADD(second,-553572,CAST(@homNay AS datetime2)), DATEADD(second,-553572,CAST(@homNay AS datetime2)), N'SEED-HD-SAMPLE-001', 285000.00, 0.00, 0, NULL, NULL, NULL),
    (2, N'HD2610190002', 3, 3, 16, 320000.00, N'TIEN_MAT', 320000.00, 0.00, 32, N'PAID', DATEADD(second,-3070800,CAST(@homNay AS datetime2)), DATEADD(second,-3070800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_1', 320000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (3, N'HD2610190003', 4, 3, 17, 30000.00, N'TIEN_MAT', 30000.00, 0.00, 3, N'PAID', DATEADD(second,-2980800,CAST(@homNay AS datetime2)), DATEADD(second,-2980800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_2', 30000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (4, N'HD2610190004', 5, 3, 18, 225000.00, N'TIEN_MAT', 225000.00, 0.00, 22, N'PAID', DATEADD(second,-2890800,CAST(@homNay AS datetime2)), DATEADD(second,-2890800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_3', 225000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (5, N'HD2610190005', 6, 3, 19, 300000.00, N'TIEN_MAT', 300000.00, 0.00, 30, N'PAID', DATEADD(second,-2800800,CAST(@homNay AS datetime2)), DATEADD(second,-2800800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_4', 300000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (6, N'HD2610190006', 7, 3, 20, 90000.00, N'TIEN_MAT', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-2710800,CAST(@homNay AS datetime2)), DATEADD(second,-2710800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_5', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (7, N'HD2610190007', 8, 3, 21, 130000.00, N'TIEN_MAT', 130000.00, 0.00, 13, N'PAID', DATEADD(second,-2620800,CAST(@homNay AS datetime2)), DATEADD(second,-2620800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_6', 130000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (8, N'HD2610190008', 9, 3, 22, 50000.00, N'TIEN_MAT', 50000.00, 0.00, 5, N'PAID', DATEADD(second,-2530800,CAST(@homNay AS datetime2)), DATEADD(second,-2530800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_7', 50000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (9, N'HD2610190009', 10, 3, 23, 480000.00, N'TIEN_MAT', 480000.00, 0.00, 48, N'PAID', DATEADD(second,-2469600,CAST(@homNay AS datetime2)), DATEADD(second,-2469600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_8', 480000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (10, N'HD2610190010', 11, 3, 24, 125000.00, N'TIEN_MAT', 125000.00, 0.00, 12, N'PAID', DATEADD(second,-2379600,CAST(@homNay AS datetime2)), DATEADD(second,-2379600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_9', 125000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (11, N'HD2610190011', 12, 3, 25, 50000.00, N'TIEN_MAT', 50000.00, 0.00, 5, N'PAID', DATEADD(second,-2289600,CAST(@homNay AS datetime2)), DATEADD(second,-2289600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_10', 50000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (12, N'HD2610190012', 13, 3, 26, 15000.00, N'TIEN_MAT', 15000.00, 0.00, 1, N'PAID', DATEADD(second,-2199600,CAST(@homNay AS datetime2)), DATEADD(second,-2199600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_11', 15000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (13, N'HD2610190013', 14, 3, 27, 25000.00, N'TIEN_MAT', 25000.00, 0.00, 2, N'PAID', DATEADD(second,-2109600,CAST(@homNay AS datetime2)), DATEADD(second,-2109600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_12', 25000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (14, N'HD2610190014', 15, 3, 28, 70000.00, N'TIEN_MAT', 70000.00, 0.00, 7, N'PAID', DATEADD(second,-2019600,CAST(@homNay AS datetime2)), DATEADD(second,-2019600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_13', 70000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (15, N'HD2610190015', 16, 3, 29, 75000.00, N'TIEN_MAT', 75000.00, 0.00, 7, N'PAID', DATEADD(second,-1929600,CAST(@homNay AS datetime2)), DATEADD(second,-1929600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_14', 75000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (16, N'HD2610190016', 17, 3, 30, 185000.00, N'TIEN_MAT', 185000.00, 0.00, 18, N'PAID', DATEADD(second,-1839600,CAST(@homNay AS datetime2)), DATEADD(second,-1839600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_15', 185000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (17, N'HD2610190017', 18, 3, 31, 50000.00, N'TIEN_MAT', 50000.00, 0.00, 5, N'PAID', DATEADD(second,-1778400,CAST(@homNay AS datetime2)), DATEADD(second,-1778400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_16', 50000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (18, N'HD2610190018', 30, 3, 32, 60000.00, N'TIEN_MAT', 60000.00, 0.00, 6, N'PAID', DATEADD(second,-738000,CAST(@homNay AS datetime2)), DATEADD(second,-738000,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_17', 60000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (19, N'HD2610190019', 30, 3, 33, 60000.00, N'TIEN_MAT', 60000.00, 0.00, 6, N'PAID', DATEADD(second,-734400,CAST(@homNay AS datetime2)), DATEADD(second,-734400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_18', 60000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (20, N'HD2610190020', 21, 3, 34, 125000.00, N'TIEN_MAT', 125000.00, 0.00, 12, N'PAID', DATEADD(second,-1508400,CAST(@homNay AS datetime2)), DATEADD(second,-1508400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_19', 125000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (21, N'HD2610190021', 22, 3, 35, 150000.00, N'TIEN_MAT', 150000.00, 0.00, 15, N'PAID', DATEADD(second,-1418400,CAST(@homNay AS datetime2)), DATEADD(second,-1418400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_20', 150000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (22, N'HD2610190022', 23, 3, 16, 175000.00, N'TIEN_MAT', 175000.00, 0.00, 17, N'PAID', DATEADD(second,-1328400,CAST(@homNay AS datetime2)), DATEADD(second,-1328400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_21', 175000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (23, N'HD2610190023', 24, 3, 17, 200000.00, N'TIEN_MAT', 200000.00, 0.00, 20, N'PAID', DATEADD(second,-1238400,CAST(@homNay AS datetime2)), DATEADD(second,-1238400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_22', 200000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (24, N'HD2610190024', 25, 3, 18, 225000.00, N'TIEN_MAT', 225000.00, 0.00, 22, N'PAID', DATEADD(second,-1148400,CAST(@homNay AS datetime2)), DATEADD(second,-1148400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_23', 225000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (25, N'HD2610190025', 26, 3, 19, 250000.00, N'TIEN_MAT', 250000.00, 0.00, 25, N'PAID', DATEADD(second,-1087200,CAST(@homNay AS datetime2)), DATEADD(second,-1087200,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_24', 250000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (26, N'HD2610190026', 23, 3, 20, 90000.00, N'TIEN_MAT', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-1342800,CAST(@homNay AS datetime2)), DATEADD(second,-1342800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_25', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (27, N'HD2610190027', 25, 3, 21, 90000.00, N'TIEN_MAT', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-1166400,CAST(@homNay AS datetime2)), DATEADD(second,-1166400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_26', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (28, N'HD2610190028', 27, 3, 22, 90000.00, N'TIEN_MAT', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-990000,CAST(@homNay AS datetime2)), DATEADD(second,-990000,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_27', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (29, N'HD2610190029', 29, 3, 23, 90000.00, N'TIEN_MAT', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-813600,CAST(@homNay AS datetime2)), DATEADD(second,-813600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_28', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (30, N'HD2610190030', 31, 3, 32, 5000.00, N'TIEN_MAT', 5000.00, 0.00, 0, N'PAID', DATEADD(second,-637200,CAST(@homNay AS datetime2)), DATEADD(second,-637200,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_29', 35000.00, 30000.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (31, N'HD2610190031', 31, 3, 33, 5000.00, N'TIEN_MAT', 5000.00, 0.00, 0, N'PAID', DATEADD(second,-633600,CAST(@homNay AS datetime2)), DATEADD(second,-633600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_30', 35000.00, 30000.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.', NULL),
    (32, N'HD2610190032', 1, 1, NULL, 180000.00, N'TIEN_MAT', 180000.00, 0.00, 0, N'PAID', DATEADD(second,-523756,CAST(@homNay AS datetime2)), DATEADD(second,-523756,CAST(@homNay AS datetime2)), N'PAY-e788e605e1f84d8c87c8', 200000.00, 20000.00, 0, N'', NULL, NULL),
    (33, N'HD2610190033', 33, 1, NULL, 90000.00, N'TIEN_MAT', 90000.00, 0.00, 0, N'PAID', DATEADD(second,-484351,CAST(@homNay AS datetime2)), DATEADD(second,-484351,CAST(@homNay AS datetime2)), N'PAY-a554c66e3ee0419e844b', 100000.00, 10000.00, 0, N'', NULL, NULL),
    (34, N'HD2610190034', 33, 1, NULL, 531000.00, N'TIEN_MAT', 531000.00, 0.00, 0, N'PAID', DATEADD(second,-122380,CAST(@homNay AS datetime2)), DATEADD(second,-122380,CAST(@homNay AS datetime2)), N'PAY-bd9b970ee60541f4b1d2', 590000.00, 59000.00, 0, N'', NULL, NULL),
    (35, N'TREO000001', NULL, 3, 16, 160000.00, N'TIEN_MAT', NULL, NULL, 0, N'GIO_TREO', DATEADD(second,-524678,CAST(@homNay AS datetime2)), DATEADD(second,-524678,CAST(@homNay AS datetime2)), NULL, 160000.00, 0.00, 0, NULL, NULL, NULL),
    (36, N'TREO000002', NULL, 3, 17, 10000.00, N'TIEN_MAT', NULL, NULL, 0, N'GIO_TREO', DATEADD(second,-524678,CAST(@homNay AS datetime2)), DATEADD(second,-524678,CAST(@homNay AS datetime2)), NULL, 10000.00, 0.00, 0, NULL, NULL, NULL),
    (37, N'TREO000003', NULL, 1, NULL, 355000.00, N'TIEN_MAT', NULL, NULL, 0, N'GIO_TREO', DATEADD(second,-516600,CAST(@homNay AS datetime2)), DATEADD(second,-516600,CAST(@homNay AS datetime2)), NULL, 355000.00, 0.00, 0, NULL, NULL, NULL);
SET IDENTITY_INSERT dbo.[hoa_don] OFF;

-- chi_tiet_hoa_don (41 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_hoa_don', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_hoa_don] ON;
INSERT INTO dbo.[chi_tiet_hoa_don] ([id], [hoa_don_id], [san_pham_id], [lo_hang_id], [so_luong], [don_gia], [giam_gia], [thue_gtgt], [ma_dich_vu], [ten_hang])
VALUES
    (1, 1, 3, 14, 1, 285000.00, 0.00, 0.00, NULL, N'Royal Canin Adult 2kg'),
    (2, 2, 17, 17, 2, 160000.00, 0.00, 0.00, NULL, N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 lít; Đóng gói: 1 túi'),
    (3, 3, 18, 18, 3, 10000.00, 0.00, 0.00, NULL, N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói'),
    (4, 4, 19, 19, 1, 225000.00, 0.00, 0.00, NULL, N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800g'),
    (5, 5, 20, 20, 2, 150000.00, 0.00, 0.00, NULL, N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150ml'),
    (6, 6, 21, 21, 3, 30000.00, 0.00, 0.00, NULL, N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85g; Đóng gói: 1 lon'),
    (7, 7, 22, 22, 1, 130000.00, 0.00, 0.00, NULL, N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400g'),
    (8, 8, 23, 23, 2, 25000.00, 0.00, 0.00, NULL, N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85g'),
    (9, 9, 24, 24, 3, 160000.00, 0.00, 0.00, NULL, N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 lít'),
    (10, 10, 25, 25, 1, 125000.00, 0.00, 0.00, NULL, N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400g'),
    (11, 11, 26, 26, 2, 25000.00, 0.00, 0.00, NULL, N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85g'),
    (12, 12, 27, 27, 3, 5000.00, 0.00, 0.00, NULL, N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ'),
    (13, 13, 28, 28, 1, 25000.00, 0.00, 0.00, NULL, N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef'),
    (14, 14, 29, 29, 2, 35000.00, 0.00, 0.00, NULL, N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100g; Đóng gói: 1 lon'),
    (15, 15, 30, 30, 3, 25000.00, 0.00, 0.00, NULL, N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100g'),
    (16, 16, 31, 31, 1, 185000.00, 0.00, 0.00, NULL, N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500g'),
    (17, 17, 32, 32, 2, 25000.00, 0.00, 0.00, NULL, N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85g'),
    (18, 18, 44, 44, 2, 30000.00, 0.00, 0.00, NULL, N'Đồ chơi cho chó mèo dạng bóng thừng BOBO'),
    (19, 19, 44, 44, 2, 30000.00, 0.00, 0.00, NULL, N'Đồ chơi cho chó mèo dạng bóng thừng BOBO'),
    (20, 20, NULL, NULL, 1, 125000.00, 0.00, 0.00, N'SPA00000005', N'Dịch vụ Spa'),
    (21, 21, NULL, NULL, 1, 150000.00, 0.00, 0.00, N'SPA00000006', N'Dịch vụ Spa'),
    (22, 22, NULL, NULL, 1, 175000.00, 0.00, 0.00, N'SPA00000007', N'Dịch vụ Spa'),
    (23, 23, NULL, NULL, 1, 200000.00, 0.00, 0.00, N'SPA00000008', N'Dịch vụ Spa'),
    (24, 24, NULL, NULL, 1, 225000.00, 0.00, 0.00, N'SPA00000009', N'Dịch vụ Spa'),
    (25, 25, NULL, NULL, 1, 250000.00, 0.00, 0.00, N'SPA00000010', N'Dịch vụ Spa'),
    (26, 26, NULL, NULL, 1, 90000.00, 0.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (27, 27, NULL, NULL, 1, 90000.00, 0.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (28, 28, NULL, NULL, 1, 90000.00, 0.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (29, 29, NULL, NULL, 1, 90000.00, 0.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (30, 30, 55, 55, 1, 35000.00, 0.00, 0.00, NULL, N'Đồ chơi dây thừng cho chó BOBO'),
    (31, 31, 55, 55, 1, 35000.00, 0.00, 0.00, NULL, N'Đồ chơi dây thừng cho chó BOBO'),
    (32, 32, 51, 51, 2, 85000.00, 0.00, 0.00, NULL, N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb'),
    (33, 32, 58, 58, 1, 30000.00, 0.00, 0.00, NULL, N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush'),
    (34, 33, 46, 46, 2, 50000.00, 0.00, 0.00, NULL, N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone'),
    (35, 34, 7, 6, 1, 450000.00, 0.00, 0.00, NULL, N'Lồng vận chuyển size L'),
    (36, 34, 36, 36, 1, 140000.00, 0.00, 0.00, NULL, N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1kg'),
    (37, 35, 17, NULL, 1, 160000.00, 0.00, 0.00, N'SP000015', N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 (lít); Đóng gói: 1 túi'),
    (38, 36, 18, NULL, 1, 10000.00, 0.00, 0.00, N'SP000016', N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói'),
    (39, 37, 47, NULL, 1, 180000.00, 0.00, 0.00, N'SP000045', N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam'),
    (40, 37, 51, NULL, 1, 85000.00, 0.00, 0.00, N'SP000049', N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb'),
    (41, 37, 58, NULL, 3, 30000.00, 0.00, 0.00, N'SP000056', N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush');
SET IDENTITY_INSERT dbo.[chi_tiet_hoa_don] OFF;

-- phieu_doi_tra (2 dòng)
DBCC CHECKIDENT ('dbo.phieu_doi_tra', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_doi_tra] ON;
INSERT INTO dbo.[phieu_doi_tra] ([id], [ma_phieu], [hoa_don_id], [nguoi_lap_id], [chenh_lech], [trang_thai], [tao_luc], [ly_do], [loai_phieu], [hoa_don_thay_the_id], [gia_tri_duoc_doi], [khoa_thao_tac])
VALUES
    (1, N'DT00000001', 18, 3, 5000.00, N'COMPLETED', DATEADD(second,-637200,CAST(@homNay AS datetime2)), N'đổi phụ kiện trong ba ngày, không hoàn tiền', N'EXCHANGE', 30, 30000, N'REAL_DATA_V1_EXCHANGE_29'),
    (2, N'DT00000002', 19, 3, 5000.00, N'COMPLETED', DATEADD(second,-633600,CAST(@homNay AS datetime2)), N'đổi phụ kiện trong ba ngày, không hoàn tiền', N'EXCHANGE', 31, 30000, N'REAL_DATA_V1_EXCHANGE_30');
SET IDENTITY_INSERT dbo.[phieu_doi_tra] OFF;

-- chi_tiet_doi_tra (2 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_doi_tra', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_doi_tra] ON;
INSERT INTO dbo.[chi_tiet_doi_tra] ([id], [phieu_doi_tra_id], [chi_tiet_hoa_don_id], [lo_hang_id], [so_luong], [tien_hoan])
VALUES
    (1, 1, 18, 44, 1, 30000.00),
    (2, 2, 19, 44, 1, 30000.00);
SET IDENTITY_INSERT dbo.[chi_tiet_doi_tra] OFF;

-- phieu_nhap_kho (62 dòng)
DBCC CHECKIDENT ('dbo.phieu_nhap_kho', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_nhap_kho] ON;
INSERT INTO dbo.[phieu_nhap_kho] ([id], [so_phieu], [nha_cung_cap_id], [nguoi_lap_id], [ngay_nhap], [tong_tien], [trang_thai], [ghi_chu], [da_tra])
VALUES
    (1, N'PN00000001', 1, 2, DATEADD(day,-7,@homNay), 720000.00, N'POSTED', NULL, 240000.00),
    (2, N'PN00000002', 5, 4, DATEADD(day,-36,@homNay), 1144000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 572000.00),
    (3, N'PN00000003', 6, 4, DATEADD(day,-36,@homNay), 78000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 39000.00),
    (4, N'PN00000004', 7, 4, DATEADD(day,-36,@homNay), 1901250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1901250.00),
    (5, N'PN00000005', 8, 4, DATEADD(day,-36,@homNay), 1365000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 682500.00),
    (6, N'PN00000006', 9, 4, DATEADD(day,-36,@homNay), 292500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 146250.00),
    (7, N'PN00000007', 10, 4, DATEADD(day,-36,@homNay), 1352000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1352000.00),
    (8, N'PN00000008', 5, 4, DATEADD(day,-36,@homNay), 276250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 138125.00),
    (9, N'PN00000009', 6, 4, DATEADD(day,-36,@homNay), 1872000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 936000.00),
    (10, N'PN00000010', 7, 4, DATEADD(day,-36,@homNay), 1543750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1543750.00),
    (11, N'PN00000011', 8, 4, DATEADD(day,-36,@homNay), 325000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 162500.00),
    (12, N'PN00000012', 9, 4, DATEADD(day,-36,@homNay), 68250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 34125.00),
    (13, N'PN00000013', 10, 4, DATEADD(day,-36,@homNay), 357500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 357500.00),
    (14, N'PN00000014', 5, 4, DATEADD(day,-36,@homNay), 523250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 261625.00),
    (15, N'PN00000015', 6, 4, DATEADD(day,-36,@homNay), 390000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 195000.00),
    (16, N'PN00000016', 7, 4, DATEADD(day,-36,@homNay), 3006250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 3006250.00),
    (17, N'PN00000017', 8, 4, DATEADD(day,-36,@homNay), 422500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 211250.00),
    (18, N'PN00000018', 9, 4, DATEADD(day,-36,@homNay), 526500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 263250.00),
    (19, N'PN00000019', 10, 4, DATEADD(day,-36,@homNay), 546000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 546000.00),
    (20, N'PN00000020', 5, 4, DATEADD(day,-36,@homNay), 377000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 188500.00),
    (21, N'PN00000021', 6, 4, DATEADD(day,-36,@homNay), 2730000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1365000.00),
    (22, N'PN00000022', 7, 4, DATEADD(day,-36,@homNay), 162500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 162500.00),
    (23, N'PN00000023', 8, 4, DATEADD(day,-36,@homNay), 929500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 464750.00),
    (24, N'PN00000024', 9, 4, DATEADD(day,-36,@homNay), 234000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 117000.00),
    (25, N'PN00000025', 10, 4, DATEADD(day,-36,@homNay), 971750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 971750.00),
    (26, N'PN00000026', 5, 4, DATEADD(day,-36,@homNay), 2866500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1433250.00),
    (27, N'PN00000027', 6, 4, DATEADD(day,-36,@homNay), 341250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 170625.00),
    (28, N'PN00000028', 7, 4, DATEADD(day,-36,@homNay), 208000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 208000.00),
    (29, N'PN00000029', 8, 4, DATEADD(day,-36,@homNay), 331500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 165750.00),
    (30, N'PN00000030', 9, 4, DATEADD(day,-36,@homNay), 351000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 175500.00),
    (31, N'PN00000031', 10, 4, DATEADD(day,-36,@homNay), 617500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 617500.00),
    (32, N'PN00000032', 5, 4, DATEADD(day,-36,@homNay), 2340000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1170000.00),
    (33, N'PN00000033', 6, 4, DATEADD(day,-36,@homNay), 3412500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1706250.00),
    (34, N'PN00000034', 7, 4, DATEADD(day,-36,@homNay), 143000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 143000.00),
    (35, N'PN00000035', 8, 4, DATEADD(day,-36,@homNay), 2392000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1196000.00),
    (36, N'PN00000036', 9, 4, DATEADD(day,-36,@homNay), 1326000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 663000.00),
    (37, N'PN00000037', 10, 4, DATEADD(day,-36,@homNay), 2518750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 2518750.00),
    (38, N'PN00000038', 5, 4, DATEADD(day,-36,@homNay), 1183000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 591500.00),
    (39, N'PN00000039', 6, 4, DATEADD(day,-36,@homNay), 789750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 394875.00),
    (40, N'PN00000040', 7, 4, DATEADD(day,-36,@homNay), 637000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 637000.00),
    (41, N'PN00000041', 8, 4, DATEADD(day,-36,@homNay), 1131000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 565500.00),
    (42, N'PN00000042', 9, 4, DATEADD(day,-36,@homNay), 4290000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 2145000.00),
    (43, N'PN00000043', 10, 4, DATEADD(day,-36,@homNay), 195000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 195000.00),
    (44, N'PN00000044', 5, 4, DATEADD(day,-36,@homNay), 536250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 268125.00),
    (45, N'PN00000045', 6, 4, DATEADD(day,-36,@homNay), 1950000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 975000.00),
    (46, N'PN00000046', 7, 4, DATEADD(day,-36,@homNay), 422500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 422500.00),
    (47, N'PN00000047', 8, 4, DATEADD(day,-36,@homNay), 728000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 364000.00),
    (48, N'PN00000048', 9, 4, DATEADD(day,-36,@homNay), 487500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 243750.00),
    (49, N'PN00000049', 10, 4, DATEADD(day,-36,@homNay), 1560000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1560000.00),
    (50, N'PN00000050', 5, 4, DATEADD(day,-36,@homNay), 442000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 221000.00),
    (51, N'PN00000051', 6, 4, DATEADD(day,-36,@homNay), 819000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 409500.00),
    (52, N'PN00000052', 7, 4, DATEADD(day,-36,@homNay), 1852500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1852500.00),
    (53, N'PN00000053', 8, 4, DATEADD(day,-36,@homNay), 715000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 357500.00),
    (54, N'PN00000054', 9, 4, DATEADD(day,-36,@homNay), 546000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 273000.00),
    (55, N'PN00000055', 10, 4, DATEADD(day,-36,@homNay), 3575000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 3575000.00),
    (56, N'PN00000056', 5, 4, DATEADD(day,-36,@homNay), 2541500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1270750.00),
    (57, N'PN00000057', 6, 4, DATEADD(day,-36,@homNay), 3042000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1521000.00),
    (58, N'PN00000058', 7, 4, DATEADD(day,-36,@homNay), 1300000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1300000.00),
    (59, N'PN00000059', 8, 4, DATEADD(day,-36,@homNay), 1014000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 507000.00),
    (60, N'PN00000060', 9, 4, DATEADD(day,-36,@homNay), 877500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 438750.00),
    (61, N'PN00000061', 10, 4, DATEADD(day,-36,@homNay), 637000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 637000.00),
    (62, N'PN00000062', 1, 2, DATEADD(day,-60,@homNay), 25978000, N'POSTED', NULL, 25978000);
SET IDENTITY_INSERT dbo.[phieu_nhap_kho] OFF;

-- chi_tiet_phieu_nhap (74 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_phieu_nhap', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_nhap] ON;
INSERT INTO dbo.[chi_tiet_phieu_nhap] ([id], [phieu_nhap_id], [san_pham_id], [so_lo], [han_su_dung], [so_luong], [gia_nhap])
VALUES
    (1, 1, 3, N'LO00000013', DATEADD(day,173,@homNay), 3, 240000.00),
    (2, 2, 17, N'LO00000016', NULL, 11, 104000.00),
    (3, 3, 18, N'LO00000017', DATEADD(day,173,@homNay), 12, 6500.00),
    (4, 4, 19, N'LO00000018', DATEADD(day,173,@homNay), 13, 146250.00),
    (5, 5, 20, N'LO00000019', NULL, 14, 97500.00),
    (6, 6, 21, N'LO00000020', DATEADD(day,173,@homNay), 15, 19500.00),
    (7, 7, 22, N'LO00000021', DATEADD(day,173,@homNay), 16, 84500.00),
    (8, 8, 23, N'LO00000022', DATEADD(day,173,@homNay), 17, 16250.00),
    (9, 9, 24, N'LO00000023', NULL, 18, 104000.00),
    (10, 10, 25, N'LO00000024', DATEADD(day,8,@homNay), 19, 81250.00),
    (11, 11, 26, N'LO00000025', DATEADD(day,173,@homNay), 20, 16250.00),
    (12, 12, 27, N'LO00000026', NULL, 21, 3250.00),
    (13, 13, 28, N'LO00000027', DATEADD(day,173,@homNay), 22, 16250.00),
    (14, 14, 29, N'LO00000028', DATEADD(day,173,@homNay), 23, 22750.00),
    (15, 15, 30, N'LO00000029', DATEADD(day,173,@homNay), 24, 16250.00),
    (16, 16, 31, N'LO00000030', DATEADD(day,173,@homNay), 25, 120250.00),
    (17, 17, 32, N'LO00000031', DATEADD(day,173,@homNay), 26, 16250.00),
    (18, 18, 33, N'LO00000032', DATEADD(day,173,@homNay), 27, 19500.00),
    (19, 19, 34, N'LO00000033', DATEADD(day,8,@homNay), 28, 19500.00),
    (20, 20, 35, N'LO00000034', DATEADD(day,173,@homNay), 29, 13000.00),
    (21, 21, 36, N'LO00000035', NULL, 30, 91000.00),
    (22, 22, 37, N'LO00000036', DATEADD(day,173,@homNay), 10, 16250.00),
    (23, 23, 38, N'LO00000037', DATEADD(day,173,@homNay), 11, 84500.00),
    (24, 24, 39, N'LO00000038', DATEADD(day,173,@homNay), 12, 19500.00),
    (25, 25, 40, N'LO00000039', DATEADD(day,173,@homNay), 13, 74750.00),
    (26, 26, 41, N'LO00000040', DATEADD(day,173,@homNay), 14, 204750.00),
    (27, 27, 42, N'LO00000041', DATEADD(day,173,@homNay), 15, 22750.00),
    (28, 28, 43, N'LO00000042', DATEADD(day,8,@homNay), 16, 13000.00),
    (29, 29, 44, N'LO00000043', NULL, 17, 19500.00),
    (30, 30, 45, N'LO00000044', DATEADD(day,173,@homNay), 18, 19500.00),
    (31, 31, 46, N'LO00000045', DATEADD(day,173,@homNay), 19, 32500.00),
    (32, 32, 47, N'LO00000046', DATEADD(day,173,@homNay), 20, 117000.00),
    (33, 33, 48, N'LO00000047', NULL, 21, 162500.00),
    (34, 34, 49, N'LO00000048', NULL, 22, 6500.00),
    (35, 35, 50, N'LO00000049', NULL, 23, 104000.00),
    (36, 36, 51, N'LO00000050', DATEADD(day,173,@homNay), 24, 55250.00),
    (37, 37, 52, N'LO00000051', NULL, 25, 100750.00),
    (38, 38, 53, N'LO00000052', NULL, 26, 45500.00),
    (39, 39, 54, N'LO00000053', NULL, 27, 29250.00),
    (40, 40, 55, N'LO00000054', NULL, 28, 22750.00),
    (41, 41, 56, N'LO00000055', NULL, 29, 39000.00),
    (42, 42, 57, N'LO00000056', NULL, 30, 143000.00),
    (43, 43, 58, N'LO00000057', DATEADD(day,173,@homNay), 10, 19500.00),
    (44, 44, 59, N'LO00000058', NULL, 11, 48750.00),
    (45, 45, 60, N'LO00000059', DATEADD(day,173,@homNay), 12, 162500.00),
    (46, 46, 61, N'LO00000060', NULL, 13, 32500.00),
    (47, 47, 62, N'LO00000061', NULL, 14, 52000.00),
    (48, 48, 63, N'LO00000062', NULL, 15, 32500.00),
    (49, 49, 64, N'LO00000063', DATEADD(day,173,@homNay), 16, 97500.00),
    (50, 50, 65, N'LO00000064', DATEADD(day,173,@homNay), 17, 26000.00),
    (51, 51, 66, N'LO00000065', DATEADD(day,173,@homNay), 18, 45500.00),
    (52, 52, 67, N'LO00000066', DATEADD(day,173,@homNay), 19, 97500.00),
    (53, 53, 68, N'LO00000067', NULL, 20, 35750.00),
    (54, 54, 69, N'LO00000068', NULL, 21, 26000.00),
    (55, 55, 70, N'LO00000069', DATEADD(day,8,@homNay), 22, 162500.00),
    (56, 56, 71, N'LO00000070', DATEADD(day,173,@homNay), 23, 110500.00),
    (57, 57, 72, N'LO00000071', NULL, 24, 126750.00),
    (58, 58, 73, N'LO00000072', NULL, 25, 52000.00),
    (59, 59, 74, N'LO00000073', NULL, 26, 39000.00),
    (60, 60, 75, N'LO00000074', NULL, 27, 32500.00),
    (61, 61, 76, N'LO00000075', NULL, 28, 22750.00),
    (62, 62, 3, N'LO00000001', DATEADD(day,173,@homNay), 47, 240000),
    (63, 62, 4, N'LO00000002', DATEADD(day,173,@homNay), 82, 36000),
    (64, 62, 5, N'LO00000003', DATEADD(day,173,@homNay), 3, 80000),
    (65, 62, 6, N'LO00000004', DATEADD(day,173,@homNay), 28, 62000),
    (66, 62, 7, N'LO00000005', DATEADD(day,173,@homNay), 8, 330000),
    (67, 62, 8, N'LO00000006', DATEADD(day,173,@homNay), 15, 20000),
    (68, 62, 9, N'LO00000007', DATEADD(day,173,@homNay), 2, 430000),
    (69, 62, 10, N'LO00000008', DATEADD(day,173,@homNay), 31, 55000),
    (70, 62, 11, N'LO00000009', DATEADD(day,173,@homNay), 12, 40000),
    (71, 62, 13, N'LO00000011', DATEADD(day,173,@homNay), 40, 30000),
    (72, 62, 14, N'LO00000012', DATEADD(day,173,@homNay), 9, 65000),
    (73, 62, 15, N'LO00000014', NULL, 20, 50000),
    (74, 62, 16, N'LO00000015', NULL, 20, 50000);
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_nhap] OFF;

-- phieu_xuat_kho (1 dòng)
DBCC CHECKIDENT ('dbo.phieu_xuat_kho', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_xuat_kho] ON;
INSERT INTO dbo.[phieu_xuat_kho] ([id], [so_phieu], [nguoi_lap_id], [loai_xuat], [ly_do], [tong_gia_tri], [trang_thai], [tao_luc])
VALUES
    (1, N'PX00000001', 4, N'WRITE_OFF', N'hàng hỏng bao bì', 3861000.00, N'POSTED', DATEADD(second,-549878,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[phieu_xuat_kho] OFF;

-- chi_tiet_phieu_xuat (1 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_phieu_xuat', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_xuat] ON;
INSERT INTO dbo.[chi_tiet_phieu_xuat] ([id], [phieu_xuat_id], [lo_hang_id], [so_luong], [gia_tri])
VALUES
    (1, 1, 57, 27, 3861000.00);
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_xuat] OFF;

-- thu_cung (37 dòng)
DBCC CHECKIDENT ('dbo.thu_cung', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[thu_cung] ON;
INSERT INTO dbo.[thu_cung] ([id], [ma_thu_cung], [khach_hang_id], [ten], [loai], [giong], [gioi_tinh], [mau_long], [can_nang], [tinh_trang_suc_khoe], [di_ung_benh_ly], [anh], [dang_theo_doi], [ngay_sinh], [tinh_cach], [tiem_chung], [so_mui_tiem], [ngay_tiem_gan_nhat])
VALUES
    (6, N'TC000001', 7, N'Mochi', N'DOG', N'Poodle', N'Đực', N'Trắng', 5.20, NULL, N'', NULL, 1, CONVERT(date,'2022-03-12',23), N'Hiền, sợ tiếng máy sấy', N'Dại (01/2026), 5 bệnh (02/2026)', 2, DATEADD(day,-60,@homNay)),
    (7, N'TC000002', 8, N'Bống', N'CAT', N'Anh lông ngắn', N'Cái', N'Xám', 4.10, NULL, N'Dị ứng cá ngừ', NULL, 1, CONVERT(date,'2021-07-05',23), N'Nhút nhát', N'Dại (03/2026)', 2, DATEADD(day,-60,@homNay)),
    (8, N'TC000003', 9, N'Lucky', N'DOG', N'Golden Retriever', N'Đực', N'Vàng', 29.50, NULL, N'', NULL, 1, CONVERT(date,'2020-11-20',23), N'Năng động, thân thiện', N'Dại, 7 bệnh (12/2025)', 2, DATEADD(day,-60,@homNay)),
    (9, N'TC000004', 10, N'Miu', N'CAT', N'Ba Tư', N'Cái', N'Kem', 3.80, NULL, N'Hen suyễn nhẹ', NULL, 1, CONVERT(date,'2023-02-02',23), N'Hay cào khi bị bế', N'', 0, NULL),
    (10, N'TC000005', 12, N'Coco', N'DOG', N'Corgi', N'Cái', N'Nâu trắng', 11.00, NULL, N'', NULL, 1, CONVERT(date,'2021-09-14',23), N'Ham ăn', N'Dại (06/2026)', 2, DATEADD(day,-60,@homNay)),
    (11, N'TC000006', 14, N'Bông', N'DOG', NULL, NULL, NULL, 4.00, NULL, NULL, NULL, 1, CONVERT(date,'2026-04-19',23), N'', N'Mũi 1|2026-09-01||tiêm phòng;Mũi 2|2026-09-01||tiêm phòng', 2, DATEADD(day,-72,@homNay)),
    (12, N'TC000007', 14, N'Mun', N'DOG', NULL, NULL, NULL, 4.00, NULL, NULL, NULL, 1, CONVERT(date,'2026-04-19',23), N'', N'', 0, NULL),
    (13, N'TC000008', 16, N'Mochi', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-09-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (14, N'TC000009', 17, N'Milo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-08-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (15, N'TC000010', 18, N'Bông', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-07-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (16, N'TC000011', 19, N'Lucky', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-06-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (17, N'TC000012', 20, N'Coco', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-05-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (18, N'TC000013', 21, N'Miu', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-04-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (19, N'TC000014', 22, N'Bắp', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-03-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (20, N'TC000015', 23, N'Đậu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-02-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (21, N'TC000016', 24, N'Sữa', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-01-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (22, N'TC000017', 25, N'Gạo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-12-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (23, N'TC000018', 26, N'Mochi', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-11-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (24, N'TC000019', 27, N'Milo', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-10-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (25, N'TC000020', 28, N'Bông', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-09-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (26, N'TC000021', 29, N'Lucky', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-08-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (27, N'TC000022', 30, N'Coco', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-07-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (28, N'TC000023', 31, N'Miu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-06-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (29, N'TC000024', 32, N'Bắp', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-05-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (30, N'TC000025', 33, N'Đậu', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-04-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (31, N'TC000026', 34, N'Sữa', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-03-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (32, N'TC000027', 35, N'Gạo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-02-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (33, N'TC000028', 16, N'Mochi', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-01-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (34, N'TC000029', 17, N'Milo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-12-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (35, N'TC000030', 18, N'Bông', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-11-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (36, N'TC000031', 19, N'Lucky', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-10-19',23), N'Hồ sơ : hiền, cần làm quen', N'Mũi cơ bản 1 ()|2026-07-04|2027-12-31|để kiểm tra điều kiện tiếp nhận;Mũi cơ bản 2 ()|2026-08-08|2027-12-31|để kiểm tra điều kiện tiếp nhận', 2, DATEADD(day,-72,@homNay)),
    (37, N'TC000032', 20, N'Coco', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-09-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung', 0, NULL),
    (38, N'TC000033', 21, N'Miu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-08-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung', 0, NULL),
    (39, N'TC000034', 22, N'Bắp', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-07-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung', 0, NULL),
    (40, N'TC000035', 23, N'Đậu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-06-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung', 0, NULL),
    (41, N'TC000036', 24, N'Sữa', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-05-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung', 0, NULL),
    (42, N'TC000037', 25, N'Gạo', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-04-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung', 0, NULL);
SET IDENTITY_INSERT dbo.[thu_cung] OFF;

-- hop_dong_luu_tru (9 dòng)
DBCC CHECKIDENT ('dbo.hop_dong_luu_tru', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[hop_dong_luu_tru] ON;
INSERT INTO dbo.[hop_dong_luu_tru] ([id], [ma_hop_dong], [khach_hang_id], [thu_cung_id], [phong_id], [hinh_thuc_gui], [ngay_vao], [ngay_ra_du_kien], [ngay_ra_thuc_te], [tien_coc], [phuong_thuc_coc], [khoa_thu_coc], [trang_thai], [gia_dem], [ghi_chu], [hoa_don_id], [da_nhan_coc], [nhan_coc_luc], [nhan_coc_boi], [phien_nhan_coc_id], [coc_yeu_cau], [ly_do_doi_coc])
VALUES
    (1, N'HDHT000001', 8, 7, 2, N'Qua đêm', DATEADD(second,-604800,CAST(@homNay AS datetime2)), DATEADD(second,-432000,CAST(@homNay AS datetime2)), NULL, 50000.00, N'TIEN_MAT', NULL, N'BOOKED', 150000.00, N'Hợp đồng từ dữ liệu mẫu', NULL, 0, NULL, NULL, NULL, 150000.00, NULL),
    (2, N'HDHT000002', 20, 17, 8, N'Qua đêm', DATEADD(second,-1429200,CAST(@homNay AS datetime2)), DATEADD(second,-1342800,CAST(@homNay AS datetime2)), DATEADD(second,-1342800,CAST(@homNay AS datetime2)), 90000.00, N'TIEN_MAT', N'LEGACY-COC-2', N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 26, 1, DATEADD(second,-1429200,CAST(@homNay AS datetime2)), 3, 22, 90000.00, NULL),
    (3, N'HDHT000003', 21, 18, 9, N'Qua đêm', DATEADD(second,-1252800,CAST(@homNay AS datetime2)), DATEADD(second,-1166400,CAST(@homNay AS datetime2)), DATEADD(second,-1166400,CAST(@homNay AS datetime2)), 90000.00, N'TIEN_MAT', N'LEGACY-COC-3', N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 27, 1, DATEADD(second,-1252800,CAST(@homNay AS datetime2)), 3, 24, 90000.00, NULL),
    (4, N'HDHT000004', 22, 19, 10, N'Qua đêm', DATEADD(second,-1076400,CAST(@homNay AS datetime2)), DATEADD(second,-990000,CAST(@homNay AS datetime2)), DATEADD(second,-990000,CAST(@homNay AS datetime2)), 90000.00, N'TIEN_MAT', N'LEGACY-COC-4', N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 28, 1, DATEADD(second,-1076400,CAST(@homNay AS datetime2)), 3, 26, 90000.00, NULL),
    (5, N'HDHT000005', 23, 20, 11, N'Qua đêm', DATEADD(second,-900000,CAST(@homNay AS datetime2)), DATEADD(second,-813600,CAST(@homNay AS datetime2)), DATEADD(second,-813600,CAST(@homNay AS datetime2)), 90000.00, N'TIEN_MAT', N'LEGACY-COC-5', N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 29, 1, DATEADD(second,-900000,CAST(@homNay AS datetime2)), 3, 28, 90000.00, NULL),
    (6, N'HDHT000006', 26, 23, 12, N'Qua đêm', DATEADD(second,-658800,CAST(@homNay AS datetime2)), DATEADD(second,-399600,CAST(@homNay AS datetime2)), NULL, 270000.00, N'TIEN_MAT', N'LEGACY-COC-6', N'CHECKED_IN', 180000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-662400,CAST(@homNay AS datetime2)), 3, 31, 270000.00, NULL),
    (7, N'HDHT000007', 27, 24, 13, N'Qua đêm', DATEADD(second,-658800,CAST(@homNay AS datetime2)), DATEADD(second,-399600,CAST(@homNay AS datetime2)), NULL, 375000.00, N'TIEN_MAT', N'LEGACY-COC-7', N'CHECKED_IN', 250000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-662400,CAST(@homNay AS datetime2)), 3, 31, 375000.00, NULL),
    (8, N'HDHT000008', 28, 25, 14, N'Qua đêm', DATEADD(second,-486000,CAST(@homNay AS datetime2)), DATEADD(second,-226800,CAST(@homNay AS datetime2)), NULL, 375000.00, N'TIEN_MAT', N'LEGACY-COC-8', N'BOOKED', 250000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-576000,CAST(@homNay AS datetime2)), 3, 32, 375000.00, NULL),
    (9, N'HDHT000009', 29, 26, 15, N'Qua đêm', DATEADD(second,-486000,CAST(@homNay AS datetime2)), DATEADD(second,-226800,CAST(@homNay AS datetime2)), NULL, 375000.00, N'TIEN_MAT', N'LEGACY-COC-9', N'BOOKED', 250000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-576000,CAST(@homNay AS datetime2)), 3, 32, 375000.00, NULL);
SET IDENTITY_INSERT dbo.[hop_dong_luu_tru] OFF;

-- lich_hen (17 dòng)
DBCC CHECKIDENT ('dbo.lich_hen', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lich_hen] ON;
INSERT INTO dbo.[lich_hen] ([id], [ma_lich_hen], [khach_hang_id], [thu_cung_id], [loai_dich_vu], [bat_dau], [ket_thuc], [trang_thai], [ghi_chu], [nhan_vien_id], [dich_vu_id])
VALUES
    (1, N'LH000001', 7, 6, N'SPA', DATEADD(second,-572400,CAST(@homNay AS datetime2)), DATEADD(second,-568800,CAST(@homNay AS datetime2)), N'SCHEDULED', N'Lịch hẹn từ dữ liệu mẫu', 17, 1),
    (2, N'LH000002', 34, 31, N'SPA', DATEADD(second,-1515600,CAST(@homNay AS datetime2)), DATEADD(second,-1508400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 5),
    (3, N'LH000003', 35, 32, N'SPA', DATEADD(second,-1425600,CAST(@homNay AS datetime2)), DATEADD(second,-1418400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 6),
    (4, N'LH000004', 16, 13, N'SPA', DATEADD(second,-1335600,CAST(@homNay AS datetime2)), DATEADD(second,-1328400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 7),
    (5, N'LH000005', 17, 14, N'SPA', DATEADD(second,-1245600,CAST(@homNay AS datetime2)), DATEADD(second,-1238400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 8),
    (6, N'LH000006', 18, 15, N'SPA', DATEADD(second,-1155600,CAST(@homNay AS datetime2)), DATEADD(second,-1148400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 9),
    (7, N'LH000007', 19, 16, N'SPA', DATEADD(second,-1094400,CAST(@homNay AS datetime2)), DATEADD(second,-1087200,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 10),
    (8, N'LH000008', 16, 13, N'SPA', DATEADD(second,-572400,CAST(@homNay AS datetime2)), DATEADD(second,-568800,CAST(@homNay AS datetime2)), N'CHECKED_IN', N'', 17, 5),
    (9, N'LH000009', 17, 14, N'SPA', DATEADD(second,-568800,CAST(@homNay AS datetime2)), DATEADD(second,-565200,CAST(@homNay AS datetime2)), N'CHECKED_IN', N'', 17, 5),
    (10, N'LH000010', 18, 15, N'SPA', DATEADD(second,-565200,CAST(@homNay AS datetime2)), DATEADD(second,-561600,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 5),
    (11, N'LH000011', 19, 16, N'SPA', DATEADD(second,-561600,CAST(@homNay AS datetime2)), DATEADD(second,-558000,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 5),
    (12, N'LH000012', 30, 27, N'SPA', DATEADD(second,-486000,CAST(@homNay AS datetime2)), DATEADD(second,-482400,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (13, N'LH000013', 31, 28, N'SPA', DATEADD(second,-399600,CAST(@homNay AS datetime2)), DATEADD(second,-396000,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (14, N'LH000014', 32, 29, N'SPA', DATEADD(second,-313200,CAST(@homNay AS datetime2)), DATEADD(second,-309600,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (15, N'LH000015', 33, 30, N'SPA', DATEADD(second,-226800,CAST(@homNay AS datetime2)), DATEADD(second,-223200,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (16, N'LH000016', 34, 31, N'SPA', DATEADD(second,-140400,CAST(@homNay AS datetime2)), DATEADD(second,-136800,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (17, N'LH000017', 35, 32, N'SPA', DATEADD(second,-54000,CAST(@homNay AS datetime2)), DATEADD(second,-50400,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5);
SET IDENTITY_INSERT dbo.[lich_hen] OFF;

-- nhat_ky_cham_soc (6 dòng)
DBCC CHECKIDENT ('dbo.nhat_ky_cham_soc', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[nhat_ky_cham_soc] ON;
INSERT INTO dbo.[nhat_ky_cham_soc] ([id], [ma_nhat_ky], [hop_dong_id], [nhan_vien_id], [thoi_gian], [an_uong], [ve_sinh], [tinh_trang], [tep_dinh_kem])
VALUES
    (1, N'NK00000001', 2, 17, DATEADD(second,-1357200,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (2, N'NK00000002', 3, 17, DATEADD(second,-1180800,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (3, N'NK00000003', 4, 17, DATEADD(second,-1004400,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (4, N'NK00000004', 5, 17, DATEADD(second,-828000,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (5, N'NK00000005', 6, 17, DATEADD(second,-524678,CAST(@homNay AS datetime2)), N'Đã cho ăn theo khẩu phần', N'Đã vệ sinh', N'Nhật ký chăm sóc', NULL),
    (6, N'NK00000006', 7, 17, DATEADD(second,-524678,CAST(@homNay AS datetime2)), N'Đã cho ăn theo khẩu phần', N'Đã vệ sinh', N'Nhật ký chăm sóc', NULL);
SET IDENTITY_INSERT dbo.[nhat_ky_cham_soc] OFF;

-- phieu_tiep_nhan_spa (10 dòng)
DBCC CHECKIDENT ('dbo.phieu_tiep_nhan_spa', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_tiep_nhan_spa] ON;
INSERT INTO dbo.[phieu_tiep_nhan_spa] ([id], [ma_phieu], [lich_hen_id], [thu_cung_id], [goi_id], [mo_ta_dich_vu], [don_gia], [so_luong], [can_nang_thuc_te], [tong_tien], [trang_thai], [tao_luc], [nhan_vien_id], [ghi_chu], [anh], [hoa_don_id])
VALUES
    (1, N'SPA00000001', 2, 31, 5, N'Tắm cơ bản', 125000.00, 1, 3.50, 125000.00, N'PAID', DATEADD(second,-1515600,CAST(@homNay AS datetime2)), 17, N'', NULL, 20),
    (2, N'SPA00000002', 3, 32, 6, N'Tắm dưỡng lông', 150000.00, 1, 3.50, 150000.00, N'PAID', DATEADD(second,-1425600,CAST(@homNay AS datetime2)), 17, N'', NULL, 21),
    (3, N'SPA00000003', 4, 13, 7, N'Cắt tỉa tạo kiểu', 175000.00, 1, 3.50, 175000.00, N'PAID', DATEADD(second,-1335600,CAST(@homNay AS datetime2)), 17, N'', NULL, 22),
    (4, N'SPA00000004', 5, 14, 8, N'Vệ sinh tai và móng', 200000.00, 1, 3.50, 200000.00, N'PAID', DATEADD(second,-1245600,CAST(@homNay AS datetime2)), 17, N'', NULL, 23),
    (5, N'SPA00000005', 6, 15, 9, N'Chải lông và gỡ rối', 225000.00, 1, 3.50, 225000.00, N'PAID', DATEADD(second,-1155600,CAST(@homNay AS datetime2)), 17, N'', NULL, 24),
    (6, N'SPA00000006', 7, 16, 10, N'Chăm sóc toàn diện', 250000.00, 1, 3.50, 250000.00, N'PAID', DATEADD(second,-1094400,CAST(@homNay AS datetime2)), 17, N'', NULL, 25),
    (7, N'SPA00000007', 8, 13, 5, N'Tắm cơ bản', 125000.00, 1, 3.50, 125000.00, N'RECEIVED', DATEADD(second,-572400,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL),
    (8, N'SPA00000008', 9, 14, 5, N'Tắm cơ bản', 125000.00, 1, 3.50, 125000.00, N'IN_PROGRESS', DATEADD(second,-568800,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL),
    (9, N'SPA00000009', 10, 15, 5, N'Tắm cơ bản', 125000.00, 1, 3.50, 125000.00, N'COMPLETED', DATEADD(second,-565200,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL),
    (10, N'SPA00000010', 11, 16, 5, N'Tắm cơ bản', 125000.00, 1, 3.50, 125000.00, N'COMPLETED', DATEADD(second,-561600,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL);
SET IDENTITY_INSERT dbo.[phieu_tiep_nhan_spa] OFF;

-- voucher (2 dòng)
DBCC CHECKIDENT ('dbo.voucher', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[voucher] ON;
INSERT INTO dbo.[voucher] ([id], [ma_voucher], [khach_hang_id], [menh_gia], [hieu_luc_den], [su_dung_luc], [chi_ap_dung_spa], [so_luong], [da_dung], [don_toi_thieu], [trang_thai], [khoa_doi_diem])
VALUES
    (1, N'VOUCHER000001', NULL, 50000.00, DATEADD(second,7220638,CAST(@homNay AS datetime2)), NULL, 0, 100, 0, 300000.00, N'ACTIVE', NULL),
    (2, N'VOUCHER000002', 16, 150000.00, DATEADD(second,4659322,CAST(@homNay AS datetime2)), NULL, 1, 1, 0, 0.00, N'ACTIVE', N'REAL_DATA_V1_REDEEM');
SET IDENTITY_INSERT dbo.[voucher] OFF;

-- yeu_cau_phe_duyet (4 dòng)
DBCC CHECKIDENT ('dbo.yeu_cau_phe_duyet', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[yeu_cau_phe_duyet] ON;
INSERT INTO dbo.[yeu_cau_phe_duyet] ([id], [ma_yeu_cau], [loai], [nguoi_gui_id], [loai_doi_tuong], [ma_doi_tuong], [ly_do], [trang_thai], [nguoi_xu_ly_id], [phan_hoi], [gui_luc], [xu_ly_luc], [noi_dung_truoc], [noi_dung_sau], [payload])
VALUES
    (1, N'YC00000001', N'KIEM_KE', 4, N'KIEM_KE', N'1', N'chờ duyệt chênh lệch', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'Tồn sổ 3', N'Tồn thực 2', N''),
    (2, N'YC00000002', N'GIA_BAN', 4, N'GIA_BAN', N'44', N'thử duyệt hoặc từ chối', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'30000', N'35000', N''),
    (3, N'YC00000003', N'GIA_BAN', 4, N'GIA_BAN', N'53', N'thử duyệt hoặc từ chối', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'70000', N'75000', N''),
    (4, N'YC00000004', N'NGHI_PHEP', 5, N'NGHI_PHEP', N'17', N'xin nghỉ phép', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'Đang phân công', N'Nghỉ một ngày', N'2026-10-22');
SET IDENTITY_INSERT dbo.[yeu_cau_phe_duyet] OFF;

COMMIT;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT>0 ROLLBACK;
    THROW;
END CATCH;
