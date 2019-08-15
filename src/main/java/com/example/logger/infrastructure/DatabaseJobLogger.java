package com.example.logger.infrastructure;

import com.example.logger.EventLog;
import com.example.logger.JobLevel;
import com.example.logger.Logger;
import com.example.logger.configuration.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseJobLogger implements Logger {


    private static DatabaseJobLogger instance = null;

    private Configuration configuration;

    public static DatabaseJobLogger getInstance(){
        if( instance == null ){
            instance = new DatabaseJobLogger();
        }
        return instance;
    }


    @Override
    public void logMessage(String message, List<JobLevel> levels) {

        List<JobLevel> filterLevels = levels.stream()
                            .filter( jobLevel -> ( get( jobLevel )!=null ) )
                            .collect(Collectors.toList());

        if( !CollectionUtils.isEmpty( filterLevels )){
            DatabaseHelper.getInstance().store( new EventLog( message, filterLevels ) );
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
        DatabaseHelper.getInstance().setup( configuration.loadProperties() );
    }
}
