
package db.table;

import db.driver.Extractor;
import db.driver.PrepareSetter;
import db.entity.Entity;
import db.entity.Instrument;
import db.entity.Musician;
import db.entity.MusicianInstrument;
import db.entity.WrapChangedEntity;
import java.sql.ResultSet;
import java.util.List;

public class MusicianInstrumentTable extends Table<MusicianInstrument> {
    
    public MusicianInstrumentTable() {
        super("musician_instrument");
    }
    
    // вернуть ссылки, у которых есть музыкант musician
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
    
    // вернуть ссылки, у которых есть инструмент instrument
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
    
    // удалить все инструменты у Musician
    public void deleteByMusician(Musician musician) {
        String prepareQuery = "delete from " + tableName + " where id_musician = ?"; 
        PrepareSetter prepareSetter = prepareStmt -> prepareStmt.setInt(1, musician.getId());
        executePrepareUpdate(prepareQuery, prepareSetter);      
        setDeleted(new WrapChangedEntity<>(null, null));
    }

}
