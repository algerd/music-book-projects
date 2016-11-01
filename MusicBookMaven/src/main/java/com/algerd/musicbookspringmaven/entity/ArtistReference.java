
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ArtistReference extends Entity {

    // поля таблицы artist_reference
    private int id = 0;
    private int id_artist = 1;
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty reference = new SimpleStringProperty("");
   
    public ArtistReference() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof ArtistReference) {
            Entity entity = (ArtistReference) obj;
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
