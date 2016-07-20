package com.goeuro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.goeuro.domain.Location;

/**
 * @author  Alexey Venderov
 */
public class LocationsRequestTest implements Locations {

    @Test
    public void shouldThrowIfCityIsNull() {
        assertThatThrownBy(() -> LocationsRequest.builder().build()).isInstanceOf(NullPointerException.class)
                                .hasMessage("city must not be null");
    }

    @Test
    public void shouldReturnEmptyLocationsByDefault() {
        final LocationsRequest locationsRequest = LocationsRequest.builder().city("berlin").build();

        assertThat(locationsRequest.getLocations()).isEmpty();
    }

    @Test
    public void shouldCopyPreviousValues() {
        final LocationsRequest locationsRequest = LocationsRequest.builder().city("berlin")
                                                                  .locations(Collections.singletonList(location()))
                                                                  .build();
        final LocationsRequest anotherLocationRequest = locationsRequest.toBuilder().build();

        assertThat(locationsRequest.getCity()).isEqualTo(anotherLocationRequest.getCity());
        assertThat(locationsRequest.getLocations()).isNotSameAs(anotherLocationRequest.getLocations());

        final Location locationFromRequest = getOnlyElement(locationsRequest.getLocations());
        final Location locationFromAnotherRequest = getOnlyElement(anotherLocationRequest.getLocations());

        assertThat(locationFromRequest).isSameAs(locationFromAnotherRequest);
    }

    @Test
    public void shouldReturnImmutableCollectionOfLocations() {
        final List<Location> locations = new ArrayList<>();
        locations.add(location());

        final LocationsRequest locationsRequest = LocationsRequest.builder().city("berlin").locations(locations)
                                                                  .build();

        assertThatThrownBy(() -> locationsRequest.getLocations().add(location())).isInstanceOf(
            UnsupportedOperationException.class);
    }

    private static <T> T getOnlyElement(final Collection<T> collection) {
        final Iterator<T> iterator = collection.iterator();

        final T first = iterator.next();

        if (iterator.hasNext()) {
            throw new IllegalStateException("Collection has more than one element");
        }

        return first;
    }

}
