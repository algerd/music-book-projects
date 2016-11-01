
package com.algerd.musicbookspringmaven.controller.explorer;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Song;
import static com.algerd.musicbookspringmaven.Params.DIR_IMAGES;

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
			String imagePath = DIR_IMAGES + fileName;			
			Image img = new Image(imagePath);
			imgView = new ImageView(img);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return imgView;
	}
}
