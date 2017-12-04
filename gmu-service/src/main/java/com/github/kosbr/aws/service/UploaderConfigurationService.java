package com.github.kosbr.aws.service;

import com.github.kosbr.aws.exception.config.ConfigurationExistsException;
import com.github.kosbr.aws.exception.config.ConfigurationNotFoundException;
import com.github.kosbr.aws.exception.config.InvalidConfigurationException;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.model.UploaderConfiguration;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing with configuration.
 */
public interface UploaderConfigurationService {

    /**
     * Find configuration by name.
     * @param name
     * @return
     */
    Optional<UploaderConfiguration> findByName(String name);

    /**
     * Creates configuration.
     * @param configuration
     * @return Created configuration
     * @throws InvalidConfigurationException If configuration is invalid
     * @throws ConfigurationExistsException If configuration name is used
     */
    UploaderConfiguration createConfiguration(UploaderConfiguration configuration)
            throws InvalidConfigurationException, ConfigurationExistsException;

    /**
     * Delete configuration by name.
     * The corresponding uploads should not exist
     * @param name
     * @throws ConfigurationNotFoundException If there is no configuration with such name.
     */
    void deleteConfiguration(String name) throws ConfigurationNotFoundException;

    /**
     * Delete all configurations.
     * All uploads should be deleted.
     */
    void deleteAllConfigurations();

    /**
     * Returns active configuration.
     * @return Active configuration.
     * @throws NoActiveConfiguration If there is no active configuration.
     */
    UploaderConfiguration findActiveConfiguration() throws NoActiveConfiguration;

    /**
     * Makes configuration with this name active.
     * @param name
     * @throws ConfigurationNotFoundException If there is no configuration with this name.
     */
    void makeConfigurationActive(String name) throws ConfigurationNotFoundException;

    /**
     * Get all configurations.
     * @return
     */
    List<UploaderConfiguration> findAll();
}
