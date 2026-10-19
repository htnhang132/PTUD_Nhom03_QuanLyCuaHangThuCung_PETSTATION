package com.petstation.dao.interfaces;

import com.petstation.model.PriceHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Price History operations
 */
public interface PriceHistoryDao {
    
    PriceHistory create(PriceHistory priceHistory);
    
    Optional<PriceHistory> findById(Long id);
    
    List<PriceHistory> findByProductId(Long productId);
    
    List<PriceHistory> findByChangedBy(Long userId);
    
    List<PriceHistory> findByChangeType(String changeType);
    
    List<PriceHistory> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<PriceHistory> findLatestByProductId(Long productId);
    
    List<PriceHistory> findAll();
    
    boolean delete(Long id);
    
    long count();
}
