package com.example.logger;


import com.example.logger.configuration.JobConfiguration;
import com.example.logger.exceptions.JobLoggerException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.example.logger.JobLoggerFactory.*;

public class JobLogger {

   private JobConfiguration configuration;


   public JobLogger ( JobConfiguration configuration ){
        configuration.validConfiguration();
        this.configuration = configuration;
   }

    /**
     * defaul logging
     * @param message
     */
    public void log( String message ){
        log( message, configuration.isAllowMessageLevel(),
                      configuration.isAllowWarningLevel() ,
                      configuration.isAllowErrorLevel() );
    }

    /**
     * logging
     * @param message
     * @param onlyWarnings
     * @param onlyMessage
     * @param onlyError
     */
    public void log( String message , boolean onlyMessage , boolean onlyWarnings, boolean onlyError ){

        if( StringUtils.isEmpty( message ) ){
            return;
        }

        validFilters( onlyWarnings, onlyMessage, onlyError );

        List<JobLevel> defaultLevels = configuration.getLevels();

        if( configuration.isEnableLogConsole() ){
            create( CONSOLE_TYPE , configuration ,onlyMessage , onlyWarnings, onlyError).logMessage( message , defaultLevels);
        }
        if( configuration.isEnableLogFile() ){
            create( FILE_TYPE, configuration , onlyMessage , onlyWarnings, onlyError).logMessage( message, defaultLevels );
        }
        if( configuration.isEnableLogDatabase() ){
            create( DATABASE_TYPE, configuration , onlyMessage , onlyWarnings, onlyError).logMessage( message, defaultLevels );
        }
    }

    /**
     *
     * @param onlyMessage
     * @param onlyWarnings
     * @param onlyError
     */
    private void validFilters( boolean onlyMessage , boolean onlyWarnings, boolean onlyError ){
        if (!onlyMessage && !onlyWarnings && !onlyError) {
            throw new JobLoggerException("Invalid configuration");
        }
    }

    public JobConfiguration getConfiguration(){
        return configuration;
    }


}
