package com.petstation.util;

import java.util.UUID;

/** Tiện ích thuần, không mở kết nối và không giữ trạng thái ứng dụng. */
public final class DuLieu {
    private DuLieu() {}

    public static String taoMa(String tienTo) {
        return tienTo + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
    }

    public static String chuyenThanhChuoi(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }
}
