/* Nháp lập phiếu Spa. Có thể chạy lại, không thay đổi phiếu tiếp nhận chính thức. */
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;
    IF OBJECT_ID(N'dbo.phieu_spa_treo', N'U') IS NULL
    BEGIN
        CREATE TABLE dbo.phieu_spa_treo (
            id BIGINT IDENTITY(1,1) PRIMARY KEY,
            ma_phieu VARCHAR(30) NOT NULL UNIQUE,
            nguoi_lap_id BIGINT NOT NULL REFERENCES dbo.tai_khoan(id),
            ma_khach VARCHAR(30) NOT NULL,
            so_dien_thoai NVARCHAR(100) NOT NULL,
            ma_thu VARCHAR(30) NOT NULL,
            ma_goi VARCHAR(30) NOT NULL,
            ma_hen VARCHAR(30) NOT NULL,
            can_nang NVARCHAR(100) NOT NULL,
            tinh_trang NVARCHAR(MAX) NOT NULL,
            yeu_cau NVARCHAR(MAX) NOT NULL,
            cap_nhat_luc DATETIME2 NOT NULL DEFAULT SYSDATETIME()
        );
    END;
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
