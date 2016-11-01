package db.table;

import db.entity.Entity;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface Extractor<P extends ResultSet, R extends Entity> {
  
    public R extract(P rs)  throws SQLException;    
}
