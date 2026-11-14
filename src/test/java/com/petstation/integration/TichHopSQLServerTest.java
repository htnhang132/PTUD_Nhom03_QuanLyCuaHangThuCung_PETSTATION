package com.petstation.integration;

import static com.petstation.database.ThaoTacSQL.*;
import static com.petstation.util.DuLieu.*;

import static org.assertj.core.api.Assertions.*;

import com.petstation.dao.khachhang.impl.KhachHangDAOImpl;
import com.petstation.database.CauHinhCSDL;
import com.petstation.database.KetNoiCSDL;
import com.petstation.database.ThaoTacSQL;
import com.petstation.dto.*;
import com.petstation.gui.banhang.InHoaDonPanel;
import com.petstation.gui.chung.CauTrucMenu;
import com.petstation.gui.chung.DangKyManHinh;
import com.petstation.gui.chung.HoTroThanhCongCu;
import com.petstation.gui.chung.ThanhDanhSach;
import com.petstation.gui.chung.thanhphan.BieuMauTruong;
import com.petstation.gui.hethong.CaiDatPanel;
import com.petstation.gui.kho.AnhSanPhamPanel;
import com.petstation.gui.kho.DanhSachSanPhamPanel;
import com.petstation.gui.kho.TimKiemTonTheoLoPanel;
import com.petstation.gui.luutru.TraCuuPhongHotelPanel;
import com.petstation.model.banhang.*;
import com.petstation.model.chung.*;
import com.petstation.model.hethong.*;
import com.petstation.model.khachhang.*;
import com.petstation.model.kho.*;
import com.petstation.model.luutru.*;
import com.petstation.model.nhansu.*;
import com.petstation.model.pheduyet.*;
import com.petstation.model.spa.*;
import com.petstation.service.banhang.*;
import com.petstation.service.chung.*;
import com.petstation.service.hethong.*;
import com.petstation.service.hethong.PhienDangNhapService;
import com.petstation.service.khachhang.*;
import com.petstation.service.kho.*;
import com.petstation.service.luutru.*;
import com.petstation.service.nhansu.*;
import com.petstation.service.pheduyet.*;
import com.petstation.service.spa.*;
import com.petstation.service.thongke.*;
import com.petstation.util.DinhDang;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.*;
import java.util.*;

@EnabledIfEnvironmentVariable(named = "PETSTATION_SQL_TESTS", matches = "true")
class TichHopSQLServerTest {
    private static class HoanTac extends RuntimeException {}

    private final BanHangService banHangService = new BanHangService();
    private final ChiTietThongKeService chiTietThongKeService = new ChiTietThongKeService();
    private final DanhMucService danhMucService = DanhMucService.macDinh();
    private final DoiTraService doiTraService = new DoiTraService();
    private final GioHangService gioHangService = new GioHangService();
    private final KiemKeService kiemKeService = new KiemKeService();
    private final LuuTruService luuTruService = new LuuTruService();
    private final NghiepVuService nghiepVuService = new NghiepVuService();
    private final NhapXuatKhoService nhapXuatKhoService = new NhapXuatKhoService();
    private final PheDuyetService pheDuyetService = new PheDuyetService();
    private final QuyTrinhService quyTrinhService = new QuyTrinhService();
    private final SpaService spaService = new SpaService();
    private final ThongKeService thongKeService = new ThongKeService();
    private final TichDiemService tichDiemService = new TichDiemService();
    private final TiemPhongService tiemPhongService = new TiemPhongService();

    private long nhanVien, taiKhoan, khachHang, sanPham, loHang1, loHang2;
    private String maNhanVien, maKhachHang, maSanPham, voucher, maThuCung, maPhong, maGoiDichVu;

    private PhienDangNhap quanLy;

    @BeforeAll
    static void cauHinh() {
        CauHinhCSDL cauHinh = CauHinhCSDL.nap();
        assertThat(cauHinh.url()).contains("databaseName=PetStationIntegrationTest");
        ThaoTacSQL.cauHinh(new KetNoiCSDL(cauHinh));
    }

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
            PhienDangNhapService.macDinh().datPhienDangNhap(null);
        }
    }

    private void layDuLieuKiemThu() {
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
                            + " VALUES(?,?,?,'QUAN_LY_CUA_HANG',0)",
                        nhanVien,
                        maNhanVien,
                        org.mindrot.jbcrypt.BCrypt.hashpw(
                                "Integration@123", org.mindrot.jbcrypt.BCrypt.gensalt(4)));
        NhanVien nvQuanLy =
                new NhanVien(
                        maNhanVien,
                        "Integration Manager",
                        "IT" + hauTo,
                        "Cửa hàng trưởng",
                        LocalDate.now(),
                        true,
                        "");
        quanLy =
                new PhienDangNhap(
                        new TaiKhoan(
                                maNhanVien,
                                nvQuanLy,
                                "hash",
                                VaiTro.QUAN_LY_CUA_HANG,
                                TrangThaiTaiKhoan.HOAT_DONG,
                                0,
                                null,
                                false),
                        nvQuanLy,
                        EnumSet.allOf(Quyen.class));
        PhienDangNhapService.macDinh().datPhienDangNhap(quanLy);
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
                    + " VALUES(?,?,N'Integration Pet',N'Chó',4,DATEADD(month,-6,CAST(GETDATE() AS"
                    + " date)))",
                maThuCung,
                khachHang);
        capNhat(
                "INSERT INTO phong_hotel(ma_phong,loai_phong,gia_dem,trang_thai) VALUES(?,N'Tiêu"
                        + " chuẩn',150000,'TRONG')",
                maPhong);
        long idThuCung = laySo("SELECT id FROM thu_cung WHERE ma_thu_cung=?", maThuCung);
        capNhat(
                "INSERT tiem_phong(thu_cung_id,ten_vaccine,ngay_tiem) VALUES(?,N'Mũi"
                        + " 1',DATEADD(day,-50,CAST(GETDATE() AS date))),(?,N'Mũi"
                        + " 2',DATEADD(day,-14,CAST(GETDATE() AS date)))",
                idThuCung,
                idThuCung);

        long goiDichVu =
                them(
                        "INSERT INTO goi_dich_vu_spa(ma_goi,ten_goi,thoi_gian_du_kien)"
                                + " VALUES(?,?,60)",
                        maGoiDichVu,
                        "Service " + hauTo);
        capNhat(
                "UPDATE goi_dich_vu_spa SET bang_gia = N'Chó|< 5kg|0|5|150000' WHERE ma_goi = ?",
                maGoiDichVu);
    }

    private GioHang layGioHang(int soLuong) {
        GioHang banHang = gioHangService.tao();
        banHang.setCacDong(
                List.of(new GioHang.DongHang(maSanPham, "Integration product", 100000, soLuong)));
        banHang.setKhachHang(maKhachHang, "Integration Customer", 100);
        return banHang;
    }

    private void mo() {
        new CaThuNganService().mo("Integration shift", 1000000);
    }

    @Test
    void layCacDanhMucDocVaLuuTruThucTeSQLCacDong() {
        layTinhHuong(
                () -> {
                    var cacDong = danhMucService.layKhachHang();

                    Object[] truoc =
                            cacDong.stream()
                                    .filter(dongDuLieu -> dongDuLieu[0].equals(maKhachHang))
                                    .findFirst()
                                    .orElseThrow();

                    Object[] daThayDoi = truoc.clone();
                    daThayDoi[1] = "Updated customer";
                    daThayDoi[3] = "test@example.com";
                    danhMucService.luu(cacDong, truoc, daThayDoi);
                    assertThat(danhMucService.layKhachHang())
                            .anySatisfy(
                                    dongDuLieu -> {
                                        assertThat(dongDuLieu[0]).isEqualTo(maKhachHang);
                                        assertThat(dongDuLieu[1]).isEqualTo("Updated customer");
                                        assertThat(dongDuLieu[3]).isEqualTo("test@example.com");
                                    });
                    thucHienGiaoDich(
                            ketNoi -> {
                                assertThat(
                                                new KhachHangDAOImpl(ketNoi)
                                                        .timTheoMaKhachHang(maKhachHang)
                                                        .orElseThrow()
                                                        .getHoTen())
                                        .isEqualTo("Updated customer");
                                return null;
                            });

                    var cacSanPham = danhMucService.laySanPham();
                    Object[] vungDangXet =
                            cacSanPham.stream()
                                    .filter(dongDuLieu -> dongDuLieu[0].equals(maSanPham))
                                    .findFirst()
                                    .orElseThrow();

                    Object[] tiepTheo = vungDangXet.clone();
                    tiepTheo[4] = "110000";
                    danhMucService.luu(cacSanPham, vungDangXet, tiepTheo);
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE ma_san_pham=?", maSanPham))
                            .isEqualTo(110000);
                    for (var loai : LoaiNghiepVu.values())
                        assertThat(nghiepVuService.layCacDong(loai)).isNotNull();
                    assertThat(danhMucService.layThuCung()).isNotEmpty();
                    assertThat(danhMucService.layNhanVien()).isNotEmpty();
                    assertThat(danhMucService.layTaiKhoan()).isNotEmpty();
                    assertThat(danhMucService.layNhaCungCap()).isNotEmpty();
                    assertThat(danhMucService.layDichVuSpa()).isNotEmpty();
                    assertThat(danhMucService.layPhong()).isNotEmpty();
                });
    }

    @Test
    void layThanhToanPhanBoFEFOVaLaKhongLapGiaoDich() {
        layTinhHuong(
                () -> {
                    mo();

                    GioHang banHang = layGioHang(3);
                    assertThat(banHang.apDungVoucher(voucher)).isNull();
                    assertThatThrownBy(() -> banHang.setDiemDaDung(2))
                            .hasMessageContaining("không trừ trực tiếp");

                    long tong = banHang.tinhTong();

                    GioHang.BienLai dauTien =
                            banHang.hoanTat(PhuongThuc.TIEN_MAT, 500000, "Manager");

                    GioHang.BienLai thuHai =
                            banHang.hoanTat(PhuongThuc.TIEN_MAT, 500000, "Manager");
                    assertThat(thuHai.getSoHoaDon()).isEqualTo(dauTien.getSoHoaDon());
                    assertThat(dauTien.getTong()).isEqualTo(tong);
                    assertThat(dauTien.getThayDoi()).isEqualTo(500000 - tong);
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang1))
                            .isZero();
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(3);
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isEqualTo(1);
                    assertThat(new CaThuNganService().demHoaDon()).isEqualTo(1);
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1000000 + tong);
                });
    }

    @Test
    void layBanQuaTonHoanTacQuayLaiHoaDonVoucherVaTonKho() {
        layTinhHuong(
                () -> {
                    mo();

                    GioHang banHang = layGioHang(7);
                    banHang.apDungVoucher(voucher);

                    long cacHoaDon = laySo("SELECT COUNT(*) FROM hoa_don"),
                            cacDonHang = laySo("SELECT COUNT(*) FROM don_hang");
                    assertThatThrownBy(
                                    () -> banHang.hoanTat(PhuongThuc.TIEN_MAT, 1000000, "Manager"))
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

    @Test
    void layDongThoiTonKhoAnhChupDuLieuCacThayDoiKhongTheDuocDaDuyetKhongKiemTra() {
        layTinhHuong(
                () -> {
                    String maBienBan =
                            kiemKeService.kiemKe(
                                    "LO" + String.format("%06d", loHang1), 1, "Recount");

                    var yeuCau =
                            pheDuyetService.layTatCa().stream()
                                    .filter(dongDuLieu -> dongDuLieu.laySau().equals("Tồn thực 1"))
                                    .findFirst()
                                    .orElseThrow();
                    capNhat("UPDATE lo_hang SET so_luong_ton=3 WHERE id=?", loHang1);
                    assertThatThrownBy(() -> pheDuyetService.duyet(yeuCau, "Manager"))
                            .hasMessageContaining("Tồn đã thay đổi");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM yeu_cau_phe_duyet WHERE"
                                                            + " ma_yeu_cau=?",
                                                    yeuCau.layId())))
                            .isEqualTo("CHO_DUYET");
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang1))
                            .isEqualTo(3);
                });
    }

    @Test
    void layDaLuuTruPheDuyetVaTuChoiApDungDungMotLan() {
        layTinhHuong(
                () -> {
                    CaiDatService.macDinh().capNhat(8, 10, 30, 50000, 3, 1);

                    String xuatKho =
                            nhapXuatKhoService.ghiHuy(
                                    "LO" + String.format("%06d", loHang2), 1, "Test writeoff");

                    var yeuCau =
                            pheDuyetService.layTatCa().stream()
                                    .filter(
                                            dongDuLieu ->
                                                    dongDuLieu.layLyDo().equals("Test writeoff"))
                                    .findFirst()
                                    .orElseThrow();
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(4);
                    pheDuyetService.duyet(yeuCau, "Manager");
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(3);
                    assertThatThrownBy(() -> pheDuyetService.duyet(yeuCau, "Manager"))
                            .hasMessageContaining("đã được xử lý");
                    nhapXuatKhoService.ghiHuy(
                            "LO" + String.format("%06d", loHang2), 1, "Reject writeoff");

                    var daTuChoi =
                            pheDuyetService.layTatCa().stream()
                                    .filter(
                                            dongDuLieu ->
                                                    dongDuLieu.layLyDo().equals("Reject writeoff"))
                                    .findFirst()
                                    .orElseThrow();
                    pheDuyetService.tuChoi(daTuChoi, "Manager", "Không hợp lệ");
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang2))
                            .isEqualTo(3);
                });
    }

    @Test
    void layLichLamViecCoKhongDuocSinhCacDongVaNghiPhepDuocGiuLaiTaiLai() {
        layTinhHuong(
                () -> {
                    LocalDate ngay = LocalDate.of(2040, 1, 9);
                    var lichService = LichLamViecService.macDinh();
                    assertThat(lichService.layLichTrongTuan(lichService.layThuHaiCuaTuan(ngay)))
                            .isEmpty();
                    assertThat(lichService.phanCong(ngay, 0, maNhanVien)).isNull();
                    assertThat(lichService.phanCong(ngay, 0, maNhanVien)).contains("đã có");

                    var yeuCau =
                            pheDuyetService.guiThaoTac(
                                    PheDuyetService.Loai.NGHI_PHEP,
                                    "Integration leave",
                                    "DI_LAM",
                                    "NGHI_PHEP",
                                    "NGHI_PHEP",
                                    maNhanVien,
                                    ngay.toString());
                    pheDuyetService.duyet(yeuCau, "Manager");
                    assertThat(lichService.layO(ngay, 0))
                            .anyMatch(
                                    suKien ->
                                            suKien.layMaNhanVien().equals(maNhanVien)
                                                    && suKien.kiemTraNghiPhep());
                    assertThat(lichService.phanCong(ngay, 1, maNhanVien)).contains("nghỉ phép");
                    assertThat(lichService.tinhGioTrongThang(maNhanVien, ngay)).isZero();
                });
    }

    @Test
    void laySpaVongDoiSuDungLoaiVaCanNangCacGia() {
        layTinhHuong(
                () -> {
                    mo();

                    String maLichHen =
                            spaService.datLichHen(
                                    maThuCung,
                                    maGoiDichVu,
                                    maNhanVien,
                                    LocalDateTime.now().plusHours(1));
                    assertThatThrownBy(
                                    () ->
                                            spaService.datLichHen(
                                                    maThuCung,
                                                    maGoiDichVu,
                                                    maNhanVien,
                                                    LocalDateTime.now().plusHours(1)))
                            .hasMessageContaining("trùng");

                    String phieuSpa = spaService.tiepNhan(maThuCung, maLichHen, 4.5, "Healthy");
                    assertThat(
                                    laySo(
                                            "SELECT tong_tien FROM phieu_tiep_nhan_spa WHERE"
                                                    + " ma_phieu=?",
                                            phieuSpa))
                            .isEqualTo(150000);
                    spaService.layTrangThaiSpa(phieuSpa, "DANG_THUC_HIEN");
                    spaService.layTrangThaiSpa(phieuSpa, "HOAN_TAT");

                    GioHang hoaDon = gioHangService.tao();
                    banHangService.quyetToan("SPA", phieuSpa, hoaDon);
                    hoaDon.xacNhanDaNhanTien(PhuongThuc.CHUYEN_KHOAN, true);
                    hoaDon.hoanTat(PhuongThuc.CHUYEN_KHOAN, hoaDon.tinhTong(), "Manager");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM phieu_tiep_nhan_spa"
                                                            + " WHERE ma_phieu=?",
                                                    phieuSpa)))
                            .isEqualTo("DA_THANH_TOAN");
                    assertThatThrownBy(
                                    () ->
                                            banHangService.quyetToan(
                                                    "SPA", phieuSpa, gioHangService.tao()))
                            .hasMessageContaining("đã thanh toán");
                });
    }

    @Test
    void layHotelNganTrungLichVaLuuDuocChamSocVaQuyetToan() {
        layTinhHuong(
                () -> {
                    mo();
                    LocalDateTime batDau = LocalDateTime.now(), ketThuc = batDau.plusDays(2);

                    String hopDongLuuTru =
                            luuTruService.datPhong(
                                    maThuCung, maPhong, batDau, ketThuc, 150000, "Test", true);
                    assertThatThrownBy(
                                    () ->
                                            luuTruService.datPhong(
                                                    maThuCung, maPhong, batDau, ketThuc, 150000, "",
                                                    true))
                            .hasMessageContaining("trùng");
                    luuTruService.kiemTraVao(hopDongLuuTru);
                    luuTruService.ghiNhatKy(hopDongLuuTru, "Ate", "Clean", "Healthy");
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM nhat_ky_cham_soc WHERE"
                                                    + " ma_hop_dong=?",
                                            hopDongLuuTru))
                            .isEqualTo(1);
                    assertThat(luuTruService.tinhTienLuuTruConLai(hopDongLuuTru)).isEqualTo(150000);

                    GioHang hoaDon = gioHangService.tao();
                    banHangService.quyetToan("HOTEL", hopDongLuuTru, hoaDon);
                    hoaDon.xacNhanDaNhanTien(PhuongThuc.THE_NGAN_HANG, true);
                    hoaDon.hoanTat(PhuongThuc.THE_NGAN_HANG, hoaDon.tinhTong(), "Manager");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                            + " ma_hop_dong=?",
                                                    hopDongLuuTru)))
                            .isEqualTo("DA_TRA_PHONG");
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM phong_hotel WHERE"
                                                            + " ma_phong=?",
                                                    maPhong)))
                            .isEqualTo("DANG_DON");
                });
    }

    @Test
    void layNhapKhoVaDoiTraCapNhatTonKhoVaCongNo() {
        layTinhHuong(
                () -> {
                    mo();

                    String nhaCungCap = chuyenThanhChuoi(danhMucService.layNhaCungCap().get(0)[0]);

                    String bienLai =
                            nhapXuatKhoService.nhapKho(
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
                                                    + " ma_phieu_nhap=?",
                                            bienLai))
                            .isEqualTo(100000);
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(9);

                    GioHang banHang = layGioHang(2);

                    var hoaDon = banHang.hoanTat(PhuongThuc.TIEN_MAT, 500000, "Manager");

                    String muc = hoaDon.getSoHoaDon() + ":" + maSanPham;
                    GioHang doiHang = gioHangService.tao();
                    doiTraService.lapDoiHang(doiHang, muc, 1, maSanPham, 1, "Đổi phụ kiện");
                    doiHang.hoanTat(PhuongThuc.TIEN_MAT, doiHang.tinhTong(), "Manager");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(7);
                    assertThatThrownBy(
                                    () ->
                                            doiTraService.lapDoiHang(
                                                    gioHangService.tao(),
                                                    muc,
                                                    2,
                                                    maSanPham,
                                                    2,
                                                    "Quá số lượng"))
                            .hasMessageContaining("vượt");
                });
    }

    @Test
    void layCaiDatVaNhatKyHeThongLaDaTaiLaiTuSQL() {
        layTinhHuong(
                () -> {
                    CaiDatService.macDinh().capNhat(10, 5, 45, 70000, 14, 1000000);
                    assertThat(CaiDatService.macDinh().layVATPhanTram()).isEqualTo(10);
                    assertThat(CaiDatService.macDinh().layHanSuDungCanhBaoSoNgay()).isEqualTo(45);
                    assertThat(CaiDatService.macDinh().layPhiTraPhongTreMoiNgay()).isEqualTo(70000);
                    NhatKyHeThongService.macDinh().ghiNhatKy("Thêm", "Integration audit");
                    assertThat(NhatKyHeThongService.macDinh().layCacMuc())
                            .anyMatch(
                                    suKien ->
                                            suKien.doiTuong().equals("Integration audit")
                                                    && suKien.nguoiDung()
                                                            .equals("Integration Manager")
                                                    && suKien.kieu().equals("CREATE"));
                });
    }

    @Test
    void layCacBaoCaoSuDungThucTeDuLieuVaPhamViCaNhanTaiKhoan() {
        layTinhHuong(
                () -> {
                    PhienDangNhap caNhan =
                            new PhienDangNhap(
                                    quanLy.getTaiKhoan(),
                                    quanLy.getNhanVien(),
                                    Set.of(
                                            Quyen.XEM_BAO_CAO_CA,
                                            Quyen.THANH_TOAN,
                                            Quyen.QUAN_LY_CA_THU_NGAN));
                    PhienDangNhapService.macDinh().datPhienDangNhap(caNhan);
                    assertThat(thongKeService.layDoanhThuTheoNgay(7)).isEmpty();
                    mo();

                    GioHang banHang = layGioHang(1);

                    long tong = banHang.tinhTong();
                    banHang.hoanTat(PhuongThuc.TIEN_MAT, 200000, "Manager");
                    assertThat(thongKeService.layDoanhThuTheoNgay(7)).hasSize(1);
                    assertThat(
                                    Math.round(
                                            thongKeService
                                                    .layDoanhThuTheoNgay(7)
                                                    .get(0)
                                                    .getTongTien()))
                            .isEqualTo(tong);
                    assertThatThrownBy(() -> thongKeService.layKhachHang(7))
                            .hasMessageContaining("không có quyền");
                    assertThatThrownBy(
                                    () ->
                                            danhMucService.luu(
                                                    danhMucService.layKhachHang(),
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

    @Test
    void layMoiDaDangKyManHinhKhoiTaoTrenThucTeSQL() {
        layTinhHuong(
                () -> {
                    DangKyManHinh boDangKy = new DangKyManHinh(quanLy, boQua -> {});

                    java.util.Set<Class<?>> vungHienThiCacKieu = new java.util.HashSet<>();
                    for (MaManHinh manHinh : MaManHinh.values()) {
                        assertThatCode(
                                        () -> {
                                            var khungVungHienThi = boDangKy.tao(manHinh);
                                            assertThat(khungVungHienThi)
                                                    .as(manHinh.name())
                                                    .isNotNull();
                                            assertThat(
                                                            (manHinh == MaManHinh.TIEP_NHAN_SPA
                                                                    || vungHienThiCacKieu.add(
                                                                            khungVungHienThi
                                                                                    .getClass())))
                                                    .as("Panel riêng cho " + manHinh)
                                                    .isTrue();

                                            if (khungVungHienThi instanceof HoTroThanhCongCu hoTro)
                                                hoTro.xuLyHienThi();

                                            if (manHinh == MaManHinh.KIEM_KE_TON_KHO
                                                    || manHinh == MaManHinh.TICH_DIEM_VOUCHER
                                                    || manHinh == MaManHinh.TAI_KHOAN_PHAN_QUYEN) {
                                                var tabCacTab =
                                                        layCacThanhPhanCon(khungVungHienThi)
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

                                            String thaoTac =
                                                    switch (manHinh) {
                                                        case DAT_LICH_HEN_SPA -> "+ Đặt lịch hẹn";
                                                        case HOAN_TAT_SPA -> "Thanh toán";
                                                        case DAT_PHONG_LUU_TRU -> "+ Đặt phòng";
                                                        case TRA_PHONG_LUU_TRU -> "Quyết toán";
                                                        case NHAP_KHO -> "Trả công nợ";
                                                        case HOA_DON_CHO -> "Mở lại đơn";
                                                        case LAP_HOA_DON -> "Xem / In lại";
                                                        default -> null;
                                                    };
                                            if (thaoTac != null)
                                                assertThat(
                                                                layCacThanhPhanCon(khungVungHienThi)
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

    private static java.util.stream.Stream<java.awt.Component> layCacThanhPhanCon(
            java.awt.Component thanhPhan) {
        var banThan = java.util.stream.Stream.of(thanhPhan);
        if (!(thanhPhan instanceof java.awt.Container vungChua)) return banThan;
        return java.util.stream.Stream.concat(
                banThan,
                java.util.Arrays.stream(vungChua.getComponents())
                        .flatMap(TichHopSQLServerTest::layCacThanhPhanCon));
    }

    @Test
    void layCacGioHangTreoTaiLaiKhachHangVaGiaHienTai() {
        layTinhHuong(
                () -> {
                    GioHang banDau = layGioHang(2);

                    String daTreo = banHangService.treo(banDau);
                    capNhat("UPDATE san_pham SET gia_ban=120000 WHERE id=?", sanPham);

                    GioHang daKhoiPhuc = gioHangService.tao();
                    banHangService.moLai(daTreo, daKhoiPhuc);
                    assertThat(daKhoiPhuc.getMaKhachHang()).isEqualTo(maKhachHang);
                    assertThat(daKhoiPhuc.getCacDong()).hasSize(1);
                    assertThat(daKhoiPhuc.getCacDong().get(0).getSoLuong()).isEqualTo(2);
                    assertThat(daKhoiPhuc.getCacDong().get(0).getDonGia()).isEqualTo(120000);
                    assertThat(laySo("SELECT COUNT(*) FROM hoa_don WHERE ma_hoa_don=?", daTreo))
                            .isZero();
                    assertThatThrownBy(() -> banHangService.moLai(daTreo, gioHangService.tao()))
                            .hasMessageContaining("Không tìm thấy");
                });
    }

    @Test
    void layKhongDongConPhaiThuHotelCoTheVanDuocDaQuyetToan() {
        layTinhHuong(
                () -> {
                    mo();

                    String hopDongLuuTru =
                            luuTruService.datPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    75000,
                                    "",
                                    true);
                    capNhat(
                            "UPDATE hop_dong_luu_tru SET tien_coc=150000 WHERE ma_hop_dong=?",
                            hopDongLuuTru);
                    luuTruService.kiemTraVao(hopDongLuuTru);

                    GioHang hoaDon = gioHangService.tao();
                    banHangService.quyetToan("HOTEL", hopDongLuuTru, hoaDon);
                    assertThat(hoaDon.tinhTong()).isZero();
                    assertThat(hoaDon.hoanTat(PhuongThuc.TIEN_MAT, 0, "Manager").getTong())
                            .isZero();
                    assertThat(
                                    chuyenThanhChuoi(
                                            layGiaTriDon(
                                                    "SELECT trang_thai FROM hop_dong_luu_tru WHERE"
                                                            + " ma_hop_dong=?",
                                                    hopDongLuuTru)))
                            .isEqualTo("DA_TRA_PHONG");
                });
    }

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
                                                            PhuongThuc.TIEN_MAT, 200000, "Manager"))
                            .hasMessageContaining("Không đủ tồn");
                    capNhat(
                            "UPDATE lo_hang SET han_su_dung=DATEADD(day,30,GETDATE()) WHERE"
                                    + " san_pham_id=?",
                            sanPham);

                    GioHang daCu = layGioHang(1);
                    capNhat("UPDATE san_pham SET gia_ban=110000 WHERE id=?", sanPham);
                    assertThatThrownBy(() -> daCu.hoanTat(PhuongThuc.TIEN_MAT, 200000, "Manager"))
                            .hasMessageContaining("đã đổi");
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(6);
                });
    }

    @Test
    void layDanhMucCacBoSoanThaoVaMoiBaoCaoTruyVanSuDungDuocChonThongNhatCauTrucCSDL() {
        layTinhHuong(
                () -> {
                    List<DanhMucService.CacDong> cacDanhMuc =
                            List.of(
                                    danhMucService.layThuCung(),
                                    danhMucService.layNhanVien(),
                                    danhMucService.layTaiKhoan(),
                                    danhMucService.layNhaCungCap(),
                                    danhMucService.layKhuyenMai(),
                                    danhMucService.layVoucher(),
                                    danhMucService.layDichVuSpa(),
                                    danhMucService.layPhong());
                    for (DanhMucService.CacDong danhMuc : cacDanhMuc) {
                        Object[] banDau = danhMuc.get(0).clone();
                        if (danhMuc.layLoai() == LoaiDanhMuc.TAI_KHOAN
                                && "Hoạt động".equals(banDau[3])
                                && !maNhanVien.equals(banDau[0])) continue;
                        danhMucService.luu(danhMuc, banDau, banDau.clone());
                        assertThatCode(danhMuc::lamMoi)
                                .as(danhMuc.layLoai().name())
                                .doesNotThrowAnyException();
                    }
                    for (String nhomTongHop : List.of("method", "staff", "shift", "source"))
                        assertThat(thongKeService.layDoanhThu(7, nhomTongHop)).isNotNull();
                    assertThat(thongKeService.layDuLieuHotel(7)).isNotNull();
                    assertThat(thongKeService.layLichHen(7)).isNotNull();
                    assertThat(thongKeService.layVoucher()).isNotNull();
                    assertThat(thongKeService.layTonKho()).isNotNull();
                    assertThat(thongKeService.layLoSapHetHan()).isNotNull();
                    assertThat(thongKeService.layCongNoNhaCungCap(7)).isNotNull();
                    assertThat(thongKeService.layNhanVien(7)).isNotNull();
                });
    }

    private void doiVaiTro(VaiTro vaiTro) {
        TaiKhoan tk = quanLy.getTaiKhoan();
        PhienDangNhapService.macDinh()
                .datPhienDangNhap(
                        new PhienDangNhap(
                                new TaiKhoan(
                                        tk.getTenDangNhap(),
                                        quanLy.getNhanVien(),
                                        tk.getMaBamMatKhau(),
                                        vaiTro,
                                        tk.getTrangThai(),
                                        0,
                                        null,
                                        false),
                                quanLy.getNhanVien(),
                                new QuyenVaiTroService().tao(vaiTro)));
    }

    @Test
    void doiVoucherDungBien199200VaKhongTruLap() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE khach_hang SET diem_tich_luy=199 WHERE id=?", khachHang);
                    String khoa = "IT-" + UUID.randomUUID();
                    assertThatThrownBy(() -> tichDiemService.doiVoucher(maKhachHang, khoa))
                            .hasMessageContaining("200");
                    capNhat("UPDATE khach_hang SET diem_tich_luy=200 WHERE id=?", khachHang);
                    String ma = tichDiemService.doiVoucher(maKhachHang, khoa);
                    assertThat(tichDiemService.doiVoucher(maKhachHang, khoa)).isEqualTo(ma);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isZero();
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
                    String ma = tichDiemService.doiVoucher(maKhachHang, "IT-" + UUID.randomUUID());
                    assertThatThrownBy(
                                    () -> tichDiemService.kiemTraVoucher(ma, "SAI", "SPA", 150000))
                            .hasMessageContaining("không thuộc");
                    assertThatThrownBy(
                                    () ->
                                            tichDiemService.kiemTraVoucher(
                                                    ma, maKhachHang, "", 150000))
                            .hasMessageContaining("Spa");
                    capNhat(
                            "UPDATE voucher SET hieu_luc_den=DATEADD(day,-1,SYSDATETIME()) WHERE"
                                    + " ma_voucher=?",
                            ma);
                    assertThatThrownBy(
                                    () ->
                                            tichDiemService.kiemTraVoucher(
                                                    ma, maKhachHang, "SPA", 150000))
                            .hasMessageContaining("hết hạn");
                    capNhat(
                            "UPDATE voucher SET hieu_luc_den=DATEADD(day,60,SYSDATETIME()) WHERE"
                                    + " ma_voucher=?",
                            ma);
                    String spa = spaService.tiepNhan(maThuCung, null, 4, "Đủ sức khỏe");
                    spaService.themDichVuSpa(spa, maGoiDichVu);
                    spaService.layTrangThaiSpa(spa, "DANG_THUC_HIEN");
                    spaService.layTrangThaiSpa(spa, "HOAN_TAT");
                    GioHang gio = gioHangService.tao();
                    banHangService.quyetToan("SPA", spa, gio);
                    assertThat(gio.apDungVoucher(ma)).isNull();
                    assertThat(gio.tinhTong()).isZero();
                    String hd = gio.hoanTat(PhuongThuc.TIEN_MAT, 0, "Test").getSoHoaDon();
                    assertThat(gio.hoanTat(PhuongThuc.TIEN_MAT, 0, "Test").getSoHoaDon())
                            .isEqualTo(hd);
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", ma))
                            .isEqualTo(1);
                    assertThatThrownBy(
                                    () ->
                                            tichDiemService.kiemTraVoucher(
                                                    ma, maKhachHang, "SPA", 150000))
                            .hasMessageContaining("hết lượt");
                });
    }

    @Test
    void spaChanThieuNgaySinhChuaDuTuoiThieuMuiVaMuiQuaGan() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE thu_cung SET ngay_sinh=NULL WHERE ma_thu_cung=?", maThuCung);
                    assertThatThrownBy(() -> spaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("ngày sinh");
                    capNhat(
                            "UPDATE thu_cung SET ngay_sinh=DATEADD(month,-1,CAST(GETDATE() AS"
                                    + " date)) WHERE ma_thu_cung=?",
                            maThuCung);
                    assertThatThrownBy(() -> spaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("2 tháng");
                    capNhat(
                            "UPDATE thu_cung SET ngay_sinh=DATEADD(month,-2,CAST(GETDATE() AS"
                                    + " date)) WHERE ma_thu_cung=?",
                            maThuCung);
                    capNhat(
                            "DELETE tiem_phong WHERE id=(SELECT MAX(tp.id) FROM tiem_phong tp JOIN"
                                    + " thu_cung t ON t.id=tp.thu_cung_id WHERE t.ma_thu_cung=?)",
                            maThuCung);
                    assertThatThrownBy(() -> spaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("2 mũi");
                    tiemPhongService.luu(
                            maThuCung, 0, "Mũi bổ sung", LocalDate.now().minusDays(13), null, "");
                    assertThatThrownBy(() -> spaService.tiepNhan(maThuCung, null, 4, ""))
                            .hasMessageContaining("14 ngày");
                    capNhat(
                            "UPDATE tiem_phong SET ngay_tiem=DATEADD(day,-14,CAST(GETDATE() AS"
                                    + " date)) WHERE thu_cung_id=(SELECT id FROM thu_cung WHERE"
                                    + " ma_thu_cung=?) AND ten_vaccine=N'Mũi bổ sung'",
                            maThuCung);
                    assertThat(spaService.tiepNhan(maThuCung, null, 4, "")).isNotBlank();
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
                                            luuTruService.datPhong(
                                                    maThuCung, maPhong, tu, den, 75000, ""))
                            .hasMessageContaining("xác nhận");
                    assertThatThrownBy(
                                    () ->
                                            luuTruService.datPhong(
                                                    maThuCung, maPhong, tu, den, 0, "", true))
                            .hasMessageContaining("50%");
                    String id =
                            luuTruService.datPhong(maThuCung, maPhong, tu, den, 75000, "", true);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT da_nhan_coc FROM hop_dong_luu_tru WHERE"
                                                    + " ma_hop_dong=?",
                                            id))
                            .isEqualTo(true);
                    assertThatThrownBy(() -> luuTruService.hoanTatDonPhong(maPhong))
                            .hasMessageContaining("đang dọn");
                    luuTruService.kiemTraVao(id);
                    assertThatThrownBy(() -> luuTruService.kiemTraVao(id))
                            .hasMessageContaining("không còn");
                    GioHang gio = gioHangService.tao();
                    banHangService.quyetToan("HOTEL", id, gio);
                    gio.hoanTat(PhuongThuc.TIEN_MAT, 75000, "Test");
                    assertThatThrownBy(
                                    () ->
                                            banHangService.quyetToan(
                                                    "HOTEL", id, gioHangService.tao()))
                            .hasMessageContaining("quyết toán");
                    luuTruService.hoanTatDonPhong(maPhong);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM phong_hotel WHERE ma_phong=?",
                                            maPhong))
                            .isEqualTo("TRONG");
                });
    }

    @Test
    void giaChiDoiSauQuanLyDuyetVaKhongDuyetLap() {
        layTinhHuong(
                () -> {
                    doiVaiTro(VaiTro.NHAN_VIEN_KHO);
                    var yc =
                            new DeXuatGiaService()
                                    .gui(maSanPham, 120000, "Điều chỉnh theo giá nhập");
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(100000);
                    assertThatThrownBy(() -> pheDuyetService.duyet(yc, "Kho"))
                            .isInstanceOf(SecurityException.class);
                    PhienDangNhapService.macDinh().datPhienDangNhap(quanLy);
                    pheDuyetService.duyet(yc, "Quản lý");
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(120000);
                    assertThatThrownBy(() -> pheDuyetService.duyet(yc, "Quản lý"))
                            .hasMessageContaining("đã được xử lý");
                });
    }

    @Test
    void tuChoiGiaKhongDoiGiaVaKhoKhongSuaGiaQuaDanhMuc() {
        layTinhHuong(
                () -> {
                    doiVaiTro(VaiTro.NHAN_VIEN_KHO);
                    var cacDong = danhMucService.laySanPham();
                    Object[] cu =
                            cacDong.stream()
                                    .filter(d -> d[0].equals(maSanPham))
                                    .findFirst()
                                    .orElseThrow();
                    Object[] moi = cu.clone();
                    moi[4] = "999999";
                    assertThatThrownBy(() -> danhMucService.luu(cacDong, cu, moi))
                            .isInstanceOf(SecurityException.class);
                    var yc = new DeXuatGiaService().gui(maSanPham, 120000, "Đề xuất thử");
                    PhienDangNhapService.macDinh().datPhienDangNhap(quanLy);
                    pheDuyetService.tuChoi(yc, "Quản lý", "Giữ giá hiện tại");
                    assertThat(laySo("SELECT gia_ban FROM san_pham WHERE id=?", sanPham))
                            .isEqualTo(100000);
                    assertThatThrownBy(() -> pheDuyetService.duyet(yc, "Quản lý"))
                            .hasMessageContaining("đã được xử lý");
                });
    }

    @Test
    void doiHangThuChenhLechDiemTonHaiChieuVaBaoCaoKhongNhanDoi() {
        layTinhHuong(
                () -> {
                    capNhat("UPDATE chuong_trinh_khuyen_mai SET trang_thai='NGUNG_HOAT_DONG'");
                    mo();
                    GioHang gio = layGioHang(2);
                    gio.apDungVoucher(voucher);
                    long doanhThuTruoc =
                            thongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> Math.round(d.getTongTien()))
                                    .sum();
                    var bienLai = gio.hoanTat(PhuongThuc.TIEN_MAT, 200000, "Test");
                    String dong = bienLai.getSoHoaDon() + ":" + maSanPham;
                    GioHang doi = gioHangService.tao();
                    doiTraService.lapDoiHang(doi, dong, 1, maSanPham, 1, "Đổi kích cỡ");
                    assertThat(doi.tinhTong()).isEqualTo(5000);
                    var thu = doi.hoanTat(PhuongThuc.TIEN_MAT, 5000, "Test");
                    assertThat(doi.hoanTat(PhuongThuc.TIEN_MAT, 5000, "Test").getSoHoaDon())
                            .isEqualTo(thu.getSoHoaDon());
                    assertThat(
                                    laySo(
                                            "SELECT SUM(so_luong_ton) FROM lo_hang WHERE"
                                                    + " san_pham_id=?",
                                            sanPham))
                            .isEqualTo(4);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isEqualTo(119);
                    assertThat(InHoaDonPanel.hienThi(thu))
                            .contains("Giá trị hàng được đổi", "HĐ gốc", bienLai.getSoHoaDon());
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1195000);
                    long doanhThuSau =
                            thongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> Math.round(d.getTongTien()))
                                    .sum();
                    assertThat(doanhThuSau - doanhThuTruoc).isEqualTo(195000);
                    assertThat(
                                    laySo(
                                            "SELECT COUNT(*) FROM phieu_doi_tra WHERE"
                                                    + " khoa_thao_tac=?",
                                            doi.getKhoaThanhToan()))
                            .isEqualTo(1);
                });
    }

    @Test
    void doiHangChanSaiNhomQuaHanThayTheThapHonVaHoanTacKhiHetTon() {
        layTinhHuong(
                () -> {
                    mo();
                    GioHang gio = layGioHang(2);
                    var hd = gio.hoanTat(PhuongThuc.TIEN_MAT, 200000, "Test");
                    String dong = hd.getSoHoaDon() + ":" + maSanPham;
                    capNhat("UPDATE san_pham SET nhom_hang=N'Thức ăn' WHERE id=?", sanPham);
                    assertThatThrownBy(
                                    () ->
                                            doiTraService.lapDoiHang(
                                                    gioHangService.tao(),
                                                    dong,
                                                    1,
                                                    maSanPham,
                                                    1,
                                                    "Đổi"))
                            .hasMessageContaining("Phụ kiện");
                    capNhat("UPDATE san_pham SET nhom_hang=N'Phụ kiện' WHERE id=?", sanPham);
                    capNhat(
                            "UPDATE hoa_don SET thanh_toan_luc=DATEADD(day,-4,SYSDATETIME()) WHERE"
                                    + " ma_hoa_don=?",
                            hd.getSoHoaDon());
                    assertThatThrownBy(
                                    () ->
                                            doiTraService.lapDoiHang(
                                                    gioHangService.tao(),
                                                    dong,
                                                    1,
                                                    maSanPham,
                                                    1,
                                                    "Đổi"))
                            .hasMessageContaining("ba ngày");
                    capNhat(
                            "UPDATE hoa_don SET thanh_toan_luc=SYSDATETIME() WHERE ma_hoa_don=?",
                            hd.getSoHoaDon());
                    assertThatThrownBy(
                                    () ->
                                            doiTraService.lapDoiHang(
                                                    gioHangService.tao(),
                                                    dong,
                                                    2,
                                                    maSanPham,
                                                    1,
                                                    "Đổi"))
                            .hasMessageContaining("không được thấp");
                    GioHang doi = gioHangService.tao();
                    doiTraService.lapDoiHang(doi, dong, 1, maSanPham, 6, "Đổi");
                    long diem = laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang),
                            soPhieu = laySo("SELECT COUNT(*) FROM phieu_doi_tra");
                    assertThatThrownBy(() -> doi.hoanTat(PhuongThuc.TIEN_MAT, 1000000, "Test"))
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
                                                    PhuongThuc.CHUYEN_KHOAN,
                                                    gio.tinhTong(),
                                                    "Test"))
                            .hasMessageContaining("xác nhận");
                    gio.xacNhanDaNhanTien(PhuongThuc.CHUYEN_KHOAN, true);
                    assertThat(gio.apDungVoucher(voucher)).isNull();
                    assertThatThrownBy(
                                    () ->
                                            gio.hoanTat(
                                                    PhuongThuc.CHUYEN_KHOAN,
                                                    gio.tinhTong(),
                                                    "Test"))
                            .hasMessageContaining("xác nhận");
                    assertThat(laySo("SELECT da_dung FROM voucher WHERE ma_voucher=?", voucher))
                            .isZero();
                    gio.xacNhanDaNhanTien(PhuongThuc.CHUYEN_KHOAN, true);
                    assertThat(
                                    gio.hoanTat(PhuongThuc.CHUYEN_KHOAN, gio.tinhTong(), "Test")
                                            .getTong())
                            .isEqualTo(gio.tinhTong());
                });
    }

    @Test
    void quyTrinhGiuDungPhieuVaChanChuyenBuocTruocDieuKien() {
        layTinhHuong(
                () -> {
                    mo();
                    GioHang gio = gioHangService.tao();
                    assertThatThrownBy(
                                    () ->
                                            quyTrinhService.chuyenBuoc(
                                                    "SPA_INTAKE", "SPA_SERVICE_SELECTION", gio))
                            .hasMessageContaining("chọn");
                    String spa = spaService.tiepNhan(maThuCung, null, 4, "");
                    gio.getQuyTrinh().chonSpa(spa);
                    assertThatThrownBy(
                                    () ->
                                            quyTrinhService.chuyenBuoc(
                                                    "SPA_SERVICE_SELECTION", "SPA_PROGRESS", gio))
                            .hasMessageContaining("thêm dịch vụ");
                    spaService.themDichVuSpa(spa, maGoiDichVu);
                    assertThatThrownBy(
                                    () ->
                                            quyTrinhService.chuyenBuoc(
                                                    "SPA_PROGRESS", "SPA_CLOSE", gio))
                            .hasMessageContaining("hoàn thành");
                    spaService.layTrangThaiSpa(spa, "DANG_THUC_HIEN");
                    spaService.layTrangThaiSpa(spa, "HOAN_TAT");
                    quyTrinhService.chuyenBuoc("SPA_CLOSE", "PAYMENT", gio);
                    assertThat(gio.getMaNguon()).isEqualTo(spa);
                    assertThat(gio.tinhTamTinh()).isEqualTo(150000);
                    gio.hoanTat(PhuongThuc.TIEN_MAT, 150000, "Test");
                    assertThat(gio.getQuyTrinh().getPhieuSpa()).isEmpty();
                    assertThatThrownBy(() -> spaService.layTrangThaiSpa(spa, "HOAN_TAT"))
                            .hasMessageContaining("trạng thái");
                });
    }

    @Test
    void spaTrongNgayKhongLayPhieuNgayCu() {
        layTinhHuong(
                () -> {
                    String spa = spaService.tiepNhan(maThuCung, null, 4, "");
                    assertThat(nghiepVuService.layCacDong(LoaiNghiepVu.PHIEU_SPA))
                            .anyMatch(d -> spa.equals(d.layMa()));
                    capNhat(
                            "UPDATE phieu_tiep_nhan_spa SET tao_luc=DATEADD(day,-1,SYSDATETIME())"
                                    + " WHERE ma_phieu=?",
                            spa);
                    assertThat(nghiepVuService.layCacDong(LoaiNghiepVu.PHIEU_SPA))
                            .noneMatch(d -> spa.equals(d.layMa()));
                    assertThat(nghiepVuService.layCacDong(LoaiNghiepVu.TIEN_DO_SPA))
                            .anyMatch(d -> spa.equals(d.layMa()));
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
                if (cbo.getItemAt(i) instanceof BieuMauTruong.LuaChon lua
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
                    capNhat("UPDATE chuong_trinh_khuyen_mai SET trang_thai='NGUNG_HOAT_DONG'");
                    var dich = new java.util.concurrent.atomic.AtomicReference<MaManHinh>();
                    var dangKy = new DangKyManHinh(quanLy, dich::set);
                    GioHang gio = dangKy.layGioHang();
                    var lich = dangKy.tao(MaManHinh.DAT_LICH_HEN_SPA);
                    nhapHopThoai(
                            "Đặt lịch Spa",
                            hop -> {
                                chonMa(hop, "pet", maThuCung);
                                chonMa(hop, "package", maGoiDichVu);
                                chonMa(hop, "staff", maNhanVien);
                            },
                            () -> bam(lich, "+ Đặt lịch hẹn"));
                    assertThat(gio.getQuyTrinh().getLichHen()).isNotBlank();
                    var tiepNhan = dangKy.tao(MaManHinh.TIEP_NHAN_SPA);
                    nhapHopThoai(
                            "Tiếp nhận Spa",
                            hop -> chonMa(hop, "weight", "4"),
                            () -> bam(tiepNhan, "+ Tiếp nhận"));
                    String id = gio.getQuyTrinh().getPhieuSpa();
                    assertThat(id).isNotBlank();
                    assertThat(dich.get()).isEqualTo(MaManHinh.CHON_GOI_SPA);
                    var chon = dangKy.tao(MaManHinh.CHON_GOI_SPA);
                    bam(chon, "Bắt đầu làm");
                    var tienDo = dangKy.tao(MaManHinh.CAP_NHAT_TIEN_DO_SPA);
                    bam(tienDo, "Hoàn thành");
                    var dong = dangKy.tao(MaManHinh.HOAN_TAT_SPA);
                    bam(dong, "Thanh toán");
                    assertThat(gio.getMaNguon()).isEqualTo(id);
                    assertThat(dich.get()).isEqualTo(MaManHinh.THANH_TOAN);
                    var thanhToan = dangKy.tao(MaManHinh.THANH_TOAN);
                    bam(thanhToan, "Đủ tiền");
                    nhapHopThoai(
                            "Xác nhận thanh toán",
                            hop -> {},
                            () -> bam(thanhToan, "Xác nhận thanh toán (F9)"));
                    assertThat(dich.get()).isEqualTo(MaManHinh.IN_HOA_DON);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM phieu_tiep_nhan_spa WHERE"
                                                    + " ma_phieu=?",
                                            id))
                            .isEqualTo("DA_THANH_TOAN");
                });
    }

    @Test
    void menu37MucVaCacManHinhDungQuyenCuaBonVaiTro() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    int soMuc =
                            CauTrucMenu.layCacNhom().stream()
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
                        var dangKy =
                                new DangKyManHinh(
                                        PhienDangNhapService.macDinh().layPhien(), id -> {});
                        for (var nhom : CauTrucMenu.layCacNhom())
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
                                            .isInstanceOf(SecurityException.class);
                            }
                    }
                });
    }

    @Test
    void thaoTacHotelNhanCocChamSocThanhToanVaDonPhongQuaGiaoDien() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    capNhat("UPDATE chuong_trinh_khuyen_mai SET trang_thai='NGUNG_HOAT_DONG'");
                    long thuTruoc =
                            thongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> Math.round(d.getTongTien()))
                                    .sum();
                    var dich = new java.util.concurrent.atomic.AtomicReference<MaManHinh>();
                    var dk = new DangKyManHinh(quanLy, dich::set);
                    GioHang gio = dk.layGioHang();
                    var datPhong = dk.tao(MaManHinh.DAT_PHONG_LUU_TRU);
                    nhapHopThoai(
                            "Đặt phòng lưu trú|Xác nhận nhận cọc",
                            hop -> {
                                if (hop.getTitle().equals("Đặt phòng lưu trú")) {
                                    chonMa(hop, "pet", maThuCung);
                                    chonMa(hop, "room", maPhong);
                                }
                            },
                            () -> bam(datPhong, "+ Đặt phòng"));
                    String id = gio.getQuyTrinh().getHopDongHotel();
                    assertThat(id).isNotBlank();
                    assertThat(luuTruService.layMucCoc(id)).containsExactly(75000, 0);
                    assertThatThrownBy(() -> luuTruService.kiemTraVao(id))
                            .isInstanceOf(IllegalArgumentException.class);
                    luuTruService.thuCoc(id, PhuongThuc.TIEN_MAT, 75000, 75000, "TEST-COC-" + id);
                    assertThat(new CaThuNganService().layTienCocHotel()).isEqualTo(75000);
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1075000);
                    ((HoTroThanhCongCu) datPhong).xuLyHienThi();
                    bam(datPhong, "Nhận phòng");
                    var nhatKy = dk.tao(MaManHinh.NHAT_KY_CHAM_SOC);
                    nhapHopThoai(
                            "Nhật ký chăm sóc",
                            hop -> {
                                chonMa(hop, "food", "Đã ăn");
                                chonMa(hop, "health", "Khỏe mạnh");
                            },
                            () -> bam(nhatKy, "Ghi chăm sóc"));
                    assertThat(luuTruService.layNhatKyChiTiet(id)).hasSize(1);
                    var traPhong = dk.tao(MaManHinh.THANH_TOAN);
                    bam(traPhong, "Quyết toán");
                    assertThat(gio.getMaNguon()).isEqualTo(id);
                    assertThat(gio.tinhTong()).isEqualTo(75000);
                    var thanhToan = dk.tao(MaManHinh.THANH_TOAN);
                    bam(thanhToan, "Đủ tiền");
                    nhapHopThoai(
                            "Xác nhận thanh toán",
                            hop -> {},
                            () -> bam(thanhToan, "Xác nhận thanh toán (F9)"));
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM phong_hotel WHERE ma_phong=?",
                                            maPhong))
                            .isEqualTo("DANG_DON");
                    nhapHopThoai(
                            "Hoàn thành dọn phòng",
                            hop -> chonMa(hop, "room", maPhong),
                            () -> bam(datPhong, "Dọn phòng xong"));
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM phong_hotel WHERE ma_phong=?",
                                            maPhong))
                            .isEqualTo("TRONG");
                    assertThat(InHoaDonPanel.hienThi(gio.getBienLaiCuoi()))
                            .contains("Tiền cọc đã trừ", "Quyết toán Hotel");
                    assertThat(new CaThuNganService().layTienMatDuKien()).isEqualTo(1150000);
                    long thuSau =
                            thongKeService.layDoanhThuTheoNgay(1).stream()
                                    .mapToLong(d -> Math.round(d.getTongTien()))
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
            assertThatThrownBy(() -> danhMucService.laySanPham())
                    .isInstanceOf(IllegalStateException.class)
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
                    var ds = danhMucService.layKhachHang();
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
                    danhMucService.luu(ds, cu, moi);
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
                    String ma = tichDiemService.doiVoucher(maKhachHang, "IT-" + UUID.randomUUID());
                    var ds = danhMucService.layVoucher();
                    Object[] cu =
                            ds.stream().filter(d -> d[0].equals(ma)).findFirst().orElseThrow();
                    Object[] moi = cu.clone();
                    moi[3] = "10";
                    moi[8] = "Mọi hóa đơn";
                    assertThatThrownBy(() -> danhMucService.luu(ds, cu, moi))
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
                        LocalDate.now().plusDays(60).format(DinhDang.NGAY_U),
                        "1",
                        "0",
                        "0",
                        "Còn hiệu lực",
                        "",
                        "Chỉ Spa"
                    };
                    assertThatThrownBy(() -> danhMucService.luu(ds, null, taoMoi))
                            .hasMessageContaining("gắn với");
                    taoMoi[7] = maKhachHang + " - Integration Customer";
                    danhMucService.luu(ds, null, taoMoi);
                    assertThat(
                                    laySo(
                                            "SELECT khach_hang_id FROM voucher WHERE ma_voucher=?",
                                            taoMoi[0]))
                            .isEqualTo(khachHang);
                    doiVaiTro(VaiTro.NHAN_VIEN_BAN_HANG);
                    assertThatThrownBy(() -> danhMucService.luu(ds, null, taoMoi))
                            .isInstanceOf(SecurityException.class);
                });
    }

    @Test
    void luuAnhVaThongTinChiTietSanPhamKhongDoiGiaVaTon() {
        layTinhHuong(
                () -> {
                    var ds = danhMucService.laySanPham();
                    Object[] cu =
                            ds.stream()
                                    .filter(d -> d[0].equals(maSanPham))
                                    .findFirst()
                                    .orElseThrow();
                    Object[] moi = cu.clone();
                    moi[10] = "images/products/SP000001.jpg";
                    moi[11] = "Thương hiệu kiểm thử";
                    moi[12] = "Gói 100g";
                    moi[13] = "Chó";
                    moi[14] = "Mô tả có dấu";
                    long ton =
                            laySo(
                                    "SELECT SUM(so_luong_ton) FROM lo_hang WHERE san_pham_id=?",
                                    sanPham);
                    danhMucService.luu(ds, cu, moi);
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
                    assertThatThrownBy(() -> danhMucService.luu(ds, cu, moi))
                            .hasMessageContaining("images/products");
                });
    }

    @Test
    void bamAnhTrenTheThemMotLanVaDanhMucTheoDungDongChon() throws Exception {
        trenLuongSwing(
                () -> {
                    capNhat(
                            "UPDATE san_pham SET anh='images/products/SP000001.jpg' WHERE id=?",
                            sanPham);
                    GioHang gio = gioHangService.tao();
                    var pos = new com.petstation.gui.banhang.LapHoaDonPanel(gio, id -> {});
                    var txt =
                            (javax.swing.JTextField)
                                    cacThanhPhan(pos).stream()
                                            .filter(c -> c instanceof javax.swing.JTextField)
                                            .findFirst()
                                            .orElseThrow();
                    for (int i = 0; i < 2; i++) {
                        txt.setText(maSanPham);
                        txt.postActionEvent();
                    }
                    assertThat(gio.getCacDong()).hasSize(1);
                    assertThat(gio.getCacDong().get(0).getMa()).isEqualTo(maSanPham);
                    assertThat(gio.getCacDong().get(0).getSoLuong()).isEqualTo(2);
                    var dm = new com.petstation.gui.kho.DanhSachSanPhamPanel();
                    var bang =
                            (javax.swing.JTable)
                                    cacThanhPhan(dm).stream()
                                            .filter(c -> c instanceof javax.swing.JTable)
                                            .findFirst()
                                            .orElseThrow();
                    for (int i = 0; i < bang.getRowCount(); i++) {
                        if (maSanPham.equals(bang.getValueAt(i, 0))) {
                            bang.setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                    var xem =
                            (com.petstation.gui.kho.AnhSanPhamPanel)
                                    cacThanhPhan(dm).stream()
                                            .filter(
                                                    c ->
                                                            c
                                                                    instanceof
                                                                    com.petstation.gui.kho
                                                                            .AnhSanPhamPanel)
                                            .findFirst()
                                            .orElseThrow();
                    assertThat(xem.layDuongDan()).isEqualTo("images/products/SP000001.jpg");
                    bang.clearSelection();
                    assertThat(xem.layDuongDan()).isEmpty();
                });
    }

    @Test
    void banGiaoLuuKhoaNgoaiNguoiNhan() {
        layTinhHuong(
                () -> {
                    var ca = new CaThuNganService();
                    ca.mo("Ca chiều", 300000);
                    String maPhien = ca.layMaPhien();
                    var nguoi = ca.layNguoiNhan().stream().findFirst().orElseThrow();
                    ca.dong(
                            300000,
                            "",
                            CaThuNganService.HinhThucBanGiao.BAN_GIAO,
                            nguoi.maNhanVien());
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT nguoi_nhan_ban_giao FROM phien_lam_viec"
                                                    + " WHERE ma_phien=?",
                                            maPhien))
                            .isEqualTo(nguoi.maNhanVien());
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT hinh_thuc_ban_giao FROM phien_lam_viec WHERE"
                                                    + " ma_phien=?",
                                            maPhien))
                            .isEqualTo("BAN_GIAO");
                });
    }

    @Test
    void baoCaoKhoangNgayCuLayDungHaiCanVaKhongLayNgayKeTiep() {
        layTinhHuong(
                () -> {
                    mo();
                    String ca = new CaThuNganService().layMaPhien();
                    java.time.LocalDate ngay = java.time.LocalDate.of(2020, 1, 15);
                    for (int i = 0; i < 3; i++) {
                        long don =
                                them(
                                        "INSERT"
                                            + " don_hang(ma_don,phien_lam_viec_id,khach_hang_id,trang_thai)"
                                            + " VALUES(?,?,?,'DA_THANH_TOAN')",
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
                                    + " hoa_don(ma_hoa_don,don_hang_id,tong_tien,hinh_thuc_thanh_toan,tien_khach_dua,tien_thoi,diem_cong,thanh_toan_luc,idempotency_key)"
                                    + " VALUES(?,?,100000,'TIEN_MAT',100000,0,0,?,CONVERT(varchar(40),NEWID()))",
                                "ITDATE" + System.nanoTime(),
                                don,
                                luc);
                    }
                    var dong = thongKeService.layDoanhThuHoanTat(new KhoangNgay(ngay, ngay));
                    assertThat(dong).hasSize(1);
                    assertThat(Math.round(dong.get(0).getDoanhThuThuan())).isEqualTo(200000);
                    assertThat(dong.get(0).getSoDon()).isEqualTo(2);
                    assertThat(
                                    thongKeService.layDoanhThuHoanTat(
                                            new KhoangNgay(ngay.minusDays(2), ngay.minusDays(1))))
                            .isEmpty();
                });
    }

    @Test
    void moiGocNhinBaoCaoChayDuocTrenKhoangNgayCu() {
        layTinhHuong(
                () -> {
                    var ky = new KhoangNgay(LocalDate.now().minusDays(29), LocalDate.now());
                    assertThat(thongKeService.layBienDongKho(ky)).isNotNull();
                    assertThat(thongKeService.layPhieuSpaTheoTrangThai(ky)).isNotNull();
                    assertThat(thongKeService.layHopDongTrongKy(ky)).isNotNull();
                    assertThat(thongKeService.layCocTrongKy(ky)).isNotNull();
                    assertThat(thongKeService.layLuotKhach(ky)).isNotNull();
                    assertThat(thongKeService.laySuDungVoucher(ky)).isNotNull();
                    assertThat(thongKeService.layTongHopCa(ky)).isNotNull();
                    assertThat(thongKeService.layDuNoHienTai(ky)).isNotNull();
                });
    }

    @Test
    void xemHoaDonKhongCatTenDaiVaMaHoaNoiDung() {
        layTinhHuong(
                () -> {
                    mo();
                    var daLuu =
                            banHangService.thanhToan(
                                    layGioHang(1), PhuongThuc.TIEN_MAT, 100000, "Thu ngân");
                    String ten =
                            "Thức ăn chăm sóc thú cưng loại đặc biệt với tên rất dài & <chăm sóc>"
                                    + " 800g";
                    var bienLai =
                            new GioHang.BienLai(
                                    daLuu.getSoHoaDon(),
                                    LocalDateTime.now(),
                                    List.of(new GioHang.DongHang("SP", ten, 150000, 2)),
                                    300000,
                                    0,
                                    300000,
                                    PhuongThuc.TIEN_MAT,
                                    500000,
                                    200000,
                                    "Khách lẻ",
                                    "",
                                    "Thu ngân");
                    String html = InHoaDonPanel.hienThiHTML(bienLai);
                    assertThat(html)
                            .contains("tên rất dài &amp; &lt;chăm sóc&gt; 800g")
                            .contains("align='center'")
                            .contains("align='right'")
                            .contains("pet-station-logo.png");
                    assertThat(InHoaDonPanel.hienThi(bienLai)).contains(ten);
                });
    }

    @Test
    void locKetHopSapXepVaAnhKhongLechBanGhi() throws Exception {
        trenLuongSwing(
                () -> {
                    var panel = new DanhSachSanPhamPanel();
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
                            (AnhSanPhamPanel)
                                    thanhPhan.stream()
                                            .filter(c -> c instanceof AnhSanPhamPanel)
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
                            banHangService.thanhToan(
                                    layGioHang(1), PhuongThuc.TIEN_MAT, 100000, "Thu ngân");
                    var truoc = banHangService.layChiTietBienLai(daLuu.getSoHoaDon());
                    assertThat(truoc.getSoDuDiemSau()).isNotNull();
                    capNhat(
                            "UPDATE khach_hang SET diem_tich_luy=diem_tich_luy+200 WHERE id=?",
                            khachHang);
                    var sau = banHangService.layChiTietBienLai(daLuu.getSoHoaDon());
                    assertThat(sau.getSoDuDiemSau()).isEqualTo(truoc.getSoDuDiemSau());
                    assertThat(sau.getDiemCong())
                            .isEqualTo(
                                    (int)
                                            laySo(
                                                    "SELECT diem_cong FROM hoa_don WHERE"
                                                            + " ma_hoa_don=?",
                                                    daLuu.getSoHoaDon()));
                    assertThat(sau.getDienThoaiKhach()).isNotBlank();
                });
    }

    @Test
    void soDoPhongDungIdGiuPhongTrongVaMoHopDongDangO() throws Exception {
        trenLuongSwing(
                () -> {
                    mo();
                    String id =
                            luuTruService.datPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    75000,
                                    "",
                                    true);
                    var chuyen = new java.util.ArrayList<MaManHinh>();
                    var gio = gioHangService.tao();
                    var map = new TraCuuPhongHotelPanel(gio, chuyen::add);
                    var phong =
                            quyTrinhService.layPhongHotel().stream()
                                    .filter(p -> p.getMaPhong().equals(maPhong))
                                    .findFirst()
                                    .orElseThrow();
                    map.moPhong(phong);
                    assertThat(chuyen.get(chuyen.size() - 1))
                            .isEqualTo(MaManHinh.DAT_PHONG_LUU_TRU);
                    assertThat(gio.getQuyTrinh().getMaPhongHotel()).isEqualTo(maPhong);
                    luuTruService.kiemTraVao(id);
                    phong =
                            quyTrinhService.layPhongHotel().stream()
                                    .filter(p -> p.getMaPhong().equals(maPhong))
                                    .findFirst()
                                    .orElseThrow();
                    map.moPhong(phong);
                    assertThat(chuyen.get(chuyen.size() - 1)).isEqualTo(MaManHinh.NHAT_KY_CHAM_SOC);
                    assertThat(gio.getQuyTrinh().getHopDongHotel()).isEqualTo(id);
                    assertThat(TraCuuPhongHotelPanel.layChiTiet(phong))
                            .contains("Đang lưu trú")
                            .contains("Chủ nuôi");
                });
    }

    @Test
    void caiDatGiuNoiDungChuaLuuKhiQuayLai() throws Exception {
        trenLuongSwing(
                () -> {
                    var panel = new CaiDatPanel(true);
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
                    NghiepVuService truyVan = org.mockito.Mockito.spy(new NghiepVuService());
                    var panel = new TimKiemTonTheoLoPanel(gioHangService.tao(), id -> {}, truyVan);
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
                    String id =
                            String.valueOf(
                                    bang.getModel().getValueAt(bang.convertRowIndexToModel(0), 0));
                    var nhan =
                            (javax.swing.JLabel)
                                    tp.stream()
                                            .filter(
                                                    c ->
                                                            c instanceof javax.swing.JLabel l
                                                                    && l.getText() != null
                                                                    && l.getText()
                                                                            .endsWith(" kết quả"))
                                            .findFirst()
                                            .orElseThrow();
                    assertThat(nhan.getText())
                            .isEqualTo(
                                    ThanhDanhSach.soKetQua(
                                            bang.getRowCount(), bang.getModel().getRowCount()));
                    bam(panel, "Làm mới");
                    assertThat(tim.getText()).isEqualTo(maSanPham);
                    assertThat(bang.getSelectedRow()).isNotNegative();
                    assertThat(bang.getRowSorter().getSortKeys())
                            .isEqualTo(
                                    java.util.List.of(
                                            new javax.swing.RowSorter.SortKey(
                                                    1, javax.swing.SortOrder.DESCENDING)));
                    assertThat(
                                    String.valueOf(
                                            bang.getModel()
                                                    .getValueAt(
                                                            bang.convertRowIndexToModel(
                                                                    bang.getSelectedRow()),
                                                            0)))
                            .isEqualTo(id);
                    tim.setText("KHONG-CO-KET-QUA");
                    assertThat(nhan.getText())
                            .isEqualTo(ThanhDanhSach.soKetQua(0, bang.getModel().getRowCount()));
                    org.mockito.Mockito.verify(truyVan, org.mockito.Mockito.times(2))
                            .layCacDong(LoaiNghiepVu.LO_HANG);
                });
    }

    @Test
    void thanhToanLoiPhanBoRollbackTonDiemVaHoaDon() {
        layTinhHuong(
                () -> {
                    mo();
                    GioHang gio = layGioHang(1);
                    long ton = laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang1),
                            hd = laySo("SELECT COUNT(*) FROM hoa_don");
                    assertThatThrownBy(
                                    () ->
                                            banHangService.thanhToan(
                                                    gio,
                                                    PhuongThuc.TIEN_MAT,
                                                    1,
                                                    "Integration Manager"))
                            .isInstanceOf(IllegalArgumentException.class);
                    assertThat(laySo("SELECT COUNT(*) FROM hoa_don")).isEqualTo(hd);
                    assertThat(laySo("SELECT so_luong_ton FROM lo_hang WHERE id=?", loHang1))
                            .isEqualTo(ton);
                    assertThat(laySo("SELECT diem_tich_luy FROM khach_hang WHERE id=?", khachHang))
                            .isEqualTo(100);
                });
    }

    @Test
    void cocNhieuDotGiuPhongChanNhanVaDoiMucCoLyDo() {
        layTinhHuong(
                () -> {
                    String id =
                            luuTruService.giuPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    "Integration");
                    assertThat(luuTruService.layMucCoc(id)).containsExactly(75000, 0);
                    assertThatThrownBy(() -> luuTruService.kiemTraVao(id))
                            .isInstanceOf(IllegalArgumentException.class);
                    mo();
                    luuTruService.thuCoc(id, PhuongThuc.TIEN_MAT, 25000, 50000, "COC-A");
                    luuTruService.thuCoc(id, PhuongThuc.TIEN_MAT, 25000, 50000, "COC-A");
                    assertThat(luuTruService.layMucCoc(id)).containsExactly(75000, 25000);
                    assertThat(luuTruService.layCoc(id)).hasSize(1);
                    assertThatThrownBy(() -> luuTruService.kiemTraVao(id))
                            .isInstanceOf(IllegalArgumentException.class);
                    luuTruService.thuCoc(id, PhuongThuc.CHUYEN_KHOAN, 50000, 50000, "COC-B");
                    assertThat(new CaThuNganService().layTienCocHotel()).isEqualTo(25000);
                    assertThatThrownBy(() -> luuTruService.dieuChinhCoc(id, 0, "Lý do"))
                            .isInstanceOf(IllegalArgumentException.class);
                    luuTruService.dieuChinhCoc(id, 100000, "Khách yêu cầu giữ phòng dài hơn");
                    assertThatThrownBy(() -> luuTruService.kiemTraVao(id))
                            .isInstanceOf(IllegalArgumentException.class);
                    luuTruService.dieuChinhCoc(id, 75000, "Đã xác nhận lịch");
                    luuTruService.kiemTraVao(id);
                    assertThat(
                                    layGiaTriDon(
                                            "SELECT trang_thai FROM hop_dong_luu_tru WHERE id=?",
                                            id))
                            .isEqualTo("DANG_O");
                });
    }

    @Test
    void suaLichKhongTuTrungNhungChanTrungVoiLichKhac() {
        layTinhHuong(
                () -> {
                    LocalDateTime start = LocalDateTime.now().plusDays(1);
                    String id = spaService.datLichHen(maThuCung, maGoiDichVu, maNhanVien, start);
                    spaService.suaLichHen(
                            id, maThuCung, maGoiDichVu, maNhanVien, start.plusHours(2));
                    spaService.datLichHen(maThuCung, maGoiDichVu, maNhanVien, start);
                    assertThatThrownBy(
                                    () ->
                                            spaService.suaLichHen(
                                                    id,
                                                    maThuCung,
                                                    maGoiDichVu,
                                                    maNhanVien,
                                                    start.plusMinutes(15)))
                            .hasMessageContaining("trùng giờ");
                });
    }

    @Test
    void phieuChamSocGiuNoiDungVaChanGhiSauDongPhieu() {
        layTinhHuong(
                () -> {
                    String id = spaService.tiepNhan(maThuCung, "", 4, "Khỏe");
                    ChamSocService chamSocService = new ChamSocService();
                    chamSocService.luu(
                            "SPA",
                            id,
                            "Khỏe mạnh",
                            "Ăn theo hướng dẫn",
                            "Theo chỉ dẫn chủ nuôi",
                            "Tắm",
                            null,
                            null);
                    assertThat(chamSocService.lay("SPA", id))
                            .singleElement()
                            .satisfies(
                                    d -> {
                                        assertThat(d.getTinhTrang()).isEqualTo("Khỏe mạnh");
                                        assertThat(d.getDaThucHien()).isEqualTo("Tắm");
                                    });
                    capNhat(
                            "UPDATE phieu_tiep_nhan_spa SET trang_thai='DA_THANH_TOAN' WHERE"
                                    + " ma_phieu=?",
                            id);
                    assertThatThrownBy(
                                    () ->
                                            chamSocService.luu(
                                                    "SPA", id, "Khỏe", "", "", "", null, null))
                            .hasMessageContaining("không còn");
                });
    }

    @Test
    void moiGocNhinBaoCaoDocDuocChungTuTrongCungPhamVi() {
        layTinhHuong(
                () -> {
                    String[][] reports = {
                        {
                            "Thống kê doanh thu",
                            "Doanh thu hoàn tất",
                            "Theo ngày",
                            "Theo thanh toán",
                            "Theo thu ngân",
                            "Theo nguồn",
                            "Theo ca"
                        },
                        {
                            "Thống kê hàng hóa",
                            "Biến động trong kỳ",
                            "Sản phẩm bán chạy",
                            "Tồn kho hiện tại",
                            "Sắp hết hạn"
                        },
                        {
                            "Thống kê dịch vụ Spa",
                            "Phiếu theo trạng thái",
                            "Dịch vụ đã thanh toán",
                            "Trạng thái lịch hẹn"
                        },
                        {
                            "Thống kê lưu trú",
                            "Hợp đồng nhận trong kỳ",
                            "Cọc nhận trong kỳ",
                            "Trạng thái phòng hiện tại",
                            "Lưu trú đã quyết toán"
                        },
                        {
                            "Thống kê khách hàng",
                            "Khách mới / quay lại",
                            "Lượt dùng voucher trong kỳ",
                            "Chi tiêu theo kỳ",
                            "Sử dụng voucher"
                        },
                        {"Công nợ nhà cung cấp", "Dư nợ hiện tại", "Công nợ theo kỳ nhập"},
                        {"Thống kê nhân viên", "Thu tiền và chênh lệch ca", "Lịch làm việc"}
                    };
                    for (String[] r : reports)
                        for (int i = 1; i < r.length; i++) {
                            var data = chiTietThongKeService.lay(r[0], r[i], KhoangNgay.ganDay(30));
                            assertThat(data).as(r[0] + " / " + r[i]).isNotNull();
                            for (Object[] row : data.getDong())
                                assertThat(row).hasSize(data.getCot().length);
                        }
                });
    }

    @Test
    void cocQuaHaiCaVaMotLanThuKetHopKhongDemTrung() {
        layTinhHuong(
                () -> {
                    long countBefore =
                            thongKeService.layCocTrongKy(KhoangNgay.ganDay(1)).stream()
                                    .mapToLong(ChiTietBaoCao.CocTrongKy::getSoLanNhan)
                                    .sum();
                    String id =
                            luuTruService.giuPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    "");
                    mo();
                    var ca = new CaThuNganService();
                    String first = ca.layMaPhien();
                    luuTruService.thuCoc(id, PhuongThuc.TIEN_MAT, 25000, 25000, "LAN-1");
                    ca.dong(1025000, "");
                    ca.mo("Integration second", 0);
                    String second = ca.layMaPhien();
                    luuTruService.thuCoc(id, PhuongThuc.CHUYEN_KHOAN, 50000, 50000, "LAN-2");
                    assertThat(ca.layTienMatDuKien()).isZero();
                    assertThat(ca.layTongCocHotel()).isEqualTo(50000);
                    assertThat(
                                    laySo(
                                            "SELECT COALESCE(SUM(so_tien), 0) FROM"
                                                    + " lich_su_thu_coc_hotel WHERE"
                                                    + " ma_hop_dong=? AND ma_phien=?",
                                            id,
                                            first))
                            .isEqualTo(25000);
                    assertThat(
                                    laySo(
                                            "SELECT COALESCE(SUM(so_tien), 0) FROM"
                                                    + " lich_su_thu_coc_hotel WHERE"
                                                    + " ma_hop_dong=? AND ma_phien=?",
                                            id,
                                            second))
                            .isEqualTo(50000);
                    long countAfter =
                            thongKeService.layCocTrongKy(KhoangNgay.ganDay(1)).stream()
                                    .mapToLong(ChiTietBaoCao.CocTrongKy::getSoLanNhan)
                                    .sum();
                    assertThat(countAfter - countBefore).isEqualTo(2);
                    luuTruService.kiemTraVao(id);
                });
    }

    @Test
    void nhanVienKhongDuocTuGiamMucCoc() {
        layTinhHuong(
                () -> {
                    String id =
                            luuTruService.giuPhong(
                                    maThuCung,
                                    maPhong,
                                    LocalDateTime.now(),
                                    LocalDateTime.now().plusDays(1),
                                    "");
                    PhienDangNhapService.macDinh()
                            .datPhienDangNhap(
                                    new PhienDangNhap(
                                            new TaiKhoan(
                                                    maNhanVien,
                                                    quanLy.getNhanVien(),
                                                    "hash",
                                                    VaiTro.NHAN_VIEN_SPA_HOTEL,
                                                    TrangThaiTaiKhoan.HOAT_DONG,
                                                    0,
                                                    null,
                                                    false),
                                            quanLy.getNhanVien(),
                                            Set.of(Quyen.QUAN_LY_LUU_TRU)));
                    assertThatThrownBy(() -> luuTruService.dieuChinhCoc(id, 0, "Tự giảm"))
                            .hasMessageContaining("Chỉ quản lý");
                    PhienDangNhapService.macDinh().datPhienDangNhap(quanLy);
                    assertThat(luuTruService.layMucCoc(id)).containsExactly(75000, 0);
                    luuTruService.dieuChinhCoc(id, 0, "Quản lý cho miễn cọc");
                    luuTruService.kiemTraVao(id);
                    assertThat(luuTruService.tinhTienLuuTruConLai(id)).isEqualTo(150000);
                });
    }
}
