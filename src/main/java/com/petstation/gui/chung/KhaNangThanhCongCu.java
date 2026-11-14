package com.petstation.gui.chung;

public final class KhaNangThanhCongCu {
    private final boolean coTheTao;
    private final boolean coTheLuu;
    private final boolean coTheTim;
    private final boolean coTheIn;
    private final boolean coTheHoanTac;
    private final boolean coTheTroGiup;

    public KhaNangThanhCongCu(
            boolean coTheTao,
            boolean coTheLuu,
            boolean coTheTim,
            boolean coTheIn,
            boolean coTheHoanTac,
            boolean coTheTroGiup) {
        this.coTheTao = coTheTao;
        this.coTheLuu = coTheLuu;
        this.coTheTim = coTheTim;
        this.coTheIn = coTheIn;
        this.coTheHoanTac = coTheHoanTac;
        this.coTheTroGiup = coTheTroGiup;
    }

    public boolean coTheTao() {
        return coTheTao;
    }

    public boolean coTheLuu() {
        return coTheLuu;
    }

    public boolean coTheTim() {
        return coTheTim;
    }

    public boolean coTheIn() {
        return coTheIn;
    }

    public boolean coTheHoanTac() {
        return coTheHoanTac;
    }

    public boolean coTheTroGiup() {
        return coTheTroGiup;
    }

    public static KhaNangThanhCongCu taoRong() {
        return new KhaNangThanhCongCu(false, false, false, false, false, false);
    }
}
