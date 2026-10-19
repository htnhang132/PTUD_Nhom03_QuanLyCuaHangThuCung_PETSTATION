# Templates for Remaining JDBC DAO Implementations

## Still Needed (9 implementations):

1. ✅ JdbcBatchDao
2. ✅ JdbcSupplierDao  
3. ❌ JdbcInvoiceDao (PRIORITY - sales tracking)
4. ❌ JdbcVoucherDao (PRIORITY - loyalty program)
5. ❌ JdbcSpaTicketDao (PRIORITY - spa services)
6. ❌ JdbcHotelBookingDao (PRIORITY - hotel operations)
7. ❌ JdbcHotelPricingDao
8. ❌ JdbcPriceHistoryDao
9. ❌ JdbcDayLockDao

---

## General Pattern

```java
package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.XxxDao;
import com.petstation.model.Xxx;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcXxxDao implements XxxDao {
    
    private final Connection connection;
    
    public JdbcXxxDao(Connection connection) {
        this.connection = connection;
    }
    
    // Implement all interface methods...
    
    private Xxx mapResultSetToXxx(ResultSet rs) throws SQLException {
        // Map columns to model object
    }
}
```

---

## 1. JdbcInvoiceDao Template

**Table:** `hoa_don`  
**Priority:** HIGH (Sales tracking)

**Key Methods:**
- `create(Invoice)` - Insert invoice
- `findById(Long)` - Get invoice details
- `findByCustomerId(Long)` - Customer purchase history
- `findByEmployeeId(Long)` - Employee sales
- `findByShiftSessionId(Long)` - Shift reconciliation
- `findByDateRange(start, end)` - Sales reports
- `getTotalRevenueByDateRange(start, end)` - Revenue calculation
- `updateStatus(Long, String)` - Complete/Cancel

**Column Mapping:**
```
id -> invoiceId
ma_hoa_don -> invoiceCode
khach_hang_id -> customerId
nhan_vien_id -> employeeId
phien_lam_viec_id -> shiftSessionId
ngay_hoa_don -> invoiceDate
tong_truoc_giam -> subtotal
tien_giam_gia -> discountAmount
tien_diem -> loyaltyPointsAmount
tien_thue -> taxAmount
tong_tien -> totalAmount
tien_khach_dua -> customerPaid
tien_thua -> changeAmount (computed)
phuong_thuc_thanh_toan -> paymentMethod
loai_hoa_don -> invoiceType
trang_thai -> status
ghi_chu -> notes
ngay_tao -> createdAt
row_version -> rowVersion
```

---

## 2. JdbcVoucherDao Template

**Table:** `voucher`  
**Priority:** HIGH (Loyalty program)

**Key Methods:**
- `create(Voucher)` - Generate voucher
- `findByVoucherCode(String)` - Validate voucher
- `findByCustomerId(Long)` - Customer vouchers
- `findActiveVouchers()` - Available vouchers
- `incrementUsedCount(Long)` - Apply voucher
- `updateStatus(Long, String)` - Activate/Expire

**Column Mapping:**
```
id -> voucherId
ma_voucher -> voucherCode
khach_hang_id -> customerId (nullable)
loai_voucher -> voucherType (CAMPAIGN, LOYALTY_REWARD)
gia_tri_giam -> discountValue
loai_giam -> discountType (PERCENTAGE, FIXED_AMOUNT)
don_hang_toi_thieu -> minOrderValue
giam_toi_da -> maxDiscountAmount
hieu_luc_tu -> validFrom
hieu_luc_den -> validUntil
gioi_han_su_dung -> usageLimit
da_su_dung -> usedCount
trang_thai -> status
chi_ap_dung_spa -> applySpaOnly
ngay_doi_diem -> redeemedDate
ngay_tao -> createdAt
```

---

## 3. JdbcSpaTicketDao Template

**Table:** `phieu_tiep_nhan_spa`  
**Priority:** HIGH (Spa operations)

**Key Methods:**
- `create(SpaTicket)` - Check-in pet
- `findById(Long)` - Ticket details
- `findByPetId(Long)` - Pet service history
- `findByStatus(String)` - Queue management
- `findPendingTickets()` - Waiting list
- `findInProgressTickets()` - Active services
- `updateCheckOutTime(Long, LocalDateTime)` - Check-out
- `updateStatus(Long, String)` - Status tracking

**Column Mapping:**
```
phieu_id -> ticketId
ma_phieu -> ticketCode
thu_cung_id -> petId
khach_hang_id -> customerId
goi_id -> packageId
nhan_vien_id -> employeeId
gio_nhan -> checkInTime
gio_tra -> checkOutTime
gia_goi -> basePrice
phi_phu_thu -> surchargeAmount
tong_tien -> totalAmount
trang_thai -> status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
ghi_chu -> notes
ngay_tao -> createdAt
```

---

## 4. JdbcHotelBookingDao Template

**Table:** `dat_phong_hotel`  
**Priority:** HIGH (Hotel operations)

**Key Methods:**
- `create(HotelBooking)` - Reserve room
- `findById(Long)` - Booking details
- `findByPetId(Long)` - Pet stay history
- `findByRoomId(Long)` - Room bookings
- `findActiveBookings()` - Currently occupied
- `findOverdueBookings()` - Late check-outs
- `isRoomAvailable(roomId, start, end)` - Availability check
- `updateActualCheckOutTime(Long, LocalDateTime)` - Check-out

**Column Mapping:**
```
id -> bookingId
ma_dat_phong -> bookingCode
thu_cung_id -> petId
khach_hang_id -> customerId
phong_id -> roomId
gio_nhan_phong -> checkInTime
gio_tra_du_kien -> expectedCheckOutTime
gio_tra_thuc_te -> actualCheckOutTime
khung_gio -> timeFrame (HOURLY, DAILY, OVERNIGHT)
gia_phong -> basePrice
tien_coc -> depositAmount
phi_tre_han -> lateFee
tong_tien -> totalAmount
trang_thai -> status (RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED, OVERDUE)
ghi_chu -> notes
nhan_vien_id -> employeeId
ngay_tao -> createdAt
```

---

## 5. JdbcHotelPricingDao Template

**Table:** `bang_gia_hotel`

**Key Methods:**
- `findByRoomTimeFrameAndWeight(roomId, timeFrame, weight)` - Price lookup
- `findByRoomId(Long)` - All room prices
- `updatePrice(Long, BigDecimal)` - Update price

**Column Mapping:**
```
gia_id -> pricingId
phong_id -> roomId
khung_gio -> timeFrame
can_nang_tu -> weightFrom
can_nang_den -> weightTo
don_gia -> price
ngay_ap_dung -> effectiveDate
ngay_het_hieu_luc -> expiryDate
```

---

## 6. JdbcBatchDao Template

**Table:** `lo_hang`

**Key Methods:**
- `findByProductId(Long)` - Product batches
- `findExpiringBefore(LocalDate)` - Expiry warnings
- `findExpiredBatches()` - Expired inventory
- `findLowStockBatches(threshold)` - Reorder alerts
- `updateQuantity(Long, Integer)` - Stock adjustment

**Column Mapping:**
```
id -> batchId
ma_lo -> batchCode
san_pham_id -> productId
ngay_san_xuat -> manufacturingDate
han_su_dung -> expiryDate
so_luong -> quantity
gia_nhap -> costPrice
nha_cung_cap_id -> supplierId
trang_thai -> status
ngay_nhap -> createdAt
```

---

## 7. JdbcSupplierDao Template

**Table:** `nha_cung_cap`

**Key Methods:**
- `findBySupplierCode(String)`
- `searchByName(String)`
- `findAllActive()`
- `updateActiveStatus(Long, Boolean)`

**Column Mapping:**
```
id -> supplierId
ma_ncc -> supplierCode
ten_ncc -> supplierName
nguoi_lien_he -> contactPerson
so_dien_thoai -> phoneNumber
email -> email
dia_chi -> address
ma_so_thue -> taxCode
hoat_dong -> isActive
ghi_chu -> notes
ngay_tao -> createdAt
```

---

## 8. JdbcPriceHistoryDao Template

**Table:** `lich_su_gia`

**Key Methods:**
- `findByProductId(Long)` - Product price history
- `findLatestByProductId(Long)` - Last price change
- `findByChangeType(String)` - Filter by reason

**Column Mapping:**
```
lich_su_id -> historyId
san_pham_id -> productId
gia_cu -> oldPrice
gia_moi -> newPrice
ly_do -> reason
nguoi_thay_doi_id -> changedBy
thoi_diem_thay_doi -> changedAt
loai_thay_doi -> changeType
```

---

## 9. JdbcDayLockDao Template

**Table:** `chot_ngay`

**Key Methods:**
- `findByLockDate(LocalDate)` - Get day lock
- `findLockedDays()` - All locked days
- `isDayLocked(LocalDate)` - Check if locked
- `updateStatus(Long, String)` - Reopen day

**Column Mapping:**
```
chot_id -> lockId
ngay_chot -> lockDate
nguoi_chot_id -> lockedBy
thoi_diem_chot -> lockedAt
tong_doanh_thu -> totalRevenue
tong_tien_mat -> totalCash
tong_chuyen_khoan -> totalBankTransfer
tong_the -> totalCard
so_hoa_don -> invoiceCount
ghi_chu -> notes
trang_thai -> status (LOCKED, REOPENED)
```

---

## SQL Note: Table Name Mapping

Vietnamese → Java:
- `khach_hang` → Customer
- `thu_cung` → Pet
- `san_pham` → Product
- `lo_hang` → Batch
- `nha_cung_cap` → Supplier
- `khuyen_mai` → Promotion
- `voucher` → Voucher
- `hoa_don` → Invoice
- `chi_tiet_hoa_don` → InvoiceItem
- `goi_dich_vu_spa` → SpaPackage
- `bang_gia_spa` → SpaPriceMatrix
- `phieu_tiep_nhan_spa` → SpaTicket
- `chi_tiet_phieu_spa` → SpaTicketDetail
- `phong_hotel` → HotelRoom
- `dat_phong_hotel` → HotelBooking
- `bang_gia_hotel` → HotelPricing
- `cai_dat` → SystemSettings
- `lich_su_gia` → PriceHistory
- `chot_ngay` → DayLock

---

## Testing Checklist

After implementing each DAO:
- [ ] Test create() with valid data
- [ ] Test findById() returns correct object
- [ ] Test findAll() returns list
- [ ] Test update() modifies record
- [ ] Test delete() removes record
- [ ] Test custom queries (search, filter, etc.)
- [ ] Test error handling (SQLException)
- [ ] Verify SQL injection protection (PreparedStatement)

---

**Estimate:** ~30 minutes per DAO implementation  
**Total remaining:** ~4.5 hours for 9 DAOs

**Priority order:**
1. JdbcInvoiceDao (sales critical)
2. JdbcVoucherDao (loyalty program)
3. JdbcSpaTicketDao (spa operations)
4. JdbcHotelBookingDao (hotel operations)
5. Others (reporting, auditing)
