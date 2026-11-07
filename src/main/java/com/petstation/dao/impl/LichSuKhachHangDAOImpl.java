package com.petstation.dao.impl;

import com.petstation.dao.LichSuKhachHangDAO;
import com.petstation.database.ThaoTacSQL;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LichSuKhachHangDAOImpl implements LichSuKhachHangDAO {
    private static final DateTimeFormatter DINH_DANG_ISO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    public LichSuKhachHangDAOImpl() {}

    public List<Object[]> layThuCung(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                        + " ma_thu_cung,ten,loai,giong,ngay_sinh,can_nang,tiem_chung,di_ung_benh_ly"
                        + " FROM thu_cung WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY ma_thu_cung",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> layCacHoaDon(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                    + " h.ma_hoa_don,h.thanh_toan_luc,h.tong_tien,h.hinh_thuc_thanh_toan,h.trang_thai"
                    + " FROM hoa_don h WHERE (h.ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND h.trang_thai<>'GIO_TREO' ORDER"
                    + " BY h.thanh_toan_luc DESC",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> layLichSuSpa(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT t.ma_phieu,p.ten,t.tao_luc,t.tong_tien,t.trang_thai FROM"
                        + " phieu_tiep_nhan_spa t JOIN thu_cung p ON p.ma_thu_cung=t.ma_thu_cung"
                        + " WHERE (p.ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(p.ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY t.tao_luc DESC",
                cacThamSo[0],
                cacThamSo[0]);
    }

    public List<Object[]> layLichSuLuuTru(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT h.ma_hop_dong,p.ten,r.ma_phong,h.ngay_vao,h.ngay_ra_du_kien,h.trang_thai"
                        + " FROM hop_dong_luu_tru h JOIN thu_cung p ON p.ma_thu_cung=h.ma_thu_cung JOIN"
                        + " phong_hotel r ON r.ma_phong=h.ma_phong WHERE (h.ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(h.ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY"
                        + " h.ngay_vao DESC",
                cacThamSo[0],
                cacThamSo[0]);
    }

    @Override
    public List<Object[]> layLichSuDiem(Object... cacThamSo) {
        List<Object[]> dong =
                ThaoTacSQL.truyVan(
                        "SELECT COALESCE(lich_su_diem, N'') "
                                + "FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                        cacThamSo[0],
                        cacThamSo[0]);

        if (dong.isEmpty()) {
            return List.of();
        }

        String chuoi = String.valueOf(dong.get(0)[0]).trim();
        if (chuoi.isEmpty()) {
            return List.of();
        }

        List<Object[]> kq = new ArrayList<>();
        int soBanGhi = 0;

        for (String phan : chuoi.split(";")) {
            if (phan.isBlank()) {
                continue;
            }

            soBanGhi++;

            String[] c = phan.split("\\|", -1);

            if (c.length != 5) {
                throw new IllegalStateException(
                        "Lịch sử điểm sai định dạng tại bản ghi " + soBanGhi);
            }

            try {
                boolean ngayODau =
                        c[0].trim().matches("\\d{4}-\\d{2}-\\d{2} .*");

                String ngay = c[ngayODau ? 0 : 4].trim();

                Timestamp thoiGian =
                        Timestamp.valueOf(
                                LocalDateTime.parse(
                                    ngay.length() > 19
                                            ? ngay.substring(0, 19)
                                            : ngay,
                                    DINH_DANG_ISO));

                long thayDoi =
                        Long.parseLong(c[ngayODau ? 2 : 1].trim());

                long soDuSau =
                        Long.parseLong(c[ngayODau ? 3 : 2].trim());

                String lyDo = c[ngayODau ? 4 : 3].trim();

                kq.add(new Object[] {
                    thoiGian,
                    thayDoi,
                    soDuSau,
                    lyDo
                });
            } catch (RuntimeException e) {
                throw new IllegalStateException(
                        "Không đọc được lịch sử điểm tại bản ghi "
                                + soBanGhi
                                + ": "
                                + e.getMessage(),
                        e);
            }
        }

        Collections.reverse(kq);
        return kq;
    }

    public List<Object[]> layVoucher(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT"
                        + " ma_voucher,menh_gia,hieu_luc_den,da_dung,so_luong,trang_thai"
                        + " FROM voucher WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY ma_voucher DESC",
                cacThamSo[0],
                cacThamSo[0]);
    }
}
