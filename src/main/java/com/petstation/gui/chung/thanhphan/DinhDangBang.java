package com.petstation.gui.chung.thanhphan;

import com.petstation.gui.chung.MauUngDung;
import com.petstation.util.DinhDang;

import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public final class DinhDangBang {
    private DinhDangBang() {}

    public static void apDung(JTable bangBang) {
        ToolTipManager.sharedInstance().setInitialDelay(250);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        bangBang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        bangBang.setRowHeight(38);
        bangBang.setGridColor(MauUngDung.VIEN);
        bangBang.setBackground(MauUngDung.THE_NEN);
        bangBang.setSelectionBackground(MauUngDung.DO_NHAT);
        bangBang.setSelectionForeground(MauUngDung.VAN_BAN_TOI);
        bangBang.setShowVerticalLines(false);
        bangBang.setShowHorizontalLines(true);
        bangBang.setIntercellSpacing(new Dimension(0, 1));
        bangBang.setFillsViewportHeight(true);
        bangBang.setAutoCreateRowSorter(true);
        bangBang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bangBang.setFocusable(true);
        bangBang.getAccessibleContext().setAccessibleName("Bảng dữ liệu");
        bangBang.setDefaultRenderer(Object.class, taoTrinhVeOBang());
        cauHinhTieuDeBang(bangBang);
        canChinhDoRongCot(bangBang);
        dangKyTuDongCanChinh(bangBang);
    }

    private static DefaultTableCellRenderer taoTrinhVeOBang() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable bangGiaTriTam,
                    Object giaTri,
                    boolean daChon,
                    boolean tieuDiem,
                    int dong,
                    int cot) {
                super.getTableCellRendererComponent(
                        bangGiaTriTam, giaTri, daChon, false, dong, cot);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                String vanBan = giaTri == null ? "" : giaTri.toString();
                setBackground(
                        daChon
                                ? bangGiaTriTam.getSelectionBackground()
                                : (dong % 2 == 0 ? MauUngDung.THE_NEN : MauUngDung.DONG_XEN_KE));
                setForeground(MauUngDung.VAN_BAN_TOI);
                setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
                setHorizontalAlignment(SwingConstants.LEFT);
                if (kiemTraCotTrangThai(bangGiaTriTam, cot)) {
                    setForeground(MauUngDung.layMauTongMau(vanBan));
                    setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
                } else if (layCoDangDangSo(vanBan)) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                vanBan = dinhDangGiaTriO(bangGiaTriTam, cot, giaTri, vanBan);
                setText(vanBan);
                int rongCot =
                        cot >= 0 && cot < bangGiaTriTam.getColumnModel().getColumnCount()
                                ? bangGiaTriTam.getColumnModel().getColumn(cot).getWidth()
                                : 0;
                boolean biChe =
                        !vanBan.isEmpty()
                                && ((rongCot > 0
                                                && getFontMetrics(getFont()).stringWidth(vanBan)
                                                                + 24
                                                        > rongCot)
                                        || vanBan.length() > 20);
                setToolTipText(biChe ? vanBan : null);
                return this;
            }
        };
    }

    private static String dinhDangGiaTriO(
            JTable bangDuLieu, int cot, Object giaTri, String vanBan) {
        String tenCot = bangDuLieu.getColumnName(cot).toLowerCase(Locale.ROOT);
        String ngayFormatted = dinhDangNgay(giaTri);
        if (ngayFormatted != null) return ngayFormatted;
        if (laTien(tenCot)) {
            if (giaTri instanceof Number n) return DinhDang.dinhDang(Math.round(n.doubleValue()));
            if (!vanBan.isEmpty()) {
                try {
                    return DinhDang.dinhDang(
                            Math.round(Double.parseDouble(vanBan.replace(",", "").trim())));
                } catch (NumberFormatException ex) {
                    LoggerFactory.getLogger(DinhDangBang.class)
                            .debug("Giữ nguyên giá trị cột tiền '{}': {}", vanBan, ex.getMessage());
                }
            }
            return vanBan;
        }
        if (kiemTraCotTrangThai(bangDuLieu, cot)
                || tenCot.contains("thanh toán")
                || tenCot.contains("phương thức")) return tenTrangThai(vanBan);
        return vanBan;
    }

    private static void cauHinhTieuDeBang(JTable bangBang) {
        JTableHeader tieuDeBang = bangBang.getTableHeader();
        tieuDeBang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        tieuDeBang.setBackground(MauUngDung.KEM_NEN_NHAN);
        tieuDeBang.setForeground(MauUngDung.VAN_BAN_TOI);
        tieuDeBang.setPreferredSize(new Dimension(tieuDeBang.getWidth(), 38));
        tieuDeBang.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));
        tieuDeBang.setReorderingAllowed(false);
        tieuDeBang.setResizingAllowed(true);
        if (tieuDeBang.getClientProperty("hoverHeaderTooltip") == null) {
            tieuDeBang.putClientProperty("hoverHeaderTooltip", true);
            tieuDeBang.addMouseMotionListener(
                    new MouseMotionAdapter() {
                        @Override
                        public void mouseMoved(MouseEvent e) {
                            int cot = tieuDeBang.columnAtPoint(e.getPoint());
                            if (cot < 0 || cot >= bangBang.getColumnModel().getColumnCount()) {
                                tieuDeBang.setToolTipText(null);
                                return;
                            }
                            TableColumn tc = bangBang.getColumnModel().getColumn(cot);
                            String tieuDe = String.valueOf(tc.getHeaderValue());
                            int rongChu =
                                    tieuDeBang
                                                    .getFontMetrics(tieuDeBang.getFont())
                                                    .stringWidth(tieuDe)
                                            + 20;
                            tieuDeBang.setToolTipText(rongChu > tc.getWidth() ? tieuDe : null);
                        }
                    });
        }
        tieuDeBang.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (tieuDeBang.getResizingColumn() != null)
                            bangBang.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    }
                });
    }

    private static void dangKyTuDongCanChinh(JTable bangBang) {
        if (bangBang.getClientProperty("modelResizeListener") == null
                && bangBang.getModel() != null) {
            bangBang.putClientProperty("modelResizeListener", true);
            bangBang.getModel()
                    .addTableModelListener(
                            e -> SwingUtilities.invokeLater(() -> canChinhDoRongCot(bangBang)));
        }
        Runnable resize =
                () -> {
                    if (bangBang.getParent() instanceof JViewport vp && vp.getWidth() > 0) {
                        canChinhDoRongCot(bangBang);
                        bangBang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    }
                };
        if (bangBang.getClientProperty("resizeColumns") == null) {
            bangBang.putClientProperty("resizeColumns", true);
            bangBang.addHierarchyListener(
                    e -> {
                        if (bangBang.getParent() instanceof JViewport vp
                                && vp.getClientProperty(
                                                "resize:" + System.identityHashCode(bangBang))
                                        == null) {
                            vp.putClientProperty(
                                    "resize:" + System.identityHashCode(bangBang), true);
                            vp.addComponentListener(
                                    new ComponentAdapter() {
                                        @Override
                                        public void componentResized(ComponentEvent event) {
                                            resize.run();
                                        }
                                    });
                            resize.run();
                        }
                    });
        }
        resize.run();
    }

    private static final List<String> TU_KHOA_COT_DAI =
            List.of(
                    "tên",
                    "sản phẩm",
                    "hàng hóa",
                    "khách hàng",
                    "chủ nuôi",
                    "dịch vụ",
                    "nhân viên",
                    "thu ngân",
                    "người",
                    "nhà cung cấp",
                    "ghi chú",
                    "mô tả",
                    "lý do",
                    "nội dung",
                    "hành động",
                    "địa chỉ",
                    "bảng giá",
                    "điều khoản",
                    "chương trình");

    public static void canChinhDoRongCot(JTable bangBang) {
        if (bangBang == null || bangBang.getColumnCount() == 0) return;
        FontMetrics hfm =
                bangBang.getTableHeader() != null
                        ? bangBang.getTableHeader()
                                .getFontMetrics(bangBang.getTableHeader().getFont())
                        : bangBang.getFontMetrics(bangBang.getFont());
        FontMetrics cfm = bangBang.getFontMetrics(bangBang.getFont());
        int tongToiThieu = 0;
        for (int chiSo = 0; chiSo < bangBang.getColumnCount(); chiSo++) {
            TableColumn cot = bangBang.getColumnModel().getColumn(chiSo);
            tongToiThieu += canChinhMotCot(bangBang, cot, chiSo, hfm, cfm);
        }
        int rongKhung =
                (bangBang.getParent() != null && bangBang.getParent().getWidth() > 200)
                        ? bangBang.getParent().getWidth()
                        : bangBang.getWidth();
        if (rongKhung > 200 && tongToiThieu > rongKhung - 6) {
            double tiLe = (double) (rongKhung - 6) / tongToiThieu;
            for (int chiSo = 0; chiSo < bangBang.getColumnCount(); chiSo++) {
                TableColumn cot = bangBang.getColumnModel().getColumn(chiSo);
                cot.setMinWidth(Math.max(48, (int) Math.floor(cot.getMinWidth() * tiLe)));
            }
        }
    }

    private static int canChinhMotCot(
            JTable bangBang, TableColumn cot, int chiSo, FontMetrics hfm, FontMetrics cfm) {
        String tieuDe = String.valueOf(cot.getHeaderValue()).trim();
        String ten = tieuDe.toLowerCase(Locale.ROOT);
        int rongTieuDe = hfm != null ? hfm.stringWidth(tieuDe) + 22 : 90;
        int rongDuLieu = doRongDuLieuCot(bangBang, chiSo, cot.getModelIndex(), ten, cfm);
        boolean laCotVanBanDai = TU_KHOA_COT_DAI.stream().anyMatch(ten::contains);
        int canThiet = Math.max(rongTieuDe, rongDuLieu);
        int toiThieu =
                (!laCotVanBanDai && canThiet <= 165)
                        ? canThiet
                        : Math.max(85, Math.min(130, rongTieuDe));
        int uuTien =
                (!laCotVanBanDai && canThiet <= 165)
                        ? canThiet + 6
                        : Math.max(toiThieu + 40, Math.min(310, canThiet));
        cot.setMinWidth(toiThieu);
        cot.setMaxWidth(Integer.MAX_VALUE);
        cot.setResizable(true);
        cot.setPreferredWidth(uuTien);
        return toiThieu;
    }

    private static int doRongDuLieuCot(
            JTable bangBang, int chiSo, int modelCol, String ten, FontMetrics cfm) {
        TableModel model = bangBang.getModel();
        int soDong = model == null ? 0 : Math.min(model.getRowCount(), 25);
        int rong = 0;
        for (int r = 0; r < soDong; r++) {
            if (modelCol < 0 || modelCol >= model.getColumnCount()) break;
            Object val;
            try {
                val = model.getValueAt(r, modelCol);
            } catch (RuntimeException ignored) {
                break;
            }
            if (val == null) continue;
            String s = dinhDangGiaTriO(bangBang, chiSo, val, val.toString());
            if (cfm != null && !s.startsWith("<html>"))
                rong = Math.max(rong, cfm.stringWidth(s) + 22);
        }
        return rong;
    }

    private static String dinhDangNgay(Object val) {
        if (val instanceof LocalDate ld) {
            return DinhDang.ngay(ld);
        }
        if (val instanceof LocalDateTime ldt) {
            return DinhDang.ngayGio(ldt);
        }
        if (val instanceof Date d) {
            LocalDateTime ldt =
                    Instant.ofEpochMilli(d.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
            return "Timestamp".equals(d.getClass().getSimpleName())
                    ? DinhDang.ngayGio(ldt)
                    : DinhDang.ngay(ldt.toLocalDate());
        }
        return null;
    }

    private static boolean laTien(String ten) {
        if (ten.contains("lượt")
                || ten.contains("lần")
                || ten.contains("số lượng")
                || ten.contains("đã dùng")
                || ten.contains("bảng giá")
                || ten.startsWith("mã ")) {
            return false;
        }
        return ten.matches(
                ".*(tiền|giá|doanh thu|chi tiêu|cọc|nợ|tổng nhập|đã trả|thực đếm|chênh lệch|thu hóa"
                        + " đơn|chiết khấu|voucher|hoàn trả|lợi nhuận|thành tiền).*");
    }

    private static final Map<String, String> TEN_TRANG_THAI =
            Map.ofEntries(
                    Map.entry("DA_THANH_TOAN", "Đã thanh toán"),
                    Map.entry("CHO_THANH_TOAN", "Chờ thanh toán"),
                    Map.entry("GIO_TREO", "Giỏ treo"),
                    Map.entry("DA_XAC_NHAN", "Đã xác nhận"),
                    Map.entry("DA_HEN", "Đã xác nhận"),
                    Map.entry("TIEP_NHAN", "Tiếp nhận"),
                    Map.entry("DA_TIEP_NHAN", "Tiếp nhận"),
                    Map.entry("DANG_THUC_HIEN", "Đang thực hiện"),
                    Map.entry("HOAN_TAT", "Hoàn tất"),
                    Map.entry("HOAN_THANH", "Hoàn tất"),
                    Map.entry("DA_HUY", "Đã hủy"),
                    Map.entry("KHONG_DEN", "Không đến"),
                    Map.entry("DA_DAT", "Đã đặt"),
                    Map.entry("DANG_O", "Đang ở"),
                    Map.entry("DA_NHAN", "Đang ở"),
                    Map.entry("DA_TRA_PHONG", "Đã trả phòng"),
                    Map.entry("TIEN_MAT", "Tiền mặt"),
                    Map.entry("CHUYEN_KHOAN", "Chuyển khoản"),
                    Map.entry("THE_NGAN_HANG", "Thẻ"),
                    Map.entry("THE", "Thẻ"),
                    Map.entry("QUET_THE", "Thẻ"),
                    Map.entry("DIEM_TICH_LUY", "Điểm tích lũy"),
                    Map.entry("TRONG", "Trống"),
                    Map.entry("KHA_DUNG", "Trống"),
                    Map.entry("CO_THU", "Có thú"),
                    Map.entry("DANG_DON", "Đang dọn"),
                    Map.entry("BAO_TRI", "Bảo trì"),
                    Map.entry("HOAT_DONG", "Đang hoạt động"),
                    Map.entry("DANG_HOAT_DONG", "Đang hoạt động"),
                    Map.entry("TAM_KHOA", "Tạm khóa"),
                    Map.entry("NGUNG_HOAT_DONG", "Ngừng hoạt động"),
                    Map.entry("DANG_BAN", "Đang kinh doanh"),
                    Map.entry("NGUNG_BAN", "Ngừng kinh doanh"),
                    Map.entry("SAN_SANG", "Sẵn sàng"),
                    Map.entry("CHO_XUAT", "Chờ xuất"),
                    Map.entry("DA_XUAT", "Đã xuất"),
                    Map.entry("CHO_NHAP", "Chờ nhập"),
                    Map.entry("DA_NHAP", "Đã nhập"),
                    Map.entry("XUAT_HUY", "Xuất hủy"),
                    Map.entry("DANG_KIEM", "Đang kiểm kê"),
                    Map.entry("DA_KIEM", "Đã kiểm kê"),
                    Map.entry("DA_DOI_SOAT", "Đã đối soát"),
                    Map.entry("HET_HANG", "Hết hàng"),
                    Map.entry("CON_HANG", "Còn hàng"),
                    Map.entry("DA_CAP_NHAT", "Đã cập nhật"),
                    Map.entry("CHO_DUYET", "Chờ duyệt"),
                    Map.entry("DA_DUYET", "Đã duyệt"),
                    Map.entry("TU_CHOI", "Từ chối"),
                    Map.entry("CAN_THEM_THONG_TIN", "Cần thêm thông tin"),
                    Map.entry("CHO_XU_LY", "Chờ xử lý"),
                    Map.entry("DANG_XU_LY", "Đang xử lý"),
                    Map.entry("DANG_MO", "Đang mở"),
                    Map.entry("DA_KHOA", "Đã khóa"),
                    Map.entry("DA_BAN_GIAO", "Đã bàn giao"),
                    Map.entry("CON_HAN", "Còn hạn"),
                    Map.entry("HET_HAN", "Hết hạn"),
                    Map.entry("DA_DUNG", "Đã dùng"),
                    Map.entry("DANG_HOP_TAC", "Đang hợp tác"),
                    Map.entry("NGUNG_HOP_TAC", "Ngừng hợp tác"),
                    Map.entry("DOI_HANG", "Đổi hàng"),
                    Map.entry("TRA_HANG", "Trả hàng"),
                    Map.entry("DA_HOAN", "Trả hàng"),
                    Map.entry("DI_LAM", "Đi làm"),
                    Map.entry("NGHI_PHEP", "Nghỉ phép"),
                    Map.entry("DA_GHI_SO", "Đã ghi sổ"));

    public static String tenTrangThai(String s) {
        if (s == null) return "";
        String trimmed = s.trim();
        String normalized = trimmed.toUpperCase().replace(" ", "");
        if ("DOI_HANG:HOAN_TAT".equals(normalized)) return "Hoàn tất đổi hàng";
        if ("DOI_HANG:CHO_XU_LY".equals(normalized)) return "Chờ xử lý đổi hàng";
        if ("DOI_HANG:DA_HUY".equals(normalized)) return "Đã hủy đổi hàng";
        if (normalized.startsWith("TRA_HANG_CU")) return "Lịch sử hoàn tiền";
        if (trimmed.contains(":")) {
            String[] parts = trimmed.split(":", 2);
            return tenTrangThai(parts[0]) + ": " + tenTrangThai(parts[1]);
        }
        return TEN_TRANG_THAI.getOrDefault(trimmed.toUpperCase(), s);
    }

    private static boolean kiemTraCotTrangThai(JTable bangBang, int cot) {
        String ten = String.valueOf(bangBang.getColumnName(cot)).toLowerCase();
        return ten.contains("trạng thái") || ten.equals("tiến độ") || ten.contains("hạng thẻ");
    }

    private static boolean layCoDangDangSo(String vanBan) {
        if (vanBan.isEmpty() || vanBan.length() > 20) return false;
        String giaTriTam =
                vanBan.replace("₫", "")
                        .replace(".", "")
                        .replace(",", "")
                        .replace("%", "")
                        .replace("-", "")
                        .replace("kg", "")
                        .replace("lần", "")
                        .replace("lượt", "")
                        .trim();
        return !giaTriTam.isEmpty() && giaTriTam.chars().allMatch(Character::isDigit);
    }

    public static JScrollPane taoBangKhongCuon(JTable bang, Dimension kichThuocViewport) {
        bang.setPreferredScrollableViewportSize(kichThuocViewport);
        JScrollPane cuon = new JScrollPane(bang);
        cuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN));
        cuon.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        cuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return cuon;
    }

    public static void coGianChieuCaoBangTheoNoiDung(
            JTable bang, JScrollPane cuon, JPanel vungChua, int soDong) {
        int caoDong = Math.max(26, bang.getRowHeight());
        int caoTieuDe =
                bang.getTableHeader() != null
                        ? bang.getTableHeader().getPreferredSize().height
                        : 26;
        if (caoTieuDe <= 0) caoTieuDe = 26;
        int caoMoi = caoTieuDe + Math.max(1, soDong) * caoDong + 4;
        cuon.setPreferredSize(new Dimension(340, caoMoi));
        cuon.setMinimumSize(new Dimension(280, caoMoi));
        vungChua.setPreferredSize(null);
        vungChua.revalidate();
        vungChua.repaint();
    }

    public static JPanel taoHangNhap(
            String nhan, JComponent oNhap, int khoangCachDoc, int coChuNhan) {
        JPanel hang = new JPanel(new BorderLayout(0, khoangCachDoc));
        hang.setOpaque(false);
        JLabel tieuDe = new JLabel(nhan);
        tieuDe.setFont(MauUngDung.taoPhongChu(Font.PLAIN, coChuNhan));
        tieuDe.setForeground(MauUngDung.VAN_BAN_XAM);
        hang.add(tieuDe, BorderLayout.NORTH);
        hang.add(oNhap, BorderLayout.CENTER);
        return hang;
    }

    public static void xoaDongDangChon(JTable bang, DefaultTableModel moHinh, Runnable sauKhiXoa) {
        int dongChon = bang.getSelectedRow();
        if (dongChon >= 0) {
            moHinh.removeRow(bang.convertRowIndexToModel(dongChon));
            if (sauKhiXoa != null) sauKhiXoa.run();
        }
    }

    public static void capNhatHoacThemDong(
            JTable bang, DefaultTableModel moHinh, boolean laSua, int dong, Object[] duLieu) {
        int dongMoHinh;
        if (laSua) {
            for (int c = 0; c < duLieu.length; c++) moHinh.setValueAt(duLieu[c], dong, c);
            dongMoHinh = dong;
        } else {
            moHinh.addRow(duLieu);
            dongMoHinh = moHinh.getRowCount() - 1;
        }
        int dongHienThi =
                bang.getRowSorter() != null ? bang.convertRowIndexToView(dongMoHinh) : dongMoHinh;
        if (dongHienThi >= 0 && dongHienThi < bang.getRowCount()) {
            bang.setRowSelectionInterval(dongHienThi, dongHienThi);
            bang.scrollRectToVisible(bang.getCellRect(dongHienThi, 0, true));
        }
    }

    public static DefaultTableModel taoMoHinhChiDoc(Object[] cot) {
        return taoMoHinhChiDoc(new Object[0][], cot);
    }

    public static DefaultTableModel taoMoHinhChiDoc(Object[][] duLieu, Object[] cot) {
        return new DefaultTableModel(duLieu, cot) {
            @Override
            public boolean isCellEditable(int dong, int cotIdx) {
                return false;
            }
        };
    }
}
