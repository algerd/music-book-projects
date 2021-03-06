
package mvc.musician.controller;

import core.DialogController;
import core.Main;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Musician;
import db.entity.MusicianSong;
import db.entity.Song;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import utils.Helper;

public class MusicianSongDialogController implements Initializable, DialogController {
    
    private Stage dialogStage;
    private MusicianSong musicianSong;    
    
    @FXML
    private ChoiceBox<Musician> musicianChoiceBox;
    @FXML
    private ChoiceBox<Artist> artistChoiceBox;
    @FXML
    private ChoiceBox<Album> albumChoiceBox;
    @FXML
    private ChoiceBox<Song> songChoiceBox;
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        artistChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changedArtistChoiceBox);
        albumChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::changedAlbumChoiceBox);
        initMusicianChoiceBox();        
        initArtistChoiceBox();       
    } 
    
    private void initMusicianChoiceBox() {
        Helper.initEntityChoiceBox(musicianChoiceBox);
        musicianChoiceBox.getItems().addAll(Main.getInstance().getDbLoader().getMusicianTable().select());
        musicianChoiceBox.getSelectionModel().selectFirst();
    }
    
    private void initArtistChoiceBox() {
        Helper.initEntityChoiceBox(artistChoiceBox);
        artistChoiceBox.getItems().addAll(Main.getInstance().getDbLoader().getArtistTable().select());
        artistChoiceBox.getSelectionModel().selectFirst();
    }
    
    /**
     * При выборе другого артиста - обновить список альбомов в albumChoiceBox.
     */
    private void changedArtistChoiceBox(ObservableValue<? extends Object> observable, Artist oldValue, Artist newValue) {          
        albumChoiceBox.getItems().clear();
        Artist artist = artistChoiceBox.getSelectionModel().getSelectedItem();
        if (artist != null) {
            albumChoiceBox.getItems().addAll(Main.getInstance().getDbLoader().getAlbumTable().selectByArtist(artist));
            albumChoiceBox.getSelectionModel().selectFirst();
        }    
    }
    
    /**
     * При выборе другого альбома - обновить список песен songChoiceBox.
     */
    private void changedAlbumChoiceBox(ObservableValue<? extends Object> observable, Album oldValue, Album newValue) {          
        songChoiceBox.getItems().clear();
        Album album = albumChoiceBox.getSelectionModel().getSelectedItem();
        if (album != null) {
            songChoiceBox.getItems().addAll(Main.getInstance().getDbLoader().getSongTable().selectByAlbum(album));
            songChoiceBox.getSelectionModel().selectFirst();
        }
    }    
    
    
     @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Musician musician = musicianChoiceBox.getValue();
            Song song = songChoiceBox.getValue();
            if (!Main.getInstance().getDbLoader().getMusicianSongTable().containsMusicianSong(musician, song)) {
                musicianSong.setId_musician(musician.getId());
                musicianSong.setId_song(song.getId());
                Main.getInstance().getDbLoader().getMusicianSongTable().save(musicianSong);               
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
        musicianChoiceBox.getSelectionModel().select(Main.getInstance().getDbLoader().getMusicianTable().select(musicianSong.getId_musician()));
    
        Song song = Main.getInstance().getDbLoader().getSongTable().select(musicianSong.getId_song());
        Album album = Main.getInstance().getDbLoader().getAlbumTable().select(song.getId_album()); 
        Artist artist = Main.getInstance().getDbLoader().getArtistTable().select(album.getId_artist()); 
        artistChoiceBox.getSelectionModel().select(artist);
        albumChoiceBox.getSelectionModel().select(album);
        songChoiceBox.getSelectionModel().select(song);
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        
        if (albumChoiceBox.getValue() == null) {
            errorMessage += "Выберите альбом из списка \n";           
        }
        else if (songChoiceBox.getValue() == null) {
            errorMessage += "Выберите песню из списка \n";           
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
        if (entity != null && entity instanceof MusicianSong) {
            musicianSong = (MusicianSong) entity;
            if (musicianSong.getId() != 0) {
                dialogStage.setTitle("Edit Musician In Song"); 
                edit();
            } 
            else {
                dialogStage.setTitle("Add Musician To Song");
                add();
            }
        }
    }
    
}
