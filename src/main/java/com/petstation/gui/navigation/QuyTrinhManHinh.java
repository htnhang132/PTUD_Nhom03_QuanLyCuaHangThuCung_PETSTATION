package com.petstation.gui.navigation;

import com.petstation.model.MaManHinh;

import java.util.List;
import java.util.Optional;

public final class QuyTrinhManHinh {
    public record QuyTrinh(
            String ten, List<MaManHinh> cacBuoc, List<String> cacNhan, boolean tuanTu) {
        public int layChiSoCua(MaManHinh id) {
            if (id == MaManHinh.QUAN_LY_SPA) id = MaManHinh.TIEP_NHAN_SPA;
            return cacBuoc.indexOf(id);
        }

        public Optional<MaManHinh> layTiepTheo(MaManHinh id) {
            int chiSo = layChiSoCua(id);
            return chiSo >= 0 && chiSo + 1 < cacBuoc.size()
                    ? Optional.of(cacBuoc.get(chiSo + 1))
                    : Optional.empty();
        }

        public Optional<MaManHinh> layTruocDo(MaManHinh id) {
            int chiSo = layChiSoCua(id);
            return chiSo > 0 ? Optional.of(cacBuoc.get(chiSo - 1)) : Optional.empty();
        }

        public boolean kiemTraCuoiCung(MaManHinh id) {
            return layChiSoCua(id) == cacBuoc.size() - 1;
        }
    }

    private static final List<QuyTrinh> CAC_QUY_TRINH =
            List.of(
                    new QuyTrinh(
                            "Bán hàng",
                            List.of(MaManHinh.LAP_HOA_DON, MaManHinh.THANH_TOAN, MaManHinh.IN_HOA_DON),
                            List.of("Lập hóa đơn", "Thanh toán", "Xem hóa đơn"),
                            false),
                    new QuyTrinh(
                            "Quản lý lưu trú",
                            List.of(
                                    MaManHinh.DAT_PHONG_LUU_TRU,
                                    MaManHinh.NHAT_KY_CHAM_SOC,
                                    MaManHinh.TRA_PHONG_LUU_TRU),
                            List.of("Đặt phòng", "Nhật ký lưu trú", "Trả phòng"),
                            true));

    private QuyTrinhManHinh() {}

    public static Optional<QuyTrinh> tim(MaManHinh id) {
        return CAC_QUY_TRINH.stream()
                .filter(truongDangXet -> truongDangXet.layChiSoCua(id) >= 0)
                .findFirst();
    }
}
