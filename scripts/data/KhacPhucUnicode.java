import com.petstation.dao.CauHinhCSDL;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/** Nâng cấp Unicode có backup; đối chiếu tài chính, quyền và cấu hình trước/sau. */
class KhacPhucUnicode {
    static Map<String, String> chup(Connection c, Map<String, List<String>> cot) throws Exception {
        var kq = ChuanHoaDuLieu.chup(c, cot);
        StringBuilder chu = new StringBuilder();
        try (var s = c.createStatement();
                var r =
                        s.executeQuery(
                                "SELECT khoa,gia_tri FROM cai_dat WHERE khoa NOT"
                                    + " IN('UI_DATA_CLEANUP_V1_APPLIED_AT','UI_UNICODE_REPAIR_V1_APPLIED_AT','UI_UNICODE_PET_V1_APPLIED_AT')"
                                    + " ORDER BY khoa")) {
            while (r.next())
                chu.append(r.getString(1)).append('\0').append(r.getString(2)).append('\0');
        }
        kq.put(
                "protected:cai_dat",
                HexFormat.of()
                        .formatHex(
                                java.security.MessageDigest.getInstance("SHA-256")
                                        .digest(chu.toString().getBytes(StandardCharsets.UTF_8))));
        return kq;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !Set.of("--test", "--apply", "--scan").contains(args[0]))
            throw new IllegalArgumentException("Dùng --test, --apply hoặc --scan");
        boolean test = args[0].equals("--test");
        var cfg = CauHinhCSDL.nap();
        String url =
                test
                        ? cfg.url()
                                .replaceAll(
                                        "(?i)databaseName=[^;]+",
                                        "databaseName=PetStationIntegrationTest")
                        : cfg.url();
        Files.createDirectories(Path.of("target/ui-repair"));
        try (var c = DriverManager.getConnection(url, cfg.nguoiDung(), cfg.matKhau());
                var s = c.createStatement()) {
            String db;
            try (var r = s.executeQuery("SELECT DB_NAME()")) {
                r.next();
                db = r.getString(1);
            }
            if (Set.of("master", "model", "msdb", "tempdb").contains(db.toLowerCase())
                    || (test && !db.equals("PetStationIntegrationTest")))
                throw new IllegalStateException("Sai database");
            System.out.println("Database: " + db);
            if (!args[0].equals("--scan")) {
                if (!test) {
                    String dir;
                    try (var r =
                            s.executeQuery(
                                    "SELECT"
                                        + " CONVERT(nvarchar(4000),SERVERPROPERTY('InstanceDefaultBackupPath'))")) {
                        r.next();
                        dir = r.getString(1);
                    }
                    if (dir == null) throw new IllegalStateException("Thiếu đường dẫn backup");
                    String path =
                            dir
                                    + (dir.endsWith("\\") ? "" : "\\")
                                    + db
                                    + "_before_unicode_"
                                    + LocalDateTime.now().toString().replaceAll("[^0-9]", "")
                                    + ".bak";
                    String literal = "N'" + path.replace("'", "''") + "'";
                    s.execute(
                            "BACKUP DATABASE "
                                    + ChuanHoaDuLieu.ten(db)
                                    + " TO DISK="
                                    + literal
                                    + " WITH COPY_ONLY,CHECKSUM");
                    s.execute("RESTORE VERIFYONLY FROM DISK=" + literal + " WITH CHECKSUM");
                    Files.writeString(Path.of("target/ui-repair/database-backup.txt"), path);
                    System.out.println("Backup CHECKSUM / VERIFYONLY: OK");
                }
                var cot = ChuanHoaDuLieu.cotBaoToan(c);
                var truoc = chup(c, cot);
                c.setAutoCommit(false);
                try {
                    ChuanHoaDuLieu.chay(
                            c,
                            ChuanHoaDuLieu.khoi(
                                    "database/001_schema.sql", "-- UI_UNICODE_SCHEMA_V1:"));
                    ChuanHoaDuLieu.chay(
                            c,
                            ChuanHoaDuLieu.khoi(
                                    "database/002_sample_data.sql", "-- UI_UNICODE_REPAIR_V1:"));
                    if (!truoc.equals(chup(c, cot)))
                        throw new IllegalStateException("Sai lệch số dư, quyền hoặc cấu hình");
                    c.commit();
                } catch (Exception e) {
                    c.rollback();
                    throw e;
                }
                var duCot = ChuanHoaDuLieu.tatCaCot(c);
                var mot = chup(c, duCot);
                ChuanHoaDuLieu.chay(
                        c,
                        ChuanHoaDuLieu.khoi("database/001_schema.sql", "-- UI_UNICODE_SCHEMA_V1:"));
                ChuanHoaDuLieu.chay(c, Files.readString(Path.of("database/002_sample_data.sql")));
                if (!mot.equals(chup(c, duCot))) {
                    c.rollback();
                    throw new IllegalStateException("Chạy lại đổi dữ liệu");
                }
                c.commit();
                Files.writeString(
                        Path.of("target/ui-repair/doi-chieu-" + (test ? "test" : "live") + ".txt"),
                        "Database: "
                                + db
                                + "\nTrước: "
                                + truoc
                                + "\nSau: "
                                + chup(c, cot)
                                + "\nChạy lại: không đổi.\n");
                System.out.println(
                        "Đối chiếu tiền/tồn/điểm/cọc/công nợ/quyền/cấu hình và chạy lại: OK");
            }
            List<String[]> cacCot = new ArrayList<>();
            try (var r =
                    s.executeQuery(
                            "SELECT t.name,c.name FROM sys.tables t JOIN sys.columns c ON"
                                + " c.object_id=t.object_id JOIN sys.types ty ON"
                                + " ty.user_type_id=c.user_type_id WHERE"
                                + " SCHEMA_NAME(t.schema_id)='dbo' AND ty.name"
                                + " IN('nvarchar','varchar') AND t.name NOT"
                                + " IN('tai_khoan','cai_dat','doi_chieu_unicode','doi_chieu_ma_du_lieu','nhat_ky_he_thong')"
                                + " AND c.name NOT LIKE '%url%' AND c.name NOT"
                                + " IN('payload','idempotency_key')")) {
                while (r.next()) cacCot.add(new String[] {r.getString(1), r.getString(2)});
            }
            StringBuilder loi =
                    new StringBuilder("# Trường văn bản còn dấu hỏi hoặc ký tự thay thế\n\n");
            int so = 0;
            for (var cot : cacCot) {
                String sql =
                        "SELECT COUNT(*) FROM "
                                + ChuanHoaDuLieu.ten(cot[0])
                                + " WHERE CHARINDEX(N'?',"
                                + ChuanHoaDuLieu.ten(cot[1])
                                + " COLLATE Latin1_General_100_BIN2)>0 OR CHARINDEX(NCHAR(65533),"
                                + ChuanHoaDuLieu.ten(cot[1])
                                + " COLLATE Latin1_General_100_BIN2)>0";
                try (var r = s.executeQuery(sql)) {
                    r.next();
                    if (r.getLong(1) > 0) {
                        loi.append("- ")
                                .append(cot[0])
                                .append('.')
                                .append(cot[1])
                                .append(": ")
                                .append(r.getLong(1))
                                .append(" bản ghi cần đối chiếu nguồn.\n");
                        so++;
                    }
                }
            }
            if (so == 0) loi.append("Không còn trường nghiệp vụ có dấu hỏi hoặc ký tự thay thế.\n");
            Files.writeString(
                    Path.of("target/ui-repair/unicode-" + (test ? "test" : "live") + ".md"), loi);
            try (var r = s.executeQuery("SELECT COUNT(*) FROM doi_chieu_unicode")) {
                r.next();
                System.out.println("Chuỗi Unicode đã phục hồi: " + r.getLong(1));
            }
            System.out.println("Trường văn bản cần đối chiếu thêm: " + so);
        }
    }
}
