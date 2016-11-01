package com.algerd.musicbookspringmaven.controller;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import javafx.stage.Stage;

public interface DialogController extends Controller {
    
    void setStage(Stage stage);
    
    void setEntity(Entity entity);
         
}
