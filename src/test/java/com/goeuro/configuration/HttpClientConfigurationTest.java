package com.goeuro.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.client.config.RequestConfig;

import org.junit.Test;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author  Alexey Venderov
 */
public class HttpClientConfigurationTest {

    private final HttpClientConfiguration httpClientConfiguration = new HttpClientConfiguration();

    @Test
    public void shouldUseTimeoutsFromConfig() {
        final int connectTimeout = 10;
        final int readTimeout = 15;

        final Config mockConfig = mock(Config.class);
        when(mockConfig.getConnectTimeout()).thenReturn((long) connectTimeout);
        when(mockConfig.getReadTimeout()).thenReturn((long) readTimeout);

        final ClientHttpRequestFactory requestFactory = httpClientConfiguration.clientHttpRequestFactory(mockConfig);

        assertThat(requestFactory).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);

        final RequestConfig requestConfig = (RequestConfig) ReflectionTestUtils.getField(requestFactory,
                "requestConfig");

        assertThat(requestConfig.getConnectTimeout()).isEqualTo(connectTimeout);
        assertThat(requestConfig.getSocketTimeout()).isEqualTo(readTimeout);

        // This value is hardcoded in the configuration
        assertThat(requestConfig.getConnectionRequestTimeout()).isEqualTo(1000);
    }

    @Test
    public void shouldUseJacksonMessageConverter() {
        final ClientHttpRequestFactory mockClientHttpRequestFactory = mock(ClientHttpRequestFactory.class);

        final RestTemplate restTemplate = httpClientConfiguration.restTemplate(mockClientHttpRequestFactory,
                new ObjectMapper());

        assertThat(restTemplate.getMessageConverters()).hasSize(1).first().isInstanceOf(
            MappingJackson2HttpMessageConverter.class);
    }

}
