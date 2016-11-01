
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import java.util.List;

public interface ArtistReferenceRepository extends CrudRepository<ArtistReference> {
    
    List<ArtistReference> selectByArtist(Artist artist);
    
}
