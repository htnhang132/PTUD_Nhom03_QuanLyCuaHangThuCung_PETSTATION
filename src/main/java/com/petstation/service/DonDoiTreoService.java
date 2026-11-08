package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.DonDoiTreoDAO;
import com.petstation.dao.HoaDonDAO;
import com.petstation.dao.impl.DonDoiTreoDAOImpl;
import com.petstation.dao.impl.HoaDonDAOImpl;
import com.petstation.model.DonDoiTreo;
import com.petstation.model.GioHang;
import com.petstation.model.Quyen;

public final class DonDoiTreoService {
    private static final DonDoiTreoDAO dao = new DonDoiTreoDAOImpl();
    private static final HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();

    private DonDoiTreoService() {}

    public static DonDoiTreo lay(long id) {
        yeuCau(Quyen.BAN_HANG);
        return dao.lay(id);
    }

    public static long treo(long id, DonDoiTreo don) {
        yeuCau(Quyen.BAN_HANG);
        return thucHienGiaoDich(c -> {
            long khoa = id;
            if (khoa == 0) khoa = hoaDonDAO.themTreo(taoMa("TREODOI"), layIdTaiKhoan(), null);
            else if (dao.lay(khoa) == null)
                throw new IllegalArgumentException("Đơn đổi treo đã được mở hoặc xóa ở nơi khác.");
            dao.luu(khoa, don);
            return khoa;
        });
    }

    public static long treo(GioHang gio) {
        yeuCau(Quyen.BAN_HANG);
        if (gio.isDangGhiThanhToan()) throw new IllegalStateException("Đang ghi thanh toán, vui lòng chờ.");
        if (!"DOI_HANG".equals(gio.getKieuNguon()) || gio.getDoiHang() == null
                || gio.getCacDong().size() != 1)
            throw new IllegalArgumentException("Không có đơn đổi phụ kiện để treo.");
        var yc = gio.getDoiHang();
        var hang = gio.getCacDong().get(0);
        return treo(0, new DonDoiTreo(dao.layMaHoaDonGoc(yc.getDongGoc()), yc.getDongGoc(),
                hang.getMa(), hang.getMa(), yc.getSoLuong(), yc.getLyDo(), true, true));
    }

    public static void chuyenThanhToan(long id, GioHang gio, long dong, int sl, String ma, String lyDo) {
        yeuCau(Quyen.BAN_HANG);
        thucHienGiaoDich(c -> {
            if (id > 0) {
                if (dao.lay(id) == null)
                    throw new IllegalArgumentException("Đơn đổi treo đã được mở hoặc xóa ở nơi khác.");
                BanHangService.xoaTreo(id);
            }
            DoiTraService.lapDoiHang(gio, dong, sl, ma, sl, lyDo);
            return null;
        });
    }
}
