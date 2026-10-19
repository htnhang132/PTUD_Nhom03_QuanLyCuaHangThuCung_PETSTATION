package com.petstation.gui.processing.staff;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import com.petstation.gui.components.HopThoai;
import com.petstation.gui.components.common.BieuMauSQL;
import com.petstation.service.PheDuyetService;

import java.awt.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Cung cấp giao diện và xử lý tương tác cho nhanh các quy trình. */
public final class ThaoTacNhanh {
    /** Khởi tạo ThaoTacNhanh với các giá trị được cung cấp. */
    private ThaoTacNhanh() {}

    /**
     * Lấy nghỉ phép trong ThaoTacNhanh.
     *
     * @param cha cha
     */
    public static void layNghiPhep(Component cha) {
        // Các giá trị.
        var cacGiaTri =
                BieuMauSQL.layYeuCauNhap(
                        cha,
                        "Đăng ký nghỉ phép",
                        BieuMauSQL.TruongDuLieu.chuyenThanhChuoi(
                                "date",
                                "Ngày nghỉ (dd/MM/yyyy)",
                                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        BieuMauSQL.TruongDuLieu.chuyenThanhChuoi("reason", "Lý do", ""));
        // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
        if (cacGiaTri == null) return;
        try {
            // Ngày.
            LocalDate ngay =
                    LocalDate.parse(
                            cacGiaTri.get("date"),
                            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                    .withResolverStyle(java.time.format.ResolverStyle.STRICT));
            PheDuyetService.guiThaoTac(
                    PheDuyetService.Loai.LEAVE,
                    cacGiaTri.get("reason"),
                    "Lịch làm việc ngày " + ngay,
                    "Nghỉ phép",
                    "LEAVE",
                    layIdNhanVien(),
                    ngay.toString());
            HopThoai.hienThiThanhCong(cha, "Đã gửi yêu cầu nghỉ phép.");
        } catch (RuntimeException ngoaiLe) {
            HopThoai.hienThiCanhBao(cha, ngoaiLe.getMessage());
        }
    }
}
