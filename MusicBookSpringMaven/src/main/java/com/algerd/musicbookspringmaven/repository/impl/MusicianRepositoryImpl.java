
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.dbDriver.Extractor;
import com.algerd.musicbookspringmaven.dbDriver.PrepareSetter;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.repository.MusicianRepository;
import java.sql.ResultSet;
import java.util.List;

public class MusicianRepositoryImpl extends CrudRepositoryImpl<Musician> implements MusicianRepository {
  
    @Override
    public List<Musician> selectMusuciansByInstrument(Instrument instrument) {
        String prepareQuery = 
            "select "
                + "musician.id, "             
                + "musician.name, "
                + "musician.rating "
            + "from musician_instrument "
                + "left join musician on musician_instrument.id_musician = musician.id "
            + "where musician_instrument.id_instrument = ?";
        
        PrepareSetter prepareSetter = prepareStmt -> {
            prepareStmt.setInt(1, instrument.getId()); 
        };
        
        Extractor<ResultSet, Entity> extractor = resultSet -> {
            Musician musician = new Musician();
            musician.setId(resultSet.getInt(1));
            musician.setName(resultSet.getString(2));
            musician.setRating(resultSet.getInt(3));
            return musician;
        };
        return executePrepareQuery(prepareQuery, prepareSetter, extractor);
    }
    
}
