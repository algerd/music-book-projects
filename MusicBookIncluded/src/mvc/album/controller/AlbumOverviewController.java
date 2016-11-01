
package mvc.album.controller;

import utils.Helper;
import db.entity.Album;
import db.entity.Artist;
import db.entity.Entity;
import db.entity.Genre;
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

public class AlbumOverviewController implements Initializable {
    
    private Album selectedItem;
    private ObservableList<Album> albums;  
    // filter   
    private String searchString = "";
    private Genre genre;    
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
    private final IntegerProperty minYear = new SimpleIntegerProperty();
    private final IntegerProperty maxYear = new SimpleIntegerProperty();
        
    @FXML
    private AnchorPane albumOverview;
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
    /* ************ albumOverviewTable *************** */
    @FXML
    private TableView<Album> albumOverviewTable;
    @FXML
    private TableColumn<Album, Integer> rankColumn;
    @FXML
    private TableColumn<Album, Artist> artistColumn;
    @FXML
    private TableColumn<Album, String> albumColumn;
    @FXML
    private TableColumn<Album, Integer> yearColumn;
    @FXML
    private TableColumn<Album, Artist> genreColumn;
    @FXML
    private TableColumn<Album, Integer> ratingColumn;
                               
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenreChoiceBox();
        initFilters();
        initFilterListeners(); 
        initSearchChoiceBox(); 
        initAlbumOverviewTable();
                             
        albums = Main.main.getLoader().getAlbumTable().getEntities();
        sortEntities();
        albums.sort(Comparator.comparingInt(Album::getRank));              
        albumOverviewTable.setItems(albums);
        Helper.setHeightTable(albumOverviewTable);
              
        // изменить rank артистов и пересортировать при добавлении/удалении артиста
        albums.addListener(this::changedEntities);
        
        // изменить rank артистов и пересортировать при изменении рейтинга артиста
        Main.main.getLoader().getAlbumTable().updatedEntityProperty().addListener((observable, oldValue, newValue) -> {                      
                Album oldEntity = ((WrapUpdatedEntity<Album>) newValue).getOldEntity();
                Album newEntity = ((WrapUpdatedEntity<Album>) newValue).getNewEntity();
                if (oldEntity.getRating() != newEntity.getRating()) {
                    sortEntities();
                    albums.sort(Comparator.comparingInt(Album::getRank));
                }
                repeatFilter();
            }
        ); 
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
    
    private void initGenreChoiceBox() {
        Helper.initEntityChoiceBox(genreChoiceBox);
        genreChoiceBox.getItems().add(new Genre(-1, "All genres", null));
        genreChoiceBox.getItems().addAll(Main.main.getLoader().getGenreTable().getEntities());
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
        searchChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> repeatFilter()
        );
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            genre = newValue;
            filter();
        });
    }
    
    private void initSearchChoiceBox() {
        searchChoiceBox.getItems().addAll("Album", "Artist");
        searchChoiceBox.getSelectionModel().selectFirst();
    }
    
    private void filter() {
        ObservableList<Album> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();        
        int selectedIndex = searchChoiceBox.getSelectionModel().getSelectedIndex();
        for (Album album : albums) {
            if (// genre    
                (genre.getId() == -1 || album.getParent().getGenre() == genre) &&
                // rating    
                (album.getRating() >= getMinRating() && album.getRating() <= getMaxRating()) &&
                // year    
                (album.getYear() >= getMinYear() && album.getYear() <= getMaxYear()) &&
                // search    
                (searchString.equals("") ||
                 // Проверить выбор в SearchChoiceBox (0 - Album, 1 - Artist)    
                 (selectedIndex == 0 && album.getName().regionMatches(true, 0, searchString, 0, lengthSearch)) ||
                 (selectedIndex == 1 && album.getParent().getName().regionMatches(true, 0, searchString, 0, lengthSearch)))
            ){               
                filteredList.add(album);              
            }
        }
        albumOverviewTable.setItems((filteredList.size() < albums.size()) ? filteredList : albums);
        Helper.setHeightTable(albumOverviewTable);
    }
    
    /**
     * Повторный вызов поискового поля.
     */
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if ((getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING) || 
            (getMinYear() != Params.MIN_YEAR || getMaxYear() != Params.MAX_YEAR) ||    
            !searchString.equals("")) {
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
                albums.sort(Comparator.comparingInt(Album::getRank));
                repeatFilter();
            }         
        }          
    }
    
    /**
     * Задать rank артистов и отсортровать по нему.
     */
    private void sortEntities() {
        SortedList<Album> sortedAlbums = albums.sorted(Comparator.comparingInt(Album::getRating).reversed());
        for (Album entity : sortedAlbums) {
            entity.setRank(sortedAlbums.indexOf(entity) + 1);
        }
    }
    
    private void initAlbumOverviewTable() {  
        // Добавить слущателей на редактирование артистов - обновить таблицу альбомов при редактировании артистов
        Main.main.getLoader().getArtistTable().updatedEntityProperty().addListener(
             (observable, oldValue, Object) -> albumOverviewTable.refresh()
        );
        // Добавить слущателей на редактирование жанров - обновить таблицу альбомов при редактировании жанров
        Main.main.getLoader().getGenreTable().updatedEntityProperty().addListener(
             (observable, oldValue, Object) -> albumOverviewTable.refresh()
        );
        
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());      
                 
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().parentProperty());
		artistColumn.setCellFactory(col -> {
			TableCell<Album, Artist> cell = new TableCell<Album, Artist>() {
                @Override
                public void updateItem(Artist item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText(item.getName());
                    }
                }
            };
			return cell;
		});
                         
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().parentProperty());
		genreColumn.setCellFactory(col -> {
			TableCell<Album, Artist> cell = new TableCell<Album, Artist>() {
                @Override
                public void updateItem(Artist item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText(item.getGenre().getName());
                    }
                }
            };
			return cell;
		});
            
        // Добавить слушателя на выбор элемента в таблице.
        albumOverviewTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = albumOverviewTable.getSelectionModel().getSelectedItem();
            }
        );
    }
       
    public void show() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedExplorerController().clearSelectionTree();  
        clearSelectionTable();
        resetFilter();
        albumOverview.setVisible(true);
    }
    
    public void hide() {
        albumOverview.setVisible(false);
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
        albumOverviewTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * При ПКМ по таблице альбомов артиста показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        Artist unknownArtist = Main.main.getLoader().getArtistTable().getEntityWithId(1);    
        contextMenu.add(ADD_ALBUM, new Album(unknownArtist));  
        // запретить удаление и редактирование записи с id = 1 (Unknown album)
        if (selectedItem != null && selectedItem.getId() != 1) {
            contextMenu.add(EDIT_ALBUM, selectedItem);
            contextMenu.add(DELETE_ALBUM, selectedItem);                       
        }
        contextMenu.show(albumOverview, mouseEvent);      
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
            Artist unknownArtist = Main.main.getLoader().getArtistTable().getEntityWithId(1);
            contextMenu.add(ADD_ALBUM, new Album(unknownArtist));
            contextMenu.show(albumOverview, mouseEvent);
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
