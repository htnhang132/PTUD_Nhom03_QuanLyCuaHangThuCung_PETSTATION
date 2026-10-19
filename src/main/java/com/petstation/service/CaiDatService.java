package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.chuyenThanhChuoi;
import static com.petstation.dao.ThaoTacSQL.thucHienGiaoDich;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.service.PhienDangNhapService.layIdTaiKhoan;
import static com.petstation.service.PhienDangNhapService.yeuCau;

import com.petstation.dao.impl.JdbcCaiDatDAO;
import com.petstation.model.Quyen;

import java.util.Arrays;

/** Đọc và cập nhật các tham số vận hành đang lưu trong bảng cai_dat. */
public final class CaiDatService {
    /** Khởi tạo CaiDatService với các giá trị được cung cấp. */
    private CaiDatService() {}

    /**
     * Lấy trong CaiDatService.
     *
     * @param khoa khóa
     * @return kết quả lấy
     */
    public static String layTuyChon(String khoa) {
        Object giaTri = JdbcCaiDatDAO.timGiaTri(khoa);
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
        // Giá trị.
        Object giaTri = JdbcCaiDatDAO.timGiaTri(khoa);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (giaTri == null)
            throw new IllegalStateException(
                    "Thiếu cài đặt " + khoa + ". Chạy database/002_sample_data.sql.");
        return chuyenThanhChuoi(giaTri);
    }

    /**
     * Ghi trong CaiDatService.
     *
     * @param khoa khóa
     * @param giaTri giá trị
     */
    public static void ghi(String khoa, String giaTri) {
        yeuCau(Quyen.ACCOUNT_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (JdbcCaiDatDAO.capNhatGiaTri(giaTri, layIdTaiKhoan(), khoa) == 0)
                        JdbcCaiDatDAO.themGiaTri(khoa, giaTri, layIdTaiKhoan());
                    return null;
                });
    }

    /**
     * Lấy số nguyên trong CaiDatService.
     *
     * @param khoaDangXet khóa đang xét
     * @return kết quả lấy số nguyên
     */
    private static int laySoNguyen(String khoaDangXet) {
        return Integer.parseInt(lay(khoaDangXet));
    }

    /**
     * Đọc số tiền trong CaiDatService.
     *
     * @param khoaDangXet khóa đang xét
     * @return kết quả đọc số tiền
     */
    private static long docSoTien(String khoaDangXet) {
        return Long.parseLong(lay(khoaDangXet));
    }

    /**
     * Lấy số tiền cho mỗi điểm tích lũy trong CaiDatService.
     *
     * @return kết quả lấy số tiền cho mỗi điểm tích lũy
     */
    public static long laySoTienChoMoiDiemTichLuy() {
        return TichDiemService.SO_TIEN_MOI_DIEM;
    }

    /**
     * Lấy giá trị quy đổi mỗi điểm trong CaiDatService.
     *
     * @return kết quả lấy giá trị quy đổi mỗi điểm
     */
    public static long layGiaTriQuyDoiMoiDiem() {
        return 0;
    }

    /**
     * Lấy hạng chi tiêu trong CaiDatService.
     *
     * @return kết quả lấy hạng chi tiêu
     */
    public static long[] layHangChiTieu() {
        return Arrays.stream(lay("TIER_SPEND").split(",")).mapToLong(Long::parseLong).toArray();
    }

    /**
     * Lấy VAT phần trăm trong CaiDatService.
     *
     * @return kết quả lấy VAT phần trăm
     */
    public static int layVATPhanTram() {
        return laySoNguyen("VAT_PERCENT");
    }

    /**
     * Lấy ngưỡng thấp tồn kho trong CaiDatService.
     *
     * @return kết quả lấy ngưỡng thấp tồn kho
     */
    public static int layNguongThapTonKho() {
        return laySoNguyen("LOW_STOCK_THRESHOLD");
    }

    /**
     * Lấy hạn sử dụng cảnh báo số ngày trong CaiDatService.
     *
     * @return kết quả lấy hạn sử dụng cảnh báo số ngày
     */
    public static int layHanSuDungCanhBaoSoNgay() {
        return laySoNguyen("EXPIRY_WARN_DAYS");
    }

    /**
     * Lấy phí trả phòng trễ mỗi ngày trong CaiDatService.
     *
     * @return kết quả lấy phí trả phòng trễ mỗi ngày
     */
    public static long layPhiTraPhongTreMoiNgay() {
        return docSoTien("LATE_CHECKOUT_FEE");
    }

    /**
     * Lấy số ngày được đổi trả trong CaiDatService.
     *
     * @return kết quả lấy số ngày được đổi trả
     */
    public static int laySoNgayDuocDoiTra() {
        return 3;
    }

    /**
     * Ghi giới hạn hủy phê duyệt trong CaiDatService.
     *
     * @return kết quả ghi giới hạn hủy phê duyệt
     */
    public static long ghiGioiHanHuyPheDuyet() {
        return docSoTien("WRITEOFF_APPROVAL_LIMIT");
    }

    /**
     * Cập nhật tích lũy trong CaiDatService.
     *
     * @param tichDiem tích điểm
     * @param doiDiem đổi điểm
     * @param cacHang các hạng
     */
    public static void capNhatTichLuy(long tichDiem, long doiDiem, long[] cacHang) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (tichDiem != 10000 || doiDiem != 0 || cacHang.length != 4)
            throw new IllegalArgumentException("Quy tắc điểm không hợp lệ.");
        for (int chiSo = 0; chiSo < cacHang.length; chiSo++)
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
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

    /**
     * Cập nhật trong CaiDatService.
     *
     * @param vat VAT
     * @param thap thấp
     * @param soNgay số ngày
     * @param tre trễ
     * @param doiTra đổi trả
     * @param gioiHan giới hạn
     */
    public static void capNhat(int vat, int thap, int soNgay, long tre, int doiTra, long gioiHan) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
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
