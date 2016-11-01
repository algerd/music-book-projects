package com.algerd.musicbookspringmaven.controller.artist;

import com.algerd.musicbookspringmaven.controller.BaseController;
import com.algerd.musicbookspringmaven.core.ContextMenuManager;
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
import javafx.beans.value.ObservableValue;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.RequestPage;
import com.algerd.musicbookspringmaven.utils.Helper;

public class GenreListController extends BaseController implements Initializable {

    private ArtistPaneController artistPaneController;
    
    @FXML
    private AnchorPane genreList;
    @FXML
    private ListView<Genre> genreListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
    }
    
    public void bootstrap(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
        setListValue();
        initRepositoryListeners();     
    }
    
    private void setListValue() {
        List<Genre> genres = new ArrayList<>();
        List<ArtistGenre> artistGenres = repositoryService.getArtistGenreRepository().selectJoinByArtist(artistPaneController.getArtist());
        artistGenres.stream().forEach(artistGenre -> genres.add(artistGenre.getGenre())); 
        genreListView.getItems().clear();       
        if (!genres.isEmpty()) {
            genreListView.getItems().addAll(genres);
            sort();
        } else {
            Genre genre = new Genre();
            genre.setName("Unknown");
            genreListView.getItems().add(genre);
        }                     
        Helper.setHeightList(genreListView, 8);        
    }
    
    private void initRepositoryListeners() {
        repositoryService.getArtistGenreRepository().clearChangeListeners(this);                                      
        repositoryService.getArtistRepository().clearUpdateListeners(this);                
        repositoryService.getGenreRepository().clearDeleteListeners(this);           
        repositoryService.getGenreRepository().clearUpdateListeners(this);      
    
        repositoryService.getArtistGenreRepository().addChangeListener(this::changed, this);                                      
        repositoryService.getArtistRepository().addUpdateListener(this::changed, this);                
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
        repositoryService.getGenreRepository().addUpdateListener(this::changed, this);       
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
        contextMenu.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Genre selectedItem = genreListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && selectedItem.getId() != 0) {
                // Дозагрузка
                Genre genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                RequestPage.GENRE_PANE.load(genre);
            }           
        }
    }
    
}
