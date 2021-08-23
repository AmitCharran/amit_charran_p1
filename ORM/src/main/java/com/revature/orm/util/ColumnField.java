package com.revature.orm.util;

import java.lang.reflect.Field;
import com.revature.orm.annotations.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a class that is used to interact with Type Field from reflections,
 * Used for my ORM to make sure the current field is marked with an annotation
 */
public class ColumnField {
    /**
     * Type Field. Holds the current field
     */
    private Field field;
    private static final Logger logger = LoggerFactory.getLogger(ColumnField.class);
    /**
     * Constructor that make sure the current field has the annotation "Column"
     * @param field From Reflections, Type Field
     */
    public ColumnField(Field field){
        if(field.getAnnotation(Column.class) == null){
            logger.warn("Cannot create ColumnField object!" +
                "Provided field, " + getName() + " is not annotated with @Column");

            throw new IllegalStateException("Cannot create ColumnField object!" +
                    "Provided field, " + getName() + " is not annotated with @Column");
        }
        this.field = field;
    }

    /**
     *  returns name of current field
     * @return String
     */
    public String getName() {return field.getName();}
    /**
     * returns type of current field
     * @return <Class<?>>
     */
    public Class<?> getType(){return field.getType();}
    /**
     * Returns annotation columnsName of current field
     * @return String that represents annotation columnName
     */
    public String getColumnName(){
        return field.getAnnotation(Column.class).columnName();
    }

    public Field getField() {
        return field;
    }
}
