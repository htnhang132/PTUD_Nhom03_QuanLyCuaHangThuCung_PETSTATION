package com.petstation.dao.impl;

import com.petstation.dao.ThaoTacSQL;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC thống kê DAO. */
public final class JdbcThongKeDAO {
    /** Khởi tạo JdbcThongKeDAO với các giá trị được cung cấp. */
    private JdbcThongKeDAO() {}

    /**
     * Tìm kho trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm kho
     */
    public static List<Object[]> timKho(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " p.ma_san_pham,p.ten,COALESCE(SUM(l.so_luong_ton),0),p.ton_toi_thieu,p.gia_nhap_gan_nhat"
                    + " FROM san_pham p LEFT JOIN lo_hang l ON l.san_pham_id=p.id GROUP BY"
                    + " p.ma_san_pham,p.ten,p.ton_toi_thieu,p.gia_nhap_gan_nhat ORDER BY"
                    + " p.ma_san_pham",
                cacThamSo);
    }

    /**
     * Tìm hạn sử dụng trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm hạn sử dụng
     */
    public static List<Object[]> timHanSuDung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ma_san_pham,p.ten,l.so_lo,l.han_su_dung,l.so_luong_ton FROM lo_hang l"
                        + " JOIN san_pham p ON p.id=l.san_pham_id WHERE l.so_luong_ton>0 AND"
                        + " l.han_su_dung<=DATEADD(day,?,CAST(GETDATE() AS date)) ORDER BY"
                        + " l.han_su_dung",
                cacThamSo);
    }

    /**
     * Tìm trạng thái phòng trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm trạng thái phòng
     */
    public static List<Object[]> timTrangThaiPhong(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT trang_thai,COUNT(*) FROM phong_hotel GROUP BY trang_thai ORDER BY"
                        + " trang_thai",
                cacThamSo);
    }

    /**
     * Tìm các khách hàng trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm các khách hàng
     */
    public static List<Object[]> timCacKhachHang(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " k.ma_khach_hang,k.ho_ten,k.hang_thanh_vien,k.diem_tich_luy,COALESCE(SUM(h.tong_tien),0)"
                    + " FROM khach_hang k LEFT JOIN don_hang d ON d.khach_hang_id=k.id LEFT JOIN"
                    + " hoa_don h ON h.don_hang_id=d.id AND h.trang_thai='PAID' AND"
                    + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? GROUP BY"
                    + " k.ma_khach_hang,k.ho_ten,k.hang_thanh_vien,k.diem_tich_luy ORDER BY"
                    + " COALESCE(SUM(h.tong_tien),0) DESC,k.ma_khach_hang",
                cacThamSo);
    }

    /**
     * Tìm các voucher trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm các voucher
     */
    public static List<Object[]> timCacVoucher(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_voucher,menh_gia,da_dung,so_luong,hieu_luc_den FROM voucher ORDER BY"
                        + " ma_voucher",
                cacThamSo);
    }

    /**
     * Tìm nhà cung cấp công nợ trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm nhà cung cấp công nợ
     */
    public static List<Object[]> timNhaCungCapCongNo(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ma_ncc,n.ten,COALESCE(SUM(p.tong_tien),0),COALESCE(SUM(p.da_tra),0),COALESCE(SUM(p.tong_tien-p.da_tra),0)"
                    + " FROM nha_cung_cap n LEFT JOIN phieu_nhap_kho p ON p.nha_cung_cap_id=n.id"
                    + " AND p.trang_thai='POSTED' AND p.ngay_nhap>=? AND p.ngay_nhap<? GROUP BY"
                    + " n.ma_ncc,n.ten ORDER BY n.ma_ncc",
                cacThamSo);
    }

    /**
     * Tìm nhân viên trong JdbcThongKeDAO.
     *
     * @param cacThamSo các tham số
     * @return kết quả tìm nhân viên
     */
    public static List<Object[]> timNhanVien(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ma_nhan_vien,n.ho_ten,n.chuc_vu,COALESCE(COUNT(l.id),0),COALESCE(SUM(DATEDIFF(minute,c.gio_bat_dau,c.gio_ket_thuc)),0)/60.0"
                    + " FROM nhan_vien n LEFT JOIN lich_lam_viec l ON l.nhan_vien_id=n.id AND"
                    + " l.loai='WORK' AND l.trang_thai<>'CANCELLED' AND l.ngay_lam>=? AND"
                    + " l.ngay_lam<? LEFT JOIN ca_lam_viec c ON c.id=l.ca_lam_viec_id GROUP BY"
                    + " n.ma_nhan_vien,n.ho_ten,n.chuc_vu ORDER BY n.ma_nhan_vien",
                cacThamSo);
    }

    /**
     * Lấy doanh thu theo ngày trong JdbcThongKeDAO.
     *
     * @param tu từ
     * @param denNgay đến ngày
     * @param taiKhoan tài khoản
     * @param nhanVien nhân viên
     * @return kết quả lấy doanh thu theo ngày
     */
    /** Tổng thu thực tế: hóa đơn, cọc Hotel mới và hoàn tiền lịch sử. */
    public static List<Object[]> layDoanhThuTheoNgay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        String loc = taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan;
        return ThaoTacSQL.truyVan(
                "WITH ledger AS (SELECT CAST(h.thanh_toan_luc AS date) ngay,h.tong_tien so_tien"
                    + " FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id JOIN phien_lam_viec s"
                    + " ON s.id=d.phien_lam_viec_id WHERE h.trang_thai='PAID'"
                        + loc
                        + " UNION ALL SELECT CAST(p.tao_luc AS date),p.chenh_lech FROM"
                        + " phieu_doi_tra p JOIN hoa_don h ON h.id=p.hoa_don_id JOIN don_hang d ON"
                        + " d.id=h.don_hang_id JOIN phien_lam_viec s ON s.id=d.phien_lam_viec_id"
                        + " WHERE p.trang_thai='COMPLETED' AND p.loai_phieu='LEGACY_RETURN'"
                        + loc
                        + " UNION ALL SELECT CAST(h.nhan_coc_luc AS date),h.tien_coc FROM"
                        + " hop_dong_luu_tru h JOIN phien_lam_viec s ON s.id=h.phien_nhan_coc_id"
                        + " WHERE h.da_nhan_coc=1"
                        + loc
                        + ") SELECT ngay,SUM(so_tien) FROM ledger WHERE ngay>=? AND ngay<? GROUP BY"
                        + " ngay ORDER BY ngay",
                tu,
                denNgay);
    }

    /**
     * Lấy doanh thu trong JdbcThongKeDAO.
     *
     * @param tu từ
     * @param denNgay đến ngày
     * @param nhomTongHop nhóm tổng hợp
     * @param taiKhoan tài khoản
     * @param nhanVien nhân viên
     * @return kết quả lấy doanh thu
     */
    public static List<Object[]> layDoanhThu(
            java.time.LocalDate tu,
            java.time.LocalDate denNgay,
            String nhomTongHop,
            Long taiKhoan,
            Long nhanVien) {
        String nhom =
                switch (nhomTongHop) {
                    case "method" -> "h.hinh_thuc_thanh_toan";
                    case "staff" -> "n.ho_ten";
                    case "shift" -> "s.ten_ca";
                    default ->
                            "CASE WHEN EXISTS(SELECT 1 FROM phieu_tiep_nhan_spa t WHERE"
                                + " t.hoa_don_id=h.id) THEN 'Spa' WHEN EXISTS(SELECT 1 FROM"
                                + " hop_dong_luu_tru t WHERE t.hoa_don_id=h.id) THEN 'Hotel' ELSE"
                                + " N'Hàng hóa' END";
                };
        String nhomCoc =
                switch (nhomTongHop) {
                    case "method" -> "'CASH'";
                    case "staff" -> "n.ho_ten";
                    case "shift" -> "s.ten_ca";
                    default -> "'Hotel'";
                };
        String loc = taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan;
        return ThaoTacSQL.truyVan(
                "WITH ledger AS (SELECT "
                        + nhom
                        + " nhan,h.tong_tien so_tien,h.id hoa_don,CAST(h.thanh_toan_luc AS date)"
                        + " ngay FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id JOIN"
                        + " phien_lam_viec s ON s.id=d.phien_lam_viec_id JOIN tai_khoan tk ON"
                        + " tk.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=tk.nhan_vien_id WHERE"
                        + " h.trang_thai='PAID'"
                        + loc
                        + " UNION ALL SELECT "
                        + nhom
                        + ",p.chenh_lech,NULL,CAST(p.tao_luc AS date) FROM phieu_doi_tra p JOIN"
                        + " hoa_don h ON h.id=p.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id"
                        + " JOIN phien_lam_viec s ON s.id=d.phien_lam_viec_id JOIN tai_khoan tk ON"
                        + " tk.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=tk.nhan_vien_id WHERE"
                        + " p.trang_thai='COMPLETED' AND p.loai_phieu='LEGACY_RETURN'"
                        + loc
                        + " UNION ALL SELECT "
                        + nhomCoc
                        + ",h.tien_coc,NULL,CAST(h.nhan_coc_luc AS date) FROM hop_dong_luu_tru h"
                        + " JOIN phien_lam_viec s ON s.id=h.phien_nhan_coc_id JOIN tai_khoan tk ON"
                        + " tk.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=tk.nhan_vien_id WHERE"
                        + " h.da_nhan_coc=1"
                        + loc
                        + ") SELECT nhan,SUM(so_tien),COUNT(hoa_don) FROM ledger WHERE ngay>=? AND"
                        + " ngay<? GROUP BY nhan ORDER BY SUM(so_tien) DESC",
                tu,
                denNgay);
    }

    /**
     * Lấy sản phẩm bán chạy trong JdbcThongKeDAO.
     *
     * @param tu từ
     * @param denNgay đến ngày
     * @param taiKhoan tài khoản
     * @param nhanVien nhân viên
     * @return kết quả lấy sản phẩm bán chạy
     */
    public static List<Object[]> laySanPhamBanChay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT TOP(10) p.ma_san_pham,p.ten,SUM(i.so_luong)-SUM(COALESCE(r.qty,0))"
                    + " qty,SUM(CASE WHEN h.tam_tinh>0 THEN"
                    + " i.so_luong*i.don_gia*(h.tong_tien+COALESCE(ex.credit,0))/h.tam_tinh ELSE 0"
                    + " END)-SUM(COALESCE(r.refund,0)) amount FROM chi_tiet_don_hang i JOIN"
                    + " san_pham p ON p.id=i.san_pham_id JOIN hoa_don h ON"
                    + " h.don_hang_id=i.don_hang_id JOIN don_hang d ON d.id=h.don_hang_id JOIN"
                    + " phien_lam_viec s ON s.id=d.phien_lam_viec_id OUTER APPLY(SELECT"
                    + " SUM(c.so_luong) qty,SUM(c.tien_hoan) refund FROM chi_tiet_doi_tra c JOIN"
                    + " phieu_doi_tra x ON x.id=c.phieu_doi_tra_id WHERE"
                    + " c.chi_tiet_don_hang_id=i.id AND x.trang_thai='COMPLETED') r OUTER"
                    + " APPLY(SELECT SUM(dx.gia_tri_duoc_doi) credit FROM phieu_doi_tra dx WHERE"
                    + " dx.hoa_don_thay_the_id=h.id AND dx.loai_phieu='EXCHANGE' AND"
                    + " dx.trang_thai='COMPLETED') ex WHERE h.trang_thai='PAID' AND"
                    + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                        + (taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan)
                        + " GROUP BY p.ma_san_pham,p.ten ORDER BY qty DESC,p.ma_san_pham",
                tu,
                denNgay);
    }

    /**
     * Lấy dữ liệu Spa trong JdbcThongKeDAO.
     *
     * @param tu từ
     * @param denNgay đến ngày
     * @param taiKhoan tài khoản
     * @param nhanVien nhân viên
     * @return kết quả lấy dữ liệu Spa
     */
    public static List<Object[]> layDuLieuSpa(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT g.ma_goi,g.ten_goi,SUM(c.so_luong),SUM(CASE WHEN h.tam_tinh>0 THEN"
                    + " c.thanh_tien*h.tong_tien/h.tam_tinh ELSE 0 END) FROM chi_tiet_phieu_spa c"
                    + " JOIN goi_dich_vu_spa g ON g.id=c.goi_id JOIN phieu_tiep_nhan_spa t ON"
                    + " t.id=c.phieu_spa_id JOIN hoa_don h ON h.id=t.hoa_don_id JOIN don_hang d ON"
                    + " d.id=h.don_hang_id JOIN phien_lam_viec s ON s.id=d.phien_lam_viec_id WHERE"
                    + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND t.trang_thai='PAID'"
                        + (taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan)
                        + " GROUP BY g.ma_goi,g.ten_goi ORDER BY SUM(c.so_luong) DESC",
                tu,
                denNgay);
    }

    /**
     * Lấy lịch hẹn trong JdbcThongKeDAO.
     *
     * @param tu từ
     * @param denNgay đến ngày
     * @param taiKhoan tài khoản
     * @param nhanVien nhân viên
     * @return kết quả lấy lịch hẹn
     */
    public static List<Object[]> layLichHen(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT trang_thai,COUNT(*) FROM lich_hen WHERE bat_dau>=? AND bat_dau<?"
                        + (nhanVien == null ? "" : " AND nhan_vien_id=" + nhanVien)
                        + " GROUP BY"
                        + " trang_thai ORDER BY trang_thai",
                tu,
                denNgay);
    }

    /**
     * Lấy dữ liệu Hotel trong JdbcThongKeDAO.
     *
     * @param tu từ
     * @param denNgay đến ngày
     * @param taiKhoan tài khoản
     * @param nhanVien nhân viên
     * @return kết quả lấy dữ liệu Hotel
     */
    public static List<Object[]> layDuLieuHotel(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "SELECT p.loai_phong,COUNT(*),SUM(CASE WHEN"
                    + " DATEDIFF(day,h.ngay_vao,h.ngay_ra_thuc_te)<1 THEN 1 ELSE"
                    + " DATEDIFF(day,h.ngay_vao,h.ngay_ra_thuc_te) END),SUM(d.tong_tien+h.tien_coc)"
                    + " FROM hop_dong_luu_tru h JOIN phong_hotel p ON p.id=h.phong_id JOIN hoa_don"
                    + " d ON d.id=h.hoa_don_id JOIN don_hang o ON o.id=d.don_hang_id JOIN"
                    + " phien_lam_viec s ON s.id=o.phien_lam_viec_id WHERE d.trang_thai='PAID' AND"
                    + " d.thanh_toan_luc>=? AND d.thanh_toan_luc<?"
                        + (taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan)
                        + " GROUP BY p.loai_phong ORDER BY p.loai_phong",
                tu,
                denNgay);
    }

    public static List<Object[]> layDoanhThuHoanTat(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT CAST(h.thanh_toan_luc AS date),SUM(h.tong_tien+COALESCE(c.coc,0)),COUNT(*)"
                    + " FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id OUTER APPLY(SELECT"
                    + " SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE hoa_don_id=h.id AND"
                    + " da_nhan_coc=1)c JOIN phien_lam_viec s ON s.id=d.phien_lam_viec_id WHERE"
                    + " h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<? AND (?"
                    + " IS NULL OR s.tai_khoan_id=?) GROUP BY CAST(h.thanh_toan_luc AS date) ORDER"
                    + " BY 1",
                thamSo);
    }

    public static List<Object[]> layBienDongKho(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "WITH dong AS (SELECT p.ma_san_pham,p.ten,N'Nhập kho' loai,c.so_luong FROM"
                    + " chi_tiet_phieu_nhap c JOIN phieu_nhap_kho n ON n.id=c.phieu_nhap_id JOIN"
                    + " san_pham p ON p.id=c.san_pham_id WHERE n.trang_thai='POSTED' AND"
                    + " n.ngay_nhap>=? AND n.ngay_nhap<? UNION ALL SELECT p.ma_san_pham,p.ten,N'Bán"
                    + " hàng',-b.so_luong FROM phan_bo_lo_hang b JOIN lo_hang l ON"
                    + " l.id=b.lo_hang_id JOIN san_pham p ON p.id=l.san_pham_id JOIN"
                    + " chi_tiet_don_hang c ON c.id=b.chi_tiet_don_hang_id JOIN hoa_don h ON"
                    + " h.don_hang_id=c.don_hang_id WHERE h.trang_thai='PAID' AND"
                    + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<? UNION ALL SELECT"
                    + " p.ma_san_pham,p.ten,N'Nhận hàng đổi',c.so_luong FROM chi_tiet_doi_tra c"
                    + " JOIN phieu_doi_tra n ON n.id=c.phieu_doi_tra_id JOIN lo_hang l ON"
                    + " l.id=c.lo_hang_id JOIN san_pham p ON p.id=l.san_pham_id WHERE"
                    + " n.trang_thai='COMPLETED' AND n.tao_luc>=? AND n.tao_luc<? UNION ALL SELECT"
                    + " p.ma_san_pham,p.ten,N'Xuất hủy',-c.so_luong FROM chi_tiet_phieu_xuat c JOIN"
                    + " phieu_xuat_kho n ON n.id=c.phieu_xuat_id JOIN lo_hang l ON"
                    + " l.id=c.lo_hang_id JOIN san_pham p ON p.id=l.san_pham_id WHERE"
                    + " n.trang_thai='POSTED' AND n.tao_luc>=? AND n.tao_luc<?) SELECT"
                    + " ma_san_pham,ten,loai,SUM(so_luong) FROM dong GROUP BY ma_san_pham,ten,loai"
                    + " ORDER BY ma_san_pham,loai",
                thamSo);
    }

    public static List<Object[]> layPhieuSpaTheoTrangThai(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT trang_thai,COUNT(*) FROM phieu_tiep_nhan_spa WHERE tao_luc>=? AND tao_luc<?"
                    + " AND (? IS NULL OR nhan_vien_id=?) GROUP BY trang_thai ORDER BY trang_thai",
                thamSo);
    }

    public static List<Object[]> layHopDongTrongKy(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.trang_thai,COUNT(*),SUM(h.gia_dem),SUM(CASE WHEN h.da_nhan_coc=1 THEN"
                    + " h.tien_coc ELSE 0 END) FROM hop_dong_luu_tru h WHERE h.ngay_vao>=? AND"
                    + " h.ngay_vao<? AND (? IS NULL OR EXISTS(SELECT 1 FROM phien_lam_viec s WHERE"
                    + " s.id=h.phien_nhan_coc_id AND s.tai_khoan_id=?)) GROUP BY h.trang_thai",
                thamSo);
    }

    public static List<Object[]> layCocTrongKy(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT CAST(h.nhan_coc_luc AS date),SUM(h.tien_coc),COUNT(*) FROM hop_dong_luu_tru"
                    + " h JOIN phien_lam_viec s ON s.id=h.phien_nhan_coc_id WHERE h.da_nhan_coc=1"
                    + " AND h.nhan_coc_luc>=? AND h.nhan_coc_luc<? AND (? IS NULL OR"
                    + " s.tai_khoan_id=?) GROUP BY CAST(h.nhan_coc_luc AS date) ORDER BY 1",
                thamSo);
    }

    public static List<Object[]> layLuotKhach(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "WITH khach AS (SELECT k.id,CASE WHEN EXISTS(SELECT 1 FROM hoa_don cu JOIN don_hang"
                    + " dc ON dc.id=cu.don_hang_id WHERE dc.khach_hang_id=k.id AND"
                    + " cu.trang_thai='PAID' AND cu.thanh_toan_luc<?) THEN N'Khách quay lại' ELSE"
                    + " N'Khách mua lần đầu' END nhom FROM khach_hang k) SELECT"
                    + " k.nhom,COUNT(DISTINCT k.id),COUNT(h.id),SUM(h.tong_tien) FROM khach k JOIN"
                    + " don_hang d ON d.khach_hang_id=k.id JOIN hoa_don h ON h.don_hang_id=d.id"
                    + " WHERE h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                    + " GROUP BY k.nhom",
                thamSo);
    }

    public static List<Object[]> laySuDungVoucher(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT v.ma_voucher,k.ho_ten,COUNT(h.id),SUM(h.giam_gia) FROM voucher v JOIN"
                    + " khach_hang k ON k.id=v.khach_hang_id JOIN hoa_don h ON"
                    + " h.ma_voucher=v.ma_voucher WHERE h.trang_thai='PAID' AND h.thanh_toan_luc>=?"
                    + " AND h.thanh_toan_luc<? GROUP BY v.ma_voucher,k.ho_ten ORDER BY"
                    + " v.ma_voucher",
                thamSo);
    }

    public static List<Object[]> layTongHopCa(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ho_ten,s.ten_ca,s.mo_luc,s.dong_luc,s.tien_dau_ca,COALESCE(h.tien,0),COALESCE(c.coc,0),s.tien_thuc_te,s.chenh_lech"
                    + " FROM phien_lam_viec s JOIN tai_khoan t ON t.id=s.tai_khoan_id JOIN"
                    + " nhan_vien n ON n.id=t.nhan_vien_id OUTER APPLY(SELECT SUM(tong_tien) tien"
                    + " FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id WHERE"
                    + " d.phien_lam_viec_id=s.id AND h.trang_thai='PAID')h OUTER APPLY(SELECT"
                    + " SUM(tien_coc) coc FROM hop_dong_luu_tru WHERE phien_nhan_coc_id=s.id AND"
                    + " da_nhan_coc=1)c WHERE s.mo_luc>=? AND s.mo_luc<? ORDER BY s.mo_luc DESC",
                thamSo);
    }

    public static List<Object[]> layDuNoHienTai(Object... thamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " n.ma_ncc,n.ten,COALESCE(SUM(p.tong_tien),0),COALESCE(SUM(p.da_tra),0),COALESCE(SUM(p.tong_tien-p.da_tra),0)"
                    + " FROM nha_cung_cap n LEFT JOIN phieu_nhap_kho p ON p.nha_cung_cap_id=n.id"
                    + " AND p.trang_thai='POSTED' GROUP BY n.ma_ncc,n.ten ORDER BY n.ma_ncc",
                thamSo);
    }
}
