
package com.algerd.musicbookspringmaven.controller.album;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import com.algerd.musicbookspringmaven.core.Loadable;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.Params;
import com.algerd.musicbookspringmaven.core.RequestPage;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.utils.ImageUtil;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.*;

public class AlbumPaneController extends BaseController implements Initializable, Loadable {
      
    private Artist artist;
    private Album album;
    private File file;
    private long imageLastModified;  
    
    @FXML
    private AnchorPane albumPane;
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
    }  
    
    @Override
    public void show(Entity entity) {
        this.album = (Album) entity;            
        showDetails();
        albumPane.setVisible(true);
        initRepositoryListeners();
        includedMusicianTableController.bootstrap(this);
        includedSongTableController.bootstrap(this);
        includedGenreListController.bootstrap(this);
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
            albumPane.setVisible(false);
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
        RequestPage.ARTIST_PANE.load(artist);
    }
  
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            Album newAlbum = new Album();
            newAlbum.setId_artist(album.getId_artist());            
            contextMenu.add(ADD_ALBUM, newAlbum);
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (album.getId() != 1) { 
                contextMenu.add(EDIT_ALBUM, album);
                contextMenu.add(DELETE_ALBUM, album);
                contextMenu.add(SEPARATOR);
            }   
            contextMenu.show(albumPane, mouseEvent);
        }      
    }

    public Album getAlbum() {
        return album;
    }

    public AnchorPane getAlbumPane() {
        return albumPane;
    }
           
}
