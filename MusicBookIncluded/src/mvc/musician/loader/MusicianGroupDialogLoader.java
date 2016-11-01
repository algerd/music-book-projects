
package mvc.musician.loader;

import db.entity.Entity;
import db.entity.MusicianGroup;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import mvc.musician.controller.MusicianGroupDialogController;

public class MusicianGroupDialogLoader {
    
    private MusicianGroupDialogLoader() {}
    
    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MusicianGroupDialogLoader.class.getClassLoader().getResource("mvc/musician/view/MusicianGroupDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(300);
            dialogStage.setMinWidth(400);

            MusicianGroupDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            MusicianGroup musicianGroup = (MusicianGroup) entity;
            if (musicianGroup.getId() != 0) {
                dialogStage.setTitle("Edit Musician In Group");
                controller.setEditDialog(musicianGroup);              
            }
            else {
                dialogStage.setTitle("Add Musician To Group");
                controller.setAddDialog(musicianGroup);              
            }           
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
