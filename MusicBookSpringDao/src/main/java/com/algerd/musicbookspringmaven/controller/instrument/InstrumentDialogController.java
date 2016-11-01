package com.algerd.musicbookspringmaven.controller.instrument;

import com.algerd.musicbookspringmaven.controller.BaseDialogController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.controller.helper.inputImageBox.DialogImageBoxController;
import com.algerd.musicbookspringmaven.utils.Helper;

public class InstrumentDialogController extends BaseDialogController {

    private Instrument instrument;
    
    @FXML
    private AnchorPane view;
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
                repositoryService.getInstrumentRepository().save(instrument);
            }
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                repositoryService.getInstrumentRepository().save(instrument);
            } else {
                requestPageService.instrumentPane(instrument);
            }
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
        if (nameField.getText() == null || nameField.getText().trim().equals("")) {
            errorMessage += "Введите название инструмента!\n"; 
        }      
        if (!edit && !repositoryService.getGenreRepository().isUniqueColumnValue("name", nameField.getText())) {
            errorMessage += "Такой инструмент уже есть!\n";
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
    protected void add() {  
        includedDialogImageBoxController.setEntity(instrument);
        Helper.limitTextInput(nameField, 255);  
        Helper.limitTextInput(descriptionTextArea, 1000); 
    } 
    
    @Override
    protected void edit() {
        edit = true;      
        nameField.setText(instrument.getName());
        descriptionTextArea.setText(instrument.getDescription());
        add();
        includedDialogImageBoxController.setEntity(instrument);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());
    }
       
    @Override
    public void setEntity(Entity entity) {
        instrument = (Instrument) entity;
        super.setEntity(entity);  
    }
    
    public AnchorPane getIncludedDialogImageBox() {
        return includedDialogImageBox;
    }
    public DialogImageBoxController getIncludedDialogImageBoxController() {
        return includedDialogImageBoxController;
    }    
    
}
