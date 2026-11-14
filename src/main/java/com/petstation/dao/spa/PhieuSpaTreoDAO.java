package com.petstation.dao.spa;

import com.petstation.dto.SpaDTO;
import com.petstation.model.spa.PhieuSpaTreo;

import java.util.List;

public interface PhieuSpaTreoDAO {
    String them(String ma, String maNhanVien, PhieuSpaTreo phieu);

    void capNhat(String maPhieu, PhieuSpaTreo phieu);

    PhieuSpaTreo lay(String maPhieu);

    List<SpaDTO.DongPhieuSpaTreo> danhSach();

    void xoa(String maPhieu);
}
