package com.algerd.musicbookspringmaven.controller.musician;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.beans.value.ObservableValue;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import com.algerd.musicbookspringmaven.utils.Helper;

public class GenreListController extends BaseIncludeController<MusicianPaneController> {
    
    @FXML
    private AnchorPane genreList;
    @FXML
    private ListView<Genre> genreListView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
    }

    @Override
    public void bootstrap() {
        setListValue();
        initRepositoryListeners();
    }
    
    private void setListValue() {
        List<Genre> genres = new ArrayList<>();
        List<MusicianGenre> musicianGenres = repositoryService.getMusicianGenreRepository().selectJoinByMusician(paneController.getMusician());
        musicianGenres.stream().forEach(musicianGenre -> genres.add(musicianGenre.getGenre()));     
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
    
    private void initRepositoryListeners() { 
        //clear listeners
        repositoryService.getMusicianGenreRepository().clearChangeListeners(this);                                  
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);           
        repositoryService.getGenreRepository().clearDeleteListeners(this);           
        repositoryService.getGenreRepository().clearUpdateListeners(this);
        
        //add listeners
        repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);                                  
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);           
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
        repositoryService.getGenreRepository().addUpdateListener(this::changed, this);
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setListValue();
    }
    
    private void sort() {
        genreListView.getSelectionModel().clearSelection();
        genreListView.getItems().sort(Comparator.comparing(Genre::getName));
    }  
    
    @FXML
    private void onMouseClickGenreList(MouseEvent mouseEvent) {    
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {           
            Genre selectedItem = genreListView.getSelectionModel().getSelectedItem();
            // если лкм выбрана запись - показать её
            if (selectedItem != null && selectedItem.getId() != 0) {
                // Дозагрузка
                Genre genre = repositoryService.getGenreRepository().selectById(selectedItem.getId());
                requestPageService.genrePane(genre);
            }           
        }
    }
    
}
