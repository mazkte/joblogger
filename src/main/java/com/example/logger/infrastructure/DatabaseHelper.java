package com.example.logger.infrastructure;

import com.example.logger.EventLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

import static com.example.logger.Util.convertToLevels;

public class DatabaseHelper  {

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    private static DatabaseHelper instance;

    private DatabaseHelper(){}

    public static DatabaseHelper getInstance(){
        if( instance == null ){
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private JdbcTemplate jdbcTemplate(){
        if( jdbcTemplate == null ){
            jdbcTemplate = new JdbcTemplate( dataSource );
        }

        return jdbcTemplate;
    }

    public void setup( Properties connectionProperties ){
        if( dataSource == null ){
            dataSource = buildDataSource(  connectionProperties );
        }
    }

    private DataSource buildDataSource( Properties connectionProperties ){
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUsername( connectionProperties.getProperty("joblogger.configuration.database.properties.user") );
        driverManagerDataSource.setPassword( connectionProperties.getProperty("joblogger.configuration.database.properties.password") );
        driverManagerDataSource.setDriverClassName( connectionProperties.getProperty("joblogger.configuration.database.properties.driverClassName") );
        driverManagerDataSource.setUrl( connectionProperties.getProperty("joblogger.configuration.database.properties.url") );
        return driverManagerDataSource;
    }

    /**
     *
     * @param event object {@link EventLog}
     * @return
     */
    public boolean store( EventLog event ){
        JdbcTemplate template = jdbcTemplate();
        int rows = template.update("INSERT INTO LOG_EVENT( message,occurredOn, level) values ( ?,?,?)",
                                        ps -> {
                                                ps.setString(1, event.getMessage());
                                                ps.setTimestamp(2, event.getOccurredOn());
                                                ps.setString(3, event.getJobLevel());
                                        });
        return (rows > 0);

    }

    public List<EventLog> list(){
        JdbcTemplate template = jdbcTemplate();
        return template.query("SELECT ID, MESSAGE, LEVEL,OCCURREDON FROM LOG_EVENT",
                                (rs, rowNum) -> new EventLog( rs.getLong(1) ,
                                                              rs.getString(2) ,
                                                              convertToLevels( rs.getString(3) ),
                                                              rs.getTimestamp( 4 )
                                                            ));
    }



}
