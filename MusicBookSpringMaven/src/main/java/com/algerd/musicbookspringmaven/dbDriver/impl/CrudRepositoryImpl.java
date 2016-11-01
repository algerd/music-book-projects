
package com.algerd.musicbookspringmaven.dbDriver.impl;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import java.util.List;
import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.dbDriver.PrepareLabel;

public abstract class CrudRepositoryImpl<T extends Entity> extends ChangeRepositoryImpl<T> implements CrudRepository<T> {
                      
    @Override
    public void save(T entity) {      
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
        String query = "select " + getColumnString() + " from " + getTableName(); 
        return executeQuery(query, resultSet -> fill(resultSet));
    } 
    
    @Override
    public T selectById(int id) {
        //String prepareQuery =  "select " + getColumnString() + " from " + tableName + " where id = ?";
        //PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, id);
        
        PrepareLabel lpstmt = new PrepareLabelImpl();             
        String prepareQuery = "select " + getColumnString() + " from " + getTableName() + " where id = " + lpstmt.get("id");       
        PrepareSetter prepareSetter = prepareStmt -> {
            lpstmt.init(prepareStmt);    
            lpstmt.set("id", id); 
        };
        List<T> entities = executePrepareQuery(prepareQuery, prepareSetter, resultSet -> fill(resultSet));        
        return entities.isEmpty() ? null : entities.get(0);
    }
    
}
