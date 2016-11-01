
package com.algerd.musicbookspringmaven.service;

import com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public interface ContextMenuService {
    /**
     * Добавить элемент меню и задать ему значение сущности entity
     * @param itemType Тип элемента меню.
     * @param entity Значение для элемента меню.
     */
    void add(ContextMenuItemType itemType, Entity entity);
    
    void add(ContextMenuItemType itemType);
    
    void show(Pane pane, MouseEvent mouseEvent);
    
    void clear();
    
    ContextMenu getContextMenu();
}
