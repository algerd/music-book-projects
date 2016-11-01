
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.entity.Genre;
import java.util.List;

public interface ArtistGenreRepository extends CrudRepository<ArtistGenre> {
    
    List<ArtistGenre> selectJoinByArtist(Artist artist);
    List<ArtistGenre> selectJoinByGenre(Genre genre);
    void deleteByArtist(Artist artist);
    int countArtistsByGenre(Genre genre);
    
}
