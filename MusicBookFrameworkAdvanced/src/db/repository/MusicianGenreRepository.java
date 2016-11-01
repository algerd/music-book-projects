
package db.repository;

import db.entity.Genre;
import db.entity.Musician;
import db.entity.MusicianGenre;
import java.util.List;

public interface MusicianGenreRepository extends CrudRepository<MusicianGenre> {
    
    List<MusicianGenre> selectJoinByMusician(Musician musician);
    List<MusicianGenre> selectJoinByGenre(Genre genre);
    void deleteByMusician(Musician musician);
    int countMusiciansByGenre(Genre genre);
    
}
