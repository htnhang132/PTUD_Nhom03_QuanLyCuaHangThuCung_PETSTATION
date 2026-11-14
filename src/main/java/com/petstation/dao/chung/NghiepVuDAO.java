package com.petstation.dao.chung;

import com.petstation.dto.DongNghiepVu;
import com.petstation.model.chung.LoaiNghiepVu;

import java.util.List;

public interface NghiepVuDAO {
    final class ThongTinBang {
        private final String tieuDe;
        private final String[] cacCot;

        public ThongTinBang(String tieuDe, String[] cacCot) {
            this.tieuDe = tieuDe;
            this.cacCot = cacCot;
        }

        public String tieuDe() {
            return tieuDe;
        }

        public String[] cacCot() {
            return cacCot;
        }
    }

    ThongTinBang layThongTin(LoaiNghiepVu cheDo);

    List<DongNghiepVu> layCacDong(LoaiNghiepVu cheDo, String tenDangNhap);
}
