
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Table("musician")
public class Musician extends Entity {
    @Column("id") 
    private int id = 0;
    @Column("name")
    private final StringProperty name = new SimpleStringProperty(""); 
    @Column("date_of_birth")
    private final StringProperty date_of_birth = new SimpleStringProperty("");
    @Column("date_of_death")
    private final StringProperty date_of_death = new SimpleStringProperty("");
    @Column("country")
    private final StringProperty country = new SimpleStringProperty(""); 
    @Column("description")
    private final StringProperty description = new SimpleStringProperty("");
    @Column("rating")
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
            
    public Musician() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Musician) {
            Entity entity = (Musician) obj;
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
    
    public String getDate_of_birth() {
        return date_of_birth.get();
    }
    public void setDate_of_birth(String value) {
        date_of_birth.set(value);
    }
    public StringProperty date_of_birthProperty() {
        return date_of_birth;
    }
    
    public String getDate_of_death() {
        return date_of_death.get();
    }
    public void setDate_of_death(String value) {
        date_of_death.set(value);
    }
    public StringProperty date_of_deathProperty() {
        return date_of_death;
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
           
}
