
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.dbDriver.exception.DeleteFailedException;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.MusicianGenreRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class MusicianGenreRepositoryImpl extends CrudRepositoryImpl<MusicianGenre> implements MusicianGenreRepository {

    @Override
    public List<MusicianGenre> selectJoinByMusician(Musician musician) {
        String prepareQuery = 
            "select "
                + "musician_genre.id, "
                + "musician_genre.id_genre, "
                + "musician_genre.id_musician, "              
                + "genre.name "
            + "from musician_genre "
                + "left join genre on musician_genre.id_genre = genre.id "
            + "where musician_genre.id_musician = ?";  
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {musician.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianGenre musicianGenre = getEntity(resultSet);
                Genre genre = new Genre();
                genre.setId(musicianGenre.getId_genre());
                genre.setName(resultSet.getString(4));
                musicianGenre.setGenre(genre);
                return musicianGenre;
            }
        );
    }
    
    @Override
    public List<MusicianGenre> selectJoinByGenre(Genre genre) {
        String prepareQuery = 
            "select "
                + "musician_genre.id, "
                + "musician_genre.id_genre, "
                + "musician_genre.id_musician, "             
                + "musician.name, "
                + "musician.rating "
            + "from musician_genre "
                + "left join musician on musician_genre.id_musician = musician.id "
            + "where musician_genre.id_genre = ?";
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {genre.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianGenre musicianGenre = getEntity(resultSet);
                Musician musician = new Musician();
                musician.setId(musicianGenre.getId_musician());
                musician.setName(resultSet.getString(4));
                musician.setRating(resultSet.getInt(5));
                musicianGenre.setMusician(musician);
                return musicianGenre;
            }
        );
    }
      
    @Override
    public void deleteByMusician(Musician musician) {
        String prepareQuery = "delete from " + getTableName() + " where id_musician = ?"; 
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
			prepareQuery,
			new int[] {Types.INTEGER}
        );
		int count = jdbcTemplate.update(pscf.newPreparedStatementCreator(new Object[] {musician.getId()}));
        if (count != 1) {
			throw new DeleteFailedException("Cannot delete MusicianGenre");
        }   
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    @Override
    public int countMusiciansByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from musician_genre "
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
