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
| Java 17 | Mức tương thích bytecode và ngôn ngữ trong Maven |
| Maven 3.9.11 Wrapper | Quản lý dependency, build và chạy nhất quán giữa máy |
| Java Swing + FlatLaf 3.5.4 | Giao diện desktop |
| Microsoft SQL Server JDBC 12.8.1.jre11 | Kết nối SQL Server |
| Apache POI 5.4.1 | Đọc/ghi Excel |
| ZXing 3.5.3 | Hỗ trợ mã vạch/QR ở các luồng có sử dụng |
| jBCrypt 0.4 | Hỗ trợ băm mật khẩu |
| SLF4J + Logback | Logging |
| JUnit 5, AssertJ, Mockito | Thư viện kiểm thử |

Phiên bản dependency được khai báo trong [`pom.xml`](pom.xml). Cấu hình kết nối thật đặt ngoài source, mặc định hướng dẫn dùng `config/db.properties`; không commit mật khẩu, token hoặc cấu hình riêng của máy.

## 3. Cấu trúc repository

Cây dưới đây phản ánh các nhóm thư mục đã xác nhận trên nhánh `develop` sau khi phục hồi lớp nền. Không liệt kê mọi file Java để tránh làm tài liệu nhanh lỗi thời.

```text
PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/
├── .github/
│   ├── pull_request_template.md
│   └── workflows/verify.yml
├── .mvn/wrapper/                     # Cấu hình Maven Wrapper
├── .vscode/                          # Cấu hình chạy/debug VS Code
├── README.md
├── SETUP.md                          # Hướng dẫn thiết lập và chạy
├── ARCHITECTURE.md                   # Tổng quan kiến trúc
├── CONTRIBUTING.md                   # Quy tắc đóng góp
├── AI_USAGE_LOG.md                   # Nhật ký sử dụng AI
├── pom.xml
├── mvnw / mvnw.cmd                   # Maven Wrapper
├── database/
│   ├── 001_schema.sql                # Schema database
│   └── 002_sample_data.sql           # Dữ liệu demo/test
├── scripts/
│   ├── check-environment.ps1
│   ├── setup-database.ps1
│   ├── test-sqlserver.ps1
│   ├── format-java.ps1
│   └── run-java.ps1
├── docs/
│   ├── ARCHITECTURE.md               # Ghi chú khôi phục và trạng thái xác minh
│   ├── KIEM_THU_VA_YEU_CAU.md
│   ├── NGUON_SAN_PHAM.md
│   ├── AI_PROMPTS.md
│   └── diagrams/
└── src/
    ├── main/
    │   ├── java/com/petstation/
    │   │   ├── App.java              # Điểm khởi chạy ứng dụng
    │   │   ├── database/             # Cấu hình, JDBC và transaction
    │   │   ├── dao/
    │   │   │   └── impl/             # Triển khai DAO (*DAOImpl)
    │   │   ├── model/                # Model và enum miền
    │   │   ├── service/              # Nghiệp vụ, xác thực và phân quyền
    │   │   ├── gui/
    │   │   │   ├── catalog/
    │   │   │   ├── components/common/
    │   │   │   ├── dashboard/
    │   │   │   ├── main/
    │   │   │   ├── navigation/
    │   │   │   ├── processing/
    │   │   │   │   ├── approval/
    │   │   │   │   ├── hotel/
    │   │   │   │   ├── inventory/
    │   │   │   │   ├── sales/
    │   │   │   │   ├── spa/
    │   │   │   │   └── staff/
    │   │   │   ├── reports/
    │   │   │   ├── search/
    │   │   │   └── system/
    │   │   └── util/
    │   └── resources/
    │       ├── db.properties.example  # Mẫu cấu hình, không phải credentials thật
    │       ├── images/
    │       │   ├── pet-station-logo.png
    │       │   └── products/
    │       └── logback.xml
    └── test/java/com/petstation/
        ├── architecture/KienTrucPhanTangTest.java
        └── integration/TichHopSQLServerTest.java
```

### Trạng thái hiện tại và phần được giữ lại có chủ đích

- Entry point chính là `com.petstation.App`; ứng dụng khởi tạo cấu hình, xác minh SQL Server rồi mở `DangNhapFrame`. Sau đăng nhập, giao diện chính dùng `ChinhFrame`.
- Package `com.petstation.database`, DAO interface, model, service, GUI và tài nguyên dùng bởi source hiện đã có trong `develop`.
- Các implementation theo quy ước `*DAOImpl`; không giữ song song các file `*Impl` cũ.
- Package `com.petstore` cũ đã bỏ khỏi bản khôi phục vì có entry point TODO và cấu hình SQL hard-code, không phải điểm chạy được của Pet Station.
- Cấu hình kết nối mẫu nằm trong repository; cấu hình thật phải đặt cục bộ ngoài Git. Các file `target/classes` đã commit trước đây cũng đã được loại bỏ.

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

## 8. Trạng thái xác minh và giới hạn còn lại

- Pull Request [#43](https://github.com/htnhang132/PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/pull/43) đã được merge vào `develop`.
- GitHub Actions workflow [Verify Java](https://github.com/htnhang132/PTUD_Nhom03_QuanLyCuaHangThuCung_PETSTATION/actions/runs/40689136445) đã thành công trên Windows và Linux với JDK 17 và 21. Quy tắc format, build và các test không cần SQL Server đều pass.
- CI không bật kiểm thử tích hợp SQL Server và không mở cửa sổ Swing tương tác. Do đó, không dùng kết quả CI để khẳng định database trên máy người dùng đã kết nối hoặc mọi màn hình đã được thao tác thủ công.
- Để mở ứng dụng tại máy Windows, làm theo [`SETUP.md`](SETUP.md): dùng JDK 17+, SQL Server 2019+, chạy script thiết lập database, tạo `config/db.properties` từ file mẫu và điền SQL login của máy đó. Sau đó chạy `./mvnw.cmd -B -ntp verify`, `./mvnw.cmd -B -ntp package` và `java -jar target/petstation.jar`.
- `database/002_sample_data.sql` dành cho demo/test và có thể thay toàn bộ dữ liệu trong database đích. Không chạy seed/reset trên database thật; sao lưu trước khi reset.
- Nếu mở ứng dụng gặp lỗi kết nối, xác minh dịch vụ SQL Server, TCP/IP/cổng, tên database, SQL login và quyền user. Không sửa source để nhúng mật khẩu vào code.

Các kiểm tra tiếp theo cần thực hiện trên máy có SQL Server: mở app, đăng nhập, điều hướng các màn hình chính và chạy `scripts/test-sqlserver.ps1` trên database `PetStationIntegrationTest` riêng. Ghi nhận kết quả thật trước khi tuyên bố đã xác minh mọi luồng nghiệp vụ.

