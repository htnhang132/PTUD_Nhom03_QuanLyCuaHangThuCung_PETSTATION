USE [PetStationDemo1];
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET ANSI_PADDING ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET NUMERIC_ROUNDABORT OFF;
GO

DECLARE @homNay date = CAST(GETDATE() AS date);

BEGIN TRY
BEGIN TRANSACTION;

IF OBJECT_ID('dbo.FK_chi_tiet_hoa_don_san_pham', 'F') IS NOT NULL
    ALTER TABLE dbo.[chi_tiet_hoa_don] DROP CONSTRAINT [FK_chi_tiet_hoa_don_san_pham];

ALTER TABLE dbo.[bien_ban_kiem_ke] ALTER COLUMN [ky_kiem_ke] NVARCHAR(150) NOT NULL;
ALTER TABLE dbo.[nhat_ky_he_thong] ALTER COLUMN [loai_doi_tuong] NVARCHAR(60) NULL;

-- Xóa dữ liệu cũ theo đúng thứ tự topo ngược (con xóa trước, cha xóa sau)
DELETE FROM dbo.[nhat_ky_cham_soc];
DELETE FROM dbo.[lich_su_thu_coc_hotel];
DELETE FROM dbo.[chi_tiet_doi_tra];
DELETE FROM dbo.[phieu_tiep_nhan_spa];
DELETE FROM dbo.[hop_dong_luu_tru];
DELETE FROM dbo.[phieu_doi_tra];
DELETE FROM dbo.[chi_tiet_hoa_don];
DELETE FROM dbo.[chi_tiet_phieu_xuat];
DELETE FROM dbo.[chi_tiet_phieu_nhap];
DELETE FROM dbo.[hoa_don];
DELETE FROM dbo.[chi_tiet_kiem_ke];
DELETE FROM dbo.[phieu_spa_treo];
DELETE FROM dbo.[yeu_cau_phe_duyet];
DELETE FROM dbo.[phieu_xuat_kho];
DELETE FROM dbo.[phieu_nhap_kho];
DELETE FROM dbo.[phien_lam_viec];
DELETE FROM dbo.[nhat_ky_he_thong];
DELETE FROM dbo.[cai_dat];
DELETE FROM dbo.[bien_ban_kiem_ke];
DELETE FROM dbo.[lich_hen];
DELETE FROM dbo.[lo_hang];
DELETE FROM dbo.[tai_khoan];
DELETE FROM dbo.[lich_lam_viec];
DELETE FROM dbo.[voucher];
DELETE FROM dbo.[thu_cung];
DELETE FROM dbo.[san_pham];
DELETE FROM dbo.[phong_hotel];
DELETE FROM dbo.[nhan_vien];
DELETE FROM dbo.[nha_cung_cap];
DELETE FROM dbo.[khach_hang];
DELETE FROM dbo.[goi_dich_vu_spa];
DELETE FROM dbo.[chuong_trinh_khuyen_mai];
DELETE FROM dbo.[ca_lam_viec];

-- 1. CA LÀM VIỆC
INSERT INTO dbo.[ca_lam_viec] ([ma_ca], [ten_ca], [gio_bat_dau], [gio_ket_thuc], [so_nhan_su_toi_thieu], [he_so_luong])
VALUES
    (N'CA01', N'Ca sáng', CONVERT(time,'07:30:00'), CONVERT(time,'14:30:00'), 2, 1.00),
    (N'CA02', N'Ca chiều', CONVERT(time,'14:30:00'), CONVERT(time,'21:30:00'), 2, 1.00),
    (N'CA03', N'Ca tối', CONVERT(time,'18:00:00'), CONVERT(time,'22:00:00'), 1, 1.20);

-- 2. CHƯƠNG TRÌNH KHUYẾN MÃI
INSERT INTO dbo.[chuong_trinh_khuyen_mai] ([ma_chuong_trinh], [ten], [loai_giam], [gia_tri], [bat_dau], [ket_thuc], [nguon], [trang_thai], [don_toi_thieu])
VALUES
    (N'KM000001', N'Giảm 10% thức ăn hạt dinh dưỡng', N'PHAN_TRAM', 10.00, DATEADD(day,-30,CAST(@homNay AS datetime2)), DATEADD(day,30,CAST(@homNay AS datetime2)), N'PRODUCT', N'HOAT_DONG', 100000.00),
    (N'KM000002', N'Ưu đãi dịch vụ Spa mùa hè', N'SO_TIEN', 50000.00, DATEADD(day,-15,CAST(@homNay AS datetime2)), DATEADD(day,45,CAST(@homNay AS datetime2)), N'SERVICE', N'HOAT_DONG', 200000.00),
    (N'KM000003', N'Giảm giá phụ kiện cuối tuần', N'PHAN_TRAM', 15.00, DATEADD(day,-10,CAST(@homNay AS datetime2)), DATEADD(day,20,CAST(@homNay AS datetime2)), N'PRODUCT', N'HOAT_DONG', 150000.00),
    (N'KM000004', N'Tri ân khách hàng thân thiết', N'SO_TIEN', 30000.00, DATEADD(day,-60,CAST(@homNay AS datetime2)), DATEADD(day,60,CAST(@homNay AS datetime2)), N'ALL', N'HOAT_DONG', 250000.00);

-- 3. GÓI DỊCH VỤ SPA
INSERT INTO dbo.[goi_dich_vu_spa] ([ma_goi], [ten_goi], [mo_ta], [thoi_gian_du_kien], [chung_loai_ap_dung], [don_gia_goc], [bang_gia], [trang_thai])
VALUES
    (N'SPA00000001', N'Tắm và sấy khô cơ bản', N'Tắm gội sạch sẽ, vắt tuyến hôi, sấy phồng và dưỡng lông', 45, N'Cả hai', 150000.00, N'DOG|< 5kg|0.00|5.00|150000.00;DOG|5 - 15kg|5.00|15.00|200000.00;DOG|>= 15kg|15.00|NULL|280000.00;CAT|< 5kg|0.00|5.00|130000.00;CAT|>= 5kg|5.00|NULL|180000.00', N'HOAT_DONG'),
    (N'SPA00000002', N'Cắt tỉa tạo kiểu dáng', N'Cắt tỉa tạo kiểu theo phong cách Hàn Quốc/Nhật Bản', 75, N'Cả hai', 230000.00, N'DOG|< 5kg|0.00|5.00|230000.00;DOG|5 - 15kg|5.00|15.00|300000.00;DOG|>= 15kg|15.00|NULL|380000.00;CAT|< 5kg|0.00|5.00|210000.00;CAT|>= 5kg|5.00|NULL|270000.00', N'HOAT_DONG'),
    (N'SPA00000003', N'Vệ sinh tai và cắt mài móng', N'Vệ sinh tai khử khuẩn chuyên dụng, cắt và dũa móng an toàn', 30, N'Cả hai', 80000.00, N'DOG|< 5kg|0.00|5.00|80000.00;DOG|5 - 15kg|5.00|15.00|100000.00;DOG|>= 15kg|15.00|NULL|130000.00;CAT|< 5kg|0.00|5.00|70000.00;CAT|>= 5kg|5.00|NULL|95000.00', N'HOAT_DONG'),
    (N'SPA00000004', N'Combo Spa toàn diện VIP', N'Tắm thảo mộc dưỡng lông, sục bồn jacuzzi, cắt tỉa toàn thân', 120, N'Cả hai', 350000.00, N'DOG|< 5kg|0.00|5.00|350000.00;DOG|5 - 15kg|5.00|15.00|450000.00;DOG|>= 15kg|15.00|NULL|600000.00;CAT|< 5kg|0.00|5.00|320000.00;CAT|>= 5kg|5.00|NULL|420000.00', N'HOAT_DONG'),
    (N'SPA00000005', N'Tắm trị liệu nấm và ve rận', N'Sử dụng dầu tắm y tế trị ký sinh trùng, khử khuẩn da lông', 60, N'Cả hai', 180000.00, N'DOG|< 5kg|0.00|5.00|180000.00;DOG|5 - 15kg|5.00|15.00|240000.00;DOG|>= 15kg|15.00|NULL|320000.00;CAT|< 5kg|0.00|5.00|160000.00;CAT|>= 5kg|5.00|NULL|220000.00', N'HOAT_DONG'),
    (N'SPA00000006', N'Gỡ rối lông chuyên sâu và ủ dưỡng collagen', N'Gỡ rối lông vón cục, ủ kem collagen phục hồi lông xơ rối', 60, N'Cả hai', 200000.00, N'DOG|< 5kg|0.00|5.00|200000.00;DOG|5 - 15kg|5.00|15.00|260000.00;DOG|>= 15kg|15.00|NULL|350000.00;CAT|< 5kg|0.00|5.00|180000.00;CAT|>= 5kg|5.00|NULL|240000.00', N'HOAT_DONG'),
    (N'SPA00000007', N'Cạo lông vệ sinh và tạo hình thẩm mỹ', N'Cạo lông bàn chân, vùng bụng, vệ sinh tuyến hôi và tỉa mặt', 50, N'Cả hai', 160000.00, N'DOG|< 5kg|0.00|5.00|160000.00;DOG|5 - 15kg|5.00|15.00|210000.00;DOG|>= 15kg|15.00|NULL|270000.00;CAT|< 5kg|0.00|5.00|150000.00;CAT|>= 5kg|5.00|NULL|195000.00', N'HOAT_DONG'),
    (N'SPA00000008', N'Tắm sục khoáng Onsen thư giãn', N'Ngâm bồn sục khoáng ấm Onsen Nhật Bản giúp tuần hoàn máu và sạch sâu', 60, N'Cả hai', 260000.00, N'DOG|< 5kg|0.00|5.00|260000.00;DOG|5 - 15kg|5.00|15.00|330000.00;DOG|>= 15kg|15.00|NULL|430000.00;CAT|< 5kg|0.00|5.00|240000.00;CAT|>= 5kg|5.00|NULL|310000.00', N'HOAT_DONG');

-- 4. KHÁCH HÀNG
INSERT INTO dbo.[khach_hang] ([ma_khach_hang], [ho_ten], [so_dien_thoai], [dia_chi], [ngay_sinh], [diem_tich_luy], [hang_thanh_vien], [dang_hoat_dong], [email], [ghi_chu], [ngay_tao], [lich_su_diem])
VALUES
    (N'KH000001', N'Nguyễn Văn An', N'0901234567', N'12 Nguyễn Trãi, Hà Nội', CONVERT(date,'1990-05-12',23), 1268, N'Vàng', 1, N'an.nguyen@email.com', N'Khách quen nuôi chó Poodle, thích mua hạt Royal Canin', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1268|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1218|1218|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000002', N'Trần Thị Bình', N'0912345678', N'45 Lê Lợi, TP.HCM', CONVERT(date,'1992-08-20',23), 4860, N'Kim Cương', 1, N'binh.tran@email.com', N'Chăm sóc mèo Anh lông ngắn, thường đặt lịch Spa định kỳ', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|4860|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|4810|4810|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000003', N'Lê Hoàng Cường', N'0923456789', N'8 Hùng Vương, Đà Nẵng', CONVERT(date,'1988-11-15',23), 3210, N'Bạch Kim', 1, N'cuong.le@email.com', N'Khách gửi khách sạn thú cưng mỗi khi đi công tác', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|3210|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|3160|3160|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000004', N'Phạm Minh Đức', N'0934567890', N'21 Lạch Tray, Hải Phòng', CONVERT(date,'1995-03-30',23), 840, N'Bạc', 1, N'duc.pham@email.com', N'Nuôi 2 bé cún Corgi rất hiếu động', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|840|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|790|790|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000005', N'Hoàng Ngọc Ánh', N'0945678901', N'34 Trần Hưng Đạo, Cần Thơ', CONVERT(date,'1997-07-25',23), 520, N'Đồng', 1, N'anh.hoang@email.com', N'Thích dùng các loại sữa tắm thảo mộc dưỡng lông', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|520|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|470|470|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000006', N'Vũ Đình Trọng', N'0956789012', N'67 Quang Trung, Nha Trang', CONVERT(date,'1991-12-08',23), 2150, N'Bạch Kim', 1, N'trong.vu@email.com', N'Thường xuyên mua cát vệ sinh hữu cơ Cats Best', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|2150|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|2100|2100|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000007', N'Đặng Thu Thảo', N'0967890123', N'89 Lý Thường Kiệt, Huế', CONVERT(date,'1996-09-14',23), 1780, N'Vàng', 1, N'thao.dang@email.com', N'Nuôi mèo Ba Tư lông dài, cần cắt tỉa lông thường xuyên', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1780|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1730|1730|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000008', N'Bùi Quốc Huy', N'0978901234', N'15 Lê Duẩn, Buôn Ma Thuột', CONVERT(date,'1989-02-18',23), 930, N'Bạc', 1, N'huy.bui@email.com', N'Khách hàng mua thức ăn cho đàn chó Husky', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|930|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|880|880|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000009', N'Đỗ Thị Mai', N'0989012345', N'52 Nguyễn Huệ, Quy Nhơn', CONVERT(date,'1994-06-22',23), 340, N'Đồng', 1, N'mai.do@email.com', N'Khách hàng mới nhận nuôi mèo con', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|340|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|290|290|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000010', N'Ngô Thành Nam', N'0990123456', N'73 Phan Đình Phùng, Đà Lạt', CONVERT(date,'1993-10-05',23), 2890, N'Bạch Kim', 1, N'nam.ngo@email.com', N'Ưa chuộng phụ kiện đồ chơi thông minh cho cún', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|2890|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|2840|2840|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000011', N'Dương Thị Hồng', N'0902345678', N'101 Điện Biên Phủ, Vũng Tàu', CONVERT(date,'1998-04-17',23), 1450, N'Vàng', 1, N'hong.duong@email.com', N'Khách hàng đặt gói Spa cao cấp và lưu trú hotel', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1450|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1400|1400|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000012', N'Lý Gia Bảo', N'0913456789', N'18 Cách Mạng Tháng 8, Biên Hòa', CONVERT(date,'1987-01-29',23), 5600, N'Kim Cương', 1, N'bao.ly@email.com', N'Khách VIP có 3 bé thú cưng tham gia thi thố', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|5600|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|5550|5550|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000013', N'Tạ Thanh Tùng', N'0924567890', N'29 Nguyễn Chí Thanh, Pleiku', CONVERT(date,'1996-08-11',23), 620, N'Bạc', 1, N'tung.ta@email.com', N'Ưu tiên dịch vụ đưa đón thú cưng tận nơi', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|620|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|570|570|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000014', N'Mai Phương Thúy', N'0935678901', N'44 Trần Phú, Thanh Hóa', CONVERT(date,'1992-11-23',23), 2400, N'Bạch Kim', 1, N'thuy.mai@email.com', N'Luôn thanh toán chuyển khoản, đánh giá dịch vụ 5 sao', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|2400|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|2350|2350|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000015', N'Trịnh Hoài An', N'0946789012', N'58 Lê Hồng Phong, Nam Định', CONVERT(date,'1999-03-08',23), 410, N'Đồng', 1, N'an.trinh@email.com', N'Khách hàng hay mua bánh thưởng và pate lon', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|410|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|360|360|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000016', N'Phan Văn Hậu', N'0957890123', N'92 Bà Triệu, Phan Thiết', CONVERT(date,'1995-12-19',23), 1100, N'Vàng', 1, N'hau.phan@email.com', N'Bé cún Golden thích tắm bồn sục jacuzzi', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1100|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1050|1050|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000017', N'Lê Thị Tuyết', N'0968901234', N'31 Hùng Vương, Thái Nguyên', CONVERT(date,'1991-05-04',23), 890, N'Bạc', 1, N'tuyet.le@email.com', N'Thường xuyên mua thực phẩm bổ sung canxi và lông', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|890|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|840|840|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000018', N'Cao Tuấn Kiệt', N'0979012345', N'66 Hai Bà Trưng, Rạch Giá', CONVERT(date,'1994-09-27',23), 3100, N'Bạch Kim', 1, N'kiet.cao@email.com', N'Khách hàng thân thiết gửi hotel dịp Tết', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|3100|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|3050|3050|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000019', N'Đinh Thúy Nga', N'0980123456', N'77 Nguyễn Thị Minh Khai, Long Xuyên', CONVERT(date,'1997-02-14',23), 1650, N'Vàng', 1, N'nga.dinh@email.com', N'Bé mèo cưng rất kén ăn, chỉ ăn pate Whiskas', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1650|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1600|1600|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000020', N'Trần Quốc Toản', N'0991234567', N'14 Trần Hưng Đạo, Cà Mau', CONVERT(date,'1990-07-31',23), 720, N'Bạc', 1, N'toan.tran@email.com', N'Thường mua chuồng vận chuyển và nệm nằm', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|720|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|670|670|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000021', N'Huỳnh Mẫn Nhi', N'0903456789', N'88 Nguyễn Tri Phương, TP.HCM', CONVERT(date,'1998-10-10',23), 2200, N'Bạch Kim', 1, N'nhi.huynh@email.com', N'Chăm sóc bé cún Corgi chân ngắn rất đáng yêu', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|2200|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|2150|2150|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000022', N'Lê Khắc Tiệp', N'0914567890', N'19 Tô Hiến Thành, Hà Nội', CONVERT(date,'1993-01-25',23), 1350, N'Vàng', 1, N'tiep.le@email.com', N'Đăng ký dịch vụ tắm sấy và vệ sinh tai định kỳ', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1350|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1300|1300|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000023', N'Nguyễn Thùy Linh', N'0925678901', N'27 Chu Văn An, TP.HCM', CONVERT(date,'1996-06-18',23), 4950, N'Kim Cương', 1, N'linh.nguyen@email.com', N'Khách VIP thường xuyên đổi điểm tích lũy lấy voucher', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|4950|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|4900|4900|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000024', N'Phạm Văn Đồng', N'0936789012', N'63 Nguyễn Trọng Tuyển, TP.HCM', CONVERT(date,'1988-08-05',23), 980, N'Bạc', 1, N'dong.pham@email.com', N'Khách hàng thích nhận thông báo khuyến mãi qua Zalo', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|980|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|930|930|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000025', N'Võ Hoàng Yến', N'0947890123', N'12 Hoàng Văn Thụ, TP.HCM', CONVERT(date,'1995-11-29',23), 3700, N'Bạch Kim', 1, N'yen.vo@email.com', N'Nuôi 2 bé mèo Ragdoll mắt xanh xinh đẹp', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|3700|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|3650|3650|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000026', N'Đoàn Văn Hậu', N'0958901234', N'35 Trường Chinh, Hà Nội', CONVERT(date,'1999-04-19',23), 830, N'Bạc', 1, N'hau.doan@email.com', N'Cún cưng đang tập thói quen đi vệ sinh đúng chỗ', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|830|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|780|780|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000027', N'Nguyễn Kiều Oanh', N'0969012345', N'48 Phan Xích Long, TP.HCM', CONVERT(date,'1992-09-02',23), 1520, N'Vàng', 1, N'oanh.nguyen@email.com', N'Khách hàng rất cẩn thận, dặn kỹ nhân viên khi tắm', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1520|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1470|1470|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000028', N'Trương Vĩnh Ký', N'0970123456', N'102 Lê Lai, TP.HCM', CONVERT(date,'1990-12-12',23), 2800, N'Bạch Kim', 1, N'ky.truong@email.com', N'Thường xuyên mua nệm ngủ và quần áo thú cưng', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|2800|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|2750|2750|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000029', N'Lâm Mỹ Lệ', N'0981234567', N'55 Đinh Tiên Hoàng, TP.HCM', CONVERT(date,'1997-03-21',23), 640, N'Bạc', 1, N'le.lam@email.com', N'Khách hàng trẻ nhiệt tình, hay chia sẻ ảnh cún', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|640|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|590|590|Số dư đầu kỳ|2026-10-01 08:00:00'),
    (N'KH000030', N'Tô Hoài An', N'0992345678', N'72 Nguyễn Bỉnh Khiêm, TP.HCM', CONVERT(date,'1994-07-07',23), 1950, N'Vàng', 1, N'an.to@email.com', N'Nuôi bé mèo Munchkin chân ngắn nhanh nhẹn', DATEADD(second,-555363,CAST(@homNay AS datetime2)), N'1|50|1950|Tích điểm giao dịch mẫu|2026-10-15 08:00:00;2|1900|1900|Số dư đầu kỳ|2026-10-01 08:00:00');

-- 5. NHÀ CUNG CẤP
INSERT INTO dbo.[nha_cung_cap] ([ma_nha_cung_cap], [ten], [so_dien_thoai], [dia_chi], [nguoi_dai_dien], [nhom_mat_hang], [ghi_chu], [trang_thai], [ma_so_thue], [dieu_khoan])
VALUES
    (N'NCC0001', N'Công ty TNHH Royal Canin Việt Nam', N'0243825678', N'Tầng 8, Tòa nhà Sun City, 13 Hai Bà Trưng, Hà Nội', N'Nguyễn Minh Tâm', N'Thức ăn thú cưng', N'Nhà phân phối chính hãng thức ăn dinh dưỡng thú cưng', N'DANG_HOP_TAC', N'0101234567', N'Công nợ 30 ngày, chiết khấu 5% khi đạt doanh số'),
    (N'NCC0002', N'Công ty TNHH Mars Petcare Việt Nam', N'0283826789', N'Lầu 12, Tòa nhà M-Building, Quận 7, TP.HCM', N'Trần Thành Long', N'Thức ăn & Dinh dưỡng', N'Cung cấp thức ăn Whiskas, Pedigree, Temptations', N'DANG_HOP_TAC', N'0309876543', N'Công nợ 15 ngày, giao hàng miễn phí nội thành'),
    (N'NCC0003', N'Công ty Cổ phần Orijen Import', N'0243827890', N'Số 25 Lý Thường Kiệt, Hoàn Kiếm, Hà Nội', N'Lê Đức Anh', N'Thức ăn cao cấp', N'Nhập khẩu thức ăn không hạt Orijen và Acana từ Canada', N'DANG_HOP_TAC', N'0105551234', N'Thanh toán ngay khi giao hàng, cam kết hàng chính ngạch'),
    (N'NCC0004', N'Công ty TNHH PetShop Thú Y Toàn Quốc', N'0911222333', N'128 Nguyễn Đình Chiểu, Quận 3, TP.HCM', N'Phạm Quang Vinh', N'Thuốc & Y tế', N'Chuyên vắc-xin, thuốc xịt ve rận, thuốc trị nấm', N'DANG_HOP_TAC', N'0312223344', N'Công nợ 30 ngày, bảo quản thuốc chuẩn nhiệt độ GSP'),
    (N'NCC0005', N'Công ty Thực phẩm thú cưng An Phú', N'02838990001', N'Khu công nghiệp Tân Bình, Tân Phú, TP.HCM', N'Nguyễn Thanh Hà', N'Pate & Thức ăn ướt', N'Sản xuất pate thịt tươi đóng gói cao cấp cho chó mèo', N'DANG_HOP_TAC', N'0314567890', N'Công nợ 30 ngày, đổi trả hàng cận date trước 3 tháng'),
    (N'NCC0006', N'Nhà phân phối phụ kiện Minh Khang', N'02838990002', N'45/2 Chu Văn An, Bình Thạnh, TP.HCM', N'Trần Quốc Huy', N'Phụ kiện & Đồ chơi', N'Cung cấp chuồng, nệm, bát ăn, đồ chơi dây thừng', N'DANG_HOP_TAC', N'0315678901', N'Công nợ 45 ngày, hỗ trợ kệ trưng bày tại cửa hàng'),
    (N'NCC0007', N'Công ty Phụ kiện thú cưng Hưng Thịnh', N'02838990003', N'88 Lê Trọng Tấn, Tân Phú, TP.HCM', N'Lê Bảo Trâm', N'Dây dắt & Vòng cổ', N'Chuyên sản xuất vòng cổ da bò, áo quần, nơ thú cưng', N'DANG_HOP_TAC', N'0316789012', N'Công nợ 30 ngày, gia công mẫu theo yêu cầu'),
    (N'NCC0008', N'Nhà phân phối cát Happy Pet', N'02838990004', N'124 Quốc lộ 13, Hiệp Bình Phước, Thủ Đức, TP.HCM', N'Phạm Minh Tuấn', N'Cát vệ sinh & Khay', N'Nhập khẩu cát đất sét, cát đậu nành, than hoạt tính', N'DANG_HOP_TAC', N'0317890123', N'Giao hàng theo pallet, công nợ 30 ngày'),
    (N'NCC0009', N'Công ty Chăm sóc thú cưng Đại Việt', N'02838990005', N'215 Nam Kỳ Khởi Nghĩa, Quận 3, TP.HCM', N'Võ Thu Ngân', N'Mỹ phẩm Spa', N'Phân phối sữa tắm Joyce & Dolls, BBN, dầu xả mềm mượt', N'DANG_HOP_TAC', N'0318901234', N'Hỗ trợ đào tạo kỹ thuật viên Spa chuyên nghiệp'),
    (N'NCC0010', N'Nhà phân phối thiết bị Thiên Phúc', N'02838990006', N'96 Nguyễn Thị Thập, Quận 7, TP.HCM', N'Đỗ Hoàng Long', N'Thiết bị Grooming', N'Cung cấp máy sấy lông công suất lớn, bàn nâng thủy lực', N'DANG_HOP_TAC', N'0319012345', N'Bảo hành thiết bị 24 tháng, bảo trì định kỳ 6 tháng');

-- 6. NHÂN VIÊN
INSERT INTO dbo.[nhan_vien] ([ma_nhan_vien], [ho_ten], [ngay_sinh], [so_dien_thoai], [dia_chi], [chuc_vu], [ngay_vao_lam], [anh_dai_dien], [dang_lam_viec], [cccd])
VALUES
    (N'NV0001', N'Huỳnh Tấn Nhàng', CONVERT(date,'2002-05-15',23), N'0901234501', N'12 Nguyễn Văn Bảo, Phường 4, Quận Gò Vấp, TP.HCM', N'Cửa hàng trưởng', CONVERT(date,'2022-01-15',23), N'images/staff/nhanght.png', 1, N'079202001122'),
    (N'NV0002', N'Trần Quang Vinh', CONVERT(date,'2002-08-20',23), N'0901234502', N'45 Lê Đức Thọ, Phường 7, Quận Gò Vấp, TP.HCM', N'Quản lý cửa hàng', CONVERT(date,'2022-02-01',23), N'images/staff/vinhtq.png', 1, N'079202002233'),
    (N'NV0003', N'Nguyễn Gia Vỉ', CONVERT(date,'2002-11-10',23), N'0901234503', N'78 Quang Trung, Phường 10, Quận Gò Vấp, TP.HCM', N'Nhân viên kho', CONVERT(date,'2022-03-15',23), N'images/staff/ving.png', 1, N'079202003344'),
    (N'NV0004', N'Phan Trần Thảo Vy', CONVERT(date,'2002-03-25',23), N'0901234504', N'102 Phan Văn Trị, Phường 12, Quận Bình Thạnh, TP.HCM', N'Nhân viên bán hàng', CONVERT(date,'2022-04-01',23), N'images/staff/vyptt.png', 1, N'079202004455'),
    (N'NV0005', N'Trần Quang Đạt', CONVERT(date,'2002-09-12',23), N'0901234505', N'25 Phạm Văn Đồng, Phường 3, Quận Gò Vấp, TP.HCM', N'Nhân viên Spa/Hotel', CONVERT(date,'2022-05-10',23), N'images/staff/dattq.png', 1, N'079202005566'),
    (N'NV0006', N'Lê Minh Tuấn', CONVERT(date,'2001-07-08',23), N'0901234506', N'56 Phan Đăng Lưu, Phường 5, Quận Phú Nhuận, TP.HCM', N'Nhân viên bán hàng', CONVERT(date,'2023-01-10',23), N'images/staff/tuanlm.png', 1, N'079201006677'),
    (N'NV0007', N'Đặng Hoàng Yến', CONVERT(date,'2003-12-18',23), N'0901234507', N'89 Hoàng Hoa Thám, Phường 7, Quận Bình Thạnh, TP.HCM', N'Nhân viên Spa/Hotel', CONVERT(date,'2023-02-15',23), N'images/staff/yendh.png', 1, N'079203007788'),
    (N'NV0008', N'Nguyễn Minh Quân', CONVERT(date,'2000-04-30',23), N'0901234508', N'34 Bạch Đằng, Phường 2, Quận Tân Bình, TP.HCM', N'Cửa hàng trưởng', CONVERT(date,'2021-05-01',23), N'images/staff/quannguyen.png', 1, N'079200008899'),
    (N'NV0009', N'Trần Ngọc Anh', CONVERT(date,'2002-10-14',23), N'0901234509', N'15 Nơ Trang Long, Phường 14, Quận Bình Thạnh, TP.HCM', N'Nhân viên bán hàng', CONVERT(date,'2023-03-01',23), N'images/staff/anhtran.png', 1, N'079202009900'),
    (N'NV0010', N'Lê Hoàng Phúc', CONVERT(date,'2001-06-22',23), N'0901234510', N'67 Thống Nhất, Phường 11, Quận Gò Vấp, TP.HCM', N'Nhân viên kho', CONVERT(date,'2023-04-10',23), N'images/staff/phucle.png', 1, N'079201010011'),
    (N'NV0011', N'Phạm Thảo Nhi', CONVERT(date,'2003-09-05',23), N'0901234511', N'91 Nguyễn Thái Sơn, Phường 3, Quận Gò Vấp, TP.HCM', N'Nhân viên Spa/Hotel', CONVERT(date,'2023-05-15',23), N'images/staff/nhipham.png', 1, N'079203011122');

-- 9. PHÒNG HOTEL
INSERT INTO dbo.[phong_hotel] ([ma_phong], [ten_phong], [loai_phong], [gia_dem], [trang_thai], [suc_chua], [ghi_chu], [bang_gia])
VALUES
    (N'P001', N'Phòng Standard Corgi', N'Tiêu chuẩn', 150000.0, N'TRONG', 1, N'Phòng nệm êm ái, camera 24/7, điều hòa 26 độ C', N'150000'),
    (N'P002', N'Phòng Standard Poodle', N'Tiêu chuẩn', 150000.0, N'DANG_O', 1, N'Phòng nệm êm ái, camera 24/7, điều hòa 26 độ C', N'150000'),
    (N'P003', N'Phòng Standard Phốc Sóc', N'Tiêu chuẩn', 150000.0, N'TRONG', 1, N'Phòng nệm êm ái, camera 24/7, điều hòa 26 độ C', N'150000'),
    (N'P004', N'Phòng Standard Mèo Anh', N'Tiêu chuẩn', 150000.0, N'DANG_DON', 1, N'Phòng nệm êm ái, camera 24/7, điều hòa 26 độ C', N'150000'),
    (N'P005', N'Phòng VIP Hoàng Gia 1', N'VIP', 250000.0, N'DANG_O', 2, N'Phòng kính sang trọng, máy lọc không khí riêng, cây cào móng cao cấp', N'250000'),
    (N'P006', N'Phòng VIP Hoàng Gia 2', N'VIP', 250000.0, N'TRONG', 2, N'Phòng kính sang trọng, máy lọc không khí riêng, cây cào móng cao cấp', N'250000'),
    (N'P007', N'Phòng VIP Hoàng Gia 3', N'VIP', 250000.0, N'TRONG', 2, N'Phòng kính sang trọng, máy lọc không khí riêng, cây cào móng cao cấp', N'250000'),
    (N'P008', N'Phòng VIP Hoàng Gia 4', N'VIP', 250000.0, N'BAO_TRI', 2, N'Đang khử khuẩn tia cực tím UV và bảo dưỡng điều hòa', N'250000'),
    (N'P009', N'Penthouse Sky Pet 1', N'Penthouse', 400000.0, N'TRONG', 3, N'Phòng penthouse rộng 15m2, sân chơi riêng, pate bữa khuya thượng hạng', N'400000'),
    (N'P010', N'Penthouse Sky Pet 2', N'Penthouse', 400000.0, N'DANG_O', 3, N'Phòng penthouse rộng 15m2, sân chơi riêng, pate bữa khuya thượng hạng', N'400000'),
    (N'P011', N'Penthouse Sky Pet 3', N'Penthouse', 400000.0, N'TRONG', 3, N'Phòng penthouse rộng 15m2, sân chơi riêng, pate bữa khuya thượng hạng', N'400000'),
    (N'P012', N'Penthouse Sky Pet 4', N'Penthouse', 400000.0, N'TRONG', 3, N'Phòng penthouse rộng 15m2, sân chơi riêng, pate bữa khuya thượng hạng', N'400000');

-- 10. SẢN PHẨM & DỊCH VỤ
INSERT INTO dbo.[san_pham] ([ma_san_pham], [barcode], [ten], [nhom_hang], [don_vi_tinh], [gia_ban], [gia_nhap_gan_nhat], [ton_toi_thieu], [theo_doi_hsd], [anh], [trang_thai], [thuong_hieu], [quy_cach], [doi_tuong_su_dung], [mo_ta], [nguon_url], [ngay_thu_thap], [lich_su_gia], [de_xuat_gia])
VALUES
    (N'SP000001', N'8938505000015', N'Thức ăn hạt cho mèo ROYAL CANIN Fit 32 — Gói 400g', N'Thức ăn', N'Gói', 115000.0, 91000.0, 10, 1, N'images/products/SP000001.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Gói 400g', N'Mèo', N'Thức ăn hạt dinh dưỡng cân bằng cho mèo trưởng thành', N'https://petstation.vn/rc-fit-32', CONVERT(date,'2026-01-01',23), N'115000|2026-01-01', N'KhongDeXuat'),
    (N'SP000002', N'8938505000022', N'Thức ăn hạt cho chó ROYAL CANIN Mini Adult — Gói 800g', N'Thức ăn', N'Gói', 145000.0, 115000.0, 10, 1, N'images/products/SP000002.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Gói 800g', N'Chó', N'Dinh dưỡng hoàn hảo cho chó kích cỡ nhỏ', N'https://petstation.vn/rc-mini-adult', CONVERT(date,'2026-01-01',23), N'145000|2026-01-01', N'KhongDeXuat'),
    (N'SP000003', N'8938505000039', N'Pate cho mèo WHISKAS vị cá hồi túi 85g', N'Thức ăn', N'Gói', 22000.0, 16000.0, 20, 1, N'images/products/SP000003.jpg', N'DANG_BAN', N'WHISKAS', N'Túi 85g', N'Mèo', N'Pate cá hồi thơm ngon cung cấp omega 3', N'https://petstation.vn/whiskas-cahoi', CONVERT(date,'2026-01-01',23), N'22000|2026-01-01', N'KhongDeXuat'),
    (N'SP000004', N'8938505000046', N'Pate cho chó PEDIGREE vị bò sốt túi 130g', N'Thức ăn', N'Gói', 25000.0, 18000.0, 20, 1, N'images/products/SP000004.jpg', N'DANG_BAN', N'PEDIGREE', N'Túi 130g', N'Chó', N'Thức ăn ướt thịt bò hầm nước sốt', N'https://petstation.vn/pedigree-bo', CONVERT(date,'2026-01-01',23), N'25000|2026-01-01', N'KhongDeXuat'),
    (N'SP000005', N'8938505000053', N'Sữa tắm dưỡng lông mượt JOYCE & DOLLS Rose 400ml', N'Grooming', N'Chai', 185000.0, 135000.0, 5, 0, N'images/products/SP000005.jpg', N'DANG_BAN', N'JOYCE & DOLLS', N'Chai 400ml', N'Chó và mèo', N'Sữa tắm hương hoa hồng lưu hương 7 ngày', N'https://petstation.vn/jd-rose', CONVERT(date,'2026-01-01',23), N'185000|2026-01-01', N'KhongDeXuat'),
    (N'SP000006', N'8938505000060', N'Sữa tắm trị ve rận và bọ chét Hantox 200ml', N'Thuốc & Sức khỏe', N'Chai', 65000.0, 45000.0, 10, 1, N'images/products/SP000006.jpg', N'DANG_BAN', N'Hantox', N'Chai 200ml', N'Chó và mèo', N'Đặc trị ve, bọ chét, rận tai hiệu quả', N'https://petstation.vn/hantox-200', CONVERT(date,'2026-01-01',23), N'65000|2026-01-01', N'KhongDeXuat'),
    (N'SP000007', N'8938505000077', N'Cát vệ sinh đất sét bentonite siêu vón túi 5L', N'Vệ sinh', N'Túi', 60000.0, 42000.0, 20, 0, N'images/products/SP000007.jpg', N'DANG_BAN', N'Happy Pet', N'Túi 5L', N'Mèo', N'Hút ẩm nhanh khử mùi hương phấn em bé', N'https://petstation.vn/cat-dat-set-5l', CONVERT(date,'2026-01-01',23), N'60000|2026-01-01', N'KhongDeXuat'),
    (N'SP000008', N'8938505000084', N'Cát đậu nành hữu cơ xả bồn cầu CATS BEST 6L', N'Vệ sinh', N'Túi', 175000.0, 130000.0, 15, 0, N'images/products/SP000008.jpg', N'DANG_BAN', N'CATS BEST', N'Túi 6L', N'Mèo', N'Cát hữu cơ tan trong nước thân thiện môi trường', N'https://petstation.vn/cats-best-6l', CONVERT(date,'2026-01-01',23), N'175000|2026-01-01', N'KhongDeXuat'),
    (N'SP000009', N'8938505000091', N'Vòng cổ da bò kèm chuông Hand in Hand', N'Phụ kiện', N'Cái', 75000.0, 50000.0, 10, 0, N'images/products/SP000009.jpg', N'DANG_BAN', N'Hand in Hand', N'Size M', N'Chó và mèo', N'Chất liệu da bò bền đẹp êm ái', N'https://petstation.vn/vong-co-da', CONVERT(date,'2026-01-01',23), N'75000|2026-01-01', N'KhongDeXuat'),
    (N'SP000010', N'8938505000107', N'Dây dắt kèm yếm ngực phản quang đi dạo', N'Phụ kiện', N'Bộ', 95000.0, 65000.0, 10, 0, N'images/products/SP000010.jpg', N'DANG_BAN', N'BOBO', N'Size L', N'Chó', N'Phản quang an toàn khi dắt cún đi dạo ban đêm', N'https://petstation.vn/day-dat-yem', CONVERT(date,'2026-01-01',23), N'95000|2026-01-01', N'KhongDeXuat'),
    (N'SP000011', N'8938505000114', N'Bát ăn inox đôi đế chống trượt hình ếch', N'Phụ kiện', N'Cái', 55000.0, 38000.0, 15, 0, N'images/products/SP000011.jpg', N'DANG_BAN', N'Makar', N'Inox 304', N'Chó và mèo', N'Chống gỉ sét, đế cao su chống lật', N'https://petstation.vn/bat-inox-doi', CONVERT(date,'2026-01-01',23), N'55000|2026-01-01', N'KhongDeXuat'),
    (N'SP000012', N'8938505000121', N'Nệm nằm lông cừu ấm áp mùa đông size L', N'Chuồng - Nệm', N'Cái', 220000.0, 160000.0, 8, 0, N'images/products/SP000012.jpg', N'DANG_BAN', N'BOBO', N'Size L 60x50cm', N'Chó và mèo', N'Bông gòn cao cấp nâng đỡ xương khớp', N'https://petstation.vn/nem-long-cuu', CONVERT(date,'2026-01-01',23), N'220000|2026-01-01', N'KhongDeXuat'),
    (N'SP000013', N'8938505000138', N'Thuốc xịt trị nấm da Fungikur 50ml', N'Thuốc & Sức khỏe', N'Chai', 110000.0, 80000.0, 12, 1, N'images/products/SP000013.jpg', N'DANG_BAN', N'Alkin', N'Chai 50ml', N'Chó và mèo', N'Đặc trị vảy gàu, rụng lông do nấm', N'https://petstation.vn/fungikur-50', CONVERT(date,'2026-01-01',23), N'110000|2026-01-01', N'KhongDeXuat'),
    (N'SP000014', N'8938505000145', N'Bánh thưởng thanh sạch răng cún Vegebrand 120g', N'Thức ăn', N'Gói', 35000.0, 24000.0, 25, 1, N'images/products/SP000014.jpg', N'DANG_BAN', N'VEGEBRAND', N'Gói 120g', N'Chó', N'Giảm cao răng và mùi hôi miệng', N'https://petstation.vn/vegebrand-stick', CONVERT(date,'2026-01-01',23), N'35000|2026-01-01', N'KhongDeXuat'),
    (N'SP000015', N'8938505000152', N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 lít; Đóng gói: 1 túi', N'Vệ sinh', N'Túi', 160000.00, 104000.00, 5, 0, N'images/products/SP000015.jpg', N'DANG_BAN', N'IRIS OHYAMA', N'Dung tích: 10 lít; Đóng gói: 1 túi', N'Mèo', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 10 lít; Đóng gói: 1 túi.', N'https://www.petmart.vn/cat-ve-sinh-cho-meo-sieu-von-iris-ohyama-kfag-100', CONVERT(date,'2026-10-19',23), N'0.00|160000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000016', N'8938505000169', N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói', N'Thức ăn', N'Gói', 10000.00, 6500.00, 5, 1, N'images/products/SP000016.jpg', N'DANG_BAN', N'VEGEBRAND', N'Đóng gói: 1 gói', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: Đóng gói: 1 gói.', N'https://www.petmart.vn/xuong-cho-cho-gam-sach-rang-vegebrand-360-bone-prevent-tartar', CONVERT(date,'2026-10-19',23), N'0.00|10000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000017', N'8938505000176', N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800g', N'Thức ăn', N'Gói', 225000.00, 146250.00, 5, 1, N'images/products/SP000017.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Trọng lượng: 800g', N'Chó', N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 800g.', N'https://www.petmart.vn/thuc-an-cho-cho-con-co-nho-royal-canin-mini-puppy', CONVERT(date,'2026-10-19',23), N'0.00|225000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000018', N'8938505000183', N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150ml', N'Vệ sinh', N'Chai', 150000.00, 97500.00, 5, 0, N'images/products/SP000018.jpg', N'DANG_BAN', N'JOYCE & DOLLS', N'Dung tích: 150ml', N'Chó và mèo', N'Vệ sinh thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 150ml.', N'https://www.petmart.vn/chai-xit-cho-meo-di-ve-sinh-dung-cho-joyce-dolls-trainer-puppy', CONVERT(date,'2026-10-19',23), N'0.00|150000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000019', N'8938505000190', N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP000019.jpg', N'DANG_BAN', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-nguyen-chat-cat-sea-fish-pure-tuna-meat', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000020', N'8938505000206', N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400g', N'Thức ăn', N'Gói', 130000.00, 84500.00, 5, 1, N'images/products/SP000020.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-con-royal-canin-kitten', CONVERT(date,'2026-10-19',23), N'0.00|130000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000021', N'8938505000213', N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP000021.jpg', N'DANG_BAN', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-ca-ngu-whiskas-tuna-flavour-sauce', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000022', N'8938505000220', N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 lít', N'Vệ sinh', N'Gói', 160000.00, 104000.00, 5, 0, N'images/products/SP000022.jpg', N'DANG_BAN', N'TRIXIE', N'Dung tích: 5 lít', N'Mèo', N'Vệ sinh thương hiệu TRIXIE, dành cho mèo. Quy cách: Dung tích: 5 lít.', N'https://www.petmart.vn/cat-ve-sinh-thuy-tinh-cho-meo-trixie-silikatstreu', CONVERT(date,'2026-10-19',23), N'0.00|160000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000023', N'8938505000237', N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400g', N'Thức ăn', N'Gói', 125000.00, 81250.00, 5, 1, N'images/products/SP000023.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-indoor-27', CONVERT(date,'2026-10-19',23), N'0.00|125000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000024', N'8938505000244', N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP000024.jpg', N'DANG_BAN', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-thit-bo-whiskas-beef-flavour-sauce', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000025', N'8938505000251', N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ', N'Vệ sinh', N'Cái', 5000.00, 3250.00, 5, 0, N'images/products/SP000025.jpg', N'DANG_BAN', N'IRIS OHYAMA', N'Đóng gói: 1 miếng lẻ', N'Chó', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.', N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-88', CONVERT(date,'2026-10-19',23), N'0.00|5000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000026', N'8938505000268', N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP000026.jpg', N'DANG_BAN', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/banh-thuong-cho-cho-vi-thit-bo-vegebrand-orgo-freshening-biscuit-bacon-beef', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000027', N'8938505000275', N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 35000.00, 22750.00, 5, 1, N'images/products/SP000027.jpg', N'DANG_BAN', N'IRIS OHYAMA', N'Trọng lượng: 100g; Đóng gói: 1 lon', N'Chó', N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-cho-vi-thit-bo-iris-ohyama-one-care-beef', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000028', N'8938505000282', N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP000028.jpg', N'DANG_BAN', N'PEDIGREE', N'Trọng lượng: 100g', N'Chó', N'Thức ăn thương hiệu PEDIGREE, dành cho chó. Quy cách: Trọng lượng: 100g.', N'https://www.petmart.vn/pate-cho-cho-nuoc-sot-vi-thit-bo-pedigree-pouch-beef', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000029', N'8938505000299', N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500g', N'Thức ăn', N'Gói', 185000.00, 120250.00, 5, 1, N'images/products/SP000029.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Trọng lượng: 500g', N'Chó', N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 500g.', N'https://www.petmart.vn/thuc-an-cho-cho-poodle-con-royal-canin-poodle-puppy', CONVERT(date,'2026-10-19',23), N'0.00|185000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000030', N'8938505000305', N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85g', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP000030.jpg', N'DANG_BAN', N'WHISKAS', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/nuoc-sot-cho-meo-vi-thit-ga-whiskas-chicken-flavour-in-sauce', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000031', N'8938505000312', N'Pate cho mèo mẹ và mèo con CATIDEA Mother & Baby Cat — Trọng lượng: 85g', N'Thức ăn', N'Gói', 30000.00, 19500.00, 5, 1, N'images/products/SP000031.jpg', N'DANG_BAN', N'CATIDEA', N'Trọng lượng: 85g', N'Mèo', N'Thức ăn thương hiệu CATIDEA, dành cho mèo. Quy cách: Trọng lượng: 85g.', N'https://www.petmart.vn/pate-cho-meo-me-va-meo-con-catidea-mother-baby-cat', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000032', N'8938505000329', N'Pate cho mèo vị cá ngừ thanh cua CAT SEA FISH Tuna Surimi — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP000032.jpg', N'DANG_BAN', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-thanh-cua-cat-sea-fish-tuna-surimi', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000033', N'8938505000336', N'Pate cho mèo CIAO Tuna & Whitebait vị cá ngừ và cá chạch trắng — Trọng lượng: 60g', N'Thức ăn', N'Gói', 20000.00, 13000.00, 5, 1, N'images/products/SP000033.jpg', N'DANG_BAN', N'CIAO', N'Trọng lượng: 60g', N'Mèo', N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.', N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-whitebait-vi-ca-ngu-va-ca-chach-trang', CONVERT(date,'2026-10-19',23), N'0.00|20000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000034', N'8938505000343', N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1kg', N'Vệ sinh', N'Gói', 140000.00, 91000.00, 5, 0, N'images/products/SP000034.jpg', N'DANG_BAN', N'CATS BEST', N'Trọng lượng: 2.1kg', N'Mèo', N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.1kg.', N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-original', CONVERT(date,'2026-10-19',23), N'0.00|140000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000035', N'8938505000350', N'Xương cho chó vị thịt bò VEGEBRAND Orgo Nutrients Beef', N'Thức ăn', N'Gói', 25000.00, 16250.00, 5, 1, N'images/products/SP000035.jpg', N'DANG_BAN', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/xuong-cho-cho-vi-thit-bo-vegebrand-orgo-nutrients-beef', CONVERT(date,'2026-10-19',23), N'0.00|25000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000036', N'8938505000367', N'Thức ăn cho mèo con và mèo mẹ ROYAL CANIN Mother & Babycat — Trọng lượng: 400g', N'Thức ăn', N'Gói', 130000.00, 84500.00, 5, 1, N'images/products/SP000036.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-con-va-meo-me-royal-canin-mother-babycat', CONVERT(date,'2026-10-19',23), N'0.00|130000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000037', N'8938505000374', N'Pate cho mèo vị cá ngừ cá thu CAT SEA FISH Tuna Mackerel — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP000037.jpg', N'DANG_BAN', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-thu-cat-sea-fish-tuna-mackerel', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000038', N'8938505000381', N'Thức ăn cho mèo trưởng thành ROYAL CANIN Regular Fit 32 — Trọng lượng: 400g', N'Thức ăn', N'Gói', 115000.00, 74750.00, 5, 1, N'images/products/SP000038.jpg', N'DANG_BAN', N'ROYAL CANIN', N'Trọng lượng: 400g', N'Mèo', N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.', N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-regular-fit-32', CONVERT(date,'2026-10-19',23), N'0.00|115000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000039', N'8938505000398', N'Thức ăn cho chó Poodle MKB All Life Stages Formula Nutrition — Trọng lượng: 1kg', N'Thức ăn', N'Gói', 315000.00, 204750.00, 5, 1, N'images/products/SP000039.jpg', N'DANG_BAN', N'MKB', N'Trọng lượng: 1kg', N'Chó', N'Thức ăn thương hiệu MKB, dành cho chó. Quy cách: Trọng lượng: 1kg.', N'https://www.petmart.vn/thuc-an-cho-cho-poodle-mkb-all-life-stages-formula-nutrition', CONVERT(date,'2026-10-19',23), N'0.00|315000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000040', N'8938505000404', N'Pate cho chó hỗ trợ chức năng gan và sáng mắt IRIS OHYAMA Benefit For Eyes & Liver — Trọng lượng: 100g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 35000.00, 22750.00, 5, 1, N'images/products/SP000040.jpg', N'DANG_BAN', N'IRIS OHYAMA', N'Trọng lượng: 100g; Đóng gói: 1 lon', N'Chó', N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-cho-ho-tro-chuc-nang-gan-va-sang-mat-iris-ohyama-benefit-for-eyes-liver', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000041', N'8938505000411', N'Pate cho mèo CIAO Tuna & Salmon vị cá ngừ và cá hồi — Trọng lượng: 60g', N'Thức ăn', N'Gói', 20000.00, 13000.00, 5, 1, N'images/products/SP000041.jpg', N'DANG_BAN', N'CIAO', N'Trọng lượng: 60g', N'Mèo', N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.', N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-salmon-vi-ca-ngu-va-ca-hoi', CONVERT(date,'2026-10-19',23), N'0.00|20000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000042', N'8938505000428', N'Đồ chơi cho chó mèo dạng bóng thừng BOBO', N'Phụ kiện', N'Cái', 30000.00, 19500.00, 5, 0, N'images/products/SP000042.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-cho-cho-meo-dang-bong-thung-bobo', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'PENDING|4|30000.00|35000.00|đề xuất giá chưa áp dụng|2026-10-19 08:00:00'),
    (N'SP000043', N'8938505000435', N'Pate cho mèo vị cá ngừ cá cơm CAT SEA FISH Tuna Anchovy — Trọng lượng: 85g; Đóng gói: 1 lon', N'Thức ăn', N'Lon', 30000.00, 19500.00, 5, 1, N'images/products/SP000043.jpg', N'DANG_BAN', N'CAT SEA FISH', N'Trọng lượng: 85g; Đóng gói: 1 lon', N'Mèo', N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.', N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-com-cat-sea-fish-tuna-anchovy', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000044', N'8938505000442', N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone', N'Thức ăn', N'Gói', 50000.00, 32500.00, 5, 1, N'images/products/SP000044.jpg', N'DANG_BAN', N'VEGEBRAND', N'', N'Chó', N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.', N'https://www.petmart.vn/xuong-cho-cho-vi-thit-vit-bone-for-poodles', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000045', N'8938505000459', N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam', N'Grooming', N'Chai', 180000.00, 117000.00, 5, 1, N'images/products/SP000045.jpg', N'DANG_BAN', N'JOYCE & DOLLS', N'', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo.', N'https://www.petmart.vn/sua-tam-kho-cho-cho-meo-joyce-dolls-waterless-cleansing-foam', CONVERT(date,'2026-10-19',23), N'0.00|180000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000046', N'8938505000466', N'Khay vệ sinh cho mèo MAKAR Cat Litter Station', N'Vệ sinh', N'Cái', 250000.00, 162500.00, 5, 0, N'images/products/SP000046.jpg', N'DANG_BAN', N'MAKAR', N'', N'Mèo', N'Vệ sinh thương hiệu MAKAR, dành cho mèo.', N'https://www.petmart.vn/khay-ve-sinh-cho-meo-makar-cat-litter-station', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000047', N'8938505000473', N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-42 — Đóng gói: 1 miếng lẻ', N'Vệ sinh', N'Cái', 10000.00, 6500.00, 5, 0, N'images/products/SP000047.jpg', N'DANG_BAN', N'IRIS OHYAMA', N'Đóng gói: 1 miếng lẻ', N'Chó', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.', N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-42', CONVERT(date,'2026-10-19',23), N'0.00|10000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000048', N'8938505000480', N'Cát vệ sinh cho mèo than hoạt tính IRIS OHYAMA Charcoal SNS-70 — Dung tích: 7 lít; Đóng gói: 1 túi', N'Vệ sinh', N'Túi', 160000.00, 104000.00, 5, 0, N'images/products/SP000048.jpg', N'DANG_BAN', N'IRIS OHYAMA', N'Dung tích: 7 lít; Đóng gói: 1 túi', N'Mèo', N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 7 lít; Đóng gói: 1 túi.', N'https://www.petmart.vn/cat-ve-sinh-cho-meo-than-hoat-tinh-iris-ohyama-charcoal-sns-70', CONVERT(date,'2026-10-19',23), N'0.00|160000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000049', N'8938505000497', N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb', N'Grooming', N'Cái', 85000.00, 55250.00, 5, 1, N'images/products/SP000049.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/luoc-chai-long-cho-meo-dau-luoi-mem-bobo-pet-comb', CONVERT(date,'2026-10-19',23), N'0.00|85000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000050', N'8938505000503', N'Cát vệ sinh hữu cơ cho mèo CATS BEST Smart Peppets — Trọng lượng: 2.5kg', N'Vệ sinh', N'Gói', 155000.00, 100750.00, 5, 0, N'images/products/SP000050.jpg', N'DANG_BAN', N'CATS BEST', N'Trọng lượng: 2.5kg', N'Mèo', N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.5kg.', N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-smart-peppets', CONVERT(date,'2026-10-19',23), N'0.00|155000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000051', N'8938505000510', N'Vòng cổ chó mèo kèm dây dắt cỡ mini HAND IN HAND', N'Phụ kiện', N'Cái', 70000.00, 45500.00, 5, 0, N'images/products/SP000051.jpg', N'DANG_BAN', N'HAND IN HAND', N'', N'Chó và mèo', N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-mini-hand-in-hand', CONVERT(date,'2026-10-19',23), N'0.00|70000.00|2|2026-09-15 08:00:00', N'PENDING|4|70000.00|75000.00|đề xuất giá chưa áp dụng|2026-10-19 08:00:00'),
    (N'SP000052', N'8938505000527', N'Bóng đồ chơi cho chó mèo có chuông nhựa BOBO', N'Phụ kiện', N'Cái', 45000.00, 29250.00, 5, 0, N'images/products/SP000052.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/bong-do-choi-cho-cho-meo-co-chuong-nhua-bobo', CONVERT(date,'2026-10-19',23), N'0.00|45000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000053', N'8938505000534', N'Đồ chơi dây thừng cho chó BOBO', N'Phụ kiện', N'Cái', 35000.00, 22750.00, 5, 0, N'images/products/SP000053.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó', N'Phụ kiện thương hiệu BOBO, dành cho chó.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-bobo', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000054', N'8938505000541', N'Vòng cổ chống ve chó BIOLINE Flea and Tick Collar For Dogs', N'Phụ kiện', N'Cái', 60000.00, 39000.00, 5, 0, N'images/products/SP000054.jpg', N'DANG_BAN', N'BIOLINE', N'', N'Chó', N'Phụ kiện thương hiệu BIOLINE, dành cho chó.', N'https://www.petmart.vn/vong-co-chong-ve-cho-bioline-flea-and-tick-collar-for-dogs', CONVERT(date,'2026-10-19',23), N'0.00|60000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000055', N'8938505000558', N'Khay vệ sinh cho chó MAKAR Dog Toilet Trays Small — Kích thước: S', N'Vệ sinh', N'Cái', 220000.00, 143000.00, 5, 0, N'images/products/SP000055.jpg', N'DANG_BAN', N'MAKAR', N'Kích thước: S', N'Chó', N'Vệ sinh thương hiệu MAKAR, dành cho chó. Quy cách: Kích thước: S.', N'https://www.petmart.vn/khay-ve-sinh-cho-cho-makar-dog-toilet-trays-small', CONVERT(date,'2026-10-19',23), N'0.00|220000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000056', N'8938505000565', N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush', N'Grooming', N'Cái', 30000.00, 19500.00, 5, 1, N'images/products/SP000056.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/ban-chai-tam-cho-cho-meo-bobo-pet-washing-brush', CONVERT(date,'2026-10-19',23), N'0.00|30000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000057', N'8938505000572', N'Đồ chơi cần câu cho mèo PAW vui nhộn', N'Phụ kiện', N'Cái', 75000.00, 48750.00, 5, 0, N'images/products/SP000057.jpg', N'DANG_BAN', N'PAW', N'', N'Mèo', N'Phụ kiện thương hiệu PAW, dành cho mèo.', N'https://www.petmart.vn/do-choi-can-cau-cho-meo-paw-vui-nhon', CONVERT(date,'2026-10-19',23), N'0.00|75000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000058', N'8938505000589', N'Sữa tắm cho chó mèo hương hoa hồng JOYCE & DOLLS Rose Dew — Dung tích: 400ml', N'Grooming', N'Chai', 250000.00, 162500.00, 5, 1, N'images/products/SP000058.jpg', N'DANG_BAN', N'JOYCE & DOLLS', N'Dung tích: 400ml', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.', N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-hong-joyce-dolls-rose-dew', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000059', N'8938505000596', N'Đồ chơi cho chó mèo Paw hình con gà', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP000059.jpg', N'DANG_BAN', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-cho-cho-meo-paw-hinh-con-ga', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000060', N'8938505000602', N'Bình uống nước cho chó mèo gài chuồng BOBO', N'Phụ kiện', N'Cái', 80000.00, 52000.00, 5, 0, N'images/products/SP000060.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/binh-uong-nuoc-cho-cho-meo-gai-chuong-bobo', CONVERT(date,'2026-10-19',23), N'0.00|80000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000061', N'8938505000619', N'Vòng cổ chống liếm cho chó mèo PAW Pet Elizabethan Collar', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP000061.jpg', N'DANG_BAN', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-chong-liem-cho-cho-meo-paw-pet-elizabethan-collar', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000062', N'8938505000626', N'Sữa tắm cho chó diệt khuẩn BBN Bactericidal Shampoo Lemon', N'Grooming', N'Chai', 150000.00, 97500.00, 5, 1, N'images/products/SP000062.jpg', N'DANG_BAN', N'BBN', N'', N'Chó', N'Grooming thương hiệu BBN, dành cho chó.', N'https://www.petmart.vn/sua-tam-cho-cho-diet-khuan-bbn-bactericidal-shampoo-lemon', CONVERT(date,'2026-10-19',23), N'0.00|150000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000063', N'8938505000633', N'Găng tay tắm cho chó mèo BOBO Pet Grooming Brush Gloves', N'Grooming', N'Cái', 40000.00, 26000.00, 5, 1, N'images/products/SP000063.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Grooming thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/gang-tay-tam-cho-cho-meo-bobo-pet-grooming-brush-gloves', CONVERT(date,'2026-10-19',23), N'0.00|40000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000064', N'8938505000640', N'Kềm cắt bấm móng chó mèo PAW Safety Guard Clipper', N'Grooming', N'Cái', 70000.00, 45500.00, 5, 1, N'images/products/SP000064.jpg', N'DANG_BAN', N'PAW', N'', N'Chó và mèo', N'Grooming thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/kem-cat-bam-mong-cho-meo-paw-safety-guard-clipper', CONVERT(date,'2026-10-19',23), N'0.00|70000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000065', N'8938505000657', N'Sữa tắm cho chó lông trắng BBN Whitening Shampoo', N'Grooming', N'Chai', 150000.00, 97500.00, 5, 1, N'images/products/SP000065.jpg', N'DANG_BAN', N'BBN', N'', N'Chó', N'Grooming thương hiệu BBN, dành cho chó.', N'https://www.petmart.vn/sua-tam-cho-cho-long-trang-bbn-whitening-shampoo', CONVERT(date,'2026-10-19',23), N'0.00|150000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000066', N'8938505000664', N'Đồ chơi dây thừng cho chó mèo đan chéo BOBO', N'Phụ kiện', N'Cái', 55000.00, 35750.00, 5, 0, N'images/products/SP000066.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-dan-cheo-bobo', CONVERT(date,'2026-10-19',23), N'0.00|55000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000067', N'8938505000671', N'Bát ăn cho chó mèo nhựa mềm SAWYER PET', N'Phụ kiện', N'Cái', 40000.00, 26000.00, 5, 0, N'images/products/SP000067.jpg', N'DANG_BAN', N'SAWYER PET', N'', N'Chó và mèo', N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-cho-cho-meo-nhua-mem-sawyer-pet', CONVERT(date,'2026-10-19',23), N'0.00|40000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000068', N'8938505000688', N'Sữa tắm cho chó mèo hương hoa nhài JOYCE & DOLLS Moder Jasmine — Dung tích: 400ml', N'Grooming', N'Chai', 250000.00, 162500.00, 5, 1, N'images/products/SP000068.jpg', N'DANG_BAN', N'JOYCE & DOLLS', N'Dung tích: 400ml', N'Chó và mèo', N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.', N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-nhai-joyce-dolls-moder-jasmine', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000069', N'8938505000695', N'Lược chải lông chó mèo nút bấm KUDI Pet Grooming Slicker Brushes', N'Grooming', N'Cái', 170000.00, 110500.00, 5, 1, N'images/products/SP000069.jpg', N'DANG_BAN', N'KUDI', N'', N'Chó và mèo', N'Grooming thương hiệu KUDI, dành cho chó và mèo.', N'https://www.petmart.vn/luoc-chai-long-cho-meo-nut-bam-kudi-pet-grooming-slicker-brushes', CONVERT(date,'2026-10-19',23), N'0.00|170000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000070', N'8938505000701', N'Thảm mát giải nhiệt cho chó mèo PAW Pet Cool Mat', N'Chuồng - Nệm', N'Cái', 195000.00, 126750.00, 5, 0, N'images/products/SP000070.jpg', N'DANG_BAN', N'PAW', N'', N'Chó và mèo', N'Chuồng - Nệm thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/tham-mat-giai-nhiet-cho-cho-meo-paw-pet-cool-mat', CONVERT(date,'2026-10-19',23), N'0.00|195000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000071', N'8938505000718', N'Vòng cổ chó mèo kèm dây dắt cỡ bé HAND IN HAND', N'Phụ kiện', N'Cái', 80000.00, 52000.00, 5, 0, N'images/products/SP000071.jpg', N'DANG_BAN', N'HAND IN HAND', N'', N'Chó và mèo', N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.', N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-be-hand-in-hand', CONVERT(date,'2026-10-19',23), N'0.00|80000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000072', N'8938505000725', N'Bát ăn đôi cho chó mèo bằng nhựa SAWYER PET', N'Phụ kiện', N'Cái', 60000.00, 39000.00, 5, 0, N'images/products/SP000072.jpg', N'DANG_BAN', N'SAWYER PET', N'', N'Chó và mèo', N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-doi-cho-cho-meo-bang-nhua-sawyer-pet', CONVERT(date,'2026-10-19',23), N'0.00|60000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000073', N'8938505000732', N'Đồ chơi dây thừng cho chó mèo PAW loại dài', N'Phụ kiện', N'Cái', 50000.00, 32500.00, 5, 0, N'images/products/SP000073.jpg', N'DANG_BAN', N'PAW', N'', N'Chó và mèo', N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.', N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-paw-loai-dai', CONVERT(date,'2026-10-19',23), N'0.00|50000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000074', N'8938505000749', N'Bát ăn cho chó mèo bằng nhựa BOBO Plastic Bowl 3038', N'Phụ kiện', N'Cái', 35000.00, 22750.00, 5, 0, N'images/products/SP000074.jpg', N'DANG_BAN', N'BOBO', N'', N'Chó và mèo', N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.', N'https://www.petmart.vn/bat-an-cho-cho-meo-bang-nhua-bobo-plastic-bowl-3038', CONVERT(date,'2026-10-19',23), N'0.00|35000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000075', N'8938505000756', N'Chuồng sắt sơn tĩnh điện gấp gọn cho chó mèo size L (Kèm khay)', N'Chuồng - Nệm', N'Cái', 450000.00, 310000.00, 3, 0, N'images/products/SP000075.jpg', N'DANG_BAN', N'BOBO', N'Size L (76x48x57cm)', N'Chó và mèo', N'Chuồng sắt sơn tĩnh điện bền đẹp, có khay hứng vệ sinh bên dưới, dễ dàng gấp gọn mang đi du lịch hoặc cất giữ.', N'https://www.petmart.vn/chuong-cho-meo-gap-gon', CONVERT(date,'2026-10-19',23), N'0.00|450000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000076', N'8938505000763', N'Chuồng sắt tĩnh điện nan dày 2 cửa cho chó mèo size M', N'Chuồng - Nệm', N'Cái', 350000.00, 240000.00, 3, 0, N'images/products/SP000076.jpg', N'DANG_BAN', N'PAW', N'Size M (60x42x50cm)', N'Chó và mèo', N'Chuồng sắt nan dày dặn chắc chắn, thiết kế 2 cửa tiện lợi đưa đón thú cưng.', N'https://www.petmart.vn/chuong-cho-meo-nan-day', CONVERT(date,'2026-10-19',23), N'0.00|350000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000077', N'8938505000770', N'Balo phi hành gia vận chuyển chó mèo mặt kính trong suốt', N'Chuồng - Nệm', N'Cái', 280000.00, 190000.00, 5, 0, N'images/products/SP000077.jpg', N'DANG_BAN', N'PAW', N'Balo 42x32x25cm', N'Chó và mèo', N'Balo vận chuyển thú cưng dạng phi hành gia với vòm kính trong suốt thoáng khí, quai đeo đệm êm chống đau vai.', N'https://www.petmart.vn/balo-phi-hanh-gia-cho-meo', CONVERT(date,'2026-10-19',23), N'0.00|280000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000080', N'8938505000800', N'Gel dinh dưỡng cao năng lượng Virbac Nutri-Plus Gel tuýp 120g', N'Thuốc & Sức khỏe', N'Tuýp', 250000.00, 180000.00, 5, 1, N'images/products/SP000080.jpg', N'DANG_BAN', N'Virbac', N'Tuýp 120g', N'Chó và mèo', N'Gel dinh dưỡng cao năng lượng cung cấp vitamin và khoáng chất thiết yếu cho thú cưng còi cọc hoặc hồi sức.', N'https://paddy.vn/products/thuc-an-dinh-duong-cho-cho-meo-gel-virbac-nutri-plus', CONVERT(date,'2026-10-19',23), N'0.00|250000.00|2|2026-09-15 08:00:00', N'KhongDeXuat'),
    (N'SP000081', N'8938505000817', N'Men tiêu hóa vi sinh đường ruột Biotic Bio-Pharmachemie gói 5g', N'Thuốc & Sức khỏe', N'Gói', 10000.00, 6500.00, 20, 1, N'images/products/SP000081.jpg', N'DANG_BAN', N'Bio-Pharmachemie', N'Gói 5g', N'Chó và mèo', N'Men vi sinh đường ruột bổ sung lợi khuẩn, phòng và trị tiêu chảy, rối loạn tiêu hóa và giảm mùi hôi phân.', N'https://paddy.vn/products/men-biotic-ho-tro-tieu-hoa-cho-meo', CONVERT(date,'2026-10-19',23), N'0.00|10000.00|2|2026-09-15 08:00:00', N'KhongDeXuat');

-- 12. THÚ CƯNG
INSERT INTO dbo.[thu_cung] ([ma_thu_cung], [ma_khach_hang], [ten], [loai], [giong], [gioi_tinh], [mau_long], [can_nang], [tinh_trang_suc_khoe], [di_ung_benh_ly], [anh], [dang_theo_doi], [ngay_sinh], [tinh_cach], [tiem_chung], [so_mui_tiem], [ngay_tiem_gan_nhat])
VALUES
    (N'TC000001', N'KH000001', N'Milo', N'Chó', N'Poodle', N'Đực', N'Nâu đỏ', 4.2, N'Khỏe mạnh, lanh lợi', N'Không có dị ứng', N'images/pets/tc01.jpg', 1, CONVERT(date,'2023-04-10',23), N'Hiền lành, quấn chủ', N'Dại|2026-05-10|2027-05-10|Đã tiêm đủ;7 bệnh|2026-06-15|2027-06-15|Đã tiêm đủ', 2, CONVERT(date,'2026-06-15',23)),
    (N'TC000002', N'KH000002', N'Miu Miu', N'Mèo', N'Anh lông ngắn', N'Cái', N'Xám xanh', 3.8, N'Khỏe mạnh, mượt lông', N'Không có dị ứng', N'images/pets/tc02.jpg', 1, CONVERT(date,'2023-06-20',23), N'Điềm đạm, thích ngủ', N'Dại|2026-04-20|2027-04-20|Đã tiêm đủ;4 bệnh mèo|2026-05-25|2027-05-25|Đã tiêm đủ', 2, CONVERT(date,'2026-05-25',23)),
    (N'TC000003', N'KH000003', N'Lu Lu', N'Chó', N'Corgi', N'Đực', N'Vàng trắng', 10.5, N'Khỏe mạnh, ăn tốt', N'Không có dị ứng', N'images/pets/tc03.jpg', 1, CONVERT(date,'2022-09-15',23), N'Tăng động, vui tính', N'Dại|2026-03-12|2027-03-12|Đã tiêm đủ;7 bệnh|2026-04-18|2027-04-18|Đã tiêm đủ', 2, CONVERT(date,'2026-04-18',23)),
    (N'TC000004', N'KH000004', N'Kem', N'Chó', N'Phốc sóc', N'Cái', N'Trắng tuyết', 2.6, N'Rất tốt, nhanh nhẹn', N'Không có dị ứng', N'images/pets/tc04.jpg', 1, CONVERT(date,'2023-08-01',23), N'Nhõng nhẽo, thông minh', N'Dại|2026-05-02|2027-05-02|Đã tiêm đủ;7 bệnh|2026-06-05|2027-06-05|Đã tiêm đủ', 2, CONVERT(date,'2026-06-05',23)),
    (N'TC000005', N'KH000005', N'Bông', N'Mèo', N'Ba Tư', N'Cái', N'Trắng', 4.1, N'Lông dài cần chải hàng ngày', N'Không có dị ứng', N'images/pets/tc05.jpg', 1, CONVERT(date,'2022-11-20',23), N'Hiền dịu, thích vuốt ve', N'Dại|2026-02-14|2027-02-14|Đã tiêm đủ;4 bệnh mèo|2026-03-20|2027-03-20|Đã tiêm đủ', 2, CONVERT(date,'2026-03-20',23)),
    (N'TC000006', N'KH000006', N'Gấu', N'Chó', N'Golden Retriever', N'Đực', N'Vàng kim', 26.5, N'Cơ bắp tốt, nhanh nhẹn', N'Không có dị ứng', N'images/pets/tc06.jpg', 1, CONVERT(date,'2021-08-10',23), N'Rất thân thiện, biết vâng lời', N'Dại|2026-01-10|2027-01-10|Đã tiêm đủ;7 bệnh|2026-02-15|2027-02-15|Đã tiêm đủ', 2, CONVERT(date,'2026-02-15',23)),
    (N'TC000007', N'KH000007', N'Mochi', N'Mèo', N'Munchkin', N'Đực', N'Bicolor', 3.2, N'Chân ngắn, sức khỏe hoàn hảo', N'Không có dị ứng', N'images/pets/tc07.jpg', 1, CONVERT(date,'2023-03-12',23), N'Tinh nghịch, tò mò', N'Dại|2026-04-10|2027-04-10|Đã tiêm đủ;4 bệnh mèo|2026-05-15|2027-05-15|Đã tiêm đủ', 2, CONVERT(date,'2026-05-15',23)),
    (N'TC000008', N'KH000008', N'Rocky', N'Chó', N'Husky', N'Đực', N'Xám trắng', 22.0, N'Sức khỏe dồi dào, ăn nhiều', N'Không có dị ứng', N'images/pets/tc08.jpg', 1, CONVERT(date,'2022-04-25',23), N'Hài hước, thích hóng chuyện', N'Dại|2026-03-01|2027-03-01|Đã tiêm đủ;7 bệnh|2026-04-05|2027-04-05|Đã tiêm đủ', 2, CONVERT(date,'2026-04-05',23)),
    (N'TC000009', N'KH000009', N'Sushi', N'Mèo', N'Xiêm', N'Cái', N'Seal point', 3.5, N'Mắt xanh trong trẻo', N'Không có dị ứng', N'images/pets/tc09.jpg', 1, CONVERT(date,'2023-01-18',23), N'Thích kêu gọi chủ', N'Dại|2026-05-18|2027-05-18|Đã tiêm đủ;4 bệnh mèo|2026-06-20|2027-06-20|Đã tiêm đủ', 2, CONVERT(date,'2026-06-20',23)),
    (N'TC000010', N'KH000010', N'Ken', N'Chó', N'Shiba Inu', N'Đực', N'Vàng đỏ', 9.8, N'Săn chắc, dũng cảm', N'Không có dị ứng', N'images/pets/tc10.jpg', 1, CONVERT(date,'2022-10-08',23), N'Độc lập, trung thành', N'Dại|2026-02-28|2027-02-28|Đã tiêm đủ;7 bệnh|2026-03-30|2027-03-30|Đã tiêm đủ', 2, CONVERT(date,'2026-03-30',23)),
    (N'TC000011', N'KH000011', N'Đậu Đậu', N'Chó', N'Poodle Tiny', N'Đực', N'Trắng', 2.2, N'Khỏe mạnh, móng sạch', N'Không có dị ứng', N'images/pets/tc11.jpg', 1, CONVERT(date,'2023-09-02',23), N'Quấn chủ, nhát nước nhẹ', N'Dại|2026-06-10|2027-06-10|Đã tiêm đủ;7 bệnh|2026-07-12|2027-07-12|Đã tiêm đủ', 2, CONVERT(date,'2026-07-12',23)),
    (N'TC000012', N'KH000012', N'Simba', N'Mèo', N'Maine Coon', N'Đực', N'Tabby nâu', 7.5, N'To lớn, lông dày mượt', N'Không có dị ứng', N'images/pets/tc12.jpg', 1, CONVERT(date,'2022-06-14',23), N'Dịu dàng, đầm tính', N'Dại|2026-01-20|2027-01-20|Đã tiêm đủ;4 bệnh mèo|2026-02-22|2027-02-22|Đã tiêm đủ', 2, CONVERT(date,'2026-02-22',23)),
    (N'TC000013', N'KH000013', N'Bơ', N'Chó', N'Lạp Xưởng', N'Cái', N'Đen vàng', 6.2, N'Lưng dài, chân khỏe', N'Không có dị ứng', N'images/pets/tc13.jpg', 1, CONVERT(date,'2023-02-11',23), N'Thích chạy nhảy, giữ nhà', N'Dại|2026-04-15|2027-04-15|Đã tiêm đủ;7 bệnh|2026-05-20|2027-05-20|Đã tiêm đủ', 2, CONVERT(date,'2026-05-20',23)),
    (N'TC000014', N'KH000014', N'Choco', N'Mèo', N'Anh lông dài', N'Đực', N'Chocolate', 4.6, N'Béo tròn, lông tơi xốp', N'Không có dị ứng', N'images/pets/tc14.jpg', 1, CONVERT(date,'2022-12-05',23), N'Trầm tính, thích nằm sofa', N'Dại|2026-03-10|2027-03-10|Đã tiêm đủ;4 bệnh mèo|2026-04-12|2027-04-12|Đã tiêm đủ', 2, CONVERT(date,'2026-04-12',23)),
    (N'TC000015', N'KH000015', N'Sam', N'Chó', N'Samoyed', N'Đực', N'Trắng tinh', 20.5, N'Cười duyên, lông trắng như tuyết', N'Không có dị ứng', N'images/pets/tc15.jpg', 1, CONVERT(date,'2021-11-28',23), N'Nhiệt tình, hòa đồng', N'Dại|2026-02-05|2027-02-05|Đã tiêm đủ;7 bệnh|2026-03-10|2027-03-10|Đã tiêm đủ', 2, CONVERT(date,'2026-03-10',23)),
    (N'TC000016', N'KH000016', N'Bắp', N'Chó', N'Bắc Kinh lai', N'Cái', N'Vàng nhạt', 5.0, N'Mắt tròn to, ăn khỏe', N'Không có dị ứng', N'images/pets/tc16.jpg', 1, CONVERT(date,'2023-05-19',23), N'Cực kỳ bám người', N'Dại|2026-05-15|2027-05-15|Đã tiêm đủ;7 bệnh|2026-06-20|2027-06-20|Đã tiêm đủ', 2, CONVERT(date,'2026-06-20',23)),
    (N'TC000017', N'KH000017', N'Cún', N'Chó', N'Chihuahua', N'Đực', N'Vàng nhạt', 1.8, N'Nhỏ nhắn, lanh lợi', N'Không có dị ứng', N'images/pets/tc17.jpg', 1, CONVERT(date,'2023-07-22',23), N'Cảnh giác, trung thành', N'Dại|2026-06-01|2027-06-01|Đã tiêm đủ;7 bệnh|2026-07-05|2027-07-05|Đã tiêm đủ', 2, CONVERT(date,'2026-07-05',23)),
    (N'TC000018', N'KH000018', N'Zin', N'Mèo', N'Sphynx không lông', N'Đực', N'Hồng phấn', 3.4, N'Ấm áp, da sạch sẽ', N'Không có dị ứng', N'images/pets/tc18.jpg', 1, CONVERT(date,'2022-08-30',23), N'Rất thích ôm ấp sưởi ấm', N'Dại|2026-04-01|2027-04-01|Đã tiêm đủ;4 bệnh mèo|2026-05-05|2027-05-05|Đã tiêm đủ', 2, CONVERT(date,'2026-05-05',23)),
    (N'TC000019', N'KH000019', N'Coca', N'Chó', N'Pug', N'Đực', N'Vàng mặt đen', 8.2, N'Mặt xệ đáng yêu, thở khỏe', N'Không có dị ứng', N'images/pets/tc19.jpg', 1, CONVERT(date,'2022-05-14',23), N'Vui vẻ, háu ăn', N'Dại|2026-03-25|2027-03-25|Đã tiêm đủ;7 bệnh|2026-04-28|2027-04-28|Đã tiêm đủ', 2, CONVERT(date,'2026-04-28',23)),
    (N'TC000020', N'KH000020', N'Pepsi', N'Mèo', N'Scottish Fold tai cụp', N'Cái', N'Xám tro', 3.6, N'Tai cụp sát đầu, mắt to', N'Không có dị ứng', N'images/pets/tc20.jpg', 1, CONVERT(date,'2023-03-05',23), N'Dễ thương, ngoan ngoãn', N'Dại|2026-05-10|2027-05-10|Đã tiêm đủ;4 bệnh mèo|2026-06-12|2027-06-12|Đã tiêm đủ', 2, CONVERT(date,'2026-06-12',23)),
    (N'TC000021', N'KH000021', N'Mập', N'Mèo', N'Ragdoll', N'Cái', N'Colorpoint', 4.9, N'Mắt xanh biếc, lông mềm như nhung', N'Không có dị ứng', N'images/pets/tc21.jpg', 1, CONVERT(date,'2022-10-18',23), N'Rất ngoan, bế là thả lỏng', N'Dại|2026-02-15|2027-02-15|Đã tiêm đủ;4 bệnh mèo|2026-03-20|2027-03-20|Đã tiêm đủ', 2, CONVERT(date,'2026-03-20',23)),
    (N'TC000022', N'KH000022', N'Lucky', N'Chó', N'Beagle', N'Đực', N'Tricolor', 11.2, N'Khứu giác cực nhạy, tai dài', N'Không có dị ứng', N'images/pets/tc22.jpg', 1, CONVERT(date,'2022-03-29',23), N'Tò mò, thích đánh hơi', N'Dại|2026-01-15|2027-01-15|Đã tiêm đủ;7 bệnh|2026-02-20|2027-02-20|Đã tiêm đủ', 2, CONVERT(date,'2026-02-20',23)),
    (N'TC000023', N'KH000023', N'Kẹo', N'Mèo', N'Mèo Ta', N'Cái', N'Tam thể', 3.1, N'Bắt chuột giỏi, hoạt bát', N'Không có dị ứng', N'images/pets/tc23.jpg', 1, CONVERT(date,'2023-04-14',23), N'Lanh lợi, tự lập', N'Dại|2026-06-05|2027-06-05|Đã tiêm đủ;4 bệnh mèo|2026-07-08|2027-07-08|Đã tiêm đủ', 2, CONVERT(date,'2026-07-08',23)),
    (N'TC000024', N'KH000024', N'Xúc Xích', N'Chó', N'Dachshund Mini', N'Đực', N'Nâu socola', 4.8, N'Nhanh nhẹn, mắt tinh', N'Không có dị ứng', N'images/pets/tc24.jpg', 1, CONVERT(date,'2023-01-08',23), N'Thích đào bới đồ chơi', N'Dại|2026-03-18|2027-03-18|Đã tiêm đủ;7 bệnh|2026-04-22|2027-04-22|Đã tiêm đủ', 2, CONVERT(date,'2026-04-22',23)),
    (N'TC000025', N'KH000025', N'Luna', N'Mèo', N'Bengal', N'Cái', N'Đốm hoa mai', 4.3, N'Lông gấm hoa mai tuyệt đẹp', N'Không có dị ứng', N'images/pets/tc25.jpg', 1, CONVERT(date,'2022-07-16',23), N'Nhanh nhẹn, thích leo trèo', N'Dại|2026-02-10|2027-02-10|Đã tiêm đủ;4 bệnh mèo|2026-03-15|2027-03-15|Đã tiêm đủ', 2, CONVERT(date,'2026-03-15',23)),
    (N'TC000026', N'KH000026', N'Bảo Bảo', N'Chó', N'Pomeranian', N'Đực', N'Vàng cam', 2.4, N'Lông xù như cục bông', N'Không có dị ứng', N'images/pets/tc26.jpg', 1, CONVERT(date,'2023-08-12',23), N'Đáng yêu, thích làm trò', N'Dại|2026-05-20|2027-05-20|Đã tiêm đủ;7 bệnh|2026-06-25|2027-06-25|Đã tiêm đủ', 2, CONVERT(date,'2026-06-25',23)),
    (N'TC000027', N'KH000027', N'Tomy', N'Mèo', N'Nga mắt xanh', N'Đực', N'Xám ánh bạc', 3.9, N'Mắt xanh lục bảo sang trọng', N'Không có dị ứng', N'images/pets/tc27.jpg', 1, CONVERT(date,'2022-11-03',23), N'Trầm mặc, tao nhã', N'Dại|2026-04-12|2027-04-12|Đã tiêm đủ;4 bệnh mèo|2026-05-18|2027-05-18|Đã tiêm đủ', 2, CONVERT(date,'2026-05-18',23)),
    (N'TC000028', N'KH000028', N'Snoopy', N'Chó', N'Bull Pháp', N'Đực', N'Trắng đen', 12.8, N'Cơ bắp cuồn cuộn, tai dơi', N'Không có dị ứng', N'images/pets/tc28.jpg', 1, CONVERT(date,'2021-09-20',23), N'Rất thích trẻ con, vui tính', N'Dại|2026-01-28|2027-01-28|Đã tiêm đủ;7 bệnh|2026-03-02|2027-03-02|Đã tiêm đủ', 2, CONVERT(date,'2026-03-02',23)),
    (N'TC000029', N'KH000029', N'Bông Gòn', N'Mèo', N'Chinchilla', N'Cái', N'Trắng bạc', 3.3, N'Lông tơ mỏng mịn, mắt viền đen', N'Không có dị ứng', N'images/pets/tc29.jpg', 1, CONVERT(date,'2023-05-01',23), N'Kiêu kỳ, thích được khen', N'Dại|2026-06-10|2027-06-10|Đã tiêm đủ;4 bệnh mèo|2026-07-15|2027-07-15|Đã tiêm đủ', 2, CONVERT(date,'2026-07-15',23)),
    (N'TC000030', N'KH000030', N'Max', N'Chó', N'Labrador', N'Đực', N'Đen tuyền', 28.0, N'Rất thông minh, biết bơi giỏi', N'Không có dị ứng', N'images/pets/tc30.jpg', 1, CONVERT(date,'2021-12-10',23), N'Tận tụy, nghe lời tuyệt đối', N'Dại|2026-02-12|2027-02-12|Đã tiêm đủ;7 bệnh|2026-03-18|2027-03-18|Đã tiêm đủ', 2, CONVERT(date,'2026-03-18',23));

-- 28. VOUCHER
INSERT INTO dbo.[voucher] ([ma_voucher], [ma_khach_hang], [menh_gia], [hieu_luc_den], [su_dung_luc], [chi_ap_dung_spa], [so_luong], [da_dung], [don_toi_thieu], [trang_thai], [khoa_doi_diem])
VALUES
    (N'VOUCHER000001', N'KH000001', 50000.00, DATEADD(day,60,CAST(@homNay AS datetime2)), DATEADD(day,-2,CAST(@homNay AS datetime2)), 0, 1, 1, 200000.00, N'DA_DUNG', N'DOI-DIEM-001'),
    (N'VOUCHER000002', N'KH000002', 100000.00, DATEADD(day,60,CAST(@homNay AS datetime2)), DATEADD(day,30,CAST(@homNay AS datetime2)), 1, 1, 0, 300000.00, N'CON_HAN', N'DOI-DIEM-002'),
    (N'VOUCHER000003', N'KH000003', 150000.00, DATEADD(day,60,CAST(@homNay AS datetime2)), DATEADD(day,45,CAST(@homNay AS datetime2)), 0, 1, 0, 500000.00, N'CON_HAN', N'DOI-DIEM-003'),
    (N'VOUCHER000004', N'KH000004', 50000.00, DATEADD(day,60,CAST(@homNay AS datetime2)), DATEADD(day,50,CAST(@homNay AS datetime2)), 1, 1, 0, 150000.00, N'CON_HAN', N'DOI-DIEM-004'),
    (N'VOUCHER000005', N'KH000005', 100000.00, DATEADD(day,60,CAST(@homNay AS datetime2)), DATEADD(day,55,CAST(@homNay AS datetime2)), 0, 1, 0, 250000.00, N'CON_HAN', N'DOI-DIEM-005');

-- 8. LỊCH LÀM VIỆC
INSERT INTO dbo.[lich_lam_viec] ([ma_nhan_vien], [ngay_lam], [ma_ca], [trang_thai], [loai])
VALUES
    (N'NV0001', DATEADD(day,-7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-7,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-7,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-7,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,-6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-6,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-6,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-6,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,-5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-5,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-5,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-5,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,-4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-4,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-4,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-4,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,-3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-3,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-3,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-3,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,-2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-2,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-2,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-2,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,-1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,-1,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,-1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,-1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,-1,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,-1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,-1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,-1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,-1,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,0,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,0,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,0,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,0,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,0,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,0,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,0,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,0,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,0,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,1,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,1,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,1,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,1,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,2,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,2,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,2,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,2,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,3,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,3,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,3,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,3,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,4,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,4,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,4,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,4,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,5,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,5,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,5,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,5,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,6,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,6,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,6,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,6,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0001', DATEADD(day,7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0002', DATEADD(day,7,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0003', DATEADD(day,7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0004', DATEADD(day,7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0005', DATEADD(day,7,@homNay), N'CA02', N'DANG_MO', N'DI_LAM'),
    (N'NV0008', DATEADD(day,7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0009', DATEADD(day,7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0010', DATEADD(day,7,@homNay), N'CA01', N'DANG_MO', N'DI_LAM'),
    (N'NV0011', DATEADD(day,7,@homNay), N'CA02', N'DANG_MO', N'DI_LAM');

-- 7. TÀI KHOẢN
INSERT INTO dbo.[tai_khoan] ([ma_nhan_vien], [ten_dang_nhap], [mat_khau_hash], [vai_tro], [quyen], [trang_thai], [so_lan_dang_nhap_sai], [khoa_den], [bat_buoc_doi_mat_khau], [dang_nhap_cuoi])
VALUES
    (N'NV0001', N'admin', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'QUAN_LY_CUA_HANG', N'XEM_TONG_QUAN;QUAN_LY_TAI_KHOAN;QUAN_LY_LICH_LAM_VIEC;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA;DUYET_GIA;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_KHUYEN_MAI;QUAN_LY_SPA;QUAN_LY_LUU_TRU;XU_LY_PHE_DUYET;XEM_NHAT_KY;XEM_BAO_CAO_CHUNG;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0002', N'vinhtq', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'QUAN_LY_CUA_HANG', N'XEM_TONG_QUAN;QUAN_LY_TAI_KHOAN;QUAN_LY_LICH_LAM_VIEC;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA;DUYET_GIA;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_KHUYEN_MAI;QUAN_LY_SPA;QUAN_LY_LUU_TRU;XU_LY_PHE_DUYET;XEM_NHAT_KY;XEM_BAO_CAO_CHUNG;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0003', N'ving', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_KHO', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0004', N'vyptt', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_BAN_HANG', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0005', N'dattq', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_SPA_HOTEL', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_SPA;QUAN_LY_LUU_TRU', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0006', N'tuanlm', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_BAN_HANG', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;XEM_BAO_CAO_CA', N'HOAT_DONG', 1, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0007', N'yendh', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_SPA_HOTEL', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_SPA;QUAN_LY_LUU_TRU', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0008', N'demo.admin', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'QUAN_LY_CUA_HANG', N'XEM_TONG_QUAN;QUAN_LY_TAI_KHOAN;QUAN_LY_LICH_LAM_VIEC;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA;DUYET_GIA;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_KHUYEN_MAI;QUAN_LY_SPA;QUAN_LY_LUU_TRU;XU_LY_PHE_DUYET;XEM_NHAT_KY;XEM_BAO_CAO_CHUNG;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0009', N'demo.sales', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_BAN_HANG', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;XEM_BAO_CAO_CA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0010', N'demo.stock', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_KHO', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_SAN_PHAM;QUAN_LY_NHA_CUNG_CAP;NHAP_KHO;XUAT_HUY_KHO;KIEM_KE_KHO;DE_XUAT_GIA', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2))),
    (N'NV0011', N'demo.spa', N'$2a$10$5HXwTpB7xQBI/z8J6tIvTefKZKocd6Yx0b7i8p0w1eyOKsfDa10fG', N'NHAN_VIEN_SPA_HOTEL', N'XEM_TONG_QUAN;XEM_LICH_LAM_VIEC;QUAN_LY_KHACH_HANG;QUAN_LY_THU_CUNG;BAN_HANG;THANH_TOAN;QUAN_LY_CA_THU_NGAN;QUAN_LY_SPA;QUAN_LY_LUU_TRU', N'HOAT_DONG', 0, CONVERT(datetime2,'2020-01-01 00:00:00',120), 0, DATEADD(minute,-15,CAST(@homNay AS datetime2)));

-- 11. LÔ HÀNG
INSERT INTO dbo.[lo_hang] ([ma_lo], [ma_san_pham], [so_lo], [ngay_san_xuat], [ngay_nhap], [han_su_dung], [gia_nhap], [so_luong_nhap], [so_luong_ton], [ngay_kiem_ke], [trang_thai])
VALUES
    (N'LO00000001', N'SP000001', N'LOTE0001', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000002', N'SP000002', N'LOTE0002', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000003', N'SP000003', N'LOTE0003', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000004', N'SP000004', N'LOTE0004', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000005', N'SP000005', N'LOTE0005', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000006', N'SP000006', N'LOTE0006', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000007', N'SP000007', N'LOTE0007', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000008', N'SP000008', N'LOTE0008', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000009', N'SP000009', N'LOTE0009', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000010', N'SP000010', N'LOTE0010', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000011', N'SP000011', N'LOTE0011', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000012', N'SP000012', N'LOTE0012', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000013', N'SP000013', N'LOTE0013', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000014', N'SP000014', N'LOTE0014', CONVERT(date,'2025-01-15',23), CONVERT(date,'2025-01-20',23), CONVERT(date,'2028-12-31',23), 50000.00, 100, 85, CONVERT(date,'2026-10-15',23), N'SAN_SANG'),
    (N'LO00000016', N'SP000015', N'LO00000016', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 104000.00, 10, 9, @homNay, N'SAN_SANG'),
    (N'LO00000017', N'SP000016', N'LO00000017', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 6500.00, 10, 9, @homNay, N'SAN_SANG'),
    (N'LO00000018', N'SP000017', N'LO00000018', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 146250.00, 12, 12, @homNay, N'SAN_SANG'),
    (N'LO00000019', N'SP000018', N'LO00000019', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 97500.00, 12, 12, @homNay, N'SAN_SANG'),
    (N'LO00000020', N'SP000019', N'LO00000020', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 12, 12, @homNay, N'SAN_SANG'),
    (N'LO00000021', N'SP000020', N'LO00000021', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 84500.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000022', N'SP000021', N'LO00000022', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000023', N'SP000022', N'LO00000023', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 104000.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000024', N'SP000023', N'LO00000024', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 81250.00, 18, 18, @homNay, N'SAN_SANG'),
    (N'LO00000025', N'SP000024', N'LO00000025', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 18, 18, @homNay, N'SAN_SANG'),
    (N'LO00000026', N'SP000025', N'LO00000026', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 3250.00, 18, 18, @homNay, N'SAN_SANG'),
    (N'LO00000027', N'SP000026', N'LO00000027', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 21, 21, @homNay, N'SAN_SANG'),
    (N'LO00000028', N'SP000027', N'LO00000028', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 22750.00, 21, 21, @homNay, N'SAN_SANG'),
    (N'LO00000029', N'SP000028', N'LO00000029', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 21, 21, @homNay, N'SAN_SANG'),
    (N'LO00000030', N'SP000029', N'LO00000030', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 120250.00, 24, 24, @homNay, N'SAN_SANG'),
    (N'LO00000031', N'SP000030', N'LO00000031', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 24, 24, @homNay, N'SAN_SANG'),
    (N'LO00000032', N'SP000031', N'LO00000032', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 27, 27, @homNay, N'SAN_SANG'),
    (N'LO00000033', N'SP000032', N'LO00000033', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 19500.00, 28, 28, @homNay, N'SAN_SANG'),
    (N'LO00000034', N'SP000033', N'LO00000034', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 13000.00, 29, 29, @homNay, N'SAN_SANG'),
    (N'LO00000035', N'SP000034', N'LO00000035', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 91000.00, 29, 29, @homNay, N'SAN_SANG'),
    (N'LO00000036', N'SP000035', N'LO00000036', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 16250.00, 10, 10, @homNay, N'SAN_SANG'),
    (N'LO00000037', N'SP000036', N'LO00000037', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 84500.00, 11, 11, @homNay, N'SAN_SANG'),
    (N'LO00000038', N'SP000037', N'LO00000038', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 12, 12, @homNay, N'SAN_SANG'),
    (N'LO00000039', N'SP000038', N'LO00000039', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 74750.00, 13, 13, @homNay, N'SAN_SANG'),
    (N'LO00000040', N'SP000039', N'LO00000040', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 204750.00, 14, 14, @homNay, N'SAN_SANG'),
    (N'LO00000041', N'SP000040', N'LO00000041', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 22750.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000042', N'SP000041', N'LO00000042', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 13000.00, 16, 16, @homNay, N'SAN_SANG'),
    (N'LO00000043', N'SP000042', N'LO00000043', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 19500.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000044', N'SP000043', N'LO00000044', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 18, 18, @homNay, N'SAN_SANG'),
    (N'LO00000045', N'SP000044', N'LO00000045', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 32500.00, 17, 17, @homNay, N'SAN_SANG'),
    (N'LO00000046', N'SP000045', N'LO00000046', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 117000.00, 20, 20, @homNay, N'SAN_SANG'),
    (N'LO00000047', N'SP000046', N'LO00000047', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 162500.00, 21, 21, @homNay, N'SAN_SANG'),
    (N'LO00000048', N'SP000047', N'LO00000048', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 6500.00, 22, 22, @homNay, N'SAN_SANG'),
    (N'LO00000049', N'SP000048', N'LO00000049', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 104000.00, 23, 23, @homNay, N'SAN_SANG'),
    (N'LO00000050', N'SP000049', N'LO00000050', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 55250.00, 22, 22, @homNay, N'SAN_SANG'),
    (N'LO00000051', N'SP000050', N'LO00000051', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 100750.00, 25, 25, @homNay, N'SAN_SANG'),
    (N'LO00000052', N'SP000051', N'LO00000052', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 45500.00, 26, 26, @homNay, N'SAN_SANG'),
    (N'LO00000053', N'SP000052', N'LO00000053', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 29250.00, 27, 27, @homNay, N'SAN_SANG'),
    (N'LO00000054', N'SP000053', N'LO00000054', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 22750.00, 26, 26, @homNay, N'SAN_SANG'),
    (N'LO00000055', N'SP000054', N'LO00000055', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 39000.00, 29, 29, @homNay, N'SAN_SANG'),
    (N'LO00000056', N'SP000055', N'LO00000056', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 143000.00, 10, 3, @homNay, N'SAN_SANG'),
    (N'LO00000057', N'SP000056', N'LO00000057', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 19500.00, 10, 9, @homNay, N'SAN_SANG'),
    (N'LO00000058', N'SP000057', N'LO00000058', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 48750.00, 11, 11, @homNay, N'SAN_SANG'),
    (N'LO00000059', N'SP000058', N'LO00000059', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 162500.00, 12, 12, @homNay, N'SAN_SANG'),
    (N'LO00000060', N'SP000059', N'LO00000060', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 32500.00, 13, 13, @homNay, N'SAN_SANG'),
    (N'LO00000061', N'SP000060', N'LO00000061', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 52000.00, 14, 14, @homNay, N'SAN_SANG'),
    (N'LO00000062', N'SP000061', N'LO00000062', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 32500.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000063', N'SP000062', N'LO00000063', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 97500.00, 16, 16, @homNay, N'SAN_SANG'),
    (N'LO00000064', N'SP000063', N'LO00000064', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 26000.00, 17, 17, @homNay, N'SAN_SANG'),
    (N'LO00000065', N'SP000064', N'LO00000065', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 45500.00, 18, 18, @homNay, N'SAN_SANG'),
    (N'LO00000066', N'SP000065', N'LO00000066', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 97500.00, 19, 19, @homNay, N'SAN_SANG'),
    (N'LO00000067', N'SP000066', N'LO00000067', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 35750.00, 20, 20, @homNay, N'SAN_SANG'),
    (N'LO00000068', N'SP000067', N'LO00000068', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 26000.00, 21, 21, @homNay, N'SAN_SANG'),
    (N'LO00000069', N'SP000068', N'LO00000069', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,8,@homNay), 162500.00, 22, 22, @homNay, N'SAN_SANG'),
    (N'LO00000070', N'SP000069', N'LO00000070', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,173,@homNay), 110500.00, 23, 23, @homNay, N'SAN_SANG'),
    (N'LO00000071', N'SP000070', N'LO00000071', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 126750.00, 24, 24, @homNay, N'SAN_SANG'),
    (N'LO00000072', N'SP000071', N'LO00000072', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 52000.00, 25, 25, @homNay, N'SAN_SANG'),
    (N'LO00000073', N'SP000072', N'LO00000073', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 39000.00, 26, 26, @homNay, N'SAN_SANG'),
    (N'LO00000074', N'SP000073', N'LO00000074', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 32500.00, 27, 27, @homNay, N'SAN_SANG'),
    (N'LO00000075', N'SP000074', N'LO00000075', DATEADD(day,-30,@homNay), @homNay, DATEADD(day,365,@homNay), 22750.00, 28, 28, @homNay, N'SAN_SANG'),
    (N'LO00000076', N'SP000075', N'LO00000076', DATEADD(day,-20,@homNay), @homNay, DATEADD(day,730,@homNay), 310000.00, 10, 10, @homNay, N'SAN_SANG'),
    (N'LO00000077', N'SP000076', N'LO00000077', DATEADD(day,-20,@homNay), @homNay, DATEADD(day,730,@homNay), 240000.00, 15, 15, @homNay, N'SAN_SANG'),
    (N'LO00000078', N'SP000077', N'LO00000078', DATEADD(day,-20,@homNay), @homNay, DATEADD(day,730,@homNay), 190000.00, 20, 20, @homNay, N'SAN_SANG'),
    (N'LO00000079', N'SP000080', N'LO00000079', DATEADD(day,-20,@homNay), @homNay, DATEADD(day,365,@homNay), 180000.00, 30, 30, @homNay, N'SAN_SANG'),
    (N'LO00000080', N'SP000081', N'LO00000080', DATEADD(day,-20,@homNay), @homNay, DATEADD(day,365,@homNay), 6500.00, 100, 100, @homNay, N'SAN_SANG');

-- 25. LỊCH HẸN SPA
INSERT INTO dbo.[lich_hen] ([ma_lich_hen], [ma_khach_hang], [ma_thu_cung], [loai_dich_vu], [bat_dau], [ket_thuc], [trang_thai], [ghi_chu], [ma_nhan_vien], [ma_goi])
VALUES
    (N'LH00000001', N'KH000001', N'TC000001', N'SPA', DATEADD(day,-10,CAST(@homNay AS datetime2)), DATEADD(day,-10,DATEADD(minute,45,CAST(@homNay AS datetime2))), N'HOAN_TAT', N'Tắm và sấy khô bé Poodle', N'NV0005', N'SPA00000001'),
    (N'LH00000002', N'KH000002', N'TC000002', N'SPA', DATEADD(day,-9,CAST(@homNay AS datetime2)), DATEADD(day,-9,DATEADD(minute,75,CAST(@homNay AS datetime2))), N'HOAN_TAT', N'Cắt tỉa tạo kiểu lông mèo', N'NV0005', N'SPA00000002'),
    (N'LH00000003', N'KH000003', N'TC000003', N'SPA', DATEADD(day,-8,CAST(@homNay AS datetime2)), DATEADD(day,-8,DATEADD(minute,30,CAST(@homNay AS datetime2))), N'HOAN_TAT', N'Vệ sinh tai móng bé Corgi', N'NV0007', N'SPA00000003'),
    (N'LH00000004', N'KH000004', N'TC000004', N'SPA', DATEADD(day,-6,CAST(@homNay AS datetime2)), DATEADD(day,-6,DATEADD(minute,120,CAST(@homNay AS datetime2))), N'HOAN_TAT', N'Combo Spa toàn diện VIP cho Phốc sóc', N'NV0005', N'SPA00000004'),
    (N'LH00000005', N'KH000005', N'TC000005', N'SPA', DATEADD(day,-5,CAST(@homNay AS datetime2)), DATEADD(day,-5,DATEADD(minute,60,CAST(@homNay AS datetime2))), N'HOAN_TAT', N'Tắm trị liệu nấm cho mèo Ba Tư', N'NV0011', N'SPA00000005'),
    (N'LH00000006', N'KH000006', N'TC000006', N'SPA', DATEADD(day,-2,CAST(@homNay AS datetime2)), DATEADD(day,-2,DATEADD(minute,60,CAST(@homNay AS datetime2))), N'HOAN_TAT', N'Tắm sục khoáng Onsen cho Golden', N'NV0007', N'SPA00000008'),
    (N'LH00000007', N'KH000007', N'TC000007', N'SPA', DATEADD(hour,9,CAST(@homNay AS datetime2)), DATEADD(hour,10,CAST(@homNay AS datetime2)), N'DANG_THUC_HIEN', N'Gỡ rối lông và ủ collagen cho mèo Munchkin', N'NV0005', N'SPA00000006'),
    (N'LH00000008', N'KH000008', N'TC000008', N'SPA', DATEADD(hour,14,CAST(@homNay AS datetime2)), DATEADD(hour,15,CAST(@homNay AS datetime2)), N'DA_XAC_NHAN', N'Tắm sấy chuyên sâu cho Husky', N'NV0007', N'SPA00000001'),
    (N'LH00000009', N'KH000009', N'TC000009', N'SPA', DATEADD(day,1,DATEADD(hour,10,CAST(@homNay AS datetime2))), DATEADD(day,1,DATEADD(hour,11,CAST(@homNay AS datetime2))), N'DA_XAC_NHAN', N'Vệ sinh tai và cắt móng cho mèo Xiêm', N'NV0011', N'SPA00000003'),
    (N'LH00000010', N'KH000010', N'TC000010', N'SPA', DATEADD(day,2,DATEADD(hour,15,CAST(@homNay AS datetime2))), DATEADD(day,2,DATEADD(hour,17,CAST(@homNay AS datetime2))), N'DA_XAC_NHAN', N'Combo Spa VIP cho Shiba Inu', N'NV0005', N'SPA00000004');

-- 14. BIÊN BẢN KIỂM KÊ
INSERT INTO dbo.[bien_ban_kiem_ke] ([ma_bien_ban], [ten_dang_nhap], [ky_kiem_ke], [trang_thai], [tao_luc])
VALUES
    (N'KK00000001', N'ving', N'Đợt kiểm kê định kỳ tháng 10/2026', N'DA_DUYET', DATEADD(day,-10,CAST(@homNay AS datetime2))),
    (N'KK00000002', N'demo.stock', N'Đợt kiểm kê đầu tháng 11/2026', N'DA_DUYET', DATEADD(day,-5,CAST(@homNay AS datetime2))),
    (N'KK00000003', N'ving', N'Kiểm kê đột xuất nhóm thức ăn hạt', N'CHO_DUYET', DATEADD(day,-1,CAST(@homNay AS datetime2))),
    (N'KK00000004', N'ving', N'Kiểm kê nhóm phụ kiện & vệ sinh', N'DANG_KIEM', DATEADD(hour,-3,CAST(@homNay AS datetime2)));

-- 13. CÀI ĐẶT HỆ THỐNG
INSERT INTO dbo.[cai_dat] ([ma_cai_dat], [gia_tri], [ten_dang_nhap], [ngay_cap_nhat])
VALUES
    (N'CANH_BAO_TON_THAP', N'true', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'CANH_BAO_DON_MOI', N'true', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'CANH_BAO_SPA', N'true', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'SO_TAI_KHOAN_NGAN_HANG', N'1029384756', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TEN_CHU_TAI_KHOAN', N'HUYNH TAN NHANG', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TEN_NGAN_HANG', N'Vietcombank', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'BANK_BIN', N'970436', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'BANK_ACCOUNT', N'1029384756', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'BANK_ACCOUNT_NAME', N'HUYNH TAN NHANG', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_NAME', N'Pet Station - Hệ thống Chăm sóc Thú cưng Toàn diện', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_ADDRESS', N'123 Phan Văn Trị, Phường 10, Quận Gò Vấp, TP.HCM', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'STORE_PHONE', N'02838256789', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_NGAY_DOI_TRA', N'3', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'RETURN_WINDOW_DAYS', N'3', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'DIA_CHI_CUA_HANG', N'123 Phan Văn Trị, Phường 10, Quận Gò Vấp, TP.HCM', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'EMAIL_CUA_HANG', N'contact@petstation.vn', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'TEN_CUA_HANG', N'Pet Station - Hệ thống Chăm sóc Thú cưng Toàn diện', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_DIEN_THOAI_CUA_HANG', N'02838256789', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MA_SO_THUE', N'0316889900', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'WEBSITE_CUA_HANG', N'https://petstation.vn', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MUC_CHI_TIEU_HANG', N'2000000,10000000,25000000,50000000', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'MUI_GIO', N'Asia/Ho_Chi_Minh', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'THUE_VAT_PHAN_TRAM', N'8', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_TIEN_MOI_DIEM_TICH', N'10000', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'SO_TIEN_MOI_DIEM_DOI', N'1000', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2))),
    (N'HAN_MUC_XUAT_HUY_CAN_DUYET', N'500000', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_NGAY_TON_DONG', N'45', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MAC_DINH_KIEM_KE_MU', N'false', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_GIO_TOI_DA_MOI_TUAN', N'40', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_GIO_BAO_TRUOC_DOI_CA', N'12', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'THU_MUC_ANH_CHAM_SOC', N'images/care', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'NGUONG_TON_KHO_THAP', N'10', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'SO_NGAY_CANH_BAO_HET_HAN', N'30', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'PHI_TRA_PHONG_TRE', N'100000', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MAY_IN', N'Microsoft Print to PDF', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'MA_BIN_NGAN_HANG', N'970436', N'admin', DATEADD(second,-483838,CAST(@homNay AS datetime2))),
    (N'PHIEN_BAN_CSDL', N'20261106', N'admin', DATEADD(second,-555362,CAST(@homNay AS datetime2)));

-- 33. NHẬT KÝ HỆ THỐNG
INSERT INTO dbo.[nhat_ky_he_thong] ([ma_nhat_ky], [ten_dang_nhap], [hanh_dong], [loai_doi_tuong], [ma_doi_tuong], [gia_tri_cu], [gia_tri_moi], [ghi_chu], [thoi_gian], [nguoi_dung], [loai_su_kien], [ket_qua])
VALUES
    (N'LOG00000001', N'admin', N'Đăng nhập hệ thống', N'HỆ THỐNG', N'HT01', N'Chưa đăng nhập', N'Đã đăng nhập', N'Huỳnh Tấn Nhàng đăng nhập thành công vai trò Cửa hàng trưởng', DATEADD(hour,-4,SYSDATETIME()), N'Huỳnh Tấn Nhàng', N'LOGIN', N'Thành công'),
    (N'LOG00000002', N'vinhtq', N'Mở ca thu ngân', N'PHIÊN LÀM VIỆC', N'MPI00000035', N'0', N'2000000', N'Trần Quang Vinh mở ca với số dư đầu ca 2,000,000 VND', DATEADD(hour,-3,SYSDATETIME()), N'Trần Quang Vinh', N'SYSTEM', N'Thành công'),
    (N'LOG00000003', N'vyptt', N'Lập hóa đơn thanh toán', N'HÓA ĐƠN', N'HD2610190001', N'Tạo mới', N'Đã thanh toán', N'Phan Trần Thảo Vy lập hóa đơn bán lẻ thành công', DATEADD(hour,-2,SYSDATETIME()), N'Phan Trần Thảo Vy', N'PAYMENT', N'Thành công'),
    (N'LOG00000004', N'ving', N'Nhập kho sản phẩm', N'PHIẾU NHẬP', N'PNK00000001', N'Chờ nhập', N'Đã nhập kho', N'Nguyễn Gia Vỉ hoàn tất nhập lô hàng thức ăn Royal Canin', DATEADD(hour,-1,SYSDATETIME()), N'Nguyễn Gia Vỉ', N'INVENTORY', N'Thành công'),
    (N'LOG00000005', N'dattq', N'Tiếp nhận dịch vụ Spa', N'PHIẾU SPA', N'SPA00000001', N'Chờ tiếp nhận', N'Đang thực hiện', N'Trần Quang Đạt tiếp nhận bé Poodle thực hiện gói tắm sấy', DATEADD(minute,-30,SYSDATETIME()), N'Trần Quang Đạt', N'CREATE', N'Thành công'),
    (N'LOG00000006', N'ving', N'Lập phiếu xuất hủy vượt hạn mức', N'PHIẾU XUẤT', N'PXK00000004', N'Tạo mới', N'Chờ duyệt', N'Phiếu xuất hủy vượt hạn mức tự duyệt, chuyển Quản lý phê duyệt', DATEADD(minute,-20,SYSDATETIME()), N'Nguyễn Gia Vỉ', N'INVENTORY', N'Cảnh báo'),
    (N'LOG00000007', N'tuanlm', N'Đăng nhập sai mật khẩu', N'HỆ THỐNG', N'HT02', N'Chưa đăng nhập', N'Từ chối', N'Nhập sai mật khẩu lần 1 trên máy quầy thu ngân', DATEADD(minute,-15,SYSDATETIME()), N'Lê Minh Tuấn', N'LOGIN', N'Thất bại');

-- 16. PHIÊN LÀM VIỆC
INSERT INTO dbo.[phien_lam_viec] ([ma_phien], [ma_ca], [ten_dang_nhap], [mo_luc], [dong_luc], [tien_dau_ca], [tien_thuc_te], [chenh_lech], [giai_trinh], [trang_thai], [ten_ca], [nguoi_nhan_ban_giao], [hinh_thuc_ban_giao])
VALUES
    (N'MPI00000001', N'CA01', N'vinhtq', DATEADD(day,-34,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-34,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 10000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000002', N'CA02', N'admin', DATEADD(day,-33,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-33,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000003', N'CA01', N'vyptt', DATEADD(day,-32,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-32,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 5000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000004', N'CA02', N'vinhtq', DATEADD(day,-31,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-31,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000005', N'CA01', N'admin', DATEADD(day,-30,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-30,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000006', N'CA02', N'vyptt', DATEADD(day,-29,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-29,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000007', N'CA01', N'vinhtq', DATEADD(day,-28,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-28,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000008', N'CA02', N'admin', DATEADD(day,-27,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-27,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000009', N'CA01', N'vyptt', DATEADD(day,-26,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-26,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000010', N'CA02', N'vinhtq', DATEADD(day,-25,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-25,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000011', N'CA01', N'admin', DATEADD(day,-24,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-24,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000012', N'CA02', N'vyptt', DATEADD(day,-23,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-23,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000013', N'CA01', N'vinhtq', DATEADD(day,-22,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-22,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000014', N'CA02', N'admin', DATEADD(day,-21,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-21,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000015', N'CA01', N'vyptt', DATEADD(day,-20,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-20,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000016', N'CA02', N'vinhtq', DATEADD(day,-19,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-19,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000017', N'CA01', N'admin', DATEADD(day,-18,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-18,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000018', N'CA02', N'vyptt', DATEADD(day,-17,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-17,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000019', N'CA01', N'vinhtq', DATEADD(day,-16,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-16,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000020', N'CA02', N'admin', DATEADD(day,-15,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-15,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000021', N'CA01', N'vyptt', DATEADD(day,-14,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-14,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000022', N'CA02', N'vinhtq', DATEADD(day,-13,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-13,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 0.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000023', N'CA01', N'admin', DATEADD(day,-12,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-12,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 15000.00, N'Khách boa thêm tiền lẻ tại quầy', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000024', N'CA02', N'vyptt', DATEADD(day,-11,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-11,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 20000.00, N'Khách không lấy tiền lẻ thối lại', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000025', N'CA01', N'vinhtq', DATEADD(day,-10,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-10,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 10000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000026', N'CA02', N'admin', DATEADD(day,-9,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-9,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 25000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000027', N'CA01', N'vyptt', DATEADD(day,-8,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-8,CAST(@homNay AS datetime2))), 1000000.00, 1500000.00, 15000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000028', N'CA02', N'vinhtq', DATEADD(day,-7,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-7,CAST(@homNay AS datetime2))), 1000000.00, 1450000.00, 20000.00, N'Khách làm tròn tiền mặt', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000029', N'CA01', N'admin', DATEADD(day,-6,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-6,CAST(@homNay AS datetime2))), 1000000.00, 1465000.00, 25000.00, N'Khách làm tròn tiền mặt', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000030', N'CA02', N'vyptt', DATEADD(day,-5,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-5,CAST(@homNay AS datetime2))), 1000000.00, 1480000.00, 30000.00, N'Tiền lẻ khách gửi tip', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000031', N'CA01', N'vinhtq', DATEADD(day,-4,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-4,CAST(@homNay AS datetime2))), 1000000.00, 1475000.00, 15000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000032', N'CA02', N'admin', DATEADD(day,-3,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-3,CAST(@homNay AS datetime2))), 1000000.00, 1510000.00, 40000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000033', N'CA01', N'vyptt', DATEADD(day,-2,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-2,CAST(@homNay AS datetime2))), 1000000.00, 1515000.00, 35000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO'),
    (N'MPI00000034', N'CA02', N'vinhtq', DATEADD(day,-1,CAST(@homNay AS datetime2)), DATEADD(hour,8,DATEADD(day,-1,CAST(@homNay AS datetime2))), 1000000.00, 1510000.00, 20000.00, N'Bàn giao đầy đủ hóa đơn chứng từ', N'DA_BAN_GIAO', N'Ca chiều', N'admin', N'BAN_GIAO'),
    (N'MPI00000035', N'CA01', N'dattq', DATEADD(hour,-6,CAST(@homNay AS datetime2)), DATEADD(hour,-1,CAST(@homNay AS datetime2)), 2000000.00, 2465000.00, 15000.00, N'Đã chốt ca sáng và bàn giao quỹ', N'DA_BAN_GIAO', N'Ca sáng', N'admin', N'BAN_GIAO');

-- 19. PHIẾU NHẬP KHO
INSERT INTO dbo.[phieu_nhap_kho] ([ma_phieu_nhap], [ma_nha_cung_cap], [ten_dang_nhap], [ngay_nhap], [tong_tien], [trang_thai], [ghi_chu], [da_tra])
VALUES
    (N'PNK00000001', N'NCC0001', N'ving', DATEADD(day,-25,CAST(@homNay AS datetime2)), 18500000.00, N'DA_NHAP', N'Nhập định kỳ thức ăn Royal Canin các loại', 10000000.00),
    (N'PNK00000002', N'NCC0002', N'demo.stock', DATEADD(day,-20,CAST(@homNay AS datetime2)), 12900000.00, N'DA_NHAP', N'Nhập bổ sung pate Whiskas và bánh thưởng', 6900000.00),
    (N'PNK00000003', N'NCC0003', N'ving', DATEADD(day,-14,CAST(@homNay AS datetime2)), 14500000.00, N'DA_NHAP', N'Nhập khẩu thức ăn hạt Orijen & Acana', 8500000.00),
    (N'PNK00000004', N'NCC0004', N'ving', DATEADD(day,-10,CAST(@homNay AS datetime2)), 9600000.00, N'DA_NHAP', N'Nhập vắc-xin và thuốc xịt ve rận', 4000000.00),
    (N'PNK00000005', N'NCC0005', N'demo.stock', DATEADD(day,-6,CAST(@homNay AS datetime2)), 11200000.00, N'DA_NHAP', N'Nhập pate tươi đóng hộp cao cấp cho chó mèo', 5000000.00),
    (N'PNK00000006', N'NCC0006', N'demo.stock', DATEADD(day,-5,CAST(@homNay AS datetime2)), 16800000.00, N'DA_NHAP', N'Nhập phụ kiện, vòng cổ và nệm thú cưng', 7800000.00),
    (N'PNK00000007', N'NCC0007', N'ving', DATEADD(day,-4,CAST(@homNay AS datetime2)), 8400000.00, N'DA_NHAP', N'Nhập dây dắt, yếm ngực và vòng cổ da bò', 3400000.00),
    (N'PNK00000008', N'NCC0008', N'ving', DATEADD(day,-3,CAST(@homNay AS datetime2)), 13500000.00, N'DA_NHAP', N'Nhập cát vệ sinh hữu cơ và khay MAKAR', 6000000.00),
    (N'PNK00000009', N'NCC0009', N'demo.stock', DATEADD(day,-2,CAST(@homNay AS datetime2)), 10500000.00, N'DA_NHAP', N'Nhập sữa tắm Joyce & Dolls và dầu xả dưỡng lông', 4500000.00),
    (N'PNK00000010', N'NCC0010', N'ving', DATEADD(day,-1,CAST(@homNay AS datetime2)), 19500000.00, N'DA_NHAP', N'Nhập máy sấy lông công suất lớn và bàn cắt tỉa', 9500000.00);

-- 21. PHIẾU XUẤT KHO
INSERT INTO dbo.[phieu_xuat_kho] ([ma_phieu_xuat], [ten_dang_nhap], [loai_xuat], [ly_do], [tong_gia_tri], [trang_thai], [tao_luc])
VALUES
    (N'PXK00000001', N'ving', N'SU_DUNG_NOI_BO', N'Xuất sữa tắm dùng nội bộ cho dịch vụ Spa', 315000.00, N'DA_XUAT', DATEADD(day,-12,CAST(@homNay AS datetime2))),
    (N'PXK00000002', N'demo.stock', N'XUAT_HUY', N'Xuất hủy gói thức ăn rách mép trong lúc vận chuyển', 91000.00, N'DA_XUAT', DATEADD(day,-8,CAST(@homNay AS datetime2))),
    (N'PXK00000003', N'ving', N'SU_DUNG_NOI_BO', N'Xuất khăn tắm và găng tay vệ sinh dùng cho khách sạn thú cưng', 180000.00, N'DA_XUAT', DATEADD(day,-3,CAST(@homNay AS datetime2))),
    (N'PXK00000004', N'ving', N'XUAT_HUY', N'Xuất hủy 6 gói hạt Royal Canin bị ẩm mốc do thấm nước mưa kho phụ', 546000.00, N'CHO_XUAT', DATEADD(hour,-5,CAST(@homNay AS datetime2))),
    (N'PXK00000005', N'demo.stock', N'XUAT_HUY', N'Xuất hủy 4 chai sữa tắm bị nứt vòi bơm khi bốc dỡ', 650000.00, N'CHO_XUAT', DATEADD(hour,-2,CAST(@homNay AS datetime2)));

-- 29. YÊU CẦU PHÊ DUYỆT
INSERT INTO dbo.[yeu_cau_phe_duyet] ([ma_yeu_cau], [loai], [ten_dang_nhap_gui], [loai_doi_tuong], [ma_doi_tuong], [ly_do], [trang_thai], [ten_dang_nhap_xu_ly], [phan_hoi], [gui_luc], [xu_ly_luc], [noi_dung_truoc], [noi_dung_sau], [payload])
VALUES
    (N'YC00000001', N'GIA_BAN', N'ving', N'DOI_GIA', N'SP000009', N'Đề xuất tăng giá bán vòng cổ da bò do nhà cung cấp tăng giá nhập', N'DA_DUYET', N'admin', N'Đồng ý phê duyệt áp dụng giá mới từ tuần tới', DATEADD(day,-5,CAST(@homNay AS datetime2)), DATEADD(day,-4,CAST(@homNay AS datetime2)), N'75,000 đ', N'80,000 đ', N'80000'),
    (N'YC00000002', N'XUAT_HUY', N'ving', N'XUAT_HUY', N'PXK00000002', N'Xuất hủy gói thức ăn rách mép trong lúc dỡ hàng pallet', N'DA_DUYET', N'admin', N'Đã xác minh hiện trạng hàng lỗi, đồng ý xuất hủy hạch toán chi phí', DATEADD(day,-8,CAST(@homNay AS datetime2)), DATEADD(day,-8,CAST(@homNay AS datetime2)), N'Tồn kho 86 gói', N'Xuất hủy 1 gói (91,000 đ)', N'PXK00000002'),
    (N'YC00000003', N'DOI_TRA', N'vyptt', N'DOI_TRA', N'DT00000001', N'Khách muốn đổi kích thước vòng cổ Hand in Hand từ S sang M', N'DA_DUYET', N'vinhtq', N'Đã kiểm tra hàng còn nguyên tem mác, cho phép đổi phụ kiện', DATEADD(day,-2,CAST(@homNay AS datetime2)), DATEADD(day,-2,CAST(@homNay AS datetime2)), N'HD2610190001 - SP000001', N'Đổi sang SP000002 (+10,000 đ)', N'DT00000001'),
    (N'YC00000004', N'SUA_HOA_DON', N'vyptt', N'HUY_HOA_DON', N'HD2610190010', N'Khách báo nhầm số lượng sau khi in hóa đơn', N'TU_CHOI', N'admin', N'Khách đã nhận hàng rời quầy, hướng dẫn làm phiếu đổi trả theo quy định', DATEADD(day,-3,CAST(@homNay AS datetime2)), DATEADD(day,-3,CAST(@homNay AS datetime2)), N'HD2610190010 (250,000 đ)', N'Hủy hóa đơn', N''),
    (N'YC00000005', N'GIA_BAN', N'ving', N'DOI_GIA', N'SP000042', N'Điều chỉnh giá bán bóng thừng BOBO theo bảng giá mới của nhà cung cấp', N'CHO_DUYET', NULL, NULL, DATEADD(hour,-6,CAST(@homNay AS datetime2)), NULL, N'30,000 đ', N'35,000 đ', N'35000'),
    (N'YC00000006', N'GIA_BAN', N'demo.stock', N'DOI_GIA', N'SP000041', N'Đề xuất điều chỉnh giá bóng nhựa gai cao su phát tiếng kêu cho chó', N'CHO_DUYET', NULL, NULL, DATEADD(hour,-5,CAST(@homNay AS datetime2)), NULL, N'25,000 đ', N'30,000 đ', N'30000'),
    (N'YC00000007', N'XUAT_HUY', N'ving', N'XUAT_HUY', N'PXK00000004', N'Xuất hủy 6 gói hạt Royal Canin Fit 32 bị ẩm mốc do thấm nước mưa', N'CHO_DUYET', NULL, NULL, DATEADD(hour,-4,CAST(@homNay AS datetime2)), NULL, N'Lô LO00000001 (Tồn: 85)', N'Xuất hủy 6 gói (546,000 đ)', N'PXK00000004'),
    (N'YC00000008', N'XUAT_HUY', N'demo.stock', N'XUAT_HUY', N'PXK00000005', N'Xuất hủy 4 chai sữa tắm Joyce & Dolls bị nứt vòi bơm khi vận chuyển', N'CHO_DUYET', NULL, NULL, DATEADD(hour,-3,CAST(@homNay AS datetime2)), NULL, N'Lô LO00000059 (Tồn: 12)', N'Xuất hủy 4 chai (650,000 đ)', N'PXK00000005'),
    (N'YC00000009', N'KIEM_KE', N'ving', N'KIEM_KE', N'KK00000003', N'Xin duyệt cân bằng kho biên bản kiểm kê đột xuất nhóm thức ăn hạt', N'CHO_DUYET', NULL, NULL, DATEADD(hour,-2,CAST(@homNay AS datetime2)), NULL, N'Tồn hệ thống: 85 gói', N'Tồn thực tế: 83 gói (Lệch -2)', N'KK00000003'),
    (N'YC00000010', N'SUA_HOA_DON', N'vyptt', N'GHI_CHU_HOA_DON', N'HD2610190033', N'Khách hàng Phạm Minh Đức yêu cầu bổ sung thông tin xuất hóa đơn VAT công ty', N'CHO_DUYET', NULL, NULL, DATEADD(hour,-1,CAST(@homNay AS datetime2)), NULL, N'HD2610190033', N'Bổ sung MST 0314455667 - Công ty TNHH Minh Đức', N'Bổ sung MST 0314455667 - Công ty TNHH Minh Đức'),
    (N'YC00000011', N'NGHI_PHEP', N'dattq', N'NGHI_PHEP', N'NV0005', N'Xin nghỉ phép 1 ngày để giải quyết việc gia đình tại quê', N'CHO_DUYET', NULL, NULL, DATEADD(minute,-30,CAST(@homNay AS datetime2)), NULL, N'Ca chiều (CA02)', N'Xin nghỉ phép có bàn giao ca', CONVERT(nvarchar(10), DATEADD(day,3,@homNay), 23));

-- 32. PHIẾU SPA TREO
INSERT INTO dbo.[phieu_spa_treo] ([ma_phieu], [ten_dang_nhap], [ma_khach], [so_dien_thoai], [ma_thu], [ma_goi], [ma_hen], [can_nang], [tinh_trang], [yeu_cau], [cap_nhat_luc])
VALUES
    (N'TREOSPA0001', N'dattq', N'KH000001', N'0901234567', N'TC000001', N'SPA00000001', N'LH00000001', N'4.2', N'Bình thường, lông hơi rối vùng cổ', N'Chải kỹ và dưỡng lông mềm', DATEADD(hour,-2,CAST(@homNay AS datetime2))),
    (N'TREOSPA0002', N'demo.spa', N'KH000002', N'0912345678', N'TC000002', N'SPA00000002', N'LH00000002', N'3.8', N'Bình thường', N'Tạo kiểu dáng tròn phong cách Hàn Quốc', DATEADD(hour,-1,CAST(@homNay AS datetime2)));

-- 15. CHI TIẾT KIỂM KÊ
INSERT INTO dbo.[chi_tiet_kiem_ke] ([ma_bien_ban], [ma_lo], [ton_he_thong], [ton_thuc_te], [nguyen_nhan])
VALUES
    (N'KK00000001', N'LO00000001', 85, 85, N'Khớp số lượng thực tế'),
    (N'KK00000001', N'LO00000002', 85, 84, N'Hao hụt 1 gói do rách bao bì'),
    (N'KK00000002', N'LO00000016', 10, 9, N'Hao hụt 1 túi do thủng đáy'),
    (N'KK00000003', N'LO00000001', 85, 83, N'Phát hiện 2 gói bị ẩm mép chờ cân bằng kho'),
    (N'KK00000003', N'LO00000003', 85, 85, N'Khớp số lượng thực tế'),
    (N'KK00000004', N'LO00000017', 9, 9, N'Đang kiểm kê định kỳ');

-- 17. HÓA ĐƠN
INSERT INTO dbo.[hoa_don] ([ma_hoa_don], [ma_phien], [ten_dang_nhap], [ma_khach_hang], [tong_tien], [hinh_thuc_thanh_toan], [tien_khach_dua], [tien_thoi], [diem_cong], [trang_thai], [tao_luc], [thanh_toan_luc], [idempotency_key], [tam_tinh], [giam_gia], [diem_dung], [ma_voucher], [ghi_chu], [chi_tiet_thanh_toan])
VALUES
    (N'HD2610190001', N'MPI00000001', N'vinhtq', N'KH000002', 160000.00, N'TIEN_MAT', 210000.00, 50000.00, 16, N'DA_THANH_TOAN', DATEADD(day,-34,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-34,CAST(@homNay AS datetime2))), N'IDEMP-HD-0001', 160000.00, 0.00, 0, N'VOUCHER000002', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:160000:210000:50000'),
    (N'HD2610190002', N'MPI00000002', N'admin', N'KH000003', 170000.00, N'CHUYEN_KHOAN', 170000.00, 0.00, 17, N'DA_THANH_TOAN', DATEADD(day,-33,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-33,CAST(@homNay AS datetime2))), N'IDEMP-HD-0002', 170000.00, 0.00, 0, N'VOUCHER000003', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:170000:170000:0'),
    (N'HD2610190003', N'MPI00000003', N'vyptt', N'KH000004', 180000.00, N'TIEN_MAT', 200000.00, 20000.00, 18, N'DA_THANH_TOAN', DATEADD(day,-32,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-32,CAST(@homNay AS datetime2))), N'IDEMP-HD-0003', 180000.00, 0.00, 0, N'VOUCHER000004', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:180000:200000:20000'),
    (N'HD2610190004', N'MPI00000004', N'vinhtq', N'KH000005', 190000.00, N'CHUYEN_KHOAN', 190000.00, 0.00, 19, N'DA_THANH_TOAN', DATEADD(day,-31,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-31,CAST(@homNay AS datetime2))), N'IDEMP-HD-0004', 190000.00, 0.00, 0, N'VOUCHER000005', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:190000:190000:0'),
    (N'HD2610190005', N'MPI00000005', N'admin', N'KH000006', 200000.00, N'TIEN_MAT', 200000.00, 0.00, 20, N'DA_THANH_TOAN', DATEADD(day,-30,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-30,CAST(@homNay AS datetime2))), N'IDEMP-HD-0005', 200000.00, 0.00, 0, N'VOUCHER000001', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:200000:200000:0'),
    (N'HD2610190006', N'MPI00000006', N'vyptt', N'KH000007', 210000.00, N'CHUYEN_KHOAN', 210000.00, 0.00, 21, N'DA_THANH_TOAN', DATEADD(day,-29,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-29,CAST(@homNay AS datetime2))), N'IDEMP-HD-0006', 210000.00, 0.00, 0, N'VOUCHER000002', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:210000:210000:0'),
    (N'HD2610190007', N'MPI00000007', N'vinhtq', N'KH000008', 220000.00, N'TIEN_MAT', 250000.00, 30000.00, 22, N'DA_THANH_TOAN', DATEADD(day,-28,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-28,CAST(@homNay AS datetime2))), N'IDEMP-HD-0007', 220000.00, 0.00, 0, N'VOUCHER000003', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:220000:250000:30000'),
    (N'HD2610190008', N'MPI00000008', N'admin', N'KH000009', 230000.00, N'CHUYEN_KHOAN', 230000.00, 0.00, 23, N'DA_THANH_TOAN', DATEADD(day,-27,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-27,CAST(@homNay AS datetime2))), N'IDEMP-HD-0008', 230000.00, 0.00, 0, N'VOUCHER000004', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:230000:230000:0'),
    (N'HD2610190009', N'MPI00000009', N'vyptt', N'KH000010', 240000.00, N'TIEN_MAT', 250000.00, 10000.00, 24, N'DA_THANH_TOAN', DATEADD(day,-26,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-26,CAST(@homNay AS datetime2))), N'IDEMP-HD-0009', 240000.00, 0.00, 0, N'VOUCHER000005', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:240000:250000:10000'),
    (N'HD2610190010', N'MPI00000010', N'vinhtq', N'KH000011', 250000.00, N'CHUYEN_KHOAN', 250000.00, 0.00, 25, N'DA_THANH_TOAN', DATEADD(day,-25,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-25,CAST(@homNay AS datetime2))), N'IDEMP-HD-0010', 250000.00, 0.00, 0, N'VOUCHER000001', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:250000:250000:0'),
    (N'HD2610190011', N'MPI00000011', N'admin', N'KH000012', 260000.00, N'TIEN_MAT', 300000.00, 40000.00, 26, N'DA_THANH_TOAN', DATEADD(day,-24,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-24,CAST(@homNay AS datetime2))), N'IDEMP-HD-0011', 260000.00, 0.00, 0, N'VOUCHER000002', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:260000:300000:40000'),
    (N'HD2610190012', N'MPI00000012', N'vyptt', N'KH000013', 270000.00, N'CHUYEN_KHOAN', 270000.00, 0.00, 27, N'DA_THANH_TOAN', DATEADD(day,-23,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-23,CAST(@homNay AS datetime2))), N'IDEMP-HD-0012', 270000.00, 0.00, 0, N'VOUCHER000003', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:270000:270000:0'),
    (N'HD2610190013', N'MPI00000013', N'vinhtq', N'KH000014', 280000.00, N'TIEN_MAT', 300000.00, 20000.00, 28, N'DA_THANH_TOAN', DATEADD(day,-22,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-22,CAST(@homNay AS datetime2))), N'IDEMP-HD-0013', 280000.00, 0.00, 0, N'VOUCHER000004', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:280000:300000:20000'),
    (N'HD2610190014', N'MPI00000014', N'admin', N'KH000015', 290000.00, N'CHUYEN_KHOAN', 290000.00, 0.00, 29, N'DA_THANH_TOAN', DATEADD(day,-21,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-21,CAST(@homNay AS datetime2))), N'IDEMP-HD-0014', 290000.00, 0.00, 0, N'VOUCHER000005', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:290000:290000:0'),
    (N'HD2610190015', N'MPI00000015', N'vyptt', N'KH000016', 300000.00, N'TIEN_MAT', 300000.00, 0.00, 30, N'DA_THANH_TOAN', DATEADD(day,-20,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-20,CAST(@homNay AS datetime2))), N'IDEMP-HD-0015', 300000.00, 0.00, 0, N'VOUCHER000001', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:300000:300000:0'),
    (N'HD2610190016', N'MPI00000016', N'vinhtq', N'KH000017', 310000.00, N'CHUYEN_KHOAN', 310000.00, 0.00, 31, N'DA_THANH_TOAN', DATEADD(day,-19,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-19,CAST(@homNay AS datetime2))), N'IDEMP-HD-0016', 310000.00, 0.00, 0, N'VOUCHER000002', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:310000:310000:0'),
    (N'HD2610190017', N'MPI00000017', N'admin', N'KH000018', 320000.00, N'TIEN_MAT', 350000.00, 30000.00, 32, N'DA_THANH_TOAN', DATEADD(day,-18,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-18,CAST(@homNay AS datetime2))), N'IDEMP-HD-0017', 320000.00, 0.00, 0, N'VOUCHER000003', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:320000:350000:30000'),
    (N'HD2610190018', N'MPI00000018', N'vyptt', N'KH000019', 330000.00, N'CHUYEN_KHOAN', 330000.00, 0.00, 33, N'DA_THANH_TOAN', DATEADD(day,-17,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-17,CAST(@homNay AS datetime2))), N'IDEMP-HD-0018', 330000.00, 0.00, 0, N'VOUCHER000004', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:330000:330000:0'),
    (N'HD2610190019', N'MPI00000019', N'vinhtq', N'KH000020', 340000.00, N'TIEN_MAT', 350000.00, 10000.00, 34, N'DA_THANH_TOAN', DATEADD(day,-16,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-16,CAST(@homNay AS datetime2))), N'IDEMP-HD-0019', 340000.00, 0.00, 0, N'VOUCHER000005', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:340000:350000:10000'),
    (N'HD2610190020', N'MPI00000020', N'admin', N'KH000021', 350000.00, N'CHUYEN_KHOAN', 350000.00, 0.00, 35, N'DA_THANH_TOAN', DATEADD(day,-15,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-15,CAST(@homNay AS datetime2))), N'IDEMP-HD-0020', 350000.00, 0.00, 0, N'VOUCHER000001', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:350000:350000:0'),
    (N'HD2610190021', N'MPI00000021', N'vyptt', N'KH000022', 360000.00, N'TIEN_MAT', 400000.00, 40000.00, 36, N'DA_THANH_TOAN', DATEADD(day,-14,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-14,CAST(@homNay AS datetime2))), N'IDEMP-HD-0021', 360000.00, 0.00, 0, N'VOUCHER000002', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:360000:400000:40000'),
    (N'HD2610190022', N'MPI00000022', N'vinhtq', N'KH000023', 370000.00, N'CHUYEN_KHOAN', 370000.00, 0.00, 37, N'DA_THANH_TOAN', DATEADD(day,-13,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-13,CAST(@homNay AS datetime2))), N'IDEMP-HD-0022', 370000.00, 0.00, 0, N'VOUCHER000003', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:370000:370000:0'),
    (N'HD2610190023', N'MPI00000023', N'admin', N'KH000024', 380000.00, N'TIEN_MAT', 400000.00, 20000.00, 38, N'DA_THANH_TOAN', DATEADD(day,-12,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-12,CAST(@homNay AS datetime2))), N'IDEMP-HD-0023', 380000.00, 0.00, 0, N'VOUCHER000004', N'Giao dịch thanh toán tại quầy', N'TIEN_MAT:380000:400000:20000'),
    (N'HD2610190024', N'MPI00000024', N'vyptt', N'KH000025', 390000.00, N'CHUYEN_KHOAN', 390000.00, 0.00, 39, N'DA_THANH_TOAN', DATEADD(day,-11,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-11,CAST(@homNay AS datetime2))), N'IDEMP-HD-0024', 390000.00, 0.00, 0, N'VOUCHER000005', N'Giao dịch thanh toán tại quầy', N'CHUYEN_KHOAN:390000:390000:0'),
    (N'HD2610190025', N'MPI00000025', N'vinhtq', N'KH000001', 150000.00, N'TIEN_MAT', 200000.00, 50000.00, 15, N'DA_THANH_TOAN', DATEADD(day,-10,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-10,CAST(@homNay AS datetime2))), N'IDEMP-HD-0025', 150000.00, 0.00, 0, NULL, N'Thanh toán dịch vụ Spa', N'TIEN_MAT:150000:200000:50000'),
    (N'HD2610190026', N'MPI00000026', N'admin', N'KH000002', 210000.00, N'CHUYEN_KHOAN', 210000.00, 0.00, 21, N'DA_THANH_TOAN', DATEADD(day,-9,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-9,CAST(@homNay AS datetime2))), N'IDEMP-HD-0026', 210000.00, 0.00, 0, NULL, N'Thanh toán dịch vụ Spa', N'CHUYEN_KHOAN:210000:210000:0'),
    (N'HD2610190027', N'MPI00000027', N'vyptt', N'KH000003', 100000.00, N'TIEN_MAT', 100000.00, 0.00, 10, N'DA_THANH_TOAN', DATEADD(day,-8,CAST(@homNay AS datetime2)), DATEADD(minute,5,DATEADD(day,-8,CAST(@homNay AS datetime2))), N'IDEMP-HD-0027', 100000.00, 0.00, 0, NULL, N'Thanh toán dịch vụ Spa', N'TIEN_MAT:100000:100000:0'),
    (N'HD2610190028', N'MPI00000028', N'vinhtq', N'KH000029', 430000.00, N'CHUYEN_KHOAN', 430000.00, 0.00, 43, N'DA_THANH_TOAN', DATEADD(day,-7,CAST(@homNay AS datetime2)), DATEADD(hour,9,DATEADD(day,-7,CAST(@homNay AS datetime2))), N'IDEMP-HD-0028', 430000.00, 0.00, 0, N'VOUCHER000004', N'Giao dịch bán hàng tại quầy', N'CHUYEN_KHOAN:430000:430000:0'),
    (N'HD2610190029', N'MPI00000029', N'admin', N'KH000004', 350000.00, N'TIEN_MAT', 400000.00, 50000.00, 35, N'DA_THANH_TOAN', DATEADD(day,-6,CAST(@homNay AS datetime2)), DATEADD(hour,10,DATEADD(day,-6,CAST(@homNay AS datetime2))), N'IDEMP-HD-0029', 350000.00, 0.00, 0, NULL, N'Thanh toán Combo Spa VIP', N'TIEN_MAT:350000:400000:50000'),
    (N'HD2610190030', N'MPI00000030', N'vyptt', N'KH000005', 160000.00, N'CHUYEN_KHOAN', 160000.00, 0.00, 16, N'DA_THANH_TOAN', DATEADD(day,-5,CAST(@homNay AS datetime2)), DATEADD(hour,15,DATEADD(day,-5,CAST(@homNay AS datetime2))), N'IDEMP-HD-0030', 160000.00, 0.00, 0, NULL, N'Thanh toán tắm trị liệu Spa', N'CHUYEN_KHOAN:160000:160000:0'),
    (N'HD2610190031', N'MPI00000031', N'vinhtq', N'KH000001', 300000.00, N'TIEN_MAT', 300000.00, 0.00, 30, N'DA_THANH_TOAN', DATEADD(day,-4,CAST(@homNay AS datetime2)), DATEADD(hour,11,DATEADD(day,-4,CAST(@homNay AS datetime2))), N'IDEMP-HD-0031', 300000.00, 0.00, 0, NULL, N'Quyết toán trả phòng Hotel P002', N'TIEN_MAT:300000:300000:0'),
    (N'HD2610190032', N'MPI00000032', N'admin', N'KH000004', 450000.00, N'CHUYEN_KHOAN', 450000.00, 0.00, 45, N'DA_THANH_TOAN', DATEADD(day,-3,CAST(@homNay AS datetime2)), DATEADD(hour,16,DATEADD(day,-3,CAST(@homNay AS datetime2))), N'IDEMP-HD-0032', 450000.00, 0.00, 0, NULL, N'Quyết toán trả phòng Hotel P001', N'CHUYEN_KHOAN:450000:450000:0'),
    (N'HD2610190033', N'MPI00000033', N'vyptt', N'KH000006', 430000.00, N'TIEN_MAT', 450000.00, 20000.00, 43, N'DA_THANH_TOAN', DATEADD(day,-2,CAST(@homNay AS datetime2)), DATEADD(hour,10,DATEADD(day,-2,CAST(@homNay AS datetime2))), N'IDEMP-HD-0033', 430000.00, 0.00, 0, NULL, N'Thanh toán tắm sục khoáng Onsen', N'TIEN_MAT:430000:450000:20000'),
    (N'HD2610190034', N'MPI00000034', N'vinhtq', N'KH000008', 490000.00, N'CHUYEN_KHOAN', 490000.00, 0.00, 49, N'DA_THANH_TOAN', DATEADD(day,-1,CAST(@homNay AS datetime2)), DATEADD(hour,17,DATEADD(day,-1,CAST(@homNay AS datetime2))), N'IDEMP-HD-0034', 490000.00, 0.00, 0, N'VOUCHER000005', N'Giao dịch bán lẻ thức ăn & phụ kiện', N'CHUYEN_KHOAN:490000:490000:0'),
    (N'HD2610190035', N'MPI00000035', N'dattq', N'KH000011', 450000.00, N'TIEN_MAT', 500000.00, 50000.00, 45, N'DA_THANH_TOAN', DATEADD(hour,-4,CAST(@homNay AS datetime2)), DATEADD(hour,-4,CAST(@homNay AS datetime2)), N'IDEMP-HD-0035', 450000.00, 0.00, 0, NULL, N'Giao dịch bán lẻ hàng hóa ca sáng', N'TIEN_MAT:450000:500000:50000');

-- 20. CHI TIẾT PHIẾU NHẬP
INSERT INTO dbo.[chi_tiet_phieu_nhap] ([ma_phieu_nhap], [ma_san_pham], [ma_lo], [han_su_dung], [so_luong], [gia_nhap])
VALUES
    (N'PNK00000001', N'SP000001', N'LO00000001', CONVERT(date,'2028-02-10',23), 100, 91000.00),
    (N'PNK00000001', N'SP000002', N'LO00000002', CONVERT(date,'2028-03-15',23), 80, 117500.00),
    (N'PNK00000002', N'SP000003', N'LO00000003', CONVERT(date,'2027-08-20',23), 500, 16000.00),
    (N'PNK00000002', N'SP000004', N'LO00000004', CONVERT(date,'2027-09-10',23), 250, 19600.00),
    (N'PNK00000003', N'SP000017', N'LO00000018', CONVERT(date,'2028-05-01',23), 100, 145000.00),
    (N'PNK00000004', N'SP000006', N'LO00000006', CONVERT(date,'2029-01-01',23), 120, 45000.00),
    (N'PNK00000004', N'SP000013', N'LO00000013', CONVERT(date,'2028-11-15',23), 50, 84000.00),
    (N'PNK00000005', N'SP000019', N'LO00000020', CONVERT(date,'2028-04-20',23), 400, 28000.00),
    (N'PNK00000006', N'SP000009', N'LO00000009', CONVERT(date,'2030-01-01',23), 120, 50000.00),
    (N'PNK00000006', N'SP000012', N'LO00000012', CONVERT(date,'2030-01-01',23), 60, 180000.00),
    (N'PNK00000007', N'SP000010', N'LO00000010', CONVERT(date,'2030-01-01',23), 120, 70000.00),
    (N'PNK00000008', N'SP000008', N'LO00000008', CONVERT(date,'2029-05-10',23), 100, 135000.00),
    (N'PNK00000009', N'SP000005', N'LO00000005', CONVERT(date,'2029-06-01',23), 75, 140000.00),
    (N'PNK00000010', N'SP000046', N'LO00000047', CONVERT(date,'2030-12-31',23), 120, 162500.00);

-- 22. CHI TIẾT PHIẾU XUẤT
INSERT INTO dbo.[chi_tiet_phieu_xuat] ([ma_phieu_xuat], [ma_lo], [so_luong], [gia_tri])
VALUES
    (N'PXK00000001', N'LO00000005', 2, 270000.00),
    (N'PXK00000001', N'LO00000006', 1, 45000.00),
    (N'PXK00000002', N'LO00000001', 1, 91000.00),
    (N'PXK00000003', N'LO00000011', 3, 78000.00),
    (N'PXK00000003', N'LO00000012', 2, 102000.00),
    (N'PXK00000004', N'LO00000001', 6, 546000.00),
    (N'PXK00000005', N'LO00000059', 4, 650000.00);

-- 18. CHI TIẾT HÓA ĐƠN
INSERT INTO dbo.[chi_tiet_hoa_don] ([ma_hoa_don], [ma_san_pham], [ma_lo], [so_luong], [don_gia], [giam_gia], [thue_gtgt], [ma_dich_vu], [ten_hang])
VALUES
    (N'HD2610190001', N'SP000001', N'LO00000001', 1, 160000.00, 0.00, 0.00, N'', N'Thức ăn hạt cho mèo ROYAL CANIN Fit 32 — Gói 400g'),
    (N'HD2610190002', N'SP000002', N'LO00000002', 1, 170000.00, 0.00, 0.00, N'', N'Thức ăn hạt cho chó ROYAL CANIN Mini Adult — Gói 800g'),
    (N'HD2610190003', N'SP000003', N'LO00000003', 1, 180000.00, 0.00, 0.00, N'', N'Pate cho mèo WHISKAS vị cá hồi túi 85g (Combo)'),
    (N'HD2610190004', N'SP000004', N'LO00000004', 1, 190000.00, 0.00, 0.00, N'', N'Pate cho chó PEDIGREE vị bò sốt túi 130g (Combo)'),
    (N'HD2610190005', N'SP000005', N'LO00000005', 1, 200000.00, 0.00, 0.00, N'', N'Sữa tắm dưỡng lông mượt JOYCE & DOLLS Rose 400ml'),
    (N'HD2610190006', N'SP000006', N'LO00000006', 1, 210000.00, 0.00, 0.00, N'', N'Sữa tắm trị ve rận và bọ chét Hantox 200ml (Combo)'),
    (N'HD2610190007', N'SP000007', N'LO00000007', 1, 220000.00, 0.00, 0.00, N'', N'Cát vệ sinh đất sét bentonite siêu vón túi 5L (Combo)'),
    (N'HD2610190008', N'SP000008', N'LO00000008', 1, 230000.00, 0.00, 0.00, N'', N'Cát đậu nành hữu cơ xả bồn cầu CATS BEST 6L'),
    (N'HD2610190009', N'SP000009', N'LO00000009', 1, 240000.00, 0.00, 0.00, N'', N'Vòng cổ da bò kèm chuông Hand in Hand'),
    (N'HD2610190010', N'SP000010', N'LO00000010', 1, 250000.00, 0.00, 0.00, N'', N'Dây dắt kèm yếm ngực phản quang đi dạo'),
    (N'HD2610190011', N'SP000011', N'LO00000011', 1, 260000.00, 0.00, 0.00, N'', N'Bát ăn inox đôi đế chống trượt hình ếch'),
    (N'HD2610190012', N'SP000012', N'LO00000012', 1, 270000.00, 0.00, 0.00, N'', N'Nệm nằm lông cừu ấm áp mùa đông size L'),
    (N'HD2610190013', N'SP000013', N'LO00000013', 1, 280000.00, 0.00, 0.00, N'', N'Thuốc xịt trị nấm da Fungikur 50ml'),
    (N'HD2610190014', N'SP000014', N'LO00000014', 1, 290000.00, 0.00, 0.00, N'', N'Bánh thưởng thanh sạch răng cún Vegebrand 120g'),
    (N'HD2610190015', N'SP000015', N'LO00000016', 1, 300000.00, 0.00, 0.00, N'', N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100'),
    (N'HD2610190016', N'SP000016', N'LO00000017', 1, 310000.00, 0.00, 0.00, N'', N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone'),
    (N'HD2610190017', N'SP000017', N'LO00000018', 1, 320000.00, 0.00, 0.00, N'', N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy'),
    (N'HD2610190018', N'SP000018', N'LO00000019', 1, 330000.00, 0.00, 0.00, N'', N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS'),
    (N'HD2610190019', N'SP000019', N'LO00000020', 1, 340000.00, 0.00, 0.00, N'', N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH'),
    (N'HD2610190020', N'SP000020', N'LO00000021', 1, 350000.00, 0.00, 0.00, N'', N'Thức ăn cho mèo con ROYAL CANIN Kitten'),
    (N'HD2610190021', N'SP000021', N'LO00000022', 1, 360000.00, 0.00, 0.00, N'', N'Pate cho mèo vị nước sốt cá ngừ WHISKAS'),
    (N'HD2610190022', N'SP000022', N'LO00000023', 1, 370000.00, 0.00, 0.00, N'', N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu'),
    (N'HD2610190023', N'SP000023', N'LO00000024', 1, 380000.00, 0.00, 0.00, N'', N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27'),
    (N'HD2610190024', N'SP000024', N'LO00000025', 1, 390000.00, 0.00, 0.00, N'', N'Pate cho mèo vị nước sốt thịt bò WHISKAS'),
    (N'HD2610190025', N'SPA00000001', NULL, 1, 150000.00, 0.00, 0.00, N'SPA00000001', N'Tắm và sấy khô cơ bản'),
    (N'HD2610190026', N'SPA00000002', NULL, 1, 210000.00, 0.00, 0.00, N'SPA00000002', N'Cắt tỉa tạo kiểu dáng'),
    (N'HD2610190027', N'SPA00000003', NULL, 1, 100000.00, 0.00, 0.00, N'SPA00000003', N'Vệ sinh tai và cắt mài móng'),
    (N'HD2610190028', N'SP000039', N'LO00000040', 1, 430000.00, 0.00, 0.00, N'', N'Thức ăn cho chó Poodle MKB All Life Stages 1kg'),
    (N'HD2610190029', N'SPA00000004', NULL, 1, 350000.00, 0.00, 0.00, N'SPA00000004', N'Combo Spa toàn diện VIP'),
    (N'HD2610190030', N'SPA00000005', NULL, 1, 160000.00, 0.00, 0.00, N'SPA00000005', N'Tắm trị liệu nấm và ve rận'),
    (N'HD2610190031', N'HOTEL-ROOM', NULL, 1, 300000.00, 0.00, 0.00, N'HOTEL-ROOM', N'Lưu trú phòng P002 (3 ngày, đã trừ cọc)'),
    (N'HD2610190032', N'HOTEL-ROOM', NULL, 1, 450000.00, 0.00, 0.00, N'HOTEL-ROOM', N'Lưu trú phòng P001 (4 ngày, đã trừ cọc)'),
    (N'HD2610190033', N'SPA00000008', NULL, 1, 430000.00, 0.00, 0.00, N'SPA00000008', N'Tắm sục khoáng Onsen thư giãn'),
    (N'HD2610190034', N'SP000046', N'LO00000047', 1, 250000.00, 0.00, 0.00, N'', N'Khay vệ sinh cho mèo MAKAR Cat Litter Station'),
    (N'HD2610190034', N'SP000048', N'LO00000049', 1, 240000.00, 0.00, 0.00, N'', N'Cát vệ sinh cho mèo than hoạt tính IRIS OHYAMA'),
    (N'HD2610190035', N'SP000002', N'LO00000002', 2, 145000.00, 0.00, 0.00, N'', N'Thức ăn hạt cho chó ROYAL CANIN Mini Adult — Gói 800g'),
    (N'HD2610190035', N'SP000015', N'LO00000016', 1, 160000.00, 0.00, 0.00, N'', N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100');

-- 30. PHIẾU ĐỔI TRẢ
INSERT INTO dbo.[phieu_doi_tra] ([ma_phieu], [ma_hoa_don], [ten_dang_nhap], [chenh_lech], [trang_thai], [tao_luc], [ly_do], [loai_phieu], [ma_hoa_don_thay_the], [gia_tri_duoc_doi], [khoa_thao_tac])
VALUES
    (N'DT00000001', N'HD2610190001', N'vyptt', 10000.00, N'HOAN_TAT', DATEADD(day,-2,CAST(@homNay AS datetime2)), N'Khách đổi cỡ vòng cổ lớn hơn cho cún cưng', N'DOI_HANG', N'HD2610190002', 75000.00, N'KHOA-DT01');

-- 23. HỢP ĐỒNG LƯU TRÚ
INSERT INTO dbo.[hop_dong_luu_tru] ([ma_hop_dong], [ma_khach_hang], [ma_thu_cung], [ma_phong], [hinh_thuc_gui], [ngay_vao], [ngay_ra_du_kien], [ngay_ra_thuc_te], [tien_coc], [phuong_thuc_coc], [khoa_thu_coc], [trang_thai], [gia_dem], [ghi_chu], [ma_hoa_don], [da_nhan_coc], [nhan_coc_luc], [nhan_coc_boi], [ma_phien_nhan_coc], [coc_yeu_cau], [ly_do_doi_coc])
VALUES
    (N'HDHT000001', N'KH000001', N'TC000001', N'P002', N'Qua đêm', DATEADD(day,-7,CAST(@homNay AS datetime2)), DATEADD(day,-4,CAST(@homNay AS datetime2)), DATEADD(day,-4,CAST(@homNay AS datetime2)), 150000.00, N'TIEN_MAT', N'COC-HDHT000001', N'DA_TRA_PHONG', 150000.00, N'Chó Poodle ngoan, ăn hạt Royal Canin 2 bữa/ngày', N'HD2610190031', 1, DATEADD(day,-7,CAST(@homNay AS datetime2)), N'admin', N'MPI00000028', 150000.00, N'Không đổi'),
    (N'HDHT000002', N'KH000002', N'TC000002', N'P005', N'Qua đêm', DATEADD(day,-3,CAST(@homNay AS datetime2)), DATEADD(day,2,CAST(@homNay AS datetime2)), NULL, 250000.00, N'CHUYEN_KHOAN', N'COC-HDHT000002', N'DANG_O', 250000.00, N'Mèo Anh lông ngắn ở phòng VIP, thích pate Whiskas buổi tối', NULL, 1, DATEADD(day,-3,CAST(@homNay AS datetime2)), N'vyptt', N'MPI00000032', 250000.00, N'Không đổi'),
    (N'HDHT000003', N'KH000003', N'TC000003', N'P010', N'Qua đêm', DATEADD(day,-2,CAST(@homNay AS datetime2)), DATEADD(day,3,CAST(@homNay AS datetime2)), NULL, 400000.00, N'CHUYEN_KHOAN', N'COC-HDHT000003', N'DANG_O', 400000.00, N'Bé Corgi ở phòng Penthouse, dắt đi dạo 2 lần/ngày', NULL, 1, DATEADD(day,-2,CAST(@homNay AS datetime2)), N'admin', N'MPI00000033', 400000.00, N'Không đổi'),
    (N'HDHT000004', N'KH000004', N'TC000004', N'P001', N'Qua đêm', DATEADD(day,-7,CAST(@homNay AS datetime2)), DATEADD(day,-3,CAST(@homNay AS datetime2)), DATEADD(day,-3,CAST(@homNay AS datetime2)), 150000.00, N'TIEN_MAT', N'COC-HDHT000004', N'DA_TRA_PHONG', 150000.00, N'Bé Phốc sóc ăn nhẹ buổi trưa, uống nước ấm', N'HD2610190032', 1, DATEADD(day,-7,CAST(@homNay AS datetime2)), N'dattq', N'MPI00000028', 150000.00, N'Không đổi'),
    (N'HDHT000005', N'KH000005', N'TC000005', N'P002', N'Qua đêm', DATEADD(day,-1,CAST(@homNay AS datetime2)), DATEADD(day,2,CAST(@homNay AS datetime2)), NULL, 150000.00, N'TIEN_MAT', N'COC-HDHT000005', N'DANG_O', 150000.00, N'Bé Mèo Ba Tư cần chải lông mỗi sáng', NULL, 1, DATEADD(day,-1,CAST(@homNay AS datetime2)), N'vyptt', N'MPI00000034', 150000.00, N'Không đổi'),
    (N'HDHT000006', N'KH000006', N'TC000006', N'P006', N'Qua đêm', DATEADD(day,1,CAST(@homNay AS datetime2)), DATEADD(day,4,CAST(@homNay AS datetime2)), NULL, 250000.00, N'CHUYEN_KHOAN', N'COC-HDHT000006', N'DA_DAT', 250000.00, N'Khách đặt trước phòng VIP cho bé Golden dịp cuối tuần', NULL, 1, DATEADD(hour,-3,CAST(@homNay AS datetime2)), N'dattq', N'MPI00000035', 250000.00, N'Không đổi'),
    (N'HDHT000007', N'KH000007', N'TC000007', N'P009', N'Qua đêm', DATEADD(day,2,CAST(@homNay AS datetime2)), DATEADD(day,5,CAST(@homNay AS datetime2)), NULL, 0.00, N'TIEN_MAT', NULL, N'DA_DAT', 400000.00, N'Khách đặt phòng Penthouse, hẹn đóng cọc khi đưa bé đến', NULL, 0, NULL, NULL, NULL, 400000.00, N'Không đổi');

-- 26. PHIẾU TIẾP NHẬN SPA
INSERT INTO dbo.[phieu_tiep_nhan_spa] ([ma_phieu], [ma_lich_hen], [ma_thu_cung], [ma_goi], [mo_ta_dich_vu], [don_gia], [so_luong], [can_nang_thuc_te], [tong_tien], [trang_thai], [tao_luc], [ma_nhan_vien], [ghi_chu], [anh], [ma_hoa_don])
VALUES
    (N'SPA00000001', N'LH00000001', N'TC000001', N'SPA00000001', N'Tắm và sấy khô cơ bản', 150000.00, 1, 4.20, 150000.00, N'DA_THANH_TOAN', DATEADD(day,-10,CAST(@homNay AS datetime2)), N'NV0005', N'Bé ngoan, sấy lông hợp tác', N'images/spa/spa_01.jpg', N'HD2610190025'),
    (N'SPA00000002', N'LH00000002', N'TC000002', N'SPA00000002', N'Cắt tỉa tạo kiểu dáng', 210000.00, 1, 3.80, 210000.00, N'DA_THANH_TOAN', DATEADD(day,-9,CAST(@homNay AS datetime2)), N'NV0005', N'Cắt tỉa cẩn thận vùng tai và bàn chân', N'images/spa/spa_02.jpg', N'HD2610190026'),
    (N'SPA00000003', N'LH00000003', N'TC000003', N'SPA00000003', N'Vệ sinh tai và cắt mài móng', 100000.00, 1, 10.50, 100000.00, N'DA_THANH_TOAN', DATEADD(day,-8,CAST(@homNay AS datetime2)), N'NV0007', N'Đã vệ sinh tai khử mùi và mài móng', N'images/spa/spa_03.jpg', N'HD2610190027'),
    (N'SPA00000004', N'LH00000004', N'TC000004', N'SPA00000004', N'Combo Spa toàn diện VIP', 350000.00, 1, 2.60, 350000.00, N'DA_THANH_TOAN', DATEADD(day,-6,CAST(@homNay AS datetime2)), N'NV0005', N'Combo tắm sục jacuzzi và cắt tỉa lông', N'images/spa/spa_04.jpg', N'HD2610190029'),
    (N'SPA00000005', N'LH00000005', N'TC000005', N'SPA00000005', N'Tắm trị liệu nấm và ve rận', 160000.00, 1, 4.10, 160000.00, N'DA_THANH_TOAN', DATEADD(day,-5,CAST(@homNay AS datetime2)), N'NV0011', N'Tắm dầu y tế khử nấm lần 1', N'images/spa/spa_05.jpg', N'HD2610190030'),
    (N'SPA00000006', N'LH00000006', N'TC000006', N'SPA00000008', N'Tắm sục khoáng Onsen thư giãn', 430000.00, 1, 26.50, 430000.00, N'DA_THANH_TOAN', DATEADD(day,-2,CAST(@homNay AS datetime2)), N'NV0007', N'Bé Golden rất thích ngâm khoáng ấm', N'images/spa/spa_06.jpg', N'HD2610190033'),
    (N'SPA00000007', NULL, N'TC000010', N'SPA00000001', N'Tắm và sấy khô cơ bản', 200000.00, 1, 9.80, 200000.00, N'HOAN_TAT', DATEADD(day,-1,DATEADD(hour,14,CAST(@homNay AS datetime2))), N'NV0005', N'Đã hoàn tất tắm sấy, chờ khách thanh toán', N'images/spa/spa_07.jpg', NULL),
    (N'SPA00000008', NULL, N'TC000011', N'SPA00000002', N'Cắt tỉa tạo kiểu dáng', 230000.00, 1, 2.20, 230000.00, N'HOAN_TAT', DATEADD(hour,-3,CAST(@homNay AS datetime2)), N'NV0005', N'Đã cắt tỉa kiểu Teddy, chờ thanh toán tại quầy', N'images/spa/spa_08.jpg', NULL),
    (N'SPA00000009', N'LH00000007', N'TC000007', N'SPA00000006', N'Gỡ rối lông chuyên sâu và ủ dưỡng collagen', 180000.00, 1, 3.20, 180000.00, N'DANG_THUC_HIEN', DATEADD(hour,-2,CAST(@homNay AS datetime2)), N'NV0005', N'Đang ủ dưỡng collagen mượt lông', N'images/spa/spa_09.jpg', NULL),
    (N'SPA00000010', NULL, N'TC000012', N'SPA00000004', N'Combo Spa toàn diện VIP', 420000.00, 1, 7.50, 420000.00, N'DANG_THUC_HIEN', DATEADD(hour,-1,CAST(@homNay AS datetime2)), N'NV0007', N'Đang sấy phồng và chải lông tơ cho mèo Maine Coon', N'images/spa/spa_10.jpg', NULL),
    (N'SPA00000011', NULL, N'TC000014', N'SPA00000007', N'Cạo lông vệ sinh và tạo hình thẩm mỹ', 150000.00, 1, 4.60, 150000.00, N'TIEP_NHAN', DATEADD(minute,-45,CAST(@homNay AS datetime2)), N'NV0011', N'Tiếp nhận kiểm tra da lông bình thường', N'images/spa/spa_11.jpg', NULL),
    (N'SPA00000012', NULL, N'TC000016', N'SPA00000001', N'Tắm và sấy khô cơ bản', 200000.00, 1, 5.00, 200000.00, N'TIEP_NHAN', DATEADD(minute,-20,CAST(@homNay AS datetime2)), N'NV0005', N'Khách dặn dùng sữa tắm dịu nhẹ', N'images/spa/spa_12.jpg', NULL),
    (N'SPA00000013', NULL, N'TC000019', N'SPA00000003', N'Vệ sinh tai và cắt mài móng', 100000.00, 1, 8.20, 100000.00, N'DA_HUY', DATEADD(day,-3,CAST(@homNay AS datetime2)), N'NV0007', N'Bé cún đang mệt nên khách hẹn dời sang tuần sau', N'images/spa/spa_13.jpg', NULL);

-- 31. CHI TIẾT ĐỔI TRẢ
INSERT INTO dbo.[chi_tiet_doi_tra] ([ma_phieu], [ma_hoa_don], [ma_san_pham], [ma_lo], [so_luong], [tien_hoan])
VALUES
    (N'DT00000001', N'HD2610190001', N'SP000001', N'LO00000001', 1, 75000.00);

-- 24. LỊCH SỬ THU CỌC HOTEL
INSERT INTO dbo.[lich_su_thu_coc_hotel] ([ma_hop_dong], [khoa_thu_coc], [phuong_thuc], [so_tien], [khach_dua], [tien_thua], [ma_phien], [ten_dang_nhap_thu], [tao_luc])
VALUES
    (N'HDHT000001', N'COC-HDHT000001', N'TIEN_MAT', 150000.00, 200000.00, 50000.00, N'MPI00000028', N'admin', DATEADD(day,-7,CAST(@homNay AS datetime2))),
    (N'HDHT000002', N'COC-HDHT000002', N'CHUYEN_KHOAN', 250000.00, 250000.00, 0.00, N'MPI00000032', N'vyptt', DATEADD(day,-3,CAST(@homNay AS datetime2))),
    (N'HDHT000003', N'COC-HDHT000003', N'CHUYEN_KHOAN', 400000.00, 400000.00, 0.00, N'MPI00000033', N'admin', DATEADD(day,-2,CAST(@homNay AS datetime2))),
    (N'HDHT000004', N'COC-HDHT000004', N'TIEN_MAT', 150000.00, 150000.00, 0.00, N'MPI00000028', N'dattq', DATEADD(day,-7,CAST(@homNay AS datetime2))),
    (N'HDHT000005', N'COC-HDHT000005', N'TIEN_MAT', 150000.00, 200000.00, 50000.00, N'MPI00000034', N'vyptt', DATEADD(day,-1,CAST(@homNay AS datetime2))),
    (N'HDHT000006', N'COC-HDHT000006', N'CHUYEN_KHOAN', 250000.00, 250000.00, 0.00, N'MPI00000035', N'dattq', DATEADD(hour,-3,CAST(@homNay AS datetime2)));

-- 27. NHẬT KÝ CHĂM SÓC
INSERT INTO dbo.[nhat_ky_cham_soc] ([ma_nhat_ky], [ma_hop_dong], [ma_phieu_spa], [ma_nhan_vien], [thoi_gian], [an_uong], [ve_sinh], [tinh_trang], [huong_dan_thuoc], [da_thuc_hien], [anh_truoc], [anh_sau], [tep_dinh_kem])
VALUES
    (N'NK00000001', N'HDHT000001', N'SPA00000001', N'NV0005', DATEADD(day,-4,CAST(@homNay AS datetime2)), N'Ăn hết khẩu phần hạt sáng và chiều', N'Đi vệ sinh tốt, khay cát sạch sẽ', N'Ngoan ngoãn, thích chơi bóng thừng', N'Không cần dùng thuốc', N'Đã chải lông và cho uống nước ấm', N'images/care/truoc_01.jpg', N'images/care/sau_01.jpg', N'images/care/care_01.jpg'),
    (N'NK00000002', N'HDHT000001', N'SPA00000001', N'NV0005', DATEADD(day,-3,CAST(@homNay AS datetime2)), N'Uống đủ nước, ăn thêm 1 gói bánh thưởng', N'Vệ sinh bình thường', N'Vui vẻ, quấn nhân viên', N'Không cần dùng thuốc', N'Đã lau chân và dọn phòng', N'images/care/truoc_02.jpg', N'images/care/sau_02.jpg', N'images/care/care_02.jpg'),
    (N'NK00000003', N'HDHT000002', N'SPA00000002', N'NV0005', DATEADD(day,-2,CAST(@homNay AS datetime2)), N'Ăn 1 lon pate Whiskas cá ngừ', N'Đã dọn khay cát buổi sáng và tối', N'Nằm thư giãn trên kệ cào móng', N'Không cần dùng thuốc', N'Đã chải lông tơ', N'images/care/truoc_03.jpg', N'images/care/sau_03.jpg', N'images/care/care_03.jpg'),
    (N'NK00000004', N'HDHT000002', N'SPA00000002', N'NV0007', DATEADD(day,-1,CAST(@homNay AS datetime2)), N'Ăn uống tốt, không bỏ bữa', N'Khay vệ sinh sạch', N'Mượt lông, mắt sáng lanh lợi', N'Không cần dùng thuốc', N'Khử khuẩn phòng', N'images/care/truoc_04.jpg', N'images/care/sau_04.jpg', N'images/care/care_04.jpg'),
    (N'NK00000005', N'HDHT000003', N'SPA00000003', N'NV0005', DATEADD(hour,-8,CAST(@homNay AS datetime2)), N'Đã ăn hạt dinh dưỡng và uống sữa ấm', N'Được dắt đi dạo sân cỏ', N'Năng động, chạy nhảy khỏe', N'Không cần dùng thuốc', N'Vận động nhẹ buổi sáng', N'images/care/truoc_05.jpg', N'images/care/sau_05.jpg', N'images/care/care_05.jpg');

COMMIT TRANSACTION;
PRINT N'Đã nạp toàn bộ dữ liệu mẫu Pet Station thành công 100% không có giá trị NULL.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;
    DECLARE @loi NVARCHAR(4000) = ERROR_MESSAGE();
    RAISERROR(N'Lỗi nạp dữ liệu mẫu: %s', 16, 1, @loi);
END CATCH;
GO
