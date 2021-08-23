package com.revature.orm.util;

import org.apache.log4j.*;
import org.slf4j.LoggerFactory;


public class LoggingConfig {

    final static Logger logger = (Logger) LoggerFactory.getLogger(LoggingConfig.class);



    public static void main(String[] args){
        /*
        Logging Levels
        TRACE
        DEBUG
        INFO
        WARN
        ERROR
        FATAL
        */

        PatternLayout layout = new PatternLayout("%n%p - %d - %C{1} - %m");
        ConsoleAppender consoleAppender = new ConsoleAppender();

        consoleAppender.setThreshold(Level.TRACE); // can switch to any other level
        consoleAppender.setLayout(layout);
        consoleAppender.activateOptions(); // forgot what this does
        Logger.getRootLogger().addAppender(consoleAppender);

        FileAppender fileAppender = new FileAppender();
        fileAppender.setThreshold(Level.WARN);
        fileAppender.setLayout(layout);
        fileAppender.setFile("src/main/logs/issues.txt");
        fileAppender.setAppend(false);
        fileAppender.activateOptions();
        Logger.getRootLogger().addAppender(fileAppender);


        logger.warn("test");

    }

}
