package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Biểu diễn dữ liệu ánh xạ với bảng nhat_ky_he_thong trong SQL Server.
 *
 * @param id id
 * @param idNguoiThucHienTaiKhoan id người thực hiện tài khoản
 * @param thaoTac thao tác
 * @param kieuThucThe kiểu thực thể
 * @param idThucThe id thực thể
 * @param giaTriCu giá trị cũ
 * @param giaTriMoi giá trị mới
 * @param ghiChu ghi chú
 * @param thoiDiemXayRa thời điểm xảy ra
 */
public class NhatKyHeThong implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private Long idNguoiThucHienTaiKhoan;
    private String thaoTac;
    private String kieuThucThe;
    private String idThucThe;
    private String giaTriCu;
    private String giaTriMoi;
    private String ghiChu;
    private LocalDateTime thoiDiemXayRa;

    public NhatKyHeThong() {}

    public NhatKyHeThong(long id, Long idNguoiThucHienTaiKhoan, String thaoTac, String kieuThucThe, String idThucThe, String giaTriCu, String giaTriMoi, String ghiChu, LocalDateTime thoiDiemXayRa) {
        this.id = id;
        this.idNguoiThucHienTaiKhoan = idNguoiThucHienTaiKhoan;
        this.thaoTac = thaoTac;
        this.kieuThucThe = kieuThucThe;
        this.idThucThe = idThucThe;
        this.giaTriCu = giaTriCu;
        this.giaTriMoi = giaTriMoi;
        this.ghiChu = ghiChu;
        this.thoiDiemXayRa = thoiDiemXayRa;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public Long getIdNguoiThucHienTaiKhoan() { return this.idNguoiThucHienTaiKhoan; }
    public Long layIdNguoiThucHienTaiKhoan() { return this.idNguoiThucHienTaiKhoan; }
    public Long idNguoiThucHienTaiKhoan() { return this.idNguoiThucHienTaiKhoan; }
    public void setIdNguoiThucHienTaiKhoan(Long idNguoiThucHienTaiKhoan) { this.idNguoiThucHienTaiKhoan = idNguoiThucHienTaiKhoan; }
    public void datIdNguoiThucHienTaiKhoan(Long idNguoiThucHienTaiKhoan) { this.idNguoiThucHienTaiKhoan = idNguoiThucHienTaiKhoan; }

    public String getThaoTac() { return this.thaoTac; }
    public String layThaoTac() { return this.thaoTac; }
    public String thaoTac() { return this.thaoTac; }
    public void setThaoTac(String thaoTac) { this.thaoTac = thaoTac; }
    public void datThaoTac(String thaoTac) { this.thaoTac = thaoTac; }

    public String getKieuThucThe() { return this.kieuThucThe; }
    public String layKieuThucThe() { return this.kieuThucThe; }
    public String kieuThucThe() { return this.kieuThucThe; }
    public void setKieuThucThe(String kieuThucThe) { this.kieuThucThe = kieuThucThe; }
    public void datKieuThucThe(String kieuThucThe) { this.kieuThucThe = kieuThucThe; }

    public String getIdThucThe() { return this.idThucThe; }
    public String layIdThucThe() { return this.idThucThe; }
    public String idThucThe() { return this.idThucThe; }
    public void setIdThucThe(String idThucThe) { this.idThucThe = idThucThe; }
    public void datIdThucThe(String idThucThe) { this.idThucThe = idThucThe; }

    public String getGiaTriCu() { return this.giaTriCu; }
    public String layGiaTriCu() { return this.giaTriCu; }
    public String giaTriCu() { return this.giaTriCu; }
    public void setGiaTriCu(String giaTriCu) { this.giaTriCu = giaTriCu; }
    public void datGiaTriCu(String giaTriCu) { this.giaTriCu = giaTriCu; }

    public String getGiaTriMoi() { return this.giaTriMoi; }
    public String layGiaTriMoi() { return this.giaTriMoi; }
    public String giaTriMoi() { return this.giaTriMoi; }
    public void setGiaTriMoi(String giaTriMoi) { this.giaTriMoi = giaTriMoi; }
    public void datGiaTriMoi(String giaTriMoi) { this.giaTriMoi = giaTriMoi; }

    public String getGhiChu() { return this.ghiChu; }
    public String layGhiChu() { return this.ghiChu; }
    public String ghiChu() { return this.ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public void datGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getThoiDiemXayRa() { return this.thoiDiemXayRa; }
    public LocalDateTime layThoiDiemXayRa() { return this.thoiDiemXayRa; }
    public LocalDateTime thoiDiemXayRa() { return this.thoiDiemXayRa; }
    public void setThoiDiemXayRa(LocalDateTime thoiDiemXayRa) { this.thoiDiemXayRa = thoiDiemXayRa; }
    public void datThoiDiemXayRa(LocalDateTime thoiDiemXayRa) { this.thoiDiemXayRa = thoiDiemXayRa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhatKyHeThong that = (NhatKyHeThong) o;
        return this.id == that.id
                && Objects.equals(idNguoiThucHienTaiKhoan, that.idNguoiThucHienTaiKhoan)
                && Objects.equals(thaoTac, that.thaoTac)
                && Objects.equals(kieuThucThe, that.kieuThucThe)
                && Objects.equals(idThucThe, that.idThucThe)
                && Objects.equals(giaTriCu, that.giaTriCu)
                && Objects.equals(giaTriMoi, that.giaTriMoi)
                && Objects.equals(ghiChu, that.ghiChu)
                && Objects.equals(thoiDiemXayRa, that.thoiDiemXayRa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idNguoiThucHienTaiKhoan, thaoTac, kieuThucThe, idThucThe, giaTriCu, giaTriMoi, ghiChu, thoiDiemXayRa);
    }

    @Override
    public String toString() {
        return "NhatKyHeThong{" + "id=" + id + ", " + "idNguoiThucHienTaiKhoan=" + idNguoiThucHienTaiKhoan + ", " + "thaoTac=" + thaoTac + ", " + "kieuThucThe=" + kieuThucThe + ", " + "idThucThe=" + idThucThe + ", " + "giaTriCu=" + giaTriCu + ", " + "giaTriMoi=" + giaTriMoi + ", " + "ghiChu=" + ghiChu + ", " + "thoiDiemXayRa=" + thoiDiemXayRa + "}";
    }
}
