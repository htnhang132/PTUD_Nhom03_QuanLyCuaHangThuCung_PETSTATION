package com.petstation.dao;

import com.petstation.model.LoaiDanhMuc;

import java.util.*;

/** Thao tác dữ liệu cho DanhMuc; service gọi qua hợp đồng này. */
public interface DanhMucDAO {
    List<Object[]> layLichSuGia();

    List<Object[]> nap(LoaiDanhMuc loai);

    String layMaTuLuaChon(Object luaChon);

    long layId(String bang, String khoa, String ma);

    int khoaCacTaiKhoanCua(String maNhanVien);

    void datLaiMatKhau(String tenDangNhap, String matKhau);

    void luu(
            LoaiDanhMuc loai,
            Object[] truoc,
            Object[] giaTri,
            long idNguoiThucHien,
            String nguoiThucHienVaiTro);

    int capNhatBarcode(long sanPhamId, String barcode);

    boolean kiemTraSanPhamCoGiaoDich(long sanPhamId);

    int ngungKinhDoanhSanPham(long sanPhamId);

    String layTenDangNhapTheoNhanVien(long nhanVienId);

    void taoTaiKhoanNhanVien(
            long nhanVienId, String tenDangNhap, String matKhauHash, String vaiTro);
    boolean tonTaiTenNcc(String ten, String maLoaiTru);

    boolean tonTaiSdtNcc(String soDienThoai, String maLoaiTru);
}
