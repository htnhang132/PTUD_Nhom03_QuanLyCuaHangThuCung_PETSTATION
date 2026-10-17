CREATE INDEX IX_tai_khoan_username_status ON tai_khoan(ten_dang_nhap, trang_thai);
CREATE INDEX IX_lich_ngay_ca ON lich_lam_viec(ngay_lam, ca_lam_viec_id);
CREATE INDEX IX_khach_hang_phone ON khach_hang(so_dien_thoai);
CREATE INDEX IX_san_pham_barcode ON san_pham(barcode) WHERE barcode IS NOT NULL;
CREATE INDEX IX_lo_hang_fefo ON lo_hang(san_pham_id, han_su_dung, trang_thai) INCLUDE (so_luong_ton);
CREATE INDEX IX_don_hang_phien_trang_thai ON don_hang(phien_lam_viec_id, trang_thai);
CREATE INDEX IX_hoa_don_thoi_gian ON hoa_don(thanh_toan_luc DESC);
CREATE INDEX IX_lich_hen_thoi_gian ON lich_hen(bat_dau, ket_thuc, trang_thai);
CREATE INDEX IX_hotel_thoi_gian ON hop_dong_luu_tru(phong_id, ngay_vao, ngay_ra_du_kien, trang_thai);
CREATE INDEX IX_approval_status ON yeu_cau_phe_duyet(trang_thai, gui_luc);
CREATE INDEX IX_audit_time ON nhat_ky_he_thong(thoi_gian DESC);
GO
