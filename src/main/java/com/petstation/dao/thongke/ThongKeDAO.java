package com.petstation.dao.thongke;

import com.petstation.dto.ChiTietBaoCao;
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

import java.time.LocalDate;
import java.util.List;

// Giao diện truy xuất dữ liệu (DAO) cho 7 phân hệ Thống kê & Báo cáo.
public interface ThongKeDAO {

    // 1. THỐNG KÊ DOANH THU

    List<DoanhThuTheoNgay> layDoanhThuTheoNgay(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<ThongKeNhom> layDoanhThu(
            LocalDate tu, LocalDate denNgay, String nhomTongHop, String maNhanVien);

    List<DoanhThuHoanTat> layDoanhThuHoanTat(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<ThongKeNhom> layDoanhThuTheoGio(LocalDate tu, LocalDate denNgay);

    List<ThongKeNhom> layDoanhThuTheoThu(LocalDate tu, LocalDate denNgay);

    // 2. THỐNG KÊ HÀNG HÓA & TỒN KHO

    List<SanPhamBanChay> laySanPhamBanChay(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<TonKhoHienTai> timKho();

    List<LoSapHetHan> timHanSuDung(int soNgayCanhBao);

    List<BienDongKho> layBienDongKho(LocalDate tu, LocalDate denNgay);

    List<HangTonDong> layHangTonDong();

    List<HaoHutKiemKe> layHaoHutKiemKe(LocalDate tu, LocalDate denNgay);

    // 3. THỐNG KÊ DỊCH VỤ SPA

    List<DichVuSpaThongKe> layDuLieuSpa(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<DemTrangThai> layLichHen(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<DemTrangThai> layPhieuSpaTheoTrangThai(LocalDate tu, LocalDate denNgay, String maNhanVien);

    // 4. THỐNG KÊ LƯU TRÚ HOTEL

    List<DemTrangThai> timTrangThaiPhong();

    List<LuuTruQuyetToan> layDuLieuHotel(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<HopDongTrongKy> layHopDongTrongKy(LocalDate tu, LocalDate denNgay, String maNhanVien);

    List<CocTrongKy> layCocTrongKy(LocalDate tu, LocalDate denNgay, String maNhanVien);

    // 5. THỐNG KÊ KHÁCH HÀNG & VOUCHER

    List<ChiTieuKhachHang> timCacKhachHang(LocalDate tu, LocalDate denNgay);

    List<LuotKhach> layLuotKhach(LocalDate tu, LocalDate denNgay);

    List<VoucherThongKe> timCacVoucher();

    List<SuDungVoucher> laySuDungVoucher(LocalDate tu, LocalDate denNgay);

    // 6. THỐNG KÊ CÔNG NỢ NHÀ CUNG CẤP

    List<CongNoNhaCungCap> timNhaCungCapCongNo(LocalDate tu, LocalDate denNgay);

    List<CongNoNhaCungCap> layDuNoHienTai();

    // 7. THỐNG KÊ NHÂN VIÊN & CA THU NGÂN

    // Tổng hợp thu tiền đầu ca, hóa đơn, tiền cọc, thực đếm và chênh lệch của các ca thu ngân
    List<TongHopCaThuNgan> layTongHopCa(LocalDate tu, LocalDate denNgay);

    // Thống kê số ca đi làm và tổng số giờ công của nhân viên trong kỳ
    List<LichLamViecNhanVien> timNhanVien(LocalDate tu, LocalDate denNgay);

    ChiTietBaoCao layChiTiet(String baoCao, String goc, KhoangNgay ky, String maNhanVien);
}
