package com.algerd.musicbookspringmaven.controller.songs;

import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_SONG;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_SONG;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_SONG;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.entity.SongGenre;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Song;

public class SongsPaneController extends BasePaneController implements Initializable {
   
    private Song selectedItem;
    private List<Song> songs;   
    // filter properties   
    private String searchString = "";  
    private Genre genre;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
       
    @FXML
    private ChoiceBox<Genre> genreChoiceBox;
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner;    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    @FXML
    private ChoiceBox<String> searchChoiceBox;   
    /* ************ songsTable *************** */
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableColumn<Song, Integer> rankColumn;
    @FXML
    private TableColumn<Song, String> songColumn;
    @FXML
    private TableColumn<Song, String> artistColumn;
    @FXML
    private TableColumn<Song, String> albumColumn;
    @FXML
    private TableColumn<Song, Integer> yearColumn;            
    @FXML
    private TableColumn<Song, Integer> ratingColumn;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTable();
        initFilters();
        setTableValue();
        initListeners();
        initRepositoryListeners();
        initFilterListeners();      
        initGenreChoiceBox();
        initSearchChoiceBox();   
    }
    
    @Override
    public void show(Entity entity) {
        resetFilter();
        clearSelectionTable();
    }   
      
    private void initTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(songsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        songColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlbum().getArtist().getName()));
        albumColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlbum().getName()));
        yearColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAlbum().getYear()).asObject());
        ratingColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRating()).asObject());                               
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);       
        resetSearchLabel.setVisible(false);
    }
    
    private void setTableValue() {
        songs = repositoryService.getSongRepository().selectJoin();    
        songsTable.setItems(FXCollections.observableArrayList(songs));      
        sort();       
        Helper.setHeightTable(songsTable, 10);  
    }
    
    private void initListeners() {
        songsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = songsTable.getSelectionModel().getSelectedItem()
        );      
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getArtistRepository().clearChangeListeners(this);                 
        repositoryService.getAlbumRepository().clearChangeListeners(this);                       
        repositoryService.getSongRepository().clearChangeListeners(this);                       
        repositoryService.getGenreRepository().clearChangeListeners(this);   
        
        //add listeners
        repositoryService.getArtistRepository().addChangeListener(this::changed, this);                 
        repositoryService.getAlbumRepository().addChangeListener(this::changed, this);                       
        repositoryService.getSongRepository().addChangeListener(this::changed, this);                       
        repositoryService.getGenreRepository().addChangeListener(this::changedGenre, this);          
    }
    
    
    private void initFilterListeners() { 
        minRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());
            
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();                     
            filter();   
        });
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                genre = newValue;
                filter();
            }
        });
    }
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        songsTable.getItems().clear();
        setTableValue();
        repeatFilter();
    }
    
    private void initGenreChoiceBox() {
        Helper.initEntityChoiceBox(genreChoiceBox);
        genre = new Genre();
        genre.setId(-1);
        genre.setName("All genres");
        genreChoiceBox.getItems().clear();
        genreChoiceBox.getItems().add(genre);
        List<Genre> genres = repositoryService.getGenreRepository().selectAll();
        genres.sort(Comparator.comparing(Genre::getName));
        genreChoiceBox.getItems().addAll(genres);
        genreChoiceBox.getSelectionModel().selectFirst();
    }
         
    private void initSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("Song", "Album", "Artist");
        searchChoiceBox.getSelectionModel().selectFirst();
        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> repeatFilter()
        );
    }
    
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING || 
            !searchString.equals("") || genre.getId() != -1) 
        {
            filter();
        }
    }
      
    private void filter() {
        ObservableList<Song> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();        
        int selectedIndex = searchChoiceBox.getSelectionModel().getSelectedIndex();
                
        for (Song song : songs) {
            //фильтр по жанру альбома
            boolean isGenre = false;
            if (genre.getId() != -1) {              
                List<SongGenre> songGenres = repositoryService.getSongGenreRepository().selectJoinBySong(song);
                for (SongGenre songGenre : songGenres) {
                    if (songGenre.getId_genre() == genre.getId()) {
                        isGenre = true;
                        break;
                    } 
                }   
            } else {
                isGenre = true;
            } 
            // результирующий фильтр
            if (// genre    
                isGenre 
                // rating
                && (song.getRating() >= getMinRating() && song.getRating() <= getMaxRating()) 
                // search
                && (searchString.equals("")
                 // Проверить выбор в SearchChoiceBox (0 - Song, 1 - Album, 2 - Artist)   
                || (selectedIndex == 0 && song.getName().regionMatches(true, 0, searchString, 0, lengthSearch)) 
                || (selectedIndex == 1 && song.getAlbum().getName().regionMatches(true, 0, searchString, 0, lengthSearch)) 
                || (selectedIndex == 2 && song.getAlbum().getArtist().getName().regionMatches(true, 0, searchString, 0, lengthSearch)))    
            ){                                      
                filteredList.add(song);                
            }
        }
        songsTable.setItems((filteredList.size() < songs.size()) ? filteredList : FXCollections.observableArrayList(songs));
        sort();
        Helper.setHeightTable(songsTable, 10);
    }
    
    private void sort() {
        songsTable.getItems().sort(Comparator.comparingInt(Song::getRating).reversed());
    }
    
    private void clearSelectionTable() {
        songsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }   
    
    @FXML
    private void resetFilter() {
        resetSearchField();
        genreChoiceBox.getSelectionModel().selectFirst();
        searchChoiceBox.getSelectionModel().selectFirst();
        initFilters();
    }
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    /**
     * ЛКМ - зызов окна выбранной песни;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранной песни.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();     
        contextMenuService.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Song song = repositoryService.getSongRepository().selectById(selectedItem.getId());
                requestPageService.songPane(song);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            // id = 1 is "Unknown" albom of "Unknown" artist
            Song newSong = new Song();
            newSong.setId_album(1);
            contextMenuService.add(ADD_SONG, newSong);                
            if (selectedItem != null) {
                Song song = repositoryService.getSongRepository().selectById(selectedItem.getId());
                contextMenuService.add(EDIT_SONG, song);
                contextMenuService.add(DELETE_SONG, song);                       
            }
            contextMenuService.show(view, mouseEvent);           
        }
    }
    
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            // id = 1 is "Unknown" albom of "Unknown" artist
            Song newSong = new Song();
            newSong.setId_album(1);
            contextMenuService.add(ADD_SONG, newSong);   
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
    public int getMaxRating() {
        return maxRating.get();
    }
    public void setMaxRating(int value) {
        maxRating.set(value);
    }
    public IntegerProperty maxRatingProperty() {
        return maxRating;
    }
       
    public int getMinRating() {
        return minRating.get();
    }
    public void setMinRating(int value) {
        minRating.set(value);
    }
    public IntegerProperty minRatingProperty() {
        return minRating;
    }
     
}
