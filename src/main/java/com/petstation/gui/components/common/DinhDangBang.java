package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public final class DinhDangBang {
    private DinhDangBang() {
    }

    public static void apDung(JTable tblBang) {
        javax.swing.ToolTipManager.sharedInstance().setInitialDelay(250);
        javax.swing.ToolTipManager.sharedInstance().setDismissDelay(10000);
        tblBang.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        tblBang.setRowHeight(38);
        tblBang.setGridColor(MauUngDung.VIEN);
        tblBang.setBackground(MauUngDung.THE_NEN);
        tblBang.setSelectionBackground(MauUngDung.DO_NHAT);
        tblBang.setSelectionForeground(MauUngDung.VAN_BAN_TOI);
        tblBang.setShowVerticalLines(false);
        tblBang.setShowHorizontalLines(true);
        tblBang.setIntercellSpacing(new Dimension(0, 1));
        tblBang.setFillsViewportHeight(true);
        tblBang.setAutoCreateRowSorter(true);
        tblBang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblBang.setFocusable(true);
        tblBang.getAccessibleContext().setAccessibleName("Bảng dữ liệu");

        tblBang.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tblGiaTriTam,
                            Object giaTri,
                            boolean daChon,
                            boolean tieuDiem,
                            int dong,
                            int cot) {
                        super.getTableCellRendererComponent(
                                tblGiaTriTam, giaTri, daChon, false, dong, cot);
                        setBorder(new EmptyBorder(0, 10, 0, 10));
                        String vanBan = giaTri == null ? "" : giaTri.toString();
                        setBackground(
                                daChon
                                        ? tblGiaTriTam.getSelectionBackground()
                                        : (dong % 2 == 0
                                                ? MauUngDung.THE_NEN
                                                : MauUngDung.DONG_XEN_KE));
                        setForeground(MauUngDung.VAN_BAN_TOI);
                        setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
                        setHorizontalAlignment(SwingConstants.LEFT);
                        if (kiemTraCotTrangThai(tblGiaTriTam, cot)) {
                            setForeground(MauUngDung.layMauTongMau(vanBan));
                            setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
                        } else if (layCoDangDangSo(vanBan)) {
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                        String tenCot = tblGiaTriTam.getColumnName(cot).toLowerCase(java.util.Locale.ROOT);
                        if (giaTri instanceof java.sql.Timestamp t) {
                            vanBan = t.toLocalDateTime()
                                    .format(
                                            java.time.format.DateTimeFormatter.ofPattern(
                                                    "dd/MM/yyyy HH:mm"));
                        } else if (giaTri instanceof java.sql.Date d) {
                            vanBan = d.toLocalDate()
                                    .format(
                                            java.time.format.DateTimeFormatter.ofPattern(
                                                    "dd/MM/yyyy"));
                        } else if (laTien(tenCot)) {
                            if (giaTri instanceof Number n) {
                                vanBan = com.petstation.util.TienTe.dinhDang(Math.round(n.doubleValue()));
                            } else if (vanBan != null && !vanBan.isEmpty()) {
                                try {
                                    double dVal = Double.parseDouble(vanBan.replace(",", "").trim());
                                    vanBan = com.petstation.util.TienTe.dinhDang(Math.round(dVal));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else if (kiemTraCotTrangThai(tblGiaTriTam, cot)
                                || tenCot.contains("thanh toán")
                                || tenCot.contains("phương thức")) {
                            vanBan = tenTrangThai(vanBan);
                        }
                        setText(vanBan);
                        boolean biChe = false;
                        if (vanBan != null && !vanBan.isEmpty()) {
                            int rongCot = cot >= 0 && cot < tblGiaTriTam.getColumnModel().getColumnCount()
                                    ? tblGiaTriTam.getColumnModel().getColumn(cot).getWidth()
                                    : 0;
                            int rongChu = getFontMetrics(getFont()).stringWidth(vanBan) + 24;
                            biChe = (rongCot > 0 && rongChu > rongCot) || vanBan.length() > 20;
                        }
                        setToolTipText(biChe ? vanBan : null);
                        return this;
                    }
                });

        JTableHeader tieuDeBang = tblBang.getTableHeader();
        tieuDeBang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        tieuDeBang.setBackground(new Color(0xF3, 0xED, 0xE5));
        tieuDeBang.setForeground(MauUngDung.VAN_BAN_TOI);
        tieuDeBang.setPreferredSize(new Dimension(tieuDeBang.getWidth(), 38));
        tieuDeBang.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));
        tieuDeBang.setReorderingAllowed(false);
        tieuDeBang.setResizingAllowed(true);
        if (tieuDeBang.getClientProperty("hoverHeaderTooltip") == null) {
            tieuDeBang.putClientProperty("hoverHeaderTooltip", true);
            tieuDeBang.addMouseMotionListener(
                    new java.awt.event.MouseMotionAdapter() {
                        @Override
                        public void mouseMoved(java.awt.event.MouseEvent e) {
                            int cot = tieuDeBang.columnAtPoint(e.getPoint());
                            if (cot < 0 || cot >= tblBang.getColumnModel().getColumnCount()) {
                                tieuDeBang.setToolTipText(null);
                                return;
                            }
                            TableColumn tc = tblBang.getColumnModel().getColumn(cot);
                            String tieuDe = String.valueOf(tc.getHeaderValue());
                            int rongCot = tc.getWidth();
                            int rongChu = tieuDeBang
                                    .getFontMetrics(tieuDeBang.getFont())
                                    .stringWidth(tieuDe)
                                    + 20;
                            tieuDeBang.setToolTipText(rongChu > rongCot ? tieuDe : null);
                        }
                    });
        }

        tieuDeBang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (tieuDeBang.getResizingColumn() != null)
                    tblBang.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            }
        });

        canChinhDoRongCot(tblBang);

        if (tblBang.getClientProperty("modelResizeListener") == null
                && tblBang.getModel() != null) {
            tblBang.putClientProperty("modelResizeListener", true);
            tblBang.getModel()
                    .addTableModelListener(
                            e -> javax.swing.SwingUtilities.invokeLater(() -> canChinhDoRongCot(tblBang)));
        }

        Runnable resize = () -> {
            if (tblBang.getParent() instanceof javax.swing.JViewport vp) {
                int vpWidth = vp.getWidth();
                if (vpWidth <= 0)
                    return;
                canChinhDoRongCot(tblBang);
                tblBang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            }
        };

        if (tblBang.getClientProperty("resizeColumns") == null) {
            tblBang.putClientProperty("resizeColumns", true);
            tblBang.addHierarchyListener(
                    e -> {
                        if (tblBang.getParent() instanceof javax.swing.JViewport vp
                                && vp.getClientProperty(
                                        "resize:" + System.identityHashCode(tblBang)) == null) {
                            vp.putClientProperty(
                                    "resize:" + System.identityHashCode(tblBang), true);
                            vp.addComponentListener(
                                    new java.awt.event.ComponentAdapter() {
                                        @Override
                                        public void componentResized(
                                                java.awt.event.ComponentEvent event) {
                                            resize.run();
                                        }
                                    });
                            resize.run();
                        }
                    });
        }
        resize.run();
    }

    public static void canChinhDoRongCot(JTable tblBang) {
        if (tblBang == null || tblBang.getColumnCount() == 0)
            return;

        FontMetrics hfm =
                tblBang.getTableHeader() != null
                        ? tblBang.getTableHeader()
                                .getFontMetrics(tblBang.getTableHeader().getFont())
                        : tblBang.getFontMetrics(tblBang.getFont());
        FontMetrics cfm = tblBang.getFontMetrics(tblBang.getFont());
        javax.swing.table.TableModel model = tblBang.getModel();
        int soDongKiemTra = model == null ? 0 : Math.min(model.getRowCount(), 25);

        int tongToiThieu = 0;
        for (int chiSo = 0; chiSo < tblBang.getColumnCount(); chiSo++) {
            TableColumn cot = tblBang.getColumnModel().getColumn(chiSo);
            int modelCol = cot.getModelIndex();
            String tieuDe = String.valueOf(cot.getHeaderValue()).trim();
            String ten = tieuDe.toLowerCase(java.util.Locale.ROOT);
            int rongTieuDe = hfm != null ? hfm.stringWidth(tieuDe) + 22 : 90;

            int rongDuLieu = 0;
            for (int r = 0; r < soDongKiemTra; r++) {
                if (modelCol < 0 || modelCol >= model.getColumnCount()) break;
                Object val;
                try {
                    val = model.getValueAt(r, modelCol);
                } catch (RuntimeException ignored) {
                    break;
                }
                if (val == null) continue;
                String s = val.toString();
                if (val instanceof java.sql.Timestamp t) {
                    s = t.toLocalDateTime()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                } else if (val instanceof java.sql.Date d) {
                    s = d.toLocalDate()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } else if (laTien(ten) && val instanceof Number n) {
                    s = com.petstation.util.TienTe.dinhDang(Math.round(n.doubleValue()));
                } else if (kiemTraCotTrangThai(tblBang, chiSo)
                        || ten.contains("thanh toán")
                        || ten.contains("phương thức")) {
                    s = tenTrangThai(s);
                }
                if (cfm != null && !s.startsWith("<html>")) {
                    rongDuLieu = Math.max(rongDuLieu, cfm.stringWidth(s) + 22);
                }
            }

            boolean laCotVanBanDai =
                    ten.contains("tên")
                            || ten.contains("sản phẩm")
                            || ten.contains("hàng hóa")
                            || ten.contains("khách hàng")
                            || ten.contains("chủ nuôi")
                            || ten.contains("dịch vụ")
                            || ten.contains("nhân viên")
                            || ten.contains("thu ngân")
                            || ten.contains("người")
                            || ten.contains("nhà cung cấp")
                            || ten.contains("ghi chú")
                            || ten.contains("mô tả")
                            || ten.contains("lý do")
                            || ten.contains("nội dung")
                            || ten.contains("hành động")
                            || ten.contains("địa chỉ")
                            || ten.contains("bảng giá")
                            || ten.contains("điều khoản")
                            || ten.contains("chương trình");

            int canThiet = Math.max(rongTieuDe, rongDuLieu);
            int toiThieu;
            int uuTien;
            if (!laCotVanBanDai && canThiet <= 165) {
                toiThieu = canThiet;
                uuTien = canThiet + 6;
            } else {
                toiThieu = Math.max(85, Math.min(130, rongTieuDe));
                uuTien = Math.max(toiThieu + 40, Math.min(310, Math.max(rongTieuDe, rongDuLieu)));
            }

            cot.setMinWidth(toiThieu);
            cot.setMaxWidth(Integer.MAX_VALUE);
            cot.setResizable(true);
            cot.setPreferredWidth(uuTien);
            tongToiThieu += toiThieu;
        }

        int rongKhung =
                (tblBang.getParent() != null && tblBang.getParent().getWidth() > 200)
                        ? tblBang.getParent().getWidth()
                        : tblBang.getWidth();
        if (rongKhung > 200 && tongToiThieu > rongKhung - 6) {
            double tiLe = (double) (rongKhung - 6) / tongToiThieu;
            for (int chiSo = 0; chiSo < tblBang.getColumnCount(); chiSo++) {
                TableColumn cot = tblBang.getColumnModel().getColumn(chiSo);
                cot.setMinWidth(Math.max(48, (int) Math.floor(cot.getMinWidth() * tiLe)));
            }
        }
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

    public static String tenTrangThai(String s) {
        if (s == null)
            return "";
        String trimmed = s.trim();
        String normalized = trimmed.toUpperCase().replace(" ", "");
        if (normalized.equals("DOI_HANG:HOAN_TAT")) {
            return "Hoàn tất đổi hàng";
        }
        if (normalized.equals("DOI_HANG:CHO_XU_LY")) {
            return "Chờ xử lý đổi hàng";
        }
        if (normalized.equals("DOI_HANG:DA_HUY")) {
            return "Đã hủy đổi hàng";
        }
        if (normalized.startsWith("TRA_HANG_CU")) {
            return "Lịch sử hoàn tiền";
        }
        if (trimmed.contains(":")) {
            String[] parts = trimmed.split(":", 2);
            return tenTrangThai(parts[0]) + ": " + tenTrangThai(parts[1]);
        }
        return switch (trimmed.toUpperCase()) {
            case "DA_THANH_TOAN" -> "Đã thanh toán";
            case "CHO_THANH_TOAN" -> "Chờ thanh toán";
            case "GIO_TREO" -> "Giỏ treo";
            case "DA_XAC_NHAN", "DA_HEN" -> "Đã xác nhận";
            case "TIEP_NHAN", "DA_TIEP_NHAN" -> "Tiếp nhận";
            case "DANG_THUC_HIEN" -> "Đang thực hiện";
            case "HOAN_TAT", "HOAN_THANH" -> "Hoàn tất";
            case "DA_HUY" -> "Đã hủy";
            case "KHONG_DEN" -> "Không đến";
            case "DA_DAT" -> "Đã đặt";
            case "DANG_O", "DA_NHAN" -> "Đang ở";
            case "DA_TRA_PHONG" -> "Đã trả phòng";
            case "TIEN_MAT" -> "Tiền mặt";
            case "CHUYEN_KHOAN" -> "Chuyển khoản";
            case "THE_NGAN_HANG", "THE", "QUET_THE" -> "Thẻ";
            case "DIEM_TICH_LUY" -> "Điểm tích lũy";
            case "KET_HOP" -> "Kết hợp";
            case "TRONG", "KHA_DUNG" -> "Trống";
            case "CO_THU" -> "Có thú";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI" -> "Bảo trì";
            case "HOAT_DONG", "DANG_HOAT_DONG" -> "Đang hoạt động";
            case "TAM_KHOA" -> "Tạm khóa";
            case "NGUNG_HOAT_DONG" -> "Ngừng hoạt động";
            case "DANG_BAN" -> "Đang kinh doanh";
            case "NGUNG_BAN" -> "Ngừng kinh doanh";
            case "SAN_SANG" -> "Sẵn sàng";
            case "CHO_XUAT" -> "Chờ xuất";
            case "DA_XUAT" -> "Đã xuất";
            case "CHO_NHAP" -> "Chờ nhập";
            case "DA_NHAP" -> "Đã nhập";
            case "XUAT_HUY" -> "Xuất hủy";
            case "DANG_KIEM" -> "Đang kiểm kê";
            case "DA_KIEM" -> "Đã kiểm kê";
            case "DA_DOI_SOAT" -> "Đã đối soát";
            case "HET_HANG" -> "Hết hàng";
            case "CON_HANG" -> "Còn hàng";
            case "DA_CAP_NHAT" -> "Đã cập nhật";
            case "CHO_DUYET" -> "Chờ duyệt";
            case "DA_DUYET" -> "Đã duyệt";
            case "TU_CHOI" -> "Từ chối";
            case "CAN_THEM_THONG_TIN" -> "Cần thêm thông tin";
            case "CHO_XU_LY" -> "Chờ xử lý";
            case "DANG_XU_LY" -> "Đang xử lý";
            case "DANG_MO" -> "Đang mở";
            case "DA_KHOA" -> "Đã khóa";
            case "DA_BAN_GIAO" -> "Đã bàn giao";
            case "CON_HAN" -> "Còn hạn";
            case "HET_HAN" -> "Hết hạn";
            case "DA_DUNG" -> "Đã dùng";
            case "DANG_HOP_TAC" -> "Đang hợp tác";
            case "NGUNG_HOP_TAC" -> "Ngừng hợp tác";
            case "DOI_HANG" -> "Đổi hàng";
            case "TRA_HANG", "DA_HOAN" -> "Trả hàng";
            case "DI_LAM" -> "Đi làm";
            case "NGHI_PHEP" -> "Nghỉ phép";
            case "DA_GHI_SO" -> "Đã ghi sổ";
            default -> s;
        };
    }

    private static boolean kiemTraCotTrangThai(JTable tblBang, int cot) {
        String ten = String.valueOf(tblBang.getColumnName(cot)).toLowerCase();
        return ten.contains("trạng thái") || ten.equals("tiến độ") || ten.contains("hạng thẻ");
    }

    private static boolean layCoDangDangSo(String vanBan) {
        if (vanBan.isEmpty() || vanBan.length() > 20)
            return false;
        String giaTriTam = vanBan.replace("₫", "")
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
}
