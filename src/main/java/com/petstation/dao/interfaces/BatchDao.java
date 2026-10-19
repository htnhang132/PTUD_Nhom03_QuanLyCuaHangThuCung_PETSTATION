package com.petstation.dao.interfaces;

import com.petstation.model.Batch;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Batch operations
 */
public interface BatchDao {
    
    Batch create(Batch batch);
    
    Optional<Batch> findById(Long id);
    
    Optional<Batch> findByBatchCode(String batchCode);
    
    List<Batch> findByProductId(Long productId);
    
    List<Batch> findBySupplierId(Long supplierId);
    
    List<Batch> findByStatus(String status);
    
    List<Batch> findExpiringBefore(LocalDate date);
    
    List<Batch> findExpiredBatches();
    
    List<Batch> findLowStockBatches(Integer threshold);
    
    List<Batch> findAll();
    
    boolean update(Batch batch);
    
    boolean updateQuantity(Long batchId, Integer quantity);
    
    boolean updateStatus(Long batchId, String status);
    
    boolean delete(Long id);
    
    long count();
}
