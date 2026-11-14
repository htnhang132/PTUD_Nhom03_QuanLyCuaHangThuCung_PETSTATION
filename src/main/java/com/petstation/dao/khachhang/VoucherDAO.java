package com.petstation.dao.khachhang;

import com.petstation.model.khachhang.Voucher;

import java.util.List;

// Giao diện DAO quản lý mã giảm giá Voucher
public interface VoucherDAO {
    List<Voucher> layTheoKhachHang(String maKhachHang);

    Voucher timTheoMa(String maVoucher);

    List<Voucher> layDanhSachVoucher();
}
