
package db.repository.impl;

import db.driver.Entity;
import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.driver.impl.Connector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import db.repository.ExecRepository;

public abstract class ExecRepositoryImpl<T extends Entity> implements ExecRepository<T> {
    
    private final Class<T> entityType;
    protected final String tableName; 
    // список имён колонок без первичной(id)
    protected final List<String> columns = new ArrayList<>();
       
    @SuppressWarnings("unchecked")
    public ExecRepositoryImpl(String tableName) {
        this.tableName = tableName;
        setColumns(); 
        
        // находим entityType
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType)) {
            genericSuperclass = ((Class) genericSuperclass).getGenericSuperclass();
        }
        Type[] arguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        entityType = (Class<T>) arguments[0];
    }
    
 
    @Override
    public T fill(ResultSet rs) throws SQLException {
        try {
            T entity = entityType.newInstance(); 
            entity.setId(rs.getInt("id"));
            
            for (String column : columns) {
                String setMethodName = "set" + Character.toString(column.charAt(0)).toUpperCase() + column.substring(1);                              
                Method[] methods = entityType.getDeclaredMethods();
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.equals(setMethodName)) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes[0].equals(String.class)) {
                            method.invoke(entity, rs.getString(column));
                        } else {
                            method.invoke(entity, rs.getInt(column)); 
                        }
                    }
                }
            }
            return entity;
        }
        catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    @Override
    public List<T> executePrepareQuery(String prepareQuery, PrepareSetter prepareSetter, Extractor<ResultSet, Entity> callback) {
        List<T> entities = new ArrayList<>();   
        try {
            PreparedStatement pstmt = Connector.getInstance().connect().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            ResultSet resultSet = pstmt.executeQuery();  
            while (resultSet.next()) {
                entities.add((T) callback.extract(resultSet));
            }              
            Connector.getInstance().commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
        return entities;
    }
      
    @Override
    public List<T> executeQuery(String query, Extractor<ResultSet, Entity> callback) {
        List<T> entities = new ArrayList<>();     
        try {                        
			ResultSet resultSet = Connector.getInstance().connect().createStatement().executeQuery(query);           
            while (resultSet.next()) {
                entities.add((T) callback.extract(resultSet));
            }                             
            Connector.getInstance().commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
        return entities;     
    }
    
    @Override
    public void executePrepareUpdate(String prepareQuery, PrepareSetter prepareSetter) {       
		try {
            PreparedStatement pstmt = Connector.getInstance().connect().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            pstmt.executeUpdate();
            Connector.getInstance().commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
    }
    
    @Override
    public void executeUpdate(String query) {
        try {                        
			Connector.getInstance().connect().createStatement().executeUpdate(query);                                                  
            Connector.getInstance().commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
    }
    
    @Override
    public int fetchIntRow(String prepareQuery, PrepareSetter prepareSetter) {   
        try {
            PreparedStatement pstmt = Connector.getInstance().connect().prepareStatement(prepareQuery);
            prepareSetter.set(pstmt);
            ResultSet resultSet = pstmt.executeQuery();  
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }              
            Connector.getInstance().commit();
        }  
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
        return 0;     
    }
     
    @Override
    public void executePrepareDelete(T entity) {
        String SQL = "delete from " + tableName + " where id = ?";
		try {
            PreparedStatement pstmt = Connector.getInstance().connect().prepareStatement(SQL);
            pstmt.setInt(1, entity.getId());
            pstmt.executeUpdate();
            Connector.getInstance().commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
    }
    
    @Override
    public int insert(T entity) {       
        String prepareStr = "";  
        for (String column : columns) {
            prepareStr += ", ?"; 
        }
        
        String SQL = "insert into " + tableName + " values (null" + prepareStr + ")";
        int lastId = 0;
		try {
            PreparedStatement pstmt = Connector.getInstance().connect().prepareStatement(SQL);
            fillPreparedStatement(entity, pstmt, false);
            pstmt.executeUpdate();
            lastId = Connector.getInstance().getGeneratedKey();
            Connector.getInstance().commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
        return lastId;
    }
    
    @Override
    public void update(T entity) {       
        String setString = "";
        Iterator<String> columnsIterator = columns.iterator();
        while (columnsIterator.hasNext()) {
            setString += columnsIterator.next() + " = ?";
            if (columnsIterator.hasNext()) {
                setString += ", ";
            }
        }
        String SQL = "update " + tableName + " set " + setString + " where id = ?";
		try {
            PreparedStatement pstmt = Connector.getInstance().connect().prepareStatement(SQL);
            fillPreparedStatement(entity, pstmt, true);
            pstmt.executeUpdate();
            Connector.getInstance().commit();
        }    
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
    }
    
    private void fillPreparedStatement(T entity, PreparedStatement pstmt, boolean update) throws SQLException {
        try {
            Class entityClass = entity.getClass();           
            int index = 1;
            for (String column : columns) {
                String methodName = "get" + Character.toString(column.charAt(0)).toUpperCase() + column.substring(1);                                             
                Method method = entityClass.getMethod(methodName);
                Class returnType = method.getReturnType();
                if (returnType.equals(String.class)) {
                    pstmt.setString(index, (String) method.invoke(entity));
                }
                else {
                    pstmt.setInt(index, (int) method.invoke(entity));
                }              
                ++index;
            }
            if (update) pstmt.setInt(index, entity.getId());
        }
		catch (IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}       
    }
    
    /**
     * Получить список названий полей таблицы tableName без первичного поля id.
     * @param tableName Имя таблицы
     * @return Список String имён колонок без первичной(id)
     */
    private void setColumns() {   
        String SQL = "select * from " + tableName;     
        try {                        
			ResultSet rs = Connector.getInstance().connect().createStatement().executeQuery(SQL);           
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 2; i <= columnCount; i++) {
                columns.add(rsmd.getColumnName(i).toLowerCase());        
			}
            Connector.getInstance().commit();
        }
        catch (SQLException e) {
			e.printStackTrace();
			Connector.getInstance().rollback();			
		}
		finally {
			Connector.getInstance().close();
		}
    }
  
}
