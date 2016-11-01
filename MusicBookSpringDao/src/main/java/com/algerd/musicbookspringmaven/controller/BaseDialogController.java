
package com.algerd.musicbookspringmaven.controller;

import com.algerd.musicbookspringmaven.dbDriver.Entity;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class BaseDialogController extends BaseAwareController implements DialogController {
     
    protected Stage dialogStage;
    protected Entity entity;
    protected boolean edit;
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setEntity(Entity entity) {
        if (entity != null) {
            this.entity = entity;
            if (entity.getId() != 0) {
                dialogStage.setTitle("Edit"); 
                edit();
            } 
            else {
                dialogStage.setTitle("Add");
                add();
            }
        }
    }
    
    protected void errorMessage(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Invalid Fields");
        alert.setHeaderText("Please correct invalid fields");
        alert.setContentText(errorMessage);           
        alert.showAndWait();  
    }
    
    protected abstract void add();
    
    protected abstract void edit();
    
    protected abstract boolean isInputValid();
    
}
