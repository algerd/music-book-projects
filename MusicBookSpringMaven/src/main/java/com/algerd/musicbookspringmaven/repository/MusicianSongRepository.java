
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianSong;
import com.algerd.musicbookspringmaven.entity.Song;
import java.util.List;

public interface MusicianSongRepository extends CrudRepository<MusicianSong> {
    
    List<MusicianSong> selectJoinByMusician(Musician musician);
    List<MusicianSong> selectJoinBySong(Song song);
    boolean containsMusicianSong(Musician musician, Song song);
    
}
