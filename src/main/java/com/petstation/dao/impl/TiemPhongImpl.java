package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;

import com.petstation.dao.TiemPhongDAO;

import java.time.LocalDate;
import java.util.List;

/** Hồ sơ tiêm phòng được lưu theo thú cưng, thay cho ghi chú tự do. */
public final class TiemPhongImpl implements TiemPhongDAO {
    public TiemPhongImpl() {}

    public List<Object[]> layThuCung(String ma) {
        return truyVan(
                "SELECT id,ngay_sinh FROM thu_cung WITH(UPDLOCK,HOLDLOCK) WHERE ma_thu_cung=? AND"
                        + " dang_theo_doi=1",
                ma);
    }

    public List<Object[]> layLichSu(long id) {
        return truyVan(
                "SELECT id,ten_vaccine,ngay_tiem,ngay_het_han,ghi_chu FROM tiem_phong WHERE"
                        + " thu_cung_id=? ORDER BY ngay_tiem DESC,id DESC",
                id);
    }

    public void luu(
            long id, long thuCung, String ten, LocalDate ngay, LocalDate han, String ghiChu) {
        if (id == 0)
            capNhat(
                    "INSERT tiem_phong(thu_cung_id,ten_vaccine,ngay_tiem,ngay_het_han,ghi_chu)"
                            + " VALUES(?,?,?,?,?)",
                    thuCung,
                    ten,
                    ngay,
                    han,
                    ghiChu);
        else if (capNhat(
                        "UPDATE tiem_phong SET ten_vaccine=?,ngay_tiem=?,ngay_het_han=?,ghi_chu=?"
                                + " WHERE id=? AND thu_cung_id=?",
                        ten,
                        ngay,
                        han,
                        ghiChu,
                        id,
                        thuCung)
                != 1) throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
    }

    public void xoa(long id, long thuCung) {
        if (capNhat("DELETE tiem_phong WHERE id=? AND thu_cung_id=?", id, thuCung) != 1)
            throw new IllegalArgumentException("Mũi tiêm không còn tồn tại.");
    }
}
