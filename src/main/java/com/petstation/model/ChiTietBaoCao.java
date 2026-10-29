package com.petstation.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Dữ liệu chứng từ và cột dùng để đối chiếu nhóm trên biểu đồ với bảng chi tiết. */
public class ChiTietBaoCao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String[] cot;
    private List<Object[]> dong;
    private int cotNhom;

    public ChiTietBaoCao() {}

    public ChiTietBaoCao(String[] cot, List<Object[]> dong, int cotNhom) {
        this.cot = cot;
        this.dong = dong;
        this.cotNhom = cotNhom;
    }

    public String[] getCot() { return this.cot; }
    public String[] layCot() { return this.cot; }
    public String[] cot() { return this.cot; }
    public void setCot(String[] cot) { this.cot = cot; }
    public void datCot(String[] cot) { this.cot = cot; }

    public List<Object[]> getDong() { return this.dong; }
    public List<Object[]> layDong() { return this.dong; }
    public List<Object[]> dong() { return this.dong; }
    public void setDong(List<Object[]> dong) { this.dong = dong; }
    public void datDong(List<Object[]> dong) { this.dong = dong; }

    public int getCotNhom() { return this.cotNhom; }
    public int layCotNhom() { return this.cotNhom; }
    public int cotNhom() { return this.cotNhom; }
    public void setCotNhom(int cotNhom) { this.cotNhom = cotNhom; }
    public void datCotNhom(int cotNhom) { this.cotNhom = cotNhom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietBaoCao that = (ChiTietBaoCao) o;
        return Arrays.equals(cot, that.cot)
                && Objects.equals(dong, that.dong)
                && this.cotNhom == that.cotNhom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(cot), dong, cotNhom);
    }

    @Override
    public String toString() {
        return "ChiTietBaoCao{" + "cot=" + Arrays.toString(cot) + ", " + "dong=" + dong + ", " + "cotNhom=" + cotNhom + "}";
    }
}
