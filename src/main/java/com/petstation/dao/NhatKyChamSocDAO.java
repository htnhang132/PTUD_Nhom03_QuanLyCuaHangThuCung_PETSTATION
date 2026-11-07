package com.petstation.dao;

import java.util.List;

public interface NhatKyChamSocDAO {
    List<Object[]> layLichSu(boolean spa, long id);

    List<Object[]> khoaPhieu(boolean spa, long id);

    int themPhieu(
            Long spa,
            Long hotel,
            long nhanVien,
            String tinhTrang,
            String an,
            String thuoc,
            String viec,
            String anhTruoc,
            String anhSau);
}
