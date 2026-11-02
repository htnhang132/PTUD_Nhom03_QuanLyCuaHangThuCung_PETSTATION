package com.petstation.dao;

import java.time.LocalDate;
import java.util.List;

/** Thao tác dữ liệu cho TiemPhong; service gọi qua hợp đồng này. */
public interface TiemPhongDAO {
    List<Object[]> layThuCung(String ma);

    List<Object[]> layLichSu(long id);

    void luu(long id, long thuCung, String ten, LocalDate ngay, LocalDate han, String ghiChu);

    void xoa(long id, long thuCung);
}
