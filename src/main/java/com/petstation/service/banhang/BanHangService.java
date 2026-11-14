package com.petstation.service.banhang;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;
import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.banhang.HoaDonDAO;
import com.petstation.dao.banhang.PhieuDoiTraDAO;
import com.petstation.dao.banhang.impl.HoaDonDAOImpl;
import com.petstation.dao.banhang.impl.PhieuDoiTraDAOImpl;
import com.petstation.dao.luutru.impl.HopDongLuuTruDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.ChiTietBienLai;
import com.petstation.dto.GioHang;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.HoaDon;
import com.petstation.model.banhang.PhieuDoiTra;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.khachhang.KhachHang;
import com.petstation.model.kho.LoHang;
import com.petstation.model.kho.SanPham;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.luutru.LuuTruService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class BanHangService {
    private final HoaDonDAO banHangDAO;
    private final PhieuDoiTraDAO doiHangDAO;
    private final DoiTraService doiTraService;
    private final LuuTruService luuTruService;

    public BanHangService() {
        this(new HoaDonDAOImpl(), new PhieuDoiTraDAOImpl());
    }

    public BanHangService(HoaDonDAO banHangDAO, PhieuDoiTraDAO doiHangDAO) {
        this.banHangDAO = Objects.requireNonNull(banHangDAO, "banHangDAO không được null");
        this.doiHangDAO = Objects.requireNonNull(doiHangDAO, "doiHangDAO không được null");
        this.doiTraService = new DoiTraService(this.doiHangDAO);
        this.luuTruService = new LuuTruService(this.banHangDAO, new HopDongLuuTruDAOImpl());
    }

    public static final class NguonBienLai {
        private final String loai;
        private final String ma;
        private final long daCanTru;
        private final String hoaDonGoc;

        public NguonBienLai(String loai, String ma, long daCanTru, String hoaDonGoc) {
            this.loai = loai;
            this.ma = ma;
            this.daCanTru = daCanTru;
            this.hoaDonGoc = hoaDonGoc;
        }

        public String loai() {
            return loai;
        }

        public String ma() {
            return ma;
        }

        public long daCanTru() {
            return daCanTru;
        }

        public String hoaDonGoc() {
            return hoaDonGoc;
        }
    }

    private static String layMaHangBienLai(ChiTietHoaDon ct) {
        if (ct == null) return "";
        if (ct.getSanPham() != null && ct.getSanPham().getMaSanPham() != null) {
            return chuyenThanhChuoi(ct.getSanPham().getMaSanPham());
        }
        if (ct.getPhieuSpa() != null) {
            if (ct.getPhieuSpa().getGoiDichVu() != null
                    && ct.getPhieuSpa().getGoiDichVu().getMaGoi() != null) {
                return chuyenThanhChuoi(ct.getPhieuSpa().getGoiDichVu().getMaGoi());
            }
            if (ct.getPhieuSpa().getMaPhieu() != null) {
                return chuyenThanhChuoi(ct.getPhieuSpa().getMaPhieu());
            }
        }
        if (ct.getHopDongLuuTru() != null && ct.getHopDongLuuTru().getMaHopDong() != null) {
            return chuyenThanhChuoi(ct.getHopDongLuuTru().getMaHopDong());
        }
        return "";
    }

    public NguonBienLai layNguonBienLai(String soHoaDon) {
        PhienDangNhapService.macDinh().layPhien();
        PhieuDoiTra nguon =
                banHangDAO
                        .layNguonBienLai(soHoaDon)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn."));
        return new NguonBienLai(
                chuyenThanhChuoi(nguon.getLoaiPhieu()),
                chuyenThanhChuoi(nguon.getMaPhieu()),
                nguon.getGiaTriDuocDoi(),
                nguon.getHoaDonGoc() != null
                        ? chuyenThanhChuoi(nguon.getHoaDonGoc().getMaHoaDon())
                        : "");
    }

    public GioHang.BienLai thanhToan(
            GioHang banHang, PhuongThuc phuongThuc, long daNhan, String thuNgan) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.THANH_TOAN);
        return QuanLyGiaoDich.macDinh()
                .thucThi(ketNoi -> thucHienThanhToan(banHang, phuongThuc, daNhan));
    }

    private GioHang.BienLai thucHienThanhToan(GioHang banHang, PhuongThuc phuongThuc, long daNhan) {
        Optional<String> hienCo = banHangDAO.timHoaDonTheoKhoaThu(banHang.getKhoaThanhToan());
        if (hienCo.isPresent()) return layBienLai(hienCo.get());
        if (banHang.kiemTraTrong()) throw new IllegalArgumentException("Hóa đơn chưa có hàng.");
        String maPhien = xacDinhCaDangMo();
        hienCo = banHangDAO.timHoaDonTheoKhoaThu(banHang.getKhoaThanhToan());
        if (hienCo.isPresent()) return layBienLai(hienCo.get());
        if (banHangDAO.demNgayDaChot() > 0)
            throw new IllegalStateException("Ngày đã chốt, không thể ghi giao dịch.");
        String phieuDoi =
                LoaiPhieuDoiTra.DOI_HANG.maDB().equals(banHang.getKieuNguon())
                        ? doiTraService.chuanBi(banHang)
                        : "";
        String maKhachHang = kiemTraKhachHangVaDiem(banHang);
        apDungVoucherNeuCo(banHang, maKhachHang);
        long tong = banHang.tinhTong();
        if (phuongThuc == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không hợp lệ.");
        }
        if (!banHang.kiemTraDaNhanTien(phuongThuc)) {
            throw new IllegalArgumentException("Cần xác nhận đã nhận tiền.");
        }
        long khachDua = phuongThuc == PhuongThuc.TIEN_MAT ? daNhan : tong;
        if (khachDua < tong) {
            throw new IllegalArgumentException("Số tiền khách đưa chưa đủ.");
        }
        long tienThua = Math.max(0L, khachDua - tong);
        String maHoaDonDuKien = banHang.getMaHoaDonDuKien();
        long duocTich = tinhDiemDuocTich(banHang, maKhachHang, !phieuDoi.isBlank(), tong);
        String maHoaDon =
                banHangDAO.themHoaDon(
                        maHoaDonDuKien,
                        maPhien,
                        PhienDangNhapService.macDinh().layMaNhanVien(),
                        tong,
                        phuongThuc,
                        khachDua,
                        tienThua,
                        duocTich,
                        banHang.getKhoaThanhToan(),
                        LocalDateTime.now(),
                        banHang.tinhTamTinh(),
                        banHang.tinhGiamGia(),
                        Math.toIntExact(banHang.getDiemDaDung()),
                        banHang.getMaVoucher(),
                        maKhachHang);
        ghiChiTietVaTruTon(banHang, maHoaDon);
        capNhatDiemVaHangKhach(banHang, maKhachHang, duocTich);
        doiSoatNguonHoaDon(banHang, maHoaDon);
        if (!phieuDoi.isBlank()) doiTraService.hoanTat(phieuDoi, maHoaDon);
        NhatKyHeThongService.macDinh().ghiNhatKy("Thanh toán", "Hóa đơn " + maHoaDon + ": " + tong);
        return layBienLai(maHoaDon);
    }

    private String xacDinhCaDangMo() {
        return banHangDAO
                .layMaPhienDangMo(PhienDangNhapService.macDinh().layMaNhanVien())
                .orElseThrow(() -> new IllegalArgumentException("Cần mở ca trước khi thanh toán."));
    }

    private String kiemTraKhachHangVaDiem(GioHang banHang) {
        if (banHang.getDiemDaDung() < 0)
            throw new IllegalArgumentException("Điểm sử dụng không hợp lệ.");
        if (banHang.getMaKhachHang().isBlank()) {
            if (banHang.getDiemDaDung() > 0)
                throw new IllegalArgumentException("Chưa chọn khách hàng để dùng điểm.");
            return null;
        }
        String maKhachHang = banHang.getMaKhachHang().trim();
        Optional<KhachHang> khTim = banHangDAO.timThanhToan(maKhachHang);
        if (khTim.isEmpty()) {
            if (banHang.getDiemDaDung() > 0)
                throw new IllegalArgumentException("Chưa chọn khách hàng để dùng điểm.");
            return null;
        }
        boolean dangHoatDong = khTim.map(KhachHang::isDangHoatDong).orElse(false);
        if (!dangHoatDong && !LoaiPhieuDoiTra.DOI_HANG.maDB().equals(banHang.getKieuNguon()))
            throw new IllegalArgumentException("Khách hàng đã ngừng hoạt động.");
        if (khTim.get().getDiemTichLuy() < banHang.getDiemDaDung())
            throw new IllegalArgumentException(
                    "Điểm khách hàng đã thay đổi, hãy chọn lại số điểm.");
        return maKhachHang;
    }

    private void apDungVoucherNeuCo(GioHang banHang, String maKhachHang) {
        if (banHang.getMaVoucher().isBlank()) return;
        String loi = banHang.apDungVoucher(banHang.getMaVoucher());
        if (loi != null) throw new IllegalArgumentException(loi);
        if (banHangDAO.ghiNhanSuDungVoucher(
                        banHang.getMaVoucher(),
                        banHang.tinhTamTinh(),
                        maKhachHang,
                        banHang.getKieuNguon())
                != 1) {
            throw new IllegalArgumentException(
                    "Voucher đã hết hạn, hết lượt hoặc không áp dụng cho hóa đơn này.");
        }
    }

    private long tinhDiemDuocTich(
            GioHang banHang, String maKhachHang, boolean laDoiHang, long tong) {
        if (maKhachHang == null) return 0;
        if (laDoiHang) return DoiTraService.tinhDiemCong(banHang);
        return tong / CaiDatService.macDinh().laySoTienChoMoiDiemTichLuy();
    }

    private void ghiChiTietVaTruTon(GioHang banHang, String maHoaDon) {
        boolean banHangHoa =
                banHang.getKieuNguon().isBlank()
                        || LoaiPhieuDoiTra.DOI_HANG.maDB().equals(banHang.getKieuNguon());
        int vatPhanTram = Math.max(0, CaiDatService.macDinh().layVATPhanTram());
        for (GioHang.DongHang dong : banHang.getCacDong()) {
            if (dong.getSoLuong() <= 0 || dong.getDonGia() < 0)
                throw new IllegalArgumentException("Số lượng hoặc đơn giá không hợp lệ.");
            String maSanPham = banHangHoa ? khoaVaKiemTraSanPham(dong) : null;
            banHangDAO.themDongDonHang(
                    maHoaDon,
                    maSanPham,
                    maSanPham == null ? dong.getMa() : null,
                    dong.getTen(),
                    dong.getSoLuong(),
                    dong.getDonGia(),
                    vatPhanTram);
            if (maSanPham != null) phanBoFEFO(maHoaDon, maSanPham, dong);
        }
    }

    private String khoaVaKiemTraSanPham(GioHang.DongHang dong) {
        Optional<SanPham> spTim = banHangDAO.khoaSanPhamDeBan(dong.getMa());
        if (spTim.isEmpty()
                || spTim.get().getTrangThaiEnum() == null
                || !spTim.get().getTrangThaiEnum().choPhepBan())
            throw new IllegalArgumentException("Sản phẩm " + dong.getMa() + " không còn bán.");
        SanPham sp = spTim.get();
        if (sp.getGia() != dong.getDonGia())
            throw new IllegalArgumentException(
                    "Giá sản phẩm " + dong.getMa() + " đã đổi. Hãy nạp lại giỏ hàng.");
        return sp.getMaSanPham();
    }

    private void phanBoFEFO(String maHoaDon, String maSanPham, GioHang.DongHang dong) {
        int conLai = dong.getSoLuong();
        for (LoHang loHang : banHangDAO.layLoTheoFEFO(maSanPham)) {
            int soLuong = Math.min(conLai, loHang.getSoLuongTon());
            if (soLuong <= 0) continue;
            if (banHangDAO.truTonLo(soLuong, loHang.getMaLo()) != 1)
                throw new IllegalArgumentException("Tồn kho vừa thay đổi.");
            if (banHangDAO.ghiPhanBoLo(maHoaDon, maSanPham, loHang.getMaLo(), soLuong) <= 0)
                throw new IllegalArgumentException(
                        "Không thể ghi nhận phân bổ lô hàng cho " + dong.getTen() + ".");
            conLai -= soLuong;
            if (conLai == 0) break;
        }
        if (conLai > 0)
            throw new IllegalStateException("Không đủ tồn kho còn hạn cho " + dong.getTen() + ".");
    }

    private void capNhatDiemVaHangKhach(GioHang banHang, String maKhachHang, long duocTich) {
        if (maKhachHang == null) return;
        banHangDAO.capNhatThanhToan(
                Math.toIntExact(banHang.getDiemDaDung()), duocTich, maKhachHang);
        capNhatHang(maKhachHang);
    }

    private void doiSoatNguonHoaDon(GioHang banHang, String maHoaDon) {
        if ("SPA".equals(banHang.getKieuNguon())) {
            long soTien = banHangDAO.layTienSpaChoThanhToan(banHang.getMaNguon());
            if (soTien != banHang.tinhTamTinh())
                throw new IllegalArgumentException(
                        "Phiếu Spa chưa hoàn thành hoặc số tiền đã thay đổi.");
            if (banHangDAO.ganHoaDonSpa(maHoaDon, banHang.getMaNguon()) != 1)
                throw new IllegalStateException("Phiếu Spa đã thanh toán.");
        } else if ("HOTEL".equals(banHang.getKieuNguon())
                || "LUU_TRU".equals(banHang.getKieuNguon())) {
            long duKien = luuTruService.tinhTienLuuTruConLai(banHang.getMaNguon());
            if (duKien != banHang.tinhTamTinh())
                throw new IllegalArgumentException("Quyết toán Hotel đã thay đổi, hãy nạp lại.");
            if (banHangDAO.ganHoaDonHotel(maHoaDon, banHang.getMaNguon()) != 1)
                throw new IllegalStateException("Hợp đồng đã trả phòng hoặc chưa check-in.");
            banHangDAO.chuyenPhongChoDon(banHang.getMaNguon());
        }
    }

    public void capNhatHang(String maKhachHang) {
        long chiTieu = banHangDAO.demCapNhatHang(maKhachHang);
        long[] cacHang = CaiDatService.macDinh().layHangChiTieu();
        String[] cacTen = {"Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương"};
        int hang = 0;
        while (hang < cacHang.length && chiTieu >= cacHang[hang]) hang++;
        banHangDAO.capNhatCapNhatHang(cacTen[Math.min(hang, cacTen.length - 1)], maKhachHang);
    }

    public GioHang.BienLai layBienLai(String khong) {
        HoaDon hd =
                banHangDAO
                        .timBienLai(khong)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Không tìm thấy hóa đơn " + khong));
        List<ChiTietHoaDon> cacChiTiet = banHangDAO.layDongBienLai(hd.getMaHoaDon());
        List<GioHang.DongHang> cacDong2 = new ArrayList<>();
        for (ChiTietHoaDon dongDangXet : cacChiTiet)
            cacDong2.add(
                    new GioHang.DongHang(
                            layMaHangBienLai(dongDangXet),
                            chuyenThanhChuoi(dongDangXet.getTenHang()),
                            dongDangXet.getDonGia(),
                            dongDangXet.getSoLuong()));
        int vatPhanTram =
                (int)
                        Math.round(
                                cacChiTiet.stream()
                                        .mapToDouble(ChiTietHoaDon::getThueGtgt)
                                        .max()
                                        .orElse(0.0));
        return new GioHang.BienLai(
                khong,
                hd.getThanhToanLuc(),
                cacDong2,
                hd.getTamTinh(),
                hd.getGiamGia(),
                hd.getTongTien(),
                PhuongThuc.docTuCSDL(chuyenThanhChuoi(hd.getHinhThucThanhToan())),
                hd.getTienKhachDua(),
                hd.getTienThoiSnapshot(),
                hd.getKhachHang() != null ? chuyenThanhChuoi(hd.getKhachHang().getHoTen()) : "",
                chuyenThanhChuoi(hd.getMaVoucher()),
                hd.getNhanVien() != null ? chuyenThanhChuoi(hd.getNhanVien().getHoTen()) : "",
                vatPhanTram);
    }

    public String treo(GioHang banHang) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        if (LoaiPhieuDoiTra.DOI_HANG.maDB().equals(banHang.getKieuNguon()))
            return new DonDoiTreoService(banHangDAO).treo(banHang);
        if (banHang.kiemTraTrong()) throw new IllegalArgumentException("Giỏ hàng trống.");
        if (!banHang.getKieuNguon().isBlank())
            throw new IllegalArgumentException(
                    "Chỉ treo đơn bán sản phẩm; phiếu Spa/Hotel vẫn được giữ trong màn xử lý.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            String maPhien =
                                    banHangDAO
                                            .layMaPhienDangMo(
                                                    PhienDangNhapService.macDinh().layMaNhanVien())
                                            .orElse(null);
                            String maKhachHang =
                                    banHang.getMaKhachHang().isBlank()
                                            ? null
                                            : banHang.getMaKhachHang().trim();
                            String maHoaDonTreo =
                                    banHangDAO.themTreo(
                                            taoMa("TREO"),
                                            maPhien,
                                            PhienDangNhapService.macDinh().layMaNhanVien(),
                                            maKhachHang);
                            for (GioHang.DongHang dong : banHang.getCacDong())
                                banHangDAO.capNhatTreo(
                                        maHoaDonTreo,
                                        dong.getMa(),
                                        dong.getTen(),
                                        dong.getDonGia(),
                                        dong.getSoLuong());
                            return maHoaDonTreo;
                        });
    }

    public void moLai(String maHoaDon, GioHang banHang) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        if (!banHang.kiemTraTrong())
            throw new IllegalArgumentException("Hãy treo hoặc thanh toán giỏ hiện tại trước.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            String maNhanVien = PhienDangNhapService.macDinh().layMaNhanVien();
                            if (banHangDAO.demGioTreoCuaTaiKhoan(maHoaDon, maNhanVien) == 0)
                                throw new IllegalArgumentException("Không tìm thấy đơn treo.");
                            if (new DonDoiTreoService(banHangDAO).lay(maHoaDon) != null)
                                throw new IllegalArgumentException(
                                        "Hãy mở đơn đổi tại màn Đơn đang treo để tiếp tục chọn phụ"
                                                + " kiện.");
                            List<GioHang.DongHang> cacDong = new ArrayList<>();
                            for (ChiTietHoaDon dongDuLieu : banHangDAO.timMoLai(maHoaDon))
                                cacDong.add(
                                        new GioHang.DongHang(
                                                chuyenThanhChuoi(
                                                        dongDuLieu.getSanPham().getMaSanPham()),
                                                chuyenThanhChuoi(
                                                        dongDuLieu.getSanPham().getTenSanPham()),
                                                dongDuLieu.getSanPham().getGia(),
                                                dongDuLieu.getSoLuong()));
                            if (cacDong.size() != banHangDAO.demMoLai(maHoaDon))
                                throw new IllegalArgumentException(
                                        "Đơn treo có sản phẩm đã ngừng bán.");
                            banHang.xoa();
                            banHang.setCacDong(cacDong);
                            banHangDAO
                                    .layKhachCuaGioTreo(maHoaDon)
                                    .ifPresent(
                                            kh ->
                                                    banHang.setKhachHang(
                                                            chuyenThanhChuoi(kh.getMaKhachHang()),
                                                            chuyenThanhChuoi(kh.getHoTen()),
                                                            kh.getDiemTichLuy() == null
                                                                    ? 0L
                                                                    : kh.getDiemTichLuy()));
                            xoaTreo(maHoaDon);
                            return null;
                        });
    }

    public void xoaTreo(String maHoaDon) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            String maNhanVien = PhienDangNhapService.macDinh().layMaNhanVien();
                            if (banHangDAO.demXoaTreo(maHoaDon, maNhanVien) == 0)
                                throw new IllegalArgumentException("Không tìm thấy đơn treo.");
                            banHangDAO.capNhatXoaTreo(maHoaDon);
                            banHangDAO.xoaGioHangTreo(maHoaDon, maNhanVien);
                            return null;
                        });
    }

    public void quyetToan(String loai, String ma, GioHang banHang) {
        List<GioHang.DongHang> cacDong = new ArrayList<>();
        String maKhachHang;
        if (loai.equals("SPA")) {
            PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_SPA);
            if (banHangDAO.demQuyetToan(ma) == 0)
                throw new IllegalStateException("Phiếu Spa chưa hoàn thành hoặc đã thanh toán.");
            for (ChiTietHoaDon dongDuLieu : banHangDAO.layDichVuSpaQuyetToan(ma))
                cacDong.add(
                        new GioHang.DongHang(
                                layMaHangBienLai(dongDuLieu),
                                chuyenThanhChuoi(dongDuLieu.getTenHang()),
                                dongDuLieu.getDonGia(),
                                dongDuLieu.getSoLuong()));
            maKhachHang = banHangDAO.layKhachCuaPhieuSpa(ma).orElse(null);
        } else if (loai.equals("HOTEL") || loai.equals("LUU_TRU")) {
            PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
            long conPhaiThu = luuTruService.tinhTienLuuTruConLai(ma);
            cacDong.add(
                    new GioHang.DongHang(
                            "HOTEL-ROOM", "Quyết toán Hotel (đã trừ cọc)", conPhaiThu, 1));
            maKhachHang = banHangDAO.layQuyetToan(ma).orElse(null);
        } else throw new IllegalArgumentException("Nguồn thanh toán không hợp lệ.");
        banHang.xoa();
        banHang.setCacDong(cacDong);
        banHang.setNguon(loai, ma);
        banHangDAO
                .timQuyetToan(maKhachHang)
                .ifPresent(
                        kh ->
                                banHang.setKhachHang(
                                        chuyenThanhChuoi(kh.getMaKhachHang()),
                                        chuyenThanhChuoi(kh.getHoTen()),
                                        kh.getDiemTichLuy() == null ? 0L : kh.getDiemTichLuy()));
    }

    public void huyHoaDon(String maHoaDon) {
        HoaDon chieuCao =
                banHangDAO
                        .timHuyHoaDon(maHoaDon)
                        .orElseThrow(() -> new IllegalStateException("Hóa đơn đã hủy."));
        if (doiHangDAO.laHoaDonThayThe(maHoaDon))
            throw new IllegalArgumentException(
                    "Không thể hủy hóa đơn gắn với phiếu đổi hàng đã hoàn thành.");
        if (banHangDAO.demPhieuDoiTraGanHoaDon(maHoaDon) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn có phiếu trả hàng, cần xử lý đổi trả thay vì hủy.");
        if (banHangDAO.demHuyHoaDon(maHoaDon) > 0 || banHangDAO.demHopDongGanHoaDon(maHoaDon) > 0)
            throw new IllegalArgumentException(
                    "Hóa đơn dịch vụ đã hoàn thành không thể hủy trực tiếp.");
        for (LoHang loHang : banHangDAO.layPhanBoCanHoanTon(chieuCao.getMaHoaDon()))
            banHangDAO.capNhatHuyHoaDon(loHang.getSoLuongTon(), loHang.getMaLo());
        String maKhach =
                chieuCao.getKhachHang() == null ? null : chieuCao.getKhachHang().getMaKhachHang();
        if (maKhach != null && !maKhach.isBlank()) {
            long cu = banHangDAO.khoaDiemKhachHang(maKhach),
                    tiepTheo =
                            Math.max(
                                    0,
                                    cu - chieuCao.getDiemCongSnapshot() + chieuCao.getDiemDung());
            banHangDAO.hoanDiemKhachHang(tiepTheo, maKhach);
        }
        String maVoucher = chuyenThanhChuoi(chieuCao.getMaVoucher());
        if (!maVoucher.isBlank()) banHangDAO.hoanLuotVoucher(maVoucher);
        banHangDAO.danhDauHuyHoaDon(maHoaDon);
        banHangDAO.danhDauHuyDonHang(chieuCao.getMaHoaDon());
        if (maKhach != null && !maKhach.isBlank()) capNhatHang(maKhach);
        NhatKyHeThongService.macDinh().ghiNhatKy("Hủy hóa đơn", maHoaDon);
    }

    public List<Object[]> layCoTheBanCacSanPham() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        return banHangDAO.layCoTheBanCacSanPham().stream()
                .map(
                        sp ->
                                new Object[] {
                                    sp.getMaSanPham(),
                                    sp.getTenSanPham(),
                                    sp.getNhomHang(),
                                    sp.getGia(),
                                    sp.tinhTongTonKhoKhaDung(),
                                    chuyenThanhChuoi(sp.getUrlAnh()),
                                    chuyenThanhChuoi(sp.getBarcode())
                                })
                .toList();
    }

    public ChiTietBienLai layChiTietBienLai(String soHoaDon) {
        PhienDangNhapService.macDinh().layPhien();
        var bienLai = layBienLai(soHoaDon);
        var nguon = layNguonBienLai(soHoaDon);
        HoaDon thongTin =
                banHangDAO
                        .layThongTinIn(soHoaDon)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Không tìm thấy thông tin in hóa đơn: "
                                                        + soHoaDon));
        KhachHang kh = thongTin.getKhachHang();
        return new ChiTietBienLai(
                bienLai,
                CaiDatService.macDinh().lay("TEN_CUA_HANG"),
                CaiDatService.macDinh().lay("DIA_CHI_CUA_HANG"),
                CaiDatService.macDinh().lay("SO_DIEN_THOAI_CUA_HANG"),
                kh == null ? "" : chuyenThanhChuoi(kh.getSoDienThoai()),
                thongTin.getDiemCongSnapshot(),
                kh == null || kh.getDiemTichLuy() == null ? null : kh.getDiemTichLuy().longValue(),
                nguon.loai(),
                nguon.ma(),
                nguon.daCanTru(),
                nguon.hoaDonGoc());
    }
}
