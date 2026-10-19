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
                            + " VALUES(?,?,N'Phụ kiện',N'Cái',100000,50000,1)",
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
                "INSERT INTO thu_cung(ma_thu_cung,khach_hang_id,ten,loai,can_nang,ngay_sinh)"
                    + " VALUES(?,?,N'Integration Pet','DOG',4,DATEADD(month,-6,CAST(GETDATE() AS"
                    + " date)))",
                maThuCung,
                khachHang);
        capNhat(
                "INSERT INTO phong_hotel(ma_phong,loai_phong,gia_dem,trang_thai) VALUES(?,N'Tiêu"
                        + " chuẩn',150000,'AVAILABLE')",
                maPhong);
        long idThuCung = laySo("SELECT id FROM thu_cung WHERE ma_thu_cung=?", maThuCung);
        capNhat(
                "INSERT tiem_phong(thu_cung_id,ten_vaccine,ngay_tiem) VALUES(?,N'Mũi"
                        + " 1',DATEADD(day,-50,CAST(GETDATE() AS date))),(?,N'Mũi"
                        + " 2',DATEADD(day,-14,CAST(GETDATE() AS date)))",
                idThuCung,
                idThuCung);
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
                    assertThatThrownBy(() -> banHang.datDiemDaDung(2))
                            .hasMessageContaining("không trừ trực tiếp");
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
                    hoaDon.xacNhanDaNhanTien(CaThuNganService.PhuongThuc.TRANSFER, true);
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
                                    maThuCung, maPhong, batDau, ketThuc, 150000, "Test", true);
                    assertThatThrownBy(
                                    () ->
                                            LuuTruService.datPhong(
                                                    maThuCung, maPhong, batDau, ketThuc, 150000, "",
                                                    true))
                            .hasMessageContaining("trùng");
                    LuuTruService.kiemTraVao(hopDongLuuTru);
                    LuuTruService.ghiNhatKy(hopDongLuuTru, "Ate", "Clean", "Healthy");
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM nhat_ky_cham_soc WHERE"
                                                    + " hop_dong_id=?",
                                            hopDongLuuTru))
                            .isEqualTo(1);
                    assertThat(LuuTruService.tinhTienLuuTruConLai(hopDongLuuTru)).isEqualTo(150000);
                    // Hóa đơn.
                    GioHang hoaDon = new GioHang();
                    BanHangService.quyetToan("HOTEL", hopDongLuuTru, hoaDon);
                    hoaDon.xacNhanDaNhanTien(CaThuNganService.PhuongThuc.CARD, true);
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
                    GioHang doiHang = new GioHang();
                    DoiTraService.lapDoiHang(doiHang, muc, 1, maSanPham, 1, "Đổi phụ kiện");
                    doiHang.hoanTat(
                            CaThuNganService.PhuongThuc.CASH, doiHang.tinhTong(), "Manager");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(7);
                    assertThatThrownBy(
                                    () ->
                                            DoiTraService.lapDoiHang(
                                                    new GioHang(),
                                                    muc,
                                                    2,
                                                    maSanPham,
                                                    2,
                                                    "Quá số lượng"))
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
                                                            (manHinh == MaManHinh.SPA_INTAKE
                                                                    || vungHienThiCacKieu.add(
                                                                            pnlVungHienThi
                                                                                    .getClass())))
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
                                    75000,
                                    "",
                                    true);
                    capNhat(
                            "UPDATE hop_dong_luu_tru SET tien_coc=150000 WHERE id=?",
                            hopDongLuuTru);
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

    private void doiVaiTro(VaiTro vaiTro) {
        TaiKhoan tk = quanLy.taiKhoan();
        datPhienDangNhap(
                new PhienDangNhap(
                        new TaiKhoan(
                                tk.id(),
                                tk.idNhanVien(),
                                tk.tenDangNhap(),
                                tk.maBamMatKhau(),
                                vaiTro,
                                tk.trangThai(),
                                0,
                                null,
                                false),
                        quanLy.nhanVien(),
                        com.petstation.service.QuyenVaiTroService.tao(vaiTro)));
    }

    @Test
    void doiVoucherDungBien199200VaKhongTruLap() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE khach_hang SET diem_tich_luy=199 WHERE id=?", khachHang);
                    String khoa = "IT-" + UUID.randomUUID();
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.TichDiemService.doiVoucher(
                                                    maKhachHang, khoa))
                            .hasMessageContaining("200");
                    capNhat("UPDATE khach_hang SET diem_tich_luy=200 WHERE id=?", khachHang);
                    String ma =
                            com.petstation.service.TichDiemService.doiVoucher(maKhachHang, khoa);
                    assertThat(com.petstation.service.TichDiemService.doiVoucher(maKhachHang, khoa))
                            .isEqualTo(ma);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isZero();
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM lich_su_diem WHERE"
                                                    + " khach_hang_id=? AND thay_doi=-200",
                                            khachHang))
                            .isEqualTo(1);
                    assertThat(laySo("SELECT menh_gia FROM voucher WHERE ma_voucher=?", ma))
                            .isEqualTo(150000);
                    assertThat(
                                    laySo(
                                            "SELECT DATEDIFF(day,SYSDATETIME(),hieu_luc_den) FROM"
                                                    + " voucher WHERE ma_voucher=?",
                                            ma))
                            .isEqualTo(60);
                });
    }

    @Test
    void voucherChanSaiChuSaiNguonHetHanDaDungVaThanhToanKhongLap() {
        layTinhHuong(
                () -> {
                    mo();
                    capNhat("UPDATE khach_hang SET diem_tich_luy=200 WHERE id=?", khachHang);
                    String ma =
                            com.petstation.service.TichDiemService.doiVoucher(
                                    maKhachHang, "IT-" + UUID.randomUUID());
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.TichDiemService.kiemTraVoucher(
                                                    ma, "SAI", "SPA", 150000))
                            .hasMessageContaining("không thuộc");
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.TichDiemService.kiemTraVoucher(
                                                    ma, maKhachHang, "", 150000))
                            .hasMessageContaining("Spa");
                    capNhat(
                            "UPDATE voucher SET hieu_luc_den=DATEADD(day,-1,SYSDATETIME()) WHERE"
                                    + " ma_voucher=?",
                            ma);
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.TichDiemService.kiemTraVoucher(
                                                    ma, maKhachHang, "SPA", 150000))
                            .hasMessageContaining("hết hạn");
                    capNhat(
                            "UPDATE voucher SET hieu_luc_den=DATEADD(day,60,SYSDATETIME()) WHERE"
                                    + " ma_voucher=?",
                            ma);
                    long spa = SpaService.tiepNhan(maThuCung, null, 4, "Đủ sức khỏe");
                    SpaService.themDichVuSpa(spa, maGoiDichVu);
                    SpaService.layTrangThaiSpa(spa, "IN_PROGRESS");
                    SpaService.layTrangThaiSpa(spa, "COMPLETED");
                    GioHang gio = new GioHang();
                    BanHangService.quyetToan("SPA", spa, gio);
                    assertThat(gio.apDungVoucher(ma)).isNull();
                    assertThat(gio.tinhTong()).isZero();
                    String hd = gio.hoanTat(CaThuNganService.PhuongThuc.CASH, 0, "Test").soHoaDon();
                    assertThat(gio.hoanTat(CaThuNganService.PhuongThuc.CASH, 0, "Test").soHoaDon())
                            .isEqualTo(hd);
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", ma))
                            .isEqualTo(1);
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.TichDiemService.kiemTraVoucher(
                                                    ma, maKhachHang, "SPA", 150000))
                            .hasMessageContaining("hết lượt");
                });
    }

    @Test
    void spaChanThieuNgaySinhChuaDuTuoiThieuMuiVaMuiQuaGan() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE thu_cung SET ngay_sinh=NULL WHERE ma_thu_cung=?", maThuCung);
                    assertThatThrownBy(() -> SpaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("ngày sinh");
                    capNhat(
                            "UPDATE thu_cung SET ngay_sinh=DATEADD(month,-1,CAST(GETDATE() AS"
                                    + " date)) WHERE ma_thu_cung=?",
                            maThuCung);
                    assertThatThrownBy(() -> SpaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("2 tháng");
                    capNhat(
                            "UPDATE thu_cung SET ngay_sinh=DATEADD(month,-2,CAST(GETDATE() AS"
                                    + " date)) WHERE ma_thu_cung=?",
                            maThuCung);
                    capNhat(
                            "DELETE tiem_phong WHERE id=(SELECT MAX(tp.id) FROM tiem_phong tp JOIN"
                                    + " thu_cung t ON t.id=tp.thu_cung_id WHERE t.ma_thu_cung=?)",
                            maThuCung);
                    assertThatThrownBy(() -> SpaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("2 mũi");
                    com.petstation.service.TiemPhongService.luu(
                            maThuCung, 0, "Mũi bổ sung", LocalDate.now().minusDays(13), null, "");
                    assertThatThrownBy(() -> SpaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("14 ngày");
                    capNhat(
                            "UPDATE tiem_phong SET ngay_tiem=DATEADD(day,-14,CAST(GETDATE() AS"
                                    + " date)) WHERE thu_cung_id=(SELECT id FROM thu_cung WHERE"
                                    + " ma_thu_cung=?) AND ten_vaccine=N'Mũi bổ sung'",
                            maThuCung);
                    assertThat(SpaService.tiepNhan(maThuCung, null, 4, "")).isPositive();
                });
    }

    @Test
    void hotelChanThieuXacNhanSaiCocVaChiDonPhongSauTraPhong() {
        layTinhHuong(
                () -> {
                    mo();
                    LocalDateTime tu = LocalDateTime.now(), den = tu.plusDays(1);
                    assertThatThrownBy(
                                    () ->
                                            LuuTruService.datPhong(
                                                    maThuCung, maPhong, tu, den, 75000, ""))
                            .hasMessageContaining("xác nhận");
                    assertThatThrownBy(
                                    () ->
                                            LuuTruService.datPhong(
                                                    maThuCung, maPhong, tu, den, 0, "", true))
                            .hasMessageContaining("50%");
                    long id = LuuTruService.datPhong(maThuCung, maPhong, tu, den, 75000, "", true);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT da_nhan_coc FROM hop_dong_luu_tru WHERE id=?",
                                            id))
                            .isEqualTo(true);
                    long phong = laySo("SELECT id FROM phong_hotel WHERE ma_phong=?", maPhong);
                    assertThatThrownBy(() -> LuuTruService.hoanTatDonPhong(phong))
                            .hasMessageContaining("đang dọn");
                    LuuTruService.kiemTraVao(id);
                    assertThatThrownBy(() -> LuuTruService.kiemTraVao(id))
                            .hasMessageContaining("không còn");
                    GioHang gio = new GioHang();
                    BanHangService.quyetToan("HOTEL", id, gio);
                    gio.hoanTat(CaThuNganService.PhuongThuc.CASH, 75000, "Test");
                    assertThatThrownBy(() -> BanHangService.quyetToan("HOTEL", id, new GioHang()))
                            .hasMessageContaining("quyết toán");
                    LuuTruService.hoanTatDonPhong(phong);
                    assertThat(layGiaTriDon("SELECT trang_thai FROM phong_hotel WHERE id=?", phong))
                            .isEqualTo("AVAILABLE");
                });
    }

    @Test
    void giaChiDoiSauQuanLyDuyetVaKhongDuyetLap() {
        layTinhHuong(
                () -> {
                    doiVaiTro(VaiTro.WAREHOUSE_STAFF);
                    var yc =
                            com.petstation.service.DeXuatGiaService.gui(
                                    maSanPham, 120000, "Điều chỉnh theo giá nhập");
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(100000);
                    assertThatThrownBy(() -> PheDuyetService.duyet(yc, "Kho"))
                            .isInstanceOf(com.petstation.service.PhanQuyenException.class);
                    datPhienDangNhap(quanLy);
                    PheDuyetService.duyet(yc, "Quản lý");
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(120000);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM lich_su_gia WHERE san_pham_id=?",
                                            sanPham))
                            .isEqualTo(1);
                    assertThatThrownBy(() -> PheDuyetService.duyet(yc, "Quản lý"))
                            .hasMessageContaining("đã được xử lý");
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM lich_su_gia WHERE san_pham_id=?",
                                            sanPham))
                            .isEqualTo(1);
                });
    }

    @Test
    void tuChoiGiaKhongDoiGiaVaKhoKhongSuaGiaQuaDanhMuc() {
        layTinhHuong(
                () -> {
                    doiVaiTro(VaiTro.WAREHOUSE_STAFF);
                    var cacDong = DanhMucService.laySanPham();
                    Object[] cu =
                            cacDong.stream()
                                    .filter(d -> d[0].equals(maSanPham))
                                    .findFirst()
                                    .orElseThrow();
                    Object[] moi = cu.clone();
                    moi[4] = "999999";
                    assertThatThrownBy(() -> DanhMucService.luu(cacDong, cu, moi))
                            .isInstanceOf(com.petstation.service.PhanQuyenException.class);
                    var yc =
                            com.petstation.service.DeXuatGiaService.gui(
                                    maSanPham, 120000, "Đề xuất thử");
                    datPhienDangNhap(quanLy);
                    PheDuyetService.tuChoi(yc, "Quản lý", "Giữ giá hiện tại");
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(100000);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM lich_su_gia WHERE san_pham_id=?",
                                            sanPham))
                            .isZero();
                    assertThatThrownBy(() -> PheDuyetService.duyet(yc, "Quản lý"))
                            .hasMessageContaining("đã được xử lý");
                });
    }

    @Test
    void doiHangThuChenhLechDiemTonHaiChieuVaBaoCaoKhongNhanDoi() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE chuong_trinh_khuyen_mai SET trang_thai='INACTIVE'");
                    mo();
                    GioHang gio = layGioHang(2);
                    gio.apDungVoucher(voucher);
                    long doanhThuTruoc =
                            ThongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> ((Number) d[1]).longValue())
                                    .sum();
                    var bienLai = gio.hoanTat(CaThuNganService.PhuongThuc.CASH, 200000, "Test");
                    long dong =
                            laySo(
                                    "SELECT i.id FROM chi_tiet_don_hang i JOIN hoa_don h ON"
                                            + " h.don_hang_id=i.don_hang_id WHERE h.so_hoa_don=?",
                                    bienLai.soHoaDon());
                    GioHang doi = new GioHang();
                    DoiTraService.lapDoiHang(doi, dong, 1, maSanPham, 1, "Đổi kích cỡ");
                    assertThat(doi.tinhTong()).isEqualTo(5000);
                    var thu = doi.hoanTat(CaThuNganService.PhuongThuc.CASH, 5000, "Test");
                    assertThat(
                                    doi.hoanTat(CaThuNganService.PhuongThuc.CASH, 5000, "Test")
                                            .soHoaDon())
                            .isEqualTo(thu.soHoaDon());
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(4);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isEqualTo(119);
                    assertThat(com.petstation.gui.processing.sales.InHoaDonPanel.hienThi(thu))
                            .contains("Giá trị hàng được đổi", "HĐ gốc", bienLai.soHoaDon());
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1195000);
                    long doanhThuSau =
                            ThongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> ((Number) d[1]).longValue())
                                    .sum();
                    assertThat(doanhThuSau - doanhThuTruoc).isEqualTo(195000);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM phieu_doi_tra WHERE"
                                                    + " khoa_thao_tac=?",
                                            doi.layKhoaThanhToan()))
                            .isEqualTo(1);
                });
    }

    @Test
    void doiHangChanSaiNhomQuaHanThayTheThapHonVaHoanTacKhiHetTon() {
        layTinhHuong(
                () -> {
                    mo();
                    GioHang gio = layGioHang(2);
                    var hd = gio.hoanTat(CaThuNganService.PhuongThuc.CASH, 200000, "Test");
                    long dong =
                            laySo(
                                    "SELECT i.id FROM chi_tiet_don_hang i JOIN hoa_don h ON"
                                            + " h.don_hang_id=i.don_hang_id WHERE h.so_hoa_don=?",
                                    hd.soHoaDon());
                    capNhat("UPDATE san_pham SET nhom_hang=N'Thức ăn' WHERE id=?", sanPham);
                    assertThatThrownBy(
                                    () ->
                                            DoiTraService.lapDoiHang(
                                                    new GioHang(), dong, 1, maSanPham, 1, "Đổi"))
                            .hasMessageContaining("Phụ kiện");
                    capNhat("UPDATE san_pham SET nhom_hang=N'Phụ kiện' WHERE id=?", sanPham);
                    capNhat(
                            "UPDATE hoa_don SET thanh_toan_luc=DATEADD(day,-4,SYSDATETIME()) WHERE"
                                    + " so_hoa_don=?",
                            hd.soHoaDon());
                    assertThatThrownBy(
                                    () ->
                                            DoiTraService.lapDoiHang(
                                                    new GioHang(), dong, 1, maSanPham, 1, "Đổi"))
                            .hasMessageContaining("ba ngày");
                    capNhat(
                            "UPDATE hoa_don SET thanh_toan_luc=SYSDATETIME() WHERE so_hoa_don=?",
                            hd.soHoaDon());
                    assertThatThrownBy(
                                    () ->
                                            DoiTraService.lapDoiHang(
                                                    new GioHang(), dong, 2, maSanPham, 1, "Đổi"))
                            .hasMessageContaining("không được thấp");
                    GioHang doi = new GioHang();
                    DoiTraService.lapDoiHang(doi, dong, 1, maSanPham, 6, "Đổi");
                    long diem = laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang),
                            soPhieu = laySo("SELECT COUNT(*) FROM phieu_doi_tra");
                    assertThatThrownBy(
                                    () ->
                                            doi.hoanTat(
                                                    CaThuNganService.PhuongThuc.CASH,
                                                    1000000,
                                                    "Test"))
                            .hasMessageContaining("Không đủ tồn");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(4);
                    assertThat(laySo("SELECT COUNT(*) FROM phieu_doi_tra")).isEqualTo(soPhieu);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isEqualTo(diem);
                });
    }

    @Test
    void thanhToanChuyenKhoanCanXacNhanVaDoiVoucherPhaiXacNhanLai() {
        layTinhHuong(
                () -> {
                    mo();
                    GioHang gio = layGioHang(1);
                    assertThatThrownBy(
                                    () ->
                                            gio.hoanTat(
                                                    CaThuNganService.PhuongThuc.TRANSFER,
                                                    gio.tinhTong(),
                                                    "Test"))
                            .hasMessageContaining("xác nhận");
                    gio.xacNhanDaNhanTien(CaThuNganService.PhuongThuc.TRANSFER, true);
                    assertThat(gio.apDungVoucher(voucher)).isNull();
                    assertThatThrownBy(
                                    () ->
                                            gio.hoanTat(
                                                    CaThuNganService.PhuongThuc.TRANSFER,
                                                    gio.tinhTong(),
                                                    "Test"))
                            .hasMessageContaining("xác nhận");
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isZero();
                    gio.xacNhanDaNhanTien(CaThuNganService.PhuongThuc.TRANSFER, true);
                    assertThat(
                                    gio.hoanTat(
                                                    CaThuNganService.PhuongThuc.TRANSFER,
                                                    gio.tinhTong(),
                                                    "Test")
                                            .tong())
                            .isEqualTo(gio.tinhTong());
                });
    }

    @Test
    void quyTrinhGiuDungPhieuVaChanChuyenBuocTruocDieuKien() {
        layTinhHuong(
                () -> {
                    mo();
                    GioHang gio = new GioHang();
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.QuyTrinhService.chuyenBuoc(
                                                    "SPA_INTAKE", "SPA_SERVICE_SELECTION", gio))
                            .hasMessageContaining("chọn");
                    long spa = SpaService.tiepNhan(maThuCung, null, 4, "");
                    gio.layQuyTrinh().chonSpa(spa);
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.QuyTrinhService.chuyenBuoc(
                                                    "SPA_SERVICE_SELECTION", "SPA_PROGRESS", gio))
                            .hasMessageContaining("thêm dịch vụ");
                    SpaService.themDichVuSpa(spa, maGoiDichVu);
                    assertThatThrownBy(
                                    () ->
                                            com.petstation.service.QuyTrinhService.chuyenBuoc(
                                                    "SPA_PROGRESS", "SPA_CLOSE", gio))
                            .hasMessageContaining("hoàn thành");
                    SpaService.layTrangThaiSpa(spa, "IN_PROGRESS");
                    SpaService.layTrangThaiSpa(spa, "COMPLETED");
                    com.petstation.service.QuyTrinhService.chuyenBuoc("SPA_CLOSE", "PAYMENT", gio);
                    assertThat(gio.layIdNguon()).isEqualTo(spa);
                    assertThat(gio.tinhTamTinh()).isEqualTo(150000);
                    gio.hoanTat(CaThuNganService.PhuongThuc.CASH, 150000, "Test");
                    assertThat(gio.layQuyTrinh().layPhieuSpa()).isZero();
                    assertThatThrownBy(() -> SpaService.layTrangThaiSpa(spa, "COMPLETED"))
                            .hasMessageContaining("trạng thái");
                });
    }

    @Test
    void spaTrongNgayKhongLayPhieuNgayCu() {
        layTinhHuong(
                () -> {
                    long spa = SpaService.tiepNhan(maThuCung, null, 4, "");
                    assertThat(NghiepVuService.layCacDong(LoaiNghiepVu.SPA_TICKETS))
                            .anyMatch(d -> ((Number) d[0]).longValue() == spa);
                    capNhat(
                            "UPDATE phieu_tiep_nhan_spa SET tao_luc=DATEADD(day,-1,SYSDATETIME())"
                                    + " WHERE id=?",
                            spa);
                    assertThat(NghiepVuService.layCacDong(LoaiNghiepVu.SPA_TICKETS))
                            .noneMatch(d -> ((Number) d[0]).longValue() == spa);
                    assertThat(NghiepVuService.layCacDong(LoaiNghiepVu.SPA_PROGRESS))
                            .anyMatch(d -> ((Number) d[0]).longValue() == spa);
                });
    }

    private static java.util.List<java.awt.Component> cacThanhPhan(java.awt.Container cha) {
        java.util.List<java.awt.Component> ketQua = new java.util.ArrayList<>();
        for (var con : cha.getComponents()) {
            ketQua.add(con);
            if (con instanceof java.awt.Container v) ketQua.addAll(cacThanhPhan(v));
        }
        return ketQua;
    }

    private static void bam(java.awt.Container cha, String nhan) {
        var nut =
                cacThanhPhan(cha).stream()
                        .filter(
                                c ->
                                        c instanceof javax.swing.AbstractButton b
                                                && b.getText().equals(nhan))
                        .map(c -> (javax.swing.AbstractButton) c)
                        .findFirst()
                        .orElseThrow();
        assertThat(nut.isEnabled()).as(nhan).isTrue();
        nut.doClick();
    }

    private static void chonMa(java.awt.Container cha, String khoa, String giaTri) {
        var thanhPhan =
                cacThanhPhan(cha).stream()
                        .filter(c -> khoa.equals(c.getName()))
                        .findFirst()
                        .orElseThrow();
        if (thanhPhan instanceof javax.swing.JTextField txt) txt.setText(giaTri);
        else if (thanhPhan instanceof javax.swing.JComboBox<?> cbo)
            for (int i = 0; i < cbo.getItemCount(); i++) {
                if (cbo.getItemAt(i)
                                instanceof
                                com.petstation.gui.components.common.BieuMauSQL.LuaChon lua
                        && lua.giaTri().equals(giaTri)) {
                    cbo.setSelectedIndex(i);
                    return;
                }
            }
    }

    private static void nhapHopThoai(
            String tieuDe,
            java.util.function.Consumer<javax.swing.JDialog> dien,
            Runnable thaoTac) {
        java.util.concurrent.atomic.AtomicReference<Throwable> loi =
                new java.util.concurrent.atomic.AtomicReference<>();
        java.util.concurrent.atomic.AtomicBoolean daDien =
                new java.util.concurrent.atomic.AtomicBoolean();
        javax.swing.Timer timer =
                new javax.swing.Timer(
                        100,
                        e -> {
                            for (java.awt.Window cuaSo : java.awt.Window.getWindows())
                                if (cuaSo instanceof javax.swing.JDialog hop && hop.isShowing()) {
                                    try {
                                        assertThat(Arrays.asList(tieuDe.split("\\|")))
                                                .contains(hop.getTitle());
                                        dien.accept(hop);
                                        daDien.set(true);
                                    } catch (Throwable ex) {
                                        loi.set(ex);
                                    }
                                    for (var c : cacThanhPhan(hop))
                                        if (c instanceof javax.swing.JOptionPane p) {
                                            p.setValue(
                                                    p.getOptions() == null
                                                            ? javax.swing.JOptionPane.OK_OPTION
                                                            : p.getOptions()[0]);
                                            break;
                                        }
                                }
                        });
        timer.start();
        try {
            thaoTac.run();
        } finally {
            timer.stop();
        }
        if (loi.get() != null) throw new AssertionError(loi.get());
        assertThat(daDien.get()).as("Đã nhập " + tieuDe).isTrue();
    }

    private void trenLuongSwing(Runnable viec) throws Exception {
        var loi = new java.util.concurrent.atomic.AtomicReference<Throwable>();
        javax.swing.SwingUtilities.invokeAndWait(
                () -> {
                    try {
                        layTinhHuong(viec);
                    } catch (Throwable ex) {
                        loi.set(ex);
                    } finally {
                        for (var w : java.awt.Window.getWindows()) w.dispose();
                    }
                });
        if (loi.get() != null) throw new AssertionError(loi.get());
    }

    @Test
    void thaoTacSpaTuLichHenDenThanhToanBangGiaoDien() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    capNhat("UPDATE chuong_trinh_khuyen_mai SET trang_thai='INACTIVE'");
                    var dich = new java.util.concurrent.atomic.AtomicReference<MaManHinh>();
                    var dangKy = new DangKyManHinh(quanLy, dich::set);
                    GioHang gio = dangKy.layGioHang();
                    var lich = dangKy.tao(MaManHinh.SPA_SCHEDULE);
                    nhapHopThoai(
                            "Đặt lịch Spa",
                            hop -> {
                                chonMa(hop, "pet", maThuCung);
                                chonMa(hop, "package", maGoiDichVu);
                                chonMa(hop, "staff", maNhanVien);
                            },
                            () -> bam(lich, "+ Đặt lịch hẹn"));
                    assertThat(gio.layQuyTrinh().layLichHen()).isPositive();
                    var tiepNhan = dangKy.tao(MaManHinh.SPA_INTAKE);
                    nhapHopThoai(
                            "Tiếp nhận Spa",
                            hop -> chonMa(hop, "weight", "4"),
                            () -> bam(tiepNhan, "+ Tiếp nhận"));
                    long id = gio.layQuyTrinh().layPhieuSpa();
                    assertThat(id).isPositive();
                    assertThat(dich.get()).isEqualTo(MaManHinh.SPA_SERVICE_SELECTION);
                    var chon = dangKy.tao(MaManHinh.SPA_SERVICE_SELECTION);
                    bam(chon, "Bắt đầu làm");
                    var tienDo = dangKy.tao(MaManHinh.SPA_PROGRESS);
                    bam(tienDo, "Hoàn thành");
                    var dong = dangKy.tao(MaManHinh.SPA_CLOSE);
                    bam(dong, "Thanh toán");
                    assertThat(gio.layIdNguon()).isEqualTo(id);
                    assertThat(dich.get()).isEqualTo(MaManHinh.PAYMENT);
                    var thanhToan = dangKy.tao(MaManHinh.PAYMENT);
                    bam(thanhToan, "Đủ tiền");
                    nhapHopThoai(
                            "Xác nhận thanh toán",
                            hop -> {},
                            () -> bam(thanhToan, "Xác nhận thanh toán (F9)"));
                    assertThat(dich.get()).isEqualTo(MaManHinh.PRINT_INVOICE);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM phieu_tiep_nhan_spa WHERE id=?",
                                            id))
                            .isEqualTo("PAID");
                });
    }

    @Test
    void menu37MucVaCacManHinhDungQuyenCuaBonVaiTro() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    int soMuc =
                            com.petstation.gui.navigation.CauTrucMenu.layCacNhom().stream()
                                    .mapToInt(
                                            n ->
                                                    (int)
                                                            n.cacMuc().stream()
                                                                    .filter(
                                                                            m ->
                                                                                    !m
                                                                                            .kiemTraPhanCach())
                                                                    .count())
                                    .sum();
                    assertThat(soMuc).isEqualTo(37);
                    for (VaiTro vai : VaiTro.values()) {
                        doiVaiTro(vai);
                        var dangKy = new DangKyManHinh(layPhien(), id -> {});
                        for (var nhom : com.petstation.gui.navigation.CauTrucMenu.layCacNhom())
                            for (var muc : nhom.cacMuc()) {
                                if (muc.kiemTraPhanCach()) continue;
                                boolean coQuyen = dangKy.coTheTruyCap(muc.manHinh());
                                if (coQuyen)
                                    assertThatCode(() -> dangKy.tao(muc.manHinh()))
                                            .as(vai + " / " + muc.nhan())
                                            .doesNotThrowAnyException();
                                else
                                    assertThatThrownBy(() -> dangKy.tao(muc.manHinh()))
                                            .as(vai + " / " + muc.nhan())
                                            .isInstanceOf(
                                                    com.petstation.service.PhanQuyenException
                                                            .class);
                            }
                    }
                });
    }

    @Test
    void thaoTacHotelNhanCocChamSocThanhToanVaDonPhongQuaGiaoDien() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    capNhat("UPDATE chuong_trinh_khuyen_mai SET trang_thai='INACTIVE'");
                    long thuTruoc =
                            ThongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> ((Number) d[1]).longValue())
                                    .sum();
                    var dich = new java.util.concurrent.atomic.AtomicReference<MaManHinh>();
                    var dk = new DangKyManHinh(quanLy, dich::set);
                    GioHang gio = dk.layGioHang();
                    var datPhong = dk.tao(MaManHinh.HOTEL_BOOKING);
                    nhapHopThoai(
                            "Đặt phòng lưu trú|Xác nhận nhận cọc",
                            hop -> {
                                if (hop.getTitle().equals("Đặt phòng lưu trú")) {
                                    chonMa(hop, "pet", maThuCung);
                                    chonMa(hop, "room", maPhong);
                                }
                            },
                            () -> bam(datPhong, "+ Đặt phòng"));
                    long id = gio.layQuyTrinh().layHopDongHotel();
                    assertThat(id).isPositive();
                    assertThat(new CaThuNganService().layTienCocHotel()).isEqualTo(75000);
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1075000);
                    bam(datPhong, "Nhận phòng");
                    var nhatKy = dk.tao(MaManHinh.HOTEL_JOURNAL);
                    nhapHopThoai(
                            "Nhật ký chăm sóc",
                            hop -> {
                                chonMa(hop, "food", "Đã ăn");
                                chonMa(hop, "health", "Khỏe mạnh");
                            },
                            () -> bam(nhatKy, "Ghi chăm sóc"));
                    assertThat(LuuTruService.layNhatKyChiTiet(id)).hasSize(1);
                    var traPhong = dk.tao(MaManHinh.CHECKOUT);
                    bam(traPhong, "Quyết toán");
                    assertThat(gio.layIdNguon()).isEqualTo(id);
                    assertThat(gio.tinhTong()).isEqualTo(75000);
                    var thanhToan = dk.tao(MaManHinh.PAYMENT);
                    bam(thanhToan, "Đủ tiền");
                    nhapHopThoai(
                            "Xác nhận thanh toán",
                            hop -> {},
                            () -> bam(thanhToan, "Xác nhận thanh toán (F9)"));
                    long phong = laySo("SELECT id FROM phong_hotel WHERE ma_phong=?", maPhong);
                    assertThat(layGiaTriDon("SELECT trang_thai FROM phong_hotel WHERE id=?", phong))
                            .isEqualTo("CLEANING");
                    nhapHopThoai(
                            "Hoàn thành dọn phòng",
                            hop -> chonMa(hop, "room", Long.toString(phong)),
                            () -> bam(datPhong, "Dọn phòng xong"));
                    assertThat(layGiaTriDon("SELECT trang_thai FROM phong_hotel WHERE id=?", phong))
                            .isEqualTo("AVAILABLE");
                    assertThat(
                                    com.petstation.gui.processing.sales.InHoaDonPanel.hienThi(
                                            gio.layBienLaiCuoi()))
                            .contains("Tiền cọc đã trừ", "Quyết toán Hotel");
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1150000);
                    long thuSau =
                            ThongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> ((Number) d[1]).longValue())
                                    .sum();
                    assertThat(thuSau - thuTruoc).isEqualTo(150000);
                });
    }

    @Test
    void matKetNoiSQLBaoLoiRoVaKhongGhiDuLieu() {
        CauHinhCSDL cauHinh = CauHinhCSDL.nap();
        long truoc = laySo("SELECT COUNT(*) FROM hoa_don");
        try {
            ThaoTacSQL.cauHinh(
                    new KetNoiCSDL(
                            new CauHinhCSDL(
                                    "jdbc:sqlserver://localhost:1;databaseName=PetStationIntegrationTest;encrypt=true;trustServerCertificate=true;loginTimeout=1",
                                    cauHinh.nguoiDung(),
                                    cauHinh.matKhau(),
                                    1)));
            assertThatThrownBy(() -> DanhMucService.laySanPham())
                    .isInstanceOf(com.petstation.dao.TruyCapDuLieuException.class)
                    .hasMessageContaining("SQL Server");
        } finally {
            ThaoTacSQL.cauHinh(new KetNoiCSDL(cauHinh));
        }
        assertThat(laySo("SELECT COUNT(*) FROM hoa_don")).isEqualTo(truoc);
    }

    @Test
    void duLieuDaLuuVanDocDuocSauKhiTaoLaiKetNoi() {
        String ma = "PERSIST-" + UUID.randomUUID().toString().substring(0, 8);
        long id =
                them(
                        "INSERT khach_hang(ma_khach_hang,ho_ten,so_dien_thoai) VALUES(?,N'Kiểm thử"
                                + " lưu bền',?)",
                        ma,
                        "PT" + ma.substring(8));
        try {
            ThaoTacSQL.cauHinh(new KetNoiCSDL(CauHinhCSDL.nap()));
            assertThat(layGiaTriDon("SELECT ho_ten FROM khach_hang WHERE id=?", id))
                    .isEqualTo("Kiểm thử lưu bền");
        } finally {
            capNhat("DELETE khach_hang WHERE id=? AND ma_khach_hang=?", id, ma);
        }
    }

    @Test
    void suaHoSoKhachKhongGhiDeDiemVaHangDangCo() {
        layTinhHuong(
                () -> {
                    var ds = DanhMucService.layKhachHang();
                    Object[] cu =
                            ds.stream()
                                    .filter(d -> d[0].equals(maKhachHang))
                                    .findFirst()
                                    .orElseThrow();
                    capNhat(
                            "UPDATE khach_hang SET diem_tich_luy=300,hang_thanh_vien=N'Vàng' WHERE"
                                    + " id=?",
                            khachHang);
                    Object[] moi = cu.clone();
                    moi[1] = "Sửa tên khách";
                    moi[7] = "0";
                    moi[6] = "Kim Cương";
                    DanhMucService.luu(ds, cu, moi);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isEqualTo(300);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT hang_thanh_vien FROM khach_hang WHERE id=?",
                                            khachHang))
                            .isEqualTo("Vàng");
                });
    }

    @Test
    void danhMucKhongPhaQuyTacVoucherDoiDiemVaVoucherMoiPhaiCoChu() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE khach_hang SET diem_tich_luy=200 WHERE id=?", khachHang);
                    String ma =
                            com.petstation.service.TichDiemService.doiVoucher(
                                    maKhachHang, "IT-" + UUID.randomUUID());
                    var ds = DanhMucService.layVoucher();
                    Object[] cu =
                            ds.stream().filter(d -> d[0].equals(ma)).findFirst().orElseThrow();
                    Object[] moi = cu.clone();
                    moi[3] = "10";
                    moi[8] = "Mọi hóa đơn";
                    assertThatThrownBy(() -> DanhMucService.luu(ds, cu, moi))
                            .hasMessageContaining("Voucher đổi điểm");
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT chi_ap_dung_spa FROM voucher WHERE"
                                                    + " ma_voucher=?",
                                            ma))
                            .isEqualTo(true);
                    assertThat(laySo("SELECT so_luong FROM voucher WHERE ma_voucher=?", ma))
                            .isEqualTo(1);
                    Object[] taoMoi = {
                        "IT-" + UUID.randomUUID(),
                        "10000",
                        LocalDate.now()
                                .plusDays(60)
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu")),
                        "1",
                        "0",
                        "0",
                        "Còn hiệu lực",
                        "",
                        "Chỉ Spa"
                    };
                    assertThatThrownBy(() -> DanhMucService.luu(ds, null, taoMoi))
                            .hasMessageContaining("gắn với");
                    taoMoi[7] = maKhachHang + " - Integration Customer";
                    DanhMucService.luu(ds, null, taoMoi);
                    assertThat(
                                    laySo(
                                            "SELECT khach_hang_id FROM voucher WHERE ma_voucher=?",
                                            taoMoi[0]))
                            .isEqualTo(khachHang);
                    doiVaiTro(VaiTro.SALES_STAFF);
                    assertThatThrownBy(() -> DanhMucService.luu(ds, null, taoMoi))
                            .isInstanceOf(com.petstation.service.PhanQuyenException.class);
                });
    }

    @Test
    void luuAnhVaThongTinChiTietSanPhamKhongDoiGiaVaTon() {
        layTinhHuong(
                () -> {
                    var ds = DanhMucService.laySanPham();
                    Object[] cu =
                            ds.stream()
                                    .filter(d -> d[0].equals(maSanPham))
                                    .findFirst()
                                    .orElseThrow();
                    Object[] moi = cu.clone();
                    moi[10] = "images/products/SP-TA-001.jpg";
                    moi[11] = "Thương hiệu kiểm thử";
                    moi[12] = "Gói 100g";
                    moi[13] = "Chó";
                    moi[14] = "Mô tả có dấu";
                    long ton =
                            laySo(
                                    "SELECT SUM(so_luong_ton) FROM lo_hang WHERE san_pham_id=?",
                                    sanPham);
                    DanhMucService.luu(ds, cu, moi);
                    assertThat(layGiaTriDon("SELECT anh FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(moi[10]);
                    assertThat(layGiaTriDon("SELECT mo_ta FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(moi[14]);
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(ton);
                    moi[10] = "../db.properties";
                    assertThatThrownBy(() -> DanhMucService.luu(ds, cu, moi))
                            .hasMessageContaining("images/products");
                });
    }

    @Test
    void bamAnhTrenTheThemMotLanVaDanhMucTheoDungDongChon() throws Exception {
        trenLuongSwing(
                () -> {
                    capNhat(
                            "UPDATE san_pham SET anh='images/products/SP-TA-001.jpg' WHERE id=?",
                            sanPham);
                    GioHang gio = new GioHang();
                    var pos = new com.petstation.gui.processing.sales.LapHoaDonPanel(gio, id -> {});
                    String ten =
                            String.valueOf(
                                    layGiaTriDon("SELECT ten FROM san_pham WHERE id=?", sanPham));
                    // Chọn đúng thẻ fixture, không phụ thuộc thứ tự sản phẩm SQL.
                    var the =
                            cacThanhPhan(pos).stream()
                                    .filter(
                                            c ->
                                                    c
                                                                    instanceof
                                                                    com.petstation.gui.components
                                                                            .BoGocPanel
                                                            && c.getAccessibleContext() != null
                                                            && ten.equals(
                                                                    c.getAccessibleContext()
                                                                            .getAccessibleName()))
                                    .findFirst()
                                    .orElseThrow();
                    var hinh =
                            cacThanhPhan((java.awt.Container) the).stream()
                                    .filter(
                                            c ->
                                                    c
                                                            instanceof
                                                            com.petstation.gui.components.common
                                                                    .AnhSanPhamPanel)
                                    .findFirst()
                                    .orElseThrow();
                    for (int i = 0; i < 2; i++)
                        hinh.dispatchEvent(
                                new java.awt.event.MouseEvent(
                                        hinh,
                                        java.awt.event.MouseEvent.MOUSE_CLICKED,
                                        System.currentTimeMillis(),
                                        0,
                                        10,
                                        10,
                                        1,
                                        false));
                    assertThat(gio.layCacDong()).hasSize(1);
                    assertThat(gio.layCacDong().get(0).ma()).isEqualTo(maSanPham);
                    assertThat(gio.layCacDong().get(0).soLuong()).isEqualTo(2);
                    var dm = new com.petstation.gui.catalog.DanhSachSanPhamPanel();
                    var bang =
                            (javax.swing.JTable)
                                    cacThanhPhan(dm).stream()
                                            .filter(c -> c instanceof javax.swing.JTable)
                                            .findFirst()
                                            .orElseThrow();
                    for (int i = 0; i < bang.getRowCount(); i++)
                        if (maSanPham.equals(bang.getValueAt(i, 0))) {
                            bang.setRowSelectionInterval(i, i);
                            break;
                        }
                    var xem =
                            (com.petstation.gui.components.common.AnhSanPhamPanel)
                                    cacThanhPhan(dm).stream()
                                            .filter(
                                                    c ->
                                                            c
                                                                    instanceof
                                                                    com.petstation.gui.components
                                                                            .common.AnhSanPhamPanel)
                                            .findFirst()
                                            .orElseThrow();
                    assertThat(xem.layDuongDan()).isEqualTo("images/products/SP-TA-001.jpg");
                    bang.clearSelection();
                    assertThat(xem.layDuongDan()).isEmpty();
                });
    }

    @Test
    void ketCaKiemTraLyDoNguoiNhanVaChongLap() {
        layTinhHuong(
                () -> {
                    var ca = new CaThuNganService();
                    ca.mo("Ca sáng", 500000);
                    assertThatIllegalArgumentException().isThrownBy(() -> ca.dong(499000, ""));
                    assertThatIllegalArgumentException()
                            .isThrownBy(
                                    () ->
                                            ca.dong(
                                                    500000,
                                                    "",
                                                    CaThuNganService.HinhThucBanGiao.BAN_GIAO,
                                                    null));
                    assertThatIllegalArgumentException()
                            .isThrownBy(
                                    () ->
                                            ca.dong(
                                                    500000,
                                                    "",
                                                    CaThuNganService.HinhThucBanGiao.BAN_GIAO,
                                                    quanLy.taiKhoan().id()));
                    long id = ca.layId();
                    ca.dong(
                            499000,
                            "Thiếu tiền lẻ",
                            CaThuNganService.HinhThucBanGiao.NOP_QUY,
                            null);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT hinh_thuc_ban_giao FROM phien_lam_viec WHERE"
                                                    + " id=?",
                                            id))
                            .isEqualTo("NOP_QUY");
                    assertThat(laySo("SELECT chenh_lech FROM phien_lam_viec WHERE id=?", id))
                            .isEqualTo(-1000);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT nguoi_nhan_ban_giao_id FROM phien_lam_viec"
                                                    + " WHERE id=?",
                                            id))
                            .isNull();
                    assertThatIllegalStateException()
                            .isThrownBy(() -> ca.dong(499000, "Thiếu tiền lẻ"));
                });
    }

    @Test
    void banGiaoLuuKhoaNgoaiNguoiNhan() {
        layTinhHuong(
                () -> {
                    var ca = new CaThuNganService();
                    ca.mo("Ca chiều", 300000);
                    long id = ca.layId();
                    var nguoi = ca.layNguoiNhan().stream().findFirst().orElseThrow();
                    ca.dong(300000, "", CaThuNganService.HinhThucBanGiao.BAN_GIAO, nguoi.id());
                    assertThat(
                                    laySo(
                                            "SELECT nguoi_nhan_ban_giao_id FROM phien_lam_viec"
                                                    + " WHERE id=?",
                                            id))
                            .isEqualTo(nguoi.id());
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT hinh_thuc_ban_giao FROM phien_lam_viec WHERE"
                                                    + " id=?",
                                            id))
                            .isEqualTo("BAN_GIAO");
                });
    }

    @Test
    void baoCaoKhoangNgayCuLayDungHaiCanVaKhongLayNgayKeTiep() {
        layTinhHuong(
                () -> {
                    mo();
                    long ca = new CaThuNganService().layId();
                    java.time.LocalDate ngay = java.time.LocalDate.of(2020, 1, 15);
                    for (int i = 0; i < 3; i++) {
                        long don =
                                them(
                                        "INSERT"
                                            + " don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai)"
                                            + " VALUES(?,?,?,'PAID')",
                                        "ITDATE" + System.nanoTime(),
                                        ca,
                                        khachHang);
                        var luc =
                                i == 0
                                        ? ngay.atStartOfDay()
                                        : i == 1
                                                ? ngay.atTime(23, 59, 59)
                                                : ngay.plusDays(1).atStartOfDay();
                        capNhat(
                                "INSERT"
                                    + " hoa_don(so_hoa_don,don_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,thanh_toan_luc,idempotency_key)"
                                    + " VALUES(?,?,100000,'CASH',100000,0,0,?,CONVERT(varchar(40),NEWID()))",
                                "ITDATE" + System.nanoTime(),
                                don,
                                luc);
                    }
                    var dong = ThongKeService.layDoanhThuHoanTat(new KhoangNgay(ngay, ngay));
                    assertThat(dong).hasSize(1);
                    assertThat(((Number) dong.get(0)[1]).longValue()).isEqualTo(200000);
                    assertThat(((Number) dong.get(0)[2]).intValue()).isEqualTo(2);
                    assertThat(
                                    ThongKeService.layDoanhThuHoanTat(
                                            new KhoangNgay(ngay.minusDays(2), ngay.minusDays(1))))
                            .isEmpty();
                });
    }

    @Test
    void moiGocNhinBaoCaoChayDuocTrenKhoangNgayCu() {
        layTinhHuong(
                () -> {
                    var ky = new KhoangNgay(LocalDate.now().minusDays(29), LocalDate.now());
                    assertThat(ThongKeService.layBienDongKho(ky)).isNotNull();
                    assertThat(ThongKeService.layPhieuSpaTheoTrangThai(ky)).isNotNull();
                    assertThat(ThongKeService.layHopDongTrongKy(ky)).isNotNull();
                    assertThat(ThongKeService.layCocTrongKy(ky)).isNotNull();
                    assertThat(ThongKeService.layLuotKhach(ky)).isNotNull();
                    assertThat(ThongKeService.laySuDungVoucher(ky)).isNotNull();
                    assertThat(ThongKeService.layTongHopCa(ky)).isNotNull();
                    assertThat(ThongKeService.layDuNoHienTai(ky)).isNotNull();
                });
    }

    @Test
    void xemHoaDonKhongCatTenDaiVaMaHoaNoiDung() {
        layTinhHuong(
                () -> {
                    mo();
                    var daLuu =
                            BanHangService.thanhToan(
                                    layGioHang(1),
                                    CaThuNganService.PhuongThuc.CASH,
                                    100000,
                                    "Thu ngân");
                    String ten =
                            "Thức ăn chăm sóc thú cưng loại đặc biệt với tên rất dài & <chăm sóc>"
                                    + " 800g";
                    var bienLai =
                            new GioHang.BienLai(
                                    daLuu.soHoaDon(),
                                    LocalDateTime.now(),
                                    List.of(new GioHang.DongHang("SP", ten, 150000, 2)),
                                    300000,
                                    0,
                                    300000,
                                    CaThuNganService.PhuongThuc.CASH,
                                    500000,
                                    200000,
                                    "Khách lẻ",
                                    "",
                                    "Thu ngân");
                    String html =
                            com.petstation.gui.processing.sales.InHoaDonPanel.hienThiHTML(bienLai);
                    assertThat(html)
                            .contains("tên rất dài &amp; &lt;chăm sóc&gt; 800g")
                            .contains("align='center'")
                            .contains("align='right'")
                            .contains("pet-station-logo.png");
                    assertThat(com.petstation.gui.processing.sales.InHoaDonPanel.hienThi(bienLai))
                            .contains(ten);
                });
    }

    @Test
    void locKetHopSapXepVaAnhKhongLechBanGhi() throws Exception {
        trenLuongSwing(
                () -> {
                    var panel = new com.petstation.gui.catalog.DanhSachSanPhamPanel();
                    var thanhPhan = cacThanhPhan(panel);
                    var tbl =
                            (javax.swing.JTable)
                                    thanhPhan.stream()
                                            .filter(c -> c instanceof javax.swing.JTable)
                                            .findFirst()
                                            .orElseThrow();
                    var nhom =
                            (javax.swing.JComboBox<?>)
                                    thanhPhan.stream()
                                            .filter(c -> "loc:Nhóm hàng".equals(c.getName()))
                                            .findFirst()
                                            .orElseThrow();
                    var hang =
                            (javax.swing.JComboBox<?>)
                                    thanhPhan.stream()
                                            .filter(c -> "loc:Thương hiệu".equals(c.getName()))
                                            .findFirst()
                                            .orElseThrow();
                    var tim =
                            (javax.swing.JTextField)
                                    thanhPhan.stream()
                                            .filter(c -> "timKiemDanhMuc".equals(c.getName()))
                                            .findFirst()
                                            .orElseThrow();
                    nhom.setSelectedItem("Thức ăn");
                    hang.setSelectedItem("ROYAL CANIN");
                    tim.setText("Kitten");
                    assertThat(tbl.getRowCount()).isPositive();
                    for (int i = 0; i < tbl.getRowCount(); i++) {
                        String ma = tbl.getValueAt(i, 0).toString();
                        assertThat(
                                        layGiaTriDon(
                                                "SELECT thuong_hieu FROM san_pham WHERE"
                                                        + " ma_san_pham=?",
                                                ma))
                                .isEqualTo("ROYAL CANIN");
                        assertThat(tbl.getValueAt(i, 1).toString()).containsIgnoringCase("Kitten");
                    }
                    tbl.getRowSorter().toggleSortOrder(0);
                    tbl.setRowSelectionInterval(0, 0);
                    String ma = tbl.getValueAt(0, 0).toString();
                    var anh =
                            (com.petstation.gui.components.common.AnhSanPhamPanel)
                                    thanhPhan.stream()
                                            .filter(
                                                    c ->
                                                            c
                                                                    instanceof
                                                                    com.petstation.gui.components
                                                                            .common.AnhSanPhamPanel)
                                            .findFirst()
                                            .orElseThrow();
                    assertThat(anh.layDuongDan()).isEqualTo("images/products/" + ma + ".jpg");
                    nhom.setSelectedItem("Phụ kiện");
                    assertThat(tbl.getRowCount()).isZero();
                    assertThat(anh.layDuongDan()).isEmpty();
                });
    }

    @Test
    void hangThanhVienLuuDuocTiengVietVaKhongDoiSoDu() {
        layTinhHuong(
                () -> {
                    long diem = laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang);
                    for (String hang : List.of("Đồng", "Bạc", "Vàng", "Bạch Kim", "Kim Cương")) {
                        capNhat(
                                "UPDATE khach_hang SET hang_thanh_vien=? WHERE id=?",
                                hang,
                                khachHang);
                        assertThat(
                                        layGiaTriDon(
                                                "SELECT hang_thanh_vien FROM khach_hang WHERE id=?",
                                                khachHang))
                                .isEqualTo(hang);
                        assertThat(
                                        laySo(
                                                "SELECT diem_tich_luy FROM khach_hang WHERE id=?",
                                                khachHang))
                                .isEqualTo(diem);
                    }
                });
    }

    @Test
    void bienLaiLayDiemLichSuKhongLayDiemHienTai() {
        layTinhHuong(
                () -> {
                    mo();
                    var daLuu =
                            BanHangService.thanhToan(
                                    layGioHang(1),
                                    CaThuNganService.PhuongThuc.CASH,
                                    100000,
                                    "Thu ngân");
                    var truoc = BanHangService.layChiTietBienLai(daLuu.soHoaDon());
                    assertThat(truoc.soDuDiemSau()).isNotNull();
                    capNhat(
                            "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+200 WHERE id=?",
                            khachHang);
                    var sau = BanHangService.layChiTietBienLai(daLuu.soHoaDon());
                    assertThat(sau.soDuDiemSau()).isEqualTo(truoc.soDuDiemSau());
                    assertThat(sau.diemCong())
                            .isEqualTo(
                                    (int)
                                            laySo(
                                                    "SELECT diem_cong FROM hoa_don WHERE"
                                                            + " so_hoa_don=?",
                                                    daLuu.soHoaDon()));
                    assertThat(sau.dienThoaiKhach()).isNotBlank();
                });
    }

    @Test
    void soDoPhongDungIdGiuPhongTrongVaMoHopDongDangO() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    long id =
                            LuuTruService.datPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    75000,
                                    "",
                                    true);
                    var chuyen = new java.util.ArrayList<MaManHinh>();
                    var gio = new GioHang();
                    var map = new com.petstation.gui.search.TraCuuPhongHotelPanel(gio, chuyen::add);
                    var phong =
                            com.petstation.service.QuyTrinhService.layThongTinPhong().stream()
                                    .filter(p -> p.ma().equals(maPhong))
                                    .findFirst()
                                    .orElseThrow();
                    map.moPhong(phong);
                    assertThat(chuyen.get(chuyen.size() - 1)).isEqualTo(MaManHinh.HOTEL_BOOKING);
                    assertThat(gio.layQuyTrinh().layPhongHotel()).isEqualTo(phong.id());
                    assertThat(gio.layQuyTrinh().layMaPhongHotel()).isEqualTo(maPhong);
                    LuuTruService.kiemTraVao(id);
                    phong =
                            com.petstation.service.QuyTrinhService.layThongTinPhong().stream()
                                    .filter(p -> p.ma().equals(maPhong))
                                    .findFirst()
                                    .orElseThrow();
                    map.moPhong(phong);
                    assertThat(chuyen.get(chuyen.size() - 1)).isEqualTo(MaManHinh.HOTEL_JOURNAL);
                    assertThat(gio.layQuyTrinh().layHopDongHotel()).isEqualTo(id);
                    assertThat(com.petstation.gui.search.TraCuuPhongHotelPanel.layChiTiet(phong))
                            .contains("Đang lưu trú")
                            .contains("Chủ nuôi");
                });
    }

    @Test
    void caiDatGiuNoiDungChuaLuuKhiQuayLai() throws Exception {
        trenLuongSwing(
                () -> {
                    var panel = new com.petstation.gui.system.CaiDatPanel(true);
                    var truong =
                            (javax.swing.JTextField)
                                    cacThanhPhan(panel).stream()
                                            .filter(c -> c instanceof javax.swing.JTextField)
                                            .findFirst()
                                            .orElseThrow();
                    truong.setText("Nội dung đang sửa chưa lưu");
                    panel.xuLyHienThi();
                    assertThat(truong.getText()).isEqualTo("Nội dung đang sửa chưa lưu");
                });
    }

    @Test
    void demKetQuaSauLocVaLamMoiGiuDongTheoId() throws Exception {
        trenLuongSwing(
                () -> {
                    try (var truyVan =
                            org.mockito.Mockito.mockStatic(
                                    NghiepVuService.class,
                                    org.mockito.Mockito.CALLS_REAL_METHODS)) {
                        var panel =
                                new com.petstation.gui.search.TimKiemTonTheoLoPanel(
                                        new GioHang(), id -> {});
                        var tp = cacThanhPhan(panel);
                        var bang =
                                (javax.swing.JTable)
                                        tp.stream()
                                                .filter(c -> c instanceof javax.swing.JTable)
                                                .findFirst()
                                                .orElseThrow();
                        var tim =
                                (javax.swing.JTextField)
                                        tp.stream()
                                                .filter(c -> c instanceof javax.swing.JTextField)
                                                .findFirst()
                                                .orElseThrow();
                        tim.setText(maSanPham);
                        assertThat(bang.getRowCount()).isPositive();
                        bang.getRowSorter()
                                .setSortKeys(
                                        java.util.List.of(
                                                new javax.swing.RowSorter.SortKey(
                                                        1, javax.swing.SortOrder.DESCENDING)));
                        bang.setRowSelectionInterval(0, 0);
                        long id =
                                ((Number)
                                                bang.getModel()
                                                        .getValueAt(
                                                                bang.convertRowIndexToModel(0), 0))
                                        .longValue();
                        var nhan =
                                (javax.swing.JLabel)
                                        tp.stream()
                                                .filter(
                                                        c ->
                                                                c instanceof javax.swing.JLabel l
                                                                        && l.getText() != null
                                                                        && l.getText()
                                                                                .endsWith(
                                                                                        " kết quả"))
                                                .findFirst()
                                                .orElseThrow();
                        assertThat(nhan.getText()).isEqualTo(bang.getRowCount() + " kết quả");
                        bam(panel, "Làm mới");
                        assertThat(tim.getText()).isEqualTo(maSanPham);
                        assertThat(bang.getSelectedRow()).isNotNegative();
                        assertThat(bang.getRowSorter().getSortKeys())
                                .isEqualTo(java.util.List.of(
                                        new javax.swing.RowSorter.SortKey(
                                                1, javax.swing.SortOrder.DESCENDING)));
                        assertThat(
                                        ((Number)
                                                        bang.getModel()
                                                                .getValueAt(
                                                                        bang.convertRowIndexToModel(
                                                                                bang
                                                                                        .getSelectedRow()),
                                                                        0))
                                                .longValue())
                                .isEqualTo(id);
                        tim.setText("KHONG-CO-KET-QUA");
                        assertThat(nhan.getText()).isEqualTo("0 kết quả");
                        truyVan.verify(
                                () -> NghiepVuService.layCacDong(LoaiNghiepVu.BATCHES),
                                org.mockito.Mockito.times(2));
                    }
                });
    }
}
