package com.petstation.gui.spa;

import static com.petstation.gui.chung.TruongNghiepVu.docThoiGian;
import static com.petstation.gui.chung.TruongNghiepVu.layThoiGianHienTai;
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
import com.petstation.gui.luutru.NhatKyChamSocDialog;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiPhieuSpa;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.chung.QuyTrinhService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.spa.SpaService;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JButton;

public abstract class NghiepVuSpaPanel extends BangNghiepVuPanel {
    protected final SpaService spaService = new SpaService();
    private final BanHangService banHangService = new BanHangService();
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();

    protected NghiepVuSpaPanel(LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
        if (kieu != LoaiNghiepVu.LICH_HEN && kieu != LoaiNghiepVu.PHIEU_SPA)
            themNut(
                    layKhungThaoTac(),
                    "Phiếu chăm sóc",
                    () -> NhatKyChamSocDialog.mo(this, "SPA", layDongChon()));
    }

    @Override
    protected void capNhatThaoTac() {
        if (layBangDuLieu() == null || layKhungThaoTac() == null) return;
        String trangThai = "";
        boolean coDichVu = false;
        if (layBangDuLieu().getSelectedRow() >= 0 && layKieuNghiepVu() != LoaiNghiepVu.LICH_HEN) {
            int dong = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
            trangThai = String.valueOf(layMoHinh().getValueAt(dong, 7));
            coDichVu = ((Number) layMoHinh().getValueAt(dong, 6)).longValue() > 0;
        }
        for (Component thanhPhan : layKhungThaoTac().getComponents())
            if (thanhPhan instanceof JButton btn) {
                switch (btn.getText()) {
                    case "Thêm dịch vụ" ->
                            btn.setEnabled(
                                    Set.of(
                                                    TrangThaiPhieuSpa.TIEP_NHAN.maDB(),
                                                    TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB())
                                            .contains(trangThai));
                    case "Bắt đầu làm" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiPhieuSpa.TIEP_NHAN.maDB())
                                            && coDichVu);
                    case "Hoàn thành" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiPhieuSpa.DANG_THUC_HIEN.maDB()));
                    case "Thanh toán" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiPhieuSpa.HOAN_TAT.maDB())
                                            && PhienDangNhapService.macDinh()
                                                    .layPhien()
                                                    .kiemTraCo(Quyen.THANH_TOAN));
                    case "Chi tiết dịch vụ", "Phiếu chăm sóc" ->
                            btn.setEnabled(layBangDuLieu().getSelectedRow() >= 0);
                    default -> {}
                }
            }
    }

    protected void datLichHen() {
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        new Object[] {
                            layNghiepVuService().layLuaChonThuCung(),
                            layNghiepVuService().layLuaChonGoiDichVu(),
                            layNghiepVuService().layLuaChonNhanVien()
                        },
                null,
                duLieu -> hienThiHopThoaiDatLich(duLieu, phien),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    @SuppressWarnings("unchecked")
    private void hienThiHopThoaiDatLich(Object[] duLieu, PhienDangNhap phien) {
        Map<String, String> giaTri =
                BieuMauTruong.layYeuCauNhap(
                        this,
                        "Đặt lịch Spa",
                        taoLuaChonMuc("pet", "Thú cưng", (List<MucLuaChon>) duLieu[0]),
                        taoLuaChonMuc("package", "Dịch vụ", (List<MucLuaChon>) duLieu[1]),
                        taoLuaChonMuc("staff", "Nhân viên", (List<MucLuaChon>) duLieu[2]),
                        layTruongVanBan(
                                "start", "Bắt đầu (yyyy-MM-dd HH:mm)", layThoiGianHienTai()));
        if (giaTri == null) return;
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () ->
                        spaService.datLichHen(
                                giaTri.get("pet"),
                                giaTri.get("package"),
                                giaTri.get("staff"),
                                docThoiGian(giaTri, "start")),
                null,
                ma -> {
                    layBanHang().getQuyTrinh().chonLichHen(ma);
                    lamMoi();
                },
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
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
                                    d.getDonGia(),
                                    d.getSoLuong(),
                                    d.getThanhTien()
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
