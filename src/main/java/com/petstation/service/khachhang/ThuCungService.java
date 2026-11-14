package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.ThuCungDAO;
import com.petstation.dao.khachhang.impl.ThuCungDAOImpl;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DanhMucService.CacDong;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;

public final class ThuCungService {
    private static final ThuCungService MAC_DINH = new ThuCungService();

    public static ThuCungService macDinh() {
        return MAC_DINH;
    }

    private final ThuCungDAO thuCungDAO;
    private final DanhMucService danhMucService;

    public ThuCungService() {
        this(new ThuCungDAOImpl(), DanhMucService.macDinh());
    }

    public ThuCungService(ThuCungDAO thuCungDAO) {
        this(thuCungDAO, DanhMucService.macDinh());
    }

    public ThuCungService(ThuCungDAO thuCungDAO, DanhMucService danhMucService) {
        this.thuCungDAO = Objects.requireNonNull(thuCungDAO, "thuCungDAO không được null");
        this.danhMucService =
                Objects.requireNonNull(danhMucService, "danhMucService không được null");
    }

    public CacDong chuanBi(boolean docNgay) {
        return danhMucService.chuanBi(LoaiDanhMuc.THU_CUNG, docNgay);
    }

    public CacDong layDanhMuc() {
        return danhMucService.layThuCung();
    }

    public List<ThuCung> layDanhSachThuCung() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_THU_CUNG);
        return thuCungDAO.layDanhSachThuCung();
    }

    public ThuCung layThuCungHoatDong(String ma) {
        ThuCung thu = thuCungDAO.timThuCungHoatDong(ma);
        if (thu == null) {
            throw new IllegalStateException("Thú cưng hoặc chủ nuôi không còn hoạt động.");
        }
        return thu;
    }

    public static String kiemTraCanNangThuCung(String canNang) {
        return DanhMucService.kiemTraCanNangThuCung(canNang);
    }
}
