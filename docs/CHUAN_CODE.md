# Chuẩn code Pet Station

Áp dụng cho đồ án Java Swing của Nhóm 03, thống nhất ngày 24/11/2026. Đây là quy ước của dự án, không phải tuyên bố mọi quy tắc bên dưới là bắt buộc đối với tất cả chương trình Java. Khi sửa code, đối chiếu thêm [rà soát từng file](RA_SOAT_MA_NGUON.md), [kiến trúc](../ARCHITECTURE.md) và [bản đồ truy vết](BAN_DO_TRUY_VET_CODE.md).

## 1. Công nghệ và định dạng

| Nội dung | Quy định | Cách kiểm tra |
| --- | --- | --- |
| Ngôn ngữ | Java, biên dịch `release=17`, không preview; SQL Server từ 2019 | `pom.xml`, `mvnw.cmd -v` |
| Build | Maven Wrapper trong repository; thư viện khai báo trong Maven, không chép JAR riêng | `mvnw.cmd -B -ntp verify` |
| Encoding | UTF-8; không sửa tiếng Việt bằng chuyển đổi encoding tùy tiện | `.editorconfig`, Maven |
| Format Java | Google Java Format **1.24.0**, kiểu **AOSP**; không chỉnh căn lề thủ công trái formatter | `scripts/format-java.ps1 -Mode check` |
| Xuống dòng | Java/SQL/Markdown dùng LF; PowerShell và CMD dùng CRLF | `.gitattributes`, `.editorconfig` |
| Import | Import tường minh, không `*`, không import thừa; không viết tên package đầy đủ trong thân code chính | Kiểm thử kiến trúc |

## 2. Đặt tên và cấu trúc file

- Tên nghiệp vụ dùng **tiếng Việt không dấu**: `tinhTongTien`, `maKhachHang`, `kiemTraNhanVien`. Giữ tên API Java, tên thư viện, hậu tố `Service`, `DAO`, `DAOImpl`, `Panel`, `Dialog`, `DTO` và getter/setter đang là hợp đồng sử dụng.
- Lớp/interface/enum: PascalCase; hàm và biến: camelCase; hằng: UPPER_SNAKE_CASE; package: chữ thường, theo phân hệ. File Java công khai trùng tên kiểu chính.
- Tên thể hiện việc làm và đơn vị: `soLuong`, `tienKhachDua`, `ngayRaDuKien`. Không đổi hàng loạt tên nội bộ chỉ để đạt chỉ tiêu số file sửa. Các viết tắt GUI hiện có như `btn`, `cbo`, `spn` được giữ; không thêm tiền tố `txt/pnl/tbl/lbl` đang bị quy tắc dự án cấm.
- `Panel` phải là thành phần giao diện; tiện ích static có constructor private, không kế thừa Swing chỉ để dùng hàm tạo thành phần. Ví dụ đã sửa: `TaoThanhPhanBieuMau`.
- DAO nằm ở package phân hệ, implementation trong `impl`, tên `<Ten>DAO` và `<Ten>DAOImpl`. Không gộp interface với implementation để giảm số file.
- Phương thức trong mã chính không quá **80 dòng** theo phép kiểm tra hiện có. Lớp trên **500 dòng** là dấu hiệu cần review, không phải lý do tự động xóa hoặc chia lớp. Các lớp lớn còn lại được ghi rõ trong báo cáo rà soát.
- Dùng class/enum/interface như quy ước hiện tại; không thêm `record` trong mã chính. `record` vẫn là tính năng hợp lệ của Java, không phải lỗi OOP nói chung.

## 3. Trách nhiệm các tầng

| Vị trí | Được làm | Không được làm |
| --- | --- | --- |
| GUI | Bố cục, hiển thị, nhận thao tác, kiểm tra định dạng nhập, gọi service | Gọi DAO/JDBC trực tiếp, tự ghi SQL, quyết định quyền chỉ bằng ẩn nút |
| Service | Kiểm tra quyền, ràng buộc nghiệp vụ, điều phối transaction và sự kiện | Import Swing/AWT, viết truy vấn qua `ThaoTacSQL` |
| DAO | SQL tham số hóa, ánh xạ kết quả, khóa dữ liệu cần thiết | Gọi ngược service/GUI, hiển thị hộp thoại |
| Model | Dữ liệu miền, enum, bất biến và tính toán của đối tượng | Phụ thuộc GUI/service/DAO/database |
| DTO | Hợp đồng dữ liệu có kiểu, bản sao dữ liệu khi cần | Gọi các tầng xử lý; che giấu mảng vị trí dưới danh nghĩa “đã typed hoàn toàn” |
| Database | Kết nối, thực thi SQL, commit/rollback/savepoint | Phụ thuộc cửa sổ hoặc quyền hiển thị |
| Util | Chuyển đổi/định dạng hoặc tác vụ dùng chung có trách nhiệm rõ | Trở thành nơi gom mọi nghiệp vụ không tìm được chỗ đặt |

Luồng gọi chính: **GUI → Service → DAO → SQL Server**. Model và DTO hỗ trợ truyền trạng thái giữa các tầng. Đây là phân tầng logic trong **một ứng dụng desktop**, không phải microservice hay bốn máy chủ.

`KhoiTaoUngDung` là nơi khởi tạo hạ tầng ban đầu. Constructor nhận dependency giúp thay DAO thật bằng DAO giả khi test. Các service còn constructor mặc định và `macDinh()` để tương thích; đó không phải bằng chứng toàn dự án đã áp dụng dependency injection hoàn toàn.

## 4. OOP và hợp đồng dữ liệu

- Thuộc tính nội bộ private; getter/setter bảo vệ trạng thái theo nghiệp vụ. Không xóa setter chỉ vì tìm kiếm văn bản không thấy lời gọi.
- Ưu tiên **composition** khi đối tượng chỉ sử dụng đối tượng khác. Dùng kế thừa khi lớp con thực sự là một dạng của lớp cha và giữ đúng hợp đồng của lớp cha.
- Dùng interface cho hợp đồng có triển khai hoặc cần thay thế khi kiểm thử; không tạo interface rỗng cho mọi lớp chỉ để được gọi là SOLID.
- Giữ kiểu trả về và cách báo lỗi nhất quán khi refactor. Không đổi `null` sang exception hoặc ngược lại nếu chưa cập nhật đầy đủ hợp đồng và kiểm thử.
- `DinhDang.docNgayChuanHoacNull` đọc đúng `dd/MM/uuuu`, ngày sai/rỗng/null trả null. Không dùng hàm đọc ngày linh hoạt để thay thế khi cần giữ kiểm tra chặt.
- `VaiTro.layCacTenHienThi` trả mảng mới theo thứ tự enum; người gọi sửa mảng không được làm đổi dữ liệu enum.
- API DAO chính dùng kiểu rõ ràng. `Object[]` còn ở adapter danh mục/bảng động và vùng ánh xạ SQL nội bộ; phải đọc cùng định nghĩa cột, ghi nhận trong review, không mở rộng thành API nghiệp vụ mới.
- `GioHang` hiện chứa cả trạng thái và tính toán dù nằm trong `dto`; đây là nợ về phân loại trách nhiệm, không gọi nó là DTO thuần. Không di chuyển package trong đợt dọn an toàn này.
- Không gộp các enum trạng thái khác nghiệp vụ chỉ vì giống getter. `MaDBEnum` đã là hợp đồng dùng chung cho mã và nhãn.

<a id="sql-va-bao-mat"></a>
## 5. Tiền, ngày, SQL và phân quyền

- Tiền nghiệp vụ dùng `long` theo đồng hoặc `BigDecimal`; không tính tiền bằng `double`. SQL dùng kiểu decimal hiện tại; các số liệu phân tích dạng tỷ lệ không đồng nghĩa là số tiền chính xác.
- Dùng `DinhDang` cho hiển thị/đọc định dạng; dùng `java.time` cho ngày giờ. Không lấy nhãn hiển thị làm mã SQL. `maDB()` dùng để lưu, `tenHienThi()` dùng để đọc trên UI.
- Tham số hóa giá trị SQL. Tên bảng/cột động chỉ từ danh sách kiểm soát trong code, không từ nhập tự do. Dùng try-with-resources khi sở hữu tài nguyên.
- Service kiểm tra quyền trước khi thao tác. Ẩn nút chỉ hỗ trợ trải nghiệm, không thay thế kiểm tra quyền.
- Một nghiệp vụ nhiều bước phải commit hoặc rollback cùng phạm vi. Dùng `QuanLyGiaoDich`; không mở connection mới cho một phần việc đáng lẽ thuộc transaction đang có.
- Giữ lịch sử giá, phân bổ lô và chi tiết dịch vụ đã chốt. Không dùng giá danh mục hiện tại để dựng lại chứng từ cũ.
- Quy trình SQL duy nhất: `scripts/setup-database.ps1`, hoặc chạy lần lượt `001_schema.sql` rồi `002_sample_data.sql`. Không tự chạy DDL lúc mở ứng dụng. Không thêm file migration song song.
- Không đưa mật khẩu local vào source/JAR/log. Mật khẩu mẫu trong tài liệu chỉ là tài khoản demo; cấu hình cá nhân được Git bỏ qua và Maven loại khỏi đóng gói.

### Ghi chú vận hành và bảo mật SQL đã hợp nhất

- Login/user SQL khác tài khoản nhân viên trong ứng dụng. Script thiết lập không tự cấp login, role hay mật khẩu vận hành cho mọi máy.
- Backup/VERIFYONLY trước nâng cấp là điều kiện để bắt đầu DDL; file backup nằm trên máy SQL Server, không nhất thiết cùng máy chạy ứng dụng. Transaction rollback không thay thế việc giữ backup.
- Seed kiểm tra dữ liệu hiện có và bỏ qua nạp mẫu khi đã có; không xóa dữ liệu để vượt guard. Tùy chọn Reset dành cho trường hợp người dùng chủ động tạo lại demo/test.
- Các nội dung GRANT/REVOKE, Audit, TDE, Always Encrypted, masking hay cấu hình HA cần yêu cầu và quyền của môi trường. [Ví dụ SQL bảo mật](SQL_SECURITY_EXAMPLES.md) chỉ phục vụ học tập, không là bằng chứng chúng đã được cấu hình trong hệ thống.
- Phần ghi chép SQL trước đây mô tả từ chối mọi database có bảng không còn là hành vi hiện hành. Quy trình hiện tại hỗ trợ phiên bản cũ đã xác định và từ chối cấu trúc không nhận diện; xem SETUP.

## 6. Swing và hướng sự kiện

- Thao tác Swing trên EDT. Truy vấn/ghi SQL hoặc I/O dài chạy qua tác vụ nền; kết quả áp lên UI phải kiểm tra phiên và yêu cầu còn hợp lệ.
- Không đăng ký listener nhiều lần khi mở lại màn hình. Hủy listener/timer theo vòng đời khi không dùng nữa.
- Service phát thông báo giao dịch sau commit; lỗi listener không được biến một giao dịch đã commit thành thông báo “thất bại” rồi khiến người dùng thu tiền lại.
- `PhatSuKien<T>` và `BoLangNgheSuKien<T>` là Observer có kiểu trong cùng tiến trình. Đây **không phải event sourcing**, không có cam kết thông báo bền vững hay tự gửi lại như hàng đợi.
- Tiêu đề cửa sổ phụ theo kiểu câu; giữ mã chứng từ. Dùng hộp thoại và màu/font chung. Không tự chèn dấu hỏi mặc định vào form nhập.
- Renderer và editor của ô số lượng giữ cùng hình học. Không tự canh lại độ rộng bảng POS mỗi lần cộng/trừ. Tên dài xuống dòng hoặc có tooltip, không chồng nhãn.

## 7. Lỗi, comment và tài liệu

- Không catch rỗng, không catch `Throwable`, không trả thành công giả để che lỗi. Catch ở nơi có thể xử lý, bổ sung ngữ cảnh hoặc chuyển lỗi phù hợp, giữ nguyên cause.
- `return null` trong callback transaction `Void` không tự động là code thừa; phải xem hợp đồng. Default callback rỗng của interface có thể là lựa chọn có chủ đích, không đồng nghĩa nuốt exception.
- Comment giải thích lý do, giới hạn, ràng buộc hoặc bước nghiệp vụ khó thấy. Xóa mô tả lớp/bảng đã mất và dấu `[cite: ...]` không có nguồn.
- Tài liệu hiện hành dẫn tới file thật; ghi chép cũ được đánh dấu rõ. Không dùng số lượng test của bản trước làm số liệu cho bản đang nộp.
- Bản đồ truy vết mô tả các lớp đang tồn tại; tên bảng phải có trong schema. Sự tồn tại của một lớp không chứng minh toàn bộ đường đi đã được chạy UI thực tế.

## 8. Checklist trước khi giao code

1. Tìm toàn bộ nơi gọi và hợp đồng trước khi xóa/đổi tên; xem cả test, cấu hình, tài nguyên và mã đăng ký màn hình.
2. Chạy formatter chế độ check, test kiến trúc và `mvnw.cmd -B -ntp verify`.
3. Khi sửa nghiệp vụ/DAO/SQL, chạy `setup-database.ps1 -Database PetStationIntegrationTest_Review -RunTests` trên database riêng. Không reset database người dùng để chạy test.
4. Khi sửa UI, kiểm tra cả trạng thái rỗng/có dữ liệu, tên dài, cửa sổ hẹp và thao tác lặp. Test ảnh layout không thay máy in, QR thật hay DPI thực tế.
5. Ghi rõ số test đã chạy, thất bại và bỏ qua; kiểm tra link nội bộ và đối chiếu tài liệu.
6. Không xóa dữ liệu ảnh ngoài, backup, cấu hình IDE cá nhân hoặc `src.zip` chỉ vì không được Maven dùng. Bản archive hiện có nhiều nội dung khác mã hiện tại.

## 9. Trả lời ngắn khi bảo vệ

| Câu hỏi | Cách trả lời dựa trên mã hiện tại |
| --- | --- |
| Tại sao nhiều file DAO/interface? | Để tách hợp đồng và SQL implementation, cho phép service dùng DAO giả khi test. Xem constructor của `BanHangService`, các test service. Ít file hơn không tự động dễ bảo trì hơn. |
| Đóng gói ở đâu? | Model có thuộc tính private và phương thức truy cập/kiểm tra; ví dụ `HoaDon`, `SanPham`. `DongNghiepVu` sao chép mảng khi nhận/trả để hạn chế sửa ngoài ý muốn. |
| Kế thừa ở đâu? | Các màn hình danh mục kế thừa `QuanLyDanhMucPanel`; các báo cáo kế thừa `BaoCaoPanel`. Không dùng kế thừa cho tiện ích tạo field; phần đó đã đổi thành composition/static factory. |
| Đa hình ở đâu? | Service nhận interface DAO và gọi hợp đồng đó; khi chạy dùng implementation SQL, khi test có thể dùng mock/proxy. Đây là thay thế implementation, không phải chỉ có nhiều hàm cùng tên. |
| Có tuân thủ SOLID hoàn toàn không? | Phân tầng và interface hỗ trợ SRP/DIP, nhưng panel rất lớn và adapter mảng còn là hạn chế. Nhóm ghi nhận và không khẳng định toàn bộ source hoàn hảo. |
| Hướng sự kiện ở đâu? | ActionListener nhận thao tác Swing; Observer `PhatSuKien` thông báo thay đổi; `TacVuNen` trả kết quả về EDT. Không phải event sourcing. |
| Vì sao phải dùng SwingWorker? | Để SQL/I/O không làm treo EDT. Callback kiểm tra phiên và yêu cầu mới nhất nhằm tránh cập nhật UI bằng dữ liệu cũ. |
| Vì sao service vẫn kiểm tra quyền? | Người gọi có thể đi qua luồng khác UI. Quyền phải được chặn ở điểm thực thi nghiệp vụ, không chỉ ở nút bấm. |
| Bán nhiều lô rồi hủy thế nào? | `HoaDonDAOImpl` lưu `phan_bo_lo_hoa_don`; hoàn theo phân bổ thực. Hóa đơn cũ thiếu dữ liệu cần đối chiếu, không tự đoán lô. |
| Đổi giá spa có làm đổi hóa đơn cũ? | Chi tiết spa và hóa đơn lưu giá đã chốt. Đọc lịch sử không tính lại bằng danh mục hiện tại. |
| Vì sao chỉ hai file SQL? | `001` cài/nâng cấp cấu trúc và sao lưu trước nâng cấp; `002` chỉ nạp mẫu khi trống. Chạy lại giữ dữ liệu, `-Reset` là lựa chọn riêng có chủ đích. |
| Làm sao chứng minh code đạt quy ước? | Trình bày formatter, kiểm thử kiến trúc, test nghiệp vụ và kết quả thực chạy. Các kiểm tra tự động có giới hạn; chúng không tự chứng minh mọi nguyên tắc OOP hay mọi UI đã tối ưu. |

Ví dụ kiểm tra trực tiếp: [phát sự kiện](../src/main/java/com/petstation/service/chung/PhatSuKien.java), [tác vụ nền](../src/main/java/com/petstation/gui/chung/TacVuNen.java), [giao dịch](../src/main/java/com/petstation/database/QuanLyGiaoDich.java), [kiểm thử kiến trúc](../src/test/java/com/petstation/architecture/KienTrucPhanTangTest.java).
