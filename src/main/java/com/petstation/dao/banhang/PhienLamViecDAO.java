package com.petstation.dao.banhang;

import com.petstation.model.banhang.PhienLamViec;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.nhansu.NhanVien;

import java.util.List;
import java.util.Optional;

public interface PhienLamViecDAO {
    long layCocHotel(String maPhien, String phuongThuc);

    List<NhanVien> layNguoiNhan(String maNhanVienHienTai);

    int dongCoBanGiao(
            long thucDem,
            long chenhLech,
            String lyDo,
            String hinhThuc,
            String nguoiNhan,
            String maPhien);

    long layTongCocDaNhan(String maPhien);

    long demDong(String maPhien);

    Optional<PhienLamViec> timHienTai(String maNhanVienHoacTenDangNhap);

    long demBanHang(String maPhien, PhuongThuc mucTieu);

    long demSoLuongHoaDon(String maPhien);

    long demDuKienTienMat(String maPhien);

    int capNhatMo(String maNhanVien, String tenCa, long tienDauCa);
}
