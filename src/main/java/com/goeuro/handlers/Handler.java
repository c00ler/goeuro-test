package com.goeuro.handlers;

import java.util.Objects;
import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;

import com.goeuro.LocationsRequest;

/**
 * Handler represents a processing object from chain-of-responsibility pattern.
 *
 * @author  Alexey Venderov
 * @see     <a href="http://www.oodesign.com/chain-of-responsibility-pattern.html">Chain of Responsibility</a>
 */
public interface Handler extends UnaryOperator<LocationsRequest> {

    default @Override LocationsRequest apply(final LocationsRequest locationsRequest) {
        Objects.requireNonNull(locationsRequest, "locationsRequest must not be null");

        return handle(locationsRequest);
    }

    LocationsRequest handle(@Nonnull final LocationsRequest locationsRequest);

}
