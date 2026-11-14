package com.petstation.dao.khachhang;

import java.time.LocalDate;
import java.util.List;

public interface TiemPhongDAO {
    final class ThongTinThuCung {
        public final String maThuCung;
        public final LocalDate ngaySinh;

        public ThongTinThuCung(String maThuCung, LocalDate ngaySinh) {
            this.maThuCung = maThuCung;
            this.ngaySinh = ngaySinh;
        }
    }

    final class MucTiemPhong {
        public final long id;
        public final String tenVaccine;
        public final LocalDate ngayTiem;
        public final LocalDate ngayHetHan;
        public final String ghiChu;

        public MucTiemPhong(
                long id,
                String tenVaccine,
                LocalDate ngayTiem,
                LocalDate ngayHetHan,
                String ghiChu) {
            this.id = id;
            this.tenVaccine = tenVaccine;
            this.ngayTiem = ngayTiem;
            this.ngayHetHan = ngayHetHan;
            this.ghiChu = ghiChu;
        }
    }

    ThongTinThuCung layThuCung(String ma);

    List<MucTiemPhong> layLichSu(String maThuCung);

    void luu(long id, String maThuCung, String ten, LocalDate ngay, LocalDate han, String ghiChu);

    void xoa(long id, String maThuCung);
}
