package com.example.logger;


import java.util.ArrayList;
import java.util.List;

public final class Util {

    private Util(){}

    public static List<JobLevel> convertToLevels( String values ){

        String[] types = values.split("\\|");

        List<JobLevel> levels = new ArrayList<>();

        for( String type : types ){
            levels.add( JobLevel.labelOf( type ) );
        }
        return levels;
    }

}
