package com.petstation.dao.hethong.impl;

import com.petstation.dao.hethong.TaiKhoanDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.hethong.TaiKhoan;
import com.petstation.model.nhansu.NhanVien;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class TaiKhoanDAOImpl implements TaiKhoanDAO {
    private static final Logger LOG = LoggerFactory.getLogger(TaiKhoanDAOImpl.class);

    private void themQuyenAnToan(Set<Quyen> cacQuyen, String nguon, String maQuyen) {
        if (maQuyen == null || maQuyen.isBlank()) {
            return;
        }
        try {
            Quyen q = Quyen.docTuCSDL(maQuyen.trim());
            if (q != null) {
                cacQuyen.add(q);
            }
        } catch (IllegalArgumentException ex) {
            LOG.warn("Bỏ qua mã quyền không hợp lệ '{}' ({}): {}", maQuyen, nguon, ex.getMessage());
        }
    }

    @Override
    public Optional<TaiKhoan> timTheoTenDangNhap(Connection ketNoi, String tenDangNhap)
            throws Exception {
        String sql =
                """
                SELECT ten_dang_nhap, ma_nhan_vien, mat_khau_hash, vai_tro, quyen, trang_thai,
                       so_lan_dang_nhap_sai, khoa_den, bat_buoc_doi_mat_khau
                FROM tai_khoan
                WHERE LOWER(ten_dang_nhap) = LOWER(?)
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, tenDangNhap);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                if (!ketQua.next()) {
                    return Optional.empty();
                }
                Timestamp khoaDenLuc = ketQua.getTimestamp("khoa_den");
                TaiKhoan tk =
                        new TaiKhoan(
                                ketQua.getString("ten_dang_nhap"),
                                new NhanVien(ketQua.getString("ma_nhan_vien")),
                                ketQua.getString("mat_khau_hash"),
                                VaiTro.docTuCSDL(ketQua.getString("vai_tro")),
                                TrangThaiTaiKhoan.docTuCSDL(ketQua.getString("trang_thai")),
                                ketQua.getInt("so_lan_dang_nhap_sai"),
                                khoaDenLuc == null ? null : khoaDenLuc.toLocalDateTime(),
                                ketQua.getBoolean("bat_buoc_doi_mat_khau"));
                tk.setQuyen(ketQua.getString("quyen"));
                return Optional.of(tk);
            }
        }
    }

    @Override
    public Set<Quyen> timCacQuyen(Connection ketNoi, String tenDangNhap) throws Exception {
        String sql = "SELECT vai_tro, quyen FROM tai_khoan WHERE ten_dang_nhap = ?";
        Set<Quyen> cacQuyen = EnumSet.noneOf(Quyen.class);
        String tenVaiTro = null;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, tenDangNhap);
            try (ResultSet ketQua = cauLenh.executeQuery()) {
                if (ketQua.next()) {
                    tenVaiTro = ketQua.getString("vai_tro");
                    String chuoiQuyen = ketQua.getString("quyen");
                    if (chuoiQuyen != null && !chuoiQuyen.isBlank()) {
                        for (String ma : chuoiQuyen.split("[,;]")) {
                            themQuyenAnToan(cacQuyen, tenDangNhap, ma);
                        }
                    }
                }
            }
        }
        if (cacQuyen.isEmpty() && tenVaiTro != null) {
            VaiTro vt = VaiTro.docTuCSDL(tenVaiTro);
            for (String maQuyen : timCua(vt.name())) {
                themQuyenAnToan(cacQuyen, vt.name(), maQuyen);
            }
        }
        return cacQuyen;
    }

    @Override
    public void ghiNhanDangNhapThatBai(
            Connection ketNoi, String tenDangNhap, int soLuongThatBai, LocalDateTime khoaDenLuc)
            throws Exception {
        String sql =
                "UPDATE tai_khoan SET so_lan_dang_nhap_sai = ?, khoa_den = ? WHERE ten_dang_nhap ="
                        + " ?";
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, soLuongThatBai);
            cauLenh.setTimestamp(2, khoaDenLuc == null ? null : Timestamp.valueOf(khoaDenLuc));
            cauLenh.setString(3, tenDangNhap);
            cauLenh.executeUpdate();
        }
    }

    @Override
    public void ghiNhanDangNhapThanhCong(Connection ketNoi, String tenDangNhap) throws Exception {
        String sql =
                """
                UPDATE tai_khoan
                SET so_lan_dang_nhap_sai = 0, khoa_den = NULL
                WHERE ten_dang_nhap = ?
                """;
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, tenDangNhap);
            cauLenh.executeUpdate();
        }
    }

    @Override
    public String layMaBamMatKhau(String tenDangNhap) {
        Object ketQua =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT mat_khau_hash FROM tai_khoan WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ten_dang_nhap=?",
                        tenDangNhap);
        return ketQua == null ? null : String.valueOf(ketQua);
    }

    @Override
    public int capNhatMatKhau(String matKhauMoi, String tenDangNhap) {
        return ThaoTacSQL.capNhat(
                "UPDATE tai_khoan SET mat_khau_hash=?,bat_buoc_doi_mat_khau=0 WHERE"
                        + " ten_dang_nhap=?",
                matKhauMoi,
                tenDangNhap);
    }

    @Override
    public List<String> timCua(String maVaiTro) {
        VaiTro vt = VaiTro.docTuCSDL(maVaiTro);
        Object chuoi =
                ThaoTacSQL.layGiaTriDon(
                        "SELECT TOP 1 quyen FROM tai_khoan WHERE vai_tro=? AND quyen IS NOT NULL"
                                + " AND quyen<>N''",
                        vt.name());
        String ds = chuoi != null ? chuoi.toString() : macDinhTheoVaiTro(vt);
        List<String> ketQua = new ArrayList<>();
        for (String q : ds.split("[,;]")) {
            if (!q.isBlank()) {
                ketQua.add(q.trim());
            }
        }
        return ketQua;
    }

    public static String macDinhTheoVaiTro(VaiTro vt) {
        return switch (vt) {
            case QUAN_LY_CUA_HANG ->
                    "XEM_TONG_QUAN,QUAN_LY_TAI_KHOAN,QUAN_LY_LICH_LAM_VIEC,XEM_LICH_LAM_VIEC,QUAN_LY_KHACH_HANG,QUAN_LY_THU_CUNG,QUAN_LY_SAN_PHAM,QUAN_LY_NHA_CUNG_CAP,NHAP_KHO,XUAT_HUY_KHO,KIEM_KE_KHO,DE_XUAT_GIA,DUYET_GIA,BAN_HANG,THANH_TOAN,QUAN_LY_CA_THU_NGAN,QUAN_LY_KHUYEN_MAI,QUAN_LY_SPA,QUAN_LY_LUU_TRU,XU_LY_PHE_DUYET,XEM_NHAT_KY,XEM_BAO_CAO_CHUNG,XEM_BAO_CAO_CA";
            case NHAN_VIEN_BAN_HANG ->
                    "XEM_TONG_QUAN,XEM_LICH_LAM_VIEC,QUAN_LY_KHACH_HANG,QUAN_LY_THU_CUNG,BAN_HANG,THANH_TOAN,QUAN_LY_CA_THU_NGAN,QUAN_LY_SPA,QUAN_LY_LUU_TRU,XEM_BAO_CAO_CA";
            case NHAN_VIEN_KHO ->
                    "XEM_TONG_QUAN,XEM_LICH_LAM_VIEC,QUAN_LY_SAN_PHAM,QUAN_LY_NHA_CUNG_CAP,NHAP_KHO,XUAT_HUY_KHO,KIEM_KE_KHO,DE_XUAT_GIA";
            case NHAN_VIEN_SPA_HOTEL ->
                    "XEM_TONG_QUAN,XEM_LICH_LAM_VIEC,QUAN_LY_THU_CUNG,QUAN_LY_SPA,QUAN_LY_LUU_TRU";
        };
    }
}
