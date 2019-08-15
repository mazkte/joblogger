package com.example.logger.infrastructure;

import com.example.logger.JobLevel;
import com.example.logger.JobLoggerMessage;
import com.example.logger.Logger;
import com.example.logger.configuration.Configuration;
import com.example.logger.exceptions.JobLoggerException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

public class FileJobLogger implements Logger {

    private java.util.logging.Logger logger = java.util.logging.Logger.getLogger( FileJobLogger.class.getName() );

    private String pathname;
    private boolean append;

    private Configuration configuration;

    private static FileJobLogger instance = null;

    private FileHandler fileHandler;

    public static FileJobLogger getInstance( String pathname ){
        if( instance == null ){
            instance = new FileJobLogger( pathname );
        }
        return instance;
    }

    private FileJobLogger( String pathname ){
        this( pathname, true );
    }

    private FileJobLogger( String pathname , boolean append ){

        validPathname( pathname );
        createFileIfNoExist( pathname );
        this.pathname = pathname;
        this.append=append;
        setupHandler();
    }

    private void validPathname( String pathname ){
        if(StringUtils.isEmpty( pathname )){
            throw new JobLoggerException( String.format("Not defined pathname for %s" , FileJobLogger.class.getName()));
        }
    }

    private void createFileIfNoExist( String pathname ){

        Path path = Paths.get( pathname );
        if( !path.toFile().exists()){
            try {
                if( path.getParent().toFile().isAbsolute() && !path.getParent().toFile().exists()){
                    Files.createDirectories(  path.getParent() );
                }
                Files.createFile( path );
            } catch (IOException e) {
                throw new JobLoggerException( e.getMessage());
            }
        }
    }

    private void setupHandler(){
        try {
            fileHandler = new FileHandler(pathname, append);
            logger.addHandler( fileHandler );
        } catch (IOException e) {
            throw new JobLoggerException( e.getMessage());
        }
    }

    @Override
    public void logMessage(String message, List<JobLevel> jobLevels ) {

        List<JobLevel> filterLevels = jobLevels.stream()
                .filter( jobLevel -> ( get( jobLevel )!=null ) )
                .collect(Collectors.toList());

        if(!CollectionUtils.isEmpty( filterLevels )){

            LogRecord record = new LogRecord(Level.INFO, JobLoggerMessage.formatText( StringUtils.join( filterLevels,"|" ), message ) );
            record.setLoggerName( FileJobLogger.class.getName() );

            fileHandler.publish( record );
        }

    }

    private JobLevel get( JobLevel jobLevel  ){

        if( jobLevel == JobLevel.MESSAGE && configuration.isAllowMessageLevel() ){
            return jobLevel;
        }
        if( jobLevel == JobLevel.WARNING  && configuration.isAllowWarningLevel()){
            return jobLevel;
        }
        if( jobLevel == JobLevel.ERROR && configuration.isAllowErrorLevel()){
            return jobLevel;
        }
        return null;
    }

    @Override
    public void config(Configuration configuration) {
        this.configuration = configuration;

    }
}
