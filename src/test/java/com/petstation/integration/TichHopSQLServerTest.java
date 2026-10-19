package com.petstation.integration;

import static com.petstation.dao.ThaoTacSQL.*;
import static com.petstation.service.PhienDangNhapService.*;

import static org.assertj.core.api.Assertions.*;

import com.petstation.dao.CauHinhCSDL;
import com.petstation.dao.KetNoiCSDL;
import com.petstation.dao.ThaoTacSQL;
import com.petstation.dao.impl.JdbcKhachHangDAO;
import com.petstation.gui.components.HoTroThanhCongCu;
import com.petstation.gui.navigation.DangKyManHinh;
import com.petstation.gui.navigation.MaManHinh;
import com.petstation.model.*;
import com.petstation.model.GioHang;
import com.petstation.model.LoaiDanhMuc;
import com.petstation.model.LoaiNghiepVu;
import com.petstation.model.NhanVien;
import com.petstation.model.PhienDangNhap;
import com.petstation.model.Quyen;
import com.petstation.model.TaiKhoan;
import com.petstation.model.TrangThaiTaiKhoan;
import com.petstation.model.VaiTro;
import com.petstation.service.BanHangService;
import com.petstation.service.CaThuNganService;
import com.petstation.service.CaiDatService;
import com.petstation.service.DanhMucService;
import com.petstation.service.DoiTraService;
import com.petstation.service.KhoService;
import com.petstation.service.LichLamViecService;
import com.petstation.service.LuuTruService;
import com.petstation.service.NghiepVuService;
import com.petstation.service.NhatKyHeThongService;
import com.petstation.service.PheDuyetService;
import com.petstation.service.SpaService;
import com.petstation.service.ThongKeService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.*;
import java.util.*;

/** Cung cấp chức năng SQL máy chủ tích hợp kiểm thử của ứng dụng. */
@EnabledIfEnvironmentVariable(named = "PETSTATION_SQL_TESTS", matches = "true")
class TichHopSQLServerTest {
    /** Cung cấp chức năng hoàn tác giao dịch của ứng dụng. */
    private static class HoanTac extends RuntimeException {}

    /** Nhân viên. */
    private long nhanVien, taiKhoan, khachHang, sanPham, loHang1, loHang2;

    /** Mã nhân viên. */
    private String maNhanVien, maKhachHang, maSanPham, voucher, maThuCung, maPhong, maGoiDichVu;

    /** Quản lý. */
    private PhienDangNhap quanLy;

    /** Cấu hình trong TichHopSQLServerTest. */
    @BeforeAll
    static void cauHinh() {
        // Cấu hình.
        CauHinhCSDL cauHinh = CauHinhCSDL.nap();
        assertThat(cauHinh.url()).contains("databaseName=PetStationIntegrationTest");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cauHinh));
    }

    /**
     * Lấy tình huống trong TichHopSQLServerTest.
     *
     * @param kiemThu kiểm thử
     */
    private void layTinhHuong(Runnable kiemThu) {
        try {
            thucHienGiaoDich(
                    ketNoi -> {
                        layDuLieuKiemThu();
                        kiemThu.run();
                        throw new HoanTac();
                    });
        } catch (HoanTac duKien) {
        } finally {
            datPhienDangNhap(null);
        }
    }

    /** Lấy dữ liệu kiểm thử trong TichHopSQLServerTest. */
    private void layDuLieuKiemThu() {
        // Hậu tố.
        String hauTo = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        maNhanVien = "ITNV" + hauTo;
        maKhachHang = "ITKH" + hauTo;
        maSanPham = "ITSP" + hauTo;
        voucher = "ITVC" + hauTo;
        maThuCung = "ITTC" + hauTo;
        maPhong = "ITP" + hauTo;
        maGoiDichVu = "ITDV" + hauTo;
        nhanVien =
                them(
                        "INSERT INTO"
                            + " nhan_vien(ma_nhan_vien,ho_ten,so_dien_thoai,chuc_vu,ngay_vao_lam)"
                            + " VALUES(?,N'Integration Manager',?,N'Cửa hàng trưởng',CAST(GETDATE()"
                            + " AS date))",
                        maNhanVien,
                        "IT" + hauTo);
        taiKhoan =
                them(
                        "INSERT INTO"
                            + " tai_khoan(nhan_vien_id,ten_dang_nhap,mat_khau_hash,vai_tro,bat_buoc_doi_mat_khau)"
                            + " VALUES(?,?,?,'STORE_MANAGER',0)",
                        nhanVien,
                        maNhanVien,
                        org.mindrot.jbcrypt.BCrypt.hashpw(
                                "Integration@123", org.mindrot.jbcrypt.BCrypt.gensalt(4)));
        quanLy =
                new PhienDangNhap(
                        new TaiKhoan(
                                taiKhoan,
                                nhanVien,
                                maNhanVien,
                                "hash",
                                VaiTro.STORE_MANAGER,
                                TrangThaiTaiKhoan.ACTIVE,
                                0,
                                null,
                                false),
                        new NhanVien(
                                nhanVien,
                                maNhanVien,
                                "Integration Manager",
                                null,
                                "IT" + hauTo,
                                null,
                                "Cửa hàng trưởng",
                                LocalDate.now(),
                                true),
                        EnumSet.allOf(Quyen.class));
        datPhienDangNhap(quanLy);
        khachHang =
                them(
                        "INSERT INTO khach_hang(ma_khach_hang,ho_ten,so_dien_thoai,diem_tich_luy)"
                                + " VALUES(?,N'Integration Customer',?,100)",
                        maKhachHang,
                        "KH" + hauTo);
        sanPham =
                them(
                        "INSERT INTO"
                            + " san_pham(ma_san_pham,ten,nhom_hang,don_vi_tinh,gia_ban,gia_nhap_gan_nhat,theo_doi_hsd)"
                            + " VALUES(?,?,N'Test',N'Cái',100000,50000,1)",
                        maSanPham,
                        "Product " + hauTo);
        loHang1 =
                them(
                        "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                                + " VALUES(?,'IT-FIRST',DATEADD(day,5,GETDATE()),2)",
                        sanPham);
        loHang2 =
                them(
                        "INSERT INTO lo_hang(san_pham_id,so_lo,han_su_dung,so_luong_ton)"
                                + " VALUES(?,'IT-SECOND',DATEADD(day,10,GETDATE()),4)",
                        sanPham);
        capNhat(
                "INSERT INTO"
                    + " voucher(ma_voucher,menh_gia,hieu_luc_den,so_luong,don_toi_thieu,chi_ap_dung_spa)"
                    + " VALUES(?,10000,DATEADD(day,1,GETDATE()),2,0,0)",
                voucher);
        capNhat(
                "INSERT INTO thu_cung(ma_thu_cung,khach_hang_id,ten,loai,can_nang)"
                        + " VALUES(?,?,N'Integration Pet','DOG',4)",
                maThuCung,
                khachHang);
        capNhat(
                "INSERT INTO phong_hotel(ma_phong,loai_phong,gia_dem,trang_thai) VALUES(?,N'Tiêu"
                        + " chuẩn',150000,'AVAILABLE')",
                maPhong);
        // Gói dịch vụ.
        long goiDichVu =
                them(
                        "INSERT INTO goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien)"
                                + " VALUES(?,?,60)",
                        maGoiDichVu,
                        "Service " + hauTo);
        capNhat(
                "INSERT INTO"
                    + " bang_gia_spa(goi_id,loai_thu_cung,khoang_can,can_nang_tu,can_nang_den,don_gia)"
                    + " VALUES(?,'DOG',N'< 5kg',0,5,150000)",
                goiDichVu);
    }

    /**
     * Lấy giỏ hàng trong TichHopSQLServerTest.
     *
     * @param soLuong số lượng
     * @return kết quả lấy giỏ hàng
     */
    private GioHang layGioHang(int soLuong) {
        // Bán hàng.
        GioHang banHang = new GioHang();
        banHang.datCacDong(
                List.of(new GioHang.DongHang(maSanPham, "Integration product", 100000, soLuong)));
        banHang.datKhachHang(maKhachHang, "Integration Customer", 100);
        return banHang;
    }

    /** Mở trong TichHopSQLServerTest. */
    private void mo() {
        new CaThuNganService().mo("Integration shift", 1000000);
    }

    /** Lấy các danh mục đọc và lưu trữ thực tế SQL các dòng trong TichHopSQLServerTest. */
    @Test
    void layCacDanhMucDocVaLuuTruThucTeSQLCacDong() {
        layTinhHuong(
                () -> {
                    // Các dòng.
                    var cacDong = DanhMucService.layKhachHang();
                    // Trước.
                    Object[] truoc =
                            cacDong.stream()
                                    .filter(dongDuLieu -> dongDuLieu[0].equals(maKhachHang))
                                    .findFirst()
                                    .orElseThrow();
                    // Đã thay đổi.
                    Object[] daThayDoi = truoc.clone();
                    daThayDoi[1] = "Updated customer";
                    daThayDoi[3] = "test@example.com";
                    DanhMucService.luu(cacDong, truoc, daThayDoi);
                    assertThat(DanhMucService.layKhachHang())
                            .anySatisfy(
                                    dongDuLieu -> {
                                        assertThat(dongDuLieu[0]).isEqualTo(maKhachHang);
                                        assertThat(dongDuLieu[1]).isEqualTo("Updated customer");
                                        assertThat(dongDuLieu[3]).isEqualTo("test@example.com");
                                    });
                    thucHienGiaoDich(
                            ketNoi -> {
                                assertThat(
                                                new JdbcKhachHangDAO(ketNoi)
                                                        .timTheoMaKhachHang(maKhachHang)
                                                        .orElseThrow()
                                                        .layHoTen())
                                        .isEqualTo("Updated customer");
                                return null;
                            });
                    // Các sản phẩm.
                    var cacSanPham = DanhMucService.laySanPham();
                    // Vùng đang xét.
                    Object[] vungDangXet =
                            cacSanPham.stream()
                                    .filter(dongDuLieu -> dongDuLieu[0].equals(maSanPham))
                                    .findFirst()
                                    .orElseThrow();
                    // Tiếp theo.
                    Object[] tiepTheo = vungDangXet.clone();
                    tiepTheo[4] = "110000";
                    DanhMucService.luu(cacSanPham, vungDangXet, tiepTheo);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM lich_su_gia WHERE san_pham_id=?"
                                                    + " AND gia_cu=100000 AND gia_moi=110000",
                                            sanPham))
                            .isEqualTo(1);
                    for (var loai : LoaiNghiepVu.values())
                        assertThat(NghiepVuService.layCacDong(loai)).isNotNull();
                    assertThat(DanhMucService.layThuCung()).isNotEmpty();
                    assertThat(DanhMucService.layNhanVien()).isNotEmpty();
                    assertThat(DanhMucService.layTaiKhoan()).isNotEmpty();
                    assertThat(DanhMucService.layNhaCungCap()).isNotEmpty();
                    assertThat(DanhMucService.layDichVuSpa()).isNotEmpty();
                    assertThat(DanhMucService.layPhong()).isNotEmpty();
                });
    }

    /** Lấy thanh toán phân bổ FEFO và là không lặp giao dịch trong TichHopSQLServerTest. */
    @Test
    void layThanhToanPhanBoFEFOVaLaKhongLapGiaoDich() {
        layTinhHuong(
                () -> {
                    mo();
                    // Bán hàng.
                    GioHang banHang = layGioHang(3);
                    assertThat(banHang.apDungVoucher(voucher)).isNull();
                    banHang.datDiemDaDung(2);
                    // Tổng.
                    long tong = banHang.tinhTong();
                    // Đầu tiên.
                    GioHang.BienLai dauTien =
                            banHang.hoanTat(CaThuNganService.PhuongThuc.CASH, 500000, "Manager");
                    // Thứ hai.
                    GioHang.BienLai thuHai =
                            banHang.hoanTat(CaThuNganService.PhuongThuc.CASH, 500000, "Manager");
                    assertThat(thuHai.soHoaDon()).isEqualTo(dauTien.soHoaDon());
                    assertThat(dauTien.tong()).isEqualTo(tong);
                    assertThat(dauTien.thayDoi()).isEqualTo(500000 - tong);
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang1))
                            .isZero();
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(3);
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isEqualTo(1);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM lich_su_diem WHERE"
                                                    + " khach_hang_id=?",
                                            khachHang))
                            .isEqualTo(1);
                    assertThat(new CaThuNganService().demHoaDon()).isEqualTo(1);
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1000000 + tong);
                });
    }

    /** Lấy bán quá tồn hoàn tác quay lại hóa đơn voucher và tồn kho trong TichHopSQLServerTest. */
    @Test
    void layBanQuaTonHoanTacQuayLaiHoaDonVoucherVaTonKho() {
        layTinhHuong(
                () -> {
                    mo();
                    // Bán hàng.
                    GioHang banHang = layGioHang(7);
                    banHang.apDungVoucher(voucher);
                    // Các hóa đơn.
                    long cacHoaDon = laySo("SELECT COUNT(*) FROM hoa_don"),
                            cacDonHang = laySo("SELECT COUNT(*) FROM don_hang");
                    assertThatThrownBy(
                                    () ->
                                            banHang.hoanTat(
                                                    CaThuNganService.PhuongThuc.CASH,
                                                    1000000,
                                                    "Manager"))
                            .hasMessageContaining("Không đủ tồn");
                    assertThat(laySo("SELECT COUNT(*) FROM hoa_don")).isEqualTo(cacHoaDon);
                    assertThat(laySo("SELECT COUNT(*) FROM don_hang")).isEqualTo(cacDonHang);
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isZero();
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(6);
                });
    }

    /**
     * Lấy đồng thời tồn kho ảnh chụp dữ liệu các thay đổi không thể được đã duyệt không kiểm tra
     * trong TichHopSQLServerTest.
     */
    @Test
    void layDongThoiTonKhoAnhChupDuLieuCacThayDoiKhongTheDuocDaDuyetKhongKiemTra() {
        layTinhHuong(
                () -> {
                    // Số lượng.
                    long soLuong = KhoService.kiemKe(loHang1, 1, "Recount");
                    // Yêu cầu.
                    var yeuCau =
                            PheDuyetService.layTatCa().stream()
                                    .filter(dongDuLieu -> dongDuLieu.sau.equals("Tồn thực 1"))
                                    .findFirst()
                                    .orElseThrow();
                    capNhat("UPDATE lo_hang SET so_luong_ton=3 WHERE id=?", loHang1);
                    assertThatThrownBy(() -> PheDuyetService.duyet(yeuCau, "Manager"))
                            .hasMessageContaining("Tồn đã thay đổi");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM yeu_cau_phe_duyet WHERE"
                                                            + " ma_yeu_cau=?",
                                                    yeuCau.id)))
                            .isEqualTo("PENDING");
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang1))
                            .isEqualTo(3);
                });
    }

    /** Lấy đã lưu trữ phê duyệt và từ chối áp dụng đúng một lần trong TichHopSQLServerTest. */
    @Test
    void layDaLuuTruPheDuyetVaTuChoiApDungDungMotLan() {
        layTinhHuong(
                () -> {
                    CaiDatService.capNhat(8, 10, 30, 50000, 3, 1);
                    // Xuất kho.
                    long xuatKho = KhoService.ghiHuy(loHang2, 1, "Test writeoff");
                    // Yêu cầu.
                    var yeuCau =
                            PheDuyetService.layTatCa().stream()
                                    .filter(dongDuLieu -> dongDuLieu.lyDo.equals("Test writeoff"))
                                    .findFirst()
                                    .orElseThrow();
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(4);
                    PheDuyetService.duyet(yeuCau, "Manager");
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(3);
                    assertThatThrownBy(() -> PheDuyetService.duyet(yeuCau, "Manager"))
                            .hasMessageContaining("đã được xử lý");
                    KhoService.ghiHuy(loHang2, 1, "Reject writeoff");
                    // Đã từ chối.
                    var daTuChoi =
                            PheDuyetService.layTatCa().stream()
                                    .filter(dongDuLieu -> dongDuLieu.lyDo.equals("Reject writeoff"))
                                    .findFirst()
                                    .orElseThrow();
                    PheDuyetService.tuChoi(daTuChoi, "Manager", "Không hợp lệ");
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(3);
                });
    }

    /**
     * Lấy lịch làm việc có không được sinh các dòng và nghỉ phép được giữ lại tải lại trong
     * TichHopSQLServerTest.
     */
    @Test
    void layLichLamViecCoKhongDuocSinhCacDongVaNghiPhepDuocGiuLaiTaiLai() {
        layTinhHuong(
                () -> {
                    // Ngày.
                    LocalDate ngay = LocalDate.of(2040, 1, 9);
                    assertThat(
                                    LichLamViecService.layLichTrongTuan(
                                            LichLamViecService.layThuHaiCuaTuan(ngay)))
                            .isEmpty();
                    assertThat(LichLamViecService.phanCong(ngay, 0, maNhanVien)).isNull();
                    assertThat(LichLamViecService.phanCong(ngay, 0, maNhanVien)).contains("đã có");
                    // Yêu cầu.
                    var yeuCau =
                            PheDuyetService.guiThaoTac(
                                    PheDuyetService.Loai.LEAVE,
                                    "Integration leave",
                                    "WORK",
                                    "LEAVE",
                                    "LEAVE",
                                    nhanVien,
                                    ngay.toString());
                    PheDuyetService.duyet(yeuCau, "Manager");
                    assertThat(LichLamViecService.layO(ngay, 0))
                            .anyMatch(
                                    suKien ->
                                            suKien.maNhanVien.equals(maNhanVien)
                                                    && suKien.kiemTraNghiPhep());
                    assertThat(LichLamViecService.phanCong(ngay, 1, maNhanVien))
                            .contains("nghỉ phép");
                    assertThat(LichLamViecService.tinhGioTrongThang(maNhanVien, ngay)).isZero();
                });
    }

    /** Lấy Spa vòng đời sử dụng loài và cân nặng các giá trong TichHopSQLServerTest. */
    @Test
    void laySpaVongDoiSuDungLoaiVaCanNangCacGia() {
        layTinhHuong(
                () -> {
                    mo();
                    // Lịch hẹn.
                    long lichHen =
                            SpaService.datLichHen(
                                    maThuCung,
                                    maGoiDichVu,
                                    maNhanVien,
                                    LocalDateTime.now().plusHours(1));
                    // Mã lịch hẹn.
                    String maLichHen =
                            chuyenThanhChuoi(
                                    layGiaTriDon(
                                            "SELECT ma_lich_hen FROM lich_hen WHERE id=?",
                                            lichHen));
                    assertThatThrownBy(
                                    () ->
                                            SpaService.datLichHen(
                                                    maThuCung,
                                                    maGoiDichVu,
                                                    maNhanVien,
                                                    LocalDateTime.now().plusHours(1)))
                            .hasMessageContaining("trùng");
                    // Phiếu Spa.
                    long phieuSpa = SpaService.tiepNhan(maThuCung, maLichHen, 4.5, "Healthy");
                    assertThat(
                                    laySo(
                                            "SELECT tong_tien FROM phieu_tiep_nhan_spa WHERE id=?",
                                            phieuSpa))
                            .isEqualTo(150000);
                    SpaService.layTrangThaiSpa(phieuSpa, "IN_PROGRESS");
                    SpaService.layTrangThaiSpa(phieuSpa, "COMPLETED");
                    // Hóa đơn.
                    GioHang hoaDon = new GioHang();
                    BanHangService.quyetToan("SPA", phieuSpa, hoaDon);
                    hoaDon.hoanTat(
                            CaThuNganService.PhuongThuc.TRANSFER, hoaDon.tinhTong(), "Manager");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM phieu_tiep_nhan_spa"
                                                            + " WHERE id=?",
                                                    phieuSpa)))
                            .isEqualTo("PAID");
                    assertThatThrownBy(
                                    () -> BanHangService.quyetToan("SPA", phieuSpa, new GioHang()))
                            .hasMessageContaining("đã thanh toán");
                });
    }

    /** Lấy Hotel ngăn trùng lịch và lưu được chăm sóc và quyết toán trong TichHopSQLServerTest. */
    @Test
    void layHotelNganTrungLichVaLuuDuocChamSocVaQuyetToan() {
        layTinhHuong(
                () -> {
                    mo();
                    // Bắt đầu.
                    LocalDateTime batDau = LocalDateTime.now(), ketThuc = batDau.plusDays(2);
                    // Hợp đồng lưu trú.
                    long hopDongLuuTru =
                            LuuTruService.datPhong(
                                    maThuCung, maPhong, batDau, ketThuc, 50000, "Test");
                    assertThatThrownBy(
                                    () ->
                                            LuuTruService.datPhong(
                                                    maThuCung, maPhong, batDau, ketThuc, 0, ""))
                            .hasMessageContaining("trùng");
                    LuuTruService.kiemTraVao(hopDongLuuTru);
                    LuuTruService.ghiNhatKy(hopDongLuuTru, "Ate", "Clean", "Healthy");
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM nhat_ky_cham_soc WHERE"
                                                    + " hop_dong_id=?",
                                            hopDongLuuTru))
                            .isEqualTo(1);
                    assertThat(LuuTruService.tinhTienLuuTruConLai(hopDongLuuTru)).isEqualTo(250000);
                    // Hóa đơn.
                    GioHang hoaDon = new GioHang();
                    BanHangService.quyetToan("HOTEL", hopDongLuuTru, hoaDon);
                    hoaDon.hoanTat(CaThuNganService.PhuongThuc.CARD, hoaDon.tinhTong(), "Manager");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                            + " id=?",
                                                    hopDongLuuTru)))
                            .isEqualTo("CHECKED_OUT");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM phong_hotel WHERE"
                                                            + " ma_phong=?",
                                                    maPhong)))
                            .isEqualTo("CLEANING");
                });
    }

    /** Lấy nhập kho và đổi trả cập nhật tồn kho và công nợ trong TichHopSQLServerTest. */
    @Test
    void layNhapKhoVaDoiTraCapNhatTonKhoVaCongNo() {
        layTinhHuong(
                () -> {
                    mo();
                    // Nhà cung cấp.
                    String nhaCungCap = chuyenThanhChuoi(DanhMucService.layNhaCungCap().get(0)[0]);
                    // Biên lai.
                    long bienLai =
                            KhoService.nhapKho(
                                    nhaCungCap,
                                    maSanPham,
                                    "IT-NEW",
                                    LocalDate.now().plusDays(30),
                                    3,
                                    50000,
                                    50000);
                    assertThat(
                                    laySo(
                                            "SELECT tong_tien-da_tra FROM phieu_nhap_kho WHERE"
                                                    + " id=?",
                                            bienLai))
                            .isEqualTo(100000);
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(9);
                    // Bán hàng.
                    GioHang banHang = layGioHang(2);
                    // Hóa đơn.
                    var hoaDon =
                            banHang.hoanTat(CaThuNganService.PhuongThuc.CASH, 500000, "Manager");
                    // Mục.
                    long muc =
                            laySo(
                                    "SELECT i.id FROM chi_tiet_don_hang i JOIN hoa_don h ON"
                                            + " h.don_hang_id=i.don_hang_id WHERE h.so_hoa_don=?",
                                    hoaDon.soHoaDon());
                    DoiTraService.traHang(muc, 1, "Test return");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(8);
                    assertThatThrownBy(() -> DoiTraService.traHang(muc, 2, "Too many"))
                            .hasMessageContaining("vượt");
                });
    }

    /** Lấy cài đặt và nhật ký hệ thống là đã tải lại từ SQL trong TichHopSQLServerTest. */
    @Test
    void layCaiDatVaNhatKyHeThongLaDaTaiLaiTuSQL() {
        layTinhHuong(
                () -> {
                    CaiDatService.capNhat(10, 5, 45, 70000, 14, 1000000);
                    assertThat(CaiDatService.layVATPhanTram()).isEqualTo(10);
                    assertThat(CaiDatService.layHanSuDungCanhBaoSoNgay()).isEqualTo(45);
                    assertThat(CaiDatService.layPhiTraPhongTreMoiNgay()).isEqualTo(70000);
                    NhatKyHeThongService.ghiNhatKy("Thêm", "Integration audit");
                    assertThat(NhatKyHeThongService.layCacMuc())
                            .anyMatch(
                                    suKien ->
                                            suKien.doiTuong().equals("Integration audit")
                                                    && suKien.nguoiDung()
                                                            .equals("Integration Manager")
                                                    && suKien.kieu().equals("CREATE"));
                });
    }

    /**
     * Lấy các báo cáo sử dụng thực tế dữ liệu và phạm vi cá nhân tài khoản trong
     * TichHopSQLServerTest.
     */
    @Test
    void layCacBaoCaoSuDungThucTeDuLieuVaPhamViCaNhanTaiKhoan() {
        layTinhHuong(
                () -> {
                    // Cá nhân.
                    PhienDangNhap caNhan =
                            new PhienDangNhap(
                                    quanLy.taiKhoan(),
                                    quanLy.nhanVien(),
                                    Set.of(
                                            Quyen.REPORT_VIEW_OWN_SHIFT,
                                            Quyen.PAYMENT_PROCESS,
                                            Quyen.CASH_SHIFT_MANAGE));
                    datPhienDangNhap(caNhan);
                    assertThat(ThongKeService.layDoanhThuTheoNgay(7)).isEmpty();
                    mo();
                    // Bán hàng.
                    GioHang banHang = layGioHang(1);
                    // Tổng.
                    long tong = banHang.tinhTong();
                    banHang.hoanTat(CaThuNganService.PhuongThuc.CASH, 200000, "Manager");
                    assertThat(ThongKeService.layDoanhThuTheoNgay(7)).hasSize(1);
                    assertThat(
                                    ((Number) ThongKeService.layDoanhThuTheoNgay(7).get(0)[1])
                                            .longValue())
                            .isEqualTo(tong);
                    assertThatThrownBy(() -> ThongKeService.layKhachHang(7))
                            .hasMessageContaining("không có quyền");
                    assertThatThrownBy(
                                    () ->
                                            DanhMucService.luu(
                                                    DanhMucService.layKhachHang(),
                                                    null,
                                                    new Object[] {
                                                        "X",
                                                        "X",
                                                        "0123456789",
                                                        "",
                                                        "",
                                                        "",
                                                        "Đồng",
                                                        "0",
                                                        "Đang hoạt động"
                                                    }))
                            .hasMessageContaining("không có quyền");
                });
    }

    /** Lấy mỗi đã đăng ký màn hình khởi tạo trên thực tế SQL trong TichHopSQLServerTest. */
    @Test
    void layMoiDaDangKyManHinhKhoiTaoTrenThucTeSQL() {
        layTinhHuong(
                () -> {
                    // Bộ đăng ký.
                    DangKyManHinh boDangKy = new DangKyManHinh(quanLy, boQua -> {});
                    // Vùng hiển thị các kiểu.
                    java.util.Set<Class<?>> vungHienThiCacKieu = new java.util.HashSet<>();
                    for (MaManHinh manHinh : MaManHinh.values()) {
                        assertThatCode(
                                        () -> {
                                            // Vùng hiển thị.
                                            var pnlVungHienThi = boDangKy.tao(manHinh);
                                            assertThat(pnlVungHienThi)
                                                    .as(manHinh.name())
                                                    .isNotNull();
                                            assertThat(
                                                            vungHienThiCacKieu.add(
                                                                    pnlVungHienThi.getClass()))
                                                    .as("Panel riêng cho " + manHinh)
                                                    .isTrue();
                                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp
                                            // ứng.
                                            if (pnlVungHienThi instanceof HoTroThanhCongCu hoTro)
                                                hoTro.xuLyHienThi();
                                            // Thực hiện bước tiếp theo khi điều kiện xử lý được đáp
                                            // ứng.
                                            if (manHinh == MaManHinh.INVENTORY
                                                    || manHinh == MaManHinh.LOYALTY_VOUCHER
                                                    || manHinh == MaManHinh.ACCOUNT_PERMISSION) {
                                                // Các tab.
                                                var tabCacTab =
                                                        layCacThanhPhanCon(pnlVungHienThi)
                                                                .filter(
                                                                        javax.swing.JTabbedPane
                                                                                        .class
                                                                                ::isInstance)
                                                                .map(
                                                                        javax.swing.JTabbedPane
                                                                                        .class
                                                                                ::cast)
                                                                .findFirst()
                                                                .orElseThrow();
                                                assertThat(tabCacTab.getTabCount()).isEqualTo(2);
                                                for (int chiSo = 0;
                                                        chiSo < tabCacTab.getTabCount();
                                                        chiSo++) tabCacTab.setSelectedIndex(chiSo);
                                            }
                                            // Thao tác.
                                            String thaoTac =
                                                    switch (manHinh) {
                                                        case SPA_SCHEDULE -> "+ Đặt lịch hẹn";
                                                        case SPA_CLOSE -> "Thanh toán";
                                                        case HOTEL_BOOKING -> "+ Đặt phòng";
                                                        case CHECKOUT -> "Quyết toán";
                                                        case PURCHASE -> "Trả công nợ";
                                                        case PENDING_INVOICES -> "Mở lại đơn";
                                                        case ORDERS -> "Xem / In lại";
                                                        default -> null;
                                                    };
                                            // Kiểm tra dữ liệu hiện có trước khi tiếp tục xử lý.
                                            if (thaoTac != null)
                                                assertThat(
                                                                layCacThanhPhanCon(pnlVungHienThi)
                                                                        .filter(
                                                                                javax.swing
                                                                                                .AbstractButton
                                                                                                .class
                                                                                        ::isInstance)
                                                                        .map(
                                                                                javax.swing
                                                                                                .AbstractButton
                                                                                                .class
                                                                                        ::cast)
                                                                        .filter(
                                                                                btnNut ->
                                                                                        thaoTac
                                                                                                .equals(
                                                                                                        btnNut
                                                                                                                .getText()))
                                                                        .anyMatch(
                                                                                btnNut ->
                                                                                        btnNut
                                                                                                        .getActionListeners()
                                                                                                        .length
                                                                                                > 0))
                                                        .as("Hành động " + manHinh)
                                                        .isTrue();
                                        })
                                .as(manHinh.name())
                                .doesNotThrowAnyException();
                    }
                });
    }

    /**
     * Lấy các thành phần con trong TichHopSQLServerTest.
     *
     * @param thanhPhan thành phần
     * @return kết quả lấy các thành phần con
     */
    private static java.util.stream.Stream<java.awt.Component> layCacThanhPhanCon(
            java.awt.Component thanhPhan) {
        // Bản thân.
        var banThan = java.util.stream.Stream.of(thanhPhan);
        // Kết thúc nhánh xử lý khi điều kiện đã được xác định.
        if (!(thanhPhan instanceof java.awt.Container vungChua)) return banThan;
        return java.util.stream.Stream.concat(
                banThan,
                java.util.Arrays.stream(vungChua.getComponents())
                        .flatMap(TichHopSQLServerTest::layCacThanhPhanCon));
    }

    /** Lấy các giỏ hàng treo tải lại khách hàng và giá hiện tại trong TichHopSQLServerTest. */
    @Test
    void layCacGioHangTreoTaiLaiKhachHangVaGiaHienTai() {
        layTinhHuong(
                () -> {
                    // Ban đầu.
                    GioHang banDau = layGioHang(2);
                    // Đã treo.
                    long daTreo = BanHangService.treo(banDau);
                    capNhat("UPDATE san_pham SET gia_ban=120000 WHERE id=?", sanPham);
                    // Đã khôi phục.
                    GioHang daKhoiPhuc = new GioHang();
                    BanHangService.moLai(daTreo, daKhoiPhuc);
                    assertThat(daKhoiPhuc.layMaKhachHang()).isEqualTo(maKhachHang);
                    assertThat(daKhoiPhuc.layCacDong()).hasSize(1);
                    assertThat(daKhoiPhuc.layCacDong().get(0).soLuong()).isEqualTo(2);
                    assertThat(daKhoiPhuc.layCacDong().get(0).donGia()).isEqualTo(120000);
                    assertThat(laySo("SELECT COUNT(*) FROM gio_hang_treo WHERE id=?", daTreo))
                            .isZero();
                    assertThatThrownBy(() -> BanHangService.moLai(daTreo, new GioHang()))
                            .hasMessageContaining("Không tìm thấy");
                });
    }

    /**
     * Lấy không đồng còn phải thu Hotel có thể vẫn được đã quyết toán trong TichHopSQLServerTest.
     */
    @Test
    void layKhongDongConPhaiThuHotelCoTheVanDuocDaQuyetToan() {
        layTinhHuong(
                () -> {
                    mo();
                    // Hợp đồng lưu trú.
                    long hopDongLuuTru =
                            LuuTruService.datPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    150000,
                                    "");
                    LuuTruService.kiemTraVao(hopDongLuuTru);
                    // Hóa đơn.
                    GioHang hoaDon = new GioHang();
                    BanHangService.quyetToan("HOTEL", hopDongLuuTru, hoaDon);
                    assertThat(hoaDon.tinhTong()).isZero();
                    assertThat(
                                    hoaDon.hoanTat(CaThuNganService.PhuongThuc.CASH, 0, "Manager")
                                            .tong())
                            .isZero();
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                            + " id=?",
                                                    hopDongLuuTru)))
                            .isEqualTo("CHECKED_OUT");
                });
    }

    /**
     * Lấy hết hạn tồn kho và đã thay đổi các giá không thể được đã bán trong TichHopSQLServerTest.
     */
    @Test
    void layHetHanTonKhoVaDaThayDoiCacGiaKhongTheDuocDaBan() {
        layTinhHuong(
                () -> {
                    mo();
                    capNhat(
                            "UPDATE lo_hang SET han_su_dung=DATEADD(day,-1,GETDATE()) WHERE"
                                    + " san_pham_id=?",
                            sanPham);
                    assertThatThrownBy(
                                    () ->
                                            layGioHang(1)
                                                    .hoanTat(
                                                            CaThuNganService.PhuongThuc.CASH,
                                                            200000,
                                                            "Manager"))
                            .hasMessageContaining("Không đủ tồn");
                    capNhat(
                            "UPDATE lo_hang SET han_su_dung=DATEADD(day,30,GETDATE()) WHERE"
                                    + " san_pham_id=?",
                            sanPham);
                    // Đã cũ.
                    GioHang daCu = layGioHang(1);
                    capNhat("UPDATE san_pham SET gia_ban=110000 WHERE id=?", sanPham);
                    assertThatThrownBy(
                                    () ->
                                            daCu.hoanTat(
                                                    CaThuNganService.PhuongThuc.CASH,
                                                    200000,
                                                    "Manager"))
                            .hasMessageContaining("đã đổi");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(6);
                });
    }

    /**
     * Lấy danh mục các bộ soạn thảo và mỗi báo cáo truy vấn sử dụng được chọn thống nhất cấu trúc
     * CSDL trong TichHopSQLServerTest.
     */
    @Test
    void layDanhMucCacBoSoanThaoVaMoiBaoCaoTruyVanSuDungDuocChonThongNhatCauTrucCSDL() {
        layTinhHuong(
                () -> {
                    // Các danh mục.
                    List<DanhMucService.CacDong> cacDanhMuc =
                            List.of(
                                    DanhMucService.layThuCung(),
                                    DanhMucService.layNhanVien(),
                                    DanhMucService.layTaiKhoan(),
                                    DanhMucService.layNhaCungCap(),
                                    DanhMucService.layKhuyenMai(),
                                    DanhMucService.layVoucher(),
                                    DanhMucService.layDichVuSpa(),
                                    DanhMucService.layPhong());
                    for (DanhMucService.CacDong danhMuc : cacDanhMuc) {
                        // Ban đầu.
                        Object[] banDau = danhMuc.get(0).clone();
                        // Chọn cách xử lý tương ứng với trạng thái hoặc loại đang xét.
                        if (danhMuc.loai == LoaiDanhMuc.ACCOUNTS
                                && "Hoạt động".equals(banDau[3])
                                && !maNhanVien.equals(banDau[0])) continue;
                        DanhMucService.luu(danhMuc, banDau, banDau.clone());
                        assertThatCode(danhMuc::lamMoi)
                                .as(danhMuc.loai.name())
                                .doesNotThrowAnyException();
                    }
                    for (String nhomTongHop : List.of("method", "staff", "shift", "source"))
                        assertThat(ThongKeService.layDoanhThu(7, nhomTongHop)).isNotNull();
                    assertThat(ThongKeService.layDuLieuHotel(7)).isNotNull();
                    assertThat(ThongKeService.layLichHen(7)).isNotNull();
                    assertThat(ThongKeService.layVoucher()).isNotNull();
                    assertThat(ThongKeService.layTonKho()).isNotNull();
                    assertThat(ThongKeService.layLoSapHetHan()).isNotNull();
                    assertThat(ThongKeService.layCongNoNhaCungCap(7)).isNotNull();
                    assertThat(ThongKeService.layNhanVien(7)).isNotNull();
                });
    }
}
