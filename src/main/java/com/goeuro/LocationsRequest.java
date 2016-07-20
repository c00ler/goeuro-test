package com.goeuro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.goeuro.domain.Location;

/**
 * LocationsRequest represents a command from chain-of-responsibility pattern.
 *
 * @author  Alexey Venderov
 * @see     <a href="http://www.oodesign.com/chain-of-responsibility-pattern.html">Chain of Responsibility</a>
 */
public final class LocationsRequest {

    private final String city;

    private final Collection<Location> locations;

    private LocationsRequest(final String city, final Collection<Location> locations) {
        this.city = city;
        this.locations = locations;
    }

    @Nonnull
    public String getCity() {
        return city;
    }

    @Nonnull
    public Collection<Location> getLocations() {
        return locations;
    }

    public Builder toBuilder() {
        return new Builder().city(this.city).locations(this.locations);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LocationsRequest{");
        sb.append("city='").append(city).append('\'');
        sb.append(", locations=").append(locations);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {

        private String city;

        private Collection<Location> locations;

        private Builder() { }

        public Builder city(final String city) {
            this.city = city;

            return this;
        }

        public Builder locations(final Collection<Location> locations) {
            this.locations = locations;

            return this;
        }

        public LocationsRequest build() {
            Objects.requireNonNull(city, "city must not be null");

            return new LocationsRequest(city,
                    (locations == null || locations.isEmpty())
                        ? Collections.emptyList() : Collections.unmodifiableCollection(new ArrayList<>(locations)));
        }

    }

}
