
package db.driver;

import db.entity.Entity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ExecTable<T extends Entity> {
    
    protected final String tableName; 
    // список имён колонок без первичной(id)
    protected final List<String> columns = new ArrayList<>();
    
    public ExecTable(String tableName) {
        this.tableName = tableName;
        setColumns();  
    }
  
    public void fillPreparedStatement(T entity, PreparedStatement pstmt, boolean update) throws SQLException {
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
     
    /*
    // only for ASCI2 characters
    public boolean isUniqueName(String name) {                     
        String prepareQuery = "select count(id) from " + tableName + " where lower(name) = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setString(1, name.trim().toLowerCase());
        return fetchIntRow(prepareQuery, prepareSetter) > 0;             
    }
    */
    public boolean isUniqueColumnValue(String column, String value) {
        boolean isUnique = true;
        column = column.trim().toLowerCase();
        String query = "select " + column + " from " + tableName;
        try {
            ResultSet resultSet = Connector.getInstance().connect().createStatement().executeQuery(query);
            while (resultSet.next()) {
                if (resultSet.getString(1).trim().toLowerCase().equals(value.trim().toLowerCase())) {
                    isUnique = false;
                    break;
                }
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
        return isUnique;
    }
    
    protected int insert(T entity) {       
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
    
    protected void update(T entity) {       
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
    
    /**
     * Удалить запись с индексом id из таблицы tableName, используя PreparedStatement.
     * @param id Индекс удаляемой записи
     */
    protected void executePrepareDelete(T entity) {
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

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }
    
}
