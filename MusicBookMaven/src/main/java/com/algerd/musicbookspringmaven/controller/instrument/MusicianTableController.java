package com.algerd.musicbookspringmaven.controller.instrument;

import com.algerd.musicbookspringmaven.controller.BaseController;
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
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.RequestPage;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.utils.Helper;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.ADD_MUSICIAN;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.DELETE_MUSICIAN;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.EDIT_MUSICIAN;

public class MusicianTableController extends BaseController implements Initializable {
  
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
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
        
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
        initRepositoryListeners();
        titleLabel.setText("Музыканты инструмента " + instrumentPaneController.getInstrument().getName());
    }
    
    private void setTableValue() {
        clearSelectionTable();
        musicianTableView.getItems().clear();
        List<Musician> musicians = Main.getInstance().getRepositoryService().getMusicianRepository().selectMusuciansByInstrument(instrumentPaneController.getInstrument());
        musicianTableView.setItems(FXCollections.observableArrayList(musicians));      
        sort();       
        Helper.setHeightTable(musicianTableView, 10);  
    }
    
    private void initRepositoryListeners() {
        repositoryService.getMusicianInstrumentRepository().clearChangeListeners(this); 
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);           
        repositoryService.getInstrumentRepository().clearDeleteListeners(this);           
        repositoryService.getInstrumentRepository().clearUpdateListeners(this);       
        
        repositoryService.getMusicianInstrumentRepository().addChangeListener(this::changed, this); 
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);           
        repositoryService.getInstrumentRepository().addDeleteListener(this::changed, this);           
        repositoryService.getInstrumentRepository().addUpdateListener(this::changed, this);       
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        musicianTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = musicianTableView.getSelectionModel().getSelectedItem()
        );                   
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
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();
        contextMenu.clear();  
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать её
            if (selectedItem != null) { 
                Musician musician = Main.getInstance().getRepositoryService().getMusicianRepository().selectById(selectedItem.getId());
                RequestPage.MUSICIAN_PANE.load(musician);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenu.add(ADD_MUSICIAN, new Musician());                    
            if (selectedItem != null) {
                Musician musician = Main.getInstance().getRepositoryService().getMusicianRepository().selectById(selectedItem.getId());
                contextMenu.add(EDIT_MUSICIAN, musician);
                contextMenu.add(DELETE_MUSICIAN, musician);     
            }
            contextMenu.show(instrumentPaneController.getInstrumentPane(), mouseEvent);    
        }
    }
    
}
