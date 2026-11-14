package com.petstation.service.chung;

import com.petstation.model.chung.MaManHinh;

public final class DieuHuongBanGhiService {
    private static final DieuHuongBanGhiService MAC_DINH = new DieuHuongBanGhiService();

    public static DieuHuongBanGhiService macDinh() {
        return MAC_DINH;
    }

    public DieuHuongBanGhiService() {}

    public static final class YeuCau {
        private final MaManHinh manHinh;
        private final String ma;

        public YeuCau(MaManHinh manHinh, String ma) {
            this.manHinh = manHinh;
            this.ma = ma;
        }

        public MaManHinh manHinh() {
            return manHinh;
        }

        public String ma() {
            return ma;
        }
    }

    private volatile YeuCau cho;

    public void dat(MaManHinh manHinh, String ma) {
        cho = new YeuCau(manHinh, ma);
    }

    public YeuCau lay() {
        return cho;
    }

    public void xoa() {
        cho = null;
    }
}
