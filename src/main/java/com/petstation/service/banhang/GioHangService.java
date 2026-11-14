package com.petstation.service.banhang;

import com.petstation.dto.GioHang;
import com.petstation.dto.GioHang.MucKhuyenMai;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.TichDiemService;
import com.petstation.util.DinhDang;

import java.util.List;
import java.util.Objects;

public final class GioHangService {
    private final BanHangService banHangService;
    private final TichDiemService tichDiemService;
    private final DanhMucService danhMucService;
    private final PhienDangNhapService phienDangNhapService;

    public GioHangService() {
        this(
                new BanHangService(),
                new TichDiemService(),
                DanhMucService.macDinh(),
                PhienDangNhapService.macDinh());
    }

    public GioHangService(
            BanHangService banHangService,
            TichDiemService tichDiemService,
            DanhMucService danhMucService) {
        this(banHangService, tichDiemService, danhMucService, PhienDangNhapService.macDinh());
    }

    public GioHangService(
            BanHangService banHangService,
            TichDiemService tichDiemService,
            DanhMucService danhMucService,
            PhienDangNhapService phienDangNhapService) {
        this.banHangService = Objects.requireNonNull(banHangService);
        this.tichDiemService = Objects.requireNonNull(tichDiemService);
        this.danhMucService = Objects.requireNonNull(danhMucService);
        this.phienDangNhapService = Objects.requireNonNull(phienDangNhapService);
    }

    public GioHang tao() {
        phienDangNhapService.yeuCauHienTai();
        return new GioHang(
                new GioHang.ChinhSach() {
                    @Override
                    public List<MucKhuyenMai> layKhuyenMai() {
                        phienDangNhapService.yeuCauHienTai();
                        return danhMucService.layKhuyenMai().stream()
                                .map(
                                        dongDuLieu ->
                                                new MucKhuyenMai(
                                                        String.valueOf(dongDuLieu[0]),
                                                        String.valueOf(dongDuLieu[1]),
                                                        String.valueOf(dongDuLieu[2]),
                                                        String.valueOf(dongDuLieu[3]),
                                                        DinhDang.doc(String.valueOf(dongDuLieu[4])),
                                                        DinhDang.doc(String.valueOf(dongDuLieu[5])),
                                                        String.valueOf(dongDuLieu[6]),
                                                        String.valueOf(dongDuLieu[7]),
                                                        String.valueOf(dongDuLieu[8])))
                                .toList();
                    }

                    @Override
                    public long kiemTraVoucher(
                            String ma, String khachHang, String nguon, long tamTinh) {
                        phienDangNhapService.yeuCauHienTai();
                        return tichDiemService.kiemTraVoucher(ma, khachHang, nguon, tamTinh);
                    }

                    @Override
                    public GioHang.BienLai thanhToan(
                            GioHang gio, PhuongThuc pt, long daNhan, String thuNgan) {
                        phienDangNhapService.yeuCauHienTai();
                        return banHangService.thanhToan(gio, pt, daNhan, thuNgan);
                    }
                });
    }
}
