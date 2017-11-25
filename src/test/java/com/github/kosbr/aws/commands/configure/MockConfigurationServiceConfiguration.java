package com.github.kosbr.aws.commands.configure;

import com.github.kosbr.aws.service.UploaderConfigurationService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockConfigurationServiceConfiguration {

    @Bean
    public UploaderConfigurationService uploaderConfigurationService() {
        return Mockito.mock(UploaderConfigurationService.class);
    }

}
