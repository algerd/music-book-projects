package mvc.album.alert;

import db.entity.Album;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;

public class AlbumDeleteAlert {
    
    private AlbumDeleteAlert() {};
    
    public static void show(Album album) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the album " + album.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getAlbumRepository().delete(album);
        }
    }

}
