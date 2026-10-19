package com.petstation.dao.interfaces;

import com.petstation.model.SystemSettings;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for System Settings operations
 */
public interface SystemSettingsDao {
    
    /**
     * Create a new system setting
     * @param settings the setting to create
     * @return the created setting with generated ID
     */
    SystemSettings create(SystemSettings settings);
    
    /**
     * Find setting by ID
     * @param id the setting ID
     * @return Optional containing the setting if found
     */
    Optional<SystemSettings> findById(Long id);
    
    /**
     * Find setting by key
     * @param key the unique setting key
     * @return Optional containing the setting if found
     */
    Optional<SystemSettings> findByKey(String key);
    
    /**
     * Get setting value by key
     * @param key the setting key
     * @return the setting value, or null if not found
     */
    String getValue(String key);
    
    /**
     * Get setting value by key with default
     * @param key the setting key
     * @param defaultValue the default value if setting not found
     * @return the setting value or default value
     */
    String getValueOrDefault(String key, String defaultValue);
    
    /**
     * Find all settings in a group
     * @param group the setting group
     * @return list of settings in the group
     */
    List<SystemSettings> findByGroup(String group);
    
    /**
     * Get all settings
     * @return list of all settings
     */
    List<SystemSettings> findAll();
    
    /**
     * Update setting information
     * @param settings the setting with updated information
     * @return true if update was successful
     */
    boolean update(SystemSettings settings);
    
    /**
     * Update setting value by key
     * @param key the setting key
     * @param value the new value
     * @param updatedBy the user ID who updated the setting
     * @return true if update was successful
     */
    boolean updateValue(String key, String value, Long updatedBy);
    
    /**
     * Delete setting by ID
     * @param id the setting ID
     * @return true if deletion was successful
     */
    boolean delete(Long id);
    
    /**
     * Delete setting by key
     * @param key the setting key
     * @return true if deletion was successful
     */
    boolean deleteByKey(String key);
    
    /**
     * Check if setting exists by key
     * @param key the setting key
     * @return true if setting exists
     */
    boolean existsByKey(String key);
    
    /**
     * Get setting count
     * @return total number of settings
     */
    long count();
}
