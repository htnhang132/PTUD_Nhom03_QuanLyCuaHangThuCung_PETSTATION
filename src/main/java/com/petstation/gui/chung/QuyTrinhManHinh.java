package com.petstation.gui.chung;

import com.petstation.model.chung.MaManHinh;

import java.util.List;
import java.util.Optional;

public final class QuyTrinhManHinh {
    public static final class QuyTrinh {
        private final String ten;
        private final List<MaManHinh> cacBuoc;
        private final List<String> cacNhan;
        private final boolean tuanTu;

        public QuyTrinh(String ten, List<MaManHinh> cacBuoc, List<String> cacNhan, boolean tuanTu) {
            this.ten = ten;
            this.cacBuoc = cacBuoc;
            this.cacNhan = cacNhan;
            this.tuanTu = tuanTu;
        }

        public String ten() {
            return ten;
        }

        public List<MaManHinh> cacBuoc() {
            return cacBuoc;
        }

        public List<String> cacNhan() {
            return cacNhan;
        }

        public boolean tuanTu() {
            return tuanTu;
        }

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
                            List.of(
                                    MaManHinh.LAP_HOA_DON,
                                    MaManHinh.THANH_TOAN,
                                    MaManHinh.IN_HOA_DON),
                            List.of("Lập hóa đơn", "Thanh toán", "Xem hóa đơn"),
                            false));

    private QuyTrinhManHinh() {}

    public static Optional<QuyTrinh> tim(MaManHinh id) {
        return CAC_QUY_TRINH.stream()
                .filter(truongDangXet -> truongDangXet.layChiSoCua(id) >= 0)
                .findFirst();
    }
}
