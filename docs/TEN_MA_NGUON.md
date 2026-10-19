# Quy tắc và bảng đối chiếu tên mã nguồn

Áp dụng cho 225 tệp Java ứng dụng và 11 tệp kiểm thử trong `src`, gồm 276 kiểu có tên (kể cả kiểu lồng nhau). Giữ các nhóm package hiện tại; riêng lớp khởi tạo ứng dụng được đặt tại `util.KhoiTaoUngDung`. Xem [quy chuẩn Java](../CONTRIBUTING.md#3-quy-chuẩn-lập-trình-coding-convention).

## Quy tắc tương thích

Điểm chạy vẫn là `com.petstation.App`; Maven, F5 và `scripts/run-java.ps1` dùng điểm chạy này. Các tên override của Java/Swing/thư viện giữ nguyên. Accessor record mang cùng tên tiếng Việt với thành phần record; các phương thức nghiệp vụ dùng động từ như `lay`, `tim`, `tao`, `luu`, `capNhat`, `kiemTra`.

Các mã enum thuộc `MaManHinh`, `VaiTro`, `Quyen`, `TrangThaiTaiKhoan`, `LoaiDanhMuc`, `LoaiNghiepVu`, loại yêu cầu phê duyệt và phương thức thanh toán được giữ để bảo toàn điều hướng và dữ liệu lưu trữ. Hằng giao diện nội bộ được Việt hóa theo UPPER_SNAKE_CASE.

Không thay đổi hai tệp SQL, tên bảng/cột, câu SQL, chuỗi trạng thái, biến môi trường, khóa cấu hình hoặc tài nguyên. Đợt đổi tên không chạy schema, dữ liệu mẫu hay migration trên database ứng dụng. Kiểm thử tích hợp chỉ dùng `PetStationIntegrationTest` với transaction hoàn tác.

## Bảng đối chiếu toàn bộ kiểu Java

Tên đầy đủ ở cột trái xác định cả package và kiểu bao ngoài; cột phải là tên đầy đủ sau đổi tên. Những tên công nghệ hoặc ngoại lệ đã thống nhất có thể giữ nguyên.

| Kiểu trước khi chuẩn hóa | Kiểu sau khi chuẩn hóa |
|---|---|
| `com.petstation.App` | `com.petstation.App` |
| `com.petstation.ApplicationContext` | `com.petstation.util.KhoiTaoUngDung` |
| `com.petstation.dao.AuditLogDao` | `com.petstation.dao.NhatKyHeThongDAO` |
| `com.petstation.dao.BatchDao` | `com.petstation.dao.LoHangDAO` |
| `com.petstation.dao.CustomerDao` | `com.petstation.dao.KhachHangDAO` |
| `com.petstation.dao.DataAccessException` | `com.petstation.dao.TruyCapDuLieuException` |
| `com.petstation.dao.DatabaseConfig` | `com.petstation.dao.CauHinhCSDL` |
| `com.petstation.dao.DatabaseConnection` | `com.petstation.dao.KetNoiCSDL` |
| `com.petstation.dao.DayLockDao` | `com.petstation.dao.ChotNgayDAO` |
| `com.petstation.dao.EmployeeDao` | `com.petstation.dao.NhanVienDAO` |
| `com.petstation.dao.HotelBookingDao` | `com.petstation.dao.HopDongLuuTruDAO` |
| `com.petstation.dao.HotelPricingDao` | `com.petstation.dao.BangGiaHotelDAO` |
| `com.petstation.dao.InvoiceDao` | `com.petstation.dao.HoaDonDAO` |
| `com.petstation.dao.PetDao` | `com.petstation.dao.ThuCungDAO` |
| `com.petstation.dao.PriceHistoryDao` | `com.petstation.dao.LichSuGiaDAO` |
| `com.petstation.dao.ProductDao` | `com.petstation.dao.SanPhamDAO` |
| `com.petstation.dao.SpaPackageDao` | `com.petstation.dao.GoiDichVuSpaDAO` |
| `com.petstation.dao.SpaPriceMatrixDao` | `com.petstation.dao.BangGiaSpaDAO` |
| `com.petstation.dao.SpaTicketDao` | `com.petstation.dao.PhieuTiepNhanSpaDAO` |
| `com.petstation.dao.SqlDatabase` | `com.petstation.dao.ThaoTacSQL` |
| `com.petstation.dao.SqlDatabase.Work` | `com.petstation.dao.ThaoTacSQL.CongViec` |
| `com.petstation.dao.SupplierDao` | `com.petstation.dao.NhaCungCapDAO` |
| `com.petstation.dao.SystemSettingsDao` | `com.petstation.dao.CaiDatHeThongDAO` |
| `com.petstation.dao.TransactionManager` | `com.petstation.dao.QuanLyGiaoDich` |
| `com.petstation.dao.TransactionManager.TransactionWork` | `com.petstation.dao.QuanLyGiaoDich.CongViecGiaoDich` |
| `com.petstation.dao.TransactionManagerTest` | `com.petstation.dao.QuanLyGiaoDichTest` |
| `com.petstation.dao.UserAccountDao` | `com.petstation.dao.TaiKhoanDAO` |
| `com.petstation.dao.VoucherDao` | `com.petstation.dao.VoucherDAO` |
| `com.petstation.dao.impl.JdbcAccountDao` | `com.petstation.dao.impl.JdbcQuanLyTaiKhoanDAO` |
| `com.petstation.dao.impl.JdbcApprovalDao` | `com.petstation.dao.impl.JdbcPheDuyetDAO` |
| `com.petstation.dao.impl.JdbcAuditDao` | `com.petstation.dao.impl.JdbcQuanLyNhatKyHeThongDAO` |
| `com.petstation.dao.impl.JdbcAuditLogDao` | `com.petstation.dao.impl.JdbcNhatKyHeThongDAO` |
| `com.petstation.dao.impl.JdbcBatchDao` | `com.petstation.dao.impl.JdbcLoHangDAO` |
| `com.petstation.dao.impl.JdbcCashShiftDao` | `com.petstation.dao.impl.JdbcCaThuNganDAO` |
| `com.petstation.dao.impl.JdbcCatalogDao` | `com.petstation.dao.impl.JdbcDanhMucDAO` |
| `com.petstation.dao.impl.JdbcCustomerDao` | `com.petstation.dao.impl.JdbcKhachHangDAO` |
| `com.petstation.dao.impl.JdbcCustomerHistoryDao` | `com.petstation.dao.impl.JdbcLichSuKhachHangDAO` |
| `com.petstation.dao.impl.JdbcDayLockDao` | `com.petstation.dao.impl.JdbcChotNgayDAO` |
| `com.petstation.dao.impl.JdbcEmployeeDao` | `com.petstation.dao.impl.JdbcNhanVienDAO` |
| `com.petstation.dao.impl.JdbcHotelBookingDao` | `com.petstation.dao.impl.JdbcHopDongLuuTruDAO` |
| `com.petstation.dao.impl.JdbcHotelDao` | `com.petstation.dao.impl.JdbcLuuTruDAO` |
| `com.petstation.dao.impl.JdbcHotelPricingDao` | `com.petstation.dao.impl.JdbcBangGiaHotelDAO` |
| `com.petstation.dao.impl.JdbcInventoryDao` | `com.petstation.dao.impl.JdbcKhoDAO` |
| `com.petstation.dao.impl.JdbcInvoiceDao` | `com.petstation.dao.impl.JdbcHoaDonDAO` |
| `com.petstation.dao.impl.JdbcPermissionDao` | `com.petstation.dao.impl.JdbcQuyenVaiTroDAO` |
| `com.petstation.dao.impl.JdbcPetDao` | `com.petstation.dao.impl.JdbcThuCungDAO` |
| `com.petstation.dao.impl.JdbcPriceHistoryDao` | `com.petstation.dao.impl.JdbcLichSuGiaDAO` |
| `com.petstation.dao.impl.JdbcProductDao` | `com.petstation.dao.impl.JdbcSanPhamDAO` |
| `com.petstation.dao.impl.JdbcReportingDao` | `com.petstation.dao.impl.JdbcThongKeDAO` |
| `com.petstation.dao.impl.JdbcReturnDao` | `com.petstation.dao.impl.JdbcDoiTraDAO` |
| `com.petstation.dao.impl.JdbcSalesDao` | `com.petstation.dao.impl.JdbcBanHangDAO` |
| `com.petstation.dao.impl.JdbcScheduleDao` | `com.petstation.dao.impl.JdbcLichLamViecDAO` |
| `com.petstation.dao.impl.JdbcSettingsDao` | `com.petstation.dao.impl.JdbcCaiDatDAO` |
| `com.petstation.dao.impl.JdbcSpaDao` | `com.petstation.dao.impl.JdbcXuLySpaDAO` |
| `com.petstation.dao.impl.JdbcSpaPackageDao` | `com.petstation.dao.impl.JdbcGoiDichVuSpaDAO` |
| `com.petstation.dao.impl.JdbcSpaPriceMatrixDao` | `com.petstation.dao.impl.JdbcBangGiaSpaDAO` |
| `com.petstation.dao.impl.JdbcSpaTicketDao` | `com.petstation.dao.impl.JdbcPhieuTiepNhanSpaDAO` |
| `com.petstation.dao.impl.JdbcSupplierDao` | `com.petstation.dao.impl.JdbcNhaCungCapDAO` |
| `com.petstation.dao.impl.JdbcSystemSettingsDao` | `com.petstation.dao.impl.JdbcCaiDatHeThongDAO` |
| `com.petstation.dao.impl.JdbcUserAccountDao` | `com.petstation.dao.impl.JdbcTaiKhoanDAO` |
| `com.petstation.dao.impl.JdbcVoucherDao` | `com.petstation.dao.impl.JdbcVoucherDAO` |
| `com.petstation.dao.impl.JdbcWorkflowDao` | `com.petstation.dao.impl.JdbcNghiepVuDAO` |
| `com.petstation.dao.impl.JdbcWorkflowDao.View` | `com.petstation.dao.impl.JdbcNghiepVuDAO.KieuHienThi` |
| `com.petstation.dao.impl.JdbcWorkflowLookupDao` | `com.petstation.dao.impl.JdbcLuaChonNghiepVuDAO` |
| `com.petstation.dao.impl.JdbcWorkflowSupportDao` | `com.petstation.dao.impl.JdbcHoTroNghiepVuDAO` |
| `com.petstation.gui.catalog.CustomerCatalogPanel` | `com.petstation.gui.catalog.DanhMucKhachHangPanel` |
| `com.petstation.gui.catalog.EmployeeCatalogPanel` | `com.petstation.gui.catalog.DanhMucNhanVienPanel` |
| `com.petstation.gui.catalog.LoyaltyRulesPanel` | `com.petstation.gui.catalog.QuyTacTichDiemPanel` |
| `com.petstation.gui.catalog.LoyaltyVoucherPanel` | `com.petstation.gui.catalog.TichDiemVoucherPanel` |
| `com.petstation.gui.catalog.PetCatalogPanel` | `com.petstation.gui.catalog.DanhMucThuCungPanel` |
| `com.petstation.gui.catalog.PriceHistoryPanel` | `com.petstation.gui.catalog.LichSuGiaPanel` |
| `com.petstation.gui.catalog.ProductCatalogPanel` | `com.petstation.gui.catalog.DanhMucSanPhamPanel` |
| `com.petstation.gui.catalog.ProductListPanel` | `com.petstation.gui.catalog.DanhSachSanPhamPanel` |
| `com.petstation.gui.catalog.PromotionCatalogPanel` | `com.petstation.gui.catalog.DanhMucKhuyenMaiPanel` |
| `com.petstation.gui.catalog.RoomCatalogPanel` | `com.petstation.gui.catalog.DanhMucPhongHotelPanel` |
| `com.petstation.gui.catalog.SpaServiceCatalogPanel` | `com.petstation.gui.catalog.DanhMucDichVuSpaPanel` |
| `com.petstation.gui.catalog.SupplierCatalogPanel` | `com.petstation.gui.catalog.DanhMucNhaCungCapPanel` |
| `com.petstation.gui.catalog.VoucherCatalogPanel` | `com.petstation.gui.catalog.DanhMucVoucherPanel` |
| `com.petstation.gui.components.AppColors` | `com.petstation.gui.components.MauUngDung` |
| `com.petstation.gui.components.BasePanel` | `com.petstation.gui.components.CoSoPanel` |
| `com.petstation.gui.components.CatalogFields` | `com.petstation.gui.components.TruongDanhMuc` |
| `com.petstation.gui.components.DenominationCounter` | `com.petstation.gui.components.BoDemMenhGia` |
| `com.petstation.gui.components.DesktopMenuBar` | `com.petstation.gui.components.ThanhMenuChinh` |
| `com.petstation.gui.components.DesktopStatusBar` | `com.petstation.gui.components.ThanhTrangThai` |
| `com.petstation.gui.components.DesktopTitleBar` | `com.petstation.gui.components.ThanhTieuDe` |
| `com.petstation.gui.components.DesktopToolBar` | `com.petstation.gui.components.ThanhCongCuChinh` |
| `com.petstation.gui.components.DesktopToolBar.Action` | `com.petstation.gui.components.ThanhCongCuChinh.ThaoTac` |
| `com.petstation.gui.components.Dialogs` | `com.petstation.gui.components.HopThoai` |
| `com.petstation.gui.components.EntityPanel` | `com.petstation.gui.components.QuanLyDanhMucPanel` |
| `com.petstation.gui.components.EntityPanel.Config` | `com.petstation.gui.components.QuanLyDanhMucPanel.CauHinh` |
| `com.petstation.gui.components.EntityPanel.Extra` | `com.petstation.gui.components.QuanLyDanhMucPanel.ThaoTacBoSung` |
| `com.petstation.gui.components.EntityPanel.Field` | `com.petstation.gui.components.QuanLyDanhMucPanel.TruongDuLieu` |
| `com.petstation.gui.components.EntityPanel.Mode` | `com.petstation.gui.components.QuanLyDanhMucPanel.CheDo` |
| `com.petstation.gui.components.EntityPanel.Type` | `com.petstation.gui.components.QuanLyDanhMucPanel.Kieu` |
| `com.petstation.gui.components.FlowBar` | `com.petstation.gui.components.ThanhQuyTrinh` |
| `com.petstation.gui.components.FormDialog` | `com.petstation.gui.components.BieuMauDialog` |
| `com.petstation.gui.components.FormPanel` | `com.petstation.gui.components.BieuMauPanel` |
| `com.petstation.gui.components.LineIcon` | `com.petstation.gui.components.BieuTuongNet` |
| `com.petstation.gui.components.LineIcon.Type` | `com.petstation.gui.components.BieuTuongNet.Kieu` |
| `com.petstation.gui.components.PriceTableEditorTest` | `com.petstation.gui.components.BoSoanThaoBangGiaTest` |
| `com.petstation.gui.components.RoundedPanel` | `com.petstation.gui.components.BoGocPanel` |
| `com.petstation.gui.components.StepIndicator` | `com.petstation.gui.components.ChiBaoBuoc` |
| `com.petstation.gui.components.StyledButton` | `com.petstation.gui.components.NutDinhDang` |
| `com.petstation.gui.components.StyledButton.Variant` | `com.petstation.gui.components.NutDinhDang.KieuNut` |
| `com.petstation.gui.components.TabbedCatalogPanel` | `com.petstation.gui.components.DanhMucNhieuTabPanel` |
| `com.petstation.gui.components.ToolbarAware` | `com.petstation.gui.components.HoTroThanhCongCu` |
| `com.petstation.gui.components.ToolbarCapabilities` | `com.petstation.gui.components.KhaNangThanhCongCu` |
| `com.petstation.gui.components.WeekSchedulePanel` | `com.petstation.gui.components.LichTuanPanel` |
| `com.petstation.gui.components.WeekSchedulePanel.Mode` | `com.petstation.gui.components.LichTuanPanel.CheDo` |
| `com.petstation.gui.components.WindowResizer` | `com.petstation.gui.components.ThayDoiKichThuocCuaSo` |
| `com.petstation.gui.components.WorkflowFields` | `com.petstation.gui.components.TruongNghiepVu` |
| `com.petstation.gui.components.WorkflowTablePanel` | `com.petstation.gui.components.BangNghiepVuPanel` |
| `com.petstation.gui.components.common.ActionColumn` | `com.petstation.gui.components.common.CotThaoTac` |
| `com.petstation.gui.components.common.ActionColumn.ActionButton` | `com.petstation.gui.components.common.CotThaoTac.NutThaoTac` |
| `com.petstation.gui.components.common.ActionColumn.ActionEditor` | `com.petstation.gui.components.common.CotThaoTac.BoXuLyCotThaoTac` |
| `com.petstation.gui.components.common.ActionColumn.ActionRenderer` | `com.petstation.gui.components.common.CotThaoTac.BoHienThiCotThaoTac` |
| `com.petstation.gui.components.common.CustomerPicker` | `com.petstation.gui.components.common.BoChonKhachHang` |
| `com.petstation.gui.components.common.DatePicker` | `com.petstation.gui.components.common.BoChonNgay` |
| `com.petstation.gui.components.common.DateRangePicker` | `com.petstation.gui.components.common.BoChonKhoangNgay` |
| `com.petstation.gui.components.common.ExcelExporter` | `com.petstation.gui.components.common.XuatExcel` |
| `com.petstation.gui.components.common.ExcelImporter` | `com.petstation.gui.components.common.NhapExcel` |
| `com.petstation.gui.components.common.ExcelImporter.ImportCallback` | `com.petstation.gui.components.common.NhapExcel.XuLySauNhap` |
| `com.petstation.gui.components.common.ExcelImporter.ImportResult` | `com.petstation.gui.components.common.NhapExcel.KetQuaNhap` |
| `com.petstation.gui.components.common.FieldEditor` | `com.petstation.gui.components.common.BoSoanThaoTruong` |
| `com.petstation.gui.components.common.FlexibleDiscountPanel` | `com.petstation.gui.components.common.GiamGiaLinhHoatPanel` |
| `com.petstation.gui.components.common.FlexibleDiscountPanel.DiscountType` | `com.petstation.gui.components.common.GiamGiaLinhHoatPanel.LoaiGiamGia` |
| `com.petstation.gui.components.common.FormValidator` | `com.petstation.gui.components.common.KiemTraBieuMau` |
| `com.petstation.gui.components.common.FormValidator.ValidationRule` | `com.petstation.gui.components.common.KiemTraBieuMau.QuyTacKiemTra` |
| `com.petstation.gui.components.common.PasswordFieldWithToggle` | `com.petstation.gui.components.common.TruongMatKhauAnHien` |
| `com.petstation.gui.components.common.PriceTableEditor` | `com.petstation.gui.components.common.BoSoanThaoBangGia` |
| `com.petstation.gui.components.common.SimpleBarChart` | `com.petstation.gui.components.common.BieuDoCot` |
| `com.petstation.gui.components.common.SqlForm` | `com.petstation.gui.components.common.BieuMauSQL` |
| `com.petstation.gui.components.common.SqlForm.Field` | `com.petstation.gui.components.common.BieuMauSQL.TruongDuLieu` |
| `com.petstation.gui.components.common.SqlForm.Option` | `com.petstation.gui.components.common.BieuMauSQL.LuaChon` |
| `com.petstation.gui.components.common.TableStyler` | `com.petstation.gui.components.common.DinhDangBang` |
| `com.petstation.gui.components.common.UniversalToolbar` | `com.petstation.gui.components.common.ThanhCongCuDungChung` |
| `com.petstation.gui.dashboard.DashboardPanel` | `com.petstation.gui.dashboard.TongQuanPanel` |
| `com.petstation.gui.main.MainFrame` | `com.petstation.gui.main.ChinhFrame` |
| `com.petstation.gui.navigation.MenuModel` | `com.petstation.gui.navigation.CauTrucMenu` |
| `com.petstation.gui.navigation.MenuModel.Group` | `com.petstation.gui.navigation.CauTrucMenu.Nhom` |
| `com.petstation.gui.navigation.MenuModel.Item` | `com.petstation.gui.navigation.CauTrucMenu.Muc` |
| `com.petstation.gui.navigation.MenuModelTest` | `com.petstation.gui.navigation.CauTrucMenuTest` |
| `com.petstation.gui.navigation.ScreenDefinition` | `com.petstation.gui.navigation.DinhNghiaManHinh` |
| `com.petstation.gui.navigation.ScreenFlows` | `com.petstation.gui.navigation.QuyTrinhManHinh` |
| `com.petstation.gui.navigation.ScreenFlows.Flow` | `com.petstation.gui.navigation.QuyTrinhManHinh.QuyTrinh` |
| `com.petstation.gui.navigation.ScreenFlowsTest` | `com.petstation.gui.navigation.QuyTrinhManHinhTest` |
| `com.petstation.gui.navigation.ScreenId` | `com.petstation.gui.navigation.MaManHinh` |
| `com.petstation.gui.navigation.ScreenRegistry` | `com.petstation.gui.navigation.DangKyManHinh` |
| `com.petstation.gui.navigation.ScreenRegistryTest` | `com.petstation.gui.navigation.DangKyManHinhTest` |
| `com.petstation.gui.processing.approval.ApprovalCenterPanel` | `com.petstation.gui.processing.approval.TrungTamPheDuyetPanel` |
| `com.petstation.gui.processing.approval.InvoiceRequestPanel` | `com.petstation.gui.processing.approval.YeuCauHoaDonPanel` |
| `com.petstation.gui.processing.hotel.CheckoutPanel` | `com.petstation.gui.processing.hotel.QuyetToanLuuTruPanel` |
| `com.petstation.gui.processing.hotel.HotelBookingPanel` | `com.petstation.gui.processing.hotel.DatPhongHotelPanel` |
| `com.petstation.gui.processing.hotel.HotelJournalPanel` | `com.petstation.gui.processing.hotel.NhatKyLuuTruPanel` |
| `com.petstation.gui.processing.hotel.HotelPanel` | `com.petstation.gui.processing.hotel.LuuTruHotelPanel` |
| `com.petstation.gui.processing.hotel.HotelWorkflowPanel` | `com.petstation.gui.processing.hotel.NghiepVuHotelPanel` |
| `com.petstation.gui.processing.inventory.GoodsWriteOffPanel` | `com.petstation.gui.processing.inventory.XuatHuyKhoPanel` |
| `com.petstation.gui.processing.inventory.InventoryWorkflowPanel` | `com.petstation.gui.processing.inventory.NghiepVuKhoPanel` |
| `com.petstation.gui.processing.inventory.PurchasePanel` | `com.petstation.gui.processing.inventory.NhapKhoPanel` |
| `com.petstation.gui.processing.inventory.StocktakePanel` | `com.petstation.gui.processing.inventory.KiemKePanel` |
| `com.petstation.gui.processing.sales.CashShiftPanel` | `com.petstation.gui.processing.sales.CaThuNganPanel` |
| `com.petstation.gui.processing.sales.POSPanel` | `com.petstation.gui.processing.sales.LapHoaDonPanel` |
| `com.petstation.gui.processing.sales.POSPanel.Product` | `com.petstation.gui.processing.sales.LapHoaDonPanel.SanPham` |
| `com.petstation.gui.processing.sales.POSPanel.TrackingWidthPanel` | `com.petstation.gui.processing.sales.LapHoaDonPanel.TheoChieuRongPanel` |
| `com.petstation.gui.processing.sales.PaymentPanel` | `com.petstation.gui.processing.sales.ThanhToanPanel` |
| `com.petstation.gui.processing.sales.PendingInvoicesPanel` | `com.petstation.gui.processing.sales.DonHangTreoPanel` |
| `com.petstation.gui.processing.sales.PrintInvoicePanel` | `com.petstation.gui.processing.sales.InHoaDonPanel` |
| `com.petstation.gui.processing.sales.ReturnPanel` | `com.petstation.gui.processing.sales.DoiTraPanel` |
| `com.petstation.gui.processing.sales.SalesWorkflowPanel` | `com.petstation.gui.processing.sales.NghiepVuBanHangPanel` |
| `com.petstation.gui.processing.spa.SpaClosePanel` | `com.petstation.gui.processing.spa.DongPhieuSpaPanel` |
| `com.petstation.gui.processing.spa.SpaIntakePanel` | `com.petstation.gui.processing.spa.TiepNhanSpaPanel` |
| `com.petstation.gui.processing.spa.SpaPanel` | `com.petstation.gui.processing.spa.DichVuSpaPanel` |
| `com.petstation.gui.processing.spa.SpaProgressPanel` | `com.petstation.gui.processing.spa.TienDoSpaPanel` |
| `com.petstation.gui.processing.spa.SpaSchedulePanel` | `com.petstation.gui.processing.spa.LichHenSpaPanel` |
| `com.petstation.gui.processing.spa.SpaServiceSelectionPanel` | `com.petstation.gui.processing.spa.ChonDichVuSpaPanel` |
| `com.petstation.gui.processing.spa.SpaWorkflowPanel` | `com.petstation.gui.processing.spa.NghiepVuSpaPanel` |
| `com.petstation.gui.processing.staff.QuickFlows` | `com.petstation.gui.processing.staff.ThaoTacNhanh` |
| `com.petstation.gui.processing.staff.StaffSchedulePanel` | `com.petstation.gui.processing.staff.PhanLichNhanVienPanel` |
| `com.petstation.gui.reports.CustomerReportPanel` | `com.petstation.gui.reports.ThongKeKhachHangPanel` |
| `com.petstation.gui.reports.GoodsReportPanel` | `com.petstation.gui.reports.ThongKeHangHoaPanel` |
| `com.petstation.gui.reports.HotelReportPanel` | `com.petstation.gui.reports.ThongKeHotelPanel` |
| `com.petstation.gui.reports.ReportPanel` | `com.petstation.gui.reports.BaoCaoPanel` |
| `com.petstation.gui.reports.ReportPanel.Data` | `com.petstation.gui.reports.BaoCaoPanel.DuLieu` |
| `com.petstation.gui.reports.ReportPanel.View` | `com.petstation.gui.reports.BaoCaoPanel.KieuHienThi` |
| `com.petstation.gui.reports.ReportViews` | `com.petstation.gui.reports.DuLieuBaoCao` |
| `com.petstation.gui.reports.RevenueReportPanel` | `com.petstation.gui.reports.ThongKeDoanhThuPanel` |
| `com.petstation.gui.reports.SpaReportPanel` | `com.petstation.gui.reports.ThongKeSpaPanel` |
| `com.petstation.gui.reports.StaffReportPanel` | `com.petstation.gui.reports.ThongKeNhanVienPanel` |
| `com.petstation.gui.reports.SupplierDebtReportPanel` | `com.petstation.gui.reports.ThongKeCongNoPanel` |
| `com.petstation.gui.search.BatchStockSearchPanel` | `com.petstation.gui.search.TimKiemTonTheoLoPanel` |
| `com.petstation.gui.search.CustomerDetailPanel` | `com.petstation.gui.search.ChiTietKhachHangPanel` |
| `com.petstation.gui.search.CustomerHistoryPanel` | `com.petstation.gui.search.LichSuKhachHangPanel` |
| `com.petstation.gui.search.HotelMapPanel` | `com.petstation.gui.search.TraCuuPhongHotelPanel` |
| `com.petstation.gui.search.InvoiceSearchPanel` | `com.petstation.gui.search.TimKiemHoaDonPanel` |
| `com.petstation.gui.search.PersonalSchedulePanel` | `com.petstation.gui.search.LichCaNhanPanel` |
| `com.petstation.gui.search.ScheduleSearchPanel` | `com.petstation.gui.search.TraCuuLichLamViecPanel` |
| `com.petstation.gui.search.SpaDailyPanel` | `com.petstation.gui.search.TraCuuPhieuSpaPanel` |
| `com.petstation.gui.system.AccountListPanel` | `com.petstation.gui.system.DanhSachTaiKhoanPanel` |
| `com.petstation.gui.system.AccountPermissionPanel` | `com.petstation.gui.system.TaiKhoanPhanQuyenPanel` |
| `com.petstation.gui.system.AuditLogPanel` | `com.petstation.gui.system.NhatKyHeThongPanel` |
| `com.petstation.gui.system.ChangePasswordPanel` | `com.petstation.gui.system.DoiMatKhauPanel` |
| `com.petstation.gui.system.LoginFrame` | `com.petstation.gui.system.DangNhapFrame` |
| `com.petstation.gui.system.PermissionMatrixPanel` | `com.petstation.gui.system.MaTranQuyenPanel` |
| `com.petstation.gui.system.SettingsPanel` | `com.petstation.gui.system.CaiDatPanel` |
| `com.petstation.integration.SqlServerIntegrationTest` | `com.petstation.integration.TichHopSQLServerTest` |
| `com.petstation.integration.SqlServerIntegrationTest.Rollback` | `com.petstation.integration.TichHopSQLServerTest.HoanTac` |
| `com.petstation.model.AccountStatus` | `com.petstation.model.TrangThaiTaiKhoan` |
| `com.petstation.model.AuditLogEntry` | `com.petstation.model.NhatKyHeThong` |
| `com.petstation.model.Batch` | `com.petstation.model.LoHang` |
| `com.petstation.model.CatalogType` | `com.petstation.model.LoaiDanhMuc` |
| `com.petstation.model.Customer` | `com.petstation.model.KhachHang` |
| `com.petstation.model.DayLock` | `com.petstation.model.ChotNgay` |
| `com.petstation.model.Employee` | `com.petstation.model.NhanVien` |
| `com.petstation.model.HotelBooking` | `com.petstation.model.HopDongLuuTru` |
| `com.petstation.model.HotelPricing` | `com.petstation.model.BangGiaHotel` |
| `com.petstation.model.Invoice` | `com.petstation.model.HoaDon` |
| `com.petstation.model.InvoiceItem` | `com.petstation.model.ChiTietDonHang` |
| `com.petstation.model.Permission` | `com.petstation.model.Quyen` |
| `com.petstation.model.Pet` | `com.petstation.model.ThuCung` |
| `com.petstation.model.PriceHistory` | `com.petstation.model.LichSuGia` |
| `com.petstation.model.Product` | `com.petstation.model.SanPham` |
| `com.petstation.model.Promotion` | `com.petstation.model.ChuongTrinhKhuyenMai` |
| `com.petstation.model.Role` | `com.petstation.model.VaiTro` |
| `com.petstation.model.RoleTest` | `com.petstation.model.VaiTroTest` |
| `com.petstation.model.SaleContext` | `com.petstation.model.GioHang` |
| `com.petstation.model.SaleContext.Line` | `com.petstation.model.GioHang.DongHang` |
| `com.petstation.model.SaleContext.Promotion` | `com.petstation.model.GioHang.KhuyenMai` |
| `com.petstation.model.SaleContext.Receipt` | `com.petstation.model.GioHang.BienLai` |
| `com.petstation.model.SaleContextTest` | `com.petstation.model.GioHangTest` |
| `com.petstation.model.SpaPackage` | `com.petstation.model.GoiDichVuSpa` |
| `com.petstation.model.SpaPriceMatrix` | `com.petstation.model.BangGiaSpa` |
| `com.petstation.model.SpaTicket` | `com.petstation.model.PhieuTiepNhanSpa` |
| `com.petstation.model.SpaTicketDetail` | `com.petstation.model.ChiTietPhieuSpa` |
| `com.petstation.model.Supplier` | `com.petstation.model.NhaCungCap` |
| `com.petstation.model.SystemSettings` | `com.petstation.model.CaiDatHeThong` |
| `com.petstation.model.UserAccount` | `com.petstation.model.TaiKhoan` |
| `com.petstation.model.UserSession` | `com.petstation.model.PhienDangNhap` |
| `com.petstation.model.Voucher` | `com.petstation.model.Voucher` |
| `com.petstation.model.WorkflowType` | `com.petstation.model.LoaiNghiepVu` |
| `com.petstation.service.AccountService` | `com.petstation.service.TaiKhoanService` |
| `com.petstation.service.ApprovalService` | `com.petstation.service.PheDuyetService` |
| `com.petstation.service.ApprovalService.Kind` | `com.petstation.service.PheDuyetService.Loai` |
| `com.petstation.service.ApprovalService.Request` | `com.petstation.service.PheDuyetService.YeuCau` |
| `com.petstation.service.AuditService` | `com.petstation.service.NhatKyHeThongService` |
| `com.petstation.service.AuditService.Entry` | `com.petstation.service.NhatKyHeThongService.Muc` |
| `com.petstation.service.AuthenticationException` | `com.petstation.service.XacThucException` |
| `com.petstation.service.AuthenticationService` | `com.petstation.service.XacThucService` |
| `com.petstation.service.AuthenticationService.LoginResult` | `com.petstation.service.XacThucService.KetQuaDangNhap` |
| `com.petstation.service.AuthenticationServiceTest` | `com.petstation.service.XacThucServiceTest` |
| `com.petstation.service.AuthorizationException` | `com.petstation.service.PhanQuyenException` |
| `com.petstation.service.AuthorizationService` | `com.petstation.service.PhanQuyenService` |
| `com.petstation.service.AuthorizationServiceTest` | `com.petstation.service.PhanQuyenServiceTest` |
| `com.petstation.service.CashShiftService` | `com.petstation.service.CaThuNganService` |
| `com.petstation.service.CashShiftService.Method` | `com.petstation.service.CaThuNganService.PhuongThuc` |
| `com.petstation.service.CatalogService` | `com.petstation.service.DanhMucService` |
| `com.petstation.service.CatalogService.Rows` | `com.petstation.service.DanhMucService.CacDong` |
| `com.petstation.service.CustomerHistoryService` | `com.petstation.service.LichSuKhachHangService` |
| `com.petstation.service.HotelService` | `com.petstation.service.LuuTruService` |
| `com.petstation.service.InventoryService` | `com.petstation.service.KhoService` |
| `com.petstation.service.PermissionService` | `com.petstation.service.QuyenVaiTroService` |
| `com.petstation.service.ReportingService` | `com.petstation.service.ThongKeService` |
| `com.petstation.service.ReturnService` | `com.petstation.service.DoiTraService` |
| `com.petstation.service.SalesService` | `com.petstation.service.BanHangService` |
| `com.petstation.service.ScheduleService` | `com.petstation.service.LichLamViecService` |
| `com.petstation.service.ScheduleService.Entry` | `com.petstation.service.LichLamViecService.Muc` |
| `com.petstation.service.ScheduleServiceTest` | `com.petstation.service.LichLamViecServiceTest` |
| `com.petstation.service.SessionContext` | `com.petstation.service.NguCanhPhienService` |
| `com.petstation.service.SessionService` | `com.petstation.service.PhienDangNhapService` |
| `com.petstation.service.SettingsService` | `com.petstation.service.CaiDatService` |
| `com.petstation.service.SpaService` | `com.petstation.service.SpaService` |
| `com.petstation.service.WorkflowLookupService` | `com.petstation.service.LuaChonNghiepVuService` |
| `com.petstation.service.WorkflowService` | `com.petstation.service.NghiepVuService` |
| `com.petstation.service.WorkflowService.Definition` | `com.petstation.service.NghiepVuService.DinhNghia` |
| `com.petstation.service.WorkflowSupport` | `com.petstation.service.HoTroNghiepVuService` |
| `com.petstation.util.AdminBootstrap` | `com.petstation.util.KhoiTaoQuanTri` |
| `com.petstation.util.Money` | `com.petstation.util.TienTe` |

## Lệnh kiểm tra

```powershell
mvn clean package
java -jar target/petstation.jar
java -cp target/petstation.jar com.petstation.util.KhoiTaoQuanTri
```

Lệnh tạo quản trị chỉ dùng khi thiết lập tài khoản ban đầu theo SETUP.md; không chạy trong đợt đổi tên. Để chạy đủ 46 kiểm thử, đặt `PETSTATION_SQL_TESTS=true` và `PETSTATION_DB_URL` trỏ tới `PetStationIntegrationTest` đã chuẩn bị sẵn, rồi chạy `mvn clean package`. Không dùng script thiết lập SQL để kiểm tra một thay đổi chỉ liên quan đến tên mã nguồn.

## Kết quả xác minh ngày 19/10/2026

- Maven clean package biên dịch đủ 225 tệp ứng dụng và 11 tệp kiểm thử; 46 kiểm thử đạt, không lỗi hoặc bỏ qua. Trong đó có 16 kiểm thử SQL trên `PetStationIntegrationTest`.
- Tạo được panel của cả 50 mã màn hình; mở đủ năm menu và gọi đủ 37 mục chức năng, xác nhận màn hình đích sau mỗi lần điều hướng. Kiểm thử tích hợp tiếp tục kiểm tra quyền, các tab danh mục và nghiệp vụ SQL.
- Khởi chạy `com.petstation.App` từ JAR, F5 và `scripts/run-java.ps1` đều mở màn đăng nhập. Dữ liệu dùng trong kiểm tra điều hướng được hoàn tác.
- Java Language Server đã khởi động lại và báo Ready; VS Code không có lỗi đỏ. Các cảnh báo Java hiện có không được coi là lỗi biên dịch.
- Đối chiếu đủ 5.037 chuỗi/ký tự Java trước và sau đổi tên: không thay đổi. Hai tệp SQL và cấu hình kết nối cục bộ giữ nguyên. Tham chiếu `java.util.function.Supplier` là kiểu thư viện Java, được giữ đúng tên; kiểu nghiệp vụ nhà cung cấp là `NhaCungCap`.
