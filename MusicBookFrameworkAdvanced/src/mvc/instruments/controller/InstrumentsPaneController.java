
package mvc.instruments.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_INSTRUMENT;
import static core.ContextMenuManager.ItemType.DELETE_INSTRUMENT;
import static core.ContextMenuManager.ItemType.EDIT_INSTRUMENT;
import core.Loadable;
import core.Main;
import core.RequestPage;
import db.driver.Entity;
import db.entity.Instrument;
import db.entity.MusicianInstrument;
import db.repository.RepositoryService;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utils.Helper;

public class InstrumentsPaneController implements Initializable, Loadable {
    
    //@Inject
    private RepositoryService repositoryService;
    
    private Instrument selectedItem;
    private List<Instrument> instruments;
    private String searchString = "";  
    
    @FXML
    private AnchorPane instrumentsPane;
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();  
        
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
        // Добавить слушателя на выбор элемента в таблице.
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
    
    // слушатель на изменение в таблицах
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
                Instrument instrument = repositoryService.getInstrumentRepository().selectById(selectedItem.getId());
                RequestPage.INSTRUMENT_PANE.load(instrument);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_INSTRUMENT, new Instrument());
            if (selectedItem != null && selectedItem.getId() > 1) {
                Instrument instrument = repositoryService.getInstrumentRepository().selectById(selectedItem.getId());
                contextMenu.add(EDIT_INSTRUMENT, instrument);
                contextMenu.add(DELETE_INSTRUMENT, instrument);                       
            }
            contextMenu.show(instrumentsPane, mouseEvent);       
        }
    }
      
    @FXML
    private void showContextMenu(MouseEvent mouseEvent) {
        clearSelectionTable();
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        contextMenu.clear();
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {       
            contextMenu.add(ADD_INSTRUMENT, null);
            contextMenu.show(instrumentsPane, mouseEvent);
        }      
    }
    
}
