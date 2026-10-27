package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.KhoDAO;
import com.petstation.dao.impl.KhoImpl;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Kiểm tra nghiệp vụ nhập kho, xuất hủy và kiểm kê trước khi lưu dữ liệu. */
public final class KhoService {
    private static final KhoDAO khoDAO = new KhoImpl();

    public static long nhapKho(
            String nhaCungCap,
            String sanPham,
            String loHang,
            LocalDate hanSuDung,
            int soLuong,
            long giaNhap,
            long daTra) {
        yeuCau(Quyen.INVENTORY_RECEIVE);
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
        yeuCau(Quyen.INVENTORY_ISSUE);
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
                                PheDuyetService.Loai.WRITE_OFF,
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

    public static long kiemKe(long loHang, int thucTe, String lyDo) {
        yeuCau(Quyen.INVENTORY_COUNT);
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
                                PheDuyetService.Loai.STOCKTAKE,
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
        yeuCau(Quyen.INVENTORY_RECEIVE);
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
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.INVENTORY_RECEIVE);
        return khoDAO.layNhapKhoChiTiet(nhapKho);
    }
}
