package com.example.logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLoggerOld {

    private static boolean logToFile;

    private static boolean logToConsole;

    private static boolean logMessage;

    private static boolean logWarning;

    private static boolean logError;

    private static boolean logToDatabase;

    //variable no usada
    private boolean initialized;

    private static Map dbParams;

    private static Logger logger;

    //Los valores a fields staticos no deberian ser asignados en el constructor
    public JobLoggerOld(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
                        boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {

        System.out.print( " JobLogger instance created... " );

        logger = Logger.getLogger("MyLog");

        logError = logErrorParam;

        logMessage = logMessageParam;

        logWarning = logWarningParam;

        logToDatabase = logToDatabaseParam;

        logToFile = logToFileParam;

        logToConsole = logToConsoleParam;

        dbParams = dbParamsMap;

    }

    //REDUCIR LA COMPLEJIDAD COGNITICVA PARA UN MEJOR ENTENDIMIENTO Y EJECUCION DEL CODIGO
    //EL NOMBRE DEL METODO NO CUMPLE LA CONVENCION PARA NOMBRES DE METODO JAVA (PRIMERA LETRA EN MINUSCULA )
    //Usar una excepcion especifica en lugar de una generica
    public static void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {

        //Posible NullPointerException
        //En caso de no ser nulo, el trim() genera una nueva cadena que no esta siendo asignada
        messageText.trim();//NO SE USA EL RESULT DE TRIM POSIBLE NULL

        if (messageText == null || messageText.length() == 0) {
            return;
        }

        //Usar una excepcion especifica en lugar de una generica
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration");

        }

        //Usar una excepcion especifica en lugar de una generica
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or JobLoggerMessage must be specified");
        }

        // variable inicializada en null de forma redundante
        Connection connection = null;

        Properties connectionProps = new Properties();

        connectionProps.put("user", dbParams.get("userName"));
        connectionProps.put("password", dbParams.get("password"));

        logger.info(connectionProps.toString() );

        //Creacion de una conexion sin cerrar , se deberia manejar un pool de conexiones
        //Esta conexion deberia crearse siempre y cuando se haya habilitado el log a base de datos
        connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName") + ":" + dbParams.get("portNumber") + "/", connectionProps);

        int t = 0;

        if (message && logMessage) {

            t = 1;

        }

        if (error && logError) {

            t = 2;

        }

        if (warning && logWarning) {

            t = 3;

        }
        //USAR TRY-WITH-RESOURCES para administrar streams abiertos y puedan cerrarse
        Statement stmt = connection.createStatement();

        String l = null;//no deberia inicializarse en null , sino en cadena vacia

        //El literal logFileFolder se repite 3 veces deberia crearse un constante
        File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");

        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");

        ConsoleHandler ch = new ConsoleHandler();

        if (error && logError) {
            l = l + " error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (warning && logWarning) {
            l = l + " warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (message && logMessage) {
            l = l + " message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if(logToFile) {

            logger.addHandler(fh);
            logger.log(Level.INFO, l);

        }

        if(logToConsole) {

            logger.addHandler(ch);
            logger.log(Level.INFO, l);

        }

        if(logToDatabase) {
            //falta insertar el mensaje en la base datos
            //No se esta cerrando el statement
            stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")");

        }

    }

}