package com.petstation.dao;

import java.util.List;

public interface DoiHangDAO {
    List<Object[]> timHoaDon(String tuKhoa);

    List<Object[]> layDongHoaDon(long hoaDon);

    List<Object[]> timPhuKien(String tuKhoa);

    List<Object[]> timDongGoc(long id);

    List<Object[]> timThayThe(String ma);

    long taoPhieu(long hoaDon, long nguoi, long chenhLech, String lyDo, long giaTri, String khoa);

    void hoanTat(long phieu, long hoaDon);

    long layChenhLechTruoc(long hoaDon);

    boolean laHoaDonThayThe(long hoaDon);
}
