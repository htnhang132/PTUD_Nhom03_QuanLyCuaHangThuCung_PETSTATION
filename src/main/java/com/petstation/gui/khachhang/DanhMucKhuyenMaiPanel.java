package com.petstation.gui.khachhang;

import static com.petstation.gui.chung.TruongDanhMuc.docNgay;

import com.petstation.gui.chung.QuanLyDanhMucPanel;
import com.petstation.service.chung.DieuKienTaiDuLieu;
import com.petstation.service.khachhang.KhuyenMaiService;
import com.petstation.util.DinhDang;

import java.time.LocalDate;
import java.util.List;

import javax.swing.SwingUtilities;

// Màn hình Danh mục > Khuyến mãi
// Màn hình: Chương trình khuyến mãi (Mã: DANH_MUC_KHUYEN_MAI)
// Luồng xử lý: GUI -> KhuyenMaiService -> ChuongTrinhKhuyenMaiDAOImpl -> SQL
// (chuong_trinh_khuyen_mai)
// Mô tả: Thiết lập các chương trình chiết khấu, giảm giá theo hóa đơn, điều kiện thời gian áp dụng
// Phân hệ: khachhang
public final class DanhMucKhuyenMaiPanel extends QuanLyDanhMucPanel {

    // 1. KHỞI TẠO MÀN HÌNH & GẮN HẬU TỐ ĐƠN VỊ THEO LOẠI GIẢM (% HOẶC ĐỒNG)

    public DanhMucKhuyenMaiPanel() {
        super(
                taoCauHinh(),
                taoCacTruong(),
                KhuyenMaiService.macDinh()
                        .chuanBi(
                                !SwingUtilities.isEventDispatchThread()
                                        || !DieuKienTaiDuLieu.coTheTachLuong()));
        ganHauToTheoTruong(2, 4, loai -> "Giảm %".equals(loai) ? "%" : "đ");
    }

    // 2. CẤU HÌNH BỘ LỌC, TÌM KIẾM & KIỂM TRA DỮ LIỆU HỢP LỆ (VALIDATION)

    private static CauHinh taoCauHinh() {
        return CauHinh.tao("Danh mục khuyến mãi", "khuyến mãi")
                .themBoLoc("Loại giảm", d -> String.valueOf(d[2]))
                .themBoLoc("Phạm vi", d -> String.valueOf(d[3]))
                .themBoLoc(
                        "Hiệu lực",
                        d -> {
                            LocalDate tu = docNgay(d[6]), den = docNgay(d[7]);
                            if ("Ngừng áp dụng".equals(d[8])) return "Đã tắt";
                            if (den != null && den.isBefore(LocalDate.now())) return "Hết hạn";
                            if (tu != null && tu.isAfter(LocalDate.now())) return "Chưa đến ngày";
                            return "Trong thời gian áp dụng";
                        })
                .datTienTo("KM")
                .timKiem("Tìm theo mã, tên chương trình...")
                .datTrangThai(8, "Đang áp dụng", "Ngừng áp dụng")
                .datBoKiemTra(
                        dongDuLieu -> {
                            LocalDate tu = docNgay(dongDuLieu[6]);
                            LocalDate den = docNgay(dongDuLieu[7]);
                            String loi =
                                    KhuyenMaiService.kiemTraKhuyenMai(
                                            tu,
                                            den,
                                            String.valueOf(dongDuLieu[2]),
                                            DinhDang.doc(String.valueOf(dongDuLieu[4])));
                            if (loi != null) return loi;
                            String tt = String.valueOf(dongDuLieu[8]);
                            if ("Chưa áp dụng".equals(tt)
                                    && (tu == null || !tu.isAfter(LocalDate.now()))) {
                                return "Trạng thái 'Chưa áp dụng' chỉ dùng khi Từ ngày sau hôm"
                                        + " nay; hãy chọn 'Đang áp dụng' hoặc 'Ngừng áp dụng'.";
                            }
                            if ("Đang áp dụng".equals(tt)
                                    && tu != null
                                    && tu.isAfter(LocalDate.now())) {
                                return "Từ ngày đang ở tương lai; hãy chọn trạng thái 'Chưa áp"
                                        + " dụng' hoặc 'Ngừng áp dụng'.";
                            }
                            return null;
                        });
    }

    // 3. ĐỊNH NGHĨA CÁC CỘT BẢNG & TRƯỜNG NHẬP LIỆU TRÊN BIỂU MẪU KHUYẾN MÃI

    private static List<TruongDuLieu> taoCacTruong() {
        return List.of(
                TruongDuLieu.tao("Mã KM", Kieu.VAN_BAN).chiDoc(),
                TruongDuLieu.tao("Tên chương trình", Kieu.VAN_BAN).batBuoc(),
                TruongDuLieu.tao("Loại", Kieu.HOP_CHON).datLuaChon("Giảm %", "Giảm tiền"),
                TruongDuLieu.tao("Phạm vi", Kieu.HOP_CHON)
                        .datLuaChon("Sản phẩm", "Dịch vụ", "Cả đơn"),
                TruongDuLieu.tao("Giá trị", Kieu.SO_NGUYEN)
                        .batBuoc()
                        .sapXepSo()
                        .hienThiTheoDong(
                                (d, v) -> {
                                    if (v == null || v.isBlank()) return "—";
                                    long so = DinhDang.doc(v);
                                    return "Giảm %".equals(d[2]) ? so + "%" : DinhDang.dinhDang(so);
                                }),
                TruongDuLieu.tao("Đơn tối thiểu", Kieu.TIEN_TE).an().datMacDinh("0"),
                TruongDuLieu.tao("Từ ngày", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Đến ngày", Kieu.NGAY).batBuoc(),
                TruongDuLieu.tao("Trạng thái", Kieu.HOP_CHON)
                        .datLuaChon("Chưa áp dụng", "Đang áp dụng", "Ngừng áp dụng")
                        .datMacDinh("Đang áp dụng"));
    }
}
