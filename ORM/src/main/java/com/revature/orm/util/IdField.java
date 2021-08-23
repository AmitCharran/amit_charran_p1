package com.revature.orm.util;

import com.revature.orm.annotations.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Creates a class that is used to interact with Type Field from reflections,
 * Used for my ORM to make sure the current field is marked with an annotation
 */
public class IdField {
    /**
     * Type Field. Holds the current field
     */
    private Field field;
    private static final Logger logger = LoggerFactory.getLogger(IdField.class);

    /**
     * Constructor that make sure the current field has the annotation "Id"
     * @param field From Reflections, Type Field
     */
    public IdField(Field field){
        if(field.getAnnotation(Id.class) == null){
            logger.warn("Cannot create IdField object! Provided field, "
                    + getName() + "is not annotated with @Id");
            throw new IllegalStateException("Cannot create IdField object! Provided field, "
                    + getName() + "is not annotated with @Id");

        }
        this.field = field;
    }

    /**
     *  returns name of current field
     * @return String
     */
    public String getName() {
        return field.getName();
    }

    /**
     * returns type of current field
     * @return <Class<?>>
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * Returns annotation columnsName of current field
     * @return String that represents annotation columnName
     */
    public String getColumnName() {
        return field.getAnnotation(Id.class).columnName();
    }

}
