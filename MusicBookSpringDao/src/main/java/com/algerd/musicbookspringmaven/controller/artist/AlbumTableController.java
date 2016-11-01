package com.algerd.musicbookspringmaven.controller.artist;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import com.algerd.musicbookspringmaven.utils.Helper;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ALBUM;

public class AlbumTableController extends BaseIncludeController<ArtistPaneController> {
    
    private Album selectedItem;
    
    @FXML
    private AnchorPane albumTable;
    @FXML
    private TableView<Album> albumTableView;
    @FXML
    private TableColumn<Album, String> albumNameColumn;
    @FXML
    private TableColumn<Album, Integer> albumYearColumn;
    @FXML
    private TableColumn<Album, String> albumTimeColumn;
    @FXML
    private TableColumn<Album, Integer> albumRatingColumn;  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        albumNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        albumYearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        albumTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        albumRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());                
    }
    
    @Override
    public void bootstrap() {
        setTableValue();
        initListeners();
        initRepositoryListeners();
    }
    
    void initRepositoryListeners() {
        repositoryService.getAlbumRepository().clearInsertListeners(this);          
        repositoryService.getAlbumRepository().clearDeleteListeners(this);           
        repositoryService.getAlbumRepository().clearUpdateListeners(this);
        
        repositoryService.getAlbumRepository().addInsertListener(this::added, this);          
        repositoryService.getAlbumRepository().addDeleteListener(this::deleted, this);           
        repositoryService.getAlbumRepository().addUpdateListener(this::updated, this);
    }
    
    private void setTableValue() {
        ObservableList<Album> albums = 
            FXCollections.observableArrayList(repositoryService.getAlbumRepository().selectByArtist(paneController.getArtist()));
        clearSelectionTable();
        albumTableView.getItems().clear();
        albumTableView.setItems(albums);      
        Helper.setHeightTable(albumTableView, 10);
        sort();
    }
    
    private void initListeners() {
        albumTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = albumTableView.getSelectionModel().getSelectedItem()
        );
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void added(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        if (newEntity.getId_artist() == paneController.getArtist().getId()) {
            albumTableView.getItems().add(newEntity);
            sort();
        }    
    }
    
    private void deleted(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        if (newEntity.getId_artist() == paneController.getArtist().getId()) {
            albumTableView.getItems().removeAll(newEntity);
            clearSelectionTable();
        }
    }
    
    private void updated(ObservableValue observable, Object oldVal, Object newVal) {
        Album oldEntity = ((WrapChangedEntity<Album>) newVal).getOld();
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew(); 
        if (oldEntity.getId_artist() == paneController.getArtist().getId()) {
            albumTableView.getItems().removeAll(oldEntity);
        }
        if (newEntity.getId_artist() == paneController.getArtist().getId()) {
            albumTableView.getItems().add(newEntity);
            sort();
        } 
    }
    
    private void sort() {
        clearSelectionTable();
        albumTableView.getItems().sort(Comparator.comparingInt(Album::getYear));
    }
    
    public void clearSelectionTable() {
        albumTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного альбома;
     * ПКМ - вызов контекстного меню для add, edit, delete выбранного альбома.
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
                requestPageService.albumPane(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            Album album = new Album();
            album.setId_artist(paneController.getArtist().getId());  
            contextMenuService.add(ADD_ALBUM, album);
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && selectedItem.getId() != 1) {
                contextMenuService.add(EDIT_ALBUM, selectedItem);
                contextMenuService.add(DELETE_ALBUM, selectedItem);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);             
        }
    }
      
}
