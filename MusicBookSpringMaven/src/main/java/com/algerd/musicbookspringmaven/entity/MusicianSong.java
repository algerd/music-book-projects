
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;

@Table("musician_song")
public class MusicianSong extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_musician")
    private int id_musician = 1; 
    @Column("id_song")
    private int id_song = 1;
    
    private Musician musician;      
    private Song song;
    
    public MusicianSong() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof MusicianSong) {
            Entity entity = (MusicianSong) obj;
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
        return "";
    }

    public int getId_musician() {
        return id_musician;
    }
    public void setId_musician(int id_musician) {
        this.id_musician = id_musician;
    }

    public int getId_song() {
        return id_song;
    }
    public void setId_song(int id_song) {
        this.id_song = id_song;
    }

    public Musician getMusician() {
        return musician;
    }
    public void setMusician(Musician musician) {
        this.musician = musician;
    }

    public Song getSong() {
        return song;
    }
    public void setSong(Song song) {
        this.song = song;
    }
       
}

