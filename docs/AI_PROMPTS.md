# Prompt người dùng — 26/10/2026

Nguồn: yêu cầu trực tiếp trong hội thoại này; chuẩn hóa bullet/khoảng trắng từ HTML, giữ nội dung yêu cầu. Không chứa prompt hoặc kết quả của các phiên trước.

> tôi muốn clean code và cấu trúc dự án lại theo yêu cầu sau

Yêu cầu chung của đề tài:

- Khảo sát, phân tích, thiết kế hệ thống phần mềm quản lý XXX (chỉ dành cho nhân viên).
- Về phần thực hiện chương trình ứng dụng theo phân tích, thiết kế:
  - Tối thiểu chương trình bao gồm các chức năng chính: Thêm, xóa, cập nhật, liệt kê (dạng danh sách và chi tiết), tìm kiếm (đơn giản, nâng cao) dữ liệu của các bảng (lưu ý cập nhật, xóa dữ liệu của các bảng có quan hệ).
  - Giao diện thân thiện, sử dụng các phím tắt, tab khi cần thiết.
  - Phần viết code cần phải dùng Coding Convention chung cho các ngôn ngữ (Java/C#).
  - Phải viết theo HĐT và theo mô hình cụ thể rõ ràng (n Tier ….).
- Quy định về sử dụng AI:
  - Mọi đoạn mã sử dụng trong đồ án phải được sinh viên hiểu rõ và giải thích được nguyên lý hoạt động.
  - Sinh viên phải nộp nhật ký sử dụng AI (AI Usage Log), ghi nhận các prompt, kết quả nhận được và các chỉnh sửa đã thực hiện.
  - Ví dụ: Không chỉ nộp source code, sinh viên nộp thêm: Prompt đã sử dụng; Kết quả AI trả về; Phần nào được giữ nguyên; Phần nào chỉnh sửa; Lý do chỉnh sửa.
  - Trong buổi bảo vệ, giảng viên có quyền yêu cầu sinh viên giải thích bất kỳ đoạn mã nào hoặc chỉnh sửa trực tiếp chương trình. Nếu sinh viên không giải thích hoặc không thực hiện được các thay đổi cơ bản trên phần mã của mình thì phần mã đó sẽ không được tính điểm.
- Về phần làm việc nhóm:
  - Phải có link GitHub.
  - Không commit trực tiếp lên main.
  - Mỗi người làm trên một branch feature/....
  - Merge vào develop sau khi hoàn thành và đã kiểm tra.
  - Khi hoàn tất một giai đoạn hoặc trước khi nộp, merge develop vào main.
  - Viết commit message rõ ràng, tránh các commit chung chung như update, fix bug, 123.

Chuyển dịch trọng tâm từ “Đánh giá sản phẩm” (Product-based) sang “Đánh giá quá trình và tư duy” (Process & Understanding-based).

- System Architecture & Design Pattern: Lý do chọn kiến trúc này? Ranh giới các module ra sao?
- Unit Test & Test Cases: Sinh viên phải tự viết các kịch bản kiểm thử (kể cả biên và ngoại lệ) để chứng minh code chạy đúng.
- Security & Performance Optimization: Đặt câu hỏi về các lỗ hổng bảo mật hoặc điểm nghẽn hiệu năng mà AI thường bỏ qua.

## Yêu cầu giản lược dự án

Yêu cầu tiếp theo trong cùng ngày, giữ nguyên nội dung (chỉ chuẩn hóa xuống dòng):

> hãy xem toàn bộ project và code lại theo cách code của sinh viên dễ hiểu, không AI quá
>
> đọc từng thư mục, file trong đó để xem những phần nào có thể xóa hay tóm gọn lại, nhất là các file md ko còn dùng đến nữa
>
> trong dao thì các file có cấu trúc ....DAO, còn trong impl thì sửa lại theo cấu trúc ....Impl bỏ jdbc đi
>
> cấu trúc hệ thống vậy đã đúng với hệ thống chưa, cần cấu trúc lại như nào để thầy ko thể bắt bẻ hơn được ko
>
> file trong database thì chỉ còn 2 file chứa các bảng và file chứ dữ liệu mẫu, chèn dữ liệu mẫu thì code lại từng bảng chèn 1 lần insert thui, ko phải 1 dữ liệu 1 lần insert làm rối mắt,
>
> còn file AI USAge LOG thì sửa nội dung lại giống sinh viên làm, rõ từng phần này kia, cách đang làm là giống AI tự làm như nào rồi chèn vào quá
>
> bỏ các dữ liệu cũ trước khi chèn dữ liệu database mới vào, do dữ liệu các mã nó không khớp
>
> xem và chỉnh sửa code lại để khi người khác lấy code về và có hướng dẫn để chạy trên máy họ được. thường bị lỗi khi chạy bằng visual rồi đem qua intelliji hay eclipse thì nó lỗi, với do ko trùng phiên bản java hay sql server nữa

Làm rõ phạm vi dữ liệu: người dùng chọn **“Sao lưu rồi reset PetStationDemo2 trên máy này bằng bộ dữ liệu mẫu mới”**. Không có yêu cầu che giấu việc dùng AI hoặc giả xác nhận review của thành viên; nhật ký vẫn phân biệt phần hỗ trợ với phần sinh viên tự thực hiện.
