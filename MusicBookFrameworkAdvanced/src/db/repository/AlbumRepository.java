
package db.repository;

import db.entity.Album;
import db.entity.AlbumGenre;
import db.entity.Artist;
import java.util.List;

public interface AlbumRepository extends CrudRepository<Album> {
    
    List<Album> selectByArtist(Artist artist);
    boolean containsArtist(Artist artist);
    boolean containsAlbum(String name, Artist artist);
}
