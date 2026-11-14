package com.petstation.dao.khachhang.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.them;
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
        List<Object[]> dongLichSu =
                truyVan(
                        "SELECT ma_tiem_phong, ten_vaccine, ngay_tiem, ngay_het_han, ghi_chu"
                                + " FROM lich_su_tiem_phong WHERE ma_thu_cung=?"
                                + " ORDER BY ngay_tiem DESC, ma_tiem_phong DESC",
                        maThuCung);
        if (!dongLichSu.isEmpty()) {
            List<MucTiemPhong> kq = new ArrayList<>(dongLichSu.size());
            for (Object[] d : dongLichSu) {
                kq.add(chuyenDongLichSu(d));
            }
            return kq;
        }
        return docDuLieuCuNeuCo(maThuCung);
    }

    @Override
    public void luu(
            long id, String maThuCung, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        dongBoDuLieuCuSangBang(maThuCung);
        String ghiChuAnToan = ghiChu == null ? "" : ghiChu;
        if (id == 0) {
            them(
                    "INSERT INTO lich_su_tiem_phong(ma_thu_cung, ten_vaccine, ngay_tiem,"
                            + " ngay_het_han, ghi_chu) VALUES(?,?,?,?,?)",
                    maThuCung,
                    ten,
                    ngay,
                    han,
                    ghiChuAnToan);
        } else {
            int capNhatDong =
                    capNhat(
                            "UPDATE lich_su_tiem_phong SET ten_vaccine=?, ngay_tiem=?,"
                                    + " ngay_het_han=?, ghi_chu=? WHERE ma_tiem_phong=? AND"
                                    + " ma_thu_cung=?",
                            ten,
                            ngay,
                            han,
                            ghiChuAnToan,
                            id,
                            maThuCung);
            if (capNhatDong == 0) {
                throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
            }
        }
        capNhatTomTatThuCung(maThuCung);
    }

    @Override
    public void xoa(long id, String maThuCung) {
        dongBoDuLieuCuSangBang(maThuCung);
        int daXoa =
                capNhat(
                        "DELETE FROM lich_su_tiem_phong WHERE ma_tiem_phong=? AND ma_thu_cung=?",
                        id,
                        maThuCung);
        if (daXoa == 0) {
            throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
        }
        capNhatTomTatThuCung(maThuCung);
    }

    private void dongBoDuLieuCuSangBang(String maThuCung) {
        if (laySo("SELECT COUNT(*) FROM lich_su_tiem_phong WHERE ma_thu_cung=?", maThuCung) > 0) {
            return;
        }
        List<MucTiemPhong> cu = docDuLieuCuNeuCo(maThuCung);
        for (MucTiemPhong m : cu) {
            them(
                    "INSERT INTO lich_su_tiem_phong(ma_thu_cung, ten_vaccine, ngay_tiem,"
                            + " ngay_het_han, ghi_chu) VALUES(?,?,?,?,?)",
                    maThuCung,
                    m.tenVaccine,
                    m.ngayTiem,
                    m.ngayHetHan,
                    m.ghiChu == null ? "" : m.ghiChu);
        }
    }

    private static MucTiemPhong chuyenDongLichSu(Object[] d) {
        long maTiem = d[0] == null ? 0L : ((Number) d[0]).longValue();
        String ten = DuLieu.chuyenThanhChuoi(d[1]).trim();
        LocalDate ngayTiem = DuLieu.chuyenLocalDate(d[2]);
        LocalDate ngayHan = DuLieu.chuyenLocalDate(d[3]);
        String ghiChu = DuLieu.chuyenThanhChuoi(d[4]);
        return new MucTiemPhong(maTiem, ten, ngayTiem, ngayHan, ghiChu);
    }

    private static List<MucTiemPhong> docDuLieuCuNeuCo(String maThuCung) {
        List<Object[]> dong =
                truyVan(
                        "SELECT COALESCE(tiem_chung,N'') FROM thu_cung WHERE ma_thu_cung=?",
                        maThuCung);
        if (dong.isEmpty()) return List.of();
        String chuoi = DuLieu.chuyenThanhChuoi(dong.get(0)[0]).trim();
        if (!chuoi.contains("|")) return List.of();
        List<MucTiemPhong> kq = new ArrayList<>();
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

    private void capNhatTomTatThuCung(String maThuCung) {
        List<Object[]> dong =
                truyVan(
                        "SELECT ma_tiem_phong, ten_vaccine, ngay_tiem, ngay_het_han, ghi_chu"
                                + " FROM lich_su_tiem_phong WHERE ma_thu_cung=?"
                                + " ORDER BY ngay_tiem DESC, ma_tiem_phong DESC",
                        maThuCung);
        List<MucTiemPhong> danhSach = new ArrayList<>(dong.size());
        for (Object[] r : dong) {
            danhSach.add(chuyenDongLichSu(r));
        }
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
        if (chuoi.length() > 1000) chuoi = chuoi.substring(0, 1000);
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
