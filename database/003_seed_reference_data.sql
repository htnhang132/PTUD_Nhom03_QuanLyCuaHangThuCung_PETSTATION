SET XACT_ABORT ON;
BEGIN TRANSACTION;

INSERT INTO quyen(ma_quyen, ten_quyen) VALUES
('DASHBOARD_VIEW', N'Xem màn hình chính'),
('ACCOUNT_MANAGE', N'Quản lý tài khoản và nhân viên'),
('SCHEDULE_MANAGE', N'Quản lý lịch làm việc'),
('SCHEDULE_VIEW', N'Xem lịch làm việc'),
('CUSTOMER_MANAGE', N'Quản lý khách hàng'),
('PET_MANAGE', N'Quản lý thú cưng'),
('PRODUCT_MANAGE', N'Quản lý sản phẩm'),
('SUPPLIER_MANAGE', N'Quản lý nhà cung cấp'),
('INVENTORY_RECEIVE', N'Nhập kho'),
('INVENTORY_ISSUE', N'Xuất kho'),
('INVENTORY_COUNT', N'Kiểm kê'),
('PRICE_PROPOSE', N'Đề xuất giá'),
('PRICE_APPROVE', N'Duyệt giá'),
('POS_SELL', N'Lập đơn POS'),
('PAYMENT_PROCESS', N'Thanh toán'),
('CASH_SHIFT_MANAGE', N'Mở/kiểm/bàn giao ca'),
('PROMOTION_MANAGE', N'Quản lý khuyến mãi'),
('SPA_MANAGE', N'Quản lý Spa'),
('HOTEL_MANAGE', N'Quản lý Hotel'),
('APPROVAL_PROCESS', N'Xử lý phê duyệt'),
('AUDIT_VIEW', N'Xem nhật ký'),
('REPORT_VIEW_ALL', N'Xem báo cáo toàn cửa hàng'),
('REPORT_VIEW_OWN_SHIFT', N'Xem báo cáo ca cá nhân');

INSERT INTO vai_tro_quyen(vai_tro, ma_quyen)
SELECT 'STORE_MANAGER', ma_quyen FROM quyen;

INSERT INTO vai_tro_quyen(vai_tro, ma_quyen) VALUES
('WAREHOUSE_STAFF','DASHBOARD_VIEW'),('WAREHOUSE_STAFF','SCHEDULE_VIEW'),
('WAREHOUSE_STAFF','PRODUCT_MANAGE'),('WAREHOUSE_STAFF','SUPPLIER_MANAGE'),
('WAREHOUSE_STAFF','INVENTORY_RECEIVE'),('WAREHOUSE_STAFF','INVENTORY_ISSUE'),
('WAREHOUSE_STAFF','INVENTORY_COUNT'),('WAREHOUSE_STAFF','PRICE_PROPOSE'),
('SALES_STAFF','DASHBOARD_VIEW'),('SALES_STAFF','SCHEDULE_VIEW'),
('SALES_STAFF','CUSTOMER_MANAGE'),('SALES_STAFF','PET_MANAGE'),
('SALES_STAFF','POS_SELL'),('SALES_STAFF','PAYMENT_PROCESS'),
('SALES_STAFF','CASH_SHIFT_MANAGE'),('SALES_STAFF','REPORT_VIEW_OWN_SHIFT'),
('SPA_HOTEL_STAFF','DASHBOARD_VIEW'),('SPA_HOTEL_STAFF','SCHEDULE_VIEW'),
('SPA_HOTEL_STAFF','CUSTOMER_MANAGE'),('SPA_HOTEL_STAFF','PET_MANAGE'),
('SPA_HOTEL_STAFF','SPA_MANAGE'),('SPA_HOTEL_STAFF','HOTEL_MANAGE'),
('SPA_HOTEL_STAFF','POS_SELL'),('SPA_HOTEL_STAFF','PAYMENT_PROCESS'),('SPA_HOTEL_STAFF','CASH_SHIFT_MANAGE');

INSERT INTO ca_lam_viec(ma_ca, ten_ca, gio_bat_dau, gio_ket_thuc, so_nhan_su_toi_thieu) VALUES
('CA_SANG', N'Ca sáng', '07:00', '14:00', 2),
('CA_CHIEU', N'Ca chiều', '14:00', '21:00', 2);

COMMIT;
GO
