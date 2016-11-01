
package db.repository.impl;

import db.driver.Entity;
import db.driver.impl.LabeledPreparedStatement;
import db.driver.Labeledable;
import db.driver.PrepareSetter;
import db.driver.impl.Connector;
import db.driver.impl.WrapChangedEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import db.repository.CrudRepository;

public abstract class CrudRepositoryImpl<T extends Entity> extends ChangeRepositoryImpl<T> implements CrudRepository<T> {
         
    public CrudRepositoryImpl(String tableName) {
        super(tableName);
    }  
               
    @Override
    public void save(T entity) {      
        // если сущность ещё не имеет id - добавлять в бд с автоинкрементацией id
        if (selectById(entity.getId()) == null || entity.getId() == 0) { 
            int lastId = insert(entity);
            entity.setId(lastId);           
            setAdded(new WrapChangedEntity<>(selectById(entity.getId()), entity));
        }
        else {
            Entity oldEntity = selectById(entity.getId());        
            update(entity);
            setUpdated(new WrapChangedEntity<>((T) oldEntity, entity));            
        }
    }
   
    @Override
    public void delete(T entity) {
        executePrepareDelete(entity);   
        setDeleted(new WrapChangedEntity<>(selectById(entity.getId()), entity));
    }
    
    @Override
    public List<T> selectAll() {
        String query = "select * from " + tableName; 
        return executeQuery(query, resultSet -> fill(resultSet));
    } 
    
    @Override
    public T selectById(int id) {
        //String prepareQuery = "selectAll * from " + tableName + " where id = ?";
        //PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, id);
        Labeledable lpstmt = new LabeledPreparedStatement();
        String prepareQuery = "select * from " + tableName + " where id = " + lpstmt.get("id");
        PrepareSetter prepareSetter = prepareStmt -> {
            lpstmt.init(prepareStmt);    
            lpstmt.set("id", id); 
        };
        List<T> entities = executePrepareQuery(prepareQuery, prepareSetter, resultSet -> fill(resultSet));        
        return entities.isEmpty() ? null : entities.get(0);
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
            
}
