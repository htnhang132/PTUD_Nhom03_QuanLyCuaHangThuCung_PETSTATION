package com.petstation.dao.chung.impl;

import static com.petstation.database.ThaoTacSQL.capNhat;
import static com.petstation.database.ThaoTacSQL.layGiaTriDon;
import static com.petstation.database.ThaoTacSQL.laySo;
import static com.petstation.database.ThaoTacSQL.truyVan;
import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dao.chung.DanhMucDAO;
import com.petstation.dao.hethong.impl.TaiKhoanDAOImpl;
import com.petstation.dao.kho.SanPhamDAO;
import com.petstation.dao.kho.impl.SanPhamDAOImpl;
import com.petstation.dto.DongNghiepVu;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.TrangThaiNhaCungCap;
import com.petstation.model.chung.TrangThaiPhongHotel;
import com.petstation.model.chung.TrangThaiSanPham;
import com.petstation.model.chung.TrangThaiTaiKhoan;
import com.petstation.model.chung.TrangThaiVoucher;
import com.petstation.model.chung.VaiTro;
import com.petstation.util.DinhDang;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Triển khai truy vấn SQL cho toàn bộ các màn hình Quản lý Danh mục
public final class DanhMucDAOImpl implements DanhMucDAO {
    private static final Logger LOG = LoggerFactory.getLogger(DanhMucDAOImpl.class);
    private static final Map<String, Set<String>> COT_HOP_LE =
            Map.ofEntries(
                    Map.entry(
                            "khach_hang",
                            Set.of(
                                    "ma_khach_hang",
                                    "ho_ten",
                                    "so_dien_thoai",
                                    "email",
                                    "dia_chi",
                                    "ghi_chu",
                                    "dang_hoat_dong")),
                    Map.entry(
                            "thu_cung",
                            Set.of(
                                    "ma_thu_cung",
                                    "ten",
                                    "loai",
                                    "giong",
                                    "gioi_tinh",
                                    "ngay_sinh",
                                    "can_nang",
                                    "mau_long",
                                    "ma_khach_hang",
                                    "tiem_chung",
                                    "di_ung_benh_ly",
                                    "tinh_cach",
                                    "anh",
                                    "dang_theo_doi")),
                    Map.entry(
                            "nhan_vien",
                            Set.of(
                                    "ma_nhan_vien",
                                    "ho_ten",
                                    "so_dien_thoai",
                                    "cccd",
                                    "chuc_vu",
                                    "ngay_vao_lam",
                                    "dang_lam_viec")),
                    Map.entry(
                            "tai_khoan",
                            Set.of(
                                    "ten_dang_nhap",
                                    "ma_nhan_vien",
                                    "mat_khau_hash",
                                    "vai_tro",
                                    "quyen",
                                    "trang_thai")),
                    Map.entry(
                            "san_pham",
                            Set.of(
                                    "ma_san_pham",
                                    "ten",
                                    "nhom_hang",
                                    "don_vi_tinh",
                                    "gia_ban",
                                    "gia_nhap_gan_nhat",
                                    "ton_toi_thieu",
                                    "theo_doi_hsd",
                                    "trang_thai",
                                    "barcode")),
                    Map.entry(
                            "nha_cung_cap",
                            Set.of(
                                    "ma_nha_cung_cap",
                                    "ten",
                                    "nguoi_dai_dien",
                                    "so_dien_thoai",
                                    "dia_chi",
                                    "ma_so_thue",
                                    "dieu_khoan",
                                    "trang_thai",
                                    "nhom_mat_hang",
                                    "ghi_chu")),
                    Map.entry(
                            "chuong_trinh_khuyen_mai",
                            Set.of(
                                    "ma_chuong_trinh",
                                    "ten",
                                    "loai_giam",
                                    "nguon",
                                    "gia_tri",
                                    "don_toi_thieu",
                                    "bat_dau",
                                    "ket_thuc",
                                    "trang_thai")),
                    Map.entry(
                            "voucher",
                            Set.of(
                                    "ma_voucher",
                                    "menh_gia",
                                    "hieu_luc_den",
                                    "so_luong",
                                    "don_toi_thieu",
                                    "trang_thai",
                                    "chi_ap_dung_spa",
                                    "ma_khach_hang")),
                    Map.entry(
                            "goi_dich_vu_spa",
                            Set.of(
                                    "ma_goi",
                                    "ten_goi",
                                    "mo_ta",
                                    "thoi_gian_du_kien",
                                    "bang_gia",
                                    "trang_thai")),
                    Map.entry(
                            "phong_hotel",
                            Set.of(
                                    "ma_phong",
                                    "loai_phong",
                                    "suc_chua",
                                    "gia_dem",
                                    "ghi_chu",
                                    "trang_thai")),
                    Map.entry("lo_hang", Set.of("ma_lo", "ma_san_pham", "so_lo")),
                    Map.entry("hoa_don", Set.of("ma_hoa_don", "trang_thai")),
                    Map.entry("phieu_tiep_nhan_spa", Set.of("ma_phieu", "trang_thai")),
                    Map.entry("hop_dong_luu_tru", Set.of("ma_hop_dong", "trang_thai")));

    private final SanPhamDAO sanPhamDAO = new SanPhamDAOImpl();

    private static void kiemTraBangVaCot(String bang, String khoa, String... cacCot) {
        Set<String> cotBang = COT_HOP_LE.get(bang);
        if (cotBang == null) {
            throw new IllegalArgumentException("Bảng danh mục không hợp lệ: " + bang);
        }
        if (khoa == null || !cotBang.contains(khoa)) {
            throw new IllegalArgumentException("Cột khóa không hợp lệ: " + bang + "." + khoa);
        }
        if (cacCot != null) {
            for (String cot : cacCot) {
                if (cot == null || !cotBang.contains(cot)) {
                    throw new IllegalArgumentException(
                            "Cột cập nhật không hợp lệ: " + bang + "." + cot);
                }
            }
        }
    }

    // 1. NẠP DỮ LIỆU DANH MỤC THEO LOẠI (nap)

    @Override
    public List<DongNghiepVu> nap(LoaiDanhMuc loai) {
        List<Object[]> cacDong = chuyenCacDongThanhChuoi(truyVan(laySqlNapDanhMuc(loai)));
        if (loai == LoaiDanhMuc.TAI_KHOAN)
            for (Object[] dongDuLieu : cacDong)
                dongDuLieu[2] = VaiTro.docTuCSDL(chuyenThanhChuoi(dongDuLieu[2])).getTenHienThi();
        if (loai == LoaiDanhMuc.DICH_VU_SPA) {
            for (Object[] dongDuLieu : cacDong) {
                dongDuLieu[4] = chuyenBangGiaSpaSangUI(chuyenThanhChuoi(dongDuLieu[4]));
            }
        }
        return cacDong.stream().map(DongNghiepVu::new).toList();
    }

    private static String laySqlNapDanhMuc(LoaiDanhMuc loai) {
        return switch (loai) {
            case KHACH_HANG -> laySqlNapDanhMucNhom1(loai);
            case THU_CUNG -> laySqlNapDanhMucNhom1(loai);
            case NHAN_VIEN -> laySqlNapDanhMucNhom1(loai);
            case TAI_KHOAN -> laySqlNapDanhMucNhom1(loai);
            case SAN_PHAM -> laySqlNapDanhMucNhom1(loai);
            case NHA_CUNG_CAP ->
                    """
                    SELECT ma_nha_cung_cap,
                           ten,
                           COALESCE(nguoi_dai_dien, N''),
                           so_dien_thoai,
                           COALESCE(dia_chi, N''),
                           COALESCE(ma_so_thue, ''),
                           dieu_khoan,
                           CASE trang_thai
                               WHEN 'DANG_HOP_TAC' THEN N'Đang hợp tác'
                               ELSE N'Ngừng hợp tác'
                           END,
                           COALESCE(nhom_mat_hang, N''),
                           COALESCE(ghi_chu, N'')
                    FROM nha_cung_cap
                    ORDER BY ma_nha_cung_cap
                    """;
            case KHUYEN_MAI ->
                    "SELECT ma_chuong_trinh,ten,CASE loai_giam WHEN 'PHAN_TRAM' THEN N'Giảm %' ELSE"
                        + " N'Giảm tiền' END,CASE nguon WHEN 'PRODUCT' THEN N'Sản phẩm' WHEN"
                        + " 'SERVICE' THEN N'Dịch vụ' ELSE N'Cả đơn'"
                        + " END,gia_tri,don_toi_thieu,CONVERT(varchar(10),bat_dau,103),CONVERT(varchar(10),DATEADD(second,-1,ket_thuc),103),CASE"
                        + " trang_thai WHEN 'NGUNG_HOAT_DONG' THEN N'Ngừng áp dụng' ELSE N'Đang áp"
                        + " dụng' END FROM chuong_trinh_khuyen_mai ORDER BY ma_chuong_trinh";
            case VOUCHER ->
                    "SELECT"
                        + " v.ma_voucher,v.menh_gia,CONVERT(varchar(10),DATEADD(second,-1,v.hieu_luc_den),103),v.so_luong,v.da_dung,v.don_toi_thieu,CASE"
                        + " v.trang_thai WHEN 'CON_HAN' THEN N'Còn hiệu lực' ELSE N'Ngừng'"
                        + " END,COALESCE(k.ma_khach_hang+' - '+k.ho_ten,''),CASE WHEN"
                        + " v.chi_ap_dung_spa=1 THEN N'Chỉ Spa' ELSE N'Mọi hóa đơn' END FROM"
                        + " voucher v LEFT JOIN khach_hang k ON k.ma_khach_hang=v.ma_khach_hang"
                        + " ORDER BY v.ma_voucher";
            case DICH_VU_SPA ->
                    "SELECT"
                            + " ma_goi,ten_goi,mo_ta,thoi_gian_du_kien,COALESCE(bang_gia,N''),CASE"
                            + " trang_thai WHEN 'HOAT_DONG' THEN N'Đang hoạt động' ELSE N'Ngừng"
                            + " hoạt động' END FROM goi_dich_vu_spa ORDER BY ma_goi";
            case PHONG_LUU_TRU ->
                    "SELECT ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,CASE trang_thai"
                            + " WHEN 'TRONG' THEN N'Trống' WHEN 'DANG_O' THEN N'Có thú'"
                            + " WHEN 'DANG_DON' THEN N'Đang dọn' WHEN 'BAO_TRI' THEN N'Bảo"
                            + " trì' ELSE N'Ngừng hoạt động' END FROM phong_hotel ORDER BY"
                            + " ma_phong";
        };
    }

    private static String laySqlNapDanhMucNhom1(LoaiDanhMuc loai) {
        return switch (loai) {
            case KHACH_HANG ->
                    "SELECT ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,CASE"
                            + " hang_thanh_vien WHEN 'REGULAR' THEN N'Đồng' ELSE"
                            + " hang_thanh_vien END,diem_tich_luy,CASE dang_hoat_dong WHEN 1"
                            + " THEN N'Đang hoạt động' ELSE N'Ngừng hoạt động' END FROM"
                            + " khach_hang ORDER BY ma_khach_hang";
            case THU_CUNG ->
                    "SELECT"
                        + " p.ma_thu_cung,p.ten,p.loai,p.giong,p.gioi_tinh,CONVERT(varchar(10),p.ngay_sinh,103),p.can_nang,p.mau_long,k.ma_khach_hang+'"
                        + " - '+k.ho_ten,p.tiem_chung,p.di_ung_benh_ly,p.tinh_cach,p.anh,CASE"
                        + " p.dang_theo_doi WHEN 1 THEN N'Đang hoạt động' ELSE N'Ngừng hoạt động'"
                        + " END FROM thu_cung p JOIN khach_hang k ON"
                        + " k.ma_khach_hang=p.ma_khach_hang ORDER BY p.ma_thu_cung";
            case NHAN_VIEN ->
                    "SELECT"
                        + " ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,CONVERT(varchar(10),ngay_vao_lam,103),CASE"
                        + " dang_lam_viec WHEN 1 THEN N'Đang làm' ELSE N'Nghỉ việc' END FROM"
                        + " nhan_vien ORDER BY ma_nhan_vien";
            case TAI_KHOAN ->
                    "SELECT t.ten_dang_nhap,n.ma_nhan_vien+' - '+n.ho_ten,t.vai_tro,CASE"
                            + " WHEN t.trang_thai = 'HOAT_DONG' THEN N'Hoạt động'"
                            + " ELSE N'Đã khóa' END,'' FROM tai_khoan t JOIN nhan_vien n ON"
                            + " n.ma_nhan_vien=t.ma_nhan_vien ORDER BY t.ten_dang_nhap";
            case SAN_PHAM ->
                    "SELECT"
                        + " p.ma_san_pham,p.ten,p.nhom_hang,p.don_vi_tinh,p.gia_ban,p.gia_nhap_gan_nhat,p.ton_toi_thieu,CASE"
                        + " p.theo_doi_hsd WHEN 1 THEN N'Có' ELSE N'Không' END,COALESCE((SELECT"
                        + " SUM(l.so_luong_ton) FROM lo_hang l WHERE"
                        + " l.ma_san_pham=p.ma_san_pham),0),CASE p.trang_thai WHEN 'DANG_BAN' THEN"
                        + " N'Đang kinh doanh' ELSE N'Ngừng kinh doanh' END,COALESCE(p.anh,''),"
                        + " COALESCE(p.thuong_hieu,''),COALESCE(p.mo_ta,''), COALESCE(p.barcode,'')"
                        + " FROM san_pham p ORDER BY CASE WHEN NULLIF(p.anh,'') IS NULL THEN 1 ELSE"
                        + " 0 END,p.ma_san_pham";
            default -> "";
        };
    }

    // 2. LƯU BẢN GHI DANH MỤC (THÊM MỚI HOẶC CẬP NHẬT THEO TỪNG LOẠI DANH MỤC)

    @Override
    public void luu(
            LoaiDanhMuc loai,
            DongNghiepVu truoc,
            DongNghiepVu giaTri,
            String nguoiThucHien,
            String nguoiThucHienVaiTro) {
        Object[] truoc2 = truoc == null ? null : truoc.thanhMang();
        Object[] giaTri2 = giaTri.thanhMang();
        String khoa = truoc2 == null ? chuyenThanhChuoi(giaTri2[0]) : chuyenThanhChuoi(truoc2[0]);
        switch (loai) {
            case KHACH_HANG -> luuKhachHang(khoa, giaTri, giaTri2);
            case THU_CUNG -> luuThuCung(khoa, giaTri2);
            case NHAN_VIEN -> luuNhanVien(khoa, giaTri2);
            case TAI_KHOAN ->
                    luuTaiKhoan(khoa, truoc, giaTri, giaTri2, nguoiThucHien, nguoiThucHienVaiTro);
            case SAN_PHAM -> luuSanPham(khoa, giaTri2, nguoiThucHien);
            case NHA_CUNG_CAP -> luuNhaCungCap(khoa, giaTri2);
            case KHUYEN_MAI -> luuKhuyenMai(khoa, giaTri2);
            case VOUCHER -> luuVoucher(khoa, giaTri2);
            case DICH_VU_SPA -> luuDichVuSpa(khoa, giaTri2);
            case PHONG_LUU_TRU -> luuPhongLuuTru(khoa, truoc, giaTri2);
        }
    }

    private void luuKhachHang(String khoa, DongNghiepVu giaTri, Object[] giaTri2) {
        var diem =
                truyVan(
                        "SELECT diem_tich_luy,hang_thanh_vien FROM khach_hang"
                                + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_khach_hang=?",
                        khoa);
        giaTri2[7] = diem.isEmpty() ? "0" : chuyenThanhChuoi(diem.get(0)[0]);
        giaTri2[6] = diem.isEmpty() ? "Đồng" : chuyenThanhChuoi(diem.get(0)[1]);
        if ("REGULAR".equals(giaTri2[6])) giaTri2[6] = "Đồng";
        giaTri.capNhatChuoi(7, String.valueOf(giaTri2[7]));
        giaTri.capNhatChuoi(6, String.valueOf(giaTri2[6]));
        layThemHoacCapNhat(
                "khach_hang",
                "ma_khach_hang",
                khoa,
                new String[] {
                    "ma_khach_hang",
                    "ho_ten",
                    "so_dien_thoai",
                    "email",
                    "dia_chi",
                    "ghi_chu",
                    "dang_hoat_dong"
                },
                giaTri2[0],
                giaTri2[1],
                giaTri2[2],
                giaTri2[3],
                giaTri2[4],
                giaTri2[5],
                "Đang hoạt động".equals(giaTri2[8]));
    }

    private void luuThuCung(String khoa, Object[] giaTri2) {
        layThemHoacCapNhat(
                "thu_cung",
                "ma_thu_cung",
                khoa,
                new String[] {
                    "ma_thu_cung",
                    "ten",
                    "loai",
                    "giong",
                    "gioi_tinh",
                    "ngay_sinh",
                    "can_nang",
                    "mau_long",
                    "ma_khach_hang",
                    "tiem_chung",
                    "di_ung_benh_ly",
                    "tinh_cach",
                    "anh",
                    "dang_theo_doi"
                },
                giaTri2[0],
                giaTri2[1],
                "Mèo".equals(giaTri2[2]) || "CAT".equalsIgnoreCase(String.valueOf(giaTri2[2]))
                        ? "Mèo"
                        : "Chó",
                giaTri2[3],
                giaTri2[4],
                docNgay(giaTri2[5]),
                docSoThapPhan(giaTri2[6]),
                giaTri2[7],
                layMaTuLuaChon(chuyenThanhChuoi(giaTri2[8])),
                giaTri2[9],
                giaTri2[10],
                giaTri2[11],
                giaTri2[12],
                "Đang hoạt động".equals(giaTri2[13]));
    }

    private void luuNhanVien(String khoa, Object[] giaTri2) {
        layThemHoacCapNhat(
                "nhan_vien",
                "ma_nhan_vien",
                khoa,
                new String[] {
                    "ma_nhan_vien",
                    "ho_ten",
                    "so_dien_thoai",
                    "cccd",
                    "chuc_vu",
                    "ngay_vao_lam",
                    "dang_lam_viec"
                },
                giaTri2[0],
                giaTri2[1],
                giaTri2[2],
                giaTri2[3],
                giaTri2[4],
                docNgay(giaTri2[5]),
                !"Nghỉ việc".equals(giaTri2[6]));
        if ("Nghỉ việc".equals(giaTri2[6])) khoaCacTaiKhoanCua(khoa);
    }

    private void luuTaiKhoan(
            String khoa,
            DongNghiepVu truoc,
            DongNghiepVu giaTri,
            Object[] giaTri2,
            String nguoiThucHien,
            String nguoiThucHienVaiTro) {
        String maNhanVien = layMaTuLuaChon(chuyenThanhChuoi(giaTri2[1]));
        if (truoc == null && (giaTri2.length < 5 || chuyenThanhChuoi(giaTri2[4]).length() < 8)) {
            throw new IllegalArgumentException("Tài khoản mới cần mật khẩu ít nhất 8 ký tự.");
        }
        if (truoc != null
                && nguoiThucHien != null
                && khoa.equals(nguoiThucHien)
                && (!"Hoạt động".equals(giaTri2[3])
                        || !nguoiThucHienVaiTro.equals(docVaiTro(giaTri2[2])))) {
            throw new IllegalArgumentException(
                    "Không thể tự khóa hoặc đổi vai trò tài khoản đang đăng nhập.");
        }
        String maVaiTro = docVaiTro(giaTri2[2]);
        String dsQuyen = TaiKhoanDAOImpl.macDinhTheoVaiTro(VaiTro.docTuCSDL(maVaiTro));
        String trangThaiDb =
                "Hoạt động".equals(giaTri2[3])
                        ? TrangThaiTaiKhoan.HOAT_DONG.maDB()
                        : TrangThaiTaiKhoan.NGUNG_HOAT_DONG.maDB();
        if (truoc == null) {
            capNhat(
                    "INSERT INTO"
                        + " tai_khoan(ma_nhan_vien,ten_dang_nhap,vai_tro,quyen,trang_thai,mat_khau_hash)"
                        + " VALUES(?,?,?,?,?,?)",
                    maNhanVien,
                    giaTri2[0],
                    maVaiTro,
                    dsQuyen,
                    trangThaiDb,
                    BCrypt.hashpw(chuyenThanhChuoi(giaTri2[4]), BCrypt.gensalt()));
        } else {
            String hienCoMaBam =
                    chuyenThanhChuoi(
                            layGiaTriDon(
                                    "SELECT mat_khau_hash FROM tai_khoan WHERE ten_dang_nhap=?",
                                    khoa));
            if ("Hoạt động".equals(giaTri2[3])
                    && !hienCoMaBam.startsWith("$2")
                    && (giaTri2.length < 5 || chuyenThanhChuoi(giaTri2[4]).isBlank())) {
                throw new IllegalArgumentException(
                        "Hãy đặt mật khẩu riêng trước khi mở khóa tài khoản mẫu.");
            }
            capNhat(
                    "UPDATE tai_khoan SET"
                            + " ten_dang_nhap=?,ma_nhan_vien=?,vai_tro=?,quyen=?,trang_thai=?"
                            + " WHERE ten_dang_nhap=?",
                    giaTri2[0],
                    maNhanVien,
                    maVaiTro,
                    dsQuyen,
                    trangThaiDb,
                    khoa);
            if (giaTri2.length > 4 && !chuyenThanhChuoi(giaTri2[4]).isBlank()) {
                datLaiMatKhau(chuyenThanhChuoi(giaTri2[0]), chuyenThanhChuoi(giaTri2[4]));
            }
        }
        if (giaTri2.length > 4) {
            giaTri2[4] = "";
            giaTri.capNhatChuoi(4, "");
        }
    }

    private void luuSanPham(String khoa, Object[] giaTri2, String nguoiThucHien) {
        BigDecimal giaBanMoi = docSoThapPhan(giaTri2[4]);
        if (giaBanMoi == null || giaBanMoi.signum() <= 0) {
            throw new IllegalArgumentException("Giá bán sản phẩm phải lớn hơn 0.");
        }
        BigDecimal giaNhapMoi = docSoThapPhan(giaTri2[5]);
        if (giaNhapMoi != null && giaNhapMoi.signum() <= 0) giaNhapMoi = null;
        layThemHoacCapNhat(
                "san_pham",
                "ma_san_pham",
                khoa,
                new String[] {
                    "ma_san_pham",
                    "ten",
                    "nhom_hang",
                    "don_vi_tinh",
                    "gia_ban",
                    "gia_nhap_gan_nhat",
                    "ton_toi_thieu",
                    "theo_doi_hsd",
                    "trang_thai"
                },
                giaTri2[0],
                giaTri2[1],
                giaTri2[2],
                giaTri2[3],
                giaBanMoi,
                giaNhapMoi,
                Integer.parseInt(chuyenThanhChuoi(giaTri2[6])),
                "Có".equals(giaTri2[7]),
                "Đang kinh doanh".equals(giaTri2[9])
                        ? TrangThaiSanPham.DANG_BAN.maDB()
                        : TrangThaiSanPham.NGUNG_BAN.maDB());
        if (giaTri2.length >= 13) {
            capNhat(
                    "UPDATE san_pham SET anh=?,thuong_hieu=?,mo_ta=?,barcode=? WHERE"
                            + " ma_san_pham=?",
                    giaTri2[10],
                    giaTri2[11],
                    giaTri2[12],
                    giaTri2.length > 13 ? giaTri2[13] : "",
                    chuyenThanhChuoi(giaTri2[0]));
        }
    }

    private void luuNhaCungCap(String khoa, Object[] giaTri2) {
        layThemHoacCapNhat(
                "nha_cung_cap",
                "ma_nha_cung_cap",
                khoa,
                new String[] {
                    "ma_nha_cung_cap",
                    "ten",
                    "nguoi_dai_dien",
                    "so_dien_thoai",
                    "dia_chi",
                    "ma_so_thue",
                    "dieu_khoan",
                    "trang_thai",
                    "nhom_mat_hang",
                    "ghi_chu"
                },
                giaTri2[0],
                giaTri2[1],
                giaTri2[2],
                giaTri2[3],
                giaTri2[4],
                giaTri2[5],
                giaTri2[6],
                "Đang hợp tác".equals(giaTri2[7])
                        ? TrangThaiNhaCungCap.DANG_HOP_TAC.maDB()
                        : TrangThaiNhaCungCap.NGUNG_HOP_TAC.maDB(),
                giaTri2[8],
                giaTri2[9]);
    }

    private void luuKhuyenMai(String khoa, Object[] giaTri2) {
        LocalDate ngayBatDau = docNgay(giaTri2[6]);
        LocalDate ngayKetThuc = docNgay(giaTri2[7]);
        if (ngayBatDau == null || ngayKetThuc == null) {
            throw new IllegalArgumentException(
                    "Ngày bắt đầu và ngày kết thúc không được để trống.");
        }
        BigDecimal donToiThieu = docSoThapPhan(giaTri2[5]);
        layThemHoacCapNhat(
                "chuong_trinh_khuyen_mai",
                "ma_chuong_trinh",
                khoa,
                new String[] {
                    "ma_chuong_trinh",
                    "ten",
                    "loai_giam",
                    "nguon",
                    "gia_tri",
                    "don_toi_thieu",
                    "bat_dau",
                    "ket_thuc",
                    "trang_thai"
                },
                giaTri2[0],
                giaTri2[1],
                "Giảm %".equals(giaTri2[2]) ? "PHAN_TRAM" : "SO_TIEN",
                "Sản phẩm".equals(giaTri2[3])
                        ? "PRODUCT"
                        : "Dịch vụ".equals(giaTri2[3]) ? "SERVICE" : "ALL",
                docSoThapPhan(giaTri2[4]),
                donToiThieu == null ? BigDecimal.ZERO : donToiThieu,
                ngayBatDau.atStartOfDay(),
                ngayKetThuc.plusDays(1).atStartOfDay(),
                "Ngừng áp dụng".equals(giaTri2[8])
                        ? TrangThaiTaiKhoan.NGUNG_HOAT_DONG.maDB()
                        : TrangThaiTaiKhoan.HOAT_DONG.maDB());
    }

    private void luuVoucher(String khoa, Object[] giaTri2) {
        LocalDate hieuLucDen = docNgay(giaTri2[2]);
        if (hieuLucDen == null) {
            throw new IllegalArgumentException(
                    "Ngày hiệu lực đến của voucher không được để trống.");
        }
        String khachStr = chuyenThanhChuoi(giaTri2[7]);
        layThemHoacCapNhat(
                "voucher",
                "ma_voucher",
                khoa,
                new String[] {
                    "ma_voucher",
                    "menh_gia",
                    "hieu_luc_den",
                    "so_luong",
                    "don_toi_thieu",
                    "trang_thai",
                    "chi_ap_dung_spa",
                    "ma_khach_hang"
                },
                giaTri2[0],
                docSoThapPhan(giaTri2[1]),
                hieuLucDen.plusDays(1).atStartOfDay(),
                Integer.parseInt(chuyenThanhChuoi(giaTri2[3])),
                docSoThapPhan(giaTri2[5]),
                "Ngừng".equals(giaTri2[6])
                        ? TrangThaiVoucher.HET_HAN.maDB()
                        : TrangThaiVoucher.CON_HAN.maDB(),
                "Chỉ Spa".equals(giaTri2[8]),
                khachStr.isBlank() ? null : khachStr.split(" - ", 2)[0]);
    }

    private void luuDichVuSpa(String khoa, Object[] giaTri2) {
        layThemHoacCapNhat(
                "goi_dich_vu_spa",
                "ma_goi",
                khoa,
                new String[] {
                    "ma_goi", "ten_goi", "mo_ta", "thoi_gian_du_kien", "bang_gia", "trang_thai"
                },
                giaTri2[0],
                giaTri2[1],
                giaTri2[2],
                Integer.parseInt(chuyenThanhChuoi(giaTri2[3])),
                chuyenBangGiaSpaSangCSDL(chuyenThanhChuoi(giaTri2[4])),
                "Đang hoạt động".equals(giaTri2[5])
                        ? TrangThaiTaiKhoan.HOAT_DONG.maDB()
                        : TrangThaiTaiKhoan.NGUNG_HOAT_DONG.maDB());
    }

    private void luuPhongLuuTru(String khoa, DongNghiepVu truoc, Object[] giaTri2) {
        String trangThai =
                maTrangThaiPhong(chuyenThanhChuoi(giaTri2[5]), "Trạng thái phòng không hợp lệ.");
        var hienTai =
                truyVan(
                        "SELECT trang_thai FROM phong_hotel WITH(UPDLOCK,HOLDLOCK) WHERE"
                                + " ma_phong=?",
                        khoa);
        Set<String> trangThaiVanHanh =
                Set.of(TrangThaiPhongHotel.DANG_O.maDB(), TrangThaiPhongHotel.DANG_DON.maDB());
        if (truoc == null) {
            if (!hienTai.isEmpty()) {
                throw new IllegalArgumentException("Mã phòng đã tồn tại. Hãy tải lại danh mục.");
            }
            if (trangThaiVanHanh.contains(trangThai)) {
                throw new IllegalStateException(
                        "Không tạo phòng mới ở trạng thái có thú/đang dọn.");
            }
        } else {
            if (hienTai.isEmpty()) {
                throw new IllegalArgumentException(
                        "Phòng không còn tồn tại. Hãy tải lại danh mục.");
            }
            String trongDB = chuyenThanhChuoi(hienTai.get(0)[0]);
            String khiMo = maTrangThaiPhong(truoc.layChuoi(5), "Dữ liệu phòng cũ không hợp lệ.");
            if (!trongDB.equals(khiMo)) {
                throw new IllegalStateException(
                        "Trạng thái phòng đã thay đổi. Hãy tải lại danh mục.");
            }
            if (!trongDB.equals(trangThai)
                    && (trangThaiVanHanh.contains(trongDB)
                            || trangThaiVanHanh.contains(trangThai))) {
                throw new IllegalStateException(
                        "Hãy đổi trạng thái có thú/đang dọn qua nghiệp vụ lưu trú.");
            }
        }
        layThemHoacCapNhat(
                "phong_hotel",
                "ma_phong",
                khoa,
                new String[] {
                    "ma_phong", "loai_phong", "suc_chua", "gia_dem", "ghi_chu", "trang_thai"
                },
                giaTri2[0],
                giaTri2[1],
                Integer.parseInt(chuyenThanhChuoi(giaTri2[2])),
                docSoThapPhan(giaTri2[3]),
                giaTri2[4],
                trangThai);
    }

    private static String maTrangThaiPhong(String nhan, String thongDiepLoi) {
        return switch (nhan) {
            case "Trống" -> "TRONG";
            case "Có thú" -> "DANG_O";
            case "Đang dọn" -> "DANG_DON";
            case "Bảo trì", "Ngừng hoạt động" -> "BAO_TRI";
            default -> throw new IllegalStateException(thongDiepLoi);
        };
    }

    // 3. CÁC HÀM TIỆN ÍCH CHUYỂN ĐỔI DỮ LIỆU, BẢNG GIÁ SPA & UPSERT (INSERT/UPDATE)

    public static String chuyenBangGiaSpaSangUI(String bangGiaCSDL) {
        if (bangGiaCSDL == null || bangGiaCSDL.isBlank()) return "";
        List<String> cacGia = new ArrayList<>();
        for (String muc : bangGiaCSDL.split(";")) {
            if (muc.isBlank()) continue;
            if (muc.contains("|")) {
                String[] p = muc.split("\\|", -1);
                if (p.length == 5) {
                    String maLoai = p[0].trim();
                    String loai =
                            ("DOG".equalsIgnoreCase(maLoai) || "Chó".equalsIgnoreCase(maLoai))
                                    ? "Chó"
                                    : "Mèo";
                    String gia = new BigDecimal(p[4].trim()).stripTrailingZeros().toPlainString();
                    cacGia.add(loai + "|" + p[1].trim() + "|" + gia);
                } else if (p.length == 3) {
                    cacGia.add(muc);
                } else if (p.length == 2) {
                    String canNang =
                            new BigDecimal(p[0].trim()).stripTrailingZeros().toPlainString();
                    String gia = new BigDecimal(p[1].trim()).stripTrailingZeros().toPlainString();
                    cacGia.add("Chó|<= " + canNang + "kg|" + gia);
                }
                continue;
            }
            String[] p = muc.split(":");
            if (p.length < 3) continue;
            String loai = "DOG".equalsIgnoreCase(p[0].trim()) ? "Chó" : "Mèo";
            String[] khoang = p[1].trim().split("-");
            String nhanKhoang = p[1].trim();
            if (khoang.length == 2) {
                double tu = Double.parseDouble(khoang[0].trim());
                double den = Double.parseDouble(khoang[1].trim());
                if (tu == 0) {
                    nhanKhoang = "< " + khoang[1].trim() + "kg";
                } else if (den >= 999) {
                    nhanKhoang = "> " + khoang[0].trim() + "kg";
                } else {
                    nhanKhoang = khoang[0].trim() + " - " + khoang[1].trim() + "kg";
                }
            }
            cacGia.add(loai + "|" + nhanKhoang + "|" + p[2].trim());
        }
        return String.join(";", cacGia);
    }

    public static String chuyenBangGiaSpaSangCSDL(String bangGiaUI) {
        if (bangGiaUI == null || bangGiaUI.isBlank()) return "";
        List<String> ketQua = new ArrayList<>();
        for (String dong : bangGiaUI.split(";")) {
            if (dong.isBlank()) continue;
            String[] vungDangXet = dong.split("\\|", -1);
            if (vungDangXet.length == 5) {
                ketQua.add(chuyenBangGiaSpaSangCSDL(chuyenBangGiaSpaSangUI(dong)));
                continue;
            }
            if (vungDangXet.length != 3)
                throw new IllegalArgumentException("Bảng giá không hợp lệ.");
            List<BigDecimal> cacCanNang = new ArrayList<>();
            Matcher boSoKhop = Pattern.compile("\\d+(?:[.,]\\d+)?").matcher(vungDangXet[1]);
            while (boSoKhop.find())
                cacCanNang.add(new BigDecimal(boSoKhop.group().replace(',', '.')));
            BigDecimal thap =
                    cacCanNang.size() > 1
                            ? cacCanNang.get(0)
                            : vungDangXet[1].contains(">") && !cacCanNang.isEmpty()
                                    ? cacCanNang.get(0)
                                    : BigDecimal.ZERO;
            BigDecimal cao =
                    cacCanNang.size() > 1
                            ? cacCanNang.get(1)
                            : vungDangXet[1].contains("<") && !cacCanNang.isEmpty()
                                    ? cacCanNang.get(0)
                                    : null;
            String tenLoai = vungDangXet[0].trim();
            String loai;
            if ("Chó".equalsIgnoreCase(tenLoai) || "DOG".equalsIgnoreCase(tenLoai)) loai = "DOG";
            else if ("Mèo".equalsIgnoreCase(tenLoai) || "CAT".equalsIgnoreCase(tenLoai))
                loai = "CAT";
            else throw new IllegalArgumentException("Loài trong bảng giá phải là Chó hoặc Mèo.");
            BigDecimal donGia = new BigDecimal(vungDangXet[2].trim());
            if (thap.signum() < 0
                    || (cao != null && cao.compareTo(thap) <= 0)
                    || donGia.signum() <= 0)
                throw new IllegalArgumentException("Khoảng cân nặng hoặc giá không hợp lệ.");
            ketQua.add(
                    loai
                            + "|"
                            + vungDangXet[1].trim()
                            + "|"
                            + thap.setScale(2, RoundingMode.HALF_UP).toPlainString()
                            + "|"
                            + (cao == null
                                    ? "NULL"
                                    : cao.setScale(2, RoundingMode.HALF_UP).toPlainString())
                            + "|"
                            + donGia.setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        return String.join(";", ketQua);
    }

    private static List<Object[]> chuyenCacDongThanhChuoi(List<Object[]> cacDong) {
        for (Object[] dongDuLieu : cacDong)
            for (int chiSo = 0; chiSo < dongDuLieu.length; chiSo++)
                dongDuLieu[chiSo] =
                        dongDuLieu[chiSo] instanceof BigDecimal giaTriSau
                                ? giaTriSau.stripTrailingZeros().toPlainString()
                                : chuyenThanhChuoi(dongDuLieu[chiSo]);
        return cacDong;
    }

    private static LocalDate docNgay(Object giaTri) {
        return chuyenThanhChuoi(giaTri).isBlank()
                ? null
                : LocalDate.parse(chuyenThanhChuoi(giaTri), DinhDang.NGAY_U);
    }

    private static BigDecimal docSoThapPhan(Object giaTri) {
        return chuyenThanhChuoi(giaTri).isBlank()
                ? null
                : new BigDecimal(chuyenThanhChuoi(giaTri).replace(',', '.'));
    }

    private static String docVaiTro(Object nhan) {
        return Arrays.stream(VaiTro.values())
                .filter(dongDuLieu -> dongDuLieu.getTenHienThi().equals(nhan))
                .findFirst()
                .orElseThrow()
                .name();
    }

    @Override
    public String layMaTuLuaChon(String luaChon) {
        return chuyenThanhChuoi(luaChon).split(" - ", 2)[0];
    }

    private static void layThemHoacCapNhat(
            String bang, String khoa, String banDau, String[] cacCot, Object... cacGiaTri) {
        kiemTraBangVaCot(bang, khoa, cacCot);
        if (layGiaTriDon(
                        "SELECT 1 FROM " + bang + " WITH(UPDLOCK,HOLDLOCK) WHERE " + khoa + "=?",
                        banDau)
                == null) {
            capNhat(
                    "INSERT INTO "
                            + bang
                            + "("
                            + String.join(",", cacCot)
                            + ") VALUES("
                            + String.join(",", Collections.nCopies(cacGiaTri.length, "?"))
                            + ")",
                    cacGiaTri);
        } else {
            List<Object> cacDoiSo = new ArrayList<>(Arrays.asList(cacGiaTri));
            cacDoiSo.add(banDau);
            capNhat(
                    "UPDATE "
                            + bang
                            + " SET "
                            + String.join(
                                    ",",
                                    Arrays.stream(cacCot)
                                            .map(giaTriDangXet -> giaTriDangXet + "=?")
                                            .toList())
                            + " WHERE "
                            + khoa
                            + "=?",
                    cacDoiSo.toArray());
        }
    }

    @Override
    public boolean tonTai(String bang, String khoa, String ma) {
        kiemTraBangVaCot(bang, khoa);
        return layGiaTriDon("SELECT 1 FROM " + bang + " WHERE " + khoa + "=?", ma) != null;
    }

    @Override
    public void xacNhanTonTai(String bang, String khoa, String ma) {
        if (!tonTai(bang, khoa, ma)) {
            throw new IllegalArgumentException("Không tìm thấy mã " + ma + ".");
        }
    }

    // 4. NGHIỆP VỤ BỔ TRỢ SẢN PHẨM

    @Override
    public int capNhatBarcode(String maSanPham, String barcode) {
        return sanPhamDAO.capNhatBarcode(maSanPham, barcode);
    }

    @Override
    public boolean kiemTraSanPhamCoGiaoDich(String maSanPham) {
        return sanPhamDAO.kiemTraSanPhamCoGiaoDich(maSanPham);
    }

    @Override
    public int ngungKinhDoanhSanPham(String maSanPham) {
        return sanPhamDAO.ngungKinhDoanhSanPham(maSanPham);
    }

    // 5. NGHIỆP VỤ BỔ TRỢ TÀI KHOẢN & NHÂN VIÊN

    public int khoaCacTaiKhoanCua(String maNhanVien) {
        return capNhat(
                "UPDATE tai_khoan SET trang_thai='NGUNG_HOAT_DONG' WHERE ma_nhan_vien=? AND"
                        + " trang_thai = 'HOAT_DONG'",
                maNhanVien);
    }

    public void datLaiMatKhau(String tenDangNhap, String matKhau) {
        String maBam;
        if (matKhau != null && matKhau.startsWith("$2") && matKhau.length() >= 59) {
            maBam = matKhau;
        } else {
            if (matKhau == null || matKhau.length() < 8)
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
            maBam = BCrypt.hashpw(matKhau, BCrypt.gensalt());
        }
        capNhat(
                "UPDATE tai_khoan SET"
                    + " mat_khau_hash=?,bat_buoc_doi_mat_khau=1,so_lan_dang_nhap_sai=0,khoa_den=NULL"
                    + " WHERE ten_dang_nhap=?",
                maBam,
                tenDangNhap);
    }

    @Override
    public String layTenDangNhapTheoNhanVien(String maNhanVien) {
        Object val =
                layGiaTriDon(
                        "SELECT TOP 1 ten_dang_nhap FROM tai_khoan WHERE ma_nhan_vien=?",
                        maNhanVien);
        return val == null ? null : val.toString();
    }

    @Override
    public void taoTaiKhoanNhanVien(
            String maNhanVien, String tenDangNhap, String matKhauHash, String vaiTro) {
        VaiTro vt = VaiTro.docTuCSDL(vaiTro);
        String dsQuyen = TaiKhoanDAOImpl.macDinhTheoVaiTro(vt);
        String maBam =
                (matKhauHash != null && matKhauHash.startsWith("$2") && matKhauHash.length() >= 59)
                        ? matKhauHash
                        : BCrypt.hashpw(String.valueOf(matKhauHash), BCrypt.gensalt());
        capNhat(
                "INSERT INTO tai_khoan(ma_nhan_vien, ten_dang_nhap, mat_khau_hash, vai_tro, quyen,"
                        + " trang_thai) VALUES(?, ?, ?, ?, ?, 'HOAT_DONG')",
                maNhanVien,
                tenDangNhap,
                maBam,
                vt.name(),
                dsQuyen);
    }

    // 6. NGHIỆP VỤ BỔ TRỢ NHÀ CUNG CẤP (KIỂM TRA TRÙNG TÊN / SĐT & ẢNH CHỤP JSON)

    @Override
    public boolean tonTaiTenNcc(String ten, String maLoaiTru) {
        return laySo(
                        """
                        SELECT COUNT(*)
                        FROM nha_cung_cap WITH (UPDLOCK, HOLDLOCK)
                        WHERE LOWER(LTRIM(RTRIM(ten))) = LOWER(?)
                          AND ma_nha_cung_cap <> ?
                        """,
                        ten.trim(),
                        maLoaiTru)
                > 0;
    }

    @Override
    public boolean tonTaiSdtNcc(String soDienThoai, String maLoaiTru) {
        return laySo(
                        """
                        SELECT COUNT(*)
                        FROM nha_cung_cap WITH (UPDLOCK, HOLDLOCK)
                        WHERE REPLACE(REPLACE(so_dien_thoai, ' ', ''), '.', '') = ?
                          AND ma_nha_cung_cap <> ?
                        """,
                        soDienThoai.replaceAll("[\\s.]", ""),
                        maLoaiTru)
                > 0;
    }

    @Override
    public String layAnhChupNcc(String maNcc) {
        Object json =
                layGiaTriDon(
                        """
                        SELECT ma_nha_cung_cap, ten, nguoi_dai_dien, so_dien_thoai,
                               dia_chi, ma_so_thue, dieu_khoan, trang_thai,
                               nhom_mat_hang, ghi_chu
                        FROM nha_cung_cap WITH (UPDLOCK, HOLDLOCK)
                        WHERE ma_nha_cung_cap = ?
                        FOR JSON PATH, INCLUDE_NULL_VALUES, WITHOUT_ARRAY_WRAPPER
                        """,
                        maNcc);
        return json == null ? null : String.valueOf(json);
    }

    @Override
    public RangBuocVoucher layRangBuocVoucher(String maVoucher) {
        List<Object[]> dong =
                truyVan(
                        "SELECT da_dung, khoa_doi_diem, ma_khach_hang FROM voucher WHERE"
                                + " ma_voucher=?",
                        maVoucher);
        if (dong.isEmpty()) return null;
        Object[] d = dong.get(0);
        return new RangBuocVoucher(
                d[0] instanceof Number n ? n.longValue() : 0L,
                d[1] == null ? null : d[1].toString(),
                d[2] == null ? null : d[2].toString());
    }

    @Override
    public Optional<Long> layGiaBanSanPham(String maSanPham) {
        List<Object[]> dong =
                truyVan(
                        "SELECT gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK) WHERE ma_san_pham=?",
                        maSanPham);
        if (dong.isEmpty()) return Optional.empty();
        Object gia = dong.get(0)[0];
        long giaBan =
                gia instanceof Number so
                        ? so.longValue()
                        : (long) Double.parseDouble(chuyenThanhChuoi(gia));
        return Optional.of(giaBan);
    }
}
