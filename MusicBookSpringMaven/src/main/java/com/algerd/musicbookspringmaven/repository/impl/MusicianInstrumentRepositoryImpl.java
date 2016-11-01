
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.repository.MusicianInstrumentRepository;
import java.sql.ResultSet;
import java.util.List;

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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, musician.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianInstrument musicianInstrument = fill(resultSet);
            Instrument instrument = new Instrument();
            instrument.setId(musicianInstrument.getId_instrument());
            instrument.setName(resultSet.getString(4));
            musicianInstrument.setInstrument(instrument);
            return musicianInstrument;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
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
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, instrument.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            MusicianInstrument musicianInstrument = fill(resultSet);
            Musician musician = new Musician();
            musician.setId(musicianInstrument.getId_musician());
            musician.setName(resultSet.getString(4));
            musician.setRating(resultSet.getInt(5));
            musicianInstrument.setMusician(musician);
            return musicianInstrument;
        };
        return super.executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
    // Вернуть количество музыкантов с инструментом instrument
    @Override
    public int countMusiciansByInstrument(Instrument instrument) {
        String prepareQuery = 
            "select count(id) "
                + "from musician_instrument "
                + "where id_instrument = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, instrument.getId()); 
        };       
        return super.fetchIntRow(prepareQuery, prepareSetter);
    }
    
    @Override
    public void deleteByMusician(Musician musician) {
        String prepareQuery = "delete from " + getTableName() + " where id_musician = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, musician.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }

}
