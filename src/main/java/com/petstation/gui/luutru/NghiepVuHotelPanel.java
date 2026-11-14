package com.petstation.gui.luutru;

import static com.petstation.gui.chung.TruongNghiepVu.docSoTien;
import static com.petstation.gui.chung.TruongNghiepVu.layTruongVanBan;

import com.petstation.dto.GioHang;
import com.petstation.dto.HotelDTO;
import com.petstation.gui.banhang.ThuTienDialog;
import com.petstation.gui.chung.BangNghiepVuPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.model.chung.LoaiNghiepVu;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.TrangThaiHopDong;
import com.petstation.model.luutru.PhongHotel;
import com.petstation.service.banhang.BanHangService;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.luutru.LuuTruService;
import com.petstation.util.DinhDang;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.swing.JButton;

public abstract class NghiepVuHotelPanel extends BangNghiepVuPanel {
    protected final LuuTruService luuTruService = new LuuTruService();
    private final BanHangService banHangService = new BanHangService();

    protected NghiepVuHotelPanel(
            LoaiNghiepVu kieu, GioHang banHang, Consumer<MaManHinh> dieuHuong) {
        super(kieu, banHang, dieuHuong);
        themNut(layKhungThaoTac(), "Thu cọc", this::thuCoc);
        themNut(
                layKhungThaoTac(),
                "Phiếu chăm sóc",
                () -> NhatKyChamSocDialog.mo(this, "HOTEL", layDongChon()));
    }

    protected void lichSuCoc() {
        String ma = layDongChon();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layCoc(ma),
                dsCoc -> {
                    List<Object[]> dong = new ArrayList<>();
                    for (HotelDTO.DongLichSuCoc c : dsCoc) {
                        dong.add(
                                new Object[] {
                                    c.getPhuongThuc(),
                                    c.getSoTien(),
                                    c.getKhachDua(),
                                    c.getTienThua(),
                                    c.getTaoLuc(),
                                    c.getCa()
                                });
                    }
                    hienThiChiTiet(
                            "Lịch sử thu cọc",
                            new String[] {
                                "Phương thức",
                                "Thực thu",
                                "Khách đưa",
                                "Tiền thừa",
                                "Lúc nhận",
                                "Ca"
                            },
                            dong);
                });
    }

    public void moDonPhong(String maPhong) {
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                luuTruService::layPhongDangDon,
                dsPhong -> hienThiHopThoaiDonPhong(maPhong, dsPhong));
    }

    private void hienThiHopThoaiDonPhong(String maPhong, List<PhongHotel> dsPhong) {
        if (dsPhong.isEmpty()) {
            HopThoai.hienThiThongTin(
                    this, "Hiện không có phòng nào đang ở trạng thái chờ dọn vệ sinh.");
            return;
        }
        List<Object[]> cacPhongDangDon = new ArrayList<>();
        for (PhongHotel p : dsPhong) {
            cacPhongDangDon.add(new Object[] {p.getMaPhong(), p.getMaPhong()});
        }
        var field =
                BieuMauTruong.TruongDuLieu.layLuaChon("room", "Phòng đang dọn", cacPhongDangDon);
        if (maPhong != null && !maPhong.isBlank())
            field =
                    new BieuMauTruong.TruongDuLieu(
                            field.khoa(), field.nhan(), maPhong, field.cacLuaChon());
        var values = BieuMauTruong.layYeuCauNhap(this, "Hoàn thành dọn phòng", field);
        if (values == null) return;
        String phongChon = values.get("room");
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.hoanTatDonPhong(phongChon),
                () -> {
                    taiLaiNen();
                    HopThoai.hienThiThanhCong(
                            this,
                            "Đã xác nhận dọn phòng xong, phòng đã chuyển sang trạng thái Trống.");
                });
    }

    @Override
    protected void capNhatThaoTac() {
        if (layBangDuLieu() == null || layKhungThaoTac() == null) return;
        String trangThai =
                layBangDuLieu().getSelectedRow() < 0
                        ? ""
                        : String.valueOf(
                                layMoHinh()
                                        .getValueAt(
                                                layBangDuLieu()
                                                        .convertRowIndexToModel(
                                                                layBangDuLieu().getSelectedRow()),
                                                9));
        boolean daChon = !trangThai.isEmpty();
        for (Component thanhPhan : layKhungThaoTac().getComponents())
            if (thanhPhan instanceof JButton btn) {
                switch (btn.getText()) {
                    case "Thu cọc" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiHopDong.DA_DAT.maDB())
                                            && cocConThieu() > 0);
                    case "Điều chỉnh cọc" ->
                            btn.setEnabled(trangThai.equals(TrangThaiHopDong.DA_DAT.maDB()));
                    case "Nhận phòng" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiHopDong.DA_DAT.maDB())
                                            && cocConThieu() <= 0);
                    case "Phiếu chăm sóc" ->
                            btn.setEnabled(trangThai.equals(TrangThaiHopDong.DANG_O.maDB()));
                    case "Quyết toán" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiHopDong.DANG_O.maDB())
                                            && layKieuNghiepVu() != LoaiNghiepVu.NHAT_KY_LUU_TRU);
                    case "Xem nhật ký" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiHopDong.DANG_O.maDB())
                                            || trangThai.equals(
                                                    TrangThaiHopDong.DA_TRA_PHONG.maDB()));
                    case "Lịch sử cọc" -> btn.setEnabled(daChon);
                    case "Sửa đặt phòng" ->
                            btn.setEnabled(trangThai.equals(TrangThaiHopDong.DA_DAT.maDB()));
                    case "Hủy đặt phòng" ->
                            btn.setEnabled(
                                    trangThai.equals(TrangThaiHopDong.DA_DAT.maDB())
                                            && cocDaThu() == 0);
                    default -> {
                        if (btn.getText().startsWith("+ Đặt phòng"))
                            btn.setEnabled(
                                    layKieuNghiepVu() != LoaiNghiepVu.TRA_PHONG_LUU_TRU
                                            && layKieuNghiepVu() != LoaiNghiepVu.NHAT_KY_LUU_TRU);
                    }
                }
            }
    }

    protected long cocConThieu() {
        if (layBangDuLieu().getSelectedRow() < 0 || layMoHinh().getColumnCount() < 11)
            return Long.MAX_VALUE;
        int row = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        return ((Number) layMoHinh().getValueAt(row, 10)).longValue()
                - ((Number) layMoHinh().getValueAt(row, 7)).longValue();
    }

    protected long cocDaThu() {
        if (layBangDuLieu().getSelectedRow() < 0 || layMoHinh().getColumnCount() < 8)
            return Long.MAX_VALUE;
        int row = layBangDuLieu().convertRowIndexToModel(layBangDuLieu().getSelectedRow());
        Object value = layMoHinh().getValueAt(row, 7);
        return value instanceof Number n ? n.longValue() : 0;
    }

    protected void datPhong() {
        String maPhong = layBanHang().getQuyTrinh().getMaPhongHotel();
        String maHopDong = DatPhongHotelDialog.mo(this, null, maPhong);
        if (maHopDong != null) layBanHang().getQuyTrinh().chonHotel(maHopDong);
    }

    protected void suaDatPhong() {
        String maHopDong = layDongChon();
        String result = DatPhongHotelDialog.mo(this, maHopDong, null);
        if (result != null) layBanHang().getQuyTrinh().chonHotel(result);
    }

    protected void huyDatPhong() {
        String maHopDong = layDongChon();
        if (!HopThoai.xacNhanDongY(
                this,
                "Hủy hợp đồng đặt phòng #"
                        + maHopDong
                        + "?\n"
                        + "Chỉ có thể hủy trực tiếp khi hợp đồng chưa thu cọc.",
                "Xác nhận hủy đặt phòng",
                true)) return;
        String reason = HopThoai.nhapChuoiThuan(this, "Lý do hủy đặt phòng:", "Hủy đặt phòng");
        if (reason == null) return;
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.huyDatPhong(maHopDong, reason),
                () -> {
                    layBanHang().getQuyTrinh().chonHotel("");
                    taiLaiNen();
                });
    }

    protected void thuCoc() {
        String ma = layDongChon();
        String maHopDong = layNoiDungDongChon(1);
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> CaThuNganService.caDangMo() ? luuTruService.layMucCoc(ma) : null,
                muc -> {
                    if (muc == null) {
                        hienThiCanhBaoMoCa(
                                "Ca thu ngân hiện chưa mở. Cần mở ca để ghi nhận phiếu thu tiền"
                                        + " cọc.");
                        return;
                    }
                    nhapVaThuCoc(ma, maHopDong, muc);
                });
    }

    private void hienThiCanhBaoMoCa(String thongDiep) {
        if (HopThoai.xacNhanDongY(
                this,
                thongDiep
                        + "\n"
                        + "Bạn có muốn chuyển sang màn hình Ca thu ngân để mở ca ngay bây"
                        + " giờ không?",
                "Chưa mở ca làm việc")) {
            layDieuHuong().accept(MaManHinh.CA_THU_NGAN);
        }
    }

    private void nhapVaThuCoc(String ma, String maHopDong, long[] muc) {
        long thieu = muc[0] - muc[1];
        String input =
                HopThoai.nhapChuoiMacDinh(
                        this,
                        "Yêu cầu cọc: "
                                + DinhDang.dinhDang(muc[0])
                                + " · Đã thu: "
                                + DinhDang.dinhDang(muc[1])
                                + "\nSố tiền thu lần này (tối đa "
                                + DinhDang.dinhDang(thieu)
                                + "):",
                        String.valueOf(thieu));
        if (input == null) return;
        long so = phanTichSoTienCoc(input, thieu);
        String khoa = "COC" + UUID.randomUUID().toString().replace("-", "").substring(0, 22);
        var thu = ThuTienDialog.nhap(this, so, maHopDong);
        if (thu == null) return;
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () ->
                        luuTruService.thuCoc(
                                ma, thu.phuongThuc(), thu.soTien(), thu.khachDua(), khoa),
                this::taiLaiNen,
                ex -> {
                    if (ex.getMessage() != null && ex.getMessage().contains("Cần mở ca")) {
                        hienThiCanhBaoMoCa(ex.getMessage());
                    } else {
                        HopThoai.hienThiCanhBao(this, ex.getMessage());
                    }
                });
    }

    private static long phanTichSoTienCoc(String input, long thieu) {
        String daDon = input.trim().replaceAll("[.,\\s₫đ]", "");
        if (!daDon.matches("\\d+")) {
            throw new IllegalArgumentException("Vui lòng nhập số tiền hợp lệ (chỉ gồm chữ số).");
        }
        long so;
        try {
            so = Long.parseLong(daDon);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Số tiền thu cọc vượt giới hạn hợp lệ.");
        }
        if (so <= 0 || so > thieu) {
            throw new IllegalArgumentException(
                    "Số thu vượt phần cọc còn thiếu (" + DinhDang.dinhDang(thieu) + ").");
        }
        return so;
    }

    protected void doiCoc() {
        String ma = layDongChon();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layMucCoc(ma)[0],
                mucHienTai -> {
                    Map<String, String> v =
                            BieuMauTruong.layYeuCauNhap(
                                    this,
                                    "Điều chỉnh cọc",
                                    layTruongVanBan(
                                            "muc",
                                            "Mức yêu cầu mới (đồng)",
                                            String.valueOf(mucHienTai)),
                                    layTruongVanBan("lydo", "Lý do", ""));
                    if (v == null) return;
                    long mucMoi = docSoTien(v, "muc");
                    String lyDo = v.get("lydo");
                    TacVuNen.chayCoPhien(
                            this,
                            () -> true,
                            () -> luuTruService.dieuChinhCoc(ma, mucMoi, lyDo),
                            this::taiLaiNen);
                });
    }

    protected void layNhatKyChiTiet() {
        String ma = layDongChon();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> luuTruService.layNhatKyChiTiet(ma),
                dsNhatKy -> {
                    List<Object[]> dong = new ArrayList<>();
                    for (HotelDTO.DongNhatKyChiTiet n : dsNhatKy) {
                        dong.add(
                                new Object[] {
                                    n.getThoiGian(),
                                    n.getNhanVien() != null ? n.getNhanVien().getHoTen() : "",
                                    n.getAnUong(),
                                    n.getDaThucHien(),
                                    n.getTinhTrang()
                                });
                    }
                    hienThiChiTiet(
                            "Nhật ký lưu trú",
                            new String[] {
                                "Thời gian", "Nhân viên", "Ăn uống", "Đã thực hiện", "Tình trạng"
                            },
                            dong);
                });
    }

    protected void quyetToan(String loai) {
        if (!layBanHang().kiemTraTrong()) {
            if (HopThoai.xacNhanDongY(
                    this,
                    "Giỏ hàng hiện tại đang có sản phẩm chưa thanh toán.\n"
                            + "Bạn có muốn làm trống giỏ hàng để chuyển sang quyết toán lưu trú"
                            + " không?",
                    "Giỏ hàng đang có sản phẩm")) {
                layBanHang().xoa();
            } else {
                return;
            }
        }
        String ma = layDongChon();
        TacVuNen.chayCoPhien(
                this,
                () -> true,
                () -> banHangService.quyetToan(loai, ma, layBanHang()),
                () -> layDieuHuong().accept(MaManHinh.THANH_TOAN));
    }
}
