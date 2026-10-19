import com.petstation.dao.CauHinhCSDL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/** Chạy phần SQL bổ sung đã duyệt, sao lưu thật và kiểm tra mọi bản ghi cũ. */
class BoSungDuLieuThucTe {
    record DauVan(long idLonNhat, String bam) {}

    static String dinhDanh(String ten) { return "["+ten.replace("]","]]")+"]"; }
    static DauVan chup(Connection c,String bang,long gioiHan) throws Exception {
        MessageDigest hash=MessageDigest.getInstance("SHA-256");long max=0;
        try(var p=c.prepareStatement("SELECT * FROM "+dinhDanh(bang)+" WHERE id<=? ORDER BY id")) {
            p.setLong(1,gioiHan);
            try(var r=p.executeQuery()) {
                var md=r.getMetaData();
                while(r.next()) { max=r.getLong("id");for(int i=1;i<=md.getColumnCount();i++) {
                    if(md.getColumnType(i)==Types.BINARY||md.getColumnType(i)==Types.VARBINARY)continue;
                    String v=r.getString(i);byte[] b=(v==null?"<NULL>":v).getBytes(StandardCharsets.UTF_8);
                    hash.update(java.nio.ByteBuffer.allocate(4).putInt(b.length).array());hash.update(b);
                }}
            }
        }
        return new DauVan(max,HexFormat.of().formatHex(hash.digest()));
    }

    static void chay(Connection c,String sql)throws Exception {
        try(var s=c.createStatement()) {
            for(String batch:sql.split("(?im)^\\s*GO\\s*(?:--[^\\r\\n]*)?$"))if(!batch.isBlank())s.execute(batch);
        }
    }

    static String docKhoi(String tep,String marker)throws Exception {
        String sql=Files.readString(Path.of(tep));int start=sql.indexOf(marker);
        if(start<0)throw new IllegalArgumentException("Thiếu marker: "+marker);
        return sql.substring(start);
    }

    public static void main(String[] args)throws Exception {
        boolean test=Arrays.asList(args).contains("--test");
        if(args.length!=1||(!test&&!args[0].equals("--apply")))throw new IllegalArgumentException("Dùng --test hoặc --apply");
        var cfg=CauHinhCSDL.nap();String url=test?cfg.url().replaceAll("(?i)databaseName=[^;]+","databaseName=PetStationIntegrationTest"):cfg.url();
        try(var c=DriverManager.getConnection(url,cfg.nguoiDung(),cfg.matKhau());var s=c.createStatement()) {
            String db;try(var r=s.executeQuery("SELECT DB_NAME()")){r.next();db=r.getString(1);}
            if(test&&!db.equals("PetStationIntegrationTest"))throw new IllegalStateException("Chỉ kiểm thử trên PetStationIntegrationTest");
            if(Set.of("master","model","msdb","tempdb").contains(db.toLowerCase()))throw new IllegalStateException("Không dùng database hệ thống");
            System.out.println("Database: "+db);
            Files.createDirectories(Path.of("target/product-import"));
            if(!test) {
                String dir;try(var r=s.executeQuery("SELECT CONVERT(nvarchar(4000),SERVERPROPERTY('InstanceDefaultBackupPath'))")){r.next();dir=r.getString(1);}
                if(dir==null)throw new IllegalStateException("Không xác định được thư mục backup SQL Server");
                String tep=dir+(dir.endsWith("\\")?"":"\\")+db+"_before_real_products_"+LocalDateTime.now().toString().replaceAll("[^0-9]","")+".bak";
                String literal="N'"+tep.replace("'","''")+"'";
                s.execute("BACKUP DATABASE "+dinhDanh(db)+" TO DISK="+literal+" WITH COPY_ONLY,CHECKSUM");
                s.execute("RESTORE VERIFYONLY FROM DISK="+literal+" WITH CHECKSUM");
                Files.writeString(Path.of("target/product-import/database-backup.txt"),tep);
                System.out.println("Backup verified: "+tep);
            }
            chay(c,docKhoi("database/001_schema.sql","-- REAL_CATALOG_V1:"));
            List<String> tables=new ArrayList<>();
            try(var r=s.executeQuery("SELECT t.name FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id AND c.name='id' WHERE SCHEMA_NAME(t.schema_id)='dbo' ORDER BY t.name")){while(r.next())tables.add(r.getString(1));}
            Map<String,DauVan> truoc=new TreeMap<>();for(String t:tables)truoc.put(t,chup(c,t,Long.MAX_VALUE));
            c.setAutoCommit(false);
            try {
                chay(c,docKhoi("database/002_sample_data.sql","-- REAL_DATA_V1:"));
                for(var e:truoc.entrySet())if(!chup(c,e.getKey(),e.getValue().idLonNhat()).bam().equals(e.getValue().bam()))throw new IllegalStateException("Bản ghi cũ thay đổi: "+e.getKey());
                c.commit();
            }catch(Exception e){c.rollback();throw e;}
            System.out.println("Preserved all previous rows in "+tables.size()+" tables.");
            Map<String,DauVan> lanMot=new TreeMap<>();for(String t:tables)lanMot.put(t,chup(c,t,Long.MAX_VALUE));
            chay(c,docKhoi("database/002_sample_data.sql","-- REAL_DATA_V1:"));
            for(var e:lanMot.entrySet())if(!chup(c,e.getKey(),Long.MAX_VALUE).equals(e.getValue()))throw new IllegalStateException("Chạy lại làm thay đổi dữ liệu: "+e.getKey());
            c.commit();
            for(String table:List.of("san_pham","khach_hang","thu_cung","hoa_don","lo_hang"))try(var r=s.executeQuery("SELECT COUNT(*) FROM "+table)){r.next();System.out.println(table+": "+r.getLong(1));}
            System.out.println("Second import is unchanged: idempotency verified.");
        }
    }
}
