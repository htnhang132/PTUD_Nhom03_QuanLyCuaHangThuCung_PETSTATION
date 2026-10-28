package com.petstation.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Tạo VietQR chuyển khoản cục bộ theo cấu trúc TLV/CRC của NAPAS. Tham chiếu và bộ mẫu:
 * https://www.vietqr.io/danh-sach-api/link-tao-ma-nhanh/api-tao-ma-qr
 */
public final class TaoMaVietQR {
    private TaoMaVietQR() {}

    private static String truong(String ma, String giaTri) {
        int doDai = giaTri.getBytes(StandardCharsets.UTF_8).length;
        if (doDai > 99) throw new IllegalArgumentException("Trường QR quá dài.");
        return ma + String.format(java.util.Locale.ROOT, "%02d", doDai) + giaTri;
    }

    public static void kiemTraTaiKhoan(String bin, String taiKhoan, String chuTaiKhoan) {
        if (bin == null
                || !bin.matches("[0-9]{6}")
                || taiKhoan == null
                || !taiKhoan.matches("[0-9]{6,19}")
                || chuTaiKhoan == null
                || !chuTaiKhoan.matches("[A-Z ]{5,50}"))
            throw new IllegalArgumentException(
                    "Cần BIN ngân hàng 6 số, tài khoản 6–19 số, tên chủ tài khoản viết hoa không"
                            + " dấu (5–50 ký tự).");
    }

    public static String taoNoiDung(String bin, String taiKhoan, long soTien, String thamChieu) {
        kiemTraTaiKhoan(bin, taiKhoan, "CHU TAI KHOAN");
        if (soTien <= 0 || soTien > 9_999_999_999_999L)
            throw new IllegalArgumentException("Số tiền QR phải dương, tối đa 13 chữ số.");
        if (thamChieu == null || !thamChieu.matches("[A-Za-z0-9 ]{1,25}"))
            throw new IllegalArgumentException(
                    "Nội dung chuyển khoản tối đa 25 ký tự chữ/số không dấu.");
        String nguoiNhan = truong("00", bin) + truong("01", taiKhoan);
        String napas =
                truong("00", "A000000727") + truong("01", nguoiNhan) + truong("02", "QRIBFTTA");
        String noiDung =
                truong("00", "01")
                        + truong("01", "12")
                        + truong("38", napas)
                        + truong("53", "704")
                        + truong("54", Long.toString(soTien))
                        + truong("58", "VN")
                        + truong("62", truong("08", thamChieu))
                        + "6304";
        return noiDung + String.format(java.util.Locale.ROOT, "%04X", tinhCRC(noiDung));
    }

    public static int tinhCRC(String noiDung) {
        int crc = 0xFFFF;
        for (byte giaTri : noiDung.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (giaTri & 0xFF) << 8;
            for (int bit = 0; bit < 8; bit++)
                crc = (crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1;
            crc &= 0xFFFF;
        }
        return crc;
    }

    public static BufferedImage taoAnh(String noiDung, int kichThuoc) {
        try {
            var ma =
                    new QRCodeWriter()
                            .encode(
                                    noiDung,
                                    BarcodeFormat.QR_CODE,
                                    kichThuoc,
                                    kichThuoc,
                                    Map.of(
                                            EncodeHintType.ERROR_CORRECTION,
                                            ErrorCorrectionLevel.Q,
                                            EncodeHintType.MARGIN,
                                            4));
            BufferedImage anh =
                    new BufferedImage(ma.getWidth(), ma.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < ma.getWidth(); x++)
                for (int y = 0; y < ma.getHeight(); y++)
                    anh.setRGB(x, y, ma.get(x, y) ? 0x000000 : 0xFFFFFF);
            return anh;
        } catch (WriterException loi) {
            throw new IllegalArgumentException("Không tạo được QR.", loi);
        }
    }
}
