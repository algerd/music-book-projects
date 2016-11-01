package mvc.song.alert;

import db.entity.Song;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;

public class SongDeleteAlert {
    
    private SongDeleteAlert() {};
    
    public static void show(Song song) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the song " + song.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getSongRepository().delete(song);
        }
    }

}
