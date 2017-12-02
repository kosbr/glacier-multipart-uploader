package com.github.kosbr.aws.config;

import com.github.kosbr.aws.ServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ServiceConfiguration.class, HandlersConfiguration.class})
public class CliConfiguration {

}
