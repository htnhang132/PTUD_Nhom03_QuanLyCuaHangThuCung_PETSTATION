package com.petstation.service.thongke;

import com.petstation.dao.thongke.ThongKeDAO;
import com.petstation.dao.thongke.impl.ThongKeDAOImpl;
import com.petstation.dto.ChiTietBaoCao;
import com.petstation.dto.KhoangNgay;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.Objects;
import java.util.Set;

// Dịch vụ truy xuất chứng từ chi tiết (Drill-down) khi người dùng nhấp vào biểu đồ hoặc thẻ KPI
public final class ChiTietThongKeService {
    private final ThongKeDAO thongKeDAO;
    private final PhienDangNhapService phienDangNhapService;

    public ChiTietThongKeService() {
        this(new ThongKeDAOImpl(), PhienDangNhapService.macDinh());
    }

    public ChiTietThongKeService(ThongKeDAO thongKeDAO) {
        this(thongKeDAO, PhienDangNhapService.macDinh());
    }

    public ChiTietThongKeService(ThongKeDAO thongKeDAO, PhienDangNhapService phienDangNhapService) {
        this.thongKeDAO = Objects.requireNonNull(thongKeDAO, "thongKeDAO không được null");
        this.phienDangNhapService =
                Objects.requireNonNull(
                        phienDangNhapService, "phienDangNhapService không được null");
    }

    // KIỂM TRA PHÂN QUYỀN & TRUY XUẤT CHỨNG TỪ CHI TIẾT THEO GÓC NHÌN BÁO CÁO

    public ChiTietBaoCao lay(String baoCao, String goc, KhoangNgay ky) {
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        if (!tatCa) phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CA);
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
        if (baoCaoToanCuaHang) phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layChiTiet(
                baoCao, goc, ky, tatCa ? null : phienDangNhapService.layMaNhanVien());
    }
}
