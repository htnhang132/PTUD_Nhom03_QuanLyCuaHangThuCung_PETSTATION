# HƯỚNG DẪN ĐÓNG GÓP VÀ QUY TẮC LÀM VIỆC (CONTRIBUTING)
**Đồ án Môn Phát triển Ứng dụng - Nhóm 03**

---

## 1. Quy tắc phân nhánh (Git Branching Strategy)
* **Tuyệt đối KHÔNG commit trực tiếp lên nhánh `main`.**
* Nhánh **`main`**: Nhánh chứa mã nguồn hoàn chỉnh, chạy ổn định, dùng để nộp bài hoặc báo cáo.
* Nhánh **`develop`**: Nhánh tích hợp chung của nhóm.
* Nhánh **`feature/<tên-chức-năng>`**: Mỗi thành viên phải tự tạo nhánh riêng để làm việc (Ví dụ: `feature/pet-management`, `feature/pos-checkout`).
* Sau khi hoàn thành tính năng và test kỹ, tạo **Pull Request (PR)** để merge vào nhánh `develop`.

## 2. Quy định về Commit Message
Viết commit rõ nghĩa, bắt đầu bằng các tiền tố chuẩn (Conventional Commits):
* `feat:` Thêm tính năng mới (Ví dụ: `feat: add pet category management screen`)
* `fix:` Sửa lỗi (Ví dụ: `fix: resolve null pointer exception in checkout`)
* `docs:` Cập nhật tài liệu (Ví dụ: `docs: update SRS requirement document`)
* `refactor:` Tái cấu trúc code không đổi chức năng
* ❌ **Cấm các commit chung chung** như: `update`, `fix bug`, `123`, `asdf`.

## 3. Quy chuẩn lập trình (Coding Convention)
* **Ngôn ngữ:** Java 17, Swing, Maven; tệp mã nguồn lưu UTF-8.
* **Mô hình kiến trúc:** Áp dụng mô hình phân tầng rõ ràng (n-Tier Architecture).
* **Lập trình hướng đối tượng (OOP):** Tuân thủ các nguyên tắc đóng gói, kế thừa, đa hình và SOLID.
* **Quy tắc đặt tên:** dùng tiếng Việt không dấu, thống nhất thuật ngữ với SQL hiện tại.
  * Kiểu Java và tệp: PascalCase; tệp trùng tên kiểu chính, ví dụ `KhachHang.java`, `KhachHangDAO.java`, `JdbcKhachHangDAO.java`.
  * Hàm, trường, biến, tham số và thành phần record: camelCase; hàm bắt đầu bằng động từ, ví dụ `timTheoSoDienThoai`, `capNhat`, `kiemTraRong`.
  * Hằng nội bộ: UPPER_SNAKE_CASE. Biến Swing có tiền tố `btn`, `txt`, `tbl`, `lbl`, `pnl`, `cbo`, `spn` theo loại thành phần.
  * Giữ cấu trúc package, hậu tố DAO/Service/Panel/Frame/Dialog/Exception/Test và tiền tố Jdbc. Giữ Spa, Hotel, Voucher, SQL, JDBC, Excel.
  * Giữ `com.petstation.App`, `main` và tên override do Java/Swing/thư viện quy định. Accessor tự sinh của record phải trùng tên thành phần record.
  * Giữ tên bảng/cột, câu SQL, giá trị trạng thái/mã quyền/mã màn hình, biến môi trường, khóa cấu hình và tên tài nguyên. Không dịch các mã enum dùng cho lưu trữ hoặc điều hướng.
  * Comment và Javadoc viết tiếng Việt có dấu; ghi mục đích lớp/hàm, tham số, kết quả, ngoại lệ và các bước xử lý cần giải thích.
  * Khi đổi tên phải cập nhật nơi gọi, import, constructor, method reference, test, cấu hình chạy và tài liệu; không giữ lớp chuyển tiếp tên cũ.
* Bảng đối chiếu đầy đủ: [Tên mã nguồn](docs/TEN_MA_NGUON.md). Ví dụ: `Customer` → `KhachHang`, `SalesService` → `BanHangService`, `Money` → `TienTe`.

## 4. Quy trình một Pull Request
1. `git checkout develop && git pull`
2. `git checkout -b feature/<tên-chức-năng>`
3. Commit nhỏ, đúng tiền tố; trước khi đẩy: `git fetch && git rebase origin/develop`
4. `git push -u origin feature/<tên-chức-năng>` rồi mở Pull Request vào `develop`
5. Cần ít nhất 1 thành viên duyệt; gộp bằng Squash and merge; xóa nhánh sau khi gộp.
