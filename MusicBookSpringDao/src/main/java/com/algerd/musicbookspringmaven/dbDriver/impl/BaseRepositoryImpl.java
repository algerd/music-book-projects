
package com.algerd.musicbookspringmaven.dbDriver.impl;

import com.algerd.musicbookspringmaven.dbDriver.DbColumn;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;
import com.algerd.musicbookspringmaven.dbDriver.exception.DeleteFailedException;
import com.algerd.musicbookspringmaven.dbDriver.exception.InsertFailedException;
import com.algerd.musicbookspringmaven.dbDriver.exception.UpdateFailedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import com.algerd.musicbookspringmaven.dbDriver.BaseRepository;

public class BaseRepositoryImpl<T extends Entity> implements BaseRepository<T> {
    
    protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
	}

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
    public T getEntity(ResultSet rs) throws SQLException {
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
        String query = "delete from " + tableName + " where id = ?";
		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
			query, new int[] {Types.INTEGER}
        );        
		int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(new Object[] {entity.getId()}));
        if (count != 1) {
			throw new DeleteFailedException("Cannot delete entity");
        }
	}

    @Override
    public int insert(T entity) {
        String prepareStr = "";
        Iterator<DbColumn> columnIterator = columnList.iterator();
        while (columnIterator.hasNext()) {
            if (!columnIterator.next().name().equals("id")) {
                prepareStr += "?";
                if (columnIterator.hasNext()) {
                    prepareStr += ", ";
                }
            }    
        }  
        String query = "insert into " + tableName + " (" + getColumnString(false) + ")  values (" + prepareStr + ")";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            query, getColumnTypes(false, false)
        );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(getFieldValues(entity, false, false)), keyHolder);        
        if (count != 1) {
            throw new InsertFailedException("Cannot insert account");
        }
		entity.setId(keyHolder.getKey().intValue());       
        return entity.getId();
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
        String query = "update " + tableName + " set " + setString + " where id = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            query, getColumnTypes(true, true)
        );
        int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(getFieldValues(entity, true, true)));
        if (count != 1) {
			throw new UpdateFailedException("Cannot update account");
        }
    }
    
    private int[] getColumnTypes(boolean hasId, boolean update) {
        int size = columnList.size();
        if (!hasId) {
            --size;
        }
        if (update) {
            ++size;
        }
        int[] arrTypes = new int[size];
        int i = 0;    
        for (DbColumn dbColumn : columnList) {
            if (hasId || (!hasId && !dbColumn.name().equals("id"))) {
                arrTypes[i] = dbColumn.type();
                ++i;
            }
        }
        if (update) {
            arrTypes[size-1] = Types.INTEGER;  
        }
        return arrTypes;
    }
    
    private Object[] getFieldValues(T entity, boolean hasId, boolean update) {
        int size = columnList.size();
        if (!hasId) {
            --size;
        }
        if (update) {
            ++size;
        }
        Object[] values = new Object[size];
        int i = 0;    
        for (DbColumn dbColumn : columnList) {
            try {
                if (hasId || (!hasId && !dbColumn.name().equals("id"))) {
                    values[i] = dbColumn.getter().invoke(entity);
                    ++i;
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ex.printStackTrace();
            }    
        }
        if (update) {
            values[size-1] = entity.getId(); 
        }
        return values;
    }
      
    @Override
    public boolean isUniqueColumnValue(String column, String value) {
        boolean isUnique = true;
        column = column.trim().toLowerCase();
        String query = "select " + column + " from " + tableName;     
        List<String> results = jdbcTemplate.queryForList(query, String.class);          
        for (String result : results) {
            if (result.trim().toLowerCase().equals(value.trim().toLowerCase())) {
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }  
    
    public String getColumnString(boolean hasId) {
        String columnString = "";
        Iterator<DbColumn> columnIterator = columnList.iterator();
        while (columnIterator.hasNext()) {
            String column = columnIterator.next().name();
            if (!hasId && column.equals("id")) continue;
            columnString += column;
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
