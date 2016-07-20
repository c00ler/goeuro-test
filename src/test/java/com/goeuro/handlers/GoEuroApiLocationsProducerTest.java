package com.goeuro.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.test.web.client.MockRestServiceServer;

import org.springframework.web.client.RestTemplate;

import com.goeuro.Locations;
import com.goeuro.LocationsRequest;

import com.goeuro.configuration.Config;
import com.goeuro.configuration.JacksonConfiguration;

/**
 * @author  Alexey Venderov
 */
public class GoEuroApiLocationsProducerTest implements Locations {

    private MockRestServiceServer mockRestServiceServer;

    private GoEuroApiLocationsProducer producer;

    @Before
    public void setUp() {
        final RestTemplate restTemplate = new RestTemplate(Collections.singletonList(
                    new MappingJackson2HttpMessageConverter(new JacksonConfiguration().objectMapper())));

        final Config mockConfig = mock(Config.class);
        when(mockConfig.getApiUrl()).thenReturn("http://localhost/suggest/en");

        producer = new GoEuroApiLocationsProducer(mockConfig, restTemplate);

        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void shouldReturnSameRequestIfLocationsNotFound() {
        mockRestServiceServer.expect(requestTo("http://localhost/suggest/en/berlin")).andExpect(method(HttpMethod.GET))
                             .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        final LocationsRequest locationsRequest = LocationsRequest.builder().city("berlin").build();
        final LocationsRequest result = producer.handle(locationsRequest);

        assertThat(result).isSameAs(locationsRequest);

        mockRestServiceServer.verify();
    }

    @Test
    public void shouldAddLocationsToRequest() {
        mockRestServiceServer.expect(requestTo("http://localhost/suggest/en/new%20york"))
                             .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(
                                     new ClassPathResource("locations_new_york.json"), MediaType.APPLICATION_JSON));

        final LocationsRequest result = producer.handle(LocationsRequest.builder().city("new york").build());

        assertThat(result.getLocations()).extracting("id").containsOnly(316368L, 316369L, 410831L, 410951L);

        mockRestServiceServer.verify();
    }

}
