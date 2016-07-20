package com.goeuro.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.goeuro.Locations;

import com.goeuro.domain.GeoPosition;

/**
 * @author  Alexey Venderov
 */
public class JacksonConfigurationTest implements Locations {

    private final JacksonConfiguration jacksonConfiguration = new JacksonConfiguration();

    @Test
    public void shouldNotFailOnUnknownProperties() throws IOException {
        final String json = "{\"latitude\":52.52437,\"longitude\":13.41053,\"key\":\"value\"}";

        final GeoPosition geoPosition = jacksonConfiguration.objectMapper().readValue(json, GeoPosition.class);

        assertThat(geoPosition.getLatitude()).isEqualTo(52.52437);
        assertThat(geoPosition.getLongitude()).isEqualTo(13.41053);
    }

    @Test
    public void shouldUseSnakeCaseNaming() throws JsonProcessingException {
        final String json = jacksonConfiguration.objectMapper().writeValueAsString(location());

        assertThat(json).contains("\"geo_position\":");
    }

}
