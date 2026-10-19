package com.petstation.dao.impl;

import com.petstation.dao.DataAccessException;
import com.petstation.dao.ProductDao;
import com.petstation.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of ProductDao */
public class JdbcProductDao implements ProductDao {

    private final Connection connection;

    public JdbcProductDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Product create(Product product) {
        String sql =
                "INSERT INTO san_pham (ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, "
                        + "gia_ban, hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getProductName());
            stmt.setLong(3, product.getCategoryId());
            stmt.setString(4, product.getUnit());
            stmt.setBigDecimal(5, product.getSellingPrice());
            stmt.setString(6, product.getImageUrl());
            stmt.setString(7, product.getDescription());
            stmt.setBoolean(
                    8,
                    product.getRequiresPrescription() != null
                            ? product.getRequiresPrescription()
                            : false);
            stmt.setBoolean(9, product.getIsActive() != null ? product.getIsActive() : true);
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }

            return product;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating product: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding product by ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Product> findByProductCode(String productCode) {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE ma_san_pham = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding product by code: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE danh_muc_id = ? ORDER BY ten_san_pham";

        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding products by category: " + e.getMessage(), e);
        }

        return products;
    }

    @Override
    public List<Product> searchByName(String name) {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE ten_san_pham LIKE ? ORDER BY ten_san_pham";

        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error searching products: " + e.getMessage(), e);
        }

        return products;
    }

    @Override
    public List<Product> findAllActive() {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE hoat_dong = 1 ORDER BY ten_san_pham";

        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active products: " + e.getMessage(), e);
        }

        return products;
    }

    @Override
    public List<Product> findRequiringPrescription() {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham WHERE can_don_thuoc = 1 ORDER BY ten_san_pham";

        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error finding prescription products: " + e.getMessage(), e);
        }

        return products;
    }

    @Override
    public List<Product> findAll() {
        String sql =
                "SELECT id, ma_san_pham, ten_san_pham, danh_muc_id, don_vi_tinh, gia_ban,"
                    + " hinh_anh_url, mo_ta, can_don_thuoc, hoat_dong, ngay_tao, row_version FROM"
                    + " san_pham ORDER BY ngay_tao DESC";

        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all products: " + e.getMessage(), e);
        }

        return products;
    }

    @Override
    public boolean update(Product product) {
        String sql =
                "UPDATE san_pham SET ten_san_pham = ?, danh_muc_id = ?, don_vi_tinh = ?, gia_ban ="
                    + " ?, hinh_anh_url = ?, mo_ta = ?, can_don_thuoc = ?, hoat_dong = ? WHERE id ="
                    + " ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setLong(2, product.getCategoryId());
            stmt.setString(3, product.getUnit());
            stmt.setBigDecimal(4, product.getSellingPrice());
            stmt.setString(5, product.getImageUrl());
            stmt.setString(6, product.getDescription());
            stmt.setBoolean(7, product.getRequiresPrescription());
            stmt.setBoolean(8, product.getIsActive());
            stmt.setLong(9, product.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateSellingPrice(Long productId, BigDecimal newPrice) {
        String sql = "UPDATE san_pham SET gia_ban = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newPrice);
            stmt.setLong(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating product price: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateActiveStatus(Long productId, Boolean isActive) {
        String sql = "UPDATE san_pham SET hoat_dong = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setLong(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating product status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM san_pham WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting product: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByProductCode(String productCode) {
        String sql = "SELECT COUNT(*) FROM san_pham WHERE ma_san_pham = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error checking product existence: " + e.getMessage(), e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM san_pham";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting products: " + e.getMessage(), e);
        }

        return 0;
    }

    /** Map ResultSet to Product object */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setProductCode(rs.getString("ma_san_pham"));
        product.setProductName(rs.getString("ten_san_pham"));
        product.setCategoryId(rs.getLong("danh_muc_id"));
        product.setUnit(rs.getString("don_vi_tinh"));
        product.setSellingPrice(rs.getBigDecimal("gia_ban"));
        product.setImageUrl(rs.getString("hinh_anh_url"));
        product.setDescription(rs.getString("mo_ta"));
        product.setRequiresPrescription(rs.getBoolean("can_don_thuoc"));
        product.setIsActive(rs.getBoolean("hoat_dong"));

        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        product.setRowVersion(rs.getBytes("row_version"));

        return product;
    }
}
