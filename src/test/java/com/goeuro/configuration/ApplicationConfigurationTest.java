package com.goeuro.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author  Alexey Venderov
 */
public class ApplicationConfigurationTest {

    private final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();

    @Test
    public void shouldCreateConfigFromPropertiesInClasspath() {
        final Config config = applicationConfiguration.config();

        assertThat(config).isInstanceOf(PropertiesBasedConfig.class);
        assertThat(config.getApiUrl()).isEqualTo("http://localhost/suggest/en");
    }

}
