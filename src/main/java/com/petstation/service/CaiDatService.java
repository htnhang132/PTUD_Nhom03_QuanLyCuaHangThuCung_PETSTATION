package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.thucHienGiaoDich;
import static com.petstation.service.PhienDangNhapService.layIdTaiKhoan;
import static com.petstation.service.PhienDangNhapService.yeuCau;
import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dao.CaiDatDAO;
import com.petstation.dao.impl.CaiDatDAOImpl;
import com.petstation.model.Quyen;

import java.util.Arrays;

/** Đọc và cập nhật các tham số vận hành đang lưu trong bảng cai_dat. */
public final class CaiDatService {
    private static final CaiDatDAO caiDatDAO = new CaiDatDAOImpl();

    private static String chuanHoaKhoa(String khoa) {
        if (khoa == null) return "";
        return switch (khoa.trim()) {
            case "STORE_NAME" -> "TEN_CUA_HANG";
            case "STORE_ADDRESS" -> "DIA_CHI_CUA_HANG";
            case "STORE_PHONE" -> "SO_DIEN_THOAI_CUA_HANG";
            case "STORE_EMAIL" -> "EMAIL_CUA_HANG";
            case "STORE_WEBSITE" -> "WEBSITE_CUA_HANG";
            case "STORE_TAX_CODE" -> "MA_SO_THUE";
            case "VAT_PERCENT" -> "THUE_VAT_PHAN_TRAM";
            case "PRINTER" -> "MAY_IN";
            case "BANK_BIN" -> "MA_BIN_NGAN_HANG";
            case "BANK_ACCOUNT" -> "SO_TAI_KHOAN_NGAN_HANG";
            case "BANK_ACCOUNT_NAME" -> "TEN_CHU_TAI_KHOAN";
            case "LOW_STOCK_THRESHOLD" -> "NGUONG_TON_KHO_THAP";
            case "EXPIRY_WARN_DAYS" -> "SO_NGAY_CANH_BAO_HET_HAN";
            case "DEAD_STOCK_DAYS" -> "SO_NGAY_TON_DONG";
            case "WRITEOFF_APPROVAL_LIMIT" -> "HAN_MUC_XUAT_HUY_CAN_DUYET";
            case "BLIND_COUNT_DEFAULT" -> "MAC_DINH_KIEM_KE_MU";
            case "MAX_WEEKLY_HOURS" -> "SO_GIO_TOI_DA_MOI_TUAN";
            case "SHIFT_SWAP_HOURS_AHEAD" -> "SO_GIO_BAO_TRUOC_DOI_CA";
            case "LATE_CHECKOUT_FEE" -> "PHI_TRA_PHONG_TRE";
            case "RETURN_WINDOW_DAYS" -> "SO_NGAY_DOI_TRA";
            case "CARE_DATA_DIR" -> "THU_MUC_ANH_CHAM_SOC";
            case "TIER_SPEND" -> "MUC_CHI_TIEU_HANG";
            case "VND_PER_EARNED_POINT" -> "SO_TIEN_MOI_DIEM_TICH";
            case "VND_PER_REDEEMED_POINT" -> "SO_TIEN_MOI_DIEM_DOI";
            default -> khoa.trim();
        };
    }

    /** Lưu toàn bộ biểu mẫu trong một transaction, tránh lưu dở tham số/ngân hàng/máy in. */
    public static void luuTatCa(java.util.Map<String, String> giaTri, String mayIn) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        java.util.Map<String, String> chuanHoa = new java.util.LinkedHashMap<>();
        for (var muc : giaTri.entrySet()) {
            chuanHoa.put(chuanHoaKhoa(muc.getKey()), muc.getValue());
        }
        if (chuanHoa.getOrDefault("TEN_CUA_HANG", "").isBlank())
            throw new IllegalArgumentException("Cần tên cửa hàng.");
        thucHienGiaoDich(
                c -> {
                    capNhat(
                            Integer.parseInt(chuanHoa.get("THUE_VAT_PHAN_TRAM")),
                            Integer.parseInt(chuanHoa.get("NGUONG_TON_KHO_THAP")),
                            Integer.parseInt(chuanHoa.get("SO_NGAY_CANH_BAO_HET_HAN")),
                            Long.parseLong(chuanHoa.get("PHI_TRA_PHONG_TRE")),
                            Integer.parseInt(chuanHoa.get("SO_NGAY_DOI_TRA")),
                            Long.parseLong(chuanHoa.get("HAN_MUC_XUAT_HUY_CAN_DUYET")));
                    capNhatNganHang(
                            chuanHoa.getOrDefault("MA_BIN_NGAN_HANG", "").trim(),
                            chuanHoa.getOrDefault("SO_TAI_KHOAN_NGAN_HANG", "").trim(),
                            chuanHoa.getOrDefault("TEN_CHU_TAI_KHOAN", "")
                                    .trim()
                                    .toUpperCase(java.util.Locale.ROOT));
                    for (var muc : chuanHoa.entrySet()) {
                        String k = muc.getKey();
                        if (k.endsWith("_CUA_HANG")
                                || k.equals("MA_SO_THUE")
                                || k.equals("THU_MUC_ANH_CHAM_SOC")
                                || k.equals("SO_NGAY_TON_DONG")
                                || k.equals("MAC_DINH_KIEM_KE_MU")
                                || k.equals("SO_GIO_TOI_DA_MOI_TUAN")
                                || k.equals("SO_GIO_BAO_TRUOC_DOI_CA")) {
                            ghi(k, muc.getValue().trim());
                        }
                    }
                    ghi("MAY_IN", mayIn);
                    NhatKyHeThongService.ghiNhatKy("Sửa", "Cài đặt hệ thống");
                    thongBaoThayDoi("ALL");
                    return null;
                });
    }

    private CaiDatService() {}

    private static final java.util.List<java.util.function.Consumer<String>> boLangNgheCaiDat =
            new java.util.concurrent.CopyOnWriteArrayList<>();

    public static void dangKyLangNghe(java.util.function.Consumer<String> langNghe) {
        if (langNghe != null && !boLangNgheCaiDat.contains(langNghe)) {
            boLangNgheCaiDat.add(langNghe);
        }
    }

    public static void xoaBoLangNghe() {
        boLangNgheCaiDat.clear();
    }

    public static void thongBaoThayDoi(String khoa) {
        for (var ln : boLangNgheCaiDat) {
            try {
                ln.accept(khoa);
            } catch (Exception ignored) {
            }
        }
    }

    public static String layTuyChonHoacMacDinh(String khoa, String macDinh) {
        Object giaTri = caiDatDAO.timGiaTri(chuanHoaKhoa(khoa));
        if (giaTri == null || giaTri.toString().isBlank()) {
            return macDinh;
        }
        return chuyenThanhChuoi(giaTri);
    }

    public static String layTuyChon(String khoa) {
        Object giaTri = caiDatDAO.timGiaTri(chuanHoaKhoa(khoa));
        return giaTri == null ? "" : chuyenThanhChuoi(giaTri);
    }

    public static void capNhatNganHang(String bin, String taiKhoan, String chuTaiKhoan) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        if (!(bin.isBlank() && taiKhoan.isBlank() && chuTaiKhoan.isBlank()))
            com.petstation.util.TaoMaVietQR.kiemTraTaiKhoan(bin, taiKhoan, chuTaiKhoan);
        thucHienGiaoDich(
                c -> {
                    ghi("MA_BIN_NGAN_HANG", bin);
                    ghi("SO_TAI_KHOAN_NGAN_HANG", taiKhoan);
                    ghi("TEN_CHU_TAI_KHOAN", chuTaiKhoan);
                    return null;
                });
    }

    public static String lay(String khoa) {
        String khoaChuan = chuanHoaKhoa(khoa);
        Object giaTri = caiDatDAO.timGiaTri(khoaChuan);
        if (giaTri == null)
            throw new IllegalStateException(
                    "Thiếu cài đặt " + khoaChuan + ". Chạy database/002_sample_data.sql.");
        return chuyenThanhChuoi(giaTri);
    }

    public static void ghi(String khoa, String giaTri) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String khoaChuan = chuanHoaKhoa(khoa);
        thucHienGiaoDich(
                ketNoi -> {
                    if (caiDatDAO.capNhatGiaTri(giaTri, layIdTaiKhoan(), khoaChuan) == 0)
                        caiDatDAO.themGiaTri(khoaChuan, giaTri, layIdTaiKhoan());
                    return null;
                });
    }

    private static int laySoNguyen(String khoaDangXet) {
        return Integer.parseInt(lay(khoaDangXet));
    }

    private static long docSoTien(String khoaDangXet) {
        return Long.parseLong(lay(khoaDangXet));
    }

    public static long laySoTienChoMoiDiemTichLuy() {
        return TichDiemService.SO_TIEN_MOI_DIEM;
    }

    public static long layGiaTriQuyDoiMoiDiem() {
        return 0;
    }

    public static long[] layHangChiTieu() {
        return Arrays.stream(lay("MUC_CHI_TIEU_HANG").split(",")).mapToLong(Long::parseLong).toArray();
    }

    public static int layVATPhanTram() {
        return laySoNguyen("THUE_VAT_PHAN_TRAM");
    }

    public static int layNguongThapTonKho() {
        return laySoNguyen("NGUONG_TON_KHO_THAP");
    }

    public static int layHanSuDungCanhBaoSoNgay() {
        return laySoNguyen("SO_NGAY_CANH_BAO_HET_HAN");
    }

    public static long layPhiTraPhongTreMoiNgay() {
        return docSoTien("PHI_TRA_PHONG_TRE");
    }

    public static int laySoNgayDuocDoiTra() {
        return Integer.parseInt(layTuyChonHoacMacDinh("SO_NGAY_DOI_TRA", "3"));
    }

    public static long ghiGioiHanHuyPheDuyet() {
        return docSoTien("HAN_MUC_XUAT_HUY_CAN_DUYET");
    }

    public static void capNhatTichLuy(long tichDiem, long doiDiem, long[] cacHang) {
        if (tichDiem != 10000 || doiDiem != 0 || cacHang.length != 4)
            throw new IllegalArgumentException("Quy tắc điểm không hợp lệ.");
        for (int chiSo = 0; chiSo < cacHang.length; chiSo++)
            if (cacHang[chiSo] < 0 || chiSo > 0 && cacHang[chiSo] <= cacHang[chiSo - 1])
                throw new IllegalArgumentException("Ngưỡng hạng phải tăng dần.");
        thucHienGiaoDich(
                ketNoi -> {
                    ghi("SO_TIEN_MOI_DIEM_TICH", "" + tichDiem);
                    ghi("SO_TIEN_MOI_DIEM_DOI", "" + doiDiem);
                    ghi(
                            "MUC_CHI_TIEU_HANG",
                            String.join(
                                    ",", Arrays.stream(cacHang).mapToObj(Long::toString).toList()));
                    return null;
                });
    }

    public static void capNhat(int vat, int thap, int soNgay, long tre, int doiTra, long gioiHan) {
        if (vat < 0 || vat > 100 || thap < 0 || soNgay < 0 || tre < 0 || doiTra < 0 || gioiHan < 0)
            throw new IllegalArgumentException("Cài đặt không hợp lệ.");
        thucHienGiaoDich(
                ketNoi -> {
                    ghi("THUE_VAT_PHAN_TRAM", "" + vat);
                    ghi("NGUONG_TON_KHO_THAP", "" + thap);
                    ghi("SO_NGAY_CANH_BAO_HET_HAN", "" + soNgay);
                    ghi("PHI_TRA_PHONG_TRE", "" + tre);
                    ghi("SO_NGAY_DOI_TRA", "" + doiTra);
                    ghi("HAN_MUC_XUAT_HUY_CAN_DUYET", "" + gioiHan);
                    return null;
                });
    }
}
