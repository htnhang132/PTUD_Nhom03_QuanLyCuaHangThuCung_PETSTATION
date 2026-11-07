package com.petstation.gui.components;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class HopThoaiTroGiup {
    private HopThoaiTroGiup() {}

    public record NoiDungHuongDan(
            String tieuDe,
            String mucDich,
            List<String> cacBuoc,
            List<String> luuY,
            List<String[]> phimTat) {}

    public static void hienThi(Component cha, String tieuDeGoc, String noiDungGoc) {
        NoiDungHuongDan hd = xayDungNoiDung(cha, tieuDeGoc, noiDungGoc);
        JPanel pnlChinh = taoGiaoDien(hd);

        Window win = cha == null ? null : SwingUtilities.getWindowAncestor(cha);
        JDialog dialog =
                new JDialog(
                        win,
                        "Hướng dẫn sử dụng — " + hd.tieuDe(),
                        Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel pnlBao = new JPanel(new BorderLayout(0, 12));
        pnlBao.setBackground(MauUngDung.NEN_KEM);
        pnlBao.setBorder(new EmptyBorder(16, 18, 14, 18));
        pnlBao.add(pnlChinh, BorderLayout.CENTER);

        JPanel pnlChan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlChan.setOpaque(false);
        NutDinhDang btnDong = new NutDinhDang("Đã hiểu (Esc)", NutDinhDang.KieuNut.CHINH);
        btnDong.setPreferredSize(new Dimension(130, 34));
        btnDong.addActionListener(e -> dialog.dispose());
        pnlChan.add(btnDong);
        pnlBao.add(pnlChan, BorderLayout.SOUTH);

        dialog.getRootPane()
                .registerKeyboardAction(
                        e -> dialog.dispose(),
                        KeyStroke.getKeyStroke("ESCAPE"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.getRootPane().setDefaultButton(btnDong);
        dialog.setContentPane(pnlBao);
        dialog.setSize(680, 540);
        dialog.setLocationRelativeTo(win);
        dialog.setVisible(true);
    }

    public static JPanel taoGiaoDien(NoiDungHuongDan hd) {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        BoGocPanel pnlDau = new BoGocPanel(12, Color.WHITE, true);
        pnlDau.setLayout(new BorderLayout(12, 4));
        pnlDau.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel lblBadge = new JLabel("HƯỚNG DẪN NHANH", SwingConstants.CENTER);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(MauUngDung.DO_NHAT);
        lblBadge.setForeground(MauUngDung.DO_THUONG_HIEU);
        lblBadge.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
        lblBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JPanel pnlTieuDeDau = new JPanel(new BorderLayout(8, 2));
        pnlTieuDeDau.setOpaque(false);
        JLabel lblTen = new JLabel(hd.tieuDe());
        lblTen.setFont(MauUngDung.taoPhongChu(Font.BOLD, 17));
        lblTen.setForeground(MauUngDung.VAN_BAN_TOI);
        pnlTieuDeDau.add(lblTen, BorderLayout.WEST);
        pnlTieuDeDau.add(lblBadge, BorderLayout.EAST);

        JLabel lblMucDich =
                new JLabel("<html><body style='width:420px'>" + hd.mucDich() + "</body></html>");
        lblMucDich.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
        lblMucDich.setForeground(MauUngDung.VAN_BAN_XAM);

        pnlDau.add(pnlTieuDeDau, BorderLayout.NORTH);
        pnlDau.add(lblMucDich, BorderLayout.CENTER);
        root.add(pnlDau, BorderLayout.NORTH);

        JPanel pnlThan = new JPanel();
        pnlThan.setLayout(new BoxLayout(pnlThan, BoxLayout.Y_AXIS));
        pnlThan.setOpaque(false);

        pnlThan.add(taoTheMuc("1. Các bước thao tác chính", hd.cacBuoc(), true));
        pnlThan.add(Box.createVerticalStrut(10));
        pnlThan.add(taoTheMuc("2. Quy tắc nghiệp vụ & Lưu ý", hd.luuY(), false));
        pnlThan.add(Box.createVerticalStrut(10));
        pnlThan.add(taoThePhimTat("3. Phím tắt & Mẹo tra cứu", hd.phimTat()));

        JScrollPane scr =
                new JScrollPane(
                        pnlThan,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(null);
        scr.setOpaque(false);
        scr.getViewport().setOpaque(false);
        scr.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scr, BorderLayout.CENTER);
        return root;
    }

    private static BoGocPanel taoTheMuc(String tieuDe, List<String> dong, boolean danhSo) {
        BoGocPanel the = new BoGocPanel(12, Color.WHITE, true);
        the.setLayout(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblTieuDe.setForeground(MauUngDung.NAU);
        the.add(lblTieuDe, BorderLayout.NORTH);

        JPanel pnlDanhSach = new JPanel(new GridLayout(0, 1, 0, 6));
        pnlDanhSach.setOpaque(false);
        for (int i = 0; i < dong.size(); i++) {
            JPanel hang = new JPanel(new BorderLayout(10, 0));
            hang.setOpaque(false);
            JLabel icon = new JLabel(danhSo ? "Bước " + (i + 1) : "•");
            icon.setFont(MauUngDung.taoPhongChu(Font.BOLD, 12));
            icon.setForeground(danhSo ? MauUngDung.DO_THUONG_HIEU : MauUngDung.NAU);
            if (danhSo) {
                icon.setPreferredSize(new Dimension(56, 22));
            } else {
                icon.setPreferredSize(new Dimension(16, 22));
            }
            JLabel txt = new JLabel("<html><body style='width:370px'>" + dong.get(i) + "</body></html>");
            txt.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 13));
            txt.setForeground(MauUngDung.VAN_BAN_TOI);
            hang.add(icon, BorderLayout.WEST);
            hang.add(txt, BorderLayout.CENTER);
            pnlDanhSach.add(hang);
        }
        the.add(pnlDanhSach, BorderLayout.CENTER);
        return the;
    }

    private static BoGocPanel taoThePhimTat(String tieuDe, List<String[]> phimTat) {
        BoGocPanel the = new BoGocPanel(12, Color.WHITE, true);
        the.setLayout(new BorderLayout(0, 8));
        the.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(MauUngDung.taoPhongChu(Font.BOLD, 14));
        lblTieuDe.setForeground(MauUngDung.NAU);
        the.add(lblTieuDe, BorderLayout.NORTH);

        JPanel luoi = new JPanel(new GridLayout(0, 2, 12, 6));
        luoi.setOpaque(false);
        for (String[] cap : phimTat) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            item.setOpaque(false);
            JLabel phim = new JLabel(cap[0]);
            phim.setOpaque(true);
            phim.setBackground(new Color(241, 245, 249));
            phim.setForeground(MauUngDung.VAN_BAN_TOI);
            phim.setFont(MauUngDung.taoPhongChu(Font.BOLD, 11));
            phim.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(MauUngDung.VIEN),
                            new EmptyBorder(2, 7, 2, 7)));
            JLabel moTa = new JLabel(cap[1]);
            moTa.setFont(MauUngDung.taoPhongChu(Font.PLAIN, 12));
            moTa.setForeground(MauUngDung.VAN_BAN_XAM);
            item.add(phim);
            item.add(moTa);
            luoi.add(item);
        }
        the.add(luoi, BorderLayout.CENTER);
        return the;
    }

    public static NoiDungHuongDan xayDungNoiDung(Component cha, String tieuDeGoc, String noiDungGoc) {
        String lop = cha == null ? "" : cha.getClass().getSimpleName();
        String tieuDe = (tieuDeGoc == null || tieuDeGoc.isBlank() || "Trợ giúp".equals(tieuDeGoc))
                ? tenManHinhTuLop(lop)
                : tieuDeGoc;

        if (lop.contains("LapHoaDonPanel")) {
            return new NoiDungHuongDan(
                    "Lập hóa đơn bán hàng (POS)",
                    "Màn hình dành cho thu ngân tạo đơn hàng nhanh bằng máy quét mã vạch, tìm kiếm từ khóa hoặc chọn sản phẩm trực quan.",
                    List.of(
                            "<b>Thêm sản phẩm:</b> Quét mã vạch (Barcode), gõ mã/tên sản phẩm vào ô tìm kiếm rồi nhấn <b>Enter</b>, hoặc bấm trực tiếp vào sản phẩm.",
                            "<b>Chuyển chế độ hiển thị:</b> Chọn <b>Bảng nhanh (POS)</b> khi cửa hàng có hàng nghìn sản phẩm để tra cứu tức thì không tốn RAM ảnh, hoặc chọn <b>Thẻ hình ảnh</b> (tối đa 24 SP/trang).",
                            "<b>Chọn khách & Khuyến mãi:</b> Nhập SĐT khách hàng ở khung bên phải rồi nhấn <b>Enter</b> để tích điểm và tự động áp dụng khuyến mãi.",
                            "<b>Hoàn tất:</b> Bấm <b>Thanh toán (F9)</b> để chuyển sang thu tiền, hoặc <b>Lưu đơn nháp (F7)</b> nếu khách cần chờ."
                    ),
                    List.of(
                            "Cần <b>Mở ca thu ngân</b> trước khi lập hóa đơn và thanh toán.",
                            "Chế độ thẻ tự động phân trang (24 SP/trang) và dùng bộ nhớ đệm ảnh để hệ thống luôn mượt dù có 10.000+ sản phẩm.",
                            "Sản phẩm hết hàng (Tồn = 0) sẽ được khóa chọn để tránh bán âm kho."
                    ),
                    List.of(
                            new String[] {"Ctrl + K", "Đặt con trỏ vào ô quét mã/tìm kiếm"},
                            new String[] {"F7 / F8", "Lưu đơn nháp / Mở danh sách đơn nháp"},
                            new String[] {"F9", "Chuyển nhanh sang màn hình Thanh toán"},
                            new String[] {"Click đúp", "Thêm nhanh sản phẩm ở chế độ Bảng POS"}
                    ));
        }

        if (lop.contains("ThanhToanPanel") || lop.contains("CaThuNganPanel")) {
            return new NoiDungHuongDan(
                    tieuDe,
                    "Quản lý ca thu ngân, kiểm soát tiền mặt đầu ca/cuối ca và thực hiện thu tiền hóa đơn.",
                    List.of(
                            "<b>Mở ca:</b> Nhập số tiền mặt đầu ca và bấm <b>Mở ca làm việc</b> trước khi bán hàng.",
                            "<b>Thanh toán:</b> Chọn phương thức (Tiền mặt, Chuyển khoản, Thẻ, hoặc Kết hợp), áp dụng Voucher/Điểm thưởng nếu có rồi bấm <b>Xác nhận thanh toán</b>.",
                            "<b>Kết ca & Bàn giao:</b> Cuối ca nhập tiền mặt thực đếm để hệ thống đối chiếu chênh lệch và khóa sổ ca."
                    ),
                    List.of(
                            "Chỉ phương thức <b>Tiền mặt</b> mới tính tiền thừa trả khách và cộng vào két tiền mặt cuối ca.",
                            "Nếu chưa mở ca mà bấm Lập hóa đơn, hệ thống sẽ tự chuyển sang Mở ca và tự động quay lại Lập hóa đơn ngay sau khi mở xong."
                    ),
                    List.of(
                            new String[] {"F1", "Xem hướng dẫn màn hình"},
                            new String[] {"Enter", "Xác nhận thao tác đang chọn"}
                    ));
        }

        if (lop.contains("DanhMuc") || lop.contains("DanhSachSanPham") || cha instanceof QuanLyDanhMucPanel) {
            return new NoiDungHuongDan(
                    tieuDe,
                    "Quản lý thông tin danh mục nền tảng của cửa hàng: tra cứu, lọc theo tiêu chí, thêm mới, cập nhật và ngừng hoạt động.",
                    List.of(
                            "<b>Tìm kiếm & Lọc:</b> Gõ từ khóa vào ô tìm kiếm bên trái hoặc chọn các hộp lọc (Nhóm, Trạng thái...) để thu hẹp danh sách.",
                            "<b>Thêm mới:</b> Bấm nút <b>+ Thêm</b> màu đỏ, điền các trường có dấu sao <b>(*)</b> ở biểu mẫu bên phải rồi bấm <b>Lưu</b>.",
                            "<b>Chỉnh sửa:</b> Chọn một dòng trong bảng rồi bấm <b>Sửa</b> (hoặc <b>nhấp đúp chuột</b> vào dòng đó), chỉnh thông tin và bấm <b>Lưu</b>.",
                            "<b>Ngừng hoạt động (Xóa mềm):</b> Chọn dòng cần ngừng và bấm <b>Xóa</b> để chuyển trạng thái mà không làm mất lịch sử giao dịch."
                    ),
                    List.of(
                            "Các trường đánh dấu <b>*</b> là bắt buộc; SĐT (10 số), CCCD (12 số), Barcode (13 số EAN-13) được kiểm tra tự động.",
                            "Bản ghi đã phát sinh hóa đơn/phiếu dịch vụ sẽ được <b>xóa mềm</b> (chuyển sang Ngừng hoạt động/Ngừng kinh doanh) để bảo toàn sổ sách.",
                            "Tại Danh mục Dịch vụ Spa, nhớ thêm ít nhất 1 mức giá theo loài và khoảng cân nặng trước khi lưu."
                    ),
                    List.of(
                            new String[] {"Ctrl + F", "Nhảy nhanh vào ô tìm kiếm"},
                            new String[] {"Click đúp dòng", "Bật ngay chế độ Sửa bản ghi"},
                            new String[] {"Rê chuột vào ô", "Xem đầy đủ chữ bị rút gọn"},
                            new String[] {"Xuất Excel", "Tải danh sách đang lọc ra file .xlsx"}
                    ));
        }

        if (lop.contains("Spa") || lop.contains("LichHen")) {
            return new NoiDungHuongDan(
                    tieuDe,
                    "Quản lý quy trình dịch vụ Grooming & Spa cho thú cưng từ lúc đặt lịch, tiếp nhận cân nặng đến khi hoàn tất và thanh toán.",
                    List.of(
                            "<b>Bước 1 - Đặt lịch / Tiếp nhận:</b> Chọn khách hàng, thú cưng, gói dịch vụ Spa, nhân viên phụ trách và ghi nhận cân nặng thực tế.",
                            "<b>Bước 2 - Thực hiện & Cập nhật tiến độ:</b> Chọn phiếu trong danh sách và chuyển trạng thái <b>Đang thực hiện → Hoàn tất</b>.",
                            "<b>Bước 3 - Chuyển thanh toán:</b> Sau khi hoàn tất dịch vụ, bấm chuyển phiếu sang giỏ hàng POS để thu ngân thanh toán."
                    ),
                    List.of(
                            "Giá dịch vụ Spa được tính tự động dựa trên <b>Loài (Chó/Mèo)</b> và <b>Cân nặng thực tế</b> của thú cưng khi tiếp nhận.",
                            "Có thể chuyển đổi giữa chế độ xem <b>Danh sách</b> và <b>Lịch tuần</b> ở màn hình Lịch hẹn Spa."
                    ),
                    List.of(
                            new String[] {"Ctrl + F", "Tìm nhanh theo mã phiếu, tên thú, khách"},
                            new String[] {"Làm mới", "Cập nhật danh sách phiếu mới nhất"}
                    ));
        }

        if (lop.contains("Hotel") || lop.contains("LuuTru")) {
            return new NoiDungHuongDan(
                    tieuDe,
                    "Theo dõi sơ đồ phòng lưu trú (Pet Hotel), đặt phòng, thu cọc, ghi nhật ký chăm sóc hằng ngày và quyết toán trả phòng.",
                    List.of(
                            "<b>Xem sơ đồ & Đặt phòng:</b> Kiểm tra các phòng <b>Trống</b> (màu xanh), chọn khách và thú cưng, ngày nhận/trả dự kiến và thu tiền cọc.",
                            "<b>Nhận phòng & Chăm sóc:</b> Khi khách đưa thú đến, bấm <b>Nhận phòng (Check-in)</b> và cập nhật <b>Nhật ký chăm sóc</b> hằng ngày.",
                            "<b>Trả phòng & Quyết toán:</b> Hệ thống tự tính <i>(Số ngày × Đơn giá phòng) + Phụ phí trả trễ/Dịch vụ thêm − Tiền đã cọc</i> để chuyển sang thanh toán."
                    ),
                    List.of(
                            "Phòng đang có thú lưu trú hoặc đang dọn sẽ được khóa không cho đặt trùng.",
                            "Trên Sơ đồ phòng lưu trú, bấm trực tiếp vào từng thẻ phòng để mở nhanh thao tác phù hợp với trạng thái phòng."
                    ),
                    List.of(
                            new String[] {"Ctrl + F", "Tìm nhanh mã phòng hoặc hợp đồng"},
                            new String[] {"Click thẻ phòng", "Thao tác nhanh theo trạng thái phòng"}
                    ));
        }

        if (lop.contains("Kho") || lop.contains("KiemKe") || lop.contains("TonTheoLo")) {
            return new NoiDungHuongDan(
                    tieuDe,
                    "Quản lý nhập kho theo lô/hạn sử dụng, xuất hủy hàng hỏng/hết hạn và kiểm kê cân bằng tồn kho thực tế.",
                    List.of(
                            "<b>Nhập kho:</b> Chọn nhà cung cấp, thêm các mặt hàng kèm <b>Số lô, Hạn sử dụng, Giá nhập</b> và số tiền đã thanh toán cho NCC.",
                            "<b>Xuất hủy kho:</b> Chọn lô hàng cần xuất hủy và nhập rõ lý do (vỡ hỏng, hết hạn...). Hệ thống ưu tiên gợi ý theo chuẩn <b>FEFO</b> (hết hạn trước xuất trước).",
                            "<b>Kiểm kê tồn kho:</b> Lập đợt kiểm kê, nhập số lượng kiểm đếm thực tế và bấm <b>Cân bằng kho (Duyệt)</b> để đồng bộ tồn."
                    ),
                    List.of(
                            "Phiếu xuất hủy có giá trị lớn hoặc biên bản kiểm kê có chênh lệch vượt ngưỡng sẽ chuyển sang trạng thái <b>Chờ duyệt</b> để Cửa hàng trưởng phê duyệt.",
                            "Trên màn hình Tồn kho theo lô: màu vàng là lô sắp hết hạn, màu đỏ là lô đã hết hạn."
                    ),
                    List.of(
                            new String[] {"Ctrl + F", "Tìm nhanh số phiếu, số lô, tên SP"},
                            new String[] {"Xem chi tiết", "Xem chi tiết từng dòng lô hàng"}
                    ));
        }

        if (lop.contains("ThongKe") || cha instanceof com.petstation.gui.reports.BaoCaoPanel) {
            return new NoiDungHuongDan(
                    tieuDe,
                    "Phân tích số liệu kinh doanh trực quan qua thẻ KPI, biểu đồ cột diễn biến, biểu đồ tròn cơ cấu và bảng dữ liệu chi tiết.",
                    List.of(
                            "<b>Chọn kỳ báo cáo:</b> Chọn nhanh kỳ có sẵn (7 ngày, 30 ngày, Tháng này, Năm nay...) hoặc chọn Từ ngày – Đến ngày tùy chỉnh.",
                            "<b>Chọn góc nhìn:</b> Đổi tiêu chí tại mục <b>Xem theo</b> và cách nhóm thời gian tại mục <b>Gộp</b> (Ngày/Tuần/Tháng), sau đó bấm <b>Xem thống kê</b>.",
                            "<b>Tra cứu chứng từ gốc (Drill-down):</b> Nhấp chuột trực tiếp vào <b>cột biểu đồ</b>, <b>miếng biểu đồ tròn</b> hoặc <b>thẻ chỉ số</b> để mở bảng chứng từ chi tiết."
                    ),
                    List.of(
                            "Bấm nút <b>Xuất Excel</b> để kết xuất bảng số liệu đang xem ra file phục vụ báo cáo.",
                            "Một số báo cáo chuyên sâu (Lợi nhuận gộp, Công nợ) được phân quyền riêng cho cấp Quản lý."
                    ),
                    List.of(
                            new String[] {"Click cột/miếng", "Mở danh sách chứng từ gốc"},
                            new String[] {"Xuất Excel", "Tải bảng báo cáo ra file .xlsx"}
                    ));
        }

        return new NoiDungHuongDan(
                tieuDe,
                noiDungGoc != null && !noiDungGoc.isBlank()
                        ? noiDungGoc.replace("\n", "<br/>")
                        : "Màn hình hỗ trợ tra cứu, theo dõi và xử lý nghiệp vụ cửa hàng thú cưng Pet Station.",
                List.of(
                        "<b>Tra cứu & Lọc:</b> Sử dụng thanh tìm kiếm và các bộ lọc phía trên để tìm nhanh dữ liệu cần làm việc.",
                        "<b>Thao tác trên bảng:</b> Chọn một dòng trong danh sách để xem chi tiết bên cạnh hoặc kích hoạt các nút chức năng tương ứng.",
                        "<b>Làm mới dữ liệu:</b> Bấm nút <b>Làm mới</b> để đồng bộ dữ liệu mới nhất từ cơ sở dữ liệu SQL Server."
                ),
                List.of(
                        "Mọi thao tác quan trọng (phê duyệt, phân quyền, đổi trả, cài đặt) đều được ghi nhận tự động vào <b>Nhật ký hệ thống</b>.",
                        "Rê chuột vào các ô bảng hoặc hộp chọn có chữ dài để xem nội dung đầy đủ qua chú thích nổi (Tooltip)."
                ),
                List.of(
                        new String[] {"Ctrl + F", "Tìm kiếm nhanh trong danh sách"},
                        new String[] {"F1", "Mở hộp thoại hướng dẫn sử dụng"},
                        new String[] {"Click tiêu đề cột", "Sắp xếp tăng / giảm dần"}
                ));
    }

    private static String tenManHinhTuLop(String lop) {
        if (lop == null || lop.isEmpty()) return "Màn hình chức năng";
        return lop.replace("Panel", "");
    }
}

