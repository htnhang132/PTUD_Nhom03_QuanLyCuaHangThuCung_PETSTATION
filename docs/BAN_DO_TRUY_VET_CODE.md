# BẢN ĐỒ TRUY VẾT CODE PETSTATION (TRACEABILITY MATRIX)
## Kiến trúc 4 Tầng Phân hệ: GUI → Service → DAO → SQL Server

Tài liệu này phục vụ cho việc đối chiếu luồng nghiệp vụ, bảo trì hệ thống và chuẩn bị vấn đáp đồ án môn Phát triển ứng dụng. Toàn bộ 41 màn hình giao diện được ánh xạ trực tiếp từ mục đăng ký menu đến lớp xử lý nghiệp vụ, lớp truy cập dữ liệu và bảng vật lý trên SQL Server.

---

### PHÂN HỆ 1: BÁN HÀNG & DỊCH VỤ KHÁCH HÀNG (POS, SPA & HOTEL)

| Tên Màn hình (GUI) | Định danh (`MaManHinh`) | Package GUI | Tầng Service | Tầng DAO & Interface | Bảng CSDL SQL Server |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Lập hóa đơn bán hàng** | `LAP_HOA_DON` | `gui.banhang` | `BanHangService`, `GioHangService` | `HoaDonDAOImpl`, `NghiepVuDAOImpl` | `hoa_don`, `chi_tiet_hoa_don`, `san_pham`, `lo_hang` |
| **Thanh toán & Thu tiền** | `THANH_TOAN` | `gui.banhang` | `BanHangService`, `GioHangService` | `HoaDonDAOImpl`, `KhoanThuDAOImpl` | `hoa_don`, `dong_thanh_toan_hoa_don`, `phien_lam_viec` |
| **Đổi trả hàng hóa** | `DOI_TRA` | `gui.banhang` | `DoiTraService`, `DonDoiTreoService` | `DoiTraDAOImpl`, `DonDoiTreoDAOImpl` | `phieu_doi_tra`, `chi_tiet_doi_tra`, `don_doi_treo` |
| **Quản lý đơn treo** | `DON_HANG_TREO` | `gui.banhang` | `BanHangService` | `NghiepVuDAOImpl` | `gio_hang_treo`, `chi_tiet_gio_hang_treo` |
| **In & Xem lại biên lai** | `IN_HOA_DON` | `gui.banhang` | `BanHangService` | `HoaDonDAOImpl` | `hoa_don`, `chi_tiet_hoa_don`, `cai_dat_he_thong` |
| **Ca thu ngân (Đầu/Cuối ca)** | `CA_THU_NGAN` | `gui.banhang` | `CaThuNganService` | `PhienLamViecDAOImpl` | `phien_lam_viec`, `hoa_don`, `lich_su_thu_coc_hotel` |
| **Tìm kiếm hóa đơn** | `TIM_KIEM_HOA_DON` | `gui.banhang` | `BanHangService` | `HoaDonDAOImpl` | `hoa_don`, `nhan_vien`, `khach_hang` |
| **Tiếp nhận & Lập phiếu Spa** | `TIEP_NHAN_SPA` | `gui.spa` | `TiepNhanSpaService`, `PhieuSpaTreoService` | `PhieuTiepNhanSpaDAOImpl`, `PhieuSpaTreoDAOImpl` | `phieu_tiep_nhan_spa`, `phieu_spa_treo`, `goi_dich_vu_spa` |
| **Lịch hẹn dịch vụ Spa** | `LICH_HEN_SPA` | `gui.spa` | `LichHenSpaService` | `LichHenSpaDAOImpl` | `lich_hen`, `khach_hang`, `thu_cung`, `nhan_vien` |
| **Danh mục gói dịch vụ Spa** | `DANH_MUC_DICH_VU_SPA` | `gui.spa` | `DanhMucSpaService` | `GoiDichVuSpaDAOImpl` | `goi_dich_vu_spa` |
| **Tra cứu phiếu Spa** | `TRA_CUU_PHIEU_SPA` | `gui.spa` | `TiepNhanSpaService` | `PhieuTiepNhanSpaDAOImpl` | `phieu_tiep_nhan_spa`, `thu_cung`, `khach_hang` |
| **Quản lý lưu trú Hotel** | `LUU_TRU_HOTEL` | `gui.luutru` | `LuuTruService`, `ChamSocService` | `HopDongLuuTruDAOImpl`, `PhongHotelDAOImpl` | `hop_dong_luu_tru`, `phong_hotel`, `nhat_ky_cham_soc` |
| **Sơ đồ phòng lưu trú** | `TRA_CUU_PHONG_HOTEL` | `gui.luutru` | `QuyTrinhService`, `LuuTruService` | `PhongHotelDAOImpl`, `QuyTrinhDAOImpl` | `phong_hotel`, `hop_dong_luu_tru` |
| **Danh mục phòng Hotel** | `DANH_MUC_PHONG_HOTEL` | `gui.luutru` | `DanhMucHotelService` | `PhongHotelDAOImpl` | `phong_hotel` |

---

### PHÂN HỆ 2: QUẢN LÝ KHO & SẢN PHẨM

| Tên Màn hình (GUI) | Định danh (`MaManHinh`) | Package GUI | Tầng Service | Tầng DAO & Interface | Bảng CSDL SQL Server |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Danh mục sản phẩm** | `DANH_MUC_SAN_PHAM` | `gui.kho` | `SanPhamService` | `SanPhamDAOImpl`, `DanhMucDAOImpl` | `san_pham`, `lo_hang` |
| **Danh mục nhà cung cấp** | `DANH_MUC_NHA_CUNG_CAP` | `gui.kho` | `SanPhamService` | `DanhMucDAOImpl` | `nha_cung_cap` |
| **Nhập kho hàng hóa** | `NHAP_KHO` | `gui.kho` | `NhapXuatKhoService` | `NhapXuatKhoDAOImpl` | `phieu_nhap_kho`, `chi_tiet_phieu_nhap`, `lo_hang` |
| **Xuất hủy hàng hóa** | `XUAT_HUY_KHO` | `gui.kho` | `NhapXuatKhoService` | `NhapXuatKhoDAOImpl` | `phieu_xuat_kho`, `chi_tiet_phieu_xuat`, `lo_hang` |
| **Kiểm kê định kỳ kho** | `KIEM_KE` | `gui.kho` | `KiemKeService` | `KiemKeDAOImpl` | `bien_ban_kiem_ke`, `chi_tiet_kiem_ke`, `lo_hang` |
| **Tra cứu tồn theo lô** | `TIM_KIEM_TON_THEO_LO` | `gui.kho` | `SanPhamService` | `SanPhamDAOImpl` | `lo_hang`, `san_pham` |

---

### PHÂN HỆ 3: ĐỐI TÁC, KHÁCH HÀNG & NHÂN SỰ

| Tên Màn hình (GUI) | Định danh (`MaManHinh`) | Package GUI | Tầng Service | Tầng DAO & Interface | Bảng CSDL SQL Server |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Quản lý khách hàng** | `DANH_MUC_KHACH_HANG` | `gui.khachhang` | `KhachHangService` | `KhachHangDAOImpl` | `khach_hang` |
| **Lịch sử giao dịch khách** | `LICH_SU_KHACH_HANG` | `gui.khachhang` | `KhachHangService` | `KhachHangDAOImpl`, `HoaDonDAOImpl` | `khach_hang`, `hoa_don`, `thu_cung` |
| **Hồ sơ thú cưng** | `DANH_MUC_THU_CUNG` | `gui.khachhang` | `KhachHangService` | `ThuCungDAOImpl` | `thu_cung`, `khach_hang` |
| **Chương trình khuyến mãi** | `DANH_MUC_KHUYEN_MAI` | `gui.khachhang` | `KhuyenMaiService` | `ChuongTrinhKhuyenMaiDAOImpl` | `chuong_trinh_khuyen_mai` |
| **Tích điểm & Voucher** | `TICH_DIEM_VOUCHER` | `gui.khachhang` | `KhuyenMaiService` | `VoucherDAOImpl` | `voucher`, `khach_hang` |
| **Danh sách nhân viên** | `DANH_MUC_NHAN_VIEN` | `gui.nhansu` | `NhanVienService` | `NhanVienDAOImpl` | `nhan_vien`, `tai_khoan` |
| **Phân lịch & Ca làm việc** | `PHAN_LICH_NHAN_VIEN` | `gui.nhansu` | `LichLamViecService` | `CaLamViecDAOImpl`, `LichLamViecDAOImpl` | `ca_lam_viec`, `lich_lam_viec`, `nhan_vien` |
| **Tra cứu lịch làm việc** | `TRA_CUU_LICH_LAM_VIEC` | `gui.nhansu` | `LichLamViecService` | `LichLamViecDAOImpl` | `lich_lam_viec`, `nhan_vien`, `ca_lam_viec` |
| **Lịch cá nhân nhân viên** | `LICH_CA_NHAN` | `gui.nhansu` | `LichLamViecService` | `LichLamViecDAOImpl` | `lich_lam_viec`, `ca_lam_viec` |

---

### PHÂN HỆ 4: THỐNG KÊ, BÁO CÁO & BIỂU ĐỒ DOANH NGHIỆP

| Tên Màn hình (GUI) | Định danh (`MaManHinh`) | Package GUI | Tầng Service | Tầng DAO & Interface | Bảng CSDL SQL Server |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Tổng quan Dashboard** | `TONG_QUAN` | `gui.hethong` | `ThongKeService` | `ThongKeDAOImpl` | `hoa_don`, `hop_dong_luu_tru`, `phieu_tiep_nhan_spa` |
| **Thống kê doanh thu** | `THONG_KE_DOANH_THU` | `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `hoa_don`, `dong_thanh_toan_hoa_don`, `phien_lam_viec` |
| **Thống kê hàng hóa & kho**| `THONG_KE_HANG_HOA` | `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `san_pham`, `lo_hang`, `chi_tiet_hoa_don` |
| **Thống kê dịch vụ Spa** | `THONG_KE_SPA` | `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `phieu_tiep_nhan_spa`, `goi_dich_vu_spa` |
| **Thống kê phòng Hotel** | `THONG_KE_HOTEL` | `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `hop_dong_luu_tru`, `phong_hotel`, `lich_su_thu_coc_hotel`|
| **Thống kê khách hàng** | `THONG_KE_KHACH_HANG`| `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `khach_hang`, `hoa_don` |
| **Thống kê nhân viên** | `THONG_KE_NHAN_VIEN` | `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `nhan_vien`, `hoa_don`, `phien_lam_viec` |
| **Thống kê công nợ NCC** | `THONG_KE_CONG_NO` | `gui.thongke` | `ThongKeService`, `ChiTietThongKeService` | `ThongKeDAOImpl`, `ChiTietThongKeDAOImpl` | `nha_cung_cap`, `phieu_nhap_kho` |

---

### PHÂN HỆ 5: PHÊ DUYỆT & QUẢN TRỊ HỆ THỐNG

| Tên Màn hình (GUI) | Định danh (`MaManHinh`) | Package GUI | Tầng Service | Tầng DAO & Interface | Bảng CSDL SQL Server |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Trung tâm phê duyệt** | `TRUNG_TAM_PHE_DUYET` | `gui.pheduyet`| `PheDuyetService`, `DeXuatGiaService` | `YeuCauPheDuyetDAOImpl`, `DeXuatGiaDAOImpl` | `yeu_cau_phe_duyet` |
| **Tài khoản & Phân quyền** | `TAI_KHOAN_PHAN_QUYEN`| `gui.hethong` | `XacThucService`, `TaiKhoanService` | `TaiKhoanDAOImpl`, `NhanVienDAOImpl` | `tai_khoan`, `nhan_vien` |
| **Đổi mật khẩu người dùng**| `DOI_MAT_KHAU` | `gui.hethong` | `XacThucService` | `TaiKhoanDAOImpl` | `tai_khoan` |
| **Nhật ký thao tác hệ thống**| `NHAT_KY_HE_THONG` | `gui.hethong` | `NhatKyHeThongService` | `NhatKyHeThongDAOImpl` | `nhat_ky_he_thong`, `tai_khoan` |
| **Cài đặt cửa hàng & tham số**| `CAI_DAT` | `gui.hethong` | `CaiDatService` | `CaiDatHeThongDAOImpl` | `cai_dat_he_thong` |

---

### QUY TẮC PHÂN TẦNG VÀ KIỂM SOÁT PHỤ THUỘC (ARCHITECTURAL INVARIANTS)
1. **Giao diện mỏng (Thin View / GUI):**
   - GUI chỉ xử lý hiển thị Swing/AWT, nhận tương tác người dùng và gọi trực tiếp xuống các Service tương ứng.
   - Tuyệt đối cấm GUI gọi DAO hoặc kết nối CSDL trực tiếp (`com.petstation.dao.*`, `com.petstation.database.*`).
2. **Nghiệp vụ tập trung (Service):**
   - Mọi kiểm tra hợp lệ nghiệp vụ (Validation), phân quyền (`PhienDangNhapService.yeuCau`), điều phối giao dịch (`thucHienGiaoDich`) đều thực hiện tại Service.
   - Tuyệt đối cấm Service import thư viện đồ họa Swing/AWT (`javax.swing.*`, `java.awt.*`).
3. **Truy cập CSDL phân tách (DAO):**
   - Mỗi Interface DAO và Implementation tương ứng chỉ đảm nhiệm thao tác SQL CRUD trên các thực thể thuộc phân hệ của mình.
   - Tên file bắt buộc theo quy chuẩn: `<Ten>DAO` và `<Ten>DAOImpl` nằm đối xứng theo cấu trúc package.
   - Tuyệt đối cấm DAO gọi ngược lên Service hoặc GUI.
4. **Đối tượng chuyển vận dữ liệu (`dto`):**
   - Tách biệt rõ giữa Entity vật lý gắn với bảng SQL (`model`) và các DTO chuyển vận, phiên làm việc trong RAM (`dto`).
   - Cấm `dto` phụ thuộc vào các tầng xử lý `service`, `dao`, `gui`, `database`.

