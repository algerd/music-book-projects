
package mvc.artist.alert;

import db.entity.Artist;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;

public class ArtistDeleteAlert {
    
    private ArtistDeleteAlert() {};
    
    public static void show(Artist artist) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the artist " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getArtistRepository().delete(artist);
        }
    }

}
