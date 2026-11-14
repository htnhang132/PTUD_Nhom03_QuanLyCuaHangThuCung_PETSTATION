package com.petstation.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MucLuaChon that)) return false;
        return Objects.equals(giaTri, that.giaTri) && Objects.equals(nhan, that.nhan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(giaTri, nhan);
    }

    @Override
    public String toString() {
        return nhan == null ? "" : nhan;
    }
}
