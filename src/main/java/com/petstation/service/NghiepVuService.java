package com.petstation.service;

import com.petstation.dao.impl.JdbcNghiepVuDAO;
import com.petstation.model.LoaiNghiepVu;

import java.util.List;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho nghiệp vụ dịch vụ. */
public final class NghiepVuService {
    /** Khởi tạo NghiepVuService với các giá trị được cung cấp. */
    private NghiepVuService() {}

    /**
     * Điều phối nghiệp vụ và kiểm tra dữ liệu cho định nghĩa.
     *
     * @param tieuDe tiêu đề
     * @param cacCot các cột
     */
    public record DinhNghia(String tieuDe, String[] cacCot) {}

    /**
     * Lấy định nghĩa trong NghiepVuService.
     *
     * @param kieu kiểu
     * @return kết quả lấy định nghĩa
     */
    public static DinhNghia layDinhNghia(LoaiNghiepVu kieu) {
        // Hiển thị.
        var hienThi = JdbcNghiepVuDAO.taoKieuHienThi(kieu);
        return new DinhNghia(hienThi.tieuDe(), hienThi.cacCot());
    }

    /**
     * Lấy các dòng trong NghiepVuService.
     *
     * @param kieu kiểu
     * @return kết quả lấy các dòng
     */
    public static List<Object[]> layCacDong(LoaiNghiepVu kieu) {
        return JdbcNghiepVuDAO.layCacDong(
                kieu, kieu == LoaiNghiepVu.PENDING ? PhienDangNhapService.layIdTaiKhoan() : 0);
    }
}
