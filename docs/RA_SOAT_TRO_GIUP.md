# Rà soát trợ giúp từng màn hình

Ngày 26/11/2026. Đã đối chiếu chức năng, tên nút và các phím được đăng ký trong mã nguồn; giữ thông tin đăng nhập demo điền sẵn.

## Phím tắt hiện tại

| Phím | Chức năng |
| --- | --- |
| F1 | Lập hóa đơn |
| F2 | Đổi hàng / mở thao tác đổi phụ kiện khi đang ở trang đổi hàng |
| F3 | Quản lý Spa |
| F4 | Quản lý lưu trú |
| F6 | Tra cứu hóa đơn |
| Alt+F1 | Trợ giúp của màn hình, tab hoặc biểu mẫu đang mở |

Menu và phím điều hướng dùng chung cấu hình để giữ đúng nhãn và hành vi. Đã kiểm tra gọi hành động của từng phím; nhật ký đầy đủ mới nhất: `target/ux-audit/shortcuts-verify.log`.

## Kết quả

- Có 49 bộ nội dung: 43 lớp màn hình/tab, 5 biểu mẫu chính và 1 lớp báo cáo dùng chung. Lịch làm việc còn đổi nội dung theo ba chế độ: phân lịch, tra cứu toàn cửa hàng, lịch cá nhân.
- Đã khởi tạo đủ 47 mã màn hình từ `target/petstation.jar` với CSDL kiểm thử, đọc trợ giúp của từng màn hình và kiểm tra tab tài khoản/voucher thực tế. Đây là kiểm tra khởi tạo và lấy nội dung, không phải bấm toàn bộ luồng nghiệp vụ. Nhật ký: `target/ux-audit/help-review/jar-help-check.log`; bảng kết quả: `target/ux-audit/help-review/routes.tsv`.
- Các cửa sổ dùng cùng bố cục: mục đích, các bước thao tác chính, lưu ý, phím tắt; đổi mật khẩu dùng chung hộp trợ giúp thay cho thông báo riêng. Văn bản xuống dòng theo chiều rộng, phím tắt trình bày một cột.
- Trợ giúp theo tab đang chọn ở Spa, lưu trú, tài khoản/phân quyền và voucher/quy tắc điểm. Không suy đoán chức năng từ chuỗi tên phân hệ.
- Bổ sung nút Trợ giúp (Alt+F1) cho đặt/sửa lịch Spa, đặt/sửa/gia hạn phòng, phiếu chăm sóc, đổi phụ kiện và thu cọc lưu trú. Alt+F1 mở hướng dẫn của biểu mẫu; Esc đóng cửa sổ trợ giúp.
- Lượt `verify` cuối đạt **135 kiểm thử, 0 thất bại, 0 lỗi, 0 bỏ qua**, gồm 98 bài thường và 37 bài SQL. Biên dịch lại 303 tệp ứng dụng và 30 tệp kiểm thử. Nhật ký: `target/ux-audit/help-review-verify.log`.
- Đã render toàn bộ 49 bộ nội dung ở chiều rộng phần nội dung 540, 624 và 850 px với FlatLaf, kiểm tra dòng cuối của văn bản và chiều rộng nhãn. Có lượt kiểm thử bổ sung ở mức phóng giao diện mô phỏng 125% (`target/ux-audit/help-review-test-125.log`); chưa thay thế kiểm tra DPI Windows vật lý.
- Kiểm thử tự dò các lớp màn hình cụ thể trong mã nguồn để phát hiện màn hình mới chưa có hướng dẫn riêng. Bổ sung kiểm tra ba chế độ lịch, chuyển trợ giúp theo tab và liên kết Alt+F1 của biểu mẫu.

## Những nội dung lệch đã chỉnh

| Nhóm | Điều chỉnh |
| --- | --- |
| Cài đặt | Tên hai tab hiện tại; nút đặt lại cấu hình ở cuối trang; cần lưu sau khi đặt lại. |
| Lập hóa đơn | Dùng đúng đơn nháp và Sang thanh toán; bỏ mô tả quét qua điện thoại và cam kết hiệu năng chưa được chứng nhận. |
| Thanh toán / ca thu ngân | Tách riêng hai quy trình; voucher ở thanh toán; QR cần đối chiếu tiền vào; ca cần tiền thực đếm và người nhận bàn giao. |
| Danh mục | Hướng dẫn và lưu ý riêng theo đối tượng; bỏ khẳng định mọi trường đều kiểm tra chung một định dạng và mọi thao tác xóa đều xóa mềm. |
| Tài khoản / phòng | Không hướng dẫn bấm nút xóa không tồn tại; nêu đúng khóa tài khoản, bảo trì phòng và giới hạn sửa theo trạng thái. |
| Voucher / điểm | Một lượt dùng chỉ đọc, voucher đã dùng và voucher đổi điểm không sửa tự do; quy đổi Spa cố định và không sửa số dư điểm thủ công. |
| Kho | Tách nhập, xuất hủy, tồn theo lô và kiểm kê; kiểm kê lập biên bản trước rồi người có quyền duyệt điều chỉnh tồn, không tự suy ra ngưỡng phê duyệt. |
| Spa / lưu trú | Tách điều phối, lập phiếu, lịch hẹn, sơ đồ phòng và hợp đồng; phân biệt yêu cầu cọc với tiền đã thu, hoàn tất dịch vụ với thanh toán. |
| Lịch | Phân biệt xem toàn cửa hàng, lịch cá nhân và phân lịch; nghỉ phép/đổi ca là yêu cầu cần xử lý. |
| Báo cáo | Dữ liệu tự tải khi đổi kỳ/kiểu xem; bỏ nút Xem thống kê không tồn tại và việc bấm thẻ KPI để mở chi tiết; giải thích công nợ theo ngày nhập với dư nợ hiện tại. |
| Hệ thống / tra cứu | Nội dung riêng cho tổng quan, nhật ký, phê duyệt, lịch sử khách, hóa đơn và đơn nháp. |

## Danh sách đã đối chiếu
| Màn hình / biểu mẫu | Nội dung đã đối chiếu |
| --- | --- |
| Thống kê | Đối chiếu số liệu theo kiểu báo cáo đang xem. |
| Ca thu ngân | Mở ca, đối soát tiền mặt và kết ca thu ngân. |
| Cài đặt hệ thống | Điều chỉnh thông tin cửa hàng và các tham số vận hành. |
| Chi tiết khách hàng | Xem hồ sơ và lịch sử của khách hàng đã chọn. |
| Dịch vụ Spa | Quản lý gói dịch vụ và bảng giá theo loài, cân nặng. |
| Khách hàng | Lưu hồ sơ liên hệ và tra cứu khách hàng của cửa hàng. |
| Khuyến mãi | Thiết lập chương trình giảm giá và thời gian áp dụng. |
| Nhà cung cấp | Quản lý thông tin liên hệ của nhà cung cấp hàng hóa. |
| Nhân viên | Quản lý hồ sơ và trạng thái làm việc của nhân viên. |
| Phòng lưu trú | Quản lý phòng, sức chứa và giá lưu trú mỗi ngày. |
| Hồ sơ thú cưng | Quản lý thú cưng gắn với từng chủ nuôi. |
| Voucher | Quản lý mã giảm giá và khách hàng được sử dụng. |
| Sản phẩm | Quản lý thông tin bán hàng và phân loại sản phẩm. |
| Tài khoản | Cấp tài khoản, gán vai trò và quản lý trạng thái đăng nhập. |
| Đặt và sửa lịch hẹn Spa | Hẹn thời gian đưa thú cưng đến làm dịch vụ. |
| Đặt, sửa và gia hạn phòng | Ghi nhận thời gian và thông tin lưu trú của thú cưng. |
| Đổi mật khẩu | Đổi mật khẩu của tài khoản đang đăng nhập. |
| Lập phiếu đổi phụ kiện | Chọn phụ kiện trả lại và hàng thay thế từ hóa đơn gốc. |
| Đổi phụ kiện | Theo dõi phiếu đổi phụ kiện và lập giao dịch đổi mới. |
| Đơn nháp | Mở lại hoặc xóa các giỏ hàng đang chờ xử lý. |
| Xem hóa đơn | Xem và in hóa đơn sau khi thanh toán. |
| Kiểm kê tồn kho | Đếm thực tế theo lô và lập biên bản trước khi điều chỉnh tồn. |
| Lập hóa đơn | Tạo giỏ hàng và chuyển thu ngân thu tiền. |
| Lập phiếu Spa | Tiếp nhận thú cưng và xác định dịch vụ theo loài, cân nặng. |
| Lịch hẹn Spa | Đặt lịch trước và chuyển lịch hẹn sang tiếp nhận khi khách đến. |
| Lịch sử khách hàng | Đối chiếu lịch sử giao dịch và hồ sơ của một khách hàng. |
| Tra cứu lịch làm việc | Xem lịch theo tuần và thực hiện các thao tác của chế độ đang mở. |
| Hợp đồng lưu trú | Theo dõi đặt phòng, nhận phòng, cọc và quyết toán lưu trú. |
| Quyền theo vai trò | Tra cứu các quyền được cấp cho từng vai trò. |
| Nhập kho | Ghi nhận hàng nhập theo nhà cung cấp và từng lô hàng. |
| Phiếu chăm sóc | Ghi và tra cứu nội dung chăm sóc, sức khỏe cùng hình ảnh thú cưng. |
| Nhật ký hệ thống | Tra cứu sự kiện để đối soát hoạt động của cửa hàng. |
| Quy tắc điểm và hạng | Thiết lập mức tích điểm và ngưỡng hạng thành viên. |
| Thanh toán | Kiểm tra số phải thu và xác nhận giao dịch của đơn hiện tại. |
| Công nợ nhà cung cấp | Đối chiếu tổng nhập, số đã trả và dư nợ nhà cung cấp. |
| Thống kê doanh thu | Theo dõi doanh thu và các góc nhìn kinh doanh được cấp quyền. |
| Thống kê hàng hóa | Xem bán hàng theo sản phẩm và các báo cáo tồn kho. |
| Thống kê lưu trú | Theo dõi số liệu hợp đồng, phòng và hoạt động lưu trú. |
| Thống kê khách hàng | Xem giao dịch, thành viên và việc sử dụng quyền lợi khách hàng. |
| Thống kê nhân viên | Xem kết quả theo nhân viên và kiểu báo cáo được chọn. |
| Thống kê Spa | Đối chiếu doanh thu và hoạt động dịch vụ Spa. |
| Thu tiền cọc lưu trú | Xác nhận khoản cọc thực nhận cho hợp đồng. |
| Điều phối phiếu Spa | Theo dõi và xử lý phiếu dịch vụ theo tiến độ. |
| Tra cứu hóa đơn | Tìm hóa đơn đã lưu, xem chi tiết và xử lý theo quyền. |
| Tồn kho theo lô | Tra cứu số lượng và hạn dùng của từng lô hàng. |
| Tổng quan | Xem nhanh tình hình cửa hàng và mở công việc hằng ngày. |
| Sơ đồ phòng lưu trú | Xem phòng và mở thao tác theo trạng thái đang có. |
| Phê duyệt yêu cầu | Xem xét yêu cầu thay đổi trước khi quyết định xử lý. |
| Xuất hủy kho | Lập phiếu ghi nhận hàng phải loại khỏi kho. |
## Ảnh và mã nguồn

[Trợ giúp cài đặt](images/nghiem-thu/tro-giup-cai-dat.png), [trợ giúp thanh toán](images/nghiem-thu/tro-giup-thanh-toan.png). Ảnh lưu phần đầu cửa sổ; nội dung còn lại được xem bằng thanh cuộn, không cần thu nhỏ chữ.

Nguồn nội dung: `src/main/java/com/petstation/gui/chung/trogiup/`. Hộp hiển thị chung: `HopThoaiTroGiup`; điểm lấy hướng dẫn theo ngữ cảnh: `CoSoPanel.layHuongDanTroGiup()`.