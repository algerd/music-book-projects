
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Album extends Entity<Artist, Song> {
    // поля таблицы album
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final IntegerProperty idArtist = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty year = new SimpleIntegerProperty(0);
    private final StringProperty time = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty(-1);
    private final StringProperty description = new SimpleStringProperty();    
    // дополнительные свойства
    private final IntegerProperty rank = new SimpleIntegerProperty();
    private final BooleanProperty changedImage = new SimpleBooleanProperty(false);
    
    public Album() {
        super();
    }
    
    public Album(Artist artist) {
        super();
        super.setParent(artist);
        setIdArtist(artist.getId());
    }

    public Album(
        int id,    
        int idArtist, 
        String name,
        int year, 
        String time, 
        int rating, 
        String comment) 
    {   
        super();
        setId(id);
        setIdArtist(idArtist);
        setName(name);
        setYear(year);
        setTime(time);
        setRating(rating);
        setDescription(comment);
    }
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setInt(1, getIdArtist());
        pstmt.setString(2, getName());
        pstmt.setInt(3, getYear());
        pstmt.setString(4, getTime()); 
        pstmt.setInt(5, getRating());
        pstmt.setString(6, getDescription());
        if (update) pstmt.setInt(7, getId()); 
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
    
    public int getYear() {
        return year.get();
    }
    public void setYear(int value) {
        year.set(value);
    }
    public IntegerProperty yearProperty() {
        return year;
    }

    public String getTime() {
        return time.get();
    }
    public void setTime(String value) {
        time.set(value);
    }
    public StringProperty timeProperty() {
        return time;
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
        String albumString = " Album{" + "id=" + getId() + ", idArtist=" + getIdArtist() + ", name=" + getName() + ", year=" + getYear() + ", time=" + getTime() + ", rating=" + getRating() + ", comment=" + getDescription() + '}';
        String songsString = "";
        Iterator<Song> songsIterator = super.getChildren().iterator();
        while(songsIterator.hasNext()) {
            songsString += "\n " + songsIterator.next();
        }     
        return albumString + songsString;  
    }
   
}
