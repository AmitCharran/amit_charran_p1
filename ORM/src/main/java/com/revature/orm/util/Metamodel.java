package com.revature.orm.util;

import com.revature.orm.annotations.Column;
import com.revature.orm.annotations.Entity;
import com.revature.orm.annotations.Id;
import com.revature.orm.annotations.JoinColumn;
import com.revature.orm.persistence.DAOimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

/**
 * Makes sure annotations on classes are correct
 * so classes be made into tables
 * @param <T> ***Not sure about this right now*** change later***
 */
public class Metamodel<T> {
    private Class<T> clazz;
    private IdField primaryKeyField;
    private List<ColumnField> columnFields;
    private List<ForeignKeyField> foreignKeyFields;



    private static final Logger logger = LoggerFactory.getLogger(Metamodel.class);

    /**
     * checks if class has Entity annotation
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Metamodel<T> of(Class<T> clazz) {
        if (clazz.getAnnotation(Entity.class) == null) {
            throw new IllegalStateException("Cannot create Metamodel object! Provided class, " + clazz.getName() + " is not annotated with @Entity");
        }
        return new Metamodel<>(clazz);
    }

    /**
     * Constructor
     * initializes column fields and foreign key fields for class
     * @param clazz
     */
    public Metamodel(Class<T> clazz){
        this.clazz = clazz;
        this.columnFields = new LinkedList<>();
        this.foreignKeyFields = new LinkedList<>();
    }

    public String getClassName() {
        return clazz.getName().toLowerCase(Locale.ROOT);
    }

    public String getSimpleClassName() {
        return clazz.getSimpleName().toLowerCase(Locale.ROOT);
    }

    /**
     * Look through all the fields and identifies primary key
     * @return field with id annotation
     */
    public IdField getPrimaryKey(){

        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            Id primaryKey = field.getAnnotation(Id.class);
            if(primaryKey != null){
                return new IdField(field);
            }
        }
        logger.warn("Did not find a field annotated with @Id in: " + clazz.getName());
        throw new RuntimeException();
    }

    /**
     * Looks through all fields and returns a list of those with column annotation
     * @return List of fields with column annotation
     */
    public List<ColumnField> getColumns() {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columnFields.add(new ColumnField(field));
            }
        }

        if (columnFields.isEmpty()) {
            logger.warn("No columns found in: " + clazz.getName());
            throw new RuntimeException();
        }

        return columnFields;
    }

    /**
     * Looks through fields and returns a list of those with JoinColumn annotation
     * @return List of fields with JoinColumn annotation
     */
    public List<ForeignKeyField> getForeignKeys() {

        List<ForeignKeyField> foreignKeyFields = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {

            JoinColumn column = field.getAnnotation(JoinColumn.class);

            if (column != null) {
                foreignKeyFields.add(new ForeignKeyField(field));
            }
        }

        return foreignKeyFields;
    }

    /**
     * returns all methods from current class
     * @return List of Methods
     */
    public List<Method> getDeclaredMethods(){
        List<Method> ans = new ArrayList<>();
        Method[] methods = this.clazz.getDeclaredMethods();
        for(Method m: methods){
            ans.add(m);
        }
        return ans;
    }

    /**
     * Returns a list of set methods
     * @return an unsorted list of set methods
     */
    public List<Method> getSetMethodsUnsorted(){
        Method[] methods = this.clazz.getDeclaredMethods();
        Stream<Method> setMethods = Arrays.stream(methods)
                .filter(m -> m.getName().contains("set"));

        List<Method> unsorted = new ArrayList<>();
        setMethods.forEach(s -> unsorted.add(s));
        return unsorted;
    }


}
