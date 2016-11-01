
package db.repository;

import db.entity.Artist;
import db.entity.ArtistGenre;
import db.entity.Genre;
import java.util.List;

public interface ArtistGenreRepository extends CrudRepository<ArtistGenre> {
    
    List<ArtistGenre> selectJoinByArtist(Artist artist);
    List<ArtistGenre> selectJoinByGenre(Genre genre);
    void deleteByArtist(Artist artist);
    int countArtistsByGenre(Genre genre);
    
}
