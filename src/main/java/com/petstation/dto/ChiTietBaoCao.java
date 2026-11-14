package com.petstation.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChiTietBaoCao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String[] cot;
    private List<Object[]> dong;
    private int cotNhom;

    public ChiTietBaoCao() {}

    public ChiTietBaoCao(String[] cot, List<Object[]> dong, int cotNhom) {
        this.cot = cot;
        this.dong = dong;
        this.cotNhom = cotNhom;
    }

    public String[] getCot() {
        return this.cot;
    }

    public List<Object[]> getDong() {
        return this.dong;
    }

    public int getCotNhom() {
        return this.cotNhom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietBaoCao that = (ChiTietBaoCao) o;
        return Arrays.equals(cot, that.cot)
                && Objects.equals(dong, that.dong)
                && this.cotNhom == that.cotNhom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(cot), dong, cotNhom);
    }

    @Override
    public String toString() {
        return "ChiTietBaoCao{"
                + "cot="
                + Arrays.toString(cot)
                + ", "
                + "dong="
                + dong
                + ", "
                + "cotNhom="
                + cotNhom
                + "}";
    }

    public interface HangBaoCao {
        Object[] thanhHang();
    }

    public static final class DoanhThuTheoNgay implements HangBaoCao {
        private final LocalDate ngay;
        private final double tongTien;

        public DoanhThuTheoNgay(LocalDate ngay, double tongTien) {
            this.ngay = ngay;
            this.tongTien = tongTien;
        }

        public LocalDate getNgay() {
            return ngay;
        }

        public double getTongTien() {
            return tongTien;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {ngay, tongTien};
        }
    }

    public static final class ThongKeNhom implements HangBaoCao {
        private final String nhan;
        private final double tongTien;
        private final int soHoaDon;

        public ThongKeNhom(String nhan, double tongTien, int soHoaDon) {
            this.nhan = nhan;
            this.tongTien = tongTien;
            this.soHoaDon = soHoaDon;
        }

        public double getTongTien() {
            return tongTien;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {nhan, tongTien, soHoaDon};
        }
    }

    public static final class DoanhThuHoanTat implements HangBaoCao {
        private final LocalDate ngay;
        private final int soDon;
        private final double doanhThuGoc;
        private final double giamGia;
        private final double hoanTra;
        private final double doanhThuThuan;
        private final double loiNhuanGop;

        public DoanhThuHoanTat(
                LocalDate ngay,
                int soDon,
                double doanhThuGoc,
                double giamGia,
                double hoanTra,
                double doanhThuThuan,
                double loiNhuanGop) {
            this.ngay = ngay;
            this.soDon = soDon;
            this.doanhThuGoc = doanhThuGoc;
            this.giamGia = giamGia;
            this.hoanTra = hoanTra;
            this.doanhThuThuan = doanhThuThuan;
            this.loiNhuanGop = loiNhuanGop;
        }

        public int getSoDon() {
            return soDon;
        }

        public double getDoanhThuThuan() {
            return doanhThuThuan;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {
                ngay, soDon, doanhThuGoc, giamGia, hoanTra, doanhThuThuan, loiNhuanGop
            };
        }
    }

    public static final class SanPhamBanChay implements HangBaoCao {
        private final String maSanPham;
        private final String tenSanPham;
        private final long soBanSauTra;
        private final double doanhThu;

        public SanPhamBanChay(
                String maSanPham, String tenSanPham, long soBanSauTra, double doanhThu) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.soBanSauTra = soBanSauTra;
            this.doanhThu = doanhThu;
        }

        public String getTenSanPham() {
            return tenSanPham;
        }

        public long getSoBanSauTra() {
            return soBanSauTra;
        }

        public double getDoanhThu() {
            return doanhThu;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maSanPham, tenSanPham, soBanSauTra, doanhThu};
        }
    }

    public static final class TonKhoHienTai implements HangBaoCao {
        private final String maSanPham;
        private final String tenSanPham;
        private final int tonKho;
        private final int tonToiThieu;
        private final double giaNhapGanNhat;

        public TonKhoHienTai(
                String maSanPham,
                String tenSanPham,
                int tonKho,
                int tonToiThieu,
                double giaNhapGanNhat) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.tonKho = tonKho;
            this.tonToiThieu = tonToiThieu;
            this.giaNhapGanNhat = giaNhapGanNhat;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maSanPham, tenSanPham, tonKho, tonToiThieu, giaNhapGanNhat};
        }
    }

    public static final class LoSapHetHan implements HangBaoCao {
        private final String maSanPham;
        private final String tenSanPham;
        private final String soLo;
        private final LocalDate hanSuDung;
        private final int soLuongTon;

        public LoSapHetHan(
                String maSanPham,
                String tenSanPham,
                String soLo,
                LocalDate hanSuDung,
                int soLuongTon) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.soLo = soLo;
            this.hanSuDung = hanSuDung;
            this.soLuongTon = soLuongTon;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maSanPham, tenSanPham, soLo, hanSuDung, soLuongTon};
        }
    }

    public static final class BienDongKho implements HangBaoCao {
        private final String maSanPham;
        private final String tenSanPham;
        private final String loaiGiaoDich;
        private final int soLuong;

        public BienDongKho(String maSanPham, String tenSanPham, String loaiGiaoDich, int soLuong) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.loaiGiaoDich = loaiGiaoDich;
            this.soLuong = soLuong;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maSanPham, tenSanPham, loaiGiaoDich, soLuong};
        }
    }

    public static final class HangTonDong implements HangBaoCao {
        private final String maSanPham;
        private final String tenSanPham;
        private final int tonKho;
        private final double giaNhapGanNhat;
        private final double tongVonChon;

        public HangTonDong(
                String maSanPham,
                String tenSanPham,
                int tonKho,
                double giaNhapGanNhat,
                double tongVonChon) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.tonKho = tonKho;
            this.giaNhapGanNhat = giaNhapGanNhat;
            this.tongVonChon = tongVonChon;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maSanPham, tenSanPham, tonKho, giaNhapGanNhat, tongVonChon};
        }
    }

    public static final class HaoHutKiemKe implements HangBaoCao {
        private final String maSanPham;
        private final String tenSanPham;
        private final String soLo;
        private final int chenhLech;
        private final double giaTriLech;
        private final String lyDo;
        private final String ngayTao;

        public HaoHutKiemKe(
                String maSanPham,
                String tenSanPham,
                String soLo,
                int chenhLech,
                double giaTriLech,
                String lyDo,
                String ngayTao) {
            this.maSanPham = maSanPham;
            this.tenSanPham = tenSanPham;
            this.soLo = soLo;
            this.chenhLech = chenhLech;
            this.giaTriLech = giaTriLech;
            this.lyDo = lyDo;
            this.ngayTao = ngayTao;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maSanPham, tenSanPham, soLo, chenhLech, giaTriLech, lyDo, ngayTao};
        }
    }

    public static final class DichVuSpaThongKe implements HangBaoCao {
        private final String maGoi;
        private final String tenGoi;
        private final int soLuot;
        private final double giaTriDichVu;

        public DichVuSpaThongKe(String maGoi, String tenGoi, int soLuot, double giaTriDichVu) {
            this.maGoi = maGoi;
            this.tenGoi = tenGoi;
            this.soLuot = soLuot;
            this.giaTriDichVu = giaTriDichVu;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maGoi, tenGoi, soLuot, giaTriDichVu};
        }
    }

    public static final class DemTrangThai implements HangBaoCao {
        private final String trangThai;
        private final int soLuong;

        public DemTrangThai(String trangThai, int soLuong) {
            this.trangThai = trangThai;
            this.soLuong = soLuong;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {trangThai, soLuong};
        }
    }

    public static final class LuuTruQuyetToan implements HangBaoCao {
        private final String loaiPhong;
        private final int luotLuuTru;
        private final int soNgay;
        private final double doanhThuHoanTat;

        public LuuTruQuyetToan(
                String loaiPhong, int luotLuuTru, int soNgay, double doanhThuHoanTat) {
            this.loaiPhong = loaiPhong;
            this.luotLuuTru = luotLuuTru;
            this.soNgay = soNgay;
            this.doanhThuHoanTat = doanhThuHoanTat;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {loaiPhong, luotLuuTru, soNgay, doanhThuHoanTat};
        }
    }

    public static final class HopDongTrongKy implements HangBaoCao {
        private final String trangThai;
        private final int soHopDong;
        private final double tongGiaDem;
        private final double cocDaNhan;

        public HopDongTrongKy(
                String trangThai, int soHopDong, double tongGiaDem, double cocDaNhan) {
            this.trangThai = trangThai;
            this.soHopDong = soHopDong;
            this.tongGiaDem = tongGiaDem;
            this.cocDaNhan = cocDaNhan;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {trangThai, soHopDong, tongGiaDem, cocDaNhan};
        }
    }

    public static final class CocTrongKy implements HangBaoCao {
        private final LocalDate ngayNhan;
        private final double tienCoc;
        private final int soLanNhan;

        public CocTrongKy(LocalDate ngayNhan, double tienCoc, int soLanNhan) {
            this.ngayNhan = ngayNhan;
            this.tienCoc = tienCoc;
            this.soLanNhan = soLanNhan;
        }

        public int getSoLanNhan() {
            return soLanNhan;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {ngayNhan, tienCoc, soLanNhan};
        }
    }

    public static final class ChiTieuKhachHang implements HangBaoCao {
        private final String maKhachHang;
        private final String hoTen;
        private final String hangThanhVien;
        private final int diemTichLuy;
        private final double chiTieu;

        public ChiTieuKhachHang(
                String maKhachHang,
                String hoTen,
                String hangThanhVien,
                int diemTichLuy,
                double chiTieu) {
            this.maKhachHang = maKhachHang;
            this.hoTen = hoTen;
            this.hangThanhVien = hangThanhVien;
            this.diemTichLuy = diemTichLuy;
            this.chiTieu = chiTieu;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maKhachHang, hoTen, hangThanhVien, diemTichLuy, chiTieu};
        }
    }

    public static final class LuotKhach implements HangBaoCao {
        private final String nhomKhach;
        private final int soKhach;
        private final int luotMua;
        private final double tienThanhToan;

        public LuotKhach(String nhomKhach, int soKhach, int luotMua, double tienThanhToan) {
            this.nhomKhach = nhomKhach;
            this.soKhach = soKhach;
            this.luotMua = luotMua;
            this.tienThanhToan = tienThanhToan;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {nhomKhach, soKhach, luotMua, tienThanhToan};
        }
    }

    public static final class VoucherThongKe implements HangBaoCao {
        private final String maVoucher;
        private final double menhGia;
        private final int daDung;
        private final int soLuong;
        private final LocalDate hieuLucDen;

        public VoucherThongKe(
                String maVoucher, double menhGia, int daDung, int soLuong, LocalDate hieuLucDen) {
            this.maVoucher = maVoucher;
            this.menhGia = menhGia;
            this.daDung = daDung;
            this.soLuong = soLuong;
            this.hieuLucDen = hieuLucDen;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maVoucher, menhGia, daDung, soLuong, hieuLucDen};
        }
    }

    public static final class SuDungVoucher implements HangBaoCao {
        private final String maVoucher;
        private final String tenKhachHang;
        private final int luotDung;
        private final double giamGia;

        public SuDungVoucher(String maVoucher, String tenKhachHang, int luotDung, double giamGia) {
            this.maVoucher = maVoucher;
            this.tenKhachHang = tenKhachHang;
            this.luotDung = luotDung;
            this.giamGia = giamGia;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maVoucher, tenKhachHang, luotDung, giamGia};
        }
    }

    public static final class CongNoNhaCungCap implements HangBaoCao {
        private final String maNhaCungCap;
        private final String tenNhaCungCap;
        private final double tongNhap;
        private final double daTra;
        private final double duNo;

        public CongNoNhaCungCap(
                String maNhaCungCap,
                String tenNhaCungCap,
                double tongNhap,
                double daTra,
                double duNo) {
            this.maNhaCungCap = maNhaCungCap;
            this.tenNhaCungCap = tenNhaCungCap;
            this.tongNhap = tongNhap;
            this.daTra = daTra;
            this.duNo = duNo;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maNhaCungCap, tenNhaCungCap, tongNhap, daTra, duNo};
        }
    }

    public static final class TongHopCaThuNgan implements HangBaoCao {
        private final String nhanVien;
        private final String tenCa;
        private final LocalDateTime moLuc;
        private final LocalDateTime dongLuc;
        private final double tienDauCa;
        private final double thuHoaDon;
        private final double cocLuuTru;
        private final Double thucDem;
        private final Double chenhLech;

        public TongHopCaThuNgan(
                String nhanVien,
                String tenCa,
                LocalDateTime moLuc,
                LocalDateTime dongLuc,
                double tienDauCa,
                double thuHoaDon,
                double cocLuuTru,
                Double thucDem,
                Double chenhLech) {
            this.nhanVien = nhanVien;
            this.tenCa = tenCa;
            this.moLuc = moLuc;
            this.dongLuc = dongLuc;
            this.tienDauCa = tienDauCa;
            this.thuHoaDon = thuHoaDon;
            this.cocLuuTru = cocLuuTru;
            this.thucDem = thucDem;
            this.chenhLech = chenhLech;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {
                nhanVien, tenCa, moLuc, dongLuc, tienDauCa, thuHoaDon, cocLuuTru, thucDem, chenhLech
            };
        }
    }

    public static final class LichLamViecNhanVien implements HangBaoCao {
        private final String maNhanVien;
        private final String hoTen;
        private final String chucVu;
        private final int soCa;
        private final double soGio;

        public LichLamViecNhanVien(
                String maNhanVien, String hoTen, String chucVu, int soCa, double soGio) {
            this.maNhanVien = maNhanVien;
            this.hoTen = hoTen;
            this.chucVu = chucVu;
            this.soCa = soCa;
            this.soGio = soGio;
        }

        @Override
        public Object[] thanhHang() {
            return new Object[] {maNhanVien, hoTen, chucVu, soCa, soGio};
        }
    }
}
