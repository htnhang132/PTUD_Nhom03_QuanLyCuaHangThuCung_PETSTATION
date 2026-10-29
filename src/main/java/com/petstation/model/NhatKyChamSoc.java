package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể nhật ký chăm sóc thú cưng khớp bảng [nhat_ky_cham_soc].
 */
public class NhatKyChamSoc implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String maNhatKy;
    private Long hopDongId;
    private Long spaId;
    private long nhanVienId;
    private LocalDateTime thoiGian;
    private String anUong;
    private String veSinh;
    private String tinhTrang;
    private String huongDanThuoc;
    private String daThucHien;
    private String anhTruoc;
    private String anhSau;
    private String tepDinhKem;

    public NhatKyChamSoc() {}

    public NhatKyChamSoc(long id, String maNhatKy, Long hopDongId, Long spaId, long nhanVienId, LocalDateTime thoiGian, String anUong, String veSinh, String tinhTrang, String huongDanThuoc, String daThucHien, String anhTruoc, String anhSau, String tepDinhKem) {
        this.id = id;
        this.maNhatKy = maNhatKy;
        this.hopDongId = hopDongId;
        this.spaId = spaId;
        this.nhanVienId = nhanVienId;
        this.thoiGian = thoiGian;
        this.anUong = anUong;
        this.veSinh = veSinh;
        this.tinhTrang = tinhTrang;
        this.huongDanThuoc = huongDanThuoc;
        this.daThucHien = daThucHien;
        this.anhTruoc = anhTruoc;
        this.anhSau = anhSau;
        this.tepDinhKem = tepDinhKem;
    }

    public long getId() { return this.id; }
    public long layId() { return this.id; }
    public long id() { return this.id; }
    public void setId(long id) { this.id = id; }
    public void datId(long id) { this.id = id; }

    public String getMaNhatKy() { return this.maNhatKy; }
    public String layMaNhatKy() { return this.maNhatKy; }
    public String maNhatKy() { return this.maNhatKy; }
    public void setMaNhatKy(String maNhatKy) { this.maNhatKy = maNhatKy; }
    public void datMaNhatKy(String maNhatKy) { this.maNhatKy = maNhatKy; }

    public Long getHopDongId() { return this.hopDongId; }
    public Long layHopDongId() { return this.hopDongId; }
    public Long hopDongId() { return this.hopDongId; }
    public void setHopDongId(Long hopDongId) { this.hopDongId = hopDongId; }
    public void datHopDongId(Long hopDongId) { this.hopDongId = hopDongId; }

    public Long getSpaId() { return this.spaId; }
    public Long laySpaId() { return this.spaId; }
    public Long spaId() { return this.spaId; }
    public void setSpaId(Long spaId) { this.spaId = spaId; }
    public void datSpaId(Long spaId) { this.spaId = spaId; }

    public long getNhanVienId() { return this.nhanVienId; }
    public long layNhanVienId() { return this.nhanVienId; }
    public long nhanVienId() { return this.nhanVienId; }
    public void setNhanVienId(long nhanVienId) { this.nhanVienId = nhanVienId; }
    public void datNhanVienId(long nhanVienId) { this.nhanVienId = nhanVienId; }

    public LocalDateTime getThoiGian() { return this.thoiGian; }
    public LocalDateTime layThoiGian() { return this.thoiGian; }
    public LocalDateTime thoiGian() { return this.thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
    public void datThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public String getAnUong() { return this.anUong; }
    public String layAnUong() { return this.anUong; }
    public String anUong() { return this.anUong; }
    public void setAnUong(String anUong) { this.anUong = anUong; }
    public void datAnUong(String anUong) { this.anUong = anUong; }

    public String getVeSinh() { return this.veSinh; }
    public String layVeSinh() { return this.veSinh; }
    public String veSinh() { return this.veSinh; }
    public void setVeSinh(String veSinh) { this.veSinh = veSinh; }
    public void datVeSinh(String veSinh) { this.veSinh = veSinh; }

    public String getTinhTrang() { return this.tinhTrang; }
    public String layTinhTrang() { return this.tinhTrang; }
    public String tinhTrang() { return this.tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }
    public void datTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getHuongDanThuoc() { return this.huongDanThuoc; }
    public String layHuongDanThuoc() { return this.huongDanThuoc; }
    public String huongDanThuoc() { return this.huongDanThuoc; }
    public void setHuongDanThuoc(String huongDanThuoc) { this.huongDanThuoc = huongDanThuoc; }
    public void datHuongDanThuoc(String huongDanThuoc) { this.huongDanThuoc = huongDanThuoc; }

    public String getDaThucHien() { return this.daThucHien; }
    public String layDaThucHien() { return this.daThucHien; }
    public String daThucHien() { return this.daThucHien; }
    public void setDaThucHien(String daThucHien) { this.daThucHien = daThucHien; }
    public void datDaThucHien(String daThucHien) { this.daThucHien = daThucHien; }

    public String getAnhTruoc() { return this.anhTruoc; }
    public String layAnhTruoc() { return this.anhTruoc; }
    public String anhTruoc() { return this.anhTruoc; }
    public void setAnhTruoc(String anhTruoc) { this.anhTruoc = anhTruoc; }
    public void datAnhTruoc(String anhTruoc) { this.anhTruoc = anhTruoc; }

    public String getAnhSau() { return this.anhSau; }
    public String layAnhSau() { return this.anhSau; }
    public String anhSau() { return this.anhSau; }
    public void setAnhSau(String anhSau) { this.anhSau = anhSau; }
    public void datAnhSau(String anhSau) { this.anhSau = anhSau; }

    public String getTepDinhKem() { return this.tepDinhKem; }
    public String layTepDinhKem() { return this.tepDinhKem; }
    public String tepDinhKem() { return this.tepDinhKem; }
    public void setTepDinhKem(String tepDinhKem) { this.tepDinhKem = tepDinhKem; }
    public void datTepDinhKem(String tepDinhKem) { this.tepDinhKem = tepDinhKem; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhatKyChamSoc that = (NhatKyChamSoc) o;
        return this.id == that.id
                && Objects.equals(maNhatKy, that.maNhatKy)
                && Objects.equals(hopDongId, that.hopDongId)
                && Objects.equals(spaId, that.spaId)
                && this.nhanVienId == that.nhanVienId
                && Objects.equals(thoiGian, that.thoiGian)
                && Objects.equals(anUong, that.anUong)
                && Objects.equals(veSinh, that.veSinh)
                && Objects.equals(tinhTrang, that.tinhTrang)
                && Objects.equals(huongDanThuoc, that.huongDanThuoc)
                && Objects.equals(daThucHien, that.daThucHien)
                && Objects.equals(anhTruoc, that.anhTruoc)
                && Objects.equals(anhSau, that.anhSau)
                && Objects.equals(tepDinhKem, that.tepDinhKem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maNhatKy, hopDongId, spaId, nhanVienId, thoiGian, anUong, veSinh, tinhTrang, huongDanThuoc, daThucHien, anhTruoc, anhSau, tepDinhKem);
    }

    @Override
    public String toString() {
        return "NhatKyChamSoc{" + "id=" + id + ", " + "maNhatKy=" + maNhatKy + ", " + "hopDongId=" + hopDongId + ", " + "spaId=" + spaId + ", " + "nhanVienId=" + nhanVienId + ", " + "thoiGian=" + thoiGian + ", " + "anUong=" + anUong + ", " + "veSinh=" + veSinh + ", " + "tinhTrang=" + tinhTrang + ", " + "huongDanThuoc=" + huongDanThuoc + ", " + "daThucHien=" + daThucHien + ", " + "anhTruoc=" + anhTruoc + ", " + "anhSau=" + anhSau + ", " + "tepDinhKem=" + tepDinhKem + "}";
    }
}
