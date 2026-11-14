package com.petstation.dao.luutru;

import com.petstation.dto.HotelDTO;
import com.petstation.model.chung.PhuongThuc;
import com.petstation.model.luutru.HopDongLuuTru;
import com.petstation.model.luutru.LichSuThuCocHotel;
import com.petstation.model.luutru.PhongHotel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HopDongLuuTruDAO {
    Optional<HopDongLuuTru> timTheoMa(String maHopDong);

    List<PhongHotel> layPhongDangDon();

    int capNhatNhanPhong(String maHopDong);

    List<ThongTinPhongDat> timDatPhong(String maPhong);

    long demPhongDatTrung(String maPhong, LocalDateTime ketThuc, LocalDateTime batDau);

    long demThuCungDatTrung(String maThuCung, LocalDateTime ketThuc, LocalDateTime batDau);

    long demPhongDatTrungBoQua(
            String maPhong, LocalDateTime ketThuc, LocalDateTime batDau, String boQuaMa);

    long demThuCungDatTrungBoQua(
            String maThuCung, LocalDateTime ketThuc, LocalDateTime batDau, String boQuaMa);

    List<HotelDTO.ThongTinDatPhong> layDatPhongSua(String maHopDong);

    int capNhatDatPhong(
            String maKhachHang,
            String maThuCung,
            String maPhong,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            long giaDem,
            long cocYeuCau,
            String ghiChu,
            String maHopDong);

    int huyDatPhong(String lyDo, String maHopDong);

    String themDatPhong(
            String maHopDong,
            String maKhachHang,
            String maThuCung,
            String maPhong,
            LocalDateTime ngayVao,
            LocalDateTime ngayRaDuKien,
            long tienCoc,
            long giaDem,
            String ghiChu);

    String layNhanPhong(String maHopDong);

    int danhDauPhongDangO(String maPhong);

    long demNhatKy(String maHopDong);

    int capNhatNhatKy(
            String maHopDong,
            String maNhanVien,
            String anUong,
            String daThucHien,
            String tinhTrang);

    List<ThongTinThanhToanHotel> timHotelConPhaiThu(String maHopDong);

    List<HotelDTO.DongNhatKyChiTiet> layNhatKyChiTiet(String maHopDong);

    int danhDauPhongKhaDung(String maPhong);

    int datMucCoc(long muc, String maHopDong);

    List<HotelDTO.DongLichSuCoc> layLichSuThuCoc(String maHopDong);

    List<MucCocHopDong> layMucCoc(String maHopDong);

    List<TrangThaiCocHopDong> khoaHopDongThuCoc(String maHopDong);

    long demKhoaThuCoc(String maHopDong, String tienToKhoa);

    int ghiNhanTongCoc(long tongCoc, String maNhanVien, String maPhien, String maHopDong);

    List<ThongTinDoiCoc> khoaHopDongDoiCoc(String maHopDong);

    int dieuChinhMucCoc(long muc, String lyDo, String maHopDong);

    void ghiThuCoc(
            String maHopDong,
            String maPhien,
            String maNhanVienThu,
            String khoa,
            PhuongThuc phuongThuc,
            long soTien,
            long khachDua);

    List<LichSuThuCocHotel> layLichSuTheoHopDong(String maHopDong);

    final class ThongTinPhongDat {
        public final String maPhong;
        public final long giaDem;
        public final String trangThai;
        public final String loaiPhong;
        public final int sucChua;

        public ThongTinPhongDat(
                String maPhong, long giaDem, String trangThai, String loaiPhong, int sucChua) {
            this.maPhong = maPhong;
            this.giaDem = giaDem;
            this.trangThai = trangThai;
            this.loaiPhong = loaiPhong;
            this.sucChua = sucChua;
        }
    }

    final class ThongTinThanhToanHotel {
        public final LocalDateTime ngayVao;
        public final LocalDateTime ngayRaDuKien;
        public final long giaDem;
        public final long tienCocDaThu;

        public ThongTinThanhToanHotel(
                LocalDateTime ngayVao, LocalDateTime ngayRaDuKien, long giaDem, long tienCocDaThu) {
            this.ngayVao = ngayVao;
            this.ngayRaDuKien = ngayRaDuKien;
            this.giaDem = giaDem;
            this.tienCocDaThu = tienCocDaThu;
        }
    }

    final class MucCocHopDong {
        public final long cocYeuCau;
        public final long cocDaThu;

        public MucCocHopDong(long cocYeuCau, long cocDaThu) {
            this.cocYeuCau = cocYeuCau;
            this.cocDaThu = cocDaThu;
        }
    }

    final class TrangThaiCocHopDong {
        public final String trangThai;
        public final long cocYeuCau;
        public final long cocDaThu;

        public TrangThaiCocHopDong(String trangThai, long cocYeuCau, long cocDaThu) {
            this.trangThai = trangThai;
            this.cocYeuCau = cocYeuCau;
            this.cocDaThu = cocDaThu;
        }
    }

    final class ThongTinDoiCoc {
        public final LocalDateTime ngayVao;
        public final LocalDateTime ngayRaDuKien;
        public final long giaDem;
        public final long cocDaThu;
        public final String trangThai;

        public ThongTinDoiCoc(
                LocalDateTime ngayVao,
                LocalDateTime ngayRaDuKien,
                long giaDem,
                long cocDaThu,
                String trangThai) {
            this.ngayVao = ngayVao;
            this.ngayRaDuKien = ngayRaDuKien;
            this.giaDem = giaDem;
            this.cocDaThu = cocDaThu;
            this.trangThai = trangThai;
        }
    }
}
