package com.petstation.model.chung;

public enum TrangThaiTaiKhoan {
    HOAT_DONG,
    TAM_KHOA,
    NGUNG_HOAT_DONG;

    public String maDB() {
        return name();
    }

    public static TrangThaiTaiKhoan docTuCSDL(String giaTri) {
        if (giaTri == null) return HOAT_DONG;
        return switch (giaTri.trim().toUpperCase()) {
            case "HOAT_DONG" -> HOAT_DONG;
            case "TAM_KHOA" -> TAM_KHOA;
            case "NGUNG_HOAT_DONG" -> NGUNG_HOAT_DONG;
            default -> TrangThaiTaiKhoan.valueOf(giaTri.trim());
        };
    }
}
