
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Album;
import db.entity.AlbumGenre;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Genre;
import db.entity.WrapChangedEntity;
import java.sql.ResultSet;
import java.util.List;

public class AlbumGenreTable extends Table<AlbumGenre> {
    
    public AlbumGenreTable() {
        super("album_genre");
    }
   
    public List<AlbumGenre> selectJoinByAlbum(Album album) {
        String prepareQuery = 
            "select "
                + "album_genre.id, "
                + "album_genre.id_genre, "
                + "album_genre.id_album, "              
                + "genre.name "
            + "from album_genre "
                + "left join genre on album_genre.id_genre = genre.id "
            + "where album_genre.id_album = ?";  
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, album.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            AlbumGenre albumGenre = fill(resultSet);
            Genre genre = new Genre();
            genre.setId(albumGenre.getId_genre());
            genre.setName(resultSet.getString("name"));
            albumGenre.setGenre(genre);
            return albumGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);              
    }
    
    public List<AlbumGenre> selectJoinByGenre(Genre genre) {
        String prepareQuery = 
            "select "
                + "album_genre.id as id, "
                + "album_genre.id_genre as id_genre, "
                + "album_genre.id_album as id_album, "             
                + "album.name as name, "
                + "album.year as year, "               
                + "album.rating as rating, "
                + "album.id_artist as id_artist, "
                + "artist.name as artist_name "               
            + "from album_genre "
                + "left join album on album_genre.id_album = album.id "
                + "left join artist on album.id_artist = artist.id "                
            + "where album_genre.id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        }; 
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            AlbumGenre albumGenre = fill(resultSet);
            
            Album album = new Album();
            album.setId(albumGenre.getId_album());
            album.setName(resultSet.getString("name"));
            album.setRating(resultSet.getInt("rating"));
            album.setId_artist(resultSet.getInt("id_artist"));
            
            Artist artist = new Artist();
            artist.setId(album.getId_artist());
            artist.setName(resultSet.getString("artist_name"));
            
            album.setArtist(artist);
            albumGenre.setAlbum(album);
            return albumGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    };
    
    // удалить все жанры для альбома
    public void deleteByAlbum(Album album) {
        String prepareQuery = "delete from " + tableName + " where id_album = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, album.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    // Вернуть количество альбомов с жанром genre
    public int countAlbumsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from album_genre "
                + "where id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };       
        return super.fetchIntRow(prepareQuery, prepareSetter);
    }

}
