package com.petstation.service;

import com.petstation.model.DongThuTien;
import com.petstation.model.GioHang;
import com.petstation.model.PhuongThuc;

import java.util.List;

/** Ghép model giỏ hàng với chính sách dữ liệu thật tại một điểm khởi tạo. */
public final class GioHangService {
    private GioHangService() {}

    public static GioHang tao() {
        return new GioHang(
                new GioHang.ChinhSach() {
                    @Override
                    public List<Object[]> layKhuyenMai() {
                        return DanhMucService.layKhuyenMai();
                    }

                    @Override
                    public long kiemTraVoucher(
                            String ma, String khachHang, String nguon, long tamTinh) {
                        return TichDiemService.kiemTraVoucher(ma, khachHang, nguon, tamTinh);
                    }

                    @Override
                    public GioHang.BienLai thanhToan(
                            GioHang gio, PhuongThuc pt, long daNhan, String thuNgan) {
                        return BanHangService.thanhToan(gio, pt, daNhan, thuNgan);
                    }

                    @Override
                    public GioHang.BienLai thanhToan(
                            GioHang gio, List<DongThuTien> dong, String thuNgan) {
                        return BanHangService.thanhToan(gio, dong, thuNgan);
                    }
                });
    }
}
