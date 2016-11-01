
package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Labeledable {
    
    void init(PreparedStatement preparedStatement);
    String get(String label);
    void set(String label, String value) throws SQLException;
    void set(String label, int value) throws SQLException;
}
