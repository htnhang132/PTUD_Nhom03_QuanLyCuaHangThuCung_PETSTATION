# Chạy Pet Station với SQL Server

Dự án dùng JDK 17+, Maven và SQL Server 2019+. VS Code dùng Extension Pack for Java, mở thư mục chứa `pom.xml`.

Cấu trúc package và nơi sửa từng chức năng được mô tả trong [ARCHITECTURE.md](docs/ARCHITECTURE.md). Nghiệp vụ và kịch bản trình diễn hiện tại được mô tả trong [KICH_BAN_DEMO.md](docs/KICH_BAN_DEMO.md). Bản hoàn thiện demo có bổ sung schema; cần nâng cấp có sao lưu trước khi dùng mã mới với database cũ.

## 1. Tạo hoặc nâng cấp database

Trong PowerShell tại thư mục dự án:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/setup-database.ps1
```

Script dùng quyền Windows hiện tại để kết nối `localhost,1433` và chuẩn bị `PetStationDemo2`. Có thể chỉ định `-Server` và `-Database`.

Thư mục `database` chỉ có hai file, chạy theo thứ tự:

1. `001_schema.sql`: bảng, khóa chính, khóa ngoại, ràng buộc, chỉ mục và các bổ sung cho database cũ.
2. `002_sample_data.sql`: quyền, ca làm việc, cài đặt và dữ liệu mẫu cho danh mục, lịch hẹn, Hotel, nhập kho, hóa đơn, điểm khách hàng.

Nếu dùng SSMS, chọn database đích rồi chạy hai file trên. Các script có thể chạy lại, không xóa bảng và không ghi đè bản ghi đã có. Phiên bản `005_schema_v2.sql` cũ đã được hợp nhất; ứng dụng thống nhất sử dụng khóa `id`.

## 2. Cấu hình kết nối ứng dụng

Sao chép `src/main/resources/db.properties.example` thành `db.properties` rồi đặt URL, tài khoản và mật khẩu SQL Server phù hợp. Cấu hình hiện có của máy này được giữ nguyên. Có thể dùng các biến môi trường để ghi đè:

```powershell
$env:PETSTATION_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=PetStationDemo2;encrypt=true;trustServerCertificate=true'
$env:PETSTATION_DB_USER = 'your_sql_login'
$env:PETSTATION_DB_PASSWORD = 'your_sql_password'
```

Tài khoản ứng dụng hiện có vẫn dùng được. Trên máy đang triển khai, bốn tài khoản demo đã được chuẩn bị bằng mật khẩu riêng tại `target/demo-access.properties` (tệp cục bộ bị Git bỏ qua). Với database mới, các tài khoản `demo.admin`, `demo.sales`, `demo.stock`, `demo.spa` được tạo ở trạng thái **khóa, chưa có mật khẩu đăng nhập**. Quản lý vào **Hệ thống → Tài khoản**, đặt mật khẩu riêng rồi mở khóa nếu cần sử dụng.

Với database hoàn toàn mới chưa có quản lý đang hoạt động, tạo quản lý đầu tiên bằng tiện ích tương tác:

```powershell
mvn -B -ntp package
java -cp target/petstation.jar com.petstation.util.KhoiTaoQuanTri
```

Tiện ích hỏi tên đăng nhập, họ tên, số điện thoại và mật khẩu trong terminal. Không có mật khẩu chung được kích hoạt tự động.

## 3. Chạy ứng dụng

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/run-java.ps1
```

Hoặc nhấn **F5 → Pet Station**, hoặc **Run Code** khi mở `App.java` trong VS Code. Chạy bản đóng gói bằng `java -jar target/petstation.jar`.

### Mở dự án sau khi checkout trên máy khác

Đặt `JAVA_HOME` trỏ tới JDK 17 trở lên của máy đang dùng, thêm thư mục `bin` vào `PATH`; kiểm tra `java -version`, `javac -version` và `mvn -version`. Cấu hình chia sẻ không cố định đường dẫn JDK của người khác. Script PowerShell yêu cầu Windows; các IDE đều có thể chạy lớp `com.petstation.App` trực tiếp sau khi import Maven.

- **IntelliJ IDEA:** mở `pom.xml` dưới dạng project, chọn Project SDK phù hợp và nạp Maven; chạy `App.main`.
- **VS Code:** mở thư mục gốc chứa `pom.xml`, cài Extension Pack for Java, chọn JDK trong **Java: Configure Java Runtime** nếu cần; đợi Java/Maven import xong rồi F5.
- **Eclipse:** chọn **File → Import → Maven → Existing Maven Projects**, trỏ tới thư mục gốc, chọn JRE/JDK phù hợp; chạy `App` bằng **Run As → Java Application**. Eclipse tự tạo `.project`, `.classpath`, `.settings`; không cần lấy cấu hình IDE cá nhân của máy khác.

Trước lần chạy đầu, tạo database và cấu hình kết nối theo mục 1–2; database mới cần khởi tạo tài khoản quản lý. `db.properties` và mật khẩu cục bộ không được đưa lên Git. Ảnh trong `src/main/resources/images/` phải có trong bản checkout để Maven đóng gói vào JAR.

Mở ca trước khi bán hàng, nhận cọc Hotel hoặc thu tiền dịch vụ. Các màn danh mục, lịch, phê duyệt, báo cáo và lịch sử truy vấn SQL Server; tuần hoặc kỳ chưa có dữ liệu sẽ hiển thị trống/0. Giỏ đang soạn được giữ trong phiên; **Treo đơn** lưu giỏ vào SQL Server để có thể mở lại sau khi khởi động lại.

Spa: hồ sơ thú cưng đủ ngày sinh (ít nhất 2 tháng), hai mũi tiêm và mũi gần nhất cách ít nhất 14 ngày → đặt lịch → tiếp nhận → dịch vụ theo loài/cân nặng → bắt đầu → hoàn thành → thanh toán. Lịch sử tiêm phòng nằm trong Hồ sơ thú cưng. Phiếu đang xử lý được giữ qua các bước.

Hotel: đặt phòng → nhận phòng → ghi nhật ký chăm sóc → quyết toán → thanh toán → dọn phòng xong. Giá phòng được chốt khi đặt. Cọc bắt buộc 50% tiền dự kiến, xác nhận nhận tiền mặt vào ca đang mở và trừ khi quyết toán. Phòng chỉ về Trống sau Dọn phòng xong tại màn Đặt phòng.

Điểm cộng 1 điểm/10.000đ, làm tròn xuống; không dùng điểm như tiền mặt. Đổi 200 điểm nhận một voucher Spa 150.000đ, hạn 60 ngày, gắn khách hàng và dùng một lần. Đổi hàng chỉ dành cho phụ kiện trong 3 ngày, bắt buộc chọn hàng thay thế có giá trị không thấp hơn phần thực trả được đổi; thu chênh lệch và không hoàn tiền.

Nhập/xuất/kiểm kê thao tác theo lô. Xuất hủy vượt ngưỡng, kiểm kê có chênh lệch, đề xuất giá, nghỉ phép và sửa/hủy hóa đơn được lưu vào phê duyệt. Nhân viên kho đề xuất giá; chỉ quản lý áp dụng giá bán. Phê duyệt vẫn xử lý được sau khi khởi động lại. Yêu cầu sửa hóa đơn chỉ chỉnh ghi chú; thay đổi tài chính dùng hủy hoặc đổi trả để giữ lịch sử.

QR được tạo cục bộ từ cấu hình ngân hàng trong Cài đặt. Khi đổi voucher, QR cập nhật số tiền và yêu cầu xác nhận nhận tiền lại. Chuyển khoản/thẻ cần nhân viên xác nhận đã nhận tiền; không có kết nối xác nhận số dư ngân hàng. Xuất Excel là `.xlsx` thật theo dữ liệu đang lọc, có thể mở bằng WPS.

## 4. Kiểm thử

```powershell
mvn -B -ntp test
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/test-sqlserver.ps1
```

Lệnh đầu chạy unit test; kiểm thử SQL được bỏ qua nếu chưa bật biến môi trường. Lệnh thứ hai tạo/nâng cấp database riêng `PetStationIntegrationTest`, bật kiểm thử tích hợp và chạy toàn bộ bộ test. Các tình huống nghiệp vụ dùng fixture rollback, không dùng database bán hàng. Test kiểm chứng lưu bền tạo một khách riêng rồi xóa đúng bản ghi đó trong database kiểm thử. Bộ hiện tại gồm 97 kiểm thử, trong đó 51 tình huống SQL.

Các tình huống gồm CRUD danh mục, lịch sử giá, FEFO, thiếu tồn và rollback, chống thanh toán lặp, voucher/điểm, phê duyệt, lịch/nghỉ phép, Spa, Hotel, nhập kho, đổi trả, phân quyền báo cáo và khởi tạo toàn bộ màn hình bằng dữ liệu SQL thật.

## 5. Sản phẩm thực tế và ảnh

Danh sách, giá tại ngày thu thập, ảnh và nguồn nằm trong [docs/NGUON_SAN_PHAM.md](docs/NGUON_SAN_PHAM.md). 60 ảnh gốc nằm trong `src/main/resources/images/products/`; SQL lưu đường dẫn tương đối `images/products/<ma>.jpg` hoặc `.png`. Maven tự đóng gói ảnh vào JAR, không tải ảnh mạng khi bán hàng.

Trên màn lập hóa đơn, bấm vào ảnh hoặc bất kỳ phần nào của thẻ để thêm một đơn vị. Giỏ bên phải không có ảnh. Trong danh mục, ảnh nằm đầu khung chi tiết và thay đổi theo dòng được chọn. Bấm Sửa → Chọn ảnh từ máy → Lưu để đổi ảnh; file JPG/PNG được kiểm tra và sao chép sang tên riêng. Trong workspace, ảnh chọn thêm nằm ở `images/products/` ngoài `target` để giữ qua lần build sạch; khi chạy JAR độc lập, đặt thư mục `images` cạnh JAR và mang theo thư mục này khi chuyển máy. Ảnh mẫu gốc vẫn nằm sẵn trong JAR.

Nâng cấp database đang có bằng đúng phần bổ sung, không chạy lại toàn bộ dữ liệu mẫu cũ:

```powershell
java --class-path "src/main/resources;target/petstation.jar" scripts/data/BoSungDuLieuThucTe.java --test
java --class-path "src/main/resources;target/petstation.jar" scripts/data/BoSungDuLieuThucTe.java --apply
```

`--test` chỉ dùng `PetStationIntegrationTest` đã được khởi tạo. `--apply` dùng database cấu hình hiện tại, tạo backup COPY_ONLY/CHECKSUM và chạy RESTORE VERIFYONLY trước khi sửa. Công cụ kiểm tra dấu vân tay các bản ghi cũ rồi chạy lần hai để xác nhận không nhập trùng. Đường dẫn backup nằm trong `target/product-import/database-backup.txt`.

Hai file SQL được giữ nguyên vai trò: schema bổ sung cột bằng `COL_LENGTH`; sample data có bộ `REAL_DATA_V1` chạy một lần trong transaction và khóa nhập. Khi khởi tạo database mới, chạy schema trước sample data như hướng dẫn trên. Bộ mẫu cố định ngày tại lần nhập đầu; chạy lại không đổi ngày, không bù tồn và không nhân đôi hóa đơn.

`node scripts/data/thu-thap-san-pham.cjs` xác minh hoặc khôi phục ảnh thiếu theo bảng nguồn đã chốt, không tự ghi đè ảnh/giá theo danh mục website mới. `node scripts/data/tao-du-lieu-thuc-te.cjs` tạo lại phần SQL từ bảng nguồn; `node scripts/data/tao-tai-lieu-san-pham.cjs` tạo bảng đối chiếu. Muốn có đợt dữ liệu mới phải tạo phiên bản mới và kiểm thử, không xóa dấu `REAL_DATA_V1_APPLIED_AT` trên database đang dùng.

## 6. Công cụ dữ liệu và nâng cấp database cũ

Ba script vận hành `run-java.ps1`, `setup-database.ps1`, `test-sqlserver.ps1` nằm ngay trong `scripts/`. Tám công cụ bảo trì nằm trong `scripts/data/`; chúng không chạy tự động khi mở app. Các lệnh Java sau chạy từ thư mục gốc dự án, sau khi đã package JAR và cấu hình kết nối:

```powershell
New-Item -ItemType Directory -Force target/data-tools | Out-Null
javac -encoding UTF-8 -cp target/petstation.jar -d target/data-tools scripts/data/BoSungDuLieuThucTe.java scripts/data/ChuanHoaDuLieu.java scripts/data/KhacPhucUnicode.java
java '-Dfile.encoding=UTF-8' -cp "src/main/resources;target/petstation.jar;target/data-tools" ChuanHoaDuLieu --test
java '-Dfile.encoding=UTF-8' -cp "src/main/resources;target/petstation.jar;target/data-tools" KhacPhucUnicode --test
```

`KhacPhucUnicode` dùng helper của `ChuanHoaDuLieu`, vì vậy biên dịch cùng nhau. `--test` chỉ dùng `PetStationIntegrationTest`. Với database ứng dụng đã có dữ liệu, dùng `--apply` thay cho `--test` sau khi đối chiếu thử nghiệm thành công; công cụ tạo backup COPY_ONLY/CHECKSUM và chạy RESTORE VERIFYONLY trước thay đổi. `KhacPhucUnicode --scan` chỉ rà văn bản. Không dùng `ChuanHoaDuLieu --retest` trên database ứng dụng; chế độ này chỉ phục vụ kiểm tra lại phần hiển thị trên database riêng.

Các phiên bản nâng cấp chính:

| Phiên bản | Mục đích |
| --- | --- |
| `REAL_DATA_V1` | Nhập danh mục và giao dịch mẫu liên thông một lần |
| `UI_FINISH_V1` | Hình thức bàn giao ca, giữ người nhận lịch sử chưa biết là NULL |
| `UI_DATA_CLEANUP_V1` | Chuẩn hóa mã/tên và tham chiếu chuỗi, giữ ID và các số dư |
| `UI_UNICODE_SCHEMA_V1` | Đổi tên hạng/giới tính sang NVARCHAR và tạo bảng đối chiếu |
| `UI_UNICODE_REPAIR_V1`, `UI_UNICODE_PET_V1` | Phục hồi đúng biến thể Unicode đã xác định, không tính lại hạng/điểm |

Giữ nguyên tài khoản `demo.*`, mật khẩu, quyền, cấu hình cửa hàng/ngân hàng và lịch sử. Không reset hoặc nạp đè toàn bộ dữ liệu để xử lý lỗi hiển thị. Mã cũ–mới tra tại [DOI_CHIEU_MA_DU_LIEU.md](docs/DOI_CHIEU_MA_DU_LIEU.md); cấu trúc bảo toàn và các bảng dấu phiên bản xem [ARCHITECTURE.md](docs/ARCHITECTURE.md).

Backup và báo cáo cục bộ của các đợt trước nằm trong `target/product-import/`, `target/ui-finish/`, `target/ui-repair/`. Đây là đường dẫn lưu báo cáo trên máy triển khai, không phải thành phần phải có trên Git. Backup `.bak` thật nằm tại đường dẫn SQL Server ghi trong `database-backup.txt`. Trước `mvn clean`, chuyển mật khẩu và báo cáo cần giữ ra khỏi `target`; không đưa chúng lên Git.

Node.js chỉ cần cho năm script `.cjs` bảo trì. Các script xác định thư mục dự án từ vị trí của mình; bộ sinh SQL giữ các dấu phiên bản và khối sửa Unicode. Việc chạy bộ sinh thay đổi file SQL/JSON/tài liệu trên đĩa, vì vậy xem diff và kiểm thử trước khi áp dụng database. Ứng dụng Swing chỉ cần JDK/Maven, SQL Server và cấu hình kết nối.
