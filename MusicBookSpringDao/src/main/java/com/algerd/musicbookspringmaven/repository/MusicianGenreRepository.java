
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import java.util.List;

public interface MusicianGenreRepository extends CrudRepository<MusicianGenre> {
    
    List<MusicianGenre> selectJoinByMusician(Musician musician);
    List<MusicianGenre> selectJoinByGenre(Genre genre);
    void deleteByMusician(Musician musician);
    int countMusiciansByGenre(Genre genre);
    
}
