package com.revature.orm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
/**
 * Creates a class that is used to interact with Type Field from reflections,
 * Used for my ORM to make sure the current field is marked with an annotation
 */
public class ForeignKeyField {
     /**
     * Type Field. Holds the current field
     */
    private Field field;
    private static final Logger logger = LoggerFactory.getLogger(ForeignKeyField.class);

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


}
