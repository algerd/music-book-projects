package com.algerd.musicbookspringmaven.controller.artist;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.utils.ImageUtil;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.SEPARATOR;

public class ArtistPaneController extends BasePaneController {
      
    private Artist artist;
    private File file;
    private long imageLastModified;
    
    @FXML
    private AnchorPane artistReferenceTable;
    @FXML
    private ArtistReferenceTableController includedArtistReferenceTableController;
    @FXML
    private AnchorPane albumTable;
    @FXML
    private AlbumTableController includedAlbumTableController;
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private MusicianTableController includedMusicianTableController;      
    @FXML
    private AnchorPane genreList;
    @FXML
    private GenreListController includedGenreListController;
           
    @FXML
    private TabPane artistTabPane;
    @FXML
    private Tab detailsTab;         
    @FXML
    private ImageView artistImageView;
    @FXML
    private Label nameLabel;       
    @FXML
    private Label ratingLabel;  
    @FXML
    private Text commentText;     
          
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
    } 
    
    @Override
    public void show(Entity entity) {       
        this.artist = (Artist) entity;                          
        showDetails();
        view.setVisible(true);
        initRepositoryListeners();
        includedArtistReferenceTableController.setPaneController(this);
        includedAlbumTableController.setPaneController(this);
        includedMusicianTableController.setPaneController(this); 
        includedGenreListController.setPaneController(this);
        artistTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void initRepositoryListeners() {
        repositoryService.getArtistRepository().clearDeleteListeners(this);
        repositoryService.getArtistRepository().clearUpdateListeners(this);
       
        repositoryService.getArtistRepository().addDeleteListener(this::deletedArtist, this);
        repositoryService.getArtistRepository().addUpdateListener(this::updatedArtist, this);
    }
    
    private void deletedArtist(ObservableValue observable, Object oldVal, Object newVal) {
        Artist newEntity = ((WrapChangedEntity<Artist>) newVal).getNew();
        if (newEntity.getId() == artist.getId()) {
            view.setVisible(false);
        }
    }
    
    private void updatedArtist(ObservableValue observable, Object oldVal, Object newVal) {
         if (file.lastModified() != imageLastModified) {
            artistImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }
      
    private void showDetails() {   
        nameLabel.textProperty().bind(artist.nameProperty());                                 
        ratingLabel.textProperty().bind(artist.ratingProperty().asString());
        commentText.textProperty().bind(artist.descriptionProperty());  
      
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "artist/" + artist.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        artistImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();       
    } 
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_ARTIST, new Artist());
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (artist.getId() != 1) {
                contextMenuService.add(EDIT_ARTIST, artist);
                contextMenuService.add(DELETE_ARTIST, artist);
                contextMenuService.add(SEPARATOR);
            }
            contextMenuService.show(view, mouseEvent);
        }      
    }

    public Artist getArtist() {
        return artist;
    }  
               
}
