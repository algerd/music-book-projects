
package mvc.song.loader;

import db.entity.Entity;
import db.entity.Song;
import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import mvc.song.controller.SongDialogController;

public class SongDialogLoader {
    
    private SongDialogLoader() {}
    
    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SongDialogLoader.class.getClassLoader().getResource("mvc/song/view/SongDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();          
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(350);
            dialogStage.setMinWidth(400);

            SongDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            Song song = (Song) entity;
            if (song.getId() != 0) {
                dialogStage.setTitle("Edit Song");
                controller.setEditDialog(song);
            }
            else {
                dialogStage.setTitle("Add Song");
                controller.setAddDialog(song);
            }
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
