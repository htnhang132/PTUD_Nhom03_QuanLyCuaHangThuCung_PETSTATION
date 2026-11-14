package com.petstation.dto;

public final class MucLuaChon {
    private final String giaTri;
    private final String nhan;

    public MucLuaChon(String giaTri, String nhan) {
        this.giaTri = giaTri;
        this.nhan = nhan;
    }

    public String giaTri() {
        return giaTri;
    }

    public String nhan() {
        return nhan;
    }
}
