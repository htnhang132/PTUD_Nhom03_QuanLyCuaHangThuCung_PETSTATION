package com.petstation.service.khachhang;

import com.petstation.dao.khachhang.TichDiemDAO;
import com.petstation.dao.khachhang.impl.TichDiemDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.TrangThaiVoucher;
import com.petstation.model.khachhang.Voucher;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Tầng xử lý nghiệp vụ Tích điểm & Voucher khách hàng (Service)
public final class TichDiemService {
    public static final long SO_TIEN_MOI_DIEM = 10_000;
    public static final int DIEM_DOI_VOUCHER = 200;

    private final TichDiemDAO tichDiemDAO;
    private final NhatKyHeThongService nhatKyHeThongService;
    private final PhienDangNhapService phienDangNhapService;

    public TichDiemService() {
        this(new TichDiemDAOImpl());
    }

    public TichDiemService(TichDiemDAO tichDiemDAO) {
        this(tichDiemDAO, NhatKyHeThongService.macDinh(), PhienDangNhapService.macDinh());
    }

    public TichDiemService(
            TichDiemDAO tichDiemDAO,
            NhatKyHeThongService nhatKyHeThongService,
            PhienDangNhapService phienDangNhapService) {
        this.tichDiemDAO = Objects.requireNonNull(tichDiemDAO, "tichDiemDAO không được null");
        this.nhatKyHeThongService =
                Objects.requireNonNull(
                        nhatKyHeThongService, "nhatKyHeThongService không được null");
        this.phienDangNhapService =
                Objects.requireNonNull(
                        phienDangNhapService, "phienDangNhapService không được null");
    }

    // 1. KIỂM TRA QUYỀN & RÀNG BUỘC CHỈNH SỬA VOUCHER TRÊN DANH MỤC

    // Kiểm tra một mã voucher có được phép chỉnh sửa trên DanhMucVoucherPanel hay
    // không
    public boolean coTheSuaDanhMuc(String ma) {
        if (!phienDangNhapService.layPhien().kiemTraCo(Quyen.QUAN_LY_KHUYEN_MAI)) return false;
        TichDiemDAO.RangBuocVoucher rb = tichDiemDAO.layRangBuocVoucher(ma);
        return rb != null && rb.khoaDoiDiem == null && rb.daDung == 0;
    }

    // 2. NGHIỆP VỤ ĐỔI ĐIỂM TÍCH LŨY LẤY VOUCHER SPA (200 ĐIỂM = 150.000Đ)

    // Đổi 200 điểm tích lũy của khách hàng lấy 1 mã Voucher Spa trị giá 150.000đ
    // (hạn 60 ngày).
    public String doiVoucher(String maKhachHang, String khoa) {
        phienDangNhapService.yeuCau(Quyen.BAN_HANG);
        if (khoa == null || khoa.isBlank() || khoa.length() > 80)
            throw new IllegalArgumentException("Thiếu khóa đổi voucher.");
        return QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            TichDiemDAO.ThongTinKhachHangDiem khach =
                                    tichDiemDAO.timKhachHang(maKhachHang);
                            if (khach == null || !khach.dangHoatDong)
                                throw new IllegalArgumentException("Khách hàng không hoạt động.");
                            String maKh = khach.maKhachHang;
                            TichDiemDAO.ThongTinTheoKhoa cu = tichDiemDAO.timTheoKhoa(khoa);
                            if (cu != null) {
                                if (!Objects.equals(cu.maKhachHang, maKh))
                                    throw new IllegalArgumentException(
                                            "Khóa đổi voucher đã thuộc khách khác.");
                                return cu.maVoucher;
                            }
                            if (khach.diemTichLuy < DIEM_DOI_VOUCHER)
                                throw new IllegalArgumentException(
                                        "Cần đủ 200 điểm để đổi voucher Spa 150.000đ.");
                            String ma = tichDiemDAO.taoVoucher(maKh, khoa);
                            nhatKyHeThongService.ghiNhatKy("Đổi voucher", maKhachHang + " / " + ma);
                            return ma;
                        });
    }

    // 3. TRA CỨU & KIỂM TRA ĐIỀU KIỆN ÁP DỤNG VOUCHER KHI THANH TOÁN

    // Kiểm tra tính hợp lệ của mã voucher khi áp dụng vào hóa đơn
    public long kiemTraVoucher(String ma, String khachHang, String nguon, long tamTinh) {
        phienDangNhapService.layPhien();
        TichDiemDAO.ThongTinVoucher v = tichDiemDAO.timVoucher(ma);
        if (v == null) throw new IllegalArgumentException("Mã voucher không tồn tại.");
        if (!TrangThaiVoucher.CON_HAN.maDB().equals(v.trangThai) || v.daDung >= v.soLuong)
            throw new IllegalArgumentException("Voucher đã dùng hết lượt hoặc ngừng áp dụng.");
        if (!v.hieuLucDen.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Voucher đã hết hạn.");
        if (!v.maKhachHang.isEmpty() && !v.maKhachHang.equals(khachHang))
            throw new IllegalArgumentException("Voucher không thuộc khách hàng đang chọn.");
        if (v.chiApDungSpa && !"SPA".equals(nguon))
            throw new IllegalArgumentException("Voucher chỉ áp dụng cho thanh toán Spa.");
        if (tamTinh < v.donToiThieu)
            throw new IllegalArgumentException("Chưa đủ giá trị đơn tối thiểu của voucher.");
        return v.menhGia;
    }

    // Lấy danh sách các mã voucher của một khách hàng cụ thể
    public List<Voucher> layVoucher(String maKhachHang) {
        phienDangNhapService.yeuCau(Quyen.BAN_HANG);
        return tichDiemDAO.layCuaKhach(maKhachHang);
    }
}
