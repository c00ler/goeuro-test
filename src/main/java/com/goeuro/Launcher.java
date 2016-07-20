package com.goeuro;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;

import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.util.StringUtils;

import com.goeuro.annotations.VisibleForTesting;

import com.goeuro.configuration.ApplicationConfiguration;
import com.goeuro.configuration.Config;

import com.goeuro.handlers.Handler;

/**
 * @author  Alexey Venderov
 */
public final class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    private final ApplicationContext context;

    public Launcher(final ApplicationContext context) {
        this.context = context;
    }

    public static void main(final String[] args) {

        // Context will be automatically closed in the end
        try(final ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(
                        ApplicationConfiguration.class)) {
            final String city = getCity(args);

            new Launcher(context).run(city);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public void run(final String city) throws IOException {
        final Config config = context.getBean(Config.class);

        // Do not need to run the program if we can't write to the file in the end
        checkOutputFile(config.getOutputFile());

        final Handler locationsProducer = context.getBean("locationsProvider", Handler.class);
        final Handler locationsConsumer = context.getBean("locationsConsumer", Handler.class);

        // I use chain of responsibility design pattern to build pipeline that processes the request. More handlers
        // can be added between producer and consumer
        final Function<LocationsRequest, LocationsRequest> pipeline = locationsProducer.andThen(locationsConsumer);

        final LocationsRequest locationsRequest = LocationsRequest.builder().city(city).build();
        pipeline.apply(locationsRequest);
    }

    @VisibleForTesting
    static void checkOutputFile(final File file) throws IOException {
        Objects.requireNonNull(file);

        if (file.isDirectory()) {
            throw new IOException(format("[%s] is a directory, not a file", file.getAbsolutePath()));
        }

        if (file.exists()) {
            throw new IOException(format("File [%s] already exists", file.getAbsolutePath()));
        }

        final File parentDir = file.getAbsoluteFile().getParentFile();
        if (!parentDir.exists()) {
            throw new IOException(format("Directory [%s] doesn't exist", parentDir.getAbsolutePath()));
        }

        if (!parentDir.canWrite()) {
            throw new IOException(format("Directory [%s] is not writeable", parentDir.getAbsolutePath()));
        }
    }

    @VisibleForTesting
    static String getCity(final String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Required argument is missing");
        }

        final String city = args[0];
        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException("Argument must not be blank");
        }

        if (args.length > 1) {
            LOGGER.warn("Only the first argument will be used");
        }

        return city.trim();
    }

}
