package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcKhoDAO;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Kiểm tra nghiệp vụ nhập kho, xuất hủy và kiểm kê trước khi lưu dữ liệu. */
public final class KhoService {
    /**
     * Nhập kho trong KhoService.
     *
     * @param nhaCungCap nhà cung cấp
     * @param sanPham sản phẩm
     * @param loHang lô hàng
     * @param hanSuDung hạn sử dụng
     * @param soLuong số lượng
     * @param giaNhap giá nhập
     * @param daTra đã trả
     * @return kết quả nhập kho
     */
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
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (loHang.isBlank() || daTra < 0 || daTra > soLuong * giaNhap)
            throw new IllegalArgumentException("Số lô hoặc tiền đã trả không hợp lệ.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Vùng đang xét.
                    long vungDangXet = layId("san_pham", "ma_san_pham", sanPham),
                            chuoi = layId("nha_cung_cap", "ma_ncc", nhaCungCap);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcKhoDAO.demNhapKho(vungDangXet) > 0
                            && (hanSuDung == null || hanSuDung.isBefore(LocalDate.now())))
                        throw new IllegalArgumentException(
                                "Sản phẩm theo dõi HSD cần ngày hết hạn còn hiệu lực.");
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcKhoDAO.demNhapKho2(chuoi) == 0)
                        throw new IllegalArgumentException("Nhà cung cấp đã ngừng hợp tác.");
                    // Biên lai.
                    long bienLai =
                            JdbcKhoDAO.themNhapKho(
                                    taoMa("PN"), chuoi, layIdTaiKhoan(), soLuong * giaNhap, daTra);
                    JdbcKhoDAO.capNhatNhapKho3(
                            bienLai, vungDangXet, loHang, hanSuDung, soLuong, giaNhap);
                    // Các lô hàng.
                    List<Object[]> cacLoHang = JdbcKhoDAO.timNhapKho(vungDangXet, loHang);
                    // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                    if (cacLoHang.isEmpty())
                        JdbcKhoDAO.capNhatNhapKho4(vungDangXet, loHang, hanSuDung, soLuong);
                    else {
                        // Giá trị sau.
                        Object[] giaTriSau = cacLoHang.get(0);
                        // Cũ.
                        LocalDate cu =
                                giaTriSau[1] == null
                                        ? null
                                        : ((java.sql.Date) giaTriSau[1]).toLocalDate();
                        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                        if (!Objects.equals(hanSuDung, cu))
                            throw new IllegalArgumentException(
                                    "Lô đã tồn tại với hạn sử dụng khác.");
                        JdbcKhoDAO.capNhatNhapKho(soLuong, giaTriSau[0]);
                    }
                    JdbcKhoDAO.capNhatNhapKho2(giaNhap, vungDangXet);
                    NhatKyHeThongService.ghiNhatKy(
                            "Nhập kho", sanPham + " / " + loHang + " x " + soLuong);
                    return bienLai;
                });
    }

    /**
     * Ghi hủy trong KhoService.
     *
     * @param loHang lô hàng
     * @param soLuong số lượng
     * @param lyDo lý do
     * @return kết quả ghi hủy
     */
    public static long ghiHuy(long loHang, int soLuong, String lyDo) {
        yeuCau(Quyen.INVENTORY_ISSUE);
        kiemTraDuong(soLuong);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (lyDo.isBlank()) throw new IllegalArgumentException("Cần lý do xuất kho.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Các lô hàng.
                    List<Object[]> cacLoHang = JdbcKhoDAO.timXuatHuy(loHang);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacLoHang.isEmpty() || layGiaTri(cacLoHang.get(0)[0]) < soLuong)
                        throw new IllegalArgumentException("Không đủ tồn lô.");
                    // Giá nhập.
                    long giaNhap = layGiaTri(cacLoHang.get(0)[1]) * soLuong;
                    // Xuất kho.
                    long xuatKho =
                            JdbcKhoDAO.themXuatHuy(taoMa("PX"), layIdTaiKhoan(), lyDo, giaNhap);
                    JdbcKhoDAO.capNhatXuatHuy(xuatKho, loHang, soLuong, giaNhap);
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
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

    /**
     * Áp dụng xuất hủy trong KhoService.
     *
     * @param xuatKho xuất kho
     */
    static void apDungXuatHuy(long xuatKho) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (JdbcKhoDAO.demApDungXuatHuy(xuatKho) == 0)
            throw new IllegalStateException("Phiếu xuất đã xử lý.");
        for (Object[] dongDuLieu : JdbcKhoDAO.timApDungXuatHuy(xuatKho))
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (JdbcKhoDAO.capNhatApDungXuatHuy2(dongDuLieu[1], dongDuLieu[0], dongDuLieu[1]) != 1)
                throw new IllegalArgumentException("Lô không còn đủ tồn, không thể duyệt xuất.");
        JdbcKhoDAO.capNhatApDungXuatHuy(xuatKho);
        NhatKyHeThongService.ghiNhatKy("Xuất kho", chuyenThanhChuoi(xuatKho));
    }

    /**
     * Kiểm kê trong KhoService.
     *
     * @param loHang lô hàng
     * @param thucTe thực tế
     * @param lyDo lý do
     * @return kết quả kiểm kê
     */
    public static long kiemKe(long loHang, int thucTe, String lyDo) {
        yeuCau(Quyen.INVENTORY_COUNT);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (thucTe < 0) throw new IllegalArgumentException("Tồn thực tế không được âm.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Các dòng.
                    List<Object[]> cacDong = JdbcKhoDAO.timKiemKe(loHang);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacDong.isEmpty()) throw new IllegalArgumentException("Không tìm thấy lô.");
                    // Đặt phòng.
                    int datPhong = ((Number) cacDong.get(0)[0]).intValue();
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (datPhong != thucTe && lyDo.isBlank())
                        throw new IllegalArgumentException("Cần lý do chênh lệch.");
                    // Kết quả.
                    long ketQua =
                            JdbcKhoDAO.themKiemKe(
                                    taoMa("KK"),
                                    layIdTaiKhoan(),
                                    LocalDate.now().toString(),
                                    datPhong == thucTe ? "APPROVED" : "PENDING");
                    JdbcKhoDAO.capNhatKiemKe(ketQua, loHang, datPhong, thucTe, lyDo);
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
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

    /**
     * Áp dụng kiểm kê trong KhoService.
     *
     * @param soLuong số lượng
     */
    static void apDungKiemKe(long soLuong) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (JdbcKhoDAO.demApDungKiemKe(soLuong) == 0)
            throw new IllegalStateException("Biên bản đã xử lý.");
        for (Object[] dongDuLieu : JdbcKhoDAO.timApDungKiemKe(soLuong))
            // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
            if (JdbcKhoDAO.capNhatApDungKiemKe(dongDuLieu[2], dongDuLieu[0], dongDuLieu[1]) != 1)
                throw new IllegalArgumentException(
                        "Tồn đã thay đổi sau kiểm kê. Cần lập lại biên bản.");
        JdbcKhoDAO.capNhatApDungKiemKe2(soLuong);
    }

    /**
     * Thanh toán nhà cung cấp trong KhoService.
     *
     * @param id id
     * @param daTra đã trả
     */
    public static void thanhToanNhaCungCap(long id, long daTra) {
        yeuCau(Quyen.INVENTORY_RECEIVE);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (daTra <= 0) throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcKhoDAO.themThanhToanNhaCungCap(daTra, id, daTra) != 1)
                        throw new IllegalArgumentException("Số tiền vượt công nợ còn lại.");
                    NhatKyHeThongService.ghiNhatKy("Trả công nợ", id + " / " + daTra);
                    return null;
                });
    }

    /**
     * Lấy nhập kho chi tiết trong KhoService.
     *
     * @param nhapKho nhập kho
     * @return kết quả lấy nhập kho chi tiết
     */
    public static List<Object[]> layNhapKhoChiTiet(long nhapKho) {
        PhienDangNhapService.yeuCau(com.petstation.model.Quyen.INVENTORY_RECEIVE);
        return JdbcKhoDAO.layNhapKhoChiTiet(nhapKho);
    }
}
