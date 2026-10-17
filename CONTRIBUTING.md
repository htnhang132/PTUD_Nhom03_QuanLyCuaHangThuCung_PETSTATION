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
* **Ngôn ngữ:** Java hoặc C# (theo lựa chọn của nhóm).
* **Mô hình kiến trúc:** Áp dụng mô hình phân tầng rõ ràng (n-Tier Architecture).
* **Lập trình hướng đối tượng (OOP):** Tuân thủ các nguyên tắc đóng gói, kế thừa, đa hình và SOLID.
* **Quy tắc đặt tên:**
  * Tên Class: PascalCase (Ví dụ: `PetController`, `InvoiceService`)
  * Tên biến / hàm: camelCase (Ví dụ: `calculateTotalPrice`, `petName`)
  * Tên bảng CSDL: Snake_case hoặc PascalCase đồng nhất.

## 4. Quy trình một Pull Request
1. `git checkout develop && git pull`
2. `git checkout -b feature/<tên-chức-năng>`
3. Commit nhỏ, đúng tiền tố; trước khi đẩy: `git fetch && git rebase origin/develop`
4. `git push -u origin feature/<tên-chức-năng>` rồi mở Pull Request vào `develop`
5. Cần ít nhất 1 thành viên duyệt; gộp bằng Squash and merge; xóa nhánh sau khi gộp.
