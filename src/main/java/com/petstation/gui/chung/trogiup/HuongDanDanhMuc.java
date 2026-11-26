package com.petstation.gui.chung.trogiup;

import static com.petstation.gui.chung.trogiup.SoTayTroGiup.tao;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;

import java.util.List;
import java.util.Map;

final class HuongDanDanhMuc {
    private HuongDanDanhMuc() {}

    static void dangKy(Map<String, NoiDungHuongDan> ds) {
        ds.put(
                "DanhMucKhachHangPanel",
                danhMuc(
                        "Khách hàng",
                        "Lưu hồ sơ liên hệ và tra cứu khách hàng của cửa hàng.",
                        "Kiểm tra số điện thoại trước khi lưu để tránh tạo trùng hồ sơ. Điểm và"
                                + " hạng được cập nhật theo quy tắc, không nhập tay."));
        ds.put(
                "DanhMucThuCungPanel",
                danhMuc(
                        "Hồ sơ thú cưng",
                        "Quản lý thú cưng gắn với từng chủ nuôi.",
                        "Chọn đúng chủ nuôi, loài và thông tin thú cưng trước khi lưu; hồ sơ thú"
                                + " cưng cần có chủ nuôi."));
        ds.put(
                "DanhMucNhanVienPanel",
                danhMuc(
                        "Nhân viên",
                        "Quản lý hồ sơ và trạng thái làm việc của nhân viên.",
                        "Hồ sơ nhân viên và tài khoản đăng nhập là hai phần riêng; cấp tài khoản"
                                + " tại Tài khoản và phân quyền."));
        ds.put(
                "DanhSachSanPhamPanel",
                danhMuc(
                        "Sản phẩm",
                        "Quản lý thông tin bán hàng và phân loại sản phẩm.",
                        "Tồn kho được cập nhật qua nhập kho, xuất hủy và kiểm kê; không sửa tồn tại"
                                + " danh mục sản phẩm."));
        ds.put(
                "DanhMucNhaCungCapPanel",
                danhMuc(
                        "Nhà cung cấp",
                        "Quản lý thông tin liên hệ của nhà cung cấp hàng hóa.",
                        "Ghi nhận thanh toán công nợ tại Nhập kho bằng Ghi nhận trả nhà cung cấp,"
                                + " không sửa số nợ trong danh mục."));
        ds.put(
                "DanhMucDichVuSpaPanel",
                danhMuc(
                        "Dịch vụ Spa",
                        "Quản lý gói dịch vụ và bảng giá theo loài, cân nặng.",
                        "Thêm ít nhất một mức giá phù hợp trong bảng giá. Kiểm tra loài, khoảng cân"
                                + " nặng và giá trước khi lưu gói."));
        ds.put("DanhMucPhongHotelPanel", phong());
        ds.put(
                "DanhMucKhuyenMaiPanel",
                danhMuc(
                        "Khuyến mãi",
                        "Thiết lập chương trình giảm giá và thời gian áp dụng.",
                        "Kiểm tra loại giảm, giá trị, điều kiện và ngày bắt đầu/kết thúc. Chương"
                                + " trình chỉ áp dụng khi đơn đáp ứng điều kiện."));
        ds.put("DanhMucVoucherPanel", voucher());
        ds.put("DanhSachTaiKhoanPanel", taiKhoan());
        ds.put("QuyTacTichDiemPanel", quyTacDiem());
    }

    private static NoiDungHuongDan danhMuc(String ten, String mucDich, String rieng) {
        return tao(
                ten,
                mucDich,
                List.of(
                        "Nhập từ khóa hoặc chọn bộ lọc để tìm bản ghi cần xem.",
                        "Bấm Thêm để tạo hồ sơ; điền các trường có dấu * rồi bấm Lưu.",
                        "Chọn dòng và bấm Sửa, hoặc nhấp đúp dòng, để chỉnh thông tin; bấm Lưu khi"
                                + " hoàn tất.",
                        "Nếu cần ngừng sử dụng, chọn bản ghi và xem xác nhận của nút Xóa trước khi"
                                + " đồng ý."),
                List.of(
                        rieng,
                        "Các thao tác sửa hoặc ngừng sử dụng phụ thuộc quyền và ràng buộc của bản"
                                + " ghi. Đọc thông báo nếu nút bị khóa hoặc hệ thống từ chối."));
    }

    private static NoiDungHuongDan taiKhoan() {
        return tao(
                "Tài khoản",
                "Cấp tài khoản, gán vai trò và quản lý trạng thái đăng nhập.",
                List.of(
                        "Tìm theo tên đăng nhập hoặc nhân viên, rồi chọn dòng để xem tài khoản.",
                        "Bấm Thêm, nhập tên đăng nhập, chọn nhân viên, vai trò và mật khẩu mới rồi"
                                + " Lưu.",
                        "Chọn tài khoản và bấm Sửa để đổi thông tin được phép; dùng Khóa tài khoản"
                                + " hoặc Mở khóa tài khoản để đổi trạng thái.",
                        "Dùng Đặt lại mật khẩu khi cần cấp mật khẩu tạm; xem tab Quyền theo vai trò"
                                + " để đối chiếu quyền."),
                List.of(
                        "Mỗi nhân viên chỉ liên kết một tài khoản; tài khoản mới cần mật khẩu ít"
                                + " nhất 8 ký tự.",
                        "Không có thao tác xóa tài khoản. Không được khóa admin hoặc tự khóa tài"
                                + " khoản đang đăng nhập.",
                        "Giao mật khẩu tạm cho đúng người dùng và yêu cầu đổi lại mật khẩu."));
    }

    private static NoiDungHuongDan phong() {
        return tao(
                "Phòng lưu trú",
                "Quản lý phòng, sức chứa và giá lưu trú mỗi ngày.",
                List.of(
                        "Tìm và chọn phòng để xem thông tin.",
                        "Bấm Thêm, điền tên/loại phòng, số thú tối đa, giá mỗi ngày và lưu.",
                        "Chọn phòng Trống hoặc Bảo trì để Sửa thông tin khi cần."),
                List.of(
                        "Không có nút xóa phòng; chuyển trạng thái sang Bảo trì nếu cần ngừng sử"
                                + " dụng.",
                        "Không sửa phòng đang Có thú hoặc Đang dọn. Nhận/trả phòng ở Quản lý lưu"
                                + " trú, không sửa trạng thái để bỏ qua hợp đồng."));
    }

    private static NoiDungHuongDan voucher() {
        return tao(
                "Voucher",
                "Quản lý mã giảm giá và khách hàng được sử dụng.",
                List.of(
                        "Tìm theo mã hoặc chủ sở hữu; lọc phạm vi và trạng thái nếu cần.",
                        "Bấm Thêm, điền mã, mệnh giá, hạn dùng, đơn tối thiểu, khách hàng sở hữu và"
                                + " phạm vi áp dụng, rồi Lưu.",
                        "Chọn dòng để xem chi tiết; chỉ sửa các voucher còn được phép chỉnh."),
                List.of(
                        "Voucher đã có lượt sử dụng chỉ được tra cứu, không sửa hoặc phát hành"
                                + " lại.",
                        "Giới hạn lượt dùng được đặt là một lượt cho mỗi voucher, không nhập sửa"
                                + " trên biểu mẫu.",
                        "Voucher đổi điểm giữ nguyên mệnh giá, hạn dùng, chủ sở hữu và một lượt;"
                                + " không sửa qua danh mục.",
                        "Voucher phải còn hạn, còn lượt và đúng chủ sở hữu khi áp dụng ở Thanh"
                                + " toán."));
    }

    private static NoiDungHuongDan quyTacDiem() {
        return tao(
                "Quy tắc điểm và hạng",
                "Thiết lập mức tích điểm và ngưỡng hạng thành viên.",
                List.of(
                        "Xem mức tiền quy đổi thành điểm và các ngưỡng hạng đang áp dụng.",
                        "Nếu có quyền sửa, nhập quy tắc mới và bấm Lưu quy tắc.",
                        "Dùng Làm mới để nạp lại giá trị đã lưu khi cần đối chiếu."),
                List.of(
                        "Điểm khách hàng được cộng/trừ theo hóa đơn; không sửa số dư điểm thủ công"
                                + " ở trang này.",
                        "Mức đổi voucher Spa cố định: 200 điểm đổi 150.000 ₫, hạn 60 ngày; ô này"
                            + " chỉ đọc.",
                        "Ngưỡng hạng cần hợp lệ theo kiểm tra của biểu mẫu. Tài khoản không có"
                                + " quyền chỉ được xem."),
                new String[] {"Ctrl + S", "Lưu quy tắc khi có quyền sửa"});
    }
}
