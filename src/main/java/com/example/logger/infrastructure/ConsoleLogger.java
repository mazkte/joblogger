package com.example.logger.infrastructure;

import com.example.logger.JobLevel;
import com.example.logger.JobLoggerMessage;
import com.example.logger.Logger;
import com.example.logger.configuration.Configuration;

import java.util.List;
import java.util.logging.Level;

/**
 * @author mazkte
 */
public class ConsoleLogger implements Logger {

    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger( ConsoleLogger.class.getName() );

    private Configuration configuration;

    private static ConsoleLogger instance = null;

    public static ConsoleLogger getInstance(){
        if( instance == null ){
            instance = new ConsoleLogger();
        }
        return instance;
    }

    @Override
    public void logMessage(String message, List<JobLevel> level){
       level.stream().forEach( l -> print( l, message ) );
    }

    @Override
    public void config(Configuration configuration) {
        this.configuration = configuration;
    }

    private void print( JobLevel jobLevel , String message ){

       if( jobLevel == JobLevel.MESSAGE && configuration.isAllowMessageLevel() ){
            logger.log( Level.INFO , () -> JobLoggerMessage.formatText( jobLevel.label(), message ) );
        }
        if( jobLevel == JobLevel.WARNING  && configuration.isAllowWarningLevel()){
            logger.log( Level.INFO , ()-> JobLoggerMessage.formatText( jobLevel.label(), message ) );
        }
        if( jobLevel == JobLevel.ERROR && configuration.isAllowErrorLevel()){
            logger.log( Level.INFO , () -> JobLoggerMessage.formatText( jobLevel.label(), message )  );
        }

    }
}
