
package db.repository.impl;

import db.entity.Artist;
import db.repository.ArtistRepository;

public class ArtistRepositoryImpl extends CrudRepositoryImpl<Artist> implements ArtistRepository {   
    
    public ArtistRepositoryImpl() {
        super("artist");
    }           
    
    @Override
    public void delete(Artist entity) {
        // запретить удаление записи с id = 1 (Unknown artist)
        if (entity.getId() != 1) {
            super.delete(entity);
        }      
    }
    
    @Override 
    public void save(Artist entity) {
        // запретить добавление/редактирование записи с id = 1 (Unknown artist)
        if (entity.getId() != 1) {
            super.save(entity);
        }
    } 
    
}
