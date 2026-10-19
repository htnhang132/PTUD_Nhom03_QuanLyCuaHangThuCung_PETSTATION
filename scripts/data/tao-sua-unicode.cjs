const path=require('node:path');
process.chdir(path.resolve(__dirname,'../..'));
const fs=require('fs');
const schema='database/001_schema.sql',seed='database/002_sample_data.sql';
const schemaMarker='-- UI_UNICODE_SCHEMA_V1:';let s=fs.readFileSync(schema,'utf8');
s=s.replace("hang_thanh_vien VARCHAR(20) NOT NULL DEFAULT 'REGULAR'","hang_thanh_vien NVARCHAR(20) NOT NULL DEFAULT N'REGULAR'");
s=s.replace('gioi_tinh VARCHAR(10) NULL','gioi_tinh NVARCHAR(10) NULL');
s=s.split(schemaMarker)[0].trimEnd()+`\n\n${schemaMarker} tên hạng thành viên cần lưu được tiếng Việt.
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
`;
fs.writeFileSync(schema,s);
const marker='-- UI_UNICODE_REPAIR_V1:';let d=fs.readFileSync(seed,'utf8').split(marker)[0].trimEnd();
d+=`\n\n${marker} chỉ sửa biến thể hỏng xác định được; không tính lại hạng hoặc số dư.
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRAN;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WITH(UPDLOCK,HOLDLOCK) WHERE khoa='UI_UNICODE_REPAIR_V1_APPLIED_AT')
 BEGIN
 DECLARE @anhXa TABLE(cu NVARCHAR(100),moi NVARCHAR(100));
 INSERT @anhXa VALUES(N'Đ?ng',N'Đồng'),(N'?ồng',N'Đồng'),(N'??ng',N'Đồng'),(N'Ð?ng',N'Đồng'),(N'B?c',N'Bạc'),(N'V?ng',N'Vàng'),(N'B?ch Kim',N'Bạch Kim'),(N'Kim C??ng',N'Kim Cương');
 INSERT @anhXa SELECT CONVERT(NVARCHAR(100),CONVERT(VARCHAR(20),v.ten)),v.ten FROM (VALUES(N'Đồng'),(N'Bạc'),(N'Vàng'),(N'Bạch Kim'),(N'Kim Cương'))v(ten)
 WHERE CONVERT(NVARCHAR(100),CONVERT(VARCHAR(20),v.ten))<>v.ten
 AND NOT EXISTS(SELECT 1 FROM @anhXa a WHERE a.cu=CONVERT(NVARCHAR(100),CONVERT(VARCHAR(20),v.ten)));
 INSERT doi_chieu_unicode(bang,cot,id_doi_tuong,gia_tri_cu,gia_tri_moi,phien_ban)
 SELECT 'khach_hang','hang_thanh_vien',k.id,k.hang_thanh_vien,a.moi,'UI_UNICODE_REPAIR_V1' FROM khach_hang k JOIN @anhXa a ON a.cu=k.hang_thanh_vien
 WHERE k.hang_thanh_vien<>a.moi AND NOT EXISTS(SELECT 1 FROM doi_chieu_unicode z WHERE z.bang='khach_hang' AND z.cot='hang_thanh_vien' AND z.id_doi_tuong=k.id AND z.phien_ban='UI_UNICODE_REPAIR_V1');
 UPDATE k SET hang_thanh_vien=z.gia_tri_moi FROM khach_hang k JOIN doi_chieu_unicode z ON z.id_doi_tuong=k.id AND z.bang='khach_hang' AND z.cot='hang_thanh_vien' AND z.phien_ban='UI_UNICODE_REPAIR_V1' WHERE k.hang_thanh_vien=z.gia_tri_cu;
 INSERT cai_dat(khoa,gia_tri) VALUES('UI_UNICODE_REPAIR_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WITH(UPDLOCK,HOLDLOCK) WHERE khoa='UI_UNICODE_PET_V1_APPLIED_AT')
 BEGIN
 DECLARE @gioi TABLE(cu NVARCHAR(50),moi NVARCHAR(50));
 INSERT @gioi VALUES(N'Đ?c',N'Đực'),(N'?ực',N'Đực'),(N'??c',N'Đực'),(N'C?i',N'Cái');
 INSERT @gioi SELECT CONVERT(NVARCHAR(50),CONVERT(VARCHAR(10),v.ten)),v.ten FROM (VALUES(N'Đực'),(N'Cái'))v(ten) WHERE CONVERT(NVARCHAR(50),CONVERT(VARCHAR(10),v.ten))<>v.ten AND NOT EXISTS(SELECT 1 FROM @gioi a WHERE a.cu=CONVERT(NVARCHAR(50),CONVERT(VARCHAR(10),v.ten)));
 INSERT doi_chieu_unicode(bang,cot,id_doi_tuong,gia_tri_cu,gia_tri_moi,phien_ban) SELECT 'thu_cung','gioi_tinh',t.id,t.gioi_tinh,a.moi,'UI_UNICODE_PET_V1' FROM thu_cung t JOIN @gioi a ON a.cu=t.gioi_tinh WHERE t.gioi_tinh<>a.moi AND NOT EXISTS(SELECT 1 FROM doi_chieu_unicode z WHERE z.bang='thu_cung' AND z.cot='gioi_tinh' AND z.id_doi_tuong=t.id AND z.phien_ban='UI_UNICODE_PET_V1');
 UPDATE t SET gioi_tinh=z.gia_tri_moi FROM thu_cung t JOIN doi_chieu_unicode z ON z.id_doi_tuong=t.id AND z.bang='thu_cung' AND z.cot='gioi_tinh' AND z.phien_ban='UI_UNICODE_PET_V1' WHERE t.gioi_tinh=z.gia_tri_cu;
 INSERT cai_dat(khoa,gia_tri) VALUES('UI_UNICODE_PET_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
`;
fs.writeFileSync(seed,d);
// Giữ khối sửa Unicode khi chạy lại bộ sinh dữ liệu/chuẩn hóa trước đó.
const gen='scripts/data/chuan-hoa-du-lieu.cjs';let g=fs.readFileSync(gen,'utf8');
if(!g.includes('const khoiUnicodeCu='))g=g.replace("let cu=fs.readFileSync(tep,'utf8').split(marker)[0]","const khoiUnicodeCu=fs.readFileSync(tep,'utf8').includes('-- UI_UNICODE_REPAIR_V1:')?'-- UI_UNICODE_REPAIR_V1:'+fs.readFileSync(tep,'utf8').split('-- UI_UNICODE_REPAIR_V1:')[1]:'';\nlet cu=fs.readFileSync(tep,'utf8').split(marker)[0]").replace("fs.writeFileSync(tep,cu+'\\n\\n'+sql);","fs.writeFileSync(tep,cu+'\\n\\n'+sql+'\\n'+khoiUnicodeCu);");
fs.writeFileSync(gen,g);
const gen2='scripts/data/tao-du-lieu-thuc-te.cjs';let h=fs.readFileSync(gen2,'utf8');
if(!h.includes('const unicodeCu='))h=h.replace("const old=fs.readFileSync(file,'utf8').split(marker)[0].trimEnd();","const unicodeCu=fs.readFileSync(file,'utf8').includes('-- UI_UNICODE_REPAIR_V1:')?'-- UI_UNICODE_REPAIR_V1:'+fs.readFileSync(file,'utf8').split('-- UI_UNICODE_REPAIR_V1:')[1]:'';\nconst old=fs.readFileSync(file,'utf8').split(marker)[0].trimEnd();").replace("fs.writeFileSync(file,old+'\\n\\n'+sql);","fs.writeFileSync(file,old+'\\n\\n'+sql+'\\n'+unicodeCu);");
fs.writeFileSync(gen2,h);
console.log('Đã tạo nâng cấp Unicode có phiên bản.');
