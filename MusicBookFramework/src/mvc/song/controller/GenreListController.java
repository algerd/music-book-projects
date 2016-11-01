package mvc.song.controller;

import db.entity.Genre;
import db.entity.SongGenre;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import core.Main;
import core.RequestPage;
import javafx.beans.value.ObservableValue;
import utils.Helper;

public class GenreListController implements Initializable {

    private SongPaneController songPaneController;
    
    @FXML
    private AnchorPane genreList;
    @FXML
    private ListView<Genre> genreListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {       
    }

    public void bootstrap(SongPaneController songPaneController) {
        this.songPaneController = songPaneController;
        setListValue();
        initListeners();
    }
    
    private void setListValue() {
        List<Genre> genres = new ArrayList<>();
        List<SongGenre> songGenres = Main.getInstance().getDbLoader().getSongGenreTable().selectJoinBySong(songPaneController.getSong());
        for (SongGenre songGenre : songGenres) {
            genres.add(songGenre.getGenre());
        } 
        genreListView.getItems().clear();       
        if (!genres.isEmpty()) {
            genreListView.getItems().addAll(genres);
            sort();
        } else {
            Genre genre = new Genre();
            genre.setName("Unknown");
            genreListView.getItems().add(genre);
        }                     
        Helper.setHeightList(genreListView, 6);        
    }
    
    private void initListeners() {
        // слушатель на добавление жанра песне
        Main.getInstance().getDbLoader().getSongGenreTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getSongGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getSongGenreTable().updatedProperty().addListener(this::changed);
                  
        Main.getInstance().getDbLoader().getSongTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getSongTable().updatedProperty().addListener(this::changed);
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::changed);
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getGenreTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getGenreTable().updatedProperty().addListener(this::changed);         
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setListValue();
    }
    
    private void sort() {
        genreListView.getSelectionModel().clearSelection();
        genreListView.getItems().sort(Comparator.comparing(Genre::getName));
    }  
    
     @FXML
    private void onMouseClickGenreList(MouseEvent mouseEvent) {    
        Main.getInstance().getContextMenu().clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Genre selectedItem = genreListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && selectedItem.getId() != 0) {
                // Дозагрузка
                Genre genre = Main.getInstance().getDbLoader().getGenreTable().select(selectedItem.getId());
                RequestPage.GENRE_PANE.load(genre);
            }           
        }
    }
    
}
