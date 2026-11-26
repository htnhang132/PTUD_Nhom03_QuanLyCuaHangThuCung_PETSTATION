package com.petstation.gui.chung.trogiup;

import com.petstation.gui.chung.HopThoaiTroGiup.NoiDungHuongDan;
import com.petstation.gui.nhansu.LichTuanPanel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Nội dung đã đối chiếu theo từng màn hình; không suy đoán chức năng từ tên phân hệ. */
public final class SoTayTroGiup {
    private static final Map<String, NoiDungHuongDan> CAC_MAN_HINH = taoDanhSach();

    private SoTayTroGiup() {}

    private static Map<String, NoiDungHuongDan> taoDanhSach() {
        Map<String, NoiDungHuongDan> ds = new LinkedHashMap<>();
        HuongDanHeThong.dangKy(ds);
        HuongDanDanhMuc.dangKy(ds);
        HuongDanBanHang.dangKy(ds);
        HuongDanVanHanh.dangKy(ds);
        HuongDanBaoCao.dangKy(ds);
        HuongDanBieuMau.dangKy(ds);
        return Map.copyOf(ds);
    }

    public static Set<String> layCacLopDaCoHuongDan() {
        return CAC_MAN_HINH.keySet();
    }

    static NoiDungHuongDan layDaDangKy(String lop) {
        return CAC_MAN_HINH.get(lop);
    }

    public static NoiDungHuongDan layTheoLop(Class<?> lop) {
        NoiDungHuongDan hd = CAC_MAN_HINH.get(lop.getSimpleName());
        if (hd == null)
            throw new IllegalArgumentException("Chưa đăng ký trợ giúp: " + lop.getName());
        return hd;
    }

    public static NoiDungHuongDan lay(Component manHinh, String tieuDe, String noiDung) {
        if (manHinh instanceof LichTuanPanel lich) return HuongDanVanHanh.lich(lich.layCheDo());
        NoiDungHuongDan hd =
                manHinh == null ? null : CAC_MAN_HINH.get(manHinh.getClass().getSimpleName());
        if (hd != null) return hd;
        return tao(
                tieuDe == null || tieuDe.isBlank() || "Trợ giúp".equals(tieuDe)
                        ? "Hướng dẫn thao tác"
                        : tieuDe,
                "Hướng dẫn cho màn hình đang mở.",
                List.of(
                        noiDung == null || noiDung.isBlank()
                                ? "Chọn chức năng đang được bật trên màn hình để thao tác."
                                : noiDung),
                List.of(
                        "Các thao tác khả dụng phụ thuộc quyền của tài khoản và dữ liệu đang"
                            + " chọn."));
    }

    static NoiDungHuongDan tao(
            String ten, String mucDich, List<String> buoc, List<String> luuY, String[]... phim) {
        List<String[]> phimTat = new ArrayList<>(List.of(phim));
        phimTat.add(new String[] {"Alt + F1", "Mở trợ giúp của màn hình hoặc tab đang chọn"});
        phimTat.add(new String[] {"Esc", "Đóng cửa sổ trợ giúp"});
        return new NoiDungHuongDan(
                ten, mucDich, List.copyOf(buoc), List.copyOf(luuY), List.copyOf(phimTat));
    }
}
