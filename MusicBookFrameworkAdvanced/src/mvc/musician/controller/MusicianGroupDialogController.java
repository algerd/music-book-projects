package mvc.musician.controller;

import utils.Helper;
import db.entity.Artist;
import db.driver.Entity;
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
import core.DialogController;
import core.Main;
import db.repository.RepositoryService;

public class MusicianGroupDialogController implements Initializable, DialogController {
    
    //@Inject
    private RepositoryService repositoryService;
    
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
        
        Helper.initEntityChoiceBox(musicianChoiceBox);
        Helper.initEntityChoiceBox(artistChoiceBox);
        musicianChoiceBox.getItems().addAll(repositoryService.getMusicianRepository().selectAll());
        artistChoiceBox.getItems().addAll(repositoryService.getArtistRepository().selectAll());
        Helper.initDatePicker(startDatePicker, true);
        Helper.initDatePicker(endDatePicker, true);       
    }  
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Musician musician = musicianChoiceBox.getValue();
            Artist artist = artistChoiceBox.getValue();
            if (!repositoryService.getMusicianGroupRepository().containsMusicianArtist(musician, artist)) {
                musicianGroup.setId_artist(artistChoiceBox.getValue().getId());
                musicianGroup.setId_musician(musicianChoiceBox.getValue().getId());
                musicianGroup.setStart_date(startDatePicker.getEditor().getText());
                musicianGroup.setEnd_date(endDatePicker.getEditor().getText());
                repositoryService.getMusicianGroupRepository().save(musicianGroup);
            }    
            dialogStage.close();
        }
    }
    
    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    public void edit() {     
        add();
        startDatePicker.setValue(startDatePicker.getConverter().fromString(musicianGroup.getStart_date()));
        endDatePicker.setValue(endDatePicker.getConverter().fromString(musicianGroup.getEnd_date()));            
    }
    
    public void add() {     
        if (musicianGroup.getId_musician() != 0) {
            musicianChoiceBox.getSelectionModel().select(repositoryService.getMusicianRepository().selectById(musicianGroup.getId_musician()));
        }
        if (musicianGroup.getId_artist() != 0) {
            artistChoiceBox.getSelectionModel().select(repositoryService.getArtistRepository().selectById(musicianGroup.getId_artist()));
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
            
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setEntity(Entity entity) {
        if (entity != null && entity instanceof MusicianGroup) {
            musicianGroup = (MusicianGroup) entity;
            if (musicianGroup.getId() != 0) {
                dialogStage.setTitle("Edit Musician In Group"); 
                edit();
            } 
            else {
                dialogStage.setTitle("Add Musician To Group");
                add();
            }
        }
    }
       
}
