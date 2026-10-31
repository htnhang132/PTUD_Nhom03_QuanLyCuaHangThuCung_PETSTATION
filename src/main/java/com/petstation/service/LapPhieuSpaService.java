package com.petstation.service;

import com.petstation.dao.PhieuTiepNhanSpaDAO;
import com.petstation.dao.impl.PhieuTiepNhanSpaDAOImpl;
import com.petstation.database.ThaoTacSQL;
import com.petstation.model.Quyen;
import java.util.List;

/** Ghép các nghiệp vụ Spa đã có; không thay đổi cách lưu của các phân hệ khác. */
public final class LapPhieuSpaService {
    private static final PhieuTiepNhanSpaDAO dao = new PhieuTiepNhanSpaDAOImpl();

    private LapPhieuSpaService() {}

    public static long baoGia(String maThu, String maGoi, double canNang) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        kiemTraCanNang(canNang);
        Object[] thu = HoTroNghiepVuService.layThuCung(maThu);
        List<Object[]> gia = dao.layGiaDichVuTheoCanNang(maGoi, thu[2], canNang);
        if (gia.isEmpty())
            throw new IllegalArgumentException("Chưa có giá phù hợp với loài và cân nặng này.");
        return ((Number) gia.get(0)[1]).longValue();
    }

    public static long luu(String maThu, String maGoi, String maHen,
                           double canNang, String tinhTrang, String yeuCau) {
        PhienDangNhapService.yeuCau(Quyen.QUAN_LY_SPA);
        kiemTraCanNang(canNang);
        if (maGoi == null || maGoi.isBlank())
            throw new IllegalArgumentException("Cần chọn dịch vụ.");
        if (tinhTrang == null || tinhTrang.isBlank())
            throw new IllegalArgumentException("Cần nhập tình trạng khi nhận.");
        String ghiChu = "Tình trạng khi nhận: " + tinhTrang.trim()
            + "\nYêu cầu khách hàng / vật dụng: " + (yeuCau == null ? "" : yeuCau.trim());
        return ThaoTacSQL.thucHienGiaoDich(c -> {
            long id = SpaService.tiepNhan(maThu, maHen, canNang, ghiChu);
            // Tiếp nhận từ lịch đã tự thêm gói trong SpaService hiện có.
            if (maHen == null || maHen.isBlank()) SpaService.themDichVuSpa(id, maGoi);
            return id;
        });
    }

    private static void kiemTraCanNang(double canNang) {
        if (!Double.isFinite(canNang) || canNang <= 0 || canNang > 100)
            throw new IllegalArgumentException("Cân nặng phải lớn hơn 0 và không quá 100 kg.");
    }
}
