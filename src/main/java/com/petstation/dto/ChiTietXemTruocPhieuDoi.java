package com.petstation.dto;

import com.petstation.model.banhang.ChiTietDoiTra;
import com.petstation.model.banhang.ChiTietHoaDon;
import com.petstation.model.banhang.PhieuDoiTra;

import java.util.List;

public final class ChiTietXemTruocPhieuDoi {
    private final PhieuDoiTra phieu;
    private final List<ChiTietDoiTra> hangTra;
    private final List<ChiTietHoaDon> hangThayThe;

    public ChiTietXemTruocPhieuDoi(
            PhieuDoiTra phieu, List<ChiTietDoiTra> hangTra, List<ChiTietHoaDon> hangThayThe) {
        this.phieu = phieu;
        this.hangTra = hangTra == null ? List.of() : List.copyOf(hangTra);
        this.hangThayThe = hangThayThe == null ? List.of() : List.copyOf(hangThayThe);
    }

    public PhieuDoiTra getPhieu() {
        return phieu;
    }

    public List<ChiTietDoiTra> getHangTra() {
        return hangTra;
    }

    public List<ChiTietHoaDon> getHangThayThe() {
        return hangThayThe;
    }
}
