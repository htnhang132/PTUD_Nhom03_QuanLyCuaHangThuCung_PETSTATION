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
                    /**
                     * Lấy bảng ô bộ hiển thị thành phần trong lớp xử lý giao diện.
                     *
                     * @param tblGiaTriTam giá trị tạm
                     * @param giaTri giá trị
                     * @param daChon đã chọn
                     * @param tieuDiem tiêu điểm
                     * @param dong dòng
                     * @param cot cột
                     * @return kết quả lấy bảng ô bộ hiển thị thành phần
                     */
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
                        // Văn bản.
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
                        } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (layCoDangDangSo(vanBan)) {
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        }
                        String tenCot =
                                tblGiaTriTam.getColumnName(cot).toLowerCase(java.util.Locale.ROOT);
                        if (giaTri instanceof java.sql.Timestamp t)
                            vanBan =
                                    t.toLocalDateTime()
                                            .format(
                                                    java.time.format.DateTimeFormatter.ofPattern(
                                                            "dd/MM/yyyy HH:mm"));
                        else if (giaTri instanceof java.sql.Date d)
                            vanBan =
                                    d.toLocalDate()
                                            .format(
                                                    java.time.format.DateTimeFormatter.ofPattern(
                                                            "dd/MM/yyyy"));
                        else if (giaTri instanceof Number n && laTien(tenCot))
                            vanBan = com.petstation.util.TienTe.dinhDang(n.longValue());
                        else if (kiemTraCotTrangThai(tblGiaTriTam, cot)
                                || tenCot.contains("thanh toán")
                                || tenCot.contains("phương thức")) vanBan = tenTrangThai(vanBan);
                        setText(vanBan);
                        setToolTipText(vanBan);
                        return this;
                    }
                });

        // Tiêu đề bảng.
        JTableHeader tieuDeBang = tblBang.getTableHeader();
        tieuDeBang.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
        tieuDeBang.setBackground(new Color(0xF3, 0xED, 0xE5));
        tieuDeBang.setForeground(MauUngDung.VAN_BAN_TOI);
        tieuDeBang.setPreferredSize(new Dimension(tieuDeBang.getWidth(), 38));
        tieuDeBang.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MauUngDung.VIEN));
        tieuDeBang.setReorderingAllowed(false);

        for (int chiSo = 0; chiSo < tblBang.getColumnCount(); chiSo++) {
            // Cột.
            TableColumn cot = tblBang.getColumnModel().getColumn(chiSo);
            // Tên.
            String ten = String.valueOf(cot.getHeaderValue()).toLowerCase();
            if (ten.startsWith("mã") || ten.equals("stt") || ten.equals("hạng"))
                cot.setPreferredWidth(110);
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.contains("nội dung") || ten.contains("tên") || ten.contains("sản phẩm"))
                cot.setPreferredWidth(200);
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.contains("chủ nuôi") || ten.contains("địa chỉ") || ten.contains("nhân viên"))
                cot.setPreferredWidth(180);
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.contains("trạng thái")) cot.setPreferredWidth(145);
            else if (laTien(ten)) cot.setPreferredWidth(145);
            else if (ten.contains("điện thoại")) cot.setPreferredWidth(135);
            else if (ten.contains("email")) cot.setPreferredWidth(190);
            else if (ten.contains("thời gian")
                    || ten.contains("ngày")
                    || ten.contains("lúc")
                    || ten.contains("bắt đầu")
                    || ten.contains("kết thúc")) cot.setPreferredWidth(155);
            cot.setMinWidth(Math.min(cot.getPreferredWidth(), 95));
        }
        Runnable resize =
                () -> {
                    if (tblBang.getParent() instanceof javax.swing.JViewport vp) {
                        int min = 0;
                        for (int i = 0; i < tblBang.getColumnCount(); i++)
                            min += tblBang.getColumnModel().getColumn(i).getMinWidth();
                        tblBang.setAutoResizeMode(
                                vp.getWidth() >= min
                                        ? JTable.AUTO_RESIZE_ALL_COLUMNS
                                        : JTable.AUTO_RESIZE_OFF);
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
        // Tên.
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
