package com.revature.orm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionUtil {
    private static Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

    /**
     * provides a way for us to connect to our database without tediously rewriting code
     * @param url DB endpoint
     * @param user username
     * @param password password
     * @return connection with credentials to access the database
     */
    public static Connection getConnection(String url, String user, String password){
        try{
            connection = DriverManager.getConnection(url,user,password);
        }catch (SQLException e){
            logger.info("Login credentials incorrect \nendpoint: " + url  +"\nuser: " + user);
            logger.warn(e.getMessage());
        }

        return connection;
    }

    /**
     * switches current schema in connection
     * @param schema
     */
    public static void setSchema(String schema){
        if(connection != null){
            try {
                connection.setSchema(schema);
            }catch (SQLException e){
                logger.warn(e.getMessage());
            }
        }
    }

}
