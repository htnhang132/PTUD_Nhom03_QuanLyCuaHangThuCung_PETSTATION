package com.petstation.dao.impl;

import com.petstation.dao.CustomerDao;
import com.petstation.dao.DataAccessException;
import com.petstation.model.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of CustomerDao */
public class JdbcCustomerDao implements CustomerDao {

    private final Connection connection;

    public JdbcCustomerDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Customer create(Customer customer) {
        String sql =
                "INSERT INTO khach_hang (ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, "
                        + "ngay_sinh, diem_tich_luy, hang_thanh_vien, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getCustomerCode());
            stmt.setString(2, customer.getFullName());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setString(4, customer.getAddress());
            stmt.setDate(
                    5,
                    customer.getDateOfBirth() != null
                            ? Date.valueOf(customer.getDateOfBirth())
                            : null);
            stmt.setInt(6, customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0);
            stmt.setString(
                    7,
                    customer.getMembershipTier() != null
                            ? customer.getMembershipTier()
                            : "REGULAR");
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getLong(1));
                } else {
                    throw new DataAccessException("Creating customer failed, no ID obtained.");
                }
            }

            return customer;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding customer by ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByCustomerCode(String customerCode) {
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ma_khach_hang = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding customer by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE so_dien_thoai = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding customer by phone: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Customer> searchByName(String name) {
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE ho_ten LIKE ? ORDER BY ho_ten";

        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error searching customers by name: " + e.getMessage(), e);
        }

        return customers;
    }

    @Override
    public List<Customer> findAll() {
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang ORDER BY ngay_tao DESC";

        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all customers: " + e.getMessage(), e);
        }

        return customers;
    }

    @Override
    public List<Customer> findByMembershipTier(String tier) {
        String sql =
                "SELECT id, ma_khach_hang, ho_ten, so_dien_thoai, dia_chi, ngay_sinh, "
                        + "diem_tich_luy, hang_thanh_vien, ngay_tao, row_version "
                        + "FROM khach_hang WHERE hang_thanh_vien = ? ORDER BY diem_tich_luy DESC";

        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tier);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding customers by tier: " + e.getMessage(), e);
        }

        return customers;
    }

    @Override
    public boolean update(Customer customer) {
        String sql =
                "UPDATE khach_hang SET ho_ten = ?, so_dien_thoai = ?, dia_chi = ?, "
                        + "ngay_sinh = ?, diem_tich_luy = ?, hang_thanh_vien = ? "
                        + "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getAddress());
            stmt.setDate(
                    4,
                    customer.getDateOfBirth() != null
                            ? Date.valueOf(customer.getDateOfBirth())
                            : null);
            stmt.setInt(5, customer.getLoyaltyPoints());
            stmt.setString(6, customer.getMembershipTier());
            stmt.setLong(7, customer.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating customer: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateLoyaltyPoints(Long customerId, Integer points) {
        String sql = "UPDATE khach_hang SET diem_tich_luy = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setLong(2, customerId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating loyalty points: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateMembershipTier(Long customerId, String tier) {
        String sql = "UPDATE khach_hang SET hang_thanh_vien = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tier);
            stmt.setLong(2, customerId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating membership tier: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM khach_hang WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting customer: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM khach_hang WHERE so_dien_thoai = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error checking customer existence: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM khach_hang";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting customers: " + e.getMessage(), e);
        }

        return 0;
    }

    /** Map ResultSet to Customer object */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setCustomerCode(rs.getString("ma_khach_hang"));
        customer.setFullName(rs.getString("ho_ten"));
        customer.setPhoneNumber(rs.getString("so_dien_thoai"));
        customer.setAddress(rs.getString("dia_chi"));

        Date dateOfBirth = rs.getDate("ngay_sinh");
        if (dateOfBirth != null) {
            customer.setDateOfBirth(dateOfBirth.toLocalDate());
        }

        customer.setLoyaltyPoints(rs.getInt("diem_tich_luy"));
        customer.setMembershipTier(rs.getString("hang_thanh_vien"));

        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            customer.setCreatedAt(createdAt.toLocalDateTime());
        }

        customer.setRowVersion(rs.getBytes("row_version"));

        return customer;
    }
}
