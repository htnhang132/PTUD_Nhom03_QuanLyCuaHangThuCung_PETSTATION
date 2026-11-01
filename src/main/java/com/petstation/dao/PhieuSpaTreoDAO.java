package com.petstation.dao;

import com.petstation.model.PhieuSpaTreo;
import java.util.List;

public interface PhieuSpaTreoDAO {
    long them(String ma, long nguoiLap, PhieuSpaTreo phieu);
    void capNhat(long id, PhieuSpaTreo phieu);
    PhieuSpaTreo lay(long id);
    List<Object[]> danhSach();
    void xoa(long id);
}
