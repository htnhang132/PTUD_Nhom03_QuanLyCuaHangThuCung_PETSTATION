package com.petstation.dao.thongke.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.database.ThaoTacSQL.truyVanDanhSach;

import com.petstation.dao.hethong.impl.CaiDatDAOImpl;
import com.petstation.dao.thongke.ThongKeDAO;
import com.petstation.dto.ChiTietBaoCao;
import com.petstation.dto.ChiTietBaoCao.BienDongKho;
import com.petstation.dto.ChiTietBaoCao.ChiTieuKhachHang;
import com.petstation.dto.ChiTietBaoCao.CocTrongKy;
import com.petstation.dto.ChiTietBaoCao.CongNoNhaCungCap;
import com.petstation.dto.ChiTietBaoCao.DemTrangThai;
import com.petstation.dto.ChiTietBaoCao.DichVuSpaThongKe;
import com.petstation.dto.ChiTietBaoCao.DoanhThuHoanTat;
import com.petstation.dto.ChiTietBaoCao.DoanhThuTheoNgay;
import com.petstation.dto.ChiTietBaoCao.HangTonDong;
import com.petstation.dto.ChiTietBaoCao.HaoHutKiemKe;
import com.petstation.dto.ChiTietBaoCao.HopDongTrongKy;
import com.petstation.dto.ChiTietBaoCao.LichLamViecNhanVien;
import com.petstation.dto.ChiTietBaoCao.LoSapHetHan;
import com.petstation.dto.ChiTietBaoCao.LuotKhach;
import com.petstation.dto.ChiTietBaoCao.LuuTruQuyetToan;
import com.petstation.dto.ChiTietBaoCao.SanPhamBanChay;
import com.petstation.dto.ChiTietBaoCao.SuDungVoucher;
import com.petstation.dto.ChiTietBaoCao.ThongKeNhom;
import com.petstation.dto.ChiTietBaoCao.TonKhoHienTai;
import com.petstation.dto.ChiTietBaoCao.TongHopCaThuNgan;
import com.petstation.dto.ChiTietBaoCao.VoucherThongKe;
import com.petstation.dto.KhoangNgay;
import com.petstation.util.DuLieu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Triển khai các câu truy vấn SQL tổng hợp số liệu cho 7 phân hệ Thống kê & Báo cáo.
public final class ThongKeDAOImpl implements ThongKeDAO {
    private static final Logger LOG = LoggerFactory.getLogger(ThongKeDAOImpl.class);

    public ThongKeDAOImpl() {}

    private static int soNguyen(Object giaTri) {
        return giaTri instanceof Number n ? n.intValue() : 0;
    }

    private static long soDai(Object giaTri) {
        return giaTri instanceof Number n ? n.longValue() : 0L;
    }

    private static double soThuc(Object giaTri) {
        return giaTri instanceof Number n ? n.doubleValue() : 0.0;
    }

    private static Double soThucCoTheNull(Object giaTri) {
        return giaTri instanceof Number n ? n.doubleValue() : null;
    }

    private static ThongKeNhom taoThongKeNhom(Object[] r) {
        return new ThongKeNhom(DuLieu.chuyenThanhChuoi(r[0]), soThuc(r[1]), soNguyen(r[2]));
    }

    private static DemTrangThai taoDemTrangThai(Object[] r) {
        return new DemTrangThai(DuLieu.chuyenThanhChuoi(r[0]), soNguyen(r[1]));
    }

    private static CongNoNhaCungCap taoCongNoNcc(Object[] r) {
        return new CongNoNhaCungCap(
                DuLieu.chuyenThanhChuoi(r[0]),
                DuLieu.chuyenThanhChuoi(r[1]),
                soThuc(r[2]),
                soThuc(r[3]),
                soThuc(r[4]));
    }

    // 1. THỐNG KÊ DOANH THU

    @Override
    public List<DoanhThuTheoNgay> layDoanhThuTheoNgay(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "WITH ledger AS (SELECT CAST(h.thanh_toan_luc AS date) ngay,h.tong_tien"
                        + " so_tien FROM hoa_don h JOIN phien_lam_viec s"
                        + " ON s.ma_phien=h.ma_phien WHERE h.trang_thai='DA_THANH_TOAN'"
                        + " AND (? IS NULL OR s.ma_nhan_vien = ?)"
                        + " UNION ALL SELECT CAST(p.tao_luc AS date),p.chenh_lech FROM"
                        + " phieu_doi_tra p JOIN hoa_don h ON h.ma_hoa_don=p.ma_hoa_don"
                        + " JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien WHERE"
                        + " p.trang_thai='HOAN_TAT' AND p.chenh_lech < 0"
                        + " AND (? IS NULL OR s.ma_nhan_vien = ?)"
                        + " UNION ALL SELECT CAST(k.tao_luc AS date),k.so_tien FROM"
                        + " lich_su_thu_coc_hotel k LEFT JOIN phien_lam_viec s ON"
                        + " s.ma_phien=k.ma_phien WHERE (? IS NULL OR"
                        + " COALESCE(k.ma_nhan_vien_thu, s.ma_nhan_vien) = ?)"
                        + ") SELECT ngay,SUM(so_tien) FROM ledger WHERE ngay>=? AND ngay<?"
                        + " GROUP BY ngay ORDER BY ngay",
                r -> new DoanhThuTheoNgay(DuLieu.chuyenLocalDate(r[0]), soThuc(r[1])),
                maNhanVien,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                tu,
                denNgay);
    }

    private static String nhomDoanhThu(String nhomTongHop, String bieuThucPhuongThuc) {
        return switch (nhomTongHop) {
            case "method" -> "COALESCE(" + bieuThucPhuongThuc + ", N'Chưa rõ')";
            case "staff" -> "COALESCE(n.ho_ten, N'Chưa rõ')";
            case "shift" -> "COALESCE(s.ten_ca, N'Ngoài ca')";
            default ->
                    "CASE WHEN EXISTS(SELECT 1 FROM phieu_tiep_nhan_spa t WHERE"
                            + " t.ma_hoa_don=h.ma_hoa_don) THEN 'Spa' WHEN EXISTS(SELECT 1 FROM"
                            + " hop_dong_luu_tru t WHERE t.ma_hoa_don=h.ma_hoa_don) THEN 'Hotel'"
                            + " ELSE N'Hàng hóa' END";
        };
    }

    @Override
    public List<ThongKeNhom> layDoanhThu(
            LocalDate tu, LocalDate denNgay, String nhomTongHop, String maNhanVien) {
        String nhom = nhomDoanhThu(nhomTongHop, "h.hinh_thuc_thanh_toan");
        String nhomDoiTra = nhomDoanhThu(nhomTongHop, "h.hinh_thuc_thanh_toan");
        String nhomCoc =
                switch (nhomTongHop) {
                    case "method" -> "COALESCE(k.phuong_thuc, N'Chưa rõ')";
                    case "staff" -> "COALESCE(n.ho_ten, N'Chưa rõ')";
                    case "shift" -> "COALESCE(s.ten_ca, N'Ngoài ca')";
                    default -> "'Hotel'";
                };
        return truyVanDanhSach(
                "WITH ledger AS (SELECT "
                        + nhom
                        + " nhan,h.tong_tien so_tien,h.ma_hoa_don hoa_don,CAST(h.thanh_toan_luc AS"
                        + " date) ngay FROM hoa_don h JOIN phien_lam_viec s ON"
                        + " s.ma_phien=h.ma_phien JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien=s.ma_nhan_vien WHERE h.trang_thai='DA_THANH_TOAN' AND"
                        + " (? IS NULL OR s.ma_nhan_vien = ?)"
                        + " UNION ALL SELECT "
                        + nhomDoiTra
                        + ",p.chenh_lech,NULL,CAST(p.tao_luc AS date) FROM phieu_doi_tra p"
                        + " JOIN hoa_don h ON h.ma_hoa_don=p.ma_hoa_don JOIN phien_lam_viec"
                        + " s ON s.ma_phien=h.ma_phien JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien=s.ma_nhan_vien WHERE p.trang_thai='HOAN_TAT'"
                        + " AND p.chenh_lech < 0 AND (? IS NULL OR s.ma_nhan_vien = ?)"
                        + " UNION ALL SELECT "
                        + nhomCoc
                        + ",k.so_tien,NULL,CAST(k.tao_luc AS date) FROM"
                        + " lich_su_thu_coc_hotel k LEFT JOIN phien_lam_viec s ON"
                        + " s.ma_phien=k.ma_phien LEFT JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien=COALESCE(k.ma_nhan_vien_thu, s.ma_nhan_vien) WHERE"
                        + " (? IS NULL OR COALESCE(k.ma_nhan_vien_thu, s.ma_nhan_vien) = ?)"
                        + ") SELECT nhan,SUM(so_tien),COUNT(DISTINCT hoa_don) FROM ledger"
                        + " WHERE ngay>=? AND ngay<? GROUP BY nhan ORDER BY SUM(so_tien)"
                        + " DESC",
                ThongKeDAOImpl::taoThongKeNhom,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                maNhanVien,
                tu,
                denNgay);
    }

    @Override
    public List<DoanhThuHoanTat> layDoanhThuHoanTat(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT CAST(h.thanh_toan_luc AS date), COUNT(DISTINCT h.ma_hoa_don),"
                        + " SUM(h.tam_tinh + COALESCE(c.coc, 0)), SUM(h.giam_gia),"
                        + " SUM(COALESCE(dt.hoan_tra, 0)), SUM(h.tong_tien + COALESCE(c.coc, 0)"
                        + " - COALESCE(dt.hoan_tra, 0)), SUM(h.tong_tien + COALESCE(c.coc, 0) -"
                        + " COALESCE(dt.hoan_tra, 0) - COALESCE(gv.gia_von, 0)) FROM hoa_don h"
                        + " OUTER APPLY (SELECT SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE"
                        + " ma_hoa_don=h.ma_hoa_don AND da_nhan_coc=1) c OUTER APPLY (SELECT"
                        + " SUM(CASE WHEN p.chenh_lech < 0 THEN -p.chenh_lech ELSE 0 END)"
                        + " hoan_tra FROM phieu_doi_tra p WHERE p.ma_hoa_don=h.ma_hoa_don AND"
                        + " p.trang_thai='HOAN_TAT') dt OUTER APPLY (SELECT SUM(ct.so_luong *"
                        + " COALESCE(NULLIF(l.gia_nhap, 0), sp.gia_nhap_gan_nhat, ct.don_gia *"
                        + " 0.6)) gia_von FROM chi_tiet_hoa_don ct JOIN san_pham sp ON"
                        + " sp.ma_san_pham=ct.ma_san_pham LEFT JOIN lo_hang l ON"
                        + " l.ma_lo=ct.ma_lo WHERE ct.ma_hoa_don=h.ma_hoa_don) gv JOIN"
                        + " phien_lam_viec s ON s.ma_phien=h.ma_phien WHERE"
                        + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND"
                        + " h.thanh_toan_luc<? AND (? IS NULL OR s.ma_nhan_vien = ?) GROUP BY"
                        + " CAST(h.thanh_toan_luc AS date) ORDER BY 1",
                r ->
                        new DoanhThuHoanTat(
                                DuLieu.chuyenLocalDate(r[0]),
                                soNguyen(r[1]),
                                soThuc(r[2]),
                                soThuc(r[3]),
                                soThuc(r[4]),
                                soThuc(r[5]),
                                soThuc(r[6])),
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<ThongKeNhom> layDoanhThuTheoGio(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT RIGHT('0' + CAST(DATEPART(hour, h.thanh_toan_luc) AS varchar(2)), 2)"
                        + " + ':00 - ' + RIGHT('0' + CAST(DATEPART(hour, h.thanh_toan_luc) + 1 AS"
                        + " varchar(2)), 2) + ':00' gio, SUM(h.tong_tien) doanh_thu,"
                        + " COUNT(h.ma_hoa_don) so_hoa_don FROM hoa_don h JOIN phien_lam_viec s"
                        + " ON s.ma_phien=h.ma_phien WHERE h.trang_thai='DA_THANH_TOAN' AND"
                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND (? IS NULL OR"
                        + " s.ma_nhan_vien = ?) GROUP BY DATEPART(hour, h.thanh_toan_luc) ORDER BY"
                        + " DATEPART(hour, h.thanh_toan_luc)",
                ThongKeDAOImpl::taoThongKeNhom,
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<ThongKeNhom> layDoanhThuTheoThu(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT CASE ((DATEPART(weekday, h.thanh_toan_luc) + @@DATEFIRST - 1) % 7)"
                        + " WHEN 0 THEN N'Chủ nhật' WHEN 1 THEN N'Thứ 2' WHEN 2 THEN N'Thứ 3'"
                        + " WHEN 3 THEN N'Thứ 4' WHEN 4 THEN N'Thứ 5' WHEN 5 THEN N'Thứ 6' ELSE"
                        + " N'Thứ 7' END thu, SUM(h.tong_tien) doanh_thu, COUNT(h.ma_hoa_don)"
                        + " so_hoa_don FROM hoa_don h JOIN phien_lam_viec s ON"
                        + " s.ma_phien=h.ma_phien WHERE h.trang_thai='DA_THANH_TOAN' AND"
                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND (? IS NULL OR"
                        + " s.ma_nhan_vien = ?) GROUP BY ((DATEPART(weekday, h.thanh_toan_luc) +"
                        + " @@DATEFIRST - 1) % 7) ORDER BY CASE WHEN ((DATEPART(weekday,"
                        + " h.thanh_toan_luc) + @@DATEFIRST - 1) % 7) = 0 THEN 7 ELSE"
                        + " ((DATEPART(weekday, h.thanh_toan_luc) + @@DATEFIRST - 1) % 7) END",
                ThongKeDAOImpl::taoThongKeNhom, tu, denNgay, maNhanVien, maNhanVien);
    }

    // 2. THỐNG KÊ HÀNG HÓA & TỒN KHO

    @Override
    public List<SanPhamBanChay> laySanPhamBanChay(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT TOP(10) p.ma_san_pham,p.ten,SUM(i.so_luong)-SUM(COALESCE(r.qty,0))"
                    + " qty,SUM(CASE WHEN h.tam_tinh>0 THEN"
                    + " i.so_luong*i.don_gia*(h.tong_tien+COALESCE(ex.credit,0))/h.tam_tinh ELSE 0"
                    + " END)-SUM(COALESCE(r.refund,0)) amount FROM chi_tiet_hoa_don i JOIN san_pham"
                    + " p ON p.ma_san_pham=i.ma_san_pham JOIN hoa_don h ON"
                    + " h.ma_hoa_don=i.ma_hoa_don JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien"
                    + " OUTER APPLY(SELECT SUM(c.so_luong) qty,SUM(c.tien_hoan) refund FROM"
                    + " chi_tiet_doi_tra c JOIN phieu_doi_tra x ON x.ma_phieu=c.ma_phieu WHERE"
                    + " c.ma_hoa_don=i.ma_hoa_don AND c.ma_san_pham=i.ma_san_pham AND"
                    + " x.trang_thai='HOAN_TAT') r OUTER APPLY(SELECT SUM(dx.gia_tri_duoc_doi)"
                    + " credit FROM phieu_doi_tra dx WHERE dx.ma_hoa_don_thay_the=h.ma_hoa_don AND"
                    + " dx.loai_phieu='DOI_HANG' AND dx.trang_thai='HOAN_TAT') ex WHERE"
                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                    + " AND (? IS NULL OR s.ma_nhan_vien = ?) GROUP BY p.ma_san_pham,p.ten ORDER BY"
                    + " amount DESC,qty DESC,p.ma_san_pham",
                r ->
                        new SanPhamBanChay(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                soDai(r[2]),
                                soThuc(r[3])),
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<TonKhoHienTai> timKho() {
        return truyVanDanhSach(
                "SELECT"
                    + " p.ma_san_pham,p.ten,COALESCE(SUM(l.so_luong_ton),0),p.ton_toi_thieu,p.gia_nhap_gan_nhat"
                    + " FROM san_pham p LEFT JOIN lo_hang l ON l.ma_san_pham=p.ma_san_pham AND"
                    + " l.so_luong_ton>0 AND l.trang_thai='SAN_SANG' GROUP BY"
                    + " p.ma_san_pham,p.ten,p.ton_toi_thieu,p.gia_nhap_gan_nhat ORDER BY"
                    + " p.ma_san_pham",
                r ->
                        new TonKhoHienTai(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                soNguyen(r[2]),
                                soNguyen(r[3]),
                                soThuc(r[4])));
    }

    @Override
    public List<LoSapHetHan> timHanSuDung(int soNgayCanhBao) {
        return truyVanDanhSach(
                "SELECT p.ma_san_pham,p.ten,l.so_lo,l.han_su_dung,l.so_luong_ton FROM"
                        + " lo_hang l JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE"
                        + " l.so_luong_ton>0 AND l.trang_thai='SAN_SANG' AND"
                        + " l.han_su_dung>=CAST(GETDATE() AS date) AND"
                        + " l.han_su_dung<=DATEADD(day,?,CAST(GETDATE() AS date)) ORDER BY"
                        + " l.han_su_dung",
                r ->
                        new LoSapHetHan(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2]),
                                DuLieu.chuyenLocalDate(r[3]),
                                soNguyen(r[4])),
                soNgayCanhBao);
    }

    @Override
    public List<BienDongKho> layBienDongKho(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "WITH dong AS (SELECT p.ma_san_pham,p.ten,N'Nhập kho' loai,c.so_luong FROM"
                        + " chi_tiet_phieu_nhap c JOIN phieu_nhap_kho n ON"
                        + " n.ma_phieu_nhap=c.ma_phieu_nhap JOIN san_pham p ON"
                        + " p.ma_san_pham=c.ma_san_pham WHERE n.trang_thai='DA_NHAP' AND"
                        + " n.ngay_nhap>=? AND n.ngay_nhap<? UNION ALL SELECT"
                        + " p.ma_san_pham,p.ten,N'Bán hàng',-c.so_luong FROM chi_tiet_hoa_don c"
                        + " JOIN san_pham p ON p.ma_san_pham=c.ma_san_pham JOIN hoa_don h ON"
                        + " h.ma_hoa_don=c.ma_hoa_don WHERE h.trang_thai='DA_THANH_TOAN' AND"
                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? UNION ALL SELECT"
                        + " p.ma_san_pham,p.ten,N'Nhận hàng đổi',c.so_luong FROM"
                        + " chi_tiet_doi_tra c JOIN phieu_doi_tra n ON n.ma_phieu=c.ma_phieu"
                        + " JOIN lo_hang l ON l.ma_lo=c.ma_lo JOIN san_pham p ON"
                        + " p.ma_san_pham=l.ma_san_pham WHERE n.trang_thai='HOAN_TAT' AND"
                        + " n.tao_luc>=? AND n.tao_luc<? UNION ALL SELECT"
                        + " p.ma_san_pham,p.ten,N'Xuất hủy',-c.so_luong FROM"
                        + " chi_tiet_phieu_xuat c JOIN phieu_xuat_kho n ON"
                        + " n.ma_phieu_xuat=c.ma_phieu_xuat JOIN lo_hang l ON l.ma_lo=c.ma_lo"
                        + " JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE"
                        + " n.trang_thai='DA_XUAT' AND n.tao_luc>=? AND n.tao_luc<?) SELECT"
                        + " ma_san_pham,ten,loai,SUM(so_luong) FROM dong GROUP BY"
                        + " ma_san_pham,ten,loai ORDER BY ma_san_pham,loai",
                r ->
                        new BienDongKho(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2]),
                                soNguyen(r[3])),
                tu,
                denNgay,
                tu,
                denNgay,
                tu,
                denNgay,
                tu,
                denNgay);
    }

    private int docSoNgayTonDong() {
        int soNgay = 45;
        try {
            soNgay =
                    Math.max(
                            1,
                            Integer.parseInt(
                                    String.valueOf(
                                                    Objects.requireNonNullElse(
                                                            new CaiDatDAOImpl()
                                                                    .timGiaTri("SO_NGAY_TON_DONG"),
                                                            "45"))
                                            .trim()));
        } catch (NumberFormatException ex) {
            LOG.warn("Cấu hình SO_NGAY_TON_DONG không hợp lệ, dùng mặc định 45 ngày.", ex);
        }
        return soNgay;
    }

    @Override
    public List<HangTonDong> layHangTonDong() {
        int soNgay = docSoNgayTonDong();
        return truyVanDanhSach(
                "SELECT p.ma_san_pham, p.ten, COALESCE(SUM(l.so_luong_ton), 0) ton,"
                        + " COALESCE(p.gia_nhap_gan_nhat, 0) gia_nhap,"
                        + " COALESCE(SUM(l.so_luong_ton), 0) * COALESCE(p.gia_nhap_gan_nhat, 0)"
                        + " von_chon FROM san_pham p JOIN lo_hang l ON l.ma_san_pham ="
                        + " p.ma_san_pham WHERE l.so_luong_ton > 0 AND NOT EXISTS (SELECT 1"
                        + " FROM chi_tiet_hoa_don ct JOIN hoa_don hd ON hd.ma_hoa_don ="
                        + " ct.ma_hoa_don WHERE ct.ma_san_pham = p.ma_san_pham AND"
                        + " hd.trang_thai = 'DA_THANH_TOAN' AND hd.thanh_toan_luc >="
                        + " DATEADD(day, -?, GETDATE())) GROUP BY p.ma_san_pham, p.ten,"
                        + " p.gia_nhap_gan_nhat ORDER BY von_chon DESC",
                r ->
                        new HangTonDong(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                soNguyen(r[2]),
                                soThuc(r[3]),
                                soThuc(r[4])),
                soNgay);
    }

    @Override
    public List<HaoHutKiemKe> layHaoHutKiemKe(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "SELECT p.ma_san_pham, p.ten, l.so_lo, (ct.ton_thuc_te - ct.ton_he_thong)"
                        + " chenh_lech, (ct.ton_thuc_te - ct.ton_he_thong) *"
                        + " COALESCE(p.gia_nhap_gan_nhat, 0) gia_tri_lech,"
                        + " COALESCE(ct.nguyen_nhan, N'Chưa rõ') ly_do, CONVERT(varchar(19),"
                        + " bb.tao_luc, 120) ngay_tao FROM chi_tiet_kiem_ke ct JOIN"
                        + " bien_ban_kiem_ke bb ON bb.ma_bien_ban = ct.ma_bien_ban JOIN lo_hang"
                        + " l ON l.ma_lo = ct.ma_lo JOIN san_pham p ON p.ma_san_pham ="
                        + " l.ma_san_pham WHERE bb.trang_thai='DA_DUYET' AND ct.ton_thuc_te <>"
                        + " ct.ton_he_thong AND bb.tao_luc>=? AND bb.tao_luc<? ORDER BY"
                        + " bb.tao_luc DESC",
                r ->
                        new HaoHutKiemKe(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2]),
                                soNguyen(r[3]),
                                soThuc(r[4]),
                                DuLieu.chuyenThanhChuoi(r[5]),
                                DuLieu.chuyenThanhChuoi(r[6])),
                tu,
                denNgay);
    }

    // 3. THỐNG KÊ DỊCH VỤ SPA

    @Override
    public List<DichVuSpaThongKe> layDuLieuSpa(LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT COALESCE(g.ma_goi,t.ma_phieu),COALESCE(NULLIF(t.mo_ta_dich_vu,N''),"
                        + "g.ten_goi,N'Dịch vụ lẻ'),SUM(t.so_luong),SUM(CASE WHEN h.tam_tinh>0"
                        + " THEN t.tong_tien*h.tong_tien/h.tam_tinh ELSE t.tong_tien END) FROM"
                        + " phieu_tiep_nhan_spa t LEFT JOIN goi_dich_vu_spa g ON"
                        + " g.ma_goi=t.ma_goi JOIN hoa_don h ON h.ma_hoa_don=t.ma_hoa_don JOIN"
                        + " phien_lam_viec s ON s.ma_phien=h.ma_phien WHERE h.thanh_toan_luc>=?"
                        + " AND h.thanh_toan_luc<? AND t.trang_thai='DA_THANH_TOAN' AND"
                        + " h.trang_thai='DA_THANH_TOAN' AND (? IS NULL OR"
                        + " COALESCE(t.ma_nhan_vien,s.ma_nhan_vien) = ?) GROUP BY"
                        + " COALESCE(g.ma_goi,t.ma_phieu),COALESCE(NULLIF(t.mo_ta_dich_vu,N''),"
                        + "g.ten_goi,N'Dịch vụ lẻ') ORDER BY SUM(t.so_luong) DESC",
                r ->
                        new DichVuSpaThongKe(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                soNguyen(r[2]),
                                soThuc(r[3])),
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<DemTrangThai> layLichHen(LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT trang_thai,COUNT(*) FROM lich_hen WHERE bat_dau>=? AND bat_dau<?"
                        + " AND (? IS NULL OR ma_nhan_vien = ?)"
                        + " GROUP BY trang_thai ORDER BY trang_thai",
                ThongKeDAOImpl::taoDemTrangThai,
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<DemTrangThai> layPhieuSpaTheoTrangThai(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT trang_thai,COUNT(*) FROM phieu_tiep_nhan_spa WHERE tao_luc>=? AND"
                        + " tao_luc<? AND (? IS NULL OR ma_nhan_vien = ?) GROUP BY trang_thai"
                        + " ORDER BY trang_thai",
                ThongKeDAOImpl::taoDemTrangThai,
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    // 4. THỐNG KÊ LƯU TRÚ HOTEL

    @Override
    public List<DemTrangThai> timTrangThaiPhong() {
        return truyVanDanhSach(
                "SELECT trang_thai,COUNT(*) FROM phong_hotel GROUP BY trang_thai ORDER BY"
                        + " trang_thai",
                ThongKeDAOImpl::taoDemTrangThai);
    }

    @Override
    public List<LuuTruQuyetToan> layDuLieuHotel(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT p.loai_phong,COUNT(*),SUM(CASE WHEN"
                    + " DATEDIFF(day,h.ngay_vao,COALESCE(h.ngay_ra_thuc_te,h.ngay_ra_du_kien))<1"
                    + " THEN 1 ELSE"
                    + " DATEDIFF(day,h.ngay_vao,COALESCE(h.ngay_ra_thuc_te,h.ngay_ra_du_kien))"
                    + " END),SUM(d.tong_tien+CASE WHEN h.da_nhan_coc=1 THEN h.tien_coc ELSE 0 END)"
                    + " FROM hop_dong_luu_tru h JOIN phong_hotel p ON p.ma_phong=h.ma_phong JOIN"
                    + " hoa_don d ON d.ma_hoa_don=h.ma_hoa_don JOIN phien_lam_viec s ON"
                    + " s.ma_phien=d.ma_phien WHERE h.trang_thai='DA_TRA_PHONG' AND"
                    + " d.trang_thai='DA_THANH_TOAN' AND d.thanh_toan_luc>=? AND d.thanh_toan_luc<?"
                    + " AND (? IS NULL OR s.ma_nhan_vien = ?) GROUP BY p.loai_phong ORDER BY"
                    + " p.loai_phong",
                r ->
                        new LuuTruQuyetToan(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                soNguyen(r[1]),
                                soNguyen(r[2]),
                                soThuc(r[3])),
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<HopDongTrongKy> layHopDongTrongKy(
            LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "SELECT h.trang_thai,COUNT(*),SUM(h.gia_dem),SUM(CASE WHEN h.da_nhan_coc=1"
                        + " THEN h.tien_coc ELSE 0 END) FROM hop_dong_luu_tru h WHERE"
                        + " h.ngay_vao>=? AND h.ngay_vao<? AND (? IS NULL OR"
                        + " h.nhan_coc_boi = ? OR h.ma_phien_nhan_coc IS NULL OR EXISTS(SELECT 1"
                        + " FROM phien_lam_viec s WHERE s.ma_phien=h.ma_phien_nhan_coc AND"
                        + " s.ma_nhan_vien = ?)) GROUP BY h.trang_thai",
                r ->
                        new HopDongTrongKy(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                soNguyen(r[1]),
                                soThuc(r[2]),
                                soThuc(r[3])),
                tu,
                denNgay,
                maNhanVien,
                maNhanVien,
                maNhanVien);
    }

    @Override
    public List<CocTrongKy> layCocTrongKy(LocalDate tu, LocalDate denNgay, String maNhanVien) {
        return truyVanDanhSach(
                "WITH lan_thu AS ( SELECT l.ma_hop_dong,l.khoa_thu_coc, MIN(l.tao_luc)"
                        + " ngay_nhan,SUM(l.so_tien) so_tien FROM lich_su_thu_coc_hotel l LEFT"
                        + " JOIN phien_lam_viec s ON s.ma_phien=l.ma_phien WHERE l.tao_luc>=?"
                        + " AND l.tao_luc<? AND (? IS NULL OR COALESCE(l.ma_nhan_vien_thu,"
                        + " s.ma_nhan_vien) = ?) GROUP BY l.ma_hop_dong,l.khoa_thu_coc) SELECT"
                        + " CAST(ngay_nhan AS date),SUM(so_tien),COUNT(*) FROM lan_thu GROUP BY"
                        + " CAST(ngay_nhan AS date) ORDER BY 1",
                r -> new CocTrongKy(DuLieu.chuyenLocalDate(r[0]), soThuc(r[1]), soNguyen(r[2])),
                tu,
                denNgay,
                maNhanVien,
                maNhanVien);
    }

    // 5. THỐNG KÊ KHÁCH HÀNG & VOUCHER

    @Override
    public List<ChiTieuKhachHang> timCacKhachHang(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "SELECT k.ma_khach_hang,k.ho_ten,k.hang_thanh_vien,k.diem_tich_luy,SUM(h.tong_tien)"
                    + " FROM khach_hang k JOIN hoa_don h ON h.ma_khach_hang=k.ma_khach_hang WHERE"
                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                    + " GROUP BY k.ma_khach_hang,k.ho_ten,k.hang_thanh_vien,k.diem_tich_luy HAVING"
                    + " COUNT(DISTINCT h.ma_hoa_don)>0 AND SUM(h.tong_tien)>0 ORDER BY"
                    + " SUM(h.tong_tien) DESC,k.ma_khach_hang",
                r ->
                        new ChiTieuKhachHang(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2]),
                                soNguyen(r[3]),
                                soThuc(r[4])),
                tu,
                denNgay);
    }

    @Override
    public List<LuotKhach> layLuotKhach(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "WITH khach AS (SELECT k.ma_khach_hang,CASE WHEN EXISTS(SELECT 1 FROM"
                        + " hoa_don cu WHERE cu.ma_khach_hang=k.ma_khach_hang AND"
                        + " cu.trang_thai='DA_THANH_TOAN' AND cu.thanh_toan_luc<?) THEN N'Khách"
                        + " quay lại' ELSE N'Khách mua lần đầu' END nhom FROM khach_hang k)"
                        + " SELECT k.nhom,COUNT(DISTINCT"
                        + " k.ma_khach_hang),COUNT(h.ma_hoa_don),SUM(h.tong_tien) FROM khach k"
                        + " JOIN hoa_don h ON h.ma_khach_hang=k.ma_khach_hang WHERE"
                        + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND"
                        + " h.thanh_toan_luc<? GROUP BY k.nhom",
                r ->
                        new LuotKhach(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                soNguyen(r[1]),
                                soNguyen(r[2]),
                                soThuc(r[3])),
                tu,
                tu,
                denNgay);
    }

    @Override
    public List<VoucherThongKe> timCacVoucher() {
        return truyVanDanhSach(
                "SELECT ma_voucher,menh_gia,da_dung,so_luong,hieu_luc_den FROM voucher"
                        + " ORDER BY ma_voucher",
                r ->
                        new VoucherThongKe(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                soThuc(r[1]),
                                soNguyen(r[2]),
                                soNguyen(r[3]),
                                DuLieu.chuyenLocalDate(r[4])));
    }

    @Override
    public List<SuDungVoucher> laySuDungVoucher(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "SELECT v.ma_voucher,COALESCE(k.ho_ten,N'Khách lẻ'),COUNT(h.ma_hoa_don),"
                        + "SUM(h.giam_gia) FROM voucher v LEFT JOIN khach_hang k ON"
                        + " k.ma_khach_hang=v.ma_khach_hang JOIN hoa_don h ON"
                        + " h.ma_voucher=v.ma_voucher WHERE h.trang_thai='DA_THANH_TOAN' AND"
                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? GROUP BY"
                        + " v.ma_voucher,COALESCE(k.ho_ten,N'Khách lẻ') ORDER BY v.ma_voucher",
                r ->
                        new SuDungVoucher(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                soNguyen(r[2]),
                                soThuc(r[3])),
                tu,
                denNgay);
    }

    // 6. THỐNG KÊ CÔNG NỢ NHÀ CUNG CẤP

    @Override
    public List<CongNoNhaCungCap> timNhaCungCapCongNo(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "SELECT n.ma_nha_cung_cap,n.ten, COALESCE(SUM(p.tong_tien),0) AS tong_nhap,"
                        + " COALESCE(SUM(p.da_tra),0) AS da_tra,"
                        + " COALESCE(SUM(p.tong_tien-p.da_tra),0) AS du_no FROM nha_cung_cap n"
                        + " JOIN phieu_nhap_kho p ON p.ma_nha_cung_cap=n.ma_nha_cung_cap"
                        + " WHERE p.trang_thai='DA_NHAP' AND p.ngay_nhap>=? AND p.ngay_nhap<?"
                        + " GROUP BY n.ma_nha_cung_cap,n.ten HAVING"
                        + " COALESCE(SUM(p.tong_tien-p.da_tra),0) > 0 ORDER BY du_no"
                        + " DESC,n.ma_nha_cung_cap",
                ThongKeDAOImpl::taoCongNoNcc,
                tu,
                denNgay);
    }

    @Override
    public List<CongNoNhaCungCap> layDuNoHienTai() {
        return truyVanDanhSach(
                "SELECT n.ma_nha_cung_cap,n.ten, COALESCE(SUM(p.tong_tien),0) AS tong_nhap,"
                        + " COALESCE(SUM(p.da_tra),0) AS da_tra,"
                        + " COALESCE(SUM(p.tong_tien-p.da_tra),0) AS du_no FROM nha_cung_cap n"
                        + " JOIN phieu_nhap_kho p ON p.ma_nha_cung_cap=n.ma_nha_cung_cap"
                        + " WHERE p.trang_thai='DA_NHAP' GROUP BY n.ma_nha_cung_cap,n.ten HAVING"
                        + " COALESCE(SUM(p.tong_tien-p.da_tra),0) > 0 ORDER BY du_no"
                        + " DESC,n.ma_nha_cung_cap",
                ThongKeDAOImpl::taoCongNoNcc);
    }

    // 7. THỐNG KÊ NHÂN VIÊN & CA THU NGÂN

    // Truy vấn danh sách các phiên làm việc (ca thu ngân) và số tiền thu/chênh lệch trong kỳ
    @Override
    public List<TongHopCaThuNgan> layTongHopCa(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "SELECT"
                    + " n.ho_ten,s.ten_ca,s.mo_luc,s.dong_luc,s.tien_dau_ca,COALESCE(h.tien,0),COALESCE(c.coc,0),s.tien_thuc_te,s.chenh_lech"
                    + " FROM phien_lam_viec s JOIN nhan_vien n ON n.ma_nhan_vien=s.ma_nhan_vien"
                    + " OUTER APPLY(SELECT SUM(tong_tien) tien FROM hoa_don h WHERE"
                    + " h.ma_phien=s.ma_phien AND h.trang_thai='DA_THANH_TOAN')h OUTER APPLY(SELECT"
                    + " SUM(so_tien) coc FROM lich_su_thu_coc_hotel WHERE ma_phien=s.ma_phien)c"
                    + " WHERE s.mo_luc>=? AND s.mo_luc<? ORDER BY s.mo_luc DESC",
                r ->
                        new TongHopCaThuNgan(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenLocalDateTime(r[2]),
                                DuLieu.chuyenLocalDateTime(r[3]),
                                soThuc(r[4]),
                                soThuc(r[5]),
                                soThuc(r[6]),
                                soThucCoTheNull(r[7]),
                                soThucCoTheNull(r[8])),
                tu,
                denNgay);
    }

    // Truy vấn tổng số ca đi làm và tổng số giờ làm việc của từng nhân viên trong kỳ
    @Override
    public List<LichLamViecNhanVien> timNhanVien(LocalDate tu, LocalDate denNgay) {
        return truyVanDanhSach(
                "SELECT"
                    + " n.ma_nhan_vien,n.ho_ten,n.chuc_vu,COALESCE(COUNT(l.ma_ca),0),ROUND(COALESCE(SUM(CASE"
                    + " WHEN c.gio_ket_thuc >= c.gio_bat_dau THEN"
                    + " DATEDIFF(minute,c.gio_bat_dau,c.gio_ket_thuc) ELSE"
                    + " DATEDIFF(minute,c.gio_bat_dau,c.gio_ket_thuc) + 1440 END),0)/60.0, 2) FROM"
                    + " nhan_vien n LEFT JOIN lich_lam_viec l ON l.ma_nhan_vien=n.ma_nhan_vien AND"
                    + " l.loai='DI_LAM' AND l.trang_thai<>'DA_HUY' AND l.ngay_lam>=? AND"
                    + " l.ngay_lam<? LEFT JOIN ca_lam_viec c ON c.ma_ca=l.ma_ca WHERE"
                    + " n.dang_lam_viec=1 GROUP BY n.ma_nhan_vien,n.ho_ten,n.chuc_vu ORDER BY"
                    + " n.ma_nhan_vien",
                r ->
                        new LichLamViecNhanVien(
                                DuLieu.chuyenThanhChuoi(r[0]),
                                DuLieu.chuyenThanhChuoi(r[1]),
                                DuLieu.chuyenThanhChuoi(r[2]),
                                soNguyen(r[3]),
                                Math.round(soThuc(r[4]) * 100.0) / 100.0),
                tu,
                denNgay);
    }

    @Override
    public ChiTietBaoCao layChiTiet(String baoCao, String goc, KhoangNgay ky, String maNhanVien) {
        Object tu = ky.getTuNgay(), den = ky.getCanTren();
        if (baoCao.contains("doanh thu")) {
            return layChiTietDoanhThu(goc, tu, den, maNhanVien);
        }
        if (baoCao.contains("hàng hóa")) {
            return layChiTietHangHoa(goc, tu, den, maNhanVien);
        }
        if (baoCao.contains("Spa")) {
            return layChiTietSpa(goc, tu, den, maNhanVien);
        }
        if (baoCao.contains("lưu trú")) {
            return layChiTietLuuTru(goc, tu, den, maNhanVien);
        }
        if (baoCao.contains("khách hàng")) {
            return layChiTietKhachHang(goc, tu, den);
        }
        if (baoCao.contains("Công nợ")) {
            return layChiTietCongNo(goc, tu, den);
        }
        if (baoCao.contains("nhân viên")) {
            return layChiTietNhanVien(goc, tu, den);
        }
        throw new IllegalArgumentException("Báo cáo chưa được đăng ký chi tiết: " + baoCao);
    }

    private ChiTietBaoCao layChiTietDoanhThu(String goc, Object tu, Object den, String maNhanVien) {
        if (goc.equals("Theo khung giờ") || goc.equals("Theo thứ trong tuần")) {
            return layChiTietDoanhThuKhungGioHoacThu(
                    goc.equals("Theo khung giờ"), tu, den, maNhanVien);
        }
        String nguon =
                "CASE WHEN EXISTS(SELECT 1 FROM phieu_tiep_nhan_spa t WHERE"
                        + " t.ma_hoa_don=h.ma_hoa_don) THEN 'Spa' WHEN EXISTS(SELECT 1 FROM"
                        + " hop_dong_luu_tru t WHERE t.ma_hoa_don=h.ma_hoa_don) THEN 'Hotel' ELSE"
                        + " N'Hàng hóa' END";
        String noiNhanVienCa =
                " JOIN phien_lam_viec s ON s.ma_phien=h.ma_phien JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien=s.ma_nhan_vien";
        List<Object[]> cacDong =
                goc.equals("Doanh thu hoàn tất")
                        ? truyVan(
                                "SELECT h.ma_hoa_don,h.thanh_toan_luc,"
                                        + nguon
                                        + ",COALESCE(h.hinh_thuc_thanh_toan, N'Chưa rõ'),"
                                        + "COALESCE(n.ho_ten, N'Chưa rõ'),"
                                        + "COALESCE(s.ten_ca, N'Ngoài ca'),"
                                        + "h.tong_tien+COALESCE((SELECT SUM(tien_coc) FROM"
                                        + " hop_dong_luu_tru t WHERE t.ma_hoa_don=h.ma_hoa_don AND"
                                        + " t.da_nhan_coc=1),0)-COALESCE((SELECT SUM(CASE WHEN"
                                        + " p.chenh_lech < 0 THEN -p.chenh_lech ELSE 0 END) FROM"
                                        + " phieu_doi_tra p WHERE p.ma_hoa_don=h.ma_hoa_don AND"
                                        + " p.trang_thai='HOAN_TAT'),0) FROM hoa_don h"
                                        + noiNhanVienCa
                                        + " WHERE h.trang_thai='DA_THANH_TOAN' AND"
                                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND (? IS"
                                        + " NULL OR s.ma_nhan_vien = ?)",
                                tu,
                                den,
                                maNhanVien,
                                maNhanVien)
                        : truyVanChiTietDoanhThuChiaTach(nguon, noiNhanVienCa, maNhanVien, tu, den);
        int nhom =
                switch (goc) {
                    case "Theo thanh toán" -> 3;
                    case "Theo thu ngân" -> 4;
                    case "Theo ca" -> 5;
                    case "Theo nguồn" -> 2;
                    default -> 1;
                };
        return new ChiTietBaoCao(
                new String[] {
                    "Chứng từ", "Thời gian", "Nguồn", "Phương thức", "Thu ngân", "Ca", "Số tiền"
                },
                cacDong,
                nhom);
    }

    private ChiTietBaoCao layChiTietDoanhThuKhungGioHoacThu(
            boolean theoGio, Object tu, Object den, String maNhanVien) {
        String nhanNhom =
                theoGio
                        ? "RIGHT('0' + CAST(DATEPART(hour, h.thanh_toan_luc) AS varchar(2)), 2)"
                                + " + ':00 - ' + RIGHT('0' + CAST(DATEPART(hour,"
                                + " h.thanh_toan_luc) + 1 AS varchar(2)), 2) + ':00'"
                        : "CASE ((DATEPART(weekday, h.thanh_toan_luc) + @@DATEFIRST - 1) % 7)"
                                + " WHEN 0 THEN N'Chủ nhật' WHEN 1 THEN N'Thứ 2' WHEN 2 THEN"
                                + " N'Thứ 3' WHEN 3 THEN N'Thứ 4' WHEN 4 THEN N'Thứ 5' WHEN 5"
                                + " THEN N'Thứ 6' ELSE N'Thứ 7' END";
        return new ChiTietBaoCao(
                new String[] {
                    "Hóa đơn",
                    "Thời gian",
                    theoGio ? "Khung giờ" : "Thứ",
                    "Thu ngân",
                    "Ca",
                    "Phương thức",
                    "Doanh thu"
                },
                truyVan(
                        "SELECT h.ma_hoa_don,h.thanh_toan_luc,"
                                + nhanNhom
                                + ",COALESCE(n.ho_ten, N'Chưa rõ'),"
                                + "COALESCE(s.ten_ca, N'Ngoài ca'),"
                                + "COALESCE(h.hinh_thuc_thanh_toan, N'Chưa rõ'),h.tong_tien"
                                + " FROM hoa_don h JOIN phien_lam_viec s ON"
                                + " s.ma_phien=h.ma_phien JOIN nhan_vien n ON"
                                + " n.ma_nhan_vien=s.ma_nhan_vien WHERE"
                                + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND"
                                + " h.thanh_toan_luc<? AND (? IS NULL OR s.ma_nhan_vien = ?)"
                                + " ORDER BY h.thanh_toan_luc DESC",
                        tu,
                        den,
                        maNhanVien,
                        maNhanVien),
                2);
    }

    private List<Object[]> truyVanChiTietDoanhThuChiaTach(
            String nguon, String noiNhanVienCa, String maNhanVien, Object tu, Object den) {
        List<Object[]> cacDong =
                new ArrayList<>(truyVanDoanhThuChiaTach(nguon, noiNhanVienCa, maNhanVien, tu, den));
        cacDong.addAll(
                truyVan(
                        "SELECT h.ma_hop_dong,l.tao_luc,'Hotel', COALESCE(l.phuong_thuc, N'Chưa"
                            + " rõ'), COALESCE(n.ho_ten, N'Chưa rõ'), COALESCE(s.ten_ca, N'Ngoài"
                            + " ca'),l.so_tien FROM lich_su_thu_coc_hotel l JOIN hop_dong_luu_tru h"
                            + " ON h.ma_hop_dong=l.ma_hop_dong LEFT JOIN phien_lam_viec s ON"
                            + " s.ma_phien=l.ma_phien LEFT JOIN nhan_vien n ON"
                            + " n.ma_nhan_vien=COALESCE(l.ma_nhan_vien_thu, s.ma_nhan_vien) WHERE"
                            + " l.tao_luc>=? AND l.tao_luc<? AND (? IS NULL OR"
                            + " COALESCE(l.ma_nhan_vien_thu, s.ma_nhan_vien) = ?)",
                        tu,
                        den,
                        maNhanVien,
                        maNhanVien));
        cacDong.addAll(
                truyVan(
                        "SELECT p.ma_phieu,p.tao_luc,"
                                + nguon
                                + ",COALESCE(h.hinh_thuc_thanh_toan, N'Chưa rõ'),"
                                + "COALESCE(n.ho_ten, N'Chưa rõ'),"
                                + "COALESCE(s.ten_ca, N'Ngoài ca'),p.chenh_lech"
                                + " FROM phieu_doi_tra p JOIN hoa_don h ON"
                                + " h.ma_hoa_don=p.ma_hoa_don"
                                + noiNhanVienCa
                                + " WHERE p.trang_thai='HOAN_TAT' AND p.chenh_lech < 0 AND"
                                + " p.tao_luc>=? AND p.tao_luc<?"
                                + " AND (? IS NULL OR s.ma_nhan_vien = ?)",
                        tu,
                        den,
                        maNhanVien,
                        maNhanVien));
        return cacDong;
    }

    private List<Object[]> truyVanDoanhThuChiaTach(
            String nguon, String noiNhanVienCa, String maNhanVien, Object tu, Object den) {
        return truyVan(
                "SELECT h.ma_hoa_don,h.thanh_toan_luc,"
                        + nguon
                        + ",COALESCE(h.hinh_thuc_thanh_toan, N'Chưa rõ'),"
                        + "COALESCE(n.ho_ten, N'Chưa rõ'),"
                        + "COALESCE(s.ten_ca, N'Ngoài ca'),h.tong_tien"
                        + " FROM hoa_don h"
                        + noiNhanVienCa
                        + " WHERE h.trang_thai='DA_THANH_TOAN' AND"
                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                        + " AND (? IS NULL OR s.ma_nhan_vien = ?)",
                tu,
                den,
                maNhanVien,
                maNhanVien);
    }

    private ChiTietBaoCao layChiTietHangHoa(String goc, Object tu, Object den, String maNhanVien) {
        if (goc.equals("Sản phẩm bán chạy")) {
            return layChiTietSanPhamBanChay(tu, den, maNhanVien);
        }
        if (goc.equals("Tồn kho hiện tại") || goc.equals("Sắp hết hạn")) {
            String sqlLo =
                    "SELECT l.so_lo,p.ma_san_pham,p.ten,l.han_su_dung,l.so_luong_ton,l.trang_thai"
                            + " FROM lo_hang l JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham";
            return new ChiTietBaoCao(
                    new String[] {"Lô hàng", "Mã SP", "Sản phẩm", "Hạn dùng", "Tồn", "Trạng thái"},
                    goc.equals("Sắp hết hạn")
                            ? truyVan(
                                    sqlLo
                                            + " WHERE l.so_luong_ton>0 AND l.trang_thai='SAN_SANG'"
                                            + " AND l.han_su_dung>=CAST(GETDATE() AS date) AND"
                                            + " l.han_su_dung<=DATEADD(day,COALESCE((SELECT"
                                            + " TRY_CAST(gia_tri AS INT) FROM cai_dat WHERE"
                                            + " ma_cai_dat='SO_NGAY_CANH_BAO_HET_HAN'),30),CAST(GETDATE()"
                                            + " AS date)) ORDER BY l.han_su_dung")
                            : truyVan(
                                    sqlLo
                                            + " WHERE l.so_luong_ton>0 AND l.trang_thai='SAN_SANG'"
                                            + " ORDER BY p.ma_san_pham,l.han_su_dung"),
                    1);
        }
        if (goc.equals("Hàng tồn đọng / chôn vốn") || goc.equals("Hao hụt từ kiểm kê")) {
            return layChiTietTonDongVaHaoHut(goc, tu, den);
        }
        return layChiTietSoCaiKho(tu, den);
    }

    private ChiTietBaoCao layChiTietSanPhamBanChay(Object tu, Object den, String maNhanVien) {
        return new ChiTietBaoCao(
                new String[] {
                    "Hóa đơn",
                    "Mã SP",
                    "Sản phẩm",
                    "Ngày bán",
                    "Số bán sau đổi",
                    "Doanh thu sau đổi",
                    "Nhóm hàng"
                },
                truyVan(
                        "SELECT"
                            + " h.ma_hoa_don,p.ma_san_pham,p.ten,h.thanh_toan_luc,i.so_luong-COALESCE(r.qty,0),CASE"
                            + " WHEN h.tam_tinh>0 THEN"
                            + " i.so_luong*i.don_gia*(h.tong_tien+COALESCE(ex.credit,0))/h.tam_tinh"
                            + " ELSE 0 END-COALESCE(r.refund,0),p.nhom_hang FROM chi_tiet_hoa_don i"
                            + " JOIN san_pham p ON p.ma_san_pham=i.ma_san_pham JOIN hoa_don h ON"
                            + " h.ma_hoa_don=i.ma_hoa_don JOIN phien_lam_viec s ON"
                            + " s.ma_phien=h.ma_phien OUTER APPLY(SELECT SUM(c.so_luong)"
                            + " qty,SUM(c.tien_hoan) refund FROM chi_tiet_doi_tra c JOIN"
                            + " phieu_doi_tra x ON x.ma_phieu=c.ma_phieu WHERE"
                            + " c.ma_hoa_don=i.ma_hoa_don AND c.ma_san_pham=i.ma_san_pham AND"
                            + " x.trang_thai='HOAN_TAT')r OUTER APPLY(SELECT"
                            + " SUM(dx.gia_tri_duoc_doi) credit FROM phieu_doi_tra dx WHERE"
                            + " dx.ma_hoa_don_thay_the=h.ma_hoa_don AND dx.loai_phieu='DOI_HANG'"
                            + " AND dx.trang_thai='HOAN_TAT')ex WHERE h.trang_thai='DA_THANH_TOAN'"
                            + " AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND (? IS NULL OR"
                            + " s.ma_nhan_vien = ?)",
                        tu,
                        den,
                        maNhanVien,
                        maNhanVien),
                1);
    }

    private ChiTietBaoCao layChiTietTonDongVaHaoHut(String goc, Object tu, Object den) {
        if (goc.equals("Hàng tồn đọng / chôn vốn")) {
            int soNgay = docSoNgayTonDong();
            return new ChiTietBaoCao(
                    new String[] {
                        "Số lô",
                        "Mã SP",
                        "Sản phẩm",
                        "Ngày nhập",
                        "Hạn dùng",
                        "Tồn",
                        "Giá nhập gần nhất",
                        "Vốn chôn"
                    },
                    truyVan(
                            "SELECT l.so_lo,p.ma_san_pham,p.ten,l.ngay_nhap,l.han_su_dung,"
                                    + "l.so_luong_ton,COALESCE(p.gia_nhap_gan_nhat,0),"
                                    + "l.so_luong_ton*COALESCE(p.gia_nhap_gan_nhat,0) FROM lo_hang"
                                    + " l JOIN san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE"
                                    + " l.so_luong_ton>0 AND NOT EXISTS (SELECT 1 FROM"
                                    + " chi_tiet_hoa_don ct JOIN hoa_don hd ON"
                                    + " hd.ma_hoa_don=ct.ma_hoa_don WHERE"
                                    + " ct.ma_san_pham=p.ma_san_pham AND"
                                    + " hd.trang_thai='DA_THANH_TOAN' AND hd.thanh_toan_luc>="
                                    + " DATEADD(day,-?,GETDATE())) ORDER BY l.ma_san_pham",
                            soNgay),
                    1);
        }
        return new ChiTietBaoCao(
                new String[] {
                    "Biên bản",
                    "Mã SP",
                    "Sản phẩm",
                    "Số lô",
                    "Tồn hệ thống",
                    "Tồn thực tế",
                    "Chênh lệch SL",
                    "Giá trị lệch",
                    "Lý do",
                    "Ngày tạo"
                },
                truyVan(
                        "SELECT bb.ma_bien_ban,p.ma_san_pham,p.ten,l.so_lo,ct.ton_he_thong,"
                                + "ct.ton_thuc_te,(ct.ton_thuc_te - ct.ton_he_thong),"
                                + "(ct.ton_thuc_te - ct.ton_he_thong) *"
                                + " COALESCE(p.gia_nhap_gan_nhat,0),COALESCE(ct.nguyen_nhan,"
                                + " N'Chưa rõ'),bb.tao_luc FROM chi_tiet_kiem_ke ct JOIN"
                                + " bien_ban_kiem_ke bb ON bb.ma_bien_ban=ct.ma_bien_ban JOIN"
                                + " lo_hang l ON l.ma_lo=ct.ma_lo JOIN san_pham p ON"
                                + " p.ma_san_pham=l.ma_san_pham WHERE"
                                + " bb.trang_thai='DA_DUYET' AND ct.ton_thuc_te <>"
                                + " ct.ton_he_thong AND bb.tao_luc>=? AND bb.tao_luc<? ORDER"
                                + " BY bb.tao_luc DESC",
                        tu,
                        den),
                1);
    }

    private ChiTietBaoCao layChiTietSoCaiKho(Object tu, Object den) {
        String soCaiKho =
                "SELECT n.ma_phieu_nhap chung_tu,p.ma_san_pham,p.ten,N'Nhập kho'"
                        + " loai,n.ngay_nhap ngay,c.so_luong FROM chi_tiet_phieu_nhap c JOIN"
                        + " phieu_nhap_kho n ON n.ma_phieu_nhap=c.ma_phieu_nhap JOIN san_pham p ON"
                        + " p.ma_san_pham=c.ma_san_pham WHERE n.trang_thai='DA_NHAP' UNION ALL"
                        + " SELECT h.ma_hoa_don,p.ma_san_pham,p.ten,N'Bán hàng',"
                        + "h.thanh_toan_luc,-c.so_luong FROM chi_tiet_hoa_don c JOIN san_pham p ON"
                        + " p.ma_san_pham=c.ma_san_pham JOIN hoa_don h ON h.ma_hoa_don=c.ma_hoa_don"
                        + " WHERE h.trang_thai='DA_THANH_TOAN' UNION ALL SELECT"
                        + " n.ma_phieu_xuat,p.ma_san_pham,p.ten,N'Xuất hủy',n.tao_luc,-c.so_luong"
                        + " FROM chi_tiet_phieu_xuat c JOIN phieu_xuat_kho n ON"
                        + " n.ma_phieu_xuat=c.ma_phieu_xuat JOIN lo_hang l ON l.ma_lo=c.ma_lo JOIN"
                        + " san_pham p ON p.ma_san_pham=l.ma_san_pham WHERE"
                        + " n.trang_thai='DA_XUAT' UNION ALL SELECT n.ma_phieu,p.ma_san_pham,"
                        + "p.ten,N'Nhận hàng đổi',n.tao_luc,c.so_luong FROM chi_tiet_doi_tra c JOIN"
                        + " phieu_doi_tra n ON n.ma_phieu=c.ma_phieu JOIN san_pham p ON"
                        + " p.ma_san_pham=c.ma_san_pham WHERE n.trang_thai='HOAN_TAT'";
        return new ChiTietBaoCao(
                new String[] {
                    "Chứng từ", "Mã SP", "Sản phẩm", "Loại giao dịch", "Ngày", "Số lượng"
                },
                truyVan(
                        "WITH dong AS("
                                + soCaiKho
                                + ") SELECT * FROM dong WHERE ngay>=? AND ngay<?",
                        tu,
                        den),
                1);
    }

    private ChiTietBaoCao layChiTietSpa(String goc, Object tu, Object den, String maNhanVien) {
        if (goc.equals("Dịch vụ đã thanh toán")) {
            return new ChiTietBaoCao(
                    new String[] {
                        "Phiếu Spa", "Hóa đơn", "Mã dịch vụ", "Dịch vụ", "Lượt", "Giá trị dịch vụ"
                    },
                    truyVan(
                            "SELECT t.ma_phieu,h.ma_hoa_don,COALESCE(g.ma_goi,t.ma_phieu),"
                                    + "COALESCE(NULLIF(t.mo_ta_dich_vu,N''),g.ten_goi,"
                                    + "N'Dịch vụ lẻ'),t.so_luong,CASE WHEN h.tam_tinh>0 THEN"
                                    + " t.tong_tien*h.tong_tien/h.tam_tinh ELSE t.tong_tien END"
                                    + " FROM phieu_tiep_nhan_spa t LEFT JOIN goi_dich_vu_spa g ON"
                                    + " g.ma_goi=t.ma_goi JOIN hoa_don h ON"
                                    + " h.ma_hoa_don=t.ma_hoa_don JOIN phien_lam_viec s ON"
                                    + " s.ma_phien=h.ma_phien WHERE t.trang_thai='DA_THANH_TOAN'"
                                    + " AND h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=?"
                                    + " AND h.thanh_toan_luc<? AND (? IS NULL OR"
                                    + " COALESCE(t.ma_nhan_vien,s.ma_nhan_vien) = ?)",
                            tu,
                            den,
                            maNhanVien,
                            maNhanVien),
                    2);
        }
        boolean lichHen = goc.equals("Trạng thái lịch hẹn");
        String cotThoiGian = lichHen ? "bat_dau" : "tao_luc";
        return new ChiTietBaoCao(
                new String[] {"Chứng từ", "Thú cưng", "Thời gian", "Trạng thái"},
                truyVan(
                        "SELECT t."
                                + (lichHen ? "ma_lich_hen" : "ma_phieu")
                                + ",p.ten,t."
                                + cotThoiGian
                                + ",t.trang_thai FROM "
                                + (lichHen ? "lich_hen" : "phieu_tiep_nhan_spa")
                                + " t JOIN thu_cung p ON p.ma_thu_cung=t.ma_thu_cung WHERE t."
                                + cotThoiGian
                                + ">=? AND t."
                                + cotThoiGian
                                + "<? AND (? IS NULL OR t.ma_nhan_vien = ?)",
                        tu,
                        den,
                        maNhanVien,
                        maNhanVien),
                3);
    }

    private ChiTietBaoCao layChiTietLuuTru(String goc, Object tu, Object den, String maNhanVien) {
        if (goc.equals("Trạng thái phòng hiện tại")) {
            return new ChiTietBaoCao(
                    new String[] {"Mã phòng", "Loại phòng", "Trạng thái"},
                    truyVan("SELECT ma_phong,loai_phong,trang_thai FROM phong_hotel"),
                    2);
        }
        if (goc.equals("Cọc nhận trong kỳ")) {
            return new ChiTietBaoCao(
                    new String[] {
                        "Hợp đồng", "Ngày nhận", "Phương thức", "Tiền cọc", "Người nhận", "Ca"
                    },
                    truyVan(
                            "SELECT"
                                + " h.ma_hop_dong,l.tao_luc,l.phuong_thuc,l.so_tien,n.ho_ten,s.ten_ca"
                                + " FROM lich_su_thu_coc_hotel l JOIN hop_dong_luu_tru h ON"
                                + " h.ma_hop_dong=l.ma_hop_dong LEFT JOIN phien_lam_viec s ON"
                                + " s.ma_phien=l.ma_phien LEFT JOIN nhan_vien n ON"
                                + " n.ma_nhan_vien=COALESCE(l.ma_nhan_vien_thu, s.ma_nhan_vien)"
                                + " WHERE l.tao_luc>=? AND l.tao_luc<? AND (? IS NULL OR"
                                + " COALESCE(l.ma_nhan_vien_thu, s.ma_nhan_vien) = ?)",
                            tu,
                            den,
                            maNhanVien,
                            maNhanVien),
                    1);
        }
        if (goc.equals("Lưu trú đã quyết toán")) {
            return new ChiTietBaoCao(
                    new String[] {
                        "Hợp đồng", "Loại phòng", "Hóa đơn", "Ngày quyết toán", "Doanh thu hoàn tất"
                    },
                    truyVan(
                            "SELECT t.ma_hop_dong,p.loai_phong,h.ma_hoa_don,h.thanh_toan_luc,"
                                    + "h.tong_tien+CASE WHEN t.da_nhan_coc=1 THEN t.tien_coc ELSE 0"
                                    + " END FROM hop_dong_luu_tru t JOIN phong_hotel p ON"
                                    + " p.ma_phong=t.ma_phong JOIN hoa_don h ON"
                                    + " h.ma_hoa_don=t.ma_hoa_don JOIN phien_lam_viec s ON"
                                    + " s.ma_phien=h.ma_phien WHERE t.trang_thai='DA_TRA_PHONG' AND"
                                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND"
                                    + " h.thanh_toan_luc<? AND (? IS NULL OR s.ma_nhan_vien = ?)",
                            tu,
                            den,
                            maNhanVien,
                            maNhanVien),
                    1);
        }
        return new ChiTietBaoCao(
                new String[] {"Hợp đồng", "Phòng", "Ngày vào", "Trạng thái", "Cọc đã nhận"},
                truyVan(
                        "SELECT t.ma_hop_dong,p.ma_phong,t.ngay_vao,t.trang_thai,CASE WHEN"
                                + " t.da_nhan_coc=1 THEN t.tien_coc ELSE 0 END FROM"
                                + " hop_dong_luu_tru t JOIN phong_hotel p ON p.ma_phong=t.ma_phong"
                                + " WHERE t.ngay_vao>=? AND t.ngay_vao<? AND (? IS NULL OR"
                                + " t.nhan_coc_boi = ? OR t.ma_phien_nhan_coc IS NULL OR"
                                + " EXISTS(SELECT 1 FROM phien_lam_viec s WHERE"
                                + " s.ma_phien=t.ma_phien_nhan_coc AND s.ma_nhan_vien = ?))",
                        tu,
                        den,
                        maNhanVien,
                        maNhanVien,
                        maNhanVien),
                3);
    }

    private ChiTietBaoCao layChiTietKhachHang(String goc, Object tu, Object den) {
        if (goc.equals("Chi tiêu theo kỳ")) {
            return new ChiTietBaoCao(
                    new String[] {"Hóa đơn", "Mã KH", "Khách hàng", "Thanh toán lúc", "Chi tiêu"},
                    truyVan(
                            "SELECT h.ma_hoa_don,k.ma_khach_hang,k.ho_ten,h.thanh_toan_luc,"
                                    + "h.tong_tien FROM hoa_don h JOIN khach_hang k ON"
                                    + " k.ma_khach_hang=h.ma_khach_hang WHERE"
                                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND"
                                    + " h.thanh_toan_luc<?",
                            tu,
                            den),
                    1);
        }
        if (goc.equals("Khách mới / quay lại")) {
            return new ChiTietBaoCao(
                    new String[] {
                        "Hóa đơn", "Nhóm khách", "Mã KH", "Khách hàng", "Ngày", "Tiền thanh toán"
                    },
                    truyVan(
                            "SELECT h.ma_hoa_don,CASE WHEN EXISTS(SELECT 1 FROM hoa_don cu"
                                    + " WHERE cu.ma_khach_hang=k.ma_khach_hang AND"
                                    + " cu.trang_thai='DA_THANH_TOAN' AND cu.thanh_toan_luc<?) THEN"
                                    + " N'Khách quay lại' ELSE N'Khách mua lần đầu' END,"
                                    + "k.ma_khach_hang,k.ho_ten,h.thanh_toan_luc,h.tong_tien FROM"
                                    + " khach_hang k JOIN hoa_don h ON"
                                    + " h.ma_khach_hang=k.ma_khach_hang WHERE"
                                    + " h.trang_thai='DA_THANH_TOAN' AND h.thanh_toan_luc>=? AND"
                                    + " h.thanh_toan_luc<?",
                            tu,
                            tu,
                            den),
                    1);
        }
        String sqlVoucher =
                "SELECT h.ma_hoa_don,v.ma_voucher,COALESCE(k.ho_ten,N'Khách lẻ'),"
                        + "h.thanh_toan_luc,h.giam_gia FROM voucher v JOIN hoa_don h ON"
                        + " h.ma_voucher=v.ma_voucher LEFT JOIN khach_hang k ON"
                        + " k.ma_khach_hang=v.ma_khach_hang WHERE h.trang_thai='DA_THANH_TOAN'";
        boolean trongKy = goc.equals("Lượt dùng voucher trong kỳ");
        return new ChiTietBaoCao(
                new String[] {"Hóa đơn", "Mã voucher", "Khách hàng", "Ngày", "Giảm giá"},
                trongKy
                        ? truyVan(
                                sqlVoucher + " AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?",
                                tu,
                                den)
                        : truyVan(sqlVoucher),
                1);
    }

    private ChiTietBaoCao layChiTietCongNo(String goc, Object tu, Object den) {
        boolean laDuNo = goc.equals("Dư nợ hiện tại");
        String sql =
                "SELECT p.ma_phieu_nhap,n.ma_nha_cung_cap,n.ten,p.ngay_nhap,p.tong_tien,"
                        + "p.da_tra,p.tong_tien-p.da_tra FROM phieu_nhap_kho p JOIN nha_cung_cap n"
                        + " ON n.ma_nha_cung_cap=p.ma_nha_cung_cap WHERE p.trang_thai='DA_NHAP'"
                        + (laDuNo ? "" : " AND p.ngay_nhap>=? AND p.ngay_nhap<?");
        Object[] thamSo = laDuNo ? new Object[0] : new Object[] {tu, den};
        return new ChiTietBaoCao(
                new String[] {
                    "Phiếu nhập",
                    "Mã NCC",
                    "Nhà cung cấp",
                    "Ngày nhập",
                    "Tổng nhập",
                    "Đã trả hiện tại",
                    "Dư nợ hiện tại"
                },
                truyVan(sql, thamSo),
                1);
    }

    private ChiTietBaoCao layChiTietNhanVien(String goc, Object tu, Object den) {
        if (goc.equals("Lịch làm việc")) {
            return new ChiTietBaoCao(
                    new String[] {
                        "Ngày làm", "Mã NV", "Nhân viên", "Ca", "Bắt đầu", "Kết thúc", "Trạng thái"
                    },
                    truyVan(
                            "SELECT l.ngay_lam,n.ma_nhan_vien,n.ho_ten,c.ten_ca,c.gio_bat_dau,"
                                    + "c.gio_ket_thuc,l.trang_thai FROM lich_lam_viec l JOIN"
                                    + " nhan_vien n ON n.ma_nhan_vien=l.ma_nhan_vien JOIN"
                                    + " ca_lam_viec c ON c.ma_ca=l.ma_ca WHERE"
                                    + " l.loai='DI_LAM' AND l.trang_thai<>'DA_HUY' AND"
                                    + " n.dang_lam_viec=1 AND l.ngay_lam>=? AND l.ngay_lam<?"
                                    + " ORDER BY l.ngay_lam DESC,n.ma_nhan_vien",
                            tu,
                            den),
                    1);
        }
        return new ChiTietBaoCao(
                new String[] {
                    "Chứng từ", "Loại thu", "Thời gian", "Ca", "Nhân viên", "Phương thức", "Số tiền"
                },
                truyVan(
                        "SELECT h.ma_hoa_don,N'Hóa đơn',h.thanh_toan_luc,s.ten_ca,n.ho_ten,"
                                + "COALESCE(h.hinh_thuc_thanh_toan,N'Chưa rõ'),h.tong_tien FROM"
                                + " phien_lam_viec s JOIN nhan_vien n ON"
                                + " n.ma_nhan_vien=s.ma_nhan_vien JOIN hoa_don h ON"
                                + " h.ma_phien=s.ma_phien WHERE h.trang_thai='DA_THANH_TOAN' AND"
                                + " s.mo_luc>=? AND s.mo_luc<? UNION ALL SELECT k.ma_hop_dong,"
                                + "N'Cọc lưu trú',k.tao_luc,s.ten_ca,n.ho_ten,"
                                + "COALESCE(k.phuong_thuc,N'Chưa rõ'),k.so_tien FROM"
                                + " phien_lam_viec s JOIN nhan_vien n ON"
                                + " n.ma_nhan_vien=s.ma_nhan_vien JOIN lich_su_thu_coc_hotel k ON"
                                + " k.ma_phien=s.ma_phien WHERE s.mo_luc>=? AND s.mo_luc<?"
                                + " ORDER BY 3 DESC",
                        tu,
                        den,
                        tu,
                        den),
                4);
    }
}
