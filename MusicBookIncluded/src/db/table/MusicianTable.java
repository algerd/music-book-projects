
package db.table;

import db.entity.Musician;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MusicianTable extends Table<Musician>  {
 
    private MusicianGroupTable musicianGroupTable;
    
    public MusicianTable() { 
        super("musician");
    }
    
    @Override
    public Musician fill(ResultSet rs) throws SQLException {
        return new Musician(
            rs.getInt(1),    
            rs.getString(2),
            rs.getString(3),
            rs.getString(4),
            rs.getString(5),    
            rs.getString(6),
            rs.getString(7),    
            rs.getInt(8)   
        ); 
    }
    
    @Override
    public void delete(Musician entity) {
        super.delete(entity);
        // удалить все записи с entity в MusicianGroupTable
        musicianGroupTable.delete(entity);         
    }

    public void setMusicianGroupTable(MusicianGroupTable musicianGroupTable) {
        this.musicianGroupTable = musicianGroupTable;
    }
      
}
