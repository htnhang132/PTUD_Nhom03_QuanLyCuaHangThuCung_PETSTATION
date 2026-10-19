package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcLuuTruDAO;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Quản lý đặt phòng, hợp đồng lưu trú, chăm sóc và quyết toán tiền Hotel. */
public final class LuuTruService {
    /**
     * Đặt phòng trong LuuTruService.
     *
     * @param maThuCung mã thú cưng
     * @param maPhong mã phòng
     * @param batDau bắt đầu
     * @param ketThuc kết thúc
     * @param tienCoc tiền cọc
     * @param ghiChu ghi chú
     * @return kết quả đặt phòng
     */
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
        yeuCau(Quyen.HOTEL_MANAGE);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau) || tienCoc < 0)
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào và tiền cọc không âm.");
        if (!daNhanCoc)
            throw new IllegalArgumentException("Cần xác nhận đã nhận cọc 50% tiền phòng dự kiến.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Vùng đang xét.
                    long phien =
                            com.petstation.dao.impl.JdbcBanHangDAO.demThanhToan(layIdTaiKhoan());
                    if (phien == 0)
                        throw new IllegalArgumentException(
                                "Cần mở ca trước khi nhận cọc Hotel bằng tiền mặt.");
                    Object[] vungDangXet = layThuCung(maThuCung);
                    // Các phòng.
                    List<Object[]> cacPhong = JdbcLuuTruDAO.timDatPhong(maPhong);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacPhong.isEmpty()
                            || Set.of("INACTIVE", "MAINTENANCE", "CLEANING")
                                    .contains(chuyenThanhChuoi(cacPhong.get(0)[2])))
                        throw new IllegalArgumentException("Phòng không nhận đặt chỗ.");
                    // Phòng.
                    Object[] phong = cacPhong.get(0);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if ("Mèo".equals(phong[3]) && !"CAT".equals(vungDangXet[2]))
                        throw new IllegalArgumentException("Phòng Mèo chỉ dành cho mèo.");
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcLuuTruDAO.demDatPhong(phong[0], vungDangXet[0], ketThuc, batDau) > 0)
                        throw new IllegalArgumentException(
                                "Phòng hoặc thú cưng đã có đặt chỗ trùng thời gian.");
                    // Giá.
                    long gia = layGiaTri(phong[1]);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (tienCoc != (Math.multiplyExact(tinhSoNgay(batDau, ketThuc), gia) + 1) / 2)
                        throw new IllegalArgumentException(
                                "Tiền cọc phải bằng 50% tiền phòng dự kiến.");
                    // Kết quả.
                    long ketQua =
                            JdbcLuuTruDAO.themDatPhong(
                                    taoMa("HT"),
                                    vungDangXet[1],
                                    vungDangXet[0],
                                    phong[0],
                                    batDau,
                                    ketThuc,
                                    tienCoc,
                                    gia,
                                    ghiChu);
                    JdbcLuuTruDAO.xacNhanCoc(ketQua, layIdTaiKhoan(), phien);
                    NhatKyHeThongService.ghiNhatKy("Đặt phòng", maPhong + " / " + maThuCung);
                    return ketQua;
                });
    }

    /**
     * Kiểm tra vào trong LuuTruService.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     */
    public static long tinhCocDuKien(String maPhong, LocalDateTime batDau, LocalDateTime ketThuc) {
        yeuCau(Quyen.HOTEL_MANAGE);
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau))
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào.");
        var phong = JdbcLuuTruDAO.timDatPhong(maPhong);
        if (phong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy phòng.");
        return (Math.multiplyExact(tinhSoNgay(batDau, ketThuc), layGiaTri(phong.get(0)[1])) + 1)
                / 2;
    }

    public static void kiemTraVao(long hopDongLuuTru) {
        yeuCau(Quyen.HOTEL_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Phòng.
                    Object phong = JdbcLuuTruDAO.layNhanPhong(hopDongLuuTru);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (phong == null)
                        throw new IllegalArgumentException("Hợp đồng không còn chờ nhận phòng.");
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcLuuTruDAO.capNhatNhanPhong2(phong) != 1)
                        throw new IllegalArgumentException("Phòng chưa sẵn sàng.");
                    JdbcLuuTruDAO.capNhatNhanPhong(hopDongLuuTru);
                    NhatKyHeThongService.ghiNhatKy("Nhận phòng", chuyenThanhChuoi(hopDongLuuTru));
                    return null;
                });
    }

    /**
     * Ghi nhật ký trong LuuTruService.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @param anUong ăn uống
     * @param veSinh vệ sinh
     * @param sucKhoe sức khỏe
     */
    public static void ghiNhatKy(long hopDongLuuTru, String anUong, String veSinh, String sucKhoe) {
        yeuCau(Quyen.HOTEL_MANAGE);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (sucKhoe.isBlank()) throw new IllegalArgumentException("Cần ghi tình trạng thú cưng.");
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcLuuTruDAO.demNhatKy(hopDongLuuTru) == 0)
                        throw new IllegalArgumentException(
                                "Thú cưng chưa nhận phòng hoặc đã trả phòng.");
                    JdbcLuuTruDAO.capNhatNhatKy(
                            hopDongLuuTru, layIdNhanVien(), anUong, veSinh, sucKhoe);
                    NhatKyHeThongService.ghiNhatKy(
                            "Ghi nhật ký Hotel", chuyenThanhChuoi(hopDongLuuTru));
                    return null;
                });
    }

    /**
     * Tính số ngày trong LuuTruService.
     *
     * @param batDau bắt đầu
     * @param ketThuc kết thúc
     * @return kết quả tính số ngày
     */
    private static long tinhSoNgay(LocalDateTime batDau, LocalDateTime ketThuc) {
        return Math.max(1, (Duration.between(batDau, ketThuc).toMinutes() + 1439) / 1440);
    }

    /**
     * Tính tiền lưu trú còn lại trong LuuTruService.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @return kết quả tính tiền lưu trú còn lại
     */
    public static long tinhTienLuuTruConLai(long hopDongLuuTru) {
        // Các dòng.
        List<Object[]> cacDong = JdbcLuuTruDAO.timHotelConPhaiThu(hopDongLuuTru);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
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

    /**
     * Hoàn tất dọn phòng trong LuuTruService.
     *
     * @param id id
     */
    public static void hoanTatDonPhong(long id) {
        yeuCau(Quyen.HOTEL_MANAGE);
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcLuuTruDAO.danhDauPhongKhaDung(id) != 1)
                        throw new IllegalArgumentException("Chỉ áp dụng cho phòng đang dọn.");
                    NhatKyHeThongService.ghiNhatKy("Cập nhật phòng", "Hoàn thành dọn phòng");
                    return null;
                });
    }

    public static List<Object[]> layPhongDangDon() {
        yeuCau(Quyen.HOTEL_MANAGE);
        return JdbcLuuTruDAO.layPhongDangDon();
    }

    /**
     * Ghi sự kiện vào nhật ký hệ thống chi tiết trong LuuTruService.
     *
     * @param hopDongLuuTru hợp đồng lưu trú
     * @return kết quả lấy nhật ký chi tiết
     */
    public static List<Object[]> layNhatKyChiTiet(long hopDongLuuTru) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.HOTEL_MANAGE);
        return JdbcLuuTruDAO.layNhatKyChiTiet(hopDongLuuTru);
    }
}
