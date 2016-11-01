package mvc.song.controller;

import utils.Helper;
import db.entity.Album;
import db.entity.Entity;
import db.entity.Genre;
import db.entity.Song;
import db.entity.WrapUpdatedEntity;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import mvc.ContextMenuManager;
import static mvc.ContextMenuManager.ItemType.*;
import main.Main;
import main.Params;

public class SongOverviewController implements Initializable {
    
    private Song selectedItem;
    private ObservableList<Song> songs;
    
    // filter properties   
    private String searchString = "";  
    private Genre genre;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
       
    @FXML
    private AnchorPane songOverview;
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
    @FXML
    private TableView<Song> songOverviewTable;
    @FXML
    private TableColumn<Song, Integer> rankColumn;
    @FXML
    private TableColumn<Song, String> songColumn;
    @FXML
    private TableColumn<Song, Album> artistColumn;
    @FXML
    private TableColumn<Song, Album> albumColumn;
    @FXML
    private TableColumn<Song, Album> yearColumn;       
    @FXML
    private TableColumn<Song, Album> genreColumn;       
    @FXML
    private TableColumn<Song, Integer> ratingColumn;  

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenreChoiceBox();
        initFilters();
        initFilterListeners();
        initSearchChoiceBox();
        initSongOverviewTable();
              
        songs = Main.main.getLoader().getSongTable().getEntities();
        sortEntities();
        songs.sort(Comparator.comparingInt(Song::getRank));              
        songOverviewTable.setItems(songs); 
        Helper.setHeightTable(songOverviewTable);
        
        // изменить rank артистов и пересортировать при добавлении/удалении артиста
        songs.addListener(this::changedEntities);
        
        // изменить rank артистов и пересортировать при изменении рейтинга артиста
        Main.main.getLoader().getSongTable().updatedEntityProperty().addListener((observable, oldValue, newValue) -> {                      
                Song oldEntity = ((WrapUpdatedEntity<Song>) newValue).getOldEntity();
                Song newEntity = ((WrapUpdatedEntity<Song>) newValue).getNewEntity();
                if (oldEntity.getRating() != newEntity.getRating()) {
                    sortEntities();
                    songs.sort(Comparator.comparingInt(Song::getRank));
                } 
                repeatFilter();
            }
        ); 
    }
    
    private void initGenreChoiceBox() {
        Helper.initEntityChoiceBox(genreChoiceBox);
        genreChoiceBox.getItems().add(new Genre(-1, "All genres", null));
        genreChoiceBox.getItems().addAll(Main.main.getLoader().getGenreTable().getEntities());
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating); 
        
        resetSearchLabel.setVisible(false);
    }
       
    private void initSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("Song", "Album", "Artist");
        searchChoiceBox.getSelectionModel().selectFirst();
    }
    
    private void initFilterListeners() { 
        minRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());
            
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();                     
            filter();   
        });
        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> repeatFilter()
        );
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            genre = newValue;
            filter();
        });
    }
    
    private void filter() {
        ObservableList<Song> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();        
        int selectedIndex = searchChoiceBox.getSelectionModel().getSelectedIndex();
        for (Song song : songs) {
            if (// genre    
                (genre.getId() == -1 || song.getParent().getParent().getGenre() == genre) &&
                // rating
                (song.getRating() >= getMinRating() && song.getRating() <= getMaxRating()) &&
                // search
                (searchString.equals("") ||
                 // Проверить выбор в SearchChoiceBox (0 - Song, 1 - Album, 2 - Artist)   
                 (selectedIndex == 0 && song.getName().regionMatches(true, 0, searchString, 0, lengthSearch)) ||
                 (selectedIndex == 1 && song.getParent().getName().regionMatches(true, 0, searchString, 0, lengthSearch)) ||
                 (selectedIndex == 2 && song.getParent().getParent().getName().regionMatches(true, 0, searchString, 0, lengthSearch)))    
            ){                                      
                filteredList.add(song);                
            }
        }
        songOverviewTable.setItems((filteredList.size() < songs.size()) ? filteredList : songs);
        Helper.setHeightTable(songOverviewTable);
    }
    
    /**
     * Повторный вызов поискового поля.
     */
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING || !searchString.equals("")) {
            filter();
        }
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
     * Изменить rank артистов(пересортировать) при добавлении/удалении артиста и пересортировать по свойству rank.
     */
    private void changedEntities(ListChangeListener.Change<? extends Entity> change) {
        while (change.next()) {                        
            if (change.wasRemoved()) {
                sortEntities();               
                repeatFilter();
            }
            if (change.wasAdded()) {
                sortEntities();
                songs.sort(Comparator.comparingInt(Song::getRank));
                repeatFilter();
            }          
        }          
    }
    
    /**
     * Задать rank артистов и отсортровать по нему.
     */
    private void sortEntities() {
        SortedList<Song> sortedsongs = songs.sorted(Comparator.comparingInt(Song::getRating).reversed());
        for (Song entity : sortedsongs) {
            entity.setRank(sortedsongs.indexOf(entity) + 1);
        }
    }
       
    private void initSongOverviewTable() {   
        // Добавить слущателей на редактирование жанров - обновить таблицу песен при редактировании жанров
        Main.main.getLoader().getGenreTable().updatedEntityProperty().addListener(
             (observable, oldValue, Object) -> songOverviewTable.refresh()
        );      
        // Добавить слущателей на редактирование артистов - обновить таблицу песен при редактировании артистов
        Main.main.getLoader().getArtistTable().updatedEntityProperty().addListener(
             (observable, oldValue, Object) -> songOverviewTable.refresh()
        );
        // Добавить слущателей на редактирование альбомов - обновить таблицу альбомов при редактировании альбомов
        Main.main.getLoader().getAlbumTable().updatedEntityProperty().addListener(
             (observable, oldValue, Object) -> songOverviewTable.refresh()
        );
        
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());
        songColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());
        
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().parentProperty());
        artistColumn.setCellFactory(col -> {
			TableCell<Song, Album> cell = new TableCell<Song, Album>() {
                @Override
                public void updateItem(Album item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText(item.getParent().getName());
                    }
                }
            };
			return cell;
		});
        
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().parentProperty());
        albumColumn.setCellFactory(col -> {
			TableCell<Song, Album> cell = new TableCell<Song, Album>() {
                @Override
                public void updateItem(Album item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText(item.getName());
                    }
                }
            };
			return cell;
		});
        
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().parentProperty());
        yearColumn.setCellFactory(col -> {
			TableCell<Song, Album> cell = new TableCell<Song, Album>() {
                @Override
                public void updateItem(Album item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText("" + item.getYear());
                    }
                }
            };
			return cell;
		});
                    
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().parentProperty());
        genreColumn.setCellFactory(col -> {
			TableCell<Song, Album> cell = new TableCell<Song, Album>() {
                @Override
                public void updateItem(Album item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText(item.getParent().getGenre().getName());
                    }
                }
            };
			return cell;
		});                     
        
        // Добавить слушателя на выбор элемента в таблице.
        songOverviewTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = songOverviewTable.getSelectionModel().getSelectedItem();
            }
        );
    }
        
    public void show() { 
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedExplorerController().clearSelectionTree();   
        clearSelectionTable();
        resetFilter();
        songOverview.setVisible(true);
    }
    
    public void hide() {
        songOverview.setVisible(false);
        resetFilter();
        clearSelectionTable();
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома selectedAlbum;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного selectedAlbum или нового альбома.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();     
        contextMenu.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                Main.main.getMainController().showTreeEntity(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            showTableContextMenu(mouseEvent);      
        }
    }
    
    private void clearSelectionTable() {
        songOverviewTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * При ПКМ по таблице альбомов артиста показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        Album unknownAlbum = Main.main.getLoader().getAlbumTable().getEntityWithId(1);
        contextMenu.add(ADD_SONG, new Song(unknownAlbum));                
        if (selectedItem != null) {
            contextMenu.add(EDIT_SONG, selectedItem);
            contextMenu.add(DELETE_SONG, selectedItem);                       
        }
        contextMenu.show(songOverview, mouseEvent);      
    } 
    
    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            Album unknownAlbum = Main.main.getLoader().getAlbumTable().getEntityWithId(1);
            contextMenu.add(ADD_SONG, new Song(unknownAlbum));
            contextMenu.show(songOverview, mouseEvent);
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
