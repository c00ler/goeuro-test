package com.goeuro.configuration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author  Alexey Venderov
 */
@Configuration
public class HttpClientConfiguration {

    @Bean
    public RestTemplate restTemplate(final ClientHttpRequestFactory requestFactory, final ObjectMapper mapper) {
        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter(mapper)));

        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(final Config config) {

        // Default client is sufficient for the use case of command line utility
        final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                closeableHttpClient);

        factory.setConnectTimeout((int) config.getConnectTimeout());
        factory.setReadTimeout((int) config.getReadTimeout()); // Socket timeout

        // This setting is irrelevant when client is not shared and there is only one thread trying to get the
        // connection
        factory.setConnectionRequestTimeout((int) TimeUnit.SECONDS.toMillis(1L));

        return factory;
    }

}
