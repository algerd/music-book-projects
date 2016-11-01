
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Artist;
import db.entity.ArtistReference;
import db.entity.Entity;
import java.sql.ResultSet;
import java.util.List;

public class ArtistReferenceTable extends Table<ArtistReference> {

    public ArtistReferenceTable() { 
        super("artist_reference");
    }
   
    public List<ArtistReference> selectByArtist(Artist artist) {
        String prepareQuery = "select * from artist_reference where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        Extractor<ResultSet, Entity> extractor = resultSet -> fill(resultSet);
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
}
