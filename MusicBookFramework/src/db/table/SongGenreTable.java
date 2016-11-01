
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Genre;
import db.entity.Song;
import db.entity.SongGenre;
import db.entity.WrapChangedEntity;
import java.sql.ResultSet;
import java.util.List;

public class SongGenreTable extends Table<SongGenre> {
    
    public SongGenreTable() {
        super("song_genre");
    }
    
    public List<SongGenre> selectJoinBySong(Song song) {
        String prepareQuery = 
            "select "
                + "song_genre.id, "
                + "song_genre.id_genre, "
                + "song_genre.id_song, "              
                + "genre.name "
            + "from song_genre "
                + "left join genre on song_genre.id_genre = genre.id "
            + "where song_genre.id_song = ?";  
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, song.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            SongGenre songGenre = fill(resultSet);
            Genre genre = new Genre();
            genre.setId(songGenre.getId_genre());
            genre.setName(resultSet.getString("name"));
            songGenre.setGenre(genre);
            return songGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);              
    }
    
    
    public List<SongGenre> selectJoinByGenre(Genre genre) {
        String prepareQuery = 
            "select "
                + "song_genre.id as id, "
                + "song_genre.id_genre as id_genre, "
                + "song_genre.id_song as id_song, "             
                + "song.name as name, "
                + "song.rating as rating, "
                + "song.id_album as id_album, "
                + "album.name as album_name, "               
                + "album.year as year, "               
                + "album.id_artist as id_artist, "
                + "artist.name as artist_name "               
            + "from song_genre "
                + "left join song on song_genre.id_song = song.id "
                + "left join album on song.id_album = album.id "
                + "left join artist on album.id_artist = artist.id "                
            + "where song_genre.id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        }; 
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            SongGenre songGenre = fill(resultSet);
            
            Song song = new Song();
            song.setId(songGenre.getId_song());
            song.setName(resultSet.getString("name"));
            song.setRating(resultSet.getInt("rating"));
            song.setId_album(resultSet.getInt("id_album"));
            
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
            songGenre.setSong(song);
            return songGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    };
    
    // удалить все жанры для песни
    public void deleteBySong(Song song) {
        String prepareQuery = "delete from " + tableName + " where id_song = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, song.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    // Вернуть количество песен с жанром genre
    public int countSongsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from song_genre "
                + "where id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };       
        return super.fetchIntRow(prepareQuery, prepareSetter);
    }

}
