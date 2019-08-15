package com.example.logger;


public enum JobLevel {

    MESSAGE("message"),
    WARNING("warning"),
    ERROR("error");

    private String label;

    JobLevel( String label ){
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static JobLevel labelOf( String label ){

        for( JobLevel e : values()){
            if( e.label.equalsIgnoreCase( label ) ){
                return e;
            }
        }
        return null;
    }

}
