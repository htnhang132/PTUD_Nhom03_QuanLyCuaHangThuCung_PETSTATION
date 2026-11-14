package com.petstation.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class KiemTraThamSoSQLTest {

    @Test
    @DisplayName("Tất cả câu lệnh ThaoTacSQL trong toàn bộ DAO phải khớp số lượng tham số '?'")
    void kiemTraKhopThamSoTrongTatCaDAO() throws IOException {
        Path thuMucDAO = Path.of("src", "main", "java", "com", "petstation", "dao");
        assertTrue(Files.exists(thuMucDAO), "Thư mục DAO phải tồn tại");

        List<File> danhSachTep = new ArrayList<>();
        timTepJava(thuMucDAO.toFile(), danhSachTep);
        assertTrue(!danhSachTep.isEmpty(), "Phải có các tệp DAO");

        List<String> cacLoi = new ArrayList<>();

        for (File tep : danhSachTep) {
            String noiDung = Files.readString(tep.toPath());
            kiemTraTep(tep.getName(), noiDung, cacLoi);
        }

        assertEquals(
                0,
                cacLoi.size(),
                "Phát hiện lỗi không khớp tham số SQL:\n" + String.join("\n", cacLoi));
    }

    private void timTepJava(File thuMuc, List<File> ketQua) {
        File[] cacTep = thuMuc.listFiles();
        if (cacTep == null) return;
        for (File t : cacTep) {
            if (t.isDirectory()) {
                timTepJava(t, ketQua);
            } else if (t.getName().endsWith(".java")) {
                ketQua.add(t);
            }
        }
    }

    private void kiemTraTep(String tenTep, String noiDung, List<String> cacLoi) {
        Pattern pattern =
                Pattern.compile(
                        "(?:ThaoTacSQL\\s*\\.\\s*|(?:this|super)\\s*\\.\\s*|\\b)(truyVan|truyVanDanhSach|capNhat|laySo|layGiaTriDon|thucThi|them)\\s*\\(");
        Matcher matcher = pattern.matcher(noiDung);

        while (matcher.find()) {
            String tenHam = matcher.group(1);
            int argsStart = matcher.end();
            int pDepth = 1;
            int curr = argsStart;
            boolean inStr = false;
            boolean inTripleStr = false;
            boolean esc = false;

            while (curr < noiDung.length() && pDepth > 0) {
                if (!inStr && !inTripleStr) {
                    if (curr + 3 <= noiDung.length() && noiDung.startsWith("\"\"\"", curr)) {
                        inTripleStr = true;
                        curr += 2;
                    } else if (noiDung.charAt(curr) == '"') {
                        inStr = true;
                    } else if (noiDung.charAt(curr) == '(') {
                        pDepth++;
                    } else if (noiDung.charAt(curr) == ')') {
                        pDepth--;
                    }
                } else if (inTripleStr) {
                    if (curr + 3 <= noiDung.length() && noiDung.startsWith("\"\"\"", curr)) {
                        inTripleStr = false;
                        curr += 2;
                    }
                } else if (inStr) {
                    if (esc) {
                        esc = false;
                    } else if (noiDung.charAt(curr) == '\\') {
                        esc = true;
                    } else if (noiDung.charAt(curr) == '"') {
                        inStr = false;
                    }
                }
                curr++;
            }

            if (pDepth != 0) continue;

            String callArgs = noiDung.substring(argsStart, curr - 1).trim();
            kiemTraLoiGoi(tenTep, tenHam, callArgs, cacLoi);
        }
    }

    private void kiemTraLoiGoi(String tenTep, String tenHam, String callArgs, List<String> cacLoi) {
        // Tách đối số đầu tiên (câu lệnh SQL)
        int pD = 0;
        int bD = 0;
        int cD = 0;
        boolean inS = false;
        boolean inTS = false;
        int commaPos = -1;

        for (int i = 0; i < callArgs.length(); i++) {
            if (!inS && !inTS) {
                if (i + 3 <= callArgs.length() && callArgs.startsWith("\"\"\"", i)) {
                    inTS = true;
                } else if (callArgs.charAt(i) == '"') {
                    inS = true;
                } else if (callArgs.charAt(i) == '('
                        || callArgs.charAt(i) == '['
                        || callArgs.charAt(i) == '{') {
                    pD++;
                } else if (callArgs.charAt(i) == ')'
                        || callArgs.charAt(i) == ']'
                        || callArgs.charAt(i) == '}') {
                    pD--;
                } else if (callArgs.charAt(i) == ',' && pD == 0 && bD == 0 && cD == 0) {
                    commaPos = i;
                    break;
                }
            } else if (inTS) {
                if (i + 3 <= callArgs.length() && callArgs.startsWith("\"\"\"", i)) {
                    inTS = false;
                }
            } else if (inS) {
                if (callArgs.charAt(i) == '"') {
                    inS = false;
                }
            }
        }

        String sqlArg = (commaPos != -1) ? callArgs.substring(0, commaPos).trim() : callArgs.trim();
        String restArgs = (commaPos != -1) ? callArgs.substring(commaPos + 1).trim() : "";

        // Đếm số lượng '?' trong chuỗi SQL literal
        int qCount = 0;
        Pattern strPattern =
                Pattern.compile("\"\"\"(.*?)\"\"\"|\"((?:[^\"\\\\]|\\\\.)*)\"", Pattern.DOTALL);
        Matcher sm = strPattern.matcher(sqlArg);
        while (sm.find()) {
            String s = sm.group(1) != null ? sm.group(1) : sm.group(2);
            for (char c : s.toCharArray()) {
                if (c == '?') qCount++;
            }
        }

        if (qCount == 0) return;

        // Phân tích các đối số truyền vào
        List<String> parsedArgs = new ArrayList<>();
        if (!restArgs.isEmpty()) {
            int start = 0;
            pD = 0;
            bD = 0;
            cD = 0;
            inS = false;
            inTS = false;

            for (int i = 0; i < restArgs.length(); i++) {
                if (!inS && !inTS) {
                    if (i + 3 <= restArgs.length() && restArgs.startsWith("\"\"\"", i)) {
                        inTS = true;
                    } else if (restArgs.charAt(i) == '"') {
                        inS = true;
                    } else if (restArgs.charAt(i) == '('
                            || restArgs.charAt(i) == '['
                            || restArgs.charAt(i) == '{') {
                        pD++;
                    } else if (restArgs.charAt(i) == ')'
                            || restArgs.charAt(i) == ']'
                            || restArgs.charAt(i) == '}') {
                        pD--;
                    } else if (restArgs.charAt(i) == ',' && pD == 0 && bD == 0 && cD == 0) {
                        parsedArgs.add(restArgs.substring(start, i).trim());
                        start = i + 1;
                    }
                } else if (inTS) {
                    if (i + 3 <= restArgs.length() && restArgs.startsWith("\"\"\"", i)) {
                        inTS = false;
                    }
                } else if (inS) {
                    if (restArgs.charAt(i) == '"') {
                        inS = false;
                    }
                }
            }
            if (start < restArgs.length()) {
                parsedArgs.add(restArgs.substring(start).trim());
            }
        }

        if ("truyVanDanhSach".equals(tenHam) && !parsedArgs.isEmpty()) {
            parsedArgs = new ArrayList<>(parsedArgs.subList(1, parsedArgs.size()));
        }

        // Bỏ qua nếu truyền mảng biến động (varargs chuyển tiếp)
        if (parsedArgs.size() == 1) {
            String single = parsedArgs.get(0);
            if (single.equals("cacThamSo")
                    || single.equals("thamSo")
                    || single.equals("cacDoiSo")
                    || single.equals("cacDoiSo.toArray()")
                    || single.equals("thamSoDayDu")
                    || single.equals("args")
                    || single.equals("params")) {
                return;
            }
        }

        if (qCount != parsedArgs.size()) {
            cacLoi.add(
                    tenTep
                            + ": SQL có "
                            + qCount
                            + " dấu '?' nhưng truyền "
                            + parsedArgs.size()
                            + " đối số: ["
                            + String.join(", ", parsedArgs)
                            + "]");
        }
    }
}
