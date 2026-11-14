package com.petstation.service.luutru;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;
import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.banhang.HoaDonDAO;
import com.petstation.dao.banhang.impl.HoaDonDAOImpl;
import com.petstation.dao.luutru.HopDongLuuTruDAO;
import com.petstation.dao.luutru.impl.HopDongLuuTruDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.HotelDTO;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.chung.VaiTro;
import com.petstation.model.khachhang.ThuCung;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.ThuCungService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class LuuTruService {
    private final HoaDonDAO banHangDAO;
    private final HopDongLuuTruDAO luuTruDAO;

    public LuuTruService() {
        this(new HoaDonDAOImpl(), new HopDongLuuTruDAOImpl());
    }

    public LuuTruService(HoaDonDAO banHangDAO, HopDongLuuTruDAO luuTruDAO) {
        this.banHangDAO = Objects.requireNonNull(banHangDAO, "banHangDAO không được null");
        this.luuTruDAO = Objects.requireNonNull(luuTruDAO, "luuTruDAO không được null");
    }

    public String datPhong(
            String maThuCung,
            String maPhong,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            long tienCoc,
            String ghiChu) {
        return datPhong(maThuCung, maPhong, batDau, ketThuc, tienCoc, ghiChu, false);
    }

    public String datPhong(
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
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            String maHopDong =
                                    giuPhong(maThuCung, maPhong, batDau, ketThuc, ghiChu);
                            thuCoc(
                                    maHopDong,
                                    PhuongThuc.TIEN_MAT,
                                    tienCoc,
                                    tienCoc,
                                    "COC-" + UUID.randomUUID());
                            return maHopDong;
                        });
    }

    public String giuPhong(
            String maThuCung,
            String maPhong,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String ghiChu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau))
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            ThuCung thu = ThuCungService.macDinh().layThuCungHoatDong(maThuCung);
                            List<HopDongLuuTruDAO.ThongTinPhongDat> phong =
                                    luuTruDAO.timDatPhong(maPhong);
                            if (phong.isEmpty()
                                    || Set.of(
                                                    TrangThaiPhongHotel.BAO_TRI.maDB(),
                                                    TrangThaiPhongHotel.DANG_DON.maDB(),
                                                    TrangThaiTaiKhoan.NGUNG_HOAT_DONG.maDB())
                                            .contains(phong.get(0).trangThai))
                                throw new IllegalArgumentException("Phòng không nhận đặt chỗ.");
                            HopDongLuuTruDAO.ThongTinPhongDat p = phong.get(0);
                            String loaiThu = thu.getLoai();
                            boolean laMeo =
                                    "Mèo".equalsIgnoreCase(loaiThu)
                                            || "CAT".equalsIgnoreCase(loaiThu);
                            String loaiPhong = p.loaiPhong;
                            if ("Mèo".equalsIgnoreCase(loaiPhong) && !laMeo)
                                throw new IllegalArgumentException("Phòng Mèo chỉ dành cho mèo.");
                            if (("Chó".equalsIgnoreCase(loaiPhong)
                                            || "DOG".equalsIgnoreCase(loaiPhong))
                                    && laMeo)
                                throw new IllegalArgumentException("Phòng Chó chỉ dành cho chó.");
                            if (luuTruDAO.demThuCungDatTrung(thu.getMaThuCung(), ketThuc, batDau)
                                    > 0)
                                throw new IllegalArgumentException(
                                        "Thú cưng đã có hợp đồng lưu trú trùng thời gian.");
                            long soChoDaDat =
                                    luuTruDAO.demPhongDatTrung(p.maPhong, ketThuc, batDau);
                            int sucChua = p.sucChua;
                            if (soChoDaDat >= sucChua)
                                throw new IllegalArgumentException(
                                        "Phòng đã đủ sức chứa trong khoảng thời gian đã chọn. Hiện"
                                                + " có "
                                                + soChoDaDat
                                                + "/"
                                                + sucChua
                                                + " chỗ được đặt.");
                            long gia = p.giaDem;
                            long yeuCau =
                                    (Math.multiplyExact(tinhSoNgay(batDau, ketThuc), gia) + 1) / 2;
                            String maKhach =
                                    thu.getKhachHang() != null
                                            ? thu.getKhachHang().getMaKhachHang()
                                            : null;
                            String maHopDong =
                                    luuTruDAO.themDatPhong(
                                            taoMa("HDHT"),
                                            maKhach,
                                            thu.getMaThuCung(),
                                            p.maPhong,
                                            batDau,
                                            ketThuc,
                                            0,
                                            gia,
                                            ghiChu);
                            luuTruDAO.datMucCoc(yeuCau, maHopDong);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Giữ phòng Hotel", maPhong + " / " + maHopDong);
                            return maHopDong;
                        });
    }

    public List<HotelDTO.DongLichSuCoc> layCoc(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        return luuTruDAO.layLichSuThuCoc(maHopDong);
    }

    public long[] layMucCoc(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        List<HopDongLuuTruDAO.MucCocHopDong> r = luuTruDAO.layMucCoc(maHopDong);
        if (r.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
        return new long[] {r.get(0).cocYeuCau, r.get(0).cocDaThu};
    }

    public void thuCoc(
            String maHopDong, PhuongThuc phuongThuc, long soTien, long khachDua, String khoa) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (khoa == null || !khoa.matches("[A-Za-z0-9-]{1,90}"))
            throw new IllegalArgumentException("Thiếu khóa thu cọc.");
        if (phuongThuc == null)
            throw new IllegalArgumentException("Cần chọn phương thức thanh toán.");
        long tienKhachDua = phuongThuc == PhuongThuc.TIEN_MAT ? khachDua : soTien;
        if (soTien <= 0 || tienKhachDua < soTien)
            throw new IllegalArgumentException("Tiền thu phải dương và đủ số cần thu.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            List<HopDongLuuTruDAO.TrangThaiCocHopDong> r =
                                    luuTruDAO.khoaHopDongThuCoc(maHopDong);
                            if (r.isEmpty())
                                throw new IllegalArgumentException("Không tìm thấy hợp đồng.");
                            HopDongLuuTruDAO.TrangThaiCocHopDong trangThaiCoc = r.get(0);
                            if (luuTruDAO.demKhoaThuCoc(maHopDong, khoa) > 0) return null;
                            if (!TrangThaiHopDong.DA_DAT.maDB().equals(trangThaiCoc.trangThai))
                                throw new IllegalArgumentException(
                                        "Chỉ nhận cọc khi hợp đồng đang chờ nhận phòng.");
                            if (soTien > trangThaiCoc.cocYeuCau - trangThaiCoc.cocDaThu)
                                throw new IllegalArgumentException(
                                        "Tiền thu phải dương và không vượt phần cọc còn thiếu.");
                            String maNhanVien = PhienDangNhapService.macDinh().layMaNhanVien();
                            String maPhien =
                                    banHangDAO
                                            .layMaPhienDangMo(maNhanVien)
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalArgumentException(
                                                                    "Cần mở ca trước khi thu"
                                                                            + " cọc."));
                            if (banHangDAO.demNgayDaChot() > 0)
                                throw new IllegalStateException("Ngày đã chốt.");
                            luuTruDAO.ghiThuCoc(
                                    maHopDong,
                                    maPhien,
                                    maNhanVien,
                                    khoa,
                                    phuongThuc,
                                    soTien,
                                    tienKhachDua);
                            luuTruDAO.ghiNhanTongCoc(
                                    Math.addExact(trangThaiCoc.cocDaThu, soTien),
                                    maNhanVien,
                                    maPhien,
                                    maHopDong);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Thu cọc Hotel", maHopDong + " / " + soTien);
                            return null;
                        });
    }

    public void dieuChinhCoc(String maHopDong, long muc, String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (PhienDangNhapService.macDinh().layPhien().docVaiTro() != VaiTro.QUAN_LY_CUA_HANG)
            throw new SecurityException("Chỉ quản lý được điều chỉnh mức cọc.");
        if (lyDo == null || lyDo.isBlank() || lyDo.length() > 1000)
            throw new IllegalArgumentException("Cần lý do điều chỉnh.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            List<HopDongLuuTruDAO.ThongTinDoiCoc> r =
                                    luuTruDAO.khoaHopDongDoiCoc(maHopDong);
                            if (r.isEmpty()
                                    || !TrangThaiHopDong.DA_DAT.maDB().equals(r.get(0).trangThai))
                                throw new IllegalArgumentException(
                                        "Hợp đồng không còn chờ nhận phòng.");
                            HopDongLuuTruDAO.ThongTinDoiCoc d = r.get(0);
                            long tong =
                                    Math.multiplyExact(
                                            tinhSoNgay(d.ngayVao, d.ngayRaDuKien), d.giaDem);
                            if (muc < d.cocDaThu || muc < 0 || muc > tong)
                                throw new IllegalArgumentException(
                                        "Mức cọc phải từ số đã thu đến tổng tiền phòng dự kiến.");
                            luuTruDAO.dieuChinhMucCoc(muc, lyDo, maHopDong);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy(
                                            "Điều chỉnh cọc Hotel",
                                            maHopDong + " / " + muc + " / " + lyDo);
                            return null;
                        });
    }

    public long tinhCocDuKien(String maPhong, LocalDateTime batDau, LocalDateTime ketThuc) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau))
            throw new IllegalArgumentException("Ngày ra phải sau ngày vào.");
        List<HopDongLuuTruDAO.ThongTinPhongDat> phong = luuTruDAO.timDatPhong(maPhong);
        if (phong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy phòng.");
        return (Math.multiplyExact(tinhSoNgay(batDau, ketThuc), phong.get(0).giaDem) + 1) / 2;
    }

    public long[] tinhTienPhongDuKien(String maPhong, LocalDateTime batDau, LocalDateTime ketThuc) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (maPhong == null
                || maPhong.isBlank()
                || batDau == null
                || ketThuc == null
                || !ketThuc.isAfter(batDau))
            throw new IllegalArgumentException("Vui lòng chọn phòng và khoảng thời gian hợp lệ.");
        List<HopDongLuuTruDAO.ThongTinPhongDat> phong = luuTruDAO.timDatPhong(maPhong.trim());
        if (phong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy phòng đã chọn.");
        long soNgay = tinhSoNgay(batDau, ketThuc);
        long gia = phong.get(0).giaDem;
        long tong = Math.multiplyExact(soNgay, gia);
        long coc = Math.addExact(tong, 1) / 2;
        return new long[] {soNgay, tong, coc, gia};
    }

    public HotelDTO.ThongTinDatPhong layThongTinDatPhong(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        List<HotelDTO.ThongTinDatPhong> dong = luuTruDAO.layDatPhongSua(maHopDong);
        if (dong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy hợp đồng lưu trú.");
        if (!TrangThaiHopDong.DA_DAT.maDB().equals(dong.get(0).getTrangThai()))
            throw new IllegalArgumentException("Chỉ sửa được hợp đồng đang chờ nhận phòng.");
        return dong.get(0);
    }

    public void suaDatPhong(
            String maHopDong,
            String maThuCung,
            String maPhong,
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            String ghiChu) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (maHopDong == null
                || maHopDong.isBlank()
                || maThuCung == null
                || maThuCung.isBlank()
                || maPhong == null
                || maPhong.isBlank()
                || batDau == null
                || ketThuc == null
                || !ketThuc.isAfter(batDau)
                || ghiChu == null
                || ghiChu.length() > 1000)
            throw new IllegalArgumentException("Thông tin đặt phòng không hợp lệ.");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            HotelDTO.ThongTinDatPhong duLieuCu = layHopDongChoNhanPhong(maHopDong);
                            ThuCung thu =
                                    ThuCungService.macDinh().layThuCungHoatDong(maThuCung.trim());
                            String maKhach =
                                    thu.getKhachHang() != null
                                            ? thu.getKhachHang().getMaKhachHang()
                                            : null;
                            HopDongLuuTruDAO.ThongTinPhongDat phong =
                                    kiemTraPhongPhuHopKhiSua(
                                            maPhong, thu, maHopDong, batDau, ketThuc);
                            long cocYeuCauMoi =
                                    tinhCocYeuCauKhiSua(batDau, ketThuc, phong, duLieuCu);
                            if (luuTruDAO.capNhatDatPhong(
                                            maKhach,
                                            thu.getMaThuCung(),
                                            phong.maPhong,
                                            batDau,
                                            ketThuc,
                                            phong.giaDem,
                                            cocYeuCauMoi,
                                            ghiChu.trim(),
                                            maHopDong)
                                    != 1)
                                throw new IllegalArgumentException(
                                        "Đặt phòng vừa thay đổi. Hãy tải lại danh sách.");
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy(
                                            "Sửa đặt phòng Hotel",
                                            maHopDong
                                                    + " / "
                                                    + maThuCung
                                                    + " / "
                                                    + maPhong
                                                    + " / "
                                                    + batDau
                                                    + " - "
                                                    + ketThuc
                                                    + " / cọc yêu cầu "
                                                    + cocYeuCauMoi);
                            return null;
                        });
    }

    private HotelDTO.ThongTinDatPhong layHopDongChoNhanPhong(String maHopDong) {
        List<HotelDTO.ThongTinDatPhong> hopDong = luuTruDAO.layDatPhongSua(maHopDong);
        if (hopDong.isEmpty()
                || !TrangThaiHopDong.DA_DAT.maDB().equals(hopDong.get(0).getTrangThai()))
            throw new IllegalStateException("Hợp đồng không còn ở trạng thái chờ nhận phòng.");
        return hopDong.get(0);
    }

    private HopDongLuuTruDAO.ThongTinPhongDat kiemTraPhongPhuHopKhiSua(
            String maPhong,
            ThuCung thu,
            String maHopDong,
            LocalDateTime batDau,
            LocalDateTime ketThuc) {
        List<HopDongLuuTruDAO.ThongTinPhongDat> cacPhong = luuTruDAO.timDatPhong(maPhong.trim());
        if (cacPhong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy phòng.");
        HopDongLuuTruDAO.ThongTinPhongDat phong = cacPhong.get(0);
        if (!Set.of(TrangThaiPhongHotel.TRONG.maDB(), TrangThaiPhongHotel.DANG_O.maDB())
                .contains(phong.trangThai))
            throw new IllegalArgumentException(
                    "Phòng không nhận đặt chỗ do đang dọn, bảo trì hoặc ngừng hoạt động.");
        kiemTraLoaiThuPhuHopPhong(thu, phong.loaiPhong);
        if (luuTruDAO.demThuCungDatTrungBoQua(thu.getMaThuCung(), ketThuc, batDau, maHopDong) > 0)
            throw new IllegalArgumentException("Thú cưng đã có hợp đồng lưu trú trùng thời gian.");
        long soChoDaDat =
                luuTruDAO.demPhongDatTrungBoQua(phong.maPhong, ketThuc, batDau, maHopDong);
        if (soChoDaDat >= phong.sucChua)
            throw new IllegalArgumentException(
                    "Phòng đã đủ sức chứa trong khoảng thời gian đã chọn ("
                            + soChoDaDat
                            + "/"
                            + phong.sucChua
                            + ").");
        return phong;
    }

    private void kiemTraLoaiThuPhuHopPhong(ThuCung thu, String loaiPhong) {
        String loaiThu = chuyenThanhChuoi(thu.getLoai());
        boolean laMeo = "Mèo".equalsIgnoreCase(loaiThu) || "CAT".equalsIgnoreCase(loaiThu);
        if ("Mèo".equalsIgnoreCase(loaiPhong) && !laMeo)
            throw new IllegalArgumentException("Phòng Mèo chỉ dành cho mèo.");
        if (("Chó".equalsIgnoreCase(loaiPhong) || "DOG".equalsIgnoreCase(loaiPhong)) && laMeo)
            throw new IllegalArgumentException("Phòng Chó chỉ dành cho chó.");
    }

    private long tinhCocYeuCauKhiSua(
            LocalDateTime batDau,
            LocalDateTime ketThuc,
            HopDongLuuTruDAO.ThongTinPhongDat phong,
            HotelDTO.ThongTinDatPhong duLieuCu) {
        long tong = Math.multiplyExact(tinhSoNgay(batDau, ketThuc), phong.giaDem);
        long cocMoi = Math.addExact(tong, 1) / 2;
        long cocDaThu = duLieuCu.isDaNhanCoc() ? duLieuCu.getTienCoc() : 0;
        return Math.max(cocMoi, cocDaThu);
    }

    public void huyDatPhong(String maHopDong, String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (maHopDong == null
                || maHopDong.isBlank()
                || lyDo == null
                || lyDo.isBlank()
                || lyDo.trim().length() > 1000)
            throw new IllegalArgumentException(
                    "Cần nhập lý do hủy đặt phòng (tối đa 1.000 ký tự).");
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            List<HotelDTO.ThongTinDatPhong> hopDong =
                                    luuTruDAO.layDatPhongSua(maHopDong);
                            if (hopDong.isEmpty()
                                    || !TrangThaiHopDong.DA_DAT
                                            .maDB()
                                            .equals(hopDong.get(0).getTrangThai()))
                                throw new IllegalArgumentException(
                                        "Chỉ hủy được hợp đồng đang chờ nhận phòng.");
                            HotelDTO.ThongTinDatPhong duLieu = hopDong.get(0);
                            if (duLieu.isDaNhanCoc() || duLieu.getTienCoc() > 0)
                                throw new IllegalArgumentException(
                                        "Hợp đồng đã thu cọc. Cần hoàn cọc và đối soát thủ quỹ"
                                            + " trước khi hủy; hệ thống hiện chưa tự động thực hiện"
                                            + " hoàn tiền.");
                            if (luuTruDAO.huyDatPhong(lyDo.trim(), maHopDong) != 1)
                                throw new IllegalArgumentException(
                                        "Không thể hủy hợp đồng. Hãy tải lại dữ liệu.");
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy(
                                            "Hủy đặt phòng Hotel", maHopDong + " / " + lyDo.trim());
                            return null;
                        });
    }

    public void kiemTraVao(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            String phong = luuTruDAO.layNhanPhong(maHopDong);
                            if (phong == null)
                                throw new IllegalArgumentException(
                                        "Hợp đồng không còn chờ nhận phòng.");
                            if (luuTruDAO.danhDauPhongDangO(phong) != 1)
                                throw new IllegalArgumentException("Phòng chưa sẵn sàng.");
                            luuTruDAO.capNhatNhanPhong(maHopDong);
                            NhatKyHeThongService.macDinh().ghiNhatKy("Nhận phòng", maHopDong);
                            return null;
                        });
    }

    public void ghiNhatKy(String maHopDong, String anUong, String daThucHien, String sucKhoe) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        if (sucKhoe == null || sucKhoe.isBlank())
            throw new IllegalArgumentException("Cần ghi tình trạng thú cưng.");
        String anUongChuan = anUong == null ? "" : anUong;
        String daThucHienChuan = daThucHien == null ? "" : daThucHien;
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (luuTruDAO.demNhatKy(maHopDong) == 0)
                                throw new IllegalStateException(
                                        "Thú cưng chưa nhận phòng hoặc đã trả phòng.");
                            luuTruDAO.capNhatNhatKy(
                                    maHopDong,
                                    PhienDangNhapService.macDinh().layMaNhanVien(),
                                    anUongChuan,
                                    daThucHienChuan,
                                    sucKhoe);
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Ghi nhật ký Hotel", maHopDong);
                            return null;
                        });
    }

    private static long tinhSoNgay(LocalDateTime batDau, LocalDateTime ketThuc) {
        return Math.max(1, (Duration.between(batDau, ketThuc).toMinutes() + 1439) / 1440);
    }

    public long tinhTienLuuTruConLai(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        List<HopDongLuuTruDAO.ThongTinThanhToanHotel> cacDong =
                luuTruDAO.timHotelConPhaiThu(maHopDong);
        if (cacDong.isEmpty())
            throw new IllegalArgumentException("Hợp đồng chưa nhận phòng hoặc đã quyết toán.");
        HopDongLuuTruDAO.ThongTinThanhToanHotel dongDuLieu = cacDong.get(0);
        LocalDateTime batDau = dongDuLieu.ngayVao;
        LocalDateTime ketThuc = dongDuLieu.ngayRaDuKien;
        LocalDateTime hienTai = LocalDateTime.now();
        long ngayO =
                hienTai.isAfter(ketThuc)
                        ? tinhSoNgay(batDau, hienTai)
                        : tinhSoNgay(batDau, ketThuc);
        long tre =
                hienTai.isAfter(ketThuc)
                        ? tinhSoNgay(ketThuc, hienTai)
                                * CaiDatService.macDinh().layPhiTraPhongTreMoiNgay()
                        : 0;
        return Math.max(0, ngayO * dongDuLieu.giaDem + tre - dongDuLieu.tienCocDaThu);
    }

    public void hoanTatDonPhong(String maPhong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (luuTruDAO.danhDauPhongKhaDung(maPhong) != 1)
                                throw new IllegalStateException("Chỉ áp dụng cho phòng đang dọn.");
                            NhatKyHeThongService.macDinh()
                                    .ghiNhatKy("Cập nhật phòng", "Hoàn thành dọn phòng");
                            return null;
                        });
    }

    public List<PhongHotel> layPhongDangDon() {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        return luuTruDAO.layPhongDangDon();
    }

    public List<HotelDTO.DongNhatKyChiTiet> layNhatKyChiTiet(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        return luuTruDAO.layNhatKyChiTiet(maHopDong);
    }

    public Optional<HopDongLuuTru> timHopDong(String maHopDong) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.QUAN_LY_LUU_TRU);
        return luuTruDAO.timTheoMa(maHopDong);
    }
}
