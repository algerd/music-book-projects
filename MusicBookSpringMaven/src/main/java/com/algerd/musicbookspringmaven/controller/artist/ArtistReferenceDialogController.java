package com.algerd.musicbookspringmaven.controller.artist;

import com.algerd.musicbookspringmaven.controller.BaseDialogController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.utils.Helper;
import javafx.scene.layout.AnchorPane;

public class ArtistReferenceDialogController extends BaseDialogController {

    private ArtistReference artistReference;
    
    @FXML
    private AnchorPane view;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField referenceTextField;       
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(referenceTextField, 255);
    }  
    
    @Override
    protected void edit() {      
        nameTextField.setText(artistReference.getName());
        referenceTextField.setText(artistReference.getReference());                             
    }
    
    @Override
    protected void add() {         
    }
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            artistReference.setName(nameTextField.getText().trim());
            artistReference.setReference(referenceTextField.getText().trim());
            repositoryService.getArtistReferenceRepository().save(artistReference);
            dialogStage.close();
        }
    }
   
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";       
        if (nameTextField.getText().trim() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите название ссылки!\n"; 
        }      
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            errorMessage(errorMessage);
            return false;
        }
    }
        
    @Override
    public void setEntity(Entity entity) {
        if (entity instanceof ArtistReference) {
            artistReference = (ArtistReference) entity;
            if (artistReference.getId() != 0) {
                dialogStage.setTitle("Edit Artist Reference"); 
            } 
            else {
                dialogStage.setTitle("Add Artist Reference");
            }
            edit();
        }
    }
    
}
