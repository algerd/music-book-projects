
package db.repository;

import db.entity.Artist;
import db.entity.ArtistReference;
import java.util.List;

public interface ArtistReferenceRepository extends CrudRepository<ArtistReference> {
    
    List<ArtistReference> selectByArtist(Artist artist);
    
}
