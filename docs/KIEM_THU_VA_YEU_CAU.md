# Kịch bản kiểm thử và demo

## Chạy tự động

```powershell
./mvnw.cmd -B -ntp verify
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-sqlserver.ps1
```

Lệnh đầu chạy bộ không cần SQL và bỏ qua 60 tình huống SQL. Lệnh sau sao lưu/reset `PetStationIntegrationTest`, nạp hai SQL rồi chạy đủ bộ. Không đổi tên này thành database đang bán hàng. Báo cáo kết quả nằm ở `target/surefire-reports/`.

Nếu database kiểm thử đã chuẩn bị, có thể đặt `PETSTATION_SQL_TESTS=true` và URL JDBC trỏ `PetStationIntegrationTest`, rồi chạy Wrapper verify. Cấu hình SQL login vẫn lấy từ tệp ngoài/env của máy. Suite tự chặn tên database khác.

`DuLieuMauSQLTest` kiểm tra toàn bộ tồn theo nhập–bán–xuất–đổi, số dư điểm theo lịch sử, khoản thu theo hóa đơn/cọc và khóa ngoại/CHECK. Test không cần bảng đối chiếu mã cũ. Các test nghiệp vụ tạo fixture riêng rồi rollback; test lưu bền tự xóa đúng bản ghi thử.

## Đối chiếu yêu cầu môn học

| Yêu cầu | Nơi kiểm tra |
| --- | --- |
| Thêm/sửa/xóa, danh sách/chi tiết | Danh mục, `DanhMucService`, `DanhMucImpl`, test CRUD SQL |
| Quan hệ khi sửa/xóa | FK/CHECK, validation service, test vòng đời Spa/Hotel/kho/đổi hàng |
| Tìm đơn giản và nâng cao | Từ khóa + lọc kết hợp; test chọn dòng/sắp xếp/Excel |
| Phím tắt, tab | `CauTrucMenu`, `ChinhFrame`, test navigation và chạy thủ công |
| HĐT/phân tầng | Interface DAO/Impl, chính sách giỏ hàng, lớp nền UI, test package |
| Quyền, transaction, lỗi | Test báo cáo cá nhân/toàn cửa hàng, rollback, thu lặp, thiếu tồn |
| Nhật ký AI/làm nhóm | AI_USAGE_LOG, PR, commit và người review thật |

Chứng từ tài chính có vòng đời duyệt/hủy; không mở thao tác xóa tự do lên mọi bảng vật lý. Sinh viên cần giải thích lựa chọn này bằng ràng buộc nghiệp vụ.

## Các ca nhóm tự chạy và ghi kết quả

| Mã | Thao tác | Mong đợi |
| --- | --- | --- |
| DM-01 | Lần lượt thêm, tìm, xem chi tiết, sửa và xóa bản ghi mới ở 9 danh mục | Lưu đúng, chọn dòng không lệch khi lọc/sắp xếp |
| DM-02 | Trùng mã, thiếu trường bắt buộc, số âm, tên dài tiếng Việt | Báo lỗi rõ; không lưu một phần |
| QH-01 | Xóa khách có thú/hóa đơn, sản phẩm đã bán, phòng có hợp đồng | Chặn/chuyển trạng thái đúng; không mất chứng từ hay tạo mồ côi |
| TK-01 | Từ khóa + hai bộ lọc, không kết quả, bỏ lọc, xuất Excel | Count/bảng/Excel cùng phạm vi và thứ tự |
| UI-01 | Tab/Shift+Tab, Enter/Escape, phím tắt menu; thử 1100×680, 1366×768, 1920×1080 và scale 125%/150% | Focus rõ, không kẹt hoặc che nút/chồng nhãn |
| BH-01 | Tiền mặt + QR + thẻ; thiếu/thừa 1 đồng; QR chưa xác nhận | Chỉ ghi khi đủ phân bổ và xác nhận; chỉ tiền mặt có tiền thừa |
| BH-02 | Bấm thu lặp, mất kết nối hoặc lỗi khi đang ghi | Không thu hai lần; tiền/tồn/điểm cùng rollback |
| SP-01 | Đặt/sửa trùng giờ, tiếp nhận, thêm dịch vụ, hoàn tất, thanh toán | Chặn trùng tại service; đúng phiếu qua các bước |
| HT-01 | Giữ phòng chưa cọc, thu nhiều đợt qua hai ca, nhận, chăm sóc, trả và dọn phòng | Chặn nhận khi thiếu cọc; quyết toán trừ thực thu một lần |
| BC-01 | Kỳ 1/7/30/90/365 ngày, 0, số âm, nhiều nhóm, nhãn dài | Biểu đồ/bảng/Excel/chứng từ khớp; không dùng tỷ trọng sai cho số âm |
| PQ-01 | Dùng vai trò bán hàng gọi service xem công nợ/báo cáo toàn cửa hàng | Từ chối ở service, không chỉ ẩn nút |
| AN-01 | Ảnh giả đổi đuôi, quá 10 MB/40 MP, di chuyển ảnh gốc sau nhập | Từ chối ảnh lỗi; ảnh đã nhập vẫn xem được |
| MT-01 | Clone vào thư mục khác, import lần lượt VS Code/IntelliJ/Eclipse và cấu hình SQL mới | Build bằng Wrapper, không sửa source hay chép JAR thủ công |
| DL-01 | Nạp mẫu hai lần trên database thử | Không tăng số dòng, mọi FK/CHECK được kiểm tra lại; tiền/tồn/điểm đối chiếu được |

Mỗi ca cần ghi người chạy, ngày, dữ liệu, kết quả thật và bằng chứng vào PR/nhật ký nhóm. Các dòng trên là kịch bản, không phải xác nhận sinh viên đã thực hiện. Test có AI hỗ trợ không thay việc sinh viên tự viết ca biên/ngoại lệ và giải thích kết quả.

## Demo ngắn

Đăng nhập từng vai trò → mở danh mục và lọc → lập hóa đơn hàng hóa → Spa từ lịch đến thanh toán → Hotel từ giữ phòng/thu cọc đến dọn phòng → kho và phê duyệt → báo cáo và xuất Excel. Dùng tài khoản quản lý xem nhật ký/quyền. Kiểm tra cả tình huống bị chặn, không chỉ đường đi thành công.

## Cần kiểm tra thêm khi nghiệm thu

Máy in thật, Excel/WPS, quét QR bằng điện thoại, DPI Windows và import IDE trên các máy khác chưa được thay thế bằng test tự động. Với dữ liệu lớn, ghi cấu hình máy, số dòng, thời gian tải/lọc và bộ nhớ; chưa có số đo tải/p95 hay kiểm thử xâm nhập trong hồ sơ hiện tại.

## Kết quả ngày 26/10/2026

- Source cuối cùng: **136 test đạt, 0 lỗi, 0 thất bại, 0 bỏ qua**, gồm 76 test không cần SQL và 60 test SQL trên `PetStationIntegrationTest`.
- Bản sao source/tài nguyên công khai, không có cấu hình SQL cá nhân, build thành công bằng Wrapper; 60 test SQL được bỏ qua đúng thiết kế.
- JAR chạy kiểm tra kết nối/đăng nhập từ thư mục khác qua cấu hình ngoài; cả bốn tài khoản demo trả đúng vai trò trên database kiểm tra.
- Seed có 48 phần bảng, 44 câu INSERT cho 44 bảng có dữ liệu; nạp lại hai lần không tăng số dòng, không tắt khóa ngoại.
- `PetStationDemo2` đã sao lưu/VERIFYONLY rồi reset theo lựa chọn của người dùng. Sau reset: 48 bảng ứng dụng; tiền hóa đơn đã thu khớp khoản thu 4.921.000 đồng; khoản cọc 1.755.000 đồng; FK và số dư điểm không lệch. Lệnh setup mặc định chạy lại không thay dữ liệu.

Đây là kết quả chạy bằng công cụ, không ghi nhận thay việc sinh viên tự nghiệm thu. CI đa hệ điều hành/JDK chỉ mới được cấu hình, chưa chạy trên GitHub trong phiên này.
