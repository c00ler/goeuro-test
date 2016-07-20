package com.goeuro.handlers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goeuro.LocationsRequest;

import com.goeuro.annotations.VisibleForTesting;

import com.goeuro.configuration.Config;

import com.goeuro.domain.Location;

/**
 * @author  Alexey Venderov
 */
public class CsvFileLocationsConsumer implements Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFileLocationsConsumer.class);

    private final Config config;

    private final CSVFormat csvFormat;

    public CsvFileLocationsConsumer(final Config config) {
        this.config = config;
        this.csvFormat = config.includeHeader()
            ? CSVFormat.DEFAULT.withHeader("_id", "name", "type", "latitude", "longitude") : CSVFormat.DEFAULT;
    }

    @VisibleForTesting
    Writer getWriter() throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(config.getOutputFile()), StandardCharsets.UTF_8);
    }

    @Override
    public LocationsRequest handle(@Nonnull final LocationsRequest locationsRequest) {
        final Collection<Location> locations = locationsRequest.getLocations();

        if (!locations.isEmpty()) {
            try(final CSVPrinter csvPrinter = new CSVPrinter(getWriter(), csvFormat)) {
                for (final Location location : locations) {
                    csvPrinter.printRecord(location.toRecord());
                }

                csvPrinter.flush(); // will flush the underlying writer

                LOGGER.info("File [{}] was written successfully", config.getOutputFile().getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Can't write to the file", e);
            }
        }

        return locationsRequest;
    }

}
