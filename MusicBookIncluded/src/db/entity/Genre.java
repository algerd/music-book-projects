
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Genre extends Entity {
    
    // поля таблицы album
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();   
    
    public Genre() {
        super();
    }
    
    public Genre(int id, String name, String description) {
        super();
        setId(id);
        setName(name);
        setDescription(description);
    }
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setString(1, getName());
        pstmt.setString(2, getDescription());      
        if (update) pstmt.setInt(3, getId()); 
    }

    @Override
    public int getId() {
        return id.get();
    }
    @Override
    public void setId(int value) {
        id.set(value);
    }
    public IntegerProperty idProperty() {
        return id;
    }
    
    @Override
    public String getName() {
        return name.get();
    }
    public void setName(String value) {
        name.set(value);
    }
    public StringProperty nameProperty() {
        return name;
    }
    
    public String getDescription() {
        return description.get();
    }
    public void setDescription(String value) {
        description.set(value);
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    
}
