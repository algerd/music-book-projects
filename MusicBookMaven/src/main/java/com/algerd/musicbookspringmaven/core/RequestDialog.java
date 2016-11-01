
package com.algerd.musicbookspringmaven.core;

import com.algerd.musicbookspringmaven.controller.DialogController;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static com.algerd.musicbookspringmaven.core.Params.DIR_FXML;

public enum RequestDialog {
    
    ALBUM_DIALOG            ("album/AlbumDialog.fxml"),
    ARTIST_DIALOG           ("artist/ArtistDialog.fxml"),   
    ARTIST_REFERENCE_DIALOG ("artist/ArtistReferenceDialog.fxml"),
    GENRE_DIALOG            ("genre/GenreDialog.fxml"),
    INSTRUMENT_DIALOG       ("instrument/InstrumentDialog.fxml"),
    MUSICIAN_DIALOG         ("musician/MusicianDialog.fxml"),
    MUSICIAN_ALBUM_DIALOG   ("musician/MusicianAlbumDialog.fxml"),
    MUSICIAN_GROUP_DIALOG   ("musician/MusicianGroupDialog.fxml"),
    MUSICIAN_SONG_DIALOG    ("musician/MusicianSongDialog.fxml"),
    SONG_DIALOG             ("song/SongDialog.fxml");
        
    private final String path;

    private RequestDialog(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
    
    // load dialog window
    public void load(Entity entity, double width, double heigth) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(RequestDialog.class.getResource(DIR_FXML + path));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.getInstance().getPrimaryStage());
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
    
    public void load(Entity entity) {
        load(entity, -1, -1);
    }
    
}
