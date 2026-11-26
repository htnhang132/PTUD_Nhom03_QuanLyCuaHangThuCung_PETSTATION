package com.petstation.gui.chung.trogiup;

import static com.petstation.gui.chung.trogiup.SoTayTroGiup.tao;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;

import java.util.List;
import java.util.Map;

final class HuongDanBieuMau {
    private HuongDanBieuMau() {}

    static void dangKy(Map<String, NoiDungHuongDan> ds) {
        ds.put(
                "DatLichSpaDialog",
                tao(
                        "Đặt và sửa lịch hẹn Spa",
                        "Hẹn thời gian đưa thú cưng đến làm dịch vụ.",
                        List.of(
                                "Tìm và chọn khách hàng, sau đó chọn đúng thú cưng của khách.",
                                "Chọn dịch vụ, nhân viên và ngày giờ hẹn; ghi chú yêu cầu của khách"
                                    + " nếu có.",
                                "Bấm Đặt lịch hẹn khi tạo mới hoặc Lưu thay đổi khi sửa lịch."),
                        List.of(
                                "Chỉ sửa lịch ở trạng thái cho phép. Khi khách đến, dùng Khách đã"
                                    + " đến ở Lịch hẹn Spa để tiếp nhận.",
                                "Lưu lịch hẹn chưa ghi nhận hoàn tất dịch vụ hoặc thanh toán.")));
        ds.put(
                "DatPhongHotelDialog",
                tao(
                        "Đặt, sửa và gia hạn phòng",
                        "Ghi nhận thời gian và thông tin lưu trú của thú cưng.",
                        List.of(
                                "Chọn khách hàng, thú cưng và phòng phù hợp.",
                                "Kiểm tra ngày nhận/trả dự kiến, mức cọc yêu cầu và các thông tin"
                                    + " chăm sóc.",
                                "Đối chiếu chi phí dự kiến rồi bấm nút xác nhận của chế độ đang mở:"
                                    + " đặt mới, sửa hoặc gia hạn."),
                        List.of(
                                "Mức cọc yêu cầu không phải tiền đã thu. Ghi nhận thu cọc qua thao"
                                    + " tác Thu cọc sau khi thực nhận tiền.",
                                "Hệ thống kiểm tra trùng lịch và điều kiện phòng. Hủy để đóng biểu"
                                    + " mẫu khi chưa muốn lưu.")));
        ds.put(
                "NhatKyChamSocDialog",
                tao(
                        "Phiếu chăm sóc",
                        "Ghi và tra cứu nội dung chăm sóc, sức khỏe cùng hình ảnh thú cưng.",
                        List.of(
                                "Đối chiếu thú cưng và phiếu/hợp đồng đang chăm sóc.",
                                "Khi có quyền ghi, nhập tình trạng sức khỏe, công việc chăm sóc và"
                                    + " chọn ảnh nếu cần; bấm Lưu phiếu chăm sóc.",
                                "Chọn bản ghi trong lịch sử để xem; dùng In / Xuất phiếu PDF khi"
                                    + " cần lấy phiếu."),
                        List.of(
                                "Chế độ chỉ xem không hiển thị các thao tác thêm nội dung.",
                                "Kiểm tra đúng ngày và thú cưng trước khi lưu hoặc xuất phiếu.")));
        ds.put(
                "DoiPhuKienDialog",
                tao(
                        "Lập phiếu đổi phụ kiện",
                        "Chọn phụ kiện trả lại và hàng thay thế từ hóa đơn gốc.",
                        List.of(
                                "Tìm hóa đơn gốc và chọn dòng phụ kiện đủ điều kiện đổi.",
                                "Nhập số lượng trả, lý do đổi và chọn sản phẩm thay thế.",
                                "Đối chiếu tiền hàng cũ, hàng mới và khoản chênh lệch.",
                                "Chọn Sang thanh toán để hoàn tất, hoặc Lưu phiếu đổi nháp để xử lý"
                                    + " tiếp sau."),
                        List.of(
                                "Không đổi vượt số lượng còn được phép hoặc ngoài thời hạn áp"
                                    + " dụng.",
                                "Xem đúng khoản cần thu thêm hoặc hoàn lại; lưu nháp chưa ghi nhận"
                                    + " thanh toán.")));
        ds.put(
                "ThuTienDialog",
                tao(
                        "Thu tiền cọc lưu trú",
                        "Xác nhận khoản cọc thực nhận cho hợp đồng.",
                        List.of(
                                "Kiểm tra tiền cọc còn phải thu và nhập số tiền thu lần này.",
                                "Chọn phương thức: với tiền mặt, nhập tiền khách đưa và kiểm tra"
                                    + " tiền thừa; với chuyển khoản, kiểm tra tiền vào thực tế.",
                                "Xác nhận đã thu tiền theo yêu cầu của biểu mẫu rồi bấm Xác nhận"
                                    + " thu cọc."),
                        List.of(
                                "Số tiền thu phải lớn hơn 0 và không vượt khoản cọc còn thiếu; có"
                                    + " thể thu từng phần.",
                                "QR chưa tự xác nhận giao dịch ngân hàng. Chỉ hoàn tất khi luồng"
                                    + " thu cọc thông báo thành công.")));
    }
}
