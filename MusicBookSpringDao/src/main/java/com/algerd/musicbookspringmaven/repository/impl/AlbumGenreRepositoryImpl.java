
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.AlbumGenre;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.exception.DeleteFailedException;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.AlbumGenreRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class AlbumGenreRepositoryImpl extends CrudRepositoryImpl<AlbumGenre> implements AlbumGenreRepository {
   
    @Override
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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {album.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                AlbumGenre albumGenre = getEntity(resultSet);
                Genre genre = new Genre();
                genre.setId(albumGenre.getId_genre());
                genre.setName(resultSet.getString("name"));
                albumGenre.setGenre(genre);
                return albumGenre;
            }
        );
    }
    
    @Override
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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {genre.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                AlbumGenre albumGenre = getEntity(resultSet);
            
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
            }
        );
    }    
  
    @Override
    public void deleteByAlbum(Album album) {
        String prepareQuery = "delete from " + getTableName() + " where id_album = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
			prepareQuery,
			new int[] {Types.INTEGER}
        );
		int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(new Object[] {album.getId()}));
        if (count != 1) {
			throw new DeleteFailedException("Cannot delete AlbumGenre ");
        }   
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    @Override
    public int countAlbumsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from album_genre "
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
