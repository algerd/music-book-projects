
package db.table;

import db.driver.Extractor;
import db.driver.LabeledPreparedStatement;
import db.driver.PrepareSetter;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import java.sql.ResultSet;
import java.util.List;

public class AlbumTable extends Table<Album> {
    
    public AlbumTable() {
        super("album");
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
    
    // вернуть альбомы, у которых есть Artist
    public List<Album> selectByArtist(Artist artist) {
        String prepareQuery = "select * from album where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        Extractor<ResultSet, Entity> extractor = resultSet -> fill(resultSet);
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }   
    
    public boolean containsArtist(Artist artist) {
        String prepareQuery = "select count(id) from " + tableName + " where id_artist = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
    
    public boolean containsAlbum(String name, Artist artist) {
        /*
        String prepareQuery = "select count(id) from " + tableName + " where id_artist = ? and name = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, artist.getId());
            prepareStmt.setString(2, name.trim());
        };
        */
        LabeledPreparedStatement lpstmt = new LabeledPreparedStatement();
        String prepareQuery = 
            "select count(id) from " + tableName
                + " where id_artist = " + lpstmt.get("id_artist") + " and name = " + lpstmt.get("name");
        PrepareSetter prepareSetter = prepareStmt -> {
            lpstmt.setPreparedStatement(prepareStmt);    
            lpstmt.set("id_artist", artist.getId());          
            lpstmt.set("name", name.trim());
        };
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
     
}
