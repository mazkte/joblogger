package com.example.logger.configuration;

import java.util.Properties;

public interface Configuration {

    boolean isAllowWarningLevel();

    boolean isAllowMessageLevel();

    boolean isAllowErrorLevel();

    Properties loadProperties();
}
