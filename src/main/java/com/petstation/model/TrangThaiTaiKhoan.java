package com.petstation.model;

/** Biểu diễn dữ liệu trạng thái tài khoản dùng trong nghiệp vụ cửa hàng. */
public enum TrangThaiTaiKhoan {
    HOAT_DONG,
    TAM_KHOA,
    NGUNG_HOAT_DONG;

    public static TrangThaiTaiKhoan docTuCSDL(String giaTri) {
        if (giaTri == null) return HOAT_DONG;
        return switch (giaTri.trim().toUpperCase()) {
            case "HOAT_DONG", "ACTIVE" -> HOAT_DONG;
            case "TAM_KHOA", "LOCKED" -> TAM_KHOA;
            case "NGUNG_HOAT_DONG", "INACTIVE" -> NGUNG_HOAT_DONG;
            default -> TrangThaiTaiKhoan.valueOf(giaTri.trim());
        };
    }
}
