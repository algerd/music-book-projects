
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianGroup;
import com.algerd.musicbookspringmaven.repository.MusicianGroupRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class MusicianGroupRepositoryImpl extends CrudRepositoryImpl<MusicianGroup> implements MusicianGroupRepository {
    
    @Override
    public List<MusicianGroup> selectJoinByMusician(Musician musician) {
        String prepareQuery = 
            "select "
                + "musician_group.id, "
                + "musician_group.id_musician, "
                + "musician_group.id_artist, "
                + "musician_group.start_date, "
                + "musician_group.end_date, "
                + "artist.name, "
                + "artist.rating "
            + "from musician_group "
                + "left join artist on musician_group.id_artist = artist.id "
            + "where musician_group.id_musician = ?"; 
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {musician.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianGroup musicianGroup = getEntity(resultSet);
                Artist artist = new Artist();
                artist.setId(musicianGroup.getId_artist());
                artist.setName(resultSet.getString(6));
                artist.setRating(resultSet.getInt(7));
                musicianGroup.setArtist(artist);
                return musicianGroup;
            }
        );
    }
    
    @Override
    public List<MusicianGroup> selectJoinByArtist(Artist artist) {
        String prepareQuery = 
            "select "
                + "musician_group.id, "
                + "musician_group.id_musician, "
                + "musician_group.id_artist, "
                + "musician_group.start_date, "
                + "musician_group.end_date, "
                + "musician.name, "
                + "musician.rating "
            + "from musician_group "
                + "left join musician on musician_group.id_musician = musician.id "
            + "where musician_group.id_artist = ?";  
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {artist.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianGroup musicianGroup = getEntity(resultSet);
                Musician musician = new Musician();
                musician.setId(musicianGroup.getId_musician());
                musician.setName(resultSet.getString(6));
                musician.setRating(resultSet.getInt(7));
                musicianGroup.setMusician(musician);
                return musicianGroup;
            }
        );
    }
    
    @Override
    public boolean containsMusicianArtist(Musician musician, Artist artist) {
        String prepareQuery = "select count(id) from " + getTableName() + " where id_musician = ? and id_artist = ?";
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER, Types.INTEGER}    
        );
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {musician.getId(), artist.getId()}), 
            (ResultSet rs) -> rs.getInt(1)
        ) > 0;
    }
  
}
