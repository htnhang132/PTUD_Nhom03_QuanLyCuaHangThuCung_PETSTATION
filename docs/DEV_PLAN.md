# Kế hoạch triển khai code – Pet Station (Nhóm 03)

Lập ngày 10/10/2026, hạn nộp 27/10/2026. Bản đầy đủ kèm rà soát tài liệu: `Ke_hoach_trien_khai_va_ra_soat_tai_lieu.docx`.

## Quy tắc Git

| Nội dung | Quy tắc |
|---|---|
| Nhánh | main chỉ chứa bản ổn định để nộp/báo cáo; develop là nhánh tích hợp; mỗi chức năng một nhánh feature/<tên-chức-năng> tách từ develop. Không commit trực tiếp lên main. |
| Quy trình một chức năng | git checkout develop && git pull → git checkout -b feature/<tên> → commit nhỏ, đúng tiền tố → git fetch && git rebase origin/develop → git push -u origin feature/<tên> → mở Pull Request vào develop → ít nhất 1 thành viên duyệt → Squash and merge → xóa nhánh. |
| Commit message | Dùng Conventional Commits: feat, fix, docs, refactor (và có thể chore, test, ci, build). Dạng “feat(phạm-vi): mô tả ngắn bằng tiếng Anh, thì hiện tại”. Cấm commit chung chung như update, fix bug, 123, asdf. |
| Pull Request | Tiêu đề theo commit; mô tả: làm gì, ảnh chụp màn hình (nếu có giao diện), cách kiểm tra, quy định/UC liên quan. Danh sách kiểm tra: biên dịch xanh, test mới/đã cập nhật, không để mã dữ liệu mẫu, đã cập nhật tài liệu, đúng quy ước đặt tên. |
| Quy ước đặt tên | Tên tiếng Việt không dấu theo SQL: lớp/file PascalCase (KhachHang, BanHangService); biến/hàm camelCase (hoTen, tinhTongTien); hằng UPPER_SNAKE_CASE; giữ hậu tố DAO/Service/Panel/Frame/Dialog/Exception/Test, tiền tố Swing và package hiện tại. Xem CONTRIBUTING.md và docs/TEN_MA_NGUON.md. |
| Phát hành | Từ develop tạo release/v1.0, sửa lỗi cuối, Pull Request vào main, gắn tag v1.0.0 (ngày 25/10/2026). Sau tag chỉ nhận hotfix qua nhánh fix/*. |

## Quyết định cần chốt (Giai đoạn 0)

| # | Quyết định | Liên quan |
|---|---|---|
| 1 | Điểm và voucher: chọn theo Quy định 7 (200 điểm → voucher 150.000₫, 60 ngày, chỉ Spa); bỏ “dùng điểm 1 điểm = 1.000₫”. | C2, C10, D1 |
| 2 | Số ca mỗi ngày: 2 (theo SRS) hay 3 (Sáng, Chiều, Tối)? | C7, D10 |
| 3 | Thanh toán: một phương thức mỗi hóa đơn (theo Quy định 3) hay cho kết hợp? | C5 |
| 4 | Đổi trả: bỏ hoàn tiền, chỉ đổi Phụ kiện trong 3 ngày (theo Quy định 14). | C6, D4 |
| 5 | Phí trả phòng trễ: bổ sung vào SRS hay bỏ khỏi Cài đặt? | C9, D11 |
| 6 | Ai được thu tiền dịch vụ Spa/Hotel: Nhân viên bán hàng hay cả Nhân viên Spa/Hotel? | D3 |
| 7 | Ai sửa giá bán: chỉ Cửa hàng trưởng; Nhân viên kho đề xuất giá. | C4, D2 |
| 8 | Mốc cận hạn: dùng 4 mốc 90/60/45/<30 và bảng giảm giá theo SRS. | A3, C8, D6 |

## Giai đoạn 0: Chuẩn hóa repository, quy trình và tài liệu

- **Thời gian:** 10/10 – 11/10/2026
- **Phụ trách:** Nhàng (chính); cả nhóm họp 1 buổi để chốt 8 quyết định
- **Nhánh:** `chore/repo-setup, docs/sync-with-ui`

**Công việc**

1. Tạo/kiểm tra repository trong GitHub Organization: nhánh main (cấm push trực tiếp, bắt buộc Pull Request và 1 người duyệt) và develop.
2. Thêm CONTRIBUTING.md, .gitignore (target/, .idea/, *.class), mẫu Pull Request, nhãn issue feat/fix/docs/refactor, workflow build kiểm thử.
3. Đưa mã giao diện phát thảo hiện tại vào develop và gắn tag v0.1-gui-prototype.
4. Họp chốt 8 quyết định ở mục 4 và ghi vào docs/DECISIONS.md.
5. Sửa tài liệu theo mục 2: First Plan, OOAD (bìa, thêm Use Case, Activity, Sequence, ERD), SRS/QAs (câu 2, 6, 13, 24; bổ sung yêu cầu phân lịch, cài đặt, xuất báo cáo).

**Commit mẫu**

- `chore: add contributing guide, pull request template and gitignore`
- `ci: add maven build and test workflow`
- `docs: record team decisions on points, shifts and payment rules`
- `docs: fix OOAD cover and add use case, activity, sequence and ERD diagrams`
- `docs: align SRS question answers with 4 roles and voucher rule`

**Tiêu chí hoàn thành**

- main và develop được bảo vệ; mọi thành viên clone và chạy được mvn -q -DskipTests package.
- docs/DECISIONS.md có 8 quyết định đã được cả nhóm đồng ý.
- Danh sách mâu thuẫn mục 2 được đánh dấu đã sửa hoặc đã chuyển thành việc code.

## Giai đoạn 1: Cơ sở dữ liệu v2 và kiến trúc n-Tier

- **Thời gian:** 12/10 – 13/10/2026
- **Phụ trách:** Vỉ (CSDL), Nhàng (kiến trúc)
- **Nhánh:** `feature/db-schema-v2, feature/ntier-foundation`

**Công việc**

1. Vỉ: viết 005_schema_v2.sql theo OOAD đã chốt: bảng goi_dich_vu_spa, bang_gia_spa, chi_tiet_phieu_spa, bang_gia_hotel, cai_dat, lich_su_gia, chot_ngay; sửa voucher (chủ sở hữu, loại), thu_cung, tiem_phong, hoa_don, phien_lam_viec, yeu_cau_phe_duyet; cập nhật dữ liệu mẫu; script chạy từ đầu run_all.sql; vẽ lại ERD.
2. Nhàng: chuẩn hóa gói gui → service (BLL) → dao (DAL) → model (DTO); mỗi nghiệp vụ có interface Service và Repository; cài đặt InMemory (để giao diện chạy khi chưa có CSDL) và Jdbc; dùng QuanLyGiaoDich cho giao dịch nhiều bước.
3. Thay dần DemoStore bằng Repository; giao diện không gọi trực tiếp dữ liệu mẫu.
4. Thêm test tích hợp DAO gắn thẻ db, chạy khi có biến môi trường kết nối SQL Server.

**Commit mẫu**

- `feat(db): add schema v2 migration for spa price matrix and hotel pricing`
- `feat(db): add settings, price history and day lock tables`
- `docs: update ERD to match schema v2`
- `refactor: introduce service and repository interfaces`
- `feat: add in-memory and jdbc repository implementations`
- `test: add database integration tests tagged db`

**Tiêu chí hoàn thành**

- Chạy 001 → 005 trên SQL Server sạch không lỗi; ERD khớp 100% sơ đồ lớp đã sửa.
- Ứng dụng vẫn chạy với InMemory khi không cấu hình CSDL.
- mvn test xanh trên develop.

## Giai đoạn 2: Danh mục, tài khoản và nhật ký nối cơ sở dữ liệu

- **Thời gian:** 13/10 – 15/10/2026
- **Phụ trách:** Vỉ, Nhàng, Đạt, Vy làm song song
- **Nhánh:** `feature/catalog-customer-pet, feature/catalog-product-price, feature/catalog-service-room, feature/catalog-supplier-promo-voucher, feature/auth-audit`

**Công việc**

1. Vỉ – Khách hàng, Thú cưng: Repository JDBC, SĐT duy nhất, bảng tiêm phòng (số mũi, ngày tiêm), bỏ loài “Khác”, ảnh sổ tiêm, tìm theo SĐT.
2. Nhàng – Sản phẩm và giá: mã vạch, ngành hàng, quy cách, vị trí kệ, nhà cung cấp; Nhân viên kho đề xuất giá, Cửa hàng trưởng duyệt; Lịch sử giá lưu CSDL.
3. Đạt – Dịch vụ Spa và Phòng Hotel: 4 gói × loài × khoảng cân nặng, dịch vụ lẻ và phụ thu; bảng giá Hotel theo khung giờ và cân nặng.
4. Vy – Nhà cung cấp, Khuyến mãi, Voucher: voucher đổi điểm (cá nhân) tách khỏi voucher chương trình; trạng thái tự tính theo ngày lưu CSDL.
5. Nhàng và Vỉ – Tài khoản và nhật ký: NhatKyHeThongDAO dùng ở mọi dịch vụ, nghỉ việc khóa tài khoản trong cùng transaction.

**Commit mẫu**

- `feat(customer): persist customer and pet profiles with unique phone`
- `feat(pet): add vaccination records and eligibility fields`
- `feat(product): add price proposal and approval flow`
- `feat(spa): add combo price matrix by species and weight`
- `feat(hotel): add room pricing by time frame and weight`
- `feat(voucher): split redeemed vouchers from campaign vouchers`
- `feat(audit): write audit log from every service in one transaction`

**Tiêu chí hoàn thành**

- Mọi màn Danh mục đọc ghi CSDL; đóng mở lại ứng dụng dữ liệu còn.
- Bộ test kiểm tra dữ liệu (SĐT, email, CCCD, ngày) vẫn đạt.
- Nhật ký do NhatKyHeThongService ghi và đọc từ CSDL.

## Giai đoạn 3: Bán hàng POS, thanh toán, đổi trả, tích điểm

- **Thời gian:** 14/10 – 17/10/2026
- **Phụ trách:** Đạt (POS, tích điểm), Vy (đổi trả), Nhàng (khóa hóa đơn, yêu cầu sửa/hủy)
- **Nhánh:** `feature/pos-checkout, feature/loyalty-voucher, feature/returns, feature/invoice-lock-request`

**Công việc**

1. Đạt – POS: đơn nháp lưu CSDL, quét mã, chặn hàng hết tồn/hết hạn theo lô, gợi ý lô FEFO, khuyến mãi tự áp, thanh toán trong một transaction (lưu hóa đơn, trừ lô, cộng điểm, ghi doanh thu ca, idempotency_key), một phương thức mỗi hóa đơn, QR chuyển khoản.
2. Đạt – Điểm và voucher: 10.000₫ = 1 điểm làm tròn xuống; đổi 200 điểm lấy voucher 150.000₫ (trừ điểm thành công mới phát), hạn 60 ngày, chỉ Spa; lịch sử điểm; bỏ “dùng điểm 1 điểm = 1.000₫”.
3. Vy – Đổi trả: kiểm tra 3 điều kiện, không hoàn tiền, sản phẩm mới giá ≥ cũ, thu chênh lệch, tồn 2 chiều, điều chỉnh điểm; vượt hạn mức thì gửi Phê duyệt.
4. Nhàng – Khóa hóa đơn ở tầng dịch vụ và CSDL; yêu cầu sửa/hủy lưu yeu_cau_phe_duyet; in lại hóa đơn.

**Commit mẫu**

- `feat(pos): persist draft invoices and block expired or out-of-stock lots`
- `feat(payment): complete checkout in one transaction with idempotency key`
- `feat(loyalty): redeem 200 points for a 60-day spa voucher`
- `fix(loyalty): remove point-to-cash redemption at pos`
- `feat(returns): validate accessory-only exchange within 3 days without refund`
- `feat(invoice): lock paid invoices and route changes to approval`

**Tiêu chí hoàn thành**

- Chạy luồng: mở ca → lập đơn → thanh toán → in; tồn, điểm, doanh thu ca đúng; ngắt giữa chừng thì hoàn tác toàn bộ.
- Test TC-POS và TC-RET mới đạt.
- POS phản hồi ≤ 2 giây với 10.000 sản phẩm mẫu.

## Giai đoạn 4: Kho theo lô, FEFO và cảnh báo hạn dùng

- **Thời gian:** 14/10 – 17/10/2026 (song song với Giai đoạn 3)
- **Phụ trách:** Vỉ (nhập kho), Nhàng (cảnh báo, kiểm kê)
- **Nhánh:** `feature/inventory-batch, feature/stock-issue-take, feature/expiry-alerts`

**Công việc**

1. Vỉ – Nhập kho: phiếu có nhà cung cấp, số lô, HSD, giá, công nợ; xác nhận của Thủ kho và Cửa hàng trưởng; cộng tồn theo lô, lịch sử biến động, khóa phiếu.
2. Nhàng – Xuất kho/xuất hủy theo FEFO có ảnh và lý do, ngưỡng cần duyệt lấy từ Cài đặt; Kiểm kê tuần/tháng, chênh lệch → duyệt → điều chỉnh.
3. Nhàng – Cảnh báo: tác vụ quét hằng ngày, 4 mốc 90/60/45/<30 kèm đề xuất giảm giá, chặn bán hàng hết hạn, cảnh báo tồn tối thiểu; hiển thị ở Tồn kho theo lô và Dashboard.

**Commit mẫu**

- `feat(inventory): receive goods by batch with expiry and supplier debt`
- `feat(inventory): confirm receipts with warehouse and manager approval`
- `feat(inventory): issue and write off stock by fefo`
- `feat(stocktake): compare counted and book stock per batch`
- `feat(expiry): add 90/60/45/30 day alerts with discount suggestions`

**Tiêu chí hoàn thành**

- Nhập một phiếu có 2 lô, bán hàng lấy đúng lô gần hạn nhất.
- Hàng hết hạn không thể thêm vào hóa đơn.
- Dashboard hiện đủ cảnh báo tồn thấp, cận hạn, hết hạn.

## Giai đoạn 5: Dịch vụ Spa – Grooming và Pet Hotel

- **Thời gian:** 14/10 – 18/10/2026 (song song)
- **Phụ trách:** Vy (Spa), Vinh (Hotel)
- **Nhánh:** `feature/spa-intake, feature/hotel-stay, feature/reminders`

**Công việc**

1. Vy – Spa: tra khách theo SĐT, sàng lọc y tế (≥ 2 tháng tuổi, ≥ 2 mũi, mũi gần nhất ≥ 14 ngày, từ chối theo tình trạng), cân thực tế, giá = Loài × Cân nặng × Gói + Phụ thu, kiểm tra trùng lịch thợ, phiếu số và in 3 liên, trạng thái Chờ tiếp nhận → Đang thực hiện → Hoàn thành (ghi người, giờ), đóng phiếu chỉ khi Hoàn thành rồi chuyển Thanh toán áp voucher.
2. Vinh – Hotel: đặt phòng (chặn trùng thời gian), kiểm tra y tế hoặc cam kết, cọc 50% chuyển khoản, nhật ký hằng ngày kèm ảnh, dịch vụ phát sinh, trả phòng quyết toán (chi phí − cọc), phòng chuyển Đang dọn → Trống, cảnh báo quá hạn và xử lý vi phạm sau 7 ngày.
3. Vy – Nhắc lịch Spa/Hotel trong ngày trên Dashboard.

**Commit mẫu**

- `feat(spa): screen pets for age and vaccination before intake`
- `feat(spa): price tickets by species weight combo and surcharge`
- `feat(spa): prevent double booking of the same groomer`
- `feat(spa): print three-part service ticket`
- `feat(hotel): block overlapping room bookings and require 50 percent deposit`
- `feat(hotel): settle stay and move room to cleaning`
- `feat(hotel): flag stays overdue by more than 7 days`

**Tiêu chí hoàn thành**

- Chạy trọn luồng Spa và Hotel đến thanh toán; hóa đơn nạp đúng khoản và voucher.
- Các test TC-SPA, TC-HOT mới đạt.
- Không còn mục “Chưa hiện thực” cho Spa và Hotel trong bảng kiểm thử.

## Giai đoạn 6: Ca, Phê duyệt, Lịch, Thống kê, xuất báo cáo, ngoại tuyến

- **Thời gian:** 16/10 – 19/10/2026
- **Phụ trách:** Vinh (ca, lịch), Nhàng (phê duyệt, ngoại tuyến), Đạt (thống kê, xuất)
- **Nhánh:** `feature/shift-handover, feature/approvals-db, feature/schedule-persist, feature/reports-dashboard, feature/export-excel-pdf, feature/offline-sync`

**Công việc**

1. Vinh – Mở ca/Kết ca: kiểm két theo giờ, bàn giao 2 bước, khóa ca, chốt và khóa dữ liệu ngày; Lịch làm việc lưu CSDL theo số ca đã chốt, đăng ký nghỉ phép đi qua Phê duyệt.
2. Nhàng – Phê duyệt lưu CSDL, duyệt chạy hành động trong transaction; chế độ ngoại tuyến: hàng đợi giao dịch cục bộ và đồng bộ bù, chống trùng bằng idempotency_key.
3. Đạt – Thống kê: truy vấn thật cho 7 màn (doanh thu theo ngày, ca, sản phẩm, dịch vụ, phương thức; top bán chạy; lợi nhuận); xuất .xlsx (Apache POI) và PDF.

**Commit mẫu**

- `feat(shift): two-step handover with hourly cash check`
- `feat(shift): lock shift and day data after confirmation`
- `feat(approval): persist requests and run approved actions in a transaction`
- `feat(schedule): persist weekly schedule and leave through approval`
- `feat(report): query revenue by day shift product service and payment method`
- `feat(export): export reports to xlsx and pdf`
- `feat(offline): queue transactions locally and sync with idempotency key`

**Tiêu chí hoàn thành**

- Chốt ngày xong thì không sửa được giao dịch của ngày đó.
- Báo cáo xuất được .xlsx và PDF mở được bằng Excel và trình đọc PDF.
- Ngắt mạng, bán 3 hóa đơn, nối lại: đồng bộ đủ, không trùng.

## Giai đoạn 7: Tích hợp, kiểm thử, UAT, đóng gói, báo cáo và bảo vệ

- **Thời gian:** 18/10 – 27/10/2026
- **Phụ trách:** Cả nhóm; Vinh dẫn kiểm thử; Nhàng và Vỉ đóng gói; Vy viết User Manual; Đạt làm slide và video
- **Nhánh:** `feature/integration-fixes, release/v1.0, docs/final-report`

**Công việc**

1. 18–19/10: chạy Regression toàn bộ test; phân loại lỗi Critical, Major, Minor; sửa Critical và Major.
2. 19–21/10: UAT tại Pet Station theo 10 luồng chính; cập nhật Checklist (File 05).
3. 21–23/10: đo hiệu năng (POS ≤ 2 giây), viết script sao lưu hằng ngày, build jar chạy được và bộ cài (jpackage), User Manual (File 07), Startup (File 08).
4. 22–25/10: cập nhật báo cáo cuối (File 09) theo kết quả thật, slide 13–18 trang và video demo.
5. 25/10: mở Pull Request release/v1.0 vào main, gắn tag v1.0.0, đóng băng mã nguồn.
6. 26–27/10: tổng duyệt và tập báo cáo trước hội đồng.

**Commit mẫu**

- `fix: resolve critical defects found in regression testing`
- `test: add uat scenarios and update checklist results`
- `build: package runnable jar and installer`
- `docs: add user manual and startup guide`
- `docs: update final report with test results`
- `chore: release v1.0.0`

**Tiêu chí hoàn thành**

- Không còn lỗi Critical/Major mở; test tự động và checklist đạt.
- Bộ cài chạy được trên máy sạch với script CSDL đính kèm.
- Báo cáo, slide và video nộp đúng hạn.

## Lịch tổng

| Thời gian | Công việc | Người thực hiện |
|---|---|---|
| 10/10 – 11/10 | GĐ0: repo, quy trình, họp chốt 8 quyết định, sửa tài liệu | Nhàng, cả nhóm |
| 12/10 – 13/10 | GĐ1: schema v2, n-Tier, Repository | Vỉ, Nhàng |
| 13/10 – 15/10 | GĐ2: Danh mục, tài khoản, nhật ký nối CSDL | Vỉ, Nhàng, Đạt, Vy |
| 14/10 – 17/10 | GĐ3 POS/thanh toán/đổi trả; GĐ4 kho | Đạt, Vy, Nhàng, Vỉ |
| 14/10 – 18/10 | GĐ5 Spa và Hotel | Vy, Vinh |
| 16/10 – 19/10 | GĐ6 ca, phê duyệt, lịch, thống kê, xuất, ngoại tuyến | Vinh, Nhàng, Đạt |
| 18/10 – 21/10 | GĐ7: Regression, sửa lỗi, UAT | Cả nhóm, Vinh dẫn |
| 21/10 – 25/10 | GĐ7: hiệu năng, đóng gói, User Manual, báo cáo, slide, video; 25/10 phát hành v1.0.0 | Cả nhóm |
| 26/10 – 27/10 | Tổng duyệt và báo cáo trước hội đồng | Cả nhóm |

## Rủi ro

| Rủi ro | Cách xử lý |
|---|---|
| Chỉ còn 17 ngày, nhiều việc song song. | Mỗi người tối đa 3 nhánh; ưu tiên luồng POS, kho, Spa, Hotel trước; chức năng ngoại tuyến cắt giảm nếu trễ mốc 19/10. |
| Xung đột khi gộp mã ở DangKyManHinh, QuanLyDanhMucPanel và các panel danh mục. | Mỗi người chỉ sửa tệp của phân hệ mình; rebase vào develop mỗi ngày; PR nhỏ dưới 400 dòng; họp đứng 15 phút mỗi sáng. |
| Chưa thống nhất quy định (điểm, số ca, thanh toán). | Chốt 8 quyết định ở Giai đoạn 0 và ghi vào docs/DECISIONS.md; không bắt đầu code phần liên quan khi chưa chốt. |
| Không có SQL Server hoặc thiết bị phần cứng. | Dùng SQL Server Express hoặc Docker và database kiểm thử riêng; thiết bị quét/in được kiểm tra bằng bàn phím và PDF. |
| UAT tại cửa hàng không sắp xếp được. | Chuẩn bị kịch bản UAT và video demo để nhóm tự đóng vai; xin lịch sớm từ Giai đoạn 5. |
| Tài liệu báo cáo lệch với sản phẩm cuối. | Mỗi PR chức năng kèm cập nhật tài liệu liên quan (docs:), báo cáo cuối chỉ lấy số liệu từ kết quả test thật. |
