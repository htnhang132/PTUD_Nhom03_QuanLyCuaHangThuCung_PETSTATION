package com.petstation.dao.pheduyet;

import com.petstation.model.pheduyet.YeuCauPheDuyet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface YeuCauPheDuyetDAO {
    Optional<YeuCauPheDuyet> timTheoMa(String maYeuCau);

    final class ThongTinYeuCau {
        private final String maYeuCau;
        private final String loai;
        private final String nguoiGui;
        private final LocalDateTime guiLuc;
        private final String lyDo;
        private final String noiDungTruoc;
        private final String noiDungSau;
        private final String trangThai;
        private final String nguoiXuLy;
        private final String phanHoi;

        public ThongTinYeuCau(
                String maYeuCau,
                String loai,
                String nguoiGui,
                LocalDateTime guiLuc,
                String lyDo,
                String noiDungTruoc,
                String noiDungSau,
                String trangThai,
                String nguoiXuLy,
                String phanHoi) {
            this.maYeuCau = maYeuCau;
            this.loai = loai;
            this.nguoiGui = nguoiGui;
            this.guiLuc = guiLuc;
            this.lyDo = lyDo;
            this.noiDungTruoc = noiDungTruoc;
            this.noiDungSau = noiDungSau;
            this.trangThai = trangThai;
            this.nguoiXuLy = nguoiXuLy;
            this.phanHoi = phanHoi;
        }

        public String maYeuCau() {
            return maYeuCau;
        }

        public String loai() {
            return loai;
        }

        public String nguoiGui() {
            return nguoiGui;
        }

        public LocalDateTime guiLuc() {
            return guiLuc;
        }

        public String lyDo() {
            return lyDo;
        }

        public String noiDungTruoc() {
            return noiDungTruoc;
        }

        public String noiDungSau() {
            return noiDungSau;
        }

        public String trangThai() {
            return trangThai;
        }

        public String nguoiXuLy() {
            return nguoiXuLy;
        }

        public String phanHoi() {
            return phanHoi;
        }
    }

    final class ThongTinDoiTuong {
        private final String loaiDoiTuong;
        private final String maDoiTuong;
        private final String payload;

        public ThongTinDoiTuong(String loaiDoiTuong, String maDoiTuong, String payload) {
            this.loaiDoiTuong = loaiDoiTuong;
            this.maDoiTuong = maDoiTuong;
            this.payload = payload;
        }

        public String loaiDoiTuong() {
            return loaiDoiTuong;
        }

        public String maDoiTuong() {
            return maDoiTuong;
        }

        public String payload() {
            return payload;
        }
    }

    long demSoLuongDangCho();

    int capNhatTuChoi(String maPhieuXuat);

    int tuChoiBienBanKiemKe(String maBienBan);

    long demApDungPheDuyet(String maHoaDon);

    int capNhatApDungPheDuyet(String ghiChu, String maHoaDon);

    int capNhatGuiThaoTac(
            String maYeuCau,
            String loai,
            String maNhanVienGui,
            String loaiDoiTuong,
            String maDoiTuong,
            String lyDo,
            String noiDungTruoc,
            String noiDungSau,
            String payload);

    List<ThongTinYeuCau> timTatCa();

    Optional<ThongTinDoiTuong> timDuyet(String maYeuCau);

    int capNhatDuyet(String maNhanVienXuLy, String maYeuCau);

    Optional<ThongTinDoiTuong> timTuChoi(String maYeuCau);

    int tuChoiPhieuDoiTra(String maPhieuDoiTra);

    int danhDauYeuCauTuChoi(String maNhanVienXuLy, String lyDo, String maYeuCau);

    int yeuCauBoSung(String maNhanVienXuLy, String noiDung, String maYeuCau);
}
