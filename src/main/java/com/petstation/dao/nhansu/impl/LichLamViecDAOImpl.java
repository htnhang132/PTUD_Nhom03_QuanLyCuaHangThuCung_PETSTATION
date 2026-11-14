package com.petstation.dao.nhansu.impl;

import com.petstation.dao.nhansu.LichLamViecDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.chung.TrangThaiLichLamViec;
import com.petstation.model.nhansu.CaLamViec;
import com.petstation.model.nhansu.LichLamViec;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DuLieu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LichLamViecDAOImpl implements LichLamViecDAO {
    public LichLamViecDAOImpl() {}

    @Override
    public long demPhanCong(String maNhanVien) {
        return ThaoTacSQL.laySo(
                "SELECT 1 FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE ma_nhan_vien = ?",
                maNhanVien);
    }

    @Override
    public long demNhanVienDangLam(String maNhanVien) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nhan_vien WHERE ma_nhan_vien = ? AND dang_lam_viec=1",
                maNhanVien);
    }

    @Override
    public List<LichLamViec> timKhoang(LocalDate tu, LocalDate den) {
        List<LichLamViec> ketQua = new ArrayList<>();
        for (Object[] dong :
                ThaoTacSQL.truyVan(
                        "SELECT l.ngay_lam, l.ma_ca, l.ma_nhan_vien, l.loai FROM lich_lam_viec l"
                                + " JOIN ca_lam_viec c ON c.ma_ca=l.ma_ca WHERE l.ngay_lam>=? AND"
                                + " l.ngay_lam<? AND l.trang_thai<>'DA_HUY' ORDER BY l.ngay_lam,"
                                + " c.gio_bat_dau, l.ma_nhan_vien",
                        tu,
                        den)) {
            LichLamViec llv = new LichLamViec();
            llv.setNgayLam(DuLieu.chuyenLocalDate(dong[0]));
            llv.setCaLamViec(new CaLamViec(DuLieu.chuyenThanhChuoi(dong[1])));
            llv.setNhanVien(new NhanVien(DuLieu.chuyenThanhChuoi(dong[2])));
            llv.setLoai(DuLieu.chuyenThanhChuoi(dong[3]));
            ketQua.add(llv);
        }
        return ketQua;
    }

    @Override
    public long demLichTrungNgayCa(String maNhanVien, LocalDate ngay, String maCa) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_lam_viec WHERE ma_nhan_vien = ? AND ngay_lam=? AND"
                        + " trang_thai<>'DA_HUY' AND (loai='NGHI_PHEP' OR ma_ca=?)",
                maNhanVien,
                ngay,
                maCa);
    }

    @Override
    public int capNhatPhanCong(String maNhanVien, LocalDate ngay, String maCa) {
        return ThaoTacSQL.capNhat(
                "DELETE FROM lich_lam_viec WHERE ma_nhan_vien = ? AND ngay_lam=? AND ma_ca=? AND"
                        + " trang_thai='DA_HUY'",
                maNhanVien,
                ngay,
                maCa);
    }

    @Override
    public int themLichLamViec(String maNhanVien, LocalDate ngay, String maCa) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_lam_viec(ma_nhan_vien, ngay_lam, ma_ca, loai, trang_thai)"
                        + " VALUES(?, ?, ?, 'DI_LAM', ?)",
                maNhanVien,
                ngay,
                maCa,
                TrangThaiLichLamViec.DANG_MO.maDB());
    }

    @Override
    public int capNhatXoa(String maNhanVien, LocalDate ngay, String maCa) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET trang_thai=? WHERE ma_nhan_vien = ? AND ngay_lam=? AND"
                        + " ma_ca=?",
                TrangThaiLichLamViec.DA_HUY.maDB(),
                maNhanVien,
                ngay,
                maCa);
    }

    @Override
    public int capNhatDanhDauNghiPhepDaDuyet(String maNhanVien, LocalDate ngay) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET loai='NGHI_PHEP',trang_thai=? WHERE ma_nhan_vien = ? AND"
                        + " ngay_lam=?",
                TrangThaiLichLamViec.DANG_MO.maDB(),
                maNhanVien,
                ngay);
    }

    @Override
    public int themLichNghiPhep(String maNhanVien, LocalDate ngay) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO lich_lam_viec(ma_nhan_vien, ngay_lam, ma_ca, loai, trang_thai)"
                        + " VALUES(?, ?, 'CA01', 'NGHI_PHEP', 'DANG_MO')",
                maNhanVien,
                ngay);
    }

    @Override
    public long demThangSoGio(String maNhanVien, LocalDate tu, LocalDate den) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(SUM(DATEDIFF(minute, c.gio_bat_dau, c.gio_ket_thuc)),0)/60 FROM"
                        + " lich_lam_viec l JOIN ca_lam_viec c ON c.ma_ca=l.ma_ca WHERE"
                        + " l.ma_nhan_vien = ? AND l.ngay_lam>=? AND l.ngay_lam<? AND"
                        + " l.loai='DI_LAM' AND l.trang_thai<>'DA_HUY'",
                maNhanVien,
                tu,
                den);
    }

    @Override
    public Map<String, Integer> layGioTheoNhanVienKhoang(LocalDate tu, LocalDate den) {
        Map<String, Integer> ketQua = new HashMap<>();
        for (Object[] dong :
                ThaoTacSQL.truyVan(
                        "SELECT l.ma_nhan_vien, COALESCE(SUM(DATEDIFF(minute, c.gio_bat_dau,"
                            + " c.gio_ket_thuc)),0)/60 FROM lich_lam_viec l JOIN ca_lam_viec c ON"
                            + " c.ma_ca=l.ma_ca WHERE l.ngay_lam>=? AND l.ngay_lam<? AND"
                            + " l.loai='DI_LAM' AND l.trang_thai<>'DA_HUY' GROUP BY l.ma_nhan_vien",
                        tu,
                        den)) {
            String ma = DuLieu.chuyenThanhChuoi(dong[0]);
            int gio = dong[1] instanceof Number n ? n.intValue() : 0;
            if (!ma.isBlank()) ketQua.put(ma, gio);
        }
        return ketQua;
    }

    @Override
    public int khoaLichKhoang(LocalDate tu, LocalDate den) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_lam_viec SET trang_thai='DA_KHOA' WHERE ngay_lam>=? AND ngay_lam<? AND"
                        + " trang_thai='DANG_MO'",
                tu,
                den);
    }
}
