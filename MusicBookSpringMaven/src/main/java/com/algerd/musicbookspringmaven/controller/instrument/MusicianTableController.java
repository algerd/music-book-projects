package com.algerd.musicbookspringmaven.controller.instrument;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.entity.Musician;
import com.algerd.musicbookspringmaven.utils.Helper;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_MUSICIAN;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_MUSICIAN;

public class MusicianTableController extends BaseIncludeController<InstrumentPaneController> {
  
    private Musician selectedItem;
    
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
    
    public void bootstrap() {     
        setTableValue();
        initListeners();
        initRepositoryListeners();
        titleLabel.setText("Музыканты инструмента " + paneController.getInstrument().getName());
    }
    
    private void setTableValue() {
        clearSelectionTable();
        musicianTableView.getItems().clear();
        List<Musician> musicians = repositoryService.getMusicianRepository().selectMusuciansByInstrument(paneController.getInstrument());
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
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    /**
     * ЛКМ - зызов окна выбранного элемента;
     * ПКМ - вызов контекстного меню для add, edit, delete.
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
                Musician musician = repositoryService.getMusicianRepository().selectById(selectedItem.getId());
                requestPageService.musicianPane(musician);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_MUSICIAN, new Musician());                    
            if (selectedItem != null) {
                Musician musician = repositoryService.getMusicianRepository().selectById(selectedItem.getId());
                contextMenuService.add(EDIT_MUSICIAN, musician);
                contextMenuService.add(DELETE_MUSICIAN, musician);     
            }
            contextMenuService.show(paneController.getView(), mouseEvent);    
        }
    }
    
}
