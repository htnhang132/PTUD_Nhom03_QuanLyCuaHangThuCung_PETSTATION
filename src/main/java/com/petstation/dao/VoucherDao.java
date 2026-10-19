package com.petstation.dao;

import com.petstation.model.Voucher;

import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Voucher operations */
public interface VoucherDao {

    Voucher create(Voucher voucher);

    Optional<Voucher> findById(Long id);

    Optional<Voucher> findByVoucherCode(String voucherCode);

    List<Voucher> findByCustomerId(Long customerId);

    List<Voucher> findByVoucherType(String voucherType);

    List<Voucher> findByStatus(String status);

    List<Voucher> findActiveVouchers();

    List<Voucher> findExpiredVouchers();

    List<Voucher> findSpaOnlyVouchers();

    List<Voucher> findAll();

    boolean update(Voucher voucher);

    boolean updateStatus(Long voucherId, String status);

    boolean incrementUsedCount(Long voucherId);

    boolean delete(Long id);

    boolean existsByVoucherCode(String voucherCode);

    long count();
}
