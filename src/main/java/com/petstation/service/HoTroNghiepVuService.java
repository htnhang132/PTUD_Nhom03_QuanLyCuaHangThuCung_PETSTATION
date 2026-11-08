package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.HoTroNghiepVuDAO;
import com.petstation.dao.impl.HoTroNghiepVuDAOImpl;

import java.time.*;
import java.util.*;

final class HoTroNghiepVuService {
    private static final HoTroNghiepVuDAO hoTroNghiepVuDAO = new HoTroNghiepVuDAOImpl();

    private HoTroNghiepVuService() {}

    static long layId(String bang, String khoa, String ma) {
        return DanhMucService.layId(bang, khoa, ma);
    }

    static long layGiaTri(Object giaTri) {
        if (giaTri instanceof Number n) return n.longValue();
        if (giaTri == null) return 0L;
        String s = String.valueOf(giaTri).trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            return com.petstation.util.DuLieu.layIdCauNoi(null, s);
        }
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
