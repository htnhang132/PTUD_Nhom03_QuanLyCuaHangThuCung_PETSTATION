package com.petstation.dao;

import com.petstation.model.LoaiNghiepVu;

import java.util.List;

public interface NghiepVuDAO {
    record ThongTinBang(String tieuDe, String[] cacCot) {}

    ThongTinBang layThongTin(LoaiNghiepVu cheDo);

    List<Object[]> layCacDong(LoaiNghiepVu cheDo, long idNguoiThucHien);
}
