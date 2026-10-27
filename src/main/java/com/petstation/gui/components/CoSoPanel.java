package com.petstation.gui.components;

import com.petstation.gui.components.common.DinhDangBang;

import java.awt.*;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Cung cấp giao diện và xử lý tương tác cho cơ sở vùng hiển thị.
 *
 * <p>Lớp nền cho mọi màn hình nghiệp vụ. Tự đăng ký ô tìm kiếm và bảng đầu tiên để thanh công cụ
 * (Tìm, In) hoạt động thật, và tự nối ô tìm kiếm với bộ lọc của bảng.
 */
public abstract class CoSoPanel extends JPanel implements HoTroThanhCongCu {
    /** Chính tìm kiếm. */
    private JTextField txtChinhTimKiem;

    /** Chính bảng. */
    private JTable tblChinhBang;

    /** Chưa ràng buộc tìm kiếm. */
    private JTextField txtChuaRangBuocTimKiem;

    public CoSoPanel() {
        com.petstation.gui.components.common.HoTroCuon.theoDoi(this);
        setBackground(MauUngDung.NEN_KEM);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));
    }

    // ── Thanh công cụ ─────────────────────────────────────────────
    @Override
    public KhaNangThanhCongCu layKhaNangThanhCongCu() {
        return new KhaNangThanhCongCu(
                false, false, txtChinhTimKiem != null, tblChinhBang != null, false, true);
    }

    @Override
    public void xuLyTimKiem() {
        if (txtChinhTimKiem != null) {
            txtChinhTimKiem.requestFocusInWindow();
            txtChinhTimKiem.selectAll();
        }
    }

    @Override
    public void xuLyIn() {
        if (tblChinhBang == null) return;
        try {
            tblChinhBang.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat(layTieuDeTroGiup()),
                    new MessageFormat("Trang {0}"));
        } catch (java.awt.print.PrinterException | HeadlessException ngoaiLe) {
            HopThoai.hienThiThongBaoHopThoai(
                    this,
                    "Không thể in: " + ngoaiLe.getMessage(),
                    "In",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void xuLyTroGiup() {
        HopThoai.hienThiThongBaoHopThoai(
                this, layNoiDungTroGiup(), layTieuDeTroGiup(), JOptionPane.INFORMATION_MESSAGE);
    }

    protected String layTieuDeTroGiup() {
        return "Trợ giúp";
    }

    protected String layNoiDungTroGiup() {
        return txtChinhTimKiem != null
                ? "Gõ vào ô tìm kiếm (Ctrl+F) để lọc danh sách. Bấm tiêu đề cột để sắp xếp."
                : "Dùng các nút trên màn hình để thao tác.";
    }

    // ── Thẻ thống kê ──────────────────────────────────────────────
    /**
     * Lấy chỉ số thẻ trong CoSoPanel.
     *
     * <p>Số liệu thường dùng màu chữ trung tính; chỉ trạng thái bất thường mới có màu.
     *
     * @param nhan nhãn
     * @param giaTri giá trị
     * @param boQuaBieuTuong bỏ qua biểu tượng
     * @param nhan2 nhấn
     * @return kết quả lấy chỉ số thẻ
     */
    protected JPanel layChiSoThe(String nhan, String giaTri, String boQuaBieuTuong, Color nhan2) {
        // Thẻ.
        BoGocPanel pnlThe = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        pnlThe.setLayout(new BorderLayout(0, 4));
        pnlThe.setBorder(new EmptyBorder(14, 18, 14, 18));

        // Nhãn.
        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
        lblNhan.setForeground(MauUngDung.VAN_BAN_XAM);

        JLabel lblGiaTri = new JLabel(giaTri);
        lblGiaTri.setFont(MauUngDung.taoPhongChu(Font.BOLD, 22));
        lblGiaTri.setForeground(nhan2 == null ? MauUngDung.VAN_BAN_TOI : nhan2);

        pnlThe.add(lblNhan, BorderLayout.NORTH);
        pnlThe.add(lblGiaTri, BorderLayout.CENTER);
        return pnlThe;
    }

    protected JPanel layChiSoThe(String nhan, String giaTri) {
        return layChiSoThe(nhan, giaTri, null, MauUngDung.VAN_BAN_TOI);
    }

    // ── Tiêu đề ───────────────────────────────────────────────────
    protected JLabel taoTieuDeMuc(String vanBan) {
        // Dòng đang xét.
        JLabel lblDongDangXet = new JLabel(vanBan);
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 16));
        lblDongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        return lblDongDangXet;
    }

    // ── Bảng ──────────────────────────────────────────────────────
    protected JScrollPane taoBangDinhDang(String[] cacCot, Object[][] duLieu) {
        // Mô hình.
        DefaultTableModel moHinh =
                new DefaultTableModel(duLieu, cacCot) {
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
        return taoBangDinhDang(moHinh);
    }

    protected JScrollPane taoBangDinhDang(TableModel moHinh) {
        // Bảng.
        JTable tblBang = new JTable(moHinh);
        DinhDangBang.apDung(tblBang);

        // Khung cuộn.
        JScrollPane scrKhungCuon = new JScrollPane(tblBang);
        scrKhungCuon.setBorder(BorderFactory.createLineBorder(MauUngDung.VIEN, 1));
        scrKhungCuon.getViewport().setBackground(MauUngDung.THE_NEN);

        if (tblChinhBang == null) tblChinhBang = tblBang;
        if (txtChuaRangBuocTimKiem != null) {
            lienKetBoLoc(txtChuaRangBuocTimKiem, tblBang);
            txtChuaRangBuocTimKiem = null;
        }
        return scrKhungCuon;
    }

    /** Nhạt xóa ghi chú. */
    protected static final String NHAT_XOA_GHI_CHU =
            "Bản ghi sẽ chuyển sang \"Ngừng hoạt động\" (xóa mềm), vẫn giữ lịch sử giao dịch.";

    protected static JTable layBang(JScrollPane scrCuon) {
        return (JTable) scrCuon.getViewport().getView();
    }

    /**
     * Liên kết bộ lọc trong CoSoPanel.
     *
     * <p>Lọc các dòng chứa chuỗi tìm kiếm (không phân biệt hoa thường) trên mọi cột.
     *
     * @param txtTimKiem tìm kiếm
     * @param tblBang bảng
     */
    protected void lienKetBoLoc(JTextField txtTimKiem, JTable tblBang) {
        txtTimKiem
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            /** Áp dụng trong lớp xử lý giao diện. */
                            private void apDung() {
                                // Từ khóa.
                                String tuKhoa = txtTimKiem.getText().trim();
                                // Bộ sắp xếp.
                                @SuppressWarnings("unchecked")
                                TableRowSorter<TableModel> boSapXep =
                                        (TableRowSorter<TableModel>) tblBang.getRowSorter();
                                if (boSapXep == null) return;
                                boSapXep.setRowFilter(
                                        tuKhoa.isEmpty()
                                                ? null
                                                : RowFilter.regexFilter(
                                                        "(?iu)"
                                                                + java.util.regex.Pattern.quote(
                                                                        tuKhoa)));
                            }

                            /**
                             * Thêm cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void insertUpdate(DocumentEvent suKien) {
                                apDung();
                            }

                            /**
                             * Xóa cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void removeUpdate(DocumentEvent suKien) {
                                apDung();
                            }

                            /**
                             * Lấy đã thay đổi cập nhật trong lớp xử lý giao diện.
                             *
                             * @param suKien sự kiện
                             */
                            @Override
                            public void changedUpdate(DocumentEvent suKien) {
                                apDung();
                            }
                        });
    }

    // ── Ô tìm kiếm ────────────────────────────────────────────────
    /**
     * Tạo ô tìm kiếm trong CoSoPanel.
     *
     * <p>Ô tìm kiếm có gợi ý mờ (vẽ đè, không chèn chữ giả vào nội dung).
     *
     * @param goiYNhap gợi ý nhập
     * @return kết quả tạo ô tìm kiếm
     */
    protected JTextField taoOTimKiem(String goiYNhap) {
        JTextField txtTruongDangXet =
                new JTextField() {
                    /**
                     * Vẽ thành phần trong lớp xử lý giao diện.
                     *
                     * @param doHoa đồ họa
                     */
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        super.paintComponent(doHoa);
                        if (getText().isEmpty() && !isFocusOwner()) {
                            // Đồ họa 2.
                            Graphics2D doHoa2 = (Graphics2D) doHoa.create();
                            doHoa2.setRenderingHint(
                                    RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            doHoa2.setColor(MauUngDung.VO_HIEU_HOA);
                            doHoa2.setFont(getFont());
                            // Vào.
                            Insets vao = getInsets();
                            doHoa2.drawString(
                                    goiYNhap,
                                    vao.left,
                                    (getHeight()
                                                    + doHoa2.getFontMetrics().getAscent()
                                                    - doHoa2.getFontMetrics().getDescent())
                                            / 2);
                            doHoa2.dispose();
                        }
                    }
                };
        txtTruongDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        txtTruongDangXet.setForeground(MauUngDung.VAN_BAN_TOI);
        txtTruongDangXet.setPreferredSize(new Dimension(280, 36));
        txtTruongDangXet.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MauUngDung.VIEN, 1, true),
                        new EmptyBorder(4, 12, 4, 12)));
        txtTruongDangXet.getAccessibleContext().setAccessibleName(goiYNhap);
        txtTruongDangXet.addFocusListener(
                new java.awt.event.FocusAdapter() {
                    /**
                     * Lấy tiêu điểm mất trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void focusLost(java.awt.event.FocusEvent suKien) {
                        txtTruongDangXet.repaint();
                    }

                    /**
                     * Lấy tiêu điểm nhận trong lớp xử lý giao diện.
                     *
                     * @param suKien sự kiện
                     */
                    @Override
                    public void focusGained(java.awt.event.FocusEvent suKien) {
                        txtTruongDangXet.repaint();
                    }
                });
        if (txtChinhTimKiem == null) txtChinhTimKiem = txtTruongDangXet;
        txtChuaRangBuocTimKiem = txtTruongDangXet;
        return txtTruongDangXet;
    }

    // ── Huy hiệu / thẻ ────────────────────────────────────────────
    protected JLabel layNhan(String vanBan, Color nen, Color chu) {
        // Dòng đang xét.
        JLabel lblDongDangXet =
                new JLabel(vanBan) {
                    /**
                     * Vẽ thành phần trong lớp xử lý giao diện.
                     *
                     * @param doHoa đồ họa
                     */
                    @Override
                    protected void paintComponent(Graphics doHoa) {
                        // Đồ họa 2.
                        Graphics2D doHoa2 = (Graphics2D) doHoa.create();
                        doHoa2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        doHoa2.setColor(nen);
                        doHoa2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        doHoa2.dispose();
                        super.paintComponent(doHoa);
                    }
                };
        lblDongDangXet.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        lblDongDangXet.setForeground(chu);
        lblDongDangXet.setBorder(new EmptyBorder(3, 8, 3, 8));
        lblDongDangXet.setOpaque(false);
        return lblDongDangXet;
    }

    protected BoGocPanel layThe(LayoutManager boCuc) {
        BoGocPanel pnlVungDangXet = new BoGocPanel(12, MauUngDung.THE_NEN, true);
        pnlVungDangXet.setLayout(boCuc);
        pnlVungDangXet.setBorder(new EmptyBorder(18, 20, 18, 20));
        return pnlVungDangXet;
    }

    protected JPanel taoHangNgang(Component... cacThanhPhan) {
        JPanel pnlVungDangXet = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlVungDangXet.setOpaque(false);
        for (Component giaTriDangXet : cacThanhPhan) pnlVungDangXet.add(giaTriDangXet);
        return pnlVungDangXet;
    }

    protected JPanel taoVungTrongSuot(LayoutManager boCuc) {
        JPanel pnlVungDangXet = new JPanel(boCuc);
        pnlVungDangXet.setOpaque(false);
        return pnlVungDangXet;
    }

    /**
     * Lấy trống trạng thái trong CoSoPanel.
     *
     * <p>Trạng thái rỗng thân thiện: tiêu đề, giải thích và một nút gợi ý bước tiếp theo.
     *
     * @param tieuDe tiêu đề
     * @param thongBao thông báo
     * @param btnThaoTac thao tác
     * @return kết quả lấy trống trạng thái
     */
    protected JPanel layTrongTrangThai(String tieuDe, String thongBao, JButton btnThaoTac) {
        // Hộp.
        JPanel pnlHop = new JPanel();
        pnlHop.setOpaque(false);
        pnlHop.setLayout(new BoxLayout(pnlHop, BoxLayout.Y_AXIS));
        JLabel lblGiaTriTam = new JLabel(tieuDe);
        lblGiaTriTam.setFont(MauUngDung.taoPhongChu(Font.BOLD, 18));
        lblGiaTriTam.setForeground(MauUngDung.VAN_BAN_TOI);
        lblGiaTriTam.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Mô hình đang xét.
        JLabel lblMoHinhDangXet = new JLabel(thongBao);
        lblMoHinhDangXet.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblMoHinhDangXet.setForeground(MauUngDung.VAN_BAN_XAM);
        lblMoHinhDangXet.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlHop.add(lblGiaTriTam);
        pnlHop.add(Box.createVerticalStrut(6));
        pnlHop.add(lblMoHinhDangXet);
        if (btnThaoTac != null) {
            btnThaoTac.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlHop.add(Box.createVerticalStrut(16));
            pnlHop.add(btnThaoTac);
        }
        // Giữa.
        JPanel pnlGiua = new JPanel(new GridBagLayout());
        pnlGiua.setOpaque(false);
        pnlGiua.add(pnlHop);
        return pnlGiua;
    }
}
