package com.example.logger;

import com.example.logger.configuration.Configuration;
import com.example.logger.exceptions.JobLoggerException;

import java.util.List;

/**
 * @author mazkte
 */
public interface Logger {

    /**
     *
     * @param message
     * @param levels
     * @throws JobLoggerException
     */
    void logMessage( String message, List<JobLevel> levels );

    /**
     *
     * @param configuration
     */
    void config(Configuration configuration);

}
