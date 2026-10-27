package com.petstation.util;

import com.petstation.dao.impl.NhanVienImpl;
import com.petstation.dao.impl.NhatKyHeThongImpl;
import com.petstation.dao.impl.TaiKhoanImpl;
import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.database.ThaoTacSQL;
import com.petstation.service.NguCanhPhienService;
import com.petstation.service.PhanQuyenService;
import com.petstation.service.XacThucService;

/** Khởi tạo kết nối SQL Server và các service dùng chung trong ứng dụng. */
public final class KhoiTaoUngDung {
    /** CSDL kết nối. */
    private final KetNoiCSDL csdlKetNoi;

    /** Ngữ cảnh phiên. */
    private final NguCanhPhienService nguCanhPhien;

    /** Xác thực dịch vụ. */
    private final XacThucService xacThucDichVu;

    /** Phân quyền dịch vụ. */
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
        // CSDL.
        KetNoiCSDL csdl = new KetNoiCSDL(CauHinhCSDL.nap());
        ThaoTacSQL.cauHinh(csdl);
        // Các giao dịch.
        QuanLyGiaoDich cacGiaoDich = new QuanLyGiaoDich(csdl);
        // Các phiên.
        NguCanhPhienService cacPhien = new NguCanhPhienService();
        // Xác thực.
        XacThucService xacThuc =
                new XacThucService(
                        cacGiaoDich,
                        new TaiKhoanImpl(),
                        new NhanVienImpl(),
                        new NhatKyHeThongImpl());
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
