package com.petstation.model;

/** Biểu diễn dữ liệu quyền dùng trong nghiệp vụ cửa hàng. */
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
            case "DASHBOARD_VIEW":
                return XEM_TONG_QUAN;
            case "QUAN_LY_TAI_KHOAN":
            case "ACCOUNT_MANAGE":
                return QUAN_LY_TAI_KHOAN;
            case "QUAN_LY_LICH_LAM_VIEC":
            case "SCHEDULE_MANAGE":
                return QUAN_LY_LICH_LAM_VIEC;
            case "XEM_LICH_LAM_VIEC":
            case "SCHEDULE_VIEW":
                return XEM_LICH_LAM_VIEC;
            case "QUAN_LY_KHACH_HANG":
            case "CUSTOMER_MANAGE":
                return QUAN_LY_KHACH_HANG;
            case "QUAN_LY_THU_CUNG":
            case "PET_MANAGE":
                return QUAN_LY_THU_CUNG;
            case "QUAN_LY_SAN_PHAM":
            case "PRODUCT_MANAGE":
                return QUAN_LY_SAN_PHAM;
            case "QUAN_LY_NHA_CUNG_CAP":
            case "SUPPLIER_MANAGE":
                return QUAN_LY_NHA_CUNG_CAP;
            case "NHAP_KHO":
            case "INVENTORY_RECEIVE":
                return NHAP_KHO;
            case "XUAT_HUY_KHO":
            case "INVENTORY_ISSUE":
                return XUAT_HUY_KHO;
            case "KIEM_KE_KHO":
            case "INVENTORY_COUNT":
                return KIEM_KE_KHO;
            case "DE_XUAT_GIA":
            case "PRICE_PROPOSE":
                return DE_XUAT_GIA;
            case "DUYET_GIA":
            case "PRICE_APPROVE":
                return DUYET_GIA;
            case "BAN_HANG":
            case "POS_SELL":
                return BAN_HANG;
            case "THANH_TOAN":
            case "PAYMENT_PROCESS":
                return THANH_TOAN;
            case "QUAN_LY_CA_THU_NGAN":
            case "CASH_SHIFT_MANAGE":
                return QUAN_LY_CA_THU_NGAN;
            case "QUAN_LY_KHUYEN_MAI":
            case "PROMOTION_MANAGE":
                return QUAN_LY_KHUYEN_MAI;
            case "QUAN_LY_SPA":
            case "SPA_MANAGE":
                return QUAN_LY_SPA;
            case "QUAN_LY_LUU_TRU":
            case "HOTEL_MANAGE":
                return QUAN_LY_LUU_TRU;
            case "XU_LY_PHE_DUYET":
            case "APPROVAL_PROCESS":
                return XU_LY_PHE_DUYET;
            case "XEM_NHAT_KY":
            case "AUDIT_VIEW":
                return XEM_NHAT_KY;
            case "XEM_BAO_CAO_CHUNG":
            case "REPORT_VIEW_ALL":
                return XEM_BAO_CAO_CHUNG;
            case "XEM_BAO_CAO_CA":
            case "REPORT_VIEW_OWN_SHIFT":
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
