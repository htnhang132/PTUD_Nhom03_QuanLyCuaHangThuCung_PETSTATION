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
    public static List<Object[]> layDoanhThuTheoNgay(
            java.time.LocalDate tu, java.time.LocalDate denNgay, Long taiKhoan, Long nhanVien) {
        return ThaoTacSQL.truyVan(
                "WITH ledger AS (SELECT CAST(h.thanh_toan_luc AS date) day,h.tong_tien amount FROM"
                    + " hoa_don h JOIN don_hang d ON d.id=h.don_hang_id JOIN phien_lam_viec s ON"
                    + " s.id=d.phien_lam_viec_id WHERE h.trang_thai='PAID'"
                        + (taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan)
                        + " UNION ALL SELECT CAST(p.tao_luc AS date),p.chenh_lech FROM"
                        + " phieu_doi_tra p JOIN hoa_don h ON h.id=p.hoa_don_id JOIN don_hang d ON"
                        + " d.id=h.don_hang_id JOIN phien_lam_viec s ON s.id=d.phien_lam_viec_id"
                        + " WHERE p.trang_thai='COMPLETED'"
                        + (taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan)
                        + ") SELECT day,SUM(amount) FROM ledger WHERE day>=? AND day<? GROUP BY day"
                        + " ORDER BY day",
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
        // Nhóm.
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
        return ThaoTacSQL.truyVan(
                "WITH source AS (SELECT "
                        + nhom
                        + " label,h.tong_tien-COALESCE((SELECT SUM(-p.chenh_lech) FROM"
                        + " phieu_doi_tra p WHERE p.hoa_don_id=h.id AND"
                        + " p.trang_thai='COMPLETED'),0) amount FROM hoa_don h JOIN don_hang d ON"
                        + " d.id=h.don_hang_id JOIN phien_lam_viec s ON s.id=d.phien_lam_viec_id"
                        + " JOIN tai_khoan t ON t.id=s.tai_khoan_id JOIN nhan_vien n ON"
                        + " n.id=t.nhan_vien_id WHERE h.trang_thai='PAID' AND h.thanh_toan_luc>=?"
                        + " AND h.thanh_toan_luc<?"
                        + (taiKhoan == null ? "" : " AND s.tai_khoan_id=" + taiKhoan)
                        + ") SELECT label,SUM(amount),COUNT(*) FROM source GROUP BY label ORDER BY"
                        + " SUM(amount) DESC",
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
                    + " i.so_luong*i.don_gia*h.tong_tien/h.tam_tinh ELSE 0"
                    + " END)-SUM(COALESCE(r.refund,0)) amount FROM chi_tiet_don_hang i JOIN"
                    + " san_pham p ON p.id=i.san_pham_id JOIN hoa_don h ON"
                    + " h.don_hang_id=i.don_hang_id JOIN don_hang d ON d.id=h.don_hang_id JOIN"
                    + " phien_lam_viec s ON s.id=d.phien_lam_viec_id OUTER APPLY(SELECT"
                    + " SUM(c.so_luong) qty,SUM(c.tien_hoan) refund FROM chi_tiet_doi_tra c JOIN"
                    + " phieu_doi_tra x ON x.id=c.phieu_doi_tra_id WHERE"
                    + " c.chi_tiet_don_hang_id=i.id AND x.trang_thai='COMPLETED') r WHERE"
                    + " h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
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
                "SELECT g.ma_goi,g.ten_goi,SUM(c.so_luong),SUM(c.thanh_tien) FROM"
                    + " chi_tiet_phieu_spa c JOIN goi_dich_vu_spa g ON g.id=c.goi_id JOIN"
                    + " phieu_tiep_nhan_spa t ON t.id=c.phieu_spa_id JOIN hoa_don h ON"
                    + " h.id=t.hoa_don_id JOIN don_hang d ON d.id=h.don_hang_id JOIN phien_lam_viec"
                    + " s ON s.id=d.phien_lam_viec_id WHERE h.thanh_toan_luc>=? AND"
                    + " h.thanh_toan_luc<? AND t.trang_thai='PAID'"
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
}
