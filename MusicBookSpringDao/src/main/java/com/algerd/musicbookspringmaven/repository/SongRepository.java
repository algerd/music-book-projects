
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Song;
import java.util.List;

public interface SongRepository extends CrudRepository<Song> {
    
    boolean containsAlbum(Album album);
    List<Song> selectByAlbum(Album album);
    List<Song> selectJoin();
    boolean containsSong(String name, Album album);
    
}
