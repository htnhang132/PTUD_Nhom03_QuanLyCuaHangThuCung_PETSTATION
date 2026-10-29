package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể phiên làm việc / ca thu ngân khớp bảng [phien_lam_viec].
 */
public class PhienLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maPhien;
    private Long lichLamViecId;
    private long taiKhoanId;
    private LocalDateTime moLuc;
    private LocalDateTime dongLuc;
    private long tienDauCa;
    private long tienThucTe;
    private long chenhLech;
    private String giaiTrinh;
    private String trangThai;
    private String tenCa;
    private Long nguoiNhanBanGiaoId;
    private String hinhThucBanGiao;

    public PhienLamViec() {}

    public PhienLamViec(long id, String maPhien, Long lichLamViecId, long taiKhoanId, LocalDateTime moLuc, LocalDateTime dongLuc, long tienDauCa, long tienThucTe, long chenhLech, String giaiTrinh, String trangThai, String tenCa, Long nguoiNhanBanGiaoId, String hinhThucBanGiao) {
        this.id = id;
        this.maPhien = maPhien;
        this.lichLamViecId = lichLamViecId;
        this.taiKhoanId = taiKhoanId;
        this.moLuc = moLuc;
        this.dongLuc = dongLuc;
        this.tienDauCa = tienDauCa;
        this.tienThucTe = tienThucTe;
        this.chenhLech = chenhLech;
        this.giaiTrinh = giaiTrinh;
        this.trangThai = trangThai;
        this.tenCa = tenCa;
        this.nguoiNhanBanGiaoId = nguoiNhanBanGiaoId;
        this.hinhThucBanGiao = hinhThucBanGiao;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaPhien() { return this.maPhien; }
    public String layMaPhien() { return this.maPhien; }
    public String maPhien() { return this.maPhien; }
    public void setMaPhien(String maPhien) { this.maPhien = maPhien; }
    public void datMaPhien(String maPhien) { this.maPhien = maPhien; }

    public Long getLichLamViecId() { return this.lichLamViecId; }
    public Long layLichLamViecId() { return this.lichLamViecId; }
    public Long lichLamViecId() { return this.lichLamViecId; }
    public void setLichLamViecId(Long lichLamViecId) { this.lichLamViecId = lichLamViecId; }
    public void datLichLamViecId(Long lichLamViecId) { this.lichLamViecId = lichLamViecId; }

    public long getTaiKhoanId() { return this.taiKhoanId; }
    public long layTaiKhoanId() { return this.taiKhoanId; }
    public long taiKhoanId() { return this.taiKhoanId; }
    public void setTaiKhoanId(long taiKhoanId) { this.taiKhoanId = taiKhoanId; }
    public void datTaiKhoanId(long taiKhoanId) { this.taiKhoanId = taiKhoanId; }

    public LocalDateTime getMoLuc() { return this.moLuc; }
    public LocalDateTime layMoLuc() { return this.moLuc; }
    public LocalDateTime moLuc() { return this.moLuc; }
    public void setMoLuc(LocalDateTime moLuc) { this.moLuc = moLuc; }
    public void datMoLuc(LocalDateTime moLuc) { this.moLuc = moLuc; }

    public LocalDateTime getDongLuc() { return this.dongLuc; }
    public LocalDateTime layDongLuc() { return this.dongLuc; }
    public LocalDateTime dongLuc() { return this.dongLuc; }
    public void setDongLuc(LocalDateTime dongLuc) { this.dongLuc = dongLuc; }
    public void datDongLuc(LocalDateTime dongLuc) { this.dongLuc = dongLuc; }

    public long getTienDauCa() { return this.tienDauCa; }
    public long layTienDauCa() { return this.tienDauCa; }
    public long tienDauCa() { return this.tienDauCa; }
    public void setTienDauCa(long tienDauCa) { this.tienDauCa = tienDauCa; }
    public void datTienDauCa(long tienDauCa) { this.tienDauCa = tienDauCa; }

    public long getTienThucTe() { return this.tienThucTe; }
    public long layTienThucTe() { return this.tienThucTe; }
    public long tienThucTe() { return this.tienThucTe; }
    public void setTienThucTe(long tienThucTe) { this.tienThucTe = tienThucTe; }
    public void datTienThucTe(long tienThucTe) { this.tienThucTe = tienThucTe; }

    public long getChenhLech() { return this.chenhLech; }
    public long layChenhLech() { return this.chenhLech; }
    public long chenhLech() { return this.chenhLech; }
    public void setChenhLech(long chenhLech) { this.chenhLech = chenhLech; }
    public void datChenhLech(long chenhLech) { this.chenhLech = chenhLech; }

    public String getGiaiTrinh() { return this.giaiTrinh; }
    public String layGiaiTrinh() { return this.giaiTrinh; }
    public String giaiTrinh() { return this.giaiTrinh; }
    public void setGiaiTrinh(String giaiTrinh) { this.giaiTrinh = giaiTrinh; }
    public void datGiaiTrinh(String giaiTrinh) { this.giaiTrinh = giaiTrinh; }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenCa() { return this.tenCa; }
    public String layTenCa() { return this.tenCa; }
    public String tenCa() { return this.tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public void datTenCa(String tenCa) { this.tenCa = tenCa; }

    public Long getNguoiNhanBanGiaoId() { return this.nguoiNhanBanGiaoId; }
    public Long layNguoiNhanBanGiaoId() { return this.nguoiNhanBanGiaoId; }
    public Long nguoiNhanBanGiaoId() { return this.nguoiNhanBanGiaoId; }
    public void setNguoiNhanBanGiaoId(Long nguoiNhanBanGiaoId) { this.nguoiNhanBanGiaoId = nguoiNhanBanGiaoId; }
    public void datNguoiNhanBanGiaoId(Long nguoiNhanBanGiaoId) { this.nguoiNhanBanGiaoId = nguoiNhanBanGiaoId; }

    public String getHinhThucBanGiao() { return this.hinhThucBanGiao; }
    public String layHinhThucBanGiao() { return this.hinhThucBanGiao; }
    public String hinhThucBanGiao() { return this.hinhThucBanGiao; }
    public void setHinhThucBanGiao(String hinhThucBanGiao) { this.hinhThucBanGiao = hinhThucBanGiao; }
    public void datHinhThucBanGiao(String hinhThucBanGiao) { this.hinhThucBanGiao = hinhThucBanGiao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhienLamViec that = (PhienLamViec) o;
        return this.id == that.id
                && Objects.equals(maPhien, that.maPhien)
                && Objects.equals(lichLamViecId, that.lichLamViecId)
                && this.taiKhoanId == that.taiKhoanId
                && Objects.equals(moLuc, that.moLuc)
                && Objects.equals(dongLuc, that.dongLuc)
                && this.tienDauCa == that.tienDauCa
                && this.tienThucTe == that.tienThucTe
                && this.chenhLech == that.chenhLech
                && Objects.equals(giaiTrinh, that.giaiTrinh)
                && Objects.equals(trangThai, that.trangThai)
                && Objects.equals(tenCa, that.tenCa)
                && Objects.equals(nguoiNhanBanGiaoId, that.nguoiNhanBanGiaoId)
                && Objects.equals(hinhThucBanGiao, that.hinhThucBanGiao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maPhien, lichLamViecId, taiKhoanId, moLuc, dongLuc, tienDauCa, tienThucTe, chenhLech, giaiTrinh, trangThai, tenCa, nguoiNhanBanGiaoId, hinhThucBanGiao);
    }

    @Override
    public String toString() {
        return "PhienLamViec{" + "id=" + id + ", " + "maPhien=" + maPhien + ", " + "lichLamViecId=" + lichLamViecId + ", " + "taiKhoanId=" + taiKhoanId + ", " + "moLuc=" + moLuc + ", " + "dongLuc=" + dongLuc + ", " + "tienDauCa=" + tienDauCa + ", " + "tienThucTe=" + tienThucTe + ", " + "chenhLech=" + chenhLech + ", " + "giaiTrinh=" + giaiTrinh + ", " + "trangThai=" + trangThai + ", " + "tenCa=" + tenCa + ", " + "nguoiNhanBanGiaoId=" + nguoiNhanBanGiaoId + ", " + "hinhThucBanGiao=" + hinhThucBanGiao + "}";
    }
}
