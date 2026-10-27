# Quy ước làm việc của nhóm

## Git

Mỗi thành viên làm trên `feature/<ten-chuc-nang>`, tạo PR vào `develop` sau khi đã chạy thử. Kết thúc giai đoạn thì tạo PR `develop` → `main`. Không commit trực tiếp lên hai nhánh tích hợp. Mỗi PR có ít nhất một thành viên khác review.

Commit nêu rõ phần thay đổi, ví dụ `refactor(dao): move deposit queries to LuuTruImpl`, `test(sales): check rollback when allocation is invalid`. Không dùng `update`, `fix bug`, `123`.

Hook `.githooks/pre-commit` hỗ trợ chặn commit sai nhánh. Nếu chưa có hook cá nhân cần giữ, bật bằng `git config core.hooksPath .githooks`; Linux/macOS thêm quyền chạy cho hook. Quản trị repo cần bật bảo vệ nhánh/PR/check trên GitHub; hook cục bộ không thay bảo vệ máy chủ. Cấu hình này chưa được tự thay đổi trên GitHub.

## Code Java

Java 17, UTF-8, 4 dấu cách. Package chữ thường; lớp PascalCase; hàm/biến camelCase; hằng UPPER_SNAKE_CASE. Dùng tiếng Việt không dấu nhất quán với mã hiện tại, giữ các mã trạng thái/quyền đã lưu trong database.

Interface DAO nằm ở `dao/TenDAO.java`; triển khai SQL ở `dao/impl/TenImpl.java` và `implements TenDAO`. Kết nối/transaction ở `database`. GUI gọi service; service kiểm tra quyền và quy tắc trước khi gọi DAO. Không đưa SQL vào GUI hoặc thêm một DAO khác làm lại cùng luồng.

Tên hàm nêu hành động, như `layLoTheoFEFO`, `truTonLo`, `ganHoaDonSpa`; không dùng tên có số thứ tự như `timThanhToan2`. Biến cần nói rõ đối tượng. Comment giải thích quy tắc hoặc lý do, không lặp câu lệnh. Tránh thêm interface/lớp trung gian nếu không có trách nhiệm riêng.

Dùng `try-with-resources` cho JDBC/I/O. Không nuốt lỗi, không ghi tiền/tồn/điểm rời transaction. Dữ liệu người nhập truyền bằng tham số SQL. Không log mật khẩu hay cấu hình thật. Không thay mã/hành vi nghiệp vụ chỉ để làm code ngắn hơn.

Truy vấn dài chạy nền và cập nhật Swing trên EDT. Giữ ID bản ghi khi lọc/sắp xếp; kiểm tra phiên/token trước khi nhận kết quả nền. Không tách một phần transaction đang giữ khóa sang luồng khác.

## Trước PR

```powershell
./scripts/format-java.ps1 -Mode check
./mvnw.cmd -B -ntp verify
```

Khi thay luồng dữ liệu, chạy thêm `scripts/test-sqlserver.ps1`. Đính kèm kết quả thật và nói rõ test nào bị bỏ qua. Formatter cố định Google Java Format 1.24.0, chế độ AOSP; dùng `-Mode apply` để sửa format. Nếu thiếu formatter, tải bằng `./mvnw.cmd dependency:get '-Dartifact=com.google.googlejavaformat:google-java-format:1.24.0:jar:all-deps'`.

Cập nhật [nhật ký AI](AI_USAGE_LOG.md) nếu có dùng AI: prompt, kết quả, giữ/sửa gì và lý do. Người phụ trách phải tự đọc, giải thích và sửa thử phần của mình; không ghi xác nhận hiểu mã hoặc tên reviewer thay cho người khác. PR template đã có các mục này.
