package com.petstation.service.hethong;

import com.petstation.dto.PhienDangNhap;
import com.petstation.model.chung.Quyen;
import com.petstation.service.chung.DieuHuongBanGhiService;
import com.petstation.service.nhansu.LichLamViecService;
import com.petstation.service.pheduyet.PheDuyetService;

import java.util.concurrent.Callable;

public final class PhienDangNhapService {
    private static final PhienDangNhapService MAC_DINH = new PhienDangNhapService();

    private volatile PhienDangNhap phienDangNhap;
    private final ThreadLocal<PhienDangNhap> phienCongViec = new ThreadLocal<>();

    public PhienDangNhapService() {}

    public static PhienDangNhapService macDinh() {
        return MAC_DINH;
    }

    public <T> T trongPhien(PhienDangNhap phien, Callable<T> viec) throws Exception {
        if (phien == null || phienDangNhap != phien)
            throw new IllegalArgumentException("Phiên đã thay đổi; hãy mở lại chức năng.");
        PhienDangNhap cu = phienCongViec.get();
        phienCongViec.set(phien);
        try {
            return viec.call();
        } finally {
            if (cu == null) {
                phienCongViec.remove();
            } else {
                phienCongViec.set(cu);
            }
        }
    }

    public void datPhienDangNhap(PhienDangNhap giaTri) {
        phienDangNhap = giaTri;
        if (giaTri == null) {
            DieuHuongBanGhiService.macDinh().xoa();
            PheDuyetService.xoaBoLangNghe();
            NhatKyHeThongService.macDinh().xoaBoLangNghe();
            LichLamViecService.macDinh().xoaBoLangNghe();
        }
    }

    public PhienDangNhap layPhienHienTai() {
        return phienDangNhap;
    }

    public PhienDangNhap yeuCauHienTai() {
        if (phienDangNhap == null) {
            throw new SecurityException("Phiên đăng nhập đã hết hạn.");
        }
        return phienDangNhap;
    }

    public PhienDangNhap layPhien() {
        PhienDangNhap nen = phienCongViec.get();
        if (nen != null) return nen;
        if (phienDangNhap == null)
            throw new IllegalStateException("Bạn cần đăng nhập để thực hiện thao tác này.");
        return phienDangNhap;
    }

    public String layTenDangNhap() {
        return layPhien().getTaiKhoan().getTenDangNhap();
    }

    public String layMaNhanVien() {
        var nv = layPhien().getNhanVien();
        return nv != null ? nv.getMaNhanVien() : null;
    }

    public void yeuCau(Quyen quyen) {
        if (!layPhien().kiemTraCo(quyen))
            throw new SecurityException("Bạn không có quyền thực hiện thao tác này.");
    }
}
