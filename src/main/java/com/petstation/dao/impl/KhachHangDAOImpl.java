package com.petstation.dao.impl;

import com.petstation.dao.KhachHangDAO;
import com.petstation.database.TruyCapDuLieuException;
import com.petstation.model.KhachHang;
import com.petstation.util.DuLieu;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhachHangDAOImpl implements KhachHangDAO {
    private final Connection ketNoi;

    public KhachHangDAOImpl(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    @Override
    public KhachHang tao(KhachHang khachHang) {
        String sql =
                "INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, "
                        + "ngay_sinh, diem_tich_luy, hang_thanh_vien, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            String ma = khachHang.getMaKhachHang();
            if (ma == null || ma.isBlank()) {
                ma = DuLieu.taoMa("KH");
                khachHang.setMaKhachHang(ma);
            }
            cauLenh.setString(1, ma);
            cauLenh.setString(2, khachHang.getHoTen());
            cauLenh.setString(3, khachHang.getSoDienThoai());
            cauLenh.setString(4, khachHang.getDiaChi());
            cauLenh.setDate(
                    5,
                    khachHang.getNgaySinh() != null ? Date.valueOf(khachHang.getNgaySinh()) : null);
            cauLenh.setInt(6, khachHang.getDiemTichLuy() != null ? khachHang.getDiemTichLuy() : 0);
            cauLenh.setString(
                    7,
                    khachHang.getHangThanhVien() != null
                            ? khachHang.getHangThanhVien()
                            : "REGULAR");
            cauLenh.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int biAnhHuongCacDong = cauLenh.executeUpdate();
            if (biAnhHuongCacDong == 0) {
                throw new TruyCapDuLieuException("Creating customer failed, no rows affected.");
            }

            return khachHang;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error creating customer: " + loi.getMessage(), loi);
        }
    }

    @Override
    public Optional<KhachHang> timTheoId(Long id) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = ?)";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            cauLenh.setLong(2, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customer by ID: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    @Override
    public Optional<KhachHang> timTheoMaKhachHang(String maKhachHang) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ma_khach_hang = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customer by code: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    @Override
    public Optional<KhachHang> timTheoSoDienThoai(String soDienThoai) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE so_dien_thoai = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, soDienThoai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                if (tapKetQua.next()) {
                    return Optional.of(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customer by phone: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    @Override
    public List<KhachHang> timTheoNgaySinh(Date ngaySinh) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ngay_sinh = ? ORDER BY ho_ten";

        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setDate(1, ngaySinh);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customers by birth date: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    @Override
    public List<KhachHang> timTheoKhoangNgayTao(
            LocalDateTime tuThoiDiem, LocalDateTime denThoiDiem) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ngay_tao BETWEEN ? AND ? ORDER BY ngay_tao DESC";

        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(tuThoiDiem));
            cauLenh.setTimestamp(2, Timestamp.valueOf(denThoiDiem));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customers by creation date: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    @Override
    public List<KhachHang> timTheoHangThanhVien(String hang) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE hang_thanh_vien = ? ORDER BY diem_tich_luy DESC";

        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, hang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding customers by tier: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    @Override
    public List<KhachHang> timTatCa() {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang ORDER BY ho_ten";

        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding all customers: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    @Override
    public List<KhachHang> timKiemTheoTen(String ten) {
        String sql =
                "SELECT ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ho_ten LIKE ? ORDER BY ho_ten";

        List<KhachHang> cacKhachHang = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, "%" + (ten == null ? "" : ten) + "%");
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacKhachHang.add(anhXaKetQuaDatDenKhachHang(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error finding customers by name: " + loi.getMessage(), loi);
        }

        return cacKhachHang;
    }

    @Override
    public boolean capNhat(KhachHang khachHang) {
        String sql =
                "UPDATE khach_hang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, "
                        + "ngay_sinh = ?, diem_tich_luy = ?, hang_thanh_vien = ? "
                        + "WHERE ma_khach_hang = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, khachHang.getHoTen());
            cauLenh.setString(2, khachHang.getSoDienThoai());
            cauLenh.setString(3, khachHang.getDiaChi());
            cauLenh.setDate(
                    4,
                    khachHang.getNgaySinh() != null ? Date.valueOf(khachHang.getNgaySinh()) : null);
            cauLenh.setInt(5, khachHang.getDiemTichLuy());
            cauLenh.setString(6, khachHang.getHangThanhVien());
            cauLenh.setString(7, khachHang.getMaKhachHang());

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error updating customer: " + loi.getMessage(), loi);
        }
    }

    @Override
    public boolean capNhatDiemTichLuy(Long idKhachHang, Integer diem) {
        String sql = "UPDATE khach_hang SET diem_tich_luy = ? WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = ?)";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setInt(1, diem);
            cauLenh.setLong(2, idKhachHang);
            cauLenh.setLong(3, idKhachHang);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating loyalty points: " + loi.getMessage(), loi);
        }
    }

    @Override
    public boolean capNhatHangThanhVien(Long idKhachHang, String hang) {
        String sql = "UPDATE khach_hang SET hang_thanh_vien = ? WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = ?)";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, hang);
            cauLenh.setLong(2, idKhachHang);
            cauLenh.setLong(3, idKhachHang);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error updating membership tier: " + loi.getMessage(), loi);
        }
    }

    @Override
    public boolean xoa(Long id) {
        String sql = "DELETE FROM khach_hang WHERE (ma_khach_hang = CAST(? AS VARCHAR) OR TRY_CAST(SUBSTRING(ma_khach_hang, 3, 20) AS BIGINT) = ?)";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            cauLenh.setLong(2, id);

            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error deleting customer: " + loi.getMessage(), loi);
        }
    }

    @Override
    public boolean layTonTaiTheoSoDienThoai(String soDienThoai) {
        return kiemTraTonTaiTheoSoDienThoai(soDienThoai);
    }

    @Override
    public boolean kiemTraTonTaiTheoSoDienThoai(String soDienThoai) {
        String sql = "SELECT COUNT(*) FROM khach_hang WHERE so_dien_thoai = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, soDienThoai);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking customer by phone: " + loi.getMessage(), loi);
        }

        return false;
    }

    @Override
    public boolean kiemTraTonTaiTheoMaKhachHang(String maKhachHang) {
        String sql = "SELECT COUNT(*) FROM khach_hang WHERE ma_khach_hang = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, maKhachHang);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                if (tapKetQua.next()) {
                    return tapKetQua.getInt(1) > 0;
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error checking customer by code: " + loi.getMessage(), loi);
        }

        return false;
    }

    @Override
    public long dem() {
        return demSoLuong();
    }

    @Override
    public long demSoLuong() {
        String sql = "SELECT COUNT(*) FROM khach_hang";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException("Error counting customers: " + loi.getMessage(), loi);
        }

        return 0;
    }

    private KhachHang anhXaKetQuaDatDenKhachHang(ResultSet tapKetQua) throws SQLException {
        KhachHang khachHang = new KhachHang();
        String maKhachHang = tapKetQua.getString("ma_khach_hang");
        khachHang.setMaKhachHang(maKhachHang);
        khachHang.setHoTen(tapKetQua.getString("ho_ten"));
        khachHang.setSoDienThoai(tapKetQua.getString("so_dien_thoai"));
        khachHang.setDiaChi(tapKetQua.getString("dia_chi"));

        Date ngaySinh = tapKetQua.getDate("ngay_sinh");
        if (ngaySinh != null) {
            khachHang.setNgaySinh(ngaySinh.toLocalDate());
        }

        khachHang.setDiemTichLuy(tapKetQua.getInt("diem_tich_luy"));
        khachHang.setHangThanhVien(tapKetQua.getString("hang_thanh_vien"));

        Timestamp thoiDiemTao = tapKetQua.getTimestamp("ngay_tao");
        if (thoiDiemTao != null) {
            khachHang.setThoiDiemTao(thoiDiemTao.toLocalDateTime());
        }

        khachHang.setPhienBanBanGhi(tapKetQua.getBytes("row_version"));

        return khachHang;
    }
}
