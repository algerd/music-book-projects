package mvc.artist.controller;

import db.entity.ArtistReference;
import db.entity.WrapChangedEntity;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import core.Main;
import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.*;
import utils.Helper;

public class ArtistReferenceTableController implements Initializable {
    
    private ArtistPaneController artistPaneController;
    private ArtistReference selectedItem;

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
    
    public void bootstrap(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;      
        setTableValue();
        initListeners();
    }
    
    private void setTableValue() {
        ObservableList<ArtistReference> artistReferences = 
            FXCollections.observableArrayList(Main.getInstance().getDbLoader().getArtistReferenceTable().selectByArtist(artistPaneController.getArtist()));
        clearSelectionTable();
        artistReferenceTableView.getItems().clear();
        artistReferenceTableView.setItems(artistReferences);
        sort();
        Helper.setHeightTable(artistReferenceTableView, 10);
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        artistReferenceTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = artistReferenceTableView.getSelectionModel().getSelectedItem();
            }
        );        
        Main.getInstance().getDbLoader().getArtistReferenceTable().addedProperty().addListener(this::added);          
        Main.getInstance().getDbLoader().getArtistReferenceTable().deletedProperty().addListener(this::deleted);           
        Main.getInstance().getDbLoader().getArtistReferenceTable().updatedProperty().addListener(this::updated);
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    // слушатель на добавление ссылки артисту
    private void added(ObservableValue observable, Object oldVal, Object newVal) {
        ArtistReference newEntity = ((WrapChangedEntity<ArtistReference>) newVal).getNew();
        if (newEntity.getId_artist() == artistPaneController.getArtist().getId()) {
            artistReferenceTableView.getItems().add(newEntity);
            sort();
        }
    }
    
    // слушатель на удаление ссылки у артиста
    private void deleted(ObservableValue observable, Object oldVal, Object newVal) {
        ArtistReference newEntity = ((WrapChangedEntity<ArtistReference>) newVal).getNew();
        if (newEntity.getId_artist() == artistPaneController.getArtist().getId()) {
            artistReferenceTableView.getItems().removeAll(newEntity);
            clearSelectionTable();
        }
    }

    // слушатель на редактирование ссылки у артиста
    private void updated(ObservableValue observable, Object oldVal, Object newVal) {
        ArtistReference oldEntity = ((WrapChangedEntity<ArtistReference>) newVal).getOld();
        ArtistReference newEntity = ((WrapChangedEntity<ArtistReference>) newVal).getNew();
        if (oldEntity.getId_artist() == artistPaneController.getArtist().getId()) {
            artistReferenceTableView.getItems().removeAll(oldEntity);
        }
        if (newEntity.getId_artist() == artistPaneController.getArtist().getId()) {
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
        ContextMenuManager contextMenu = Main.getInstance().getContextMenu();
        boolean isShowingContextMenu = contextMenu.getContextMenu().isShowing();       
        contextMenu.clear();   
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
                    alert.initOwner(Main.getInstance().getPrimaryStage());
                    alert.setTitle("ERROR");
                    alert.setContentText(errorMessage);           
                    alert.showAndWait();    
                }               
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            ArtistReference artistReference = new ArtistReference();
            artistReference.setId_artist(artistPaneController.getArtist().getId());
            contextMenu.add(ADD_ARTIST_REFERENCE, artistReference);    
            if (selectedItem != null) {
                contextMenu.add(EDIT_ARTIST_REFERENCE, selectedItem);
                contextMenu.add(DELETE_ARTIST_REFERENCE, selectedItem);                           
            }
            contextMenu.show(artistPaneController.getArtistPane(), mouseEvent);      
        }
    }
    
}
