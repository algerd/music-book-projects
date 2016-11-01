
package db.table;

import db.entity.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreTable extends Table<Genre> {
    
    private ArtistTable artistTable;
    
    public GenreTable() { 
        super("genre");
    }
    
    /**
     * Удалить сущность из бд и из таблицы.
     */
    @Override
    public void delete(Genre genre) {
        super.delete(genre);      
        artistTable.resetGenre(genre, getEntityWithId(1));
    }    
    
    @Override
    public Genre fill(ResultSet rs) throws SQLException {
        return new Genre(
            rs.getInt(1),    
            rs.getString(2),
            rs.getString(3)
        ); 
    }
    
    public void setArtistTable(ArtistTable artistTable) {
        this.artistTable = artistTable;
    }
   
}
