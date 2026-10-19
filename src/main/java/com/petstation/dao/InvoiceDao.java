package com.petstation.dao;

import com.petstation.model.Invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Invoice operations */
public interface InvoiceDao {

    Invoice create(Invoice invoice);

    Optional<Invoice> findById(Long id);

    Optional<Invoice> findByInvoiceCode(String invoiceCode);

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByEmployeeId(Long employeeId);

    List<Invoice> findByShiftSessionId(Long shiftSessionId);

    List<Invoice> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Invoice> findByStatus(String status);

    List<Invoice> findByInvoiceType(String invoiceType);

    List<Invoice> findByPaymentMethod(String paymentMethod);

    List<Invoice> findAll();

    boolean update(Invoice invoice);

    boolean updateStatus(Long invoiceId, String status);

    boolean delete(Long id);

    BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalRevenueByShiftSession(Long shiftSessionId);

    long countByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    long count();
}
