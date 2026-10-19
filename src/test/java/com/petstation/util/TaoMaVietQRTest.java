package com.petstation.util;

import static org.assertj.core.api.Assertions.*;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;

import org.junit.jupiter.api.Test;

/** Đối chiếu mẫu công bố bởi VietQR và giải mã ảnh được tạo cục bộ. */
class TaoMaVietQRTest {
    @Test
    void khopMauVietQRChinhThuc() {
        assertThat(TaoMaVietQR.taoNoiDung("970415", "113366668888", 79000, "Ung Ho Quy Vac Xin"))
                .isEqualTo(
                        "00020101021238560010A0000007270126000697041501121133666688880208QRIBFTTA53037045405790005802VN62220818Ung"
                            + " Ho Quy Vac Xin63043ACF");
    }

    @Test
    void giaiMaDuocAnhVaThaySoTienSauGiamGia() throws Exception {
        String truoc = TaoMaVietQR.taoNoiDung("970436", "1234567890", 250000, "PAY123");
        String sau = TaoMaVietQR.taoNoiDung("970436", "1234567890", 100000, "PAY123");
        assertThat(sau).isNotEqualTo(truoc).contains("5406100000");
        var anh = TaoMaVietQR.taoAnh(sau, 300);
        var nguon =
                new RGBLuminanceSource(
                        anh.getWidth(),
                        anh.getHeight(),
                        anh.getRGB(0, 0, anh.getWidth(), anh.getHeight(), null, 0, anh.getWidth()));
        assertThat(
                        new MultiFormatReader()
                                .decode(new BinaryBitmap(new HybridBinarizer(nguon)))
                                .getText())
                .isEqualTo(sau);
    }

    @Test
    void chanTaiKhoanSoTienVaNoiDungSai() {
        assertThatThrownBy(() -> TaoMaVietQR.kiemTraTaiKhoan("VCB", "123456", "SHOP"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TaoMaVietQR.taoNoiDung("970436", "123456", 0, "HD1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TaoMaVietQR.taoNoiDung("970436", "123456", 10000, "Hóa đơn"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
