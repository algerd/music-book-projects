
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Song;
import java.sql.ResultSet;
import java.util.List;

public class SongTable extends Table<Song> {
    
     public SongTable() { 
        super("song");
    }
   
    public boolean containsAlbum(Album album) {
        String prepareQuery = "select count(id) from " + tableName + " where id_album = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, album.getId());
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }  
    
    public List<Song> selectByAlbum(Album album) {
        String prepareQuery = "select * from song where id_album = ?";
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, album.getId());
        Extractor<ResultSet, Entity> extractor = resultSet -> fill(resultSet);
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
     
    public List<Song> selectJoin() {
        String query = 
            "select "
                + "song.id as id, "
                + "song.id_album as id_album, "
                + "song.name as name, "
                + "song.rating as rating, "
                + "album.name as album_name, "
                + "album.year as year, "
                + "album.id_artist as id_artist, "
                + "artist.name as artist_name "
            + "from song "
                + "left join album on song.id_album = album.id "
                + "left join artist on album.id_artist = artist.id ";
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            Song song = new Song();
            song.setId(resultSet.getInt("id"));
            song.setId_album(resultSet.getInt("id_album"));
            song.setName(resultSet.getString("name"));
            song.setRating(resultSet.getInt("rating"));
            
            Album album = new Album();
            album.setId(song.getId_album());
            album.setName(resultSet.getString("album_name"));
            album.setYear(resultSet.getInt("year"));
            album.setId_artist(resultSet.getInt("id_artist"));
            
            Artist artist = new Artist();
            artist.setId(album.getId_artist());
            artist.setName(resultSet.getString("artist_name"));
            
            album.setArtist(artist);
            song.setAlbum(album);
            return song;
        };             
        return executeQuery(query, extractor);
    }
    
    public boolean containsSong(String name, Album album) {
        String prepareQuery = "select count(id) from " + tableName + " where id_album = ? and name = ?";
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, album.getId());
            prepareStmt.setString(2, name.trim());
        };         
        return fetchIntRow(prepareQuery, prepareSetter) > 0;
    }
   
}
