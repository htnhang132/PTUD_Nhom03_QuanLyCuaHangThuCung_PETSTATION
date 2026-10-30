package com.petstation.gui.components;

import com.petstation.gui.components.common.BoSoanThaoTruong;
import com.petstation.gui.components.common.XuatExcel;
import com.petstation.service.NhatKyHeThongService;
import com.petstation.util.TienTe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Cung cấp giao diện và xử lý tương tác cho thực thể vùng hiển thị.
 *
 * <p>Màn Danh mục chuẩn (chỉ CRUD): danh sách bên trái, form chi tiết bên phải. Nút Thêm, Sửa, Xóa
 * (xóa mềm), Lưu, Tìm kiếm. Kiểm tra bắt buộc, trùng, định dạng SĐT/email/ngày/tiền. Mọi thay đổi
 * ghi vào Nhật ký hệ thống. Dữ liệu là danh sách chia sẻ trong {@code DanhMucService}.
 */
public class QuanLyDanhMucPanel extends CoSoPanel implements HoTroThanhCongCu {
    /** Cung cấp giao diện và xử lý tương tác cho kiểu. */
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

    /**
     * Cung cấp giao diện và xử lý tương tác cho trường.
     *
     * <p>Mô tả một trường dữ liệu. Giá trị lưu dạng chuỗi (tiền lưu chuỗi số, ngày lưu dd/MM/yyyy).
     */
    public static final class TruongDuLieu {
        /** Nhãn. */
        final String nhan;

        /** Kiểu. */
        final Kieu kieu;

        /** Bắt buộc. */
        boolean batBuoc, duyNhat, docChi, vaoDanhSach = true;

        boolean trongBieuMau = true;

        public TruongDuLieu anBieuMau() {
            trongBieuMau = false;
            return this;
        }

        /** Các lựa chọn. */
        String[] cacLuaChon = new String[0];

        /** Động các lựa chọn. */
        Supplier<List<String>> dongCacLuaChon;

        /** Giá trị mặc định. */
        String giaTriMacDinh = "";

        /** Bộ soạn thảo bộ tạo. */
        Supplier<BoSoanThaoTruong> boSoanThaoBoTao;

        /** Có thể sửa khi. */
        Predicate<String> coTheSuaKhi;

        /** Hiển thị. */
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

        /**
         * Cho sửa khi trong TruongDuLieu.
         *
         * <p>Chỉ cho sửa tay khi giá trị hiện tại thỏa điều kiện (ví dụ phòng chỉ chỉnh tay khi
         * Trống/Bảo trì).
         *
         * @param vungDangXet vùng đang xét
         * @return kết quả cho sửa khi
         */
        public TruongDuLieu choSuaKhi(Predicate<String> vungDangXet) {
            this.coTheSuaKhi = vungDangXet;
            return this;
        }

        public TruongDuLieu hienThi(Function<String, String> truongDangXet) {
            this.hienThi = truongDangXet;
            return this;
        }
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho bổ sung.
     *
     * <p>Thao tác thêm ngoài CRUD (ví dụ Khóa/Mở khóa tài khoản). Trả về thông báo hiện ở thanh
     * trạng thái.
     *
     * @param nhan nhãn
     * @param boXuLy bộ xử lý
     */
    public record ThaoTacBoSung(String nhan, Function<Object[], String> boXuLy) {}

    /** Cung cấp giao diện và xử lý tương tác cho cấu hình. */
    public static final class CauHinh {
        /** Tiêu đề. */
        String tieuDe, timKiemGoiY = "Tìm kiếm...", tenThucThe = "bản ghi", maTienTo = "";

        /** Chỉ số trạng thái. */
        int chiSoTrangThai = -1;

        /** Trường ưu tiên ở đầu form, không thay đổi chỉ số dữ liệu hoặc cột bảng. */
        int chiSoTruongDau = -1;

        public CauHinh datTruongDau(int chiSo) {
            this.chiSoTruongDau = chiSo;
            return this;
        }

        /** Nhãn hoạt động. */
        String nhanHoatDong = "Đang hoạt động", nhanNgungHoatDong = "Ngừng hoạt động";

        /** Bộ chuẩn hóa. */
        Consumer<Object[]> boChuanHoa;

        /** Khi đã lưu. */
        BiConsumer<Object[], Object[]> khiDaLuu;

        /** Xóa điều kiện bảo vệ. */
        Function<Object[], String> xoaDieuKienBaoVe;

        /** Bổ sung bộ kiểm tra. */
        Function<Object[], String> boSungBoKiemTra;

        /** Các thao tác bổ sung. */
        final List<ThaoTacBoSung> cacThaoTacBoSung = new ArrayList<>();

        final List<BoLocDanhMuc> cacBoLoc = new ArrayList<>();

        public CauHinh themBoLoc(String nhan, Function<Object[], String> giaTri) {
            cacBoLoc.add(new BoLocDanhMuc(nhan, giaTri));
            return this;
        }

        /** Tên xuất. */
        String tenXuat = "Danh_muc";

        public static CauHinh tao(String tieuDe, String tenThucThe) {
            // Giá trị đang xét.
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

    /** Cung cấp giao diện và xử lý tương tác cho chế độ. */
    public record BoLocDanhMuc(String nhan, Function<Object[], String> giaTri) {}

    private final List<JComboBox<String>> cboCacBoLoc = new ArrayList<>();
    private final JComboBox<String> cboTrangThai = new JComboBox<>();
    private final JLabel lblSoKetQua = new JLabel();
    private JTextField txtTimKiemChung;

    private enum CheDo {
        HIEN_THI,
        SUA,
        MOI
    }

    /** Ngày. */
    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    /** Cấu hình. */
    private final CauHinh cauHinh;

    /** Các trường. */
    private final List<TruongDuLieu> cacTruong;

    /** Các dòng. */
    private final List<Object[]> cacDong;

    /** Đã hiển thị. */
    private final List<Object[]> daHienThi = new ArrayList<>();

    /** Mô hình. */
    private final DefaultTableModel moHinh;

    /** Bảng. */
    private final JTable tblBang;

    /** Các đầu vào. */
    private final JComponent[] cacDauVao;

    /** Các bộ soạn thảo. */
    private final BoSoanThaoTruong[] cacBoSoanThao;

    /** Tiêu đề biểu mẫu. */
    private final JLabel lblTieuDeBieuMau = new JLabel();

    /** Thông báo. */
    private final JLabel lblThongBao = new JLabel(" ");

    /** Nút sửa. */
    private final NutDinhDang btnNutSua = new NutDinhDang("Sửa", NutDinhDang.KieuNut.PHU);

    /** Nút xóa. */
    private final NutDinhDang btnNutXoa = new NutDinhDang("Xóa", NutDinhDang.KieuNut.NGUY_HIEM);

    /** Nút lưu. */
    private final NutDinhDang btnNutLuu = new NutDinhDang("Lưu", NutDinhDang.KieuNut.THANH_CONG);

    /** Nút hủy. */
    private final NutDinhDang btnNutHuy = new NutDinhDang("Hủy", NutDinhDang.KieuNut.TRONG_SUOT);

    /** Bổ sung các nút. */
    private final List<NutDinhDang> boSungCacNut = new ArrayList<>();

    /** Danh sách các trường. */
    private final List<Integer> danhSachCacTruong = new ArrayList<>();

    /** Chế độ. */
    private CheDo cheDo = CheDo.HIEN_THI;

    /** Hiện tại. */
    private Object[] hienTai;

    /** Đang chọn. */
    private boolean dangChon;

    public QuanLyDanhMucPanel(
            CauHinh cauHinh, List<TruongDuLieu> cacTruong, List<Object[]> cacDong) {
        this.cauHinh = cauHinh;
        this.cacTruong = cacTruong;
        this.cacDong = cacDong;
        setLayout(new BorderLayout(0, 12));
        for (int chiSo = 0;
                chiSo < cacTruong.size();
                chiSo++) // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (cacTruong.get(chiSo).vaoDanhSach) danhSachCacTruong.add(chiSo);
        for (Object[] dongDuLieu : cacDong) chuanHoa(dongDuLieu);
        lamMoiLuaChonLoc();

        // Các cột.
        String[] cacCot =
                danhSachCacTruong.stream()
                        .map(chiSo -> cacTruong.get(chiSo).nhan)
                        .toArray(String[]::new);
        moHinh =
                new DefaultTableModel(cacCot, 0) {
                    /**
                     * Kiểm tra ô có thể sửa trong lớp xử lý giao diện.
                     *
                     * @param dongDuLieu dòng dữ liệu
                     * @param giaTriDangXet giá trị đang xét
                     * @return kết quả kiểm tra điều kiện xử lý
                     */
                    @Override
                    public boolean isCellEditable(int dongDuLieu, int giaTriDangXet) {
                        return false;
                    }
                };
        // Cuộn.
        JScrollPane scrCuon = taoBangDinhDang(moHinh);
        tblBang = layBang(scrCuon);

        // ── Thanh trên: tiêu đề, tìm kiếm, thao tác
        // Tìm kiếm.
        JTextField txtTimKiem = taoOTimKiem(cauHinh.timKiemGoiY);
        lienKetBoLoc(txtTimKiem, tblBang);
        txtTimKiemChung = txtTimKiem;
        txtTimKiem.setName("timKiemDanhMuc");
        tblBang.getRowSorter().addRowSorterListener(e -> capNhatSoKetQua());
        // Thêm.
        NutDinhDang btnThem = new NutDinhDang("+ Thêm", NutDinhDang.KieuNut.CHINH);
        btnThem.setEnabled(coTheChinhSua());
        btnThem.addActionListener(suKien -> batDauTaoMoi());
        btnNutSua.addActionListener(suKien -> batDauSua());
        btnNutXoa.addActionListener(suKien -> layNhatXoa());
        // Xuất.
        NutDinhDang btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(suKien -> XuatExcel.xuatBang(this, tblBang, cauHinh.tenXuat));

        // Các thao tác.
        ThanhDanhSach pnlCacThaoTac = new ThanhDanhSach();
        thanhDuLieu = pnlCacThaoTac;
        pnlCacThaoTac.setOpaque(false);
        pnlCacThaoTac.add(txtTimKiem);
        taoThanhBoLoc(pnlCacThaoTac);
        pnlCacThaoTac.add(btnThem);
        pnlCacThaoTac.add(btnNutSua);
        pnlCacThaoTac.add(btnNutXoa);
        for (ThaoTacBoSung ngoaiLe : cauHinh.cacThaoTacBoSung) {
            // Giá trị sau.
            NutDinhDang btnGiaTriSau =
                    new NutDinhDang(ngoaiLe.nhan(), NutDinhDang.KieuNut.TRONG_SUOT);
            btnGiaTriSau.addActionListener(suKien -> chayBoSung(ngoaiLe));
            boSungCacNut.add(btnGiaTriSau);
            pnlCacThaoTac.add(btnGiaTriSau);
        }
        pnlCacThaoTac.add(btnXuat);

        // Tiêu đề.
        JPanel pnlTieuDe = new JPanel(new BorderLayout(12, 0));
        pnlTieuDe.setOpaque(false);
        pnlTieuDe.add(
                ThanhDanhSach.tieuDe(taoTieuDeMuc(cauHinh.tieuDe), lblSoKetQua),
                BorderLayout.NORTH);
        pnlTieuDe.add(pnlCacThaoTac, BorderLayout.CENTER);
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 8));
        pnlTren.add(pnlTieuDe, BorderLayout.NORTH);
        add(pnlTren, BorderLayout.NORTH);

        // ── Danh sách trái, form phải
        cacDauVao = new JComponent[cacTruong.size()];
        cacBoSoanThao = new BoSoanThaoTruong[cacTruong.size()];
        // Tách.
        JSplitPane tach = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrCuon, taoBieuMau());
        tach.setResizeWeight(1.0);
        tach.setBorder(null);
        tach.setOpaque(false);
        tach.setContinuousLayout(true);
        add(tach, BorderLayout.CENTER);

        tblBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> {
                            if (suKien.getValueIsAdjusting() || dangChon) return;
                            layKhiLuaChonDaThayDoi();
                        });
        tblBang.addMouseListener(
                new MouseAdapter() {
                    /**
                     * Lấy chuột đã bấm trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        if (suKien.getClickCount() == 2
                                && hienTai != null
                                && cheDo == CheDo.HIEN_THI) batDauSua();
                    }
                });
        taiLai(null);
        datCheDo(CheDo.HIEN_THI);
        if (cacDong.isEmpty()
                && cacDong instanceof com.petstation.service.DanhMucService.CacDong d
                && javax.swing.SwingUtilities.isEventDispatchThread()
                && com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()) napDuLieuNen(d);
    }

    // ───────────────────────── Form
    private JPanel taoBieuMau() {
        // Biểu mẫu.
        JPanel pnlBieuMau = new JPanel(new GridBagLayout());
        pnlBieuMau.setBackground(Color.WHITE);
        pnlBieuMau.setBorder(new EmptyBorder(14, 16, 14, 16));
        // Giá trị đang xét.
        GridBagConstraints giaTriDangXet = new GridBagConstraints();
        giaTriDangXet.gridx = 0;
        giaTriDangXet.weightx = 1;
        giaTriDangXet.fill = GridBagConstraints.HORIZONTAL;
        giaTriDangXet.insets = new Insets(2, 0, 2, 0);
        // Tọa độ dọc.
        int toaDoDoc = 0;
        lblTieuDeBieuMau.setFont(MauUngDung.taoPhongChu(Font.BOLD, 15));
        giaTriDangXet.gridy = toaDoDoc++;
        pnlBieuMau.add(lblTieuDeBieuMau, giaTriDangXet);
        List<Integer> thuTuBieuMau = new ArrayList<>();
        if (cauHinh.chiSoTruongDau >= 0 && cauHinh.chiSoTruongDau < cacTruong.size())
            thuTuBieuMau.add(cauHinh.chiSoTruongDau);
        for (int i = 0; i < cacTruong.size(); i++)
            if (i != cauHinh.chiSoTruongDau) thuTuBieuMau.add(i);
        for (int chiSo : thuTuBieuMau) {
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
            if (!truongDangXet.trongBieuMau) {
                cacDauVao[chiSo] = taoDauVao(chiSo, truongDangXet);
                continue;
            }
            // Nhãn.
            JLabel lblNhan = new JLabel(truongDangXet.nhan + (truongDangXet.batBuoc ? " *" : ""));
            lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            lblNhan.setForeground(MauUngDung.VAN_BAN_XAM);
            giaTriDangXet.gridy = toaDoDoc++;
            giaTriDangXet.insets = new Insets(8, 0, 1, 0);
            pnlBieuMau.add(lblNhan, giaTriDangXet);
            // Vào.
            JComponent vao = taoDauVao(chiSo, truongDangXet);
            cacDauVao[chiSo] = vao;
            giaTriDangXet.gridy = toaDoDoc++;
            giaTriDangXet.insets = new Insets(0, 0, 0, 0);
            pnlBieuMau.add(vao, giaTriDangXet);
        }
        lblThongBao.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.insets = new Insets(10, 0, 0, 0);
        pnlBieuMau.add(lblThongBao, giaTriDangXet);
        // Các nút.
        JPanel pnlCacNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlCacNut.setOpaque(false);
        btnNutHuy.addActionListener(suKien -> huySua());
        btnNutLuu.addActionListener(suKien -> luu());
        pnlCacNut.add(btnNutHuy);
        pnlCacNut.add(btnNutLuu);
        giaTriDangXet.gridy = toaDoDoc++;
        giaTriDangXet.insets = new Insets(6, 0, 0, 0);
        pnlBieuMau.add(pnlCacNut, giaTriDangXet);
        giaTriDangXet.gridy = toaDoDoc;
        giaTriDangXet.weighty = 1;
        giaTriDangXet.fill = GridBagConstraints.BOTH;
        pnlBieuMau.add(
                new JPanel() {
                    {
                        setOpaque(false);
                    }
                },
                giaTriDangXet);

        // Khung cuộn.
        JScrollPane scrKhungCuon = new JScrollPane(pnlBieuMau);
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        scrKhungCuon.getVerticalScrollBar().setUnitIncrement(16);
        scrKhungCuon.setPreferredSize(new Dimension(380, 0));
        scrKhungCuon.setMinimumSize(new Dimension(320, 0));
        scrKhungCuon.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));
        // Vùng chứa.
        JPanel pnlVungChua = new JPanel(new BorderLayout());
        pnlVungChua.setPreferredSize(new Dimension(380, 0));
        pnlVungChua.setMinimumSize(new Dimension(320, 0));
        pnlVungChua.setMaximumSize(new Dimension(420, Integer.MAX_VALUE));
        pnlVungChua.add(scrKhungCuon);
        return pnlVungChua;
    }

    private JComponent taoDauVao(int chiSo, TruongDuLieu truongDangXet) {
        switch (truongDangXet.kieu) {
            case MAT_KHAU -> {
                return new javax.swing.JPasswordField(20);
            }
            case HOP_CHON -> {
                // Hộp.
                JComboBox<String> cboHop = new JComboBox<>(layCacLuaChonCua(truongDangXet));
                cboHop.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
                return cboHop;
            }
            case VUNG -> {
                // Vùng.
                JTextArea txtVung = new JTextArea(3, 20);
                txtVung.setLineWrap(true);
                txtVung.setWrapStyleWord(true);
                txtVung.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
                // Chuỗi.
                JScrollPane scrChuoi = new JScrollPane(txtVung);
                scrChuoi.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
                return scrChuoi;
            }
            case TUY_CHINH -> {
                // Bộ soạn thảo.
                BoSoanThaoTruong boSoanThao = truongDangXet.boSoanThaoBoTao.get();
                cacBoSoanThao[chiSo] = boSoanThao;
                return boSoanThao.layThanhPhan();
            }
            case TEP -> {
                JPanel pnlVungDangXet = new JPanel(new BorderLayout(6, 0));
                pnlVungDangXet.setOpaque(false);
                JTextField txtGiaTriTam = layVanBanDauVao(truongDangXet);
                // Chọn.
                NutDinhDang btnChon =
                        new NutDinhDang("Chọn ảnh...", NutDinhDang.KieuNut.TRONG_SUOT);
                btnChon.addActionListener(
                        suKien -> {
                            // Ký tự.
                            JFileChooser kyTu = new JFileChooser();
                            kyTu.setFileFilter(
                                    new javax.swing.filechooser.FileNameExtensionFilter(
                                            "Ảnh (jpg, png)", "jpg", "jpeg", "png"));
                            if (kyTu.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                                txtGiaTriTam.setText(kyTu.getSelectedFile().getAbsolutePath());
                        });
                pnlVungDangXet.add(txtGiaTriTam, BorderLayout.CENTER);
                pnlVungDangXet.add(btnChon, BorderLayout.EAST);
                pnlVungDangXet.putClientProperty("text", txtGiaTriTam);
                return pnlVungDangXet;
            }
            default -> {
                return layVanBanDauVao(truongDangXet);
            }
        }
    }

    private JTextField layVanBanDauVao(TruongDuLieu truongDangXet) {
        JTextField txtGiaTriTam = new JTextField();
        txtGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtGiaTriTam.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(5, 8, 5, 8)));
        if (truongDangXet.kieu == Kieu.NGAY) txtGiaTriTam.setToolTipText("Định dạng dd/MM/yyyy");
        return txtGiaTriTam;
    }

    private String[] layCacLuaChonCua(TruongDuLieu truongDangXet) {
        if (truongDangXet.dongCacLuaChon != null)
            return truongDangXet.dongCacLuaChon.get().toArray(String[]::new);
        return truongDangXet.cacLuaChon;
    }

    private String layDauVao(int chiSo) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        // Vào.
        JComponent vao = cacDauVao[chiSo];
        return switch (truongDangXet.kieu) {
            case HOP_CHON -> {
                // Chuỗi.
                Object chuoi = ((JComboBox<?>) vao).getSelectedItem();
                yield chuoi == null ? "" : chuoi.toString();
            }
            case VUNG -> ((JTextArea) ((JScrollPane) vao).getViewport().getView()).getText().trim();
            case TUY_CHINH -> cacBoSoanThao[chiSo].lay();
            case TEP -> ((JTextField) vao.getClientProperty("text")).getText().trim();
            case TIEN_TE -> {
                // Mô hình đang xét.
                long moHinhDangXet = TienTe.doc(((JTextField) vao).getText());
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
        // Vào.
        JComponent vao = cacDauVao[chiSo];
        String giaTri2 = giaTri == null ? "" : giaTri;
        switch (truongDangXet.kieu) {
            case HOP_CHON -> {
                // Hộp.
                JComboBox<String> cboHop = (JComboBox<String>) vao;
                cboHop.setModel(
                        new javax.swing.DefaultComboBoxModel<>(layCacLuaChonCua(truongDangXet)));
                cboHop.setSelectedItem(giaTri2);
                if (cboHop.getSelectedItem() == null || !cboHop.getSelectedItem().equals(giaTri2)) {
                    if (!giaTri2.isEmpty()) {
                        cboHop.addItem(giaTri2);
                        cboHop.setSelectedItem(giaTri2);
                    } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (cboHop.getItemCount() > 0) cboHop.setSelectedIndex(0);
                }
            }
            case VUNG -> ((JTextArea) ((JScrollPane) vao).getViewport().getView()).setText(giaTri2);
            case TUY_CHINH -> cacBoSoanThao[chiSo].dat(giaTri2);
            case TEP -> ((JTextField) vao.getClientProperty("text")).setText(giaTri2);
            case TIEN_TE ->
                    ((JTextField) vao)
                            .setText(giaTri2.isEmpty() ? "" : TienTe.dinhDang(TienTe.doc(giaTri2)));
            default -> ((JTextField) vao).setText(giaTri2);
        }
    }

    private void datDauVaoDuocBat(int chiSo, boolean duocBat) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        // Vào.
        JComponent vao = cacDauVao[chiSo];
        if (truongDangXet.kieu == Kieu.TUY_CHINH) cacBoSoanThao[chiSo].datDuocBat(duocBat);
        else // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (truongDangXet.kieu == Kieu.VUNG)
            ((JTextArea) ((JScrollPane) vao).getViewport().getView()).setEditable(duocBat);
        else // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (truongDangXet.kieu == Kieu.TEP) {
            for (java.awt.Component khoaDangXet : ((JPanel) vao).getComponents())
                khoaDangXet.setEnabled(duocBat);
        } else vao.setEnabled(duocBat);
    }

    // ───────────────────────── Chế độ & chọn dòng
    protected boolean coTheChinhSua() {
        return true;
    }

    protected boolean coTheSuaBanGhi(Object[] dong) {
        return true;
    }

    private void datCheDo(CheDo moHinhDangXet) {
        this.cheDo = moHinhDangXet;
        // Đang sửa.
        boolean dangSua = moHinhDangXet != CheDo.HIEN_THI && coTheChinhSua();
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
            // Được phép.
            boolean duocPhep = dangSua && !truongDangXet.docChi;
            if (duocPhep && truongDangXet.coTheSuaKhi != null && moHinhDangXet == CheDo.SUA)
                duocPhep = truongDangXet.coTheSuaKhi.test(String.valueOf(hienTai[chiSo]));
            datDauVaoDuocBat(chiSo, duocPhep);
        }
        btnNutLuu.setEnabled(dangSua);
        btnNutHuy.setEnabled(dangSua);
        // Có.
        boolean co =
                hienTai != null
                        && moHinhDangXet == CheDo.HIEN_THI
                        && coTheChinhSua()
                        && coTheSuaBanGhi(hienTai);
        btnNutSua.setEnabled(co);
        btnNutXoa.setEnabled(co && !kiemTraNgungHoatDong(hienTai));
        for (NutDinhDang btnGiaTriSau : boSungCacNut) btnGiaTriSau.setEnabled(co);
        lblTieuDeBieuMau.setText(
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
        // Hiển thị dòng.
        int hienThiDong = tblBang.getSelectedRow();
        // Đích.
        Object[] dich =
                hienThiDong < 0 ? null : daHienThi.get(tblBang.convertRowIndexToModel(hienThiDong));
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
            // Hiện tại.
            String hienTai2 = layDauVao(chiSo);
            // Trước.
            String truoc =
                    cheDo == CheDo.MOI ? layMacDinhCua(chiSo) : String.valueOf(hienTai[chiSo]);
            if (!hienTai2.equals(truoc)) return true;
        }
        return false;
    }

    private String layMacDinhCua(int chiSo) {
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        if (truongDangXet.kieu == Kieu.HOP_CHON) {
            // Đối tượng.
            String[] doiTuong = layCacLuaChonCua(truongDangXet);
            return truongDangXet.giaTriMacDinh.isEmpty()
                    ? (doiTuong.length > 0 ? doiTuong[0] : "")
                    : truongDangXet.giaTriMacDinh;
        }
        return truongDangXet.giaTriMacDinh;
    }

    private void batDauTaoMoi() {
        if (!coTheChinhSua()) return;
        if (cheDo != CheDo.HIEN_THI
                && kiemTraChuaLuu()
                && !HopThoai.xacNhan(this, "Bỏ các thay đổi chưa lưu?")) return;
        dangChon = true;
        tblBang.clearSelection();
        dangChon = false;
        hienTai = null;
        xoaThongBao();
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            datDauVao(
                    chiSo,
                    chiSo == 0 && cacTruong.get(chiSo).docChi
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
            // Cho phép khôi phục bản ghi đã ngừng hoạt động
            hienThiBanGhi(hienTai);
        }
        xoaThongBao();
        datCheDo(CheDo.SUA);
        layTieuDiemDauTienCoTheSua();
    }

    private void huySua() {
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
                            cacDauVao[chiSo].requestFocusInWindow();
                            if (cacDauVao[chiSo] instanceof JTextField txtGiaTriTam)
                                txtGiaTriTam.selectAll();
                            return;
                        }
                    }
                });
    }

    // ───────────────────────── Lưu / Xóa mềm
    private void luu() {
        // Các giá trị.
        Object[] cacGiaTri = new Object[cacTruong.size()];
        // Các lỗi.
        List<String> cacLoi = new ArrayList<>();
        // Đầu tiên không hợp lệ.
        int dauTienKhongHopLe = -1;
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
            String giaTri;

            try {
                giaTri = layDauVao(chiSo);
            } catch (IllegalArgumentException e) {
                HopThoai.hienThiCanhBao(
                    this,
                    "Dữ liệu không hợp lệ:\n" + e.getMessage()
                );
                return;
            }
            if (chiSo == 0 && truongDangXet.docChi)
                giaTri =
                        cheDo == CheDo.MOI
                                ? String.valueOf(layDauVao(0))
                                : String.valueOf(hienTai[0]);
            cacGiaTri[chiSo] = giaTri;
            if (truongDangXet.docChi && !(chiSo == 0))
                cacGiaTri[chiSo] = cheDo == CheDo.MOI ? layMacDinhCua(chiSo) : hienTai[chiSo];
            // Lỗi.
            String loi = kiemTraTruong(chiSo, truongDangXet, String.valueOf(cacGiaTri[chiSo]));
            if (loi != null) {
                cacLoi.add(loi);
                if (dauTienKhongHopLe < 0) dauTienKhongHopLe = chiSo;
            }
        }
        if (cacLoi.isEmpty() && cauHinh.boSungBoKiemTra != null) {
            // Lỗi.
            String loi = cauHinh.boSungBoKiemTra.apply(cacGiaTri);
            if (loi != null) cacLoi.add(loi);
        }
        if (!cacLoi.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Chưa thể lưu:\n• " + String.join("\n• ", cacLoi));
            if (dauTienKhongHopLe >= 0) cacDauVao[dauTienKhongHopLe].requestFocusInWindow();
            return;
        }
        if (cauHinh.boChuanHoa != null) cauHinh.boChuanHoa.accept(cacGiaTri);
        // Trước.
        Object[] truoc = cheDo == CheDo.SUA ? hienTai.clone() : null;
        // Đã lưu.
        Object[] daLuu;
        try {
            com.petstation.service.DanhMucService.luu(cacDong, truoc, cacGiaTri);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, "Không thể lưu: " + ngoaiLe.getMessage());
            return;
        }
        if (cheDo == CheDo.MOI) {
            cacDong.add(cacGiaTri);
            daLuu = cacGiaTri;
        } else {
            System.arraycopy(cacGiaTri, 0, hienTai, 0, cacGiaTri.length);
            daLuu = hienTai;
        }
        if (cauHinh.khiDaLuu != null) cauHinh.khiDaLuu.accept(truoc, daLuu);
        taiLai(daLuu);
        nhay("Đã lưu " + cauHinh.tenThucThe + " " + daLuu[0] + ".");
    }

    private String kiemTraTruong(int chiSo, TruongDuLieu truongDangXet, String giaTri) {
        // Tên.
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
                    LocalDate.parse(giaTri, NGAY);
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
            // Khóa.
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
            // Khối.
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
        // Trước.
        Object[] truoc = hienTai.clone();
        // Đã cập nhật.
        Object[] daCapNhat = hienTai.clone();
        daCapNhat[cauHinh.chiSoTrangThai] = cauHinh.nhanNgungHoatDong;
        try {
            com.petstation.service.DanhMucService.luu(cacDong, hienTai.clone(), daCapNhat);
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(this, ngoaiLe.getMessage());
            return;
        }
        hienTai[cauHinh.chiSoTrangThai] = cauHinh.nhanNgungHoatDong;
        NhatKyHeThongService.ghiNhatKy(
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
        // Trước.
        Object[] truoc = hienTai.clone();
        String ketQua;
        try {
            ketQua = ngoaiLe.boXuLy().apply(hienTai);
            if (!java.util.Arrays.equals(truoc, hienTai))
                com.petstation.service.DanhMucService.luu(cacDong, truoc, hienTai);
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
            String cacChuSo = String.valueOf(dongDuLieu[0]).replaceAll("\\D", "");
            if (!cacChuSo.isEmpty()) {
                try {
                    lonNhat = Math.max(lonNhat, Long.parseLong(cacChuSo));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int soChuSo =
                switch (cauHinh.maTienTo) {
                    case "NV", "NCC" -> 4;
                    case "SP", "KH", "TC", "LH", "VOUCHER", "HDHT" -> 6;
                    case "SPA", "LO", "PN", "PX", "KK", "DT", "NK", "YC", "MPI" -> 8;
                    default -> 3;
                };
        return cauHinh.maTienTo + String.format("%0" + soChuSo + "d", lonNhat + 1);
    }

    // ───────────────────────── Bảng
    private void taiLai(Object[] giu) {
        for (Object[] dongDuLieu : cacDong) chuanHoa(dongDuLieu);
        dangChon = true;
        daHienThi.clear();
        moHinh.setRowCount(0);
        for (Object[] dongDuLieu : cacDong) {
            if (!khopBoLoc(dongDuLieu)) continue;
            daHienThi.add(dongDuLieu);
            // Dòng.
            Object[] dong = new Object[danhSachCacTruong.size()];
            for (int khoaDangXet = 0; khoaDangXet < dong.length; khoaDangXet++) {
                // Chỉ số.
                int chiSo = danhSachCacTruong.get(khoaDangXet);
                TruongDuLieu truongDangXet = cacTruong.get(chiSo);
                String giaTri = String.valueOf(dongDuLieu[chiSo]);
                dong[khoaDangXet] =
                        truongDangXet.hienThi != null
                                ? truongDangXet.hienThi.apply(giaTri)
                                : truongDangXet.kieu == Kieu.TIEN_TE && !giaTri.isBlank()
                                        ? TienTe.dinhDang(TienTe.doc(giaTri))
                                        : giaTri;
            }
            moHinh.addRow(dong);
        }
        dangChon = false;
        capNhatSoKetQua();
        chonDong(giu);
        if (giu != null
                && (!daHienThi.contains(giu)
                        || tblBang.convertRowIndexToView(daHienThi.indexOf(giu)) < 0))
            hienThiBanGhi(null);
        else // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giu != null) hienThiBanGhi(giu);
    }

    private void chonDong(Object[] bienLai) {
        dangChon = true;
        try {
            // Chỉ số.
            int chiSo = bienLai == null ? -1 : daHienThi.indexOf(bienLai);
            if (chiSo < 0) tblBang.clearSelection();
            else {
                // Hiển thị.
                int hienThi = tblBang.convertRowIndexToView(chiSo);
                if (hienThi >= 0) tblBang.setRowSelectionInterval(hienThi, hienThi);
                else tblBang.clearSelection();
            }
        } finally {
            dangChon = false;
        }
    }

    private void nhay(String vanBan) {
        lblThongBao.setForeground(MauUngDung.XANH_LA);
        lblThongBao.setText(vanBan);
        Timer giaTriTam =
                new Timer(
                        3500,
                        suKien -> {
                            if (lblThongBao.getText().equals(vanBan)) xoaThongBao();
                        });
        giaTriTam.setRepeats(false);
        giaTriTam.start();
    }

    private void xoaThongBao() {
        lblThongBao.setText(" ");
    }

    private ThanhDanhSach thanhDuLieu;

    private int phienNap;
    private final java.util.Map<javax.swing.AbstractButton, Boolean> nutDangTai =
            new java.util.IdentityHashMap<>();

    public boolean dangTaiDuLieu() {
        return !nutDangTai.isEmpty();
    }

    private void napDuLieuNen(com.petstation.service.DanhMucService.CacDong danhMuc) {
        int token = ++phienNap;
        var session = com.petstation.service.PhienDangNhapService.layPhienHienTai();
        for (java.awt.Component c : thanhDuLieu.getComponents())
            if (c instanceof javax.swing.AbstractButton button) {
                nutDangTai.putIfAbsent(button, button.isEnabled());
                button.setEnabled(false);
            }
        lblSoKetQua.setText("· Đang tải…");
        new javax.swing.SwingWorker<java.util.List<Object[]>, Void>() {
            protected java.util.List<Object[]> doInBackground() throws Exception {
                return com.petstation.service.PhienDangNhapService.trongPhien(
                        session, danhMuc::taiBanSao);
            }

            protected void done() {
                if (token != phienNap
                        || com.petstation.service.PhienDangNhapService.layPhienHienTai() != session)
                    return;
                nutDangTai.forEach(javax.swing.AbstractButton::setEnabled);
                nutDangTai.clear();
                try {
                    var rows = get();
                    if (cheDo != CheDo.HIEN_THI) return;
                    danhMuc.clear();
                    danhMuc.addAll(rows);
                    lamMoiLuaChonLoc();
                    taiLai(null);
                    com.petstation.gui.navigation.NguCanhMoBanGhi.apDung(QuanLyDanhMucPanel.this);
                } catch (Exception ex) {
                    lblSoKetQua.setText("· Không tải được dữ liệu");
                    HopThoai.hienThiCanhBao(
                            QuanLyDanhMucPanel.this,
                            ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage());
                }
            }
        }.execute();
    }

    @Override
    public void xuLyHienThi() {
        if (cheDo != CheDo.HIEN_THI) return;
        if (cacDong instanceof com.petstation.service.DanhMucService.CacDong danhMuc) {
            if (javax.swing.SwingUtilities.isEventDispatchThread()
                    && com.petstation.service.DieuKienTaiDuLieu.coTheTachLuong()) {
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

    /** Bộ lọc dùng chung, giá trị chọn lấy từ dữ liệu thật của từng danh mục. */
    private void capNhatSoKetQua() {
        lblSoKetQua.setText(ThanhDanhSach.soKetQua(tblBang.getRowCount(), cacDong.size()));
    }

    private void taoThanhBoLoc(ThanhDanhSach thanh) {
        for (BoLocDanhMuc bo : cauHinh.cacBoLoc) {
            JComboBox<String> chon = new JComboBox<>();
            chon.setName("loc:" + bo.nhan());
            chon.setPreferredSize(new Dimension(125, 36));
            chon.addItem("Tất cả");
            cacDong.stream()
                    .map(bo.giaTri())
                    .filter(v -> v != null && !v.isBlank())
                    .distinct()
                    .sorted()
                    .forEach(chon::addItem);
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
            thanh.themBoLoc(layNhanBoLocTrangThai() + ":", cboTrangThai);
            cboTrangThai.addActionListener(
                    e -> {
                        if (!dangNapBoLoc) taiLai(hienTai);
                    });
        }
        JButton xoa = new JButton("Xóa lọc");
        xoa.addActionListener(
                e -> {
                    dangNapBoLoc = true;
                    try {
                        for (var c : cboCacBoLoc) c.setSelectedIndex(0);
                        if (cboTrangThai.getItemCount() > 0) cboTrangThai.setSelectedIndex(0);
                        txtTimKiemChung.setText("");
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
                java.util.List<String> moi = new ArrayList<>();
                moi.add(layNhanTrangThaiMacDinh());
                moi.add("Tất cả");
                cacDong.stream()
                        .map(d -> String.valueOf(d[cauHinh.chiSoTrangThai]))
                        .filter(v -> !moi.contains(v))
                        .distinct()
                        .sorted()
                        .forEach(moi::add);
                java.util.List<String> cu = new ArrayList<>();
                for (int j = 0; j < cboTrangThai.getItemCount(); j++)
                    cu.add(cboTrangThai.getItemAt(j));
                if (!cu.equals(moi)) {
                    cboTrangThai.setModel(
                            new javax.swing.DefaultComboBoxModel<>(moi.toArray(String[]::new)));
                    cboTrangThai.setSelectedItem(moi.contains(daChon) ? daChon : moi.get(0));
                }
            }
            for (int i = 0; i < cboCacBoLoc.size(); i++) {
                var cbo = cboCacBoLoc.get(i);
                Object daChon = cbo.getSelectedItem();
                java.util.List<String> moi = new ArrayList<>();
                moi.add("Tất cả");
                cacDong.stream()
                        .map(cauHinh.cacBoLoc.get(i).giaTri())
                        .filter(v -> v != null && !v.isBlank())
                        .distinct()
                        .sorted()
                        .forEach(moi::add);
                java.util.List<String> cu = new ArrayList<>();
                for (int j = 0; j < cbo.getItemCount(); j++) cu.add(cbo.getItemAt(j));
                if (!moi.equals(cu)) {
                    cbo.setModel(
                            new javax.swing.DefaultComboBoxModel<>(moi.toArray(String[]::new)));
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
        return switch (cauHinh.nhanNgungHoatDong) {
            case "Nghỉ việc" -> "Tình trạng làm việc";
            case "Ngừng kinh doanh" -> "Trạng thái kinh doanh";
            case "Ngừng hợp tác" -> "Trạng thái hợp tác";
            case "Ngừng" -> "Trạng thái sử dụng";
            default -> "Trạng thái";
        };
    }
}
