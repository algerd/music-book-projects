package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface Extractor<P extends ResultSet, R extends Entity> {
  
    public R extract(P rs)  throws SQLException;    
}
