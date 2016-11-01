
package com.algerd.musicbookspringmaven.dbDriver.impl;

import com.algerd.musicbookspringmaven.dbDriver.DbColumn;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;
import com.algerd.musicbookspringmaven.dbDriver.BaseRepository;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseRepositoryImpl<T extends Entity> extends ExecRepositoryImpl<T> implements BaseRepository<T> {

    private Class<T> entityType;
    private String tableName; 
    private final List<DbColumn> columnList = new ArrayList<>();
    
    public BaseRepositoryImpl() {
        super();
        setEntityType();
        setTableName();
        setColumns();
    }
       
    private void setEntityType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType)) {
            genericSuperclass = ((Class) genericSuperclass).getGenericSuperclass();
        }
        Type[] arguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        entityType = (Class<T>) arguments[0];
    }
    
    private void setTableName() {           
        Table table = entityType.getAnnotation(Table.class);
        tableName = (table == null || table.value().equals("")) ?
            entityType.getSimpleName().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase() :
            table.value();    
    }

    private void setColumns() {   
        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {            
            DbColumn dbColumn = DbColumn.create(field);
            if (dbColumn != null) {
                columnList.add(dbColumn);
            }
        }        
    }
              
    @Override
    public T fill(ResultSet rs) throws SQLException {
        try {
            T entity = entityType.newInstance();             
            for (DbColumn dbColumn : columnList) {
                Method setter = dbColumn.setter();
                Class<?>[] paramTypes = setter.getParameterTypes();
                if (paramTypes[0].equals(String.class)) {                         
                    setter.invoke(entity, rs.getString(dbColumn.name()));                         
                } else {
                    setter.invoke(entity, rs.getInt(dbColumn.name())); 
                }
            }
            return entity;          
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
        }
        return null;       
    }
    
    @Override
    public void executePrepareDelete(T entity) {
        String SQL = "delete from " + tableName + " where id = ?";
        executePrepareUpdate(SQL, pstmt -> pstmt.setInt(1, entity.getId()));
    }
    
    @Override
    public int insert(T entity) {  
        String prepareStr = "";  
        for (DbColumn dbColumn : columnList) {
            if (!dbColumn.name().equals("id")) {
                prepareStr += ", ?";
            }
        }   
        String SQL = "insert into " + tableName + " (" + getColumnString() + ")  values (null" + prepareStr + ")";      
        return executePrepareInsert(SQL, pstmt -> fillPreparedStatement(entity, pstmt, false));      
    }
    
    @Override
    public void update(T entity) {        
        String setString = "";
        Iterator<DbColumn> columnIterator = columnList.iterator();
        while (columnIterator.hasNext()) {
            setString += columnIterator.next().name() + " = ?";
            if (columnIterator.hasNext()) {
                setString += ", ";
            }
        }      
        String SQL = "update " + tableName + " set " + setString + " where id = ?";
        executePrepareUpdate(SQL, pstmt -> fillPreparedStatement(entity, pstmt, true));
    }
    
    /*
    // only for ASCI2 characters
    public boolean isUniqueName(String name) {                     
        String prepareQuery = "select count(id) from " + tableName + " where lower(name) = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setString(1, name.trim().toLowerCase());
        return fetchIntRow(prepareQuery, prepareSetter) > 0;             
    }
    */
    @Override
    public boolean isUniqueColumnValue(String column, String value) {
        boolean isUnique = true;
        column = column.trim().toLowerCase();
        String query = "select " + column + " from " + tableName;       
        List<String> results = fetchColumnList(query);
        for (String result : results) {
            if (result.trim().toLowerCase().equals(value.trim().toLowerCase())) {
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }  
    
    private void fillPreparedStatement(T entity, PreparedStatement pstmt, boolean update) throws SQLException {
        try {
            Class entityClass = entity.getClass();           
            int index = 1;                    
            for (DbColumn dbColumn : columnList) {
                if (update || !dbColumn.name().equals("id")) {
                    Method method = dbColumn.getter();
                    Class returnType = method.getReturnType();
                    if (returnType.equals(String.class)) {
                        pstmt.setString(index, (String) method.invoke(entity));
                    }
                    else {
                        pstmt.setInt(index, (int) method.invoke(entity));
                    }              
                    ++index;
                }
            }           
            if (update) {
                pstmt.setInt(index, entity.getId());
            }
        }
		catch (IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}       
    }
    
    public String getColumnString() {
        String columnString = "";
        Iterator<DbColumn> columnIterator = columnList.iterator();
        while (columnIterator.hasNext()) {
            columnString += columnIterator.next().name();
            if (columnIterator.hasNext()) {
                columnString += ", ";
            }
        }  
        return columnString;
    } 

    public String getTableName() {
        return tableName;
    }
}
