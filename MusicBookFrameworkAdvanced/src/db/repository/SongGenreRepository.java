
package db.repository;

import db.entity.Genre;
import db.entity.Song;
import db.entity.SongGenre;
import java.util.List;

public interface SongGenreRepository extends CrudRepository<SongGenre> {
    
    List<SongGenre> selectJoinBySong(Song song);
    List<SongGenre> selectJoinByGenre(Genre genre);
    void deleteBySong(Song song);
    int countSongsByGenre(Genre genre);
}
