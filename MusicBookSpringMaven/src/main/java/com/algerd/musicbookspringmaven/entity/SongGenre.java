
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;

@Table("song_genre")
public class SongGenre extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_genre")
    private int id_genre = 0;
    @Column("id_song")
    private int id_song = 0; 
    
    private Genre genre;
    private Song song;
          
    public SongGenre() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof SongGenre) {
            Entity entity = (SongGenre) obj;
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

    public int getId_song() {
        return id_song;
    }
    public void setId_song(int id_song) {
        this.id_song = id_song;
    } 

    public Genre getGenre() {
        return genre;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Song getSong() {
        return song;
    }
    public void setSong(Song song) {
        this.song = song;
    }
      
}
