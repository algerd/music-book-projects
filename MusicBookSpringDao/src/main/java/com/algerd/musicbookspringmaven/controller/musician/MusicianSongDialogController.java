
package com.algerd.musicbookspringmaven.controller.musician;

import com.algerd.musicbookspringmaven.controller.BaseDialogController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.entity.MusicianSong;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.utils.Helper;
import javafx.scene.layout.AnchorPane;

public class MusicianSongDialogController extends BaseDialogController {
    
    private MusicianSong musicianSong;    
    
    @FXML
    private AnchorPane view;
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
        musicianChoiceBox.getItems().addAll(repositoryService.getMusicianRepository().selectAll());
        musicianChoiceBox.getSelectionModel().selectFirst();
    }
    
    private void initArtistChoiceBox() {
        Helper.initEntityChoiceBox(artistChoiceBox);
        artistChoiceBox.getItems().addAll(repositoryService.getArtistRepository().selectAll());
        artistChoiceBox.getSelectionModel().selectFirst();
    }
    
    /**
     * При выборе другого артиста - обновить список альбомов в albumChoiceBox.
     */
    private void changedArtistChoiceBox(ObservableValue<? extends Object> observable, Artist oldValue, Artist newValue) {          
        albumChoiceBox.getItems().clear();
        Artist artist = artistChoiceBox.getSelectionModel().getSelectedItem();
        if (artist != null) {
            albumChoiceBox.getItems().addAll(repositoryService.getAlbumRepository().selectByArtist(artist));
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
            songChoiceBox.getItems().addAll(repositoryService.getSongRepository().selectByAlbum(album));
            songChoiceBox.getSelectionModel().selectFirst();
        }
    }    
    
    
    @FXML
    private void handleOkButton() {
        if (isInputValid()) {
            Musician musician = musicianChoiceBox.getValue();
            Song song = songChoiceBox.getValue();
            if (!repositoryService.getMusicianSongRepository().containsMusicianSong(musician, song)) {
                musicianSong.setId_musician(musician.getId());
                musicianSong.setId_song(song.getId());
                repositoryService.getMusicianSongRepository().save(musicianSong);               
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancelButton() {
        dialogStage.close();
    }
    
    @Override
    protected void edit() {
        add();
    }
    
    @Override
    protected void add() { 
        musicianChoiceBox.getSelectionModel().select(repositoryService.getMusicianRepository().selectById(musicianSong.getId_musician()));
    
        Song song = repositoryService.getSongRepository().selectById(musicianSong.getId_song());
        Album album = repositoryService.getAlbumRepository().selectById(song.getId_album()); 
        Artist artist = repositoryService.getArtistRepository().selectById(album.getId_artist()); 
        artistChoiceBox.getSelectionModel().select(artist);
        albumChoiceBox.getSelectionModel().select(album);
        songChoiceBox.getSelectionModel().select(song);
    }
    
    @Override
    protected boolean isInputValid() {
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
            errorMessage(errorMessage);          
            return false;
        }           
    }
       
    @Override
    public void setEntity(Entity entity) {
        musicianSong = (MusicianSong) entity;
        super.setEntity(entity);
    }
    
}
