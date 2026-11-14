package com.petstation.gui.hethong;

import com.petstation.dto.DongNhatKyHeThong;
import com.petstation.gui.chung.CoSoPanel;
import com.petstation.gui.chung.HopThoai;
import com.petstation.gui.chung.MauUngDung;
import com.petstation.gui.chung.NutDinhDang;
import com.petstation.gui.chung.TacVuNen;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BoChonKhoangNgay;
import com.petstation.gui.chung.thanhphan.XuatExcelDialog;
import com.petstation.service.chung.BoLangNgheSuKien;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

// Màn hình: Nhật ký thao tác hệ thống (Mã: NHAT_KY_HE_THONG)
// Luồng xử lý: GUI -> NhatKyHeThongService -> NhatKyHeThongDAOImpl -> SQL (nhat_ky_he_thong,
// tai_khoan)
// Mô tả: Truy vết toàn bộ nhật ký (Audit Log) các hành vi thao tác, sửa đổi dữ liệu và đăng nhập hệ
// thống
// Phân hệ: hethong
public final class NhatKyHeThongPanel extends CoSoPanel {
    private static final int GIOI_HAN_BAN_GHI = 2000;

    private static final String[] MA_LOAI_SU_KIEN = {
        "", "LOGIN", "PAYMENT", "INVENTORY", "SERVICE", "APPROVAL", "CATALOG", "SYSTEM"
    };

    private static final String[] TEN_LOAI_SU_KIEN = {
        "Tất cả",
        "Đăng nhập & Tài khoản",
        "Bán hàng & Thu ngân",
        "Kho hàng",
        "Spa & Lưu trú",
        "Phê duyệt",
        "Danh mục & Nhân sự",
        "Hệ thống"
    };

    private JTable bangBang;

    private DefaultTableModel moHinhBang;

    private BoChonKhoangNgay khungKhoangNgayBoChon;

    private JComboBox<String> cboSuKienKieuHopChon;

    private JComboBox<String> cboKetQuaHopChon;

    private JComboBox<String> cboNhanVienHopChon;

    private JTextField oTruongNguoiDungTimKiem;

    private JLabel nhanNhanCacChiSo;

    private int luotTai;

    private List<DongNhatKyHeThong> duLieuDaTai = new ArrayList<>();

    private List<DongNhatKyHeThong> duLieuDangHienThi = new ArrayList<>();

    private boolean dangDatLaiBoLoc;

    public NhatKyHeThongPanel() {
        setLayout(new BorderLayout(0, 12));
        add(taoThanhDau(), BorderLayout.NORTH);
        add(taoBangVungHienThi(), BorderLayout.CENTER);
        dangDatLaiBoLoc = true;
        try {
            khungKhoangNgayBoChon.chonKy("7 ngày qua");
        } finally {
            dangDatLaiBoLoc = false;
        }
        khungKhoangNgayBoChon.addPropertyChangeListener(
                "dateRange",
                e -> {
                    if (!dangDatLaiBoLoc) taiLai();
                });
        cboSuKienKieuHopChon.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocClient();
                });
        cboKetQuaHopChon.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocClient();
                });
        cboNhanVienHopChon.addActionListener(
                e -> {
                    if (!dangDatLaiBoLoc) apDungBoLocClient();
                });
        taiLai();
    }

    private final BoLangNgheSuKien<String> boLangNgheNhatKy =
            thaoTac ->
                    SwingUtilities.invokeLater(
                            () -> {
                                if (isShowing()
                                        && PhienDangNhapService.macDinh().layPhienHienTai() != null)
                                    taiLai();
                            });

    @Override
    public void addNotify() {
        super.addNotify();
        NhatKyHeThongService.macDinh().themBoLangNghe(boLangNgheNhatKy);
    }

    @Override
    public void removeNotify() {
        NhatKyHeThongService.macDinh().xoaBoLangNghe(boLangNgheNhatKy);
        super.removeNotify();
    }

    private JPanel taoThanhDau() {
        JPanel khungTren = taoVungTrongSuot(new BorderLayout());
        nhanNhanCacChiSo = new JLabel("· 0 sự kiện");
        nhanNhanCacChiSo.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        nhanNhanCacChiSo.setForeground(MauUngDung.VAN_BAN_XAM);
        khungTren.add(
                ThanhDanhSach.tieuDe(taoTieuDeManHinh("Nhật ký hệ thống"), nhanNhanCacChiSo),
                BorderLayout.NORTH);

        ThanhDanhSach thanhLoc = new ThanhDanhSach();
        taoOTimKiemNhatKy();
        taoBoLocLoaiSuKien();
        taoBoLocKetQua();
        taoBoLocNhanVien();
        khungKhoangNgayBoChon = new BoChonKhoangNgay();

        thanhLoc.add(oTruongNguoiDungTimKiem);
        thanhLoc.themBoLoc("Loại sự kiện:", cboSuKienKieuHopChon);
        thanhLoc.themBoLoc("Kết quả:", cboKetQuaHopChon);
        thanhLoc.themBoLoc("Nhân viên:", cboNhanVienHopChon);
        thanhLoc.add(khungKhoangNgayBoChon);

        NutDinhDang btnXuatNut = new NutDinhDang("Xuất Excel", NutDinhDang.KieuNut.TRONG_SUOT);
        btnXuatNut.setToolTipText("Xuất kết quả tìm kiếm ra file Excel (.xlsx)");
        btnXuatNut.putClientProperty("luonCuoi", Boolean.TRUE);
        btnXuatNut.addActionListener(suKien -> xuLyXuat());
        thanhLoc.add(btnXuatNut);

        khungTren.add(thanhLoc, BorderLayout.CENTER);
        return khungTren;
    }

    private void taoBoLocLoaiSuKien() {
        cboSuKienKieuHopChon = new JComboBox<>(TEN_LOAI_SU_KIEN);
    }

    private void taoBoLocKetQua() {
        String[] cacKetQua = {"Tất cả", "Thành công", "Thất bại", "Cảnh báo"};
        cboKetQuaHopChon = new JComboBox<>(cacKetQua);
    }

    private void taoBoLocNhanVien() {
        cboNhanVienHopChon = new JComboBox<>(new String[] {"Tất cả"});
        cboNhanVienHopChon.setPrototypeDisplayValue("Nguyễn Văn Hoàng Anh");
    }

    private void taoOTimKiemNhatKy() {
        oTruongNguoiDungTimKiem = taoOTimKiem("Tìm người dùng, hành động, mã...");
        oTruongNguoiDungTimKiem.setToolTipText(
                "Tìm theo nhân viên, hành động, đối tượng hoặc mã chứng từ");
        themBoLangNgheTaiLieu(oTruongNguoiDungTimKiem, this::apDungBoLocClient);
        oTruongNguoiDungTimKiem.addActionListener(
                e -> {
                    if (bangBang != null && bangBang.getRowCount() > 0) {
                        bangBang.setRowSelectionInterval(0, 0);
                        bangBang.requestFocusInWindow();
                    }
                });
        oTruongNguoiDungTimKiem.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE
                                && !oTruongNguoiDungTimKiem.getText().isEmpty()) {
                            oTruongNguoiDungTimKiem.setText("");
                        }
                    }
                });
    }

    private JScrollPane taoBangVungHienThi() {
        String[] cacCot = {"Thời gian", "Người dùng", "Hành động", "Loại sự kiện", "Kết quả"};
        moHinhBang =
                new DefaultTableModel(new Object[0][], cacCot) {
                    @Override
                    public boolean isCellEditable(int dong, int cot) {
                        return false;
                    }
                };
        JScrollPane scrCuonKhung = taoBangDinhDang(moHinhBang);
        bangBang = layBang(scrCuonKhung);
        bangBang.setRowHeight(38);
        bangBang.setFillsViewportHeight(true);
        bangBang.setToolTipText("Nhấp đúp vào dòng để xem chi tiết sự kiện");
        datBangChinh(bangBang);
        cauHinhCotHienThiNhatKy();
        bangBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent suKien) {
                        int dongClick = bangBang.rowAtPoint(suKien.getPoint());
                        if (SwingUtilities.isLeftMouseButton(suKien)
                                && suKien.getClickCount() == 2
                                && dongClick >= 0) {
                            bangBang.setRowSelectionInterval(dongClick, dongClick);
                            xuLyHienThiChiTiet(dongClick);
                        }
                    }
                });
        return scrCuonKhung;
    }

    private void cauHinhCotHienThiNhatKy() {
        bangBang.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        (b, giaTri, daChon, foc, dong, cot) -> {
                            JLabel nhanChu =
                                    taoNhanBangNhatKy(
                                            giaTri, SwingConstants.LEFT, Font.PLAIN, daChon, dong);
                            nhanChu.setBorder(new EmptyBorder(0, 8, 0, 16));
                            return nhanChu;
                        });
        bangBang.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        (b, giaTri, daChon, foc, dong, cot) -> {
                            JLabel nhanChu =
                                    taoNhanBangNhatKy(
                                            giaTri, SwingConstants.CENTER, Font.BOLD, daChon, dong);
                            if (!daChon) {
                                String s =
                                        (giaTri == null ? "" : giaTri.toString())
                                                .toUpperCase(Locale.ROOT);
                                if (s.contains("CẢNH BÁO")
                                        || s.contains("LỖI")
                                        || s.contains("XÓA")
                                        || s.contains("WARNING")
                                        || s.contains("ERROR")
                                        || s.contains("DELETE")) {
                                    nhanChu.setForeground(MauUngDung.VAN_BAN_NGUY_HIEM);
                                } else if (s.contains("ĐĂNG NHẬP")
                                        || s.contains("TÀI KHOẢN")
                                        || s.contains("LOGIN")
                                        || s.contains("LOGOUT")) {
                                    nhanChu.setForeground(MauUngDung.CHINH);
                                } else if (s.contains("PHÊ DUYỆT") || s.contains("APPROVAL")) {
                                    nhanChu.setForeground(MauUngDung.CAM_NHAN);
                                } else if (s.contains("BÁN HÀNG")
                                        || s.contains("THU NGÂN")
                                        || s.contains("THANH TOÁN")
                                        || s.contains("PAYMENT")) {
                                    nhanChu.setForeground(MauUngDung.XANH_LA);
                                } else if (s.contains("KHO")
                                        || s.contains("SPA")
                                        || s.contains("LƯU TRÚ")
                                        || s.contains("DANH MỤC")
                                        || s.contains("INVENTORY")
                                        || s.contains("SERVICE")
                                        || s.contains("CATALOG")) {
                                    nhanChu.setForeground(MauUngDung.NAU);
                                }
                            }
                            return nhanChu;
                        });
        bangBang.getColumnModel().getColumn(4).setCellRenderer(new PillBadgeCellRenderer());
    }

    private JLabel taoNhanBangNhatKy(
            Object giaTri, int canLe, int kieuChu, boolean daChon, int dong) {
        JLabel nhanChu = new JLabel(giaTri == null ? "" : giaTri.toString());
        nhanChu.setOpaque(true);
        nhanChu.setHorizontalAlignment(canLe);
        nhanChu.setFont(MauUngDung.taoPhongChu(kieuChu, 12));
        nhanChu.setBackground(
                daChon
                        ? bangBang.getSelectionBackground()
                        : (dong % 2 == 0 ? Color.WHITE : MauUngDung.DONG_XEN_KE));
        nhanChu.setForeground(daChon ? bangBang.getSelectionForeground() : MauUngDung.VAN_BAN_TOI);
        return nhanChu;
    }

    private void taiLai() {
        LocalDate tu = khungKhoangNgayBoChon.layNgayBatDau();
        LocalDate den = khungKhoangNgayBoChon.layNgayKetThuc();
        if (tu != null && den != null && tu.isAfter(den)) {
            HopThoai.hienThiCanhBao(this, "Từ ngày không được lớn hơn Đến ngày.");
            return;
        }
        int luot = ++luotTai;
        TacVuNen.chayCoPhien(
                this,
                () -> luot == luotTai,
                () -> new ArrayList<>(NhatKyHeThongService.macDinh().layCacMuc(tu, den)),
                cacMuc -> {
                    duLieuDaTai = cacMuc;
                    capNhatDanhSachNhanVien();
                    apDungBoLocClient();
                });
    }

    private void capNhatDanhSachNhanVien() {
        String luaChonTruoc = Objects.toString(cboNhanVienHopChon.getSelectedItem(), "Tất cả");
        Set<String> dsTen = new TreeSet<>(String::compareToIgnoreCase);
        for (DongNhatKyHeThong suKien : duLieuDaTai) {
            if (suKien.nguoiDung() != null && suKien.nguoiDung().getHoTen() != null) {
                String ten = suKien.nguoiDung().getHoTen().trim();
                if (!ten.isEmpty()) dsTen.add(ten);
            }
        }
        dangDatLaiBoLoc = true;
        try {
            cboNhanVienHopChon.removeAllItems();
            cboNhanVienHopChon.addItem("Tất cả");
            for (String ten : dsTen) {
                cboNhanVienHopChon.addItem(ten);
            }
            if (dsTen.contains(luaChonTruoc)) {
                cboNhanVienHopChon.setSelectedItem(luaChonTruoc);
            } else {
                cboNhanVienHopChon.setSelectedIndex(0);
            }
        } finally {
            dangDatLaiBoLoc = false;
        }
    }

    private void apDungBoLocClient() {
        LocalDate tu = khungKhoangNgayBoChon.layNgayBatDau();
        LocalDate den = khungKhoangNgayBoChon.layNgayKetThuc();
        int viTriLoai = cboSuKienKieuHopChon.getSelectedIndex();
        String kieuKhoa =
                viTriLoai > 0 && viTriLoai < MA_LOAI_SU_KIEN.length
                        ? MA_LOAI_SU_KIEN[viTriLoai]
                        : "";
        String ketQua = String.valueOf(cboKetQuaHopChon.getSelectedItem());
        String nhanVien = Objects.toString(cboNhanVienHopChon.getSelectedItem(), "Tất cả");
        String tuKhoa = oTruongNguoiDungTimKiem.getText().trim().toLowerCase(Locale.ROOT);
        apDungDanhSachNhatKy(duLieuDaTai, tu, den, kieuKhoa, ketQua, nhanVien, tuKhoa);
    }

    private void apDungDanhSachNhatKy(
            List<DongNhatKyHeThong> cacMuc,
            LocalDate tu,
            LocalDate den,
            String kieuKhoa,
            String ketQua,
            String nhanVien,
            String tuKhoa) {
        moHinhBang.setRowCount(0);
        duLieuDangHienThi = new ArrayList<>();
        int cacCanhBao = 0;
        for (DongNhatKyHeThong suKien : cacMuc) {
            if (!trongKhoangNgay(suKien, tu, den)) continue;
            if (!khopKieuSuKien(kieuKhoa, suKien.kieu())) continue;
            if (!"Tất cả".equals(ketQua) && !ketQua.equals(suKien.ketQua())) continue;
            String tenNguoiDung =
                    suKien.nguoiDung() != null && suKien.nguoiDung().getHoTen() != null
                            ? suKien.nguoiDung().getHoTen().trim()
                            : "";
            if (!"Tất cả".equals(nhanVien) && !nhanVien.equalsIgnoreCase(tenNguoiDung)) continue;
            String hanhDong = taoVanBanHanhDong(suKien);
            if (!khopTuKhoa(suKien, tenNguoiDung, hanhDong, tuKhoa)) continue;
            if ("Cảnh báo".equals(suKien.ketQua()) || "Thất bại".equals(suKien.ketQua()))
                cacCanhBao++;
            duLieuDangHienThi.add(suKien);
            moHinhBang.addRow(
                    new Object[] {
                        suKien.layVanBanThoiGian(),
                        tenNguoiDung,
                        hanhDong,
                        dinhDangLoaiSuKien(suKien.kieu()),
                        suKien.ketQua()
                    });
        }
        capNhatChiSoTieuDe(cacCanhBao);
    }

    private void capNhatChiSoTieuDe(int cacCanhBao) {
        String chiSo = "· " + moHinhBang.getRowCount() + " sự kiện";
        if (cacCanhBao > 0) chiSo += " · " + cacCanhBao + " cảnh báo/thất bại";
        boolean chamGioiHan = duLieuDaTai.size() >= GIOI_HAN_BAN_GHI;
        if (chamGioiHan) chiSo += " · Đạt giới hạn 2.000 dòng (nên thu hẹp khoảng ngày)";
        nhanNhanCacChiSo.setText(chiSo);
        nhanNhanCacChiSo.setForeground(chamGioiHan ? MauUngDung.CAM_NHAN : MauUngDung.VAN_BAN_XAM);
    }

    private static boolean trongKhoangNgay(DongNhatKyHeThong suKien, LocalDate tu, LocalDate den) {
        if (suKien.thoiGian() == null) return tu == null && den == null;
        LocalDate ngay = suKien.thoiGian().toLocalDate();
        if (tu != null && ngay.isBefore(tu)) return false;
        return den == null || !ngay.isAfter(den);
    }

    private static String taoVanBanHanhDong(DongNhatKyHeThong suKien) {
        return suKien.thaoTac() + (suKien.doiTuong().isBlank() ? "" : ": " + suKien.doiTuong());
    }

    private static boolean khopTuKhoa(
            DongNhatKyHeThong suKien, String tenNguoiDung, String hanhDong, String tuKhoa) {
        if (tuKhoa.isEmpty()) return true;
        return tenNguoiDung.toLowerCase(Locale.ROOT).contains(tuKhoa)
                || hanhDong.toLowerCase(Locale.ROOT).contains(tuKhoa)
                || suKien.loaiDoiTuong().toLowerCase(Locale.ROOT).contains(tuKhoa)
                || suKien.maDoiTuong().toLowerCase(Locale.ROOT).contains(tuKhoa)
                || suKien.ghiChu().toLowerCase(Locale.ROOT).contains(tuKhoa)
                || dinhDangLoaiSuKien(suKien.kieu()).toLowerCase(Locale.ROOT).contains(tuKhoa);
    }

    private static String dinhDangLoaiSuKien(String kieu) {
        if (kieu == null || kieu.isBlank()) return "";
        return switch (kieu.trim().toUpperCase(Locale.ROOT)) {
            case "LOGIN", "LOGOUT", "AUTH" -> "Đăng nhập & Tài khoản";
            case "PAYMENT" -> "Bán hàng & Thu ngân";
            case "INVENTORY" -> "Kho hàng";
            case "SERVICE" -> "Spa & Lưu trú";
            case "APPROVAL" -> "Phê duyệt";
            case "CATALOG", "CREATE", "UPDATE", "DELETE" -> "Danh mục & Nhân sự";
            case "SYSTEM" -> "Hệ thống";
            case "WARNING" -> "Cảnh báo";
            case "ERROR" -> "Lỗi";
            default -> kieu;
        };
    }

    private static boolean khopKieuSuKien(String kieuKhoa, String kieuSuKien) {
        if (kieuKhoa == null || kieuKhoa.isEmpty()) return true;
        if ("LOGIN".equals(kieuKhoa)) {
            return "LOGIN".equalsIgnoreCase(kieuSuKien)
                    || "LOGOUT".equalsIgnoreCase(kieuSuKien)
                    || "AUTH".equalsIgnoreCase(kieuSuKien);
        }
        if ("CATALOG".equals(kieuKhoa)) {
            return "CATALOG".equalsIgnoreCase(kieuSuKien)
                    || "CREATE".equalsIgnoreCase(kieuSuKien)
                    || "UPDATE".equalsIgnoreCase(kieuSuKien)
                    || "DELETE".equalsIgnoreCase(kieuSuKien);
        }
        return kieuKhoa.equalsIgnoreCase(kieuSuKien);
    }

    private void xoaCacBoLoc() {
        dangDatLaiBoLoc = true;
        try {
            khungKhoangNgayBoChon.chonKy("7 ngày qua");
            cboSuKienKieuHopChon.setSelectedIndex(0);
            cboKetQuaHopChon.setSelectedIndex(0);
            cboNhanVienHopChon.setSelectedIndex(0);
            oTruongNguoiDungTimKiem.setText("");
        } finally {
            dangDatLaiBoLoc = false;
        }
        taiLai();
    }

    private void xuLyHienThiChiTiet(int dong) {
        int moHinhDong = bangBang.convertRowIndexToModel(dong);
        if (moHinhDong < 0 || moHinhDong >= duLieuDangHienThi.size()) return;
        DongNhatKyHeThong suKien = duLieuDangHienThi.get(moHinhDong);
        String tenNguoi = Objects.toString(moHinhBang.getValueAt(moHinhDong, 1), "");
        String maNv =
                suKien.nguoiDung() != null && suKien.nguoiDung().getMaNhanVien() != null
                        ? suKien.nguoiDung().getMaNhanVien().trim()
                        : "";
        String nguoiThucHien = maNv.isEmpty() ? tenNguoi : tenNguoi + " (" + maNv + ")";
        StringBuilder chiTiet = new StringBuilder();
        chiTiet.append("CHI TIẾT SỰ KIỆN\n\n")
                .append("══════════════════════════════\n")
                .append("Thời gian: ")
                .append(suKien.layVanBanThoiGian())
                .append('\n')
                .append("Người thực hiện: ")
                .append(nguoiThucHien)
                .append('\n')
                .append("Phân loại: ")
                .append(dinhDangLoaiSuKien(suKien.kieu()))
                .append('\n')
                .append("Hành động: ")
                .append(suKien.thaoTac())
                .append('\n');
        if (!suKien.loaiDoiTuong().isBlank() || !suKien.maDoiTuong().isBlank()) {
            chiTiet.append("Đối tượng: ");
            if (!suKien.loaiDoiTuong().isBlank()) chiTiet.append(suKien.loaiDoiTuong());
            if (!suKien.loaiDoiTuong().isBlank() && !suKien.maDoiTuong().isBlank()) {
                chiTiet.append(" - ");
            }
            if (!suKien.maDoiTuong().isBlank()) chiTiet.append(suKien.maDoiTuong());
            chiTiet.append('\n');
        }
        String ghiChu = suKien.ghiChu().isBlank() ? suKien.doiTuong() : suKien.ghiChu();
        if (!ghiChu.isBlank()) chiTiet.append("Chi tiết: ").append(ghiChu).append('\n');
        chiTiet.append("Kết quả: ")
                .append(suKien.ketQua())
                .append('\n')
                .append("══════════════════════════════\n\n")
                .append("Nhật ký chỉ đọc, không thể sửa hoặc xóa.");
        HopThoai.hienThiThongTin(this, chiTiet.toString(), "Chi tiết nhật ký");
    }

    private void xuLyXuat() {
        XuatExcelDialog.xuatBang(this, bangBang, "Nhat_ky_he_thong");
    }

    @Override
    public void xuLyHienThi() {
        taiLai();
    }

    @Override
    public void xuLyLamMoi() {
        xoaCacBoLoc();
    }

    @Override
    protected String layTieuDeTroGiup() {
        return "Nhật ký hệ thống";
    }

    @Override
    protected String layNoiDungTroGiup() {
        return "Hướng dẫn tra cứu nhật ký hệ thống:\n\n"
                + "【 Bộ lọc 】\n"
                + "• Khoảng thời gian: Chọn khoảng ngày cần tra cứu (tối đa 2.000 dòng/lượt)\n"
                + "• Loại sự kiện: Lọc theo phân hệ nghiệp vụ (Đăng nhập & Tài khoản, Bán hàng"
                + " & Thu ngân, Kho hàng, Spa & Lưu trú, Phê duyệt, Danh mục & Nhân sự, Hệ"
                + " thống)\n"
                + "• Kết quả & Nhân viên: Lọc Thành công / Thất bại / Cảnh báo hoặc theo từng"
                + " nhân viên\n"
                + "• Tìm kiếm: Lọc nhanh theo tên nhân viên, hành động, đối tượng hoặc mã chứng"
                + " từ\n\n"
                + "【 Tra soát bất thường 】\n"
                + "• Hủy hóa đơn / Từ chối / Điều chỉnh cọc: Kiểm tra lý do ở cửa sổ chi tiết\n"
                + "• Đăng nhập thất bại nhiều lần: Nghi vấn truy cập trái phép\n\n"
                + "【 Xuất dữ liệu 】\n"
                + "Xuất kết quả ra tập tin Excel (.xlsx) để đối soát chi tiết\n\n"
                + "Mẹo: Nhấp đúp vào dòng để xem đầy đủ đối tượng, mã chứng từ và ghi chú.";
    }

    private static class PillBadgeCellRenderer extends JPanel implements TableCellRenderer {
        private final BadgePillLabel huyHieu = new BadgePillLabel();

        public PillBadgeCellRenderer() {
            super(new GridBagLayout());
            setOpaque(true);
            add(huyHieu);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            String val = value == null ? "" : value.toString();
            huyHieu.setText(val);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : MauUngDung.DONG_XEN_KE);
            }
            switch (val) {
                case "Thành công" ->
                        huyHieu.datMauHuyHieu(MauUngDung.mau(0xDCFCE7), MauUngDung.XANH_LA);
                case "Thất bại" ->
                        huyHieu.datMauHuyHieu(
                                MauUngDung.KHONG_HOP_LE_NEN, MauUngDung.VAN_BAN_NGUY_HIEM);
                case "Cảnh báo" ->
                        huyHieu.datMauHuyHieu(MauUngDung.mau(0xFEF3C7), MauUngDung.CAM_NHAN);
                default ->
                        huyHieu.datMauHuyHieu(MauUngDung.XAM_NEN_TRUNG, MauUngDung.mau(0x4B5563));
            }
            return this;
        }
    }

    private static class BadgePillLabel extends JLabel {
        private Color nenHuyHieu = Color.WHITE;

        public BadgePillLabel() {
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 12, 4, 12));
        }

        public void datMauHuyHieu(Color bg, Color fg) {
            this.nenHuyHieu = bg;
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getText() != null && !getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(nenHuyHieu);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
