
package db.table;

import db.driver.ExecTable;
import db.driver.LabeledPreparedStatement;
import db.driver.PrepareSetter;
import db.entity.Entity;
import db.entity.WrapChangedEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class Table<T extends Entity> extends ExecTable<T> {
    
    private final Class<T> entityType;
    protected final ObjectProperty<WrapChangedEntity<T>> added = new SimpleObjectProperty<>();
    protected final ObjectProperty<WrapChangedEntity<T>> updated = new SimpleObjectProperty<>();    
    protected final ObjectProperty<WrapChangedEntity<T>> deleted = new SimpleObjectProperty<>();    
         
    public Table(String tableName, Class<T> type) {
        super(tableName);
        this.entityType = type;
    }  
       
    @SuppressWarnings("unchecked")
    public Table(String tableName) {
        super(tableName);
        
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (!(genericSuperclass instanceof ParameterizedType)) {
            genericSuperclass = ((Class) genericSuperclass).getGenericSuperclass();
        }
        Type[] arguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        entityType = (Class<T>) arguments[0];
    }
    
    public Class<T> getEntityType() {
         return this.entityType;
    }
    
    /**
     * Извлечь данные из ResultSet и создать на основе их новую сущность.
     */
    public T fill(ResultSet rs) throws SQLException {
        try {
            T entity = getEntityType().newInstance(); 
            entity.setId(rs.getInt("id"));
            
            for (String column : columns) {
                String setMethodName = "set" + Character.toString(column.charAt(0)).toUpperCase() + column.substring(1);                              
                Method[] methods = getEntityType().getDeclaredMethods();
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
    
    /**
     * Добавить сущность в бд и в таблицу, если её ещё нет в таблице,
     * в противном случае обновить сущность в бд.
     */
    public void save(T entity) {
        // если сущность ещё не имеет id - добавлять в бд с автоинкрементацией id
        if (select(entity.getId()) == null || entity.getId() == 0) { 
            int lastId = insert(entity);
            entity.setId(lastId);           
            setAdded(new WrapChangedEntity<>(select(entity.getId()), entity));
        }
        else {
            Entity oldEntity = select(entity.getId());        
            update(entity);
            setUpdated(new WrapChangedEntity<>((T) oldEntity, entity));            
        }
    }
   
    /**
     * Удалить сущность из бд и из таблицы.
     */
    public void delete(T entity) {
        executePrepareDelete(entity);   
        setDeleted(new WrapChangedEntity<>(select(entity.getId()), entity));
    }
    
    /**
     * Извлечь все записи из бд и вернуть список.
     */
    public List<T> select() {
        String query = "select * from " + tableName; 
        return executeQuery(query, resultSet -> fill(resultSet));
    } 
    
    /**
     * Извлечь запись из бд c id и вернуть сущность T.
     */
    public T select(int id) {
        //String prepareQuery = "select * from " + tableName + " where id = ?";
        //PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, id);
        LabeledPreparedStatement lpstmt = new LabeledPreparedStatement();
        String prepareQuery = "select * from " + tableName + " where id = " + lpstmt.get("id");
        PrepareSetter prepareSetter = prepareStmt -> {
            lpstmt.setPreparedStatement(prepareStmt);    
            lpstmt.set("id", id); 
        };
        List<T> entities = executePrepareQuery(prepareQuery, prepareSetter, resultSet -> fill(resultSet));        
        return entities.isEmpty() ? null : entities.get(0);
    }
    
    public WrapChangedEntity<T> getUpdated() {
        return updated.get();
    }
    public void setUpdated(WrapChangedEntity<T> value) {
        updated.set(value);
    }
    public ObjectProperty updatedProperty() {
        return updated;
    }
    
    public WrapChangedEntity getDeleted() {
        return deleted.get();
    }
    public void setDeleted(WrapChangedEntity value) {
        deleted.set(value);
    }
    public ObjectProperty deletedProperty() {
        return deleted;
    }
          
    public WrapChangedEntity getAdded() {
        return added.get();
    }
    public void setAdded(WrapChangedEntity value) {
        added.set(value);
    }
    public ObjectProperty addedProperty() {
        return added;
    }     
         
}
