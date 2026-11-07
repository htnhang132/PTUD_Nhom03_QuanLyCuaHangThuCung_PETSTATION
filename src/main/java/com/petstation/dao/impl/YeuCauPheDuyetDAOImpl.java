package com.petstation.dao.impl;

import com.petstation.dao.YeuCauPheDuyetDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.service.PhienDangNhapService;

import java.util.List;

public final class YeuCauPheDuyetDAOImpl implements YeuCauPheDuyetDAO {
    public YeuCauPheDuyetDAOImpl() {}

    public long demSoLuongDangCho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM yeu_cau_phe_duyet WHERE trang_thai='CHO_DUYET'", cacThamSo);
    }

    public int capNhatTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='TU_CHOI' WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[0], cacThamSo[0]);
    }

    public int tuChoiBienBanKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='TU_CHOI' WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[0], cacThamSo[0]);
    }

    public long demApDungPheDuyet(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='DA_THANH_TOAN'", cacThamSo[0], cacThamSo[0]);
    }

    public int capNhatApDungPheDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat("UPDATE hoa_don SET ghi_chu=? WHERE (ma_hoa_don = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_hoa_don, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[0], cacThamSo[1], cacThamSo[1]);
    }

    public int capNhatGuiThaoTac(Object... cacThamSo) {
        String tenDangNhapGui = PhienDangNhapService.layTenDangNhap();
        if (tenDangNhapGui == null || tenDangNhapGui.isBlank()) {
            if (cacThamSo[2] instanceof Number) {
                List<Object[]> r = ThaoTacSQL.truyVan("SELECT ten_dang_nhap FROM tai_khoan WHERE (ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[2], cacThamSo[2]);
                if (!r.isEmpty()) tenDangNhapGui = String.valueOf(r.get(0)[0]);
            } else if (cacThamSo[2] != null) {
                tenDangNhapGui = String.valueOf(cacThamSo[2]);
            }
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO yeu_cau_phe_duyet(ma_yeu_cau, loai, ten_dang_nhap_gui, loai_doi_tuong, ma_doi_tuong, ly_do, noi_dung_truoc, noi_dung_sau, payload, gui_luc) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME())",
                cacThamSo[0],
                cacThamSo[1],
                tenDangNhapGui,
                cacThamSo[3],
                String.valueOf(cacThamSo[4]),
                cacThamSo[5],
                cacThamSo[6],
                cacThamSo[7],
                cacThamSo[8]);
    }

    public List<Object[]> timTatCa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " y.ma_yeu_cau,y.loai,n.ho_ten,y.gui_luc,y.ly_do,y.noi_dung_truoc,y.noi_dung_sau,y.trang_thai,COALESCE(a.ho_ten,N''),COALESCE(y.phan_hoi,N'')"
                    + " FROM yeu_cau_phe_duyet y JOIN tai_khoan t ON t.ten_dang_nhap=y.ten_dang_nhap_gui JOIN"
                    + " nhan_vien n ON n.ma_nhan_vien=t.ma_nhan_vien LEFT JOIN tai_khoan u ON"
                    + " u.ten_dang_nhap=y.ten_dang_nhap_xu_ly LEFT JOIN nhan_vien a ON a.ma_nhan_vien=u.ma_nhan_vien ORDER BY"
                    + " y.gui_luc DESC, y.ma_yeu_cau DESC",
                cacThamSo);
    }

    public List<Object[]> timDuyet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT loai_doi_tuong,ma_doi_tuong,payload FROM"
                        + " yeu_cau_phe_duyet WITH(UPDLOCK,HOLDLOCK) WHERE"
                        + " ma_yeu_cau=? AND trang_thai='CHO_DUYET'",
                cacThamSo);
    }

    public int capNhatDuyet(Object... cacThamSo) {
        String tenDangNhapXuLy = PhienDangNhapService.layTenDangNhap();
        if (tenDangNhapXuLy == null || tenDangNhapXuLy.isBlank()) {
            if (cacThamSo[0] instanceof Number) {
                List<Object[]> r = ThaoTacSQL.truyVan("SELECT ten_dang_nhap FROM tai_khoan WHERE (ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[0], cacThamSo[0]);
                if (!r.isEmpty()) tenDangNhapXuLy = String.valueOf(r.get(0)[0]);
            } else if (cacThamSo[0] != null) {
                tenDangNhapXuLy = String.valueOf(cacThamSo[0]);
            }
        }
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                        + " trang_thai='DA_DUYET', ten_dang_nhap_xu_ly=?, xu_ly_luc=SYSDATETIME()"
                        + " WHERE ma_yeu_cau=?",
                tenDangNhapXuLy, cacThamSo[1]);
    }

    public List<Object[]> timTuChoi(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT loai_doi_tuong,ma_doi_tuong FROM yeu_cau_phe_duyet"
                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_yeu_cau=? AND"
                        + " trang_thai='CHO_DUYET'",
                cacThamSo);
    }

    public int tuChoiPhieuDoiTra(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_doi_tra SET trang_thai='TU_CHOI' WHERE (ma_phieu = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND"
                        + " trang_thai='CHO_XU_LY'",
                cacThamSo[0], cacThamSo[0]);
    }

    public int danhDauYeuCauTuChoi(Object... cacThamSo) {
        String tenDangNhapXuLy = PhienDangNhapService.layTenDangNhap();
        if (tenDangNhapXuLy == null || tenDangNhapXuLy.isBlank()) {
            if (cacThamSo[0] instanceof Number) {
                List<Object[]> r = ThaoTacSQL.truyVan("SELECT ten_dang_nhap FROM tai_khoan WHERE (ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", cacThamSo[0], cacThamSo[0]);
                if (!r.isEmpty()) tenDangNhapXuLy = String.valueOf(r.get(0)[0]);
            } else if (cacThamSo[0] != null) {
                tenDangNhapXuLy = String.valueOf(cacThamSo[0]);
            }
        }
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                    + " trang_thai='TU_CHOI', ten_dang_nhap_xu_ly=?, phan_hoi=?, xu_ly_luc=SYSDATETIME()"
                    + " WHERE ma_yeu_cau=?",
                tenDangNhapXuLy,
                cacThamSo[1],
                cacThamSo[2]);
    }

    @Override
    public int yeuCauBoSung(
            long nguoiXuLyId,
            String noiDung,
            String maYeuCau) {
        String tenDangNhapXuLy = PhienDangNhapService.layTenDangNhap();
        if (tenDangNhapXuLy == null || tenDangNhapXuLy.isBlank()) {
            List<Object[]> r = ThaoTacSQL.truyVan("SELECT ten_dang_nhap FROM tai_khoan WHERE (ten_dang_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nhan_vien, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))", nguoiXuLyId, nguoiXuLyId);
            if (!r.isEmpty()) tenDangNhapXuLy = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                """
                UPDATE yeu_cau_phe_duyet
                SET trang_thai = 'CAN_THEM_THONG_TIN',
                    ten_dang_nhap_xu_ly = ?,
                    phan_hoi = ?,
                    xu_ly_luc = SYSDATETIME()
                WHERE ma_yeu_cau = ?
                  AND trang_thai = 'CHO_DUYET'
                """,
                tenDangNhapXuLy, noiDung, maYeuCau);
    }
}
