-- DỮ LIỆU MẪU: script này XÓA toàn bộ dữ liệu ứng dụng rồi nạp lại.
-- Chỉ chạy trên database demo/test. Sao lưu trước khi chạy.
-- Mỗi bảng có dữ liệu dùng một INSERT nhiều dòng, giữ ID và khóa ngoại đồng nhất.
-- PN-DAU-KY và lịch sử điểm đầu kỳ giải thích số dư ban đầu của bộ mẫu.
SET NOCOUNT ON;
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET ANSI_PADDING ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET NUMERIC_ROUNDABORT OFF;
DECLARE @homNay date=CAST(GETDATE() AS date);
BEGIN TRY
BEGIN TRANSACTION;

-- Xóa bảng con trước, bảng cha sau; không tắt kiểm tra khóa ngoại.
DELETE FROM dbo.[yeu_cau_phe_duyet];
DELETE FROM dbo.[voucher];
DELETE FROM dbo.[vai_tro_quyen];
DELETE FROM dbo.[tiem_phong];
DELETE FROM dbo.[phieu_cham_soc];
DELETE FROM dbo.[chi_tiet_phieu_spa];
DELETE FROM dbo.[phieu_tiep_nhan_spa];
DELETE FROM dbo.[nhat_ky_cham_soc];
DELETE FROM dbo.[lich_hen];
DELETE FROM dbo.[khoan_thu];
DELETE FROM dbo.[hop_dong_luu_tru];
DELETE FROM dbo.[thu_cung];
DELETE FROM dbo.[chi_tiet_phieu_xuat];
DELETE FROM dbo.[phieu_xuat_kho];
DELETE FROM dbo.[chi_tiet_phieu_nhap];
DELETE FROM dbo.[phieu_nhap_kho];
DELETE FROM dbo.[chi_tiet_doi_tra];
DELETE FROM dbo.[phieu_doi_tra];
DELETE FROM dbo.[phan_bo_lo_hang];
DELETE FROM dbo.[lich_su_diem];
DELETE FROM dbo.[hoa_don];
DELETE FROM dbo.[chi_tiet_don_hang];
DELETE FROM dbo.[don_hang];
DELETE FROM dbo.[phien_lam_viec];
DELETE FROM dbo.[nhat_ky_he_thong];
DELETE FROM dbo.[nhac_viec_da_xem];
DELETE FROM dbo.[lich_su_gia];
DELETE FROM dbo.[chi_tiet_gio_hang_treo];
DELETE FROM dbo.[gio_hang_treo];
DELETE FROM dbo.[de_xuat_gia];
DELETE FROM dbo.[chot_ngay];
DELETE FROM dbo.[chi_tiet_kiem_ke];
DELETE FROM dbo.[cai_dat];
DELETE FROM dbo.[bien_ban_kiem_ke];
DELETE FROM dbo.[tai_khoan];
DELETE FROM dbo.[lo_hang];
DELETE FROM dbo.[san_pham];
DELETE FROM dbo.[quyen];
DELETE FROM dbo.[bang_gia_hotel];
DELETE FROM dbo.[phong_hotel];
DELETE FROM dbo.[lich_lam_viec];
DELETE FROM dbo.[nhan_vien];
DELETE FROM dbo.[nha_cung_cap];
DELETE FROM dbo.[khach_hang];
DELETE FROM dbo.[bang_gia_spa];
DELETE FROM dbo.[goi_dich_vu_spa];
DELETE FROM dbo.[chuong_trinh_khuyen_mai];
DELETE FROM dbo.[ca_lam_viec];

-- Chèn bảng cha trước, bảng con sau.
-- ca_lam_viec (3 dòng)
DBCC CHECKIDENT ('dbo.ca_lam_viec', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[ca_lam_viec] ON;
INSERT INTO dbo.[ca_lam_viec] ([id], [ma_ca], [ten_ca], [gio_bat_dau], [gio_ket_thuc], [so_nhan_su_toi_thieu])
VALUES
    (1, N'CA_SANG', N'Ca sáng', CONVERT(time,'07:00:00'), CONVERT(time,'14:00:00'), 2),
    (2, N'CA_CHIEU', N'Ca chiều', CONVERT(time,'14:00:00'), CONVERT(time,'21:00:00'), 2),
    (4, N'CA_TOI', N'Tối', CONVERT(time,'17:00:00'), CONVERT(time,'21:00:00'), 1);
SET IDENTITY_INSERT dbo.[ca_lam_viec] OFF;

-- chuong_trinh_khuyen_mai (1 dòng)
DBCC CHECKIDENT ('dbo.chuong_trinh_khuyen_mai', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chuong_trinh_khuyen_mai] ON;
INSERT INTO dbo.[chuong_trinh_khuyen_mai] ([id], [ma_chuong_trinh], [ten], [loai_giam], [gia_tri], [bat_dau], [ket_thuc], [nguon], [trang_thai], [don_toi_thieu])
VALUES
    (1, N'KM001', N'Giảm 10% sản phẩm trong tháng', N'PERCENT', 10.00, DATEADD(second,-2160000,CAST(@homNay AS datetime2)), DATEADD(second,518400,CAST(@homNay AS datetime2)), N'PRODUCT', N'ACTIVE', 0.00);
SET IDENTITY_INSERT dbo.[chuong_trinh_khuyen_mai] OFF;

-- goi_dich_vu_spa (10 dòng)
DBCC CHECKIDENT ('dbo.goi_dich_vu_spa', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[goi_dich_vu_spa] ON;
INSERT INTO dbo.[goi_dich_vu_spa] ([id], [ma_goi], [ten_goi], [mo_ta], [thoi_gian_du_kien], [trang_thai])
VALUES
    (1, N'DV001', N'Tắm và sấy khô', N'Tắm, sấy, chải lông', 60, N'ACTIVE'),
    (2, N'DV002', N'Cắt tỉa tạo kiểu', N'Cắt tỉa theo giống', 90, N'ACTIVE'),
    (3, N'DV003', N'Cắt móng, vệ sinh tai', N'Cắt và dũa móng, lau tai', 20, N'ACTIVE'),
    (4, N'DV000004', N'Tắm và chăm sóc', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 60, N'ACTIVE'),
    (5, N'DV000005', N'Tắm cơ bản', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 45, N'ACTIVE'),
    (6, N'DV000006', N'Tắm dưỡng lông', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 60, N'ACTIVE'),
    (7, N'DV000007', N'Cắt tỉa tạo kiểu (DV000007)', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 75, N'ACTIVE'),
    (8, N'DV000008', N'Vệ sinh tai và móng', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 90, N'ACTIVE'),
    (9, N'DV000009', N'Chải lông và gỡ rối', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 105, N'ACTIVE'),
    (10, N'DV000010', N'Chăm sóc toàn diện', N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.', 120, N'ACTIVE');
SET IDENTITY_INSERT dbo.[goi_dich_vu_spa] OFF;

-- bang_gia_spa (47 dòng)
DBCC CHECKIDENT ('dbo.bang_gia_spa', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[bang_gia_spa] ON;
INSERT INTO dbo.[bang_gia_spa] ([id], [goi_id], [loai_thu_cung], [khoang_can], [can_nang_tu], [can_nang_den], [don_gia])
VALUES
    (1, 1, N'DOG', N'< 5kg', 0.00, 5.00, 150000.00),
    (2, 1, N'DOG', N'5 - 10kg', 5.00, 10.00, 200000.00),
    (3, 1, N'DOG', N'> 10kg', 10.00, NULL, 280000.00),
    (4, 1, N'CAT', N'Mọi cân nặng', 0.00, NULL, 180000.00),
    (5, 2, N'DOG', N'< 5kg', 0.00, 5.00, 230000.00),
    (6, 2, N'DOG', N'5 - 10kg', 5.00, 10.00, 300000.00),
    (7, 2, N'DOG', N'> 10kg', 10.00, NULL, 400000.00),
    (8, 2, N'CAT', N'Mọi cân nặng', 0.00, NULL, 250000.00),
    (9, 3, N'DOG', N'Mọi cân nặng', 0.00, NULL, 80000.00),
    (10, 3, N'CAT', N'Mọi cân nặng', 0.00, NULL, 80000.00),
    (11, 4, N'DOG', N'Dưới 5kg', 0.00, 5.00, 200000.00),
    (12, 5, N'DOG', N'< 5kg', 0.00, 5.00, 125000.00),
    (13, 5, N'DOG', N'5 - 10kg', 5.00, 10.00, 250000.00),
    (14, 5, N'DOG', N'>= 10kg', 10.00, NULL, 375000.00),
    (15, 5, N'CAT', N'< 5kg', 0.00, 5.00, 125000.00),
    (16, 5, N'CAT', N'5 - 10kg', 5.00, 10.00, 250000.00),
    (17, 5, N'CAT', N'>= 10kg', 10.00, NULL, 375000.00),
    (18, 6, N'DOG', N'< 5kg', 0.00, 5.00, 150000.00),
    (19, 6, N'DOG', N'5 - 10kg', 5.00, 10.00, 300000.00),
    (20, 6, N'DOG', N'>= 10kg', 10.00, NULL, 450000.00),
    (21, 6, N'CAT', N'< 5kg', 0.00, 5.00, 150000.00),
    (22, 6, N'CAT', N'5 - 10kg', 5.00, 10.00, 300000.00),
    (23, 6, N'CAT', N'>= 10kg', 10.00, NULL, 450000.00),
    (24, 7, N'DOG', N'< 5kg', 0.00, 5.00, 175000.00),
    (25, 7, N'DOG', N'5 - 10kg', 5.00, 10.00, 350000.00),
    (26, 7, N'DOG', N'>= 10kg', 10.00, NULL, 525000.00),
    (27, 7, N'CAT', N'< 5kg', 0.00, 5.00, 175000.00),
    (28, 7, N'CAT', N'5 - 10kg', 5.00, 10.00, 350000.00),
    (29, 7, N'CAT', N'>= 10kg', 10.00, NULL, 525000.00),
    (30, 8, N'DOG', N'< 5kg', 0.00, 5.00, 200000.00),
    (31, 8, N'DOG', N'5 - 10kg', 5.00, 10.00, 400000.00),
    (32, 8, N'DOG', N'>= 10kg', 10.00, NULL, 600000.00),
    (33, 8, N'CAT', N'< 5kg', 0.00, 5.00, 200000.00),
    (34, 8, N'CAT', N'5 - 10kg', 5.00, 10.00, 400000.00),
    (35, 8, N'CAT', N'>= 10kg', 10.00, NULL, 600000.00),
    (36, 9, N'DOG', N'< 5kg', 0.00, 5.00, 225000.00),
    (37, 9, N'DOG', N'5 - 10kg', 5.00, 10.00, 450000.00),
    (38, 9, N'DOG', N'>= 10kg', 10.00, NULL, 675000.00),
    (39, 9, N'CAT', N'< 5kg', 0.00, 5.00, 225000.00),
    (40, 9, N'CAT', N'5 - 10kg', 5.00, 10.00, 450000.00),
    (41, 9, N'CAT', N'>= 10kg', 10.00, NULL, 675000.00),
    (42, 10, N'DOG', N'< 5kg', 0.00, 5.00, 250000.00),
    (43, 10, N'DOG', N'5 - 10kg', 5.00, 10.00, 500000.00),
    (44, 10, N'DOG', N'>= 10kg', 10.00, NULL, 750000.00),
    (45, 10, N'CAT', N'< 5kg', 0.00, 5.00, 250000.00),
    (46, 10, N'CAT', N'5 - 10kg', 5.00, 10.00, 500000.00),
    (47, 10, N'CAT', N'>= 10kg', 10.00, NULL, 750000.00);
SET IDENTITY_INSERT dbo.[bang_gia_spa] OFF;

-- khach_hang (29 dòng)
DBCC CHECKIDENT ('dbo.khach_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[khach_hang] ON;
INSERT INTO dbo.[khach_hang] ([id], [ma_khach_hang], [ho_ten], [so_dien_thoai], [dia_chi], [ngay_sinh], [diem_tich_luy], [hang_thanh_vien], [dang_hoat_dong], [email], [ghi_chu], [ngay_tao])
VALUES
    (7, N'KH001', N'Nguyễn Văn An', N'0901234567', N'12 Nguyễn Trãi, Hà Nội', NULL, 1268, N'Vàng', 1, N'an.nguyen@email.com', N'Thích giao hàng buổi tối', DATEADD(second,-555363,CAST(@homNay AS datetime2))),
    (8, N'KH002', N'Trần Thị Bình', N'0912345678', N'45 Lê Lợi, TP.HCM', NULL, 4860, N'Kim Cương', 1, N'binh.tran@email.com', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2))),
    (9, N'KH003', N'Lê Hoàng Cường', N'0923456789', N'8 Hùng Vương, Đà Nẵng', NULL, 3210, N'Bạch Kim', 1, N'', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2))),
    (10, N'KH004', N'Phạm Minh Đức', N'0934567890', N'21 Lạch Tray, Hải Phòng', NULL, 840, N'Bạc', 1, N'duc.pham@email.com', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2))),
    (11, N'KH005', N'Hoàng Thị Em', N'0945678901', N'5 Ninh Kiều, Cần Thơ', NULL, 230, N'Đồng', 1, N'', N'Dị ứng mùi nước hoa mạnh', DATEADD(second,-555363,CAST(@homNay AS datetime2))),
    (12, N'KH006', N'Vũ Quang Hải', N'0956789012', N'77 Cầu Giấy, Hà Nội', NULL, 1980, N'Vàng', 1, N'hai.vu@email.com', N'', DATEADD(second,-555363,CAST(@homNay AS datetime2))),
    (13, N'KH000007', N'Lê Minh Châu', N'0999199199', NULL, NULL, 199, N'REGULAR', 1, N'', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-527699,CAST(@homNay AS datetime2))),
    (14, N'KH000008', N'Nguyễn Gia Hân', N'0999200200', NULL, NULL, 200, N'REGULAR', 1, N'', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-527699,CAST(@homNay AS datetime2))),
    (15, N'KH000009', N'Võ Anh Thư', N'0999111222', NULL, NULL, 0, N'REGULAR', 1, N'', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-527699,CAST(@homNay AS datetime2))),
    (16, N'KH000010', N'Nguyễn Minh Anh', N'0009000001', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 249, N'Đồng', 1, N'khachhang16@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (17, N'KH000011', N'Trần Gia Hân', N'0009000002', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 23, N'Đồng', 1, N'khachhang17@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (18, N'KH000012', N'Lê Hoàng Nam', N'0009000003', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 44, N'Đồng', 1, N'khachhang18@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (19, N'KH000013', N'Phạm Ngọc Linh', N'0009000004', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 55, N'Đồng', 1, N'khachhang19@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (20, N'KH000014', N'Võ Khánh An', N'0009000005', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 18, N'Đồng', 1, N'khachhang20@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (21, N'KH000015', N'Đặng Thanh Tâm', N'0009000006', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 22, N'Đồng', 1, N'khachhang21@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (22, N'KH000016', N'Bùi Quang Huy', N'0009000007', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 14, N'Đồng', 1, N'khachhang22@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (23, N'KH000017', N'Đỗ Bảo Ngọc', N'0009000008', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 57, N'Đồng', 1, N'khachhang23@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (24, N'KH000018', N'Hồ Minh Khang', N'0009000009', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 12, N'Đồng', 1, N'khachhang24@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (25, N'KH000019', N'Dương Tuệ Nhi', N'0009000010', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 5, N'Đồng', 1, N'khachhang25@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (26, N'KH000020', N'Nguyễn Minh Anh', N'0009000011', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 1, N'Đồng', 1, N'khachhang26@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (27, N'KH000021', N'Trần Gia Hân', N'0009000012', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 2, N'Đồng', 1, N'khachhang27@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (28, N'KH000022', N'Lê Hoàng Nam', N'0009000013', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 7, N'Đồng', 1, N'khachhang28@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (29, N'KH000023', N'Phạm Ngọc Linh', N'0009000014', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 7, N'Đồng', 1, N'khachhang29@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (30, N'KH000024', N'Võ Khánh An', N'0009000015', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 18, N'Đồng', 1, N'khachhang30@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (31, N'KH000025', N'Đặng Thanh Tâm', N'0009000016', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 5, N'Đồng', 1, N'khachhang31@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (32, N'KH000026', N'Bùi Quang Huy', N'0009000017', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 6, N'Đồng', 1, N'khachhang32@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (33, N'KH000027', N'Đỗ Bảo Ngọc', N'0009000018', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 6, N'Đồng', 1, N'khachhang33@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (34, N'KH000028', N'Hồ Minh Khang', N'0009000019', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 12, N'Đồng', 1, N'khachhang34@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2))),
    (35, N'KH000029', N'Dương Tuệ Nhi', N'0009000020', N'Quận Bình Thạnh, TP. Hồ Chí Minh', NULL, 15, N'Đồng', 1, N'khachhang35@example.com', N'Ưu tiên liên hệ qua điện thoại.', DATEADD(second,-3110400,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[khach_hang] OFF;

-- nha_cung_cap (10 dòng)
DBCC CHECKIDENT ('dbo.nha_cung_cap', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[nha_cung_cap] ON;
INSERT INTO dbo.[nha_cung_cap] ([id], [ma_ncc], [ten], [so_dien_thoai], [dia_chi], [nguoi_dai_dien], [nhom_mat_hang], [ghi_chu], [trang_thai], [ma_so_thue], [dieu_khoan])
VALUES
    (1, N'NCC001', N'Royal Canin VN', N'0243825678', N'Hà Nội', N'Nguyễn Minh Tâm', NULL, NULL, N'COOPERATING', N'0101234567', N'30 ngày'),
    (2, N'NCC002', N'Mars Petcare VN', N'0283826789', N'TP.HCM', N'Trần Thành Long', NULL, NULL, N'COOPERATING', N'0309876543', N'15 ngày'),
    (3, N'NCC003', N'Orijen Import', N'0243827890', N'Hà Nội', N'Lê Đức Anh', NULL, NULL, N'COOPERATING', N'0105551234', N'Trả ngay'),
    (4, N'NCC004', N'PetShop Thú Y', N'0911222333', N'TP.HCM', N'Phạm Quang Vinh', NULL, NULL, N'COOPERATING', N'0312223344', N'30 ngày'),
    (5, N'NCC000005', N'Công ty Thực phẩm thú cưng An Phú', N'0008000001', N'TP. Hồ Chí Minh', N'Nguyễn Thanh Hà', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (6, N'NCC000006', N'Nhà phân phối Minh Khang', N'0008000002', N'TP. Hồ Chí Minh', N'Trần Quốc Huy', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (7, N'NCC000007', N'Công ty Phụ kiện thú cưng Hưng Thịnh', N'0008000003', N'TP. Hồ Chí Minh', N'Lê Bảo Trâm', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (8, N'NCC000008', N'Nhà phân phối Happy Pet', N'0008000004', N'TP. Hồ Chí Minh', N'Phạm Minh Tuấn', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (9, N'NCC000009', N'Công ty Chăm sóc thú cưng Đại Việt', N'0008000005', N'TP. Hồ Chí Minh', N'Võ Thu Ngân', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO'),
    (10, N'NCC000010', N'Nhà phân phối Thiên Phúc', N'0008000006', N'TP. Hồ Chí Minh', N'Đỗ Hoàng Long', NULL, N'Thanh toán theo điều khoản hợp tác.', N'COOPERATING', NULL, N'30 ngày — dữ liệu DEMO');
SET IDENTITY_INSERT dbo.[nha_cung_cap] OFF;

-- nhan_vien (11 dòng)
DBCC CHECKIDENT ('dbo.nhan_vien', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[nhan_vien] ON;
INSERT INTO dbo.[nhan_vien] ([id], [ma_nhan_vien], [ho_ten], [ngay_sinh], [so_dien_thoai], [dia_chi], [chuc_vu], [ngay_vao_lam], [anh_dai_dien], [dang_lam_viec], [cccd])
VALUES
    (1, N'NV1791027506727', N'Vi', NULL, N'0123456789', NULL, N'Cửa hàng trưởng', CONVERT(date,'2026-10-03',23), NULL, 1, NULL),
    (8, N'NV001', N'Nguyễn Văn An', NULL, N'0901111222', NULL, N'Cửa hàng trưởng', CONVERT(date,'2020-03-01',23), NULL, 1, N'001090012345'),
    (9, N'NV002', N'Phạm Thu Lan', NULL, N'0902222333', NULL, N'Nhân viên bán hàng', CONVERT(date,'2021-06-15',23), NULL, 1, N'001092045678'),
    (10, N'NV003', N'Hoàng Văn Nam', NULL, N'0903333444', NULL, N'Nhân viên kho', CONVERT(date,'2022-01-10',23), NULL, 1, N'001088078901'),
    (11, N'NV004', N'Trần Thị Hoa', NULL, N'0904444555', NULL, N'Nhân viên Spa/Hotel', CONVERT(date,'2022-09-05',23), NULL, 1, N'001095011223'),
    (12, N'NV005', N'Lê Văn Hùng', NULL, N'0905555666', NULL, N'Nhân viên Spa/Hotel', CONVERT(date,'2023-02-20',23), NULL, 1, N'001093099887'),
    (13, N'NV006', N'Đặng Minh Tú', NULL, N'0906666777', NULL, N'Nhân viên bán hàng', CONVERT(date,'2024-08-01',23), NULL, 1, N'001096055443'),
    (14, N'NV000007', N'Nguyễn Minh Quân', NULL, N'0999000001', NULL, N'Cửa hàng trưởng', CONVERT(date,'2026-10-19',23), NULL, 1, NULL),
    (15, N'NV000008', N'Trần Ngọc Anh', NULL, N'0999000002', NULL, N'Nhân viên bán hàng', CONVERT(date,'2026-10-19',23), NULL, 1, NULL),
    (16, N'NV000009', N'Lê Hoàng Phúc', NULL, N'0999000003', NULL, N'Nhân viên kho', CONVERT(date,'2026-10-19',23), NULL, 1, NULL),
    (17, N'NV000010', N'Phạm Thảo Nhi', NULL, N'0999000004', NULL, N'Nhân viên Spa/Hotel', CONVERT(date,'2026-10-19',23), NULL, 1, NULL);
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
INSERT INTO dbo.[phong_hotel] ([id], [ma_phong], [loai_phong], [gia_dem], [trang_thai], [suc_chua], [ghi_chu])
VALUES
    (1, N'P01', N'Tiêu chuẩn', 150000.00, N'AVAILABLE', 1, N''),
    (2, N'P02', N'Tiêu chuẩn', 150000.00, N'AVAILABLE', 1, N''),
    (3, N'P03', N'Mèo', 120000.00, N'CLEANING', 1, N'Có cây leo'),
    (4, N'P04', N'VIP', 300000.00, N'AVAILABLE', 2, N'Điều hòa riêng'),
    (5, N'P05', N'VIP', 300000.00, N'AVAILABLE', 2, N'Điều hòa riêng'),
    (6, N'P06', N'Tiêu chuẩn', 150000.00, N'MAINTENANCE', 1, N'Sửa quạt thông gió'),
    (7, N'P000007', N'Tiêu chuẩn', 200000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (8, N'P000008', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (9, N'P000009', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (10, N'P000010', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (11, N'P000011', N'Tiêu chuẩn', 180000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (12, N'P000012', N'Tiêu chuẩn', 180000.00, N'OCCUPIED', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (13, N'P000013', N'VIP', 250000.00, N'OCCUPIED', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (14, N'P000014', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (15, N'P000015', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (16, N'P000016', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.'),
    (17, N'P000017', N'VIP', 250000.00, N'AVAILABLE', 1, N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.');
SET IDENTITY_INSERT dbo.[phong_hotel] OFF;

-- bang_gia_hotel (10 dòng)
DBCC CHECKIDENT ('dbo.bang_gia_hotel', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[bang_gia_hotel] ON;
INSERT INTO dbo.[bang_gia_hotel] ([id], [phong_id], [khung_gio], [can_nang_tu], [can_nang_den], [don_gia])
VALUES
    (1, 8, N'DAILY', 0.00, 999.00, 180000.00),
    (2, 9, N'DAILY', 0.00, 999.00, 180000.00),
    (3, 10, N'DAILY', 0.00, 999.00, 180000.00),
    (4, 11, N'DAILY', 0.00, 999.00, 180000.00),
    (5, 12, N'DAILY', 0.00, 999.00, 180000.00),
    (6, 13, N'DAILY', 0.00, 999.00, 250000.00),
    (7, 14, N'DAILY', 0.00, 999.00, 250000.00),
    (8, 15, N'DAILY', 0.00, 999.00, 250000.00),
    (9, 16, N'DAILY', 0.00, 999.00, 250000.00),
    (10, 17, N'DAILY', 0.00, 999.00, 250000.00);
SET IDENTITY_INSERT dbo.[bang_gia_hotel] OFF;

-- quyen (23 dòng)
INSERT INTO dbo.[quyen] ([ma_quyen], [ten_quyen])
VALUES
    (N'ACCOUNT_MANAGE', N'Quản lý tài khoản và nhân viên'),
    (N'APPROVAL_PROCESS', N'Xử lý phê duyệt'),
    (N'AUDIT_VIEW', N'Xem nhật ký'),
    (N'CASH_SHIFT_MANAGE', N'Mở/kiểm/bàn giao ca'),
    (N'CUSTOMER_MANAGE', N'Quản lý khách hàng'),
    (N'DASHBOARD_VIEW', N'Xem màn hình chính'),
    (N'HOTEL_MANAGE', N'Quản lý Hotel'),
    (N'INVENTORY_COUNT', N'Kiểm kê'),
    (N'INVENTORY_ISSUE', N'Xuất kho'),
    (N'INVENTORY_RECEIVE', N'Nhập kho'),
    (N'PAYMENT_PROCESS', N'Thanh toán'),
    (N'PET_MANAGE', N'Quản lý thú cưng'),
    (N'POS_SELL', N'Lập đơn POS'),
    (N'PRICE_APPROVE', N'Duyệt giá'),
    (N'PRICE_PROPOSE', N'Đề xuất giá'),
    (N'PRODUCT_MANAGE', N'Quản lý sản phẩm'),
    (N'PROMOTION_MANAGE', N'Quản lý khuyến mãi'),
    (N'REPORT_VIEW_ALL', N'Xem báo cáo toàn cửa hàng'),
    (N'REPORT_VIEW_OWN_SHIFT', N'Xem báo cáo ca cá nhân'),
    (N'SCHEDULE_MANAGE', N'Quản lý lịch làm việc'),
    (N'SCHEDULE_VIEW', N'Xem lịch làm việc'),
    (N'SPA_MANAGE', N'Quản lý Spa'),
    (N'SUPPLIER_MANAGE', N'Quản lý nhà cung cấp');

-- san_pham (74 dòng)
DBCC CHECKIDENT ('dbo.san_pham', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[san_pham] ON;
INSERT INTO dbo.[san_pham] ([id], [ma_san_pham], [barcode], [ten], [nhom_hang], [don_vi_tinh], [gia_ban], [gia_nhap_gan_nhat], [ton_toi_thieu], [theo_doi_hsd], [anh], [trang_thai], [thuong_hieu], [quy_cach], [doi_tuong_su_dung], [mo_ta], [nguon_url], [ngay_thu_thap])
VALUES
    (3, N'SP001', NULL, N'Royal Canin Adult 2kg', N'Thức ăn', N'Túi', 285000.00, 240000.00, 10, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (4, N'SP002', NULL, N'Pate Whiskas 400g', N'Thức ăn', N'Lon', 45000.00, 36000.00, 30, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (5, N'SP003', NULL, N'Vòng cổ da size M', N'Phụ kiện', N'Cái', 120000.00, 80000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (6, N'SP004', NULL, N'Sữa tắm Spa Dogs 500ml', N'Grooming', N'Chai', 89000.00, 62000.00, 10, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (7, N'SP005', NULL, N'Lồng vận chuyển size L', N'Phụ kiện', N'Cái', 450000.00, 330000.00, 3, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (8, N'SP006', NULL, N'Đồ chơi bóng cao su', N'Phụ kiện', N'Cái', 35000.00, 20000.00, 10, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (9, N'SP007', NULL, N'Thức ăn hạt Orijen 1.8kg', N'Thức ăn', N'Túi', 520000.00, 430000.00, 5, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (10, N'SP008', NULL, N'Cát vệ sinh mèo 5L', N'Vệ sinh', N'Túi', 75000.00, 55000.00, 15, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (11, N'SP009', NULL, N'Bàn chải lông chó', N'Grooming', N'Cái', 55000.00, 40000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (12, N'SP010', NULL, N'Thuốc nhỏ gáy Frontline', N'Thuốc', N'Hộp', 180000.00, 130000.00, 5, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (13, N'SP011', NULL, N'Snack xương gặm răng', N'Thức ăn', N'Gói', 42000.00, 30000.00, 10, 1, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (14, N'SP012', NULL, N'Áo mưa chó size S', N'Phụ kiện', N'Cái', 95000.00, 65000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (15, N'SP000013', NULL, N'Vòng cổ cỡ S', N'Phụ kiện', N'Cái', 100000.00, 50000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (16, N'SP000014', NULL, N'Vòng cổ cỡ M', N'Phụ kiện', N'Cái', 150000.00, 50000.00, 5, 0, NULL, N'SELLING', NULL, NULL, NULL, NULL, NULL, NULL),
    (17, N'SP-VS-001', NULL, N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 lít; Đóng gói: 1 túi', N'Vệ sinh', N'Túi', 160000.00, 104000.00, 5, 0, N'images/products/SP-VS-001.jpg', N'SELLING', N'IRIS OHYAMA', N'Dung tích: 10 lít; Đóng gói: 1 túi', N'Mèo', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 10 lít; Đóng gói: 1 túi.', N'https://www.petmart.vn/cat-ve-sinh-cho-meo-sieu-von-iris-ohyama-kfag-100', CONVERT(date,'2026-10-19',23)),
    (18, N'SP-TA-001', NULL, N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói', N'Thức ăn', N'Gói', 10000.00, 6500.00, 5, 1, N'images/products/SP-TA-001.jpg', N'SELLING', N'VEGEBRAND', N'Đóng gói: 1 gói', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: Đóng gói: 1 gói.', N'https://www.petmart.vn/xuong-cho-cho-gam-sach-rang-vegebrand-360-bone-prevent-tartar', CONVERT(date,'2026-10-19',23)),
    (19, N'SP-TA-002', NULL, N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800g', N'Thức ăn', N'Gói', 225000.00, 146250.00, 5, 1, N'images/products/SP-TA-002.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 800g', N'Chó', N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 800g.', N'https://www.petmart.vn/thuc-an-cho-cho-con-co-nho-royal-canin-mini-puppy', CONVERT(date,'2026-10-19',23)),
    (20, N'SP-VS-002', NULL, N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150ml', N'Vệ sinh', N'Chai', 150000.00, 97500.00, 5, 0, N'images/products/SP-VS-002.jpg', N'SELLING', N'JOYCE & DOLLS', N'Dung tích: 150ml', N'Chó và mèo', N'Vệ sinh thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 150ml.', N'https://www.petmart.vn/chai-xit-cho-meo-di-ve-sinh-dung-cho-joyce-dolls-trainer-puppy', CONVERT(date,'2026-10-19',23)),
    (21, N'SP-TA-003', NULL, N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-003.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-nguyen-chat-cat-sea-fish-pure-tuna-meat', CONVERT(date,'2026-10-19',23)),
    (22, N'SP-TA-004', NULL, N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400g', N'Thức ăn', N'Gói', 130000.00, 84500.00, 5, 1, N'images/products/SP-TA-004.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-con-royal-canin-kitten', CONVERT(date,'2026-10-19',23)),
    (23, N'SP-TA-005', NULL, N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-005.jpg', N'SELLING', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-ca-ngu-whiskas-tuna-flavour-sauce', CONVERT(date,'2026-10-19',23)),
    (24, N'SP-VS-003', NULL, N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 lít', N'Vệ sinh', N'Gói', 160000.00, 104000.00, 5, 0, N'images/products/SP-VS-003.jpg', N'SELLING', N'TRIXIE', N'Dung tích: 5 lít', N'Mèo', N'Vệ sinh thương hiệu TRIXIE, dành cho mèo. Quy cách: Dung tích: 5 lít.', N'https://www.petmart.vn/cat-ve-sinh-thuy-tinh-cho-meo-trixie-silikatstreu', CONVERT(date,'2026-10-19',23)),
    (25, N'SP-TA-006', NULL, N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400g', N'Thức ăn', N'Gói', 125000.00, 81250.00, 5, 1, N'images/products/SP-TA-006.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-indoor-27', CONVERT(date,'2026-10-19',23)),
    (26, N'SP-TA-007', NULL, N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-007.jpg', N'SELLING', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-thit-bo-whiskas-beef-flavour-sauce', CONVERT(date,'2026-10-19',23)),
    (27, N'SP-VS-004', NULL, N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ', N'Vệ sinh', N'Cái', 5000.00, 3250.00, 5, 0, N'images/products/SP-VS-004.jpg', N'SELLING', N'IRIS OHYAMA', N'Đóng gói: 1 miếng lẻ', N'Chó', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.', N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-88', CONVERT(date,'2026-10-19',23)),
    (28, N'SP-TA-008', NULL, N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-008.jpg', N'SELLING', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/banh-thuong-cho-cho-vi-thit-bo-vegebrand-orgo-freshening-biscuit-bacon-beef', CONVERT(date,'2026-10-19',23)),
    (29, N'SP-TA-009', NULL, N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 35000.00, 22750.00, 5, 1, N'images/products/SP-TA-009.jpg', N'SELLING', N'IRIS OHYAMA', N'Trọng lượng: 100g; Đóng gói: 1 lon', N'Chó', N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-cho-vi-thit-bo-iris-ohyama-one-care-beef', CONVERT(date,'2026-10-19',23)),
    (30, N'SP-TA-010', NULL, N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-010.jpg', N'SELLING', N'PEDIGREE', N'Trọng lượng: 100g', N'Chó', N'Thức ăn thương hiệu PEDIGREE, dành cho chó. Quy cách: Trọng lượng: 100g.', N'https://www.petmart.vn/pate-cho-cho-nuoc-sot-vi-thit-bo-pedigree-pouch-beef', CONVERT(date,'2026-10-19',23)),
    (31, N'SP-TA-011', NULL, N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500g', N'Thức ăn', N'Gói', 185000.00, 120250.00, 5, 1, N'images/products/SP-TA-011.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 500g', N'Chó', N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 500g.', N'https://www.petmart.vn/thuc-an-cho-cho-poodle-con-royal-canin-poodle-puppy', CONVERT(date,'2026-10-19',23)),
    (32, N'SP-TA-012', NULL, N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-012.jpg', N'SELLING', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/nuoc-sot-cho-meo-vi-thit-ga-whiskas-chicken-flavour-in-sauce', CONVERT(date,'2026-10-19',23)),
    (33, N'SP-TA-013', NULL, N'Pate cho mèo mẹ và mèo con CATIDEA Mother & Baby Cat — Trọng lượng: 85g', N'Thức ăn', N'Gói', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-013.jpg', N'SELLING', N'CATIDEA', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu CATIDEA, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-me-va-meo-con-catidea-mother-baby-cat', CONVERT(date,'2026-10-19',23)),
    (34, N'SP-TA-014', NULL, N'Pate cho mèo vị cá ngừ thanh cua CAT SEA FISH Tuna Surimi — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-014.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-thanh-cua-cat-sea-fish-tuna-surimi', CONVERT(date,'2026-10-19',23)),
    (35, N'SP-TA-015', NULL, N'Pate cho mèo CIAO Tuna & Whitebait vị cá ngừ và cá chạch trắng — Trọng lượng: 60g', N'Thức ăn', N'Gói', 20000.00, 13000.00, 5, 1, N'images/products/SP-TA-015.jpg', N'SELLING', N'CIAO', N'Trọng lượng: 60g', N'Mèo', N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.', N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-whitebait-vi-ca-ngu-va-ca-chach-trang', CONVERT(date,'2026-10-19',23)),
    (36, N'SP-VS-005', NULL, N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1kg', N'Vệ sinh', N'Gói', 140000.00, 91000.00, 5, 0, N'images/products/SP-VS-005.jpg', N'SELLING', N'CATS BEST', N'Trọng lượng: 2.1kg', N'Mèo', N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.1kg.', N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-original', CONVERT(date,'2026-10-19',23)),
    (37, N'SP-TA-016', NULL, N'Xương cho chó vị thịt bò VEGEBRAND Orgo Nutrients Beef', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP-TA-016.jpg', N'SELLING', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/xuong-cho-cho-vi-thit-bo-vegebrand-orgo-nutrients-beef', CONVERT(date,'2026-10-19',23)),
    (38, N'SP-TA-017', NULL, N'Thức ăn cho mèo con và mèo mẹ ROYAL CANIN Mother & Babycat — Trọng lượng: 400g', N'Thức ăn', N'Gói', 130000.00, 84500.00, 5, 1, N'images/products/SP-TA-017.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-con-va-meo-me-royal-canin-mother-babycat', CONVERT(date,'2026-10-19',23)),
    (39, N'SP-TA-018', NULL, N'Pate cho mèo vị cá ngừ cá thu CAT SEA FISH Tuna Mackerel — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-018.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-thu-cat-sea-fish-tuna-mackerel', CONVERT(date,'2026-10-19',23)),
    (40, N'SP-TA-019', NULL, N'Thức ăn cho mèo trưởng thành ROYAL CANIN Regular Fit 32 — Trọng lượng: 400g', N'Thức ăn', N'Gói', 115000.00, 74750.00, 5, 1, N'images/products/SP-TA-019.jpg', N'SELLING', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-regular-fit-32', CONVERT(date,'2026-10-19',23)),
    (41, N'SP-TA-020', NULL, N'Thức ăn cho chó Poodle MKB All Life Stages Formula Nutrition — Trọng lượng: 1kg', N'Thức ăn', N'Gói', 315000.00, 204750.00, 5, 1, N'images/products/SP-TA-020.jpg', N'SELLING', N'MKB', N'Trọng lượng: 1kg', N'Chó', N'Thức ăn thương hiệu MKB, dành cho chó. Quy cách: Trọng lượng: 1kg.', N'https://www.petmart.vn/thuc-an-cho-cho-poodle-mkb-all-life-stages-formula-nutrition', CONVERT(date,'2026-10-19',23)),
    (42, N'SP-TA-021', NULL, N'Pate cho chó hỗ trợ chức năng gan và sáng mắt IRIS OHYAMA Benefit For Eyes & Liver — Trọng lượng: 100g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 35000.00, 22750.00, 5, 1, N'images/products/SP-TA-021.jpg', N'SELLING', N'IRIS OHYAMA', N'Trọng lượng: 100g; Đóng gói: 1 lon', N'Chó', N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-cho-ho-tro-chuc-nang-gan-va-sang-mat-iris-ohyama-benefit-for-eyes-liver', CONVERT(date,'2026-10-19',23)),
    (43, N'SP-TA-022', NULL, N'Pate cho mèo CIAO Tuna & Salmon vị cá ngừ và cá hồi — Trọng lượng: 60g', N'Thức ăn', N'Gói', 20000.00, 13000.00, 5, 1, N'images/products/SP-TA-022.jpg', N'SELLING', N'CIAO', N'Trọng lượng: 60g', N'Mèo', N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.', N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-salmon-vi-ca-ngu-va-ca-hoi', CONVERT(date,'2026-10-19',23)),
    (44, N'SP-PK-001', NULL, N'Đồ chơi cho chó mèo dạng bóng thừng BOBO', N'Phụ kiện', N'Cái', 30000.00, 19500.00, 5, 0, N'images/products/SP-PK-001.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-cho-cho-meo-dang-bong-thung-bobo', CONVERT(date,'2026-10-19',23)),
    (45, N'SP-TA-023', NULL, N'Pate cho mèo vị cá ngừ cá cơm CAT SEA FISH Tuna Anchovy — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP-TA-023.jpg', N'SELLING', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-com-cat-sea-fish-tuna-anchovy', CONVERT(date,'2026-10-19',23)),
    (46, N'SP-TA-024', NULL, N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone', N'Thức ăn', N'Gói', 50000.00, 32500.00, 5, 1, N'images/products/SP-TA-024.jpg', N'SELLING', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/xuong-cho-cho-vi-thit-vit-bone-for-poodles', CONVERT(date,'2026-10-19',23)),
    (47, N'SP-GR-001', NULL, N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam', N'Grooming', N'Chai', 180000.00, 117000.00, 5, 1, N'images/products/SP-GR-001.jpg', N'SELLING', N'JOYCE & DOLLS', N'', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo.', N'https://www.petmart.vn/sua-tam-kho-cho-cho-meo-joyce-dolls-waterless-cleansing-foam', CONVERT(date,'2026-10-19',23)),
    (48, N'SP-VS-006', NULL, N'Khay vệ sinh cho mèo MAKAR Cat Litter Station', N'Vệ sinh', N'Cái', 250000.00, 162500.00, 5, 0, N'images/products/SP-VS-006.jpg', N'SELLING', N'MAKAR', N'', N'Mèo', N'Vệ sinh thương hiệu MAKAR, dành cho mèo.', N'https://www.petmart.vn/khay-ve-sinh-cho-meo-makar-cat-litter-station', CONVERT(date,'2026-10-19',23)),
    (49, N'SP-VS-007', NULL, N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-42 — Đóng gói: 1 miếng lẻ', N'Vệ sinh', N'Cái', 10000.00, 6500.00, 5, 0, N'images/products/SP-VS-007.jpg', N'SELLING', N'IRIS OHYAMA', N'Đóng gói: 1 miếng lẻ', N'Chó', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.', N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-42', CONVERT(date,'2026-10-19',23)),
    (50, N'SP-VS-008', NULL, N'Cát vệ sinh cho mèo than hoạt tính IRIS OHYAMA Charcoal SNS-70 — Dung tích: 7 lít; Đóng gói: 1 túi', N'Vệ sinh', N'Túi', 160000.00, 104000.00, 5, 0, N'images/products/SP-VS-008.jpg', N'SELLING', N'IRIS OHYAMA', N'Dung tích: 7 lít; Đóng gói: 1 túi', N'Mèo', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 7 lít; Đóng gói: 1 túi.', N'https://www.petmart.vn/cat-ve-sinh-cho-meo-than-hoat-tinh-iris-ohyama-charcoal-sns-70', CONVERT(date,'2026-10-19',23)),
    (51, N'SP-GR-002', NULL, N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb', N'Grooming', N'Cái', 85000.00, 55250.00, 5, 1, N'images/products/SP-GR-002.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/luoc-chai-long-cho-meo-dau-luoi-mem-bobo-pet-comb', CONVERT(date,'2026-10-19',23)),
    (52, N'SP-VS-009', NULL, N'Cát vệ sinh hữu cơ cho mèo CATS BEST Smart Peppets — Trọng lượng: 2.5kg', N'Vệ sinh', N'Gói', 155000.00, 100750.00, 5, 0, N'images/products/SP-VS-009.jpg', N'SELLING', N'CATS BEST', N'Trọng lượng: 2.5kg', N'Mèo', N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.5kg.', N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-smart-peppets', CONVERT(date,'2026-10-19',23)),
    (53, N'SP-PK-002', NULL, N'Vòng cổ chó mèo kèm dây dắt cỡ mini HAND IN HAND', N'Phụ kiện', N'Cái', 70000.00, 45500.00, 5, 0, N'images/products/SP-PK-002.jpg', N'SELLING', N'HAND IN HAND', N'', N'Chó và mèo', N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-mini-hand-in-hand', CONVERT(date,'2026-10-19',23)),
    (54, N'SP-PK-003', NULL, N'Bóng đồ chơi cho chó mèo có chuông nhựa BOBO', N'Phụ kiện', N'Cái', 45000.00, 29250.00, 5, 0, N'images/products/SP-PK-003.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/bong-do-choi-cho-cho-meo-co-chuong-nhua-bobo', CONVERT(date,'2026-10-19',23)),
    (55, N'SP-PK-004', NULL, N'Đồ chơi dây thừng cho chó BOBO', N'Phụ kiện', N'Cái', 35000.00, 22750.00, 5, 0, N'images/products/SP-PK-004.jpg', N'SELLING', N'BOBO', N'', N'Chó', N'Phụ kiện thương hiệu BOBO, dành cho chó.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-bobo', CONVERT(date,'2026-10-19',23)),
    (56, N'SP-PK-005', NULL, N'Vòng cổ chống ve chó BIOLINE Flea and Tick Collar For Dogs', N'Phụ kiện', N'Cái', 60000.00, 39000.00, 5, 0, N'images/products/SP-PK-005.jpg', N'SELLING', N'BIOLINE', N'', N'Chó', N'Phụ kiện thương hiệu BIOLINE, dành cho chó.', N'https://www.petmart.vn/vong-co-chong-ve-cho-bioline-flea-and-tick-collar-for-dogs', CONVERT(date,'2026-10-19',23)),
    (57, N'SP-VS-010', NULL, N'Khay vệ sinh cho chó MAKAR Dog Toilet Trays Small — Kích thước: S', N'Vệ sinh', N'Cái', 220000.00, 143000.00, 5, 0, N'images/products/SP-VS-010.jpg', N'SELLING', N'MAKAR', N'Kích thước: S', N'Chó', N'Vệ sinh thương hiệu MAKAR, dành cho chó. Quy cách: Kích thước: S.', N'https://www.petmart.vn/khay-ve-sinh-cho-cho-makar-dog-toilet-trays-small', CONVERT(date,'2026-10-19',23)),
    (58, N'SP-GR-003', NULL, N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush', N'Grooming', N'Cái', 30000.00, 19500.00, 5, 1, N'images/products/SP-GR-003.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/ban-chai-tam-cho-cho-meo-bobo-pet-washing-brush', CONVERT(date,'2026-10-19',23)),
    (59, N'SP-PK-006', NULL, N'Đồ chơi cần câu cho mèo PAW vui nhộn', N'Phụ kiện', N'Cái', 75000.00, 48750.00, 5, 0, N'images/products/SP-PK-006.jpg', N'SELLING', N'PAW', N'', N'Mèo', N'Phụ kiện thương hiệu PAW, dành cho mèo.', N'https://www.petmart.vn/do-choi-can-cau-cho-meo-paw-vui-nhon', CONVERT(date,'2026-10-19',23)),
    (60, N'SP-GR-004', NULL, N'Sữa tắm cho chó mèo hương hoa hồng JOYCE & DOLLS Rose Dew — Dung tích: 400ml', N'Grooming', N'Chai', 250000.00, 162500.00, 5, 1, N'images/products/SP-GR-004.jpg', N'SELLING', N'JOYCE & DOLLS', N'Dung tích: 400ml', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.', N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-hong-joyce-dolls-rose-dew', CONVERT(date,'2026-10-19',23)),
    (61, N'SP-PK-007', NULL, N'Đồ chơi cho chó mèo Paw hình con gà', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP-PK-007.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-cho-cho-meo-paw-hinh-con-ga', CONVERT(date,'2026-10-19',23)),
    (62, N'SP-PK-008', NULL, N'Bình uống nước cho chó mèo gài chuồng BOBO', N'Phụ kiện', N'Cái', 80000.00, 52000.00, 5, 0, N'images/products/SP-PK-008.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/binh-uong-nuoc-cho-cho-meo-gai-chuong-bobo', CONVERT(date,'2026-10-19',23)),
    (63, N'SP-PK-009', NULL, N'Vòng cổ chống liếm cho chó mèo PAW Pet Elizabethan Collar', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP-PK-009.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-chong-liem-cho-cho-meo-paw-pet-elizabethan-collar', CONVERT(date,'2026-10-19',23)),
    (64, N'SP-GR-005', NULL, N'Sữa tắm cho chó diệt khuẩn BBN Bactericidal Shampoo Lemon', N'Grooming', N'Chai', 150000.00, 97500.00, 5, 1, N'images/products/SP-GR-005.jpg', N'SELLING', N'BBN', N'', N'Chó', N'Grooming thương hiệu BBN, dành cho chó.', N'https://www.petmart.vn/sua-tam-cho-cho-diet-khuan-bbn-bactericidal-shampoo-lemon', CONVERT(date,'2026-10-19',23)),
    (65, N'SP-GR-006', NULL, N'Găng tay tắm cho chó mèo BOBO Pet Grooming Brush Gloves', N'Grooming', N'Cái', 40000.00, 26000.00, 5, 1, N'images/products/SP-GR-006.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/gang-tay-tam-cho-cho-meo-bobo-pet-grooming-brush-gloves', CONVERT(date,'2026-10-19',23)),
    (66, N'SP-GR-007', NULL, N'Kềm cắt bấm móng chó mèo PAW Safety Guard Clipper', N'Grooming', N'Cái', 70000.00, 45500.00, 5, 1, N'images/products/SP-GR-007.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Grooming thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/kem-cat-bam-mong-cho-meo-paw-safety-guard-clipper', CONVERT(date,'2026-10-19',23)),
    (67, N'SP-GR-008', NULL, N'Sữa tắm cho chó lông trắng BBN Whitening Shampoo', N'Grooming', N'Chai', 150000.00, 97500.00, 5, 1, N'images/products/SP-GR-008.jpg', N'SELLING', N'BBN', N'', N'Chó', N'Grooming thương hiệu BBN, dành cho chó.', N'https://www.petmart.vn/sua-tam-cho-cho-long-trang-bbn-whitening-shampoo', CONVERT(date,'2026-10-19',23)),
    (68, N'SP-PK-010', NULL, N'Đồ chơi dây thừng cho chó mèo đan chéo BOBO', N'Phụ kiện', N'Cái', 55000.00, 35750.00, 5, 0, N'images/products/SP-PK-010.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-dan-cheo-bobo', CONVERT(date,'2026-10-19',23)),
    (69, N'SP-PK-011', NULL, N'Bát ăn cho chó mèo nhựa mềm SAWYER PET', N'Phụ kiện', N'Cái', 40000.00, 26000.00, 5, 0, N'images/products/SP-PK-011.jpg', N'SELLING', N'SAWYER PET', N'', N'Chó và mèo', N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-cho-cho-meo-nhua-mem-sawyer-pet', CONVERT(date,'2026-10-19',23)),
    (70, N'SP-GR-009', NULL, N'Sữa tắm cho chó mèo hương hoa nhài JOYCE & DOLLS Moder Jasmine — Dung tích: 400ml', N'Grooming', N'Chai', 250000.00, 162500.00, 5, 1, N'images/products/SP-GR-009.jpg', N'SELLING', N'JOYCE & DOLLS', N'Dung tích: 400ml', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.', N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-nhai-joyce-dolls-moder-jasmine', CONVERT(date,'2026-10-19',23)),
    (71, N'SP-GR-010', NULL, N'Lược chải lông chó mèo nút bấm KUDI Pet Grooming Slicker Brushes', N'Grooming', N'Cái', 170000.00, 110500.00, 5, 1, N'images/products/SP-GR-010.jpg', N'SELLING', N'KUDI', N'', N'Chó và mèo', N'Grooming thương hiệu KUDI, dành cho chó và mèo.', N'https://www.petmart.vn/luoc-chai-long-cho-meo-nut-bam-kudi-pet-grooming-slicker-brushes', CONVERT(date,'2026-10-19',23)),
    (72, N'SP-PK-012', NULL, N'Thảm mát giải nhiệt cho chó mèo PAW Pet Cool Mat', N'Phụ kiện', N'Cái', 195000.00, 126750.00, 5, 0, N'images/products/SP-PK-012.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/tham-mat-giai-nhiet-cho-cho-meo-paw-pet-cool-mat', CONVERT(date,'2026-10-19',23)),
    (73, N'SP-PK-013', NULL, N'Vòng cổ chó mèo kèm dây dắt cỡ bé HAND IN HAND', N'Phụ kiện', N'Cái', 80000.00, 52000.00, 5, 0, N'images/products/SP-PK-013.jpg', N'SELLING', N'HAND IN HAND', N'', N'Chó và mèo', N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-be-hand-in-hand', CONVERT(date,'2026-10-19',23)),
    (74, N'SP-PK-014', NULL, N'Bát ăn đôi cho chó mèo bằng nhựa SAWYER PET', N'Phụ kiện', N'Cái', 60000.00, 39000.00, 5, 0, N'images/products/SP-PK-014.jpg', N'SELLING', N'SAWYER PET', N'', N'Chó và mèo', N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-doi-cho-cho-meo-bang-nhua-sawyer-pet', CONVERT(date,'2026-10-19',23)),
    (75, N'SP-PK-015', NULL, N'Đồ chơi dây thừng cho chó mèo PAW loại dài', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP-PK-015.jpg', N'SELLING', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-paw-loai-dai', CONVERT(date,'2026-10-19',23)),
    (76, N'SP-PK-016', NULL, N'Bát ăn cho chó mèo bằng nhựa BOBO Plastic Bowl 3038', N'Phụ kiện', N'Cái', 35000.00, 22750.00, 5, 0, N'images/products/SP-PK-016.jpg', N'SELLING', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-cho-cho-meo-bang-nhua-bobo-plastic-bowl-3038', CONVERT(date,'2026-10-19',23));
SET IDENTITY_INSERT dbo.[san_pham] OFF;

-- lo_hang (75 dòng)
DBCC CHECKIDENT ('dbo.lo_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lo_hang] ON;
INSERT INTO dbo.[lo_hang] ([id], [san_pham_id], [so_lo], [han_su_dung], [so_luong_ton], [trang_thai])
VALUES
    (2, 3, N'SEED-001', DATEADD(day,173,@homNay), 47, N'AVAILABLE'),
    (3, 4, N'SEED-001', DATEADD(day,173,@homNay), 82, N'AVAILABLE'),
    (4, 5, N'SEED-001', DATEADD(day,173,@homNay), 3, N'AVAILABLE'),
    (5, 6, N'SEED-001', DATEADD(day,173,@homNay), 28, N'AVAILABLE'),
    (6, 7, N'SEED-001', DATEADD(day,173,@homNay), 7, N'AVAILABLE'),
    (7, 8, N'SEED-001', DATEADD(day,173,@homNay), 15, N'AVAILABLE'),
    (8, 9, N'SEED-001', DATEADD(day,173,@homNay), 2, N'AVAILABLE'),
    (9, 10, N'SEED-001', DATEADD(day,173,@homNay), 31, N'AVAILABLE'),
    (10, 11, N'SEED-001', DATEADD(day,173,@homNay), 12, N'AVAILABLE'),
    (11, 12, N'SEED-001', DATEADD(day,173,@homNay), 0, N'AVAILABLE'),
    (12, 13, N'SEED-001', DATEADD(day,173,@homNay), 40, N'AVAILABLE'),
    (13, 14, N'SEED-001', DATEADD(day,173,@homNay), 9, N'AVAILABLE'),
    (14, 3, N'SAMPLE-RECEIPT', DATEADD(day,173,@homNay), 2, N'AVAILABLE'),
    (15, 15, N'LO000001', NULL, 20, N'AVAILABLE'),
    (16, 16, N'LO000002', NULL, 20, N'AVAILABLE'),
    (17, 17, N'LO000003', NULL, 9, N'AVAILABLE'),
    (18, 18, N'LO000004', DATEADD(day,173,@homNay), 9, N'AVAILABLE'),
    (19, 19, N'LO000005', DATEADD(day,173,@homNay), 12, N'AVAILABLE'),
    (20, 20, N'LO000006', NULL, 12, N'AVAILABLE'),
    (21, 21, N'LO000007', DATEADD(day,173,@homNay), 12, N'AVAILABLE'),
    (22, 22, N'LO000008', DATEADD(day,173,@homNay), 15, N'AVAILABLE'),
    (23, 23, N'LO000009', DATEADD(day,173,@homNay), 15, N'AVAILABLE'),
    (24, 24, N'LO000010', NULL, 15, N'AVAILABLE'),
    (25, 25, N'LO000011', DATEADD(day,8,@homNay), 18, N'AVAILABLE'),
    (26, 26, N'LO000012', DATEADD(day,173,@homNay), 18, N'AVAILABLE'),
    (27, 27, N'LO000013', NULL, 18, N'AVAILABLE'),
    (28, 28, N'LO000014', DATEADD(day,173,@homNay), 21, N'AVAILABLE'),
    (29, 29, N'LO000015', DATEADD(day,173,@homNay), 21, N'AVAILABLE'),
    (30, 30, N'LO000016', DATEADD(day,173,@homNay), 21, N'AVAILABLE'),
    (31, 31, N'LO000017', DATEADD(day,173,@homNay), 24, N'AVAILABLE'),
    (32, 32, N'LO000018', DATEADD(day,173,@homNay), 24, N'AVAILABLE'),
    (33, 33, N'LO000019', DATEADD(day,173,@homNay), 27, N'AVAILABLE'),
    (34, 34, N'LO000020', DATEADD(day,8,@homNay), 28, N'AVAILABLE'),
    (35, 35, N'LO000021', DATEADD(day,173,@homNay), 29, N'AVAILABLE'),
    (36, 36, N'LO000022', NULL, 29, N'AVAILABLE'),
    (37, 37, N'LO000023', DATEADD(day,173,@homNay), 10, N'AVAILABLE'),
    (38, 38, N'LO000024', DATEADD(day,173,@homNay), 11, N'AVAILABLE'),
    (39, 39, N'LO000025', DATEADD(day,173,@homNay), 12, N'AVAILABLE'),
    (40, 40, N'LO000026', DATEADD(day,173,@homNay), 13, N'AVAILABLE'),
    (41, 41, N'LO000027', DATEADD(day,173,@homNay), 14, N'AVAILABLE'),
    (42, 42, N'LO000028', DATEADD(day,173,@homNay), 15, N'AVAILABLE'),
    (43, 43, N'LO000029', DATEADD(day,8,@homNay), 16, N'AVAILABLE'),
    (44, 44, N'LO000030', NULL, 15, N'AVAILABLE'),
    (45, 45, N'LO000031', DATEADD(day,173,@homNay), 18, N'AVAILABLE'),
    (46, 46, N'LO000032', DATEADD(day,173,@homNay), 17, N'AVAILABLE'),
    (47, 47, N'LO000033', DATEADD(day,173,@homNay), 20, N'AVAILABLE'),
    (48, 48, N'LO000034', NULL, 21, N'AVAILABLE'),
    (49, 49, N'LO000035', NULL, 22, N'AVAILABLE'),
    (50, 50, N'LO000036', NULL, 23, N'AVAILABLE'),
    (51, 51, N'LO000037', DATEADD(day,173,@homNay), 22, N'AVAILABLE'),
    (52, 52, N'LO000038', NULL, 25, N'AVAILABLE'),
    (53, 53, N'LO000039', NULL, 26, N'AVAILABLE'),
    (54, 54, N'LO000040', NULL, 27, N'AVAILABLE'),
    (55, 55, N'LO000041', NULL, 26, N'AVAILABLE'),
    (56, 56, N'LO000042', NULL, 29, N'AVAILABLE'),
    (57, 57, N'LO000043', NULL, 3, N'AVAILABLE'),
    (58, 58, N'LO000044', DATEADD(day,173,@homNay), 9, N'AVAILABLE'),
    (59, 59, N'LO000045', NULL, 11, N'AVAILABLE'),
    (60, 60, N'LO000046', DATEADD(day,173,@homNay), 12, N'AVAILABLE'),
    (61, 61, N'LO000047', NULL, 13, N'AVAILABLE'),
    (62, 62, N'LO000048', NULL, 14, N'AVAILABLE'),
    (63, 63, N'LO000049', NULL, 15, N'AVAILABLE'),
    (64, 64, N'LO000050', DATEADD(day,173,@homNay), 16, N'AVAILABLE'),
    (65, 65, N'LO000051', DATEADD(day,173,@homNay), 17, N'AVAILABLE'),
    (66, 66, N'LO000052', DATEADD(day,173,@homNay), 18, N'AVAILABLE'),
    (67, 67, N'LO000053', DATEADD(day,173,@homNay), 19, N'AVAILABLE'),
    (68, 68, N'LO000054', NULL, 20, N'AVAILABLE'),
    (69, 69, N'LO000055', NULL, 21, N'AVAILABLE'),
    (70, 70, N'LO000056', DATEADD(day,8,@homNay), 22, N'AVAILABLE'),
    (71, 71, N'LO000057', DATEADD(day,173,@homNay), 23, N'AVAILABLE'),
    (72, 72, N'LO000058', NULL, 24, N'AVAILABLE'),
    (73, 73, N'LO000059', NULL, 25, N'AVAILABLE'),
    (74, 74, N'LO000060', NULL, 26, N'AVAILABLE'),
    (75, 75, N'LO000061', NULL, 27, N'AVAILABLE'),
    (76, 76, N'LO000062', NULL, 28, N'AVAILABLE');
SET IDENTITY_INSERT dbo.[lo_hang] OFF;

-- tai_khoan (5 dòng)
DBCC CHECKIDENT ('dbo.tai_khoan', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[tai_khoan] ON;
INSERT INTO dbo.[tai_khoan] ([id], [nhan_vien_id], [ten_dang_nhap], [mat_khau_hash], [vai_tro], [trang_thai], [so_lan_dang_nhap_sai], [khoa_den], [bat_buoc_doi_mat_khau], [dang_nhap_cuoi])
VALUES
    (1, 1, N'admin', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'STORE_MANAGER', N'ACTIVE', 0, NULL, 1, NULL),
    (2, 14, N'demo.admin', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'STORE_MANAGER', N'ACTIVE', 0, NULL, 1, NULL),
    (3, 15, N'demo.sales', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'SALES_STAFF', N'ACTIVE', 0, NULL, 1, NULL),
    (4, 16, N'demo.stock', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'WAREHOUSE_STAFF', N'ACTIVE', 0, NULL, 1, NULL),
    (5, 17, N'demo.spa', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'SPA_HOTEL_STAFF', N'ACTIVE', 0, NULL, 1, NULL);
SET IDENTITY_INSERT dbo.[tai_khoan] OFF;

-- bien_ban_kiem_ke (1 dòng)
DBCC CHECKIDENT ('dbo.bien_ban_kiem_ke', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[bien_ban_kiem_ke] ON;
INSERT INTO dbo.[bien_ban_kiem_ke] ([id], [so_bien_ban], [nguoi_lap_id], [ky_kiem_ke], [trang_thai], [tao_luc])
VALUES
    (1, N'KK000001', 4, N'2026-10-19', N'PENDING', DATEADD(second,-549878,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[bien_ban_kiem_ke] OFF;

-- cai_dat (29 dòng)
INSERT INTO dbo.[cai_dat] ([khoa], [gia_tri], [nguoi_cap_nhat_id], [ngay_cap_nhat])
VALUES
    (N'ALERT_LOW_STOCK', N'true', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'ALERT_NEW_ORDER', N'true', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'ALERT_SPA', N'true', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'BANK_ACCOUNT', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'BANK_ACCOUNT_NAME', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'BANK_BIN', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'CURRENCY', N'VND', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'DAILY_REPORT', N'false', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'EXPIRY_WARN_DAYS', N'30', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'LANGUAGE', N'Tiếng Việt', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'LATE_CHECKOUT_FEE', N'50000', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'LOW_STOCK_THRESHOLD', N'10', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'PAPER_SIZE', N'80mm', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'PRINTER', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'REPORT_TIME', N'21:00', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'RETURN_WINDOW_DAYS', N'3', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_ADDRESS', N'123 Phan Văn Trị, Gò Vấp, TP.HCM', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_EMAIL', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_NAME', N'Pet Station', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_PHONE', N'0243825678', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_TAX_CODE', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_WEBSITE', N'', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TIER_SPEND', N'2000000,10000000,25000000,50000000', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'TIME_ZONE', N'Asia/Ho_Chi_Minh', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'VAT_PERCENT', N'8', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'VND_PER_EARNED_POINT', N'10000', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'VND_PER_REDEEMED_POINT', N'1000', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'WRITEOFF_APPROVAL_LIMIT', N'500000', 1, DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'APP_SCHEMA_VERSION', N'20261026', NULL, DATEADD(second,-555362,CAST(@homNay AS datetime2)));

-- chi_tiet_kiem_ke (1 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_kiem_ke', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_kiem_ke] ON;
INSERT INTO dbo.[chi_tiet_kiem_ke] ([id], [bien_ban_id], [lo_hang_id], [ton_he_thong], [ton_thuc_te], [nguyen_nhan])
VALUES
    (1, 1, 57, 3, 2, N'lệch kiểm kê , chưa áp dụng');
SET IDENTITY_INSERT dbo.[chi_tiet_kiem_ke] OFF;

-- chot_ngay (0 dòng)
DBCC CHECKIDENT ('dbo.chot_ngay', RESEED, 1) WITH NO_INFOMSGS;

-- de_xuat_gia (2 dòng)
DBCC CHECKIDENT ('dbo.de_xuat_gia', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[de_xuat_gia] ON;
INSERT INTO dbo.[de_xuat_gia] ([id], [san_pham_id], [nguoi_de_xuat_id], [gia_hien_tai], [gia_de_xuat], [ly_do], [trang_thai])
VALUES
    (1, 44, 4, 30000.00, 35000.00, N'đề xuất giá chưa áp dụng', N'PENDING'),
    (2, 53, 4, 70000.00, 75000.00, N'đề xuất giá chưa áp dụng', N'PENDING');
SET IDENTITY_INSERT dbo.[de_xuat_gia] OFF;

-- gio_hang_treo (3 dòng)
DBCC CHECKIDENT ('dbo.gio_hang_treo', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[gio_hang_treo] ON;
INSERT INTO dbo.[gio_hang_treo] ([id], [ma_gio], [tai_khoan_id], [khach_hang_id], [tao_luc])
VALUES
    (1, N'TREO000001', 3, 16, DATEADD(second,-524678,CAST(@homNay AS datetime2))),
    (2, N'TREO000002', 3, 17, DATEADD(second,-524678,CAST(@homNay AS datetime2))),
    (4, N'GIO-d9fa92eb1120433aac1f', 1, NULL, DATEADD(second,-516600,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[gio_hang_treo] OFF;

-- chi_tiet_gio_hang_treo (5 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_gio_hang_treo', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_gio_hang_treo] ON;
INSERT INTO dbo.[chi_tiet_gio_hang_treo] ([id], [gio_id], [ma_san_pham], [ten], [don_gia], [so_luong])
VALUES
    (1, 1, N'SP-VS-001', N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 (lít); Đóng gói: 1 túi', 160000.00, 1),
    (2, 2, N'SP-TA-001', N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói', 10000.00, 1),
    (6, 4, N'SP-GR-001', N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam', 180000.00, 1),
    (7, 4, N'SP-GR-002', N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb', 85000.00, 1),
    (8, 4, N'SP-GR-003', N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush', 30000.00, 3);
SET IDENTITY_INSERT dbo.[chi_tiet_gio_hang_treo] OFF;

-- lich_su_gia (60 dòng)
DBCC CHECKIDENT ('dbo.lich_su_gia', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lich_su_gia] ON;
INSERT INTO dbo.[lich_su_gia] ([id], [san_pham_id], [gia_cu], [gia_moi], [nguoi_thay_doi_id], [thoi_diem_thay_doi])
VALUES
    (1, 17, 0.00, 160000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (2, 18, 0.00, 10000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (3, 19, 0.00, 225000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (4, 20, 0.00, 150000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (5, 21, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (6, 22, 0.00, 130000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (7, 23, 0.00, 25000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (8, 24, 0.00, 160000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (9, 25, 0.00, 125000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (10, 26, 0.00, 25000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (11, 27, 0.00, 5000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (12, 28, 0.00, 25000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (13, 29, 0.00, 35000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (14, 30, 0.00, 25000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (15, 31, 0.00, 185000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (16, 32, 0.00, 25000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (17, 33, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (18, 34, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (19, 35, 0.00, 20000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (20, 36, 0.00, 140000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (21, 37, 0.00, 25000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (22, 38, 0.00, 130000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (23, 39, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (24, 40, 0.00, 115000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (25, 41, 0.00, 315000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (26, 42, 0.00, 35000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (27, 43, 0.00, 20000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (28, 44, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (29, 45, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (30, 46, 0.00, 50000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (31, 47, 0.00, 180000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (32, 48, 0.00, 250000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (33, 49, 0.00, 10000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (34, 50, 0.00, 160000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (35, 51, 0.00, 85000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (36, 52, 0.00, 155000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (37, 53, 0.00, 70000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (38, 54, 0.00, 45000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (39, 55, 0.00, 35000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (40, 56, 0.00, 60000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (41, 57, 0.00, 220000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (42, 58, 0.00, 30000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (43, 59, 0.00, 75000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (44, 60, 0.00, 250000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (45, 61, 0.00, 50000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (46, 62, 0.00, 80000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (47, 63, 0.00, 50000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (48, 64, 0.00, 150000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (49, 65, 0.00, 40000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (50, 66, 0.00, 70000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (51, 67, 0.00, 150000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (52, 68, 0.00, 55000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (53, 69, 0.00, 40000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (54, 70, 0.00, 250000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (55, 71, 0.00, 170000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (56, 72, 0.00, 195000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (57, 73, 0.00, 80000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (58, 74, 0.00, 60000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (59, 75, 0.00, 50000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2))),
    (60, 76, 0.00, 35000.00, 2, DATEADD(second,-3085200,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[lich_su_gia] OFF;

-- nhac_viec_da_xem (0 dòng)

-- nhat_ky_he_thong (0 dòng)
DBCC CHECKIDENT ('dbo.nhat_ky_he_thong', RESEED, 1) WITH NO_INFOMSGS;

-- phien_lam_viec (33 dòng)
DBCC CHECKIDENT ('dbo.phien_lam_viec', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phien_lam_viec] ON;
INSERT INTO dbo.[phien_lam_viec] ([id], [lich_lam_viec_id], [tai_khoan_id], [mo_luc], [dong_luc], [tien_dau_ca], [tien_thuc_te], [chenh_lech], [giai_trinh], [trang_thai], [ten_ca], [nguoi_nhan_ban_giao_id], [hinh_thuc_ban_giao])
VALUES
    (1, NULL, 1, DATEADD(second,-554336,CAST(@homNay AS datetime2)), DATEADD(second,-517635,CAST(@homNay AS datetime2)), 22000000.00, 22180000.00, 0.00, N'', N'HANDED_OVER', N'Ca chiều', 2, N'BAN_GIAO'),
    (2, NULL, 2, DATEADD(second,-560772,CAST(@homNay AS datetime2)), DATEADD(second,-553572,CAST(@homNay AS datetime2)), 1000000.00, 1285000.00, 0.00, NULL, N'HANDED_OVER', N'Ca dữ liệu mẫu', NULL, NULL),
    (3, NULL, 3, DATEADD(second,-3081600,CAST(@homNay AS datetime2)), DATEADD(second,-3038400,CAST(@homNay AS datetime2)), 500000.00, 820000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (4, NULL, 3, DATEADD(second,-2995200,CAST(@homNay AS datetime2)), DATEADD(second,-2952000,CAST(@homNay AS datetime2)), 500000.00, 530000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (5, NULL, 3, DATEADD(second,-2908800,CAST(@homNay AS datetime2)), DATEADD(second,-2865600,CAST(@homNay AS datetime2)), 500000.00, 725000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (6, NULL, 3, DATEADD(second,-2822400,CAST(@homNay AS datetime2)), DATEADD(second,-2779200,CAST(@homNay AS datetime2)), 500000.00, 800000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (7, NULL, 3, DATEADD(second,-2736000,CAST(@homNay AS datetime2)), DATEADD(second,-2692800,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (8, NULL, 3, DATEADD(second,-2649600,CAST(@homNay AS datetime2)), DATEADD(second,-2606400,CAST(@homNay AS datetime2)), 500000.00, 630000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (9, NULL, 3, DATEADD(second,-2563200,CAST(@homNay AS datetime2)), DATEADD(second,-2520000,CAST(@homNay AS datetime2)), 500000.00, 550000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (10, NULL, 3, DATEADD(second,-2476800,CAST(@homNay AS datetime2)), DATEADD(second,-2433600,CAST(@homNay AS datetime2)), 500000.00, 980000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (11, NULL, 3, DATEADD(second,-2390400,CAST(@homNay AS datetime2)), DATEADD(second,-2347200,CAST(@homNay AS datetime2)), 500000.00, 625000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (12, NULL, 3, DATEADD(second,-2304000,CAST(@homNay AS datetime2)), DATEADD(second,-2260800,CAST(@homNay AS datetime2)), 500000.00, 550000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (13, NULL, 3, DATEADD(second,-2217600,CAST(@homNay AS datetime2)), DATEADD(second,-2174400,CAST(@homNay AS datetime2)), 500000.00, 515000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (14, NULL, 3, DATEADD(second,-2131200,CAST(@homNay AS datetime2)), DATEADD(second,-2088000,CAST(@homNay AS datetime2)), 500000.00, 525000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (15, NULL, 3, DATEADD(second,-2044800,CAST(@homNay AS datetime2)), DATEADD(second,-2001600,CAST(@homNay AS datetime2)), 500000.00, 570000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (16, NULL, 3, DATEADD(second,-1958400,CAST(@homNay AS datetime2)), DATEADD(second,-1915200,CAST(@homNay AS datetime2)), 500000.00, 575000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (17, NULL, 3, DATEADD(second,-1872000,CAST(@homNay AS datetime2)), DATEADD(second,-1828800,CAST(@homNay AS datetime2)), 500000.00, 685000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (18, NULL, 3, DATEADD(second,-1785600,CAST(@homNay AS datetime2)), DATEADD(second,-1742400,CAST(@homNay AS datetime2)), 500000.00, 550000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (19, NULL, 3, DATEADD(second,-1699200,CAST(@homNay AS datetime2)), DATEADD(second,-1656000,CAST(@homNay AS datetime2)), 500000.00, 500000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (20, NULL, 3, DATEADD(second,-1612800,CAST(@homNay AS datetime2)), DATEADD(second,-1569600,CAST(@homNay AS datetime2)), 500000.00, 500000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (21, NULL, 3, DATEADD(second,-1526400,CAST(@homNay AS datetime2)), DATEADD(second,-1483200,CAST(@homNay AS datetime2)), 500000.00, 625000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (22, NULL, 3, DATEADD(second,-1440000,CAST(@homNay AS datetime2)), DATEADD(second,-1396800,CAST(@homNay AS datetime2)), 500000.00, 740000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (23, NULL, 3, DATEADD(second,-1353600,CAST(@homNay AS datetime2)), DATEADD(second,-1310400,CAST(@homNay AS datetime2)), 500000.00, 765000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (24, NULL, 3, DATEADD(second,-1267200,CAST(@homNay AS datetime2)), DATEADD(second,-1224000,CAST(@homNay AS datetime2)), 500000.00, 790000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (25, NULL, 3, DATEADD(second,-1180800,CAST(@homNay AS datetime2)), DATEADD(second,-1137600,CAST(@homNay AS datetime2)), 500000.00, 815000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (26, NULL, 3, DATEADD(second,-1094400,CAST(@homNay AS datetime2)), DATEADD(second,-1051200,CAST(@homNay AS datetime2)), 500000.00, 840000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (27, NULL, 3, DATEADD(second,-1008000,CAST(@homNay AS datetime2)), DATEADD(second,-964800,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (28, NULL, 3, DATEADD(second,-921600,CAST(@homNay AS datetime2)), DATEADD(second,-878400,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (29, NULL, 3, DATEADD(second,-835200,CAST(@homNay AS datetime2)), DATEADD(second,-792000,CAST(@homNay AS datetime2)), 500000.00, 590000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (30, NULL, 3, DATEADD(second,-748800,CAST(@homNay AS datetime2)), DATEADD(second,-705600,CAST(@homNay AS datetime2)), 500000.00, 620000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (31, NULL, 3, DATEADD(second,-662400,CAST(@homNay AS datetime2)), DATEADD(second,-619200,CAST(@homNay AS datetime2)), 500000.00, 1155000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (32, NULL, 3, DATEADD(second,-576000,CAST(@homNay AS datetime2)), DATEADD(second,-532800,CAST(@homNay AS datetime2)), 500000.00, 1250000.00, 0.00, N'Đã kiểm đếm và đối chiếu tiền mặt', N'HANDED_OVER', N'Ca ngày', NULL, NULL),
    (33, NULL, 1, DATEADD(second,-517618,CAST(@homNay AS datetime2)), NULL, 22180000.00, NULL, NULL, NULL, N'OPEN', N'Ca sáng', NULL, NULL);
SET IDENTITY_INSERT dbo.[phien_lam_viec] OFF;

-- don_hang (34 dòng)
DBCC CHECKIDENT ('dbo.don_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[don_hang] ON;
INSERT INTO dbo.[don_hang] ([id], [ma_don], [phien_lam_viec_id], [khach_hang_id], [trang_thai], [tao_luc])
VALUES
    (1, N'DH-SAMPLE-001', 2, 7, N'PAID', DATEADD(second,-553572,CAST(@homNay AS datetime2))),
    (2, N'DH000001', 3, 16, N'PAID', DATEADD(second,-3070800,CAST(@homNay AS datetime2))),
    (3, N'DH000002', 4, 17, N'PAID', DATEADD(second,-2980800,CAST(@homNay AS datetime2))),
    (4, N'DH000003', 5, 18, N'PAID', DATEADD(second,-2890800,CAST(@homNay AS datetime2))),
    (5, N'DH000004', 6, 19, N'PAID', DATEADD(second,-2800800,CAST(@homNay AS datetime2))),
    (6, N'DH000005', 7, 20, N'PAID', DATEADD(second,-2710800,CAST(@homNay AS datetime2))),
    (7, N'DH000006', 8, 21, N'PAID', DATEADD(second,-2620800,CAST(@homNay AS datetime2))),
    (8, N'DH000007', 9, 22, N'PAID', DATEADD(second,-2530800,CAST(@homNay AS datetime2))),
    (9, N'DH000008', 10, 23, N'PAID', DATEADD(second,-2469600,CAST(@homNay AS datetime2))),
    (10, N'DH000009', 11, 24, N'PAID', DATEADD(second,-2379600,CAST(@homNay AS datetime2))),
    (11, N'DH000010', 12, 25, N'PAID', DATEADD(second,-2289600,CAST(@homNay AS datetime2))),
    (12, N'DH000011', 13, 26, N'PAID', DATEADD(second,-2199600,CAST(@homNay AS datetime2))),
    (13, N'DH000012', 14, 27, N'PAID', DATEADD(second,-2109600,CAST(@homNay AS datetime2))),
    (14, N'DH000013', 15, 28, N'PAID', DATEADD(second,-2019600,CAST(@homNay AS datetime2))),
    (15, N'DH000014', 16, 29, N'PAID', DATEADD(second,-1929600,CAST(@homNay AS datetime2))),
    (16, N'DH000015', 17, 30, N'PAID', DATEADD(second,-1839600,CAST(@homNay AS datetime2))),
    (17, N'DH000016', 18, 31, N'PAID', DATEADD(second,-1778400,CAST(@homNay AS datetime2))),
    (18, N'DH000017', 30, 32, N'PAID', DATEADD(second,-738000,CAST(@homNay AS datetime2))),
    (19, N'DH000018', 30, 33, N'PAID', DATEADD(second,-734400,CAST(@homNay AS datetime2))),
    (20, N'DH000019', 21, 34, N'PAID', DATEADD(second,-1508400,CAST(@homNay AS datetime2))),
    (21, N'DH000020', 22, 35, N'PAID', DATEADD(second,-1418400,CAST(@homNay AS datetime2))),
    (22, N'DH000021', 23, 16, N'PAID', DATEADD(second,-1328400,CAST(@homNay AS datetime2))),
    (23, N'DH000022', 24, 17, N'PAID', DATEADD(second,-1238400,CAST(@homNay AS datetime2))),
    (24, N'DH000023', 25, 18, N'PAID', DATEADD(second,-1148400,CAST(@homNay AS datetime2))),
    (25, N'DH000024', 26, 19, N'PAID', DATEADD(second,-1087200,CAST(@homNay AS datetime2))),
    (26, N'DH000025', 23, 20, N'PAID', DATEADD(second,-1342800,CAST(@homNay AS datetime2))),
    (27, N'DH000026', 25, 21, N'PAID', DATEADD(second,-1166400,CAST(@homNay AS datetime2))),
    (28, N'DH000027', 27, 22, N'PAID', DATEADD(second,-990000,CAST(@homNay AS datetime2))),
    (29, N'DH000028', 29, 23, N'PAID', DATEADD(second,-813600,CAST(@homNay AS datetime2))),
    (30, N'DH000029', 31, 32, N'PAID', DATEADD(second,-637200,CAST(@homNay AS datetime2))),
    (31, N'DH000030', 31, 33, N'PAID', DATEADD(second,-633600,CAST(@homNay AS datetime2))),
    (32, N'DH-6bb229c7f26a40cb8806', 1, NULL, N'PAID', DATEADD(second,-523756,CAST(@homNay AS datetime2))),
    (33, N'DH-0eb0899617ee45bca197', 33, NULL, N'PAID', DATEADD(second,-484351,CAST(@homNay AS datetime2))),
    (34, N'DH-9ce6ae4af01d4c9e8fc2', 33, NULL, N'PAID', DATEADD(second,-122380,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[don_hang] OFF;

-- chi_tiet_don_hang (36 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_don_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_don_hang] ON;
INSERT INTO dbo.[chi_tiet_don_hang] ([id], [don_hang_id], [san_pham_id], [so_luong], [don_gia], [giam_gia], [ma_dich_vu], [ten_hang])
VALUES
    (1, 1, 3, 1, 285000.00, 0.00, NULL, N'Royal Canin Adult 2kg'),
    (2, 2, 17, 2, 160000.00, 0.00, NULL, N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 lít; Đóng gói: 1 túi'),
    (3, 3, 18, 3, 10000.00, 0.00, NULL, N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói'),
    (4, 4, 19, 1, 225000.00, 0.00, NULL, N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800g'),
    (5, 5, 20, 2, 150000.00, 0.00, NULL, N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150ml'),
    (6, 6, 21, 3, 30000.00, 0.00, NULL, N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85g; Đóng gói: 1 lon'),
    (7, 7, 22, 1, 130000.00, 0.00, NULL, N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400g'),
    (8, 8, 23, 2, 25000.00, 0.00, NULL, N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85g'),
    (9, 9, 24, 3, 160000.00, 0.00, NULL, N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 lít'),
    (10, 10, 25, 1, 125000.00, 0.00, NULL, N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400g'),
    (11, 11, 26, 2, 25000.00, 0.00, NULL, N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85g'),
    (12, 12, 27, 3, 5000.00, 0.00, NULL, N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ'),
    (13, 13, 28, 1, 25000.00, 0.00, NULL, N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef'),
    (14, 14, 29, 2, 35000.00, 0.00, NULL, N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100g; Đóng gói: 1 lon'),
    (15, 15, 30, 3, 25000.00, 0.00, NULL, N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100g'),
    (16, 16, 31, 1, 185000.00, 0.00, NULL, N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500g'),
    (17, 17, 32, 2, 25000.00, 0.00, NULL, N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85g'),
    (18, 18, 44, 2, 30000.00, 0.00, NULL, N'Đồ chơi cho chó mèo dạng bóng thừng BOBO'),
    (19, 19, 44, 2, 30000.00, 0.00, NULL, N'Đồ chơi cho chó mèo dạng bóng thừng BOBO'),
    (20, 20, NULL, 1, 125000.00, 0.00, N'DV000005', N'Dịch vụ Spa'),
    (21, 21, NULL, 1, 150000.00, 0.00, N'DV000006', N'Dịch vụ Spa'),
    (22, 22, NULL, 1, 175000.00, 0.00, N'DV000007', N'Dịch vụ Spa'),
    (23, 23, NULL, 1, 200000.00, 0.00, N'DV000008', N'Dịch vụ Spa'),
    (24, 24, NULL, 1, 225000.00, 0.00, N'DV000009', N'Dịch vụ Spa'),
    (25, 25, NULL, 1, 250000.00, 0.00, N'DV000010', N'Dịch vụ Spa'),
    (26, 26, NULL, 1, 90000.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (27, 27, NULL, 1, 90000.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (28, 28, NULL, 1, 90000.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (29, 29, NULL, 1, 90000.00, 0.00, N'HOTEL', N'Quyết toán Hotel (đã trừ cọc)'),
    (30, 30, 55, 1, 35000.00, 0.00, NULL, N'Đồ chơi dây thừng cho chó BOBO'),
    (31, 31, 55, 1, 35000.00, 0.00, NULL, N'Đồ chơi dây thừng cho chó BOBO'),
    (32, 32, 51, 2, 85000.00, 0.00, NULL, N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb'),
    (33, 32, 58, 1, 30000.00, 0.00, NULL, N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush'),
    (34, 33, 46, 2, 50000.00, 0.00, NULL, N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone'),
    (35, 34, 7, 1, 450000.00, 0.00, NULL, N'Lồng vận chuyển size L'),
    (36, 34, 36, 1, 140000.00, 0.00, NULL, N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1kg');
SET IDENTITY_INSERT dbo.[chi_tiet_don_hang] OFF;

-- hoa_don (34 dòng)
DBCC CHECKIDENT ('dbo.hoa_don', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[hoa_don] ON;
INSERT INTO dbo.[hoa_don] ([id], [so_hoa_don], [don_hang_id], [tong_tien], [hinh_thuc_thanh_toan], [tien_khach_dua], [tien_thoi], [diem_cong], [trang_thai], [thanh_toan_luc], [idempotency_key], [tam_tinh], [giam_gia], [diem_dung], [ma_voucher], [ghi_chu])
VALUES
    (1, N'HD-SAMPLE-001', 1, 285000.00, N'CASH', 285000.00, 0.00, 28, N'PAID', DATEADD(second,-553572,CAST(@homNay AS datetime2)), N'SEED-HD-SAMPLE-001', 285000.00, 0.00, 0, NULL, NULL),
    (2, N'HD000001', 2, 320000.00, N'CASH', 320000.00, 0.00, 32, N'PAID', DATEADD(second,-3070800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_1', 320000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (3, N'HD000002', 3, 30000.00, N'CASH', 30000.00, 0.00, 3, N'PAID', DATEADD(second,-2980800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_2', 30000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (4, N'HD000003', 4, 225000.00, N'CASH', 225000.00, 0.00, 22, N'PAID', DATEADD(second,-2890800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_3', 225000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (5, N'HD000004', 5, 300000.00, N'CASH', 300000.00, 0.00, 30, N'PAID', DATEADD(second,-2800800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_4', 300000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (6, N'HD000005', 6, 90000.00, N'CASH', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-2710800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_5', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (7, N'HD000006', 7, 130000.00, N'CASH', 130000.00, 0.00, 13, N'PAID', DATEADD(second,-2620800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_6', 130000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (8, N'HD000007', 8, 50000.00, N'CASH', 50000.00, 0.00, 5, N'PAID', DATEADD(second,-2530800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_7', 50000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (9, N'HD000008', 9, 480000.00, N'CASH', 480000.00, 0.00, 48, N'PAID', DATEADD(second,-2469600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_8', 480000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (10, N'HD000009', 10, 125000.00, N'CASH', 125000.00, 0.00, 12, N'PAID', DATEADD(second,-2379600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_9', 125000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (11, N'HD000010', 11, 50000.00, N'CASH', 50000.00, 0.00, 5, N'PAID', DATEADD(second,-2289600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_10', 50000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (12, N'HD000011', 12, 15000.00, N'CASH', 15000.00, 0.00, 1, N'PAID', DATEADD(second,-2199600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_11', 15000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (13, N'HD000012', 13, 25000.00, N'CASH', 25000.00, 0.00, 2, N'PAID', DATEADD(second,-2109600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_12', 25000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (14, N'HD000013', 14, 70000.00, N'CASH', 70000.00, 0.00, 7, N'PAID', DATEADD(second,-2019600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_13', 70000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (15, N'HD000014', 15, 75000.00, N'CASH', 75000.00, 0.00, 7, N'PAID', DATEADD(second,-1929600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_14', 75000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (16, N'HD000015', 16, 185000.00, N'CASH', 185000.00, 0.00, 18, N'PAID', DATEADD(second,-1839600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_15', 185000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (17, N'HD000016', 17, 50000.00, N'CASH', 50000.00, 0.00, 5, N'PAID', DATEADD(second,-1778400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_16', 50000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (18, N'HD000017', 18, 60000.00, N'CASH', 60000.00, 0.00, 6, N'PAID', DATEADD(second,-738000,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_17', 60000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (19, N'HD000018', 19, 60000.00, N'CASH', 60000.00, 0.00, 6, N'PAID', DATEADD(second,-734400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_18', 60000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (20, N'HD000019', 20, 125000.00, N'CASH', 125000.00, 0.00, 12, N'PAID', DATEADD(second,-1508400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_19', 125000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (21, N'HD000020', 21, 150000.00, N'CASH', 150000.00, 0.00, 15, N'PAID', DATEADD(second,-1418400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_20', 150000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (22, N'HD000021', 22, 175000.00, N'CASH', 175000.00, 0.00, 17, N'PAID', DATEADD(second,-1328400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_21', 175000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (23, N'HD000022', 23, 200000.00, N'CASH', 200000.00, 0.00, 20, N'PAID', DATEADD(second,-1238400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_22', 200000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (24, N'HD000023', 24, 225000.00, N'CASH', 225000.00, 0.00, 22, N'PAID', DATEADD(second,-1148400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_23', 225000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (25, N'HD000024', 25, 250000.00, N'CASH', 250000.00, 0.00, 25, N'PAID', DATEADD(second,-1087200,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_24', 250000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (26, N'HD000025', 26, 90000.00, N'CASH', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-1342800,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_25', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (27, N'HD000026', 27, 90000.00, N'CASH', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-1166400,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_26', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (28, N'HD000027', 28, 90000.00, N'CASH', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-990000,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_27', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (29, N'HD000028', 29, 90000.00, N'CASH', 90000.00, 0.00, 9, N'PAID', DATEADD(second,-813600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_28', 90000.00, 0.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (30, N'HD000029', 30, 5000.00, N'CASH', 5000.00, 0.00, 0, N'PAID', DATEADD(second,-637200,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_29', 35000.00, 30000.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (31, N'HD000030', 31, 5000.00, N'CASH', 5000.00, 0.00, 0, N'PAID', DATEADD(second,-633600,CAST(@homNay AS datetime2)), N'REAL_DATA_V1_INVOICE_30', 35000.00, 30000.00, 0, NULL, N'Đã kiểm tra thông tin thanh toán.'),
    (32, N'HD-b65502c673d14ca7a202', 32, 180000.00, N'CASH', 180000.00, 0.00, 0, N'PAID', DATEADD(second,-523756,CAST(@homNay AS datetime2)), N'PAY-e788e605e1f84d8c87c8', 200000.00, 20000.00, 0, N'', NULL),
    (33, N'HD-14c52b57f8004da7b89c', 33, 90000.00, N'CASH', 90000.00, 0.00, 0, N'PAID', DATEADD(second,-484351,CAST(@homNay AS datetime2)), N'PAY-a554c66e3ee0419e844b', 100000.00, 10000.00, 0, N'', NULL),
    (34, N'HD-9cb6b419e67a476e9fcb', 34, 531000.00, N'CASH', 531000.00, 0.00, 0, N'PAID', DATEADD(second,-122380,CAST(@homNay AS datetime2)), N'PAY-bd9b970ee60541f4b1d2', 590000.00, 59000.00, 0, N'', NULL);
SET IDENTITY_INSERT dbo.[hoa_don] OFF;

-- lich_su_diem (41 dòng)
DBCC CHECKIDENT ('dbo.lich_su_diem', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lich_su_diem] ON;
INSERT INTO dbo.[lich_su_diem] ([id], [khach_hang_id], [hoa_don_id], [thay_doi], [so_du_sau], [ly_do], [tao_luc])
VALUES
    (1, 7, 1, 28, 1268, N'Hóa đơn dữ liệu mẫu', DATEADD(second,-553572,CAST(@homNay AS datetime2))),
    (2, 13, NULL, 199, 199, N'Số dư đầu', DATEADD(second,-552899,CAST(@homNay AS datetime2))),
    (3, 14, NULL, 200, 200, N'Số dư đầu', DATEADD(second,-552899,CAST(@homNay AS datetime2))),
    (4, 16, 2, 32, 32, N'tích điểm giao dịch', DATEADD(second,-3070800,CAST(@homNay AS datetime2))),
    (5, 17, 3, 3, 3, N'tích điểm giao dịch', DATEADD(second,-2980800,CAST(@homNay AS datetime2))),
    (6, 18, 4, 22, 22, N'tích điểm giao dịch', DATEADD(second,-2890800,CAST(@homNay AS datetime2))),
    (7, 19, 5, 30, 30, N'tích điểm giao dịch', DATEADD(second,-2800800,CAST(@homNay AS datetime2))),
    (8, 20, 6, 9, 9, N'tích điểm giao dịch', DATEADD(second,-2710800,CAST(@homNay AS datetime2))),
    (9, 21, 7, 13, 13, N'tích điểm giao dịch', DATEADD(second,-2620800,CAST(@homNay AS datetime2))),
    (10, 22, 8, 5, 5, N'tích điểm giao dịch', DATEADD(second,-2530800,CAST(@homNay AS datetime2))),
    (11, 23, 9, 48, 48, N'tích điểm giao dịch', DATEADD(second,-2469600,CAST(@homNay AS datetime2))),
    (12, 24, 10, 12, 12, N'tích điểm giao dịch', DATEADD(second,-2379600,CAST(@homNay AS datetime2))),
    (13, 25, 11, 5, 5, N'tích điểm giao dịch', DATEADD(second,-2289600,CAST(@homNay AS datetime2))),
    (14, 26, 12, 1, 1, N'tích điểm giao dịch', DATEADD(second,-2199600,CAST(@homNay AS datetime2))),
    (15, 27, 13, 2, 2, N'tích điểm giao dịch', DATEADD(second,-2109600,CAST(@homNay AS datetime2))),
    (16, 28, 14, 7, 7, N'tích điểm giao dịch', DATEADD(second,-2019600,CAST(@homNay AS datetime2))),
    (17, 29, 15, 7, 7, N'tích điểm giao dịch', DATEADD(second,-1929600,CAST(@homNay AS datetime2))),
    (18, 30, 16, 18, 18, N'tích điểm giao dịch', DATEADD(second,-1839600,CAST(@homNay AS datetime2))),
    (19, 31, 17, 5, 5, N'tích điểm giao dịch', DATEADD(second,-1778400,CAST(@homNay AS datetime2))),
    (20, 32, 18, 6, 6, N'tích điểm giao dịch', DATEADD(second,-738000,CAST(@homNay AS datetime2))),
    (21, 33, 19, 6, 6, N'tích điểm giao dịch', DATEADD(second,-734400,CAST(@homNay AS datetime2))),
    (22, 34, 20, 12, 12, N'tích điểm giao dịch', DATEADD(second,-1508400,CAST(@homNay AS datetime2))),
    (23, 35, 21, 15, 15, N'tích điểm giao dịch', DATEADD(second,-1418400,CAST(@homNay AS datetime2))),
    (24, 16, 22, 17, 49, N'tích điểm giao dịch', DATEADD(second,-1328400,CAST(@homNay AS datetime2))),
    (25, 17, 23, 20, 23, N'tích điểm giao dịch', DATEADD(second,-1238400,CAST(@homNay AS datetime2))),
    (26, 18, 24, 22, 44, N'tích điểm giao dịch', DATEADD(second,-1148400,CAST(@homNay AS datetime2))),
    (27, 19, 25, 25, 55, N'tích điểm giao dịch', DATEADD(second,-1087200,CAST(@homNay AS datetime2))),
    (28, 20, 26, 9, 18, N'tích điểm giao dịch', DATEADD(second,-1342800,CAST(@homNay AS datetime2))),
    (29, 21, 27, 9, 22, N'tích điểm giao dịch', DATEADD(second,-1166400,CAST(@homNay AS datetime2))),
    (30, 22, 28, 9, 14, N'tích điểm giao dịch', DATEADD(second,-990000,CAST(@homNay AS datetime2))),
    (31, 23, 29, 9, 57, N'tích điểm giao dịch', DATEADD(second,-813600,CAST(@homNay AS datetime2))),
    (32, 32, 30, 0, 6, N'tích điểm giao dịch', DATEADD(second,-637200,CAST(@homNay AS datetime2))),
    (33, 33, 31, 0, 6, N'tích điểm giao dịch', DATEADD(second,-633600,CAST(@homNay AS datetime2))),
    (34, 16, NULL, 400, 449, N'điểm mở đầu', DATEADD(second,-549878,CAST(@homNay AS datetime2))),
    (35, 16, NULL, -200, 249, N'đổi voucher Spa 150000', DATEADD(second,-549878,CAST(@homNay AS datetime2))),
    (36, 7, NULL, 1240, 1240, N'Số dư đầu kỳ mẫu', DATEADD(second,-5270400,CAST(@homNay AS datetime2))),
    (37, 8, NULL, 4860, 4860, N'Số dư đầu kỳ mẫu', DATEADD(second,-5270400,CAST(@homNay AS datetime2))),
    (38, 9, NULL, 3210, 3210, N'Số dư đầu kỳ mẫu', DATEADD(second,-5270400,CAST(@homNay AS datetime2))),
    (39, 10, NULL, 840, 840, N'Số dư đầu kỳ mẫu', DATEADD(second,-5270400,CAST(@homNay AS datetime2))),
    (40, 11, NULL, 230, 230, N'Số dư đầu kỳ mẫu', DATEADD(second,-5270400,CAST(@homNay AS datetime2))),
    (41, 12, NULL, 1980, 1980, N'Số dư đầu kỳ mẫu', DATEADD(second,-5270400,CAST(@homNay AS datetime2)));
SET IDENTITY_INSERT dbo.[lich_su_diem] OFF;

-- phan_bo_lo_hang (26 dòng)
DBCC CHECKIDENT ('dbo.phan_bo_lo_hang', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phan_bo_lo_hang] ON;
INSERT INTO dbo.[phan_bo_lo_hang] ([id], [chi_tiet_don_hang_id], [lo_hang_id], [so_luong])
VALUES
    (1, 1, 14, 1),
    (2, 2, 17, 2),
    (3, 3, 18, 3),
    (4, 4, 19, 1),
    (5, 5, 20, 2),
    (6, 6, 21, 3),
    (7, 7, 22, 1),
    (8, 8, 23, 2),
    (9, 9, 24, 3),
    (10, 10, 25, 1),
    (11, 11, 26, 2),
    (12, 12, 27, 3),
    (13, 13, 28, 1),
    (14, 14, 29, 2),
    (15, 15, 30, 3),
    (16, 16, 31, 1),
    (17, 17, 32, 2),
    (18, 18, 44, 2),
    (19, 19, 44, 2),
    (20, 30, 55, 1),
    (21, 31, 55, 1),
    (22, 32, 51, 2),
    (23, 33, 58, 1),
    (24, 34, 46, 2),
    (25, 35, 6, 1),
    (26, 36, 36, 1);
SET IDENTITY_INSERT dbo.[phan_bo_lo_hang] OFF;

-- phieu_doi_tra (2 dòng)
DBCC CHECKIDENT ('dbo.phieu_doi_tra', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_doi_tra] ON;
INSERT INTO dbo.[phieu_doi_tra] ([id], [ma_phieu], [hoa_don_id], [nguoi_lap_id], [chenh_lech], [trang_thai], [tao_luc], [ly_do], [loai_phieu], [hoa_don_thay_the_id], [gia_tri_duoc_doi], [khoa_thao_tac])
VALUES
    (1, N'DT000001', 18, 3, 5000.00, N'COMPLETED', DATEADD(second,-637200,CAST(@homNay AS datetime2)), N'đổi phụ kiện trong ba ngày, không hoàn tiền', N'EXCHANGE', 30, 30000, N'REAL_DATA_V1_EXCHANGE_29'),
    (2, N'DT000002', 19, 3, 5000.00, N'COMPLETED', DATEADD(second,-633600,CAST(@homNay AS datetime2)), N'đổi phụ kiện trong ba ngày, không hoàn tiền', N'EXCHANGE', 31, 30000, N'REAL_DATA_V1_EXCHANGE_30');
SET IDENTITY_INSERT dbo.[phieu_doi_tra] OFF;

-- chi_tiet_doi_tra (2 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_doi_tra', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_doi_tra] ON;
INSERT INTO dbo.[chi_tiet_doi_tra] ([id], [phieu_doi_tra_id], [chi_tiet_don_hang_id], [lo_hang_id], [so_luong], [tien_hoan])
VALUES
    (1, 1, 18, 44, 1, 30000.00),
    (2, 2, 19, 44, 1, 30000.00);
SET IDENTITY_INSERT dbo.[chi_tiet_doi_tra] OFF;

-- phieu_nhap_kho (62 dòng)
DBCC CHECKIDENT ('dbo.phieu_nhap_kho', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_nhap_kho] ON;
INSERT INTO dbo.[phieu_nhap_kho] ([id], [so_phieu], [nha_cung_cap_id], [nguoi_lap_id], [ngay_nhap], [tong_tien], [trang_thai], [ghi_chu], [da_tra])
VALUES
    (1, N'PN-SAMPLE-001', 1, 2, DATEADD(day,-7,@homNay), 720000.00, N'POSTED', NULL, 240000.00),
    (2, N'PN000001', 5, 4, DATEADD(day,-36,@homNay), 1144000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 572000.00),
    (3, N'PN000002', 6, 4, DATEADD(day,-36,@homNay), 78000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 39000.00),
    (4, N'PN000003', 7, 4, DATEADD(day,-36,@homNay), 1901250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1901250.00),
    (5, N'PN000004', 8, 4, DATEADD(day,-36,@homNay), 1365000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 682500.00),
    (6, N'PN000005', 9, 4, DATEADD(day,-36,@homNay), 292500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 146250.00),
    (7, N'PN000006', 10, 4, DATEADD(day,-36,@homNay), 1352000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1352000.00),
    (8, N'PN000007', 5, 4, DATEADD(day,-36,@homNay), 276250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 138125.00),
    (9, N'PN000008', 6, 4, DATEADD(day,-36,@homNay), 1872000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 936000.00),
    (10, N'PN000009', 7, 4, DATEADD(day,-36,@homNay), 1543750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1543750.00),
    (11, N'PN000010', 8, 4, DATEADD(day,-36,@homNay), 325000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 162500.00),
    (12, N'PN000011', 9, 4, DATEADD(day,-36,@homNay), 68250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 34125.00),
    (13, N'PN000012', 10, 4, DATEADD(day,-36,@homNay), 357500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 357500.00),
    (14, N'PN000013', 5, 4, DATEADD(day,-36,@homNay), 523250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 261625.00),
    (15, N'PN000014', 6, 4, DATEADD(day,-36,@homNay), 390000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 195000.00),
    (16, N'PN000015', 7, 4, DATEADD(day,-36,@homNay), 3006250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 3006250.00),
    (17, N'PN000016', 8, 4, DATEADD(day,-36,@homNay), 422500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 211250.00),
    (18, N'PN000017', 9, 4, DATEADD(day,-36,@homNay), 526500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 263250.00),
    (19, N'PN000018', 10, 4, DATEADD(day,-36,@homNay), 546000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 546000.00),
    (20, N'PN000019', 5, 4, DATEADD(day,-36,@homNay), 377000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 188500.00),
    (21, N'PN000020', 6, 4, DATEADD(day,-36,@homNay), 2730000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1365000.00),
    (22, N'PN000021', 7, 4, DATEADD(day,-36,@homNay), 162500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 162500.00),
    (23, N'PN000022', 8, 4, DATEADD(day,-36,@homNay), 929500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 464750.00),
    (24, N'PN000023', 9, 4, DATEADD(day,-36,@homNay), 234000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 117000.00),
    (25, N'PN000024', 10, 4, DATEADD(day,-36,@homNay), 971750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 971750.00),
    (26, N'PN000025', 5, 4, DATEADD(day,-36,@homNay), 2866500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1433250.00),
    (27, N'PN000026', 6, 4, DATEADD(day,-36,@homNay), 341250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 170625.00),
    (28, N'PN000027', 7, 4, DATEADD(day,-36,@homNay), 208000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 208000.00),
    (29, N'PN000028', 8, 4, DATEADD(day,-36,@homNay), 331500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 165750.00),
    (30, N'PN000029', 9, 4, DATEADD(day,-36,@homNay), 351000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 175500.00),
    (31, N'PN000030', 10, 4, DATEADD(day,-36,@homNay), 617500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 617500.00),
    (32, N'PN000031', 5, 4, DATEADD(day,-36,@homNay), 2340000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1170000.00),
    (33, N'PN000032', 6, 4, DATEADD(day,-36,@homNay), 3412500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1706250.00),
    (34, N'PN000033', 7, 4, DATEADD(day,-36,@homNay), 143000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 143000.00),
    (35, N'PN000034', 8, 4, DATEADD(day,-36,@homNay), 2392000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1196000.00),
    (36, N'PN000035', 9, 4, DATEADD(day,-36,@homNay), 1326000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 663000.00),
    (37, N'PN000036', 10, 4, DATEADD(day,-36,@homNay), 2518750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 2518750.00),
    (38, N'PN000037', 5, 4, DATEADD(day,-36,@homNay), 1183000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 591500.00),
    (39, N'PN000038', 6, 4, DATEADD(day,-36,@homNay), 789750.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 394875.00),
    (40, N'PN000039', 7, 4, DATEADD(day,-36,@homNay), 637000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 637000.00),
    (41, N'PN000040', 8, 4, DATEADD(day,-36,@homNay), 1131000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 565500.00),
    (42, N'PN000041', 9, 4, DATEADD(day,-36,@homNay), 4290000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 2145000.00),
    (43, N'PN000042', 10, 4, DATEADD(day,-36,@homNay), 195000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 195000.00),
    (44, N'PN000043', 5, 4, DATEADD(day,-36,@homNay), 536250.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 268125.00),
    (45, N'PN000044', 6, 4, DATEADD(day,-36,@homNay), 1950000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 975000.00),
    (46, N'PN000045', 7, 4, DATEADD(day,-36,@homNay), 422500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 422500.00),
    (47, N'PN000046', 8, 4, DATEADD(day,-36,@homNay), 728000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 364000.00),
    (48, N'PN000047', 9, 4, DATEADD(day,-36,@homNay), 487500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 243750.00),
    (49, N'PN000048', 10, 4, DATEADD(day,-36,@homNay), 1560000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1560000.00),
    (50, N'PN000049', 5, 4, DATEADD(day,-36,@homNay), 442000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 221000.00),
    (51, N'PN000050', 6, 4, DATEADD(day,-36,@homNay), 819000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 409500.00),
    (52, N'PN000051', 7, 4, DATEADD(day,-36,@homNay), 1852500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1852500.00),
    (53, N'PN000052', 8, 4, DATEADD(day,-36,@homNay), 715000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 357500.00),
    (54, N'PN000053', 9, 4, DATEADD(day,-36,@homNay), 546000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 273000.00),
    (55, N'PN000054', 10, 4, DATEADD(day,-36,@homNay), 3575000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 3575000.00),
    (56, N'PN000055', 5, 4, DATEADD(day,-36,@homNay), 2541500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1270750.00),
    (57, N'PN000056', 6, 4, DATEADD(day,-36,@homNay), 3042000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1521000.00),
    (58, N'PN000057', 7, 4, DATEADD(day,-36,@homNay), 1300000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 1300000.00),
    (59, N'PN000058', 8, 4, DATEADD(day,-36,@homNay), 1014000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 507000.00),
    (60, N'PN000059', 9, 4, DATEADD(day,-36,@homNay), 877500.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 438750.00),
    (61, N'PN000060', 10, 4, DATEADD(day,-36,@homNay), 637000.00, N'POSTED', N'Nhập bổ sung hàng hóa.', 637000.00),
    (62, N'PN-DAU-KY', 1, 2, DATEADD(day,-60,@homNay), 25978000, N'POSTED', NULL, 25978000);
SET IDENTITY_INSERT dbo.[phieu_nhap_kho] OFF;

-- chi_tiet_phieu_nhap (74 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_phieu_nhap', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_nhap] ON;
INSERT INTO dbo.[chi_tiet_phieu_nhap] ([id], [phieu_nhap_id], [san_pham_id], [so_lo], [han_su_dung], [so_luong], [gia_nhap])
VALUES
    (1, 1, 3, N'SAMPLE-RECEIPT', DATEADD(day,173,@homNay), 3, 240000.00),
    (2, 2, 17, N'LO000003', NULL, 11, 104000.00),
    (3, 3, 18, N'LO000004', DATEADD(day,173,@homNay), 12, 6500.00),
    (4, 4, 19, N'LO000005', DATEADD(day,173,@homNay), 13, 146250.00),
    (5, 5, 20, N'LO000006', NULL, 14, 97500.00),
    (6, 6, 21, N'LO000007', DATEADD(day,173,@homNay), 15, 19500.00),
    (7, 7, 22, N'LO000008', DATEADD(day,173,@homNay), 16, 84500.00),
    (8, 8, 23, N'LO000009', DATEADD(day,173,@homNay), 17, 16250.00),
    (9, 9, 24, N'LO000010', NULL, 18, 104000.00),
    (10, 10, 25, N'LO000011', DATEADD(day,8,@homNay), 19, 81250.00),
    (11, 11, 26, N'LO000012', DATEADD(day,173,@homNay), 20, 16250.00),
    (12, 12, 27, N'LO000013', NULL, 21, 3250.00),
    (13, 13, 28, N'LO000014', DATEADD(day,173,@homNay), 22, 16250.00),
    (14, 14, 29, N'LO000015', DATEADD(day,173,@homNay), 23, 22750.00),
    (15, 15, 30, N'LO000016', DATEADD(day,173,@homNay), 24, 16250.00),
    (16, 16, 31, N'LO000017', DATEADD(day,173,@homNay), 25, 120250.00),
    (17, 17, 32, N'LO000018', DATEADD(day,173,@homNay), 26, 16250.00),
    (18, 18, 33, N'LO000019', DATEADD(day,173,@homNay), 27, 19500.00),
    (19, 19, 34, N'LO000020', DATEADD(day,8,@homNay), 28, 19500.00),
    (20, 20, 35, N'LO000021', DATEADD(day,173,@homNay), 29, 13000.00),
    (21, 21, 36, N'LO000022', NULL, 30, 91000.00),
    (22, 22, 37, N'LO000023', DATEADD(day,173,@homNay), 10, 16250.00),
    (23, 23, 38, N'LO000024', DATEADD(day,173,@homNay), 11, 84500.00),
    (24, 24, 39, N'LO000025', DATEADD(day,173,@homNay), 12, 19500.00),
    (25, 25, 40, N'LO000026', DATEADD(day,173,@homNay), 13, 74750.00),
    (26, 26, 41, N'LO000027', DATEADD(day,173,@homNay), 14, 204750.00),
    (27, 27, 42, N'LO000028', DATEADD(day,173,@homNay), 15, 22750.00),
    (28, 28, 43, N'LO000029', DATEADD(day,8,@homNay), 16, 13000.00),
    (29, 29, 44, N'LO000030', NULL, 17, 19500.00),
    (30, 30, 45, N'LO000031', DATEADD(day,173,@homNay), 18, 19500.00),
    (31, 31, 46, N'LO000032', DATEADD(day,173,@homNay), 19, 32500.00),
    (32, 32, 47, N'LO000033', DATEADD(day,173,@homNay), 20, 117000.00),
    (33, 33, 48, N'LO000034', NULL, 21, 162500.00),
    (34, 34, 49, N'LO000035', NULL, 22, 6500.00),
    (35, 35, 50, N'LO000036', NULL, 23, 104000.00),
    (36, 36, 51, N'LO000037', DATEADD(day,173,@homNay), 24, 55250.00),
    (37, 37, 52, N'LO000038', NULL, 25, 100750.00),
    (38, 38, 53, N'LO000039', NULL, 26, 45500.00),
    (39, 39, 54, N'LO000040', NULL, 27, 29250.00),
    (40, 40, 55, N'LO000041', NULL, 28, 22750.00),
    (41, 41, 56, N'LO000042', NULL, 29, 39000.00),
    (42, 42, 57, N'LO000043', NULL, 30, 143000.00),
    (43, 43, 58, N'LO000044', DATEADD(day,173,@homNay), 10, 19500.00),
    (44, 44, 59, N'LO000045', NULL, 11, 48750.00),
    (45, 45, 60, N'LO000046', DATEADD(day,173,@homNay), 12, 162500.00),
    (46, 46, 61, N'LO000047', NULL, 13, 32500.00),
    (47, 47, 62, N'LO000048', NULL, 14, 52000.00),
    (48, 48, 63, N'LO000049', NULL, 15, 32500.00),
    (49, 49, 64, N'LO000050', DATEADD(day,173,@homNay), 16, 97500.00),
    (50, 50, 65, N'LO000051', DATEADD(day,173,@homNay), 17, 26000.00),
    (51, 51, 66, N'LO000052', DATEADD(day,173,@homNay), 18, 45500.00),
    (52, 52, 67, N'LO000053', DATEADD(day,173,@homNay), 19, 97500.00),
    (53, 53, 68, N'LO000054', NULL, 20, 35750.00),
    (54, 54, 69, N'LO000055', NULL, 21, 26000.00),
    (55, 55, 70, N'LO000056', DATEADD(day,8,@homNay), 22, 162500.00),
    (56, 56, 71, N'LO000057', DATEADD(day,173,@homNay), 23, 110500.00),
    (57, 57, 72, N'LO000058', NULL, 24, 126750.00),
    (58, 58, 73, N'LO000059', NULL, 25, 52000.00),
    (59, 59, 74, N'LO000060', NULL, 26, 39000.00),
    (60, 60, 75, N'LO000061', NULL, 27, 32500.00),
    (61, 61, 76, N'LO000062', NULL, 28, 22750.00),
    (62, 62, 3, N'SEED-001', DATEADD(day,173,@homNay), 47, 240000),
    (63, 62, 4, N'SEED-001', DATEADD(day,173,@homNay), 82, 36000),
    (64, 62, 5, N'SEED-001', DATEADD(day,173,@homNay), 3, 80000),
    (65, 62, 6, N'SEED-001', DATEADD(day,173,@homNay), 28, 62000),
    (66, 62, 7, N'SEED-001', DATEADD(day,173,@homNay), 8, 330000),
    (67, 62, 8, N'SEED-001', DATEADD(day,173,@homNay), 15, 20000),
    (68, 62, 9, N'SEED-001', DATEADD(day,173,@homNay), 2, 430000),
    (69, 62, 10, N'SEED-001', DATEADD(day,173,@homNay), 31, 55000),
    (70, 62, 11, N'SEED-001', DATEADD(day,173,@homNay), 12, 40000),
    (71, 62, 13, N'SEED-001', DATEADD(day,173,@homNay), 40, 30000),
    (72, 62, 14, N'SEED-001', DATEADD(day,173,@homNay), 9, 65000),
    (73, 62, 15, N'LO000001', NULL, 20, 50000),
    (74, 62, 16, N'LO000002', NULL, 20, 50000);
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_nhap] OFF;

-- phieu_xuat_kho (1 dòng)
DBCC CHECKIDENT ('dbo.phieu_xuat_kho', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_xuat_kho] ON;
INSERT INTO dbo.[phieu_xuat_kho] ([id], [so_phieu], [nguoi_lap_id], [loai_xuat], [ly_do], [tong_gia_tri], [trang_thai], [tao_luc])
VALUES
    (1, N'PX000001', 4, N'WRITE_OFF', N'hàng hỏng bao bì', 3861000.00, N'POSTED', DATEADD(second,-549878,CAST(@homNay AS datetime2)));
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
INSERT INTO dbo.[thu_cung] ([id], [ma_thu_cung], [khach_hang_id], [ten], [loai], [giong], [gioi_tinh], [mau_long], [can_nang], [tinh_trang_suc_khoe], [di_ung_benh_ly], [anh], [dang_theo_doi], [ngay_sinh], [tinh_cach], [tiem_chung])
VALUES
    (6, N'TC001', 7, N'Mochi', N'DOG', N'Poodle', N'Đực', N'Trắng', 5.20, NULL, N'', NULL, 1, CONVERT(date,'2022-03-12',23), N'Hiền, sợ tiếng máy sấy', N'Dại (01/2026), 5 bệnh (02/2026)'),
    (7, N'TC002', 8, N'Bống', N'CAT', N'Anh lông ngắn', N'Cái', N'Xám', 4.10, NULL, N'Dị ứng cá ngừ', NULL, 1, CONVERT(date,'2021-07-05',23), N'Nhút nhát', N'Dại (03/2026)'),
    (8, N'TC003', 9, N'Lucky', N'DOG', N'Golden Retriever', N'Đực', N'Vàng', 29.50, NULL, N'', NULL, 1, CONVERT(date,'2020-11-20',23), N'Năng động, thân thiện', N'Dại, 7 bệnh (12/2025)'),
    (9, N'TC004', 10, N'Miu', N'CAT', N'Ba Tư', N'Cái', N'Kem', 3.80, NULL, N'Hen suyễn nhẹ', NULL, 1, CONVERT(date,'2023-02-02',23), N'Hay cào khi bị bế', N''),
    (10, N'TC005', 12, N'Coco', N'DOG', N'Corgi', N'Cái', N'Nâu trắng', 11.00, NULL, N'', NULL, 1, CONVERT(date,'2021-09-14',23), N'Ham ăn', N'Dại (06/2026)'),
    (11, N'TC000006', 14, N'Bông', N'DOG', NULL, NULL, NULL, 4.00, NULL, NULL, NULL, 1, CONVERT(date,'2026-04-19',23), N'', N''),
    (12, N'TC000007', 14, N'Mun', N'DOG', NULL, NULL, NULL, 4.00, NULL, NULL, NULL, 1, CONVERT(date,'2026-04-19',23), N'', N''),
    (13, N'TC000008', 16, N'Mochi', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-09-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (14, N'TC000009', 17, N'Milo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-08-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (15, N'TC000010', 18, N'Bông', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-07-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (16, N'TC000011', 19, N'Lucky', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-06-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (17, N'TC000012', 20, N'Coco', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-05-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (18, N'TC000013', 21, N'Miu', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-04-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (19, N'TC000014', 22, N'Bắp', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-03-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (20, N'TC000015', 23, N'Đậu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-02-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (21, N'TC000016', 24, N'Sữa', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2025-01-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (22, N'TC000017', 25, N'Gạo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-12-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (23, N'TC000018', 26, N'Mochi', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-11-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (24, N'TC000019', 27, N'Milo', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-10-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (25, N'TC000020', 28, N'Bông', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-09-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (26, N'TC000021', 29, N'Lucky', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-08-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (27, N'TC000022', 30, N'Coco', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-07-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (28, N'TC000023', 31, N'Miu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-06-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (29, N'TC000024', 32, N'Bắp', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-05-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (30, N'TC000025', 33, N'Đậu', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-04-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (31, N'TC000026', 34, N'Sữa', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-03-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (32, N'TC000027', 35, N'Gạo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-02-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (33, N'TC000028', 16, N'Mochi', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2024-01-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (34, N'TC000029', 17, N'Milo', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-12-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (35, N'TC000030', 18, N'Bông', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-11-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (36, N'TC000031', 19, N'Lucky', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-10-19',23), N'Hồ sơ : hiền, cần làm quen', N'Có hai lần tiêm giả lập, xem lịch sử'),
    (37, N'TC000032', 20, N'Coco', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-09-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung'),
    (38, N'TC000033', 21, N'Miu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-08-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung'),
    (39, N'TC000034', 22, N'Bắp', N'DOG', N'Poodle', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-07-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung'),
    (40, N'TC000035', 23, N'Đậu', N'CAT', N'Anh lông ngắn', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-06-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung'),
    (41, N'TC000036', 24, N'Sữa', N'DOG', N'Poodle', N'Duc', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-05-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung'),
    (42, N'TC000037', 25, N'Gạo', N'CAT', N'Anh lông ngắn', N'Cai', N'Trắng / vàng', 3.50, N'Theo dõi sức khỏe định kỳ.', NULL, NULL, 1, CONVERT(date,'2023-04-19',23), N'Hồ sơ : hiền, cần làm quen', N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung');
SET IDENTITY_INSERT dbo.[thu_cung] OFF;

-- hop_dong_luu_tru (9 dòng)
DBCC CHECKIDENT ('dbo.hop_dong_luu_tru', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[hop_dong_luu_tru] ON;
INSERT INTO dbo.[hop_dong_luu_tru] ([id], [ma_hop_dong], [khach_hang_id], [thu_cung_id], [phong_id], [ngay_vao], [ngay_ra_du_kien], [ngay_ra_thuc_te], [tien_coc], [trang_thai], [gia_dem], [ghi_chu], [hoa_don_id], [da_nhan_coc], [nhan_coc_luc], [nhan_coc_boi], [phien_nhan_coc_id], [coc_yeu_cau], [ly_do_doi_coc])
VALUES
    (1, N'HT-SAMPLE-001', 8, 7, 2, DATEADD(second,-604800,CAST(@homNay AS datetime2)), DATEADD(second,-432000,CAST(@homNay AS datetime2)), NULL, 50000.00, N'BOOKED', 150000.00, N'Hợp đồng từ dữ liệu mẫu', NULL, 0, NULL, NULL, NULL, 150000.00, NULL),
    (2, N'HT000001', 20, 17, 8, DATEADD(second,-1429200,CAST(@homNay AS datetime2)), DATEADD(second,-1342800,CAST(@homNay AS datetime2)), DATEADD(second,-1342800,CAST(@homNay AS datetime2)), 90000.00, N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 26, 1, DATEADD(second,-1429200,CAST(@homNay AS datetime2)), 3, 22, 90000.00, NULL),
    (3, N'HT000002', 21, 18, 9, DATEADD(second,-1252800,CAST(@homNay AS datetime2)), DATEADD(second,-1166400,CAST(@homNay AS datetime2)), DATEADD(second,-1166400,CAST(@homNay AS datetime2)), 90000.00, N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 27, 1, DATEADD(second,-1252800,CAST(@homNay AS datetime2)), 3, 24, 90000.00, NULL),
    (4, N'HT000003', 22, 19, 10, DATEADD(second,-1076400,CAST(@homNay AS datetime2)), DATEADD(second,-990000,CAST(@homNay AS datetime2)), DATEADD(second,-990000,CAST(@homNay AS datetime2)), 90000.00, N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 28, 1, DATEADD(second,-1076400,CAST(@homNay AS datetime2)), 3, 26, 90000.00, NULL),
    (5, N'HT000004', 23, 20, 11, DATEADD(second,-900000,CAST(@homNay AS datetime2)), DATEADD(second,-813600,CAST(@homNay AS datetime2)), DATEADD(second,-813600,CAST(@homNay AS datetime2)), 90000.00, N'CHECKED_OUT', 180000.00, N'một đêm, cọc tiền mặt', 29, 1, DATEADD(second,-900000,CAST(@homNay AS datetime2)), 3, 28, 90000.00, NULL),
    (6, N'HT000005', 26, 23, 12, DATEADD(second,-658800,CAST(@homNay AS datetime2)), DATEADD(second,-399600,CAST(@homNay AS datetime2)), NULL, 270000.00, N'CHECKED_IN', 180000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-662400,CAST(@homNay AS datetime2)), 3, 31, 270000.00, NULL),
    (7, N'HT000006', 27, 24, 13, DATEADD(second,-658800,CAST(@homNay AS datetime2)), DATEADD(second,-399600,CAST(@homNay AS datetime2)), NULL, 375000.00, N'CHECKED_IN', 250000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-662400,CAST(@homNay AS datetime2)), 3, 31, 375000.00, NULL),
    (8, N'HT000007', 28, 25, 14, DATEADD(second,-486000,CAST(@homNay AS datetime2)), DATEADD(second,-226800,CAST(@homNay AS datetime2)), NULL, 375000.00, N'BOOKED', 250000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-576000,CAST(@homNay AS datetime2)), 3, 32, 375000.00, NULL),
    (9, N'HT000008', 29, 26, 15, DATEADD(second,-486000,CAST(@homNay AS datetime2)), DATEADD(second,-226800,CAST(@homNay AS datetime2)), NULL, 375000.00, N'BOOKED', 250000.00, N'cọc tiền mặt', NULL, 1, DATEADD(second,-576000,CAST(@homNay AS datetime2)), 3, 32, 375000.00, NULL);
SET IDENTITY_INSERT dbo.[hop_dong_luu_tru] OFF;

-- khoan_thu (42 dòng)
DBCC CHECKIDENT ('dbo.khoan_thu', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[khoan_thu] ON;
INSERT INTO dbo.[khoan_thu] ([id], [hoa_don_id], [hop_dong_id], [phien_id], [nguoi_nhan_id], [phuong_thuc], [so_tien], [khach_dua], [tien_thua], [nhan_luc], [khoa])
VALUES
    (1, 1, NULL, 2, 2, N'CASH', 285000.00, 285000.00, 0.00, DATEADD(second,-553572,CAST(@homNay AS datetime2)), N'LEGACY-HD-1'),
    (2, 2, NULL, 3, 3, N'CASH', 320000.00, 320000.00, 0.00, DATEADD(second,-3070800,CAST(@homNay AS datetime2)), N'LEGACY-HD-2'),
    (3, 3, NULL, 4, 3, N'CASH', 30000.00, 30000.00, 0.00, DATEADD(second,-2980800,CAST(@homNay AS datetime2)), N'LEGACY-HD-3'),
    (4, 4, NULL, 5, 3, N'CASH', 225000.00, 225000.00, 0.00, DATEADD(second,-2890800,CAST(@homNay AS datetime2)), N'LEGACY-HD-4'),
    (5, 5, NULL, 6, 3, N'CASH', 300000.00, 300000.00, 0.00, DATEADD(second,-2800800,CAST(@homNay AS datetime2)), N'LEGACY-HD-5'),
    (6, 6, NULL, 7, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-2710800,CAST(@homNay AS datetime2)), N'LEGACY-HD-6'),
    (7, 7, NULL, 8, 3, N'CASH', 130000.00, 130000.00, 0.00, DATEADD(second,-2620800,CAST(@homNay AS datetime2)), N'LEGACY-HD-7'),
    (8, 8, NULL, 9, 3, N'CASH', 50000.00, 50000.00, 0.00, DATEADD(second,-2530800,CAST(@homNay AS datetime2)), N'LEGACY-HD-8'),
    (9, 9, NULL, 10, 3, N'CASH', 480000.00, 480000.00, 0.00, DATEADD(second,-2469600,CAST(@homNay AS datetime2)), N'LEGACY-HD-9'),
    (10, 10, NULL, 11, 3, N'CASH', 125000.00, 125000.00, 0.00, DATEADD(second,-2379600,CAST(@homNay AS datetime2)), N'LEGACY-HD-10'),
    (11, 11, NULL, 12, 3, N'CASH', 50000.00, 50000.00, 0.00, DATEADD(second,-2289600,CAST(@homNay AS datetime2)), N'LEGACY-HD-11'),
    (12, 12, NULL, 13, 3, N'CASH', 15000.00, 15000.00, 0.00, DATEADD(second,-2199600,CAST(@homNay AS datetime2)), N'LEGACY-HD-12'),
    (13, 13, NULL, 14, 3, N'CASH', 25000.00, 25000.00, 0.00, DATEADD(second,-2109600,CAST(@homNay AS datetime2)), N'LEGACY-HD-13'),
    (14, 14, NULL, 15, 3, N'CASH', 70000.00, 70000.00, 0.00, DATEADD(second,-2019600,CAST(@homNay AS datetime2)), N'LEGACY-HD-14'),
    (15, 15, NULL, 16, 3, N'CASH', 75000.00, 75000.00, 0.00, DATEADD(second,-1929600,CAST(@homNay AS datetime2)), N'LEGACY-HD-15'),
    (16, 16, NULL, 17, 3, N'CASH', 185000.00, 185000.00, 0.00, DATEADD(second,-1839600,CAST(@homNay AS datetime2)), N'LEGACY-HD-16'),
    (17, 17, NULL, 18, 3, N'CASH', 50000.00, 50000.00, 0.00, DATEADD(second,-1778400,CAST(@homNay AS datetime2)), N'LEGACY-HD-17'),
    (18, 18, NULL, 30, 3, N'CASH', 60000.00, 60000.00, 0.00, DATEADD(second,-738000,CAST(@homNay AS datetime2)), N'LEGACY-HD-18'),
    (19, 19, NULL, 30, 3, N'CASH', 60000.00, 60000.00, 0.00, DATEADD(second,-734400,CAST(@homNay AS datetime2)), N'LEGACY-HD-19'),
    (20, 20, NULL, 21, 3, N'CASH', 125000.00, 125000.00, 0.00, DATEADD(second,-1508400,CAST(@homNay AS datetime2)), N'LEGACY-HD-20'),
    (21, 21, NULL, 22, 3, N'CASH', 150000.00, 150000.00, 0.00, DATEADD(second,-1418400,CAST(@homNay AS datetime2)), N'LEGACY-HD-21'),
    (22, 22, NULL, 23, 3, N'CASH', 175000.00, 175000.00, 0.00, DATEADD(second,-1328400,CAST(@homNay AS datetime2)), N'LEGACY-HD-22'),
    (23, 23, NULL, 24, 3, N'CASH', 200000.00, 200000.00, 0.00, DATEADD(second,-1238400,CAST(@homNay AS datetime2)), N'LEGACY-HD-23'),
    (24, 24, NULL, 25, 3, N'CASH', 225000.00, 225000.00, 0.00, DATEADD(second,-1148400,CAST(@homNay AS datetime2)), N'LEGACY-HD-24'),
    (25, 25, NULL, 26, 3, N'CASH', 250000.00, 250000.00, 0.00, DATEADD(second,-1087200,CAST(@homNay AS datetime2)), N'LEGACY-HD-25'),
    (26, 26, NULL, 23, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-1342800,CAST(@homNay AS datetime2)), N'LEGACY-HD-26'),
    (27, 27, NULL, 25, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-1166400,CAST(@homNay AS datetime2)), N'LEGACY-HD-27'),
    (28, 28, NULL, 27, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-990000,CAST(@homNay AS datetime2)), N'LEGACY-HD-28'),
    (29, 29, NULL, 29, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-813600,CAST(@homNay AS datetime2)), N'LEGACY-HD-29'),
    (30, 30, NULL, 31, 3, N'CASH', 5000.00, 5000.00, 0.00, DATEADD(second,-637200,CAST(@homNay AS datetime2)), N'LEGACY-HD-30'),
    (31, 31, NULL, 31, 3, N'CASH', 5000.00, 5000.00, 0.00, DATEADD(second,-633600,CAST(@homNay AS datetime2)), N'LEGACY-HD-31'),
    (32, 32, NULL, 1, 1, N'CASH', 180000.00, 180000.00, 0.00, DATEADD(second,-523756,CAST(@homNay AS datetime2)), N'LEGACY-HD-32'),
    (33, 33, NULL, 33, 1, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-484351,CAST(@homNay AS datetime2)), N'LEGACY-HD-33'),
    (34, 34, NULL, 33, 1, N'CASH', 531000.00, 531000.00, 0.00, DATEADD(second,-122380,CAST(@homNay AS datetime2)), N'LEGACY-HD-34'),
    (35, NULL, 2, 22, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-1429200,CAST(@homNay AS datetime2)), N'LEGACY-COC-2'),
    (36, NULL, 3, 24, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-1252800,CAST(@homNay AS datetime2)), N'LEGACY-COC-3'),
    (37, NULL, 4, 26, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-1076400,CAST(@homNay AS datetime2)), N'LEGACY-COC-4'),
    (38, NULL, 5, 28, 3, N'CASH', 90000.00, 90000.00, 0.00, DATEADD(second,-900000,CAST(@homNay AS datetime2)), N'LEGACY-COC-5'),
    (39, NULL, 6, 31, 3, N'CASH', 270000.00, 270000.00, 0.00, DATEADD(second,-662400,CAST(@homNay AS datetime2)), N'LEGACY-COC-6'),
    (40, NULL, 7, 31, 3, N'CASH', 375000.00, 375000.00, 0.00, DATEADD(second,-662400,CAST(@homNay AS datetime2)), N'LEGACY-COC-7'),
    (41, NULL, 8, 32, 3, N'CASH', 375000.00, 375000.00, 0.00, DATEADD(second,-576000,CAST(@homNay AS datetime2)), N'LEGACY-COC-8'),
    (42, NULL, 9, 32, 3, N'CASH', 375000.00, 375000.00, 0.00, DATEADD(second,-576000,CAST(@homNay AS datetime2)), N'LEGACY-COC-9');
SET IDENTITY_INSERT dbo.[khoan_thu] OFF;

-- lich_hen (17 dòng)
DBCC CHECKIDENT ('dbo.lich_hen', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[lich_hen] ON;
INSERT INTO dbo.[lich_hen] ([id], [ma_lich_hen], [khach_hang_id], [thu_cung_id], [loai_dich_vu], [bat_dau], [ket_thuc], [trang_thai], [ghi_chu], [nhan_vien_id], [dich_vu_id])
VALUES
    (1, N'LH-SAMPLE-001', 7, 6, N'SPA', DATEADD(second,-572400,CAST(@homNay AS datetime2)), DATEADD(second,-568800,CAST(@homNay AS datetime2)), N'SCHEDULED', N'Lịch hẹn từ dữ liệu mẫu', 17, 1),
    (2, N'LH000001', 34, 31, N'SPA', DATEADD(second,-1515600,CAST(@homNay AS datetime2)), DATEADD(second,-1508400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 5),
    (3, N'LH000002', 35, 32, N'SPA', DATEADD(second,-1425600,CAST(@homNay AS datetime2)), DATEADD(second,-1418400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 6),
    (4, N'LH000003', 16, 13, N'SPA', DATEADD(second,-1335600,CAST(@homNay AS datetime2)), DATEADD(second,-1328400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 7),
    (5, N'LH000004', 17, 14, N'SPA', DATEADD(second,-1245600,CAST(@homNay AS datetime2)), DATEADD(second,-1238400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 8),
    (6, N'LH000005', 18, 15, N'SPA', DATEADD(second,-1155600,CAST(@homNay AS datetime2)), DATEADD(second,-1148400,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 9),
    (7, N'LH000006', 19, 16, N'SPA', DATEADD(second,-1094400,CAST(@homNay AS datetime2)), DATEADD(second,-1087200,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 10),
    (8, N'LH000007', 16, 13, N'SPA', DATEADD(second,-572400,CAST(@homNay AS datetime2)), DATEADD(second,-568800,CAST(@homNay AS datetime2)), N'CHECKED_IN', N'', 17, 5),
    (9, N'LH000008', 17, 14, N'SPA', DATEADD(second,-568800,CAST(@homNay AS datetime2)), DATEADD(second,-565200,CAST(@homNay AS datetime2)), N'CHECKED_IN', N'', 17, 5),
    (10, N'LH000009', 18, 15, N'SPA', DATEADD(second,-565200,CAST(@homNay AS datetime2)), DATEADD(second,-561600,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 5),
    (11, N'LH000010', 19, 16, N'SPA', DATEADD(second,-561600,CAST(@homNay AS datetime2)), DATEADD(second,-558000,CAST(@homNay AS datetime2)), N'COMPLETED', N'', 17, 5),
    (12, N'LH000011', 30, 27, N'SPA', DATEADD(second,-486000,CAST(@homNay AS datetime2)), DATEADD(second,-482400,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (13, N'LH000012', 31, 28, N'SPA', DATEADD(second,-399600,CAST(@homNay AS datetime2)), DATEADD(second,-396000,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (14, N'LH000013', 32, 29, N'SPA', DATEADD(second,-313200,CAST(@homNay AS datetime2)), DATEADD(second,-309600,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (15, N'LH000014', 33, 30, N'SPA', DATEADD(second,-226800,CAST(@homNay AS datetime2)), DATEADD(second,-223200,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (16, N'LH000015', 34, 31, N'SPA', DATEADD(second,-140400,CAST(@homNay AS datetime2)), DATEADD(second,-136800,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5),
    (17, N'LH000016', 35, 32, N'SPA', DATEADD(second,-54000,CAST(@homNay AS datetime2)), DATEADD(second,-50400,CAST(@homNay AS datetime2)), N'SCHEDULED', N'lịch hẹn', 17, 5);
SET IDENTITY_INSERT dbo.[lich_hen] OFF;

-- nhat_ky_cham_soc (6 dòng)
DBCC CHECKIDENT ('dbo.nhat_ky_cham_soc', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[nhat_ky_cham_soc] ON;
INSERT INTO dbo.[nhat_ky_cham_soc] ([id], [hop_dong_id], [nhan_vien_id], [thoi_gian], [an_uong], [ve_sinh], [tinh_trang], [tep_dinh_kem])
VALUES
    (1, 2, 17, DATEADD(second,-1357200,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (2, 3, 17, DATEADD(second,-1180800,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (3, 4, 17, DATEADD(second,-1004400,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (4, 5, 17, DATEADD(second,-828000,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần', N'Đã vệ sinh khay và phòng', N'Nhật ký : sinh hoạt bình thường', NULL),
    (5, 6, 17, DATEADD(second,-524678,CAST(@homNay AS datetime2)), N'Đã cho ăn theo khẩu phần', N'Đã vệ sinh', N'Nhật ký chăm sóc', NULL),
    (6, 7, 17, DATEADD(second,-524678,CAST(@homNay AS datetime2)), N'Đã cho ăn theo khẩu phần', N'Đã vệ sinh', N'Nhật ký chăm sóc', NULL);
SET IDENTITY_INSERT dbo.[nhat_ky_cham_soc] OFF;

-- phieu_tiep_nhan_spa (10 dòng)
DBCC CHECKIDENT ('dbo.phieu_tiep_nhan_spa', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[phieu_tiep_nhan_spa] ON;
INSERT INTO dbo.[phieu_tiep_nhan_spa] ([id], [ma_phieu], [lich_hen_id], [thu_cung_id], [can_nang_thuc_te], [tong_tien], [trang_thai], [tao_luc], [nhan_vien_id], [ghi_chu], [anh], [hoa_don_id])
VALUES
    (1, N'SPA000001', 2, 31, 3.50, 125000.00, N'PAID', DATEADD(second,-1515600,CAST(@homNay AS datetime2)), 17, N'', NULL, 20),
    (2, N'SPA000002', 3, 32, 3.50, 150000.00, N'PAID', DATEADD(second,-1425600,CAST(@homNay AS datetime2)), 17, N'', NULL, 21),
    (3, N'SPA000003', 4, 13, 3.50, 175000.00, N'PAID', DATEADD(second,-1335600,CAST(@homNay AS datetime2)), 17, N'', NULL, 22),
    (4, N'SPA000004', 5, 14, 3.50, 200000.00, N'PAID', DATEADD(second,-1245600,CAST(@homNay AS datetime2)), 17, N'', NULL, 23),
    (5, N'SPA000005', 6, 15, 3.50, 225000.00, N'PAID', DATEADD(second,-1155600,CAST(@homNay AS datetime2)), 17, N'', NULL, 24),
    (6, N'SPA000006', 7, 16, 3.50, 250000.00, N'PAID', DATEADD(second,-1094400,CAST(@homNay AS datetime2)), 17, N'', NULL, 25),
    (7, N'SPA000007', 8, 13, 3.50, 125000.00, N'RECEIVED', DATEADD(second,-572400,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL),
    (8, N'SPA000008', 9, 14, 3.50, 125000.00, N'IN_PROGRESS', DATEADD(second,-568800,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL),
    (9, N'SPA000009', 10, 15, 3.50, 125000.00, N'COMPLETED', DATEADD(second,-565200,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL),
    (10, N'SPA000010', 11, 16, 3.50, 125000.00, N'COMPLETED', DATEADD(second,-561600,CAST(@homNay AS datetime2)), 17, N'có thể tiếp tục thao tác', NULL, NULL);
SET IDENTITY_INSERT dbo.[phieu_tiep_nhan_spa] OFF;

-- chi_tiet_phieu_spa (10 dòng)
DBCC CHECKIDENT ('dbo.chi_tiet_phieu_spa', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_spa] ON;
INSERT INTO dbo.[chi_tiet_phieu_spa] ([id], [phieu_spa_id], [goi_id], [mo_ta], [don_gia], [so_luong])
VALUES
    (1, 1, 5, N'Tắm cơ bản', 125000.00, 1),
    (2, 2, 6, N'Tắm dưỡng lông', 150000.00, 1),
    (3, 3, 7, N'Cắt tỉa tạo kiểu', 175000.00, 1),
    (4, 4, 8, N'Vệ sinh tai và móng', 200000.00, 1),
    (5, 5, 9, N'Chải lông và gỡ rối', 225000.00, 1),
    (6, 6, 10, N'Chăm sóc toàn diện', 250000.00, 1),
    (7, 7, 5, N'Tắm cơ bản', 125000.00, 1),
    (8, 8, 5, N'Tắm cơ bản', 125000.00, 1),
    (9, 9, 5, N'Tắm cơ bản', 125000.00, 1),
    (10, 10, 5, N'Tắm cơ bản', 125000.00, 1);
SET IDENTITY_INSERT dbo.[chi_tiet_phieu_spa] OFF;

-- phieu_cham_soc (0 dòng)
DBCC CHECKIDENT ('dbo.phieu_cham_soc', RESEED, 1) WITH NO_INFOMSGS;

-- tiem_phong (50 dòng)
DBCC CHECKIDENT ('dbo.tiem_phong', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[tiem_phong] ON;
INSERT INTO dbo.[tiem_phong] ([id], [thu_cung_id], [ten_vaccine], [ngay_tiem], [ngay_het_han], [ghi_chu])
VALUES
    (1, 11, N'Mũi 1', DATEADD(day,-57,@homNay), NULL, N'tiêm phòng'),
    (2, 11, N'Mũi 2', DATEADD(day,-27,@homNay), NULL, N'tiêm phòng'),
    (3, 13, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (4, 13, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (5, 14, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (6, 14, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (7, 15, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (8, 15, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (9, 16, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (10, 16, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (11, 17, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (12, 17, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (13, 18, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (14, 18, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (15, 19, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (16, 19, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (17, 20, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (18, 20, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (19, 21, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (20, 21, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (21, 22, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (22, 22, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (23, 23, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (24, 23, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (25, 24, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (26, 24, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (27, 25, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (28, 25, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (29, 26, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (30, 26, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (31, 27, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (32, 27, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (33, 28, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (34, 28, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (35, 29, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (36, 29, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (37, 30, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (38, 30, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (39, 31, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (40, 31, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (41, 32, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (42, 32, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (43, 33, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (44, 33, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (45, 34, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (46, 34, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (47, 35, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (48, 35, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (49, 36, N'Mũi cơ bản 1 ()', DATEADD(day,-107,@homNay), DATEADD(day,258,@homNay), N'để kiểm tra điều kiện tiếp nhận'),
    (50, 36, N'Mũi cơ bản 2 ()', DATEADD(day,-72,@homNay), DATEADD(day,293,@homNay), N'để kiểm tra điều kiện tiếp nhận');
SET IDENTITY_INSERT dbo.[tiem_phong] OFF;

-- vai_tro_quyen (48 dòng)
INSERT INTO dbo.[vai_tro_quyen] ([vai_tro], [ma_quyen])
VALUES
    (N'SALES_STAFF', N'CASH_SHIFT_MANAGE'),
    (N'SALES_STAFF', N'CUSTOMER_MANAGE'),
    (N'SALES_STAFF', N'DASHBOARD_VIEW'),
    (N'SALES_STAFF', N'PAYMENT_PROCESS'),
    (N'SALES_STAFF', N'PET_MANAGE'),
    (N'SALES_STAFF', N'POS_SELL'),
    (N'SALES_STAFF', N'REPORT_VIEW_OWN_SHIFT'),
    (N'SALES_STAFF', N'SCHEDULE_VIEW'),
    (N'SPA_HOTEL_STAFF', N'CASH_SHIFT_MANAGE'),
    (N'SPA_HOTEL_STAFF', N'CUSTOMER_MANAGE'),
    (N'SPA_HOTEL_STAFF', N'DASHBOARD_VIEW'),
    (N'SPA_HOTEL_STAFF', N'HOTEL_MANAGE'),
    (N'SPA_HOTEL_STAFF', N'PAYMENT_PROCESS'),
    (N'SPA_HOTEL_STAFF', N'PET_MANAGE'),
    (N'SPA_HOTEL_STAFF', N'POS_SELL'),
    (N'SPA_HOTEL_STAFF', N'SCHEDULE_VIEW'),
    (N'SPA_HOTEL_STAFF', N'SPA_MANAGE'),
    (N'STORE_MANAGER', N'ACCOUNT_MANAGE'),
    (N'STORE_MANAGER', N'APPROVAL_PROCESS'),
    (N'STORE_MANAGER', N'AUDIT_VIEW'),
    (N'STORE_MANAGER', N'CASH_SHIFT_MANAGE'),
    (N'STORE_MANAGER', N'CUSTOMER_MANAGE'),
    (N'STORE_MANAGER', N'DASHBOARD_VIEW'),
    (N'STORE_MANAGER', N'HOTEL_MANAGE'),
    (N'STORE_MANAGER', N'INVENTORY_COUNT'),
    (N'STORE_MANAGER', N'INVENTORY_ISSUE'),
    (N'STORE_MANAGER', N'INVENTORY_RECEIVE'),
    (N'STORE_MANAGER', N'PAYMENT_PROCESS'),
    (N'STORE_MANAGER', N'PET_MANAGE'),
    (N'STORE_MANAGER', N'POS_SELL'),
    (N'STORE_MANAGER', N'PRICE_APPROVE'),
    (N'STORE_MANAGER', N'PRICE_PROPOSE'),
    (N'STORE_MANAGER', N'PRODUCT_MANAGE'),
    (N'STORE_MANAGER', N'PROMOTION_MANAGE'),
    (N'STORE_MANAGER', N'REPORT_VIEW_ALL'),
    (N'STORE_MANAGER', N'REPORT_VIEW_OWN_SHIFT'),
    (N'STORE_MANAGER', N'SCHEDULE_MANAGE'),
    (N'STORE_MANAGER', N'SCHEDULE_VIEW'),
    (N'STORE_MANAGER', N'SPA_MANAGE'),
    (N'STORE_MANAGER', N'SUPPLIER_MANAGE'),
    (N'WAREHOUSE_STAFF', N'DASHBOARD_VIEW'),
    (N'WAREHOUSE_STAFF', N'INVENTORY_COUNT'),
    (N'WAREHOUSE_STAFF', N'INVENTORY_ISSUE'),
    (N'WAREHOUSE_STAFF', N'INVENTORY_RECEIVE'),
    (N'WAREHOUSE_STAFF', N'PRICE_PROPOSE'),
    (N'WAREHOUSE_STAFF', N'PRODUCT_MANAGE'),
    (N'WAREHOUSE_STAFF', N'SCHEDULE_VIEW'),
    (N'WAREHOUSE_STAFF', N'SUPPLIER_MANAGE');

-- voucher (2 dòng)
DBCC CHECKIDENT ('dbo.voucher', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[voucher] ON;
INSERT INTO dbo.[voucher] ([id], [ma_voucher], [khach_hang_id], [menh_gia], [hieu_luc_den], [su_dung_luc], [chi_ap_dung_spa], [so_luong], [da_dung], [don_toi_thieu], [trang_thai], [khoa_doi_diem])
VALUES
    (1, N'VC-WELCOME50', NULL, 50000.00, DATEADD(second,7220638,CAST(@homNay AS datetime2)), NULL, 0, 100, 0, 300000.00, N'ACTIVE', NULL),
    (2, N'VC000001', 16, 150000.00, DATEADD(second,4659322,CAST(@homNay AS datetime2)), NULL, 1, 1, 0, 0.00, N'ACTIVE', N'REAL_DATA_V1_REDEEM');
SET IDENTITY_INSERT dbo.[voucher] OFF;

-- yeu_cau_phe_duyet (4 dòng)
DBCC CHECKIDENT ('dbo.yeu_cau_phe_duyet', RESEED, 0) WITH NO_INFOMSGS;
SET IDENTITY_INSERT dbo.[yeu_cau_phe_duyet] ON;
INSERT INTO dbo.[yeu_cau_phe_duyet] ([id], [ma_yeu_cau], [loai], [nguoi_gui_id], [loai_doi_tuong], [ma_doi_tuong], [ly_do], [trang_thai], [nguoi_xu_ly_id], [phan_hoi], [gui_luc], [xu_ly_luc], [noi_dung_truoc], [noi_dung_sau], [payload])
VALUES
    (1, N'YC000001', N'STOCKTAKE', 4, N'STOCKTAKE', N'1', N'chờ duyệt chênh lệch', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'Tồn sổ 3', N'Tồn thực 2', N''),
    (2, N'YC000002', N'PRICE', 4, N'PRICE_CHANGE', N'1', N'thử duyệt hoặc từ chối', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'30000', N'35000', N''),
    (3, N'YC000003', N'PRICE', 4, N'PRICE_CHANGE', N'2', N'thử duyệt hoặc từ chối', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'70000', N'75000', N''),
    (4, N'YC000004', N'LEAVE', 5, N'LEAVE', N'17', N'xin nghỉ phép', N'PENDING', NULL, NULL, DATEADD(second,-549878,CAST(@homNay AS datetime2)), NULL, N'Đang phân công', N'Nghỉ một ngày', N'2026-10-22');
SET IDENTITY_INSERT dbo.[yeu_cau_phe_duyet] OFF;

COMMIT;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT>0 ROLLBACK;
    THROW;
END CATCH;
