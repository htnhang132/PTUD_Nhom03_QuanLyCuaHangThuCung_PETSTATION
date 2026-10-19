package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.HotelPricingDao;
import com.petstation.model.HotelPricing;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of HotelPricingDao */
public class JdbcHotelPricingDao implements HotelPricingDao {

    private final Connection connection;

    public JdbcHotelPricingDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public HotelPricing create(HotelPricing pricing) {
        String sql =
                "INSERT INTO bang_gia_hotel (phong_id, khung_gio, can_nang_tu, can_nang_den, "
                        + "don_gia, ngay_ap_dung, ngay_het_hieu_luc) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pricing.getRoomId());
            stmt.setString(2, pricing.getTimeFrame());
            stmt.setBigDecimal(3, pricing.getWeightFrom());
            stmt.setBigDecimal(4, pricing.getWeightTo());
            stmt.setBigDecimal(5, pricing.getPrice());
            stmt.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            pricing.getEffectiveDate() != null
                                    ? pricing.getEffectiveDate()
                                    : LocalDateTime.now()));
            stmt.setTimestamp(
                    7,
                    pricing.getExpiryDate() != null
                            ? Timestamp.valueOf(pricing.getExpiryDate())
                            : null);

            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pricing.setId(generatedKeys.getLong(1));
                }
            }

            return pricing;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating hotel pricing: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<HotelPricing> findById(Long id) {
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia,"
                        + " ngay_ap_dung, ngay_het_hieu_luc, rowversion FROM bang_gia_hotel WHERE"
                        + " gia_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHotelPricing(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding hotel pricing: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<HotelPricing> findByRoomId(Long roomId) {
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel WHERE phong_id = ? ORDER BY khung_gio, can_nang_tu";

        List<HotelPricing> pricingList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pricingList.add(mapResultSetToHotelPricing(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding pricing by room: " + e.getMessage(), e);
        }

        return pricingList;
    }

    @Override
    public List<HotelPricing> findByTimeFrame(String timeFrame) {
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel WHERE khung_gio = ? ORDER BY phong_id, can_nang_tu";

        List<HotelPricing> pricingList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, timeFrame);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pricingList.add(mapResultSetToHotelPricing(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding pricing by time frame: " + e.getMessage(), e);
        }

        return pricingList;
    }

    @Override
    public Optional<HotelPricing> findByRoomTimeFrameAndWeight(
            Long roomId, String timeFrame, BigDecimal weight) {
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel WHERE phong_id = ? AND khung_gio = ? "
                        + "AND can_nang_tu <= ? AND can_nang_den > ? "
                        + "AND (ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE())";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            stmt.setString(2, timeFrame);
            stmt.setBigDecimal(3, weight);
            stmt.setBigDecimal(4, weight);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHotelPricing(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding pricing by criteria: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<HotelPricing> findAllActive() {
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia,"
                        + " ngay_ap_dung, ngay_het_hieu_luc, rowversion FROM bang_gia_hotel WHERE"
                        + " ngay_het_hieu_luc IS NULL OR ngay_het_hieu_luc > GETDATE() ORDER BY"
                        + " phong_id, khung_gio, can_nang_tu";

        List<HotelPricing> pricingList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pricingList.add(mapResultSetToHotelPricing(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active pricing: " + e.getMessage(), e);
        }

        return pricingList;
    }

    @Override
    public List<HotelPricing> findAll() {
        String sql =
                "SELECT gia_id, phong_id, khung_gio, can_nang_tu, can_nang_den, don_gia, "
                        + "ngay_ap_dung, ngay_het_hieu_luc, rowversion "
                        + "FROM bang_gia_hotel ORDER BY phong_id, khung_gio, can_nang_tu";

        List<HotelPricing> pricingList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pricingList.add(mapResultSetToHotelPricing(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all pricing: " + e.getMessage(), e);
        }

        return pricingList;
    }

    @Override
    public boolean update(HotelPricing pricing) {
        String sql =
                "UPDATE bang_gia_hotel SET don_gia = ?, ngay_het_hieu_luc = ? WHERE gia_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, pricing.getPrice());
            stmt.setTimestamp(
                    2,
                    pricing.getExpiryDate() != null
                            ? Timestamp.valueOf(pricing.getExpiryDate())
                            : null);
            stmt.setLong(3, pricing.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating hotel pricing: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePrice(Long pricingId, BigDecimal newPrice) {
        String sql = "UPDATE bang_gia_hotel SET don_gia = ? WHERE gia_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newPrice);
            stmt.setLong(2, pricingId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating price: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM bang_gia_hotel WHERE gia_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting hotel pricing: " + e.getMessage(), e);
        }
    }

    @Override
    public int deleteByRoomId(Long roomId) {
        String sql = "DELETE FROM bang_gia_hotel WHERE phong_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting pricing by room: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM bang_gia_hotel";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting hotel pricing: " + e.getMessage(), e);
        }

        return 0;
    }

    private HotelPricing mapResultSetToHotelPricing(ResultSet rs) throws SQLException {
        HotelPricing pricing = new HotelPricing();
        pricing.setId(rs.getLong("gia_id"));
        pricing.setRoomId(rs.getLong("phong_id"));
        pricing.setTimeFrame(rs.getString("khung_gio"));
        pricing.setWeightFrom(rs.getBigDecimal("can_nang_tu"));
        pricing.setWeightTo(rs.getBigDecimal("can_nang_den"));
        pricing.setPrice(rs.getBigDecimal("don_gia"));

        Timestamp effectiveDate = rs.getTimestamp("ngay_ap_dung");
        if (effectiveDate != null) {
            pricing.setEffectiveDate(effectiveDate.toLocalDateTime());
        }

        Timestamp expiryDate = rs.getTimestamp("ngay_het_hieu_luc");
        if (expiryDate != null) {
            pricing.setExpiryDate(expiryDate.toLocalDateTime());
        }

        pricing.setRowVersion(rs.getBytes("rowversion"));

        return pricing;
    }
}
