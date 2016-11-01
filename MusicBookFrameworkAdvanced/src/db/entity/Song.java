
package db.entity;

import db.driver.Entity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song extends Entity {
    
    private int id = 0;
    private int id_album = 1; // id = 1 "Unknown" album
    private final StringProperty name = new SimpleStringProperty("");
    private final IntegerProperty track = new SimpleIntegerProperty(0);
    private final StringProperty lyric = new SimpleStringProperty("");
    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty rating = new SimpleIntegerProperty(0);    
    private final StringProperty description = new SimpleStringProperty("");
    
    private Album album;
    
    public Song() {
        super();
    }
  
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Song) {
            Entity entity = (Song) obj;
            if (entity.getId() == getId()) {
                return true;
            }
        }    
		return false;
	}

    @Override
    public int getId() {
        return id;
    }
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId_album() {
        return id_album;
    }
    public void setId_album(int id_album) {
        this.id_album = id_album;
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

    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
        
    @Override
    public String toString() {
        return getName();
    }
    
}
