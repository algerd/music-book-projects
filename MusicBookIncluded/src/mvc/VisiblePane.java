package mvc;

import db.entity.Entity;

public interface VisiblePane<T extends Entity> {
    
    void show(T entity);
    
    void hide();
    
}
