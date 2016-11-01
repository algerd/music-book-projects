
package com.algerd.musicbookspringmaven.entity;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Column;
import com.algerd.musicbookspringmaven.dbDriver.annotation.Table;

@Table("album_genre")
public class AlbumGenre extends Entity {
    @Column("id")
    private int id = 0;
    @Column("id_genre")
    private int id_genre = 0;   
    @Column("id_album")
    private int id_album = 0;
    
    private Genre genre;
    private Album album;
    
    public AlbumGenre() {
        super();
    }
    
    @Override
	public boolean equals(Object obj) {        
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof AlbumGenre) {
            Entity entity = (AlbumGenre) obj;
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

    public int getId_album() {
        return id_album;
    }
    public void setId_album(int id_album) {
        this.id_album = id_album;
    } 

    public Genre getGenre() {
        return genre;
    }
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }  
    
}
