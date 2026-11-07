package com.petstation.service;

import com.petstation.model.PhienDangNhap;

public final class PhienDangNhapService {
    private PhienDangNhapService() {}

    private static volatile PhienDangNhap phienDangNhap;

    private static final ThreadLocal<PhienDangNhap> phienCongViec = new ThreadLocal<>();

    public static <T> T trongPhien(PhienDangNhap phien, java.util.concurrent.Callable<T> viec)
            throws Exception {
        if (phien == null || phienDangNhap != phien)
            throw new IllegalStateException("Phiên đã thay đổi; hãy mở lại chức năng.");
        PhienDangNhap cu = phienCongViec.get();
        phienCongViec.set(phien);
        com.petstation.util.BoiCanhDangNhap.phienCongViec().set(phien);
        try {
            return viec.call();
        } finally {
            if (cu == null) {
                phienCongViec.remove();
                com.petstation.util.BoiCanhDangNhap.phienCongViec().remove();
            } else {
                phienCongViec.set(cu);
                com.petstation.util.BoiCanhDangNhap.phienCongViec().set(cu);
            }
        }
    }

    public static void datPhienDangNhap(PhienDangNhap giaTri) {
        phienDangNhap = giaTri;
        com.petstation.util.BoiCanhDangNhap.datPhien(giaTri);
        if (giaTri == null) {
            DieuHuongBanGhiService.xoa();
            PheDuyetService.xoaBoLangNghe();
            NhatKyHeThongService.xoaBoLangNghe();
            LichLamViecService.xoaBoLangNghe();
            DanhMucService.xoaBoLangNghe();
            PheDuyetService.daChonHoaDon = "";
        }
    }

    public static PhienDangNhap layPhienHienTai() {
        return phienDangNhap;
    }

    public static PhienDangNhap layPhien() {
        PhienDangNhap nen = phienCongViec.get();
        if (nen != null) return nen;

        if (phienDangNhap == null)
            throw new IllegalStateException("Bạn cần đăng nhập để thực hiện thao tác này.");
        return phienDangNhap;
    }

    public static long layIdTaiKhoan() {
        var tk = layPhien().getTaiKhoan();
        if (tk == null) return 0L;
        long id = com.petstation.util.DuLieu.layIdCauNoi(tk, tk.getTenDangNhap());
        if (id > 0) return id;
        if (tk.getNhanVien() != null && tk.getNhanVien().getMaNhanVien() != null) {
            long nvId = com.petstation.util.DuLieu.layIdCauNoi(null, tk.getNhanVien().getMaNhanVien());
            if (nvId > 0) return nvId;
        }
        return 1L;
    }

    public static String layTenDangNhap() {
        return layPhien().getTaiKhoan().getTenDangNhap();
    }

    public static long layIdNhanVien() {
        var nv = layPhien().getNhanVien();
        return com.petstation.util.DuLieu.layIdCauNoi(nv, nv != null ? nv.getMaNhanVien() : null);
    }

    public static void yeuCau(com.petstation.model.Quyen quyen) {
        if (!layPhien().kiemTraCo(quyen))
            throw new com.petstation.service.PhanQuyenException(
                    "Bạn không có quyền thực hiện thao tác này.");
    }
}
