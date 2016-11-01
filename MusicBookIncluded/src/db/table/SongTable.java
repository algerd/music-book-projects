
package db.table;

import db.entity.Song;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SongTable extends Table<Song> {
      
    public SongTable() { 
        super("song");
    }
    
    @Override
    public Song fill(ResultSet rs) throws SQLException {
        return new Song(
            rs.getInt(1),
            rs.getInt(2),    
            rs.getString(3),
            rs.getInt(4),
            rs.getString(5),   
            rs.getString(6), 
            rs.getInt(7),
            rs.getString(8)    
        ); 
    }
}
