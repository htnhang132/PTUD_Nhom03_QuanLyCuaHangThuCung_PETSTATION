package com.petstation.dao.pheduyet.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.truyVan;

import com.petstation.dao.pheduyet.DeXuatGiaDAO;
import com.petstation.model.chung.TrangThaiPheDuyet;
import com.petstation.model.kho.SanPham;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.pheduyet.DeXuatGia;
import com.petstation.util.DuLieu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class DeXuatGiaDAOImpl implements DeXuatGiaDAO {
    private static final Logger LOG = LoggerFactory.getLogger(DeXuatGiaDAOImpl.class);

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
                                + " yeu_cau_phe_duyet y JOIN san_pham p ON"
                                + " p.ma_san_pham=y.ma_doi_tuong WHERE y.loai_doi_tuong='DOI_GIA'"
                                + " AND (y.ma_yeu_cau=? OR y.ma_doi_tuong=?) AND"
                                + " y.trang_thai='CHO_DUYET' ORDER BY y.gui_luc DESC",
                        maDoiTuongHoacYeuCau,
                        maDoiTuongHoacYeuCau);
        if (dong.isEmpty()) return Optional.empty();
        Object[] d = dong.get(0);
        String truocStr = String.valueOf(d[1]);
        if (truocStr.contains(":")) truocStr = truocStr.substring(truocStr.indexOf(":") + 1).trim();
        String sauStr = String.valueOf(d[2]);
        if (sauStr.contains(":")) sauStr = sauStr.substring(sauStr.indexOf(":") + 1).trim();
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
                        + " (ma_yeu_cau=? OR ma_doi_tuong=?) AND trang_thai='CHO_DUYET'",
                maNhanVien,
                maDoiTuongHoacYeuCau,
                maDoiTuongHoacYeuCau);
    }

    @Override
    public void tuChoi(String maDoiTuongHoacYeuCau) {
        capNhat(
                "UPDATE yeu_cau_phe_duyet SET trang_thai='TU_CHOI', xu_ly_luc=SYSDATETIME()"
                        + " WHERE loai_doi_tuong='DOI_GIA' AND (ma_yeu_cau=? OR"
                        + " ma_doi_tuong=?) AND trang_thai='CHO_DUYET'",
                maDoiTuongHoacYeuCau,
                maDoiTuongHoacYeuCau);
    }

    @Override
    public Optional<DeXuatGia> timDeXuatTheoMa(String maYeuCau) {
        var dong =
                truyVan(
                        "SELECT ma_yeu_cau, ma_doi_tuong, CAST(noi_dung_truoc AS NVARCHAR(MAX)),"
                                + " CAST(noi_dung_sau AS NVARCHAR(MAX)), ly_do, ma_nhan_vien_gui,"
                                + " trang_thai, gui_luc FROM yeu_cau_phe_duyet WHERE loai_doi_tuong"
                                + " = 'DOI_GIA' AND ma_yeu_cau = ?",
                        maYeuCau);
        if (dong.isEmpty()) return Optional.empty();
        Object[] d = dong.get(0);
        DeXuatGia dx = new DeXuatGia();
        dx.setMaYeuCau(DuLieu.chuyenThanhChuoi(d[0]));
        String maSp = DuLieu.chuyenThanhChuoi(d[1]);
        if (!maSp.isBlank()) {
            SanPham sp = new SanPham();
            sp.setMaSanPham(maSp);
            dx.setSanPham(sp);
        }
        String truocStr = DuLieu.chuyenThanhChuoi(d[2]);
        if (truocStr.contains(":")) truocStr = truocStr.substring(truocStr.indexOf(':') + 1).trim();
        String sauStr = DuLieu.chuyenThanhChuoi(d[3]);
        if (sauStr.contains(":")) sauStr = sauStr.substring(sauStr.indexOf(':') + 1).trim();
        try {
            if (!truocStr.isBlank()) dx.setGiaCu((long) Double.parseDouble(truocStr));
            if (!sauStr.isBlank()) dx.setGiaMoi((long) Double.parseDouble(sauStr));
        } catch (NumberFormatException ex) {
            LOG.warn(
                    "Đề xuất giá {} có giá trị không phải số ('{}' -> '{}').",
                    dx.getMaYeuCau(),
                    truocStr,
                    sauStr,
                    ex);
        }
        dx.setLyDo(DuLieu.chuyenThanhChuoi(d[4]));
        String maNvGui = DuLieu.chuyenThanhChuoi(d[5]);
        if (!maNvGui.isBlank()) {
            NhanVien nvGui = new NhanVien();
            nvGui.setMaNhanVien(maNvGui);
            dx.setNguoiGui(nvGui);
        }
        dx.setTrangThai(TrangThaiPheDuyet.tuMaDB(DuLieu.chuyenThanhChuoi(d[6])));
        dx.setGuiLuc(DuLieu.chuyenLocalDateTime(d[7]));
        return Optional.of(dx);
    }
}
