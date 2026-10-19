import com.petstation.dao.CauHinhCSDL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/** Sao lưu, kiểm tra nâng cấp hiển thị và bảo toàn số dư trước khi commit. */
class ChuanHoaDuLieu {
    static String ten(String s) { return "["+s.replace("]","]]")+"]"; }
    static void chay(Connection c,String sql) throws Exception {
        for(String batch:sql.split("(?im)^\\s*GO\\s*(?:--[^\\r\\n]*)?$"))
            if(!batch.isBlank()) try(var s=c.createStatement()){
                boolean ketQua=s.execute(batch);
                while(ketQua || s.getUpdateCount()!=-1) {
                    if(ketQua) try(var r=s.getResultSet()){while(r.next()) { /* Đọc hết để SQL hoàn tất. */ }}
                    ketQua=s.getMoreResults();
                }
            }
    }
    static String khoi(String file,String marker)throws Exception {
        String sql=Files.readString(Path.of(file));int i=sql.indexOf(marker);
        if(i<0)throw new IllegalArgumentException("Thiếu khối nâng cấp "+marker);
        return sql.substring(i);
    }
    static Map<String,List<String>> cotBaoToan(Connection c)throws Exception {
        Map<String,List<String>> bang=new TreeMap<>();
        String query="SELECT t.name,c.name FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id JOIN sys.types ty ON ty.user_type_id=c.user_type_id WHERE SCHEMA_NAME(t.schema_id)='dbo' AND EXISTS(SELECT 1 FROM sys.columns k WHERE k.object_id=t.object_id AND k.name='id') AND t.name<>'doi_chieu_ma_du_lieu' AND (ty.name NOT IN('nvarchar','varchar','nchar','char','text','ntext','timestamp','binary','varbinary') OR c.name IN('trang_thai','loai_phieu','loai_dich_vu','loai_xuat','hinh_thuc_thanh_toan','vai_tro','ten_dang_nhap','mat_khau_hash','idempotency_key','nguon_url','ngay_thu_thap','anh')) ORDER BY t.name,c.column_id";
        try(var s=c.createStatement();var r=s.executeQuery(query)){while(r.next())bang.computeIfAbsent(r.getString(1),x->new ArrayList<>()).add(r.getString(2));}
        return bang;
    }
    static Map<String,String> chup(Connection c,Map<String,List<String>> cot)throws Exception {
        Map<String,String> kq=new TreeMap<>();
        for(var entry:cot.entrySet()) {
            MessageDigest hash=MessageDigest.getInstance("SHA-256");long n=0;
            String sql="SELECT "+String.join(",",entry.getValue().stream().map(ChuanHoaDuLieu::ten).toList())+" FROM "+ten(entry.getKey())+" ORDER BY id";
            try(var s=c.createStatement();var r=s.executeQuery(sql)){while(r.next()){n++;for(int i=1;i<=entry.getValue().size();i++){
                byte[] b=Objects.toString(r.getString(i),"<NULL>").getBytes(StandardCharsets.UTF_8);
                hash.update(java.nio.ByteBuffer.allocate(4).putInt(b.length).array());hash.update(b);
            }}}
            kq.put(entry.getKey(),n+":"+HexFormat.of().formatHex(hash.digest()));
        }
        // Cấu hình cửa hàng, ngân hàng, quyền và toàn bộ tài khoản không được đổi.
        for(String table:List.of("cai_dat","vai_tro_quyen","tai_khoan")) {
            MessageDigest hash=MessageDigest.getInstance("SHA-256");
            String query="SELECT * FROM "+ten(table)+(table.equals("cai_dat")?" WHERE khoa<>'UI_DATA_CLEANUP_V1_APPLIED_AT' ORDER BY khoa":table.equals("tai_khoan")?" ORDER BY id":" ORDER BY vai_tro,ma_quyen");
            try(var s=c.createStatement();var r=s.executeQuery(query)){while(r.next())for(int i=1;i<=r.getMetaData().getColumnCount();i++)hash.update((Objects.toString(r.getString(i),"<NULL>")+"\u0000").getBytes(StandardCharsets.UTF_8));}
            kq.put("protected:"+table,HexFormat.of().formatHex(hash.digest()));
        }
        return kq;
    }
    static Map<String,List<String>> tatCaCot(Connection c)throws Exception {
        Map<String,List<String>> kq=new TreeMap<>();
        try(var s=c.createStatement();var r=s.executeQuery("SELECT t.name,c.name FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id JOIN sys.types ty ON ty.user_type_id=c.user_type_id WHERE SCHEMA_NAME(t.schema_id)='dbo' AND ty.name NOT IN('timestamp','binary','varbinary') AND EXISTS(SELECT 1 FROM sys.columns k WHERE k.object_id=t.object_id AND k.name='id') ORDER BY t.name,c.column_id")) {
            while(r.next())kq.computeIfAbsent(r.getString(1),x->new ArrayList<>()).add(r.getString(2));
        }
        return kq;
    }
    static void kiemTraNoiDung(Connection c)throws Exception {
        List<String[]> cot=new ArrayList<>();
        String query="SELECT t.name,c.name FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id JOIN sys.types ty ON ty.user_type_id=c.user_type_id WHERE SCHEMA_NAME(t.schema_id)='dbo' AND t.name NOT IN('tai_khoan','cai_dat','doi_chieu_ma_du_lieu','nhat_ky_he_thong') AND ty.name IN('varchar','nvarchar') AND (c.name IN('ten','ho_ten','ten_goi','loai_phong','ten_ca','ten_hang','ghi_chu','mo_ta','ly_do','nguyen_nhan','ten_vaccine','an_uong','ve_sinh','tinh_trang','tinh_trang_suc_khoe','tinh_cach','tiem_phong','quy_cach','nguoi_dai_dien','dia_chi') OR c.name LIKE 'ma[_]%' OR c.name IN('so_phieu','so_lo','so_bien_ban','so_hoa_don'))";
        try(var s=c.createStatement();var r=s.executeQuery(query)){while(r.next())cot.add(new String[]{r.getString(1),r.getString(2)});}
        for(var col:cot) {
            String sql="SELECT COUNT(*) FROM "+ten(col[0])+" WHERE "+ten(col[1])+" LIKE N'%DEMO%' OR "+ten(col[1])+" LIKE N'%trình diễn%' OR "+ten(col[1])+" LIKE N'%giả lập%' OR "+ten(col[1])+" LIKE N'%Thông tin quy cách và giá được đối chiếu%' OR "+ten(col[1])+" LIKE N'%1 sản phẩm theo mẫu%'";
            try(var s=c.createStatement();var r=s.executeQuery(sql)){r.next();if(r.getLong(1)>0)throw new IllegalStateException("Còn nhãn mẫu tại "+col[0]+"."+col[1]+": "+r.getLong(1));}
        }
    }
    public static void main(String[] args)throws Exception {
        if(args.length!=1||!Set.of("--test","--retest","--apply").contains(args[0]))throw new IllegalArgumentException("Dùng --test, --retest hoặc --apply");
        boolean test=!args[0].equals("--apply");var cfg=CauHinhCSDL.nap();
        String url=test?cfg.url().replaceAll("(?i)databaseName=[^;]+","databaseName=PetStationIntegrationTest"):cfg.url();
        Files.createDirectories(Path.of("target/ui-finish"));
        try(var c=DriverManager.getConnection(url,cfg.nguoiDung(),cfg.matKhau());var s=c.createStatement()) {
            String db;try(var r=s.executeQuery("SELECT DB_NAME()")){r.next();db=r.getString(1);}
            if(test&&!db.equals("PetStationIntegrationTest"))throw new IllegalStateException("Sai database kiểm thử");
            if(Set.of("master","msdb","model","tempdb").contains(db.toLowerCase()))throw new IllegalStateException("Không dùng database hệ thống");
            System.out.println("Database: "+db);
            if(!test) {
                String dir;try(var r=s.executeQuery("SELECT CONVERT(nvarchar(4000),SERVERPROPERTY('InstanceDefaultBackupPath'))")){r.next();dir=r.getString(1);}
                if(dir==null)throw new IllegalStateException("Thiếu thư mục sao lưu");
                String path=dir+(dir.endsWith("\\")?"":"\\")+db+"_before_ui_cleanup_"+LocalDateTime.now().toString().replaceAll("[^0-9]","")+".bak";
                String lit="N'"+path.replace("'","''")+"'";
                s.execute("BACKUP DATABASE "+ten(db)+" TO DISK="+lit+" WITH COPY_ONLY,CHECKSUM");
                s.execute("RESTORE VERIFYONLY FROM DISK="+lit+" WITH CHECKSUM");
                Files.writeString(Path.of("target/ui-finish/database-backup.txt"),path);
                System.out.println("Backup CHECKSUM / VERIFYONLY: OK");
            }
            chay(c,khoi("database/001_schema.sql","-- UI_FINISH_V1:"));
            if(test && args[0].equals("--retest")) s.executeUpdate("DELETE cai_dat WHERE khoa='UI_DATA_CLEANUP_V1_APPLIED_AT'");
            var cot=cotBaoToan(c);var truoc=chup(c,cot);
            c.setAutoCommit(false);
            try {
                chay(c,khoi("database/002_sample_data.sql","-- UI_DATA_CLEANUP_V1:"));
                kiemTraNoiDung(c);
                var sau=chup(c,cot);
                if(!truoc.equals(sau))throw new IllegalStateException("Sai lệch số dư/cấu hình ở "+truoc.keySet().stream().filter(k->!Objects.equals(truoc.get(k),sau.get(k))).toList());
                c.commit();
            } catch(Exception e){c.rollback();throw e;}
            var cotDayDu=tatCaCot(c);var lanMot=chup(c,cotDayDu);
            long soMa;try(var r=s.executeQuery("SELECT COUNT(*) FROM doi_chieu_ma_du_lieu")){r.next();soMa=r.getLong(1);}
            chay(c,Files.readString(Path.of("database/002_sample_data.sql")));
            long soMaSau;try(var r=s.executeQuery("SELECT COUNT(*) FROM doi_chieu_ma_du_lieu")){r.next();soMaSau=r.getLong(1);}
            if(soMa!=soMaSau || !lanMot.equals(chup(c,cotDayDu))) {c.rollback();throw new IllegalStateException("Seed chạy lại thay đổi mã, nội dung hoặc số dư");}
            c.commit();
            StringBuilder map=new StringBuilder("# Đối chiếu mã sau chuẩn hóa\n\n| Bảng | ID | Mã cũ | Mã mới |\n|---|---:|---|---|\n");
            try(var r=s.executeQuery("SELECT bang,id_doi_tuong,ma_cu,ma_moi FROM doi_chieu_ma_du_lieu ORDER BY bang,id_doi_tuong")){while(r.next())map.append("| ").append(r.getString(1)).append(" | ").append(r.getLong(2)).append(" | ").append(r.getString(3)).append(" | ").append(r.getString(4)).append(" |\n");}
            Files.writeString(Path.of(test?"target/ui-finish/doi-chieu-ma-test.md":"docs/DOI_CHIEU_MA_DU_LIEU.md"),map);
            Files.writeString(Path.of("target/ui-finish/doi-chieu-"+(test?"test":"live")+".txt"),"Database: "+db+"\n"+truoc+"\nSau: "+chup(c,cot)+"\nChạy lại: không thay đổi.\n");
            System.out.println("Số bản ghi, tiền, tồn, điểm, cọc, công nợ, thời gian, trạng thái và tài khoản giữ nguyên: "+cot.size()+" bảng. Chạy lại: OK.");
        }
    }
}
