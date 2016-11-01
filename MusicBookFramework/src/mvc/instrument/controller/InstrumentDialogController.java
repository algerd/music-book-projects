package mvc.instrument.controller;

import core.DialogController;
import core.Main;
import core.RequestPage;
import db.entity.Entity;
import db.entity.Instrument;
import helper.inputImageBox.DialogImageBoxController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.Helper;

public class InstrumentDialogController implements Initializable, DialogController {

    private Stage dialogStage;
    private Instrument instrument;
    private boolean edit;
    
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private AnchorPane includedDialogImageBox;
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        includedDialogImageBoxController.setStage(dialogStage);
    } 
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            instrument.setName(nameField.getText().trim());
            instrument.setDescription(descriptionTextArea.getText());            
            if (!edit) {
                Main.getInstance().getDbLoader().getInstrumentTable().save(instrument);
            }
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                Main.getInstance().getDbLoader().getInstrumentTable().save(instrument);
            } else {
                RequestPage.INSTRUMENT_PANE.load(instrument);
            }
            dialogStage.close();
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
       private boolean isInputValid() {
        String errorMessage = "";       
        if (nameField.getText() == null || nameField.getText().trim().equals("")) {
            errorMessage += "Введите название инструмента!\n"; 
        }      
        if (!edit && !Main.getInstance().getDbLoader().getGenreTable().isUniqueColumnValue("name", nameField.getText())) {
            errorMessage += "Такой инструмент уже есть!\n";
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
       
    private void add() {  
        includedDialogImageBoxController.setEntity(instrument);
        Helper.limitTextInput(nameField, 255);  
        Helper.limitTextInput(descriptionTextArea, 1000); 
    } 
    
    private void edit() {
        edit = true;      
        nameField.setText(instrument.getName());
        descriptionTextArea.setText(instrument.getDescription());
        add();
        includedDialogImageBoxController.setEntity(instrument);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());
    }
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setEntity(Entity entity) {
        this.instrument = (Instrument) entity;
        if (instrument.getId() != 0) {
            dialogStage.setTitle("Edit Instrument");
            edit();
        } 
        else {
            dialogStage.setTitle("Add Instrument");
            add();
        }      
    }
    
    public AnchorPane getIncludedDialogImageBox() {
        return includedDialogImageBox;
    }
    public DialogImageBoxController getIncludedDialogImageBoxController() {
        return includedDialogImageBoxController;
    }    
    
}
