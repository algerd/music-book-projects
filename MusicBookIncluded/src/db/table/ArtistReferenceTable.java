
package db.table;

import db.entity.Artist;
import db.entity.ArtistReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArtistReferenceTable extends Table<ArtistReference> {

    public ArtistReferenceTable() { 
        super("artist_reference");
    }
    
    @Override
    public ArtistReference fill(ResultSet rs) throws SQLException {
        return new ArtistReference(
            rs.getInt(1),
            rs.getInt(2),    
            rs.getString(3),
            rs.getString(4)
        ); 
    }
    
    public void delete(Artist artist) {        
        ArrayList<ArtistReference> deleteEntities = new ArrayList<>();
        for (ArtistReference artistReference : super.getEntities()) {
            if (artistReference.getIdArtist() == artist.getId()) {
                deleteEntities.add(artistReference);
            }
        }
        for (ArtistReference artistReference : deleteEntities) {
            super.delete(artistReference);
        }      
    }
}
