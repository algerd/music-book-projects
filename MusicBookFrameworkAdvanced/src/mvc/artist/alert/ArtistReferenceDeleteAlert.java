
package mvc.artist.alert;

import db.entity.ArtistReference;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;

public class ArtistReferenceDeleteAlert {
    
    private ArtistReferenceDeleteAlert() {}
    
    public static void show(ArtistReference artistReference) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Do you want to remove the artist reference: " + artistReference.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getArtistReferenceRepository().delete(artistReference);
        }
    }

}
