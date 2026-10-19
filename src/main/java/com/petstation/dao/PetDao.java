package com.petstation.dao;

import com.petstation.model.Pet;

import java.util.List;
import java.util.Optional;

/** Data Access Object interface for Pet operations */
public interface PetDao {

    /**
     * Create a new pet
     *
     * @param pet the pet to create
     * @return the created pet with generated ID
     */
    Pet create(Pet pet);

    /**
     * Find pet by ID
     *
     * @param id the pet ID
     * @return Optional containing the pet if found
     */
    Optional<Pet> findById(Long id);

    /**
     * Find pet by pet code
     *
     * @param petCode the unique pet code
     * @return Optional containing the pet if found
     */
    Optional<Pet> findByPetCode(String petCode);

    /**
     * Find all pets belonging to a customer
     *
     * @param customerId the customer ID
     * @return list of pets owned by the customer
     */
    List<Pet> findByCustomerId(Long customerId);

    /**
     * Find pets by species
     *
     * @param species the species (DOG, CAT)
     * @return list of pets of the specified species
     */
    List<Pet> findBySpecies(String species);

    /**
     * Search pets by name (partial match)
     *
     * @param name the name to search for
     * @return list of matching pets
     */
    List<Pet> searchByName(String name);

    /**
     * Get all pets
     *
     * @return list of all pets
     */
    List<Pet> findAll();

    /**
     * Update pet information
     *
     * @param pet the pet with updated information
     * @return true if update was successful
     */
    boolean update(Pet pet);

    /**
     * Update pet weight
     *
     * @param petId the pet ID
     * @param weight the new weight
     * @return true if update was successful
     */
    boolean updateWeight(Long petId, java.math.BigDecimal weight);

    /**
     * Delete pet by ID
     *
     * @param id the pet ID
     * @return true if deletion was successful
     */
    boolean delete(Long id);

    /**
     * Get pet count for a customer
     *
     * @param customerId the customer ID
     * @return number of pets owned by the customer
     */
    long countByCustomerId(Long customerId);

    /**
     * Get total pet count
     *
     * @return total number of pets
     */
    long count();
}
