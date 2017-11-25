package com.github.kosbr.aws;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DAOConfiguration.class, HandlersConfiguration.class})
@ComponentScan(basePackages = "com.github.kosbr.aws.service")
public class SpringConfiguration {

}
