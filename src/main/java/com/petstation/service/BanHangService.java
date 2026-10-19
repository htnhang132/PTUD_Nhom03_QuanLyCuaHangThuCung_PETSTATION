package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcBanHangDAO;
import com.petstation.model.GioHang;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/**
 * Kiểm tra và lưu thanh toán, phân bổ tồn kho, điểm thưởng và hóa đơn dịch vụ trong cùng giao dịch.
 */
public final class BanHangService {
    /** Khởi tạo BanHangService với các giá trị được cung cấp. */
    private BanHangService() {}

    /**
     * Thanh toán trong BanHangService.
     *
     * @param banHang bán hàng
     * @param phuongThuc phương thức
     * @param daNhan đã nhận
     * @param thuNgan thu ngân
     * @return kết quả thanh toán
     */
    public static GioHang.BienLai thanhToan(
            GioHang banHang, CaThuNganService.PhuongThuc phuongThuc, long daNhan, String thuNgan) {
        yeuCau(Quyen.PAYMENT_PROCESS);
        return thucHienGiaoDich(
                ketNoi -> {
                    // Hiện có.
                    Object hienCo = JdbcBanHangDAO.layThanhToan(banHang.layKhoaThanhToan());
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (hienCo != null) return layBienLai(chuyenThanhChuoi(hienCo));
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (banHang.kiemTraTrong())
                        throw new IllegalArgumentException("Hóa đơn chưa có hàng.");
                    // Ca làm việc.
                    long caLamViec = JdbcBanHangDAO.demThanhToan(layIdTaiKhoan());
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (caLamViec == 0)
                        throw new IllegalStateException("Cần mở ca trước khi thanh toán.");
                    // Khóa ca thu ngân để xử lý tuần tự các yêu cầu đồng thời từ cùng tài khoản.
                    hienCo = JdbcBanHangDAO.layThanhToan2(banHang.layKhoaThanhToan());
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (hienCo != null) return layBienLai(chuyenThanhChuoi(hienCo));
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcBanHangDAO.demThanhToan2() > 0)
                        throw new IllegalStateException("Ngày đã chốt, không thể ghi giao dịch.");
                    // Khách hàng.
                    Long khachHang =
                            banHang.layMaKhachHang().isBlank()
                                    ? null
                                    : DanhMucService.layId(
                                            "khach_hang",
                                            "ma_khach_hang",
                                            banHang.layMaKhachHang());
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (khachHang != null) {
                        // Các khách hàng.
                        List<Object[]> cacKhachHang = JdbcBanHangDAO.timThanhToan(khachHang);
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (cacKhachHang.isEmpty() || !Boolean.TRUE.equals(cacKhachHang.get(0)[1]))
                            throw new IllegalArgumentException("Khách hàng đã ngừng hoạt động.");
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (((Number) cacKhachHang.get(0)[0]).longValue() < banHang.layDiemDaDung())
                            throw new IllegalArgumentException(
                                    "Điểm khách hàng đã thay đổi, hãy chọn lại số điểm.");
                    } else // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (banHang.layDiemDaDung() > 0)
                        throw new IllegalArgumentException("Chưa chọn khách hàng để dùng điểm.");
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (!banHang.layMaVoucher().isBlank()) {
                        // Lỗi.
                        String loi = banHang.apDungVoucher(banHang.layMaVoucher());
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (loi != null) throw new IllegalArgumentException(loi);
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (JdbcBanHangDAO.capNhatThanhToan2(
                                        banHang.layMaVoucher(),
                                        banHang.tinhTamTinh(),
                                        khachHang,
                                        banHang.layKieuNguon())
                                != 1)
                            throw new IllegalArgumentException(
                                    "Voucher đã hết hạn, hết lượt hoặc không áp dụng cho hóa đơn"
                                            + " này.");
                    }
                    // Tổng.
                    long tong = banHang.tinhTong();
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (tong < 0 || phuongThuc == CaThuNganService.PhuongThuc.CASH && daNhan < tong)
                        throw new IllegalArgumentException("Tiền khách đưa chưa đủ.");
                    // Không.
                    String khong = taoMa("HD");
                    // Hiện tại.
                    LocalDateTime hienTai = LocalDateTime.now();
                    // Đơn hàng.
                    long donHang =
                            JdbcBanHangDAO.themThanhToan(
                                    taoMa("DH"), caLamViec, khachHang, hienTai);
                    for (GioHang.DongHang dong : banHang.layCacDong()) {
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (dong.soLuong() <= 0 || dong.donGia() < 0)
                            throw new IllegalArgumentException(
                                    "Số lượng hoặc đơn giá không hợp lệ.");
                        // Sản phẩm.
                        Long sanPham = null;
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (banHang.layKieuNguon().isBlank()) {
                            // Các sản phẩm.
                            List<Object[]> cacSanPham = JdbcBanHangDAO.timThanhToan2(dong.ma());
                            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                            if (cacSanPham.isEmpty() || !"SELLING".equals(cacSanPham.get(0)[2]))
                                throw new IllegalArgumentException(
                                        "Sản phẩm " + dong.ma() + " không còn bán.");
                            sanPham = ((Number) cacSanPham.get(0)[0]).longValue();
                            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                            if (((Number) cacSanPham.get(0)[1]).longValue() != dong.donGia())
                                throw new IllegalArgumentException(
                                        "Giá sản phẩm "
                                                + dong.ma()
                                                + " đã đổi. Hãy nạp lại giỏ hàng.");
                        }
                        // Mục.
                        long muc =
                                JdbcBanHangDAO.themThanhToan2(
                                        donHang,
                                        sanPham,
                                        sanPham == null ? dong.ma() : null,
                                        dong.ten(),
                                        dong.soLuong(),
                                        dong.donGia());
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (sanPham != null) {
                            // Còn lại.
                            int conLai = dong.soLuong();
                            for (Object[] loHang : JdbcBanHangDAO.timThanhToan3(sanPham)) {
                                // Số lượng.
                                int soLuong = Math.min(conLai, ((Number) loHang[1]).intValue());
                                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                                if (soLuong <= 0) continue;
                                // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                                if (JdbcBanHangDAO.capNhatThanhToan3(soLuong, loHang[0], soLuong)
                                        != 1)
                                    throw new IllegalStateException("Tồn kho vừa thay đổi.");
                                JdbcBanHangDAO.capNhatThanhToan4(muc, loHang[0], soLuong);
                                conLai -= soLuong;
                                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                                if (conLai == 0) break;
                            }
                            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                            if (conLai > 0)
                                throw new IllegalArgumentException(
                                        "Không đủ tồn kho còn hạn cho " + dong.ten() + ".");
                        }
                    }
                    // Được tích.
                    long duocTich =
                            khachHang == null
                                    ? 0
                                    : tong / CaiDatService.laySoTienChoMoiDiemTichLuy();
                    // Hóa đơn.
                    long hoaDon =
                            JdbcBanHangDAO.themThanhToan3(
                                    khong,
                                    donHang,
                                    tong,
                                    phuongThuc.name(),
                                    phuongThuc == CaThuNganService.PhuongThuc.CASH ? daNhan : tong,
                                    phuongThuc == CaThuNganService.PhuongThuc.CASH
                                            ? daNhan - tong
                                            : 0,
                                    duocTich,
                                    banHang.layKhoaThanhToan(),
                                    hienTai,
                                    banHang.tinhTamTinh(),
                                    banHang.tinhGiamGia(),
                                    banHang.layDiemDaDung(),
                                    banHang.layMaVoucher());
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (khachHang != null) {
                        JdbcBanHangDAO.capNhatThanhToan(
                                banHang.layDiemDaDung(), duocTich, khachHang);
                        JdbcBanHangDAO.capNhatThanhToan5(
                                hoaDon, duocTich - banHang.layDiemDaDung(), khachHang);
                        capNhatHang(khachHang);
                    }
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if ("SPA".equals(banHang.layKieuNguon())) {
                        // Số tiền.
                        long soTien = JdbcBanHangDAO.demThanhToan3(banHang.layIdNguon());
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (soTien != banHang.tinhTamTinh())
                            throw new IllegalStateException(
                                    "Phiếu Spa chưa hoàn thành hoặc số tiền đã thay đổi.");
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (JdbcBanHangDAO.capNhatThanhToan6(hoaDon, banHang.layIdNguon()) != 1)
                            throw new IllegalStateException("Phiếu Spa đã thanh toán.");
                    } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if ("HOTEL".equals(banHang.layKieuNguon())) {
                        // Dự kiến.
                        long duKien = LuuTruService.tinhTienLuuTruConLai(banHang.layIdNguon());
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (duKien != banHang.tinhTamTinh())
                            throw new IllegalStateException(
                                    "Quyết toán Hotel đã thay đổi, hãy nạp lại.");
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (JdbcBanHangDAO.capNhatThanhToan7(hoaDon, banHang.layIdNguon()) != 1)
                            throw new IllegalStateException(
                                    "Hợp đồng đã trả phòng hoặc chưa check-in.");
                        JdbcBanHangDAO.capNhatThanhToan8(banHang.layIdNguon());
                    }
                    NhatKyHeThongService.ghiNhatKy("Thanh toán", "Hóa đơn " + khong + ": " + tong);
                    return layBienLai(khong);
                });
    }

    /**
     * Cập nhật hạng trong BanHangService.
     *
     * @param khachHang khách hàng
     */
    static void capNhatHang(long khachHang) {
        // Chi tiêu.
        long chiTieu = JdbcBanHangDAO.demCapNhatHang(khachHang);
        // Các hạng.
        long[] cacHang = CaiDatService.layHangChiTieu();
        // Các tên.
        String[] cacTen = {"Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
        // Hạng.
        int hang = 0;
        while (hang < cacHang.length && chiTieu >= cacHang[hang]) hang++;
        JdbcBanHangDAO.capNhatCapNhatHang(cacTen[hang], khachHang);
    }

    /**
     * Lấy biên lai trong BanHangService.
     *
     * @param khong không
     * @return kết quả lấy biên lai
     */
    public static GioHang.BienLai layBienLai(String khong) {
        // Các dòng.
        List<Object[]> cacDong = JdbcBanHangDAO.timBienLai(khong);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Không tìm thấy hóa đơn " + khong);
        // Dòng dữ liệu.
        Object[] dongDuLieu = cacDong.get(0);
        // Các dòng.
        List<GioHang.DongHang> cacDong2 = new ArrayList<>();
        for (Object[] dongDangXet : JdbcBanHangDAO.timBienLai2(dongDuLieu[1]))
            cacDong2.add(
                    new GioHang.DongHang(
                            chuyenThanhChuoi(dongDangXet[0]),
                            chuyenThanhChuoi(dongDangXet[1]),
                            ((Number) dongDangXet[2]).longValue(),
                            ((Number) dongDangXet[3]).intValue()));
        return new GioHang.BienLai(
                khong,
                ((java.sql.Timestamp) dongDuLieu[0]).toLocalDateTime(),
                cacDong2,
                ((Number) dongDuLieu[2]).longValue(),
                ((Number) dongDuLieu[3]).longValue(),
                ((Number) dongDuLieu[4]).longValue(),
                CaThuNganService.PhuongThuc.valueOf(chuyenThanhChuoi(dongDuLieu[5])),
                dongDuLieu[6] == null ? 0 : ((Number) dongDuLieu[6]).longValue(),
                dongDuLieu[7] == null ? 0 : ((Number) dongDuLieu[7]).longValue(),
                chuyenThanhChuoi(dongDuLieu[8]),
                chuyenThanhChuoi(dongDuLieu[9]),
                chuyenThanhChuoi(dongDuLieu[10]));
    }

    /**
     * Treo trong BanHangService.
     *
     * @param banHang bán hàng
     * @return kết quả treo
     */
    public static long treo(GioHang banHang) {
        yeuCau(Quyen.POS_SELL);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (banHang.kiemTraTrong()) throw new IllegalArgumentException("Giỏ hàng trống.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Id.
                    long id =
                            JdbcBanHangDAO.themTreo(
                                    taoMa("GIO"),
                                    layIdTaiKhoan(),
                                    banHang.layMaKhachHang().isBlank()
                                            ? null
                                            : DanhMucService.layId(
                                                    "khach_hang",
                                                    "ma_khach_hang",
                                                    banHang.layMaKhachHang()));
                    for (GioHang.DongHang dong : banHang.layCacDong())
                        JdbcBanHangDAO.capNhatTreo(
                                id, dong.ma(), dong.ten(), dong.donGia(), dong.soLuong());
                    return id;
                });
    }

    /**
     * Mở lại trong BanHangService.
     *
     * @param id id
     * @param banHang bán hàng
     */
    public static void moLai(long id, GioHang banHang) {
        yeuCau(Quyen.POS_SELL);
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcBanHangDAO.demMoLai2(id, layIdTaiKhoan()) == 0)
                        throw new IllegalArgumentException("Không tìm thấy đơn treo của bạn.");
                    // Các dòng.
                    List<GioHang.DongHang> cacDong = new ArrayList<>();
                    for (Object[] dongDuLieu : JdbcBanHangDAO.timMoLai(id))
                        cacDong.add(
                                new GioHang.DongHang(
                                        chuyenThanhChuoi(dongDuLieu[0]),
                                        chuyenThanhChuoi(dongDuLieu[1]),
                                        ((Number) dongDuLieu[2]).longValue(),
                                        ((Number) dongDuLieu[3]).intValue()));
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacDong.size() != JdbcBanHangDAO.demMoLai(id))
                        throw new IllegalArgumentException("Đơn treo có sản phẩm đã ngừng bán.");
                    banHang.xoa();
                    banHang.datCacDong(cacDong);
                    // Các khách hàng.
                    List<Object[]> cacKhachHang = JdbcBanHangDAO.timMoLai2(id);
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (!cacKhachHang.isEmpty()) {
                        // Khóa đang xét.
                        Object[] khoaDangXet = cacKhachHang.get(0);
                        banHang.datKhachHang(
                                chuyenThanhChuoi(khoaDangXet[0]),
                                chuyenThanhChuoi(khoaDangXet[1]),
                                ((Number) khoaDangXet[2]).longValue());
                    }
                    xoaTreo(id);
                    return null;
                });
    }

    /**
     * Xóa treo trong BanHangService.
     *
     * @param id id
     */
    public static void xoaTreo(long id) {
        yeuCau(Quyen.POS_SELL);
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcBanHangDAO.demXoaTreo(id, layIdTaiKhoan()) == 0)
                        throw new IllegalArgumentException("Đơn treo không thuộc tài khoản này.");
                    JdbcBanHangDAO.capNhatXoaTreo(id);
                    JdbcBanHangDAO.capNhatXoaTreo2(id, layIdTaiKhoan());
                    return null;
                });
    }

    /**
     * Quyết toán trong BanHangService.
     *
     * @param loai loại
     * @param id id
     * @param banHang bán hàng
     */
    public static void quyetToan(String loai, long id, GioHang banHang) {
        // Các dòng.
        List<GioHang.DongHang> cacDong = new ArrayList<>();
        // Khách hàng.
        Object khachHang;
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (loai.equals("SPA")) {
            yeuCau(Quyen.SPA_MANAGE);
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (JdbcBanHangDAO.demQuyetToan(id) == 0)
                throw new IllegalArgumentException("Phiếu Spa chưa hoàn thành hoặc đã thanh toán.");
            for (Object[] dongDuLieu : JdbcBanHangDAO.timQuyetToan2(id))
                cacDong.add(
                        new GioHang.DongHang(
                                chuyenThanhChuoi(dongDuLieu[0]),
                                chuyenThanhChuoi(dongDuLieu[1]),
                                layGiaTri(dongDuLieu[2]),
                                ((Number) dongDuLieu[3]).intValue()));
            khachHang = JdbcBanHangDAO.layQuyetToan2(id);
        } else // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (loai.equals("HOTEL")) {
            yeuCau(Quyen.HOTEL_MANAGE);
            // Còn phải thu.
            long conPhaiThu = LuuTruService.tinhTienLuuTruConLai(id);
            cacDong.add(
                    new GioHang.DongHang(
                            "HOTEL-ROOM", "Quyết toán Hotel (đã trừ cọc)", conPhaiThu, 1));
            khachHang = JdbcBanHangDAO.layQuyetToan(id);
        } else throw new IllegalArgumentException("Nguồn thanh toán không hợp lệ.");
        banHang.xoa();
        banHang.datCacDong(cacDong);
        banHang.datNguon(loai, id);
        // Các khách hàng.
        List<Object[]> cacKhachHang = JdbcBanHangDAO.timQuyetToan(khachHang);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (!cacKhachHang.isEmpty()) {
            // Dòng dữ liệu.
            Object[] dongDuLieu = cacKhachHang.get(0);
            banHang.datKhachHang(
                    chuyenThanhChuoi(dongDuLieu[0]),
                    chuyenThanhChuoi(dongDuLieu[1]),
                    layGiaTri(dongDuLieu[2]));
        }
    }

    /**
     * Hủy hóa đơn trong BanHangService.
     *
     * @param hoaDon hóa đơn
     */
    static void huyHoaDon(long hoaDon) {
        // Các hóa đơn.
        List<Object[]> cacHoaDon = JdbcBanHangDAO.timHuyHoaDon(hoaDon);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (cacHoaDon.isEmpty()) throw new IllegalArgumentException("Hóa đơn đã hủy.");
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (JdbcBanHangDAO.demHuyHoaDon3(hoaDon) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn có phiếu trả hàng, cần xử lý đổi trả thay vì hủy.");
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (JdbcBanHangDAO.demHuyHoaDon(hoaDon) > 0 || JdbcBanHangDAO.demHuyHoaDon2(hoaDon) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn dịch vụ đã hoàn thành không thể hủy trực tiếp.");
        // Chiều cao.
        Object[] chieuCao = cacHoaDon.get(0);
        for (Object[] loHang : JdbcBanHangDAO.timHuyHoaDon2(chieuCao[0]))
            JdbcBanHangDAO.capNhatHuyHoaDon(loHang[1], loHang[0]);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (chieuCao[4] != null) {
            // Cũ.
            long cu = JdbcBanHangDAO.demHuyHoaDon4(chieuCao[4]),
                    tiepTheo = Math.max(0, cu - layGiaTri(chieuCao[1]) + layGiaTri(chieuCao[2]));
            JdbcBanHangDAO.capNhatHuyHoaDon2(tiepTheo, chieuCao[4]);
            JdbcBanHangDAO.capNhatHuyHoaDon5(chieuCao[4], hoaDon, tiepTheo - cu, tiepTheo);
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (!chuyenThanhChuoi(chieuCao[3]).isBlank()) JdbcBanHangDAO.capNhatHuyHoaDon6(chieuCao[3]);
        JdbcBanHangDAO.capNhatHuyHoaDon3(hoaDon);
        JdbcBanHangDAO.capNhatHuyHoaDon4(chieuCao[0]);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (chieuCao[4] != null) BanHangService.capNhatHang(layGiaTri(chieuCao[4]));
        NhatKyHeThongService.ghiNhatKy("Hủy hóa đơn", chuyenThanhChuoi(hoaDon));
    }

    /**
     * Lấy có thể bán các sản phẩm trong BanHangService.
     *
     * @return kết quả lấy có thể bán các sản phẩm
     */
    public static List<Object[]> layCoTheBanCacSanPham() {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.POS_SELL);
        return JdbcBanHangDAO.layCoTheBanCacSanPham();
    }
}
