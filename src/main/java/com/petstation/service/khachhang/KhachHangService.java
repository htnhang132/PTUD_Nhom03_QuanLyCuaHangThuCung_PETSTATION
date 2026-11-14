package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.KhachHangDAO;
import com.petstation.dao.khachhang.impl.KhachHangDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DanhMucService.CacDong;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class KhachHangService {
    private static final KhachHangService MAC_DINH = new KhachHangService();

    public static KhachHangService macDinh() {
        return MAC_DINH;
    }

    private final KhachHangDAO khachHangDAO;
    private final DanhMucService danhMucService;

    public KhachHangService() {
        this.khachHangDAO = null;
        this.danhMucService = DanhMucService.macDinh();
    }

    public KhachHangService(KhachHangDAO khachHangDAO) {
        this(khachHangDAO, DanhMucService.macDinh());
    }

    public KhachHangService(KhachHangDAO khachHangDAO, DanhMucService danhMucService) {
        this.khachHangDAO = Objects.requireNonNull(khachHangDAO, "khachHangDAO không được null");
        this.danhMucService =
                Objects.requireNonNull(danhMucService, "danhMucService không được null");
    }

    public CacDong chuanBi(boolean docNgay) {
        return danhMucService.chuanBi(LoaiDanhMuc.KHACH_HANG, docNgay);
    }

    public CacDong layDanhMuc() {
        return danhMucService.layKhachHang();
    }

    public List<String> layLuaChonKhachHang() {
        return danhMucService.layLuaChonKhachHang();
    }

    public List<KhachHang> timTatCa() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        if (khachHangDAO != null) {
            return khachHangDAO.timTatCa();
        }
        return QuanLyGiaoDich.macDinh().thucThi(c -> new KhachHangDAOImpl(c).timTatCa());
    }

    public Optional<KhachHang> timTheoMa(String maKhachHang) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHACH_HANG);
        if (khachHangDAO != null) {
            return khachHangDAO.timTheoMaKhachHang(maKhachHang);
        }
        return QuanLyGiaoDich.macDinh()
                .thucThi(c -> new KhachHangDAOImpl(c).timTheoMaKhachHang(maKhachHang));
    }
}
