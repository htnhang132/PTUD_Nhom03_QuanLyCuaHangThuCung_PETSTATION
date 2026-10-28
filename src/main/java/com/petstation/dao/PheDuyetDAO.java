package com.petstation.dao;

import java.util.List;

/** Thao tác dữ liệu cho PheDuyet; service gọi qua hợp đồng này. */
public interface PheDuyetDAO {
    long demSoLuongDangCho(Object... cacThamSo);

    int capNhatTuChoi(Object... cacThamSo);

    int tuChoiBienBanKiemKe(Object... cacThamSo);

    long demApDungPheDuyet(Object... cacThamSo);

    int capNhatApDungPheDuyet(Object... cacThamSo);

    int capNhatGuiThaoTac(Object... cacThamSo);

    List<Object[]> timTatCa(Object... cacThamSo);

    List<Object[]> timDuyet(Object... cacThamSo);

    int capNhatDuyet(Object... cacThamSo);

    List<Object[]> timTuChoi(Object... cacThamSo);

    int tuChoiPhieuDoiTra(Object... cacThamSo);

    int danhDauYeuCauTuChoi(Object... cacThamSo);
}
