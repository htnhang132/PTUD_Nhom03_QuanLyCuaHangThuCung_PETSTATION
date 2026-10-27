package com.petstation.dao;

import java.time.LocalDateTime;
import java.util.List;

/** Thao tác dữ liệu cho ViecCanLam; service gọi qua hợp đồng này. */
public interface ViecCanLamDAO {
    List<Object[]> layLichSapDen();

    List<Object[]> laySpaChoXuLy();

    List<Object[]> layHotelSapTra();

    List<Object[]> layPhongChoDon();

    List<Object[]> layTonThap();

    List<Object[]> layLoSapHetHan();

    List<Object[]> layVaccineDenHan();

    List<Object[]> layTrangThaiNhac(long taiKhoan);

    int luuTrangThaiNhac(long taiKhoan, String khoa, boolean daXem, LocalDateTime hoan);
}
