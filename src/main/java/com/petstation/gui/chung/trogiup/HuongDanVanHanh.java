package com.petstation.gui.chung.trogiup;

import static com.petstation.gui.chung.trogiup.SoTayTroGiup.tao;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;
import com.petstation.gui.nhansu.LichTuanPanel.CheDo;

import java.util.List;
import java.util.Map;

final class HuongDanVanHanh {
    private HuongDanVanHanh() {}

    static void dangKy(Map<String, NoiDungHuongDan> ds) {
        ds.put("NhapKhoPanel", nhapKho());
        ds.put("XuatHuyKhoPanel", xuatHuy());
        ds.put("KiemKePanel", kiemKe());
        ds.put("TimKiemTonTheoLoPanel", tonTheoLo());
        ds.put("TiepNhanSpaPanel", dieuPhoiSpa());
        ds.put("LapPhieuSpaPanel", lapPhieuSpa());
        ds.put("LichHenSpaPanel", lichHenSpa());
        ds.put("LuuTruHotelPanel", hopDong());
        ds.put("TraCuuPhongHotelPanel", soDoPhong());
        ds.put("LichTuanPanel", lich(CheDo.HIEN_THI_TAT_CA));
    }

    private static NoiDungHuongDan nhapKho() {
        return tao(
                "Nhập kho",
                "Ghi nhận hàng nhập theo nhà cung cấp và từng lô hàng.",
                List.of(
                        "Bấm + Lập phiếu nhập kho, chọn đúng nhà cung cấp và sản phẩm.",
                        "Nhập số lô, hạn dùng theo mặt hàng, số lượng, giá nhập và số tiền đã trả"
                                + " nhà cung cấp.",
                        "Kiểm tra các dòng hàng và tổng tiền ở bước xác nhận trước khi lưu.",
                        "Chọn phiếu để Xem phiếu nhập; dùng Ghi nhận trả nhà cung cấp nếu phiếu còn"
                                + " nợ."),
                List.of(
                        "Không tự đoán số lô hoặc hạn dùng; đối chiếu hàng thực nhận và chứng từ"
                                + " nhà cung cấp.",
                        "Số tiền trả thêm không được vượt dư nợ của phiếu. Chỉ phiếu lưu thành công"
                                + " mới cập nhật tồn kho."));
    }

    private static NoiDungHuongDan xuatHuy() {
        return tao(
                "Xuất hủy kho",
                "Lập phiếu ghi nhận hàng phải loại khỏi kho.",
                List.of(
                        "Bấm + Lập phiếu xuất hủy, chọn đúng lô hàng cần hủy.",
                        "Nhập số lượng và lý do hủy, kiểm tra giá trị phiếu rồi xác nhận.",
                        "Theo dõi trạng thái phiếu để biết đã xử lý hay đang chờ phê duyệt."),
                List.of(
                        "Không xuất quá tồn khả dụng của lô.",
                        "Phiếu vượt hạn mức cấu hình cần phê duyệt; gửi yêu cầu chưa đồng nghĩa tồn"
                                + " kho đã được điều chỉnh."));
    }

    private static NoiDungHuongDan kiemKe() {
        return tao(
                "Kiểm kê tồn kho",
                "Đếm thực tế theo lô và lập biên bản trước khi điều chỉnh tồn.",
                List.of(
                        "Bấm Bắt đầu kiểm kê; chọn phạm vi và Lọc danh sách trước khi đếm.",
                        "Nhập số lượng đã đếm, hoặc quét mã để cộng một đơn vị. Chọn đúng lô khi mã"
                                + " khớp nhiều lô.",
                        "Kiểm tra các lô đã đếm, ghi lý do chênh lệch và bấm Lập biên bản kiểm kê.",
                        "Người có quyền phê duyệt chọn biên bản trong danh sách và bấm Duyệt và"
                                + " điều chỉnh tồn."),
                List.of(
                        "Ẩn tồn sổ sách khi đếm giúp kiểm đếm độc lập; xem bước đối chiếu trước khi"
                                + " lưu biên bản.",
                        "Đổi phạm vi phải thực hiện trước khi bắt đầu đếm. Lọc tìm trong lúc đếm"
                                + " không làm mất các lô đã ghi nhận.",
                        "Lập biên bản chưa tự điều chỉnh tồn. Nếu tồn thay đổi trong lúc đếm, cần"
                                + " tải và đối chiếu lại."));
    }

    private static NoiDungHuongDan tonTheoLo() {
        return tao(
                "Tồn kho theo lô",
                "Tra cứu số lượng và hạn dùng của từng lô hàng.",
                List.of(
                        "Tìm theo sản phẩm hoặc số lô và chọn bộ lọc trạng thái phù hợp.",
                        "Đối chiếu số lô, hạn dùng và tồn khả dụng trong bảng.",
                        "Nếu có quyền và cần loại hàng, chọn lô rồi dùng thao tác xuất hủy."),
                List.of(
                        "Tra cứu không làm thay đổi tồn; nhập kho, xuất hủy và kiểm kê là các luồng"
                                + " ghi nhận thay đổi.",
                        "Đọc cả ngày hết hạn và trạng thái, không chỉ dựa vào màu cảnh báo."));
    }

    private static NoiDungHuongDan dieuPhoiSpa() {
        return tao(
                "Điều phối phiếu Spa",
                "Theo dõi và xử lý phiếu dịch vụ theo tiến độ.",
                List.of(
                        "Lọc và chọn phiếu trong tab Điều phối phiếu Spa để xem dịch vụ và trạng"
                                + " thái.",
                        "Dùng + Lập phiếu hoặc tab Lập phiếu mới để tiếp nhận khách mới.",
                        "Bắt đầu làm khi đã có dịch vụ; cập nhật hoàn tất sau khi thực hiện xong.",
                        "Chuyển phiếu đủ điều kiện sang thanh toán; mở tab Lịch hẹn Spa để quản lý"
                                + " hẹn trước."),
                List.of(
                        "Thao tác được bật theo trạng thái phiếu và quyền của nhân viên.",
                        "Hoàn tất dịch vụ và thanh toán là hai bước khác nhau; kiểm tra tình trạng"
                                + " thanh toán của phiếu."));
    }

    private static NoiDungHuongDan lapPhieuSpa() {
        return tao(
                "Lập phiếu Spa",
                "Tiếp nhận thú cưng và xác định dịch vụ theo loài, cân nặng.",
                List.of(
                        "Tìm khách hàng và chọn đúng thú cưng; có thể thêm khách/thú hoặc chọn lịch"
                                + " hẹn có sẵn.",
                        "Ghi cân nặng thực tế và thông tin sức khỏe cần lưu ý.",
                        "Chọn gói phù hợp rồi bấm + Thêm gói; kiểm tra danh sách dịch vụ và tổng"
                                + " tiền.",
                        "Chọn Tạo phiếu hoặc Tạo phiếu và bắt đầu dịch vụ. Nếu chưa làm xong, dùng"
                                + " Lưu phiếu tạm và Mở phiếu tạm."),
                List.of(
                        "Giá dựa trên loài và khoảng cân nặng đã cấu hình; cần có mức giá phù hợp"
                                + " cho thú cưng.",
                        "Xóa nội dung đã nhập là đặt lại biểu mẫu; phiếu tạm chưa phải phiếu dịch"
                                + " vụ đã tiếp nhận."));
    }

    private static NoiDungHuongDan lichHenSpa() {
        return tao(
                "Lịch hẹn Spa",
                "Đặt lịch trước và chuyển lịch hẹn sang tiếp nhận khi khách đến.",
                List.of(
                        "Chọn khoảng thời gian hoặc chế độ xem danh sách/lịch để tìm cuộc hẹn.",
                        "Dùng thao tác đặt lịch để chọn khách, thú cưng, thời điểm và dịch vụ.",
                        "Chọn lịch hẹn để sửa, hủy hoặc tiếp nhận theo trạng thái cho phép."),
                List.of(
                        "Đặt lịch hẹn chưa phải hoàn tất dịch vụ và chưa ghi nhận thanh toán.",
                        "Kiểm tra đúng khách, thú và thời gian; lịch đã tiếp nhận được theo dõi"
                                + " tiếp ở phiếu Spa."));
    }

    private static NoiDungHuongDan hopDong() {
        return tao(
                "Hợp đồng lưu trú",
                "Theo dõi đặt phòng, nhận phòng, cọc và quyết toán lưu trú.",
                List.of(
                        "Chọn hợp đồng trong tab Hợp đồng lưu trú và xem trạng thái hiện tại.",
                        "Dùng Đặt phòng để tạo hợp đồng; nhận phòng khi khách đưa thú đến và ghi"
                                + " nhận thu cọc thực tế.",
                        "Khi đang lưu trú, ghi phiếu chăm sóc hoặc gia hạn nếu cần.",
                        "Chọn Trả phòng & Quyết toán để kiểm tra tiền phòng, phụ phí và tiền cọc"
                                + " trước khi sang thanh toán.",
                        "Mở Thao tác khác để xem lịch sử cọc, sửa/hủy đặt phòng hoặc xác nhận dọn"
                                + " phòng theo trạng thái."),
                List.of(
                        "Mức cọc yêu cầu và tiền đã thu là hai số khác nhau; chỉ thu cọc sau khi"
                                + " nhận tiền thực tế.",
                        "Tiền cọc đã thu được đối trừ khi quyết toán. Kiểm tra số còn thu hoặc cần"
                                + " hoàn.",
                        "Đặt, sửa, hủy, gia hạn và trả phòng phụ thuộc trạng thái hợp đồng và quyền"
                                + " của tài khoản."));
    }

    private static NoiDungHuongDan soDoPhong() {
        return tao(
                "Sơ đồ phòng lưu trú",
                "Xem phòng và mở thao tác theo trạng thái đang có.",
                List.of(
                        "Dùng bộ lọc để tìm phòng; đọc trạng thái và thông tin trên từng thẻ.",
                        "Bấm thẻ phòng để xem thông tin và các thao tác đang được phép.",
                        "Với phòng trống, tạo đặt phòng; với hợp đồng đang ở, mở phiếu chăm sóc,"
                                + " gia hạn hoặc quyết toán.",
                        "Xác nhận dọn phòng xong hoặc hoàn tất bảo trì khi công việc thực tế đã"
                                + " hoàn tất."),
                List.of(
                        "Phân biệt trạng thái phòng và trạng thái hợp đồng; đối chiếu ngày đặt để"
                                + " tránh nhầm lịch.",
                        "Có thể chuyển sang tab Hợp đồng lưu trú để tra cứu danh sách và lịch sử"
                                + " cọc."));
    }

    static NoiDungHuongDan lich(CheDo cheDo) {
        String ten =
                switch (cheDo) {
                    case SUA -> "Phân lịch nhân viên";
                    case CA_NHAN -> "Lịch cá nhân";
                    case HIEN_THI_TAT_CA -> "Tra cứu lịch làm việc";
                };
        String thaoTac =
                switch (cheDo) {
                    case SUA ->
                            "Bấm Thêm người vào ca để phân ca; chọn thẻ nhân viên để bỏ khỏi ca. Có"
                                    + " thể Sao chép lịch tuần trước, Đổi ca và Khóa lịch tuần.";
                    case CA_NHAN ->
                            "Dùng Đăng ký nghỉ phép hoặc Yêu cầu đổi ca để gửi yêu cầu; theo dõi"
                                    + " kết quả xử lý trước khi thay đổi lịch đi làm.";
                    case HIEN_THI_TAT_CA ->
                            "Xem nhân viên trong từng ca để tra cứu lịch cả cửa hàng; chế độ này"
                                    + " không chỉnh phân ca.";
                };
        return tao(
                ten,
                "Xem lịch theo tuần và thực hiện các thao tác của chế độ đang mở.",
                List.of(
                        "Dùng Trở về, Tiếp, chọn ngày hoặc Hiện tại để chuyển tuần.",
                        thaoTac,
                        "Dùng In lịch hoặc Xuất Excel để lấy lịch của tuần đang xem."),
                List.of(
                        "Ô viền đỏ báo ca thiếu người. Kiểm tra ngày và tuần trước khi thao tác.",
                        "Việc sửa lịch phụ thuộc quyền, trạng thái khóa và quy tắc giờ làm; yêu cầu"
                                + " cá nhân cần được duyệt."),
                new String[] {"Ctrl + P", "In lịch tuần đang xem"});
    }
}
