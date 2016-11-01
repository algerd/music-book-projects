
package mvc.genre.loader;

import db.entity.Entity;
import db.entity.Genre;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import mvc.genre.controller.GenreDialogController;

public class GenreDialogLoader {
    
    private GenreDialogLoader() {}

    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GenreDialogLoader.class.getClassLoader().getResource("mvc/genre/view/GenreDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(300);
            dialogStage.setMinWidth(400);

            GenreDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            if (entity == null) {
                dialogStage.setTitle("Add Genre");
                controller.setAddDialog();
            }
            else {
                dialogStage.setTitle("Edit Genre");
                controller.setEditDialog((Genre) entity);
            }           
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
