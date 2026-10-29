package com.petstation.dao;

import java.time.LocalDateTime;
import java.util.List;

/** Thao tác dữ liệu cho XuLySpa; service gọi qua hợp đồng này. */
public interface PhieuTiepNhanSpaDAO {
    int capNhatTiepNhan(Object... cacThamSo);

    int capNhatSucKhoeThuCung(Object... cacThamSo);

    long demTrangThaiSpa(Object... cacThamSo);

    long demLichHen(Object... cacThamSo);

    long khoaNhanVienDangLam(Object... cacThamSo);

    long layThoiLuongDichVu(Object... cacThamSo);

    long demLichTrung(Object... cacThamSo);

    long themLichHen(Object... cacThamSo);

    List<Object[]> timTiepNhan(Object... cacThamSo);

    long themTiepNhan(Object... cacThamSo);

    Object layTiepNhan(Object... cacThamSo);

    List<Object[]> timThemDichVuSpa(Object... cacThamSo);

    List<Object[]> layGiaDichVuTheoCanNang(Object... cacThamSo);

    int capNhatThemDichVuSpa(Object... cacThamSo);

    int tinhLaiTongPhieuSpa(Object... cacThamSo);

    int capNhatTrangThaiSpa(Object... cacThamSo);

    int hoanTatLichHen(Object... cacThamSo);

    int huyLichHenBanGhi(Object... cacThamSo);

    Object layMaLichHenThuCung(Object... cacThamSo);

    List<Object[]> layPhieuSpaChiTiet(Object... cacThamSo);

    List<Object[]> layChiTietLichHen(long id);

    List<Object[]> khoaLichHen(long id);

    long demLichTrungKhiSua(
            long id, long nhanVien, Object thuCung, LocalDateTime ketThuc, LocalDateTime batDau);

    int suaLichHen(
            Object khachHang,
            Object thuCung,
            long goi,
            long nhanVien,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long id);
}
