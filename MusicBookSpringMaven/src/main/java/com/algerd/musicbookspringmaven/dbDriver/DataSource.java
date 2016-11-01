
package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
    
    Connection getConnection() throws SQLException;
    Connection getConnection(String username, String password) throws SQLException;
    boolean isConnection() throws SQLException;
}
