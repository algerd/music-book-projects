
package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PrepareSetter {
    public void set(PreparedStatement pstmt) throws SQLException ;
}
