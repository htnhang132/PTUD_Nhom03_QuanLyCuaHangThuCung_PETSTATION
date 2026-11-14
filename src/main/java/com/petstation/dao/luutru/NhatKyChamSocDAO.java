package com.petstation.dao.luutru;

import com.petstation.model.luutru.NhatKyChamSoc;

import java.util.List;

public interface NhatKyChamSocDAO {
    List<NhatKyChamSoc> layLichSu(boolean spa, String maPhieu);

    List<String> khoaPhieu(boolean spa, String maPhieu);

    int themPhieu(
            String maSpa,
            String maHotel,
            String maNhanVien,
            String tinhTrang,
            String an,
            String thuoc,
            String viec,
            String anhTruoc,
            String anhSau);
}
