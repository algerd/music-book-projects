
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Musician extends Entity {
     
    // поля таблицы musician
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty(); 
    private final StringProperty dateOfBirth = new SimpleStringProperty();
    private final StringProperty dateOfDeath = new SimpleStringProperty();
    private final StringProperty country = new SimpleStringProperty(); 
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    // дополнительные свойства
    private final BooleanProperty changedImage = new SimpleBooleanProperty(false);
         
    public Musician() {
        super();
    }
    
    public Musician(int id, String name, String type, String dateOfBirth, String dateOfDearth, String country, String description, int rating) {
        super();
        setId(id);
        setName(name);  
        setType(type);
        setDateOfBirth(dateOfBirth);
        setDateOfDeath(dateOfDearth);
        setCountry(country);
        setDescription(description);
        setRating(rating);
    }
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setString(1, getName()); 
        pstmt.setString(2, getType());
        pstmt.setString(3, getDateOfBirth());
        pstmt.setString(4, getDateOfDeath());
        pstmt.setString(5, getCountry());      
        pstmt.setString(6, getDescription());
        pstmt.setInt(7, getRating());
        if (update) pstmt.setInt(8, getId()); 
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
    
    public String getType() {
        return type.get();
    }
    public void setType(String value) {
        type.set(value);
    }
    public StringProperty typeProperty() {
        return type;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth.get();
    }
    public void setDateOfBirth(String value) {
        dateOfBirth.set(value);
    }
    public StringProperty dateOfBirthProperty() {
        return dateOfBirth;
    }
    
    public String getDateOfDeath() {
        return dateOfDeath.get();
    }
    public void setDateOfDeath(String value) {
        dateOfDeath.set(value);
    }
    public StringProperty dateOfDeathProperty() {
        return dateOfDeath;
    }
    
    public String getCountry() {
        return country.get();
    }
    public void setCountry(String value) {
        country.set(value);
    }
    public StringProperty countryProperty() {
        return country;
    }
    
    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
    }
    
    public boolean isChangedImage() {
        return changedImage.get();
    }
    public void setChangedImage(boolean value) {
        changedImage.set(value);
    }
    public BooleanProperty changedImageProperty() {
        return changedImage;
    }    
       
}
