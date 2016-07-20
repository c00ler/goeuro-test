package com.goeuro.configuration;

import static java.lang.String.format;

import java.io.File;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.goeuro.annotations.VisibleForTesting;

/**
 * @author  Alexey Venderov
 */
class PropertiesBasedConfig implements Config {

    @VisibleForTesting
    static final long DEFAULT_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(3L);

    private final Properties properties;

    public PropertiesBasedConfig(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getApiUrl() {
        return requiredProperty("api.url");
    }

    @Override
    public long getConnectTimeout() {
        return property("api.connect.timeout").map(Long::parseLong).orElse(DEFAULT_TIMEOUT_MILLIS);
    }

    @Override
    public long getReadTimeout() {
        return property("api.read.timeout").map(Long::parseLong).orElse(DEFAULT_TIMEOUT_MILLIS);
    }

    @Override
    public File getOutputFile() {
        return new File(requiredProperty("output.file"));
    }

    @Override
    public boolean includeHeader() {
        return property("include.header").map(Boolean::parseBoolean).orElse(true);
    }

    private String requiredProperty(final String key) {
        return property(key).orElseThrow(() ->
                    new NullPointerException(format("Required property [%s] is missing", key)));
    }

    private Optional<String> property(final String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

}
