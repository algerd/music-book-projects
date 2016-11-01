
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.repository.InstrumentRepository;

public class InstrumentRepositoryImpl extends CrudRepositoryImpl<Instrument> implements InstrumentRepository {
    
    public InstrumentRepositoryImpl() { 
        super("instrument");
    } 

}
