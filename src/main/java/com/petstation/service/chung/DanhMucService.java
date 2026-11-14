package com.petstation.service.chung;

import static com.petstation.util.DuLieu.chuyenThanhChuoi;

import com.petstation.dao.chung.DanhMucDAO;
import com.petstation.dao.chung.DanhMucDAO.RangBuocVoucher;
import com.petstation.dao.chung.impl.DanhMucDAOImpl;
import com.petstation.database.QuanLyGiaoDich;
import com.petstation.dto.DongNghiepVu;
import com.petstation.model.chung.LoaiDanhMuc;
import com.petstation.model.chung.Quyen;
import com.petstation.model.chung.VaiTro;
import com.petstation.service.hethong.NhatKyHeThongService;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.util.DinhDang;
import com.petstation.util.TaiAnhSanPham;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

// Tầng dịch vụ Quản lý Danh mục dùng chung (Service)
public final class DanhMucService {
    private static final Logger LOG = LoggerFactory.getLogger(DanhMucService.class);
    private static final DanhMucService MAC_DINH = new DanhMucService();

    public static DanhMucService macDinh() {
        return MAC_DINH;
    }

    private final DanhMucDAO danhMucDAO;
    private final PhienDangNhapService phienDangNhapService;
    private final NhatKyHeThongService nhatKyHeThongService;

    public DanhMucService() {
        this(new DanhMucDAOImpl(), PhienDangNhapService.macDinh(), NhatKyHeThongService.macDinh());
    }

    public DanhMucService(DanhMucDAO danhMucDAO) {
        this(danhMucDAO, PhienDangNhapService.macDinh(), NhatKyHeThongService.macDinh());
    }

    public DanhMucService(
            DanhMucDAO danhMucDAO,
            PhienDangNhapService phienDangNhapService,
            NhatKyHeThongService nhatKyHeThongService) {
        this.danhMucDAO = Objects.requireNonNull(danhMucDAO);
        this.phienDangNhapService = Objects.requireNonNull(phienDangNhapService);
        this.nhatKyHeThongService = Objects.requireNonNull(nhatKyHeThongService);
    }

    // 1. MÔ HÌNH DANH SÁCH DÒNG DỮ LIỆU ĐỒNG BỘ VỚI CSDL (CacDong)

    public final class CacDong extends ArrayList<Object[]> {
        public final LoaiDanhMuc loai;

        CacDong(LoaiDanhMuc loai) {
            this(loai, true);
        }

        CacDong(LoaiDanhMuc loai, boolean docNgay) {
            this.loai = loai;
            if (docNgay) lamMoi();
        }

        public List<Object[]> taiBanSao() {
            return danhMucDAO.nap(loai).stream().map(DongNghiepVu::thanhMang).toList();
        }

        public void lamMoi() {
            super.clear();
            super.addAll(taiBanSao());
        }

        public LoaiDanhMuc layLoai() {
            return loai;
        }
    }

    public static final class TrungSoDienThoaiException extends IllegalArgumentException {
        public TrungSoDienThoaiException(String thongBao) {
            super(thongBao);
        }
    }

    // 2. NẠP DỮ LIỆU CÁC LOẠI DANH MỤC & DANH SÁCH LỰA CHỌN COMBOBOX

    public CacDong chuanBi(LoaiDanhMuc loai, boolean docNgay) {
        return new CacDong(loai, docNgay);
    }

    public CacDong layKhachHang() {
        return new CacDong(LoaiDanhMuc.KHACH_HANG);
    }

    public CacDong layThuCung() {
        return new CacDong(LoaiDanhMuc.THU_CUNG);
    }

    public CacDong layNhanVien() {
        return new CacDong(LoaiDanhMuc.NHAN_VIEN);
    }

    public CacDong layTaiKhoan() {
        return new CacDong(LoaiDanhMuc.TAI_KHOAN);
    }

    public CacDong laySanPham() {
        return new CacDong(LoaiDanhMuc.SAN_PHAM);
    }

    public CacDong layNhaCungCap() {
        return new CacDong(LoaiDanhMuc.NHA_CUNG_CAP);
    }

    public CacDong layKhuyenMai() {
        return new CacDong(LoaiDanhMuc.KHUYEN_MAI);
    }

    public CacDong layVoucher() {
        return new CacDong(LoaiDanhMuc.VOUCHER);
    }

    public CacDong layDichVuSpa() {
        return new CacDong(LoaiDanhMuc.DICH_VU_SPA);
    }

    public CacDong layPhong() {
        return new CacDong(LoaiDanhMuc.PHONG_LUU_TRU);
    }

    public List<String> layLuaChonKhachHang() {
        return layKhachHang().stream()
                .filter(dongDuLieu -> "Đang hoạt động".equals(dongDuLieu[8]))
                .map(dongDuLieu -> dongDuLieu[0] + " - " + dongDuLieu[1])
                .toList();
    }

    public List<String> layLuaChonNhanVien() {
        return layNhanVien().stream()
                .filter(dongDuLieu -> !"Nghỉ việc".equals(dongDuLieu[6]))
                .map(dongDuLieu -> dongDuLieu[0] + " - " + dongDuLieu[1])
                .toList();
    }

    public static String layMaTuLuaChon(Object luaChon) {
        return chuyenThanhChuoi(luaChon).split(" - ", 2)[0];
    }

    public boolean tonTai(String bang, String khoa, String ma) {
        return danhMucDAO.tonTai(bang, khoa, ma);
    }

    public void xacNhanTonTai(String bang, String khoa, String ma) {
        danhMucDAO.xacNhanTonTai(bang, khoa, ma);
    }

    // 3. TIỆN ÍCH HỖ TRỢ PHÁT HÀNH VOUCHER CHO NHIỀU KHÁCH HÀNG

    public static List<String> tachDanhSachKhach(Object giaTri) {
        String chuoi = chuyenThanhChuoi(giaTri);
        if (chuoi.isBlank()) return List.of();
        List<String> ketQua = new ArrayList<>();
        for (String phan : chuoi.split(";")) {
            String cat = phan.trim();
            if (!cat.isEmpty() && !ketQua.contains(cat)) {
                ketQua.add(cat);
            }
        }
        return ketQua;
    }

    public static String taoMaVoucherTheoKhach(String maGoc, String maKhach) {
        String goc = chuyenThanhChuoi(maGoc).trim().replaceFirst("(?i)-KH\\d+$", "");
        String khach = chuyenThanhChuoi(maKhach).trim();
        if (khach.isEmpty()) return goc;
        String hauTo = "-" + khach;
        if (goc.toUpperCase(Locale.ROOT).endsWith(hauTo.toUpperCase(Locale.ROOT))) {
            return goc;
        }
        int toiDaGoc = Math.max(1, 40 - hauTo.length());
        if (goc.length() > toiDaGoc) {
            goc = goc.substring(0, toiDaGoc);
        }
        return goc + hauTo;
    }

    // 4. LƯU BẢN GHI DANH MỤC (THÊM MỚI / CẬP NHẬT) & KIỂM TRA RÀNG BUỘC

    public void luuNhieu(
            List<Object[]> cacDong,
            Object[] truoc,
            Object[] giaTriCapNhat,
            List<Object[]> cacGiaTriMoi) {
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            if (giaTriCapNhat != null) {
                                luu(cacDong, truoc, giaTriCapNhat, false);
                            }
                            if (cacGiaTriMoi != null) {
                                for (Object[] dongMoi : cacGiaTriMoi) {
                                    luu(cacDong, null, dongMoi, false);
                                }
                            }
                            return null;
                        });
    }

    public void luu(List<Object[]> cacDong, Object[] truoc, Object[] giaTri) {
        luu(cacDong, truoc, giaTri, false);
    }

    public void luu(
            List<Object[]> cacDong, Object[] truoc, Object[] giaTri, boolean xacNhanTrungSdt) {
        if (!(cacDong instanceof CacDong danhMuc))
            throw new IllegalArgumentException("Danh mục chưa nối SQL Server.");
        phienDangNhapService.yeuCau(quyenTheoLoai(danhMuc.loai));
        QuanLyGiaoDich.macDinh()
                .thucThi(
                        ketNoi -> {
                            kiemTraRangBuocKhiLuu(danhMuc.loai, truoc, giaTri, xacNhanTrungSdt);
                            boolean laNcc = danhMuc.loai == LoaiDanhMuc.NHA_CUNG_CAP;
                            DongNghiepVu giaTriDto = new DongNghiepVu(giaTri);
                            danhMucDAO.luu(
                                    danhMuc.loai,
                                    truoc == null ? null : new DongNghiepVu(truoc),
                                    giaTriDto,
                                    phienDangNhapService.layTenDangNhap(),
                                    phienDangNhapService.layPhien().docVaiTro().name());
                            Object[] sauKhiLuu = giaTriDto.thanhMang();
                            System.arraycopy(
                                    sauKhiLuu,
                                    0,
                                    giaTri,
                                    0,
                                    Math.min(giaTri.length, sauKhiLuu.length));
                            ghiNhatKyLuuDanhMuc(ketNoi, danhMuc.loai, truoc, giaTri, laNcc);
                            return null;
                        });
    }

    private static Quyen quyenTheoLoai(LoaiDanhMuc loai) {
        return switch (loai) {
            case KHACH_HANG -> Quyen.QUAN_LY_KHACH_HANG;
            case THU_CUNG -> Quyen.QUAN_LY_THU_CUNG;
            case NHAN_VIEN, TAI_KHOAN -> Quyen.QUAN_LY_TAI_KHOAN;
            case SAN_PHAM -> Quyen.QUAN_LY_SAN_PHAM;
            case NHA_CUNG_CAP -> Quyen.QUAN_LY_NHA_CUNG_CAP;
            case KHUYEN_MAI, VOUCHER -> Quyen.QUAN_LY_KHUYEN_MAI;
            case DICH_VU_SPA -> Quyen.QUAN_LY_SPA;
            case PHONG_LUU_TRU -> Quyen.QUAN_LY_LUU_TRU;
        };
    }

    private void kiemTraRangBuocKhiLuu(
            LoaiDanhMuc loai, Object[] truoc, Object[] giaTri, boolean xacNhanTrungSdt) {
        if (loai == LoaiDanhMuc.VOUCHER) kiemTraLuuVoucher(truoc, giaTri);
        else if (loai == LoaiDanhMuc.NHA_CUNG_CAP) kiemTraLuuNcc(truoc, giaTri, xacNhanTrungSdt);
        else if (loai == LoaiDanhMuc.SAN_PHAM) kiemTraLuuSanPham(giaTri);
    }

    private void kiemTraLuuVoucher(Object[] truoc, Object[] giaTri) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_KHUYEN_MAI);
        String ma = String.valueOf(truoc == null ? giaTri[0] : truoc[0]);
        RangBuocVoucher rangBuoc = danhMucDAO.layRangBuocVoucher(ma);
        if (rangBuoc != null && rangBuoc.khoaDoiDiem != null)
            throw new IllegalArgumentException(
                    "Voucher đổi điểm giữ nguyên mệnh giá, hạn, chủ và một lượt dùng; không sửa qua"
                            + " danh mục.");
        if (giaTri.length < 9) throw new IllegalArgumentException("Cần chọn khách sở hữu voucher.");
        if (rangBuoc == null && String.valueOf(giaTri[7]).isBlank())
            throw new IllegalArgumentException("Voucher mới phải gắn với một khách hàng.");
        if ((rangBuoc == null || rangBuoc.maKhachHang != null)
                && !"1".equals(String.valueOf(giaTri[3])))
            throw new IllegalArgumentException("Voucher cá nhân chỉ có một lượt dùng.");
        if (rangBuoc != null && rangBuoc.daDung > 0)
            throw new IllegalArgumentException(
                    "Voucher đã dùng chỉ được tra cứu, không sửa hoặc phát hành lại.");
    }

    private void kiemTraLuuNcc(Object[] truoc, Object[] giaTri, boolean xacNhanTrungSdt) {
        String maLoaiTru = truoc == null ? "" : String.valueOf(truoc[0]);
        String ten = String.valueOf(giaTri[1]).trim();
        String sdt = String.valueOf(giaTri[3]).replaceAll("[\\s.]", "");
        if (ten.isBlank())
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống.");
        if (!sdt.matches("0\\d{9}"))
            throw new IllegalArgumentException("Số điện thoại phải gồm 10 chữ số, bắt đầu bằng 0.");
        if (danhMucDAO.tonTaiTenNcc(ten, maLoaiTru))
            throw new IllegalArgumentException("Tên nhà cung cấp đã tồn tại.");
        String sdtCu = truoc == null ? "" : String.valueOf(truoc[3]).replaceAll("[\\s.]", "");
        if ((truoc == null || !sdt.equals(sdtCu))
                && !xacNhanTrungSdt
                && danhMucDAO.tonTaiSdtNcc(sdt, maLoaiTru))
            throw new TrungSoDienThoaiException("Số điện thoại đã được nhà cung cấp khác sử dụng.");
        giaTri[1] = ten;
        giaTri[3] = sdt;
    }

    private void kiemTraLuuSanPham(Object[] giaTri) {
        if (giaTri.length >= 17) {
            String anh = String.valueOf(giaTri[10]);
            if (!anh.isBlank() && !TaiAnhSanPham.kiemTraDuongDan(anh))
                throw new IllegalArgumentException("Ảnh sản phẩm phải nằm trong images/products.");
            int[] chiSo = {11, 12, 13, 14};
            int[] gioiHan = {100, 100, 100, 1000};
            for (int i = 0; i < chiSo.length; i++)
                if (String.valueOf(giaTri[chiSo[i]]).length() > gioiHan[i])
                    throw new IllegalArgumentException("Thông tin chi tiết sản phẩm quá dài.");
        }
        if (phienDangNhapService.layPhien().kiemTraCo(Quyen.DUYET_GIA)) {
            phienDangNhapService.yeuCau(Quyen.DUYET_GIA);
        } else {
            Optional<Long> gia = danhMucDAO.layGiaBanSanPham(String.valueOf(giaTri[0]));
            long giaCu = gia.orElse(0L);
            if (DinhDang.doc(String.valueOf(giaTri[4])) != giaCu)
                throw new SecurityException(
                        "Nhân viên kho dùng nút Đề xuất giá; chỉ quản lý được áp dụng giá bán.");
            if (gia.isEmpty()) giaTri[9] = "Ngừng kinh doanh";
        }
    }

    private void ghiNhatKyLuuDanhMuc(
            Connection ketNoi, LoaiDanhMuc loai, Object[] truoc, Object[] giaTri, boolean laNcc)
            throws Exception {
        if (laNcc) {
            String maMoi = String.valueOf(giaTri[0]);
            nhatKyHeThongService.ghiThayDoi(
                    ketNoi,
                    truoc == null ? "Thêm NCC" : "Sửa NCC",
                    "NHA_CUNG_CAP",
                    maMoi,
                    danhMucDAO.layAnhChupNcc(maMoi));
        } else {
            nhatKyHeThongService.ghiNhatKy(truoc == null ? "Thêm" : "Sửa", loai + " " + giaTri[0]);
        }
    }

    // 5. NGHIỆP VỤ BỔ TRỢ SẢN PHẨM

    public void capNhatBarcode(String maSanPham, String barcode) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        danhMucDAO.capNhatBarcode(maSanPham, barcode);
    }

    // 6. NGHIỆP VỤ BỔ TRỢ TÀI KHOẢN & NHÂN VIÊN

    public void datLaiMatKhau(String tenDangNhap, String matKhau) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        danhMucDAO.datLaiMatKhau(tenDangNhap, matKhau);
    }

    public String layTenDangNhapTheoNhanVien(String maNhanVien) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        return danhMucDAO.layTenDangNhapTheoNhanVien(maNhanVien);
    }

    public void capNhatHoacTaoTaiKhoan(
            String maNhanVien, String tenDangNhap, String matKhauHash, String vaiTro) {
        phienDangNhapService.yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String daCo = danhMucDAO.layTenDangNhapTheoNhanVien(maNhanVien);
        if (daCo == null) {
            danhMucDAO.taoTaiKhoanNhanVien(maNhanVien, tenDangNhap, matKhauHash, vaiTro);
        } else {
            danhMucDAO.datLaiMatKhau(daCo, matKhauHash);
        }
    }

    // 7. KIỂM TRA DỮ LIỆU NGHIỆP VỤ DANH MỤC

    public static String kiemTraNhanVien(String hoTen, String sdt, String cccd, String ngayVaoLam) {
        String ten = hoTen == null ? "" : hoTen.trim();
        if (ten.split("\\s+").length < 2 || ten.matches(".*\\d.*")) {
            return "Họ và tên nhân viên phải gồm ít nhất 2 từ và không chứa chữ số.";
        }
        String dienThoai = sdt == null ? "" : sdt.replaceAll("[\\s.]", "");
        if (!dienThoai.matches("0\\d{9}")) {
            return "Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng số 0.";
        }
        String soCccd = cccd == null ? "" : cccd.trim();
        if (!soCccd.matches("\\d{12}")) {
            return "Số CCCD phải gồm đúng 12 chữ số.";
        }
        int maTheKy = soCccd.charAt(3) - '0';
        int haiSoNam = Integer.parseInt(soCccd.substring(4, 6));
        int namSinh =
                (maTheKy == 0 || maTheKy == 1)
                        ? (1900 + haiSoNam)
                        : (maTheKy == 2 || maTheKy == 3) ? (2000 + haiSoNam) : -1;
        if (namSinh < 0) {
            return "Mã thế kỷ / giới tính trên CCCD (chữ số thứ 4) phải từ 0 đến 3.";
        }
        int namHienTai = LocalDate.now().getYear();
        if (namHienTai - namSinh < 18) {
            return "Nhân viên phải đủ từ 18 tuổi trở lên (theo năm sinh trên CCCD: "
                    + namSinh
                    + ").";
        }
        try {
            LocalDate ngayVao =
                    LocalDate.parse(ngayVaoLam == null ? "" : ngayVaoLam.trim(), DinhDang.NGAY_U);
            if (ngayVao.isAfter(LocalDate.now())) {
                return "Ngày vào làm không được lớn hơn ngày hiện tại.";
            }
        } catch (RuntimeException ex) {
            LOG.debug(
                    "Bỏ qua kiểm tra ngày vào làm chưa đúng định dạng '{}': {}",
                    ngayVaoLam,
                    ex.getMessage());
        }
        return null;
    }

    public static String suyRaVaiTroTuChucVu(String chucVu) {
        String cv = chucVu == null ? "" : chucVu;
        if (cv.contains("trưởng")) return VaiTro.QUAN_LY_CUA_HANG.name();
        if (cv.contains("kho")) return VaiTro.NHAN_VIEN_KHO.name();
        if (cv.contains("Spa")) return VaiTro.NHAN_VIEN_SPA_HOTEL.name();
        return VaiTro.NHAN_VIEN_BAN_HANG.name();
    }

    public static String kiemTraKhuyenMai(LocalDate tu, LocalDate den, String loai, long giaTri) {
        if (tu != null && den != null && den.isBefore(tu)) {
            return "Đến ngày phải sau hoặc bằng Từ ngày.";
        }
        if (giaTri <= 0) {
            return "Giá trị khuyến mãi phải lớn hơn 0.";
        }
        if ("Giảm %".equals(loai) && giaTri > 100) {
            return "Giá trị giảm % phải từ 1 đến 100.";
        }
        return null;
    }

    public static String kiemTraCanNangThuCung(String canNang) {
        String giaTri = canNang == null ? "" : canNang.trim();
        return giaTri.isEmpty() || giaTri.matches("\\d{1,3}([.,]\\d{1,2})?")
                ? null
                : "Cân nặng phải là số (kg), ví dụ 5.2.";
    }

    public static String kiemTraVoucherSoLuong(long soLuong, long daDung) {
        return soLuong < daDung ? "Số lượng không được nhỏ hơn số đã dùng." : null;
    }
}
