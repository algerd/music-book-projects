
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.dbDriver.exception.DeleteFailedException;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.ArtistGenreRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class ArtistGenreRepositoryImpl extends CrudRepositoryImpl<ArtistGenre> implements ArtistGenreRepository {
   
    @Override
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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {artist.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                ArtistGenre artistGenre = getEntity(resultSet);
                Genre genre = new Genre();
                genre.setId(artistGenre.getId_genre());
                genre.setName(resultSet.getString("name"));
                artistGenre.setGenre(genre);
                return artistGenre;
            }
        );        
    }
    
    @Override
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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {genre.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                ArtistGenre artistGenre = getEntity(resultSet);
                Artist artist = new Artist();
                artist.setId(artistGenre.getId_artist());
                artist.setName(resultSet.getString("name"));
                artist.setRating(resultSet.getInt("rating"));
                artistGenre.setArtist(artist);
                return artistGenre;
            }
        );
    };
    
    @Override
    public void deleteByArtist(Artist artist) {
        String prepareQuery = "delete from " + getTableName() + " where id_artist = ?"; 
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
			prepareQuery,
			new int[] {Types.INTEGER}
        );
		int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(new Object[] {artist.getId()}));
        if (count != 1) {
			throw new DeleteFailedException("Cannot delete ArtistGenre");
        }   
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    @Override
    public int countArtistsByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from artist_genre "
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
