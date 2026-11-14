package com.petstation.dao.pheduyet;

import com.petstation.model.pheduyet.DeXuatGia;

import java.util.Optional;

public interface DeXuatGiaDAO {
    Optional<DeXuatGia> timDeXuatTheoMa(String maYeuCau);

    final class ThongTinSanPham {
        private final String maSanPham;
        private final long giaBan;

        public ThongTinSanPham(String maSanPham, long giaBan) {
            this.maSanPham = maSanPham;
            this.giaBan = giaBan;
        }

        public String maSanPham() {
            return maSanPham;
        }

        public long giaBan() {
            return giaBan;
        }
    }

    final class ThongTinDeXuat {
        private final String maSanPham;
        private final long giaCu;
        private final long giaMoi;
        private final long giaHienTai;

        public ThongTinDeXuat(String maSanPham, long giaCu, long giaMoi, long giaHienTai) {
            this.maSanPham = maSanPham;
            this.giaCu = giaCu;
            this.giaMoi = giaMoi;
            this.giaHienTai = giaHienTai;
        }

        public String maSanPham() {
            return maSanPham;
        }

        public long giaCu() {
            return giaCu;
        }

        public long giaMoi() {
            return giaMoi;
        }

        public long giaHienTai() {
            return giaHienTai;
        }
    }

    Optional<ThongTinSanPham> timSanPham(String ma);

    String them(String maSanPham, String maNhanVien, long cu, long moi, String lyDo);

    Optional<ThongTinDeXuat> timChoDuyet(String maDoiTuongHoacYeuCau);

    void apDung(String maDoiTuongHoacYeuCau, String maSanPham, long gia, String maNhanVien);

    void tuChoi(String maDoiTuongHoacYeuCau);
}
