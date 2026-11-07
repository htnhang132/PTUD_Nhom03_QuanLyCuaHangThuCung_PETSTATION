# PHÂN TÍCH CODE & NGHIỆP VỤ: THỐNG KÊ DOANH THU VÀ THỐNG KÊ HÀNG HÓA

> Mọi số dòng trong tài liệu này được đọc trực tiếp từ mã nguồn hiện tại (ngày 03/11/2026).
> Nếu sửa code, số dòng có thể lệch — hãy dùng `Ctrl + F` theo **tên hàm** để tìm lại.
> Sơ đồ dùng dạng chữ (ASCII) để hiển thị được trong mọi trình xem Markdown, kể cả IntelliJ.

---

## 0. TÓM TẮT 30 GIÂY

Hai màn hình này **gần như không có code riêng**. Mỗi màn hình chỉ là một danh sách "góc nhìn"
(`KieuHienThi`) truyền vào lớp cha dùng chung `BaoCaoPanel`. Lớp cha lo hết: bộ chọn kỳ, combobox
"Xem theo", nút "Xem thống kê", thẻ KPI, biểu đồ cột, biểu đồ tròn, bảng, xuất Excel, xem chứng từ.

```
Menu "Thống kê > Doanh thu / Hàng hóa"
   │  CauTrucMenu.java:103-104
   ▼
DangKyManHinh.tao()  ── kiểm tra quyền XEM_BAO_CAO_CA (dòng 336-345, 381-396)
   │  dòng 469-470: new ThongKeDoanhThuPanel() / new ThongKeHangHoaPanel()
   ▼
ThongKeDoanhThuPanel / ThongKeHangHoaPanel   (chỉ khai báo danh sách góc nhìn)
   │  extends BaoCaoPanel ; mỗi góc nhìn tạo bằng DuLieuBaoCao.taoKieuHienThi / taoKieuHienTai
   ▼
BaoCaoPanel (lớp cha dùng chung)  ── dungLai() chạy SwingWorker (dòng 404-478)
   │           ├── k.tao().apply(ky)  ──► ThongKeService.xxx()      (số liệu tổng hợp)
   │           └── ChiTietThongKeService.lay(...)                    (chứng từ chi tiết)
   ▼
ThongKeService  ── kiểm tra quyền, quyết định "xem toàn cửa hàng" hay "chỉ ca của mình"
   ▼
ThongKeDAO (interface)  ──►  ThongKeDAOImpl (câu SQL thật)
   ▼
ThaoTacSQL.truyVan()  ── PreparedStatement, trả List<Object[]>
   ▼
SQL Server: hoa_don, phieu_doi_tra, hop_dong_luu_tru, lich_su_thu_coc_hotel, chi_tiet_hoa_don,
            san_pham, lo_hang, bien_ban_kiem_ke, chi_tiet_kiem_ke, phieu_nhap_kho, phieu_xuat_kho ...
```

---

## 1. DANH SÁCH TẤT CẢ FILE LIÊN QUAN (THEO PACKAGE)

Đường dẫn gốc: `src/main/java/com/petstation/`

### 1.1. `gui.reports` — Giao diện báo cáo

| File | Vai trò | Dòng quan trọng |
|---|---|---|
| `ThongKeDoanhThuPanel.java` (83 dòng) | Màn hình doanh thu. **Kế thừa** `BaoCaoPanel` (dòng 11). Khai báo 8 góc nhìn | 13-81 |
| `ThongKeHangHoaPanel.java` (87 dòng) | Màn hình hàng hóa. **Kế thừa** `BaoCaoPanel` (dòng 11). Khai báo 6 góc nhìn | 13-85 |
| `BaoCaoPanel.java` (607 dòng) | **Lớp cha dùng chung** cho mọi màn thống kê | xem mục 3 |
| `DuLieuBaoCao.java` (159 dòng) | **Hàm dùng chung** tạo góc nhìn + tính thẻ KPI | `taoKieuHienTai` 14-49, `taoKieuHienThi` 51-153 |
| `TongHopBieuDo.java` (61 dòng) | Gộp dữ liệu biểu đồ theo Ngày/Tuần/Tháng | `tao` 15-49, `khoa` 51-60 |

### 1.2. `gui.components` và `gui.components.common` — Thành phần giao diện dùng chung

| File | Vai trò | Dòng quan trọng |
|---|---|---|
| `components/CoSoPanel.java` | **Ông nội** — `BaoCaoPanel extends CoSoPanel` | class 22; `layChiSoThe` 99; `taoTieuDeMuc` 124; `layThe` 346; `taoVungTrongSuot` 360; trợ giúp 72-81 |
| `components/HoTroThanhCongCu.java` | Interface `BaoCaoPanel` implements; có `xuLyHienThi()` | 26 |
| `common/BoChonKhoangNgay.java` | Bộ chọn kỳ (30 ngày qua, tháng này, tùy chỉnh…) | `layNgayBatDau` 166, `layNgayKetThuc` 170, `chonKy` 197 |
| `common/BieuDoCot.java` | Biểu đồ cột | `khiChon` 38, `datDuLieu` 47 |
| `common/BieuDoTron.java` | Biểu đồ tròn | `khiChon` 180, `datDuLieu` 185 |
| `common/XuatExcel.java` | Xuất bảng ra Excel | `xuatBang` 24 |

### 1.3. `gui.navigation`, `gui.main`, `model` — Điều hướng & mô hình

| File | Vai trò | Dòng quan trọng |
|---|---|---|
| `gui/navigation/CauTrucMenu.java` | Mục menu "Doanh thu", "Hàng hóa" | 103-104 |
| `gui/navigation/DangKyManHinh.java` | Đăng ký màn hình + quyền; tạo panel | đăng ký 336-345; `coTheTruyCap` 381-386; `tao` 394-396; `new ...Panel()` 469-470 |
| `gui/main/ChinhFrame.java` | Mỗi lần mở lại màn hình gọi `xuLyHienThi()` → làm mới | 313 |
| `model/MaManHinh.java` | Enum `THONG_KE_DOANH_THU`, `THONG_KE_HANG_HOA` | 55-56 |
| `model/KhoangNgay.java` | Khoảng ngày báo cáo | constructor kiểm tra 16-24; `ganDay` 38-41; `layCanTren` 44-46; `demSoNgay` 48-50 |
| `model/ChiTietBaoCao.java` | Gói dữ liệu chứng từ (cột, dòng, cột nhóm) | 12-22 |
| `model/Quyen.java` | Enum quyền `XEM_BAO_CAO_CHUNG`, `XEM_BAO_CAO_CA` | 26-27 |

### 1.4. `service` — Nghiệp vụ

| File | Vai trò | Dòng quan trọng |
|---|---|---|
| `ThongKeService.java` (271 dòng) | Kiểm tra quyền + gọi DAO | `kiemTraQuyenBaoCao` 28-31; các hàm xem mục 5, 6 |
| `ChiTietThongKeService.java` (37 dòng) | Quyền mở chứng từ chi tiết | 23-36 |
| `PhienDangNhapService.java` | Lấy phiên, id tài khoản, kiểm tra quyền | `layPhien` 45-52; `layIdTaiKhoan` 54; `layIdNhanVien` 58; `yeuCau` 62-67 |
| `CaiDatService.java` | Đọc số ngày cảnh báo hết hạn | `layHanSuDungCanhBaoSoNgay` 190-192 |

### 1.5. `dao`, `dao.impl`, `database` — Truy xuất dữ liệu

| File | Vai trò | Dòng quan trọng |
|---|---|---|
| `dao/ThongKeDAO.java` | Interface (hợp đồng) | 6-68 |
| `dao/impl/ThongKeDAOImpl.java` (421 dòng) | **Toàn bộ câu SQL thống kê** | xem mục 5, 6 |
| `dao/ChiTietThongKeDAO.java` | Interface chứng từ | 9 |
| `dao/impl/ChiTietThongKeDAOImpl.java` (406 dòng) | SQL chứng từ chi tiết | doanh thu 20-96; hàng hóa 239-316 |
| `dao/impl/CaiDatDAOImpl.java` | Đọc `SO_NGAY_TON_DONG` (được gọi thẳng từ `ThongKeDAOImpl` dòng 392) | — |
| `database/ThaoTacSQL.java` (162 dòng) | Chạy SQL có tham số | `chuanBi` 92-107; `truyVan` 109-127; `dungKetNoi` 81-90 |

### 1.6. `database/*.sql` — Dữ liệu cấu hình

| File | Nội dung | Dòng |
|---|---|---|
| `database/002_sample_data.sql` | `SO_NGAY_CANH_BAO_HET_HAN = 30` | 402 |
| `database/002_sample_data.sql` | `SO_NGAY_TON_DONG = 45` | 422 |
| `database/002_sample_data.sql` | `admin` có cả `XEM_BAO_CAO_CHUNG;XEM_BAO_CAO_CA` | 376 |
| `database/002_sample_data.sql` | `demo.sales` chỉ có `XEM_BAO_CAO_CA` | 378 |

---

## 2. CHUỖI KẾ THỪA VÀ "DÙNG CHUNG" — CHỈ RÕ DÒNG

```
JPanel (Swing)
  └── CoSoPanel                         components/CoSoPanel.java:22   (abstract, implements HoTroThanhCongCu)
        └── BaoCaoPanel                 reports/BaoCaoPanel.java:16    (extends CoSoPanel implements HoTroThanhCongCu)
              ├── ThongKeDoanhThuPanel  reports/ThongKeDoanhThuPanel.java:11
              ├── ThongKeHangHoaPanel   reports/ThongKeHangHoaPanel.java:11
              └── (ThongKeSpaPanel, ThongKeHotelPanel, ThongKeKhachHangPanel, ThongKeCongNoPanel, ThongKeNhanVienPanel cũng vậy)
```

| Thứ được dùng chung | Định nghĩa ở đâu | Hai màn hình dùng ở đâu |
|---|---|---|
| Constructor 4 tham số | `BaoCaoPanel.java:80-82` → gọi constructor 6 tham số `84-314` với `hienBoGop=true`, `uuTienBang=false` | `super(...)` dòng 13 ở cả hai file |
| Record `KieuHienThi` (một góc nhìn) | `BaoCaoPanel.java:33-49` | mỗi phần tử trong `List.of(...)` |
| Record `DuLieu` (kết quả 1 lần tải) | `BaoCaoPanel.java:17-25` | được tạo trong `DuLieuBaoCao` |
| `chiQuanLy()` (góc nhìn chỉ quản lý thấy) | `BaoCaoPanel.java:46-48` | `ThongKeHangHoaPanel.java:72` ("Biến động trong kỳ") |
| `taoKieuHienThi(...)` (góc nhìn **theo kỳ**) | `DuLieuBaoCao.java:51-153` | import tĩnh dòng 3 ở cả hai file |
| `taoKieuHienTai(...)` (góc nhìn **hiện tại**, bỏ qua kỳ) | `DuLieuBaoCao.java:14-49` | chỉ `ThongKeHangHoaPanel` dòng 25, 38, 73 |
| Thẻ KPI (card) | `CoSoPanel.layChiSoThe` dòng 99 | `BaoCaoPanel.java:447-465` |
| Làm mới khi mở lại màn hình | `HoTroThanhCongCu.java:26` → override `BaoCaoPanel.java:592-595` | `ChinhFrame.java:313` gọi |
| Trợ giúp F1 | `CoSoPanel.java:72-81` → override `BaoCaoPanel.java:597-606` | — |

**Ý nghĩa của 6 tham số `taoKieuHienThi(tieuDe, cacCot, nhan, chiSo, tienTe, truyVan)`:**

| Tham số | Nghĩa | Ví dụ "Doanh thu hoàn tất" (`ThongKeDoanhThuPanel.java:18-32`) |
|---|---|---|
| `tieuDe` | Tên hiện trong combobox "Xem theo" | `"Doanh thu hoàn tất"` |
| `cacCot` | Tiêu đề cột của bảng | 7 cột: Ngày … Lợi nhuận gộp |
| `nhan` | Cột nào làm **nhãn trục X** của biểu đồ | `0` → cột "Ngày" |
| `chiSo` | Cột nào làm **giá trị** (chiều cao cột) | `5` → "Doanh thu thuần" |
| `tienTe` | Định dạng tiền VNĐ hay số thường | `true` |
| `truyVan` | Hàm lấy dữ liệu (nhận `KhoangNgay`) | `ThongKeService::layDoanhThuHoanTat` |

---

## 3. LỚP CHA `BaoCaoPanel` — PHÂN TÍCH TỪNG ĐOẠN

File: `gui/reports/BaoCaoPanel.java`

| Dòng | Làm gì | Giải thích dễ hiểu |
|---|---|---|
| 52-59 | Khai báo `cboKy`, `cboHienThiHop`, `cboTongHop`, `pnlCacThe`, `bieuDo`, `tron` | Các ô trên màn hình |
| 59-65 | `DefaultTableModel` override `isCellEditable` trả `false` | Bảng chỉ xem, không sửa được |
| 68 | `khoangDaApDung = KhoangNgay.ganDay(30)` | Mặc định 30 ngày gần nhất |
| 92-100 | Lọc danh sách góc nhìn theo quyền | Góc nhìn có `quyen` mà người dùng không có → bị ẩn khỏi combobox |
| 102 | `cboKy.chonKy("30 ngày qua")` | Chọn sẵn kỳ |
| 103 | Đổ tên các góc nhìn vào combobox "Xem theo" | |
| 104-112 | Tạo thanh công cụ: Kỳ, "Xem theo:", "Gộp:", nút **Xem thống kê**, nút **Xuất Excel** | |
| 113-122 | Bấm **Xem thống kê** → tạo `KhoangNgay` mới → `dungLai()` | Nếu ngày bắt đầu > ngày kết thúc, `KhoangNgay` ném lỗi (`KhoangNgay.java:19-21`) → hiện cảnh báo dòng 120 |
| 123-124 | Bấm **Xuất Excel** → `XuatExcel.xuatBang(...)` | Tên file = tên báo cáo + kỳ |
| 125 | Đổi combobox "Xem theo" → `dungLai()` | Tải lại ngay, không cần bấm nút |
| 126 | Đổi "Gộp" → chỉ `veBieuDo()` | **Không** truy vấn lại DB, chỉ vẽ lại |
| 144-171 | Khung biểu đồ cột (cuộn ngang) + biểu đồ tròn | |
| 178-200 | Tỷ lệ cột : tròn = 58 : 42 | `weightx` 0.58 / 0.42 |
| 207-227 | Bảng luôn cao tối thiểu 5 dòng | |
| 233-266 | `uuTienBang=false` → biểu đồ trên, bảng dưới (56% : 44%) | |
| 286-290 | Click cột biểu đồ → `chiTiet(...)` | Mở chứng từ của cột đó |
| 291-312 | Click lát biểu đồ tròn → mở chứng từ | |
| 313 | Gọi `dungLai()` lần đầu | Mở màn hình là có dữ liệu ngay |
| **404-478** | **`dungLai()` — trái tim của màn hình** | xem bảng dưới |
| 480-519 | `veBieuDo()` | xem bảng dưới |
| 527-553 | `chiTiet(rows)` lọc chứng từ theo nhãn đã chọn | |
| 555-572 | `moBang(...)` hiện hộp thoại "Chứng từ chi tiết" + nút Xuất Excel | |

### 3.1. `dungLai()` (dòng 404-478)

| Dòng | Code | Ý nghĩa |
|---|---|---|
| 406 | `int version = ++phienTai;` | Đánh số lần tải. Người dùng đổi góc nhìn liên tục thì chỉ kết quả **mới nhất** được hiển thị |
| 407 | Lấy góc nhìn đang chọn | |
| 409 | `choPhep(cboKy, !k.hienTai())` | Góc nhìn "hiện tại" (tồn kho…) → **khóa** bộ chọn kỳ |
| 410-412 | Hiện "Đang tải…", khóa nút Xuất | |
| 414-420 | `SwingWorker.doInBackground()` chạy **2 truy vấn** ở luồng nền: `k.tao().apply(ky)` (số liệu) và `ChiTietThongKeService.lay(...)` (chứng từ) | Không làm đơ giao diện |
| 423-425 | Bỏ kết quả nếu `version` cũ hoặc **đã đổi người đăng nhập** | Chống hiển thị nhầm dữ liệu của phiên trước |
| 431 | `moHinh.setDataVector(...)` | Đổ dữ liệu vào bảng |
| 433-445 | Nhãn "Đã áp dụng: dd/MM/yyyy – dd/MM/yyyy" hoặc "Hiện tại (không theo kỳ)" | |
| 446-465 | Tạo 4 thẻ KPI; click thẻ → mở toàn bộ chứng từ | |
| 466 | `veBieuDo()` | |
| 470-474 | Lỗi (mất kết nối, thiếu quyền…) → hiện cảnh báo | |

### 3.2. `veBieuDo()` (dòng 480-519)

| Dòng | Ý nghĩa |
|---|---|
| 482-488 | Gọi `TongHopBieuDo.tao(...)` gộp theo Ngày/Tuần/Tháng rồi vẽ biểu đồ **cột** |
| 490-492 | `temporal` = nhãn có dạng ngày `yyyy-MM-dd` |
| 493-495 | `goods` = đang ở màn hàng hóa **và** góc "Sản phẩm bán chạy" |
| 496-517 | Biểu đồ **tròn** lấy từ **chứng từ**: doanh thu gộp theo cột 2 (**Nguồn**: Spa / Hotel / Hàng hóa), số tiền cột 6; bán chạy gộp theo cột 6 (**Nhóm hàng**), số tiền cột 5 |
| 518 | Trường hợp khác: biểu đồ tròn dùng chính số liệu biểu đồ cột |

### 3.3. `TongHopBieuDo.tao()` (`TongHopBieuDo.java:15-49`)

- Dòng 26-29: chế độ **Tự động**: kỳ ≤ 31 ngày → gộp theo **Ngày**; ≤ 180 ngày → **Tuần**; còn lại → **Tháng**.
- Dòng 32-37: tạo sẵn **mọi ngày trong kỳ** với giá trị 0 → ngày không bán được vẫn có cột (cao 0), biểu đồ không bị "nhảy cóc".
- Dòng 51-60: định dạng nhãn `dd/MM/yyyy`, `Tuần dd/MM/yyyy` (thứ Hai đầu tuần), `MM/yyyy`.
- Chú thích dòng 9: *chỉ gộp cho biểu đồ, không rút gọn bảng hay file Excel*.

---

## 4. `DuLieuBaoCao` — TÍNH 4 THẺ KPI

File: `gui/reports/DuLieuBaoCao.java`

### 4.1. `taoKieuHienThi` (dòng 51-153) — góc nhìn theo kỳ

| Dòng | Ý nghĩa |
|---|---|
| 63 | Gọi hàm truy vấn (ví dụ `ThongKeService::layDoanhThuHoanTat`) |
| 69-70 | Nếu bảng có ≥ 7 cột và cột 7 là "Lợi nhuận gộp" → chế độ KPI doanh thu |
| 73-78 | Tìm cột "Số đơn" / "Số hóa đơn" |
| 80-93 | Cộng dồn tổng giá trị, tổng số đơn, tổng lợi nhuận gộp |
| **99-121** | **Thẻ KPI cho "Doanh thu hoàn tất"**: Tổng số đơn · Doanh thu thuần · **AOV** = doanh thu ÷ số đơn (dòng 100) · Lợi nhuận gộp kèm **tỷ suất %** = LNG × 100 ÷ doanh thu (dòng 101, 120) |
| 124-151 | Thẻ KPI cho góc khác: Số bản ghi (hoặc Tổng số đơn) · Tổng giá trị · Giá trị TB · Kỳ báo cáo (số ngày) |

### 4.2. `taoKieuHienTai` (dòng 14-49) — góc nhìn "hiện tại"

- Dòng 21: dùng lại `taoKieuHienThi`.
- Dòng 27: **luôn truyền `KhoangNgay.ganDay(1)`**, bỏ qua kỳ người dùng chọn.
- Dòng 35-46: thẻ KPI thứ 3, 4 đổi thành "Phạm vi: Hiện tại" và "Thời điểm: ngày hôm nay".
- Dùng cho: *Hàng tồn đọng*, *Sắp hết hạn*, *Tồn kho hiện tại* (vì tồn kho là **ảnh chụp tại thời điểm**, không có khái niệm "trong kỳ").

---

## 5. THỐNG KÊ DOANH THU — 8 GÓC NHÌN

### 5.1. Thao tác người dùng

1. Menu **Thống kê → Doanh thu** (cần quyền `XEM_BAO_CAO_CA`).
2. Màn hình tự tải "Doanh thu hoàn tất" của **30 ngày qua**.
3. Chọn kỳ (Hôm nay / 7 ngày / 30 ngày / Tháng này / Tùy chỉnh) → bấm **Xem thống kê**.
4. Đổi **Xem theo** để chuyển góc nhìn (tải lại ngay).
5. Đổi **Gộp** Ngày/Tuần/Tháng (chỉ vẽ lại biểu đồ).
6. Click cột / lát tròn / thẻ KPI → hộp thoại **Chứng từ chi tiết** (có Xuất Excel riêng).
7. **Xuất Excel** → xuất bảng đang hiển thị.

### 5.2. Bảng tra 8 góc nhìn

| # | Góc nhìn (dòng trong `ThongKeDoanhThuPanel`) | Service (`ThongKeService`) | DAO (`ThongKeDAOImpl`) | Lọc theo thu ngân? |
|---|---|---|---|---|
| 1 | Doanh thu hoàn tất (18-32) | `layDoanhThuHoanTat` 181-186 | `layDoanhThuHoanTat` 238-256 | Có (dòng 253) |
| 2 | Theo ngày (33-39) | `layDoanhThuTheoNgay` 38-47 | `layDoanhThuTheoNgay` 84-105 | Có (dòng 86) |
| 3 | Theo thanh toán (40-46) | `layDoanhThu(..., "method")` 54-64 | `layDoanhThu` 107-172 | Có (dòng 142) |
| 4 | Theo thu ngân (47-53) | `layDoanhThu(..., "staff")` | như trên | Có |
| 5 | Theo nguồn (54-60) | `layDoanhThu(..., "source")` | như trên (nhánh `default`) | Có |
| 6 | Theo ca (61-67) | `layDoanhThu(..., "shift")` | như trên | Có |
| 7 | Theo khung giờ (68-74) | `layDoanhThuTheoGio` 252-255 | `layDoanhThuTheoGio` 362-371 | **Không** |
| 8 | Theo thứ trong tuần (75-81) | `layDoanhThuTheoThu` 257-260 | `layDoanhThuTheoThu` 373-383 | **Không** |

### 5.3. Góc 1 — "Doanh thu hoàn tất" (`ThongKeDAOImpl.java:238-256`)

Nghiệp vụ: doanh thu **đã ghi nhận** theo ngày thanh toán hóa đơn, kèm **lợi nhuận gộp**.

| Cột | Dòng SQL | Công thức |
|---|---|---|
| Ngày | 240 | `CAST(h.thanh_toan_luc AS date)` |
| Số đơn | 241 | `COUNT(DISTINCT h.id)` |
| Doanh thu gốc | 242 | `tam_tinh + tiền cọc đã nhận của hợp đồng gắn hóa đơn` |
| Chiết khấu/Voucher | 243 | `SUM(h.giam_gia)` |
| Hoàn trả | 244 + 249 | tổng `-chenh_lech` của phiếu đổi trả có `chenh_lech < 0` |
| **Doanh thu thuần** | 245 | `tong_tien + cọc − hoàn trả` |
| **Lợi nhuận gộp** | 246 + 250 | `doanh thu thuần − giá vốn` |

- **Giá vốn** (dòng 250): mỗi dòng hàng lấy `gia_nhap` của **đúng lô đã bán**; nếu lô không có giá → `gia_nhap_gan_nhat` của sản phẩm; nếu vẫn không có → **giả định 60% đơn giá** (`ct.don_gia * 0.6`).
- `OUTER APPLY` (dòng 248-250): giống "LEFT JOIN có subquery", hóa đơn không có cọc / đổi trả vẫn được tính (giá trị NULL → `COALESCE` thành 0).
- Dòng 253 `(? IS NULL OR s.tai_khoan_id=?)`: truyền `null` → xem tất cả; truyền id → chỉ ca của mình. Vì có **2 dấu `?`** nên Service truyền `taiKhoan` **2 lần** (`ThongKeService.java:185`) — đây là đúng, không phải lỗi.

### 5.4. Góc 2 — "Theo ngày" = dòng tiền thực thu (`ThongKeDAOImpl.java:84-105`)

Nghiệp vụ: tiền **thực sự vào két** mỗi ngày (khác với doanh thu ghi nhận).

```sql
WITH ledger AS (
  -- (1) Hóa đơn đã thanh toán, tính vào ngày thanh toán          (dòng 88-91)
  SELECT CAST(h.thanh_toan_luc AS date) ngay, h.tong_tien so_tien FROM hoa_don h ... WHERE h.trang_thai='PAID'
  UNION ALL
  -- (2) Phiếu trả hàng kiểu cũ: chenh_lech (âm = tiền trả lại khách) (dòng 92-96)
  SELECT CAST(p.tao_luc AS date), p.chenh_lech FROM phieu_doi_tra p ... WHERE p.loai_phieu='LEGACY_RETURN'
  UNION ALL
  -- (3) Tiền cọc Hotel, tính vào NGÀY NHẬN CỌC                    (dòng 97-100)
  SELECT CAST(k.nhan_coc_luc AS date), k.tien_coc FROM hop_dong_luu_tru k ... WHERE k.da_nhan_coc=1
)
SELECT ngay, SUM(so_tien) FROM ledger WHERE ngay>=? AND ngay<? GROUP BY ngay ORDER BY ngay  -- dòng 101-102
```

- `WITH ledger AS` = tạo "sổ cái tạm" gom 3 nguồn tiền về chung 2 cột `(ngay, so_tien)`, rồi cộng theo ngày.
- `ngay < ?` với tham số là `KhoangNgay.layCanTren()` = **ngày kết thúc + 1** (`KhoangNgay.java:44-46`) → lấy trọn ngày cuối dù cột có giờ phút.

### 5.5. Góc 3-6 — Theo thanh toán / thu ngân / nguồn / ca (`ThongKeDAOImpl.java:107-172`)

Một hàm, đổi **biểu thức nhóm** bằng `switch`:

| `nhomTongHop` | Nhóm hóa đơn (113-123) | Nhóm phiếu đổi trả (124-134) | Nhóm tiền cọc (135-141) |
|---|---|---|---|
| `method` | phần trước dấu `:` trong `chi_tiet_thanh_toan` | `h.hinh_thuc_thanh_toan` | `k.phuong_thuc` |
| `staff` | `n.ho_ten` | `n.ho_ten` | `n.ho_ten` |
| `shift` | `s.ten_ca` | `s.ten_ca` | `s.ten_ca` |
| `source` (default) | Spa nếu có phiếu Spa; Hotel nếu có hợp đồng; còn lại "Hàng hóa" | như trên | `'Hotel'` |

**Điểm đắt giá — thanh toán kết hợp (dòng 146, 150):**
- Một hóa đơn có thể trả nhiều cách, lưu dạng `TIEN_MAT:100000;CHUYEN_KHOAN:50000` trong `chi_tiet_thanh_toan`.
- `CROSS APPLY STRING_SPLIT(..., ';')` tách thành từng phần; `LEFT(..., CHARINDEX(':')-1)` lấy tên phương thức; `SUBSTRING(...)` lấy số tiền.
- Hóa đơn cũ không có chi tiết → ghép tạm `hinh_thuc_thanh_toan + ':' + tong_tien` (dòng 150).
- Kết quả: 1 hóa đơn trả 2 cách được chia đúng tiền vào 2 phương thức.
- Cột "Số hóa đơn" = `COUNT(DISTINCT hoa_don)` (dòng 168) — cọc và đổi trả gán `NULL` nên không bị đếm là hóa đơn.
- Tiền cọc ở góc này lấy từ bảng **`lich_su_thu_coc_hotel`** (dòng 163), mỗi lần thu cọc là 1 dòng.

### 5.6. Góc 7-8 — Theo khung giờ / thứ (`ThongKeDAOImpl.java:362-383`)

- `DATEPART(hour, thanh_toan_luc)` → nhãn `"9h:00 - 10h:00"` (dòng 364-365).
- `DATEPART(weekday, ...)`: 1 = Chủ nhật … 7 = Thứ 7 (dòng 375-377).
- Chỉ tính `hoa_don` PAID, **không** tính cọc / đổi trả, **không** lọc theo thu ngân.
- Mục đích nghiệp vụ: biết giờ cao điểm / ngày đông khách để xếp ca.

### 5.7. Chứng từ chi tiết doanh thu (`ChiTietThongKeDAOImpl.java:20-96`)

- Cột: Chứng từ · Thời gian · Nguồn · Phương thức · Thu ngân · Ca · Số tiền (dòng 91-93).
- Góc "Doanh thu hoàn tất": chỉ hóa đơn + cọc gắn hóa đơn (36-50).
- Góc khác: hóa đơn tách theo phương thức + lịch sử thu cọc + phiếu trả hàng cũ (51-81).
- `group` (82-89): cột dùng để khớp với nhãn đã click (Theo thanh toán → cột 3; thu ngân → 4; ca → 5; nguồn → 2; mặc định → cột Thời gian).

---

## 6. THỐNG KÊ HÀNG HÓA — 6 GÓC NHÌN

### 6.1. Bảng tra

| # | Góc nhìn (dòng trong `ThongKeHangHoaPanel`) | Loại | Service | DAO (`ThongKeDAOImpl`) | Quyền |
|---|---|---|---|---|---|
| 1 | Sản phẩm bán chạy (18-24) | theo kỳ | `laySanPhamBanChay` 71-80 | 174-195 | `XEM_BAO_CAO_CA`, lọc theo ca |
| 2 | Hàng tồn đọng / chôn vốn (25-37) | hiện tại | `layHangTonDong` 262-265 | 385-406 | CA (nhưng chứng từ cần CHUNG — xem 9.2) |
| 3 | Sắp hết hạn (38-44) | hiện tại | `layLoSapHetHan` 87-91 | `timHanSuDung` 22-29 | CA |
| 4 | Hao hụt từ kiểm kê (45-59) | theo kỳ | `layHaoHutKiemKe` 267-270 | 408-420 | CA (chứng từ cần CHUNG) |
| 5 | Biến động trong kỳ (60-72) | theo kỳ | `layBienDongKho` 188-201 | 258-278 | **Chỉ quản lý**: `.chiQuanLy()` dòng 72 + `yeuCau(CHUNG)` Service 189 |
| 6 | Tồn kho hiện tại (73-85) | hiện tại | `layTonKho` 82-85 | `timKho` 12-20 | CA |

### 6.2. Góc 1 — Sản phẩm bán chạy (`ThongKeDAOImpl.java:174-195`)

```
TOP(10) theo số lượng
qty    = SUM(số bán) − SUM(số đã trả lại)                                      dòng 177
amount = SUM( so_luong × don_gia × (tong_tien + credit) / tam_tinh ) − SUM(refund)  dòng 178-180
```

- `r` (OUTER APPLY dòng 183-186): số lượng và tiền hoàn của **chính dòng hàng đó** trong các phiếu đổi trả COMPLETED.
- **Phân bổ chiết khấu**: nhân `tong_tien / tam_tinh` → hóa đơn giảm 10% thì doanh thu từng món cũng giảm 10%.
- `ex.credit` (dòng 187-189): hóa đơn **thay thế** sau khi đổi hàng có `tong_tien` đã bị trừ giá trị hàng cũ. Cộng lại `gia_tri_duoc_doi` để món mới không bị tính doanh thu thấp hơn thực tế.
- Dòng 191: thu ngân chỉ thấy hàng bán trong ca của mình.
- Biểu đồ tròn của góc này nhóm theo **Nhóm hàng** (`BaoCaoPanel.java:497`; cột `p.nhom_hang` ở `ChiTietThongKeDAOImpl.java:256`).

### 6.3. Góc 2 — Hàng tồn đọng / chôn vốn (`ThongKeDAOImpl.java:385-406`)

- Dòng 386-395: đọc `SO_NGAY_TON_DONG` từ bảng `cai_dat` (mặc định **45**, `002_sample_data.sql:422`); sai định dạng → giữ 45.
- Điều kiện: còn tồn (`so_luong_ton > 0`) **VÀ** `NOT EXISTS` hóa đơn PAID nào bán sản phẩm đó trong N ngày gần nhất (dòng 400-403).
- `von_chon = tổng tồn × giá nhập gần nhất` (dòng 398-399), sắp giảm dần → món chôn nhiều tiền nhất lên đầu.

### 6.4. Góc 3 — Sắp hết hạn (`ThongKeDAOImpl.java:22-29`)

```sql
WHERE l.so_luong_ton > 0 AND l.han_su_dung <= DATEADD(day, ?, CAST(GETDATE() AS date))
ORDER BY l.han_su_dung
```

- `?` = `CaiDatService.layHanSuDungCanhBaoSoNgay()` (`ThongKeService.java:89-90` → `CaiDatService.java:190-192`) = khóa `SO_NGAY_CANH_BAO_HET_HAN` (mặc định **30**, `002_sample_data.sql:402`).
- Tính theo **từng lô** (`lo_hang`) vì cùng một sản phẩm có nhiều lô, hạn khác nhau.
- Lô **đã quá hạn** vẫn hiện (vì `han_su_dung <= hôm nay + 30` bao gồm cả quá khứ).

### 6.5. Góc 4 — Hao hụt từ kiểm kê (`ThongKeDAOImpl.java:408-420`)

- `chenh_lech = ton_thuc_te − ton_he_thong` (âm = thiếu hàng, dương = thừa).
- `gia_tri_lech = chenh_lech × gia_nhap_gan_nhat`.
- Chỉ lấy dòng có lệch (`<>`), theo ngày tạo biên bản trong kỳ, mới nhất lên trước.
- Lý do rỗng → "Chưa rõ".

### 6.6. Góc 5 — Biến động trong kỳ (`ThongKeDAOImpl.java:258-278`)

Gom 4 loại giao dịch kho bằng `UNION ALL`, số dương = nhập, số âm = xuất:

| Loại | Nguồn bảng | Dấu |
|---|---|---|
| Nhập kho | `chi_tiet_phieu_nhap` + `phieu_nhap_kho` POSTED | + |
| Bán hàng | `chi_tiet_hoa_don` + `hoa_don` PAID | − |
| Nhận hàng đổi | `chi_tiet_doi_tra` + `phieu_doi_tra` COMPLETED | + |
| Xuất hủy | `chi_tiet_phieu_xuat` + `phieu_xuat_kho` POSTED | − |

- Có **8 dấu `?`** (4 cặp từ ngày / đến ngày) → Service truyền 8 tham số (`ThongKeService.java:192-200`).
- Ẩn với nhân viên ở **2 lớp**: combobox (`BaoCaoPanel.java:92-100` + `.chiQuanLy()`) và Service (`yeuCau(CHUNG)` dòng 189).

### 6.7. Góc 6 — Tồn kho hiện tại (`ThongKeDAOImpl.java:12-20`)

- `LEFT JOIN lo_hang` + `COALESCE(SUM(so_luong_ton),0)` → sản phẩm chưa có lô nào vẫn hiện, tồn = 0.
- Hiển thị kèm `ton_toi_thieu` để so sánh mức cảnh báo.

### 6.8. Chứng từ chi tiết hàng hóa (`ChiTietThongKeDAOImpl.java:239-316`)

| Góc nhìn | Dòng | Nội dung |
|---|---|---|
| Sản phẩm bán chạy | 240-272 | Từng dòng hóa đơn: Hóa đơn, Mã SP, Ngày bán, Số bán sau đổi, Doanh thu sau đổi, **Nhóm hàng** |
| Tồn kho / Sắp hết hạn | 273-287 | Từng lô: Số lô, Hạn dùng, Tồn, Trạng thái |
| Còn lại | 288-315 | Sổ cái kho: Chứng từ, Loại giao dịch, Ngày, Số lượng |

---

## 7. LỚP TRUY XUẤT CHUNG `ThaoTacSQL`

File: `database/ThaoTacSQL.java`

| Dòng | Ý nghĩa |
|---|---|
| 109-127 | `truyVan(sql, thamSo...)`: mở `PreparedStatement`, chạy `executeQuery`, chép từng dòng thành `Object[]`, trả `List<Object[]>` |
| 92-107 | `chuanBi(...)`: gán tham số đúng kiểu — `LocalDate` → `setDate`, `LocalDateTime` → `setTimestamp`, `String` → `setNString` (hỗ trợ tiếng Việt), còn lại `setObject` |
| 81-90 | `dungKetNoi(...)`: nếu đang trong transaction thì dùng lại kết nối; nếu không thì mở transaction mới |
| 20 | `ThreadLocal<Connection>`: mỗi luồng (SwingWorker) có kết nối riêng |

Vì mọi khoảng ngày đều đi qua `?` nên **không bị SQL Injection** từ phía bộ chọn ngày.

---

## 8. PHÂN QUYỀN — 4 LỚP CHẶN

| Lớp | Ở đâu | Làm gì |
|---|---|---|
| 1. Menu | `DangKyManHinh.java:336-345`, `381-386`, `394-396` | Không có `XEM_BAO_CAO_CA` → không thấy menu; cố mở → `PhanQuyenException` |
| 2. Góc nhìn | `BaoCaoPanel.java:92-100` + `chiQuanLy()` dòng 46-48 | Ẩn "Biến động trong kỳ" khỏi combobox nếu thiếu `XEM_BAO_CAO_CHUNG` |
| 3. Service | `ThongKeService.kiemTraQuyenBaoCao()` dòng 28-31 | Có CHUNG thì qua; không thì bắt buộc có CA |
| 3'. Phạm vi dữ liệu | `ThongKeService` dòng 41-46, 57-63, 74-79, 183 | `tatCa ? null : layIdTaiKhoan()` → `null` = toàn cửa hàng; id = chỉ phiên của tài khoản đó |
| 4. Chứng từ | `ChiTietThongKeService.java:24-33` | Báo cáo hàng hóa (trừ Bán chạy, Tồn kho, Sắp hết hạn) **bắt buộc** `XEM_BAO_CAO_CHUNG` |

Dữ liệu mẫu: `admin` có cả 2 quyền (`002_sample_data.sql:376`); `demo.sales` chỉ có `XEM_BAO_CAO_CA` (dòng 378).

---

## 9. ĐIỂM CẦN BIẾT TRƯỚC KHI BẢO VỆ (CÓ THỂ BỊ HỎI / YÊU CẦU SỬA)

Các điểm dưới đây rút ra từ việc **đọc code**, chưa chạy thử trên máy. Nên tự kiểm tra lại bằng
tài khoản `demo.sales` trước buổi bảo vệ.

### 9.1. Khung giờ và Thứ trong tuần không lọc theo thu ngân
- `ThongKeService.java:252-260` và `ThongKeDAOImpl.java:362-383` không có tham số tài khoản.
- Hệ quả: nhân viên chỉ có quyền "xem báo cáo ca" vẫn thấy doanh thu **toàn cửa hàng** ở 2 góc này, trong khi 6 góc còn lại chỉ thấy ca của mình.
- Cách sửa nếu thầy yêu cầu: thêm tham số `Long taiKhoan`, JOIN `phien_lam_viec s` và nối `(? IS NULL OR s.tai_khoan_id=?)` giống dòng 253.

### 9.2. "Hàng tồn đọng" và "Hao hụt" có thể báo lỗi quyền với nhân viên
- Service chỉ yêu cầu CA (`ThongKeService.java:263, 268`), nhưng `ChiTietThongKeService.java:26-33` yêu cầu CHUNG cho 2 góc này.
- `BaoCaoPanel.dungLai()` tải số liệu **và** chứng từ trong **cùng** `doInBackground` (dòng 416-419) → một cái lỗi là cả màn hình báo lỗi (dòng 470-474).
- Cách sửa: hoặc thêm `.chiQuanLy()` cho 2 góc này trong `ThongKeHangHoaPanel`, hoặc thêm 2 tên này vào `Set.of(...)` ở `ChiTietThongKeService.java:31`.

### 9.3. Chứng từ "Sắp hết hạn" cứng 30 ngày
- Bảng chính đọc cài đặt `SO_NGAY_CANH_BAO_HET_HAN` (`ThongKeService.java:89-90`).
- Chứng từ lại viết cứng `DATEADD(day,30,...)` (`ChiTietThongKeDAOImpl.java:284`).
- Đổi cài đặt sang 60 ngày → bảng hiện lô hết hạn trong 60 ngày nhưng chứng từ chỉ hiện 30 ngày.

### 9.4. Tổng tiền giữa các góc nhìn có thể không bằng nhau — đây là do định nghĩa
| Góc | Cọc Hotel tính khi nào | Lấy từ bảng |
|---|---|---|
| Doanh thu hoàn tất | Ngày **thanh toán hóa đơn** quyết toán hợp đồng | `hop_dong_luu_tru` (dòng 248) |
| Theo ngày | Ngày **nhận cọc** | `hop_dong_luu_tru.nhan_coc_luc` (dòng 97-99) |
| Theo thanh toán/thu ngân/ca/nguồn | Ngày từng **lần thu cọc** | `lich_su_thu_coc_hotel` (dòng 163) |
| Khung giờ / Thứ | Không tính cọc | — |

Câu trả lời: *"Doanh thu hoàn tất là doanh thu ghi nhận (theo hóa đơn); Theo ngày là dòng tiền thực thu (theo lúc tiền vào két). Hai khái niệm kế toán khác nhau nên số liệu theo ngày có thể lệch, nhưng cộng cả kỳ dài thì khớp."*

### 9.5. Giá vốn dự phòng 60%
- `ThongKeDAOImpl.java:250`: không có giá nhập lô lẫn giá nhập gần nhất → lấy `don_gia × 0.6`.
- Đây là **giả định**, nên nói rõ nếu thầy hỏi "lợi nhuận gộp lấy giá vốn ở đâu".

### 9.6. Lọc tài khoản bằng nối chuỗi
- `ThongKeDAOImpl.java:86, 142, 191` và `ChiTietThongKeDAOImpl.java:17-18` nối `" AND s.tai_khoan_id=" + taiKhoan`.
- Giá trị là kiểu `Long` lấy từ phiên đăng nhập, người dùng không gõ được → **không** bị SQL Injection. Nhưng không đồng nhất với kiểu `?` ở `layDoanhThuHoanTat` (dòng 253).

### 9.7. Biến khai báo không dùng
- `ThongKeService.java:184` (`nhanVien`), 190-191, 205, 212-213, 219-220… khai báo nhưng không truyền xuống DAO. Không gây lỗi, chỉ thừa code.

### 9.8. DAO gọi DAO
- `ThongKeDAOImpl.layHangTonDong()` tự `new CaiDatDAOImpl()` (dòng 392) thay vì nhận số ngày từ Service như `layLoSapHetHan`. Không lỗi, nhưng lệch kiến trúc 3 tầng.

---

## 10. CÂU HỎI THƯỜNG GẶP — TRẢ LỜI NGẮN

**H: Hai màn hình này code ở đâu, sao file ngắn vậy?**
Đ: Mở `ThongKeDoanhThuPanel.java` dòng 11 `extends BaoCaoPanel`. Lớp con chỉ khai báo danh sách góc nhìn; giao diện, biểu đồ, bảng, xuất Excel nằm ở lớp cha `BaoCaoPanel` (607 dòng) để 7 màn thống kê dùng chung.

**H: Bấm "Xem thống kê" thì chạy những gì?**
Đ: `BaoCaoPanel.java:113-122` tạo `KhoangNgay` → `dungLai()` dòng 404 → `SwingWorker` dòng 414 gọi `ThongKeService` → `ThongKeDAOImpl` → `ThaoTacSQL.truyVan` → đổ vào bảng dòng 431, vẽ thẻ KPI 446-465, vẽ biểu đồ 466.

**H: Sao giao diện không bị đơ khi tải báo cáo nặng?**
Đ: Truy vấn chạy trong `SwingWorker.doInBackground()` (dòng 415-420), cập nhật giao diện trong `done()` (dòng 422). Biến `phienTai` (dòng 406, 423) bỏ kết quả cũ nếu người dùng đổi góc nhìn liên tục.

**H: Doanh thu thuần tính thế nào? Lợi nhuận gộp?**
Đ: `ThongKeDAOImpl.java:245-246`: thuần = tổng tiền hóa đơn + cọc − hoàn trả; LNG = thuần − giá vốn (dòng 250, ưu tiên giá nhập của đúng lô đã bán).

**H: AOV là gì, tính ở đâu?**
Đ: Giá trị trung bình mỗi đơn = doanh thu thuần ÷ số đơn, `DuLieuBaoCao.java:100`. Tỷ suất LNG ở dòng 101.

**H: Thanh toán kết hợp tiền mặt + chuyển khoản thống kê thế nào?**
Đ: `ThongKeDAOImpl.java:150` dùng `STRING_SPLIT` tách chuỗi `TIEN_MAT:100000;CHUYEN_KHOAN:50000`, dòng 146 cắt lấy số tiền → mỗi phương thức nhận đúng phần tiền.

**H: Thu ngân có xem được doanh thu của người khác không?**
Đ: `ThongKeService.java:41-46`: không có `XEM_BAO_CAO_CHUNG` thì truyền id tài khoản xuống DAO, DAO thêm `AND s.tai_khoan_id=...` (dòng 86). Ngoại lệ: 2 góc Khung giờ / Thứ (xem 9.1).

**H: Sản phẩm bán chạy có trừ hàng khách trả lại không?**
Đ: Có. `ThongKeDAOImpl.java:177`: `SUM(so_luong) − SUM(r.qty)`; `r` lấy từ `chi_tiet_doi_tra` COMPLETED (dòng 183-186).

**H: Hàng tồn đọng xác định thế nào?**
Đ: Còn tồn nhưng **không** bán được đơn PAID nào trong `SO_NGAY_TON_DONG` ngày (mặc định 45), dùng `NOT EXISTS` ở `ThongKeDAOImpl.java:400-403`. Sắp theo vốn chôn giảm dần.

**H: Đổi số ngày cảnh báo hết hạn ở đâu?**
Đ: Màn Cài đặt → khóa `SO_NGAY_CANH_BAO_HET_HAN`, đọc qua `CaiDatService.java:190-192`, dùng ở `ThongKeService.java:89-90`.

**H: Tại sao "Tồn kho hiện tại" không cho chọn kỳ?**
Đ: Tạo bằng `taoKieuHienTai` (`DuLieuBaoCao.java:14-49`) → `hienTai = true` → `BaoCaoPanel.java:409` khóa bộ chọn kỳ. Tồn kho là ảnh chụp tại thời điểm, không có khái niệm "trong kỳ".

**H: Click vào cột biểu đồ thì sao?**
Đ: `BaoCaoPanel.java:286-290` → `chiTiet()` dòng 527 lọc chứng từ (lấy từ `ChiTietThongKeDAOImpl`) theo nhãn cột → `moBang()` dòng 555 hiện hộp thoại có nút Xuất Excel riêng.

