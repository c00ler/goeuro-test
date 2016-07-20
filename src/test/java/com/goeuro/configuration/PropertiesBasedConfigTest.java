package com.goeuro.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Properties;

import org.junit.Test;

/**
 * @author  Alexey Venderov
 */
public class PropertiesBasedConfigTest {

    @Test
    public void shouldThrowIfApiUrlIsMissing() {
        final PropertiesBasedConfig config = new PropertiesBasedConfig(new Properties());

        assertThatThrownBy(config::getApiUrl).isInstanceOf(NullPointerException.class)
                                             .hasMessageContaining("is missing").hasMessageContaining("api.url");
    }

    @Test
    public void shouldReturnDefaultConnectTimeout() {
        final PropertiesBasedConfig config = new PropertiesBasedConfig(new Properties());

        assertThat(config.getConnectTimeout()).isEqualTo(PropertiesBasedConfig.DEFAULT_TIMEOUT_MILLIS);
    }

}
