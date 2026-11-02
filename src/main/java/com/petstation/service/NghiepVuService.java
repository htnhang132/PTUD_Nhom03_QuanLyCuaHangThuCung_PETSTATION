package com.petstation.service;

import com.petstation.dao.NghiepVuDAO;
import com.petstation.dao.impl.NghiepVuDAOImpl;
import com.petstation.model.LoaiNghiepVu;

import java.util.List;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho nghiệp vụ dịch vụ. */
public final class NghiepVuService {
    private static final NghiepVuDAO nghiepVuDAO = new NghiepVuDAOImpl();

    private NghiepVuService() {}

    /**
     * Điều phối nghiệp vụ và kiểm tra dữ liệu cho định nghĩa.
     *
     * @param tieuDe tiêu đề
     * @param cacCot các cột
     */
    public record DinhNghia(String tieuDe, String[] cacCot) {}

    public static DinhNghia layDinhNghia(LoaiNghiepVu kieu) {
        // Hiển thị.
        var hienThi = nghiepVuDAO.layThongTin(kieu);
        return new DinhNghia(hienThi.tieuDe(), hienThi.cacCot());
    }

    public static List<Object[]> layCacDong(LoaiNghiepVu kieu) {
        return nghiepVuDAO.layCacDong(
                kieu, kieu == LoaiNghiepVu.GIO_HANG_TREO ? PhienDangNhapService.layIdTaiKhoan() : 0);
    }
}
