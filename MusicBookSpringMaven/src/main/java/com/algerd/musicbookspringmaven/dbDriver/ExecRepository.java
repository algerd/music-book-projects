
package com.algerd.musicbookspringmaven.dbDriver;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public interface ExecRepository<T extends Entity> {
    
    void setConnector(DataSource connector);
    
    List<T> executePrepareQuery(String prepareQuery, PrepareSetter prepareSetter, Extractor<ResultSet, Entity> callback);
    
    List<T> executeQuery(String query, Extractor<ResultSet, Entity> callback);
    
    List<String> fetchColumnList(String query);
    
    void executePrepareUpdate(String prepareQuery, PrepareSetter prepareSetter);
    
    void executeUpdate(String query);
   
    int fetchIntRow(String prepareQuery, PrepareSetter prepareSetter);
   
    int executePrepareInsert(String prepareQuery, PrepareSetter prepareSetter);
    
    void closeStatement(Statement stmt);
    
    void closeResultSet(ResultSet rs);
    
    void closeConnection();
    
    void commit();
    
    void rollback();
}
