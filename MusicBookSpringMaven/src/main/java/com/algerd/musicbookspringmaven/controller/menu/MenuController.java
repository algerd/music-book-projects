package com.algerd.musicbookspringmaven.controller.menu;

import com.algerd.musicbookspringmaven.service.RequestPageService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;

public class MenuController implements Initializable {
    
    @Autowired
    private RequestPageService requestPageService;
    
    @FXML
    private AnchorPane menu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    } 
       
    @FXML
    private void showArtists() {
        requestPageService.artistsPane();
    }
    
    @FXML
    private void showAlbums() {
        requestPageService.albumsPane();
    }
    
    @FXML
    private void showSongs() {
        requestPageService.songsPane();
    }
          
    @FXML
    private void showMusicians() {
        requestPageService.musiciansPane();
    }
    
    @FXML
    private void showGenres() {
        requestPageService.genresPane();
    }  
    
    @FXML
    private void showInstruments() {
        requestPageService.instrumentsPane();
    }
}
