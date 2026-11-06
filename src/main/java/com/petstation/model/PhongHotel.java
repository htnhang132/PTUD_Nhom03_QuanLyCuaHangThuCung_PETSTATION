package com.petstation.model;

import com.petstation.util.DuLieu;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Thực thể Phòng lưu trú khách sạn thú cưng khớp bảng [phong_hotel].
 * Khóa chính: ma (ma_phong).
 */
public class PhongHotel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính (PK): Mã phòng (ví dụ: PH00000001). */
    private String ma;
    private String loai;
    private long gia;
    private String trangThai;
    private int sucChua;
    private LichPhong dangO;
    private List<LichPhong> datTruoc;

    /** Constructor mặc định. */
    public PhongHotel() {
        this.trangThai = "TRONG";
        this.sucChua = 1;
    }

    /** Constructor chỉ có khóa chính. */
    public PhongHotel(String ma) {
        this();
        setMa(ma);
    }

    /** Constructor đầy đủ thuộc tính. */
    public PhongHotel(
            String ma,
            String loai,
            long gia,
            String trangThai,
            int sucChua,
            LichPhong dangO,
            List<LichPhong> datTruoc) {
        this();
        setMa(ma);
        setLoai(loai);
        setGia(gia);
        setTrangThai(trangThai);
        setSucChua(sucChua);
        setDangO(dangO);
        setDatTruoc(datTruoc);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public PhongHotel(long id) {
        this();
        setId(id);
    }

    public PhongHotel(
            long id,
            String ma,
            String loai,
            long gia,
            String trangThai,
            int sucChua,
            LichPhong dangO,
            List<LichPhong> datTruoc) {
        this(ma, loai, gia, trangThai, sucChua, dangO, datTruoc);
        setId(id);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Lấy nhãn trạng thái tiếng Việt hiển thị trên giao diện (dẫn xuất từ trangThai). */
    public String layNhanTrangThai() {
        return switch (trangThai != null ? trangThai : "") {
            case "TRONG" -> "Trống";
            case "DANG_O" -> "Có thú";
            case "DANG_DON" -> "Đang dọn";
            case "BAO_TRI" -> "Bảo trì";
            default -> "Ngừng hoạt động";
        };
    }

    /** Kiểm tra phòng hiện có đang trống sẵn sàng đón thú cưng hay không (dẫn xuất). */
    public boolean kiemTraPhongTrong() {
        return "TRONG".equalsIgnoreCase(trangThai);
    }

    /** Tính tiền phòng theo số đêm lưu trú (dẫn xuất: soDem * gia). */
    public long tinhTienTheoSoDem(int soDem) {
        return Math.max(0, soDem) * gia;
    }

    // ==================== GETTER & SETTER ====================

    public String getMa() { return this.ma; }
    public String layMa() { return this.ma; }
    public String ma() { return this.ma; }
    public String getMaPhong() { return this.ma; }
    public void setMa(String ma) { this.ma = ma == null ? null : ma.trim(); }
    public void datMa(String ma) { setMa(ma); }
    public void setMaPhong(String maPhong) { setMa(maPhong); }

    public String getLoai() { return this.loai; }
    public String layLoai() { return this.loai; }
    public String loai() { return this.loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public void datLoai(String loai) { setLoai(loai); }

    public long getGia() { return this.gia; }
    public long layGia() { return this.gia; }
    public long gia() { return this.gia; }
    public void setGia(long gia) {
        if (gia < 0) throw new IllegalArgumentException("Giá phòng không được âm.");
        this.gia = gia;
    }
    public void datGia(long gia) { setGia(gia); }

    public String getTrangThai() { return this.trangThai; }
    public String layTrangThai() { return this.trangThai; }
    public String trangThai() { return this.trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void datTrangThai(String trangThai) { setTrangThai(trangThai); }

    public int getSucChua() { return this.sucChua; }
    public int laySucChua() { return this.sucChua; }
    public int sucChua() { return this.sucChua; }
    public void setSucChua(int sucChua) {
        if (sucChua < 0) throw new IllegalArgumentException("Sức chứa phòng không được âm.");
        this.sucChua = sucChua;
    }
    public void datSucChua(int sucChua) { setSucChua(sucChua); }

    public LichPhong getDangO() { return this.dangO; }
    public LichPhong layDangO() { return this.dangO; }
    public LichPhong dangO() { return this.dangO; }
    public void setDangO(LichPhong dangO) { this.dangO = dangO; }
    public void datDangO(LichPhong dangO) { setDangO(dangO); }

    public List<LichPhong> getDatTruoc() { return this.datTruoc; }
    public List<LichPhong> layDatTruoc() { return this.datTruoc; }
    public List<LichPhong> datTruoc() { return this.datTruoc; }
    public void setDatTruoc(List<LichPhong> datTruoc) { this.datTruoc = datTruoc; }
    public void datDatTruoc(List<LichPhong> datTruoc) { setDatTruoc(datTruoc); }

    // ==================== CẦU NỐI TƯƠNG THÍCH TẦNG DAO ====================

    public long getId() { return DuLieu.layIdCauNoi(this, ma); }
    public long layId() { return getId(); }
    public long id() { return getId(); }
    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.ma == null || this.ma.isBlank()) && id > 0) {
            this.ma = DuLieu.taoMaTuId("PH", 8, id);
        }
    }
    public void datId(long id) { setId(id); }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH (ma) ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhongHotel that = (PhongHotel) o;
        return Objects.equals(this.ma, that.ma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ma);
    }

    @Override
    public String toString() {
        return "PhongHotel{" + "ma='" + ma + '\'' + ", loai='" + loai + '\'' + ", gia=" + gia + ", trangThai='" + trangThai + '\'' + '}';
    }

    public static class LichPhong implements Serializable {
        private static final long serialVersionUID = 1L;

        private String maHopDong;
        private String thuCung;
        private String khachHang;
        private LocalDateTime ngayVao;
        private LocalDateTime ngayRa;

        public LichPhong() {}

        public LichPhong(String maHopDong) {
            this.maHopDong = maHopDong;
        }

        public LichPhong(long id) {
            setId(id);
        }

        public LichPhong(String maHopDong, String thuCung, String khachHang, LocalDateTime ngayVao, LocalDateTime ngayRa) {
            this.maHopDong = maHopDong;
            this.thuCung = thuCung;
            this.khachHang = khachHang;
            this.ngayVao = ngayVao;
            this.ngayRa = ngayRa;
        }

        public LichPhong(long id, String thuCung, String khachHang, LocalDateTime ngayVao, LocalDateTime ngayRa) {
            this(DuLieu.taoMaTuId("HDHT", 6, id), thuCung, khachHang, ngayVao, ngayRa);
            setId(id);
        }

        public String getMaHopDong() { return this.maHopDong; }
        public void setMaHopDong(String maHopDong) { this.maHopDong = maHopDong; }

        public long getId() { return DuLieu.layIdCauNoi(this, maHopDong); }
        public long layId() { return getId(); }
        public long id() { return getId(); }
        public void setId(long id) {
            DuLieu.luuIdCauNoi(this, id);
            if ((this.maHopDong == null || this.maHopDong.isBlank()) && id > 0) {
                this.maHopDong = DuLieu.taoMaTuId("HDHT", 6, id);
            }
        }
        public void datId(long id) { setId(id); }

        public String getThuCung() { return this.thuCung; }
        public String layThuCung() { return this.thuCung; }
        public String thuCung() { return this.thuCung; }
        public void setThuCung(String thuCung) { this.thuCung = thuCung; }
        public void datThuCung(String thuCung) { this.thuCung = thuCung; }

        public String getKhachHang() { return this.khachHang; }
        public String layKhachHang() { return this.khachHang; }
        public String khachHang() { return this.khachHang; }
        public void setKhachHang(String khachHang) { this.khachHang = khachHang; }
        public void datKhachHang(String khachHang) { this.khachHang = khachHang; }

        public LocalDateTime getNgayVao() { return this.ngayVao; }
        public LocalDateTime layNgayVao() { return this.ngayVao; }
        public LocalDateTime ngayVao() { return this.ngayVao; }
        public void setNgayVao(LocalDateTime ngayVao) { this.ngayVao = ngayVao; }
        public void datNgayVao(LocalDateTime ngayVao) { this.ngayVao = ngayVao; }

        public LocalDateTime getNgayRa() { return this.ngayRa; }
        public LocalDateTime layNgayRa() { return this.ngayRa; }
        public LocalDateTime ngayRa() { return this.ngayRa; }
        public void setNgayRa(LocalDateTime ngayRa) { this.ngayRa = ngayRa; }
        public void datNgayRa(LocalDateTime ngayRa) { this.ngayRa = ngayRa; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LichPhong that = (LichPhong) o;
            return Objects.equals(this.maHopDong, that.maHopDong);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maHopDong);
        }

        @Override
        public String toString() {
            return "LichPhong{" + "maHopDong='" + maHopDong + '\'' + ", thuCung='" + thuCung + '\'' + ", khachHang='" + khachHang + '\'' + '}';
        }
    }
}
