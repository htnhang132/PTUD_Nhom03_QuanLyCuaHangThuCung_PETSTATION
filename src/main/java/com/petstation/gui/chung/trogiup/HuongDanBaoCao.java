package com.petstation.gui.chung.trogiup;

import static com.petstation.gui.chung.trogiup.SoTayTroGiup.tao;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;

import java.util.List;
import java.util.Map;

final class HuongDanBaoCao {
    private HuongDanBaoCao() {}

    static void dangKy(Map<String, NoiDungHuongDan> ds) {
        ds.put(
                "BaoCaoPanel",
                baoCao(
                        "Thống kê",
                        "Đối chiếu số liệu theo kiểu báo cáo đang xem.",
                        "Đọc tên chỉ tiêu và khoảng kỳ trước khi so sánh các số liệu."));
        ds.put(
                "ThongKeDoanhThuPanel",
                baoCao(
                        "Thống kê doanh thu",
                        "Theo dõi doanh thu và các góc nhìn kinh doanh được cấp quyền.",
                        "Phân biệt doanh thu, hoàn trả và doanh thu thuần theo tiêu đề cột; không"
                                + " cộng trộn các chỉ tiêu khác nhau."));
        ds.put(
                "ThongKeHangHoaPanel",
                baoCao(
                        "Thống kê hàng hóa",
                        "Xem bán hàng theo sản phẩm và các báo cáo tồn kho.",
                        "Báo cáo Top 10 chỉ phản ánh các dòng trong bảng Top 10. Các báo cáo tồn"
                                + " hiện tại không phải tồn lịch sử tại ngày kết thúc kỳ."));
        ds.put(
                "ThongKeSpaPanel",
                baoCao(
                        "Thống kê Spa",
                        "Đối chiếu doanh thu và hoạt động dịch vụ Spa.",
                        "Phân biệt số phiếu, lượt dịch vụ và số tiền theo nhãn của từng kiểu báo"
                                + " cáo."));
        ds.put(
                "ThongKeHotelPanel",
                baoCao(
                        "Thống kê lưu trú",
                        "Theo dõi số liệu hợp đồng, phòng và hoạt động lưu trú.",
                        "Đối chiếu trạng thái hợp đồng; tiền cọc thu trước không đồng nghĩa doanh"
                                + " thu đã quyết toán."));
        ds.put(
                "ThongKeKhachHangPanel",
                baoCao(
                        "Thống kê khách hàng",
                        "Xem giao dịch, thành viên và việc sử dụng quyền lợi khách hàng.",
                        "Đọc đúng nhãn lượt sử dụng, số khách và số tiền; chỉ tiêu hiện tại có thể"
                                + " không phụ thuộc khoảng kỳ."));
        ds.put(
                "ThongKeNhanVienPanel",
                baoCao(
                        "Thống kê nhân viên",
                        "Xem kết quả theo nhân viên và kiểu báo cáo được chọn.",
                        "Phân biệt người lập chứng từ, thu ngân và nhân viên phụ trách theo tên"
                                + " kiểu báo cáo."));
        ds.put(
                "ThongKeCongNoPanel",
                baoCao(
                        "Công nợ nhà cung cấp",
                        "Đối chiếu tổng nhập, số đã trả và dư nợ nhà cung cấp.",
                        "Công nợ theo kỳ nhập lọc theo ngày nhập; đã trả và dư nợ vẫn là số cập"
                            + " nhật hiện tại của các phiếu trong kỳ. Dư nợ hiện tại tổng hợp các"
                            + " phiếu đã ghi nhận, không phải ảnh chụp công nợ trong quá khứ."));
    }

    private static NoiDungHuongDan baoCao(String ten, String mucDich, String rieng) {
        return tao(
                ten,
                mucDich,
                List.of(
                        "Chọn kiểu báo cáo và khoảng kỳ nếu kiểu đó hỗ trợ lọc thời gian; dữ liệu"
                                + " tự tải khi thay đổi lựa chọn.",
                        "Với báo cáo theo thời gian, chọn Biểu đồ: Ngày, Tuần hoặc Tháng để đổi"
                                + " cách gộp cột.",
                        "Bấm cột biểu đồ hoặc chú giải có hỗ trợ xem chi tiết để đối chiếu chứng"
                                + " từ.",
                        "Dùng Xuất Excel để xuất đầy đủ bảng đang hiển thị; dùng Làm mới để tải"
                                + " lại."),
                List.of(
                        rieng,
                        "Khi báo cáo không có dòng dữ liệu, kiểm tra lại kiểu báo cáo và khoảng kỳ."
                            + " Các kiểu bị giới hạn quyền chỉ xuất hiện với tài khoản được phép."),
                new String[] {"F5", "Làm mới báo cáo"});
    }
}
