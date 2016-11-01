package mvc.song.controller;

import db.entity.Album;
import db.entity.Artist;
import db.driver.Entity;
import db.entity.Song;
import db.driver.impl.WrapChangedEntity;
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
import core.Loadable;
import core.Main;
import core.Params;
import core.RequestPage;
import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_SONG;
import static core.ContextMenuManager.ItemType.DELETE_SONG;
import static core.ContextMenuManager.ItemType.EDIT_SONG;
import db.repository.RepositoryService;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TabPane;
import utils.ImageUtil;

public class SongPaneController implements Initializable, Loadable {
    
    //@Inject
    private RepositoryService repositoryService; 
   
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
    private AnchorPane songPane;  
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
    } 
       
    @Override
    public void show(Entity entity) {
        this.song = (Song) entity;            
        showDetails();
        songPane.setVisible(true);
        initRepositoryListeners();
        includedGenreListController.bootstrap(this);
        includedMusicianTableController.bootstrap(this);
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
            songPane.setVisible(false);
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
        RequestPage.ARTIST_PANE.load(artist);      
    }
          
    @FXML 
    private void onLinkAlbum() {
        RequestPage.ALBUM_PANE.load(album);
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            Song newSong = new Song();
            newSong.setId_album(song.getId_album());
            contextMenu.add(ADD_SONG, newSong);
            contextMenu.add(EDIT_SONG, song);
            contextMenu.add(DELETE_SONG, song);        
            contextMenu.show(songPane, mouseEvent);
        }      
    }

    public Song getSong() {
        return song;
    }

    public AnchorPane getSongPane() {
        return songPane;
    }   
   
}
