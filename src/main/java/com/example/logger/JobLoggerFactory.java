package com.example.logger;

import com.example.logger.configuration.BasicConfiguration;
import com.example.logger.configuration.JobConfiguration;
import com.example.logger.infrastructure.ConsoleLogger;
import com.example.logger.infrastructure.DatabaseJobLogger;
import com.example.logger.infrastructure.FileJobLogger;

import java.util.Properties;

public class JobLoggerFactory {


    public static final int CONSOLE_TYPE = 1;
    public static final int FILE_TYPE = 2;
    public static final int DATABASE_TYPE = 3;

    private JobLoggerFactory(){}

    public static Logger getLogger(){
        return new ConsoleLogger();
    }

    /**
     *
     * @param type
     * @param configuration
     * @return
     */
    public static Logger create( int type , JobConfiguration configuration){
        switch ( type ){
            case CONSOLE_TYPE: return createConsoleLogger( configuration.getFileLogProperties() ,
                                                configuration.isAllowErrorLevel(),
                                                configuration.isAllowMessageLevel(),
                                                configuration.isAllowWarningLevel() );

            case FILE_TYPE: return createFileJobLogger( configuration.getConsoleProperties() ,
                                                configuration.isAllowErrorLevel(),
                                                configuration.isAllowMessageLevel(),
                                                configuration.isAllowWarningLevel() );

            case DATABASE_TYPE: return createDatabaseLogger( configuration.getDbProperties() ,
                                                configuration.isAllowErrorLevel(),
                                                configuration.isAllowMessageLevel(),
                                                configuration.isAllowWarningLevel() );
            default: return null;

        }
    }

    /**
     *
     * @param type
     * @param configuration
     * @param onlyMessage
     * @param onlyWarnings
     * @param onlyError
     * @return
     */
    public static Logger create( int type, JobConfiguration configuration, boolean onlyMessage , boolean onlyWarnings, boolean onlyError ){

        if( type == CONSOLE_TYPE ){
            return createConsoleLogger( configuration.getConsoleProperties() , onlyMessage, onlyWarnings, onlyError );
        }
        else if( type == FILE_TYPE ){
            return createFileJobLogger( configuration.getFileLogProperties() , onlyMessage, onlyWarnings, onlyError );
        }else if( type == DATABASE_TYPE){
            return createDatabaseLogger( configuration.getDbProperties() ,  onlyMessage, onlyWarnings , onlyError );
        }

        return null;
    }

    /**
     *
     * @param fileProperties
     * @param onlyMessage
     * @param onlyWarnings
     * @param onlyError
     * @return
     */
    private static FileJobLogger createFileJobLogger(Properties fileProperties, boolean onlyMessage , boolean onlyWarnings, boolean onlyError  ){

        String finalName = fileProperties.getProperty("joblogger.configuration.logfile.properties.path");

        FileJobLogger fileJobLogger = FileJobLogger.getInstance( finalName );
        fileJobLogger.config( new BasicConfiguration( onlyMessage, onlyWarnings, onlyError,fileProperties));
        return fileJobLogger;
    }

    /**
     *
     * @param consoleProperties
     * @param onlyMessage
     * @param onlyWarnings
     * @param onlyError
     * @return
     */
    private static ConsoleLogger createConsoleLogger( Properties consoleProperties, boolean onlyMessage , boolean onlyWarnings, boolean onlyError  ){
        ConsoleLogger consoleLogger = ConsoleLogger.getInstance();
        consoleLogger.config( new BasicConfiguration( onlyMessage, onlyWarnings, onlyError , consoleProperties));
        return consoleLogger;
    }

    /**
     *
     * @param databaseProperties
     * @param onlyMessage
     * @param onlyWarnings
     * @param onlyError
     * @return
     */
    private static DatabaseJobLogger createDatabaseLogger(Properties databaseProperties, boolean onlyMessage , boolean onlyWarnings, boolean onlyError ){

        DatabaseJobLogger dbLogger = DatabaseJobLogger.getInstance();
        dbLogger.config( new BasicConfiguration( onlyMessage , onlyWarnings, onlyError , databaseProperties ) );
        return dbLogger;

    }

}
