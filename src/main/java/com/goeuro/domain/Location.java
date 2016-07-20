package com.goeuro.domain;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean that contains location properties that we are interested in.
 *
 * @author  Alexey Venderov
 */
public class Location {

    @JsonProperty("_id")
    private Long id;

    private String name;

    private String type;

    private GeoPosition geoPosition;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public void setGeoPosition(final GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    public Collection<Object> toRecord() {
        final Collection<Object> record = new ArrayList<>(5);
        record.add(this.getId());
        record.add(this.getName());
        record.add(this.getType());
        record.add(this.getGeoPosition().getLatitude());
        record.add(this.getGeoPosition().getLongitude());

        return record;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Location{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", geoPosition=").append(geoPosition);
        sb.append('}');
        return sb.toString();
    }

}
