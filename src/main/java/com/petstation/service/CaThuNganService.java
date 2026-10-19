package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcCaThuNganDAO;
import com.petstation.model.Quyen;

import java.time.LocalTime;
import java.util.*;

/** Quản lý mở ca, doanh thu, tiền bàn giao và đóng ca của tài khoản hiện tại. */
public final class CaThuNganService {
    /** Điều phối nghiệp vụ và kiểm tra dữ liệu cho phương thức. */
    public enum PhuongThuc {
        CASH,
        TRANSFER,
        CARD
    }

    /** Các bộ lắng nghe. */
    private final List<Runnable> cacBoLangNghe = new ArrayList<>();

    /** Tiêu đề đang chờ. */
    private String tieuDeDangCho = "", cuoiCungBanGiao = "";

    /**
     * Lấy hiện tại trong CaThuNganService.
     *
     * @return kết quả lấy hiện tại
     */
    private Object[] layHienTai() {
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (layPhienHienTai() == null) return null;
        // Các dòng.
        List<Object[]> cacDong = JdbcCaThuNganDAO.timHienTai(layIdTaiKhoan());
        return cacDong.isEmpty() ? null : cacDong.get(0);
    }

    /**
     * Lấy id trong CaThuNganService.
     *
     * @return kết quả lấy id
     */
    public long layId() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null ? 0 : ((Number) dongDuLieu[0]).longValue();
    }

    /**
     * Kiểm tra mở trong CaThuNganService.
     *
     * @return kết quả kiểm tra điều kiện xử lý
     */
    public boolean kiemTraMo() {
        return layId() > 0;
    }

    /**
     * Lấy tên ca trong CaThuNganService.
     *
     * @return kết quả lấy tên ca
     */
    public String layTenCa() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null ? "" : chuyenThanhChuoi(dongDuLieu[1]);
    }

    /**
     * Lấy giờ mở ca trong CaThuNganService.
     *
     * @return kết quả lấy giờ mở ca
     */
    public LocalTime layGioMoCa() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null
                ? null
                : ((java.sql.Timestamp) dongDuLieu[2]).toLocalDateTime().toLocalTime().withNano(0);
    }

    /**
     * Lấy tiền đầu ca trong CaThuNganService.
     *
     * @return kết quả lấy tiền đầu ca
     */
    public long layTienDauCa() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null ? 0 : ((Number) dongDuLieu[3]).longValue();
    }

    /**
     * Lấy doanh thu trong CaThuNganService.
     *
     * @param phuongThuc phương thức
     * @return kết quả lấy doanh thu
     */
    private long layDoanhThu(PhuongThuc phuongThuc) {
        return JdbcCaThuNganDAO.demBanHang(layId(), phuongThuc.name());
    }

    /**
     * Lấy doanh thu tiền mặt trong CaThuNganService.
     *
     * @return kết quả lấy doanh thu tiền mặt
     */
    public long layDoanhThuTienMat() {
        return layDoanhThu(PhuongThuc.CASH);
    }

    /**
     * Lấy doanh thu chuyển khoản trong CaThuNganService.
     *
     * @return kết quả lấy doanh thu chuyển khoản
     */
    public long layDoanhThuChuyenKhoan() {
        return layDoanhThu(PhuongThuc.TRANSFER);
    }

    /**
     * Lấy doanh thu thẻ trong CaThuNganService.
     *
     * @return kết quả lấy doanh thu thẻ
     */
    public long layDoanhThuThe() {
        return layDoanhThu(PhuongThuc.CARD);
    }

    /**
     * Đếm hóa đơn trong CaThuNganService.
     *
     * @return kết quả đếm hóa đơn
     */
    public int demHoaDon() {
        return (int) JdbcCaThuNganDAO.demSoLuongHoaDon(layId());
    }

    /**
     * Lấy tiền mặt dự kiến trong CaThuNganService.
     *
     * @return kết quả lấy tiền mặt dự kiến
     */
    public long layTienMatDuKien() {
        return layTienDauCa() + layDoanhThuTienMat() - JdbcCaThuNganDAO.demDuKienTienMat(layId());
    }

    /**
     * Lấy nhãn trong CaThuNganService.
     *
     * @return kết quả lấy nhãn
     */
    public String layNhan() {
        return kiemTraMo() ? "Đang mở – " + layTenCa() : "Chưa mở ca";
    }

    /**
     * Lấy tiêu đề đang chờ trong CaThuNganService.
     *
     * @return kết quả lấy tiêu đề đang chờ
     */
    public String layTieuDeDangCho() {
        return tieuDeDangCho;
    }

    /**
     * Đặt tiêu đề đang chờ trong CaThuNganService.
     *
     * @param tieuDe tiêu đề
     */
    public void datTieuDeDangCho(String tieuDe) {
        tieuDeDangCho = tieuDe == null ? "" : tieuDe;
    }

    /**
     * Lấy bàn giao gần nhất trong CaThuNganService.
     *
     * @return kết quả lấy bàn giao gần nhất
     */
    public String layBanGiaoGanNhat() {
        return cuoiCungBanGiao;
    }

    /**
     * Mở trong CaThuNganService.
     *
     * @param ten tên
     * @param soTien số tiền
     */
    public void mo(String ten, long soTien) {
        yeuCau(Quyen.CASH_SHIFT_MANAGE);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (soTien < 0) throw new IllegalArgumentException("Tiền đầu ca phải không âm.");
        thucHienGiaoDich(
                ketNoi -> {
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (kiemTraMo()) throw new IllegalStateException("Tài khoản đã có ca đang mở.");
                    JdbcCaThuNganDAO.capNhatMo(layIdTaiKhoan(), ten, soTien);
                    NhatKyHeThongService.ghiNhatKy("Mở ca", ten);
                    return null;
                });
        thongBao();
    }

    /**
     * Ghi nhận doanh thu bán hàng trong CaThuNganService.
     *
     * @param phuongThuc phương thức
     * @param soTien số tiền
     */
    public void ghiNhanBanHang(PhuongThuc phuongThuc, long soTien) {
        thongBao();
    }

    /**
     * Đóng trong CaThuNganService.
     *
     * @param daDem đã đếm
     * @param ghiChu ghi chú
     */
    public void dong(long daDem, String ghiChu) {
        yeuCau(Quyen.CASH_SHIFT_MANAGE);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (daDem < 0) throw new IllegalArgumentException("Tiền thực đếm phải không âm.");
        thucHienGiaoDich(
                ketNoi -> {
                    // Id hiện tại.
                    long idHienTai = layId();
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (idHienTai == 0) throw new IllegalStateException("Chưa mở ca.");
                    JdbcCaThuNganDAO.demDong(idHienTai);
                    // Dự kiến.
                    long duKien = layTienMatDuKien();
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (JdbcCaThuNganDAO.capNhatDong(daDem, daDem - duKien, ghiChu, idHienTai) != 1)
                        throw new IllegalStateException("Ca đã đóng.");
                    cuoiCungBanGiao =
                            "Thực đếm "
                                    + daDem
                                    + " / dự kiến "
                                    + duKien
                                    + " (chênh lệch "
                                    + (daDem - duKien)
                                    + ")";
                    NhatKyHeThongService.ghiNhatKy("Kết ca", cuoiCungBanGiao);
                    return null;
                });
        thongBao();
    }

    /**
     * Thêm bộ lắng nghe trong CaThuNganService.
     *
     * @param boLangNghe bộ lắng nghe
     */
    public void themBoLangNghe(Runnable boLangNghe) {
        cacBoLangNghe.add(boLangNghe);
    }

    /** Thông báo trong CaThuNganService. */
    private void thongBao() {
        for (Runnable boLangNghe : List.copyOf(cacBoLangNghe)) boLangNghe.run();
    }
}
