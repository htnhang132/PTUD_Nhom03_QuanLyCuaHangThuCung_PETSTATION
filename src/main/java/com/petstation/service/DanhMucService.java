package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcDanhMucDAO;
import com.petstation.model.LoaiDanhMuc;
import com.petstation.model.Quyen;

import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho danh mục dịch vụ. */
public final class DanhMucService {
    /** Điều phối nghiệp vụ và kiểm tra dữ liệu cho các dòng. */
    public static final class CacDong extends ArrayList<Object[]> {
        /** Loại. */
        public final LoaiDanhMuc loai;

        /**
         * Khởi tạo CacDong với các giá trị được cung cấp.
         *
         * @param loai loại
         */
        CacDong(LoaiDanhMuc loai) {
            this.loai = loai;
            lamMoi();
        }

        /** Làm mới trong CacDong. */
        public void lamMoi() {
            super.clear();
            super.addAll(JdbcDanhMucDAO.nap(loai));
        }
    }

    /** Giá các bộ lắng nghe. */
    private static final List<Runnable> GIA_CAC_BO_LANG_NGHE = new ArrayList<>();

    /** Khởi tạo DanhMucService với các giá trị được cung cấp. */
    private DanhMucService() {}

    /**
     * Lấy khách hàng trong DanhMucService.
     *
     * @return kết quả lấy khách hàng
     */
    public static CacDong layKhachHang() {
        return new CacDong(LoaiDanhMuc.CUSTOMERS);
    }

    /**
     * Lấy thú cưng trong DanhMucService.
     *
     * @return kết quả lấy thú cưng
     */
    public static CacDong layThuCung() {
        return new CacDong(LoaiDanhMuc.PETS);
    }

    /**
     * Lấy nhân viên trong DanhMucService.
     *
     * @return kết quả lấy nhân viên
     */
    public static CacDong layNhanVien() {
        return new CacDong(LoaiDanhMuc.STAFF);
    }

    /**
     * Lấy tài khoản trong DanhMucService.
     *
     * @return kết quả lấy tài khoản
     */
    public static CacDong layTaiKhoan() {
        return new CacDong(LoaiDanhMuc.ACCOUNTS);
    }

    /**
     * Lấy sản phẩm trong DanhMucService.
     *
     * @return kết quả lấy sản phẩm
     */
    public static CacDong laySanPham() {
        return new CacDong(LoaiDanhMuc.PRODUCTS);
    }

    /**
     * Lấy nhà cung cấp trong DanhMucService.
     *
     * @return kết quả lấy nhà cung cấp
     */
    public static CacDong layNhaCungCap() {
        return new CacDong(LoaiDanhMuc.SUPPLIERS);
    }

    /**
     * Lấy khuyến mãi trong DanhMucService.
     *
     * @return kết quả lấy khuyến mãi
     */
    public static CacDong layKhuyenMai() {
        return new CacDong(LoaiDanhMuc.PROMOTIONS);
    }

    /**
     * Lấy voucher trong DanhMucService.
     *
     * @return kết quả lấy voucher
     */
    public static CacDong layVoucher() {
        return new CacDong(LoaiDanhMuc.VOUCHERS);
    }

    /**
     * Lấy dịch vụ Spa trong DanhMucService.
     *
     * @return kết quả lấy dịch vụ Spa
     */
    public static CacDong layDichVuSpa() {
        return new CacDong(LoaiDanhMuc.SPA);
    }

    /**
     * Lấy phòng trong DanhMucService.
     *
     * @return kết quả lấy phòng
     */
    public static CacDong layPhong() {
        return new CacDong(LoaiDanhMuc.ROOMS);
    }

    /**
     * Lấy lựa chọn khách hàng trong DanhMucService.
     *
     * @return kết quả lấy lựa chọn khách hàng
     */
    public static List<String> layLuaChonKhachHang() {
        return layKhachHang().stream()
                .filter(dongDuLieu -> "Đang hoạt động".equals(dongDuLieu[8]))
                .map(dongDuLieu -> dongDuLieu[0] + " - " + dongDuLieu[1])
                .toList();
    }

    /**
     * Lấy lựa chọn nhân viên trong DanhMucService.
     *
     * @return kết quả lấy lựa chọn nhân viên
     */
    public static List<String> layLuaChonNhanVien() {
        return layNhanVien().stream()
                .filter(dongDuLieu -> !"Nghỉ việc".equals(dongDuLieu[6]))
                .map(dongDuLieu -> dongDuLieu[0] + " - " + dongDuLieu[1])
                .toList();
    }

    /**
     * Lấy mã từ lựa chọn trong DanhMucService.
     *
     * @param luaChon lựa chọn
     * @return kết quả lấy mã từ lựa chọn
     */
    public static String layMaTuLuaChon(Object luaChon) {
        return chuyenThanhChuoi(luaChon).split(" - ", 2)[0];
    }

    /**
     * Thêm bộ lắng nghe giá trong DanhMucService.
     *
     * @param boLangNghe bộ lắng nghe
     */
    public static void themBoLangNgheGia(Runnable boLangNghe) {
        GIA_CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    /**
     * Lưu trong DanhMucService.
     *
     * @param cacDong các dòng
     * @param truoc trước
     * @param giaTri giá trị
     */
    public static void luu(List<Object[]> cacDong, Object[] truoc, Object[] giaTri) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (!(cacDong instanceof CacDong danhMuc))
            throw new IllegalArgumentException("Danh mục chưa nối SQL Server.");
        // Quyền.
        Quyen quyen =
                switch (danhMuc.loai) {
                    case CUSTOMERS, VOUCHERS -> Quyen.CUSTOMER_MANAGE;
                    case PETS -> Quyen.PET_MANAGE;
                    case STAFF, ACCOUNTS -> Quyen.ACCOUNT_MANAGE;
                    case PRODUCTS -> Quyen.PRODUCT_MANAGE;
                    case SUPPLIERS -> Quyen.SUPPLIER_MANAGE;
                    case PROMOTIONS -> Quyen.PROMOTION_MANAGE;
                    case SPA -> Quyen.SPA_MANAGE;
                    case ROOMS -> Quyen.HOTEL_MANAGE;
                };
        yeuCau(quyen);

        thucHienGiaoDich(
                ketNoi -> {
                    if (danhMuc.loai == LoaiDanhMuc.VOUCHERS) {
                        yeuCau(Quyen.PROMOTION_MANAGE);
                        String ma = String.valueOf(truoc == null ? giaTri[0] : truoc[0]);
                        var rangBuoc =
                                com.petstation.dao.impl.JdbcTichDiemDAO.layRangBuocVoucher(ma);
                        if (!rangBuoc.isEmpty() && rangBuoc.get(0)[1] != null)
                            throw new IllegalArgumentException(
                                    "Voucher đổi điểm giữ nguyên mệnh giá, hạn, chủ và một lượt"
                                            + " dùng; không sửa qua danh mục.");
                        if (giaTri.length < 9)
                            throw new IllegalArgumentException("Cần chọn khách sở hữu voucher.");
                        if (rangBuoc.isEmpty() && String.valueOf(giaTri[7]).isBlank())
                            throw new IllegalArgumentException(
                                    "Voucher mới phải gắn với một khách hàng.");
                        if ((rangBuoc.isEmpty() || rangBuoc.get(0)[2] != null)
                                && !"1".equals(String.valueOf(giaTri[3])))
                            throw new IllegalArgumentException(
                                    "Voucher cá nhân chỉ có một lượt dùng.");
                        if (!rangBuoc.isEmpty() && ((Number) rangBuoc.get(0)[0]).longValue() > 0)
                            throw new IllegalArgumentException(
                                    "Voucher đã dùng chỉ được tra cứu, không sửa hoặc phát hành"
                                            + " lại.");
                    }
                    if (danhMuc.loai == LoaiDanhMuc.PRODUCTS && giaTri.length >= 17) {
                        String anh = String.valueOf(giaTri[10]);
                        if (!anh.isBlank()
                                && !com.petstation.util.TaiAnhSanPham.kiemTraDuongDan(anh))
                            throw new IllegalArgumentException(
                                    "Ảnh sản phẩm phải nằm trong images/products.");
                        int[] chiSo = {11, 12, 13, 14};
                        int[] gioiHan = {100, 100, 100, 1000};
                        for (int i = 0; i < chiSo.length; i++)
                            if (String.valueOf(giaTri[chiSo[i]]).length() > gioiHan[i])
                                throw new IllegalArgumentException(
                                        "Thông tin chi tiết sản phẩm quá dài.");
                    }
                    if (danhMuc.loai == LoaiDanhMuc.PRODUCTS
                            && layPhien().docVaiTro() == com.petstation.model.VaiTro.STORE_MANAGER)
                        yeuCau(Quyen.PRICE_APPROVE);
                    if (danhMuc.loai == LoaiDanhMuc.PRODUCTS
                            && layPhien().docVaiTro()
                                    != com.petstation.model.VaiTro.STORE_MANAGER) {
                        var sp =
                                com.petstation.dao.impl.JdbcDeXuatGiaDAO.timSanPham(
                                        String.valueOf(giaTri[0]));
                        long giaCu = sp.isEmpty() ? 0 : ((Number) sp.get(0)[1]).longValue();
                        if (com.petstation.util.TienTe.doc(String.valueOf(giaTri[4])) != giaCu)
                            throw new PhanQuyenException(
                                    "Nhân viên kho dùng nút Đề xuất giá; chỉ quản lý được áp dụng"
                                            + " giá bán.");
                        if (sp.isEmpty()) giaTri[9] = "Ngừng kinh doanh";
                    }
                    JdbcDanhMucDAO.luu(
                            danhMuc.loai,
                            truoc,
                            giaTri,
                            layIdTaiKhoan(),
                            layPhien().docVaiTro().name());
                    NhatKyHeThongService.ghiNhatKy(
                            truoc == null ? "Thêm" : "Sửa", danhMuc.loai + " " + giaTri[0]);
                    return null;
                });
        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
        if (danhMuc.loai == LoaiDanhMuc.PRODUCTS)
            for (Runnable boLangNghe : List.copyOf(GIA_CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    /**
     * Lấy lịch sử giá trong DanhMucService.
     *
     * @return kết quả lấy lịch sử giá
     */
    public static List<Object[]> layLichSuGia() {
        return JdbcDanhMucDAO.layLichSuGia();
    }

    /**
     * Lấy id trong DanhMucService.
     *
     * @param bang bảng
     * @param khoa khóa
     * @param ma mã
     * @return kết quả lấy id
     */
    public static long layId(String bang, String khoa, String ma) {
        return JdbcDanhMucDAO.layId(bang, khoa, ma);
    }

    /**
     * Lấy khóa danh sách tài khoản của trong DanhMucService.
     *
     * @param maNhanVien mã nhân viên
     * @return kết quả lấy khóa danh sách tài khoản của
     */
    public static int khoaCacTaiKhoanCua(String maNhanVien) {
        yeuCau(Quyen.ACCOUNT_MANAGE);
        return JdbcDanhMucDAO.khoaCacTaiKhoanCua(maNhanVien);
    }

    /**
     * Đặt lại mật khẩu trong DanhMucService.
     *
     * @param tenDangNhap tên đăng nhập
     * @param matKhau mật khẩu
     */
    public static void datLaiMatKhau(String tenDangNhap, String matKhau) {
        yeuCau(Quyen.ACCOUNT_MANAGE);
        JdbcDanhMucDAO.datLaiMatKhau(tenDangNhap, matKhau);
    }

    public static void xoaBoLangNghe() {
        GIA_CAC_BO_LANG_NGHE.clear();
    }
}
