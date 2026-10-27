package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.BanHangDAO;
import com.petstation.dao.KhoanThuDAO;
import com.petstation.dao.LuuTruDAO;
import com.petstation.dao.impl.BanHangImpl;
import com.petstation.dao.impl.KhoanThuImpl;
import com.petstation.dao.impl.LuuTruImpl;
import com.petstation.model.PhuongThuc;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Quản lý đặt phòng, hợp đồng lưu trú, chăm sóc và quyết toán tiền Hotel. */
public final class LuuTruService {
    private static final BanHangDAO banHangDAO = new BanHangImpl();
    private static final KhoanThuDAO khoanThuDAO = new KhoanThuImpl();
    private static final LuuTruDAO luuTruDAO = new LuuTruImpl();

    public static long datPhong(
            String maThuCung,
            String maPhong,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long tienCoc,
            String ghiChu) {
        return datPhong(maThuCung, maPhong, batDau, ketThuc, tienCoc, ghiChu, false);
    }

    /** Chỉ ghi hợp đồng khi nhân viên xác nhận đã nhận đủ cọc. */
    public static long datPhong(
            String maThuCung,
            String maPhong,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long tienCoc,
            String ghiChu,
            boolean daNhanCoc) {
        if (!daNhanCoc)
            throw new IllegalArgumentException("Cần xác nhận đã nhận cọc 50% tiền phòng dự kiến.");
        if (tienCoc != tinhCocDuKien(maPhong, batDau, ketThuc))
            throw new IllegalArgumentException("Tiền cọc phải bằng 50% tiền phòng dự kiến.");
        return thucHienGiaoDich(
                c -> {
                    long id = giuPhong(maThuCung, maPhong, batDau, ketThuc, ghiChu);
                    thuCoc(
                            id,
                            java.util.List.of(
                                    new com.petstation.model.DongThuTien(
                                            PhuongThuc.CASH, tienCoc, tienCoc, true)),
                            "COC-" + java.util.UUID.randomUUID());
                    return id;
                });
    }

    public static long giuPhong(
            String maThuCung,
            String maPhong,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String ghiChu) {
        yeuCau(Quyen.HOTEL_MANAGE);
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau))
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào.");
        return thucHienGiaoDich(
                c -> {
                    Object[] thu = layThuCung(maThuCung);
                    var phong = luuTruDAO.timDatPhong(maPhong);
                    if (phong.isEmpty()
                            || Set.of("INACTIVE", "MAINTENANCE", "CLEANING")
                                    .contains(chuyenThanhChuoi(phong.get(0)[2])))
                        throw new IllegalArgumentException("Phòng không nhận đặt chỗ.");
                    Object[] p = phong.get(0);
                    if ("Mèo".equals(p[3]) && !"CAT".equals(thu[2]))
                        throw new IllegalArgumentException("Phòng Mèo chỉ dành cho mèo.");
                    if (luuTruDAO.demDatPhong(p[0], thu[0], ketThuc, batDau) > 0)
                        throw new IllegalArgumentException(
                                "Phòng hoặc thú cưng đã có đặt chỗ trùng thời gian.");
                    long gia = layGiaTri(p[1]);
                    long yeuCau = (Math.multiplyExact(tinhSoNgay(batDau, ketThuc), gia) + 1) / 2;
                    long id =
                            luuTruDAO.themDatPhong(
                                    taoMa("HT"),
                                    thu[1],
                                    thu[0],
                                    p[0],
                                    batDau,
                                    ketThuc,
                                    0,
                                    gia,
                                    ghiChu);
                    luuTruDAO.datMucCoc(yeuCau, id);
                    NhatKyHeThongService.ghiNhatKy("Giữ phòng Hotel", maPhong + " / " + id);
                    return id;
                });
    }

    public static java.util.List<Object[]> layCoc(long id) {
        yeuCau(Quyen.HOTEL_MANAGE);
        return luuTruDAO.layLichSuThuCoc(id);
    }

    public static long[] layMucCoc(long id) {
        yeuCau(Quyen.HOTEL_MANAGE);
        var r = luuTruDAO.layMucCoc(id);
        if (r.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
        return new long[] {layGiaTri(r.get(0)[0]), layGiaTri(r.get(0)[1])};
    }

    public static void thuCoc(
            long id, java.util.List<com.petstation.model.DongThuTien> dong, String khoa) {
        yeuCau(Quyen.HOTEL_MANAGE);
        if (khoa == null || !khoa.matches("[A-Za-z0-9-]{1,90}"))
            throw new IllegalArgumentException("Thiếu khóa thu cọc.");
        thucHienGiaoDich(
                c -> {
                    var r = luuTruDAO.khoaHopDongThuCoc(id);
                    if (r.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
                    if (luuTruDAO.demKhoaThuCoc(id, khoa + "-%") > 0) return null;
                    if (!"BOOKED".equals(r.get(0)[0]))
                        throw new IllegalArgumentException(
                                "Chỉ nhận cọc khi hợp đồng đang chờ nhận phòng.");
                    long thu = 0;
                    for (var d : dong) thu = Math.addExact(thu, d.soTien());
                    com.petstation.model.DongThuTien.kiemTra(dong, thu);
                    if (thu <= 0 || thu > layGiaTri(r.get(0)[1]) - layGiaTri(r.get(0)[2]))
                        throw new IllegalArgumentException(
                                "Tiền thu phải dương và không vượt phần cọc còn thiếu.");
                    long ca = banHangDAO.demThanhToan(layIdTaiKhoan());
                    if (ca == 0) throw new IllegalArgumentException("Cần mở ca trước khi thu cọc.");
                    if (banHangDAO.demNgayDaChot() > 0)
                        throw new IllegalArgumentException("Ngày đã chốt.");
                    khoanThuDAO.ghi(null, id, ca, layIdTaiKhoan(), khoa, dong);
                    luuTruDAO.ghiNhanTongCoc(
                            Math.addExact(layGiaTri(r.get(0)[2]), thu), layIdTaiKhoan(), ca, id);
                    NhatKyHeThongService.ghiNhatKy("Thu cọc Hotel", id + " / " + thu);
                    return null;
                });
    }

    public static void dieuChinhCoc(long id, long muc, String lyDo) {
        yeuCau(Quyen.HOTEL_MANAGE);
        if (layPhien().docVaiTro() != com.petstation.model.VaiTro.STORE_MANAGER)
            throw new IllegalArgumentException("Chỉ quản lý được điều chỉnh mức cọc.");
        if (lyDo == null || lyDo.isBlank() || lyDo.length() > 1000)
            throw new IllegalArgumentException("Cần lý do điều chỉnh.");
        thucHienGiaoDich(
                c -> {
                    var r = luuTruDAO.khoaHopDongDoiCoc(id);
                    if (r.isEmpty() || !"BOOKED".equals(r.get(0)[4]))
                        throw new IllegalArgumentException("Hợp đồng không còn chờ nhận phòng.");
                    Object[] d = r.get(0);
                    long tong =
                            Math.multiplyExact(
                                    tinhSoNgay(
                                            ((java.sql.Timestamp) d[0]).toLocalDateTime(),
                                            ((java.sql.Timestamp) d[1]).toLocalDateTime()),
                                    layGiaTri(d[2]));
                    if (muc < layGiaTri(d[3]) || muc < 0 || muc > tong)
                        throw new IllegalArgumentException(
                                "Mức cọc phải từ số đã thu đến tổng tiền phòng dự kiến.");
                    luuTruDAO.dieuChinhMucCoc(muc, lyDo, id);
                    NhatKyHeThongService.ghiNhatKy(
                            "Điều chỉnh cọc Hotel", id + " / " + muc + " / " + lyDo);
                    return null;
                });
    }

    public static long tinhCocDuKien(String maPhong, LocalDateTime batDau, LocalDateTime ketThuc) {
        yeuCau(Quyen.HOTEL_MANAGE);
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau))
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào.");
        var phong = luuTruDAO.timDatPhong(maPhong);
        if (phong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy phòng.");
        return (Math.multiplyExact(tinhSoNgay(batDau, ketThuc), layGiaTri(phong.get(0)[1])) + 1)
                / 2;
    }

    public static void kiemTraVao(long hopDongLuuTru) {
        yeuCau(Quyen.HOTEL_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Phòng.
                    Object phong = luuTruDAO.layNhanPhong(hopDongLuuTru);
                    if (phong == null)
                        throw new IllegalArgumentException("Hợp đồng không còn chờ nhận phòng.");
                    if (luuTruDAO.danhDauPhongDangO(phong) != 1)
                        throw new IllegalArgumentException("Phòng chưa sẵn sàng.");
                    luuTruDAO.capNhatNhanPhong(hopDongLuuTru);
                    NhatKyHeThongService.ghiNhatKy("Nhận phòng", chuyenThanhChuoi(hopDongLuuTru));
                    return null;
                });
    }

    public static void ghiNhatKy(long hopDongLuuTru, String anUong, String veSinh, String sucKhoe) {
        yeuCau(Quyen.HOTEL_MANAGE);
        if (sucKhoe.isBlank()) throw new IllegalArgumentException("Cần ghi tình trạng thú cưng.");
        thucHienGiaoDich(
                ketNoi -> {
                    if (luuTruDAO.demNhatKy(hopDongLuuTru) == 0)
                        throw new IllegalArgumentException(
                                "Thú cưng chưa nhận phòng hoặc đã trả phòng.");
                    luuTruDAO.capNhatNhatKy(
                            hopDongLuuTru, layIdNhanVien(), anUong, veSinh, sucKhoe);
                    NhatKyHeThongService.ghiNhatKy(
                            "Ghi nhật ký Hotel", chuyenThanhChuoi(hopDongLuuTru));
                    return null;
                });
    }

    private static long tinhSoNgay(LocalDateTime batDau, LocalDateTime ketThuc) {
        return Math.max(1, (Duration.between(batDau, ketThuc).toMinutes() + 1439) / 1440);
    }

    public static long tinhTienLuuTruConLai(long hopDongLuuTru) {
        List<Object[]> cacDong = luuTruDAO.timHotelConPhaiThu(hopDongLuuTru);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Hợp đồng chưa nhận phòng hoặc đã quyết toán.");
        // Dòng dữ liệu.
        Object[] dongDuLieu = cacDong.get(0);
        // Bắt đầu.
        LocalDateTime batDau = ((java.sql.Timestamp) dongDuLieu[0]).toLocalDateTime(),
                ketThuc = ((java.sql.Timestamp) dongDuLieu[1]).toLocalDateTime();
        // Trễ.
        long tre =
                LocalDateTime.now().isAfter(ketThuc)
                        ? tinhSoNgay(ketThuc, LocalDateTime.now())
                                * CaiDatService.layPhiTraPhongTreMoiNgay()
                        : 0;
        return Math.max(
                0,
                tinhSoNgay(batDau, ketThuc) * layGiaTri(dongDuLieu[2])
                        + tre
                        - layGiaTri(dongDuLieu[3]));
    }

    public static void hoanTatDonPhong(long id) {
        yeuCau(Quyen.HOTEL_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    if (luuTruDAO.danhDauPhongKhaDung(id) != 1)
                        throw new IllegalArgumentException("Chỉ áp dụng cho phòng đang dọn.");
                    NhatKyHeThongService.ghiNhatKy("Cập nhật phòng", "Hoàn thành dọn phòng");
                    return null;
                });
    }

    public static List<Object[]> layPhongDangDon() {
        yeuCau(Quyen.HOTEL_MANAGE);
        return luuTruDAO.layPhongDangDon();
    }

    public static List<Object[]> layNhatKyChiTiet(long hopDongLuuTru) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.HOTEL_MANAGE);
        return luuTruDAO.layNhatKyChiTiet(hopDongLuuTru);
    }
}
