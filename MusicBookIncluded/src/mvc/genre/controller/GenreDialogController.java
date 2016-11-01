
package mvc.genre.controller;

import utils.Helper;
import db.entity.Genre;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import main.Main;

public class GenreDialogController implements Initializable {

    private Stage dialogStage;
    private Genre genre;
    private boolean edit;
    
    @FXML
    private TextField nameField;
    @FXML
    private TextArea commentTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameField, 255);  
        Helper.limitTextInput(commentTextArea, 1000);  
    } 
    
    /**
     * Добавить в список объектов.
     */
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            genre.setName(nameField.getText().trim());
            genre.setDescription(commentTextArea.getText()); 
            
            Main.main.getLoader().getGenreTable().save(genre);
            dialogStage.close();
            edit = false;
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
        if (!edit && !Main.main.getLoader().getGenreTable().isUniqueName(nameField.getText())) {
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
    
    public void setAddDialog() {
        genre = new Genre();
    }
       
    public void setEditDialog(Genre gnr) {
        edit = true;
        genre = gnr;
        nameField.setText(genre.getName());
        commentTextArea.setText(genre.getDescription());
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
}
