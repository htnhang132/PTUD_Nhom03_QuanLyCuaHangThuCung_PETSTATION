package com.petstation.util;

import com.petstation.dao.impl.NhanVienDAOImpl;
import com.petstation.dao.impl.NhatKyHeThongDAOImpl;
import com.petstation.dao.impl.TaiKhoanDAOImpl;
import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.database.ThaoTacSQL;
import com.petstation.service.NguCanhPhienService;
import com.petstation.service.PhanQuyenService;
import com.petstation.service.XacThucService;

public final class KhoiTaoUngDung {
    private final KetNoiCSDL csdlKetNoi;

    private final NguCanhPhienService nguCanhPhien;

    private final XacThucService xacThucDichVu;

    private final PhanQuyenService phanQuyenDichVu;

    private KhoiTaoUngDung(
            KetNoiCSDL csdlKetNoi,
            NguCanhPhienService nguCanhPhien,
            XacThucService xacThucDichVu,
            PhanQuyenService phanQuyenDichVu) {
        this.csdlKetNoi = csdlKetNoi;
        this.nguCanhPhien = nguCanhPhien;
        this.xacThucDichVu = xacThucDichVu;
        this.phanQuyenDichVu = phanQuyenDichVu;
    }

    public static KhoiTaoUngDung tao() {
        KetNoiCSDL csdl = new KetNoiCSDL(CauHinhCSDL.nap());
        ThaoTacSQL.cauHinh(csdl);

        QuanLyGiaoDich cacGiaoDich = new QuanLyGiaoDich(csdl);

        NguCanhPhienService cacPhien = new NguCanhPhienService();

        XacThucService xacThuc =
                new XacThucService(
                        cacGiaoDich,
                        new TaiKhoanDAOImpl(),
                        new NhanVienDAOImpl(),
                        new NhatKyHeThongDAOImpl());
        return new KhoiTaoUngDung(csdl, cacPhien, xacThuc, new PhanQuyenService(cacPhien));
    }

    public KetNoiCSDL layCSDLKetNoi() {
        return csdlKetNoi;
    }

    public NguCanhPhienService layNguCanhPhien() {
        return nguCanhPhien;
    }

    public XacThucService layXacThucDichVu() {
        return xacThucDichVu;
    }

    public PhanQuyenService layPhanQuyenDichVu() {
        return phanQuyenDichVu;
    }
}
