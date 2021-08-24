package com.revature.orm.persistence;

import com.revature.orm.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Used to connect to our database
 */
public class DAOimpl implements DAO{

    final static Logger logger = LoggerFactory.getLogger(DAOimpl.class);


    private String url;
    private String user;
    private String pass;

    /**
     * constructor with values used for database initialization
     * @param url
     * @param user
     * @param pass
     */
    public DAOimpl(String url, String user, String pass){
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    /**
     * Create table depending on current class
     * @param clazz if annotations are correct, a table is created
     */
    @Override
    public void createTable(Class<?> clazz) {
        Metamodel mm = new Metamodel(clazz);
        if(tableExists(mm.getSimpleClassName())){
            logger.warn("Table name " + mm.getSimpleClassName() + " already exists");
            return;
        }
        createTable(mm);
        IdField id = mm.getPrimaryKey();
        addPrimaryKey(mm, id);
        List<ColumnField> columns = mm.getColumns();
        for(ColumnField c: columns){
            addColumn(mm,c);
        }
    }

    /**
     * Added primary column to table
     * @param mm MetaModel of current table
     * @param id id of current table
     */
    private void addPrimaryKey(Metamodel mm, IdField id){
        HashMap<Type, String> mapTypeToSQLType = new TypeToStringMap().dataTypeToStringConversion();
        String addKey = "ALTER TABLE " + mm.getSimpleClassName()
                + " ADD COLUMN " + id.getName() + " " + mapTypeToSQLType.get(id.getType()) +
                " GENERATED ALWAYS AS IDENTITY";

        Statement s;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            s.execute(addKey);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns column name of current table
     * @param clazz used to identify current table
     * @return String of column names
     */
    public Optional<List<String>> getColumnNames(Class clazz){
        List<String> ans = new ArrayList<>();
        Metamodel mm = new Metamodel(clazz);
        if(!tableExists(mm.getSimpleClassName())){
            return Optional.of(ans);
        }
        String getAllFromTable = "SELECT * FROM " + mm.getSimpleClassName();
        PreparedStatement ps;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
        ps = connection.prepareStatement(getAllFromTable);
        ResultSetMetaData meta = ps.getMetaData();
        for(int i=1; i <= meta.getColumnCount(); i++){
            ans.add(meta.getColumnName(i));
        }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.of(ans);
    }


    /**
     * Return column types from current table
     * @param clazz used to identify current table
     * @return List of column Types
     */
    public Optional<List<String>> getAllColumnTypes(Class clazz){
        List<String> ans = new ArrayList<>();
        Metamodel mm = new Metamodel(clazz);
        if(!tableExists(mm.getSimpleClassName())){
            throw new IllegalStateException("Table does not exist");
        }
        String getAllFromTable = "SELECT * FROM " + mm.getSimpleClassName();
        PreparedStatement ps;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            ps = connection.prepareStatement(getAllFromTable);
            ResultSetMetaData meta = ps.getMetaData();
            for(int i =1; i <= meta.getColumnCount(); i++){
                ans.add(meta.getColumnTypeName(i));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.of(ans);
    }

    /**
     * add column to current table. Table is identified using Metamodel mm
     * @param mm used to identify current table
     * @param c used to identify which column field name to use
     */
    private void addColumn(Metamodel mm, ColumnField c){

        HashMap<Type, String> mapTypeToSQLType = new TypeToStringMap().dataTypeToStringConversion();

        String columnType = "varchar (50)";
        if(mapTypeToSQLType.containsKey(c.getType())){
           columnType = mapTypeToSQLType.get(c.getType());
        }


        String addColumn = "ALTER TABLE " + mm.getSimpleClassName()
                            + " ADD COLUMN " + c.getName() + " " + columnType;

        Statement s;
        try(Connection connection = ConnectionUtil.getConnection(url, user, pass)){
            s = connection.createStatement();
            s.execute(addColumn);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Creates table based on current class
     * @param mm used to get current class
     */
    private void createTable(Metamodel mm){
        String createTable = "CREATE TABLE " + mm.getSimpleClassName() + "()";
        Statement s;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            s.execute(createTable);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * checks if current table exist
     * @param tableName current table
     * @return true or false depending on if table exist
     */
    public boolean tableExists(String tableName){
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)) {
            DatabaseMetaData dbm = connection.getMetaData();

            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next()) {
                return true;
            } else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
            logger.warn("cannot connect to DB");
        }
        logger.info("Cannot connect to DB, so will print table already exists");
        return true; // do not want to create table
    }

    /**
     * Insert values into table identified with clazz
     * @param clazz used to identify the current table
     * @param o set of objects needed to insert.
     */
    @Override
    public void insert(Class<?> clazz, Object ...o) {
        if(!tableExists(clazz.getSimpleName().toLowerCase(Locale.ROOT))){
            logger.warn("table does not exists, table name: " + clazz.getClass().getSimpleName());
            return;
        }

        Metamodel mm = new Metamodel(clazz);
        //List<ColumnField> fields = mm.getColumns();
        Object[] allObjects = o;

        List<String> columnNames = getColumnNames(clazz).get();
        //List<String> columnTypes = getAllColumnTypes(clazz).get();

        if(allObjects.length != columnNames.size() - 1){
            logger.warn("Parameters do not match");
            List<String> colNames = getColumnNames(clazz).get();
            colNames.remove(0);
            logger.warn("Parameters required based on columns." +
                    colNames +"\nDo not include ID");
            return;
        }

        //HashMap<String,Type> reversedMap = new TypeToStringMap().reversedMapStringToDataType();

        String insertString = "INSERT INTO " + mm.getSimpleClassName() + " (";
        String columnNamesFormat = "";
        for(int i = 0; i < columnNames.size(); i++){
            String s = columnNames.get(i);
            if(i == 0){
                continue;
            }else if(i == columnNames.size() - 1){
                columnNamesFormat +=  s + ")";
            }else{
                columnNamesFormat += s +", ";
            }
        }

        insertString += columnNamesFormat + " values (";


        columnNamesFormat = "";
        for(int i =0; i < allObjects.length;i++){
            if(i == allObjects.length -1){
                columnNamesFormat += "\'" +  allObjects[i] + "\')";
            }else{
                columnNamesFormat += "\'" + allObjects[i] + "\', ";
            }
        }
        insertString += columnNamesFormat;
        Statement s;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            s.execute(insertString);


        }catch (SQLException e){
            logger.warn("Cannot connect to Database for insertion to table " + clazz.getSimpleName());
        }
    }

    /**
     * Get one values from SQL table by ID
     * @param clazz current class to identify current SQL table user want to access
     * @param id the id of object the user wants to return
     * @return object that represents table
     */
    @Override
    public Object getById(Class clazz, int id) {
        if(!hasNoArgConstructor(clazz)){
            logger.warn("Class does not have no Arg constructor. Cannot continue" +
                    "\nclass name: " + clazz.getSimpleName());
            return null;
        }
        Metamodel mm = new Metamodel(clazz);


        List<String> allColumnNames = getColumnNames(clazz).get();
        List<Method> unsortedSet= mm.getSetMethodsUnsorted();

        List<Method> sortedSet = sortUnsortedSet(allColumnNames, unsortedSet);

        ResultSet rs;
        Statement s;
        String selectAll = "SELECT * FROM " + mm.getSimpleClassName() + " WHERE " + allColumnNames.get(0) + " = " + id;
        Object o = null;

        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            rs = s.executeQuery(selectAll);



            while(rs.next()){
                o = clazz.newInstance();
                for(int i = 0; i < allColumnNames.size(); i++) {
                    // make sure type is correct
                    Object oToSet = rs.getObject(allColumnNames.get(i));
                    sortedSet.get(i).invoke(o, oToSet);
                    // set object and make sure it is the right type
                }
            }

        }catch (SQLException e){
            logger.warn("Cannot create object instance from table " + mm.getSimpleClassName());
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return o;

    }

    /**
     * Get all values from SQL table
     * @param clazz current class to identify current SQL table user want to access
     * @return List of current object identified by clazz
     */
    @Override
    public List<Object> getAll(Class clazz) {
        if(!hasNoArgConstructor(clazz)){
            logger.warn("Class does not have no Arg constructor. Cannot continue" +
                    "\nclass name: " + clazz.getSimpleName());
            return null;
        }

        Metamodel mm = new Metamodel(clazz);

        String selectAll = "SELECT * FROM " + mm.getSimpleClassName();
        List<String> allColumnNames = getColumnNames(clazz).get();
        List<Method> unsortedSet= mm.getSetMethodsUnsorted();

        List<Method> sortedSet = sortUnsortedSet(allColumnNames, unsortedSet);

        ResultSet rs;
        Statement s;
        ArrayList<Object> allObject = new ArrayList<>();

        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            rs = s.executeQuery(selectAll);

            Object o = null;

            while(rs.next()){
                o = clazz.newInstance();
                for(int i = 0; i < allColumnNames.size(); i++) {
                    // make sure type is correct
                    Object oToSet = rs.getObject(allColumnNames.get(i));
                    sortedSet.get(i).invoke(o, oToSet);
                    // set object and make sure it is the right type
                }
                allObject.add(o);
            }

        }catch (SQLException e){
            logger.warn("Cannot create object instance from table " + mm.getSimpleClassName());
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return allObject;
    }

    /**
     * update values into table identified with clazz. Value is identified by Id given in ..o
     * @param clazz used to identify the current table
     * @param o used to set values in current table and identify which row to replace
     */
    @Override
    public void update(Class clazz, Object... o) {
        if(!tableExists(clazz.getSimpleName().toLowerCase(Locale.ROOT))){
            logger.warn("table does not exists, table name: " + clazz.getClass().getSimpleName());
        }

        Metamodel mm = new Metamodel(clazz);
        List<ColumnField> fields = mm.getColumns();
        Object[] allObjects = o;

        List<String> columnNames = getColumnNames(clazz).get();

        if(allObjects.length != columnNames.size()){
            logger.warn("Parameters do not match");
            List<String> colNames = getColumnNames(clazz).get();
            colNames.remove(0);
            logger.warn("Parameters required based on columns." +
                    colNames +"\nDo not include ID");
            return;
        }

        String updateString = "UPDATE " + mm.getSimpleClassName() + " SET ";
        String setPortionOfUpdateString = "";
        for(int i = 1; i < columnNames.size(); i++){
            if(i == columnNames.size() - 1){
                setPortionOfUpdateString += columnNames.get(i) + " = \'" + allObjects[i] + "\' WHERE ";
            }else{
                setPortionOfUpdateString += columnNames.get(i) + " = \'" + allObjects[i] + "\', ";
            }
        }

        updateString += setPortionOfUpdateString;
        updateString += columnNames.get(0) + " = \'" + allObjects[0] +"\'";

        Statement s;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            s.execute(updateString);


        }catch (SQLException e){
            logger.warn("cannot update table: " + clazz.getSimpleName());
            return;
        }


    }

    /**
     * update values into table identified with clazz. Value is identified by Id
     * @param clazz used to identify the current table
     * @param id used to identify row
     */
    @Override
    public void removeById(Class clazz, int id) {
        Metamodel mm = new Metamodel(clazz);
        List<String> columnNames = getColumnNames(clazz).get();

        String delete = "DELETE FROM " + mm.getSimpleClassName()
                + " WHERE " + columnNames.get(0) + " = " + id;

        Statement s;
        try(Connection connection = ConnectionUtil.getConnection(url,user,pass)){
            s = connection.createStatement();
            s.execute(delete);

        }catch (SQLException e){
            logger.warn("Cannot Delete " + id + " from table " + mm.getSimpleClassName());
        }


    }

    /**
     * When sorted set is retrieved, this method will sort it depending on table columns
     * @param allColumnNames a list of all column names
     * @param unsortedSet An unsorted list of setter methods
     * @return
     */
    private List<Method> sortUnsortedSet(List<String> allColumnNames, List<Method> unsortedSet) {
        List<Method> sortedSet = new ArrayList<>();
        for(int i =0; i < allColumnNames.size(); i++){
            String colName = allColumnNames.get(i);
            for(int j = 0; j < unsortedSet.size(); j++){
                String methName= unsortedSet.get(j).getName().toLowerCase(Locale.ROOT);
                if(methName.contains(colName)){
                    sortedSet.add(unsortedSet.get(j));
                    break;
                }
            }
        }
        return sortedSet;
    }

    /**
     * check if there is a no argument constructor is class
     * @param clazz class we are checking the current method in
     * @return true or false
     */
    private boolean hasNoArgConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
                .anyMatch((c) -> c.getParameterCount() == 0);
    }


}
