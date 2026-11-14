package com.petstation.dao.kho;

import com.petstation.model.kho.ChiTietKiemKe;
import com.petstation.model.kho.LoHang;

import java.util.List;
import java.util.Optional;

// Giao diện DAO quản lý kiểm kê hàng hóa tồn kho
public interface KiemKeDAO {
    List<LoHang> layDanhSachLoHangKiemKe();

    Optional<Integer> timKiemKe(String maLo);

    String themKiemKe(String maBienBan, String maNhanVien, String kyKiemKe, String trangThai);

    int capNhatKiemKe(
            String maBienBan, String maLo, int tonHeThong, int tonThucTe, String nguyenNhan);

    List<ChiTietKiemKe> layChiTietKiemKe(String maBienBan);

    long demApDungKiemKe(String maBienBan);

    List<ChiTietKiemKe> timApDungKiemKe(String maBienBan);

    int capNhatApDungKiemKe(int tonThucTe, String maLo, int tonHeThong);

    int danhDauKiemKeDaDuyet(String maBienBan);
}
