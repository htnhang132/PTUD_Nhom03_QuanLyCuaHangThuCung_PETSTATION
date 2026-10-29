package com.petstation.model;

/** Mã lưu trữ phương thức thu tiền; KET_HOP chỉ biểu diễn tổng hợp trên hóa đơn. */
public enum PhuongThuc {
    TIEN_MAT,
    CHUYEN_KHOAN,
    THE_NGAN_HANG,
    DIEM_TICH_LUY,
    KET_HOP;

    public static PhuongThuc docTuCSDL(String giaTri) {
        if (giaTri == null || giaTri.isBlank()) return TIEN_MAT;
        return switch (giaTri.trim().toUpperCase()) {
            case "TIEN_MAT", "CASH" -> TIEN_MAT;
            case "CHUYEN_KHOAN", "TRANSFER" -> CHUYEN_KHOAN;
            case "THE_NGAN_HANG", "CARD" -> THE_NGAN_HANG;
            case "DIEM_TICH_LUY", "POINTS" -> DIEM_TICH_LUY;
            case "KET_HOP", "MIXED" -> KET_HOP;
            default -> PhuongThuc.valueOf(giaTri.trim());
        };
    }
}
