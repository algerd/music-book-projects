
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.dbDriver.exception.DeleteFailedException;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.MusicianInstrumentRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

public class MusicianInstrumentRepositoryImpl extends CrudRepositoryImpl<MusicianInstrument> implements MusicianInstrumentRepository {
  
    @Override
    public List<MusicianInstrument> selectJoinByMusician(Musician musician) {
        String prepareQuery = 
            "select "
                + "musician_instrument.id, "
                + "musician_instrument.id_instrument, "
                + "musician_instrument.id_musician, "              
                + "instrument.name "
            + "from musician_instrument "
                + "left join instrument on musician_instrument.id_instrument = instrument.id "
            + "where musician_instrument.id_musician = ?";  
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {musician.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianInstrument musicianInstrument = getEntity(resultSet);
                Instrument instrument = new Instrument();
                instrument.setId(musicianInstrument.getId_instrument());
                instrument.setName(resultSet.getString(4));
                musicianInstrument.setInstrument(instrument);
                return musicianInstrument;
            }
        );
    }
    
    @Override
    public List<MusicianInstrument> selectJoinByInstrument(Instrument instrument) {
        String prepareQuery = 
            "select "
                + "musician_instrument.id, "
                + "musician_instrument.id_instrument, "
                + "musician_instrument.id_musician, "             
                + "musician.name, "
                + "musician.rating "
            + "from musician_instrument "
                + "left join musician on musician_instrument.id_musician = musician.id "
            + "where musician_instrument.id_instrument = ?";
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {instrument.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                MusicianInstrument musicianInstrument = getEntity(resultSet);
                Musician musician = new Musician();
                musician.setId(musicianInstrument.getId_musician());
                musician.setName(resultSet.getString(4));
                musician.setRating(resultSet.getInt(5));
                musicianInstrument.setMusician(musician);
                return musicianInstrument;
            }
        );
    }

    @Override
    public int countMusiciansByInstrument(Instrument instrument) {
        String prepareQuery = 
            "select count(id) "
                + "from musician_instrument "
                + "where id_instrument = ?";       
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        ); 
        return jdbcTemplate.query(pscf.newPreparedStatementCreator(
            new Object[] {instrument.getId()}), 
            (ResultSet rs) -> rs.getInt(1)
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
			throw new DeleteFailedException("Cannot delete MusicianInstrument");
        }   
        setDeleted(new WrapChangedEntity<>(null, null));
    }

}
