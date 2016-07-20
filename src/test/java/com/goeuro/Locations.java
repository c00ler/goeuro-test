package com.goeuro;

import com.goeuro.domain.GeoPosition;
import com.goeuro.domain.Location;

/**
 * @author  Alexey Venderov
 */
public interface Locations {

    default Location location() {
        final Location location = new Location();

        location.setId(376217L);
        location.setName("Berlin");
        location.setType("location");

        final GeoPosition geoPosition = new GeoPosition();
        geoPosition.setLatitude(52.52437);
        geoPosition.setLongitude(13.41053);

        location.setGeoPosition(geoPosition);

        return location;
    }

}
