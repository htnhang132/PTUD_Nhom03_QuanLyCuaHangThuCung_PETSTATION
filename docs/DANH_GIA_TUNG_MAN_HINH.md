# Đánh giá từng màn hình của Pet Station

Các nhận xét bên dưới ghi nhận bản trước khi sửa. Xem [kết quả nghiệm thu sau chỉnh sửa](NGHIEM_THU_SAU_CHINH_SUA.md) để đối chiếu trạng thái hiện tại và phạm vi chưa được xác nhận.

Ngày 25/11/2026 · Bản mã `f97c021`. [Kết quả kiểm thử và các lỗi ưu tiên](DANH_GIA_CODE_VA_GIAO_DIEN.md)

Mỗi mục ghi nhận xét cho trang, sau đó liệt kê chữ trong giao diện để người sửa có thể đối chiếu. Tiêu đề mục dùng tên gọi nghiệp vụ dễ tra. “Tên trên thanh tiêu đề” và “Nhãn trên trang” ghi chữ của bản hiện tại. Các nút, nhãn của trạng thái trống, đang sửa, đang tải hoặc tab khác có thể xuất hiện ở những thời điểm khác nhau. Các giá trị bản ghi, tên khách, số tài khoản và danh sách lựa chọn được sinh từ dữ liệu không được lặp lại. Tên nút lịch và nút mắt có thể được cung cấp qua gợi ý khi rê chuột.

| Nhóm | Phạm vi |
| --- | --- |
| [Đăng nhập và điều hướng](#đăng-nhập-và-điều-hướng) | Đăng nhập, khung chính, tổng quan |
| [Hệ thống](#hệ-thống) | Mật khẩu, tài khoản và quyền, cài đặt, nhật ký |
| [Danh mục](#danh-mục) | Khách, thú cưng, nhân viên, sản phẩm, dịch vụ, phòng, nhà cung cấp, khuyến mãi, voucher |
| [Tra cứu](#tra-cứu) | Hóa đơn, lịch sử từng khách, lô hàng, lịch toàn cửa hàng và lịch cá nhân |
| [Bán hàng](#bán-hàng) | Lập hóa đơn, thu tiền, biên lai, đơn nháp, đổi phụ kiện |
| [Spa và lưu trú](#spa-và-lưu-trú) | Điều phối, lập phiếu, lịch hẹn, phòng, hợp đồng, cọc, chăm sóc |
| [Kho và vận hành](#kho-và-vận-hành) | Nhập, xuất hủy, kiểm kê, ca thu ngân, phân ca, phê duyệt |
| [Thống kê](#thống-kê) | Bảy trang báo cáo, 29 chế độ và toàn bộ cột được khai báo cho từng chế độ |
| [Các bước dùng chung panel](#các-bước-dùng-chung-panel) | Tám mã điều hướng spa và lưu trú, nhãn đề nghị cho từng bước |

## Đăng nhập và điều hướng

### Đăng nhập

Giữ “Tên đăng nhập”, “Mật khẩu”, “Hiện mật khẩu” và “Đăng nhập”. Điểm cần sửa đầu tiên là hai ô đã điền sẵn khi mở ứng dụng. Với “Quên mật khẩu?”, nội dung hiện tại hướng người dùng liên hệ quản lý, nên ghi “Cách đặt lại mật khẩu”. Logo đã chứa tên thương hiệu, dòng Pet Station ngay dưới logo lặp lại tên mà lẽ ra vị trí đó có thể dành cho tiêu đề “Đăng nhập”. [DangNhapFrame](../src/main/java/com/petstation/gui/hethong/DangNhapFrame.java)

- Nhãn trên trang · Pet Station · Hệ thống quản lý cửa hàng thú cưng · Tên đăng nhập · Mật khẩu
- Nút và lựa chọn · × · Hiện mật khẩu · Quên mật khẩu? · Đăng nhập

### Khung chính và điều hướng

Ở kích thước tối thiểu 1100 × 680, tên trang nằm ở thanh tiêu đề, phía dưới là menu, thanh công cụ rồi mới đến tiêu đề nội dung. Dùng cùng tên ở hai vị trí, đổi “Màn hình chính” thành “Tổng quan”. Giữ “Quay lại”, “Làm mới”, “Trợ giúp”, phân biệt “Đăng xuất” với “Thoát”. Ba nút cửa sổ dùng ký hiệu – □ × cần tên truy cập “Thu nhỏ”, “Phóng to / Khôi phục”, “Đóng cửa sổ”. Khi có bước tiếp theo, ghi tên đích đến. Menu “Xử lý” chứa cả phân lịch và kho, nên cân nhắc đặt tên “Nghiệp vụ”. [ChinhFrame](../src/main/java/com/petstation/gui/chung/ChinhFrame.java)

Menu gồm Hệ thống, Danh mục, Tìm kiếm, Xử lý, Thống kê. Hệ thống còn có Đăng xuất và Thoát. Thanh trạng thái hiển thị người dùng, vai trò, ca hiện tại, đồng hồ và gợi ý. Giữ F1 cho trợ giúp, F5 cho làm mới, Alt+Home cho tổng quan. “Đã hiểu (Esc)” trong hộp trợ giúp có thể rút thành “Đóng (Esc)”.

- Nhãn trên trang · Người dùng · Vai trò · Ca hiện tại
- Nút và lựa chọn · Hệ thống · Danh mục · Tìm kiếm · Xử lý · Thống kê · Đăng xuất · Thoát · Quay lại · Màn hình chính · Làm mới · Trợ giúp · – · □ · ×

### Tổng quan

“Tổng quan hôm nay” bao cả biểu đồ bảy ngày. Giữ ngày ở góc phải, đổi tiêu đề thành “Tổng quan cửa hàng” và ghi kỳ ngay tại “Sản phẩm bán chạy”. Cột “#” nên là “Hạng” nếu bảng xếp hạng, “Đã bán” thành “Số lượng bán”. “Đặt phòng Hotel” → “Đặt phòng lưu trú”, “Phê duyệt” → “Yêu cầu chờ duyệt”. Giữ phím F2 trên lối tắt lập hóa đơn. [TongQuanPanel](../src/main/java/com/petstation/gui/hethong/TongQuanPanel.java)

**Tên trên thanh tiêu đề** · Màn hình chính

**Gợi ý trên thanh trạng thái** · Chọn một công việc để bắt đầu.

- Nhãn trên trang · Tổng quan hôm nay · Doanh thu 7 ngày qua · Sản phẩm bán chạy
- Cột bảng · # · Sản phẩm · Đã bán · Doanh thu
- Nút và lựa chọn · Lập hóa đơn (F2) · Tạo lịch hẹn Spa · Đặt phòng Hotel · Mở ca · Lập phiếu nhập kho · Phê duyệt

## Hệ thống

### Đổi mật khẩu

Ba ô nằm theo đúng thứ tự thao tác và có chỉ dẫn độ dài. “Xác nhận mật khẩu” → “Nhập lại mật khẩu mới”. “Xóa trắng” → “Xóa nội dung đã nhập”. “Lưu mật khẩu” → “Đổi mật khẩu”. Nút mắt nên đọc “Hiện mật khẩu mới” hoặc “Ẩn mật khẩu mới” theo trạng thái và theo đúng ô được điều khiển. [DoiMatKhauPanel](../src/main/java/com/petstation/gui/hethong/DoiMatKhauPanel.java)

**Tên trên thanh tiêu đề** · Đổi mật khẩu

**Gợi ý trên thanh trạng thái** · Cập nhật mật khẩu tài khoản hiện tại.

- Nhãn trên trang · Đổi mật khẩu · Cập nhật mật khẩu đăng nhập của tài khoản hiện tại · Thông tin mật khẩu · Mật khẩu hiện tại * · Mật khẩu mới * · Xác nhận mật khẩu *
- Nút và lựa chọn · Hiển thị/Ẩn mật khẩu · Xóa trắng · Lưu mật khẩu

### Tài khoản và quyền theo vai trò

Tab “Tài khoản” có cả “Xóa” lẫn “Khóa / Mở khóa”. Dùng đúng một hành động đổi trạng thái với nhãn “Khóa tài khoản” hoặc “Mở khóa tài khoản”. Giữ “Đặt lại mật khẩu” riêng. Đặt nhãn ô là “Mật khẩu mới” và chuyển quy tắc bắt buộc khi tạo vào dòng trợ giúp. Tab “Ma trận quyền” chỉ đọc, nên gọi “Quyền theo vai trò”. Bỏ “(4 vai trò)” khỏi tiêu đề vì các cột đã liệt kê đủ. Tên quyền và vai trò cần đọc được bằng bàn phím cùng trạng thái Được phép hoặc Không được phép. [TaiKhoanPhanQuyenPanel](../src/main/java/com/petstation/gui/hethong/TaiKhoanPhanQuyenPanel.java)

**Tên trên thanh tiêu đề** · Tài khoản & phân quyền

**Gợi ý trên thanh trạng thái** · Tài khoản, vai trò và ma trận quyền (chỉ Cửa hàng trưởng).

**Tab** · Tài khoản · Ma trận quyền

**Tab Tài khoản**

- Nhãn trên trang · Tài khoản · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Tên đăng nhập * · Nhân viên * · Vai trò * · Trạng thái · Mật khẩu mới (tối thiểu 8 ký tự, bắt buộc khi thêm tài khoản)
- Cột bảng · Tên đăng nhập · Nhân viên · Vai trò · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · Hiển thị/Ẩn mật khẩu · Khóa / Mở khóa · Đặt lại mật khẩu · Sửa · Xóa · Hủy · Lưu

**Tab Ma trận quyền**

- Nhãn trên trang · Ma trận quyền theo vai trò (4 vai trò) · Tổng hợp các quyền chức năng mặc định của từng vai trò nhân sự
- Cột bảng · Chức năng · Cửa hàng trưởng · Nhân viên kho · Nhân viên bán hàng · Nhân viên Spa/Hotel

### Cài đặt cửa hàng

Tab cửa hàng có cả máy in, thuế và tài khoản nhận tiền. Đặt nhóm gần nhau theo tác vụ “Thông tin cửa hàng”, “In hóa đơn”, “Nhận chuyển khoản”. “Kho & Hàng hóa” → “Kho hàng”, “kiểm kê mù (Blind Count)” → “Ẩn tồn sổ sách khi đếm”. Tab “Nhân sự & Phân lịch” còn chứa phí trả phòng và thời hạn đổi hàng, nên chuyển hai trường này sang “Chính sách dịch vụ”. “Lưu trữ & Bảo trì” dễ lẫn với dịch vụ lưu trú, dùng “Ảnh và cấu hình”. Nút “Khôi phục thiết lập chuẩn” điền lại một số ô, nên ghi “Đặt lại các thông số mặc định” và nói rõ cần bấm “Lưu cài đặt”. [CaiDatPanel](../src/main/java/com/petstation/gui/hethong/CaiDatPanel.java)

**Tên trên thanh tiêu đề** · Cài đặt

**Gợi ý trên thanh trạng thái** · Cửa hàng, hóa đơn, VAT, ngưỡng tồn, phí trả phòng trễ.

**Tab** · Cửa hàng & Hóa đơn · Kho & Hàng hóa · Nhân sự & Phân lịch · Lưu trữ & Bảo trì

- Nhãn trên trang · Cài đặt hệ thống · Cấu hình thông tin cửa hàng, hóa đơn, kho hàng, nhân sự và bảo trì
- Nút và lựa chọn · Lưu cài đặt

**Tab Cửa hàng & Hóa đơn**

- Nhãn trên trang · Thông tin pháp nhân & Liên hệ · Tên cửa hàng · Địa chỉ cửa hàng · Số điện thoại / Hotline · Email cửa hàng · Website · Mã số thuế · Bán hàng, Thuế & Thiết bị · Thuế VAT (%) · Máy in hóa đơn mặc định · Ngân hàng nhận tiền (VietQR) · Mã BIN ngân hàng VietQR · Số tài khoản nhận tiền · Chủ tài khoản (IN HOA)
- Nút và lựa chọn · In thử trang mẫu

**Tab Kho & Hàng hóa**

- Nhãn trên trang · Cảnh báo tồn kho & Hạn dùng · Ngưỡng tồn thấp cảnh báo · Cảnh báo trước hạn dùng (ngày) · Ngưỡng hàng tồn đọng / chôn vốn (ngày) · Xuất hủy & Kiểm kê · Hạn mức xuất hủy cần duyệt (₫)
- Nút và lựa chọn · Mặc định bật chế độ kiểm kê mù (Blind Count) cho nhân viên kho

**Tab Nhân sự & Phân lịch**

- Nhãn trên trang · Chính sách làm việc & Ca trực · Số giờ làm việc tối đa mỗi tuần (giờ) · Thời gian báo trước khi đổi ca (giờ) · Quy định dịch vụ & Đổi trả · Phí trả phòng lưu trú trễ (₫/ngày) · Thời hạn đổi trả phụ kiện (ngày)

**Tab Lưu trữ & Bảo trì**

- Nhãn trên trang · Đường dẫn dữ liệu · Thư mục lưu trữ hình ảnh chăm sóc thú cưng · Khôi phục cấu hình
- Nút và lựa chọn · Chọn thư mục... · Khôi phục thiết lập chuẩn

### Nhật ký hệ thống

Cột “Hành động” bị cắt ở một số dòng dài, trong khi “Kết quả” còn nhiều chỗ trống. Ưu tiên chiều rộng cho hành động và hiển thị đầy đủ khi chọn dòng. Đổi bộ lọc “Nhân viên” thành “Người thực hiện” nếu dùng chung với cột “Người dùng”. Giữ “Loại sự kiện”, “Kết quả”, “Từ ngày”, “Đến ngày”. Tách số “Cảnh báo” khỏi “Thất bại” khi cần truy sự cố. [NhatKyHeThongPanel](../src/main/java/com/petstation/gui/hethong/NhatKyHeThongPanel.java)

**Tên trên thanh tiêu đề** · Nhật ký hệ thống

**Gợi ý trên thanh trạng thái** · Chỉ đọc: thời gian, người dùng, thao tác, đối tượng.

- Nhãn trên trang · Nhật ký hệ thống · Loại sự kiện: · Kết quả: · Nhân viên: · Khoảng thời gian: · Từ ngày: · Đến ngày:
- Cột bảng · Thời gian · Người dùng · Hành động · Loại sự kiện · Kết quả
- Nút và lựa chọn · Chọn ngày · Xuất Excel

## Danh mục

### Khách hàng

Menu “Khách hàng” đủ ngắn để dùng luôn làm tiêu đề. “Hạng” → “Hạng thành viên”, “Điểm” → “Điểm tích lũy”. Giữ “+ Thêm thú cưng” cạnh hồ sơ đã chọn. Đổi “+ Thêm” thành “Thêm khách hàng” và “Xóa” thành “Ngừng hoạt động”. Trong trạng thái chưa chọn, một dòng “Chọn khách hàng để xem hồ sơ” dễ hiểu hơn cả một cột trường bị khóa. [DanhMucKhachHangPanel](../src/main/java/com/petstation/gui/khachhang/DanhMucKhachHangPanel.java)

**Tên trên thanh tiêu đề** · Khách hàng

**Gợi ý trên thanh trạng thái** · Danh mục khách hàng: thêm, sửa, xóa, lưu.

- Nhãn trên trang · Danh mục khách hàng · Hạng thành viên: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã khách · Họ và tên * · Số điện thoại * · Email · Địa chỉ · Ghi chú · Điểm · Trạng thái
- Cột bảng · Mã khách · Họ và tên · Số điện thoại · Hạng · Điểm · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · + Thêm thú cưng · Sửa · Xóa · Hủy · Lưu

### Hồ sơ thú cưng

Tên trang và “Chủ nuôi” đã rõ. Tên chủ nuôi trong bảng bị rút gọn vì ghép chung mã với tên. Ưu tiên tên, đưa mã vào chi tiết. Ở lịch tiêm, đổi “+ Thêm mũi”, “Cập nhật mũi”, “Xóa mũi” thành “Thêm mũi tiêm”, “Lưu mũi tiêm”, “Xóa mũi tiêm”. “Hết hạn” cần ghi rõ ngày hết hiệu lực của mũi tiêm theo ý nghĩa dữ liệu đã lưu. Nút ảnh nên ghi “Chọn ảnh thú cưng”. Gom lịch tiêm và tiền sử vào nhóm để giảm cuộn. [DanhMucThuCungPanel](../src/main/java/com/petstation/gui/khachhang/DanhMucThuCungPanel.java)

**Tên trên thanh tiêu đề** · Hồ sơ thú cưng

**Gợi ý trên thanh trạng thái** · Danh mục thú cưng, bắt buộc có chủ nuôi.

- Nhãn trên trang · Hồ sơ thú cưng · Loài: · Chủ nuôi: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã thú cưng · Tên thú cưng * · Loài * · Giống * · Giới tính · Ngày sinh · Cân nặng (kg) · Màu lông · Chủ nuôi * · Lịch sử tiêm phòng · Loại vắc-xin · Ghi chú / Phòng khám · Ngày tiêm * · Hết hạn (tùy chọn) · Dị ứng / Tiền sử bệnh lý · Tính cách / Lưu ý chăm sóc · Ảnh đại diện · Trạng thái
- Cột bảng · Mã thú cưng · Tên thú cưng · Loài · Giống · Chủ nuôi · Trạng thái
- Cột bảng · Loại vắc-xin · Ngày tiêm · Hết hạn · Ghi chú
- Nút và lựa chọn · + Thêm · Xuất Excel · Chọn ngày · + Thêm mũi · Cập nhật mũi · Xóa mũi · Chọn ảnh... · Sửa · Xóa · Hủy · Lưu

### Nhân viên

Dùng “Mã nhân viên” thay “Mã NV”. Giữ “Chức vụ” cho hồ sơ nhân sự và “Vai trò” cho quyền tài khoản. Nút “Xóa” thực chất chuyển sang “Nghỉ việc”, nên dùng “Ghi nhận nghỉ việc”. “Đặt lại mật khẩu” ở đây có thể cấp tài khoản, vì vậy cần hiển thị “Cấp tài khoản” với người chưa có tài khoản và “Đặt lại mật khẩu” với người đã có. [DanhMucNhanVienPanel](../src/main/java/com/petstation/gui/nhansu/DanhMucNhanVienPanel.java)

Hộp xác nhận ghi “mật khẩu mặc định”, còn code sinh mật khẩu tạm ngẫu nhiên. Dùng “Cấp mật khẩu tạm” trong câu xác nhận và tránh đặt mật khẩu ở nơi lưu nhật ký.

**Tên trên thanh tiêu đề** · Nhân viên

**Gợi ý trên thanh trạng thái** · Danh mục nhân viên.

- Nhãn trên trang · Danh mục nhân viên · Chức vụ: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã NV · Họ và tên * · Số điện thoại * · CCCD * · Chức vụ * · Ngày vào làm * · Trạng thái
- Cột bảng · Mã NV · Họ và tên · Số điện thoại · Chức vụ · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · Chọn ngày · Đặt lại mật khẩu · Sửa · Xóa · Hủy · Lưu

### Sản phẩm

Nhiều tên hàng dài bị cắt trong bảng dù đây là dữ kiện quan trọng nhất để nhận diện. Dành thêm chiều rộng cho “Tên sản phẩm”, hiển thị tên đầy đủ ở khung chi tiết. Đổi “Mã SP” → “Mã sản phẩm”, “Tạo Barcode” → “Tạo mã vạch”, “Quản lý lô / HSD” → “Theo dõi lô và hạn dùng”, “Xóa” → “Ngừng kinh doanh”. Giữ “Xóa ảnh” vì đối tượng của nút rõ ràng và khác với sản phẩm. [DanhSachSanPhamPanel](../src/main/java/com/petstation/gui/kho/DanhSachSanPhamPanel.java)

**Tên trên thanh tiêu đề** · Sản phẩm

**Gợi ý trên thanh trạng thái** · Danh mục sản phẩm. Tồn kho chỉ cập nhật qua Nhập kho, Xuất kho, Kiểm kê.

- Nhãn trên trang · Danh mục sản phẩm · Nhóm hàng: · Thương hiệu: · Tồn kho: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Tên sản phẩm * · Mã SP · Nhóm hàng * · Đơn vị tính * · Giá bán * · Giá nhập gần nhất · Tồn tối thiểu * · Quản lý lô / HSD · Tồn kho · Trạng thái · Thương hiệu · Mô tả · Mã vạch (Barcode)
- Cột bảng · Mã SP · Tên sản phẩm · Nhóm hàng · Giá bán · Tồn kho · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · Chọn ảnh từ máy… · Xóa ảnh · Tạo Barcode · Sửa · Xóa · Hủy · Lưu

### Dịch vụ spa và bảng giá

Cột “Bảng giá theo loài / cân nặng” dài, giá rút gọn hiển thị theo dạng “Từ …”. Dùng “Giá từ” cho danh sách, giữ bảng mức giá chi tiết ở bên phải. “4 mốc cân” → “Tạo 4 khoảng cân nặng”. “Từ cân” → “Từ (kg)”, “Đến cân (kg, trống = ∞)” → “Đến (kg)” cùng dòng “Để trống nếu không giới hạn”. Giữ “Sửa mức giá” và “Xóa mức giá” vì xác định đúng đối tượng. [DanhMucDichVuSpaPanel](../src/main/java/com/petstation/gui/spa/DanhMucDichVuSpaPanel.java)

Khi cấu hình khoảng cân, hiển thị quy ước có bao gồm cận trên/cận dưới ngay tại bảng. Hai khoảng liền nhau phải cho một giá duy nhất ở đúng mốc cân.

**Tên trên thanh tiêu đề** · Dịch vụ Spa

**Gợi ý trên thanh trạng thái** · Danh mục dịch vụ, giá theo loài và cân nặng.

- Nhãn trên trang · Danh mục dịch vụ Spa · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã DV · Tên dịch vụ * · Mô tả · Thời gian (phút) * · Bảng giá theo loài / cân nặng * · Loài · Giá dịch vụ (₫) · Từ cân (kg) · Đến cân (kg, trống = ∞) · Trạng thái
- Cột bảng · Mã DV · Tên dịch vụ · Thời gian (phút) · Bảng giá theo loài / cân nặng · Trạng thái
- Cột bảng · Loài · Khoảng cân nặng · Giá (₫)
- Nút và lựa chọn · + Thêm · Xuất Excel · + Thêm mức giá · 4 mốc cân · Sửa mức giá · Xóa mức giá · Sửa · Xóa · Hủy · Lưu

### Phòng lưu trú

“Sức chứa (thú)” → “Số thú tối đa”, “Giá / ngày” → “Giá mỗi ngày (₫)”. Bảng có trạng thái Trống, Có thú, Đang dọn, Bảo trì. Sơ đồ dùng “Đang ở” cho phòng có thú, nên chọn một cách gọi xuyên suốt. Nút “Xóa” luôn bị chặn trên danh mục này, nên bỏ khỏi chân biểu mẫu và đưa thao tác bảo trì vào vị trí rõ ràng. [DanhMucPhongHotelPanel](../src/main/java/com/petstation/gui/luutru/DanhMucPhongHotelPanel.java)

**Tên trên thanh tiêu đề** · Phòng lưu trú

**Gợi ý trên thanh trạng thái** · Danh mục phòng. Trạng thái do nghiệp vụ cập nhật, chỉ "bảo trì" chỉnh tay.

- Nhãn trên trang · Danh mục phòng lưu trú · Loại phòng: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã phòng * · Loại phòng * · Sức chứa (thú) * · Giá / ngày * · Ghi chú · Trạng thái
- Cột bảng · Mã phòng · Loại phòng · Sức chứa (thú) · Giá / ngày · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · Sửa · Xóa · Hủy · Lưu

### Nhà cung cấp

Đổi “Mã NCC” → “Mã nhà cung cấp” nếu đủ chỗ, giữ “Người liên hệ” và “Điều khoản thanh toán”. Nút đỏ “Xóa” nên là “Ngừng hợp tác”. Phần thông tin liên hệ nên đặt cùng một nhóm, còn điều khoản thanh toán đặt cạnh thông tin giao dịch để nhân viên nhập hàng tìm được. [DanhMucNhaCungCapPanel](../src/main/java/com/petstation/gui/kho/DanhMucNhaCungCapPanel.java)

**Tên trên thanh tiêu đề** · Nhà cung cấp

**Gợi ý trên thanh trạng thái** · Danh mục nhà cung cấp.

- Nhãn trên trang · Danh mục nhà cung cấp · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã NCC · Tên nhà cung cấp * · Người liên hệ · Số điện thoại * · Địa chỉ · Mã số thuế · Điều khoản thanh toán · Trạng thái · Nhóm mặt hàng cung cấp · Ghi chú
- Cột bảng · Mã NCC · Tên nhà cung cấp · Người liên hệ · Số điện thoại · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · Sửa · Xóa · Hủy · Lưu

### Chương trình khuyến mãi

Nhiều bộ lọc ép nút thêm và xuất xuống hàng thứ hai, trong khi tên chương trình và giá trị bị rút gọn. Ưu tiên tên và mức giảm, để các điều kiện phụ vào chi tiết. “Loại” → “Hình thức giảm”, “Giá trị” → “Mức giảm” với đơn vị % hoặc ₫ theo từng dòng. “Hiệu lực” nói về ngày áp dụng, “Trạng thái” nói về bật/tắt chương trình. “Xóa” → “Ngừng áp dụng”. [DanhMucKhuyenMaiPanel](../src/main/java/com/petstation/gui/khachhang/DanhMucKhuyenMaiPanel.java)

**Tên trên thanh tiêu đề** · Khuyến mãi

**Gợi ý trên thanh trạng thái** · Danh mục chương trình khuyến mãi.

- Nhãn trên trang · Danh mục khuyến mãi · Loại giảm: · Phạm vi: · Hiệu lực: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã KM · Tên chương trình * · Loại · Phạm vi · Giá trị * · Đơn tối thiểu · Từ ngày * · Đến ngày * · Trạng thái
- Cột bảng · Mã KM · Tên chương trình · Loại · Phạm vi · Giá trị · Từ ngày · Đến ngày · Trạng thái
- Nút và lựa chọn · + Thêm · Xuất Excel · Chọn ngày · Sửa · Xóa · Hủy · Lưu

### Voucher và điểm thưởng

Tab voucher có chín cột, mã và tên người sở hữu cùng bị cắt. Ưu tiên mã, mệnh giá, lượt còn lại, hạn dùng. “Số lượt” → “Giới hạn lượt dùng”, “Đã dùng” → “Lượt đã dùng”. Khi chọn nhiều khách, ghi phạm vi của “Chọn tất cả”. Tab quy tắc nên gọi “Tích điểm và hạng thành viên”. “Chi tiêu bao nhiêu đồng được 1 điểm” → “Chi tiêu để nhận 1 điểm (₫)”. Hiển thị quy đổi cố định dưới dạng văn bản để tránh nhầm là thông số sửa được. [TichDiemVoucherPanel](../src/main/java/com/petstation/gui/khachhang/TichDiemVoucherPanel.java)

**Tên trên thanh tiêu đề** · Voucher và điểm thưởng

**Gợi ý trên thanh trạng thái** · Voucher, quy tắc điểm và hạng thành viên.

**Tab** · Voucher · Quy tắc điểm & hạng

**Tab Voucher**

- Nhãn trên trang · Danh mục voucher · Chủ sở hữu: · Phạm vi: · Trạng thái: · Chọn một dòng hoặc bấm + Thêm · Mã voucher * · Giá trị (₫) * · Hạn dùng * · Số lượt · Đã dùng · Đơn tối thiểu (₫) · Trạng thái · Khách hàng sở hữu * · Phạm vi
- Cột bảng · Mã voucher · Giá trị (₫) · Hạn dùng · Số lượt · Đã dùng · Đơn tối thiểu (₫) · Trạng thái · Khách hàng sở hữu · Phạm vi
- Nút và lựa chọn · + Thêm · Xuất Excel · Chọn ngày · Chọn tất cả · Sửa · Xóa · Hủy · Lưu

**Tab Quy tắc điểm & hạng**

- Nhãn trên trang · Quy tắc tích điểm và hạng thành viên · Chi tiêu bao nhiêu đồng được 1 điểm · 200 điểm đổi voucher Spa (hạn 60 ngày) · Hạng thành viên (theo tổng chi tiêu từ):
- Nút và lựa chọn · Lưu quy tắc

## Tra cứu

### Tra cứu hóa đơn

Dòng hướng dẫn “Chỉ xem và in lại” mâu thuẫn với các nút đổi phụ kiện và yêu cầu sửa/hủy. Viết “Chọn hóa đơn để xem, in, đổi phụ kiện hoặc gửi yêu cầu điều chỉnh”. “Thanh toán” ở cột chứa Tiền mặt, Chuyển khoản → “Phương thức”. “Xuất file PDF” → “Xuất PDF”, “Đổi phụ kiện từ HĐ này” → “Đổi phụ kiện”, “Yêu cầu hủy / sửa HĐ” → “Yêu cầu sửa hoặc hủy hóa đơn”. Giữ mã của hóa đơn đã chọn trên đầu phần chi tiết. [TimKiemHoaDonPanel](../src/main/java/com/petstation/gui/banhang/TimKiemHoaDonPanel.java)

Nhánh “sửa” thực tế nhập ghi chú mới và lý do gửi duyệt. Đặt lựa chọn “Yêu cầu sửa ghi chú” bên cạnh “Yêu cầu hủy hóa đơn” để tránh bị hiểu là sửa hàng, giá hoặc số tiền trên hóa đơn.

**Tên trên thanh tiêu đề** · Tìm kiếm hóa đơn

**Gợi ý trên thanh trạng thái** · Lọc theo ngày, mã HĐ, khách, thu ngân, trạng thái. Chỉ xem và in lại.

- Nhãn trên trang · Tìm kiếm hóa đơn · Từ ngày: · Đến ngày: · Phương thức: · Trạng thái:
- Cột bảng · Số hóa đơn · Khách hàng · Thu ngân · Thời gian · Tổng tiền · Thanh toán · Trạng thái
- Nút và lựa chọn · Chọn ngày · Xuất Excel · In lại hóa đơn · Xuất file PDF · Đổi phụ kiện từ HĐ này · Yêu cầu hủy / sửa HĐ

### Lịch sử khách hàng

Nút “Xem lịch sử” là đường vào rõ ràng, giữ cả cách mở bằng nhấp đúp. “Hạng thẻ” nên đồng bộ với “Hạng thành viên” ở danh mục. Sau khi vào lịch sử, giữ “Chọn khách khác” tại vị trí dễ thấy. Dùng “Mã khách hàng” và “Điểm tích lũy” xuyên suốt cả hai màn hình. [LichSuKhachHangPanel](../src/main/java/com/petstation/gui/khachhang/LichSuKhachHangPanel.java)

**Tên trên thanh tiêu đề** · Lịch sử khách hàng

**Gợi ý trên thanh trạng thái** · Hóa đơn, phiếu Spa, lưu trú, thú cưng, điểm.

- Nhãn trên trang · Lịch sử khách hàng · Hạng thẻ: · Điểm:
- Cột bảng · Mã KH · Họ và tên · Số điện thoại · Địa chỉ · Hạng thẻ · Điểm
- Nút và lựa chọn · Xem lịch sử · Xuất Excel · Chọn khách khác

### Chi tiết lịch sử một khách hàng

Năm tab cần giữ ngữ cảnh cùng một khách. Dùng “Tên thú cưng” thay “Tên”, “Cân nặng (kg)” thay “Cân nặng”, “Phương thức” thay “Thanh toán” ở tab hóa đơn, “Mã hợp đồng” thay “Hợp đồng”. “Xem / In hóa đơn” mở chứng từ trước, nên ghi “Xem hóa đơn” rồi đặt nút in ở bản xem. “Xuất Excel” trên từng tab nên có gợi ý nêu tên tập dữ liệu sẽ xuất. [ChiTietKhachHangPanel](../src/main/java/com/petstation/gui/khachhang/ChiTietKhachHangPanel.java)

**Tab** · Thú cưng · Hóa đơn · Spa · Lưu trú · Voucher cá nhân

**Tab Thú cưng**

- Cột bảng · Mã thú · Tên · Loài · Giống · Ngày sinh · Cân nặng · Tiêm chủng · Lưu ý sức khỏe
- Nút và lựa chọn · Xuất Excel

**Tab Hóa đơn**

- Cột bảng · Số hóa đơn · Ngày · Tổng tiền · Thanh toán · Trạng thái
- Nút và lựa chọn · Xem / In hóa đơn · Xuất Excel

**Tab Spa**

- Cột bảng · Mã phiếu · Thú cưng · Ngày · Tổng tiền · Trạng thái
- Nút và lựa chọn · Xuất Excel

**Tab Lưu trú**

- Cột bảng · Hợp đồng · Thú cưng · Phòng · Ngày vào · Ngày ra dự kiến · Trạng thái
- Nút và lựa chọn · Xuất Excel

**Tab Voucher cá nhân**

- Cột bảng · Mã voucher · Mệnh giá · Hạn dùng · Đã dùng · Số lượng · Trạng thái
- Nút và lựa chọn · Xuất Excel

### Tồn kho theo lô

Bảng trên màn hình hiển thị “Mã sản phẩm” nhưng mô hình bảng vẫn gọi cột là “Sản phẩm”. Xuất Excel lấy tên từ mô hình, khiến cùng cột có hai nhãn. Dùng một định nghĩa cột cho cả hai. “Tồn kho” → “Tồn của lô”. “Xuất hủy lô này” mới mở biểu mẫu chọn số lượng, nên ghi “Lập phiếu xuất hủy”. Giữ tên lô và hạn dùng cạnh nhau khi xác nhận. [TimKiemTonTheoLoPanel](../src/main/java/com/petstation/gui/kho/TimKiemTonTheoLoPanel.java)

**Tên trên thanh tiêu đề** · Tồn kho theo lô

**Gợi ý trên thanh trạng thái** · Số lô, hạn dùng, tồn. Vàng: sắp hết hạn, đỏ: hết hạn.

- Nhãn trên trang · Tồn kho theo lô · Trạng thái:
- Cột bảng · Mã sản phẩm · Tên sản phẩm · Số lô · Hạn sử dụng · Tồn kho · Trạng thái
- Nút và lựa chọn · Xuất Excel · Xuất hủy lô này

### Lịch làm việc của cửa hàng

Lưới tuần nhóm ca và nhân viên dễ đọc hơn bảng bản ghi. “Hiện tại” → “Tuần này”, “Lọc NV” → “Lọc nhân viên”. Thay “< Tuần trước” và “Tuần sau >” bằng chữ và biểu tượng mũi tên riêng. Chú thích “Viền đỏ = ca thiếu người” cần thêm chữ “Thiếu người” ngay trong ô cho người khó phân biệt màu. [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java)

**Tên trên thanh tiêu đề** · Lịch làm việc

**Gợi ý trên thanh trạng thái** · Lịch toàn cửa hàng theo tuần: ca Sáng, Chiều, Tối.

- Nhãn trên trang · Lịch làm việc theo tuần · Lọc NV: · Nhân viên bán hàng · Nhân viên Spa/Hotel · Nhân viên kho · Nghỉ phép · Viền đỏ = ca thiếu người
- Nút và lựa chọn · Tất cả · Ca làm việc · Nghỉ phép · ‹ Tuần trước · Chọn ngày · Tuần sau › · Hiện tại · In lịch · Xuất Excel

### Lịch làm việc của tôi

Giữ “Đăng ký nghỉ phép”, “Yêu cầu đổi ca”. Tách rõ ba tình huống “Tuần này bạn chưa được xếp ca”, “Chưa liên kết hồ sơ nhân viên” và lỗi tải. Tóm tắt cộng giờ trong tháng còn lưới hiển thị một tuần. Đặt rõ tháng bên cạnh số giờ, giữ “Nghỉ phép tuần này” bên cạnh số ca nghỉ. Thu gọn chú giải vai trò ở trang chỉ xem lịch một người. [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java)

Hộp nghỉ phép có Từ ngày, Đến ngày, Lý do, Gửi yêu cầu, Hủy. Hộp đổi ca có Ca của bạn, Đồng nghiệp, Ca đồng nghiệp và Lý do đổi ca. Đặt nút gửi lần lượt là “Gửi yêu cầu nghỉ phép” và “Gửi yêu cầu đổi ca”. Giữ riêng lựa chọn nhường ca khi không đổi lại. [Biểu mẫu nghỉ phép](../src/main/java/com/petstation/gui/nhansu/ThaoTacNhanh.java)

**Tên trên thanh tiêu đề** · Lịch làm việc cá nhân

**Gợi ý trên thanh trạng thái** · Ca của bạn, ngày nghỉ, tổng giờ trong tháng.

- Nhãn trên trang · Lịch làm việc cá nhân theo tuần · Nhân viên bán hàng · Nhân viên Spa/Hotel · Nhân viên kho · Nghỉ phép · Viền đỏ = ca thiếu người
- Nút và lựa chọn · Tất cả · Ca làm việc · Nghỉ phép · ‹ Tuần trước · Chọn ngày · Tuần sau › · Hiện tại · Đăng ký nghỉ phép · Yêu cầu đổi ca · In lịch · Xuất Excel

## Bán hàng

### Lập hóa đơn

“Lập hóa đơn (POS)” và “QUÉT MÃ VẠCH SẢN PHẨM (POS)” lặp thuật ngữ máy bán hàng. Dùng “Lập hóa đơn” và “Quét mã hoặc tìm sản phẩm”. “Mã SP / Barcode” → “Mã sản phẩm / mã vạch”, “SL” → “Số lượng”. “TIẾN HÀNH THANH TOÁN (F9)” → “Sang thanh toán (F9)” để phân biệt với bước ghi tiền. Giữ “Thêm vào đơn (Enter)”, ghi “Lưu đơn nháp” và “Mở đơn nháp”. “USB: chưa khởi tạo” nên nói “Chưa kết nối máy quét” khi đúng với trạng thái thiết bị. [LapHoaDonPanel](../src/main/java/com/petstation/gui/banhang/LapHoaDonPanel.java)

**Tên trên thanh tiêu đề** · Lập hóa đơn

**Gợi ý trên thanh trạng thái** · Quét mã hoặc nhập mã sản phẩm, rồi Enter.

- Nhãn trên trang · Lập hóa đơn (POS) · QUÉT MÃ VẠCH SẢN PHẨM (POS) · SL: · USB: chưa khởi tạo. · Sẵn sàng quét mã vạch (Ctrl+K tìm nhanh · F5 tải kho) · Chi tiết mặt hàng trong hóa đơn · 0 mặt hàng · 0 sản phẩm · Thông tin thanh toán · Khách hàng (SĐT, mã hoặc tên — Enter để tìm; bỏ trống = khách lẻ) · Đổi 200 điểm → voucher Spa 150.000₫ / 60 ngày · Tạm tính tiền hàng · Khuyến mãi tự áp · Giảm giá khuyến mãi · Ghi chú đơn hàng (phân biệt khi lưu tạm) · TỔNG CỘNG
- Cột bảng · STT · Mã SP / Barcode · Tên hàng hóa / Dịch vụ · Đơn giá · Số lượng · Thành tiền · Xóa
- Nút và lựa chọn · Thêm vào đơn (Enter) · Xóa toàn bộ giỏ (Alt+X) · Tìm · Đổi voucher · Bỏ chọn · Lưu đơn tạm (Ctrl+S) · Mở đơn tạm (F8) · TIẾN HÀNH THANH TOÁN (F9)

### Thanh toán

Bố cục hai cột giúp đối chiếu hóa đơn với số tiền thu. Giữ “Tiền khách đưa”, “Tiền thừa trả khách”, “Đủ tiền”, số phải thu và nút cuối ở đáy. “Chuyển khoản / QR” → “Chuyển khoản bằng mã QR”. “Tìm” → “Tìm khách hàng”, “Áp dụng” → “Áp dụng voucher”. Phân tách tiền giảm do khuyến mãi và voucher khi cùng phát sinh. Khi đơn trống, dùng “Về lập hóa đơn” thay “Về lập đơn hàng”. Chỉ hiện lời xác nhận tương ứng với tiền mặt, thẻ hoặc giao dịch chuyển khoản. [ThanhToanPanel](../src/main/java/com/petstation/gui/banhang/ThanhToanPanel.java)

Khi có đơn tiền mặt, bảng gồm Sản phẩm, SL, Đơn giá, Thành tiền. Có nút “Đủ tiền” và các mức gợi ý theo giá trị đơn. Với QR, giữ “Sao chép nội dung” cạnh đúng mã chuyển khoản. Có thể rút lời xác nhận xuống “Đã kiểm tra tiền vào tài khoản” và tách phím tắt ra tooltip. Với thẻ, dùng “Đã xác nhận giao dịch thẻ thành công”.

**Tên trên thanh tiêu đề** · Thanh toán

**Gợi ý trên thanh trạng thái** · Chọn phương thức và xác nhận thanh toán.

- Nhãn trên trang · Thanh toán · Kiểm tra hóa đơn, chọn phương thức và xác nhận. · Hóa đơn · Tạm tính · Giảm giá và voucher · Cần thanh toán · Phương thức thanh toán · Khách hàng (SĐT, mã hoặc tên — Enter để tìm; bỏ trống = khách lẻ) · Đổi 200 điểm → voucher Spa 150.000₫ / 60 ngày · Mã voucher · Tiền khách đưa · Quét mã bằng ứng dụng ngân hàng · Nội dung chuyển khoản · Chưa xác nhận giao dịch. Thu ngân cần kiểm tra tài khoản nhận tiền. · Chưa có hóa đơn để thanh toán · Hãy thêm sản phẩm ở màn Lập đơn hàng trước.
- Cột bảng · Sản phẩm · SL · Đơn giá · Thành tiền
- Nút và lựa chọn · Tiền mặt · Chuyển khoản / QR · Thẻ · Tìm · Đổi voucher · Bỏ chọn · Áp dụng · Chọn voucher của khách · Sao chép nội dung · Tôi đã kiểm tra và nhận đủ tiền · Quay lại sửa đơn · Xác nhận thanh toán (F9) · Về lập đơn hàng · Đủ tiền · Các mức tiền gợi ý theo đơn

### Xem và in hóa đơn

Trạng thái trống đã giải thích lúc nào có hóa đơn. “Về lập đơn hàng” và “Lập hóa đơn mới (F2)” nên thống nhất danh từ “hóa đơn”. Giữ “In hóa đơn (Ctrl+P)”. Ở đây lưu PDF phụ thuộc lựa chọn máy in, khác với nút xuất PDF trực tiếp ở trang tra cứu. Gợi ý nên ghi “In hóa đơn hoặc chọn máy in PDF nếu máy có hỗ trợ”. [InHoaDonPanel](../src/main/java/com/petstation/gui/banhang/InHoaDonPanel.java)

Trên biên lai, “KH”, “NVBH”, “ĐG”, “TT” khó đọc với khách. Ưu tiên “Khách hàng”, “Thu ngân”, “Đơn giá”, “Thành tiền”, kiểm tra trên đúng khổ giấy. Giữ phân biệt “Phải thu sau cọc”, “Cọc đã cấn trừ”, “Tổng thanh toán” và “Trả lại khách”. [Mẫu biên lai](../src/main/java/com/petstation/gui/banhang/HoaDonDocPanel.java)

**Tên trên thanh tiêu đề** · Xem hóa đơn

**Gợi ý trên thanh trạng thái** · Xem trước, in hoặc xuất PDF.

- Nhãn trên trang · Xem hóa đơn · Chưa có hóa đơn nào để in · Hóa đơn xuất hiện ở đây sau khi thanh toán.
- Nút và lựa chọn · Lập hóa đơn mới (F2) · In hóa đơn (Ctrl+P) · Về lập đơn hàng

### Đơn nháp

Cùng đối tượng có ít nhất ba tên “Đơn nháp”, “Đơn đang treo”, “đơn tạm”. Chọn “Đơn nháp” ở tiêu đề và tất cả nút. “Mở lại đơn” → “Tiếp tục xử lý”, “Xóa đơn treo” → “Xóa đơn nháp”. “Số dòng” → “Số dòng hàng”. Thay “Chưa có dữ liệu để hiển thị” bằng “Chưa có đơn nháp. Dùng Lưu đơn nháp khi lập hóa đơn”. [DonHangTreoPanel](../src/main/java/com/petstation/gui/banhang/DonHangTreoPanel.java)

**Tên trên thanh tiêu đề** · Đơn nháp

**Gợi ý trên thanh trạng thái** · Danh sách đơn treo.

- Nhãn trên trang · Đơn đang treo
- Cột bảng · Mã đơn · Khách hàng · Người lập · Ngày tạo · Số dòng · Tạm tính · Loại đơn
- Nút và lựa chọn · Xuất Excel · Mở lại đơn · Xóa đơn treo

### Đổi phụ kiện

Menu ghi “Đổi hàng”, màn hình ghi “Đổi trả hàng”, hộp thoại lại quy định “không hoàn tiền”. Dùng “Đổi phụ kiện” cho thao tác mới và “Lịch sử đổi hàng” cho danh sách. Cột “Thu chênh lệch / lịch sử hoàn” đang gộp hai nghĩa, nên có “Loại giao dịch” và “Số tiền”. Giữ khả năng đọc chứng từ hoàn cũ. “Xem HĐ thay thế” → “Xem hóa đơn thay thế”. [DoiTraPanel](../src/main/java/com/petstation/gui/banhang/DoiTraPanel.java)

Menu chuột phải còn có “+ Đổi phụ kiện mới”, “Xem hóa đơn gốc”, “Xem hóa đơn thay thế”. Đồng bộ nhãn menu với nút đầu trang. Ở khung chi tiết, giữ “Giá trị hàng đổi” và “Khách trả thêm” ở hai dòng riêng.

**Tên trên thanh tiêu đề** · Đổi hàng

**Gợi ý trên thanh trạng thái** · Tìm hóa đơn gốc và đổi phụ kiện, thu chênh lệch.

- Nhãn trên trang · Đổi trả hàng · Trạng thái: · Chi tiết đổi trả · Hoàn tất · Hóa đơn gốc · Hóa đơn thay thế · Nhân viên lập · Hàng khách trả lại · Phụ kiện thay thế · Giá trị hàng đổi · Khách trả thêm · 10.000₫ · Lý do đổi trả
- Cột bảng · Mã phiếu · Hóa đơn gốc · Hóa đơn thay thế · Thu chênh lệch / lịch sử hoàn · Lý do · Nhân viên lập · Trạng thái · Ngày tạo
- Nút và lựa chọn · Xuất Excel · + Đổi phụ kiện (F7) · Xem hóa đơn gốc · Xem HĐ thay thế

### Lập phiếu đổi phụ kiện

Ba bước thể hiện thứ tự hợp lý. Giữ hai điều kiện hàng đổi và phần khách trả thêm. Dùng “Số lượng còn được đổi” thay “Còn đổi”, “Giá mua ban đầu” thay “Giá bán gốc”. “Treo đơn đổi” → “Lưu phiếu đổi nháp”, “Tiếp tục thanh toán” → “Sang thanh toán”. Con số chênh lệch phải nổi bật hơn hướng dẫn về điểm khi chọn xong hai mặt hàng. [DoiPhuKienDialog](../src/main/java/com/petstation/gui/banhang/DoiPhuKienDialog.java)

- Nhãn trên trang · Đổi phụ kiện · 1. Chọn hóa đơn gốc · 2. Chọn hàng cần đổi · Số lượng đổi · Lý do · 3. Chọn phụ kiện thay thế · Số lượng thay thế · Tổng tiền hàng thay thế phải bằng hoặc lớn hơn hàng trả. · Tổng kết đổi hàng · Chưa chọn hàng đổi và hàng thay thế. · Chọn hóa đơn hoặc nhập mã hóa đơn / SĐT.
- Cột bảng · Hóa đơn · Khách hàng · Ngày lập · Tổng tiền
- Cột bảng · Sản phẩm · Nhóm · Đã mua · Đã đổi · Còn đổi · Giá bán gốc
- Cột bảng · Mã phụ kiện · Tên phụ kiện · Đơn giá · Tồn khả dụng
- Nút và lựa chọn · Tìm hóa đơn · Nguyên tem mác và bao bì · Chưa qua sử dụng, không rách bẩn · Tìm phụ kiện · Hủy · Treo đơn đổi · Tiếp tục thanh toán

## Spa và lưu trú

### Quản lý spa

Tab điều phối có “Bắt đầu làm”, “Hoàn thành”, “Thanh toán”. Giữ ba bước này, ghi “Bắt đầu dịch vụ” cho rõ. Ở tab lập phiếu, “+ Khách”, “+ Thú”, “+ Thêm gói” nên là “Thêm khách hàng”, “Thêm thú cưng”, “Thêm dịch vụ”. “Xóa hết” phải nói rõ chỉ xóa danh sách dịch vụ, “Xóa trắng” phải nói rõ đặt lại cả phiếu. “Tạo & Bắt đầu làm ngay” → “Tạo phiếu và bắt đầu dịch vụ”. Khối sức khỏe mặc định báo “Bình thường — Đủ điều kiện tiếp nhận” ngay cả khi chưa chọn thú, nên dùng “Chưa đánh giá” cho tới khi nhân viên xác nhận. Tab lịch hẹn nên giữ “Khách đã đến” và “Hủy lịch” riêng. [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java)

Menu theo trạng thái có Bắt đầu làm, + Dịch vụ, Xóa dịch vụ, Hoàn thành, Thanh toán, Chi tiết dịch vụ, Phiếu chăm sóc và Xem phiếu chăm sóc. “Xóa dịch vụ” nên là “Gỡ dịch vụ khỏi phiếu”. Bảng chi tiết gồm Dịch vụ, Đơn giá, Số lượng, Thành tiền. Khi chuyển sang thanh toán mà giỏ hiện có hàng, cho biết giỏ nào sẽ được làm trống trước khi xác nhận.

**Tên trên thanh tiêu đề** · Quản lý Spa

**Gợi ý trên thanh trạng thái** · Tra cứu, tiếp nhận, lập phiếu nhiều dịch vụ, theo dõi tiến độ và thanh toán Spa.

**Tab** · Điều phối phiếu Spa · Lập phiếu mới · Lịch hẹn Spa

**Tab Điều phối phiếu Spa**

- Nhãn trên trang · Phiếu Spa · Từ ngày: · Đến ngày: · Trạng thái:
- Cột bảng · Mã phiếu · Khách hàng · Thú cưng · Cân nặng (kg) · Nhân viên · Tổng tiền · Trạng thái · Tiếp nhận lúc
- Nút và lựa chọn · Xuất Excel · Chọn ngày · + Lập phiếu · Bắt đầu làm · Hoàn thành · Thanh toán · + Dịch vụ · Thao tác khác

**Tab Lập phiếu mới**

- Nhãn trên trang · Khách hàng & Thú cưng · Khách đến trực tiếp · Khách hàng * · Đã chọn · Chưa chọn khách hàng · Thú cưng * · Cân (kg) * · CHƯA CHỌN THÚ CƯNG · Chọn thú cưng để kiểm tra điều kiện tuổi và tiêm phòng. · Kiểm tra sức khỏe khi nhận · Mặc định: Bình thường — Đủ điều kiện tiếp nhận · Da & Lông · Ký sinh trùng · Mắt & Tai · Độ rối lông · Kết luận quầy · Ghi chú · Dịch vụ Spa · Chọn thú cưng, dịch vụ và cân nặng hợp lệ để xem giá. · Nếu bảng trống, hệ thống tự dùng gói đang chọn ở trên. · Yêu cầu khách hàng & Vật dụng mang theo · Tổng ước tính:
- Cột bảng · Gói dịch vụ · Đơn giá · (ô thao tác không có tiêu đề)
- Nút và lựa chọn · Chọn lịch hẹn · Tìm · + Khách · + Thú · Sổ tiêm · Chi tiết · + Thêm gói · Xóa hết · Xóa trắng · Mở phiếu tạm (0) · Lưu phiếu tạm · Tạo phiếu · Tạo & Bắt đầu làm ngay

**Tab Lịch hẹn Spa**

- Nhãn trên trang · Lịch hẹn Spa · Ngày: · Nhân viên: · Trạng thái:
- Cột bảng · Mã lịch · Khách hàng · Thú cưng · Dịch vụ · Nhân viên · Bắt đầu · Kết thúc · Trạng thái
- Nút và lựa chọn · Xuất Excel · Chọn ngày · + Đặt lịch hẹn · Khách đã đến · Sửa lịch · Hủy lịch

### Đặt hoặc sửa lịch hẹn spa

“Thợ phụ trách” và “Nhân viên” ở danh sách là cùng một người, nên dùng “Nhân viên phụ trách”. “Thời gian hẹn” bên cạnh ô “Giờ” → “Ngày hẹn” và “Giờ hẹn”. Dùng “Thêm khách hàng”, “Thêm thú cưng” thay hai nhãn rút gọn. Dữ liệu mẫu mở ra đã chọn sẵn khách, thú và nhân viên đầu tiên. Yêu cầu người đặt chủ động chọn khách hoặc chỉ điền sẵn khi mở từ đúng hồ sơ. Giữ “Đặt lịch hẹn” cho tạo mới và “Lưu thay đổi” cho sửa. [DatLichSpaDialog](../src/main/java/com/petstation/gui/spa/DatLichSpaDialog.java)

- Nhãn trên trang · Đặt lịch hẹn dịch vụ Spa · Chọn khách hàng, thú cưng và gói dịch vụ để lên lịch tiếp đón · Khách hàng: · Thú cưng: · Gói dịch vụ Spa: · Thợ phụ trách: · Thời gian hẹn: · Giờ: · Ghi chú:
- Nút và lựa chọn · + Khách · + Thú cưng · Chọn ngày · Hủy · Đặt lịch hẹn

### Quản lý lưu trú

Sơ đồ phòng có cả chữ lẫn màu, giữ cách này. Phân biệt “Đặt phòng” ở đầu trang với “Đặt phòng này” bằng cách đưa mã phòng vào nút của phòng đang chọn. Tab “Hợp đồng lưu trú” lại mở đầu bằng “Lưu trú Pet Hotel”, nên giữ một tiêu đề. Đặt “Cọc yêu cầu” cạnh “Cọc đã nhận”. “Quyết toán” nên ghi “Tính tiền trả phòng” nếu bước kế mở thanh toán. Ưu tiên các động từ tiếng Việt trong menu theo trạng thái phòng. [LuuTruHotelPanel](../src/main/java/com/petstation/gui/luutru/LuuTruHotelPanel.java)

Menu theo trạng thái còn có Xem nhật ký chăm sóc, Lịch sử thu cọc, Điều chỉnh mức cọc, Sửa thông tin đặt phòng, Hủy đặt phòng, Xác nhận dọn phòng xong, Nhận phòng (Check-in), Thu cọc phòng, Phiếu chăm sóc (Xem / Thêm), Gia hạn phòng và Trả phòng & Quyết toán (POS). Dùng “Nhận phòng”, “Gia hạn lưu trú”, “Trả phòng và tính tiền”. Lịch sử thu cọc gồm Phương thức, Thực thu, Khách đưa, Tiền thừa, Lúc nhận, Ca. Giữ “Thực thu” và đổi “Lúc nhận” thành “Thời điểm thu”. Điều chỉnh mức cọc cần giữ hai trường mức yêu cầu mới và lý do.

**Tên trên thanh tiêu đề** · Quản lý lưu trú

**Gợi ý trên thanh trạng thái** · Sơ đồ phòng trực quan, đặt phòng, thu cọc, chăm sóc và trả phòng.

**Tab** · Sơ đồ phòng lưu trú · Hợp đồng lưu trú

**Tab Sơ đồ phòng lưu trú**

- Nhãn trên trang · Sơ đồ phòng lưu trú · Loại phòng: · Trạng thái: · Tiêu chuẩn · Trống · Sẵn sàng đón khách · Đang ở · Đang dọn · Chờ xác nhận dọn phòng · VIP · Bảo trì · Tạm ngưng phục vụ · Penthouse
- Nút và lựa chọn · + Đặt phòng · In sơ đồ · + Đặt phòng này

**Tab Hợp đồng lưu trú**

- Nhãn trên trang · Lưu trú Pet Hotel · Trạng thái:
- Cột bảng · Mã hợp đồng · Khách hàng · Thú cưng · Phòng · Ngày vào · Ngày ra dự kiến · Cọc đã nhận · Giá / ngày · Trạng thái · Cọc yêu cầu
- Nút và lựa chọn · + Đặt phòng · Thu cọc · Nhận phòng · Phiếu chăm sóc · Quyết toán · Thao tác khác · Xuất Excel

### Đặt phòng lưu trú

Đổi tiêu đề in hoa “THÔNG TIN ĐẶT PHÒNG” thành “Đặt phòng lưu trú”. Giữ cụm chi phí dự kiến gồm số ngày, tiền phòng và cọc yêu cầu (50%). Chữ “Lưu đặt phòng” → “Xác nhận đặt phòng”. Khi phòng chưa tải xong nên giữ thông báo tải ngay gần ô Phòng. Phân biệt “Cọc yêu cầu” ở đây với “Cọc đã nhận” tại hợp đồng. [DatPhongHotelDialog](../src/main/java/com/petstation/gui/luutru/DatPhongHotelDialog.java)

- Nhãn trên trang · THÔNG TIN ĐẶT PHÒNG · Khách hàng * · Thú cưng * · Phòng * · Ngày/giờ nhận phòng * · Giờ: · Ngày/giờ trả phòng * · Ghi chú · Dự kiến chi phí · Số ngày tính phí: · Tổng tiền phòng: · Tiền cọc yêu cầu (50%):
- Nút và lựa chọn · + Khách · + Thú cưng · Chọn ngày · Hủy · Lưu đặt phòng

### Thu tiền cọc lưu trú

“Thu tiền cọc Hotel” → “Thu tiền cọc lưu trú”. Ô phương thức hiển thị nguyên TIEN_MAT, các lựa chọn còn lại là mã enum. Hiển thị “Tiền mặt”, “Chuyển khoản”, “Thẻ ngân hàng”. “Còn thiếu tối đa” → “Tiền cọc còn phải thu”, “Tiền trả lại” → “Tiền thừa trả khách”. Giữ “Xác nhận thu cọc” và xác nhận riêng cho giao dịch thẻ/chuyển khoản. Các ô tiền ban đầu đang hiện số liền, nên định dạng hàng nghìn khi rời ô. [ThuTienDialog](../src/main/java/com/petstation/gui/banhang/ThuTienDialog.java)

- Nhãn trên trang · Còn thiếu tối đa: · 150.000₫ · Số tiền thu lần này: · Phương thức: · Tiền khách đưa: · Tiền trả lại:
- Nút và lựa chọn · Đã kiểm tra tiền vào đúng tài khoản ngân hàng · Hủy · Xác nhận thu cọc

### Phiếu chăm sóc thú cưng

Bố cục ghi nhận bên trái, lịch sử bên phải phù hợp thao tác giao ca. “Thuốc & chỉ định từ chủ nuôi” → “Thuốc và hướng dẫn của chủ nuôi”. Hai nút “Xóa” sát các nút chọn ảnh nên đặt rõ “Bỏ ảnh trước”, “Bỏ ảnh sau”. Các nút chèn nhanh là nhận xét mẫu, cần cho người nhập xem lại nội dung đã chèn. Chuyển “thư mục mạng UNC” sang trợ giúp kỹ thuật. Giữ “Lưu phiếu chăm sóc” và thông báo riêng khi lịch sử trống. [NhatKyChamSocDialog](../src/main/java/com/petstation/gui/luutru/NhatKyChamSocDialog.java)

**Tên trên thanh tiêu đề** · Phiếu chăm sóc

- Nhãn trên trang · PHIẾU CHĂM SÓC THÚ CƯNG · Ghi nhận chăm sóc mới · Tình trạng sức khỏe khi nhận * · Hướng dẫn ăn uống & khẩu phần · Thuốc & chỉ định từ chủ nuôi · Việc đã thực hiện trong ca · Chưa chọn · Lịch sử chăm sóc
- Nút và lựa chọn · + Khỏe mạnh, tỉnh táo · + Ăn uống tốt · + Hơi nhút nhát · + Cần theo dõi thêm · + Ăn hết suất · + Uống nước bình thường · + Vận động vui vẻ · + Tắm / vệ sinh sạch · + Ngủ ngoan · Chọn ảnh trước · Xóa · Chọn ảnh sau · Lưu phiếu chăm sóc · In / Xuất phiếu PDF… · Đóng

## Kho và vận hành

### Phiếu nhập kho

Trang mở ra là danh sách phiếu, vì vậy dùng “Phiếu nhập kho” làm tiêu đề và giữ “Lập phiếu nhập kho” cho nút tạo. “Chi tiết” → “Xem phiếu nhập”, “Trả công nợ” → “Ghi nhận trả nhà cung cấp”. Nên có cột “Còn nợ” cạnh “Đã trả” để chọn đúng phiếu. Phiếu chi tiết có Sản phẩm, Số lô, Hạn dùng, Số lượng, Giá nhập. Hộp ghi trả thêm nên hiển thị số phiếu và số dư còn lại sau khi nhập tiền. [NhapKhoPanel](../src/main/java/com/petstation/gui/kho/NhapKhoPanel.java)

Sau khi chọn phiếu có công nợ, hộp “Trả công nợ” có “Số tiền trả thêm (còn nợ …)” và mặc định điền toàn bộ số còn lại. Nên tách dư nợ thành nhãn chỉ đọc và ghi rõ nếu người dùng chọn trả hết.

**Tên trên thanh tiêu đề** · Lập phiếu nhập kho

**Gợi ý trên thanh trạng thái** · Lập phiếu nhập: số lô, hạn dùng, công nợ.

- Nhãn trên trang · Nhập kho
- Cột bảng · Số phiếu · Nhà cung cấp · Ngày nhập · Tổng tiền · Đã trả · Trạng thái
- Nút và lựa chọn · Xuất Excel · + Lập phiếu nhập kho · Chi tiết · Trả công nợ

### Lập phiếu nhập kho

Form chọn sẵn nhà cung cấp và sản phẩm đầu tiên, tự điền số lô theo ngày và hạn dùng sau một năm. Người nhận hàng cần chép số lô, hạn dùng từ hàng thực nhận hoặc xác nhận từng giá trị. “Giá nhập / đơn vị” → “Đơn giá nhập (₫)”. “Đồng ý” → “Ghi nhận nhập kho”. Hiển thị tổng giá trị, số đã trả và còn nợ trước khi ghi phiếu. Dùng ô ngày nhất quán với các màn hình có lịch chọn. [NghiepVuKhoPanel](../src/main/java/com/petstation/gui/kho/NghiepVuKhoPanel.java)

**Tên trên thanh tiêu đề** · Lập phiếu nhập kho

- Nhãn trên trang · Nhà cung cấp · Sản phẩm · Số lô · Hạn sử dụng (dd/MM/yyyy hoặc yyyy-MM-dd) · Số lượng · Giá nhập / đơn vị (₫) · Đã trả (₫)
- Nút và lựa chọn · Đồng ý · Hủy

### Phiếu xuất kho và xuất hủy

Tên menu “Xuất hủy hàng hóa” hẹp hơn nội dung bảng, vốn có cả xuất sử dụng nội bộ. Cột “Loại xuất” đang hiện XUAT_HUY và SU_DUNG_NOI_BO trên ảnh dựng. Hiển thị “Xuất hủy” và “Sử dụng nội bộ”. Nút “Lập phiếu xuất hủy” đúng với thao tác tạo. “Giá trị” → “Giá trị xuất (₫)”, giữ lý do trước trạng thái duyệt. [XuatHuyKhoPanel](../src/main/java/com/petstation/gui/kho/XuatHuyKhoPanel.java)

**Tên trên thanh tiêu đề** · Xuất hủy hàng hóa

**Gợi ý trên thanh trạng thái** · Chọn lý do, lô và số lượng.

- Nhãn trên trang · Xuất kho và xuất hủy
- Cột bảng · Số phiếu · Loại xuất · Lý do · Giá trị · Trạng thái · Ngày tạo
- Nút và lựa chọn · Xuất Excel · + Lập phiếu xuất hủy

### Lập phiếu xuất hủy

Giữ thứ tự lô, số lượng, lý do. Đặt tồn khả dụng cạnh số lượng, thêm đơn vị, ghi “Lý do xuất hủy”. “Đồng ý” mơ hồ khi kết quả có thể là xuất kho hoặc gửi duyệt. Khi xác định được ngưỡng, ghi “Ghi nhận xuất hủy” hoặc “Gửi yêu cầu duyệt” kèm giá trị phiếu. [NghiepVuKhoPanel](../src/main/java/com/petstation/gui/kho/NghiepVuKhoPanel.java)

**Tên trên thanh tiêu đề** · Xuất hủy hàng hóa

- Nhãn trên trang · Lô hàng · Số lượng · Lý do
- Nút và lựa chọn · Đồng ý · Hủy

### Kiểm kê tồn kho

Dùng “Biên bản kiểm kê” cho danh sách và “Bắt đầu kiểm kê” cho nút mở biểu mẫu. “Cân bằng kho (Duyệt)” → “Duyệt và điều chỉnh tồn”. Ở bảng chi tiết, giữ riêng số sổ sách, số đếm và chênh lệch. Trước khi đổi chữ trên nút hoàn tất, phải xử lý hai lỗi về bộ lọc và máy quét được tái hiện trong báo cáo chính. [KiemKePanel](../src/main/java/com/petstation/gui/kho/KiemKePanel.java)

**Tên trên thanh tiêu đề** · Kiểm kê tồn kho

**Gợi ý trên thanh trạng thái** · Nhập số thực tế, chênh lệch gửi duyệt.

- Nhãn trên trang · Kiểm kê tồn kho · Chi tiết đợt kiểm kê: (Chưa chọn biên bản)
- Cột bảng · Số biên bản · Kỳ kiểm kê · Trạng thái · Ngày tạo
- Cột bảng · Mã SP · Tên sản phẩm · Số lô · Hạn dùng · Tồn hệ thống · Tồn thực tế · Chênh lệch · Lý do chênh lệch
- Nút và lựa chọn · Xuất Excel · + Lập đợt kiểm kê · Cân bằng kho (Duyệt)

### Đếm hàng theo lô

“Quét Barcode” → “Quét mã vạch”, “Tồn HT” → “Tồn sổ sách”, “Tồn thực tế” → “Số lượng đã đếm”. Chân bảng đếm dòng theo lô nhưng lại gọi là “mặt hàng”. Ghi “Đã đếm …/… lô”. Đổi “Chế độ kiểm kê mù (Blind Count)” → “Ẩn tồn sổ sách khi đếm”. “Lưu biên bản & Hoàn tất” hứa hoàn tất cả khi còn chờ duyệt, nên dùng “Lập biên bản kiểm kê” và báo trạng thái cụ thể sau khi lưu. [KiemKePanel](../src/main/java/com/petstation/gui/kho/KiemKePanel.java)

**Tên trên thanh tiêu đề** · Kiểm kê tồn kho theo đợt

- Nhãn trên trang · Kỳ kiểm kê: · Nhóm hàng: · Tìm nhanh: · Quét Barcode:
- Cột bảng · Mã SP · Tên sản phẩm · Barcode · Số lô · Hạn dùng · Tồn HT · Tồn thực tế · Chênh lệch · Lý do chênh lệch
- Nút và lựa chọn · Lô sắp hết hạn · Lọc danh sách · Chế độ kiểm kê mù (Blind Count) · Hủy · Lưu biên bản & Hoàn tất

### Ca thu ngân

Phần mở ca tập trung tốt vào tiền mặt đầu ca. Các nút mệnh giá cần làm rõ là điền nhanh số đầu ca. Phần kết ca cần giữ “Tổng tiền mặt thực đếm”, “Chênh lệch” và “Người nhận ca” cùng nhau. “Đối soát tiền mặt & kết ca” → “Đối soát tiền mặt và kết ca”. Hiện lý do buộc ghi chú ngay cạnh khoản chênh lệch. Phân biệt tiền dự kiến trong két với tiền thu bằng thẻ hoặc chuyển khoản. [CaThuNganPanel](../src/main/java/com/petstation/gui/banhang/CaThuNganPanel.java)

Mở ca và kết ca là hai trạng thái của cùng trang. Giữ “Mở ca” cho số tiền đầu ca, “Kết ca” cho phần bàn giao. Khi có chênh lệch, diễn đạt thành “Thừa …” hoặc “Thiếu …” bên cạnh lý do để giảm nhầm dấu.

**Tên trên thanh tiêu đề** · Ca thu ngân

**Gợi ý trên thanh trạng thái** · Quản lý ca và tiền mặt.

- Nhãn trên trang · Ca thu ngân · Mở ca làm việc · Ca làm việc · Tiền mặt đầu ca (₫) · Đối soát tiền mặt & kết ca · Tổng tiền mặt thực đếm (₫) · Chênh lệch so với dự kiến · Nhập trực tiếp tổng tiền mặt thực đếm trong két để đối soát. · Hình thức kết ca · Người nhận ca · Ghi chú bàn giao (bắt buộc khi có chênh lệch)
- Nút và lựa chọn · 1.000.000₫ · 2.000.000₫ · 3.000.000₫ · 5.000.000₫ · Mở ca · Kết ca

### Phân ca theo tuần

“+ Thêm nhân viên” trong ô ca dễ bị hiểu là tạo hồ sơ mới, nên ghi “Thêm người vào ca”. “Sao chép tuần trước” → “Sao chép lịch tuần trước”, hiển thị ngày nguồn và tuần đích trong xác nhận. Giữ “Khóa lịch tuần” và cho thấy tuần cụ thể. Trong biểu mẫu, dùng “Lưu phân ca”, “Hoán đổi hai ca”, “Gửi yêu cầu” theo tác vụ của quản lý hoặc nhân viên. [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java)

Bảng chọn người cho ca gồm Chọn, Mã NV, Họ tên, Chức vụ, Giờ tuần này, Trạng thái / Ghi chú. Đổi “Giờ tuần này” thành “Giờ đã phân trong tuần”. “Lưu phân ca”, “Hủy”, “Xác nhận hoán đổi” nên đặt cạnh hai ca đang chọn, kèm ngày, giờ và hai người bị ảnh hưởng.

**Tên trên thanh tiêu đề** · Phân lịch nhân viên

**Gợi ý trên thanh trạng thái** · Phân ca theo lưới tuần, cảnh báo ca thiếu người (chỉ Cửa hàng trưởng).

- Nhãn trên trang · Phân lịch nhân viên theo tuần · Lọc NV: · + Thêm nhân viên · Nhân viên bán hàng · Nhân viên Spa/Hotel · Nhân viên kho · Nghỉ phép · Viền đỏ = ca thiếu người
- Nút và lựa chọn · Tất cả · Ca làm việc · Nghỉ phép · ‹ Tuần trước · Chọn ngày · Tuần sau › · Hiện tại · Sao chép tuần trước · Đổi ca · Khóa lịch tuần · In lịch · Xuất Excel

### Phê duyệt yêu cầu

Đổi cột “Mã” → “Mã yêu cầu”, “Thời gian” → “Gửi lúc”. Dạng hai cột trước và sau hợp với đề xuất giá nhưng “Trước khi sửa / Sau khi sửa” khó dùng cho nghỉ phép. Đặt nhãn theo loại yêu cầu. Giữ “Duyệt”, “Từ chối”, “Yêu cầu bổ sung”, ghi hành động cụ thể trên nút xác nhận cuối vì duyệt có thể thực thi thay đổi ngay. [TrungTamPheDuyetPanel](../src/main/java/com/petstation/gui/pheduyet/TrungTamPheDuyetPanel.java)

**Tên trên thanh tiêu đề** · Phê duyệt yêu cầu

**Gợi ý trên thanh trạng thái** · Duyệt hoặc từ chối yêu cầu (chỉ Cửa hàng trưởng).

- Nhãn trên trang · Trung tâm phê duyệt · Loại: · Chọn một yêu cầu để xem chi tiết.
- Cột bảng · Mã · Loại yêu cầu · Người gửi · Thời gian · Trạng thái
- Nút và lựa chọn · Chỉ hiện yêu cầu chờ duyệt

### Chi tiết yêu cầu phê duyệt

Giữ đối chiếu trước và sau khi đổi giá. Với nghỉ phép cần người nghỉ, ngày và ca. Với xuất hủy cần lô, số lượng và giá trị. Nút xác nhận “Đồng ý” ở bước duyệt nên đổi thành “Duyệt yêu cầu”. Hộp nhập lý do từ chối hoặc bổ sung nên dùng “Gửi lý do từ chối” hoặc “Gửi yêu cầu bổ sung”. [TrungTamPheDuyetPanel](../src/main/java/com/petstation/gui/pheduyet/TrungTamPheDuyetPanel.java)

- Nhãn trên trang · Trung tâm phê duyệt · Loại: · Trước khi sửa · Sau khi sửa
- Cột bảng · Mã · Loại yêu cầu · Người gửi · Thời gian · Trạng thái
- Nút và lựa chọn · Chỉ hiện yêu cầu chờ duyệt · Duyệt · Từ chối · Yêu cầu bổ sung

## Thống kê

Bộ lọc kỳ dùng chung có Hôm nay, Hôm qua, 7 ngày qua, Tuần này, Tuần trước, Tháng này, Tháng trước, 30 ngày qua, Năm nay và Tùy chọn. Dùng “Chọn khoảng ngày…” cho mục cuối. “Xuất Excel” xuất bảng đang hiển thị, vì vậy tên tệp và thông báo thành công nên ghi rõ chế độ và kỳ. Các nút chú giải biểu đồ phụ thuộc dữ liệu trong kỳ. [Bộ chọn khoảng ngày](../src/main/java/com/petstation/gui/chung/thanhphan/BoChonKhoangNgay.java) · [Trang báo cáo dùng chung](../src/main/java/com/petstation/gui/thongke/BaoCaoPanel.java)

### Thống kê doanh thu

“Doanh thu hoàn tất” ghi nhận doanh thu khác với “Tiền thu ròng (gồm cọc)”. Đặt tên bộ chọn theo đại lượng thay vì chỉ “Theo ngày”. “Giá trị TB/đơn (AOV)” → “Giá trị trung bình mỗi đơn”. Trên thẻ lợi nhuận, chú thích con số % là tỷ suất. Giữ hoàn trả tách khỏi chiết khấu và ghi rõ kỳ áp dụng khi xuất bảng. [ThongKeDoanhThuPanel](../src/main/java/com/petstation/gui/thongke/ThongKeDoanhThuPanel.java)

**Tên trên thanh tiêu đề** · Thống kê doanh thu

**Gợi ý trên thanh trạng thái** · Theo ngày, nhân viên, ca, nguồn, phương thức thanh toán.

- Nhãn trên trang · Thống kê doanh thu · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Biểu đồ: · Tổng số đơn · Doanh thu thuần · Giá trị TB/đơn (AOV) · Lợi nhuận gộp · Không có dữ liệu phát sinh trong kỳ đang chọn. · BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Ngày · Số đơn · Doanh thu gốc · Chiết khấu/Voucher · Hoàn trả · Doanh thu thuần · Lợi nhuận gộp
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Doanh thu hoàn tất | Theo kỳ | Ngày · Số đơn · Doanh thu gốc · Chiết khấu/Voucher · Hoàn trả · Doanh thu thuần · Lợi nhuận gộp |
| Theo ngày | Theo kỳ | Ngày · Tiền thu ròng (gồm cọc) |
| Theo thanh toán | Theo kỳ | Phương thức · Tiền thu ròng · Số hóa đơn |
| Theo thu ngân | Theo kỳ | Nhân viên · Tiền thu ròng · Số hóa đơn |
| Theo nguồn | Theo kỳ | Nguồn · Tiền thu ròng · Số hóa đơn |
| Theo ca | Theo kỳ | Ca · Tiền thu ròng · Số hóa đơn |
| Theo khung giờ | Theo kỳ | Khung giờ · Doanh thu · Số hóa đơn |
| Theo thứ trong tuần | Theo kỳ | Thứ · Doanh thu · Số hóa đơn |

### Thống kê hàng hóa

Chế độ “Sản phẩm bán chạy” lấy tối đa 10 sản phẩm, thẻ “Tổng doanh thu” hiện không ghi giới hạn này. Dùng “Doanh thu của 10 sản phẩm bán chạy” hoặc tính riêng tổng toàn bộ. “Số bán sau trả” → “Số lượng bán ròng”. “Tổng vốn chôn” tính theo giá nhập gần nhất, nên ghi “Giá trị tồn ước tính”. Gắn “Hiện tại” vào các chế độ tồn để người dùng không đọc nhầm theo kỳ. [ThongKeHangHoaPanel](../src/main/java/com/petstation/gui/thongke/ThongKeHangHoaPanel.java)

**Tên trên thanh tiêu đề** · Thống kê hàng hóa

**Gợi ý trên thanh trạng thái** · Bán chạy, nhập–xuất–tồn, tồn thấp, sắp hết hạn.

- Nhãn trên trang · Thống kê hàng hóa · Chọn kỳ để xem giao dịch đã lưu · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Biểu đồ: · Số sản phẩm · Tổng doanh thu · TB/sản phẩm · Kỳ báo cáo · Không có dữ liệu phát sinh trong kỳ đang chọn. · BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Mã SP · Sản phẩm · Số bán sau trả · Doanh thu
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Sản phẩm bán chạy | Theo kỳ | Mã SP · Sản phẩm · Số bán sau trả · Doanh thu |
| Hàng tồn đọng / chôn vốn | Hiện tại | Mã SP · Sản phẩm · Tồn kho · Giá nhập gần nhất · Tổng vốn chôn |
| Sắp hết hạn | Hiện tại | Mã SP · Sản phẩm · Số lô · Hạn dùng · Tồn |
| Hao hụt từ kiểm kê | Theo kỳ | Mã SP · Sản phẩm · Số lô · Chênh lệch SL · Giá trị lệch · Lý do · Ngày tạo |
| Biến động trong kỳ | Theo kỳ | Mã SP · Sản phẩm · Loại giao dịch · Số lượng (+ nhập / − xuất) |
| Tồn kho hiện tại | Hiện tại | Mã SP · Sản phẩm · Tồn kho · Tồn tối thiểu · Giá nhập gần nhất |

### Thống kê dịch vụ spa

Với “Phiếu theo trạng thái”, tiêu đề biểu đồ “BIỂU ĐỒ DIỄN BIẾN” gợi trục thời gian, dù trục là các trạng thái. Dùng “Số phiếu theo trạng thái”. “TB/trạng thái” ít giúp điều phối, có thể thay bằng số phiếu đang chờ. Ở “Dịch vụ đã thanh toán”, phân biệt giá trị theo loại dịch vụ với giá trị trung bình mỗi lượt. Dòng hướng dẫn chung cần khớp ba chế độ hiện có. [ThongKeSpaPanel](../src/main/java/com/petstation/gui/thongke/ThongKeSpaPanel.java)

**Tên trên thanh tiêu đề** · Thống kê dịch vụ Spa

**Gợi ý trên thanh trạng thái** · Số phiếu, dịch vụ phổ biến, tỷ lệ hủy, hiệu suất.

- Nhãn trên trang · Thống kê dịch vụ Spa · Chọn kỳ để xem giao dịch đã lưu · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Biểu đồ: · Số trạng thái · Tổng số phiếu · TB/trạng thái · 2.6 · Kỳ báo cáo · Không có dữ liệu phát sinh trong kỳ đang chọn. · BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Trạng thái · Số phiếu
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Phiếu theo trạng thái | Theo kỳ | Trạng thái · Số phiếu |
| Dịch vụ đã thanh toán | Theo kỳ | Mã DV · Dịch vụ · Số lượt · Giá trị dịch vụ |
| Trạng thái lịch hẹn | Theo kỳ | Trạng thái · Số lịch |

### Thống kê lưu trú

Giữ tên “Cọc nhận trong kỳ” và “Trạng thái phòng hiện tại”, vì khác mốc thời gian. “Tổng đơn giá phòng/đêm” cộng nhiều đơn giá, dễ đọc thành doanh thu, nên ghi “Tổng đơn giá đêm của các hợp đồng” hoặc bỏ nếu không dùng. Nếu giới thiệu công suất phòng, cần một tỷ lệ có mẫu số phòng-đêm khả dụng, thay vì chú thích khiến người dùng tưởng đã có số đó. Đồng bộ đơn vị ngày/đêm với hợp đồng và cách tính phí. [ThongKeHotelPanel](../src/main/java/com/petstation/gui/thongke/ThongKeHotelPanel.java)

**Tên trên thanh tiêu đề** · Thống kê lưu trú

**Gợi ý trên thanh trạng thái** · Công suất phòng, số ngày lưu trú, lượt quá hạn.

- Nhãn trên trang · Thống kê lưu trú · Chọn kỳ để xem giao dịch đã lưu · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Biểu đồ: · Số trạng thái · Tổng hợp đồng · TB/trạng thái · 2.0 · Kỳ báo cáo · Không có dữ liệu phát sinh trong kỳ đang chọn. · BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Trạng thái · Số hợp đồng · Tổng đơn giá phòng/đêm · Cọc đã nhận
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Hợp đồng nhận trong kỳ | Theo kỳ | Trạng thái · Số hợp đồng · Tổng đơn giá phòng/đêm · Cọc đã nhận |
| Cọc nhận trong kỳ | Theo kỳ | Ngày nhận · Tiền cọc · Số lần nhận |
| Trạng thái phòng hiện tại | Hiện tại | Trạng thái · Số phòng |
| Lưu trú đã quyết toán | Theo kỳ | Loại phòng · Lượt lưu trú · Số ngày · Doanh thu hoàn tất (gồm cọc) |

### Thống kê khách hàng

Ở “Lượt dùng voucher trong kỳ”, truy vấn lấy tên chủ voucher và tổng giảm giá hóa đơn. Cột “Khách hàng” nên ghi “Chủ voucher”. “Giảm giá” phải nói rõ “Tổng giảm trên hóa đơn dùng voucher” nếu giữ phép tính. “TB/nhóm” trong biểu đồ khách mới/quay lại có ít giá trị vận hành, có thể thay bằng tỷ lệ khách quay lại kèm kỳ. Giữ số khách và lượt mua là hai cột riêng. [ThongKeKhachHangPanel](../src/main/java/com/petstation/gui/thongke/ThongKeKhachHangPanel.java)

**Tên trên thanh tiêu đề** · Thống kê khách hàng

**Gợi ý trên thanh trạng thái** · Khách mới, thân thiết, top chi tiêu, điểm và voucher.

- Nhãn trên trang · Thống kê khách hàng · Chọn kỳ để xem giao dịch đã lưu · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Biểu đồ: · Số nhóm khách · Tổng số khách · TB/nhóm · Kỳ báo cáo · Không có dữ liệu phát sinh trong kỳ đang chọn. · BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Nhóm khách · Số khách · Lượt mua · Tiền thanh toán
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Khách mới / quay lại | Theo kỳ | Nhóm khách · Số khách · Lượt mua · Tiền thanh toán |
| Lượt dùng voucher trong kỳ | Theo kỳ | Mã voucher · Khách hàng · Lượt dùng · Giảm giá |
| Chi tiêu theo kỳ | Theo kỳ | Mã KH · Khách hàng · Hạng · Điểm · Chi tiêu |
| Sử dụng voucher | Hiện tại | Mã voucher · Mệnh giá · Đã dùng · Số lượng · Hạn dùng |

### Công nợ nhà cung cấp

Đây là trang có lời giải thích đúng và cần giữ. Bộ lọc chọn phiếu theo ngày nhập, còn nợ là số ở hiện tại. “Dư nợ trên phiếu hiện tại” → “Còn nợ hiện tại của các phiếu trong kỳ”. “Dư nợ TB/NCC” → “Dư nợ trung bình mỗi nhà cung cấp”. Biểu đồ tròn rút ngắn tên doanh nghiệp khiến nhiều nhãn giống nhau, nên ưu tiên bảng xếp theo dư nợ và cho xem tên đầy đủ. [ThongKeCongNoPanel](../src/main/java/com/petstation/gui/thongke/ThongKeCongNoPanel.java)

**Tên trên thanh tiêu đề** · Công nợ nhà cung cấp

**Gợi ý trên thanh trạng thái** · Tổng nhập, đã trả, còn nợ.

- Nhãn trên trang · Công nợ nhà cung cấp · Báo cáo theo kỳ lọc ngày nhập; đã trả và dư nợ là số cập nhật hiện tại. · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Số nhà cung cấp · Tổng dư nợ · Dư nợ TB/NCC · Kỳ báo cáo · Không có dữ liệu phát sinh trong kỳ đang chọn. · DƯ NỢ THEO NHÀ CUNG CẤP (Nhấp vào cột để xem chứng từ) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Mã NCC · Nhà cung cấp · Tổng nhập trong kỳ · Đã trả hiện tại · Dư nợ trên phiếu hiện tại
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Công nợ theo kỳ nhập | Theo kỳ | Mã NCC · Nhà cung cấp · Tổng nhập trong kỳ · Đã trả hiện tại · Dư nợ trên phiếu hiện tại |
| Dư nợ hiện tại | Hiện tại | Mã NCC · Nhà cung cấp · Tổng nhập · Đã trả hiện tại · Dư nợ hiện tại |

### Thống kê nhân viên

Hai chế độ là thu tiền theo ca và giờ làm, vì vậy gợi ý về “phiếu Spa đã thực hiện” cần chỉnh. Giữ các cột “Thu hóa đơn”, “Cọc lưu trú”, “Thực đếm” tách biệt. “Tổng thu trong ca (HĐ + Cọc)” → “Tổng thu gồm cọc”. Nêu rõ thực đếm là tiền mặt và số tổng thu có thể gồm phương thức khác. Tiêu đề biểu đồ cần cho biết so sánh nhân viên, ca hay giờ làm. [ThongKeNhanVienPanel](../src/main/java/com/petstation/gui/thongke/ThongKeNhanVienPanel.java)

**Tên trên thanh tiêu đề** · Thống kê nhân viên

**Gợi ý trên thanh trạng thái** · Số ca, giờ làm, doanh số, phiếu Spa đã thực hiện.

- Nhãn trên trang · Thống kê nhân viên · Chọn kỳ để xem giao dịch đã lưu · Khoảng thời gian: · Từ ngày: · Đến ngày: · Xem theo: · Biểu đồ: · Số ca thu ngân · Tổng thu trong ca · TB thu/ca · Kỳ báo cáo · Không có dữ liệu phát sinh trong kỳ đang chọn. · BIỂU ĐỒ DIỄN BIẾN (Nhấp vào cột để xem chứng từ chi tiết) · Cơ cấu trong phạm vi báo cáo
- Cột bảng · Nhân viên · Ca · Mở lúc · Đóng lúc · Tiền đầu ca · Thu hóa đơn · Cọc lưu trú · Tổng thu trong ca (HĐ + Cọc) · Thực đếm · Chênh lệch
- Nút và lựa chọn · Chọn ngày · Xuất Excel

| Chế độ xem | Phạm vi thời gian | Các cột theo thứ tự hiển thị |
| --- | --- | --- |
| Thu tiền và chênh lệch ca | Theo kỳ | Nhân viên · Ca · Mở lúc · Đóng lúc · Tiền đầu ca · Thu hóa đơn · Cọc lưu trú · Tổng thu trong ca (HĐ + Cọc) · Thực đếm · Chênh lệch |
| Lịch làm việc | Theo kỳ | Mã NV · Nhân viên · Chức vụ · Số ca · Số giờ |

## Câu chữ trong ô tìm kiếm

| Trang | Chữ hiện tại | Câu chữ đề nghị |
| --- | --- | --- |
| Tài khoản | Tìm theo tên đăng nhập, nhân viên... | “Tên đăng nhập hoặc nhân viên”. Giữ một nhãn “Tìm tài khoản” bên ngoài ô nếu đủ chỗ. |
| Nhật ký hệ thống | Tìm người dùng, hành động, mã... | “Người thực hiện, hành động hoặc mã chứng từ”. Giữ tên trường khớp cột bảng. |
| Khách hàng và lịch sử khách hàng | Tìm theo tên, SĐT, Email, Địa chỉ, mã khách... / Nhập tên, SĐT hoặc mã khách... | Dùng “Tên, số điện thoại hoặc mã khách hàng” cho ba tiêu chí phổ biến. Đưa email và địa chỉ vào gợi ý đầy đủ ở trang có hỗ trợ. |
| Hồ sơ thú cưng | Tìm theo tên thú, giống, chủ nuôi... | “Tên thú cưng, giống hoặc chủ nuôi”. |
| Nhân viên | Tìm theo tên, SĐT, CCCD, mã NV... | “Tên, điện thoại, số căn cước hoặc mã nhân viên”. Nếu ô hẹp, hiển thị hai tiêu chí chính và đưa danh sách đủ vào tooltip. |
| Sản phẩm / lập hóa đơn | Tìm mã, tên, mã vạch... / Quét mã vạch EAN-13, nhập mã SP hoặc tên rồi nhấn Enter. | “Mã, tên hoặc mã vạch” ở danh mục. Ở quầy dùng “Quét mã vạch hoặc nhập tên sản phẩm”, giữ hướng dẫn Enter cạnh nút thêm. |
| Dịch vụ spa / bảng giá | Tìm theo mã, tên dịch vụ... / Nhập giá... | “Mã hoặc tên dịch vụ”, “Giá dịch vụ (₫)”. |
| Phòng lưu trú / sơ đồ phòng | Tìm theo mã phòng, loại phòng... / Tìm mã phòng, loại, khách, SĐT, thú cưng... | “Mã hoặc loại phòng” ở danh mục. “Phòng, khách hàng hoặc thú cưng” ở sơ đồ, giữ số điện thoại trong gợi ý đầy đủ. |
| Nhà cung cấp | Tìm theo tên, người liên hệ, MST... | “Tên nhà cung cấp, người liên hệ hoặc mã số thuế”. |
| Khuyến mãi | Tìm theo mã, tên chương trình... | “Mã hoặc tên chương trình”. |
| Voucher và chọn người nhận | Tìm theo mã voucher, khách hàng... / Tìm mã, tên hoặc SĐT khách hàng... | “Mã voucher hoặc chủ sở hữu” cho danh sách. “Mã, tên hoặc điện thoại người nhận” cho bộ chọn khách. |
| Hóa đơn, đơn nháp và đổi hàng | Tìm trong danh sách... | Ở từng trang nêu khóa tra chính, lần lượt là “Số hóa đơn”, “Mã đơn nháp”, “Mã phiếu đổi”. Kiểm tra bộ lọc thật sự hỗ trợ khóa được ghi. |
| Kho | Tìm trong danh sách... | “Sản phẩm hoặc số lô” khi tra tồn. “Số phiếu nhập”, “Số phiếu xuất” và “Số biên bản” ở ba danh sách chứng từ. |
| Phiếu spa, lịch hẹn và hợp đồng | Tìm trong danh sách... | Nêu đối tượng theo từng tab, chẳng hạn “Mã phiếu spa”, “Mã lịch hẹn”, “Mã hợp đồng”. Giữ ngày và trạng thái ở bộ lọc riêng. |
| Đặt lịch, đặt phòng | Tìm tên, SĐT... | “Tên hoặc số điện thoại khách hàng”. Sau khi chọn khách cần thấy rõ thú cưng tương ứng. |
| Phê duyệt | Tìm mã, người gửi... | “Mã yêu cầu hoặc người gửi”. |

Trong hộp mở lại đơn nháp khi giỏ hiện có hàng, ba lựa chọn thực tế là “Treo giỏ hiện tại rồi mở”, “Thay thế (xóa giỏ hiện tại)” và “Hủy”. Nên dùng “Lưu giỏ hiện tại và mở đơn”, “Bỏ giỏ hiện tại và mở đơn”, “Giữ nguyên giỏ”. Đây là nơi phải viết rõ hậu quả dù nhãn dài hơn. [Xử lý mở đơn](../src/main/java/com/petstation/gui/banhang/DonHangTreoPanel.java)

## Các bước dùng chung panel

Registry ánh xạ sáu mã spa vào `TiepNhanSpaPanel`, bốn mã lưu trú vào `LuuTruHotelPanel`. Hai trang quản lý đã có ở trên. Tám mục dưới là tiêu đề và ý nghĩa nghiệp vụ của các mã còn lại. Tab hoặc hộp thoại thực tế còn tùy ngữ cảnh, phiếu đang chọn và cách mở từ khung chính. [Registry](../src/main/java/com/petstation/gui/chung/DangKyManHinh.java) · [Cách mở trang](../src/main/java/com/petstation/gui/chung/ChinhFrame.java#L237)

| Mã | Tiêu đề đã đăng ký | Đánh giá cho bước này |
| --- | --- | --- |
| `DAT_LICH_HEN_SPA` | Spa: Lịch hẹn | Khung chính chọn tab lịch hẹn. Dùng “Lịch hẹn spa” cho danh sách, “Đặt lịch hẹn” cho nút mở form. |
| `TIEP_NHAN_SPA` | Spa: Tiếp nhận | Dùng “Tiếp nhận thú cưng”. Phiếu cần xác định chủ, thú, cân nặng và tình trạng. Ghi rõ khi chỉ lưu nháp và khi tiếp nhận chính thức. |
| `CHON_GOI_SPA` | Spa: Chọn dịch vụ | Dùng “Thêm dịch vụ vào phiếu”. Bảng phải cho thấy tên dịch vụ, giá áp theo cân nặng và tổng tiền mới trước khi ghi. |
| `CAP_NHAT_TIEN_DO_SPA` | Spa: Tiến độ | Dùng “Cập nhật tiến độ spa”. Nút mô tả bước sắp làm, cột trạng thái mô tả bước đã ghi. |
| `HOAN_TAT_SPA` | Spa: Đóng phiếu | Dùng “Hoàn tất dịch vụ” cho xác nhận làm xong, “Sang thanh toán” cho bước thu tiền. Tránh để chữ “đóng” bao hàm cả hai. |
| `DAT_PHONG_LUU_TRU` | Lưu trú: Đặt phòng | Dùng “Đặt phòng lưu trú”. Khi mở từ thẻ phòng, giữ mã phòng và ngày nhận trong hộp xác nhận. |
| `NHAT_KY_CHAM_SOC` | Lưu trú: Nhật ký chăm sóc | Dùng “Nhật ký chăm sóc” cho lịch sử và “Thêm phiếu chăm sóc” cho thao tác ghi. Các cột lịch sử gồm Thời gian, Nhân viên, Ăn uống, Đã thực hiện, Tình trạng. |
| `TRA_PHONG_LUU_TRU` | Lưu trú: Trả phòng và quyết toán | Dùng “Trả phòng và tính tiền”. Hiển thị số ngày, tiền phòng, phụ phí, cọc đã trừ và còn phải thu cùng nhau trước khi chuyển sang thu tiền. |

## Các hộp thoại dùng lại ở nhiều trang

| Hộp thoại | Chữ hoặc hành vi hiện tại | Đề nghị nghiệm thu |
| --- | --- | --- |
| Xác nhận nghiệp vụ | Biểu mẫu chung dùng “Đồng ý” và “Hủy”. Sau khi đóng form, mã gọi mới phân tích dữ liệu. Ở phiếu nhập, nhập sai số rồi xác nhận có thể phải mở lại form. | Kiểm tra dữ liệu tại trường sai, giữ lại các giá trị vừa nhập, đặt động từ cuối là “Ghi nhận nhập kho”, “Gửi duyệt” hoặc “Ghi nhận trả tiền” theo tác vụ. [Biểu mẫu](../src/main/java/com/petstation/gui/chung/thanhphan/BieuMauTruong.java#L164) |
| Chọn ngày và giờ | Nút lịch dùng gợi ý “Chọn ngày”. Form nhập kho lại cho gõ ngày bằng hai định dạng, và trong bảng kiểm kê có ngày hiển thị dạng năm-tháng-ngày. | Dùng ngày/tháng/năm ở giao diện, giữ định dạng trao đổi trong tầng lưu trữ. Nút lịch nên có tên truy cập gắn với trường, ví dụ “Chọn ngày trả phòng”. |
| Chọn khách và đổi điểm | “Tìm”, “Bỏ chọn”, “Đổi voucher”. Bỏ trống có thể mang nghĩa khách lẻ trong bán hàng. | Dùng “Tìm khách hàng”, “Bỏ chọn khách”, “Đổi điểm lấy voucher”. Hiển thị số điểm bị trừ, mệnh giá, phạm vi và hạn dùng trước khi xác nhận. [Bộ chọn khách](../src/main/java/com/petstation/gui/khachhang/BoChonKhachHang.java) |
| Xuất Excel | Tiêu đề “Xuất Excel (.xlsx)”, bộ lọc tệp “Excel Workbook (*.xlsx)”, có xác nhận ghi đè khi trùng tên. | Giữ phần mở rộng và cảnh báo ghi đè. Việt hóa thành “Sổ làm việc Excel (*.xlsx)”. Với trang nhiều tab, thêm tên tab và phạm vi ngày vào tên tệp. [Hộp xuất Excel](../src/main/java/com/petstation/gui/chung/thanhphan/XuatExcelDialog.java) |
| In, PDF và chọn ảnh | Thao tác in có thể mở hộp máy in của hệ điều hành. Một số trang có thêm chức năng ghi PDF trực tiếp. Trong phiếu chăm sóc có cả ảnh trước và sau. | Chỉ ghi “Xuất PDF” trên hành động thật sự ghi PDF. Nếu mở hộp in, ghi “In…” và nêu tùy chọn lưu PDF trong trợ giúp. Khi bỏ ảnh, hiển thị đúng ảnh bị tác động. |
| Lỗi tải và trợ giúp | `TacVuNen.thongDiepLoi` lấy thông điệp của nguyên nhân gốc. Một số nơi hiển thị thẳng nội dung này cho người dùng. | Dùng câu theo tác vụ, ví dụ “Chưa tải được danh sách lô. Thử lại.” Giữ chi tiết SQL hoặc exception ở nhật ký kỹ thuật. Nút “Đóng” cho thông báo, nút “Thử lại” khi thao tác có thể thực hiện lại an toàn. [Tác vụ nền](../src/main/java/com/petstation/gui/chung/TacVuNen.java) |
