
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import java.util.List;

public interface MusicianRepository extends CrudRepository<Musician> {
    
    List<Musician> selectMusuciansByInstrument(Instrument instrument);
    
}
