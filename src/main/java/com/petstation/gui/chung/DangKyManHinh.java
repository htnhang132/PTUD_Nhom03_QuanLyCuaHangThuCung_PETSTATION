package com.petstation.gui.chung;

import com.petstation.dto.GioHang;
import com.petstation.dto.PhienDangNhap;
import com.petstation.gui.banhang.CaThuNganPanel;
import com.petstation.gui.banhang.DoiTraPanel;
import com.petstation.gui.banhang.DonHangTreoPanel;
import com.petstation.gui.banhang.InHoaDonPanel;
import com.petstation.gui.banhang.LapHoaDonPanel;
import com.petstation.gui.banhang.ThanhToanPanel;
import com.petstation.gui.banhang.TimKiemHoaDonPanel;
import com.petstation.gui.hethong.CaiDatPanel;
import com.petstation.gui.hethong.DoiMatKhauPanel;
import com.petstation.gui.hethong.NhatKyHeThongPanel;
import com.petstation.gui.hethong.TaiKhoanPhanQuyenPanel;
import com.petstation.gui.hethong.TongQuanPanel;
import com.petstation.gui.khachhang.DanhMucKhachHangPanel;
import com.petstation.gui.khachhang.DanhMucKhuyenMaiPanel;
import com.petstation.gui.khachhang.DanhMucThuCungPanel;
import com.petstation.gui.khachhang.LichSuKhachHangPanel;
import com.petstation.gui.khachhang.TichDiemVoucherPanel;
import com.petstation.gui.kho.DanhMucNhaCungCapPanel;
import com.petstation.gui.kho.DanhSachSanPhamPanel;
import com.petstation.gui.kho.KiemKePanel;
import com.petstation.gui.kho.NhapKhoPanel;
import com.petstation.gui.kho.TimKiemTonTheoLoPanel;
import com.petstation.gui.kho.XuatHuyKhoPanel;
import com.petstation.gui.luutru.DanhMucPhongHotelPanel;
import com.petstation.gui.luutru.LuuTruHotelPanel;
import com.petstation.gui.luutru.TraCuuPhongHotelPanel;
import com.petstation.gui.nhansu.DanhMucNhanVienPanel;
import com.petstation.gui.nhansu.LichTuanPanel;
import com.petstation.gui.pheduyet.TrungTamPheDuyetPanel;
import com.petstation.gui.spa.DanhMucDichVuSpaPanel;
import com.petstation.gui.spa.TiepNhanSpaPanel;
import com.petstation.gui.spa.TraCuuPhieuSpaPanel;
import com.petstation.gui.thongke.ThongKeCongNoPanel;
import com.petstation.gui.thongke.ThongKeDoanhThuPanel;
import com.petstation.gui.thongke.ThongKeHangHoaPanel;
import com.petstation.gui.thongke.ThongKeHotelPanel;
import com.petstation.gui.thongke.ThongKeKhachHangPanel;
import com.petstation.gui.thongke.ThongKeNhanVienPanel;
import com.petstation.gui.thongke.ThongKeSpaPanel;
import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.Quyen;
import com.petstation.service.banhang.CaThuNganService;
import com.petstation.service.banhang.GioHangService;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.JPanel;

public final class DangKyManHinh {
    private static final Set<MaManHinh> CAN_MO_CA_LAM_VIEC =
            EnumSet.of(MaManHinh.LAP_HOA_DON, MaManHinh.THANH_TOAN);

    private final PhienDangNhap phienDangNhap;

    private final Consumer<MaManHinh> boDieuHuong;

    private final Consumer<MaManHinh> quayLaiTuThanhToan;

    private final Map<MaManHinh, DinhNghiaManHinh> cacDinhNghia = new EnumMap<>(MaManHinh.class);

    private final CaThuNganService caLamViec = new CaThuNganService();

    private final GioHang banHang = new GioHangService().tao();

    public DangKyManHinh(PhienDangNhap phienDangNhap, Consumer<MaManHinh> boDieuHuong) {
        this(phienDangNhap, boDieuHuong, boDieuHuong);
    }

    public DangKyManHinh(
            PhienDangNhap phienDangNhap,
            Consumer<MaManHinh> boDieuHuong,
            Consumer<MaManHinh> quayLaiTuThanhToan) {
        this.phienDangNhap = phienDangNhap;
        this.boDieuHuong = boDieuHuong;
        this.quayLaiTuThanhToan = quayLaiTuThanhToan != null ? quayLaiTuThanhToan : boDieuHuong;
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
        dangKyHeThong();
        dangKyDanhMuc();
        dangKyTraCuu();
        dangKyBanHang();
        dangKySpaVaLuuTru();
        dangKyKhoVaVanHanh();
        dangKyThongKe();
    }

    private void dangKyHeThong() {
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
    }

    private void dangKyDanhMuc() {
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
        dangKy(
                MaManHinh.DANH_MUC_NHAN_VIEN,
                "Nhân viên",
                "Danh mục nhân viên.",
                Quyen.QUAN_LY_TAI_KHOAN);
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
    }

    private void dangKyTraCuu() {
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
                "Phiếu Spa",
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
    }

    private void dangKyBanHang() {
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
        dangKy(MaManHinh.IN_HOA_DON, "Xem hóa đơn", "Xem trước, in hoặc xuất PDF.", Quyen.BAN_HANG);
        dangKy(MaManHinh.HOA_DON_CHO, "Đơn nháp", "Danh sách đơn treo.", Quyen.BAN_HANG);
        dangKy(
                MaManHinh.DOI_TRA_HANG_HOA,
                "Đổi hàng",
                "Tìm hóa đơn gốc và đổi phụ kiện, thu chênh lệch.",
                Quyen.BAN_HANG);
    }

    private void dangKySpaVaLuuTru() {
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
    }

    private void dangKyKhoVaVanHanh() {
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
    }

    private void dangKyThongKe() {
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
        DinhNghiaManHinh ngayDangXet = layDinhNghia(id);
        return ngayDangXet != null
                && (ngayDangXet.quyen() == null || phienDangNhap.kiemTraCo(ngayDangXet.quyen()));
    }

    public JPanel tao(MaManHinh id) {
        if (!coTheTruyCap(id)) throw new SecurityException("Bạn không có quyền mở màn hình này.");
        return switch (id) {
            case TONG_QUAN -> new TongQuanPanel(boDieuHuong, this::coTheTruyCap, caLamViec);
            case DOI_MAT_KHAU -> new DoiMatKhauPanel();
            case TAI_KHOAN_PHAN_QUYEN -> new TaiKhoanPhanQuyenPanel();
            case CAI_DAT ->
                    new CaiDatPanel(
                            phienDangNhap != null
                                    && phienDangNhap.kiemTraCo(Quyen.QUAN_LY_TAI_KHOAN));
            case NHAT_KY_HE_THONG -> new NhatKyHeThongPanel();
            case DANH_MUC_KHACH_HANG,
                            DANH_MUC_THU_CUNG,
                            DANH_MUC_NHAN_VIEN,
                            DANH_MUC_SAN_PHAM,
                            DANH_MUC_DICH_VU_SPA,
                            DANH_MUC_PHONG_LUU_TRU,
                            DANH_MUC_NHA_CUNG_CAP,
                            DANH_MUC_KHUYEN_MAI,
                            TICH_DIEM_VOUCHER ->
                    taoManHinhDanhMuc(id);
            case TIM_KIEM_HOA_DON,
                            TRA_CUU_KHACH_HANG,
                            TRA_CUU_LO_HANG,
                            TRA_CUU_LICH_SPA,
                            SO_DO_PHONG_LUU_TRU,
                            TRA_CUU_LICH_LAM_VIEC,
                            LICH_CA_NHAN ->
                    taoManHinhTraCuu(id);
            case LAP_HOA_DON -> new LapHoaDonPanel(banHang, boDieuHuong);
            case THANH_TOAN ->
                    new ThanhToanPanel(
                            banHang,
                            caLamViec,
                            phienDangNhap.getTenHienThi(),
                            boDieuHuong,
                            quayLaiTuThanhToan);
            case IN_HOA_DON -> new InHoaDonPanel(banHang, boDieuHuong);
            case HOA_DON_CHO -> new DonHangTreoPanel(banHang, boDieuHuong);
            case DOI_TRA_HANG_HOA -> new DoiTraPanel(banHang, boDieuHuong);
            case QUAN_LY_SPA,
                            TIEP_NHAN_SPA,
                            CHON_GOI_SPA,
                            CAP_NHAT_TIEN_DO_SPA,
                            HOAN_TAT_SPA,
                            DAT_LICH_HEN_SPA ->
                    new TiepNhanSpaPanel(banHang, boDieuHuong);
            case QUAN_LY_LUU_TRU, DAT_PHONG_LUU_TRU, NHAT_KY_CHAM_SOC, TRA_PHONG_LUU_TRU ->
                    new LuuTruHotelPanel(banHang, boDieuHuong);
            case NHAP_KHO -> new NhapKhoPanel(banHang, boDieuHuong);
            case XUAT_HUY_KHO -> new XuatHuyKhoPanel(banHang, boDieuHuong);
            case KIEM_KE_TON_KHO -> new KiemKePanel(banHang, boDieuHuong);
            case CA_THU_NGAN -> new CaThuNganPanel(caLamViec);
            case PHAN_LICH_NHAN_VIEN ->
                    new LichTuanPanel(LichTuanPanel.CheDo.SUA, maNhanVienHienTai());
            case PHE_DUYET_YEU_CAU -> new TrungTamPheDuyetPanel();
            case THONG_KE_DOANH_THU -> new ThongKeDoanhThuPanel();
            case THONG_KE_HANG_HOA -> new ThongKeHangHoaPanel();
            case THONG_KE_SPA -> new ThongKeSpaPanel();
            case THONG_KE_LUU_TRU -> new ThongKeHotelPanel();
            case THONG_KE_KHACH_HANG -> new ThongKeKhachHangPanel();
            case THONG_KE_CONG_NO_NCC -> new ThongKeCongNoPanel();
            case THONG_KE_NHAN_VIEN -> new ThongKeNhanVienPanel();
        };
    }

    private String maNhanVienHienTai() {
        return phienDangNhap == null || phienDangNhap.getNhanVien() == null
                ? ""
                : phienDangNhap.getNhanVien().getMaNhanVien();
    }

    private static JPanel taoManHinhDanhMuc(MaManHinh id) {
        return switch (id) {
            case DANH_MUC_KHACH_HANG -> new DanhMucKhachHangPanel();
            case DANH_MUC_THU_CUNG -> new DanhMucThuCungPanel();
            case DANH_MUC_NHAN_VIEN -> new DanhMucNhanVienPanel();
            case DANH_MUC_SAN_PHAM -> new DanhSachSanPhamPanel();
            case DANH_MUC_DICH_VU_SPA -> new DanhMucDichVuSpaPanel();
            case DANH_MUC_PHONG_LUU_TRU -> new DanhMucPhongHotelPanel();
            case DANH_MUC_NHA_CUNG_CAP -> new DanhMucNhaCungCapPanel();
            case DANH_MUC_KHUYEN_MAI -> new DanhMucKhuyenMaiPanel();
            case TICH_DIEM_VOUCHER -> new TichDiemVoucherPanel();
            default -> throw new IllegalArgumentException("Không hỗ trợ danh mục: " + id);
        };
    }

    private JPanel taoManHinhTraCuu(MaManHinh id) {
        return switch (id) {
            case TIM_KIEM_HOA_DON -> new TimKiemHoaDonPanel(banHang, boDieuHuong);
            case TRA_CUU_KHACH_HANG -> new LichSuKhachHangPanel();
            case TRA_CUU_LO_HANG -> new TimKiemTonTheoLoPanel(banHang, boDieuHuong);
            case TRA_CUU_LICH_SPA -> new TraCuuPhieuSpaPanel(banHang, boDieuHuong);
            case SO_DO_PHONG_LUU_TRU -> new TraCuuPhongHotelPanel(banHang, boDieuHuong);
            case TRA_CUU_LICH_LAM_VIEC ->
                    new LichTuanPanel(LichTuanPanel.CheDo.HIEN_THI_TAT_CA, maNhanVienHienTai());
            case LICH_CA_NHAN ->
                    new LichTuanPanel(LichTuanPanel.CheDo.CA_NHAN, maNhanVienHienTai());
            default -> throw new IllegalArgumentException("Không hỗ trợ tra cứu: " + id);
        };
    }
}
