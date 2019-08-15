package com.example.logger.configuration;

import com.example.logger.JobLevel;
import com.example.logger.exceptions.JobLoggerException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JobConfiguration {

    private Properties consoleProperties;
    private Properties fileLogProperties;
    private Properties dbProperties;

    private boolean enableLogConsole;
    private boolean enableLogFile;
    private boolean enableLogDatabase;

    private boolean allowWarningLevel;
    private boolean allowMessageLevel;
    private boolean allowErrorLevel;

    private List<JobLevel> levels;


    public JobConfiguration(){
        consoleProperties = new Properties();
        fileLogProperties = new Properties();
        dbProperties = new Properties();
        levels = new ArrayList<>();
    }

    public JobConfiguration( String propertyFileName ){
        this();
        loadProperties( propertyFileName );
    }

    /**
     *
     * @param propertyFileName
     */
    private void loadProperties(String propertyFileName) {

        Resource resource = new ClassPathResource( propertyFileName );

        Properties globalProperties = new Properties();
        try{
            globalProperties.load( resource.getInputStream() );
        }catch (IOException ex){
            throw  new JobLoggerException( ex.getMessage() );
        }

        loadBasicProperties( globalProperties );
        loadConsoleProperties( globalProperties );
        loadFileLogProperties( globalProperties );
        loadDbProperties( globalProperties );
    }

    private void loadBasicProperties( Properties globalProperties ){

        enableLogConsole    = Boolean.valueOf( globalProperties.getProperty("joblogger.configuration.console.enabled") );
        enableLogFile       = Boolean.valueOf( globalProperties.getProperty("joblogger.configuration.filelog.enabled") );
        enableLogDatabase   = Boolean.valueOf( globalProperties.getProperty("joblogger.configuration.database.enabled") );

        setAllowMessageLevel(  Boolean.valueOf( globalProperties.getProperty("joblogger.configuration.level.message" ) ) );
        setAllowWarningLevel( Boolean.valueOf( globalProperties.getProperty("joblogger.configuration.level.warning") ) );
        setAllowErrorLevel( Boolean.valueOf( globalProperties.getProperty("joblogger.configuration.level.error" ) ) );
    }

    private void loadDbProperties( Properties globalProperties ){
        globalProperties.forEach((key, value) -> {
            if( String.valueOf( key ).contains("joblogger.configuration.database.properties") ){
                dbProperties.setProperty(key.toString(), value.toString());
            }
        });
    }

    private void loadFileLogProperties( Properties globalProperties ){
        globalProperties.forEach((key, value) -> {
            if( String.valueOf( key ).contains("joblogger.configuration.logfile.properties") ){
                fileLogProperties.setProperty(key.toString(), value.toString());
            }
        });
    }

    private void loadConsoleProperties( Properties globalProperties  ){
        globalProperties.forEach((key, value) -> {
            if( String.valueOf( key ).contains("joblogger.configuration.console.properties") ){
                consoleProperties.setProperty(key.toString(), value.toString());
            }
        });
    }


    public boolean isEnableLogConsole() {
        return enableLogConsole;
    }

    public void setEnableLogConsole(boolean enableLogConsole) {
        this.enableLogConsole = enableLogConsole;
    }

    public boolean isEnableLogFile() {
        return enableLogFile;
    }

    public void setEnableLogFile(boolean enableLogFile) {
        this.enableLogFile = enableLogFile;
    }

    public boolean isEnableLogDatabase() {
        return enableLogDatabase;
    }

    public void setEnableLogDatabase(boolean enableLogDatabase) {
        this.enableLogDatabase = enableLogDatabase;
    }

    public boolean isAllowWarningLevel() {
        return allowWarningLevel;
    }

    public void setAllowWarningLevel(boolean allowWarningLevel) {
        this.allowWarningLevel = allowWarningLevel;
        if( allowWarningLevel ){
            levels.add( JobLevel.WARNING );
        }
    }

    public boolean isAllowMessageLevel() {
        return allowMessageLevel;
    }

    public void setAllowMessageLevel(boolean allowMessageLevel) {
        this.allowMessageLevel = allowMessageLevel;
        if( allowMessageLevel ){
            levels.add( JobLevel.MESSAGE );
        }

    }

    public boolean isAllowErrorLevel() {
        return allowErrorLevel;
    }

    public void setAllowErrorLevel(boolean allowErrorLevel) {
        this.allowErrorLevel = allowErrorLevel;
        if( allowErrorLevel ){
            levels.add( JobLevel.ERROR );
        }
    }

    public Properties getConsoleProperties() {
        return consoleProperties;
    }

    public void setConsoleProperties(Properties consoleProperties) {
        this.consoleProperties = consoleProperties;
    }

    public Properties getFileLogProperties() {
        return fileLogProperties;
    }

    public void setFileLogProperties(Properties fileLogProperties) {
        this.fileLogProperties = fileLogProperties;
    }

    public Properties getDbProperties() {
        return dbProperties;
    }

    public void setDbProperties(Properties dbProperties) {
        this.dbProperties = dbProperties;
    }

    public List<JobLevel> getLevels() {
        return levels;
    }

    public void validConfiguration(){
        if ( (!enableLogConsole && !enableLogDatabase && !enableLogFile) ||
                ( !allowErrorLevel && !allowMessageLevel && !allowWarningLevel )) {
            throw new JobLoggerException("Invalid configuration => you must define a connection type {console, logfile or database}");
        }

        if( CollectionUtils.isEmpty( levels ) ){
            throw new JobLoggerException("Invalid configuration => A level must be defined for the log");
        }
    }
}
