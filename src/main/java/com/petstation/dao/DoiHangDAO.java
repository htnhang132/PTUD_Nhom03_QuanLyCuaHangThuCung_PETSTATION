package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho DoiHang; service gọi qua hợp đồng này. */
public interface DoiHangDAO {
    List<Object[]> timDongGoc(long id);

    List<Object[]> timThayThe(String ma);

    long taoPhieu(long hoaDon, long nguoi, long chenhLech, String lyDo, long giaTri, String khoa);

    void hoanTat(long phieu, long hoaDon);

    long layChenhLechTruoc(long hoaDon);

    boolean laHoaDonThayThe(long hoaDon);
}
