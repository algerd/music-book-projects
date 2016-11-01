
package db.repository;

import db.entity.Album;
import db.entity.Musician;
import db.entity.MusicianAlbum;
import java.util.List;


public interface MusicianAlbumRepository extends CrudRepository<MusicianAlbum> {
    
    List<MusicianAlbum> selectJoinByMusician(Musician musician);
    List<MusicianAlbum> selectJoinByAlbum(Album album);
    boolean containsMusicianAlbum(Musician musician, Album album);
    
}
