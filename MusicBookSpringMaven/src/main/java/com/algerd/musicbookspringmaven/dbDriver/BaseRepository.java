
package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface BaseRepository<T extends Entity> {
      
    /**
     * Извлечь данные из ResultSet и создать на основе их новую сущность.
     */
    T fill(ResultSet rs) throws SQLException;
    
    /**
     * @return last id
     */
    int insert(T entity);
    
    void update(T entity);
    
    void executePrepareDelete(T entity);
    
    boolean isUniqueColumnValue(String column, String value);
    
}
