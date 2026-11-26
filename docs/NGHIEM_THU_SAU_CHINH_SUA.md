# Nghiệm thu sau chỉnh sửa Pet Station

Ngày 26/11/2026. Đối chiếu với hai báo cáo đánh giá tại bản `f97c021`. Giữ thông tin đăng nhập điền sẵn theo yêu cầu demo. Các nhận xét trong hai báo cáo gốc là ghi nhận trước khi sửa.

## Kết quả kiểm thử

- Phím điều hướng đã đổi thành F1 lập hóa đơn, F2 đổi hàng, F3 Spa, F4 lưu trú, F6 tra cứu hóa đơn; trợ giúp dùng Alt+F1 trên màn hình và năm biểu mẫu. Đã chạy kiểm thử hành động phím khớp cấu hình menu; lượt verify cuối: `target/ux-audit/shortcuts-verify.log`.

- Đã rà trợ giúp theo từng màn hình, bổ sung 49 bộ nội dung và hướng dẫn theo tab/ngữ cảnh; khởi tạo 47 mã màn hình từ JAR và kiểm tra liên kết trợ giúp. Xem [bảng rà soát trợ giúp](RA_SOAT_TRO_GIUP.md). Nhật ký verify mới nhất: `target/ux-audit/help-review-verify.log`.

- Đã biên dịch lại 296 tệp ứng dụng và 28 tệp kiểm thử sau khi xóa riêng các lớp sinh tự động và trạng thái biên dịch. Đã bổ sung lại import `JTextArea` dùng cho in thử, bỏ biến chọn dòng không dùng ở kiểm kê và trường DAO không dùng trong kiểm thử SQL. Lượt `verify` này đạt 128 bài; nhật ký: `target/ux-audit/compile-fix-verify.log`.

- Cài đặt đã gộp còn hai tab: **Cửa hàng và thanh toán**, **Vận hành**. Các nhóm trường chia hai cột cân đáy; thẻ cuối của cột ngắn hơn nhận phần chiều cao dư, các ô nhập và nút giữ chiều cao theo nội dung. Nút **Đặt lại cấu hình** nằm cố định ở cuối trang, ngay trước **Lưu cài đặt**; xác nhận nêu rõ cần lưu để áp dụng. Kiểm thử hai tab ở 900, 1100, 1366 và 1665 px, kiểm tra nút cùng hàng, đáy thẻ cuối bằng đáy cột, hai cột cao bằng nhau, nội dung vừa chiều rộng khung cuộn và quyền chỉ xem. Kiểm tra nhãn bằng FlatLaf, bảo đảm chiều rộng đủ toàn bộ chữ với khoảng dư để tránh dấu ba chấm; đã chạy thêm ở tỉ lệ giao diện mô phỏng 125%. [Ảnh cửa hàng](images/nghiem-thu/cai-dat-cua-hang.png), [ảnh vận hành](images/nghiem-thu/cai-dat-van-hanh.png). Nhật ký: `target/ux-audit/settings-labels-verify.log`.

- Lần chạy `verify` đầy đủ cuối đạt **135 kiểm thử, 0 thất bại, 0 lỗi, 0 bỏ qua**, gồm 98 bài thường và 37 bài SQL. Bật cả ba bộ tích hợp SQL trên `PetStationIntegrationTest_Review`. Fixture của các bài SQL được hoàn tác sau kiểm thử.
- Các bài mới kiểm tra quét lần đầu, lưu đủ các lô khi lọc, so sánh nhân viên, đổi nhãn và đảo cột báo cáo, tổng thu ca gồm cọc, tồn thay đổi trong lúc đếm, xếp hạng 11 sản phẩm, hóa đơn có cả khuyến mãi và voucher, cùng thông báo khi bảng lọc trống.
- Luồng bán hàng, phân bổ nhiều lô, hoàn chứng từ, chống ghi lặp, phân quyền, spa và cọc/quyết toán lưu trú được kiểm tra qua các entry point service trong bộ tích hợp. Kết quả này chưa tương đương việc bấm toàn bộ các bước trên cửa sổ thật.
- Kiểm thử PDF hiện có được chạy cùng bộ đầy đủ. Máy hiện cung cấp OneNote, Microsoft Print to PDF và Fax, chưa xác nhận bản in trên máy in quầy.
- Bổ sung kiểm thử nút Kết ca tại giao diện. Tiền thực đếm trống, chênh lệch thiếu ghi chú, bàn giao thiếu người nhận đều bị chặn và số lần gọi DAO ghi bằng 0. Ảnh đối soát dùng dữ liệu giả lập đã được xem ở hai kích thước.
- Đã tạo `target/petstation.jar` và kiểm tra từ thư mục khác. JAR tải được lớp ứng dụng, logo, FlatLaf, JDBC và kết nối SQL. Đã xác thực thông tin điền sẵn và ba tài khoản demo nhân viên qua service trong JAR, trả đúng bốn vai trò. Ba tài khoản nhân viên không được cấp quyền quản lý tài khoản. Nhật ký và trạng thái đăng nhập của lượt thử được hoàn tác. Đây là kiểm tra qua service, chưa thay thế việc bấm hết luồng demo từ JAR bằng tay.
- Đã xuất rồi mở lại Excel bằng Apache POI. Kết quả có 27 dòng báo cáo, đủ bảy tiêu đề, số tiền giữ kiểu số và tổng doanh thu thuần 9.090.000 ₫ khớp dữ liệu truy vấn. Chưa mở tệp này bằng giao diện Excel/WPS.
- Đã tạo PDF bằng hàm xuất thật, chạy kiểm tra cấu trúc và giải nén ảnh biên lai nhúng để xem. Phát hiện rồi sửa cột STT sát tên hàng và tiêu đề số lượng quá nhỏ trên khổ 80 mm. Đã xem lại ảnh tiếng Việt, bảng, tổng tiền và phần cuối biên lai. Máy không có Poppler trong các vị trí đã kiểm tra nên bước xem sử dụng ảnh RGB nhúng trong PDF của ứng dụng.

## Tệp và bằng chứng mang đi báo cáo

- Bản chạy đã đóng gói ở `target/petstation.jar`. Cần mang cấu hình kết nối và chuẩn bị SQL theo `SETUP.md` trên máy trình bày.
- SHA-256 của bản JAR đóng gói sau khi đổi phím tắt là `D0943999B7224A00204337FCC2F366A22670EFA27B6C08DE6DCBC4F3B88B4C35`. Giá trị này xác định đúng tệp trong lượt kiểm tra, có thể đổi khi build lại.
- Nhật ký build đầy đủ nằm ở `target/ux-audit/report-ready-verify.log`. Kiểm tra đóng gói ở `target/ux-audit/report-ready/jar-smoke.log`.
- Ảnh dưới đây được giữ trong thư mục tài liệu của repository, ngoài `target`.

| Mục trình bày | Ảnh |
| --- | --- |
| Đối soát tiền mặt, phân biệt tiền đầu ca, thu hóa đơn, cọc và tiền thực đếm | [Ca thu ngân](images/nghiem-thu/ca-thu-ngan.png) |
| Phiếu nhập có đã trả, còn nợ và trạng thái | [Phiếu nhập kho](images/nghiem-thu/phieu-nhap.png) |
| Số đơn, doanh thu thuần và lợi nhuận | [Báo cáo doanh thu](images/nghiem-thu/doanh-thu.png) |
| Top 10 theo doanh thu, phạm vi tổng đang hiển thị | [Top sản phẩm](images/nghiem-thu/top-san-pham.png) |
| Bố cục biên lai khổ 80 mm sau sửa | [Ảnh lấy từ PDF](images/nghiem-thu/bien-lai.png) |

Biên lai PDF và báo cáo Excel đã được xuất và kiểm tra trong lượt demo. Thư mục xuất mẫu `output` đã được bỏ theo yêu cầu; xuất lại từ ứng dụng nếu cần tệp minh họa. Nhật ký kiểm tra tệp nằm ở `target/ux-audit/report-ready/files-smoke.log`, kiểm tra đăng nhập ở `target/ux-audit/report-ready/login-smoke.log`.

## Kịch bản trình bày 7–10 phút

1. Giới thiệu Java Swing, service, DAO và SQL Server. Nêu giao dịch, ràng buộc dữ liệu và phân quyền được xử lý ở service/DAO.
2. Mở bản demo với tài khoản đã điền sẵn. Chỉ ra vai trò, ca hiện tại và chức năng được phép. Thông tin điền sẵn là lựa chọn phục vụ demo đã được chấp thuận.
3. Trình bày ca thu ngân. Giải thích dự kiến trong két gồm tiền đầu ca, thu tiền mặt, cọc tiền mặt và khoản chi/hoàn tương ứng. Chỉ ra tiền chuyển khoản/thẻ được theo dõi riêng. Cho thấy bắt buộc nhập tiền thực đếm và lý do khi lệch.
4. Đi qua lập đơn, đơn nháp, thanh toán, xem hóa đơn. Giải thích nút Sang thanh toán mở bước thu tiền và nút Xác nhận thanh toán ghi giao dịch. Dùng database demo riêng cho thao tác có ghi dữ liệu.
5. Trình bày hai lô nhập cùng sản phẩm. Tại kiểm kê, giải thích chọn phạm vi, chưa đếm khác 0, lọc giữ đủ lô và tồn thay đổi sẽ bị chặn trước khi ghi.
6. Mở báo cáo có dữ liệu. Phân biệt doanh thu hoàn tất, tiền thu gồm cọc, top 10 và giảm trên hóa đơn dùng voucher. Cho thấy bảng công nợ được ưu tiên để đối soát.
7. Trình bày kết quả 135 kiểm thử và phạm vi chưa chứng nhận ở cuối hồ sơ. Khi được hỏi về máy in, máy quét hoặc DPI, dùng đúng bằng chứng đã có.

## Các thay đổi đã đối chiếu

| Nhóm | Thay đổi và bằng chứng |
| --- | --- |
| Kiểm kê | Chọn phạm vi trước khi đếm. Giữ toàn bộ lô khi lọc. Khởi tạo chưa đếm, lần quét đầu ghi 1. Đếm mù ẩn số sổ và tổng lệch, có bước đối chiếu. Hiển thị tiến độ theo lô và trạng thái cụ thể sau khi ghi. Kiểm tra lại tồn có khóa trong transaction trước khi lập biên bản. |
| Nhập kho | Chọn chủ động nhà cung cấp và sản phẩm. Bỏ lô và hạn dùng tự suy đoán. Giữ dữ liệu form khi nhập sai. Xác nhận tổng nhập, đã trả và còn nợ. Hiển thị số lô nhà sản xuất và cột còn nợ trên danh sách phiếu. |
| Danh mục | Ưu tiên chiều rộng cột tên, cho cuộn ngang khi cần. Chia lại vùng danh sách và chi tiết. Thanh nút tự xuống dòng khi khung hẹp. Thao tác ngừng hoạt động có nhãn và nhật ký đúng hậu quả. |
| Tài khoản và nhân viên | Ma trận lấy nguồn quyền mặc định xác định. Quyền cá nhân tiếp tục được dùng khi đăng nhập. Bỏ thao tác khóa trùng trên danh sách tài khoản. Nhãn khóa/mở khóa theo trạng thái. Nhãn cấp tài khoản/đặt lại mật khẩu nhân viên được tải nền. |
| Đăng nhập và điều hướng | Giữ tài khoản demo. Sửa tiêu đề đăng nhập và cách đặt lại mật khẩu. Chỉ đổi lịch sử và màn hình hiện tại sau khi tạo trang thành công. Nút cửa sổ có tên truy cập. |
| Cài đặt | Gộp còn hai tab, mỗi tab chia hai cột với các nhóm rõ ràng. Đặt lại cấu hình và Lưu cài đặt nằm cạnh nhau ở cuối trang. Nêu rõ cần lưu sau khi đặt lại thông số mặc định. |
| Bán hàng và đơn nháp | Thống nhất đơn nháp, sang thanh toán, lựa chọn xử lý giỏ hiện tại và câu chữ đổi phụ kiện. Gợi ý tìm kiếm theo loại chứng từ. Biên lai dùng tên đầy đủ cho khách hàng, thu ngân, đơn giá và thành tiền. |
| Spa và lưu trú | Yêu cầu chọn khách chủ động khi đặt lịch/phòng. Sửa nhãn thêm khách/thú/dịch vụ, thu cọc, phương thức và ảnh trước/sau. Thông tin sức khỏe mặc định chưa được coi là xác nhận, có bước xác nhận trước khi tiếp nhận bình thường. |
| Lịch và phê duyệt | Dùng tuần này, thêm người vào ca, mã yêu cầu và gửi lúc ở đúng trang. Giữ chữ cảnh báo thiếu người. Sửa nhãn thông tin hiện tại/nội dung đề nghị và nút gửi lý do từ chối/bổ sung. |
| Báo cáo | Cấu hình tổng hợp độc lập với nhãn. Tổng thu ca được tạo trong DTO. Nhãn top 10, giảm trên hóa đơn dùng voucher, đơn giá lưu trú và tiền mặt thực đếm nói rõ phạm vi. Dùng định dạng số Việt Nam, tên biểu đồ theo chế độ. Công nợ ưu tiên bảng. |
| Xuất Excel | Tiêu đề lấy theo cột đang hiển thị. Tên tệp báo cáo gồm chế độ và kỳ. Giữ cảnh báo ghi đè và bộ lọc phần mở rộng. |
| Trạng thái trống và bàn phím | Phân biệt chưa có dữ liệu, lọc không có kết quả, đang tải và lỗi tải. Sửa vẽ lại thông báo trống. Nhãn form liên kết ô nhập. Kiểm kê các phím đã gắn và thử bộ lọc, Esc, Enter trên cửa sổ QA. |

## Bằng chứng giao diện

- Đã dựng 47 mã điều hướng ở 1.100 × 680 và 1.366 × 768, gồm trạng thái chọn dòng và tab dùng chung. Cả 29 chế độ báo cáo tải được dữ liệu trong lượt rà.
- Đã xem ảnh tổng hợp 113 trạng thái ở kích thước nhỏ, đồng thời xem riêng báo cáo doanh thu, top sản phẩm, tài khoản và danh mục sản phẩm để đọc nội dung chi tiết. Các ảnh tổng hợp giúp rà cấu trúc và độ tràn, chưa thay thế kiểm tra từng ô bằng bàn phím.
- Đã dựng lại các hộp thoại nhập, xuất, chọn phạm vi kiểm kê, chăm sóc và thanh toán. Các hộp được đóng trước bước ghi dữ liệu.
- Đã thử mô phỏng cỡ giao diện FlatLaf 125% và 150% trên danh mục sản phẩm. Việc mô phỏng này có phạm vi khác với thay đổi DPI Windows trên toàn bộ màn hình và hộp thoại.
- Bằng chứng tạm nằm trong `target/ux-audit/verified/`, `target/ux-audit/scale-1.25/`, `target/ux-audit/scale-1.5/`. Nhật ký đầy đủ nằm trong `target/ux-audit/all-final.log`, `verified-run.log`, `final-dialogs.log`. Các tệp trong `target` có thể bị xóa khi chạy clean.

## Phạm vi chưa thể chứng nhận

- Chưa có bằng chứng in giấy trên máy in quầy hoặc quét từ máy quét mã vạch thật. Danh sách HID chỉ có tên thiết bị chung nên chưa xác định được máy quét.
- Chưa thử toàn bộ từng ô bằng bàn phím vật lý, mọi tổ hợp vai trò/trạng thái chứng từ và từng cửa sổ ở DPI Windows thực tế. Đã kiểm tra mã, phím được gắn, ảnh dựng, các trường hợp hồi quy và một lượt thao tác trực tiếp.
- Chưa bấm trọn các luồng có ghi dữ liệu trên cửa sổ thật từ JAR. Các giao dịch bán hàng, spa, lưu trú và kho hiện có bằng chứng kiểm thử service/SQL. Chưa mở Excel/WPS để kiểm tra tệp xuất hoặc chạy CI trên máy/hệ điều hành khác trong phiên này.
- Chưa có số đo thử tải trên dữ liệu lớn hoặc kiểm thử xâm nhập. Bộ kiểm thử hiện tại đánh giá nghiệp vụ, hồi quy và các ràng buộc được nêu trong hồ sơ.
- Những giới hạn này cần được giữ trong kết luận nghiệm thu. Chưa đủ cơ sở ghi nhận “đã nghiệm thu toàn bộ trên thiết bị thật”.

## Nội dung có thể trình bày với giảng viên

“Nhóm đã kiểm tra 47 mã màn hình ở hai kích thước và 29 chế độ báo cáo có dữ liệu. Lần chạy verify mới nhất đạt 135 bài, gồm 37 bài tích hợp SQL. Bộ kiểm thử kiểm tra các trường hợp rollback, chống ghi lặp, phân quyền, kiểm kê theo lô, thanh toán và cọc lưu trú. Bản JAR đã được đóng gói, kiểm tra khởi tạo và đăng nhập bốn vai trò. Tệp Excel đã được đọc lại để đối chiếu, biên lai PDF đã được kiểm tra cấu trúc và ảnh nhúng. Phần kiểm tra máy in/máy quét thật, thao tác bàn phím toàn bộ form và DPI Windows thực tế còn nằm ngoài phạm vi đã chứng nhận.”
