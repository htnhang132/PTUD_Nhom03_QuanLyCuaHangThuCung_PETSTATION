package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.NghiepVuDAO;
import com.petstation.model.LoaiNghiepVu;

import java.util.List;

/** Thực hiện truy vấn JDBC cho JDBC nghiệp vụ DAO. */
public final class NghiepVuImpl implements NghiepVuDAO {
    private record KieuHienThi(String tieuDe, String[] cacCot, String sql) {}

    public NghiepVuImpl() {}

    public ThongTinBang layThongTin(LoaiNghiepVu cheDo) {
        KieuHienThi kieu = taoKieuHienThi(cheDo);
        return new ThongTinBang(kieu.tieuDe(), kieu.cacCot());
    }

    private KieuHienThi taoKieuHienThi(LoaiNghiepVu cheDo) {
        return switch (cheDo) {
            case INVOICES ->
                    new KieuHienThi(
                            "Tìm kiếm hóa đơn",
                            new String[] {
                                "ID",
                                "Số hóa đơn",
                                "Khách hàng",
                                "Thu ngân",
                                "Thời gian",
                                "Tổng tiền",
                                "Thanh toán",
                                "Trạng thái"
                            },
                            "SELECT h.id,h.so_hoa_don,COALESCE(k.ho_ten,N'Khách"
                                + " lẻ'),n.ho_ten,h.thanh_toan_luc,h.tong_tien,h.hinh_thuc_thanh_toan,h.trang_thai"
                                + " FROM hoa_don h JOIN don_hang d ON d.id=h.don_hang_id JOIN"
                                + " phien_lam_viec s ON s.id=d.phien_lam_viec_id JOIN tai_khoan a"
                                + " ON a.id=s.tai_khoan_id JOIN nhan_vien n ON n.id=a.nhan_vien_id"
                                + " LEFT JOIN khach_hang k ON k.id=d.khach_hang_id ORDER BY"
                                + " h.thanh_toan_luc DESC,h.id DESC");
            case BATCHES ->
                    new KieuHienThi(
                            "Tồn kho theo lô",
                            new String[] {
                                "ID",
                                "Sản phẩm",
                                "Tên sản phẩm",
                                "Số lô",
                                "Hạn sử dụng",
                                "Tồn kho",
                                "Trạng thái"
                            },
                            "SELECT"
                                + " l.id,p.ma_san_pham,p.ten,l.so_lo,l.han_su_dung,l.so_luong_ton,l.trang_thai"
                                + " FROM lo_hang l JOIN san_pham p ON p.id=l.san_pham_id ORDER BY"
                                + " p.ma_san_pham,l.han_su_dung,l.id");
            case APPOINTMENTS ->
                    new KieuHienThi(
                            "Lịch hẹn Spa",
                            new String[] {
                                "ID",
                                "Mã lịch",
                                "Khách hàng",
                                "Thú cưng",
                                "Dịch vụ",
                                "Nhân viên",
                                "Bắt đầu",
                                "Kết thúc",
                                "Trạng thái"
                            },
                            "SELECT"
                                + " l.id,l.ma_lich_hen,k.ho_ten,p.ten,g.ten_goi,n.ho_ten,l.bat_dau,l.ket_thuc,l.trang_thai"
                                + " FROM lich_hen l JOIN khach_hang k ON k.id=l.khach_hang_id JOIN"
                                + " thu_cung p ON p.id=l.thu_cung_id LEFT JOIN goi_dich_vu_spa g ON"
                                + " g.id=l.dich_vu_id LEFT JOIN nhan_vien n ON n.id=l.nhan_vien_id"
                                + " ORDER BY l.bat_dau DESC");
            case SPA_TICKETS, SPA_INTAKE, SPA_SERVICES, SPA_PROGRESS, SPA_CLOSE ->
                    new KieuHienThi(
                            switch (cheDo) {
                                case SPA_INTAKE -> "Tiếp nhận Spa";
                                case SPA_SERVICES -> "Chọn dịch vụ Spa";
                                case SPA_PROGRESS -> "Tiến độ Spa";
                                case SPA_CLOSE -> "Đóng phiếu Spa";
                                default -> "Phiếu Spa";
                            },
                            new String[] {
                                "ID",
                                "Mã phiếu",
                                "Khách hàng",
                                "Thú cưng",
                                "Cân nặng (kg)",
                                "Nhân viên",
                                "Tổng tiền",
                                "Trạng thái",
                                "Tiếp nhận lúc"
                            },
                            "SELECT"
                                + " t.id,t.ma_phieu,k.ho_ten,p.ten,t.can_nang_thuc_te,n.ho_ten,t.tong_tien,t.trang_thai,t.tao_luc"
                                + " FROM phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                                + " p.id=t.thu_cung_id JOIN khach_hang k ON k.id=p.khach_hang_id"
                                + " LEFT JOIN nhan_vien n ON n.id=t.nhan_vien_id"
                                    + (cheDo == LoaiNghiepVu.SPA_TICKETS
                                            ? " WHERE t.tao_luc>=CONVERT(date,SYSDATETIME()) AND"
                                                  + " t.tao_luc<DATEADD(day,1,CONVERT(date,SYSDATETIME()))"
                                            : "")
                                    + " ORDER BY t.tao_luc"
                                    + " DESC,t.id DESC");
            case HOTEL_STAY, HOTEL_BOOKING, HOTEL_JOURNAL, HOTEL_CHECKOUT ->
                    new KieuHienThi(
                            switch (cheDo) {
                                case HOTEL_BOOKING -> "Đặt phòng Pet Hotel";
                                case HOTEL_JOURNAL -> "Nhật ký lưu trú";
                                case HOTEL_CHECKOUT -> "Trả phòng và quyết toán";
                                default -> "Lưu trú Pet Hotel";
                            },
                            new String[] {
                                "ID",
                                "Mã hợp đồng",
                                "Khách hàng",
                                "Thú cưng",
                                "Phòng",
                                "Ngày vào",
                                "Ngày ra dự kiến",
                                "Cọc đã nhận",
                                "Giá / ngày",
                                "Trạng thái",
                                "Cọc yêu cầu"
                            },
                            "SELECT"
                                + " h.id,h.ma_hop_dong,k.ho_ten,p.ten,r.ma_phong,h.ngay_vao,h.ngay_ra_du_kien,CASE"
                                + " WHEN h.da_nhan_coc=1 THEN h.tien_coc ELSE 0"
                                + " END,COALESCE(h.gia_dem,r.gia_dem),h.trang_thai,COALESCE(h.coc_yeu_cau,h.tien_coc)"
                                + " FROM hop_dong_luu_tru h JOIN khach_hang k ON"
                                + " k.id=h.khach_hang_id JOIN thu_cung p ON p.id=h.thu_cung_id JOIN"
                                + " phong_hotel r ON r.id=h.phong_id ORDER BY h.ngay_vao DESC,h.id"
                                + " DESC");
            case ROOMS ->
                    new KieuHienThi(
                            "Sơ đồ phòng và lưu trú",
                            new String[] {
                                "ID",
                                "Mã phòng",
                                "Loại phòng",
                                "Sức chứa",
                                "Giá / ngày",
                                "Trạng thái"
                            },
                            "SELECT id,ma_phong,loai_phong,suc_chua,gia_dem,trang_thai FROM"
                                    + " phong_hotel ORDER BY ma_phong");
            case PURCHASE ->
                    new KieuHienThi(
                            "Nhập kho",
                            new String[] {
                                "ID",
                                "Số phiếu",
                                "Nhà cung cấp",
                                "Ngày nhập",
                                "Tổng tiền",
                                "Đã trả",
                                "Trạng thái"
                            },
                            "SELECT"
                                + " p.id,p.so_phieu,n.ten,p.ngay_nhap,p.tong_tien,p.da_tra,p.trang_thai"
                                + " FROM phieu_nhap_kho p JOIN nha_cung_cap n ON"
                                + " n.id=p.nha_cung_cap_id ORDER BY p.id DESC");
            case WRITE_OFF ->
                    new KieuHienThi(
                            "Xuất kho và xuất hủy",
                            new String[] {
                                "ID",
                                "Số phiếu",
                                "Loại xuất",
                                "Lý do",
                                "Giá trị",
                                "Trạng thái",
                                "Ngày tạo"
                            },
                            "SELECT id,so_phieu,loai_xuat,ly_do,tong_gia_tri,trang_thai,tao_luc"
                                    + " FROM phieu_xuat_kho ORDER BY id DESC");
            case STOCKTAKE ->
                    new KieuHienThi(
                            "Kiểm kê tồn kho",
                            new String[] {
                                "ID", "Số biên bản", "Kỳ kiểm kê", "Trạng thái", "Ngày tạo"
                            },
                            "SELECT id,so_bien_ban,ky_kiem_ke,trang_thai,tao_luc FROM"
                                    + " bien_ban_kiem_ke ORDER BY id DESC");
            case RETURNS ->
                    new KieuHienThi(
                            "Đổi trả hàng",
                            new String[] {
                                "ID",
                                "Mã phiếu",
                                "Hóa đơn gốc",
                                "Thu chênh lệch / lịch sử hoàn",
                                "Lý do",
                                "Trạng thái",
                                "Ngày tạo"
                            },
                            "SELECT"
                                + " p.id,p.ma_phieu,h.so_hoa_don,p.chenh_lech,p.ly_do,CONCAT(p.loai_phieu,':"
                                + " ',p.trang_thai),p.tao_luc FROM phieu_doi_tra p JOIN hoa_don h"
                                + " ON h.id=p.hoa_don_id ORDER BY p.id DESC");
            case PENDING ->
                    new KieuHienThi(
                            "Đơn đang treo",
                            new String[] {
                                "ID",
                                "Mã đơn",
                                "Khách hàng",
                                "Người lập",
                                "Ngày tạo",
                                "Số dòng",
                                "Tạm tính"
                            },
                            "SELECT g.id,g.ma_gio,COALESCE(k.ho_ten,N'Khách"
                                + " lẻ'),n.ho_ten,g.tao_luc,(SELECT COUNT(*) FROM"
                                + " chi_tiet_gio_hang_treo c WHERE c.gio_id=g.id),(SELECT"
                                + " COALESCE(SUM(c.don_gia*c.so_luong),0) FROM"
                                + " chi_tiet_gio_hang_treo c WHERE c.gio_id=g.id) FROM"
                                + " gio_hang_treo g JOIN tai_khoan t ON t.id=g.tai_khoan_id JOIN"
                                + " nhan_vien n ON n.id=t.nhan_vien_id LEFT JOIN khach_hang k ON"
                                + " k.id=g.khach_hang_id WHERE g.tai_khoan_id=? ORDER BY g.id"
                                + " DESC");
        };
    }

    public List<Object[]> layCacDong(LoaiNghiepVu cheDo, long idNguoiThucHien) {
        return cheDo == LoaiNghiepVu.PENDING
                ? truyVan(taoKieuHienThi(cheDo).sql(), idNguoiThucHien)
                : truyVan(taoKieuHienThi(cheDo).sql());
    }
}
