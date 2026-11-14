package com.petstation.model.chung;

public enum PhuongThuc {
    TIEN_MAT,
    CHUYEN_KHOAN,
    THE_NGAN_HANG,
    DIEM_TICH_LUY;

    public static PhuongThuc docTuCSDL(String giaTri) {
        if (giaTri == null || giaTri.isBlank()) return TIEN_MAT;
        return switch (giaTri.trim().toUpperCase()) {
            case "TIEN_MAT" -> TIEN_MAT;
            case "CHUYEN_KHOAN" -> CHUYEN_KHOAN;
            case "THE_NGAN_HANG" -> THE_NGAN_HANG;
            case "DIEM_TICH_LUY" -> DIEM_TICH_LUY;
            default -> PhuongThuc.valueOf(giaTri.trim());
        };
    }
}
