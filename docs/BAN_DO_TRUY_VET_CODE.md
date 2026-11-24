# Bản đồ truy vết mã nguồn hiện hành

Đối chiếu ngày 24/11/2026 với `DangKyManHinh`, `MaManHinh` và import DAO trong service. Có **47 mã màn hình/quy trình**, không đồng nghĩa có 47 lớp cửa sổ độc lập: spa/hotel dùng màn hình tổng hợp và lịch tuần có nhiều chế độ.

Nguồn đăng ký: [DangKyManHinh](../src/main/java/com/petstation/gui/chung/DangKyManHinh.java); mã: [MaManHinh](../src/main/java/com/petstation/model/chung/MaManHinh.java). Các service dưới đây là các điểm xử lý chính của màn hình và màn hình con; không phải cam kết mọi thao tác đều gọi mọi service/DAO trong cùng hàng. Danh mục dùng facade chung nên phần SQL được chọn theo loại danh mục. Để lần theo một thao tác cụ thể, mở handler của GUI, phương thức service rồi phương thức DAO tương ứng.

## Mã màn hình → GUI → service

| Mã | Tên đăng ký | GUI thực tế | Service chính |
| --- | --- | --- | --- |
| `TONG_QUAN` | Màn hình chính | [TongQuanPanel](../src/main/java/com/petstation/gui/hethong/TongQuanPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [CaThuNganService](../src/main/java/com/petstation/service/banhang/CaThuNganService.java), [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DOI_MAT_KHAU` | Đổi mật khẩu | [DoiMatKhauPanel](../src/main/java/com/petstation/gui/hethong/DoiMatKhauPanel.java) | [TaiKhoanService](../src/main/java/com/petstation/service/hethong/TaiKhoanService.java) |
| `TAI_KHOAN_PHAN_QUYEN` | Tài khoản & phân quyền | [TaiKhoanPhanQuyenPanel](../src/main/java/com/petstation/gui/hethong/TaiKhoanPhanQuyenPanel.java) | [TaiKhoanService](../src/main/java/com/petstation/service/hethong/TaiKhoanService.java), [QuyenVaiTroService](../src/main/java/com/petstation/service/hethong/QuyenVaiTroService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `CAI_DAT` | Cài đặt | [CaiDatPanel](../src/main/java/com/petstation/gui/hethong/CaiDatPanel.java) | [CaiDatService](../src/main/java/com/petstation/service/hethong/CaiDatService.java) |
| `NHAT_KY_HE_THONG` | Nhật ký hệ thống | [NhatKyHeThongPanel](../src/main/java/com/petstation/gui/hethong/NhatKyHeThongPanel.java) | [NhatKyHeThongService](../src/main/java/com/petstation/service/hethong/NhatKyHeThongService.java) |
| `DANH_MUC_KHACH_HANG` | Khách hàng | [DanhMucKhachHangPanel](../src/main/java/com/petstation/gui/khachhang/DanhMucKhachHangPanel.java) | [KhachHangService](../src/main/java/com/petstation/service/khachhang/KhachHangService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_THU_CUNG` | Hồ sơ thú cưng | [DanhMucThuCungPanel](../src/main/java/com/petstation/gui/khachhang/DanhMucThuCungPanel.java) | [ThuCungService](../src/main/java/com/petstation/service/khachhang/ThuCungService.java), [KhachHangService](../src/main/java/com/petstation/service/khachhang/KhachHangService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_NHAN_VIEN` | Nhân viên | [DanhMucNhanVienPanel](../src/main/java/com/petstation/gui/nhansu/DanhMucNhanVienPanel.java) | [NhanVienService](../src/main/java/com/petstation/service/nhansu/NhanVienService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_SAN_PHAM` | Sản phẩm | [DanhSachSanPhamPanel](../src/main/java/com/petstation/gui/kho/DanhSachSanPhamPanel.java) | [SanPhamService](../src/main/java/com/petstation/service/kho/SanPhamService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_DICH_VU_SPA` | Dịch vụ Spa | [DanhMucDichVuSpaPanel](../src/main/java/com/petstation/gui/spa/DanhMucDichVuSpaPanel.java) | [GoiDichVuSpaService](../src/main/java/com/petstation/service/spa/GoiDichVuSpaService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_PHONG_LUU_TRU` | Phòng lưu trú | [DanhMucPhongHotelPanel](../src/main/java/com/petstation/gui/luutru/DanhMucPhongHotelPanel.java) | [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_NHA_CUNG_CAP` | Nhà cung cấp | [DanhMucNhaCungCapPanel](../src/main/java/com/petstation/gui/kho/DanhMucNhaCungCapPanel.java) | [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `DANH_MUC_KHUYEN_MAI` | Khuyến mãi | [DanhMucKhuyenMaiPanel](../src/main/java/com/petstation/gui/khachhang/DanhMucKhuyenMaiPanel.java) | [KhuyenMaiService](../src/main/java/com/petstation/service/khachhang/KhuyenMaiService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `TICH_DIEM_VOUCHER` | Voucher và điểm thưởng | [TichDiemVoucherPanel](../src/main/java/com/petstation/gui/khachhang/TichDiemVoucherPanel.java) | [VoucherService](../src/main/java/com/petstation/service/khachhang/VoucherService.java), [TichDiemService](../src/main/java/com/petstation/service/khachhang/TichDiemService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java), [CaiDatService](../src/main/java/com/petstation/service/hethong/CaiDatService.java) |
| `TIM_KIEM_HOA_DON` | Tìm kiếm hóa đơn | [TimKiemHoaDonPanel](../src/main/java/com/petstation/gui/banhang/TimKiemHoaDonPanel.java) | [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java), [DoiTraService](../src/main/java/com/petstation/service/banhang/DoiTraService.java), [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java) |
| `TRA_CUU_KHACH_HANG` | Lịch sử khách hàng | [LichSuKhachHangPanel](../src/main/java/com/petstation/gui/khachhang/LichSuKhachHangPanel.java) | [LichSuKhachHangService](../src/main/java/com/petstation/service/khachhang/LichSuKhachHangService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) |
| `TRA_CUU_LO_HANG` | Tồn kho theo lô | [TimKiemTonTheoLoPanel](../src/main/java/com/petstation/gui/kho/TimKiemTonTheoLoPanel.java) | [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java), [NhapXuatKhoService](../src/main/java/com/petstation/service/kho/NhapXuatKhoService.java) |
| `TRA_CUU_LICH_LAM_VIEC` | Lịch làm việc | [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java) | [LichLamViecService](../src/main/java/com/petstation/service/nhansu/LichLamViecService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java), [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java) |
| `LICH_CA_NHAN` | Lịch làm việc cá nhân | [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java) | [LichLamViecService](../src/main/java/com/petstation/service/nhansu/LichLamViecService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java), [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java) |
| `LAP_HOA_DON` | Lập hóa đơn | [LapHoaDonPanel](../src/main/java/com/petstation/gui/banhang/LapHoaDonPanel.java) | [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java), [SanPhamService](../src/main/java/com/petstation/service/kho/SanPhamService.java) |
| `THANH_TOAN` | Thanh toán | [ThanhToanPanel](../src/main/java/com/petstation/gui/banhang/ThanhToanPanel.java) | [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java), [CaThuNganService](../src/main/java/com/petstation/service/banhang/CaThuNganService.java), [CaiDatService](../src/main/java/com/petstation/service/hethong/CaiDatService.java), [TichDiemService](../src/main/java/com/petstation/service/khachhang/TichDiemService.java) |
| `IN_HOA_DON` | Xem hóa đơn | [InHoaDonPanel](../src/main/java/com/petstation/gui/banhang/InHoaDonPanel.java) | [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java), [CaiDatService](../src/main/java/com/petstation/service/hethong/CaiDatService.java) |
| `HOA_DON_CHO` | Đơn nháp | [DonHangTreoPanel](../src/main/java/com/petstation/gui/banhang/DonHangTreoPanel.java) | [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java), [DonDoiTreoService](../src/main/java/com/petstation/service/banhang/DonDoiTreoService.java) |
| `DOI_TRA_HANG_HOA` | Đổi hàng | [DoiTraPanel](../src/main/java/com/petstation/gui/banhang/DoiTraPanel.java) | [DoiTraService](../src/main/java/com/petstation/service/banhang/DoiTraService.java), [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `QUAN_LY_SPA` | Quản lý Spa | [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java) | [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `DAT_LICH_HEN_SPA` | Spa: Lịch hẹn | [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java) | [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `TIEP_NHAN_SPA` | Spa: Tiếp nhận | [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java) | [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `CHON_GOI_SPA` | Spa: Chọn dịch vụ | [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java) | [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `CAP_NHAT_TIEN_DO_SPA` | Spa: Tiến độ | [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java) | [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `HOAN_TAT_SPA` | Spa: Đóng phiếu | [TiepNhanSpaPanel](../src/main/java/com/petstation/gui/spa/TiepNhanSpaPanel.java) | [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java), [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java), [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `QUAN_LY_LUU_TRU` | Quản lý lưu trú | [LuuTruHotelPanel](../src/main/java/com/petstation/gui/luutru/LuuTruHotelPanel.java) | [LuuTruService](../src/main/java/com/petstation/service/luutru/LuuTruService.java), [ChamSocService](../src/main/java/com/petstation/service/luutru/ChamSocService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `DAT_PHONG_LUU_TRU` | Lưu trú: Đặt phòng | [LuuTruHotelPanel](../src/main/java/com/petstation/gui/luutru/LuuTruHotelPanel.java) | [LuuTruService](../src/main/java/com/petstation/service/luutru/LuuTruService.java), [ChamSocService](../src/main/java/com/petstation/service/luutru/ChamSocService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `NHAT_KY_CHAM_SOC` | Lưu trú: Nhật ký chăm sóc | [LuuTruHotelPanel](../src/main/java/com/petstation/gui/luutru/LuuTruHotelPanel.java) | [LuuTruService](../src/main/java/com/petstation/service/luutru/LuuTruService.java), [ChamSocService](../src/main/java/com/petstation/service/luutru/ChamSocService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `TRA_PHONG_LUU_TRU` | Lưu trú: Trả phòng và quyết toán | [LuuTruHotelPanel](../src/main/java/com/petstation/gui/luutru/LuuTruHotelPanel.java) | [LuuTruService](../src/main/java/com/petstation/service/luutru/LuuTruService.java), [ChamSocService](../src/main/java/com/petstation/service/luutru/ChamSocService.java), [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `NHAP_KHO` | Lập phiếu nhập kho | [NhapKhoPanel](../src/main/java/com/petstation/gui/kho/NhapKhoPanel.java) | [NhapXuatKhoService](../src/main/java/com/petstation/service/kho/NhapXuatKhoService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `XUAT_HUY_KHO` | Xuất hủy hàng hóa | [XuatHuyKhoPanel](../src/main/java/com/petstation/gui/kho/XuatHuyKhoPanel.java) | [NhapXuatKhoService](../src/main/java/com/petstation/service/kho/NhapXuatKhoService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `KIEM_KE_TON_KHO` | Kiểm kê tồn kho | [KiemKePanel](../src/main/java/com/petstation/gui/kho/KiemKePanel.java) | [KiemKeService](../src/main/java/com/petstation/service/kho/KiemKeService.java), [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) |
| `CA_THU_NGAN` | Ca thu ngân | [CaThuNganPanel](../src/main/java/com/petstation/gui/banhang/CaThuNganPanel.java) | [CaThuNganService](../src/main/java/com/petstation/service/banhang/CaThuNganService.java) |
| `PHAN_LICH_NHAN_VIEN` | Phân lịch nhân viên | [LichTuanPanel](../src/main/java/com/petstation/gui/nhansu/LichTuanPanel.java) | [LichLamViecService](../src/main/java/com/petstation/service/nhansu/LichLamViecService.java), [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java), [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java) |
| `PHE_DUYET_YEU_CAU` | Phê duyệt yêu cầu | [TrungTamPheDuyetPanel](../src/main/java/com/petstation/gui/pheduyet/TrungTamPheDuyetPanel.java) | [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java), [DeXuatGiaService](../src/main/java/com/petstation/service/pheduyet/DeXuatGiaService.java) |
| `THONG_KE_DOANH_THU` | Thống kê doanh thu | [ThongKeDoanhThuPanel](../src/main/java/com/petstation/gui/thongke/ThongKeDoanhThuPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |
| `THONG_KE_HANG_HOA` | Thống kê hàng hóa | [ThongKeHangHoaPanel](../src/main/java/com/petstation/gui/thongke/ThongKeHangHoaPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |
| `THONG_KE_SPA` | Thống kê dịch vụ Spa | [ThongKeSpaPanel](../src/main/java/com/petstation/gui/thongke/ThongKeSpaPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |
| `THONG_KE_LUU_TRU` | Thống kê lưu trú | [ThongKeHotelPanel](../src/main/java/com/petstation/gui/thongke/ThongKeHotelPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |
| `THONG_KE_KHACH_HANG` | Thống kê khách hàng | [ThongKeKhachHangPanel](../src/main/java/com/petstation/gui/thongke/ThongKeKhachHangPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |
| `THONG_KE_CONG_NO_NCC` | Công nợ nhà cung cấp | [ThongKeCongNoPanel](../src/main/java/com/petstation/gui/thongke/ThongKeCongNoPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |
| `THONG_KE_NHAN_VIEN` | Thống kê nhân viên | [ThongKeNhanVienPanel](../src/main/java/com/petstation/gui/thongke/ThongKeNhanVienPanel.java) | [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java), [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) |

## Service → DAO trực tiếp

Danh sách này lấy từ các import DAO thực sự tồn tại, không suy ra tên implementation từ tên màn hình. Service có thể tiếp tục gọi một service/facade khác.

| Service | DAO trực tiếp trong file |
| --- | --- |
| [BanHangService](../src/main/java/com/petstation/service/banhang/BanHangService.java) | [HoaDonDAO](../src/main/java/com/petstation/dao/banhang/HoaDonDAO.java), [PhieuDoiTraDAO](../src/main/java/com/petstation/dao/banhang/PhieuDoiTraDAO.java), [HoaDonDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/HoaDonDAOImpl.java), [PhieuDoiTraDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/PhieuDoiTraDAOImpl.java), [HopDongLuuTruDAOImpl](../src/main/java/com/petstation/dao/luutru/impl/HopDongLuuTruDAOImpl.java) |
| [CaThuNganService](../src/main/java/com/petstation/service/banhang/CaThuNganService.java) | [PhienLamViecDAO](../src/main/java/com/petstation/dao/banhang/PhienLamViecDAO.java), [PhienLamViecDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/PhienLamViecDAOImpl.java) |
| [CaiDatService](../src/main/java/com/petstation/service/hethong/CaiDatService.java) | [CaiDatDAO](../src/main/java/com/petstation/dao/hethong/CaiDatDAO.java), [CaiDatDAOImpl](../src/main/java/com/petstation/dao/hethong/impl/CaiDatDAOImpl.java) |
| [ChamSocService](../src/main/java/com/petstation/service/luutru/ChamSocService.java) | [NhatKyChamSocDAO](../src/main/java/com/petstation/dao/luutru/NhatKyChamSocDAO.java), [NhatKyChamSocDAOImpl](../src/main/java/com/petstation/dao/luutru/impl/NhatKyChamSocDAOImpl.java) |
| [ChiTietThongKeService](../src/main/java/com/petstation/service/thongke/ChiTietThongKeService.java) | [ThongKeDAO](../src/main/java/com/petstation/dao/thongke/ThongKeDAO.java), [ThongKeDAOImpl](../src/main/java/com/petstation/dao/thongke/impl/ThongKeDAOImpl.java) |
| [DanhMucService](../src/main/java/com/petstation/service/chung/DanhMucService.java) | [DanhMucDAO](../src/main/java/com/petstation/dao/chung/DanhMucDAO.java), [DanhMucDAOImpl](../src/main/java/com/petstation/dao/chung/impl/DanhMucDAOImpl.java) |
| [DeXuatGiaService](../src/main/java/com/petstation/service/pheduyet/DeXuatGiaService.java) | [DeXuatGiaDAO](../src/main/java/com/petstation/dao/pheduyet/DeXuatGiaDAO.java), [DeXuatGiaDAOImpl](../src/main/java/com/petstation/dao/pheduyet/impl/DeXuatGiaDAOImpl.java) |
| [DoiTraService](../src/main/java/com/petstation/service/banhang/DoiTraService.java) | [PhieuDoiTraDAO](../src/main/java/com/petstation/dao/banhang/PhieuDoiTraDAO.java), [PhieuDoiTraDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/PhieuDoiTraDAOImpl.java) |
| [DonDoiTreoService](../src/main/java/com/petstation/service/banhang/DonDoiTreoService.java) | [HoaDonDAO](../src/main/java/com/petstation/dao/banhang/HoaDonDAO.java), [HoaDonDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/HoaDonDAOImpl.java) |
| [GoiDichVuSpaService](../src/main/java/com/petstation/service/spa/GoiDichVuSpaService.java) | [GoiDichVuSpaDAO](../src/main/java/com/petstation/dao/spa/GoiDichVuSpaDAO.java), [GoiDichVuSpaDAOImpl](../src/main/java/com/petstation/dao/spa/impl/GoiDichVuSpaDAOImpl.java) |
| [KhachHangService](../src/main/java/com/petstation/service/khachhang/KhachHangService.java) | [KhachHangDAO](../src/main/java/com/petstation/dao/khachhang/KhachHangDAO.java), [KhachHangDAOImpl](../src/main/java/com/petstation/dao/khachhang/impl/KhachHangDAOImpl.java) |
| [KhuyenMaiService](../src/main/java/com/petstation/service/khachhang/KhuyenMaiService.java) | [ChuongTrinhKhuyenMaiDAO](../src/main/java/com/petstation/dao/khachhang/ChuongTrinhKhuyenMaiDAO.java), [ChuongTrinhKhuyenMaiDAOImpl](../src/main/java/com/petstation/dao/khachhang/impl/ChuongTrinhKhuyenMaiDAOImpl.java) |
| [KiemKeService](../src/main/java/com/petstation/service/kho/KiemKeService.java) | [KiemKeDAO](../src/main/java/com/petstation/dao/kho/KiemKeDAO.java), [KiemKeDAOImpl](../src/main/java/com/petstation/dao/kho/impl/KiemKeDAOImpl.java) |
| [LapPhieuSpaService](../src/main/java/com/petstation/service/spa/LapPhieuSpaService.java) | [PhieuTiepNhanSpaDAO](../src/main/java/com/petstation/dao/spa/PhieuTiepNhanSpaDAO.java), [PhieuTiepNhanSpaDAOImpl](../src/main/java/com/petstation/dao/spa/impl/PhieuTiepNhanSpaDAOImpl.java) |
| [LichLamViecService](../src/main/java/com/petstation/service/nhansu/LichLamViecService.java) | [LichLamViecDAO](../src/main/java/com/petstation/dao/nhansu/LichLamViecDAO.java), [LichLamViecDAOImpl](../src/main/java/com/petstation/dao/nhansu/impl/LichLamViecDAOImpl.java) |
| [LichSuKhachHangService](../src/main/java/com/petstation/service/khachhang/LichSuKhachHangService.java) | [LichSuKhachHangDAO](../src/main/java/com/petstation/dao/khachhang/LichSuKhachHangDAO.java), [LichSuKhachHangDAOImpl](../src/main/java/com/petstation/dao/khachhang/impl/LichSuKhachHangDAOImpl.java) |
| [LuuTruService](../src/main/java/com/petstation/service/luutru/LuuTruService.java) | [HoaDonDAO](../src/main/java/com/petstation/dao/banhang/HoaDonDAO.java), [HoaDonDAOImpl](../src/main/java/com/petstation/dao/banhang/impl/HoaDonDAOImpl.java), [HopDongLuuTruDAO](../src/main/java/com/petstation/dao/luutru/HopDongLuuTruDAO.java), [HopDongLuuTruDAOImpl](../src/main/java/com/petstation/dao/luutru/impl/HopDongLuuTruDAOImpl.java) |
| [NghiepVuService](../src/main/java/com/petstation/service/chung/NghiepVuService.java) | [LuaChonNghiepVuDAO](../src/main/java/com/petstation/dao/chung/LuaChonNghiepVuDAO.java), [NghiepVuDAO](../src/main/java/com/petstation/dao/chung/NghiepVuDAO.java), [LuaChonNghiepVuDAOImpl](../src/main/java/com/petstation/dao/chung/impl/LuaChonNghiepVuDAOImpl.java), [NghiepVuDAOImpl](../src/main/java/com/petstation/dao/chung/impl/NghiepVuDAOImpl.java) |
| [NhanVienService](../src/main/java/com/petstation/service/nhansu/NhanVienService.java) | [NhanVienDAO](../src/main/java/com/petstation/dao/nhansu/NhanVienDAO.java), [NhanVienDAOImpl](../src/main/java/com/petstation/dao/nhansu/impl/NhanVienDAOImpl.java) |
| [NhapXuatKhoService](../src/main/java/com/petstation/service/kho/NhapXuatKhoService.java) | [NhapXuatKhoDAO](../src/main/java/com/petstation/dao/kho/NhapXuatKhoDAO.java), [NhapXuatKhoDAOImpl](../src/main/java/com/petstation/dao/kho/impl/NhapXuatKhoDAOImpl.java) |
| [NhatKyHeThongService](../src/main/java/com/petstation/service/hethong/NhatKyHeThongService.java) | [NhatKyHeThongDAO](../src/main/java/com/petstation/dao/hethong/NhatKyHeThongDAO.java), [NhatKyHeThongDAOImpl](../src/main/java/com/petstation/dao/hethong/impl/NhatKyHeThongDAOImpl.java) |
| [PheDuyetService](../src/main/java/com/petstation/service/pheduyet/PheDuyetService.java) | [YeuCauPheDuyetDAO](../src/main/java/com/petstation/dao/pheduyet/YeuCauPheDuyetDAO.java), [YeuCauPheDuyetDAOImpl](../src/main/java/com/petstation/dao/pheduyet/impl/YeuCauPheDuyetDAOImpl.java) |
| [PhieuSpaTreoService](../src/main/java/com/petstation/service/spa/PhieuSpaTreoService.java) | [PhieuSpaTreoDAO](../src/main/java/com/petstation/dao/spa/PhieuSpaTreoDAO.java), [PhieuSpaTreoDAOImpl](../src/main/java/com/petstation/dao/spa/impl/PhieuSpaTreoDAOImpl.java) |
| [QuyTrinhService](../src/main/java/com/petstation/service/chung/QuyTrinhService.java) | [QuyTrinhDAO](../src/main/java/com/petstation/dao/chung/QuyTrinhDAO.java), [QuyTrinhDAOImpl](../src/main/java/com/petstation/dao/chung/impl/QuyTrinhDAOImpl.java) |
| [QuyenVaiTroService](../src/main/java/com/petstation/service/hethong/QuyenVaiTroService.java) | [TaiKhoanDAO](../src/main/java/com/petstation/dao/hethong/TaiKhoanDAO.java), [TaiKhoanDAOImpl](../src/main/java/com/petstation/dao/hethong/impl/TaiKhoanDAOImpl.java) |
| [SanPhamService](../src/main/java/com/petstation/service/kho/SanPhamService.java) | [SanPhamDAO](../src/main/java/com/petstation/dao/kho/SanPhamDAO.java), [SanPhamDAOImpl](../src/main/java/com/petstation/dao/kho/impl/SanPhamDAOImpl.java) |
| [SpaService](../src/main/java/com/petstation/service/spa/SpaService.java) | [PhieuTiepNhanSpaDAO](../src/main/java/com/petstation/dao/spa/PhieuTiepNhanSpaDAO.java), [PhieuTiepNhanSpaDAOImpl](../src/main/java/com/petstation/dao/spa/impl/PhieuTiepNhanSpaDAOImpl.java) |
| [TaiKhoanService](../src/main/java/com/petstation/service/hethong/TaiKhoanService.java) | [TaiKhoanDAO](../src/main/java/com/petstation/dao/hethong/TaiKhoanDAO.java), [TaiKhoanDAOImpl](../src/main/java/com/petstation/dao/hethong/impl/TaiKhoanDAOImpl.java) |
| [ThongKeService](../src/main/java/com/petstation/service/thongke/ThongKeService.java) | [ThongKeDAO](../src/main/java/com/petstation/dao/thongke/ThongKeDAO.java), [ThongKeDAOImpl](../src/main/java/com/petstation/dao/thongke/impl/ThongKeDAOImpl.java) |
| [ThuCungService](../src/main/java/com/petstation/service/khachhang/ThuCungService.java) | [ThuCungDAO](../src/main/java/com/petstation/dao/khachhang/ThuCungDAO.java), [ThuCungDAOImpl](../src/main/java/com/petstation/dao/khachhang/impl/ThuCungDAOImpl.java) |
| [TichDiemService](../src/main/java/com/petstation/service/khachhang/TichDiemService.java) | [TichDiemDAO](../src/main/java/com/petstation/dao/khachhang/TichDiemDAO.java), [TichDiemDAOImpl](../src/main/java/com/petstation/dao/khachhang/impl/TichDiemDAOImpl.java) |
| [VoucherService](../src/main/java/com/petstation/service/khachhang/VoucherService.java) | [VoucherDAO](../src/main/java/com/petstation/dao/khachhang/VoucherDAO.java), [VoucherDAOImpl](../src/main/java/com/petstation/dao/khachhang/impl/VoucherDAOImpl.java) |

## Bảng SQL có trong schema

Các tên dưới đây được đối chiếu từ [001_schema.sql](../database/001_schema.sql). Đây là danh mục tồn tại, không phải ánh xạ một-một bảng với model. Chứng từ tổng hợp có thể dùng nhiều bảng.

- `bien_ban_kiem_ke`
- `bo_dem_hoa_don_ngay`
- `ca_lam_viec`
- `cai_dat`
- `chi_tiet_doi_tra`
- `chi_tiet_hoa_don`
- `chi_tiet_kiem_ke`
- `chi_tiet_phieu_nhap`
- `chi_tiet_phieu_spa`
- `chi_tiet_phieu_xuat`
- `chuong_trinh_khuyen_mai`
- `doi_chieu_du_lieu`
- `goi_dich_vu_spa`
- `hoa_don`
- `hop_dong_luu_tru`
- `khach_hang`
- `lich_hen`
- `lich_lam_viec`
- `lich_su_thu_coc_hotel`
- `lich_su_tiem_phong`
- `lo_hang`
- `nha_cung_cap`
- `nhan_vien`
- `nhat_ky_cham_soc`
- `nhat_ky_he_thong`
- `phan_bo_lo_hoa_don`
- `phien_lam_viec`
- `phieu_doi_tra`
- `phieu_nhap_kho`
- `phieu_spa_treo`
- `phieu_tiep_nhan_spa`
- `phieu_xuat_kho`
- `phong_hotel`
- `san_pham`
- `tai_khoan`
- `thu_cung`
- `voucher`
- `yeu_cau_phe_duyet`

## Các điểm dễ giải thích nhầm

- `BangNghiepVuPanel` dùng `NghiepVuService` để tải bảng chung; các panel con vẫn dùng service chuyên biệt để ghi nghiệp vụ.
- `DanhMucService`/`DanhMucDAOImpl` là bộ điều phối danh mục dùng chung; không có một DAO độc lập cho mỗi tab.
- Lịch tuần/cá nhân/tra cứu cùng dùng `LichTuanPanel`, khác `CheDo`; không cần giữ các lớp giả tưởng riêng cho ba màn hình.
- Spa/lưu trú có các panel con và dialog; xem các component được tạo trong panel tổng hợp trước khi lần theo service.
- Không dùng tên implementation trong tài liệu cũ nếu không tìm được file tương ứng trong source hiện tại.
- [Chuẩn code và câu trả lời bảo vệ](CHUAN_CODE.md#9-trả-lời-ngắn-khi-bảo-vệ) phân biệt phân tầng, Observer và transaction với những kiến trúc chưa triển khai.
