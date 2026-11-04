/*
    Pet Station - 001_schema.sql
    Mục đích: Khởi tạo database PetStationDemo1, cấu trúc toàn bộ 33 bảng theo chuẩn OOP / PTUD:
              - Loại bỏ hoàn toàn trường [id] BIGINT IDENTITY.
              - Khóa chính là các mã nghiệp vụ (ma_..., so_..., ten_dang_nhap, khoa).
              - Liên kết khóa ngoại trực tiếp bằng các mã nghiệp vụ tương ứng.
              - Đầy đủ ràng buộc toàn vẹn (CHECK, UNIQUE, DEFAULT) và INDEX phục vụ tra cứu.
    
    Hướng dẫn chạy:
      - Mở SQL Server Management Studio (SSMS).
      - Mở file 001_schema.sql và nhấn Execute (F5).
      - File tự động tạo database PetStationDemo1 (nếu chưa có), chuyển ngữ cảnh vào database
        và dọn dẹp các ràng buộc / bảng cũ nếu có (an toàn chạy lại nhiều lần).
*/

USE [master];
GO

IF DB_ID(N'PetStationDemo1') IS NULL AND DB_ID(N'PetStationDB') IS NULL
BEGIN
    CREATE DATABASE [PetStationDemo1]
        COLLATE Vietnamese_CI_AS;
END;
GO

IF DB_ID(N'PetStationDemo1') IS NOT NULL
    USE [PetStationDemo1];
ELSE
    USE [PetStationDB];
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET ARITHABORT ON;
SET NUMERIC_ROUNDABORT OFF;
GO

-- 1. Dọn dẹp ràng buộc khóa ngoại và bảng cũ nếu đã tồn tại
DECLARE @dropFkSql NVARCHAR(MAX) = N'';
SELECT @dropFkSql += N'ALTER TABLE ' + QUOTENAME(s.name) + N'.' + QUOTENAME(t.name) 
                   + N' DROP CONSTRAINT ' + QUOTENAME(f.name) + N';' + CHAR(13)
FROM sys.foreign_keys f
JOIN sys.tables t ON f.parent_object_id = t.object_id
JOIN sys.schemas s ON t.schema_id = s.schema_id;

IF LEN(@dropFkSql) > 0
    EXEC sp_executesql @dropFkSql;

DECLARE @dropTableSql NVARCHAR(MAX) = N'';
SELECT @dropTableSql += N'DROP TABLE ' + QUOTENAME(s.name) + N'.' + QUOTENAME(t.name) + N';' + CHAR(13)
FROM sys.tables t
JOIN sys.schemas s ON t.schema_id = s.schema_id
WHERE t.name <> N'sysdiagrams';

IF LEN(@dropTableSql) > 0
    EXEC sp_executesql @dropTableSql;
GO

BEGIN TRY
    BEGIN TRANSACTION;

-- 1. BẢNG CA LÀM VIỆC (PK: ma_ca)
CREATE TABLE dbo.[ca_lam_viec] (
    [ma_ca] VARCHAR(20) NOT NULL,
    [ten_ca] NVARCHAR(60) NOT NULL,
    [gio_bat_dau] TIME(7) NOT NULL,
    [gio_ket_thuc] TIME(7) NOT NULL,
    [so_nhan_su_toi_thieu] INT NOT NULL DEFAULT ((1)),
    [he_so_luong] DECIMAL(5,2) NOT NULL DEFAULT ((1.0)),
    CONSTRAINT [PK_ca_lam_viec] PRIMARY KEY CLUSTERED ([ma_ca]),
    CONSTRAINT [CK_ca_lam_viec_1] CHECK ([so_nhan_su_toi_thieu]>(0))
);

-- 2. BẢNG CHƯƠNG TRÌNH KHUYẾN MÃI (PK: ma_chuong_trinh)
CREATE TABLE dbo.[chuong_trinh_khuyen_mai] (
    [ma_chuong_trinh] VARCHAR(30) NOT NULL,
    [ten] NVARCHAR(180) NOT NULL,
    [loai_giam] VARCHAR(20) NOT NULL,
    [gia_tri] DECIMAL(18,2) NOT NULL,
    [bat_dau] DATETIME2(7) NOT NULL,
    [ket_thuc] DATETIME2(7) NOT NULL,
    [nguon] VARCHAR(30) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [don_toi_thieu] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    CONSTRAINT [PK_chuong_trinh_khuyen_mai] PRIMARY KEY CLUSTERED ([ma_chuong_trinh]),
    CONSTRAINT [CK_chuong_trinh_khuyen_mai_1] CHECK ([don_toi_thieu]>=(0)),
    CONSTRAINT [CK_chuong_trinh_khuyen_mai_2] CHECK ([gia_tri]>(0)),
    CONSTRAINT [CK_km_ngay] CHECK ([ket_thuc]>[bat_dau])
);

-- 3. BẢNG GÓI DỊCH VỤ SPA (PK: ma_goi)
CREATE TABLE dbo.[goi_dich_vu_spa] (
    [ma_goi] VARCHAR(30) NOT NULL,
    [ten_goi] NVARCHAR(180) NOT NULL,
    [mo_ta] NVARCHAR(1000) NULL,
    [thoi_gian_du_kien] INT NOT NULL,
    [chung_loai_ap_dung] NVARCHAR(30) NOT NULL DEFAULT (N'Cả hai'),
    [don_gia_goc] DECIMAL(18,2) NOT NULL DEFAULT ((150000)),
    [bang_gia] NVARCHAR(1000) NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('ACTIVE'),
    CONSTRAINT [PK_goi_dich_vu_spa] PRIMARY KEY CLUSTERED ([ma_goi]),
    CONSTRAINT [UQ_goi_dich_vu_spa_ten] UNIQUE NONCLUSTERED ([ten_goi]),
    CONSTRAINT [CK_goi_dich_vu_spa_1] CHECK ([thoi_gian_du_kien]>(0))
);

-- 4. BẢNG KHÁCH HÀNG (PK: ma_khach_hang)
CREATE TABLE dbo.[khach_hang] (
    [ma_khach_hang] VARCHAR(20) NOT NULL,
    [ho_ten] NVARCHAR(120) NOT NULL,
    [so_dien_thoai] VARCHAR(15) NOT NULL,
    [dia_chi] NVARCHAR(300) NULL,
    [ngay_sinh] DATE NULL,
    [diem_tich_luy] INT NOT NULL DEFAULT ((0)),
    [hang_thanh_vien] NVARCHAR(20) NOT NULL DEFAULT (N'Thường'),
    [dang_hoat_dong] BIT NOT NULL DEFAULT ((1)),
    [row_version] ROWVERSION NOT NULL,
    [email] NVARCHAR(160) NOT NULL DEFAULT (N''),
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [ngay_tao] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    [lich_su_diem] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_khach_hang] PRIMARY KEY CLUSTERED ([ma_khach_hang]),
    CONSTRAINT [UQ_khach_hang_phone] UNIQUE NONCLUSTERED ([so_dien_thoai]),
    CONSTRAINT [CK_khach_hang_1] CHECK ([diem_tich_luy]>=(0))
);

-- 5. BẢNG NHÀ CUNG CẤP (PK: ma_ncc)
CREATE TABLE dbo.[nha_cung_cap] (
    [ma_ncc] VARCHAR(20) NOT NULL,
    [ten] NVARCHAR(160) NOT NULL,
    [so_dien_thoai] VARCHAR(20) NOT NULL,
    [dia_chi] NVARCHAR(300) NULL,
    [nguoi_dai_dien] NVARCHAR(120) NULL,
    [nhom_mat_hang] NVARCHAR(200) NULL,
    [ghi_chu] NVARCHAR(500) NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('COOPERATING'),
    [ma_so_thue] VARCHAR(30) NULL,
    [dieu_khoan] NVARCHAR(100) NOT NULL DEFAULT (N'Trả ngay'),
    CONSTRAINT [PK_nha_cung_cap] PRIMARY KEY CLUSTERED ([ma_ncc])
);

-- 6. BẢNG NHÂN VIÊN (PK: ma_nhan_vien)
CREATE TABLE dbo.[nhan_vien] (
    [ma_nhan_vien] VARCHAR(20) NOT NULL,
    [ho_ten] NVARCHAR(120) NOT NULL,
    [ngay_sinh] DATE NULL,
    [so_dien_thoai] VARCHAR(15) NOT NULL,
    [dia_chi] NVARCHAR(300) NULL,
    [chuc_vu] NVARCHAR(80) NOT NULL,
    [ngay_vao_lam] DATE NOT NULL,
    [anh_dai_dien] NVARCHAR(500) NULL,
    [dang_lam_viec] BIT NOT NULL DEFAULT ((1)),
    [row_version] ROWVERSION NOT NULL,
    [cccd] VARCHAR(12) NULL,
    CONSTRAINT [PK_nhan_vien] PRIMARY KEY CLUSTERED ([ma_nhan_vien]),
    CONSTRAINT [UQ_nhan_vien_phone] UNIQUE NONCLUSTERED ([so_dien_thoai])
);

-- 7. BẢNG LỊCH LÀM VIỆC (PK: ma_lich_lam_viec, FK: ma_nhan_vien, ma_ca)
CREATE TABLE dbo.[lich_lam_viec] (
    [ma_lich_lam_viec] VARCHAR(30) NOT NULL,
    [ma_nhan_vien] VARCHAR(20) NOT NULL,
    [ma_ca] VARCHAR(20) NOT NULL,
    [ngay_lam] DATE NOT NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('OPEN'),
    [loai] VARCHAR(10) NOT NULL DEFAULT ('WORK'),
    CONSTRAINT [PK_lich_lam_viec] PRIMARY KEY CLUSTERED ([ma_lich_lam_viec]),
    CONSTRAINT [UQ_lich_lam_viec] UNIQUE NONCLUSTERED ([ma_nhan_vien], [ngay_lam], [ma_ca]),
    CONSTRAINT [CK_lich_lam_viec_1] CHECK ([loai]='LEAVE' OR [loai]='WORK'),
    CONSTRAINT [CK_lich_trang_thai] CHECK ([trang_thai]='CANCELLED' OR [trang_thai]='LOCKED' OR [trang_thai]='OPEN')
);

-- 8. BẢNG PHÒNG HOTEL (PK: ma_phong)
CREATE TABLE dbo.[phong_hotel] (
    [ma_phong] VARCHAR(20) NOT NULL,
    [ten_phong] NVARCHAR(80) NULL,
    [loai_phong] NVARCHAR(80) NOT NULL,
    [gia_dem] DECIMAL(18,2) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [row_version] ROWVERSION NOT NULL,
    [suc_chua] INT NOT NULL DEFAULT ((1)),
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [bang_gia] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_phong_hotel] PRIMARY KEY CLUSTERED ([ma_phong]),
    CONSTRAINT [CK_phong_hotel_1] CHECK ([gia_dem]>(0)),
    CONSTRAINT [CK_phong_hotel_2] CHECK ([suc_chua]>(0))
);

-- 9. BẢNG SẢN PHẨM (PK: ma_san_pham)
CREATE TABLE dbo.[san_pham] (
    [ma_san_pham] VARCHAR(30) NOT NULL,
    [barcode] VARCHAR(80) NULL,
    [ten] NVARCHAR(180) NOT NULL,
    [nhom_hang] NVARCHAR(100) NOT NULL,
    [don_vi_tinh] NVARCHAR(30) NOT NULL,
    [gia_ban] DECIMAL(18,2) NOT NULL,
    [gia_nhap_gan_nhat] DECIMAL(18,2) NULL,
    [ton_toi_thieu] INT NOT NULL DEFAULT ((0)),
    [theo_doi_hsd] BIT NOT NULL DEFAULT ((0)),
    [anh] NVARCHAR(500) NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('SELLING'),
    [row_version] ROWVERSION NOT NULL,
    [thuong_hieu] NVARCHAR(100) NULL,
    [quy_cach] NVARCHAR(100) NULL,
    [doi_tuong_su_dung] NVARCHAR(100) NULL,
    [mo_ta] NVARCHAR(1000) NULL,
    [nguon_url] NVARCHAR(1000) NULL,
    [ngay_thu_thap] DATE NULL,
    [lich_su_gia] NVARCHAR(MAX) NULL,
    [de_xuat_gia] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_san_pham] PRIMARY KEY CLUSTERED ([ma_san_pham]),
    CONSTRAINT [UQ_san_pham_ten] UNIQUE NONCLUSTERED ([ten]),
    CONSTRAINT [CK_san_pham_1] CHECK ([gia_ban]>(0)),
    CONSTRAINT [CK_san_pham_2] CHECK ([gia_nhap_gan_nhat]>(0)),
    CONSTRAINT [CK_san_pham_3] CHECK ([ton_toi_thieu]>=(0))
);

-- 10. BẢNG LÔ HÀNG (PK: ma_lo, FK: ma_san_pham)
CREATE TABLE dbo.[lo_hang] (
    [ma_lo] VARCHAR(40) NOT NULL,
    [ma_san_pham] VARCHAR(30) NOT NULL,
    [so_lo] VARCHAR(60) NOT NULL,
    [ngay_san_xuat] DATE NULL,
    [ngay_nhap] DATE NULL,
    [han_su_dung] DATE NULL,
    [gia_nhap] DECIMAL(18,2) NULL,
    [so_luong_nhap] INT NULL,
    [so_luong_ton] INT NOT NULL,
    [ngay_kiem_ke] DATE NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('AVAILABLE'),
    [row_version] ROWVERSION NOT NULL,
    CONSTRAINT [PK_lo_hang] PRIMARY KEY CLUSTERED ([ma_lo]),
    CONSTRAINT [UQ_lo_hang] UNIQUE NONCLUSTERED ([ma_san_pham], [so_lo]),
    CONSTRAINT [CK_lo_hang_1] CHECK ([so_luong_ton]>=(0))
);

-- 11. BẢNG TÀI KHOẢN (PK: ten_dang_nhap, FK: ma_nhan_vien)
CREATE TABLE dbo.[tai_khoan] (
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [ma_nhan_vien] VARCHAR(20) NOT NULL,
    [mat_khau_hash] VARCHAR(100) NOT NULL,
    [vai_tro] VARCHAR(40) NOT NULL,
    [quyen] NVARCHAR(1000) NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('HOAT_DONG'),
    [so_lan_dang_nhap_sai] INT NOT NULL DEFAULT ((0)),
    [khoa_den] DATETIME2(7) NULL,
    [bat_buoc_doi_mat_khau] BIT NOT NULL DEFAULT ((1)),
    [dang_nhap_cuoi] DATETIME2(7) NULL,
    CONSTRAINT [PK_tai_khoan] PRIMARY KEY CLUSTERED ([ten_dang_nhap]),
    CONSTRAINT [UQ_tai_khoan_nhan_vien] UNIQUE NONCLUSTERED ([ma_nhan_vien]),
    CONSTRAINT [CK_tai_khoan_trang_thai] CHECK ([trang_thai] IN ('HOAT_DONG', 'TAM_KHOA', 'NGUNG_HOAT_DONG', 'ACTIVE', 'INACTIVE')),
    CONSTRAINT [CK_tai_khoan_vai_tro] CHECK ([vai_tro] IN ('QUAN_LY_CUA_HANG', 'CUA_HANG_TRUONG', 'NHAN_VIEN_KHO', 'NHAN_VIEN_BAN_HANG', 'NHAN_VIEN_SPA_HOTEL', 'STORE_MANAGER', 'WAREHOUSE_STAFF', 'SALES_STAFF', 'SPA_HOTEL_STAFF'))
);

-- 12. BẢNG BIÊN BẢN KIỂM KÊ (PK: so_bien_ban, FK: ten_dang_nhap)
CREATE TABLE dbo.[bien_ban_kiem_ke] (
    [so_bien_ban] VARCHAR(30) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [ky_kiem_ke] VARCHAR(30) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('COUNTING'),
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    CONSTRAINT [PK_bien_ban_kiem_ke] PRIMARY KEY CLUSTERED ([so_bien_ban])
);

-- 13. BẢNG CÀI ĐẶT HỆ THỐNG (PK: khoa, FK: ten_dang_nhap)
CREATE TABLE dbo.[cai_dat] (
    [khoa] VARCHAR(100) NOT NULL,
    [gia_tri] NVARCHAR(1000) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NULL,
    [ngay_cap_nhat] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    CONSTRAINT [PK_cai_dat] PRIMARY KEY CLUSTERED ([khoa])
);

-- 14. BẢNG CHI TIẾT KIỂM KÊ (PK phức hợp: so_bien_ban, ma_lo)
CREATE TABLE dbo.[chi_tiet_kiem_ke] (
    [so_bien_ban] VARCHAR(30) NOT NULL,
    [ma_lo] VARCHAR(40) NOT NULL,
    [ton_he_thong] INT NOT NULL,
    [ton_thuc_te] INT NOT NULL,
    [nguyen_nhan] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_chi_tiet_kiem_ke] PRIMARY KEY CLUSTERED ([so_bien_ban], [ma_lo])
);

-- 15. BẢNG NHẬT KÝ HỆ THỐNG (PK: ma_nhat_ky, FK: ten_dang_nhap)
CREATE TABLE dbo.[nhat_ky_he_thong] (
    [ma_nhat_ky] VARCHAR(30) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NULL,
    [hanh_dong] NVARCHAR(100) NOT NULL,
    [loai_doi_tuong] VARCHAR(60) NULL,
    [ma_doi_tuong] VARCHAR(60) NULL,
    [gia_tri_cu] NVARCHAR(MAX) NULL,
    [gia_tri_moi] NVARCHAR(MAX) NULL,
    [ghi_chu] NVARCHAR(1000) NULL,
    [thoi_gian] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [nguoi_dung] NVARCHAR(120) NULL,
    [loai_su_kien] VARCHAR(30) NULL,
    [ket_qua] NVARCHAR(100) NULL,
    CONSTRAINT [PK_nhat_ky_he_thong] PRIMARY KEY CLUSTERED ([ma_nhat_ky])
);

-- 16. BẢNG PHIÊN LÀM VIỆC (PK: ma_phien, FK: ma_lich_lam_viec, ten_dang_nhap, nguoi_nhan_ban_giao)
CREATE TABLE dbo.[phien_lam_viec] (
    [ma_phien] VARCHAR(30) NOT NULL,
    [ma_lich_lam_viec] VARCHAR(30) NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [mo_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [dong_luc] DATETIME2(7) NULL,
    [tien_dau_ca] DECIMAL(18,2) NOT NULL,
    [tien_thuc_te] DECIMAL(18,2) NULL,
    [chenh_lech] DECIMAL(18,2) NULL,
    [giai_trinh] NVARCHAR(1000) NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('OPEN'),
    [ten_ca] NVARCHAR(80) NOT NULL DEFAULT (N'Ca làm việc'),
    [nguoi_nhan_ban_giao] VARCHAR(80) NULL,
    [hinh_thuc_ban_giao] VARCHAR(20) NULL,
    CONSTRAINT [PK_phien_lam_viec] PRIMARY KEY CLUSTERED ([ma_phien]),
    CONSTRAINT [CK_phien_lam_viec_1] CHECK ([tien_dau_ca]>=(0)),
    CONSTRAINT [CK_phien_hinh_thuc_ban_giao] CHECK ([hinh_thuc_ban_giao]='BAN_GIAO' OR [hinh_thuc_ban_giao]='NOP_QUY'),
    CONSTRAINT [CK_phien_trang_thai] CHECK ([trang_thai]='LOCKED' OR [trang_thai]='HANDED_OVER' OR [trang_thai]='RECONCILED' OR [trang_thai]='PENDING_APPROVAL' OR [trang_thai]='OPEN')
);

-- 17. BẢNG HÓA ĐƠN (PK: so_hoa_don, FK: ma_phien, ten_dang_nhap, ma_khach_hang, ma_voucher)
CREATE TABLE dbo.[hoa_don] (
    [so_hoa_don] VARCHAR(40) NOT NULL,
    [ma_phien] VARCHAR(30) NULL,
    [ten_dang_nhap] VARCHAR(80) NULL,
    [ma_khach_hang] VARCHAR(20) NULL,
    [tong_tien] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [hinh_thuc_thanh_toan] VARCHAR(30) NOT NULL DEFAULT ('TIEN_MAT'),
    [tien_khach_dua] DECIMAL(18,2) NULL,
    [tien_thoi] DECIMAL(18,2) NULL,
    [diem_cong] INT NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('PAID'),
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    [thanh_toan_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [idempotency_key] VARCHAR(80) NULL,
    [tam_tinh] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [giam_gia] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [diem_dung] INT NOT NULL DEFAULT ((0)),
    [ma_voucher] VARCHAR(40) NULL,
    [ghi_chu] NVARCHAR(1000) NULL,
    [chi_tiet_thanh_toan] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_hoa_don] PRIMARY KEY CLUSTERED ([so_hoa_don]),
    CONSTRAINT [CK_hoa_don_1] CHECK ([tong_tien]>=(0))
);

-- 18. BẢNG CHI TIẾT HÓA ĐƠN (PK: ma_chi_tiet_hoa_don, FK: so_hoa_don, ma_san_pham, ma_lo)
CREATE TABLE dbo.[chi_tiet_hoa_don] (
    [ma_chi_tiet_hoa_don] VARCHAR(30) NOT NULL,
    [so_hoa_don] VARCHAR(40) NOT NULL,
    [ma_san_pham] VARCHAR(30) NULL,
    [ma_lo] VARCHAR(40) NULL,
    [so_luong] INT NOT NULL,
    [don_gia] DECIMAL(18,2) NOT NULL,
    [giam_gia] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [thue_gtgt] DECIMAL(5,2) NOT NULL DEFAULT ((0)),
    [ma_dich_vu] VARCHAR(40) NULL,
    [ten_hang] NVARCHAR(180) NULL,
    CONSTRAINT [PK_chi_tiet_hoa_don] PRIMARY KEY CLUSTERED ([ma_chi_tiet_hoa_don]),
    CONSTRAINT [CK_chi_tiet_hoa_don_1] CHECK ([giam_gia]>=(0)),
    CONSTRAINT [CK_chi_tiet_hoa_don_2] CHECK ([so_luong]>(0)),
    CONSTRAINT [CK_chi_tiet_hoa_don_gia] CHECK ([don_gia]>=(0))
);

-- 19. BẢNG PHIẾU ĐỔI TRẢ (PK: ma_phieu, FK: so_hoa_don, ten_dang_nhap, hoa_don_thay_the)
CREATE TABLE dbo.[phieu_doi_tra] (
    [ma_phieu] VARCHAR(30) NOT NULL,
    [so_hoa_don] VARCHAR(40) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [chenh_lech] DECIMAL(18,2) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [ly_do] NVARCHAR(1000) NULL,
    [loai_phieu] VARCHAR(20) NOT NULL DEFAULT ('LEGACY_RETURN'),
    [hoa_don_thay_the] VARCHAR(40) NULL,
    [gia_tri_duoc_doi] BIGINT NULL,
    [khoa_thao_tac] VARCHAR(80) NULL,
    CONSTRAINT [PK_phieu_doi_tra] PRIMARY KEY CLUSTERED ([ma_phieu])
);

-- 20. BẢNG CHI TIẾT ĐỔI TRẢ (PK phức hợp: ma_phieu, ma_chi_tiet_hoa_don, ma_lo)
CREATE TABLE dbo.[chi_tiet_doi_tra] (
    [ma_phieu] VARCHAR(30) NOT NULL,
    [ma_chi_tiet_hoa_don] VARCHAR(30) NOT NULL,
    [ma_lo] VARCHAR(40) NOT NULL,
    [so_luong] INT NOT NULL,
    [tien_hoan] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_chi_tiet_doi_tra] PRIMARY KEY CLUSTERED ([ma_phieu], [ma_chi_tiet_hoa_don], [ma_lo]),
    CONSTRAINT [CK_chi_tiet_doi_tra_1] CHECK ([so_luong]>(0)),
    CONSTRAINT [CK_chi_tiet_doi_tra_2] CHECK ([tien_hoan]>=(0))
);

-- 21. BẢNG PHIẾU NHẬP KHO (PK: so_phieu, FK: ma_ncc, ten_dang_nhap)
CREATE TABLE dbo.[phieu_nhap_kho] (
    [so_phieu] VARCHAR(30) NOT NULL,
    [ma_ncc] VARCHAR(20) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [ngay_nhap] DATE NOT NULL,
    [tong_tien] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('DRAFT'),
    [ghi_chu] NVARCHAR(500) NULL,
    [da_tra] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    CONSTRAINT [PK_phieu_nhap_kho] PRIMARY KEY CLUSTERED ([so_phieu]),
    CONSTRAINT [CK_phieu_nhap_kho_1] CHECK ([da_tra]>=(0)),
    CONSTRAINT [CK_phieu_nhap_kho_2] CHECK ([tong_tien]>=(0))
);

-- 22. BẢNG CHI TIẾT PHIẾU NHẬP (PK phức hợp: so_phieu, ma_san_pham, so_lo)
CREATE TABLE dbo.[chi_tiet_phieu_nhap] (
    [so_phieu] VARCHAR(30) NOT NULL,
    [ma_san_pham] VARCHAR(30) NOT NULL,
    [so_lo] VARCHAR(60) NOT NULL,
    [han_su_dung] DATE NULL,
    [so_luong] INT NOT NULL,
    [gia_nhap] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_chi_tiet_phieu_nhap] PRIMARY KEY CLUSTERED ([so_phieu], [ma_san_pham], [so_lo]),
    CONSTRAINT [CK_chi_tiet_phieu_nhap_1] CHECK ([gia_nhap]>(0)),
    CONSTRAINT [CK_chi_tiet_phieu_nhap_2] CHECK ([so_luong]>(0))
);

-- 23. BẢNG PHIẾU XUẤT KHO (PK: so_phieu, FK: ten_dang_nhap)
CREATE TABLE dbo.[phieu_xuat_kho] (
    [so_phieu] VARCHAR(30) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [loai_xuat] VARCHAR(40) NOT NULL,
    [ly_do] NVARCHAR(1000) NOT NULL,
    [tong_gia_tri] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('DRAFT'),
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    CONSTRAINT [PK_phieu_xuat_kho] PRIMARY KEY CLUSTERED ([so_phieu])
);

-- 24. BẢNG CHI TIẾT PHIẾU XUẤT (PK phức hợp: so_phieu, ma_lo)
CREATE TABLE dbo.[chi_tiet_phieu_xuat] (
    [so_phieu] VARCHAR(30) NOT NULL,
    [ma_lo] VARCHAR(40) NOT NULL,
    [so_luong] INT NOT NULL,
    [gia_tri] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_chi_tiet_phieu_xuat] PRIMARY KEY CLUSTERED ([so_phieu], [ma_lo]),
    CONSTRAINT [CK_chi_tiet_phieu_xuat_1] CHECK ([gia_tri]>=(0)),
    CONSTRAINT [CK_chi_tiet_phieu_xuat_2] CHECK ([so_luong]>(0))
);

-- 25. BẢNG THÚ CƯNG (PK: ma_thu_cung, FK: ma_khach_hang)
CREATE TABLE dbo.[thu_cung] (
    [ma_thu_cung] VARCHAR(20) NOT NULL,
    [ma_khach_hang] VARCHAR(20) NOT NULL,
    [ten] NVARCHAR(80) NOT NULL,
    [loai] NVARCHAR(15) NOT NULL,
    [giong] NVARCHAR(100) NULL,
    [gioi_tinh] NVARCHAR(10) NULL,
    [mau_long] NVARCHAR(80) NULL,
    [can_nang] DECIMAL(8,2) NULL,
    [tinh_trang_suc_khoe] NVARCHAR(1000) NULL,
    [di_ung_benh_ly] NVARCHAR(1000) NULL,
    [anh] NVARCHAR(500) NULL,
    [dang_theo_doi] BIT NOT NULL DEFAULT ((1)),
    [ngay_sinh] DATE NULL,
    [tinh_cach] NVARCHAR(500) NOT NULL DEFAULT (N''),
    [tiem_chung] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [so_mui_tiem] INT NOT NULL DEFAULT ((2)),
    [ngay_tiem_gan_nhat] DATE NULL,
    CONSTRAINT [PK_thu_cung] PRIMARY KEY CLUSTERED ([ma_thu_cung]),
    CONSTRAINT [CK_thu_cung_1] CHECK ([can_nang]>(0)),
    CONSTRAINT [CK_thu_cung_2] CHECK ([loai] IN (N'Chó', N'Mèo', 'DOG', 'CAT'))
);

-- 26. BẢNG HỢP ĐỒNG LƯU TRÚ (PK: ma_hop_dong, FK: ma_khach_hang, ma_thu_cung, ma_phong, so_hoa_don, nhan_coc_boi, ma_phien_nhan_coc)
CREATE TABLE dbo.[hop_dong_luu_tru] (
    [ma_hop_dong] VARCHAR(30) NOT NULL,
    [ma_khach_hang] VARCHAR(20) NOT NULL,
    [ma_thu_cung] VARCHAR(20) NOT NULL,
    [ma_phong] VARCHAR(20) NOT NULL,
    [hinh_thuc_gui] NVARCHAR(30) NOT NULL DEFAULT (N'Qua đêm'),
    [ngay_vao] DATETIME2(7) NOT NULL,
    [ngay_ra_du_kien] DATETIME2(7) NOT NULL,
    [ngay_ra_thuc_te] DATETIME2(7) NULL,
    [tien_coc] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [phuong_thuc_coc] VARCHAR(30) NULL DEFAULT ('TIEN_MAT'),
    [khoa_thu_coc] VARCHAR(120) NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [gia_dem] DECIMAL(18,2) NULL,
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [so_hoa_don] VARCHAR(40) NULL,
    [da_nhan_coc] BIT NOT NULL DEFAULT ((0)),
    [nhan_coc_luc] DATETIME2(7) NULL,
    [nhan_coc_boi] VARCHAR(80) NULL,
    [ma_phien_nhan_coc] VARCHAR(30) NULL,
    [coc_yeu_cau] DECIMAL(18,2) NULL,
    [ly_do_doi_coc] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_hop_dong_luu_tru] PRIMARY KEY CLUSTERED ([ma_hop_dong]),
    CONSTRAINT [CK_hop_dong_luu_tru_1] CHECK ([gia_dem]>(0))
);

-- 27. BẢNG LỊCH HẸN (PK: ma_lich_hen, FK: ma_khach_hang, ma_thu_cung, ma_nhan_vien, ma_goi)
CREATE TABLE dbo.[lich_hen] (
    [ma_lich_hen] VARCHAR(30) NOT NULL,
    [ma_khach_hang] VARCHAR(20) NOT NULL,
    [ma_thu_cung] VARCHAR(20) NOT NULL,
    [loai_dich_vu] VARCHAR(20) NOT NULL,
    [bat_dau] DATETIME2(7) NOT NULL,
    [ket_thuc] DATETIME2(7) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [ghi_chu] NVARCHAR(1000) NULL,
    [ma_nhan_vien] VARCHAR(20) NULL,
    [ma_goi] VARCHAR(30) NULL,
    CONSTRAINT [PK_lich_hen] PRIMARY KEY CLUSTERED ([ma_lich_hen])
);

-- 28. BẢNG NHẬT KÝ CHĂM SÓC (PK: ma_nhat_ky, FK: ma_hop_dong, ma_phieu_spa, ma_nhan_vien)
CREATE TABLE dbo.[nhat_ky_cham_soc] (
    [ma_nhat_ky] VARCHAR(30) NOT NULL,
    [ma_hop_dong] VARCHAR(30) NULL,
    [ma_phieu_spa] VARCHAR(30) NULL,
    [ma_nhan_vien] VARCHAR(20) NOT NULL,
    [thoi_gian] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [an_uong] NVARCHAR(1000) NULL,
    [ve_sinh] NVARCHAR(1000) NULL,
    [tinh_trang] NVARCHAR(1000) NOT NULL,
    [huong_dan_thuoc] NVARCHAR(1000) NULL,
    [da_thuc_hien] NVARCHAR(2000) NULL,
    [anh_truoc] NVARCHAR(1000) NULL,
    [anh_sau] NVARCHAR(1000) NULL,
    [tep_dinh_kem] NVARCHAR(500) NULL,
    CONSTRAINT [PK_nhat_ky_cham_soc] PRIMARY KEY CLUSTERED ([ma_nhat_ky])
);

-- 29. BẢNG PHIẾU TIẾP NHẬN SPA (PK: ma_phieu, FK: ma_lich_hen, ma_thu_cung, ma_goi, ma_nhan_vien, so_hoa_don)
CREATE TABLE dbo.[phieu_tiep_nhan_spa] (
    [ma_phieu] VARCHAR(30) NOT NULL,
    [ma_lich_hen] VARCHAR(30) NULL,
    [ma_thu_cung] VARCHAR(20) NOT NULL,
    [ma_goi] VARCHAR(30) NULL,
    [mo_ta_dich_vu] NVARCHAR(500) NULL,
    [don_gia] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [so_luong] INT NOT NULL DEFAULT ((1)),
    [can_nang_thuc_te] DECIMAL(8,2) NOT NULL,
    [tong_tien] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [ma_nhan_vien] VARCHAR(20) NULL,
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [anh] NVARCHAR(500) NULL,
    [so_hoa_don] VARCHAR(40) NULL,
    CONSTRAINT [PK_phieu_tiep_nhan_spa] PRIMARY KEY CLUSTERED ([ma_phieu]),
    CONSTRAINT [CK_phieu_tiep_nhan_spa_1] CHECK ([can_nang_thuc_te]>(0))
);

-- 30. BẢNG VOUCHER (PK: ma_voucher, FK: ma_khach_hang)
CREATE TABLE dbo.[voucher] (
    [ma_voucher] VARCHAR(40) NOT NULL,
    [ma_khach_hang] VARCHAR(20) NULL,
    [menh_gia] DECIMAL(18,2) NOT NULL,
    [hieu_luc_den] DATETIME2(7) NOT NULL,
    [su_dung_luc] DATETIME2(7) NULL,
    [chi_ap_dung_spa] BIT NOT NULL DEFAULT ((1)),
    [so_luong] INT NOT NULL DEFAULT ((1)),
    [da_dung] INT NOT NULL DEFAULT ((0)),
    [don_toi_thieu] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('ACTIVE'),
    [khoa_doi_diem] VARCHAR(80) NULL,
    CONSTRAINT [PK_voucher] PRIMARY KEY CLUSTERED ([ma_voucher]),
    CONSTRAINT [CK_voucher_1] CHECK ([da_dung]>=(0)),
    CONSTRAINT [CK_voucher_2] CHECK ([menh_gia]>(0)),
    CONSTRAINT [CK_voucher_3] CHECK ([so_luong]>=(0))
);

-- 31. BẢNG YÊU CẦU PHÊ DUYỆT (PK: ma_yeu_cau, FK: ten_dang_nhap_gui, ten_dang_nhap_xu_ly)
CREATE TABLE dbo.[yeu_cau_phe_duyet] (
    [ma_yeu_cau] VARCHAR(30) NOT NULL,
    [loai] VARCHAR(50) NOT NULL,
    [ten_dang_nhap_gui] VARCHAR(80) NOT NULL,
    [loai_doi_tuong] VARCHAR(50) NOT NULL,
    [ma_doi_tuong] VARCHAR(50) NOT NULL,
    [ly_do] NVARCHAR(1000) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('PENDING'),
    [ten_dang_nhap_xu_ly] VARCHAR(80) NULL,
    [phan_hoi] NVARCHAR(1000) NULL,
    [gui_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [xu_ly_luc] DATETIME2(7) NULL,
    [noi_dung_truoc] NVARCHAR(MAX) NULL,
    [noi_dung_sau] NVARCHAR(MAX) NULL,
    [payload] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_yeu_cau_phe_duyet] PRIMARY KEY CLUSTERED ([ma_yeu_cau]),
    CONSTRAINT [CK_yeu_cau_phe_duyet_loai]
        CHECK ([loai] IN ('SUA_HOA_DON', 'DOI_TRA', 'XUAT_HUY', 'KIEM_KE', 'NGHI_PHEP', 'GIA_BAN')),
    CONSTRAINT [CK_yeu_cau_phe_duyet_loai_doi_tuong]
        CHECK ([loai_doi_tuong] IN ('PRICE_CHANGE', 'WRITE_OFF', 'STOCKTAKE', 'RETURN', 'LEAVE', 'INVOICE_NOTE', 'CANCEL_INVOICE')),
    CONSTRAINT [CK_yeu_cau_phe_duyet_trang_thai]
        CHECK ([trang_thai] IN ('PENDING', 'APPROVED', 'REJECTED', 'NEEDS_INFO', 'CANCELLED'))
);

-- 32. BẢNG LỊCH SỬ THU CỌC HOTEL (PK phức hợp: ma_hop_dong, khoa_thu_coc, phuong_thuc)
CREATE TABLE dbo.[lich_su_thu_coc_hotel] (
    [ma_hop_dong] VARCHAR(30) NOT NULL,
    [khoa_thu_coc] VARCHAR(90) NOT NULL,
    [phuong_thuc] VARCHAR(30) NOT NULL,
    [so_tien] DECIMAL(18,2) NOT NULL,
    [khach_dua] DECIMAL(18,2) NOT NULL,
    [tien_thua] DECIMAL(18,2) NOT NULL CONSTRAINT [DF_lich_su_thu_coc_tien_thua] DEFAULT ((0)),
    [ma_phien] VARCHAR(30) NULL,
    [ten_dang_nhap_thu] VARCHAR(80) NULL,
    [tao_luc] DATETIME2(7) NOT NULL CONSTRAINT [DF_lich_su_thu_coc_tao_luc] DEFAULT (sysdatetime()),
    CONSTRAINT [PK_lich_su_thu_coc_hotel] PRIMARY KEY CLUSTERED ([ma_hop_dong], [khoa_thu_coc], [phuong_thuc]),
    CONSTRAINT [CK_lich_su_thu_coc_hotel_so_tien] CHECK ([so_tien]>=(0)),
    CONSTRAINT [CK_lich_su_thu_coc_hotel_khach_dua] CHECK ([khach_dua]>=[so_tien]),
    CONSTRAINT [CK_lich_su_thu_coc_hotel_tien_thua] CHECK ([tien_thua]>=(0))
);

-- 33. BẢNG PHIẾU SPA TREO (PK: ma_phieu, FK: ten_dang_nhap)
CREATE TABLE dbo.[phieu_spa_treo] (
    [ma_phieu] VARCHAR(30) NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [ma_khach] VARCHAR(30) NOT NULL,
    [so_dien_thoai] NVARCHAR(100) NOT NULL,
    [ma_thu] VARCHAR(30) NOT NULL,
    [ma_goi] VARCHAR(30) NOT NULL,
    [ma_hen] VARCHAR(30) NOT NULL,
    [can_nang] NVARCHAR(100) NOT NULL,
    [tinh_trang] NVARCHAR(MAX) NOT NULL,
    [yeu_cau] NVARCHAR(MAX) NOT NULL,
    [cap_nhat_luc] DATETIME2(7) NOT NULL CONSTRAINT [DF_phieu_spa_treo_cap_nhat_luc] DEFAULT (sysdatetime()),
    CONSTRAINT [PK_phieu_spa_treo] PRIMARY KEY CLUSTERED ([ma_phieu])
);

-- ==================== KHÓA NGOẠI (FOREIGN KEYS) ====================
ALTER TABLE dbo.[lich_lam_viec] ADD CONSTRAINT [FK_lich_lam_viec_nhan_vien] FOREIGN KEY ([ma_nhan_vien]) REFERENCES dbo.[nhan_vien] ([ma_nhan_vien]);
ALTER TABLE dbo.[lich_lam_viec] ADD CONSTRAINT [FK_lich_lam_viec_ca_lam_viec] FOREIGN KEY ([ma_ca]) REFERENCES dbo.[ca_lam_viec] ([ma_ca]);

ALTER TABLE dbo.[lo_hang] ADD CONSTRAINT [FK_lo_hang_san_pham] FOREIGN KEY ([ma_san_pham]) REFERENCES dbo.[san_pham] ([ma_san_pham]);

ALTER TABLE dbo.[tai_khoan] ADD CONSTRAINT [FK_tai_khoan_nhan_vien] FOREIGN KEY ([ma_nhan_vien]) REFERENCES dbo.[nhan_vien] ([ma_nhan_vien]);

ALTER TABLE dbo.[bien_ban_kiem_ke] ADD CONSTRAINT [FK_bien_ban_kiem_ke_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[cai_dat] ADD CONSTRAINT [FK_cai_dat_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[chi_tiet_kiem_ke] ADD CONSTRAINT [FK_chi_tiet_kiem_ke_bien_ban] FOREIGN KEY ([so_bien_ban]) REFERENCES dbo.[bien_ban_kiem_ke] ([so_bien_ban]);
ALTER TABLE dbo.[chi_tiet_kiem_ke] ADD CONSTRAINT [FK_chi_tiet_kiem_ke_lo_hang] FOREIGN KEY ([ma_lo]) REFERENCES dbo.[lo_hang] ([ma_lo]);

ALTER TABLE dbo.[nhat_ky_he_thong] ADD CONSTRAINT [FK_nhat_ky_he_thong_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[phien_lam_viec] ADD CONSTRAINT [FK_phien_lam_viec_lich_lam_viec] FOREIGN KEY ([ma_lich_lam_viec]) REFERENCES dbo.[lich_lam_viec] ([ma_lich_lam_viec]);
ALTER TABLE dbo.[phien_lam_viec] ADD CONSTRAINT [FK_phien_lam_viec_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);
ALTER TABLE dbo.[phien_lam_viec] ADD CONSTRAINT [FK_phien_lam_viec_nguoi_nhan_ban_giao] FOREIGN KEY ([nguoi_nhan_ban_giao]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[hoa_don] ADD CONSTRAINT [FK_hoa_don_phien_lam_viec] FOREIGN KEY ([ma_phien]) REFERENCES dbo.[phien_lam_viec] ([ma_phien]);
ALTER TABLE dbo.[hoa_don] ADD CONSTRAINT [FK_hoa_don_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);
ALTER TABLE dbo.[hoa_don] ADD CONSTRAINT [FK_hoa_don_khach_hang] FOREIGN KEY ([ma_khach_hang]) REFERENCES dbo.[khach_hang] ([ma_khach_hang]);
ALTER TABLE dbo.[hoa_don] ADD CONSTRAINT [FK_hoa_don_voucher] FOREIGN KEY ([ma_voucher]) REFERENCES dbo.[voucher] ([ma_voucher]);

ALTER TABLE dbo.[chi_tiet_hoa_don] ADD CONSTRAINT [FK_chi_tiet_hoa_don_hoa_don] FOREIGN KEY ([so_hoa_don]) REFERENCES dbo.[hoa_don] ([so_hoa_don]);
ALTER TABLE dbo.[chi_tiet_hoa_don] ADD CONSTRAINT [FK_chi_tiet_hoa_don_san_pham] FOREIGN KEY ([ma_san_pham]) REFERENCES dbo.[san_pham] ([ma_san_pham]);
ALTER TABLE dbo.[chi_tiet_hoa_don] ADD CONSTRAINT [FK_chi_tiet_hoa_don_lo_hang] FOREIGN KEY ([ma_lo]) REFERENCES dbo.[lo_hang] ([ma_lo]);

ALTER TABLE dbo.[phieu_doi_tra] ADD CONSTRAINT [FK_phieu_doi_tra_hoa_don] FOREIGN KEY ([so_hoa_don]) REFERENCES dbo.[hoa_don] ([so_hoa_don]);
ALTER TABLE dbo.[phieu_doi_tra] ADD CONSTRAINT [FK_phieu_doi_tra_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);
ALTER TABLE dbo.[phieu_doi_tra] ADD CONSTRAINT [FK_phieu_doi_tra_hoa_don_thay_the] FOREIGN KEY ([hoa_don_thay_the]) REFERENCES dbo.[hoa_don] ([so_hoa_don]);

ALTER TABLE dbo.[chi_tiet_doi_tra] ADD CONSTRAINT [FK_chi_tiet_doi_tra_phieu_doi_tra] FOREIGN KEY ([ma_phieu]) REFERENCES dbo.[phieu_doi_tra] ([ma_phieu]);
ALTER TABLE dbo.[chi_tiet_doi_tra] ADD CONSTRAINT [FK_chi_tiet_doi_tra_chi_tiet_hoa_don] FOREIGN KEY ([ma_chi_tiet_hoa_don]) REFERENCES dbo.[chi_tiet_hoa_don] ([ma_chi_tiet_hoa_don]);
ALTER TABLE dbo.[chi_tiet_doi_tra] ADD CONSTRAINT [FK_chi_tiet_doi_tra_lo_hang] FOREIGN KEY ([ma_lo]) REFERENCES dbo.[lo_hang] ([ma_lo]);

ALTER TABLE dbo.[phieu_nhap_kho] ADD CONSTRAINT [FK_phieu_nhap_kho_nha_cung_cap] FOREIGN KEY ([ma_ncc]) REFERENCES dbo.[nha_cung_cap] ([ma_ncc]);
ALTER TABLE dbo.[phieu_nhap_kho] ADD CONSTRAINT [FK_phieu_nhap_kho_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[chi_tiet_phieu_nhap] ADD CONSTRAINT [FK_chi_tiet_phieu_nhap_phieu_nhap] FOREIGN KEY ([so_phieu]) REFERENCES dbo.[phieu_nhap_kho] ([so_phieu]);
ALTER TABLE dbo.[chi_tiet_phieu_nhap] ADD CONSTRAINT [FK_chi_tiet_phieu_nhap_san_pham] FOREIGN KEY ([ma_san_pham]) REFERENCES dbo.[san_pham] ([ma_san_pham]);

ALTER TABLE dbo.[phieu_xuat_kho] ADD CONSTRAINT [FK_phieu_xuat_kho_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[chi_tiet_phieu_xuat] ADD CONSTRAINT [FK_chi_tiet_phieu_xuat_phieu_xuat] FOREIGN KEY ([so_phieu]) REFERENCES dbo.[phieu_xuat_kho] ([so_phieu]);
ALTER TABLE dbo.[chi_tiet_phieu_xuat] ADD CONSTRAINT [FK_chi_tiet_phieu_xuat_lo_hang] FOREIGN KEY ([ma_lo]) REFERENCES dbo.[lo_hang] ([ma_lo]);

ALTER TABLE dbo.[thu_cung] ADD CONSTRAINT [FK_thu_cung_khach_hang] FOREIGN KEY ([ma_khach_hang]) REFERENCES dbo.[khach_hang] ([ma_khach_hang]);

ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_khach_hang] FOREIGN KEY ([ma_khach_hang]) REFERENCES dbo.[khach_hang] ([ma_khach_hang]);
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_thu_cung] FOREIGN KEY ([ma_thu_cung]) REFERENCES dbo.[thu_cung] ([ma_thu_cung]);
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_phong] FOREIGN KEY ([ma_phong]) REFERENCES dbo.[phong_hotel] ([ma_phong]);
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_hoa_don] FOREIGN KEY ([so_hoa_don]) REFERENCES dbo.[hoa_don] ([so_hoa_don]);
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_tai_khoan_coc] FOREIGN KEY ([nhan_coc_boi]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_phien_coc] FOREIGN KEY ([ma_phien_nhan_coc]) REFERENCES dbo.[phien_lam_viec] ([ma_phien]);

ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_khach_hang] FOREIGN KEY ([ma_khach_hang]) REFERENCES dbo.[khach_hang] ([ma_khach_hang]);
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_thu_cung] FOREIGN KEY ([ma_thu_cung]) REFERENCES dbo.[thu_cung] ([ma_thu_cung]);
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_nhan_vien] FOREIGN KEY ([ma_nhan_vien]) REFERENCES dbo.[nhan_vien] ([ma_nhan_vien]);
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_goi_spa] FOREIGN KEY ([ma_goi]) REFERENCES dbo.[goi_dich_vu_spa] ([ma_goi]);

ALTER TABLE dbo.[nhat_ky_cham_soc] ADD CONSTRAINT [FK_nhat_ky_cham_soc_hop_dong] FOREIGN KEY ([ma_hop_dong]) REFERENCES dbo.[hop_dong_luu_tru] ([ma_hop_dong]);
ALTER TABLE dbo.[nhat_ky_cham_soc] ADD CONSTRAINT [FK_nhat_ky_cham_soc_phieu_spa] FOREIGN KEY ([ma_phieu_spa]) REFERENCES dbo.[phieu_tiep_nhan_spa] ([ma_phieu]);
ALTER TABLE dbo.[nhat_ky_cham_soc] ADD CONSTRAINT [FK_nhat_ky_cham_soc_nhan_vien] FOREIGN KEY ([ma_nhan_vien]) REFERENCES dbo.[nhan_vien] ([ma_nhan_vien]);

ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_lich_hen] FOREIGN KEY ([ma_lich_hen]) REFERENCES dbo.[lich_hen] ([ma_lich_hen]);
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_thu_cung] FOREIGN KEY ([ma_thu_cung]) REFERENCES dbo.[thu_cung] ([ma_thu_cung]);
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_goi_spa] FOREIGN KEY ([ma_goi]) REFERENCES dbo.[goi_dich_vu_spa] ([ma_goi]);
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_nhan_vien] FOREIGN KEY ([ma_nhan_vien]) REFERENCES dbo.[nhan_vien] ([ma_nhan_vien]);
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_hoa_don] FOREIGN KEY ([so_hoa_don]) REFERENCES dbo.[hoa_don] ([so_hoa_don]);

ALTER TABLE dbo.[voucher] ADD CONSTRAINT [FK_voucher_khach_hang] FOREIGN KEY ([ma_khach_hang]) REFERENCES dbo.[khach_hang] ([ma_khach_hang]);

ALTER TABLE dbo.[yeu_cau_phe_duyet] ADD CONSTRAINT [FK_yeu_cau_phe_duyet_nguoi_gui] FOREIGN KEY ([ten_dang_nhap_gui]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);
ALTER TABLE dbo.[yeu_cau_phe_duyet] ADD CONSTRAINT [FK_yeu_cau_phe_duyet_nguoi_xu_ly] FOREIGN KEY ([ten_dang_nhap_xu_ly]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[lich_su_thu_coc_hotel] ADD CONSTRAINT [FK_lich_su_thu_coc_hotel_hop_dong] FOREIGN KEY ([ma_hop_dong]) REFERENCES dbo.[hop_dong_luu_tru] ([ma_hop_dong]);
ALTER TABLE dbo.[lich_su_thu_coc_hotel] ADD CONSTRAINT [FK_lich_su_thu_coc_hotel_phien] FOREIGN KEY ([ma_phien]) REFERENCES dbo.[phien_lam_viec] ([ma_phien]);
ALTER TABLE dbo.[lich_su_thu_coc_hotel] ADD CONSTRAINT [FK_lich_su_thu_coc_hotel_tai_khoan_thu] FOREIGN KEY ([ten_dang_nhap_thu]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

ALTER TABLE dbo.[phieu_spa_treo] ADD CONSTRAINT [FK_phieu_spa_treo_tai_khoan] FOREIGN KEY ([ten_dang_nhap]) REFERENCES dbo.[tai_khoan] ([ten_dang_nhap]);

-- ==================== CHỈ MỤC TỐI ƯU HÓA TRA CỨU (INDEXES) ====================
CREATE INDEX [IX_hoa_don_phien_trang_thai] ON dbo.[hoa_don] ([ma_phien], [trang_thai]);
CREATE INDEX [IX_hoa_don_thoi_gian] ON dbo.[hoa_don] ([thanh_toan_luc] DESC);
CREATE UNIQUE INDEX [UX_hoa_don_idempotency] ON dbo.[hoa_don] ([idempotency_key]) WHERE ([idempotency_key] IS NOT NULL AND [idempotency_key]<>'');
CREATE INDEX [IX_hotel_thoi_gian] ON dbo.[hop_dong_luu_tru] ([ma_phong], [ngay_vao], [ngay_ra_du_kien], [trang_thai]);
CREATE INDEX [IX_khach_hang_phone] ON dbo.[khach_hang] ([so_dien_thoai]);
CREATE INDEX [IX_lich_hen_thoi_gian] ON dbo.[lich_hen] ([bat_dau], [ket_thuc], [trang_thai]);
CREATE INDEX [IX_lich_ngay_ca] ON dbo.[lich_lam_viec] ([ngay_lam], [ma_ca]);
CREATE INDEX [IX_lo_hang_fefo] ON dbo.[lo_hang] ([ma_san_pham], [han_su_dung], [trang_thai]) INCLUDE ([so_luong_ton]);
CREATE INDEX [IX_audit_time] ON dbo.[nhat_ky_he_thong] ([thoi_gian] DESC);
CREATE UNIQUE INDEX [UX_phien_open] ON dbo.[phien_lam_viec] ([ten_dang_nhap]) WHERE ([trang_thai]='OPEN');
CREATE UNIQUE INDEX [UX_doi_hang_khoa] ON dbo.[phieu_doi_tra] ([khoa_thao_tac]) WHERE ([khoa_thao_tac] IS NOT NULL);
CREATE UNIQUE INDEX [UX_doi_hang_hoa_don] ON dbo.[phieu_doi_tra] ([hoa_don_thay_the]) WHERE ([hoa_don_thay_the] IS NOT NULL);
CREATE INDEX [IX_spa_pending] ON dbo.[phieu_tiep_nhan_spa] ([trang_thai], [tao_luc]);
CREATE UNIQUE INDEX [UX_san_pham_barcode] ON dbo.[san_pham] ([barcode]) WHERE ([barcode] IS NOT NULL AND [barcode]<>'');
CREATE INDEX [IX_tai_khoan_username_status] ON dbo.[tai_khoan] ([ten_dang_nhap], [trang_thai]);
CREATE UNIQUE INDEX [UX_voucher_doi_diem] ON dbo.[voucher] ([khoa_doi_diem]) WHERE ([khoa_doi_diem] IS NOT NULL);
CREATE INDEX [IX_approval_status] ON dbo.[yeu_cau_phe_duyet] ([trang_thai], [gui_luc]);
CREATE INDEX [IX_lich_su_thu_coc_hotel_hop_dong] ON dbo.[lich_su_thu_coc_hotel] ([ma_hop_dong], [tao_luc] DESC);

COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
