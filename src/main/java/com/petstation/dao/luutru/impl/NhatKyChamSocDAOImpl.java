package com.petstation.dao.luutru.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.luutru.NhatKyChamSocDAO;
import com.petstation.model.luutru.NhatKyChamSoc;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DuLieu;

import java.util.List;

public final class NhatKyChamSocDAOImpl implements NhatKyChamSocDAO {
    public NhatKyChamSocDAOImpl() {}

    @Override
    public List<NhatKyChamSoc> layLichSu(boolean spa, String maPhieu) {
        String fk = spa ? "ma_phieu_spa" : "ma_hop_dong";
        return truyVanDanhSach(
                "SELECT p.ma_nhat_ky, p.thoi_gian, p.ma_nhan_vien, n.ho_ten, p.tinh_trang,"
                        + " COALESCE(p.an_uong,N''), COALESCE(p.huong_dan_thuoc,N''),"
                        + " COALESCE(p.da_thuc_hien,N''), p.anh_truoc, p.anh_sau FROM"
                        + " nhat_ky_cham_soc p JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien=p.ma_nhan_vien WHERE p."
                        + fk
                        + "=? ORDER BY p.thoi_gian DESC, p.ma_nhat_ky DESC",
                r -> {
                    NhanVien nv = new NhanVien();
                    nv.setMaNhanVien(DuLieu.chuyenThanhChuoi(r[2]));
                    nv.setHoTen(DuLieu.chuyenThanhChuoi(r[3]));
                    return new NhatKyChamSoc(
                            DuLieu.chuyenThanhChuoi(r[0]),
                            nv,
                            DuLieu.chuyenLocalDateTime(r[1]),
                            DuLieu.chuyenThanhChuoi(r[4]),
                            DuLieu.chuyenThanhChuoi(r[5]),
                            DuLieu.chuyenThanhChuoi(r[6]),
                            DuLieu.chuyenThanhChuoi(r[7]),
                            r[8] == null ? null : r[8].toString(),
                            r[9] == null ? null : r[9].toString());
                },
                maPhieu);
    }

    @Override
    public List<String> khoaPhieu(boolean spa, String maPhieu) {
        String table = spa ? "phieu_tiep_nhan_spa" : "hop_dong_luu_tru";
        String col = spa ? "ma_phieu" : "ma_hop_dong";
        return truyVanDanhSach(
                "SELECT trang_thai FROM " + table + " WITH(UPDLOCK,HOLDLOCK) WHERE " + col + "=?",
                r -> DuLieu.chuyenThanhChuoi(r[0]),
                maPhieu);
    }

    @Override
    public int themPhieu(
            String maSpa,
            String maHotel,
            String maNhanVien,
            String tinhTrang,
            String an,
            String thuoc,
            String viec,
            String anhTruoc,
            String anhSau) {
        return capNhat(
                "INSERT INTO nhat_ky_cham_soc(ma_nhat_ky, ma_phieu_spa, ma_hop_dong, ma_nhan_vien,"
                        + " tinh_trang, an_uong, huong_dan_thuoc, da_thuc_hien, anh_truoc,"
                        + " anh_sau, thoi_gian) VALUES(?,?,?,?,?,?,?,?,?,?,SYSDATETIME())",
                DuLieu.taoMa("NK"),
                maSpa,
                maHotel,
                maNhanVien,
                tinhTrang,
                an,
                thuoc,
                viec,
                anhTruoc,
                anhSau);
    }
}
