
package db.driver.impl;

import db.driver.Labeledable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

 /*
Использование:

    Labeledable lpstmt = new LabeledPreparedStatement();    
    String prepareQuery = "delete from " + tableName + " where id_album = " + lpstmt.get("id_album");
    PrepareSetter prepareSetter = prepareStmt -> {
        lpstmt.init(prepareStmt);    
        lpstmt.set("id_album", album.getId()); 
    };
    executePrepareUpdate(prepareQuery, prepareSetter);  
*/
public class LabeledPreparedStatement implements Labeledable {
    
    private final List<String> labels = new ArrayList<>();
    private PreparedStatement preparedStatement;        
      
    public LabeledPreparedStatement() {  
    }
    
    public void init(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }
       
    public String get(String label) {
        labels.add(label);
        return " ?";
    }
    
    public void set(String label, String value) throws SQLException {      
        preparedStatement.setString(labels.indexOf(label) + 1, value);
    }
    
    public void set(String label, int value) throws SQLException {      
        preparedStatement.setInt(labels.indexOf(label) + 1, value);
    } 
    
}
