package com.github.kosbr.aws.commands.configure;

import com.beust.jcommander.internal.Lists;
import com.github.kosbr.aws.HandlersConfiguration;
import com.github.kosbr.aws.commands.config.list.ConfigListHandler;
import com.github.kosbr.aws.commands.config.list.ConfigListOptions;
import com.github.kosbr.aws.model.UploaderConfiguration;
import com.github.kosbr.aws.service.UploaderConfigurationService;
import com.github.kosbr.aws.util.PrintStreamWrapper;
import com.github.kosbr.aws.util.SequenceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HandlersConfiguration.class, MockConfigurationServiceConfiguration.class})
public class ConfigListCommandTest {

    @Autowired
    private ConfigListHandler configListHandler;

    @Autowired
    private UploaderConfigurationService configurationServiceMock;

    @Before
    public void prepare() {
        reset(configurationServiceMock);
    }

    @Test
    public void testListConfigurations() {
        final String name1 = "moscow";
        final String service1 = "service1";
        final String region1 = "region1";

        final String name2 = "london";
        final String service2 = "service2";
        final String region2 = "region2";

        final String name3 = "tokio";
        final String service3 = "service3";
        final String region3 = "region3";

        final UploaderConfiguration configuration1 = new UploaderConfiguration();
        configuration1.setName(name1);
        configuration1.setServiceEndpoint(service1);
        configuration1.setSigningRegion(region1);
        configuration1.setActive(true);

        final UploaderConfiguration configuration2 = new UploaderConfiguration();
        configuration2.setName(name2);
        configuration2.setServiceEndpoint(service2);
        configuration2.setSigningRegion(region2);
        configuration2.setActive(false);

        final UploaderConfiguration configuration3 = new UploaderConfiguration();
        configuration3.setName(name3);
        configuration3.setServiceEndpoint(service3);
        configuration3.setSigningRegion(region3);
        configuration3.setActive(null);


        when(configurationServiceMock.findAll())
                .thenReturn(Lists.newArrayList(configuration1, configuration2, configuration3));

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();


        final ConfigListOptions options = new ConfigListOptions();
        configListHandler.handle(options, printStreamWrapper.getPrintStream());

        final String expectedOutput = SequenceBuilder.createSequence(
                "Configuration name:   moscow",
                        "--- service endpoint: service1",
                        "--- signing region:   region1",
                        "--- active:           true",
                        "----------------------",
                        "Configuration name:   london",
                        "--- service endpoint: service2",
                        "--- signing region:   region2",
                        "--- active:           false",
                        "----------------------",
                        "Configuration name:   tokio",
                        "--- service endpoint: service3",
                        "--- signing region:   region3",
                        "--- active:           false",
                        "----------------------"
        );

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());

    }

    @Test
    public void testListEmpty() {
        when(configurationServiceMock.findAll())
                .thenReturn(Lists.newArrayList());

        final PrintStreamWrapper printStreamWrapper = new PrintStreamWrapper();


        final ConfigListOptions options = new ConfigListOptions();
        configListHandler.handle(options, printStreamWrapper.getPrintStream());

        final String expectedOutput = SequenceBuilder.createSequence(
                "The are no configurations"
        );

        Assert.assertEquals(expectedOutput, printStreamWrapper.getOutContent());

    }
}
