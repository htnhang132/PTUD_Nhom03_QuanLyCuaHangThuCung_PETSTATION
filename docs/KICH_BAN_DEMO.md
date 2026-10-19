# Kịch bản demo Pet Station

Cập nhật cho buổi demo 20/10/2026. Điểm chạy: `com.petstation.App`; JDK 17, Maven, SQL Server trên máy hiện tại. Bản đóng gói: `target/petstation.jar`.

Hướng dẫn cài/nâng cấp: [SETUP.md](../SETUP.md). Cấu trúc và nguyên tắc bảo trì: [ARCHITECTURE.md](ARCHITECTURE.md).

## Chuẩn bị trên máy này

- Database đang dùng: `PetStationDemo2`. Đã sao lưu COPY_ONLY/CHECKSUM, chạy RESTORE VERIFYONLY trước nâng cấp; đường dẫn bản sao lưu nằm trong `target/ui-finish/database-backup.txt`.
- Đã chạy nâng cấp `UI_FINISH_V1` và `UI_DATA_CLEANUP_V1`. Bản ghi, tiền/tồn/điểm/cọc/công nợ và tài khoản được đối chiếu trên 42 bảng trước/sau; chạy lại toàn bộ seed không đổi nội dung hoặc số dư. Không reset database. [Đối chiếu mã](DOI_CHIEU_MA_DU_LIEU.md).
- Bốn tài khoản: `demo.admin`, `demo.sales`, `demo.stock`, `demo.spa`. Mật khẩu riêng được tạo cục bộ trong `target/demo-access.properties`; sao chép nguyên phần sau dấu `=` của đúng tài khoản. Tệp này dùng mật khẩu dạng văn bản để sao chép trực tiếp, không thêm dấu `\` trước `!`; không đưa tệp này lên Git. Tài khoản quản lý có sẵn được giữ nguyên.
- **Cài đặt → Ngân hàng** lưu BIN, số tài khoản và tên chủ tài khoản thực tế. Tên shop hiển thị riêng; ứng dụng ngân hàng xác định tên người nhận theo tài khoản. Không ghi thông tin ngân hàng cá nhân vào SQL mẫu hay mã nguồn.
- Khởi động: `java -jar target/petstation.jar`, F5 hoặc `scripts/run-java.ps1`. Mở ca trước bán hàng, nhận cọc và thanh toán.
- Tệp kiểm tra đầu ra: `target/demo-output/ton-kho-dang-loc.xlsx`, `target/demo-output/qr-kiem-tra-50000.png`. Mở `.xlsx` bằng WPS hoặc Excel; quét QR chỉ kiểm tra thông tin, không cần chuyển tiền.

## Dữ liệu tình huống

| Mã | Mục đích |
| --- | --- |
| `KH000007` | Không đủ điểm đổi voucher |
| `KH000008` | Đủ 200 điểm đổi voucher Spa 150.000đ, hạn 60 ngày |
| `KH000009` | Bán và đổi phụ kiện; số dư điểm ban đầu 0 |
| `TC000006` | Thuộc khách 200 điểm, đủ tuổi và hai mũi tiêm |
| `TC000007` | Thiếu lịch sử tiêm phòng để minh họa chặn tiếp nhận |
| `SP000013`, `SP000014` | Phụ kiện 100.000đ và 150.000đ; tồn đầu tình huống 20 mỗi loại |
| `DV000004` | Spa cho chó dưới 5kg, giá 200.000đ |
| `P000007` | Hotel 200.000đ/ngày, cọc một ngày 100.000đ |

Chạy lại seed chỉ thêm bản ghi thiếu, **không đưa điểm hoặc tồn về ban đầu**. Sau một lần demo, đối chiếu theo số dư thực tế; không reset để diễn lại.

## Trình diễn xuyên suốt

1. **Quản lý:** xem năm nhóm menu, cài đặt, ma trận quyền và nhật ký. Ghi lại tồn hai sản phẩm, điểm ba khách và doanh thu đầu buổi.
2. **Bán hàng:** đăng nhập `demo.sales`, mở ca với tiền đầu ca đã đếm. Chọn `KH000009`, bán hai `SP000013`, treo đơn; đóng/mở ứng dụng, mở lại đơn và thanh toán. Tra cứu hóa đơn bằng F3, xem/in lại.
3. **Đổi hàng:** Xử lý → Đổi hàng; chọn đúng dòng trên hóa đơn vừa bán, đổi một `SP000013` lấy một `SP000014`. Kiểm tra giá trị được đổi và số cần thu, thanh toán rồi tra lại phiếu. Phiếu và biên lai ghi rõ hóa đơn gốc, giá trị được đổi, chênh lệch.
4. **Spa:** đăng nhập `demo.spa`. Thử tiếp nhận `TC000007` và xem thông báo yêu cầu bổ sung; mở Hồ sơ thú cưng → Lịch sử tiêm phòng. Đặt lịch `TC000006` với `DV000004`, tiếp nhận cân nặng 4kg, qua các bước bắt đầu/hoàn thành/đóng phiếu. ID phiếu được giữ khi chuyển màn.
5. **Voucher và QR:** tại thanh toán Spa, đổi 200 điểm, chọn voucher của khách. Kiểm tra số tiền QR sau khi áp dụng hoặc bỏ voucher. Chọn sai chủ/hết hạn/đã dùng phải bị chặn. Khi trình diễn QR không chuyển tiền thật, không đánh dấu đã nhận tiền để tạo giao dịch giả. Muốn hoàn tất hóa đơn demo, dùng phương thức tiền mặt và ghi rõ đây là giao dịch tình huống.
6. **Hotel:** chọn `TC000006`, phòng `P000007`, thời gian một ngày. Mở ca, xác nhận đã nhận cọc tiền mặt 100.000đ; kiểm tra cọc trong ca. Nhận phòng, ghi chăm sóc, quyết toán và thanh toán phần còn lại. Phòng chuyển Đang dọn; tại Đặt phòng bấm Dọn phòng xong để về Trống. Xem lại sơ đồ phòng.
7. **Kho và quản lý:** `demo.stock` nhập một lô có hạn dùng; đối chiếu công nợ nhà cung cấp. Gửi đề xuất giá `SP000014`, kiểm tra giá chưa đổi. `demo.admin` từ chối một đề xuất, duyệt một đề xuất khác; đối chiếu lịch sử giá và nhật ký, không thể duyệt lặp.
8. **Nghiệp vụ còn lại:** xuất hủy, kiểm kê có chênh lệch → phê duyệt; phân lịch, gửi nghỉ phép → quản lý duyệt. Cho nhân viên nghỉ việc rồi thử đăng nhập tài khoản đã khóa. Dùng bản ghi tình huống riêng, không sửa nhân viên đang sử dụng.
9. **Báo cáo và kết ca:** mở từng báo cáo, chọn kỳ, xuất Excel. Đếm tiền thực tế và kết ca; giải trình khi lệch. Đóng/mở lại ứng dụng và tra cứu lại các hóa đơn/phiếu đã lưu.

## Bảng đối chiếu trước/sau

| Thao tác | Kết quả cần đối chiếu |
| --- | --- |
| Bán 2 A | Tồn A giảm 2; hóa đơn bằng tiền thực trả; điểm cộng `floor(tổng thực trả / 10.000)` |
| Đổi 1 A lấy 1 B | Tồn A tăng 1, B giảm 1; thu `giá B − phần thực trả của A`; không chi tiền hoàn |
| Điểm khi đổi | Chỉ cộng phần điểm tăng thêm theo tổng chi tiêu gốc và các chênh lệch; không tích lại giá trị hàng đã được đổi |
| Đổi voucher | Điểm giảm đúng 200, có một dòng lịch sử và một voucher gắn khách; gửi lại cùng khóa không trừ lần hai |
| Spa | Tổng phiếu = tổng chi tiết; tiền thanh toán = phiếu − giảm giá hợp lệ − voucher; phiếu chuyển PAID đúng một lần |
| Hotel | Cọc = 50% dự kiến; còn thu = tiền phòng + phí trễ hiện có − cọc; ghi nhận cọc riêng theo ca nhận |
| Tiền mặt ca | Tiền đầu ca + hóa đơn tiền mặt + cọc Hotel tiền mặt − hoàn tiền lịch sử thuộc ca |
| Doanh thu tổng hợp | Hóa đơn đã thanh toán + cọc Hotel mới theo ngày nhận + số âm hoàn tiền lịch sử; không cộng lại chênh lệch đổi hàng vì đã có hóa đơn mới |
| Hàng bán chạy | Trừ số lượng/giá trị hàng đã đổi ở hóa đơn gốc, ghi giá trị đầy đủ của hàng thay thế |
| Spa/Hotel chi tiết | Spa dùng số thực trả sau giảm giá; Hotel hoàn tất gồm tiền cọc đã cấn trừ và khoản quyết toán |
| Duyệt giá | Giá mới, một dòng lịch sử và nhật ký cùng transaction; từ chối không đổi giá |

Ví dụ đổi hàng **không có khuyến mãi**: bán 2 A = 200.000đ, đổi 1 A lấy 1 B = thu thêm 50.000đ; khách có 25 điểm, tổng thu 250.000đ; tồn từ A/B 20/20 thành 19/19. Nếu có khuyến mãi, dùng số thực trả và giá trị được đổi hiển thị trên màn hình để tính lại.

## 37 mục menu

| Nhóm | Các mục cần mở |
| --- | --- |
| Hệ thống (4) | Đổi mật khẩu; Tài khoản & phân quyền; Cài đặt; Nhật ký |
| Danh mục (9) | Khách hàng; Thú cưng; Nhân viên; Sản phẩm/lịch sử giá; Spa; Phòng; Nhà cung cấp; Khuyến mãi; Điểm/voucher |
| Tìm kiếm (7) | Hóa đơn; Lịch sử khách hàng; Tồn theo lô; Spa trong ngày; Sơ đồ phòng; Lịch làm việc; Lịch cá nhân |
| Xử lý (10) | Lập hóa đơn; Đổi hàng; Spa; Hotel; Nhập kho; Xuất/hủy; Kiểm kê; Ca; Phân lịch; Phê duyệt |
| Thống kê (7) | Doanh thu; Hàng hóa; Spa; Hotel; Khách hàng; Công nợ; Nhân viên |

Mỗi vai trò chỉ thấy các mục được cấp quyền; kiểm tra cả phím tắt. Tầng tạo màn hình và service cũng kiểm tra quyền. Danh sách Spa trong ngày chỉ xem, in và mở xử lý. Sơ đồ phòng chỉ tra cứu/mở bản ghi; thao tác dọn phòng ở màn xử lý Hotel.

## Kiểm chứng và giới hạn

- Bộ test gồm 77 kiểm thử, trong đó 40 tình huống SQL chỉ chấp nhận `PetStationIntegrationTest`; có thao tác Swing Spa/Hotel và ảnh sản phẩm, 37 menu/bốn vai trò, rollback thiếu tồn, voucher, cọc, duyệt giá, SQL mất kết nối và mở kết nối lại. Các fixture nghiệp vụ rollback; test lưu bền xóa đúng bản ghi riêng nó tạo.
- XLSX được đọc lại bằng POI và kiểm tra bộ lọc, cột hiển thị, kiểu tiền/ngày, không biến văn bản thành công thức. QR đối chiếu mẫu VietQR công bố và giải mã lại ảnh bằng ZXing.
- Xem trực tiếp tệp trong WPS và in qua Microsoft Print to PDF là bước kiểm tra thủ công trên máy. Không coi việc tạo được XLSX/ảnh QR là bằng chứng đã mở WPS hoặc đã chuyển tiền.
- Chỉ hỗ trợ một phương thức mỗi hóa đơn. Cọc Hotel trong đợt demo được thu **tiền mặt**, ghi ca nhận cọc. Thu khoản còn lại có tiền mặt, QR hoặc thẻ với xác nhận nhận tiền thủ công.
- Hợp đồng BOOKED cũ chưa có xác nhận cọc không được tự đánh dấu đã thu trong migration. Không tự sửa tiền cọc hoặc trạng thái của hợp đồng cũ để vượt kiểm tra mới; cần đối chiếu chứng từ trước khi xử lý.
- Phiếu hoàn tiền cũ vẫn được tra cứu. Không tạo phiếu hoàn tiền mới, không duyệt phiếu cũ thành giao dịch đổi hàng mới. Hàng thay thế đã đổi không đổi tiếp; hóa đơn gắn phiếu đổi hoàn tất không hủy trực tiếp.
- Giữ ba ca và phí trả trễ hiện tại. Chưa có bán ngoại tuyến, đồng bộ nhiều máy, xác nhận số dư ngân hàng tự động hoặc bộ cài máy khác. Không kiểm tra chuyển tiền thật trong demo.

Nguồn kỹ thuật: [VietQR: tham số và mẫu QR](https://www.vietqr.io/danh-sach-api/link-tao-ma-nhanh/api-tao-ma-qr), [Apache POI XSSF](https://poi.apache.org/components/spreadsheet/quick-guide.html).

## Kết quả đợt hoàn thiện nghiệp vụ trước khi thêm danh mục thực tế

- Maven package thành công; **68/68 test đạt, không bỏ qua**, gồm 34 tình huống SQL trên database kiểm thử riêng. Có kiểm thử bấm các nút Spa/Hotel trên luồng Swing, giữ bản ghi qua màn, nhận cọc, thanh toán, dọn phòng; kiểm tra quyền tạo màn hình của 37 mục menu với bốn vai trò.
- JAR cuối gọi đúng `com.petstation.App`, kết nối database hiện tại và mở được `DangNhapFrame`. F5 và script chạy giữ điểm chạy cũ.
- Đã kiểm tra đăng nhập thực tế bằng service cho cả bốn tài khoản demo. Không đổi mật khẩu tài khoản quản lý đã có của chủ dự án.
- Sau nâng cấp: hóa đơn **1 → 1**, khách **6 → 9**, sản phẩm **12 → 14**, lô hàng **13 → 15**, tài khoản **5 → 5**. Phần tăng là dữ liệu DEMO có mã riêng; dấu vân tay dữ liệu cũ khớp trước/sau. Không tạo hóa đơn demo giả trong database đang dùng khi kiểm thử tự động.
- XLSX mẫu xuất đúng hai dòng DEMO đang lọc; kiểm tra cấu trúc và đọc lại bằng POI đạt. QR được đối chiếu bộ mẫu công bố và giải mã cục bộ; không xác nhận chuyển tiền thật.
- **Chưa xác nhận thủ công:** mở XLSX trong WPS, quét bằng ứng dụng ngân hàng trên điện thoại, in qua máy in PDF. Excel trên máy dừng ở màn hình quyền riêng tư; người dùng chọn WPS rồi dừng Computer Use bằng Esc, nên không tiếp tục điều khiển ứng dụng Windows.

## Bộ sản phẩm thực tế và dữ liệu liên thông REAL_DATA_V1

- Nguồn sản phẩm và quy cách: [NGUON_SAN_PHAM.md](NGUON_SAN_PHAM.md). Thêm 60 mã gồm 24 thức ăn, 16 phụ kiện, 10 grooming và 10 vệ sinh, mỗi mã có ảnh tải cục bộ. 14 mã cũ giữ nguyên giá và lịch sử; mã chưa đủ thông tin xác minh dùng “Chưa có ảnh”.
- Thêm 20 khách `KH000010..20`, 30 thú cưng `TC000008..30`. Thú cưng 01..24 có hai mũi tiêm giả lập; 25..30 thiếu thông tin để thử chặn tiếp nhận. Số điện thoại và email đều giả lập, không liên hệ thực tế.
- Thêm 60 phiếu nhập với tồn mở đầu 10–30 đơn vị, sáu nhà cung cấp giả lập, sáu gói Spa và mười phòng Hotel. Giá nhập/HSD/tồn là tình huống DEMO, giá bán ban đầu theo trang nguồn tại ngày thu thập.
- 30 hóa đơn `HD000001..30` trải trong 30 ngày tính từ ngày nhập đầu: 01..18 bán sản phẩm, 19..24 Spa, 25..28 Hotel, 29..30 đổi phụ kiện. Các hóa đơn đều thu tiền mặt **giả lập**, có phân bổ lô, lịch sử điểm và ca đã đối chiếu. Không có giao dịch ngân hàng thật.
- Mở `SPA000007..4` để tiếp tục từ tiếp nhận/đang làm/hoàn thành; lịch hẹn `LH000011..6` ở các ngày sắp tới. Hotel `HT000005..2` đang ở, 3..4 đặt trước, cọc giả lập đủ 50%, có nhật ký chăm sóc.
- Đăng nhập `demo.sales` để mở `TREO000001..2`. Quản lý có thể duyệt/từ chối hai đề xuất giá, một kiểm kê lệch và một nghỉ phép có mã `YC-REAL-*`. Voucher `VC000001` thuộc khách 01, được đổi bằng 200 điểm với sổ điểm đi kèm.
- Cảnh báo tồn thấp có `SP-VS-010`, còn 3 sau phiếu xuất hủy mẫu; một số lô theo dõi HSD còn 15 ngày. Đối chiếu chi tiết nhập/xuất trước khi thao tác để không nhầm tồn mở đầu và tồn hiện tại.

### Đối chiếu đợt bổ sung ngày 19/10/2026

| Dữ liệu | Trước | Sau |
| --- | ---: | ---: |
| Sản phẩm | 14 | 74 |
| Khách hàng | 9 | 29 |
| Thú cưng | 7 | 37 |
| Hóa đơn | 1 | 31 |
| Lô hàng | 15 | 75 |

Backup đã chạy COPY_ONLY/CHECKSUM và RESTORE VERIFYONLY; đường dẫn cục bộ ghi ở `target/product-import/database-backup.txt`. Dấu vân tay bản ghi cũ trong 42 bảng khớp trước/sau. Bộ nhập chạy lần hai không đổi dữ liệu.

Maven đạt **77/77 kiểm thử, không bỏ qua**, gồm 40 tình huống SQL trên `PetStationIntegrationTest`; có kiểm tra bấm ảnh thêm đúng một đơn vị, chọn/xóa chọn dòng danh mục, lưu ảnh không đổi giá/tồn và chặn đường dẫn ngoài thư mục ảnh. Đã đối chiếu tồn theo chứng từ, tiền ca, điểm và liên kết Spa/Hotel/đổi hàng.

Ảnh xem thử và XLSX 60 sản phẩm ở `target/product-import/preview/`. JAR được kiểm tra từ thư mục khác, đọc đủ 60 ảnh mà không tải mạng; đã kiểm tra giỏ hàng không chứa ảnh. Chưa xác nhận mở XLSX bằng WPS hoặc in qua máy in PDF.

## Các thao tác giao diện mới

Bản mới đạt 97 kiểm thử; đã kiểm tra in PDF nhiều trang. Các ghi chú “chưa in PDF” ở phần kết quả các đợt trước là trạng thái tại thời điểm đó.

- Danh mục: dùng bộ lọc trạng thái, kết hợp nhóm/loài/thương hiệu/chủ nuôi theo từng màn hình. Xóa bộ lọc đưa về trạng thái đang dùng; Excel lấy đúng dòng đang hiển thị.
- Thống kê: mặc định 30 ngày qua. Chọn kỳ hoặc Từ ngày–Đến ngày rồi bấm **Xem thống kê**. Tồn kho, phòng và dư nợ hiện tại được ghi rõ, không phải số cuối kỳ quá khứ.
- Doanh thu: phân biệt **Doanh thu hoàn tất** với **Tiền thu ròng (gồm cọc)**; cọc lưu trú có góc nhìn riêng.
- Ca thu ngân: nhập tổng tiền đầu ca; kết ca để trống thực đếm cho đến khi đếm xong. Chọn nộp quỹ hoặc người nhận ca; có chênh lệch phải nhập lý do. Kiểm đếm mệnh giá là tùy chọn.
- Xem hóa đơn: bảng hàng đầy đủ, cuộn dọc nếu hóa đơn dài; In là thao tác phụ.
- Tìm kiếm/Xử lý: **N kết quả** là số dòng đang lọc; **Làm mới** đọc lại SQL và giữ lọc/sắp xếp/dòng chọn.
- Sơ đồ phòng: rê chuột hoặc dùng Tab để xem chi tiết; nhấn phòng trống để đặt đúng phòng, phòng có thú để mở hợp đồng đang ở.
- Xem hóa đơn: bill dọc 80mm có logo. Hotel ghi rõ khoản sau cọc; điểm sau giao dịch chỉ hiện khi có lịch sử xác định được. In qua hộp thoại hiện có, trên A4/PDF nội dung vẫn hẹp và căn giữa.
- Khi thử lịch, chọn ngày 10–31 hoặc chuyển tháng; Xóa ngày bắt buộc rồi bấm xem báo cáo sẽ được yêu cầu chọn đủ ngày.

## Quy tắc và giới hạn hiện tại

- Giữ năm nhóm menu, 37 mục. Nhập kho thuộc Xử lý với tên Lập phiếu nhập kho; Dịch vụ Spa trong Danh mục quản lý bảng giá, Lập phiếu Spa thực hiện nghiệp vụ. Lịch sử giá vẫn nằm trong Sản phẩm.
- Giữ ba ca và phí trả trễ đang cấu hình. Thanh toán một phương thức cho mỗi hóa đơn; nhân viên kho đề xuất giá, quản lý duyệt áp dụng. Điểm không đổi trực tiếp ra tiền; chỉ đổi phụ kiện trong ba ngày, không hoàn tiền mới.
- Danh mục có bộ lọc kết hợp và trạng thái riêng. Báo cáo dùng cùng khoảng ngày cho bảng/biểu đồ/Excel; tồn, phòng và dư nợ hiện tại ghi rõ thời điểm. Cọc Hotel tách khỏi doanh thu hoàn tất; công nợ theo kỳ chứng từ không được coi là dòng tiền trả nợ trong kỳ.
- Ca thu ngân nhập tiền đầu ca và thực đếm trực tiếp, không tự điền thực đếm. Có chênh lệch cần lý do; nộp quỹ không cần người nhận, bàn giao chọn quản lý/thu ngân phù hợp.
- Sơ đồ phòng có bộ lọc mã/loại/trạng thái. Hover sau khoảng 300ms hoặc dùng Tab xem chi tiết; phòng dọn/bảo trì/ngừng chỉ xem. Popup dùng dữ liệu đã nạp, không truy vấn SQL theo mỗi lần rê chuột.
- Cài đặt giữ nội dung chưa lưu khi quay lại; Làm mới hỏi xác nhận bỏ bản nháp. Đổi mật khẩu có icon mắt trong ô nhập. Màu chính đỏ/vàng/kem/nâu, màu trạng thái giữ ý nghĩa riêng.
- Phiếu nhập hiện một sản phẩm/một lô; xuất kho chỉ là xuất hủy. Chưa có xuất nội bộ/chuyển kho/trả nhà cung cấp, hóa đơn điện tử, xác nhận ngân hàng tự động hoặc bàn giao hai bên xác nhận.

## Kết quả kiểm tra gần nhất

Đợt dọn mã ngày 20/10/2026 build sạch và đạt **97/97 kiểm thử**, gồm 51 tình huống SQL chỉ trên `PetStationIntegrationTest`. Đã kiểm tra tạo màn hình theo quyền: quản lý 37 mục, kho 8, bán hàng 14, Spa/Hotel 16; giữ các góc nhìn báo cáo.

Biên lai kiểm tra 50 dòng tên dài in qua Microsoft Print to PDF thành 4 trang, đọc lại và xem ảnh đủ tên hàng/dòng cuối/tổng tiền. Đã kiểm tra giấy hẹp 80mm bằng Printable; chưa thử máy in nhiệt vật lý. Lịch kiểm tra tháng 28/29/30/31 ngày và cỡ chữ tương ứng 100%/125%/150%, chưa đổi DPI Windows thật. Ảnh bố cục tương ứng 1366×768/1920×1080 và báo cáo kiểm tra cục bộ nằm tại `target/ui-repair/`; các file này không có trong checkout Git.

Sửa Unicode đã phục hồi 23 tên hạng và 2 giới tính thú cưng trong database hiện tại, đối chiếu 42 bảng và tài khoản/quyền/cấu hình trước–sau, chạy lại không đổi số dư. Đường dẫn backup được lưu ở `target/ui-repair/database-backup.txt`; backup thật nằm trong thư mục Backup SQL Server. JAR sau khi dọn đã mở màn đăng nhập và kết nối SQL; F5 giữ điểm chạy `com.petstation.App`.
