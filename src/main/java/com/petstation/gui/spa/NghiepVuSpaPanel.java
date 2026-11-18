package com.petstation.gui.spa;

import static com.petstation.gui.chung.TruongNghiepVu.layTruongVanBan;
import static com.petstation.gui.chung.TruongNghiepVu.taoLuaChonMuc;
import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dto.GioHang;
import com.petstation.dto.MucLuaChon;
import com.petstation.dto.PhienDangNhap;
import com.petstation.dto.SpaDTO;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.chung.thanhphan.BieuMauTruong.TruongDuLieu;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.spa.SpaService;
import com.petstation.util.DinhDang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class NghiepVuSpaPanel extends BangNghiepVuPanel {
    protected final SpaService spaService = new SpaService();
    private final BanHangService banHangService = new BanHangService();
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();

    protected NghiepVuSpaPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
    }

    protected void datLichHen() {
        DatLichSpaDialog.mo(this, this::lamMoi);
    }

    protected void huyLichHen() {
        String ma = layDongChon();
        if (!HopThoai.xacNhan(this, "Hủy lịch hẹn đã chọn?")) return;
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    spaService.huyLichHen(ma);
                    return null;
                },
                null,
                v -> lamMoi(),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    protected void tiepNhan() {
        String lichHenBang =
                (layKieuNghiepVu() == LoaiNghiepVu.LICH_HEN
                                && layBangDuLieu().getSelectedRow() >= 0)
                        ? layNoiDungDongChon(1)
                        : "";
        String maLichHenDong =
                (layKieuNghiepVu() == LoaiNghiepVu.LICH_HEN
                                && layBangDuLieu().getSelectedRow() >= 0)
                        ? layDongChon()
                        : "";
        String maLichHenHienTai = layBanHang().getQuyTrinh().getLichHen();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> taiDuLieuTiepNhan(lichHenBang, maLichHenDong, maLichHenHienTai),
                null,
                duLieu -> hienThiHopThoaiTiepNhan(duLieu, phien),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private Object[] taiDuLieuTiepNhan(
            String lichHenBang, String maLichHenDong, String maLichHenHienTai) {
        String lichHen = lichHenBang;
        String thuCung =
                maLichHenDong.isEmpty()
                        ? ""
                        : chuyenThanhChuoi(spaService.layMaLichHenThuCung(maLichHenDong));
        if (lichHen.isEmpty() && !maLichHenHienTai.isBlank()) {
            var hen = quyTrinhService.layLichHen(maLichHenHienTai);
            if (hen.isPresent()) {
                lichHen = hen.get().getMaLichHen();
                thuCung =
                        hen.get().getThuCung() != null ? hen.get().getThuCung().getMaThuCung() : "";
            }
        }
        List<MucLuaChon> dsThu =
                thuCung.isEmpty() ? layNghiepVuService().layLuaChonThuCung() : List.of();
        return new Object[] {lichHen, thuCung, dsThu};
    }

    @SuppressWarnings("unchecked")
    private void hienThiHopThoaiTiepNhan(Object[] duLieu, PhienDangNhap phien) {
        String lichHen = (String) duLieu[0];
        String thuCung = (String) duLieu[1];
        List<MucLuaChon> dsThu = (List<MucLuaChon>) duLieu[2];
        TruongDuLieu truongThuCung =
                thuCung.isEmpty()
                        ? taoLuaChonMuc("pet", "Thú cưng", dsThu)
                        : TruongDuLieu.layLuaChon(
                                "pet",
                                "Thú cưng",
                                List.<Object[]>of(new Object[] {thuCung, thuCung}));
        Map<String, String> giaTri =
                BieuMauTruong.layYeuCauNhap(
                        this,
                        "Tiếp nhận Spa",
                        truongThuCung,
                        layTruongVanBan("weight", "Cân nặng thực tế (kg)", ""),
                        layTruongVanBan("note", "Tình trạng / Lưu ý", ""));
        if (giaTri == null) return;
        double canNang;
        try {
            canNang = Double.parseDouble(giaTri.get("weight").trim().replace(',', '.'));
            if (!Double.isFinite(canNang) || canNang <= 0) {
                throw new IllegalArgumentException("Cân nặng thực tế phải lớn hơn 0.");
            }
        } catch (NumberFormatException ex) {
            HopThoai.hienThiCanhBao(this, "Cân nặng thực tế phải là số hợp lệ.");
            return;
        } catch (RuntimeException ex) {
            HopThoai.hienThiCanhBao(this, ex.getMessage());
            return;
        }
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> spaService.tiepNhan(giaTri.get("pet"), lichHen, canNang, giaTri.get("note")),
                null,
                ma -> {
                    layBanHang().getQuyTrinh().chonSpa(ma);
                    layDieuHuong().accept(MaManHinh.CHON_GOI_SPA);
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    protected void themDichVu() {
        String phieuSpa = layDongChon();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> layNghiepVuService().layLuaChonGoiDichVu(),
                null,
                dsGoi -> {
                    Map<String, String> giaTri =
                            BieuMauTruong.layYeuCauNhap(
                                    this,
                                    "Thêm dịch vụ vào phiếu",
                                    taoLuaChonMuc("package", "Dịch vụ", dsGoi));
                    if (giaTri == null) return;
                    TacVuNen.chayCoPhien(
                            phien,
                            () -> true,
                            () -> {
                                spaService.themDichVuSpa(phieuSpa, giaTri.get("package"));
                                return null;
                            },
                            null,
                            v -> lamMoi(),
                            ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    protected void xoaDichVu() {
        String maPhieu = layDongChon();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> spaService.layPhieuSpaChiTiet(maPhieu),
                null,
                ds -> {
                    if (ds.size() <= 1) {
                        HopThoai.hienThiCanhBao(this, "Phiếu Spa phải giữ lại ít nhất 1 dịch vụ.");
                        return;
                    }
                    String[] cacMuc = new String[ds.size()];
                    for (int i = 0; i < ds.size(); i++) {
                        SpaDTO.DongChiTietSpa d = ds.get(i);
                        cacMuc[i] =
                                (i + 1)
                                        + ". "
                                        + d.getTenDichVu()
                                        + " — "
                                        + DinhDang.dinhDang(d.getThanhTien());
                    }
                    Object chon =
                            HopThoai.chonMuc(
                                    this,
                                    "Chọn dịch vụ thêm nhầm cần xóa khỏi phiếu " + maPhieu + ":",
                                    "Xóa dịch vụ Spa",
                                    cacMuc,
                                    cacMuc[cacMuc.length - 1]);
                    if (chon == null) return;
                    int viTri = -1;
                    for (int i = 0; i < cacMuc.length; i++) {
                        if (cacMuc[i].equals(chon)) {
                            viTri = i;
                            break;
                        }
                    }
                    if (viTri < 0
                            || !HopThoai.xacNhan(
                                    this,
                                    "Xóa dịch vụ \""
                                            + ds.get(viTri).getTenDichVu()
                                            + "\" khỏi phiếu "
                                            + maPhieu
                                            + "?")) {
                        return;
                    }
                    int idxXoa = viTri;
                    TacVuNen.chayCoPhien(
                            phien,
                            () -> true,
                            () -> {
                                spaService.xoaDichVuSpa(maPhieu, idxXoa);
                                return null;
                            },
                            null,
                            v -> lamMoi(),
                            ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    protected void laySpaChiTiet() {
        String maPhieu = layDongChon();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    List<Object[]> dongChiTiet = new ArrayList<>();
                    for (SpaDTO.DongChiTietSpa d : spaService.layPhieuSpaChiTiet(maPhieu)) {
                        dongChiTiet.add(
                                new Object[] {
                                    d.getTenDichVu(),
                                    DinhDang.dinhDang(d.getDonGia()),
                                    d.getSoLuong(),
                                    DinhDang.dinhDang(d.getThanhTien())
                                });
                    }
                    return dongChiTiet;
                },
                null,
                dongChiTiet ->
                        hienThiChiTiet(
                                "Chi tiết phiếu Spa",
                                new String[] {"Dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"},
                                dongChiTiet),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    protected void quyetToan(String loai) {
        if (!layBanHang().kiemTraTrong()) {
            if (HopThoai.xacNhanDongY(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\n"
                            + "Bạn có muốn làm trống giỏ hàng để chuyển sang quyết toán Spa"
                            + " không?",
                    "Giỏ hàng đang có sản phẩm")) {
                layBanHang().xoa();
            } else {
                return;
            }
        }
        String maPhieu = layDongChon();
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    banHangService.quyetToan(loai, maPhieu, layBanHang());
                    return null;
                },
                null,
                v -> layDieuHuong().accept(MaManHinh.THANH_TOAN),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }
}
