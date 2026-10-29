package com.petstation.gui.components.common;

import com.petstation.gui.components.MauUngDung;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * Cung cấp giao diện và xử lý tương tác cho bảng định dạng.
 *
 * <p>Kiểu bảng dùng chung cho mọi màn danh sách: dòng chọn màu hồng nhạt (không dùng xanh dương mặc
 * định), dòng xen kẽ, chữ số căn phải, cột trạng thái tô màu, tiêu đề đồng nhất.
 * Cột ngắn có độ rộng giới hạn, cột dài (tên, mô tả, địa chỉ) tự động dãn để không che dữ liệu.
 */
public final class DinhDangBang {
    private DinhDangBang() {}

    public static void apDung(JTable tblBang) {
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
                        String tenCot =
                                tblGiaTriTam.getColumnName(cot).toLowerCase(java.util.Locale.ROOT);
                        if (giaTri instanceof java.sql.Timestamp t) {
                            vanBan =
                                    t.toLocalDateTime()
                                            .format(
                                                    java.time.format.DateTimeFormatter.ofPattern(
                                                            "dd/MM/yyyy HH:mm"));
                        } else if (giaTri instanceof java.sql.Date d) {
                            vanBan =
                                    d.toLocalDate()
                                            .format(
                                                    java.time.format.DateTimeFormatter.ofPattern(
                                                            "dd/MM/yyyy"));
                        } else if (giaTri instanceof Number n && laTien(tenCot)) {
                            vanBan = com.petstation.util.TienTe.dinhDang(n.longValue());
                        } else if (kiemTraCotTrangThai(tblGiaTriTam, cot)
                                || tenCot.contains("thanh toán")
                                || tenCot.contains("phương thức")) {
                            vanBan = tenTrangThai(vanBan);
                        }
                        setText(vanBan);
                        setToolTipText(vanBan != null && vanBan.length() > 20 ? vanBan : null);
                        return this;
                    }
                });

        // Tiêu đề bảng
        JTableHeader tieuDeBang = tblBang.getTableHeader();
        tieuDeBang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        tieuDeBang.setBackground(new Color(0xF3, 0xED, 0xE5));
        tieuDeBang.setForeground(MauUngDung.VAN_BAN_TOI);
        tieuDeBang.setPreferredSize(new Dimension(tieuDeBang.getWidth(), 38));
        tieuDeBang.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));
        tieuDeBang.setReorderingAllowed(false);

        // Định dạng bề rộng các cột thông minh
        canChinhDoRongCot(tblBang);

        Runnable resize =
                () -> {
                    if (tblBang.getParent() instanceof javax.swing.JViewport vp) {
                        int vpWidth = vp.getWidth();
                        if (vpWidth <= 0) return;
                        int tongUuTien = 0;
                        for (int i = 0; i < tblBang.getColumnCount(); i++) {
                            tongUuTien += tblBang.getColumnModel().getColumn(i).getPreferredWidth();
                        }
                        if (vpWidth >= tongUuTien) {
                            tblBang.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                        } else {
                            tblBang.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        }
                    }
                };

        if (tblBang.getClientProperty("resizeColumns") == null) {
            tblBang.putClientProperty("resizeColumns", true);
            tblBang.addHierarchyListener(
                    e -> {
                        if (tblBang.getParent() instanceof javax.swing.JViewport vp
                                && vp.getClientProperty(
                                                "resize:" + System.identityHashCode(tblBang))
                                         == null) {
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

    /**
     * Căn chỉnh độ rộng các cột dựa trên tên và kiểu dữ liệu:
     * - Cột dữ liệu ngắn (STT, Mã, SĐT, Giới tính, ĐVT, Ngày, Trạng thái, Giá): giới hạn maxWidth
     *   để không bị dãn quá rộng vô lý.
     * - Cột dữ liệu dài (Tên sản phẩm, Tên khách, Địa chỉ, Mô tả, Ghi chú): không đặt maxWidth,
     *   tự do dãn chiếm toàn bộ không gian còn lại để không bị cắt chữ (...).
     */
    public static void canChinhDoRongCot(JTable tblBang) {
        if (tblBang == null || tblBang.getColumnCount() == 0) return;

        for (int chiSo = 0; chiSo < tblBang.getColumnCount(); chiSo++) {
            TableColumn cot = tblBang.getColumnModel().getColumn(chiSo);
            String ten = String.valueOf(cot.getHeaderValue()).trim().toLowerCase(java.util.Locale.ROOT);

            if (ten.equals("stt") || ten.equals("#")) {
                cot.setMinWidth(45);
                cot.setMaxWidth(65);
                cot.setPreferredWidth(55);
            } else if (ten.equals("mã")
                    || ten.startsWith("mã ")
                    || ten.endsWith(" mã")
                    || ten.equals("id")
                    || ten.equals("code")
                    || ten.equals("sku")
                    || ten.equals("barcode")) {
                cot.setMinWidth(80);
                cot.setMaxWidth(130);
                cot.setPreferredWidth(105);
            } else if (ten.contains("điện thoại") || ten.contains("sđt") || ten.equals("phone")) {
                cot.setMinWidth(100);
                cot.setMaxWidth(135);
                cot.setPreferredWidth(120);
            } else if (ten.equals("giới tính")) {
                cot.setMinWidth(65);
                cot.setMaxWidth(95);
                cot.setPreferredWidth(75);
            } else if (ten.equals("hạng") || ten.contains("hạng thẻ") || ten.equals("loại")) {
                cot.setMinWidth(75);
                cot.setMaxWidth(115);
                cot.setPreferredWidth(90);
            } else if (ten.equals("đvt") || ten.contains("đơn vị tính") || ten.equals("đơn vị")) {
                cot.setMinWidth(65);
                cot.setMaxWidth(95);
                cot.setPreferredWidth(80);
            } else if (ten.equals("số lượng") || ten.equals("sl") || ten.contains("cân nặng")) {
                cot.setMinWidth(70);
                cot.setMaxWidth(110);
                cot.setPreferredWidth(85);
            } else if (ten.contains("thời gian")
                    || ten.contains("ngày")
                    || ten.contains("lúc")
                    || ten.contains("hạn dùng")
                    || ten.contains("bắt đầu")
                    || ten.contains("kết thúc")) {
                cot.setMinWidth(100);
                cot.setMaxWidth(145);
                cot.setPreferredWidth(125);
            } else if (ten.contains("trạng thái") || ten.equals("tiến độ")) {
                cot.setMinWidth(110);
                cot.setMaxWidth(155);
                cot.setPreferredWidth(135);
            } else if (laTien(ten)) {
                cot.setMinWidth(100);
                cot.setMaxWidth(150);
                cot.setPreferredWidth(125);
            } else if (ten.contains("phương thức") || ten.contains("hình thức")) {
                cot.setMinWidth(100);
                cot.setMaxWidth(150);
                cot.setPreferredWidth(125);
            } else if (ten.contains("email")) {
                cot.setMinWidth(130);
                cot.setPreferredWidth(180);
            } else if (ten.contains("địa chỉ")) {
                cot.setMinWidth(160);
                cot.setPreferredWidth(280);
            } else if (ten.contains("ghi chú")
                    || ten.contains("mô tả")
                    || ten.contains("lý do")
                    || ten.contains("nội dung")
                    || ten.contains("tính trạng")
                    || ten.contains("dị ứng")
                    || ten.contains("tính cách")) {
                cot.setMinWidth(160);
                cot.setPreferredWidth(280);
            } else if (ten.contains("tên")
                    || ten.contains("sản phẩm")
                    || ten.contains("hàng hóa")
                    || ten.contains("khách hàng")
                    || ten.contains("chủ nuôi")
                    || ten.contains("dịch vụ")
                    || ten.contains("nhân viên")) {
                cot.setMinWidth(160);
                cot.setPreferredWidth(260);
            } else {
                cot.setMinWidth(90);
                cot.setPreferredWidth(150);
            }
        }
    }

    private static boolean laTien(String ten) {
        return ten.matches(
                ".*(tiền|giá|doanh thu|chi tiêu|cọc|nợ|tổng nhập|đã trả|thực đếm|chênh lệch|thu hóa"
                        + " đơn).*");
    }

    public static String tenTrangThai(String s) {
        return switch (s) {
            case "PAID" -> "Đã thanh toán";
            case "SCHEDULED" -> "Đã hẹn";
            case "RECEIVED" -> "Đã tiếp nhận";
            case "IN_PROGRESS" -> "Đang thực hiện";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            case "NO_SHOW" -> "Không đến";
            case "BOOKED" -> "Đã đặt";
            case "CHECKED_IN" -> "Đã nhận";
            case "CHECKED_OUT" -> "Đã trả phòng";
            case "CASH" -> "Tiền mặt";
            case "TRANSFER" -> "Chuyển khoản / QR";
            case "CARD" -> "Thẻ";
            case "MIXED" -> "Kết hợp";
            case "AVAILABLE" -> "Khả dụng";
            case "OCCUPIED" -> "Có thú";
            case "CLEANING" -> "Đang dọn";
            case "MAINTENANCE" -> "Bảo trì";
            case "ACTIVE" -> "Đang hoạt động";
            case "INACTIVE" -> "Ngừng hoạt động";
            case "SELLING" -> "Đang bán";
            case "OPEN" -> "Đang mở";
            case "LOCKED" -> "Đã khóa";
            case "PENDING" -> "Chờ xử lý";
            case "POSTED" -> "Đã ghi sổ";
            default -> s;
        };
    }

    private static boolean kiemTraCotTrangThai(JTable tblBang, int cot) {
        String ten = String.valueOf(tblBang.getColumnName(cot)).toLowerCase();
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
                        .trim();
        return !giaTriTam.isEmpty() && giaTriTam.chars().allMatch(Character::isDigit);
    }
}
