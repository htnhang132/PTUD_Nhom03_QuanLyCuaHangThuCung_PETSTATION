package com.petstation.model.chung;

/** Giao diện dùng chung cho các enum ánh xạ mã cột cơ sở dữ liệu và nhãn hiển thị. */
public interface MaDBEnum {
    String maDB();

    String tenHienThi();

    static <E extends Enum<E> & MaDBEnum> E tuMaDB(Class<E> loai, String ma) {
        if (ma == null || ma.isBlank()) return null;
        String chuan = ma.trim();
        for (E muc : loai.getEnumConstants()) {
            if (muc.maDB().equalsIgnoreCase(chuan)) {
                return muc;
            }
        }
        throw new IllegalArgumentException(
                "Không tìm thấy " + loai.getSimpleName() + " với mã: " + ma);
    }
}
