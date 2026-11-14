package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.VoucherDAO;
import com.petstation.dao.khachhang.impl.VoucherDAOImpl;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.Voucher;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DanhMucService.CacDong;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;

public final class VoucherService {
    private static final VoucherService MAC_DINH = new VoucherService();

    public static VoucherService macDinh() {
        return MAC_DINH;
    }

    private final VoucherDAO voucherDAO;
    private final DanhMucService danhMucService;

    public VoucherService() {
        this(new VoucherDAOImpl(), DanhMucService.macDinh());
    }

    public VoucherService(VoucherDAO voucherDAO) {
        this(voucherDAO, DanhMucService.macDinh());
    }

    public VoucherService(VoucherDAO voucherDAO, DanhMucService danhMucService) {
        this.voucherDAO = Objects.requireNonNull(voucherDAO, "voucherDAO không được null");
        this.danhMucService =
                Objects.requireNonNull(danhMucService, "danhMucService không được null");
    }

    public CacDong chuanBi(boolean docNgay) {
        return danhMucService.chuanBi(LoaiDanhMuc.VOUCHER, docNgay);
    }

    public List<Voucher> layDanhSachVoucher() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHUYEN_MAI);
        return voucherDAO.layDanhSachVoucher();
    }

    public Voucher timTheoMa(String maVoucher) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_KHUYEN_MAI);
        return voucherDAO.timTheoMa(maVoucher);
    }

    public static String kiemTraVoucherSoLuong(long soLuong, long daDung) {
        return DanhMucService.kiemTraVoucherSoLuong(soLuong, daDung);
    }
}
