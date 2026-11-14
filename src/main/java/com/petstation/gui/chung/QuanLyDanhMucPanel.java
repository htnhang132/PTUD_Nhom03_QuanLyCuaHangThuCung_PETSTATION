package com.petstation.gui.chung;

import com.petstation.gui.chung.thanhphan.BoChonNgay;
import com.petstation.gui.chung.thanhphan.BoSoanThaoTruong;
import com.petstation.gui.chung.thanhphan.DinhDangBang;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class QuanLyDanhMucPanel extends CoSoPanel {
    public enum Kieu {
        VAN_BAN,
        MAT_KHAU,
        SO_DIEN_THOAI,
        EMAIL,
        CCCD,
        NGAY,
        TIEN_TE,
        SO_NGUYEN,
        HOP_CHON,
        VUNG,
        TEP,
        TUY_CHINH
    }

    public static final class TruongDuLieu {
        final String nhan;
        final Kieu kieu;
        boolean batBuoc, duyNhat, docChi, vaoDanhSach = true;
        boolean trongBieuMau = true;
        boolean hienNhan = true;
        boolean nhieuDong = false;
        int doRongToiThieu = -1;
        int doRongUuTien = -1;
        int doRongToiDa = -1;

        public TruongDuLieu datDoRong(int toiThieu, int uuTien, int toiDa) {
            this.doRongToiThieu = toiThieu;
            this.doRongUuTien = uuTien;
            this.doRongToiDa = toiDa;
            return this;
        }

        public TruongDuLieu nhieuDong() {
            this.nhieuDong = true;
            return this;
        }

        public TruongDuLieu anNhan() {
            this.hienNhan = false;
            return this;
        }

        String[] cacLuaChon = new String[0];
        Supplier<List<String>> dongCacLuaChon;
        String giaTriMacDinh = "";
        Supplier<BoSoanThaoTruong> boSoanThaoBoTao;
        Predicate<String> coTheSuaKhi;
        Function<String, String> hienThi;

        private TruongDuLieu(String nhan, Kieu kieu) {
            this.nhan = nhan;
            this.kieu = kieu;
        }

        public static TruongDuLieu tao(String nhan, Kieu kieu) {
            return new TruongDuLieu(nhan, kieu);
        }

        public static TruongDuLieu chuyenThanhChuoi(String nhan) {
            return new TruongDuLieu(nhan, Kieu.VAN_BAN);
        }

        public TruongDuLieu batBuoc() {
            this.batBuoc = true;
            return this;
        }

        public TruongDuLieu kiemTraDuyNhat() {
            this.duyNhat = true;
            return this;
        }

        public TruongDuLieu chiDoc() {
            this.docChi = true;
            return this;
        }

        public TruongDuLieu an() {
            this.vaoDanhSach = false;
            return this;
        }

        public TruongDuLieu datLuaChon(String... doiTuong) {
            this.cacLuaChon = doiTuong;
            return this;
        }

        public TruongDuLieu layDong(Supplier<List<String>> chuoi) {
            this.dongCacLuaChon = chuoi;
            return this;
        }

        public TruongDuLieu datMacDinh(String giaTri) {
            this.giaTriMacDinh = giaTri;
            return this;
        }

        public TruongDuLieu datBoSoanThao(Supplier<BoSoanThaoTruong> truongDangXet) {
            this.boSoanThaoBoTao = truongDangXet;
            return this;
        }

        public TruongDuLieu choSuaKhi(Predicate<String> vungDangXet) {
            this.coTheSuaKhi = vungDangXet;
            return this;
        }

        public TruongDuLieu hienThi(Function<String, String> truongDangXet) {
            this.hienThi = truongDangXet;
            return this;
        }
    }

    public static final class ThaoTacBoSung {
        private final String nhan;
        private final Function<Object[], String> boXuLy;

        public ThaoTacBoSung(String nhan, Function<Object[], String> boXuLy) {
            this.nhan = nhan;
            this.boXuLy = boXuLy;
        }

        public String nhan() {
            return nhan;
        }

        public Function<Object[], String> boXuLy() {
            return boXuLy;
        }
    }

    public static final class CauHinh {
        String tieuDe, timKiemGoiY = "Tìm kiếm...", tenThucThe = "bản ghi", maTienTo = "";
        int chiSoTrangThai = -1;
        int chiSoTruongDau = -1;

        public CauHinh datTruongDau(int chiSo) {
            this.chiSoTruongDau = chiSo;
            return this;
        }

        String nhanHoatDong = "Đang hoạt động", nhanNgungHoatDong = "Ngừng hoạt động";
        Consumer<Object[]> boChuanHoa;
        BiConsumer<Object[], Object[]> khiDaLuu;
        Function<Object[], String> xoaDieuKienBaoVe;
        Function<Object[], String> boSungBoKiemTra;
        final List<ThaoTacBoSung> cacThaoTacBoSung = new ArrayList<>();
        final List<BoLocDanhMuc> cacBoLoc = new ArrayList<>();

        public CauHinh themBoLoc(String nhan, Function<Object[], String> giaTri) {
            cacBoLoc.add(new BoLocDanhMuc(nhan, giaTri));
            return this;
        }

        String tenXuat = "Danh_muc";

        public static CauHinh tao(String tieuDe, String tenThucThe) {
            CauHinh giaTriDangXet = new CauHinh();
            giaTriDangXet.tieuDe = tieuDe;
            giaTriDangXet.tenThucThe = tenThucThe;
            giaTriDangXet.tenXuat = tieuDe.replace(' ', '_');
            return giaTriDangXet;
        }

        public CauHinh timKiem(String goiY) {
            this.timKiemGoiY = goiY;
            return this;
        }

        public CauHinh datTienTo(String vungDangXet) {
            this.maTienTo = vungDangXet;
            return this;
        }

        public CauHinh datTrangThai(int chiSo, String hoatDong, String ngungHoatDong) {
            this.chiSoTrangThai = chiSo;
            this.nhanHoatDong = hoatDong;
            this.nhanNgungHoatDong = ngungHoatDong;
            return this;
        }

        public CauHinh datBoChuanHoa(Consumer<Object[]> soPhanTu) {
            this.boChuanHoa = soPhanTu;
            return this;
        }

        public CauHinh xuLySauLuu(BiConsumer<Object[], Object[]> truongDangXet) {
            this.khiDaLuu = truongDangXet;
            return this;
        }

        public CauHinh datDieuKienXoa(Function<Object[], String> doHoa) {
            this.xoaDieuKienBaoVe = doHoa;
            return this;
        }

        public CauHinh datBoKiemTra(Function<Object[], String> giaTri) {
            this.boSungBoKiemTra = giaTri;
            return this;
        }

        public CauHinh themThaoTacBoSung(String nhan, Function<Object[], String> boXuLy) {
            cacThaoTacBoSung.add(new ThaoTacBoSung(nhan, boXuLy));
            return this;
        }
    }

    public static final class BoLocDanhMuc {
        private final String nhan;
        private final Function<Object[], String> giaTri;

        public BoLocDanhMuc(String nhan, Function<Object[], String> giaTri) {
            this.nhan = nhan;
            this.giaTri = giaTri;
        }

        public String nhan() {
            return nhan;
        }

        public Function<Object[], String> giaTri() {
            return giaTri;
        }
    }

    private final List<JComboBox<String>> cboCacBoLoc = new ArrayList<>();
    private final JComboBox<String> cboTrangThai = new JComboBox<>();
    private final JLabel nhanSoKetQua = new JLabel();
    private JTextField oTimKiemChung;

    private enum CheDo {
        HIEN_THI,
        SUA,
        MOI
    }

    private final CauHinh cauHinh;

    private final List<TruongDuLieu> cacTruong;

    private final List<Object[]> cacDong;

    private final List<Object[]> daHienThi = new ArrayList<>();

    private final DefaultTableModel moHinh;

    private final JTable bangBang;

    private final JComponent[] cacDauVao;

    private final BoSoanThaoTruong[] cacBoSoanThao;

    private final JLabel nhanTieuDeBieuMau = new JLabel();

    private final JLabel nhanThongBao = new JLabel(" ");

    private final NutDinhDang btnNutSua = new NutDinhDang("Sửa", NutDinhDang.KieuNut.PHU);

    private final NutDinhDang btnNutXoa = new NutDinhDang("Xóa", NutDinhDang.KieuNut.NGUY_HIEM);

    private final NutDinhDang btnNutLuu = new NutDinhDang("Lưu", NutDinhDang.KieuNut.THANH_CONG);

    private final NutDinhDang btnNutHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);

    private final List<NutDinhDang> boSungCacNut = new ArrayList<>();

    private final List<Integer> danhSachCacTruong = new ArrayList<>();

    private CheDo cheDo = CheDo.HIEN_THI;

    private Object[] hienTai;

    private boolean dangChon;

    public QuanLyDanhMucPanel(
            CauHinh cauHinh, List<TruongDuLieu> cacTruong, List<Object[]> cacDong) {
        this.cauHinh = cauHinh;
        this.cacTruong = cacTruong;
        this.cacDong = cacDong;
        setLayout(new BorderLayout(0, 12));
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++)
            if (cacTruong.get(chiSo).vaoDanhSach) danhSachCacTruong.add(chiSo);
        for (Object[] dongDuLieu : cacDong) chuanHoa(dongDuLieu);
        lamMoiLuaChonLoc();
        String[] cacCot =
                danhSachCacTruong.stream()
                        .map(chiSo -> cacTruong.get(chiSo).nhan)
                        .toArray(String[]::new);
        moHinh = DinhDangBang.taoMoHinhChiDoc(cacCot);
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        bangBang = layBang(scrCuon);
        cauHinhDoRongCotBang();
        ThanhDanhSach khungCacThaoTac = taoThanhThaoTacDanhMuc(cauHinh);
        JPanel khungTieuDe = new JPanel(new BorderLayout(12, 0));
        khungTieuDe.setOpaque(false);
        khungTieuDe.add(
                ThanhDanhSach.tieuDe(taoTieuDeMuc(cauHinh.tieuDe), nhanSoKetQua),
                BorderLayout.NORTH);
        khungTieuDe.add(khungCacThaoTac, BorderLayout.CENTER);
        JPanel khungTren = taoVungTrongSuot(new BorderLayout(0, 8));
        khungTren.add(khungTieuDe, BorderLayout.NORTH);
        add(khungTren, BorderLayout.NORTH);
        cacDauVao = new JComponent[cacTruong.size()];
        cacBoSoanThao = new BoSoanThaoTruong[cacTruong.size()];
        add(taoSplitDanhMuc(scrCuon), BorderLayout.CENTER);
        ganSuKienBangDanhMuc();
        taiLai(null);
        datCheDo(CheDo.HIEN_THI);
        if (cacDong.isEmpty()
                && cacDong instanceof DanhMucService.CacDong d
                && SwingUtilities.isEventDispatchThread()
                && DieuKienTaiDuLieu.coTheTachLuong()) napDuLieuNen(d);
    }

    private void cauHinhDoRongCotBang() {
        for (int colIdx = 0; colIdx < danhSachCacTruong.size(); colIdx++) {
            TruongDuLieu truong = cacTruong.get(danhSachCacTruong.get(colIdx));
            if (colIdx >= bangBang.getColumnModel().getColumnCount()) continue;
            TableColumn col = bangBang.getColumnModel().getColumn(colIdx);
            if (truong.doRongToiThieu > 0) col.setMinWidth(truong.doRongToiThieu);
            if (truong.doRongToiDa > 0) col.setMaxWidth(truong.doRongToiDa);
            else if (truong.doRongUuTien > 0) col.setMaxWidth(Integer.MAX_VALUE);
            if (truong.doRongUuTien > 0) col.setPreferredWidth(truong.doRongUuTien);
        }
        bangBang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private ThanhDanhSach taoThanhThaoTacDanhMuc(CauHinh cauHinh) {
        JTextField oTimKiem = taoOTimKiem(cauHinh.timKiemGoiY);
        lienKetBoLoc(oTimKiem, bangBang);
        oTimKiemChung = oTimKiem;
        oTimKiem.setName("timKiemDanhMuc");
        oTimKiem.setPreferredSize(new Dimension(205, 32));
        bangBang.getRowSorter().addRowSorterListener(e -> capNhatSoKetQua());
        NutDinhDang btnThem = new NutDinhDang("+ Thêm", NutDinhDang.KieuNut.CHINH);
        btnThem.setEnabled(coTheChinhSua());
        btnThem.addActionListener(suKien -> batDauTaoMoi());
        btnNutSua.addActionListener(suKien -> batDauSua());
        btnNutXoa.addActionListener(suKien -> layNhatXoa());
        NutDinhDang btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(
                suKien -> XuatExcelDialog.xuatBang(this, bangBang, cauHinh.tenXuat));
        ThanhDanhSach khungCacThaoTac = new ThanhDanhSach();
        thanhDuLieu = khungCacThaoTac;
        khungCacThaoTac.setOpaque(false);
        khungCacThaoTac.add(oTimKiem);
        taoThanhBoLoc(khungCacThaoTac);
        khungCacThaoTac.add(btnThem);
        for (ThaoTacBoSung ngoaiLe : cauHinh.cacThaoTacBoSung) {
            NutDinhDang btnGiaTriSau = new NutDinhDang(ngoaiLe.nhan(), NutDinhDang.KieuNut.PHU);
            btnGiaTriSau.addActionListener(suKien -> chayBoSung(ngoaiLe));
            boSungCacNut.add(btnGiaTriSau);
        }
        khungCacThaoTac.add(btnXuat);
        return khungCacThaoTac;
    }

    private JSplitPane taoSplitDanhMuc(JScrollPane scrCuon) {
        JSplitPane tach = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrCuon, taoBieuMau());
        tach.setResizeWeight(1.0);
        tach.setBorder(null);
        tach.setOpaque(false);
        tach.setContinuousLayout(true);
        return tach;
    }

    private void ganSuKienBangDanhMuc() {
        bangBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> {
                            if (suKien.getValueIsAdjusting() || dangChon) return;
                            layKhiLuaChonDaThayDoi();
                        });
        bangBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (suKien.getClickCount() == 2
                                && hienTai != null
                                && cheDo == CheDo.HIEN_THI) batDauSua();
                    }
                });
    }

    private static final class KhungBieuMauCuon extends JPanel implements Scrollable {
        KhungBieuMauCuon() {
            super(new GridBagLayout());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private JPanel taoBieuMau() {
        JPanel khungBieuMau = new KhungBieuMauCuon();
        khungBieuMau.setBackground(Color.WHITE);
        khungBieuMau.setBorder(new EmptyBorder(14, 16, 14, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        int toaDoDoc = dienCacTruongVaoBieuMau(khungBieuMau, gbc);
        nhanThongBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        gbc.gridy = toaDoDoc++;
        gbc.insets = new Insets(10, 0, 0, 0);
        khungBieuMau.add(nhanThongBao, gbc);
        btnNutHuy.addActionListener(suKien -> huySua());
        btnNutLuu.addActionListener(suKien -> luu());
        gbc.gridy = toaDoDoc;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel khoangTrong = new JPanel();
        khoangTrong.setOpaque(false);
        khungBieuMau.add(khoangTrong, gbc);
        JScrollPane scrKhungCuon =
                new JScrollPane(
                        khungBieuMau,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrKhungCuon.getVerticalScrollBar().setUnitIncrement(16);
        JPanel khungVungChua = new JPanel(new BorderLayout());
        khungVungChua.setPreferredSize(new Dimension(380, 0));
        khungVungChua.setMinimumSize(new Dimension(320, 0));
        khungVungChua.add(scrKhungCuon, BorderLayout.CENTER);
        khungVungChua.add(taoChanTrangBieuMau(), BorderLayout.SOUTH);
        return khungVungChua;
    }

    private int dienCacTruongVaoBieuMau(JPanel khungBieuMau, GridBagConstraints gbc) {
        int toaDoDoc = 0;
        nhanTieuDeBieuMau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        gbc.gridy = toaDoDoc++;
        khungBieuMau.add(nhanTieuDeBieuMau, gbc);
        List<Integer> thuTuBieuMau = new ArrayList<>();
        if (cauHinh.chiSoTruongDau >= 0 && cauHinh.chiSoTruongDau < cacTruong.size())
            thuTuBieuMau.add(cauHinh.chiSoTruongDau);
        for (int i = 0; i < cacTruong.size(); i++)
            if (i != cauHinh.chiSoTruongDau) thuTuBieuMau.add(i);
        for (int chiSo : thuTuBieuMau) {
            TruongDuLieu truong = cacTruong.get(chiSo);
            JComponent dauVao = taoDauVao(chiSo, truong);
            cacDauVao[chiSo] = dauVao;
            if (!truong.trongBieuMau) continue;
            if (truong.hienNhan && !truong.nhan.isBlank()) {
                JLabel nhanHienThi = new JLabel(truong.nhan + (truong.batBuoc ? " *" : ""));
                nhanHienThi.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
                nhanHienThi.setForeground(MauUngDung.VAN_BAN_XAM);
                gbc.gridy = toaDoDoc++;
                gbc.insets = new Insets(8, 0, 1, 0);
                khungBieuMau.add(nhanHienThi, gbc);
            }
            gbc.gridy = toaDoDoc++;
            gbc.insets = new Insets(0, 0, 0, 0);
            khungBieuMau.add(dauVao, gbc);
        }
        return toaDoDoc;
    }

    private JPanel taoChanTrangBieuMau() {
        JPanel khungChanHopThoai = new JPanel(new BorderLayout(0, 4));
        khungChanHopThoai.setBackground(Color.WHITE);
        khungChanHopThoai.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 0, 0, 0, MauUngDung.VIEN),
                        new EmptyBorder(6, 6, 6, 6)));
        if (!boSungCacNut.isEmpty()) {
            JPanel khungThaoTacPhu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
            khungThaoTacPhu.setOpaque(false);
            for (NutDinhDang btnGiaTriSau : boSungCacNut) khungThaoTacPhu.add(btnGiaTriSau);
            khungChanHopThoai.add(khungThaoTacPhu, BorderLayout.NORTH);
        }
        JPanel khungThaoTacChinh = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        khungThaoTacChinh.setOpaque(false);
        khungThaoTacChinh.add(btnNutSua);
        khungThaoTacChinh.add(btnNutXoa);
        khungThaoTacChinh.add(btnNutHuy);
        khungThaoTacChinh.add(btnNutLuu);
        khungChanHopThoai.add(khungThaoTacChinh, BorderLayout.SOUTH);
        return khungChanHopThoai;
    }

    private JComponent taoDauVao(int chiSo, TruongDuLieu truongDangXet) {
        if (truongDangXet.nhieuDong) return taoVungVanBanCuon(2, 56, 48);
        return switch (truongDangXet.kieu) {
            case MAT_KHAU -> new JPasswordField(20);
            case HOP_CHON -> taoHopChonDauVao(truongDangXet);
            case VUNG -> taoVungVanBanCuon(3, 96, 72);
            case TUY_CHINH -> {
                BoSoanThaoTruong boSoanThao = truongDangXet.boSoanThaoBoTao.get();
                cacBoSoanThao[chiSo] = boSoanThao;
                yield boSoanThao.layThanhPhan();
            }
            case TEP -> taoChonTepDauVao(truongDangXet);
            case NGAY -> taoChonNgayDauVao(truongDangXet);
            default -> layVanBanDauVao(truongDangXet);
        };
    }

    private static JScrollPane taoVungVanBanCuon(int soDong, int caoUuTien, int caoToiThieu) {
        JTextArea oNhapLieu =
                new JTextArea(soDong, 20) {
                    @Override
                    public boolean getScrollableTracksViewportWidth() {
                        return true;
                    }
                };
        oNhapLieu.setLineWrap(true);
        oNhapLieu.setWrapStyleWord(true);
        oNhapLieu.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oNhapLieu.setBorder(new EmptyBorder(4, 6, 4, 6));
        JScrollPane scr =
                new JScrollPane(
                        oNhapLieu,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scr.setPreferredSize(new Dimension(100, caoUuTien));
        scr.setMinimumSize(new Dimension(100, caoToiThieu));
        return scr;
    }

    private JComboBox<String> taoHopChonDauVao(TruongDuLieu truongDangXet) {
        JComboBox<String> cboHop =
                new JComboBox<>(layCacLuaChonCua(truongDangXet)) {
                    @Override
                    public String getToolTipText(MouseEvent event) {
                        Object chon = getSelectedItem();
                        if (chon != null) {
                            String vanBan = chon.toString();
                            if (!vanBan.isEmpty()
                                    && getWidth() > 32
                                    && getFontMetrics(getFont()).stringWidth(vanBan)
                                            > getWidth() - 32) {
                                return vanBan;
                            }
                        }
                        return super.getToolTipText(event);
                    }
                };
        cboHop.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        ToolTipManager.sharedInstance().registerComponent(cboHop);
        return cboHop;
    }

    private JPanel taoChonTepDauVao(TruongDuLieu truongDangXet) {
        JPanel khungTam = new JPanel(new BorderLayout(6, 0));
        khungTam.setOpaque(false);
        JTextField oGiaTriTam = layVanBanDauVao(truongDangXet);
        NutDinhDang btnChon = new NutDinhDang("Chọn ảnh...", NutDinhDang.KieuNut.TRONG_SUOT);
        btnChon.addActionListener(
                suKien -> {
                    JFileChooser chonTep = new JFileChooser();
                    chonTep.setFileFilter(
                            new FileNameExtensionFilter("Ảnh (jpg, png)", "jpg", "jpeg", "png"));
                    if (chonTep.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                        oGiaTriTam.setText(chonTep.getSelectedFile().getAbsolutePath());
                });
        khungTam.add(oGiaTriTam, BorderLayout.CENTER);
        khungTam.add(btnChon, BorderLayout.EAST);
        khungTam.putClientProperty("text", oGiaTriTam);
        return khungTam;
    }

    private JPanel taoChonNgayDauVao(TruongDuLieu truongDangXet) {
        JPanel khungTam = new JPanel(new BorderLayout(6, 0));
        khungTam.setOpaque(false);
        JTextField oGiaTriTam = layVanBanDauVao(truongDangXet);
        JButton btnChonNgay = BoChonNgay.taoNutLich(oGiaTriTam, DinhDang.NGAY_CHUAN);
        btnChonNgay.setName("chonNgay:" + truongDangXet.nhan);
        btnChonNgay.setPreferredSize(new Dimension(36, 30));
        khungTam.add(oGiaTriTam, BorderLayout.CENTER);
        khungTam.add(btnChonNgay, BorderLayout.EAST);
        khungTam.putClientProperty("text", oGiaTriTam);
        khungTam.putClientProperty("button", btnChonNgay);
        return khungTam;
    }

    private JTextField layVanBanDauVao(TruongDuLieu truongDangXet) {
        JTextField oGiaTriTam =
                new JTextField() {
                    @Override
                    public String getToolTipText(MouseEvent event) {
                        String vanBan = getText();
                        if (vanBan != null && !vanBan.isEmpty()) {
                            Insets le = getInsets();
                            int rongKhaDung = getWidth() - le.left - le.right;
                            int rongChu = getFontMetrics(getFont()).stringWidth(vanBan);
                            if (rongKhaDung > 0 && rongChu > rongKhaDung) {
                                return vanBan;
                            }
                        }
                        return super.getToolTipText(event);
                    }
                };
        oGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        oGiaTriTam.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(5, 8, 5, 8)));
        if (truongDangXet.kieu == Kieu.NGAY)
            oGiaTriTam.setToolTipText("Định dạng dd/MM/yyyy hoặc chọn từ lịch");
        ToolTipManager.sharedInstance().registerComponent(oGiaTriTam);
        return oGiaTriTam;
    }

    private String[] layCacLuaChonCua(TruongDuLieu truongDangXet) {
        if (truongDangXet.dongCacLuaChon != null)
            return truongDangXet.dongCacLuaChon.get().toArray(String[]::new);
        return truongDangXet.cacLuaChon;
    }

    private String layDauVao(int chiSo) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        JComponent vao = cacDauVao[chiSo];
        if (truongDangXet.nhieuDong || truongDangXet.kieu == Kieu.VUNG) {
            return ((JTextArea) ((JScrollPane) vao).getViewport().getView()).getText().trim();
        }
        return switch (truongDangXet.kieu) {
            case HOP_CHON -> {
                Object chuoi = ((JComboBox<?>) vao).getSelectedItem();
                yield chuoi == null ? "" : chuoi.toString();
            }
            case TUY_CHINH -> cacBoSoanThao[chiSo].lay();
            case TEP -> ((JTextField) vao.getClientProperty("text")).getText().trim();
            case NGAY -> {
                String vanBan = ((JTextField) vao.getClientProperty("text")).getText().trim();
                LocalDate ngay = BoChonNgay.docNgayLinhHoat(vanBan, DinhDang.NGAY_CHUAN);
                yield ngay != null ? DinhDang.NGAY_CHUAN.format(ngay) : vanBan;
            }
            case TIEN_TE -> {
                long moHinhDangXet = DinhDang.doc(((JTextField) vao).getText());
                yield moHinhDangXet < 0
                        ? ((JTextField) vao).getText().trim().isEmpty() ? "" : "?"
                        : String.valueOf(moHinhDangXet);
            }
            default -> ((JTextField) vao).getText().trim();
        };
    }

    @SuppressWarnings("unchecked")
    private void datDauVao(int chiSo, String giaTri) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        JComponent vao = cacDauVao[chiSo];
        String giaTri2 = giaTri == null ? "" : giaTri;
        if (truongDangXet.nhieuDong || truongDangXet.kieu == Kieu.VUNG) {
            JTextArea oNhapLieu = (JTextArea) ((JScrollPane) vao).getViewport().getView();
            oNhapLieu.setText(giaTri2);
            oNhapLieu.setCaretPosition(0);
            return;
        }
        switch (truongDangXet.kieu) {
            case HOP_CHON -> {
                JComboBox<String> cboHop = (JComboBox<String>) vao;
                cboHop.setModel(new DefaultComboBoxModel<>(layCacLuaChonCua(truongDangXet)));
                cboHop.setSelectedItem(giaTri2);
                if (cboHop.getSelectedItem() == null || !cboHop.getSelectedItem().equals(giaTri2)) {
                    if (!giaTri2.isEmpty()) {
                        cboHop.addItem(giaTri2);
                        cboHop.setSelectedItem(giaTri2);
                    } else if (cboHop.getItemCount() > 0) cboHop.setSelectedIndex(0);
                }
            }
            case TUY_CHINH -> cacBoSoanThao[chiSo].dat(giaTri2);
            case TEP, NGAY -> ((JTextField) vao.getClientProperty("text")).setText(giaTri2);
            case TIEN_TE ->
                    ((JTextField) vao)
                            .setText(
                                    giaTri2.isEmpty()
                                            ? ""
                                            : DinhDang.dinhDang(DinhDang.doc(giaTri2)));
            default -> ((JTextField) vao).setText(giaTri2);
        }
    }

    private void datDauVaoDuocBat(int chiSo, boolean duocBat) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        JComponent vao = cacDauVao[chiSo];
        if (truongDangXet.nhieuDong || truongDangXet.kieu == Kieu.VUNG) {
            JTextArea oNhapLieu = (JTextArea) ((JScrollPane) vao).getViewport().getView();
            oNhapLieu.setEditable(duocBat);
            oNhapLieu.setEnabled(duocBat);
            oNhapLieu.setForeground(duocBat ? MauUngDung.VAN_BAN_TOI : MauUngDung.VO_HIEU_HOA);
            oNhapLieu.setDisabledTextColor(MauUngDung.VO_HIEU_HOA);
            Color bg = duocBat ? Color.WHITE : MauUngDung.XAM_TRUNG_TINH;
            oNhapLieu.setBackground(bg);
            ((JScrollPane) vao).getViewport().setBackground(bg);
            vao.setBackground(bg);
        } else if (truongDangXet.kieu == Kieu.TUY_CHINH) {
            cacBoSoanThao[chiSo].datDuocBat(duocBat);
        } else if (truongDangXet.kieu == Kieu.TEP || truongDangXet.kieu == Kieu.NGAY) {
            vao.setEnabled(duocBat);
            for (Component khoaDangXet : ((JPanel) vao).getComponents())
                khoaDangXet.setEnabled(duocBat);
        } else {
            vao.setEnabled(duocBat);
        }
    }

    protected boolean coTheChinhSua() {
        return true;
    }

    protected boolean coTheSuaBanGhi(Object[] dong) {
        return true;
    }

    private void datCheDo(CheDo moHinhDangXet) {
        this.cheDo = moHinhDangXet;
        boolean dangSua = moHinhDangXet != CheDo.HIEN_THI && coTheChinhSua();
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
            boolean duocPhep = dangSua && !truongDangXet.docChi;
            if (duocPhep && truongDangXet.coTheSuaKhi != null && moHinhDangXet == CheDo.SUA)
                duocPhep = truongDangXet.coTheSuaKhi.test(String.valueOf(hienTai[chiSo]));
            datDauVaoDuocBat(chiSo, duocPhep);
        }
        btnNutLuu.setEnabled(dangSua);
        btnNutHuy.setEnabled(dangSua);
        boolean co =
                hienTai != null
                        && moHinhDangXet == CheDo.HIEN_THI
                        && coTheChinhSua()
                        && coTheSuaBanGhi(hienTai);
        btnNutSua.setEnabled(co);
        btnNutXoa.setEnabled(co && !kiemTraNgungHoatDong(hienTai));
        for (NutDinhDang btnGiaTriSau : boSungCacNut) btnGiaTriSau.setEnabled(co);
        nhanTieuDeBieuMau.setText(
                switch (moHinhDangXet) {
                    case MOI -> "Thêm " + cauHinh.tenThucThe + " mới";
                    case SUA -> "Sửa " + cauHinh.tenThucThe;
                    case HIEN_THI ->
                            hienTai == null
                                    ? "Chọn một dòng hoặc bấm + Thêm"
                                    : "Chi tiết " + cauHinh.tenThucThe;
                });
    }

    private void layKhiLuaChonDaThayDoi() {
        int hienThiDong = bangBang.getSelectedRow();
        Object[] dich =
                hienThiDong < 0
                        ? null
                        : daHienThi.get(bangBang.convertRowIndexToModel(hienThiDong));
        if (cheDo != CheDo.HIEN_THI && dich != hienTai && kiemTraChuaLuu()) {
            if (!HopThoai.xacNhan(this, "Bỏ các thay đổi chưa lưu?")) {
                dangChon = true;
                chonDong(hienTai);
                dangChon = false;
                return;
            }
        }
        hienThiBanGhi(dich);
    }

    private void hienThiBanGhi(Object[] bienLai) {
        hienTai = bienLai;
        xoaThongBao();
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++)
            datDauVao(chiSo, bienLai == null ? "" : String.valueOf(bienLai[chiSo]));
        datCheDo(CheDo.HIEN_THI);
    }

    private boolean kiemTraChuaLuu() {
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            String hienTai2 = layDauVao(chiSo);
            String truoc =
                    cheDo == CheDo.MOI ? layMacDinhCua(chiSo) : String.valueOf(hienTai[chiSo]);
            if (!hienTai2.equals(truoc)) return true;
        }
        return false;
    }

    private String layMacDinhCua(int chiSo) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        if (truongDangXet.kieu == Kieu.HOP_CHON) {
            String[] doiTuong = layCacLuaChonCua(truongDangXet);
            return truongDangXet.giaTriMacDinh.isEmpty()
                    ? (doiTuong.length > 0 ? doiTuong[0] : "")
                    : truongDangXet.giaTriMacDinh;
        }
        return truongDangXet.giaTriMacDinh;
    }

    protected final void ganHauToTheoTruong(
            int chiSoTruongNguon, int chiSoTruongDich, Function<String, String> chuyenDoiHauTo) {
        if (chiSoTruongNguon < 0
                || chiSoTruongNguon >= cacDauVao.length
                || chiSoTruongDich < 0
                || chiSoTruongDich >= cacDauVao.length) {
            return;
        }
        if (!(cacDauVao[chiSoTruongNguon] instanceof JComboBox<?> cboNguon)
                || !(cacDauVao[chiSoTruongDich] instanceof JTextField oDich)) {
            return;
        }
        JLabel nhanHauTo = new JLabel("");
        nhanHauTo.setName("hauTo:" + chiSoTruongDich);
        nhanHauTo.setOpaque(false);
        nhanHauTo.setBorder(null);
        nhanHauTo.setFont(oDich.getFont());
        Runnable datViTri =
                () -> {
                    FontMetrics fm = oDich.getFontMetrics(oDich.getFont());
                    Insets vao = oDich.getInsets();
                    int rongChu = fm.stringWidth(oDich.getText());
                    Dimension kt = nhanHauTo.getPreferredSize();
                    int x = vao.left + rongChu + 1;
                    int y = Math.max(0, (oDich.getHeight() - kt.height) / 2);
                    nhanHauTo.setBounds(x, y, kt.width, kt.height);
                    oDich.repaint();
                };
        oDich.setLayout(null);
        oDich.add(nhanHauTo);
        Runnable capNhatHauTo =
                () -> {
                    Object chon = cboNguon.getSelectedItem();
                    String hauTo = chuyenDoiHauTo.apply(chon == null ? "" : chon.toString());
                    nhanHauTo.setText(hauTo == null ? "" : hauTo);
                    nhanHauTo.setFont(oDich.getFont());
                    nhanHauTo.setForeground(
                            oDich.isEnabled() ? oDich.getForeground() : MauUngDung.VO_HIEU_HOA);
                    datViTri.run();
                };
        themBoLangNgheTaiLieu(oDich, () -> SwingUtilities.invokeLater(datViTri));
        oDich.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        datViTri.run();
                    }
                });
        oDich.addPropertyChangeListener("enabled", e -> capNhatHauTo.run());
        oDich.addPropertyChangeListener("foreground", e -> capNhatHauTo.run());
        cboNguon.addActionListener(e -> capNhatHauTo.run());
        capNhatHauTo.run();
    }

    private Runnable khiHuyThuGon;

    public final void moTaoMoi() {
        batDauTaoMoi();
    }

    public final void moSuaTheoKhoa(Object khoa) {
        if (khoa == null) return;
        String khoaStr = String.valueOf(khoa);
        for (Object[] d : cacDong) {
            if (d.length > 0 && khoaStr.equals(String.valueOf(d[0]))) {
                hienTai = d;
                hienThiBanGhi(d);
                chonDong(d);
                batDauSua();
                return;
            }
        }
    }

    public final void batCheDoThuGon(Runnable khiHuy) {
        this.khiHuyThuGon = khiHuy;
        btnNutSua.setVisible(false);
        btnNutXoa.setVisible(false);
        Component center = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        Component north = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (north != null) {
            remove(north);
        }
        if (center instanceof JSplitPane split) {
            Component right = split.getRightComponent();
            remove(split);
            if (right != null) {
                right.setPreferredSize(null);
                add(right, BorderLayout.CENTER);
            }
        }
        revalidate();
        repaint();
    }

    private void batDauTaoMoi() {
        if (!coTheChinhSua()) return;
        if (cheDo != CheDo.HIEN_THI
                && kiemTraChuaLuu()
                && !HopThoai.xacNhan(this, "Bỏ các thay đổi chưa lưu?")) return;
        dangChon = true;
        bangBang.clearSelection();
        dangChon = false;
        hienTai = null;
        xoaThongBao();
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            datDauVao(
                    chiSo,
                    chiSo == 0 && (cacTruong.get(chiSo).docChi || !cauHinh.maTienTo.isEmpty())
                            ? layMaTiepTheo()
                            : layMacDinhCua(chiSo));
        }
        datCheDo(CheDo.MOI);
        layTieuDiemDauTienCoTheSua();
    }

    private void batDauSua() {
        if (!coTheChinhSua() || hienTai != null && !coTheSuaBanGhi(hienTai)) return;
        if (hienTai == null) {
            HopThoai.hienThiThongTin(this, "Chọn một dòng để sửa.");
            return;
        }
        if (kiemTraNgungHoatDong(hienTai)) {
            hienThiBanGhi(hienTai);
        }
        xoaThongBao();
        datCheDo(CheDo.SUA);
        layTieuDiemDauTienCoTheSua();
    }

    private void huySua() {
        if (khiHuyThuGon != null) {
            khiHuyThuGon.run();
            return;
        }
        hienThiBanGhi(hienTai);
        chonDong(hienTai);
    }

    private void layTieuDiemDauTienCoTheSua() {
        SwingUtilities.invokeLater(
                () -> {
                    for (int chiSo = 0; chiSo < cacDauVao.length; chiSo++) {
                        if (cacDauVao[chiSo].isEnabled()
                                && cacTruong.get(chiSo).kieu != Kieu.TUY_CHINH
                                && cacTruong.get(chiSo).kieu != Kieu.TEP) {
                            if (cacDauVao[chiSo].getClientProperty("text")
                                    instanceof JTextField oNhapLieu) {
                                oNhapLieu.requestFocusInWindow();
                                oNhapLieu.selectAll();
                                return;
                            }
                            cacDauVao[chiSo].requestFocusInWindow();
                            if (cacDauVao[chiSo] instanceof JTextField oGiaTriTam)
                                oGiaTriTam.selectAll();
                            return;
                        }
                    }
                });
    }

    private void luu() {
        Object[] cacGiaTri = thuThapGiaTriBieuMau();
        if (cacGiaTri == null) return;
        boolean laVoucher =
                cacDong instanceof DanhMucService.CacDong dmVoucher
                        && dmVoucher.layLoai() == LoaiDanhMuc.VOUCHER;
        List<String> dsKhachVoucher =
                laVoucher && cacGiaTri.length > 7
                        ? DanhMucService.tachDanhSachKhach(cacGiaTri[7])
                        : List.of();
        dieuChinhMaVoucherKhachDuyNhat(laVoucher, dsKhachVoucher, cacGiaTri);
        boolean phatHanhNhieuKhach = laVoucher && dsKhachVoucher.size() > 1;
        if (!kiemTraToanBoBieuMau(cacGiaTri, phatHanhNhieuKhach)) return;
        if (cauHinh.boChuanHoa != null) cauHinh.boChuanHoa.accept(cacGiaTri);
        Object[] truoc = cheDo == CheDo.SUA ? hienTai.clone() : null;
        if (phatHanhNhieuKhach) {
            luuVoucherNhieuKhach(cacGiaTri, truoc, dsKhachVoucher);
            return;
        }
        if (laNhaCungCapKhongDoi(truoc, cacGiaTri)) {
            HopThoai.hienThiThongTin(this, "Không có dữ liệu mới.");
            return;
        }
        luuMotBanGhi(cacGiaTri, truoc);
    }

    private Object[] thuThapGiaTriBieuMau() {
        Object[] cacGiaTri = new Object[cacTruong.size()];
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            TruongDuLieu truong = cacTruong.get(chiSo);
            String giaTri;
            try {
                giaTri = layDauVao(chiSo);
            } catch (RuntimeException e) {
                HopThoai.hienThiCanhBao(this, "Dữ liệu không hợp lệ:\n" + e.getMessage());
                return null;
            }
            if (chiSo == 0 && truong.docChi)
                giaTri =
                        cheDo == CheDo.MOI
                                ? String.valueOf(layDauVao(0))
                                : String.valueOf(hienTai[0]);
            cacGiaTri[chiSo] =
                    truong.docChi && chiSo != 0
                            ? (cheDo == CheDo.MOI ? layMacDinhCua(chiSo) : hienTai[chiSo])
                            : giaTri;
        }
        return cacGiaTri;
    }

    private void dieuChinhMaVoucherKhachDuyNhat(
            boolean laVoucher, List<String> dsKhachVoucher, Object[] cacGiaTri) {
        if (!laVoucher || dsKhachVoucher.size() != 1) return;
        String khachDuyNhat = dsKhachVoucher.get(0);
        cacGiaTri[7] = khachDuyNhat;
        String maNhap = String.valueOf(cacGiaTri[0]).trim();
        if (cheDo != CheDo.MOI || maNhap.isEmpty()) return;
        for (Object[] d : cacDong) {
            if (String.valueOf(d[0]).equalsIgnoreCase(maNhap)
                    && !String.valueOf(d[7]).equalsIgnoreCase(khachDuyNhat)) {
                cacGiaTri[0] =
                        DanhMucService.taoMaVoucherTheoKhach(
                                maNhap, DanhMucService.layMaTuLuaChon(khachDuyNhat));
                break;
            }
        }
    }

    private boolean kiemTraToanBoBieuMau(Object[] cacGiaTri, boolean phatHanhNhieuKhach) {
        List<String> cacLoi = new ArrayList<>();
        int dauTienKhongHopLe = -1;
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            if (phatHanhNhieuKhach && chiSo == 0 && !String.valueOf(cacGiaTri[0]).isBlank())
                continue;
            String loi =
                    kiemTraTruong(chiSo, cacTruong.get(chiSo), String.valueOf(cacGiaTri[chiSo]));
            if (loi != null) {
                cacLoi.add(loi);
                if (dauTienKhongHopLe < 0) dauTienKhongHopLe = chiSo;
            }
        }
        if (cacLoi.isEmpty() && cauHinh.boSungBoKiemTra != null) {
            String loi = cauHinh.boSungBoKiemTra.apply(cacGiaTri);
            if (loi != null) cacLoi.add(loi);
        }
        if (cacLoi.isEmpty()) return true;
        HopThoai.hienThiCanhBao(this, "Chưa thể lưu:\n• " + String.join("\n• ", cacLoi));
        if (dauTienKhongHopLe >= 0) {
            JComponent dauVaoLoi = cacDauVao[dauTienKhongHopLe];
            if (dauVaoLoi.getClientProperty("text") instanceof JTextField oNhapLieu)
                oNhapLieu.requestFocusInWindow();
            else dauVaoLoi.requestFocusInWindow();
        }
        return false;
    }

    private void luuVoucherNhieuKhach(
            Object[] cacGiaTri, Object[] truoc, List<String> dsKhachVoucher) {
        String maGoc = String.valueOf(cacGiaTri[0]).trim();
        Object[] dongCapNhat = null;
        String khachChinh = null;
        if (cheDo == CheDo.SUA && hienTai != null) {
            String khachCu = String.valueOf(hienTai[7]);
            khachChinh = dsKhachVoucher.contains(khachCu) ? khachCu : dsKhachVoucher.get(0);
            dongCapNhat = cacGiaTri.clone();
            dongCapNhat[7] = khachChinh;
            if (cauHinh.boChuanHoa != null) cauHinh.boChuanHoa.accept(dongCapNhat);
            String loiMa = kiemTraTruong(0, cacTruong.get(0), String.valueOf(dongCapNhat[0]));
            if (loiMa != null) {
                HopThoai.hienThiCanhBao(this, "Chưa thể lưu:\n• " + loiMa);
                return;
            }
        }
        List<Object[]> cacDongMoi =
                taoDanhSachVoucherMoi(cacGiaTri, maGoc, khachChinh, dsKhachVoucher);
        if (cacDongMoi == null) return;
        try {
            DanhMucService.macDinh().luuNhieu(cacDong, truoc, dongCapNhat, cacDongMoi);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, "Không thể lưu: " + ngoaiLe.getMessage());
            return;
        }
        Object[] daLuuCuoi = null;
        if (dongCapNhat != null && hienTai != null) {
            System.arraycopy(dongCapNhat, 0, hienTai, 0, dongCapNhat.length);
            daLuuCuoi = hienTai;
            if (cauHinh.khiDaLuu != null) cauHinh.khiDaLuu.accept(truoc, hienTai);
        }
        for (Object[] dongMoi : cacDongMoi) {
            cacDong.add(dongMoi);
            daLuuCuoi = dongMoi;
            if (cauHinh.khiDaLuu != null) cauHinh.khiDaLuu.accept(null, dongMoi);
        }
        lamMoiLuaChonLoc();
        taiLai(daLuuCuoi);
        nhay("Đã lưu " + dsKhachVoucher.size() + " " + cauHinh.tenThucThe + ".");
    }

    private List<Object[]> taoDanhSachVoucherMoi(
            Object[] cacGiaTri, String maGoc, String khachChinh, List<String> dsKhachVoucher) {
        List<Object[]> cacDongMoi = new ArrayList<>();
        for (String khach : dsKhachVoucher) {
            if (khachChinh != null && khach.equals(khachChinh)) continue;
            Object[] dongMoi = cacGiaTri.clone();
            String maMoi =
                    DanhMucService.taoMaVoucherTheoKhach(
                            maGoc, DanhMucService.layMaTuLuaChon(khach));
            dongMoi[0] = maMoi;
            dongMoi[7] = khach;
            if (cauHinh.boChuanHoa != null) cauHinh.boChuanHoa.accept(dongMoi);
            for (Object[] d : cacDong) {
                if (String.valueOf(d[0]).equalsIgnoreCase(maMoi)) {
                    HopThoai.hienThiCanhBao(
                            this,
                            "Chưa thể lưu:\n• Mã voucher \""
                                    + maMoi
                                    + "\" đã tồn tại ("
                                    + d[7]
                                    + ").");
                    return null;
                }
            }
            cacDongMoi.add(dongMoi);
        }
        return cacDongMoi;
    }

    private boolean laNhaCungCapKhongDoi(Object[] truoc, Object[] cacGiaTri) {
        boolean laNcc =
                cacDong instanceof DanhMucService.CacDong dm
                        && dm.layLoai() == LoaiDanhMuc.NHA_CUNG_CAP;
        if (!laNcc || truoc == null) return false;
        String[] cu =
                Arrays.stream(truoc)
                        .map(v -> Objects.toString(v, "").trim())
                        .toArray(String[]::new);
        String[] moi =
                Arrays.stream(cacGiaTri)
                        .map(v -> Objects.toString(v, "").trim())
                        .toArray(String[]::new);
        cu[3] = cu[3].replaceAll("[\\s.]", "");
        moi[3] = moi[3].replaceAll("[\\s.]", "");
        return Arrays.equals(cu, moi);
    }

    private void luuMotBanGhi(Object[] cacGiaTri, Object[] truoc) {
        try {
            try {
                DanhMucService.macDinh().luu(cacDong, truoc, cacGiaTri);
            } catch (DanhMucService.TrungSoDienThoaiException ex) {
                if (!HopThoai.xacNhan(
                        this,
                        ex.getMessage() + "\nNếu đây là chi nhánh khác, bạn có muốn vẫn lưu?"))
                    return;
                DanhMucService.macDinh().luu(cacDong, truoc, cacGiaTri, true);
            }
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, "Không thể lưu: " + ngoaiLe.getMessage());
            return;
        }
        Object[] daLuu;
        if (cheDo == CheDo.MOI) {
            cacDong.add(cacGiaTri);
            daLuu = cacGiaTri;
        } else {
            System.arraycopy(cacGiaTri, 0, hienTai, 0, cacGiaTri.length);
            daLuu = hienTai;
        }
        if (cauHinh.khiDaLuu != null) cauHinh.khiDaLuu.accept(truoc, daLuu);
        lamMoiLuaChonLoc();
        taiLai(daLuu);
        nhay("Đã lưu " + cauHinh.tenThucThe + " " + daLuu[0] + ".");
    }

    private String kiemTraTruong(int chiSo, TruongDuLieu truongDangXet, String giaTri) {
        String ten = truongDangXet.nhan;
        if (truongDangXet.batBuoc && giaTri.isBlank()) return ten + " không được để trống.";
        if (giaTri.isBlank()) return null;
        switch (truongDangXet.kieu) {
            case SO_DIEN_THOAI -> {
                if (!giaTri.replaceAll("[\\s.]", "").matches("0\\d{9}"))
                    return ten + " phải gồm 10 chữ số, bắt đầu bằng 0.";
            }
            case EMAIL -> {
                if (!giaTri.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))
                    return ten + " không đúng định dạng (vd: ten@email.com).";
            }
            case CCCD -> {
                if (!giaTri.matches("\\d{12}")) return ten + " phải gồm đúng 12 chữ số.";
            }
            case NGAY -> {
                try {
                    LocalDate.parse(giaTri, DinhDang.NGAY_CHUAN);
                } catch (DateTimeParseException ngoaiLe) {
                    return ten + " phải theo dạng dd/MM/yyyy.";
                }
            }
            case TIEN_TE -> {
                if (giaTri.equals("?")) return ten + " không phải số tiền hợp lệ.";
            }
            case SO_NGUYEN -> {
                if (!giaTri.matches("\\d{1,9}")) return ten + " phải là số nguyên không âm.";
            }
            default -> {}
        }
        if (truongDangXet.duyNhat) {
            String khoa = giaTri.replaceAll("[\\s.]", "").toLowerCase();
            for (Object[] dongDuLieu : cacDong) {
                if (dongDuLieu == hienTai && cheDo == CheDo.SUA) continue;
                if (String.valueOf(dongDuLieu[chiSo])
                        .replaceAll("[\\s.]", "")
                        .toLowerCase()
                        .equals(khoa)) {
                    return ten + " \"" + giaTri + "\" đã tồn tại (" + dongDuLieu[0] + ").";
                }
            }
        }
        return null;
    }

    private void layNhatXoa() {
        if (hienTai == null) return;
        if (cauHinh.chiSoTrangThai < 0) {
            HopThoai.hienThiThongTin(this, "Danh mục này không hỗ trợ ngừng hoạt động.");
            return;
        }
        if (cauHinh.xoaDieuKienBaoVe != null) {
            String khoi = cauHinh.xoaDieuKienBaoVe.apply(hienTai);
            if (khoi != null) {
                HopThoai.hienThiCanhBao(this, khoi);
                return;
            }
        }
        if (!HopThoai.xacNhanNguyHiem(
                this,
                "Chuyển "
                        + cauHinh.tenThucThe
                        + " "
                        + hienTai[0]
                        + " - "
                        + hienTai[1]
                        + " sang \""
                        + cauHinh.nhanNgungHoatDong
                        + "\"?\nDữ liệu và lịch sử giao dịch vẫn được giữ (xóa mềm).",
                cauHinh.nhanNgungHoatDong)) return;
        Object[] truoc = hienTai.clone();
        Object[] daCapNhat = hienTai.clone();
        daCapNhat[cauHinh.chiSoTrangThai] = cauHinh.nhanNgungHoatDong;
        try {
            DanhMucService.macDinh().luu(cacDong, hienTai.clone(), daCapNhat);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
            return;
        }
        hienTai[cauHinh.chiSoTrangThai] = cauHinh.nhanNgungHoatDong;
        NhatKyHeThongService.macDinh()
                .ghiNhatKy(
                        "Xóa",
                        cauHinh.tenThucThe
                                + " "
                                + hienTai[0]
                                + " - "
                                + hienTai[1]
                                + " ("
                                + cauHinh.nhanNgungHoatDong
                                + ")");
        if (cauHinh.khiDaLuu != null) cauHinh.khiDaLuu.accept(truoc, hienTai);
        taiLai(hienTai);
        nhay(
                cauHinh.tenThucThe
                        + " "
                        + hienTai[0]
                        + " đã chuyển sang "
                        + cauHinh.nhanNgungHoatDong
                        + ".");
    }

    private void chayBoSung(ThaoTacBoSung ngoaiLe) {
        if (hienTai == null) return;
        Object[] truoc = hienTai.clone();
        String ketQua;
        try {
            ketQua = ngoaiLe.boXuLy().apply(hienTai);
            if (!Arrays.equals(truoc, hienTai))
                DanhMucService.macDinh().luu(cacDong, truoc, hienTai);
        } catch (RuntimeException ngoaiLe2) {
            System.arraycopy(truoc, 0, hienTai, 0, truoc.length);
            HopThoai.hienThiCanhBao(this, ngoaiLe2.getMessage());
            return;
        }
        taiLai(hienTai);
        if (ketQua != null) nhay(ketQua);
    }

    private boolean kiemTraNgungHoatDong(Object[] dongDuLieu) {
        return cauHinh.chiSoTrangThai >= 0
                && cauHinh.nhanNgungHoatDong.equals(
                        String.valueOf(dongDuLieu[cauHinh.chiSoTrangThai]));
    }

    private void chuanHoa(Object[] dongDuLieu) {
        if (cauHinh.boChuanHoa != null) cauHinh.boChuanHoa.accept(dongDuLieu);
    }

    private String layMaTiepTheo() {
        long lonNhat = 0;
        for (Object[] dongDuLieu : cacDong) {
            String ma = String.valueOf(dongDuLieu[0]).trim();
            int idxGach = ma.indexOf('-');
            if (idxGach > 0) {
                ma = ma.substring(0, idxGach);
            }
            String cacChuSo = ma.replaceAll("\\D", "");
            if (!cacChuSo.isEmpty()) {
                try {
                    long val = Long.parseLong(cacChuSo);
                    lonNhat = Math.max(lonNhat, val);
                } catch (NumberFormatException ex) {
                    LoggerFactory.getLogger(QuanLyDanhMucPanel.class)
                            .debug("Bỏ qua phần số vượt giới hạn trong mã danh mục '{}'.", ma, ex);
                }
            }
        }
        int soChuSo =
                switch (cauHinh.maTienTo) {
                    case "CA" -> 2;
                    case "P" -> 3;
                    case "NV", "NCC", "TREOSPA", "TREODOI" -> 4;
                    case "SP", "KH", "TC", "VOUCHER", "HDHT", "KM", "TREO" -> 6;
                    case "SPA",
                                    "LO",
                                    "PN",
                                    "PNK",
                                    "PX",
                                    "PXK",
                                    "LH",
                                    "KK",
                                    "DT",
                                    "NK",
                                    "YC",
                                    "MPI",
                                    "LOG" ->
                            8;
                    default -> 6;
                };
        long tiep = lonNhat + 1;
        String ungVien = cauHinh.maTienTo + String.format("%0" + soChuSo + "d", tiep);
        while (daTonTaiMa(ungVien)) {
            tiep++;
            ungVien = cauHinh.maTienTo + String.format("%0" + soChuSo + "d", tiep);
        }
        return ungVien;
    }

    private boolean daTonTaiMa(String ma) {
        for (Object[] dongDuLieu : cacDong) {
            if (ma.equalsIgnoreCase(String.valueOf(dongDuLieu[0]).trim())) {
                return true;
            }
        }
        return false;
    }

    private void taiLai(Object[] giu) {
        for (Object[] dongDuLieu : cacDong) chuanHoa(dongDuLieu);
        dangChon = true;
        daHienThi.clear();
        moHinh.setRowCount(0);
        for (Object[] dongDuLieu : cacDong) {
            if (!khopBoLoc(dongDuLieu)) continue;
            daHienThi.add(dongDuLieu);
            Object[] dong = new Object[danhSachCacTruong.size()];
            for (int khoaDangXet = 0; khoaDangXet < dong.length; khoaDangXet++) {
                int chiSo = danhSachCacTruong.get(khoaDangXet);
                TruongDuLieu truongDangXet = cacTruong.get(chiSo);
                String giaTri = String.valueOf(dongDuLieu[chiSo]);
                dong[khoaDangXet] =
                        truongDangXet.hienThi != null
                                ? truongDangXet.hienThi.apply(giaTri)
                                : truongDangXet.kieu == Kieu.TIEN_TE && !giaTri.isBlank()
                                        ? DinhDang.dinhDang(DinhDang.doc(giaTri))
                                        : giaTri;
            }
            moHinh.addRow(dong);
        }
        DinhDangBang.canChinhDoRongCot(bangBang);
        for (int colIdx = 0; colIdx < danhSachCacTruong.size(); colIdx++) {
            int truongIdx = danhSachCacTruong.get(colIdx);
            TruongDuLieu truong = cacTruong.get(truongIdx);
            if (colIdx < bangBang.getColumnModel().getColumnCount()) {
                TableColumn col = bangBang.getColumnModel().getColumn(colIdx);
                if (truong.doRongToiThieu > 0) col.setMinWidth(truong.doRongToiThieu);
                if (truong.doRongToiDa > 0) col.setMaxWidth(truong.doRongToiDa);
                if (truong.doRongUuTien > 0) col.setPreferredWidth(truong.doRongUuTien);
            }
        }
        dangChon = false;
        capNhatSoKetQua();
        chonDong(giu);
        if (giu == null
                || !daHienThi.contains(giu)
                || bangBang.convertRowIndexToView(daHienThi.indexOf(giu)) < 0) hienThiBanGhi(null);
        else hienThiBanGhi(giu);
    }

    private void chonDong(Object[] bienLai) {
        dangChon = true;
        try {
            int chiSo = bienLai == null ? -1 : daHienThi.indexOf(bienLai);
            if (chiSo < 0) bangBang.clearSelection();
            else {
                int hienThi = bangBang.convertRowIndexToView(chiSo);
                if (hienThi >= 0) bangBang.setRowSelectionInterval(hienThi, hienThi);
                else bangBang.clearSelection();
            }
        } finally {
            dangChon = false;
        }
    }

    private void nhay(String vanBan) {
        nhanThongBao.setForeground(MauUngDung.XANH_LA);
        nhanThongBao.setText(vanBan);
        Timer giaTriTam =
                new Timer(
                        3500,
                        suKien -> {
                            if (nhanThongBao.getText().equals(vanBan)) xoaThongBao();
                        });
        giaTriTam.setRepeats(false);
        giaTriTam.start();
    }

    private void xoaThongBao() {
        nhanThongBao.setText(" ");
    }

    private ThanhDanhSach thanhDuLieu;

    private int phienNap;
    private final Map<AbstractButton, Boolean> nutDangTai = new IdentityHashMap<>();

    private void napDuLieuNen(DanhMucService.CacDong danhMuc) {
        int token = ++phienNap;
        var session = PhienDangNhapService.macDinh().layPhienHienTai();
        for (Component c : thanhDuLieu.getComponents())
            if (c instanceof AbstractButton button) {
                nutDangTai.putIfAbsent(button, button.isEnabled());
                button.setEnabled(false);
            }
        for (AbstractButton b : List.of(btnNutSua, btnNutXoa, btnNutHuy, btnNutLuu)) {
            nutDangTai.putIfAbsent(b, b.isEnabled());
            b.setEnabled(false);
        }
        for (NutDinhDang b : boSungCacNut) {
            nutDangTai.putIfAbsent(b, b.isEnabled());
            b.setEnabled(false);
        }
        nhanSoKetQua.setText("· Đang tải…");
        TacVuNen.chayCoPhien(
                session,
                () -> token == phienNap,
                danhMuc::taiBanSao,
                () -> {
                    nutDangTai.forEach(AbstractButton::setEnabled);
                    nutDangTai.clear();
                },
                rows -> {
                    if (cheDo != CheDo.HIEN_THI) return;
                    danhMuc.clear();
                    danhMuc.addAll(rows);
                    lamMoiLuaChonLoc();
                    taiLai(null);
                    NguCanhMoBanGhi.apDung(QuanLyDanhMucPanel.this);
                },
                ex -> {
                    nhanSoKetQua.setText("· Không tải được dữ liệu");
                    HopThoai.hienThiCanhBao(QuanLyDanhMucPanel.this, TacVuNen.thongDiepLoi(ex));
                });
    }

    @Override
    public void xuLyHienThi() {
        if (cheDo != CheDo.HIEN_THI) return;
        if (cacDong instanceof DanhMucService.CacDong danhMuc) {
            if (SwingUtilities.isEventDispatchThread() && DieuKienTaiDuLieu.coTheTachLuong()) {
                napDuLieuNen(danhMuc);
                return;
            }
            danhMuc.lamMoi();
            lamMoiLuaChonLoc();
        }
        taiLai(null);
    }

    @Override
    protected String layTieuDeTroGiup() {
        return cauHinh.tieuDe;
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Danh mục chỉ thêm, sửa, xóa (xóa mềm), lưu và tìm kiếm.\n"
                + "Chọn dòng để xem, bấm đúp hoặc \"Sửa\" để chỉnh; ô * là bắt buộc.\n"
                + "Bản ghi đã phát sinh giao dịch không xóa thật, chỉ chuyển sang \""
                + cauHinh.nhanNgungHoatDong
                + "\".";
    }

    private void capNhatSoKetQua() {
        nhanSoKetQua.setText(ThanhDanhSach.soKetQua(bangBang.getRowCount(), cacDong.size()));
    }

    private static void ganTooltipKhiBiChe(JComboBox<String> cbo) {
        Runnable capNhat =
                () -> {
                    Object chon = cbo.getSelectedItem();
                    String vanBan = chon == null ? "" : chon.toString();
                    if (vanBan.isEmpty()) {
                        cbo.setToolTipText(null);
                        return;
                    }
                    int rongKhaDung =
                            (cbo.getWidth() > 0 ? cbo.getWidth() : cbo.getPreferredSize().width)
                                    - 28;
                    int rongChu = cbo.getFontMetrics(cbo.getFont()).stringWidth(vanBan);
                    cbo.setToolTipText(rongChu > rongKhaDung ? vanBan : null);
                };
        cbo.addActionListener(e -> capNhat.run());
        cbo.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        capNhat.run();
                    }
                });
        capNhat.run();
    }

    private void taoThanhBoLoc(ThanhDanhSach thanh) {
        for (BoLocDanhMuc bo : cauHinh.cacBoLoc) {
            JComboBox<String> chon = new JComboBox<>();
            chon.setName("loc:" + bo.nhan());
            chon.addItem("Tất cả");
            cacDong.stream()
                    .map(bo.giaTri())
                    .filter(v -> v != null && !v.isBlank())
                    .distinct()
                    .sorted()
                    .forEach(chon::addItem);
            ganTooltipKhiBiChe(chon);
            cboCacBoLoc.add(chon);
            thanh.themBoLoc(bo.nhan() + ":", chon);
            chon.addActionListener(
                    e -> {
                        if (!dangNapBoLoc) taiLai(hienTai);
                    });
        }
        if (cauHinh.chiSoTrangThai >= 0) {
            cboTrangThai.setName("loc:Trạng thái");
            cboTrangThai.addItem(layNhanTrangThaiMacDinh());
            cboTrangThai.addItem("Tất cả");
            cacDong.stream()
                    .map(d -> String.valueOf(d[cauHinh.chiSoTrangThai]))
                    .distinct()
                    .sorted()
                    .forEach(cboTrangThai::addItem);
            ganTooltipKhiBiChe(cboTrangThai);
            thanh.themBoLoc(layNhanBoLocTrangThai() + ":", cboTrangThai);
            cboTrangThai.addActionListener(
                    e -> {
                        if (!dangNapBoLoc) taiLai(hienTai);
                    });
        }
        NutDinhDang xoa = new NutDinhDang("Xóa lọc", NutDinhDang.KieuNut.TRONG_SUOT);
        xoa.addActionListener(
                e -> {
                    dangNapBoLoc = true;
                    try {
                        for (var c : cboCacBoLoc) c.setSelectedIndex(0);
                        if (cboTrangThai.getItemCount() > 0) cboTrangThai.setSelectedIndex(0);
                        oTimKiemChung.setText("");
                    } finally {
                        dangNapBoLoc = false;
                    }
                    taiLai(hienTai);
                });
        thanh.themBoLoc(xoa);
    }

    private boolean khopBoLoc(Object[] d) {
        if (cauHinh.chiSoTrangThai >= 0) {
            String tt = String.valueOf(cboTrangThai.getSelectedItem());
            if (tt.equals(layNhanTrangThaiMacDinh())
                    && (cauHinh.nhanHoatDong.equals("Trống")
                            ? kiemTraNgungHoatDong(d)
                            : !cauHinh.nhanHoatDong.equals(
                                    String.valueOf(d[cauHinh.chiSoTrangThai])))) return false;
            if (!tt.equals("Tất cả")
                    && !tt.equals(layNhanTrangThaiMacDinh())
                    && !tt.equals(String.valueOf(d[cauHinh.chiSoTrangThai]))) return false;
        }
        for (int i = 0; i < cboCacBoLoc.size(); i++) {
            String chon = String.valueOf(cboCacBoLoc.get(i).getSelectedItem());
            if (!chon.equals("Tất cả") && !chon.equals(cauHinh.cacBoLoc.get(i).giaTri().apply(d)))
                return false;
        }
        return true;
    }

    private boolean dangNapBoLoc;

    private void lamMoiLuaChonLoc() {
        if (dangNapBoLoc) return;
        dangNapBoLoc = true;
        try {
            if (cauHinh.chiSoTrangThai >= 0 && cboTrangThai.getItemCount() > 0) {
                Object daChon = cboTrangThai.getSelectedItem();
                List<String> moi = new ArrayList<>();
                moi.add(layNhanTrangThaiMacDinh());
                moi.add("Tất cả");
                cacDong.stream()
                        .map(d -> String.valueOf(d[cauHinh.chiSoTrangThai]))
                        .filter(v -> !moi.contains(v))
                        .distinct()
                        .sorted()
                        .forEach(moi::add);
                List<String> cu = new ArrayList<>();
                for (int j = 0; j < cboTrangThai.getItemCount(); j++)
                    cu.add(cboTrangThai.getItemAt(j));
                if (!cu.equals(moi)) {
                    cboTrangThai.setModel(new DefaultComboBoxModel<>(moi.toArray(String[]::new)));
                    cboTrangThai.setSelectedItem(moi.contains(daChon) ? daChon : moi.get(0));
                }
            }
            for (int i = 0; i < cboCacBoLoc.size(); i++) {
                var cbo = cboCacBoLoc.get(i);
                Object daChon = cbo.getSelectedItem();
                List<String> moi = new ArrayList<>();
                moi.add("Tất cả");
                cacDong.stream()
                        .map(cauHinh.cacBoLoc.get(i).giaTri())
                        .filter(v -> v != null && !v.isBlank())
                        .distinct()
                        .sorted()
                        .forEach(moi::add);
                List<String> cu = new ArrayList<>();
                for (int j = 0; j < cbo.getItemCount(); j++) cu.add(cbo.getItemAt(j));
                if (!moi.equals(cu)) {
                    cbo.setModel(new DefaultComboBoxModel<>(moi.toArray(String[]::new)));
                    cbo.setSelectedItem(moi.contains(daChon) ? daChon : "Tất cả");
                }
            }
        } finally {
            dangNapBoLoc = false;
        }
    }

    private String layNhanTrangThaiMacDinh() {
        return cauHinh.nhanHoatDong.equals("Trống") ? "Đang khai thác" : cauHinh.nhanHoatDong;
    }

    private String layNhanBoLocTrangThai() {
        return "Trạng thái";
    }
}
