
package com.algerd.musicbookspringmaven.service.impl;

import com.algerd.musicbookspringmaven.controller.DialogController;
import com.algerd.musicbookspringmaven.service.RequestDialogService;
import com.algerd.musicbookspringmaven.controller.album.AlbumDialogController;
import com.algerd.musicbookspringmaven.controller.artist.ArtistDialogController;
import com.algerd.musicbookspringmaven.controller.artist.ArtistReferenceDialogController;
import com.algerd.musicbookspringmaven.controller.genre.GenreDialogController;
import com.algerd.musicbookspringmaven.controller.instrument.InstrumentDialogController;
import com.algerd.musicbookspringmaven.controller.musician.MusicianAlbumDialogController;
import com.algerd.musicbookspringmaven.controller.musician.MusicianDialogController;
import com.algerd.musicbookspringmaven.controller.musician.MusicianGroupDialogController;
import com.algerd.musicbookspringmaven.controller.musician.MusicianSongDialogController;
import com.algerd.musicbookspringmaven.controller.song.SongDialogController;
import com.algerd.musicbookspringmaven.spring.SpringFxmlLoader;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

public class RequestDialogServiceImpl implements RequestDialogService {
    
    @Autowired
    private SpringFxmlLoader springFxmlLoader;
    @Autowired
    private Stage primaryStage;
          
    @Override
    public void load(Class<? extends DialogController> type, Entity entity, double width, double heigth) {
        try {
            FXMLLoader loader = springFxmlLoader.loadController(type);        
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            if (width > 0) {
                dialogStage.setMinHeight(width);
            }
            if (heigth > 0) {
                dialogStage.setMinWidth(heigth);
            }
            DialogController controller = loader.getController();
            controller.setStage(dialogStage);
            controller.setEntity(entity);
                     
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }      
    }
       
    @Override
    public void load(Class<? extends DialogController> type, Entity entity) {
        load(type, entity, -1, -1);
    }
          
    @Override
    public void albumDialog(Entity entity, double width, double heigth) {
        load(AlbumDialogController.class, entity, width, heigth);
    }
    @Override
    public void albumDialog(Entity entity) {
        albumDialog(entity, -1, -1);
    }
    
    @Override
    public void artistDialog(Entity entity, double width, double heigth) {
        load(ArtistDialogController.class, entity, width, heigth);
    }
    @Override
    public void artistDialog(Entity entity) {
        artistDialog(entity, -1, -1);
    }
    
    @Override
    public void artistReferenceDialog(Entity entity, double width, double heigth) {
        load(ArtistReferenceDialogController.class, entity, width, heigth);
    }
    @Override
    public void artistReferenceDialog(Entity entity) {
        artistReferenceDialog(entity, -1, -1);
    }
    
    @Override
    public void genreDialog(Entity entity, double width, double heigth) {
        load(GenreDialogController.class, entity, width, heigth);
    }
    @Override
    public void genreDialog(Entity entity) {
        genreDialog(entity, -1, -1);
    }
    
    @Override
    public void instrumentDialog(Entity entity, double width, double heigth) {
        load(InstrumentDialogController.class, entity, width, heigth);
    }
    @Override
    public void instrumentDialog(Entity entity) {
        instrumentDialog(entity, -1, -1);
    }
    
    @Override
    public void musicianDialog(Entity entity, double width, double heigth) {
        load(MusicianDialogController.class, entity, width, heigth);
    }
    @Override
    public void musicianDialog(Entity entity) {
        musicianDialog(entity, -1, -1);
    }
    
    @Override
    public void musicianAlbumDialog(Entity entity, double width, double heigth) {
        load(MusicianAlbumDialogController.class, entity, width, heigth);
    }
    @Override
    public void musicianAlbumDialog(Entity entity) {
        musicianAlbumDialog(entity, -1, -1);
    }
    
    @Override
    public void musicianGroupDialog(Entity entity, double width, double heigth) {
        load(MusicianGroupDialogController.class, entity, width, heigth);
    }
    @Override
    public void musicianGroupDialog(Entity entity) {
        musicianGroupDialog(entity, -1, -1);
    }
    
    @Override
    public void musicianSongDialog(Entity entity, double width, double heigth) {
        load(MusicianSongDialogController.class, entity, width, heigth);
    }
    @Override
    public void musicianSongDialog(Entity entity) {
        musicianSongDialog(entity, -1, -1);
    }
    
    @Override
    public void songDialog(Entity entity, double width, double heigth) {
        load(SongDialogController.class, entity, width, heigth);
    }
    @Override
    public void songDialog(Entity entity) {
        songDialog(entity, -1, -1);
    }
       
}
