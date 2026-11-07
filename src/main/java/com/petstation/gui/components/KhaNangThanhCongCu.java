package com.petstation.gui.components;

public record KhaNangThanhCongCu(
        boolean coTheTao,
        boolean coTheLuu,
        boolean coTheTim,
        boolean coTheIn,
        boolean coTheHoanTac,
        boolean coTheTroGiup) {
    public static KhaNangThanhCongCu taoRong() {
        return new KhaNangThanhCongCu(false, false, false, false, false, false);
    }

    public static KhaNangThanhCongCu layTroGiupChi() {
        return new KhaNangThanhCongCu(false, false, false, false, false, true);
    }
}
