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
import javax.swing.JCheckBox;
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

        /**
         * Khởi tạo TruongDuLieu với các giá trị được cung cấp.
         *
         * @param nhan nhãn
         * @param kieu kiểu
         */
        private TruongDuLieu(String nhan, Kieu kieu) {
            this.nhan = nhan;
            this.kieu = kieu;
        }

        /**
         * Tạo trong TruongDuLieu.
         *
         * @param nhan nhãn
         * @param kieu kiểu
         * @return kết quả tạo
         */
        public static TruongDuLieu tao(String nhan, Kieu kieu) {
            return new TruongDuLieu(nhan, kieu);
        }

        /**
         * Chuyển thành chuỗi trong TruongDuLieu.
         *
         * @param nhan nhãn
         * @return kết quả chuyển thành chuỗi
         */
        public static TruongDuLieu chuyenThanhChuoi(String nhan) {
            return new TruongDuLieu(nhan, Kieu.VAN_BAN);
        }

        /**
         * Bắt buộc trong TruongDuLieu.
         *
         * @return kết quả bắt buộc
         */
        public TruongDuLieu batBuoc() {
            this.batBuoc = true;
            return this;
        }

        /**
         * Kiểm tra duy nhất trong TruongDuLieu.
         *
         * @return kết quả kiểm tra duy nhất
         */
        public TruongDuLieu kiemTraDuyNhat() {
            this.duyNhat = true;
            return this;
        }

        /**
         * Chỉ đọc trong TruongDuLieu.
         *
         * @return kết quả chỉ đọc
         */
        public TruongDuLieu chiDoc() {
            this.docChi = true;
            return this;
        }

        /**
         * Ẩn trong TruongDuLieu.
         *
         * @return kết quả ẩn
         */
        public TruongDuLieu an() {
            this.vaoDanhSach = false;
            return this;
        }

        /**
         * Đặt lựa chọn trong TruongDuLieu.
         *
         * @param doiTuong đối tượng
         * @return kết quả đặt lựa chọn
         */
        public TruongDuLieu datLuaChon(String... doiTuong) {
            this.cacLuaChon = doiTuong;
            return this;
        }

        /**
         * Lấy động trong TruongDuLieu.
         *
         * @param chuoi chuỗi
         * @return kết quả lấy động
         */
        public TruongDuLieu layDong(Supplier<List<String>> chuoi) {
            this.dongCacLuaChon = chuoi;
            return this;
        }

        /**
         * Đặt mặc định trong TruongDuLieu.
         *
         * @param giaTri giá trị
         * @return kết quả đặt mặc định
         */
        public TruongDuLieu datMacDinh(String giaTri) {
            this.giaTriMacDinh = giaTri;
            return this;
        }

        /**
         * Đặt bộ soạn thảo trong TruongDuLieu.
         *
         * @param truongDangXet trường đang xét
         * @return kết quả đặt bộ soạn thảo
         */
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

        /**
         * Hiển thị trong TruongDuLieu.
         *
         * @param truongDangXet trường đang xét
         * @return kết quả hiển thị
         */
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

        /** Tên xuất. */
        String tenXuat = "Danh_muc";

        /**
         * Tạo trong CauHinh.
         *
         * @param tieuDe tiêu đề
         * @param tenThucThe tên thực thể
         * @return kết quả tạo
         */
        public static CauHinh tao(String tieuDe, String tenThucThe) {
            // Giá trị đang xét.
            CauHinh giaTriDangXet = new CauHinh();
            giaTriDangXet.tieuDe = tieuDe;
            giaTriDangXet.tenThucThe = tenThucThe;
            giaTriDangXet.tenXuat = tieuDe.replace(' ', '_');
            return giaTriDangXet;
        }

        /**
         * Tìm kiếm trong CauHinh.
         *
         * @param goiY gợi ý
         * @return kết quả tìm kiếm
         */
        public CauHinh timKiem(String goiY) {
            this.timKiemGoiY = goiY;
            return this;
        }

        /**
         * Đặt tiền tố trong CauHinh.
         *
         * @param vungDangXet vùng đang xét
         * @return kết quả đặt tiền tố
         */
        public CauHinh datTienTo(String vungDangXet) {
            this.maTienTo = vungDangXet;
            return this;
        }

        /**
         * Đặt trạng thái trong CauHinh.
         *
         * @param chiSo chỉ số
         * @param hoatDong hoạt động
         * @param ngungHoatDong ngừng hoạt động
         * @return kết quả đặt trạng thái
         */
        public CauHinh datTrangThai(int chiSo, String hoatDong, String ngungHoatDong) {
            this.chiSoTrangThai = chiSo;
            this.nhanHoatDong = hoatDong;
            this.nhanNgungHoatDong = ngungHoatDong;
            return this;
        }

        /**
         * Đặt bộ chuẩn hóa trong CauHinh.
         *
         * @param soPhanTu số phần tử
         * @return kết quả đặt bộ chuẩn hóa
         */
        public CauHinh datBoChuanHoa(Consumer<Object[]> soPhanTu) {
            this.boChuanHoa = soPhanTu;
            return this;
        }

        /**
         * Xử lý sau lưu trong CauHinh.
         *
         * @param truongDangXet trường đang xét
         * @return kết quả xử lý sau lưu
         */
        public CauHinh xuLySauLuu(BiConsumer<Object[], Object[]> truongDangXet) {
            this.khiDaLuu = truongDangXet;
            return this;
        }

        /**
         * Đặt điều kiện xóa trong CauHinh.
         *
         * @param doHoa đồ họa
         * @return kết quả đặt điều kiện xóa
         */
        public CauHinh datDieuKienXoa(Function<Object[], String> doHoa) {
            this.xoaDieuKienBaoVe = doHoa;
            return this;
        }

        /**
         * Đặt bộ kiểm tra trong CauHinh.
         *
         * @param giaTri giá trị
         * @return kết quả đặt bộ kiểm tra
         */
        public CauHinh datBoKiemTra(Function<Object[], String> giaTri) {
            this.boSungBoKiemTra = giaTri;
            return this;
        }

        /**
         * Thêm thao tác bổ sung trong CauHinh.
         *
         * @param nhan nhãn
         * @param boXuLy bộ xử lý
         * @return kết quả thêm thao tác bổ sung
         */
        public CauHinh themThaoTacBoSung(String nhan, Function<Object[], String> boXuLy) {
            cacThaoTacBoSung.add(new ThaoTacBoSung(nhan, boXuLy));
            return this;
        }
    }

    /** Cung cấp giao diện và xử lý tương tác cho chế độ. */
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

    /** Hiển thị ngừng hoạt động. */
    private final JCheckBox btnHienThiNgungHoatDong =
            new JCheckBox("Hiện cả bản ghi ngừng hoạt động");

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

    /**
     * Khởi tạo QuanLyDanhMucPanel với các giá trị được cung cấp.
     *
     * @param cauHinh cấu hình
     * @param cacTruong các trường
     * @param cacDong các dòng
     */
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
        // Thêm.
        NutDinhDang btnThem = new NutDinhDang("+ Thêm", NutDinhDang.KieuNut.CHINH);
        btnThem.addActionListener(suKien -> batDauTaoMoi());
        btnNutSua.addActionListener(suKien -> batDauSua());
        btnNutXoa.addActionListener(suKien -> layNhatXoa());
        // Xuất.
        NutDinhDang btnXuat = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuat.addActionListener(suKien -> XuatExcel.xuatBang(this, tblBang, cauHinh.tenXuat));

        // Các thao tác.
        JPanel pnlCacThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlCacThaoTac.setOpaque(false);
        pnlCacThaoTac.add(txtTimKiem);
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
        pnlTieuDe.add(taoTieuDeMuc(cauHinh.tieuDe), BorderLayout.WEST);
        pnlTieuDe.add(pnlCacThaoTac, BorderLayout.EAST);
        btnHienThiNgungHoatDong.setOpaque(false);
        btnHienThiNgungHoatDong.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        btnHienThiNgungHoatDong.addActionListener(suKien -> taiLai(hienTai));
        // Trên.
        JPanel pnlTren = taoVungTrongSuot(new BorderLayout(0, 4));
        pnlTren.add(pnlTieuDe, BorderLayout.NORTH);
        pnlTren.add(btnHienThiNgungHoatDong, BorderLayout.SOUTH);
        add(pnlTren, BorderLayout.NORTH);

        // ── Danh sách trái, form phải
        cacDauVao = new JComponent[cacTruong.size()];
        cacBoSoanThao = new BoSoanThaoTruong[cacTruong.size()];
        // Tách.
        JSplitPane tach = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrCuon, taoBieuMau());
        tach.setResizeWeight(0.62);
        tach.setBorder(null);
        tach.setOpaque(false);
        tach.setContinuousLayout(true);
        add(tach, BorderLayout.CENTER);

        tblBang.getSelectionModel()
                .addListSelectionListener(
                        suKien -> {
                            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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
                        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                        if (suKien.getClickCount() == 2
                                && hienTai != null
                                && cheDo == CheDo.HIEN_THI) batDauSua();
                    }
                });
        taiLai(null);
        datCheDo(CheDo.HIEN_THI);
    }

    // ───────────────────────── Form
    /**
     * Tạo biểu mẫu trong QuanLyDanhMucPanel.
     *
     * @return kết quả tạo biểu mẫu
     */
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
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            // Trường đang xét.
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
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
        scrKhungCuon.setPreferredSize(new Dimension(360, 0));
        // Vùng chứa.
        JPanel pnlVungChua = new JPanel(new BorderLayout());
        pnlVungChua.add(scrKhungCuon);
        return pnlVungChua;
    }

    /**
     * Tạo đầu vào trong QuanLyDanhMucPanel.
     *
     * @param chiSo chỉ số
     * @param truongDangXet trường đang xét
     * @return kết quả tạo đầu vào
     */
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
                // Vùng đang xét.
                JPanel pnlVungDangXet = new JPanel(new BorderLayout(6, 0));
                pnlVungDangXet.setOpaque(false);
                // Giá trị tạm.
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
                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
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

    /**
     * Lấy văn bản đầu vào trong QuanLyDanhMucPanel.
     *
     * @param truongDangXet trường đang xét
     * @return kết quả lấy văn bản đầu vào
     */
    private JTextField layVanBanDauVao(TruongDuLieu truongDangXet) {
        // Giá trị tạm.
        JTextField txtGiaTriTam = new JTextField();
        txtGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtGiaTriTam.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN),
                        new EmptyBorder(5, 8, 5, 8)));
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (truongDangXet.kieu == Kieu.NGAY) txtGiaTriTam.setToolTipText("Định dạng dd/MM/yyyy");
        return txtGiaTriTam;
    }

    /**
     * Lấy các lựa chọn của trong QuanLyDanhMucPanel.
     *
     * @param truongDangXet trường đang xét
     * @return kết quả lấy các lựa chọn của
     */
    private String[] layCacLuaChonCua(TruongDuLieu truongDangXet) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (truongDangXet.dongCacLuaChon != null)
            return truongDangXet.dongCacLuaChon.get().toArray(String[]::new);
        return truongDangXet.cacLuaChon;
    }

    /**
     * Lấy đầu vào trong QuanLyDanhMucPanel.
     *
     * @param chiSo chỉ số
     * @return kết quả lấy đầu vào
     */
    private String layDauVao(int chiSo) {
        // Trường đang xét.
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

    /**
     * Đặt đầu vào trong QuanLyDanhMucPanel.
     *
     * @param chiSo chỉ số
     * @param giaTri giá trị
     */
    @SuppressWarnings("unchecked")
    private void datDauVao(int chiSo, String giaTri) {
        // Trường đang xét.
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        // Vào.
        JComponent vao = cacDauVao[chiSo];
        // Giá trị.
        String giaTri2 = giaTri == null ? "" : giaTri;
        switch (truongDangXet.kieu) {
            case HOP_CHON -> {
                // Hộp.
                JComboBox<String> cboHop = (JComboBox<String>) vao;
                cboHop.setModel(
                        new javax.swing.DefaultComboBoxModel<>(layCacLuaChonCua(truongDangXet)));
                cboHop.setSelectedItem(giaTri2);
                // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                if (cboHop.getSelectedItem() == null || !cboHop.getSelectedItem().equals(giaTri2)) {
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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

    /**
     * Đặt đầu vào được bật trong QuanLyDanhMucPanel.
     *
     * @param chiSo chỉ số
     * @param duocBat được bật
     */
    private void datDauVaoDuocBat(int chiSo, boolean duocBat) {
        // Trường đang xét.
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        // Vào.
        JComponent vao = cacDauVao[chiSo];
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
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
    /**
     * Đặt chế độ trong QuanLyDanhMucPanel.
     *
     * @param moHinhDangXet mô hình đang xét
     */
    private void datCheDo(CheDo moHinhDangXet) {
        this.cheDo = moHinhDangXet;
        // Đang sửa.
        boolean dangSua = moHinhDangXet != CheDo.HIEN_THI;
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            // Trường đang xét.
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
            // Được phép.
            boolean duocPhep = dangSua && !truongDangXet.docChi;
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (duocPhep && truongDangXet.coTheSuaKhi != null && moHinhDangXet == CheDo.SUA)
                duocPhep = truongDangXet.coTheSuaKhi.test(String.valueOf(hienTai[chiSo]));
            datDauVaoDuocBat(chiSo, duocPhep);
        }
        btnNutLuu.setEnabled(dangSua);
        btnNutHuy.setEnabled(dangSua);
        // Có.
        boolean co = hienTai != null && moHinhDangXet == CheDo.HIEN_THI;
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

    /** Lấy khi lựa chọn đã thay đổi trong QuanLyDanhMucPanel. */
    private void layKhiLuaChonDaThayDoi() {
        // Hiển thị dòng.
        int hienThiDong = tblBang.getSelectedRow();
        // Đích.
        Object[] dich =
                hienThiDong < 0 ? null : daHienThi.get(tblBang.convertRowIndexToModel(hienThiDong));
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (cheDo != CheDo.HIEN_THI && dich != hienTai && kiemTraChuaLuu()) {
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (!HopThoai.xacNhan(this, "Bỏ các thay đổi chưa lưu?")) {
                dangChon = true;
                chonDong(hienTai);
                dangChon = false;
                return;
            }
        }
        hienThiBanGhi(dich);
    }

    /**
     * Hiển thị bản ghi trong QuanLyDanhMucPanel.
     *
     * @param bienLai biên lai
     */
    private void hienThiBanGhi(Object[] bienLai) {
        hienTai = bienLai;
        xoaThongBao();
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++)
            datDauVao(chiSo, bienLai == null ? "" : String.valueOf(bienLai[chiSo]));
        datCheDo(CheDo.HIEN_THI);
    }

    /**
     * Kiểm tra chưa lưu trong QuanLyDanhMucPanel.
     *
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private boolean kiemTraChuaLuu() {
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            // Hiện tại.
            String hienTai2 = layDauVao(chiSo);
            // Trước.
            String truoc =
                    cheDo == CheDo.MOI ? layMacDinhCua(chiSo) : String.valueOf(hienTai[chiSo]);
            // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
            if (!hienTai2.equals(truoc)) return true;
        }
        return false;
    }

    /**
     * Lấy mặc định của trong QuanLyDanhMucPanel.
     *
     * @param chiSo chỉ số
     * @return kết quả lấy mặc định của
     */
    private String layMacDinhCua(int chiSo) {
        // Trường đang xét.
        TruongDuLieu truongDangXet = cacTruong.get(chiSo);
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (truongDangXet.kieu == Kieu.HOP_CHON) {
            // Đối tượng.
            String[] doiTuong = layCacLuaChonCua(truongDangXet);
            return truongDangXet.giaTriMacDinh.isEmpty()
                    ? (doiTuong.length > 0 ? doiTuong[0] : "")
                    : truongDangXet.giaTriMacDinh;
        }
        return truongDangXet.giaTriMacDinh;
    }

    /** Bắt đầu tạo bản ghi mới trong QuanLyDanhMucPanel. */
    private void batDauTaoMoi() {
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
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

    /** Bắt đầu chỉnh sửa bản ghi trong QuanLyDanhMucPanel. */
    private void batDauSua() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hienTai == null) {
            HopThoai.hienThiThongTin(this, "Chọn một dòng để sửa.");
            return;
        }
        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
        if (kiemTraNgungHoatDong(hienTai)) {
            // Cho phép khôi phục bản ghi đã ngừng hoạt động
            hienThiBanGhi(hienTai);
        }
        xoaThongBao();
        datCheDo(CheDo.SUA);
        layTieuDiemDauTienCoTheSua();
    }

    /** Hủy sửa trong QuanLyDanhMucPanel. */
    private void huySua() {
        hienThiBanGhi(hienTai);
        chonDong(hienTai);
    }

    /** Lấy tiêu điểm đầu tiên có thể sửa trong QuanLyDanhMucPanel. */
    private void layTieuDiemDauTienCoTheSua() {
        SwingUtilities.invokeLater(
                () -> {
                    for (int chiSo = 0; chiSo < cacDauVao.length; chiSo++) {
                        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
                        if (cacDauVao[chiSo].isEnabled()
                                && cacTruong.get(chiSo).kieu != Kieu.TUY_CHINH
                                && cacTruong.get(chiSo).kieu != Kieu.TEP) {
                            cacDauVao[chiSo].requestFocusInWindow();
                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (cacDauVao[chiSo] instanceof JTextField txtGiaTriTam)
                                txtGiaTriTam.selectAll();
                            return;
                        }
                    }
                });
    }

    // ───────────────────────── Lưu / Xóa mềm
    /** Lưu trong QuanLyDanhMucPanel. */
    private void luu() {
        // Các giá trị.
        Object[] cacGiaTri = new Object[cacTruong.size()];
        // Các lỗi.
        List<String> cacLoi = new ArrayList<>();
        // Đầu tiên không hợp lệ.
        int dauTienKhongHopLe = -1;
        for (int chiSo = 0; chiSo < cacTruong.size(); chiSo++) {
            // Trường đang xét.
            TruongDuLieu truongDangXet = cacTruong.get(chiSo);
            // Giá trị.
            String giaTri = layDauVao(chiSo);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (chiSo == 0 && truongDangXet.docChi)
                giaTri =
                        cheDo == CheDo.MOI
                                ? String.valueOf(layDauVao(0))
                                : String.valueOf(hienTai[0]);
            cacGiaTri[chiSo] = giaTri;
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (truongDangXet.docChi && !(chiSo == 0))
                cacGiaTri[chiSo] = cheDo == CheDo.MOI ? layMacDinhCua(chiSo) : hienTai[chiSo];
            // Lỗi.
            String loi = kiemTraTruong(chiSo, truongDangXet, String.valueOf(cacGiaTri[chiSo]));
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (loi != null) {
                cacLoi.add(loi);
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (dauTienKhongHopLe < 0) dauTienKhongHopLe = chiSo;
            }
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cacLoi.isEmpty() && cauHinh.boSungBoKiemTra != null) {
            // Lỗi.
            String loi = cauHinh.boSungBoKiemTra.apply(cacGiaTri);
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (loi != null) cacLoi.add(loi);
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (!cacLoi.isEmpty()) {
            HopThoai.hienThiCanhBao(this, "Chưa thể lưu:\n• " + String.join("\n• ", cacLoi));
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (dauTienKhongHopLe >= 0) cacDauVao[dauTienKhongHopLe].requestFocusInWindow();
            return;
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (cheDo == CheDo.MOI) {
            cacDong.add(cacGiaTri);
            daLuu = cacGiaTri;
        } else {
            System.arraycopy(cacGiaTri, 0, hienTai, 0, cacGiaTri.length);
            daLuu = hienTai;
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cauHinh.khiDaLuu != null) cauHinh.khiDaLuu.accept(truoc, daLuu);
        taiLai(daLuu);
        nhay("Đã lưu " + cauHinh.tenThucThe + " " + daLuu[0] + ".");
    }

    /**
     * Kiểm tra trường trong QuanLyDanhMucPanel.
     *
     * @param chiSo chỉ số
     * @param truongDangXet trường đang xét
     * @param giaTri giá trị
     * @return kết quả kiểm tra trường
     */
    private String kiemTraTruong(int chiSo, TruongDuLieu truongDangXet, String giaTri) {
        // Tên.
        String ten = truongDangXet.nhan;
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (truongDangXet.batBuoc && giaTri.isBlank()) return ten + " không được để trống.";
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giaTri.isBlank()) return null;
        switch (truongDangXet.kieu) {
            case SO_DIEN_THOAI -> {
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (!giaTri.replaceAll("[\\s.]", "").matches("0\\d{9}"))
                    return ten + " phải gồm 10 chữ số, bắt đầu bằng 0.";
            }
            case EMAIL -> {
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (!giaTri.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))
                    return ten + " không đúng định dạng (vd: ten@email.com).";
            }
            case CCCD -> {
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (giaTri.equals("?")) return ten + " không phải số tiền hợp lệ.";
            }
            case SO_NGUYEN -> {
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
                if (!giaTri.matches("\\d{1,9}")) return ten + " phải là số nguyên không âm.";
            }
            default -> {}
        }
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (truongDangXet.duyNhat) {
            // Khóa.
            String khoa = giaTri.replaceAll("[\\s.]", "").toLowerCase();
            for (Object[] dongDuLieu : cacDong) {
                // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
                if (dongDuLieu == hienTai && cheDo == CheDo.SUA) continue;
                // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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

    /** Lấy nhạt xóa trong QuanLyDanhMucPanel. */
    private void layNhatXoa() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hienTai == null) return;
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (cauHinh.chiSoTrangThai < 0) {
            HopThoai.hienThiThongTin(this, "Danh mục này không hỗ trợ ngừng hoạt động.");
            return;
        }
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cauHinh.xoaDieuKienBaoVe != null) {
            // Khối.
            String khoi = cauHinh.xoaDieuKienBaoVe.apply(hienTai);
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (khoi != null) {
                HopThoai.hienThiCanhBao(this, khoi);
                return;
            }
        }
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
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
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
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

    /**
     * Chạy bổ sung trong QuanLyDanhMucPanel.
     *
     * @param ngoaiLe ngoại lệ
     */
    private void chayBoSung(ThaoTacBoSung ngoaiLe) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hienTai == null) return;
        // Trước.
        Object[] truoc = hienTai.clone();
        // Kết quả.
        String ketQua;
        try {
            ketQua = ngoaiLe.boXuLy().apply(hienTai);
            com.petstation.service.DanhMucService.luu(cacDong, truoc, hienTai);
        } catch (RuntimeException ngoaiLe2) {
            System.arraycopy(truoc, 0, hienTai, 0, truoc.length);
            HopThoai.hienThiCanhBao(this, ngoaiLe2.getMessage());
            return;
        }
        taiLai(hienTai);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (ketQua != null) nhay(ketQua);
    }

    /**
     * Kiểm tra ngừng hoạt động trong QuanLyDanhMucPanel.
     *
     * @param dongDuLieu dòng dữ liệu
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private boolean kiemTraNgungHoatDong(Object[] dongDuLieu) {
        return cauHinh.chiSoTrangThai >= 0
                && cauHinh.nhanNgungHoatDong.equals(
                        String.valueOf(dongDuLieu[cauHinh.chiSoTrangThai]));
    }

    /**
     * Chuẩn hóa trong QuanLyDanhMucPanel.
     *
     * @param dongDuLieu dòng dữ liệu
     */
    private void chuanHoa(Object[] dongDuLieu) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cauHinh.boChuanHoa != null) cauHinh.boChuanHoa.accept(dongDuLieu);
    }

    /**
     * Lấy mã tiếp theo trong QuanLyDanhMucPanel.
     *
     * @return kết quả lấy mã tiếp theo
     */
    private String layMaTiepTheo() {
        // Lớn nhất.
        int lonNhat = 0;
        for (Object[] dongDuLieu : cacDong) {
            // Các chữ số.
            String cacChuSo = String.valueOf(dongDuLieu[0]).replaceAll("\\D", "");
            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
            if (!cacChuSo.isEmpty()) lonNhat = Math.max(lonNhat, Integer.parseInt(cacChuSo));
        }
        return cauHinh.maTienTo + String.format("%03d", lonNhat + 1);
    }

    // ───────────────────────── Bảng
    /**
     * Tải lại trong QuanLyDanhMucPanel.
     *
     * @param giu giữ
     */
    private void taiLai(Object[] giu) {
        for (Object[] dongDuLieu : cacDong) chuanHoa(dongDuLieu);
        daHienThi.clear();
        moHinh.setRowCount(0);
        for (Object[] dongDuLieu : cacDong) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (!btnHienThiNgungHoatDong.isSelected() && kiemTraNgungHoatDong(dongDuLieu)) continue;
            daHienThi.add(dongDuLieu);
            // Dòng.
            Object[] dong = new Object[danhSachCacTruong.size()];
            for (int khoaDangXet = 0; khoaDangXet < dong.length; khoaDangXet++) {
                // Chỉ số.
                int chiSo = danhSachCacTruong.get(khoaDangXet);
                // Trường đang xét.
                TruongDuLieu truongDangXet = cacTruong.get(chiSo);
                // Giá trị.
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
        chonDong(giu);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giu != null && !daHienThi.contains(giu)) hienThiBanGhi(null);
        else // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (giu != null) hienThiBanGhi(giu);
    }

    /**
     * Chọn dòng trong QuanLyDanhMucPanel.
     *
     * @param bienLai biên lai
     */
    private void chonDong(Object[] bienLai) {
        dangChon = true;
        try {
            // Chỉ số.
            int chiSo = bienLai == null ? -1 : daHienThi.indexOf(bienLai);
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (chiSo < 0) tblBang.clearSelection();
            else {
                // Hiển thị.
                int hienThi = tblBang.convertRowIndexToView(chiSo);
                // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                if (hienThi >= 0) tblBang.setRowSelectionInterval(hienThi, hienThi);
            }
        } finally {
            dangChon = false;
        }
    }

    /**
     * Nháy trong QuanLyDanhMucPanel.
     *
     * @param vanBan văn bản
     */
    private void nhay(String vanBan) {
        lblThongBao.setForeground(MauUngDung.XANH_LA);
        lblThongBao.setText(vanBan);
        // Giá trị tạm.
        Timer giaTriTam =
                new Timer(
                        3500,
                        suKien -> {
                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                            if (lblThongBao.getText().equals(vanBan)) xoaThongBao();
                        });
        giaTriTam.setRepeats(false);
        giaTriTam.start();
    }

    /** Xóa thông báo trong QuanLyDanhMucPanel. */
    private void xoaThongBao() {
        lblThongBao.setText(" ");
    }

    /** Xử lý hiển thị trong QuanLyDanhMucPanel. */
    @Override
    public void xuLyHienThi() {
        // Danh sách chọn (ví dụ chủ nuôi) và trạng thái tự tính theo ngày có thể đã đổi
        // Giữ.
        Object[] giu = hienTai;
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (cheDo == CheDo.HIEN_THI) {
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (cacDong instanceof com.petstation.service.DanhMucService.CacDong danhMuc)
                danhMuc.lamMoi();
            taiLai(null);
        }
    }

    /**
     * Lấy tiêu đề trợ giúp trong QuanLyDanhMucPanel.
     *
     * @return kết quả lấy tiêu đề trợ giúp
     */
    @Override
    protected String layTieuDeTroGiup() {
        return cauHinh.tieuDe;
    }

    /**
     * Lấy nội dung trợ giúp trong QuanLyDanhMucPanel.
     *
     * @return kết quả lấy nội dung trợ giúp
     */
    @Override
    protected String layNoiDungTroGiup() {
        return "Danh mục chỉ thêm, sửa, xóa (xóa mềm), lưu và tìm kiếm.\n"
                + "Chọn dòng để xem, bấm đúp hoặc \"Sửa\" để chỉnh; ô * là bắt buộc.\n"
                + "Bản ghi đã phát sinh giao dịch không xóa thật, chỉ chuyển sang \""
                + cauHinh.nhanNgungHoatDong
                + "\".";
    }
}
