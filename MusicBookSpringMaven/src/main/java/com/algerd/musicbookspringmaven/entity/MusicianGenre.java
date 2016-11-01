
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;

@Table("musician_genre")
public class MusicianGenre extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_genre")
    private int id_genre = 0;
    @Column("id_musician")
    private int id_musician = 0;
    
    private Genre genre;
    private Musician musician;
    
    public MusicianGenre() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof MusicianGenre) {
            Entity entity = (MusicianGenre) obj;
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

    public int getId_genre() {
        return id_genre;
    }
    public void setId_genre(int id_genre) {
        this.id_genre = id_genre;
    }

    public int getId_musician() {
        return id_musician;
    }
    public void setId_musician(int id_musician) {
        this.id_musician = id_musician;
    } 

    public Genre getGenre() {
        return genre;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Musician getMusician() {
        return musician;
    }
    public void setMusician(Musician musician) {
        this.musician = musician;
    }
    
}
