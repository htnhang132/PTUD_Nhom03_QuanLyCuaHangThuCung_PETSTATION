# Cài đặt và chạy Pet Station

## 1. Phiên bản cần dùng

| Thành phần | Yêu cầu |
| --- | --- |
| Java | JDK 17 trở lên; nên thống nhất JDK 17 trong nhóm |
| Maven | Wrapper có sẵn trong repo, cố định 3.9.11 |
| Database | SQL Server 2019 trở lên, Express hoặc Developer đều được |
| Công cụ SQL | SSMS để chạy thủ công, hoặc `sqlcmd` để dùng script |
| IDE | VS Code có Extension Pack for Java; IntelliJ hỗ trợ Maven; Eclipse có m2e |

Mã được biên dịch với `release=17`, không bật preview. Maven kiểm tra phiên bản Java trước khi build. Driver SQL Server được Maven tải theo `pom.xml`; không thêm driver thủ công vào IDE. Microsoft ghi nhận driver 12.8 hỗ trợ JDK 17/21 trong [release notes](https://learn.microsoft.com/en-us/sql/connect/jdbc/release-notes-for-the-jdbc-driver?view=sql-server-ver17).

Đợt kiểm tra hiện tại chạy trên Windows, JDK 17 và SQL Server 2022. CI được cấu hình kiểm tra Windows/Linux với JDK 17/21; cần xem kết quả trên GitHub sau khi push, không coi cấu hình CI là bằng chứng mọi môi trường đã chạy thành công.

## 2. Kiểm tra Java và Maven

Clone toàn bộ repository và mở thư mục chứa `pom.xml`. Trong PowerShell:

```powershell
java -version
./mvnw.cmd -v
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/check-environment.ps1
```

`JAVA_HOME` phải trỏ đến thư mục **JDK**, không phải thư mục `bin`. Nếu sửa biến môi trường, mở terminal/IDE mới. JDK của Maven trong IDE phải cùng phiên bản với project SDK. Lần đầu Wrapper cần mạng để tải Maven và dependency. Với proxy của trường, cấu hình Maven trong `~/.m2/settings.xml`, không ghi mật khẩu proxy vào repository.

Linux/macOS dùng `bash ./mvnw` thay cho `./mvnw.cmd`; các script `.ps1` hỗ trợ Windows. Trên máy khác, có thể chạy hai SQL thủ công trong công cụ quản trị tương ứng và dùng Maven Wrapper trực tiếp.

## 3. Chuẩn bị SQL Server

Chạy dịch vụ SQL Server. Trong SQL Server Configuration Manager, bật TCP/IP, đặt hoặc kiểm tra cổng TCP, rồi khởi động lại dịch vụ nếu đổi cấu hình. Ví dụ này dùng `localhost,1433` cho sqlcmd và `localhost:1433` trong JDBC. Không mặc định mọi SQL Express đều dùng cổng 1433; named instance có thể dùng cổng động.

Phiên bản SSMS không phải phiên bản máy chủ. Kiểm tra bằng:

```sql
SELECT SERVERPROPERTY('ProductVersion'), SERVERPROPERTY('Edition');
```

### Cách A — Script Windows

Với Windows Authentication có quyền tạo database:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/setup-database.ps1 -Server 'localhost,1433'
```

Với SQL Authentication, dùng login do người quản trị máy cung cấp. Mật khẩu truyền qua biến môi trường, không đưa vào tham số lệnh:

```powershell
$env:SQLCMDPASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'SQL password' -AsSecureString)).Password
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/setup-database.ps1 -Server 'localhost,1433' -SqlUser 'your_sql_login'
Remove-Item Env:SQLCMDPASSWORD
```

Script tạo `PetStationDemo2` nếu chưa có, rồi chạy hai file SQL. Nếu database đã đúng bản hiện tại, lệnh không nạp lại mẫu. Nếu database cũ, script dừng và yêu cầu xem hướng dẫn reset. Script không tạo SQL login hoặc tự đổi mật khẩu SQL Server.

### Cách B — SSMS

Tạo một database trống, nên dùng collation `Vietnamese_CI_AS`. Chọn đúng database trong SSMS, chạy lần lượt:

1. `database/001_schema.sql`: 31 bảng, khóa chính/ngoại, CHECK và chỉ mục.
2. `database/002_sample_data.sql`: dữ liệu mẫu, mỗi bảng có dữ liệu dùng một `INSERT ... VALUES` nhiều dòng.

File schema yêu cầu database trống. File mẫu **không xóa dữ liệu đang có**. Script yêu cầu các bảng dbo trống, kiểm tra điều kiện trước khi chèn và nạp dữ liệu trong một transaction; chỉ chạy trên database demo/test mới. Các câu INSERT dùng ID mẫu và được sắp theo thứ tự đáp ứng khóa ngoại; script không tắt kiểm tra CHECK/FK. Ngày nghiệp vụ mẫu tính tương đối theo ngày nạp; ngày sinh và ngày thu thập nguồn được giữ cố định. `PN-DAU-KY` và lịch sử điểm đầu kỳ giải thích số dư có sẵn của bộ mẫu.

## 4. Cấu hình ứng dụng

```powershell
New-Item -ItemType Directory -Force config | Out-Null
Copy-Item src/main/resources/db.properties.example config/db.properties
```

Sửa `config/db.properties`:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=PetStationDemo2;encrypt=true;trustServerCertificate=true
db.user=your_sql_login
db.password=your_sql_password
db.loginTimeoutSeconds=10
```

Ứng dụng dùng SQL Authentication. Login cần có user trong database và quyền đọc/ghi các bảng ứng dụng; script setup cần quyền cao hơn để tạo bảng/backup/reset. Nếu dùng login đã có trên máy khác, tạo ánh xạ user và cấp quyền trong SSMS. Không cần tên login/mật khẩu giống máy người viết code.

`trustServerCertificate=true` dùng cho môi trường học/demo có chứng chỉ tự ký. Khi vận hành cần chứng chỉ tin cậy và xác minh máy chủ. Không đưa file cấu hình thật lên Git hoặc gửi kèm source/JAR.

Ưu tiên biến môi trường `PETSTATION_DB_URL`, `PETSTATION_DB_USER`, `PETSTATION_DB_PASSWORD` hơn tệp. `PETSTATION_DB_CONFIG` chỉ định tệp riêng; đường dẫn tuyệt đối giúp tránh nhầm working directory. Mã vẫn đọc `src/main/resources/db.properties` cục bộ nếu chưa có `config/db.properties`, để tương thích máy phát triển cũ. Tệp được chỉ định mà không tồn tại sẽ báo lỗi.

## 5. Build và chạy

```powershell
./mvnw.cmd -B -ntp verify
java -jar target/petstation.jar
```

Hoặc `./mvnw.cmd compile exec:java '-Dexec.mainClass=com.petstation.App'`. Không dùng lệnh mặc định `javac App.java && java App`: lệnh đó thiếu classpath/resource Maven và có thể dùng mã hóa Windows thay cho UTF-8. Trong VS Code, cấu hình Code Runner của project đã chuyển nút **Run Code** cho Java sang `scripts/run-java.ps1`; script luôn chạy Maven Wrapper từ thư mục gốc. Có thể dùng **F5 → Pet Station** qua Java extension. Nếu vẫn thấy lệnh `javac App.java`, hãy mở đúng thư mục chứa `pom.xml` rồi chạy **Developer: Reload Window** để nạp lại settings.

Đặt working directory là thư mục gốc dự án. Nếu chỉ mang JAR sang máy khác, mang theo cấu hình ngoài, ảnh người dùng thêm và dữ liệu chăm sóc; ảnh 60 sản phẩm mẫu đã nằm trong JAR.

## 6. Mở bằng IDE

| IDE | Cách mở và cấu hình |
| --- | --- |
| VS Code | Open Folder tại thư mục có pom; cài Extension Pack for Java; chọn JDK 17; đợi Java/Maven tải xong; chạy cấu hình `Pet Station` hoặc lệnh Wrapper |
| IntelliJ IDEA | Open `pom.xml` như Maven Project; chọn Project SDK 17, Maven Importer/Runner JDK 17 và Maven Wrapper; Reload Maven; chạy `com.petstation.App`, working directory là project root |
| Eclipse | File → Import → Maven → Existing Maven Projects; chọn project root; Preferences → Java → Installed JREs chọn JDK 17; Maven → Update Project; Run As → Java Application cho `App` |

Không chép `.idea`, `.classpath`, `.project` hoặc `.settings` từ máy khác; IDE tự tạo từ Maven. IntelliJ cũng khuyến nghị [import cấu hình build tool](https://www.jetbrains.com/help/idea/import-project-or-module-wizard.html). Khi đổi nhánh/đổi package, Reload Maven và build lại; kiểm tra tab cũ chưa lưu trước khi ghi đè source mới.

## 7. Reset và khôi phục

Lệnh sau **thay toàn bộ bảng/dữ liệu ứng dụng của database đích** bằng bản demo mới:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/setup-database.ps1 -Database PetStationDemo2 -Reset
```

Script kiểm tra tên database và bảng lạ, tạo backup `COPY_ONLY,CHECKSUM`, chạy `RESTORE VERIFYONLY`, rồi mới xóa bảng ứng dụng và chạy hai SQL. Backup nằm tại thư mục backup của SQL Server; đường dẫn được ghi trong `target/database-backups/`. Nếu sao lưu hoặc kiểm chứng lỗi, reset không bắt đầu. Nếu lỗi sau bước xóa, dùng backup đã xác minh để khôi phục qua SSMS trước khi thao tác tiếp. Dừng ứng dụng và ngắt các kết nối ghi trong lúc reset; không dùng script này trên database bán hàng thật.

Khôi phục: SSMS → database → Tasks → Restore → Database → Device → chọn file `.bak` được ghi trong log, kiểm tra database đích rồi thực hiện. Người quản trị cần có quyền và xử lý các kết nối đang mở. Không xóa backup trước khi đã kiểm tra dữ liệu mới.

## 8. Kiểm thử

`./mvnw.cmd verify` chạy bộ không cần SQL, bỏ qua 60 test tích hợp. Để tạo/reset database kiểm thử **riêng** và chạy đủ bộ:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-sqlserver.ps1
```

Script dùng `PetStationIntegrationTest`, không dùng database bán hàng. Hai suite SQL tự kiểm tra tên database. Xem [kịch bản kiểm thử](docs/KIEM_THU_VA_YEU_CAU.md) để ghi kết quả thủ công và các giới hạn còn lại.

## 9. Lỗi thường gặp

| Lỗi | Cách kiểm tra |
| --- | --- |
| `UnsupportedClassVersionError`, không hiểu record/switch | Java chạy app hoặc Maven JDK còn dưới 17; kiểm tra `java -version`, `mvnw -v` và SDK của IDE |
| Thiếu FlatLaf/POI/SQLServerDriver | Import bằng Maven, Reload Maven hoặc dùng JAR đã package; bỏ cấu hình JAR thủ công cũ |
| Connection refused / TCP timeout | Dịch vụ SQL, TCP/IP, cổng thật, firewall; dùng đúng server của máy mới |
| Login failed / cannot open database | Phân biệt tài khoản ứng dụng với SQL login; kiểm tra SQL Authentication, mật khẩu và user trong database |
| Thiếu bảng/cột | Chưa chạy bộ SQL hiện tại hoặc cấu hình nhầm database; không trộn SQL cũ với schema mới |
| Không thấy config/ảnh | Working directory sai; đặt `PETSTATION_DB_CONFIG` tuyệt đối và mang theo ảnh ngoài JAR |
| IDE đỏ code nhưng Wrapper chạy | Project chưa import đúng Maven/JDK; reload/update Maven, bỏ file cấu hình IDE chép từ máy khác |
| Chữ tiếng Việt lỗi | Lưu source/SQL UTF-8; dùng sqlcmd `-f 65001`; không chuyển file qua ANSI |

Maven Wrapper được cung cấp theo [hướng dẫn Apache Maven](https://maven.apache.org/tools/wrapper/index.html); dependency và phiên bản build khai báo tập trung trong `pom.xml`.
