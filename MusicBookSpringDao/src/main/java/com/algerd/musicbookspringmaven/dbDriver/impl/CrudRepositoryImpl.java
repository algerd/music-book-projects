
package com.algerd.musicbookspringmaven.dbDriver.impl;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import java.util.List;
import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import java.sql.ResultSet;
import java.sql.Types;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public abstract class CrudRepositoryImpl<T extends Entity> extends ChangeRepositoryImpl<T> implements CrudRepository<T> {
                      
    @Override
    public void save(T entity) {   
        if (selectById(entity.getId()) == null || entity.getId() == 0) { 
            insert(entity);          
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
        return jdbcTemplate.query (
            "select " + getColumnString(true) + " from " + getTableName(), 
            (ResultSet rs, int rowNum) -> getEntity(rs)
        );
    } 
    
    @Override
    public T selectById(int id) {
        String query = "select " + getColumnString(true) + " from " + getTableName() + " where id = ?";               
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            query,
            new int[] { Types.INTEGER }
        );            
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {id}), 
            (ResultSet rs) -> rs.next() ? getEntity(rs) : null
        );
    }
    
}
