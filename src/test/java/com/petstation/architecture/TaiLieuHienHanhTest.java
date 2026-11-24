package com.petstation.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import com.petstation.model.chung.MaManHinh;

import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Pattern;

class TaiLieuHienHanhTest {
    @Test
    void lienKetNoiBoTrongTaiLieuTroDenFileCoThat() throws Exception {
        List<Path> taiLieu = new ArrayList<>();
        try (var files = Files.list(Path.of("."))) {
            taiLieu.addAll(files.filter(p -> p.toString().endsWith(".md")).toList());
        }
        try (var files = Files.walk(Path.of("docs"))) {
            taiLieu.addAll(files.filter(p -> p.toString().endsWith(".md")).toList());
        }
        List<String> loi = new ArrayList<>();
        Pattern link = Pattern.compile("\\]\\(([^)]+)\\)");
        for (Path file : taiLieu) {
            var matcher = link.matcher(Files.readString(file));
            while (matcher.find()) {
                String dich = matcher.group(1).split("#", 2)[0].replaceAll("^<|>$", "");
                if (dich.isEmpty() || dich.matches("^(https?:|mailto:|data:).*$")) continue;
                dich = URLDecoder.decode(dich, StandardCharsets.UTF_8);
                if (!Files.exists(file.getParent().resolve(dich).normalize()))
                    loi.add(file + " -> " + dich);
            }
        }
        assertThat(loi).as("Liên kết nội bộ trong tài liệu").isEmpty();
    }

    @Test
    void banDoTruyVetBaoPhuCacMaManHinhHienTai() throws Exception {
        String banDo = Files.readString(Path.of("docs/BAN_DO_TRUY_VET_CODE.md"));
        for (MaManHinh ma : MaManHinh.values()) assertThat(banDo).contains("`" + ma.name() + "`");
    }

    @Test
    void anhNguonVanDungNoiDungSauKhiDoiTenTep() throws Exception {
        String json = Files.readString(Path.of("docs/data/san-pham-nguon.json"));
        var objects = Pattern.compile("(?s)\\{(.*?)\\}").matcher(json);
        int soAnh = 0;
        while (objects.find()) {
            var anh = Pattern.compile("\"anh\"\\s*:\\s*\"([^\"]+)\"").matcher(objects.group(1));
            var sha = Pattern.compile("\"sha256\"\\s*:\\s*\"([^\"]+)\"").matcher(objects.group(1));
            assertThat(anh.find()).isTrue();
            assertThat(sha.find()).isTrue();
            byte[] bytes = Files.readAllBytes(Path.of("src/main/resources").resolve(anh.group(1)));
            String thucTe =
                    HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            assertThat(thucTe).as(anh.group(1)).isEqualTo(sha.group(1));
            soAnh++;
        }
        assertThat(soAnh).isEqualTo(60);
    }
}
