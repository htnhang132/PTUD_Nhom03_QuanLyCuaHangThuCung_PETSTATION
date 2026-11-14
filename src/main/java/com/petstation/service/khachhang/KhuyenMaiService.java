package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.ChuongTrinhKhuyenMaiDAO;
import com.petstation.dao.khachhang.impl.ChuongTrinhKhuyenMaiDAOImpl;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.ChuongTrinhKhuyenMai;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DanhMucService.CacDong;
import com.petstation.service.hethong.PhienDangNhapService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class KhuyenMaiService {
    private static final KhuyenMaiService MAC_DINH = new KhuyenMaiService();

    public static KhuyenMaiService macDinh() {
        return MAC_DINH;
    }

    private final ChuongTrinhKhuyenMaiDAO khuyenMaiDAO;
    private final DanhMucService danhMucService;

    public KhuyenMaiService() {
        this(new ChuongTrinhKhuyenMaiDAOImpl(), DanhMucService.macDinh());
    }

    public KhuyenMaiService(ChuongTrinhKhuyenMaiDAO khuyenMaiDAO) {
        this(khuyenMaiDAO, DanhMucService.macDinh());
    }

    public KhuyenMaiService(ChuongTrinhKhuyenMaiDAO khuyenMaiDAO, DanhMucService danhMucService) {
        this.khuyenMaiDAO = Objects.requireNonNull(khuyenMaiDAO, "khuyenMaiDAO không được null");
        this.danhMucService =
                Objects.requireNonNull(danhMucService, "danhMucService không được null");
    }

    public CacDong chuanBi(boolean docNgay) {
        return danhMucService.chuanBi(LoaiDanhMuc.KHUYEN_MAI, docNgay);
    }

    public List<ChuongTrinhKhuyenMai> layDanhSachKhuyenMai() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHUYEN_MAI);
        return khuyenMaiDAO.layDanhSachKhuyenMai();
    }

    public ChuongTrinhKhuyenMai timTheoMa(String maKhuyenMai) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHUYEN_MAI);
        return khuyenMaiDAO.timTheoMa(maKhuyenMai);
    }

    public static String kiemTraKhuyenMai(LocalDate tu, LocalDate den, String loai, long giaTri) {
        return DanhMucService.kiemTraKhuyenMai(tu, den, loai, giaTri);
    }
}
