package mvc.musician.controller;

import utils.Helper;
import db.entity.Artist;
import db.entity.Musician;
import db.entity.MusicianGroup;
import java.net.URL;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import main.Main;

public class MusicianGroupDialogController implements Initializable {
    
    private Stage dialogStage;
    private MusicianGroup musicianGroup;    
    
    @FXML
    private ChoiceBox<Musician> musicianChoiceBox;
    @FXML
    private ChoiceBox<Artist> artistChoiceBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Helper.initEntityChoiceBox(musicianChoiceBox);
        Helper.initEntityChoiceBox(artistChoiceBox);
        musicianChoiceBox.getItems().addAll(Main.main.getLoader().getMusicianTable().getEntities());
        artistChoiceBox.getItems().addAll(Main.main.getLoader().getArtistTable().getEntities());
        Helper.initDatePicker(startDatePicker, true);
        Helper.initDatePicker(endDatePicker, true);       
    }  
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            musicianGroup.setIdArtist(artistChoiceBox.getValue().getId());
            musicianGroup.setIdMusician(musicianChoiceBox.getValue().getId());
            musicianGroup.setStartDate(startDatePicker.getEditor().getText());
            musicianGroup.setEndDate(endDatePicker.getEditor().getText());
            Main.main.getLoader().getMusicianGroupTable().save(musicianGroup);
            dialogStage.close();
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    public void setEditDialog(MusicianGroup mg) {
        this.musicianGroup = mg;      
        musicianChoiceBox.getSelectionModel().select(Main.main.getLoader().getMusicianTable().getEntityWithId(musicianGroup.getIdMusician()));
        artistChoiceBox.getSelectionModel().select(Main.main.getLoader().getArtistTable().getEntityWithId(musicianGroup.getIdArtist()));
        startDatePicker.setValue(startDatePicker.getConverter().fromString(musicianGroup.getStartDate()));
        endDatePicker.setValue(endDatePicker.getConverter().fromString(musicianGroup.getEndDate()));            
    }
    
    public void setAddDialog(MusicianGroup mg) {
        this.musicianGroup = mg;      
        if (musicianGroup.getIdMusician() != 0) {
            musicianChoiceBox.getSelectionModel().select(Main.main.getLoader().getMusicianTable().getEntityWithId(musicianGroup.getIdMusician()));
        }
        if (musicianGroup.getIdArtist() != 0) {
            artistChoiceBox.getSelectionModel().select(Main.main.getLoader().getArtistTable().getEntityWithId(musicianGroup.getIdArtist()));
        }     
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        
        if (musicianChoiceBox.getValue() == null) {
            errorMessage += "Выберите музыканта из списка \n";
        }       
        if (artistChoiceBox.getValue() == null) {
            errorMessage += "Выберите группу из списка \n";
        }
        
        try { 
            startDatePicker.getConverter().fromString(startDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Неправильно введён формат Start Date " + startDatePicker.getEditor().getText() +". Надо mm.dd.yyyy \n";
        }     
        try { 
            endDatePicker.getConverter().fromString(endDatePicker.getEditor().getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Неправильно введён формат End Date " + endDatePicker.getEditor().getText() +". Надо mm.dd.yyyy \n";
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
