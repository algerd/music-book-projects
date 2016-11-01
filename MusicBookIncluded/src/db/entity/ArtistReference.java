
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ArtistReference extends Entity {

    // поля таблицы artist_reference
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final IntegerProperty idArtist = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty reference = new SimpleStringProperty("");
   
    public ArtistReference() {
        super();
    }
    
    public ArtistReference(Artist artist) {
        super();
        setIdArtist(artist.getId());
    }
    
    public ArtistReference(int id, int idArtist, String name, String reference) {
        super();
        setId(id);
        setIdArtist(idArtist);
        setName(name);
        setReference(reference);
    }
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setInt(1, getIdArtist());
        pstmt.setString(2, getName());
        pstmt.setString(3, getReference());
        if (update) pstmt.setInt(4, getId()); 
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
    
    public int getIdArtist() {
        return idArtist.get();
    }
    public void setIdArtist(int value) {
        idArtist.set(value);
    }
    public IntegerProperty idArtistProperty() {
        return idArtist;
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
    
    public String getReference() {
        return reference.get();
    }
    public void setReference(String value) {
        reference.set(value);
    }
    public StringProperty referenceProperty() {
        return reference;
    }
    
    
}
