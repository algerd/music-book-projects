
package mvc.genre.alert;

import db.entity.Genre;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.Main;

public class GenreDeleteAlert {
    
    private GenreDeleteAlert() {}
    
     public static void show(Genre genre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");       
        alert.setContentText("Do you want to remove the genre " + genre.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.main.getLoader().getGenreTable().delete(genre);
            Main.main.getMainController().getIncludedGenrePaneController().hide();
        }
    }

}
