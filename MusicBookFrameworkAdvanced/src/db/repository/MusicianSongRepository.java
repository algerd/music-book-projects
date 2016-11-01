
package db.repository;

import db.entity.Musician;
import db.entity.MusicianSong;
import db.entity.Song;
import java.util.List;

public interface MusicianSongRepository extends CrudRepository<MusicianSong> {
    
    List<MusicianSong> selectJoinByMusician(Musician musician);
    List<MusicianSong> selectJoinBySong(Song song);
    boolean containsMusicianSong(Musician musician, Song song);
    
}
