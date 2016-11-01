
package core;

import db.entity.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public enum RequestDialog {
    
    ALBUM_DIALOG            ("mvc/album/view/AlbumDialog.fxml"),
    ARTIST_DIALOG           ("mvc/artist/view/ArtistDialog.fxml"),   
    ARTIST_REFERENCE_DIALOG ("mvc/artist/view/ArtistReferenceDialog.fxml"),
    GENRE_DIALOG            ("mvc/genre/view/GenreDialog.fxml"),
    INSTRUMENT_DIALOG       ("mvc/instrument/view/InstrumentDialog.fxml"),
    MUSICIAN_DIALOG         ("mvc/musician/view/MusicianDialog.fxml"),
    MUSICIAN_ALBUM_DIALOG   ("mvc/musician/view/MusicianAlbumDialog.fxml"),
    MUSICIAN_GROUP_DIALOG   ("mvc/musician/view/MusicianGroupDialog.fxml"),
    MUSICIAN_SONG_DIALOG    ("mvc/musician/view/MusicianSongDialog.fxml"),
    SONG_DIALOG             ("mvc/song/view/SongDialog.fxml");
 
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
            loader.setLocation(RequestDialog.class.getClassLoader().getResource(path));
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
