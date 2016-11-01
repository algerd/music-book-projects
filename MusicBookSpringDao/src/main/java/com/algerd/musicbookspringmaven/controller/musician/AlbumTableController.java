package com.algerd.musicbookspringmaven.controller.musician;

import com.algerd.musicbookspringmaven.controller.BaseIncludeController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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
import com.algerd.musicbookspringmaven.entity.Album;
import com.algerd.musicbookspringmaven.entity.MusicianAlbum;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.ADD_MUSICIAN_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.DELETE_MUSICIAN_ALBUM;
import static com.algerd.musicbookspringmaven.service.impl.ContextMenuItemType.EDIT_MUSICIAN_ALBUM;

public class AlbumTableController extends BaseIncludeController<MusicianPaneController> {
   
    private MusicianAlbum selectedItem;   
    
    @FXML
    private AnchorPane albumTable;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<MusicianAlbum> albumTableView;
    @FXML
    private TableColumn<MusicianAlbum, String> artistColumn;
    @FXML
    private TableColumn<MusicianAlbum, String> albumColumn;   
    @FXML
    private TableColumn<MusicianAlbum, Integer> yearColumn;
    @FXML
    private TableColumn<MusicianAlbum, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().getArtist().nameProperty());
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().nameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().yearProperty().asObject());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbum().ratingProperty().asObject());
    }

    @Override
    public void bootstrap() {
        setTableValue();
        initListeners();
        initRepositoryListeners();
        titleLabel.setText("Альбомы с авторством музыканта " + paneController.getMusician().getName());
    }
    
    private void setTableValue() {
        List<MusicianAlbum> musicianAlbums = repositoryService.getMusicianAlbumRepository().selectJoinByMusician(paneController.getMusician());
        clearSelectionTable();
        albumTableView.getItems().clear();
        albumTableView.setItems(FXCollections.observableArrayList(musicianAlbums));
        sort();
        Helper.setHeightTable(albumTableView, 10);  
    }
    
    private void initListeners() {
        albumTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = albumTableView.getSelectionModel().getSelectedItem()
        );          
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianAlbumRepository().clearChangeListeners(this);                                   
        repositoryService.getAlbumRepository().clearDeleteListeners(this);           
        repositoryService.getAlbumRepository().clearUpdateListeners(this);       
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);      
        repositoryService.getArtistRepository().clearDeleteListeners(this);   
        
        //add listeners
        repositoryService.getMusicianAlbumRepository().addChangeListener(this::changed, this);                                   
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);           
        repositoryService.getAlbumRepository().addUpdateListener(this::changed, this);       
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);      
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);   
    }
    
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        albumTableView.getItems().sort(Comparator.comparingInt((MusicianAlbum musicianAlbum) -> musicianAlbum.getAlbum().getYear()).reversed());
    }
    
    private void clearSelectionTable() {
        albumTableView.getSelectionModel().clearSelection();
        selectedItem = null;
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
                Album album = repositoryService.getAlbumRepository().selectById(selectedItem.getAlbum().getId());
                requestPageService.albumPane(album);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianAlbum newMusicianAlbum = new MusicianAlbum();
            newMusicianAlbum.setId_musician(paneController.getMusician().getId());
            contextMenuService.add(ADD_MUSICIAN_ALBUM, newMusicianAlbum);                    
            if (selectedItem != null) {
                contextMenuService.add(EDIT_MUSICIAN_ALBUM, selectedItem);
                contextMenuService.add(DELETE_MUSICIAN_ALBUM, selectedItem);     
            }
            contextMenuService.show(paneController.getView(), mouseEvent);            
        }
    }  
    
}
