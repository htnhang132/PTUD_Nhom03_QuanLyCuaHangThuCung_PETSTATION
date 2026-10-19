package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.SpaPackageDao;
import com.petstation.model.SpaPackage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of SpaPackageDao */
public class JdbcSpaPackageDao implements SpaPackageDao {

    private final Connection connection;

    public JdbcSpaPackageDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SpaPackage create(SpaPackage spaPackage) {
        String sql =
                "INSERT INTO goi_dich_vu_spa (ma_goi, ten_goi, mo_ta, loai_goi, trang_thai,"
                        + " ngay_tao) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, spaPackage.getPackageCode());
            stmt.setString(2, spaPackage.getPackageName());
            stmt.setString(3, spaPackage.getDescription());
            stmt.setString(4, spaPackage.getPackageType());
            stmt.setString(5, spaPackage.getStatus() != null ? spaPackage.getStatus() : "ACTIVE");
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating spa package failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    spaPackage.setId(generatedKeys.getLong(1));
                } else {
                    throw new DataAccessException("Creating spa package failed, no ID obtained.");
                }
            }

            return spaPackage;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating spa package: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<SpaPackage> findById(Long id) {
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE goi_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpaPackage(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding spa package by ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<SpaPackage> findByPackageCode(String packageCode) {
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE ma_goi = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, packageCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSpaPackage(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding spa package by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<SpaPackage> findByPackageType(String packageType) {
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE loai_goi = ? ORDER BY ten_goi";

        List<SpaPackage> packages = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, packageType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    packages.add(mapResultSetToSpaPackage(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding spa packages by type: " + e.getMessage(), e);
        }

        return packages;
    }

    @Override
    public List<SpaPackage> findAllActive() {
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa WHERE trang_thai = 'ACTIVE' ORDER BY ten_goi";

        List<SpaPackage> packages = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                packages.add(mapResultSetToSpaPackage(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding active spa packages: " + e.getMessage(), e);
        }

        return packages;
    }

    @Override
    public List<SpaPackage> findAll() {
        String sql =
                "SELECT goi_id, ma_goi, ten_goi, mo_ta, loai_goi, trang_thai, ngay_tao, rowversion "
                        + "FROM goi_dich_vu_spa ORDER BY ngay_tao DESC";

        List<SpaPackage> packages = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                packages.add(mapResultSetToSpaPackage(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all spa packages: " + e.getMessage(), e);
        }

        return packages;
    }

    @Override
    public boolean update(SpaPackage spaPackage) {
        String sql =
                "UPDATE goi_dich_vu_spa SET ten_goi = ?, mo_ta = ?, loai_goi = ?, trang_thai = ? "
                        + "WHERE goi_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, spaPackage.getPackageName());
            stmt.setString(2, spaPackage.getDescription());
            stmt.setString(3, spaPackage.getPackageType());
            stmt.setString(4, spaPackage.getStatus());
            stmt.setLong(5, spaPackage.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating spa package: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Long packageId, String status) {
        String sql = "UPDATE goi_dich_vu_spa SET trang_thai = ? WHERE goi_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, packageId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error updating spa package status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM goi_dich_vu_spa WHERE goi_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting spa package: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByPackageCode(String packageCode) {
        String sql = "SELECT COUNT(*) FROM goi_dich_vu_spa WHERE ma_goi = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, packageCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error checking spa package existence: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM goi_dich_vu_spa";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting spa packages: " + e.getMessage(), e);
        }

        return 0;
    }

    /** Map ResultSet to SpaPackage object */
    private SpaPackage mapResultSetToSpaPackage(ResultSet rs) throws SQLException {
        SpaPackage spaPackage = new SpaPackage();
        spaPackage.setId(rs.getLong("goi_id"));
        spaPackage.setPackageCode(rs.getString("ma_goi"));
        spaPackage.setPackageName(rs.getString("ten_goi"));
        spaPackage.setDescription(rs.getString("mo_ta"));
        spaPackage.setPackageType(rs.getString("loai_goi"));
        spaPackage.setStatus(rs.getString("trang_thai"));

        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            spaPackage.setCreatedAt(createdAt.toLocalDateTime());
        }

        spaPackage.setRowVersion(rs.getBytes("rowversion"));

        return spaPackage;
    }
}
