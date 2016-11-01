
package com.algerd.musicbookspringmaven.controller.album;

import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.SEPARATOR;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.utils.ImageUtil;

public class AlbumPaneController extends BasePaneController {
      
    private Artist artist;
    private Album album;
    private File file;
    private long imageLastModified;  
    
    @FXML
    private TabPane albumTabPane;
    @FXML
    private Tab detailsTab;   
    @FXML
    private AnchorPane songTable;
    @FXML
    private SongTableController includedSongTableController;
    @FXML
    private AnchorPane genreList;
    @FXML
    private GenreListController includedGenreListController;
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private MusicianTableController includedMusicianTableController;    
    /* ********** Details *********** */
    @FXML
    private ImageView albumImageView;
    @FXML
    private Hyperlink artistLink; 
    @FXML
    private Label nameLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Text commentText;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }  
    
    @Override
    public void show(Entity entity) {
        this.album = (Album) entity;            
        showDetails();
        view.setVisible(true);
        initRepositoryListeners();
        includedMusicianTableController.setPaneController(this);
        includedSongTableController.setPaneController(this);
        includedGenreListController.setPaneController(this);
        albumTabPane.getSelectionModel().select(detailsTab);
    }
    
    private void initRepositoryListeners() {           
        repositoryService.getAlbumRepository().clearDeleteListeners(this);
        repositoryService.getAlbumRepository().clearUpdateListeners(this);
               
        repositoryService.getAlbumRepository().addDeleteListener(this::deletedAlbum, this);
        repositoryService.getAlbumRepository().addUpdateListener(this::updatedAlbum, this);
    }
       
    private void deletedAlbum(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        if (newEntity.getId() == album.getId()) {
            view.setVisible(false);
        }
    }
    
    private void updatedAlbum(ObservableValue observable, Object oldVal, Object newVal) {
        if (file.lastModified() != imageLastModified) {
            albumImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        showDetails();
    }
                
    private void showDetails() {
        artist = repositoryService.getArtistRepository().selectById(album.getId_artist());
        artistLink.textProperty().bind(artist.nameProperty());
        nameLabel.textProperty().bind(album.nameProperty());
        yearLabel.textProperty().bind(album.yearProperty().asString());
        timeLabel.textProperty().bind(album.timeProperty());
        ratingLabel.textProperty().bind(album.ratingProperty().asString());
        commentText.textProperty().bind(album.descriptionProperty());
        
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "album/" + album.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        albumImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();      
    }  
    
    @FXML 
    private void onLinkArtist() {
        requestPageService.artistPane(artist);
    }
  
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            Album newAlbum = new Album();
            newAlbum.setId_artist(album.getId_artist());            
            contextMenuService.add(ADD_ALBUM, newAlbum);
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (album.getId() != 1) { 
                contextMenuService.add(EDIT_ALBUM, album);
                contextMenuService.add(DELETE_ALBUM, album);
                contextMenuService.add(SEPARATOR);
            }   
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
    public Album getAlbum() {
        return album;
    }
           
}
