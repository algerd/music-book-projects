package com.algerd.musicbookspringmaven.controller.genre;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_ALBUM;
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.AlbumGenre;
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
import com.algerd.musicbookspringmaven.utils.Helper;

public class AlbumGenreTableController extends BaseIncludeController<GenrePaneController> {
      
    private AlbumGenre selectedItem;
    
    @FXML
    private AnchorPane albumGenreTable;
    @FXML
    private Label titleLabel;    
    @FXML
    private TableView<AlbumGenre> albumTableView;
    @FXML
    private TableColumn<AlbumGenre, Integer> rankColumn;
    @FXML
    private TableColumn<AlbumGenre, String> albumColumn;
    @FXML
    private TableColumn<AlbumGenre, Integer> yearColumn;
    @FXML
    private TableColumn<AlbumGenre, String> artistColumn;   
    @FXML
    private TableColumn<AlbumGenre, Integer> ratingColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rankColumn.setCellValueFactory(
            cellData -> new SimpleIntegerProperty(albumTableView.getItems().indexOf(cellData.getValue()) + 1).asObject()
        );
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().nameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().yearProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().ratingProperty().asObject());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().getArtist().nameProperty());
    }
    
    @Override
    public void bootstrap() {      
        setTableValue();
        initListeners();
        initRepositoryListeners();  
        titleLabel.setText("Альбомы жанра " + paneController.getGenre().getName());
    }
    
    private void initRepositoryListeners() {
        repositoryService.getArtistRepository().clearDeleteListeners(this);                  
        repositoryService.getAlbumRepository().clearDeleteListeners(this);           
        repositoryService.getAlbumRepository().clearUpdateListeners(this);                
        repositoryService.getGenreRepository().clearDeleteListeners(this);           
        repositoryService.getGenreRepository().clearUpdateListeners(this);       
        repositoryService.getAlbumGenreRepository().clearChangeListeners(this);      
        
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);                  
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);           
        repositoryService.getAlbumRepository().addUpdateListener(this::changed, this);                
        repositoryService.getGenreRepository().addDeleteListener(this::changed, this);           
        repositoryService.getGenreRepository().addUpdateListener(this::changed, this);       
        repositoryService.getAlbumGenreRepository().addChangeListener(this::changed, this);      
    }
    
    private void setTableValue() { 
        clearSelectionTable();
        albumTableView.getItems().clear();
        List<AlbumGenre> albumGenres = repositoryService.getAlbumGenreRepository().selectJoinByGenre(paneController.getGenre());  
        albumTableView.setItems(FXCollections.observableArrayList(albumGenres));      
        sort();
        Helper.setHeightTable(albumTableView, 10);      
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        albumTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = albumTableView.getSelectionModel().getSelectedItem()
        );       
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        if (paneController != null) {
            setTableValue();
        }    
    }
      
    private void sort() {
        clearSelectionTable();
        albumTableView.getItems().sort(Comparator.comparingInt((AlbumGenre albumGenre) -> albumGenre.getAlbum().getRating()).reversed());
    }
    
    public void clearSelectionTable() {
        albumTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    /**
     * ЛКМ - зызов окна выбранного артиста;
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
                Album album = repositoryService.getAlbumRepository().selectById(selectedItem.getAlbum().getId());
                requestPageService.albumPane(album);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            contextMenuService.add(ADD_ALBUM, new Album());
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && selectedItem.getId() != 1) {
                Album album = repositoryService.getAlbumRepository().selectById(selectedItem.getAlbum().getId());
                contextMenuService.add(EDIT_ALBUM, album);
                contextMenuService.add(DELETE_ALBUM, album);                       
            }
            contextMenuService.show(paneController.getView(), mouseEvent);          
        }
    }
    
}
