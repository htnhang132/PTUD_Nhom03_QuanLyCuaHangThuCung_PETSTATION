package com.petstation.gui.navigation;

import com.petstation.model.MaManHinh;

import java.util.List;

/** Khai báo năm nhóm menu, các mục chức năng và phím tắt dùng trong điều hướng. */
public final class CauTrucMenu {
    /**
     * Cung cấp giao diện và xử lý tương tác cho mục.
     *
     * @param nhan nhãn
     * @param manHinh màn hình
     * @param phimTat phím tắt
     */
    public record Muc(String nhan, MaManHinh manHinh, String phimTat) {
        public boolean kiemTraPhanCach() {
            return manHinh == null;
        }
    }

    /**
     * Cung cấp giao diện và xử lý tương tác cho nhóm.
     *
     * @param phimTruyCap phím truy cập
     * @param nhan nhãn
     * @param cacMuc các mục
     */
    public record Nhom(char phimTruyCap, String nhan, List<Muc> cacMuc) {}

    /** Phân cách. */
    private static final Muc PHAN_CACH = new Muc(null, null, null);

    private static Muc layMuc(String nhan, MaManHinh manHinh) {
        return new Muc(nhan, manHinh, null);
    }

    private static Muc layMuc(String nhan, MaManHinh manHinh, String khoa) {
        return new Muc(nhan, manHinh, khoa);
    }

    /** Các nhóm. */
    private static final List<Nhom> CAC_NHOM =
            List.of(
                    // 1. Hệ thống (Đăng xuất / Thoát do ThanhMenuChinh thêm vào cuối)
                    new Nhom(
                            'H',
                            "Hệ thống",
                            List.of(
                                    layMuc("Đổi mật khẩu", MaManHinh.CHANGE_PASSWORD),
                                    layMuc("Tài khoản & phân quyền", MaManHinh.ACCOUNT_PERMISSION),
                                    layMuc("Cài đặt", MaManHinh.SETTINGS),
                                    layMuc("Nhật ký hệ thống", MaManHinh.AUDIT_LOG))),
                    // 2. Danh mục: chỉ CRUD
                    new Nhom(
                            'D',
                            "Danh mục",
                            List.of(
                                    layMuc("Khách hàng", MaManHinh.CUSTOMERS),
                                    layMuc("Hồ sơ thú cưng", MaManHinh.PETS),
                                    layMuc("Nhân viên", MaManHinh.STAFF),
                                    layMuc("Sản phẩm", MaManHinh.INVENTORY),
                                    layMuc("Dịch vụ Spa", MaManHinh.SPA_SERVICES),
                                    layMuc("Phòng lưu trú", MaManHinh.HOTEL_ROOMS),
                                    layMuc("Nhà cung cấp", MaManHinh.SUPPLIERS),
                                    layMuc("Khuyến mãi", MaManHinh.PROMOTIONS),
                                    layMuc("Điểm thưởng và voucher", MaManHinh.LOYALTY_VOUCHER))),
                    // 3. Tìm kiếm: chỉ xem
                    new Nhom(
                            'T',
                            "Tìm kiếm",
                            List.of(
                                    layMuc("Hóa đơn", MaManHinh.ORDERS, "F3"),
                                    layMuc("Lịch sử khách hàng", MaManHinh.CUSTOMER_DETAIL),
                                    layMuc("Tồn kho theo lô", MaManHinh.BATCH_STOCK),
                                    layMuc("Phiếu Spa trong ngày", MaManHinh.SPA_DAILY),
                                    layMuc("Sơ đồ phòng lưu trú", MaManHinh.HOTEL_MAP),
                                    PHAN_CACH,
                                    layMuc("Lịch làm việc", MaManHinh.SCHEDULE),
                                    layMuc("Lịch làm việc cá nhân", MaManHinh.PERSONAL_SCHEDULE))),
                    // 4. Xử lý: nghiệp vụ
                    new Nhom(
                            'X',
                            "Xử lý",
                            List.of(
                                    layMuc("Lập hóa đơn", MaManHinh.POS, "F2"),
                                    layMuc("Đổi hàng", MaManHinh.RETURNS),
                                    layMuc("Lập phiếu Spa", MaManHinh.SPA),
                                    layMuc("Quản lý lưu trú", MaManHinh.HOTEL),
                                    PHAN_CACH,
                                    layMuc("Lập phiếu nhập kho", MaManHinh.PURCHASE),
                                    layMuc("Xuất hủy hàng hóa", MaManHinh.GOODS_WRITE_OFF),
                                    layMuc("Kiểm kê tồn kho", MaManHinh.STOCKTAKE),
                                    PHAN_CACH,
                                    layMuc("Ca thu ngân", MaManHinh.CASH_SHIFT),
                                    layMuc("Phân lịch nhân viên", MaManHinh.STAFF_SCHEDULE),
                                    layMuc("Phê duyệt yêu cầu", MaManHinh.APPROVALS))),
                    // 5. Thống kê: chỉ xem và xuất
                    new Nhom(
                            'K',
                            "Thống kê",
                            List.of(
                                    layMuc("Doanh thu", MaManHinh.REPORTS),
                                    layMuc("Hàng hóa", MaManHinh.REPORT_GOODS),
                                    layMuc("Dịch vụ Spa", MaManHinh.REPORT_SPA),
                                    layMuc("Lưu trú", MaManHinh.REPORT_HOTEL),
                                    layMuc("Khách hàng", MaManHinh.REPORT_CUSTOMERS),
                                    layMuc("Công nợ nhà cung cấp", MaManHinh.REPORT_SUPPLIER_DEBT),
                                    layMuc("Nhân viên", MaManHinh.REPORT_STAFF))));

    private CauTrucMenu() {}

    public static List<Nhom> layCacNhom() {
        return CAC_NHOM;
    }
}
