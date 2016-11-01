
package mvc.artist.loader;

import db.entity.Artist;
import db.entity.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import mvc.artist.controller.ArtistDialogController;

public class ArtistDialogLoader {
    
    private ArtistDialogLoader() {}
    
    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArtistDialogLoader.class.getClassLoader().getResource("mvc/artist/view/ArtistDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(350);
            dialogStage.setMinWidth(400);

            ArtistDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            if (entity == null) {
                dialogStage.setTitle("Add Artist");
                controller.setAddDialog();
            }
            else {
                dialogStage.setTitle("Edit Artist");
                controller.setEditDialog((Artist) entity);
            }           
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
