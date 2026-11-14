package com.petstation.dao.nhansu;

import com.petstation.model.nhansu.LichLamViec;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LichLamViecDAO {
    List<LichLamViec> timKhoang(LocalDate tu, LocalDate den);

    Map<String, Integer> layGioTheoNhanVienKhoang(LocalDate tu, LocalDate den);

    long demPhanCong(String maNhanVien);

    long demNhanVienDangLam(String maNhanVien);

    long demLichTrungNgayCa(String maNhanVien, LocalDate ngay, String maCa);

    int capNhatPhanCong(String maNhanVien, LocalDate ngay, String maCa);

    int themLichLamViec(String maNhanVien, LocalDate ngay, String maCa);

    int capNhatXoa(String maNhanVien, LocalDate ngay, String maCa);

    int capNhatDanhDauNghiPhepDaDuyet(String maNhanVien, LocalDate ngay);

    int themLichNghiPhep(String maNhanVien, LocalDate ngay);

    long demThangSoGio(String maNhanVien, LocalDate tu, LocalDate den);

    int khoaLichKhoang(LocalDate tu, LocalDate den);
}
