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
            EnumSet.of(MaManHinh.LAP_HOA_DON, MaManHinh.THANH_TOAN);

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
        dangKy(MaManHinh.TONG_QUAN, "Màn hình chính", "Chọn một công việc để bắt đầu.", null);

        // 1. Hệ thống
        dangKy(
                MaManHinh.DOI_MAT_KHAU,
                "Đổi mật khẩu",
                "Cập nhật mật khẩu tài khoản hiện tại.",
                null);
        dangKy(
                MaManHinh.TAI_KHOAN_PHAN_QUYEN,
                "Tài khoản & phân quyền",
                "Tài khoản, vai trò và ma trận quyền (chỉ Cửa hàng trưởng).",
                Quyen.QUAN_LY_TAI_KHOAN);
        dangKy(
                MaManHinh.CAI_DAT,
                "Cài đặt",
                "Cửa hàng, hóa đơn, VAT, ngưỡng tồn, phí trả phòng trễ.",
                Quyen.QUAN_LY_TAI_KHOAN);
        dangKy(
                MaManHinh.NHAT_KY_HE_THONG,
                "Nhật ký hệ thống",
                "Chỉ đọc: thời gian, người dùng, thao tác, đối tượng.",
                Quyen.XEM_NHAT_KY);

        // 2. Danh mục (chỉ CRUD)
        dangKy(
                MaManHinh.DANH_MUC_KHACH_HANG,
                "Khách hàng",
                "Danh mục khách hàng: thêm, sửa, xóa, lưu.",
                Quyen.QUAN_LY_KHACH_HANG);
        dangKy(
                MaManHinh.DANH_MUC_THU_CUNG,
                "Hồ sơ thú cưng",
                "Danh mục thú cưng, bắt buộc có chủ nuôi.",
                Quyen.QUAN_LY_THU_CUNG);
        dangKy(MaManHinh.DANH_MUC_NHAN_VIEN, "Nhân viên", "Danh mục nhân viên.", Quyen.QUAN_LY_TAI_KHOAN);
        dangKy(
                MaManHinh.DANH_MUC_SAN_PHAM,
                "Sản phẩm",
                "Danh mục sản phẩm. Tồn kho chỉ cập nhật qua Nhập kho, Xuất kho, Kiểm kê.",
                Quyen.QUAN_LY_SAN_PHAM);
        dangKy(
                MaManHinh.DANH_MUC_DICH_VU_SPA,
                "Dịch vụ Spa",
                "Danh mục dịch vụ, giá theo loài và cân nặng.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.DANH_MUC_PHONG_LUU_TRU,
                "Phòng lưu trú",
                "Danh mục phòng. Trạng thái do nghiệp vụ cập nhật, chỉ \"bảo trì\" chỉnh tay.",
                Quyen.QUAN_LY_LUU_TRU);
        dangKy(
                MaManHinh.DANH_MUC_NHA_CUNG_CAP,
                "Nhà cung cấp",
                "Danh mục nhà cung cấp.",
                Quyen.QUAN_LY_NHA_CUNG_CAP);
        dangKy(
                MaManHinh.DANH_MUC_KHUYEN_MAI,
                "Khuyến mãi",
                "Danh mục chương trình khuyến mãi.",
                Quyen.QUAN_LY_KHUYEN_MAI);
        dangKy(
                MaManHinh.TICH_DIEM_VOUCHER,
                "Điểm thưởng và voucher",
                "Quy tắc điểm, hạng thành viên và voucher.",
                Quyen.QUAN_LY_KHACH_HANG);

        // 3. Tìm kiếm (chỉ xem)
        dangKy(
                MaManHinh.TIM_KIEM_HOA_DON,
                "Tìm kiếm hóa đơn",
                "Lọc theo ngày, mã HĐ, khách, thu ngân, trạng thái. Chỉ xem và in lại.",
                Quyen.BAN_HANG);
        dangKy(
                MaManHinh.TRA_CUU_KHACH_HANG,
                "Lịch sử khách hàng",
                "Hóa đơn, phiếu Spa, lưu trú, thú cưng, điểm.",
                Quyen.QUAN_LY_KHACH_HANG);
        dangKy(
                MaManHinh.TRA_CUU_LO_HANG,
                "Tồn kho theo lô",
                "Số lô, hạn dùng, tồn. Vàng: sắp hết hạn, đỏ: hết hạn.",
                Quyen.QUAN_LY_SAN_PHAM);
        dangKy(
                MaManHinh.TRA_CUU_LICH_SPA,
                "Phiếu Spa trong ngày",
                "Lọc theo ngày và trạng thái. Mở phiếu để sang Xử lý.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.SO_DO_PHONG_LUU_TRU,
                "Sơ đồ phòng lưu trú",
                "Lưới phòng theo trạng thái. Phòng quá hạn trả tô đỏ.",
                Quyen.QUAN_LY_LUU_TRU);
        dangKy(
                MaManHinh.TRA_CUU_LICH_LAM_VIEC,
                "Lịch làm việc",
                "Lịch toàn cửa hàng theo tuần: ca Sáng, Chiều, Tối.",
                Quyen.QUAN_LY_LICH_LAM_VIEC);
        dangKy(
                MaManHinh.LICH_CA_NHAN,
                "Lịch làm việc cá nhân",
                "Ca của bạn, ngày nghỉ, tổng giờ trong tháng.",
                Quyen.XEM_LICH_LAM_VIEC);

        // 4. Xử lý: bán hàng
        dangKy(
                MaManHinh.LAP_HOA_DON,
                "Lập hóa đơn",
                "Quét mã hoặc nhập mã sản phẩm, rồi Enter.",
                Quyen.BAN_HANG);
        dangKy(
                MaManHinh.THANH_TOAN,
                "Thanh toán",
                "Chọn phương thức và xác nhận thanh toán.",
                Quyen.THANH_TOAN);
        dangKy(
                MaManHinh.IN_HOA_DON,
                "Xem hóa đơn",
                "Xem trước, in hoặc xuất PDF.",
                Quyen.BAN_HANG);
        dangKy(MaManHinh.HOA_DON_CHO, "Đơn nháp", "Danh sách đơn treo.", Quyen.BAN_HANG);
        dangKy(
                MaManHinh.DOI_TRA_HANG_HOA,
                "Đổi hàng",
                "Tìm hóa đơn gốc và đổi phụ kiện, thu chênh lệch.",
                Quyen.BAN_HANG);

        // 4. Xử lý: Spa (danh sách phiếu đang chạy, rồi 5 bước)
        dangKy(
                MaManHinh.QUAN_LY_SPA,
                "Lập phiếu Spa",
                "Tiếp nhận thú cưng, lập phiếu và thực hiện dịch vụ theo từng bước.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.DAT_LICH_HEN_SPA,
                "Spa: Lịch hẹn",
                "Chọn khách, thú, dịch vụ, ngày giờ, nhân viên.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.TIEP_NHAN_SPA,
                "Spa: Tiếp nhận",
                "Cân nặng, tình trạng, lưu ý, ảnh.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.CHON_GOI_SPA,
                "Spa: Chọn dịch vụ",
                "Dịch vụ thực tế, giá theo cân nặng, phụ phí.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.CAP_NHAT_TIEN_DO_SPA,
                "Spa: Tiến độ",
                "Chờ, đang làm, hoàn thành.",
                Quyen.QUAN_LY_SPA);
        dangKy(
                MaManHinh.HOAN_TAT_SPA,
                "Spa: Đóng phiếu",
                "Chốt tổng tiền rồi chuyển sang Thanh toán.",
                Quyen.QUAN_LY_SPA);

        // 4. Xử lý: ThuCung Hotel (danh sách thú đang lưu trú, rồi 3 bước)
        dangKy(
                MaManHinh.QUAN_LY_LUU_TRU,
                "Quản lý lưu trú",
                "Danh sách thú đang lưu trú. Bấm + Đặt phòng để bắt đầu.",
                Quyen.QUAN_LY_LUU_TRU);
        dangKy(
                MaManHinh.DAT_PHONG_LUU_TRU,
                "Lưu trú: Đặt phòng",
                "Chọn khách, thú, phòng trống, ngày vào ra, tiền cọc.",
                Quyen.QUAN_LY_LUU_TRU);
        dangKy(
                MaManHinh.NHAT_KY_CHAM_SOC,
                "Lưu trú: Nhật ký chăm sóc",
                "Check-in, chăm sóc hằng ngày, gia hạn, dịch vụ thêm.",
                Quyen.QUAN_LY_LUU_TRU);
        dangKy(
                MaManHinh.TRA_PHONG_LUU_TRU,
                "Lưu trú: Trả phòng và quyết toán",
                "Số ngày × giá phòng + phụ phí trễ + dịch vụ − tiền cọc.",
                Quyen.QUAN_LY_LUU_TRU);

        // 4. Xử lý: kho, ca, lịch, phê duyệt
        dangKy(
                MaManHinh.NHAP_KHO,
                "Lập phiếu nhập kho",
                "Lập phiếu nhập: số lô, hạn dùng, công nợ.",
                Quyen.NHAP_KHO);
        dangKy(
                MaManHinh.XUAT_HUY_KHO,
                "Xuất hủy hàng hóa",
                "Chọn lý do, lô và số lượng.",
                Quyen.XUAT_HUY_KHO);
        dangKy(
                MaManHinh.KIEM_KE_TON_KHO,
                "Kiểm kê tồn kho",
                "Nhập số thực tế, chênh lệch gửi duyệt.",
                Quyen.KIEM_KE_KHO);
        dangKy(
                MaManHinh.CA_THU_NGAN,
                "Ca thu ngân",
                "Quản lý ca và tiền mặt.",
                Quyen.QUAN_LY_CA_THU_NGAN);
        dangKy(
                MaManHinh.PHAN_LICH_NHAN_VIEN,
                "Phân lịch nhân viên",
                "Phân ca theo lưới tuần, cảnh báo ca thiếu người (chỉ Cửa hàng trưởng).",
                Quyen.QUAN_LY_LICH_LAM_VIEC);
        dangKy(
                MaManHinh.PHE_DUYET_YEU_CAU,
                "Phê duyệt yêu cầu",
                "Duyệt hoặc từ chối yêu cầu (chỉ Cửa hàng trưởng).",
                Quyen.XU_LY_PHE_DUYET);

        // 5. Thống kê (chỉ xem và xuất)
        dangKy(
                MaManHinh.THONG_KE_DOANH_THU,
                "Thống kê doanh thu",
                "Theo ngày, nhân viên, ca, nguồn, phương thức thanh toán.",
                Quyen.XEM_BAO_CAO_CA);
        dangKy(
                MaManHinh.THONG_KE_HANG_HOA,
                "Thống kê hàng hóa",
                "Bán chạy, nhập–xuất–tồn, tồn thấp, sắp hết hạn.",
                Quyen.XEM_BAO_CAO_CA);
        dangKy(
                MaManHinh.THONG_KE_SPA,
                "Thống kê dịch vụ Spa",
                "Số phiếu, dịch vụ phổ biến, tỷ lệ hủy, hiệu suất.",
                Quyen.XEM_BAO_CAO_CA);
        dangKy(
                MaManHinh.THONG_KE_LUU_TRU,
                "Thống kê lưu trú",
                "Công suất phòng, số ngày lưu trú, lượt quá hạn.",
                Quyen.XEM_BAO_CAO_CA);
        dangKy(
                MaManHinh.THONG_KE_KHACH_HANG,
                "Thống kê khách hàng",
                "Khách mới, thân thiết, top chi tiêu, điểm và voucher.",
                Quyen.XEM_BAO_CAO_CHUNG);
        dangKy(
                MaManHinh.THONG_KE_CONG_NO_NCC,
                "Công nợ nhà cung cấp",
                "Tổng nhập, đã trả, còn nợ.",
                Quyen.XEM_BAO_CAO_CHUNG);
        dangKy(
                MaManHinh.THONG_KE_NHAN_VIEN,
                "Thống kê nhân viên",
                "Số ca, giờ làm, doanh số, phiếu Spa đã thực hiện.",
                Quyen.XEM_BAO_CAO_CHUNG);
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
            case TONG_QUAN -> new TongQuanPanel(boDieuHuong, this::coTheTruyCap, caLamViec);
            // Hệ thống
            case DOI_MAT_KHAU -> new DoiMatKhauPanel(boDieuHuong);
            case TAI_KHOAN_PHAN_QUYEN -> new TaiKhoanPhanQuyenPanel();
            case CAI_DAT ->
                    new CaiDatPanel(
                            phienDangNhap == null
                                    || phienDangNhap.docVaiTro()
                                            == com.petstation.model.VaiTro.QUAN_LY_CUA_HANG);
            case NHAT_KY_HE_THONG -> new NhatKyHeThongPanel();
            // Danh mục
            case DANH_MUC_KHACH_HANG -> new DanhMucKhachHangPanel();
            case DANH_MUC_THU_CUNG -> new DanhMucThuCungPanel();
            case DANH_MUC_NHAN_VIEN -> new DanhMucNhanVienPanel();
            case DANH_MUC_SAN_PHAM -> new com.petstation.gui.catalog.DanhSachSanPhamPanel();
            case DANH_MUC_DICH_VU_SPA -> new DanhMucDichVuSpaPanel();
            case DANH_MUC_PHONG_LUU_TRU -> new DanhMucPhongHotelPanel();
            case DANH_MUC_NHA_CUNG_CAP -> new DanhMucNhaCungCapPanel();
            case DANH_MUC_KHUYEN_MAI -> new DanhMucKhuyenMaiPanel();
            case TICH_DIEM_VOUCHER -> new TichDiemVoucherPanel();
            // Tìm kiếm
            case TIM_KIEM_HOA_DON -> new TimKiemHoaDonPanel(banHang, boDieuHuong);
            case TRA_CUU_KHACH_HANG -> new LichSuKhachHangPanel();
            case TRA_CUU_LO_HANG -> new TimKiemTonTheoLoPanel(banHang, boDieuHuong);
            case TRA_CUU_LICH_SPA -> new TraCuuPhieuSpaPanel(banHang, boDieuHuong);
            case SO_DO_PHONG_LUU_TRU -> new TraCuuPhongHotelPanel(banHang, boDieuHuong);
            case TRA_CUU_LICH_LAM_VIEC ->
                    new TraCuuLichLamViecPanel(
                            phienDangNhap == null ? "" : phienDangNhap.nhanVien().maNhanVien());
            case LICH_CA_NHAN ->
                    new LichCaNhanPanel(
                            phienDangNhap == null ? "" : phienDangNhap.nhanVien().maNhanVien());
            // Xử lý: bán hàng
            case LAP_HOA_DON -> new LapHoaDonPanel(banHang, boDieuHuong);
            case THANH_TOAN ->
                    new ThanhToanPanel(
                            banHang, caLamViec, phienDangNhap.layTenHienThi(), boDieuHuong);
            case IN_HOA_DON -> new InHoaDonPanel(banHang, boDieuHuong);
            case HOA_DON_CHO -> new DonHangTreoPanel(banHang, boDieuHuong);
            case DOI_TRA_HANG_HOA -> new DoiTraPanel(banHang, boDieuHuong);
            // Xử lý: Spa
            case QUAN_LY_SPA -> new TiepNhanSpaPanel(banHang, boDieuHuong);
            case DAT_LICH_HEN_SPA -> new LichHenSpaPanel(banHang, boDieuHuong);
            case TIEP_NHAN_SPA -> new TiepNhanSpaPanel(banHang, boDieuHuong);
            case CHON_GOI_SPA -> new ChonDichVuSpaPanel(banHang, boDieuHuong);
            case CAP_NHAT_TIEN_DO_SPA -> new TienDoSpaPanel(banHang, boDieuHuong);
            case HOAN_TAT_SPA -> new DongPhieuSpaPanel(banHang, boDieuHuong);
            // Xử lý: ThuCung Hotel
            case QUAN_LY_LUU_TRU -> new LuuTruHotelPanel(banHang, boDieuHuong);
            case DAT_PHONG_LUU_TRU -> new DatPhongHotelPanel(banHang, boDieuHuong);
            case NHAT_KY_CHAM_SOC -> new NhatKyLuuTruPanel(banHang, boDieuHuong);
            case TRA_PHONG_LUU_TRU -> new QuyetToanLuuTruPanel(banHang, boDieuHuong);
            // Xử lý: kho, ca, lịch, phê duyệt
            case NHAP_KHO -> new NhapKhoPanel(banHang, boDieuHuong);
            case XUAT_HUY_KHO -> new XuatHuyKhoPanel(banHang, boDieuHuong);
            case KIEM_KE_TON_KHO -> new KiemKePanel(banHang, boDieuHuong);
            case CA_THU_NGAN -> new CaThuNganPanel(caLamViec);
            case PHAN_LICH_NHAN_VIEN ->
                    new PhanLichNhanVienPanel(
                            phienDangNhap == null ? "" : phienDangNhap.nhanVien().maNhanVien());
            case PHE_DUYET_YEU_CAU -> new TrungTamPheDuyetPanel();
            // Thống kê
            case THONG_KE_DOANH_THU -> new ThongKeDoanhThuPanel();
            case THONG_KE_HANG_HOA -> new ThongKeHangHoaPanel();
            case THONG_KE_SPA -> new ThongKeSpaPanel();
            case THONG_KE_LUU_TRU -> new ThongKeHotelPanel();
            case THONG_KE_KHACH_HANG -> new ThongKeKhachHangPanel();
            case THONG_KE_CONG_NO_NCC -> new ThongKeCongNoPanel();
            case THONG_KE_NHAN_VIEN -> new ThongKeNhanVienPanel();
        };
    }
}
