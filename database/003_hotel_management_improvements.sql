/*
    PetStation - cải tiến quản lý lưu trú
    Chạy sau database/001_schema.sql trên database PetStationDemo1.
    Có thể chạy lại an toàn.
*/
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.lich_su_thu_coc_hotel', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.lich_su_thu_coc_hotel (
            id BIGINT IDENTITY(1,1) NOT NULL,
            hop_dong_id BIGINT NOT NULL,
            khoa_thu_coc VARCHAR(90) NOT NULL,
            phuong_thuc VARCHAR(30) NOT NULL,
            so_tien DECIMAL(18,2) NOT NULL,
            khach_dua DECIMAL(18,2) NOT NULL,
            tien_thua DECIMAL(18,2) NOT NULL CONSTRAINT DF_lich_su_thu_coc_tien_thua DEFAULT (0),
            phien_lam_viec_id BIGINT NULL,
            nguoi_thu_id BIGINT NULL,
            tao_luc DATETIME2(7) NOT NULL CONSTRAINT DF_lich_su_thu_coc_tao_luc DEFAULT (SYSDATETIME()),
            CONSTRAINT PK_lich_su_thu_coc_hotel PRIMARY KEY CLUSTERED (id),
            CONSTRAINT FK_lich_su_thu_coc_hotel_hop_dong FOREIGN KEY (hop_dong_id)
                REFERENCES dbo.hop_dong_luu_tru(id),
            CONSTRAINT FK_lich_su_thu_coc_hotel_phien FOREIGN KEY (phien_lam_viec_id)
                REFERENCES dbo.phien_lam_viec(id),
            CONSTRAINT FK_lich_su_thu_coc_hotel_nguoi_thu FOREIGN KEY (nguoi_thu_id)
                REFERENCES dbo.tai_khoan(id),
            CONSTRAINT CK_lich_su_thu_coc_hotel_so_tien CHECK (so_tien >= 0),
            CONSTRAINT CK_lich_su_thu_coc_hotel_khach_dua CHECK (khach_dua >= so_tien),
            CONSTRAINT CK_lich_su_thu_coc_hotel_tien_thua CHECK (tien_thua >= 0),
            CONSTRAINT UQ_lich_su_thu_coc_hotel_khoa UNIQUE (hop_dong_id, khoa_thu_coc, phuong_thuc)
        );
    END;

    IF NOT EXISTS (
        SELECT 1 FROM sys.indexes
        WHERE object_id = OBJECT_ID(N'dbo.lich_su_thu_coc_hotel')
          AND name = N'IX_lich_su_thu_coc_hotel_hop_dong'
    )
        CREATE INDEX IX_lich_su_thu_coc_hotel_hop_dong
            ON dbo.lich_su_thu_coc_hotel(hop_dong_id, tao_luc DESC);

    /*
       Chuyển dữ liệu cọc cũ thành một dòng lịch sử tổng hợp.
       Database cũ không lưu chi tiết từng lần thu nên không thể khôi phục
       chính xác khách đưa/tiền thừa của các giao dịch trước migration.
    */
    INSERT INTO dbo.lich_su_thu_coc_hotel
        (hop_dong_id, khoa_thu_coc, phuong_thuc, so_tien, khach_dua,
         tien_thua, phien_lam_viec_id, nguoi_thu_id, tao_luc)
    SELECT
        h.id,
        COALESCE(NULLIF(h.khoa_thu_coc, ''), CONCAT('LEGACY-', h.id)),
        COALESCE(NULLIF(h.phuong_thuc_coc, ''), 'TIEN_MAT'),
        h.tien_coc,
        h.tien_coc,
        0,
        h.phien_nhan_coc_id,
        h.nhan_coc_boi,
        COALESCE(h.nhan_coc_luc, SYSDATETIME())
    FROM dbo.hop_dong_luu_tru h
    WHERE h.da_nhan_coc = 1
      AND NOT EXISTS (
          SELECT 1 FROM dbo.lich_su_thu_coc_hotel l WHERE l.hop_dong_id = h.id
      );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
