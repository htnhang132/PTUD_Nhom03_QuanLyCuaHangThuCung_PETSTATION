package com.petstation.gui.chung;

import com.petstation.model.chung.MaManHinh;
import com.petstation.model.chung.Quyen;

public final class DinhNghiaManHinh {
    private final MaManHinh id;
    private final String tieuDe;
    private final String goiY;
    private final Quyen quyen;
    private final boolean daTrienKhai;

    public DinhNghiaManHinh(
            MaManHinh id, String tieuDe, String goiY, Quyen quyen, boolean daTrienKhai) {
        this.id = id;
        this.tieuDe = tieuDe;
        this.goiY = goiY;
        this.quyen = quyen;
        this.daTrienKhai = daTrienKhai;
    }

    public MaManHinh id() {
        return id;
    }

    public String tieuDe() {
        return tieuDe;
    }

    public String goiY() {
        return goiY;
    }

    public Quyen quyen() {
        return quyen;
    }

    public boolean daTrienKhai() {
        return daTrienKhai;
    }
}
