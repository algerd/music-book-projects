package com.algerd.musicbookspringmaven.controller;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import javafx.scene.layout.AnchorPane;

public interface PaneController {
    
    void show(Entity entity);
   
    AnchorPane getView();
                
    void loadFxml();
    
}
