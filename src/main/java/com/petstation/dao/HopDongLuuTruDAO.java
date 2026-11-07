package com.petstation.dao;

import java.util.List;

public interface HopDongLuuTruDAO {
    List<Object[]> layPhongDangDon();

    int capNhatNhanPhong(Object... cacThamSo);

    List<Object[]> timDatPhong(Object... cacThamSo);

    long demPhongDatTrung(long phongId, java.time.LocalDateTime ketThuc, java.time.LocalDateTime batDau);

    long demThuCungDatTrung(long thuCungId, java.time.LocalDateTime ketThuc, java.time.LocalDateTime batDau);

    long demPhongDatTrungBoQua(long phongId, java.time.LocalDateTime ketThuc, java.time.LocalDateTime batDau, long boQuaId);

    long demThuCungDatTrungBoQua(long thuCungId, java.time.LocalDateTime ketThuc, java.time.LocalDateTime batDau, long boQuaId);

    List<Object[]> layDatPhongSua(long id);

    int capNhatDatPhong(Object... cacThamSo);

    int huyDatPhong(Object... cacThamSo);

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
