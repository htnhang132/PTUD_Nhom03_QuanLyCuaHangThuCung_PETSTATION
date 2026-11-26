package com.petstation.gui.chung.trogiup;

import static com.petstation.gui.chung.trogiup.SoTayTroGiup.tao;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;

import java.util.List;
import java.util.Map;

final class HuongDanHeThong {
    private HuongDanHeThong() {}

    static void dangKy(Map<String, NoiDungHuongDan> ds) {
        ds.put(
                "TongQuanPanel",
                tao(
                        "Tổng quan",
                        "Xem nhanh tình hình cửa hàng và mở công việc hằng ngày.",
                        List.of(
                                "Chọn lối tắt công việc ở đầu trang để mở màn hình tương ứng.",
                                "Xem doanh thu ca, cảnh báo tồn thấp và yêu cầu chờ duyệt.",
                                "Xem biểu đồ 7 ngày; dùng Làm mới khi cần cập nhật số liệu."),
                        List.of(
                                "Lối tắt chỉ xuất hiện khi tài khoản có quyền truy cập.",
                                "Doanh thu trên biểu đồ tổng hợp hóa đơn và hoàn trả đã lưu.")));
        ds.put(
                "DoiMatKhauPanel",
                tao(
                        "Đổi mật khẩu",
                        "Đổi mật khẩu của tài khoản đang đăng nhập.",
                        List.of(
                                "Nhập mật khẩu hiện tại, mật khẩu mới và nhập lại mật khẩu mới.",
                                "Dùng nút hiện/ẩn ở từng ô nếu cần kiểm tra nội dung đã nhập.",
                                "Bấm Đổi mật khẩu để lưu; Xóa nội dung đã nhập để nhập lại."),
                        List.of(
                                "Mật khẩu mới cần ít nhất 8 ký tự, khác mật khẩu hiện tại; hai ô"
                                        + " mới phải khớp.",
                                "Chỉ hoàn tất khi có thông báo cập nhật thành công."),
                        new String[] {"Ctrl + S", "Đổi mật khẩu"}));
        ds.put(
                "CaiDatPanel",
                tao(
                        "Cài đặt hệ thống",
                        "Điều chỉnh thông tin cửa hàng và các tham số vận hành.",
                        List.of(
                                "Tab Cửa hàng và thanh toán: sửa thông tin cửa hàng, tài khoản nhận"
                                        + " tiền, VAT và máy in.",
                                "Tab Vận hành: sửa cảnh báo kho, kiểm kê, phân ca, phí lưu trú,"
                                        + " thời hạn đổi trả và thư mục ảnh.",
                                "Bấm In thử trang mẫu để kiểm tra máy in đã chọn.",
                                "Bấm Lưu cài đặt ở cuối trang để lưu thay đổi của cả hai tab."),
                        List.of(
                                "Đặt lại cấu hình chỉ điền lại các tham số mặc định; cần bấm Lưu"
                                        + " cài đặt để áp dụng.",
                                "Kiểm tra đúng ngân hàng, số tài khoản và chủ tài khoản trước khi"
                                        + " lưu để thu tiền QR.",
                                "Các nút thay đổi chỉ được bật khi tài khoản có quyền cấu hình."),
                        new String[] {"Ctrl + S", "Lưu cài đặt"}));
        ds.put(
                "MaTranQuyenPanel",
                tao(
                        "Quyền theo vai trò",
                        "Tra cứu các quyền được cấp cho từng vai trò.",
                        List.of(
                                "Đối chiếu hàng quyền với cột vai trò trong bảng.",
                                "Chuyển sang tab Tài khoản để xem hoặc đổi vai trò của từng tài"
                                        + " khoản."),
                        List.of(
                                "Bảng quyền chỉ đọc; mỗi tài khoản có đúng một vai trò.",
                                "Đổi vai trò tại Tài khoản và lưu, không sửa trực tiếp các ô"
                                        + " quyền.")));
        ds.put("NhatKyHeThongPanel", nhatKy());
        ds.put("TrungTamPheDuyetPanel", pheDuyet());
    }

    private static NoiDungHuongDan nhatKy() {
        return tao(
                "Nhật ký hệ thống",
                "Tra cứu sự kiện để đối soát hoạt động của cửa hàng.",
                List.of(
                        "Chọn khoảng ngày, loại sự kiện, kết quả và nhân viên để lọc nhật ký.",
                        "Gõ từ khóa để tìm nhân viên, thao tác, đối tượng hoặc mã chứng từ.",
                        "Nhấp đúp dòng để xem nội dung đầy đủ; dùng Xuất Excel để xuất kết quả."),
                List.of(
                        "Nhật ký chỉ đọc; mỗi lượt tra cứu lấy tối đa 2.000 dòng. Thu hẹp khoảng"
                                + " ngày nếu cần.",
                        "Đọc lý do và thông tin chứng từ trong chi tiết khi đối soát hủy, sửa hoặc"
                                + " điều chỉnh."));
    }

    private static NoiDungHuongDan pheDuyet() {
        return tao(
                "Phê duyệt yêu cầu",
                "Xem xét yêu cầu thay đổi trước khi quyết định xử lý.",
                List.of(
                        "Lọc và chọn yêu cầu cần xử lý; đọc người gửi, lý do và nội dung chi tiết.",
                        "Đối chiếu dữ liệu trước và sau thay đổi nếu yêu cầu có thông tin này.",
                        "Chọn Duyệt, Từ chối hoặc Yêu cầu bổ sung theo nội dung đã kiểm tra."),
                List.of(
                        "Từ chối và yêu cầu bổ sung cần nêu lý do để người gửi biết cách xử lý.",
                        "Chỉ yêu cầu ở trạng thái phù hợp mới xử lý được. Khi duyệt, hệ thống thực"
                                + " hiện thay đổi tương ứng."));
    }
}
