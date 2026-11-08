package com.petstation.dao.impl;

import com.petstation.dao.KhoDAO;
import com.petstation.database.ThaoTacSQL;
import com.petstation.util.BoiCanhDangNhap;
import com.petstation.util.DuLieu;

import java.util.List;

// Triển khai truy vấn SQL cho các nghiệp vụ Kho (Nhập kho & Công nợ NCC, Xuất hủy, Kiểm kê).
public final class KhoDAOImpl implements KhoDAO {
    public KhoDAOImpl() {}

    // 1. NGHIỆP VỤ NHẬP KHO & CÔNG NỢ NHÀ CUNG CẤP

    // Kiểm tra sản phẩm có bật cờ theo_doi_hsd = 1 hay không
    public long demNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM san_pham WITH(UPDLOCK,HOLDLOCK)"
                        + " WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND theo_doi_hsd=1",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Kiểm tra nhà cung cấp có đang ở trạng thái DANG_HOP_TAC hay không
    public long demNhaCungCapDangHopTac(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM nha_cung_cap WHERE (ma_nha_cung_cap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nha_cung_cap, PATINDEX('%[0-9]%', ma_nha_cung_cap), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='DANG_HOP_TAC'",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Thêm mới bản ghi phiếu nhập kho (phieu_nhap_kho) và trả về ID cầu nối
    public long themNhapKho(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        String maNcc = cacThamSo[1] != null ? String.valueOf(cacThamSo[1]) : "";
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> rows = ThaoTacSQL.truyVan(
                    "SELECT ma_nha_cung_cap FROM nha_cung_cap WHERE (ma_nha_cung_cap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_nha_cung_cap, PATINDEX('%[0-9]%', ma_nha_cung_cap), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!rows.isEmpty()) {
                maNcc = String.valueOf(rows.get(0)[0]);
            }
        }
        String tenDangNhap = BoiCanhDangNhap.layTenDangNhap();
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_nhap_kho(ma_phieu_nhap, ma_nha_cung_cap, ten_dang_nhap, ngay_nhap, tong_tien, trang_thai, da_tra) "
                        + "VALUES(?, ?, ?, CAST(GETDATE() AS DATE), ?, 'DA_NHAP', ?)",
                maPhieu,
                maNcc,
                tenDangNhap,
                cacThamSo[3],
                cacThamSo[4]);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    // Thêm dòng chi tiết sản phẩm, lô hàng, hạn dùng, số lượng, giá nhập vào chi_tiet_phieu_nhap
    public int themChiTietNhapKho(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_phieu_nhap FROM phieu_nhap_kho WHERE (ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_nhap, PATINDEX('%[0-9]%', ma_phieu_nhap), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maPhieu = String.valueOf(r.get(0)[0]);
        }
        String maSP = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_san_pham FROM san_pham WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maSP = String.valueOf(r.get(0)[0]);
        }
        String maLo = String.valueOf(cacThamSo[2]);
        if (cacThamSo[2] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[2], cacThamSo[2]);
            if (!r.isEmpty()) maLo = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_phieu_nhap(ma_phieu_nhap, ma_san_pham, ma_lo, han_su_dung, so_luong, gia_nhap) "
                        + "VALUES(?, ?, ?, ?, ?, ?)",
                maPhieu,
                maSP,
                maLo,
                cacThamSo[3],
                cacThamSo[4],
                cacThamSo[5]);
    }

    // Tìm lô hàng theo mã sản phẩm và số lô/mã lô (có khóa UPDLOCK để tránh xung đột giao dịch)
    public List<Object[]> timNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo, han_su_dung FROM lo_hang WITH(UPDLOCK,HOLDLOCK) "
                        + "WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) "
                        + "AND (so_lo = ? OR ma_lo = ?)",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    // Tạo mới một lô hàng trong bảng lo_hang khi nhập lô mới
    public int themLoHang(Object... cacThamSo) {
        String maSP = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_san_pham FROM san_pham WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maSP = String.valueOf(r.get(0)[0]);
        }
        String soLo = String.valueOf(cacThamSo[1]);
        String maLo = soLo.matches("LO\\d{8}") ? soLo : DuLieu.taoMa("LO");
        return ThaoTacSQL.capNhat(
                "INSERT INTO lo_hang(ma_lo, ma_san_pham, so_lo, ngay_nhap, han_su_dung, so_luong_nhap, so_luong_ton) "
                        + "VALUES(?, ?, ?, CAST(GETDATE() AS DATE), ?, ?, ?)",
                maLo,
                maSP,
                soLo,
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[3]);
    }

    // Cộng dồn số lượng tồn và số lượng nhập cho lô hàng đã tồn tại
    public int capNhatNhapKho(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton+?, so_luong_nhap=COALESCE(so_luong_nhap,0)+? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    // Cập nhật giá nhập gần nhất của sản phẩm sau khi nhập kho
    public int capNhatGiaNhapGanNhat(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE san_pham SET gia_nhap_gan_nhat=? WHERE (ma_san_pham = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_san_pham, PATINDEX('%[0-9]%', ma_san_pham), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1]);
    }

    // Lấy danh sách các dòng chi tiết (tên SP, mã lô, HSD, số lượng, giá nhập) của phiếu nhập
    public List<Object[]> layNhapKhoChiTiet(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT p.ten, c.ma_lo, c.han_su_dung, c.so_luong, c.gia_nhap FROM chi_tiet_phieu_nhap c JOIN san_pham p ON p.ma_san_pham = c.ma_san_pham WHERE (c.ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_phieu_nhap, PATINDEX('%[0-9]%', c.ma_phieu_nhap), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY p.ma_san_pham",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Tính số tiền công nợ còn lại (tong_tien - da_tra) của một phiếu nhập kho
    public long layCongNoConLai(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COALESCE(MAX(tong_tien - da_tra), 0) FROM phieu_nhap_kho WHERE (ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_nhap, PATINDEX('%[0-9]%', ma_phieu_nhap), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Cộng thêm số tiền thanh toán công nợ cho nhà cung cấp (đảm bảo da_tra <= tong_tien)
    public int themThanhToanNhaCungCap(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_nhap_kho SET da_tra=da_tra+? WHERE (ma_phieu_nhap = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_nhap, PATINDEX('%[0-9]%', ma_phieu_nhap), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND da_tra+?<=tong_tien AND trang_thai='DA_NHAP'",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    // 2. NGHIỆP VỤ XUẤT HỦY HÀNG HÓA

    // Lấy tồn kho hiện tại và giá vốn (gia_nhap_gan_nhat hoặc gia_ban) của lô hàng cần xuất hủy
    public List<Object[]> timXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT l.so_luong_ton, COALESCE(p.gia_nhap_gan_nhat, p.gia_ban) "
                        + "FROM lo_hang l WITH(UPDLOCK,HOLDLOCK) JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham "
                        + "WHERE (l.ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(l.ma_lo, PATINDEX('%[0-9]%', l.ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Thêm mới phiếu xuất hủy vào phieu_xuat_kho với trạng thái mặc định CHO_XUAT
    public long themXuatHuy(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        String tenDangNhap = BoiCanhDangNhap.layTenDangNhap();
        ThaoTacSQL.capNhat(
                "INSERT INTO phieu_xuat_kho(ma_phieu_xuat, ten_dang_nhap, loai_xuat, ly_do, tong_gia_tri, trang_thai) "
                        + "VALUES(?, ?, 'XUAT_HUY', ?, ?, 'CHO_XUAT')",
                maPhieu,
                tenDangNhap,
                cacThamSo[2],
                cacThamSo[3]);
        return DuLieu.layIdCauNoi(null, maPhieu);
    }

    // Thêm dòng chi tiết lô hàng xuất hủy vào bảng chi_tiet_phieu_xuat
    public int capNhatXuatHuy(Object... cacThamSo) {
        String maPhieu = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_phieu_xuat FROM phieu_xuat_kho WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, PATINDEX('%[0-9]%', ma_phieu_xuat), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maPhieu = String.valueOf(r.get(0)[0]);
        }
        String maLo = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maLo = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_phieu_xuat(ma_phieu_xuat, ma_lo, so_luong, gia_tri) VALUES(?, ?, ?, ?)",
                maPhieu,
                maLo,
                cacThamSo[2],
                cacThamSo[3]);
    }

    // Đếm kiểm tra phiếu xuất hủy đang ở trạng thái CHO_XUAT (khóa dòng để chuẩn bị duyệt xuất)
    public long demApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM phieu_xuat_kho WITH(UPDLOCK,HOLDLOCK) WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, PATINDEX('%[0-9]%', ma_phieu_xuat), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='CHO_XUAT'",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Lấy danh sách mã lô và số lượng trong chi_tiet_phieu_xuat để thực hiện trừ tồn
    public List<Object[]> timApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo,so_luong FROM chi_tiet_phieu_xuat WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, PATINDEX('%[0-9]%', ma_phieu_xuat), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Trừ số lượng tồn của lô hàng (chỉ trừ khi so_luong_ton >= số lượng cần xuất)
    public int truTonXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=so_luong_ton-? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND so_luong_ton>=?",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    // Chuyển trạng thái phiếu xuất kho sang DA_XUAT sau khi đã trừ tồn thành công
    public int capNhatApDungXuatHuy(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE phieu_xuat_kho SET trang_thai='DA_XUAT' WHERE (ma_phieu_xuat = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_phieu_xuat, PATINDEX('%[0-9]%', ma_phieu_xuat), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // 3. NGHIỆP VỤ KIỂM KÊ TỒN KHO

    // Lấy toàn bộ danh sách lô hàng sẵn sàng của các sản phẩm đang bán để kiểm kê theo đợt
    public List<Object[]> layDanhSachLoHangKiemKe(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT l.ma_lo, p.ma_san_pham, p.ten, COALESCE(p.barcode, ''), l.so_lo, l.han_su_dung, l.so_luong_ton, p.nhom_hang FROM lo_hang l JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham WHERE l.trang_thai = 'SAN_SANG' AND p.trang_thai = 'DANG_BAN' ORDER BY p.ma_san_pham, l.han_su_dung",
                cacThamSo);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    // Lấy số lượng tồn hệ thống hiện tại của một lô hàng cụ thể (có khóa UPDLOCK)
    public List<Object[]> timKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT so_luong_ton FROM lo_hang WITH(UPDLOCK,HOLDLOCK) WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Thêm mới biên bản kiểm kê vào bảng bien_ban_kiem_ke
    public long themKiemKe(Object... cacThamSo) {
        String maBienBan = String.valueOf(cacThamSo[0]);
        String tenDangNhap = BoiCanhDangNhap.layTenDangNhap();
        ThaoTacSQL.capNhat(
                "INSERT INTO bien_ban_kiem_ke(ma_bien_ban, ten_dang_nhap, ky_kiem_ke, trang_thai) VALUES(?, ?, ?, ?)",
                maBienBan,
                tenDangNhap,
                cacThamSo[2],
                cacThamSo[3]);
        return DuLieu.layIdCauNoi(null, maBienBan);
    }

    // Thêm dòng chi tiết kiểm kê (tồn hệ thống, tồn thực tế, nguyên nhân) vào chi_tiet_kiem_ke
    public int capNhatKiemKe(Object... cacThamSo) {
        String maBienBan = String.valueOf(cacThamSo[0]);
        if (cacThamSo[0] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_bien_ban FROM bien_ban_kiem_ke WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, PATINDEX('%[0-9]%', ma_bien_ban), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[0], cacThamSo[0]);
            if (!r.isEmpty()) maBienBan = String.valueOf(r.get(0)[0]);
        }
        String maLo = String.valueOf(cacThamSo[1]);
        if (cacThamSo[1] instanceof Number) {
            List<Object[]> r = ThaoTacSQL.truyVan(
                    "SELECT ma_lo FROM lo_hang WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                    cacThamSo[1], cacThamSo[1]);
            if (!r.isEmpty()) maLo = String.valueOf(r.get(0)[0]);
        }
        return ThaoTacSQL.capNhat(
                "INSERT INTO chi_tiet_kiem_ke(ma_bien_ban, ma_lo, ton_he_thong, ton_thuc_te, nguyen_nhan) VALUES(?, ?, ?, ?, ?)",
                maBienBan,
                maLo,
                cacThamSo[2],
                cacThamSo[3],
                cacThamSo[4]);
    }

    // Lấy danh sách chi tiết các lô hàng và độ lệch trong một biên bản kiểm kê
    public List<Object[]> layChiTietKiemKe(Object... cacThamSo) {
        List<Object[]> rows = ThaoTacSQL.truyVan(
                "SELECT c.ma_lo, p.ma_san_pham, p.ten, l.so_lo, l.han_su_dung, c.ton_he_thong, c.ton_thuc_te, (c.ton_thuc_te - c.ton_he_thong) AS chenh_lech, COALESCE(c.nguyen_nhan, N'') FROM chi_tiet_kiem_ke c JOIN lo_hang l ON l.ma_lo = c.ma_lo JOIN san_pham p ON p.ma_san_pham = l.ma_san_pham WHERE (c.ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(c.ma_bien_ban, PATINDEX('%[0-9]%', c.ma_bien_ban), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) ORDER BY p.ma_san_pham",
                cacThamSo[0],
                cacThamSo[0]);
        for (Object[] r : rows) {
            if (r[0] != null) {
                r[0] = DuLieu.layIdCauNoi(null, String.valueOf(r[0]));
            }
        }
        return rows;
    }

    // Kiểm tra biên bản kiểm kê có đang ở trạng thái CHO_DUYET hay không
    public long demApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.laySo(
                "SELECT COUNT(*) FROM bien_ban_kiem_ke WITH(UPDLOCK,HOLDLOCK) WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, PATINDEX('%[0-9]%', ma_bien_ban), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND trang_thai='CHO_DUYET'",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Lấy danh sách mã lô, tồn hệ thống và tồn thực tế của biên bản kiểm kê để cân bằng kho
    public List<Object[]> timApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.truyVan(
                "SELECT ma_lo, ton_he_thong, ton_thuc_te FROM chi_tiet_kiem_ke WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, PATINDEX('%[0-9]%', ma_bien_ban), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }

    // Cập nhật tồn kho của lô hàng bằng tồn thực tế (chỉ cập nhật khi tồn hiện tại khớp tồn lúc kiểm kê)
    public int capNhatApDungKiemKe(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE lo_hang SET so_luong_ton=? WHERE (ma_lo = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_lo, PATINDEX('%[0-9]%', ma_lo), 20) AS BIGINT) = TRY_CAST(? AS BIGINT)) AND so_luong_ton=?",
                cacThamSo[0],
                cacThamSo[1],
                cacThamSo[1],
                cacThamSo[2]);
    }

    // Đánh dấu biên bản kiểm kê sang trạng thái DA_DUYET sau khi cân bằng kho xong
    public int danhDauKiemKeDaDuyet(Object... cacThamSo) {
        return ThaoTacSQL.capNhat(
                "UPDATE bien_ban_kiem_ke SET trang_thai='DA_DUYET' WHERE (ma_bien_ban = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_bien_ban, PATINDEX('%[0-9]%', ma_bien_ban), 20) AS BIGINT) = TRY_CAST(? AS BIGINT))",
                cacThamSo[0],
                cacThamSo[0]);
    }
}
