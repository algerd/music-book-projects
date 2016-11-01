
package mvc.artist.loader;

import db.entity.ArtistReference;
import db.entity.Entity;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import mvc.artist.controller.ArtistReferenceDialogController;

public class ArtistReferenceDialogLoader {
    
    private ArtistReferenceDialogLoader() {}
    
    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ArtistReferenceDialogLoader.class.getClassLoader().getResource("mvc/artist/view/ArtistReferenceDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();           
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(350);
            dialogStage.setMinWidth(400);

            ArtistReferenceDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            ArtistReference artistReference = (ArtistReference) entity;
            if (artistReference.getId() != 0) {
                dialogStage.setTitle("Edit Artist Reference");
                controller.setEditDialog(artistReference);
            } 
            else {
                dialogStage.setTitle("Add Artist Reference");
                controller.setAddDialog(artistReference);
            }             
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }  

}
