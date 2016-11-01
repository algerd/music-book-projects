
package mvc.musician.alert;

import db.entity.Musician;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;

public class MusicianDeleteAlert {

    private MusicianDeleteAlert() {}
    
    public static void show(Musician musician) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getMusicianRepository().delete(musician);       
        }
    }
}
