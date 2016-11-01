
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import java.util.List;

public interface MusicianGroupRepository extends CrudRepository<MusicianGroup> {
    
    List<MusicianGroup> selectJoinByMusician(Musician musician);
    List<MusicianGroup> selectJoinByArtist(Artist artist);
    boolean containsMusicianArtist(Musician musician, Artist artist);
}
