package com.algerd.musicbookspringmaven.controller.song;

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
import javafx.scene.control.TabPane;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.utils.ImageUtil;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_SONG;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_SONG;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_SONG;

public class SongPaneController extends BasePaneController {
   
    private Artist artist;
    private Album album;
    private Song song;
    private File file;
    private long imageLastModified;
    
    @FXML
    private AnchorPane genreList;
    @FXML
    private GenreListController includedGenreListController;
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private MusicianTableController includedMusicianTableController;
        
    @FXML
    private TabPane songTabPane;  
    @FXML
    private ImageView songImageView;
    @FXML
    private Hyperlink artistLink;       
    @FXML
    private Hyperlink albumLink; 
    @FXML
    private Label yearLabel;       
    @FXML
    private Label nameLabel;   
    @FXML
    private Label trackLabel;    
    @FXML
    private Label timeLabel;   
    @FXML
    private Label ratingLabel;
    @FXML
    private Text commentText;
    @FXML
    private Text lyricText;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    } 
       
    @Override
    public void show(Entity entity) {
        this.song = (Song) entity;            
        showDetails();
        view.setVisible(true);
        initRepositoryListeners();
        includedGenreListController.setPaneController(this);
        includedMusicianTableController.setPaneController(this);
        songTabPane.getSelectionModel().selectFirst();
    }
    
    private void initRepositoryListeners() {      
        //clear listeners
        repositoryService.getSongRepository().clearDeleteListeners(this);      
        repositoryService.getSongRepository().clearUpdateListeners(this); 
        
        //add listeners
        repositoryService.getSongRepository().addDeleteListener(this::deletedSong, this);      
        repositoryService.getSongRepository().addUpdateListener(this::updatedSong, this);          
    }
    
    private void deletedSong(ObservableValue observable, Object oldVal, Object newVal) {
        Song newEntity = ((WrapChangedEntity<Song>) newVal).getNew();
        if (newEntity.getId() == song.getId()) {
            view.setVisible(false);
        }
    }
    
    private void updatedSong(ObservableValue observable, Object oldVal, Object newVal) {
        if (file.lastModified() != imageLastModified) {
            songImageView.setImage(ImageUtil.readImage(file));
            imageLastModified = file.lastModified();
        }
    }
     
    
    private void showDetails() {
        album = repositoryService.getAlbumRepository().selectById(song.getId_album());
        artist = repositoryService.getArtistRepository().selectById(album.getId_artist());        
        
        artistLink.textProperty().bind(artist.nameProperty());
        albumLink.textProperty().bind(album.nameProperty()); 
        yearLabel.textProperty().bind(album.yearProperty().asString());                
        nameLabel.textProperty().bind(song.nameProperty());
        trackLabel.textProperty().bind(song.trackProperty().asString());
        timeLabel.textProperty().bind(song.timeProperty());
        ratingLabel.textProperty().bind(song.ratingProperty().asString());
        lyricText.textProperty().bind(song.lyricProperty());
        commentText.textProperty().bind(song.descriptionProperty());
        
        // проверить и загрузить изображение
        file = new File(Params.DIR_IMAGE_ENTITY + "song/" + song.getId() + "." + Params.SAVED_IMAGE_FORMAT);
        songImageView.setImage(ImageUtil.readImage(file));
        imageLastModified = file.lastModified();                    
    }
    
    @FXML 
    private void onLinkArtist() {  
        requestPageService.artistPane(artist);
    }
          
    @FXML 
    private void onLinkAlbum() {
        requestPageService.albumPane(album);
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            Song newSong = new Song();
            newSong.setId_album(song.getId_album());
            contextMenuService.add(ADD_SONG, newSong);
            contextMenuService.add(EDIT_SONG, song);
            contextMenuService.add(DELETE_SONG, song);        
            contextMenuService.show(view, mouseEvent);
        }      
    }

    public Song getSong() {
        return song;
    }

}
