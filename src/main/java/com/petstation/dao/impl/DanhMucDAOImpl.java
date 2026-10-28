package com.petstation.dao.impl;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.DanhMucDAO;
import com.petstation.model.LoaiDanhMuc;
import com.petstation.model.VaiTro;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Thực hiện truy vấn JDBC cho JDBC danh mục DAO. */
public final class DanhMucDAOImpl implements DanhMucDAO {
    public List<Object[]> layLichSuGia() {
        return chuyenCacDongThanhChuoi(
                truyVan(
                        "SELECT CONVERT(varchar(16),h.thoi_diem_thay_doi,103)+'"
                            + " '+CONVERT(varchar(5),h.thoi_diem_thay_doi,108),p.ma_san_pham,p.ten,h.gia_cu,h.gia_moi,COALESCE(n.ho_ten,N'Hệ"
                            + " thống') FROM lich_su_gia h JOIN san_pham p ON p.id=h.san_pham_id"
                            + " LEFT JOIN tai_khoan t ON t.id=h.nguoi_thay_doi_id LEFT JOIN"
                            + " nhan_vien n ON n.id=t.nhan_vien_id ORDER BY h.id DESC"));
    }

    public List<Object[]> nap(LoaiDanhMuc loai) {
        // SQL.
        String sql =
                switch (loai) {
                    case CUSTOMERS ->
                            "SELECT ma_khach_hang,ho_ten,so_dien_thoai,email,dia_chi,ghi_chu,CASE"
                                + " hang_thanh_vien WHEN 'REGULAR' THEN N'Đồng' ELSE"
                                + " hang_thanh_vien END,diem_tich_luy,CASE dang_hoat_dong WHEN 1"
                                + " THEN N'Đang hoạt động' ELSE N'Ngừng hoạt động' END FROM"
                                + " khach_hang ORDER BY ma_khach_hang";
                    case PETS ->
                            "SELECT p.ma_thu_cung,p.ten,CASE p.loai WHEN 'DOG' THEN N'Chó' ELSE"
                                + " N'Mèo'"
                                + " END,p.giong,p.gioi_tinh,CONVERT(varchar(10),p.ngay_sinh,103),p.can_nang,p.mau_long,k.ma_khach_hang+'"
                                + " - '+k.ho_ten,p.tiem_chung,p.di_ung_benh_ly,p.tinh_cach,p.anh,CASE"
                                + " p.dang_theo_doi WHEN 1 THEN N'Đang hoạt động' ELSE N'Ngừng hoạt"
                                + " động' END FROM thu_cung p JOIN khach_hang k ON"
                                + " k.id=p.khach_hang_id ORDER BY p.ma_thu_cung";
                    case STAFF ->
                            "SELECT"
                                + " ma_nhan_vien,ho_ten,so_dien_thoai,cccd,chuc_vu,CONVERT(varchar(10),ngay_vao_lam,103),CASE"
                                + " dang_lam_viec WHEN 1 THEN N'Đang làm' ELSE N'Nghỉ việc' END"
                                + " FROM nhan_vien ORDER BY ma_nhan_vien";
                    case ACCOUNTS ->
                            "SELECT t.ten_dang_nhap,n.ma_nhan_vien+' - '+n.ho_ten,t.vai_tro,CASE"
                                + " t.trang_thai WHEN 'ACTIVE' THEN N'Hoạt động' ELSE N'Đã khóa'"
                                + " END,'' FROM tai_khoan t JOIN nhan_vien n ON n.id=t.nhan_vien_id"
                                + " ORDER BY t.ten_dang_nhap";
                    case PRODUCTS ->
                            "SELECT"
                                + " p.ma_san_pham,p.ten,p.nhom_hang,p.don_vi_tinh,p.gia_ban,p.gia_nhap_gan_nhat,p.ton_toi_thieu,CASE"
                                + " p.theo_doi_hsd WHEN 1 THEN N'Có' ELSE N'Không'"
                                + " END,COALESCE((SELECT SUM(l.so_luong_ton) FROM lo_hang l WHERE"
                                + " l.san_pham_id=p.id),0),CASE p.trang_thai WHEN 'SELLING' THEN"
                                + " N'Đang kinh doanh' ELSE N'Ngừng kinh doanh'"
                                + " END,COALESCE(p.anh,''),"
                                + " COALESCE(p.thuong_hieu,''),COALESCE(p.quy_cach,''),COALESCE(p.doi_tuong_su_dung,''),"
                                + " COALESCE(p.mo_ta,''),COALESCE(p.nguon_url,''),COALESCE(CONVERT(varchar(10),p.ngay_thu_thap,23),''),"
                                + " COALESCE(p.barcode,'') FROM san_pham p ORDER BY CASE WHEN"
                                + " NULLIF(p.anh,'') IS NULL THEN 1 ELSE 0 END,p.ma_san_pham";

                    case SUPPLIERS ->
                            "SELECT"
                                + " ma_ncc,ten,nguoi_dai_dien,so_dien_thoai,dia_chi,ma_so_thue,dieu_khoan,CASE"
                                + " trang_thai WHEN 'COOPERATING' THEN N'Đang hợp tác' ELSE N'Ngừng"
                                + " hợp tác' END FROM nha_cung_cap ORDER BY ma_ncc";
                    case PROMOTIONS ->
                            "SELECT ma_chuong_trinh,ten,CASE loai_giam WHEN 'PERCENT' THEN N'Giảm"
                                + " %' ELSE N'Giảm tiền' END,CASE nguon WHEN 'PRODUCT' THEN N'Sản"
                                + " phẩm' WHEN 'SERVICE' THEN N'Dịch vụ' ELSE N'Cả đơn'"
                                + " END,gia_tri,don_toi_thieu,CONVERT(varchar(10),bat_dau,103),CONVERT(varchar(10),DATEADD(second,-1,ket_thuc),103),CASE"
                                + " trang_thai WHEN 'INACTIVE' THEN N'Ngừng áp dụng' ELSE N'Đang áp"
                                + " dụng' END FROM chuong_trinh_khuyen_mai ORDER BY"
                                + " ma_chuong_trinh";
                    case VOUCHERS ->
                            "SELECT"
                                + " v.ma_voucher,v.menh_gia,CONVERT(varchar(10),DATEADD(second,-1,v.hieu_luc_den),103),v.so_luong,v.da_dung,v.don_toi_thieu,CASE"
                                + " v.trang_thai WHEN 'ACTIVE' THEN N'Còn hiệu lực' ELSE N'Ngừng'"
                                + " END,COALESCE(k.ma_khach_hang+' - '+k.ho_ten,''),CASE WHEN"
                                + " v.chi_ap_dung_spa=1 THEN N'Chỉ Spa' ELSE N'Mọi hóa đơn' END"
                                + " FROM voucher v LEFT JOIN khach_hang k ON k.id=v.khach_hang_id"
                                + " ORDER BY v.ma_voucher";
                    case SPA ->
                            "SELECT ma_goi,ten_goi,mo_ta,thoi_gian_du_kien,'',CASE trang_thai WHEN"
                                    + " 'ACTIVE' THEN N'Đang hoạt động' ELSE N'Ngừng hoạt động' END"
                                    + " FROM goi_dich_vu_spa ORDER BY ma_goi";
                    case ROOMS ->
                            "SELECT ma_phong,loai_phong,suc_chua,gia_dem,ghi_chu,CASE trang_thai"
                                + " WHEN 'AVAILABLE' THEN N'Trống' WHEN 'OCCUPIED' THEN N'Có thú'"
                                + " WHEN 'CLEANING' THEN N'Đang dọn' WHEN 'MAINTENANCE' THEN N'Bảo"
                                + " trì' ELSE N'Ngừng hoạt động' END FROM phong_hotel ORDER BY"
                                + " ma_phong";
                };
        List<Object[]> cacDong = chuyenCacDongThanhChuoi(truyVan(sql));
        if (loai == LoaiDanhMuc.ACCOUNTS)
            for (Object[] dongDuLieu : cacDong)
                dongDuLieu[2] = VaiTro.valueOf(chuyenThanhChuoi(dongDuLieu[2])).layTenHienThi();
        if (loai == LoaiDanhMuc.SPA) {
            for (Object[] dongDuLieu : cacDong) {
                // Các giá.
                List<String> cacGia = new ArrayList<>();
                for (Object[] vungDangXet :
                        truyVan(
                                "SELECT CASE b.loai_thu_cung WHEN 'DOG' THEN N'Chó' ELSE N'Mèo'"
                                    + " END,b.khoang_can,b.don_gia FROM bang_gia_spa b JOIN"
                                    + " goi_dich_vu_spa g ON g.id=b.goi_id WHERE g.ma_goi=? ORDER"
                                    + " BY b.id",
                                dongDuLieu[0]))
                    cacGia.add(
                            vungDangXet[0]
                                    + "|"
                                    + vungDangXet[1]
                                    + "|"
                                    + ((BigDecimal) vungDangXet[2]).toPlainString());
                dongDuLieu[4] = String.join(";", cacGia);
            }
        }
        return cacDong;
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
                : LocalDate.parse(
                        chuyenThanhChuoi(giaTri), DateTimeFormatter.ofPattern("dd/MM/uuuu"));
    }

    private static BigDecimal docSoThapPhan(Object giaTri) {
        return chuyenThanhChuoi(giaTri).isBlank()
                ? null
                : new BigDecimal(chuyenThanhChuoi(giaTri).replace(',', '.'));
    }

    private static String docVaiTro(Object nhan) {
        return Arrays.stream(VaiTro.values())
                .filter(dongDuLieu -> dongDuLieu.layTenHienThi().equals(nhan))
                .findFirst()
                .orElseThrow()
                .name();
    }

    public String layMaTuLuaChon(Object luaChon) {
        return chuyenThanhChuoi(luaChon).split(" - ", 2)[0];
    }

    private static void layThemHoacCapNhat(
            String bang, String khoa, String banDau, String[] cacCot, Object... cacGiaTri) {
        // Định danh chỉ lấy từ các hằng nội bộ đã chọn ở trên; giá trị luôn được truyền qua
        // tham số của câu lệnh.
        if (layGiaTriDon(
                        "SELECT id FROM " + bang + " WITH(UPDLOCK,HOLDLOCK) WHERE " + khoa + "=?",
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
            // Các đối số.
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

    public long layId(String bang, String khoa, String ma) {
        if (!Set.of(
                        "khach_hang",
                        "thu_cung",
                        "nhan_vien",
                        "tai_khoan",
                        "san_pham",
                        "nha_cung_cap",
                        "goi_dich_vu_spa",
                        "phong_hotel",
                        "lo_hang",
                        "hoa_don",
                        "phieu_tiep_nhan_spa",
                        "hop_dong_luu_tru")
                .contains(bang)) throw new IllegalArgumentException("Unknown table");
        Object giaTri = layGiaTriDon("SELECT id FROM " + bang + " WHERE " + khoa + "=?", ma);
        if (giaTri == null) throw new IllegalArgumentException("Không tìm thấy mã " + ma + ".");
        return ((Number) giaTri).longValue();
    }

    public int khoaCacTaiKhoanCua(String maNhanVien) {
        return capNhat(
                "UPDATE tai_khoan SET trang_thai='INACTIVE' WHERE nhan_vien_id=(SELECT id FROM"
                        + " nhan_vien WHERE ma_nhan_vien=?) AND trang_thai='ACTIVE'",
                maNhanVien);
    }

    public void datLaiMatKhau(String tenDangNhap, String matKhau) {
        if (matKhau.length() < 8)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        capNhat(
                "UPDATE tai_khoan SET"
                    + " mat_khau_hash=?,bat_buoc_doi_mat_khau=1,so_lan_dang_nhap_sai=0,khoa_den=NULL"
                    + " WHERE ten_dang_nhap=?",
                BCrypt.hashpw(matKhau, BCrypt.gensalt()),
                tenDangNhap);
    }

    public void luu(
            LoaiDanhMuc loai,
            Object[] truoc,
            Object[] giaTri,
            long idNguoiThucHien,
            String nguoiThucHienVaiTro) {
        // Khóa.
        String khoa = truoc == null ? chuyenThanhChuoi(giaTri[0]) : chuyenThanhChuoi(truoc[0]);
        Object[] giaTri2 = giaTri;
        switch (loai) {
            case CUSTOMERS -> {
                var diem =
                        truyVan(
                                "SELECT diem_tich_luy,hang_thanh_vien FROM khach_hang"
                                        + " WITH(UPDLOCK,HOLDLOCK) WHERE ma_khach_hang=?",
                                khoa);
                giaTri2[7] = diem.isEmpty() ? "0" : chuyenThanhChuoi(diem.get(0)[0]);
                giaTri2[6] = diem.isEmpty() ? "Đồng" : chuyenThanhChuoi(diem.get(0)[1]);
                if ("REGULAR".equals(giaTri2[6])) giaTri2[6] = "Đồng";
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
            case PETS ->
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
                                "khach_hang_id",
                                "tiem_chung",
                                "di_ung_benh_ly",
                                "tinh_cach",
                                "anh",
                                "dang_theo_doi"
                            },
                            giaTri2[0],
                            giaTri2[1],
                            "Chó".equals(giaTri2[2]) ? "DOG" : "CAT",
                            giaTri2[3],
                            giaTri2[4],
                            docNgay(giaTri2[5]),
                            docSoThapPhan(giaTri2[6]),
                            giaTri2[7],
                            layId("khach_hang", "ma_khach_hang", layMaTuLuaChon(giaTri2[8])),
                            giaTri2[9],
                            giaTri2[10],
                            giaTri2[11],
                            giaTri2[12],
                            "Đang hoạt động".equals(giaTri2[13]));
            case STAFF -> {
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
            case ACCOUNTS -> {
                // Nhân viên.
                long nhanVien = layId("nhan_vien", "ma_nhan_vien", layMaTuLuaChon(giaTri2[1]));
                if (truoc == null
                        && (giaTri2.length < 5 || chuyenThanhChuoi(giaTri2[4]).length() < 8))
                    throw new IllegalArgumentException(
                            "Tài khoản mới cần mật khẩu ít nhất 8 ký tự.");
                if (truoc != null
                        && laySo("SELECT id FROM tai_khoan WHERE ten_dang_nhap=?", khoa)
                                == idNguoiThucHien
                        && (!"Hoạt động".equals(giaTri2[3])
                                || !nguoiThucHienVaiTro.equals(docVaiTro(giaTri2[2]))))
                    throw new IllegalArgumentException(
                            "Không thể tự khóa hoặc đổi vai trò tài khoản đang đăng" + " nhập.");
                if (truoc == null)
                    them(
                            "INSERT INTO"
                                + " tai_khoan(ten_dang_nhap,nhan_vien_id,vai_tro,trang_thai,mat_khau_hash)"
                                + " VALUES(?,?,?,?,?)",
                            giaTri2[0],
                            nhanVien,
                            docVaiTro(giaTri2[2]),
                            "Hoạt động".equals(giaTri2[3]) ? "ACTIVE" : "INACTIVE",
                            BCrypt.hashpw(chuyenThanhChuoi(giaTri2[4]), BCrypt.gensalt()));
                else {
                    // Hiện có mã băm.
                    String hienCoMaBam =
                            chuyenThanhChuoi(
                                    layGiaTriDon(
                                            "SELECT mat_khau_hash FROM tai_khoan WHERE"
                                                    + " ten_dang_nhap=?",
                                            khoa));
                    if ("Hoạt động".equals(giaTri2[3])
                            && !hienCoMaBam.startsWith("$2")
                            && (giaTri2.length < 5 || chuyenThanhChuoi(giaTri2[4]).isBlank()))
                        throw new IllegalArgumentException(
                                "Hãy đặt mật khẩu riêng trước khi mở khóa tài khoản" + " mẫu.");
                    capNhat(
                            "UPDATE tai_khoan SET"
                                    + " ten_dang_nhap=?,nhan_vien_id=?,vai_tro=?,trang_thai=?"
                                    + " WHERE ten_dang_nhap=?",
                            giaTri2[0],
                            nhanVien,
                            docVaiTro(giaTri2[2]),
                            "Hoạt động".equals(giaTri2[3]) ? "ACTIVE" : "INACTIVE",
                            khoa);
                    if (giaTri2.length > 4 && !chuyenThanhChuoi(giaTri2[4]).isBlank())
                        datLaiMatKhau(chuyenThanhChuoi(giaTri2[0]), chuyenThanhChuoi(giaTri2[4]));
                }
                if (giaTri2.length > 4) giaTri2[4] = "";
            }
            case PRODUCTS -> {
                // Cũ.
                Object cu =
                        layGiaTriDon(
                                "SELECT gia_ban FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                                        + " WHERE ma_san_pham=?",
                                khoa);
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
                        docSoThapPhan(giaTri2[4]),
                        docSoThapPhan(giaTri2[5]),
                        Integer.parseInt(chuyenThanhChuoi(giaTri2[6])),
                        "Có".equals(giaTri2[7]),
                        "Đang kinh doanh".equals(giaTri2[9]) ? "SELLING" : "STOPPED");
                if (giaTri2.length >= 17) {
                    capNhat(
                            "UPDATE san_pham SET"
                                + " anh=?,thuong_hieu=?,quy_cach=?,doi_tuong_su_dung=?,mo_ta=?,barcode=?"
                                + " WHERE ma_san_pham=?",
                            giaTri2[10],
                            giaTri2[11],
                            giaTri2[12],
                            giaTri2[13],
                            giaTri2[14],
                            giaTri2.length > 17 ? giaTri2[17] : "",
                            khoa);
                }

                if (cu == null || ((BigDecimal) cu).compareTo(docSoThapPhan(giaTri2[4])) != 0)
                    capNhat(
                            "INSERT INTO"
                                    + " lich_su_gia(san_pham_id,gia_cu,gia_moi,nguoi_thay_doi_id)"
                                    + " VALUES(?,?,?,?)",
                            layId("san_pham", "ma_san_pham", chuyenThanhChuoi(giaTri2[0])),
                            cu == null ? BigDecimal.ZERO : cu,
                            docSoThapPhan(giaTri2[4]),
                            idNguoiThucHien);
            }
            case SUPPLIERS ->
                    layThemHoacCapNhat(
                            "nha_cung_cap",
                            "ma_ncc",
                            khoa,
                            new String[] {
                                "ma_ncc",
                                "ten",
                                "nguoi_dai_dien",
                                "so_dien_thoai",
                                "dia_chi",
                                "ma_so_thue",
                                "dieu_khoan",
                                "trang_thai"
                            },
                            giaTri2[0],
                            giaTri2[1],
                            giaTri2[2],
                            giaTri2[3],
                            giaTri2[4],
                            giaTri2[5],
                            giaTri2[6],
                            "Đang hợp tác".equals(giaTri2[7]) ? "COOPERATING" : "INACTIVE");
            case PROMOTIONS ->
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
                            "Giảm %".equals(giaTri2[2]) ? "PERCENT" : "AMOUNT",
                            "Sản phẩm".equals(giaTri2[3])
                                    ? "PRODUCT"
                                    : "Dịch vụ".equals(giaTri2[3]) ? "SERVICE" : "ALL",
                            docSoThapPhan(giaTri2[4]),
                            docSoThapPhan(giaTri2[5]) == null
                                    ? BigDecimal.ZERO
                                    : docSoThapPhan(giaTri2[5]),
                            docNgay(giaTri2[6]).atStartOfDay(),
                            docNgay(giaTri2[7]).plusDays(1).atStartOfDay(),
                            "Ngừng áp dụng".equals(giaTri2[8]) ? "INACTIVE" : "ACTIVE");
            case VOUCHERS ->
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
                                "khach_hang_id"
                            },
                            giaTri2[0],
                            docSoThapPhan(giaTri2[1]),
                            docNgay(giaTri2[2]).plusDays(1).atStartOfDay(),
                            Integer.parseInt(chuyenThanhChuoi(giaTri2[3])),
                            docSoThapPhan(giaTri2[5]),
                            "Ngừng".equals(giaTri2[6]) ? "INACTIVE" : "ACTIVE",
                            "Chỉ Spa".equals(giaTri2[8]),
                            chuyenThanhChuoi(giaTri2[7]).isBlank()
                                    ? null
                                    : layId(
                                            "khach_hang",
                                            "ma_khach_hang",
                                            chuyenThanhChuoi(giaTri2[7]).split(" - ", 2)[0]));
            case SPA -> {
                layThemHoacCapNhat(
                        "goi_dich_vu_spa",
                        "ma_goi",
                        khoa,
                        new String[] {
                            "ma_goi", "ten_goi", "mo_ta", "thoi_gian_du_kien", "trang_thai"
                        },
                        giaTri2[0],
                        giaTri2[1],
                        giaTri2[2],
                        Integer.parseInt(chuyenThanhChuoi(giaTri2[3])),
                        "Đang hoạt động".equals(giaTri2[5]) ? "ACTIVE" : "INACTIVE");
                // Id gói dịch vụ.
                long idGoiDichVu = layId("goi_dich_vu_spa", "ma_goi", chuyenThanhChuoi(giaTri2[0]));
                capNhat("DELETE FROM bang_gia_spa WHERE goi_id=?", idGoiDichVu);
                for (String dong : chuyenThanhChuoi(giaTri2[4]).split(";")) {
                    String[] vungDangXet = dong.split("\\|");
                    if (vungDangXet.length != 3)
                        throw new IllegalArgumentException("Bảng giá không hợp lệ.");
                    // Các cân nặng.
                    List<BigDecimal> cacCanNang = new ArrayList<>();
                    // Bộ so khớp.
                    java.util.regex.Matcher boSoKhop =
                            java.util.regex.Pattern.compile("\\d+(?:[.,]\\d+)?")
                                    .matcher(vungDangXet[1]);
                    while (boSoKhop.find())
                        cacCanNang.add(new BigDecimal(boSoKhop.group().replace(',', '.')));
                    // Thấp.
                    BigDecimal thap =
                            cacCanNang.size() > 1
                                    ? cacCanNang.get(0)
                                    : vungDangXet[1].contains(">") && !cacCanNang.isEmpty()
                                            ? cacCanNang.get(0)
                                            : BigDecimal.ZERO;
                    // Cao.
                    BigDecimal cao =
                            cacCanNang.size() > 1
                                    ? cacCanNang.get(1)
                                    : vungDangXet[1].contains("<") && !cacCanNang.isEmpty()
                                            ? cacCanNang.get(0)
                                            : null;
                    capNhat(
                            "INSERT INTO"
                                + " bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)"
                                + " VALUES(?,?,?,?,?,?)",
                            idGoiDichVu,
                            "Chó".equals(vungDangXet[0]) ? "DOG" : "CAT",
                            vungDangXet[1],
                            thap,
                            cao,
                            new BigDecimal(vungDangXet[2]));
                }
            }
            case ROOMS -> {
                // Trạng thái.
                String trangThai =
                        switch (chuyenThanhChuoi(giaTri2[5])) {
                            case "Trống" -> "AVAILABLE";
                            case "Có thú" -> "OCCUPIED";
                            case "Đang dọn" -> "CLEANING";
                            case "Bảo trì" -> "MAINTENANCE";
                            default -> "INACTIVE";
                        };
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
        }
    }

    public int capNhatBarcode(long sanPhamId, String barcode) {
        return capNhat("UPDATE san_pham SET barcode=? WHERE id=?", barcode, sanPhamId);
    }

    public boolean kiemTraSanPhamCoGiaoDich(long sanPhamId) {
        return laySo(
                        "SELECT COUNT(*) FROM chi_tiet_don_hang c JOIN lo_hang l ON l.id ="
                                + " c.lo_hang_id WHERE l.san_pham_id = ?",
                        sanPhamId)
                > 0;
    }

    public int ngungKinhDoanhSanPham(long sanPhamId) {
        return capNhat("UPDATE san_pham SET trang_thai='STOPPED' WHERE id=?", sanPhamId);
    }

    public String layTenDangNhapTheoNhanVien(long nhanVienId) {
        Object val =
                layGiaTriDon(
                        "SELECT ten_dang_nhap FROM tai_khoan WHERE nhan_vien_id=?", nhanVienId);
        return val == null ? null : val.toString();
    }

    public void taoTaiKhoanNhanVien(
            long nhanVienId, String tenDangNhap, String matKhauHash, String vaiTro) {
        capNhat(
                "INSERT INTO tai_khoan(ten_dang_nhap, mat_khau_hash, vai_tro, nhan_vien_id,"
                        + " trang_thai) VALUES(?, ?, ?, ?, 'ACTIVE')",
                tenDangNhap,
                matKhauHash,
                vaiTro,
                nhanVienId);
    }
}
