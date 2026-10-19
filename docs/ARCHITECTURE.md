# Cấu trúc mã nguồn Pet Station

Điểm chạy vẫn là `com.petstation.App`. Dự án dùng Swing, JDK 17 và Maven.

```text
com.petstation
├── App.java
├── model/                 Các thực thể, PhienDangNhap, GioHang, loại danh mục/màn dữ liệu
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
│   │   └── common/        Chọn khách/ngày, xuất Excel và editor dùng chung
│   └── navigation/        MaManHinh, DangKyManHinh, menu, luồng chuyển màn hình
└── util/                  KhoiTaoUngDung, TienTe và tiện ích khởi tạo quản lý
```

## Tìm đúng nơi để sửa

| Công việc | Nơi sửa |
| --- | --- |
| Đổi trường/form của một danh mục | Panel tương ứng trong `gui/catalog` hoặc `gui/system` |
| Đọc/ghi danh mục | `DanhMucService` và `JdbcDanhMucDAO`; giữ các interface DAO thực thể trong `dao` |
| Đổi dữ liệu bảng nghiệp vụ | `LoaiNghiepVu`, `NghiepVuService`, `JdbcNghiepVuDAO` |
| Thêm/sửa nút xử lý | Panel cụ thể; form dùng chung của phân hệ nằm ở `NghiepVuSpaPanel`, `NghiepVuHotelPanel`, `NghiepVuKhoPanel`, `NghiepVuBanHangPanel` |
| Spa, Hotel, kho, đổi trả | `SpaService`, `LuuTruService`, `KhoService`, `DoiTraService` và DAO tương ứng |
| Điểm, đổi voucher và sàng lọc Spa | `TichDiemService`/`JdbcTichDiemDAO`, `TiemPhongService`/`JdbcTiemPhongDAO` |
| Ngữ cảnh chuyển bước Spa/Hotel | `model.NguCanhQuyTrinh`, `QuyTrinhService`, `JdbcQuyTrinhDAO` |
| Đề xuất và áp dụng giá | `DeXuatGiaService`, `JdbcDeXuatGiaDAO`, `PheDuyetService` |
| Đổi phụ kiện có hàng thay thế | `DoiTraService`, `JdbcDoiHangDAO`, thanh toán chung qua `BanHangService` |
| QR và xuất bảng tính | `util.TaoMaVietQR` (ZXing), `common.XuatExcel` (POI/XSSF) |
| Thanh toán, nạp hóa đơn dịch vụ, đơn treo | `BanHangService` và `JdbcBanHangDAO` |
| Phê duyệt và tác động của quyết định | `PheDuyetService`; phối hợp các service nghiệp vụ trong cùng transaction |
| Cài đặt, nhật ký, lịch, quyền, ca | `CaiDatService`, `NhatKyHeThongService`, `LichLamViecService`, `QuyenVaiTroService`, `CaThuNganService` |
| Đăng nhập và phiên | `XacThucService`, `TaiKhoanService`, `NguCanhPhienService`, `PhienDangNhapService`, `PhanQuyenService`; dữ liệu phiên là `model.PhienDangNhap` |
| Nhóm menu, quyền màn hình, phím tắt, điều hướng | `CauTrucMenu`, `DangKyManHinh`, `QuyTrinhManHinh` |
| Tổng hợp báo cáo | `ThongKeService`, `JdbcThongKeDAO` và panel trong `gui/reports` |

Panel thu thập đầu vào và hiển thị kết quả. Service kiểm tra quyền/nghiệp vụ và mở transaction. DAO chứa SQL với tham số ràng buộc; các lệnh JDBC dùng chung kết nối của transaction hiện tại. Transaction lồng nhau dùng savepoint. Các truy vấn báo cáo nhận phạm vi tài khoản/nhân viên đã xác định ở service.

`BangNghiepVuPanel` chỉ dựng bảng, lọc, tải lại, chọn dòng, xem chi tiết và xuất Excel. Panel con chọn loại dữ liệu và tự đăng ký các hành động; không có switch tổng xử lý mọi nghiệp vụ trong GUI.

Các tab được giữ: sản phẩm/lịch sử giá, quy tắc điểm/voucher, tài khoản/ma trận quyền. Mỗi `MaManHinh` có panel riêng; mã màn hình và nhóm menu giữ nguyên.

## Database và kiểm tra

Database có đúng hai file SQL. Bản demo bổ sung khóa đổi voucher, thông tin nhận cọc theo ca và loại phiếu đổi hàng, theo các block `DEMO_COMPLETION_*`. Nâng cấp phải sao lưu/kiểm chứng và thử trước trên database riêng; không reset hay chuyển đổi phiếu hoàn tiền lịch sử. Xem [kịch bản demo](KICH_BAN_DEMO.md).

Build và unit test: `mvn -B -ntp package`. Để chạy đủ 97 test với database kiểm thử **đã chuẩn bị sẵn**, dùng PowerShell:

```powershell
$env:PETSTATION_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=PetStationIntegrationTest;encrypt=true;trustServerCertificate=true'
$env:PETSTATION_SQL_TESTS = 'true'
mvn -B -ntp package
Remove-Item Env:PETSTATION_SQL_TESTS
Remove-Item Env:PETSTATION_DB_URL
```

51 test SQL chỉ chấp nhận `PetStationIntegrationTest`; fixture nghiệp vụ rollback; test lưu bền tự xóa bản ghi riêng. Chúng kiểm tra CRUD, thanh toán, tồn kho, phê duyệt, lịch, Spa, Hotel, báo cáo, ảnh sản phẩm, sổ chi tiết bộ dữ liệu demo, toàn bộ panel và các tab/hành động chính. Lệnh trên không thực thi file schema hay seed.

## Dọn mã không sử dụng

Đợt dọn ngày 20/10/2026 bỏ 22 file Java không được luồng hiện tại sử dụng: tám model/thành phần GUI độc lập và 14 triển khai JDBC cũ. Giữ các interface DAO và model còn được hợp đồng sử dụng; một số interface hiện chưa có triển khai. `JdbcKhachHangDAO` vẫn được giữ vì kiểm thử SQL dùng trực tiếp. Không dùng lại câu SQL từ các DAO đã bỏ vì có tên cột thuộc schema cũ. Nguồn ứng dụng còn 225 file Java và 20 file kiểm thử. Bản sao cục bộ của nguồn đã bỏ nằm trong `target/cleanup-approved/` và không đưa lên Git.

Danh mục thực tế dùng `san_pham.anh` và các cột thông tin tùy chọn bổ sung trong `REAL_CATALOG_V1`. Thứ tự mười trường danh mục cũ được giữ, bảy trường ảnh/chi tiết nối phía sau; truy vấn bán hàng nối đường dẫn ảnh ở vị trí 5. `BoChonAnhSanPham` dùng điểm mở rộng bộ soạn thảo form hiện có, đưa ảnh lên đầu form mà không đổi chỉ số dữ liệu. `AnhSanPhamPanel` tải nền, lưu bộ nhớ đệm tối đa 96 ảnh kích thước cạnh dài 600px và loại kết quả cũ khi đổi sản phẩm. Không dùng URL mạng trong luồng bán hàng.

Bộ `REAL_DATA_V1` có bảng nguồn trong [NGUON_SAN_PHAM.md](NGUON_SAN_PHAM.md), 60 ảnh JAR và giao dịch giả lập có mã riêng. Bộ nhập có khóa SQL, transaction và dấu phiên bản, không tự cập nhật theo giá web mới hoặc chạy lại để bù tồn. Công cụ bổ sung sao lưu/VERIFYONLY và so dấu vân tay 42 bảng có ID trước/sau, kiểm tra nhập lần hai không đổi dữ liệu.

Chạy `java -jar target/petstation.jar`, F5 hoặc `scripts/run-java.ps1` như trước. Nếu VS Code còn tab cũ có thay đổi chưa lưu sau khi đổi package, đối chiếu bản nháp với file mới trước khi lưu lại đường dẫn cũ.

## Nguyên tắc bảo trì và dữ liệu

DAO dùng PreparedStatement/tham số Unicode; service kiểm tra quyền và trạng thái, phối hợp transaction. Không mở kết nối ngoài transaction đang xử lý, không fallback sang dữ liệu giả khi SQL lỗi. Model biểu diễn dữ liệu; GUI chỉ thu thập đầu vào và hiển thị kết quả. Các interface được giữ để tiếp tục hoàn thiện kiến trúc, không phải bằng chứng mọi interface đã có triển khai đang chạy.

Database giữ đúng hai file SQL, nâng cấp có phiên bản. `UI_DATA_CLEANUP_V1` chuẩn hóa tên/mã và tham chiếu chuỗi, giữ ID, số lượng, tiền, điểm, cọc, công nợ, ngày và trạng thái; mã cũ–mới được lưu trong `doi_chieu_ma_du_lieu` và [DOI_CHIEU_MA_DU_LIEU.md](DOI_CHIEU_MA_DU_LIEU.md).

`UI_UNICODE_SCHEMA_V1` chuyển hạng thành viên sang NVARCHAR(20), giới tính thú cưng sang NVARCHAR(10). `UI_UNICODE_REPAIR_V1` và `UI_UNICODE_PET_V1` chỉ phục hồi biến thể hỏng xác định được, lưu trước/sau tại `doi_chieu_unicode`; không thay hàng loạt dấu hỏi hay tính lại hạng theo điểm. Dấu phiên bản ngăn chạy lại sửa thêm. Tài khoản, mật khẩu và cấu hình cửa hàng/ngân hàng được bảo toàn.

Các công cụ quản lý dữ liệu đặt trong `scripts/data/`, tách khỏi ba script vận hành ở `scripts/`. Node.js chỉ cần khi bảo trì/tái tạo dữ liệu, không cần để chạy ứng dụng Swing. Xem lệnh sử dụng trong [SETUP.md](../SETUP.md).

## Thành phần giao diện dùng chung

- `BangNghiepVuPanel`: đếm dòng sau lọc bằng N kết quả; Làm mới đọc SQL một lần, giữ bộ lọc/sắp xếp và dòng chọn theo ID. Xuất Excel theo các dòng đang hiển thị.
- `HoTroCuon`: form/thẻ cuộn theo đơn vị 24px, bảng theo chiều cao dòng, không chồng thêm bộ xử lý wheel.
- `BoChonNgay`: lưới tối đa sáu tuần, tính kích thước theo font, Xóa phát sự kiện thay đổi; khoảng ngày bắt buộc kiểm tra thiếu hoặc đảo ngược.
- `BieuTuongGiaoDien`: icon vẽ bằng nét cho quyền/cảnh báo/điều hướng, không dựa vào glyph đặc biệt. Ma trận quyền giữ dữ liệu Boolean.
- `ThongTinPhong` và `QuyTrinhService`: nạp chi tiết phòng cùng dữ liệu danh sách, không truy vấn khi hover; ngữ cảnh quy trình giữ ID phòng/hợp đồng.
- `HoaDonDocPanel` và `ChiTietBienLai`: xem/in chung bố cục 80mm, logo tải trước in, ngắt giữa các dòng hàng. Dùng số tiền lưu trong hóa đơn; số dư điểm lịch sử chỉ hiện khi xác định được, không lấy điểm hiện tại. Trên A4/PDF giữ nội dung hẹp căn giữa.
