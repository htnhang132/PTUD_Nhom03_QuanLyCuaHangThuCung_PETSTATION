package com.petstation.model.chung;

public enum Quyen {
    XEM_TONG_QUAN,
    QUAN_LY_TAI_KHOAN,
    QUAN_LY_LICH_LAM_VIEC,
    XEM_LICH_LAM_VIEC,
    QUAN_LY_KHACH_HANG,
    QUAN_LY_THU_CUNG,
    QUAN_LY_SAN_PHAM,
    QUAN_LY_NHA_CUNG_CAP,
    NHAP_KHO,
    XUAT_HUY_KHO,
    KIEM_KE_KHO,
    DE_XUAT_GIA,
    DUYET_GIA,
    BAN_HANG,
    THANH_TOAN,
    QUAN_LY_CA_THU_NGAN,
    QUAN_LY_KHUYEN_MAI,
    QUAN_LY_SPA,
    QUAN_LY_LUU_TRU,
    XU_LY_PHE_DUYET,
    XEM_NHAT_KY,
    XEM_BAO_CAO_CHUNG,
    XEM_BAO_CAO_CA;

    public static Quyen docTuCSDL(String giaTri) {
        if (giaTri == null || giaTri.isBlank()) {
            return null;
        }
        String chuoi = giaTri.trim().toUpperCase();
        if (chuoi.contains(";") || chuoi.contains(",")) {
            for (String sub : chuoi.split("[,;]")) {
                if (!sub.isBlank()) {
                    Quyen q = docTuCSDL(sub);
                    if (q != null) {
                        return q;
                    }
                }
            }
            return null;
        }
        switch (chuoi) {
            case "XEM_TONG_QUAN":
                return XEM_TONG_QUAN;
            case "QUAN_LY_TAI_KHOAN":
                return QUAN_LY_TAI_KHOAN;
            case "QUAN_LY_LICH_LAM_VIEC":
                return QUAN_LY_LICH_LAM_VIEC;
            case "XEM_LICH_LAM_VIEC":
                return XEM_LICH_LAM_VIEC;
            case "QUAN_LY_KHACH_HANG":
                return QUAN_LY_KHACH_HANG;
            case "QUAN_LY_THU_CUNG":
                return QUAN_LY_THU_CUNG;
            case "QUAN_LY_SAN_PHAM":
                return QUAN_LY_SAN_PHAM;
            case "QUAN_LY_NHA_CUNG_CAP":
                return QUAN_LY_NHA_CUNG_CAP;
            case "NHAP_KHO":
                return NHAP_KHO;
            case "XUAT_HUY_KHO":
                return XUAT_HUY_KHO;
            case "KIEM_KE_KHO":
                return KIEM_KE_KHO;
            case "DE_XUAT_GIA":
                return DE_XUAT_GIA;
            case "DUYET_GIA":
                return DUYET_GIA;
            case "BAN_HANG":
                return BAN_HANG;
            case "THANH_TOAN":
                return THANH_TOAN;
            case "QUAN_LY_CA_THU_NGAN":
                return QUAN_LY_CA_THU_NGAN;
            case "QUAN_LY_KHUYEN_MAI":
                return QUAN_LY_KHUYEN_MAI;
            case "QUAN_LY_SPA":
                return QUAN_LY_SPA;
            case "QUAN_LY_LUU_TRU":
                return QUAN_LY_LUU_TRU;
            case "XU_LY_PHE_DUYET":
                return XU_LY_PHE_DUYET;
            case "XEM_NHAT_KY":
                return XEM_NHAT_KY;
            case "XEM_BAO_CAO_CHUNG":
                return XEM_BAO_CAO_CHUNG;
            case "XEM_BAO_CAO_CA":
                return XEM_BAO_CAO_CA;
            default:
                try {
                    return Quyen.valueOf(chuoi);
                } catch (Exception e) {
                    return null;
                }
        }
    }
}
