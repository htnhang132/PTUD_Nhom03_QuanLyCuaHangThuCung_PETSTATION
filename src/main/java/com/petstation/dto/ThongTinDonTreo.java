package com.petstation.dto;

/** Phần bổ sung được lưu bền cùng đơn nháp, độc lập vòng đời ứng dụng. */
public final class ThongTinDonTreo {
    private final String maVoucher;
    private final String ghiChu;

    public ThongTinDonTreo(String maVoucher, String ghiChu) {
        this.maVoucher = maVoucher == null ? "" : maVoucher.trim();
        this.ghiChu = ghiChu == null ? "" : ghiChu;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public String getGhiChu() {
        return ghiChu;
    }
}
