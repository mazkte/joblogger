package com.example.logger.configuration;

import com.example.logger.exceptions.JobLoggerException;

import java.util.Properties;

/**
 * @author mazkte
 */
public class BasicConfiguration implements Configuration {

    private boolean allowWarningLevel;
    private boolean allowMessageLevel;
    private boolean allowErrorLevel;
    private Properties properties;

    /**
     *
     * @param allowWarningLevel
     * @param allowMessageLevel
     * @param allowErrorLevel
     * @param properties
     */
    public BasicConfiguration(boolean allowMessageLevel, boolean allowWarningLevel, boolean allowErrorLevel , Properties properties){

        if (!allowWarningLevel && !allowMessageLevel && !allowErrorLevel) {
            throw new JobLoggerException("Invalid  basic configuration");
        }

        this.allowMessageLevel = allowMessageLevel;
        this.allowWarningLevel = allowWarningLevel;
        this.allowErrorLevel = allowErrorLevel;
        this.properties = properties;
    }

    @Override
    public boolean isAllowWarningLevel() {
        return allowWarningLevel;
    }

    @Override
    public boolean isAllowMessageLevel() {
        return allowMessageLevel;
    }

    @Override
    public boolean isAllowErrorLevel() {
        return allowErrorLevel;
    }

    @Override
    public Properties loadProperties() {
        return properties;
    }
}
