package mvc.artist.controller;

import utils.Helper;
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

public class ArtistOverviewController implements Initializable {
    
    private Artist selectedItem;
    private ObservableList<Artist> artists;
    // filter properties   
    private String searchString = "";
    private Genre genre;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
          
    @FXML
    private AnchorPane artistOverview;
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
    private TableView<Artist> artistOverviewTable;
    @FXML
    private TableColumn<Artist, Integer> rankColumn;
    @FXML
    private TableColumn<Artist, String> artistColumn;
    @FXML
    private TableColumn<Artist, Genre> genreColumn;
    @FXML
    private TableColumn<Artist, Integer> ratingColumn; 
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initGenreChoiceBox();
        initFilters();
        initFilterListeners();        
        initArtistOverviewTable();
               
        artists = Main.main.getLoader().getArtistTable().getEntities();
        sortEntities();
        artists.sort(Comparator.comparingInt(Artist::getRank));              
        artistOverviewTable.setItems(artists);
        Helper.setHeightTable(artistOverviewTable);
        
        // изменить rank артистов и пересортировать при добавлении/удалении артиста
        artists.addListener(this::changedEntities);
        
        // изменить rank артистов и пересортировать при изменении рейтинга артиста
        Main.main.getLoader().getArtistTable().updatedEntityProperty().addListener((observable, oldValue, newValue) -> {                      
                Artist oldEntity = ((WrapUpdatedEntity<Artist>) newValue).getOldEntity();
                Artist newEntity = ((WrapUpdatedEntity<Artist>) newValue).getNewEntity();
                if (oldEntity.getRating() != newEntity.getRating()) {
                    sortEntities();
                    artists.sort(Comparator.comparingInt(Artist::getRank));
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
        
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {           
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();
            filter();   
        });
        
        genreChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            genre = newValue;
            filter();
        });
    }
    
    private void filter() {
        ObservableList<Artist> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();
        for (Artist artist : artists) {
            if ((genre.getId() == -1 || artist.getGenre() == genre) &&
                (artist.getRating() >= getMinRating() && artist.getRating() <= getMaxRating()) &&
                (searchString.equals("") || artist.getName().regionMatches(true, 0, searchString, 0, lengthSearch)))
            {               
                filteredList.add(artist);                
            }
        }                 
        artistOverviewTable.setItems((filteredList.size() < artists.size()) ? filteredList : artists);
        Helper.setHeightTable(artistOverviewTable);
    }
    
    /**
     * Повторный вызов поискового поля.
     */
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING || 
            !searchString.equals("") || genre.getId() != -1) {
            filter();
        }
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
     * Изменить rank артистов(пересортировать) при добавлении/удалении артиста и пересортировать по свойству rank.
     */
    private void changedEntities(ListChangeListener.Change<? extends Entity> change) {
        while (change.next()) {   
            if (change.wasRemoved()) {
                sortEntities();               
                repeatFilter();
            }
            else if (change.wasAdded()) {
                sortEntities();
                artists.sort(Comparator.comparingInt(Artist::getRank));
                repeatFilter();
            }
        }          
    }
    
    /**
     * Задать rank артистов.
     */
    private void sortEntities() {
        SortedList<Artist> sortedArtists = artists.sorted(Comparator.comparingInt(Artist::getRating).reversed());
        for (Artist entity : sortedArtists) {
            entity.setRank(sortedArtists.indexOf(entity) + 1);
        }
    }
    
    private void initArtistOverviewTable() {  
        // Добавить слущателей на редактирование жанров - обновить таблицу артистов при редактировании жанров
        Main.main.getLoader().getGenreTable().updatedEntityProperty().addListener(
             (observable, oldValue, Object) -> artistOverviewTable.refresh()
        );
        
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());       
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject()); 
        // отслеживать смену жанра у артистов
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        genreColumn.setCellFactory(col -> {
			TableCell<Artist, Genre> cell = new TableCell<Artist, Genre>() {
                @Override
                public void updateItem(Genre item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {
                        this.setText(item.getName());
                    }
                }
            };
			return cell;
		});
    
        // Добавить слушателя на выбор элемента в таблице.
        artistOverviewTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = artistOverviewTable.getSelectionModel().getSelectedItem();
            }
        );
    }
    
    public void show() {
        Main.main.getMainController().hideAllPanes();
        Main.main.getMainController().getIncludedExplorerController().clearSelectionTree();
        clearSelectionTable();
        resetFilter();
        artistOverview.setVisible(true);
    }
    
    public void hide() {       
        artistOverview.setVisible(false);
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
        artistOverviewTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * При ПКМ по таблице альбомов артиста показать контекстное меню.
     */
    private void showTableContextMenu(MouseEvent mouseEvent) { 
        ContextMenuManager contextMenu = Main.main.getContextMenu();
        contextMenu.add(ADD_ARTIST, null);
        // запретить удаление и редактирование записи с id = 1 (Unknown artist)
        if (selectedItem != null && selectedItem.getId() != 1) {
            contextMenu.add(EDIT_ARTIST, selectedItem);
            contextMenu.add(DELETE_ARTIST, selectedItem);                       
        }
        contextMenu.show(artistOverview, mouseEvent);      
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
            contextMenu.add(ADD_ARTIST, null);
            contextMenu.show(artistOverview, mouseEvent);
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
