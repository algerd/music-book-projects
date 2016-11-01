
package mvc.album.loader;

import db.entity.Album;
import db.entity.Entity;
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
import mvc.album.controller.AlbumDialogController;

public class AlbumDialogLoader {
    
    private AlbumDialogLoader() {}

    public static void show(Entity entity) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AlbumDialogLoader.class.getClassLoader().getResource("mvc/album/view/AlbumDialog.fxml"));
            AnchorPane page = loader.<AnchorPane>load();

            Stage dialogStage = new Stage();         
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.main.getPrimaryStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setMinHeight(350);
            dialogStage.setMinWidth(400);

            AlbumDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            Album album = (Album) entity;
            if (album.getId() != 0) {
                dialogStage.setTitle("Edit Album");
                controller.setEditDialog(album);
            } 
            else {
                dialogStage.setTitle("Add Album");
                controller.setAddDialog(album);
            }  
            dialogStage.showAndWait();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
