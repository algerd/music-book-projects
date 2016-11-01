
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;

@Table("musician_instrument")
public class MusicianInstrument extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_instrument")
    private int id_instrument = 0; 
    @Column("id_musician")
    private int id_musician = 0;
    
    private Instrument instrument;
    private Musician musician;
    
    public MusicianInstrument() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof MusicianInstrument) {
            Entity entity = (MusicianInstrument) obj;
            if (entity.getId() == getId()) {
                return true;
            }
        }    
		return false;
	}
    
    @Override
    public String getName() {
        return "";
    }
    
    @Override
    public int getId() {
        return id;
    }
    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getId_instrument() {
        return id_instrument;
    }
    public void setId_instrument(int id_instrument) {
        this.id_instrument = id_instrument;
    }

    public int getId_musician() {
        return id_musician;
    }
    public void setId_musician(int id_musician) {
        this.id_musician = id_musician;
    }

    public Instrument getInstrument() {
        return instrument;
    }
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Musician getMusician() {
        return musician;
    }
    public void setMusician(Musician musician) {
        this.musician = musician;
    }
    
}
