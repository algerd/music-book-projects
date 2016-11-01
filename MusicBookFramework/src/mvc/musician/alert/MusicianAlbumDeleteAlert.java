
package mvc.musician.alert;

import db.entity.Musician;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;
import db.entity.Album;
import db.entity.MusicianAlbum;

public class MusicianAlbumDeleteAlert {
    
    private MusicianAlbumDeleteAlert() {}
    
    public static void show(MusicianAlbum musicianAlbum) {
        Musician musician = Main.getInstance().getDbLoader().getMusicianTable().select(musicianAlbum.getId_musician());
        Album album = Main.getInstance().getDbLoader().getAlbumTable().select(musicianAlbum.getId_album());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + album.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getDbLoader().getMusicianAlbumTable().delete(musicianAlbum);
        }
    }

}
