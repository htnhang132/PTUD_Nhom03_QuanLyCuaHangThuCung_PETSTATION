package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.PhieuTiepNhanSpaDAO;
import com.petstation.database.ThaoTacSQL;

import java.time.LocalDateTime;
import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC Spa DAO. */
public final class PhieuTiepNhanSpaDAOImpl implements PhieuTiepNhanSpaDAO {
    public PhieuTiepNhanSpaDAOImpl() {}

    public int capNhatTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='CHECKED_IN' WHERE id=?", cacThamSo);
    }

    public int capNhatSucKhoeThuCung(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE thu_cung SET can_nang=?,tinh_trang_suc_khoe=? WHERE id=?", cacThamSo);
    }

    public long demTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT CASE WHEN (goi_id IS NOT NULL OR don_gia>0 OR tong_tien>0) THEN 1 ELSE 0"
                        + " END FROM phieu_tiep_nhan_spa WHERE id=?",
                cacThamSo);
    }

    public long demLichHen(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE id=? AND trang_thai='ACTIVE'",
                cacThamSo);
    }

    public long khoaNhanVienDangLam(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT id FROM nhan_vien WITH(UPDLOCK,HOLDLOCK) WHERE id=? AND"
                        + " dang_lam_viec=1",
                cacThamSo);
    }

    public long layThoiLuongDichVu(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT thoi_gian_du_kien FROM goi_dich_vu_spa WHERE id=?", cacThamSo);
    }

    public long demLichTrung(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " (nhan_vien_id=? OR thu_cung_id=?) AND trang_thai NOT IN"
                        + " ('CANCELLED','NO_SHOW','COMPLETED') AND bat_dau<? AND"
                        + " ket_thuc>?",
                cacThamSo);
    }

    public long themLichHen(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " lich_hen(ma_lich_hen,khach_hang_id,thu_cung_id,loai_dich_vu,bat_dau,ket_thuc,trang_thai,nhan_vien_id,dich_vu_id)"
                    + " VALUES(?,?,?,'SPA',?,?,'SCHEDULED',?,?)",
                cacThamSo);
    }

    public List<Object[]> timTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT id,nhan_vien_id FROM lich_hen"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_lich_hen=? AND"
                        + " thu_cung_id=? AND trang_thai='SCHEDULED'",
                cacThamSo);
    }

    public long themTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.them(
                "INSERT INTO"
                    + " phieu_tiep_nhan_spa(ma_phieu,lich_hen_id,thu_cung_id,can_nang_thuc_te,nhan_vien_id,ghi_chu,trang_thai,tao_luc)"
                    + " VALUES(?,?,?,?,?,?,'RECEIVED',SYSDATETIME())",
                cacThamSo);
    }

    public Object layTiepNhan(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT g.ma_goi FROM lich_hen l JOIN goi_dich_vu_spa g ON"
                        + " g.id=l.dich_vu_id WHERE l.id=?",
                cacThamSo);
    }

    public List<Object[]> timThemDichVuSpa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT t.can_nang_thuc_te,p.loai,t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t WITH(UPDLOCK,HOLDLOCK) JOIN"
                        + " thu_cung p ON p.id=t.thu_cung_id WHERE t.id=?",
                cacThamSo);
    }

    public List<Object[]> layGiaDichVuTheoCanNang(Object... cacThamSo) {
        String maGoi = String.valueOf(cacThamSo[0]);
        String loaiThuCung = String.valueOf(cacThamSo[1]);
        double canNang = ((Number) cacThamSo[2]).doubleValue();
        List<Object[]> goiList =
                ThaoTacSQL.truyVan(
                        "SELECT id, ten_goi, COALESCE(bang_gia, N'') FROM goi_dich_vu_spa WHERE"
                                + " ma_goi=? AND trang_thai='ACTIVE'",
                        maGoi);
        if (goiList.isEmpty()) return List.of();
        Object[] goi = goiList.get(0);
        long goiId = ((Number) goi[0]).longValue();
        String tenGoi = String.valueOf(goi[1]);
        String bangGia = String.valueOf(goi[2]).trim();
        if (bangGia.isEmpty()) return List.of();

        long donGiaChon = -1;
        double maxTu = -1;
        for (String muc : bangGia.split(";")) {
            if (muc.isBlank()) continue;
            String[] p = muc.split(":");
            if (p.length < 3) continue;
            if (!p[0].trim().equalsIgnoreCase(loaiThuCung)) continue;
            String[] khoang = p[1].trim().split("-");
            double tu = Double.parseDouble(khoang[0].trim());
            double den =
                    khoang.length > 1 && !khoang[1].isBlank()
                            ? Double.parseDouble(khoang[1].trim())
                            : Double.MAX_VALUE;
            long gia = Long.parseLong(p[2].trim());
            if (canNang >= tu && (canNang <= den || den >= 999) && tu >= maxTu) {
                maxTu = tu;
                donGiaChon = gia;
            }
        }
        if (donGiaChon < 0) return List.of();
        return List.<Object[]>of(new Object[] {goiId, donGiaChon, tenGoi});
    }

    public int capNhatThemDichVuSpa(Object... cacThamSo) {
        long phieuSpaId = ((Number) cacThamSo[0]).longValue();
        long goiId = ((Number) cacThamSo[1]).longValue();
        String moTa = String.valueOf(cacThamSo[2]);
        long donGia = ((Number) cacThamSo[3]).longValue();
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET goi_id=COALESCE(goi_id,?),"
                        + " mo_ta_dich_vu=CASE WHEN mo_ta_dich_vu IS NULL OR mo_ta_dich_vu=N''"
                        + " THEN ? ELSE mo_ta_dich_vu + N'; ' + ? END,"
                        + " don_gia=don_gia+?, so_luong=CASE WHEN so_luong<=0 THEN 1 ELSE so_luong"
                        + " END, tong_tien=tong_tien+? WHERE id=?",
                goiId,
                moTa,
                moTa,
                donGia,
                donGia,
                phieuSpaId);
    }

    public int tinhLaiTongPhieuSpa(Object... cacThamSo) {
        long phieuSpaId = ((Number) cacThamSo[0]).longValue();
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET tong_tien=don_gia*COALESCE(NULLIF(so_luong,0),1)"
                        + " WHERE id=?",
                phieuSpaId);
    }

    public int capNhatTrangThaiSpa(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_tiep_nhan_spa SET trang_thai=? WHERE id=? AND trang_thai=?",
                cacThamSo);
    }

    public int hoanTatLichHen(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='COMPLETED' WHERE id=(SELECT"
                        + " lich_hen_id FROM phieu_tiep_nhan_spa WHERE id=?)",
                cacThamSo);
    }

    public int huyLichHenBanGhi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lich_hen SET trang_thai='CANCELLED' WHERE id=? AND"
                        + " trang_thai='SCHEDULED'",
                cacThamSo);
    }

    public Object layMaLichHenThuCung(Object... cacThamSo) {
        return ThaoTacSQL.layGiaTriDon(
                "SELECT p.ma_thu_cung FROM lich_hen l JOIN thu_cung p ON"
                        + " p.id=l.thu_cung_id WHERE l.id=?",
                cacThamSo);
    }

    public List<Object[]> layPhieuSpaChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT COALESCE(mo_ta_dich_vu,N'Dịch vụ Spa'),"
                        + " COALESCE(NULLIF(don_gia,0),tong_tien),"
                        + " COALESCE(NULLIF(so_luong,0),1), tong_tien FROM phieu_tiep_nhan_spa"
                        + " WHERE id=? AND (goi_id IS NOT NULL OR don_gia>0 OR tong_tien>0)",
                cacThamSo);
    }

    public List<Object[]> layChiTietLichHen(long id) {
        return truyVan(
                "SELECT p.ma_thu_cung,g.ma_goi,n.ma_nhan_vien,l.bat_dau FROM lich_hen l"
                        + " JOIN thu_cung p ON p.id=l.thu_cung_id LEFT JOIN goi_dich_vu_spa g"
                        + " ON g.id=l.dich_vu_id LEFT JOIN nhan_vien n ON n.id=l.nhan_vien_id"
                        + " WHERE l.id=?",
                id);
    }

    public List<Object[]> khoaLichHen(long id) {
        return truyVan(
                "SELECT trang_thai FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE id=?", id);
    }

    public long demLichTrungKhiSua(
            long id, long nhanVien, Object thuCung, LocalDateTime ketThuc, LocalDateTime batDau) {
        return laySo(
                "SELECT COUNT(*) FROM lich_hen WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " id<>? AND (nhan_vien_id=? OR thu_cung_id=?) AND"
                        + " trang_thai NOT IN ('CANCELLED','NO_SHOW','COMPLETED')"
                        + " AND bat_dau<? AND ket_thuc>?",
                id,
                nhanVien,
                thuCung,
                ketThuc,
                batDau);
    }

    public int suaLichHen(
            Object khachHang,
            Object thuCung,
            long goi,
            long nhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long id) {
        return capNhat(
                "UPDATE lich_hen SET"
                    + " khach_hang_id=?,thu_cung_id=?,dich_vu_id=?,nhan_vien_id=?,bat_dau=?,ket_thuc=?"
                    + " WHERE id=?",
                khachHang,
                thuCung,
                goi,
                nhanVien,
                batDau,
                ketThuc,
                id);
    }
}
