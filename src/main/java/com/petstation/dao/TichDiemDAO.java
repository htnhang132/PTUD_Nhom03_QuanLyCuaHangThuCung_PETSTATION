package com.petstation.dao;

import java.util.List;

public interface TichDiemDAO {
    List<Object[]> layRangBuocVoucher(String ma);

    List<Object[]> timKhachHang(String ma);

    List<Object[]> timTheoKhoa(String khoa);

    String taoVoucher(long khachHang, String khoa);

    List<Object[]> timVoucher(String ma);

    List<Object[]> layCuaKhach(String ma);
}
