
package mvc.musician.alert;

import db.entity.Artist;
import db.entity.Musician;
import db.entity.MusicianGroup;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;

public class MusicianGroupDeleteAlert {
    
    private MusicianGroupDeleteAlert() {}
    
    public static void show(MusicianGroup musicianGroup) {
        Musician musician = Main.getInstance().getRepositoryService().getMusicianRepository().selectById(musicianGroup.getId_musician());
        Artist artist = Main.getInstance().getRepositoryService().getArtistRepository().selectById(musicianGroup.getId_artist());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + artist.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getMusicianGroupRepository().delete(musicianGroup);
        }
    }

}
