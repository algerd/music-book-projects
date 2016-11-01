package mvc.menu.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import main.Main;

public class MenuController implements Initializable {
    
    @FXML
    private AnchorPane menu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    } 
    
    @FXML
    private void showArtist() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedArtistPane().setVisible(true);
    }
    
    @FXML
    private void showAlbum() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedAlbumPane().setVisible(true);
    }
    
    @FXML
    private void showSong() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedSongPane().setVisible(true);
    }
    
    @FXML
    private void showGenre() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedGenrePane().setVisible(true);
    }
    
    @FXML
    private void showMusician() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedMusicianPane().setVisible(true);
    }
    
    @FXML
    private void showArtistOverview() {
        Main.main.getMainController().getIncludedArtistOverviewController().show();
    }
    
    @FXML
    private void showAlbumOverview() {
        Main.main.getMainController().getIncludedAlbumOverviewController().show();
    }
    
    @FXML
    private void showSongOverview() {
        Main.main.getMainController().getIncludedSongOverviewController().show();
    }
    
    @FXML
    private void showGenreOverview() {
        Main.main.getMainController().getIncludedGenreOverviewController().show();
    }
    
    @FXML
    private void showMusicianOverview() {
        Main.main.getMainController().getIncludedMusicianOverviewController().show();
    }
          
}
