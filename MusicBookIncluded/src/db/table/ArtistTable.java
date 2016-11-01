
package db.table;

import db.entity.Artist;
import db.entity.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistTable extends Table<Artist> {
    
    private MusicianGroupTable musicianGroupTable;
    private ArtistReferenceTable artistReferenceTable;
    
    public ArtistTable() {
        super("artist");
    }
       
    /**
     * Сбросить жанр артиста к Unknown жанру.
     */
    public void resetGenre(Genre genre, Genre unknownGenre) {     
        for (Artist artist : getEntities()) {
            if (artist.getGenre() == genre) {
                artist.setGenre(unknownGenre);
                artist.setIdGenre(unknownGenre.getId());
                save(artist);
            }
        }       
    }   
    
    @Override
    public Artist fill(ResultSet rs) throws SQLException {
        return new Artist(
            rs.getInt(1),
            rs.getString(2),
            rs.getInt(3),
            rs.getInt(4),   
            rs.getString(5)
        ); 
    }
    
    @Override
    public void delete(Artist entity) {
        // запретить удаление записи с id = 1 (Unknown artist)
        if (entity.getId() != 1) {
            super.delete(entity);
            // удалить все записи с entity в MusicianGroupTable, ArtistReferenceTable
            musicianGroupTable.delete(entity);
            artistReferenceTable.delete(entity);
        }      
    }
    
    @Override 
    public void save(Artist entity) {
        // запретить добавление/редактирование записи с id = 1 (Unknown artist)
        if (entity.getId() != 1) {
            super.save(entity);
        }
    }
    
    public void setMusicianGroupTable(MusicianGroupTable musicianGroupTable) {
        this.musicianGroupTable = musicianGroupTable;
    }

    public void setArtistReferenceTable(ArtistReferenceTable artistReferenceTable) {
        this.artistReferenceTable = artistReferenceTable;
    }   
                        
}
