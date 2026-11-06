package com.petstation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.petstation.util.DuLieu;

/**
 * Thực thể Yêu cầu phê duyệt khớp bảng [yeu_cau_phe_duyet].
 * Tuân thủ chuẩn OOP môn PTUD:
 * - Khóa chính là mã yêu cầu (maYeuCau). Đã bỏ thuộc tính id.
 * - Quan hệ kết hợp với TaiKhoan (người gửi và người xử lý).
 * - Phương thức kiểm tra trạng thái xử lý dẫn xuất và equals/hashCode theo khóa chính maYeuCau.
 * - Cung cấp hàm cầu nối getId()/setId() tương thích với tầng DAO/GUI.
 */
public class YeuCauPheDuyet implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Khóa chính nghiệp vụ của Yêu cầu phê duyệt */
    private String maYeuCau;
    private String loai;
    /** Quan hệ kết hợp: Tài khoản nhân viên gửi yêu cầu phê duyệt. */
    private TaiKhoan nguoiGui;
    private String loaiDoiTuong;
    private String maDoiTuong;
    private String lyDo;
    private String trangThai;
    /**
     * Quan hệ kết hợp: Tài khoản quản lý phê duyệt/từ chối yêu cầu (có thể null khi đang chờ).
     */
    private TaiKhoan nguoiXuLy;
    private String phanHoi;
    private LocalDateTime guiLuc;
    private LocalDateTime xuLyLuc;
    private String noiDungTruoc;
    private String noiDungSau;
    private String payload;

    /** Constructor mặc định. */
    public YeuCauPheDuyet() {
        this.trangThai = "CHO_DUYET";
        this.guiLuc = LocalDateTime.now();
    }

    /** Constructor theo khóa chính nghiệp vụ maYeuCau. */
    public YeuCauPheDuyet(String maYeuCau) {
        this();
        setMaYeuCau(maYeuCau);
    }

    /** Constructor cầu nối nhận id từ DAO. */
    public YeuCauPheDuyet(long id) {
        this();
        setId(id);
    }

    /** Constructor chuẩn OOP nhận đối tượng TaiKhoan và khóa chính maYeuCau. */
    public YeuCauPheDuyet(
            String maYeuCau,
            String loai,
            TaiKhoan nguoiGui,
            String loaiDoiTuong,
            String maDoiTuong,
            String lyDo,
            String trangThai,
            TaiKhoan nguoiXuLy,
            String phanHoi,
            LocalDateTime guiLuc,
            LocalDateTime xuLyLuc,
            String noiDungTruoc,
            String noiDungSau,
            String payload) {
        setMaYeuCau(maYeuCau);
        setLoai(loai);
        setNguoiGui(nguoiGui);
        setLoaiDoiTuong(loaiDoiTuong);
        setMaDoiTuong(maDoiTuong);
        setLyDo(lyDo);
        setTrangThai(trangThai);
        setNguoiXuLy(nguoiXuLy);
        setPhanHoi(phanHoi);
        setGuiLuc(guiLuc);
        setXuLyLuc(xuLyLuc);
        setNoiDungTruoc(noiDungTruoc);
        setNoiDungSau(noiDungSau);
        setPayload(payload);
    }

    /** Constructor cầu nối tương thích DAO cũ (có tham số id). */
    public YeuCauPheDuyet(
            long id,
            String maYeuCau,
            String loai,
            TaiKhoan nguoiGui,
            String loaiDoiTuong,
            String maDoiTuong,
            String lyDo,
            String trangThai,
            TaiKhoan nguoiXuLy,
            String phanHoi,
            LocalDateTime guiLuc,
            LocalDateTime xuLyLuc,
            String noiDungTruoc,
            String noiDungSau,
            String payload) {
        this(maYeuCau, loai, nguoiGui, loaiDoiTuong, maDoiTuong, lyDo, trangThai, nguoiXuLy, phanHoi, guiLuc, xuLyLuc, noiDungTruoc, noiDungSau, payload);
        setId(id);
    }

    /** Constructor cầu nối tương thích ngược với tầng DAO. */
    public YeuCauPheDuyet(
            long id,
            String maYeuCau,
            String loai,
            long nguoiGuiId,
            String loaiDoiTuong,
            String maDoiTuong,
            String lyDo,
            String trangThai,
            Long nguoiXuLyId,
            String phanHoi,
            LocalDateTime guiLuc,
            LocalDateTime xuLyLuc,
            String noiDungTruoc,
            String noiDungSau,
            String payload) {
        this(
                id,
                maYeuCau,
                loai,
                new TaiKhoan(nguoiGuiId),
                loaiDoiTuong,
                maDoiTuong,
                lyDo,
                trangThai,
                nguoiXuLyId == null ? null : new TaiKhoan(nguoiXuLyId),
                phanHoi,
                guiLuc,
                xuLyLuc,
                noiDungTruoc,
                noiDungSau,
                payload);
    }

    // ==================== PHƯƠNG THỨC TÍNH THUỘC TÍNH DẪN XUẤT ====================

    /** Kiểm tra yêu cầu phê duyệt đã được xử lý hay chưa (dẫn xuất). */
    public boolean kiemTraDaXuLy() {
        return xuLyLuc != null || (trangThai != null && !"CHO_DUYET".equalsIgnoreCase(trangThai));
    }

    // ==================== GETTER & SETTER CẦU NỐI ID ====================

    public Long getId() {
        return DuLieu.layIdCauNoiObj(this, this.maYeuCau);
    }

    public long layId() {
        return DuLieu.layIdCauNoi(this, this.maYeuCau);
    }

    public long id() {
        return layId();
    }

    public void setId(long id) {
        DuLieu.luuIdCauNoi(this, id);
        if ((this.maYeuCau == null || this.maYeuCau.isBlank()) && id > 0) {
            this.maYeuCau = DuLieu.taoMaTuId("YC", 8, id);
        }
    }

    public void datId(long id) {
        setId(id);
    }

    // ==================== GETTER & SETTER THUỘC TÍNH ====================

    public String getMaYeuCau() {
        return this.maYeuCau;
    }

    public String layMaYeuCau() {
        return this.maYeuCau;
    }

    public String maYeuCau() {
        return this.maYeuCau;
    }

    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau == null ? null : maYeuCau.trim();
    }

    public void datMaYeuCau(String maYeuCau) {
        setMaYeuCau(maYeuCau);
    }

    public String getLoai() {
        return this.loai;
    }

    public String layLoai() {
        return this.loai;
    }

    public String loai() {
        return this.loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public void datLoai(String loai) {
        setLoai(loai);
    }

    public TaiKhoan getNguoiGui() {
        return this.nguoiGui;
    }

    public void setNguoiGui(TaiKhoan nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public long getNguoiGuiId() {
        return nguoiGui == null ? 0L : nguoiGui.getId();
    }

    public long layNguoiGuiId() {
        return getNguoiGuiId();
    }

    public long nguoiGuiId() {
        return getNguoiGuiId();
    }

    public void setNguoiGuiId(long nguoiGuiId) {
        this.nguoiGui = new TaiKhoan(nguoiGuiId);
    }

    public void datNguoiGuiId(long nguoiGuiId) {
        setNguoiGuiId(nguoiGuiId);
    }

    public String getLoaiDoiTuong() {
        return this.loaiDoiTuong;
    }

    public String layLoaiDoiTuong() {
        return this.loaiDoiTuong;
    }

    public String loaiDoiTuong() {
        return this.loaiDoiTuong;
    }

    public void setLoaiDoiTuong(String loaiDoiTuong) {
        this.loaiDoiTuong = loaiDoiTuong;
    }

    public void datLoaiDoiTuong(String loaiDoiTuong) {
        setLoaiDoiTuong(loaiDoiTuong);
    }

    public String getMaDoiTuong() {
        return this.maDoiTuong;
    }

    public String layMaDoiTuong() {
        return this.maDoiTuong;
    }

    public String maDoiTuong() {
        return this.maDoiTuong;
    }

    public void setMaDoiTuong(String maDoiTuong) {
        this.maDoiTuong = maDoiTuong;
    }

    public void datMaDoiTuong(String maDoiTuong) {
        setMaDoiTuong(maDoiTuong);
    }

    public String getLyDo() {
        return this.lyDo;
    }

    public String layLyDo() {
        return this.lyDo;
    }

    public String lyDo() {
        return this.lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public void datLyDo(String lyDo) {
        setLyDo(lyDo);
    }

    public String getTrangThai() {
        return this.trangThai;
    }

    public String layTrangThai() {
        return this.trangThai;
    }

    public String trangThai() {
        return this.trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void datTrangThai(String trangThai) {
        setTrangThai(trangThai);
    }

    public TaiKhoan getNguoiXuLy() {
        return this.nguoiXuLy;
    }

    public void setNguoiXuLy(TaiKhoan nguoiXuLy) {
        this.nguoiXuLy = nguoiXuLy;
    }

    public Long getNguoiXuLyId() {
        return nguoiXuLy == null ? null : nguoiXuLy.getId();
    }

    public Long layNguoiXuLyId() {
        return getNguoiXuLyId();
    }

    public Long nguoiXuLyId() {
        return getNguoiXuLyId();
    }

    public void setNguoiXuLyId(Long nguoiXuLyId) {
        this.nguoiXuLy = nguoiXuLyId == null ? null : new TaiKhoan(nguoiXuLyId);
    }

    public void datNguoiXuLyId(Long nguoiXuLyId) {
        setNguoiXuLyId(nguoiXuLyId);
    }

    public String getPhanHoi() {
        return this.phanHoi;
    }

    public String layPhanHoi() {
        return this.phanHoi;
    }

    public String phanHoi() {
        return this.phanHoi;
    }

    public void setPhanHoi(String phanHoi) {
        this.phanHoi = phanHoi;
    }

    public void datPhanHoi(String phanHoi) {
        setPhanHoi(phanHoi);
    }

    public LocalDateTime getGuiLuc() {
        return this.guiLuc;
    }

    public LocalDateTime layGuiLuc() {
        return this.guiLuc;
    }

    public LocalDateTime guiLuc() {
        return this.guiLuc;
    }

    public void setGuiLuc(LocalDateTime guiLuc) {
        this.guiLuc = guiLuc;
    }

    public void datGuiLuc(LocalDateTime guiLuc) {
        setGuiLuc(guiLuc);
    }

    public LocalDateTime getXuLyLuc() {
        return this.xuLyLuc;
    }

    public LocalDateTime layXuLyLuc() {
        return this.xuLyLuc;
    }

    public LocalDateTime xuLyLuc() {
        return this.xuLyLuc;
    }

    public void setXuLyLuc(LocalDateTime xuLyLuc) {
        this.xuLyLuc = xuLyLuc;
    }

    public void datXuLyLuc(LocalDateTime xuLyLuc) {
        setXuLyLuc(xuLyLuc);
    }

    public String getNoiDungTruoc() {
        return this.noiDungTruoc;
    }

    public String layNoiDungTruoc() {
        return this.noiDungTruoc;
    }

    public String noiDungTruoc() {
        return this.noiDungTruoc;
    }

    public void setNoiDungTruoc(String noiDungTruoc) {
        this.noiDungTruoc = noiDungTruoc;
    }

    public void datNoiDungTruoc(String noiDungTruoc) {
        setNoiDungTruoc(noiDungTruoc);
    }

    public String getNoiDungSau() {
        return this.noiDungSau;
    }

    public String layNoiDungSau() {
        return this.noiDungSau;
    }

    public String noiDungSau() {
        return this.noiDungSau;
    }

    public void setNoiDungSau(String noiDungSau) {
        this.noiDungSau = noiDungSau;
    }

    public void datNoiDungSau(String noiDungSau) {
        setNoiDungSau(noiDungSau);
    }

    public String getPayload() {
        return this.payload;
    }

    public String layPayload() {
        return this.payload;
    }

    public String payload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void datPayload(String payload) {
        setPayload(payload);
    }

    // ==================== EQUALS & HASHCODE CHỈ THEO KHÓA CHÍNH ====================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        YeuCauPheDuyet that = (YeuCauPheDuyet) o;
        return Objects.equals(this.maYeuCau, that.maYeuCau);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maYeuCau);
    }

    @Override
    public String toString() {
        return "YeuCauPheDuyet{" + "maYeuCau='" + maYeuCau + '\'' + ", loai='" + loai + '\''
                + ", nguoiGuiId=" + getNguoiGuiId() + ", trangThai='" + trangThai + '\'' + '}';
    }
}
