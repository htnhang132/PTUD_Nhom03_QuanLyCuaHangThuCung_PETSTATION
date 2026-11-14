package com.petstation.dao.spa.impl;

import static com.petstation.database.ThaoTacSQL.truyVan;

import com.petstation.dao.spa.PhieuSpaTreoDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.SpaDTO;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.model.spa.GoiDichVuSpa;
import com.petstation.model.spa.LichHen;
import com.petstation.model.spa.PhieuSpaTreo;
import com.petstation.util.DuLieu;

import java.util.ArrayList;
import java.util.List;

public final class PhieuSpaTreoDAOImpl implements PhieuSpaTreoDAO {
    @Override
    public String them(String ma, String maNhanVien, PhieuSpaTreo p) {
        String maPhieu = (ma == null || ma.isBlank()) ? DuLieu.taoMa("TREOSPA") : ma;
        ThaoTacSQL.capNhat(
                """
INSERT INTO phieu_spa_treo(ma_phieu, ma_nhan_vien, ma_khach, so_dien_thoai, ma_thu,
    ma_goi, ma_hen, can_nang, tinh_trang, yeu_cau) VALUES(?,?,?,?,?,?,?,?,?,?)
""",
                maPhieu,
                maNhanVien,
                layMaKhach(p),
                p.getSoDienThoai(),
                layMaThu(p),
                layMaGoi(p),
                layMaHen(p),
                p.getCanNang(),
                p.getTinhTrang(),
                p.getYeuCau());
        return maPhieu;
    }

    @Override
    public void capNhat(String maPhieu, PhieuSpaTreo p) {
        ThaoTacSQL.capNhat(
                """
                UPDATE phieu_spa_treo SET ma_khach=?, so_dien_thoai=?, ma_thu=?, ma_goi=?,
                    ma_hen=?, can_nang=?, tinh_trang=?, yeu_cau=?, cap_nhat_luc=SYSDATETIME()
                WHERE ma_phieu = ?
                """,
                layMaKhach(p),
                p.getSoDienThoai(),
                layMaThu(p),
                layMaGoi(p),
                layMaHen(p),
                p.getCanNang(),
                p.getTinhTrang(),
                p.getYeuCau(),
                maPhieu);
    }

    private static String layMaKhach(PhieuSpaTreo p) {
        return p != null && p.getKhachHang() != null && p.getKhachHang().getMaKhachHang() != null
                ? p.getKhachHang().getMaKhachHang()
                : "";
    }

    private static String layMaThu(PhieuSpaTreo p) {
        return p != null && p.getThuCung() != null && p.getThuCung().getMaThuCung() != null
                ? p.getThuCung().getMaThuCung()
                : "";
    }

    private static String layMaGoi(PhieuSpaTreo p) {
        return p != null && p.getGoiDichVu() != null && p.getGoiDichVu().getMaGoi() != null
                ? p.getGoiDichVu().getMaGoi()
                : "";
    }

    private static String layMaHen(PhieuSpaTreo p) {
        return p != null && p.getLichHen() != null && p.getLichHen().getMaLichHen() != null
                ? p.getLichHen().getMaLichHen()
                : "";
    }

    @Override
    public PhieuSpaTreo lay(String maPhieu) {
        var ds =
                truyVan(
                        """
SELECT ma_khach, so_dien_thoai, ma_thu, ma_goi, ma_hen, can_nang, tinh_trang, yeu_cau,
    ma_phieu, ma_nhan_vien, cap_nhat_luc
FROM phieu_spa_treo WITH(UPDLOCK,HOLDLOCK)
WHERE ma_phieu = ?
""",
                        maPhieu);
        if (ds.isEmpty()) return null;
        Object[] d = ds.get(0);
        KhachHang kh =
                d[0] != null && !d[0].toString().isBlank() ? new KhachHang(d[0].toString()) : null;
        ThuCung tc =
                d[2] != null && !d[2].toString().isBlank() ? new ThuCung(d[2].toString()) : null;
        GoiDichVuSpa goi =
                d[3] != null && !d[3].toString().isBlank()
                        ? new GoiDichVuSpa(d[3].toString())
                        : null;
        LichHen lh = null;
        if (d[4] != null && !d[4].toString().isBlank()) {
            lh = new LichHen();
            lh.setMaLichHen(d[4].toString());
        }
        PhieuSpaTreo phieu =
                new PhieuSpaTreo(
                        kh,
                        tc,
                        goi,
                        lh,
                        d[1] == null ? "" : d[1].toString(),
                        d[5] == null ? "" : d[5].toString(),
                        d[6] == null ? "" : d[6].toString(),
                        d[7] == null ? "" : d[7].toString());
        phieu.setMaPhieu(d[8] == null ? null : d[8].toString());
        if (d[9] != null && !d[9].toString().isBlank()) {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(d[9].toString());
            phieu.setNhanVien(nv);
        }
        phieu.setCapNhatLuc(DuLieu.chuyenLocalDateTime(d[10]));
        return phieu;
    }

    @Override
    public List<SpaDTO.DongPhieuSpaTreo> danhSach() {
        var ds =
                truyVan(
                        """
SELECT p.ma_phieu AS ma_khoa, p.ma_phieu, p.ma_khach, k.ho_ten, p.so_dien_thoai,
    p.ma_thu, t.ten, p.ma_goi, g.ten_goi, p.cap_nhat_luc
FROM phieu_spa_treo p
LEFT JOIN khach_hang k ON k.ma_khach_hang = p.ma_khach
LEFT JOIN thu_cung t ON t.ma_thu_cung = p.ma_thu
LEFT JOIN goi_dich_vu_spa g ON g.ma_goi = p.ma_goi
ORDER BY p.cap_nhat_luc DESC, p.ma_phieu DESC
""");
        List<SpaDTO.DongPhieuSpaTreo> ketQua = new ArrayList<>();
        for (Object[] r : ds) {
            String maKhach = DuLieu.chuyenThanhChuoi(r[2]);
            KhachHang kh = null;
            if (!maKhach.isBlank()) {
                kh = new KhachHang(maKhach);
                kh.setHoTen(DuLieu.chuyenThanhChuoi(r[3]));
            }
            String maThu = DuLieu.chuyenThanhChuoi(r[5]);
            ThuCung tc = null;
            if (!maThu.isBlank()) {
                tc = new ThuCung(maThu);
                tc.setTen(DuLieu.chuyenThanhChuoi(r[6]));
            }
            String maGoi = DuLieu.chuyenThanhChuoi(r[7]);
            GoiDichVuSpa goi = null;
            if (!maGoi.isBlank()) {
                goi = new GoiDichVuSpa(maGoi);
                goi.setTenGoi(DuLieu.chuyenThanhChuoi(r[8]));
            }
            PhieuSpaTreo phieu =
                    new PhieuSpaTreo(kh, tc, goi, null, DuLieu.chuyenThanhChuoi(r[4]), "", "", "");
            phieu.setMaPhieu(DuLieu.chuyenThanhChuoi(r[1]));
            phieu.setCapNhatLuc(DuLieu.chuyenLocalDateTime(r[9]));
            ketQua.add(new SpaDTO.DongPhieuSpaTreo(DuLieu.chuyenThanhChuoi(r[0]), phieu));
        }
        return ketQua;
    }

    @Override
    public void xoa(String maPhieu) {
        ThaoTacSQL.capNhat(
                """
                DELETE FROM phieu_spa_treo
                WHERE ma_phieu = ?
                """,
                maPhieu);
    }
}
