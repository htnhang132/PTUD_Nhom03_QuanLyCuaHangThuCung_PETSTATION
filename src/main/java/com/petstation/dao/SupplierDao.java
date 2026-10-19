package com.petstation.dao;

import com.petstation.model.Supplier;

import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Supplier operations */
public interface SupplierDao {

    Supplier create(Supplier supplier);

    Optional<Supplier> findById(Long id);

    Optional<Supplier> findBySupplierCode(String supplierCode);

    Optional<Supplier> findByPhoneNumber(String phoneNumber);

    List<Supplier> searchByName(String name);

    List<Supplier> findAllActive();

    List<Supplier> findAll();

    boolean update(Supplier supplier);

    boolean updateActiveStatus(Long supplierId, Boolean isActive);

    boolean delete(Long id);

    boolean existsBySupplierCode(String supplierCode);

    long count();
}
