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
 INSERT INTO cai_dat(khoa,gia_tri) VALUES(N'VND_PER_REDEEMED_POINT',N'1000');
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
