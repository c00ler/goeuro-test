package com.goeuro.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.goeuro.Locations;
import com.goeuro.LocationsRequest;

import com.goeuro.configuration.Config;

/**
 * @author  Alexey Venderov
 */
public class CsvFileLocationsConsumerTest implements Locations {

    @Mock
    private Config mockConfig;

    private StringWriter writer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockConfig.getOutputFile()).thenReturn(new File("/tmp/test.csv"));

        writer = new StringWriter();
    }

    @Test
    public void shouldPrintWithHeader() {
        final String expectedResult = "\"_id\",name,type,latitude,longitude\r\n"
                + "376217,Berlin,location,52.52437,13.41053\r\n";

        when(mockConfig.includeHeader()).thenReturn(true);

        final CsvFileLocationsConsumer consumer = new CsvFileLocationsConsumer(mockConfig) {

            @Override
            Writer getWriter() throws FileNotFoundException {
                return writer;
            }

        };

        consumer.handle(LocationsRequest.builder().city("berlin").locations(Collections.singletonList(location()))
                .build());

        assertThat(writer.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void shouldPrintWithoutHeader() {
        final String expectedResult = "376217,Berlin,location,52.52437,13.41053\r\n";

        when(mockConfig.includeHeader()).thenReturn(false);

        final CsvFileLocationsConsumer consumer = new CsvFileLocationsConsumer(mockConfig) {

            @Override
            Writer getWriter() throws FileNotFoundException {
                return writer;
            }

        };

        consumer.handle(LocationsRequest.builder().city("berlin").locations(Collections.singletonList(location()))
                .build());

        assertThat(writer.toString()).isEqualTo(expectedResult);
    }

    @Test
    public void shouldReturnSameRequest() {
        when(mockConfig.includeHeader()).thenReturn(false);

        final CsvFileLocationsConsumer consumer = new CsvFileLocationsConsumer(mockConfig) {

            @Override
            Writer getWriter() throws FileNotFoundException {
                return writer;
            }

        };

        final LocationsRequest locationsRequest = LocationsRequest.builder().city("berlin")
                                                                  .locations(Collections.singletonList(location()))
                                                                  .build();
        final LocationsRequest result = consumer.handle(locationsRequest);

        assertThat(result).isSameAs(locationsRequest);
    }

    @Test
    public void shouldSkipWritingIfNoLocations() {
        when(mockConfig.includeHeader()).thenReturn(false);

        final CsvFileLocationsConsumer consumer = new CsvFileLocationsConsumer(mockConfig) {

            @Override
            Writer getWriter() throws FileNotFoundException {
                throw new RuntimeException("method should not be called");
            }

        };

        final LocationsRequest locationsRequest = LocationsRequest.builder().city("berlin").build();
        final LocationsRequest result = consumer.handle(locationsRequest);

        assertThat(result).isSameAs(locationsRequest);
    }

    @Test
    public void shouldWriteIntoFile() throws IOException {
        final String expectedResult = "376217,Berlin,location,52.52437,13.41053\r\n";

        final Path directory = Files.createTempDirectory(CsvFileLocationsConsumerTest.class.getSimpleName());
        directory.toFile().deleteOnExit();

        final File outputFile = new File(directory.toFile(), UUID.randomUUID().toString());

        when(mockConfig.includeHeader()).thenReturn(false);
        when(mockConfig.getOutputFile()).thenReturn(outputFile);

        final CsvFileLocationsConsumer consumer = new CsvFileLocationsConsumer(mockConfig);

        consumer.handle(LocationsRequest.builder().city("berlin").locations(Collections.singletonList(location()))
                .build());

        final String result = new String(Files.readAllBytes(outputFile.toPath()), StandardCharsets.UTF_8);

        assertThat(result).isEqualTo(expectedResult);
    }

}
