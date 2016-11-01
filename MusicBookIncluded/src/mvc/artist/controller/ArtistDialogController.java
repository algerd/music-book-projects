
package mvc.artist.controller;

import utils.Helper;
import db.entity.Artist;
import db.entity.Genre;
import helper.inputImageBox.DialogImageBoxController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import main.Main;
import main.Params;

public class ArtistDialogController implements Initializable {
    
    private Stage dialogStage;
    private Artist artist;
    private boolean edit;
       
    private final IntegerProperty rating = new SimpleIntegerProperty();
       
    @FXML
    private TextField nameTextField;
    @FXML
    private ChoiceBox<Genre> genreChoiceBox;
    @FXML
    private Spinner<Integer> ratingSpinner;    
    @FXML
    private TextArea commentTextArea;
    
    @FXML
    private AnchorPane includedDialogImageBox;
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Helper.initIntegerSpinner(ratingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);
        Helper.initEntityChoiceBox(genreChoiceBox);
        genreChoiceBox.getItems().addAll(Main.main.getLoader().getGenreTable().getEntities()); 
        // ограничить поле ввода указав вторым параметром предельное количество символов
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage);
    } 
          
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            artist.setName(nameTextField.getText().trim());
            artist.setIdGenre(genreChoiceBox.getValue().getId());
            artist.setGenre(genreChoiceBox.getValue());        
            artist.setRating(getRating());
            artist.setDescription(commentTextArea.getText().trim()); 
            
            Main.main.getLoader().getArtistTable().save(artist);
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
                artist.setChangedImage(!artist.isChangedImage());
            }
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
        
        if (nameTextField.getText() == null || nameTextField.getText().trim().equals("")) {
            errorMessage += "Введите имя артиста!\n"; 
        }
        if (!edit && !Main.main.getLoader().getArtistTable().isUniqueName(nameTextField.getText())) {
            errorMessage += "Такой артист уже есть!\n";
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
        artist = new Artist();
        // id = 1 "Unknown" genre "Unknown" жанра (id = 1)
        Genre genre = Main.main.getLoader().getGenreTable().getEntityWithId(1);
        genreChoiceBox.getSelectionModel().select(genre);
        includedDialogImageBoxController.setEntity(artist);
    }
       
    public void setEditDialog(Artist art) {
        edit = true;
        artist = art;
        nameTextField.setText(artist.getName());
        genreChoiceBox.getSelectionModel().select(artist.getGenre());
        ratingSpinner.getValueFactory().setValue(artist.getRating());
        commentTextArea.setText(artist.getDescription());
        
        includedDialogImageBoxController.setEntity(artist);
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());  
    }
            
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public int getRating() {
        return rating.get();
    }
    public void setRating(int value) {
        rating.set(value);
    }
    public IntegerProperty ratingProperty() {
        return rating;
    }
    
    public AnchorPane getIncludedDialogImageBox() {
        return includedDialogImageBox;
    }
    public DialogImageBoxController getIncludedDialogImageBoxController() {
        return includedDialogImageBoxController;
    } 
          
}
