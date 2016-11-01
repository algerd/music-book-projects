
package mvc.musician.alert;

import db.entity.Musician;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import core.Main;
import db.entity.MusicianSong;
import db.entity.Song;

public class MusicianSongDeleteAlert {
    
    private MusicianSongDeleteAlert() {}
    
    public static void show(MusicianSong musicianSong) {
        Musician musician = Main.getInstance().getRepositoryService().getMusicianRepository().selectById(musicianSong.getId_musician());
        Song song = Main.getInstance().getRepositoryService().getSongRepository().selectById(musicianSong.getId_song());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");        
        alert.setContentText("Do you want to remove the musician " + musician.getName() + " from " + song.getName() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.getInstance().getRepositoryService().getMusicianSongRepository().delete(musicianSong);
        }
    }

}
