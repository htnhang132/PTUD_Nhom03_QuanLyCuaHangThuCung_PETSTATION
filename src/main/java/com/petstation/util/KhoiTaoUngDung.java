package com.petstation.util;

import com.petstation.dao.hethong.impl.NhatKyHeThongDAOImpl;
import com.petstation.dao.hethong.impl.TaiKhoanDAOImpl;
import com.petstation.dao.nhansu.impl.NhanVienDAOImpl;
import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.database.ThaoTacSQL;
import com.petstation.service.hethong.XacThucService;

public final class KhoiTaoUngDung {
    private final KetNoiCSDL csdlKetNoi;

    private final XacThucService xacThucDichVu;

    private KhoiTaoUngDung(KetNoiCSDL csdlKetNoi, XacThucService xacThucDichVu) {
        this.csdlKetNoi = csdlKetNoi;
        this.xacThucDichVu = xacThucDichVu;
    }

    public static KhoiTaoUngDung tao() {
        KetNoiCSDL csdl = new KetNoiCSDL(CauHinhCSDL.nap());
        ThaoTacSQL.cauHinh(csdl);
        QuanLyGiaoDich cacGiaoDich = new QuanLyGiaoDich(csdl);
        XacThucService xacThuc =
                new XacThucService(
                        cacGiaoDich,
                        new TaiKhoanDAOImpl(),
                        new NhanVienDAOImpl(),
                        new NhatKyHeThongDAOImpl());
        return new KhoiTaoUngDung(csdl, xacThuc);
    }

    public KetNoiCSDL layCSDLKetNoi() {
        return csdlKetNoi;
    }

    public XacThucService layXacThucDichVu() {
        return xacThucDichVu;
    }
}
