/*
 Pet Station - SQL Server 2019+
 Chạy trong database PetStationDemo2 với collation Vietnamese_CI_AS.
*/
SET XACT_ABORT ON;
GO

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

CREATE TABLE quyen (
    ma_quyen VARCHAR(60) PRIMARY KEY,
    ten_quyen NVARCHAR(160) NOT NULL
);
GO

CREATE TABLE vai_tro_quyen (
    vai_tro VARCHAR(40) NOT NULL,
    ma_quyen VARCHAR(60) NOT NULL REFERENCES quyen(ma_quyen),
    CONSTRAINT PK_vai_tro_quyen PRIMARY KEY (vai_tro, ma_quyen),
    CONSTRAINT CK_vai_tro_quyen_vai_tro CHECK (
        vai_tro IN ('STORE_MANAGER','WAREHOUSE_STAFF','SALES_STAFF','SPA_HOTEL_STAFF')
    )
);
GO

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

CREATE TABLE ca_lam_viec (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_ca VARCHAR(20) NOT NULL UNIQUE,
    ten_ca NVARCHAR(60) NOT NULL,
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    so_nhan_su_toi_thieu INT NOT NULL DEFAULT 1 CHECK (so_nhan_su_toi_thieu > 0)
);

CREATE TABLE lich_lam_viec (
    id BIGINT IDENTITY PRIMARY KEY,
    nhan_vien_id BIGINT NOT NULL REFERENCES nhan_vien(id),
    ca_lam_viec_id BIGINT NOT NULL REFERENCES ca_lam_viec(id),
    ngay_lam DATE NOT NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    CONSTRAINT UQ_lich_lam_viec UNIQUE (nhan_vien_id, ngay_lam, ca_lam_viec_id),
    CONSTRAINT CK_lich_trang_thai CHECK (trang_thai IN ('OPEN','LOCKED','CANCELLED'))
);

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

CREATE TABLE khach_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_khach_hang VARCHAR(20) NOT NULL UNIQUE,
    ho_ten NVARCHAR(120) NOT NULL,
    so_dien_thoai VARCHAR(15) NOT NULL UNIQUE,
    dia_chi NVARCHAR(300) NULL,
    ngay_sinh DATE NULL,
    diem_tich_luy INT NOT NULL DEFAULT 0 CHECK (diem_tich_luy >= 0),
    hang_thanh_vien VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    dang_hoat_dong BIT NOT NULL DEFAULT 1,
    row_version ROWVERSION
);

CREATE TABLE thu_cung (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_thu_cung VARCHAR(20) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    ten NVARCHAR(80) NOT NULL,
    loai VARCHAR(10) NOT NULL CHECK (loai IN ('DOG','CAT')),
    giong NVARCHAR(100) NULL,
    gioi_tinh VARCHAR(10) NULL,
    mau_long NVARCHAR(80) NULL,
    can_nang DECIMAL(8,2) NULL CHECK (can_nang > 0),
    tinh_trang_suc_khoe NVARCHAR(1000) NULL,
    di_ung_benh_ly NVARCHAR(1000) NULL,
    anh NVARCHAR(500) NULL,
    dang_theo_doi BIT NOT NULL DEFAULT 1
);

CREATE TABLE tiem_phong (
    id BIGINT IDENTITY PRIMARY KEY,
    thu_cung_id BIGINT NOT NULL REFERENCES thu_cung(id),
    ten_vaccine NVARCHAR(120) NOT NULL,
    ngay_tiem DATE NOT NULL,
    ngay_het_han DATE NULL,
    ghi_chu NVARCHAR(500) NULL
);

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

CREATE TABLE san_pham (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_san_pham VARCHAR(30) NOT NULL UNIQUE,
    barcode VARCHAR(80) NULL UNIQUE,
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

CREATE TABLE chi_tiet_phieu_nhap (
    id BIGINT IDENTITY PRIMARY KEY,
    phieu_nhap_id BIGINT NOT NULL REFERENCES phieu_nhap_kho(id),
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    so_lo VARCHAR(60) NOT NULL,
    han_su_dung DATE NULL,
    so_luong INT NOT NULL CHECK (so_luong > 0),
    gia_nhap DECIMAL(18,2) NOT NULL CHECK (gia_nhap > 0)
);

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

CREATE TABLE chi_tiet_phieu_xuat (
    id BIGINT IDENTITY PRIMARY KEY,
    phieu_xuat_id BIGINT NOT NULL REFERENCES phieu_xuat_kho(id),
    lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    gia_tri DECIMAL(18,2) NOT NULL CHECK (gia_tri >= 0)
);

CREATE TABLE bien_ban_kiem_ke (
    id BIGINT IDENTITY PRIMARY KEY,
    so_bien_ban VARCHAR(30) NOT NULL UNIQUE,
    nguoi_lap_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    ky_kiem_ke VARCHAR(30) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'COUNTING',
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE chi_tiet_kiem_ke (
    id BIGINT IDENTITY PRIMARY KEY,
    bien_ban_id BIGINT NOT NULL REFERENCES bien_ban_kiem_ke(id),
    lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id),
    ton_he_thong INT NOT NULL,
    ton_thuc_te INT NOT NULL,
    nguyen_nhan NVARCHAR(1000) NULL
);

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

CREATE TABLE de_xuat_gia (
    id BIGINT IDENTITY PRIMARY KEY,
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    nguoi_de_xuat_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    gia_hien_tai DECIMAL(18,2) NOT NULL,
    gia_de_xuat DECIMAL(18,2) NOT NULL CHECK (gia_de_xuat > 0),
    ly_do NVARCHAR(500) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE don_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_don VARCHAR(30) NOT NULL UNIQUE,
    phien_lam_viec_id BIGINT NOT NULL REFERENCES phien_lam_viec(id),
    khach_hang_id BIGINT NULL REFERENCES khach_hang(id),
    trang_thai VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE chi_tiet_don_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    don_hang_id BIGINT NOT NULL REFERENCES don_hang(id),
    san_pham_id BIGINT NOT NULL REFERENCES san_pham(id),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    don_gia DECIMAL(18,2) NOT NULL CHECK (don_gia > 0),
    giam_gia DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (giam_gia >= 0)
);

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

CREATE TABLE phan_bo_lo_hang (
    id BIGINT IDENTITY PRIMARY KEY,
    chi_tiet_don_hang_id BIGINT NOT NULL REFERENCES chi_tiet_don_hang(id),
    lo_hang_id BIGINT NOT NULL REFERENCES lo_hang(id),
    so_luong INT NOT NULL CHECK (so_luong > 0)
);

CREATE TABLE lich_su_diem (
    id BIGINT IDENTITY PRIMARY KEY,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    hoa_don_id BIGINT NULL REFERENCES hoa_don(id),
    thay_doi INT NOT NULL,
    so_du_sau INT NOT NULL CHECK (so_du_sau >= 0),
    ly_do NVARCHAR(300) NOT NULL,
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE voucher (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_voucher VARCHAR(40) NOT NULL UNIQUE,
    khach_hang_id BIGINT NOT NULL REFERENCES khach_hang(id),
    menh_gia DECIMAL(18,2) NOT NULL CHECK (menh_gia > 0),
    hieu_luc_den DATETIME2 NOT NULL,
    su_dung_luc DATETIME2 NULL,
    chi_ap_dung_spa BIT NOT NULL DEFAULT 1
);

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

CREATE TABLE phong_hotel (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_phong VARCHAR(20) NOT NULL UNIQUE,
    loai_phong NVARCHAR(80) NOT NULL,
    gia_dem DECIMAL(18,2) NOT NULL CHECK (gia_dem > 0),
    trang_thai VARCHAR(30) NOT NULL,
    row_version ROWVERSION
);

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

CREATE TABLE phieu_doi_tra (
    id BIGINT IDENTITY PRIMARY KEY,
    ma_phieu VARCHAR(30) NOT NULL UNIQUE,
    hoa_don_id BIGINT NOT NULL REFERENCES hoa_don(id),
    nguoi_lap_id BIGINT NOT NULL REFERENCES tai_khoan(id),
    chenh_lech DECIMAL(18,2) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    tao_luc DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

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
