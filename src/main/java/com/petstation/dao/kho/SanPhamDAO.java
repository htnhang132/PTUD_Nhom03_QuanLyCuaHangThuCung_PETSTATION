package com.petstation.dao.kho;

import com.petstation.model.kho.SanPham;

import java.util.List;

// Giao diện DAO quản lý danh mục sản phẩm, mã vạch & lịch sử giá
public interface SanPhamDAO {
    List<SanPham> layTatCa();

    SanPham timTheoMa(String maSanPham);

    List<SanPham> layDanhSachSanPham();

    int capNhatBarcode(String maSanPham, String barcode);

    boolean kiemTraSanPhamCoGiaoDich(String maSanPham);

    int ngungKinhDoanhSanPham(String maSanPham);
}
