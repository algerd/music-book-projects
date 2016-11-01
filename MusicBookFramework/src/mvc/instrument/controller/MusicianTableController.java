package mvc.instrument.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN;
import core.Main;
import core.RequestPage;
import db.entity.Musician;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utils.Helper;

public class MusicianTableController implements Initializable {
    
    private Musician selectedItem;
    private InstrumentPaneController instrumentPaneController;
    
    @FXML
    private AnchorPane musicianTable;
    @FXML
    private Label titleLabel;        
    @FXML
    private TableView<Musician> musicianTableView;
    @FXML
    private TableColumn<Musician, Integer> rankColumn;
    @FXML
    private TableColumn<Musician, String> musicianColumn;
    @FXML
    private TableColumn<Musician, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(musicianTableView.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        musicianColumn.setCellValueFactory(cellData ->cellData.getValue().nameProperty());
        ratingColumn.setCellValueFactory(cellData ->cellData.getValue().ratingProperty().asObject());
    } 
    
    public void bootstrap(InstrumentPaneController instrumentPaneController) {
        this.instrumentPaneController = instrumentPaneController;       
        setTableValue();
        initListeners();
        titleLabel.setText("Музыканты инструмента " + instrumentPaneController.getInstrument().getName());
    }
    
    private void setTableValue() {
        clearSelectionTable();
        musicianTableView.getItems().clear();
        List<Musician> musicians = Main.getInstance().getDbLoader().getMusicianTable().selectMusuciansByInstrument(instrumentPaneController.getInstrument());
        musicianTableView.setItems(FXCollections.observableArrayList(musicians));      
        sort();       
        Helper.setHeightTable(musicianTableView, 10);  
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = musicianTableView.getSelectionModel().getSelectedItem();
            }
        );             
        Main.getInstance().getDbLoader().getMusicianTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianTable().updatedProperty().addListener(this::changed);
            
        Main.getInstance().getDbLoader().getInstrumentTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getInstrumentTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getMusicianInstrumentTable().addedProperty().addListener(this::changed);      
        Main.getInstance().getDbLoader().getMusicianInstrumentTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianTable().updatedProperty().addListener(this::changed);
    }
    
    public void clearSelectionTable() {
        musicianTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    private void sort() {
        clearSelectionTable();
        musicianTableView.getItems().sort(Comparator.comparingInt(Musician ::getRating).reversed());
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    /**
     * ЛКМ - зызов окна выбранного элемента;
     * ПКМ - вызов контекстного меню для add, edit, delete.
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
                Musician musician = Main.getInstance().getDbLoader().getMusicianTable().select(selectedItem.getId());
                RequestPage.MUSICIAN_PANE.load(musician);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_MUSICIAN, new Musician());                    
            if (selectedItem != null) {
                Musician musician = Main.getInstance().getDbLoader().getMusicianTable().select(selectedItem.getId());
                contextMenu.add(EDIT_MUSICIAN, musician);
                contextMenu.add(DELETE_MUSICIAN, musician);     
            }
            contextMenu.show(instrumentPaneController.getInstrumentPane(), mouseEvent);    
        }
    }
    
}
