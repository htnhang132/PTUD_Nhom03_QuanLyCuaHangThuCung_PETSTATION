# Cấu trúc Pet Station

Ứng dụng dành cho nhân viên, chạy Swing trong một JVM và kết nối SQL Server. Dự án dùng **ba tầng logic: giao diện – nghiệp vụ – dữ liệu**. Không gọi các package cùng JVM là ba máy chủ độc lập.

```text
src/main/java/com/petstation/
  App.java                  Điểm chạy
  database/                 Cấu hình, kết nối, câu lệnh JDBC dùng chung, transaction
  model/                    Dữ liệu, enum, giỏ hàng và quy tắc tính toán
  dao/                      Các interface ...DAO
    impl/                   Các lớp ...Impl implements ...DAO, chứa SQL
  service/                  Kiểm tra quyền, nghiệp vụ, phối hợp transaction
  gui/
    main/                   Cửa sổ chính
    navigation/             Menu, đăng ký và chuyển màn hình
    catalog/                Danh mục
    search/                 Tra cứu
    processing/             Bán hàng, kho, Spa, Hotel, lịch, phê duyệt
    reports/                Thống kê
    system/                 Đăng nhập, tài khoản, cài đặt, nhật ký
    dashboard/              Tổng quan
    components/             Form, bảng, toolbar, lịch dùng chung
  util/                     Khởi tạo và tiện ích nhỏ
src/main/resources/         Ảnh mẫu, cấu hình mẫu và logging
src/test/java/              Unit test và kiểm thử SQL
database/                   Chỉ hai file schema và dữ liệu mẫu
scripts/                    Cài database, kiểm tra môi trường, chạy và test
```

Chọn cấu trúc này vì nghiệp vụ bán hàng cần lưu hóa đơn, tồn và điểm nhất quán; Swing/JDBC phù hợp đồ án một cửa hàng. Maven quản lý dependency và build để đổi IDE không đổi mã. Chưa cần web API, microservices, framework DI hay nhiều module Maven.

## Ranh giới các lớp

| Nơi sửa | Trách nhiệm | Ví dụ |
| --- | --- | --- |
| GUI | Nhập, chọn bản ghi, hiển thị và điều hướng | `ThanhToanPanel`, `DanhMucKhachHangPanel` |
| Service | Quyền, validation, trạng thái nghiệp vụ và transaction | `BanHangService`, `LuuTruService` |
| DAO interface | Hợp đồng thao tác dữ liệu | `BanHangDAO`, `LuuTruDAO` |
| Impl | SQL có tham số, đọc/ghi/ánh xạ | `BanHangImpl`, `LuuTruImpl` |
| Database | Mở kết nối, chia sẻ transaction, rollback/savepoint | `KetNoiCSDL`, `ThaoTacSQL` |
| Model | Dữ liệu và tính toán không gọi GUI/JDBC | `DongThuTien`, `KhoangNgay`, `GioHang` |

Service giữ DAO bằng kiểu interface và tạo implementation cụ thể khi khởi tạo. Các service cũ vẫn có hàm static để phù hợp luồng gọi Swing hiện tại; không cần viết thêm container/factory cho từng lớp. Model không phụ thuộc service. `GioHang.ChinhSach` là cổng nhỏ để thay việc lấy khuyến mãi/voucher/thanh toán khi test; bản thật được ghép ở `GioHangService.tao()`.

`QuanLyDanhMucPanel` và `BangNghiepVuPanel` là lớp nền dùng chung vòng đời bảng/form, tránh chép lại cùng chức năng trên các màn. Panel con khai báo dữ liệu và hành động. Listener báo thay đổi giá/lịch/phê duyệt là cách thông báo màn liên quan tải lại; phải tháo khi hết phiên.

Đó là các ví dụ thực tế của DAO, Service Layer, Strategy/composition và Template Method. Khi bảo vệ cần chỉ được đoạn code sử dụng và lý do, không chỉ đọc tên pattern.

## Ba luồng cần giải thích

**Thanh toán:** GUI lấy các dòng thu tiền → service kiểm tra quyền/ca/phân bổ/trạng thái → mở transaction → DAO ghi hóa đơn, khoản thu, trừ tồn và ghi điểm → commit → hiển thị biên lai. Một bước lỗi thì rollback toàn bộ; khóa chống lặp ngăn bấm hai lần. Giỏ hàng chỉ kết thúc ngữ cảnh sau khi thanh toán trả thành công.

**Lưu danh mục:** GUI kiểm tra cách nhập và truyền dữ liệu → service kiểm tra quyền/quy tắc → DAO cập nhật. Với dữ liệu đã có quan hệ, phải chặn xóa hoặc chuyển trạng thái theo nghiệp vụ. Không xóa dây chuyền hóa đơn chỉ vì xóa khách hàng.

**Thu cọc Hotel:** giữ phòng không bắt buộc thu đủ ngay; mỗi lần thu có ca/người nhận/phương thức. Service khóa hợp đồng, kiểm tra mức còn thiếu rồi ghi trong cùng transaction. Nhận phòng chỉ khi đủ mức yêu cầu. Quyết toán cấn trừ tiền thực thu đúng một lần.

## Database và mã mẫu

`001_schema.sql` là schema cuối cùng, không chứa chuỗi migration cũ. `002_sample_data.sql` thay toàn bộ dữ liệu mẫu, dùng ID tường minh để các khóa ngoại khớp nhau. Mỗi bảng có dữ liệu chỉ có một câu INSERT nhiều dòng. Không còn bảng đối chiếu mã/Unicode hay công cụ vá dữ liệu cũ trong luồng cài đặt.

Phiếu `PN-DAU-KY` và dòng lịch sử điểm đầu kỳ biểu diễn số dư mẫu ban đầu. Chúng giúp tổng hợp nhập–bán–xuất–đổi khớp tồn, tổng lịch sử điểm khớp số dư khách. Không phải tiền hoặc giao dịch thực tế của cửa hàng.

## Bảo mật, hiệu năng và giới hạn

Service kiểm tra quyền dù GUI đã ẩn nút. DAO dùng tham số cho dữ liệu nhập; tên bảng/cột động phải thuộc danh sách cho phép. BCrypt dùng cho mật khẩu; file kết nối thật không nằm trong Git/JAR. Ảnh được kiểm tra định dạng/dung lượng và sao chép vào vùng dữ liệu của ứng dụng. Tài khoản SQL tại máy nhân viên vẫn cần giới hạn quyền và bảo vệ cục bộ.

Các danh sách chính, báo cáo, ảnh và xuất Excel có tải nền. Một số form/màn cũ còn đọc đồng bộ; cần đo khi tăng dữ liệu. Nhiều danh sách còn tải toàn bộ rồi lọc tại Swing, chưa phải phân trang SQL. Không coi kiểm thử chức năng là kết quả đo tải.

API bảng còn `Object[]`; vì vậy khi sửa cột cần cập nhật cả DAO và panel/test tương ứng. Một số panel nền vẫn lớn do dùng chung nhiều danh mục. Đây là hạn chế phải hiểu, không nên tuyên bố đã đạt SOLID hoàn toàn hoặc không còn điểm nào để góp ý. Bộ test ranh giới package hỗ trợ phát hiện import sai tầng, không thay review nghiệp vụ.
