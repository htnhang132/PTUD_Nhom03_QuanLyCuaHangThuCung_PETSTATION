package com.petstation.model;

public enum PhuongThuc {
    TIEN_MAT,
    CHUYEN_KHOAN,
    THE_NGAN_HANG,
    DIEM_TICH_LUY,
    KET_HOP;

    public static PhuongThuc docTuCSDL(String giaTri) {
        if (giaTri == null || giaTri.isBlank()) return TIEN_MAT;
        return switch (giaTri.trim().toUpperCase()) {
            case "TIEN_MAT" -> TIEN_MAT;
            case "CHUYEN_KHOAN" -> CHUYEN_KHOAN;
            case "THE_NGAN_HANG" -> THE_NGAN_HANG;
            case "DIEM_TICH_LUY" -> DIEM_TICH_LUY;
            case "KET_HOP" -> KET_HOP;
            default -> PhuongThuc.valueOf(giaTri.trim());
        };
    }
}
