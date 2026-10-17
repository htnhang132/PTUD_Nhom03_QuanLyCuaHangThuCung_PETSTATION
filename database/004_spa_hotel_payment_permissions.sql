-- Nhân viên Spa/Hotel thu tiền tại quầy dịch vụ: bổ sung quyền lập hóa đơn, thanh toán, mở/kết ca.
-- Chạy cho cơ sở dữ liệu đã seed trước đó (003 đã gồm các dòng này cho cơ sở dữ liệu mới).
INSERT INTO vai_tro_quyen(vai_tro, ma_quyen)
SELECT v.vai_tro, v.ma_quyen FROM (VALUES
 ('SPA_HOTEL_STAFF','POS_SELL'),('SPA_HOTEL_STAFF','PAYMENT_PROCESS'),('SPA_HOTEL_STAFF','CASH_SHIFT_MANAGE')
) AS v(vai_tro, ma_quyen)
WHERE NOT EXISTS (SELECT 1 FROM vai_tro_quyen x WHERE x.vai_tro = v.vai_tro AND x.ma_quyen = v.ma_quyen);
GO
