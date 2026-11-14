package com.petstation.service.hethong;

import com.petstation.dao.hethong.CaiDatDAO;
import com.petstation.dao.hethong.impl.CaiDatDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.hethong.CaiDat;
import com.petstation.util.TaoMaVietQR;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class CaiDatService {
    private static final CaiDatService MAC_DINH = new CaiDatService();

    private final CaiDatDAO caiDatDAO;
    private final NhatKyHeThongService nhatKyService;
    private final PhienDangNhapService phienService;

    public CaiDatService() {
        this(new CaiDatDAOImpl(), NhatKyHeThongService.macDinh(), PhienDangNhapService.macDinh());
    }

    public CaiDatService(CaiDatDAO caiDatDAO) {
        this(caiDatDAO, NhatKyHeThongService.macDinh(), PhienDangNhapService.macDinh());
    }

    public CaiDatService(
            CaiDatDAO caiDatDAO,
            NhatKyHeThongService nhatKyService,
            PhienDangNhapService phienService) {
        this.caiDatDAO = Objects.requireNonNull(caiDatDAO);
        this.nhatKyService = Objects.requireNonNull(nhatKyService);
        this.phienService = Objects.requireNonNull(phienService);
    }

    public static CaiDatService macDinh() {
        return MAC_DINH;
    }

    private String chuanHoaKhoa(String khoa) {
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

    public void luuTatCa(Map<String, String> giaTri, String mayIn) {
        phienService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        Map<String, String> chuanHoa = new LinkedHashMap<>();
        for (var muc : giaTri.entrySet()) {
            chuanHoa.put(chuanHoaKhoa(muc.getKey()), muc.getValue());
        }
        if (chuanHoa.getOrDefault("TEN_CUA_HANG", "").isBlank())
            throw new IllegalArgumentException("Cần tên cửa hàng.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
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
                                            .toUpperCase(Locale.ROOT));
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
                            nhatKyService.ghiNhatKy("Sửa", "Cài đặt hệ thống");
                            return null;
                        });
    }

    public String layTuyChonHoacMacDinh(String khoa, String macDinh) {
        String giaTri = caiDatDAO.timGiaTri(chuanHoaKhoa(khoa));
        if (giaTri == null || giaTri.isBlank()) {
            return macDinh;
        }
        return giaTri;
    }

    public String layTuyChon(String khoa) {
        String giaTri = caiDatDAO.timGiaTri(chuanHoaKhoa(khoa));
        return giaTri == null ? "" : giaTri;
    }

    public void capNhatNganHang(String bin, String taiKhoan, String chuTaiKhoan) {
        phienService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        if (!(bin.isBlank() && taiKhoan.isBlank() && chuTaiKhoan.isBlank()))
            TaoMaVietQR.kiemTraTaiKhoan(bin, taiKhoan, chuTaiKhoan);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            ghi("MA_BIN_NGAN_HANG", bin);
                            ghi("SO_TAI_KHOAN_NGAN_HANG", taiKhoan);
                            ghi("TEN_CHU_TAI_KHOAN", chuTaiKhoan);
                            return null;
                        });
    }

    public String lay(String khoa) {
        String khoaChuan = chuanHoaKhoa(khoa);
        String giaTri = caiDatDAO.timGiaTri(khoaChuan);
        if (giaTri == null) {
            String macDinh =
                    switch (khoaChuan) {
                        case "NGUONG_TON_KHO_THAP" -> "10";
                        case "SO_NGAY_CANH_BAO_HET_HAN" -> "30";
                        case "PHI_TRA_PHONG_TRE" -> "100000";
                        case "MAY_IN" -> "";
                        case "THUE_VAT_PHAN_TRAM" -> "8";
                        case "HAN_MUC_XUAT_HUY_CAN_DUYET" -> "500000";
                        case "SO_NGAY_DOI_TRA" -> "3";
                        case "SO_TIEN_MOI_DIEM_TICH" -> "10000";
                        default -> null;
                    };
            if (macDinh != null) return macDinh;
            throw new IllegalArgumentException(
                    "Thiếu cài đặt " + khoaChuan + ". Chạy database/002_sample_data.sql.");
        }
        return giaTri;
    }

    public void ghi(String khoa, String giaTri) {
        phienService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String khoaChuan = chuanHoaKhoa(khoa);
        String nguoiCapNhat =
                phienService.layPhienHienTai() == null ? null : phienService.layMaNhanVien();
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (caiDatDAO.capNhatGiaTri(giaTri, nguoiCapNhat, khoaChuan) == 0)
                                caiDatDAO.themGiaTri(khoaChuan, giaTri, nguoiCapNhat);
                            return null;
                        });
    }

    private int laySoNguyen(String khoaDangXet) {
        String giaTri = lay(khoaDangXet).trim();
        try {
            return Integer.parseInt(giaTri);
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException("Cài đặt " + khoaDangXet + " không hợp lệ.");
        }
    }

    private long docSoTien(String khoaDangXet) {
        String giaTri = lay(khoaDangXet).trim();
        try {
            return Long.parseLong(giaTri);
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException("Cài đặt " + khoaDangXet + " không hợp lệ.");
        }
    }

    public long laySoTienChoMoiDiemTichLuy() {
        String giaTri = layTuyChonHoacMacDinh("SO_TIEN_MOI_DIEM_TICH", "10000").trim();
        try {
            long soTien = Long.parseLong(giaTri);
            if (soTien <= 0)
                throw new IllegalArgumentException("Cài đặt SO_TIEN_MOI_DIEM_TICH phải lớn hơn 0.");
            return soTien;
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException(
                    "Cài đặt SO_TIEN_MOI_DIEM_TICH không hợp lệ: " + giaTri);
        }
    }

    public long[] layHangChiTieu() {
        try {
            return Arrays.stream(lay("MUC_CHI_TIEU_HANG").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToLong(Long::parseLong)
                    .toArray();
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException("Cài đặt MUC_CHI_TIEU_HANG không hợp lệ.");
        }
    }

    public int layVATPhanTram() {
        return laySoNguyen("THUE_VAT_PHAN_TRAM");
    }

    public int layHanSuDungCanhBaoSoNgay() {
        return laySoNguyen("SO_NGAY_CANH_BAO_HET_HAN");
    }

    public long layPhiTraPhongTreMoiNgay() {
        return docSoTien("PHI_TRA_PHONG_TRE");
    }

    public int laySoNgayDuocDoiTra() {
        String giaTri = layTuyChonHoacMacDinh("SO_NGAY_DOI_TRA", "3").trim();
        try {
            return Integer.parseInt(giaTri);
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException("Cài đặt SO_NGAY_DOI_TRA không hợp lệ.");
        }
    }

    public long ghiGioiHanHuyPheDuyet() {
        return docSoTien("HAN_MUC_XUAT_HUY_CAN_DUYET");
    }

    public void capNhatTichLuy(long tichDiem, long doiDiem, long[] cacHang) {
        phienService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        if (tichDiem <= 0 || doiDiem < 0 || cacHang == null || cacHang.length != 4)
            throw new IllegalArgumentException("Quy tắc điểm không hợp lệ.");
        for (int chiSo = 0; chiSo < cacHang.length; chiSo++)
            if (cacHang[chiSo] < 0 || chiSo > 0 && cacHang[chiSo] <= cacHang[chiSo - 1])
                throw new IllegalArgumentException("Ngưỡng hạng phải tăng dần.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            ghi("SO_TIEN_MOI_DIEM_TICH", "" + tichDiem);
                            ghi("SO_TIEN_MOI_DIEM_DOI", "" + doiDiem);
                            ghi(
                                    "MUC_CHI_TIEU_HANG",
                                    String.join(
                                            ",",
                                            Arrays.stream(cacHang)
                                                    .mapToObj(Long::toString)
                                                    .toList()));
                            return null;
                        });
    }

    public void capNhat(int vat, int thap, int soNgay, long tre, int doiTra, long gioiHan) {
        if (vat < 0 || vat > 100 || thap < 0 || soNgay < 0 || tre < 0 || doiTra < 0 || gioiHan < 0)
            throw new IllegalArgumentException("Cài đặt không hợp lệ.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
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

    public Optional<CaiDat> timCaiDat(String maCaiDat) {
        return caiDatDAO.timCaiDat(chuanHoaKhoa(maCaiDat));
    }
}
