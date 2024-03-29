package com.example.logger;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

public final class JobLoggerMessage {

   private  JobLoggerMessage(){}

    /**
     *
     * @param level
     * @param text
     * @return
     */
    public static String formatText( String level , String text ){
        StringBuilder builder = new StringBuilder();
        builder.append( DateFormat.getDateInstance(DateFormat.LONG).format(Date.from( Instant.now() ) ))
                .append( " | ")
                .append( level )
                .append(" | ")
                .append( text );
        return  builder.toString();
    }



}
