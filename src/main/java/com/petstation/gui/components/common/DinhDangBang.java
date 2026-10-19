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
    /** Khởi tạo DinhDangBang với các giá trị được cung cấp. */
    private DinhDangBang() {}

    /**
     * Áp dụng trong DinhDangBang.
     *
     * @param tblBang bảng
     */
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
                        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (kiemTraCotTrangThai(tblGiaTriTam, cot)) {
                            setForeground(MauUngDung.layMauTongMau(vanBan));
                            setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
                        } else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (layCoDangDangSo(vanBan)) {
                            setHorizontalAlignment(SwingConstants.RIGHT);
                        }
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
            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.startsWith("mã") || ten.equals("stt") || ten.equals("hạng"))
                cot.setPreferredWidth(110);
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.contains("nội dung") || ten.contains("tên") || ten.contains("sản phẩm"))
                cot.setPreferredWidth(200);
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.contains("chủ nuôi") || ten.contains("địa chỉ") || ten.contains("nhân viên"))
                cot.setPreferredWidth(180);
            else // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
            if (ten.contains("trạng thái")) cot.setPreferredWidth(120);
        }
    }

    /**
     * Kiểm tra cột trạng thái trong DinhDangBang.
     *
     * @param tblBang bảng
     * @param cot cột
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private static boolean kiemTraCotTrangThai(JTable tblBang, int cot) {
        // Tên.
        String ten = String.valueOf(tblBang.getColumnName(cot)).toLowerCase();
        return ten.contains("trạng thái") || ten.equals("tiến độ") || ten.contains("hạng thẻ");
    }

    /**
     * Lấy có dạng dạng số trong DinhDangBang.
     *
     * @param vanBan văn bản
     * @return kết quả kiểm tra điều kiện xử lý
     */
    private static boolean layCoDangDangSo(String vanBan) {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (vanBan.isEmpty() || vanBan.length() > 20) return false;
        // Giá trị tạm.
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
