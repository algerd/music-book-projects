
package db.repository;

import db.entity.Instrument;
import db.entity.Musician;
import db.entity.MusicianInstrument;
import java.util.List;

public interface MusicianInstrumentRepository extends CrudRepository<MusicianInstrument> {
    
    List<MusicianInstrument> selectJoinByMusician(Musician musician);
    List<MusicianInstrument> selectJoinByInstrument(Instrument instrument);
    int countMusiciansByInstrument(Instrument instrument);
    void deleteByMusician(Musician musician);
    
}
