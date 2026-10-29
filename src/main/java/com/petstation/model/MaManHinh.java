package com.petstation.model;

/** Các mã màn hình dùng chung cho menu, quyền truy cập và điều hướng. */
public enum MaManHinh {
    TONG_QUAN,
    // Hệ thống
    DOI_MAT_KHAU,
    TAI_KHOAN_PHAN_QUYEN,
    CAI_DAT,
    NHAT_KY_HE_THONG,
    // Danh mục (CRUD)
    DANH_MUC_KHACH_HANG,
    DANH_MUC_THU_CUNG,
    DANH_MUC_NHAN_VIEN,
    DANH_MUC_SAN_PHAM,
    DANH_MUC_DICH_VU_SPA,
    DANH_MUC_PHONG_LUU_TRU,
    DANH_MUC_NHA_CUNG_CAP,
    DANH_MUC_KHUYEN_MAI,
    TICH_DIEM_VOUCHER,
    // Tìm kiếm (chỉ xem)
    TIM_KIEM_HOA_DON,
    TRA_CUU_KHACH_HANG,
    TRA_CUU_LO_HANG,
    TRA_CUU_LICH_SPA,
    SO_DO_PHONG_LUU_TRU,
    TRA_CUU_LICH_LAM_VIEC,
    LICH_CA_NHAN,
    // Xử lý: bán hàng
    LAP_HOA_DON,
    THANH_TOAN,
    IN_HOA_DON,
    HOA_DON_CHO,
    DOI_TRA_HANG_HOA,
    // Xử lý: Spa
    QUAN_LY_SPA,
    DAT_LICH_HEN_SPA,
    TIEP_NHAN_SPA,
    CHON_GOI_SPA,
    CAP_NHAT_TIEN_DO_SPA,
    HOAN_TAT_SPA,
    // Xử lý: Pet Hotel
    QUAN_LY_LUU_TRU,
    DAT_PHONG_LUU_TRU,
    NHAT_KY_CHAM_SOC,
    TRA_PHONG_LUU_TRU,
    // Xử lý: kho, ca, lịch, phê duyệt
    NHAP_KHO,
    XUAT_HUY_KHO,
    KIEM_KE_TON_KHO,
    CA_THU_NGAN,
    PHAN_LICH_NHAN_VIEN,
    PHE_DUYET_YEU_CAU,
    // Thống kê
    THONG_KE_DOANH_THU,
    THONG_KE_HANG_HOA,
    THONG_KE_SPA,
    THONG_KE_LUU_TRU,
    THONG_KE_KHACH_HANG,
    THONG_KE_CONG_NO_NCC,
    THONG_KE_NHAN_VIEN
}
