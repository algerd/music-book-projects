
package db.repository;

import db.entity.Instrument;
import db.entity.Musician;
import java.util.List;

public interface MusicianRepository extends CrudRepository<Musician> {
    
    List<Musician> selectMusuciansByInstrument(Instrument instrument);
    
}
