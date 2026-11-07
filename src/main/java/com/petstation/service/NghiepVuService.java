package com.petstation.service;

import com.petstation.dao.NghiepVuDAO;
import com.petstation.dao.impl.NghiepVuDAOImpl;
import com.petstation.model.LoaiNghiepVu;

import java.util.List;

public final class NghiepVuService {
    private static final NghiepVuDAO nghiepVuDAO = new NghiepVuDAOImpl();

    private NghiepVuService() {}

    public record DinhNghia(String tieuDe, String[] cacCot) {}

    public static DinhNghia layDinhNghia(LoaiNghiepVu kieu) {
        var hienThi = nghiepVuDAO.layThongTin(kieu);
        return new DinhNghia(hienThi.tieuDe(), hienThi.cacCot());
    }

    public static List<Object[]> layCacDong(LoaiNghiepVu kieu) {
        return nghiepVuDAO.layCacDong(
                kieu, kieu == LoaiNghiepVu.GIO_HANG_TREO ? PhienDangNhapService.layIdTaiKhoan() : 0);
    }
}
