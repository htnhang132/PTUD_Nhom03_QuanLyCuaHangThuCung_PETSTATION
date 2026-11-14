package com.petstation.model.luutru;

import com.petstation.dto.HotelDTO;
import com.petstation.model.chung.TrangThaiPhongHotel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhongHotel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String maPhong;
    private HotelDTO.LichPhong dangO;
    private List<HotelDTO.LichPhong> datTruoc;
    private String loaiPhong;
    private long giaDem;
    private int sucChua;
    private String ghiChu;
    private String trangThai;

    public PhongHotel() {
        this.trangThai = TrangThaiPhongHotel.TRONG.maDB();
        this.sucChua = 1;
        this.ghiChu = "";
        this.datTruoc = new ArrayList<>();
    }

    public PhongHotel(String maPhong) {
        this();
        setMaPhong(maPhong);
    }

    public PhongHotel(
            String maPhong,
            HotelDTO.LichPhong dangO,
            List<HotelDTO.LichPhong> datTruoc,
            String loaiPhong,
            long giaDem,
            int sucChua,
            String trangThai) {
        this();
        setMaPhong(maPhong);
        setDangO(dangO);
        setDatTruoc(datTruoc);
        setLoaiPhong(loaiPhong);
        setGiaDem(giaDem);
        setSucChua(sucChua);
        setTrangThai(trangThai);
    }

    public PhongHotel(
            String maPhong,
            String loaiPhong,
            long giaDem,
            int sucChua,
            String ghiChu,
            String trangThai) {
        this();
        setMaPhong(maPhong);
        setLoaiPhong(loaiPhong);
        setGiaDem(giaDem);
        setSucChua(sucChua);
        setGhiChu(ghiChu);
        setTrangThai(trangThai);
    }

    public String getMaPhong() {
        return this.maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong == null ? null : maPhong.trim();
    }

    public HotelDTO.LichPhong getDangO() {
        return this.dangO;
    }

    public void setDangO(HotelDTO.LichPhong dangO) {
        this.dangO = dangO;
    }

    public List<HotelDTO.LichPhong> getDatTruoc() {
        return this.datTruoc != null ? this.datTruoc : List.of();
    }

    public void setDatTruoc(List<HotelDTO.LichPhong> datTruoc) {
        this.datTruoc = datTruoc != null ? new ArrayList<>(datTruoc) : new ArrayList<>();
    }

    public String getLoaiPhong() {
        return this.loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public long getGiaDem() {
        return this.giaDem;
    }

    public void setGiaDem(long giaDem) {
        if (giaDem < 0) throw new IllegalArgumentException("Giá phòng không được âm.");
        this.giaDem = giaDem;
    }

    public int getSucChua() {
        return this.sucChua;
    }

    public void setSucChua(int sucChua) {
        if (sucChua < 0) throw new IllegalArgumentException("Sức chứa phòng không được âm.");
        this.sucChua = forcePositive(sucChua);
    }

    public String getGhiChu() {
        return this.ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu != null ? ghiChu : "";
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getNhanTrangThai() {
        return switch (trangThai != null ? trangThai : "") {
            case "TRONG" -> "Trống";
            case "DANG_O" -> "Có thú";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI" -> "Bảo trì";
            default -> "Ngừng hoạt động";
        };
    }

    public boolean kiemTraPhongTrong() {
        return TrangThaiPhongHotel.TRONG.maDB().equalsIgnoreCase(trangThai);
    }

    private int forcePositive(int val) {
        return Math.max(1, val);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhongHotel that = (PhongHotel) o;
        return Objects.equals(this.maPhong, that.maPhong);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhong);
    }

    @Override
    public String toString() {
        return "PhongHotel{"
                + "maPhong='"
                + maPhong
                + '\''
                + ", loaiPhong='"
                + loaiPhong
                + '\''
                + ", giaDem="
                + giaDem
                + ", trangThai='"
                + trangThai
                + '\''
                + '}';
    }
}
