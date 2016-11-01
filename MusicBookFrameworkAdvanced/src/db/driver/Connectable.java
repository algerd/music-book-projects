
package db.driver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface Connectable {
    
    Connection connect() throws SQLException;   
    void close();
    void commit();
    void rollback();
    void closeStatement(Statement stmt);
    void closeResultSet(ResultSet rs);
    
    /**
     * Получить последний сгенерированный первичный ключ
     * @return int id последней вставленной записи
     * @throws SQLException 
     */
    int getGeneratedKey() throws SQLException;
     
}
