package com.petstation.dao;

import com.petstation.model.SpaTicket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Spa Ticket operations */
public interface SpaTicketDao {

    SpaTicket create(SpaTicket spaTicket);

    Optional<SpaTicket> findById(Long id);

    Optional<SpaTicket> findByTicketCode(String ticketCode);

    List<SpaTicket> findByPetId(Long petId);

    List<SpaTicket> findByCustomerId(Long customerId);

    List<SpaTicket> findByPackageId(Long packageId);

    List<SpaTicket> findByEmployeeId(Long employeeId);

    List<SpaTicket> findByStatus(String status);

    List<SpaTicket> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<SpaTicket> findPendingTickets();

    List<SpaTicket> findInProgressTickets();

    List<SpaTicket> findAll();

    boolean update(SpaTicket spaTicket);

    boolean updateStatus(Long ticketId, String status);

    boolean updateCheckOutTime(Long ticketId, LocalDateTime checkOutTime);

    boolean delete(Long id);

    long count();
}
