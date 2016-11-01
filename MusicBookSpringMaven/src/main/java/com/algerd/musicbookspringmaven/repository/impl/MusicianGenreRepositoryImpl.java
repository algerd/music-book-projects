
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.MusicianGenreRepository;
import java.sql.ResultSet;
import java.util.List;

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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianGenre musicianGenre = fill(resultSet);
            Genre genre = new Genre();
            genre.setId(musicianGenre.getId_genre());
            genre.setName(resultSet.getString(4));
            musicianGenre.setGenre(genre);
            return musicianGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianGenre musicianGenre = fill(resultSet);
            Musician musician = new Musician();
            musician.setId(musicianGenre.getId_musician());
            musician.setName(resultSet.getString(4));
            musician.setRating(resultSet.getInt(5));
            musicianGenre.setMusician(musician);
            return musicianGenre;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
      
    @Override
    public void deleteByMusician(Musician musician) {
        String prepareQuery = "delete from " + getTableName() + " where id_musician = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, musician.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }
    
    @Override
    public int countMusiciansByGenre(Genre genre) {
        String prepareQuery = 
            "select count(id) "
                + "from musician_genre "
                + "where id_genre = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, genre.getId()); 
        };       
        return super.fetchIntRow(prepareQuery, prepareSetter);
    }
    
}
