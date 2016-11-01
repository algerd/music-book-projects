
package com.algerd.musicbookspringmaven.dbDriver;

import java.util.List;

public interface CrudRepository<T extends Entity> extends ChangeRepository<T> {
    
    /**
     * Добавить сущность в бд и в таблицу, если её ещё нет в таблице,
     * в противном случае обновить сущность в бд.
     */
    void save(T entity);
    
    /**
     * Удалить сущность из бд и из таблицы.
     */
    void delete(T entity);
    
    /**
     * Извлечь все записи из бд и вернуть список.
     */
    List<T> selectAll();
    
    /**
     * Извлечь запись из бд c id и вернуть сущность T.
     */
    T selectById(int id);
    
    boolean isUniqueColumnValue(String column, String value);   

}
