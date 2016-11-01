package com.algerd.musicbookspringmaven.controller.artist;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.controller.DialogController;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.utils.Helper;

public class ArtistReferenceDialogController extends BaseController implements Initializable, DialogController {

    private Stage dialogStage;
    private ArtistReference artistReference;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField referenceTextField;       
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(referenceTextField, 255);
    }  
    
    private void edit() {      
        nameTextField.setText(artistReference.getName());
        referenceTextField.setText(artistReference.getReference());                             
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
    
    private boolean isInputValid() {
        String errorMessage = "";       
        if (nameTextField.getText().trim() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите название ссылки!\n"; 
        }      
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);           
            alert.showAndWait();           
            return false;
        }
    }
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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
