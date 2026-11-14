package com.petstation.service.thongke;

import com.petstation.dao.thongke.ThongKeDAO;
import com.petstation.dao.thongke.impl.ThongKeDAOImpl;
import com.petstation.dto.ChiTietBaoCao.BienDongKho;
import com.petstation.dto.ChiTietBaoCao.ChiTieuKhachHang;
import com.petstation.dto.ChiTietBaoCao.CocTrongKy;
import com.petstation.dto.ChiTietBaoCao.CongNoNhaCungCap;
import com.petstation.dto.ChiTietBaoCao.DemTrangThai;
import com.petstation.dto.ChiTietBaoCao.DichVuSpaThongKe;
import com.petstation.dto.ChiTietBaoCao.DoanhThuHoanTat;
import com.petstation.dto.ChiTietBaoCao.DoanhThuTheoNgay;
import com.petstation.dto.ChiTietBaoCao.HangTonDong;
import com.petstation.dto.ChiTietBaoCao.HaoHutKiemKe;
import com.petstation.dto.ChiTietBaoCao.HopDongTrongKy;
import com.petstation.dto.ChiTietBaoCao.LichLamViecNhanVien;
import com.petstation.dto.ChiTietBaoCao.LoSapHetHan;
import com.petstation.dto.ChiTietBaoCao.LuotKhach;
import com.petstation.dto.ChiTietBaoCao.LuuTruQuyetToan;
import com.petstation.dto.ChiTietBaoCao.SanPhamBanChay;
import com.petstation.dto.ChiTietBaoCao.SuDungVoucher;
import com.petstation.dto.ChiTietBaoCao.ThongKeNhom;
import com.petstation.dto.ChiTietBaoCao.TonKhoHienTai;
import com.petstation.dto.ChiTietBaoCao.TongHopCaThuNgan;
import com.petstation.dto.ChiTietBaoCao.VoucherThongKe;
import com.petstation.dto.KhoangNgay;
import com.petstation.model.chung.Quyen;
import com.petstation.service.hethong.CaiDatService;
import com.petstation.service.hethong.PhienDangNhapService;

import java.util.List;
import java.util.Objects;

// Tầng dịch vụ Thống kê & Báo cáo (Service)
public final class ThongKeService {
    private final ThongKeDAO thongKeDAO;
    private final CaiDatService caiDatService;
    private final PhienDangNhapService phienDangNhapService;

    public ThongKeService() {
        this(new ThongKeDAOImpl(), CaiDatService.macDinh(), PhienDangNhapService.macDinh());
    }

    public ThongKeService(ThongKeDAO thongKeDAO) {
        this(thongKeDAO, CaiDatService.macDinh(), PhienDangNhapService.macDinh());
    }

    public ThongKeService(
            ThongKeDAO thongKeDAO,
            CaiDatService caiDatService,
            PhienDangNhapService phienDangNhapService) {
        this.thongKeDAO = Objects.requireNonNull(thongKeDAO, "thongKeDAO không được null");
        this.caiDatService = Objects.requireNonNull(caiDatService, "caiDatService không được null");
        this.phienDangNhapService =
                Objects.requireNonNull(
                        phienDangNhapService, "phienDangNhapService không được null");
    }

    // Kiểm tra quyền xem báo cáo chung hoặc báo cáo ca của người dùng hiện tại
    private void kiemTraQuyenBaoCao() {
        if (!phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)) {
            phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CA);
        }
    }

    // 1. THỐNG KÊ DOANH THU (ThongKeDoanhThuPanel & BangDieuKhienPanel)

    public List<DoanhThuTheoNgay> layDoanhThuTheoNgay(int soNgay) {
        return layDoanhThuTheoNgay(KhoangNgay.ganDay(soNgay));
    }

    public List<DoanhThuTheoNgay> layDoanhThuTheoNgay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDoanhThuTheoNgay(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : phienDangNhapService.layMaNhanVien());
    }

    public List<ThongKeNhom> layDoanhThu(int soNgay, String nhomTongHop) {
        return layDoanhThu(KhoangNgay.ganDay(soNgay), nhomTongHop);
    }

    public List<ThongKeNhom> layDoanhThu(KhoangNgay khoangNgay, String nhomTongHop) {
        kiemTraQuyenBaoCao();
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDoanhThu(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                nhomTongHop,
                tatCa ? null : phienDangNhapService.layMaNhanVien());
    }

    public List<DoanhThuHoanTat> layDoanhThuHoanTat(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        String maNhanVien =
                phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)
                        ? null
                        : phienDangNhapService.layMaNhanVien();
        return thongKeDAO.layDoanhThuHoanTat(k.getTuNgay(), k.getCanTren(), maNhanVien);
    }

    public List<ThongKeNhom> layDoanhThuTheoGio(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layDoanhThuTheoGio(k.getTuNgay(), k.getCanTren());
    }

    public List<ThongKeNhom> layDoanhThuTheoThu(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layDoanhThuTheoThu(k.getTuNgay(), k.getCanTren());
    }

    // 2. THỐNG KÊ HÀNG HÓA & TỒN KHO (ThongKeHangHoaPanel)

    public List<SanPhamBanChay> laySanPhamBanChay(int soNgay) {
        return laySanPhamBanChay(KhoangNgay.ganDay(soNgay));
    }

    public List<SanPhamBanChay> laySanPhamBanChay(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.laySanPhamBanChay(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : phienDangNhapService.layMaNhanVien());
    }

    public List<TonKhoHienTai> layTonKho() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timKho();
    }

    public List<LoSapHetHan> layLoSapHetHan() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timHanSuDung(caiDatService.layHanSuDungCanhBaoSoNgay());
    }

    public List<BienDongKho> layBienDongKho(KhoangNgay k) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layBienDongKho(k.getTuNgay(), k.getCanTren());
    }

    public List<HangTonDong> layHangTonDong() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layHangTonDong();
    }

    public List<HaoHutKiemKe> layHaoHutKiemKe(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        return thongKeDAO.layHaoHutKiemKe(k.getTuNgay(), k.getCanTren());
    }

    // 3. THỐNG KÊ DỊCH VỤ SPA (ThongKeSpaPanel)

    public List<DichVuSpaThongKe> layDuLieuSpa(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDuLieuSpa(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : phienDangNhapService.layMaNhanVien());
    }

    public List<DemTrangThai> layLichHen(int soNgay) {
        return layLichHen(KhoangNgay.ganDay(soNgay));
    }

    public List<DemTrangThai> layLichHen(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layLichHen(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : phienDangNhapService.layMaNhanVien());
    }

    public List<DemTrangThai> layPhieuSpaTheoTrangThai(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        String maNhanVien =
                phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)
                        ? null
                        : phienDangNhapService.layMaNhanVien();
        return thongKeDAO.layPhieuSpaTheoTrangThai(k.getTuNgay(), k.getCanTren(), maNhanVien);
    }

    // 4. THỐNG KÊ LƯU TRÚ HOTEL (ThongKeLuuTruPanel)

    public List<DemTrangThai> layTrangThaiPhong() {
        kiemTraQuyenBaoCao();
        return thongKeDAO.timTrangThaiPhong();
    }

    public List<LuuTruQuyetToan> layDuLieuHotel(int soNgay) {
        return layDuLieuHotel(KhoangNgay.ganDay(soNgay));
    }

    public List<LuuTruQuyetToan> layDuLieuHotel(KhoangNgay khoangNgay) {
        kiemTraQuyenBaoCao();
        boolean tatCa = phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDuLieuHotel(
                khoangNgay.getTuNgay(),
                khoangNgay.getCanTren(),
                tatCa ? null : phienDangNhapService.layMaNhanVien());
    }

    public List<HopDongTrongKy> layHopDongTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        String maNhanVien =
                phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)
                        ? null
                        : phienDangNhapService.layMaNhanVien();
        return thongKeDAO.layHopDongTrongKy(k.getTuNgay(), k.getCanTren(), maNhanVien);
    }

    public List<CocTrongKy> layCocTrongKy(KhoangNgay k) {
        kiemTraQuyenBaoCao();
        String maNhanVien =
                phienDangNhapService.layPhien().kiemTraCo(Quyen.XEM_BAO_CAO_CHUNG)
                        ? null
                        : phienDangNhapService.layMaNhanVien();
        return thongKeDAO.layCocTrongKy(k.getTuNgay(), k.getCanTren(), maNhanVien);
    }

    // 5. THỐNG KÊ KHÁCH HÀNG & VOUCHER (ThongKeKhachHangPanel)

    public List<ChiTieuKhachHang> layKhachHang(int soNgay) {
        return layKhachHang(KhoangNgay.ganDay(soNgay));
    }

    public List<ChiTieuKhachHang> layKhachHang(KhoangNgay khoangNgay) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timCacKhachHang(khoangNgay.getTuNgay(), khoangNgay.getCanTren());
    }

    public List<LuotKhach> layLuotKhach(KhoangNgay k) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layLuotKhach(k.getTuNgay(), k.getCanTren());
    }

    public List<VoucherThongKe> layVoucher() {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timCacVoucher();
    }

    public List<SuDungVoucher> laySuDungVoucher(KhoangNgay k) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.laySuDungVoucher(k.getTuNgay(), k.getCanTren());
    }

    // 6. THỐNG KÊ CÔNG NỢ NHÀ CUNG CẤP (ThongKeCongNoPanel)

    public List<CongNoNhaCungCap> layCongNoNhaCungCap(int soNgay) {
        return layCongNoNhaCungCap(KhoangNgay.ganDay(soNgay));
    }

    public List<CongNoNhaCungCap> layCongNoNhaCungCap(KhoangNgay khoangNgay) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timNhaCungCapCongNo(khoangNgay.getTuNgay(), khoangNgay.getCanTren());
    }

    public List<CongNoNhaCungCap> layDuNoHienTai(KhoangNgay k) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layDuNoHienTai();
    }

    // 7. THỐNG KÊ NHÂN VIÊN & CA THU NGÂN (ThongKeNhanVienPanel)

    // Lấy bảng tổng hợp thu tiền đầu ca, hóa đơn, cọc lưu trú, thực đếm và chênh
    // lệch ca
    public List<TongHopCaThuNgan> layTongHopCa(KhoangNgay k) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.layTongHopCa(k.getTuNgay(), k.getCanTren());
    }

    public List<LichLamViecNhanVien> layNhanVien(int soNgay) {
        return layNhanVien(KhoangNgay.ganDay(soNgay));
    }

    // Lấy thống kê tổng số ca đi làm và tổng số giờ làm việc của từng nhân viên
    // trong kỳ
    public List<LichLamViecNhanVien> layNhanVien(KhoangNgay khoangNgay) {
        phienDangNhapService.yeuCau(Quyen.XEM_BAO_CAO_CHUNG);
        return thongKeDAO.timNhanVien(khoangNgay.getTuNgay(), khoangNgay.getCanTren());
    }
}
