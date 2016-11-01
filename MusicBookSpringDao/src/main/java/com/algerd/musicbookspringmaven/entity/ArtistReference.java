
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Table("artist_reference")
public class ArtistReference extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_artist")
    private int id_artist = 1;
    @Column("name")
    private final StringProperty name = new SimpleStringProperty("");
    @Column("reference")
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
