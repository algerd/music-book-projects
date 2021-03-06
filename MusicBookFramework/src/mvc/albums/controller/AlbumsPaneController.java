
package mvc.albums.controller;

import core.Loadable;
import utils.Helper;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Genre;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.*;
import core.Main;
import core.Params;
import core.RequestPage;
import db.entity.AlbumGenre;
import db.entity.WrapChangedEntity;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class AlbumsPaneController implements Initializable, Loadable {
    
    private Album selectedItem;
    private List<Album> albums;  
    // filter   
    private String searchString = "";
    private Genre genre;    
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
    private final IntegerProperty minYear = new SimpleIntegerProperty();
    private final IntegerProperty maxYear = new SimpleIntegerProperty();
        
    @FXML
    private AnchorPane albumsPane;
    @FXML
    private ChoiceBox<Genre> genreChoiceBox;
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner; 
    @FXML
    private Spinner<Integer> minYearSpinner; 
    @FXML
    private Spinner<Integer> maxYearSpinner;    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    /* ************ albumsTable *************** */
    @FXML
    private TableView<Album> albumsTable;
    @FXML
    private TableColumn<Album, Integer> rankColumn;
    @FXML
    private TableColumn<Album, Album> artistColumn;
    @FXML
    private TableColumn<Album, String> albumColumn;
    @FXML
    private TableColumn<Album, Integer> yearColumn;
    @FXML
    private TableColumn<Album, Integer> ratingColumn;
                               
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initAlbumsTable();
        initFilters();
        setTableValue();
        initFilterListeners();      
        initGenreChoiceBox();
        initSearchChoiceBox();       
    }
    
    private void initAlbumsTable() {        
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(albumsTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());      
              
        artistColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
		artistColumn.setCellFactory(col -> {
			TableCell<Album, Album> cell = new TableCell<Album, Album>() {
                @Override
                public void updateItem(Album item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        Artist artist = Main.getInstance().getDbLoader().getArtistTable().select(item.getId_artist());                 
                        this.setText(artist.getName());                                          
                    }
                }
            };
			return cell;
		}); 
      
        // Добавить слушателя на выбор элемента в таблице.
        albumsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = albumsTable.getSelectionModel().getSelectedItem();
            }
        );
        Main.getInstance().getDbLoader().getAlbumTable().addedProperty().addListener(this::added);          
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::deleted);           
        Main.getInstance().getDbLoader().getAlbumTable().updatedProperty().addListener(this::updated);
        
        Main.getInstance().getDbLoader().getArtistTable().addedProperty().addListener(this::changed);        
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getArtistTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getGenreTable().addedProperty().addListener(this::changedGenre);          
        Main.getInstance().getDbLoader().getGenreTable().deletedProperty().addListener(this::changedGenre);           
        Main.getInstance().getDbLoader().getGenreTable().updatedProperty().addListener(this::changedGenre);
    }  
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        albumsTable.getItems().clear();
        setTableValue();
        repeatFilter();
    }
    
    // слушатель на добавление артиста
    private void added(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        albums.add(newEntity);
        albumsTable.getItems().add(newEntity);
        sort();
        repeatFilter(); 
        clearSelectionTable();
    }
    
    // слушатель на удаление артиста
    private void deleted(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        albums.remove(newEntity);
        albumsTable.getItems().remove(newEntity);
        repeatFilter(); 
        clearSelectionTable();
    }
    
    // слушатель на редактирование артиста
    private void updated(ObservableValue observable, Object oldVal, Object newVal) {
        sort();
        repeatFilter();
        clearSelectionTable();
    }
    
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if ((getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING) || 
            (getMinYear() != Params.MIN_YEAR || getMaxYear() != Params.MAX_YEAR) ||    
            !searchString.equals("") || genre.getId() != -1) 
        {
            filter();
        }
    }
    
    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);        
               
        setMinYear(Params.MIN_YEAR);
        setMaxYear(Params.MAX_YEAR);
        Helper.initIntegerSpinner(minYearSpinner, Params.MIN_YEAR, Params.MAX_YEAR, Params.MIN_YEAR, true, minYear);       
        Helper.initIntegerSpinner(maxYearSpinner, Params.MIN_YEAR, Params.MAX_YEAR, Params.MAX_YEAR, true, maxYear);     
        resetSearchLabel.setVisible(false);      
    }
    
    private void initFilterListeners() {
        minRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxRating.addListener((ObservableValue, oldValue, newValue)-> filter());
        minYear.addListener((ObservableValue, oldValue, newValue)-> filter());
        maxYear.addListener((ObservableValue, oldValue, newValue)-> filter());
        
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
        List<Genre> genres = Main.getInstance().getDbLoader().getGenreTable().select();
        genres.sort(Comparator.comparing(Genre::getName));
        genreChoiceBox.getItems().addAll(genres);
        genreChoiceBox.getSelectionModel().selectFirst();
    }
        
    private void initSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("Album", "Artist");
        searchChoiceBox.getSelectionModel().selectFirst();
        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> repeatFilter()
        );
    }
    
    private void filter() {
        ObservableList<Album> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();        
        int selectedIndex = searchChoiceBox.getSelectionModel().getSelectedIndex();
        for (Album album : albums) {
            //фильтр по жанру альбома
            boolean isGenre = false;
            if (genre.getId() != -1) {              
                List<AlbumGenre> albumGenres = Main.getInstance().getDbLoader().getAlbumGenreTable().selectJoinByAlbum(album);
                for (AlbumGenre albumGenre : albumGenres) {
                    if (albumGenre.getId_genre() == genre.getId()) {
                        isGenre = true;
                        break;
                    } 
                }   
            } else {
                isGenre = true;
            } 
            // результирующий фильтр
            if (// genre    
                isGenre &&
                // rating    
                (album.getRating() >= getMinRating() && album.getRating() <= getMaxRating()) &&
                // year    
                (album.getYear() >= getMinYear() && album.getYear() <= getMaxYear()) &&
                // search    
                (searchString.equals("") ||
                 // Проверить выбор в SearchChoiceBox (0 - Album, 1 - Artist)    
                 (selectedIndex == 0 && album.getName().regionMatches(true, 0, searchString, 0, lengthSearch)) ||
                 (selectedIndex == 1 && Main.getInstance().getDbLoader().getArtistTable().select(album.getId_artist()).getName().regionMatches(true, 0, searchString, 0, lengthSearch)))
            ){               
                filteredList.add(album);              
            }
        }
        albumsTable.setItems((filteredList.size() < albums.size()) ? filteredList : FXCollections.observableArrayList(albums));
        sort();
        Helper.setHeightTable(albumsTable, 10);
    }
    
    private void sort() {
        albumsTable.getItems().sort(Comparator.comparingInt(Album::getRating).reversed());
    }
    
    private void clearSelectionTable() {
        albumsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    @Override
    public void show(Entity entity) {
        resetFilter();
        clearSelectionTable();
    }
    
    private void setTableValue() {
        albums = Main.getInstance().getDbLoader().getAlbumTable().select();
        albumsTable.setItems(FXCollections.observableArrayList(albums));      
        sort();       
        Helper.setHeightTable(albumsTable, 10);  
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
     * ЛКМ - зызов окна выбранного альбома selectedAlbum;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного selectedAlbum или нового альбома.
     */
    @FXML
    private void onMouseClickTable(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();     
        contextMenu.clear();        
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) {
                RequestPage.ALBUM_PANE.load(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) {  
            Album album = new Album();
            album.setId_artist(1);   
            contextMenu.add(ADD_ALBUM, album);  
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && selectedItem.getId() != 1) {
                contextMenu.add(EDIT_ALBUM, selectedItem);
                contextMenu.add(DELETE_ALBUM, selectedItem);                       
            }
            contextMenu.show(albumsPane, mouseEvent);          
        }
    }

    /**
     * При ПКМ по странице артиста показать контекстное меню.
     */
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {         
            Album album = new Album();
            album.setId_artist(1);   
            contextMenu.add(ADD_ALBUM, album);
            contextMenu.show(albumsPane, mouseEvent);
        }      
    }
    
    public int getMaxYear() {
        return maxYear.get();
    }
    public void setMaxYear(int value) {
        maxYear.set(value);
    }
    public IntegerProperty maxYearProperty() {
        return maxYear;
    }
    
    public int getMinYear() {
        return minYear.get();
    }
    public void setMinYear(int value) {
        minYear.set(value);
    }
    public IntegerProperty minYearProperty() {
        return minYear;
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
