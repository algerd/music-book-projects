
package com.algerd.musicbookspringmaven.repository.impl;

import com.algerd.musicbookspringmaven.dbDriver.impl.CrudRepositoryImpl;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.repository.ArtistRepository;

public class ArtistRepositoryImpl extends CrudRepositoryImpl<Artist> implements ArtistRepository {   
               
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
