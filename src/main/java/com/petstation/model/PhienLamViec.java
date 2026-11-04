package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Thực thể Phiên làm việc / Ca thu ngân khớp bảng [phien_lam_viec].
 * Khóa chính: maPhien.
 */
public class PhienLamViec implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính (PK): Mã phiên làm việc (ví dụ: MPI00000001). */
    private String maPhien;
    /** Quan hệ kết hợp: Phiên làm việc gắn với một Lịch phân ca (có thể null nếu mở ca đột xuất). */
    private LichLamViec lichLamViec;
    /** Quan hệ kết hợp: Tài khoản nhân viên mở phiên làm việc. */
    private TaiKhoan taiKhoan;
    private LocalDateTime moLuc;
    private LocalDateTime dongLuc;
    private long tienDauCa;
    private long tienThucTe;
    private long chenhLech;
    private String giaiTrinh;
    private String trangThai;
    private String tenCa;
    /** Quan hệ kết hợp: Tài khoản người nhận bàn giao ca (có thể null). */
    private TaiKhoan nguoiNhanBanGiao;
    private String hinhThucBanGiao;

    /** Constructor mặc định. */
    public PhienLamViec() {
        this.trangThai = "OPEN";
        this.moLuc = LocalDateTime.now();
    }

    /** Constructor chỉ có khóa chính. */
    public PhienLamViec(String maPhien) {
        this();
        setMaPhien(maPhien);
    }

    /** Constructor chuẩn OOP nhận khóa chính maPhien và đối tượng thực thể liên kết. */
    public PhienLamViec(
            String maPhien,
            LichLamViec lichLamViec,
            TaiKhoan taiKhoan,
            LocalDateTime moLuc,
            LocalDateTime dongLuc,
            long tienDauCa,
            long tienThucTe,
            long chenhLech,
            String giaiTrinh,
            String trangThai,
            String tenCa,
            TaiKhoan nguoiNhanBanGiao,
            String hinhThucBanGiao) {
        this();
        setMaPhien(maPhien);
        setLichLamViec(lichLamViec);
        setTaiKhoan(taiKhoan);
        setMoLuc(moLuc);
        setDongLuc(dongLuc);
        setTienDauCa(tienDauCa);
        setTienThucTe(tienThucTe);
        setChenhLech(chenhLech);
        setGiaiTrinh(giaiTrinh);
        setTrangThai(trangThai);
        setTenCa(tenCa);
        setNguoiNhanBanGiao(nguoiNhanBanGiao);
        setHinhThucBanGiao(hinhThucBanGiao);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public PhienLamViec(long id) {
        this();
        setId(id);
    }

    public PhienLamViec(
            long id,
            String maPhien,
            LichLamViec lichLamViec,
            TaiKhoan taiKhoan,
            LocalDateTime moLuc,
            LocalDateTime dongLuc,
            long tienDauCa,
            long tienThucTe,
            long chenhLech,
            String giaiTrinh,
            String trangThai,
            String tenCa,
            TaiKhoan nguoiNhanBanGiao,
            String hinhThucBanGiao) {
        this(maPhien, lichLamViec, taiKhoan, moLuc, dongLuc, tienDauCa, tienThucTe, chenhLech, giaiTrinh, trangThai, tenCa, nguoiNhanBanGiao, hinhThucBanGiao);
        setId(id);
    }

    public PhienLamViec(
            long id,
            String maPhien,
            Long lichLamViecId,
            long taiKhoanId,
            LocalDateTime moLuc,
            LocalDateTime dongLuc,
            long tienDauCa,
            long tienThucTe,
            long chenhLech,
            String giaiTrinh,
            String trangThai,
            String tenCa,
            Long nguoiNhanBanGiaoId,
            String hinhThucBanGiao) {
        this(
                id,
                maPhien,
                lichLamViecId == null ? null : new LichLamViec(lichLamViecId),
                new TaiKhoan(taiKhoanId),
                moLuc,
                dongLuc,
                tienDauCa,
                tienThucTe,
                chenhLech,
                giaiTrinh,
                trangThai,
                tenCa,
                nguoiNhanBanGiaoId == null ? null : new TaiKhoan(nguoiNhanBanGiaoId),
                hinhThucBanGiao);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Tính chênh lệch két tiền mặt cuối ca (thuộc tính dẫn xuất: tienThucTe - (tienDauCa + tongThuTienMatTrongCa)). */
    public long tinhChenhLechKetTien(long tongThuTienMatTrongCa) {
        return tienThucTe - (tienDauCa + tongThuTienMatTrongCa);
    }

    /** Tính thời lượng phiên làm việc theo số phút (thuộc tính dẫn xuất từ moLuc và dongLuc). */
    public long tinhSoPhutPhien() {
        if (moLuc == null) return 0L;
        LocalDateTime ketThuc = dongLuc != null ? dongLuc : LocalDateTime.now();
        return Math.max(0L, Duration.between(moLuc, ketThuc).toMinutes());
    }

    // ==================== GETTER & SETTER ====================

    public String getMaPhien() { return this.maPhien; }
    public String layMaPhien() { return this.maPhien; }
    public String maPhien() { return this.maPhien; }
    public void setMaPhien(String maPhien) { this.maPhien = maPhien == null ? null : maPhien.trim(); }
    public void datMaPhien(String maPhien) { setMaPhien(maPhien); }

    public LichLamViec getLichLamViec() { return this.lichLamViec; }
    public LichLamViec layLichLamViec() { return this.lichLamViec; }
    public void setLichLamViec(LichLamViec lichLamViec) { this.lichLamViec = lichLamViec; }
    public void datLichLamViec(LichLamViec lichLamViec) { setLichLamViec(lichLamViec); }

    public TaiKhoan getTaiKhoan() { return this.taiKhoan; }
    public TaiKhoan layTaiKhoan() { return this.taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }
    public void datTaiKhoan(TaiKhoan taiKhoan) { setTaiKhoan(taiKhoan); }

    public LocalDateTime getMoLuc() { return this.moLuc; }
    public LocalDateTime layMoLuc() { return this.moLuc; }
    public LocalDateTime moLuc() { return this.moLuc; }
    public void setMoLuc(LocalDateTime moLuc) { this.moLuc = moLuc; }
    public void datMoLuc(LocalDateTime moLuc) { setMoLuc(moLuc); }

    public LocalDateTime getDongLuc() { return this.dongLuc; }
    public LocalDateTime layDongLuc() { return this.dongLuc; }
    public LocalDateTime dongLuc() { return this.dongLuc; }
    public void setDongLuc(LocalDateTime dongLuc) { this.dongLuc = dongLuc; }
    public void datDongLuc(LocalDateTime dongLuc) { setDongLuc(dongLuc); }

    public long getTienDauCa() { return this.tienDauCa; }
    public long layTienDauCa() { return this.tienDauCa; }
    public long tienDauCa() { return this.tienDauCa; }
    public void setTienDauCa(long tienDauCa) {
        if (tienDauCa < 0) throw new IllegalArgumentException("Tiền đầu ca không được âm.");
        this.tienDauCa = tienDauCa;
    }
    public void datTienDauCa(long tienDauCa) { setTienDauCa(tienDauCa); }

    public long getTienThucTe() { return this.tienThucTe; }
    public long layTienThucTe() { return this.tienThucTe; }
    public long tienThucTe() { return this.tienThucTe; }
    public void setTienThucTe(long tienThucTe) {
        if (tienThucTe < 0) throw new IllegalArgumentException("Tiền thực tế không được âm.");
        this.tienThucTe = tienThucTe;
    }
    public void datTienThucTe(long tienThucTe) { setTienThucTe(tienThucTe); }

    public long getChenhLech() { return this.chenhLech; }
    public long layChenhLech() { return this.chenhLech; }
    public long chenhLech() { return this.chenhLech; }
    public void setChenhLech(long chenhLech) { this.chenhLech = chenhLech; }
    public void datChenhLech(long chenhLech) { setChenhLech(chenhLech); }

    public String getGiaiTrinh() { return this.giaiTrinh; }
    public String layGiaiTrinh() { return this.giaiTrinh; }
    public String giaiTrinh() { return this.giaiTrinh; }
    public void setGiaiTrinh(String giaiTrinh) { this.giaiTrinh = giaiTrinh; }
    public void datGiaiTrinh(String giaiTrinh) { setGiaiTrinh(giaiTrinh); }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { setTrangThai(trangThai); }

    public String getTenCa() { return this.tenCa; }
    public String layTenCa() { return this.tenCa; }
    public String tenCa() { return this.tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }
    public void datTenCa(String tenCa) { setTenCa(tenCa); }

    public TaiKhoan getNguoiNhanBanGiao() { return this.nguoiNhanBanGiao; }
    public TaiKhoan layNguoiNhanBanGiao() { return this.nguoiNhanBanGiao; }
    public void setNguoiNhanBanGiao(TaiKhoan nguoiNhanBanGiao) { this.nguoiNhanBanGiao = nguoiNhanBanGiao; }
    public void datNguoiNhanBanGiao(TaiKhoan nguoiNhanBanGiao) { setNguoiNhanBanGiao(nguoiNhanBanGiao); }

    public String getHinhThucBanGiao() { return this.hinhThucBanGiao; }
    public String layHinhThucBanGiao() { return this.hinhThucBanGiao; }
    public String hinhThucBanGiao() { return this.hinhThucBanGiao; }
    public void setHinhThucBanGiao(String hinhThucBanGiao) { this.hinhThucBanGiao = hinhThucBanGiao; }
    public void datHinhThucBanGiao(String hinhThucBanGiao) { setHinhThucBanGiao(hinhThucBanGiao); }

    // ==================== CẦU NỐI TƯƠNG THÍCH TẦNG DAO ====================

    public long getId() { return DuLieu.layIdCauNoi(this, maPhien); }
    public long layId() { return getId(); }
    public long id() { return getId(); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maPhien == null || this.maPhien.isBlank()) && id > 0) {
            this.maPhien = DuLieu.taoMaTuId("MPI", 8, id);
        }
    }
    public void datId(long id) { setId(id); }

    public Long getLichLamViecId() { return lichLamViec == null ? null : lichLamViec.getId(); }
    public Long layLichLamViecId() { return getLichLamViecId(); }
    public Long lichLamViecId() { return getLichLamViecId(); }
    public void setLichLamViecId(Long lichLamViecId) {
        this.lichLamViec = lichLamViecId == null ? null : new LichLamViec(lichLamViecId);
    }
    public void datLichLamViecId(Long lichLamViecId) { setLichLamViecId(lichLamViecId); }

    public long getTaiKhoanId() { return taiKhoan == null ? 0L : taiKhoan.getId(); }
    public long layTaiKhoanId() { return getTaiKhoanId(); }
    public long taiKhoanId() { return getTaiKhoanId(); }
    public void setTaiKhoanId(long taiKhoanId) { this.taiKhoan = new TaiKhoan(taiKhoanId); }
    public void datTaiKhoanId(long taiKhoanId) { setTaiKhoanId(taiKhoanId); }

    public Long getNguoiNhanBanGiaoId() { return nguoiNhanBanGiao == null ? null : nguoiNhanBanGiao.getId(); }
    public Long layNguoiNhanBanGiaoId() { return getNguoiNhanBanGiaoId(); }
    public Long nguoiNhanBanGiaoId() { return getNguoiNhanBanGiaoId(); }
    public void setNguoiNhanBanGiaoId(Long nguoiNhanBanGiaoId) {
        this.nguoiNhanBanGiao = nguoiNhanBanGiaoId == null ? null : new TaiKhoan(nguoiNhanBanGiaoId);
    }
    public void datNguoiNhanBanGiaoId(Long nguoiNhanBanGiaoId) { setNguoiNhanBanGiaoId(nguoiNhanBanGiaoId); }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH (maPhien) ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhienLamViec that = (PhienLamViec) o;
        return Objects.equals(this.maPhien, that.maPhien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhien);
    }

    @Override
    public String toString() {
        return "PhienLamViec{" + "maPhien='" + maPhien + '\''
                + ", taiKhoan=" + (taiKhoan == null ? null : taiKhoan.getTenDangNhap())
                + ", trangThai='" + trangThai + '\'' + '}';
    }
}
