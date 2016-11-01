
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.exception.DeleteFailedException;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.entity.SongGenre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.SongGenreRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class SongGenreRepositoryImpl extends CrudRepositoryImpl<SongGenre> implements SongGenreRepository {

    @Override
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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {song.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                SongGenre songGenre = getEntity(resultSet);
                Genre genre = new Genre();
                genre.setId(songGenre.getId_genre());
                genre.setName(resultSet.getString("name"));
                songGenre.setGenre(genre);
                return songGenre;
            }
        );          
    }
        
    @Override
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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {genre.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                SongGenre songGenre = getEntity(resultSet);
            
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
            }
        );  
    };
    
    @Override
    public void deleteBySong(Song song) {
        String prepareQuery = "delete from " + getTableName() + " where id_song = ?"; 
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
			prepareQuery,
			new int[] {Types.INTEGER}
        );
		int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(new Object[] {song.getId()}));
        if (count != 1) {
			throw new DeleteFailedException("Cannot delete SongGenre");
        }   
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    @Override
    public int countSongsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from song_genre "
                + "where id_genre = ?";       
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        ); 
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {genre.getId()}), 
            (ResultSet rs) -> rs.getInt(1)
        );  
    }

}
