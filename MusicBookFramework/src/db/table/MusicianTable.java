
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Entity;
import db.entity.Instrument;
import db.entity.Musician;
import java.sql.ResultSet;
import java.util.List;

public class MusicianTable extends Table<Musician>  {
   
    public MusicianTable() { 
        super("musician");
    }
    
    // вернуть ссылки, у которых есть инструмент instrument
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
