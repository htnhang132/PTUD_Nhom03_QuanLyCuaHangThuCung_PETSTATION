package com.petstation.dao.hethong.impl;

import com.petstation.dao.hethong.NhatKyHeThongDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.model.hethong.NhatKyHeThong;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.util.DuLieu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class NhatKyHeThongDAOImpl implements NhatKyHeThongDAO {
    @Override
    public void them(Connection ketNoi, NhatKyHeThong muc) throws Exception {
        String sql =
                """
                INSERT INTO nhat_ky_he_thong
                    (ma_nhat_ky, ma_nhan_vien, hanh_dong, loai_doi_tuong, ma_doi_tuong,
                     ghi_chu, loai_su_kien, ket_qua, thoi_gian)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME())
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, muc.getMaNhatKy());
            if (muc.getNguoiThucHien() == null || muc.getNguoiThucHien().getMaNhanVien() == null) {
                cauLenh.setNull(2, Types.VARCHAR);
            } else {
                cauLenh.setString(2, muc.getNguoiThucHien().getMaNhanVien());
            }
            cauLenh.setString(3, muc.getThaoTac());
            cauLenh.setString(4, muc.getKieuThucThe());
            cauLenh.setString(5, muc.getIdThucThe());
            cauLenh.setString(6, muc.getGhiChu());
            cauLenh.setString(7, muc.getLoaiSuKien());
            cauLenh.setString(8, muc.getKetQua());
            cauLenh.executeUpdate();
        }
    }

    @Override
    public int capNhatNhatKy(
            String maNhanVien, String hanhDong, String ghiChu, String loaiSuKien, String ketQua) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO nhat_ky_he_thong"
                    + "(ma_nhat_ky,ma_nhan_vien,hanh_dong,ghi_chu,loai_su_kien,ket_qua,thoi_gian)"
                    + " VALUES(?,?,?,?,?,?,SYSDATETIME())",
                DuLieu.taoMa("LOG"),
                maNhanVien,
                hanhDong,
                ghiChu,
                loaiSuKien,
                ketQua);
    }

    @Override
    public List<DongNhatKyHeThong> timCacMuc() {
        List<DongNhatKyHeThong> ketQua = new ArrayList<>();
        for (Object[] dong :
                ThaoTacSQL.truyVan(
                        "SELECT TOP(2000) a.thoi_gian, a.ma_nhan_vien, COALESCE(n.ho_ten,"
                                + " a.ma_nhan_vien, N'Hệ thống'), a.hanh_dong,"
                                + " COALESCE(a.ghi_chu,a.ma_doi_tuong,N''),"
                                + " COALESCE(a.loai_su_kien,'SYSTEM'), COALESCE(a.ket_qua,N'Thành"
                                + " công') FROM nhat_ky_he_thong a LEFT JOIN nhan_vien n ON"
                                + " n.ma_nhan_vien=a.ma_nhan_vien ORDER BY a.thoi_gian"
                                + " DESC,a.ma_nhat_ky DESC")) {
            NhanVien nguoiDung = new NhanVien();
            String maNhanVien = DuLieu.chuyenThanhChuoi(dong[1]);
            if (!maNhanVien.isBlank()) {
                nguoiDung.setMaNhanVien(maNhanVien);
            }
            nguoiDung.setHoTen(DuLieu.chuyenThanhChuoi(dong[2]));
            ketQua.add(
                    new DongNhatKyHeThong(
                            nguoiDung,
                            DuLieu.chuyenLocalDateTime(dong[0]),
                            DuLieu.chuyenThanhChuoi(dong[3]),
                            DuLieu.chuyenThanhChuoi(dong[4]),
                            DuLieu.chuyenThanhChuoi(dong[5]),
                            DuLieu.chuyenThanhChuoi(dong[6])));
        }
        return ketQua;
    }
}
