package com.petstation.dao.pheduyet.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.truyVan;

import com.petstation.dao.pheduyet.DeXuatGiaDAO;
import com.petstation.util.DuLieu;

import java.util.Optional;

public final class DeXuatGiaDAOImpl implements DeXuatGiaDAO {
    public DeXuatGiaDAOImpl() {}

    @Override
    public Optional<ThongTinSanPham> timSanPham(String ma) {
        var ketQua =
                truyVan(
                        "SELECT ma_san_pham,gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_san_pham=?",
                        ma);
        if (ketQua.isEmpty()) {
            return Optional.empty();
        }
        Object[] dong = ketQua.get(0);
        String maSanPham = DuLieu.chuyenThanhChuoi(dong[0]);
        long giaBan =
                dong[1] instanceof Number so
                        ? so.longValue()
                        : (long) Double.parseDouble(DuLieu.chuyenThanhChuoi(dong[1]));
        return Optional.of(new ThongTinSanPham(maSanPham, giaBan));
    }

    @Override
    public String them(String maSanPham, String maNhanVien, long cu, long moi, String lyDo) {
        var hienTai =
                truyVan(
                        "SELECT ma_san_pham, gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_san_pham=?",
                        maSanPham);
        if (hienTai.isEmpty()) throw new IllegalArgumentException("Không tìm thấy sản phẩm.");
        long yeuCauMo =
                laySo(
                        "SELECT COUNT(*) FROM yeu_cau_phe_duyet WHERE loai_doi_tuong='DOI_GIA' AND"
                                + " ma_doi_tuong=? AND trang_thai IN"
                                + " ('CHO_DUYET','CAN_THEM_THONG_TIN')",
                        maSanPham);
        if (yeuCauMo > 0)
            throw new IllegalArgumentException(
                    "Sản phẩm đã có đề xuất giá chưa xử lý. Hãy xử lý đề xuất đó trước.");
        return maSanPham;
    }

    @Override
    public Optional<ThongTinDeXuat> timChoDuyet(String maDoiTuongHoacYeuCau) {
        var dong =
                truyVan(
                        "SELECT y.ma_doi_tuong, CAST(y.noi_dung_truoc AS NVARCHAR(MAX)),"
                                + " CAST(y.noi_dung_sau AS NVARCHAR(MAX)), p.gia_ban FROM"
                                + " yeu_cau_phe_duyet y WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p ON"
                                + " p.ma_san_pham=y.ma_doi_tuong WHERE y.loai_doi_tuong='DOI_GIA'"
                                + " AND (y.ma_yeu_cau=? OR y.ma_doi_tuong=?) AND"
                                + " y.trang_thai IN ('CHO_DUYET','CAN_THEM_THONG_TIN') ORDER BY"
                                + " y.gui_luc DESC",
                        maDoiTuongHoacYeuCau,
                        maDoiTuongHoacYeuCau);
        if (dong.isEmpty()) return Optional.empty();
        Object[] d = dong.get(0);
        String truocStr = String.valueOf(d[1]);
        if (truocStr.contains(":")) {
            truocStr = truocStr.substring(truocStr.lastIndexOf(':') + 1).trim();
        }
        String sauStr = String.valueOf(d[2]);
        if (sauStr.contains(":")) {
            sauStr = sauStr.substring(sauStr.lastIndexOf(':') + 1).trim();
        }
        long giaHienTai = (long) Double.parseDouble(truocStr);
        long giaDeXuat = (long) Double.parseDouble(sauStr);
        String spMa = DuLieu.chuyenThanhChuoi(d[0]);
        long giaBanHienTai =
                d[3] instanceof Number so
                        ? so.longValue()
                        : (long) Double.parseDouble(DuLieu.chuyenThanhChuoi(d[3]));
        return Optional.of(new ThongTinDeXuat(spMa, giaHienTai, giaDeXuat, giaBanHienTai));
    }

    @Override
    public void apDung(String maDoiTuongHoacYeuCau, String maSanPham, long gia, String maNhanVien) {
        capNhat("UPDATE san_pham SET gia_ban=? WHERE ma_san_pham=?", gia, maSanPham);
        capNhat(
                "UPDATE yeu_cau_phe_duyet SET trang_thai='DA_DUYET', ma_nhan_vien_xu_ly=?,"
                        + " xu_ly_luc=SYSDATETIME() WHERE loai_doi_tuong='DOI_GIA' AND"
                        + " (ma_yeu_cau=? OR ma_doi_tuong=?) AND trang_thai IN"
                        + " ('CHO_DUYET','CAN_THEM_THONG_TIN')",
                maNhanVien,
                maDoiTuongHoacYeuCau,
                maDoiTuongHoacYeuCau);
    }

    @Override
    public void tuChoi(String maDoiTuongHoacYeuCau) {
        capNhat(
                "UPDATE yeu_cau_phe_duyet SET trang_thai='TU_CHOI', xu_ly_luc=SYSDATETIME()"
                        + " WHERE loai_doi_tuong='DOI_GIA' AND (ma_yeu_cau=? OR"
                        + " ma_doi_tuong=?) AND trang_thai IN"
                        + " ('CHO_DUYET','CAN_THEM_THONG_TIN')",
                maDoiTuongHoacYeuCau,
                maDoiTuongHoacYeuCau);
    }
}
