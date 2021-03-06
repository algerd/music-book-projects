package mvc.musician.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN_ALBUM;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN_ALBUM;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN_ALBUM;
import core.Main;
import core.RequestPage;
import db.entity.Album;
import db.entity.MusicianAlbum;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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

public class AlbumTableController implements Initializable {
    
    private MusicianAlbum selectedItem;   
    private MusicianPaneController musicianPaneController;
    
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

    public void bootstrap(MusicianPaneController musicianPaneController) {
        this.musicianPaneController = musicianPaneController;
        setTableValue();
        initListeners();
        titleLabel.setText("Альбомы с авторством музыканта " + musicianPaneController.getMusician().getName());
    }
    
    private void setTableValue() {
        List<MusicianAlbum> musicianAlbums = Main.getInstance().getDbLoader().getMusicianAlbumTable().selectJoinByMusician(musicianPaneController.getMusician());
        clearSelectionTable();
        albumTableView.getItems().clear();
        albumTableView.setItems(FXCollections.observableArrayList(musicianAlbums));
        sort();
        Helper.setHeightTable(albumTableView, 10);  
    }
    
    private void initListeners() {
        //Добавить слушателей на выбор элемента.
        albumTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedItem = albumTableView.getSelectionModel().getSelectedItem();
            }
        );
        Main.getInstance().getDbLoader().getMusicianAlbumTable().addedProperty().addListener(this::changed);          
        Main.getInstance().getDbLoader().getMusicianAlbumTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianAlbumTable().updatedProperty().addListener(this::changed);
                  
        Main.getInstance().getDbLoader().getAlbumTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getAlbumTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getMusicianTable().deletedProperty().addListener(this::changed);           
        Main.getInstance().getDbLoader().getMusicianTable().updatedProperty().addListener(this::changed);
        
        Main.getInstance().getDbLoader().getArtistTable().deletedProperty().addListener(this::changed);    
    }
    
    // слушатель на изменение в таблицах
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
                Album album = Main.getInstance().getDbLoader().getAlbumTable().select(selectedItem.getAlbum().getId());
                RequestPage.ALBUM_PANE.load(album);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianAlbum newMusicianAlbum = new MusicianAlbum();
            newMusicianAlbum.setId_musician(musicianPaneController.getMusician().getId());
            contextMenu.add(ADD_MUSICIAN_ALBUM, newMusicianAlbum);                    
            if (selectedItem != null) {
                contextMenu.add(EDIT_MUSICIAN_ALBUM, selectedItem);
                contextMenu.add(DELETE_MUSICIAN_ALBUM, selectedItem);     
            }
            contextMenu.show(musicianPaneController.getMusicianPane(), mouseEvent);            
        }
    }  
    
}
