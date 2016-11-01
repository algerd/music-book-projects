
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.AlbumGenre;
import com.algerd.musicbookspringmaven.entity.Genre;
import java.util.List;

public interface AlbumGenreRepository extends CrudRepository<AlbumGenre> {
    
    List<AlbumGenre> selectJoinByAlbum(Album album);
    List<AlbumGenre> selectJoinByGenre(Genre genre);
    void deleteByAlbum(Album album);
    int countAlbumsByGenre(Genre genre);    
}
