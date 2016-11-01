
package db.repository;

import db.driver.Entity;
import db.driver.Extractor;
import db.driver.PrepareSetter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ExecRepository<T extends Entity> {
    
    /**
     * Извлечь данные из ResultSet и создать на основе их новую сущность.
     */
    T fill(ResultSet rs) throws SQLException;
    
    List<T> executePrepareQuery(String prepareQuery, PrepareSetter prepareSetter, Extractor<ResultSet, Entity> callback);
    
    List<T> executeQuery(String query, Extractor<ResultSet, Entity> callback);
    
    void executePrepareUpdate(String prepareQuery, PrepareSetter prepareSetter);
    
    void executeUpdate(String query);
    
    void executePrepareDelete(T entity);
    
    /**
     * @return last id
     */
    int insert(T entity);
    
    void update(T entity);
    
    int fetchIntRow(String prepareQuery, PrepareSetter prepareSetter);
    
}
