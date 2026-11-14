package com.petstation.gui.nhansu;

import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.TruongNghiepVu;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.model.chung.LoaiLichLamViec;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.nhansu.LichLamViecService;
import com.petstation.service.nhansu.LichLamViecService.Muc;
import com.petstation.service.pheduyet.PheDuyetService;
import com.petstation.util.DinhDang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class LichTuanPanel extends CoSoPanel {
    private static final Logger LOG = LoggerFactory.getLogger(LichTuanPanel.class);

    public enum CheDo {
        HIEN_THI_TAT_CA,
        CA_NHAN,
        SUA
    }

    private static final String[] THU = {
        "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"
    };

    private static final Color TIEU_DE_NEN = MauUngDung.mau(0xEAF1FB);

    private static final Color TIEU_DE_CHU = MauUngDung.mau(0x1F6FB5);

    private static final Color DONG_NHAN_NEN = MauUngDung.mau(0xFFFFCC);

    private static final Color BAN_HANG = MauUngDung.mau(0xE6EAEE);

    private static final Color SPA = MauUngDung.mau(0x72D12F);

    private static final Color KHO = MauUngDung.mau(0x92D2F6);

    private static final Color QUAN_LY = MauUngDung.mau(0xFFF28C);

    private static final Color NGHI_PHEP = MauUngDung.mau(0xE04B3A);

    private final CheDo cheDo;

    private final String hienTaiNguoiDung;

    private LocalDate thuHai = LichLamViecService.macDinh().layThuHaiCuaTuan(LocalDate.now());

    private final BoChonNgay oTruongNgay = new BoChonNgay();

    private final JTextField oLocNhanVien = new JTextField(11);

    private final JRadioButton btnTatCa = new JRadioButton("Tất cả", true);

    private final JRadioButton btnLamViec = new JRadioButton("Ca làm việc");

    private final JRadioButton btnNghiPhep = new JRadioButton("Nghỉ phép");

    private final JPanel khungLuoiVungChu = new JPanel(new BorderLayout());

    private final JLabel nhanTomTat = new JLabel(" ");

    private List<Muc> lichTuanHienTai = List.of();

    private Map<String, Object[]> mapNhanVienHienTai = Map.of();

    private int gioLamTrongThangCaNhan;

    private static final class DuLieuTuan {
        final List<Muc> lichTuan;
        final Map<String, Object[]> mapNv;
        final int gioThang;

        DuLieuTuan(List<Muc> lichTuan, Map<String, Object[]> mapNv, int gioThang) {
            this.lichTuan = lichTuan;
            this.mapNv = mapNv;
            this.gioThang = gioThang;
        }
    }

    private static final class DuLieuPhanCa {
        final List<Object[]> dsNhanVien;
        final List<Muc> lichTuan;
        final Map<String, Integer> gioTuanTheoNv;
        final int gioToiDaTuan;

        DuLieuPhanCa(
                List<Object[]> dsNhanVien,
                List<Muc> lichTuan,
                Map<String, Integer> gioTuanTheoNv,
                int gioToiDaTuan) {
            this.dsNhanVien = dsNhanVien;
            this.lichTuan = lichTuan;
            this.gioTuanTheoNv = gioTuanTheoNv;
            this.gioToiDaTuan = gioToiDaTuan;
        }
    }

    public LichTuanPanel(CheDo cheDo, String hienTaiNguoiDung) {
        this.cheDo = cheDo;
        this.hienTaiNguoiDung = hienTaiNguoiDung == null ? "" : hienTaiNguoiDung;
        setLayout(new BorderLayout(0, 10));
        String tieuDe =
                switch (cheDo) {
                    case HIEN_THI_TAT_CA -> "Lịch làm việc theo tuần";
                    case CA_NHAN -> "Lịch làm việc cá nhân theo tuần";
                    case SUA -> "Phân lịch nhân viên theo tuần";
                };
        JLabel nhanTieuDe = taoTieuDeMuc(tieuDe);
        JPanel khungTren = taoVungTrongSuot(new BorderLayout(0, 8));
        khungTren.add(ThanhDanhSach.tieuDe(nhanTieuDe, nhanTomTat), BorderLayout.NORTH);
        khungTren.add(taoThanhCongCu(), BorderLayout.CENTER);
        nhanTomTat.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanTomTat.setForeground(MauUngDung.VAN_BAN_XAM);
        add(khungTren, BorderLayout.NORTH);
        khungLuoiVungChu.setOpaque(false);
        JScrollPane scrCuon = new JScrollPane(khungLuoiVungChu);
        scrCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrCuon.getVerticalScrollBar().setUnitIncrement(16);
        scrCuon.getViewport().setBackground(Color.WHITE);
        add(scrCuon, BorderLayout.CENTER);
        add(taoChuGiai(), BorderLayout.SOUTH);
        LichLamViecService.macDinh().themBoLangNghe(boLangNgheLich);
        dungLai();
    }

    private final BoLangNgheSuKien<String> boLangNgheLich =
            suKien ->
                    SwingUtilities.invokeLater(
                            () -> {
                                if (isShowing()
                                        && PhienDangNhapService.macDinh().layPhienHienTai() != null)
                                    dungLai();
                            });

    @Override
    public void addNotify() {
        super.addNotify();
        LichLamViecService.macDinh().themBoLangNghe(boLangNgheLich);
    }

    @Override
    public void removeNotify() {
        LichLamViecService.macDinh().xoaBoLangNghe(boLangNgheLich);
        super.removeNotify();
    }

    private JPanel taoThanhCongCu() {
        JPanel khungThanh = new JPanel(new BorderLayout(10, 0));
        khungThanh.setOpaque(false);
        khungThanh.add(taoCumBoLocTrai(), BorderLayout.WEST);
        khungThanh.add(taoCumDieuHuongGiua(), BorderLayout.CENTER);
        khungThanh.add(taoCumTacVuPhai(), BorderLayout.EAST);
        return khungThanh;
    }

    private JPanel taoCumBoLocTrai() {
        JPanel khungTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        khungTrai.setOpaque(false);
        ButtonGroup nhom = new ButtonGroup();
        for (JRadioButton btnDongDuLieu : new JRadioButton[] {btnTatCa, btnLamViec, btnNghiPhep}) {
            btnDongDuLieu.setOpaque(false);
            btnDongDuLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            btnDongDuLieu.addActionListener(suKien -> dungLai());
            nhom.add(btnDongDuLieu);
            khungTrai.add(btnDongDuLieu);
        }
        if (cheDo != CheDo.CA_NHAN) {
            JLabel nhanLocNV = new JLabel("Lọc NV:");
            nhanLocNV.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            nhanLocNV.setForeground(MauUngDung.VAN_BAN_XAM);
            khungTrai.add(nhanLocNV);
            oLocNhanVien.setPreferredSize(new Dimension(160, 34));
            oLocNhanVien.setToolTipText("Lọc theo tên, mã NV hoặc chức vụ");
            oLocNhanVien.addActionListener(suKien -> dungLai());
            themBoLangNgheTaiLieu(oLocNhanVien, this::dungLai);
            khungTrai.add(oLocNhanVien);
        }
        return khungTrai;
    }

    private JPanel taoCumDieuHuongGiua() {
        JPanel khungGiua = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        khungGiua.setOpaque(false);
        khungGiua.add(
                taoNutCongCu(
                        "< Tuần trước",
                        NutDinhDang.KieuNut.TRONG_SUOT,
                        110,
                        null,
                        () -> {
                            thuHai = thuHai.minusWeeks(1);
                            dungLai();
                        }));
        oTruongNgay.setPreferredSize(new Dimension(140, 34));
        oTruongNgay.addPropertyChangeListener(
                "date",
                suKien -> {
                    LocalDate ngay = oTruongNgay.layNgayDuocChon();
                    if (ngay == null) return;
                    LocalDate tuan = LichLamViecService.macDinh().layThuHaiCuaTuan(ngay);
                    if (!tuan.equals(thuHai)) {
                        thuHai = tuan;
                        dungLai();
                    }
                });
        khungGiua.add(oTruongNgay);
        khungGiua.add(
                taoNutCongCu(
                        "Tuần sau >",
                        NutDinhDang.KieuNut.TRONG_SUOT,
                        105,
                        null,
                        () -> {
                            thuHai = thuHai.plusWeeks(1);
                            dungLai();
                        }));
        khungGiua.add(
                taoNutCongCu(
                        "Hiện tại",
                        NutDinhDang.KieuNut.PHU,
                        90,
                        null,
                        () -> {
                            thuHai = LichLamViecService.macDinh().layThuHaiCuaTuan(LocalDate.now());
                            dungLai();
                        }));
        return khungGiua;
    }

    private JPanel taoCumTacVuPhai() {
        JPanel khungPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        khungPhai.setOpaque(false);
        if (cheDo == CheDo.SUA) {
            khungPhai.add(
                    taoNutCongCu(
                            "Sao chép tuần trước",
                            NutDinhDang.KieuNut.TRONG_SUOT,
                            150,
                            "Sao chép phân ca từ tuần trước sang tuần này",
                            this::saoChepLichTuan));
            khungPhai.add(
                    taoNutCongCu(
                            "Đổi ca",
                            NutDinhDang.KieuNut.TRONG_SUOT,
                            85,
                            "Hoán đổi ca làm việc trực tiếp giữa 2 nhân viên",
                            this::hoanDoiCaNhanVien));
            khungPhai.add(
                    taoNutCongCu(
                            "Khóa lịch tuần",
                            NutDinhDang.KieuNut.PHU,
                            125,
                            "Khóa lịch tuần này để công bố chính thức",
                            this::khoaLichTuan));
        } else if (cheDo == CheDo.CA_NHAN) {
            khungPhai.add(
                    taoNutCongCu(
                            "Đăng ký nghỉ phép",
                            NutDinhDang.KieuNut.PHU,
                            145,
                            null,
                            () -> ThaoTacNhanh.layNghiPhep(this)));
            khungPhai.add(
                    taoNutCongCu(
                            "Yêu cầu đổi ca",
                            NutDinhDang.KieuNut.TRONG_SUOT,
                            130,
                            null,
                            this::guiYeuCauDoiCaCaNhan));
        }
        khungPhai.add(
                taoNutCongCu(
                        "In lịch", NutDinhDang.KieuNut.TRONG_SUOT, 85, null, this::inLichLamViec));
        return khungPhai;
    }

    private static NutDinhDang taoNutCongCu(
            String nhan, NutDinhDang.KieuNut kieu, int rong, String goiY, Runnable hanhDong) {
        NutDinhDang btn = new NutDinhDang(nhan, kieu);
        btn.setPreferredSize(new Dimension(rong, 34));
        if (goiY != null) btn.setToolTipText(goiY);
        btn.addActionListener(e -> hanhDong.run());
        return btn;
    }

    private JPanel taoChuGiai() {
        JPanel khungChuGiai = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        khungChuGiai.setOpaque(false);
        khungChuGiai.add(layNhan(BAN_HANG, "Nhân viên bán hàng"));
        khungChuGiai.add(layNhan(SPA, "Nhân viên Spa/Hotel"));
        khungChuGiai.add(layNhan(KHO, "Nhân viên kho"));
        khungChuGiai.add(layNhan(QUAN_LY, "Cửa hàng trưởng"));
        khungChuGiai.add(layNhan(NGHI_PHEP, "Nghỉ phép"));
        JLabel nhanCanhBao = new JLabel("Viền đỏ = ca thiếu người");
        nhanCanhBao.setForeground(NGHI_PHEP);
        nhanCanhBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        khungChuGiai.add(nhanCanhBao);
        return khungChuGiai;
    }

    private static JPanel layNhan(Color giaTriDangXet, String vanBan) {
        JPanel khungVungDangXet = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        khungVungDangXet.setOpaque(false);
        JPanel khungHop = new JPanel();
        khungHop.setBackground(giaTriDangXet);
        khungHop.setPreferredSize(new Dimension(44, 14));
        khungHop.setBorder(BorderFactory.createLineBorder(MauUngDung.voiDoTrong(Color.BLACK, 40)));
        JLabel nhanDongDangXet = new JLabel(vanBan);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        khungVungDangXet.add(khungHop);
        khungVungDangXet.add(nhanDongDangXet);
        return khungVungDangXet;
    }

    private void dungLai() {
        oTruongNgay.datNgayDuocChon(thuHai);
        var phien = PhienDangNhapService.macDinh().layPhienHienTai();
        if (phien == null) {
            veLuoiTuan();
            return;
        }
        LocalDate tuanTai = thuHai;
        TacVuNen.chayCoPhien(
                phien,
                () -> true,
                () -> {
                    List<Muc> lichTuan = LichLamViecService.macDinh().layLichTrongTuan(tuanTai);
                    Map<String, Object[]> mapNv = new HashMap<>();
                    for (Object[] nv : DanhMucService.macDinh().layNhanVien()) {
                        mapNv.put(String.valueOf(nv[0]), nv);
                    }
                    int gioThang =
                            cheDo == CheDo.CA_NHAN && !hienTaiNguoiDung.isBlank()
                                    ? LichLamViecService.macDinh()
                                            .tinhGioTrongThang(
                                                    hienTaiNguoiDung, tuanTai.plusDays(3))
                                    : 0;
                    return new DuLieuTuan(lichTuan, mapNv, gioThang);
                },
                null,
                duLieu -> {
                    if (!tuanTai.equals(thuHai)) return;
                    lichTuanHienTai = duLieu.lichTuan;
                    mapNhanVienHienTai = duLieu.mapNv;
                    gioLamTrongThangCaNhan = duLieu.gioThang;
                    veLuoiTuan();
                },
                loi -> TacVuNen.baoLoi(this, loi));
    }

    private void veLuoiTuan() {
        khungLuoiVungChu.removeAll();
        JPanel khungLuoi = new JPanel(new GridBagLayout());
        khungLuoi.setBackground(Color.WHITE);
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.fill = GridBagConstraints.BOTH;
        giaTriDangXet.insets = new Insets(0, 0, 0, 0);
        giaTriDangXet.gridy = 0;
        giaTriDangXet.gridx = 0;
        giaTriDangXet.weightx = 0;
        giaTriDangXet.weighty = 0;
        khungLuoi.add(layOTieuDe("Ca làm", null, false), giaTriDangXet);
        for (int ngayDangXet = 0; ngayDangXet < 7; ngayDangXet++) {
            LocalDate ngay = thuHai.plusDays(ngayDangXet);
            giaTriDangXet.gridx = ngayDangXet + 1;
            giaTriDangXet.weightx = 1;
            khungLuoi.add(
                    layOTieuDe(
                            THU[ngayDangXet],
                            ngay.format(DinhDang.NGAY_CHUAN),
                            ngay.equals(LocalDate.now())),
                    giaTriDangXet);
        }
        for (int chuoi = 0; chuoi < LichLamViecService.CAC_CA_LAM_VIEC.length; chuoi++) {
            giaTriDangXet.gridy = chuoi + 1;
            giaTriDangXet.gridx = 0;
            giaTriDangXet.weightx = 0;
            giaTriDangXet.weighty = 1;
            khungLuoi.add(layNhanDong(chuoi), giaTriDangXet);
            for (int ngayDangXet = 0; ngayDangXet < 7; ngayDangXet++) {
                giaTriDangXet.gridx = ngayDangXet + 1;
                giaTriDangXet.weightx = 1;
                khungLuoi.add(layO(thuHai.plusDays(ngayDangXet), chuoi), giaTriDangXet);
            }
        }
        khungLuoiVungChu.add(khungLuoi, BorderLayout.CENTER);
        capNhatTomTat();
        khungLuoiVungChu.revalidate();
        khungLuoiVungChu.repaint();
    }

    private JPanel layOTieuDe(String tren, String duoi, boolean homNay) {
        JPanel khungVungDangXet = new JPanel(new BorderLayout());
        khungVungDangXet.setBackground(homNay ? MauUngDung.mau(0xD3E6FA) : TIEU_DE_NEN);
        khungVungDangXet.setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 1, MauUngDung.mau(0xC9D6E5)));
        khungVungDangXet.setPreferredSize(new Dimension(duoi == null ? 96 : 104, 52));
        JLabel nhanDongDangXet =
                new JLabel(
                        "<html><div style='text-align:center'>"
                                + tren
                                + (duoi == null ? "" : "<br>" + duoi)
                                + "</div></html>",
                        SwingConstants.CENTER);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanDongDangXet.setForeground(TIEU_DE_CHU);
        khungVungDangXet.add(nhanDongDangXet, BorderLayout.CENTER);
        return khungVungDangXet;
    }

    private JPanel layNhanDong(int caLamViec) {
        JPanel khungVungDangXet = new JPanel(new BorderLayout());
        khungVungDangXet.setBackground(DONG_NHAN_NEN);
        khungVungDangXet.setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, MauUngDung.mau(0xE3E3B5)));
        JLabel nhanDongDangXet =
                new JLabel(
                        "<html><div style='text-align:center'><b>"
                                + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                + "</b><br><span style='font-size:9px;color:#666'>"
                                + LichLamViecService.CA_LAM_VIEC_CAC_THOI_GIAN[caLamViec]
                                + "</span></div></html>",
                        SwingConstants.CENTER);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        nhanDongDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        khungVungDangXet.add(nhanDongDangXet, BorderLayout.CENTER);
        return khungVungDangXet;
    }

    private boolean kiemTraHienThi(Muc suKien) {
        if (btnLamViec.isSelected() && suKien.kiemTraNghiPhep()) return false;
        if (btnNghiPhep.isSelected() && !suKien.kiemTraNghiPhep()) return false;
        if (cheDo == CheDo.CA_NHAN) return suKien.layMaNhanVien().equals(hienTaiNguoiDung);
        String tuKhoa = oLocNhanVien.getText().trim().toLowerCase();
        if (!tuKhoa.isEmpty()) {
            Object[] nv = layNhanVienCua(suKien.layMaNhanVien());
            String ten = nv == null ? "" : String.valueOf(nv[1]).toLowerCase();
            String cv = nv == null ? "" : String.valueOf(nv[4]).toLowerCase();
            if (!suKien.layMaNhanVien().toLowerCase().contains(tuKhoa)
                    && !ten.contains(tuKhoa)
                    && !cv.contains(tuKhoa)) {
                return false;
            }
        }
        return true;
    }

    private JPanel layO(LocalDate ngay, int caLamViec) {
        JPanel khungVungDangXet =
                new JPanel() {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension ngayDangXet = super.getPreferredSize();
                        return new Dimension(
                                Math.max(104, ngayDangXet.width),
                                Math.max(150, ngayDangXet.height));
                    }
                };
        khungVungDangXet.setLayout(new BoxLayout(khungVungDangXet, BoxLayout.Y_AXIS));
        khungVungDangXet.setBackground(
                ngay.equals(LocalDate.now()) ? MauUngDung.mau(0xFCFDFF) : Color.WHITE);
        List<Muc> cacMucTrongO =
                lichTuanHienTai.stream()
                        .filter(m -> m.layNgay().equals(ngay) && m.layCaLamViec() == caLamViec)
                        .toList();
        long soNguoiDiLam = cacMucTrongO.stream().filter(m -> !m.kiemTraNghiPhep()).count();
        boolean ngan1 =
                cheDo != CheDo.CA_NHAN
                        && soNguoiDiLam < LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec];
        khungVungDangXet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, MauUngDung.mau(0xE3E7EC)),
                        ngan1 && !btnNghiPhep.isSelected()
                                ? BorderFactory.createLineBorder(NGHI_PHEP, 2)
                                : new EmptyBorder(2, 2, 2, 2)));
        List<Muc> cacMuc = new ArrayList<>();
        for (Muc suKien : cacMucTrongO) if (kiemTraHienThi(suKien)) cacMuc.add(suKien);
        for (Muc suKien : cacMuc) {
            khungVungDangXet.add(layThe(suKien));
            khungVungDangXet.add(Box.createVerticalStrut(4));
        }
        if (ngan1 && !btnNghiPhep.isSelected()) {
            long can = LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec] - soNguoiDiLam;
            JLabel nhanCanhBao = new JLabel("Thiếu " + can + " người");
            nhanCanhBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            nhanCanhBao.setForeground(NGHI_PHEP);
            nhanCanhBao.setAlignmentX(Component.LEFT_ALIGNMENT);
            khungVungDangXet.add(nhanCanhBao);
        }
        if (cheDo == CheDo.SUA) {
            JLabel nhanThem = new JLabel("+ Thêm nhân viên");
            nhanThem.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            nhanThem.setForeground(TIEU_DE_CHU);
            nhanThem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            nhanThem.setAlignmentX(Component.LEFT_ALIGNMENT);
            nhanThem.addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent suKien) {
                            themNhanVien(ngay, caLamViec);
                        }
                    });
            khungVungDangXet.add(nhanThem);
        }
        return khungVungDangXet;
    }

    private JPanel layThe(Muc suKien) {
        Object[] nhanVien = layNhanVienCua(suKien.layMaNhanVien());
        String ten = nhanVien == null ? suKien.layMaNhanVien() : String.valueOf(nhanVien[1]);
        String dayDuViTri = nhanVien == null ? "" : String.valueOf(nhanVien[4]);
        String viTri =
                dayDuViTri.contains("bán hàng")
                        ? "Bán hàng"
                        : dayDuViTri.contains("Spa")
                                ? "Spa / Hotel"
                                : dayDuViTri.contains("kho") ? "Kho" : dayDuViTri;
        Color nen =
                suKien.kiemTraNghiPhep()
                        ? NGHI_PHEP
                        : dayDuViTri.contains("Spa")
                                ? SPA
                                : dayDuViTri.contains("kho")
                                        ? KHO
                                        : dayDuViTri.contains("trưởng") ? QUAN_LY : BAN_HANG;
        Color chu = suKien.kiemTraNghiPhep() ? Color.WHITE : MauUngDung.mau(0x1B2A3A);
        JPanel khungThe = new JPanel(new BorderLayout());
        khungThe.setBackground(nen);
        khungThe.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.voiDoTrong(Color.BLACK, 45)),
                        new EmptyBorder(5, 6, 5, 6)));
        khungThe.setAlignmentX(Component.LEFT_ALIGNMENT);
        String noiDung =
                "<html><body style='width:88px'><b>"
                        + ten
                        + "</b><br>"
                        + viTri
                        + "<br>"
                        + (suKien.kiemTraNghiPhep()
                                ? "<b>Nghỉ phép</b>"
                                : LichLamViecService.CA_LAM_VIEC_CAC_THOI_GIAN[
                                        suKien.layCaLamViec()])
                        + "</body></html>";
        JLabel nhanDongDangXet = new JLabel(noiDung);
        nhanDongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        nhanDongDangXet.setForeground(chu);
        khungThe.add(nhanDongDangXet, BorderLayout.CENTER);
        int chieuCao = 84;
        khungThe.setPreferredSize(new Dimension(104, chieuCao));
        khungThe.setMinimumSize(new Dimension(0, chieuCao));
        khungThe.setMaximumSize(new Dimension(Integer.MAX_VALUE, chieuCao));
        if (cheDo == CheDo.SUA) {
            khungThe.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            khungThe.setToolTipText("Bấm để bỏ " + ten + " khỏi ca này");
            khungThe.addMouseListener(
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent suKien2) {
                            xoaNhanVien(suKien, ten);
                        }
                    });
        }
        return khungThe;
    }

    private Object[] layNhanVienCua(String ma) {
        return mapNhanVienHienTai.get(ma);
    }

    private void themNhanVien(LocalDate ngay, int caLamViec) {
        moHopThoaiPhanCa(ngay, caLamViec);
    }

    private void xoaNhanVien(Muc suKien, String ten) {
        if (!HopThoai.xacNhanNguyHiem(
                this,
                "Bỏ "
                        + ten
                        + " khỏi "
                        + LichLamViecService.CAC_CA_LAM_VIEC[suKien.layCaLamViec()]
                        + " ngày "
                        + suKien.layNgay().format(DinhDang.NGAY_CHUAN)
                        + "?",
                "Bỏ khỏi ca")) return;
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> {
                    LichLamViecService.macDinh().xoa(suKien);
                    NhatKyHeThongService.macDinh()
                            .ghiNhatKy(
                                    "Sửa",
                                    "Phân lịch: bỏ "
                                            + ten
                                            + " khỏi "
                                            + LichLamViecService.CAC_CA_LAM_VIEC[
                                                    suKien.layCaLamViec()]
                                            + " "
                                            + suKien.layNgay().format(DinhDang.NGAY_CHUAN));
                    return null;
                },
                null,
                v -> dungLai(),
                loi -> TacVuNen.baoLoi(this, loi));
    }

    private void moHopThoaiPhanCa(LocalDate ngay, int caLamViec) {
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        LocalDate tuan = thuHai;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> {
                    List<Object[]> dsNv = DanhMucService.macDinh().layNhanVien();
                    List<Muc> lichTuan = LichLamViecService.macDinh().layLichTrongTuan(tuan);
                    Map<String, Integer> gioTuanTheoNv =
                            LichLamViecService.macDinh().tinhGioTatCaTrongTuan(tuan);
                    int gioToiDaTuan = docGioSoCauHinh("SO_GIO_TOI_DA_MOI_TUAN", 42, 7);
                    return new DuLieuPhanCa(dsNv, lichTuan, gioTuanTheoNv, gioToiDaTuan);
                },
                () -> setCursor(Cursor.getDefaultCursor()),
                duLieu -> hienHopThoaiPhanCa(ngay, caLamViec, duLieu),
                loi -> TacVuNen.baoLoi(this, loi));
    }

    private void hienHopThoaiPhanCa(LocalDate ngay, int caLamViec, DuLieuPhanCa duLieu) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg =
                new JDialog(
                        owner,
                        "Phân ca: "
                                + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                + " ("
                                + LichLamViecService.CA_LAM_VIEC_CAC_THOI_GIAN[caLamViec]
                                + ") - Ngày "
                                + ngay.format(DinhDang.NGAY_CHUAN),
                        Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(0, 10));
        dlg.setSize(680, 500);
        HopThoai.chuanHoaCuaSo(dlg, owner);
        List<Muc> hienCo =
                duLieu.lichTuan.stream()
                        .filter(m -> m.layNgay().equals(ngay) && m.layCaLamViec() == caLamViec)
                        .toList();
        Set<String> maHienCo = new HashSet<>();
        for (Muc m : hienCo) if (!m.kiemTraNghiPhep()) maHienCo.add(m.layMaNhanVien());
        DefaultTableModel model = taoMoHinhPhanCa(ngay, caLamViec, maHienCo, duLieu);
        JTable table = taoBangPhanCa(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        JPanel khungDuoiChinh = taoChanTrangPhanCa(dlg, ngay, caLamViec, hienCo, maHienCo, model);
        dlg.add(taoBoLocPhanCa(sorter), BorderLayout.NORTH);
        dlg.add(new JScrollPane(table), BorderLayout.CENTER);
        dlg.add(khungDuoiChinh, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private static int docGioSoCauHinh(String khoa, int macDinh, int toiThieu) {
        String giaTri =
                CaiDatService.macDinh().layTuyChonHoacMacDinh(khoa, String.valueOf(macDinh));
        try {
            return Math.max(toiThieu, Integer.parseInt(giaTri.trim()));
        } catch (NumberFormatException loi) {
            LOG.warn(
                    "Giá trị cấu hình {} không hợp lệ ('{}'), dùng mặc định {}.",
                    khoa,
                    giaTri,
                    macDinh);
            return macDinh;
        }
    }

    private DefaultTableModel taoMoHinhPhanCa(
            LocalDate ngay, int caLamViec, Set<String> maHienCo, DuLieuPhanCa duLieu) {
        String[] columns = {
            "Chọn", "Mã NV", "Họ tên", "Chức vụ", "Giờ tuần này", "Trạng thái / Ghi chú"
        };
        DefaultTableModel model =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public Class<?> getColumnClass(int col) {
                        return col == 0 ? Boolean.class : String.class;
                    }

                    @Override
                    public boolean isCellEditable(int row, int col) {
                        return col == 0;
                    }
                };
        for (Object[] nv : duLieu.dsNhanVien) {
            if ("Nghỉ việc".equals(String.valueOf(nv[6]))) continue;
            String ma = String.valueOf(nv[0]);
            int gioTuan = duLieu.gioTuanTheoNv.getOrDefault(ma, 0);
            String canhBao =
                    xacDinhCanhBaoPhanCa(
                            ngay, caLamViec, ma, gioTuan, duLieu.gioToiDaTuan, duLieu.lichTuan);
            model.addRow(
                    new Object[] {
                        maHienCo.contains(ma),
                        ma,
                        String.valueOf(nv[1]),
                        String.valueOf(nv[4]),
                        gioTuan + " giờ",
                        canhBao
                    });
        }
        return model;
    }

    private static String xacDinhCanhBaoPhanCa(
            LocalDate ngay,
            int caLamViec,
            String ma,
            int gioTuan,
            int gioToiDaTuan,
            List<Muc> lichTuan) {
        for (Muc m : lichTuan) {
            if (!m.layNgay().equals(ngay) || !m.layMaNhanVien().equals(ma)) continue;
            if (m.kiemTraNghiPhep()) return "[!] Đang nghỉ phép";
            if (m.layCaLamViec() != caLamViec)
                return "[!] Đã có " + LichLamViecService.CAC_CA_LAM_VIEC[m.layCaLamViec()];
        }
        return gioTuan >= gioToiDaTuan
                ? "[!] Đã làm " + gioTuan + "h/" + gioToiDaTuan + "h tuần này"
                : "";
    }

    private static JTable taoBangPhanCa(DefaultTableModel model) {
        JTable table = new JTable(model);
        DinhDangBang.apDung(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        int[] widths = {0, 75, 140, 110, 90, 150};
        for (int i = 1; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        return table;
    }

    private JPanel taoBoLocPhanCa(TableRowSorter<DefaultTableModel> sorter) {
        JPanel khungTrenChinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        khungTrenChinh.setOpaque(false);
        JTextField oTimKiemNhanh = new JTextField(14);
        oTimKiemNhanh.setToolTipText("Tìm tên hoặc mã nhân viên...");
        JComboBox<String> cboRole =
                new JComboBox<>(
                        new String[] {
                            "Tất cả chức vụ", "Bán hàng", "Spa", "Kho", "Cửa hàng trưởng"
                        });
        Runnable filterAction =
                () ->
                        apDungLocPhanCa(
                                sorter,
                                oTimKiemNhanh.getText(),
                                (String) cboRole.getSelectedItem());
        themBoLangNgheTaiLieu(oTimKiemNhanh, filterAction);
        cboRole.addActionListener(e -> filterAction.run());
        khungTrenChinh.add(new JLabel("Tìm:"));
        khungTrenChinh.add(oTimKiemNhanh);
        khungTrenChinh.add(new JLabel("Chức vụ:"));
        khungTrenChinh.add(cboRole);
        return khungTrenChinh;
    }

    private static void apDungLocPhanCa(
            TableRowSorter<DefaultTableModel> sorter, String tuKhoa, String role) {
        String text = tuKhoa.trim().toLowerCase();
        sorter.setRowFilter(
                new RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(
                            Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        String ma = entry.getStringValue(1).toLowerCase();
                        String ten = entry.getStringValue(2).toLowerCase();
                        String cv = entry.getStringValue(3);
                        boolean matchText =
                                text.isEmpty() || ma.contains(text) || ten.contains(text);
                        boolean matchRole =
                                "Tất cả chức vụ".equals(role)
                                        || cv.toLowerCase().contains(role.toLowerCase());
                        return matchText && matchRole;
                    }
                });
    }

    private JPanel taoChanTrangPhanCa(
            JDialog dlg,
            LocalDate ngay,
            int caLamViec,
            List<Muc> hienCo,
            Set<String> maHienCo,
            DefaultTableModel model) {
        JPanel khungDuoiChinh = new JPanel(new BorderLayout(10, 0));
        khungDuoiChinh.setBorder(new EmptyBorder(8, 12, 10, 12));
        JLabel nhanThongTin =
                new JLabel(
                        "Yêu cầu tối thiểu: "
                                + LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec]
                                + " nhân viên cho ca này.");
        nhanThongTin.setFont(MauUngDung.taoPhongChu(Font.ITALIC, 12));
        nhanThongTin.setForeground(MauUngDung.VAN_BAN_XAM);
        JPanel khungChuaNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        NutDinhDang nutLuuThayDoi = new NutDinhDang("Lưu phân ca", NutDinhDang.KieuNut.CHINH);
        NutDinhDang nutHuyBo = new NutDinhDang("Đóng", NutDinhDang.KieuNut.TRONG_SUOT);
        nutLuuThayDoi.addActionListener(
                e -> luuPhanCa(dlg, nutLuuThayDoi, ngay, caLamViec, hienCo, maHienCo, model));
        nutHuyBo.addActionListener(e -> dlg.dispose());
        khungChuaNut.add(nutHuyBo);
        khungChuaNut.add(nutLuuThayDoi);
        khungDuoiChinh.add(nhanThongTin, BorderLayout.WEST);
        khungDuoiChinh.add(khungChuaNut, BorderLayout.EAST);
        return khungDuoiChinh;
    }

    private void luuPhanCa(
            JDialog dlg,
            NutDinhDang nutLuuThayDoi,
            LocalDate ngay,
            int caLamViec,
            List<Muc> hienCo,
            Set<String> maHienCo,
            DefaultTableModel model) {
        int selectedCount = 0;
        List<String> toAdd = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            String ma = String.valueOf(model.getValueAt(r, 1));
            if (Boolean.TRUE.equals(model.getValueAt(r, 0))) {
                selectedCount++;
                if (!maHienCo.contains(ma)) toAdd.add(ma);
            } else if (maHienCo.contains(ma)) {
                toRemove.add(ma);
            }
        }
        int toiThieu = LichLamViecService.NHO_NHAT_NHAN_VIEN[caLamViec];
        if (selectedCount < toiThieu
                && !HopThoai.xacNhan(
                        dlg,
                        "Ca này có "
                                + selectedCount
                                + " người (ít hơn mức tối thiểu "
                                + toiThieu
                                + "). Vẫn tiếp tục lưu?")) {
            return;
        }
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        nutLuuThayDoi.setEnabled(false);
        TacVuNen.chayCoPhien(
                phien,
                dlg::isDisplayable,
                () -> {
                    List<String> cacLoi = new ArrayList<>();
                    for (String ma : toAdd) {
                        String err = LichLamViecService.macDinh().phanCong(ngay, caLamViec, ma);
                        if (err != null) cacLoi.add("Lỗi phân ca " + ma + ": " + err);
                    }
                    for (String ma : toRemove) {
                        hienCo.stream()
                                .filter(x -> x.layMaNhanVien().equals(ma))
                                .findFirst()
                                .ifPresent(m -> LichLamViecService.macDinh().xoa(m));
                    }
                    NhatKyHeThongService.macDinh()
                            .ghiNhatKy(
                                    "Phân lịch",
                                    "Cập nhật "
                                            + LichLamViecService.CAC_CA_LAM_VIEC[caLamViec]
                                            + " ngày "
                                            + ngay.format(DinhDang.NGAY_CHUAN));
                    return cacLoi;
                },
                () -> nutLuuThayDoi.setEnabled(true),
                cacLoi -> {
                    for (String loi : cacLoi) HopThoai.hienThiCanhBao(dlg, loi);
                    dlg.dispose();
                    dungLai();
                },
                loi -> TacVuNen.baoLoi(dlg, loi));
    }

    private void saoChepLichTuan() {
        LocalDate tuanTruoc = thuHai.minusWeeks(1);
        if (!HopThoai.xacNhan(
                this,
                "Sao chép toàn bộ phân ca từ tuần "
                        + tuanTruoc.format(DinhDang.NGAY_CHUAN)
                        + " sang tuần "
                        + thuHai.format(DinhDang.NGAY_CHUAN)
                        + "?")) return;
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        LocalDate tuanDich = thuHai;
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> LichLamViecService.macDinh().saoChepLichTuan(tuanTruoc, tuanDich),
                null,
                thongBao -> {
                    HopThoai.hienThiThongTin(this, thongBao);
                    dungLai();
                },
                loi -> TacVuNen.baoLoi(this, loi));
    }

    private void hoanDoiCaNhanVien() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg =
                new JDialog(
                        owner,
                        "Hoán đổi ca làm việc giữa 2 nhân viên",
                        Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(0, 12));
        dlg.setSize(520, 320);
        HopThoai.chuanHoaCuaSo(dlg, owner);
        JComboBox<String> cboDay1 = new JComboBox<>(), cboDay2 = new JComboBox<>();
        JComboBox<String> cboShift1 = new JComboBox<>(LichLamViecService.CAC_CA_LAM_VIEC);
        JComboBox<String> cboShift2 = new JComboBox<>(LichLamViecService.CAC_CA_LAM_VIEC);
        JComboBox<String> cboStaff1 = new JComboBox<>(), cboStaff2 = new JComboBox<>();
        JPanel khungTrungTam = new JPanel(new GridLayout(2, 1, 0, 10));
        khungTrungTam.setBorder(new EmptyBorder(12, 16, 12, 16));
        khungTrungTam.add(taoKhungChonCaHoanDoi("Ca thứ nhất", cboDay1, cboShift1, cboStaff1));
        khungTrungTam.add(
                taoKhungChonCaHoanDoi("Ca thứ hai (hoán đổi cùng)", cboDay2, cboShift2, cboStaff2));
        JPanel khungCacNutNho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        NutDinhDang nutXacNhanDongY =
                new NutDinhDang("Xác nhận hoán đổi", NutDinhDang.KieuNut.CHINH);
        NutDinhDang nutDongHopThoai = new NutDinhDang("Đóng", NutDinhDang.KieuNut.TRONG_SUOT);
        nutXacNhanDongY.addActionListener(
                e ->
                        thucHienHoanDoiCa(
                                dlg,
                                nutXacNhanDongY,
                                cboDay1,
                                cboShift1,
                                cboStaff1,
                                cboDay2,
                                cboShift2,
                                cboStaff2));
        nutDongHopThoai.addActionListener(e -> dlg.dispose());
        khungCacNutNho.add(nutDongHopThoai);
        khungCacNutNho.add(nutXacNhanDongY);
        dlg.add(khungTrungTam, BorderLayout.CENTER);
        dlg.add(khungCacNutNho, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel taoKhungChonCaHoanDoi(
            String tieuDe,
            JComboBox<String> cboDay,
            JComboBox<String> cboShift,
            JComboBox<String> cboStaff) {
        JPanel khungTam = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        khungTam.setBorder(BorderFactory.createTitledBorder(tieuDe));
        for (int i = 0; i < 7; i++)
            cboDay.addItem(THU[i] + " (" + thuHai.plusDays(i).format(DinhDang.NGAY_CHUAN) + ")");
        Runnable napNv =
                () -> {
                    cboStaff.removeAllItems();
                    int dayIdx = cboDay.getSelectedIndex(), shiftIdx = cboShift.getSelectedIndex();
                    if (dayIdx < 0 || shiftIdx < 0) return;
                    LocalDate ngayChon = thuHai.plusDays(dayIdx);
                    for (Muc m : lichTuanHienTai) {
                        if (!m.layNgay().equals(ngayChon)
                                || m.layCaLamViec() != shiftIdx
                                || m.kiemTraNghiPhep()) continue;
                        Object[] nv = layNhanVienCua(m.layMaNhanVien());
                        cboStaff.addItem(
                                m.layMaNhanVien()
                                        + " - "
                                        + (nv == null ? m.layMaNhanVien() : nv[1]));
                    }
                };
        cboDay.addActionListener(e -> napNv.run());
        cboShift.addActionListener(e -> napNv.run());
        napNv.run();
        khungTam.add(new JLabel("Ngày:"));
        khungTam.add(cboDay);
        khungTam.add(new JLabel("Ca:"));
        khungTam.add(cboShift);
        khungTam.add(new JLabel("Nhân viên:"));
        khungTam.add(cboStaff);
        return khungTam;
    }

    private void thucHienHoanDoiCa(
            JDialog dlg,
            NutDinhDang nutXacNhanDongY,
            JComboBox<String> cboDay1,
            JComboBox<String> cboShift1,
            JComboBox<String> cboStaff1,
            JComboBox<String> cboDay2,
            JComboBox<String> cboShift2,
            JComboBox<String> cboStaff2) {
        if (cboStaff1.getItemCount() == 0 || cboStaff2.getItemCount() == 0) {
            HopThoai.hienThiCanhBao(dlg, "Cả hai ca phải có nhân viên để hoán đổi.");
            return;
        }
        String ma1 = String.valueOf(cboStaff1.getSelectedItem()).split(" ")[0];
        String ma2 = String.valueOf(cboStaff2.getSelectedItem()).split(" ")[0];
        LocalDate ngay1 = thuHai.plusDays(cboDay1.getSelectedIndex());
        LocalDate ngay2 = thuHai.plusDays(cboDay2.getSelectedIndex());
        int ca1 = cboShift1.getSelectedIndex();
        int ca2 = cboShift2.getSelectedIndex();
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        nutXacNhanDongY.setEnabled(false);
        TacVuNen.chayCoPhien(
                phien,
                dlg::isDisplayable,
                () -> LichLamViecService.macDinh().hoanDoiCa(ngay1, ca1, ma1, ngay2, ca2, ma2),
                () -> nutXacNhanDongY.setEnabled(true),
                err -> {
                    if (err != null) {
                        HopThoai.hienThiCanhBao(dlg, err);
                        return;
                    }
                    HopThoai.hienThiThanhCong(dlg, "Đã hoán đổi ca giữa " + ma1 + " và " + ma2);
                    dlg.dispose();
                    dungLai();
                },
                loi -> HopThoai.hienThiCanhBao(dlg, TacVuNen.thongDiepLoi(loi)));
    }

    private void khoaLichTuan() {
        if (!HopThoai.xacNhan(
                this,
                "Khóa lịch tuần "
                        + thuHai.format(DinhDang.NGAY_CHUAN)
                        + " để công bố chính thức? Sau khi khóa, nhân viên chỉ có thể xem hoặc gửi"
                        + " yêu cầu đổi ca.")) return;
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        LocalDate tuan = thuHai;
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> {
                    LichLamViecService.macDinh().khoaLichTuan(tuan);
                    return null;
                },
                null,
                v -> {
                    HopThoai.hienThiThanhCong(this, "Đã khóa lịch tuần thành công.");
                    dungLai();
                },
                loi -> TacVuNen.baoLoi(this, loi));
    }

    private void guiYeuCauDoiCaCaNhan() {
        List<Muc> caCuaToi =
                lichTuanHienTai.stream()
                        .filter(
                                m ->
                                        m.layMaNhanVien().equals(hienTaiNguoiDung)
                                                && !m.kiemTraNghiPhep())
                        .toList();
        if (caCuaToi.isEmpty()) {
            HopThoai.hienThiCanhBao(
                    this, "Bạn chưa có ca làm việc nào trong tuần này để yêu cầu đổi.");
            return;
        }
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> {
                    List<Object[]> dsDongNghiep =
                            DanhMucService.macDinh().layLuaChonNhanVien().stream()
                                    .filter(s -> !s.startsWith(hienTaiNguoiDung + " "))
                                    .map(s -> new Object[] {s.substring(0, s.indexOf(' ')), s})
                                    .toList();
                    int soGioBaoTruoc = docGioSoCauHinh("SO_GIO_BAO_TRUOC_DOI_CA", 12, 0);
                    return new Object[] {dsDongNghiep, soGioBaoTruoc};
                },
                null,
                duLieu -> moHopThoaiYeuCauDoiCa(caCuaToi, duLieu),
                loi -> TacVuNen.baoLoi(this, loi));
    }

    @SuppressWarnings("unchecked")
    private void moHopThoaiYeuCauDoiCa(List<Muc> caCuaToi, Object[] duLieu) {
        List<Object[]> dsDongNghiep = (List<Object[]>) duLieu[0];
        int soGioBaoTruoc = (int) duLieu[1];
        var giaTri = nhapThongTinYeuCauDoiCa(caCuaToi, dsDongNghiep);
        if (giaTri == null) return;
        if (!kiemTraThoiGianBaoTruocDoiCa(giaTri.get("my_shift"), soGioBaoTruoc)) return;
        var phien = PhienDangNhapService.macDinh().yeuCauHienTai();
        String maNhanVien = phien.getNhanVien() != null ? phien.getNhanVien().getMaNhanVien() : "";
        TacVuNen.chayCoPhien(
                phien,
                this::isDisplayable,
                () -> {
                    new PheDuyetService()
                            .guiThaoTac(
                                    PheDuyetService.Loai.NGHI_PHEP,
                                    giaTri.get("reason"),
                                    "Đổi " + giaTri.get("my_shift"),
                                    "Đổi với " + giaTri.get("target_staff"),
                                    LoaiLichLamViec.NGHI_PHEP.maDB(),
                                    maNhanVien,
                                    giaTri.get("target_staff"));
                    return null;
                },
                null,
                v -> HopThoai.hienThiThanhCong(this, "Đã gửi yêu cầu đổi ca đến Cửa hàng trưởng."),
                ex -> HopThoai.hienThiCanhBao(this, TacVuNen.thongDiepLoi(ex)));
    }

    private Map<String, String> nhapThongTinYeuCauDoiCa(
            List<Muc> caCuaToi, List<Object[]> dsDongNghiep) {
        List<Object[]> dsCaCuaToi =
                caCuaToi.stream()
                        .map(
                                m ->
                                        new Object[] {
                                            m.layNgay().format(DinhDang.NGAY_CHUAN)
                                                    + "|"
                                                    + m.layCaLamViec(),
                                            m.layNgay().format(DinhDang.NGAY_CHUAN)
                                                    + " - "
                                                    + LichLamViecService.CAC_CA_LAM_VIEC[
                                                            m.layCaLamViec()]
                                        })
                        .toList();
        return BieuMauTruong.layYeuCauNhap(
                this,
                "Yêu cầu đổi ca làm việc",
                TruongNghiepVu.taoLuaChon("my_shift", "Ca của bạn muốn đổi", dsCaCuaToi),
                TruongNghiepVu.taoLuaChon(
                        "target_staff", "Đồng nghiệp muốn đổi cùng", dsDongNghiep),
                TruongNghiepVu.layTruongVanBan("reason", "Lý do đổi ca", ""));
    }

    private boolean kiemTraThoiGianBaoTruocDoiCa(String caChon, int soGioBaoTruoc) {
        String[] phan = caChon.split("\\|");
        if (phan.length == 2) {
            LocalDate ngayCa = LocalDate.parse(phan[0], DinhDang.NGAY_CHUAN);
            LocalTime gioBatDau =
                    Integer.parseInt(phan[1]) == 0 ? LocalTime.of(7, 30) : LocalTime.of(14, 30);
            if (Duration.between(LocalDateTime.now(), LocalDateTime.of(ngayCa, gioBatDau)).toHours()
                    < soGioBaoTruoc) {
                HopThoai.hienThiCanhBao(
                        this,
                        "Yêu cầu đổi ca phải được gửi trước ít nhất "
                                + soGioBaoTruoc
                                + " giờ so với giờ bắt đầu ca.");
                return false;
            }
        }
        return true;
    }

    private void capNhatTomTat() {
        LocalDate ketThuc = thuHai.plusDays(6);
        String tuan =
                "Tuần "
                        + thuHai.format(DinhDang.NGAY_CHUAN)
                        + " – "
                        + ketThuc.format(DinhDang.NGAY_CHUAN);
        if (cheDo == CheDo.CA_NHAN) {
            Object[] banThan = mapNhanVienHienTai.get(hienTaiNguoiDung);
            if (banThan == null) {
                nhanTomTat.setText(
                        tuan + " · Chưa tìm thấy hồ sơ nhân viên của bạn nên chưa có ca nào.");
                return;
            }
            final Object[] caNhan = banThan;
            long roiKhoi =
                    lichTuanHienTai.stream()
                            .filter(
                                    suKien ->
                                            suKien.layMaNhanVien().equals(caNhan[0])
                                                    && suKien.kiemTraNghiPhep())
                            .count();
            nhanTomTat.setText(
                    tuan
                            + " · Tổng giờ làm trong tháng "
                            + thuHai.getMonthValue()
                            + ": "
                            + gioLamTrongThangCaNhan
                            + " giờ · Nghỉ phép tuần này: "
                            + roiKhoi
                            + " ca");
            return;
        }
        int ngan1 = 0;
        for (int ngayDangXet = 0; ngayDangXet < 7; ngayDangXet++) {
            LocalDate ngayKiem = thuHai.plusDays(ngayDangXet);
            for (int chuoi = 0; chuoi < LichLamViecService.CAC_CA_LAM_VIEC.length; chuoi++) {
                int caKiem = chuoi;
                long dem =
                        lichTuanHienTai.stream()
                                .filter(
                                        m ->
                                                m.layNgay().equals(ngayKiem)
                                                        && m.layCaLamViec() == caKiem
                                                        && !m.kiemTraNghiPhep())
                                .count();
                if (dem < LichLamViecService.NHO_NHAT_NHAN_VIEN[chuoi]) ngan1++;
            }
        }
        nhanTomTat.setText(
                tuan
                        + " · "
                        + (ngan1 == 0 ? "Đủ người ở mọi ca" : ngan1 + " ca thiếu người (viền đỏ)")
                        + (cheDo == CheDo.SUA
                                ? " · Bấm \"+ Thêm nhân viên\" để phân ca, bấm vào thẻ để bỏ khỏi"
                                        + " ca"
                                : ""));
    }

    private void inLichLamViec() {
        PrinterJob congViec = PrinterJob.getPrinterJob();
        congViec.setJobName("Lịch làm việc " + thuHai.format(DinhDang.NGAY_CHUAN));
        congViec.setPrintable(
                new Printable() {
                    @Override
                    public int print(Graphics doHoa, PageFormat dinhDangTrang, int trang)
                            throws PrinterException {
                        if (trang > 0) return NO_SUCH_PAGE;
                        Component dich = khungLuoiVungChu;
                        Graphics2D doHoa2 = (Graphics2D) doHoa;
                        doHoa2.translate(
                                dinhDangTrang.getImageableX(), dinhDangTrang.getImageableY());
                        double gocNgang = dinhDangTrang.getImageableWidth() / dich.getWidth();
                        double gocDoc = dinhDangTrang.getImageableHeight() / dich.getHeight();
                        double chuoi = Math.min(1, Math.min(gocNgang, gocDoc));
                        doHoa2.scale(chuoi, chuoi);
                        dich.printAll(doHoa2);
                        return PAGE_EXISTS;
                    }
                });
        try {
            if (congViec.printDialog()) {
                congViec.print();
                NhatKyHeThongService.macDinh()
                        .ghiNhatKy(
                                "In lịch",
                                "Lịch làm việc tuần " + thuHai.format(DinhDang.NGAY_CHUAN));
            }
        } catch (PrinterException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Không in được: " + ngoaiLe.getMessage());
        } catch (HeadlessException ngoaiLe) {
            HopThoai.hienThiLoi(this, "Không tìm thấy máy in.");
        }
    }

    @Override
    public void xuLyHienThi() {
        dungLai();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Lịch làm việc theo tuần";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return switch (cheDo) {
            case HIEN_THI_TAT_CA ->
                    "Lịch cả cửa hàng theo tuần. Dùng Trở về / Tiếp, chọn ngày hoặc Hiện tại để đổi"
                            + " tuần. Ô viền đỏ là ca thiếu người.";
            case CA_NHAN ->
                    "Ca của bạn trong tuần, tổng giờ trong tháng. Bấm \"Đăng ký nghỉ phép\" để gửi"
                            + " yêu cầu sang Cửa hàng trưởng duyệt.";
            case SUA ->
                    "Chọn tuần rồi bấm \"+ Thêm nhân viên\" trong ô để phân ca; bấm vào thẻ nhân"
                            + " viên để bỏ khỏi ca. Ô viền đỏ là ca thiếu người.";
        };
    }
}
