
package mvc.musician.loader;

import db.entity.Entity;
import db.entity.Musician;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import mvc.musician.controller.MusicianDialogController;

public class MusicianDialogLoader {
    
    private MusicianDialogLoader() {}
    
    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MusicianDialogLoader.class.getClassLoader().getResource("mvc/musician/view/MusicianDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(300);
            dialogStage.setMinWidth(400);

            MusicianDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            if (entity == null) {
                dialogStage.setTitle("Add Musician");
                controller.setAddDialog();
            }
            else {
                dialogStage.setTitle("Edit Musician");
                controller.setEditDialog((Musician) entity);
            }           
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
