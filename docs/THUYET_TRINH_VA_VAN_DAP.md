# Thuyết trình và vấn đáp Pet Station

Tài liệu hiện hành, biên tập ngày 24/11/2026 từ các cẩm nang/quy trình/phân tích trước đây, đối chiếu lại với source đang có. Chỉ giữ nội dung có thể dẫn chứng; không nối nguyên văn các bản cũ có tên lớp giả định. Xem [chuẩn code](CHUAN_CODE.md), [bản đồ 47 mã màn hình/quy trình](BAN_DO_TRUY_VET_CODE.md), [kịch bản kiểm thử](KIEM_THU_VA_YEU_CAU.md) và [các hạn chế còn lại](RA_SOAT_MA_NGUON.md).

## 1. Bài giới thiệu ngắn

Pet Station là ứng dụng desktop dành cho nhân viên cửa hàng thú cưng, gồm bán hàng, khách hàng/thú cưng, spa, lưu trú, kho, nhân sự, phê duyệt và báo cáo. Giao diện dùng Java Swing/FlatLaf; dữ liệu lưu trong SQL Server; Maven quản lý build và thư viện.

Ứng dụng phân tầng logic GUI → Service → DAO → SQL Server trong cùng tiến trình. Mục tiêu là tách thao tác giao diện khỏi kiểm tra nghiệp vụ và truy cập dữ liệu. Mã màn hình và điều hướng tập trung trong `DangKyManHinh`; một số mã là bước hoặc chế độ của cùng màn hình, không phải mỗi mã có một cửa sổ độc lập.

Khi trình bày, ưu tiên luồng có thể thao tác trên bản demo và chỉ rõ một handler GUI, phương thức service, DAO và bảng liên quan. Không dùng tên lớp trong các bản cẩm nang cũ nếu không tìm được file thật.

<a id="so-do-tong-quat"></a>
## 2. Sơ đồ tổng quát

```text
Người dùng
    │ ActionListener / phím tắt
    ▼
GUI: Panel, Dialog, thành phần dùng chung
    │ gọi service; I/O dài chạy qua TacVuNen
    ▼
Service: quyền, ràng buộc nghiệp vụ, transaction
    │ hợp đồng DAO
    ▼
DAO interface → DAOImpl → JDBC → SQL Server

SQL thành công → commit → thông báo Observer
Kết quả nền còn hợp lệ → callback trên EDT → cập nhật GUI
```

Đây không phải microservice hay event sourcing. Model/DTO cung cấp dữ liệu và trạng thái cho các tầng; `GioHang` hiện có cả tính toán nghiệp vụ dù nằm trong package dto, là hạn chế đã ghi nhận.

Sơ đồ phân cấp chức năng: [bản xem PNG](diagrams/so-do-phan-cap-chuc-nang-ver2.png), [bản gốc chỉnh sửa drawio](diagrams/so-do-phan-cap-chuc-nang.drawio). Sơ đồ phân tích không thay cho bản đồ lớp thực tế trong source.

## 3. Trình tự demo đề xuất

1. Đăng nhập và chỉ ra vai trò/quyền; đối chiếu menu được phép với kiểm tra quyền tại service.
2. Mở danh mục sản phẩm: tìm kiếm, xem chi tiết và nhập/sửa dữ liệu trên database demo.
3. Mở ca, lập đơn, thay đổi số lượng, xem khuyến mãi, lưu/mở đơn treo rồi thanh toán.
4. Xem hóa đơn và phiếu đổi trả; giải thích phân bổ lô, giá lịch sử và chặn thao tác không hợp lệ.
5. Tạo phiếu spa, chọn nhiều dịch vụ, hoàn tất rồi chuyển thanh toán.
6. Đặt phòng lưu trú, thu cọc từng phần, nhận phòng, ghi chăm sóc và quyết toán.
7. Minh họa nhập/xuất/kiểm kê kho, lịch nhân viên và một luồng phê duyệt trên dữ liệu thử.
8. Xem báo cáo, lọc khoảng ngày, giải thích nguồn số liệu và giới hạn giá vốn.

Luôn demo thêm một trường hợp bị chặn: không có quyền, thiếu tồn hoặc số tiền thu vượt mức còn thiếu. Chuẩn bị dữ liệu demo trước; không dùng `-Reset` trên database cửa hàng để chuẩn bị bài.

## 4. Các luồng nghiệp vụ có dẫn chứng

| Luồng | Nơi bắt đầu xem code | Điểm cần giải thích |
| --- | --- | --- |
| Khởi động, đăng nhập | [App](../src/main/java/com/petstation/App.java), [KhoiTaoUngDung](../src/main/java/com/petstation/util/KhoiTaoUngDung.java), [XacThucService](../src/main/java/com/petstation/service/hethong/XacThucService.java) | Nạp cấu hình, xác minh SQL/phiên bản, xác thực rồi mở giao diện theo phiên. Chạy app không tự sửa schema. |
| Danh mục | [QuanLyDanhMucPanel](../src/main/java/com/petstation/gui/chung/QuanLyDanhMucPanel.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java), [DanhMucDAOImpl](../src/main/java/com/petstation/dao/chung/impl/DanhMucDAOImpl.java) | Dùng khung chung theo loại danh mục. Xóa mềm và quyền do nghiệp vụ quyết định; không phải mọi danh mục có DAO riêng. |
| Bán hàng | [LapHoaDonPanel](../src/main/java/com/petstation/gui/banhang/LapHoaDonPanel.java), [GioHang](../src/main/java/com/petstation/dto/GioHang.java), [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java) | UI nhận mã/số lượng, giỏ tính tiền, service kiểm tra điều kiện thực thi. Cột số lượng giữ kích thước khi cập nhật; khuyến mãi dài không đè nhãn. |
| Thanh toán và hủy | [HoaDonDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/HoaDonDAOImpl.java) | Transaction bảo vệ hóa đơn, tồn và các cập nhật liên quan. `phan_bo_lo_hoa_don` lưu từng lô và giá vốn; hóa đơn lịch sử thiếu phân bổ phải đối chiếu trước khi hủy. |
| Đổi phụ kiện | [DoiTraService](../src/main/java/com/petstation/service/banhang/DoiTraService.java), [PhieuDoiTraDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/PhieuDoiTraDAOImpl.java) | Kiểm tra nhóm phụ kiện, hạn đổi theo cấu hình, phần số lượng còn được đổi và lô gốc. Không nói mọi mặt hàng đều đổi được. |
| Ca thu ngân | [CaThuNganService](../src/main/java/com/petstation/service/banhang/CaThuNganService.java), [PhienLamViecDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/PhienLamViecDAOImpl.java) | Mở ca, tiền đầu ca và đối chiếu khi kết ca. Bản UI hiện tại nhập tổng tiền trực tiếp; không trình bày cơ chế bắt buộc đếm từng mệnh giá từ bản đề xuất cũ như chức năng đang có. |
| Spa | [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [PhieuTiepNhanSpaDAOImpl](../src/main/java/com/petstation/dao/spa/impl/PhieuTiepNhanSpaDAOImpl.java) | Kiểm tra hồ sơ, báo giá theo gói/cân nặng, lưu từng dịch vụ với giá đã chốt; không dựng lại giá cũ từ danh mục mới. |
| Hồ sơ tiêm phòng | [TiemPhongService](../src/main/java/com/petstation/service/khachhang/TiemPhongService.java) | Đọc điều kiện thực tế trong service và lịch sử tiêm; không khẳng định một ngưỡng y tế chỉ vì có trong tài liệu khảo sát cũ. |
| Lưu trú | [LuuTruService](../src/main/java/com/petstation/service/luutru/LuuTruService.java), [HopDongLuuTruDAOImpl](../src/main/java/com/petstation/dao/luutru/impl/HopDongLuuTruDAOImpl.java) | `giuPhong`, `thuCoc`, `kiemTraVao`, gia hạn và quyết toán. Thu cọc từng phần có kiểm tra hạn mức và khóa chống thu trùng; tiền cọc không được trừ hai lần. |
| Kho và kiểm kê | [NhapXuatKhoService](../src/main/java/com/petstation/service/kho/NhapXuatKhoService.java), [KiemKeService](../src/main/java/com/petstation/service/kho/KiemKeService.java) | Tồn theo lô, chứng từ nhập/xuất, lập biên bản và áp dụng sau bước duyệt. Đọc trạng thái thực tế trước khi mô tả ai được cân bằng kho. |
| Lịch nhân viên | [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java), [LichLamViecService](../src/main/java/com/petstation/service/nhansu/LichLamViecService.java) | Các chế độ sửa, hiển thị tất cả và cá nhân dùng chung panel; kiểm tra trùng/lịch nghỉ theo service và quyền hiện tại. |
| Phê duyệt | [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java) | Yêu cầu chờ duyệt khác với nghiệp vụ đã thực hiện. Người duyệt và trạng thái được kiểm tra khi xử lý. |
| Báo cáo | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ThongKeDAOImpl](../src/main/java/com/petstation/dao/thongke/impl/ThongKeDAOImpl.java) | Đọc đúng câu truy vấn và phạm vi thời gian; phân biệt khoản thu, doanh thu hoàn tất và tiền cọc. Thiếu giá vốn lịch sử phải hiển thị cần đối chiếu; chi phí vận hành chưa đầy đủ cho lợi nhuận kế toán. |

Các bảng SQL có thật được liệt kê ở [bản đồ truy vết](BAN_DO_TRUY_VET_CODE.md). Không suy ra một bảng hay lớp mới chỉ từ tên nghiệp vụ.

## 5. Vấn đáp OOP, kiến trúc và sự kiện

| Câu hỏi | Trả lời ngắn và dẫn chứng |
| --- | --- |
| Tại sao tách DAO interface và implementation? | Service phụ thuộc hợp đồng; implementation chứa SQL. Constructor nhận DAO cho phép thay bằng mock/proxy khi test. Không gộp hai file chỉ vì muốn ít file hơn. |
| Đóng gói nằm ở đâu? | Thuộc tính model được đóng gói qua phương thức truy cập/kiểm tra. `DongNghiepVu` sao chép mảng khi nhận/trả; không đưa mảng nội bộ ra để bên ngoài sửa trực tiếp. |
| Kế thừa và composition? | Panel danh mục/báo cáo kế thừa khung chung vì quan hệ “là một”. Tiện ích `TaoThanhPhanBieuMau` chỉ tạo component nên không kế thừa `JPanel`; service sử dụng DAO là composition. |
| Đa hình khác nạp chồng thế nào? | Gọi qua interface DAO có thể chạy implementation thật hoặc giả là thay thế theo hợp đồng. Nạp chồng chỉ là nhiều chữ ký cùng tên, không đủ để chứng minh cơ chế thay thế đó. |
| Đã đạt SOLID hoàn toàn chưa? | Chưa. Phân tầng/interface hỗ trợ SRP/DIP nhưng còn panel lớn, adapter mảng và service mặc định/static. Nhóm ghi nhận thay vì khẳng định hoàn hảo. |
| Vì sao cần SwingWorker? | EDT phải rảnh để xử lý vẽ và tương tác. `TacVuNen` chạy I/O nền rồi áp kết quả lên UI khi phiên/yêu cầu còn hợp lệ. |
| Hướng sự kiện ở đâu? | Listener Swing xử lý thao tác; `PhatSuKien<T>` thông báo thay đổi cho observer. Giao dịch thông báo sau commit. Đây không phải hệ thống event sourcing hay hàng đợi bền vững. |
| Nếu listener lỗi sau commit? | Giao dịch đã commit vẫn thành công; lỗi listener được log và không chặn listener tiếp theo. Không báo thu tiền thất bại rồi khiến người dùng thực hiện lại. |
| Chỉ ẩn nút có đủ bảo mật? | Không. Service kiểm tra quyền trước khi thực thi; ẩn nút chỉ hỗ trợ UX. Quyền SQL của login lại là lớp bảo vệ môi trường khác. |
| Transaction giải quyết gì? | Gom các bước nghiệp vụ để cùng commit/rollback; savepoint xử lý phạm vi lồng nhau. Không tự mở connection mới cho một bước cần thuộc cùng transaction. |
| Prepared statement có bảo đảm mọi SQL động an toàn? | Nó tham số hóa giá trị. Tên bảng/cột động vẫn cần danh sách kiểm soát; không lấy trực tiếp từ người nhập. |
| Vì sao hai file SQL chạy lại được? | `001` tạo/nâng cấp và kiểm chứng backup trước nâng cấp; `002` bỏ qua nạp mẫu nếu đã có dữ liệu. `-Reset` là lựa chọn riêng để chủ động tạo lại demo/test. |
| Vì sao chưa xóa mọi file dài? | File dài là dấu hiệu cần tách trách nhiệm, không chứng minh không được dùng. Đợt dọn an toàn chỉ gộp phần có bằng chứng và kiểm thử, không đổi kiến trúc lớn. |

Các quy tắc cụ thể về tên, tiền, ngày và kiểm tra tự động nằm trong [CHUAN_CODE.md](CHUAN_CODE.md), không lặp lại đầy đủ ở cẩm nang này.

## 6. Những câu không nên khẳng định khi chưa chứng minh

- Không nói “mọi dòng đều được kiểm thử” hoặc “đã tối ưu toàn bộ giao diện”. Test tự động, quét tĩnh và nghiệm thu thủ công có phạm vi khác nhau.
- Không dùng kết quả CI tháng 10 hoặc số test của đợt trước làm kết quả bản hiện hành. Xem kết quả gần nhất trong [báo cáo rà soát](RA_SOAT_MA_NGUON.md).
- Không mô tả SQL Audit, TDE, Always Encrypted, MAC hoặc phân quyền server như đã triển khai chỉ vì có [ví dụ học tập](SQL_SECURITY_EXAMPLES.md).
- Không suy ra lịch sử lô/giá đã mất; migration ghi nhận dòng cần đối chiếu. Sao lưu không tự làm dữ liệu lịch sử thiếu trở nên đầy đủ.
- Không khẳng định thầy cô yêu cầu đúng giới hạn 80 dòng hoặc cấm record theo chuẩn Java. Đây là lựa chọn quy ước của nhóm.

## 7. Chuẩn bị trước buổi bảo vệ

1. Đọc chuẩn code, chọn một luồng nghiệp vụ mình phụ trách và lần theo file thật từ GUI đến SQL.
2. Chạy build, chuẩn bị cấu hình local và database demo riêng; không đưa mật khẩu lên màn hình trình chiếu.
3. Thử cả đường đi thành công và bị chặn; kiểm tra tên dài, nút cộng/trừ, mở lại phiếu treo và quyền theo vai trò.
4. Chuẩn bị phần giải thích OOP/sự kiện và hạn chế đã biết bằng chính source, không học thuộc một đoạn mô tả không còn đúng.
5. Nộp/đối chiếu [nhật ký sử dụng AI](../AI_USAGE_LOG.md) và [nguồn sản phẩm/ảnh](NGUON_SAN_PHAM.md). Thành viên cần tự hiểu phần code phụ trách; tài liệu này không thay việc đọc code.
