package com.petstation.service;

import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.ChiTietThongKeDAO;
import com.petstation.dao.impl.ChiTietThongKeDAOImpl;
import com.petstation.model.ChiTietBaoCao;
import com.petstation.model.KhoangNgay;
import com.petstation.model.Quyen;

import java.util.Set;

// Dịch vụ truy xuất chứng từ chi tiết (Drill-down) khi người dùng nhấp vào biểu đồ hoặc thẻ KPI
public final class ChiTietThongKeService {
    private static final ChiTietThongKeDAO chiTietThongKeDAO = new ChiTietThongKeDAOImpl();

    private ChiTietThongKeService() {}

    // KIỂM TRA PHÂN QUYỀN & TRUY XUẤT CHỨNG TỪ CHI TIẾT THEO GÓC NHÌN BÁO CÁO

    public static ChiTietBaoCao lay(String baoCao, String goc, KhoangNgay ky) {
        return lay(chiTietThongKeDAO, baoCao, goc, ky);
    }

    static ChiTietBaoCao lay(ChiTietThongKeDAO dao, String baoCao, String goc, KhoangNgay ky) {
        boolean tatCa = layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        if (!tatCa) yeuCau(Quyen.XEM_BAO_CAO_CA);
        boolean baoCaoToanCuaHang =
                baoCao.contains("Công nợ")
                        || baoCao.contains("khách hàng")
                        || baoCao.contains("nhân viên")
                        || baoCao.contains("hàng hóa")
                                && !Set.of(
                                                "Sản phẩm bán chạy",
                                                "Tồn kho hiện tại",
                                                "Sắp hết hạn",
                                                "Hàng tồn đọng / chôn vốn",
                                                "Hao hụt từ kiểm kê")
                                        .contains(goc);
        if (baoCaoToanCuaHang) yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return dao.lay(
                baoCao, goc, ky, tatCa ? null : layIdTaiKhoan(), tatCa ? null : layIdNhanVien());
    }
}
