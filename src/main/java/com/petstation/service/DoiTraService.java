package com.petstation.service;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.HoTroNghiepVuService.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.dao.impl.JdbcDoiTraDAO;
import com.petstation.model.Quyen;

import java.time.*;
import java.util.*;

/** Điều phối nghiệp vụ và kiểm tra dữ liệu cho đổi trả dịch vụ. */
public final class DoiTraService {
    /**
     * Trả hàng trong DoiTraService.
     *
     * @param muc mục
     * @param soLuong số lượng
     * @param lyDo lý do
     * @return kết quả trả hàng
     */
    public static long traHang(long muc, int soLuong, String lyDo) {
        yeuCau(Quyen.POS_SELL);
        kiemTraDuong(soLuong);
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (lyDo.isBlank()) throw new IllegalArgumentException("Cần lý do đổi trả.");
        return thucHienGiaoDich(
                ketNoi -> {
                    // Các mục.
                    List<Object[]> cacMuc = JdbcDoiTraDAO.timDoiTraMuc(muc);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (cacMuc.isEmpty() || !"PAID".equals(cacMuc.get(0)[6]))
                        throw new IllegalArgumentException("Hóa đơn không còn hợp lệ để trả hàng.");
                    // Chỉ số.
                    Object[] chiSo = cacMuc.get(0);
                    // Đã có.
                    long daCo = JdbcDoiTraDAO.demDoiTraMuc(muc);
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (soLuong + daCo > layGiaTri(chiSo[5]))
                        throw new IllegalArgumentException("Số trả vượt số đã mua chưa trả.");
                    // Hoàn tiền.
                    long hoanTien =
                            layGiaTri(chiSo[2]) == 0
                                    ? 0
                                    : Math.round(
                                            soLuong
                                                    * layGiaTri(chiSo[4])
                                                    * (double) layGiaTri(chiSo[1])
                                                    / layGiaTri(chiSo[2]));
                    // Kết quả.
                    long ketQua =
                            JdbcDoiTraDAO.themDoiTraMuc(
                                    taoMa("DT"), chiSo[0], layIdTaiKhoan(), -hoanTien, lyDo);
                    // Còn lại.
                    int conLai = soLuong;
                    // Hoàn tiền trái.
                    long hoanTienTrai = hoanTien;
                    for (Object[] giaTriSau : JdbcDoiTraDAO.timDoiTraMuc2(muc)) {
                        // Số tiền.
                        int soTien = Math.min(conLai, ((Number) giaTriSau[1]).intValue());
                        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (soTien <= 0) continue;
                        // Phần.
                        long phan =
                                soTien == conLai
                                        ? hoanTienTrai
                                        : Math.round(hoanTien * (double) soTien / soLuong);
                        JdbcDoiTraDAO.capNhatDoiTraMuc(ketQua, muc, giaTriSau[0], soTien, phan);
                        conLai -= soTien;
                        hoanTienTrai -= phan;
                        // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                        if (conLai == 0) break;
                    }
                    // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
                    if (conLai != 0)
                        throw new IllegalArgumentException(
                                "Thiếu thông tin phân bổ lô của hóa đơn gốc.");
                    // Trễ.
                    boolean tre =
                            ((java.sql.Timestamp) chiSo[3])
                                    .toLocalDateTime()
                                    .toLocalDate()
                                    .plusDays(CaiDatService.laySoNgayDuocDoiTra())
                                    .isBefore(LocalDate.now());
                    // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp ứng.
                    if (tre || hoanTien >= CaiDatService.ghiGioiHanHuyPheDuyet())
                        PheDuyetService.guiThaoTac(
                                PheDuyetService.Loai.RETURN,
                                lyDo,
                                "Hóa đơn " + chiSo[0],
                                "Hoàn " + hoanTien,
                                "RETURN",
                                ketQua,
                                "");
                    else apDungDoiTra(ketQua);
                    return ketQua;
                });
    }

    /**
     * Áp dụng đổi trả trong DoiTraService.
     *
     * @param daTra đã trả
     */
    static void apDungDoiTra(long daTra) {
        // Dừng thao tác khi dữ liệu vi phạm điều kiện nghiệp vụ.
        if (JdbcDoiTraDAO.demApDungDoiTra(daTra) == 0)
            throw new IllegalStateException("Phiếu đổi trả đã xử lý.");
        // Hóa đơn.
        Object[] hoaDon =
                JdbcDoiTraDAO.timApDungDoiTra2(daTra).stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Hóa đơn đã hủy."));
        for (Object[] dongDuLieu : JdbcDoiTraDAO.timApDungDoiTra(daTra))
            JdbcDoiTraDAO.capNhatApDungDoiTra(dongDuLieu[1], dongDuLieu[0]);
        JdbcDoiTraDAO.capNhatApDungDoiTra2(daTra);
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (hoaDon[4] != null && layGiaTri(hoaDon[3]) > 0) {
            // Chênh lệch.
            long chenhLech =
                    Math.round(
                            (layGiaTri(hoaDon[2]) - layGiaTri(hoaDon[1]))
                                    * (-layGiaTri(hoaDon[5]))
                                    / (double) layGiaTri(hoaDon[3]));
            // Cũ.
            long cu = JdbcDoiTraDAO.demApDungDoiTra2(hoaDon[4]);
            // Tiếp theo.
            long tiepTheo = Math.max(0, cu + chenhLech);
            JdbcDoiTraDAO.capNhatApDungDoiTra3(tiepTheo, hoaDon[4]);
            JdbcDoiTraDAO.capNhatApDungDoiTra4(hoaDon[4], hoaDon[0], tiepTheo - cu, tiepTheo);
        }
        NhatKyHeThongService.ghiNhatKy("Đổi trả", chuyenThanhChuoi(daTra));
    }
}
