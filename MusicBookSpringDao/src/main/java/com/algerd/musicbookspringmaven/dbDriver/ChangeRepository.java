
package com.algerd.musicbookspringmaven.dbDriver;

import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;

public interface ChangeRepository<T extends Entity> {
    
    ObjectProperty updatedProperty();
    ObjectProperty deletedProperty();
    ObjectProperty addedProperty();   
       
    void addChangeListener(ChangeListener<? super WrapChangedEntity<T>> listener, Object object);
    void addInsertListener(ChangeListener<? super WrapChangedEntity<T>> listener, Object object);
    void addUpdateListener(ChangeListener<? super WrapChangedEntity<T>> listener, Object object);
    void addDeleteListener(ChangeListener<? super WrapChangedEntity<T>> listener, Object object);
       
    void clearChangeListeners();
    void clearInsertListeners();
    void clearUpdateListeners();
    void clearDeleteListeners();
    
    void clearChangeListeners(Object object);
    void clearInsertListeners(Object object);
    void clearUpdateListeners(Object object);
    void clearDeleteListeners(Object object);
    
    void removeChangeListener(ChangeListener<? super WrapChangedEntity<T>> listener);
    void removeInsertListener(ChangeListener<? super WrapChangedEntity<T>> listener);
    void removeUpdateListener(ChangeListener<? super WrapChangedEntity<T>> listener);
    void removeDeleteListener(ChangeListener<? super WrapChangedEntity<T>> listener);
    
}
