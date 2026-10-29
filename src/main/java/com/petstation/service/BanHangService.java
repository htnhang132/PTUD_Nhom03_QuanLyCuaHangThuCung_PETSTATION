package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.HoaDonDAO;
import com.petstation.dao.DoiHangDAO;
import com.petstation.dao.KhoanThuDAO;
import com.petstation.dao.impl.HoaDonDAOImpl;
import com.petstation.dao.impl.DoiHangDAOImpl;
import com.petstation.dao.impl.KhoanThuDAOImpl;
import com.petstation.model.DongThuTien;
import com.petstation.model.GioHang;
import com.petstation.model.PhuongThuc;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/**
 * Kiểm tra và lưu thanh toán, phân bổ tồn kho, điểm thưởng và hóa đơn dịch vụ trong cùng giao dịch.
 */
public final class BanHangService {
    private static final HoaDonDAO banHangDAO = new HoaDonDAOImpl();
    private static final DoiHangDAO doiHangDAO = new DoiHangDAOImpl();
    private static final KhoanThuDAO khoanThuDAO = new KhoanThuDAOImpl();

    private BanHangService() {}

    /** Nguồn và khoản đã cấn trừ vẫn đọc được khi in lại sau khi khởi động ứng dụng. */
    public record NguonBienLai(String loai, String ma, long daCanTru, String hoaDonGoc) {}

    public static NguonBienLai layNguonBienLai(String soHoaDon) {
        layPhien();
        var dong = banHangDAO.layNguonBienLai(soHoaDon);
        if (dong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hóa đơn.");
        Object[] d = dong.get(0);
        return new NguonBienLai(
                chuyenThanhChuoi(d[0]),
                chuyenThanhChuoi(d[1]),
                ((Number) d[2]).longValue(),
                chuyenThanhChuoi(d[3]));
    }

    public static GioHang.BienLai thanhToan(
            GioHang banHang, PhuongThuc phuongThuc, long daNhan, String thuNgan) {
        return thanhToan(
                banHang,
                List.of(
                        new DongThuTien(
                                phuongThuc,
                                banHang.tinhTong(),
                                phuongThuc == PhuongThuc.TIEN_MAT ? daNhan : banHang.tinhTong(),
                                banHang.kiemTraDaNhanTien(phuongThuc))),
                thuNgan);
    }

    public static GioHang.BienLai thanhToan(
            GioHang banHang, List<DongThuTien> cacKhoanThu, String thuNgan) {
        yeuCau(Quyen.THANH_TOAN);
        final List<DongThuTien> khoanThu = List.copyOf(cacKhoanThu);
        return thucHienGiaoDich(
                ketNoi -> {
                    // Hiện có.
                    Object hienCo = banHangDAO.timHoaDonTheoKhoaThu(banHang.layKhoaThanhToan());
                    if (hienCo != null) return layBienLai(chuyenThanhChuoi(hienCo));
                    if (banHang.kiemTraTrong())
                        throw new IllegalArgumentException("Hóa đơn chưa có hàng.");
                    // Ca làm việc.
                    long caLamViec = banHangDAO.demThanhToan(layIdTaiKhoan());
                    if (caLamViec == 0)
                        throw new IllegalStateException("Cần mở ca trước khi thanh toán.");
                    // Khóa ca thu ngân để xử lý tuần tự các yêu cầu đồng thời từ cùng tài khoản.
                    hienCo = banHangDAO.timHoaDonTheoKhoaThu(banHang.layKhoaThanhToan());
                    if (hienCo != null) return layBienLai(chuyenThanhChuoi(hienCo));
                    if (banHangDAO.demNgayDaChot() > 0)
                        throw new IllegalStateException("Ngày đã chốt, không thể ghi giao dịch.");
                    // Khách hàng.
                    long phieuDoi =
                            "EXCHANGE".equals(banHang.layKieuNguon())
                                    ? DoiTraService.chuanBi(banHang)
                                    : 0;
                    Long khachHang =
                            banHang.layMaKhachHang().isBlank()
                                    ? null
                                    : DanhMucService.layId(
                                            "khach_hang",
                                            "ma_khach_hang",
                                            banHang.layMaKhachHang());
                    if (khachHang != null) {
                        // Các khách hàng.
                        List<Object[]> cacKhachHang = banHangDAO.timThanhToan(khachHang);
                        if (cacKhachHang.isEmpty() || !Boolean.TRUE.equals(cacKhachHang.get(0)[1]))
                            throw new IllegalArgumentException("Khách hàng đã ngừng hoạt động.");
                        if (((Number) cacKhachHang.get(0)[0]).longValue() < banHang.layDiemDaDung())
                            throw new IllegalArgumentException(
                                    "Điểm khách hàng đã thay đổi, hãy chọn lại số điểm.");
                    } else if (banHang.layDiemDaDung() > 0)
                        throw new IllegalArgumentException("Chưa chọn khách hàng để dùng điểm.");
                    if (!banHang.layMaVoucher().isBlank()) {
                        // Lỗi.
                        String loi = banHang.apDungVoucher(banHang.layMaVoucher());
                        if (loi != null) throw new IllegalArgumentException(loi);
                        if (banHangDAO.ghiNhanSuDungVoucher(
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
                    DongThuTien.kiemTra(khoanThu, tong);
                    PhuongThuc phuongThuc =
                            khoanThu.size() == 1
                                    ? khoanThu.get(0).phuongThuc()
                                    : PhuongThuc.KET_HOP;
                    long daNhan = khoanThu.stream().mapToLong(DongThuTien::khachDua).sum();
                    long tienThua = khoanThu.stream().mapToLong(DongThuTien::tienThua).sum();
                    String khong = taoMa("HD");
                    // Hiện tại.
                    LocalDateTime hienTai = LocalDateTime.now();
                    // Được tích.
                    long duocTich =
                            khachHang == null
                                    ? 0
                                    : phieuDoi > 0
                                            ? DoiTraService.tinhDiemCong(banHang)
                                            : tong / CaiDatService.laySoTienChoMoiDiemTichLuy();
                    // Hóa đơn (hợp nhất đơn hàng).
                    long hoaDon =
                            banHangDAO.themHoaDon(
                                    khong,
                                    caLamViec,
                                    tong,
                                    phuongThuc.name(),
                                    daNhan,
                                    tienThua,
                                    duocTich,
                                    banHang.layKhoaThanhToan(),
                                    hienTai,
                                    banHang.tinhTamTinh(),
                                    banHang.tinhGiamGia(),
                                    banHang.layDiemDaDung(),
                                    banHang.layMaVoucher(),
                                    khachHang,
                                    layIdTaiKhoan());
                    for (GioHang.DongHang dong : banHang.layCacDong()) {
                        if (dong.soLuong() <= 0 || dong.donGia() < 0)
                            throw new IllegalArgumentException(
                                    "Số lượng hoặc đơn giá không hợp lệ.");
                        // Sản phẩm.
                        Long sanPham = null;
                        if (banHang.layKieuNguon().isBlank()
                                || "EXCHANGE".equals(banHang.layKieuNguon())) {
                            // Các sản phẩm.
                            List<Object[]> cacSanPham = banHangDAO.khoaSanPhamDeBan(dong.ma());
                            if (cacSanPham.isEmpty() || !"SELLING".equals(cacSanPham.get(0)[2]))
                                throw new IllegalArgumentException(
                                        "Sản phẩm " + dong.ma() + " không còn bán.");
                            sanPham = ((Number) cacSanPham.get(0)[0]).longValue();
                            if (((Number) cacSanPham.get(0)[1]).longValue() != dong.donGia())
                                throw new IllegalArgumentException(
                                        "Giá sản phẩm "
                                                + dong.ma()
                                                + " đã đổi. Hãy nạp lại giỏ hàng.");
                        }
                        // Mục.
                        long muc =
                                banHangDAO.themDongDonHang(
                                        hoaDon,
                                        sanPham,
                                        sanPham == null ? dong.ma() : null,
                                        dong.ten(),
                                        dong.soLuong(),
                                        dong.donGia());
                        if (sanPham != null) {
                            // Còn lại.
                            int conLai = dong.soLuong();
                            for (Object[] loHang : banHangDAO.layLoTheoFEFO(sanPham)) {
                                // Số lượng.
                                int soLuong = Math.min(conLai, ((Number) loHang[1]).intValue());
                                if (soLuong <= 0) continue;
                                if (banHangDAO.truTonLo(soLuong, loHang[0], soLuong) != 1)
                                    throw new IllegalStateException("Tồn kho vừa thay đổi.");
                                banHangDAO.ghiPhanBoLo(muc, loHang[0], soLuong);
                                conLai -= soLuong;
                                if (conLai == 0) break;
                            }
                            if (conLai > 0)
                                throw new IllegalArgumentException(
                                        "Không đủ tồn kho còn hạn cho " + dong.ten() + ".");
                        }
                    }
                    if (khachHang != null) {
                        banHangDAO.capNhatThanhToan(banHang.layDiemDaDung(), duocTich, khachHang);
                        banHangDAO.ghiDiemThanhToan(
                                hoaDon, duocTich - banHang.layDiemDaDung(), khachHang);
                        capNhatHang(khachHang);
                    }
                    if ("SPA".equals(banHang.layKieuNguon())) {
                        // Số tiền.
                        long soTien = banHangDAO.layTienSpaChoThanhToan(banHang.layIdNguon());
                        if (soTien != banHang.tinhTamTinh())
                            throw new IllegalStateException(
                                    "Phiếu Spa chưa hoàn thành hoặc số tiền đã thay đổi.");
                        if (banHangDAO.ganHoaDonSpa(hoaDon, banHang.layIdNguon()) != 1)
                            throw new IllegalStateException("Phiếu Spa đã thanh toán.");
                    } else if ("HOTEL".equals(banHang.layKieuNguon())) {
                        // Dự kiến.
                        long duKien = LuuTruService.tinhTienLuuTruConLai(banHang.layIdNguon());
                        if (duKien != banHang.tinhTamTinh())
                            throw new IllegalStateException(
                                    "Quyết toán Hotel đã thay đổi, hãy nạp lại.");
                        if (banHangDAO.ganHoaDonHotel(hoaDon, banHang.layIdNguon()) != 1)
                            throw new IllegalStateException(
                                    "Hợp đồng đã trả phòng hoặc chưa check-in.");
                        banHangDAO.chuyenPhongChoDon(banHang.layIdNguon());
                    }
                    khoanThuDAO.ghi(
                            hoaDon,
                            null,
                            caLamViec,
                            layIdTaiKhoan(),
                            banHang.layKhoaThanhToan(),
                            khoanThu);
                    if (phieuDoi > 0) DoiTraService.hoanTat(phieuDoi, hoaDon);
                    NhatKyHeThongService.ghiNhatKy("Thanh toán", "Hóa đơn " + khong + ": " + tong);
                    return layBienLai(khong);
                });
    }

    static void capNhatHang(long khachHang) {
        // Chi tiêu.
        long chiTieu = banHangDAO.demCapNhatHang(khachHang);
        // Các hạng.
        long[] cacHang = CaiDatService.layHangChiTieu();
        // Các tên.
        String[] cacTen = {"Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
        // Hạng.
        int hang = 0;
        while (hang < cacHang.length && chiTieu >= cacHang[hang]) hang++;
        banHangDAO.capNhatCapNhatHang(cacTen[hang], khachHang);
    }

    public static GioHang.BienLai layBienLai(String khong) {
        List<Object[]> cacDong = banHangDAO.timBienLai(khong);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Không tìm thấy hóa đơn " + khong);
        // Dòng dữ liệu.
        Object[] dongDuLieu = cacDong.get(0);
        List<GioHang.DongHang> cacDong2 = new ArrayList<>();
        for (Object[] dongDangXet : banHangDAO.layDongBienLai(dongDuLieu[1]))
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
                PhuongThuc.docTuCSDL(chuyenThanhChuoi(dongDuLieu[5])),
                dongDuLieu[6] == null ? 0 : ((Number) dongDuLieu[6]).longValue(),
                dongDuLieu[7] == null ? 0 : ((Number) dongDuLieu[7]).longValue(),
                chuyenThanhChuoi(dongDuLieu[8]),
                chuyenThanhChuoi(dongDuLieu[9]),
                chuyenThanhChuoi(dongDuLieu[10]),
                khoanThuDAO.lay(khong));
    }

    public static long treo(GioHang banHang) {
        yeuCau(Quyen.BAN_HANG);
        if (banHang.kiemTraTrong()) throw new IllegalArgumentException("Giỏ hàng trống.");
        if (!banHang.layKieuNguon().isBlank())
            throw new IllegalArgumentException(
                    "Chỉ treo đơn bán sản phẩm; phiếu Spa/Hotel vẫn được giữ trong màn xử lý.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Id.
                    long id =
                            banHangDAO.themTreo(
                                    taoMa("TREO"),
                                    layIdTaiKhoan(),
                                    banHang.layMaKhachHang().isBlank()
                                            ? null
                                            : DanhMucService.layId(
                                                    "khach_hang",
                                                    "ma_khach_hang",
                                                    banHang.layMaKhachHang()));
                    for (GioHang.DongHang dong : banHang.layCacDong())
                        banHangDAO.capNhatTreo(
                                id, dong.ma(), dong.ten(), dong.donGia(), dong.soLuong());
                    return id;
                });
    }

    public static void moLai(long id, GioHang banHang) {
        yeuCau(Quyen.BAN_HANG);
        thucHienGiaoDich(
                ketNoi -> {
                    if (banHangDAO.demGioTreoCuaTaiKhoan(id, layIdTaiKhoan()) == 0)
                        throw new IllegalArgumentException("Không tìm thấy đơn treo.");
                    List<GioHang.DongHang> cacDong = new ArrayList<>();
                    for (Object[] dongDuLieu : banHangDAO.timMoLai(id))
                        cacDong.add(
                                new GioHang.DongHang(
                                        chuyenThanhChuoi(dongDuLieu[0]),
                                        chuyenThanhChuoi(dongDuLieu[1]),
                                        ((Number) dongDuLieu[2]).longValue(),
                                        ((Number) dongDuLieu[3]).intValue()));
                    if (cacDong.size() != banHangDAO.demMoLai(id))
                        throw new IllegalArgumentException("Đơn treo có sản phẩm đã ngừng bán.");
                    banHang.xoa();
                    banHang.datCacDong(cacDong);
                    // Các khách hàng.
                    List<Object[]> cacKhachHang = banHangDAO.layKhachCuaGioTreo(id);
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

    public static void xoaTreo(long id) {
        yeuCau(Quyen.BAN_HANG);
        thucHienGiaoDich(
                ketNoi -> {
                    if (banHangDAO.demXoaTreo(id, layIdTaiKhoan()) == 0)
                        throw new IllegalArgumentException("Không tìm thấy đơn treo.");
                    banHangDAO.capNhatXoaTreo(id);
                    banHangDAO.xoaGioHangTreo(id, layIdTaiKhoan());
                    return null;
                });
    }

    public static void quyetToan(String loai, long id, GioHang banHang) {
        List<GioHang.DongHang> cacDong = new ArrayList<>();
        // Khách hàng.
        Object khachHang;
        if (loai.equals("SPA")) {
            yeuCau(Quyen.QUAN_LY_SPA);
            if (banHangDAO.demQuyetToan(id) == 0)
                throw new IllegalArgumentException("Phiếu Spa chưa hoàn thành hoặc đã thanh toán.");
            for (Object[] dongDuLieu : banHangDAO.layDichVuSpaQuyetToan(id))
                cacDong.add(
                        new GioHang.DongHang(
                                chuyenThanhChuoi(dongDuLieu[0]),
                                chuyenThanhChuoi(dongDuLieu[1]),
                                layGiaTri(dongDuLieu[2]),
                                ((Number) dongDuLieu[3]).intValue()));
            khachHang = banHangDAO.layKhachCuaPhieuSpa(id);
        } else if (loai.equals("HOTEL")) {
            yeuCau(Quyen.QUAN_LY_LUU_TRU);
            // Còn phải thu.
            long conPhaiThu = LuuTruService.tinhTienLuuTruConLai(id);
            cacDong.add(
                    new GioHang.DongHang(
                            "HOTEL-ROOM", "Quyết toán Hotel (đã trừ cọc)", conPhaiThu, 1));
            khachHang = banHangDAO.layQuyetToan(id);
        } else throw new IllegalArgumentException("Nguồn thanh toán không hợp lệ.");
        banHang.xoa();
        banHang.datCacDong(cacDong);
        banHang.datNguon(loai, id);
        // Các khách hàng.
        List<Object[]> cacKhachHang = banHangDAO.timQuyetToan(khachHang);
        if (!cacKhachHang.isEmpty()) {
            // Dòng dữ liệu.
            Object[] dongDuLieu = cacKhachHang.get(0);
            banHang.datKhachHang(
                    chuyenThanhChuoi(dongDuLieu[0]),
                    chuyenThanhChuoi(dongDuLieu[1]),
                    layGiaTri(dongDuLieu[2]));
        }
    }

    static void huyHoaDon(long hoaDon) {
        // Các hóa đơn.
        List<Object[]> cacHoaDon = banHangDAO.timHuyHoaDon(hoaDon);
        if (cacHoaDon.isEmpty()) throw new IllegalArgumentException("Hóa đơn đã hủy.");
        if (doiHangDAO.laHoaDonThayThe(hoaDon))
            throw new IllegalArgumentException(
                    "Không thể hủy hóa đơn gắn với phiếu đổi hàng đã hoàn thành.");
        if (banHangDAO.demPhieuDoiTraGanHoaDon(hoaDon) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn có phiếu trả hàng, cần xử lý đổi trả thay vì hủy.");
        if (banHangDAO.demHuyHoaDon(hoaDon) > 0 || banHangDAO.demHopDongGanHoaDon(hoaDon) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn dịch vụ đã hoàn thành không thể hủy trực tiếp.");
        // Chiều cao.
        Object[] chieuCao = cacHoaDon.get(0);
        for (Object[] loHang : banHangDAO.layPhanBoCanHoanTon(chieuCao[0]))
            banHangDAO.capNhatHuyHoaDon(loHang[1], loHang[0]);
        if (chieuCao[4] != null) {
            // Cũ.
            long cu = banHangDAO.khoaDiemKhachHang(chieuCao[4]),
                    tiepTheo = Math.max(0, cu - layGiaTri(chieuCao[1]) + layGiaTri(chieuCao[2]));
            banHangDAO.hoanDiemKhachHang(tiepTheo, chieuCao[4]);
            banHangDAO.ghiDiemHuyHoaDon(chieuCao[4], hoaDon, tiepTheo - cu, tiepTheo);
        }
        if (!chuyenThanhChuoi(chieuCao[3]).isBlank()) banHangDAO.hoanLuotVoucher(chieuCao[3]);
        banHangDAO.danhDauHuyHoaDon(hoaDon);
        banHangDAO.danhDauHuyDonHang(chieuCao[0]);
        if (chieuCao[4] != null) BanHangService.capNhatHang(layGiaTri(chieuCao[4]));
        NhatKyHeThongService.ghiNhatKy("Hủy hóa đơn", chuyenThanhChuoi(hoaDon));
    }

    public static List<Object[]> layCoTheBanCacSanPham() {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.BAN_HANG);
        return banHangDAO.layCoTheBanCacSanPham();
    }

    public static com.petstation.model.ChiTietBienLai layChiTietBienLai(String soHoaDon) {
        layPhien();
        var bienLai = layBienLai(soHoaDon);
        var nguon = layNguonBienLai(soHoaDon);
        var dong = banHangDAO.layThongTinIn(soHoaDon).get(0);
        return new com.petstation.model.ChiTietBienLai(
                bienLai,
                CaiDatService.lay("TEN_CUA_HANG"),
                CaiDatService.lay("DIA_CHI_CUA_HANG"),
                CaiDatService.lay("SO_DIEN_THOAI_CUA_HANG"),
                chuyenThanhChuoi(dong[0]),
                ((Number) dong[1]).intValue(),
                dong[2] == null ? null : ((Number) dong[2]).longValue(),
                nguon.loai(),
                nguon.ma(),
                nguon.daCanTru(),
                nguon.hoaDonGoc());
    }
}
