package com.example.logger;

import com.example.logger.configuration.JobConfiguration;
import com.example.logger.exceptions.JobLoggerException;
import com.example.logger.infrastructure.DatabaseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "classpath:schema.sql")
public class JobLoggerTest {

    private Logger logger = LoggerFactory.getLogger( JobLogger.class );

    private String pathname="C:\\opt\\logger\\logfile.txt";

    @Test
    public void testConnectionTypeNotSetForLogThenError(){

        JobConfiguration configuration = buildConfiguration( false,false,false,
                                                            true, true ,true );

        JobLoggerException loggerException = Assertions.assertThrows(JobLoggerException.class , () -> new JobLogger( configuration ));
        logger.error( loggerException.getMessage() );

    }

    @Test
    public void testLevelTypeNotSetForLogThenError(){

        JobConfiguration configuration = buildConfiguration( true,true,true,
                false, false ,false );

        JobLoggerException loggerException = Assertions.assertThrows(JobLoggerException.class , () -> new JobLogger( configuration ));
        logger.error( loggerException.getMessage() );

    }

    /**
     * Valida que el objeto JobLogger este configurado para todos los entornos y para los niveles de log
     */
    @Test
    public void testCreateJobLoggerFullLoggingThenOK(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( true,true,true,
                true, true ,true ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        assertNotNull( currentConfiguration );

        assertEquals( true , currentConfiguration.isEnableLogConsole() );
        assertEquals( true , currentConfiguration.isEnableLogDatabase() );
        assertEquals( true , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.MESSAGE , JobLevel.WARNING , JobLevel.ERROR ) , levels );

    }

    @Test
    public void testCreateJobLoggerForConsoleThenOk(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( true,false,false,
                true, true ,true ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        assertNotNull( currentConfiguration );

        assertEquals( true , currentConfiguration.isEnableLogConsole() );
        assertEquals( false , currentConfiguration.isEnableLogDatabase() );
        assertEquals( false , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.MESSAGE , JobLevel.WARNING , JobLevel.ERROR ) , levels );

        jobLogger.log( "TEST MESSAGE FOR CONSOLE" );
    }

    @Test
    public void testCreateJobLoggerForConsoleAndWarningLevelThenOk(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( true,false,false,
                false, true ,false ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        assertNotNull( currentConfiguration );

        assertEquals( true , currentConfiguration.isEnableLogConsole() );
        assertEquals( false , currentConfiguration.isEnableLogDatabase() );
        assertEquals( false , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.WARNING ) , levels );

        jobLogger.log( "TEST MESSAGE FOR CONSOLE" );
    }

    @Test
    public void testCreateJobLoggerForFileThenOk(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( false,true,false,
                                                                true, true ,true ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        Properties properties = new Properties();
        properties.put("joblogger.configuration.logfile.properties.path",pathname);

        currentConfiguration.setFileLogProperties(  properties );

        assertNotNull( currentConfiguration );

        assertEquals( false , currentConfiguration.isEnableLogConsole() );
        assertEquals( false , currentConfiguration.isEnableLogDatabase() );
        assertEquals( true , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.MESSAGE , JobLevel.WARNING , JobLevel.ERROR ) , levels );

        jobLogger.log( "TEST MESSAGE FOR FILE" );
    }

    @Test
    public void testCreateJobLoggerForFileAndMessageLevelThenOk(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( false,true,false,
                                                                true, false ,false ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        Properties properties = new Properties();
        properties.put("joblogger.configuration.logfile.properties.path","C:\\opt\\logger\\logfile.txt");

        currentConfiguration.setFileLogProperties(  properties );

        assertNotNull( currentConfiguration );

        assertEquals( false , currentConfiguration.isEnableLogConsole() );
        assertEquals( false , currentConfiguration.isEnableLogDatabase() );
        assertEquals( true , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.MESSAGE ) , levels );

        jobLogger.log( "TEST MESSAGE FOR FILE" );

    }

    @Test
    public void testCreateJobLoggerForDatabaseThenOk(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( false,false,true,
                false, true ,true ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        Properties dbProperties = new Properties();

        dbProperties.put( "joblogger.configuration.database.properties.user","dblogger" );
        dbProperties.put( "joblogger.configuration.database.properties.password","dblo$$3r" );
        dbProperties.put( "joblogger.configuration.database.properties.driverClassName","org.h2.Driver" );
        dbProperties.put( "joblogger.configuration.database.properties.url","jdbc:h2:mem:dblogger" );

        currentConfiguration.setDbProperties( dbProperties );

        assertNotNull( currentConfiguration );

        assertEquals( false , currentConfiguration.isEnableLogConsole() );
        assertEquals( true , currentConfiguration.isEnableLogDatabase() );
        assertEquals( false , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = currentConfiguration.getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.WARNING , JobLevel.ERROR ) , levels );

        jobLogger.log( "TEST MESSAGE FOR DATABASE" );
    }

    @Test
    public void testCreateJobLoggerForDatabaseAndErrWarningLevelThenOk(){

        JobLogger jobLogger = new JobLogger( buildConfiguration( false,false,true,
                false, true ,true ) );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration =  jobLogger.getConfiguration();

        Properties dbProperties = new Properties();

        dbProperties.put( "joblogger.configuration.database.properties.user","dblogger" );
        dbProperties.put( "joblogger.configuration.database.properties.password","dblo$$3r" );
        dbProperties.put( "joblogger.configuration.database.properties.driverClassName","org.h2.Driver" );
        dbProperties.put( "joblogger.configuration.database.properties.url","jdbc:h2:mem:dblogger" );

        currentConfiguration.setDbProperties( dbProperties );

        assertNotNull( currentConfiguration );

        assertEquals( false , currentConfiguration.isEnableLogConsole() );
        assertEquals( true , currentConfiguration.isEnableLogDatabase() );
        assertEquals( false , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.WARNING , JobLevel.ERROR ) , levels );

        jobLogger.log( "TEST MESSAGE FOR DATABASE" );
    }

    private JobConfiguration buildConfiguration( boolean enableLogConsole, boolean enableFileConsole, boolean enableDbConsole,
                                                 boolean allowMessageLevel, boolean allowWarningLevel, boolean allowErrorLevel ){
        JobConfiguration jobConfiguration = new JobConfiguration();

        jobConfiguration.setEnableLogConsole( enableLogConsole );
        jobConfiguration.setEnableLogDatabase( enableDbConsole );
        jobConfiguration.setEnableLogFile( enableFileConsole );

        jobConfiguration.setAllowMessageLevel( allowMessageLevel );
        jobConfiguration.setAllowWarningLevel( allowWarningLevel );
        jobConfiguration.setAllowErrorLevel( allowErrorLevel );


        return jobConfiguration;
    }

    /**
     * Test con configuracion en archivo properties
     */
    @Test
    public void testCreateJobLoggerFromPropertiesConfigThenOk(){

        JobLogger jobLogger = new JobLogger( new JobConfiguration("application.properties") );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration = jobLogger.getConfiguration();

        assertNotNull( currentConfiguration );

        assertEquals( true , currentConfiguration.isEnableLogConsole() );
        assertEquals( true , currentConfiguration.isEnableLogDatabase() );
        assertEquals( true , currentConfiguration.isEnableLogFile() );

        List<JobLevel> levels = jobLogger.getConfiguration().getLevels();
        assertIterableEquals( Arrays.asList( JobLevel.MESSAGE , JobLevel.WARNING , JobLevel.ERROR ) , levels );
    }


    @Test
    public void testValidateJobLoggerExistRecordsThenOk(){

        JobLogger jobLogger = new JobLogger( new JobConfiguration("application.properties") );

        assertNotNull( jobLogger );

        JobConfiguration currentConfiguration = jobLogger.getConfiguration();

        assertNotNull( currentConfiguration );

        String message = "SOMEWHERE I BELONG";

        //Solo registraremmos los niveles WARNING | ERROR
        jobLogger.log( message , false, true, true );

        //Verificar si registro en la BD el mensaje
        List<EventLog> eventLogs = DatabaseHelper.getInstance().list();
        assertFalse( CollectionUtils.isEmpty( eventLogs ));
        assertTrue( (eventLogs.size()==1 ));

        eventLogs.stream().forEach( e-> assertEquals( message, e.getMessage() ) );

        //Verificar si registro en BD los niveles indicados
        eventLogs.stream().forEach( e-> assertEquals( "WARNING|ERROR" , e.getJobLevel() ) );

    }

    @Test
    public void testValidateJobLoggerExistFileThenOk()  {

        JobLogger jobLogger = new JobLogger(new JobConfiguration("application.properties"));

        assertNotNull(jobLogger);

        JobConfiguration currentConfiguration = jobLogger.getConfiguration();
        currentConfiguration.setEnableLogDatabase(false);
        currentConfiguration.setEnableLogConsole(false);

        assertNotNull(currentConfiguration);

        String message = "SOMEWHERE I BELONG";

        //Solo registraremmos los niveles WARNING | ERROR
        jobLogger.log(message, false, true, true);

        String pathname = currentConfiguration.getFileLogProperties().getProperty("joblogger.configuration.logfile.properties.path");

        File logFile = Paths.get(pathname).toFile();

        assertTrue(logFile.exists());
        assertTrue((logFile.length() > 0));

    }

}
