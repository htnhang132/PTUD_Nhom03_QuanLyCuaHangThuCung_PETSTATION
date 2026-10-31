--Chạy khúc này thoi
USE [master];
GO

IF DB_ID(N'PetStationDemo1') IS NULL
BEGIN
    CREATE DATABASE [PetStationDemo1]
        COLLATE Vietnamese_CI_AS;
END;
GO

SELECT
    name AS DatabaseName,
    collation_name AS Collation
FROM sys.databases
WHERE name = N'PetStationDemo1';
GO
-------------------------------------------------
USE [PetStationDemo1];
GO

SELECT
    LTRIM(RTRIM(barcode)) AS barcode,
    COUNT(*) AS so_luong
FROM dbo.san_pham
WHERE NULLIF(LTRIM(RTRIM(barcode)), '') IS NOT NULL
GROUP BY LTRIM(RTRIM(barcode))
HAVING COUNT(*) > 1;

UPDATE dbo.san_pham
SET barcode = NULLIF(LTRIM(RTRIM(barcode)), '');

CREATE UNIQUE INDEX UX_san_pham_barcode
ON dbo.san_pham(barcode)
WHERE barcode IS NOT NULL;

USE PetStationDemo1;
GO

-- 1. Chuẩn hóa barcode: bỏ khoảng trắng, chuỗi rỗng chuyển thành NULL.
UPDATE dbo.san_pham
SET barcode = NULLIF(LTRIM(RTRIM(barcode)), '')
WHERE barcode IS NOT NULL;
GO

-- 2. Tạo chỉ mục duy nhất nếu chưa tồn tại.
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'UX_san_pham_barcode'
      AND object_id = OBJECT_ID('dbo.san_pham')
)
BEGIN
    CREATE UNIQUE INDEX UX_san_pham_barcode
    ON dbo.san_pham(barcode)
    WHERE barcode IS NOT NULL;
END;
GO

SELECT
    name,
    is_unique,
    has_filter,
    filter_definition
FROM sys.indexes
WHERE object_id = OBJECT_ID('dbo.san_pham')
  AND name = 'UX_san_pham_barcode';


  -- Xác nhận database đang kiểm tra
SELECT DB_NAME() AS DatabaseDangKetNoi;
GO

-- Kiểm tra trigger liên quan đến đơn nháp
SELECT
    OBJECT_SCHEMA_NAME(t.parent_id) AS SchemaName,
    OBJECT_NAME(t.parent_id) AS TableName,
    t.name AS TriggerName,
    m.definition AS TriggerDefinition
FROM sys.triggers t
JOIN sys.sql_modules m
    ON t.object_id = m.object_id
WHERE OBJECT_NAME(t.parent_id) IN (
    'gio_hang_treo',
    'chi_tiet_gio_hang_treo'
)
OR m.definition LIKE N'%thanh_tien%';
GO

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN (
    'gio_hang_treo',
    'chi_tiet_gio_hang_treo'
)
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO

CREATE TABLE #KetQua (
    DatabaseName NVARCHAR(128),
    SchemaName NVARCHAR(128),
    TableName NVARCHAR(128)
);

DECLARE @TenDB SYSNAME;
DECLARE @SQL NVARCHAR(MAX);

DECLARE db_cursor CURSOR LOCAL FAST_FORWARD FOR
SELECT name
FROM sys.databases
WHERE database_id > 4
  AND state = 0;

OPEN db_cursor;
FETCH NEXT FROM db_cursor INTO @TenDB;

WHILE @@FETCH_STATUS = 0
BEGIN
    SET @SQL = N'
        USE ' + QUOTENAME(@TenDB) + N';

        INSERT INTO #KetQua
        SELECT
            DB_NAME(),
            s.name,
            t.name
        FROM sys.tables t
        JOIN sys.schemas s
            ON t.schema_id = s.schema_id
        WHERE t.name LIKE N''%gio_hang%''
           OR t.name LIKE N''%don_hang%'';
    ';

    EXEC sp_executesql @SQL;

    FETCH NEXT FROM db_cursor INTO @TenDB;
END;

CLOSE db_cursor;
DEALLOCATE db_cursor;

SELECT *
FROM #KetQua
ORDER BY DatabaseName, TableName;

DROP TABLE #KetQua;


USE PetStation;
GO

-- Kiểm tra cấu trúc bảng lưu đơn nháp
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN (
    'gio_hang_treo',
    'chi_tiet_gio_hang_treo'
)
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO

-- Kiểm tra trigger và nội dung trigger
SELECT
    OBJECT_SCHEMA_NAME(t.parent_id) AS SchemaName,
    OBJECT_NAME(t.parent_id) AS TableName,
    t.name AS TriggerName,
    m.definition AS TriggerDefinition
FROM sys.triggers t
JOIN sys.sql_modules m
    ON t.object_id = m.object_id
WHERE OBJECT_NAME(t.parent_id) IN (
    'gio_hang_treo',
    'chi_tiet_gio_hang_treo'
)
OR m.definition LIKE N'%thanh_tien%';
GO



USE PetStationDemo1;
GO

SELECT
    SCHEMA_NAME(o.schema_id) AS SchemaName,
    o.name AS ObjectName,
    o.type_desc AS ObjectType,
    m.definition AS ObjectDefinition
FROM sys.sql_modules m
JOIN sys.objects o
    ON o.object_id = m.object_id
WHERE m.definition LIKE N'%thanh_tien%';
GO

USE PetStationDemo1;
GO

SELECT
    id,
    so_hoa_don,
    tam_tinh,
    tong_tien,
    trang_thai
FROM hoa_don
WHERE trang_thai = 'GIO_TREO';
GO