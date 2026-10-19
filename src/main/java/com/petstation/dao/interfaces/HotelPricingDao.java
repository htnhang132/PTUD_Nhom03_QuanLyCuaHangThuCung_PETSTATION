package com.petstation.dao.interfaces;

import com.petstation.model.HotelPricing;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Hotel Pricing operations
 */
public interface HotelPricingDao {
    
    HotelPricing create(HotelPricing pricing);
    
    Optional<HotelPricing> findById(Long id);
    
    List<HotelPricing> findByRoomId(Long roomId);
    
    List<HotelPricing> findByTimeFrame(String timeFrame);
    
    Optional<HotelPricing> findByRoomTimeFrameAndWeight(Long roomId, String timeFrame, BigDecimal weight);
    
    List<HotelPricing> findAllActive();
    
    List<HotelPricing> findAll();
    
    boolean update(HotelPricing pricing);
    
    boolean updatePrice(Long pricingId, BigDecimal newPrice);
    
    boolean delete(Long id);
    
    int deleteByRoomId(Long roomId);
    
    long count();
}
