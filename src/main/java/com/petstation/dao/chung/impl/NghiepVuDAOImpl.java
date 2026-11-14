package com.petstation.dao.chung.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;

import com.petstation.dao.chung.NghiepVuDAO;
import com.petstation.dto.DongNghiepVu;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.util.DuLieu;

import java.util.List;

public final class NghiepVuDAOImpl implements NghiepVuDAO {
    private static final class KieuHienThi {
        final String tieuDe;
        final String[] cacCot;
        final String sql;

        private KieuHienThi(String tieuDe, String[] cacCot, String sql) {
            this.tieuDe = tieuDe;
            this.cacCot = cacCot;
            this.sql = sql;
        }
    }

    public NghiepVuDAOImpl() {}

    public ThongTinBang layThongTin(LoaiNghiepVu cheDo) {
        KieuHienThi kieu = taoKieuHienThi(cheDo);
        return new ThongTinBang(kieu.tieuDe, kieu.cacCot);
    }

    private KieuHienThi taoKieuHienThi(LoaiNghiepVu cheDo) {
        return switch (cheDo) {
            case HOA_DON -> taoKieuHoaDon();
            case LO_HANG -> taoKieuLoHang();
            case LICH_HEN -> taoKieuLichHen();
            case PHIEU_SPA, TIEP_NHAN_SPA, GOI_DICH_VU_SPA, TIEN_DO_SPA, HOAN_TAT_SPA ->
                    taoKieuPhieuSpa(cheDo);
            case HOP_DONG_LUU_TRU, DAT_PHONG_LUU_TRU, NHAT_KY_LUU_TRU, TRA_PHONG_LUU_TRU ->
                    taoKieuHopDongLuuTru(cheDo);
            case PHONG_LUU_TRU ->
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
                            "SELECT ma_phong AS ma_khoa, ma_phong, loai_phong, suc_chua, gia_dem,"
                                    + " trang_thai FROM phong_hotel ORDER BY ma_phong");
            case PHIEU_NHAP_KHO, PHIEU_XUAT_KHO, KIEM_KE -> taoKieuKho(cheDo);
            case DOI_TRA, GIO_HANG_TREO -> taoKieuDoiTraHoacTreo(cheDo);
        };
    }

    private KieuHienThi taoKieuHoaDon() {
        return new KieuHienThi(
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
                "SELECT h.ma_hoa_don AS ma_khoa, h.ma_hoa_don, COALESCE(k.ho_ten,"
                        + " N'Khách lẻ'), COALESCE(n.ho_ten, N'Thu ngân'),"
                        + " h.thanh_toan_luc, h.tong_tien, CASE h.hinh_thuc_thanh_toan WHEN"
                        + " 'TIEN_MAT' THEN N'Tiền mặt' WHEN 'CHUYEN_KHOAN' THEN N'Chuyển"
                        + " khoản' WHEN 'THE_NGAN_HANG' THEN N'Thẻ' WHEN 'THE' THEN N'Thẻ'"
                        + " WHEN 'DIEM_TICH_LUY' THEN N'Điểm tích lũy' ELSE"
                        + " COALESCE(h.hinh_thuc_thanh_toan, N'Tiền mặt')"
                        + " END, CASE h.trang_thai WHEN 'DA_THANH_TOAN' THEN N'Đã thanh"
                        + " toán' WHEN 'DA_HUY' THEN N'Đã hủy' ELSE COALESCE(h.trang_thai,"
                        + " N'Đã thanh toán') END FROM hoa_don h LEFT JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien = h.ma_nhan_vien LEFT JOIN khach_hang k ON"
                        + " k.ma_khach_hang = h.ma_khach_hang WHERE h.trang_thai <>"
                        + " 'GIO_TREO' ORDER BY h.thanh_toan_luc DESC, h.ma_hoa_don DESC");
    }

    private KieuHienThi taoKieuLoHang() {
        return new KieuHienThi(
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
                "SELECT l.ma_lo, p.ma_san_pham, p.ten, l.so_lo, l.han_su_dung,"
                        + " l.so_luong_ton, l.trang_thai FROM lo_hang l JOIN san_pham p ON"
                        + " p.ma_san_pham = l.ma_san_pham ORDER BY p.ma_san_pham,"
                        + " l.han_su_dung, l.ma_lo");
    }

    private KieuHienThi taoKieuLichHen() {
        return new KieuHienThi(
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
                "SELECT l.ma_lich_hen AS ma_khoa, l.ma_lich_hen, k.ho_ten, p.ten,"
                        + " g.ten_goi, n.ho_ten, l.bat_dau, l.ket_thuc, l.trang_thai FROM"
                        + " lich_hen l JOIN khach_hang k ON k.ma_khach_hang ="
                        + " l.ma_khach_hang JOIN thu_cung p ON p.ma_thu_cung ="
                        + " l.ma_thu_cung LEFT JOIN goi_dich_vu_spa g ON g.ma_goi ="
                        + " l.ma_goi LEFT JOIN nhan_vien n ON n.ma_nhan_vien ="
                        + " l.ma_nhan_vien ORDER BY l.bat_dau DESC");
    }

    private KieuHienThi taoKieuPhieuSpa(LoaiNghiepVu cheDo) {
        String tieuDe =
                switch (cheDo) {
                    case TIEP_NHAN_SPA -> "Tiếp nhận Spa";
                    case GOI_DICH_VU_SPA -> "Chọn dịch vụ Spa";
                    case TIEN_DO_SPA -> "Tiến độ Spa";
                    case HOAN_TAT_SPA -> "Đóng phiếu Spa";
                    default -> "Phiếu Spa";
                };
        String dieuKien =
                cheDo == LoaiNghiepVu.PHIEU_SPA
                        ? " WHERE t.tao_luc >= CONVERT(date, SYSDATETIME()) AND"
                                + " t.tao_luc < DATEADD(day, 1, CONVERT(date, SYSDATETIME()))"
                        : "";
        return new KieuHienThi(
                tieuDe,
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
                "SELECT t.ma_phieu AS ma_khoa, t.ma_phieu, k.ho_ten, p.ten,"
                        + " t.can_nang_thuc_te, n.ho_ten, t.tong_tien, t.trang_thai,"
                        + " t.tao_luc FROM phieu_tiep_nhan_spa t JOIN thu_cung p ON"
                        + " p.ma_thu_cung = t.ma_thu_cung JOIN khach_hang k ON"
                        + " k.ma_khach_hang = p.ma_khach_hang LEFT JOIN nhan_vien n ON"
                        + " n.ma_nhan_vien = t.ma_nhan_vien"
                        + dieuKien
                        + " ORDER BY t.tao_luc DESC, t.ma_phieu DESC");
    }

    private KieuHienThi taoKieuHopDongLuuTru(LoaiNghiepVu cheDo) {
        String tieuDe =
                switch (cheDo) {
                    case DAT_PHONG_LUU_TRU -> "Đặt phòng Pet Hotel";
                    case NHAT_KY_LUU_TRU -> "Nhật ký chăm sóc lưu trú";
                    case TRA_PHONG_LUU_TRU -> "Trả phòng và quyết toán";
                    default -> "Lưu trú Pet Hotel";
                };
        String dieuKien =
                switch (cheDo) {
                    case DAT_PHONG_LUU_TRU -> " WHERE h.trang_thai='DA_DAT'";
                    case NHAT_KY_LUU_TRU ->
                            " WHERE h.trang_thai IN ('DANG_O','DA_TRA_PHONG') AND EXISTS"
                                    + " (SELECT 1 FROM nhat_ky_cham_soc j"
                                    + " WHERE j.ma_hop_dong=h.ma_hop_dong)";
                    case TRA_PHONG_LUU_TRU -> " WHERE h.trang_thai='DANG_O'";
                    default -> "";
                };
        return new KieuHienThi(
                tieuDe,
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
                "SELECT h.ma_hop_dong AS ma_khoa, h.ma_hop_dong, k.ho_ten, p.ten,"
                        + " r.ma_phong, h.ngay_vao, h.ngay_ra_du_kien, CASE WHEN"
                        + " h.da_nhan_coc=1 THEN h.tien_coc ELSE 0 END, COALESCE(h.gia_dem,"
                        + " r.gia_dem), h.trang_thai, COALESCE(h.coc_yeu_cau, h.tien_coc)"
                        + " FROM hop_dong_luu_tru h JOIN khach_hang k ON k.ma_khach_hang ="
                        + " h.ma_khach_hang JOIN thu_cung p ON p.ma_thu_cung ="
                        + " h.ma_thu_cung JOIN phong_hotel r ON r.ma_phong = h.ma_phong"
                        + dieuKien
                        + " ORDER BY h.ngay_vao DESC, h.ma_hop_dong DESC");
    }

    private KieuHienThi taoKieuKho(LoaiNghiepVu cheDo) {
        return switch (cheDo) {
            case PHIEU_NHAP_KHO ->
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
                            "SELECT p.ma_phieu_nhap AS ma_khoa, p.ma_phieu_nhap, n.ten,"
                                    + " p.ngay_nhap, p.tong_tien, p.da_tra, CASE WHEN"
                                    + " p.da_tra>=p.tong_tien THEN N'Đã thanh toán' ELSE N'Đã ghi"
                                    + " sổ' END FROM phieu_nhap_kho p JOIN nha_cung_cap n ON"
                                    + " n.ma_nha_cung_cap = p.ma_nha_cung_cap ORDER BY"
                                    + " p.ma_phieu_nhap DESC");
            case PHIEU_XUAT_KHO ->
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
                            "SELECT ma_phieu_xuat AS ma_khoa, ma_phieu_xuat, loai_xuat, ly_do,"
                                    + " tong_gia_tri, trang_thai, tao_luc FROM phieu_xuat_kho"
                                    + " ORDER BY ma_phieu_xuat DESC");
            default ->
                    new KieuHienThi(
                            "Kiểm kê tồn kho",
                            new String[] {
                                "ID", "Số biên bản", "Kỳ kiểm kê", "Trạng thái", "Ngày tạo"
                            },
                            "SELECT ma_bien_ban AS ma_khoa, ma_bien_ban, ky_kiem_ke, CASE"
                                    + " trang_thai WHEN 'DANG_KIEM' THEN N'Đang kiểm kê' WHEN"
                                    + " 'CHO_DUYET' THEN N'Chờ duyệt' WHEN 'DA_DUYET' THEN N'Đã"
                                    + " duyệt' WHEN 'TU_CHOI' THEN N'Từ chối' ELSE trang_thai END,"
                                    + " tao_luc FROM bien_ban_kiem_ke ORDER BY ma_bien_ban DESC");
        };
    }

    private KieuHienThi taoKieuDoiTraHoacTreo(LoaiNghiepVu cheDo) {
        if (cheDo == LoaiNghiepVu.DOI_TRA) {
            return new KieuHienThi(
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
                    "SELECT p.ma_phieu AS ma_khoa, p.ma_phieu, h.ma_hoa_don, p.chenh_lech,"
                            + " p.ly_do, CASE WHEN p.loai_phieu='DOI_HANG' AND"
                            + " p.trang_thai='HOAN_TAT' THEN N'Hoàn tất đổi hàng' WHEN"
                            + " p.loai_phieu='DOI_HANG' AND p.trang_thai='CHO_XU_LY' THEN N'Chờ"
                            + " xử lý đổi hàng' WHEN p.loai_phieu='DOI_HANG' AND"
                            + " p.trang_thai='DA_HUY' THEN N'Đã hủy đổi hàng' WHEN"
                            + " p.loai_phieu='TRA_HANG_CU' THEN N'Lịch sử hoàn tiền' ELSE"
                            + " CONCAT(p.loai_phieu, N': ', p.trang_thai) END, p.tao_luc FROM"
                            + " phieu_doi_tra p JOIN hoa_don h ON h.ma_hoa_don = p.ma_hoa_don"
                            + " ORDER BY p.ma_phieu DESC");
        }
        return new KieuHienThi(
                "Đơn đang treo",
                new String[] {
                    "ID",
                    "Mã đơn",
                    "Khách hàng",
                    "Người lập",
                    "Ngày tạo",
                    "Số dòng",
                    "Tạm tính",
                    "Loại đơn"
                },
                "SELECT g.ma_hoa_don AS ma_khoa, g.ma_hoa_don, COALESCE(k.ho_ten,"
                        + " N'Khách lẻ'), COALESCE(n.ho_ten, N'Thu ngân'), g.tao_luc,"
                        + " (SELECT COUNT(*) FROM chi_tiet_hoa_don c WHERE c.ma_hoa_don ="
                        + " g.ma_hoa_don), g.tam_tinh, CASE WHEN JSON_VALUE(CASE WHEN"
                        + " ISJSON(g.ghi_chu)=1 THEN g.ghi_chu ELSE N'{}' END,"
                        + " '$.loai')='DOI_PHU_KIEN' THEN N'Đổi phụ kiện' ELSE N'Bán hàng'"
                        + " END FROM hoa_don g LEFT JOIN nhan_vien n ON n.ma_nhan_vien ="
                        + " g.ma_nhan_vien LEFT JOIN khach_hang k ON k.ma_khach_hang ="
                        + " g.ma_khach_hang WHERE g.trang_thai = 'GIO_TREO' AND (? = '' OR"
                        + " g.ma_nhan_vien = ?) ORDER BY g.ma_hoa_don DESC");
    }

    @Override
    public List<DongNghiepVu> layCacDong(LoaiNghiepVu cheDo, String maNhanVien) {
        String maNv = maNhanVien == null ? "" : maNhanVien.trim();
        List<Object[]> cacDong =
                cheDo == LoaiNghiepVu.GIO_HANG_TREO
                        ? truyVan(taoKieuHienThi(cheDo).sql, maNv, maNv)
                        : truyVan(taoKieuHienThi(cheDo).sql);
        return cacDong.stream()
                .map(
                        dong -> {
                            String maKhoa = "";
                            if (dong.length > 0 && dong[0] != null) {
                                maKhoa = DuLieu.chuyenThanhChuoi(dong[0]);
                                dong[0] = maKhoa;
                            }
                            return new DongNghiepVu(maKhoa, dong);
                        })
                .toList();
    }
}
