
package com.algerd.musicbookspringmaven.dbDriver.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.algerd.musicbookspringmaven.dbDriver.PrepareLabel;

 /*
Использование:

    PrepareLabel lpstmt = new PrepareLabelImpl();    
    String prepareQuery = "delete from " + tableName + " where id_album = " + lpstmt.get("id_album");
    PrepareSetter prepareSetter = prepareStmt -> {
        lpstmt.init(prepareStmt);    
        lpstmt.set("id_album", album.getId()); 
    };
    executePrepareUpdate(prepareQuery, prepareSetter);  
*/
public class PrepareLabelImpl implements PrepareLabel {
    
    private final List<String> labels = new ArrayList<>();
    private PreparedStatement preparedStatement;        
      
    public PrepareLabelImpl() {  
    }
    
    @Override
    public void init(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }
       
    @Override
    public String get(String label) {
        labels.add(label);
        return " ?";
    }
    
    @Override
    public void set(String label, String value) throws SQLException {      
        preparedStatement.setString(labels.indexOf(label) + 1, value);
    }
    
    @Override
    public void set(String label, int value) throws SQLException {      
        preparedStatement.setInt(labels.indexOf(label) + 1, value);
    } 
    
}
