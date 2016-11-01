
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Table("song")
public class Song extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_album")
    private int id_album = 1; // id = 1 "Unknown" album
    @Column("name")
    private final StringProperty name = new SimpleStringProperty("");
    @Column("track")
    private final IntegerProperty track = new SimpleIntegerProperty(0);
    @Column("lyric")
    private final StringProperty lyric = new SimpleStringProperty("");
    @Column("time")
    private final StringProperty time = new SimpleStringProperty("");
    @Column("rating")
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    @Column("description")    
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
