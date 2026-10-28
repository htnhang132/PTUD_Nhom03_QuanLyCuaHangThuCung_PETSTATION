-- Pet Station: schema đầy đủ, SQL Server 2019 trở lên.
-- Chạy trên database trống; xem SETUP.md nếu cần reset database cũ.
SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET ARITHABORT ON;
SET NUMERIC_ROUNDABORT OFF;
IF EXISTS(SELECT 1 FROM sys.tables WHERE is_ms_shipped=0 AND name<>'sysdiagrams')
    THROW 50000, 'Database is not empty. Use setup-database.ps1 -Reset after backup.', 1;
GO

CREATE TABLE dbo.[bang_gia_hotel] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [phong_id] BIGINT NOT NULL,
    [khung_gio] VARCHAR(20) NOT NULL DEFAULT ('DAILY'),
    [can_nang_tu] DECIMAL(8,2) NOT NULL DEFAULT ((0)),
    [can_nang_den] DECIMAL(8,2) NOT NULL DEFAULT ((999)),
    [don_gia] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_bang_gia_hotel_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_bang_gia_hotel_1] CHECK ([don_gia]>(0))
);
GO

CREATE TABLE dbo.[bang_gia_spa] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [goi_id] BIGINT NOT NULL,
    [loai_thu_cung] VARCHAR(10) NOT NULL,
    [khoang_can] NVARCHAR(60) NOT NULL,
    [can_nang_tu] DECIMAL(8,2) NOT NULL DEFAULT ((0)),
    [can_nang_den] DECIMAL(8,2) NULL,
    [don_gia] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_bang_gia_spa_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_bang_gia_spa_2] UNIQUE NONCLUSTERED ([goi_id], [loai_thu_cung], [khoang_can]),
    CONSTRAINT [CK_bang_gia_spa_1] CHECK ([don_gia]>(0)),
    CONSTRAINT [CK_bang_gia_spa_2] CHECK ([loai_thu_cung]='CAT' OR [loai_thu_cung]='DOG'),
    CONSTRAINT [CK_bang_gia_spa_3] CHECK ([can_nang_den] IS NULL OR [can_nang_den]>[can_nang_tu])
);
GO

CREATE TABLE dbo.[bien_ban_kiem_ke] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [so_bien_ban] VARCHAR(30) NOT NULL,
    [nguoi_lap_id] BIGINT NOT NULL,
    [ky_kiem_ke] VARCHAR(30) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('COUNTING'),
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    CONSTRAINT [PK_bien_ban_kiem_ke_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_bien_ban_kiem_ke_2] UNIQUE NONCLUSTERED ([so_bien_ban])
);
GO

CREATE TABLE dbo.[ca_lam_viec] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_ca] VARCHAR(20) NOT NULL,
    [ten_ca] NVARCHAR(60) NOT NULL,
    [gio_bat_dau] TIME(7) NOT NULL,
    [gio_ket_thuc] TIME(7) NOT NULL,
    [so_nhan_su_toi_thieu] INT NOT NULL DEFAULT ((1)),
    CONSTRAINT [PK_ca_lam_viec_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_ca_lam_viec_2] UNIQUE NONCLUSTERED ([ma_ca]),
    CONSTRAINT [CK_ca_lam_viec_1] CHECK ([so_nhan_su_toi_thieu]>(0))
);
GO

CREATE TABLE dbo.[cai_dat] (
    [khoa] VARCHAR(100) NOT NULL,
    [gia_tri] NVARCHAR(1000) NOT NULL,
    [nguoi_cap_nhat_id] BIGINT NULL,
    [ngay_cap_nhat] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    CONSTRAINT [PK_cai_dat_1] PRIMARY KEY CLUSTERED ([khoa])
);
GO

CREATE TABLE dbo.[chi_tiet_doi_tra] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [phieu_doi_tra_id] BIGINT NOT NULL,
    [chi_tiet_don_hang_id] BIGINT NOT NULL,
    [lo_hang_id] BIGINT NOT NULL,
    [so_luong] INT NOT NULL,
    [tien_hoan] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_chi_tiet_doi_tra_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_chi_tiet_doi_tra_1] CHECK ([so_luong]>(0)),
    CONSTRAINT [CK_chi_tiet_doi_tra_2] CHECK ([tien_hoan]>=(0))
);
GO

CREATE TABLE dbo.[chi_tiet_don_hang] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [don_hang_id] BIGINT NOT NULL,
    [san_pham_id] BIGINT NULL,
    [so_luong] INT NOT NULL,
    [don_gia] DECIMAL(18,2) NOT NULL,
    [giam_gia] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [ma_dich_vu] VARCHAR(40) NULL,
    [ten_hang] NVARCHAR(180) NULL,
    CONSTRAINT [PK_chi_tiet_don_hang_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_chi_tiet_don_hang_1] CHECK ([giam_gia]>=(0)),
    CONSTRAINT [CK_chi_tiet_don_hang_2] CHECK ([so_luong]>(0)),
    CONSTRAINT [CK_chi_tiet_don_gia] CHECK ([don_gia]>=(0))
);
GO

CREATE TABLE dbo.[chi_tiet_gio_hang_treo] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [gio_id] BIGINT NOT NULL,
    [ma_san_pham] VARCHAR(30) NOT NULL,
    [ten] NVARCHAR(180) NOT NULL,
    [don_gia] DECIMAL(18,2) NOT NULL,
    [so_luong] INT NOT NULL,
    CONSTRAINT [PK_chi_tiet_gio_hang_treo_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_chi_tiet_gio_hang_treo_1] CHECK ([don_gia]>(0)),
    CONSTRAINT [CK_chi_tiet_gio_hang_treo_2] CHECK ([so_luong]>(0))
);
GO

CREATE TABLE dbo.[chi_tiet_kiem_ke] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [bien_ban_id] BIGINT NOT NULL,
    [lo_hang_id] BIGINT NOT NULL,
    [ton_he_thong] INT NOT NULL,
    [ton_thuc_te] INT NOT NULL,
    [nguyen_nhan] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_chi_tiet_kiem_ke_1] PRIMARY KEY CLUSTERED ([id])
);
GO

CREATE TABLE dbo.[chi_tiet_phieu_nhap] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [phieu_nhap_id] BIGINT NOT NULL,
    [san_pham_id] BIGINT NOT NULL,
    [so_lo] VARCHAR(60) NOT NULL,
    [han_su_dung] DATE NULL,
    [so_luong] INT NOT NULL,
    [gia_nhap] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_chi_tiet_phieu_nhap_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_chi_tiet_phieu_nhap_1] CHECK ([gia_nhap]>(0)),
    CONSTRAINT [CK_chi_tiet_phieu_nhap_2] CHECK ([so_luong]>(0))
);
GO

CREATE TABLE dbo.[chi_tiet_phieu_spa] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [phieu_spa_id] BIGINT NOT NULL,
    [goi_id] BIGINT NULL,
    [mo_ta] NVARCHAR(180) NOT NULL,
    [don_gia] DECIMAL(18,2) NOT NULL,
    [so_luong] INT NOT NULL DEFAULT ((1)),
    [thanh_tien] AS ([don_gia]*[so_luong]) PERSISTED,
    CONSTRAINT [PK_chi_tiet_phieu_spa_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_chi_tiet_phieu_spa_1] CHECK ([don_gia]>=(0)),
    CONSTRAINT [CK_chi_tiet_phieu_spa_2] CHECK ([so_luong]>(0))
);
GO

CREATE TABLE dbo.[chi_tiet_phieu_xuat] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [phieu_xuat_id] BIGINT NOT NULL,
    [lo_hang_id] BIGINT NOT NULL,
    [so_luong] INT NOT NULL,
    [gia_tri] DECIMAL(18,2) NOT NULL,
    CONSTRAINT [PK_chi_tiet_phieu_xuat_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_chi_tiet_phieu_xuat_1] CHECK ([gia_tri]>=(0)),
    CONSTRAINT [CK_chi_tiet_phieu_xuat_2] CHECK ([so_luong]>(0))
);
GO

CREATE TABLE dbo.[chot_ngay] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ngay_chot] DATE NOT NULL,
    [nguoi_chot_id] BIGINT NOT NULL,
    [tong_doanh_thu] DECIMAL(18,2) NOT NULL,
    [thoi_diem_chot] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('LOCKED'),
    CONSTRAINT [PK_chot_ngay_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_chot_ngay_2] UNIQUE NONCLUSTERED ([ngay_chot])
);
GO

CREATE TABLE dbo.[chuong_trinh_khuyen_mai] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_chuong_trinh] VARCHAR(30) NOT NULL,
    [ten] NVARCHAR(180) NOT NULL,
    [loai_giam] VARCHAR(20) NOT NULL,
    [gia_tri] DECIMAL(18,2) NOT NULL,
    [bat_dau] DATETIME2(7) NOT NULL,
    [ket_thuc] DATETIME2(7) NOT NULL,
    [nguon] VARCHAR(30) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [don_toi_thieu] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    CONSTRAINT [PK_chuong_trinh_khuyen_mai_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_chuong_trinh_khuyen_mai_2] UNIQUE NONCLUSTERED ([ma_chuong_trinh]),
    CONSTRAINT [CK_chuong_trinh_khuyen_mai_1] CHECK ([don_toi_thieu]>=(0)),
    CONSTRAINT [CK_chuong_trinh_khuyen_mai_2] CHECK ([gia_tri]>(0)),
    CONSTRAINT [CK_km_ngay] CHECK ([ket_thuc]>[bat_dau])
);
GO

CREATE TABLE dbo.[de_xuat_gia] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [san_pham_id] BIGINT NOT NULL,
    [nguoi_de_xuat_id] BIGINT NOT NULL,
    [gia_hien_tai] DECIMAL(18,2) NOT NULL,
    [gia_de_xuat] DECIMAL(18,2) NOT NULL,
    [ly_do] NVARCHAR(500) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('PENDING'),
    CONSTRAINT [PK_de_xuat_gia_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_de_xuat_gia_1] CHECK ([gia_de_xuat]>(0))
);
GO

CREATE TABLE dbo.[don_hang] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_don] VARCHAR(30) NOT NULL,
    [phien_lam_viec_id] BIGINT NOT NULL,
    [khach_hang_id] BIGINT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('DRAFT'),
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    CONSTRAINT [PK_don_hang_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_don_hang_2] UNIQUE NONCLUSTERED ([ma_don])
);
GO

CREATE TABLE dbo.[goi_dich_vu_spa] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_goi] VARCHAR(30) NOT NULL,
    [ten_goi] NVARCHAR(180) NOT NULL,
    [mo_ta] NVARCHAR(1000) NULL,
    [thoi_gian_du_kien] INT NOT NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('ACTIVE'),
    CONSTRAINT [PK_goi_dich_vu_spa_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_goi_dich_vu_spa_2] UNIQUE NONCLUSTERED ([ten_goi]),
    CONSTRAINT [UQ_goi_dich_vu_spa_3] UNIQUE NONCLUSTERED ([ma_goi]),
    CONSTRAINT [CK_goi_dich_vu_spa_1] CHECK ([thoi_gian_du_kien]>(0))
);
GO

CREATE TABLE dbo.[gio_hang_treo] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_gio] VARCHAR(40) NOT NULL,
    [tai_khoan_id] BIGINT NOT NULL,
    [khach_hang_id] BIGINT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    CONSTRAINT [PK_gio_hang_treo_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_gio_hang_treo_2] UNIQUE NONCLUSTERED ([ma_gio])
);
GO

CREATE TABLE dbo.[hoa_don] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [so_hoa_don] VARCHAR(30) NOT NULL,
    [don_hang_id] BIGINT NOT NULL,
    [tong_tien] DECIMAL(18,2) NOT NULL,
    [hinh_thuc_thanh_toan] VARCHAR(30) NOT NULL,
    [tien_khach_dua] DECIMAL(18,2) NULL,
    [tien_thoi] DECIMAL(18,2) NULL,
    [diem_cong] INT NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('PAID'),
    [thanh_toan_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [idempotency_key] VARCHAR(80) NOT NULL,
    [tam_tinh] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [giam_gia] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [diem_dung] INT NOT NULL DEFAULT ((0)),
    [ma_voucher] VARCHAR(40) NULL,
    [ghi_chu] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_hoa_don_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_hoa_don_2] UNIQUE NONCLUSTERED ([idempotency_key]),
    CONSTRAINT [UQ_hoa_don_3] UNIQUE NONCLUSTERED ([don_hang_id]),
    CONSTRAINT [UQ_hoa_don_4] UNIQUE NONCLUSTERED ([so_hoa_don]),
    CONSTRAINT [CK_hoa_don_1] CHECK ([tong_tien]>=(0))
);
GO

CREATE TABLE dbo.[hop_dong_luu_tru] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_hop_dong] VARCHAR(30) NOT NULL,
    [khach_hang_id] BIGINT NOT NULL,
    [thu_cung_id] BIGINT NOT NULL,
    [phong_id] BIGINT NOT NULL,
    [ngay_vao] DATETIME2(7) NOT NULL,
    [ngay_ra_du_kien] DATETIME2(7) NOT NULL,
    [ngay_ra_thuc_te] DATETIME2(7) NULL,
    [tien_coc] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL,
    [gia_dem] DECIMAL(18,2) NULL,
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [hoa_don_id] BIGINT NULL,
    [da_nhan_coc] BIT NOT NULL DEFAULT ((0)),
    [nhan_coc_luc] DATETIME2(7) NULL,
    [nhan_coc_boi] BIGINT NULL,
    [phien_nhan_coc_id] BIGINT NULL,
    [coc_yeu_cau] DECIMAL(18,2) NULL,
    [ly_do_doi_coc] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_hop_dong_luu_tru_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_hop_dong_luu_tru_2] UNIQUE NONCLUSTERED ([ma_hop_dong]),
    CONSTRAINT [CK_hop_dong_luu_tru_1] CHECK ([gia_dem]>(0))
);
GO

CREATE TABLE dbo.[khach_hang] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_khach_hang] VARCHAR(20) NOT NULL,
    [ho_ten] NVARCHAR(120) NOT NULL,
    [so_dien_thoai] VARCHAR(15) NOT NULL,
    [dia_chi] NVARCHAR(300) NULL,
    [ngay_sinh] DATE NULL,
    [diem_tich_luy] INT NOT NULL DEFAULT ((0)),
    [hang_thanh_vien] NVARCHAR(20) NOT NULL DEFAULT ('REGULAR'),
    [dang_hoat_dong] BIT NOT NULL DEFAULT ((1)),
    [row_version] ROWVERSION NOT NULL,
    [email] NVARCHAR(160) NOT NULL DEFAULT (N''),
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [ngay_tao] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    CONSTRAINT [PK_khach_hang_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_khach_hang_2] UNIQUE NONCLUSTERED ([so_dien_thoai]),
    CONSTRAINT [UQ_khach_hang_3] UNIQUE NONCLUSTERED ([ma_khach_hang]),
    CONSTRAINT [CK_khach_hang_1] CHECK ([diem_tich_luy]>=(0))
);
GO

CREATE TABLE dbo.[khoan_thu] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [hoa_don_id] BIGINT NULL,
    [hop_dong_id] BIGINT NULL,
    [phien_id] BIGINT NOT NULL,
    [nguoi_nhan_id] BIGINT NOT NULL,
    [phuong_thuc] VARCHAR(20) NOT NULL,
    [so_tien] DECIMAL(18,2) NOT NULL,
    [khach_dua] DECIMAL(18,2) NOT NULL,
    [tien_thua] DECIMAL(18,2) NOT NULL,
    [nhan_luc] DATETIME2(7) NOT NULL,
    [khoa] VARCHAR(120) NOT NULL,
    CONSTRAINT [PK_khoan_thu_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_khoan_thu_2] UNIQUE NONCLUSTERED ([khoa]),
    CONSTRAINT [CK_khoan_thu_1] CHECK ([hoa_don_id] IS NULL AND [hop_dong_id] IS NOT NULL OR [hoa_don_id] IS NOT NULL AND [hop_dong_id] IS NULL),
    CONSTRAINT [CK_khoan_thu_2] CHECK (([khach_dua]-[tien_thua])=[so_tien]),
    CONSTRAINT [CK_khoan_thu_3] CHECK ([phuong_thuc]='CASH' OR [tien_thua]=(0)),
    CONSTRAINT [CK_khoan_thu_4] CHECK ([khach_dua]>=(0)),
    CONSTRAINT [CK_khoan_thu_5] CHECK ([phuong_thuc]='CARD' OR [phuong_thuc]='TRANSFER' OR [phuong_thuc]='CASH'),
    CONSTRAINT [CK_khoan_thu_6] CHECK ([so_tien]>=(0)),
    CONSTRAINT [CK_khoan_thu_7] CHECK ([tien_thua]>=(0))
);
GO

CREATE TABLE dbo.[lich_hen] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_lich_hen] VARCHAR(30) NOT NULL,
    [khach_hang_id] BIGINT NOT NULL,
    [thu_cung_id] BIGINT NOT NULL,
    [loai_dich_vu] VARCHAR(20) NOT NULL,
    [bat_dau] DATETIME2(7) NOT NULL,
    [ket_thuc] DATETIME2(7) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [ghi_chu] NVARCHAR(1000) NULL,
    [nhan_vien_id] BIGINT NULL,
    [dich_vu_id] BIGINT NULL,
    CONSTRAINT [PK_lich_hen_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_lich_hen_2] UNIQUE NONCLUSTERED ([ma_lich_hen])
);
GO

CREATE TABLE dbo.[lich_lam_viec] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [nhan_vien_id] BIGINT NOT NULL,
    [ca_lam_viec_id] BIGINT NOT NULL,
    [ngay_lam] DATE NOT NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('OPEN'),
    [loai] VARCHAR(10) NOT NULL DEFAULT ('WORK'),
    CONSTRAINT [PK_lich_lam_viec_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_lich_lam_viec] UNIQUE NONCLUSTERED ([nhan_vien_id], [ngay_lam], [ca_lam_viec_id]),
    CONSTRAINT [CK_lich_lam_viec_1] CHECK ([loai]='LEAVE' OR [loai]='WORK'),
    CONSTRAINT [CK_lich_trang_thai] CHECK ([trang_thai]='CANCELLED' OR [trang_thai]='LOCKED' OR [trang_thai]='OPEN')
);
GO

CREATE TABLE dbo.[lich_su_diem] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [khach_hang_id] BIGINT NOT NULL,
    [hoa_don_id] BIGINT NULL,
    [thay_doi] INT NOT NULL,
    [so_du_sau] INT NOT NULL,
    [ly_do] NVARCHAR(300) NOT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    CONSTRAINT [PK_lich_su_diem_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_lich_su_diem_1] CHECK ([so_du_sau]>=(0))
);
GO

CREATE TABLE dbo.[lich_su_gia] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [san_pham_id] BIGINT NOT NULL,
    [gia_cu] DECIMAL(18,2) NOT NULL,
    [gia_moi] DECIMAL(18,2) NOT NULL,
    [nguoi_thay_doi_id] BIGINT NULL,
    [thoi_diem_thay_doi] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    CONSTRAINT [PK_lich_su_gia_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_lich_su_gia_1] CHECK ([gia_moi]>(0))
);
GO

CREATE TABLE dbo.[lo_hang] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [san_pham_id] BIGINT NOT NULL,
    [so_lo] VARCHAR(60) NOT NULL,
    [han_su_dung] DATE NULL,
    [so_luong_ton] INT NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('AVAILABLE'),
    [row_version] ROWVERSION NOT NULL,
    CONSTRAINT [PK_lo_hang_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_lo_hang] UNIQUE NONCLUSTERED ([san_pham_id], [so_lo]),
    CONSTRAINT [CK_lo_hang_1] CHECK ([so_luong_ton]>=(0))
);
GO

CREATE TABLE dbo.[nha_cung_cap] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
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
    CONSTRAINT [PK_nha_cung_cap_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_nha_cung_cap_2] UNIQUE NONCLUSTERED ([ma_ncc])
);
GO

CREATE TABLE dbo.[nhac_viec_da_xem] (
    [tai_khoan_id] BIGINT NOT NULL,
    [khoa] VARCHAR(160) NOT NULL,
    [da_xem] BIT NOT NULL DEFAULT ((0)),
    [hoan_den] DATETIME2(7) NULL,
    CONSTRAINT [PK_nhac_viec_da_xem_1] PRIMARY KEY CLUSTERED ([tai_khoan_id], [khoa])
);
GO

CREATE TABLE dbo.[nhan_vien] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
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
    CONSTRAINT [PK_nhan_vien_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_nhan_vien_2] UNIQUE NONCLUSTERED ([so_dien_thoai]),
    CONSTRAINT [UQ_nhan_vien_3] UNIQUE NONCLUSTERED ([ma_nhan_vien])
);
GO

CREATE TABLE dbo.[nhat_ky_cham_soc] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [hop_dong_id] BIGINT NOT NULL,
    [nhan_vien_id] BIGINT NOT NULL,
    [thoi_gian] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [an_uong] NVARCHAR(500) NULL,
    [ve_sinh] NVARCHAR(500) NULL,
    [tinh_trang] NVARCHAR(1000) NOT NULL,
    [tep_dinh_kem] NVARCHAR(500) NULL,
    CONSTRAINT [PK_nhat_ky_cham_soc_1] PRIMARY KEY CLUSTERED ([id])
);
GO

CREATE TABLE dbo.[nhat_ky_he_thong] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [tai_khoan_id] BIGINT NULL,
    [hanh_dong] VARCHAR(60) NOT NULL,
    [loai_doi_tuong] VARCHAR(60) NULL,
    [ma_doi_tuong] VARCHAR(60) NULL,
    [gia_tri_cu] NVARCHAR(MAX) NULL,
    [gia_tri_moi] NVARCHAR(MAX) NULL,
    [ghi_chu] NVARCHAR(1000) NULL,
    [thoi_gian] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [nguoi_dung] NVARCHAR(120) NULL,
    [loai_su_kien] VARCHAR(30) NULL,
    [ket_qua] NVARCHAR(100) NULL,
    CONSTRAINT [PK_nhat_ky_he_thong_1] PRIMARY KEY CLUSTERED ([id])
);
GO

CREATE TABLE dbo.[phan_bo_lo_hang] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [chi_tiet_don_hang_id] BIGINT NOT NULL,
    [lo_hang_id] BIGINT NOT NULL,
    [so_luong] INT NOT NULL,
    CONSTRAINT [PK_phan_bo_lo_hang_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_phan_bo_lo_hang_1] CHECK ([so_luong]>(0))
);
GO

CREATE TABLE dbo.[phien_lam_viec] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [lich_lam_viec_id] BIGINT NULL,
    [tai_khoan_id] BIGINT NOT NULL,
    [mo_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [dong_luc] DATETIME2(7) NULL,
    [tien_dau_ca] DECIMAL(18,2) NOT NULL,
    [tien_thuc_te] DECIMAL(18,2) NULL,
    [chenh_lech] DECIMAL(18,2) NULL,
    [giai_trinh] NVARCHAR(1000) NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('OPEN'),
    [ten_ca] NVARCHAR(80) NOT NULL DEFAULT (N'Ca làm việc'),
    [nguoi_nhan_ban_giao_id] BIGINT NULL,
    [hinh_thuc_ban_giao] VARCHAR(20) NULL,
    CONSTRAINT [PK_phien_lam_viec_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_phien_lam_viec_1] CHECK ([tien_dau_ca]>=(0)),
    CONSTRAINT [CK_phien_hinh_thuc_ban_giao] CHECK ([hinh_thuc_ban_giao]='BAN_GIAO' OR [hinh_thuc_ban_giao]='NOP_QUY'),
    CONSTRAINT [CK_phien_trang_thai] CHECK ([trang_thai]='LOCKED' OR [trang_thai]='HANDED_OVER' OR [trang_thai]='RECONCILED' OR [trang_thai]='PENDING_APPROVAL' OR [trang_thai]='OPEN')
);
GO

CREATE TABLE dbo.[phieu_cham_soc] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [spa_id] BIGINT NULL,
    [hotel_id] BIGINT NULL,
    [nhan_vien_id] BIGINT NOT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysdatetime()),
    [tinh_trang] NVARCHAR(1000) NOT NULL,
    [an_uong] NVARCHAR(1000) NOT NULL,
    [huong_dan_thuoc] NVARCHAR(1000) NOT NULL,
    [da_thuc_hien] NVARCHAR(2000) NOT NULL,
    [anh_truoc] NVARCHAR(1000) NULL,
    [anh_sau] NVARCHAR(1000) NULL,
    CONSTRAINT [PK_phieu_cham_soc_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [CK_phieu_cham_soc_1] CHECK ([spa_id] IS NULL AND [hotel_id] IS NOT NULL OR [spa_id] IS NOT NULL AND [hotel_id] IS NULL)
);
GO

CREATE TABLE dbo.[phieu_doi_tra] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_phieu] VARCHAR(30) NOT NULL,
    [hoa_don_id] BIGINT NOT NULL,
    [nguoi_lap_id] BIGINT NOT NULL,
    [chenh_lech] DECIMAL(18,2) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [ly_do] NVARCHAR(1000) NULL,
    [loai_phieu] VARCHAR(20) NOT NULL DEFAULT ('LEGACY_RETURN'),
    [hoa_don_thay_the_id] BIGINT NULL,
    [gia_tri_duoc_doi] BIGINT NULL,
    [khoa_thao_tac] VARCHAR(80) NULL,
    CONSTRAINT [PK_phieu_doi_tra_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_phieu_doi_tra_2] UNIQUE NONCLUSTERED ([ma_phieu])
);
GO

CREATE TABLE dbo.[phieu_nhap_kho] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [so_phieu] VARCHAR(30) NOT NULL,
    [nha_cung_cap_id] BIGINT NOT NULL,
    [nguoi_lap_id] BIGINT NOT NULL,
    [ngay_nhap] DATE NOT NULL,
    [tong_tien] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('DRAFT'),
    [ghi_chu] NVARCHAR(500) NULL,
    [da_tra] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    CONSTRAINT [PK_phieu_nhap_kho_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_phieu_nhap_kho_2] UNIQUE NONCLUSTERED ([so_phieu]),
    CONSTRAINT [CK_phieu_nhap_kho_1] CHECK ([da_tra]>=(0)),
    CONSTRAINT [CK_phieu_nhap_kho_2] CHECK ([tong_tien]>=(0))
);
GO

CREATE TABLE dbo.[phieu_tiep_nhan_spa] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_phieu] VARCHAR(30) NOT NULL,
    [lich_hen_id] BIGINT NULL,
    [thu_cung_id] BIGINT NOT NULL,
    [can_nang_thuc_te] DECIMAL(8,2) NOT NULL,
    [tong_tien] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL,
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [nhan_vien_id] BIGINT NULL,
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    [anh] NVARCHAR(500) NULL,
    [hoa_don_id] BIGINT NULL,
    CONSTRAINT [PK_phieu_tiep_nhan_spa_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_phieu_tiep_nhan_spa_2] UNIQUE NONCLUSTERED ([ma_phieu]),
    CONSTRAINT [CK_phieu_tiep_nhan_spa_1] CHECK ([can_nang_thuc_te]>(0))
);
GO

CREATE TABLE dbo.[phieu_xuat_kho] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [so_phieu] VARCHAR(30) NOT NULL,
    [nguoi_lap_id] BIGINT NOT NULL,
    [loai_xuat] VARCHAR(40) NOT NULL,
    [ly_do] NVARCHAR(1000) NOT NULL,
    [tong_gia_tri] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('DRAFT'),
    [tao_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    CONSTRAINT [PK_phieu_xuat_kho_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_phieu_xuat_kho_2] UNIQUE NONCLUSTERED ([so_phieu])
);
GO

CREATE TABLE dbo.[phong_hotel] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_phong] VARCHAR(20) NOT NULL,
    [loai_phong] NVARCHAR(80) NOT NULL,
    [gia_dem] DECIMAL(18,2) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL,
    [row_version] ROWVERSION NOT NULL,
    [suc_chua] INT NOT NULL DEFAULT ((1)),
    [ghi_chu] NVARCHAR(1000) NOT NULL DEFAULT (N''),
    CONSTRAINT [PK_phong_hotel_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_phong_hotel_2] UNIQUE NONCLUSTERED ([ma_phong]),
    CONSTRAINT [CK_phong_hotel_1] CHECK ([gia_dem]>(0)),
    CONSTRAINT [CK_phong_hotel_2] CHECK ([suc_chua]>(0))
);
GO

CREATE TABLE dbo.[quyen] (
    [ma_quyen] VARCHAR(60) NOT NULL,
    [ten_quyen] NVARCHAR(160) NOT NULL,
    CONSTRAINT [PK_quyen_1] PRIMARY KEY CLUSTERED ([ma_quyen])
);
GO

CREATE TABLE dbo.[san_pham] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
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
    CONSTRAINT [PK_san_pham_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_san_pham_2] UNIQUE NONCLUSTERED ([ten]),
    CONSTRAINT [UQ_san_pham_4] UNIQUE NONCLUSTERED ([ma_san_pham]),
    CONSTRAINT [CK_san_pham_1] CHECK ([gia_ban]>(0)),
    CONSTRAINT [CK_san_pham_2] CHECK ([gia_nhap_gan_nhat]>(0)),
    CONSTRAINT [CK_san_pham_3] CHECK ([ton_toi_thieu]>=(0))
);
GO

CREATE TABLE dbo.[tai_khoan] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [nhan_vien_id] BIGINT NOT NULL,
    [ten_dang_nhap] VARCHAR(80) NOT NULL,
    [mat_khau_hash] VARCHAR(100) NOT NULL,
    [vai_tro] VARCHAR(40) NOT NULL,
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('ACTIVE'),
    [so_lan_dang_nhap_sai] INT NOT NULL DEFAULT ((0)),
    [khoa_den] DATETIME2(7) NULL,
    [bat_buoc_doi_mat_khau] BIT NOT NULL DEFAULT ((1)),
    [dang_nhap_cuoi] DATETIME2(7) NULL,
    CONSTRAINT [PK_tai_khoan_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_tai_khoan_2] UNIQUE NONCLUSTERED ([ten_dang_nhap]),
    CONSTRAINT [UQ_tai_khoan_3] UNIQUE NONCLUSTERED ([nhan_vien_id]),
    CONSTRAINT [CK_tai_khoan_trang_thai] CHECK ([trang_thai]='INACTIVE' OR [trang_thai]='ACTIVE'),
    CONSTRAINT [CK_tai_khoan_vai_tro] CHECK ([vai_tro]='SPA_HOTEL_STAFF' OR [vai_tro]='SALES_STAFF' OR [vai_tro]='WAREHOUSE_STAFF' OR [vai_tro]='STORE_MANAGER')
);
GO

CREATE TABLE dbo.[tiem_phong] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [thu_cung_id] BIGINT NOT NULL,
    [ten_vaccine] NVARCHAR(120) NOT NULL,
    [ngay_tiem] DATE NOT NULL,
    [ngay_het_han] DATE NULL,
    [ghi_chu] NVARCHAR(500) NULL,
    CONSTRAINT [PK_tiem_phong_1] PRIMARY KEY CLUSTERED ([id])
);
GO

CREATE TABLE dbo.[thu_cung] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_thu_cung] VARCHAR(20) NOT NULL,
    [khach_hang_id] BIGINT NOT NULL,
    [ten] NVARCHAR(80) NOT NULL,
    [loai] VARCHAR(10) NOT NULL,
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
    CONSTRAINT [PK_thu_cung_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_thu_cung_2] UNIQUE NONCLUSTERED ([ma_thu_cung]),
    CONSTRAINT [CK_thu_cung_1] CHECK ([can_nang]>(0)),
    CONSTRAINT [CK_thu_cung_2] CHECK ([loai]='CAT' OR [loai]='DOG')
);
GO

CREATE TABLE dbo.[vai_tro_quyen] (
    [vai_tro] VARCHAR(40) NOT NULL,
    [ma_quyen] VARCHAR(60) NOT NULL,
    CONSTRAINT [PK_vai_tro_quyen] PRIMARY KEY CLUSTERED ([vai_tro], [ma_quyen]),
    CONSTRAINT [CK_vai_tro_quyen_vai_tro] CHECK ([vai_tro]='SPA_HOTEL_STAFF' OR [vai_tro]='SALES_STAFF' OR [vai_tro]='WAREHOUSE_STAFF' OR [vai_tro]='STORE_MANAGER')
);
GO

CREATE TABLE dbo.[voucher] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_voucher] VARCHAR(40) NOT NULL,
    [khach_hang_id] BIGINT NULL,
    [menh_gia] DECIMAL(18,2) NOT NULL,
    [hieu_luc_den] DATETIME2(7) NOT NULL,
    [su_dung_luc] DATETIME2(7) NULL,
    [chi_ap_dung_spa] BIT NOT NULL DEFAULT ((1)),
    [so_luong] INT NOT NULL DEFAULT ((1)),
    [da_dung] INT NOT NULL DEFAULT ((0)),
    [don_toi_thieu] DECIMAL(18,2) NOT NULL DEFAULT ((0)),
    [trang_thai] VARCHAR(20) NOT NULL DEFAULT ('ACTIVE'),
    [khoa_doi_diem] VARCHAR(80) NULL,
    CONSTRAINT [PK_voucher_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_voucher_2] UNIQUE NONCLUSTERED ([ma_voucher]),
    CONSTRAINT [CK_voucher_1] CHECK ([da_dung]>=(0)),
    CONSTRAINT [CK_voucher_2] CHECK ([menh_gia]>(0)),
    CONSTRAINT [CK_voucher_3] CHECK ([so_luong]>=(0))
);
GO

CREATE TABLE dbo.[yeu_cau_phe_duyet] (
    [id] BIGINT IDENTITY(1,1) NOT NULL,
    [ma_yeu_cau] VARCHAR(30) NOT NULL,
    [loai] VARCHAR(50) NOT NULL,
    [nguoi_gui_id] BIGINT NOT NULL,
    [loai_doi_tuong] VARCHAR(50) NOT NULL,
    [ma_doi_tuong] VARCHAR(50) NOT NULL,
    [ly_do] NVARCHAR(1000) NOT NULL,
    [trang_thai] VARCHAR(30) NOT NULL DEFAULT ('PENDING'),
    [nguoi_xu_ly_id] BIGINT NULL,
    [phan_hoi] NVARCHAR(1000) NULL,
    [gui_luc] DATETIME2(7) NOT NULL DEFAULT (sysutcdatetime()),
    [xu_ly_luc] DATETIME2(7) NULL,
    [noi_dung_truoc] NVARCHAR(MAX) NULL,
    [noi_dung_sau] NVARCHAR(MAX) NULL,
    [payload] NVARCHAR(MAX) NULL,
    CONSTRAINT [PK_yeu_cau_phe_duyet_1] PRIMARY KEY CLUSTERED ([id]),
    CONSTRAINT [UQ_yeu_cau_phe_duyet_2] UNIQUE NONCLUSTERED ([ma_yeu_cau])
);
GO

-- Khóa ngoại: tạo sau khi đã có đủ bảng.
ALTER TABLE dbo.[bang_gia_hotel] ADD CONSTRAINT [FK_bang_gia_hotel_phong_id] FOREIGN KEY ([phong_id]) REFERENCES dbo.[phong_hotel] ([id]);
GO
ALTER TABLE dbo.[bang_gia_spa] ADD CONSTRAINT [FK_bang_gia_spa_goi_id] FOREIGN KEY ([goi_id]) REFERENCES dbo.[goi_dich_vu_spa] ([id]);
GO
ALTER TABLE dbo.[bien_ban_kiem_ke] ADD CONSTRAINT [FK_bien_ban_kiem_ke_nguoi_lap_id] FOREIGN KEY ([nguoi_lap_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[cai_dat] ADD CONSTRAINT [FK_cai_dat_nguoi_cap_nhat_id] FOREIGN KEY ([nguoi_cap_nhat_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_doi_tra] ADD CONSTRAINT [FK_chi_tiet_doi_tra_chi_tiet_don_hang_id] FOREIGN KEY ([chi_tiet_don_hang_id]) REFERENCES dbo.[chi_tiet_don_hang] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_doi_tra] ADD CONSTRAINT [FK_chi_tiet_doi_tra_lo_hang_id] FOREIGN KEY ([lo_hang_id]) REFERENCES dbo.[lo_hang] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_doi_tra] ADD CONSTRAINT [FK_chi_tiet_doi_tra_phieu_doi_tra_id] FOREIGN KEY ([phieu_doi_tra_id]) REFERENCES dbo.[phieu_doi_tra] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_don_hang] ADD CONSTRAINT [FK_chi_tiet_don_hang_don_hang_id] FOREIGN KEY ([don_hang_id]) REFERENCES dbo.[don_hang] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_don_hang] ADD CONSTRAINT [FK_chi_tiet_don_hang_san_pham_id] FOREIGN KEY ([san_pham_id]) REFERENCES dbo.[san_pham] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_gio_hang_treo] ADD CONSTRAINT [FK_chi_tiet_gio_hang_treo_gio_id] FOREIGN KEY ([gio_id]) REFERENCES dbo.[gio_hang_treo] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_kiem_ke] ADD CONSTRAINT [FK_chi_tiet_kiem_ke_bien_ban_id] FOREIGN KEY ([bien_ban_id]) REFERENCES dbo.[bien_ban_kiem_ke] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_kiem_ke] ADD CONSTRAINT [FK_chi_tiet_kiem_ke_lo_hang_id] FOREIGN KEY ([lo_hang_id]) REFERENCES dbo.[lo_hang] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_phieu_nhap] ADD CONSTRAINT [FK_chi_tiet_phieu_nhap_phieu_nhap_id] FOREIGN KEY ([phieu_nhap_id]) REFERENCES dbo.[phieu_nhap_kho] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_phieu_nhap] ADD CONSTRAINT [FK_chi_tiet_phieu_nhap_san_pham_id] FOREIGN KEY ([san_pham_id]) REFERENCES dbo.[san_pham] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_phieu_spa] ADD CONSTRAINT [FK_chi_tiet_phieu_spa_goi_id] FOREIGN KEY ([goi_id]) REFERENCES dbo.[goi_dich_vu_spa] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_phieu_spa] ADD CONSTRAINT [FK_chi_tiet_phieu_spa_phieu_spa_id] FOREIGN KEY ([phieu_spa_id]) REFERENCES dbo.[phieu_tiep_nhan_spa] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_phieu_xuat] ADD CONSTRAINT [FK_chi_tiet_phieu_xuat_lo_hang_id] FOREIGN KEY ([lo_hang_id]) REFERENCES dbo.[lo_hang] ([id]);
GO
ALTER TABLE dbo.[chi_tiet_phieu_xuat] ADD CONSTRAINT [FK_chi_tiet_phieu_xuat_phieu_xuat_id] FOREIGN KEY ([phieu_xuat_id]) REFERENCES dbo.[phieu_xuat_kho] ([id]);
GO
ALTER TABLE dbo.[chot_ngay] ADD CONSTRAINT [FK_chot_ngay_nguoi_chot_id] FOREIGN KEY ([nguoi_chot_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[de_xuat_gia] ADD CONSTRAINT [FK_de_xuat_gia_nguoi_de_xuat_id] FOREIGN KEY ([nguoi_de_xuat_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[de_xuat_gia] ADD CONSTRAINT [FK_de_xuat_gia_san_pham_id] FOREIGN KEY ([san_pham_id]) REFERENCES dbo.[san_pham] ([id]);
GO
ALTER TABLE dbo.[don_hang] ADD CONSTRAINT [FK_don_hang_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[don_hang] ADD CONSTRAINT [FK_don_hang_phien_lam_viec_id] FOREIGN KEY ([phien_lam_viec_id]) REFERENCES dbo.[phien_lam_viec] ([id]);
GO
ALTER TABLE dbo.[gio_hang_treo] ADD CONSTRAINT [FK_gio_hang_treo_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[gio_hang_treo] ADD CONSTRAINT [FK_gio_hang_treo_tai_khoan_id] FOREIGN KEY ([tai_khoan_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[hoa_don] ADD CONSTRAINT [FK_hoa_don_don_hang_id] FOREIGN KEY ([don_hang_id]) REFERENCES dbo.[don_hang] ([id]);
GO
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_hoa_don_id] FOREIGN KEY ([hoa_don_id]) REFERENCES dbo.[hoa_don] ([id]);
GO
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_nhan_coc_boi] FOREIGN KEY ([nhan_coc_boi]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_phien_nhan_coc_id] FOREIGN KEY ([phien_nhan_coc_id]) REFERENCES dbo.[phien_lam_viec] ([id]);
GO
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_phong_id] FOREIGN KEY ([phong_id]) REFERENCES dbo.[phong_hotel] ([id]);
GO
ALTER TABLE dbo.[hop_dong_luu_tru] ADD CONSTRAINT [FK_hop_dong_luu_tru_thu_cung_id] FOREIGN KEY ([thu_cung_id]) REFERENCES dbo.[thu_cung] ([id]);
GO
ALTER TABLE dbo.[khoan_thu] ADD CONSTRAINT [FK_khoan_thu_hoa_don_id] FOREIGN KEY ([hoa_don_id]) REFERENCES dbo.[hoa_don] ([id]);
GO
ALTER TABLE dbo.[khoan_thu] ADD CONSTRAINT [FK_khoan_thu_hop_dong_id] FOREIGN KEY ([hop_dong_id]) REFERENCES dbo.[hop_dong_luu_tru] ([id]);
GO
ALTER TABLE dbo.[khoan_thu] ADD CONSTRAINT [FK_khoan_thu_nguoi_nhan_id] FOREIGN KEY ([nguoi_nhan_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[khoan_thu] ADD CONSTRAINT [FK_khoan_thu_phien_id] FOREIGN KEY ([phien_id]) REFERENCES dbo.[phien_lam_viec] ([id]);
GO
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_nhan_vien_id] FOREIGN KEY ([nhan_vien_id]) REFERENCES dbo.[nhan_vien] ([id]);
GO
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_thu_cung_id] FOREIGN KEY ([thu_cung_id]) REFERENCES dbo.[thu_cung] ([id]);
GO
ALTER TABLE dbo.[lich_hen] ADD CONSTRAINT [FK_lich_hen_dich_vu] FOREIGN KEY ([dich_vu_id]) REFERENCES dbo.[goi_dich_vu_spa] ([id]);
GO
ALTER TABLE dbo.[lich_lam_viec] ADD CONSTRAINT [FK_lich_lam_viec_ca_lam_viec_id] FOREIGN KEY ([ca_lam_viec_id]) REFERENCES dbo.[ca_lam_viec] ([id]);
GO
ALTER TABLE dbo.[lich_lam_viec] ADD CONSTRAINT [FK_lich_lam_viec_nhan_vien_id] FOREIGN KEY ([nhan_vien_id]) REFERENCES dbo.[nhan_vien] ([id]);
GO
ALTER TABLE dbo.[lich_su_diem] ADD CONSTRAINT [FK_lich_su_diem_hoa_don_id] FOREIGN KEY ([hoa_don_id]) REFERENCES dbo.[hoa_don] ([id]);
GO
ALTER TABLE dbo.[lich_su_diem] ADD CONSTRAINT [FK_lich_su_diem_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[lich_su_gia] ADD CONSTRAINT [FK_lich_su_gia_nguoi_thay_doi_id] FOREIGN KEY ([nguoi_thay_doi_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[lich_su_gia] ADD CONSTRAINT [FK_lich_su_gia_san_pham_id] FOREIGN KEY ([san_pham_id]) REFERENCES dbo.[san_pham] ([id]);
GO
ALTER TABLE dbo.[lo_hang] ADD CONSTRAINT [FK_lo_hang_san_pham_id] FOREIGN KEY ([san_pham_id]) REFERENCES dbo.[san_pham] ([id]);
GO
ALTER TABLE dbo.[nhac_viec_da_xem] ADD CONSTRAINT [FK_nhac_viec_da_xem_tai_khoan_id] FOREIGN KEY ([tai_khoan_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[nhat_ky_cham_soc] ADD CONSTRAINT [FK_nhat_ky_cham_soc_hop_dong_id] FOREIGN KEY ([hop_dong_id]) REFERENCES dbo.[hop_dong_luu_tru] ([id]);
GO
ALTER TABLE dbo.[nhat_ky_cham_soc] ADD CONSTRAINT [FK_nhat_ky_cham_soc_nhan_vien_id] FOREIGN KEY ([nhan_vien_id]) REFERENCES dbo.[nhan_vien] ([id]);
GO
ALTER TABLE dbo.[nhat_ky_he_thong] ADD CONSTRAINT [FK_nhat_ky_he_thong_tai_khoan_id] FOREIGN KEY ([tai_khoan_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[phan_bo_lo_hang] ADD CONSTRAINT [FK_phan_bo_lo_hang_chi_tiet_don_hang_id] FOREIGN KEY ([chi_tiet_don_hang_id]) REFERENCES dbo.[chi_tiet_don_hang] ([id]);
GO
ALTER TABLE dbo.[phan_bo_lo_hang] ADD CONSTRAINT [FK_phan_bo_lo_hang_lo_hang_id] FOREIGN KEY ([lo_hang_id]) REFERENCES dbo.[lo_hang] ([id]);
GO
ALTER TABLE dbo.[phien_lam_viec] ADD CONSTRAINT [FK_phien_lam_viec_lich_lam_viec_id] FOREIGN KEY ([lich_lam_viec_id]) REFERENCES dbo.[lich_lam_viec] ([id]);
GO
ALTER TABLE dbo.[phien_lam_viec] ADD CONSTRAINT [FK_phien_lam_viec_nguoi_nhan_ban_giao_id] FOREIGN KEY ([nguoi_nhan_ban_giao_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[phien_lam_viec] ADD CONSTRAINT [FK_phien_lam_viec_tai_khoan_id] FOREIGN KEY ([tai_khoan_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[phieu_cham_soc] ADD CONSTRAINT [FK_phieu_cham_soc_hotel_id] FOREIGN KEY ([hotel_id]) REFERENCES dbo.[hop_dong_luu_tru] ([id]);
GO
ALTER TABLE dbo.[phieu_cham_soc] ADD CONSTRAINT [FK_phieu_cham_soc_nhan_vien_id] FOREIGN KEY ([nhan_vien_id]) REFERENCES dbo.[nhan_vien] ([id]);
GO
ALTER TABLE dbo.[phieu_cham_soc] ADD CONSTRAINT [FK_phieu_cham_soc_spa_id] FOREIGN KEY ([spa_id]) REFERENCES dbo.[phieu_tiep_nhan_spa] ([id]);
GO
ALTER TABLE dbo.[phieu_doi_tra] ADD CONSTRAINT [FK_phieu_doi_tra_hoa_don_id] FOREIGN KEY ([hoa_don_id]) REFERENCES dbo.[hoa_don] ([id]);
GO
ALTER TABLE dbo.[phieu_doi_tra] ADD CONSTRAINT [FK_phieu_doi_tra_hoa_don_thay_the_id] FOREIGN KEY ([hoa_don_thay_the_id]) REFERENCES dbo.[hoa_don] ([id]);
GO
ALTER TABLE dbo.[phieu_doi_tra] ADD CONSTRAINT [FK_phieu_doi_tra_nguoi_lap_id] FOREIGN KEY ([nguoi_lap_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[phieu_nhap_kho] ADD CONSTRAINT [FK_phieu_nhap_kho_nguoi_lap_id] FOREIGN KEY ([nguoi_lap_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[phieu_nhap_kho] ADD CONSTRAINT [FK_phieu_nhap_kho_nha_cung_cap_id] FOREIGN KEY ([nha_cung_cap_id]) REFERENCES dbo.[nha_cung_cap] ([id]);
GO
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_hoa_don_id] FOREIGN KEY ([hoa_don_id]) REFERENCES dbo.[hoa_don] ([id]);
GO
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_lich_hen_id] FOREIGN KEY ([lich_hen_id]) REFERENCES dbo.[lich_hen] ([id]);
GO
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_nhan_vien_id] FOREIGN KEY ([nhan_vien_id]) REFERENCES dbo.[nhan_vien] ([id]);
GO
ALTER TABLE dbo.[phieu_tiep_nhan_spa] ADD CONSTRAINT [FK_phieu_tiep_nhan_spa_thu_cung_id] FOREIGN KEY ([thu_cung_id]) REFERENCES dbo.[thu_cung] ([id]);
GO
ALTER TABLE dbo.[phieu_xuat_kho] ADD CONSTRAINT [FK_phieu_xuat_kho_nguoi_lap_id] FOREIGN KEY ([nguoi_lap_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[tai_khoan] ADD CONSTRAINT [FK_tai_khoan_nhan_vien_id] FOREIGN KEY ([nhan_vien_id]) REFERENCES dbo.[nhan_vien] ([id]);
GO
ALTER TABLE dbo.[tiem_phong] ADD CONSTRAINT [FK_tiem_phong_thu_cung_id] FOREIGN KEY ([thu_cung_id]) REFERENCES dbo.[thu_cung] ([id]);
GO
ALTER TABLE dbo.[thu_cung] ADD CONSTRAINT [FK_thu_cung_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[vai_tro_quyen] ADD CONSTRAINT [FK_vai_tro_quyen_ma_quyen] FOREIGN KEY ([ma_quyen]) REFERENCES dbo.[quyen] ([ma_quyen]);
GO
ALTER TABLE dbo.[voucher] ADD CONSTRAINT [FK_voucher_khach_hang_id] FOREIGN KEY ([khach_hang_id]) REFERENCES dbo.[khach_hang] ([id]);
GO
ALTER TABLE dbo.[yeu_cau_phe_duyet] ADD CONSTRAINT [FK_yeu_cau_phe_duyet_nguoi_gui_id] FOREIGN KEY ([nguoi_gui_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO
ALTER TABLE dbo.[yeu_cau_phe_duyet] ADD CONSTRAINT [FK_yeu_cau_phe_duyet_nguoi_xu_ly_id] FOREIGN KEY ([nguoi_xu_ly_id]) REFERENCES dbo.[tai_khoan] ([id]);
GO

-- Chỉ mục phục vụ tra cứu.
CREATE INDEX [IX_don_hang_phien_trang_thai] ON dbo.[don_hang] ([phien_lam_viec_id], [trang_thai]);
GO
CREATE INDEX [IX_hoa_don_thoi_gian] ON dbo.[hoa_don] ([thanh_toan_luc] DESC);
GO
CREATE INDEX [IX_hotel_thoi_gian] ON dbo.[hop_dong_luu_tru] ([phong_id], [ngay_vao], [ngay_ra_du_kien], [trang_thai]);
GO
CREATE INDEX [IX_khach_hang_phone] ON dbo.[khach_hang] ([so_dien_thoai]);
GO
CREATE INDEX [IX_khoan_thu_phien] ON dbo.[khoan_thu] ([phien_id], [phuong_thuc]) INCLUDE ([so_tien], [hoa_don_id], [hop_dong_id]);
GO
CREATE INDEX [IX_khoan_thu_ngay] ON dbo.[khoan_thu] ([nhan_luc]) INCLUDE ([so_tien], [phien_id], [hoa_don_id], [hop_dong_id], [phuong_thuc]);
GO
CREATE INDEX [IX_lich_hen_thoi_gian] ON dbo.[lich_hen] ([bat_dau], [ket_thuc], [trang_thai]);
GO
CREATE INDEX [IX_lich_ngay_ca] ON dbo.[lich_lam_viec] ([ngay_lam], [ca_lam_viec_id]);
GO
CREATE INDEX [IX_lo_hang_fefo] ON dbo.[lo_hang] ([san_pham_id], [han_su_dung], [trang_thai]) INCLUDE ([so_luong_ton]);
GO
CREATE INDEX [IX_audit_time] ON dbo.[nhat_ky_he_thong] ([thoi_gian] DESC);
GO
CREATE UNIQUE INDEX [UX_phien_open] ON dbo.[phien_lam_viec] ([tai_khoan_id]) WHERE ([trang_thai]='OPEN');
GO
CREATE UNIQUE INDEX [UX_doi_hang_khoa] ON dbo.[phieu_doi_tra] ([khoa_thao_tac]) WHERE ([khoa_thao_tac] IS NOT NULL);
GO
CREATE UNIQUE INDEX [UX_doi_hang_hoa_don] ON dbo.[phieu_doi_tra] ([hoa_don_thay_the_id]) WHERE ([hoa_don_thay_the_id] IS NOT NULL);
GO
CREATE INDEX [IX_spa_pending] ON dbo.[phieu_tiep_nhan_spa] ([trang_thai], [tao_luc]);
GO
CREATE UNIQUE INDEX [UX_san_pham_barcode] ON dbo.[san_pham] ([barcode]) WHERE ([barcode] IS NOT NULL);
GO
CREATE INDEX [IX_san_pham_barcode] ON dbo.[san_pham] ([barcode]) WHERE ([barcode] IS NOT NULL);
GO
CREATE INDEX [IX_tai_khoan_username_status] ON dbo.[tai_khoan] ([ten_dang_nhap], [trang_thai]);
GO
CREATE INDEX [IX_tiem_phong_thu_cung] ON dbo.[tiem_phong] ([thu_cung_id], [ngay_tiem] DESC);
GO
CREATE UNIQUE INDEX [UX_voucher_doi_diem] ON dbo.[voucher] ([khoa_doi_diem]) WHERE ([khoa_doi_diem] IS NOT NULL);
GO
CREATE INDEX [IX_approval_status] ON dbo.[yeu_cau_phe_duyet] ([trang_thai], [gui_luc]);
GO
