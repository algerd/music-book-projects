package com.algerd.musicbookspringmaven.controller.artist;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ARTIST_REFERENCE;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ARTIST_REFERENCE;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ARTIST_REFERENCE;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.entity.ArtistReference;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.utils.Helper;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

public class ArtistReferenceTableController extends BaseIncludeController<ArtistPaneController> {
       
    private ArtistReference selectedItem;
    
    @Autowired
    private Stage primaryStage;
              
    @FXML
    private AnchorPane artistReferenceTable;
    @FXML
    private TableView<ArtistReference> artistReferenceTableView;
    @FXML
    private TableColumn<ArtistReference, String> nameReferenceColumn;
    @FXML
    private TableColumn<ArtistReference, String> referenceColumn;   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameReferenceColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        referenceColumn.setCellValueFactory(cellData -> cellData.getValue().referenceProperty());                     
    }
          
    @Override
    public void bootstrap() {            
        setTableValue();
        initListeners();
        initRepositoryListeners();
    }
    
    private void setTableValue() {
        ObservableList<ArtistReference> artistReferences = 
            FXCollections.observableArrayList(repositoryService.getArtistReferenceRepository().selectByArtist(paneController.getArtist()));
        clearSelectionTable();
        artistReferenceTableView.getItems().clear();
        artistReferenceTableView.setItems(artistReferences);
        sort();
        Helper.setHeightTable(artistReferenceTableView, 10);
    }
    
    private void initListeners() {
        artistReferenceTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = artistReferenceTableView.getSelectionModel().getSelectedItem()         
        ); 
    }
    
    public void initRepositoryListeners() {      
        repositoryService.getArtistReferenceRepository().clearInsertListeners(this);
        repositoryService.getArtistReferenceRepository().clearUpdateListeners(this);
        repositoryService.getArtistReferenceRepository().clearDeleteListeners(this);
               
        repositoryService.getArtistReferenceRepository().addInsertListener(this::added, this);
        repositoryService.getArtistReferenceRepository().addUpdateListener(this::updated, this);
        repositoryService.getArtistReferenceRepository().addDeleteListener(this::deleted, this);
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void added(ObservableValue observable, Object oldVal, Object newVal) {  
        ArtistReference newEntity = ((WrapChangedEntity<ArtistReference>) newVal).getNew();       
        if (newEntity.getId_artist() == paneController.getArtist().getId()) {
            artistReferenceTableView.getItems().add(newEntity);
            sort();
        }
    }
    
    private void deleted(ObservableValue observable, Object oldVal, Object newVal) {
        ArtistReference newEntity = ((WrapChangedEntity<ArtistReference>) newVal).getNew();
        if (newEntity.getId_artist() == paneController.getArtist().getId()) {
            artistReferenceTableView.getItems().removeAll(newEntity);
            clearSelectionTable();
        }
    }

    private void updated(ObservableValue observable, Object oldVal, Object newVal) {
        ArtistReference oldEntity = ((WrapChangedEntity<ArtistReference>) newVal).getOld();
        ArtistReference newEntity = ((WrapChangedEntity<ArtistReference>) newVal).getNew();
        if (oldEntity.getId_artist() == paneController.getArtist().getId()) {
            artistReferenceTableView.getItems().removeAll(oldEntity);
        }
        if (newEntity.getId_artist() == paneController.getArtist().getId()) {
            artistReferenceTableView.getItems().add(newEntity);
            sort();
        }
    }       
    
    private void sort() {
        clearSelectionTable();
        artistReferenceTableView.getItems().sort(Comparator.comparing(ArtistReference::getName));
    }

    public void clearSelectionTable() {
        artistReferenceTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного альбома.
     */
    @FXML
    private void onMouseClickArtistReferenceTable(MouseEvent mouseEvent) { 
        boolean isShowingContextMenu = contextMenuService.getContextMenu().isShowing();       
        contextMenuService.clear();   
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            // если контекстное меню выбрано, то лкм сбрасывает контекстное меню и выбор в таблице
            if (isShowingContextMenu) {
                clearSelectionTable();
            }
            // если лкм выбрана запись - показать веб-страницу ссылки
            if (selectedItem != null) {
                String errorMessage = "";                
                Desktop desktop = Desktop.getDesktop();  
                if (desktop.isSupported(Desktop.Action.BROWSE)) {  
                    try {  
                         desktop.browse(new URI(selectedItem.getReference()));  
                    } catch (URISyntaxException | IOException ex) {  
                         errorMessage += "Сбой при попытке открытия ссылки.";  
                    }  
                } else {  
                    errorMessage += "На вашей платформе просмотр ссылок не из приложения не поддерживается";
                }              
                if (!errorMessage.equals("")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(primaryStage);
                    alert.setTitle("ERROR");
                    alert.setContentText(errorMessage);           
                    alert.showAndWait();    
                }               
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            ArtistReference artistReference = new ArtistReference();
            artistReference.setId_artist(paneController.getArtist().getId());
            contextMenuService.add(ADD_ARTIST_REFERENCE, artistReference);    
            if (selectedItem != null) {
                contextMenuService.add(EDIT_ARTIST_REFERENCE, selectedItem);
                contextMenuService.add(DELETE_ARTIST_REFERENCE, selectedItem);                           
            }
            contextMenuService.show(paneController.getView(), mouseEvent);      
        }
    }
    
}
