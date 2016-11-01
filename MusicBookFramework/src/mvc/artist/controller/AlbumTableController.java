package mvc.artist.controller;

import db.entity.Album;
import db.entity.WrapChangedEntity;
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
import javafx.scene.layout.AnchorPane;
import core.Main;
import core.RequestPage;
import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_ALBUM;
import static core.ContextMenuManager.ItemType.DELETE_ALBUM;
import static core.ContextMenuManager.ItemType.EDIT_ALBUM;
import utils.Helper;

public class AlbumTableController implements Initializable {

    private Album selectedItem;
    private ArtistPaneController artistPaneController;
    
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
    
    public void bootstrap(ArtistPaneController artistPaneController) {
        this.artistPaneController = artistPaneController;
        setTableValue();
        initListeners();
    }
    
    private void setTableValue() {
        ObservableList<Album> albums = 
            FXCollections.observableArrayList(Main.getInstance().getDbLoader().getAlbumTable().selectByArtist(artistPaneController.getArtist()));
        clearSelectionTable();
        albumTableView.getItems().clear();
        albumTableView.setItems(albums);      
        Helper.setHeightTable(albumTableView, 10);
        sort();
    }
    
    private void initListeners() {
        // Добавить слушателя на выбор элемента в таблице.
        albumTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {             
                selectedItem = albumTableView.getSelectionModel().getSelectedItem();
            }
        );
        Main.getInstance().getDbLoader().getAlbumTable().addedProperty().addListener(this::added);          
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::deleted);           
        Main.getInstance().getDbLoader().getAlbumTable().updatedProperty().addListener(this::updated);
    }
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    // слушатель на добавление альбома
    private void added(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        if (newEntity.getId_artist() == artistPaneController.getArtist().getId()) {
            albumTableView.getItems().add(newEntity);
            sort();
        }    
    }
    
    // слушатель на удаление альбома
    private void deleted(ObservableValue observable, Object oldVal, Object newVal) {
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew();
        if (newEntity.getId_artist() == artistPaneController.getArtist().getId()) {
            albumTableView.getItems().removeAll(newEntity);
            clearSelectionTable();
        }
    }
    
    // слушатель на редактирование альбома
    private void updated(ObservableValue observable, Object oldVal, Object newVal) {
        Album oldEntity = ((WrapChangedEntity<Album>) newVal).getOld();
        Album newEntity = ((WrapChangedEntity<Album>) newVal).getNew(); 
        if (oldEntity.getId_artist() == artistPaneController.getArtist().getId()) {
            albumTableView.getItems().removeAll(oldEntity);
        }
        if (newEntity.getId_artist() == artistPaneController.getArtist().getId()) {
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
                RequestPage.ALBUM_PANE.load(selectedItem);
            }           
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            Album album = new Album();
            album.setId_artist(artistPaneController.getArtist().getId());  
            contextMenu.add(ADD_ALBUM, album);
            // запретить удаление и редактирование записи с id = 1 (Unknown album)
            if (selectedItem != null && selectedItem.getId() != 1) {
                contextMenu.add(EDIT_ALBUM, selectedItem);
                contextMenu.add(DELETE_ALBUM, selectedItem);                       
            }
            contextMenu.show(artistPaneController.getArtistPane(), mouseEvent);             
        }
    }
      
}
