package mvc.musician.controller;

import core.DialogController;
import core.Main;
import db.entity.Album;
import db.entity.Artist;
import db.driver.Entity;
import db.entity.Musician;
import db.entity.MusicianAlbum;
import db.repository.RepositoryService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import utils.Helper;

public class MusicianAlbumDialogController implements Initializable, DialogController {
    
    //@Inject
    private RepositoryService repositoryService;
    
    private Stage dialogStage;
    private MusicianAlbum musicianAlbum;    
    
    @FXML
    private ChoiceBox<Musician> musicianChoiceBox;
    @FXML
    private ChoiceBox<Artist> artistChoiceBox;
    @FXML
    private ChoiceBox<Album> albumChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();  
        
        initMusicianChoiceBox();
        initArtistChoiceBox();
    }
    
    private void initMusicianChoiceBox() {
        Helper.initEntityChoiceBox(musicianChoiceBox);
        musicianChoiceBox.getItems().addAll(repositoryService.getMusicianRepository().selectAll());
        musicianChoiceBox.getSelectionModel().selectFirst();
    }
    
    private void initArtistChoiceBox() {
        Helper.initEntityChoiceBox(artistChoiceBox);
        artistChoiceBox.getItems().addAll(repositoryService.getArtistRepository().selectAll());
        artistChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changedArtistChoiceBox);
        artistChoiceBox.getSelectionModel().selectFirst();
    }
    
    /**
     * При выборе другого артиста - обновить список альбомов в ChoiceBox albumField.
     */
    private void changedArtistChoiceBox(ObservableValue<? extends Object> observable, Artist oldValue, Artist newValue) {          
        albumChoiceBox.getItems().clear();
        Artist artist = artistChoiceBox.getSelectionModel().getSelectedItem();
        if (artist != null) {
            albumChoiceBox.getItems().addAll(repositoryService.getAlbumRepository().selectByArtist(artist));
            albumChoiceBox.getSelectionModel().selectFirst();
        }    
    }

    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Musician musician = musicianChoiceBox.getValue();
            Album album = albumChoiceBox.getValue();
            if (!repositoryService.getMusicianAlbumRepository().containsMusicianAlbum(musician, album)) {
                musicianAlbum.setId_musician(musician.getId());
                musicianAlbum.setId_album(album.getId());
                repositoryService.getMusicianAlbumRepository().save(musicianAlbum);               
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
    }
    
    public void add() {       
        musicianChoiceBox.getSelectionModel().select(repositoryService.getMusicianRepository().selectById(musicianAlbum.getId_musician()));
   
        Album album = repositoryService.getAlbumRepository().selectById(musicianAlbum.getId_album());
        Artist artist = repositoryService.getArtistRepository().selectById(album.getId_artist());          
        artistChoiceBox.getSelectionModel().select(artist);
        albumChoiceBox.getSelectionModel().select(album);      
    }
    
    @Override
    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @Override
    public void setEntity(Entity entity) {
        if (entity != null && entity instanceof MusicianAlbum) {
            musicianAlbum = (MusicianAlbum) entity;
            if (musicianAlbum.getId() != 0) {
                dialogStage.setTitle("Edit Musician In Album"); 
                edit();
            } 
            else {
                dialogStage.setTitle("Add Musician To Album");
                add();
            }
        }
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        
        if (albumChoiceBox.getValue() == null) {
            errorMessage += "Выберите альбом из списка артиста\n";
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
       
}
