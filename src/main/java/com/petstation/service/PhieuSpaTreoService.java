package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import com.petstation.dao.PhieuSpaTreoDAO;
import com.petstation.dao.impl.PhieuSpaTreoDAOImpl;
import com.petstation.model.PhieuSpaTreo;
import com.petstation.model.Quyen;
import com.petstation.util.DuLieu;
import java.util.List;

/** Treo nháp không tiếp nhận thú cưng, không thay đổi lịch hẹn hoặc doanh thu. */
public final class PhieuSpaTreoService {
    private static final PhieuSpaTreoDAO dao = new PhieuSpaTreoDAOImpl();
    private PhieuSpaTreoService() {}

    public static long treo(long id, PhieuSpaTreo p) {
        yeuCau(Quyen.QUAN_LY_SPA);
        if (!p.coNoiDung()) throw new IllegalArgumentException("Chưa có nội dung để treo phiếu.");
        return thucHienGiaoDich(c -> {
            if (id == 0) return dao.them(DuLieu.taoMa("TREOSPA"), layIdTaiKhoan(), p);
            yeuCauConTonTai(id);
            dao.capNhat(id, p);
            return id;
        });
    }

    public static PhieuSpaTreo lay(long id) {
        yeuCau(Quyen.QUAN_LY_SPA);
        return yeuCauConTonTai(id);
    }

    private static PhieuSpaTreo yeuCauConTonTai(long id) {
        var p = dao.lay(id);
        if (p == null) throw new IllegalArgumentException("Phiếu treo đã được tạo hoặc xóa ở nơi khác.");
        return p;
    }

    public static List<Object[]> danhSach() {
        yeuCau(Quyen.QUAN_LY_SPA);
        return dao.danhSach();
    }

    public static void xoa(long id) {
        yeuCau(Quyen.QUAN_LY_SPA);
        thucHienGiaoDich(c -> { yeuCauConTonTai(id); dao.xoa(id); return null; });
    }

    /** Tạo phiếu và xóa nháp trong cùng giao dịch; lỗi nghiệp vụ giữ nguyên nháp. */
    public static long taoPhieu(long idTreo, String maThu, String maGoi, String maHen,
            double canNang, String tinhTrang, String yeuCau) {
        yeuCau(Quyen.QUAN_LY_SPA);
        return thucHienGiaoDich(c -> {
            if (idTreo > 0) { yeuCauConTonTai(idTreo); dao.xoa(idTreo); }
            return LapPhieuSpaService.luu(maThu, maGoi, maHen, canNang, tinhTrang, yeuCau);
        });
    }
}
