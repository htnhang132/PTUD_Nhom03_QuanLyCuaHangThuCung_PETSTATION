# Cấu trúc mã nguồn Pet Station

Điểm chạy vẫn là `com.petstation.App`. Dự án dùng Swing, JDK 17 và Maven.

```text
com.petstation
├── App.java
├── ApplicationContext.java
├── model/                 Các thực thể, UserSession, SaleContext, loại danh mục/màn dữ liệu
├── dao/                   Interface DAO, cấu hình, kết nối, transaction, lỗi truy cập dữ liệu
│   └── impl/              Các lớp Jdbc*: câu SQL và ánh xạ kết quả
├── service/               Xác thực, quyền, kiểm tra nghiệp vụ và phối hợp transaction
├── gui/
│   ├── main/              Cửa sổ chính
│   ├── dashboard/         Tổng quan
│   ├── system/            Đăng nhập, mật khẩu, tài khoản, quyền, cài đặt, nhật ký
│   ├── catalog/           Mỗi danh mục một panel
│   ├── search/            Hóa đơn, khách hàng, tồn theo lô, Spa, phòng, lịch
│   ├── processing/
│   │   ├── sales/         POS, thanh toán, in, đơn treo, đổi trả, ca tiền mặt
│   │   ├── spa/           Lịch hẹn, tiếp nhận, dịch vụ, tiến độ, đóng phiếu
│   │   ├── hotel/         Đặt phòng, lưu trú, nhật ký, quyết toán
│   │   ├── inventory/     Nhập kho, xuất/hủy, kiểm kê
│   │   ├── staff/         Phân lịch và form nghỉ phép
│   │   └── approval/      Yêu cầu sửa/hủy hóa đơn và phê duyệt
│   ├── reports/           Mỗi nhóm báo cáo một panel
│   ├── components/        Bảng, bộ lọc, form, lịch tuần và thành phần Swing dùng chung
│   │   └── common/        Chọn khách/ngày, nhập/xuất Excel và editor dùng chung
│   └── navigation/        ScreenId, ScreenRegistry, menu, luồng chuyển màn hình
└── util/                  Money và tiện ích khởi tạo quản lý
```

## Tìm đúng nơi để sửa

| Công việc | Nơi sửa |
| --- | --- |
| Đổi trường/form của một danh mục | Panel tương ứng trong `gui/catalog` hoặc `gui/system` |
| Đọc/ghi danh mục | `CatalogService` và `JdbcCatalogDao`; giữ các interface DAO thực thể trong `dao` |
| Đổi dữ liệu bảng nghiệp vụ | `WorkflowType`, `WorkflowService`, `JdbcWorkflowDao` |
| Thêm/sửa nút xử lý | Panel cụ thể; form dùng chung của phân hệ nằm ở `SpaWorkflowPanel`, `HotelWorkflowPanel`, `InventoryWorkflowPanel`, `SalesWorkflowPanel` |
| Spa, Hotel, kho, đổi trả | `SpaService`, `HotelService`, `InventoryService`, `ReturnService` và DAO tương ứng |
| Thanh toán, nạp hóa đơn dịch vụ, đơn treo | `SalesService` và `JdbcSalesDao` |
| Phê duyệt và tác động của quyết định | `ApprovalService`; phối hợp các service nghiệp vụ trong cùng transaction |
| Cài đặt, nhật ký, lịch, quyền, ca | `SettingsService`, `AuditService`, `ScheduleService`, `PermissionService`, `CashShiftService` |
| Đăng nhập và phiên | `AuthenticationService`, `AccountService`, `SessionContext`, `SessionService`, `AuthorizationService`; dữ liệu phiên là `model.UserSession` |
| Nhóm menu, quyền màn hình, phím tắt, điều hướng | `MenuModel`, `ScreenRegistry`, `ScreenFlows` |
| Tổng hợp báo cáo | `ReportingService`, `JdbcReportingDao` và panel trong `gui/reports` |

Panel thu thập đầu vào và hiển thị kết quả. Service kiểm tra quyền/nghiệp vụ và mở transaction. DAO chứa SQL với tham số ràng buộc; các lệnh JDBC dùng chung kết nối của transaction hiện tại. Transaction lồng nhau dùng savepoint. Các truy vấn báo cáo nhận phạm vi tài khoản/nhân viên đã xác định ở service.

`WorkflowTablePanel` chỉ dựng bảng, lọc, tải lại, chọn dòng, xem chi tiết và xuất Excel. Panel con chọn loại dữ liệu và tự đăng ký các hành động; không có switch tổng xử lý mọi nghiệp vụ trong GUI.

Các tab được giữ: sản phẩm/lịch sử giá, quy tắc điểm/voucher, tài khoản/ma trận quyền. Mỗi `ScreenId` có panel riêng; mã màn hình và nhóm menu giữ nguyên.

## Database và kiểm tra

Hai file `database/001_schema.sql` và `database/002_sample_data.sql` giữ nguyên trong đợt sắp xếp package này. Refactor không yêu cầu chạy migration hoặc nạp lại dữ liệu mẫu.

Build và unit test: `mvn -B -ntp clean package`. Để chạy đủ 46 test với database kiểm thử **đã chuẩn bị sẵn**, dùng PowerShell:

```powershell
$env:PETSTATION_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=PetStationIntegrationTest;encrypt=true;trustServerCertificate=true'
$env:PETSTATION_SQL_TESTS = 'true'
mvn -B -ntp clean package
Remove-Item Env:PETSTATION_SQL_TESTS
Remove-Item Env:PETSTATION_DB_URL
```

16 test SQL chỉ chấp nhận `PetStationIntegrationTest`; từng tình huống rollback. Chúng kiểm tra CRUD, thanh toán, tồn kho, phê duyệt, lịch, Spa, Hotel, báo cáo, toàn bộ panel và các tab/hành động chính. Lệnh trên không thực thi file schema hay seed.

Chạy `java -jar target/petstation.jar`, F5 hoặc `scripts/run-java.ps1` như trước. Nếu VS Code còn tab cũ có thay đổi chưa lưu sau khi đổi package, đối chiếu bản nháp với file mới trước khi lưu lại đường dẫn cũ.
