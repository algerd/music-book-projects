
package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseRepository<T extends Entity> {
         
    T getEntity(ResultSet rs) throws SQLException;
    
    int insert(T entity);
    
    void update(T entity);
    
    void executePrepareDelete(T entity);
    
    boolean isUniqueColumnValue(String column, String value);
    
}
