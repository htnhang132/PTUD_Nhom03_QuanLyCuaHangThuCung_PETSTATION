package com.petstation.dao;

import com.petstation.model.Customer;

import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Customer operations */
public interface CustomerDao {

    /**
     * Create a new customer
     *
     * @param customer the customer to create
     * @return the created customer with generated ID
     */
    Customer create(Customer customer);

    /**
     * Find customer by ID
     *
     * @param id the customer ID
     * @return Optional containing the customer if found
     */
    Optional<Customer> findById(Long id);

    /**
     * Find customer by customer code
     *
     * @param customerCode the unique customer code
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * Find customer by phone number
     *
     * @param phoneNumber the phone number
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Search customers by name (partial match)
     *
     * @param name the name to search for
     * @return list of matching customers
     */
    List<Customer> searchByName(String name);

    /**
     * Get all customers
     *
     * @return list of all customers
     */
    List<Customer> findAll();

    /**
     * Get customers by membership tier
     *
     * @param tier the membership tier (REGULAR, SILVER, GOLD, PLATINUM)
     * @return list of customers with the specified tier
     */
    List<Customer> findByMembershipTier(String tier);

    /**
     * Update customer information
     *
     * @param customer the customer with updated information
     * @return true if update was successful
     */
    boolean update(Customer customer);

    /**
     * Update customer loyalty points
     *
     * @param customerId the customer ID
     * @param points the new points value
     * @return true if update was successful
     */
    boolean updateLoyaltyPoints(Long customerId, Integer points);

    /**
     * Update customer membership tier
     *
     * @param customerId the customer ID
     * @param tier the new membership tier
     * @return true if update was successful
     */
    boolean updateMembershipTier(Long customerId, String tier);

    /**
     * Delete customer by ID
     *
     * @param id the customer ID
     * @return true if deletion was successful
     */
    boolean delete(Long id);

    /**
     * Check if customer exists by phone number
     *
     * @param phoneNumber the phone number
     * @return true if customer exists
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Get customer count
     *
     * @return total number of customers
     */
    long count();
}
