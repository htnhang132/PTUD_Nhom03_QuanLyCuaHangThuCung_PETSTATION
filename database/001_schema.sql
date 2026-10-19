-- Pet Station / SQL Server 2019+. Chon database dich truoc khi chay.
-- Chay lai khong xoa bang hoac du lieu hien co.

SET XACT_ABORT ON;
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET ARITHABORT ON;
SET NUMERIC_ROUNDABORT OFF;
GO

IF OBJECT_ID(N'dbo.nhan_vien', N'U') IS NULL
CREATE TABLE nhan_vien (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_nhan_vien VARCHAR(20) NOT NULL UNIQUE,
    ho_ten NVARCHAR(120) NOT NULL,
    ngay_sinh DATE NULL,
    so_dien_thoai VARCHAR(15) NOT NULL UNIQUE,
    dia_chi NVARCHAR(300) NULL,
    chuc_vu NVARCHAR(80) NOT NULL,
    ngay_vao_lam DATE NOT NULL,
    anh_dai_dien NVARCHAR(500) NULL,
    dang_lam_viec BIT NOT NULL DEFAULT 1,
    row_version ROWVERSION
);
GO

IF OBJECT_ID(N'dbo.quyen', N'U') IS NULL
CREATE TABLE quyen (
    ma_quyen VARCHAR(60) PRIMARY KEY,
    ten_quyen NVARCHAR(160) NOT NULL
);
GO

IF OBJECT_ID(N'dbo.vai_tro_quyen', N'U') IS NULL
CREATE TABLE vai_tro_quyen (
    vai_tro VARCHAR(40) NOT NULL,
    ma_quyen VARCHAR(60) NOT NULL REFERENCES quyen(ma_quyen),
    CONSTRAINT PK_vai_tro_quyen PRIMARY KEY (vai_tro, ma_quyen),
    CONSTRAINT CK_vai_tro_quyen_vai_tro CHECK (
        vai_tro IN ('STORE_MANAGER','WAREHOUSE_STAFF','SALES_STAFF','SPA_HOTEL_STAFF')
    )
);
GO

IF OBJECT_ID(N'dbo.tai_khoan', N'U') IS NULL
CREATE TABLE tai_khoan (
    id BIGINT IDENTITY PRIMARY KEY,
    nhan_vien_id BIGINT NOT NULL UNIQUE REFERENCES nhan_vien(id),
    ten_dang_nhap VARCHAR(80) NOT NULL UNIQUE,
    mat_khau_hash VARCHAR(100) NOT NULL,
    vai_tro VARCHAR(40) NOT NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    so_lan_dang_nhap_sai INT NOT NULL DEFAULT 0,
    khoa_den DATETIME2 NULL,
    bat_buoc_doi_mat_khau BIT NOT NULL DEFAULT 1,
    dang_nhap_cuoi DATETIME2 NULL,
    CONSTRAINT CK_tai_khoan_vai_tro CHECK (
        vai_tro IN ('STORE_MANAGER','WAREHOUSE_STAFF','SALES_STAFF','SPA_HOTEL_STAFF')
    ),
    CONSTRAINT CK_tai_khoan_trang_thai CHECK (trang_thai IN ('ACTIVE','INACTIVE'))
);
GO

IF OBJECT_ID(N'dbo.ca_lam_viec', N'U') IS NULL
CREATE TABLE ca_lam_viec (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_ca VARCHAR(20) NOT NULL UNIQUE,
    ten_ca NVARCHAR(60) NOT NULL,
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    so_nhan_su_toi_thieu INT NOT NULL DEFAULT 1 CHECK (so_nhan_su_toi_thieu > 0)
);

IF OBJECT_ID(N'dbo.lich_lam_viec', N'U') IS NULL
CREATE TABLE lich_lam_viec (
    id BIGINT IDENTITY PRIMARY KEY,
    nhan_vien_id BIGINT NOT NULL REFERENCES nhan_vien(id),
    ca_lam_viec_id BIGINT NOT NULL REFERENCES ca_lam_viec(id),
    ngay_lam DATE NOT NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT UQ_lich_lam_viec UNIQUE (nhan_vien_id, ngay_lam, ca_lam_viec_id),
    CONSTRAINT CK_lich_trang_thai CHECK (trang_thai IN ('OPEN','LOCKED','CANCELLED'))
);

IF OBJECT_ID(N'dbo.phien_lam_viec', N'U') IS NULL
CREATE TABLE phien_lam_viec (
    id BIGINT IDENTITY PRIMARY KEY,
    lich_lam_viec_id BIGINT NULL REFERENCES lich_lam_viec(id),
    tai_khoan_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    mo_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    dong_luc DATETIME2 NULL,
    tien_dau_ca DECIMAL(18,2) NOT NULL CHECK (tien_dau_ca >= 0),
    tien_thuc_te DECIMAL(18,2) NULL,
    chenh_lech DECIMAL(18,2) NULL,
    giai_trinh NVARCHAR(1000) NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT CK_phien_trang_thai CHECK (
        trang_thai IN ('OPEN','PENDING_APPROVAL','RECONCILED','HANDED_OVER','LOCKED')
    )
);

IF OBJECT_ID(N'dbo.khach_hang', N'U') IS NULL
CREATE TABLE khach_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_khach_hang VARCHAR(20) NOT NULL UNIQUE,
    ho_ten NVARCHAR(120) NOT NULL,
    so_dien_thoai VARCHAR(15) NOT NULL UNIQUE,
    dia_chi NVARCHAR(300) NULL,
    ngay_sinh DATE NULL,
    diem_tich_luy INT NOT NULL DEFAULT 0 CHECK (diem_tich_luy >= 0),
    hang_thanh_vien NVARCHAR(20) NOT NULL DEFAULT N'REGULAR',
    dang_hoat_dong BIT NOT NULL DEFAULT 1,
    row_version ROWVERSION
);

IF OBJECT_ID(N'dbo.thu_cung', N'U') IS NULL
CREATE TABLE thu_cung (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_thu_cung VARCHAR(20) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    ten NVARCHAR(80) NOT NULL,
    loai VARCHAR(10) NOT NULL CHECK (loai IN ('DOG','CAT')),
    giong NVARCHAR(100) NULL,
    gioi_tinh NVARCHAR(10) NULL,
    mau_long NVARCHAR(80) NULL,
    can_nang DECIMAL(8,2) NULL CHECK (can_nang > 0),
    tinh_trang_suc_khoe NVARCHAR(1000) NULL,
    di_ung_benh_ly NVARCHAR(1000) NULL,
    anh NVARCHAR(500) NULL,
    dang_theo_doi BIT NOT NULL DEFAULT 1
);

IF OBJECT_ID(N'dbo.tiem_phong', N'U') IS NULL
CREATE TABLE tiem_phong (
    id BIGINT IDENTITY PRIMARY KEY,
    thu_cung_id BIGINT NOT NULL REFERENCES thu_cung(id),
    ten_vaccine NVARCHAR(120) NOT NULL,
    ngay_tiem DATE NOT NULL,
    ngay_het_han DATE NULL,
    ghi_chu NVARCHAR(500) NULL
);

IF OBJECT_ID(N'dbo.nha_cung_cap', N'U') IS NULL
CREATE TABLE nha_cung_cap (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_ncc VARCHAR(20) NOT NULL UNIQUE,
    ten NVARCHAR(160) NOT NULL,
    so_dien_thoai VARCHAR(20) NOT NULL,
    dia_chi NVARCHAR(300) NULL,
    nguoi_dai_dien NVARCHAR(120) NULL,
    nhom_mat_hang NVARCHAR(200) NULL,
    ghi_chu NVARCHAR(500) NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'COOPERATING'
);

IF OBJECT_ID(N'dbo.san_pham', N'U') IS NULL
CREATE TABLE san_pham (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_san_pham VARCHAR(30) NOT NULL UNIQUE,
    barcode VARCHAR(80) NULL,
    ten NVARCHAR(180) NOT NULL UNIQUE,
    nhom_hang NVARCHAR(100) NOT NULL,
    don_vi_tinh NVARCHAR(30) NOT NULL,
    gia_ban DECIMAL(18,2) NOT NULL CHECK (gia_ban > 0),
    gia_nhap_gan_nhat DECIMAL(18,2) NULL CHECK (gia_nhap_gan_nhat > 0),
    ton_toi_thieu INT NOT NULL DEFAULT 0 CHECK (ton_toi_thieu >= 0),
    theo_doi_hsd BIT NOT NULL DEFAULT 0,
    anh NVARCHAR(500) NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'SELLING',
    row_version ROWVERSION
);

IF OBJECT_ID(N'dbo.lo_hang', N'U') IS NULL
CREATE TABLE lo_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    so_lo VARCHAR(60) NOT NULL,
    han_su_dung DATE NULL,
    so_luong_ton INT NOT NULL CHECK (so_luong_ton >= 0),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    row_version ROWVERSION,
    CONSTRAINT UQ_lo_hang UNIQUE (san_pham_id, so_lo)
);

IF OBJECT_ID(N'dbo.phieu_nhap_kho', N'U') IS NULL
CREATE TABLE phieu_nhap_kho (
    id BIGINT IDENTITY PRIMARY KEY,
    so_phieu VARCHAR(30) NOT NULL UNIQUE,
    nha_cung_cap_id BIGINT NOT NULL REFERENCES nha_cung_cap(id),
    nguoi_lap_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    ngay_nhap DATE NOT NULL,
    tong_tien DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (tong_tien >= 0),
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    ghi_chu NVARCHAR(500) NULL
);

IF OBJECT_ID(N'dbo.chi_tiet_phieu_nhap', N'U') IS NULL
CREATE TABLE chi_tiet_phieu_nhap (
    id BIGINT IDENTITY PRIMARY KEY,
    phieu_nhap_id BIGINT NOT NULL REFERENCES phieu_nhap_kho(id),
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    so_lo VARCHAR(60) NOT NULL,
    han_su_dung DATE NULL,
    so_luong INT NOT NULL CHECK (so_luong > 0),
    gia_nhap DECIMAL(18,2) NOT NULL CHECK (gia_nhap > 0)
);

IF OBJECT_ID(N'dbo.phieu_xuat_kho', N'U') IS NULL
CREATE TABLE phieu_xuat_kho (
    id BIGINT IDENTITY PRIMARY KEY,
    so_phieu VARCHAR(30) NOT NULL UNIQUE,
    nguoi_lap_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    loai_xuat VARCHAR(40) NOT NULL,
    ly_do NVARCHAR(1000) NOT NULL,
    tong_gia_tri DECIMAL(18,2) NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID(N'dbo.chi_tiet_phieu_xuat', N'U') IS NULL
CREATE TABLE chi_tiet_phieu_xuat (
    id BIGINT IDENTITY PRIMARY KEY,
    phieu_xuat_id BIGINT NOT NULL REFERENCES phieu_xuat_kho(id),
    lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    gia_tri DECIMAL(18,2) NOT NULL CHECK (gia_tri >= 0)
);

IF OBJECT_ID(N'dbo.bien_ban_kiem_ke', N'U') IS NULL
CREATE TABLE bien_ban_kiem_ke (
    id BIGINT IDENTITY PRIMARY KEY,
    so_bien_ban VARCHAR(30) NOT NULL UNIQUE,
    nguoi_lap_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    ky_kiem_ke VARCHAR(30) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'COUNTING',
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID(N'dbo.chi_tiet_kiem_ke', N'U') IS NULL
CREATE TABLE chi_tiet_kiem_ke (
    id BIGINT IDENTITY PRIMARY KEY,
    bien_ban_id BIGINT NOT NULL REFERENCES bien_ban_kiem_ke(id),
    lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id),
    ton_he_thong INT NOT NULL,
    ton_thuc_te INT NOT NULL,
    nguyen_nhan NVARCHAR(1000) NULL
);

IF OBJECT_ID(N'dbo.chuong_trinh_khuyen_mai', N'U') IS NULL
CREATE TABLE chuong_trinh_khuyen_mai (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_chuong_trinh VARCHAR(30) NOT NULL UNIQUE,
    ten NVARCHAR(180) NOT NULL,
    loai_giam VARCHAR(20) NOT NULL,
    gia_tri DECIMAL(18,2) NOT NULL CHECK (gia_tri > 0),
    bat_dau DATETIME2 NOT NULL,
    ket_thuc DATETIME2 NOT NULL,
    nguon VARCHAR(30) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    CONSTRAINT CK_km_ngay CHECK (ket_thuc > bat_dau)
);

IF OBJECT_ID(N'dbo.de_xuat_gia', N'U') IS NULL
CREATE TABLE de_xuat_gia (
    id BIGINT IDENTITY PRIMARY KEY,
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    nguoi_de_xuat_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    gia_hien_tai DECIMAL(18,2) NOT NULL,
    gia_de_xuat DECIMAL(18,2) NOT NULL CHECK (gia_de_xuat > 0),
    ly_do NVARCHAR(500) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'PENDING'
);

IF OBJECT_ID(N'dbo.don_hang', N'U') IS NULL
CREATE TABLE don_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_don VARCHAR(30) NOT NULL UNIQUE,
    phien_lam_viec_id BIGINT NOT NULL REFERENCES phien_lam_viec(id),
    khach_hang_id BIGINT NULL REFERENCES khach_hang(id),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID(N'dbo.chi_tiet_don_hang', N'U') IS NULL
CREATE TABLE chi_tiet_don_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    don_hang_id BIGINT NOT NULL REFERENCES don_hang(id),
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    don_gia DECIMAL(18,2) NOT NULL CHECK (don_gia >= 0),
    giam_gia DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (giam_gia >= 0)
);

IF OBJECT_ID(N'dbo.hoa_don', N'U') IS NULL
CREATE TABLE hoa_don (
    id BIGINT IDENTITY PRIMARY KEY,
    so_hoa_don VARCHAR(30) NOT NULL UNIQUE,
    don_hang_id BIGINT NOT NULL UNIQUE REFERENCES don_hang(id),
    tong_tien DECIMAL(18,2) NOT NULL CHECK (tong_tien >= 0),
    hinh_thuc_thanh_toan VARCHAR(30) NOT NULL,
    tien_khach_dua DECIMAL(18,2) NULL,
    tien_thoi DECIMAL(18,2) NULL,
    diem_cong INT NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'PAID',
    thanh_toan_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    idempotency_key VARCHAR(80) NOT NULL UNIQUE
);

IF OBJECT_ID(N'dbo.phan_bo_lo_hang', N'U') IS NULL
CREATE TABLE phan_bo_lo_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    chi_tiet_don_hang_id BIGINT NOT NULL REFERENCES chi_tiet_don_hang(id),
    lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id),
    so_luong INT NOT NULL CHECK (so_luong > 0)
);

IF OBJECT_ID(N'dbo.lich_su_diem', N'U') IS NULL
CREATE TABLE lich_su_diem (
    id BIGINT IDENTITY PRIMARY KEY,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    hoa_don_id BIGINT NULL REFERENCES hoa_don(id),
    thay_doi INT NOT NULL,
    so_du_sau INT NOT NULL CHECK (so_du_sau >= 0),
    ly_do NVARCHAR(300) NOT NULL,
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID(N'dbo.voucher', N'U') IS NULL
CREATE TABLE voucher (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_voucher VARCHAR(40) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    menh_gia DECIMAL(18,2) NOT NULL CHECK (menh_gia > 0),
    hieu_luc_den DATETIME2 NOT NULL,
    su_dung_luc DATETIME2 NULL,
    chi_ap_dung_spa BIT NOT NULL DEFAULT 1
);

IF OBJECT_ID(N'dbo.lich_hen', N'U') IS NULL
CREATE TABLE lich_hen (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_lich_hen VARCHAR(30) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    thu_cung_id BIGINT NOT NULL REFERENCES thu_cung(id),
    loai_dich_vu VARCHAR(20) NOT NULL,
    bat_dau DATETIME2 NOT NULL,
    ket_thuc DATETIME2 NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    ghi_chu NVARCHAR(1000) NULL
);

IF OBJECT_ID(N'dbo.phieu_tiep_nhan_spa', N'U') IS NULL
CREATE TABLE phieu_tiep_nhan_spa (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_phieu VARCHAR(30) NOT NULL UNIQUE,
    lich_hen_id BIGINT NULL REFERENCES lich_hen(id),
    thu_cung_id BIGINT NOT NULL REFERENCES thu_cung(id),
    can_nang_thuc_te DECIMAL(8,2) NOT NULL CHECK (can_nang_thuc_te > 0),
    tong_tien DECIMAL(18,2) NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL,
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID(N'dbo.phong_hotel', N'U') IS NULL
CREATE TABLE phong_hotel (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_phong VARCHAR(20) NOT NULL UNIQUE,
    loai_phong NVARCHAR(80) NOT NULL,
    gia_dem DECIMAL(18,2) NOT NULL CHECK (gia_dem > 0),
    trang_thai VARCHAR(30) NOT NULL,
    row_version ROWVERSION
);

IF OBJECT_ID(N'dbo.hop_dong_luu_tru', N'U') IS NULL
CREATE TABLE hop_dong_luu_tru (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_hop_dong VARCHAR(30) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    thu_cung_id BIGINT NOT NULL REFERENCES thu_cung(id),
    phong_id BIGINT NOT NULL REFERENCES phong_hotel(id),
    ngay_vao DATETIME2 NOT NULL,
    ngay_ra_du_kien DATETIME2 NOT NULL,
    ngay_ra_thuc_te DATETIME2 NULL,
    tien_coc DECIMAL(18,2) NOT NULL DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL
);

IF OBJECT_ID(N'dbo.nhat_ky_cham_soc', N'U') IS NULL
CREATE TABLE nhat_ky_cham_soc (
    id BIGINT IDENTITY PRIMARY KEY,
    hop_dong_id BIGINT NOT NULL REFERENCES hop_dong_luu_tru(id),
    nhan_vien_id BIGINT NOT NULL REFERENCES nhan_vien(id),
    thoi_gian DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    an_uong NVARCHAR(500) NULL,
    ve_sinh NVARCHAR(500) NULL,
    tinh_trang NVARCHAR(1000) NOT NULL,
    tep_dinh_kem NVARCHAR(500) NULL
);

IF OBJECT_ID(N'dbo.phieu_doi_tra', N'U') IS NULL
CREATE TABLE phieu_doi_tra (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_phieu VARCHAR(30) NOT NULL UNIQUE,
    hoa_don_id BIGINT NOT NULL REFERENCES hoa_don(id),
    nguoi_lap_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    chenh_lech DECIMAL(18,2) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

IF OBJECT_ID(N'dbo.yeu_cau_phe_duyet', N'U') IS NULL
CREATE TABLE yeu_cau_phe_duyet (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_yeu_cau VARCHAR(30) NOT NULL UNIQUE,
    loai VARCHAR(50) NOT NULL,
    nguoi_gui_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    loai_doi_tuong VARCHAR(50) NOT NULL,
    ma_doi_tuong VARCHAR(50) NOT NULL,
    ly_do NVARCHAR(1000) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    nguoi_xu_ly_id BIGINT NULL REFERENCES tai_khoan(id),
    phan_hoi NVARCHAR(1000) NULL,
    gui_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    xu_ly_luc DATETIME2 NULL
);

IF OBJECT_ID(N'dbo.nhat_ky_he_thong', N'U') IS NULL
CREATE TABLE nhat_ky_he_thong (
    id BIGINT IDENTITY PRIMARY KEY,
    tai_khoan_id BIGINT NULL REFERENCES tai_khoan(id),
    hanh_dong VARCHAR(60) NOT NULL,
    loai_doi_tuong VARCHAR(60) NULL,
    ma_doi_tuong VARCHAR(60) NULL,
    gia_tri_cu NVARCHAR(MAX) NULL,
    gia_tri_moi NVARCHAR(MAX) NULL,
    ghi_chu NVARCHAR(1000) NULL,
    thoi_gian DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);
GO
-- A fully prepaid hotel bill can have a zero remaining balance.
DECLARE @lineCheck sysname, @lineSql nvarchar(500);
SELECT @lineCheck=name FROM sys.check_constraints
WHERE parent_object_id=OBJECT_ID('chi_tiet_don_hang')
 AND parent_column_id=COLUMNPROPERTY(OBJECT_ID('chi_tiet_don_hang'),'don_gia','ColumnId');
IF @lineCheck IS NOT NULL
BEGIN
 SET @lineSql=N'ALTER TABLE chi_tiet_don_hang DROP CONSTRAINT '+QUOTENAME(@lineCheck);
 EXEC(@lineSql);
END;
IF NOT EXISTS(SELECT 1 FROM sys.check_constraints WHERE name='CK_chi_tiet_don_gia')
 ALTER TABLE chi_tiet_don_hang ADD CONSTRAINT CK_chi_tiet_don_gia CHECK(don_gia>=0);
GO

IF COL_LENGTH('dbo.nhan_vien', 'cccd') IS NULL
 ALTER TABLE dbo.nhan_vien ADD cccd VARCHAR(12) NULL;
GO

IF COL_LENGTH('dbo.khach_hang', 'email') IS NULL
 ALTER TABLE dbo.khach_hang ADD email NVARCHAR(160) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.khach_hang', 'ghi_chu') IS NULL
 ALTER TABLE dbo.khach_hang ADD ghi_chu NVARCHAR(1000) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.khach_hang', 'ngay_tao') IS NULL
 ALTER TABLE dbo.khach_hang ADD ngay_tao DATETIME2 NOT NULL DEFAULT SYSDATETIME();
GO

IF COL_LENGTH('dbo.thu_cung', 'ngay_sinh') IS NULL
 ALTER TABLE dbo.thu_cung ADD ngay_sinh DATE NULL;
GO

IF COL_LENGTH('dbo.thu_cung', 'tinh_cach') IS NULL
 ALTER TABLE dbo.thu_cung ADD tinh_cach NVARCHAR(500) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.thu_cung', 'tiem_chung') IS NULL
 ALTER TABLE dbo.thu_cung ADD tiem_chung NVARCHAR(1000) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.nha_cung_cap', 'ma_so_thue') IS NULL
 ALTER TABLE dbo.nha_cung_cap ADD ma_so_thue VARCHAR(30) NULL;
GO

IF COL_LENGTH('dbo.nha_cung_cap', 'dieu_khoan') IS NULL
 ALTER TABLE dbo.nha_cung_cap ADD dieu_khoan NVARCHAR(100) NOT NULL DEFAULT N'Trả ngay';
GO

IF COL_LENGTH('dbo.chuong_trinh_khuyen_mai', 'don_toi_thieu') IS NULL
 ALTER TABLE dbo.chuong_trinh_khuyen_mai ADD don_toi_thieu DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (don_toi_thieu >= 0);
GO

IF COL_LENGTH('dbo.phong_hotel', 'suc_chua') IS NULL
 ALTER TABLE dbo.phong_hotel ADD suc_chua INT NOT NULL DEFAULT 1 CHECK (suc_chua > 0);
GO

IF COL_LENGTH('dbo.phong_hotel', 'ghi_chu') IS NULL
 ALTER TABLE dbo.phong_hotel ADD ghi_chu NVARCHAR(1000) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.lich_hen', 'nhan_vien_id') IS NULL
 ALTER TABLE dbo.lich_hen ADD nhan_vien_id BIGINT NULL REFERENCES nhan_vien(id);
GO

IF COL_LENGTH('dbo.lich_hen', 'dich_vu_id') IS NULL
 ALTER TABLE dbo.lich_hen ADD dich_vu_id BIGINT NULL;
GO

IF COL_LENGTH('dbo.phieu_tiep_nhan_spa', 'nhan_vien_id') IS NULL
 ALTER TABLE dbo.phieu_tiep_nhan_spa ADD nhan_vien_id BIGINT NULL REFERENCES nhan_vien(id);
GO

IF COL_LENGTH('dbo.phieu_tiep_nhan_spa', 'ghi_chu') IS NULL
 ALTER TABLE dbo.phieu_tiep_nhan_spa ADD ghi_chu NVARCHAR(1000) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.phieu_tiep_nhan_spa', 'anh') IS NULL
 ALTER TABLE dbo.phieu_tiep_nhan_spa ADD anh NVARCHAR(500) NULL;
GO

IF COL_LENGTH('dbo.phieu_tiep_nhan_spa', 'hoa_don_id') IS NULL
 ALTER TABLE dbo.phieu_tiep_nhan_spa ADD hoa_don_id BIGINT NULL REFERENCES hoa_don(id);
GO

IF COL_LENGTH('dbo.hop_dong_luu_tru', 'gia_dem') IS NULL
 ALTER TABLE dbo.hop_dong_luu_tru ADD gia_dem DECIMAL(18,2) NULL CHECK (gia_dem > 0);
GO

IF COL_LENGTH('dbo.hop_dong_luu_tru', 'ghi_chu') IS NULL
 ALTER TABLE dbo.hop_dong_luu_tru ADD ghi_chu NVARCHAR(1000) NOT NULL DEFAULT N'';
GO

IF COL_LENGTH('dbo.hop_dong_luu_tru', 'hoa_don_id') IS NULL
 ALTER TABLE dbo.hop_dong_luu_tru ADD hoa_don_id BIGINT NULL REFERENCES hoa_don(id);
GO

IF COL_LENGTH('dbo.phien_lam_viec', 'ten_ca') IS NULL
 ALTER TABLE dbo.phien_lam_viec ADD ten_ca NVARCHAR(80) NOT NULL DEFAULT N'Ca làm việc';
GO

IF COL_LENGTH('dbo.phien_lam_viec', 'nguoi_nhan_ban_giao_id') IS NULL
 ALTER TABLE dbo.phien_lam_viec ADD nguoi_nhan_ban_giao_id BIGINT NULL REFERENCES tai_khoan(id);
GO

IF COL_LENGTH('dbo.hoa_don', 'tam_tinh') IS NULL
 ALTER TABLE dbo.hoa_don ADD tam_tinh DECIMAL(18,2) NOT NULL DEFAULT 0;
GO
IF COL_LENGTH('dbo.hoa_don', 'ghi_chu') IS NULL
 ALTER TABLE dbo.hoa_don ADD ghi_chu NVARCHAR(1000) NULL;
GO

IF COL_LENGTH('dbo.hoa_don', 'giam_gia') IS NULL
 ALTER TABLE dbo.hoa_don ADD giam_gia DECIMAL(18,2) NOT NULL DEFAULT 0;
GO

IF COL_LENGTH('dbo.hoa_don', 'diem_dung') IS NULL
 ALTER TABLE dbo.hoa_don ADD diem_dung INT NOT NULL DEFAULT 0;
GO

IF COL_LENGTH('dbo.hoa_don', 'ma_voucher') IS NULL
 ALTER TABLE dbo.hoa_don ADD ma_voucher VARCHAR(40) NULL;
GO

IF COL_LENGTH('dbo.chi_tiet_don_hang', 'ma_dich_vu') IS NULL
 ALTER TABLE dbo.chi_tiet_don_hang ADD ma_dich_vu VARCHAR(40) NULL;
GO

IF COL_LENGTH('dbo.chi_tiet_don_hang', 'ten_hang') IS NULL
 ALTER TABLE dbo.chi_tiet_don_hang ADD ten_hang NVARCHAR(180) NULL;
GO

IF COL_LENGTH('dbo.phieu_nhap_kho', 'da_tra') IS NULL
 ALTER TABLE dbo.phieu_nhap_kho ADD da_tra DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (da_tra >= 0);
GO

IF COL_LENGTH('dbo.yeu_cau_phe_duyet', 'noi_dung_truoc') IS NULL
 ALTER TABLE dbo.yeu_cau_phe_duyet ADD noi_dung_truoc NVARCHAR(MAX) NULL;
GO

IF COL_LENGTH('dbo.yeu_cau_phe_duyet', 'noi_dung_sau') IS NULL
 ALTER TABLE dbo.yeu_cau_phe_duyet ADD noi_dung_sau NVARCHAR(MAX) NULL;
GO

IF COL_LENGTH('dbo.yeu_cau_phe_duyet', 'payload') IS NULL
 ALTER TABLE dbo.yeu_cau_phe_duyet ADD payload NVARCHAR(MAX) NULL;
GO

IF COL_LENGTH('dbo.phieu_doi_tra', 'ly_do') IS NULL
 ALTER TABLE dbo.phieu_doi_tra ADD ly_do NVARCHAR(1000) NULL;
GO

IF COL_LENGTH('dbo.lich_lam_viec', 'loai') IS NULL
 ALTER TABLE dbo.lich_lam_viec ADD loai VARCHAR(10) NOT NULL DEFAULT 'WORK' CHECK (loai IN ('WORK','LEAVE'));
GO

IF COL_LENGTH('dbo.nhat_ky_he_thong', 'nguoi_dung') IS NULL
 ALTER TABLE dbo.nhat_ky_he_thong ADD nguoi_dung NVARCHAR(120) NULL;
GO

IF COL_LENGTH('dbo.nhat_ky_he_thong', 'loai_su_kien') IS NULL
 ALTER TABLE dbo.nhat_ky_he_thong ADD loai_su_kien VARCHAR(30) NULL;
GO

IF COL_LENGTH('dbo.nhat_ky_he_thong', 'ket_qua') IS NULL
 ALTER TABLE dbo.nhat_ky_he_thong ADD ket_qua NVARCHAR(100) NULL;
GO

IF COL_LENGTH('dbo.voucher', 'so_luong') IS NULL
 ALTER TABLE dbo.voucher ADD so_luong INT NOT NULL DEFAULT 1 CHECK (so_luong >= 0);
GO

IF COL_LENGTH('dbo.voucher', 'da_dung') IS NULL
 ALTER TABLE dbo.voucher ADD da_dung INT NOT NULL DEFAULT 0 CHECK (da_dung >= 0);
GO

IF COL_LENGTH('dbo.voucher', 'don_toi_thieu') IS NULL
 ALTER TABLE dbo.voucher ADD don_toi_thieu DECIMAL(18,2) NOT NULL DEFAULT 0;
GO

IF COL_LENGTH('dbo.voucher', 'trang_thai') IS NULL
 ALTER TABLE dbo.voucher ADD trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
GO

ALTER TABLE voucher ALTER COLUMN khach_hang_id BIGINT NULL;
ALTER TABLE chi_tiet_don_hang ALTER COLUMN san_pham_id BIGINT NULL;
GO
IF OBJECT_ID('dbo.goi_dich_vu_spa','U') IS NULL
CREATE TABLE goi_dich_vu_spa (
 id BIGINT IDENTITY PRIMARY KEY, ma_goi VARCHAR(30) NOT NULL UNIQUE,
 ten_goi NVARCHAR(180) NOT NULL UNIQUE, mo_ta NVARCHAR(1000) NULL,
 thoi_gian_du_kien INT NOT NULL CHECK (thoi_gian_du_kien > 0),
 trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);
IF OBJECT_ID('dbo.bang_gia_spa','U') IS NULL
CREATE TABLE bang_gia_spa (
 id BIGINT IDENTITY PRIMARY KEY, goi_id BIGINT NOT NULL REFERENCES goi_dich_vu_spa(id),
 loai_thu_cung VARCHAR(10) NOT NULL CHECK (loai_thu_cung IN ('DOG','CAT')),
 khoang_can NVARCHAR(60) NOT NULL, can_nang_tu DECIMAL(8,2) NOT NULL DEFAULT 0,
 can_nang_den DECIMAL(8,2) NULL, don_gia DECIMAL(18,2) NOT NULL CHECK (don_gia > 0),
 CHECK (can_nang_den IS NULL OR can_nang_den > can_nang_tu),
 UNIQUE(goi_id,loai_thu_cung,khoang_can)
);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name='FK_lich_hen_dich_vu')
 ALTER TABLE lich_hen ADD CONSTRAINT FK_lich_hen_dich_vu FOREIGN KEY(dich_vu_id) REFERENCES goi_dich_vu_spa(id);
IF OBJECT_ID('dbo.chi_tiet_phieu_spa','U') IS NULL
CREATE TABLE chi_tiet_phieu_spa (
 id BIGINT IDENTITY PRIMARY KEY, phieu_spa_id BIGINT NOT NULL REFERENCES phieu_tiep_nhan_spa(id),
 goi_id BIGINT NULL REFERENCES goi_dich_vu_spa(id), mo_ta NVARCHAR(180) NOT NULL,
 don_gia DECIMAL(18,2) NOT NULL CHECK(don_gia >= 0), so_luong INT NOT NULL DEFAULT 1 CHECK(so_luong > 0),
 thanh_tien AS (don_gia * so_luong) PERSISTED
);
IF OBJECT_ID('dbo.cai_dat','U') IS NULL
CREATE TABLE cai_dat (khoa VARCHAR(100) PRIMARY KEY, gia_tri NVARCHAR(1000) NOT NULL,
 nguoi_cap_nhat_id BIGINT NULL REFERENCES tai_khoan(id), ngay_cap_nhat DATETIME2 NOT NULL DEFAULT SYSDATETIME());
IF OBJECT_ID('dbo.lich_su_gia','U') IS NULL
CREATE TABLE lich_su_gia (id BIGINT IDENTITY PRIMARY KEY,
 san_pham_id BIGINT NOT NULL REFERENCES san_pham(id), gia_cu DECIMAL(18,2) NOT NULL,
 gia_moi DECIMAL(18,2) NOT NULL CHECK(gia_moi > 0), nguoi_thay_doi_id BIGINT NULL REFERENCES tai_khoan(id),
 thoi_diem_thay_doi DATETIME2 NOT NULL DEFAULT SYSDATETIME());
IF OBJECT_ID('dbo.chi_tiet_doi_tra','U') IS NULL
CREATE TABLE chi_tiet_doi_tra (id BIGINT IDENTITY PRIMARY KEY,
 phieu_doi_tra_id BIGINT NOT NULL REFERENCES phieu_doi_tra(id),
 chi_tiet_don_hang_id BIGINT NOT NULL REFERENCES chi_tiet_don_hang(id),
 lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id), so_luong INT NOT NULL CHECK(so_luong > 0),
 tien_hoan DECIMAL(18,2) NOT NULL CHECK(tien_hoan >= 0));
IF OBJECT_ID('dbo.gio_hang_treo','U') IS NULL
CREATE TABLE gio_hang_treo (id BIGINT IDENTITY PRIMARY KEY, ma_gio VARCHAR(40) NOT NULL UNIQUE,
 tai_khoan_id BIGINT NOT NULL REFERENCES tai_khoan(id), khach_hang_id BIGINT NULL REFERENCES khach_hang(id),
 tao_luc DATETIME2 NOT NULL DEFAULT SYSDATETIME());
IF OBJECT_ID('dbo.chi_tiet_gio_hang_treo','U') IS NULL
CREATE TABLE chi_tiet_gio_hang_treo (id BIGINT IDENTITY PRIMARY KEY,
 gio_id BIGINT NOT NULL REFERENCES gio_hang_treo(id), ma_san_pham VARCHAR(30) NOT NULL,
 ten NVARCHAR(180) NOT NULL, don_gia DECIMAL(18,2) NOT NULL CHECK(don_gia > 0),
 so_luong INT NOT NULL CHECK(so_luong > 0));
IF OBJECT_ID('dbo.chot_ngay','U') IS NULL
CREATE TABLE chot_ngay (id BIGINT IDENTITY PRIMARY KEY, ngay_chot DATE NOT NULL UNIQUE,
 nguoi_chot_id BIGINT NOT NULL REFERENCES tai_khoan(id), tong_doanh_thu DECIMAL(18,2) NOT NULL,
 thoi_diem_chot DATETIME2 NOT NULL DEFAULT SYSDATETIME(), trang_thai VARCHAR(20) NOT NULL DEFAULT 'LOCKED');
IF OBJECT_ID('dbo.bang_gia_hotel','U') IS NULL
CREATE TABLE bang_gia_hotel (id BIGINT IDENTITY PRIMARY KEY, phong_id BIGINT NOT NULL REFERENCES phong_hotel(id),
 khung_gio VARCHAR(20) NOT NULL DEFAULT 'DAILY', can_nang_tu DECIMAL(8,2) NOT NULL DEFAULT 0,
 can_nang_den DECIMAL(8,2) NOT NULL DEFAULT 999, don_gia DECIMAL(18,2) NOT NULL CHECK(don_gia > 0));
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='UX_phien_open' AND object_id=OBJECT_ID('phien_lam_viec'))
 CREATE UNIQUE INDEX UX_phien_open ON phien_lam_viec(tai_khoan_id) WHERE trang_thai='OPEN';
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name='IX_spa_pending' AND object_id=OBJECT_ID('phieu_tiep_nhan_spa'))
 CREATE INDEX IX_spa_pending ON phieu_tiep_nhan_spa(trang_thai,tao_luc);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_tai_khoan_username_status' AND object_id=OBJECT_ID('tai_khoan'))
 CREATE INDEX IX_tai_khoan_username_status ON tai_khoan(ten_dang_nhap, trang_thai);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_lich_ngay_ca' AND object_id=OBJECT_ID('lich_lam_viec'))
 CREATE INDEX IX_lich_ngay_ca ON lich_lam_viec(ngay_lam, ca_lam_viec_id);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_khach_hang_phone' AND object_id=OBJECT_ID('khach_hang'))
 CREATE INDEX IX_khach_hang_phone ON khach_hang(so_dien_thoai);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_san_pham_barcode' AND object_id=OBJECT_ID('san_pham'))
 CREATE INDEX IX_san_pham_barcode ON san_pham(barcode) WHERE barcode IS NOT NULL;
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_lo_hang_fefo' AND object_id=OBJECT_ID('lo_hang'))
 CREATE INDEX IX_lo_hang_fefo ON lo_hang(san_pham_id, han_su_dung, trang_thai) INCLUDE (so_luong_ton);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_don_hang_phien_trang_thai' AND object_id=OBJECT_ID('don_hang'))
 CREATE INDEX IX_don_hang_phien_trang_thai ON don_hang(phien_lam_viec_id, trang_thai);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_hoa_don_thoi_gian' AND object_id=OBJECT_ID('hoa_don'))
 CREATE INDEX IX_hoa_don_thoi_gian ON hoa_don(thanh_toan_luc DESC);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_lich_hen_thoi_gian' AND object_id=OBJECT_ID('lich_hen'))
 CREATE INDEX IX_lich_hen_thoi_gian ON lich_hen(bat_dau, ket_thuc, trang_thai);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_hotel_thoi_gian' AND object_id=OBJECT_ID('hop_dong_luu_tru'))
 CREATE INDEX IX_hotel_thoi_gian ON hop_dong_luu_tru(phong_id, ngay_vao, ngay_ra_du_kien, trang_thai);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_approval_status' AND object_id=OBJECT_ID('yeu_cau_phe_duyet'))
 CREATE INDEX IX_approval_status ON yeu_cau_phe_duyet(trang_thai, gui_luc);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_audit_time' AND object_id=OBJECT_ID('nhat_ky_he_thong'))
 CREATE INDEX IX_audit_time ON nhat_ky_he_thong(thoi_gian DESC);
GO
-- SQL Server permits only one NULL in a plain UNIQUE constraint. Barcodes are optional.
DECLARE @barcodeConstraint sysname;
SELECT @barcodeConstraint=kc.name FROM sys.key_constraints kc
JOIN sys.index_columns ic ON ic.object_id=kc.parent_object_id AND ic.index_id=kc.unique_index_id
JOIN sys.columns col ON col.object_id=ic.object_id AND col.column_id=ic.column_id
WHERE kc.parent_object_id=OBJECT_ID('san_pham') AND kc.type='UQ' AND col.name='barcode';
IF @barcodeConstraint IS NOT NULL
BEGIN
 DECLARE @barcodeSql nvarchar(500)=N'ALTER TABLE dbo.san_pham DROP CONSTRAINT '+QUOTENAME(@barcodeConstraint);
 EXEC(@barcodeSql);
END;
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='UX_san_pham_barcode' AND object_id=OBJECT_ID('san_pham'))
 CREATE UNIQUE INDEX UX_san_pham_barcode ON san_pham(barcode) WHERE barcode IS NOT NULL;
GO

-- DEMO_COMPLETION_V1: nâng cấp bổ sung; không đổi dữ liệu lịch sử.
IF COL_LENGTH('voucher','khoa_doi_diem') IS NULL
 ALTER TABLE voucher ADD khoa_doi_diem VARCHAR(80) NULL;
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='UX_voucher_doi_diem' AND object_id=OBJECT_ID('voucher'))
 CREATE UNIQUE INDEX UX_voucher_doi_diem ON voucher(khoa_doi_diem) WHERE khoa_doi_diem IS NOT NULL;
GO
IF COL_LENGTH('hop_dong_luu_tru','da_nhan_coc') IS NULL
 ALTER TABLE hop_dong_luu_tru ADD da_nhan_coc BIT NOT NULL CONSTRAINT DF_hotel_nhan_coc DEFAULT 0,
  nhan_coc_luc DATETIME2 NULL, nhan_coc_boi BIGINT NULL REFERENCES tai_khoan(id);
GO
IF COL_LENGTH('phieu_doi_tra','loai_phieu') IS NULL
 ALTER TABLE phieu_doi_tra ADD loai_phieu VARCHAR(20) NOT NULL CONSTRAINT DF_doi_tra_loai DEFAULT 'LEGACY_RETURN',
  hoa_don_thay_the_id BIGINT NULL REFERENCES hoa_don(id),
  gia_tri_duoc_doi BIGINT NULL, khoa_thao_tac VARCHAR(80) NULL;
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='UX_doi_hang_khoa' AND object_id=OBJECT_ID('phieu_doi_tra'))
 CREATE UNIQUE INDEX UX_doi_hang_khoa ON phieu_doi_tra(khoa_thao_tac) WHERE khoa_thao_tac IS NOT NULL;
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='IX_tiem_phong_thu_cung' AND object_id=OBJECT_ID('tiem_phong'))
 CREATE INDEX IX_tiem_phong_thu_cung ON tiem_phong(thu_cung_id,ngay_tiem DESC);
GO
IF COL_LENGTH('hop_dong_luu_tru','phien_nhan_coc_id') IS NULL
 ALTER TABLE hop_dong_luu_tru ADD phien_nhan_coc_id BIGINT NULL REFERENCES phien_lam_viec(id);
GO
IF NOT EXISTS(SELECT 1 FROM sys.indexes WHERE name='UX_doi_hang_hoa_don' AND object_id=OBJECT_ID('phieu_doi_tra'))
 CREATE UNIQUE INDEX UX_doi_hang_hoa_don ON phieu_doi_tra(hoa_don_thay_the_id) WHERE hoa_don_thay_the_id IS NOT NULL;
GO

-- REAL_CATALOG_V1: Thông tin sản phẩm thu thập; không đổi giá, tồn và lịch sử cũ.
IF COL_LENGTH('san_pham','thuong_hieu') IS NULL
 ALTER TABLE san_pham ADD thuong_hieu NVARCHAR(100) NULL;
GO
IF COL_LENGTH('san_pham','quy_cach') IS NULL
 ALTER TABLE san_pham ADD quy_cach NVARCHAR(100) NULL;
GO
IF COL_LENGTH('san_pham','doi_tuong_su_dung') IS NULL
 ALTER TABLE san_pham ADD doi_tuong_su_dung NVARCHAR(100) NULL;
GO
IF COL_LENGTH('san_pham','mo_ta') IS NULL
 ALTER TABLE san_pham ADD mo_ta NVARCHAR(1000) NULL;
GO
IF COL_LENGTH('san_pham','nguon_url') IS NULL
 ALTER TABLE san_pham ADD nguon_url NVARCHAR(1000) NULL;
GO
IF COL_LENGTH('san_pham','ngay_thu_thap') IS NULL
 ALTER TABLE san_pham ADD ngay_thu_thap DATE NULL;
GO

-- UI_FINISH_V1: hình thức kết ca có cấu trúc, để NULL cho lịch sử chưa xác định.
IF COL_LENGTH('dbo.phien_lam_viec','hinh_thuc_ban_giao') IS NULL
 ALTER TABLE dbo.phien_lam_viec ADD hinh_thuc_ban_giao VARCHAR(20) NULL
 CONSTRAINT CK_phien_hinh_thuc_ban_giao CHECK(hinh_thuc_ban_giao IN ('NOP_QUY','BAN_GIAO'));
GO
IF OBJECT_ID('dbo.doi_chieu_ma_du_lieu','U') IS NULL
 CREATE TABLE dbo.doi_chieu_ma_du_lieu(
 bang SYSNAME NOT NULL,cot SYSNAME NOT NULL,id_doi_tuong BIGINT NOT NULL,
 ma_cu NVARCHAR(100) NOT NULL,ma_moi NVARCHAR(100) NOT NULL,phien_ban VARCHAR(40) NOT NULL,
 CONSTRAINT PK_doi_chieu_ma_du_lieu PRIMARY KEY(bang,cot,id_doi_tuong));
GO

-- UI_UNICODE_SCHEMA_V1: tên hạng thành viên cần lưu được tiếng Việt.
IF EXISTS(SELECT 1 FROM sys.columns c JOIN sys.types t ON t.user_type_id=c.user_type_id WHERE c.object_id=OBJECT_ID('dbo.khach_hang') AND c.name='hang_thanh_vien' AND t.name='varchar')
 BEGIN
 DECLARE @tenMacDinh SYSNAME,@dinhNghia NVARCHAR(MAX),@lenh NVARCHAR(MAX);
 SELECT @tenMacDinh=d.name,@dinhNghia=d.definition FROM sys.default_constraints d JOIN sys.columns c ON c.object_id=d.parent_object_id AND c.column_id=d.parent_column_id WHERE c.object_id=OBJECT_ID('dbo.khach_hang') AND c.name='hang_thanh_vien';
 IF @tenMacDinh IS NOT NULL BEGIN SET @lenh=N'ALTER TABLE dbo.khach_hang DROP CONSTRAINT '+QUOTENAME(@tenMacDinh);EXEC sp_executesql @lenh;END;
 ALTER TABLE dbo.khach_hang ALTER COLUMN hang_thanh_vien NVARCHAR(20) NOT NULL;
 IF @tenMacDinh IS NOT NULL BEGIN SET @lenh=N'ALTER TABLE dbo.khach_hang ADD CONSTRAINT '+QUOTENAME(@tenMacDinh)+N' DEFAULT '+@dinhNghia+N' FOR hang_thanh_vien';EXEC sp_executesql @lenh;END;
 END;
IF EXISTS(SELECT 1 FROM sys.columns c JOIN sys.types t ON t.user_type_id=c.user_type_id WHERE c.object_id=OBJECT_ID('dbo.thu_cung') AND c.name='gioi_tinh' AND t.name='varchar')
 ALTER TABLE dbo.thu_cung ALTER COLUMN gioi_tinh NVARCHAR(10) NULL;
IF OBJECT_ID('dbo.doi_chieu_unicode','U') IS NULL
 CREATE TABLE doi_chieu_unicode(bang SYSNAME NOT NULL,cot SYSNAME NOT NULL,id_doi_tuong BIGINT NOT NULL,gia_tri_cu NVARCHAR(500) NOT NULL,gia_tri_moi NVARCHAR(500) NOT NULL,phien_ban VARCHAR(50) NOT NULL,sua_luc DATETIME2 NOT NULL DEFAULT SYSDATETIME(),PRIMARY KEY(bang,cot,id_doi_tuong,phien_ban));
GO
