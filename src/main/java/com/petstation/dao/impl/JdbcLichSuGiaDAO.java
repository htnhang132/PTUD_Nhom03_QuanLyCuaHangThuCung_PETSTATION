package com.petstation.dao.impl;

import com.petstation.dao.LichSuGiaDAO;
import com.petstation.dao.TruyCapDuLieuException;
import com.petstation.model.LichSuGia;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Thực hiện truy vấn JDBC cho JDBC lịch sử giá DAO. */
public class JdbcLichSuGiaDAO implements LichSuGiaDAO {

    /** Kết nối. */
    private final Connection ketNoi;

    /**
     * Khởi tạo JdbcLichSuGiaDAO với các giá trị được cung cấp.
     *
     * @param ketNoi kết nối
     */
    public JdbcLichSuGiaDAO(Connection ketNoi) {
        this.ketNoi = ketNoi;
    }

    /**
     * Tạo trong JdbcLichSuGiaDAO.
     *
     * @param lichSuGia lịch sử giá
     * @return kết quả tạo
     */
    @Override
    public LichSuGia tao(LichSuGia lichSuGia) {
        // SQL.
        String sql =
                "INSERT INTO lich_su_gia (san_pham_id, gia_cu, gia_moi, ly_do, "
                        + "nguoi_thay_doi_id, thoi_diem_thay_doi, loai_thay_doi) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement cauLenh =
                ketNoi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cauLenh.setLong(1, lichSuGia.layIdSanPham());
            cauLenh.setBigDecimal(2, lichSuGia.layGiaCu());
            cauLenh.setBigDecimal(3, lichSuGia.layGiaMoi());
            cauLenh.setString(4, lichSuGia.layLyDo());
            cauLenh.setLong(5, lichSuGia.layDaThayDoiTheo());
            cauLenh.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            lichSuGia.layDaThayDoiTai() != null
                                    ? lichSuGia.layDaThayDoiTai()
                                    : LocalDateTime.now()));
            cauLenh.setString(
                    7, lichSuGia.layKieuThayDoi() != null ? lichSuGia.layKieuThayDoi() : "MANUAL");

            cauLenh.executeUpdate();
            try (ResultSet duocSinhCacKhoa = cauLenh.getGeneratedKeys()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (duocSinhCacKhoa.next()) {
                    lichSuGia.datId(duocSinhCacKhoa.getLong(1));
                }
            }

            return lichSuGia;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error creating price history: " + loi.getMessage(), loi);
        }
    }

    /**
     * Tìm theo id trong JdbcLichSuGiaDAO.
     *
     * @param id id
     * @return kết quả tìm theo id
     */
    @Override
    public Optional<LichSuGia> timTheoId(Long id) {
        // SQL.
        String sql =
                "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id,"
                        + " thoi_diem_thay_doi, loai_thay_doi, rowversion FROM lich_su_gia WHERE"
                        + " lich_su_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaLichSuKetQuaDatDenGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price history: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm theo id sản phẩm trong JdbcLichSuGiaDAO.
     *
     * @param idSanPham id sản phẩm
     * @return kết quả tìm theo id sản phẩm
     */
    @Override
    public List<LichSuGia> timTheoIdSanPham(Long idSanPham) {
        // SQL.
        String sql =
                "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, "
                        + "thoi_diem_thay_doi, loai_thay_doi, rowversion "
                        + "FROM lich_su_gia WHERE san_pham_id = ? ORDER BY thoi_diem_thay_doi DESC";

        // Các lịch sử.
        List<LichSuGia> cacLichSu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idSanPham);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLichSu.add(anhXaLichSuKetQuaDatDenGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price history by product: " + loi.getMessage(), loi);
        }

        return cacLichSu;
    }

    /**
     * Tìm theo đã thay đổi theo trong JdbcLichSuGiaDAO.
     *
     * @param idNguoiDung id người dùng
     * @return kết quả tìm theo đã thay đổi theo
     */
    @Override
    public List<LichSuGia> timTheoDaThayDoiTheo(Long idNguoiDung) {
        // SQL.
        String sql =
                "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id,"
                        + " thoi_diem_thay_doi, loai_thay_doi, rowversion FROM lich_su_gia WHERE"
                        + " nguoi_thay_doi_id = ? ORDER BY thoi_diem_thay_doi DESC";

        // Các lịch sử.
        List<LichSuGia> cacLichSu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idNguoiDung);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLichSu.add(anhXaLichSuKetQuaDatDenGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price history by user: " + loi.getMessage(), loi);
        }

        return cacLichSu;
    }

    /**
     * Tìm theo kiểu thay đổi trong JdbcLichSuGiaDAO.
     *
     * @param kieuThayDoi kiểu thay đổi
     * @return kết quả tìm theo kiểu thay đổi
     */
    @Override
    public List<LichSuGia> timTheoKieuThayDoi(String kieuThayDoi) {
        // SQL.
        String sql =
                "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id,"
                        + " thoi_diem_thay_doi, loai_thay_doi, rowversion FROM lich_su_gia WHERE"
                        + " loai_thay_doi = ? ORDER BY thoi_diem_thay_doi DESC";

        // Các lịch sử.
        List<LichSuGia> cacLichSu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setString(1, kieuThayDoi);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLichSu.add(anhXaLichSuKetQuaDatDenGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price history by type: " + loi.getMessage(), loi);
        }

        return cacLichSu;
    }

    /**
     * Tìm theo khoảng ngày trong JdbcLichSuGiaDAO.
     *
     * @param ngayBatDau ngày bắt đầu
     * @param ngayKetThuc ngày kết thúc
     * @return kết quả tìm theo khoảng ngày
     */
    @Override
    public List<LichSuGia> timTheoKhoangNgay(LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc) {
        // SQL.
        String sql =
                "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id,"
                        + " thoi_diem_thay_doi, loai_thay_doi, rowversion FROM lich_su_gia WHERE"
                        + " thoi_diem_thay_doi BETWEEN ? AND ? ORDER BY thoi_diem_thay_doi DESC";

        // Các lịch sử.
        List<LichSuGia> cacLichSu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setTimestamp(1, Timestamp.valueOf(ngayBatDau));
            cauLenh.setTimestamp(2, Timestamp.valueOf(ngayKetThuc));
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                while (tapKetQua.next()) {
                    cacLichSu.add(anhXaLichSuKetQuaDatDenGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding price history by date range: " + loi.getMessage(), loi);
        }

        return cacLichSu;
    }

    /**
     * Tìm id gần nhất theo sản phẩm trong JdbcLichSuGiaDAO.
     *
     * @param idSanPham id sản phẩm
     * @return kết quả tìm id gần nhất theo sản phẩm
     */
    @Override
    public Optional<LichSuGia> timIdGanNhatTheoSanPham(Long idSanPham) {
        // SQL.
        String sql =
                "SELECT TOP 1 lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, "
                        + "thoi_diem_thay_doi, loai_thay_doi, rowversion "
                        + "FROM lich_su_gia WHERE san_pham_id = ? ORDER BY thoi_diem_thay_doi DESC";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, idSanPham);
            try (ResultSet tapKetQua = cauLenh.executeQuery()) {
                // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
                if (tapKetQua.next()) {
                    return Optional.of(anhXaLichSuKetQuaDatDenGia(tapKetQua));
                }
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding latest price history: " + loi.getMessage(), loi);
        }

        return Optional.empty();
    }

    /**
     * Tìm tất cả trong JdbcLichSuGiaDAO.
     *
     * @return kết quả tìm tất cả
     */
    @Override
    public List<LichSuGia> timTatCa() {
        // SQL.
        String sql =
                "SELECT lich_su_id, san_pham_id, gia_cu, gia_moi, ly_do, nguoi_thay_doi_id, "
                        + "thoi_diem_thay_doi, loai_thay_doi, rowversion "
                        + "FROM lich_su_gia ORDER BY thoi_diem_thay_doi DESC";

        // Các lịch sử.
        List<LichSuGia> cacLichSu = new ArrayList<>();
        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            while (tapKetQua.next()) {
                cacLichSu.add(anhXaLichSuKetQuaDatDenGia(tapKetQua));
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error finding all price history: " + loi.getMessage(), loi);
        }

        return cacLichSu;
    }

    /**
     * Xóa trong JdbcLichSuGiaDAO.
     *
     * @param id id
     * @return kết quả kiểm tra điều kiện xử lý
     */
    @Override
    public boolean xoa(Long id) {
        // SQL.
        String sql = "DELETE FROM lich_su_gia WHERE lich_su_id = ?";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql)) {
            cauLenh.setLong(1, id);
            return cauLenh.executeUpdate() > 0;
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error deleting price history: " + loi.getMessage(), loi);
        }
    }

    /**
     * Đếm trong JdbcLichSuGiaDAO.
     *
     * @return kết quả đếm
     */
    @Override
    public long dem() {
        // SQL.
        String sql = "SELECT COUNT(*) FROM lich_su_gia";

        try (PreparedStatement cauLenh = ketNoi.prepareStatement(sql);
                ResultSet tapKetQua = cauLenh.executeQuery()) {
            // Chỉ đọc bản ghi khi truy vấn trả về dữ liệu.
            if (tapKetQua.next()) {
                return tapKetQua.getLong(1);
            }
        } catch (SQLException loi) {
            throw new TruyCapDuLieuException(
                    "Error counting price history: " + loi.getMessage(), loi);
        }

        return 0;
    }

    /**
     * Ánh xạ lịch sử kết quả đặt đến giá trong JdbcLichSuGiaDAO.
     *
     * @param tapKetQua tập kết quả
     * @return kết quả ánh xạ lịch sử kết quả đặt đến giá
     * @throws SQLException nếu thao tác không thể hoàn tất
     */
    private LichSuGia anhXaLichSuKetQuaDatDenGia(ResultSet tapKetQua) throws SQLException {
        // Lịch sử.
        LichSuGia lichSu = new LichSuGia();
        lichSu.datId(tapKetQua.getLong("lich_su_id"));
        lichSu.datIdSanPham(tapKetQua.getLong("san_pham_id"));
        lichSu.datGiaCu(tapKetQua.getBigDecimal("gia_cu"));
        lichSu.datGiaMoi(tapKetQua.getBigDecimal("gia_moi"));
        lichSu.datLyDo(tapKetQua.getString("ly_do"));
        lichSu.datDaThayDoiTheo(tapKetQua.getLong("nguoi_thay_doi_id"));

        // Đã thay đổi tại.
        Timestamp daThayDoiTai = tapKetQua.getTimestamp("thoi_diem_thay_doi");
        // Kiểm tra số bản ghi chịu tác động để phát hiện cập nhật thất bại.
        if (daThayDoiTai != null) {
            lichSu.datDaThayDoiTai(daThayDoiTai.toLocalDateTime());
        }

        lichSu.datKieuThayDoi(tapKetQua.getString("loai_thay_doi"));
        lichSu.datPhienBanBanGhi(tapKetQua.getBytes("rowversion"));

        return lichSu;
    }
}
