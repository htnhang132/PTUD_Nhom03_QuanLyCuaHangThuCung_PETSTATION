package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.ChiTietThongKeDAO;
import com.petstation.model.*;

import java.util.*;

/** Dữ liệu chứng từ sau cùng phạm vi quyền với báo cáo, không dùng truy vấn GUI không giới hạn. */
public final class ChiTietThongKeDAOImpl implements ChiTietThongKeDAO {
    public ChiTietThongKeDAOImpl() {}

    public ChiTietBaoCao lay(
            String baoCao, String goc, KhoangNgay ky, Long taiKhoan, Long nhanVien) {
        boolean all = taiKhoan == null;
        String own = all ? "" : " AND s.tai_khoan_id=" + taiKhoan;
        String nv = all ? "" : " AND t.nhan_vien_id=" + nhanVien;
        Object tu = ky.tuNgay(), den = ky.layCanTren();
        if (baoCao.contains("doanh thu")) {
            String source =
                    "CASE WHEN EXISTS(SELECT 1 FROM phieu_tiep_nhan_spa t WHERE t.hoa_don_id=h.id)"
                            + " THEN 'Spa' WHEN EXISTS(SELECT 1 FROM hop_dong_luu_tru t WHERE"
                            + " t.hoa_don_id=h.id) THEN 'Hotel' ELSE N'Hàng hóa' END";
            String select =
                    "SELECT h.so_hoa_don,h.thanh_toan_luc,"
                            + source
                            + ",LEFT(sp.value, CHARINDEX(':', sp.value) - 1),n.ho_ten,s.ten_ca,COALESCE(TRY_CAST(SUBSTRING(sp.value, CHARINDEX(':', sp.value) + 1, 50) AS decimal(18,2)), h.tong_tien)"
                            + " FROM hoa_don h JOIN phien_lam_viec s ON s.id=h.phien_lam_viec_id"
                            + " JOIN tai_khoan a ON a.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=a.nhan_vien_id"
                            + " CROSS APPLY STRING_SPLIT(CASE WHEN h.chi_tiet_thanh_toan IS NOT NULL AND CHARINDEX(':', h.chi_tiet_thanh_toan) > 0 THEN h.chi_tiet_thanh_toan ELSE h.hinh_thuc_thanh_toan + ':' + CAST(h.tong_tien AS varchar(30)) END, ';') sp"
                            + " WHERE h.trang_thai='PAID' AND CHARINDEX(':', sp.value) > 0 AND"
                            + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                            + own;
            List<Object[]> rows;
            if (goc.equals("Doanh thu hoàn tất"))
                rows =
                        truyVan(
                                "SELECT h.so_hoa_don,h.thanh_toan_luc,"
                                        + source
                                        + ",h.hinh_thuc_thanh_toan,n.ho_ten,s.ten_ca,h.tong_tien+COALESCE((SELECT"
                                        + " SUM(tien_coc) FROM hop_dong_luu_tru t WHERE"
                                        + " t.hoa_don_id=h.id AND t.da_nhan_coc=1),0) FROM hoa_don"
                                        + " h JOIN phien_lam_viec s ON s.id=h.phien_lam_viec_id JOIN"
                                        + " tai_khoan a ON a.id=s.tai_khoan_id JOIN nhan_vien n ON"
                                        + " n.id=a.nhan_vien_id WHERE h.trang_thai='PAID' AND"
                                        + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                                        + own,
                                tu,
                                den);
            else {
                rows = new ArrayList<>(truyVan(select, tu, den));
                rows.addAll(
                        truyVan(
                                "SELECT"
                                    + " h.ma_hop_dong,h.nhan_coc_luc,'Hotel',COALESCE(h.phuong_thuc_coc,'CASH'),n.ho_ten,s.ten_ca,h.tien_coc"
                                    + " FROM hop_dong_luu_tru h JOIN phien_lam_viec s ON"
                                    + " s.id=h.phien_nhan_coc_id JOIN tai_khoan a ON"
                                    + " a.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=a.nhan_vien_id"
                                    + " WHERE h.da_nhan_coc=1 AND h.nhan_coc_luc>=? AND"
                                    + " h.nhan_coc_luc<?"
                                        + own,
                                tu,
                                den));
                rows.addAll(
                        truyVan(
                                "SELECT p.ma_phieu,p.tao_luc,"
                                        + source
                                        + ",h.hinh_thuc_thanh_toan,n.ho_ten,s.ten_ca,p.chenh_lech"
                                        + " FROM phieu_doi_tra p JOIN hoa_don h ON"
                                        + " h.id=p.hoa_don_id JOIN phien_lam_viec s ON"
                                        + " s.id=h.phien_lam_viec_id JOIN tai_khoan a ON"
                                        + " a.id=s.tai_khoan_id JOIN nhan_vien n ON"
                                        + " n.id=a.nhan_vien_id WHERE p.loai_phieu='LEGACY_RETURN'"
                                        + " AND p.trang_thai='COMPLETED' AND p.tao_luc>=? AND"
                                        + " p.tao_luc<?"
                                        + own,
                                tu,
                                den));
            }
            int group =
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
                    rows,
                    group);
        }
        if (baoCao.contains("Spa")) {
            if (goc.equals("Dịch vụ đã thanh toán"))
                return new ChiTietBaoCao(
                        new String[] {
                            "Phiếu Spa",
                            "Hóa đơn",
                            "Mã dịch vụ",
                            "Dịch vụ",
                            "Lượt",
                            "Giá trị dịch vụ"
                        },
                        truyVan(
                                "SELECT t.ma_phieu,h.so_hoa_don,g.ma_goi,g.ten_goi,t.so_luong,CASE"
                                    + " WHEN h.tam_tinh>0 THEN t.tong_tien*h.tong_tien/h.tam_tinh"
                                    + " ELSE t.tong_tien END FROM phieu_tiep_nhan_spa t JOIN"
                                    + " goi_dich_vu_spa g ON g.id=t.goi_id JOIN hoa_don h ON"
                                    + " h.id=t.hoa_don_id JOIN phien_lam_viec s ON"
                                    + " s.id=h.phien_lam_viec_id WHERE t.trang_thai='PAID' AND"
                                    + " h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                                        + own,
                                tu,
                                den),
                        2);
            boolean hen = goc.equals("Trạng thái lịch hẹn");
            return new ChiTietBaoCao(
                    new String[] {"Chứng từ", "Thú cưng", "Thời gian", "Trạng thái"},
                    truyVan(
                            "SELECT t."
                                    + (hen ? "ma_lich_hen" : "ma_phieu")
                                    + ",p.ten,t."
                                    + (hen ? "bat_dau" : "tao_luc")
                                    + ",t.trang_thai FROM "
                                    + (hen ? "lich_hen" : "phieu_tiep_nhan_spa")
                                    + " t JOIN thu_cung p ON p.id=t.thu_cung_id WHERE t."
                                    + (hen ? "bat_dau" : "tao_luc")
                                    + ">=? AND t."
                                    + (hen ? "bat_dau" : "tao_luc")
                                    + "<?"
                                    + nv,
                            tu,
                            den),
                    3);
        }
        if (baoCao.contains("lưu trú")) {
            if (goc.equals("Trạng thái phòng hiện tại"))
                return new ChiTietBaoCao(
                        new String[] {"Mã phòng", "Loại phòng", "Trạng thái"},
                        truyVan("SELECT ma_phong,loai_phong,trang_thai FROM phong_hotel"),
                        2);
            if (goc.equals("Cọc nhận trong kỳ"))
                return new ChiTietBaoCao(
                        new String[] {
                            "Hợp đồng", "Ngày nhận", "Phương thức", "Tiền cọc", "Người nhận", "Ca"
                        },
                        truyVan(
                                "SELECT"
                                    + " h.ma_hop_dong,h.nhan_coc_luc,COALESCE(h.phuong_thuc_coc,'CASH'),h.tien_coc,n.ho_ten,s.ten_ca"
                                    + " FROM hop_dong_luu_tru h JOIN phien_lam_viec s ON"
                                    + " s.id=h.phien_nhan_coc_id JOIN tai_khoan a ON"
                                    + " a.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=a.nhan_vien_id"
                                    + " WHERE h.da_nhan_coc=1 AND h.nhan_coc_luc>=? AND"
                                    + " h.nhan_coc_luc<?"
                                        + own,
                                tu,
                                den),
                        1);
            if (goc.equals("Lưu trú đã quyết toán"))
                return new ChiTietBaoCao(
                        new String[] {
                            "Hợp đồng",
                            "Loại phòng",
                            "Hóa đơn",
                            "Ngày quyết toán",
                            "Doanh thu hoàn tất"
                        },
                        truyVan(
                                "SELECT"
                                    + " t.ma_hop_dong,p.loai_phong,h.so_hoa_don,h.thanh_toan_luc,h.tong_tien+CASE"
                                    + " WHEN t.da_nhan_coc=1 THEN t.tien_coc ELSE 0 END FROM"
                                    + " hop_dong_luu_tru t JOIN phong_hotel p ON p.id=t.phong_id"
                                    + " JOIN hoa_don h ON h.id=t.hoa_don_id JOIN phien_lam_viec s"
                                    + " ON s.id=h.phien_lam_viec_id WHERE t.trang_thai='CHECKED_OUT'"
                                    + " AND h.thanh_toan_luc>=? AND h.thanh_toan_luc<?"
                                        + own,
                                tu,
                                den),
                        1);
            return new ChiTietBaoCao(
                    new String[] {"Hợp đồng", "Phòng", "Ngày vào", "Trạng thái", "Cọc đã nhận"},
                    truyVan(
                            "SELECT t.ma_hop_dong,p.ma_phong,t.ngay_vao,t.trang_thai,CASE WHEN"
                                + " t.da_nhan_coc=1 THEN t.tien_coc ELSE 0 END FROM"
                                + " hop_dong_luu_tru t JOIN phong_hotel p ON p.id=t.phong_id WHERE"
                                + " t.ngay_vao>=? AND t.ngay_vao<?"
                                    + (all
                                            ? ""
                                            : " AND EXISTS(SELECT 1 FROM phien_lam_viec s WHERE"
                                                  + " s.id=t.phien_nhan_coc_id AND s.tai_khoan_id="
                                                    + taiKhoan
                                                    + ")"),
                            tu,
                            den),
                    3);
        }
        if (baoCao.contains("Công nợ")) {
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
                    truyVan(
                            "SELECT"
                                + " p.so_phieu,n.ma_ncc,n.ten,p.ngay_nhap,p.tong_tien,p.da_tra,p.tong_tien-p.da_tra"
                                + " FROM phieu_nhap_kho p JOIN nha_cung_cap n ON"
                                + " n.id=p.nha_cung_cap_id WHERE p.trang_thai='POSTED'"
                                    + (goc.equals("Dư nợ hiện tại")
                                            ? ""
                                            : " AND p.ngay_nhap>=? AND p.ngay_nhap<?"),
                            goc.equals("Dư nợ hiện tại")
                                    ? new Object[] {}
                                    : new Object[] {tu, den}),
                    1);
        }
        if (baoCao.contains("khách hàng") && goc.equals("Chi tiêu theo kỳ")) {
            return new ChiTietBaoCao(
                    new String[] {"Hóa đơn", "Mã KH", "Khách hàng", "Thanh toán lúc", "Chi tiêu"},
                    truyVan(
                            "SELECT"
                                + " h.so_hoa_don,k.ma_khach_hang,k.ho_ten,h.thanh_toan_luc,h.tong_tien"
                                + " FROM hoa_don h JOIN khach_hang k ON k.id=h.khach_hang_id WHERE"
                                + " h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND"
                                + " h.thanh_toan_luc<?",
                            tu,
                            den),
                    1);
        }
        if (baoCao.contains("hàng hóa")) {
            if (goc.equals("Sản phẩm bán chạy"))
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
                                    + " h.so_hoa_don,p.ma_san_pham,p.ten,h.thanh_toan_luc,i.so_luong-COALESCE(r.qty,0),CASE"
                                    + " WHEN h.tam_tinh>0 THEN"
                                    + " i.so_luong*i.don_gia*(h.tong_tien+COALESCE(ex.credit,0))/h.tam_tinh"
                                    + " ELSE 0 END-COALESCE(r.refund,0),p.nhom_hang FROM"
                                    + " chi_tiet_hoa_don i JOIN san_pham p ON p.id=i.san_pham_id"
                                    + " JOIN hoa_don h ON h.id=i.hoa_don_id JOIN phien_lam_viec s"
                                    + " ON s.id=h.phien_lam_viec_id OUTER APPLY(SELECT"
                                    + " SUM(c.so_luong) qty,SUM(c.tien_hoan) refund FROM"
                                    + " chi_tiet_doi_tra c JOIN phieu_doi_tra x ON"
                                    + " x.id=c.phieu_doi_tra_id WHERE c.chi_tiet_hoa_don_id=i.id"
                                    + " AND x.trang_thai='COMPLETED')r OUTER APPLY(SELECT"
                                    + " SUM(dx.gia_tri_duoc_doi) credit FROM phieu_doi_tra dx WHERE"
                                    + " dx.hoa_don_thay_the_id=h.id AND dx.loai_phieu='EXCHANGE'"
                                    + " AND dx.trang_thai='COMPLETED')ex WHERE"
                                    + " h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND"
                                    + " h.thanh_toan_luc<?"
                                        + own,
                                tu,
                                den),
                        1);
            if (goc.equals("Tồn kho hiện tại") || goc.equals("Sắp hết hạn"))
                return new ChiTietBaoCao(
                        new String[] {
                            "Lô hàng", "Mã SP", "Sản phẩm", "Hạn dùng", "Tồn", "Trạng thái"
                        },
                        truyVan(
                                "SELECT"
                                    + " l.so_lo,p.ma_san_pham,p.ten,l.han_su_dung,l.so_luong_ton,l.trang_thai"
                                    + " FROM lo_hang l JOIN san_pham p ON p.id=l.san_pham_id"
                                        + (goc.equals("Sắp hết hạn")
                                                ? " WHERE l.so_luong_ton>0 AND"
                                                      + " l.han_su_dung<=DATEADD(day,30,CAST(GETDATE()"
                                                      + " AS date))"
                                                : "")),
                        1);
            String ledger =
                    "SELECT n.so_phieu chung_tu,p.ma_san_pham,p.ten,N'Nhập kho' loai,n.ngay_nhap"
                        + " ngay,c.so_luong FROM chi_tiet_phieu_nhap c JOIN phieu_nhap_kho n ON"
                        + " n.id=c.phieu_nhap_id JOIN san_pham p ON p.id=c.san_pham_id WHERE"
                        + " n.trang_thai='POSTED' UNION ALL SELECT"
                        + " h.so_hoa_don,p.ma_san_pham,p.ten,N'Bán"
                        + " hàng',h.thanh_toan_luc,-c.so_luong FROM chi_tiet_hoa_don c JOIN"
                        + " san_pham p ON p.id=c.san_pham_id JOIN hoa_don h ON"
                        + " h.id=c.hoa_don_id WHERE h.trang_thai='PAID' UNION ALL SELECT"
                        + " n.so_phieu,p.ma_san_pham,p.ten,N'Xuất hủy',n.tao_luc,-c.so_luong FROM"
                        + " chi_tiet_phieu_xuat c JOIN phieu_xuat_kho n ON n.id=c.phieu_xuat_id"
                        + " JOIN lo_hang l ON l.id=c.lo_hang_id JOIN san_pham p ON"
                        + " p.id=l.san_pham_id WHERE n.trang_thai='POSTED' UNION ALL SELECT"
                        + " n.ma_phieu,p.ma_san_pham,p.ten,N'Nhận hàng đổi',n.tao_luc,c.so_luong"
                        + " FROM chi_tiet_doi_tra c JOIN phieu_doi_tra n ON n.id=c.phieu_doi_tra_id"
                        + " JOIN chi_tiet_hoa_don i ON i.id=c.chi_tiet_hoa_don_id JOIN san_pham p"
                        + " ON p.id=i.san_pham_id WHERE n.trang_thai='COMPLETED'";
            return new ChiTietBaoCao(
                    new String[] {
                        "Chứng từ", "Mã SP", "Sản phẩm", "Loại giao dịch", "Ngày", "Số lượng"
                    },
                    truyVan(
                            "WITH dong AS("
                                    + ledger
                                    + ") SELECT * FROM dong WHERE ngay>=? AND ngay<?",
                            tu,
                            den),
                    1);
        }
        if (baoCao.contains("khách hàng")) {
            if (goc.equals("Khách mới / quay lại"))
                return new ChiTietBaoCao(
                        new String[] {
                            "Hóa đơn",
                            "Nhóm khách",
                            "Mã KH",
                            "Khách hàng",
                            "Ngày",
                            "Tiền thanh toán"
                        },
                        truyVan(
                                "SELECT h.so_hoa_don,CASE WHEN EXISTS(SELECT 1 FROM hoa_don cu"
                                    + " WHERE cu.khach_hang_id=k.id AND cu.trang_thai='PAID' AND"
                                    + " cu.thanh_toan_luc<?) THEN N'Khách quay lại' ELSE N'Khách"
                                    + " mua lần đầu'"
                                    + " END,k.ma_khach_hang,k.ho_ten,h.thanh_toan_luc,h.tong_tien"
                                    + " FROM khach_hang k JOIN hoa_don h ON h.khach_hang_id=k.id"
                                    + " WHERE h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND"
                                    + " h.thanh_toan_luc<?",
                                tu,
                                tu,
                                den),
                        1);
            if (goc.equals("Lượt dùng voucher trong kỳ"))
                return new ChiTietBaoCao(
                        new String[] {"Hóa đơn", "Voucher", "Khách hàng", "Ngày", "Giảm giá"},
                        truyVan(
                                "SELECT"
                                    + " h.so_hoa_don,v.ma_voucher,k.ho_ten,h.thanh_toan_luc,h.giam_gia"
                                    + " FROM voucher v JOIN khach_hang k ON k.id=v.khach_hang_id"
                                    + " JOIN hoa_don h ON h.ma_voucher=v.ma_voucher WHERE"
                                    + " h.trang_thai='PAID' AND h.thanh_toan_luc>=? AND"
                                    + " h.thanh_toan_luc<?",
                                tu,
                                den),
                        1);
            return new ChiTietBaoCao(
                    new String[] {"Hóa đơn", "Voucher", "Khách hàng", "Ngày", "Giảm giá"},
                    truyVan(
                            "SELECT h.so_hoa_don,v.ma_voucher,COALESCE(k.ho_ten,N'Khách"
                                + " lẻ'),h.thanh_toan_luc,h.giam_gia FROM voucher v JOIN hoa_don h"
                                + " ON h.ma_voucher=v.ma_voucher LEFT JOIN khach_hang k ON"
                                + " k.id=v.khach_hang_id WHERE h.trang_thai='PAID'"),
                    1);
        }
        if (baoCao.contains("nhân viên")) {
            if (goc.equals("Lịch làm việc"))
                return new ChiTietBaoCao(
                        new String[] {
                            "Ngày làm",
                            "Mã NV",
                            "Nhân viên",
                            "Ca",
                            "Bắt đầu",
                            "Kết thúc",
                            "Trạng thái"
                        },
                        truyVan(
                                "SELECT"
                                    + " l.ngay_lam,n.ma_nhan_vien,n.ho_ten,c.ten_ca,c.gio_bat_dau,c.gio_ket_thuc,l.trang_thai"
                                    + " FROM lich_lam_viec l JOIN nhan_vien n ON"
                                    + " n.id=l.nhan_vien_id JOIN ca_lam_viec c ON"
                                    + " c.id=l.ca_lam_viec_id WHERE l.ngay_lam>=? AND l.ngay_lam<?",
                                tu,
                                den),
                        1);
            return new ChiTietBaoCao(
                    new String[] {
                        "Ca",
                        "Nhân viên",
                        "Mở lúc",
                        "Đóng lúc",
                        "Tiền đầu ca",
                        "Thực đếm",
                        "Chênh lệch"
                    },
                    truyVan(
                            "SELECT"
                                + " s.ten_ca,n.ho_ten,s.mo_luc,s.dong_luc,s.tien_dau_ca,s.tien_thuc_te,s.chenh_lech"
                                + " FROM phien_lam_viec s JOIN tai_khoan a ON a.id=s.tai_khoan_id"
                                + " JOIN nhan_vien n ON n.id=a.nhan_vien_id WHERE s.mo_luc>=? AND"
                                + " s.mo_luc<?",
                            tu,
                            den),
                    1);
        }
        throw new IllegalArgumentException("Báo cáo chưa được đăng ký chi tiết: " + baoCao);
    }
}
