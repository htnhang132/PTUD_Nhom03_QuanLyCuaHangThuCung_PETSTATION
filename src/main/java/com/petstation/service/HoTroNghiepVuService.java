package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.HoTroNghiepVuDAO;
import com.petstation.dao.impl.HoTroNghiepVuImpl;

import java.time.*;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho nghiệp vụ hỗ trợ. */
final class HoTroNghiepVuService {
    private static final HoTroNghiepVuDAO hoTroNghiepVuDAO = new HoTroNghiepVuImpl();

    private HoTroNghiepVuService() {}

    static long layId(String bang, String khoa, String ma) {
        return DanhMucService.layId(bang, khoa, ma);
    }

    static long layGiaTri(Object giaTri) {
        return ((Number) giaTri).longValue();
    }

    static void kiemTraDuong(long soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng/giá trị phải lớn hơn 0.");
    }

    static Object[] layThuCung(String ma) {
        List<Object[]> cacDong = hoTroNghiepVuDAO.timThuCung(ma);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Thú cưng hoặc chủ nuôi không còn hoạt động.");
        return cacDong.get(0);
    }
}
