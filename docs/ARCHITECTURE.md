# Kiến trúc và đối chiếu nhánh PETSTATION

> Phạm vi: đối chiếu tĩnh giữa nhánh vi và develop ngày 28/10/2026. Đây không phải xác nhận build hoặc kiểm thử runtime.

## 1. Kiến trúc hiện có trong develop

Dự án hiện có cấu trúc Java desktop với Maven, Swing, FlatLaf và JDBC SQL Server. Source nghiệp vụ chính thuộc package com.petstation; trong khi đó, package com.petstore còn tồn tại với MainApplication.java dạng khung và config/DatabaseConnection.java. Không nên coi hai package này là một kiến trúc thống nhất nếu chưa đối chiếu điểm khởi chạy và cách kết nối dữ liệu.

~~~text
src/main/java/
├── com/petstation/
│   ├── dao/
│   │   └── impl/
│   ├── model/
│   ├── service/
│   ├── gui/
│   │   ├── catalog/
│   │   ├── components/
│   │   │   └── common/
│   │   ├── processing/
│   │   │   └── inventory/
│   │   ├── reports/
│   │   ├── search/
│   │   └── system/
│   └── util/
└── com/petstore/
    ├── MainApplication.java
    └── config/
        └── DatabaseConnection.java
~~~

## 2. Thành phần có ở vi nhưng không có ở develop

So sánh branch cho thấy các thành phần sau bị loại khỏi develop. Danh sách dưới đây dùng để ưu tiên rà soát; nó **không có nghĩa** các file đã được khôi phục.

| Thành phần | Mức độ cần thiết | Khuyến nghị |
| --- | --- | --- |
| src/main/java/com/petstation/database/ — cấu hình, kết nối, transaction và tiện ích SQL | **Rất cao** | Cần đối chiếu và khôi phục/viết lại cùng các Service/DAO đang gọi lớp trong package này. |
| Các DAO interface còn thiếu trong com.petstation.dao | **Rất cao** | Đối chiếu từng `*DAOImpl implements *DAO` và các import của Service; không chỉ tạo interface rỗng. |
| Các model được Service đang dùng nhưng không còn trong source | **Rất cao** | Kiểm tra compile errors trước khi chọn bản từ vi, vì kiểu dữ liệu có thể đã đổi. |
| Điểm vào ứng dụng com.petstation.App và cửa sổ chính | **Rất cao** | Xác định một entry point thống nhất. com.petstore.MainApplication hiện chỉ là khung TODO, không phải điểm chạy thay thế đã hoàn chỉnh. |
| database/001_schema.sql, database/002_sample_data.sql | **Cao** | Có ích để thiết lập database tái lập được, nhưng phải xác nhận schema khớp toàn bộ truy vấn đang giữ lại. Chỉ dùng seed cho môi trường demo/test. |
| Maven Wrapper: .mvn/, mvnw, mvnw.cmd | **Cao** | Nên khôi phục khi chốt phiên bản Maven/JDK và kiểm tra chạy sạch trên Windows/Linux. |
| SETUP.md, các script cài/kiểm tra SQL Server | **Cao** | Phục hồi sau khi thống nhất đường dẫn cấu hình và các lệnh build thực sự hoạt động trong develop. |
| docs/KIEM_THU_VA_YEU_CAU.md, docs/NGUON_SAN_PHAM.md, docs/AI_PROMPTS.md | Trung bình | Có thể khôi phục có chọn lọc sau khi gỡ các tham chiếu tới tính năng/tệp đã bỏ. |
| Ảnh sản phẩm, logo và file cấu hình mẫu | Trung bình | Chỉ phục hồi tài nguyên còn được source dùng; thông tin kết nối thật phải được giữ ngoài Git. |
| GitHub Actions, PR template và pre-commit hook | Trung bình | Khôi phục sau khi build/test pass. CI không nên báo xanh khi source không biên dịch hoặc test đang bị bỏ qua. |
| Các màn hình nghiệp vụ lớn bị loại khỏi source | Cần quyết định nghiệp vụ | Không khôi phục hàng loạt chỉ để giống cây thư mục vi; xác định chức năng nào vẫn thuộc phạm vi sản phẩm rồi mới đưa code và test tương ứng trở lại. |

## 3. Các điểm cần xử lý trước khi gọi develop là build được

1. **Ranh giới kết nối dữ liệu chưa đầy đủ:** các Service trong com.petstation vẫn import com.petstation.database, trong khi package này không xuất hiện trong cây file hiện tại của develop.
2. **DAO interface chưa đồng bộ:** một số Service tham chiếu DAO interface không còn trong cây file; cần xác minh đủ hợp đồng và implementation.
3. **Entry point chưa rõ:** pom.xml hiện là cấu hình Maven giản lược, còn com.petstore.MainApplication chỉ chứa TODO. Cần xác định lớp chạy chính và cách đóng gói JAR.
4. **Dependency kiểm thử:** test hiện dùng JUnit 5 và AssertJ; phải khai báo đúng dependency test trong pom.xml trước khi chạy test/verify.
5. **Cấu hình nhạy cảm:** com.petstore.config.DatabaseConnection chứa cấu hình kết nối trực tiếp trong mã và điều chỉnh các thuộc tính TLS tĩnh. Không sử dụng lớp này làm cấu hình sản xuất; chuyển thông tin kết nối ra ngoài source, bỏ tài khoản/mật khẩu mặc định và không nới lỏng TLS toàn JVM.

Các nhận xét trên dựa trên file và import đã kiểm tra qua GitHub; chưa thực hiện checkout, compile hoặc chạy integration test. Do đó, đây là các vấn đề cần xác minh ưu tiên chứ không phải kết luận từ log build.

## 4. Thứ tự khôi phục đề xuất

1. Chọn một package ứng dụng chính (com.petstation) và một entry point.
2. Khôi phục/đồng bộ database, DAO interface và model cần cho source hiện tại; cập nhật pom.xml theo dependency thực tế.
3. Xác minh schema SQL và seed trên một database thử nghiệm riêng.
4. Thêm Maven Wrapper, hướng dẫn cài đặt và script test; chạy compile/test từ checkout sạch.
5. Sau khi build xanh, lựa chọn tài nguyên, tài liệu nghiệp vụ và CI phù hợp. Chỉ khôi phục màn hình từ vi khi chức năng đó còn thuộc phạm vi của develop.

**Nguyên tắc:** không sao chép toàn bộ vi vào develop một cách máy móc. Khôi phục từng nhóm phụ thuộc theo thứ tự, kiểm tra diff và chạy test sau mỗi nhóm để tránh đưa cả mã cũ lẫn cấu trúc mới vào cùng project mà không tương thích.
