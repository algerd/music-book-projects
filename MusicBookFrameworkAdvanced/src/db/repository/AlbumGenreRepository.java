
package db.repository;

import db.entity.Album;
import db.entity.AlbumGenre;
import db.entity.Genre;
import java.util.List;

public interface AlbumGenreRepository extends CrudRepository<AlbumGenre> {
    
    List<AlbumGenre> selectJoinByAlbum(Album album);
    List<AlbumGenre> selectJoinByGenre(Genre genre);
    void deleteByAlbum(Album album);
    int countAlbumsByGenre(Genre genre);    
}
