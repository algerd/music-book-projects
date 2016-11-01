
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.entity.SongGenre;
import java.util.List;

public interface SongGenreRepository extends CrudRepository<SongGenre> {
    
    List<SongGenre> selectJoinBySong(Song song);
    List<SongGenre> selectJoinByGenre(Genre genre);
    void deleteBySong(Song song);
    int countSongsByGenre(Genre genre);
}
