# HƯỚNG DẪN ĐÓNG GÓP VÀ QUY TẮC LÀM VIỆC (CONTRIBUTING)
**Đồ án Môn Phát triển Ứng dụng - Nhóm 03**

---

## 1. Quy tắc phân nhánh (Git Branching Strategy)
* **Tuyệt đối KHÔNG commit trực tiếp lên nhánh `main`.**[cite: 3]
* Nhánh **`main`**: Nhánh chứa mã nguồn hoàn chỉnh, chạy ổn định, dùng để nộp bài hoặc báo cáo.
* Nhánh **`develop`**: Nhánh tích hợp chung của nhóm[cite: 3].
* Nhánh **`feature/<tên-chức-năng>`**: Mỗi thành viên phải tự tạo nhánh riêng để làm việc (Ví dụ: `feature/pet-management`, `feature/pos-checkout`)[cite: 3].
* Sau khi hoàn thành tính năng và test kỹ, tạo **Pull Request (PR)** để merge vào nhánh `develop`[cite: 3].

## 2. Quy định về Commit Message
Viết commit rõ nghĩa, bắt đầu bằng các tiền tố chuẩn (Conventional Commits):
* `feat:` Thêm tính năng mới (Ví dụ: `feat: add pet category management screen`)
* `fix:` Sửa lỗi (Ví dụ: `fix: resolve null pointer exception in checkout`)
* `docs:` Cập nhật tài liệu (Ví dụ: `docs: update SRS requirement document`)
* `refactor:` Tái cấu trúc code không đổi chức năng
* ❌ **Cấm các commit chung chung** như: `update`, `fix bug`, `123`, `asdf`[cite: 3].

## Code Java

Java 17, UTF-8, 4 dấu cách. Package chữ thường; lớp PascalCase; hàm/biến camelCase; hằng UPPER_SNAKE_CASE. Dùng tiếng Việt không dấu nhất quán với mã hiện tại, giữ các mã trạng thái/quyền đã lưu trong database.

Interface DAO nằm ở `dao/TenDAO.java`; triển khai SQL ở `dao/impl/TenDAOImpl.java` và `implements TenDAO`. Kết nối/transaction ở `database`. GUI gọi service; service kiểm tra quyền và quy tắc trước khi gọi DAO. Không đưa SQL vào GUI hoặc thêm một DAO khác làm lại cùng luồng.

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
=======
## 3. Quy chuẩn lập trình (Coding Convention)
* **Ngôn ngữ:** Java hoặc C# (theo lựa chọn của nhóm)[cite: 3].
* **Mô hình kiến trúc:** Áp dụng mô hình phân tầng rõ ràng (n-Tier Architecture)[cite: 3].
* **Lập trình hướng đối tượng (OOP):** Tuân thủ các nguyên tắc đóng gói, kế thừa, đa hình và SOLID.
* **Quy tắc đặt tên:**
  * Tên Class: PascalCase (Ví dụ: `PetController`, `InvoiceService`)
  * Tên biến / hàm: camelCase (Ví dụ: `calculateTotalPrice`, `petName`)
  * Tên bảng CSDL: Snake_case hoặc PascalCase đồng nhất.
