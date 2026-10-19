package com.petstation.dao.interfaces;

import com.petstation.model.HotelBooking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Hotel Booking operations
 */
public interface HotelBookingDao {
    
    HotelBooking create(HotelBooking booking);
    
    Optional<HotelBooking> findById(Long id);
    
    Optional<HotelBooking> findByBookingCode(String bookingCode);
    
    List<HotelBooking> findByPetId(Long petId);
    
    List<HotelBooking> findByCustomerId(Long customerId);
    
    List<HotelBooking> findByRoomId(Long roomId);
    
    List<HotelBooking> findByStatus(String status);
    
    List<HotelBooking> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<HotelBooking> findActiveBookings();
    
    List<HotelBooking> findOverdueBookings();
    
    List<HotelBooking> findCheckedInBookings();
    
    List<HotelBooking> findAll();
    
    boolean update(HotelBooking booking);
    
    boolean updateStatus(Long bookingId, String status);
    
    boolean updateActualCheckOutTime(Long bookingId, LocalDateTime checkOutTime);
    
    boolean delete(Long id);
    
    boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime);
    
    long count();
}
