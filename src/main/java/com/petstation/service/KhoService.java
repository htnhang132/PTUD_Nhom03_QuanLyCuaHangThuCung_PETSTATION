package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.KhoDAO;
import com.petstation.dao.impl.KhoDAOImpl;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Kiểm tra nghiệp vụ nhập kho, xuất hủy và kiểm kê trước khi lưu dữ liệu. */
public final class KhoService {
    private static final KhoDAO khoDAO = new KhoDAOImpl();

    public static long nhapKho(
            String nhaCungCap,
            String sanPham,
            String loHang,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap,
            long daTra) {
        yeuCau(Quyen.NHAP_KHO);
        kiemTraDuong(soLuong);
        kiemTraDuong(giaNhap);
        if (loHang.isBlank() || daTra < 0 || daTra > soLuong * giaNhap)
            throw new IllegalArgumentException("Số lô hoặc tiền đã trả không hợp lệ.");
        return thucHienGiaoDich(
                ketNoi -> {
                    long vungDangXet = layId("san_pham", "ma_san_pham", sanPham),
                            chuoi = layId("nha_cung_cap", "ma_ncc", nhaCungCap);
                    if (khoDAO.demNhapKho(vungDangXet) > 0
                            && (hanSuDung == null || hanSuDung.isBefore(LocalDate.now())))
                        throw new IllegalArgumentException(
                                "Sản phẩm theo dõi HSD cần ngày hết hạn còn hiệu lực.");
                    if (khoDAO.demNhaCungCapDangHopTac(chuoi) == 0)
                        throw new IllegalArgumentException("Nhà cung cấp đã ngừng hợp tác.");
                    // Biên lai.
                    long bienLai =
                            khoDAO.themNhapKho(
                                    taoMa("PN"), chuoi, layIdTaiKhoan(), soLuong * giaNhap, daTra);
                    khoDAO.themChiTietNhapKho(
                            bienLai, vungDangXet, loHang, hanSuDung, soLuong, giaNhap);
                    // Các lô hàng.
                    List<Object[]> cacLoHang = khoDAO.timNhapKho(vungDangXet, loHang);
                    if (cacLoHang.isEmpty())
                        khoDAO.themLoHang(vungDangXet, loHang, hanSuDung, soLuong);
                    else {
                        // Giá trị sau.
                        Object[] giaTriSau = cacLoHang.get(0);
                        // Cũ.
                        LocalDate cu =
                                giaTriSau[1] == null
                                        ? null
                                        : ((java.sql.Date) giaTriSau[1]).toLocalDate();
                        if (!Objects.equals(hanSuDung, cu))
                            throw new IllegalArgumentException(
                                    "Lô đã tồn tại với hạn sử dụng khác.");
                        khoDAO.capNhatNhapKho(soLuong, giaTriSau[0]);
                    }
                    khoDAO.capNhatGiaNhapGanNhat(giaNhap, vungDangXet);
                    NhatKyHeThongService.ghiNhatKy(
                            "Nhập kho", sanPham + " / " + loHang + " x " + soLuong);
                    return bienLai;
                });
    }

    public static long ghiHuy(long loHang, int soLuong, String lyDo) {
        yeuCau(Quyen.XUAT_HUY_KHO);
        kiemTraDuong(soLuong);
        if (lyDo.isBlank()) throw new IllegalArgumentException("Cần lý do xuất kho.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Các lô hàng.
                    List<Object[]> cacLoHang = khoDAO.timXuatHuy(loHang);
                    if (cacLoHang.isEmpty() || layGiaTri(cacLoHang.get(0)[0]) < soLuong)
                        throw new IllegalArgumentException("Không đủ tồn lô.");
                    // Giá nhập.
                    long giaNhap = layGiaTri(cacLoHang.get(0)[1]) * soLuong;
                    // Xuất kho.
                    long xuatKho = khoDAO.themXuatHuy(taoMa("PX"), layIdTaiKhoan(), lyDo, giaNhap);
                    khoDAO.capNhatXuatHuy(xuatKho, loHang, soLuong, giaNhap);
                    if (giaNhap >= CaiDatService.ghiGioiHanHuyPheDuyet())
                        PheDuyetService.guiThaoTac(
                                PheDuyetService.Loai.XUAT_HUY,
                                lyDo,
                                "Tồn " + cacLoHang.get(0)[0],
                                "Xuất " + soLuong,
                                "WRITE_OFF",
                                xuatKho,
                                "");
                    else apDungXuatHuy(xuatKho);
                    return xuatKho;
                });
    }

    static void apDungXuatHuy(long xuatKho) {
        if (khoDAO.demApDungXuatHuy(xuatKho) == 0)
            throw new IllegalStateException("Phiếu xuất đã xử lý.");
        for (Object[] dongDuLieu : khoDAO.timApDungXuatHuy(xuatKho))
            if (khoDAO.truTonXuatHuy(dongDuLieu[1], dongDuLieu[0], dongDuLieu[1]) != 1)
                throw new IllegalArgumentException("Lô không còn đủ tồn, không thể duyệt xuất.");
        khoDAO.capNhatApDungXuatHuy(xuatKho);
        NhatKyHeThongService.ghiNhatKy("Xuất kho", chuyenThanhChuoi(xuatKho));
    }

    public record DongKiemKe(
            long loHangId,
            String maSP,
            String tenSP,
            String barcode,
            String soLo,
            LocalDate hanDung,
            int tonHeThong,
            int tonThucTe,
            String lyDo) {}

    public static List<Object[]> layDanhSachLoHangKiemKe(String loaiSanPham, boolean chiSapHetHan) {
        yeuCau(Quyen.KIEM_KE_KHO);
        List<Object[]> tatCa = khoDAO.layDanhSachLoHangKiemKe();
        int soNgayCanhBao = CaiDatService.layHanSuDungCanhBaoSoNgay();
        LocalDate mocHetHan = LocalDate.now().plusDays(soNgayCanhBao);
        return tatCa.stream()
                .filter(
                        r -> {
                            if (loaiSanPham != null
                                    && !loaiSanPham.isBlank()
                                    && !loaiSanPham.equalsIgnoreCase("TẤT CẢ")) {
                                String loai = r[7] == null ? "" : r[7].toString();
                                if (!loai.equalsIgnoreCase(loaiSanPham)) return false;
                            }
                            if (chiSapHetHan) {
                                Object hsdObj = r[5];
                                if (hsdObj == null) return false;
                                LocalDate hsd = ((java.sql.Date) hsdObj).toLocalDate();
                                if (hsd.isAfter(mocHetHan)) return false;
                            }
                            return true;
                        })
                .toList();
    }

    public static List<Object[]> layChiTietKiemKe(long bienBanId) {
        yeuCau(Quyen.KIEM_KE_KHO);
        return khoDAO.layChiTietKiemKe(bienBanId);
    }

    public static long lapBienBanKiemKe(String kyKiemKe, List<DongKiemKe> danhSachDong) {
        yeuCau(Quyen.KIEM_KE_KHO);
        if (danhSachDong == null || danhSachDong.isEmpty()) {
            throw new IllegalArgumentException("Danh sách mặt hàng kiểm kê không được rỗng.");
        }
        if (kyKiemKe == null || kyKiemKe.isBlank()) {
            kyKiemKe = "Kỳ kiểm kê " + LocalDate.now();
        }
        final String kyKiemKeFinal = kyKiemKe;

        boolean coChenhLech = false;
        int demChenhLech = 0;
        for (DongKiemKe d : danhSachDong) {
            if (d.tonThucTe() < 0) {
                throw new IllegalArgumentException(
                        "Tồn thực tế của SP " + d.maSP() + " không được âm.");
            }
            if (d.tonThucTe() != d.tonHeThong()) {
                coChenhLech = true;
                demChenhLech++;
                if (d.lyDo() == null || d.lyDo().isBlank()) {
                    throw new IllegalArgumentException(
                            "Vui lòng ghi lý do chênh lệch cho sản phẩm: "
                                    + d.maSP()
                                    + " ("
                                    + d.tenSP()
                                    + ")");
                }
            }
        }

        final boolean coChenhLechFinal = coChenhLech;
        final int demChenhLechFinal = demChenhLech;

        return thucHienGiaoDich(
                ketNoi -> {
                    String soBienBan = taoMa("KK");
                    String trangThai = coChenhLechFinal ? "PENDING" : "APPROVED";
                    long bienBanId =
                            khoDAO.themKiemKe(soBienBan, layIdTaiKhoan(), kyKiemKeFinal, trangThai);

                    for (DongKiemKe d : danhSachDong) {
                        khoDAO.capNhatKiemKe(
                                bienBanId,
                                d.loHangId(),
                                d.tonHeThong(),
                                d.tonThucTe(),
                                d.lyDo() == null ? "" : d.lyDo());
                    }

                    if (coChenhLechFinal) {
                        PheDuyetService.guiThaoTac(
                                PheDuyetService.Loai.KIEM_KE,
                                "Kiểm kê định kỳ: có " + demChenhLechFinal + " mặt hàng chênh lệch",
                                "Kỳ: " + kyKiemKeFinal,
                                "Số BB: " + soBienBan,
                                "STOCKTAKE",
                                bienBanId,
                                "");
                    }

                    NhatKyHeThongService.ghiNhatKy(
                            "Kiểm kê đợt",
                            soBienBan
                                    + " ("
                                    + danhSachDong.size()
                                    + " dòng, "
                                    + demChenhLechFinal
                                    + " lệch)");
                    return bienBanId;
                });
    }

    public static void duyetVaCanBangKho(long bienBanId) {
        yeuCau(Quyen.XU_LY_PHE_DUYET);
        thucHienGiaoDich(
                ketNoi -> {
                    apDungKiemKe(bienBanId);
                    NhatKyHeThongService.ghiNhatKy(
                            "Cân bằng kho kiểm kê", "Biên bản ID " + bienBanId);
                    return null;
                });
    }

    public static long kiemKe(long loHang, int thucTe, String lyDo) {
        yeuCau(Quyen.KIEM_KE_KHO);
        if (thucTe < 0) throw new IllegalArgumentException("Tồn thực tế không được âm.");
        return thucHienGiaoDich(
                ketNoi -> {
                    List<Object[]> cacDong = khoDAO.timKiemKe(loHang);
                    if (cacDong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy lô.");
                    // Đặt phòng.
                    int datPhong = ((Number) cacDong.get(0)[0]).intValue();
                    if (datPhong != thucTe && lyDo.isBlank())
                        throw new IllegalArgumentException("Cần lý do chênh lệch.");
                    long ketQua =
                            khoDAO.themKiemKe(
                                     taoMa("KK"),
                                    layIdTaiKhoan(),
                                    LocalDate.now().toString(),
                                    datPhong == thucTe ? "APPROVED" : "PENDING");
                    khoDAO.capNhatKiemKe(ketQua, loHang, datPhong, thucTe, lyDo);
                    if (datPhong != thucTe)
                        PheDuyetService.guiThaoTac(
                                PheDuyetService.Loai.KIEM_KE,
                                lyDo,
                                "Tồn sổ " + datPhong,
                                "Tồn thực " + thucTe,
                                "STOCKTAKE",
                                ketQua,
                                "");
                    NhatKyHeThongService.ghiNhatKy("Kiểm kê", chuyenThanhChuoi(ketQua));
                    return ketQua;
                });
    }

    static void apDungKiemKe(long soLuong) {
        if (khoDAO.demApDungKiemKe(soLuong) == 0)
            throw new IllegalStateException("Biên bản đã xử lý.");
        for (Object[] dongDuLieu : khoDAO.timApDungKiemKe(soLuong))
            if (khoDAO.capNhatApDungKiemKe(dongDuLieu[2], dongDuLieu[0], dongDuLieu[1]) != 1)
                throw new IllegalArgumentException(
                        "Tồn đã thay đổi sau kiểm kê. Cần lập lại biên bản.");
        khoDAO.danhDauKiemKeDaDuyet(soLuong);
    }

    public static void thanhToanNhaCungCap(long id, long daTra) {
        yeuCau(Quyen.NHAP_KHO);
        if (daTra <= 0) throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        thucHienGiaoDich(
                ketNoi -> {
                    if (khoDAO.themThanhToanNhaCungCap(daTra, id, daTra) != 1)
                        throw new IllegalArgumentException("Số tiền vượt công nợ còn lại.");
                    NhatKyHeThongService.ghiNhatKy("Trả công nợ", id + " / " + daTra);
                    return null;
                });
    }

    public static List<Object[]> layNhapKhoChiTiet(long nhapKho) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.NHAP_KHO);
        return khoDAO.layNhapKhoChiTiet(nhapKho);
    }
}
