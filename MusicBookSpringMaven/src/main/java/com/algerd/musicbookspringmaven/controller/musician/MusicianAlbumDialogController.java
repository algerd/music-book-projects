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
import com.algerd.musicbookspringmaven.entity.MusicianAlbum;
import com.algerd.musicbookspringmaven.utils.Helper;
import javafx.scene.layout.AnchorPane;

public class MusicianAlbumDialogController extends BaseDialogController {
   
    private MusicianAlbum musicianAlbum;    
    
    @FXML
    private AnchorPane view;
    @FXML
    private ChoiceBox<Musician> musicianChoiceBox;
    @FXML
    private ChoiceBox<Artist> artistChoiceBox;
    @FXML
    private ChoiceBox<Album> albumChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
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
    
    @Override
    protected void edit() {
        add();
    }
    
    @Override
    protected void add() {       
        musicianChoiceBox.getSelectionModel().select(repositoryService.getMusicianRepository().selectById(musicianAlbum.getId_musician()));
   
        Album album = repositoryService.getAlbumRepository().selectById(musicianAlbum.getId_album());
        Artist artist = repositoryService.getArtistRepository().selectById(album.getId_artist());          
        artistChoiceBox.getSelectionModel().select(artist);
        albumChoiceBox.getSelectionModel().select(album);      
    }
       
    @Override
    public void setEntity(Entity entity) {
        musicianAlbum = (MusicianAlbum) entity;
        super.setEntity(entity);
    }
    
    @Override
    protected boolean isInputValid() {
        String errorMessage = "";
        
        if (albumChoiceBox.getValue() == null) {
            errorMessage += "Выберите альбом из списка артиста\n";
        }              
        if (errorMessage.equals("")) {
            return true;
        } 
        else {
            errorMessage(errorMessage);         
            return false;
        }   
    }  
       
}
