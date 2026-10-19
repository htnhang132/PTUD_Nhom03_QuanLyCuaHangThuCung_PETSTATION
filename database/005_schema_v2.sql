-- =====================================================
-- Pet Station - Database Schema v2
-- File: 005_schema_v2.sql
-- Purpose: Add missing tables for Spa pricing, Hotel pricing, 
--          Settings, Price history, Day lock, and modify existing tables
-- =====================================================

USE PetStationDB;
GO

-- =====================================================
-- 1. GOI_DICH_VU_SPA: Spa Service Packages
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'goi_dich_vu_spa')
BEGIN
    CREATE TABLE goi_dich_vu_spa (
        goi_id BIGINT PRIMARY KEY IDENTITY(1,1),
        ma_goi VARCHAR(50) NOT NULL UNIQUE,
        ten_goi NVARCHAR(100) NOT NULL,
        mo_ta NVARCHAR(500),
        loai_goi VARCHAR(20) NOT NULL CHECK (loai_goi IN ('COMBO', 'SINGLE')),
        -- COMBO: Silver, Gold, Platinum, Diamond
        -- SINGLE: Individual services (bath, trim, etc.)
        trang_thai VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (trang_thai IN ('ACTIVE', 'INACTIVE')),
        ngay_tao DATETIME2 NOT NULL DEFAULT GETDATE(),
        rowversion ROWVERSION,
        CONSTRAINT CHK_goi_dich_vu_spa_ma_goi CHECK (LEN(ma_goi) >= 2)
    );
    
    CREATE INDEX IDX_goi_dich_vu_spa_loai ON goi_dich_vu_spa(loai_goi);
    CREATE INDEX IDX_goi_dich_vu_spa_trang_thai ON goi_dich_vu_spa(trang_thai);
END
GO

-- =====================================================
-- 2. BANG_GIA_SPA: Spa Pricing Matrix
--    Price = Species × Weight Range × Package + Surcharge
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'bang_gia_spa')
BEGIN
    CREATE TABLE bang_gia_spa (
        gia_id BIGINT PRIMARY KEY IDENTITY(1,1),
        goi_id BIGINT NOT NULL,
        loai_thu_cung VARCHAR(10) NOT NULL CHECK (loai_thu_cung IN ('DOG', 'CAT')),
        can_nang_tu DECIMAL(5,2) NOT NULL CHECK (can_nang_tu >= 0),
        can_nang_den DECIMAL(5,2) NOT NULL CHECK (can_nang_den > can_nang_tu),
        don_gia DECIMAL(12,2) NOT NULL CHECK (don_gia > 0),
        thoi_gian_du_kien INT, -- minutes
        ngay_ap_dung DATETIME2 NOT NULL DEFAULT GETDATE(),
        ngay_het_hieu_luc DATETIME2,
        rowversion ROWVERSION,
        CONSTRAINT FK_bang_gia_spa_goi FOREIGN KEY (goi_id) 
            REFERENCES goi_dich_vu_spa(goi_id),
        CONSTRAINT CHK_bang_gia_spa_can_nang CHECK (can_nang_den > can_nang_tu),
        CONSTRAINT CHK_bang_gia_spa_thoi_gian CHECK (thoi_gian_du_kien > 0 OR thoi_gian_du_kien IS NULL)
    );
    
    CREATE INDEX IDX_bang_gia_spa_goi ON bang_gia_spa(goi_id);
    CREATE INDEX IDX_bang_gia_spa_loai ON bang_gia_spa(loai_thu_cung);
    CREATE INDEX IDX_bang_gia_spa_can_nang ON bang_gia_spa(can_nang_tu, can_nang_den);
END
GO

-- =====================================================
-- 3. CHI_TIET_PHIEU_SPA: Spa Ticket Details
--    For individual services and surcharges
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'chi_tiet_phieu_spa')
BEGIN
    CREATE TABLE chi_tiet_phieu_spa (
        chi_tiet_id BIGINT PRIMARY KEY IDENTITY(1,1),
        phieu_spa_id BIGINT NOT NULL,
        goi_id BIGINT, -- NULL for surcharges
        loai_chi_tiet VARCHAR(20) NOT NULL CHECK (loai_chi_tiet IN ('SERVICE', 'SURCHARGE')),
        mo_ta NVARCHAR(200) NOT NULL,
        don_gia DECIMAL(12,2) NOT NULL CHECK (don_gia >= 0),
        so_luong INT NOT NULL DEFAULT 1 CHECK (so_luong > 0),
        thanh_tien AS (don_gia * so_luong) PERSISTED,
        ghi_chu NVARCHAR(500),
        rowversion ROWVERSION,
        CONSTRAINT FK_chi_tiet_phieu_spa_phieu FOREIGN KEY (phieu_spa_id) 
            REFERENCES phieu_tiep_nhan_spa(phieu_id),
        CONSTRAINT FK_chi_tiet_phieu_spa_goi FOREIGN KEY (goi_id) 
            REFERENCES goi_dich_vu_spa(goi_id)
    );
    
    CREATE INDEX IDX_chi_tiet_phieu_spa_phieu ON chi_tiet_phieu_spa(phieu_spa_id);
    CREATE INDEX IDX_chi_tiet_phieu_spa_loai ON chi_tiet_phieu_spa(loai_chi_tiet);
END
GO

-- =====================================================
-- 4. BANG_GIA_HOTEL: Hotel Room Pricing
--    Price by time frame (hourly, daily, overnight) and weight
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'bang_gia_hotel')
BEGIN
    CREATE TABLE bang_gia_hotel (
        gia_id BIGINT PRIMARY KEY IDENTITY(1,1),
        phong_id BIGINT NOT NULL,
        khung_gio VARCHAR(20) NOT NULL CHECK (khung_gio IN ('HOURLY', 'DAILY', 'OVERNIGHT')),
        can_nang_tu DECIMAL(5,2) NOT NULL CHECK (can_nang_tu >= 0),
        can_nang_den DECIMAL(5,2) NOT NULL CHECK (can_nang_den > can_nang_tu),
        don_gia DECIMAL(12,2) NOT NULL CHECK (don_gia > 0),
        ngay_ap_dung DATETIME2 NOT NULL DEFAULT GETDATE(),
        ngay_het_hieu_luc DATETIME2,
        rowversion ROWVERSION,
        CONSTRAINT FK_bang_gia_hotel_phong FOREIGN KEY (phong_id) 
            REFERENCES phong_hotel(phong_id),
        CONSTRAINT CHK_bang_gia_hotel_can_nang CHECK (can_nang_den > can_nang_tu)
    );
    
    CREATE INDEX IDX_bang_gia_hotel_phong ON bang_gia_hotel(phong_id);
    CREATE INDEX IDX_bang_gia_hotel_khung ON bang_gia_hotel(khung_gio);
    CREATE INDEX IDX_bang_gia_hotel_can_nang ON bang_gia_hotel(can_nang_tu, can_nang_den);
END
GO

-- =====================================================
-- 5. CAI_DAT: System Settings
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'cai_dat')
BEGIN
    CREATE TABLE cai_dat (
        cai_dat_id BIGINT PRIMARY KEY IDENTITY(1,1),
        khoa VARCHAR(100) NOT NULL UNIQUE,
        gia_tri NVARCHAR(500) NOT NULL,
        kieu_du_lieu VARCHAR(20) NOT NULL DEFAULT 'STRING' 
            CHECK (kieu_du_lieu IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'DATE')),
        nhom VARCHAR(50) NOT NULL,
        mo_ta NVARCHAR(500),
        nguoi_cap_nhat_id BIGINT,
        ngay_cap_nhat DATETIME2 NOT NULL DEFAULT GETDATE(),
        rowversion ROWVERSION,
        CONSTRAINT FK_cai_dat_nguoi_cap_nhat FOREIGN KEY (nguoi_cap_nhat_id) 
            REFERENCES tai_khoan(tai_khoan_id)
    );
    
    CREATE INDEX IDX_cai_dat_khoa ON cai_dat(khoa);
    CREATE INDEX IDX_cai_dat_nhom ON cai_dat(nhom);
END
GO

-- =====================================================
-- 6. LICH_SU_GIA: Price History
--    Track all price changes for products
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'lich_su_gia')
BEGIN
    CREATE TABLE lich_su_gia (
        lich_su_id BIGINT PRIMARY KEY IDENTITY(1,1),
        san_pham_id BIGINT NOT NULL,
        gia_cu DECIMAL(12,2) NOT NULL CHECK (gia_cu >= 0),
        gia_moi DECIMAL(12,2) NOT NULL CHECK (gia_moi >= 0),
        ly_do NVARCHAR(500),
        nguoi_thay_doi_id BIGINT NOT NULL,
        thoi_diem_thay_doi DATETIME2 NOT NULL DEFAULT GETDATE(),
        loai_thay_doi VARCHAR(20) NOT NULL DEFAULT 'MANUAL' 
            CHECK (loai_thay_doi IN ('MANUAL', 'PROMOTION', 'EXPIRY_DISCOUNT', 'COST_ADJUSTMENT')),
        rowversion ROWVERSION,
        CONSTRAINT FK_lich_su_gia_san_pham FOREIGN KEY (san_pham_id) 
            REFERENCES san_pham(san_pham_id),
        CONSTRAINT FK_lich_su_gia_nguoi_thay_doi FOREIGN KEY (nguoi_thay_doi_id) 
            REFERENCES tai_khoan(tai_khoan_id)
    );
    
    CREATE INDEX IDX_lich_su_gia_san_pham ON lich_su_gia(san_pham_id);
    CREATE INDEX IDX_lich_su_gia_thoi_diem ON lich_su_gia(thoi_diem_thay_doi DESC);
    CREATE INDEX IDX_lich_su_gia_nguoi ON lich_su_gia(nguoi_thay_doi_id);
END
GO

-- =====================================================
-- 7. CHOT_NGAY: Day Lock
--    Lock transactions after day-end reconciliation
-- =====================================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'chot_ngay')
BEGIN
    CREATE TABLE chot_ngay (
        chot_id BIGINT PRIMARY KEY IDENTITY(1,1),
        ngay_chot DATE NOT NULL UNIQUE,
        nguoi_chot_id BIGINT NOT NULL,
        thoi_diem_chot DATETIME2 NOT NULL DEFAULT GETDATE(),
        tong_doanh_thu DECIMAL(15,2) NOT NULL CHECK (tong_doanh_thu >= 0),
        tong_tien_mat DECIMAL(15,2) NOT NULL CHECK (tong_tien_mat >= 0),
        tong_chuyen_khoan DECIMAL(15,2) NOT NULL CHECK (tong_chuyen_khoan >= 0),
        tong_the DECIMAL(15,2) NOT NULL CHECK (tong_the >= 0),
        so_hoa_don INT NOT NULL CHECK (so_hoa_don >= 0),
        ghi_chu NVARCHAR(1000),
        trang_thai VARCHAR(20) NOT NULL DEFAULT 'LOCKED' CHECK (trang_thai IN ('LOCKED', 'REOPENED')),
        rowversion ROWVERSION,
        CONSTRAINT FK_chot_ngay_nguoi_chot FOREIGN KEY (nguoi_chot_id) 
            REFERENCES tai_khoan(tai_khoan_id)
    );
    
    CREATE INDEX IDX_chot_ngay_ngay ON chot_ngay(ngay_chot DESC);
    CREATE INDEX IDX_chot_ngay_trang_thai ON chot_ngay(trang_thai);
END
GO

-- =====================================================
-- 8. Modify existing tables
-- =====================================================

-- 8.1 Add columns to VOUCHER table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('voucher') AND name = 'khach_hang_id')
BEGIN
    ALTER TABLE voucher ADD khach_hang_id BIGINT NULL;
    ALTER TABLE voucher ADD CONSTRAINT FK_voucher_khach_hang 
        FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(khach_hang_id);
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('voucher') AND name = 'loai_voucher')
BEGIN
    ALTER TABLE voucher ADD loai_voucher VARCHAR(20) NOT NULL DEFAULT 'CAMPAIGN' 
        CHECK (loai_voucher IN ('CAMPAIGN', 'LOYALTY_REWARD'));
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('voucher') AND name = 'chi_ap_dung_spa')
BEGIN
    ALTER TABLE voucher ADD chi_ap_dung_spa BIT NOT NULL DEFAULT 0;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('voucher') AND name = 'ngay_doi_diem')
BEGIN
    ALTER TABLE voucher ADD ngay_doi_diem DATETIME2 NULL;
END
GO

-- 8.2 Add columns to THU_CUNG table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('thu_cung') AND name = 'ngay_sinh')
BEGIN
    ALTER TABLE thu_cung ADD ngay_sinh DATE NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('thu_cung') AND name = 'mau_long')
BEGIN
    ALTER TABLE thu_cung ADD mau_long NVARCHAR(50) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('thu_cung') AND name = 'tinh_cach')
BEGIN
    ALTER TABLE thu_cung ADD tinh_cach NVARCHAR(200) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('thu_cung') AND name = 'hinh_anh_url')
BEGIN
    ALTER TABLE thu_cung ADD hinh_anh_url NVARCHAR(500) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('thu_cung') AND name = 'di_ung')
BEGIN
    ALTER TABLE thu_cung ADD di_ung NVARCHAR(500) NULL;
END
GO

-- 8.3 Add columns to HOA_DON table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('hoa_don') AND name = 'phien_lam_viec_id')
BEGIN
    ALTER TABLE hoa_don ADD phien_lam_viec_id BIGINT NULL;
    ALTER TABLE hoa_don ADD CONSTRAINT FK_hoa_don_phien 
        FOREIGN KEY (phien_lam_viec_id) REFERENCES phien_lam_viec(phien_id);
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('hoa_don') AND name = 'tien_diem')
BEGIN
    ALTER TABLE hoa_don ADD tien_diem DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (tien_diem >= 0);
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('hoa_don') AND name = 'tien_khach_dua')
BEGIN
    ALTER TABLE hoa_don ADD tien_khach_dua DECIMAL(12,2) NULL CHECK (tien_khach_dua >= 0);
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('hoa_don') AND name = 'tien_thua')
BEGIN
    ALTER TABLE hoa_don ADD tien_thua AS (
        CASE 
            WHEN tien_khach_dua IS NOT NULL AND tien_khach_dua > tong_tien 
            THEN tien_khach_dua - tong_tien 
            ELSE 0 
        END
    ) PERSISTED;
END
GO

-- 8.4 Add columns to PHIEN_LAM_VIEC table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('phien_lam_viec') AND name = 'ca_lam_viec_id')
BEGIN
    ALTER TABLE phien_lam_viec ADD ca_lam_viec_id BIGINT NULL;
    ALTER TABLE phien_lam_viec ADD CONSTRAINT FK_phien_lam_viec_ca 
        FOREIGN KEY (ca_lam_viec_id) REFERENCES ca_lam_viec(ca_id);
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('phien_lam_viec') AND name = 'chenh_lech')
BEGIN
    ALTER TABLE phien_lam_viec ADD chenh_lech DECIMAL(12,2) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('phien_lam_viec') AND name = 'giai_trinh')
BEGIN
    ALTER TABLE phien_lam_viec ADD giai_trinh NVARCHAR(1000) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('phien_lam_viec') AND name = 'nguoi_nhan_ban_giao_id')
BEGIN
    ALTER TABLE phien_lam_viec ADD nguoi_nhan_ban_giao_id BIGINT NULL;
    ALTER TABLE phien_lam_viec ADD CONSTRAINT FK_phien_nguoi_nhan 
        FOREIGN KEY (nguoi_nhan_ban_giao_id) REFERENCES tai_khoan(tai_khoan_id);
END
GO

-- 8.5 Add columns to YEU_CAU_PHE_DUYET table
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('yeu_cau_phe_duyet') AND name = 'noi_dung_truoc')
BEGIN
    ALTER TABLE yeu_cau_phe_duyet ADD noi_dung_truoc NVARCHAR(MAX) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('yeu_cau_phe_duyet') AND name = 'noi_dung_sau')
BEGIN
    ALTER TABLE yeu_cau_phe_duyet ADD noi_dung_sau NVARCHAR(MAX) NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('yeu_cau_phe_duyet') AND name = 'nguoi_duyet_id')
BEGIN
    ALTER TABLE yeu_cau_phe_duyet ADD nguoi_duyet_id BIGINT NULL;
    ALTER TABLE yeu_cau_phe_duyet ADD CONSTRAINT FK_yeu_cau_nguoi_duyet 
        FOREIGN KEY (nguoi_duyet_id) REFERENCES tai_khoan(tai_khoan_id);
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('yeu_cau_phe_duyet') AND name = 'ngay_duyet')
BEGIN
    ALTER TABLE yeu_cau_phe_duyet ADD ngay_duyet DATETIME2 NULL;
END
GO

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('yeu_cau_phe_duyet') AND name = 'ly_do_tu_choi')
BEGIN
    ALTER TABLE yeu_cau_phe_duyet ADD ly_do_tu_choi NVARCHAR(1000) NULL;
END
GO

-- =====================================================
-- 9. Insert default settings
-- =====================================================
IF NOT EXISTS (SELECT * FROM cai_dat WHERE khoa = 'VAT_RATE')
BEGIN
    INSERT INTO cai_dat (khoa, gia_tri, kieu_du_lieu, nhom, mo_ta)
    VALUES 
        ('VAT_RATE', '10', 'DECIMAL', 'INVOICE', N'Thuế VAT (%)'),
        ('MIN_STOCK_THRESHOLD', '10', 'INTEGER', 'INVENTORY', N'Ngưỡng tồn tối thiểu'),
        ('EXPIRY_WARNING_90', '90', 'INTEGER', 'INVENTORY', N'Cảnh báo cận hạn 90 ngày'),
        ('EXPIRY_WARNING_60', '60', 'INTEGER', 'INVENTORY', N'Cảnh báo cận hạn 60 ngày'),
        ('EXPIRY_WARNING_45', '45', 'INTEGER', 'INVENTORY', N'Cảnh báo cận hạn 45 ngày'),
        ('EXPIRY_WARNING_30', '30', 'INTEGER', 'INVENTORY', N'Cảnh báo cận hạn 30 ngày'),
        ('DISCOUNT_90_DAYS', '10', 'DECIMAL', 'INVENTORY', N'Giảm giá hàng cận hạn 90 ngày (%)'),
        ('DISCOUNT_60_DAYS', '20', 'DECIMAL', 'INVENTORY', N'Giảm giá hàng cận hạn 60 ngày (%)'),
        ('DISCOUNT_45_DAYS', '30', 'DECIMAL', 'INVENTORY', N'Giảm giá hàng cận hạn 45 ngày (%)'),
        ('DISCOUNT_30_DAYS', '40', 'DECIMAL', 'INVENTORY', N'Giảm giá hàng cận hạn dưới 30 ngày (%)'),
        ('LATE_CHECKOUT_FEE', '50000', 'DECIMAL', 'HOTEL', N'Phí trả phòng trễ mỗi ngày (VNĐ)'),
        ('RETURN_PERIOD_DAYS', '3', 'INTEGER', 'RETURN', N'Thời hạn đổi trả (ngày)'),
        ('WRITEOFF_APPROVAL_THRESHOLD', '5000000', 'DECIMAL', 'INVENTORY', N'Ngưỡng xuất hủy cần duyệt (VNĐ)'),
        ('POINTS_PER_10K', '1', 'INTEGER', 'LOYALTY', N'Số điểm cho 10,000 VNĐ'),
        ('VOUCHER_POINTS_REQUIRED', '200', 'INTEGER', 'LOYALTY', N'Số điểm đổi voucher'),
        ('VOUCHER_VALUE', '150000', 'DECIMAL', 'LOYALTY', N'Giá trị voucher (VNĐ)'),
        ('VOUCHER_VALIDITY_DAYS', '60', 'INTEGER', 'LOYALTY', N'Hạn sử dụng voucher (ngày)'),
        ('HOTEL_DEPOSIT_PERCENT', '50', 'DECIMAL', 'HOTEL', N'Tỷ lệ đặt cọc Hotel (%)'),
        ('HOTEL_OVERDUE_DAYS', '7', 'INTEGER', 'HOTEL', N'Số ngày quá hạn xử lý vi phạm');
END
GO

-- =====================================================
-- 10. Insert default Spa packages
-- =====================================================
IF NOT EXISTS (SELECT * FROM goi_dich_vu_spa WHERE ma_goi = 'SILVER')
BEGIN
    INSERT INTO goi_dich_vu_spa (ma_goi, ten_goi, mo_ta, loai_goi, trang_thai)
    VALUES 
        ('SILVER', N'Gói Silver', N'Tắm vệ sinh cơ bản + Cắt móng + Vệ sinh tai mắt', 'COMBO', 'ACTIVE'),
        ('GOLD', N'Gói Gold', N'Gói Silver + Cạo lông + Massage thư giãn', 'COMBO', 'ACTIVE'),
        ('PLATINUM', N'Gói Platinum', N'Gói Gold + Cắt tỉa tạo kiểu + Spa thảo mộc', 'COMBO', 'ACTIVE'),
        ('DIAMOND', N'Gói Diamond', N'Gói Platinum + Nhuộm lông + Chăm sóc da cao cấp', 'COMBO', 'ACTIVE'),
        ('BATH_ONLY', N'Tắm vệ sinh đơn', N'Chỉ tắm và vệ sinh cơ bản', 'SINGLE', 'ACTIVE'),
        ('TRIM_ONLY', N'Cắt tỉa lông', N'Cắt tỉa tạo kiểu theo yêu cầu', 'SINGLE', 'ACTIVE'),
        ('NAIL_TRIM', N'Cắt móng', N'Cắt và mài móng', 'SINGLE', 'ACTIVE');
END
GO

PRINT 'Schema v2 migration completed successfully!';
GO
