IF EXISTS(SELECT 1 FROM dbo.cai_dat WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT') RETURN;
-- Du lieu mau. Chay sau 001_schema.sql; khong ghi de ban ghi da co.
SET XACT_ABORT ON;
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET ANSI_PADDING ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET NUMERIC_ROUNDABORT OFF;
BEGIN TRANSACTION;
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'DASHBOARD_VIEW')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'DASHBOARD_VIEW',N'Xem màn hình chính');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'ACCOUNT_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'ACCOUNT_MANAGE',N'Quản lý tài khoản và nhân viên');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'SCHEDULE_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'SCHEDULE_MANAGE',N'Quản lý lịch làm việc');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'SCHEDULE_VIEW')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'SCHEDULE_VIEW',N'Xem lịch làm việc');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'CUSTOMER_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'CUSTOMER_MANAGE',N'Quản lý khách hàng');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'PET_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'PET_MANAGE',N'Quản lý thú cưng');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'PRODUCT_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'PRODUCT_MANAGE',N'Quản lý sản phẩm');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'SUPPLIER_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'SUPPLIER_MANAGE',N'Quản lý nhà cung cấp');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'INVENTORY_RECEIVE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'INVENTORY_RECEIVE',N'Nhập kho');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'INVENTORY_ISSUE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'INVENTORY_ISSUE',N'Xuất kho');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'INVENTORY_COUNT')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'INVENTORY_COUNT',N'Kiểm kê');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'PRICE_PROPOSE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'PRICE_PROPOSE',N'Đề xuất giá');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'PRICE_APPROVE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'PRICE_APPROVE',N'Duyệt giá');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'POS_SELL')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'POS_SELL',N'Lập đơn POS');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'PAYMENT_PROCESS')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'PAYMENT_PROCESS',N'Thanh toán');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'CASH_SHIFT_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'CASH_SHIFT_MANAGE',N'Mở/kiểm/bàn giao ca');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'PROMOTION_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'PROMOTION_MANAGE',N'Quản lý khuyến mãi');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'SPA_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'SPA_MANAGE',N'Quản lý Spa');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'HOTEL_MANAGE')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'HOTEL_MANAGE',N'Quản lý Hotel');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'APPROVAL_PROCESS')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'APPROVAL_PROCESS',N'Xử lý phê duyệt');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'AUDIT_VIEW')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'AUDIT_VIEW',N'Xem nhật ký');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'REPORT_VIEW_ALL')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'REPORT_VIEW_ALL',N'Xem báo cáo toàn cửa hàng');
IF NOT EXISTS(SELECT 1 FROM quyen WHERE ma_quyen=N'REPORT_VIEW_OWN_SHIFT')
 INSERT INTO quyen(ma_quyen,ten_quyen) VALUES(N'REPORT_VIEW_OWN_SHIFT',N'Xem báo cáo ca cá nhân');
INSERT INTO vai_tro_quyen(vai_tro,ma_quyen) SELECT 'STORE_MANAGER',q.ma_quyen FROM quyen q WHERE NOT EXISTS(SELECT 1 FROM vai_tro_quyen v WHERE v.vai_tro='STORE_MANAGER' AND v.ma_quyen=q.ma_quyen);
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='DASHBOARD_VIEW') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','DASHBOARD_VIEW');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='SCHEDULE_VIEW') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','SCHEDULE_VIEW');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='PRODUCT_MANAGE') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','PRODUCT_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='SUPPLIER_MANAGE') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','SUPPLIER_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='INVENTORY_RECEIVE') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','INVENTORY_RECEIVE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='INVENTORY_ISSUE') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','INVENTORY_ISSUE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='INVENTORY_COUNT') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','INVENTORY_COUNT');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='WAREHOUSE_STAFF' AND ma_quyen='PRICE_PROPOSE') INSERT INTO vai_tro_quyen VALUES('WAREHOUSE_STAFF','PRICE_PROPOSE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='DASHBOARD_VIEW') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','DASHBOARD_VIEW');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='SCHEDULE_VIEW') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','SCHEDULE_VIEW');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='CUSTOMER_MANAGE') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','CUSTOMER_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='PET_MANAGE') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','PET_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='POS_SELL') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','POS_SELL');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='PAYMENT_PROCESS') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','PAYMENT_PROCESS');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='CASH_SHIFT_MANAGE') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','CASH_SHIFT_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SALES_STAFF' AND ma_quyen='REPORT_VIEW_OWN_SHIFT') INSERT INTO vai_tro_quyen VALUES('SALES_STAFF','REPORT_VIEW_OWN_SHIFT');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='DASHBOARD_VIEW') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','DASHBOARD_VIEW');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='SCHEDULE_VIEW') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','SCHEDULE_VIEW');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='CUSTOMER_MANAGE') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','CUSTOMER_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='PET_MANAGE') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','PET_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='SPA_MANAGE') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','SPA_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='HOTEL_MANAGE') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','HOTEL_MANAGE');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='POS_SELL') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','POS_SELL');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='PAYMENT_PROCESS') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','PAYMENT_PROCESS');
IF NOT EXISTS(SELECT 1 FROM vai_tro_quyen WHERE vai_tro='SPA_HOTEL_STAFF' AND ma_quyen='CASH_SHIFT_MANAGE') INSERT INTO vai_tro_quyen VALUES('SPA_HOTEL_STAFF','CASH_SHIFT_MANAGE');
IF NOT EXISTS(SELECT 1 FROM ca_lam_viec WHERE ma_ca=N'CA_SANG')
 INSERT INTO ca_lam_viec(ma_ca,ten_ca,gio_bat_dau,gio_ket_thuc,so_nhan_su_toi_thieu) VALUES(N'CA_SANG',N'Sáng',N'07:00',N'12:00',2);
IF NOT EXISTS(SELECT 1 FROM ca_lam_viec WHERE ma_ca=N'CA_CHIEU')
 INSERT INTO ca_lam_viec(ma_ca,ten_ca,gio_bat_dau,gio_ket_thuc,so_nhan_su_toi_thieu) VALUES(N'CA_CHIEU',N'Chiều',N'12:00',N'17:00',2);
IF NOT EXISTS(SELECT 1 FROM ca_lam_viec WHERE ma_ca=N'CA_TOI')
 INSERT INTO ca_lam_viec(ma_ca,ten_ca,gio_bat_dau,gio_ket_thuc,so_nhan_su_toi_thieu) VALUES(N'CA_TOI',N'Tối',N'17:00',N'21:00',1);
IF NOT EXISTS(SELECT 1 FROM khach_hang WHERE ma_khach_hang=N'KH001')
 INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,hang_thanh_vien,diem_tich_luy) VALUES(N'KH001',N'Nguyễn Văn An',N'0901234567',N'an.nguyen@email.com',N'12 Nguyễn Trãi, Hà Nội',N'Thích giao hàng buổi tối',N'Vàng',N'1240');
IF NOT EXISTS(SELECT 1 FROM khach_hang WHERE ma_khach_hang=N'KH002')
 INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,hang_thanh_vien,diem_tich_luy) VALUES(N'KH002',N'Trần Thị Bình',N'0912345678',N'binh.tran@email.com',N'45 Lê Lợi, TP.HCM',N'',N'Kim Cương',N'4860');
IF NOT EXISTS(SELECT 1 FROM khach_hang WHERE ma_khach_hang=N'KH003')
 INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,hang_thanh_vien,diem_tich_luy) VALUES(N'KH003',N'Lê Hoàng Cường',N'0923456789',N'',N'8 Hùng Vương, Đà Nẵng',N'',N'Bạch Kim',N'3210');
IF NOT EXISTS(SELECT 1 FROM khach_hang WHERE ma_khach_hang=N'KH004')
 INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,hang_thanh_vien,diem_tich_luy) VALUES(N'KH004',N'Phạm Minh Đức',N'0934567890',N'duc.pham@email.com',N'21 Lạch Tray, Hải Phòng',N'',N'Bạc',N'840');
IF NOT EXISTS(SELECT 1 FROM khach_hang WHERE ma_khach_hang=N'KH005')
 INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,hang_thanh_vien,diem_tich_luy) VALUES(N'KH005',N'Hoàng Thị Em',N'0945678901',N'',N'5 Ninh Kiều, Cần Thơ',N'Dị ứng mùi nước hoa mạnh',N'Đồng',N'230');
IF NOT EXISTS(SELECT 1 FROM khach_hang WHERE ma_khach_hang=N'KH006')
 INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,hang_thanh_vien,diem_tich_luy) VALUES(N'KH006',N'Vũ Quang Hải',N'0956789012',N'hai.vu@email.com',N'77 Cầu Giấy, Hà Nội',N'',N'Vàng',N'1980');
IF NOT EXISTS(SELECT 1 FROM nhan_vien WHERE ma_nhan_vien=N'NV001')
 INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,ngay_vao_lam) VALUES(N'NV001',N'Nguyễn Văn An',N'0901111222',N'001090012345',N'Cửa hàng trưởng',N'2020-03-01');
IF NOT EXISTS(SELECT 1 FROM nhan_vien WHERE ma_nhan_vien=N'NV002')
 INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,ngay_vao_lam) VALUES(N'NV002',N'Phạm Thu Lan',N'0902222333',N'001092045678',N'Nhân viên bán hàng',N'2021-06-15');
IF NOT EXISTS(SELECT 1 FROM nhan_vien WHERE ma_nhan_vien=N'NV003')
 INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,ngay_vao_lam) VALUES(N'NV003',N'Hoàng Văn Nam',N'0903333444',N'001088078901',N'Nhân viên kho',N'2022-01-10');
IF NOT EXISTS(SELECT 1 FROM nhan_vien WHERE ma_nhan_vien=N'NV004')
 INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,ngay_vao_lam) VALUES(N'NV004',N'Trần Thị Hoa',N'0904444555',N'001095011223',N'Nhân viên Spa/Hotel',N'2022-09-05');
IF NOT EXISTS(SELECT 1 FROM nhan_vien WHERE ma_nhan_vien=N'NV005')
 INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,ngay_vao_lam) VALUES(N'NV005',N'Lê Văn Hùng',N'0905555666',N'001093099887',N'Nhân viên Spa/Hotel',N'2023-02-20');
IF NOT EXISTS(SELECT 1 FROM nhan_vien WHERE ma_nhan_vien=N'NV006')
 INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,ngay_vao_lam) VALUES(N'NV006',N'Đặng Minh Tú',N'0906666777',N'001096055443',N'Nhân viên bán hàng',N'2024-08-01');
IF NOT EXISTS(SELECT 1 FROM thu_cung WHERE ma_thu_cung=N'TC001')
 INSERT INTO thu_cung(ma_thu_cung,ten,loai,giong,gioi_tinh,ngay_sinh,can_nang,mau_long,khach_hang_id,tiem_chung,di_ung_benh_ly,tinh_cach) VALUES(N'TC001',N'Mochi',N'DOG',N'Poodle',N'Đực',N'2022-03-12',N'5.2',N'Trắng',(SELECT id FROM khach_hang WHERE ma_khach_hang=N'KH001'),N'Dại (01/2026), 5 bệnh (02/2026)',N'',N'Hiền, sợ tiếng máy sấy');
IF NOT EXISTS(SELECT 1 FROM thu_cung WHERE ma_thu_cung=N'TC002')
 INSERT INTO thu_cung(ma_thu_cung,ten,loai,giong,gioi_tinh,ngay_sinh,can_nang,mau_long,khach_hang_id,tiem_chung,di_ung_benh_ly,tinh_cach) VALUES(N'TC002',N'Bống',N'CAT',N'Anh lông ngắn',N'Cái',N'2021-07-05',N'4.1',N'Xám',(SELECT id FROM khach_hang WHERE ma_khach_hang=N'KH002'),N'Dại (03/2026)',N'Dị ứng cá ngừ',N'Nhút nhát');
IF NOT EXISTS(SELECT 1 FROM thu_cung WHERE ma_thu_cung=N'TC003')
 INSERT INTO thu_cung(ma_thu_cung,ten,loai,giong,gioi_tinh,ngay_sinh,can_nang,mau_long,khach_hang_id,tiem_chung,di_ung_benh_ly,tinh_cach) VALUES(N'TC003',N'Lucky',N'DOG',N'Golden Retriever',N'Đực',N'2020-11-20',N'29.5',N'Vàng',(SELECT id FROM khach_hang WHERE ma_khach_hang=N'KH003'),N'Dại, 7 bệnh (12/2025)',N'',N'Năng động, thân thiện');
IF NOT EXISTS(SELECT 1 FROM thu_cung WHERE ma_thu_cung=N'TC004')
 INSERT INTO thu_cung(ma_thu_cung,ten,loai,giong,gioi_tinh,ngay_sinh,can_nang,mau_long,khach_hang_id,tiem_chung,di_ung_benh_ly,tinh_cach) VALUES(N'TC004',N'Miu',N'CAT',N'Ba Tư',N'Cái',N'2023-02-02',N'3.8',N'Kem',(SELECT id FROM khach_hang WHERE ma_khach_hang=N'KH004'),N'',N'Hen suyễn nhẹ',N'Hay cào khi bị bế');
IF NOT EXISTS(SELECT 1 FROM thu_cung WHERE ma_thu_cung=N'TC005')
 INSERT INTO thu_cung(ma_thu_cung,ten,loai,giong,gioi_tinh,ngay_sinh,can_nang,mau_long,khach_hang_id,tiem_chung,di_ung_benh_ly,tinh_cach) VALUES(N'TC005',N'Coco',N'DOG',N'Corgi',N'Cái',N'2021-09-14',N'11.0',N'Nâu trắng',(SELECT id FROM khach_hang WHERE ma_khach_hang=N'KH006'),N'Dại (06/2026)',N'',N'Ham ăn');
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP001')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP001',N'Royal Canin Adult 2kg',N'Thức ăn',N'Túi',N'285000',N'240000',N'10',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP001')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP001'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),47);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP002')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP002',N'Pate Whiskas 400g',N'Thức ăn',N'Lon',N'45000',N'36000',N'30',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP002')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP002'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),82);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP003')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP003',N'Vòng cổ da size M',N'Phụ kiện',N'Cái',N'120000',N'80000',N'5',0);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP003')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP003'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),3);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP004')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP004',N'Sữa tắm Spa Dogs 500ml',N'Grooming',N'Chai',N'89000',N'62000',N'10',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP004')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP004'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),28);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP005')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP005',N'Lồng vận chuyển size L',N'Phụ kiện',N'Cái',N'450000',N'330000',N'3',0);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP005')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP005'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),8);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP006')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP006',N'Đồ chơi bóng cao su',N'Phụ kiện',N'Cái',N'35000',N'20000',N'10',0);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP006')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP006'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),15);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP007')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP007',N'Thức ăn hạt Orijen 1.8kg',N'Thức ăn',N'Túi',N'520000',N'430000',N'5',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP007')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP007'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),2);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP008')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP008',N'Cát vệ sinh mèo 5L',N'Vệ sinh',N'Túi',N'75000',N'55000',N'15',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP008')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP008'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),31);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP009')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP009',N'Bàn chải lông chó',N'Grooming',N'Cái',N'55000',N'40000',N'5',0);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP009')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP009'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),12);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP010')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP010',N'Thuốc nhỏ gáy Frontline',N'Thuốc',N'Hộp',N'180000',N'130000',N'5',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP010')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP010'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),0);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP011')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP011',N'Snack xương gặm răng',N'Thức ăn',N'Gói',N'42000',N'30000',N'10',1);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP011')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP011'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),40);
IF NOT EXISTS(SELECT 1 FROM san_pham WHERE ma_san_pham=N'SP012')
 INSERT INTO san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd) VALUES(N'SP012',N'Áo mưa chó size S',N'Phụ kiện',N'Cái',N'95000',N'65000',N'5',0);
IF NOT EXISTS(SELECT 1 FROM lo_hang WHERE san_pham_id=(SELECT id FROM san_pham WHERE ma_san_pham=N'SP012')) INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton) VALUES((SELECT id FROM san_pham WHERE ma_san_pham=N'SP012'),'SEED-001',DATEADD(day,180,CAST(GETDATE() AS DATE)),9);
IF NOT EXISTS(SELECT 1 FROM nha_cung_cap WHERE ma_ncc=N'NCC001')
 INSERT INTO nha_cung_cap(ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,ma_so_thue,dieu_khoan) VALUES(N'NCC001',N'Royal Canin VN',N'Nguyễn Minh Tâm',N'0243825678',N'Hà Nội',N'0101234567',N'30 ngày');
IF NOT EXISTS(SELECT 1 FROM nha_cung_cap WHERE ma_ncc=N'NCC002')
 INSERT INTO nha_cung_cap(ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,ma_so_thue,dieu_khoan) VALUES(N'NCC002',N'Mars Petcare VN',N'Trần Thành Long',N'0283826789',N'TP.HCM',N'0309876543',N'15 ngày');
IF NOT EXISTS(SELECT 1 FROM nha_cung_cap WHERE ma_ncc=N'NCC003')
 INSERT INTO nha_cung_cap(ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,ma_so_thue,dieu_khoan) VALUES(N'NCC003',N'Orijen Import',N'Lê Đức Anh',N'0243827890',N'Hà Nội',N'0105551234',N'Trả ngay');
IF NOT EXISTS(SELECT 1 FROM nha_cung_cap WHERE ma_ncc=N'NCC004')
 INSERT INTO nha_cung_cap(ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,ma_so_thue,dieu_khoan) VALUES(N'NCC004',N'PetShop Thú Y',N'Phạm Quang Vinh',N'0911222333',N'TP.HCM',N'0312223344',N'30 ngày');
IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong=N'P01')
 INSERT INTO phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,trang_thai) VALUES(N'P01',N'Tiêu chuẩn',N'1',N'150000',N'',N'AVAILABLE');
IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong=N'P02')
 INSERT INTO phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,trang_thai) VALUES(N'P02',N'Tiêu chuẩn',N'1',N'150000',N'',N'AVAILABLE');
IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong=N'P03')
 INSERT INTO phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,trang_thai) VALUES(N'P03',N'Mèo',N'1',N'120000',N'Có cây leo',N'CLEANING');
IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong=N'P04')
 INSERT INTO phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,trang_thai) VALUES(N'P04',N'VIP',N'2',N'300000',N'Điều hòa riêng',N'AVAILABLE');
IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong=N'P05')
 INSERT INTO phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,trang_thai) VALUES(N'P05',N'VIP',N'2',N'300000',N'Điều hòa riêng',N'AVAILABLE');
IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong=N'P06')
 INSERT INTO phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,trang_thai) VALUES(N'P06',N'Tiêu chuẩn',N'1',N'150000',N'Sửa quạt thông gió',N'MAINTENANCE');
IF NOT EXISTS(SELECT 1 FROM goi_dich_vu_spa WHERE ma_goi=N'DV001')
 INSERT INTO goi_dich_vu_spa(ma_goi,ten_goi,mo_ta,thoi_gian_du_kien) VALUES(N'DV001',N'Tắm và sấy khô',N'Tắm, sấy, chải lông',N'60');
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001') AND loai_thu_cung='DOG' AND khoang_can=N'< 5kg') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001'),'DOG',N'< 5kg',0,5,150000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001') AND loai_thu_cung='DOG' AND khoang_can=N'5 - 10kg') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001'),'DOG',N'5 - 10kg',5,10,200000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001') AND loai_thu_cung='DOG' AND khoang_can=N'> 10kg') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001'),'DOG',N'> 10kg',10,NULL,280000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001') AND loai_thu_cung='CAT' AND khoang_can=N'Mọi cân nặng') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV001'),'CAT',N'Mọi cân nặng',0,NULL,180000);
IF NOT EXISTS(SELECT 1 FROM goi_dich_vu_spa WHERE ma_goi=N'DV002')
 INSERT INTO goi_dich_vu_spa(ma_goi,ten_goi,mo_ta,thoi_gian_du_kien) VALUES(N'DV002',N'Cắt tỉa tạo kiểu',N'Cắt tỉa theo giống',N'90');
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002') AND loai_thu_cung='DOG' AND khoang_can=N'< 5kg') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002'),'DOG',N'< 5kg',0,5,230000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002') AND loai_thu_cung='DOG' AND khoang_can=N'5 - 10kg') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002'),'DOG',N'5 - 10kg',5,10,300000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002') AND loai_thu_cung='DOG' AND khoang_can=N'> 10kg') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002'),'DOG',N'> 10kg',10,NULL,400000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002') AND loai_thu_cung='CAT' AND khoang_can=N'Mọi cân nặng') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV002'),'CAT',N'Mọi cân nặng',0,NULL,250000);
IF NOT EXISTS(SELECT 1 FROM goi_dich_vu_spa WHERE ma_goi=N'DV003')
 INSERT INTO goi_dich_vu_spa(ma_goi,ten_goi,mo_ta,thoi_gian_du_kien) VALUES(N'DV003',N'Cắt móng, vệ sinh tai',N'Cắt và dũa móng, lau tai',N'20');
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV003') AND loai_thu_cung='DOG' AND khoang_can=N'Mọi cân nặng') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV003'),'DOG',N'Mọi cân nặng',0,NULL,80000);
IF NOT EXISTS(SELECT 1 FROM bang_gia_spa WHERE goi_id=(SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV003') AND loai_thu_cung='CAT' AND khoang_can=N'Mọi cân nặng') INSERT INTO bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia) VALUES((SELECT id FROM goi_dich_vu_spa WHERE ma_goi=N'DV003'),'CAT',N'Mọi cân nặng',0,NULL,80000);
IF NOT EXISTS(SELECT 1 FROM chuong_trinh_khuyen_mai WHERE ma_chuong_trinh=N'KM001')
 INSERT INTO chuong_trinh_khuyen_mai(ma_chuong_trinh,ten,loai_giam,gia_tri,bat_dau,ket_thuc,nguon,trang_thai,don_toi_thieu) VALUES(N'KM001',N'Giảm 10% sản phẩm trong tháng',N'PERCENT',10,DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1),DATEADD(month,1,DATEFROMPARTS(YEAR(GETDATE()),MONTH(GETDATE()),1)),N'PRODUCT',N'ACTIVE',0);
IF NOT EXISTS(SELECT 1 FROM voucher WHERE ma_voucher=N'VC-WELCOME50')
 INSERT INTO voucher(ma_voucher,menh_gia,hieu_luc_den,so_luong,da_dung,don_toi_thieu,chi_ap_dung_spa) VALUES(N'VC-WELCOME50',50000,DATEADD(day,90,GETDATE()),100,0,300000,0);
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'VAT_PERCENT')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'VAT_PERCENT',N'8');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'LOW_STOCK_THRESHOLD')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'LOW_STOCK_THRESHOLD',N'10');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'EXPIRY_WARN_DAYS')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'EXPIRY_WARN_DAYS',N'30');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'LATE_CHECKOUT_FEE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'LATE_CHECKOUT_FEE',N'50000');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'RETURN_WINDOW_DAYS')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'RETURN_WINDOW_DAYS',N'3');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'WRITEOFF_APPROVAL_LIMIT')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'WRITEOFF_APPROVAL_LIMIT',N'500000');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'VND_PER_EARNED_POINT')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'VND_PER_EARNED_POINT',N'10000');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'VND_PER_REDEEMED_POINT')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'VND_PER_REDEEMED_POINT',N'0');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'TIER_SPEND')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'TIER_SPEND',N'2000000,10000000,25000000,50000000');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'STORE_NAME')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'STORE_NAME',N'Pet Station');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'STORE_ADDRESS')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'STORE_ADDRESS',N'123 Phố Huế, Hà Nội');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'STORE_PHONE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'STORE_PHONE',N'0243825678');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'STORE_TAX_CODE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'STORE_TAX_CODE',N'');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'TIME_ZONE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'TIME_ZONE',N'Asia/Ho_Chi_Minh');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'LANGUAGE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'LANGUAGE',N'Tiếng Việt');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'CURRENCY')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'CURRENCY',N'VND');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'PRINTER')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'PRINTER',N'');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'PAPER_SIZE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'PAPER_SIZE',N'80mm');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'ALERT_LOW_STOCK')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'ALERT_LOW_STOCK',N'true');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'ALERT_NEW_ORDER')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'ALERT_NEW_ORDER',N'true');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'ALERT_SPA')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'ALERT_SPA',N'true');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'DAILY_REPORT')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'DAILY_REPORT',N'false');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa=N'REPORT_TIME')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'REPORT_TIME',N'21:00');
-- Tài khoản mẫu bị khóa và chưa có mật khẩu đăng nhập.
-- Quản lý phải đặt mật khẩu riêng rồi mở khóa ở màn Tài khoản.
DECLARE @demoHash varchar(100)='!RESET_REQUIRED!';
INSERT INTO nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)
SELECT v.code,v.full_name,v.phone,v.position,CAST(GETDATE() AS date)
FROM (VALUES
 ('NVDEMO001',N'Quản lý trình diễn','0999000001',N'Cửa hàng trưởng'),
 ('NVDEMO002',N'Bán hàng trình diễn','0999000002',N'Nhân viên bán hàng'),
 ('NVDEMO003',N'Kho trình diễn','0999000003',N'Nhân viên kho'),
 ('NVDEMO004',N'Spa Hotel trình diễn','0999000004',N'Nhân viên Spa/Hotel')
)v(code,full_name,phone,position)
WHERE NOT EXISTS(SELECT 1 FROM nhan_vien n WHERE n.ma_nhan_vien=v.code OR n.so_dien_thoai=v.phone);
INSERT INTO tai_khoan(nhan_vien_id,ten_dang_nhap,mat_khau_hash,vai_tro,bat_buoc_doi_mat_khau,trang_thai)
SELECT n.id,v.username,@demoHash,v.role,1,'INACTIVE'
FROM (VALUES
 ('NVDEMO001','demo.admin','STORE_MANAGER'),
 ('NVDEMO002','demo.sales','SALES_STAFF'),
 ('NVDEMO003','demo.stock','WAREHOUSE_STAFF'),
 ('NVDEMO004','demo.spa','SPA_HOTEL_STAFF')
)v(code,username,role) JOIN nhan_vien n ON n.ma_nhan_vien=v.code
WHERE NOT EXISTS(SELECT 1 FROM tai_khoan t WHERE t.ten_dang_nhap=v.username OR t.nhan_vien_id=n.id);

-- Lịch làm việc mẫu cho tuần hiện tại, chỉ bổ sung các ô chưa tồn tại.
DECLARE @monday date=DATEADD(day,-(DATEDIFF(day,CONVERT(date,'19000101'),CAST(GETDATE() AS date))%7),CAST(GETDATE() AS date));
INSERT INTO lich_lam_viec(nhan_vien_id,ca_lam_viec_id,ngay_lam,loai)
SELECT n.id,c.id,DATEADD(day,d.number,@monday),'WORK'
FROM nhan_vien n CROSS JOIN ca_lam_viec c CROSS JOIN (VALUES(0),(1),(2),(3),(4))d(number)
WHERE n.ma_nhan_vien IN ('NVDEMO001','NVDEMO002','NVDEMO003','NVDEMO004') AND c.ma_ca='CA_SANG'
AND NOT EXISTS(SELECT 1 FROM lich_lam_viec l WHERE l.nhan_vien_id=n.id AND l.ca_lam_viec_id=c.id AND l.ngay_lam=DATEADD(day,d.number,@monday));

-- Lịch hẹn có thể tiếp nhận và hợp đồng có thể check-in để thử toàn bộ luồng.
IF NOT EXISTS(SELECT 1 FROM lich_hen WHERE ma_lich_hen='LH-SAMPLE-001')
 INSERT INTO lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
 SELECT 'LH-SAMPLE-001',p.khach_hang_id,p.id,'SPA',DATEADD(hour,9,CAST(CAST(GETDATE() AS date) AS datetime2)),DATEADD(hour,10,CAST(CAST(GETDATE() AS date) AS datetime2)),'SCHEDULED',n.id,g.id,N'Lịch hẹn từ dữ liệu mẫu'
 FROM thu_cung p CROSS JOIN nhan_vien n CROSS JOIN goi_dich_vu_spa g
 WHERE p.ma_thu_cung='TC001' AND n.ma_nhan_vien='NVDEMO004' AND g.ma_goi='DV001';
IF NOT EXISTS(SELECT 1 FROM hop_dong_luu_tru WHERE ma_hop_dong='HT-SAMPLE-001')
 INSERT INTO hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu)
 SELECT 'HT-SAMPLE-001',p.khach_hang_id,p.id,r.id,CAST(GETDATE() AS date),DATEADD(day,2,CAST(GETDATE() AS date)),50000,'BOOKED',r.gia_dem,N'Hợp đồng từ dữ liệu mẫu'
 FROM thu_cung p CROSS JOIN phong_hotel r
 WHERE p.ma_thu_cung='TC002' AND r.ma_phong='P02' AND r.trang_thai='AVAILABLE'
 AND NOT EXISTS(SELECT 1 FROM hop_dong_luu_tru h WHERE (h.phong_id=r.id OR h.thu_cung_id=p.id) AND h.trang_thai IN ('BOOKED','CHECKED_IN') AND h.ngay_vao<DATEADD(day,2,CAST(GETDATE() AS date)) AND h.ngay_ra_du_kien>CAST(GETDATE() AS date));

-- Một giao dịch mẫu có đầy đủ phiếu nhập, lô, hóa đơn và phân bổ tồn.
IF NOT EXISTS(SELECT 1 FROM hoa_don WHERE so_hoa_don='HD-SAMPLE-001')
AND EXISTS(SELECT 1 FROM tai_khoan WHERE ten_dang_nhap='demo.admin')
BEGIN
 DECLARE @sampleAccount bigint=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.admin');
 DECLARE @sampleProduct bigint=(SELECT id FROM san_pham WHERE ma_san_pham='SP001');
 DECLARE @sampleCustomer bigint=(SELECT id FROM khach_hang WHERE ma_khach_hang='KH001');
 DECLARE @sampleSupplier bigint=(SELECT id FROM nha_cung_cap WHERE ma_ncc='NCC001');
 DECLARE @samplePrice decimal(18,2)=(SELECT gia_ban FROM san_pham WHERE id=@sampleProduct);
 DECLARE @sampleCost decimal(18,2)=COALESCE((SELECT gia_nhap_gan_nhat FROM san_pham WHERE id=@sampleProduct),@samplePrice);
 IF @sampleProduct IS NOT NULL AND @sampleCustomer IS NOT NULL AND @sampleSupplier IS NOT NULL
 BEGIN
  INSERT INTO phieu_nhap_kho(so_phieu,nha_cung_cap_id,nguoi_lap_id,ngay_nhap,tong_tien,da_tra,trang_thai)
  VALUES('PN-SAMPLE-001',@sampleSupplier,@sampleAccount,CAST(GETDATE() AS date),3*@sampleCost,@sampleCost,'POSTED');
  DECLARE @samplePurchase bigint=SCOPE_IDENTITY();
  INSERT INTO chi_tiet_phieu_nhap(phieu_nhap_id,san_pham_id,so_lo,han_su_dung,so_luong,gia_nhap)
  VALUES(@samplePurchase,@sampleProduct,'SAMPLE-RECEIPT',DATEADD(day,180,CAST(GETDATE() AS date)),3,@sampleCost);
  INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)
  VALUES(@sampleProduct,'SAMPLE-RECEIPT',DATEADD(day,180,CAST(GETDATE() AS date)),2);
  DECLARE @sampleBatch bigint=SCOPE_IDENTITY();
  INSERT INTO phien_lam_viec(tai_khoan_id,ten_ca,mo_luc,dong_luc,tien_dau_ca,tien_thuc_te,chenh_lech,trang_thai)
  VALUES(@sampleAccount,N'Ca dữ liệu mẫu',DATEADD(hour,-2,SYSDATETIME()),SYSDATETIME(),1000000,1000000+@samplePrice,0,'HANDED_OVER');
  DECLARE @sampleShift bigint=SCOPE_IDENTITY();
  INSERT INTO don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai,tao_luc)
  VALUES('DH-SAMPLE-001',@sampleShift,@sampleCustomer,'PAID',SYSDATETIME());
  DECLARE @sampleOrder bigint=SCOPE_IDENTITY();
  INSERT INTO chi_tiet_don_hang(don_hang_id,san_pham_id,ten_hang,so_luong,don_gia)
  SELECT @sampleOrder,id,ten,1,@samplePrice FROM san_pham WHERE id=@sampleProduct;
  DECLARE @sampleLine bigint=SCOPE_IDENTITY();
  INSERT INTO phan_bo_lo_hang(chi_tiet_don_hang_id,lo_hang_id,so_luong) VALUES(@sampleLine,@sampleBatch,1);
  DECLARE @samplePoints int=CAST(@samplePrice/10000 AS int);
  INSERT INTO hoa_don(so_hoa_don,don_hang_id,tam_tinh,giam_gia,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,trang_thai,thanh_toan_luc,idempotency_key)
  VALUES('HD-SAMPLE-001',@sampleOrder,@samplePrice,0,@samplePrice,'CASH',@samplePrice,0,@samplePoints,'PAID',SYSDATETIME(),'SEED-HD-SAMPLE-001');
  DECLARE @sampleInvoice bigint=SCOPE_IDENTITY();
  UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+@samplePoints WHERE id=@sampleCustomer;
  INSERT INTO lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do,tao_luc)
  SELECT id,@sampleInvoice,@samplePoints,diem_tich_luy,N'Hóa đơn dữ liệu mẫu',SYSDATETIME() FROM khach_hang WHERE id=@sampleCustomer;
 END;
END;

IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='STORE_EMAIL')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES('STORE_EMAIL',N'');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='STORE_WEBSITE')
 INSERT INTO cai_dat(khoa,gia_tri) VALUES('STORE_WEBSITE',N'');
COMMIT;
GO
IF EXISTS(SELECT 1 FROM dbo.cai_dat WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT') RETURN;
-- Cấu hình QR do quản lý nhập; không ghi tài khoản cá nhân vào mã nguồn.
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='BANK_BIN') INSERT cai_dat(khoa,gia_tri) VALUES('BANK_BIN',N'');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='BANK_ACCOUNT') INSERT cai_dat(khoa,gia_tri) VALUES('BANK_ACCOUNT',N'');
IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='BANK_ACCOUNT_NAME') INSERT cai_dat(khoa,gia_tri) VALUES('BANK_ACCOUNT_NAME',N'');
GO
IF EXISTS(SELECT 1 FROM dbo.cai_dat WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT') RETURN;
-- DEMO_COMPLETION_DATA_V1: chỉ thêm tình huống có mã riêng, không nạp lại tồn/điểm đã dùng.
SET XACT_ABORT ON;
BEGIN TRANSACTION;
INSERT khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,diem_tich_luy)
SELECT v.ma,v.ten,v.sdt,v.diem FROM (VALUES
 ('KH-DEMO-199',N'DEMO Khách 199 điểm','0999199199',199),
 ('KH-DEMO-200',N'DEMO Khách 200 điểm','0999200200',200),
 ('KH-DEMO-SALES',N'DEMO Khách mua phụ kiện','0999111222',0)
)v(ma,ten,sdt,diem)
WHERE NOT EXISTS(SELECT 1 FROM khach_hang k WHERE k.ma_khach_hang=v.ma OR k.so_dien_thoai=v.sdt);

INSERT lich_su_diem(khach_hang_id,thay_doi,so_du_sau,ly_do)
SELECT k.id,k.diem_tich_luy,k.diem_tich_luy,N'Số dư đầu tình huống DEMO'
FROM khach_hang k WHERE k.ma_khach_hang IN('KH-DEMO-199','KH-DEMO-200')
AND NOT EXISTS(SELECT 1 FROM lich_su_diem l WHERE l.khach_hang_id=k.id);

INSERT thu_cung(ma_thu_cung,khach_hang_id,ten,loai,can_nang,ngay_sinh)
SELECT v.ma,k.id,v.ten,'DOG',4,DATEADD(month,-6,CAST(GETDATE() AS date))
FROM (VALUES('TC-DEMO-OK',N'DEMO Đủ tiêm phòng'),('TC-DEMO-THIEU',N'DEMO Thiếu tiêm phòng'))v(ma,ten)
JOIN khach_hang k ON k.ma_khach_hang='KH-DEMO-200'
WHERE NOT EXISTS(SELECT 1 FROM thu_cung t WHERE t.ma_thu_cung=v.ma);

INSERT tiem_phong(thu_cung_id,ten_vaccine,ngay_tiem,ghi_chu)
SELECT t.id,v.ten,DATEADD(day,v.lui,CAST(GETDATE() AS date)),N'Tình huống tiêm phòng DEMO'
FROM thu_cung t CROSS JOIN (VALUES(N'DEMO Mũi 1',-50),(N'DEMO Mũi 2',-20))v(ten,lui)
WHERE t.ma_thu_cung='TC-DEMO-OK'
AND NOT EXISTS(SELECT 1 FROM tiem_phong tp WHERE tp.thu_cung_id=t.id AND tp.ten_vaccine=v.ten);

INSERT san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd)
SELECT v.ma,v.ten,N'Phụ kiện',N'Cái',v.gia,50000,5,0
FROM (VALUES('SP-DEMO-A',N'DEMO Vòng cổ cỡ S',100000),('SP-DEMO-B',N'DEMO Vòng cổ cỡ M',150000))v(ma,ten,gia)
WHERE NOT EXISTS(SELECT 1 FROM san_pham p WHERE p.ma_san_pham=v.ma);

-- Tồn đầu tình huống. Không bù lại tồn sau khi đã demo bán/đổi hàng.
INSERT lo_hang(san_pham_id,so_lo,so_luong_ton)
SELECT p.id,'DEMO-OPENING',20 FROM san_pham p
WHERE p.ma_san_pham IN('SP-DEMO-A','SP-DEMO-B')
AND NOT EXISTS(SELECT 1 FROM lo_hang l WHERE l.san_pham_id=p.id AND l.so_lo='DEMO-OPENING');

IF NOT EXISTS(SELECT 1 FROM phong_hotel WHERE ma_phong='P-DEMO')
 INSERT phong_hotel(ma_phong,loai_phong,suc_chua,gia_dem,trang_thai,ghi_chu)
 VALUES('P-DEMO',N'Tiêu chuẩn',1,200000,'AVAILABLE',N'Phòng dành cho kịch bản DEMO');

IF NOT EXISTS(SELECT 1 FROM goi_dich_vu_spa WHERE ma_goi='DV-DEMO')
 INSERT goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien,mo_ta)
 VALUES('DV-DEMO',N'DEMO Tắm và chăm sóc',60,N'Gói dùng trình diễn voucher và thanh toán');
INSERT bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)
SELECT g.id,'DOG',N'Dưới 5kg',0,5,200000 FROM goi_dich_vu_spa g
WHERE g.ma_goi='DV-DEMO'
AND NOT EXISTS(SELECT 1 FROM bang_gia_spa b WHERE b.goi_id=g.id AND b.loai_thu_cung='DOG');
COMMIT;
GO
IF EXISTS(SELECT 1 FROM dbo.cai_dat WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT') RETURN;
-- REAL_DATA_V1: Danh mục thật và giao dịch GIẢ LẬP phục vụ demo; chỉ nhập một lần.
-- Nguồn và ảnh: docs/data/san-pham-nguon.json. Giá nhập, HSD, tồn, khách và giao dịch KHÔNG phải số liệu thực tế.
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRANSACTION;
 DECLARE @khoaNhap INT;
 EXEC @khoaNhap=sys.sp_getapplock @Resource='PetStation_REAL_DATA_V1',@LockMode='Exclusive',@LockOwner='Transaction',@LockTimeout=10000;
 IF @khoaNhap<0 THROW 51000,N'Không khóa được bộ dữ liệu demo.',1;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='REAL_DATA_V1_APPLIED_AT')
 BEGIN
 DECLARE @sp TABLE(stt INT,ma VARCHAR(30),ten NVARCHAR(180),nhom NVARCHAR(100),don_vi NVARCHAR(30),gia BIGINT,anh NVARCHAR(500),thuong_hieu NVARCHAR(100),quy_cach NVARCHAR(100),doi_tuong NVARCHAR(100),mo_ta NVARCHAR(1000),nguon NVARCHAR(1000),ngay DATE);
 INSERT @sp VALUES
(1,N'SP-VS-001',N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 (lít); Đóng gói: 1 túi',N'Vệ sinh',N'Gói',160000,N'images/products/SP-VS-001.jpg',N'IRIS OHYAMA',N'Dung tích: 10 (lít); Đóng gói: 1 túi',N'Mèo',N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 10 (lít); Đóng gói: 1 túi. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/cat-ve-sinh-cho-meo-sieu-von-iris-ohyama-kfag-100',N'2026-10-19'),
(2,N'SP-TA-001',N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói',N'Thức ăn',N'Gói',10000,N'images/products/SP-TA-001.jpg',N'VEGEBRAND',N'Đóng gói: 1 gói',N'Chó',N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: Đóng gói: 1 gói. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/xuong-cho-cho-gam-sach-rang-vegebrand-360-bone-prevent-tartar',N'2026-10-19'),
(3,N'SP-TA-002',N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800 (gr)',N'Thức ăn',N'Gói',225000,N'images/products/SP-TA-002.jpg',N'ROYAL CANIN',N'Trọng lượng: 800 (gr)',N'Chó',N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 800 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-cho-con-co-nho-royal-canin-mini-puppy',N'2026-10-19'),
(4,N'SP-VS-002',N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150 (ml)',N'Vệ sinh',N'Cái',150000,N'images/products/SP-VS-002.jpg',N'JOYCE & DOLLS',N'Dung tích: 150 (ml)',N'Chó và mèo',N'Vệ sinh thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 150 (ml). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/chai-xit-cho-meo-di-ve-sinh-dung-cho-joyce-dolls-trainer-puppy',N'2026-10-19'),
(5,N'SP-TA-003',N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Thức ăn',N'Gói',30000,N'images/products/SP-TA-003.jpg',N'CAT SEA FISH',N'Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Mèo',N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85 (gr); Đóng gói: 1 lon. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-nguyen-chat-cat-sea-fish-pure-tuna-meat',N'2026-10-19'),
(6,N'SP-TA-004',N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400 (gr)',N'Thức ăn',N'Gói',130000,N'images/products/SP-TA-004.jpg',N'ROYAL CANIN',N'Trọng lượng: 400 (gr)',N'Mèo',N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-meo-con-royal-canin-kitten',N'2026-10-19'),
(7,N'SP-TA-005',N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85 (gr)',N'Thức ăn',N'Gói',25000,N'images/products/SP-TA-005.jpg',N'WHISKAS',N'Trọng lượng: 85 (gr)',N'Mèo',N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-ca-ngu-whiskas-tuna-flavour-sauce',N'2026-10-19'),
(8,N'SP-VS-003',N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 (lít)',N'Vệ sinh',N'Gói',160000,N'images/products/SP-VS-003.jpg',N'TRIXIE',N'Dung tích: 5 (lít)',N'Mèo',N'Vệ sinh thương hiệu TRIXIE, dành cho mèo. Quy cách: Dung tích: 5 (lít). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/cat-ve-sinh-thuy-tinh-cho-meo-trixie-silikatstreu',N'2026-10-19'),
(9,N'SP-TA-006',N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400 (gr)',N'Thức ăn',N'Gói',125000,N'images/products/SP-TA-006.jpg',N'ROYAL CANIN',N'Trọng lượng: 400 (gr)',N'Mèo',N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-indoor-27',N'2026-10-19'),
(10,N'SP-TA-007',N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85 (gr)',N'Thức ăn',N'Gói',25000,N'images/products/SP-TA-007.jpg',N'WHISKAS',N'Trọng lượng: 85 (gr)',N'Mèo',N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-vi-nuoc-sot-thit-bo-whiskas-beef-flavour-sauce',N'2026-10-19'),
(11,N'SP-VS-004',N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ',N'Vệ sinh',N'Cái',5000,N'images/products/SP-VS-004.jpg',N'IRIS OHYAMA',N'Đóng gói: 1 miếng lẻ',N'Chó',N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-88',N'2026-10-19'),
(12,N'SP-TA-008',N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef — 1 sản phẩm theo mẫu của nhà sản xuất',N'Thức ăn',N'Gói',25000,N'images/products/SP-TA-008.jpg',N'VEGEBRAND',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/banh-thuong-cho-cho-vi-thit-bo-vegebrand-orgo-freshening-biscuit-bacon-beef',N'2026-10-19'),
(13,N'SP-TA-009',N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100 (gr); Đóng gói: 1 lon',N'Thức ăn',N'Gói',35000,N'images/products/SP-TA-009.jpg',N'IRIS OHYAMA',N'Trọng lượng: 100 (gr); Đóng gói: 1 lon',N'Chó',N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100 (gr); Đóng gói: 1 lon. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-cho-vi-thit-bo-iris-ohyama-one-care-beef',N'2026-10-19'),
(14,N'SP-TA-010',N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100 (gr)',N'Thức ăn',N'Gói',25000,N'images/products/SP-TA-010.jpg',N'PEDIGREE',N'Trọng lượng: 100 (gr)',N'Chó',N'Thức ăn thương hiệu PEDIGREE, dành cho chó. Quy cách: Trọng lượng: 100 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-cho-nuoc-sot-vi-thit-bo-pedigree-pouch-beef',N'2026-10-19'),
(15,N'SP-TA-011',N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500 (gr)',N'Thức ăn',N'Gói',185000,N'images/products/SP-TA-011.jpg',N'ROYAL CANIN',N'Trọng lượng: 500 (gr)',N'Chó',N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 500 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-cho-poodle-con-royal-canin-poodle-puppy',N'2026-10-19'),
(16,N'SP-TA-012',N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85 (gr)',N'Thức ăn',N'Gói',25000,N'images/products/SP-TA-012.jpg',N'WHISKAS',N'Trọng lượng: 85 (gr)',N'Mèo',N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/nuoc-sot-cho-meo-vi-thit-ga-whiskas-chicken-flavour-in-sauce',N'2026-10-19'),
(17,N'SP-TA-013',N'Pate cho mèo mẹ và mèo con CATIDEA Mother & Baby Cat — Trọng lượng: 85 (gr)',N'Thức ăn',N'Gói',30000,N'images/products/SP-TA-013.jpg',N'CATIDEA',N'Trọng lượng: 85 (gr)',N'Mèo',N'Thức ăn thương hiệu CATIDEA, dành cho mèo. Quy cách: Trọng lượng: 85 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-me-va-meo-con-catidea-mother-baby-cat',N'2026-10-19'),
(18,N'SP-TA-014',N'Pate cho mèo vị cá ngừ thanh cua CAT SEA FISH Tuna Surimi — Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Thức ăn',N'Gói',30000,N'images/products/SP-TA-014.jpg',N'CAT SEA FISH',N'Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Mèo',N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85 (gr); Đóng gói: 1 lon. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-thanh-cua-cat-sea-fish-tuna-surimi',N'2026-10-19'),
(19,N'SP-TA-015',N'Pate cho mèo CIAO Tuna & Whitebait vị cá ngừ và cá chạch trắng — Trọng lượng: 60 (gr)',N'Thức ăn',N'Gói',20000,N'images/products/SP-TA-015.jpg',N'CIAO',N'Trọng lượng: 60 (gr)',N'Mèo',N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-whitebait-vi-ca-ngu-va-ca-chach-trang',N'2026-10-19'),
(20,N'SP-VS-005',N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1 (kg)',N'Vệ sinh',N'Gói',140000,N'images/products/SP-VS-005.jpg',N'CATS BEST',N'Trọng lượng: 2.1 (kg)',N'Mèo',N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.1 (kg). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-original',N'2026-10-19'),
(21,N'SP-TA-016',N'Xương cho chó vị thịt bò VEGEBRAND Orgo Nutrients Beef — 1 sản phẩm theo mẫu của nhà sản xuất',N'Thức ăn',N'Gói',25000,N'images/products/SP-TA-016.jpg',N'VEGEBRAND',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/xuong-cho-cho-vi-thit-bo-vegebrand-orgo-nutrients-beef',N'2026-10-19'),
(22,N'SP-TA-017',N'Thức ăn cho mèo con và mèo mẹ ROYAL CANIN Mother & Babycat — Trọng lượng: 400 (gr)',N'Thức ăn',N'Gói',130000,N'images/products/SP-TA-017.jpg',N'ROYAL CANIN',N'Trọng lượng: 400 (gr)',N'Mèo',N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-meo-con-va-meo-me-royal-canin-mother-babycat',N'2026-10-19'),
(23,N'SP-TA-018',N'Pate cho mèo vị cá ngừ cá thu CAT SEA FISH Tuna Mackerel — Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Thức ăn',N'Gói',30000,N'images/products/SP-TA-018.jpg',N'CAT SEA FISH',N'Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Mèo',N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85 (gr); Đóng gói: 1 lon. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-thu-cat-sea-fish-tuna-mackerel',N'2026-10-19'),
(24,N'SP-TA-019',N'Thức ăn cho mèo trưởng thành ROYAL CANIN Regular Fit 32 — Trọng lượng: 400 (gr)',N'Thức ăn',N'Gói',115000,N'images/products/SP-TA-019.jpg',N'ROYAL CANIN',N'Trọng lượng: 400 (gr)',N'Mèo',N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-meo-truong-thanh-royal-canin-regular-fit-32',N'2026-10-19'),
(25,N'SP-TA-020',N'Thức ăn cho chó Poodle MKB All Life Stages Formula Nutrition — Trọng lượng: 1 (kg)',N'Thức ăn',N'Gói',315000,N'images/products/SP-TA-020.jpg',N'MKB',N'Trọng lượng: 1 (kg)',N'Chó',N'Thức ăn thương hiệu MKB, dành cho chó. Quy cách: Trọng lượng: 1 (kg). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/thuc-an-cho-cho-poodle-mkb-all-life-stages-formula-nutrition',N'2026-10-19'),
(26,N'SP-TA-021',N'Pate cho chó hỗ trợ chức năng gan và sáng mắt IRIS OHYAMA Benefit For Eyes & Liver — Trọng lượng: 100 (gr); Đóng gói: 1 lon',N'Thức ăn',N'Gói',35000,N'images/products/SP-TA-021.jpg',N'IRIS OHYAMA',N'Trọng lượng: 100 (gr); Đóng gói: 1 lon',N'Chó',N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100 (gr); Đóng gói: 1 lon. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-cho-ho-tro-chuc-nang-gan-va-sang-mat-iris-ohyama-benefit-for-eyes-liver',N'2026-10-19'),
(27,N'SP-TA-022',N'Pate cho mèo CIAO Tuna & Salmon vị cá ngừ và cá hồi — Trọng lượng: 60 (gr)',N'Thức ăn',N'Gói',20000,N'images/products/SP-TA-022.jpg',N'CIAO',N'Trọng lượng: 60 (gr)',N'Mèo',N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60 (gr). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-ciao-tuna-salmon-vi-ca-ngu-va-ca-hoi',N'2026-10-19'),
(28,N'SP-PK-001',N'Đồ chơi cho chó mèo dạng bóng thừng BOBO — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',30000,N'images/products/SP-PK-001.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/do-choi-cho-cho-meo-dang-bong-thung-bobo',N'2026-10-19'),
(29,N'SP-TA-023',N'Pate cho mèo vị cá ngừ cá cơm CAT SEA FISH Tuna Anchovy — Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Thức ăn',N'Gói',30000,N'images/products/SP-TA-023.jpg',N'CAT SEA FISH',N'Trọng lượng: 85 (gr); Đóng gói: 1 lon',N'Mèo',N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85 (gr); Đóng gói: 1 lon. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/pate-cho-meo-vi-ca-ngu-ca-com-cat-sea-fish-tuna-anchovy',N'2026-10-19'),
(30,N'SP-TA-024',N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone — 1 sản phẩm theo mẫu của nhà sản xuất',N'Thức ăn',N'Gói',50000,N'images/products/SP-TA-024.jpg',N'VEGEBRAND',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/xuong-cho-cho-vi-thit-vit-bone-for-poodles',N'2026-10-19'),
(31,N'SP-GR-001',N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Chai',180000,N'images/products/SP-GR-001.jpg',N'JOYCE & DOLLS',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/sua-tam-kho-cho-cho-meo-joyce-dolls-waterless-cleansing-foam',N'2026-10-19'),
(32,N'SP-VS-006',N'Khay vệ sinh cho mèo MAKAR Cat Litter Station — 1 sản phẩm theo mẫu của nhà sản xuất',N'Vệ sinh',N'Cái',250000,N'images/products/SP-VS-006.jpg',N'MAKAR',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Mèo',N'Vệ sinh thương hiệu MAKAR, dành cho mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/khay-ve-sinh-cho-meo-makar-cat-litter-station',N'2026-10-19'),
(33,N'SP-VS-007',N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-42 — Đóng gói: 1 miếng lẻ',N'Vệ sinh',N'Cái',10000,N'images/products/SP-VS-007.jpg',N'IRIS OHYAMA',N'Đóng gói: 1 miếng lẻ',N'Chó',N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/mieng-lot-cho-cho-di-ve-sinh-iris-ohyama-clean-pet-sheets-cps-42',N'2026-10-19'),
(34,N'SP-VS-008',N'Cát vệ sinh cho mèo than hoạt tính IRIS OHYAMA Charcoal SNS-70 — Dung tích: 7 (lít); Đóng gói: 1 túi',N'Vệ sinh',N'Gói',160000,N'images/products/SP-VS-008.jpg',N'IRIS OHYAMA',N'Dung tích: 7 (lít); Đóng gói: 1 túi',N'Mèo',N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 7 (lít); Đóng gói: 1 túi. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/cat-ve-sinh-cho-meo-than-hoat-tinh-iris-ohyama-charcoal-sns-70',N'2026-10-19'),
(35,N'SP-GR-002',N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Cái',85000,N'images/products/SP-GR-002.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Grooming thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/luoc-chai-long-cho-meo-dau-luoi-mem-bobo-pet-comb',N'2026-10-19'),
(36,N'SP-VS-009',N'Cát vệ sinh hữu cơ cho mèo CATS BEST Smart Peppets — Trọng lượng: 2.5 (kg)',N'Vệ sinh',N'Gói',155000,N'images/products/SP-VS-009.jpg',N'CATS BEST',N'Trọng lượng: 2.5 (kg)',N'Mèo',N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.5 (kg). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/cat-ve-sinh-huu-co-cho-meo-cats-best-smart-peppets',N'2026-10-19'),
(37,N'SP-PK-002',N'Vòng cổ chó mèo kèm dây dắt cỡ mini HAND IN HAND — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',70000,N'images/products/SP-PK-002.jpg',N'HAND IN HAND',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-mini-hand-in-hand',N'2026-10-19'),
(38,N'SP-PK-003',N'Bóng đồ chơi cho chó mèo có chuông nhựa BOBO — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',45000,N'images/products/SP-PK-003.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/bong-do-choi-cho-cho-meo-co-chuong-nhua-bobo',N'2026-10-19'),
(39,N'SP-PK-004',N'Đồ chơi dây thừng cho chó BOBO — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',35000,N'images/products/SP-PK-004.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Phụ kiện thương hiệu BOBO, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/do-choi-day-thung-cho-cho-bobo',N'2026-10-19'),
(40,N'SP-PK-005',N'Vòng cổ chống ve chó BIOLINE Flea and Tick Collar For Dogs — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',60000,N'images/products/SP-PK-005.jpg',N'BIOLINE',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Phụ kiện thương hiệu BIOLINE, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/vong-co-chong-ve-cho-bioline-flea-and-tick-collar-for-dogs',N'2026-10-19'),
(41,N'SP-VS-010',N'Khay vệ sinh cho chó MAKAR Dog Toilet Trays Small — Kích thước: S',N'Vệ sinh',N'Cái',220000,N'images/products/SP-VS-010.jpg',N'MAKAR',N'Kích thước: S',N'Chó',N'Vệ sinh thương hiệu MAKAR, dành cho chó. Quy cách: Kích thước: S. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/khay-ve-sinh-cho-cho-makar-dog-toilet-trays-small',N'2026-10-19'),
(42,N'SP-GR-003',N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Cái',30000,N'images/products/SP-GR-003.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Grooming thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/ban-chai-tam-cho-cho-meo-bobo-pet-washing-brush',N'2026-10-19'),
(43,N'SP-PK-006',N'Đồ chơi cần câu cho mèo PAW vui nhộn — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',75000,N'images/products/SP-PK-006.jpg',N'PAW',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Mèo',N'Phụ kiện thương hiệu PAW, dành cho mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/do-choi-can-cau-cho-meo-paw-vui-nhon',N'2026-10-19'),
(44,N'SP-GR-004',N'Sữa tắm cho chó mèo hương hoa hồng JOYCE & DOLLS Rose Dew — Dung tích: 400 (ml)',N'Grooming',N'Chai',250000,N'images/products/SP-GR-004.jpg',N'JOYCE & DOLLS',N'Dung tích: 400 (ml)',N'Chó và mèo',N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400 (ml). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-hong-joyce-dolls-rose-dew',N'2026-10-19'),
(45,N'SP-PK-007',N'Đồ chơi cho chó mèo Paw hình con gà — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',50000,N'images/products/SP-PK-007.jpg',N'PAW',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu PAW, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/do-choi-cho-cho-meo-paw-hinh-con-ga',N'2026-10-19'),
(46,N'SP-PK-008',N'Bình uống nước cho chó mèo gài chuồng BOBO — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',80000,N'images/products/SP-PK-008.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/binh-uong-nuoc-cho-cho-meo-gai-chuong-bobo',N'2026-10-19'),
(47,N'SP-PK-009',N'Vòng cổ chống liếm cho chó mèo PAW Pet Elizabethan Collar — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',50000,N'images/products/SP-PK-009.jpg',N'PAW',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu PAW, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/vong-co-chong-liem-cho-cho-meo-paw-pet-elizabethan-collar',N'2026-10-19'),
(48,N'SP-GR-005',N'Sữa tắm cho chó diệt khuẩn BBN Bactericidal Shampoo Lemon — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Chai',150000,N'images/products/SP-GR-005.jpg',N'BBN',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Grooming thương hiệu BBN, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/sua-tam-cho-cho-diet-khuan-bbn-bactericidal-shampoo-lemon',N'2026-10-19'),
(49,N'SP-GR-006',N'Găng tay tắm cho chó mèo BOBO Pet Grooming Brush Gloves — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Cái',40000,N'images/products/SP-GR-006.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Grooming thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/gang-tay-tam-cho-cho-meo-bobo-pet-grooming-brush-gloves',N'2026-10-19'),
(50,N'SP-GR-007',N'Kềm cắt bấm móng chó mèo PAW Safety Guard Clipper — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Cái',70000,N'images/products/SP-GR-007.jpg',N'PAW',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Grooming thương hiệu PAW, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/kem-cat-bam-mong-cho-meo-paw-safety-guard-clipper',N'2026-10-19'),
(51,N'SP-GR-008',N'Sữa tắm cho chó lông trắng BBN Whitening Shampoo — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Chai',150000,N'images/products/SP-GR-008.jpg',N'BBN',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó',N'Grooming thương hiệu BBN, dành cho chó. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/sua-tam-cho-cho-long-trang-bbn-whitening-shampoo',N'2026-10-19'),
(52,N'SP-PK-010',N'Đồ chơi dây thừng cho chó mèo đan chéo BOBO — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',55000,N'images/products/SP-PK-010.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-dan-cheo-bobo',N'2026-10-19'),
(53,N'SP-PK-011',N'Bát ăn cho chó mèo nhựa mềm SAWYER PET — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',40000,N'images/products/SP-PK-011.jpg',N'SAWYER PET',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/bat-an-cho-cho-meo-nhua-mem-sawyer-pet',N'2026-10-19'),
(54,N'SP-GR-009',N'Sữa tắm cho chó mèo hương hoa nhài JOYCE & DOLLS Moder Jasmine — Dung tích: 400 (ml)',N'Grooming',N'Chai',250000,N'images/products/SP-GR-009.jpg',N'JOYCE & DOLLS',N'Dung tích: 400 (ml)',N'Chó và mèo',N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400 (ml). Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/sua-tam-cho-cho-meo-huong-hoa-nhai-joyce-dolls-moder-jasmine',N'2026-10-19'),
(55,N'SP-GR-010',N'Lược chải lông chó mèo nút bấm KUDI Pet Grooming Slicker Brushes — 1 sản phẩm theo mẫu của nhà sản xuất',N'Grooming',N'Cái',170000,N'images/products/SP-GR-010.jpg',N'KUDI',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Grooming thương hiệu KUDI, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/luoc-chai-long-cho-meo-nut-bam-kudi-pet-grooming-slicker-brushes',N'2026-10-19'),
(56,N'SP-PK-012',N'Thảm mát giải nhiệt cho chó mèo PAW Pet Cool Mat — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',195000,N'images/products/SP-PK-012.jpg',N'PAW',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu PAW, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/tham-mat-giai-nhiet-cho-cho-meo-paw-pet-cool-mat',N'2026-10-19'),
(57,N'SP-PK-013',N'Vòng cổ chó mèo kèm dây dắt cỡ bé HAND IN HAND — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',80000,N'images/products/SP-PK-013.jpg',N'HAND IN HAND',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/vong-co-cho-meo-kem-day-dat-co-be-hand-in-hand',N'2026-10-19'),
(58,N'SP-PK-014',N'Bát ăn đôi cho chó mèo bằng nhựa SAWYER PET — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',60000,N'images/products/SP-PK-014.jpg',N'SAWYER PET',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/bat-an-doi-cho-cho-meo-bang-nhua-sawyer-pet',N'2026-10-19'),
(59,N'SP-PK-015',N'Đồ chơi dây thừng cho chó mèo PAW loại dài — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',50000,N'images/products/SP-PK-015.jpg',N'PAW',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu PAW, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/do-choi-day-thung-cho-cho-meo-paw-loai-dai',N'2026-10-19'),
(60,N'SP-PK-016',N'Bát ăn cho chó mèo bằng nhựa BOBO Plastic Bowl 3038 — 1 sản phẩm theo mẫu của nhà sản xuất',N'Phụ kiện',N'Cái',35000,N'images/products/SP-PK-016.jpg',N'BOBO',N'1 sản phẩm theo mẫu của nhà sản xuất',N'Chó và mèo',N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo. Quy cách: 1 sản phẩm theo mẫu của nhà sản xuất. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.',N'https://www.petmart.vn/bat-an-cho-cho-meo-bang-nhua-bobo-plastic-bowl-3038',N'2026-10-19');
 IF EXISTS(SELECT 1 FROM san_pham p JOIN @sp s ON p.ma_san_pham=s.ma OR p.ten=s.ten)
  THROW 51001,N'Mã hoặc tên sản phẩm bộ REAL_DATA_V1 đã tồn tại nhưng chưa có dấu xác nhận; cần đối chiếu trước khi nhập.',1;
 DECLARE @quanLy BIGINT=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.admin'),
  @banHang BIGINT=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.sales'),
  @kho BIGINT=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.stock'),
  @nvSpa BIGINT=(SELECT nhan_vien_id FROM tai_khoan WHERE ten_dang_nhap='demo.spa'),
  @homNay DATE=CAST(SYSDATETIME() AS DATE), @moc DATE=DATEADD(day,-29,CAST(SYSDATETIME() AS DATE));
 IF @quanLy IS NULL OR @banHang IS NULL OR @kho IS NULL OR @nvSpa IS NULL
  THROW 51002,N'Thiếu bốn tài khoản demo trong dữ liệu nền.',1;
 INSERT san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd,anh,thuong_hieu,quy_cach,doi_tuong_su_dung,mo_ta,nguon_url,ngay_thu_thap)
 SELECT ma,ten,nhom,don_vi,gia,ROUND(gia*0.65,0),5,CASE WHEN nhom IN(N'Thức ăn',N'Grooming') THEN 1 ELSE 0 END,anh,thuong_hieu,quy_cach,doi_tuong,mo_ta,nguon,ngay FROM @sp;
 INSERT lich_su_gia(san_pham_id,gia_cu,gia_moi,nguoi_thay_doi_id,thoi_diem_thay_doi)
 SELECT p.id,0,p.gia_ban,@quanLy,DATEADD(hour,7,CAST(@moc AS DATETIME2)) FROM san_pham p JOIN @sp s ON s.ma=p.ma_san_pham;
 DECLARE @i INT=1,@kh BIGINT,@tc BIGINT,@sanPham BIGINT,@lo BIGINT,@phieu BIGINT,@don BIGINT,@hd BIGINT,@dong BIGINT,@ca BIGINT,
  @gia BIGINT,@tong BIGINT,@giam BIGINT,@diem INT,@soLuong INT,@luc DATETIME2,@ma VARCHAR(30),@ncc BIGINT,@giaNhap BIGINT,@nhap INT,
  @goi BIGINT,@phong BIGINT,@coc BIGINT,@hopDong BIGINT,@phieuSpa BIGINT,@lichHen BIGINT,@goc BIGINT,@dongGoc BIGINT,@loGoc BIGINT,@tienDoi BIGINT;
 -- Khách hàng và thông tin liên hệ dưới đây hoàn toàn giả lập, không gửi thư hay gọi điện.
 WHILE @i<=20 BEGIN
  SET @ma='KH-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2);
  INSERT khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,ngay_tao)
  VALUES(@ma,CHOOSE((@i-1)%10+1,N'Nguyễn Minh Anh',N'Trần Gia Hân',N'Lê Hoàng Nam',N'Phạm Ngọc Linh',N'Võ Khánh An',N'Đặng Thanh Tâm',N'Bùi Quang Huy',N'Đỗ Bảo Ngọc',N'Hồ Minh Khang',N'Dương Tuệ Nhi')+N' (DEMO '+CAST(@i AS NVARCHAR)+N')','000900'+RIGHT('0000'+CAST(@i AS VARCHAR),4),'demo'+CAST(@i AS VARCHAR)+'@example.invalid',N'Địa chỉ giả lập, TP.HCM',N'REAL_DATA_V1 — hồ sơ tình huống giả lập',@moc);
  SET @i+=1;
 END;
 SET @i=1;
 WHILE @i<=30 BEGIN
  SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-'+RIGHT('00'+CAST((@i-1)%20+1 AS VARCHAR),2);
  INSERT thu_cung(ma_thu_cung,khach_hang_id,ten,loai,giong,gioi_tinh,mau_long,can_nang,ngay_sinh,tinh_cach,tinh_trang_suc_khoe,tiem_chung)
  VALUES('TC-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),@kh,CHOOSE((@i-1)%10+1,N'Mochi',N'Milo',N'Bông',N'Lucky',N'Coco',N'Miu',N'Bắp',N'Đậu',N'Sữa',N'Gạo')+N' DEMO '+CAST(@i AS NVARCHAR),CASE WHEN @i%2=0 THEN 'CAT' ELSE 'DOG' END,CASE WHEN @i%2=0 THEN N'Anh lông ngắn' ELSE N'Poodle' END,CASE WHEN @i%3=0 THEN 'Cai' ELSE 'Duc' END,N'Trắng / vàng',3.5,DATEADD(month,-12-@i,@homNay),N'Hồ sơ giả lập: hiền, cần làm quen',N'DEMO — không thay thế hồ sơ thú y',CASE WHEN @i<=24 THEN N'Có hai lần tiêm giả lập, xem lịch sử' ELSE N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung' END);
  SET @tc=SCOPE_IDENTITY();
  IF @i<=24 INSERT tiem_phong(thu_cung_id,ten_vaccine,ngay_tiem,ngay_het_han,ghi_chu)
   VALUES(@tc,N'Mũi cơ bản 1 (DEMO)',DATEADD(day,-100,@homNay),DATEADD(day,265,@homNay),N'Giả lập để kiểm tra điều kiện tiếp nhận'),(@tc,N'Mũi cơ bản 2 (DEMO)',DATEADD(day,-65,@homNay),DATEADD(day,300,@homNay),N'Giả lập để kiểm tra điều kiện tiếp nhận');
  SET @i+=1;
 END;
 SET @i=1;
 WHILE @i<=6 BEGIN
  INSERT nha_cung_cap(ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,dieu_khoan)
  VALUES('NCC-REAL-DEMO-'+CAST(@i AS VARCHAR),N'Nhà cung cấp tình huống DEMO '+CAST(@i AS NVARCHAR),N'Đại diện giả lập','000800000'+CAST(@i AS VARCHAR),N'Địa chỉ giả lập',N'30 ngày — dữ liệu DEMO');
  SET @i+=1;
 END;
 -- Sáu mươi phiếu nhập giúp đối chiếu tồn và công nợ, không cộng tồn khi chạy lại.
 SET @i=1;
 WHILE @i<=60 BEGIN
  SELECT @ma=ma,@gia=gia FROM @sp WHERE stt=@i;
  SELECT @sanPham=id,@giaNhap=gia_nhap_gan_nhat FROM san_pham WHERE ma_san_pham=@ma;
  SELECT @ncc=id FROM nha_cung_cap WHERE ma_ncc='NCC-REAL-DEMO-'+CAST((@i-1)%6+1 AS VARCHAR);
  SET @nhap=10+@i%21;
  INSERT phieu_nhap_kho(so_phieu,nha_cung_cap_id,nguoi_lap_id,ngay_nhap,tong_tien,da_tra,trang_thai,ghi_chu)
  VALUES('PN-REAL-DEMO-'+RIGHT('000'+CAST(@i AS VARCHAR),3),@ncc,@kho,@moc,@nhap*@giaNhap,CASE WHEN @i%3=0 THEN @nhap*@giaNhap ELSE (@nhap*@giaNhap)/2 END,'POSTED',N'REAL_DATA_V1 — giá nhập, số lượng và HSD giả lập');
  SET @phieu=SCOPE_IDENTITY();
  INSERT chi_tiet_phieu_nhap(phieu_nhap_id,san_pham_id,so_lo,han_su_dung,so_luong,gia_nhap)
  SELECT @phieu,@sanPham,'REAL-DEMO-'+RIGHT('000'+CAST(@i AS VARCHAR),3),CASE WHEN theo_doi_hsd=1 THEN DATEADD(day,CASE WHEN @i%9=0 THEN 15 ELSE 180 END,@homNay) END,@nhap,@giaNhap FROM san_pham WHERE id=@sanPham;
  INSERT lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)
  SELECT san_pham_id,so_lo,han_su_dung,so_luong FROM chi_tiet_phieu_nhap WHERE phieu_nhap_id=@phieu;
  SET @i+=1;
 END;
 -- Gói và giá Spa do shop giả lập cho buổi demo, không mạo nhận bảng giá của nguồn sản phẩm.
 SET @i=1;
 WHILE @i<=6 BEGIN
  INSERT goi_dich_vu_spa(ma_goi,ten_goi,mo_ta,thoi_gian_du_kien)
  VALUES('DV-REAL-DEMO-'+CAST(@i AS VARCHAR),CHOOSE(@i,N'Tắm cơ bản',N'Tắm dưỡng lông',N'Cắt tỉa tạo kiểu',N'Vệ sinh tai và móng',N'Chải lông và gỡ rối',N'Chăm sóc toàn diện')+N' — DEMO',N'Dịch vụ và giá giả lập cho trình diễn',30+@i*15);
  SET @goi=SCOPE_IDENTITY();
  INSERT bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)
  SELECT @goi,l.loai,k.ten,k.tu,k.den,(100000+@i*25000)*k.he_so FROM (VALUES('DOG'),('CAT'))l(loai)
  CROSS JOIN (VALUES(N'< 5kg',0,5,1),(N'5 - 10kg',5,10,2),(N'>= 10kg',10,NULL,3))k(ten,tu,den,he_so);
  SET @i+=1;
 END;
 SET @i=1;
 WHILE @i<=10 BEGIN
  INSERT phong_hotel(ma_phong,loai_phong,gia_dem,trang_thai,suc_chua,ghi_chu)
  VALUES('P-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),CASE WHEN @i<=5 THEN N'Tiêu chuẩn DEMO' ELSE N'VIP DEMO' END,CASE WHEN @i<=5 THEN 180000 ELSE 250000 END,'AVAILABLE',1,N'REAL_DATA_V1 — phòng và giá giả lập');
  SET @phong=SCOPE_IDENTITY();
  INSERT bang_gia_hotel(phong_id,don_gia) SELECT id,gia_dem FROM phong_hotel WHERE id=@phong;
  SET @i+=1;
 END;
 DECLARE @cacCa TABLE(ngay DATE,id BIGINT);
 SET @i=0;
 WHILE @i<30 BEGIN
  INSERT phien_lam_viec(tai_khoan_id,ten_ca,mo_luc,dong_luc,tien_dau_ca,tien_thuc_te,chenh_lech,giai_trinh,trang_thai)
  VALUES(@banHang,N'Ca REAL_DATA_V1 DEMO',DATEADD(hour,8,CAST(DATEADD(day,@i,@moc) AS DATETIME2)),DATEADD(hour,20,CAST(DATEADD(day,@i,@moc) AS DATETIME2)),500000,500000,0,N'Ca lịch sử giả lập đã đối chiếu','HANDED_OVER');
  INSERT @cacCa VALUES(DATEADD(day,@i,@moc),SCOPE_IDENTITY());
  SET @i+=1;
 END;
 -- 18 hóa đơn hàng, 6 Spa, 4 Hotel, 2 đổi phụ kiện; cùng định nghĩa tiền và điểm với service.
 SET @i=1;
 WHILE @i<=30 BEGIN
  SET @sanPham=NULL;SET @lo=NULL;SET @phieuSpa=NULL;SET @hopDong=NULL;SET @giam=0;SET @coc=0;SET @tienDoi=0;
  SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-'+RIGHT('00'+CAST(CASE WHEN @i=29 THEN 17 WHEN @i=30 THEN 18 ELSE (@i-1)%20+1 END AS VARCHAR),2);
  SET @luc=DATEADD(hour,10+@i%8,CAST(DATEADD(day,CASE WHEN @i IN(17,18) THEN 27 WHEN @i>=29 THEN 28 WHEN @i BETWEEN 25 AND 28 THEN 20+(@i-25)*2 ELSE (@i-1)%26 END,@moc) AS DATETIME2));
  SELECT @ca=id FROM @cacCa WHERE ngay=CAST(@luc AS DATE);
  SET @soLuong=1;
  IF @i<=18 BEGIN
   IF @i<=16 SELECT @ma=ma FROM @sp WHERE stt=@i;
   ELSE SELECT TOP 1 @ma=ma FROM @sp WHERE nhom=N'Phụ kiện' ORDER BY gia,ma;
   SELECT @sanPham=id,@gia=CAST(gia_ban AS BIGINT) FROM san_pham WHERE ma_san_pham=@ma;
   SET @soLuong=CASE WHEN @i>=17 THEN 2 ELSE 1+@i%3 END;
  END ELSE IF @i<=24 BEGIN
   SELECT @goi=id FROM goi_dich_vu_spa WHERE ma_goi='DV-REAL-DEMO-'+CAST(@i-18 AS VARCHAR);
   SELECT @gia=CAST(don_gia AS BIGINT) FROM bang_gia_spa WHERE goi_id=@goi AND loai_thu_cung='DOG' AND can_nang_tu=0;
  END ELSE IF @i<=28 BEGIN
   SELECT @phong=id,@gia=CAST(gia_dem AS BIGINT) FROM phong_hotel WHERE ma_phong='P-REAL-DEMO-'+RIGHT('00'+CAST(@i-24 AS VARCHAR),2);
   SET @coc=@gia/2;
  END ELSE BEGIN
   SELECT @goc=h.id,@dongGoc=c.id,@loGoc=b.lo_hang_id,@tienDoi=c.don_gia FROM hoa_don h JOIN chi_tiet_don_hang c ON c.don_hang_id=h.don_hang_id JOIN phan_bo_lo_hang b ON b.chi_tiet_don_hang_id=c.id WHERE h.so_hoa_don='HD-REAL-DEMO-'+RIGHT('00'+CAST(@i-12 AS VARCHAR),2);
   SELECT TOP 1 @ma=ma FROM @sp WHERE nhom=N'Phụ kiện' AND gia>@tienDoi ORDER BY gia,ma;
   SELECT @sanPham=id,@gia=CAST(gia_ban AS BIGINT) FROM san_pham WHERE ma_san_pham=@ma;
   SET @giam=@tienDoi;
  END;
  IF @i BETWEEN 25 AND 28 SET @giam=@coc;
  SET @tong=@gia*@soLuong-@giam;
  SET @diem=CONVERT(INT,FLOOR(@tong/10000.0));
  IF @i>=29 BEGIN
   DECLARE @daTraGoc BIGINT=(SELECT tong_tien FROM hoa_don WHERE id=@goc);
   SET @diem=CONVERT(INT,FLOOR((@daTraGoc+@tong)/10000.0)-FLOOR(@daTraGoc/10000.0));
  END;
  INSERT don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai,tao_luc) VALUES('DH-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),@ca,@kh,'PAID',@luc);
  SET @don=SCOPE_IDENTITY();
  INSERT chi_tiet_don_hang(don_hang_id,san_pham_id,so_luong,don_gia,giam_gia,ma_dich_vu,ten_hang)
  VALUES(@don,@sanPham,@soLuong,CASE WHEN @i BETWEEN 25 AND 28 THEN @tong ELSE @gia END,0,CASE WHEN @i BETWEEN 19 AND 24 THEN 'DV-REAL-DEMO-'+CAST(@i-18 AS VARCHAR) WHEN @i BETWEEN 25 AND 28 THEN 'HOTEL' END,CASE WHEN @sanPham IS NOT NULL THEN (SELECT ten FROM san_pham WHERE id=@sanPham) WHEN @i<=24 THEN N'Dịch vụ Spa DEMO' ELSE N'Quyết toán Hotel DEMO (đã trừ cọc)' END);
  SET @dong=SCOPE_IDENTITY();
  INSERT hoa_don(so_hoa_don,don_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,thanh_toan_luc,idempotency_key,tam_tinh,giam_gia,ghi_chu)
  VALUES('HD-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),@don,@tong,'CASH',@tong,0,@diem,@luc,'REAL_DATA_V1_INVOICE_'+CAST(@i AS VARCHAR),CASE WHEN @i BETWEEN 25 AND 28 THEN @tong ELSE @gia*@soLuong END,CASE WHEN @i BETWEEN 25 AND 28 THEN 0 ELSE @giam END,N'REAL_DATA_V1 — giao dịch GIẢ LẬP, không có thu tiền thật');
  SET @hd=SCOPE_IDENTITY();
  IF @sanPham IS NOT NULL BEGIN
   SELECT @lo=id FROM lo_hang WHERE san_pham_id=@sanPham AND so_lo LIKE 'REAL-DEMO-%';
   UPDATE lo_hang SET so_luong_ton=so_luong_ton-@soLuong WHERE id=@lo;
   INSERT phan_bo_lo_hang(chi_tiet_don_hang_id,lo_hang_id,so_luong) VALUES(@dong,@lo,@soLuong);
  END;
  UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+@diem WHERE id=@kh;
  INSERT lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do,tao_luc)
  SELECT id,@hd,@diem,diem_tich_luy,N'REAL_DATA_V1 — tích điểm giao dịch giả lập',@luc FROM khach_hang WHERE id=@kh;
  IF @i BETWEEN 19 AND 24 BEGIN
   SELECT TOP 1 @tc=id FROM thu_cung WHERE khach_hang_id=@kh ORDER BY id;
   INSERT lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
   VALUES('LH-REAL-PAID-'+CAST(@i AS VARCHAR),@kh,@tc,'SPA',DATEADD(hour,-2,@luc),@luc,'COMPLETED',@nvSpa,@goi,N'REAL_DATA_V1 DEMO');
   SET @lichHen=SCOPE_IDENTITY();
   INSERT phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,tong_tien,trang_thai,tao_luc,nhan_vien_id,ghi_chu,hoa_don_id)
   VALUES('SPA-REAL-DEMO-'+CAST(@i-18 AS VARCHAR),@lichHen,@tc,3.5,@gia,'PAID',DATEADD(hour,-2,@luc),@nvSpa,N'REAL_DATA_V1 DEMO',@hd);
   SET @phieuSpa=SCOPE_IDENTITY();
   INSERT chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong) SELECT @phieuSpa,id,ten_goi,@gia,1 FROM goi_dich_vu_spa WHERE id=@goi;
  END;
  IF @i BETWEEN 25 AND 28 BEGIN
   SELECT TOP 1 @tc=id FROM thu_cung WHERE khach_hang_id=@kh ORDER BY id;
   INSERT hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,ngay_ra_thuc_te,tien_coc,trang_thai,gia_dem,ghi_chu,hoa_don_id,da_nhan_coc,nhan_coc_luc,nhan_coc_boi,phien_nhan_coc_id)
   VALUES('HT-REAL-PAID-'+CAST(@i AS VARCHAR),@kh,@tc,@phong,DATEADD(day,-1,@luc),@luc,@luc,@coc,'CHECKED_OUT',@gia,N'REAL_DATA_V1 — một đêm, cọc tiền mặt giả lập',@hd,1,DATEADD(day,-1,@luc),@banHang,(SELECT id FROM @cacCa WHERE ngay=CAST(DATEADD(day,-1,@luc) AS DATE)));
   SET @hopDong=SCOPE_IDENTITY();
   INSERT nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,thoi_gian,an_uong,ve_sinh,tinh_trang) VALUES(@hopDong,@nvSpa,DATEADD(hour,-4,@luc),N'Ăn hết khẩu phần DEMO',N'Đã vệ sinh khay và phòng',N'Nhật ký giả lập: sinh hoạt bình thường');
  END;
  IF @i>=29 BEGIN
   INSERT phieu_doi_tra(ma_phieu,hoa_don_id,nguoi_lap_id,chenh_lech,trang_thai,tao_luc,ly_do,loai_phieu,hoa_don_thay_the_id,gia_tri_duoc_doi,khoa_thao_tac)
   VALUES('DOI-REAL-DEMO-'+CAST(@i-28 AS VARCHAR),@goc,@banHang,@tong,'COMPLETED',@luc,N'REAL_DATA_V1 — đổi phụ kiện trong ba ngày, không hoàn tiền','EXCHANGE',@hd,@tienDoi,'REAL_DATA_V1_EXCHANGE_'+CAST(@i AS VARCHAR));
   SET @phieu=SCOPE_IDENTITY();
   INSERT chi_tiet_doi_tra(phieu_doi_tra_id,chi_tiet_don_hang_id,lo_hang_id,so_luong,tien_hoan) VALUES(@phieu,@dongGoc,@loGoc,1,@tienDoi);
   UPDATE lo_hang SET so_luong_ton=so_luong_ton+1 WHERE id=@loGoc;
  END;
  SET @i+=1;
 END;
 -- Voucher có chủ, có lịch sử điểm; thêm điểm mở đầu DEMO rồi mới đổi, không tạo điểm âm.
 SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-01';
 UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+400 WHERE id=@kh;
 INSERT lich_su_diem(khach_hang_id,thay_doi,so_du_sau,ly_do) SELECT id,400,diem_tich_luy,N'REAL_DATA_V1 — điểm mở đầu giả lập' FROM khach_hang WHERE id=@kh;
 UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-200 WHERE id=@kh;
 INSERT lich_su_diem(khach_hang_id,thay_doi,so_du_sau,ly_do) SELECT id,-200,diem_tich_luy,N'REAL_DATA_V1 — đổi voucher Spa 150000' FROM khach_hang WHERE id=@kh;
 INSERT voucher(ma_voucher,khach_hang_id,menh_gia,hieu_luc_den,chi_ap_dung_spa,so_luong,khoa_doi_diem)
 VALUES('VC-REAL-DEMO-150',@kh,150000,DATEADD(day,60,SYSDATETIME()),1,1,'REAL_DATA_V1_REDEEM');
 UPDATE k SET hang_thanh_vien=CHOOSE(1+hang.bac,N'Đồng',N'Bạc',N'Vàng',N'Bạch Kim',N'Kim Cương')
 FROM khach_hang k
 OUTER APPLY(SELECT COALESCE(SUM(h.tong_tien),0) tong FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE d.khach_hang_id=k.id AND h.trang_thai='PAID')ct
 OUTER APPLY(SELECT COUNT(*) bac FROM STRING_SPLIT((SELECT gia_tri FROM cai_dat WHERE khoa='TIER_SPEND'),',') WHERE TRY_CONVERT(BIGINT,value)<=ct.tong)hang
 WHERE k.ma_khach_hang LIKE 'KH-REAL-DEMO-%';
 -- Phiếu Spa trong ngày ở các bước khác nhau, có lịch hẹn và hồ sơ tiêm phòng phù hợp.
 SET @i=1;
 WHILE @i<=4 BEGIN
  SELECT @tc=id,@kh=khach_hang_id FROM thu_cung WHERE ma_thu_cung='TC-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2);
  SELECT @goi=id FROM goi_dich_vu_spa WHERE ma_goi='DV-REAL-DEMO-1';
  SET @luc=DATEADD(hour,8+@i,CAST(@homNay AS DATETIME2));
  INSERT lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
  VALUES('LH-REAL-OPEN-'+CAST(@i AS VARCHAR),@kh,@tc,'SPA',@luc,DATEADD(hour,1,@luc),CASE WHEN @i<=2 THEN 'CHECKED_IN' ELSE 'COMPLETED' END,@nvSpa,@goi,N'REAL_DATA_V1 DEMO');
  SET @lichHen=SCOPE_IDENTITY();
  INSERT phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,tong_tien,trang_thai,tao_luc,nhan_vien_id,ghi_chu)
  VALUES('SPA-REAL-OPEN-'+CAST(@i AS VARCHAR),@lichHen,@tc,3.5,125000,CHOOSE(@i,'RECEIVED','IN_PROGRESS','COMPLETED','COMPLETED'),@luc,@nvSpa,N'REAL_DATA_V1 DEMO — có thể tiếp tục thao tác');
  SET @phieuSpa=SCOPE_IDENTITY();
  INSERT chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong) VALUES(@phieuSpa,@goi,N'Tắm cơ bản — DEMO',125000,1);
  SET @i+=1;
 END;
 -- Hai Hotel đang ở và hai đặt trước, mỗi hợp đồng một phòng riêng, cọc đủ 50%.
 SET @i=1;
 WHILE @i<=4 BEGIN
  SELECT @tc=id,@kh=khach_hang_id FROM thu_cung WHERE ma_thu_cung='TC-REAL-DEMO-'+RIGHT('00'+CAST(@i+10 AS VARCHAR),2);
  SELECT @phong=id,@gia=gia_dem FROM phong_hotel WHERE ma_phong='P-REAL-DEMO-'+RIGHT('00'+CAST(@i+4 AS VARCHAR),2);
  SET @luc=DATEADD(hour,9,CAST(DATEADD(day,CASE WHEN @i<=2 THEN -1 ELSE 1 END,@homNay) AS DATETIME2));
  INSERT hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu,da_nhan_coc,nhan_coc_luc,nhan_coc_boi,phien_nhan_coc_id)
  VALUES('HT-REAL-OPEN-'+CAST(@i AS VARCHAR),@kh,@tc,@phong,@luc,DATEADD(day,3,@luc),@gia*3/2,CASE WHEN @i<=2 THEN 'CHECKED_IN' ELSE 'BOOKED' END,@gia,N'REAL_DATA_V1 DEMO — cọc tiền mặt giả lập',1,DATEADD(hour,8,CAST(DATEADD(day,CASE WHEN @i<=2 THEN -1 ELSE 0 END,@homNay) AS DATETIME2)),@banHang,(SELECT id FROM @cacCa WHERE ngay=DATEADD(day,CASE WHEN @i<=2 THEN -1 ELSE 0 END,@homNay)));
  SET @hopDong=SCOPE_IDENTITY();
  IF @i<=2 BEGIN
   UPDATE phong_hotel SET trang_thai='OCCUPIED' WHERE id=@phong;
   INSERT nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,thoi_gian,an_uong,ve_sinh,tinh_trang) VALUES(@hopDong,@nvSpa,SYSDATETIME(),N'Đã cho ăn theo khẩu phần DEMO',N'Đã vệ sinh',N'Nhật ký chăm sóc giả lập');
  END;
  SET @i+=1;
 END;
 -- Đơn treo thuộc đúng tài khoản bán hàng để mở lại bằng giao diện.
 SET @i=1;
 WHILE @i<=2 BEGIN
  SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2);
  INSERT gio_hang_treo(ma_gio,tai_khoan_id,khach_hang_id) VALUES('TREO-REAL-DEMO-'+CAST(@i AS VARCHAR),@banHang,@kh);
  SET @phieu=SCOPE_IDENTITY();
  INSERT chi_tiet_gio_hang_treo(gio_id,ma_san_pham,ten,don_gia,so_luong) SELECT @phieu,ma,ten,gia,1 FROM @sp WHERE stt=@i;
  SET @i+=1;
 END;
 -- Lịch hẹn sắp tới, không trùng nhân viên hoặc thú cưng.
 SET @i=1;
 WHILE @i<=6 BEGIN
  SELECT @tc=id,@kh=khach_hang_id FROM thu_cung WHERE ma_thu_cung='TC-REAL-DEMO-'+RIGHT('00'+CAST(@i+14 AS VARCHAR),2);
  SELECT @goi=id FROM goi_dich_vu_spa WHERE ma_goi='DV-REAL-DEMO-1';
  SET @luc=DATEADD(hour,9,CAST(DATEADD(day,@i,@homNay) AS DATETIME2));
  INSERT lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
  VALUES('LH-REAL-NEXT-'+CAST(@i AS VARCHAR),@kh,@tc,'SPA',@luc,DATEADD(hour,1,@luc),'SCHEDULED',@nvSpa,@goi,N'REAL_DATA_V1 — lịch hẹn giả lập');
  SET @i+=1;
 END;
 -- Xuất hủy có chứng từ để minh họa cảnh báo tồn thấp, không sửa tồn trực tiếp thiếu lịch sử.
 SELECT @lo=l.id,@nhap=l.so_luong_ton-3,@giaNhap=p.gia_nhap_gan_nhat FROM lo_hang l JOIN san_pham p ON p.id=l.san_pham_id WHERE p.ma_san_pham='SP-VS-010' AND l.so_lo LIKE 'REAL-DEMO-%';
 INSERT phieu_xuat_kho(so_phieu,nguoi_lap_id,loai_xuat,ly_do,tong_gia_tri,trang_thai)
 VALUES('PX-REAL-DEMO-01',@kho,'WRITE_OFF',N'REAL_DATA_V1 — tình huống hàng hỏng bao bì giả lập',@nhap*@giaNhap,'POSTED');
 SET @phieu=SCOPE_IDENTITY();
 INSERT chi_tiet_phieu_xuat(phieu_xuat_id,lo_hang_id,so_luong,gia_tri) VALUES(@phieu,@lo,@nhap,@nhap*@giaNhap);
 UPDATE lo_hang SET so_luong_ton=so_luong_ton-@nhap WHERE id=@lo;
 INSERT bien_ban_kiem_ke(so_bien_ban,nguoi_lap_id,ky_kiem_ke,trang_thai) VALUES('KK-REAL-DEMO-01',@kho,CONVERT(VARCHAR(10),@homNay,23),'PENDING');
 SET @phieu=SCOPE_IDENTITY();
 INSERT chi_tiet_kiem_ke(bien_ban_id,lo_hang_id,ton_he_thong,ton_thuc_te,nguyen_nhan) VALUES(@phieu,@lo,3,2,N'REAL_DATA_V1 — lệch kiểm kê giả lập, chưa áp dụng');
 INSERT yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload)
 VALUES('YC-REAL-KIEM-KE','STOCKTAKE',@kho,'STOCKTAKE',CAST(@phieu AS VARCHAR),N'REAL_DATA_V1 — chờ duyệt chênh lệch',N'Tồn sổ 3',N'Tồn thực 2','');
 SET @i=1;
 WHILE @i<=2 BEGIN
  SELECT @sanPham=p.id,@gia=p.gia_ban FROM san_pham p WHERE p.ma_san_pham='SP-PK-'+RIGHT('000'+CAST(@i AS VARCHAR),3);
  INSERT de_xuat_gia(san_pham_id,nguoi_de_xuat_id,gia_hien_tai,gia_de_xuat,ly_do)
  VALUES(@sanPham,@kho,@gia,@gia+5000,N'REAL_DATA_V1 — đề xuất giá giả lập chưa áp dụng');
  SET @phieu=SCOPE_IDENTITY();
  INSERT yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload)
  VALUES('YC-REAL-GIA-'+CAST(@i AS VARCHAR),'PRICE',@kho,'PRICE_CHANGE',CAST(@phieu AS VARCHAR),N'REAL_DATA_V1 — thử duyệt hoặc từ chối',CAST(@gia AS NVARCHAR),CAST(@gia+5000 AS NVARCHAR),'');
  SET @i+=1;
 END;
 -- Lịch tuần sau cho nhân viên DEMO; yêu cầu nghỉ phép còn chờ quản lý duyệt.
 INSERT lich_lam_viec(nhan_vien_id,ca_lam_viec_id,ngay_lam,loai)
 SELECT n.id,c.id,DATEADD(day,d.so,@homNay),'WORK' FROM nhan_vien n CROSS JOIN ca_lam_viec c CROSS JOIN(VALUES(1),(2),(3),(4),(5),(6),(7))d(so)
 WHERE n.ma_nhan_vien LIKE 'NVDEMO%' AND c.ma_ca='CA_SANG'
 AND NOT EXISTS(SELECT 1 FROM lich_lam_viec l WHERE l.nhan_vien_id=n.id AND l.ca_lam_viec_id=c.id AND l.ngay_lam=DATEADD(day,d.so,@homNay));
 INSERT yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload)
 VALUES('YC-REAL-NGHI-PHEP','LEAVE',(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.spa'),'LEAVE',CAST(@nvSpa AS VARCHAR),N'REAL_DATA_V1 — tình huống xin nghỉ phép giả lập',N'Đang phân công',N'Nghỉ một ngày',CONVERT(VARCHAR(10),DATEADD(day,3,@homNay),23));
 -- Kết ca lịch sử phải khớp tiền mặt hóa đơn và cọc đã nhận.
 UPDATE c SET tien_thuc_te=c.tien_dau_ca+COALESCE(h.tien,0)+COALESCE(ht.coc,0),chenh_lech=0
 FROM phien_lam_viec c JOIN @cacCa ds ON ds.id=c.id
 OUTER APPLY(SELECT SUM(h.tong_tien) tien FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE d.phien_lam_viec_id=c.id AND h.trang_thai='PAID' AND h.hinh_thuc_thanh_toan='CASH')h
 OUTER APPLY(SELECT SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE phien_nhan_coc_id=c.id AND da_nhan_coc=1)ht;
 INSERT nhat_ky_he_thong(tai_khoan_id,hanh_dong,loai_doi_tuong,ma_doi_tuong,ghi_chu)
 VALUES(@quanLy,'DEMO_IMPORT','DATASET','REAL_DATA_V1',N'Nhập 60 sản phẩm có nguồn và bộ giao dịch giả lập liên thông; không có thu/chi thật.');
 INSERT cai_dat(khoa,gia_tri) VALUES('REAL_DATA_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO

-- UI_DATA_CLEANUP_V1: đổi nội dung hiển thị, giữ ID và toàn bộ số liệu nghiệp vụ.
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRAN;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WITH(UPDLOCK,HOLDLOCK) WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT')
 BEGIN
 DECLARE @bang SYSNAME,@cot SYSNAME,@tienTo VARCHAR(12),@lenh NVARCHAR(MAX),@id BIGINT,@maCu NVARCHAR(100),@maMoi NVARCHAR(100),@so INT,@trung INT;
 CREATE TABLE #canDoi(id BIGINT,ma NVARCHAR(100));
 DECLARE bang_cursor CURSOR LOCAL FAST_FORWARD FOR SELECT * FROM (VALUES
 (N'nhan_vien',N'ma_nhan_vien',N'NV'),
 (N'khach_hang',N'ma_khach_hang',N'KH'),
 (N'thu_cung',N'ma_thu_cung',N'TC'),
 (N'san_pham',N'ma_san_pham',N'SP'),
 (N'nha_cung_cap',N'ma_ncc',N'NCC'),
 (N'goi_dich_vu_spa',N'ma_goi',N'DV'),
 (N'phong_hotel',N'ma_phong',N'P'),
 (N'don_hang',N'ma_don',N'DH'),
 (N'hoa_don',N'so_hoa_don',N'HD'),
 (N'phieu_nhap_kho',N'so_phieu',N'PN'),
 (N'phieu_xuat_kho',N'so_phieu',N'PX'),
 (N'bien_ban_kiem_ke',N'so_bien_ban',N'KK'),
 (N'phieu_doi_tra',N'ma_phieu',N'DT'),
 (N'phieu_tiep_nhan_spa',N'ma_phieu',N'SPA'),
 (N'hop_dong_luu_tru',N'ma_hop_dong',N'HT'),
 (N'lich_hen',N'ma_lich_hen',N'LH'),
 (N'gio_hang_treo',N'ma_gio',N'TREO'),
 (N'voucher',N'ma_voucher',N'VC'),
 (N'yeu_cau_phe_duyet',N'ma_yeu_cau',N'YC'),
 (N'lo_hang',N'so_lo',N'LO')
 )v(bang,cot,tien_to);
 OPEN bang_cursor;FETCH NEXT FROM bang_cursor INTO @bang,@cot,@tienTo;
 WHILE @@FETCH_STATUS=0
 BEGIN
  TRUNCATE TABLE #canDoi;
  SET @lenh=N'INSERT #canDoi SELECT id,'+QUOTENAME(@cot)+N' FROM '+QUOTENAME(@bang)+N' WHERE '+QUOTENAME(@cot)+N' LIKE ''%DEMO%'' OR '+QUOTENAME(@cot)+N' LIKE ''%REAL-%''; SELECT @so=COALESCE(MAX(TRY_CONVERT(int,SUBSTRING('+QUOTENAME(@cot)+N',LEN(@tienTo)+1,30))),0) FROM '+QUOTENAME(@bang)+N' WHERE '+QUOTENAME(@cot)+N' LIKE @tienTo+''%'';';
  EXEC sp_executesql @lenh,N'@so int OUTPUT,@tienTo varchar(12)',@so OUTPUT,@tienTo;
  WHILE EXISTS(SELECT 1 FROM #canDoi)
  BEGIN
   SELECT TOP(1) @id=id,@maCu=ma FROM #canDoi ORDER BY id;
   SET @trung=1;
   WHILE @trung>0
   BEGIN
    SET @so=@so+1;SET @maMoi=@tienTo+RIGHT('000000'+CAST(@so AS VARCHAR(10)),6);
    SET @lenh=N'SELECT @trung=COUNT(*) FROM '+QUOTENAME(@bang)+N' WHERE '+QUOTENAME(@cot)+N'=@maMoi';
    EXEC sp_executesql @lenh,N'@trung int OUTPUT,@maMoi nvarchar(100)',@trung OUTPUT,@maMoi;
   END;
   INSERT doi_chieu_ma_du_lieu VALUES(@bang,@cot,@id,@maCu,@maMoi,'UI_DATA_CLEANUP_V1');
   SET @lenh=N'UPDATE '+QUOTENAME(@bang)+N' SET '+QUOTENAME(@cot)+N'=@maMoi WHERE id=@id AND '+QUOTENAME(@cot)+N'=@maCu';
   EXEC sp_executesql @lenh,N'@id bigint,@maCu nvarchar(100),@maMoi nvarchar(100)',@id,@maCu,@maMoi;
   DELETE #canDoi WHERE id=@id;
  END;
  FETCH NEXT FROM bang_cursor INTO @bang,@cot,@tienTo;
 END;
 CLOSE bang_cursor;DEALLOCATE bang_cursor;DROP TABLE #canDoi;
 -- Những tham chiếu lưu bằng chuỗi được đổi cùng transaction với mã chính.
 UPDATE c SET ma_san_pham=m.ma_moi FROM chi_tiet_gio_hang_treo c JOIN doi_chieu_ma_du_lieu m ON m.bang='san_pham' AND c.ma_san_pham=m.ma_cu;
 UPDATE c SET ma_dich_vu=m.ma_moi FROM chi_tiet_don_hang c JOIN doi_chieu_ma_du_lieu m ON m.bang='goi_dich_vu_spa' AND c.ma_dich_vu=m.ma_cu;
 UPDATE h SET ma_voucher=m.ma_moi FROM hoa_don h JOIN doi_chieu_ma_du_lieu m ON m.bang='voucher' AND h.ma_voucher=m.ma_cu;
 UPDATE c SET so_lo=m.ma_moi FROM chi_tiet_phieu_nhap c JOIN doi_chieu_ma_du_lieu m ON m.bang='lo_hang' AND c.so_lo=m.ma_cu JOIN lo_hang l ON l.id=m.id_doi_tuong AND l.san_pham_id=c.san_pham_id;
 UPDATE y SET ma_doi_tuong=m.ma_moi FROM yeu_cau_phe_duyet y JOIN doi_chieu_ma_du_lieu m ON y.ma_doi_tuong=m.ma_cu WHERE NOT EXISTS(SELECT 1 FROM doi_chieu_ma_du_lieu z WHERE z.ma_cu=m.ma_cu AND z.ma_moi<>m.ma_moi);
 -- Chỉ sửa hồ sơ mẫu được nhận diện bằng bảng đối chiếu.
 UPDATE n SET ho_ten=CASE t.ten_dang_nhap WHEN 'demo.admin' THEN N'Nguyễn Minh Quân' WHEN 'demo.sales' THEN N'Trần Ngọc Anh' WHEN 'demo.stock' THEN N'Lê Hoàng Phúc' WHEN 'demo.spa' THEN N'Phạm Thảo Nhi' ELSE n.ho_ten END
 FROM nhan_vien n JOIN tai_khoan t ON t.nhan_vien_id=n.id WHERE t.ten_dang_nhap IN('demo.admin','demo.sales','demo.stock','demo.spa');
 UPDATE k SET ho_ten=CASE m.ma_cu WHEN 'KH-DEMO-199' THEN N'Lê Minh Châu' WHEN 'KH-DEMO-200' THEN N'Nguyễn Gia Hân' WHEN 'KH-DEMO-SALES' THEN N'Võ Anh Thư' ELSE REPLACE(LEFT(k.ho_ten,CHARINDEX(N' (DEMO',k.ho_ten+N' (DEMO')-1),N'DEMO ',N'') END,
 dia_chi=CASE WHEN k.dia_chi LIKE N'%giả lập%' THEN N'Quận Bình Thạnh, TP. Hồ Chí Minh' ELSE k.dia_chi END,
 email=CASE WHEN k.email LIKE 'demo%@example.invalid' THEN 'khachhang'+CAST(k.id AS VARCHAR)+'@example.com' ELSE k.email END
 FROM khach_hang k JOIN doi_chieu_ma_du_lieu m ON m.bang='khach_hang' AND m.id_doi_tuong=k.id;
 UPDATE t SET ten=CASE WHEN m.ma_cu='TC-DEMO-OK' THEN N'Bông' WHEN m.ma_cu='TC-DEMO-THIEU' THEN N'Mun' ELSE LEFT(t.ten,CHARINDEX(N' DEMO',t.ten+N' DEMO')-1) END
 FROM thu_cung t JOIN doi_chieu_ma_du_lieu m ON m.bang='thu_cung' AND m.id_doi_tuong=t.id;
 UPDATE n SET ten=CHOOSE((ROWNUM.stt-1)%6+1,N'Công ty Thực phẩm thú cưng An Phú',N'Nhà phân phối Minh Khang',N'Công ty Phụ kiện thú cưng Hưng Thịnh',N'Nhà phân phối Happy Pet',N'Công ty Chăm sóc thú cưng Đại Việt',N'Nhà phân phối Thiên Phúc'),nguoi_dai_dien=CHOOSE((ROWNUM.stt-1)%6+1,N'Nguyễn Thanh Hà',N'Trần Quốc Huy',N'Lê Bảo Trâm',N'Phạm Minh Tuấn',N'Võ Thu Ngân',N'Đỗ Hoàng Long'),dia_chi=N'TP. Hồ Chí Minh'
 FROM nha_cung_cap n JOIN (SELECT id_doi_tuong,ROW_NUMBER() OVER(ORDER BY id_doi_tuong) stt FROM doi_chieu_ma_du_lieu WHERE bang='nha_cung_cap') ROWNUM ON ROWNUM.id_doi_tuong=n.id;
 UPDATE p SET ten=REPLACE(p.ten,N'DEMO ',N'') FROM san_pham p JOIN doi_chieu_ma_du_lieu m ON m.bang='san_pham' AND m.id_doi_tuong=p.id;
 UPDATE g SET ten_goi=x.ten+CASE WHEN EXISTS(SELECT 1 FROM goi_dich_vu_spa z WHERE z.id<>g.id AND z.ten_goi=x.ten) THEN N' ('+g.ma_goi+N')' ELSE N'' END
 FROM goi_dich_vu_spa g CROSS APPLY(SELECT REPLACE(REPLACE(g.ten_goi,N' — DEMO',N''),N'DEMO ',N'') ten)x WHERE g.ten_goi LIKE N'%DEMO%';
 UPDATE phong_hotel SET loai_phong=REPLACE(loai_phong,N' DEMO',N'') WHERE loai_phong LIKE N'%DEMO%';
 UPDATE phien_lam_viec SET ten_ca=N'Ca ngày',giai_trinh=N'Đã kiểm đếm và đối chiếu tiền mặt' WHERE ten_ca LIKE N'%REAL_DATA_V1%' OR ten_ca LIKE N'%DEMO%';
 UPDATE san_pham SET ten=N'Cát vệ sinh cho mèo siêu vón IRIS OHYAMA KFAG-100 — Dung tích: 10 lít; Đóng gói: 1 túi',quy_cach=N'Dung tích: 10 lít; Đóng gói: 1 túi',mo_ta=N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 10 lít; Đóng gói: 1 túi.',don_vi_tinh=N'Túi' WHERE ma_san_pham=N'SP-VS-001';
 UPDATE san_pham SET ten=N'Xương cho chó gặm sạch răng VEGEBRAND 360 Bone Prevent Tartar — Đóng gói: 1 gói',quy_cach=N'Đóng gói: 1 gói',mo_ta=N'Thức ăn thương hiệu VEGEBRAND, dành cho chó. Quy cách: Đóng gói: 1 gói.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-001';
 UPDATE san_pham SET ten=N'Thức ăn cho chó con cỡ nhỏ ROYAL CANIN Mini Puppy — Trọng lượng: 800g',quy_cach=N'Trọng lượng: 800g',mo_ta=N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 800g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-002';
 UPDATE san_pham SET ten=N'Chai xịt chó mèo đi vệ sinh đúng chỗ JOYCE & DOLLS Trainer Puppy — Dung tích: 150ml',quy_cach=N'Dung tích: 150ml',mo_ta=N'Vệ sinh thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 150ml.',don_vi_tinh=N'Chai' WHERE ma_san_pham=N'SP-VS-002';
 UPDATE san_pham SET ten=N'Pate cho mèo vị cá ngừ nguyên chất CAT SEA FISH Pure Tuna Meat — Trọng lượng: 85g; Đóng gói: 1 lon',quy_cach=N'Trọng lượng: 85g; Đóng gói: 1 lon',mo_ta=N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.',don_vi_tinh=N'Lon' WHERE ma_san_pham=N'SP-TA-003';
 UPDATE san_pham SET ten=N'Thức ăn cho mèo con ROYAL CANIN Kitten — Trọng lượng: 400g',quy_cach=N'Trọng lượng: 400g',mo_ta=N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-004';
 UPDATE san_pham SET ten=N'Pate cho mèo vị nước sốt cá ngừ WHISKAS Tuna Flavour Sauce — Trọng lượng: 85g',quy_cach=N'Trọng lượng: 85g',mo_ta=N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-005';
 UPDATE san_pham SET ten=N'Cát vệ sinh thủy tinh cho mèo TRIXIE Silikatstreu — Dung tích: 5 lít',quy_cach=N'Dung tích: 5 lít',mo_ta=N'Vệ sinh thương hiệu TRIXIE, dành cho mèo. Quy cách: Dung tích: 5 lít.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-VS-003';
 UPDATE san_pham SET ten=N'Thức ăn cho mèo trưởng thành ROYAL CANIN Indoor 27 — Trọng lượng: 400g',quy_cach=N'Trọng lượng: 400g',mo_ta=N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-006';
 UPDATE san_pham SET ten=N'Pate cho mèo vị nước sốt thịt bò WHISKAS Beef Flavour Sauce — Trọng lượng: 85g',quy_cach=N'Trọng lượng: 85g',mo_ta=N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-007';
 UPDATE san_pham SET ten=N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-88 — Đóng gói: 1 miếng lẻ',quy_cach=N'Đóng gói: 1 miếng lẻ',mo_ta=N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-VS-004';
 UPDATE san_pham SET ten=N'Bánh thưởng cho chó vị thịt bò VEGEBRAND Orgo Freshening Biscuit Bacon Beef',quy_cach=N'',mo_ta=N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-008';
 UPDATE san_pham SET ten=N'Pate cho chó vị thịt bò IRIS OHYAMA One Care Beef — Trọng lượng: 100g; Đóng gói: 1 lon',quy_cach=N'Trọng lượng: 100g; Đóng gói: 1 lon',mo_ta=N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.',don_vi_tinh=N'Lon' WHERE ma_san_pham=N'SP-TA-009';
 UPDATE san_pham SET ten=N'Pate cho chó nước sốt vị thịt bò PEDIGREE Pouch Beef — Trọng lượng: 100g',quy_cach=N'Trọng lượng: 100g',mo_ta=N'Thức ăn thương hiệu PEDIGREE, dành cho chó. Quy cách: Trọng lượng: 100g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-010';
 UPDATE san_pham SET ten=N'Thức ăn cho chó Poodle con ROYAL CANIN Poodle Puppy — Trọng lượng: 500g',quy_cach=N'Trọng lượng: 500g',mo_ta=N'Thức ăn thương hiệu ROYAL CANIN, dành cho chó. Quy cách: Trọng lượng: 500g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-011';
 UPDATE san_pham SET ten=N'Pate cho mèo vị nước sốt thịt gà WHISKAS Chicken Flavour Sauce — Trọng lượng: 85g',quy_cach=N'Trọng lượng: 85g',mo_ta=N'Thức ăn thương hiệu WHISKAS, dành cho mèo. Quy cách: Trọng lượng: 85g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-012';
 UPDATE san_pham SET ten=N'Pate cho mèo mẹ và mèo con CATIDEA Mother & Baby Cat — Trọng lượng: 85g',quy_cach=N'Trọng lượng: 85g',mo_ta=N'Thức ăn thương hiệu CATIDEA, dành cho mèo. Quy cách: Trọng lượng: 85g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-013';
 UPDATE san_pham SET ten=N'Pate cho mèo vị cá ngừ thanh cua CAT SEA FISH Tuna Surimi — Trọng lượng: 85g; Đóng gói: 1 lon',quy_cach=N'Trọng lượng: 85g; Đóng gói: 1 lon',mo_ta=N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.',don_vi_tinh=N'Lon' WHERE ma_san_pham=N'SP-TA-014';
 UPDATE san_pham SET ten=N'Pate cho mèo CIAO Tuna & Whitebait vị cá ngừ và cá chạch trắng — Trọng lượng: 60g',quy_cach=N'Trọng lượng: 60g',mo_ta=N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-015';
 UPDATE san_pham SET ten=N'Cát vệ sinh hữu cơ cho mèo CATS BEST Original — Trọng lượng: 2.1kg',quy_cach=N'Trọng lượng: 2.1kg',mo_ta=N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.1kg.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-VS-005';
 UPDATE san_pham SET ten=N'Xương cho chó vị thịt bò VEGEBRAND Orgo Nutrients Beef',quy_cach=N'',mo_ta=N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-016';
 UPDATE san_pham SET ten=N'Thức ăn cho mèo con và mèo mẹ ROYAL CANIN Mother & Babycat — Trọng lượng: 400g',quy_cach=N'Trọng lượng: 400g',mo_ta=N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-017';
 UPDATE san_pham SET ten=N'Pate cho mèo vị cá ngừ cá thu CAT SEA FISH Tuna Mackerel — Trọng lượng: 85g; Đóng gói: 1 lon',quy_cach=N'Trọng lượng: 85g; Đóng gói: 1 lon',mo_ta=N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.',don_vi_tinh=N'Lon' WHERE ma_san_pham=N'SP-TA-018';
 UPDATE san_pham SET ten=N'Thức ăn cho mèo trưởng thành ROYAL CANIN Regular Fit 32 — Trọng lượng: 400g',quy_cach=N'Trọng lượng: 400g',mo_ta=N'Thức ăn thương hiệu ROYAL CANIN, dành cho mèo. Quy cách: Trọng lượng: 400g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-019';
 UPDATE san_pham SET ten=N'Thức ăn cho chó Poodle MKB All Life Stages Formula Nutrition — Trọng lượng: 1kg',quy_cach=N'Trọng lượng: 1kg',mo_ta=N'Thức ăn thương hiệu MKB, dành cho chó. Quy cách: Trọng lượng: 1kg.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-020';
 UPDATE san_pham SET ten=N'Pate cho chó hỗ trợ chức năng gan và sáng mắt IRIS OHYAMA Benefit For Eyes & Liver — Trọng lượng: 100g; Đóng gói: 1 lon',quy_cach=N'Trọng lượng: 100g; Đóng gói: 1 lon',mo_ta=N'Thức ăn thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Trọng lượng: 100g; Đóng gói: 1 lon.',don_vi_tinh=N'Lon' WHERE ma_san_pham=N'SP-TA-021';
 UPDATE san_pham SET ten=N'Pate cho mèo CIAO Tuna & Salmon vị cá ngừ và cá hồi — Trọng lượng: 60g',quy_cach=N'Trọng lượng: 60g',mo_ta=N'Thức ăn thương hiệu CIAO, dành cho mèo. Quy cách: Trọng lượng: 60g.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-022';
 UPDATE san_pham SET ten=N'Đồ chơi cho chó mèo dạng bóng thừng BOBO',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-001';
 UPDATE san_pham SET ten=N'Pate cho mèo vị cá ngừ cá cơm CAT SEA FISH Tuna Anchovy — Trọng lượng: 85g; Đóng gói: 1 lon',quy_cach=N'Trọng lượng: 85g; Đóng gói: 1 lon',mo_ta=N'Thức ăn thương hiệu CAT SEA FISH, dành cho mèo. Quy cách: Trọng lượng: 85g; Đóng gói: 1 lon.',don_vi_tinh=N'Lon' WHERE ma_san_pham=N'SP-TA-023';
 UPDATE san_pham SET ten=N'Xương gặm cho chó Poodle VEGEBRAND 360 For Poodles Bone',quy_cach=N'',mo_ta=N'Thức ăn thương hiệu VEGEBRAND, dành cho chó.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-TA-024';
 UPDATE san_pham SET ten=N'Sữa tắm khô cho chó mèo JOYCE & DOLLS Waterless Cleansing Foam',quy_cach=N'',mo_ta=N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo.',don_vi_tinh=N'Chai' WHERE ma_san_pham=N'SP-GR-001';
 UPDATE san_pham SET ten=N'Khay vệ sinh cho mèo MAKAR Cat Litter Station',quy_cach=N'',mo_ta=N'Vệ sinh thương hiệu MAKAR, dành cho mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-VS-006';
 UPDATE san_pham SET ten=N'Miếng lót cho chó đi vệ sinh IRIS OHYAMA Clean Pet Sheets CPS-42 — Đóng gói: 1 miếng lẻ',quy_cach=N'Đóng gói: 1 miếng lẻ',mo_ta=N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho chó. Quy cách: Đóng gói: 1 miếng lẻ.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-VS-007';
 UPDATE san_pham SET ten=N'Cát vệ sinh cho mèo than hoạt tính IRIS OHYAMA Charcoal SNS-70 — Dung tích: 7 lít; Đóng gói: 1 túi',quy_cach=N'Dung tích: 7 lít; Đóng gói: 1 túi',mo_ta=N'Vệ sinh thương hiệu IRIS OHYAMA, dành cho mèo. Quy cách: Dung tích: 7 lít; Đóng gói: 1 túi.',don_vi_tinh=N'Túi' WHERE ma_san_pham=N'SP-VS-008';
 UPDATE san_pham SET ten=N'Lược chải lông chó mèo đầu lưỡi mềm BOBO Pet Comb',quy_cach=N'',mo_ta=N'Grooming thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-GR-002';
 UPDATE san_pham SET ten=N'Cát vệ sinh hữu cơ cho mèo CATS BEST Smart Peppets — Trọng lượng: 2.5kg',quy_cach=N'Trọng lượng: 2.5kg',mo_ta=N'Vệ sinh thương hiệu CATS BEST, dành cho mèo. Quy cách: Trọng lượng: 2.5kg.',don_vi_tinh=N'Gói' WHERE ma_san_pham=N'SP-VS-009';
 UPDATE san_pham SET ten=N'Vòng cổ chó mèo kèm dây dắt cỡ mini HAND IN HAND',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-002';
 UPDATE san_pham SET ten=N'Bóng đồ chơi cho chó mèo có chuông nhựa BOBO',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-003';
 UPDATE san_pham SET ten=N'Đồ chơi dây thừng cho chó BOBO',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BOBO, dành cho chó.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-004';
 UPDATE san_pham SET ten=N'Vòng cổ chống ve chó BIOLINE Flea and Tick Collar For Dogs',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BIOLINE, dành cho chó.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-005';
 UPDATE san_pham SET ten=N'Khay vệ sinh cho chó MAKAR Dog Toilet Trays Small — Kích thước: S',quy_cach=N'Kích thước: S',mo_ta=N'Vệ sinh thương hiệu MAKAR, dành cho chó. Quy cách: Kích thước: S.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-VS-010';
 UPDATE san_pham SET ten=N'Bàn chải tắm cho chó mèo BOBO Pet Washing Brush',quy_cach=N'',mo_ta=N'Grooming thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-GR-003';
 UPDATE san_pham SET ten=N'Đồ chơi cần câu cho mèo PAW vui nhộn',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu PAW, dành cho mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-006';
 UPDATE san_pham SET ten=N'Sữa tắm cho chó mèo hương hoa hồng JOYCE & DOLLS Rose Dew — Dung tích: 400ml',quy_cach=N'Dung tích: 400ml',mo_ta=N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.',don_vi_tinh=N'Chai' WHERE ma_san_pham=N'SP-GR-004';
 UPDATE san_pham SET ten=N'Đồ chơi cho chó mèo Paw hình con gà',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-007';
 UPDATE san_pham SET ten=N'Bình uống nước cho chó mèo gài chuồng BOBO',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-008';
 UPDATE san_pham SET ten=N'Vòng cổ chống liếm cho chó mèo PAW Pet Elizabethan Collar',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-009';
 UPDATE san_pham SET ten=N'Sữa tắm cho chó diệt khuẩn BBN Bactericidal Shampoo Lemon',quy_cach=N'',mo_ta=N'Grooming thương hiệu BBN, dành cho chó.',don_vi_tinh=N'Chai' WHERE ma_san_pham=N'SP-GR-005';
 UPDATE san_pham SET ten=N'Găng tay tắm cho chó mèo BOBO Pet Grooming Brush Gloves',quy_cach=N'',mo_ta=N'Grooming thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-GR-006';
 UPDATE san_pham SET ten=N'Kềm cắt bấm móng chó mèo PAW Safety Guard Clipper',quy_cach=N'',mo_ta=N'Grooming thương hiệu PAW, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-GR-007';
 UPDATE san_pham SET ten=N'Sữa tắm cho chó lông trắng BBN Whitening Shampoo',quy_cach=N'',mo_ta=N'Grooming thương hiệu BBN, dành cho chó.',don_vi_tinh=N'Chai' WHERE ma_san_pham=N'SP-GR-008';
 UPDATE san_pham SET ten=N'Đồ chơi dây thừng cho chó mèo đan chéo BOBO',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-010';
 UPDATE san_pham SET ten=N'Bát ăn cho chó mèo nhựa mềm SAWYER PET',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-011';
 UPDATE san_pham SET ten=N'Sữa tắm cho chó mèo hương hoa nhài JOYCE & DOLLS Moder Jasmine — Dung tích: 400ml',quy_cach=N'Dung tích: 400ml',mo_ta=N'Grooming thương hiệu JOYCE & DOLLS, dành cho chó và mèo. Quy cách: Dung tích: 400ml.',don_vi_tinh=N'Chai' WHERE ma_san_pham=N'SP-GR-009';
 UPDATE san_pham SET ten=N'Lược chải lông chó mèo nút bấm KUDI Pet Grooming Slicker Brushes',quy_cach=N'',mo_ta=N'Grooming thương hiệu KUDI, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-GR-010';
 UPDATE san_pham SET ten=N'Thảm mát giải nhiệt cho chó mèo PAW Pet Cool Mat',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-012';
 UPDATE san_pham SET ten=N'Vòng cổ chó mèo kèm dây dắt cỡ bé HAND IN HAND',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu HAND IN HAND, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-013';
 UPDATE san_pham SET ten=N'Bát ăn đôi cho chó mèo bằng nhựa SAWYER PET',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu SAWYER PET, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-014';
 UPDATE san_pham SET ten=N'Đồ chơi dây thừng cho chó mèo PAW loại dài',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu PAW, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-015';
 UPDATE san_pham SET ten=N'Bát ăn cho chó mèo bằng nhựa BOBO Plastic Bowl 3038',quy_cach=N'',mo_ta=N'Phụ kiện thương hiệu BOBO, dành cho chó và mèo.',don_vi_tinh=N'Cái' WHERE ma_san_pham=N'SP-PK-016';
 UPDATE c SET ten_hang=p.ten FROM chi_tiet_don_hang c JOIN san_pham p ON p.id=c.san_pham_id WHERE c.ten_hang LIKE N'%DEMO%' OR c.ten_hang LIKE N'%1 sản phẩm theo mẫu%' OR p.ma_san_pham LIKE 'SP-__-%';
 UPDATE c SET ten=p.ten FROM chi_tiet_gio_hang_treo c JOIN san_pham p ON p.ma_san_pham=c.ma_san_pham WHERE c.ten LIKE N'%DEMO%';
 -- Các trường văn bản nghiệp vụ; loại trừ tài khoản, cấu hình, nguồn và nhật ký kiểm toán.
 DECLARE @tb SYSNAME,@col SYSNAME;
 DECLARE text_cursor CURSOR LOCAL FAST_FORWARD FOR
 SELECT t.name,c.name FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id JOIN sys.types ty ON ty.user_type_id=c.user_type_id
 WHERE SCHEMA_NAME(t.schema_id)='dbo' AND ty.name IN('nvarchar','varchar') AND t.name NOT IN('tai_khoan','cai_dat','doi_chieu_ma_du_lieu','nhat_ky_he_thong')
 AND c.name IN('ghi_chu','mo_ta','ly_do','nguyen_nhan','ten_vaccine','tinh_trang_suc_khoe','tinh_cach','tiem_phong','ten_hang','ten_gio','noi_dung','giai_trinh','gia_tri_cu','gia_tri_moi','noi_dung_truoc','noi_dung_sau','an_uong','ve_sinh','tinh_trang');
 OPEN text_cursor;FETCH NEXT FROM text_cursor INTO @tb,@col;
 WHILE @@FETCH_STATUS=0 BEGIN
  SET @lenh=N'UPDATE '+QUOTENAME(@tb)+N' SET '+QUOTENAME(@col)+N'=LTRIM(RTRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE('+QUOTENAME(@col)+N',N''REAL_DATA_V1 — '',N''''),N''REAL_DATA_V1'',N''''),N''REAL-DEMO'',N''''),N''DEMO'',N''''),N''trình diễn'',N''''),N''giả lập'',N''''),N''tình huống'',N''''),N'' — '',N'' ''),N''  '',N'' ''))) WHERE '+QUOTENAME(@col)+N' LIKE N''%DEMO%'' OR '+QUOTENAME(@col)+N' LIKE N''%REAL_DATA_V1%'' OR '+QUOTENAME(@col)+N' LIKE N''%giả lập%'' OR '+QUOTENAME(@col)+N' LIKE N''%trình diễn%'' OR '+QUOTENAME(@col)+N' LIKE N''%tình huống%''';
  EXEC sp_executesql @lenh;
  FETCH NEXT FROM text_cursor INTO @tb,@col;
 END;CLOSE text_cursor;DEALLOCATE text_cursor;
 -- Nội dung ghi chú gọn, đúng mục đích của chứng từ/hồ sơ.
 UPDATE khach_hang SET ghi_chu=N'Ưu tiên liên hệ qua điện thoại.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='khach_hang');
 UPDATE goi_dich_vu_spa SET mo_ta=N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='goi_dich_vu_spa');
 UPDATE phong_hotel SET ghi_chu=N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='phong_hotel');
 UPDATE nha_cung_cap SET ghi_chu=N'Thanh toán theo điều khoản hợp tác.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='nha_cung_cap');
 UPDATE phieu_nhap_kho SET ghi_chu=N'Nhập bổ sung hàng hóa.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='phieu_nhap_kho');
 UPDATE hoa_don SET ghi_chu=N'Đã kiểm tra thông tin thanh toán.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='hoa_don');
 UPDATE thu_cung SET tinh_trang_suc_khoe=N'Theo dõi sức khỏe định kỳ.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='thu_cung') AND tinh_trang_suc_khoe LIKE N'%không thay thế%';
 INSERT cai_dat(khoa,gia_tri) VALUES('UI_DATA_CLEANUP_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO

-- UI_UNICODE_REPAIR_V1: chỉ sửa biến thể hỏng xác định được; không tính lại hạng hoặc số dư.
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRAN;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WITH(UPDLOCK,HOLDLOCK) WHERE khoa='UI_UNICODE_REPAIR_V1_APPLIED_AT')
 BEGIN
 DECLARE @anhXa TABLE(cu NVARCHAR(100),moi NVARCHAR(100));
 INSERT @anhXa VALUES(N'Đ?ng',N'Đồng'),(N'?ồng',N'Đồng'),(N'??ng',N'Đồng'),(N'Ð?ng',N'Đồng'),(N'B?c',N'Bạc'),(N'V?ng',N'Vàng'),(N'B?ch Kim',N'Bạch Kim'),(N'Kim C??ng',N'Kim Cương');
 INSERT @anhXa SELECT CONVERT(NVARCHAR(100),CONVERT(VARCHAR(20),v.ten)),v.ten FROM (VALUES(N'Đồng'),(N'Bạc'),(N'Vàng'),(N'Bạch Kim'),(N'Kim Cương'))v(ten)
 WHERE CONVERT(NVARCHAR(100),CONVERT(VARCHAR(20),v.ten))<>v.ten
 AND NOT EXISTS(SELECT 1 FROM @anhXa a WHERE a.cu=CONVERT(NVARCHAR(100),CONVERT(VARCHAR(20),v.ten)));
 INSERT doi_chieu_unicode(bang,cot,id_doi_tuong,gia_tri_cu,gia_tri_moi,phien_ban)
 SELECT 'khach_hang','hang_thanh_vien',k.id,k.hang_thanh_vien,a.moi,'UI_UNICODE_REPAIR_V1' FROM khach_hang k JOIN @anhXa a ON a.cu=k.hang_thanh_vien
 WHERE k.hang_thanh_vien<>a.moi AND NOT EXISTS(SELECT 1 FROM doi_chieu_unicode z WHERE z.bang='khach_hang' AND z.cot='hang_thanh_vien' AND z.id_doi_tuong=k.id AND z.phien_ban='UI_UNICODE_REPAIR_V1');
 UPDATE k SET hang_thanh_vien=z.gia_tri_moi FROM khach_hang k JOIN doi_chieu_unicode z ON z.id_doi_tuong=k.id AND z.bang='khach_hang' AND z.cot='hang_thanh_vien' AND z.phien_ban='UI_UNICODE_REPAIR_V1' WHERE k.hang_thanh_vien=z.gia_tri_cu;
 INSERT cai_dat(khoa,gia_tri) VALUES('UI_UNICODE_REPAIR_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WITH(UPDLOCK,HOLDLOCK) WHERE khoa='UI_UNICODE_PET_V1_APPLIED_AT')
 BEGIN
 DECLARE @gioi TABLE(cu NVARCHAR(50),moi NVARCHAR(50));
 INSERT @gioi VALUES(N'Đ?c',N'Đực'),(N'?ực',N'Đực'),(N'??c',N'Đực'),(N'C?i',N'Cái');
 INSERT @gioi SELECT CONVERT(NVARCHAR(50),CONVERT(VARCHAR(10),v.ten)),v.ten FROM (VALUES(N'Đực'),(N'Cái'))v(ten) WHERE CONVERT(NVARCHAR(50),CONVERT(VARCHAR(10),v.ten))<>v.ten AND NOT EXISTS(SELECT 1 FROM @gioi a WHERE a.cu=CONVERT(NVARCHAR(50),CONVERT(VARCHAR(10),v.ten)));
 INSERT doi_chieu_unicode(bang,cot,id_doi_tuong,gia_tri_cu,gia_tri_moi,phien_ban) SELECT 'thu_cung','gioi_tinh',t.id,t.gioi_tinh,a.moi,'UI_UNICODE_PET_V1' FROM thu_cung t JOIN @gioi a ON a.cu=t.gioi_tinh WHERE t.gioi_tinh<>a.moi AND NOT EXISTS(SELECT 1 FROM doi_chieu_unicode z WHERE z.bang='thu_cung' AND z.cot='gioi_tinh' AND z.id_doi_tuong=t.id AND z.phien_ban='UI_UNICODE_PET_V1');
 UPDATE t SET gioi_tinh=z.gia_tri_moi FROM thu_cung t JOIN doi_chieu_unicode z ON z.id_doi_tuong=t.id AND z.bang='thu_cung' AND z.cot='gioi_tinh' AND z.phien_ban='UI_UNICODE_PET_V1' WHERE t.gioi_tinh=z.gia_tri_cu;
 INSERT cai_dat(khoa,gia_tri) VALUES('UI_UNICODE_PET_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
