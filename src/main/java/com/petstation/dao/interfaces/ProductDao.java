package com.petstation.dao.interfaces;

import com.petstation.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Product operations
 */
public interface ProductDao {
    
    /**
     * Create a new product
     * @param product the product to create
     * @return the created product with generated ID
     */
    Product create(Product product);
    
    /**
     * Find product by ID
     * @param id the product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findById(Long id);
    
    /**
     * Find product by product code
     * @param productCode the unique product code
     * @return Optional containing the product if found
     */
    Optional<Product> findByProductCode(String productCode);
    
    /**
     * Find products by category
     * @param categoryId the category ID
     * @return list of products in the specified category
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * Search products by name (partial match)
     * @param name the name to search for
     * @return list of matching products
     */
    List<Product> searchByName(String name);
    
    /**
     * Find active products only
     * @return list of active products
     */
    List<Product> findAllActive();
    
    /**
     * Find products that require prescription
     * @return list of products requiring prescription
     */
    List<Product> findRequiringPrescription();
    
    /**
     * Get all products
     * @return list of all products
     */
    List<Product> findAll();
    
    /**
     * Update product information
     * @param product the product with updated information
     * @return true if update was successful
     */
    boolean update(Product product);
    
    /**
     * Update product selling price
     * @param productId the product ID
     * @param newPrice the new selling price
     * @return true if update was successful
     */
    boolean updateSellingPrice(Long productId, BigDecimal newPrice);
    
    /**
     * Update product active status
     * @param productId the product ID
     * @param isActive the active status
     * @return true if update was successful
     */
    boolean updateActiveStatus(Long productId, Boolean isActive);
    
    /**
     * Delete product by ID
     * @param id the product ID
     * @return true if deletion was successful
     */
    boolean delete(Long id);
    
    /**
     * Check if product code exists
     * @param productCode the product code
     * @return true if product exists
     */
    boolean existsByProductCode(String productCode);
    
    /**
     * Get product count
     * @return total number of products
     */
    long count();
}
