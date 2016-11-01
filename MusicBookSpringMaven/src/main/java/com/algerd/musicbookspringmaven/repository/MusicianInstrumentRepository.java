
package com.algerd.musicbookspringmaven.repository;

import com.algerd.musicbookspringmaven.dbDriver.CrudRepository;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import java.util.List;

public interface MusicianInstrumentRepository extends CrudRepository<MusicianInstrument> {
    
    List<MusicianInstrument> selectJoinByMusician(Musician musician);
    List<MusicianInstrument> selectJoinByInstrument(Instrument instrument);
    int countMusiciansByInstrument(Instrument instrument);
    void deleteByMusician(Musician musician);
    
}
