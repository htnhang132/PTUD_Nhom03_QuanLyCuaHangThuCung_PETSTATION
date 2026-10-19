package com.petstation.dao.impl;

import com.petstation.config.DataAccessException;
import com.petstation.dao.interfaces.PetDao;
import com.petstation.model.Pet;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of PetDao
 */
public class JdbcPetDao implements PetDao {
    
    private final Connection connection;
    
    public JdbcPetDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Pet create(Pet pet) {
        String sql = "INSERT INTO thu_cung (khach_hang_id, ma_thu_cung, ten, loai, giong, " +
                    "gioi_tinh, can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, " +
                    "di_ung, da_tiem_phong, ghi_chu_suc_khoe, ngay_tao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, pet.getCustomerId());
            stmt.setString(2, pet.getPetCode());
            stmt.setString(3, pet.getName());
            stmt.setString(4, pet.getSpecies());
            stmt.setString(5, pet.getBreed());
            stmt.setString(6, pet.getGender());
            stmt.setBigDecimal(7, pet.getWeight());
            stmt.setDate(8, pet.getDateOfBirth() != null ? Date.valueOf(pet.getDateOfBirth()) : null);
            stmt.setString(9, pet.getFurColor());
            stmt.setString(10, pet.getTemperament());
            stmt.setString(11, pet.getImageUrl());
            stmt.setString(12, pet.getAllergies());
            stmt.setBoolean(13, pet.getIsNeutered() != null ? pet.getIsNeutered() : false);
            stmt.setString(14, pet.getHealthNotes());
            stmt.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Creating pet failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pet.setId(generatedKeys.getLong(1));
                } else {
                    throw new DataAccessException("Creating pet failed, no ID obtained.");
                }
            }
            
            return pet;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating pet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Pet> findById(Long id) {
        String sql = "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, " +
                    "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, " +
                    "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version " +
                    "FROM thu_cung WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding pet by ID: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Pet> findByPetCode(String petCode) {
        String sql = "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, " +
                    "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, " +
                    "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version " +
                    "FROM thu_cung WHERE ma_thu_cung = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, petCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding pet by code: " + e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Pet> findByCustomerId(Long customerId) {
        String sql = "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, " +
                    "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, " +
                    "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version " +
                    "FROM thu_cung WHERE khach_hang_id = ? ORDER BY ten";
        
        List<Pet> pets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding pets by customer: " + e.getMessage(), e);
        }
        
        return pets;
    }
    
    @Override
    public List<Pet> findBySpecies(String species) {
        String sql = "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, " +
                    "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, " +
                    "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version " +
                    "FROM thu_cung WHERE loai = ? ORDER BY ten";
        
        List<Pet> pets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, species);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding pets by species: " + e.getMessage(), e);
        }
        
        return pets;
    }
    
    @Override
    public List<Pet> searchByName(String name) {
        String sql = "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, " +
                    "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, " +
                    "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version " +
                    "FROM thu_cung WHERE ten LIKE ? ORDER BY ten";
        
        List<Pet> pets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error searching pets by name: " + e.getMessage(), e);
        }
        
        return pets;
    }
    
    @Override
    public List<Pet> findAll() {
        String sql = "SELECT id, khach_hang_id, ma_thu_cung, ten, loai, giong, gioi_tinh, " +
                    "can_nang, ngay_sinh, mau_long, tinh_cach, hinh_anh_url, di_ung, " +
                    "da_tiem_phong, ghi_chu_suc_khoe, ngay_tao, row_version " +
                    "FROM thu_cung ORDER BY ngay_tao DESC";
        
        List<Pet> pets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pets.add(mapResultSetToPet(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all pets: " + e.getMessage(), e);
        }
        
        return pets;
    }
    
    @Override
    public boolean update(Pet pet) {
        String sql = "UPDATE thu_cung SET ten = ?, loai = ?, giong = ?, gioi_tinh = ?, " +
                    "can_nang = ?, ngay_sinh = ?, mau_long = ?, tinh_cach = ?, hinh_anh_url = ?, " +
                    "di_ung = ?, da_tiem_phong = ?, ghi_chu_suc_khoe = ? " +
                    "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pet.getName());
            stmt.setString(2, pet.getSpecies());
            stmt.setString(3, pet.getBreed());
            stmt.setString(4, pet.getGender());
            stmt.setBigDecimal(5, pet.getWeight());
            stmt.setDate(6, pet.getDateOfBirth() != null ? Date.valueOf(pet.getDateOfBirth()) : null);
            stmt.setString(7, pet.getFurColor());
            stmt.setString(8, pet.getTemperament());
            stmt.setString(9, pet.getImageUrl());
            stmt.setString(10, pet.getAllergies());
            stmt.setBoolean(11, pet.getIsNeutered());
            stmt.setString(12, pet.getHealthNotes());
            stmt.setLong(13, pet.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating pet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateWeight(Long petId, java.math.BigDecimal weight) {
        String sql = "UPDATE thu_cung SET can_nang = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, weight);
            stmt.setLong(2, petId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error updating pet weight: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM thu_cung WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting pet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public long countByCustomerId(Long customerId) {
        String sql = "SELECT COUNT(*) FROM thu_cung WHERE khach_hang_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting pets by customer: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM thu_cung";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error counting pets: " + e.getMessage(), e);
        }
        
        return 0;
    }
    
    /**
     * Map ResultSet to Pet object
     */
    private Pet mapResultSetToPet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getLong("id"));
        pet.setCustomerId(rs.getLong("khach_hang_id"));
        pet.setPetCode(rs.getString("ma_thu_cung"));
        pet.setName(rs.getString("ten"));
        pet.setSpecies(rs.getString("loai"));
        pet.setBreed(rs.getString("giong"));
        pet.setGender(rs.getString("gioi_tinh"));
        pet.setWeight(rs.getBigDecimal("can_nang"));
        
        Date dateOfBirth = rs.getDate("ngay_sinh");
        if (dateOfBirth != null) {
            pet.setDateOfBirth(dateOfBirth.toLocalDate());
        }
        
        pet.setFurColor(rs.getString("mau_long"));
        pet.setTemperament(rs.getString("tinh_cach"));
        pet.setImageUrl(rs.getString("hinh_anh_url"));
        pet.setAllergies(rs.getString("di_ung"));
        pet.setIsNeutered(rs.getBoolean("da_tiem_phong"));
        pet.setHealthNotes(rs.getString("ghi_chu_suc_khoe"));
        
        Timestamp createdAt = rs.getTimestamp("ngay_tao");
        if (createdAt != null) {
            pet.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        pet.setRowVersion(rs.getBytes("row_version"));
        
        return pet;
    }
}
