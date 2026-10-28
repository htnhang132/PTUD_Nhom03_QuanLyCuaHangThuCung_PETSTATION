# NHẬT KÝ SỬ DỤNG AI (AI USAGE LOG)
**Đề tài:** Xây dựng Hệ thống Quản lý Cửa hàng Thú cưng  
**Môn học:** Phát triển Ứng dụng - Nhóm 03  

---

## BẢNG GHI NHẬN SỬ DỤNG AI HÀNG TUẦN

| STT | Tuần / Ngày | Prompt đã sử dụng (Nội dung yêu cầu AI) | Kết quả AI trả về | Phần giữ nguyên | Phần chỉnh sửa & Lý do chỉnh sửa |
| :---: | :---: | :--- | :--- | :--- | :--- |
| **01** | Tuần 01<br>(12/08/2026) | *Lập kế hoạch chi tiết 11 tuần cho đồ án quản lý cửa hàng thú cưng gồm 5 thành viên.* | Danh sách phân chia công việc theo 5 giai đoạn sơ bộ. | Cấu trúc 5 giai đoạn phát triển phần mềm. | **Chỉnh sửa:** Phân lại tên thành viên cụ thể (Nhàng, Vinh, Vỉ, Vy, Đạt) theo đúng phân hệ thực tế của nhóm. |
| **02** | 28/10/2026 | *Khôi phục khả năng build và khởi chạy giao diện nhánh develop sau khi đối chiếu với nhánh vi.* | Phục hồi các source Java còn thiếu, database layer, DAO interface, model, GUI, Maven Wrapper, schema/seed, script setup, cấu hình mẫu và tài nguyên giao diện; tạo PR #43. | Giữ lại source đang có ở develop và các implementation DAO hậu tố `*DAOImpl`; thêm cấu hình build và hướng dẫn chạy từ checkout sạch. | Loại bỏ hai lớp `com.petstore` skeleton (entry TODO và SQL credentials hard-code/TLS toàn JVM); bỏ các binary `target/classes` cũ. CI Windows/Linux với JDK 17/21 thành công. Chưa xác minh đăng nhập với SQL Server trên máy người dùng; cần chạy theo `SETUP.md` và kiểm thử SQL riêng. |
