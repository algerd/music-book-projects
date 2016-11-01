
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.repository.MusicianRepository;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

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
        
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
            prepareQuery, 
            new int[] {Types.INTEGER}    
        );      
        return jdbcTemplate.query(
            pscf.newPreparedStatementCreator(new Object[] {instrument.getId()}), 
            (ResultSet resultSet, int rowNum) -> {
                Musician musician = new Musician();
                musician.setId(resultSet.getInt(1));
                musician.setName(resultSet.getString(2));
                musician.setRating(resultSet.getInt(3));
                return musician;
            }
        );
    }
    
}
