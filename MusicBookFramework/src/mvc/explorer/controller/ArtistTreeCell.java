
package mvc.explorer.controller;

import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Song;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ArtistTreeCell extends TreeCell<Entity> {

    @Override
    public void updateItem(Entity item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            textProperty().unbind();
            setText(null);
            setGraphic(null);
        } else {
            if (item instanceof Artist) {
                textProperty().bind(((Artist) item).nameProperty());
                setGraphic(getIcon("folder.jpg"));
            }
            else if (item instanceof Album) {
                textProperty().bind(((Album) item).nameProperty());
                setGraphic(getIcon("file.jpg"));
            }
            else if (item instanceof Song) {
                this.textProperty().bind(((Song) item).nameProperty());
                //this.setGraphic(getIcon("file.jpg"));
            }
        }
    }
    
     private ImageView getIcon(String fileName) {
		ImageView imgView = null;
		try {
			String imagePath = "resources/images/" + fileName;			
			Image img = new Image(imagePath);
			imgView = new ImageView(img);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return imgView;
	}
}
