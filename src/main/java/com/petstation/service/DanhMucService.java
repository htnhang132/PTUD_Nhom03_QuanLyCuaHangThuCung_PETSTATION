package com.petstation.service;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;
import static com.petstation.util.DuLieu.*;

import com.petstation.dao.DanhMucDAO;
import com.petstation.dao.DeXuatGiaDAO;
import com.petstation.dao.TichDiemDAO;
import com.petstation.dao.impl.DanhMucDAOImpl;
import com.petstation.dao.impl.DeXuatGiaDAOImpl;
import com.petstation.dao.impl.TichDiemDAOImpl;
import com.petstation.model.LoaiDanhMuc;
import com.petstation.model.Quyen;

import java.util.*;

public final class DanhMucService {
    private static final DanhMucDAO danhMucDAO = new DanhMucDAOImpl();
    private static final DeXuatGiaDAO deXuatGiaDAO = new DeXuatGiaDAOImpl();
    private static final TichDiemDAO tichDiemDAO = new TichDiemDAOImpl();

    public static final class CacDong extends ArrayList<Object[]> {
        public final LoaiDanhMuc loai;

        CacDong(LoaiDanhMuc loai) {
            this(loai, true);
        }

        CacDong(LoaiDanhMuc loai, boolean docNgay) {
            this.loai = loai;
            if (docNgay) lamMoi();
        }

        public List<Object[]> taiBanSao() {
            return danhMucDAO.nap(loai);
        }

        public void lamMoi() {
            super.clear();
            super.addAll(danhMucDAO.nap(loai));
        }
    }

    private static final List<Runnable> GIA_CAC_BO_LANG_NGHE = new ArrayList<>();

    private DanhMucService() {}

    public static CacDong chuanBi(LoaiDanhMuc loai, boolean docNgay) {
        return new CacDong(loai, docNgay);
    }

    public static CacDong layKhachHang() {
        return new CacDong(LoaiDanhMuc.KHACH_HANG);
    }

    public static CacDong layThuCung() {
        return new CacDong(LoaiDanhMuc.THU_CUNG);
    }

    public static CacDong layNhanVien() {
        return new CacDong(LoaiDanhMuc.NHAN_VIEN);
    }

    public static CacDong layTaiKhoan() {
        return new CacDong(LoaiDanhMuc.TAI_KHOAN);
    }

    public static CacDong laySanPham() {
        return new CacDong(LoaiDanhMuc.SAN_PHAM);
    }

    public static CacDong layNhaCungCap() {
        return new CacDong(LoaiDanhMuc.NHA_CUNG_CAP);
    }

    public static CacDong layKhuyenMai() {
        return new CacDong(LoaiDanhMuc.KHUYEN_MAI);
    }

    public static CacDong layVoucher() {
        return new CacDong(LoaiDanhMuc.VOUCHER);
    }

    public static CacDong layDichVuSpa() {
        return new CacDong(LoaiDanhMuc.DICH_VU_SPA);
    }

    public static CacDong layPhong() {
        return new CacDong(LoaiDanhMuc.PHONG_LUU_TRU);
    }

    public static List<String> layLuaChonKhachHang() {
        return layKhachHang().stream()
                .filter(dongDuLieu -> "Đang hoạt động".equals(dongDuLieu[8]))
                .map(dongDuLieu -> dongDuLieu[0] + " - " + dongDuLieu[1])
                .toList();
    }

    public static List<String> layLuaChonNhanVien() {
        return layNhanVien().stream()
                .filter(dongDuLieu -> !"Nghỉ việc".equals(dongDuLieu[6]))
                .map(dongDuLieu -> dongDuLieu[0] + " - " + dongDuLieu[1])
                .toList();
    }

    public static String layMaTuLuaChon(Object luaChon) {
        return chuyenThanhChuoi(luaChon).split(" - ", 2)[0];
    }

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

    public static void luuNhieu(
            List<Object[]> cacDong,
            Object[] truoc,
            Object[] giaTriCapNhat,
            List<Object[]> cacGiaTriMoi) {
        thucHienGiaoDich(
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

    public static void themBoLangNgheGia(Runnable boLangNghe) {
        GIA_CAC_BO_LANG_NGHE.add(boLangNghe);
    }

    public static void luu(List<Object[]> cacDong, Object[] truoc, Object[] giaTri){
        luu(cacDong, truoc, giaTri, false);
    }
    public static final class TrungSoDienThoaiException
    extends IllegalArgumentException {
	public TrungSoDienThoaiException(String thongBao) {
	    super(thongBao);
		}
    }
    public static void luu(
            List<Object[]> cacDong,
            Object[] truoc,
            Object[] giaTri,
            boolean xacNhanTrungSdt){
        if (!(cacDong instanceof CacDong danhMuc))
            throw new IllegalArgumentException("Danh mục chưa nối SQL Server.");

        Quyen quyen =
                switch (danhMuc.loai) {
                    case KHACH_HANG -> Quyen.QUAN_LY_KHACH_HANG;
                    case THU_CUNG -> Quyen.QUAN_LY_THU_CUNG;
                    case NHAN_VIEN, TAI_KHOAN -> Quyen.QUAN_LY_TAI_KHOAN;
                    case SAN_PHAM -> Quyen.QUAN_LY_SAN_PHAM;
                    case NHA_CUNG_CAP -> Quyen.QUAN_LY_NHA_CUNG_CAP;
                    case KHUYEN_MAI, VOUCHER -> Quyen.QUAN_LY_KHUYEN_MAI;
                    case DICH_VU_SPA -> Quyen.QUAN_LY_SPA;
                    case PHONG_LUU_TRU -> Quyen.QUAN_LY_LUU_TRU;
                };
        yeuCau(quyen);

        thucHienGiaoDich(
                ketNoi -> {
                    if (danhMuc.loai == LoaiDanhMuc.VOUCHER) {
                        yeuCau(Quyen.QUAN_LY_KHUYEN_MAI);
                        String ma = String.valueOf(truoc == null ? giaTri[0] : truoc[0]);
                        var rangBuoc = tichDiemDAO.layRangBuocVoucher(ma);
                        if (!rangBuoc.isEmpty() && rangBuoc.get(0)[1] != null)
                            throw new IllegalArgumentException(
                                    "Voucher đổi điểm giữ nguyên mệnh giá, hạn, chủ và một lượt"
                                            + " dùng; không sửa qua danh mục.");
                        if (giaTri.length < 9)
                            throw new IllegalArgumentException("Cần chọn khách sở hữu voucher.");
                        if (rangBuoc.isEmpty() && String.valueOf(giaTri[7]).isBlank())
                            throw new IllegalArgumentException(
                                    "Voucher mới phải gắn với một khách hàng.");
                        if ((rangBuoc.isEmpty() || rangBuoc.get(0)[2] != null)
                                && !"1".equals(String.valueOf(giaTri[3])))
                            throw new IllegalArgumentException(
                                    "Voucher cá nhân chỉ có một lượt dùng.");
                        if (!rangBuoc.isEmpty() && ((Number) rangBuoc.get(0)[0]).longValue() > 0)
                            throw new IllegalArgumentException(
                                    "Voucher đã dùng chỉ được tra cứu, không sửa hoặc phát hành"
                                            + " lại.");
                    }
                    if (danhMuc.loai == LoaiDanhMuc.NHA_CUNG_CAP) {
                        String maLoaiTru = truoc == null
                                ? ""
                                : String.valueOf(truoc[0]);

                        String ten = String.valueOf(giaTri[1]).trim();
                        String sdt = String.valueOf(giaTri[3])
                                .replaceAll("[\\s.]", "");

                        if (ten.isBlank()) {
                            throw new IllegalArgumentException(
                                    "Tên nhà cung cấp không được để trống.");
                        }

                        if (!sdt.matches("0\\d{9}")) {
                            throw new IllegalArgumentException(
                                    "Số điện thoại phải gồm 10 chữ số, bắt đầu bằng 0.");
                        }

                        if (danhMucDAO.tonTaiTenNcc(ten, maLoaiTru)) {
                            throw new IllegalArgumentException(
                                    "Tên nhà cung cấp đã tồn tại.");
                        }

                        String sdtCu = truoc == null
                                ? ""
                                : String.valueOf(truoc[3]).replaceAll("[\\s.]", "");

                        boolean canKiemTraTrungSdt =
                                truoc == null || !sdt.equals(sdtCu);

                        if (canKiemTraTrungSdt
                                && !xacNhanTrungSdt
                                && danhMucDAO.tonTaiSdtNcc(sdt, maLoaiTru)) {
                            throw new TrungSoDienThoaiException(
                                    "Số điện thoại đã được nhà cung cấp khác sử dụng.");
                        }

                        giaTri[1] = ten;
                        giaTri[3] = sdt;
                    }
                    if (danhMuc.loai == LoaiDanhMuc.SAN_PHAM && giaTri.length >= 17) {
                        String anh = String.valueOf(giaTri[10]);
                        if (!anh.isBlank()
                                && !com.petstation.util.TaiAnhSanPham.kiemTraDuongDan(anh))
                            throw new IllegalArgumentException(
                                    "Ảnh sản phẩm phải nằm trong images/products.");
                        int[] chiSo = {11, 12, 13, 14};
                        int[] gioiHan = {100, 100, 100, 1000};
                        for (int i = 0; i < chiSo.length; i++)
                            if (String.valueOf(giaTri[chiSo[i]]).length() > gioiHan[i])
                                throw new IllegalArgumentException(
                                        "Thông tin chi tiết sản phẩm quá dài.");
                    }
                    if (danhMuc.loai == LoaiDanhMuc.SAN_PHAM
                            && layPhien().docVaiTro() == com.petstation.model.VaiTro.QUAN_LY_CUA_HANG)
                        yeuCau(Quyen.DUYET_GIA);
                    if (danhMuc.loai == LoaiDanhMuc.SAN_PHAM
                            && layPhien().docVaiTro()
                                    != com.petstation.model.VaiTro.QUAN_LY_CUA_HANG) {
                        var sp = deXuatGiaDAO.timSanPham(String.valueOf(giaTri[0]));
                        long giaCu = sp.isEmpty() ? 0 : ((Number) sp.get(0)[1]).longValue();
                        if (com.petstation.util.TienTe.doc(String.valueOf(giaTri[4])) != giaCu)
                            throw new PhanQuyenException(
                                    "Nhân viên kho dùng nút Đề xuất giá; chỉ quản lý được áp dụng"
                                            + " giá bán.");
                        if (sp.isEmpty()) giaTri[9] = "Ngừng kinh doanh";
                    }
                    boolean laNcc = danhMuc.loai == LoaiDanhMuc.NHA_CUNG_CAP;

                    String maCu = String.valueOf(
                            truoc == null ? giaTri[0] : truoc[0]);

                    String duLieuCu = laNcc
                            ? danhMucDAO.layAnhChupNcc(maCu)
                            : null;

                    danhMucDAO.luu(
                            danhMuc.loai, truoc, giaTri,
                            layIdTaiKhoan(), layPhien().docVaiTro().name());

                    if (laNcc) {
                        String maMoi = String.valueOf(giaTri[0]);

                        NhatKyHeThongService.ghiThayDoi(
                                ketNoi,
                                truoc == null ? "Thêm NCC" : "Sửa NCC",
                                "NHA_CUNG_CAP",
                                maMoi,
                                duLieuCu,
                                danhMucDAO.layAnhChupNcc(maMoi));
                    } else {
                        NhatKyHeThongService.ghiNhatKy(
                                truoc == null ? "Thêm" : "Sửa",
                                danhMuc.loai + " " + giaTri[0]);
                    }
                    return null;
                });
        if (danhMuc.loai == LoaiDanhMuc.SAN_PHAM)
            for (Runnable boLangNghe : List.copyOf(GIA_CAC_BO_LANG_NGHE)) boLangNghe.run();
    }

    public static List<Object[]> layLichSuGia() {
        return danhMucDAO.layLichSuGia();
    }

    public static long layId(String bang, String khoa, String ma) {
        return danhMucDAO.layId(bang, khoa, ma);
    }

    public static int khoaCacTaiKhoanCua(String maNhanVien) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        return danhMucDAO.khoaCacTaiKhoanCua(maNhanVien);
    }

    public static void datLaiMatKhau(String tenDangNhap, String matKhau) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        danhMucDAO.datLaiMatKhau(tenDangNhap, matKhau);
    }

    public static void capNhatBarcode(long sanPhamId, String barcode) {
        yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        danhMucDAO.capNhatBarcode(sanPhamId, barcode);
    }

    public static boolean kiemTraSanPhamCoGiaoDich(long sanPhamId) {
        return danhMucDAO.kiemTraSanPhamCoGiaoDich(sanPhamId);
    }

    public static void ngungKinhDoanhSanPham(long sanPhamId) {
        yeuCau(Quyen.QUAN_LY_SAN_PHAM);
        danhMucDAO.ngungKinhDoanhSanPham(sanPhamId);
    }

    public static String layTenDangNhapTheoNhanVien(long nhanVienId) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        return danhMucDAO.layTenDangNhapTheoNhanVien(nhanVienId);
    }

    public static void capNhatHoacTaoTaiKhoan(
            long nhanVienId, String tenDangNhap, String matKhauHash, String vaiTro) {
        yeuCau(Quyen.QUAN_LY_TAI_KHOAN);
        String daCo = danhMucDAO.layTenDangNhapTheoNhanVien(nhanVienId);
        if (daCo == null) {
            danhMucDAO.taoTaiKhoanNhanVien(nhanVienId, tenDangNhap, matKhauHash, vaiTro);
        } else {
            danhMucDAO.datLaiMatKhau(daCo, matKhauHash);
        }
    }

    public static void xoaBoLangNghe() {
        GIA_CAC_BO_LANG_NGHE.clear();
    }
}
