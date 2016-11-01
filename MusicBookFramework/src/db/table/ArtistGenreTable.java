
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Artist;
import db.entity.ArtistGenre;
import db.entity.Entity;
import db.entity.Genre;
import db.entity.WrapChangedEntity;
import java.sql.ResultSet;
import java.util.List;

public class ArtistGenreTable extends Table<ArtistGenre> {
    
    public ArtistGenreTable() {
        super("artist_genre");
    }
   
    public List<ArtistGenre> selectJoinByArtist(Artist artist) {   
        String prepareQuery = 
            "select "
                + "artist_genre.id, "
                + "artist_genre.id_genre, "
                + "artist_genre.id_artist, "              
                + "genre.name "
            + "from artist_genre "
                + "left join genre on artist_genre.id_genre = genre.id "
            + "where artist_genre.id_artist = ?";  
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, artist.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            ArtistGenre artistGenre = fill(resultSet);
            Genre genre = new Genre();
            genre.setId(artistGenre.getId_genre());
            genre.setName(resultSet.getString("name"));
            artistGenre.setGenre(genre);
            return artistGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);       
    }
    
    // вернуть ссылки, у которых есть жанр genre
    public List<ArtistGenre> selectJoinByGenre(Genre genre) {
        String prepareQuery = 
            "select "
                + "artist_genre.id, "
                + "artist_genre.id_genre, "
                + "artist_genre.id_artist, "             
                + "artist.name, "
                + "artist.rating "
            + "from artist_genre "
                + "left join artist on artist_genre.id_artist = artist.id "
            + "where artist_genre.id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };    
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            ArtistGenre artistGenre = fill(resultSet);
            Artist artist = new Artist();
            artist.setId(artistGenre.getId_artist());
            artist.setName(resultSet.getString("name"));
            artist.setRating(resultSet.getInt("rating"));
            artistGenre.setArtist(artist);
            return artistGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    };
    
    // удалить все жанры для артиста
    public void deleteByArtist(Artist artist) {
        String prepareQuery = "delete from " + tableName + " where id_artist = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, artist.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    // Вернуть количество артистов с жанром genre
    public int countArtistsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from artist_genre "
                + "where id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };       
        return super.fetchIntRow(prepareQuery, prepareSetter);
    }
    
}
