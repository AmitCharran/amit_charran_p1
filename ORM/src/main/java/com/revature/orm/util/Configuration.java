package com.revature.orm.util;

import com.revature.orm.persistence.DAOimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Configuration {
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private List<Metamodel<Class<?>>> metamodelList;
    private DAOimpl dao;

    final static Logger logger = LoggerFactory.getLogger(Configuration.class);

    /**
     * Constructor which initializes the
     * @param url endpoint for DB
     * @param user username for DB
     * @param pass password for DB
     */
    public Configuration(String url, String user, String pass){
        dbURL = url;
        dbUsername = user;
        dbPassword = pass;
        dao = new DAOimpl(dbURL, dbUsername, dbPassword);
    }

    /**
     * Structures class based on annotation
     * @param annotatedClass
     * @return
     */
    public Configuration addAnnotatedClass(Class annotatedClass) {
        if(metamodelList == null) {
            metamodelList = new LinkedList<>();
        }

        // generate a method in metamodel that transforms a class into an appropriate data model to be
        // transposed into a relation db object
        metamodelList.add(Metamodel.of(annotatedClass));

        dao = new DAOimpl(dbURL,dbUsername,dbPassword);
        dao.createTable(annotatedClass);

        return this; // we're returning the
    }


    /**
     * Get all values from SQL table
     * @param clazz current class to identify current SQL table user want to access
     * @return List of current object identified by clazz
     */
    public List<Object> getAll(Class clazz){
        if(!dao.tableExists(clazz.getSimpleName().toLowerCase())){
               logger.warn("Table name " + clazz.getSimpleName().toLowerCase() + " does not exist. Cannot retrieve");
        }
        return dao.getAll(clazz);
    }

    /**
     * Get one values from SQL table by ID
     * @param clazz current class to identify current SQL table user want to access
     * @param id the id of object the user wants to return
     * @return object that represents table
     */
    public Object getById(Class clazz, int id){
        return dao.getById(clazz, id);
    }


    /**
     * Insert values into table identified with clazz
     * @param clazz used to identify the current table
     * @param o set of objects needed to insert.
     */
    public void insertIntoTable(Class clazz, Object ...o){
       dao.insert(clazz, o);
    }

    /**
     * update values into table identified with clazz. Value is identified by Id given in ..o
     * @param clazz used to identify the current table
     * @param o used to set values in current table and identify which row to replace
     */
    public void update(Class clazz, Object ...o){
        dao.update(clazz, o);
    }

    /**
     * update values into table identified with clazz. Value is identified by Id
     * @param clazz used to identify the current table
     * @param id used to identify row
     */
    public void deleteByID(Class clazz,int id){
        dao.removeById(clazz, id);
    }

    /**
     * return column names from table
     * @param clazz used to identify table
     * @return list of column names
     */
    public List<String> getColumnNames(Class clazz){
        return dao.getColumnNames(clazz).orElse(null);
    }

    /**
     * Return the types of all columns to map to current type of class
     * @param clazz identifies current table
     * @return list of class type
     */
    public List<String> getColumnTypes(Class clazz){
        return dao.getAllColumnTypes(clazz).orElse(null);
    }

    /**
     * returns a metamodel instance of list of all current classes initialized by configuration class
     * @return list of metamodels
     */
    public List<Metamodel<Class<?>>> getMetamodels() {
        return (metamodelList == null) ? Collections.emptyList() : metamodelList;
    }

    /**
     * Sets current schema in current database
     * @param schema the shema you want to set
     */
    public void setSchema(String schema){
        ConnectionUtil.setSchema(schema);
    }

    public String getDbURL() {
        return dbURL;
    }

    public void setDbURL(String dbURL) {
        this.dbURL = dbURL;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
