
package db.repository.impl;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Artist;
import db.entity.ArtistReference;
import db.driver.Entity;
import db.repository.ArtistReferenceRepository;
import java.sql.ResultSet;
import java.util.List;

public class ArtistReferenceRepositoryImpl extends CrudRepositoryImpl<ArtistReference> implements ArtistReferenceRepository {

    public ArtistReferenceRepositoryImpl() { 
        super("artist_reference");
    }
   
    @Override
    public List<ArtistReference> selectByArtist(Artist artist) {
        String prepareQuery = "select * from artist_reference where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        Extractor<ResultSet, Entity> extractor = resultSet -> fill(resultSet);
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
}
