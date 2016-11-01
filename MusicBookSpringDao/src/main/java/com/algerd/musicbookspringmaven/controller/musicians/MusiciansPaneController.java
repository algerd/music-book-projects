package com.algerd.musicbookspringmaven.controller.musicians;

import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_MUSICIAN;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.Params;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Genre;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.MusicianGenre;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;

public class MusiciansPaneController extends BasePaneController {

    private Musician selectedItem;
    private List<Musician> musicians;
    // filter properties   
    private String searchString = "";  
    private Genre genre;
    private Instrument instrument;
    private final IntegerProperty minRating = new SimpleIntegerProperty();
    private final IntegerProperty maxRating = new SimpleIntegerProperty();
    
    @FXML
    private Spinner<Integer> minRatingSpinner; 
    @FXML
    private Spinner<Integer> maxRatingSpinner;
    @FXML
    private ChoiceBox<Genre> genreChoiceBox;
    @FXML
    private ChoiceBox<Instrument> instrumentChoiceBox;        
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel;   
    /* ************ musiciansTable *************** */   
    @FXML
    private TableView<Musician> musiciansTable;
    @FXML
    private TableColumn<Musician, Integer> rankColumn;  
    @FXML
    private TableColumn<Musician, String> nameColumn;
    @FXML
    private TableColumn<Musician, String> dobColumn;
    @FXML
    private TableColumn<Musician, String> dodColumn;
    @FXML
    private TableColumn<Musician, String> countryColumn;  
    @FXML
    private TableColumn<Musician, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTable();
        initFilters();
        setTableValue();
        initListeners(); 
        initRepositoryListeners();
        initGenreChoiceBox();
        initInstrumentChoiceBox();
        initFilterListeners(); 
    } 
    
    @Override
    public void show(Entity entity) {
        resetFilter();
        clearSelectionTable();
    } 
    
    private void initTable() { 
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(musiciansTable.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        dobColumn.setCellValueFactory(cellData -> cellData.getValue().date_of_birthProperty());
        dodColumn.setCellValueFactory(cellData -> cellData.getValue().date_of_deathProperty());
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());        
    }

    private void initFilters() {
        setMinRating(Params.MIN_RATING);
        setMaxRating(Params.MAX_RATING);
        Helper.initIntegerSpinner(minRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MIN_RATING, true, minRating);       
        Helper.initIntegerSpinner(maxRatingSpinner, Params.MIN_RATING, Params.MAX_RATING, Params.MAX_RATING, true, maxRating);       
        resetSearchLabel.setVisible(false);
    }
    
    private void setTableValue() {
        musicians = repositoryService.getMusicianRepository().selectAll();
        musiciansTable.setItems(FXCollections.observableArrayList(musicians));      
        sort();       
        Helper.setHeightTable(musiciansTable, 10);  
    }
    
    private void initListeners() {
        musiciansTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = musiciansTable.getSelectionModel().getSelectedItem()
        );                 
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianRepository().clearChangeListeners(this);                         
        repositoryService.getMusicianGenreRepository().clearChangeListeners(this);                        
        repositoryService.getGenreRepository().clearChangeListeners(this);     
        
        //add listeners
        repositoryService.getMusicianRepository().addChangeListener(this::changed, this);                         
        repositoryService.getMusicianGenreRepository().addChangeListener(this::changed, this);                        
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
        instrumentChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {    
                instrument = newValue;
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
    
    private void initInstrumentChoiceBox() {
        Helper.initEntityChoiceBox(instrumentChoiceBox);
        instrument = new Instrument();
        instrument.setId(-1);
        instrument.setName("All instruments");
        instrumentChoiceBox.getItems().clear();
        instrumentChoiceBox.getItems().add(instrument);
        List<Instrument> instruments = repositoryService.getInstrumentRepository().selectAll();
        instruments.sort(Comparator.comparing(Instrument::getName));
        instrumentChoiceBox.getItems().addAll(instruments);
        instrumentChoiceBox.getSelectionModel().selectFirst();
    }
    
    private void changedGenre(ObservableValue observable, Object oldVal, Object newVal) {
        initGenreChoiceBox();
        resetFilter();
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        musiciansTable.getItems().clear();
        setTableValue();
        repeatFilter();
    }
    
    private void clearSelectionTable() {
        musiciansTable.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    private void sort() {
        musiciansTable.getItems().sort(Comparator.comparingInt(Musician::getRating).reversed());
    }
    
    @FXML
    private void resetFilter() {
        resetSearchField();
        genreChoiceBox.getSelectionModel().selectFirst();
        instrumentChoiceBox.getSelectionModel().selectFirst();
        initFilters();
    }
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
    }
    
    private void filter() {
        ObservableList<Musician> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();        
        
        for (Musician musician : musicians) {
            //фильтр по жанру
            boolean isGenre = false;
            if (genre.getId() != -1) {
                List<MusicianGenre> musicianGenres = repositoryService.getMusicianGenreRepository().selectJoinByMusician(musician);
                for (MusicianGenre musicianGenre : musicianGenres) {
                    if (musicianGenre.getGenre().equals(genre)) {
                        isGenre = true;
                        break;
                    } 
                }                
            } else {
                isGenre = true;
            } 
            //фильтр по инструменту 
            boolean isInstrument = false;
            if (instrument.getId() != -1) {
                List<MusicianInstrument> musicianInstruments = repositoryService.getMusicianInstrumentRepository().selectJoinByMusician(musician);
                for (MusicianInstrument musicianInstrument : musicianInstruments) {
                    if (musicianInstrument.getInstrument().equals(instrument)) {
                        isInstrument = true;
                        break;
                    } 
                }                
            } else {
                isInstrument = true;
            } 
            // результирующий фильтр
            if (// genre   
                isGenre
                // instrument    
                && isInstrument
                // rating
                && (musician.getRating() >= getMinRating() && musician.getRating() <= getMaxRating()) 
                // search
                && (searchString.equals("") 
                || musician.getName().regionMatches(true, 0, searchString, 0, lengthSearch))    
            ) {                                      
                filteredList.add(musician);                
            }
        }
        musiciansTable.setItems((filteredList.size() < musicians.size()) ? filteredList : FXCollections.observableArrayList(musicians));
        sort();
        Helper.setHeightTable(musiciansTable, 10);
    }
    
    private void repeatFilter() {
        // если были изменены поля фильтра или был введён поиск
        if (getMinRating() != Params.MIN_RATING || getMaxRating() != Params.MAX_RATING 
                || !searchString.equals("") || genre.getId() != -1 || instrument.getId() != -1) 
        {
            filter();
        }
    }
       
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
                requestPageService.musicianPane(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_MUSICIAN, new Musician());
            if (selectedItem != null  && selectedItem.getId() != 1) {
                contextMenuService.add(EDIT_MUSICIAN, selectedItem);
                contextMenuService.add(DELETE_MUSICIAN, selectedItem);                       
            }
            contextMenuService.show(view, mouseEvent);       
        }
    }
      
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_MUSICIAN, null);
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
