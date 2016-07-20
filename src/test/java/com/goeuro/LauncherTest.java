package com.goeuro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.goeuro.configuration.Config;

import com.goeuro.handlers.CsvFileLocationsConsumer;
import com.goeuro.handlers.Handler;

/**
 * @author  Alexey Venderov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LauncherTest.TestConfiguration.class)
public class LauncherTest {

    @Autowired
    private ApplicationContext context;

    @Configuration
    public static class TestConfiguration implements Locations {

        // Fake provider
        @Bean
        public Handler locationsProvider() {
            return
                locationsRequest ->
                    locationsRequest.toBuilder().locations(Collections.singletonList(location())).build();
        }

        // Real consumer
        @Bean
        public Handler locationsConsumer(final Config config) {
            return new CsvFileLocationsConsumer(config);
        }

        // Config will be created only once, because context is cached
        @Bean
        public Config config() throws IOException {
            final Path directory = Files.createTempDirectory(LauncherTest.class.getSimpleName());
            directory.toFile().deleteOnExit();

            final Config mockConfig = mock(Config.class);
            when(mockConfig.includeHeader()).thenReturn(false);
            when(mockConfig.getOutputFile()).thenReturn(new File(directory.toFile(), UUID.randomUUID().toString()));

            return mockConfig;
        }

    }

    @Test
    public void shouldThrowIfNoArgsAreEmpty() {
        final String[] args = new String[0];

        assertThatThrownBy(() -> Launcher.getCity(args)).isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Required argument is missing");
    }

    @Test
    public void shouldThrowIfCityIsBlank() {
        final String[] args = {" "};

        assertThatThrownBy(() -> Launcher.getCity(args)).isInstanceOf(IllegalArgumentException.class).hasMessage(
            "Argument must not be blank");
    }

    @Test
    public void shouldReturnFirstArgument() {
        final String[] args = {"berlin", "london"};

        assertThat(Launcher.getCity(args)).isEqualTo("berlin");
    }

    @Test
    public void shouldTrimArgument() {
        final String[] args = {" berlin "};

        assertThat(Launcher.getCity(args)).isEqualTo("berlin");
    }

    @Test
    public void shouldCreateAndRunPipeline() throws IOException {
        new Launcher(context).run("berlin");

        final String expectedResult = "376217,Berlin,location,52.52437,13.41053\r\n";

        final Config config = context.getBean(Config.class);
        final String result = new String(Files.readAllBytes(config.getOutputFile().toPath()), StandardCharsets.UTF_8);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void shouldThrowIfOutputFileIsDir() {
        final File mockFile = mock(File.class);
        when(mockFile.isDirectory()).thenReturn(true);
        when(mockFile.getAbsolutePath()).thenReturn("/test");

        assertThatThrownBy(() -> Launcher.checkOutputFile(mockFile)).isInstanceOf(IOException.class)
                                .hasMessageContaining("is a directory, not a file").hasMessageContaining("/test");
    }

    @Test
    public void shouldThrowIfOutputFileExists() {
        final File mockFile = mock(File.class);
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.getAbsolutePath()).thenReturn("/test/result.csv");

        assertThatThrownBy(() -> Launcher.checkOutputFile(mockFile)).isInstanceOf(IOException.class)
                                .hasMessageContaining("already exists").hasMessageContaining("/test/result.csv");
    }

    @Test
    public void shouldThrowIfDirectoryNotExist() {
        final File mockFile = mock(File.class);
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.getAbsoluteFile()).thenReturn(mockFile);

        final File mockParentDir = mock(File.class);
        when(mockParentDir.exists()).thenReturn(false);
        when(mockParentDir.getAbsolutePath()).thenReturn("/test");

        when(mockFile.getParentFile()).thenReturn(mockParentDir);

        assertThatThrownBy(() -> Launcher.checkOutputFile(mockFile)).isInstanceOf(IOException.class)
                                .hasMessageContaining("doesn't exist").hasMessageContaining("/test");
    }

    @Test
    public void shouldThrowIfDirectoryNotWriteable() {
        final File mockFile = mock(File.class);
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.getAbsoluteFile()).thenReturn(mockFile);

        final File mockParentDir = mock(File.class);
        when(mockParentDir.exists()).thenReturn(true);
        when(mockParentDir.canWrite()).thenReturn(false);
        when(mockParentDir.getAbsolutePath()).thenReturn("/test");

        when(mockFile.getParentFile()).thenReturn(mockParentDir);

        assertThatThrownBy(() -> Launcher.checkOutputFile(mockFile)).isInstanceOf(IOException.class)
                                .hasMessageContaining("is not writeable").hasMessageContaining("/test");
    }

}
