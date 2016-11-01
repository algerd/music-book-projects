
package db.table;

import db.entity.Artist;
import db.entity.Musician;
import db.entity.MusicianGroup;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MusicianGroupTable extends Table<MusicianGroup> {
    
    public MusicianGroupTable() { 
        super("musician_group");
    }
    
    @Override
    public MusicianGroup fill(ResultSet rs) throws SQLException {
        return new MusicianGroup(
            rs.getInt(1),
            rs.getInt(2),    
            rs.getInt(3),      
            rs.getString(4),
            rs.getString(5)
        ); 
    }
    
    public void delete(Artist artist) {        
        ArrayList<MusicianGroup> deleteEntities = new ArrayList<>();
        for (MusicianGroup musicianGroup : super.getEntities()) {
            if (musicianGroup.getIdArtist() == artist.getId()) {
                deleteEntities.add(musicianGroup);
            }
        }
        for (MusicianGroup musicianGroup : deleteEntities) {
            super.delete(musicianGroup);
        }      
    }
    
    public void delete(Musician musician) {
        ArrayList<MusicianGroup> deleteEntities = new ArrayList<>();
        for (MusicianGroup musicianGroup : super.getEntities()) {
            if (musicianGroup.getIdMusician() == musician.getId()) {
                deleteEntities.add(musicianGroup);
            }
        }
        for (MusicianGroup musicianGroup : deleteEntities) {
            super.delete(musicianGroup);
        }
    }
    
}
