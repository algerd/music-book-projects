
package com.algerd.musicbookspringmaven.controller.genre;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.helper.inputImageBox.DialogImageBoxController;
import com.algerd.musicbookspringmaven.controller.DialogController;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.RequestPage;

public class GenreDialogController extends BaseController implements Initializable, DialogController {
    
    private Stage dialogStage;
    private Genre genre;
    private boolean edit;
    
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        
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
                RequestPage.GENRE_PANE.load(genre);
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
            errorMessage += "Введите название жанра!\n"; 
        }      
        if (!edit && !repositoryService.getGenreRepository().isUniqueColumnValue("name", nameField.getText())) {
            errorMessage += "Такой жанр уже есть!\n";
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
        includedDialogImageBoxController.setEntity(genre);
    } 
    
    private void edit() {
        edit = true;
        add();
        nameField.setText(genre.getName());
        commentTextArea.setText(genre.getDescription());
        
        includedDialogImageBoxController.setEntity(genre);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());
    }
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setEntity(Entity entity) {
        this.genre = (Genre) entity;
        if (genre.getId() != 0) {
            dialogStage.setTitle("Edit Genre");
            edit();
        } 
        else {
            dialogStage.setTitle("Add Genre");
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
