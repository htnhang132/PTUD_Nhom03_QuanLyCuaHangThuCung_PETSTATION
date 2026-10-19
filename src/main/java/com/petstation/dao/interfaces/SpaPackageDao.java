package com.petstation.dao.interfaces;

import com.petstation.model.SpaPackage;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Spa Package operations
 */
public interface SpaPackageDao {
    
    /**
     * Create a new spa package
     * @param spaPackage the spa package to create
     * @return the created spa package with generated ID
     */
    SpaPackage create(SpaPackage spaPackage);
    
    /**
     * Find spa package by ID
     * @param id the spa package ID
     * @return Optional containing the spa package if found
     */
    Optional<SpaPackage> findById(Long id);
    
    /**
     * Find spa package by package code
     * @param packageCode the unique package code
     * @return Optional containing the spa package if found
     */
    Optional<SpaPackage> findByPackageCode(String packageCode);
    
    /**
     * Find spa packages by type
     * @param packageType the package type (COMBO, SINGLE)
     * @return list of spa packages of the specified type
     */
    List<SpaPackage> findByPackageType(String packageType);
    
    /**
     * Find active spa packages only
     * @return list of active spa packages
     */
    List<SpaPackage> findAllActive();
    
    /**
     * Get all spa packages
     * @return list of all spa packages
     */
    List<SpaPackage> findAll();
    
    /**
     * Update spa package information
     * @param spaPackage the spa package with updated information
     * @return true if update was successful
     */
    boolean update(SpaPackage spaPackage);
    
    /**
     * Update spa package status
     * @param packageId the package ID
     * @param status the new status (ACTIVE, INACTIVE)
     * @return true if update was successful
     */
    boolean updateStatus(Long packageId, String status);
    
    /**
     * Delete spa package by ID
     * @param id the spa package ID
     * @return true if deletion was successful
     */
    boolean delete(Long id);
    
    /**
     * Check if package code exists
     * @param packageCode the package code
     * @return true if package exists
     */
    boolean existsByPackageCode(String packageCode);
    
    /**
     * Get spa package count
     * @return total number of spa packages
     */
    long count();
}
