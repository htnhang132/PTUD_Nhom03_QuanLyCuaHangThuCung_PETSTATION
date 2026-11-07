package com.petstation.model;

public record PhieuSpaTreo(String maKhach, String soDienThoai, String maThu, String maGoi,
                          String maHen, String canNang, String tinhTrang, String yeuCau) implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public PhieuSpaTreo {
        maKhach = rongNeuNull(maKhach);
        soDienThoai = rongNeuNull(soDienThoai);
        maThu = rongNeuNull(maThu);
        maGoi = rongNeuNull(maGoi);
        maHen = rongNeuNull(maHen);
        canNang = rongNeuNull(canNang);
        tinhTrang = rongNeuNull(tinhTrang);
        yeuCau = rongNeuNull(yeuCau);
    }

    private static String rongNeuNull(String s) { return s == null ? "" : s; }

    public boolean coNoiDung() {
        return !(maKhach.isBlank() && soDienThoai.isBlank() && maThu.isBlank()
            && maGoi.isBlank() && maHen.isBlank() && canNang.isBlank()
            && tinhTrang.isBlank() && yeuCau.isBlank());
    }
}
