
package mvc.musician.alert;

import db.entity.Artist;
import db.entity.Musician;
import db.entity.MusicianGroup;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.Main;

public class MusicianGroupDeleteDialog {
    
    private MusicianGroupDeleteDialog() {}
    
    public static void show(MusicianGroup musicianGroup) {
        Musician musician = Main.main.getLoader().getMusicianTable().getEntityWithId(musicianGroup.getIdMusician());
        Artist artist = Main.main.getLoader().getArtistTable().getEntityWithId(musicianGroup.getIdArtist());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.main.getLoader().getMusicianGroupTable().delete(musicianGroup);
        }
    }

}
