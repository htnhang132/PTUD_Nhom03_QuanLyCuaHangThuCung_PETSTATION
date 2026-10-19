/** Tạo phần bổ sung SQL từ bảng nguồn đã xác minh, giữ đúng hai tệp SQL của dự án. */
const fs=require('node:fs');
const path=require('node:path');
const root=path.resolve(__dirname,'../..');
const products=JSON.parse(fs.readFileSync(path.join(root,'docs/data/san-pham-nguon.json'),'utf8'));
if(products.length!==60)throw new Error('Cần đủ 60 sản phẩm đã xác minh');
const sqlString=s=>"N'"+String(s).replaceAll("'","''")+"'";
const rows=products.map((p,i)=>`(${i+1},${[p.ma,p.ten,p.nhom,p.donVi].map(sqlString).join(',')},${p.gia},${[p.anh,p.thuongHieu,p.quyCach,p.doiTuong,p.moTa,p.nguon,p.ngayThuThap].map(sqlString).join(',')})`).join(',\n');
const marker='-- REAL_DATA_V1:';
let sql=`
${marker} Danh mục thật và giao dịch GIẢ LẬP phục vụ demo; chỉ nhập một lần.
-- Nguồn và ảnh: docs/data/san-pham-nguon.json. Giá nhập, HSD, tồn, khách và giao dịch KHÔNG phải số liệu thực tế.
SET XACT_ABORT ON;
BEGIN TRY
 BEGIN TRANSACTION;
 DECLARE @khoaNhap INT;
 EXEC @khoaNhap=sys.sp_getapplock @Resource='PetStation_REAL_DATA_V1',@LockMode='Exclusive',@LockOwner='Transaction',@LockTimeout=10000;
 IF @khoaNhap<0 THROW 51000,N'Không khóa được bộ dữ liệu demo.',1;
 IF NOT EXISTS(SELECT 1 FROM cai_dat WHERE khoa='REAL_DATA_V1_APPLIED_AT')
 BEGIN
 DECLARE @sp TABLE(stt INT,ma VARCHAR(30),ten NVARCHAR(180),nhom NVARCHAR(100),don_vi NVARCHAR(30),gia BIGINT,anh NVARCHAR(500),thuong_hieu NVARCHAR(100),quy_cach NVARCHAR(100),doi_tuong NVARCHAR(100),mo_ta NVARCHAR(1000),nguon NVARCHAR(1000),ngay DATE);
 INSERT @sp VALUES
${rows};
 IF EXISTS(SELECT 1 FROM san_pham p JOIN @sp s ON p.ma_san_pham=s.ma OR p.ten=s.ten)
  THROW 51001,N'Mã hoặc tên sản phẩm bộ REAL_DATA_V1 đã tồn tại nhưng chưa có dấu xác nhận; cần đối chiếu trước khi nhập.',1;
 DECLARE @quanLy BIGINT=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.admin'),
  @banHang BIGINT=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.sales'),
  @kho BIGINT=(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.stock'),
  @nvSpa BIGINT=(SELECT nhan_vien_id FROM tai_khoan WHERE ten_dang_nhap='demo.spa'),
  @homNay DATE=CAST(SYSDATETIME() AS DATE), @moc DATE=DATEADD(day,-29,CAST(SYSDATETIME() AS DATE));
 IF @quanLy IS NULL OR @banHang IS NULL OR @kho IS NULL OR @nvSpa IS NULL
  THROW 51002,N'Thiếu bốn tài khoản demo trong dữ liệu nền.',1;
 INSERT san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,ton_toi_thieu,theo_doi_hsd,anh,thuong_hieu,quy_cach,doi_tuong_su_dung,mo_ta,nguon_url,ngay_thu_thap)
 SELECT ma,ten,nhom,don_vi,gia,ROUND(gia*0.65,0),5,CASE WHEN nhom IN(N'Thức ăn',N'Grooming') THEN 1 ELSE 0 END,anh,thuong_hieu,quy_cach,doi_tuong,mo_ta,nguon,ngay FROM @sp;
 INSERT lich_su_gia(san_pham_id,gia_cu,gia_moi,nguoi_thay_doi_id,thoi_diem_thay_doi)
 SELECT p.id,0,p.gia_ban,@quanLy,DATEADD(hour,7,CAST(@moc AS DATETIME2)) FROM san_pham p JOIN @sp s ON s.ma=p.ma_san_pham;
 DECLARE @i INT=1,@kh BIGINT,@tc BIGINT,@sanPham BIGINT,@lo BIGINT,@phieu BIGINT,@don BIGINT,@hd BIGINT,@dong BIGINT,@ca BIGINT,
  @gia BIGINT,@tong BIGINT,@giam BIGINT,@diem INT,@soLuong INT,@luc DATETIME2,@ma VARCHAR(30),@ncc BIGINT,@giaNhap BIGINT,@nhap INT,
  @goi BIGINT,@phong BIGINT,@coc BIGINT,@hopDong BIGINT,@phieuSpa BIGINT,@lichHen BIGINT,@goc BIGINT,@dongGoc BIGINT,@loGoc BIGINT,@tienDoi BIGINT;
 -- Khách hàng và thông tin liên hệ dưới đây hoàn toàn giả lập, không gửi thư hay gọi điện.
 WHILE @i<=20 BEGIN
  SET @ma='KH-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2);
  INSERT khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,ngay_tao)
  VALUES(@ma,CHOOSE((@i-1)%10+1,N'Nguyễn Minh Anh',N'Trần Gia Hân',N'Lê Hoàng Nam',N'Phạm Ngọc Linh',N'Võ Khánh An',N'Đặng Thanh Tâm',N'Bùi Quang Huy',N'Đỗ Bảo Ngọc',N'Hồ Minh Khang',N'Dương Tuệ Nhi')+N' (DEMO '+CAST(@i AS NVARCHAR)+N')','000900'+RIGHT('0000'+CAST(@i AS VARCHAR),4),'demo'+CAST(@i AS VARCHAR)+'@example.invalid',N'Địa chỉ giả lập, TP.HCM',N'REAL_DATA_V1 — hồ sơ tình huống giả lập',@moc);
  SET @i+=1;
 END;
 SET @i=1;
 WHILE @i<=30 BEGIN
  SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-'+RIGHT('00'+CAST((@i-1)%20+1 AS VARCHAR),2);
  INSERT thu_cung(ma_thu_cung,khach_hang_id,ten,loai,giong,gioi_tinh,mau_long,can_nang,ngay_sinh,tinh_cach,tinh_trang_suc_khoe,tiem_chung)
  VALUES('TC-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),@kh,CHOOSE((@i-1)%10+1,N'Mochi',N'Milo',N'Bông',N'Lucky',N'Coco',N'Miu',N'Bắp',N'Đậu',N'Sữa',N'Gạo')+N' DEMO '+CAST(@i AS NVARCHAR),CASE WHEN @i%2=0 THEN 'CAT' ELSE 'DOG' END,CASE WHEN @i%2=0 THEN N'Anh lông ngắn' ELSE N'Poodle' END,CASE WHEN @i%3=0 THEN 'Cai' ELSE 'Duc' END,N'Trắng / vàng',3.5,DATEADD(month,-12-@i,@homNay),N'Hồ sơ giả lập: hiền, cần làm quen',N'DEMO — không thay thế hồ sơ thú y',CASE WHEN @i<=24 THEN N'Có hai lần tiêm giả lập, xem lịch sử' ELSE N'Thiếu thông tin tiêm phòng — yêu cầu bổ sung' END);
  SET @tc=SCOPE_IDENTITY();
  IF @i<=24 INSERT tiem_phong(thu_cung_id,ten_vaccine,ngay_tiem,ngay_het_han,ghi_chu)
   VALUES(@tc,N'Mũi cơ bản 1 (DEMO)',DATEADD(day,-100,@homNay),DATEADD(day,265,@homNay),N'Giả lập để kiểm tra điều kiện tiếp nhận'),(@tc,N'Mũi cơ bản 2 (DEMO)',DATEADD(day,-65,@homNay),DATEADD(day,300,@homNay),N'Giả lập để kiểm tra điều kiện tiếp nhận');
  SET @i+=1;
 END;
 SET @i=1;
 WHILE @i<=6 BEGIN
  INSERT nha_cung_cap(ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,dieu_khoan)
  VALUES('NCC-REAL-DEMO-'+CAST(@i AS VARCHAR),N'Nhà cung cấp tình huống DEMO '+CAST(@i AS NVARCHAR),N'Đại diện giả lập','000800000'+CAST(@i AS VARCHAR),N'Địa chỉ giả lập',N'30 ngày — dữ liệu DEMO');
  SET @i+=1;
 END;
 -- Sáu mươi phiếu nhập giúp đối chiếu tồn và công nợ, không cộng tồn khi chạy lại.
 SET @i=1;
 WHILE @i<=60 BEGIN
  SELECT @ma=ma,@gia=gia FROM @sp WHERE stt=@i;
  SELECT @sanPham=id,@giaNhap=gia_nhap_gan_nhat FROM san_pham WHERE ma_san_pham=@ma;
  SELECT @ncc=id FROM nha_cung_cap WHERE ma_ncc='NCC-REAL-DEMO-'+CAST((@i-1)%6+1 AS VARCHAR);
  SET @nhap=10+@i%21;
  INSERT phieu_nhap_kho(so_phieu,nha_cung_cap_id,nguoi_lap_id,ngay_nhap,tong_tien,da_tra,trang_thai,ghi_chu)
  VALUES('PN-REAL-DEMO-'+RIGHT('000'+CAST(@i AS VARCHAR),3),@ncc,@kho,@moc,@nhap*@giaNhap,CASE WHEN @i%3=0 THEN @nhap*@giaNhap ELSE (@nhap*@giaNhap)/2 END,'POSTED',N'REAL_DATA_V1 — giá nhập, số lượng và HSD giả lập');
  SET @phieu=SCOPE_IDENTITY();
  INSERT chi_tiet_phieu_nhap(phieu_nhap_id,san_pham_id,so_lo,han_su_dung,so_luong,gia_nhap)
  SELECT @phieu,@sanPham,'REAL-DEMO-'+RIGHT('000'+CAST(@i AS VARCHAR),3),CASE WHEN theo_doi_hsd=1 THEN DATEADD(day,CASE WHEN @i%9=0 THEN 15 ELSE 180 END,@homNay) END,@nhap,@giaNhap FROM san_pham WHERE id=@sanPham;
  INSERT lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)
  SELECT san_pham_id,so_lo,han_su_dung,so_luong FROM chi_tiet_phieu_nhap WHERE phieu_nhap_id=@phieu;
  SET @i+=1;
 END;
 -- Gói và giá Spa do shop giả lập cho buổi demo, không mạo nhận bảng giá của nguồn sản phẩm.
 SET @i=1;
 WHILE @i<=6 BEGIN
  INSERT goi_dich_vu_spa(ma_goi,ten_goi,mo_ta,thoi_gian_du_kien)
  VALUES('DV-REAL-DEMO-'+CAST(@i AS VARCHAR),CHOOSE(@i,N'Tắm cơ bản',N'Tắm dưỡng lông',N'Cắt tỉa tạo kiểu',N'Vệ sinh tai và móng',N'Chải lông và gỡ rối',N'Chăm sóc toàn diện')+N' — DEMO',N'Dịch vụ và giá giả lập cho trình diễn',30+@i*15);
  SET @goi=SCOPE_IDENTITY();
  INSERT bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)
  SELECT @goi,l.loai,k.ten,k.tu,k.den,(100000+@i*25000)*k.he_so FROM (VALUES('DOG'),('CAT'))l(loai)
  CROSS JOIN (VALUES(N'< 5kg',0,5,1),(N'5 - 10kg',5,10,2),(N'>= 10kg',10,NULL,3))k(ten,tu,den,he_so);
  SET @i+=1;
 END;
 SET @i=1;
 WHILE @i<=10 BEGIN
  INSERT phong_hotel(ma_phong,loai_phong,gia_dem,trang_thai,suc_chua,ghi_chu)
  VALUES('P-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),CASE WHEN @i<=5 THEN N'Tiêu chuẩn DEMO' ELSE N'VIP DEMO' END,CASE WHEN @i<=5 THEN 180000 ELSE 250000 END,'AVAILABLE',1,N'REAL_DATA_V1 — phòng và giá giả lập');
  SET @phong=SCOPE_IDENTITY();
  INSERT bang_gia_hotel(phong_id,don_gia) SELECT id,gia_dem FROM phong_hotel WHERE id=@phong;
  SET @i+=1;
 END;
 DECLARE @cacCa TABLE(ngay DATE,id BIGINT);
 SET @i=0;
 WHILE @i<30 BEGIN
  INSERT phien_lam_viec(tai_khoan_id,ten_ca,mo_luc,dong_luc,tien_dau_ca,tien_thuc_te,chenh_lech,giai_trinh,trang_thai)
  VALUES(@banHang,N'Ca REAL_DATA_V1 DEMO',DATEADD(hour,8,CAST(DATEADD(day,@i,@moc) AS DATETIME2)),DATEADD(hour,20,CAST(DATEADD(day,@i,@moc) AS DATETIME2)),500000,500000,0,N'Ca lịch sử giả lập đã đối chiếu','HANDED_OVER');
  INSERT @cacCa VALUES(DATEADD(day,@i,@moc),SCOPE_IDENTITY());
  SET @i+=1;
 END;
 -- 18 hóa đơn hàng, 6 Spa, 4 Hotel, 2 đổi phụ kiện; cùng định nghĩa tiền và điểm với service.
 SET @i=1;
 WHILE @i<=30 BEGIN
  SET @sanPham=NULL;SET @lo=NULL;SET @phieuSpa=NULL;SET @hopDong=NULL;SET @giam=0;SET @coc=0;SET @tienDoi=0;
  SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-'+RIGHT('00'+CAST(CASE WHEN @i=29 THEN 17 WHEN @i=30 THEN 18 ELSE (@i-1)%20+1 END AS VARCHAR),2);
  SET @luc=DATEADD(hour,10+@i%8,CAST(DATEADD(day,CASE WHEN @i IN(17,18) THEN 27 WHEN @i>=29 THEN 28 WHEN @i BETWEEN 25 AND 28 THEN 20+(@i-25)*2 ELSE (@i-1)%26 END,@moc) AS DATETIME2));
  SELECT @ca=id FROM @cacCa WHERE ngay=CAST(@luc AS DATE);
  SET @soLuong=1;
  IF @i<=18 BEGIN
   IF @i<=16 SELECT @ma=ma FROM @sp WHERE stt=@i;
   ELSE SELECT TOP 1 @ma=ma FROM @sp WHERE nhom=N'Phụ kiện' ORDER BY gia,ma;
   SELECT @sanPham=id,@gia=CAST(gia_ban AS BIGINT) FROM san_pham WHERE ma_san_pham=@ma;
   SET @soLuong=CASE WHEN @i>=17 THEN 2 ELSE 1+@i%3 END;
  END ELSE IF @i<=24 BEGIN
   SELECT @goi=id FROM goi_dich_vu_spa WHERE ma_goi='DV-REAL-DEMO-'+CAST(@i-18 AS VARCHAR);
   SELECT @gia=CAST(don_gia AS BIGINT) FROM bang_gia_spa WHERE goi_id=@goi AND loai_thu_cung='DOG' AND can_nang_tu=0;
  END ELSE IF @i<=28 BEGIN
   SELECT @phong=id,@gia=CAST(gia_dem AS BIGINT) FROM phong_hotel WHERE ma_phong='P-REAL-DEMO-'+RIGHT('00'+CAST(@i-24 AS VARCHAR),2);
   SET @coc=@gia/2;
  END ELSE BEGIN
   SELECT @goc=h.id,@dongGoc=c.id,@loGoc=b.lo_hang_id,@tienDoi=c.don_gia FROM hoa_don h JOIN chi_tiet_don_hang c ON c.don_hang_id=h.don_hang_id JOIN phan_bo_lo_hang b ON b.chi_tiet_don_hang_id=c.id WHERE h.so_hoa_don='HD-REAL-DEMO-'+RIGHT('00'+CAST(@i-12 AS VARCHAR),2);
   SELECT TOP 1 @ma=ma FROM @sp WHERE nhom=N'Phụ kiện' AND gia>@tienDoi ORDER BY gia,ma;
   SELECT @sanPham=id,@gia=CAST(gia_ban AS BIGINT) FROM san_pham WHERE ma_san_pham=@ma;
   SET @giam=@tienDoi;
  END;
  IF @i BETWEEN 25 AND 28 SET @giam=@coc;
  SET @tong=@gia*@soLuong-@giam;
  SET @diem=CONVERT(INT,FLOOR(@tong/10000.0));
  IF @i>=29 BEGIN
   DECLARE @daTraGoc BIGINT=(SELECT tong_tien FROM hoa_don WHERE id=@goc);
   SET @diem=CONVERT(INT,FLOOR((@daTraGoc+@tong)/10000.0)-FLOOR(@daTraGoc/10000.0));
  END;
  INSERT don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai,tao_luc) VALUES('DH-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),@ca,@kh,'PAID',@luc);
  SET @don=SCOPE_IDENTITY();
  INSERT chi_tiet_don_hang(don_hang_id,san_pham_id,so_luong,don_gia,giam_gia,ma_dich_vu,ten_hang)
  VALUES(@don,@sanPham,@soLuong,CASE WHEN @i BETWEEN 25 AND 28 THEN @tong ELSE @gia END,0,CASE WHEN @i BETWEEN 19 AND 24 THEN 'DV-REAL-DEMO-'+CAST(@i-18 AS VARCHAR) WHEN @i BETWEEN 25 AND 28 THEN 'HOTEL' END,CASE WHEN @sanPham IS NOT NULL THEN (SELECT ten FROM san_pham WHERE id=@sanPham) WHEN @i<=24 THEN N'Dịch vụ Spa DEMO' ELSE N'Quyết toán Hotel DEMO (đã trừ cọc)' END);
  SET @dong=SCOPE_IDENTITY();
  INSERT hoa_don(so_hoa_don,don_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,thanh_toan_luc,idempotency_key,tam_tinh,giam_gia,ghi_chu)
  VALUES('HD-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2),@don,@tong,'CASH',@tong,0,@diem,@luc,'REAL_DATA_V1_INVOICE_'+CAST(@i AS VARCHAR),CASE WHEN @i BETWEEN 25 AND 28 THEN @tong ELSE @gia*@soLuong END,CASE WHEN @i BETWEEN 25 AND 28 THEN 0 ELSE @giam END,N'REAL_DATA_V1 — giao dịch GIẢ LẬP, không có thu tiền thật');
  SET @hd=SCOPE_IDENTITY();
  IF @sanPham IS NOT NULL BEGIN
   SELECT @lo=id FROM lo_hang WHERE san_pham_id=@sanPham AND so_lo LIKE 'REAL-DEMO-%';
   UPDATE lo_hang SET so_luong_ton=so_luong_ton-@soLuong WHERE id=@lo;
   INSERT phan_bo_lo_hang(chi_tiet_don_hang_id,lo_hang_id,so_luong) VALUES(@dong,@lo,@soLuong);
  END;
  UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+@diem WHERE id=@kh;
  INSERT lich_su_diem(khach_hang_id,hoa_don_id,thay_doi,so_du_sau,ly_do,tao_luc)
  SELECT id,@hd,@diem,diem_tich_luy,N'REAL_DATA_V1 — tích điểm giao dịch giả lập',@luc FROM khach_hang WHERE id=@kh;
  IF @i BETWEEN 19 AND 24 BEGIN
   SELECT TOP 1 @tc=id FROM thu_cung WHERE khach_hang_id=@kh ORDER BY id;
   INSERT lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
   VALUES('LH-REAL-PAID-'+CAST(@i AS VARCHAR),@kh,@tc,'SPA',DATEADD(hour,-2,@luc),@luc,'COMPLETED',@nvSpa,@goi,N'REAL_DATA_V1 DEMO');
   SET @lichHen=SCOPE_IDENTITY();
   INSERT phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,tong_tien,trang_thai,tao_luc,nhan_vien_id,ghi_chu,hoa_don_id)
   VALUES('SPA-REAL-DEMO-'+CAST(@i-18 AS VARCHAR),@lichHen,@tc,3.5,@gia,'PAID',DATEADD(hour,-2,@luc),@nvSpa,N'REAL_DATA_V1 DEMO',@hd);
   SET @phieuSpa=SCOPE_IDENTITY();
   INSERT chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong) SELECT @phieuSpa,id,ten_goi,@gia,1 FROM goi_dich_vu_spa WHERE id=@goi;
  END;
  IF @i BETWEEN 25 AND 28 BEGIN
   SELECT TOP 1 @tc=id FROM thu_cung WHERE khach_hang_id=@kh ORDER BY id;
   INSERT hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,ngay_ra_thuc_te,tien_coc,trang_thai,gia_dem,ghi_chu,hoa_don_id,da_nhan_coc,nhan_coc_luc,nhan_coc_boi,phien_nhan_coc_id)
   VALUES('HT-REAL-PAID-'+CAST(@i AS VARCHAR),@kh,@tc,@phong,DATEADD(day,-1,@luc),@luc,@luc,@coc,'CHECKED_OUT',@gia,N'REAL_DATA_V1 — một đêm, cọc tiền mặt giả lập',@hd,1,DATEADD(day,-1,@luc),@banHang,(SELECT id FROM @cacCa WHERE ngay=CAST(DATEADD(day,-1,@luc) AS DATE)));
   SET @hopDong=SCOPE_IDENTITY();
   INSERT nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,thoi_gian,an_uong,ve_sinh,tinh_trang) VALUES(@hopDong,@nvSpa,DATEADD(hour,-4,@luc),N'Ăn hết khẩu phần DEMO',N'Đã vệ sinh khay và phòng',N'Nhật ký giả lập: sinh hoạt bình thường');
  END;
  IF @i>=29 BEGIN
   INSERT phieu_doi_tra(ma_phieu,hoa_don_id,nguoi_lap_id,chenh_lech,trang_thai,tao_luc,ly_do,loai_phieu,hoa_don_thay_the_id,gia_tri_duoc_doi,khoa_thao_tac)
   VALUES('DOI-REAL-DEMO-'+CAST(@i-28 AS VARCHAR),@goc,@banHang,@tong,'COMPLETED',@luc,N'REAL_DATA_V1 — đổi phụ kiện trong ba ngày, không hoàn tiền','EXCHANGE',@hd,@tienDoi,'REAL_DATA_V1_EXCHANGE_'+CAST(@i AS VARCHAR));
   SET @phieu=SCOPE_IDENTITY();
   INSERT chi_tiet_doi_tra(phieu_doi_tra_id,chi_tiet_don_hang_id,lo_hang_id,so_luong,tien_hoan) VALUES(@phieu,@dongGoc,@loGoc,1,@tienDoi);
   UPDATE lo_hang SET so_luong_ton=so_luong_ton+1 WHERE id=@loGoc;
  END;
  SET @i+=1;
 END;
 -- Voucher có chủ, có lịch sử điểm; thêm điểm mở đầu DEMO rồi mới đổi, không tạo điểm âm.
 SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-01';
 UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+400 WHERE id=@kh;
 INSERT lich_su_diem(khach_hang_id,thay_doi,so_du_sau,ly_do) SELECT id,400,diem_tich_luy,N'REAL_DATA_V1 — điểm mở đầu giả lập' FROM khach_hang WHERE id=@kh;
 UPDATE khach_hang SET diem_tich_luy=diem_tich_luy-200 WHERE id=@kh;
 INSERT lich_su_diem(khach_hang_id,thay_doi,so_du_sau,ly_do) SELECT id,-200,diem_tich_luy,N'REAL_DATA_V1 — đổi voucher Spa 150000' FROM khach_hang WHERE id=@kh;
 INSERT voucher(ma_voucher,khach_hang_id,menh_gia,hieu_luc_den,chi_ap_dung_spa,so_luong,khoa_doi_diem)
 VALUES('VC-REAL-DEMO-150',@kh,150000,DATEADD(day,60,SYSDATETIME()),1,1,'REAL_DATA_V1_REDEEM');
 UPDATE k SET hang_thanh_vien=CHOOSE(1+hang.bac,N'Đồng',N'Bạc',N'Vàng',N'Bạch Kim',N'Kim Cương')
 FROM khach_hang k
 OUTER APPLY(SELECT COALESCE(SUM(h.tong_tien),0) tong FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE d.khach_hang_id=k.id AND h.trang_thai='PAID')ct
 OUTER APPLY(SELECT COUNT(*) bac FROM STRING_SPLIT((SELECT gia_tri FROM cai_dat WHERE khoa='TIER_SPEND'),',') WHERE TRY_CONVERT(BIGINT,value)<=ct.tong)hang
 WHERE k.ma_khach_hang LIKE 'KH-REAL-DEMO-%';
 -- Phiếu Spa trong ngày ở các bước khác nhau, có lịch hẹn và hồ sơ tiêm phòng phù hợp.
 SET @i=1;
 WHILE @i<=4 BEGIN
  SELECT @tc=id,@kh=khach_hang_id FROM thu_cung WHERE ma_thu_cung='TC-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2);
  SELECT @goi=id FROM goi_dich_vu_spa WHERE ma_goi='DV-REAL-DEMO-1';
  SET @luc=DATEADD(hour,8+@i,CAST(@homNay AS DATETIME2));
  INSERT lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
  VALUES('LH-REAL-OPEN-'+CAST(@i AS VARCHAR),@kh,@tc,'SPA',@luc,DATEADD(hour,1,@luc),CASE WHEN @i<=2 THEN 'CHECKED_IN' ELSE 'COMPLETED' END,@nvSpa,@goi,N'REAL_DATA_V1 DEMO');
  SET @lichHen=SCOPE_IDENTITY();
  INSERT phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,tong_tien,trang_thai,tao_luc,nhan_vien_id,ghi_chu)
  VALUES('SPA-REAL-OPEN-'+CAST(@i AS VARCHAR),@lichHen,@tc,3.5,125000,CHOOSE(@i,'RECEIVED','IN_PROGRESS','COMPLETED','COMPLETED'),@luc,@nvSpa,N'REAL_DATA_V1 DEMO — có thể tiếp tục thao tác');
  SET @phieuSpa=SCOPE_IDENTITY();
  INSERT chi_tiet_phieu_spa(phieu_spa_id,goi_id,mo_ta,don_gia,so_luong) VALUES(@phieuSpa,@goi,N'Tắm cơ bản — DEMO',125000,1);
  SET @i+=1;
 END;
 -- Hai Hotel đang ở và hai đặt trước, mỗi hợp đồng một phòng riêng, cọc đủ 50%.
 SET @i=1;
 WHILE @i<=4 BEGIN
  SELECT @tc=id,@kh=khach_hang_id FROM thu_cung WHERE ma_thu_cung='TC-REAL-DEMO-'+RIGHT('00'+CAST(@i+10 AS VARCHAR),2);
  SELECT @phong=id,@gia=gia_dem FROM phong_hotel WHERE ma_phong='P-REAL-DEMO-'+RIGHT('00'+CAST(@i+4 AS VARCHAR),2);
  SET @luc=DATEADD(hour,9,CAST(DATEADD(day,CASE WHEN @i<=2 THEN -1 ELSE 1 END,@homNay) AS DATETIME2));
  INSERT hop_dong_luu_tru(ma_hop_dong,khach_hang_id,thu_cung_id,phong_id,ngay_vao,ngay_ra_du_kien,tien_coc,trang_thai,gia_dem,ghi_chu,da_nhan_coc,nhan_coc_luc,nhan_coc_boi,phien_nhan_coc_id)
  VALUES('HT-REAL-OPEN-'+CAST(@i AS VARCHAR),@kh,@tc,@phong,@luc,DATEADD(day,3,@luc),@gia*3/2,CASE WHEN @i<=2 THEN 'CHECKED_IN' ELSE 'BOOKED' END,@gia,N'REAL_DATA_V1 DEMO — cọc tiền mặt giả lập',1,DATEADD(hour,8,CAST(DATEADD(day,CASE WHEN @i<=2 THEN -1 ELSE 0 END,@homNay) AS DATETIME2)),@banHang,(SELECT id FROM @cacCa WHERE ngay=DATEADD(day,CASE WHEN @i<=2 THEN -1 ELSE 0 END,@homNay)));
  SET @hopDong=SCOPE_IDENTITY();
  IF @i<=2 BEGIN
   UPDATE phong_hotel SET trang_thai='OCCUPIED' WHERE id=@phong;
   INSERT nhat_ky_cham_soc(hop_dong_id,nhan_vien_id,thoi_gian,an_uong,ve_sinh,tinh_trang) VALUES(@hopDong,@nvSpa,SYSDATETIME(),N'Đã cho ăn theo khẩu phần DEMO',N'Đã vệ sinh',N'Nhật ký chăm sóc giả lập');
  END;
  SET @i+=1;
 END;
 -- Đơn treo thuộc đúng tài khoản bán hàng để mở lại bằng giao diện.
 SET @i=1;
 WHILE @i<=2 BEGIN
  SELECT @kh=id FROM khach_hang WHERE ma_khach_hang='KH-REAL-DEMO-'+RIGHT('00'+CAST(@i AS VARCHAR),2);
  INSERT gio_hang_treo(ma_gio,tai_khoan_id,khach_hang_id) VALUES('TREO-REAL-DEMO-'+CAST(@i AS VARCHAR),@banHang,@kh);
  SET @phieu=SCOPE_IDENTITY();
  INSERT chi_tiet_gio_hang_treo(gio_id,ma_san_pham,ten,don_gia,so_luong) SELECT @phieu,ma,ten,gia,1 FROM @sp WHERE stt=@i;
  SET @i+=1;
 END;
 -- Lịch hẹn sắp tới, không trùng nhân viên hoặc thú cưng.
 SET @i=1;
 WHILE @i<=6 BEGIN
  SELECT @tc=id,@kh=khach_hang_id FROM thu_cung WHERE ma_thu_cung='TC-REAL-DEMO-'+RIGHT('00'+CAST(@i+14 AS VARCHAR),2);
  SELECT @goi=id FROM goi_dich_vu_spa WHERE ma_goi='DV-REAL-DEMO-1';
  SET @luc=DATEADD(hour,9,CAST(DATEADD(day,@i,@homNay) AS DATETIME2));
  INSERT lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id,ghi_chu)
  VALUES('LH-REAL-NEXT-'+CAST(@i AS VARCHAR),@kh,@tc,'SPA',@luc,DATEADD(hour,1,@luc),'SCHEDULED',@nvSpa,@goi,N'REAL_DATA_V1 — lịch hẹn giả lập');
  SET @i+=1;
 END;
 -- Xuất hủy có chứng từ để minh họa cảnh báo tồn thấp, không sửa tồn trực tiếp thiếu lịch sử.
 SELECT @lo=l.id,@nhap=l.so_luong_ton-3,@giaNhap=p.gia_nhap_gan_nhat FROM lo_hang l JOIN san_pham p ON p.id=l.san_pham_id WHERE p.ma_san_pham='SP-VS-010' AND l.so_lo LIKE 'REAL-DEMO-%';
 INSERT phieu_xuat_kho(so_phieu,nguoi_lap_id,loai_xuat,ly_do,tong_gia_tri,trang_thai)
 VALUES('PX-REAL-DEMO-01',@kho,'WRITE_OFF',N'REAL_DATA_V1 — tình huống hàng hỏng bao bì giả lập',@nhap*@giaNhap,'POSTED');
 SET @phieu=SCOPE_IDENTITY();
 INSERT chi_tiet_phieu_xuat(phieu_xuat_id,lo_hang_id,so_luong,gia_tri) VALUES(@phieu,@lo,@nhap,@nhap*@giaNhap);
 UPDATE lo_hang SET so_luong_ton=so_luong_ton-@nhap WHERE id=@lo;
 INSERT bien_ban_kiem_ke(so_bien_ban,nguoi_lap_id,ky_kiem_ke,trang_thai) VALUES('KK-REAL-DEMO-01',@kho,CONVERT(VARCHAR(10),@homNay,23),'PENDING');
 SET @phieu=SCOPE_IDENTITY();
 INSERT chi_tiet_kiem_ke(bien_ban_id,lo_hang_id,ton_he_thong,ton_thuc_te,nguyen_nhan) VALUES(@phieu,@lo,3,2,N'REAL_DATA_V1 — lệch kiểm kê giả lập, chưa áp dụng');
 INSERT yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload)
 VALUES('YC-REAL-KIEM-KE','STOCKTAKE',@kho,'STOCKTAKE',CAST(@phieu AS VARCHAR),N'REAL_DATA_V1 — chờ duyệt chênh lệch',N'Tồn sổ 3',N'Tồn thực 2','');
 SET @i=1;
 WHILE @i<=2 BEGIN
  SELECT @sanPham=p.id,@gia=p.gia_ban FROM san_pham p WHERE p.ma_san_pham='SP-PK-'+RIGHT('000'+CAST(@i AS VARCHAR),3);
  INSERT de_xuat_gia(san_pham_id,nguoi_de_xuat_id,gia_hien_tai,gia_de_xuat,ly_do)
  VALUES(@sanPham,@kho,@gia,@gia+5000,N'REAL_DATA_V1 — đề xuất giá giả lập chưa áp dụng');
  SET @phieu=SCOPE_IDENTITY();
  INSERT yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload)
  VALUES('YC-REAL-GIA-'+CAST(@i AS VARCHAR),'PRICE',@kho,'PRICE_CHANGE',CAST(@phieu AS VARCHAR),N'REAL_DATA_V1 — thử duyệt hoặc từ chối',CAST(@gia AS NVARCHAR),CAST(@gia+5000 AS NVARCHAR),'');
  SET @i+=1;
 END;
 -- Lịch tuần sau cho nhân viên DEMO; yêu cầu nghỉ phép còn chờ quản lý duyệt.
 INSERT lich_lam_viec(nhan_vien_id,ca_lam_viec_id,ngay_lam,loai)
 SELECT n.id,c.id,DATEADD(day,d.so,@homNay),'WORK' FROM nhan_vien n CROSS JOIN ca_lam_viec c CROSS JOIN(VALUES(1),(2),(3),(4),(5),(6),(7))d(so)
 WHERE n.ma_nhan_vien LIKE 'NVDEMO%' AND c.ma_ca='CA_SANG'
 AND NOT EXISTS(SELECT 1 FROM lich_lam_viec l WHERE l.nhan_vien_id=n.id AND l.ca_lam_viec_id=c.id AND l.ngay_lam=DATEADD(day,d.so,@homNay));
 INSERT yeu_cau_phe_duyet(ma_yeu_cau,loai,nguoi_gui_id,loai_doi_tuong,ma_doi_tuong,ly_do,noi_dung_truoc,noi_dung_sau,payload)
 VALUES('YC-REAL-NGHI-PHEP','LEAVE',(SELECT id FROM tai_khoan WHERE ten_dang_nhap='demo.spa'),'LEAVE',CAST(@nvSpa AS VARCHAR),N'REAL_DATA_V1 — tình huống xin nghỉ phép giả lập',N'Đang phân công',N'Nghỉ một ngày',CONVERT(VARCHAR(10),DATEADD(day,3,@homNay),23));
 -- Kết ca lịch sử phải khớp tiền mặt hóa đơn và cọc đã nhận.
 UPDATE c SET tien_thuc_te=c.tien_dau_ca+COALESCE(h.tien,0)+COALESCE(ht.coc,0),chenh_lech=0
 FROM phien_lam_viec c JOIN @cacCa ds ON ds.id=c.id
 OUTER APPLY(SELECT SUM(h.tong_tien) tien FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE d.phien_lam_viec_id=c.id AND h.trang_thai='PAID' AND h.hinh_thuc_thanh_toan='CASH')h
 OUTER APPLY(SELECT SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE phien_nhan_coc_id=c.id AND da_nhan_coc=1)ht;
 INSERT nhat_ky_he_thong(tai_khoan_id,hanh_dong,loai_doi_tuong,ma_doi_tuong,ghi_chu)
 VALUES(@quanLy,'DEMO_IMPORT','DATASET','REAL_DATA_V1',N'Nhập 60 sản phẩm có nguồn và bộ giao dịch giả lập liên thông; không có thu/chi thật.');
 INSERT cai_dat(khoa,gia_tri) VALUES('REAL_DATA_V1_APPLIED_AT',CONVERT(varchar(30),SYSDATETIME(),126));
 END;
 COMMIT;
END TRY
BEGIN CATCH
 IF @@TRANCOUNT>0 ROLLBACK;
 THROW;
END CATCH;
GO
`;
const file=path.join(root,'database/002_sample_data.sql');
const unicodeCu=fs.readFileSync(file,'utf8').includes('-- UI_UNICODE_REPAIR_V1:')?'-- UI_UNICODE_REPAIR_V1:'+fs.readFileSync(file,'utf8').split('-- UI_UNICODE_REPAIR_V1:')[1]:'';
const old=fs.readFileSync(file,'utf8').split(marker)[0].trimEnd();
fs.writeFileSync(file,old+'\n\n'+sql+'\n'+unicodeCu);
console.log('Generated REAL_DATA_V1: 60 products and linked demo transactions.');
// Tái tạo phần chuẩn hóa cuối cùng và chặn seed cũ sau khi mã đã đổi.
require('./chuan-hoa-du-lieu.cjs');
