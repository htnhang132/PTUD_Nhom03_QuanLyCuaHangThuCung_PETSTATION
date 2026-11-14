package com.petstation.dao.khachhang.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.truyVan;

import com.petstation.dao.khachhang.TiemPhongDAO;
import com.petstation.util.DuLieu;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class TiemPhongDAOImpl implements TiemPhongDAO {
    public TiemPhongDAOImpl() {}

    @Override
    public ThongTinThuCung layThuCung(String ma) {
        var ds =
                truyVan(
                        "SELECT ma_thu_cung,ngay_sinh FROM thu_cung WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_thu_cung=? AND dang_theo_doi=1",
                        ma);
        if (ds.isEmpty()) return null;
        Object[] r = ds.get(0);
        String maThuCung = DuLieu.chuyenThanhChuoi(r[0]);
        LocalDate ngaySinh = DuLieu.chuyenLocalDate(r[1]);
        return new ThongTinThuCung(maThuCung, ngaySinh);
    }

    @Override
    public List<MucTiemPhong> layLichSu(String maThuCung) {
        List<Object[]> dong =
                truyVan(
                        "SELECT COALESCE(tiem_chung,N''), so_mui_tiem, ngay_tiem_gan_nhat FROM"
                                + " thu_cung WHERE ma_thu_cung=?",
                        maThuCung);
        if (dong.isEmpty()) return List.of();
        Object[] d = dong.get(0);
        String chuoi = String.valueOf(d[0]).trim();
        int soMui = d[1] == null ? 0 : ((Number) d[1]).intValue();
        Date ngayGanNhat = (Date) d[2];
        List<MucTiemPhong> kq = new ArrayList<>();
        if (chuoi.contains("|")) {
            long seq = 1;
            for (String phan : chuoi.split(";")) {
                if (phan.isBlank()) continue;
                String[] c = phan.split("\\|", -1);
                if (c.length >= 2 && !c[1].isBlank()) {
                    LocalDate ngayTiem = LocalDate.parse(c[1].trim());
                    LocalDate ngayHan =
                            c.length >= 3 && !c[2].isBlank() ? LocalDate.parse(c[2].trim()) : null;
                    String ghiChu = c.length >= 4 ? c[3] : "";
                    kq.add(new MucTiemPhong(seq++, c[0].trim(), ngayTiem, ngayHan, ghiChu));
                }
            }
            kq.sort(Comparator.comparing((MucTiemPhong m) -> m.ngayTiem).reversed());
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
                kq.add(new MucTiemPhong((long) (i + 1), ten, nt, nt.plusYears(1), chuoi));
            }
        }
        return kq;
    }

    @Override
    public void luu(
            long id, String maThuCung, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        List<MucTiemPhong> hienTai = new ArrayList<>(layLichSu(maThuCung));
        if (id == 0) {
            long nextId = hienTai.stream().mapToLong(r -> r.id).max().orElse(0L) + 1;
            hienTai.add(new MucTiemPhong(nextId, ten, ngay, han, ghiChu == null ? "" : ghiChu));
        } else {
            boolean found = false;
            for (int i = 0; i < hienTai.size(); i++) {
                if (hienTai.get(i).id == id) {
                    hienTai.set(
                            i, new MucTiemPhong(id, ten, ngay, han, ghiChu == null ? "" : ghiChu));
                    found = true;
                    break;
                }
            }
            if (!found) throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
        }
        ghiLaiThuCung(maThuCung, hienTai);
    }

    @Override
    public void xoa(long id, String maThuCung) {
        List<MucTiemPhong> hienTai = new ArrayList<>(layLichSu(maThuCung));
        boolean removed = hienTai.removeIf(r -> r.id == id);
        if (!removed) throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
        ghiLaiThuCung(maThuCung, hienTai);
    }

    private void ghiLaiThuCung(String maThuCung, List<MucTiemPhong> danhSach) {
        danhSach.sort(Comparator.comparing((MucTiemPhong m) -> m.ngayTiem).reversed());
        String chuoi =
                danhSach.stream()
                        .map(
                                r ->
                                        String.valueOf(r.tenVaccine)
                                                        .replace("|", "/")
                                                        .replace(";", ",")
                                                + "|"
                                                + r.ngayTiem
                                                + "|"
                                                + (r.ngayHetHan == null ? "" : r.ngayHetHan)
                                                + "|"
                                                + (r.ghiChu == null
                                                        ? ""
                                                        : r.ghiChu
                                                                .replace("|", "/")
                                                                .replace(";", ",")))
                        .collect(Collectors.joining(";"));
        if (chuoi.length() > 500) chuoi = chuoi.substring(0, 500);
        Date ganNhat = danhSach.isEmpty() ? null : Date.valueOf(danhSach.get(0).ngayTiem);
        capNhat(
                "UPDATE thu_cung SET tiem_chung=?, so_mui_tiem=?, ngay_tiem_gan_nhat=? WHERE"
                        + " ma_thu_cung=?",
                chuoi.isBlank() ? "" : chuoi,
                danhSach.size(),
                ganNhat,
                maThuCung);
    }
}
