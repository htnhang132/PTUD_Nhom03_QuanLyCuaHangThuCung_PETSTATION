package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho DeXuatGia; service gọi qua hợp đồng này. */
public interface DeXuatGiaDAO {
    List<Object[]> timSanPham(String ma);

    long them(long sanPham, long nguoi, long cu, long moi, String lyDo);

    List<Object[]> timChoDuyet(long id);

    void apDung(long id, long sanPham, long gia, long nguoi);

    void tuChoi(long id);
}
