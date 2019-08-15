package com.example.logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class EventLog implements Serializable {


    private static final long serialVersionUID = 4218347854333941929L;

    private Long logId;

    private String message;

    private Timestamp occurredOn;

    private String jobLevels;


    public EventLog(String message, List<JobLevel> jobLevels ){
        this( null, message, jobLevels, null );
    }

    public EventLog(Long logId, String message, List<JobLevel> jobLevels, Timestamp occurredOn ){
       valid( message , jobLevels);
       this.logId = logId;
       this.message = message;
       this.jobLevels = StringUtils.join( jobLevels,"|" );
       this.occurredOn = ( occurredOn == null ? Timestamp.from( Instant.now() ): occurredOn );
    }

    private void valid( String message, List<JobLevel> jobLevels){
        Assert.hasText( message , "Message is required");
        Assert.isTrue( !CollectionUtils.isEmpty( jobLevels ), "JobLevel is required");
    }


    public long getLogId() {
        return logId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getOccurredOn() {
        return occurredOn;
    }

    public String getJobLevel() {
        return jobLevels;
    }

    public List<JobLevel> levels(){
        return Util.convertToLevels( jobLevels );
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "logId=" + logId +
                ", message='" + message + '\'' +
                ", occurredOn=" + occurredOn +
                ", jobLevel='" + jobLevels + '\'' +
                '}';
    }
}
