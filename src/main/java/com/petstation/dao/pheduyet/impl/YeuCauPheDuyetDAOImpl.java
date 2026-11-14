package com.petstation.dao.pheduyet.impl;

import com.petstation.dao.pheduyet.YeuCauPheDuyetDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.pheduyet.YeuCauPheDuyet;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class YeuCauPheDuyetDAOImpl implements YeuCauPheDuyetDAO {
    public YeuCauPheDuyetDAOImpl() {}

    @Override
    public long demSoLuongDangCho() {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM yeu_cau_phe_duyet WHERE trang_thai='CHO_DUYET'");
    }

    @Override
    public int capNhatTuChoi(String maPhieuXuat) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='TU_CHOI' WHERE ma_phieu_xuat = ?",
                maPhieuXuat);
    }

    @Override
    public int tuChoiBienBanKiemKe(String maBienBan) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='TU_CHOI' WHERE ma_bien_ban = ?",
                maBienBan);
    }

    @Override
    public long demApDungPheDuyet(String maHoaDon) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM hoa_don WHERE ma_hoa_don = ? AND trang_thai='DA_THANH_TOAN'",
                maHoaDon);
    }

    @Override
    public int capNhatApDungPheDuyet(String ghiChu, String maHoaDon) {
        return ThaoTacSQL.capNhat(
                "UPDATE hoa_don SET ghi_chu=? WHERE ma_hoa_don = ?", ghiChu, maHoaDon);
    }

    @Override
    public int capNhatGuiThaoTac(
            String maYeuCau,
            String loai,
            String maNhanVienGui,
            String loaiDoiTuong,
            String maDoiTuong,
            String lyDo,
            String noiDungTruoc,
            String noiDungSau,
            String payload) {
        return ThaoTacSQL.capNhat(
                "INSERT INTO yeu_cau_phe_duyet(ma_yeu_cau, loai, ma_nhan_vien_gui, loai_doi_tuong,"
                        + " ma_doi_tuong, ly_do, noi_dung_truoc, noi_dung_sau, payload, gui_luc)"
                        + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME())",
                maYeuCau,
                loai,
                maNhanVienGui,
                loaiDoiTuong,
                maDoiTuong,
                lyDo,
                noiDungTruoc,
                noiDungSau,
                payload);
    }

    @Override
    public List<ThongTinYeuCau> timTatCa() {
        List<ThongTinYeuCau> ketQua = new ArrayList<>();
        for (Object[] dong :
                ThaoTacSQL.truyVan(
                        "SELECT"
                            + " y.ma_yeu_cau,y.loai,n.ho_ten,y.gui_luc,y.ly_do,y.noi_dung_truoc,y.noi_dung_sau,y.trang_thai,COALESCE(a.ho_ten,N''),COALESCE(y.phan_hoi,N'')"
                            + " FROM yeu_cau_phe_duyet y JOIN nhan_vien n ON"
                            + " n.ma_nhan_vien=y.ma_nhan_vien_gui LEFT JOIN nhan_vien a ON"
                            + " a.ma_nhan_vien=y.ma_nhan_vien_xu_ly ORDER BY y.gui_luc DESC,"
                            + " y.ma_yeu_cau DESC")) {
            ketQua.add(
                    new ThongTinYeuCau(
                            DuLieu.chuyenThanhChuoi(dong[0]),
                            DuLieu.chuyenThanhChuoi(dong[1]),
                            DuLieu.chuyenThanhChuoi(dong[2]),
                            DuLieu.chuyenLocalDateTime(dong[3]),
                            DuLieu.chuyenThanhChuoi(dong[4]),
                            DuLieu.chuyenThanhChuoi(dong[5]),
                            DuLieu.chuyenThanhChuoi(dong[6]),
                            DuLieu.chuyenThanhChuoi(dong[7]),
                            DuLieu.chuyenThanhChuoi(dong[8]),
                            DuLieu.chuyenThanhChuoi(dong[9])));
        }
        return ketQua;
    }

    @Override
    public Optional<ThongTinDoiTuong> timDuyet(String maYeuCau) {
        List<Object[]> cacDong =
                ThaoTacSQL.truyVan(
                        "SELECT loai_doi_tuong,ma_doi_tuong,payload FROM"
                                + " yeu_cau_phe_duyet WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_yeu_cau=? AND trang_thai IN"
                                + " ('CHO_DUYET','CAN_THEM_THONG_TIN')",
                        maYeuCau);
        if (cacDong.isEmpty()) {
            return Optional.empty();
        }
        Object[] dong = cacDong.get(0);
        return Optional.of(
                new ThongTinDoiTuong(
                        DuLieu.chuyenThanhChuoi(dong[0]),
                        DuLieu.chuyenThanhChuoi(dong[1]),
                        DuLieu.chuyenThanhChuoi(dong[2])));
    }

    @Override
    public int capNhatDuyet(String maNhanVienXuLy, String maYeuCau) {
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET"
                        + " trang_thai='DA_DUYET', ma_nhan_vien_xu_ly=?, xu_ly_luc=SYSDATETIME()"
                        + " WHERE ma_yeu_cau=?",
                maNhanVienXuLy,
                maYeuCau);
    }

    @Override
    public Optional<ThongTinDoiTuong> timTuChoi(String maYeuCau) {
        List<Object[]> cacDong =
                ThaoTacSQL.truyVan(
                        "SELECT loai_doi_tuong,ma_doi_tuong FROM yeu_cau_phe_duyet"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_yeu_cau=? AND"
                                + " trang_thai IN ('CHO_DUYET','CAN_THEM_THONG_TIN')",
                        maYeuCau);
        if (cacDong.isEmpty()) {
            return Optional.empty();
        }
        Object[] dong = cacDong.get(0);
        return Optional.of(
                new ThongTinDoiTuong(
                        DuLieu.chuyenThanhChuoi(dong[0]), DuLieu.chuyenThanhChuoi(dong[1]), ""));
    }

    @Override
    public int tuChoiPhieuDoiTra(String maPhieuDoiTra) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_doi_tra SET trang_thai='TU_CHOI' WHERE ma_phieu = ? AND"
                        + " trang_thai='CHO_XU_LY'",
                maPhieuDoiTra);
    }

    @Override
    public int danhDauYeuCauTuChoi(String maNhanVienXuLy, String lyDo, String maYeuCau) {
        return ThaoTacSQL.capNhat(
                "UPDATE yeu_cau_phe_duyet SET trang_thai='TU_CHOI', ma_nhan_vien_xu_ly=?,"
                        + " phan_hoi=?, xu_ly_luc=SYSDATETIME() WHERE ma_yeu_cau=?",
                maNhanVienXuLy,
                lyDo,
                maYeuCau);
    }

    @Override
    public int yeuCauBoSung(String maNhanVienXuLy, String noiDung, String maYeuCau) {
        return ThaoTacSQL.capNhat(
                """
                UPDATE yeu_cau_phe_duyet
                SET trang_thai = 'CAN_THEM_THONG_TIN',
                    ma_nhan_vien_xu_ly = ?,
                    phan_hoi = ?,
                    xu_ly_luc = SYSDATETIME()
                WHERE ma_yeu_cau = ?
                  AND trang_thai = 'CHO_DUYET'
                """,
                maNhanVienXuLy,
                noiDung,
                maYeuCau);
    }

    @Override
    public Optional<YeuCauPheDuyet> timTheoMa(String maYeuCau) {
        List<Object[]> rows =
                ThaoTacSQL.truyVan(
                        "SELECT ma_yeu_cau, loai, ma_nhan_vien_gui, loai_doi_tuong, ma_doi_tuong,"
                                + " ly_do, trang_thai, ma_nhan_vien_xu_ly, phan_hoi, gui_luc,"
                                + " xu_ly_luc, CAST(noi_dung_truoc AS NVARCHAR(MAX)),"
                                + " CAST(noi_dung_sau AS NVARCHAR(MAX)), CAST(payload AS"
                                + " NVARCHAR(MAX)) FROM yeu_cau_phe_duyet WHERE ma_yeu_cau = ?",
                        maYeuCau);
        if (rows.isEmpty()) return Optional.empty();
        Object[] r = rows.get(0);
        YeuCauPheDuyet yc = new YeuCauPheDuyet();
        yc.setMaYeuCau(DuLieu.chuyenThanhChuoi(r[0]));
        yc.setLoai(DuLieu.chuyenThanhChuoi(r[1]));
        String maNvGui = DuLieu.chuyenThanhChuoi(r[2]);
        if (!maNvGui.isBlank()) {
            NhanVien nvGui = new NhanVien();
            nvGui.setMaNhanVien(maNvGui);
            yc.setNguoiGui(nvGui);
        }
        yc.setLoaiDoiTuong(DuLieu.chuyenThanhChuoi(r[3]));
        yc.setMaDoiTuong(DuLieu.chuyenThanhChuoi(r[4]));
        yc.setLyDo(DuLieu.chuyenThanhChuoi(r[5]));
        yc.setTrangThai(TrangThaiPheDuyet.tuMaDB(DuLieu.chuyenThanhChuoi(r[6])));
        if (r[7] != null) {
            NhanVien nvXuLy = new NhanVien();
            nvXuLy.setMaNhanVien(r[7].toString());
            yc.setNguoiXuLy(nvXuLy);
        }
        yc.setPhanHoi(r[8] == null ? null : r[8].toString());
        yc.setGuiLuc(DuLieu.chuyenLocalDateTime(r[9]));
        yc.setXuLyLuc(DuLieu.chuyenLocalDateTime(r[10]));
        yc.setNoiDungTruoc(r[11] == null ? null : r[11].toString());
        yc.setNoiDungSau(r[12] == null ? null : r[12].toString());
        yc.setPayload(r[13] == null ? null : r[13].toString());
        return Optional.of(yc);
    }
}
