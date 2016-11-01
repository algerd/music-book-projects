
package com.algerd.musicbookspringmaven.controller.album;

import com.algerd.musicbookspringmaven.controller.BaseController;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.algerd.musicbookspringmaven.core.Main;
import com.algerd.musicbookspringmaven.core.RequestPage;
import com.algerd.musicbookspringmaven.utils.Helper;
import com.algerd.musicbookspringmaven.entity.Song;
import com.algerd.musicbookspringmaven.dbDriver.impl.WrapChangedEntity;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.ADD_SONG;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.DELETE_SONG;
import static com.algerd.musicbookspringmaven.core.ContextMenuManager.ItemType.EDIT_SONG;

public class SongTableController extends BaseController implements Initializable {
      
    private Song selectedItem;
    private AlbumPaneController albumPaneController;
    
    @FXML
    private TableView<Song> songTableView;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, Integer> songTrackColumn;
    @FXML
    private TableColumn<Song, String> songTimeColumn;
    @FXML
    private TableColumn<Song, Integer> songRatingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService();
        //@Inject
        contextMenu = Main.getInstance().getContextMenu();
        
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        songTrackColumn.setCellValueFactory(cellData -> cellData.getValue().trackProperty().asObject());
        songTimeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        songRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty().asObject());
    }
    
    public void bootstrap(AlbumPaneController albumPaneController) {
        this.albumPaneController = albumPaneController;
        setTableValue();
        initListeners();
        initRepositoryListeners();
    }
    
    private void initRepositoryListeners() {  
        repositoryService.getAlbumRepository().clearDeleteListeners(this);           
        repositoryService.getAlbumRepository().clearUpdateListeners(this);      
        repositoryService.getArtistRepository().clearDeleteListeners(this);       
        repositoryService.getSongRepository().clearDeleteListeners(this);        
        repositoryService.getSongRepository().clearUpdateListeners(this);  
        
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);           
        repositoryService.getAlbumRepository().addUpdateListener(this::changed, this);      
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);       
        repositoryService.getSongRepository().addDeleteListener(this::deletedSong, this);        
        repositoryService.getSongRepository().addUpdateListener(this::updatedSong, this);        
    }
    
    private void setTableValue() {
        ObservableList<Song> songs = 
            FXCollections.observableArrayList(repositoryService.getSongRepository().selectByAlbum(albumPaneController.getAlbum()));
        clearSelectionTable();
        songTableView.getItems().clear();
        songTableView.setItems(songs);
        Helper.setHeightTable(songTableView, 10);
        sort();
    }
    
    private void initListeners() {
        //Добавить слушателей на выбор элемента в дереве артистов.
        songTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = songTableView.getSelectionModel().getSelectedItem()
        );       
    } 
     
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    } 
    
    // слушатель на добавление песни
    private void addedSong(ObservableValue observable, Object oldVal, Object newVal) {
        Song newEntity = ((WrapChangedEntity<Song>) newVal).getNew();
        if (newEntity.getId_album() == albumPaneController.getAlbum().getId()) {
            songTableView.getItems().add(newEntity);
            sort();
        }    
    }
    
    // слушатель на удаление песни
    private void deletedSong(ObservableValue observable, Object oldVal, Object newVal) {
        Song newEntity = ((WrapChangedEntity<Song>) newVal).getNew();
        if (newEntity.getId_album() == albumPaneController.getAlbum().getId()) {
            songTableView.getItems().add(newEntity);
            clearSelectionTable();
        }       
    }
    
    // слушатель на редактирование песни
    private void updatedSong(ObservableValue observable, Object oldVal, Object newVal) {
        Song oldEntity = ((WrapChangedEntity<Song>) newVal).getOld();
        Song newEntity = ((WrapChangedEntity<Song>) newVal).getNew(); 
        if (oldEntity.getId_album() == albumPaneController.getAlbum().getId()) {
            songTableView.getItems().removeAll(oldEntity);
        }
        if (newEntity.getId_album() == albumPaneController.getAlbum().getId()) {
            songTableView.getItems().add(newEntity);
            sort();
        } 
    }      
    
    private void clearSelectionTable() {
        songTableView.getSelectionModel().clearSelection();
        selectedItem = null;
    }
    
    private void sort() {
        clearSelectionTable();
        songTableView.getItems().sort(Comparator.comparingInt(Song::getTrack));       
    }
    
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
                RequestPage.SONG_PANE.load(selectedItem);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            Song song = new Song();
            song.setId_album(albumPaneController.getAlbum().getId());
            contextMenu.add(ADD_SONG, song);                    
            if (selectedItem != null) {
                contextMenu.add(EDIT_SONG, selectedItem);
                contextMenu.add(DELETE_SONG, selectedItem);     
            }
            contextMenu.show(albumPaneController.getAlbumPane(), mouseEvent);           
        }
    }   

}
