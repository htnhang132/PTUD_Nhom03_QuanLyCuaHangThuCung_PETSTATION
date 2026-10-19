package com.petstation.dao;

import com.petstation.model.SpaPriceMatrix;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Spa Price Matrix operations */
public interface SpaPriceMatrixDao {

    /**
     * Create a new spa price matrix entry
     *
     * @param priceMatrix the price matrix to create
     * @return the created price matrix with generated ID
     */
    SpaPriceMatrix create(SpaPriceMatrix priceMatrix);

    /**
     * Find spa price matrix by ID
     *
     * @param id the price matrix ID
     * @return Optional containing the price matrix if found
     */
    Optional<SpaPriceMatrix> findById(Long id);

    /**
     * Find all price matrices for a specific package
     *
     * @param packageId the spa package ID
     * @return list of price matrices for the package
     */
    List<SpaPriceMatrix> findByPackageId(Long packageId);

    /**
     * Find price matrix by package, species, and weight
     *
     * @param packageId the spa package ID
     * @param species the species (DOG, CAT)
     * @param weight the pet weight
     * @return Optional containing the applicable price matrix
     */
    Optional<SpaPriceMatrix> findByPackageSpeciesAndWeight(
            Long packageId, String species, BigDecimal weight);

    /**
     * Find all active price matrices (not expired)
     *
     * @return list of active price matrices
     */
    List<SpaPriceMatrix> findAllActive();

    /**
     * Find price matrices by species
     *
     * @param species the species (DOG, CAT)
     * @return list of price matrices for the species
     */
    List<SpaPriceMatrix> findBySpecies(String species);

    /**
     * Get all price matrices
     *
     * @return list of all price matrices
     */
    List<SpaPriceMatrix> findAll();

    /**
     * Update price matrix information
     *
     * @param priceMatrix the price matrix with updated information
     * @return true if update was successful
     */
    boolean update(SpaPriceMatrix priceMatrix);

    /**
     * Update price for a specific matrix
     *
     * @param priceMatrixId the price matrix ID
     * @param newPrice the new price
     * @return true if update was successful
     */
    boolean updatePrice(Long priceMatrixId, BigDecimal newPrice);

    /**
     * Delete price matrix by ID
     *
     * @param id the price matrix ID
     * @return true if deletion was successful
     */
    boolean delete(Long id);

    /**
     * Delete all price matrices for a package
     *
     * @param packageId the spa package ID
     * @return number of deleted records
     */
    int deleteByPackageId(Long packageId);

    /**
     * Get price matrix count
     *
     * @return total number of price matrices
     */
    long count();
}
