package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho CaThuNgan; service gọi qua hợp đồng này. */
public interface CaThuNganDAO {
    long layCocHotel(long ca, String phuongThuc);

    List<Object[]> layNguoiNhan(long taiKhoanHienTai);

    int dongCoBanGiao(
            long thucDem, long chenhLech, String lyDo, String hinhThuc, Long nguoiNhan, long id);

    long layTongCocDaNhan(long phien);

    long demDong(Object... cacThamSo);

    List<Object[]> timHienTai(Object... cacThamSo);

    long demBanHang(Object... cacThamSo);

    long demSoLuongHoaDon(Object... cacThamSo);

    long demDuKienTienMat(Object... cacThamSo);

    int capNhatMo(Object... cacThamSo);
}
