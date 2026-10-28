package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.ChiTietThongKeDAO;
import com.petstation.dao.impl.ChiTietThongKeDAOImpl;
import com.petstation.model.ChiTietBaoCao;
import com.petstation.model.KhoangNgay;
import com.petstation.model.Quyen;

import java.util.Set;

/** Kiểm tra quyền trước khi mở chứng từ; DAO không đọc phiên đăng nhập. */
public final class ChiTietThongKeService {
    private static final ChiTietThongKeDAO chiTietThongKeDAO = new ChiTietThongKeDAOImpl();

    private ChiTietThongKeService() {}

    public static ChiTietBaoCao lay(String baoCao, String goc, KhoangNgay ky) {
        return lay(chiTietThongKeDAO, baoCao, goc, ky);
    }

    static ChiTietBaoCao lay(ChiTietThongKeDAO dao, String baoCao, String goc, KhoangNgay ky) {
        boolean tatCa = layPhien().kiemTraCo(Quyen.REPORT_VIEW_ALL);
        if (!tatCa) yeuCau(Quyen.REPORT_VIEW_OWN_SHIFT);
        boolean baoCaoToanCuaHang =
                baoCao.contains("Công nợ")
                        || baoCao.contains("khách hàng")
                        || baoCao.contains("nhân viên")
                        || baoCao.contains("hàng hóa")
                                && !Set.of("Sản phẩm bán chạy", "Tồn kho hiện tại", "Sắp hết hạn")
                                        .contains(goc);
        if (baoCaoToanCuaHang) yeuCau(Quyen.REPORT_VIEW_ALL);
        return dao.lay(
                baoCao, goc, ky, tatCa ? null : layIdTaiKhoan(), tatCa ? null : layIdNhanVien());
    }
}
