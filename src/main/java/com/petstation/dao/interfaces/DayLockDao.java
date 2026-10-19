package com.petstation.dao.interfaces;

import com.petstation.model.DayLock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Day Lock operations
 */
public interface DayLockDao {
    
    DayLock create(DayLock dayLock);
    
    Optional<DayLock> findById(Long id);
    
    Optional<DayLock> findByLockDate(LocalDate lockDate);
    
    List<DayLock> findByLockedBy(Long userId);
    
    List<DayLock> findByStatus(String status);
    
    List<DayLock> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    List<DayLock> findLockedDays();
    
    List<DayLock> findAll();
    
    boolean update(DayLock dayLock);
    
    boolean updateStatus(Long dayLockId, String status);
    
    boolean delete(Long id);
    
    boolean isDayLocked(LocalDate date);
    
    long count();
}
