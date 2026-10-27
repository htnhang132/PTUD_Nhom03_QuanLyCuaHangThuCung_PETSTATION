package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.CaThuNganDAO;
import com.petstation.dao.impl.CaThuNganImpl;
import com.petstation.model.PhuongThuc;
import com.petstation.model.Quyen;

import java.time.LocalTime;
import java.util.*;

/** Quản lý mở ca, doanh thu, tiền bàn giao và đóng ca của tài khoản hiện tại. */
public final class CaThuNganService {
    private static final CaThuNganDAO caThuNganDAO = new CaThuNganImpl();

    /** Các bộ lắng nghe. */
    private final List<Runnable> cacBoLangNghe = new ArrayList<>();

    /** Tiêu đề đang chờ. */
    private String tieuDeDangCho = "", cuoiCungBanGiao = "";

    private Object[] layHienTai() {
        if (layPhienHienTai() == null) return null;
        List<Object[]> cacDong = caThuNganDAO.timHienTai(layIdTaiKhoan());
        return cacDong.isEmpty() ? null : cacDong.get(0);
    }

    public long layId() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null ? 0 : ((Number) dongDuLieu[0]).longValue();
    }

    public boolean kiemTraMo() {
        return layId() > 0;
    }

    public String layTenCa() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null ? "" : chuyenThanhChuoi(dongDuLieu[1]);
    }

    public LocalTime layGioMoCa() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null
                ? null
                : ((java.sql.Timestamp) dongDuLieu[2]).toLocalDateTime().toLocalTime().withNano(0);
    }

    public long layTienDauCa() {
        // Dòng dữ liệu.
        Object[] dongDuLieu = layHienTai();
        return dongDuLieu == null ? 0 : ((Number) dongDuLieu[3]).longValue();
    }

    private long layDoanhThu(PhuongThuc phuongThuc) {
        return caThuNganDAO.demBanHang(layId(), phuongThuc.name());
    }

    public long layDoanhThuTienMat() {
        return layDoanhThu(PhuongThuc.CASH);
    }

    public long layDoanhThuChuyenKhoan() {
        return layDoanhThu(PhuongThuc.TRANSFER);
    }

    public long layDoanhThuThe() {
        return layDoanhThu(PhuongThuc.CARD);
    }

    public int demHoaDon() {
        return (int) caThuNganDAO.demSoLuongHoaDon(layId());
    }

    public long layCocHotel(PhuongThuc pt) {
        return caThuNganDAO.layCocHotel(layId(), pt.name());
    }

    public long layTongCocHotel() {
        return layCocHotel(PhuongThuc.CASH)
                + layCocHotel(PhuongThuc.TRANSFER)
                + layCocHotel(PhuongThuc.CARD);
    }

    public long layTienMatDuKien() {
        return layTienDauCa()
                + layDoanhThuTienMat()
                + layTienCocHotel()
                - caThuNganDAO.demDuKienTienMat(layId());
    }

    public long layTienCocHotel() {
        return caThuNganDAO.layTongCocDaNhan(layId());
    }

    public String layNhan() {
        return kiemTraMo() ? "Đang mở – " + layTenCa() : "Chưa mở ca";
    }

    public String layTieuDeDangCho() {
        return tieuDeDangCho;
    }

    public void datTieuDeDangCho(String tieuDe) {
        tieuDeDangCho = tieuDe == null ? "" : tieuDe;
    }

    public String layBanGiaoGanNhat() {
        return cuoiCungBanGiao;
    }

    public void mo(String ten, long soTien) {
        yeuCau(Quyen.CASH_SHIFT_MANAGE);
        if (ten == null || ten.isBlank()) throw new IllegalArgumentException("Chưa chọn tên ca.");
        if (soTien < 0) throw new IllegalArgumentException("Tiền đầu ca phải không âm.");
        thucHienGiaoDich(
                ketNoi -> {
                    if (kiemTraMo()) throw new IllegalStateException("Tài khoản đã có ca đang mở.");
                    caThuNganDAO.capNhatMo(layIdTaiKhoan(), ten, soTien);
                    NhatKyHeThongService.ghiNhatKy("Mở ca", ten);
                    return null;
                });
        thongBao();
    }

    public void ghiNhanBanHang(PhuongThuc phuongThuc, long soTien) {
        thongBao();
    }

    public void dong(long daDem, String ghiChu) {
        dong(daDem, ghiChu, HinhThucBanGiao.NOP_QUY, null);
    }

    public enum HinhThucBanGiao {
        NOP_QUY,
        BAN_GIAO
    }

    public record NguoiNhan(long id, String hoTen, String tenDangNhap) {
        @Override
        public String toString() {
            return hoTen + " (" + tenDangNhap + ")";
        }
    }

    public List<NguoiNhan> layNguoiNhan() {
        yeuCau(Quyen.CASH_SHIFT_MANAGE);
        return caThuNganDAO.layNguoiNhan(layIdTaiKhoan()).stream()
                .map(
                        d ->
                                new NguoiNhan(
                                        ((Number) d[0]).longValue(),
                                        String.valueOf(d[1]),
                                        String.valueOf(d[2])))
                .toList();
    }

    /** Kết ca và lưu người nhận bằng khóa ngoại, cùng giao dịch với số tiền. */
    public void dong(long daDem, String ghiChu, HinhThucBanGiao hinhThuc, Long nguoiNhan) {
        yeuCau(Quyen.CASH_SHIFT_MANAGE);
        Objects.requireNonNull(hinhThuc, "Chưa chọn hình thức bàn giao.");
        if (hinhThuc == HinhThucBanGiao.NOP_QUY && nguoiNhan != null)
            throw new IllegalArgumentException("Nộp quỹ không có người nhận ca.");
        if (daDem < 0) throw new IllegalArgumentException("Tiền thực đếm phải không âm.");
        thucHienGiaoDich(
                ketNoi -> {
                    // Id hiện tại.
                    long idHienTai = layId();
                    if (idHienTai == 0) throw new IllegalStateException("Chưa mở ca.");
                    caThuNganDAO.demDong(idHienTai);
                    // Dự kiến.
                    long duKien = layTienMatDuKien();
                    if (daDem != duKien && (ghiChu == null || ghiChu.isBlank()))
                        throw new IllegalArgumentException("Cần nhập lý do chênh lệch tiền mặt.");
                    if (hinhThuc == HinhThucBanGiao.BAN_GIAO
                            && (nguoiNhan == null
                                    || layNguoiNhan().stream().noneMatch(n -> n.id() == nguoiNhan)))
                        throw new IllegalArgumentException(
                                "Chọn quản lý hoặc thu ngân đang hoạt động để nhận ca.");
                    if (caThuNganDAO.dongCoBanGiao(
                                    daDem,
                                    daDem - duKien,
                                    ghiChu,
                                    hinhThuc.name(),
                                    nguoiNhan,
                                    idHienTai)
                            != 1) throw new IllegalStateException("Ca đã đóng.");
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

    public void themBoLangNghe(Runnable boLangNghe) {
        cacBoLangNghe.add(boLangNghe);
    }

    private void thongBao() {
        for (Runnable boLangNghe : List.copyOf(cacBoLangNghe)) boLangNghe.run();
    }
}
