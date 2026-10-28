# Kiến trúc dự án PETSTATION

> **Nhánh:** `develop`  
> **Sản phẩm:** Ứng dụng desktop quản lý cửa hàng thú cưng Pet Station  
> **Mục đích tài liệu:** Cung cấp điểm vào nhanh về cấu trúc source, ranh giới giữa các tầng, luồng nghiệp vụ, công nghệ và các điểm cần kiểm tra khi phát triển.

Tài liệu này là bản tổng quan ở thư mục gốc repository. Xem thêm [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) để đọc mô tả kiến trúc chi tiết đã có trong dự án; [`CONTRIBUTING.md`](CONTRIBUTING.md) để xem quy ước nhánh, commit và code; [`AI_USAGE_LOG.md`](AI_USAGE_LOG.md) để xem nhật ký sử dụng AI.

## 1. Tổng quan kiến trúc

PETSTATION là ứng dụng quản lý cửa hàng thú cưng dạng **desktop**, giao diện bằng Java Swing và lưu dữ liệu trong Microsoft SQL Server. Mã nguồn được quản lý bằng Maven. Ứng dụng chạy trong một JVM; các tầng GUI, Service và DAO là các ranh giới logic trong cùng ứng dụng, **không phải** ba máy chủ hay các microservice độc lập.

Luồng phụ thuộc dự kiến:

```text
Nhân viên
   |
   v
GUI / Swing panels
   |
   v
Service layer
(quyền, xác thực nghiệp vụ, điều phối giao dịch)
   |
   v
DAO interfaces  <---->  DAO implementations
                         (JDBC / SQL có tham số)
   |
   v
Microsoft SQL Server

Model: đối tượng dữ liệu, enum và logic tính toán miền
Util: khởi tạo ứng dụng và tiện ích dùng chung
```

Nguyên tắc chính là giao diện không tự thực hiện truy vấn SQL; quy tắc nghiệp vụ và phân quyền nằm ở Service; thao tác dữ liệu được gom ở DAO/implementation. Các giao dịch quan trọng phải được xử lý nhất quán, đặc biệt là thanh toán, tồn kho, điểm khách hàng và thu cọc.

## 2. Công nghệ chính

| Thành phần | Vai trò |
| --- | --- |
| Java 17 | Ngôn ngữ và phiên bản biên dịch được khai báo trong `pom.xml` |
| Maven | Quản lý thư viện và vòng đời build |
| Java Swing | Xây dựng giao diện desktop |
| FlatLaf 3.4.1 | Thư viện giao diện Swing |
| Microsoft SQL Server JDBC 12.6.1.jre11 | Driver kết nối SQL Server |
| Microsoft SQL Server | Cơ sở dữ liệu quan hệ của ứng dụng |

Các dependency được khai báo tại [`pom.xml`](pom.xml). Không đóng gói mật khẩu hoặc cấu hình máy cá nhân vào repository/JAR; cấu hình thực tế phải được quản lý riêng và cấp quyền tối thiểu cho tài khoản SQL.

## 3. Cấu trúc repository

Cây sau mô tả **những thành phần đang có trong nhánh `develop`** tại thời điểm rà soát. Những mục được đề xuất khôi phục được liệt kê riêng ở cuối phần này, không trộn vào cây hiện trạng.

```text
PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/
├── README.md
├── ARCHITECTURE.md                       # Tổng quan kiến trúc và cây thư mục
├── CONTRIBUTING.md                       # Quy tắc đóng góp
├── AI_USAGE_LOG.md                       # Nhật ký sử dụng AI
├── pom.xml                               # Cấu hình Maven hiện tại
├── .gitignore
├── .classpath
├── .project
├── .settings/                            # Cấu hình Eclipse
├── docs/
│   ├── ARCHITECTURE.md                   # Đối chiếu nhánh và hạng mục cần kiểm tra
│   └── diagrams/
│       ├── so-do-phan-cap-chuc-nang-ver2.png
│       └── so-do-phan-cap-chuc-nang.drawio
└── src/
    ├── main/
    │   └── java/
    │       ├── com/petstation/
    │       │   ├── dao/                  # Interface DAO còn có trong develop
    │       │   │   └── impl/              # Các lớp triển khai DAO
    │       │   ├── model/                # Model, enum, kiểu dữ liệu miền
    │       │   ├── service/              # Nghiệp vụ ứng dụng
    │       │   ├── gui/
    │       │   │   ├── catalog/
    │       │   │   ├── components/
    │       │   │   │   └── common/
    │       │   │   ├── processing/
    │       │   │   │   └── inventory/
    │       │   │   ├── reports/
    │       │   │   ├── search/
    │       │   │   └── system/
    │       │   └── util/
    │       └── com/petstore/              # Package khác còn tồn tại song song
    │           ├── MainApplication.java   # Hiện là khung TODO
    │           └── config/
    │               └── DatabaseConnection.java
    └── test/
        └── java/com/petstation/
            ├── architecture/
            │   └── KienTrucPhanTangTest.java
            └── integration/
                └── TichHopSQLServerTest.java
```

### Những thành phần nên cân nhắc bổ sung lại

Đây là **đề xuất**, không phải các file đang tồn tại trong `develop`.

| Thành phần | Đánh giá | Điều kiện trước khi thêm |
| --- | --- | --- |
| `database/001_schema.sql` và `database/002_sample_data.sql` | Nên ưu tiên | Đối chiếu schema với SQL được gọi bởi các Service/DAO hiện tại; chỉ dùng seed cho demo/test. |
| `src/main/java/com/petstation/database/` | Bắt buộc phải giải quyết | Source hiện tại vẫn tham chiếu package kết nối/transaction này nhưng package không có trong cây file. Khôi phục hoặc triển khai lại theo hợp đồng mà các lớp hiện dùng. |
| Các interface `*DAO` bị thiếu và model được import | Bắt buộc phải giải quyết | So khớp implementation, Service, kiểu dữ liệu và import; không tạo file rỗng chỉ để qua compile. |
| `mvnw`, `mvnw.cmd`, `.mvn/wrapper/` | Nên bổ sung | Chốt phiên bản Maven, sau đó kiểm tra build từ checkout sạch. |
| `SETUP.md` và các script cài/kiểm tra SQL Server | Nên bổ sung | Cập nhật theo cấu hình và lệnh thực tế của develop; không sao chép hướng dẫn cũ mà chưa thử. |
| Tài liệu kiểm thử, nguồn sản phẩm, prompt AI | Có thể bổ sung | Xem lại liên kết và loại bỏ chỉ dẫn trỏ đến file hoặc chức năng không còn tồn tại. |
| Ảnh/logo và file cấu hình mẫu | Có thể bổ sung | Chỉ phục hồi tài nguyên đang được source dùng; tuyệt đối không thêm mật khẩu hoặc cấu hình máy cá nhân. |
| GitHub Actions, PR template, pre-commit | Có thể bổ sung sau | Điều kiện kiểm tra phải chạy đúng trên source hiện tại và không bỏ qua lỗi build/test. |
| Các màn hình nghiệp vụ chỉ còn ở `vi` | Chưa phục hồi tự động | Xác nhận còn thuộc phạm vi sản phẩm rồi mới đưa mã, dependency và test tương thích vào develop. |

**Lưu ý:** `com/petstore` và `com/petstation` hiện song song. Cần chốt một điểm chạy chính; `MainApplication.java` hiện chỉ là khung TODO. Đối chiếu chi tiết các khoảng thiếu và thứ tự phục hồi tại [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## 4. Trách nhiệm từng tầng

### GUI — `com.petstation.gui`

- Nhận thao tác người dùng, thu thập/hiển thị dữ liệu và điều hướng giữa các màn.
- Hiển thị trạng thái thành công, lỗi hoặc xác nhận nghiệp vụ.
- Không nên giữ câu SQL, tự quản lý transaction hoặc là nơi duy nhất kiểm tra quyền.
- Các thành phần dùng chung nên đặt trong `components` để tránh sao chép bảng, toolbar và form ở nhiều màn hình.

### Service — `com.petstation.service`

- Chứa quy tắc nghiệp vụ, validation, kiểm tra quyền và phối hợp các thao tác dữ liệu.
- Là nơi điều phối các bước phải thành công hoặc thất bại cùng nhau.
- Kiểm tra quyền ở tầng này vẫn cần thiết dù GUI đã ẩn nút hoặc mục menu.
- Ví dụ nghiệp vụ có thể liên quan đến bán hàng, kho, đổi trả, tích điểm, Spa, lưu trú/Hotel, lịch làm việc, tài khoản và báo cáo.

### DAO interface và implementation — `com.petstation.dao`

- Interface `*DAO` định nghĩa thao tác dữ liệu mà Service cần.
- Các lớp trong `dao/impl` triển khai hợp đồng, thực hiện câu lệnh JDBC và ánh xạ kết quả.
- Dữ liệu đầu vào phải được truyền bằng tham số SQL; không ghép trực tiếp dữ liệu người dùng vào câu truy vấn.
- Nếu dùng tên bảng/cột động, phải giới hạn chúng bằng danh sách cho phép; placeholder SQL không thay thế được tên bảng/cột.

### Model — `com.petstation.model`

- Đại diện dữ liệu nghiệp vụ, enum, trạng thái và các quy tắc tính toán thuần.
- Không nên phụ thuộc GUI, Service hoặc JDBC. Logic tính tiền/tồn/điểm cần có thể kiểm thử mà không mở màn hình.
- Khi thay đổi một trường, hãy kiểm tra các màn đang dùng nó và các kết quả DAO được ánh xạ theo vị trí.

### Khởi tạo và hạ tầng

- `com.petstation.util` chứa các tiện ích/điểm khởi tạo dùng chung được tổ chức trong source.
- Các lớp kết nối SQL, cấu hình và transaction phải có một vị trí rõ ràng, thống nhất. Tránh tạo một cách kết nối mới bên trong từng panel.
- Đường dẫn file cấu hình cục bộ, thông tin đăng nhập và dữ liệu nhạy cảm không được đưa lên Git.

## 5. Các luồng nghiệp vụ quan trọng

### 5.1 Thanh toán bán hàng

```text
GUI / giỏ hàng
   -> kiểm tra quyền, ca làm việc và dữ liệu đơn
   -> Service mở transaction
   -> DAO ghi hóa đơn và khoản thu
   -> cập nhật tồn kho / điểm / voucher theo quy tắc áp dụng
   -> commit
   -> trả biên lai cho GUI

Nếu có lỗi giữa chừng: rollback thay vì giữ lại một phần giao dịch.
```

Cần chặn thanh toán trùng khi người dùng bấm nhiều lần hoặc yêu cầu bị gửi lại. Giỏ hàng chỉ nên được kết thúc sau khi Service xác nhận thanh toán thành công.

### 5.2 Cập nhật danh mục

```text
GUI -> Service (quyền + validation) -> DAO -> SQL Server
```

Khi bản ghi đã được tham chiếu bởi hóa đơn hoặc lịch sử, phải tuân thủ quy tắc xóa/ngừng hoạt động của nghiệp vụ; không xóa dây chuyền chứng từ lịch sử để làm mất liên kết dữ liệu.

### 5.3 Thu cọc và quyết toán Hotel

Các lần thu cọc cần được ghi nhận tách bạch, gắn hợp đồng và chứng từ tương ứng. Service phải kiểm tra số tiền còn thiếu, trạng thái hợp đồng và các điều kiện nhận phòng trong cùng cơ chế giao dịch phù hợp. Quyết toán phải cấn trừ đúng số đã thu và không trừ lặp.

## 6. Quy tắc bảo mật và toàn vẹn dữ liệu

1. **Phân quyền:** kiểm tra ở Service; GUI chỉ hỗ trợ hiển thị chức năng phù hợp vai trò, không phải ranh giới bảo mật duy nhất.
2. **SQL injection:** sử dụng PreparedStatement/tham số truy vấn cho dữ liệu người dùng; kiểm soát danh sách cho phép nếu có định danh SQL động.
3. **Mật khẩu:** dùng hàm băm thích hợp như BCrypt; không lưu mật khẩu dạng rõ trong database hoặc log.
4. **Transaction:** nhóm các thao tác phụ thuộc nhau vào cùng transaction; rollback khi phát sinh lỗi.
5. **Dữ liệu lịch sử:** bảo toàn quan hệ hóa đơn, tồn kho, điểm và chứng từ; tránh hard-delete không đúng quy tắc.
6. **Cấu hình và log:** không commit secrets; log không nên chứa mật khẩu hoặc thông tin nhạy cảm không cần thiết.
7. **Tải nền:** tác vụ đọc dữ liệu lớn, ảnh, báo cáo và xuất file không nên làm treo Event Dispatch Thread của Swing.

## 7. Kiểm thử và cách làm việc

- Source có kiểm thử kiến trúc để phát hiện phụ thuộc package sai tầng và kiểm thử tích hợp SQL Server. Kiểm thử tích hợp cần có môi trường/database kiểm thử riêng, không dùng nhầm dữ liệu thật.
- Trước khi mở Pull Request, kiểm tra diff, build/test bằng cấu hình Maven của repository và thử các luồng nghiệp vụ đã sửa.
- Không tuyên bố build/test thành công chỉ dựa trên việc đọc source hoặc kiểm tra giao diện tĩnh; cần ghi nhận kết quả chạy lệnh thực tế.
- Quy tắc làm việc nhánh/commit xem tại [`CONTRIBUTING.md`](CONTRIBUTING.md). Nhánh `develop` là nhánh tích hợp theo tài liệu đóng góp; không commit trực tiếp lên `main` theo quy ước hiện tại.

## 8. Những điểm cần xác minh khi bảo trì

Cấu trúc tài liệu và mã nguồn cần được giữ đồng bộ. Khi đọc nhánh `develop` để lập file này, một số class/package mà source hoặc tài liệu kiến trúc tham chiếu không được tìm thấy tại vị trí theo quy ước package, ví dụ `com.petstation.database` và một số file interface/model được import từ các lớp hiện có. Điều này **không đủ để kết luận build chắc chắn lỗi**, vì file có thể được đặt ở vị trí khác nhưng vẫn khai báo package Java tương ứng; tuy nhiên cần kiểm tra cây file thực tế và chạy build/test tại checkout mới trước khi dựa vào sơ đồ để refactor.

Các việc nên kiểm tra trong lần rà soát source tiếp theo:

- Đối chiếu vị trí file với dòng `package` và các `import`; thống nhất vị trí các lớp kết nối/transaction.
- Đảm bảo mọi `*DAOImpl` đều có interface `*DAO` tương ứng và khai báo `implements` đúng.
- Kiểm tra dependency test trong `pom.xml` so với thư viện mà test đang import.
- Xác minh đường dẫn database/schema/sample data và hướng dẫn cài đặt từ một checkout sạch.
- Cập nhật cả tài liệu này và [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) khi thay đổi kiến trúc cấp cao.

---

**Quy tắc cập nhật tài liệu:** giữ file này ở mức kiến trúc cấp cao; nếu cần giải thích lớp mới, luồng chi tiết hoặc quyết định thiết kế, bổ sung ở `docs/ARCHITECTURE.md` và liên kết từ đây.
