package com.petstation.dto;

import com.petstation.model.nhansu.NhanVien;

import java.time.LocalTime;
import java.util.List;

public final class TomTatCaLamViec {
    private final boolean dangMo;
    private final String tenCa;
    private final LocalTime gioMoCa;
    private final long tienDauCa;
    private final long doanhThuTienMat;
    private final long doanhThuChuyenKhoanVaThe;
    private final long tienCocHotelTienMat;
    private final long tienMatDuKien;
    private final String banGiaoGanNhat;
    private final String tieuDeDangCho;
    private final List<NhanVien> nguoiNhan;

    public TomTatCaLamViec(
            boolean dangMo,
            String tenCa,
            LocalTime gioMoCa,
            long tienDauCa,
            long doanhThuTienMat,
            long doanhThuChuyenKhoanVaThe,
            long tienCocHotelTienMat,
            long tienMatDuKien,
            String banGiaoGanNhat,
            String tieuDeDangCho,
            List<NhanVien> nguoiNhan) {
        this.dangMo = dangMo;
        this.tenCa = tenCa == null ? "" : tenCa;
        this.gioMoCa = gioMoCa;
        this.tienDauCa = tienDauCa;
        this.doanhThuTienMat = doanhThuTienMat;
        this.doanhThuChuyenKhoanVaThe = doanhThuChuyenKhoanVaThe;
        this.tienCocHotelTienMat = tienCocHotelTienMat;
        this.tienMatDuKien = tienMatDuKien;
        this.banGiaoGanNhat = banGiaoGanNhat == null ? "" : banGiaoGanNhat;
        this.tieuDeDangCho = tieuDeDangCho == null ? "" : tieuDeDangCho;
        this.nguoiNhan = nguoiNhan == null ? List.of() : List.copyOf(nguoiNhan);
    }

    public boolean isDangMo() {
        return dangMo;
    }

    public String getTenCa() {
        return tenCa;
    }

    public LocalTime getGioMoCa() {
        return gioMoCa;
    }

    public long getTienDauCa() {
        return tienDauCa;
    }

    public long getDoanhThuTienMat() {
        return doanhThuTienMat;
    }

    public long getDoanhThuChuyenKhoanVaThe() {
        return doanhThuChuyenKhoanVaThe;
    }

    public long getTienCocHotelTienMat() {
        return tienCocHotelTienMat;
    }

    public long getTienMatDuKien() {
        return tienMatDuKien;
    }

    public String getBanGiaoGanNhat() {
        return banGiaoGanNhat;
    }

    public String getTieuDeDangCho() {
        return tieuDeDangCho;
    }

    public List<NhanVien> getNguoiNhan() {
        return nguoiNhan;
    }
}
