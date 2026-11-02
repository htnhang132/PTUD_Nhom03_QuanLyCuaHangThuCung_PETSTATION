package com.petstation.service;

import com.petstation.dao.QuyenVaiTroDAO;
import com.petstation.dao.impl.QuyenVaiTroDAOImpl;
import com.petstation.model.Quyen;
import com.petstation.model.VaiTro;

import java.util.EnumSet;
import java.util.Set;

/**
 * Điều phối nghiệp vụ và kiểm tra dữ liệu cho quyền dịch vụ.
 *
 * <p>Quyền theo vai trò (khớp database/002_sample_data.sql) để hiển thị ma trận phân quyền.
 */
public final class QuyenVaiTroService {
    private static final QuyenVaiTroDAO quyenVaiTroDAO = new QuyenVaiTroDAOImpl();

    private QuyenVaiTroService() {}

    public static Set<Quyen> tao(VaiTro vaiTro) {
        // Các quyền.
        Set<Quyen> cacQuyen = EnumSet.noneOf(Quyen.class);
        for (Object[] dong : quyenVaiTroDAO.timCua(vaiTro.name())) {
            if (dong != null && dong.length > 0 && dong[0] != null) {
                try {
                    Quyen q = Quyen.docTuCSDL(String.valueOf(dong[0]));
                    if (q != null) {
                        cacQuyen.add(q);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return cacQuyen;
    }

    public static String layNhan(Quyen vungDangXet) {
        return switch (vungDangXet) {
            case XEM_TONG_QUAN -> "Xem màn hình chính";
            case QUAN_LY_TAI_KHOAN -> "Tài khoản, nhân viên, cài đặt";
            case QUAN_LY_LICH_LAM_VIEC -> "Phân lịch nhân viên";
            case XEM_LICH_LAM_VIEC -> "Xem lịch làm việc";
            case QUAN_LY_KHACH_HANG -> "Khách hàng";
            case QUAN_LY_THU_CUNG -> "Hồ sơ thú cưng";
            case QUAN_LY_SAN_PHAM -> "Sản phẩm";
            case QUAN_LY_NHA_CUNG_CAP -> "Nhà cung cấp";
            case NHAP_KHO -> "Lập phiếu nhập kho";
            case XUAT_HUY_KHO -> "Xuất hủy hàng hóa";
            case KIEM_KE_KHO -> "Kiểm kê";
            case DE_XUAT_GIA -> "Đề xuất giá";
            case DUYET_GIA -> "Duyệt giá";
            case BAN_HANG -> "Lập hóa đơn, đổi hàng";
            case THANH_TOAN -> "Thanh toán";
            case QUAN_LY_CA_THU_NGAN -> "Mở ca / Kết ca";
            case QUAN_LY_KHUYEN_MAI -> "Khuyến mãi, tích điểm, voucher";
            case QUAN_LY_SPA -> "Dịch vụ Spa";
            case QUAN_LY_LUU_TRU -> "Pet Hotel";
            case XU_LY_PHE_DUYET -> "Phê duyệt yêu cầu";
            case XEM_NHAT_KY -> "Nhật ký hệ thống";
            case XEM_BAO_CAO_CHUNG -> "Báo cáo doanh thu chung";
            case XEM_BAO_CAO_CA -> "Báo cáo ca làm việc";
        };
    }
}
