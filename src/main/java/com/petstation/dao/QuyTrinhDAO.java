package com.petstation.dao;

import java.util.List;

public interface QuyTrinhDAO {
    List<Object[]> laySpa(long id);

    String layTrangThaiHotel(long id);

    List<Object[]> layLichHen(long id);

    List<Object[]> layPhong();

    java.util.List<Object[]> layPhongChiTiet();

    java.util.List<Object[]> layLichPhong();
}
