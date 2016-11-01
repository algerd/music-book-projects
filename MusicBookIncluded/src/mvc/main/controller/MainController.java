package mvc.main.controller;

import mvc.musician.controller.MusicianOverviewController;
import mvc.musician.controller.MusicianPaneController;
import mvc.genre.controller.GenreOverviewController;
import mvc.genre.controller.GenrePaneController;
import mvc.song.controller.SongOverviewController;
import mvc.song.controller.SongPaneController;
import mvc.album.controller.AlbumOverviewController;
import mvc.album.controller.AlbumPaneController;
import mvc.artist.controller.ArtistOverviewController;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Genre;
import db.entity.Musician;
import db.entity.Song;
import mvc.explorer.controller.ExplorerController;
import mvc.menu.controller.MenuController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import mvc.artist.controller.ArtistPaneController;

public class MainController implements Initializable {
    
    @FXML
    private AnchorPane includedMenu;
    @FXML
    private MenuController includedMenuController;
    
    @FXML
    private AnchorPane includedExplorer;
    @FXML
    private ExplorerController includedExplorerController;
    
    @FXML
    private AnchorPane includedArtistPane;
    @FXML
    private ArtistPaneController includedArtistPaneController;
    
    @FXML
    private AnchorPane includedAlbumPane;            
    @FXML
    private AlbumPaneController includedAlbumPaneController;
    
    @FXML
    private AnchorPane includedSongPane;            
    @FXML
    private SongPaneController includedSongPaneController;
    
    @FXML
    private AnchorPane includedGenrePane;            
    @FXML
    private GenrePaneController includedGenrePaneController;
    
    @FXML
    private AnchorPane includedArtistOverview;
    @FXML
    private ArtistOverviewController includedArtistOverviewController;
    
    @FXML
    private AnchorPane includedAlbumOverview;
    @FXML
    private AlbumOverviewController includedAlbumOverviewController;
    
    @FXML
    private AnchorPane includedSongOverview;
    @FXML
    private SongOverviewController includedSongOverviewController;
    
    @FXML
    private AnchorPane includedGenreOverview;
    @FXML
    private GenreOverviewController includedGenreOverviewController;
    
    @FXML
    private AnchorPane includedMusicianPane;
    @FXML
    private MusicianPaneController includedMusicianPaneController;
    
    @FXML
    private AnchorPane includedMusicianOverview;
    @FXML
    private MusicianOverviewController includedMusicianOverviewController;
    
    public void initialize(URL url, ResourceBundle rb) {
        hideAllPanes();
    } 
    
    /**
     * Сделать невидимыми все панели.
     */
    public void hideAllPanes() {
        hideContentPanes();
        includedArtistOverviewController.hide();
        includedAlbumOverviewController.hide();
        includedSongOverviewController.hide();
        includedGenreOverviewController.hide();
        includedMusicianOverviewController.hide();
    }
    
    /**
     * Сделать невидимыми все контентные(артист, альбом, песня) панели.
     */
    public void hideContentPanes() {
        includedArtistPaneController.hide(); 
        includedAlbumPaneController.hide();
        includedSongPaneController.hide();
        includedGenrePaneController.hide();
        includedMusicianPaneController.hide();
    }
    
    public void showEntity(Entity entity) {
        hideAllPanes();
        if (entity instanceof Artist) {        
            includedArtistPaneController.show((Artist) entity);
        }
        else if (entity instanceof Album) {       
            includedAlbumPaneController.show((Album) entity);
        }
        else if (entity instanceof Song) {   
            includedSongPaneController.show((Song) entity);
        }
        else if (entity instanceof Genre) {   
            includedGenrePaneController.show((Genre) entity);
        }
        else if (entity instanceof Musician) {   
            includedMusicianPaneController.show((Musician) entity);
        }
    }
    
    public void showTreeEntity(Entity entity) {
        showEntity(entity);
        includedExplorerController.selectEntity(entity);
    }
    
    public AnchorPane getIncludedMenu() {
        return includedMenu;
    }                 
    public MenuController getIncludedMenuController() {
        return includedMenuController;
    }
    
    public AnchorPane getIncludedExplorer() {
        return includedExplorer;
    }
    public ExplorerController getIncludedExplorerController() {
        return includedExplorerController;
    }
    
    public AnchorPane getIncludedArtistPane() {
        return includedArtistPane;
    }
    public ArtistPaneController getIncludedArtistPaneController() {
        return includedArtistPaneController;
    }
    
    public AnchorPane getIncludedAlbumPane() {
        return includedAlbumPane;
    }
    public AlbumPaneController getIncludedAlbumPaneController() {
        return includedAlbumPaneController;
    }
    
    public AnchorPane getIncludedSongPane() {
        return includedSongPane;
    }
    public SongPaneController getIncludedSongPaneController() {
        return includedSongPaneController;
    }

    public AnchorPane getIncludedGenrePane() {
        return includedGenrePane;
    }
    public GenrePaneController getIncludedGenrePaneController() {
        return includedGenrePaneController;
    }
    
    public AnchorPane getIncludedArtistOverview() {
        return includedArtistOverview;
    }
    public ArtistOverviewController getIncludedArtistOverviewController() {
        return includedArtistOverviewController;
    }

    public AnchorPane getIncludedAlbumOverview() {
        return includedAlbumOverview;
    }
    public AlbumOverviewController getIncludedAlbumOverviewController() {
        return includedAlbumOverviewController;
    }

    public AnchorPane getIncludedSongOverview() {
        return includedSongOverview;
    }
    public SongOverviewController getIncludedSongOverviewController() {
        return includedSongOverviewController;
    } 

    public AnchorPane getIncludedGenreOverview() {
        return includedGenreOverview;
    }
    public GenreOverviewController getIncludedGenreOverviewController() {
        return includedGenreOverviewController;
    }

    public AnchorPane getIncludedMusicianPane() {
        return includedMusicianPane;
    } 
    public MusicianPaneController getIncludedMusicianPaneController() {
        return includedMusicianPaneController;
    }

    public AnchorPane getIncludedMusicianOverview() {
        return includedMusicianOverview;
    }
    public MusicianOverviewController getIncludedMusicianOverviewController() {
        return includedMusicianOverviewController;
    }
                      
}
