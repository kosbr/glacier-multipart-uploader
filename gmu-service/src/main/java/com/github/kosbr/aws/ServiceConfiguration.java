package com.github.kosbr.aws;

import com.github.kosbr.aws.config.DAOConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DAOConfiguration.class)
@ComponentScan(basePackages = "com.github.kosbr.aws.service.impl")
public class ServiceConfiguration {
}
