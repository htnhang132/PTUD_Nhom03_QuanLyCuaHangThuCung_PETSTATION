package com.petstation.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

class KienTrucPhanTangTest {
    private final Path nguon = Path.of("src/main/java/com/petstation");

    private void kiemTraPhuThuoc(String thuMuc, String... goiCam) throws Exception {
        List<String> loi = new ArrayList<>();
        try (var files = Files.walk(nguon.resolve(thuMuc))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String code = Files.readString(file);
                for (String goi : goiCam) {
                    if (code.contains(goi + ".")) loi.add(file.getFileName() + " -> " + goi);
                }
            }
        }
        assertThat(loi).as("Phụ thuộc sai tầng").isEmpty();
    }

    @Test
    void modelKhongGoiTangTren() throws Exception {
        kiemTraPhuThuoc(
                "model",
                "com.petstation.gui",
                "com.petstation.service",
                "com.petstation.dao",
                "com.petstation.database",
                "javax.swing",
                "java.awt");
    }

    @Test
    void daoKhongGoiServiceHoacGui() throws Exception {
        kiemTraPhuThuoc(
                "dao", "com.petstation.service", "com.petstation.gui", "javax.swing", "java.awt");
    }

    @Test
    void serviceKhongGoiSwingHoacThaoTacSQL() throws Exception {
        kiemTraPhuThuoc(
                "service",
                "com.petstation.gui",
                "javax.swing",
                "java.awt",
                "com.petstation.database.ThaoTacSQL",
                "java.sql.Date",
                "java.sql.Timestamp");
    }

    @Test
    void guiKhongGoiDaoKetNoiHoacJavaSql() throws Exception {
        kiemTraPhuThuoc("gui", "com.petstation.dao", "com.petstation.database", "java.sql");
    }

    @Test
    void dtoKhongGoiTangKhac() throws Exception {
        kiemTraPhuThuoc(
                "dto",
                "com.petstation.gui",
                "com.petstation.service",
                "com.petstation.dao",
                "com.petstation.database",
                "javax.swing",
                "java.awt");
    }

    @Test
    void daoVaImplDatTenDungQuyUoc() throws Exception {
        try (var files = Files.walk(nguon.resolve("dao"))) {
            for (Path file :
                    files.filter(p -> p.getFileName().toString().endsWith("DAOImpl.java"))
                            .toList()) {
                String ten = file.getFileName().toString().replace(".java", "");
                assertThat(ten).endsWith("DAOImpl").doesNotStartWith("Jdbc");
                String dao = ten.replaceFirst("DAOImpl$", "DAO");
                Path daoFile = file.getParent().getParent().resolve(dao + ".java");
                if (!Files.isRegularFile(daoFile)) {
                    daoFile = nguon.resolve("dao/" + dao + ".java");
                }
                assertThat(Files.isRegularFile(daoFile))
                        .as("Interface %s.java phải tồn tại cạnh package impl của %s", dao, ten)
                        .isTrue();
                assertThat(Files.readString(file)).contains("implements " + dao);
            }
        }
    }

    @Test
    void thanCodeKhongDungTenDayDu() throws Exception {
        List<String> tienToCam =
                List.of("com.petstation.", "java.util.", "java.time.", "javax.swing.");
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                List<String> dong = Files.readAllLines(file);
                for (int i = 0; i < dong.size(); i++) {
                    String s = dong.get(i).trim();
                    if (s.startsWith("package ")
                            || s.startsWith("import ")
                            || s.startsWith("//")
                            || s.startsWith("/*")
                            || s.startsWith("*")) {
                        continue;
                    }
                    String khongChuoi = s.replaceAll("\"([^\"\\\\]|\\\\.)*\"", "\"\"");
                    for (String tienTo : tienToCam) {
                        if (khongChuoi.contains(tienTo)) {
                            viPham.add(nguon.relativize(file) + ":" + (i + 1) + " chứa " + tienTo);
                        }
                    }
                }
            }
        }
        assertThat(viPham).as("Không dùng tên đầy đủ trong thân code, hãy thêm import").isEmpty();
    }

    @Test
    void serviceKhongDungCauHinhDaoHoacDaoStatic() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon.resolve("service"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                List<String> dong = Files.readAllLines(file);
                for (int i = 0; i < dong.size(); i++) {
                    String s = dong.get(i).trim();
                    if (s.contains("cauHinhDAO(")) {
                        viPham.add(nguon.relativize(file) + ":" + (i + 1) + " còn cauHinhDAO");
                    }
                    if (s.matches("private\\s+static\\s+[^({]+DAO\\b.*")) {
                        viPham.add(nguon.relativize(file) + ":" + (i + 1) + " còn DAO static");
                    }
                }
            }
        }
        assertThat(viPham).as("Service dùng constructor DI, không giữ DAO static").isEmpty();
    }

    @Test
    void suKienDungBoLangNgheCoKieu() throws Exception {
        List<String> viPham = new ArrayList<>();
        for (String tang : List.of("dto", "service")) {
            Path thuMuc = nguon.resolve(tang);
            if (!Files.exists(thuMuc)) continue;
            try (var files = Files.walk(thuMuc)) {
                for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                    List<String> dong = Files.readAllLines(file);
                    for (int i = 0; i < dong.size(); i++) {
                        if (dong.get(i).contains("List<Runnable>")) {
                            viPham.add(
                                    nguon.relativize(file)
                                            + ":"
                                            + (i + 1)
                                            + " dùng List<Runnable>");
                        }
                    }
                }
            }
        }
        assertThat(viPham)
                .as(
                        "Publisher trong dto/service dùng BoLangNgheSuKien<T>, không dùng"
                                + " List<Runnable>")
                .isEmpty();
    }

    @Test
    void daoBanHangKhoCoKieuRoRang() throws Exception {
        List<String> viPham = new ArrayList<>();
        for (String module : List.of("banhang", "kho")) {
            Path thuMuc = nguon.resolve("dao").resolve(module);
            if (!Files.exists(thuMuc)) continue;
            try (var files = Files.walk(thuMuc)) {
                for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                    boolean laImpl = file.getParent().getFileName().toString().equals("impl");
                    List<String> dong = Files.readAllLines(file);
                    for (int i = 0; i < dong.size(); i++) {
                        String s = dong.get(i).trim();
                        if (s.startsWith("//")) continue;
                        boolean coChuKy = !laImpl || s.startsWith("public ");
                        if (coChuKy
                                && (s.contains("Object...")
                                        || s.contains("Object[]")
                                        || s.matches(".*\\bObject\\b.*"))) {
                            viPham.add(nguon.relativize(file) + ":" + (i + 1) + " -> " + s);
                        }
                    }
                }
            }
        }
        assertThat(viPham)
                .as("DAO banhang/kho không dùng Object/Object[]/Object... trong chữ ký")
                .isEmpty();
    }

    @Test
    void daoToanHeThongCoKieuRoRang() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon.resolve("dao"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                boolean laImpl = file.getParent().getFileName().toString().equals("impl");
                List<String> dong = Files.readAllLines(file);
                for (int i = 0; i < dong.size(); i++) {
                    String s = dong.get(i).trim();
                    if (s.startsWith("//")) continue;
                    boolean coChuKy = !laImpl || s.startsWith("public ");
                    if (coChuKy
                            && (s.contains("Object...")
                                    || s.contains("Object[]")
                                    || s.matches(".*\\bObject\\b.*"))) {
                        viPham.add(nguon.relativize(file) + ":" + (i + 1) + " -> " + s);
                    }
                }
            }
        }
        assertThat(viPham)
                .as(
                        "Toàn bộ DAO (interface và public method impl) không dùng"
                                + " Object/Object[]/Object...")
                .isEmpty();
    }

    @Test
    void khongConRecordTrongSrcMain() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var luong = Files.walk(nguon)) {
            for (Path p : luong.filter(f -> f.toString().endsWith(".java")).toList()) {
                String sach =
                        Files.readString(p)
                                .replaceAll(
                                        "(?s)/\\*.*?\\*/|//[^\\n]*|\"(?:\\\\.|[^\"\\\\])*\"", "");
                if (sach.matches("(?s).*\\brecord\\s+[A-Z].*")) {
                    viPham.add(nguon.relativize(p).toString());
                }
            }
        }
        assertThat(viPham).as("Không còn khai báo record trong src/main").isEmpty();
    }

    @Test
    void khongCoClassHoacEnumKhongDuocSuDung() throws Exception {
        List<Path> cacFile = new ArrayList<>();
        List<String> noiDung = new ArrayList<>();
        try (var luong = Files.walk(Path.of("src"))) {
            for (Path p : luong.filter(f -> f.toString().endsWith(".java")).toList()) {
                cacFile.add(p);
                noiDung.add(Files.readString(p));
            }
        }
        List<String> viPham = new ArrayList<>();
        for (int i = 0; i < cacFile.size(); i++) {
            Path p = cacFile.get(i);
            if (!p.startsWith(nguon)) continue;
            String tenFile = p.getFileName().toString();
            String tenLop = tenFile.substring(0, tenFile.length() - 5);
            if ("App".equals(tenLop) || "package-info".equals(tenLop)) continue;
            boolean duocDung = false;
            for (int j = 0; j < cacFile.size(); j++) {
                if (i == j) continue;
                String s = noiDung.get(j);
                if (s.contains(tenLop) && s.matches("(?s).*\\b" + tenLop + "\\b.*")) {
                    duocDung = true;
                    break;
                }
            }
            if (!duocDung) viPham.add(nguon.relativize(p).toString());
        }
        assertThat(viPham).as("Không còn class/enum/interface top-level không được dùng").isEmpty();
    }

    @Test
    void khongCoImportSaoHoacImportKhongDung() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var luong = Files.walk(nguon)) {
            for (Path file : luong.filter(p -> p.toString().endsWith(".java")).toList()) {
                String tho = Files.readString(file);
                String sach = xoaChuoiVaChuThich(tho);
                List<String> nhap = new ArrayList<>();
                StringBuilder than = new StringBuilder();
                for (String dong : sach.split("\n", -1)) {
                    String s = dong.trim();
                    if (s.startsWith("import ") && s.endsWith(";")) {
                        nhap.add(s);
                    } else if (!s.startsWith("package ")) {
                        than.append(dong).append('\n');
                    }
                }
                String noiDungThan = than.toString();
                for (String s : nhap) {
                    String muc = s.substring("import ".length(), s.length() - 1).trim();
                    if (muc.startsWith("static ")) {
                        muc = muc.substring("static ".length()).trim();
                    }
                    if (muc.endsWith(".*")) {
                        viPham.add(nguon.relativize(file) + " có wildcard: " + s);
                        continue;
                    }
                    int cham = muc.lastIndexOf('.');
                    String ten = cham >= 0 ? muc.substring(cham + 1) : muc;
                    if (!chuaTuKhoa(noiDungThan, ten)) {
                        viPham.add(nguon.relativize(file) + " import thừa: " + s);
                    }
                }
            }
        }
        assertThat(viPham)
                .as("Không dùng import * và không để import thừa trong src/main")
                .isEmpty();
    }

    @Test
    void khongTrungMauTienIchTapTrung() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var luong = Files.walk(nguon)) {
            for (Path file : luong.filter(p -> p.toString().endsWith(".java")).toList()) {
                String tenFile = file.getFileName().toString();
                String[] dong = xoaChuoiVaChuThich(Files.readString(file)).split("\n", -1);
                for (int i = 0; i < dong.length; i++) {
                    String s = dong[i];
                    if (!"DinhDang.java".equals(tenFile)
                            && (s.contains("DateTimeFormatter.ofPattern")
                                    || s.contains("DecimalFormat"))) {
                        viPham.add(
                                nguon.relativize(file)
                                        + ":"
                                        + (i + 1)
                                        + " định dạng ngoài DinhDang");
                    }
                    if (!"MauUngDung.java".equals(tenFile) && s.contains("new Color(")) {
                        viPham.add(nguon.relativize(file) + ":" + (i + 1) + " dùng new Color(");
                    }
                    if (!"TacVuNen.java".equals(tenFile) && s.contains("new SwingWorker")) {
                        viPham.add(nguon.relativize(file) + ":" + (i + 1) + " dùng SwingWorker");
                    }
                    if (!"HopThoai.java".equals(tenFile) && s.contains("JOptionPane.")) {
                        viPham.add(nguon.relativize(file) + ":" + (i + 1) + " dùng JOptionPane");
                    }
                }
            }
        }
        assertThat(viPham)
                .as("Định dạng, màu, SwingWorker và JOptionPane phải đi qua lớp dùng chung")
                .isEmpty();
    }

    @Test
    void khongCoPhuongThucQua80Dong() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var luong = Files.walk(nguon)) {
            for (Path file : luong.filter(p -> p.toString().endsWith(".java")).toList()) {
                kiemTraPhuongThucQuaDai(file, viPham);
            }
        }
        assertThat(viPham).as("Mọi phương thức trong src/main không vượt quá 80 dòng").isEmpty();
    }

    @Test
    void khongConTienToHungariaTrongTenBien() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var luong = Files.walk(nguon)) {
            for (Path file : luong.filter(p -> p.toString().endsWith(".java")).toList()) {
                String[] dong = xoaChuoiVaChuThich(Files.readString(file)).split("\n", -1);
                for (int i = 0; i < dong.length; i++) {
                    String s = dong[i].trim();
                    if (s.startsWith("package ") || s.startsWith("import ")) continue;
                    StringBuilder tu = new StringBuilder();
                    for (int k = 0; k <= s.length(); k++) {
                        char c = k < s.length() ? s.charAt(k) : ' ';
                        if (laKyTuTu(c)) {
                            tu.append(c);
                        } else if (!tu.isEmpty()) {
                            String id = tu.toString();
                            boolean tienTo =
                                    id.equals("txt")
                                            || id.equals("pnl")
                                            || id.equals("tbl")
                                            || id.equals("lbl")
                                            || ((id.startsWith("txt")
                                                            || id.startsWith("pnl")
                                                            || id.startsWith("tbl")
                                                            || id.startsWith("lbl"))
                                                    && Character.isUpperCase(id.charAt(3)));
                            if (tienTo) {
                                viPham.add(
                                        nguon.relativize(file) + ":" + (i + 1) + " còn tên " + id);
                            }
                            tu.setLength(0);
                        }
                    }
                }
            }
        }
        assertThat(viPham)
                .as("Không còn tiền tố txt/pnl/tbl/lbl trong mã nguồn src/main")
                .isEmpty();
    }

    private void kiemTraPhuongThucQuaDai(Path file, List<String> viPham) throws Exception {
        String[] dong =
                xoaChuoiVaChuThich(Files.readString(file).replace("\r\n", "\n")).split("\n", -1);
        int doSau = 0;
        List<Integer> doSauKieu = new ArrayList<>();
        doSauKieu.add(0);
        boolean trongHam = false;
        int doSauHam = -1;
        int dongBatDauHam = 0;
        StringBuilder dauHam = new StringBuilder();
        int dongChoHam = 0;
        for (int i = 0; i < dong.length; i++) {
            String s = dong[i].trim();
            int soDong = i + 1;
            if (doSauKieu.contains(doSau) && !trongHam && !s.isEmpty()) {
                if (dauHam.isEmpty()) dongChoHam = soDong;
                dauHam.append(' ').append(s);
                if (s.contains(";") && !s.contains("{")) {
                    dauHam.setLength(0);
                }
            }
            for (int k = 0; k < dong[i].length(); k++) {
                char c = dong[i].charAt(k);
                if (c == '{') {
                    if (doSauKieu.contains(doSau)) {
                        String hdr = dauHam.toString().trim();
                        int ngoacMo = hdr.indexOf('(');
                        String truocMo = ngoacMo >= 0 ? hdr.substring(0, ngoacMo) : hdr;
                        if (chuaKhaiBaoKieu(hdr) && !truocMo.contains("(")) {
                            doSauKieu.add(doSau + 1);
                            dauHam.setLength(0);
                        } else if (ngoacMo >= 0
                                && hdr.contains(")")
                                && !hdr.startsWith("static {")
                                && !truocMo.contains("=")) {
                            trongHam = true;
                            doSauHam = doSau + 1;
                            dongBatDauHam = dongChoHam;
                            dauHam.setLength(0);
                        } else {
                            if (chuaKhaiBaoKieu(hdr)) {
                                doSauKieu.add(doSau + 1);
                            }
                            dauHam.setLength(0);
                        }
                    }
                    doSau++;
                } else if (c == '}') {
                    if (trongHam && doSau == doSauHam) {
                        int doDai = soDong - dongBatDauHam + 1;
                        if (doDai > 80) {
                            viPham.add(
                                    nguon.relativize(file)
                                            + ":"
                                            + dongBatDauHam
                                            + "-"
                                            + soDong
                                            + " ("
                                            + doDai
                                            + " dòng)");
                        }
                        trongHam = false;
                    }
                    doSauKieu.remove(Integer.valueOf(doSau));
                    doSau--;
                    if (doSauKieu.contains(doSau)) {
                        dauHam.setLength(0);
                    }
                }
            }
        }
    }

    private static boolean chuaKhaiBaoKieu(String hdr) {
        return chuaTuKhoa(hdr, "class")
                || chuaTuKhoa(hdr, "interface")
                || chuaTuKhoa(hdr, "enum")
                || chuaTuKhoa(hdr, "record");
    }

    private static boolean chuaTuKhoa(String noiDung, String tu) {
        int viTri = 0;
        while ((viTri = noiDung.indexOf(tu, viTri)) >= 0) {
            boolean dau = viTri == 0 || !laKyTuTu(noiDung.charAt(viTri - 1));
            int sau = viTri + tu.length();
            boolean cuoi = sau >= noiDung.length() || !laKyTuTu(noiDung.charAt(sau));
            if (dau && cuoi) return true;
            viTri += tu.length();
        }
        return false;
    }

    private static boolean laKyTuTu(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '$';
    }

    private static String xoaChuoiVaChuThich(String src) {
        StringBuilder out = new StringBuilder(src.length());
        int i = 0;
        int n = src.length();
        while (i < n) {
            char c = src.charAt(i);
            if (c == '/' && i + 1 < n && src.charAt(i + 1) == '*') {
                out.append("  ");
                i += 2;
                while (i < n && !(src.charAt(i) == '*' && i + 1 < n && src.charAt(i + 1) == '/')) {
                    out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                    i++;
                }
                if (i + 1 < n) {
                    out.append("  ");
                    i += 2;
                }
            } else if (c == '/' && i + 1 < n && src.charAt(i + 1) == '/') {
                while (i < n && src.charAt(i) != '\n') {
                    out.append(' ');
                    i++;
                }
            } else if (src.startsWith("\"\"\"", i)) {
                out.append("   ");
                i += 3;
                while (i < n && !src.startsWith("\"\"\"", i)) {
                    out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                    i++;
                }
                if (i < n) {
                    out.append("   ");
                    i += 3;
                }
            } else if (c == '"' || c == '\'') {
                char q = c;
                out.append(q);
                i++;
                while (i < n && src.charAt(i) != q && src.charAt(i) != '\n') {
                    if (src.charAt(i) == '\\' && i + 1 < n) {
                        out.append("  ");
                        i += 2;
                    } else {
                        out.append(' ');
                        i++;
                    }
                }
                if (i < n && src.charAt(i) == q) {
                    out.append(q);
                    i++;
                }
            } else {
                out.append(c);
                i++;
            }
        }
        return out.toString();
    }

    @Test
    void serviceChungKhongPhuThuocDaoNghiepVu() throws Exception {
        kiemTraPhuThuoc(
                "service/chung",
                "com.petstation.dao.banhang",
                "com.petstation.dao.kho",
                "com.petstation.dao.luutru",
                "com.petstation.dao.spa",
                "com.petstation.dao.nhansu",
                "com.petstation.dao.khachhang",
                "com.petstation.dao.pheduyet",
                "com.petstation.dao.thongke",
                "com.petstation.dao.hethong");
    }

    @Test
    void khongConLayIdCauNoiVaPatindex() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String code = Files.readString(file);
                if (code.contains("layIdCauNoi") || code.contains("PATINDEX")) {
                    viPham.add(nguon.relativize(file).toString());
                }
            }
        }
        assertThat(viPham).as("Không được dùng layIdCauNoi hoặc PATINDEX").isEmpty();
    }

    @Test
    void khongConCatchThrowableVaCatchRong() throws Exception {
        Pattern catchThrowable = Pattern.compile("catch\\s*\\(\\s*Throwable\\b");
        Pattern catchRong = Pattern.compile("catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}");
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String code = xoaChuoiVaChuThich(Files.readString(file));
                if (catchThrowable.matcher(code).find() || catchRong.matcher(code).find()) {
                    viPham.add(nguon.relativize(file).toString());
                }
            }
        }
        assertThat(viPham).as("Không được catch Throwable hoặc catch rỗng").isEmpty();
    }

    @Test
    void khongThemFileSqlHoacDdlTuDong() throws Exception {
        try (var sqlFiles = Files.list(Path.of("database"))) {
            List<String> tenFiles = sqlFiles.map(p -> p.getFileName().toString()).toList();
            assertThat(tenFiles)
                    .as("Chỉ được giữ 2 file SQL gốc")
                    .containsExactlyInAnyOrder("001_schema.sql", "002_sample_data.sql");
        }
        Pattern ddlTuDong =
                Pattern.compile(
                        "\\b(CREATE\\s+TABLE|CREATE\\s+SEQUENCE|ALTER\\s+TABLE|DROP\\s+TABLE|DROP\\s+SEQUENCE)\\b");
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon)) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String code = Files.readString(file);
                if (ddlTuDong.matcher(code).find()) {
                    viPham.add(nguon.relativize(file).toString());
                }
            }
        }
        assertThat(viPham).as("Không được chạy DDL tự động trong Java").isEmpty();
    }

    @Test
    void daoKhongDungDoubleChoTienVaDonGia() throws Exception {
        List<String> viPham = new ArrayList<>();
        try (var files = Files.walk(nguon.resolve("dao"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".java")).toList()) {
                String code = xoaChuoiVaChuThich(Files.readString(file));
                if (code.contains("double donGia")
                        || code.contains("setDouble")
                        || code.contains("getDouble")) {
                    viPham.add(nguon.relativize(file).toString());
                }
            }
        }
        assertThat(viPham).as("DAO không được dùng double cho tiền hoặc đơn giá").isEmpty();
    }
}
