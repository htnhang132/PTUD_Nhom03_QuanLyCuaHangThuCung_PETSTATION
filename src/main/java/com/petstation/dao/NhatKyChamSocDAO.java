package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho ChamSoc; service gọi qua hợp đồng này. */
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
