
package db.repository;

import db.entity.Artist;
import db.entity.Musician;
import db.entity.MusicianGroup;
import java.util.List;

public interface MusicianGroupRepository extends CrudRepository<MusicianGroup> {
    
    List<MusicianGroup> selectJoinByMusician(Musician musician);
    List<MusicianGroup> selectJoinByArtist(Artist artist);
    boolean containsMusicianArtist(Musician musician, Artist artist);
}
