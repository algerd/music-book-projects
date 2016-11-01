package mvc.artist.controller;

import db.entity.ArtistReference;
import db.entity.Entity;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import core.DialogController;
import core.Main;
import utils.Helper;

public class ArtistReferenceDialogController implements Initializable, DialogController {

    private Stage dialogStage;
    private ArtistReference artistReference;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField referenceTextField;       
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
            Main.getInstance().getDbLoader().getArtistReferenceTable().save(artistReference);
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
