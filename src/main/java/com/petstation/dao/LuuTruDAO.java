package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho LuuTru; service gọi qua hợp đồng này. */
public interface LuuTruDAO {
    List<Object[]> layPhongDangDon();

    int capNhatNhanPhong(Object... cacThamSo);

    List<Object[]> timDatPhong(Object... cacThamSo);

    long demDatPhong(Object... cacThamSo);

    long themDatPhong(Object... cacThamSo);

    Object layNhanPhong(Object... cacThamSo);

    int danhDauPhongDangO(Object... cacThamSo);

    long demNhatKy(Object... cacThamSo);

    int capNhatNhatKy(Object... cacThamSo);

    List<Object[]> timHotelConPhaiThu(Object... cacThamSo);

    List<Object[]> layNhatKyChiTiet(Object... cacThamSo);

    int danhDauPhongKhaDung(Object... cacThamSo);

    int datMucCoc(long muc, long id);

    List<Object[]> layLichSuThuCoc(long id);

    List<Object[]> layMucCoc(long id);

    List<Object[]> khoaHopDongThuCoc(long id);

    long demKhoaThuCoc(long id, String tienToKhoa);

    int ghiNhanTongCoc(long tongCoc, long taiKhoan, long ca, long id);

    List<Object[]> khoaHopDongDoiCoc(long id);

    int dieuChinhMucCoc(long muc, String lyDo, long id);
}
