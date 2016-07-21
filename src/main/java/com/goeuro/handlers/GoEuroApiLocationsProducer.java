package com.goeuro.handlers;

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import com.goeuro.LocationsRequest;

import com.goeuro.configuration.Config;

import com.goeuro.domain.Location;

/**
 * {@link Handler} that gets locations from GoEuro API.
 *
 * @author  Alexey Venderov
 * @see     <a href="http://api.goeuro.com/api/v2/position/suggest/en">GoEuro API</a>
 */
public class GoEuroApiLocationsProducer implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoEuroApiLocationsProducer.class);

    private static final ParameterizedTypeReference<List<Location>> RESPONSE_TYPE =
        new ParameterizedTypeReference<List<Location>>() { };

    private final RestTemplate restTemplate;

    private final String url;

    public GoEuroApiLocationsProducer(final Config config, final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        final String url = config.getApiUrl();

        // RestTemplate automatically encodes uri variables
        this.url = url.endsWith("/") ? url + "{city}" : url + "/{city}";
    }

    @Override
    public LocationsRequest handle(@Nonnull final LocationsRequest locationsRequest) {
        LOGGER.info("Searching for locations for the city [{}]", locationsRequest.getCity());

        // Normally calls to external services should be wrapped into a circuit breaker. But as soon as this is a
        // console application it is not needed
        final ResponseEntity<List<Location>> exchange = restTemplate.exchange(url, HttpMethod.GET, null, RESPONSE_TYPE,
                locationsRequest.getCity());
        final List<Location> locations = exchange.getBody();

        if (locations == null || locations.isEmpty()) {
            LOGGER.info("No locations found for the requested city [{}]", locationsRequest.getCity());

            return locationsRequest;
        }

        LOGGER.info("Found [{}] locations", locations.size());

        // Add locations to the request
        return locationsRequest.toBuilder().locations(locations).build();
    }

}
