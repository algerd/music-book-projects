
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Artist extends Entity<Entity, Album> {
    // поля таблицы artist
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty idGenre = new SimpleIntegerProperty(1);
    private final IntegerProperty rating = new SimpleIntegerProperty(-1);
    private final StringProperty description = new SimpleStringProperty();
    // дополнительные свойства
    private final IntegerProperty rank = new SimpleIntegerProperty();   
    private final ObjectProperty<Genre> genre = new SimpleObjectProperty<>();   
    private final BooleanProperty changedImage = new SimpleBooleanProperty(false);
                  
    public Artist() {
        super();
    }
    
    public Artist(int id, String name, int idGenre, int rating, String comment) {
        super();
        setId(id);
        setName(name);
        setIdGenre(idGenre);
        setRating(rating);
        setDescription(comment);
    }
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setString(1, getName());
        pstmt.setInt(2, getIdGenre());
        pstmt.setInt(3, getRating());
        pstmt.setString(4, getDescription());
        if (update) pstmt.setInt(5, getId()); 
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
      
    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
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

    public int getRank() {
        return rank.get();
    }
    public void setRank(int value) {
        rank.set(value);
    }
    public IntegerProperty rankProperty() {
        return rank;
    }
    
    public int getIdGenre() {
        return idGenre.get();
    }
    public void setIdGenre(int value) {
        idGenre.set(value);
    }
    public IntegerProperty IdGenreProperty() {
        return idGenre;
    }
    
    public Genre getGenre() {
        return genre.get();
    }
    public void setGenre(Genre value) {
        genre.set(value);
    }
    public ObjectProperty genreProperty() {
        return genre;
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
    
    @Override
    public String toString() {
        String artistString = "Artist{" + "id=" + getId() + ", name=" + getName() + ", genre=" + getIdGenre() + ", rating=" + getRating() + ", comment=" + getDescription() + '}';
        String albumsString = "";
        Iterator<Album> albumsIterator = super.getChildren().iterator();
        while(albumsIterator.hasNext()) {
            albumsString += "\n " + albumsIterator.next();
        }     
        return artistString + albumsString;
    }

}
