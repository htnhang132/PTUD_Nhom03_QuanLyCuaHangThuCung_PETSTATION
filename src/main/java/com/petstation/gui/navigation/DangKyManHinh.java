package com.petstation.gui.navigation;

import com.petstation.gui.catalog.DanhMucDichVuSpaPanel;
import com.petstation.gui.catalog.DanhMucKhachHangPanel;
import com.petstation.gui.catalog.DanhMucKhuyenMaiPanel;
import com.petstation.gui.catalog.DanhMucNhaCungCapPanel;
import com.petstation.gui.catalog.DanhMucNhanVienPanel;
import com.petstation.gui.catalog.DanhMucPhongHotelPanel;
import com.petstation.gui.catalog.DanhMucSanPhamPanel;
import com.petstation.gui.catalog.DanhMucThuCungPanel;
import com.petstation.gui.catalog.TichDiemVoucherPanel;
import com.petstation.gui.dashboard.TongQuanPanel;
import com.petstation.gui.processing.approval.TrungTamPheDuyetPanel;
import com.petstation.gui.processing.approval.YeuCauHoaDonPanel;
import com.petstation.gui.processing.hotel.DatPhongHotelPanel;
import com.petstation.gui.processing.hotel.LuuTruHotelPanel;
import com.petstation.gui.processing.hotel.NhatKyLuuTruPanel;
import com.petstation.gui.processing.hotel.QuyetToanLuuTruPanel;
import com.petstation.gui.processing.inventory.KiemKePanel;
import com.petstation.gui.processing.inventory.NhapKhoPanel;
import com.petstation.gui.processing.inventory.XuatHuyKhoPanel;
import com.petstation.gui.processing.sales.CaThuNganPanel;
import com.petstation.gui.processing.sales.DoiTraPanel;
import com.petstation.gui.processing.sales.DonHangTreoPanel;
import com.petstation.gui.processing.sales.InHoaDonPanel;
import com.petstation.gui.processing.sales.LapHoaDonPanel;
import com.petstation.gui.processing.sales.ThanhToanPanel;
import com.petstation.gui.processing.spa.ChonDichVuSpaPanel;
import com.petstation.gui.processing.spa.DongPhieuSpaPanel;
import com.petstation.gui.processing.spa.LichHenSpaPanel;
import com.petstation.gui.processing.spa.TienDoSpaPanel;
import com.petstation.gui.processing.spa.TiepNhanSpaPanel;
import com.petstation.gui.processing.staff.PhanLichNhanVienPanel;
import com.petstation.gui.reports.ThongKeCongNoPanel;
import com.petstation.gui.reports.ThongKeDoanhThuPanel;
import com.petstation.gui.reports.ThongKeHangHoaPanel;
import com.petstation.gui.reports.ThongKeHotelPanel;
import com.petstation.gui.reports.ThongKeKhachHangPanel;
import com.petstation.gui.reports.ThongKeNhanVienPanel;
import com.petstation.gui.reports.ThongKeSpaPanel;
import com.petstation.gui.search.LichCaNhanPanel;
import com.petstation.gui.search.LichSuKhachHangPanel;
import com.petstation.gui.search.TimKiemHoaDonPanel;
import com.petstation.gui.search.TimKiemTonTheoLoPanel;
import com.petstation.gui.search.TraCuuLichLamViecPanel;
import com.petstation.gui.search.TraCuuPhieuSpaPanel;
import com.petstation.gui.search.TraCuuPhongHotelPanel;
import com.petstation.gui.system.CaiDatPanel;
import com.petstation.gui.system.DoiMatKhauPanel;
import com.petstation.gui.system.NhatKyHeThongPanel;
import com.petstation.gui.system.TaiKhoanPhanQuyenPanel;
import com.petstation.model.GioHang;
import com.petstation.model.MaManHinh;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.service.CaThuNganService;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JPanel;

/** Đăng ký panel, tiêu đề, quyền và điều kiện mở ca cho từng mã màn hình. */
public final class DangKyManHinh {
    /** Các màn bán hàng chỉ dùng được khi đã mở ca. */
    /** Cần mở ca làm việc. */
    private static final Set<MaManHinh> CAN_MO_CA_LAM_VIEC =
            EnumSet.of(MaManHinh.POS, MaManHinh.PAYMENT);

    /** Phiên đăng nhập. */
    private final PhienDangNhap phienDangNhap;

    /** Bộ điều hướng. */
    private final Consumer<MaManHinh> boDieuHuong;

    /** Các định nghĩa. */
    private final Map<MaManHinh, DinhNghiaManHinh> cacDinhNghia = new EnumMap<>(MaManHinh.class);

    /** Ca làm việc. */
    private final CaThuNganService caLamViec = new CaThuNganService();

    /** Bán hàng. */
    private final GioHang banHang = com.petstation.service.GioHangService.tao();

    public DangKyManHinh(PhienDangNhap phienDangNhap, Consumer<MaManHinh> boDieuHuong) {
        this.phienDangNhap = phienDangNhap;
        this.boDieuHuong = boDieuHuong;
        dangKyTatCa();
    }

    public CaThuNganService layCaLamViec() {
        return caLamViec;
    }

    public GioHang layGioHang() {
        return banHang;
    }

    public boolean yeuCauMoCaLamViec(MaManHinh id) {
        return CAN_MO_CA_LAM_VIEC.contains(id);
    }

    private void dangKyTatCa() {
        dangKy(MaManHinh.DASHBOARD, "Màn hình chính", "Chọn một công việc để bắt đầu.", null);

        // 1. Hệ thống
        dangKy(
                MaManHinh.CHANGE_PASSWORD,
                "Đổi mật khẩu",
                "Cập nhật mật khẩu tài khoản hiện tại.",
                null);
        dangKy(
                MaManHinh.ACCOUNT_PERMISSION,
                "Tài khoản & phân quyền",
                "Tài khoản, vai trò và ma trận quyền (chỉ Cửa hàng trưởng).",
                Quyen.ACCOUNT_MANAGE);
        dangKy(
                MaManHinh.SETTINGS,
                "Cài đặt",
                "Cửa hàng, hóa đơn, VAT, ngưỡng tồn, phí trả phòng trễ.",
                Quyen.ACCOUNT_MANAGE);
        dangKy(
                MaManHinh.AUDIT_LOG,
                "Nhật ký hệ thống",
                "Chỉ đọc: thời gian, người dùng, thao tác, đối tượng.",
                Quyen.AUDIT_VIEW);

        // 2. Danh mục (chỉ CRUD)
        dangKy(
                MaManHinh.CUSTOMERS,
                "Khách hàng",
                "Danh mục khách hàng: thêm, sửa, xóa, lưu.",
                Quyen.CUSTOMER_MANAGE);
        dangKy(
                MaManHinh.PETS,
                "Hồ sơ thú cưng",
                "Danh mục thú cưng, bắt buộc có chủ nuôi.",
                Quyen.PET_MANAGE);
        dangKy(MaManHinh.STAFF, "Nhân viên", "Danh mục nhân viên.", Quyen.ACCOUNT_MANAGE);
        dangKy(
                MaManHinh.INVENTORY,
                "Sản phẩm",
                "Danh mục sản phẩm, tab Lịch sử giá. Tồn kho chỉ đổi qua Nhập, Xuất, Kiểm kê.",
                Quyen.PRODUCT_MANAGE);
        dangKy(
                MaManHinh.SPA_SERVICES,
                "Dịch vụ Spa",
                "Danh mục dịch vụ, giá theo loài và cân nặng.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.HOTEL_ROOMS,
                "Phòng lưu trú",
                "Danh mục phòng. Trạng thái do nghiệp vụ cập nhật, chỉ \"bảo trì\" chỉnh tay.",
                Quyen.HOTEL_MANAGE);
        dangKy(
                MaManHinh.SUPPLIERS,
                "Nhà cung cấp",
                "Danh mục nhà cung cấp.",
                Quyen.SUPPLIER_MANAGE);
        dangKy(
                MaManHinh.PROMOTIONS,
                "Khuyến mãi",
                "Danh mục chương trình khuyến mãi.",
                Quyen.PROMOTION_MANAGE);
        dangKy(
                MaManHinh.LOYALTY_VOUCHER,
                "Điểm thưởng và voucher",
                "Quy tắc điểm, hạng thành viên và voucher.",
                Quyen.CUSTOMER_MANAGE);

        // 3. Tìm kiếm (chỉ xem)
        dangKy(
                MaManHinh.ORDERS,
                "Tìm kiếm hóa đơn",
                "Lọc theo ngày, mã HĐ, khách, thu ngân, trạng thái. Chỉ xem và in lại.",
                Quyen.POS_SELL);
        dangKy(
                MaManHinh.CUSTOMER_DETAIL,
                "Lịch sử khách hàng",
                "Hóa đơn, phiếu Spa, lưu trú, thú cưng, điểm.",
                Quyen.CUSTOMER_MANAGE);
        dangKy(
                MaManHinh.BATCH_STOCK,
                "Tồn kho theo lô",
                "Số lô, hạn dùng, tồn. Vàng: sắp hết hạn, đỏ: hết hạn.",
                Quyen.PRODUCT_MANAGE);
        dangKy(
                MaManHinh.SPA_DAILY,
                "Phiếu Spa trong ngày",
                "Lọc theo ngày và trạng thái. Mở phiếu để sang Xử lý.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.HOTEL_MAP,
                "Sơ đồ phòng lưu trú",
                "Lưới phòng theo trạng thái. Phòng quá hạn trả tô đỏ.",
                Quyen.HOTEL_MANAGE);
        dangKy(
                MaManHinh.SCHEDULE,
                "Lịch làm việc",
                "Lịch toàn cửa hàng theo tuần: ca Sáng, Chiều, Tối.",
                Quyen.SCHEDULE_MANAGE);
        dangKy(
                MaManHinh.PERSONAL_SCHEDULE,
                "Lịch làm việc cá nhân",
                "Ca của bạn, ngày nghỉ, tổng giờ trong tháng.",
                Quyen.SCHEDULE_VIEW);

        // 4. Xử lý: bán hàng
        dangKy(
                MaManHinh.POS,
                "Lập hóa đơn",
                "Quét mã hoặc nhập mã sản phẩm, rồi Enter.",
                Quyen.POS_SELL);
        dangKy(
                MaManHinh.PAYMENT,
                "Thanh toán",
                "Chọn phương thức và xác nhận thanh toán.",
                Quyen.PAYMENT_PROCESS);
        dangKy(
                MaManHinh.PRINT_INVOICE,
                "Xem hóa đơn",
                "Xem trước, in hoặc xuất PDF.",
                Quyen.POS_SELL);
        dangKy(MaManHinh.PENDING_INVOICES, "Đơn nháp", "Danh sách đơn treo.", Quyen.POS_SELL);
        dangKy(
                MaManHinh.INVOICE_EDIT_REQUEST,
                "Yêu cầu sửa/hủy hóa đơn",
                "Gửi yêu cầu sang Phê duyệt, không sửa trực tiếp.",
                Quyen.POS_SELL);
        dangKy(
                MaManHinh.RETURNS,
                "Đổi hàng",
                "Tìm hóa đơn gốc và đổi phụ kiện, thu chênh lệch.",
                Quyen.POS_SELL);

        // 4. Xử lý: Spa (danh sách phiếu đang chạy, rồi 5 bước)
        dangKy(
                MaManHinh.SPA,
                "Lập phiếu Spa",
                "Tiếp nhận thú cưng, lập phiếu và thực hiện dịch vụ theo từng bước.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.SPA_SCHEDULE,
                "Spa: Lịch hẹn",
                "Chọn khách, thú, dịch vụ, ngày giờ, nhân viên.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.SPA_INTAKE,
                "Spa: Tiếp nhận",
                "Cân nặng, tình trạng, lưu ý, ảnh.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.SPA_SERVICE_SELECTION,
                "Spa: Chọn dịch vụ",
                "Dịch vụ thực tế, giá theo cân nặng, phụ phí.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.SPA_PROGRESS,
                "Spa: Tiến độ",
                "Chờ, đang làm, hoàn thành.",
                Quyen.SPA_MANAGE);
        dangKy(
                MaManHinh.SPA_CLOSE,
                "Spa: Đóng phiếu",
                "Chốt tổng tiền rồi chuyển sang Thanh toán.",
                Quyen.SPA_MANAGE);

        // 4. Xử lý: ThuCung Hotel (danh sách thú đang lưu trú, rồi 3 bước)
        dangKy(
                MaManHinh.HOTEL,
                "Quản lý lưu trú",
                "Danh sách thú đang lưu trú. Bấm + Đặt phòng để bắt đầu.",
                Quyen.HOTEL_MANAGE);
        dangKy(
                MaManHinh.HOTEL_BOOKING,
                "Lưu trú: Đặt phòng",
                "Chọn khách, thú, phòng trống, ngày vào ra, tiền cọc.",
                Quyen.HOTEL_MANAGE);
        dangKy(
                MaManHinh.HOTEL_JOURNAL,
                "Lưu trú: Nhật ký chăm sóc",
                "Check-in, chăm sóc hằng ngày, gia hạn, dịch vụ thêm.",
                Quyen.HOTEL_MANAGE);
        dangKy(
                MaManHinh.CHECKOUT,
                "Lưu trú: Trả phòng và quyết toán",
                "Số ngày × giá phòng + phụ phí trễ + dịch vụ − tiền cọc.",
                Quyen.HOTEL_MANAGE);

        // 4. Xử lý: kho, ca, lịch, phê duyệt
        dangKy(
                MaManHinh.PURCHASE,
                "Lập phiếu nhập kho",
                "Lập phiếu nhập: số lô, hạn dùng, công nợ.",
                Quyen.INVENTORY_RECEIVE);
        dangKy(
                MaManHinh.GOODS_WRITE_OFF,
                "Xuất hủy hàng hóa",
                "Chọn lý do, lô và số lượng.",
                Quyen.INVENTORY_ISSUE);
        dangKy(
                MaManHinh.STOCKTAKE,
                "Kiểm kê tồn kho",
                "Nhập số thực tế, chênh lệch gửi duyệt.",
                Quyen.INVENTORY_COUNT);
        dangKy(
                MaManHinh.CASH_SHIFT,
                "Ca thu ngân",
                "Quản lý ca và tiền mặt.",
                Quyen.CASH_SHIFT_MANAGE);
        dangKy(
                MaManHinh.STAFF_SCHEDULE,
                "Phân lịch nhân viên",
                "Phân ca theo lưới tuần, cảnh báo ca thiếu người (chỉ Cửa hàng trưởng).",
                Quyen.SCHEDULE_MANAGE);
        dangKy(
                MaManHinh.APPROVALS,
                "Phê duyệt yêu cầu",
                "Duyệt hoặc từ chối yêu cầu (chỉ Cửa hàng trưởng).",
                Quyen.APPROVAL_PROCESS);

        // 5. Thống kê (chỉ xem và xuất)
        dangKy(
                MaManHinh.REPORTS,
                "Thống kê doanh thu",
                "Theo ngày, nhân viên, ca, nguồn, phương thức thanh toán.",
                Quyen.REPORT_VIEW_OWN_SHIFT);
        dangKy(
                MaManHinh.REPORT_GOODS,
                "Thống kê hàng hóa",
                "Bán chạy, nhập–xuất–tồn, tồn thấp, sắp hết hạn.",
                Quyen.REPORT_VIEW_OWN_SHIFT);
        dangKy(
                MaManHinh.REPORT_SPA,
                "Thống kê dịch vụ Spa",
                "Số phiếu, dịch vụ phổ biến, tỷ lệ hủy, hiệu suất.",
                Quyen.REPORT_VIEW_OWN_SHIFT);
        dangKy(
                MaManHinh.REPORT_HOTEL,
                "Thống kê lưu trú",
                "Công suất phòng, số ngày lưu trú, lượt quá hạn.",
                Quyen.REPORT_VIEW_OWN_SHIFT);
        dangKy(
                MaManHinh.REPORT_CUSTOMERS,
                "Thống kê khách hàng",
                "Khách mới, thân thiết, top chi tiêu, điểm và voucher.",
                Quyen.REPORT_VIEW_ALL);
        dangKy(
                MaManHinh.REPORT_SUPPLIER_DEBT,
                "Công nợ nhà cung cấp",
                "Tổng nhập, đã trả, còn nợ.",
                Quyen.REPORT_VIEW_ALL);
        dangKy(
                MaManHinh.REPORT_STAFF,
                "Thống kê nhân viên",
                "Số ca, giờ làm, doanh số, phiếu Spa đã thực hiện.",
                Quyen.REPORT_VIEW_ALL);
    }

    private void dangKy(MaManHinh id, String tieuDe, String goiY, Quyen quyen) {
        cacDinhNghia.put(id, new DinhNghiaManHinh(id, tieuDe, goiY, quyen, true));
    }

    public DinhNghiaManHinh layDinhNghia(MaManHinh id) {
        return cacDinhNghia.get(id);
    }

    public boolean coTheTruyCap(MaManHinh id) {
        // Ngày đang xét.
        DinhNghiaManHinh ngayDangXet = layDinhNghia(id);
        return ngayDangXet != null
                && (ngayDangXet.quyen() == null || phienDangNhap.kiemTraCo(ngayDangXet.quyen()));
    }

    public List<DinhNghiaManHinh> layCacDinhNghiaHienThi() {
        return cacDinhNghia.values().stream()
                .filter(ngayDangXet -> coTheTruyCap(ngayDangXet.id()))
                .toList();
    }

    public JPanel tao(MaManHinh id) {
        if (!coTheTruyCap(id))
            throw new com.petstation.service.PhanQuyenException(
                    "Bạn không có quyền mở màn hình này.");
        return switch (id) {
            case DASHBOARD -> new TongQuanPanel(boDieuHuong, this::coTheTruyCap, caLamViec);
            // Hệ thống
            case CHANGE_PASSWORD -> new DoiMatKhauPanel(boDieuHuong);
            case ACCOUNT_PERMISSION -> new TaiKhoanPhanQuyenPanel();
            case SETTINGS ->
                    new CaiDatPanel(
                            phienDangNhap == null
                                    || phienDangNhap.docVaiTro()
                                            == com.petstation.model.VaiTro.STORE_MANAGER);
            case AUDIT_LOG -> new NhatKyHeThongPanel();
            // Danh mục
            case CUSTOMERS -> new DanhMucKhachHangPanel();
            case PETS -> new DanhMucThuCungPanel();
            case STAFF -> new DanhMucNhanVienPanel();
            case INVENTORY -> new DanhMucSanPhamPanel();
            case SPA_SERVICES -> new DanhMucDichVuSpaPanel();
            case HOTEL_ROOMS -> new DanhMucPhongHotelPanel();
            case SUPPLIERS -> new DanhMucNhaCungCapPanel();
            case PROMOTIONS -> new DanhMucKhuyenMaiPanel();
            case LOYALTY_VOUCHER -> new TichDiemVoucherPanel();
            // Tìm kiếm
            case ORDERS -> new TimKiemHoaDonPanel(banHang, boDieuHuong);
            case CUSTOMER_DETAIL -> new LichSuKhachHangPanel();
            case BATCH_STOCK -> new TimKiemTonTheoLoPanel(banHang, boDieuHuong);
            case SPA_DAILY -> new TraCuuPhieuSpaPanel(banHang, boDieuHuong);
            case HOTEL_MAP -> new TraCuuPhongHotelPanel(banHang, boDieuHuong);
            case SCHEDULE ->
                    new TraCuuLichLamViecPanel(
                            phienDangNhap == null ? "" : phienDangNhap.nhanVien().maNhanVien());
            case PERSONAL_SCHEDULE ->
                    new LichCaNhanPanel(
                            phienDangNhap == null ? "" : phienDangNhap.nhanVien().maNhanVien());
            // Xử lý: bán hàng
            case POS -> new LapHoaDonPanel(banHang, boDieuHuong);
            case PAYMENT ->
                    new ThanhToanPanel(
                            banHang, caLamViec, phienDangNhap.layTenHienThi(), boDieuHuong);
            case PRINT_INVOICE -> new InHoaDonPanel(banHang, boDieuHuong);
            case PENDING_INVOICES -> new DonHangTreoPanel(banHang, boDieuHuong);
            case INVOICE_EDIT_REQUEST -> new YeuCauHoaDonPanel(boDieuHuong);
            case RETURNS -> new DoiTraPanel(banHang, boDieuHuong);
            // Xử lý: Spa
            case SPA -> new TiepNhanSpaPanel(banHang, boDieuHuong);
            case SPA_SCHEDULE -> new LichHenSpaPanel(banHang, boDieuHuong);
            case SPA_INTAKE -> new TiepNhanSpaPanel(banHang, boDieuHuong);
            case SPA_SERVICE_SELECTION -> new ChonDichVuSpaPanel(banHang, boDieuHuong);
            case SPA_PROGRESS -> new TienDoSpaPanel(banHang, boDieuHuong);
            case SPA_CLOSE -> new DongPhieuSpaPanel(banHang, boDieuHuong);
            // Xử lý: ThuCung Hotel
            case HOTEL -> new LuuTruHotelPanel(banHang, boDieuHuong);
            case HOTEL_BOOKING -> new DatPhongHotelPanel(banHang, boDieuHuong);
            case HOTEL_JOURNAL -> new NhatKyLuuTruPanel(banHang, boDieuHuong);
            case CHECKOUT -> new QuyetToanLuuTruPanel(banHang, boDieuHuong);
            // Xử lý: kho, ca, lịch, phê duyệt
            case PURCHASE -> new NhapKhoPanel(banHang, boDieuHuong);
            case GOODS_WRITE_OFF -> new XuatHuyKhoPanel(banHang, boDieuHuong);
            case STOCKTAKE -> new KiemKePanel(banHang, boDieuHuong);
            case CASH_SHIFT -> new CaThuNganPanel(caLamViec);
            case STAFF_SCHEDULE ->
                    new PhanLichNhanVienPanel(
                            phienDangNhap == null ? "" : phienDangNhap.nhanVien().maNhanVien());
            case APPROVALS -> new TrungTamPheDuyetPanel();
            // Thống kê
            case REPORTS -> new ThongKeDoanhThuPanel();
            case REPORT_GOODS -> new ThongKeHangHoaPanel();
            case REPORT_SPA -> new ThongKeSpaPanel();
            case REPORT_HOTEL -> new ThongKeHotelPanel();
            case REPORT_CUSTOMERS -> new ThongKeKhachHangPanel();
            case REPORT_SUPPLIER_DEBT -> new ThongKeCongNoPanel();
            case REPORT_STAFF -> new ThongKeNhanVienPanel();
        };
    }
}
