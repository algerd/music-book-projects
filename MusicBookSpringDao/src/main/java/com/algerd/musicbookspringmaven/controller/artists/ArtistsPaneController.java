package com.algerd.musicbookspringmaven.controller.artists;

import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ARTIST;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ARTIST;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Artist;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.entity.ArtistGenre;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;

public class ArtistsPaneController extends BasePaneController {
   
    private Artist selectedItem;
    private List<Artist> artists;
    // filter properties   
    private String searchString = "";
    private Genre genre;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
          
    //filter
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
    //table
    @FXML
    private TableView<Artist> artistsTable;
    @FXML
    private TableColumn<Artist, Integer> rankColumn;
    @FXML
    private TableColumn<Artist, String> artistColumn;
    @FXML
    private TableColumn<Artist, Integer> ratingColumn; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        initArtistsTable(); 
        initRepositoryListeners();
        initFilters();
        setTableValue();
        initFilterListeners();       
        initGenreChoiceBox();
    } 
    
    private void initRepositoryListeners() { 
        repositoryService.getArtistRepository().clearInsertListeners(this);          
        repositoryService.getArtistRepository().clearDeleteListeners(this);           
        repositoryService.getArtistRepository().clearUpdateListeners(this);       
        repositoryService.getGenreRepository().clearChangeListeners(this);  
        
        repositoryService.getArtistRepository().addInsertListener(this::added, this);          
        repositoryService.getArtistRepository().addDeleteListener(this::deleted, this);           
        repositoryService.getArtistRepository().addUpdateListener(this::updated, this);       
        repositoryService.getGenreRepository().addChangeListener(this::changedGenre, this);          
    }
    
    private void initArtistsTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(artistsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );      
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject()); 
        
        // Добавить слушателя на выбор элемента в таблице.
        artistsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistsTable.getSelectionModel().getSelectedItem()
        );
    }
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    }
    
    private void added(ObservableValue observable, Object oldVal, Object newVal) {
        Artist newEntity = ((WrapChangedEntity<Artist>) newVal).getNew();
        artists.add(newEntity);
        artistsTable.getItems().add(newEntity);
        sort();
        repeatFilter(); 
        clearSelectionTable();
    }
    
    private void deleted(ObservableValue observable, Object oldVal, Object newVal) {
        Artist newEntity = ((WrapChangedEntity<Artist>) newVal).getNew();
        artists.remove(newEntity);
        artistsTable.getItems().remove(newEntity);
        repeatFilter(); 
        clearSelectionTable();
    }
    
    private void updated(ObservableValue observable, Object oldVal, Object newVal) {
        sort();
        repeatFilter();
        clearSelectionTable();
    }
    
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING || 
            !searchString.equals("") || genre.getId() != -1) {
            filter();
        }
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);                      
        resetSearchLabel.setVisible(false);
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
    
    private void filter() {
        ObservableList<Artist> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();       
        for (Artist artist : artists) {          
            //фильтр по жанру артиста
            boolean isGenre = false;
            if (genre.getId() != -1) {              
                List<ArtistGenre> artistGenres = repositoryService.getArtistGenreRepository().selectJoinByArtist(artist);
                for (ArtistGenre artistGenre : artistGenres) {
                    if (artistGenre.getId_genre() == genre.getId()) {
                        isGenre = true;
                        break;
                    } 
                }   
            } else {
                isGenre = true;
            }  
            // результирующий фильтр
            if (isGenre &&
                (artist.getRating() >= getMinRating() && artist.getRating() <= getMaxRating()) &&
                (searchString.equals("") || artist.getName().regionMatches(true, 0, searchString, 0, lengthSearch)))
            {               
                filteredList.add(artist);                
            }
        }                 
        artistsTable.setItems((filteredList.size() < artists.size()) ? filteredList : FXCollections.observableArrayList(artists));
        sort();
        Helper.setHeightTable(artistsTable, 10);      
    }
    
    private void sort() {
        artistsTable.getItems().sort(Comparator.comparingInt(Artist::getRating).reversed());
    }
    
    private void clearSelectionTable() {
        artistsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
     
    @Override
    public void show(Entity entity) {
        resetFilter();
        clearSelectionTable();
    }
    
    public void setTableValue() {
        artists = repositoryService.getArtistRepository().selectAll();
        artistsTable.setItems(FXCollections.observableArrayList(artists));      
        sort();       
        Helper.setHeightTable(artistsTable, 10);  
    }
    
    @FXML
    private void resetFilter() {
        resetSearchField();
        genreChoiceBox.getSelectionModel().selectFirst();
        initFilters();
    } 
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома selectedAlbum;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного selectedAlbum или нового альбома.
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
                requestPageService.artistPane(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ARTIST, new Artist());
            // запретить удаление и редактирование записи с id = 1 (Unknown artist)
            if (selectedItem != null && selectedItem.getId() != 1) {
                contextMenuService.add(EDIT_ARTIST, selectedItem);
                contextMenuService.add(DELETE_ARTIST, selectedItem);                       
            }
            contextMenuService.show(view, mouseEvent);         
        }
    }
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_ARTIST, new Artist());
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
