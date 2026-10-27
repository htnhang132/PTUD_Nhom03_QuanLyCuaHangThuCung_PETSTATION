# Nhật ký sử dụng AI

Đề tài: Pet Station — Quản lý cửa hàng thú cưng, PTUD Nhóm 03.

Mục đích sử dụng AI: hỗ trợ rà soát mã, đề xuất cách tổ chức, viết/sửa một số phần và gợi ý kiểm thử. Nhật ký ghi nội dung hỗ trợ và thay đổi đã áp dụng. Người phụ trách, phần sinh viên tự sửa và người review chỉ ghi sau khi thành viên thực hiện thật; không xác nhận thay.

## 1. Hoàn thiện giao diện và nghiệp vụ

Yêu cầu: thanh tìm kiếm/lọc/thao tác gọn một hàng; thống kê có cơ cấu và đọc được nhiều ngày; lịch Spa, chăm sóc, nhắc việc; thanh toán kết hợp và cọc Hotel nhiều đợt.

| Nội dung | Ghi nhận |
| --- | --- |
| Kết quả nhận được | Toolbar dùng chung, biểu đồ cột/tròn, lịch ngày/tuần, phiếu chăm sóc, nhắc việc, khoản thu chi tiết và các API thanh toán/cọc |
| Phần giữ | Java Swing, menu, nhận diện, quy tắc đổi phụ kiện 3 ngày không hoàn tiền và quyền hiện có |
| Phần điều chỉnh | Bố cục responsive, xuất Excel theo lọc, dữ liệu khoản thu và kiểm tra trạng thái/transaction |
| Lý do | Đồng nhất giao diện và sử dụng nghiệp vụ đã có rõ hơn |
| Giới hạn hồ sơ | Chưa có bản xuất đầy đủ prompt/kết quả của các phiên cũ; không tự gán ngày hoặc thành viên cho từng đoạn mã |

## 2. Chuẩn hóa theo yêu cầu môn học — 26/10/2026

Prompt chi tiết: [phần yêu cầu môn học](docs/AI_PROMPTS.md#prompt-người-dùng--26102026).

| Nội dung | Ghi nhận |
| --- | --- |
| Kết quả nhận được | Tách SQL về tầng dữ liệu, model không gọi service/GUI; kiểm thử quyền và giỏ hàng; quy ước code và quy trình PR |
| Phần giữ | Quy tắc tiền, tồn, điểm, cọc, các mã lưu trong SQL và chức năng giao diện |
| Phần điều chỉnh | `GioHang.ChinhSach`, các kiểu dùng chung trong model, tải nền có phiên/token, cấu hình ngoài không đóng gói mật khẩu vào JAR |
| Lý do | Dễ giải thích ranh giới, kiểm thử độc lập và tránh mang cấu hình máy cá nhân sang máy khác |
| Kiểm tra | Trước đợt giản lược tiếp theo đã chạy 135 test, không lỗi/không bỏ qua trên database kiểm thử riêng |

## 3. Giản lược cấu trúc, SQL và cách cài đặt — 26/10/2026

Prompt: [yêu cầu giản lược dự án](docs/AI_PROMPTS.md#yêu-cầu-giản-lược-dự-án). Phạm vi reset được người dùng chọn rõ: sao lưu rồi reset `PetStationDemo2` bằng bộ mẫu mới.

| Phần việc | Kết quả hỗ trợ và thay đổi áp dụng | Giữ nguyên/lý do |
| --- | --- | --- |
| DAO | Đổi `Jdbc...DAO` thành `...Impl`, tạo interface `...DAO` được dùng thật; đưa kết nối/transaction về `database`; đặt lại tên hàm thay hậu tố số | Giữ SQL và điều kiện khóa; làm rõ hợp đồng và nơi triển khai |
| Mã thừa | Bỏ model/phương thức không còn nơi gọi, tài liệu đối chiếu/migration và công cụ vá dữ liệu cũ | Giữ phần đang phục vụ chức năng; giảm đường xử lý trùng |
| SQL | Hai file: schema cuối cùng và mẫu theo từng bảng; xóa con trước/cha sau, chèn cha trước/con sau, không tắt FK/CHECK, mỗi bảng một INSERT nhiều dòng | Giữ liên kết ID, bổ sung chứng từ tồn/điểm đầu kỳ để sổ chi tiết khớp số dư |
| Chạy trên máy khác | Maven Wrapper, kiểm tra JDK/SQL Server, cấu hình ngoài và hướng dẫn từng IDE | Không phụ thuộc đường dẫn JDK/Maven/IDE của máy viết code |
| Tài liệu | Gộp hướng dẫn cấu trúc, cài đặt, quy ước và test case; viết lại nhật ký theo từng công việc | Bỏ ghi chép thao tác nội bộ; giữ prompt, kết quả, phần giữ/sửa và lý do |

Các phần trên có AI hỗ trợ trực tiếp vào mã/tài liệu. Chưa có bằng chứng để ghi phần nào là sinh viên tự viết lại sau hỗ trợ. Thành viên cần đối chiếu diff, chạy thử và bổ sung mục dưới trước khi nộp.

Kết quả kiểm tra của đợt 3: 136 test đạt, không lỗi và không bỏ qua trên database kiểm thử; build bản source sạch không kèm cấu hình cá nhân thành công. JAR kiểm tra đăng nhập bốn vai trò từ thư mục khác thành công. Bộ seed được nạp lặp trên database thử; `PetStationDemo2` đã được sao lưu và kiểm chứng backup trước khi reset. Đường dẫn backup cục bộ được ghi tại `target/database-backups/PetStationDemo2-20261026-122333.txt`. Chưa commit/push/merge hoặc thay cài đặt bảo vệ nhánh GitHub.

Bổ sung ngày 26/10/2026: người dùng báo lỗi `unmappable character (0x90) for encoding windows-1258` khi Code Runner chạy `javac App.java && java App`. Điều chỉnh `.vscode/settings.json` để Run Code dùng script Maven Wrapper của project; cập nhật hướng dẫn chạy. Giữ nguyên nội dung UTF-8 của `App.java`, không bỏ dấu tiếng Việt để che lỗi mã hóa. Lý do: cấu hình Code Runner đã bị bỏ ở đợt dọn trước, làm mất đường chạy có dependency và encoding của Maven.

Kiểm tra: `mvnw.cmd -B -ntp compile` thành công; cấu hình executor theo ngôn ngữ và đuôi `.java` đều trỏ đúng script hiện có. Không mở ứng dụng trong lượt kiểm tra này.

## 4. Tinh chỉnh nghiệp vụ & UX/UI 9 chức năng chính — 27/10/2026

Yêu cầu: Refactor toàn diện 9 chức năng (Thống kê Doanh thu & Hàng hóa, Kiểm kê tồn kho, Phân lịch & Tra cứu lịch nhân viên, Tìm kiếm hóa đơn, Danh mục Sản phẩm & Nhân viên, Cài đặt hệ thống). Loại bỏ bảng trùng lặp ở các màn hình thống kê, mở rộng biểu đồ chiếm trọn màn hình không cần cuộn chuột.

| Nội dung | Ghi nhận |
| --- | --- |
| Kết quả nhận được | 1. Thống kê: Thêm báo cáo Khung giờ cao điểm, Thứ trong tuần, Hàng tồn đọng/chôn vốn, Hao hụt kiểm kê; Bỏ bảng dữ liệu thừa, làm to biểu đồ cột & tròn chiếm trọn màn hình không cần cuộn dọc.<br>2. Phân lịch: Checkbox đa chọn, ràng buộc >40h, nghỉ phép, sao chép tuần trước, đổi ca có phê duyệt, khóa lịch tuần.<br>3. Kiểm kê: Master-Detail SplitPane, Modal Workspace kiểm kê đa sản phẩm, quét Barcode tự động +1, chế độ Blind Count, cân bằng kho an toàn.<br>4. Tìm kiếm Hóa đơn: Master-Detail SplitPane, bộ lọc đa tiêu chí (ngày, giá, PTTT, trạng thái), xem trước bill nhiệt K80 và in/xuất HTML.<br>5. Danh mục: Barcode tự sinh EAN-13, ràng buộc giá bán lẻ >= giá vốn, soft-delete bảo vệ dữ liệu đơn hàng; Reset password BCrypt 1-1 theo vai trò.<br>6. Cài đặt: JTabbedPane 4 tab trực quan, chọn thư mục lưu ảnh, khôi phục mặc định, cơ chế hot-reload cấu hình. |
| Phần giữ | Kiến trúc phân tầng (DAO, Service, GUI), kết nối MS SQL Server, transaction `thucHienGiaoDich`, logic phân quyền `Quyen`. |
| Phần điều chỉnh | Layout Master-Detail, Modal Workspace, Scrollable tự động co giãn chiều cao của `BieuDoCot`, xuất Excel vẫn giữ nguyên trên dữ liệu nền. |
| Lý do | Nâng cao trải nghiệm người dùng, đúng quy chuẩn vận hành chuỗi bán lẻ thực tế, loại bỏ cuộn chuột rườm rà. |
| Kiểm tra | `.\mvnw test-compile` hoàn tất với BUILD SUCCESS (238 source files, 27 test files). |

## 5. Phần thành viên bổ sung khi review

| Ngày, thành viên | File/hàm đã đọc | Phần giữ sau khi hiểu | Phần tự sửa và lý do | Test tự viết/chạy | Người review, commit/bằng chứng |
| --- | --- | --- | --- | --- | --- |
| Chờ nhóm ghi thực tế | | | | | |

Mỗi người tự giải thích một luồng GUI → service → DAO, chỉ ra validation/quyền/transaction và xử lý lỗi, sau đó sửa một yêu cầu nhỏ tại chỗ. Các ca gợi ý nằm trong [tài liệu kiểm thử](docs/KIEM_THU_VA_YEU_CAU.md). Không ghi “đã hiểu”, tên reviewer hay commit khi chưa thực hiện.
