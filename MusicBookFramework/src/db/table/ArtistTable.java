
package db.table;

import db.entity.Artist;

public class ArtistTable extends Table<Artist> {   
    
    public ArtistTable() {
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
