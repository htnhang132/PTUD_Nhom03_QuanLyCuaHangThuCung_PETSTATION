package com.petstation.dao;

import java.util.List;

public interface CaLamViecDAO {
    long layCocHotel(String maPhien, String phuongThuc);

    default long layCocHotel(long ca, String phuongThuc) {
        return layCocHotel(String.valueOf(ca), phuongThuc);
    }

    List<Object[]> layNguoiNhan(String tenDangNhapHienTai);

    default List<Object[]> layNguoiNhan(long taiKhoanHienTai) {
        return layNguoiNhan(String.valueOf(taiKhoanHienTai));
    }

    int dongCoBanGiao(
            long thucDem, long chenhLech, String lyDo, String hinhThuc, String nguoiNhan, String maPhien);

    default int dongCoBanGiao(
            long thucDem, long chenhLech, String lyDo, String hinhThuc, Long nguoiNhan, long id) {
        return dongCoBanGiao(
                thucDem,
                chenhLech,
                lyDo,
                hinhThuc,
                nguoiNhan == null ? null : String.valueOf(nguoiNhan),
                String.valueOf(id));
    }

    long layTongCocDaNhan(String maPhien);

    default long layTongCocDaNhan(long phien) {
        return layTongCocDaNhan(String.valueOf(phien));
    }

    long demDong(Object... cacThamSo);

    List<Object[]> timHienTai(Object... cacThamSo);

    long demBanHang(Object... cacThamSo);

    long demSoLuongHoaDon(Object... cacThamSo);

    long demDuKienTienMat(Object... cacThamSo);

    int capNhatMo(Object... cacThamSo);
}
