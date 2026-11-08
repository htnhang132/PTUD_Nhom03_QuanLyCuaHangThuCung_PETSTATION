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

public final class BanHangService {
    private static HoaDonDAO banHangDAO = new HoaDonDAOImpl();
    private static DoiHangDAO doiHangDAO = new DoiHangDAOImpl();
    private static KhoanThuDAO khoanThuDAO = new KhoanThuDAOImpl();

    private BanHangService() {}

    public static void cauHinhDAO(HoaDonDAO hoaDon, DoiHangDAO doiHang, KhoanThuDAO khoanThu) {
        if (hoaDon != null) banHangDAO = hoaDon;
        if (doiHang != null) doiHangDAO = doiHang;
        if (khoanThu != null) khoanThuDAO = khoanThu;
    }

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
                    Object hienCo = banHangDAO.timHoaDonTheoKhoaThu(banHang.getKhoaThanhToan());
                    if (hienCo != null) return layBienLai(chuyenThanhChuoi(hienCo));
                    if (banHang.kiemTraTrong())
                        throw new IllegalArgumentException("Hóa đơn chưa có hàng.");

                    long caLamViec = banHangDAO.demThanhToan(layTenDangNhap());
                    if (caLamViec == 0) caLamViec = banHangDAO.demThanhToan(layIdTaiKhoan());
                    if (caLamViec == 0)
                        throw new IllegalStateException("Cần mở ca trước khi thanh toán.");

                    hienCo = banHangDAO.timHoaDonTheoKhoaThu(banHang.getKhoaThanhToan());
                    if (hienCo != null) return layBienLai(chuyenThanhChuoi(hienCo));
                    if (banHangDAO.demNgayDaChot() > 0)
                        throw new IllegalStateException("Ngày đã chốt, không thể ghi giao dịch.");

                    long phieuDoi =
                            "DOI_HANG".equals(banHang.getKieuNguon())
                                    ? DoiTraService.chuanBi(banHang)
                                    : 0;
                    Long khachHang =
                            banHang.getMaKhachHang().isBlank()
                                    ? null
                                    : DanhMucService.layId(
                                            "khach_hang",
                                            "ma_khach_hang",
                                            banHang.getMaKhachHang());
                    if (khachHang != null) {
                        List<Object[]> cacKhachHang = banHangDAO.timThanhToan(khachHang);
                        Object ttKhach = cacKhachHang.isEmpty() ? null : cacKhachHang.get(0)[1];
                        boolean dangHoatDong =
                                Boolean.TRUE.equals(ttKhach)
                                        || (ttKhach instanceof Number n && n.intValue() != 0);
                        if (!dangHoatDong)
                            throw new IllegalArgumentException("Khách hàng đã ngừng hoạt động.");
                        if (((Number) cacKhachHang.get(0)[0]).longValue() < banHang.getDiemDaDung())
                            throw new IllegalArgumentException(
                                    "Điểm khách hàng đã thay đổi, hãy chọn lại số điểm.");
                    } else if (banHang.getDiemDaDung() > 0)
                        throw new IllegalArgumentException("Chưa chọn khách hàng để dùng điểm.");
                    if (!banHang.getMaVoucher().isBlank()) {
                        String loi = banHang.apDungVoucher(banHang.getMaVoucher());
                        if (loi != null) throw new IllegalArgumentException(loi);
                        if (banHangDAO.ghiNhanSuDungVoucher(
                                        banHang.getMaVoucher(),
                                        banHang.tinhTamTinh(),
                                        khachHang,
                                        banHang.getKieuNguon())
                                != 1)
                            throw new IllegalArgumentException(
                                    "Voucher đã hết hạn, hết lượt hoặc không áp dụng cho hóa đơn"
                                            + " này.");
                    }

                    long tong = banHang.tinhTong();
                    DongThuTien.kiemTra(khoanThu, tong);
                    PhuongThuc phuongThuc =
                            khoanThu.size() == 1
                                    ? khoanThu.get(0).getPhuongThuc()
                                    : PhuongThuc.KET_HOP;
                    long daNhan = khoanThu.stream().mapToLong(DongThuTien::getKhachDua).sum();
                    long tienThua = khoanThu.stream().mapToLong(DongThuTien::tienThua).sum();
                    String khong = taoMa("HD");

                    LocalDateTime hienTai = LocalDateTime.now();

                    long duocTich =
                            khachHang == null
                                    ? 0
                                    : phieuDoi > 0
                                            ? DoiTraService.tinhDiemCong(banHang)
                                            : tong / CaiDatService.laySoTienChoMoiDiemTichLuy();

                    long hoaDon =
                            banHangDAO.themHoaDon(
                                    khong,
                                    caLamViec,
                                    tong,
                                    phuongThuc.name(),
                                    daNhan,
                                    tienThua,
                                    duocTich,
                                    banHang.getKhoaThanhToan(),
                                    hienTai,
                                    banHang.tinhTamTinh(),
                                    banHang.tinhGiamGia(),
                                    banHang.getDiemDaDung(),
                                    banHang.getMaVoucher(),
                                    khachHang,
                                    layIdTaiKhoan());
                    for (GioHang.DongHang dong : banHang.getCacDong()) {
                        if (dong.getSoLuong() <= 0 || dong.getDonGia() < 0)
                            throw new IllegalArgumentException(
                                    "Số lượng hoặc đơn giá không hợp lệ.");

                        Long sanPham = null;
                        if (banHang.getKieuNguon().isBlank()
                                || "DOI_HANG".equals(banHang.getKieuNguon())) {
                            List<Object[]> cacSanPham = banHangDAO.khoaSanPhamDeBan(dong.getMa());
                            if (cacSanPham.isEmpty() || !"DANG_BAN".equals(cacSanPham.get(0)[2]))
                                throw new IllegalArgumentException(
                                        "Sản phẩm " + dong.getMa() + " không còn bán.");
                            sanPham = layIdCauNoi(null, chuyenThanhChuoi(cacSanPham.get(0)[0]));
                            if (((Number) cacSanPham.get(0)[1]).longValue() != dong.getDonGia())
                                throw new IllegalArgumentException(
                                        "Giá sản phẩm "
                                                + dong.getMa()
                                                + " đã đổi. Hãy nạp lại giỏ hàng.");
                        }

                        long muc =
                                banHangDAO.themDongDonHang(
                                        hoaDon,
                                        sanPham,
                                        sanPham == null ? dong.getMa() : null,
                                        dong.getTen(),
                                        dong.getSoLuong(),
                                        dong.getDonGia());
                        if (sanPham != null) {
                            int conLai = dong.getSoLuong();
                            for (Object[] loHang : banHangDAO.layLoTheoFEFO(sanPham)) {
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
                                        "Không đủ tồn kho còn hạn cho " + dong.getTen() + ".");
                        }
                    }
                    if (khachHang != null) {
                        banHangDAO.capNhatThanhToan(banHang.getDiemDaDung(), duocTich, khachHang);
                        banHangDAO.ghiDiemThanhToan(
                                hoaDon, duocTich - banHang.getDiemDaDung(), khachHang);
                        capNhatHang(khachHang);
                    }
                    if ("SPA".equals(banHang.getKieuNguon())) {
                        long soTien = banHangDAO.layTienSpaChoThanhToan(banHang.getIdNguon());
                        if (soTien != banHang.tinhTamTinh())
                            throw new IllegalStateException(
                                    "Phiếu Spa chưa hoàn thành hoặc số tiền đã thay đổi.");
                        if (banHangDAO.ganHoaDonSpa(hoaDon, banHang.getIdNguon()) != 1)
                            throw new IllegalStateException("Phiếu Spa đã thanh toán.");
                    } else if ("HOTEL".equals(banHang.getKieuNguon()) || "LUU_TRU".equals(banHang.getKieuNguon())) {
                        long duKien = LuuTruService.tinhTienLuuTruConLai(banHang.getIdNguon());
                        if (duKien != banHang.tinhTamTinh())
                            throw new IllegalStateException(
                                    "Quyết toán Hotel đã thay đổi, hãy nạp lại.");
                        if (banHangDAO.ganHoaDonHotel(hoaDon, banHang.getIdNguon()) != 1)
                            throw new IllegalStateException(
                                    "Hợp đồng đã trả phòng hoặc chưa check-in.");
                        banHangDAO.chuyenPhongChoDon(banHang.getIdNguon());
                    }
                    khoanThuDAO.ghi(
                            hoaDon,
                            null,
                            caLamViec,
                            layIdTaiKhoan(),
                            banHang.getKhoaThanhToan(),
                            khoanThu);
                    if (phieuDoi > 0) DoiTraService.hoanTat(phieuDoi, hoaDon);
                    NhatKyHeThongService.ghiNhatKy("Thanh toán", "Hóa đơn " + khong + ": " + tong);
                    return layBienLai(khong);
                });
    }

    static void capNhatHang(long khachHang) {
        long chiTieu = banHangDAO.demCapNhatHang(khachHang);

        long[] cacHang = CaiDatService.layHangChiTieu();

        String[] cacTen = {"Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương"};

        int hang = 0;
        while (hang < cacHang.length && chiTieu >= cacHang[hang]) hang++;
        banHangDAO.capNhatCapNhatHang(cacTen[Math.min(hang, cacTen.length - 1)], khachHang);
    }

    public static GioHang.BienLai layBienLai(String khong) {
        List<Object[]> cacDong = banHangDAO.timBienLai(khong);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Không tìm thấy hóa đơn " + khong);

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
        if ("DOI_HANG".equals(banHang.getKieuNguon())) return DonDoiTreoService.treo(banHang);
        if (banHang.kiemTraTrong()) throw new IllegalArgumentException("Giỏ hàng trống.");
        if (!banHang.getKieuNguon().isBlank())
            throw new IllegalArgumentException(
                    "Chỉ treo đơn bán sản phẩm; phiếu Spa/Hotel vẫn được giữ trong màn xử lý.");
        return thucHienGiaoDich(
                ketNoi -> {
                    long id =
                            banHangDAO.themTreo(
                                    taoMa("TREO"),
                                    layIdTaiKhoan(),
                                    banHang.getMaKhachHang().isBlank()
                                            ? null
                                            : DanhMucService.layId(
                                                    "khach_hang",
                                                    "ma_khach_hang",
                                                    banHang.getMaKhachHang()));
                    for (GioHang.DongHang dong : banHang.getCacDong())
                        banHangDAO.capNhatTreo(
                                id, dong.getMa(), dong.getTen(), dong.getDonGia(), dong.getSoLuong());
                    return id;
                });
    }

    public static void moLai(long id, GioHang banHang) {
        yeuCau(Quyen.BAN_HANG);
        if (!banHang.kiemTraTrong())
            throw new IllegalArgumentException("Hãy treo hoặc thanh toán giỏ hiện tại trước.");
        thucHienGiaoDich(
                ketNoi -> {
                    if (banHangDAO.demGioTreoCuaTaiKhoan(id, layIdTaiKhoan()) == 0)
                        throw new IllegalArgumentException("Không tìm thấy đơn treo.");
                    if (DonDoiTreoService.lay(id) != null)
                        throw new IllegalArgumentException("Hãy mở đơn đổi tại màn Đơn đang treo để tiếp tục chọn phụ kiện.");
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
                    banHang.setCacDong(cacDong);

                    List<Object[]> cacKhachHang = banHangDAO.layKhachCuaGioTreo(id);
                    if (!cacKhachHang.isEmpty()) {
                        Object[] khoaDangXet = cacKhachHang.get(0);
                        banHang.setKhachHang(
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
        } else if (loai.equals("HOTEL") || loai.equals("LUU_TRU")) {
            yeuCau(Quyen.QUAN_LY_LUU_TRU);

            long conPhaiThu = LuuTruService.tinhTienLuuTruConLai(id);
            cacDong.add(
                    new GioHang.DongHang(
                            "HOTEL-ROOM", "Quyết toán Hotel (đã trừ cọc)", conPhaiThu, 1));
            khachHang = banHangDAO.layQuyetToan(id);
        } else throw new IllegalArgumentException("Nguồn thanh toán không hợp lệ.");
        banHang.xoa();
        banHang.setCacDong(cacDong);
        banHang.setNguon(loai, id);

        List<Object[]> cacKhachHang = banHangDAO.timQuyetToan(khachHang);
        if (!cacKhachHang.isEmpty()) {
            Object[] dongDuLieu = cacKhachHang.get(0);
            banHang.setKhachHang(
                    chuyenThanhChuoi(dongDuLieu[0]),
                    chuyenThanhChuoi(dongDuLieu[1]),
                    layGiaTri(dongDuLieu[2]));
        }
    }

    static void huyHoaDon(long hoaDon) {
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

        Object[] chieuCao = cacHoaDon.get(0);
        for (Object[] loHang : banHangDAO.layPhanBoCanHoanTon(chieuCao[0]))
            banHangDAO.capNhatHuyHoaDon(loHang[1], loHang[0]);
        if (chieuCao[4] != null) {
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
        var dsThongTin = banHangDAO.layThongTinIn(soHoaDon);
        if (dsThongTin.isEmpty())
            throw new IllegalArgumentException("Không tìm thấy thông tin in hóa đơn: " + soHoaDon);
        var dong = dsThongTin.get(0);
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
