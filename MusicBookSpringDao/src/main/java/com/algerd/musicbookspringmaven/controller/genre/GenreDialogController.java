
package com.algerd.musicbookspringmaven.controller.genre;

import com.algerd.musicbookspringmaven.controller.BaseDialogController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.controller.helper.inputImageBox.DialogImageBoxController;

public class GenreDialogController extends BaseDialogController {
    
    private Genre genre;
    
    @FXML
    private AnchorPane view;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private AnchorPane includedDialogImageBox;
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameField, 255);  
        Helper.limitTextInput(commentTextArea, 1000); 
        includedDialogImageBoxController.setStage(dialogStage);
    } 
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            genre.setName(nameField.getText().trim());
            genre.setDescription(commentTextArea.getText());            
            if (!edit) {
                repositoryService.getGenreRepository().save(genre);
            }
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                repositoryService.getGenreRepository().save(genre);
            } else {
                requestPageService.genrePane(genre);
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
            errorMessage += "Введите название жанра!\n"; 
        }      
        if (!edit && !repositoryService.getGenreRepository().isUniqueColumnValue("name", nameField.getText())) {
            errorMessage += "Такой жанр уже есть!\n";
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
        includedDialogImageBoxController.setEntity(genre);
    } 
    
    @Override
    protected void edit() {
        edit = true;
        add();
        nameField.setText(genre.getName());
        commentTextArea.setText(genre.getDescription());
        
        includedDialogImageBoxController.setEntity(genre);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());
    }
        
    @Override
    public void setEntity(Entity entity) {
        genre = (Genre) entity;
        super.setEntity(entity);    
    }
    
    public AnchorPane getIncludedDialogImageBox() {
        return includedDialogImageBox;
    }
    public DialogImageBoxController getIncludedDialogImageBoxController() {
        return includedDialogImageBoxController;
    } 
    
}
