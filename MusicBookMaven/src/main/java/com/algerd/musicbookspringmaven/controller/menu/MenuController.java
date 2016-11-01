package com.algerd.musicbookspringmaven.controller.menu;

import com.algerd.musicbookspringmaven.core.RequestPage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class MenuController implements Initializable {
    
    @FXML
    private AnchorPane menu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    } 
       
    @FXML
    private void showArtists() {
        RequestPage.ARTISTS_PANE.load();
    }
    
    @FXML
    private void showAlbums() {
        RequestPage.ALBUMS_PANE.load();
    }
    
    @FXML
    private void showSongs() {
        RequestPage.SONGS_PANE.load();   
    }
          
    @FXML
    private void showMusicians() {
        RequestPage.MUSICIANS_PANE.load();
    }
    
    @FXML
    private void showGenres() {
        RequestPage.GENRES_PANE.load();
    }  
    
    @FXML
    private void showInstruments() {
        RequestPage.INSTRUMENTS_PANE.load();
    }
}
