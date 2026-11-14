package com.petstation.dao.khachhang;

import com.petstation.model.khachhang.Voucher;

import java.time.LocalDateTime;
import java.util.List;

// Giao diện DAO phục vụ nghiệp vụ Tích điểm thành viên & Quản lý/Áp dụng Voucher.
public interface TichDiemDAO {

    final class RangBuocVoucher {
        public final long daDung;
        public final String khoaDoiDiem;
        public final String maKhachHang;

        public RangBuocVoucher(long daDung, String khoaDoiDiem, String maKhachHang) {
            this.daDung = daDung;
            this.khoaDoiDiem = khoaDoiDiem;
            this.maKhachHang = maKhachHang;
        }
    }

    final class ThongTinVoucher {
        public final long menhGia;
        public final LocalDateTime hieuLucDen;
        public final long daDung;
        public final long soLuong;
        public final long donToiThieu;
        public final String trangThai;
        public final boolean chiApDungSpa;
        public final String maKhachHang;

        public ThongTinVoucher(
                long menhGia,
                LocalDateTime hieuLucDen,
                long daDung,
                long soLuong,
                long donToiThieu,
                String trangThai,
                boolean chiApDungSpa,
                String maKhachHang) {
            this.menhGia = menhGia;
            this.hieuLucDen = hieuLucDen;
            this.daDung = daDung;
            this.soLuong = soLuong;
            this.donToiThieu = donToiThieu;
            this.trangThai = trangThai;
            this.chiApDungSpa = chiApDungSpa;
            this.maKhachHang = maKhachHang;
        }
    }

    final class ThongTinKhachHangDiem {
        public final String maKhachHang;
        public final long diemTichLuy;
        public final boolean dangHoatDong;

        public ThongTinKhachHangDiem(String maKhachHang, long diemTichLuy, boolean dangHoatDong) {
            this.maKhachHang = maKhachHang;
            this.diemTichLuy = diemTichLuy;
            this.dangHoatDong = dangHoatDong;
        }
    }

    final class ThongTinTheoKhoa {
        public final String maVoucher;
        public final String maKhachHang;

        public ThongTinTheoKhoa(String maVoucher, String maKhachHang) {
            this.maVoucher = maVoucher;
            this.maKhachHang = maKhachHang;
        }
    }

    // 1. KIỂM TRA RÀNG BUỘC & TRA CỨU VOUCHER

    // Lấy thông tin số lượt đã dùng, khóa đổi điểm và chủ sở hữu của mã voucher
    RangBuocVoucher layRangBuocVoucher(String ma);

    // Tìm thông tin chi tiết của một mã voucher để kiểm tra điều kiện áp dụng khi thanh toán
    ThongTinVoucher timVoucher(String ma);

    // Lấy danh sách tất cả voucher thuộc sở hữu của một khách hàng
    List<Voucher> layCuaKhach(String ma);

    // 2. ĐỔI ĐIỂM TÍCH LŨY LẤY VOUCHER SPA

    // Tìm thông tin điểm tích lũy và trạng thái hoạt động của khách hàng (có khóa dòng UPDLOCK)
    ThongTinKhachHangDiem timKhachHang(String ma);

    // Tìm voucher đã phát hành theo khóa đổi điểm (tránh phát hành trùng lặp)
    ThongTinTheoKhoa timTheoKhoa(String khoa);

    // Tạo mới voucher Spa 150.000đ và trừ 200 điểm tích lũy của khách hàng
    String taoVoucher(String maKhachHang, String khoa);
}
