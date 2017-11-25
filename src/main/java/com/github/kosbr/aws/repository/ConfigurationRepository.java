package com.github.kosbr.aws.repository;

import com.github.kosbr.aws.model.UploaderConfiguration;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ConfigurationRepository extends CrudRepository<UploaderConfiguration, String> {

    Optional<UploaderConfiguration> findByActiveTrue();
}
