import re

def build_schema_sql():
    schema = """/*
    Pet Station - 001_schema.sql
    Mục đích: Khởi tạo database PetStationDemo1, cấu trúc toàn bộ 33 bảng theo chuẩn OOP / PTUD:
              - Loại bỏ hoàn toàn trường [id] BIGINT IDENTITY.
              - Khóa chính là các mã nghiệp vụ (ma_..., so_..., ten_dang_nhap, khoa).
              - Liên kết khóa ngoại trực tiếp bằng các mã tương ứng.
              - Bảo toàn đầy đủ ràng buộc toàn vẹn (CHECK, UNIQUE, DEFAULT) và INDEX.
    
    Hướng dẫn chạy:
      - Mở SQL Server Management Studio (SSMS).
      - Mở file 001_schema.sql và nhấn Execute (F5).
      - File tự động tạo database PetStationDemo1 (nếu chưa có), chuyển ngữ cảnh vào database
        và dọn dẹp các ràng buộc / bảng cũ nếu có (idempotent khi chạy lại nhiều lần).
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
    [ten_goi] NVARCHAR(120) NOT NULL,
    [mo_ta] NVARCHAR(500) NULL,
    [thoi_gian_du_kien] INT NOT NULL DEFAULT ((60)),
    [chung_loai_ap_dung] NVARCHAR(50) NOT NULL DEFAULT (N'Cả hai'),
    [don_gia_goc] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [bang_gia] NVARCHAR(MAX) NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('ACTIVE'),
    CONSTRAINT [PK_goi_dich_vu_spa] PRIMARY KEY CLUSTERED ([ma_goi]),
    CONSTRAINT [CK_goi_dich_vu_spa_1] CHECK ([don_gia_goc]>=(0)),
    CONSTRAINT [CK_goi_dich_vu_spa_2] CHECK ([thoi_gian_du_kien]>(0))
);

-- 4. BẢNG KHÁCH HÀNG (PK: ma_khach_hang)
CREATE TABLE dbo.[khach_hang] (
    [ma_khach_hang] VARCHAR(30) NOT NULL,
    [ho_ten] NVARCHAR(120) NOT NULL,
    [so_dien_thoai] VARCHAR(20) NOT NULL,
    [dia_chi] NVARCHAR(255) NULL,
    [ngay_sinh] DATE NULL,
    [diem_tich_luy] INT NOT NULL DEFAULT ((0)),
    [hang_thanh_vien] NVARCHAR(30) NOT NULL DEFAULT (N'Đồng'),
    [dang_hoat_dong] BIT NOT NULL DEFAULT ((1)),
    [email] VARCHAR(120) NULL,
    [ghi_chu] NVARCHAR(500) NULL,
    [ngay_tao] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [lich_su_diem] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_khach_hang] PRIMARY KEY CLUSTERED ([ma_khach_hang]),
    CONSTRAINT [UQ_khach_hang_phone] UNIQUE NONCLUSTERED ([so_dien_thoai]),
    CONSTRAINT [CK_khach_hang_diem] CHECK ([diem_tich_luy]>=(0))
);

-- 5. BẢNG NHÀ CUNG CẤP (PK: ma_ncc)
CREATE TABLE dbo.[nha_cung_cap] (
    [ma_ncc] VARCHAR(30) NOT NULL,
    [ten] NVARCHAR(150) NOT NULL,
    [so_dien_thoai] VARCHAR(30) NULL,
    [dia_chi] NVARCHAR(255) NULL,
    [nguoi_dai_dien] NVARCHAR(100) NULL,
    [nhom_mat_hang] NVARCHAR(200) NULL,
    [ghi_chu] NVARCHAR(500) NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('COOPERATING'),
    [ma_so_thue] VARCHAR(30) NULL,
    [dieu_khoan] NVARCHAR(255) NULL,
    CONSTRAINT [PK_nha_cung_cap] PRIMARY KEY CLUSTERED ([ma_ncc])
);

-- 6. BẢNG NHÂN VIÊN (PK: ma_nhan_vien)
CREATE TABLE dbo.[nhan_vien] (
    [ma_nhan_vien] VARCHAR(20) NOT NULL,
    [ho_ten] NVARCHAR(100) NOT NULL,
    [ngay_sinh] DATE NULL,
    [so_dien_thoai] VARCHAR(20) NOT NULL,
    [dia_chi] NVARCHAR(255) NULL,
    [chuc_vu] NVARCHAR(60) NOT NULL,
    [ngay_vao_lam] DATE NOT NULL,
    [anh_dai_dien] NVARCHAR(500) NULL,
    [dang_lam_viec] BIT NOT NULL DEFAULT ((1)),
    [cccd] VARCHAR(20) NULL,
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
    [loai] VARCHAR(20) NOT NULL DEFAULT ('WORK'),
    CONSTRAINT [PK_lich_lam_viec] PRIMARY KEY CLUSTERED ([ma_lich_lam_viec]),
    CONSTRAINT [UQ_lich_lam_viec_unique] UNIQUE NONCLUSTERED ([ma_nhan_vien], [ca_lam_viec_id] /* wait */)
);
"""

# Let's verify constraints and syntax before continuing
print("Helper ready")

