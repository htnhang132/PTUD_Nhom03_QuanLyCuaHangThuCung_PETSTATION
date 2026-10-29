package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.TiemPhongDAO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Hồ sơ tiêm phòng được lưu trực tiếp trong bảng thu_cung (tiem_chung, so_mui_tiem, ngay_tiem_gan_nhat). */
public final class TiemPhongDAOImpl implements TiemPhongDAO {
    public TiemPhongDAOImpl() {}

    public List<Object[]> layThuCung(String ma) {
        return truyVan(
                "SELECT id,ngay_sinh FROM thu_cung WITH(UPDLOCK,HOLDLOCK) WHERE ma_thu_cung=? AND"
                        + " dang_theo_doi=1",
                ma);
    }

    public List<Object[]> layLichSu(long id) {
        List<Object[]> dong =
                truyVan(
                        "SELECT COALESCE(tiem_chung,N''), so_mui_tiem, ngay_tiem_gan_nhat FROM"
                                + " thu_cung WHERE id=?",
                        id);
        if (dong.isEmpty()) return List.of();
        Object[] d = dong.get(0);
        String chuoi = String.valueOf(d[0]).trim();
        int soMui = d[1] == null ? 0 : ((Number) d[1]).intValue();
        Date ngayGanNhat = (Date) d[2];

        List<Object[]> kq = new ArrayList<>();
        if (chuoi.contains("|")) {
            long seq = 1;
            for (String phan : chuoi.split(";")) {
                if (phan.isBlank()) continue;
                String[] c = phan.split("\\|", -1);
                if (c.length >= 2 && !c[1].isBlank()) {
                    Date ngayTiem = Date.valueOf(c[1].trim());
                    Date ngayHan =
                            c.length >= 3 && !c[2].isBlank() ? Date.valueOf(c[2].trim()) : null;
                    String ghiChu = c.length >= 4 ? c[3] : "";
                    kq.add(new Object[] {seq++, c[0].trim(), ngayTiem, ngayHan, ghiChu});
                }
            }
            kq.sort(
                    Comparator.<Object[], LocalDate>comparing(r -> ((Date) r[2]).toLocalDate())
                            .reversed());
            return kq;
        }

        if (soMui > 0 && ngayGanNhat != null) {
            LocalDate ganNhat = ngayGanNhat.toLocalDate();
            for (int i = 0; i < soMui; i++) {
                LocalDate nt = ganNhat.minusDays(i * 30L);
                String ten =
                        i == 0
                                ? "Rabies + Vaccine tổng hợp (Mũi " + (soMui - i) + ")"
                                : "Vaccine tổng hợp (Mũi " + (soMui - i) + ")";
                kq.add(
                        new Object[] {
                            (long) (i + 1),
                            ten,
                            Date.valueOf(nt),
                            Date.valueOf(nt.plusYears(1)),
                            chuoi
                        });
            }
        }
        return kq;
    }

    public void luu(
            long id, long thuCung, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        List<Object[]> hienTai = new ArrayList<>(layLichSu(thuCung));
        if (id == 0) {
            long nextId =
                    hienTai.stream().mapToLong(r -> ((Number) r[0]).longValue()).max().orElse(0L)
                            + 1;
            hienTai.add(
                    new Object[] {
                        nextId,
                        ten,
                        Date.valueOf(ngay),
                        han == null ? null : Date.valueOf(han),
                        ghiChu == null ? "" : ghiChu
                    });
        } else {
            boolean found = false;
            for (int i = 0; i < hienTai.size(); i++) {
                if (((Number) hienTai.get(i)[0]).longValue() == id) {
                    hienTai.set(
                            i,
                            new Object[] {
                                id,
                                ten,
                                Date.valueOf(ngay),
                                han == null ? null : Date.valueOf(han),
                                ghiChu == null ? "" : ghiChu
                            });
                    found = true;
                    break;
                }
            }
            if (!found) throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
        }
        ghiLaiThuCung(thuCung, hienTai);
    }

    public void xoa(long id, long thuCung) {
        List<Object[]> hienTai = new ArrayList<>(layLichSu(thuCung));
        boolean removed = hienTai.removeIf(r -> ((Number) r[0]).longValue() == id);
        if (!removed) throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
        ghiLaiThuCung(thuCung, hienTai);
    }

    private void ghiLaiThuCung(long thuCung, List<Object[]> danhSach) {
        danhSach.sort(
                Comparator.<Object[], LocalDate>comparing(r -> ((Date) r[2]).toLocalDate())
                        .reversed());
        String chuoi =
                danhSach.stream()
                        .map(
                                r ->
                                        String.valueOf(r[1]).replace("|", "/").replace(";", ",")
                                                + "|"
                                                + r[2]
                                                + "|"
                                                + (r[3] == null ? "" : r[3])
                                                + "|"
                                                + (r[4] == null
                                                        ? ""
                                                        : String.valueOf(r[4])
                                                                .replace("|", "/")
                                                                .replace(";", ",")))
                        .collect(Collectors.joining(";"));
        if (chuoi.length() > 500) chuoi = chuoi.substring(0, 500);
        Date ganNhat = danhSach.isEmpty() ? null : (Date) danhSach.get(0)[2];
        capNhat(
                "UPDATE thu_cung SET tiem_chung=?, so_mui_tiem=?, ngay_tiem_gan_nhat=? WHERE id=?",
                chuoi.isBlank() ? null : chuoi,
                danhSach.size(),
                ganNhat,
                thuCung);
    }
}
