package com.petstation.service.banhang;

import static com.petstation.util.DuLieu.taoMa;

import com.petstation.dao.banhang.HoaDonDAO;
import com.petstation.dao.banhang.impl.HoaDonDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.DonDoiTreo;
import com.petstation.dto.GioHang;
import com.petstation.model.chung.LoaiPhieuDoiTra;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.Objects;

public final class DonDoiTreoService {
    private final HoaDonDAO hoaDonDAO;

    public DonDoiTreoService() {
        this(new HoaDonDAOImpl());
    }

    public DonDoiTreoService(HoaDonDAO hoaDonDAO) {
        this.hoaDonDAO = Objects.requireNonNull(hoaDonDAO);
    }

    public DonDoiTreo lay(String maHoaDon) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        if (maHoaDon == null || maHoaDon.isBlank()) return null;
        return hoaDonDAO.layDonDoiTreo(maHoaDon);
    }

    public String treo(String maHoaDon, DonDoiTreo don) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            String khoa = maHoaDon == null ? "" : maHoaDon.trim();
                            if (khoa.isBlank()) {
                                String maPhien =
                                        hoaDonDAO
                                                .layMaPhienDangMo(
                                                        PhienDangNhapService.macDinh()
                                                                .layMaNhanVien())
                                                .orElse(null);
                                khoa =
                                        hoaDonDAO.themTreo(
                                                taoMa("TREODOI"),
                                                maPhien,
                                                PhienDangNhapService.macDinh().layMaNhanVien(),
                                                null);
                            } else if (hoaDonDAO.layDonDoiTreo(khoa) == null) {
                                throw new IllegalStateException(
                                        "Đơn đổi treo đã được mở hoặc xóa ở nơi khác.");
                            }
                            hoaDonDAO.luuDonDoiTreo(khoa, don);
                            return khoa;
                        });
    }

    public String treo(GioHang gio) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        if (gio.isDangGhiThanhToan())
            throw new IllegalArgumentException("Đang ghi thanh toán, vui lòng chờ.");
        if (!LoaiPhieuDoiTra.DOI_HANG.maDB().equals(gio.getKieuNguon())
                || gio.getDoiHang() == null
                || gio.getCacDong().size() != 1)
            throw new IllegalArgumentException("Không có đơn đổi phụ kiện để treo.");
        var yc = gio.getDoiHang();
        var hang = gio.getCacDong().get(0);
        return treo(
                "",
                new DonDoiTreo(
                        hoaDonDAO.layMaHoaDonGoc(yc.getDongGoc()),
                        yc.getDongGoc(),
                        hang.getMa(),
                        hang.getMa(),
                        yc.getSoLuong(),
                        yc.getLyDo(),
                        true,
                        true));
    }

    public void chuyenThanhToan(
            String maHoaDon, GioHang gio, String maDong, int sl, String ma, String lyDo) {
        PhienDangNhapService.macDinh().yeuCau(Quyen.BAN_HANG);
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        c -> {
                            if (maHoaDon != null && !maHoaDon.isBlank()) {
                                if (hoaDonDAO.layDonDoiTreo(maHoaDon) == null)
                                    throw new IllegalStateException(
                                            "Đơn đổi treo đã được mở hoặc xóa ở nơi khác.");
                                new BanHangService().xoaTreo(maHoaDon);
                            }
                            new DoiTraService().lapDoiHang(gio, maDong, sl, ma, sl, lyDo);
                            return null;
                        });
    }
}
