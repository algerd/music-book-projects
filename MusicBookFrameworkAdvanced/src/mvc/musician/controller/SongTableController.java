
package mvc.musician.controller;

import core.ContextMenuManager;
import static core.ContextMenuManager.ItemType.ADD_MUSICIAN_SONG;
import static core.ContextMenuManager.ItemType.DELETE_MUSICIAN_SONG;
import static core.ContextMenuManager.ItemType.EDIT_MUSICIAN_SONG;
import core.Main;
import core.RequestPage;
import db.entity.MusicianSong;
import db.entity.Song;
import db.repository.RepositoryService;
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

public class SongTableController implements Initializable {
    //@Inject
    private RepositoryService repositoryService;
    
    private MusicianSong selectedItem;   
    private MusicianPaneController musicianPaneController;
    
    @FXML
    private AnchorPane songTable;
    @FXML
    private Label titleLabel;
    @FXML
    private TableView<MusicianSong> songTableView;
    @FXML
    private TableColumn<MusicianSong, String> artistColumn;
    @FXML
    private TableColumn<MusicianSong, String> albumColumn;   
    @FXML
    private TableColumn<MusicianSong, Integer> yearColumn;
    @FXML
    private TableColumn<MusicianSong, String> songColumn;       
    @FXML
    private TableColumn<MusicianSong, Integer> ratingColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //@Inject
        repositoryService = Main.getInstance().getRepositoryService(); 
        
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getSong().getAlbum().getArtist().nameProperty());
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getSong().getAlbum().nameProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().getSong().getAlbum().yearProperty().asObject());
        songColumn.setCellValueFactory(cellData -> cellData.getValue().getSong().nameProperty());
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().getSong().ratingProperty().asObject());
    } 
    
    public void bootstrap(MusicianPaneController musicianPaneController) {
        this.musicianPaneController = musicianPaneController;
        setTableValue();
        initListeners();
        titleLabel.setText("Песни с авторством музыканта " + musicianPaneController.getMusician().getName());
    }
    
    private void setTableValue() {
        List<MusicianSong> musicianSongs = repositoryService.getMusicianSongRepository().selectJoinByMusician(musicianPaneController.getMusician());
        clearSelectionTable();
        songTableView.getItems().clear();
        songTableView.setItems(FXCollections.observableArrayList(musicianSongs));
        sort();
        Helper.setHeightTable(songTableView, 10);  
    }
    
    private void initListeners() {
        //Добавить слушателей на выбор элемента.
        songTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> selectedItem = songTableView.getSelectionModel().getSelectedItem()
        );               
    }
    
    private void initRepositoryListeners() {
        //clear listeners
        repositoryService.getMusicianSongRepository().clearChangeListeners(this);               
        repositoryService.getSongRepository().clearDeleteListeners(this);           
        repositoryService.getSongRepository().clearUpdateListeners(this);       
        repositoryService.getMusicianRepository().clearDeleteListeners(this);           
        repositoryService.getMusicianRepository().clearUpdateListeners(this);
        repositoryService.getAlbumRepository().clearDeleteListeners(this);
        repositoryService.getArtistRepository().clearDeleteListeners(this);   
        
        //add listeners
        repositoryService.getMusicianSongRepository().addChangeListener(this::changed, this);               
        repositoryService.getSongRepository().addDeleteListener(this::changed, this);           
        repositoryService.getSongRepository().addUpdateListener(this::changed, this);       
        repositoryService.getMusicianRepository().addDeleteListener(this::changed, this);           
        repositoryService.getMusicianRepository().addUpdateListener(this::changed, this);
        repositoryService.getAlbumRepository().addDeleteListener(this::changed, this);
        repositoryService.getArtistRepository().addDeleteListener(this::changed, this);   
    }
    
    
    // слушатель на изменение в таблицах
    private void changed(ObservableValue observable, Object oldVal, Object newVal) {
        setTableValue();
    }
    
    private void sort() {
        clearSelectionTable();
        songTableView.getItems().sort(Comparator.comparing((MusicianSong musicianSong) -> musicianSong.getSong().getName()));
    }
    
    private void clearSelectionTable() {
        songTableView.getSelectionModel().clearSelection();
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
                Song song = repositoryService.getSongRepository().selectById(selectedItem.getSong().getId());
                RequestPage.SONG_PANE.load(song);
            }
        }
        else if (mouseEvent.getButton() == MouseButton.SECONDARY) { 
            MusicianSong newMusicianSong = new MusicianSong();
            newMusicianSong.setId_musician(musicianPaneController.getMusician().getId());
            contextMenu.add(ADD_MUSICIAN_SONG, newMusicianSong);                    
            if (selectedItem != null) {
                contextMenu.add(EDIT_MUSICIAN_SONG, selectedItem);
                contextMenu.add(DELETE_MUSICIAN_SONG, selectedItem);     
            }
            contextMenu.show(musicianPaneController.getMusicianPane(), mouseEvent);            
        }
    }  
    
}
