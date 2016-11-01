
package com.algerd.musicbookspringmaven.dbDriver;

import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Types;

public class DbColumn {

    private String name;
    private final Field field;
    private int type;
    private Method setter;
    private Method getter;
       
    public static DbColumn create(Field field) {
        return (field.getAnnotation(Column.class) != null) ? new DbColumn(field) : null;
    }
    
    private DbColumn(Field field) {
        this.field = field;
        setName();
        setMethods();      
    }
    
    private void setName() {
        Column column = field.getAnnotation(Column.class);       
        name = (column.value().equals("")) ?
            field.getName().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase() :
            column.value();
    } 
    
    private void setMethods() {
        String upperFieldStr = Character.toString(field.getName().charAt(0)).toUpperCase() + field.getName().substring(1);
        String setMethod = "set" + upperFieldStr;
        String getMethod = "get" + upperFieldStr; 
        Method[] methods = field.getDeclaringClass().getDeclaredMethods();
         for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.equals(setMethod)) {
                setter = method;
            }
            if (methodName.equals(getMethod)) {
                getter = method;               
                Class returnType = method.getReturnType();
                if (returnType.equals(String.class)) {
                    type = Types.VARCHAR;
                }
                else if (returnType.equals(Integer.class)) {
                    type = Types.INTEGER;
                }  
                // TODO: дополнить типы
            }
        } 
    }

    public String name() {
        return name;
    }

    public Field field() {
        return field;
    }

    public int type() {
        return type;
    }
    
    public Method setter() {
        return setter;
    }

    public Method getter() {
        return getter;
    }

    @Override
    public String toString() {
        return "DbColumn{" + "name=" + name + ", field=" + field + ", setter=" + setter + ", getter=" + getter + '}';
    }
      
}
