package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;

import com.petstation.dao.impl.JdbcHoTroNghiepVuDAO;

import java.time.*;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho nghiệp vụ hỗ trợ. */
final class HoTroNghiepVuService {
    /** Khởi tạo HoTroNghiepVuService với các giá trị được cung cấp. */
    private HoTroNghiepVuService() {}

    /**
     * Lấy id trong HoTroNghiepVuService.
     *
     * @param bang bảng
     * @param khoa khóa
     * @param ma mã
     * @return kết quả lấy id
     */
    static long layId(String bang, String khoa, String ma) {
        return DanhMucService.layId(bang, khoa, ma);
    }

    /**
     * Lấy giá trị trong HoTroNghiepVuService.
     *
     * @param giaTri giá trị
     * @return kết quả lấy giá trị
     */
    static long layGiaTri(Object giaTri) {
        return ((Number) giaTri).longValue();
    }

    /**
     * Kiểm tra dương trong HoTroNghiepVuService.
     *
     * @param soLuong số lượng
     */
    static void kiemTraDuong(long soLuong) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng/giá trị phải lớn hơn 0.");
    }

    /**
     * Lấy thú cưng trong HoTroNghiepVuService.
     *
     * @param ma mã
     * @return kết quả lấy thú cưng
     */
    static Object[] layThuCung(String ma) {
        // Các dòng.
        List<Object[]> cacDong = JdbcHoTroNghiepVuDAO.timThuCung(ma);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Thú cưng hoặc chủ nuôi không còn hoạt động.");
        return cacDong.get(0);
    }
}
