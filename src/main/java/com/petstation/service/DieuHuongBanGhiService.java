package com.petstation.service;

import com.petstation.model.MaManHinh;

/** Giữ yêu cầu mở bản ghi giữa các lần tải; không thao tác thành phần Swing. */
public final class DieuHuongBanGhiService {
    private DieuHuongBanGhiService() {}

    public record YeuCau(MaManHinh manHinh, long id, String ma) {}

    private static volatile YeuCau cho;

    public static void dat(MaManHinh manHinh, long id, String ma) {
        cho = new YeuCau(manHinh, id, ma);
    }

    public static YeuCau lay() {
        return cho;
    }

    public static void xoa() {
        cho = null;
    }
}
