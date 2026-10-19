const path=require('node:path');
process.chdir(path.resolve(__dirname,'../..'));
// Sinh nâng cấp chỉ đổi nội dung hiển thị; không sửa số tiền, số dư hoặc trạng thái.
const fs=require('fs');
const tep='database/002_sample_data.sql', marker='-- UI_DATA_CLEANUP_V1:';
const nguon='docs/data/san-pham-nguon.json';
const duLieu=JSON.parse(fs.readFileSync(nguon,'utf8'));
function gon(s){return (s||'').replace(/Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng\./g,'').replace(/(?: — |Quy cách: )?1 sản phẩm theo mẫu của nhà sản xuất\.?/g,'').replace(/(\d+)\s*\(gr\)/g,'$1g').replace(/(\d+)\s*\(kg\)/g,'$1kg').replace(/(\d+)\s*\(ml\)/g,'$1ml').replace(/(\d+)\s*\(lít\)/g,'$1 lít').trim();}
for(const sp of duLieu){sp.ten=gon(sp.ten);sp.quyCach=gon(sp.quyCach);sp.moTa=gon(sp.moTa);if(sp.quyCach.includes('lon'))sp.donVi='Lon';else if(sp.quyCach.includes('túi'))sp.donVi='Túi';else if(sp.quyCach.includes('ml')&&/chai|sữa tắm|dầu xả|nước hoa/i.test(sp.ten))sp.donVi='Chai';}
fs.writeFileSync(nguon,JSON.stringify(duLieu,null,2)+'\n');
const q=s=>"N'"+String(s).replaceAll("'","''")+"'";
const danhSach=[['nhan_vien','ma_nhan_vien','NV'],['khach_hang','ma_khach_hang','KH'],['thu_cung','ma_thu_cung','TC'],['san_pham','ma_san_pham','SP'],['nha_cung_cap','ma_ncc','NCC'],['goi_dich_vu_spa','ma_goi','DV'],['phong_hotel','ma_phong','P'],['don_hang','ma_don','DH'],['hoa_don','so_hoa_don','HD'],['phieu_nhap_kho','so_phieu','PN'],['phieu_xuat_kho','so_phieu','PX'],['bien_ban_kiem_ke','so_bien_ban','KK'],['phieu_doi_tra','ma_phieu','DT'],['phieu_tiep_nhan_spa','ma_phieu','SPA'],['hop_dong_luu_tru','ma_hop_dong','HT'],['lich_hen','ma_lich_hen','LH'],['gio_hang_treo','ma_gio','TREO'],['voucher','ma_voucher','VC'],['yeu_cau_phe_duyet','ma_yeu_cau','YC'],['lo_hang','so_lo','LO']];
let sql=`${marker} đổi nội dung hiển thị, giữ ID và toàn bộ số liệu nghiệp vụ.
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRAN;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WITH(UPDLOCK,HOLDLOCK) WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT')
 BEGIN
 DECLARE @bang SYSNAME,@cot SYSNAME,@tienTo VARCHAR(12),@lenh NVARCHAR(MAX),@id BIGINT,@maCu NVARCHAR(100),@maMoi NVARCHAR(100),@so INT,@trung INT;
 CREATE TABLE #canDoi(id BIGINT,ma NVARCHAR(100));
 DECLARE bang_cursor CURSOR LOCAL FAST_FORWARD FOR SELECT * FROM (VALUES
 ${danhSach.map(x=>'('+x.map(q).join(',')+')').join(',\n ')}
 )v(bang,cot,tien_to);
 OPEN bang_cursor;FETCH NEXT FROM bang_cursor INTO @bang,@cot,@tienTo;
 WHILE @@FETCH_STATUS=0
 BEGIN
  TRUNCATE TABLE #canDoi;
  SET @lenh=N'INSERT #canDoi SELECT id,'+QUOTENAME(@cot)+N' FROM '+QUOTENAME(@bang)+N' WHERE '+QUOTENAME(@cot)+N' LIKE ''%DEMO%'' OR '+QUOTENAME(@cot)+N' LIKE ''%REAL-%''; SELECT @so=COALESCE(MAX(TRY_CONVERT(int,SUBSTRING('+QUOTENAME(@cot)+N',LEN(@tienTo)+1,30))),0) FROM '+QUOTENAME(@bang)+N' WHERE '+QUOTENAME(@cot)+N' LIKE @tienTo+''%'';';
  EXEC sp_executesql @lenh,N'@so int OUTPUT,@tienTo varchar(12)',@so OUTPUT,@tienTo;
  WHILE EXISTS(SELECT 1 FROM #canDoi)
  BEGIN
   SELECT TOP(1) @id=id,@maCu=ma FROM #canDoi ORDER BY id;
   SET @trung=1;
   WHILE @trung>0
   BEGIN
    SET @so=@so+1;SET @maMoi=@tienTo+RIGHT('000000'+CAST(@so AS VARCHAR(10)),6);
    SET @lenh=N'SELECT @trung=COUNT(*) FROM '+QUOTENAME(@bang)+N' WHERE '+QUOTENAME(@cot)+N'=@maMoi';
    EXEC sp_executesql @lenh,N'@trung int OUTPUT,@maMoi nvarchar(100)',@trung OUTPUT,@maMoi;
   END;
   INSERT doi_chieu_ma_du_lieu VALUES(@bang,@cot,@id,@maCu,@maMoi,'UI_DATA_CLEANUP_V1');
   SET @lenh=N'UPDATE '+QUOTENAME(@bang)+N' SET '+QUOTENAME(@cot)+N'=@maMoi WHERE id=@id AND '+QUOTENAME(@cot)+N'=@maCu';
   EXEC sp_executesql @lenh,N'@id bigint,@maCu nvarchar(100),@maMoi nvarchar(100)',@id,@maCu,@maMoi;
   DELETE #canDoi WHERE id=@id;
  END;
  FETCH NEXT FROM bang_cursor INTO @bang,@cot,@tienTo;
 END;
 CLOSE bang_cursor;DEALLOCATE bang_cursor;DROP TABLE #canDoi;
 -- Những tham chiếu lưu bằng chuỗi được đổi cùng transaction với mã chính.
 UPDATE c SET ma_san_pham=m.ma_moi FROM chi_tiet_gio_hang_treo c JOIN doi_chieu_ma_du_lieu m ON m.bang='san_pham' AND c.ma_san_pham=m.ma_cu;
 UPDATE c SET ma_dich_vu=m.ma_moi FROM chi_tiet_don_hang c JOIN doi_chieu_ma_du_lieu m ON m.bang='goi_dich_vu_spa' AND c.ma_dich_vu=m.ma_cu;
 UPDATE h SET ma_voucher=m.ma_moi FROM hoa_don h JOIN doi_chieu_ma_du_lieu m ON m.bang='voucher' AND h.ma_voucher=m.ma_cu;
 UPDATE c SET so_lo=m.ma_moi FROM chi_tiet_phieu_nhap c JOIN doi_chieu_ma_du_lieu m ON m.bang='lo_hang' AND c.so_lo=m.ma_cu JOIN lo_hang l ON l.id=m.id_doi_tuong AND l.san_pham_id=c.san_pham_id;
 UPDATE y SET ma_doi_tuong=m.ma_moi FROM yeu_cau_phe_duyet y JOIN doi_chieu_ma_du_lieu m ON y.ma_doi_tuong=m.ma_cu WHERE NOT EXISTS(SELECT 1 FROM doi_chieu_ma_du_lieu z WHERE z.ma_cu=m.ma_cu AND z.ma_moi<>m.ma_moi);
 -- Chỉ sửa hồ sơ mẫu được nhận diện bằng bảng đối chiếu.
 UPDATE n SET ho_ten=CASE t.ten_dang_nhap WHEN 'demo.admin' THEN N'Nguyễn Minh Quân' WHEN 'demo.sales' THEN N'Trần Ngọc Anh' WHEN 'demo.stock' THEN N'Lê Hoàng Phúc' WHEN 'demo.spa' THEN N'Phạm Thảo Nhi' ELSE n.ho_ten END
 FROM nhan_vien n JOIN tai_khoan t ON t.nhan_vien_id=n.id WHERE t.ten_dang_nhap IN('demo.admin','demo.sales','demo.stock','demo.spa');
 UPDATE k SET ho_ten=CASE m.ma_cu WHEN 'KH-DEMO-199' THEN N'Lê Minh Châu' WHEN 'KH-DEMO-200' THEN N'Nguyễn Gia Hân' WHEN 'KH-DEMO-SALES' THEN N'Võ Anh Thư' ELSE REPLACE(LEFT(k.ho_ten,CHARINDEX(N' (DEMO',k.ho_ten+N' (DEMO')-1),N'DEMO ',N'') END,
 dia_chi=CASE WHEN k.dia_chi LIKE N'%giả lập%' THEN N'Quận Bình Thạnh, TP. Hồ Chí Minh' ELSE k.dia_chi END,
 email=CASE WHEN k.email LIKE 'demo%@example.invalid' THEN 'khachhang'+CAST(k.id AS VARCHAR)+'@example.com' ELSE k.email END
 FROM khach_hang k JOIN doi_chieu_ma_du_lieu m ON m.bang='khach_hang' AND m.id_doi_tuong=k.id;
 UPDATE t SET ten=CASE WHEN m.ma_cu='TC-DEMO-OK' THEN N'Bông' WHEN m.ma_cu='TC-DEMO-THIEU' THEN N'Mun' ELSE LEFT(t.ten,CHARINDEX(N' DEMO',t.ten+N' DEMO')-1) END
 FROM thu_cung t JOIN doi_chieu_ma_du_lieu m ON m.bang='thu_cung' AND m.id_doi_tuong=t.id;
 UPDATE n SET ten=CHOOSE((ROWNUM.stt-1)%6+1,N'Công ty Thực phẩm thú cưng An Phú',N'Nhà phân phối Minh Khang',N'Công ty Phụ kiện thú cưng Hưng Thịnh',N'Nhà phân phối Happy Pet',N'Công ty Chăm sóc thú cưng Đại Việt',N'Nhà phân phối Thiên Phúc'),nguoi_dai_dien=CHOOSE((ROWNUM.stt-1)%6+1,N'Nguyễn Thanh Hà',N'Trần Quốc Huy',N'Lê Bảo Trâm',N'Phạm Minh Tuấn',N'Võ Thu Ngân',N'Đỗ Hoàng Long'),dia_chi=N'TP. Hồ Chí Minh'
 FROM nha_cung_cap n JOIN (SELECT id_doi_tuong,ROW_NUMBER() OVER(ORDER BY id_doi_tuong) stt FROM doi_chieu_ma_du_lieu WHERE bang='nha_cung_cap') ROWNUM ON ROWNUM.id_doi_tuong=n.id;
 UPDATE p SET ten=REPLACE(p.ten,N'DEMO ',N'') FROM san_pham p JOIN doi_chieu_ma_du_lieu m ON m.bang='san_pham' AND m.id_doi_tuong=p.id;
 UPDATE g SET ten_goi=x.ten+CASE WHEN EXISTS(SELECT 1 FROM goi_dich_vu_spa z WHERE z.id<>g.id AND z.ten_goi=x.ten) THEN N' ('+g.ma_goi+N')' ELSE N'' END
 FROM goi_dich_vu_spa g CROSS APPLY(SELECT REPLACE(REPLACE(g.ten_goi,N' — DEMO',N''),N'DEMO ',N'') ten)x WHERE g.ten_goi LIKE N'%DEMO%';
 UPDATE phong_hotel SET loai_phong=REPLACE(loai_phong,N' DEMO',N'') WHERE loai_phong LIKE N'%DEMO%';
 UPDATE phien_lam_viec SET ten_ca=N'Ca ngày',giai_trinh=N'Đã kiểm đếm và đối chiếu tiền mặt' WHERE ten_ca LIKE N'%REAL_DATA_V1%' OR ten_ca LIKE N'%DEMO%';
`;
for(const sp of duLieu)sql+=` UPDATE san_pham SET ten=${q(sp.ten)},quy_cach=${q(sp.quyCach)},mo_ta=${q(sp.moTa)},don_vi_tinh=${q(sp.donVi)} WHERE ma_san_pham=${q(sp.ma)};\n`;
sql+=` UPDATE c SET ten_hang=p.ten FROM chi_tiet_don_hang c JOIN san_pham p ON p.id=c.san_pham_id WHERE c.ten_hang LIKE N'%DEMO%' OR c.ten_hang LIKE N'%1 sản phẩm theo mẫu%' OR p.ma_san_pham LIKE 'SP-__-%';
 UPDATE c SET ten=p.ten FROM chi_tiet_gio_hang_treo c JOIN san_pham p ON p.ma_san_pham=c.ma_san_pham WHERE c.ten LIKE N'%DEMO%';
 -- Các trường văn bản nghiệp vụ; loại trừ tài khoản, cấu hình, nguồn và nhật ký kiểm toán.
 DECLARE @tb SYSNAME,@col SYSNAME;
 DECLARE text_cursor CURSOR LOCAL FAST_FORWARD FOR
 SELECT t.name,c.name FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id JOIN sys.types ty ON ty.user_type_id=c.user_type_id
 WHERE SCHEMA_NAME(t.schema_id)='dbo' AND ty.name IN('nvarchar','varchar') AND t.name NOT IN('tai_khoan','cai_dat','doi_chieu_ma_du_lieu','nhat_ky_he_thong')
 AND c.name IN('ghi_chu','mo_ta','ly_do','nguyen_nhan','ten_vaccine','tinh_trang_suc_khoe','tinh_cach','tiem_phong','ten_hang','ten_gio','noi_dung','giai_trinh','gia_tri_cu','gia_tri_moi','noi_dung_truoc','noi_dung_sau','an_uong','ve_sinh','tinh_trang');
 OPEN text_cursor;FETCH NEXT FROM text_cursor INTO @tb,@col;
 WHILE @@FETCH_STATUS=0 BEGIN
  SET @lenh=N'UPDATE '+QUOTENAME(@tb)+N' SET '+QUOTENAME(@col)+N'=LTRIM(RTRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE('+QUOTENAME(@col)+N',N''REAL_DATA_V1 — '',N''''),N''REAL_DATA_V1'',N''''),N''REAL-DEMO'',N''''),N''DEMO'',N''''),N''trình diễn'',N''''),N''giả lập'',N''''),N''tình huống'',N''''),N'' — '',N'' ''),N''  '',N'' ''))) WHERE '+QUOTENAME(@col)+N' LIKE N''%DEMO%'' OR '+QUOTENAME(@col)+N' LIKE N''%REAL_DATA_V1%'' OR '+QUOTENAME(@col)+N' LIKE N''%giả lập%'' OR '+QUOTENAME(@col)+N' LIKE N''%trình diễn%'' OR '+QUOTENAME(@col)+N' LIKE N''%tình huống%''';
  EXEC sp_executesql @lenh;
  FETCH NEXT FROM text_cursor INTO @tb,@col;
 END;CLOSE text_cursor;DEALLOCATE text_cursor;
 -- Nội dung ghi chú gọn, đúng mục đích của chứng từ/hồ sơ.
 UPDATE khach_hang SET ghi_chu=N'Ưu tiên liên hệ qua điện thoại.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='khach_hang');
 UPDATE goi_dich_vu_spa SET mo_ta=N'Chăm sóc theo gói dịch vụ và bảng giá theo cân nặng.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='goi_dich_vu_spa');
 UPDATE phong_hotel SET ghi_chu=N'Vệ sinh và khử khuẩn sau mỗi lượt lưu trú.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='phong_hotel');
 UPDATE nha_cung_cap SET ghi_chu=N'Thanh toán theo điều khoản hợp tác.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='nha_cung_cap');
 UPDATE phieu_nhap_kho SET ghi_chu=N'Nhập bổ sung hàng hóa.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='phieu_nhap_kho');
 UPDATE hoa_don SET ghi_chu=N'Đã kiểm tra thông tin thanh toán.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='hoa_don');
 UPDATE thu_cung SET tinh_trang_suc_khoe=N'Theo dõi sức khỏe định kỳ.' WHERE id IN(SELECT id_doi_tuong FROM doi_chieu_ma_du_lieu WHERE bang='thu_cung') AND tinh_trang_suc_khoe LIKE N'%không thay thế%';
 INSERT cai_dat(khoa,gia_tri) VALUES('UI_DATA_CLEANUP_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
`;
// Sau nâng cấp, không chạy lại các khối seed tham chiếu mã cũ. Cơ sở dữ liệu mới vẫn nạp đủ.
const guard="IF EXISTS(SELECT 1 FROM dbo.cai_dat WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT') RETURN;\n";
const khoiUnicodeCu=fs.readFileSync(tep,'utf8').includes('-- UI_UNICODE_REPAIR_V1:')?'-- UI_UNICODE_REPAIR_V1:'+fs.readFileSync(tep,'utf8').split('-- UI_UNICODE_REPAIR_V1:')[1]:'';
let cu=fs.readFileSync(tep,'utf8').split(marker)[0].split(guard).join('').trimEnd();
cu=cu.split(/(?<=^|\n)GO\s*\r?\n/mi).map(batch=>batch.trim()?guard+batch:batch).join('GO\n');
fs.writeFileSync(tep,cu+'\n\n'+sql+'\n'+khoiUnicodeCu);
console.log('Đã tạo nâng cấp dữ liệu UI_DATA_CLEANUP_V1.');
