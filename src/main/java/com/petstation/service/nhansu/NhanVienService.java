package com.petstation.service.nhansu;

import com.petstation.dao.nhansu.NhanVienDAO;
import com.petstation.dao.nhansu.impl.NhanVienDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.nhansu.NhanVien;
import com.petstation.service.chung.DanhMucService;
import com.petstation.service.chung.DanhMucService.CacDong;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class NhanVienService {
    private static final NhanVienService MAC_DINH = new NhanVienService();

    public static NhanVienService macDinh() {
        return MAC_DINH;
    }

    private final NhanVienDAO nhanVienDAO;
    private final DanhMucService danhMucService;

    public NhanVienService() {
        this(new NhanVienDAOImpl(), DanhMucService.macDinh());
    }

    public NhanVienService(NhanVienDAO nhanVienDAO) {
        this(nhanVienDAO, DanhMucService.macDinh());
    }

    public NhanVienService(NhanVienDAO nhanVienDAO, DanhMucService danhMucService) {
        this.nhanVienDAO = Objects.requireNonNull(nhanVienDAO, "nhanVienDAO không được null");
        this.danhMucService =
                Objects.requireNonNull(danhMucService, "danhMucService không được null");
    }

    public CacDong chuanBi(boolean docNgay) {
        return danhMucService.chuanBi(LoaiDanhMuc.NHAN_VIEN, docNgay);
    }

    public List<String> layLuaChonNhanVien() {
        return danhMucService.layLuaChonNhanVien();
    }

    public Optional<NhanVien> timTheoMa(String maNhanVien) {
        return QuanLyGiaoDich.macDinh()
                .thucThi(ketNoi -> nhanVienDAO.timTheoMa(ketNoi, maNhanVien));
    }

    public boolean tonTai(String maNhanVien) {
        return danhMucService.tonTai("nhan_vien", "ma_nhan_vien", maNhanVien);
    }

    public String layTenDangNhapTheoNhanVien(String maNhanVien) {
        return danhMucService.layTenDangNhapTheoNhanVien(maNhanVien);
    }

    public void capNhatHoacTaoTaiKhoan(
            String maNhanVien, String tenDangNhap, String matKhauHash, String vaiTro) {
        danhMucService.capNhatHoacTaoTaiKhoan(maNhanVien, tenDangNhap, matKhauHash, vaiTro);
    }

    public static String kiemTraNhanVien(String hoTen, String sdt, String cccd, String ngayVaoLam) {
        return DanhMucService.kiemTraNhanVien(hoTen, sdt, cccd, ngayVaoLam);
    }

    public static String suyRaVaiTroTuChucVu(String chucVu) {
        return DanhMucService.suyRaVaiTroTuChucVu(chucVu);
    }
}
