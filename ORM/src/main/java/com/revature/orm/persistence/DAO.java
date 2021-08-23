package com.revature.orm.persistence;

import java.util.List;

public interface DAO {
    /**
     * Create table depending on current class
     * @param clazz if annotations are correct, a table is created
     */
    public void createTable(Class<?> clazz);
    /**
     * Insert values into table identified with clazz
     * @param clazz used to identify the current table
     * @param o set of objects needed to insert.
     */
    public void insert(Class<?> clazz, Object ...o);

    /**
     * Get one values from SQL table by ID
     * @param clazz current class to identify current SQL table user want to access
     * @param id the id of object the user wants to return
     * @return object that represents table
     */
    public Object getById(Class clazz, int id);
    /**
     * Get all values from SQL table
     * @param clazz current class to identify current SQL table user want to access
     * @return List of current object identified by clazz
     */
    public List<Object> getAll(Class clazz);


    /**
     * update values into table identified with clazz. Value is identified by Id given in ..o
     * @param clazz used to identify the current table
     * @param o used to set values in current table and identify which row to replace
     */
    public void update(Class clazz, Object ...o);


    /**
     * update values into table identified with clazz. Value is identified by Id
     * @param clazz used to identify the current table
     * @param id used to identify row
     */
    public void removeById(Class clazz, int id);


}
