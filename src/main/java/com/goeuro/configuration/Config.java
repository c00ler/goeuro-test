package com.goeuro.configuration;

import java.io.File;

/**
 * @author  Alexey Venderov
 */
public interface Config {

    String getApiUrl();

    long getConnectTimeout();

    long getReadTimeout();

    File getOutputFile();

    boolean includeHeader();

}
