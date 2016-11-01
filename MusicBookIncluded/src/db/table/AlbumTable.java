
package db.table;

import db.entity.Album;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbumTable extends Table<Album> {
       
    public AlbumTable() {
        super("album");
    }
    
    @Override
    public Album fill(ResultSet rs) throws SQLException {
        return new Album(
            rs.getInt(1),
            rs.getInt(2),
            rs.getString(3),
            rs.getInt(4),   
            rs.getString(5), 
            rs.getInt(6),    
            rs.getString(7)    
        ); 
    }
    
    @Override
    public void delete(Album entity) {
        // запретить удаление записи с id = 1 (Unknown album)
        if (entity.getId() != 1) {
            super.delete(entity);
        }      
    }
    
    @Override 
    public void save(Album entity) {
        // запретить добавление/редактирование записи с id = 1 (Unknown album)
        if (entity.getId() != 1) {
            super.save(entity);
        }
    }
}
