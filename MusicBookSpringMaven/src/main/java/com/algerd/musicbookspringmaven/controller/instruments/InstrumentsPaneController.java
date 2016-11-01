
package com.algerd.musicbookspringmaven.controller.instruments;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.controller.BasePaneController;
import com.algerd.musicbookspringmaven.dbDriver.Entity;
import com.algerd.musicbookspringmaven.entity.Instrument;
import com.algerd.musicbookspringmaven.entity.MusicianInstrument;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_INSTRUMENT;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_INSTRUMENT;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_INSTRUMENT;

public class InstrumentsPaneController extends BasePaneController {
   
    private Instrument selectedItem;
    private List<Instrument> instruments;
    private String searchString = "";  
    
    @FXML
    private TextField searchField;
    @FXML
    private Label resetSearchLabel; 
    //table
    @FXML
    private TableView<Instrument> instrumentsTable;
    @FXML
    private TableColumn<Instrument, String> instrumentColumn;
    @FXML
    private TableColumn<Instrument, Instrument> numberOfMusiciansColumn;
    @FXML
    private TableColumn<Instrument, Instrument> averageRatingColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        initInstrumentsTable();
        setTableValue();
        initListeners();
        initRepositoryListeners();
        initFilterListeners();
    } 
    
    @Override
    public void show(Entity entity) {
        resetSearchField();
        clearSelectionTable();
    }   
    
    private void initInstrumentsTable() { 
        instrumentColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());       
        numberOfMusiciansColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        numberOfMusiciansColumn.setCellFactory(col -> {
            TableCell<Instrument, Instrument> cell = new TableCell<Instrument, Instrument>() {
                @Override
                public void updateItem(Instrument item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        this.setText("" + repositoryService.getMusicianInstrumentRepository().countMusiciansByInstrument(item));                   
                    }
                }
            };
			return cell;
        });
        averageRatingColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        averageRatingColumn.setCellFactory(col -> {
            TableCell<Instrument, Instrument> cell = new TableCell<Instrument, Instrument>() {
                @Override
                public void updateItem(Instrument item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!empty) {                        
                        List<MusicianInstrument> musicianInstruments = repositoryService.getMusicianInstrumentRepository().selectJoinByInstrument(item);
                        int averageRating = 0;
                        for (MusicianInstrument musicianInstrument : musicianInstruments) {
                            averageRating += musicianInstrument.getMusician().getRating();
                        }
                        int countMusicians = musicianInstruments.size();
                        this.setText("" + ((countMusicians != 0) ? ((int) (100.0 * averageRating / musicianInstruments.size() + 0.5))/ 100.0 : ""));
                    }
                }
            };
			return cell;
        }); 
    }
        
    private void setTableValue() {
        instruments = repositoryService.getInstrumentRepository().selectAll();    
        instrumentsTable.setItems(FXCollections.observableArrayList(instruments));      
        sort();       
        Helper.setHeightTable(instrumentsTable, 10);  
    }

    private void initListeners() {      
        instrumentsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = instrumentsTable.getSelectionModel().getSelectedItem());  
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianInstrumentRepository().clearChangeListeners(this);               
        repositoryService.getInstrumentRepository().clearChangeListeners(this);
        repositoryService.getMusicianRepository().clearDeleteListeners(this);                         
        
        //add listeners
        repositoryService.getMusicianInstrumentRepository().addChangeListener(this::changed, this);
        repositoryService.getInstrumentRepository().addChangeListener(this::changed, this); 
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);              
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        instrumentsTable.getItems().clear();
        setTableValue();
        repeatFilter();
    }
    
    private void initFilterListeners() {            
        searchField.textProperty().addListener((ObservableValue, oldValue, newValue)-> {
            resetSearchLabel.setVisible(newValue.length() > 0);
            searchString = newValue.trim();                     
            filter();   
        });
    } 
    
    private void repeatFilter() {
        if (!searchString.equals("")) {
            filter();
        }
    }
    
    private void filter() {
        ObservableList<Instrument> filteredList = FXCollections.observableArrayList();
        int lengthSearch = searchString.length();
        
        for (Instrument instrument : instruments) {
            if ((searchString.equals("") || instrument.getName().regionMatches(true, 0, searchString, 0, lengthSearch))) {                                      
                filteredList.add(instrument);                
            }
        }
        instrumentsTable.setItems((filteredList.size() < instruments.size()) ? filteredList : FXCollections.observableArrayList(instruments));
        sort();
        Helper.setHeightTable(instrumentsTable, 10);       
    }
    
    private void sort() {
        instrumentsTable.getItems().sort(Comparator.comparing(Instrument::getName));
    }
    
    private void clearSelectionTable() {
        instrumentsTable.getSelectionModel().clearSelection();
        selectedItem = null;
    } 
    
    @FXML
    private void resetSearchField() {
        searchField.textProperty().setValue("");
        resetSearchLabel.setVisible(false);
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
                Instrument instrument = repositoryService.getInstrumentRepository().selectById(selectedItem.getId());
                requestPageService.instrumentPane(instrument);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_INSTRUMENT, new Instrument());
            if (selectedItem != null && selectedItem.getId() > 1) {
                Instrument instrument = repositoryService.getInstrumentRepository().selectById(selectedItem.getId());
                contextMenuService.add(EDIT_INSTRUMENT, instrument);
                contextMenuService.add(DELETE_INSTRUMENT, instrument);                       
            }
            contextMenuService.show(view, mouseEvent);       
        }
    }
      
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        contextMenuService.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenuService.add(ADD_INSTRUMENT, null);
            contextMenuService.show(view, mouseEvent);
        }      
    }
    
}
