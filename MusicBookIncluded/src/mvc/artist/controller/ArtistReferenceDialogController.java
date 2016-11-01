package mvc.artist.controller;

import db.entity.ArtistReference;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Main;
import utils.Helper;

public class ArtistReferenceDialogController implements Initializable {

    private Stage dialogStage;
    private ArtistReference artistReference;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField referenceTextField;       
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(referenceTextField, 255);
    }  
    
    public void setEditDialog(ArtistReference reference) {
        artistReference = reference;       
        nameTextField.setText(reference.getName());
        referenceTextField.setText(reference.getReference());                             
    }
    
    public void setAddDialog(ArtistReference reference) {
        artistReference = reference;               
    }
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            artistReference.setName(nameTextField.getText().trim());
            artistReference.setReference(referenceTextField.getText().trim());
            Main.main.getLoader().getArtistReferenceTable().save(artistReference);
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
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }   
    
}
