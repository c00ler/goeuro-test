package com.goeuro.configuration;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.core.io.ClassPathResource;

import org.springframework.web.client.RestTemplate;

import com.goeuro.handlers.CsvFileLocationsConsumer;
import com.goeuro.handlers.GoEuroApiLocationsProducer;
import com.goeuro.handlers.Handler;

/**
 * @author  Alexey Venderov
 */
@Configuration
@Import(value = {JacksonConfiguration.class, HttpClientConfiguration.class})
public class ApplicationConfiguration {

    @Bean
    public Handler locationsProvider(final Config config, final RestTemplate restTemplate) {
        return new GoEuroApiLocationsProducer(config, restTemplate);
    }

    @Bean
    public Handler locationsConsumer(final Config config) {
        return new CsvFileLocationsConsumer(config);
    }

    @Bean
    public Config config() {

        // This is just a simple implementation that only reads properties from the file in the classpath. It is not
        // really useful because every change to the configuration requires a new jar to be built. Proper
        // implementation should allow to override properties with system properties, environment variables, etc.
        try(final InputStream is = new ClassPathResource("application.properties").getInputStream()) {
            final Properties properties = new Properties();
            properties.load(is);

            return new PropertiesBasedConfig(properties);
        } catch (IOException e) {
            throw new RuntimeException("Can't load application.properties");
        }

    }

}
