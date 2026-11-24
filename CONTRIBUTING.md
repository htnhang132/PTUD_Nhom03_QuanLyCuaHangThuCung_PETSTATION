# Đóng góp và quy tắc làm việc

Pet Station dùng Java 17, Swing và SQL Server. Quy định code chính thức nằm tại [Chuẩn code](docs/CHUAN_CODE.md); kết luận từng file tại [Rà soát mã nguồn](docs/RA_SOAT_MA_NGUON.md). Không dùng quy ước công nghệ khác thay cho cấu hình Maven hiện có.

## Nhánh và commit

- Làm việc ở `feature/<ten-chuc-nang>`, tạo PR vào `develop`; tích hợp `main` từ `develop` theo workflow hiện tại.
- Dùng tiền tố `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:` với nội dung mô tả thay đổi cụ thể. Không commit mật khẩu hoặc kết quả build.
- Không đảo ngược thay đổi đang có của thành viên khác khi dọn code. Không gộp nghiệp vụ khác nhau chỉ để giảm số file.

## Trước khi gửi PR

1. Kiểm tra nơi sử dụng trước khi xóa/đổi tên; cập nhật test và tài liệu tương ứng.
2. Chạy `powershell -File scripts/format-java.ps1 -Mode check` và `mvnw.cmd -B -ntp verify`.
3. Khi sửa SQL/nghiệp vụ, chạy `scripts/setup-database.ps1 -Database PetStationIntegrationTest_Review -RunTests` trên database riêng. Không reset dữ liệu cửa hàng để kiểm thử.
4. Ghi số test chạy/bỏ qua, kiểm tra UI liên quan và rủi ro còn lại. Đối chiếu [bản đồ truy vết](docs/BAN_DO_TRUY_VET_CODE.md), không chép tên lớp cũ vào phần giải thích.

Giới hạn phương thức 80 dòng và quy ước class thay record là lựa chọn của nhóm, không phải định nghĩa phổ quát về Java/OOP. Những điểm chưa đạt thiết kế tốt được ghi nhận trong báo cáo, không che bằng việc chỉ sửa format.
