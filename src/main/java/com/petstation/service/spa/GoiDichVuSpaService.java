package com.petstation.service.spa;

import com.petstation.dao.spa.GoiDichVuSpaDAO;
import com.petstation.dao.spa.impl.GoiDichVuSpaDAOImpl;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DanhMucService.CacDong;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;

public final class GoiDichVuSpaService {
    private static final GoiDichVuSpaService MAC_DINH = new GoiDichVuSpaService();

    public static GoiDichVuSpaService macDinh() {
        return MAC_DINH;
    }

    private final GoiDichVuSpaDAO goiDichVuSpaDAO;
    private final DanhMucService danhMucService;

    public GoiDichVuSpaService() {
        this(new GoiDichVuSpaDAOImpl(), DanhMucService.macDinh());
    }

    public GoiDichVuSpaService(GoiDichVuSpaDAO goiDichVuSpaDAO) {
        this(goiDichVuSpaDAO, DanhMucService.macDinh());
    }

    public GoiDichVuSpaService(GoiDichVuSpaDAO goiDichVuSpaDAO, DanhMucService danhMucService) {
        this.goiDichVuSpaDAO =
                Objects.requireNonNull(goiDichVuSpaDAO, "goiDichVuSpaDAO không được null");
        this.danhMucService =
                Objects.requireNonNull(danhMucService, "danhMucService không được null");
    }

    public CacDong chuanBi(boolean docNgay) {
        return danhMucService.chuanBi(LoaiDanhMuc.DICH_VU_SPA, docNgay);
    }

    public List<GoiDichVuSpa> layDanhSachDichVu() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        return goiDichVuSpaDAO.layDanhSachDichVu();
    }

    public GoiDichVuSpa timTheoMa(String maDichVu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
        return goiDichVuSpaDAO.timTheoMa(maDichVu);
    }
}
