package com.github.kosbr.aws.service;

import com.github.kosbr.aws.exception.config.*;
import com.github.kosbr.aws.model.UploaderConfiguration;

import java.util.List;
import java.util.Optional;

public interface UploaderConfigurationService {

    Optional<UploaderConfiguration> findByName(String name);

    UploaderConfiguration createConfiguration(UploaderConfiguration configuration)
            throws InvalidConfigurationException, ConfigurationExistsException;

    void deleteConfiguration(String name) throws ConfigurationNotFoundException;

    void deleteAllConfigurations();

    UploaderConfiguration findActiveConfiguration() throws NoActiveConfiguration;

    void makeConfigurationActive(String name) throws ConfigurationNotFoundException;

    List<UploaderConfiguration> findAll();
}
