
package db.repository;

import db.entity.Album;
import db.entity.Song;
import java.util.List;

public interface SongRepository extends CrudRepository<Song> {
    
    boolean containsAlbum(Album album);
    List<Song> selectByAlbum(Album album);
    List<Song> selectJoin();
    boolean containsSong(String name, Album album);
    
}
