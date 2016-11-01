
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import java.util.List;

public interface AlbumRepository extends CrudRepository<Album> {
    
    List<Album> selectByArtist(Artist artist);
    boolean containsArtist(Artist artist);
    boolean containsAlbum(String name, Artist artist);
}
