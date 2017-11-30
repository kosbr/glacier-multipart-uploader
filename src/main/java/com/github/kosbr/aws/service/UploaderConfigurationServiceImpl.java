package com.github.kosbr.aws.service;

import com.github.kosbr.aws.exception.config.ConfigurationExistsException;
import com.github.kosbr.aws.exception.config.ConfigurationNotFoundException;
import com.github.kosbr.aws.exception.config.InvalidConfigurationException;
import com.github.kosbr.aws.exception.config.NoActiveConfiguration;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UploaderConfigurationServiceImpl implements UploaderConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Override
    public Optional<UploaderConfiguration> findByName(final String name) {
        return configurationRepository.findById(name);
    }

    @Override
    public UploaderConfiguration createConfiguration(final UploaderConfiguration configuration)
            throws ConfigurationExistsException, InvalidConfigurationException {
        if (configuration == null) {
            throw new IllegalArgumentException("The configuration is null");
        }
        if (configurationRepository.findById(configuration.getName()).isPresent()) {
            throw new ConfigurationExistsException(
                    "The configuration with name " + configuration.getName() + " already exists"
            );
        }
        validateConfiguration(configuration);
        configurationRepository.save(configuration);
        return configuration;
    }

    @Override
    public void deleteConfiguration(final String name) throws ConfigurationNotFoundException {
        final Optional<UploaderConfiguration> configuration = configurationRepository.findById(name);
        if (!configuration.isPresent()) {
            throwConfigurationNotFoundException(name);
        }
        configurationRepository.delete(configuration.get());
    }

    @Override
    public void deleteAllConfigurations() {
        configurationRepository.deleteAll();
    }

    @Override
    public UploaderConfiguration findActiveConfiguration() throws NoActiveConfiguration {
        final Optional<UploaderConfiguration> maybeActive = configurationRepository.findByActiveTrue();
        if (!maybeActive.isPresent()) {
            throw new NoActiveConfiguration("There is no active configuration");
        }
        return maybeActive.get();
    }

    @Override
    public void makeConfigurationActive(final String name) throws ConfigurationNotFoundException {
        final Optional<UploaderConfiguration> maybeConfig = configurationRepository.findById(name);
        if (!maybeConfig.isPresent()) {
            throwConfigurationNotFoundException(name);
            return;
        }
        configurationRepository.findAll().forEach(config -> {
            config.setActive(false);
            configurationRepository.save(config);
        });
        final UploaderConfiguration configuration = maybeConfig.get();
        configuration.setActive(true);
        configurationRepository.save(configuration);
    }

    @Override
    public List<UploaderConfiguration> findAll() {
        final Iterable<UploaderConfiguration> all = configurationRepository.findAll();
        final List<UploaderConfiguration> result = new ArrayList<>();
        all.forEach(result::add);
        return result;
    }

    private void validateConfiguration(final UploaderConfiguration config) throws InvalidConfigurationException {
        if (StringUtils.isEmpty(config.getServiceEndpoint())) {
            throw new InvalidConfigurationException("Service endpoint is null or empty");
        }
        if (StringUtils.isEmpty(config.getSigningRegion())) {
            throw new InvalidConfigurationException("Signing region is null or empty");
        }
    }

    private void throwConfigurationNotFoundException(final String name) throws ConfigurationNotFoundException {
        throw new ConfigurationNotFoundException("Configuration with name " + name + " is not found");
    }
}
