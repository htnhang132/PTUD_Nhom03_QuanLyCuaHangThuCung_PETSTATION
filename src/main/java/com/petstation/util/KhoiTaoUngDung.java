package com.petstation.util;

import com.petstation.dao.CauHinhCSDL;
import com.petstation.dao.KetNoiCSDL;
import com.petstation.dao.QuanLyGiaoDich;
import com.petstation.dao.ThaoTacSQL;
import com.petstation.dao.impl.JdbcNhanVienDAO;
import com.petstation.dao.impl.JdbcNhatKyHeThongDAO;
import com.petstation.dao.impl.JdbcTaiKhoanDAO;
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

    /**
     * Khởi tạo KhoiTaoUngDung với các giá trị được cung cấp.
     *
     * @param csdlKetNoi CSDL kết nối
     * @param nguCanhPhien ngữ cảnh phiên
     * @param xacThucDichVu xác thực dịch vụ
     * @param phanQuyenDichVu phân quyền dịch vụ
     */
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

    /**
     * Tạo trong KhoiTaoUngDung.
     *
     * @return kết quả tạo
     */
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
                        new JdbcTaiKhoanDAO(),
                        new JdbcNhanVienDAO(),
                        new JdbcNhatKyHeThongDAO());
        return new KhoiTaoUngDung(csdl, cacPhien, xacThuc, new PhanQuyenService(cacPhien));
    }

    /**
     * Lấy CSDL kết nối trong KhoiTaoUngDung.
     *
     * @return kết quả lấy CSDL kết nối
     */
    public KetNoiCSDL layCSDLKetNoi() {
        return csdlKetNoi;
    }

    /**
     * Lấy ngữ cảnh phiên trong KhoiTaoUngDung.
     *
     * @return kết quả lấy ngữ cảnh phiên
     */
    public NguCanhPhienService layNguCanhPhien() {
        return nguCanhPhien;
    }

    /**
     * Lấy xác thực dịch vụ trong KhoiTaoUngDung.
     *
     * @return kết quả lấy xác thực dịch vụ
     */
    public XacThucService layXacThucDichVu() {
        return xacThucDichVu;
    }

    /**
     * Lấy phân quyền dịch vụ trong KhoiTaoUngDung.
     *
     * @return kết quả lấy phân quyền dịch vụ
     */
    public PhanQuyenService layPhanQuyenDichVu() {
        return phanQuyenDichVu;
    }
}
