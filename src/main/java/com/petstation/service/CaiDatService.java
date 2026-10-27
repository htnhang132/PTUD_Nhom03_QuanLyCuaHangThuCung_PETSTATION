package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.thucHienGiaoDich;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.service.PhienDangNhapService.layIdTaiKhoan;
import static com.petstation.service.PhienDangNhapService.yeuCau;
import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dao.CaiDatDAO;
import com.petstation.dao.impl.CaiDatImpl;
import com.petstation.model.Quyen;

import java.util.Arrays;

/** Đọc và cập nhật các tham số vận hành đang lưu trong bảng cai_dat. */
public final class CaiDatService {
    private static final CaiDatDAO caiDatDAO = new CaiDatImpl();

    /** Lưu toàn bộ biểu mẫu trong một transaction, tránh lưu dở tham số/ngân hàng/máy in. */
    public static void luuTatCa(java.util.Map<String, String> giaTri, String mayIn) {
        yeuCau(Quyen.ACCOUNT_MANAGE);
        if (giaTri.getOrDefault("STORE_NAME", "").isBlank())
            throw new IllegalArgumentException("Cần tên cửa hàng.");
        thucHienGiaoDich(
                c -> {
                    capNhat(
                            Integer.parseInt(giaTri.get("VAT_PERCENT")),
                            Integer.parseInt(giaTri.get("LOW_STOCK_THRESHOLD")),
                            Integer.parseInt(giaTri.get("EXPIRY_WARN_DAYS")),
                            Long.parseLong(giaTri.get("LATE_CHECKOUT_FEE")),
                            Integer.parseInt(giaTri.get("RETURN_WINDOW_DAYS")),
                            Long.parseLong(giaTri.get("WRITEOFF_APPROVAL_LIMIT")));
                    capNhatNganHang(
                            giaTri.get("BANK_BIN").trim(),
                            giaTri.get("BANK_ACCOUNT").trim(),
                            giaTri.get("BANK_ACCOUNT_NAME")
                                    .trim()
                                    .toUpperCase(java.util.Locale.ROOT));
                    for (var muc : giaTri.entrySet()) {
                        if (muc.getKey().startsWith("STORE_")
                                || muc.getKey().equals("CARE_DATA_DIR"))
                            ghi(muc.getKey(), muc.getValue().trim());
                    }
                    ghi("PRINTER", mayIn);
                    NhatKyHeThongService.ghiNhatKy("Sửa", "Cài đặt hệ thống");
                    return null;
                });
    }

    private CaiDatService() {}

    public static String layTuyChon(String khoa) {
        Object giaTri = caiDatDAO.timGiaTri(khoa);
        return giaTri == null ? "" : chuyenThanhChuoi(giaTri);
    }

    public static void capNhatNganHang(String bin, String taiKhoan, String chuTaiKhoan) {
        yeuCau(Quyen.ACCOUNT_MANAGE);
        if (!(bin.isBlank() && taiKhoan.isBlank() && chuTaiKhoan.isBlank()))
            com.petstation.util.TaoMaVietQR.kiemTraTaiKhoan(bin, taiKhoan, chuTaiKhoan);
        thucHienGiaoDich(
                c -> {
                    ghi("BANK_BIN", bin);
                    ghi("BANK_ACCOUNT", taiKhoan);
                    ghi("BANK_ACCOUNT_NAME", chuTaiKhoan);
                    return null;
                });
    }

    public static String lay(String khoa) {
        Object giaTri = caiDatDAO.timGiaTri(khoa);
        if (giaTri == null)
            throw new IllegalStateException(
                    "Thiếu cài đặt " + khoa + ". Chạy database/002_sample_data.sql.");
        return chuyenThanhChuoi(giaTri);
    }

    public static void ghi(String khoa, String giaTri) {
        yeuCau(Quyen.ACCOUNT_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    if (caiDatDAO.capNhatGiaTri(giaTri, layIdTaiKhoan(), khoa) == 0)
                        caiDatDAO.themGiaTri(khoa, giaTri, layIdTaiKhoan());
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
        return Arrays.stream(lay("TIER_SPEND").split(",")).mapToLong(Long::parseLong).toArray();
    }

    public static int layVATPhanTram() {
        return laySoNguyen("VAT_PERCENT");
    }

    public static int layNguongThapTonKho() {
        return laySoNguyen("LOW_STOCK_THRESHOLD");
    }

    public static int layHanSuDungCanhBaoSoNgay() {
        return laySoNguyen("EXPIRY_WARN_DAYS");
    }

    public static long layPhiTraPhongTreMoiNgay() {
        return docSoTien("LATE_CHECKOUT_FEE");
    }

    public static int laySoNgayDuocDoiTra() {
        return 3;
    }

    public static long ghiGioiHanHuyPheDuyet() {
        return docSoTien("WRITEOFF_APPROVAL_LIMIT");
    }

    public static void capNhatTichLuy(long tichDiem, long doiDiem, long[] cacHang) {
        if (tichDiem != 10000 || doiDiem != 0 || cacHang.length != 4)
            throw new IllegalArgumentException("Quy tắc điểm không hợp lệ.");
        for (int chiSo = 0; chiSo < cacHang.length; chiSo++)
            if (cacHang[chiSo] < 0 || chiSo > 0 && cacHang[chiSo] <= cacHang[chiSo - 1])
                throw new IllegalArgumentException("Ngưỡng hạng phải tăng dần.");
        thucHienGiaoDich(
                ketNoi -> {
                    ghi("VND_PER_EARNED_POINT", "" + tichDiem);
                    ghi("VND_PER_REDEEMED_POINT", "" + doiDiem);
                    ghi(
                            "TIER_SPEND",
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
                    ghi("VAT_PERCENT", "" + vat);
                    ghi("LOW_STOCK_THRESHOLD", "" + thap);
                    ghi("EXPIRY_WARN_DAYS", "" + soNgay);
                    ghi("LATE_CHECKOUT_FEE", "" + tre);
                    ghi("RETURN_WINDOW_DAYS", "" + doiTra);
                    ghi("WRITEOFF_APPROVAL_LIMIT", "" + gioiHan);
                    return null;
                });
    }
}
