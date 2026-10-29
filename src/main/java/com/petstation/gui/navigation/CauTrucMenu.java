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
                                    layMuc("Đổi mật khẩu", MaManHinh.DOI_MAT_KHAU),
                                    layMuc("Tài khoản & phân quyền", MaManHinh.TAI_KHOAN_PHAN_QUYEN),
                                    layMuc("Cài đặt", MaManHinh.CAI_DAT),
                                    layMuc("Nhật ký hệ thống", MaManHinh.NHAT_KY_HE_THONG))),
                    // 2. Danh mục: chỉ CRUD
                    new Nhom(
                            'D',
                            "Danh mục",
                            List.of(
                                    layMuc("Khách hàng", MaManHinh.DANH_MUC_KHACH_HANG),
                                    layMuc("Hồ sơ thú cưng", MaManHinh.DANH_MUC_THU_CUNG),
                                    layMuc("Nhân viên", MaManHinh.DANH_MUC_NHAN_VIEN),
                                    layMuc("Sản phẩm", MaManHinh.DANH_MUC_SAN_PHAM),
                                    layMuc("Dịch vụ Spa", MaManHinh.DANH_MUC_DICH_VU_SPA),
                                    layMuc("Phòng lưu trú", MaManHinh.DANH_MUC_PHONG_LUU_TRU),
                                    layMuc("Nhà cung cấp", MaManHinh.DANH_MUC_NHA_CUNG_CAP),
                                    layMuc("Khuyến mãi", MaManHinh.DANH_MUC_KHUYEN_MAI),
                                    layMuc("Điểm thưởng và voucher", MaManHinh.TICH_DIEM_VOUCHER))),
                    // 3. Tìm kiếm: chỉ xem
                    new Nhom(
                            'T',
                            "Tìm kiếm",
                            List.of(
                                    layMuc("Hóa đơn", MaManHinh.TIM_KIEM_HOA_DON, "F3"),
                                    layMuc("Lịch sử khách hàng", MaManHinh.TRA_CUU_KHACH_HANG),
                                    layMuc("Tồn kho theo lô", MaManHinh.TRA_CUU_LO_HANG),
                                    layMuc("Phiếu Spa trong ngày", MaManHinh.TRA_CUU_LICH_SPA),
                                    layMuc("Sơ đồ phòng lưu trú", MaManHinh.SO_DO_PHONG_LUU_TRU),
                                    PHAN_CACH,
                                    layMuc("Lịch làm việc", MaManHinh.TRA_CUU_LICH_LAM_VIEC),
                                    layMuc("Lịch làm việc cá nhân", MaManHinh.LICH_CA_NHAN))),
                    // 4. Xử lý: nghiệp vụ
                    new Nhom(
                            'X',
                            "Xử lý",
                            List.of(
                                    layMuc("Lập hóa đơn", MaManHinh.LAP_HOA_DON, "F2"),
                                    layMuc("Đổi hàng", MaManHinh.DOI_TRA_HANG_HOA),
                                    layMuc("Lập phiếu Spa", MaManHinh.QUAN_LY_SPA),
                                    layMuc("Quản lý lưu trú", MaManHinh.QUAN_LY_LUU_TRU),
                                    PHAN_CACH,
                                    layMuc("Lập phiếu nhập kho", MaManHinh.NHAP_KHO),
                                    layMuc("Xuất hủy hàng hóa", MaManHinh.XUAT_HUY_KHO),
                                    layMuc("Kiểm kê tồn kho", MaManHinh.KIEM_KE_TON_KHO),
                                    PHAN_CACH,
                                    layMuc("Ca thu ngân", MaManHinh.CA_THU_NGAN),
                                    layMuc("Phân lịch nhân viên", MaManHinh.PHAN_LICH_NHAN_VIEN),
                                    layMuc("Phê duyệt yêu cầu", MaManHinh.PHE_DUYET_YEU_CAU))),
                    // 5. Thống kê: chỉ xem và xuất
                    new Nhom(
                            'K',
                            "Thống kê",
                            List.of(
                                    layMuc("Doanh thu", MaManHinh.THONG_KE_DOANH_THU),
                                    layMuc("Hàng hóa", MaManHinh.THONG_KE_HANG_HOA),
                                    layMuc("Dịch vụ Spa", MaManHinh.THONG_KE_SPA),
                                    layMuc("Lưu trú", MaManHinh.THONG_KE_LUU_TRU),
                                    layMuc("Khách hàng", MaManHinh.THONG_KE_KHACH_HANG),
                                    layMuc("Công nợ nhà cung cấp", MaManHinh.THONG_KE_CONG_NO_NCC),
                                    layMuc("Nhân viên", MaManHinh.THONG_KE_NHAN_VIEN))));

    private CauTrucMenu() {}

    public static List<Nhom> layCacNhom() {
        return CAC_NHOM;
    }
}
