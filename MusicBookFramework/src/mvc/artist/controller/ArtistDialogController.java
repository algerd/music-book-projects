
package mvc.artist.controller;

import utils.Helper;
import db.entity.Artist;
import db.entity.ArtistGenre;
import db.entity.Entity;
import db.entity.Genre;
import helper.inputImageBox.DialogImageBoxController;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import core.DialogController;
import core.Main;
import core.Params;
import core.RequestPage;
import helper.choiceCheckBox.ChoiceCheckBoxController;

public class ArtistDialogController implements Initializable, DialogController {
    
    private Stage dialogStage;
    private Artist artist;
    private boolean edit;      
    private final IntegerProperty rating = new SimpleIntegerProperty();
          
    @FXML
    private AnchorPane dialogPane;
    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> ratingSpinner;    
    @FXML
    private TextArea commentTextArea;
    
    @FXML
    private AnchorPane includedDialogImageBox;
    @FXML
    private DialogImageBoxController includedDialogImageBoxController;
    @FXML   
    private AnchorPane includedChoiceCheckBox;
    @FXML
    private ChoiceCheckBoxController<Genre> includedChoiceCheckBoxController;    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        Helper.initIntegerSpinner(ratingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, rating);      
        Helper.limitTextInput(nameTextField, 255);
        Helper.limitTextInput(commentTextArea, 1000);
        includedDialogImageBoxController.setStage(dialogStage); 
        
        includedChoiceCheckBoxController.setMainPane(dialogPane);
        includedChoiceCheckBoxController.getChoiceCheckBox().setPrefWidth(250.0);
    }
    
    private void initGenreChoiceCheckBox() {
        List<Genre> artistGenres = new ArrayList<>();
        if (edit) {        
            Main.getInstance().getDbLoader().getArtistGenreTable().selectJoinByArtist(artist).stream().forEach(artistGenre -> {
                artistGenres.add(artistGenre.getGenre());
            });
        }
        Map<Genre, ObservableValue<Boolean>> map = new HashMap<>();
        Main.getInstance().getDbLoader().getGenreTable().select().stream().forEach(genre -> {                     
            map.put(genre, new SimpleBooleanProperty(artistGenres.contains(genre)));
        });
        includedChoiceCheckBoxController.addItems(map);       
    }
     
    @FXML
    private void handleOkButton() {
        if (isInputValid()) { 
            artist.setName(nameTextField.getText().trim());
            artist.setRating(getRating());
            artist.setDescription(commentTextArea.getText().trim());           
            /*
            Если артист создаётся снуля, то сначала надо его сохранить,
            получить его id (в объекте artist) и потом сохранить связку artistGenre
            */
            if (!edit) {
                Main.getInstance().getDbLoader().getArtistTable().save(artist);
            } else {           
                // Cначала удалить все жанры из бд для артиста, а потом добавить
                Main.getInstance().getDbLoader().getArtistGenreTable().deleteByArtist(artist);
            }    
            // Извлечь жанры из списка и сохранить их в связке связанные с артистом             
            for (Genre genre : includedChoiceCheckBoxController.getItemMap().keySet()) {
                ObservableValue<Boolean> value = includedChoiceCheckBoxController.getItemMap().get(genre);    
                if (value.getValue()) {
                    ArtistGenre artistGenre = new ArtistGenre();
                    artistGenre.setId_artist(artist.getId());
                    artistGenre.setId_genre(genre.getId());                  
                    Main.getInstance().getDbLoader().getArtistGenreTable().save(artistGenre);
                }
            }                       
            if (includedDialogImageBoxController.isChangedImage()) {
                includedDialogImageBoxController.saveImage();
                includedDialogImageBoxController.setChangedImage(false);                              
            }
            if (edit) {
                Main.getInstance().getDbLoader().getArtistTable().save(artist);
            } else {
                RequestPage.ARTIST_PANE.load(artist);
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
        if (!edit && !Main.getInstance().getDbLoader().getArtistTable().isUniqueColumnValue("name", nameTextField.getText())) {
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
      
    private void add() {
        includedDialogImageBoxController.setEntity(artist);
        initGenreChoiceCheckBox();
    }
       
    private void edit() { 
        edit = true;
        nameTextField.setText(artist.getName());
        ratingSpinner.getValueFactory().setValue(artist.getRating());
        commentTextArea.setText(artist.getDescription());
        add();
        includedDialogImageBoxController.setImage(includedDialogImageBoxController.getImageFile());
    }
            
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setEntity(Entity entity) {
        if (entity != null && entity instanceof Artist) {
            artist = (Artist) entity;
            if (artist.getId() != 0) {
                dialogStage.setTitle("Edit Artist"); 
                edit();
            } 
            else {
                dialogStage.setTitle("Add Artist");
                add();
            }
        }
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
