
package db.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Album extends Entity {
       
    private int id = 0;
    private int id_artist = 1; // id = 1 "Unknown" artist
    private final StringProperty name = new SimpleStringProperty("");
    private final IntegerProperty year = new SimpleIntegerProperty(0);
    private final StringProperty time = new SimpleStringProperty("");
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private final StringProperty description = new SimpleStringProperty("");  
    
    private Artist artist;

    public Album() {
        super();
    }
   
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Album) {
            Entity entity = (Album) obj;
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

    public int getId_artist() {
        return id_artist;
    }
    public void setId_artist(int id_artist) {
        this.id_artist = id_artist;
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

    public Artist getArtist() {
        return artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    
    @Override
    public String toString() {
        return getName();
    }   
              
}
