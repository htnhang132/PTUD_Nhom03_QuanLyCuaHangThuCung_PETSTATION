package com.petstation.gui.chung.trogiup;

import static com.petstation.gui.chung.trogiup.SoTayTroGiup.tao;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;

import java.util.List;
import java.util.Map;

final class HuongDanBanHang {
    private HuongDanBanHang() {}

    static void dangKy(Map<String, NoiDungHuongDan> ds) {
        ds.put("LapHoaDonPanel", lapHoaDon());
        ds.put("ThanhToanPanel", thanhToan());
        ds.put("CaThuNganPanel", caThuNgan());
        ds.put("TimKiemHoaDonPanel", timHoaDon());
        ds.put("DonHangTreoPanel", donNhap());
        ds.put("DoiTraPanel", doiTra());
        ds.put(
                "ChiTietKhachHangPanel",
                tao(
                        "Chi tiết khách hàng",
                        "Xem hồ sơ và lịch sử của khách hàng đã chọn.",
                        List.of(
                                "Đối chiếu thông tin khách ở đầu trang.",
                                "Chọn tab lịch sử để xem hóa đơn, dịch vụ, thú cưng hoặc voucher cá"
                                        + " nhân.",
                                "Chọn hóa đơn và dùng Xem hóa đơn khi cần đối chiếu; Xuất Excel"
                                        + " xuất bảng của tab đang xem."),
                        List.of(
                                "Thông tin lịch sử chỉ đọc; mở danh mục tương ứng nếu cần cập nhật"
                                        + " hồ sơ.")));
        ds.put(
                "InHoaDonPanel",
                tao(
                        "Xem hóa đơn",
                        "Xem và in hóa đơn sau khi thanh toán.",
                        List.of(
                                "Đối chiếu nội dung hóa đơn, số tiền và thông tin khách hàng.",
                                "Bấm In hóa đơn để chọn máy in và gửi bản in.",
                                "Bấm Lập hóa đơn mới để bắt đầu đơn tiếp theo."),
                        List.of(
                                "Chỉ in được khi có hóa đơn đã lưu. Nếu chưa có, chọn Về lập hóa"
                                        + " đơn.",
                                "Có thể tra cứu và in lại chứng từ đã lưu tại Tra cứu hóa đơn."),
                        new String[] {"Ctrl + P", "In hóa đơn"},
                        new String[] {"F1", "Lập hóa đơn mới"}));
        ds.put(
                "LichSuKhachHangPanel",
                tao(
                        "Lịch sử khách hàng",
                        "Đối chiếu lịch sử giao dịch và hồ sơ của một khách hàng.",
                        List.of(
                                "Tìm và chọn đúng khách hàng trước khi xem lịch sử.",
                                "Chuyển giữa các phần hóa đơn, phiếu Spa, thú cưng và voucher để"
                                        + " xem dữ liệu của khách."),
                        List.of(
                                "Màn hình lịch sử chỉ đọc; muốn sửa hồ sơ hãy mở danh mục tương"
                                        + " ứng.",
                                "Đối chiếu số điện thoại và mã khách để tránh nhầm người trùng"
                                        + " tên.")));
    }

    private static NoiDungHuongDan lapHoaDon() {
        return tao(
                "Lập hóa đơn",
                "Tạo giỏ hàng và chuyển thu ngân thu tiền.",
                List.of(
                        "Quét mã vạch, hoặc tìm mã/tên sản phẩm rồi chọn sản phẩm cần thêm vào"
                                + " đơn.",
                        "Kiểm tra số lượng, giá và từng dòng trong giỏ; xóa dòng hoặc Xóa toàn bộ"
                                + " giỏ khi cần.",
                        "Chọn khách hàng nếu cần lưu giao dịch theo khách và sử dụng quyền lợi"
                                + " thành viên.",
                        "Bấm Lưu đơn nháp để xử lý sau, Mở đơn nháp để lấy lại, hoặc Sang thanh"
                                + " toán để thu tiền."),
                List.of(
                        "Cần mở ca thu ngân trước khi lập hóa đơn và thanh toán.",
                        "Kiểm tra tồn khả dụng; giỏ hàng và đơn nháp chưa phải hóa đơn đã thanh"
                                + " toán."),
                new String[] {"Ctrl + K", "Đến ô quét mã/tìm sản phẩm"},
                new String[] {"Ctrl + S", "Lưu đơn nháp"},
                new String[] {"F8", "Mở đơn nháp"},
                new String[] {"F9", "Sang thanh toán"},
                new String[] {"Alt + X", "Xóa toàn bộ giỏ sau khi xác nhận"});
    }

    private static NoiDungHuongDan thanhToan() {
        return tao(
                "Thanh toán",
                "Kiểm tra số phải thu và xác nhận giao dịch của đơn hiện tại.",
                List.of(
                        "Đối chiếu các dòng hàng/dịch vụ, khách hàng và tổng tiền.",
                        "Chọn voucher của khách và bấm Áp dụng voucher nếu có mã hợp lệ.",
                        "Chọn phương thức thanh toán. Với tiền mặt, nhập số tiền khách đưa và kiểm"
                                + " tra tiền thừa.",
                        "Với chuyển khoản, kiểm tra tiền thực nhận rồi đánh dấu xác nhận; bấm Xác"
                                + " nhận thanh toán khi đã thu đủ.",
                        "Dùng Quay lại sửa đơn nếu cần điều chỉnh trước khi ghi nhận."),
                List.of(
                        "Ứng dụng tạo QR có số tiền và nội dung chuyển khoản nhưng chưa tự nhận"
                                + " thông báo tiền vào từ ngân hàng.",
                        "Đơn đổi phụ kiện có thể có số phải thu hoặc phải hoàn; đọc đúng nhãn và số"
                                + " tiền trước khi xác nhận.",
                        "Ca thu ngân phải đang mở. Chỉ hoàn tất khi hệ thống thông báo thành"
                                + " công."),
                new String[] {"F9", "Xác nhận thanh toán khi dữ liệu hợp lệ"});
    }

    private static NoiDungHuongDan caThuNgan() {
        return tao(
                "Ca thu ngân",
                "Mở ca, đối soát tiền mặt và kết ca thu ngân.",
                List.of(
                        "Mở ca: chọn ca làm việc, nhập tổng tiền mặt đầu ca và bấm Mở ca.",
                        "Trước khi kết ca, đối chiếu số thu và số tiền mặt hệ thống dự kiến.",
                        "Nhập tổng tiền mặt thực đếm; chọn Nộp quỹ cửa hàng hoặc Bàn giao nhân"
                                + " viên.",
                        "Nếu bàn giao, chọn người nhận; ghi rõ lý do khi có chênh lệch rồi bấm Kết"
                                + " ca và xác nhận."),
                List.of(
                        "Tiền thực đếm do thu ngân kiểm đếm và nhập, hệ thống không điền hộ.",
                        "Chênh lệch phải có ghi chú; bàn giao nhân viên phải chọn người nhận đang"
                                + " hoạt động.",
                        "Phân biệt tổng thu của ca với tiền mặt trong két; các khoản chuyển khoản"
                                + " và thẻ không phải tiền mặt."));
    }

    private static NoiDungHuongDan timHoaDon() {
        return tao(
                "Tra cứu hóa đơn",
                "Tìm hóa đơn đã lưu, xem chi tiết và xử lý theo quyền.",
                List.of(
                        "Chọn khoảng ngày, phương thức, trạng thái hoặc nhập từ khóa để tìm hóa"
                                + " đơn.",
                        "Chọn một dòng để xem trước nội dung hóa đơn.",
                        "Dùng In lại hoặc Xuất PDF; chọn Đổi phụ kiện khi hóa đơn đủ điều kiện.",
                        "Nếu cần sửa hoặc hủy chứng từ, dùng Yêu cầu sửa hoặc hủy hóa đơn và nhập"
                                + " lý do."),
                List.of(
                        "Nút đổi phụ kiện và gửi yêu cầu chỉ hiện/bật khi đúng quyền và trạng thái"
                                + " chứng từ.",
                        "Gửi yêu cầu sửa/hủy chưa đồng nghĩa hóa đơn đã được thay đổi; cần chờ xử"
                                + " lý phê duyệt."));
    }

    private static NoiDungHuongDan donNhap() {
        return tao(
                "Đơn nháp",
                "Mở lại hoặc xóa các giỏ hàng đang chờ xử lý.",
                List.of(
                        "Tìm và chọn đơn nháp cần xử lý trong danh sách.",
                        "Bấm Tiếp tục xử lý để nạp lại đơn và tiếp tục công việc.",
                        "Chỉ dùng Xóa đơn nháp khi không cần giữ đơn và đọc xác nhận trước khi đồng"
                                + " ý."),
                List.of(
                        "Đơn nháp chưa phải giao dịch thanh toán hoàn tất.",
                        "Đối chiếu nội dung đơn hiện tại trước khi mở đơn khác để tránh bỏ sót công"
                                + " việc đang làm."));
    }

    private static NoiDungHuongDan doiTra() {
        return tao(
                "Đổi phụ kiện",
                "Theo dõi phiếu đổi phụ kiện và lập giao dịch đổi mới.",
                List.of(
                        "Bấm + Đổi phụ kiện để tìm hóa đơn gốc và chọn phụ kiện đủ điều kiện đổi.",
                        "Nhập số lượng, lý do, chọn hàng thay thế rồi kiểm tra số tiền chênh lệch.",
                        "Chọn Sang thanh toán để xác nhận thu thêm hoặc hoàn chênh lệch.",
                        "Trong danh sách phiếu đổi, chọn dòng để xem chi tiết, hóa đơn gốc hoặc hóa"
                                + " đơn thay thế."),
                List.of(
                        "Chỉ phụ kiện và số lượng còn được phép đổi trong thời hạn cấu hình mới"
                                + " được xử lý.",
                        "Phiếu đổi nháp cần hoàn tất bước thanh toán để ghi nhận giao dịch."),
                new String[] {"F2", "Mở thao tác đổi phụ kiện"});
    }
}
