
package db.repository.impl;

import db.entity.Instrument;
import db.repository.InstrumentRepository;

public class InstrumentRepositoryImpl extends CrudRepositoryImpl<Instrument> implements InstrumentRepository {
    
    public InstrumentRepositoryImpl() { 
        super("instrument");
    } 

}
