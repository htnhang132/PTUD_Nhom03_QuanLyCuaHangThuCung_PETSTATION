package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.DoiHangDAO;
import com.petstation.dao.DoiTraDAO;
import com.petstation.dao.impl.DoiHangImpl;
import com.petstation.dao.impl.DoiTraImpl;
import com.petstation.model.GioHang;
import com.petstation.model.Quyen;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/** Đổi phụ kiện trong ba ngày, thu phần chênh lệch; không phát sinh hoàn tiền. */
public final class DoiTraService {
    private static final DoiHangDAO doiHangDAO = new DoiHangImpl();
    private static final DoiTraDAO doiTraDAO = new DoiTraImpl();

    private DoiTraService() {}

    /** API hoàn tiền cũ chỉ còn để báo rõ quy định khi gặp lời gọi cũ. */
    public static long traHang(long muc, int soLuong, String lyDo) {
        yeuCau(Quyen.POS_SELL);
        throw new IllegalArgumentException(
                "Cần chọn hàng thay thế để đổi phụ kiện; không hoàn tiền.");
    }

    static void apDungDoiTra(long id) {
        throw new IllegalArgumentException(
                "Phiếu hoàn tiền lịch sử chỉ được tra cứu; không áp dụng thành giao dịch mới.");
    }

    private record DuLieu(Object[] goc, long giaTri, long daDoi) {}

    private static long so(Object v) {
        return ((Number) v).longValue();
    }

    private static long phanBo(long soLuong, Object[] goc) {
        if (so(goc[2]) <= 0)
            throw new IllegalArgumentException("Hóa đơn gốc thiếu tạm tính để phân bổ giảm giá.");
        return BigDecimal.valueOf(soLuong)
                .multiply(new BigDecimal(goc[4].toString()))
                .multiply(new BigDecimal(goc[1].toString()))
                .divide(new BigDecimal(goc[2].toString()), 0, RoundingMode.DOWN)
                .longValueExact();
    }

    private static DuLieu kiemTra(long dong, int soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng đổi phải dương.");
        var cacDong = doiHangDAO.timDongGoc(dong);
        if (cacDong.isEmpty() || !"PAID".equals(cacDong.get(0)[6]))
            throw new IllegalArgumentException("Hóa đơn gốc không hợp lệ.");
        Object[] goc = cacDong.get(0);
        if (!"Phụ kiện".equals(goc[7]))
            throw new IllegalArgumentException("Chỉ được đổi hàng thuộc nhóm Phụ kiện.");
        if (doiHangDAO.laHoaDonThayThe(so(goc[0])))
            throw new IllegalArgumentException("Hàng thay thế đã đổi không được đổi tiếp.");
        LocalDate ngay = ((java.sql.Timestamp) goc[3]).toLocalDateTime().toLocalDate();
        if (ngay.isAfter(LocalDate.now()) || ngay.plusDays(3).isBefore(LocalDate.now()))
            throw new IllegalArgumentException(
                    "Chỉ được đổi phụ kiện trong ba ngày kể từ ngày mua.");
        long daDoi = doiTraDAO.demDoiTraMuc(dong);
        if (daDoi + soLuong > so(goc[5]))
            throw new IllegalArgumentException("Số lượng đổi vượt số đã mua chưa đổi.");
        return new DuLieu(goc, phanBo(daDoi + soLuong, goc) - phanBo(daDoi, goc), daDoi);
    }

    /** Nạp hàng thay thế và tín dụng đổi hàng vào màn thanh toán chung. */
    public static void lapDoiHang(
            GioHang gio, long dong, int soLuong, String maThayThe, int soLuongMoi, String lyDo) {
        yeuCau(Quyen.POS_SELL);
        if (!gio.kiemTraTrong())
            throw new IllegalArgumentException("Hãy thanh toán hoặc treo giỏ hiện tại trước.");
        if (soLuongMoi <= 0 || lyDo == null || lyDo.isBlank())
            throw new IllegalArgumentException("Cần số lượng thay thế và lý do đổi.");
        thucHienGiaoDich(
                c -> {
                    DuLieu dl = kiemTra(dong, soLuong);
                    var sp = doiHangDAO.timThayThe(maThayThe);
                    if (sp.isEmpty() || !"Phụ kiện".equals(sp.get(0)[3]))
                        throw new IllegalArgumentException(
                                "Hàng thay thế phải là phụ kiện đang kinh doanh.");
                    long gia = so(sp.get(0)[2]);
                    if (Math.multiplyExact(gia, soLuongMoi) < dl.giaTri())
                        throw new IllegalArgumentException(
                                "Giá trị hàng thay thế không được thấp hơn giá trị được đổi.");
                    gio.xoa();
                    gio.datCacDong(
                            List.of(
                                    new GioHang.DongHang(
                                            maThayThe, sp.get(0)[1].toString(), gia, soLuongMoi)));
                    Object[] g = dl.goc();
                    gio.datKhachHang(g[8].toString(), g[9].toString(), so(g[10]));
                    gio.datNguon("EXCHANGE", dong);
                    gio.datDoiHang(
                            new GioHang.YeuCauDoiHang(
                                    dong, soLuong, lyDo, dl.giaTri(), g[8].toString()));
                    return null;
                });
    }

    /** Khóa hóa đơn gốc; ghi hàng nhập lại trong transaction thanh toán. */
    static long chuanBi(GioHang gio) {
        var yc = gio.layDoiHang();
        if (yc == null || yc.lyDo().isBlank())
            throw new IllegalArgumentException("Thiếu thông tin đổi hàng.");
        DuLieu dl = kiemTra(yc.dongGoc(), yc.soLuong());
        if (dl.giaTri() != yc.giaTriDuocDoi()
                || !dl.goc()[8].toString().equals(gio.layMaKhachHang())
                || gio.tinhTong() < 0
                || !gio.layMaVoucher().isBlank()
                || gio.layGiamGiaPhanTram() != 0)
            throw new IllegalArgumentException(
                    "Thông tin đổi hàng đã thay đổi; hãy lập lại phiếu.");
        for (var dong : gio.layCacDong()) {
            var sp = doiHangDAO.timThayThe(dong.ma());
            if (sp.isEmpty() || !"Phụ kiện".equals(sp.get(0)[3]))
                throw new IllegalArgumentException("Chỉ chọn phụ kiện thay thế.");
        }
        long phieu =
                doiHangDAO.taoPhieu(
                        so(dl.goc()[0]),
                        layIdTaiKhoan(),
                        gio.tinhTong(),
                        yc.lyDo(),
                        dl.giaTri(),
                        gio.layKhoaThanhToan());
        int con = yc.soLuong();
        long tienCon = dl.giaTri();
        for (Object[] lo : doiTraDAO.laySoLuongLoConDuocDoi(yc.dongGoc())) {
            int luong = (int) Math.min(con, so(lo[1]));
            if (luong <= 0) continue;
            long tien = luong == con ? tienCon : dl.giaTri() * luong / yc.soLuong();
            doiTraDAO.capNhatDoiTraMuc(phieu, yc.dongGoc(), lo[0], luong, tien);
            doiTraDAO.capNhatApDungDoiTra(luong, lo[0]);
            con -= luong;
            tienCon -= tien;
            if (con == 0) break;
        }
        if (con != 0) throw new IllegalArgumentException("Thiếu phân bổ lô hàng gốc.");
        return phieu;
    }

    static long tinhDiemCong(GioHang gio) {
        var goc = doiHangDAO.timDongGoc(gio.layDoiHang().dongGoc()).get(0);
        long cu = Math.addExact(so(goc[1]), doiHangDAO.layChenhLechTruoc(so(goc[0])));
        return Math.addExact(cu, gio.tinhTong()) / 10000 - cu / 10000;
    }

    static void hoanTat(long phieu, long hoaDon) {
        doiHangDAO.hoanTat(phieu, hoaDon);
        NhatKyHeThongService.ghiNhatKy("Đổi phụ kiện", phieu + " / hóa đơn chênh lệch " + hoaDon);
    }
}
