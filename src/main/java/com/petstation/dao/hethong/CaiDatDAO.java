package com.petstation.dao.hethong;

import com.petstation.model.hethong.CaiDat;

import java.util.Optional;

public interface CaiDatDAO {
    String timGiaTri(String maCaiDat);

    Optional<CaiDat> timCaiDat(String maCaiDat);

    int themGiaTri(String maCaiDat, String giaTri, String maNhanVien);

    int capNhatGiaTri(String giaTri, String maNhanVien, String maCaiDat);
}
