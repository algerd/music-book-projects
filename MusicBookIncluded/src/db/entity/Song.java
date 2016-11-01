
package db.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song extends Entity<Album, Entity> {
    // поля таблицы song
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final IntegerProperty idAlbum = new SimpleIntegerProperty(0);
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty track = new SimpleIntegerProperty();
    private final StringProperty lyric = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();    
    private final StringProperty description = new SimpleStringProperty();
    // дополнительные свойства
    private final IntegerProperty rank = new SimpleIntegerProperty();
      
    public Song() {
        super();
    }
    
    public Song(Album album) {
        super();
        super.setParent(album);
    }

    public Song(
        int id, 
        int idAlbum, 
        String name, 
        int track, 
        String lyric, 
        String time, 
        int rating, 
        String comment) 
    {     
        super();
        setId(id);
        setIdAlbum(idAlbum);
        setName(name);
        setTrack(track);
        setLyric(lyric);
        setTime(time);
        setRating(rating);
        setDescription(comment);
    }    
    
    @Override
    public void fillPreparedStatement(PreparedStatement pstmt, boolean update) throws SQLException {
        pstmt.setInt(1, getIdAlbum());
        pstmt.setString(2, getName());
        pstmt.setInt(3, getTrack()); 
        pstmt.setString(4, getLyric());
        pstmt.setString(5, getTime());
        pstmt.setInt(6, getRating()); 
        pstmt.setString(7, getDescription());
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

    public int getIdAlbum() {
        return idAlbum.get();
    }

    public void setIdAlbum(int value) {
        idAlbum.set(value);
    }

    public IntegerProperty idAlbumProperty() {
        return idAlbum;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getTrack() {
        return track.get();
    }

    public void setTrack(int value) {
        track.set(value);
    }

    public IntegerProperty trackProperty() {
        return track;
    }

    public String getLyric() {
        return lyric.get();
    }

    public void setLyric(String value) {
        lyric.set(value);
    }

    public StringProperty lyricProperty() {
        return lyric;
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
              
    @Override
    public String toString() {
        return "  Song{" + "id=" + getId() + ", idAlbum=" + getIdAlbum() +", name=" + getName() + ", track=" + getTrack() + ", lyric=" + getLyric() + ", time=" + getTime() + ", rating=" + getRating() + ", comment=" + getDescription() + '}';
    }

}
